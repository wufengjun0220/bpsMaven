package com.mingtech.application.report.domain;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-18 下午02:04:12
 * @描述: [PropertyObject]属性对象
 */
public class PropertyObject{

	private String propertyName; // 属性名称
	private String propertyId; // 属性ID
	private String propertyValue; // 属性值
	private String propertyValueType; // 属性值类型      数值型，金额型，字符串型
	
	
	public PropertyObject(){
		
	}


	
	/**
	* <p>方法名称: getPropertyName|描述: 属性名称</p>
	* @return
	*/
	public String getPropertyName(){
		return propertyName;
	}
	/**
	* <p>方法名称: setPropertyName|描述: 属性名称</p>
	* @param propertyName
	*/
	public void setPropertyName(String propertyName){
		this.propertyName = propertyName;
	}


	
	/**
	* <p>方法名称: getPropertyId|描述: 属性ID</p>
	* @return
	*/
	public String getPropertyId(){
		return propertyId;
	}
	/**
	* <p>方法名称: setPropertyId|描述: 属性ID</p>
	* @param propertyId
	*/
	public void setPropertyId(String propertyId){
		this.propertyId = propertyId;
	}


	/**
	* <p>方法名称: getPropertyValue|描述: 属性值</p>
	* @return
	*/
	public String getPropertyValue(){
		return propertyValue;
	}
	/**
	* <p>方法名称: setPropertyValue|描述: 属性值</p>
	* @param propertyValue
	*/
	public void setPropertyValue(String propertyValue){
		this.propertyValue = propertyValue;
	}


	/**
	* <p>方法名称: getPropertyValueType|描述: 属性值类型</p>
	* @return
	*/
	public String getPropertyValueType(){
		return propertyValueType;
	}
	/**
	* <p>方法名称: setPropertyValueType|描述: 属性值类型</p>
	* @param propertyValueType
	*/
	public void setPropertyValueType(String propertyValueType){
		this.propertyValueType = propertyValueType;
	}
	
	
}
