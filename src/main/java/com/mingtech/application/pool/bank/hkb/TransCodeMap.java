/**
 * 
 */
package com.mingtech.application.pool.bank.hkb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Node;

import com.mingtech.application.pool.bank.codec.config.ConfigReader;
import com.mingtech.application.pool.bank.codec.config.MessageElement;
import com.mingtech.application.pool.bank.codec.config.MessageTemplateBuilder;

/**
 * @author yecheng 类描述：读取配置文件，根据接口编号查找对应的ESB服务码
 */
public class TransCodeMap {

	private Map<String, String> codeMap = new HashMap<String, String>();
	private Map<String, String> textMap = new HashMap<String, String>();
	private Map<String, String> nameMap = new HashMap<String, String>();

	private TransCodeMap() {

	}

	/**
	 * 读取配置文件中的交易编号，查找对应服务码
	 * 
	 * @return map
	 * @throws DocumentException
	 */
	public static TransCodeMap templateMap() throws DocumentException {

		ConfigReader cReader = new ConfigReader(MessageTemplateBuilder.class
				.getClassLoader().getResourceAsStream(
						"cominterface/TransCodeMap.xml"));
		List list = cReader.getNodeList("/head/code");
		TransCodeMap CreditCodeMap = new TransCodeMap();

		Node node = null;
		MessageElement me;
		for (int i = 0; i < list.size(); i++) {
			node = (Node) list.get(i);
			me = new MessageElement();
			me.setName(node.valueOf("@name"));
			me.setValue(node.valueOf("@value"));
			me.setDescription(node.valueOf("@text"));
			CreditCodeMap.getCodeMap().put(me.getName(), me.getValue());// 服务码对应
			CreditCodeMap.getTextMap().put(me.getName(), me.getDescription());// 名称对应
		}

		return CreditCodeMap;

	}

	/**
	 * 读取配置文件中的交易编号，查找对应服务码
	 * 
	 * @return map
	 * @throws DocumentException
	 */
	public static TransCodeMap templateMapNew() throws DocumentException {

		ConfigReader cReader = new ConfigReader(MessageTemplateBuilder.class
				.getClassLoader().getResourceAsStream(
						"pjcinterface/TransCodeMap.xml"));
		List list = cReader.getNodeList("/head/code");
		TransCodeMap CreditCodeMap = new TransCodeMap();

		Node node = null;
		MessageElement me;
		for (int i = 0; i < list.size(); i++) {
			node = (Node) list.get(i);
			me = new MessageElement();
			me.setName(node.valueOf("@name"));
			me.setValue(node.valueOf("@value"));
			me.setDescription(node.valueOf("@text"));
			CreditCodeMap.getCodeMap().put(me.getName(), me.getValue());// 服务码对应
			CreditCodeMap.getTextMap().put(me.getName(), me.getDescription());// 名称对应
			CreditCodeMap.getNameMap().put(me.getValue(), me.getDescription());// 内部交易码名称对应
		}

		return CreditCodeMap;

	}

	public Map<String, String> getCodeMap() {
		return codeMap;
	}

	public void setCodeMap(Map<String, String> codeMap) {
		this.codeMap = codeMap;
	}

	public Map<String, String> getTextMap() {
		return textMap;
	}

	public void setTextMap(Map<String, String> textMap) {
		this.textMap = textMap;
	}

	public Map<String, String> getNameMap() {
		return nameMap;
	}

	public void setNameMap(Map<String, String> nameMap) {
		this.nameMap = nameMap;
	}

	
}
