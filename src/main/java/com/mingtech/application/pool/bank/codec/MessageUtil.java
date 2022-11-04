package com.mingtech.application.pool.bank.codec;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.xml.sax.InputSource;

import com.mingtech.application.pool.bank.codec.config.ConfigReader;
import com.mingtech.application.pool.bank.codec.config.MessageElement;
import com.mingtech.application.pool.bank.codec.config.MessageTemplate;
import com.mingtech.application.pool.bank.codec.config.MessageTemplateBuilder;
import com.mingtech.application.pool.bank.codec.converter.BigDecimalTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.DatetimeTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.IntTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.StringTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.TypeConverter;
import com.mingtech.application.pool.bank.codec.converter.VarStringTypeConverter;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.ReturnMessage;
import com.mingtech.framework.common.util.StringUtil;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司 
 * </p>
 * 
 * @作者: chenwei
 * @日期: Jun 16, 2009 5:11:29 PM
 * @描述: [MessageUtil]报文处理相关工具类
 */
public class MessageUtil {

	private static final Logger logger = Logger.getLogger(MessageUtil.class);
	protected static Map typeConverters = new HashMap();
	static {
		typeConverters.put(Constants.INT_TYPE, new IntTypeConverter());
		typeConverters.put(Constants.BIGDECIMAL_TYPE,
				new BigDecimalTypeConverter());
		typeConverters.put(Constants.DATE_TIME_TYPE,
				new DatetimeTypeConverter());
		typeConverters.put(Constants.STRING_TYPE, new StringTypeConverter());
		typeConverters.put(Constants.VAR_STRING_TYPE,
				new VarStringTypeConverter());
	}

	/**
	 * 解码服务器端接收的消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code
	 *            交易码
	 * @param message
	 *            XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public static ReturnMessage decodeServerMessage(String code, String message)
			throws Exception {
		return decodeMessage(code, message, false);
	}

	/**
	 * 解码客户端接收的反馈消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code
	 *            交易码
	 * @param message
	 *            XML格式报文，不包括10位报文头
	 * @param isClient
	 *            是否客户端解码
	 * @return
	 * @throws Exception
	 */
	public static ReturnMessage decodeClientMessage(String code, String message)
			throws Exception {
		return decodeMessage(code, message, true);
	}

	/**
	 * 解码消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code
	 *            交易码
	 * @param message
	 *            XML格式报文，不包括10位报文头
	 * @param isClient
	 *            是否客户端解码
	 * @return
	 * @throws Exception
	 */
	private static ReturnMessage decodeMessage(String code, String message,
			boolean isClient) throws Exception {
		logger.debug("解码:" + message);
		ReturnMessage request = new ReturnMessage();
		StringReader sr = new StringReader(message);
		ConfigReader cReader = new ConfigReader(new InputSource(sr));
		// 解析主报文
		List list = cReader.getNodeList("/bob/pub/key");
		Node node = null;
		for (int i = 0; i < list.size(); i++) {
			node = (Node) list.get(i);
			request.getHead().put(
					cReader.getStringValue(node, "name").toUpperCase(),
					cReader.getStringValue(node, "value"));
		}
		if (code == null)
			code = (String) request.getHead().get(Constants.PP003);
		// 解析明细报文
		list = cReader.getNodeList("/bob/details/mx");
		String templateFileName = isClient ? (code + Constants.TEMPLATE_SUFFIX)
				: code;
		MessageTemplate template = MessageTemplateBuilder
				.getMessageTemplate(templateFileName);
		logger.debug("明细报文===========================");
		for (int i = 0; i < list.size(); i++) {
			node = (Node) list.get(i);
			String value = cReader.getStringValue(node, "value");
			request.getDetails().add(unbuildDetail(template, value));
		}
		// 转换主报文类型
		request.setHead(unbuildBody(template, request.getHead()));
		return request;
	}

