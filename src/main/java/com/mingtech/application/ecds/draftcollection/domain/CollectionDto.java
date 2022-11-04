package com.mingtech.application.ecds.draftcollection.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
import com.mingtech.application.ecds.common.DictionaryCache;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者：ChenFuLing
 * @日期：Jun 2, 2009 11:54:37 AM
 * @描述：[CollectionDto]托收明细
 */
public class CollectionDto implements java.io.Serializable {

	// Fields

	private String collectionBillId;   //主键id
//	private BtCollectionBatch btCollectionBatch;   
	private String collectbatchid;//批次id
	private BtBillInfo btBillInfo;   //机构id
	private String SBranchId;        //操作员id
	private String SOperatorId;      //票据id
	private String SBillNo;          //票号
	private String SBillType;        //票据类型
	private Date DIssueDt;           //出票日
	private Date DDueDt;             //到期日
	private String SAcceptor;        //承兑方名称
	private String SAcceptorBankName;//承兑方行名称
	private String SAcceptorBankCode;//承兑方行行号
	private String SAcceptorBankPhone;//承兑行电话号码
	private String SOwnerName;       //持票方全称(提示付款人)
	private String SOwnerBankCode;   //持票方行号
	private String SOwnerBankName;   //持票方开户行名
	private String SIssuerBankName;// 出票人开户行名称(直接从大票取值)
	private String SOwnerAccount;    //持票方账号
	private BigDecimal FBillAmount;      //票面金额
	private Date DCollectDt;         //托收日期
	private String clearingFlagName;//清算方式名称
	private String SBillMedia;       //票据介质
	private String SBillFrom;        //票据来源 参考：PublicStaticDefineTab.BILL_HIST_STATION_    01票据系统自己；02手工输入(代客托收) 03 票据池 
	private String SPaypromOrgCode;  //提示付款人组织机构代码
	private String SBillStatus;   //票据状态
	
	private String SBillStatusName;//票据状态名
	private String SBillTypeName;//票据类型名
	private String billinfoId;//票据id
	private String SBillMediaName;// 票据介质名称
	private Integer WorkflowCaseId;//工作流id
	
	//托收挑票人 时间、柜员信息
	private Date selectDate;
	private String selectUserNo;
	private String selectUserName;
	
	/** 网银接口使用属性 */
	private String overdueFlag;// 逾期标识
	private String overdueFlagName;// 逾期标识名称
	
	private String overdueCause;// 逾期原因
	private String ifSysInner;// 是否系统内
	private String aterAcct;// 承兑人账号
	private BigDecimal clewAmount;// 提示金额
	private Date aplyDate;//提示付款申请日期
	
	private Date rvetDt	;//	提示付款回复日期
	private String rvetMk;//提示付款回复标记
	private String ifPrxyCstmRvet;//是否代客户回复
	private String rfseCode;//拒付理由代码
	private String rfseRmakInfo;//拒付备注信息
	
	
	private String pyerKind;// 提示付款人类别
	
	private String ElctrncSgntr;//电子签名
	
	private String ElctrncSgntrFlag;//电子签章标示
	
	private String SMsgStatus;//报文状态
	private String SMsgStatusName;//报文状态中文名
	private String SMsgCode;//报文错误码
	private Integer workItemId;//工作项ID
	
	//private String clewPymtRmak;//提示付款备注
	private String pyerRemk;// 提示付款人备注
	private String signRmak;//签收备注
	
	private String SAgcySvcrName;//承接行名称
	private String SAgcySvcr;//承接行行号
	
	/* 承兑方组织机构代码 */
	private String acceptorOrgCode;
	
	private String collectOperSts;//托收批次状态
	private String collectOperStsName;//托收批次状态
	
	private Integer printCount;//打印次数
	
	private String printFlag;// 是否已打印
	
	private String coreBatchNo;//核心批次号
	private String collectAddr;//托收地址--承兑行地址
	private String collectPostcode;//托收邮编--承兑行邮编
	
	 private String sequenceId;//行内序号
	 
	 private String tuoshouShouhuiStautus;//托收收回记账状态
	 private String tuoshouShouhuiStautusName;//托收收回记账状态
	 private String SifPoolFlag;//是否是票据池对象
	 
	// Constructors
	
	 private Date startDate; //委托收款记账页面到期开始日期 没有映射到数据库
	 private Date endDate;   //委托收款记账页面到期截止日期 没有映射到数据库
	 
