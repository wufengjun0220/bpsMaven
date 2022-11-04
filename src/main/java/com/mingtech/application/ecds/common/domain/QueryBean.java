package com.mingtech.application.ecds.common.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: zhangdapeng
* @日期: Aug 13, 2009 1:09:59 PM
* @描述: [QueryBean]用来封装组合查询的条件
 */
public class QueryBean {
	
	/**
	 * 票据池信息组合查询
	 */
	private String id;//
	private Double startamount;//授信金额起始
	private Double endamount;//授信金额最小
	
	private String billNo;//票号
	private String idNb;//报文中票号
	private Date startDate;//起始日期
	private Date endDate;//结束日期
	private String billType;//票据类型
	private String discOutOrgCode;//贴出人组织机构代码
	private String acceptorBankCode;//承兑行行号
	private String sBillType;//票据类型
	private String rRcvgprsnofrcrscmonid;//被追索人组织机构代码
	private String rRcrsrcmonid;//追索人组织机构代码
	private String acceptAcctSvcr;//提示付款承兑方大额行号
	private String collAcctSvcr;//提示付款人行号
	/* 承兑 */
	private String SApplyerOrgCode;//出票人组织机构代码
	private String SStatusFlag;//票据状态
	private String SCollztnpropsrOrgCode; //出质人组织机构代码
	private String SCollztnbkBankCode; //质权人开户行行号
	private String queryParam;//高级查询参数
	private String SIntPayway;//付息方式
	private String SBatchNo;//批次号
	private String SCustOrgCode;//客户组织机构
	private String SCustName;//客户名称
	private Date DDueDt;//到期日
	private BigDecimal FBillAmount; // 票面金额
	private String SBillMedia;//票据介质
	private Date applDt;      //提示付款(或逾期提示付款)申请日期
	private String applDtStart;      //提示付款(或逾期提示付款)申请日期
	private String applDtEnd;      //提示付款(或逾期提示付款)申请日期
	
	/*电子合同*/
	private String ctrctNb;//合同编号
	
	/*报表管理*/
	private String formBusType ;//报表业务类型
	
	private BigDecimal startAmount; // 起始金额
	private BigDecimal endAmount; // 结束金额
	private String orderByName; // 排序项
	private String orderByType; // 排序类型
	private String acceptorBankName; //承兑人名称
	private String issuerName; // 出票人名称
	

	private String giDesc;//汇总信息名称
	private String Flag;//查询方式
	private String BusiType;//业务类型
	private String GiType;//汇总统计孔径
	
	private int remindNum = 0;   //提醒天数
	private String sBankNos;  //行号集合
	
	private String queryStatus;
	
	//票据查询相关参数 zhuyanan 20120428 start
	private Date startIssueDate;//出票日起始日期
	private Date endIssueDate;//出票日结束日期
	private Date startDueDate;//到期日起始日期
	private Date endDueDate;//到期日结束日期
	//票据查询相关参数 zhuyanan 20120428 end
	private String billClass;//票据分类
	private String discountOutName;//原帖出人名称
	private String billSource;//票据来源
	private String SBranchId;        //机构ID
	
	private String acceptAccount;//承兑人账号
	
	/***2017.03.17新增***/ 
	private String deptlevelCode; // 机构使用层级编码
	
	/*贴现*/
	private String productId;//产品号（新加买入/票据来源）
	private String productIdSale;//产品号（新加/卖出方式）
	
	/*纸票质押*/
	private String collztnPropsrOrgCode; // 出质人组织机构代码
	private String collztnPropsrName; // 出质人名称
	private String collztnPropsrAccount; // 出质人账号
	private String collztnPropsrBankCode;// 出质人开户行行号
	private String collztnPropsrAgcyBankCode;// 出质人承接行行号
	private String uncollztnApplyStatus; //是否解质押
	private String collztnPropsrSocCode;//出质人社会信用代码
	private String poolAgreement; //票据池编号
	/*保证金*/
	private String custNumber;//核心客户号
	private String marginAccount;//保证金账号
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startplDueDt;// 到期日
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endplDueDt;// 到期日
	private List statusList ;
	/** 审批系统添加 20210619  start */
	private String tabNm;//表名
	private String tabCNm;//表中文名

