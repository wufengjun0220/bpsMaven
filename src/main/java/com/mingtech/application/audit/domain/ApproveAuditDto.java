package com.mingtech.application.audit.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.cache.ProductTypeCache;
import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * 审批受理对象
 * @author ice
 *
 */
public class ApproveAuditDto {
	private String id;//主键
	private String auditType;//01 批次审批 、02清单审批、03通用业务审批
	/**业务类型  
	 * 取值范围：1	贴现买入	2转贴现买入3转贴现卖出4再贴现卖出5再贴现买入			
	 * 6回购式贴现到期卖出        7纸票转贴现买入8纸票转贴现卖出9纸票贴现
	 * 10提示付款11追索12提示承兑
	 * 20000代保管20001票据池20002库存票据
	 * 以产品编码的  大类作为分类标准  
	 **/
	private String busiType;
	private String busiId;//原业务ID
	private String productId;//产品编码-例如：1001买断式贴现
    private String applyNo;//业务申请号(可以为批次号、票据明细、自定义编号)
	private String custName;//客户(交易对手)名称
	private String custCertNo;//客户(交易对手)证件号码
	private String custBankNm;//客户(交易对手)开户行名称
	private BigDecimal auditAmt;//审批金额
	private String billType;//票据类型
	private String billMedia;//票据介质
	private int totalNum;//票据总笔数
	private Date openDate;//启动日期
	private String routeId;//审核路线ID
	private String openUserId;//启动审批流人员ID
	private String openUserNm;//启动用户名称
	private String openBranchId;//启动机构ID
	private String openBranchNm;//启动机构名称
	private String innerBranchId;//系统内机构号，卖出方提交 买入方审批是赋值,默认为-1
	private String auditStatus;//审批状态：SP_01提交审批、SP_02审批中、SP_03驳回、SP_04审批通过、SP_00终止(系统管理员可以直接终止)
	private String lastAuditNodeId;//上一审批节点ID
	private String lastAuditUser;//上一审批人账号
	private String lastAuditUserNm;//上一审批人名称
	private String lastAuditBranchId;//上一审批人所属机构ID
	private String lastAuditBranchNm;//上一审批人所属机构名称
	private String curAuditNodeId;//当前审批节点ID
	private String curAuditUser;//当前审批人账号
	private String curAuditUserNm;//当前审批人名称
	private String curAuditBranchId;//当前审批人所属机构ID
	private String curAuditBranchNm;//当前审批人所属机构名称
	private String nextAuditNodeId;//下一审批路节点ID
	private String nextAuditUser;//下一审批人账号
	private String nextAuditUserNm;//上一审批人名称
	private String nextAuditBranchId;//上一审批人所属机构ID
	private String nextAuditBranchNm;//上一审批人所属机构名称
	private String curRoleName;//当前执行角色名称
	private String allUsers;//记录所有的审批人信息
	private Date lastUpdate;//最后更新时间
	
	
	/*以下字段只做页面显示或参数传递*/
	private String applyNoDes;//加工后的申请编号
	private String auditTypeDes;//审批受理类型描述
	private String auditStatusDes;//状态描述
	private String busiTypeDes;//业务类型
	private String productIdDes;//产品类型描述
	private String billTypeDes;//票据类型描述
	private String billMediaDes;//票据介质描述
	
	
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
	public Date getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public String getOpenUserId() {
		return openUserId;
	}
	public void setOpenUserId(String openUserId) {
		this.openUserId = openUserId;
	}
	public String getOpenUserNm() {
		return openUserNm;
	}
	public void setOpenUserNm(String openUserNm) {
		this.openUserNm = openUserNm;
	}
	public String getOpenBranchId() {
		return openBranchId;
	}
	public void setOpenBranchId(String openBranchId) {
		this.openBranchId = openBranchId;
	}
	public String getOpenBranchNm() {
		return openBranchNm;
	}
	public void setOpenBranchNm(String openBranchNm) {
		this.openBranchNm = openBranchNm;
	}
	public String getInnerBranchId() {
		return innerBranchId;
	}
	public void setInnerBranchId(String innerBranchId) {
		this.innerBranchId = innerBranchId;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getLastAuditNodeId() {
		return lastAuditNodeId;
	}
	public void setLastAuditNodeId(String lastAuditNodeId) {
		this.lastAuditNodeId = lastAuditNodeId;
	}
	public String getLastAuditUser() {
		return lastAuditUser;
	}
	public void setLastAuditUser(String lastAuditUser) {
		this.lastAuditUser = lastAuditUser;
	}
	public String getLastAuditUserNm() {
		return lastAuditUserNm;
	}
	public void setLastAuditUserNm(String lastAuditUserNm) {
		this.lastAuditUserNm = lastAuditUserNm;
	}
	public String getLastAuditBranchId() {
		return lastAuditBranchId;
	}
	public void setLastAuditBranchId(String lastAuditBranchId) {
		this.lastAuditBranchId = lastAuditBranchId;
	}
	public String getLastAuditBranchNm() {
		return lastAuditBranchNm;
	}
	public void setLastAuditBranchNm(String lastAuditBranchNm) {
		this.lastAuditBranchNm = lastAuditBranchNm;
	}
	public String getCurAuditNodeId() {
		return curAuditNodeId;
	}
	public void setCurAuditNodeId(String curAuditNodeId) {
		this.curAuditNodeId = curAuditNodeId;
	}
	public String getCurAuditUser() {
		return curAuditUser;
	}
	public void setCurAuditUser(String curAuditUser) {
		this.curAuditUser = curAuditUser;
	}
	public String getCurAuditUserNm() {
		return curAuditUserNm;
	}
	public void setCurAuditUserNm(String curAuditUserNm) {
		this.curAuditUserNm = curAuditUserNm;
	}
	public String getCurAuditBranchId() {
		return curAuditBranchId;
	}
	public void setCurAuditBranchId(String curAuditBranchId) {
		this.curAuditBranchId = curAuditBranchId;
	}
	public String getCurAuditBranchNm() {
		return curAuditBranchNm;
	}
	public void setCurAuditBranchNm(String curAuditBranchNm) {
		this.curAuditBranchNm = curAuditBranchNm;
	}
	public String getNextAuditNodeId() {
		return nextAuditNodeId;
	}
	public void setNextAuditNodeId(String nextAuditNodeId) {
		this.nextAuditNodeId = nextAuditNodeId;
	}
	public String getNextAuditUser() {
		return nextAuditUser;
	}
	public void setNextAuditUser(String nextAuditUser) {
		this.nextAuditUser = nextAuditUser;
	}
	public String getNextAuditUserNm() {
		return nextAuditUserNm;
	}
	public void setNextAuditUserNm(String nextAuditUserNm) {
		this.nextAuditUserNm = nextAuditUserNm;
	}
	public String getNextAuditBranchId() {
		return nextAuditBranchId;
	}
	public void setNextAuditBranchId(String nextAuditBranchId) {
		this.nextAuditBranchId = nextAuditBranchId;
	}
	public String getNextAuditBranchNm() {
		return nextAuditBranchNm;
	}
	public void setNextAuditBranchNm(String nextAuditBranchNm) {
		this.nextAuditBranchNm = nextAuditBranchNm;
	}
	public String getCurRoleName() {
		return curRoleName;
	}
	public void setCurRoleName(String curRoleName) {
		this.curRoleName = curRoleName;
	}
	public String getAllUsers() {
		return allUsers;
	}
	public void setAllUsers(String allUsers) {
		this.allUsers = allUsers;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public String getAuditTypeDes() {
			auditTypeDes = DictionaryCache.getAuditTypeMap(this.getAuditType());
		return auditTypeDes;
	}
	public String getAuditStatusDes() {
			auditStatusDes = DictionaryCache.getFromPoolDictMap(this.getAuditStatus());
		return auditStatusDes;
	}
	public String getApplyNoDes() {
		if(PublicStaticDefineTab.AUDIT_TYPE_BATCH.equals(this.getAuditType())){
			applyNoDes = "[批]"+this.getApplyNo();
		}else if(PublicStaticDefineTab.AUDIT_TYPE_BILLS.equals(this.getAuditType())){
			applyNoDes = "[票]"+this.getApplyNo();
		}else{
			applyNoDes = "[单]"+this.getApplyNo();
		}
		return applyNoDes;
	}
	public String getBusiTypeDes() {
			busiTypeDes = DictionaryCache.getAuditBusiTypeMap(this.getBusiType());
		return busiTypeDes;
	}
	public String getProductIdDes() {
			productIdDes = ProductTypeCache.getProductName(this.getProductId());
		return productIdDes;
	}
	public String getBillTypeDes() {
			billTypeDes = DictionaryCache.getBillTypeMapName(this.getBillType());
		return billTypeDes;
	}
	public String getBillMediaDes() {
			billMediaDes = DictionaryCache.getBillMediaMapName(this.getBillMedia());
		return billMediaDes;
	}
	
}
