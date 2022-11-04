package com.mingtech.application.pool.bank.codec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.codec.config.MessageElement;
import com.mingtech.application.pool.bank.codec.config.MessageTemplate;
import com.mingtech.application.pool.bank.codec.config.MessageTemplateBuilder;
import com.mingtech.application.pool.bank.codec.converter.BigDecimalTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.DatetimeTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.IntTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.StringTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.TypeConverter;
import com.mingtech.application.pool.bank.codec.converter.VarStringTypeConverter;
import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.ReturnMessage;





/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: chenwei
 * @日期: Jun 16, 2009 5:11:29 PM
 * @描述: [StandardMessageCodec]报文处理相关工具类，本类为标准的协议解析实现，本协议采用北京银行的协议规范
 */
public class StandardMessageCodec{

	private static final Logger logger = Logger.getLogger(StandardMessageCodec.class);
	private static Map typeConverters = new HashMap();
	static{
		typeConverters.put(Constants.INT_TYPE, new IntTypeConverter());
		typeConverters.put(Constants.BIGDECIMAL_TYPE,
				new BigDecimalTypeConverter());
		typeConverters.put(Constants.DATE_TIME_TYPE,
				new DatetimeTypeConverter());
		typeConverters.put(Constants.STRING_TYPE, new StringTypeConverter());
		typeConverters.put(Constants.VAR_STRING_TYPE, new VarStringTypeConverter());
	}
	
	/**
	 * 报文中交易码的标识符号--仅对服务器端解码有效
	 */
	private Map txCodeNames = new HashMap();
	static{
		
	}

	/**
	 * 解码服务器端接收的消息 <p>方法名称: decodeMessage|描述: </p>
	 * @param code 交易码
	 * @param message XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public ReturnMessage decodeServerMessage(String code, String message)
			throws Exception{
		return decodeMessage(code, message, false);
	}

	/**
	 * 解码客户端接收的反馈消息 <p>方法名称: decodeMessage|描述: </p>
	 * @param code 交易码
	 * @param message XML格式报文，不包括10位报文头
	 * @param isClient 是否客户端解码
	 * @return
	 * @throws Exception
	 */
	public ReturnMessage decodeClientMessage(String code, String message)
			throws Exception{
		return decodeMessage(code, message, true);
	}

	/**
	 * 解码消息 <p>方法名称: decodeMessage|描述: </p>
	 * @param code 交易码
	 * @param message XML格式报文，不包括10位报文头
	 * @param isClient 是否客户端解码
	 * @return
	 * @throws Exception
	 */
	private ReturnMessage decodeMessage(String code, String message,
			boolean isClient) throws Exception{
		logger.debug("开始解码 解码报文为===" + message);
		ReturnMessage request = new ReturnMessage();
		Map heads = new HashMap();
		String[] messageArray = message.split(Constants.SPLICT_CODE);
		String mainBody = "";
		String detailBody = "";		
		if(messageArray.length == 1){
			mainBody = messageArray[0];
		}else if(messageArray.length == 2){
			mainBody = messageArray[0];
			detailBody = messageArray[1];
		}
		// 解析主报文
		logger.info("开始解码 主报文为 ===：" + mainBody);
		List list = Arrays.asList(mainBody.split("</>"));
		for(int i = 0; i < list.size(); i++){
			String s = (String)list.get(i);
			String[] sArray = s.split(">");
			if(sArray.length == 2){
				heads.put(sArray[0].substring(1), sArray[1]);
			}
		}
		if(code == null)
			code = getTxCode(heads);
		request.setCode(code);//设置交易码
		// 解析明细报文
		
		String templateFileName = isClient ? (code + Constants.TEMPLATE_SUFFIX) : code;
		MessageTemplate template = MessageTemplateBuilder.getMessageTemplate(templateFileName);
		if(detailBody != null && detailBody.trim().length() > 0){
			String[] detailArray = detailBody.split("\n");
			for(int i = 0; i < detailArray.length; i ++){
				String detailItem = detailArray[i];
				request.getDetails().add(unbuildDetailHRB(template, detailItem));
			}
		}
		// 转换主报文类型
		request.setHead(unbuildBody(template, heads));
		return request;
	}

