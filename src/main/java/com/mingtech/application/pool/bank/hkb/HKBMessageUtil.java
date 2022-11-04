package com.mingtech.application.pool.bank.hkb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dc.eai.data.Array;
import com.dc.eai.data.CompositeData;
import com.dc.eai.data.Field;
import com.dc.eai.data.FieldAttr;
import com.dc.eai.data.FieldType;
import com.dcfs.esb.client.converter.PackUtil;
import com.dcfs.esb.client.converter.XmlStaxParse;
import com.dcfs.esb.ftp.client.FtpClientConfig;
import com.dcfs.esb.ftp.client.FtpGet;
import com.mingtech.application.pool.bank.codec.MessageUtil;
import com.mingtech.application.pool.bank.codec.config.MessageElement;
import com.mingtech.application.pool.bank.codec.config.MessageTemplateBuilder;
import com.mingtech.application.pool.bank.codec.config.MessageTemplateNew;
import com.mingtech.application.pool.bank.codec.converter.TypeConverter;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 
 * @author Orange
 * 
 * @copyright 北京明润华创科技有限责任公司
 * 
 * @description
 * 
 */
public class HKBMessageUtil extends MessageUtil {

	private static final Logger logger = Logger.getLogger(HKBMessageUtil.class);

	/**
	 * 解码服务器端接收的消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code    交易码
	 * @param message XML格式报文，不包括10位报文头
	 * @return
	 * @throws Exception
	 */
	public static ReturnMessageNew decodeServerMessageNew(String code, String message) throws Exception {
		return decodeMessageNew(code, message, false);
	}

	/**
	 * 解码客户端接收的反馈消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code     交易码
	 * @param message  XML格式报文，不包括10位报文头
	 * @param isClient 是否客户端解码
	 * @return
	 * @throws Exception
	 */
	public static ReturnMessageNew decodeClientMessageNew(String code, String message) throws Exception {
		return decodeMessageNew(code, message, true);
	}

	/**
	 * 解码消息
	 * <p>
	 * 方法名称: decodeMessage|描述:
	 * </p>
	 * 
	 * @param code     交易码
	 * @param message  XML格式报文，不包括10位报文头
	 * @param isClient 是否客户端解码
	 * @return
	 * @throws Exception
	 */
	private static ReturnMessageNew decodeMessageNew(String code, String message, boolean isClient) throws Exception {
		logger.debug("解码:" + message);
		ReturnMessageNew request = new ReturnMessageNew();

		XmlStaxParse parse = new XmlStaxParse();
		CompositeData rootCD = new CompositeData();
		parse.parse(message.getBytes(Constants.ENCODING), rootCD);

		CompositeData sysHead = rootCD.getStruct("SYS_HEAD");
		CompositeData appHead = rootCD.getStruct("APP_HEAD");
		CompositeData localHead = rootCD.getStruct("LOCAL_HEAD");
		CompositeData fileHead = rootCD.getStruct("FILE_HEAD");
		CompositeData body = rootCD.getStruct("BODY");

		// code
		if (sysHead.getField("SERVICE_CODE") == null) {
			throw new Exception("服务代码为空 ");
		}
		if (sysHead.getField("SERVICE_SCENE") == null) {
			throw new Exception("服务应用场景为空 ");
		}

		String SERVICE_CODE = (String) sysHead.getField("SERVICE_CODE").getValue() + "|"
				+ (String) sysHead.getField("SERVICE_SCENE").getValue();
		// 根据服务代码获取内部交易码
		code = TransCodeMap.templateMapNew().getCodeMap().get(SERVICE_CODE);
		if (StringUtil.isEmpty(code)) {
			throw new Exception("根据服务代码[" + SERVICE_CODE + "]获取交易码为空，请检查TransCodeMap.xml文件 ");
		}
		request.setTxCode(code);

		String templateFileName = isClient ? (code + Constants.TEMPLATE_SUFFIX) : code;
		MessageTemplateNew template = MessageTemplateBuilder.getMessageTemplateNew(templateFileName);

		unbuildCompositeData(sysHead, template.getSysHeaderItems(), request.getSysHead());
		// 从head把值放到RET中
		Ret ret = new Ret();
		String RET_CODE = (String) request.getSysHead().get("RET.RET_CODE");
		String RET_MSG = (String) request.getSysHead().get("RET.RET_MSG");
		ret.setRET_CODE(RET_CODE);
		ret.setRET_MSG(RET_MSG);
		request.setRet(ret);
		unbuildCompositeData(appHead, template.getAppHeaderItems(), request.getAppHead());
		unbuildCompositeData(localHead, template.getLocalHeaderItems(), request.getLocalHead());
		unbuildCompositeData(fileHead, template.getFileHeaderItems(), request.getFileHead());
		unbuildCompositeData(body, template.getBodyItems(), request.getBody());
		// 解析到明细数组 文件、数组并存 均放入details
//		if(request.getFileHead() == null || request.getFileHead().size() == 0) {
			unbuildCompositeDataDetail(body, template.getDetailItems(), request.getDetails());
//		}
		return request;
	}

