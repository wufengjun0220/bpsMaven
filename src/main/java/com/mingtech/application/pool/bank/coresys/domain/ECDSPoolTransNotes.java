package com.mingtech.application.pool.bank.coresys.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 电票系统接口交易 实体
 * 
 */
public class ECDSPoolTransNotes {

	public static final String IS_OPEN_CORE = "1";   //1 是使用票据池核心接口，0 为不使用
	// -----------------公共----------
	private String billId;// 票据id
	private String batchNo;// 批次号
	private String reserve1;// 预留字段
	private String signature;// 电子签名
	private int totalRows;// 总行数
	private int totalPages;// 总页数
	private int pageSize;// 每页显示行数
	private int currentPage;// 当前页号
	private String forbidFlag;// 禁止背书标记
	private String applyDt;// 提示付款申请日期(背书日期)

	private String fileName;// 文件名称
	private String filePath;// 文件相对路径

	// ----------出票人信息-----Start-----------------------
	private String remitter;// 出票人全称
	private String remitterAcctNo;// 出票人账号
	private String remitterBankNo;// 出票人开户行行号
	private String remitterBankName;// 出票人开户行行名
	// ----------出票人信息-----end-----------------------

	// ----------承兑人信息-----Start-----------------------
	private String acceptorAcctNo;// 承兑人账号
	private String acceptorBankNo;// 承兑人开户行行号
	private String acceptorBankName;// 承兑人开户行名称
	private String acceptor;// 承兑人全称
	// ----------承兑人信息-----end-----------------------

	// ----------收款人信息-----Start-----------------------
	private String payee;// 收款人全称
	private String payeeAcctNo;// 收款人账号
	private String payeeBankName;// 收款人开户行行名
	private String payeeBankNo;// 收款人开户行行号
	// ----------收款人信息-----end-----------------------

	// ----------票据质押、解质押-----Start-----------------------
	private String applicantAcctNo;// 客户账号(电子票据信息共用->申请人账号)
	private String receiverName;// 质权人名称 经办行名称
	private String receiverAcctNo;// 质权人帐号 0
	private String receiverBankNo;// 质权人开户行行号 业务经办行行号
	private String ifInPool;// 是否入池
	// ----------票据质押、解质押-----end-----------------------

	// ----------提示付款申请-----Start-----------------------
	private String branchNo;// 机构号
	private String overdueReason;// 逾期原因说明
	private String remark;// 提示付款备注
	private String onlineMark;// 线上清算标记
	// ----------提示付款申请-----end-----------------------

	// ----------票据正面信息-----Start-----------------------
	private String billMoney;// 票据金额
	private String remitterCreditRatgs;// 出票人信用等级
	private String remitterRatgDuedt;// 出票人评级到期日
	private String remitterAssuName;// 出票保证人名称
	private String remitterAssuAddr;// 出票保证人地址
	private String remitterAssuDt;// 出票保证日期
	private String acceptorAssuName;// 承兑保证人名称
	private String acceptorAssuAddr;// 承兑保证人地址
	private String acceptorAssuDt;// 承兑保证日期
	private String status;// 票据状态 (电子票据信息共用,电子票据交易用于交易状态)
	// ----------票据正面信息-----end-----------------------

	// ----------票据背面信息-----start-----------------------
	private String transNo;// 交易类型(电子票据信息共用)
	private String name;// 背书人名称、电子票据交易交易发起方全称、质押待签收的申请方名称
	private String toName;// 被背书人名称、电子票据交易交易接收方全称
	private String finishDt;// 签收日期
	private String backOpenDt;// 赎回开放日
	private String backEndDt;// 赎回截止日
	private String assuSignerAddr;// 保证人地址
	private String presentationFlag;// 付款或拒付标志
	private String toRemark;// 拒付理由、电子票据交易的签收方备注   
	private String recourseType;// 追索类型
	// ----------票据背面信息-----end-----------------------

