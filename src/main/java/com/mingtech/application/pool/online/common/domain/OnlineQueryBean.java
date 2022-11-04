package com.mingtech.application.pool.online.common.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;

public class OnlineQueryBean {
	private String id;
	//在线银承
	private String bpsId;                //票据池协议表ID       
	private String bpsNo;                //票据池协议号         
	private String bpsName;              //票据池名称          
	private String custNumber;           //客户号            
	private String custOrgcode;          //客户组织机构代码       
	private String custName;             //客户名称           
	private String protocolStatus;       //在线协议状态         
	private String onlineAcptNo;         //在线银承编号         
	private String baseCreditNo;         //基本授信额度编号       
	private String ebkCustNo;            //网银客户号          
	private BigDecimal onlineAcptTotal;  //在线银承总额         
	private String acceptorBankNo;       //承兑人承兑行行号       
	private String acceptorBankName;     //承兑人承兑行名称       
	private String depositAcctNo;        //扣收保证金账号        
	private String depositAcctName;      //扣收保证金账户名称 
	private String depositBranchNo;           //扣收保证金账户开户行
	private String depositBranchName;         //扣收保证金账户开户行名称
	private String depositRateLevel;     //保证金利率档次        
	private String depositRateFloatFlag; //保证金利率浮动标志      
	private BigDecimal depositRateFloatValue;//保证金利率浮动值       
	private BigDecimal depositRatio;         //保证金比例（%）       
	private BigDecimal poolCreditRatio;      //票据池额度占用比例（%）   
	private BigDecimal feeRate;              //手续费率（%）        
	private String inAcctBranchNo;       //入账机构所号         
	private String inAcctBranchName;     //入账机构名称         
	private String contractNo;           //合同编号         
	private String guarantor;            //担保人名称          
	private String guarantorNo;          //担保人客户号          
	private String appName;              //经办人名称          
	private String appNo;                //经办人编号          
	private String signBranchNo;         //签约机构所号         
	private String signBranchName;       //签约机构名称         
	private Date openDate;               //开通日期           
	private Date changeDate;             //变更日期           
	private String modeType;             //修改标识
	private String fileName;             //文件名称
	private String filePath;			 //文件路径
	private String delimitor;			 //分隔符
	private String billNo;        //票号
	private String acptBatchId;   //批次id
	private BigDecimal billAmt;   //票面金额
	private String issuerName;    //出票人名称
	private String issuerAcct;    //出票人账号
	private String issuerBankCode;//出票人开户行行号
	private String issuerBankName;//出票人开户行名称
	private String issuerOrgcode; //出票人组织机构代码
	private String payeeName;     //收票人名称
	private String payeeAcct;     //收票人账号
	private String payeeBankCode; //收票人开户行行号
	private String payeeBankName; //收票人开户行名称
	private BigDecimal payeeTotalAmt;//收票人收票总额
	private BigDecimal payeeUsedAmt;//收票人已收票总额
	private BigDecimal payeeFreeAmt;//收票人可用金额

	private Date isseDate;        //出票日
	private Date dueDate;         //到期日
	private String acptBankName;  //承兑行行名
	private String acptBankCode;  //承兑行行号
	private String limitType;     //期限方式
	private String transferFlag;  //是否可转让
	private String isAutoCallPyee;//是否联动收票人自动收票
	private String operationType; //操作类型
	private String msgStatus;     //报文状态 未处理  待回复  处理成功
	private Date createTime;      //创建时间
	private Date updateTime;      //最近修改时间
	private String lastSourceId;//上一次修改的id
	private String applyBankNo;               //申请人开户行行号
	private String applyBankName;             //申请人开户行行名
	