	/**
	 * 编码服务器端发送的反馈消息 <p>方法名称: decodeMessage|描述: </p>
	 * @param code 交易码
	 * @param message XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public String encodeServerMessage(String code, ReturnMessage response)
			throws Exception{
		StringBuffer message = new StringBuffer();
		
		String responseCode = Constants.RESPONSE_CODE_E001;
		String txResponseCode = (String) response.getResponseCode();
		if(code == null){   // 交易码为空
			responseCode = txResponseCode == null ? responseCode : txResponseCode;
			buildFailureResponse(message, responseCode);
		}else{    //交易码非空			
			try{
				String templateFileName = (code + Constants.TEMPLATE_SUFFIX);
				MessageTemplate msgTemplate = MessageTemplateBuilder
						.getMessageTemplate(templateFileName);
				buildResponse(message,msgTemplate,response);
			}catch (Exception e){
				logger.error(e,e);
				message = new StringBuffer();
				responseCode = txResponseCode == null ? responseCode : txResponseCode;
				buildFailureResponse(message, responseCode);
			}
		}
		logger.debug("编码:" + message);
		return message.toString();
	}

	/**
	 * 编码客户端发送的请求信息
	 * @param code 交易码
	 * @param message XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public String encodeClientMessage(String code, ReturnMessage response)
			throws Exception{
		StringBuffer message = new StringBuffer();
		String templateFileName = code;
		MessageTemplate msgTemplate = MessageTemplateBuilder.getMessageTemplate(templateFileName);
		buildResponse(message,msgTemplate,response);
		logger.debug("编码:" + message);
		return message.toString();
	}
	
	/**
	 * 构建响应模板
	* <p>方法名称: buildResponse|描述: </p>
	* @param message
	* @param template
	* @param response
	 */
	private  void buildResponse(final StringBuffer message,MessageTemplate template,ReturnMessage response)
	{
		//组装主报文
		buildBody(message, template, response.getHead());
		//添加分隔符 用于在传输前分隔
		if(response.getDetails() != null && response.getDetails().size() > 0){
			message.append(Constants.SPLICT_CODE);
			// 组装明细报文 
			buildDetailsHRB(message,template, response.getDetails());
		}
	}

	/**
	 * 构建明细报文 <p>方法名称: buildDetails|描述: </p>
	 * @param sb
	 * @param template 模板
	 * @param objs 业务对象列表
	 */
	private void buildDetails(final StringBuffer sb,
			MessageTemplate template, List objs){
		if(objs == null || objs.size() == 0)
			return;
		sb.append("<details>");
		for(int i = 0; i < objs.size(); i++){
			if(template.getDetailItems() != null
					&& template.getDetailItems().size() > 0){
				sb.append("<mx><value>");
				for(int j = 0; j < template.getDetailItems().size(); j++){
					MessageElement element = (MessageElement) template
							.getDetailItems().get(j);
					TypeConverter converter = (TypeConverter) typeConverters
							.get(element.getDataType());
					sb.append(converter.toString(objs.get(i), element));
				}
				sb.append("</value></mx>");
			}
		}
		sb.append("</details>");
	}
	/**
	 * 构建明细报文 <p>方法名称: buildDetails|描述: </p>HRB专用
	 * @param sb
	 * @param template 模板
	 * @param objs 业务对象列表
	 */
	private String buildDetailsHRB(final StringBuffer sb, MessageTemplate template, List objs){
		if(objs == null || objs.size() == 0)
			return "";
		for(int i = 0; i < objs.size(); i++){
			if(template.getDetailItems() != null && template.getDetailItems().size() > 0){
				for(int j = 0; j < template.getDetailItems().size(); j++){
					MessageElement element = (MessageElement) template.getDetailItems().get(j);
					if(element.getDataType().equals(Constants.INT_TYPE) || element.getDataType().equals(Constants.BIGDECIMAL_TYPE)){//int类型的不加“”，其它类型添加“”
						TypeConverter converter = (TypeConverter) typeConverters.get(element.getDataType());
						sb.append(converter.toString(objs.get(i), element));
					}else{
						sb.append("\"");
						TypeConverter converter = (TypeConverter) typeConverters.get(element.getDataType());
						sb.append(converter.toString(objs.get(i), element));
						sb.append("\"");
					}
					sb.append("~");
				}
				sb.append("\n");//添加换行符
			}
		}
		//sb.append("</details>");
		return sb.toString();
	}


	/**
	 * 组装主报文字符串 <p>方法名称: buildBody|描述: </p>
	 * @param sb
	 * @param template 模板
	 * @param headers 主报文键值对
	 */
	private void buildBody(final StringBuffer sb,
			MessageTemplate template, Map headers){
		List elements = template.getMainItems();
		for(int i = 0; i < elements.size(); i++){
			MessageElement element = (MessageElement) elements.get(i);
			String ptName = element.getPtName();
			if(StringUtil.isEmpty(ptName)) ptName = element.getName();
			sb.append("<" + ptName + ">");
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			sb.append(converter.toString(headers, element));
			sb.append("</>");
		}
	}
	
