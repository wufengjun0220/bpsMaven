package com.mingtech.application.pool.bank.common;

import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.codec.MessageCodecServerService;
import com.mingtech.application.pool.bank.creditsys.ICreditHandler;
import com.mingtech.application.pool.bank.hkb.HKBConstants;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.ReturnMessage;

/**
 * @描述: [AbstractServer]服务器的抽象类，具体服务器实现可以继承本类
 */
public abstract class AbstractServer implements Server {

	private static final Logger logger = Logger.getLogger(AbstractServer.class);

	private String serverName;

	/**
	 * 服务器端编解码服务类
	 */
	private MessageCodecServerService messageCodecServerService;

	/**
	 * 服务器中的业务处理类
	 */
	private Map handlers;

	/**
	 * 交易结果码
	 */
	private Map txResponseCode;

	/**
	 * 端口号
	 */
	private int port;

	public void setMessageCodecServerService(
			MessageCodecServerService messageCodecServerService) {
		this.messageCodecServerService = messageCodecServerService;
	}

	public MessageCodecServerService getMessageCodecServerService() {
		return this.messageCodecServerService;
	}

	public Map getHandlers() {
		return handlers;
	}

	public void setHandlers(Map handlers) {
		this.handlers = handlers;
	}

	/**
	 * <p>
	 * 方法名称: getSuccessCode|描述: 获得处理成功的交易码
	 * </p>
	 * 
	 * @return
	 */
	public String getSuccessCode() {
		return (String) txResponseCode.get(Constants.TX_SUCCESS);
	}

	/**
	 * <p>
	 * 方法名称: getFailCode|描述: 获得处理失败的交易码
	 * </p>
	 * 
	 * @return
	 */
	public String getFailCode() {
		return (String) txResponseCode.get(Constants.TX_FAIL);
	}

	/**
	 * <p>
	 * 方法名称: getFailCodeDecode|描述: 获得解码失败交易码
	 * </p>
	 * 
	 * @return
	 */
	public String getFailCodeDecode() {
		return (String) txResponseCode.get(Constants.TX_FAIL_DECODE);
	}

	/**
	 * <p>
	 * 方法名称: getFailCodeNotSupportedTx|描述: 获得系统不支持该业务的交易码
	 * </p>
	 * 
	 * @return
	 */
	public String getFailCodeNotSupportedTx() {
		return (String) txResponseCode.get(Constants.TX_FAIL_NOTSUPPORTED_TX);
	}

	public Map getTxResponseCode() {
		return txResponseCode;
	}

	public void setTxResponseCode(Map txResponseCode) {
		this.txResponseCode = txResponseCode;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String messageReceived(String message) throws Exception {

		String code = "";
		String responseCode = this.getSuccessCode();

		ReturnMessage request = null;
		ReturnMessage response = null;// 需要最终返回的Response
		ReturnMessage txResponse = null;// Handler的结果

		// 获得编解码类
		try {
			request = this.messageCodecServerService.decodeMessage(null,
					message);
 		} catch (Exception e) {
			logger.error("端口"+getPort()+"解码请求报文失败!", e);
			responseCode = this.getFailCodeDecode();
		}

		// 处理交易
		if (responseCode.equals(this.getSuccessCode())) {
			// 解码成功
			code = request.getCode();
			if (serverName.equals("credit")) { // 信贷交易
				ICreditHandler handler = (ICreditHandler) this.getHandlers()
						.get(code.toUpperCase());// 获得交易处理类
				if (handler == null) {
					// 不支持本交易
					logger.error("不支持本交易：" + code + ".");
					responseCode = this.getFailCodeNotSupportedTx();
				} else {
					// 对交易做处理
					try {
						txResponse = handler.txHandleRequest(code, request);
					} catch (Exception e) {
						String errMsg = "交易[" + code + "]业务处理失败!";
						logger.error(errMsg, e);
						responseCode = this.getFailCode();
					}
				}
			} else {// 网银交易
				RequestHandler handler = (RequestHandler) this.getHandlers()
						.get(code.toUpperCase());// 获得交易处理类

				if (handler == null) {
					// 不支持本交易
					logger.error("不支持本交易：" + code + ".");
					responseCode = this.getFailCodeNotSupportedTx();
				} else {
					// 对交易做处理
					try {
						txResponse = handler.txHandleRequest(code, request);
					} catch (Exception e) {
						String errMsg = "交易[" + code + "]业务处理失败!";
						logger.error(errMsg, e);
						responseCode = this.getFailCode();
					}
				}
			}
		}

		if (txResponse != null) {
			// 已经经过后台处理类处理
			response = txResponse;
		} else {
			response = new ReturnMessage();
			response.getHead().put(Constants.RspCode, responseCode);
			response.getHead().put(Constants.TX_RETURN_MSG, "业务处理失败");
		}

		// 编码返回结果，并返回给客户端
		byte[] bMessage = this.messageCodecServerService.encodeMessage(code,
				response).getBytes();

		String resposeMsg = new String(bMessage);
		logger.debug("端口"+getPort()+"开始编码 报文为 ===" + code + " encode:\n" + resposeMsg);

		return resposeMsg;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public void buildResponse(Ret ret, ReturnMessageNew response) {
		if (ret.getRET_CODE()
				.equals(HKBConstants.TX_SUCCESS_CODE)) {
			response.getSysHead().put("RET_STATUS", "S");
		} else {
			response.getSysHead().put("RET_STATUS", "S");
		}
		response.getSysHead().put("RET.RET_CODE", ret.getRET_CODE());
		response.getSysHead().put("RET.RET_MSG", ret.getRET_MSG());
	}

}