	//在线流贷
	private String onlineCrdtNo;           //在线流贷编号
	private BigDecimal onlineLoanTotal;    //在线流贷总额
	private String baseRateType;           //基准利率类型
	private String rateFloatType;          //利率浮动方式
	private BigDecimal rateFloatValue;     //利率浮动值（%）
	private String overRateFloatType;      //逾期利率浮动方式
	private BigDecimal overRateFloatValue; //逾期利率浮动值（%）
	private String makeLoanType;           //放款方式
	private String repaymentType;          //还款方式
	private String isAutoDeduct;           //是否自动扣划本息
	private String isDiscInterest;         //是否贴息
	private String loanAcctNo;             //放款账户账号
	private String loanAcctName;         //放款账户名称
	private String deduAcctNo;             //扣款账户账号
	private String deduAcctName;           //扣款账户名称
	private String payType;           //支付方式
	private BigDecimal loanAmt;           //借据金额
	private BigDecimal lprRate;     //基准利率（LPR利率）

	
	//收票人
	private String payeeId;          //收票人编号    
	private String payeeAcctName;    //收票人名称    
	private String payeeCustName;	//收票人客户名称
	private String payeeAcctNo;      //收票人账号    
	private String payeeOpenBankNo;  //收票人开户行行号 
	private String payeeOpenBankName;//收票人开户行名称 
	private String payeeStatus;      //收票人状态
	private String acptId;
	private String crdtId;
	
	//短信
	private String addresseeNo;       // 联系人编号 
	private String addresseeName;     // 联系人名称 
	private String addresseeRole;     // 联系人身份 
	private String addresseePhoneNo;  // 联系人电话 
	private String onlineProtocolType;// 在线协议类型
	private String onlineProtocolTypeDesc;// 在线协议类型
	private String onlineNo;          // 在线协议编号
	private String onlineProtocolId;  // 在线协议主键
	
	//支付计划
	private String loanNo;        //借据编号
	private String deduBankCode;  //收款人开户行行号
	private String deduBankName;  //收款人开户行名称
	private BigDecimal unusedAmt; //可用金额
	private BigDecimal repayAmt;  //还款金额
	private String status;        //状态
	private String statusDesc;        //状态
	private String operatorType;  //操作
	private Date createDate;      //创建时间
	private String payeeSerialNo; //支付编号
	private boolean isEntrustedPay;//受托支付
	private boolean isSelfPay;     //自主支付
	private String serialNo;      //序列号
	private String payPlanId;      //支付计划id
	private String isLocal;      //是否跨行

	
	private String bbspAcctNo;       //电票签约账号
	private String elctrncSign;      //电子签名
	private Date startDate;          //开始日期
	private Date endDate;			 //结束日期
	private Date dueStartDate;       //开始日期
	private Date dueEndDate;		 //结束日期
	private BigDecimal startAmt;		   //开始金额
	private BigDecimal endAmt;			   //结束金额
	private String poolMode;               //额度模式 
	private String currency;               //币种
	private Date effectiveDate;          //生效日期
	private String businessSum; //合同金额
	private String onlineLoanNo ;   //在线业务合同号
	private BigDecimal totalAmt;//金额
	private BigDecimal UsedAmt;
	private BigDecimal availableAmt; //可用金额
	private String billId;//票据id      
	private String batchId;
	private BigDecimal lowRiskAmt;//低风险额度
	private BigDecimal protocolAmt;//协议可用额度
	private BigDecimal guaranteeAmt;//低风险的
	private String dealStatus;    //业务处理状态  
	private String dealStatusDesc;    //业务处理状态 
	private String modeMark;//唯一标记
	private Timestamp openTime;//开通时间
	private String onlineProtocolNo;
	private String busiId;//业务Id
	private String ids;//
	private String modeContent;//修改内容
	private String custNo;//客户号
	private String deptId;//机构id
	
	private List detalis = new ArrayList();
	private List list = new ArrayList();
	private List payees = new ArrayList();
	private List statuList = new ArrayList();
	private Map map = new HashMap();
	
