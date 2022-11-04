package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 协议查询Bean
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-18
 */
public class ProtocolQueryBean implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	/*票据池基本信息*/
	private String poolInfoId;//主键ID
	private String poolAgreement;//票据池协议号
	private String poolName;//票据池名称
	private String custnumber;// 客户号
	private String custOrgcode;//客户组织机构代码
	private String custname;// 客户名称
	private String elecDraftAccount;//电票签约账号  (存多个字段，用 | 分隔)
	private String elecDraftAccountName;//电票签约账号名称  (存多个字段，用 | 分隔)
	private Date effstartdate;// 票据池协议生效日期
	private Date effenddate;// 票据池协议截止日期
	private String isGroup;// 是否集团
	private String busiType; // 业务类型  02虚拟票据池
	private String signingFunction;//签约功能  01：票据账务管家
	private String authperson;// 客户经办人身份证号
	private String licename;// 客户经办人名称
	private String phonenumber;// 客户经办人手机号	
	private String xyflag;// 是否自动续约（0：否 ，1：是）   汉口银行均为自动续约
	private String signDeptNo;//签约机构号
	private String signDeptName;//签约机构名称
	
	/*融资票据池基本信息*/
	private String marginAccount;// 池保证金账户
	private String marginAccountName;// 池保证金账户名称
	private String poolMode;// 池模式 01总量控制
	private String zyflag;// 是否签自动质押协议（00：否 ，01：是）
	private String zyflagName;// 界面显示字段映射：是否签自动质押协议（00：否 ，01：是）
	private String poolAccount;// 客户结算账户
	private String poolAccountName;//结算账号名称
	private String officeNet;// 受理网点
	private String officeNetName;//受理网点名称
	private String operatorName1; //受理人
	
	/*担保合同信息基本信息*/
	private String creditDeptNo;//融资机构号
	private String creditDeptName;//融资机构名称
	private String contract;//担保合同号
	private String pawnType;//质物类型    ZW_01:票据池高风险  ZW_02：票据池低风险  
	private BigDecimal creditamount;// 担保合同金额 即 授信金额
	private BigDecimal creditFreeAmount;//担保合同未用金额
	private BigDecimal creditUsedAmount;//担保合同已用金额
	private Date contractTransDt; //担保合同签订日期
	private Date contractEffectiveDt;//担保合同生效日期
	private Date contractDueDt;//担保合同到期日
	
	/*状态控制信息*/
	private String vStatus;//虚拟票据池签约状态   VS_01：签约    VS_02:解约
	private String vtStatusName;//虚拟票据池签约状态说明   用于界面显示
	private String openFlag;//融资业务开通标识：00：未签约    01：已签约  02：已解约
	private String openFlagName;//融资业务开通标识名称，用于界面显示，不存库
	private String approveFlag;//融资业务审批标识：01：签约审批中 02：签约审批拒绝  03：签约审批通过  04：解约审批中 05：解约审批拒绝  06：解约审批通过
	private String approveFlagName;//融资业务审批标识名称，用于界面显示，不存库
	private String frozenstate;//冻结
	private Date frozenTime;//冻结操作时间
	private String frozenstateName;//冻结状态名称
	private String frozenFlag;//解冻
	private String frozenFlagName;
	private Date operateTime;//操作时间
	private String contractType;//签约类型  (01：单户票据池)-----
	private String contractTypeName;
	
	private String accountManager;//客户经理
	private String accountManagerId;//客户经理id
	
	private String feeType;//缴费模式SFMS_01:年费 SFMS_02:逐笔
	private Date feeIssueDt;//年费生效日
	private Date feeDueDt;//年费到期日
	private String checkResult;//对账结果
	private String pSignType;//签约类型
	private String pBreakType;//解约类型
	private String feeModel;//收费标准
	private String memberId;//会员代码
	private String bankname;//行名
	private String bankno;//支付行号
	private String acptBankName;   //承兑人名称（承兑人开户行行名） 
	private String guarantDiscName;//保贴人名称           
	private String guarantDiscNo;  //保贴编号            
	private String checkType;      //校验类型  1-财票 2-商票 
	private String acceptor;//承兑人名称
	private String serialNo;//序号
	private String transBrchBankNo;
	private String id;
	private String acptBankCode;//承兑人开户行行号
	
	
	public String getAcptBankName() {
		return acptBankName;
	}
	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}
	public String getAcptBankCode() {
		return acptBankCode;
	}
	public void setAcptBankCode(String acptBankCode) {
		this.acptBankCode = acptBankCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTransBrchBankNo() {
		return transBrchBankNo;
	}
	public void setTransBrchBankNo(String transBrchBankNo) {
		this.transBrchBankNo = transBrchBankNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getCheckType() {
		return checkType;
	}
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	
	public String getGuarantDiscName() {
		return guarantDiscName;
	}
	public void setGuarantDiscName(String guarantDiscName) {
		this.guarantDiscName = guarantDiscName;
	}
	public String getGuarantDiscNo() {
		return guarantDiscNo;
	}
	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getBankno() {
		return bankno;
	}
	public void setBankno(String bankno) {
		this.bankno = bankno;
	}
	public String getPoolInfoId() {
		return poolInfoId;
	}
	public void setPoolInfoId(String poolInfoId) {
		this.poolInfoId = poolInfoId;
	}
	public String getPoolAgreement() {
		return poolAgreement;
	}
	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getCustnumber() {
		return custnumber;
	}
	public void setCustnumber(String custnumber) {
		this.custnumber = custnumber;
	}
	public String getCustOrgcode() {
		return custOrgcode;
	}
	public void setCustOrgcode(String custOrgcode) {
		this.custOrgcode = custOrgcode;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getElecDraftAccount() {
		return elecDraftAccount;
	}
	public void setElecDraftAccount(String elecDraftAccount) {
		this.elecDraftAccount = elecDraftAccount;
	}
	public String getElecDraftAccountName() {
		return elecDraftAccountName;
	}
	public void setElecDraftAccountName(String elecDraftAccountName) {
		this.elecDraftAccountName = elecDraftAccountName;
	}
	public Date getEffstartdate() {
		return effstartdate;
	}
	public void setEffstartdate(Date effstartdate) {
		this.effstartdate = effstartdate;
	}
	public Date getEffenddate() {
		return effenddate;
	}
	public void setEffenddate(Date effenddate) {
		this.effenddate = effenddate;
	}
	public String getIsGroup() {
		return isGroup;
	}
	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public String getSigningFunction() {
		return signingFunction;
	}
	public void setSigningFunction(String signingFunction) {
		this.signingFunction = signingFunction;
	}
	public String getAuthperson() {
		return authperson;
	}
	public void setAuthperson(String authperson) {
		this.authperson = authperson;
	}
	public String getLicename() {
		return licename;
	}
	public void setLicename(String licename) {
		this.licename = licename;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getXyflag() {
		return xyflag;
	}
	public void setXyflag(String xyflag) {
		this.xyflag = xyflag;
	}
	public String getSignDeptNo() {
		return signDeptNo;
	}
	public void setSignDeptNo(String signDeptNo) {
		this.signDeptNo = signDeptNo;
	}
	public String getSignDeptName() {
		return signDeptName;
	}
	public void setSignDeptName(String signDeptName) {
		this.signDeptName = signDeptName;
	}
	public String getMarginAccount() {
		return marginAccount;
	}
	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}
	public String getMarginAccountName() {
		return marginAccountName;
	}
	public void setMarginAccountName(String marginAccountName) {
		this.marginAccountName = marginAccountName;
	}
	public String getPoolMode() {
		return poolMode;
	}
	public void setPoolMode(String poolMode) {
		this.poolMode = poolMode;
	}
	public String getZyflag() {
		return zyflag;
	}
	public void setZyflag(String zyflag) {
		this.zyflag = zyflag;
	}
	public String getZyflagName() {
		return zyflagName;
	}
	public void setZyflagName(String zyflagName) {
		this.zyflagName = zyflagName;
	}
	public String getPoolAccount() {
		return poolAccount;
	}
	public void setPoolAccount(String poolAccount) {
		this.poolAccount = poolAccount;
	}
	public String getPoolAccountName() {
		return poolAccountName;
	}
	public void setPoolAccountName(String poolAccountName) {
		this.poolAccountName = poolAccountName;
	}
	public String getOfficeNet() {
		return officeNet;
	}
	public void setOfficeNet(String officeNet) {
		this.officeNet = officeNet;
	}
	public String getOfficeNetName() {
		return officeNetName;
	}
	public void setOfficeNetName(String officeNetName) {
		this.officeNetName = officeNetName;
	}
	public String getOperatorName1() {
		return operatorName1;
	}
	public void setOperatorName1(String operatorName1) {
		this.operatorName1 = operatorName1;
	}
	public String getCreditDeptNo() {
		return creditDeptNo;
	}
	public void setCreditDeptNo(String creditDeptNo) {
		this.creditDeptNo = creditDeptNo;
	}
	public String getCreditDeptName() {
		return creditDeptName;
	}
	public void setCreditDeptName(String creditDeptName) {
		this.creditDeptName = creditDeptName;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getPawnType() {
		return pawnType;
	}
	public void setPawnType(String pawnType) {
		this.pawnType = pawnType;
	}
	public BigDecimal getCreditamount() {
		return creditamount;
	}
	public void setCreditamount(BigDecimal creditamount) {
		this.creditamount = creditamount;
	}
	public BigDecimal getCreditFreeAmount() {
		return creditFreeAmount;
	}
	public void setCreditFreeAmount(BigDecimal creditFreeAmount) {
		this.creditFreeAmount = creditFreeAmount;
	}
	public BigDecimal getCreditUsedAmount() {
		return creditUsedAmount;
	}
	public void setCreditUsedAmount(BigDecimal creditUsedAmount) {
		this.creditUsedAmount = creditUsedAmount;
	}
	public Date getContractTransDt() {
		return contractTransDt;
	}
	public void setContractTransDt(Date contractTransDt) {
		this.contractTransDt = contractTransDt;
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
	public String getvStatus() {
		return vStatus;
	}
	public void setvStatus(String vStatus) {
		this.vStatus = vStatus;
	}
	public String getVtStatusName() {
		return vtStatusName;
	}
	public void setVtStatusName(String vtStatusName) {
		this.vtStatusName = vtStatusName;
	}
	public String getOpenFlag() {
		return openFlag;
	}
	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}
	public String getOpenFlagName() {
		return openFlagName;
	}
	public void setOpenFlagName(String openFlagName) {
		this.openFlagName = openFlagName;
	}
	public String getApproveFlag() {
		return approveFlag;
	}
	public void setApproveFlag(String approveFlag) {
		this.approveFlag = approveFlag;
	}
	public String getApproveFlagName() {
		return approveFlagName;
	}
	public void setApproveFlagName(String approveFlagName) {
		this.approveFlagName = approveFlagName;
	}
	public String getFrozenstate() {
		return frozenstate;
	}
	public void setFrozenstate(String frozenstate) {
		this.frozenstate = frozenstate;
	}
	public Date getFrozenTime() {
		return frozenTime;
	}
	public void setFrozenTime(Date frozenTime) {
		this.frozenTime = frozenTime;
	}
	public String getFrozenstateName() {
		return frozenstateName;
	}
	public void setFrozenstateName(String frozenstateName) {
		this.frozenstateName = frozenstateName;
	}
	public String getFrozenFlag() {
		return frozenFlag;
	}
	public void setFrozenFlag(String frozenFlag) {
		this.frozenFlag = frozenFlag;
	}
	public String getFrozenFlagName() {
		return frozenFlagName;
	}
	public void setFrozenFlagName(String frozenFlagName) {
		this.frozenFlagName = frozenFlagName;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getContractTypeName() {
		return contractTypeName;
	}
	public void setContractTypeName(String contractTypeName) {
		this.contractTypeName = contractTypeName;
	}
	public String getAccountManager() {
		return accountManager;
	}
	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}
	public String getAccountManagerId() {
		return accountManagerId;
	}
	public void setAccountManagerId(String accountManagerId) {
		this.accountManagerId = accountManagerId;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public Date getFeeIssueDt() {
		return feeIssueDt;
	}
	public void setFeeIssueDt(Date feeIssueDt) {
		this.feeIssueDt = feeIssueDt;
	}
	public Date getFeeDueDt() {
		return feeDueDt;
	}
	public void setFeeDueDt(Date feeDueDt) {
		this.feeDueDt = feeDueDt;
	}
	public String getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}
	public String getpSignType() {
		return pSignType;
	}
	public void setpSignType(String pSignType) {
		this.pSignType = pSignType;
	}
	public String getpBreakType() {
		return pBreakType;
	}
	public void setpBreakType(String pBreakType) {
		this.pBreakType = pBreakType;
	}
	public String getFeeModel() {
		return feeModel;
	}
	public void setFeeModel(String feeModel) {
		this.feeModel = feeModel;
	}
	
}