	private String applyUserNm;//业务申请人名称
	private String applyBranchId;//业务申请机构id
	private String applyNo;//业务申请号
	private String cpCustNm;//交易对手名称
	private String cpCustCertNo;//交易对手证件号
	private String rangType;// 统计范围 0 当日 1 本周 2 本月 3 全年
	private String auditType;//审批类型-01 批次审批 、02清单审批、03通用业务审批
	private String auditStatus;//审批状态  -1终止，1提交，2处理中，3驳回，4通过
	private String finacialType;//授信类型
	private String creditType;//信用类别
	private String branchClass;//机构类型
	private String billMedia;//票据介质

	private String oldAcctNo;
	private String newAcctNo;
	
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	
	/*** 融合改造新增字段  end*/
	
	public String getOldAcctNo() {
		return oldAcctNo;
	}

	public String getBeginRangeNo() {
		return beginRangeNo;
	}

	public void setBeginRangeNo(String beginRangeNo) {
		this.beginRangeNo = beginRangeNo;
	}

	public String getEndRangeNo() {
		return endRangeNo;
	}

	public void setEndRangeNo(String endRangeNo) {
		this.endRangeNo = endRangeNo;
	}

	public BigDecimal getStandardAmt() {
		return standardAmt;
	}

	public void setStandardAmt(BigDecimal standardAmt) {
		this.standardAmt = standardAmt;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public String getDraftSource() {
		return draftSource;
	}

	public void setDraftSource(String draftSource) {
		this.draftSource = draftSource;
	}

	public String getSplitFlag() {
		return splitFlag;
	}

	public void setSplitFlag(String splitFlag) {
		this.splitFlag = splitFlag;
	}

	public void setOldAcctNo(String oldAcctNo) {
		this.oldAcctNo = oldAcctNo;
	}

	public String getNewAcctNo() {
		return newAcctNo;
	}

	public void setNewAcctNo(String newAcctNo) {
		this.newAcctNo = newAcctNo;
	}

	/** 审批系统添加 20210619  start */

	public Date getStartplDueDt() {
		return startplDueDt;
	}

	public void setStartplDueDt(Date startplDueDt) {
		this.startplDueDt = startplDueDt;
	}

	public Date getEndplDueDt() {
		return endplDueDt;
	}

	public void setEndplDueDt(Date endplDueDt) {
		this.endplDueDt = endplDueDt;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getMarginAccount() {
		return marginAccount;
	}

	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductIdSale() {
		return productIdSale;
	}

	public void setProductIdSale(String productIdSale) {
		this.productIdSale = productIdSale;
	}

	public String getAcceptAccount() {
		return acceptAccount;
	}

	public void setAcceptAccount(String acceptAccount) {
		this.acceptAccount = acceptAccount;
	}

	public String getsBillType() {
		return sBillType;
	}

	public void setsBillType(String sBillType) {
		this.sBillType = sBillType;
	}

	public String getrRcvgprsnofrcrscmonid() {
		return rRcvgprsnofrcrscmonid;
	}

	public void setrRcvgprsnofrcrscmonid(String rRcvgprsnofrcrscmonid) {
		this.rRcvgprsnofrcrscmonid = rRcvgprsnofrcrscmonid;
	}

	public String getrRcrsrcmonid() {
		return rRcrsrcmonid;
	}

	public void setrRcrsrcmonid(String rRcrsrcmonid) {
		this.rRcrsrcmonid = rRcrsrcmonid;
	}

	public String getsBankNos() {
		return sBankNos;
	}

	public void setsBankNos(String sBankNos) {
		this.sBankNos = sBankNos;
	}

	public String getSBranchId() {
		return SBranchId;
	}

	public void setSBranchId(String sBranchId) {
		SBranchId = sBranchId;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public String getBillClass() {
		return billClass;
	}

	public void setBillClass(String billClass) {
		this.billClass = billClass;
	}

	public String getDiscountOutName() {
		return discountOutName;
	}


	public void setDiscountOutName(String discountOutName) {
		this.discountOutName = discountOutName;
	}


	public String getQueryStatus() {
		return queryStatus;
	}


	public void setQueryStatus(String queryStatus) {
		this.queryStatus = queryStatus;
	}


	public String getFormBusType(){
		return formBusType;
	}

	
	public void setFormBusType(String formBusType){
		this.formBusType = formBusType;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	
	public String getBillType() {
		return billType;
	}



	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}


	public String getSApplyerOrgCode() {
		return SApplyerOrgCode;
	}


	public void setSApplyerOrgCode(String applyerOrgCode) {
		SApplyerOrgCode = applyerOrgCode;
	}


	public String getSStatusFlag() {
		return SStatusFlag;
	}

	public void setSStatusFlag(String sStatusFlag) {
		SStatusFlag = sStatusFlag;
	}

	public String getDiscOutOrgCode() {
		return discOutOrgCode;
	}

	public void setDiscOutOrgCode(String discOutOrgCode) {
		this.discOutOrgCode = discOutOrgCode;
	}
	public String getAcceptorBankCode() {
		return acceptorBankCode;
	}

	public void setAcceptorBankCode(String acceptorBankCode) {
		this.acceptorBankCode = acceptorBankCode;
	}

	public String getSBillType() {
		return sBillType;
	}

	public void setSBillType(String billType) {
		sBillType = billType;
	}

	public String getRRcvgprsnofrcrscmonid() {
		return rRcvgprsnofrcrscmonid;
	}

	public void setRRcvgprsnofrcrscmonid(String rcvgprsnofrcrscmonid) {
		rRcvgprsnofrcrscmonid = rcvgprsnofrcrscmonid;
	}

	public String getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public String getRRcrsrcmonid() {
		return rRcrsrcmonid;
	}

	public void setRRcrsrcmonid(String rcrsrcmonid) {
		rRcrsrcmonid = rcrsrcmonid;
	}

	public Date getDDueDt() {
		return DDueDt;
	}

	public void setDDueDt(Date dueDt) {
		DDueDt = dueDt;
	}

	public String getSCollztnpropsrOrgCode() {
		return SCollztnpropsrOrgCode;
	}

	public void setSCollztnpropsrOrgCode(String collztnpropsrOrgCode) {
		SCollztnpropsrOrgCode = collztnpropsrOrgCode;
	}

	public String getSCollztnbkBankCode() {
		return SCollztnbkBankCode;
	}

	public void setSCollztnbkBankCode(String collztnbkBankCode) {
		SCollztnbkBankCode = collztnbkBankCode;
	}

	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}

	public void setFBillAmount(BigDecimal billAmount) {
		FBillAmount = billAmount;
	}

	public String getSBillMedia() {
		return SBillMedia;
	}

	public void setSBillMedia(String billMedia) {
		SBillMedia = billMedia;
	}

	public String getSIntPayway() {
		return SIntPayway;
	}

	public void setSIntPayway(String intPayway) {
		SIntPayway = intPayway;
	}

	public String getSBatchNo() {
		return SBatchNo;
	}

	public void setSBatchNo(String batchNo) {
		SBatchNo = batchNo;
	}

	public String getSCustOrgCode() {
		return SCustOrgCode;
	}

	public void setSCustOrgCode(String custOrgCode) {
		SCustOrgCode = custOrgCode;
	}

	public String getSCustName() {
		return SCustName;
	}

	public void setSCustName(String custName) {
		SCustName = custName;
	}

	public String getAcceptAcctSvcr() {
		return acceptAcctSvcr;
	}

	public void setAcceptAcctSvcr(String acceptAcctSvcr) {
		this.acceptAcctSvcr = acceptAcctSvcr;
	}

	public String getCollAcctSvcr() {
		return collAcctSvcr;
	}

	public void setCollAcctSvcr(String collAcctSvcr) {
		this.collAcctSvcr = collAcctSvcr;
	}

	public String getIdNb() {
		return idNb;
	}

	public void setIdNb(String idNb) {
		this.idNb = idNb;
	}

	public String getCtrctNb() {
		return ctrctNb;
	}

	public void setCtrctNb(String ctrctNb) {
		this.ctrctNb = ctrctNb;
	}

	public BigDecimal getStartAmount(){
		return startAmount;
	}

	public void setStartAmount(BigDecimal startAmount){
		this.startAmount = startAmount;
	}

	public BigDecimal getEndAmount(){
		return endAmount;
	}

	public void setEndAmount(BigDecimal endAmount){
		this.endAmount = endAmount;
	}

	public String getOrderByName(){
		return orderByName;
	}

	public void setOrderByName(String orderByName){
		this.orderByName = orderByName;
	}

	public String getOrderByType(){
		return orderByType;
	}

	public void setOrderByType(String orderByType){
		this.orderByType = orderByType;
	}

	public String getAcceptorBankName(){
		return acceptorBankName;
	}

	public void setAcceptorBankName(String acceptorBankName){
		this.acceptorBankName = acceptorBankName;
	}

	public String getIssuerName(){
		return issuerName;
	}

	public void setIssuerName(String issuerName){
		this.issuerName = issuerName;
	}

	public String getGiDesc() {
		return giDesc;
	}


	public void setGiDesc(String giDesc) {
		this.giDesc = giDesc;
	}


	public String getFlag() {
		return Flag;
	}


	public void setFlag(String flag) {
		Flag = flag;
	}


	public String getBusiType() {
		return BusiType;
	}


	public void setBusiType(String busiType) {
		BusiType = busiType;
	}


	public String getGiType() {
		return GiType;
	}


	public void setGiType(String giType) {
		GiType = giType;
	}


	public int getRemindNum() {
		return remindNum;
	}


	public void setRemindNum(int remindNum) {
		this.remindNum = remindNum;
	}


	public String getSBankNos() {
		return sBankNos;
	}


	public void setSBankNos(String bankNos) {
		sBankNos = bankNos;
	}


	public Date getStartIssueDate() {
		return startIssueDate;
	}


	public void setStartIssueDate(Date startIssueDate) {
		this.startIssueDate = startIssueDate;
	}


	public Date getEndIssueDate() {
		return endIssueDate;
	}


	public void setEndIssueDate(Date endIssueDate) {
		this.endIssueDate = endIssueDate;
	}


	public Date getStartDueDate() {
		return startDueDate;
	}


	public void setStartDueDate(Date startDueDate) {
		this.startDueDate = startDueDate;
	}


	public Date getEndDueDate() {
		return endDueDate;
	}


	public void setEndDueDate(Date endDueDate) {
		this.endDueDate = endDueDate;
	}

	public Date getApplDt() {
		return applDt;
	}

	public void setApplDt(Date applDt) {
		this.applDt = applDt;
	}

	public String getApplDtStart() {
		return applDtStart;
	}

	public void setApplDtStart(String applDtStart) {
		this.applDtStart = applDtStart;
	}

	public String getApplDtEnd() {
		return applDtEnd;
	}

	public void setApplDtEnd(String applDtEnd) {
		this.applDtEnd = applDtEnd;
	}

	public String getDeptlevelCode() {
		return deptlevelCode;
	}

	public void setDeptlevelCode(String deptlevelCode) {
		this.deptlevelCode = deptlevelCode;
	}

	public String getCollztnPropsrOrgCode() {
		return collztnPropsrOrgCode;
	}

	public void setCollztnPropsrOrgCode(String collztnPropsrOrgCode) {
		this.collztnPropsrOrgCode = collztnPropsrOrgCode;
	}

	public String getCollztnPropsrName() {
		return collztnPropsrName;
	}

	public void setCollztnPropsrName(String collztnPropsrName) {
		this.collztnPropsrName = collztnPropsrName;
	}

	public String getCollztnPropsrAccount() {
		return collztnPropsrAccount;
	}

	public void setCollztnPropsrAccount(String collztnPropsrAccount) {
		this.collztnPropsrAccount = collztnPropsrAccount;
	}

	public String getCollztnPropsrBankCode() {
		return collztnPropsrBankCode;
	}

	public void setCollztnPropsrBankCode(String collztnPropsrBankCode) {
		this.collztnPropsrBankCode = collztnPropsrBankCode;
	}

	public String getCollztnPropsrAgcyBankCode() {
		return collztnPropsrAgcyBankCode;
	}

	public void setCollztnPropsrAgcyBankCode(String collztnPropsrAgcyBankCode) {
		this.collztnPropsrAgcyBankCode = collztnPropsrAgcyBankCode;
	}

	public String getUncollztnApplyStatus() {
		return uncollztnApplyStatus;
	}

	public void setUncollztnApplyStatus(String uncollztnApplyStatus) {
		this.uncollztnApplyStatus = uncollztnApplyStatus;
	}

	public String getCollztnPropsrSocCode() {
		return collztnPropsrSocCode;
	}

	public void setCollztnPropsrSocCode(String collztnPropsrSocCode) {
		this.collztnPropsrSocCode = collztnPropsrSocCode;
	}

	public Double getStartamount() {
		return startamount;
	}

	public void setStartamount(Double startamount) {
		this.startamount = startamount;
	}

	public Double getEndamount() {
		return endamount;
	}

	public void setEndamount(Double endamount) {
		this.endamount = endamount;
	}

	public List getStatusList() {
		return statusList;
	}

	public void setStatusList(List statusList) {
		this.statusList = statusList;
	}

	public String getPoolAgreement() {
		return poolAgreement;
	}

	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}


	public String getTabNm() {
		return tabNm;
	}

	public void setTabNm(String tabNm) {
		this.tabNm = tabNm;
	}

	public String getTabCNm() {
		return tabCNm;
	}

	public void setTabCNm(String tabCNm) {
		this.tabCNm = tabCNm;
	}

	public String getBranchClass() {
		return branchClass;
	}

	public void setBranchClass(String branchClass) {
		this.branchClass = branchClass;
	}

	public String getApplyUserNm() {
		return applyUserNm;
	}

	public void setApplyUserNm(String applyUserNm) {
		this.applyUserNm = applyUserNm;
	}

	public String getApplyBranchId() {
		return applyBranchId;
	}

	public void setApplyBranchId(String applyBranchId) {
		this.applyBranchId = applyBranchId;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public String getCpCustNm() {
		return cpCustNm;
	}

	public void setCpCustNm(String cpCustNm) {
		this.cpCustNm = cpCustNm;
	}

	public String getCpCustCertNo() {
		return cpCustCertNo;
	}

	public void setCpCustCertNo(String cpCustCertNo) {
		this.cpCustCertNo = cpCustCertNo;
	}

	public String getRangType() {
		return rangType;
	}

	public void setRangType(String rangType) {
		this.rangType = rangType;
	}

	public String getAuditType() {
		return auditType;
	}

	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getFinacialType() {
		return finacialType;
	}

	public void setFinacialType(String finacialType) {
		this.finacialType = finacialType;
	}

	public String getCreditType() {
		return creditType;
	}

	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	public String getBillMedia() {
		return billMedia;
	}

	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
