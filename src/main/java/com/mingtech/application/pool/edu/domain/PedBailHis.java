package com.mingtech.application.pool.edu.domain;

/**
 * 
 * @Title: 保证金划转历史表
 * @Description: 记录保证金划转历史，数据来源为核心系统，字段名称均与核心接口一致
 * @author Ju Nana
 * @date 2019-2-14
 */

public class PedBailHis implements java.io.Serializable {

	// Fields

	private String bailHisId;//主键ID     
	private String accNo;     //账号          
	private String ccy;       //币种          
	private String vouTyp;    //凭证种类      
	private String vouNo;     //凭证号        
	private String dateTran;  //交易日期      
	private String timeMch;   //机器时间      
	private String memoNo;    //摘要码        
	private String memo;      //摘要          
	private String amtTran;   //交易金额      
	private String bal;       //余额          
	private String serSeqNo;  //主机流水号    
	private String CSeqNo;    //子流水号      
	private String flgCd;     //借贷标志      
	private String flagCt;    //现转标识      
	private String accNoA;    //对方账号      
	private String acctSeqNo; //交易卡号后四位
	private String subSeq;    //分帐号        
	private String telTran;   //交易柜员      
	private String brcTran;   //交易机构      
	private String brcName;   //机构名称      
	private String num1;      //记录号        
	private String custNam;   //名称          
	private String field;     //备注          
	private String flgCanl;   //冲销标识      
	private String seqNoR;    //冲销流水号    
	private String accNoA2;   //对方户名      
	private String accNamA2;  //对方行名      
	private String bnkNamA2;  //对方行名

	// Constructors

	/** default constructor */
	public PedBailHis() {
	}

	/** full constructor */
	public PedBailHis(String accNo, String ccy, String vouTyp, String vouNo,
			String dateTran, String timeMch, String memoNo, String memo,
			String amtTran, String bal, String serSeqNo, String CSeqNo,
			String flgCd, String flagCt, String accNoA, String acctSeqNo,
			String subSeq, String telTran, String brcTran, String brcName,
			String num1, String custNam, String field, String flgCanl,
			String seqNoR, String accNoA2, String accNamA2, String bnkNamA2) {
		this.accNo = accNo;
		this.ccy = ccy;
		this.vouTyp = vouTyp;
		this.vouNo = vouNo;
		this.dateTran = dateTran;
		this.timeMch = timeMch;
		this.memoNo = memoNo;
		this.memo = memo;
		this.amtTran = amtTran;
		this.bal = bal;
		this.serSeqNo = serSeqNo;
		this.CSeqNo = CSeqNo;
		this.flgCd = flgCd;
		this.flagCt = flagCt;
		this.accNoA = accNoA;
		this.acctSeqNo = acctSeqNo;
		this.subSeq = subSeq;
		this.telTran = telTran;
		this.brcTran = brcTran;
		this.brcName = brcName;
		this.num1 = num1;
		this.custNam = custNam;
		this.field = field;
		this.flgCanl = flgCanl;
		this.seqNoR = seqNoR;
		this.accNoA2 = accNoA2;
		this.accNamA2 = accNamA2;
		this.bnkNamA2 = bnkNamA2;
	}

	// Property accessors

	public String getBailHisId() {
		return this.bailHisId;
	}

	public void setBailHisId(String bailHisId) {
		this.bailHisId = bailHisId;
	}

	public String getAccNo() {
		return this.accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getCcy() {
		return this.ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public String getVouTyp() {
		return this.vouTyp;
	}

	public void setVouTyp(String vouTyp) {
		this.vouTyp = vouTyp;
	}

	public String getVouNo() {
		return this.vouNo;
	}

	public void setVouNo(String vouNo) {
		this.vouNo = vouNo;
	}

	public String getDateTran() {
		return this.dateTran;
	}

	public void setDateTran(String dateTran) {
		this.dateTran = dateTran;
	}

	public String getTimeMch() {
		return this.timeMch;
	}

	public void setTimeMch(String timeMch) {
		this.timeMch = timeMch;
	}

	public String getMemoNo() {
		return this.memoNo;
	}

	public void setMemoNo(String memoNo) {
		this.memoNo = memoNo;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getAmtTran() {
		return this.amtTran;
	}

	public void setAmtTran(String amtTran) {
		this.amtTran = amtTran;
	}

	public String getBal() {
		return this.bal;
	}

	public void setBal(String bal) {
		this.bal = bal;
	}

	public String getSerSeqNo() {
		return this.serSeqNo;
	}

	public void setSerSeqNo(String serSeqNo) {
		this.serSeqNo = serSeqNo;
	}

	public String getCSeqNo() {
		return this.CSeqNo;
	}

	public void setCSeqNo(String CSeqNo) {
		this.CSeqNo = CSeqNo;
	}

	public String getFlgCd() {
		return this.flgCd;
	}

	public void setFlgCd(String flgCd) {
		this.flgCd = flgCd;
	}

	public String getFlagCt() {
		return this.flagCt;
	}

	public void setFlagCt(String flagCt) {
		this.flagCt = flagCt;
	}

	public String getAccNoA() {
		return this.accNoA;
	}

	public void setAccNoA(String accNoA) {
		this.accNoA = accNoA;
	}

	public String getAcctSeqNo() {
		return this.acctSeqNo;
	}

	public void setAcctSeqNo(String acctSeqNo) {
		this.acctSeqNo = acctSeqNo;
	}

	public String getSubSeq() {
		return this.subSeq;
	}

	public void setSubSeq(String subSeq) {
		this.subSeq = subSeq;
	}

	public String getTelTran() {
		return this.telTran;
	}

	public void setTelTran(String telTran) {
		this.telTran = telTran;
	}

	public String getBrcTran() {
		return this.brcTran;
	}

	public void setBrcTran(String brcTran) {
		this.brcTran = brcTran;
	}

	public String getBrcName() {
		return this.brcName;
	}

	public void setBrcName(String brcName) {
		this.brcName = brcName;
	}

	public String getNum1() {
		return this.num1;
	}

	public void setNum1(String num1) {
		this.num1 = num1;
	}

	public String getCustNam() {
		return this.custNam;
	}

	public void setCustNam(String custNam) {
		this.custNam = custNam;
	}

	public String getField() {
		return this.field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFlgCanl() {
		return this.flgCanl;
	}

	public void setFlgCanl(String flgCanl) {
		this.flgCanl = flgCanl;
	}

	public String getSeqNoR() {
		return this.seqNoR;
	}

	public void setSeqNoR(String seqNoR) {
		this.seqNoR = seqNoR;
	}

	public String getAccNoA2() {
		return this.accNoA2;
	}

	public void setAccNoA2(String accNoA2) {
		this.accNoA2 = accNoA2;
	}

	public String getAccNamA2() {
		return this.accNamA2;
	}

	public void setAccNamA2(String accNamA2) {
		this.accNamA2 = accNamA2;
	}

	public String getBnkNamA2() {
		return this.bnkNamA2;
	}

	public void setBnkNamA2(String bnkNamA2) {
		this.bnkNamA2 = bnkNamA2;
	}

}