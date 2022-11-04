package com.mingtech.application.audit.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mingtech.application.cache.ProductTypeCache;
import com.mingtech.application.ecds.common.DictionaryCache;

/**
 *审批受理参数对象定义
 */
public class ApproveAuditBean {
	private String id;//审批受理id
	private String auditType;//审批类型-01 批次审批 、02清单审批、03通用业务审批
	private String busiType;//业务类型-参照常量定义类PublicStaticDefine.AUDIT_BUSI_TYPE_
	private String busiId;//原业务ID
	private String productId;//产品编码-例如：1001买断式贴现
	 private String applyNo;//业务申请号(可以为批次号、票据明细、自定义编号)
	private String custName;//客户(交易对手)名称
	private String custCertNo;//客户(交易对手)证件号码
	private String custBankNm;//客户(交易对手)开户行名称
	private BigDecimal auditAmt;//审批金额
	private String billType;//票据类型
	private String billMedia;//票据介质
	private int totalNum;//总笔数
	private String auditStatus;//审批状态：1提交审批、2审批中、3驳回、4审批通过、0终止(系统管理员可以直接终止)
	private String auditStatusDes;//状态描述
	private String innerBranchId;
	
	private String nextAuditNodeId;//下一审批路节点ID
	private List nextAuditUserList;//下一岗审批用户信息
	private String taskName;//首页代办任务名称
	private String openUserNm;//业务经办人
	private Date openDate;//启动日期
	
	private String approverUserId;  //已选择的审批人id
	
	
	
	public Date getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	public String getOpenUserNm() {
		return openUserNm;
	}
	public void setOpenUserNm(String openUserNm) {
		this.openUserNm = openUserNm;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAuditType() {
		return auditType;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public String getBusiId() {
		return busiId;
	}
	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getApplyNo() {
		return applyNo;
	}
	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustCertNo() {
		return custCertNo;
	}
	public void setCustCertNo(String custCertNo) {
		this.custCertNo = custCertNo;
	}
	public String getCustBankNm() {
		return custBankNm;
	}
	public void setCustBankNm(String custBankNm) {
		this.custBankNm = custBankNm;
	}
	public BigDecimal getAuditAmt() {
		return auditAmt;
	}
	public void setAuditAmt(BigDecimal auditAmt) {
		this.auditAmt = auditAmt;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getBillMedia() {
		return billMedia;
	}
	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public String getNextAuditNodeId() {
		return nextAuditNodeId;
	}
	public void setNextAuditNodeId(String nextAuditNodeId) {
		this.nextAuditNodeId = nextAuditNodeId;
	}
	public List getNextAuditUserList() {
		return nextAuditUserList;
	}
	public void setNextAuditUserList(List nextAuditUserList) {
		this.nextAuditUserList = nextAuditUserList;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	
	public String getAuditStatusDes() {
			auditStatusDes = DictionaryCache.getAuditStatusMap(this.getAuditStatus());
		return auditStatusDes;
	}
	public String getTaskName() {
			taskName = ProductTypeCache.getProductTaskNm(this.getProductId());
		return taskName;
	}
	public String getInnerBranchId() {
		return innerBranchId;
	}
	public void setInnerBranchId(String innerBranchId) {
		this.innerBranchId = innerBranchId;
	}
	public String getApproverUserId() {
		return approverUserId;
	}
	public void setApproverUserId(String approverUserId) {
		this.approverUserId = approverUserId;
	}
	
}