	/**
	 * 编码服务器端发送的反馈消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code
	 *            交易码
	 * @param message
	 *            XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public static String encodeServerMessage(String code, ReturnMessage response)
			throws Exception {
		StringBuffer message = new StringBuffer();
		if (!Constants.RESPONSE_CODE_0000.equals((String) response.getHead()
				.get(Constants.PP039))) { // 交易码为空
			buildFailureResponse(message, (String) response.getHead().get(
					Constants.PP039));
		} else { // 交易码非空
			try {
				String templateFileName = (code + Constants.TEMPLATE_SUFFIX);
				MessageTemplate msgTemplate = MessageTemplateBuilder
						.getMessageTemplate(templateFileName);
				buildResponse(message, msgTemplate, response);
			} catch (Exception e) {
				logger.error(e);
				message = new StringBuffer();
				buildFailureResponse(message, Constants.RESPONSE_CODE_E001);
			}
		}
		logger.debug("encodeServerMessage编码结束。");
		return message.toString();
	}

	public static String encodeCreditClientMessage(String code,
			ReturnMessage response) throws Exception {
		StringBuffer message = new StringBuffer();
		String templateFileName = code;
		MessageTemplate msgTemplate = MessageTemplateBuilder
				.getMessageTemplate(templateFileName);
		if(templateFileName.equals("S003003990MS5700")||templateFileName.equals("S003003990MS5736")){
			/*MessageTemplate msgTemplates = MessageTemplateBuilder
					.getMessageTemplates("5700");*/
			buildCreditResponse(message, msgTemplate,templateFileName,response);
		}else{
		buildCreditResponse(message, msgTemplate,null, response);
		}
		logger.debug("encodeCreditClientMessage编码结束。");
		return message.toString();
	}

	/**
	 * 构建核心响应模板
	 * 
	 * @param message
	 * @param template
	 * @param response
	 */
	private static void buildCreditResponse(final StringBuffer message,
			MessageTemplate template,String code, ReturnMessage response) {
		// 组装主报文
		if(code==null){
			buildCreditBody(message, template,response.getHead());
		}else{
			buildCreditBodys(message, template, code,response.getHead());
		}
		// 添加分隔符 用于在传输前分隔
		if (response.getDetails() != null && response.getDetails().size() > 0) {
			message.append(Constants.SPLICT_CODE);
			// 组装明细报文
			buildDetailsHRB(message, template, response.getDetails());
		}
	}

	/**
	 * ESB报文组装请求主报文字符串
	 */
	private static void buildCreditBody(final StringBuffer sb,
			MessageTemplate template, Map headers) {
		List elements = template.getMainItems();
		sb.append("<RequestBody>\n");
		for (int i = 0; i < elements.size(); i++) {
			MessageElement element = (MessageElement) elements.get(i);
			String ptName = element.getPtName();
			if (StringUtil.isEmpty(ptName))
				ptName = element.getName();
			sb.append("<" + ptName + ">");
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			sb.append(converter.toString(headers, element));
			sb.append("</" + ptName + ">");
		}
		sb.append("</RequestBody>\n");
	}
	/**
	 * 
	 * ESB报文组装请求主报文字符串 (注：此方法只用于二代接口)
	 */
	private static void buildCreditBodys(final StringBuffer sb,
			MessageTemplate template, String code,Map headers) {
		List elements = template.getMainItems();
		sb.append("<RequestBody>\n");
		for (int i = 0; i < elements.size(); i++) {
			MessageElement element = (MessageElement) elements.get(i);
			String ptName = element.getPtName();
			if (StringUtil.isEmpty(ptName))
				ptName = element.getName();
			sb.append("<" + ptName + ">");
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			sb.append(converter.toString(headers, element));
			sb.append("</" + ptName + ">");
		}
	}
	private static String buildDetailsHRB(final StringBuffer sb,
			MessageTemplate template, List objs) {
		if (objs == null || objs.size() == 0)
			return "";
		for (int i = 0; i < objs.size(); i++) {
			if (template.getDetailItems() != null
					&& template.getDetailItems().size() > 0) {
				for (int j = 0; j < template.getDetailItems().size(); j++) {
					MessageElement element = (MessageElement) template
							.getDetailItems().get(j);
					Map tempMap = (Map)objs.get(i);
					// 明细MAP里包含模板定义的
					if(tempMap.containsKey(element.getName())) {
						if (element.getDataType().equals(Constants.INT_TYPE)
								|| element.getDataType().equals(
										Constants.BIGDECIMAL_TYPE)) {// int类型的不加“”，其它类型添加“”
							TypeConverter converter = (TypeConverter) typeConverters
							.get(element.getDataType());
							sb.append(converter.toString(tempMap, element));
						} else {
							sb.append("\"");
							TypeConverter converter = (TypeConverter) typeConverters
							.get(element.getDataType());
							sb.append(converter.toString(tempMap, element));
							sb.append("\"");
						}
						sb.append("~");
					}
				}
				sb.append("\n");// 添加换行符
			}
		}
		return sb.toString();
	}

	/**
	 * @param code
	 * @param message
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static ReturnMessage decodeCreditClientMessage(String code,
			String message) throws Exception {
		return decodeCreditMessage(code, message, true);
	}

	/**
	 * 联盟核心解码消息
	 */
	private static ReturnMessage decodeCreditMessage(String code,
			String message, boolean isClient) throws Exception {
		logger.debug("*****************解码开始**************************");
		ReturnMessage request = new ReturnMessage();
		Map heads = new HashMap();
		String[] messageArray = message.split(Constants.SPLICT_CODE);
		String mainBody = "";
		String detailBody = "";
		if (messageArray.length == 1) {
			mainBody = messageArray[0];
		} else if (messageArray.length == 2) {
			mainBody = messageArray[0];
			detailBody = messageArray[1];
		}

		String templateFileName = isClient ? (code + Constants.TEMPLATE_SUFFIX)
				: code;
		MessageTemplate template = MessageTemplateBuilder
				.getMessageTemplate(templateFileName);
		// 解析主报文
		if (mainBody != null && mainBody.trim().length() > 0) {
			logger.info("开始解码 主报文为 ===：" + mainBody);
			StringReader sr = new StringReader(mainBody);
			ConfigReader cReader = new ConfigReader(new InputSource(sr));
			// 解析主报文
			Element root = cReader.getRootElement();
			List listHeader = root.element("ResponseHeader").elements();
			Element eHeader = null;
			for (int i = 0; i < listHeader.size(); i++) {
				eHeader = (Element) listHeader.get(i);
				heads.put(eHeader.getName(), eHeader.getText());
			}
			List listBodyBody = root.element("ResponseBody").elements();
			List listBodyBodys=null;
			Element eBodyBody = null;
				for (int i = 0; i < listBodyBody.size(); i++) {
					eBodyBody = (Element) listBodyBody.get(i);
					listBodyBodys=eBodyBody.elements();
					if(listBodyBodys!=null&&listBodyBodys.size()>0){
						for (int j = 0; j < listBodyBodys.size(); j++){
							eBodyBody = (Element) listBodyBodys.get(j);
							heads.put(eBodyBody.getName(), eBodyBody.getText());
						}
					}else{
					heads.put(eBodyBody.getName(), eBodyBody.getText());	
					}
			}
			List faultBody = root.element("Fault").elements();
			Element eFault = null;
			for (int i = 0; i < faultBody.size(); i++) {
				eFault = (Element) faultBody.get(i);
				// 专门针对返回的/Fault/Detail/TxnStat
				List childrenList = eFault.elements();
				if (childrenList != null && childrenList.size() > 0) {
					Element child = (Element) childrenList.get(0);
					heads.put(child.getName(), child.getText());
				} else {
					heads.put(eFault.getName(), eFault.getText());
				}
			}
			request.setHead(unbuildBody(template, heads));
		}
		// 解析明细报文
		if (detailBody != null && detailBody.trim().length() > 0) {
			logger.info("开始解码 明细报文为 ===：" + detailBody);
			String[] detailArray = detailBody.split("\n");
			for (int i = 0; i < detailArray.length; i++) {
				String detailItem = detailArray[i];
				request.getDetails()
						.add(unbuildDetailHRB(template, detailItem));
			}
		}
		return request;

	}
	/**
	 * 编码客户端发送的请求信息
	 * 
	 * @param code
	 *            交易码
	 * @param message
	 *            XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public static String encodeClientMessage(String code, ReturnMessage response)
			throws Exception {
		StringBuffer message = new StringBuffer();
		String templateFileName = code;
		MessageTemplate msgTemplate = MessageTemplateBuilder
				.getMessageTemplate(templateFileName);
		buildResponse(message, msgTemplate, response);
		logger.debug("编码:" + message);
		return message.toString();
	}

	/**
	 * 构建响应模板
	 * <p>
	 * 方法名称: buildResponse|描述:
	 * </p>
	 * 
	 * @param message
	 * @param template
	 * @param response
	 */
	private static void buildResponse(final StringBuffer message,
			MessageTemplate template, ReturnMessage response) {
		message.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		message.append("<bob>");
		// 组装主报文
		buildBody(message, template, response.getHead());
		// 组装明细报文
		buildDetails(message, template, response.getDetails());
		message.append("</bob>");
	}

	/**
	 * 构建明细报文
	 * <p>
	 * 方法名称: buildDetails|描述:
	 * </p>
	 * 
	 * @param sb
	 * @param template
	 *            模板
	 * @param objs
	 *            业务对象列表
	 */
	private static void buildDetails(final StringBuffer sb,
			MessageTemplate template, List objs) {
		if (objs == null || objs.size() == 0)
			return;
		sb.append("<details>");
		for (int i = 0; i < objs.size(); i++) {
			if (template.getDetailItems() != null
					&& template.getDetailItems().size() > 0) {
				sb.append("<mx><value>");
				for (int j = 0; j < template.getDetailItems().size(); j++) {
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
	 * 组装主报文字符串
	 * <p>
	 * 方法名称: buildBody|描述:
	 * </p>
	 * 
	 * @param sb
	 * @param template
	 *            模板
	 * @param headers
	 *            主报文键值对
	 */
	private static void buildBody(final StringBuffer sb,
			MessageTemplate template, Map headers) {
		sb.append("<pub>");
		List elements = template.getMainItems();
		for (int i = 0; i < elements.size(); i++) {
			MessageElement element = (MessageElement) elements.get(i);
			sb.append("<key>");
			sb.append("<name>").append(element.getName()).append("</name>");
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			sb.append("<value>").append(converter.toString(headers, element))
					.append("</value>");
			sb.append("</key>");
		}
		sb.append("</pub>");
	}

	/**
	 * 如果服务器端响应失败，构建报文
	 * <p>
	 * 方法名称: buildFailureResponse|描述:
	 * </p>
	 * 
	 * @param sb
	 * @param responseCode
	 */
	private static void buildFailureResponse(StringBuffer sb,
			String responseCode) {
		sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		sb.append("<bob>");
		sb.append("<pub><key><name>PP039</name>");
		sb.append("<value>" + responseCode + "</value></key></pub>");
		sb.append("</bob>");
	}

	/**
	 * 转换主报文类型
	 * <p>
	 * 方法名称: unbuildBody|描述:
	 * </p>
	 * 
	 * @param template
	 *            模板
	 * @param headers
	 *            主报文键值对
	 * @return 经过类型转换后的主报文键值对
	 */
	private static Map unbuildBody(MessageTemplate template, Map headers) {
		Map results = new HashMap();
		List elements = template.getMainItems();
		logger.debug("主报文===========================");
		for (int i = 0; i < elements.size(); i++) {
			MessageElement element = (MessageElement) elements.get(i);
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			results.put(element.getName(), converter.valueOf(
					(String) headers.get(element.getName()),
					element));
			logger.debug("\t" + element.getDescription() + ":\t"
					+ (String) headers.get(element.getName()));
		}
		return results;
	}

	/**
	 * 解码单条明细报文
	 * <p>
	 * 方法名称: unbuildDetail|描述:
	 * </p>
	 * 
	 * @param template
	 *            模板
	 * @param message
	 *            消息字符串
	 * @return
	 */
	private static Map unbuildDetail(MessageTemplate template, String message) {
		byte[] bytes = message.getBytes();
		int bLength = 0;
		MessageElement element = null;
		Map map = new HashMap();
		for (int i = 0; i < template.getDetailItems().size(); i++) {
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
	 * 解码单条明细报文汉口银行专用
	 * <p>
	 * 方法名称: unbuildDetailHRB|描述:
	 * </p>
	 * 
	 * @param template
	 *            模板
	 * @param message
	 *            消息字符串
	 * @return
	 */
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
	 * <p>
	 * 方法名称: unBuildClientDetail|描述: 解码客户端收到消息的单条明细
	 * </p>
	 * 
	 * @param template
	 * @param message
	 * @return
	 */
	private static Map unBuildClientDetail(MessageTemplate template, Element el) {
		MessageElement element = null;
		Map results = new HashMap();
		List elements = template.getDetailItems();
		logger.info("明细报文===========================");
		logger.info(el.asXML());
		List details = el.elements();
		Map detailsMap = new HashMap();
		Element detailElement = null;
		for (int i = 0; i < details.size(); i++) {
			detailElement = (Element) details.get(i);
			detailsMap.put(detailElement.getName(), detailElement.getText());
		}
		for (int i = 0; i < elements.size(); i++) {
			element = (MessageElement) elements.get(i);
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			Object elementValue = converter.valueOf((String) detailsMap
					.get(element.getName()), element);// 数据值
			String elementName = element.getName();// 内部表示
			results.put(elementName, elementValue);
			logger.debug("\t" + element.getDescription() + ":\t"
					+ (String) detailsMap.get(element.getName().toUpperCase()));
		}
		return results;
	}

	/**
	 * <p>
	 * 方法名称: substring|描述: 按字节长度截取字符串
	 * </p>
	 * 
	 * @param oldStr
	 *            原字符串byte数组
	 * @param start
	 *            起始字节位置
	 * @param length
	 *            截取字节长度
	 * @return 返回的处理后字串
	 */
	public static String substring(byte[] source, int start, int length) {
		byte[] results = new byte[length];
		System.arraycopy(source, start, results, 0, length);
		return new String(results);
	}
	
	/**
	 * 解码服务器端接收的消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code
	 *            交易码
	 * @param message
	 *            XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public static ReturnMessage decodeNewServerMessage(String code, String message)
			throws Exception {
		return decodeNewServerMessage(code, message, false);
	}
	/**
	 * 解码申请消息 <p>方法名称: decodeMessage|描述: </p>
	 * @param code 交易码
	 * @param message XML格式报文，不包括8位报文头
	 * @param isClient 是否客户端解码
	 * @return
	 * @throws Exception
	 */
	private static ReturnMessage decodeNewServerMessage(String code, String message,
			boolean isClient) throws Exception{
		logger.debug("解码:" + message);
		ReturnMessage request = new ReturnMessage();
		Map heads = new HashMap();
		StringReader sr = new StringReader(message);
		InputSource in = new InputSource(sr);
		ConfigReader cReader = new ConfigReader(in);
		// 解析主报文		
		code = cReader.getStringValue("/root/TXCODE");
		List listBody = cReader.getElementList("/root");
		Element eBody = null;
		for(int i = 0; i < listBody.size(); i++){
			eBody = (Element) listBody.get(i);
			heads.put(eBody.getName(),eBody.getText());
		}
		String templateFileName = isClient ? (code + Constants.TEMPLATE_SUFFIX)
				: code;
		MessageTemplate template = MessageTemplateBuilder
				.getMessageTemplate(templateFileName);
		request.setHead(unbuildNewBody(template, heads));
		// 解析明细报文
		List listDetails = cReader.getNodeList("/root/DetailList/Detail");
		logger.debug("明细报文===========================");
		Element eDetails = null;
		if(listDetails != null && listDetails.size()>0){
			for(int i = 0; i < listDetails.size(); i++){
				eDetails = (Element) listDetails.get(i);
				request.getDetails().add(unBuildClientDetail(template, eDetails));
			}
		}
		
		return request;
	}
	
	/**
	 * 转换主报文类型 <p>方法名称: unbuildNewBody|描述: </p>
	 * @param template 模板
	 * @param headers 主报文键值对
	 * @return 经过类型转换后的主报文键值对
	 */
	private static Map unbuildNewBody(MessageTemplate template, Map headers){
		Map results = new HashMap();
		List elements = template.getMainItems();
		logger.debug("主报文===========================");
		for(int i = 0; i < elements.size(); i++){
			MessageElement element = (MessageElement) elements.get(i);
			TypeConverter converter = (TypeConverter) typeConverters
					.get(element.getDataType());
			if(!element.getName().equals("BkIntrstRate")&&!element.getName().equals("RpdIntrstRate")){
				results.put(element.getName(), converter.valueOf((String) headers
						.get(element.getName()), element));
			}else{
				results.put(element.getName(), deBigdeciaml((String) headers
						.get(element.getName()), element));
			}
		}
		return results;
	}
	/**
	 * <p>方法名称：deBigdeciaml|描述：转换普兰传过来的转贴现利率</P>
	 * @author wgbaojiaming
	 * @param value
	 * @param element
	 * @return
	 */
	public static Object deBigdeciaml(String value, MessageElement element){
		if(null == value || "".equals(value))
			return null;		
		String sText = value;
		BigDecimal temp = new BigDecimal(value);
		temp = temp.setScale(6, BigDecimal.ROUND_HALF_UP);
		return temp;
	}
	/**
	 * 编码服务器端发送的反馈消息 <p>方法名称: decodeMessage|描述: </p>
	 * @param code 交易码
	 * @param response 返回报文信息集合
	 * @return message 变长字符串格式，不包括10位报文头
	 * @throws Exception
	 */
	public static String encodeNewServerMessage(String code, ReturnMessage response)
			throws Exception{
		StringBuffer message = new StringBuffer();
		
		String templateFileName = code + Constants.TEMPLATE_SUFFIX;//构建返回模板名称
		MessageTemplate msgTemplate = MessageTemplateBuilder//根据模板名称构建报文映射模板对象
				.getMessageTemplate(templateFileName);
		
		if(response == null || response.getHead() == null 
				|| response.getHead().get("RESPONSECODE") == null){
			buildNewFailureResponse(message,msgTemplate);
		}
		try{
			buildNewResponse(message,msgTemplate,response);
		}catch (Exception e){
			logger.error(e);
			message = new StringBuffer();
			buildNewFailureResponse(message,msgTemplate);
		}
		logger.info("编码:" + message);
		return message.toString();
	}
	/**
	 * 构建响应模板
	* <p>方法名称: buildResponse|描述: </p>
	* @param message
	* @param template
	* @param response
	 */
	private static void buildNewResponse(final StringBuffer message,MessageTemplate template,ReturnMessage response)
	{
		message.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		message.append("<root>");
		//组装主报文
		buildNewRespBody(message, template, response.getHead());
		// 组装明细报文
		buildNewDetails(message, template, response);
		message.append("</root>");
	}

	
	/**
	 * 组装主报文字符串 <p>方法名称: buildBody|描述: </p>
	 * @param sb
	 * @param template 模板
	 * @param headers 主报文键值对
	 */
	private static void buildNewRespBody(final StringBuffer sb,
			MessageTemplate template, Map headers){
		List elements = template.getMainItems();
		
		for(int i = 0; i < elements.size(); i++){
			MessageElement element = (MessageElement) elements.get(i);
			
			TypeConverter converter = (TypeConverter)typeConverters.get(element.getDataType());
			Object fieldValue = converter.toString(headers, element);
			
			if(null != fieldValue && !"".equals(fieldValue)){
			sb.append("<").append(element.getName()).append(">");
			sb.append(converter.toString(headers, element));
			sb.append("</").append(element.getName()).append(">");
			}else{
				sb.append("<").append(element.getName()).append("/>");
			}
		}
	}
	
	/**
	 * 构建明细报文 <p>方法名称: buildDetails|描述: </p>
	 * @param sb
	 * @param template 模板
	 * @param objs 业务对象列表
	 */
	private static void buildNewDetails(final StringBuffer sb,
			MessageTemplate template, ReturnMessage response){
		List objs = response.getDetails();
		if(objs == null || objs.size() == 0)
			return;
		sb.append("<DetailList>");
//			sb.append("<count>"+objs.size()+"</count>");
			for(int i = 0; i < objs.size(); i++){
				if(template.getDetailItems() != null
						&& template.getDetailItems().size() > 0){
					sb.append("<Detail>");
					for(int j = 0; j < template.getDetailItems().size(); j++){
						MessageElement element = (MessageElement) template
								.getDetailItems().get(j);
						
						TypeConverter converter = (TypeConverter) typeConverters
						.get(element.getDataType());
						Object fieldValue = converter.toString(objs.get(i), element);
						if (null != fieldValue && !"".equals(fieldValue)) {
							sb.append("<").append(element.getName()).append(">");
							sb.append(converter.toString(objs.get(i), element));
							sb.append("</").append(element.getName()).append(">");
						}else {
							sb.append("<").append(element.getName()).append("/>");
						}
						
					}
					sb.append("</Detail>");
				}
			}
		
		sb.append("</DetailList>");
	}
	
	/**
	 * 如果服务器端响应失败，构建报文
	* <p>方法名称: buildNewFailureResponse|描述: </p>
	* @param sb
	* @param responseCode
	 */
	private static void buildNewFailureResponse(StringBuffer sb,
			MessageTemplate template){
		List head = template.getMainItems();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<root>");
		sb.append("<RESPONSECODE>");
		sb.append("9999");
		sb.append("</RESPONSECODE>");
		sb.append("<RESPONSERMK>");
		sb.append("处理失败");
		sb.append("</RESPONSERMK>");
		sb.append("</root>");
	}
}