	// ----------电子票据交易信息-----start-----------------------
	private String billNo;// 票号
	
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String billOriginCode;//票据来源
	private String CorpApplyTransCode;//交易编号
	private String dataSource;//渠道来源
	private String billSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String hldrId;//持有id
	
	private String maxBeginRangeNo;//最大子票区间起始
	private String minBeginRangeNo;//最小子票区间起始
	private String maxEndRangeNo;//最大子票区间截至
	private String minEndRangeNo;//最小子票区间截至

	/*** 融合改造新增字段  end*/
	
	
	private String startTransDt;// 交易开始日期
	private String endTransDt;// 交易结束日期
	private String minStartTransDt;// 最小交易开始日期
	private String maxStartTransDt;// 最大交易开始日期
	private String minEndTransDt;// 最小交易结束日期
	private String maxEndTransDt;// 最大交易结束日期
	private String transType;// 业务分类
	private String signFlag;// 签收标记
	private String XTransNo;// 伪交易编号
	private String statusCode;// 交易结果
	private String transId;// 交易ID
	private String maxAcptDt;// 最大出票日
	private String minAcptDt;// 最小出票日
	private String billType;// 票据类型
	private String dueDt;// 到期日
	private String acptDt;// 出票日
	private String trancode;//交易码
	
	private String transName      ;//交易名称          
	private String prodName       ;//产品名称          
	private String assuType       ;//保证类型          
	private String acctNo         ;//交易发起方帐号    、质押待签收的申请方账号
	private String bankNo         ;//交易发起方行号    、质押待签收的申请方行号
	private String toAcctNo       ;//交易接收方帐号    
	private String toBankNo       ;//交易接收方行号    
	private String rate           ;//利率              
	private String actuMoney      ;//实付金额          
	private String rateEndDt      ;//计息到期日        
	private String backRate       ;//赎回利率          
	private String backActuMoney  ;//赎回实付金额      
	private String aoAcctNo       ;//入账账号          
	private String aoBankNo       ;//入账行号          
	private String signer         ;//签收人全称        
	private String operNo         ;//操作员号          
	private String isLock         ;//是否锁定          
	private String obligeeAcctNo  ;//当前票据权利人账号
	private String obligeeBankNo  ;//当前票据权利人行号
	private String obligeeCustName;//当前票据权利人名称
	private String haveAcctNo     ;//当前票据持有人账号
	private String haveBankNo     ;//当前票据持有人行号
	private String haveCustName   ;//当前票据持有人名称
	private String Remark         ;//申请方备注        
	private String returnCode     ;//返回码            
	private String returnMsg      ;//返回信息          
	// ----------电子票据交易信息-----end-----------------------
	
	//----------------查询、质押待签收--------start-----------------
	private String conferNo        ;//合同文本号                     
	private String invoiceNo       ;//发票号码                       
	private String bankName        ;//申请方行名                     
	private String orgNo           ;//申请方组织机构代码             
	private String partnerType     ;//申请方类型                     
	//----------------质押待签收--------end-----------------

	private String maxBillMoney    ;//金额上限
	private String minBillMoney    ;//金额下限
	private String maxDueDt        ;//到期日上限
	private String minDueDt        ;//到期日下限

	private String SignUpMark;//签收意见
	private String RejectCode;//拒付代码
	
	//----------------贴现签收及记账接口--------start-----------------
	private String iouNo;//借据号
	private String addInterst;//加息天数 
	private String ccy;//币种
	private String payIntMode;//贴现付息方式
	private String exeIntRate;//执行利率
	private String intRecvAmt;//应收利息
	private String guaranteeNo;//担保编号
	private String intersetAccNo;//付息账号
	private String intersetAccNoName;//付息账号名称
	private String cancelAccNo;//销账编号
	private String loanAcctNo;//放款账号
	private String loanAcctNoName;//放款账号名称
	private String loanOpenBank;//放款账号开户行行号
	private String cllentNo;//客户号
	private String cllentNoName;//客户名称
	private String auditStatus;//审核状态
	private String discountIntRate;//贴现利率
	private String discountDate;//贴现日期
	private String redeemOpemDate;//赎回开放日期
	private String redeenEndDate;//赎回截止日期
	private String redeemIntRate;//赎回利率
	private String redeemAmt;//赎回金额
	private String discountInBankCode;//帖入行行号
	private String discountInBankName;//帖入行名称
	private String discountInAcctNo;//帖入账号
	private String enterAcctNo;//入账账号
	private String enterBankCode;//入账行号
	private String end;//背书转让标记
	private String operationType;//操作类型
	private String thirdPayRate;//第三方付息比例
	private String thirdAcctNo;//第三方账号
	private String thridAcctName;//第三方账号名称
	private String thridOpenBranch;//第三方开户行行号
	private String remitterBatch;//出票机构
	private String marginAccount;//保证金账号
	//----------------贴现签收及记账接口--------end-----------------
	