	 private String startDate_ts; //委托收款记账页面托收开始日期 没有映射到数据库
	 private String endDate_ts;   //委托收款记账页面托收截止日期 没有映射到数据库
	 private String SOwnerAccount_ts;    //页面显示用没有映射到数据库
	 
	 /** 以下字段用于导出excel使用  */
	 private String	phone	;//	电话/电挂
	 private String	categoryCode	;//	行别代码
	 private String discountInName;// 贴入方名称1 即贴现支行
	 private String discountInAccount;// 贴入人账号 即贴现帐号
	 private Date discBatchDate;// 贴现申请日   纸票：贴现日
	 private BigDecimal FRate;// 利率
	 private Long FIntDays;// 计息天数
	 private BigDecimal FInt;// 应付利息
	 private BigDecimal FBuyPayment;//贴现买入方实付金额
	 private String discountOutName;// 贴出方名称1 即贴现申请人
	 
	 
	 private String	totalMoneyShow	;
	 private String	totalSizeShow	;
	 private String inAddress;//打印电票凭证使用 不存数据库
	 private String outAddress;//打印电票凭证使用 不存数据库
	 private String SAcceptorAccount;//打印电票凭证使用 不存数据库 付款人账号
	
	/*--satrt 大额来账- 20120601-yangyawei-*/
	
	 private String backAccountNo;//大额来账序号
	 private String accountflag;//大额来账校验标识
	 private String returnCause;//托收退回原因
	 
	 /*--end----*/
	 private String endrCounts;//纸票登记用-背书次数
	 private String lastOwner;//纸票登记用-最后一手持票人
	// *****************2012-04-26 针对新核心改造新增*****************
	 private String reqSeqNo;//核心交易流水号
	 private String dueBillNum; // 贴现借据号
	 
	 private String coreSysAccount; // 转账账号
	 private String fundsDirection; // 资金来源    0-现金  1-转帐   2-待销帐
	 
	 //托收退票  日期、原因、退票登记人信息 2016
	 private Date returnDate;
	 private String returnReson;
	 private String returnUserNo;
	 private String returnUserName;
	 
	//托收回款 记账日期、流水号、记账柜员信息2016
	private Date acctDate;
	private String acctFlowNo;//票据系统记账生成的记账流水号
	//记账柜员信息
	private String acctUserNo;
	private String acctUserName;
	//记账授权柜员信息；复核人员
	private String acctAuthUserNo;
	private String acctAuthUserName;
	
//	private String billHistStation;//票据来源   01大票表  02 手动添加  03票据池
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getDiscountInName() {
		return discountInName;
	}

	public void setDiscountInName(String discountInName) {
		this.discountInName = discountInName;
	}

	public String getDiscountInAccount() {
		return discountInAccount;
	}

	public void setDiscountInAccount(String discountInAccount) {
		this.discountInAccount = discountInAccount;
	}

	public Date getDiscBatchDate() {
		return discBatchDate;
	}

	public void setDiscBatchDate(Date discBatchDate) {
		this.discBatchDate = discBatchDate;
	}

	public BigDecimal getFRate() {
		return FRate;
	}

	public void setFRate(BigDecimal rate) {
		FRate = rate;
	}

	public Long getFIntDays() {
		return FIntDays;
	}

	public void setFIntDays(Long intDays) {
		FIntDays = intDays;
	}

	public BigDecimal getFInt() {
		return FInt;
	}

	public void setFInt(BigDecimal int1) {
		FInt = int1;
	}

	public BigDecimal getFBuyPayment() {
		return FBuyPayment;
	}

	public void setFBuyPayment(BigDecimal buyPayment) {
		FBuyPayment = buyPayment;
	}

	public String getDiscountOutName() {
		return discountOutName;
	}

	public void setDiscountOutName(String discountOutName) {
		this.discountOutName = discountOutName;
	}

	/** default constructor */
	public CollectionDto() {
	}

	
	/**
	 * @param clearingFlagName 要设置的 clearingFlagName
	 */
	public void setClearingFlagName(String clearingFlagName){
		this.clearingFlagName = clearingFlagName;
	}
	
	// Property accessors

	public String getSBillMediaName(){
		return this.SBillMediaName = (String) DictionaryCache.getBillMedia(this.getSBillMedia());
	}

	public void setSBillMediaName(String billMediaName){
		SBillMediaName = billMediaName;
	}
	