	/**
	 * 解析CompositeData
	 * <p>
	 * 方法名称: unbuildCompositeData|描述:
	 * </p>
	 * 
	 * @param cd 系统报文头
	 * @param elements      报文模板List
	 * @param header        系统报文头赋值MAP
	 */
	private static void unbuildCompositeData(CompositeData cd, List elements, Map header) throws Exception {
		Field field;
		for (int i = 0; i < elements.size(); i++) {
			MessageElement element = (MessageElement) elements.get(i);
			String key = element.getName();
			String parentKey = "";
			String subKey = "";
			if (key.contains(".")) {
				parentKey = key.split("\\.")[0];
				subKey = key.split("\\.")[1];
				// 结构是CompositeData[Struct[Field]]
				if (cd.getArray(parentKey) != null) {
					// 内置的Struct
					CompositeData arrCD = cd.getArray(parentKey).getStruct(0);
					if (arrCD == null) {
						continue;
					}
					field = arrCD.getField(subKey);
					logger.debug(field);
					if (field != null) {
						header.put(element.getName(), field.getValue());
					} else {
						logger.debug("field" + element.getName() + "is not exist");
					}
				}
			} else {
				field = cd.getField(element.getName());
//				logger.info(field);
				if (field != null) {
					header.put(element.getName(), field.getValue());
				} else {
					logger.debug("field" + element.getName() + "is not exist");
				}
			}

		}
	}