	private PlOnlineAcptBatch acptBatch;//在线银承批次对象
	private PlOnlineCrdt crdtBatch;//在线流贷批次对象
	private List<PlOnlineCrdt> crdtBatchList;//在线流贷批次对象列表
	private List<PlCrdtPayPlan> payList;//支付计划列表
	private CreditProduct product;//主业务合同对象
	private List<PedCreditDetail> crdtDetailList;//借据明细对象
	
	private PedOnlineAcptProtocol acptPro;//银承主协议对象
	private PedOnlineCrdtProtocol crdtPro;//流贷主协议对象
	private PedProtocolDto pool;
	private String flowNo;//流水号
	private String type;//类型
	private String typeName;//类型
	
	private String transAccount;//贷款账号/业务保证金账号
	private String checked;
	private Date taskDateStart; //调度时间开始
	private Date taskDateEnd; //调度时间结束

	
	public String getPayeeCustName() {
		return payeeCustName;
	}
	public void setPayeeCustName(String payeeCustName) {
		this.payeeCustName = payeeCustName;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public Date getTaskDateStart() {
		return taskDateStart;
	}
	public void setTaskDateStart(Date taskDateStart) {
		this.taskDateStart = taskDateStart;
	}
	public Date getTaskDateEnd() {
		return taskDateEnd;
	}
	public void setTaskDateEnd(Date taskDateEnd) {
		this.taskDateEnd = taskDateEnd;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public BigDecimal getLprRate() {
		return lprRate;
	}
	public void setLprRate(BigDecimal lprRate) {
		this.lprRate = lprRate;
	}
	public PedProtocolDto getPool() {
		return pool;
	}
	public void setPool(PedProtocolDto pool) {
		this.pool = pool;
	}
	public PedOnlineAcptProtocol getAcptPro() {
		return acptPro;
	}
	public void setAcptPro(PedOnlineAcptProtocol acptPro) {
		this.acptPro = acptPro;
	}
	public PedOnlineCrdtProtocol getCrdtPro() {
		return crdtPro;
	}
	public void setCrdtPro(PedOnlineCrdtProtocol crdtPro) {
		this.crdtPro = crdtPro;
	}
	public String getTransAccount() {
		return transAccount;
	}
	public void setTransAccount(String transAccount) {
		this.transAccount = transAccount;
	}
	public BigDecimal getPayeeFreeAmt() {
		return payeeFreeAmt;
	}
	public void setPayeeFreeAmt(BigDecimal payeeFreeAmt) {
		this.payeeFreeAmt = payeeFreeAmt;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAcptId() {
		return acptId;
	}
	public void setAcptId(String acptId) {
		this.acptId = acptId;
	}
	public String getOnlineProtocolTypeDesc() {
		if(PublicStaticDefineTab.PRODUCT_001.equals(this.onlineProtocolType) || PublicStaticDefineTab.PRODUCT_YC.equals(this.onlineProtocolType)){
			return "在线银承";
		}else if(PublicStaticDefineTab.PRODUCT_002.equals(this.onlineProtocolType) || PublicStaticDefineTab.PRODUCT_LD.equals(this.onlineProtocolType)){
			return "在线流贷";
		}else{
			return onlineProtocolTypeDesc;
		}
	}
	public void setOnlineProtocolTypeDesc(String onlineProtocolTypeDesc) {
		this.onlineProtocolTypeDesc = onlineProtocolTypeDesc;
	}
	
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public BigDecimal getUsedAmt() {
		return UsedAmt;
	}
	public void setUsedAmt(BigDecimal usedAmt) {
		UsedAmt = usedAmt;
	}
	public String getOnlineProtocolNo() {
		return onlineProtocolNo;
	}
	public void setOnlineProtocolNo(String onlineProtocolNo) {
		this.onlineProtocolNo = onlineProtocolNo;
	}
	public Timestamp getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Timestamp openTime) {
		this.openTime = openTime;
	}
	public String getModeMark() {
		return modeMark;
	}
	public void setModeMark(String modeMark) {
		this.modeMark = modeMark;
	}
	public void setLoanAcctName(String loanAcctName) {
		this.loanAcctName = loanAcctName;
	}
	public List getStatuList() {
		return statuList;
	}
	public void setStatuList(List statuList) {
		this.statuList = statuList;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public boolean isEntrustedPay() {
		return isEntrustedPay;
	}
	public void setEntrustedPay(boolean isEntrustedPay) {
		this.isEntrustedPay = isEntrustedPay;
	}
	public boolean isSelfPay() {
		return isSelfPay;
	}
	public void setSelfPay(boolean isSelfPay) {
		this.isSelfPay = isSelfPay;
	}
	public BigDecimal getLowRiskAmt() {
		return lowRiskAmt;
	}
	public void setLowRiskAmt(BigDecimal lowRiskAmt) {
		this.lowRiskAmt = lowRiskAmt;
	}
	public BigDecimal getProtocolAmt() {
		return protocolAmt;
	}
	public void setProtocolAmt(BigDecimal protocolAmt) {
		this.protocolAmt = protocolAmt;
	}
	public BigDecimal getGuaranteeAmt() {
		return guaranteeAmt;
	}
	public void setGuaranteeAmt(BigDecimal guaranteeAmt) {
		this.guaranteeAmt = guaranteeAmt;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getOnlineLoanNo() {
		return onlineLoanNo;
	}
	public void setOnlineLoanNo(String onlineLoanNo) {
		this.onlineLoanNo = onlineLoanNo;
	}
	public String getPayeeSerialNo() {
		return payeeSerialNo;
	}
	public void setPayeeSerialNo(String payeeSerialNo) {
		this.payeeSerialNo = payeeSerialNo;
	}
	public String getLoanNo() {
		return loanNo;
	}
	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
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
	public BigDecimal getUnusedAmt() {
		return unusedAmt;
	}
	public void setUnusedAmt(BigDecimal unusedAmt) {
		this.unusedAmt = unusedAmt;
	}
	public BigDecimal getRepayAmt() {
		return repayAmt;
	}
	public void setRepayAmt(BigDecimal repayAmt) {
		this.repayAmt = repayAmt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOperatorType() {
		return operatorType;
	}
	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}
	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}
	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		this.map = map;
	}
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
	public String getElctrncSign() {
		return elctrncSign;
	}
	public void setElctrncSign(String elctrncSign) {
		this.elctrncSign = elctrncSign;
	}
	public String getBbspAcctNo() {
		return bbspAcctNo;
	}
	public void setBbspAcctNo(String bbspAcctNo) {
		this.bbspAcctNo = bbspAcctNo;
	}
	public List getDetalis() {
		return detalis;
	}
	public void setDetalis(List detalis) {
		this.detalis = detalis;
	}
	public String getBpsId() {
		return bpsId;
	}
	public void setBpsId(String bpsId) {
		this.bpsId = bpsId;
	}
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getBpsName() {
		return bpsName;
	}
	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}
	public String getCustNumber() {
		return custNumber;
	}
	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}
	public String getCustOrgcode() {
		return custOrgcode;
	}
	public void setCustOrgcode(String custOrgcode) {
		this.custOrgcode = custOrgcode;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getProtocolStatus() {
		return protocolStatus;
	}
	public void setProtocolStatus(String protocolStatus) {
		this.protocolStatus = protocolStatus;
	}
	public String getOnlineAcptNo() {
		return onlineAcptNo;
	}
	public void setOnlineAcptNo(String onlineAcptNo) {
		this.onlineAcptNo = onlineAcptNo;
	}
	public String getBaseCreditNo() {
		return baseCreditNo;
	}
	public void setBaseCreditNo(String baseCreditNo) {
		this.baseCreditNo = baseCreditNo;
	}
	public String getEbkCustNo() {
		return ebkCustNo;
	}
	public void setEbkCustNo(String ebkCustNo) {
		this.ebkCustNo = ebkCustNo;
	}
	public BigDecimal getOnlineAcptTotal() {
		return onlineAcptTotal;
	}
	public void setOnlineAcptTotal(BigDecimal onlineAcptTotal) {
		this.onlineAcptTotal = onlineAcptTotal;
	}
	public String getAcceptorBankNo() {
		return acceptorBankNo;
	}
	public void setAcceptorBankNo(String acceptorBankNo) {
		this.acceptorBankNo = acceptorBankNo;
	}
	public String getAcceptorBankName() {
		return acceptorBankName;
	}
	public void setAcceptorBankName(String acceptorBankName) {
		this.acceptorBankName = acceptorBankName;
	}
	public String getDepositAcctNo() {
		return depositAcctNo;
	}
	public void setDepositAcctNo(String depositAcctNo) {
		this.depositAcctNo = depositAcctNo;
	}
	public String getDepositAcctName() {
		return depositAcctName;
	}
	public void setDepositAcctName(String depositAcctName) {
		this.depositAcctName = depositAcctName;
	}
	public String getDepositRateLevel() {
		return depositRateLevel;
	}
	public void setDepositRateLevel(String depositRateLevel) {
		this.depositRateLevel = depositRateLevel;
	}
	public String getDepositRateFloatFlag() {
		return depositRateFloatFlag;
	}
	public void setDepositRateFloatFlag(String depositRateFloatFlag) {
		this.depositRateFloatFlag = depositRateFloatFlag;
	}
	public BigDecimal getDepositRateFloatValue() {
		return depositRateFloatValue;
	}
	public void setDepositRateFloatValue(BigDecimal depositRateFloatValue) {
		this.depositRateFloatValue = depositRateFloatValue;
	}
	public BigDecimal getDepositRatio() {
		return depositRatio;
	}
	public void setDepositRatio(BigDecimal depositRatio) {
		this.depositRatio = depositRatio;
	}
	public BigDecimal getPoolCreditRatio() {
		return poolCreditRatio;
	}
	public void setPoolCreditRatio(BigDecimal poolCreditRatio) {
		this.poolCreditRatio = poolCreditRatio;
	}
	public BigDecimal getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}
	public String getInAcctBranchNo() {
		return inAcctBranchNo;
	}
	public void setInAcctBranchNo(String inAcctBranchNo) {
		this.inAcctBranchNo = inAcctBranchNo;
	}
	public String getInAcctBranchName() {
		return inAcctBranchName;
	}
	public void setInAcctBranchName(String inAcctBranchName) {
		this.inAcctBranchName = inAcctBranchName;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getGuarantor() {
		return guarantor;
	}
	public void setGuarantor(String guarantor) {
		this.guarantor = guarantor;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppNo() {
		return appNo;
	}
	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}
	public String getSignBranchNo() {
		return signBranchNo;
	}
	public void setSignBranchNo(String signBranchNo) {
		this.signBranchNo = signBranchNo;
	}
	public String getSignBranchName() {
		return signBranchName;
	}
	public void setSignBranchName(String signBranchName) {
		this.signBranchName = signBranchName;
	}
	public Date getOpenDate() {
		return openDate;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	public Date getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public String getGuarantorNo() {
		return guarantorNo;
	}
	public void setGuarantorNo(String guarantorNo) {
		this.guarantorNo = guarantorNo;
	}
	public String getModeType() {
		return modeType;
	}
	public void setModeType(String modeType) {
		this.modeType = modeType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getDelimitor() {
		return delimitor;
	}
	public void setDelimitor(String delimitor) {
		this.delimitor = delimitor;
	}
	public List getPayees() {
		return payees;
	}
	public void setPayees(List payees) {
		this.payees = payees;
	}
	public String getOnlineCrdtNo() {
		return onlineCrdtNo;
	}
	public void setOnlineCrdtNo(String onlineCrdtNo) {
		this.onlineCrdtNo = onlineCrdtNo;
	}
	public BigDecimal getOnlineLoanTotal() {
		return onlineLoanTotal;
	}
	public void setOnlineLoanTotal(BigDecimal onlineLoanTotal) {
		this.onlineLoanTotal = onlineLoanTotal;
	}
	public String getBaseRateType() {
		return baseRateType;
	}
	public void setBaseRateType(String baseRateType) {
		this.baseRateType = baseRateType;
	}
	public String getRateFloatType() {
		return rateFloatType;
	}
	public void setRateFloatType(String rateFloatType) {
		this.rateFloatType = rateFloatType;
	}
	public BigDecimal getRateFloatValue() {
		return rateFloatValue;
	}
	public void setRateFloatValue(BigDecimal rateFloatValue) {
		this.rateFloatValue = rateFloatValue;
	}
	public String getOverRateFloatType() {
		return overRateFloatType;
	}
	public void setOverRateFloatType(String overRateFloatType) {
		this.overRateFloatType = overRateFloatType;
	}
	public BigDecimal getOverRateFloatValue() {
		return overRateFloatValue;
	}
	public void setOverRateFloatValue(BigDecimal overRateFloatValue) {
		this.overRateFloatValue = overRateFloatValue;
	}
	public String getMakeLoanType() {
		return makeLoanType;
	}
	public void setMakeLoanType(String makeLoanType) {
		this.makeLoanType = makeLoanType;
	}
	public String getRepaymentType() {
		return repaymentType;
	}
	public void setRepaymentType(String repaymentType) {
		this.repaymentType = repaymentType;
	}
	public String getIsAutoDeduct() {
		return isAutoDeduct;
	}
	public void setIsAutoDeduct(String isAutoDeduct) {
		this.isAutoDeduct = isAutoDeduct;
	}
	public String getIsDiscInterest() {
		return isDiscInterest;
	}
	public void setIsDiscInterest(String isDiscInterest) {
		this.isDiscInterest = isDiscInterest;
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
	public void setLoanerAcctName(String loanAcctName) {
		this.loanAcctName = loanAcctName;
	}
	public String getDeduAcctNo() {
		return deduAcctNo;
	}
	public void setDeduAcctNo(String deduAcctNo) {
		this.deduAcctNo = deduAcctNo;
	}
	public String getDeduAcctName() {
		return deduAcctName;
	}
	public void setDeduAcctName(String deduAcctName) {
		this.deduAcctName = deduAcctName;
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
	public BigDecimal getStartAmt() {
		return startAmt;
	}
	public void setStartAmt(BigDecimal startAmt) {
		this.startAmt = startAmt;
	}
	public BigDecimal getEndAmt() {
		return endAmt;
	}
	public void setEndAmt(BigDecimal endAmt) {
		this.endAmt = endAmt;
	}

	public String getPayeeId() {
		return payeeId;
	}
	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}
	public String getPayeeAcctName() {
		return payeeAcctName;
	}
	public void setPayeeAcctName(String payeeAcctName) {
		this.payeeAcctName = payeeAcctName;
	}
	public String getPayeeAcctNo() {
		return payeeAcctNo;
	}
	public void setPayeeAcctNo(String payeeAcctNo) {
		this.payeeAcctNo = payeeAcctNo;
	}
	public String getPayeeOpenBankNo() {
		return payeeOpenBankNo;
	}
	public void setPayeeOpenBankNo(String payeeOpenBankNo) {
		this.payeeOpenBankNo = payeeOpenBankNo;
	}
	public String getPayeeOpenBankName() {
		return payeeOpenBankName;
	}
	public void setPayeeOpenBankName(String payeeOpenBankName) {
		this.payeeOpenBankName = payeeOpenBankName;
	}
	public String getPayeeStatus() {
		return payeeStatus;
	}
	public void setPayeeStatus(String payeeStatus) {
		this.payeeStatus = payeeStatus;
	}
	public String getAddresseeNo() {
		return addresseeNo;
	}
	public void setAddresseeNo(String addresseeNo) {
		this.addresseeNo = addresseeNo;
	}
	public String getAddresseeName() {
		return addresseeName;
	}
	public void setAddresseeName(String addresseeName) {
		this.addresseeName = addresseeName;
	}
	public String getAddresseeRole() {
		return addresseeRole;
	}
	public void setAddresseeRole(String addresseeRole) {
		this.addresseeRole = addresseeRole;
	}
	public String getAddresseePhoneNo() {
		return addresseePhoneNo;
	}
	public void setAddresseePhoneNo(String addresseePhoneNo) {
		this.addresseePhoneNo = addresseePhoneNo;
	}
	public String page() {
		return onlineProtocolType;
	}
	public void setOnlineProtocolType(String onlineProtocolType) {
		this.onlineProtocolType = onlineProtocolType;
	}
	
	public String getOnlineProtocolType() {
		return onlineProtocolType;
	}
	public String getOnlineNo() {
		return onlineNo;
	}
	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}
	public String getOnlineProtocolId() {
		return onlineProtocolId;
	}
	public void setOnlineProtocolId(String onlineProtocolId) {
		this.onlineProtocolId = onlineProtocolId;
	}
	public String getPoolMode() {
		return poolMode;
	}
	public void setPoolMode(String poolMode) {
		this.poolMode = poolMode;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getBusinessSum() {
		return businessSum;
	}
	public void setBusinessSum(String businessSum) {
		this.businessSum = businessSum;
	}
	public String getAcptBatchId() {
		return acptBatchId;
	}
	public void setAcptBatchId(String acptBatchId) {
		this.acptBatchId = acptBatchId;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public BigDecimal getBillAmt() {
		return billAmt;
	}
	public void setBillAmt(BigDecimal billAmt) {
		this.billAmt = billAmt;
	}
	public String getIssuerName() {
		return issuerName;
	}
	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
	public String getIssuerAcct() {
		return issuerAcct;
	}
	public void setIssuerAcct(String issuerAcct) {
		this.issuerAcct = issuerAcct;
	}
	public String getIssuerBankCode() {
		return issuerBankCode;
	}
	public void setIssuerBankCode(String issuerBankCode) {
		this.issuerBankCode = issuerBankCode;
	}
	public String getIssuerBankName() {
		return issuerBankName;
	}
	public void setIssuerBankName(String issuerBankName) {
		this.issuerBankName = issuerBankName;
	}
	public String getIssuerOrgcode() {
		return issuerOrgcode;
	}
	public void setIssuerOrgcode(String issuerOrgcode) {
		this.issuerOrgcode = issuerOrgcode;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getPayeeAcct() {
		return payeeAcct;
	}
	public void setPayeeAcct(String payeeAcct) {
		this.payeeAcct = payeeAcct;
	}
	public String getPayeeBankCode() {
		return payeeBankCode;
	}
	public void setPayeeBankCode(String payeeBankCode) {
		this.payeeBankCode = payeeBankCode;
	}
	public String getPayeeBankName() {
		return payeeBankName;
	}
	public void setPayeeBankName(String payeeBankName) {
		this.payeeBankName = payeeBankName;
	}
	public Date getIsseDate() {
		return isseDate;
	}
	public void setIsseDate(Date isseDate) {
		this.isseDate = isseDate;
	}
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
	public String getLimitType() {
		return limitType;
	}
	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}
	public String getTransferFlag() {
		return transferFlag;
	}
	public void setTransferFlag(String transferFlag) {
		this.transferFlag = transferFlag;
	}
	public String getIsAutoCallPyee() {
		return isAutoCallPyee;
	}
	public void setIsAutoCallPyee(String isAutoCallPyee) {
		this.isAutoCallPyee = isAutoCallPyee;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public String getMsgStatus() {
		return msgStatus;
	}
	public void setMsgStatus(String msgStatus) {
		this.msgStatus = msgStatus;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getLastSourceId() {
		return lastSourceId;
	}
	public void setLastSourceId(String lastSourceId) {
		this.lastSourceId = lastSourceId;
	}
	public String getDealStatus() {
		return dealStatus;
	}
	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}
	public Date getDueStartDate() {
		return dueStartDate;
	}
	public void setDueStartDate(Date dueStartDate) {
		this.dueStartDate = dueStartDate;
	}
	public Date getDueEndDate() {
		return dueEndDate;
	}
	public void setDueEndDate(Date dueEndDate) {
		this.dueEndDate = dueEndDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCrdtId() {
		return crdtId;
	}
	public void setCrdtId(String crdtId) {
		this.crdtId = crdtId;
	}
	public String getDealStatusDesc() {
		return dealStatusDesc;
	}
	public void setDealStatusDesc(String dealStatusDesc) {
		this.dealStatusDesc = dealStatusDesc;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayPlanId() {
		return payPlanId;
	}
	public void setPayPlanId(String payPlanId) {
		this.payPlanId = payPlanId;
	}
	public String getBusiId() {
		return busiId;
	}
	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getModeContent() {
		return modeContent;
	}
	public void setModeContent(String modeContent) {
		this.modeContent = modeContent;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public BigDecimal getLoanAmt() {
		return loanAmt;
	}
	public void setLoanAmt(BigDecimal loanAmt) {
		this.loanAmt = loanAmt;
	}
	public PlOnlineAcptBatch getAcptBatch() {
		return acptBatch;
	}
	public void setAcptBatch(PlOnlineAcptBatch acptBatch) {
		this.acptBatch = acptBatch;
	}
	public CreditProduct getProduct() {
		return product;
	}
	public void setProduct(CreditProduct product) {
		this.product = product;
	}
	public List<PedCreditDetail> getCrdtDetailList() {
		return crdtDetailList;
	}
	public void setCrdtDetailList(List<PedCreditDetail> crdtDetailList) {
		this.crdtDetailList = crdtDetailList;
	}
	public PlOnlineCrdt getCrdtBatch() {
		return crdtBatch;
	}
	public void setCrdtBatch(PlOnlineCrdt crdtBatch) {
		this.crdtBatch = crdtBatch;
	}
	public List<PlOnlineCrdt> getCrdtBatchList() {
		return crdtBatchList;
	}
	public void setCrdtBatchList(List<PlOnlineCrdt> crdtBatchList) {
		this.crdtBatchList = crdtBatchList;
	}
	public List<PlCrdtPayPlan> getPayList() {
		return payList;
	}
	public void setPayList(List<PlCrdtPayPlan> payList) {
		this.payList = payList;
	}
	public BigDecimal getPayeeTotalAmt() {
		return payeeTotalAmt;
	}
	public void setPayeeTotalAmt(BigDecimal payeeTotalAmt) {
		this.payeeTotalAmt = payeeTotalAmt;
	}
	public BigDecimal getPayeeUsedAmt() {
		return payeeUsedAmt;
	}
	public void setPayeeUsedAmt(BigDecimal payeeUsedAmt) {
		this.payeeUsedAmt = payeeUsedAmt;
	}
	public String getIsLocal() {
		return isLocal;
	}
	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}
	public String getDepositBranchNo() {
		return depositBranchNo;
	}
	public void setDepositBranchNo(String depositBranchNo) {
		this.depositBranchNo = depositBranchNo;
	}
	public String getDepositBranchName() {
		return depositBranchName;
	}
	public void setDepositBranchName(String depositBranchName) {
		this.depositBranchName = depositBranchName;
	}
	public String getApplyBankNo() {
		return applyBankNo;
	}
	public void setApplyBankNo(String applyBankNo) {
		this.applyBankNo = applyBankNo;
	}
	public String getApplyBankName() {
		return applyBankName;
	}
	public void setApplyBankName(String applyBankName) {
		this.applyBankName = applyBankName;
	}
	public String getFlowNo() {
		return flowNo;
	}
	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}
	
	
	

}
