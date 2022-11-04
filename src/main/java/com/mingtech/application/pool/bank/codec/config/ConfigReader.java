package com.mingtech.application.pool.bank.codec.config;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

/**
 * <p> * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司  * </p> 
 * @作者: chenwei
 * @日期: Jun 17, 2009 4:31:40 PM
 * @描述: [ConfigReader]对读取xml配置文件方法进行了简单的封装(基于dom4j)
 */
public class ConfigReader {
	
	private SAXReader saxReader = new SAXReader();
	private Document doc4j;
	
	/**
	  * 读取指定uri的配置文件
	  * @param uri 配置文件的uri
	  * @throws DocumentException 
	*/
	public ConfigReader(String uri) throws DocumentException {
		doc4j = saxReader.read(uri);
	}
	
	/**
	  * 读取指定InputSource的配置文件
	  * @param InputSource 配置文件的InputSource
	  * @throws DocumentException
	*/
	public ConfigReader(InputStream is) throws DocumentException {
		doc4j = saxReader.read(is);
	}

	/**
	  * 读取指定InputSource的配置文件
	  * @param InputSource 配置文件的InputSource
	  * @throws DocumentException
	*/
	public ConfigReader(InputSource is) throws DocumentException {
		doc4j = saxReader.read(is);
	}
	/**
	  * 读取本地文件系统中指定的配置文件
	  * @param file 文件对象
	  * @throws DocumentException
	*/
	public ConfigReader(File file) throws DocumentException {
		doc4j = saxReader.read(file);
	}
	
	/**
	  * 获得配置文件中指定项目的值
	  * @param xpath xpath表达式
	  * @return 返回符合条件的string值
	  */
	 public String getStringValue(String xpath){
		Node node = (Node)doc4j.selectSingleNode(xpath);
		if(node==null) return "";
		return node.getStringValue();
	 }
	 
	 public String getStringValue(Node node,String xpath){
		 if(node==null) return "";
		 Node node1 = (Node)node.selectSingleNode(xpath);
		 if(node1==null) return "";
		 return node1.getStringValue();
	 }
	 
	 /**
	   * 获得配置文件中指定项目的值
	   * @param xpath xpath表达式
	   * @return 返回符合条件的boolean值
	 */
	 public boolean getBooleanValue(String xpath){
		 String v=getStringValue(xpath);
	   	 if(v.toUpperCase().equals("TRUE") || v.equals("1")){
	   		 return true;
	   	 }
	   	 return false;
	 }
	 public boolean getBooleanValue(Node node,String xpath){
	   	 String v = getStringValue(node,xpath);
	   	 if(v.toUpperCase().equals("TRUE") || v.equals("1")){
	   		 return true;
	   	 }
	   	 return false;
	 }
	 
	/**
	  * 获得配置文件中指定路径的nodelist(从根查找，或从某节点下范围)
	  * @param xpath xpath表达式
	  * @return 返回符合条件的node集合
	 */
	 public List getNodeList(String xpath){
		 return doc4j.selectNodes(xpath);
	 }
	 
	 public List getNodeList(Node node,String xpath){
		 return node.selectNodes(xpath);
	 }
	 
	 public Element getRootElement(){
		 return doc4j.getRootElement();
	 }

		//XK ADD 20140612
	 public List getElementList(String elName){
		 List list = doc4j.selectNodes(elName);
		 if(list.size() > 0){
			 Element el = (Element) doc4j.selectNodes(elName).get(0);
			 return el.elements();
		 }else{
			 return null;
		 }		 
	 }
}
