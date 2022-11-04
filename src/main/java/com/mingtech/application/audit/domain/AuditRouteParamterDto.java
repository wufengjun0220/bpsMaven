package com.mingtech.application.audit.domain;

import com.mingtech.application.ecds.common.DictionaryCache;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: h2
* @日期: 2009-7-2 下午02:25:05
* @描述: [AuditRouteParamterDto]审批路线请求参数配置表
*/
public class AuditRouteParamterDto implements java.io.Serializable{

	private String id;//主键
	private String routeId;//审批路线ID
	private String paramName;//参数中文名称
	private String paramCode;//参数字段名称-不区分大小写
	private String dataType;//数据类型-文本String、整数int、金额BigDecimal
	
	/**以下字段只做参数传递或页面显示**/
	private String dataTypeDesc;//
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamCode() {
		return paramCode;
	}
	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataTypeDesc() {
		dataTypeDesc = DictionaryCache.getAuditParamDataTypeMap(dataType);
		return dataTypeDesc;
	}
}