	/**
	 * 如果服务器端响应失败，构建报文
	* <p>方法名称: buildFailureResponse|描述: </p>
	* @param sb
	* @param responseCode
	 */
	protected void buildFailureResponse(StringBuffer sb,
			String responseCode){
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		sb.append("<bob>");
		sb.append("<pub><key>" + ReturnMessage.RESPONSE_CODE + "</key>");
		sb.append("<value>" + responseCode + "</value></pub>");
		sb.append("</bob>");
	}
	
	/**
	 * 转换主报文类型 <p>方法名称: unbuildBody|描述: </p>
	 * @param template 模板
	 * @param headers 主报文键值对
	 * @return 经过类型转换后的主报文键值对
	 */
	private Map unbuildBody(MessageTemplate template, Map headers){
		Map results = new HashMap();
		List elements = template.getMainItems();
		logger.debug("主报文===========================");
		for(int i = 0; i < elements.size(); i++){
			MessageElement element = (MessageElement) elements.get(i);
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			
			String ptName = element.getPtName();
			if(StringUtil.isEmpty(ptName)) ptName = element.getName();//用Name表示PTNAME
			Object elementValue = converter.valueOf((String) headers.get(ptName), element);//数据值
			
			String elementName = element.getName();//内部表示
			results.put(elementName, elementValue);
			
			logger.debug("\t" + element.getDescription() + ":\t"
					+ (String) headers.get(element.getName()));
		}
		return results;
	}
	
	/**
	 * 解码单条明细报文 <p>方法名称: unbuildDetail|描述: </p>
	 * @param template 模板
	 * @param message 消息字符串
	 * @return
	 */
	private Map unbuildDetail(MessageTemplate template, String message){
		byte[] bytes = message.getBytes();
		int bLength = 0;
		MessageElement element = null;
		Map map = new HashMap();
		for(int i = 0; i < template.getDetailItems().size(); i++){
			element = (MessageElement) template.getDetailItems().get(i);
			String value = substring(bytes, bLength, element.getLength())
					.trim();
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			map.put(element.getName(), converter.valueOf(value, element));
			bLength += element.getLength();
			logger.debug("\t" + element.getDescription() + ":\t" + value);
		}
		return map;
	}
	/**
	 * 解码单条明细报文汉口银行专用 <p>方法名称: unbuildDetailHRB|描述: </p>
	 * @param template 模板
	 * @param message 消息字符串
	 * @return
	 */
	/*private Map unbuildDetailHRB(MessageTemplate template, String message){
		String[] detailItemArray = message.split(Constants.Detail_SPLICT_CODE);
		int bLength = 0;
		MessageElement element = null;
		Map map = new HashMap();
		for(int i = 0; i < template.getDetailItems().size(); i++){
			element = (MessageElement) template.getDetailItems().get(i);
			String value = detailItemArray[i].replaceAll("\n", "");
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			map.put(element.getName(), converter.valueOf(value, element));
			bLength += element.getLength();
			logger.debug("\t" + element.getDescription() + ":\t" + value);
		}
		return map;
	}*/
	
	private static Map unbuildDetailHRB(MessageTemplate template, String message) {
		String[] detailItemArray = message.split(Constants.Detail_SPLICT_CODE);
		MessageElement element = null;
		Map map = new HashMap();
		for (int i = 0; i < template.getDetailItems().size(); i++) {
			element = (MessageElement) template.getDetailItems().get(i);
			String value = detailItemArray[i].replaceAll("\n", "");
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			map.put(element.getName(), converter.valueOf(value, element));
			logger.debug("\t" + element.getDescription() + ":\t" + value);
		}
		return map;
	}




	/**
	 * <p>方法名称: substring|描述: 按字节长度截取字符串</p>
	 * @param oldStr 原字符串byte数组
	 * @param start 起始字节位置
	 * @param length 截取字节长度
	 * @return 返回的处理后字串
	 */
	private String substring(byte[] source, int start, int length){
		byte[] results = new byte[length];
		System.arraycopy(source, start, results, 0, length);
		return new String(results);
	}
	
	public String getTxCode(Map request){
		
		String code = null;
		
		Iterator txcodeNames = txCodeNames.keySet().iterator();
		while(txcodeNames.hasNext()){
			String codeName = (String)txcodeNames.next();
			code = (String)request.get(codeName);
			if(code != null && !"".equals(code)){
				break;
			}
		}
	
		return code;
	}
	
	public  void setTxCodeName(Map codeName){
		txCodeNames = codeName;
	}
}