	/**
	 * 解析CompositeData
	 * <p>
	 * 方法名称: unbuildCompositeData|描述:
	 * </p>
	 *
	 * @param cd 系统报文头
	 * @param elements      报文模板List
	 * @param list       文件明细赋值
	 */
	private static void unbuildCompositeFileData(CompositeData cd, List elements, List list) throws Exception {
		BufferedReader br = null;
		FtpGet ftp = null;
		try {
			//服务端文件下载
			//文件下载
			String fileFlag = "0";
			String fileName = "";
			if (cd != null && cd.size() > 0) {
				fileFlag = (String) cd.getField(HKBConstants.FTP_FILE_FLAG).getValue();
				fileName = (String) cd.getField(HKBConstants.FTP_FILE_PATH).getValue();
//				splictCode = HKBConstants.FTP_SPILCT_CODE;
			}
	
			logger.info("文件下载模块开始........");
			// 1.若有FTP文件则进行下载并解析
			if (fileFlag.equals(HKBConstants.FILE_FLAG_2)) {
				// YeCheng 对应ecds.tft.localPath配置
				String localFile = ProjectConfig.getInstance().getLocalPath() + fileName;
				//String localFile = fileName;
				logger.info("加载FTP配置文件开始");
				FtpClientConfig.loadConf(HKBConstants.FTP_CLIENT_CONFIG);
				logger.info("加载FTP配置文件结束，下载文件开始，文件名为【" + fileName + "】");
				ftp = new FtpGet(fileName, localFile, false);
				logger.info("下载文件结束，本地文件名为【" + localFile + "】");
				if (ftp.doGetFile()) {
//				if (true) {	
					logger.info("下载成功，解析文件开始");
					// 解析报文文件
	//
					//处理文件的数据
					List elementList = new ArrayList();
					for (Object obj : elements){
						MessageElement element = (MessageElement) obj;
						String key = element.getName();
						if (!key.contains(".")) {
							elementList .add(element);
						}
					}
	
				
				
					String encoding;//编码
					if(fileName.contains("CORE") || fileName.contains("TELLER") || fileName.contains("CMS")){  //核心编码为：GBK
						encoding = Constants.ENCODING_GBK;
					}else{//其他系统的文件编码为 UTF-8
						encoding = Constants.ENCODING;
					}
//					localFile = "D:"+File.separator+"hkcs"+File.separator+"PJE014";
					br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(localFile)), encoding));
					String str = null;
					Map headerMap = null; // 每行MAP
					String[] msgVal = null; // 每行去掉分隔符后的值（数组）
					MessageElement element = null;
					while ((str = (br.readLine())) != null) {
						headerMap = new HashMap();
						msgVal = str.split("\\|", elementList.size());
						for (int i = 0; i < elementList.size(); i++) {
							element = (MessageElement) elementList.get(i);
							logger.debug(msgVal[i]);

							if (msgVal[i] != null) {
								headerMap.put(element.getName(), msgVal[i]);
							} else {
								logger.debug("field" + element.getName() + "is not exist");
							}
						}
						// 将每行解析的值放到MAP
						list.add(headerMap);
					}
				
	
					logger.debug("报文明细[" + list + "]");
				} else {
					throw new Exception(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC021);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			throw new Exception("解析报文文件异常，异常[" + ex.getMessage() + "]");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (ftp != null) {
					ftp.close();
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(),ex);
			}

		}
		logger.info("文件下载模块结束........");

	}
	// 解析明细数组
	private static void unbuildCompositeDataDetail(CompositeData cd, List elements, List list) throws Exception {
		MessageElement element = null;
		boolean flag = false;
		
		if (elements != null && elements.size() > 0) {
			
			//数据类型为(SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_KEY)两层数组时，内置数组组名Map集合
			Map<String , String> innerNameMap = new HashMap<String, String>();
			List innerNames = new ArrayList();
			
			// 1.首先获取pakey（最外围数组名）
			String pakey = "";//外围数组名
			List<String> parentKeyList = new ArrayList<String>();
			for (Object object : elements) {
				element = (MessageElement) object;
				if (element.getName().contains(".")) {
					
					pakey = element.getName().split("\\.")[0];
					if(!parentKeyList.contains(pakey)){
						parentKeyList.add(pakey);
					}
					
					if (element.getName().split("\\.").length ==3) {
						flag = true;
						System.out.println(element.getName().split("\\.")[0]+"."+element.getName().split("\\.")[1]);
						innerNameMap.put(element.getName().split("\\.")[0]+"."+element.getName().split("\\.")[1] , element.getName().split("\\.")[1] );//
					}
					continue; 
				}
				
			}
			if(StringUtil.isEmpty(pakey)){
				return;
			}
			if(parentKeyList.size() == 1){
				String parentKey = parentKeyList.get(0);
				
				// 2.根据parentKey获取Struct，因获取不到Struct的数量，所以只能先获取再判断
				// 结构是CompositeData[Struct[Field]]
				if (cd.getArray(parentKey) != null) {
					int index = 0;
					
					Field field = new Field(null);
					Map header = null;//
					CompositeData arrCD = cd.getArray(parentKey).getStruct(index);//外围数组
					CompositeData arrCD2 = new CompositeData();
					
					if(flag){
						if(arrCD != null){
							do {
								Map<String , List> innerMap = new HashMap<String, List>();//存放内置数组的值
								Iterator it = innerNameMap.values().iterator();  
								header = new HashMap();
								for (Map.Entry<String, String> entry : innerNameMap.entrySet()) {
									String key = entry.getKey();
									String value = entry.getValue();
									
									if(!key.contains(parentKey)){
										continue;
									}
									
									int index2 = 0;//计算内部数组
									List innerList = new ArrayList();//存放内置数组的值
									
									if(arrCD.getArray(value) != null){
										arrCD2 = arrCD.getArray(value).getStruct(index2);//内部数组
									}
									do {
										if (arrCD2 != null && arrCD2.isStruct()) {
											Map map = new HashMap();
											String subKey = "";
											for (int i = 0; i < elements.size(); i++) {
												element = (MessageElement) elements.get(i);
												if (element.getName().contains(".")) {
													if(element.getName().split("\\.").length ==3){
														subKey = element.getName().split("\\.")[2];
														if(arrCD2 != null){
															field = arrCD2.getField(subKey);
														}
													}
													logger.debug(field);
													if (field != null) {
														map.put(element.getName(), field.getValue());
													} else {
														logger.debug("field" + element.getName() + "is not exist");
													}
												}
											}
											innerList.add(map);
										}
										index2 = index2 + 1;
										arrCD2 = arrCD.getArray(value).getStruct(index2);
									} while (arrCD2 != null && arrCD2.isStruct());
									
									header.put(value, innerList);
								}
//							list.add(header);
								
								List innerList = new ArrayList();//存放内置数组的值
								
								if (arrCD != null && arrCD.isStruct()) {
									Map map = new HashMap();
									String subKey = "";
									for (int i = 0; i < elements.size(); i++) {
										element = (MessageElement) elements.get(i);
										if (element.getName().contains(".")) {
											if(element.getName().split("\\.").length == 2){
												subKey = element.getName().split("\\.")[1];
												field = arrCD.getField(subKey);
												
												logger.debug(field);
												if (field != null) {
													map.put(element.getName(), field.getValue());
												} else {
													logger.debug("field" + element.getName() + "is not exist");
												}
											}
										}
									}
									innerList.add(map);
									header.put(parentKey, innerList);
								}
								list.add(header);
								index = index + 1;
								arrCD = cd.getArray(parentKey).getStruct(index);
							} while (arrCD != null && arrCD.isStruct());
						}
						
					}else{
						do {
							if (arrCD != null && arrCD.isStruct()) {
								header = new HashMap();
								String subKey = "";
								for (int i = 0; i < elements.size(); i++) {
									element = (MessageElement) elements.get(i);
									if (element.getName().contains(".")) {
										if(element.getName().split("\\.").length == 2){
											subKey = element.getName().split("\\.")[1];
											field = arrCD.getField(subKey);
										}
										logger.debug(field);
										if (field != null) {
											header.put(element.getName(), field.getValue());
										} else {
											logger.debug("field" + element.getName() + "is not exist");
										}
									}
								}
								list.add(header);
							}
							index = index + 1;
							arrCD = cd.getArray(parentKey).getStruct(index);
						} while (arrCD != null && arrCD.isStruct());
					}
					
					
				}
			}else{
				for (int k = 0; k < parentKeyList.size(); k++) {
					String parentKey = parentKeyList.get(k);
					
					// 2.根据parentKey获取Struct，因获取不到Struct的数量，所以只能先获取再判断
					// 结构是CompositeData[Struct[Field]]
					if (cd.getArray(parentKey) != null) {
						int index = 0;
						
						Field field = new Field(null);
						Map header = null;//
						CompositeData arrCD = cd.getArray(parentKey).getStruct(index);//外围数组
						CompositeData arrCD2 = new CompositeData();
						
						if(flag){
							do {
								Map<String , List> innerMap = new HashMap<String, List>();//存放内置数组的值
								header = new HashMap();
								
								for (Map.Entry<String, String> entry : innerNameMap.entrySet()) {
									String key = entry.getKey();
									String value = entry.getValue();
									
									if(!key.contains(parentKey)){
										continue;
									}
									
									int index2 = 0;//计算内部数组
									List innerList = new ArrayList();//存放内置数组的值
									
									arrCD2 = arrCD.getArray(value).getStruct(index2);//内部数组
									do {
										if (arrCD2 != null && arrCD2.isStruct()) {
											Map map = new HashMap();
											String subKey = "";
											for (int i = 0; i < elements.size(); i++) {
												element = (MessageElement) elements.get(i);
												if (element.getName().contains(".")) {
													if(element.getName().split("\\.").length ==3){
														subKey = element.getName().split("\\.")[2];
														if(arrCD2 != null){
															field = arrCD2.getField(subKey);
														}
													}
													logger.debug(field);
													if (field != null && field.getValue() != null) {
														map.put(element.getName(), field.getValue());
													} else {
														logger.debug("field" + element.getName() + "is not exist");
													}
												}
											}
											innerList.add(map);
										}
										index2 = index2 + 1;
										arrCD2 = arrCD.getArray(value).getStruct(index2);
									} while (arrCD2 != null && arrCD2.isStruct());
									
									header.put(value, innerList);
								}
//								list.add(header);
								List innerList = new ArrayList();
								
								field = new Field(null);
								
								if (arrCD != null && arrCD.isStruct()) {
//								header = new HashMap();
									Map map = new HashMap();
									String subKey = "";
									for (int i = 0; i < elements.size(); i++) {
										element = (MessageElement) elements.get(i);
										if (element.getName().contains(".") && element.getName().contains(parentKey)) {
											if(element.getName().split("\\.").length == 2){
												subKey = element.getName().split("\\.")[1];
												field = arrCD.getField(subKey);
												
												logger.debug(field);
												if (field != null && field.getValue() != null) {
													map.put(element.getName(), field.getValue());
												} else {
													logger.debug("field" + element.getName() + "is not exist");
												}
											}
										}
									}
									innerList.add(map);
									header.put(parentKey, innerList);
									list.add(header);
								}
								index = index + 1;
								arrCD = cd.getArray(parentKey).getStruct(index);
							} while (arrCD != null && arrCD.isStruct());
							
						}else{
							List innerList = new ArrayList();//存放内置数组的值
							do {
								header = new HashMap();
								
								if (arrCD != null && arrCD.isStruct()) {
									Map map = new HashMap();
									String subKey = "";
									for (int i = 0; i < elements.size(); i++) {
										element = (MessageElement) elements.get(i);
										if (element.getName().contains(".")) {
											if(element.getName().split("\\.").length == 2){
												subKey = element.getName().split("\\.")[1];
												field = arrCD.getField(subKey);
											}
											logger.debug(field);
											if (field != null) {
												map.put(element.getName(), field.getValue());
											} else {
												logger.debug("field" + element.getName() + "is not exist");
											}
										}
									}
									innerList.add(map);
								}
								index = index + 1;
								arrCD = cd.getArray(parentKey).getStruct(index);
							} while (arrCD != null && arrCD.isStruct());
							header.put(parentKey, innerList);
							
							list.add(header);
							
						}
						
						
					}
				}
			}
			
			
		}
	}

	
	public static ReturnMessageNew decodeMessageFile(String code, String splictCode, String fileName, boolean isClient)
			throws Exception {
		logger.debug("解码文件:" + fileName);
		ReturnMessageNew request = new ReturnMessageNew();

		String templateFileName = isClient ? (code + Constants.TEMPLATE_SUFFIX) : code;
		MessageTemplateNew template = MessageTemplateBuilder.getMessageTemplateNew(templateFileName);

		unbuildFile(fileName, splictCode, template.getDetailItems(), request.getDetails());
		return request;
	}

	private static void unbuildFile(String fileName, String splictCode, List elements, List details) throws Exception {
		BufferedReader br = null;
		try {
			String encoding = "";//编码
			if(fileName.contains("CORE") || fileName.contains("TELLER") || fileName.contains("CMS")){//核心编码为：GBK
				encoding = Constants.ENCODING_GBK;
			}else{//其他系统的文件编码为 UTF-8
				encoding = Constants.ENCODING;
			}
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), encoding));
			
			//文件下载  去除掉文件中包含数组的字段
			List elementList = new ArrayList();
			for (Object obj : elements){
				MessageElement element = (MessageElement) obj;
				String key = element.getName();
				if (!key.contains(".")) {
					elementList .add(element);
				}
			}
			
			
			String str = null;
			Map header = null; // 每行MAP
			String[] msgVal = null; // 每行去掉分隔符后的值（数组）
			MessageElement element = null;
			while ((str = (br.readLine())) != null) {
				header = new HashMap();
				msgVal = str.split("\\|", elementList.size());
				for (int i = 0; i < elementList.size(); i++) {
					element = (MessageElement) elementList.get(i);
					logger.debug(msgVal[i]);
					
					if (msgVal[i] != null) {
						if(i == elementList.size()-1){
							String string = msgVal[elementList.size()-1];
							if(!string.equals("") && string.contains("|")){
								header.put(element.getName(), string.substring(0,string.length()-1));
								continue;
							}
						}
						header.put(element.getName(), msgVal[i]);
					} else {
						logger.debug("field" + element.getName() + "is not exist");
					}
				}
				// 将每行解析的值放到MAP
				details.add(header);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			throw new Exception("解析报文文件异常，异常[" + ex.getMessage() + "]");
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(),ex);
			}

		}
	}

	/**
	 * 编码客户端发送的请求信息,针对新报文
	 * 
	 * @param code     交易码
	 * @param response 返回报文对象
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeClientMessageNew(String code, ReturnMessageNew response) throws Exception {
		return encodeMessageNew(code, response, false);
	}

	public static byte[] encodeServerMessageNew(String code, ReturnMessageNew response) throws Exception {
		return encodeMessageNew(code, response, true);
	}

	public static byte[] encodeMessageNew(String code, ReturnMessageNew response, boolean isServer) throws Exception {
		logger.debug("encodeMessageNew组装报文开始");
		String templateFileName = isServer ? (code + Constants.TEMPLATE_SUFFIX) : code;
		MessageTemplateNew msgTemplate = MessageTemplateBuilder.getMessageTemplateNew(templateFileName);
		// 构建SysHeader
		CompositeData sysHead = new CompositeData();
		buildCompositeData(sysHead, msgTemplate.getSysHeaderItems(), response.getSysHead());

		// 构建AppHeader
		CompositeData appHead = new CompositeData();
		buildCompositeData(appHead, msgTemplate.getAppHeaderItems(), response.getAppHead());

		// 构建LocalHeader
		CompositeData localHead = new CompositeData();
		buildCompositeData(localHead, msgTemplate.getLocalHeaderItems(), response.getLocalHead());

		// 构建FileHeader
		CompositeData fileHead = new CompositeData();
		buildCompositeData(fileHead, msgTemplate.getFileHeaderItems(), response.getFileHead());

		// 构建报文内容
		CompositeData body = new CompositeData();
		buildCompositeData(body, msgTemplate.getBodyItems(), response.getBody());

		// 构建明细数组
		if(response.getFileHead() == null || response.getFileHead().size() == 0) {
			if(code.equals("30600071") || code.equals("CP2022040502")){
				buildCompositeDataDetail2(body, msgTemplate.getDetailItems(), response.getDetails());
			}else if(code.equals("CP2022040501")){
				buildCompositeDataDetail3(body, msgTemplate.getDetailItems(), response.getDetails());
			}else{
				buildCompositeDataDetail(body, msgTemplate.getDetailItems(), response.getDetails());
			}
		}

		CompositeData cd = new CompositeData();
		cd.addStruct("SYS_HEAD", sysHead);
		cd.addStruct("APP_HEAD", appHead);
		cd.addStruct("LOCAL_HEAD", localHead);
		cd.addStruct("FILE_HEAD", fileHead);
		cd.addStruct("BODY", body);

		byte[] message = PackUtil.pack(cd);
		System.out.println("组装报文的长度："+message.length+"============================================");
		logger.debug("encodeMessageNew组装报文结束");
		return message;
//		return new String(message, Constants.ENCODING);
	}

	/**
	 * 组装SysHeader
	 * <p>
	 * 方法名称: buildSysHeader|描述:
	 * </p>
	 * 
	 * @param sysHeadbeisDataObject 系统报文头
	 * @param template              报文模板
	 * @param sysHeader             系统报文头赋值MAP
	 */
	private static void buildCompositeData(CompositeData cd, List elements, Map header) throws Exception {
		TypeConverter converter = null;
		Field field = null;
		CompositeData arrCD = null;
		if (elements != null && elements.size() > 0) {
			for (int i = 0; i < elements.size(); i++) {
				MessageElement element = (MessageElement) elements.get(i);
				String key = element.getName();
				String parentKey = "";
				String subKey = "";
				if (key.contains(".")) {
					parentKey = key.split("\\.")[0];
					subKey = key.split("\\.")[1];
					// 内置的Struct
					arrCD = new CompositeData();
					// 数据类型转换
					converter = (TypeConverter) typeConverters.get(element.getDataType());
					String value = converter.toString(header, element);
					field = new Field(new FieldAttr(FieldType.FIELD_STRING, value.length(), 0));
					field.setValue(value);
					// 结构是CompositeData[Array[Struct[Field]]]
					if (cd.getArray(parentKey) == null || cd.getArray(parentKey).getStruct(0) == null) {
						Array array = new Array();
						arrCD.addField(subKey, field);
						array.addStruct(arrCD);
						cd.addArray(parentKey, array);
					} else {
						cd.getArray(parentKey).getStruct(0).addField(subKey, field);
					}
				} else {
					converter = (TypeConverter) typeConverters.get(element.getDataType());
					String value;
					try {
						if (key.equals("FILE_PATH")) {
							if(header != null && header.get("FILE_PATH") != null){
								value = (String) header.get("FILE_PATH");
							}else {
								value = "";
							}
						} else if (key.equals("CONSUMER_SEQ_NO") || key.equals("SERV_SEQ_NO")) {
							value = DateUtils.dtuGetCurDatTimStr().substring(8, 12)
									+ (1000 + (int) (Math.random() * 9000)) + "";
						} else if (key.equals("TRAN_DATE")) {
							value = DateUtils.dtuGetCurDatTimStr().substring(0, 8);
						} else if (key.equals("TRAN_TIMESTAMP")) {
							value = DateUtils.dtuGetCurDatTimStr().substring(8, 14);
						} else {
							value = converter.toString(header, element);
						}
						field = new Field(new FieldAttr(FieldType.FIELD_STRING, value.length(), 0));
						field.setValue(value);
						cd.addField(key, field);
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
		}
	}

	// 组装明细数据(普通组装)
	private static void buildCompositeDataDetail(CompositeData cd, List elements, List objs) throws Exception {
		TypeConverter converter = null;
		CompositeData arrCD = null;
		Array array = new Array();
		for (int i = 0; i < objs.size(); i++) {
			if (elements != null && elements.size() > 0) {
				Field field = null;
				// 内置的Struct
				arrCD = new CompositeData();
				String parentKey = "";
				for (int j = 0; j < elements.size(); j++) {
					MessageElement element = (MessageElement) elements.get(j);
					Map header = (Map) objs.get(i);
					String key = element.getName();
					String subKey = "";
					if (key.contains(".")) {
						parentKey = key.split("\\.")[0];
						subKey = key.split("\\.")[1];
						// 数据类型转换
						converter = (TypeConverter) typeConverters.get(element.getDataType());
						String value = converter.toString(header, element);
						field = new Field(new FieldAttr(FieldType.FIELD_STRING, value.length(), 0));
						field.setValue(value);
						// 结构是CompositeData[Array[Struct[Field]]]
						arrCD.addField(subKey, field);
					}
				}
				array.addStruct(arrCD);
				cd.addArray(parentKey, array);
			}
		}
	}

	// 组装明细数据(电票 申请类报文组装及中台额度审价模式二)
	private static void buildCompositeDataDetail2(CompositeData cd, List elements, List objs) throws Exception {
		TypeConverter converter = null;
		CompositeData arrCD = new CompositeData();
		CompositeData arrCD2 = null;
		Array array = new Array();//外部数组
		Array array2 = new Array();//内部数组
		
		String parentKey = "";//外部数组名
		String parentKey2 = "";//内部数组名
		for (int i = 0; i < objs.size(); i++) {
			if (elements != null && elements.size() > 0) {
				Field field = null;
				// 内置的Struct
				arrCD2 = new CompositeData();
//				arrCD = new CompositeData();
//				String parentKey = "";
				for (int j = 0; j < elements.size(); j++) {
					MessageElement element = (MessageElement) elements.get(j);
					Map header = (Map) objs.get(i);
					String key = element.getName();
					String subKey = "";
					
					/**
					 * 根据传递过来的数组组装map里面的list
					 */
					if(header.containsKey(key)){
						if (key.contains(".")) {
							if(key.split("\\.").length == 2){
								parentKey = key.split("\\.")[0];
								subKey = key.split("\\.")[1];
								// 数据类型转换
								converter = (TypeConverter) typeConverters.get(element.getDataType());
								String value = converter.toString(header, element);
								field = new Field(new FieldAttr(FieldType.FIELD_STRING, value.length(), 0));
								field.setValue(value);
								// 结构是CompositeData[Array[Struct[Field]]]
								arrCD.addField(subKey, field);
							}
							if(key.split("\\.").length == 3){
								parentKey = key.split("\\.")[0];
								parentKey2 = key.split("\\.")[1];
								subKey = key.split("\\.")[2];
								// 数据类型转换
								converter = (TypeConverter) typeConverters.get(element.getDataType());
								String value = converter.toString(header, element);
								field = new Field(new FieldAttr(FieldType.FIELD_STRING, value.length(), 0));
								field.setValue(value);
								// 结构是CompositeData[Array[Struct[Field]]]
								arrCD2.addField(subKey, field);
								
							}
							
						}
						
					}
				}
				if(StringUtils.isNotEmpty(parentKey2)){
					array2.addStruct(arrCD2);
					arrCD.addArray(parentKey2, array2);
				}
//				array.addStruct(arrCD);
//				cd.addArray(parentKey, array);
			}
		}
		if(StringUtils.isNotEmpty(parentKey)){
			array.addStruct(arrCD);
			cd.addArray(parentKey, array);
		}
		
		
	}

	// 组装明细数据(中台额度审价模式一)
	private static void buildCompositeDataDetail3(CompositeData cd, List elements, List objs) throws Exception {
		TypeConverter converter = null;
		CompositeData arrCD = null;
		CompositeData arrCD2 = null;
		Array array = new Array();//外部数组
		
		String parentKey = "";//外部数组名
		String parentKey2 = "";//内部数组名
		for (int i = 0; i < objs.size(); i++) {
			arrCD = new CompositeData();
			Array array2 = new Array();//内部数组
			
			if (elements != null && elements.size() > 0) {
				Field field = null;
				// 内置的Struct
				arrCD2 = new CompositeData();
//					arrCD = new CompositeData();
//					String parentKey = "";
				for (int j = 0; j < elements.size(); j++) {
					MessageElement element = (MessageElement) elements.get(j);
					Map header = (Map) objs.get(i);
					String key = element.getName();
					String subKey = "";
					
					/**
					 * 根据传递过来的数组组装map里面的list
					 */
					if(header.containsKey(key)){
						if (key.contains(".")) {
							if(key.split("\\.").length == 2){
								parentKey = key.split("\\.")[0];
								subKey = key.split("\\.")[1];
								// 数据类型转换
								converter = (TypeConverter) typeConverters.get(element.getDataType());
								String value = converter.toString(header, element);
								field = new Field(new FieldAttr(FieldType.FIELD_STRING, value.length(), 0));
								field.setValue(value);
								// 结构是CompositeData[Array[Struct[Field]]]
								arrCD.addField(subKey, field);
							}
							if(key.split("\\.").length == 3){
								parentKey = key.split("\\.")[0];
								parentKey2 = key.split("\\.")[1];
								subKey = key.split("\\.")[2];
								// 数据类型转换
								converter = (TypeConverter) typeConverters.get(element.getDataType());
								String value = converter.toString(header, element);
								field = new Field(new FieldAttr(FieldType.FIELD_STRING, value.length(), 0));
								field.setValue(value);
								// 结构是CompositeData[Array[Struct[Field]]]
								arrCD2.addField(subKey, field);
								
							}
							
						}
						
					}
				}
				
//					array.addStruct(arrCD);
//					cd.addArray(parentKey, array);
			}
			if(StringUtils.isNotEmpty(parentKey)){
				array.addStruct(arrCD);
				cd.addArray(parentKey, array);
			}
			if(StringUtils.isNotEmpty(parentKey2)){
				array2.addStruct(arrCD2);
				arrCD.addArray(parentKey2, array2);
			}
		}
		
		
		
		
	}

		
	public static String encodeMessageFile(String code, ReturnMessageNew response, boolean isServer) throws Exception {
		String fileName = null;
		logger.debug("解码文件:" + fileName);

		String templateFileName = isServer ? (code + Constants.TEMPLATE_SUFFIX) : code;
		MessageTemplateNew template = MessageTemplateBuilder.getMessageTemplateNew(templateFileName);

		fileName = buildFile(response, template.getDetailItems(), response.getDetails());

		return fileName;
	}

	private static String buildFile(ReturnMessageNew response, List elements, List details) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (details == null || details.size() == 0) {
			return "";
		} else {
			
			//文件下载  去除掉文件中包含数组的字段
			List elementList = new ArrayList();
			for (Object obj : elements){
				MessageElement element = (MessageElement) obj;
				String key = element.getName();
				if (!key.contains(".")) {
					elementList .add(element);
				}
			}
			//去除带数组的集合
			List tempList = new ArrayList();
			for (int i = 0; i < details.size(); i++) {
				Map<String,Object> tempMap = (Map) details.get(i);
				for (String str : tempMap.keySet()) {
					if(!str.contains(".")){
						tempList.add(tempMap);
						break;
					}
				}
			}
			
			TypeConverter converter = null;
			if(response.getCodeSign() != null && response.getCodeSign().equals("1")){//核心上传文件,分隔符送~|
				for (int i = 0; i < tempList.size(); i++) {
					if (elementList != null && elementList.size() > 0) {
						Map tempMap = (Map) tempList.get(i);
						tempMap.keySet();
						for (int j = 0; j < elementList.size(); j++) {
							MessageElement element = (MessageElement) elementList.get(j);
							// 明细MAP里包含模板定义的
							if (tempMap.containsKey(element.getName())) {
								converter = (TypeConverter) typeConverters.get(element.getDataType());
								sb.append(converter.toString(tempMap, element));
								sb.append("~"+HKBConstants.FTP_SPILCT_CODE);
							}else {
								sb.append("~"+HKBConstants.FTP_SPILCT_CODE);
							}
						}
					}
					sb.append("\r\n");
				}
			}else {
				for (int i = 0; i < tempList.size(); i++) {
					if (elementList != null && elementList.size() > 0) {
						Map tempMap = (Map) tempList.get(i);
						for (int j = 0; j < elementList.size(); j++) {
							MessageElement element = (MessageElement) elementList.get(j);
							// 明细MAP里包含模板定义的
							if (tempMap.containsKey(element.getName())) {
								converter = (TypeConverter) typeConverters.get(element.getDataType());
								sb.append(converter.toString(tempMap, element));
								sb.append(HKBConstants.FTP_SPILCT_CODE);
							}else {
								sb.append(HKBConstants.FTP_SPILCT_CODE);
							}
						}
					}
					sb.append("\r\n");
				}
			}
		}
		logger.debug("文件内容:" + sb.toString());
		BufferedWriter bw = null;
		String fileName = "";
		String localName = "";
		try {
			if(response.getFileHead().get("FILE_PATH") != null && StringUtil.isNotEmpty((String)response.getFileHead().get("FILE_PATH"))){
				fileName = (String) response.getFileHead().get("FILE_PATH");
				localName = "/home/bps/file/upFile"+fileName;
			}
			logger.debug("文件名:" + localName);
			bw = new BufferedWriter(new FileWriter(localName));
			bw.write(sb.toString());
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			throw new Exception("生成报文文件异常，异常[" + ex.getMessage() + "]");
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (final Exception ex) {
				logger.error(ex.getMessage(),ex);
			}
			// 返回发哦
			response.getFileHead().put(HKBConstants.FTP_FILE_PATH, fileName);
			response.getFileHead().put(HKBConstants.FTP_DELIMITOR, "|");
			return localName;
		}
	}
}
