package com.mingtech.application.pool.bank.codec;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.ReturnMessage;



/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 10:39:36 AM
 * @描述: [MessageCodecServiceServerImpl]请在此简要描述类的功能
 */
public class MessageCodecServerServiceImpl implements MessageCodecServerService{
	
	private static final Logger logger = Logger.getLogger(MessageCodecServerServiceImpl.class);

	/* （非 Javadoc）
	* <p>重写方法: decodeMessage|描述: </p>
	* @param code
	* @param message
	* @return
	* @throws Exception
	* @see com.mingtech.application.pool.bank.codec.MessageCodecService#decodeMessage(java.lang.String, java.lang.String)
	*/
	public ReturnMessage decodeMessage(String code, String message)
			throws Exception{
		logger.debug(code + " decode:\n" + message.toString());
		return MessageUtil.decodeServerMessage(code, message);
	}

	/* （非 Javadoc）
	* <p>重写方法: encodeMessage|描述: </p>
	* @param code
	* @param objs
	* @return
	* @throws Exception
	* @see com.mingtech.application.pool.bank.codec.MessageCodecService#encodeMessage(java.lang.String, java.util.List)
	*/
	public String encodeMessage(String code, ReturnMessage response) throws Exception{
		String message = MessageUtil.encodeServerMessage(code, response);
		logger.debug(code + " encode:\n" + message.toString());
		return message;
	}
	
	public ReturnMessage decodeNewMessage(String code, String message) throws Exception {
		
		String msg = message.substring(Constants.MSG_LENGTH_LEN);//只保留报文体，前8位是报文头
		
		//解码
		return MessageUtil.decodeNewServerMessage(code, msg);
	}

	public String encodeNewMessage(String code, ReturnMessage request) throws Exception {

		//编码
		String message = MessageUtil.encodeNewServerMessage(code, request);

		//报文总长度（是否包含文件内容长度？待确认，暂不添加,已确认，不包括）
		String totalLength  = "";
		String keyValueLength = "";
		
//		if(message.split(Constants.SPLICT_CODE).length == 1){
			totalLength = StringUtil.leftPad(String.valueOf(message.getBytes(Constants.ENCODING).length), 8, '0');
			//keyValue长度
//			keyValueLength = StringUtil.leftPad(String.valueOf(message.split(Constants.SPLICT_CODE)[0].getBytes().length), 8, '0');//如果需要补位，则还需要做补位操作
//		}else{
//			String fileName = "<FILNAME>" + FileName.getFileNameServer() + "</>";
//			message = message.split(Constants.SPLICT_CODE)[0] + fileName + Constants.SPLICT_CODE + message.split(Constants.SPLICT_CODE)[1];
//			totalLength = StringUtil.leftPad(String.valueOf(message.split(Constants.SPLICT_CODE)[0].getBytes().length), 8, '0');
//			//keyValue长度
//			keyValueLength = StringUtil.leftPad(String.valueOf(message.split(Constants.SPLICT_CODE)[0].getBytes().length), 8, '0');//如果需要补位，则还需要做补位操作
//		}
		
		
		
		//获得最终报文
		message = totalLength  + message;		
	
		
		return message;
	}
}
