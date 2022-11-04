package com.mingtech.application.ecds.common.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;

public class ApproveAuditBean {
	private String id;
	//审批类型  01 批次审批  02清单审批
	private String auditType;
	//业务类型  1	贴现买入	2转贴现买入3转贴现卖出4再贴现卖出5再贴现买入			
	//6回购式贴现到期卖出        7纸票转贴现买入8纸票转贴现卖出9纸票贴现
	//10提示付款11追索12提示承兑
	//20000代保管20001票据池20002库存票据
	//以产品编码的  大类作为分类标准  
	private String busiType;
	//业务来源ID
	private String busiId;
	//产品编码
	private String productId;
	private String productName;
	
	//上一审批人列表
	private String lastAuditUser;
	private String lastAuditUserName;
	//上一审批路线配置的节点ID
	private String lastAuditNodeId;
	
	//当前审批人列表
	private String curAuditUser;
	private String curAuditUserName;
	//审批路线配置的节点ID
	private String curAuditNodeId;
	
	//显示 使用
	private String curNodeName;
	private String curNodeRouteName;
	private String curNodeNum;
	private String curNodeRoleName;
	
	
	//下一审批人列表
	private String nextAuditUser;
	//下一审批路线配置的节点ID
	private String nextAuditNodeId;
	
	//审批路线状态  00开启  01关闭
	private String openStatus;
	private String openStatusName;
	//启动日期
	private Date openDate;
	//关闭日期
	private Date endDate;
	
	//启动审批流人员ID
	private String openUserId;
	private String openUserName;
	//启动机构ID
	private String openBranchId;
	private String openBranchName;
	//审批金额
	private BigDecimal auditAmt;
	//业务批次号
	private String batchNo;
	//交易对手名称
	private String custName;
	//记录所有的审批人信息
	private String allUsers;
	//系统内机构号，卖出方提交 买入方审批是赋值；
	private String innerBranchId;
	
	
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
	public String getLastAuditUser() {
		return lastAuditUser;
	}
	public void setLastAuditUser(String lastAuditUser) {
		this.lastAuditUser = lastAuditUser;
	}
	public String getCurAuditUser() {
		return curAuditUser;
	}
	public void setCurAuditUser(String curAuditUser) {
		this.curAuditUser = curAuditUser;
	}
	public String getNextAuditUser() {
		return nextAuditUser;
	}
	public void setNextAuditUser(String nextAuditUser) {
		this.nextAuditUser = nextAuditUser;
	}
	public String getOpenStatus() {
		return openStatus;
	}
	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}
	public Date getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getOpenUserId() {
		return openUserId;
	}
	public void setOpenUserId(String openUserId) {
		this.openUserId = openUserId;
	}
	public String getOpenBranchId() {
		return openBranchId;
	}
	public void setOpenBranchId(String openBranchId) {
		this.openBranchId = openBranchId;
	}
	public BigDecimal getAuditAmt() {
		return auditAmt;
	}
	public void setAuditAmt(BigDecimal auditAmt) {
		this.auditAmt = auditAmt;
	}
	public String getLastAuditNodeId() {
		return lastAuditNodeId;
	}
	public void setLastAuditNodeId(String lastAuditNodeId) {
		this.lastAuditNodeId = lastAuditNodeId;
	}
	public String getCurAuditNodeId() {
		return curAuditNodeId;
	}
	public void setCurAuditNodeId(String curAuditNodeId) {
		this.curAuditNodeId = curAuditNodeId;
	}
	public String getNextAuditNodeId() {
		return nextAuditNodeId;
	}
	public void setNextAuditNodeId(String nextAuditNodeId) {
		this.nextAuditNodeId = nextAuditNodeId;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getAllUsers() {
		return allUsers;
	}
	public void setAllUsers(String allUsers) {
		this.allUsers = allUsers;
	}
	public String getInnerBranchId() {
		return innerBranchId;
	}
	public void setInnerBranchId(String innerBranchId) {
		this.innerBranchId = innerBranchId;
	}
	
	public String getProductName() {
		return this.productName = DictionaryCache.getProductName(this.getProductId());
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getLastAuditUserName() {
		return lastAuditUserName;
	}
	public void setLastAuditUserName(String lastAuditUserName) {
		this.lastAuditUserName = lastAuditUserName;
	}
	public String getCurAuditUserName() {
		return curAuditUserName;
	}
	public void setCurAuditUserName(String curAuditUserName) {
		this.curAuditUserName = curAuditUserName;
	}
	public String getOpenStatusName() {
		if("00".equals(this.getOpenStatus())){
			return "开启";
		}
		return "结束";
	}
	public void setOpenStatusName(String openStatusName) {
		this.openStatusName = openStatusName;
	}
	public String getOpenUserName() {
		return openUserName;
	}
	public void setOpenUserName(String openUserName) {
		this.openUserName = openUserName;
	}
	public String getOpenBranchName() {
		return openBranchName;
	}
	public void setOpenBranchName(String openBranchName) {
		this.openBranchName = openBranchName;
	}
	public String getCurNodeName() {
		return curNodeName;
	}
	public void setCurNodeName(String curNodeName) {
		this.curNodeName = curNodeName;
	}
	public String getCurNodeRouteName() {
		return curNodeRouteName;
	}
	public void setCurNodeRouteName(String curNodeRouteName) {
		this.curNodeRouteName = curNodeRouteName;
	}
	public String getCurNodeNum() {
		return curNodeNum;
	}
	public void setCurNodeNum(String curNodeNum) {
		this.curNodeNum = curNodeNum;
	}
	public String getCurNodeRoleName() {
		return curNodeRoleName;
	}
	public void setCurNodeRoleName(String curNodeRoleName) {
		this.curNodeRoleName = curNodeRoleName;
	}
	
}
