package com.mingtech.application.ecds.common.query.domain;

/**
 * FieldCodeMap entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class FieldCodeMapDto implements java.io.Serializable {

	// Fields

	private String id;
	private String tableName;
	private String fieldName;
	private String fieldCnName;
	private String handleTypeId;
	private String relateData;
	private String fieldOrder;

	// Constructors

	
	public String getFieldOrder(){
		return fieldOrder;
	}

	
	public void setFieldOrder(String fieldOrder){
		this.fieldOrder = fieldOrder;
	}

	/** default constructor */
	public FieldCodeMapDto() {
	}

	/** full constructor */
	public FieldCodeMapDto(String tableName, String fieldName, String fieldCnName,
			String handleTypeId, String relateData,String fieldOrder) {
		this.tableName = tableName;
		this.fieldName = fieldName;
		this.fieldCnName = fieldCnName;
		this.handleTypeId = handleTypeId;
		this.relateData = relateData;
		this.fieldOrder = fieldOrder;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldCnName() {
		return this.fieldCnName;
	}

	public void setFieldCnName(String fieldCnName) {
		this.fieldCnName = fieldCnName;
	}

	public String getHandleTypeId() {
		return this.handleTypeId;
	}

	public void setHandleTypeId(String handleTypeId) {
		this.handleTypeId = handleTypeId;
	}

	public String getRelateData() {
		return this.relateData;
	}

	public void setRelateData(String relateData) {
		this.relateData = relateData;
	}

}