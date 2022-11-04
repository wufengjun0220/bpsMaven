package com.mingtech.application.common.domain;

import java.util.List;

/**
 * 通用数据字典
 * @author huangshiqiang
 * @date Dec 28, 2007
 * @comment 
 * @hibernate.class table="t_dictionary"
 * @hibernate.cache usage="read-write"
 */
public class Dictionary {
	
	private String id;             //主键
	private String code;           //字典编码
	private String name;           //字典全称
	private String shortName;      //字典简称
	private String parentCode;     //父节点
	private int level=0;//排序等级
	
	public static String CODESEPRATOR="_";   //code分割符
	public static int INUSE=1;               //可用
	public static int NOUSE=0;               //不可用
	public static int DICTIONARY_TYPE_COMM=0;//普通字典
	public static int DICTIONARY_TYPE_SYS=1;//系统字典
	
	private List children;
	public Dictionary(){
				
	}
	/**
	 * @hibernate.id generator-class="uuid.hex" type="string" column="id" length="50"
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @hibernate.property type="string" column="d_code" length="100"
	 */
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @hibernate.property type="string" column="d_codeName" length="50"
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @hibernate.property type="string" column="d_shortName" length="50"
	 */
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	/**
	 * @hibernate.property type="string" column="d_parentCode"  length="100"
	*/
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	
	public Dictionary copy(){
		Dictionary temp = new Dictionary();
		temp.setCode(this.getCode());
		temp.setName(this.getName());
		temp.setParentCode(this.getParentCode());
		temp.setShortName(this.getShortName());
		return temp;
	}
	
}