	//--------------------------出票登记---------------------------
	private String contractNo;//合同号
	private String isAutoRecvFlag;//自动签收
	private Date tradeDate;//交易日期
	private String acptProtocolNo;//承兑协议编号
	private String deduDepositAcctNo;//扣款保证金账户
	private String depositAcctNo;//保证金账户
	private String inAcctBranch;//出账机构
	private BigDecimal depositRate;//出账机构
	private String rateType;//出账机构
	private String intFluctuationMode;//出账机构
	private BigDecimal floatIntRate;//出账机构
	private String feeFlag;//出账机构
	private String feeMode;//出账机构
	private String feeAcctNO;//出账机构
	private BigDecimal FEE_RATE;//出账机构
	private String poolDepositAcctNo;//
	
	private String custNo;//客户经理号
	private String custName;//客户经理号
	
	
	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCllentNoName() {
		return cllentNoName;
	}

	public void setCllentNoName(String cllentNoName) {
		this.cllentNoName = cllentNoName;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public String getMaxBeginRangeNo() {
		return maxBeginRangeNo;
	}

	public void setMaxBeginRangeNo(String maxBeginRangeNo) {
		this.maxBeginRangeNo = maxBeginRangeNo;
	}

	public String getMinBeginRangeNo() {
		return minBeginRangeNo;
	}

	public void setMinBeginRangeNo(String minBeginRangeNo) {
		this.minBeginRangeNo = minBeginRangeNo;
	}

	public String getMaxEndRangeNo() {
		return maxEndRangeNo;
	}

	public void setMaxEndRangeNo(String maxEndRangeNo) {
		this.maxEndRangeNo = maxEndRangeNo;
	}

	public String getMinEndRangeNo() {
		return minEndRangeNo;
	}

	public void setMinEndRangeNo(String minEndRangeNo) {
		this.minEndRangeNo = minEndRangeNo;
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

	public String getInAcctBranch() {
		return inAcctBranch;
	}

	public void setInAcctBranch(String inAcctBranch) {
		this.inAcctBranch = inAcctBranch;
	}

	public BigDecimal getDepositRate() {
		return depositRate;
	}

	public void setDepositRate(BigDecimal depositRate) {
		this.depositRate = depositRate;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getIntFluctuationMode() {
		return intFluctuationMode;
	}

	public void setIntFluctuationMode(String intFluctuationMode) {
		this.intFluctuationMode = intFluctuationMode;
	}

	public BigDecimal getFloatIntRate() {
		return floatIntRate;
	}

	public void setFloatIntRate(BigDecimal floatIntRate) {
		this.floatIntRate = floatIntRate;
	}

	public String getFeeFlag() {
		return feeFlag;
	}

	public void setFeeFlag(String feeFlag) {
		this.feeFlag = feeFlag;
	}

	public String getFeeMode() {
		return feeMode;
	}

	public void setFeeMode(String feeMode) {
		this.feeMode = feeMode;
	}

	public String getFeeAcctNO() {
		return feeAcctNO;
	}

	public void setFeeAcctNO(String feeAcctNO) {
		this.feeAcctNO = feeAcctNO;
	}

	public BigDecimal getFEE_RATE() {
		return FEE_RATE;
	}

	public void setFEE_RATE(BigDecimal fEE_RATE) {
		FEE_RATE = fEE_RATE;
	}

	public String getPoolDepositAcctNo() {
		return poolDepositAcctNo;
	}

	public void setPoolDepositAcctNo(String poolDepositAcctNo) {
		this.poolDepositAcctNo = poolDepositAcctNo;
	}

	public String getDepositAcctNo() {
		return depositAcctNo;
	}

	public void setDepositAcctNo(String depositAcctNo) {
		this.depositAcctNo = depositAcctNo;
	}

	public String getDeduDepositAcctNo() {
		return deduDepositAcctNo;
	}

	public void setDeduDepositAcctNo(String deduDepositAcctNo) {
		this.deduDepositAcctNo = deduDepositAcctNo;
	}

	public String getAcptProtocolNo() {
		return acptProtocolNo;
	}

	public void setAcptProtocolNo(String acptProtocolNo) {
		this.acptProtocolNo = acptProtocolNo;
	}
	private List details = new ArrayList();

	
	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getIsAutoRecvFlag() {
		return isAutoRecvFlag;
	}

	public void setIsAutoRecvFlag(String isAutoRecvFlag) {
		this.isAutoRecvFlag = isAutoRecvFlag;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public List getDetails() {
		return details;
	}

	public void setDetails(List details) {
		this.details = details;
	}

	public String getMarginAccount() {
		return marginAccount;
	}

	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}

	public String getBillId() {
		return billId;
	}
	public String getRemitterBatch() {
		return remitterBatch;
	}
	public void setRemitterBatch(String remitterBatch) {
		this.remitterBatch = remitterBatch;
	}
	public String getThirdPayRate() {
		return thirdPayRate;
	}
	public void setThirdPayRate(String thirdPayRate) {
		this.thirdPayRate = thirdPayRate;
	}
	public String getThirdAcctNo() {
		return thirdAcctNo;
	}
	public void setThirdAcctNo(String thirdAcctNo) {
		this.thirdAcctNo = thirdAcctNo;
	}
	public String getThridAcctName() {
		return thridAcctName;
	}
	public void setThridAcctName(String thridAcctName) {
		this.thridAcctName = thridAcctName;
	}
	public String getThridOpenBranch() {
		return thridOpenBranch;
	}
	public void setThridOpenBranch(String thridOpenBranch) {
		this.thridOpenBranch = thridOpenBranch;
	}
	public String getIouNo() {
		return iouNo;
	}
	public void setIouNo(String iouNo) {
		this.iouNo = iouNo;
	}
	public String getAddInterst() {
		return addInterst;
	}
	public void setAddInterst(String addInterst) {
		this.addInterst = addInterst;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public String getPayIntMode() {
		return payIntMode;
	}
	public void setPayIntMode(String payIntMode) {
		this.payIntMode = payIntMode;
	}
	public String getExeIntRate() {
		return exeIntRate;
	}
	public void setExeIntRate(String exeIntRate) {
		this.exeIntRate = exeIntRate;
	}
	public String getIntRecvAmt() {
		return intRecvAmt;
	}
	public void setIntRecvAmt(String intRecvAmt) {
		this.intRecvAmt = intRecvAmt;
	}
	public String getGuaranteeNo() {
		return guaranteeNo;
	}
	public void setGuaranteeNo(String guaranteeNo) {
		this.guaranteeNo = guaranteeNo;
	}
	public String getIntersetAccNo() {
		return intersetAccNo;
	}
	public void setIntersetAccNo(String intersetAccNo) {
		this.intersetAccNo = intersetAccNo;
	}
	public String getIntersetAccNoName() {
		return intersetAccNoName;
	}
	public void setIntersetAccNoName(String intersetAccNoName) {
		this.intersetAccNoName = intersetAccNoName;
	}
	public String getCancelAccNo() {
		return cancelAccNo;
	}
	public void setCancelAccNo(String cancelAccNo) {
		this.cancelAccNo = cancelAccNo;
	}
	public String getLoanAcctNo() {
		return loanAcctNo;
	}
	public void setLoanAcctNo(String loanAcctNo) {
		this.loanAcctNo = loanAcctNo;
	}
	public String getLoanAcctNoName() {
		return loanAcctNoName;
	}
	public void setLoanAcctNoName(String loanAcctNoName) {
		this.loanAcctNoName = loanAcctNoName;
	}
	public String getLoanOpenBank() {
		return loanOpenBank;
	}
	public void setLoanOpenBank(String loanOpenBank) {
		this.loanOpenBank = loanOpenBank;
	}
	public String getCllentNo() {
		return cllentNo;
	}
	public void setCllentNo(String cllentNo) {
		this.cllentNo = cllentNo;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getReserve1() {
		return reserve1;
	}
	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public String getForbidFlag() {
		return forbidFlag;
	}
	public void setForbidFlag(String forbidFlag) {
		this.forbidFlag = forbidFlag;
	}
	public String getApplyDt() {
		return applyDt;
	}
	public void setApplyDt(String applyDt) {
		this.applyDt = applyDt;
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
	public String getRemitter() {
		return remitter;
	}
	public void setRemitter(String remitter) {
		this.remitter = remitter;
	}
	public String getRemitterAcctNo() {
		return remitterAcctNo;
	}
	public void setRemitterAcctNo(String remitterAcctNo) {
		this.remitterAcctNo = remitterAcctNo;
	}
	public String getRemitterBankNo() {
		return remitterBankNo;
	}
	public void setRemitterBankNo(String remitterBankNo) {
		this.remitterBankNo = remitterBankNo;
	}
	public String getRemitterBankName() {
		return remitterBankName;
	}
	public void setRemitterBankName(String remitterBankName) {
		this.remitterBankName = remitterBankName;
	}
	public String getAcceptorAcctNo() {
		return acceptorAcctNo;
	}
	public void setAcceptorAcctNo(String acceptorAcctNo) {
		this.acceptorAcctNo = acceptorAcctNo;
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
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public String getPayeeAcctNo() {
		return payeeAcctNo;
	}
	public void setPayeeAcctNo(String payeeAcctNo) {
		this.payeeAcctNo = payeeAcctNo;
	}
	public String getPayeeBankName() {
		return payeeBankName;
	}
	public void setPayeeBankName(String payeeBankName) {
		this.payeeBankName = payeeBankName;
	}
	public String getPayeeBankNo() {
		return payeeBankNo;
	}
	public void setPayeeBankNo(String payeeBankNo) {
		this.payeeBankNo = payeeBankNo;
	}
	public String getApplicantAcctNo() {
		return applicantAcctNo;
	}
	public void setApplicantAcctNo(String applicantAcctNo) {
		this.applicantAcctNo = applicantAcctNo;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverAcctNo() {
		return receiverAcctNo;
	}
	public void setReceiverAcctNo(String receiverAcctNo) {
		this.receiverAcctNo = receiverAcctNo;
	}
	public String getReceiverBankNo() {
		return receiverBankNo;
	}
	public void setReceiverBankNo(String receiverBankNo) {
		this.receiverBankNo = receiverBankNo;
	}
	public String getIfInPool() {
		return ifInPool;
	}
	public void setIfInPool(String ifInPool) {
		this.ifInPool = ifInPool;
	}
	public String getBranchNo() {
		return branchNo;
	}
	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}
	public String getOverdueReason() {
		return overdueReason;
	}
	public void setOverdueReason(String overdueReason) {
		this.overdueReason = overdueReason;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getOnlineMark() {
		return onlineMark;
	}
	public void setOnlineMark(String onlineMark) {
		this.onlineMark = onlineMark;
	}
	public String getBillMoney() {
		return billMoney;
	}
	public void setBillMoney(String billMoney) {
		this.billMoney = billMoney;
	}
	public String getRemitterCreditRatgs() {
		return remitterCreditRatgs;
	}
	public void setRemitterCreditRatgs(String remitterCreditRatgs) {
		this.remitterCreditRatgs = remitterCreditRatgs;
	}
	public String getRemitterRatgDuedt() {
		return remitterRatgDuedt;
	}
	public void setRemitterRatgDuedt(String remitterRatgDuedt) {
		this.remitterRatgDuedt = remitterRatgDuedt;
	}
	public String getRemitterAssuName() {
		return remitterAssuName;
	}
	public void setRemitterAssuName(String remitterAssuName) {
		this.remitterAssuName = remitterAssuName;
	}
	public String getRemitterAssuAddr() {
		return remitterAssuAddr;
	}
	public void setRemitterAssuAddr(String remitterAssuAddr) {
		this.remitterAssuAddr = remitterAssuAddr;
	}
	public String getRemitterAssuDt() {
		return remitterAssuDt;
	}
	public void setRemitterAssuDt(String remitterAssuDt) {
		this.remitterAssuDt = remitterAssuDt;
	}
	public String getAcceptorAssuName() {
		return acceptorAssuName;
	}
	public void setAcceptorAssuName(String acceptorAssuName) {
		this.acceptorAssuName = acceptorAssuName;
	}
	public String getAcceptorAssuAddr() {
		return acceptorAssuAddr;
	}
	public void setAcceptorAssuAddr(String acceptorAssuAddr) {
		this.acceptorAssuAddr = acceptorAssuAddr;
	}
	public String getAcceptorAssuDt() {
		return acceptorAssuDt;
	}
	public void setAcceptorAssuDt(String acceptorAssuDt) {
		this.acceptorAssuDt = acceptorAssuDt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTransNo() {
		return transNo;
	}
	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public String getFinishDt() {
		return finishDt;
	}
	public void setFinishDt(String finishDt) {
		this.finishDt = finishDt;
	}
	public String getBackOpenDt() {
		return backOpenDt;
	}
	public void setBackOpenDt(String backOpenDt) {
		this.backOpenDt = backOpenDt;
	}
	public String getBackEndDt() {
		return backEndDt;
	}
	public void setBackEndDt(String backEndDt) {
		this.backEndDt = backEndDt;
	}
	public String getAssuSignerAddr() {
		return assuSignerAddr;
	}
	public void setAssuSignerAddr(String assuSignerAddr) {
		this.assuSignerAddr = assuSignerAddr;
	}
	public String getPresentationFlag() {
		return presentationFlag;
	}
	public void setPresentationFlag(String presentationFlag) {
		this.presentationFlag = presentationFlag;
	}
	public String getToRemark() {
		return toRemark;
	}
	public void setToRemark(String toRemark) {
		this.toRemark = toRemark;
	}
	public String getRecourseType() {
		return recourseType;
	}
	public void setRecourseType(String recourseType) {
		this.recourseType = recourseType;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public String getStartTransDt() {
		return startTransDt;
	}
	public void setStartTransDt(String startTransDt) {
		this.startTransDt = startTransDt;
	}
	public String getEndTransDt() {
		return endTransDt;
	}
	public void setEndTransDt(String endTransDt) {
		this.endTransDt = endTransDt;
	}
	public String getMinStartTransDt() {
		return minStartTransDt;
	}
	public void setMinStartTransDt(String minStartTransDt) {
		this.minStartTransDt = minStartTransDt;
	}
	public String getMaxStartTransDt() {
		return maxStartTransDt;
	}
	public void setMaxStartTransDt(String maxStartTransDt) {
		this.maxStartTransDt = maxStartTransDt;
	}
	public String getMinEndTransDt() {
		return minEndTransDt;
	}
	public void setMinEndTransDt(String minEndTransDt) {
		this.minEndTransDt = minEndTransDt;
	}
	public String getMaxEndTransDt() {
		return maxEndTransDt;
	}
	public void setMaxEndTransDt(String maxEndTransDt) {
		this.maxEndTransDt = maxEndTransDt;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getSignFlag() {
		return signFlag;
	}
	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}
	public String getXTransNo() {
		return XTransNo;
	}
	public void setXTransNo(String xTransNo) {
		XTransNo = xTransNo;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getMaxAcptDt() {
		return maxAcptDt;
	}
	public void setMaxAcptDt(String maxAcptDt) {
		this.maxAcptDt = maxAcptDt;
	}
	public String getMinAcptDt() {
		return minAcptDt;
	}
	public void setMinAcptDt(String minAcptDt) {
		this.minAcptDt = minAcptDt;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getDueDt() {
		return dueDt;
	}
	public void setDueDt(String dueDt) {
		this.dueDt = dueDt;
	}
	public String getAcptDt() {
		return acptDt;
	}
	public void setAcptDt(String acptDt) {
		this.acptDt = acptDt;
	}
	public ECDSPoolTransNotes(){}
	public String getTransName() {
		return transName;
	}
	public void setTransName(String transName) {
		this.transName = transName;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getAssuType() {
		return assuType;
	}
	public void setAssuType(String assuType) {
		this.assuType = assuType;
	}
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getToAcctNo() {
		return toAcctNo;
	}
	public void setToAcctNo(String toAcctNo) {
		this.toAcctNo = toAcctNo;
	}
	public String getToBankNo() {
		return toBankNo;
	}
	public void setToBankNo(String toBankNo) {
		this.toBankNo = toBankNo;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getActuMoney() {
		return actuMoney;
	}
	public void setActuMoney(String actuMoney) {
		this.actuMoney = actuMoney;
	}
	public String getRateEndDt() {
		return rateEndDt;
	}
	public void setRateEndDt(String rateEndDt) {
		this.rateEndDt = rateEndDt;
	}
	public String getBackRate() {
		return backRate;
	}
	public void setBackRate(String backRate) {
		this.backRate = backRate;
	}
	public String getBackActuMoney() {
		return backActuMoney;
	}
	public void setBackActuMoney(String backActuMoney) {
		this.backActuMoney = backActuMoney;
	}
	public String getAoAcctNo() {
		return aoAcctNo;
	}
	public void setAoAcctNo(String aoAcctNo) {
		this.aoAcctNo = aoAcctNo;
	}
	public String getAoBankNo() {
		return aoBankNo;
	}
	public void setAoBankNo(String aoBankNo) {
		this.aoBankNo = aoBankNo;
	}
	public String getSigner() {
		return signer;
	}
	public void setSigner(String signer) {
		this.signer = signer;
	}
	public String getOperNo() {
		return operNo;
	}
	public void setOperNo(String operNo) {
		this.operNo = operNo;
	}
	public String getIsLock() {
		return isLock;
	}
	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
	public String getObligeeAcctNo() {
		return obligeeAcctNo;
	}
	public void setObligeeAcctNo(String obligeeAcctNo) {
		this.obligeeAcctNo = obligeeAcctNo;
	}
	public String getObligeeBankNo() {
		return obligeeBankNo;
	}
	public void setObligeeBankNo(String obligeeBankNo) {
		this.obligeeBankNo = obligeeBankNo;
	}
	public String getObligeeCustName() {
		return obligeeCustName;
	}
	public void setObligeeCustName(String obligeeCustName) {
		this.obligeeCustName = obligeeCustName;
	}
	public String getHaveAcctNo() {
		return haveAcctNo;
	}
	public void setHaveAcctNo(String haveAcctNo) {
		this.haveAcctNo = haveAcctNo;
	}
	public String getHaveBankNo() {
		return haveBankNo;
	}
	public void setHaveBankNo(String haveBankNo) {
		this.haveBankNo = haveBankNo;
	}
	public String getHaveCustName() {
		return haveCustName;
	}
	public void setHaveCustName(String haveCustName) {
		this.haveCustName = haveCustName;
	}
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	public String getConferNo() {
		return conferNo;
	}
	public void setConferNo(String conferNo) {
		this.conferNo = conferNo;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public String getPartnerType() {
		return partnerType;
	}
	public void setPartnerType(String partnerType) {
		this.partnerType = partnerType;
	}
	public String getMaxBillMoney() {
		return maxBillMoney;
	}
	public void setMaxBillMoney(String maxBillMoney) {
		this.maxBillMoney = maxBillMoney;
	}
	public String getMinBillMoney() {
		return minBillMoney;
	}
	public void setMinBillMoney(String minBillMoney) {
		this.minBillMoney = minBillMoney;
	}
	public String getMaxDueDt() {
		return maxDueDt;
	}
	public void setMaxDueDt(String maxDueDt) {
		this.maxDueDt = maxDueDt;
	}
	public String getMinDueDt() {
		return minDueDt;
	}
	public void setMinDueDt(String minDueDt) {
		this.minDueDt = minDueDt;
	}
	public static String getIsOpenCore() {
		return IS_OPEN_CORE;
	}
	public String getSignUpMark() {
		return SignUpMark;
	}
	public void setSignUpMark(String signUpMark) {
		SignUpMark = signUpMark;
	}
	public String getRejectCode() {
		return RejectCode;
	}
	public void setRejectCode(String rejectCode) {
		RejectCode = rejectCode;
	}

	public String getTrancode() {
		return trancode;
	}

	public void setTrancode(String trancode) {
		this.trancode = trancode;
	}
	public String getDiscountIntRate() {
		return discountIntRate;
	}
	public void setDiscountIntRate(String discountIntRate) {
		this.discountIntRate = discountIntRate;
	}
	public String getDiscountDate() {
		return discountDate;
	}
	public void setDiscountDate(String discountDate) {
		this.discountDate = discountDate;
	}
	public String getRedeemOpemDate() {
		return redeemOpemDate;
	}
	public void setRedeemOpemDate(String redeemOpemDate) {
		this.redeemOpemDate = redeemOpemDate;
	}
	public String getRedeenEndDate() {
		return redeenEndDate;
	}
	public void setRedeenEndDate(String redeenEndDate) {
		this.redeenEndDate = redeenEndDate;
	}
	public String getRedeemIntRate() {
		return redeemIntRate;
	}
	public void setRedeemIntRate(String redeemIntRate) {
		this.redeemIntRate = redeemIntRate;
	}
	public String getRedeemAmt() {
		return redeemAmt;
	}
	public void setRedeemAmt(String redeemAmt) {
		this.redeemAmt = redeemAmt;
	}
	public String getDiscountInBankCode() {
		return discountInBankCode;
	}
	public void setDiscountInBankCode(String discountInBankCode) {
		this.discountInBankCode = discountInBankCode;
	}
	public String getDiscountInBankName() {
		return discountInBankName;
	}
	public void setDiscountInBankName(String discountInBankName) {
		this.discountInBankName = discountInBankName;
	}
	public String getDiscountInAcctNo() {
		return discountInAcctNo;
	}
	public void setDiscountInAcctNo(String discountInAcctNo) {
		this.discountInAcctNo = discountInAcctNo;
	}
	public String getEnterAcctNo() {
		return enterAcctNo;
	}
	public void setEnterAcctNo(String enterAcctNo) {
		this.enterAcctNo = enterAcctNo;
	}
	public String getEnterBankCode() {
		return enterBankCode;
	}
	public void setEnterBankCode(String enterBankCode) {
		this.enterBankCode = enterBankCode;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
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


	public String getSplitFlag() {
		return splitFlag;
	}

	public void setSplitFlag(String splitFlag) {
		this.splitFlag = splitFlag;
	}

	public String getBillOriginCode() {
		return billOriginCode;
	}

	public void setBillOriginCode(String billOriginCode) {
		this.billOriginCode = billOriginCode;
	}

	public String getCorpApplyTransCode() {
		return CorpApplyTransCode;
	}

	public void setCorpApplyTransCode(String corpApplyTransCode) {
		CorpApplyTransCode = corpApplyTransCode;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getHldrId() {
		return hldrId;
	}

	public void setHldrId(String hldrId) {
		this.hldrId = hldrId;
	}
}
