package com.mingtech.application.audit.domain;

public class RouteBrchProd {
	private String id;//ID
	private String routeId;//审核路线ID
	private String brchId;//机构ID
	private String prodId;//产品ID
	
	public String getBrchId() {
		return brchId;
	}
	public void setBrchId(String brchId) {
		this.brchId = brchId;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
