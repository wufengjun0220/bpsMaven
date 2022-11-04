package com.mingtech.application.pool.bank.hkb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.dcfs.esb.ftp.client.FtpClientConfig;
import com.dcfs.esb.ftp.client.FtpGet;
import com.dcfs.esb.ftp.client.FtpPut;
import com.dcfs.esb.ftp.server.error.FtpException;
import com.hkb.esb.util.TCPConnUtil;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.translog.domain.TransLog;
import com.mingtech.application.pool.bank.translog.service.TransLogService;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 核心客户端，使用Java Socket实现
 */
@Service
public class DefaultESBClient implements ESBClient {

	private static final Logger logger = Logger.getLogger(DefaultESBClient.class);
	private TransLogService transLogService;
	private static final int HEAD_LEN = 8;
	private static final String ENCODING = "UTF-8";
	private String ip;
	private int port;
	private int timeout = 5000;

	/**
	 * <p>
	 * 构造函数名称: |描述:
	 * </p>
	 */
	public DefaultESBClient() {

	}

	/**
	 * <p>
	 * 构造函数名称: |描述:
	 * </p>
	 * 
	 * @param ip   ip
	 * @param port 端口
	 */
	public DefaultESBClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	/**
	 * <p>
	 * 构造函数名称: |描述:
	 * </p>
	 * 
	 * @param ip      ip
	 * @param port    端口
	 * @param timeout 超时时间
	 */
	public DefaultESBClient(String ip, int port, int timeout) {
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mingtech.application.pool.bank.hkb.ESBClient#process(java.lang.String,
	 * com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	public ReturnMessageNew process(String code, ReturnMessageNew request) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingtech.application.pool.bank.hkb.ESBClient#processCore(java.lang.
	 * String, com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	public ReturnMessageNew processCore(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = null;
		try {
			byte[] beisMsg = HKBMessageCodecFactory.createCoreClient().encodeCoreMessage(code, request);// 构建报文主体
			String bodyLength = String.valueOf(beisMsg.length);// 如果需要补位，则还需要做补位操作
			String bmsgLength = StringUtil.leftPad(bodyLength, 8, '0');
			String beisMessage = new String(beisMsg, Constants.ENCODING);
			beisMessage = bmsgLength + beisMessage;
			String txName = TransCodeMap.templateMapNew().getNameMap().get(code);
			logger.info("核心交易[" + code + "]，交易名称为["+txName+"]，发送报文内容[" + beisMessage + "]");
			
			FtpPut ftpput = null;
			try {
				String responseFileFlag = "0";
				Map resFileHead = request.getFileHead();
				if (resFileHead != null && resFileHead.size() > 0) {
					responseFileFlag = (String) resFileHead.get(HKBConstants.FTP_FILE_FLAG);
					// 4.2 如果有上传文件
					if (responseFileFlag.equals(HKBConstants.FILE_FLAG_1)) {
						logger.info("核心交易【" + code + "】生成文件开始");
						request.setTxCode(code);
						String responseFileName = HKBMessageCodecFactory.createNetBankServer().encodeMessageFileClien(code,
								request);
						logger.info("MinaServerHKB【" + code + "】生成文件完毕");
						// 调用API上传
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						ftpput = new FtpPut(responseFileName,(String) request.getFileHead().get("FILE_PATH"), false);
						String resultFileName = ftpput.doPutFile(); // 上传文件，返回文件路径+文件名
						logger.info("MinaServerHKB文件[" + resultFileName + "]上传成功！");
					}
				}
			} catch (FtpException e) {
				logger.info("文件上传出错", e);
			} finally {
				if (ftpput != null) {
					ftpput.close();
				}
			}
			//保存发送报文到日志表中
			logger.info("保存发送报文至日志表开始................................");
			byte[] reqBytes = beisMessage.getBytes(ENCODING);
			TransLog tl = transLogService.txStoreSendMsg(code, reqBytes, request);
			
			// 对报文进行加密
			byte[] resmessage = send(beisMessage);
			// 保存到日志表中
			logger.info("保存接受报文至日志表开始................................");
			tl = transLogService.txUpdateLog(tl, resmessage,true);
			// 接受响应
			if (resmessage != null) {
				response = HKBMessageCodecFactory.createCoreClient().decodeEcdsMessage(code,
						new String(resmessage, ENCODING));
				logger.info("核心交易【" + code + "】，回执报文【" + response + "】");
				if (response.getRet().getRET_CODE().equals(HKBConstants.TX_SUCCESS_CODE)) {
					transLogService.txUpdateTransLogRec(tl, true, response, "交易成功，交易返回代码" + response.getRet().getRET_CODE());
				} else {
					transLogService.txUpdateTransLogRec(tl, false, response, "交易失败，交易返回代码" + response.getRet().getRET_CODE()
							+ "，交易返回信息" + response.getRet().getRET_MSG());
				}
				FtpGet ftp = null;
				try {
					Map fileHead = response.getFileHead();
					logger.info("核心交易【" + code + "】，报文文件头[" + request.getFileHead() + "]");
					String fileFlag = "0";
					String fileName = "";
					String splictCode = "";
					if (fileHead != null && fileHead.size() > 0) {
						fileFlag = (String) fileHead.get(HKBConstants.FTP_FILE_FLAG);
						fileName = (String) fileHead.get(HKBConstants.FTP_FILE_PATH);
						splictCode = HKBConstants.FTP_SPILCT_CODE;
					}
					// 1.若有FTP文件则进行下载并解析
					if (fileFlag.equals(HKBConstants.FILE_FLAG_2)) {
						// YeCheng 对应ecds.tft.localPath配置
						String localFile = ProjectConfig.getInstance().getLocalPath() + fileName;
						//String localFile = fileName;
						logger.info("核心交易【" + code + "】加载FTP配置文件开始");
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						logger.info("核心交易【" + code + "】加载FTP配置文件结束，下载文件开始，文件名为【" + fileName + "】");
						ftp = new FtpGet(fileName, localFile, false);
						logger.info("核心交易【" + code + "】下载文件结束，本地文件名为【" + localFile + "】");
						if (ftp.doGetFile()) {
//						if (true) {
							logger.info("核心交易【" + code + "】下载成功,解析文件开始");
							// 解析报文文件
							request = HKBMessageCodecFactory.createCoreClient().decodeMessageFile(code, splictCode,
									localFile);
							logger.info("核心交易【" + code + "】，报文明细[" + request.getDetails() + "]");
						} else {
							throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC021);
						}
					}
				} catch (Exception e) {
					logger.info("DefaultESBClient解码FTP文件失败!", e);
				} finally {
					if (ftp != null) {
						ftp.close();
					}
				}
				
				response.setDetails(request.getDetails());
				return response;
			} else {
				return null;
			}

		} catch (IOException e) {
			logger.info("处理核心交易" + code + "失败!", e);
			throw new Exception("处理核心交易失败：Err00008");
		} finally {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingtech.application.pool.bank.hkb.ESBClient#processECDS(java.lang.
	 * String, com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	@Override
	public ReturnMessageNew processECDS(String code, ReturnMessageNew request) throws Exception {
		String msg = "";
		if(code.contains("PJE")){
			msg = "MIS/额度系统交易";
		}else if(code.contains("PJC")){
			msg = "网银系统交易";
		}else if(code.contains("CP")){
			msg = "中台系统交易";
		}else{
			msg = "电票系统交易";
		}
		/*
		 * 加入文件上传    Ju Nana   20191213 
		 */
		logger.info("文件上传模块【" + code + "】开始.......");
		
		
		FtpPut ftpput = null;
		try {
			String responseFileFlag = "0";
			Map resFileHead = request.getFileHead();
			if (resFileHead != null && resFileHead.size() > 0) {
				responseFileFlag = (String) resFileHead.get(HKBConstants.FTP_FILE_FLAG);
				// 4.2 如果有上传文件
				if (responseFileFlag.equals(HKBConstants.FILE_FLAG_1)) {
					logger.info("MinaServerHKB【" + code + "】上传文件开始");
					request.setTxCode(code);
					String responseFileName = HKBMessageCodecFactory.createNetBankServer().encodeMessageFileClien(code,request);					
					// 调用API上传
					FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
					ftpput = new FtpPut(responseFileName, (String) request.getFileHead().get("FILE_PATH"), false);
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
		logger.info("文件上传模块【" + code + "】结束.......");
		
		
		
		ReturnMessageNew response = null;
		try {
			byte[] resMsg = HKBMessageCodecFactory.createECDSlient().encodeEcdsMessage(code, request);// 构建报文主体
			String bodyLength = String.valueOf(resMsg.length);// 如果需要补位，则还需要做补位操作
			String bmsgLength = StringUtil.leftPad(bodyLength, 8, '0');
			String resposeMsg = new String(resMsg, Constants.ENCODING);
			resposeMsg = bmsgLength + resposeMsg;
			String txName = TransCodeMap.templateMapNew().getNameMap().get(code);
			logger.info(msg +"[" + code + "]，交易名称为["+txName+"]，发送报文内容[" + resposeMsg + "]");
			
			
			//保存发送报文到日志表中
			TransLog tl = transLogService.txStoreSendMsg(code, resposeMsg.getBytes(ENCODING), request);
			
			// 对报文进行加密
			byte[] resmessage = send(resposeMsg);
			// 保存到日志表中
			logger.info("保存接受报文至日志表开始................................");
			tl = transLogService.txUpdateLog(tl, resmessage,true);
			
			// 接受响应
			if (resmessage != null) {
				response = HKBMessageCodecFactory.createECDSlient().decodeEcdsMessage(code,new String(resmessage, ENCODING));
				logger.debug(msg + "【" + code + "】，回执报文【" + response + "】");
				if (response.getRet().getRET_CODE().equals(HKBConstants.TX_SUCCESS_CODE)) {
					if (tl != null) {
						transLogService.txUpdateTransLogRec(tl, true, response, "交易成功，交易返回代码" + response.getRet().getRET_CODE());
					}
				} else {
					if (tl != null) {
						transLogService.txUpdateTransLogRec(tl, false, response, "交易失败，交易返回代码" + response.getRet().getRET_CODE()+ "，交易返回信息" + response.getRet().getRET_MSG());
					}
				}

				FtpGet ftp = null;
				try {
					Map fileHead = response.getFileHead();
					logger.debug(msg + "【" + code + "】，报文文件头[" + request.getFileHead() + "]");
					String fileFlag = "0";
					String fileName = "";
					String splictCode = "";
					if (fileHead != null && fileHead.size() > 0) {
						fileFlag = (String) fileHead.get(HKBConstants.FTP_FILE_FLAG);
						fileName = (String) fileHead.get(HKBConstants.FTP_FILE_PATH);
						splictCode = HKBConstants.FTP_SPILCT_CODE;
					}
					
					logger.info("fileFlag的值为" + fileFlag + "】........");
					logger.info("文件下载模块【" + code +msg+ "】开始........");
					// 1.若有FTP文件则进行下载并解析
					if (fileFlag.equals(HKBConstants.FILE_FLAG_2)) {
						// YeCheng 对应ecds.tft.localPath配置
						String localFile = ProjectConfig.getInstance().getLocalPath() + fileName;
						//String localFile = fileName;
						logger.info(msg + "【" + code + "】加载FTP配置文件开始");
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						logger.info(msg + "【" + code + "】加载FTP配置文件结束，下载文件开始，文件名为【" + fileName + "】");
						ftp = new FtpGet(fileName, localFile, false);
						logger.info(msg + "【" + code + "】下载文件结束，本地文件名为【" + localFile + "】");
						if (ftp.doGetFile()) {
//						if (true) {
							logger.info(msg + "【" + code + "】下载成功，解析文件开始");
							// 解析报文文件
							request = HKBMessageCodecFactory.createECDSlient().decodeMessageFile(code, splictCode,localFile);
							logger.info(msg + "【" + code + "】，报文明细[" + request.getDetails() + "]");
						} else {
							throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC021);
						}
						response.setDetails(request.getDetails());
					}
					logger.info("文件下载模块【" + code + "】结束........");
				} catch (Exception e) {
					logger.error("DefaultESBClient解码FTP文件失败!", e);
				} finally {
					if (ftp != null) {
						ftp.close();
					}
				}
				
				return response;
			} else {
				return null;
			}
		} catch (IOException e) {
			logger.error("处理" + msg + code + "失败!", e);
			throw new Exception("Err00008");
		} finally {

		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingtech.application.pool.bank.hkb.ESBClient#processCore(java.lang.
	 * String, com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	public ReturnMessageNew processMSS(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = null;
		try {
			byte[] beisMsg = HKBMessageCodecFactory.createCoreClient().encodeCoreMessage(code, request);// 构建报文主体
			String bodyLength = String.valueOf(beisMsg.length);// 如果需要补位，则还需要做补位操作
			String bmsgLength = StringUtil.leftPad(bodyLength, 8, '0');
			String beisMessage = new String(beisMsg, Constants.ENCODING);
			beisMessage = bmsgLength + beisMessage;
			String txName = TransCodeMap.templateMapNew().getNameMap().get(code);
			logger.info("短信交易[" + code + "]，交易名称为["+txName+"]，发送报文内容[" + beisMessage + "]");
			
			FtpPut ftpput = null;
			try {
				String responseFileFlag = "0";
				Map resFileHead = request.getFileHead();
				if (resFileHead != null && resFileHead.size() > 0) {
					responseFileFlag = (String) resFileHead.get(HKBConstants.FTP_FILE_FLAG);
					// 4.2 如果有上传文件
					if (responseFileFlag.equals(HKBConstants.FILE_FLAG_1)) {
						logger.debug("短信交易【" + code + "】生成文件开始");
						request.setTxCode(code);
						String responseFileName = HKBMessageCodecFactory.createNetBankServer().encodeMessageFileClien(code,
								request);
						logger.debug("MinaServerHKB【" + code + "】生成文件完毕");
						// 调用API上传
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						ftpput = new FtpPut(responseFileName, responseFileName, false);
						String resultFileName = ftpput.doPutFile(); // 上传文件，返回文件路径+文件名
						logger.debug("MinaServerHKB文件[" + resultFileName + "]上传成功！");
					}
				}
			} catch (FtpException e) {
				logger.error("文件上传出错", e);
			} finally {
				if (ftpput != null) {
					ftpput.close();
				}
			}
			//保存发送报文到日志表中
			logger.info("保存发送报文至日志表开始................................");
			byte[] reqBytes = beisMessage.getBytes(ENCODING);
			TransLog tl = transLogService.txStoreSendMsg(code, reqBytes, request);
			
			// 对报文进行加密
			byte[] resmessage = send(beisMessage);
			// 保存到日志表中
			logger.info("保存接受报文至日志表开始................................");
			tl = transLogService.txUpdateLog(tl, resmessage,true);
			// 接受响应
			if (resmessage != null) {
				response = HKBMessageCodecFactory.createCoreClient().decodeEcdsMessage(code,
						new String(resmessage, ENCODING));
				logger.debug("短信交易【" + code + "】，回执报文【" + response + "】");
				if (response.getRet().getRET_CODE().equals(HKBConstants.TX_SUCCESS_CODE)) {
					transLogService.txUpdateTransLogRec(tl, true, response, "交易成功，交易返回代码" + response.getRet().getRET_CODE());
				} else {
					transLogService.txUpdateTransLogRec(tl, false, response, "交易失败，交易返回代码" + response.getRet().getRET_CODE()
							+ "，交易返回信息" + response.getRet().getRET_MSG());
				}
				FtpGet ftp = null;
				try {
					Map fileHead = response.getFileHead();
					logger.debug("短信交易【" + code + "】，报文文件头[" + request.getFileHead() + "]");
					String fileFlag = "0";
					String fileName = "";
					String splictCode = "";
					if (fileHead != null && fileHead.size() > 0) {
						fileFlag = (String) fileHead.get(HKBConstants.FTP_FILE_FLAG);
						fileName = (String) fileHead.get(HKBConstants.FTP_FILE_PATH);
						splictCode = HKBConstants.FTP_SPILCT_CODE;
					}
					// 1.若有FTP文件则进行下载并解析
					if (fileFlag.equals(HKBConstants.FILE_FLAG_2)) {
						// YeCheng 对应ecds.tft.localPath配置
						String localFile = ProjectConfig.getInstance().getLocalPath() + fileName;
						//String localFile = fileName;
						logger.info("【" + code + "】加载FTP配置文件开始");
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						logger.info("【" + code + "】加载FTP配置文件结束，下载文件开始，文件名为【" + fileName + "】");
						ftp = new FtpGet(fileName, localFile, false);
						logger.info("【" + code + "】下载文件结束，本地文件名为【" + localFile + "】");
						if (ftp.doGetFile()) {
//						if (true) {
							logger.info("【" + code + "】下载成功,解析文件开始");
							// 解析报文文件
							request = HKBMessageCodecFactory.createCoreClient().decodeMessageFile(code, splictCode,
									localFile);
							logger.info("【" + code + "】，报文明细[" + request.getDetails() + "]");
						} else {
							throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC021);
						}
					}
				} catch (Exception e) {
					logger.error("DefaultESBClient解码FTP文件失败!", e);
				} finally {
					if (ftp != null) {
						ftp.close();
					}
				}
				
				
				
				
				response.setDetails(request.getDetails());
				return response;
			} else {
				return null;
			}

		} catch (IOException e) {
			logger.error("处理短信交易" + code + "失败!", e);
			throw new Exception("Err00008");
		} finally {

		}
	}
	
	@Override
	public ReturnMessageNew processLPR(String code, ReturnMessageNew request)
			throws Exception {
		ReturnMessageNew response = null;
		try {
			byte[] beisMsg = HKBMessageCodecFactory.createCoreClient().encodeCoreMessage(code, request);// 构建报文主体
			String bodyLength = String.valueOf(beisMsg.length);// 如果需要补位，则还需要做补位操作
			String bmsgLength = StringUtil.leftPad(bodyLength, 8, '0');
			String beisMessage = new String(beisMsg, Constants.ENCODING);
			beisMessage = bmsgLength + beisMessage;
			String txName = TransCodeMap.templateMapNew().getNameMap().get(code);
			logger.info("lpr交易[" + code + "]，交易名称为["+txName+"]，发送报文内容[" + beisMessage + "]");
			
			FtpPut ftpput = null;
			try {
				String responseFileFlag = "0";
				Map resFileHead = request.getFileHead();
				if (resFileHead != null && resFileHead.size() > 0) {
					responseFileFlag = (String) resFileHead.get(HKBConstants.FTP_FILE_FLAG);
					// 4.2 如果有上传文件
					if (responseFileFlag.equals(HKBConstants.FILE_FLAG_1)) {
						logger.debug("lpr交易【" + code + "】生成文件开始");
						request.setTxCode(code);
						String responseFileName = HKBMessageCodecFactory.createNetBankServer().encodeMessageFileClien(code,
								request);
						logger.debug("MinaServerHKB【" + code + "】生成文件完毕");
						// 调用API上传
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						ftpput = new FtpPut(responseFileName, responseFileName, false);
						String resultFileName = ftpput.doPutFile(); // 上传文件，返回文件路径+文件名
						logger.debug("MinaServerHKB文件[" + resultFileName + "]上传成功！");
					}
				}
			} catch (FtpException e) {
				logger.error("文件上传出错", e);
			} finally {
				if (ftpput != null) {
					ftpput.close();
				}
			}
			
			//保存发送报文到日志表中
			logger.info("保存发送报文至日志表开始.......................");
			byte[] reqBytes = beisMessage.getBytes(ENCODING);
			TransLog tl = transLogService.txStoreSendMsg(code, reqBytes, request);
			
			// 对报文进行加密
			byte[] resmessage = send(beisMessage);
			// 保存到日志表中
			logger.info("保存接受报文至日志表开始.......................");
			tl = transLogService.txUpdateLog(tl, resmessage,true);
			
			// 接受响应
			if (resmessage != null) {
				response = HKBMessageCodecFactory.createCoreClient().decodeEcdsMessage(code,
						new String(resmessage, ENCODING));
				logger.debug("lpr交易【" + code + "】，回执报文【" + response + "】");
				if (response.getRet().getRET_CODE().equals(HKBConstants.TX_SUCCESS_CODE)) {
					transLogService.txUpdateTransLogRec(tl, true, response, "交易成功，交易返回代码" + response.getRet().getRET_CODE());
				} else {
					transLogService.txUpdateTransLogRec(tl, false, response, "交易失败，交易返回代码" + response.getRet().getRET_CODE()
							+ "，交易返回信息" + response.getRet().getRET_MSG());
				}
				FtpGet ftp = null;
				try {
					Map fileHead = response.getFileHead();
					logger.debug("lpr交易【" + code + "】，报文文件头[" + request.getFileHead() + "]");
					String fileFlag = "0";
					String fileName = "";
					String splictCode = "";
					if (fileHead != null && fileHead.size() > 0) {
						fileFlag = (String) fileHead.get(HKBConstants.FTP_FILE_FLAG);
						fileName = (String) fileHead.get(HKBConstants.FTP_FILE_PATH);
						splictCode = HKBConstants.FTP_SPILCT_CODE;
					}
					// 1.若有FTP文件则进行下载并解析
					if (fileFlag.equals(HKBConstants.FILE_FLAG_2)) {
						// YeCheng 对应ecds.tft.localPath配置
						String localFile = ProjectConfig.getInstance().getLocalPath() + fileName;
						//String localFile = fileName;
						logger.debug("lpr交易【" + code + "】加载FTP配置文件开始");
						FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
						logger.debug("lpr交易【" + code + "】加载FTP配置文件结束，下载文件开始，文件名为【" + fileName + "】");
						ftp = new FtpGet(fileName, localFile, false);
						logger.info("lpr交易【" + code + "】下载文件结束，本地文件名为【" + localFile + "】");
						if (ftp.doGetFile()) {
//						if(true){
							logger.debug("lpr交易【" + code + "】下载成功,解析文件开始");
							// 解析报文文件
							request = HKBMessageCodecFactory.createCoreClient().decodeMessageFile(code, splictCode,
									localFile);
							logger.debug("lpr交易【" + code + "】，报文明细[" + request.getDetails() + "]");
						} else {
							throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC021);
						}
					}
				} catch (Exception e) {
					logger.error("DefaultESBClient解码FTP文件失败!", e);
				} finally {
					if (ftp != null) {
						ftp.close();
					}
				}
				response.setDetails(request.getDetails());
				return response;
			} else {
				return null;
			}

		} catch (IOException e) {
			logger.error("处理lpr交易" + code + "失败!", e);
			throw new Exception("Err00008");
		} finally {

		}
	}


	private byte[] send(String message) throws Exception {

		byte[] reqBytes = message.getBytes(ENCODING);

		Socket socket = null;
		InputStream is = null;
		OutputStream os = null;
		byte[] rspData = null;

		try {
			logger.info("connect to " + ip + ":" + port);
			socket = TCPConnUtil.getConnect(ip, port, timeout);

			os = new BufferedOutputStream(socket.getOutputStream());
			TCPConnUtil.writeMessage(os, reqBytes);

			is = new BufferedInputStream(socket.getInputStream());
			byte[] lenByte = TCPConnUtil.readLenContent(is, HEAD_LEN);
			int length = Integer.parseInt(new String(lenByte, ENCODING));

			rspData = TCPConnUtil.readLenContent(is, length);
			logger.info("接收到的返回报文["+new String(rspData, ENCODING)+"]");

		} finally {
			TCPConnUtil.closeConnect(socket, os, is);
		}

		return rspData;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setTransLogService(TransLogService transLogService) {
		this.transLogService = transLogService;
	}

}
