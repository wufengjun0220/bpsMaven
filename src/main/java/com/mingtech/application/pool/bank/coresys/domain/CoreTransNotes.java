package com.mingtech.application.pool.bank.coresys.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class CoreTransNotes {

	// ----------------------记账start----------------------
	private String amtAct;// 担保金额
	private String bilCode;// 票据号码
	private String brcNo;// 机构
	private String ccy;// 币种
	private String colNo;// 担保品编号
	private String inColNo;// 入库担保品编号
	private String outColNo;// 出库担保品编号
	private String custId;// 客户号
	private String dateDue;// 到期日期
	private String devSeqNo;// 设备流水号
	private String frntCode;// 前置交易代码
	private String frntDate;// 前置交易日期
	private String frntSeq;// 前置流水号
	private String frntTime;// 前置交易时间
	private String noVouCom;// 担保合同号
	private String numBatch;// 批号
	private String serSeqNo;// 流水号
	private String typGag;// 抵质押物类型
	private String remark;// 备注(出库原因)
	private String transeq;// 主机流水号
	
	private String deductionAcctNo;//扣款账号
	private String feeRateCode;//代码费率
	private String feeAmt;//扣费金额
	private String totalAmt;//合计金额
	private String totalNum;//合计笔数
	private String path;//路径

	private String drAcctNo;//借方账号
	private String drAcctNoName;//借方账号名称
	private String crAcctNo;//贷方账号
	private String crAcctNoName;//贷方账号名称
	private String tranAmt;//交易金额
	// ----------------------记账end-------------------

	//----------------------理财查询客户信息start-------------------
	private String accNo   ;//账号                  
	private String flgCR   ;//钞汇标志              
	private String accPwdD ;//借方账号/卡号密码     
	private String isChkPwd;//是否检查密码          
	private String seqChk  ;//验印流水号            
	private String isWthSe ;//是否凭印鉴            
	private String tranDate;//平台交易日期          
	private String isChkTrk;//是否检查磁道          
	private String track2  ;//二磁道信息            
	private String track3  ;//三磁道信息            
	private String isLosRej;//是否对挂失状态拒绝标志
	private String revFld2 ;//降级标志              
	private String currentFlag;//定期活期标识 1-活期，2-定期   预存保证金为活期，业务保证金为定期
	
	//----------------------理财查询客户信息end-------------------
	
	//--------------------------客户信息查询start------------------------
	private String brcBld  ;//开户机构
	private String brcMgm  ;//管理机构
	private String custIdA ;//客户号  
	private String custNam ;//客户名称
	private String custType;//客户类别
	private String iDNoS   ;//证件号码
	private String iDTypeS ;//证件类型

	//--------------------------客户信息查询end------------------------
	private String loadNo;//借据号
	
	//--------------------------票据系统票款收回start------------------------
	private String AmtPay;//支付金额
	private String NumSbArt;//代保管品编号
	private String FlgEnt;//收款标志
	private String IssWay;//是否电子票据
	private String PayMode;//支付方式
	private String RejRen;//拒付原因
	private String IssNo;//是否理财票据
	private String AccNoPye;//收款人账号
	private String FlgWay;//线上清算标志
	private String ConsnNo;//托收编号
	private String IsTerm;//承兑行标志
	//--------------------------票据系统票款收回end------------------------
	
	//--------------------------保证金start------------------------
	private String Num;//请求笔数
	private String DateStr;//开始日期
	private String DateEnd;//终止日期
	private String AccSub;//款项代码
	private String Amt;//交易金额
	private String IsSucced;//查询类型
	private String SubSeq;//款项序号
	//--------------------------保证金end------------------------
	
	private String user;//用户
	private String beatch;//机构
	
	private ArrayList list;

	private String bpsNo;//票据池编号
	
	//验印信息
	private String checkSeqNo;//验印流水号
	private String checkAcctNo;//验印账号
	private BigDecimal  checkAmt;//验印金额
	private Date checkDrawDate;//验印签发日期
	private String checkCertNo;//验印凭证号
	private String checkCretCategory;//验印凭证种类
	private Date checkTranDate;//验印交易日期
	
	
	//--------------------------收费start------------------------
	
	
	//--------------------------收费end------------------------
	
	//--------------------------放款申请start----------------------------
	private String batchNo;               //批次号
	private String onlineCrdtNo;          //在线协议编号
	private String loanNo;                //借据号
	private String custNo;                //客户号
	private String branchNo;              //管理机构
	private String baseRateType;          //基准利率类型
	private String currency;              //币种  01-人民币
	private Date applyDate;               //开户日期
	private BigDecimal loanLimit;         //贷款期限
	private Date dueDate;                 //到期日期
	private BigDecimal loanAmt;           //借据金额
	private String deduAcctNo;            //扣款账号
	private String rateFloatType;         //正常利率浮动方式
	private BigDecimal rateFloatValue;    //正常利率浮动值
	private BigDecimal interestRate;      //执行利率
	private String overRateFloatType;     //逾期利率浮动方式
	private BigDecimal overRateFloatValue;//逾期利率浮动值
	private BigDecimal overInterestRate;  //逾期执行利率
	private String contractNo;            //合同编号
	private BigDecimal actualLoanAmt;     //实际发放金额
	private String creditAcct;            //放款账号
	private String depositAcctNo;         //票据池保证金账号
	private Date acctDate;                //核心记账日期
	private String acctFlow;              //核心记账流水
	private String status;                //状态
	private Date createTime;              //创建时间
	private Date updateTime;              //最近修改时间
	private String fundNo;                //圈存编号
	
	//--------------------------------智汇宝付款-----------------------------
	private String loanAcctNo;    //付款账号
	private String loanAcctName;//付款户名
	private String loanBankNo;    //付款人开户行行号
	private String loanBankName;  //付款人开户行行名
	private String loanClearBank;  //付款清算行
	
	private String deduAcctName;  //收款人名称
	private String deduBankCode;  //收款人开户行行号
	private String deduBankName;  //收款人开户行名称
	private String deduClearBank;  //收款清算行
	private String usage;
	private String postscript;
	
	//--------------------------------短信平台----------------------------
	private String pushID;  //手机号
	private String template;  //短信内容
	private String rate;//利率

	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	/*** 融合改造新增字段  end*/
	
	
	public String getRate() {
		return rate;
	}

	public String getInColNo() {
		return inColNo;
	}

	public void setInColNo(String inColNo) {
		this.inColNo = inColNo;
	}

	public String getOutColNo() {
		return outColNo;
	}

	public void setOutColNo(String outColNo) {
		this.outColNo = outColNo;
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

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getPushID() {
		return pushID;
	}

	public void setPushID(String pushID) {
		this.pushID = pushID;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getLoanClearBank() {
		return loanClearBank;
	}

	public void setLoanClearBank(String loanClearBank) {
		this.loanClearBank = loanClearBank;
	}

	public String getDeduClearBank() {
		return deduClearBank;
	}

	public void setDeduClearBank(String deduClearBank) {
		this.deduClearBank = deduClearBank;
	}

	public String getLoanBankNo() {
		return loanBankNo;
	}

	public void setLoanBankNo(String loanBankNo) {
		this.loanBankNo = loanBankNo;
	}

	public String getLoanBankName() {
		return loanBankName;
	}

	public void setLoanBankName(String loanBankName) {
		this.loanBankName = loanBankName;
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

	public String getCreditAcct() {
		return creditAcct;
	}

	public void setCreditAcct(String creditAcct) {
		this.creditAcct = creditAcct;
	}

	public String getUser() {
		return user;
	}

	public String getDrAcctNo() {
		return drAcctNo;
	}

	public void setDrAcctNo(String drAcctNo) {
		this.drAcctNo = drAcctNo;
	}

	public String getDrAcctNoName() {
		return drAcctNoName;
	}

	public void setDrAcctNoName(String drAcctNoName) {
		this.drAcctNoName = drAcctNoName;
	}

	public String getCrAcctNo() {
		return crAcctNo;
	}

	public void setCrAcctNo(String crAcctNo) {
		this.crAcctNo = crAcctNo;
	}

	public String getCrAcctNoName() {
		return crAcctNoName;
	}

	public void setCrAcctNoName(String crAcctNoName) {
		this.crAcctNoName = crAcctNoName;
	}

	public String getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String getDeductionAcctNo() {
		return deductionAcctNo;
	}

	public void setDeductionAcctNo(String deductionAcctNo) {
		this.deductionAcctNo = deductionAcctNo;
	}

	public String getFeeRateCode() {
		return feeRateCode;
	}

	public void setFeeRateCode(String feeRateCode) {
		this.feeRateCode = feeRateCode;
	}

	public String getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}

	public String getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getBeatch() {
		return beatch;
	}

	public void setBeatch(String beatch) {
		this.beatch = beatch;
	}

	public String getAmtAct() {
		return amtAct;
	}

	public String getCurrentFlag() {
		return currentFlag;
	}

	public void setCurrentFlag(String currentFlag) {
		this.currentFlag = currentFlag;
	}

	public void setAmtAct(String amtAct) {
		this.amtAct = amtAct;
	}

	public String getBilCode() {
		return bilCode;
	}

	public void setBilCode(String bilCode) {
		this.bilCode = bilCode;
	}

	public String getBrcNo() {
		return brcNo;
	}

	public void setBrcNo(String brcNo) {
		this.brcNo = brcNo;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public String getColNo() {
		return colNo;
	}

	public void setColNo(String colNo) {
		this.colNo = colNo;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getDateDue() {
		return dateDue;
	}

	public void setDateDue(String dateDue) {
		this.dateDue = dateDue;
	}

	public String getDevSeqNo() {
		return devSeqNo;
	}

	public void setDevSeqNo(String devSeqNo) {
		this.devSeqNo = devSeqNo;
	}

	public String getFrntCode() {
		return frntCode;
	}

	public void setFrntCode(String frntCode) {
		this.frntCode = frntCode;
	}

	public String getFrntDate() {
		return frntDate;
	}

	public void setFrntDate(String frntDate) {
		this.frntDate = frntDate;
	}

	public String getFrntSeq() {
		return frntSeq;
	}

	public void setFrntSeq(String frntSeq) {
		this.frntSeq = frntSeq;
	}

	public String getFrntTime() {
		return frntTime;
	}

	public void setFrntTime(String frntTime) {
		this.frntTime = frntTime;
	}

	public String getNoVouCom() {
		return noVouCom;
	}

	public void setNoVouCom(String noVouCom) {
		this.noVouCom = noVouCom;
	}

	public String getNumBatch() {
		return numBatch;
	}

	public void setNumBatch(String numBatch) {
		this.numBatch = numBatch;
	}

	public String getSerSeqNo() {
		return serSeqNo;
	}

	public void setSerSeqNo(String serSeqNo) {
		this.serSeqNo = serSeqNo;
	}

	public String getTypGag() {
		return typGag;
	}

	public void setTypGag(String typGag) {
		this.typGag = typGag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTranseq() {
		return transeq;
	}

	public void setTranseq(String transeq) {
		this.transeq = transeq;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getFlgCR() {
		return flgCR;
	}

	public void setFlgCR(String flgCR) {
		this.flgCR = flgCR;
	}

	public String getAccPwdD() {
		return accPwdD;
	}

	public void setAccPwdD(String accPwdD) {
		this.accPwdD = accPwdD;
	}

	public String getIsChkPwd() {
		return isChkPwd;
	}

	public void setIsChkPwd(String isChkPwd) {
		this.isChkPwd = isChkPwd;
	}

	public String getSeqChk() {
		return seqChk;
	}

	public void setSeqChk(String seqChk) {
		this.seqChk = seqChk;
	}

	public String getIsWthSe() {
		return isWthSe;
	}

	public void setIsWthSe(String isWthSe) {
		this.isWthSe = isWthSe;
	}

	public String getTranDate() {
		return tranDate;
	}

	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}

	public String getIsChkTrk() {
		return isChkTrk;
	}

	public void setIsChkTrk(String isChkTrk) {
		this.isChkTrk = isChkTrk;
	}

	public String getTrack2() {
		return track2;
	}

	public void setTrack2(String track2) {
		this.track2 = track2;
	}

	public String getTrack3() {
		return track3;
	}

	public void setTrack3(String track3) {
		this.track3 = track3;
	}

	public String getIsLosRej() {
		return isLosRej;
	}

	public void setIsLosRej(String isLosRej) {
		this.isLosRej = isLosRej;
	}

	public String getRevFld2() {
		return revFld2;
	}

	public void setRevFld2(String revFld2) {
		this.revFld2 = revFld2;
	}

	
	public String getBrcBld() {
		return brcBld;
	}

	public void setBrcBld(String brcBld) {
		this.brcBld = brcBld;
	}

	public String getBrcMgm() {
		return brcMgm;
	}

	public void setBrcMgm(String brcMgm) {
		this.brcMgm = brcMgm;
	}

	

	public String getCustNam() {
		return custNam;
	}

	public void setCustNam(String custNam) {
		this.custNam = custNam;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getCustIdA() {
		return custIdA;
	}

	public void setCustIdA(String custIdA) {
		this.custIdA = custIdA;
	}

	public String getiDNoS() {
		return iDNoS;
	}

	public void setiDNoS(String iDNoS) {
		this.iDNoS = iDNoS;
	}

	public String getiDTypeS() {
		return iDTypeS;
	}

	public void setiDTypeS(String iDTypeS) {
		this.iDTypeS = iDTypeS;
	}

	public String getLoadNo() {
		return loadNo;
	}

	public void setLoadNo(String loadNo) {
		this.loadNo = loadNo;
	}

	public String getAmtPay() {
		return AmtPay;
	}

	public void setAmtPay(String amtPay) {
		AmtPay = amtPay;
	}

	public String getNumSbArt() {
		return NumSbArt;
	}

	public void setNumSbArt(String numSbArt) {
		NumSbArt = numSbArt;
	}

	public String getFlgEnt() {
		return FlgEnt;
	}

	public void setFlgEnt(String flgEnt) {
		FlgEnt = flgEnt;
	}

	public String getIssWay() {
		return IssWay;
	}

	public void setIssWay(String issWay) {
		IssWay = issWay;
	}

	public String getPayMode() {
		return PayMode;
	}

	public void setPayMode(String payMode) {
		PayMode = payMode;
	}

	public String getRejRen() {
		return RejRen;
	}

	public void setRejRen(String rejRen) {
		RejRen = rejRen;
	}

	public String getIssNo() {
		return IssNo;
	}

	public void setIssNo(String issNo) {
		IssNo = issNo;
	}

	public String getAccNoPye() {
		return AccNoPye;
	}

	public void setAccNoPye(String accNoPye) {
		AccNoPye = accNoPye;
	}

	public String getFlgWay() {
		return FlgWay;
	}

	public void setFlgWay(String flgWay) {
		FlgWay = flgWay;
	}

	public String getConsnNo() {
		return ConsnNo;
	}

	public void setConsnNo(String consnNo) {
		ConsnNo = consnNo;
	}

	public String getIsTerm() {
		return IsTerm;
	}

	public void setIsTerm(String isTerm) {
		IsTerm = isTerm;
	}

	public String getNum() {
		return Num;
	}

	public void setNum(String num) {
		Num = num;
	}

	public String getDateStr() {
		return DateStr;
	}

	public void setDateStr(String dateStr) {
		DateStr = dateStr;
	}

	public String getDateEnd() {
		return DateEnd;
	}

	public void setDateEnd(String dateEnd) {
		DateEnd = dateEnd;
	}

	public String getAccSub() {
		return AccSub;
	}

	public void setAccSub(String accSub) {
		AccSub = accSub;
	}

	public String getAmt() {
		return Amt;
	}

	public void setAmt(String amt) {
		Amt = amt;
	}

	public String getIsSucced() {
		return IsSucced;
	}

	public void setIsSucced(String isSucced) {
		IsSucced = isSucced;
	}

	public String getSubSeq() {
		return SubSeq;
	}

	public void setSubSeq(String subSeq) {
		SubSeq = subSeq;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getCheckSeqNo() {
		return checkSeqNo;
	}

	public void setCheckSeqNo(String checkSeqNo) {
		this.checkSeqNo = checkSeqNo;
	}

	public String getCheckAcctNo() {
		return checkAcctNo;
	}

	public void setCheckAcctNo(String checkAcctNo) {
		this.checkAcctNo = checkAcctNo;
	}

	public BigDecimal getCheckAmt() {
		return checkAmt;
	}

	public void setCheckAmt(BigDecimal checkAmt) {
		this.checkAmt = checkAmt;
	}

	public Date getCheckDrawDate() {
		return checkDrawDate;
	}

	public void setCheckDrawDate(Date checkDrawDate) {
		this.checkDrawDate = checkDrawDate;
	}

	public String getCheckCertNo() {
		return checkCertNo;
	}

	public void setCheckCertNo(String checkCertNo) {
		this.checkCertNo = checkCertNo;
	}

	public String getCheckCretCategory() {
		return checkCretCategory;
	}

	public void setCheckCretCategory(String checkCretCategory) {
		this.checkCretCategory = checkCretCategory;
	}

	public Date getCheckTranDate() {
		return checkTranDate;
	}

	public void setCheckTranDate(Date checkTranDate) {
		this.checkTranDate = checkTranDate;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getOnlineCrdtNo() {
		return onlineCrdtNo;
	}

	public void setOnlineCrdtNo(String onlineCrdtNo) {
		this.onlineCrdtNo = onlineCrdtNo;
	}

	public String getLoanNo() {
		return loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getBranchNo() {
		return branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public String getBaseRateType() {
		return baseRateType;
	}

	public void setBaseRateType(String baseRateType) {
		this.baseRateType = baseRateType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public BigDecimal getLoanLimit() {
		return loanLimit;
	}

	public void setLoanLimit(BigDecimal loanLimit) {
		this.loanLimit = loanLimit;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getLoanAmt() {
		return loanAmt;
	}

	public void setLoanAmt(BigDecimal loanAmt) {
		this.loanAmt = loanAmt;
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

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
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

	public BigDecimal getOverInterestRate() {
		return overInterestRate;
	}

	public void setOverInterestRate(BigDecimal overInterestRate) {
		this.overInterestRate = overInterestRate;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public BigDecimal getActualLoanAmt() {
		return actualLoanAmt;
	}

	public void setActualLoanAmt(BigDecimal actualLoanAmt) {
		this.actualLoanAmt = actualLoanAmt;
	}

	public String getDepositAcctNo() {
		return depositAcctNo;
	}

	public void setDepositAcctNo(String depositAcctNo) {
		this.depositAcctNo = depositAcctNo;
	}

	public Date getAcctDate() {
		return acctDate;
	}

	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}

	public String getAcctFlow() {
		return acctFlow;
	}

	public void setAcctFlow(String acctFlow) {
		this.acctFlow = acctFlow;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getFundNo() {
		return fundNo;
	}

	public void setFundNo(String fundNo) {
		this.fundNo = fundNo;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getPostscript() {
		return postscript;
	}

	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
	
	
	
	
}
