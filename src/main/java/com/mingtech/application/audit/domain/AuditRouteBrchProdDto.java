package com.mingtech.application.audit.domain;

public class AuditRouteBrchProdDto {
	
	private String routeId;
	private String routeName;
	private String brchName;
	private String prodName;
	
	public String getBrchName() {
		return brchName;
	}
	public void setBrchName(String brchName) {
		this.brchName = brchName;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public AuditRouteBrchProdDto(String routeId,String routeName, String brchName, String prodName) {
		this.routeId=routeId;
		this.routeName = routeName;
		this.brchName = brchName;
		this.prodName = prodName;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
}