	public String getCollectionBillId() {
		return this.collectionBillId;
	}

	public void setCollectionBillId(String collectionBillId) {
		this.collectionBillId = collectionBillId;
	}

	public BtBillInfo getBtBillInfo() {
		return this.btBillInfo;
	}

	public void setBtBillInfo(BtBillInfo btBillInfo) {
		this.btBillInfo = btBillInfo;
	}

	public String getSBranchId() {
		return this.SBranchId;
	}

	public void setSBranchId(String SBranchId) {
		this.SBranchId = SBranchId;
	}

	public String getSOperatorId() {
		return this.SOperatorId;
	}

	public void setSOperatorId(String SOperatorId) {
		this.SOperatorId = SOperatorId;
	}

	public String getSBillNo() {
		return this.SBillNo;
	}

	public void setSBillNo(String SBillNo) {
		this.SBillNo = SBillNo;
	}

	public String getSBillType() {
		return this.SBillType;
	}

	public void setSBillType(String SBillType) {
		this.SBillType = SBillType;
	}

	public Date getDIssueDt() {
		return this.DIssueDt;
	}

	public void setDIssueDt(Date DIssueDt) {
		this.DIssueDt = DIssueDt;
	}

	public Date getDDueDt() {
		return this.DDueDt;
	}

	public void setDDueDt(Date DDueDt) {
		this.DDueDt = DDueDt;
	}

	public String getSAcceptor() {
		return this.SAcceptor;
	}

	public void setSAcceptor(String SAcceptor) {
		this.SAcceptor = SAcceptor;
	}

	public String getSAcceptorBankName() {
		return this.SAcceptorBankName;
	}

	public void setSAcceptorBankName(String SAcceptorBankName) {
		this.SAcceptorBankName = SAcceptorBankName;
	}

	public String getSAcceptorBankCode() {
		return this.SAcceptorBankCode;
	}

	public void setSAcceptorBankCode(String SAcceptorBankCode) {
		this.SAcceptorBankCode = SAcceptorBankCode;
	}

	public String getSOwnerName() {
		return this.SOwnerName;
	}

	public void setSOwnerName(String SOwnerName) {
		this.SOwnerName = SOwnerName;
	}

	public String getSOwnerBankCode() {
		return this.SOwnerBankCode;
	}

	public void setSOwnerBankCode(String SOwnerBankCode) {
		this.SOwnerBankCode = SOwnerBankCode;
	}

	public String getSOwnerAccount() {
		return this.SOwnerAccount;
	}

	public void setSOwnerAccount(String SOwnerAccount) {
		this.SOwnerAccount = SOwnerAccount;
	}

	public Date getDCollectDt() {
		return this.DCollectDt;
	}

	public void setDCollectDt(Date DCollectDt) {
		this.DCollectDt = DCollectDt;
	}

	public String getSBillMedia() {
		return this.SBillMedia;
	}

	public void setSBillMedia(String SBillMedia) {
		this.SBillMedia = SBillMedia;
	}

	public String getSBillFrom() {
		return this.SBillFrom;
	}

	public void setSBillFrom(String SBillFrom) {
		this.SBillFrom = SBillFrom;
	}

	public String getSPaypromOrgCode() {
		return this.SPaypromOrgCode;
	}

	public void setSPaypromOrgCode(String SPaypromOrgCode) {
		this.SPaypromOrgCode = SPaypromOrgCode;
	}


	public String getSBillStatus(){
		return SBillStatus;
	}


