package com.mingtech.application.pool.query.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;



/**
 * 基础查询Bean
 * @Description TODO
 * @author gcj
 * @version v1.0
 * @date 20210607
 */
public class CommonQueryBean implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private String custNo;     //客户号
	private String custName;   //客户名称
	private String custOrgcode;//组织结构代码
	private String status;     //状态
	private String statusName;     //状态名称
	private String deprtName;  //报送机构名称
	private String deprtId;  //报送机构
	private String acceptName;      //承兑人名称
	private String bankNo;          //开户行行号
	private String bankName;        //开户行行名
	private String bpsNo;     //票据池编号
	private String bpsName; //票据池名称  
	private Date createDate;  //创建时间
	private Date createDate1;  //创建时间1
	private String addresseeName;     //联系人名称
	private String addresseePhoneNo;  //联系人电话
	private String onlineNo;          //在线协议编号
	private String msgContent;  //短信内容
	private String id;//ID
	private String contractNo;    //合同编号
	private String loanNo;        //借据编号
	private String loanAcctName;//付款户名
	private String deduAcctName;  //收款人名称
	private String deduAcctNo;    //收款人账号
	private String loanAcctNo;    //付款账号
	private BigDecimal surpluslAmt;  //剩余金额
	private String deduBankCode;  //收款人开户行行号
	private String deduBankName;  //收款人开户行名称
	private String ccupy;//占用比例
	private String ccupy1;//新占用比例
	private BigDecimal creditamount;// 担保合同金额 即 授信金额
	private Date contractEffectiveDt;//担保合同生效日期
	private Date contractDueDt;//担保合同到期日
	private String crdtType;//信贷产品类型  XD_01银承   XD_02流贷   XD_03保函   XD_04 信用证
	private String assetType;     //资产类型     01-票据    02-活期保证金
	private String crdtTypeName;//信贷产品类型名称
	private String billNo;//票号
	private String onlineCrdtNo;//在线协议编号
	private String openFlag;//01  签约票据池融资功能
	private String frozenstate; //DJ_03  表示冻结
	private String protocolStatus; //是否有生效在线流贷协议 1 :生效 0:不生效
	private String protocolStatus1; //是否有生效在线银承协议 1 :生效0:不生效
	private Date firstSignDate;// 首次签约时间
	private String signDeptName;// 归属机构
	private String ifCrdt;//是否签约在线流贷 0:是1:否
	private String ifAcpt;//是否签约在线银承 0:是1:否
	private BigDecimal loanAmt;      //业务金额   
	private BigDecimal loanBalance;  //业务余额   
	private String risklevel;//风险等级	FX_01 高风险产品 FX_02 低风险产品
	private Date startDate;      //融资业务起始日
	private Date endDate;        //融资业务到期日
	private BigDecimal amt;           //金额       
	private String billMedia;     //票据介质     1-纸票	2-电子
	private String billType;      //票据类型     AC01-银票  AC02-商票
	private Date issueDt;         //出票日      
	private Date dueDt;           //到期日      
	private String banEndrsmtFlag;//不得转让标记   0-可转让 1-不可转让
	private String drwrName;      //出票人名称    
	private String drwrBankNo;    //出票人开户行行号 
	private String acptBankName;  //承兑人开户行名称 
	private String acptBankNo;    //承兑人开户行行号 
	private BigDecimal totalAmt;  //总金额
	private String busiType;  //业务类型  01：资产-现金 02：资产-票据 03：融资业务-银承 04：融资业务-流贷  05：融资业务-保函  06：融资业务-国内信用证
	private String operaStatus;//审批状态 
	private String operaStatusDesc;//审批状态 中文描述
	private String SIssuerAccount;// 出票人账号
	private String SPayeeAccount; // 收款人账号	
	private BigDecimal  waitPayAmt;//剩余支付金额
	private String busiId;//业务ID
    private String isOnline;//是否线上 1 是 0 否
    
    /*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	
	
	private String SIssuerAcctName;// 出票人账号名称
	private String plAccptrAcctName;// 承兑人账号名称
	private String plPyeeAcctName;// 收款人账号名称
	private String plAccptrAcctNo;// 承兑人账号
	private String plAccptr;// 承兑人名称
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	
	/*** 融合改造新增字段  end*/
	
	

	public String getBusiId() {
		return busiId;
	}
	public String getSIssuerAcctName() {
		return SIssuerAcctName;
	}
	public void setSIssuerAcctName(String sIssuerAcctName) {
		SIssuerAcctName = sIssuerAcctName;
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
	public String getPlAccptrAcctName() {
		return plAccptrAcctName;
	}
	public void setPlAccptrAcctName(String plAccptrAcctName) {
		this.plAccptrAcctName = plAccptrAcctName;
	}
	public String getPlPyeeAcctName() {
		return plPyeeAcctName;
	}
	public void setPlPyeeAcctName(String plPyeeAcctName) {
		this.plPyeeAcctName = plPyeeAcctName;
	}
	public String getPlAccptrAcctNo() {
		return plAccptrAcctNo;
	}
	public void setPlAccptrAcctNo(String plAccptrAcctNo) {
		this.plAccptrAcctNo = plAccptrAcctNo;
	}
	public String getPlAccptr() {
		return plAccptr;
	}
	public void setPlAccptr(String plAccptr) {
		this.plAccptr = plAccptr;
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
	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}
	
	
	
	
	public String getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}
	public BigDecimal getWaitPayAmt() {
		return waitPayAmt;
	}
	public void setWaitPayAmt(BigDecimal waitPayAmt) {
		this.waitPayAmt = waitPayAmt;
	}
	public String getSIssuerAccount() {
		return SIssuerAccount;
	}
	public void setSIssuerAccount(String sIssuerAccount) {
		SIssuerAccount = sIssuerAccount;
	}
	public String getSPayeeAccount() {
		return SPayeeAccount;
	}
	public void setSPayeeAccount(String sPayeeAccount) {
		SPayeeAccount = sPayeeAccount;
	}
	public String getOperaStatusDesc() {
		return operaStatusDesc = DictionaryCache.getFromPoolDictMap(String.valueOf(operaStatus));
	}
	public void setOperaStatusDesc(String operaStatusDesc) {
		this.operaStatusDesc = operaStatusDesc;
	}
	public String getOperaStatus() {
		return operaStatus;
	}
	public void setOperaStatus(String operaStatus) {
		this.operaStatus = operaStatus;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
	public BigDecimal getAmt() {
		return amt;
	}
	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}
	public String getBillMedia() {
		return billMedia;
	}
	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public Date getIssueDt() {
		return issueDt;
	}
	public void setIssueDt(Date issueDt) {
		this.issueDt = issueDt;
	}
	public Date getDueDt() {
		return dueDt;
	}
	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
	}
	public String getBanEndrsmtFlag() {
		return banEndrsmtFlag;
	}
	public void setBanEndrsmtFlag(String banEndrsmtFlag) {
		this.banEndrsmtFlag = banEndrsmtFlag;
	}
	public String getDrwrName() {
		return drwrName;
	}
	public void setDrwrName(String drwrName) {
		this.drwrName = drwrName;
	}
	public String getDrwrBankNo() {
		return drwrBankNo;
	}
	public void setDrwrBankNo(String drwrBankNo) {
		this.drwrBankNo = drwrBankNo;
	}
	public String getAcptBankName() {
		return acptBankName;
	}
	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}
	public String getAcptBankNo() {
		return acptBankNo;
	}
	public void setAcptBankNo(String acptBankNo) {
		this.acptBankNo = acptBankNo;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getBpsName() {
		return bpsName;
	}
	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
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
	public String getRisklevel() {
		return risklevel;
	}
	public void setRisklevel(String risklevel) {
		this.risklevel = risklevel;
	}
	public BigDecimal getLoanAmt() {
		return loanAmt;
	}
	public void setLoanAmt(BigDecimal loanAmt) {
		this.loanAmt = loanAmt;
	}
	public BigDecimal getLoanBalance() {
		return loanBalance;
	}
	public void setLoanBalance(BigDecimal loanBalance) {
		this.loanBalance = loanBalance;
	}
	public String getIfCrdt() {
		return ifCrdt;
	}
	public void setIfCrdt(String ifCrdt) {
		this.ifCrdt = ifCrdt;
	}
	public String getIfAcpt() {
		return ifAcpt;
	}
	public void setIfAcpt(String ifAcpt) {
		this.ifAcpt = ifAcpt;
	}
	public Date getFirstSignDate() {
		return firstSignDate;
	}
	public void setFirstSignDate(Date firstSignDate) {
		this.firstSignDate = firstSignDate;
	}
	public String getSignDeptName() {
		return signDeptName;
	}
	public void setSignDeptName(String signDeptName) {
		this.signDeptName = signDeptName;
	}
	public String getOpenFlag() {
		return openFlag;
	}
	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}
	public String getFrozenstate() {
		return frozenstate;
	}
	public void setFrozenstate(String frozenstate) {
		this.frozenstate = frozenstate;
	}
	public String getProtocolStatus() {
		return protocolStatus;
	}
	public void setProtocolStatus(String protocolStatus) {
		this.protocolStatus = protocolStatus;
	}
	public String getProtocolStatus1() {
		return protocolStatus1;
	}
	public void setProtocolStatus1(String protocolStatus1) {
		this.protocolStatus1 = protocolStatus1;
	}
	public String getOnlineCrdtNo() {
		return onlineCrdtNo;
	}
	public void setOnlineCrdtNo(String onlineCrdtNo) {
		this.onlineCrdtNo = onlineCrdtNo;
	}
	public Date getCreateDate1() {
		return createDate1;
	}
	public void setCreateDate1(Date createDate1) {
		this.createDate1 = createDate1;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public String getCrdtType() {
		return crdtType;
	}
	public void setCrdtType(String crdtType) {
		this.crdtType = crdtType;
	}
	public String getCrdtTypeName() {
		return crdtTypeName;
	}
	public void setCrdtTypeName(String crdtTypeName) {
		this.crdtTypeName = crdtTypeName;
	}
	public BigDecimal getCreditamount() {
		return creditamount;
	}
	public void setCreditamount(BigDecimal creditamount) {
		this.creditamount = creditamount;
	}
	public Date getContractEffectiveDt() {
		return contractEffectiveDt;
	}
	public void setContractEffectiveDt(Date contractEffectiveDt) {
		this.contractEffectiveDt = contractEffectiveDt;
	}
	public Date getContractDueDt() {
		return contractDueDt;
	}
	public void setContractDueDt(Date contractDueDt) {
		this.contractDueDt = contractDueDt;
	}
	public String getCcupy() {
		return ccupy;
	}
	public void setCcupy(String ccupy) {
		this.ccupy = ccupy;
	}
	public String getDeduBankCode() {
		return deduBankCode;
	}
	public void setDeduBankCode(String deduBankCode) {
		this.deduBankCode = deduBankCode;
	}
	public String getDeduBankName() {
		return deduBankName;
	}
	public void setDeduBankName(String deduBankName) {
		this.deduBankName = deduBankName;
	}
	public String getDeduAcctNo() {
		return deduAcctNo;
	}
	public void setDeduAcctNo(String deduAcctNo) {
		this.deduAcctNo = deduAcctNo;
	}
	public BigDecimal getSurpluslAmt() {
		return surpluslAmt;
	}
	public void setSurpluslAmt(BigDecimal surpluslAmt) {
		this.surpluslAmt = surpluslAmt;
	}
	public String getLoanAcctNo() {
		return loanAcctNo;
	}
	public void setLoanAcctNo(String loanAcctNo) {
		this.loanAcctNo = loanAcctNo;
	}
	public String getLoanAcctName() {
		return loanAcctName;
	}
	public void setLoanAcctName(String loanAcctName) {
		this.loanAcctName = loanAcctName;
	}
	public String getDeduAcctName() {
		return deduAcctName;
	}
	public void setDeduAcctName(String deduAcctName) {
		this.deduAcctName = deduAcctName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getLoanNo() {
		return loanNo;
	}
	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public String getAddresseeName() {
		return addresseeName;
	}
	public void setAddresseeName(String addresseeName) {
		this.addresseeName = addresseeName;
	}
	public String getAddresseePhoneNo() {
		return addresseePhoneNo;
	}
	public void setAddresseePhoneNo(String addresseePhoneNo) {
		this.addresseePhoneNo = addresseePhoneNo;
	}
	public String getOnlineNo() {
		return onlineNo;
	}
	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getAcceptName() {
		return acceptName;
	}
	public void setAcceptName(String acceptName) {
		this.acceptName = acceptName;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getDeprtId() {
		return deprtId;
	}
	public void setDeprtId(String deprtId) {
		this.deprtId = deprtId;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustOrgcode() {
		return custOrgcode;
	}
	public void setCustOrgcode(String custOrgcode) {
		this.custOrgcode = custOrgcode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDeprtName() {
		return deprtName;
	}
	public void setDeprtName(String deprtName) {
		this.deprtName = deprtName;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getCcupy1() {
		return ccupy1;
	}
	public void setCcupy1(String ccupy1) {
		this.ccupy1 = ccupy1;
	}

	
	
	
}
