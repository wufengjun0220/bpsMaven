package com.mingtech.application.runmanage.domain;


import com.mingtech.application.cache.ProductTypeCache;

/**
 * <p>说明:业务表字段配置表</p>
 * @author   ice
 * @Date	 2019-07-19
 * @hibernate.class table="BO_BUSI_TABLE_CONFIG"
 * @hibernate.cache usage="read-write"
 */
public class BusiTableConfig implements java.io.Serializable{
	
	private	String id;                  //主键ID
	private String productId;//产品id
	private String busiTableNm;//业务表中文名称
	private String busiTable;//业务表名
	private String busiStatusField;//状态字段名称
	private String busiIdField;//主键字段名称
	private String auditRunningStatus;//审批中状态
	private String auditPassStatus;//审批通过状态
	private String auditRefuseStatus;//审批不通过状态
	private String extendService;//扩展服务
	
	private String submitNode; //是否可选提交节点（true false）
	private String rejustNode; //是否可选驳回节点（true false）
	
	
	/*以下字段只做页面显示或参数传递*/
	private String productIdDes;//产品类型描述
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getBusiTableNm() {
		return busiTableNm;
	}
	public void setBusiTableNm(String busiTableNm) {
		this.busiTableNm = busiTableNm;
	}
	public String getBusiTable() {
		return busiTable;
	}
	public void setBusiTable(String busiTable) {
		this.busiTable = busiTable;
	}
	public String getBusiStatusField() {
		return busiStatusField;
	}
	public void setBusiStatusField(String busiStatusField) {
		this.busiStatusField = busiStatusField;
	}
	public String getBusiIdField() {
		return busiIdField;
	}
	public void setBusiIdField(String busiIdField) {
		this.busiIdField = busiIdField;
	}
	
	public String getProductIdDes() {
		productIdDes = ProductTypeCache.getProductName(this.getProductId());
		return productIdDes;
	}
	public String getAuditPassStatus() {
		return auditPassStatus;
	}
	public void setAuditPassStatus(String auditPassStatus) {
		this.auditPassStatus = auditPassStatus;
	}
	public String getAuditRefuseStatus() {
		return auditRefuseStatus;
	}
	public void setAuditRefuseStatus(String auditRefuseStatus) {
		this.auditRefuseStatus = auditRefuseStatus;
	}
	public String getAuditRunningStatus() {
		return auditRunningStatus;
	}
	public void setAuditRunningStatus(String auditRunningStatus) {
		this.auditRunningStatus = auditRunningStatus;
	}
	public String getExtendService() {
		return extendService;
	}
	public void setExtendService(String extendService) {
		this.extendService = extendService;
	}
	public String getSubmitNode() {
		return submitNode;
	}
	public void setSubmitNode(String submitNode) {
		this.submitNode = submitNode;
	}
	public String getRejustNode() {
		return rejustNode;
	}
	public void setRejustNode(String rejustNode) {
		this.rejustNode = rejustNode;
	}
	
	
}