	public void setSBillStatus(String billStatus){
		SBillStatus = billStatus;
	}

	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}

	public void setFBillAmount(BigDecimal billAmount) {
		FBillAmount = billAmount;
	}

	/* 票据状态名 */
	public String getSBillStatusName() {
		return SBillStatusName = DictionaryCache.getStatusName(this.SBillStatus);
	}

	public void setSBillStatusName(String billStatusName) {
		SBillStatusName = billStatusName;
	}
	/* 票据类型名 */
	public String getSBillTypeName() {
		return this.SBillTypeName = DictionaryCache.getBillType(this.getSBillType()).toString();
	}

	public void setSBillTypeName(String billTypeName) {
		this.SBillTypeName = billTypeName;
	}

	public Integer getWorkflowCaseId() {
		return WorkflowCaseId;
	}

	public void setWorkflowCaseId(Integer workflowCaseId) {
		WorkflowCaseId = workflowCaseId;
	}

	public String getOverdueFlag() {
		return overdueFlag;
	}

	public void setOverdueFlag(String overdueFlag) {
		this.overdueFlag = overdueFlag;
	}

	public String getOverdueCause() {
		return overdueCause;
	}

	public void setOverdueCause(String overdueCause) {
		this.overdueCause = overdueCause;
	}

	public String getIfSysInner() {
		return ifSysInner;
	}

	public void setIfSysInner(String ifSysInner) {
		this.ifSysInner = ifSysInner;
	}

	public String getPyerRemk() {
		return pyerRemk;
	}

	public void setPyerRemk(String pyerRemk) {
		this.pyerRemk = pyerRemk;
	}

	public String getPyerKind() {
		return pyerKind;
	}

	public void setPyerKind(String pyerKind) {
		this.pyerKind = pyerKind;
	}

	public String getAterAcct() {
		return aterAcct;
	}

	public void setAterAcct(String aterAcct) {
		this.aterAcct = aterAcct;
	}

	public BigDecimal getClewAmount() {
		return clewAmount;
	}

	public void setClewAmount(BigDecimal clewAmount) {
		this.clewAmount = clewAmount;
	}

	public Date getAplyDate() {
		return aplyDate;
	}

	public void setAplyDate(Date aplyDate) {
		this.aplyDate = aplyDate;
	}

	public String getElctrncSgntr() {
		return ElctrncSgntr;
	}

	public void setElctrncSgntr(String elctrncSgntr) {
		ElctrncSgntr = elctrncSgntr;
	}

	public String getSMsgStatus() {
		return SMsgStatus;
	}

	public void setSMsgStatus(String msgStatus) {
		SMsgStatus = msgStatus;
	}

	public String getSMsgCode() {
		return SMsgCode;
	}

	public void setSMsgCode(String msgCode) {
		SMsgCode = msgCode;
	}

	public Date getRvetDt() {
		return rvetDt;
	}

	public void setRvetDt(Date rvetDt) {
		this.rvetDt = rvetDt;
	}

	public String getRvetMk() {
		return rvetMk;
	}

	public void setRvetMk(String rvetMk) {
		this.rvetMk = rvetMk;
	}

	public String getIfPrxyCstmRvet() {
		return ifPrxyCstmRvet;
	}

	public void setIfPrxyCstmRvet(String ifPrxyCstmRvet) {
		this.ifPrxyCstmRvet = ifPrxyCstmRvet;
	}

	public String getRfseCode() {
		return rfseCode;
	}

	public void setRfseCode(String rfseCode) {
		this.rfseCode = rfseCode;
	}

	public String getRfseRmakInfo() {
		return rfseRmakInfo;
	}

	public void setRfseRmakInfo(String rfseRmakInfo) {
		this.rfseRmakInfo = rfseRmakInfo;
	}

	public Integer getWorkItemId() {
		return workItemId;
	}

	public void setWorkItemId(Integer workItemId) {
		this.workItemId = workItemId;
	}

	public String getAcceptorOrgCode() {
		return acceptorOrgCode;
	}

	public void setAcceptorOrgCode(String acceptorOrgCode) {
		this.acceptorOrgCode = acceptorOrgCode;
	}

	public String getSignRmak() {
		return signRmak;
	}

	public void setSignRmak(String signRmak) {
		this.signRmak = signRmak;
	}

	public String getSMsgStatusName() {
		return SMsgStatusName = DictionaryCache.getStatusName(this.getSMsgStatus());
	}

	public void setSMsgStatusName(String msgStatusName) {
		SMsgStatusName = msgStatusName;
	}

	public String getSAgcySvcrName() {
		return SAgcySvcrName;
	}

	public void setSAgcySvcrName(String agcySvcrName) {
		SAgcySvcrName = agcySvcrName;
	}

	public String getSAgcySvcr() {
		return SAgcySvcr;
	}

	public void setSAgcySvcr(String agcySvcr) {
		SAgcySvcr = agcySvcr;
	}

	public String getSOwnerBankName() {
		return SOwnerBankName;
	}

	public void setSOwnerBankName(String ownerBankName) {
		SOwnerBankName = ownerBankName;
	}

	public String getElctrncSgntrFlag() {
		return ElctrncSgntrFlag;
	}

	public void setElctrncSgntrFlag(String elctrncSgntrFlag) {
		ElctrncSgntrFlag = elctrncSgntrFlag;
	}

	public String getOverdueFlagName() {
		if("02".equals(overdueFlagName)){
			overdueFlagName = "已逾期";
		}else{
			overdueFlagName = "未逾期";
		}
		return overdueFlagName;
	}

	

	public String getPrintFlag() {
		return printFlag;
	}
	public void setPrintFlag(String printFlag) {
		this.printFlag = printFlag;
	}

	public Integer getPrintCount() {
		return printCount;
	}
	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}

	public String getCoreBatchNo() {
		return coreBatchNo;
	}
	public void setCoreBatchNo(String coreBatchNo) {
		this.coreBatchNo = coreBatchNo;
	}

	public void setCollectAddr(String collectAddr) {
		this.collectAddr = collectAddr;
	}

	public void setCollectPostcode(String collectPostcode) {
		this.collectPostcode = collectPostcode;
	}

	public String getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getTuoshouShouhuiStautus() {
		return tuoshouShouhuiStautus;
	}
	public void setTuoshouShouhuiStautus(String tuoshouShouhuiStautus) {
		this.tuoshouShouhuiStautus = tuoshouShouhuiStautus;
	}

	public String getTuoshouShouhuiStautusName() {
		return (String) DictionaryCache.getStatusName(tuoshouShouhuiStautus);
	}
	public void setTuoshouShouhuiStautusName(String tuoshouShouhuiStautusName) {
		this.tuoshouShouhuiStautusName =  tuoshouShouhuiStautusName;
	}

	public String getSAcceptorBankPhone() {
		return SAcceptorBankPhone;
	}
	public void setSAcceptorBankPhone(String acceptorBankPhone) {
		SAcceptorBankPhone = acceptorBankPhone;
	}

	public Date getStartDate(){
		return startDate;
	}
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	public Date getEndDate(){
		return endDate;
	}
	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	public String getTotalMoneyShow() {
		return totalMoneyShow;
	}

	public void setTotalMoneyShow(String totalMoneyShow) {
		this.totalMoneyShow = totalMoneyShow;
	}

	public String getTotalSizeShow() {
		return totalSizeShow;
	}

	public void setTotalSizeShow(String totalSizeShow) {
		this.totalSizeShow = totalSizeShow;
	}

	public String getSifPoolFlag() {
		return SifPoolFlag;
	}

	public void setSifPoolFlag(String sifPoolFlag) {
		SifPoolFlag = sifPoolFlag;
	}

	public String getStartDate_ts() {
		return startDate_ts;
	}

	public void setStartDate_ts(String startDate_ts) {
		this.startDate_ts = startDate_ts;
	}

	public String getEndDate_ts() {
		return endDate_ts;
	}

	public void setEndDate_ts(String endDate_ts) {
		this.endDate_ts = endDate_ts;
	}

	public String getSOwnerAccount_ts() {
		return SOwnerAccount_ts;
	}

	public void setSOwnerAccount_ts(String ownerAccount_ts) {
		SOwnerAccount_ts = ownerAccount_ts;
	}

	
	public String getInAddress(){
		return inAddress;
	}

	
	public void setInAddress(String inAddress){
		this.inAddress = inAddress;
	}

	
	public String getOutAddress(){
		return outAddress;
	}

	
	public void setOutAddress(String outAddress){
		this.outAddress = outAddress;
	}

	public String getBackAccountNo() {
		return backAccountNo;
	}

	public void setBackAccountNo(String backAccountNo) {
		this.backAccountNo = backAccountNo;
	}

	public String getAccountflag() {
		return accountflag;
	}

	public void setAccountflag(String accountflag) {
		this.accountflag = accountflag;
	}

	public String getReturnCause() {
		return returnCause;
	}

	public void setReturnCause(String returnCause) {
		this.returnCause = returnCause;
	}
		public String getEndrCounts() {
		return endrCounts;
	}

	public void setEndrCounts(String endrCounts) {
		this.endrCounts = endrCounts;
	}

	public String getLastOwner() {
		return lastOwner;
	}

	public void setLastOwner(String lastOwner) {
		this.lastOwner = lastOwner;
	}

	public String getReqSeqNo() {
		return reqSeqNo;
	}

	public void setReqSeqNo(String reqSeqNo) {
		this.reqSeqNo = reqSeqNo;
	}

	public String getDueBillNum(){
//		BtBillInfo bt = this.getBtBillInfo();
//		if(bt != null){
//			return bt.getDueBillNum();
//		}
		return "";
	}

	public void setDueBillNum(String dueBillNum){
		this.dueBillNum = dueBillNum;
	}

	
	public String getCoreSysAccount(){
		return coreSysAccount;
	}

	
	public void setCoreSysAccount(String coreSysAccount){
		this.coreSysAccount = coreSysAccount;
	}

	
	public String getFundsDirection(){
		return fundsDirection;
	}
	public String getFundsDirectionStr(){
		if(StringUtils.equals(this.getFundsDirection(), "0")){
			return "现金";
		}else if(StringUtils.equals(this.getFundsDirection(), "1")){
			return "转账";
		}else if(StringUtils.equals(this.getFundsDirection(), "2")){
			return "待销帐";
		}
		return this.getFundsDirection();
		
	}
	
	public void setFundsDirection(String fundsDirection){
		this.fundsDirection = fundsDirection;
	}

	public String getCollectbatchid() {
		return collectbatchid;
	}

	public void setCollectbatchid(String collectbatchid) {
		this.collectbatchid = collectbatchid;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public String getSelectUserNo() {
		return selectUserNo;
	}

	public void setSelectUserNo(String selectUserNo) {
		this.selectUserNo = selectUserNo;
	}

	public String getSelectUserName() {
		return selectUserName;
	}

	public void setSelectUserName(String selectUserName) {
		this.selectUserName = selectUserName;
	}

	public void setSIssuerBankName(String sIssuerBankName) {
		SIssuerBankName = sIssuerBankName;
	}

	public String getCollectOperSts() {
		return collectOperSts;
	}

	public void setCollectOperSts(String collectOperSts) {
		this.collectOperSts = collectOperSts;
	}

	public String getCollectOperStsName() {
		return collectOperStsName;
	}

	public void setCollectOperStsName(String collectOperStsName) {
		this.collectOperStsName = collectOperStsName;
	}

	public String getSAcceptorAccount() {
		return SAcceptorAccount;
	}

	public void setSAcceptorAccount(String sAcceptorAccount) {
		SAcceptorAccount = sAcceptorAccount;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public String getReturnReson() {
		return returnReson;
	}

	public void setReturnReson(String returnReson) {
		this.returnReson = returnReson;
	}

	public String getReturnUserNo() {
		return returnUserNo;
	}

	public void setReturnUserNo(String returnUserNo) {
		this.returnUserNo = returnUserNo;
	}

	public String getReturnUserName() {
		return returnUserName;
	}

	public void setReturnUserName(String returnUserName) {
		this.returnUserName = returnUserName;
	}

	public Date getAcctDate() {
		return acctDate;
	}

	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}

	public String getAcctFlowNo() {
		return acctFlowNo;
	}

	public void setAcctFlowNo(String acctFlowNo) {
		this.acctFlowNo = acctFlowNo;
	}

	public String getAcctUserNo() {
		return acctUserNo;
	}

	public void setAcctUserNo(String acctUserNo) {
		this.acctUserNo = acctUserNo;
	}

	public String getAcctUserName() {
		return acctUserName;
	}

	public void setAcctUserName(String acctUserName) {
		this.acctUserName = acctUserName;
	}

	public String getAcctAuthUserNo() {
		return acctAuthUserNo;
	}

	public void setAcctAuthUserNo(String acctAuthUserNo) {
		this.acctAuthUserNo = acctAuthUserNo;
	}

	public String getAcctAuthUserName() {
		return acctAuthUserName;
	}

	public void setAcctAuthUserName(String acctAuthUserName) {
		this.acctAuthUserName = acctAuthUserName;
	}

	public String getCollectAddr() {
		return collectAddr;
	}

	public String getCollectPostcode() {
		return collectPostcode;
	}

	public void setOverdueFlagName(String overdueFlagName) {
		this.overdueFlagName = overdueFlagName;
	}

	public String getBillinfoId() {
		return billinfoId;
	}

	public void setBillinfoId(String billinfoId) {
		this.billinfoId = billinfoId;
	}

	public String getSIssuerBankName() {
		return SIssuerBankName;
	}
//
//	public String getBillHistStation() {
//		return billHistStation;
//	}
//
//	public void setBillHistStation(String billHistStation) {
//		this.billHistStation = billHistStation;
//	}
	
}