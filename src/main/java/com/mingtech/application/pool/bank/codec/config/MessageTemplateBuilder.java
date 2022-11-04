package com.mingtech.application.pool.bank.codec.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Node;

import com.mingtech.application.pool.bank.message.Constants;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: chenwei
 * @日期: Jun 16, 2009 5:11:29 PM
 * @描述: [MessageTemplateBuilder]把xml映射配置文件构建为MessageTemplate对象
 */
public class MessageTemplateBuilder {

	private static final Map templateMap = new HashMap();
	
	private static final Map templateMapNew = new HashMap();
	
	/**
	* <p>方法名称: buildMessageTemplate|描述: 构建报文配置文件对象</p>
	* @param tranCode  交易码  根据交易码定位xml配置文件名
	* @return MessageTemplate 报文模板对象
	*/
	private static MessageTemplate buildMessageTemplate(String tranCode) throws Exception  {
		ConfigReader cReader = new ConfigReader(MessageTemplateBuilder.class.getClassLoader().getResourceAsStream("pjcinterface/"+tranCode+".xml"));
		List list = cReader.getNodeList("/head/messageElement");
		Node node = null;
		MessageElement me;
		MessageTemplate mt = new MessageTemplate();
		mt.setMainItems(new ArrayList());
		mt.setDetailItems(new ArrayList());
		for (int i = 0; i < list.size(); i++) {
			node = (Node)list.get(i);
			me = new MessageElement();
			me.setName(node.valueOf("@name"));
			me.setPtName(node.valueOf("@ptName"));
			me.setLength(Integer.parseInt(node.valueOf("@length")));
			me.setDataType(node.valueOf("@dataType"));
			me.setFormat(node.valueOf("@format"));
			me.setMandatory(node.valueOf("@mandatory"));
			me.setDataSource(node.valueOf("@dataSource"));
			me.setValue(node.valueOf("@value"));
			me.setType(node.valueOf("@type"));
			me.setDescription(node.valueOf("@description"));
			if(me.getType().equalsIgnoreCase(Constants.MAIN_BODY)){
				mt.getMainItems().add(me);
			}else{
				mt.getDetailItems().add(me);
			}
		}
		return mt;
	}
	
	/**
	* <p>方法名称: getMessageTemplate|描述: 获取报文配置文件对象</p>
	* @param tranCode  交易码  根据交易码定位xml配置文件名
	* @return MessageTemplate 报文模板对象
	*/
	public static MessageTemplate getMessageTemplate(String tranCode) throws Exception {
		MessageTemplate mt = (MessageTemplate)templateMap.get(tranCode);
		if(mt == null){
			mt = buildMessageTemplate(tranCode);
			templateMap.put(tranCode, mt);
		}
		return mt;
	}
	
	/**
	* <p>方法名称: buildMessageTemplate|描述: 构建新报文的配置文件对象</p>
	* @param tranCode  交易码  根据交易码定位xml配置文件名
	*/
	private static MessageTemplateNew buildMessageTemplateNew(String tranCode) throws Exception  {
		ConfigReader cReader = new ConfigReader(MessageTemplateBuilder.class.getClassLoader().getResourceAsStream("pjcinterface/"+tranCode+".xml"));
		List list = cReader.getNodeList("/head/messageElement");
		Node node = null;
		MessageElement me;
		MessageTemplateNew mt = new MessageTemplateNew();

		mt.setSysHeaderItems(new ArrayList());
		mt.setAppHeaderItems(new ArrayList());
		mt.setLocalHeaderItems(new ArrayList());
		mt.setFileHeaderItems(new ArrayList());
		mt.setBodyItems(new ArrayList());
		mt.setDetailItems(new ArrayList());
		
		for (int i = 0; i < list.size(); i++) {
			node = (Node)list.get(i);
			me = new MessageElement();
			me.setName(node.valueOf("@name"));
			me.setLength(Integer.parseInt(node.valueOf("@length")));
			me.setDataType(node.valueOf("@dataType"));
			me.setFormat(node.valueOf("@format"));
			me.setMandatory(node.valueOf("@mandatory"));
			me.setDataSource(node.valueOf("@dataSource"));
			me.setValue(node.valueOf("@value"));
			me.setType(node.valueOf("@type"));
			me.setDescription(node.valueOf("@description"));
			if(me.getType().equalsIgnoreCase(Constants.SYS_HEADER)){
				mt.getSysHeaderItems().add(me);
			}else if(me.getType().equalsIgnoreCase(Constants.APP_HEADER)){
				mt.getAppHeaderItems().add(me);
			}else if(me.getType().equalsIgnoreCase(Constants.LOCAL_HEADER)){
				mt.getLocalHeaderItems().add(me);
			}else if(me.getType().equalsIgnoreCase(Constants.FILE_HEADER)){
				mt.getFileHeaderItems().add(me);
			}else if(me.getType().equalsIgnoreCase(Constants.MAIN_BODY)){
				mt.getBodyItems().add(me);
			}else{
				mt.getDetailItems().add(me);
			}
		}
		return mt;
	}
	
	/**
	* <p>方法名称: getMessageTemplateNew|描述: 获取报文配置文件对象(针对新报文接口)</p>
	* @param tranCode  交易码  根据交易码定位xml配置文件名
	* @return MessageTemplateNew 报文模板对象
	*/
	public static MessageTemplateNew getMessageTemplateNew(String tranCode) throws Exception {
		//2014-07-05 核心BEIS报文格式采用新的MAP
		MessageTemplateNew mt = (MessageTemplateNew)templateMapNew.get(tranCode);
		if(mt == null){
			mt = buildMessageTemplateNew(tranCode);
			templateMapNew.put(tranCode, mt);
		}
		return mt;
	}
}
