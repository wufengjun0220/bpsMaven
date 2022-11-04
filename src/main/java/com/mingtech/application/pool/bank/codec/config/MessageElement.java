package com.mingtech.application.pool.bank.codec.config;

/**
 * <p> * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司  * </p> 
 * @作者: guoyaofeng
 * @日期: Jun 17, 2009 4:31:40 PM
 * @描述: [MessageElement]报文模板数据项描述条目
 */
public class MessageElement {
	
	public String toString() {
		return "name="+this.getName() + ",ptName="+this.getPtName() + ",length="+this.getLength()
				+",dataSource="+this.getDataSource() + ",dataType="+this.getDataType() + ",description="+this.getDescription() 
				+ ",format="+this.getFormat() + ",value="+this.getValue() + ",mandatory="+this.getMandatory() + ",type="+this.getType();
	}

	private String name;	             //元素数据项名
	private int length;		             //元素长度
	private String dataType;	         //元素数据类型
	private String format;	             //数据类型的格式描述
	private String mandatory;	         //是否必须
	private String dataSource;	         //元素数据源
	private String value;	             //如数据源为xml则配置值
	private String type;	             //类型，主包报文、明细报文
	private String description;          //描述
	private String ptName;               //报文中的名称
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getMandatory() {
		return mandatory;
	}
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getPtName(){
		return ptName;
	}
	
	public void setPtName(String ptName){
		this.ptName = ptName;
	}

}
