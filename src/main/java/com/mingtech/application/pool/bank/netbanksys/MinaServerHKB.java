package com.mingtech.application.pool.bank.netbanksys;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.common.WriteFuture;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.dcfs.esb.ftp.client.FtpClientConfig;
import com.dcfs.esb.ftp.client.FtpGet;
import com.dcfs.esb.ftp.client.FtpPut;
import com.dcfs.esb.ftp.server.error.FtpException;
import com.mingtech.application.pool.bank.codec.MessageCodecServerService;
import com.mingtech.application.pool.bank.common.AbstractServer;
import com.mingtech.application.pool.bank.common.RequestHandler;
import com.mingtech.application.pool.bank.hkb.HKBConstants;
import com.mingtech.application.pool.bank.hkb.HKBMessageCodecFactory;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.hkb.TransCodeMap;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.translog.domain.TransLog;
import com.mingtech.application.pool.bank.translog.service.TransLogService;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.common.util.StringUtil;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;

public class MinaServerHKB extends AbstractServer implements IoHandler {

	private static final Logger logger = Logger.getLogger(MinaServerHKB.class);
	private IoAcceptor acceptor = null;
	private TransLogService transLogService;

	public MinaServerHKB() {
	}

	/**
	 * 启动网银服务器
	 * <p>
	 * 方法名称: start|描述:
	 * </p>
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		try {
			// 配置
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setThreadModel(ThreadModel.MANUAL);
			DefaultIoFilterChainBuilder chain = config.getFilterChain();
			chain.addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
			chain.addLast("logger", new LoggingFilter());
			// 启动
			acceptor = new SocketAcceptor(4, Executors.newCachedThreadPool());
			acceptor.bind(new InetSocketAddress(getPort()), this, config);
			logger.info(getServerName() + "开始监听端口:" + getPort());
		} catch (IOException e) {
			logger.error("启动" + getServerName() + "失败!", e);
		}
	}

	/**
	 * 停止网银服务器
	 * <p>
	 * 方法名称: stop|描述:
	 * </p>
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		acceptor.unbindAll();
		logger.info(getServerName() + "停止监听端口:" + getPort());
	}

	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
		logger.info(getServerName() + "发生异常", arg1);
	}

	public void messageReceived(IoSession session, Object message) throws Exception {

		// 读取消息放入缓存
		ByteBuffer buffer = (ByteBuffer) session.getAttribute("buffer");
		Integer length = (Integer) session.getAttribute("bodyLength");
		byte[] head = (byte[]) session.getAttribute("head");
		if (buffer == null) {
			buffer = ByteBuffer.allocate(1024);
			buffer.setAutoExpand(true);
			session.setAttribute("buffer", buffer);
		}
		buffer.put((ByteBuffer) message);

		// 解析报文头，获取报文体长度
		if (length == null && buffer.limit() > Constants.MSG_LENGTH_LEN) {
			buffer.rewind();
			head = new byte[Constants.MSG_LENGTH_LEN];
			buffer.get(head);
			buffer.position(buffer.limit());
			length = Integer.valueOf(new String(head));
			session.setAttribute("head", head);
			session.setAttribute("bodyLength", length);
		}
		// 解析报文体
		if (length != null && buffer.limit() >= (Constants.MSG_LENGTH_LEN + length.intValue())) {
			buffer.position(Constants.MSG_LENGTH_LEN);
			byte[] body = new byte[length.intValue()];
			buffer.get(body);
			session.removeAttribute("buffer");
			session.removeAttribute("bodyLength");
			session.removeAttribute("head");

			String shead = new String(head, Constants.ENCODING);
			String sbody = new String(body, Constants.ENCODING); 
			String msgStr = shead + sbody;
			logger.info("MinaServerHKB端口" + getPort() + "接收到报文：" + msgStr);

			ReturnMessageNew response = null; // 响应对象
			Ret ret = new Ret();
			String txCode = "";// 内部交易码
			ReturnMessageNew request = null; // 请求对象
			try {
				// 1.解码请求报文
				logger.info("MinaServerHKB解码请求报文开始");
				request = HKBMessageCodecFactory.createNetBankServer().decodeNetBankMessage("", sbody);
				if (request == null) {
					throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC002);
				} else {
					txCode = request.getTxCode();
					ret.setRET_CODE(HKBConstants.TX_SUCCESS_CODE);
					ret.setRET_MSG(HKBConstants.RESPONSE_SUCCESS_RET_MSG);
				}
				String txName = TransCodeMap.templateMapNew().getNameMap().get(txCode);
				logger.info("MinaServerHKB解码请求报文完毕，交易码为[" + txCode + "]，交易名称为["+txName+"]");
			} catch (Exception e) {
				logger.error("MinaServerHKB解码请求报文失败!", e);
				ret.setRET_CODE(HKBConstants.RESPONSE_FAIL_RET_CODE_PJC002);
				ret.setRET_MSG(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC002 + "[" + e.getMessage() + "]");
			}

			FtpGet ftp = null;
			try {
				Map fileHead = request.getFileHead();
				logger.info("MinaServerHKB【" + txCode + "】，报文文件头[" + request.getFileHead() + "]");
				String fileFlag = "0";
				String fileName = "";
				String splictCode = "";
				if (fileHead != null && fileHead.size() > 0) {
					fileFlag = (String) fileHead.get(HKBConstants.FTP_FILE_FLAG);
					fileName = (String) fileHead.get(HKBConstants.FTP_FILE_PATH);
					splictCode = HKBConstants.FTP_SPILCT_CODE;
					// 1.若有FTP文件则进行下载并解析
					if (fileFlag != null && (fileFlag.equals(HKBConstants.FILE_FLAG_2) || fileFlag.equals(HKBConstants.FILE_FLAG_1))) {
						// YeCheng 对应ecds.tft.localPath配置
						String localFile = ProjectConfig.getInstance().getLocalPath() + fileName;
//						String localFile = fileName;
						logger.info("MinaServerHKB【" + txCode + "】加载FTP配置文件开始");
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						logger.info("MinaServerHKB【" + txCode + "】加载FTP配置文件结束，下载文件开始，文件名为【" + fileName + "】");
						ftp = new FtpGet(fileName, localFile, false);
						logger.info("MinaServerHKB【" + txCode + "】下载文件结束，本地文件名为【" + localFile + "】");
						if (ftp.doGetFile()) {
//						if(true){
							logger.info("MinaServerHKB【" + txCode + "】下载成功，解析文件开始");
							// 解析报文文件
							ReturnMessageNew quest = HKBMessageCodecFactory.createNetBankServer().decodeMessageFile(txCode, splictCode,
									localFile);
							logger.info("MinaServerHKB【" + txCode + "】，报文明细[" + quest.getDetails() + "]");
							request.setDetails(quest.getDetails());
							ret.setRET_CODE(HKBConstants.TX_SUCCESS_CODE);
							ret.setRET_MSG(HKBConstants.RESPONSE_SUCCESS_RET_MSG);
						} else {
							throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC021);
						}
					}
				}
			} catch (Exception e) {
				logger.error("MinaServerHKB解码FTP文件失败!", e);
				ret.setRET_CODE(HKBConstants.RESPONSE_FAIL_RET_CODE_PJC002);
				ret.setRET_MSG(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC002 + "[" + e.getMessage() + "]");
			} finally {
				if (ftp != null) {
					ftp.close();
				}
			}
			// 保存到日志表中
			logger.info("保存到日志表开始................................");
			TransLog tl = transLogService.txStoreRecMsg(body, request);
			if (ret.getRET_CODE().equals(HKBConstants.TX_SUCCESS_CODE)) {
				try {
					// 2.处理请求
					logger.info("MinaServerHKB【" + txCode + "】处理请求开始");
					RequestHandler handler = (RequestHandler) this.getHandlers().get(request.getTxCode().toUpperCase()); // 获得交易处理类
					if (handler == null) {
						throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC003);
					}
					// 3.业务逻辑处理
					
					logger.info(request.getTxCode()+"接口处理开始...");
					Date starTime = new Date();
					
					
					response = handler.txHandleRequest(request.getTxCode(), request);
					
					Date endTime = new Date();
					long executeTime = (endTime.getTime() - starTime.getTime());
					logger.info(request.getTxCode()+"接口内部逻辑执行结束，执行时间为 "+executeTime/1000+"秒（"+executeTime+"毫秒)");
					
					if (response == null) {
						throw new Exception("业务逻辑处理返回失败!");
					}
					logger.info("MinaServerHKB【" + txCode + "】处理请求完毕");
				} catch (Exception e) {
					if(response == null){
						response = new ReturnMessageNew();
					}
					logger.error("MinaServerHKB交易" + txCode + "处理失败!", e);
					ret.setRET_CODE(HKBConstants.RESPONSE_FAIL_RET_CODE);
					ret.setRET_MSG(HKBConstants.RESPONSE_FAIL_RET_CODE + "[票据池内部处理异常!]");
					response.setRet(ret);
				}
			}

			
			FtpPut ftpput = null;
			try {
				String responseFileFlag = "0";
				Map resFileHead = response.getFileHead();
				if (resFileHead != null && resFileHead.size() > 0) {
					responseFileFlag = (String) resFileHead.get(HKBConstants.FTP_FILE_FLAG);
					// 4.2 如果有上传文件
					if (responseFileFlag.equals(HKBConstants.FILE_FLAG_2)) {
						logger.info("MinaServerHKB【" + txCode + "】回执文件开始");
						response.setTxCode(txCode);
						String responseFileName = HKBMessageCodecFactory.createNetBankServer().encodeMessageFile(txCode,
								response);
						logger.info("MinaServerHKB【" + txCode + "】回执文件完毕");
						// 调用API上传
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						ftpput = new FtpPut(responseFileName, (String) response.getFileHead().get("FILE_PATH"), false);
						String resultFileName = ftpput.doPutFile(); // 上传文件，返回文件路径+文件名
						logger.info("MinaServerHKB文件[" + resultFileName + "]上传成功！");
					}
				}
			} catch (FtpException e) {
				logger.error("文件上传出错", e);
			} finally {
				if (ftpput != null) {
					ftpput.close();
				}
			}
			
			// 4.获取响应报文
			String resposeMsg = "";
			byte[] msg = null;
			try {
				if (response == null) {
					response = new ReturnMessageNew();
					response.setRet(ret);
				} else {
					ret = response.getRet();
				}
				// 4.1 创建交易状态、交易返回代码、交易返回信息
				buildResponse(ret, response);
				logger.info("MinaServerHKB【" + txCode + "】回执报文开始");
				msg = HKBMessageCodecFactory.createNetBankServer().encodeNetBankMessage(txCode, response);
				logger.info("MinaServerHKB【" + txCode + "】回执报文完毕");
			} catch (Exception e) {
				logger.error("MinaServerHKB交易" + txCode + "响应失败!", e);
			}

			if (response.getRet().getRET_CODE().equals(HKBConstants.TX_SUCCESS_CODE)) {
				if (tl != null) {
					transLogService.txUpdateTransLogRec(tl, true, response, "交易成功，交易返回代码" + response.getRet().getRET_CODE());
				}
			} else {
				if (tl != null) {
					transLogService.txUpdateTransLogRec(tl, false, response, "交易失败，交易返回代码" + response.getRet().getRET_CODE()
							+ "，交易返回信息" + response.getRet().getRET_MSG());
				}
			}

			String bodyLength = String.valueOf(msg.length);// 如果需要补位，则还需要做补位操作
			String bmsgLength = StringUtil.leftPad(bodyLength, 8, '0');
			resposeMsg = new String(msg, Constants.ENCODING);
			resposeMsg = bmsgLength + resposeMsg;
			logger.info("MinaServerHKB发送报文[" + resposeMsg + "]");
			// 将返回结果发给客户端
			byte[] bMessage = resposeMsg.getBytes(Constants.ENCODING);
			
			// 保存到日志表中
			logger.info("保存接受返回报文至日志表开始................................");
			tl = transLogService.txUpdateLog(tl, bMessage,false);
			ByteBuffer rBuffer = ByteBuffer.allocate(bMessage.length);
			rBuffer.put(bMessage);
			rBuffer.flip();
			WriteFuture future = session.write(rBuffer);
			future.join();
		}
	}

	public void messageSent(IoSession arg0, Object arg1) throws Exception {

	}

	public void sessionClosed(IoSession arg0) throws Exception {
	}

	public void sessionCreated(IoSession arg0) throws Exception {
	}

	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
	}

	public void sessionOpened(IoSession arg0) throws Exception {
	}

	public MessageCodecServerService getMessageCodecServerService() {
		return null;
	}

	public void setTransLogService(TransLogService transLogService) {
		this.transLogService = transLogService;
	}

}
