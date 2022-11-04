package com.mingtech.application.pool.draft.domain;

/**
 * 
 * @Title: 保证金划转当日流水表
 * @Description: 记录保证金当日划转流水，数据来源为核心系统，字段名称均与核心接口一致
 * @author Ju Nana
 * @date 2019-2-14
 */

public class PedBailFlowQuery implements java.io.Serializable {

	private String bailFlowId;// 主键ID
	private String accNo; // 账号
	private String ccy; // 币种
	private String vouTyp; // 凭证种类
	private String vouNo; // 凭证号
	private String dateTran; // 交易日期
	private String timeMch; // 机器时间
	private String memoNo; // 摘要码
	private String memo; // 摘要
	private String amtTran; // 交易金额
	private String bal; // 余额
	private String serSeqNo; // 主机流水号
	private String CSeqNo; // 子流水号
	private String flgCd; // 借贷标志
	private String flagCt; // 现转标识
	private String accNoA; // 对方账号
	private String acctSeqNo; // 交易卡号后四位
	private String subSeq; // 分帐号
	private String telTran; // 交易柜员
	private String brcTran; // 交易机构
	private String brcName; // 机构名称
	private String num1; // 记录号
	private String custNam; // 名称
	private String field; // 备注
	private String flgCanl; // 冲销标识
	private String seqNoR; // 冲销流水号
	private String accNoA2; // 对方户名
	private String accNamA2; // 对方行名
	private String bnkNamA2; // 对方行名

	private String poolAgreement;
	private String poolName;// 票据池名称

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

	public String getBailFlowId() {
		return bailFlowId;
	}

	public void setBailFlowId(String bailFlowId) {
		this.bailFlowId = bailFlowId;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public String getVouTyp() {
		return vouTyp;
	}

	public void setVouTyp(String vouTyp) {
		this.vouTyp = vouTyp;
	}

	public String getVouNo() {
		return vouNo;
	}

	public void setVouNo(String vouNo) {
		this.vouNo = vouNo;
	}

	public String getDateTran() {
		return dateTran;
	}

	public void setDateTran(String dateTran) {
		this.dateTran = dateTran;
	}

	public String getTimeMch() {
		return timeMch;
	}

	public void setTimeMch(String timeMch) {
		this.timeMch = timeMch;
	}

	public String getMemoNo() {
		return memoNo;
	}

	public void setMemoNo(String memoNo) {
		this.memoNo = memoNo;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getAmtTran() {
		return amtTran;
	}

	public void setAmtTran(String amtTran) {
		this.amtTran = amtTran;
	}

	public String getBal() {
		return bal;
	}

	public void setBal(String bal) {
		this.bal = bal;
	}

	public String getSerSeqNo() {
		return serSeqNo;
	}

	public void setSerSeqNo(String serSeqNo) {
		this.serSeqNo = serSeqNo;
	}

	public String getCSeqNo() {
		return CSeqNo;
	}

	public void setCSeqNo(String cSeqNo) {
		CSeqNo = cSeqNo;
	}

	public String getFlgCd() {
		return flgCd;
	}

	public void setFlgCd(String flgCd) {
		this.flgCd = flgCd;
	}

	public String getFlagCt() {
		return flagCt;
	}

	public void setFlagCt(String flagCt) {
		this.flagCt = flagCt;
	}

	public String getAccNoA() {
		return accNoA;
	}

	public void setAccNoA(String accNoA) {
		this.accNoA = accNoA;
	}

	public String getAcctSeqNo() {
		return acctSeqNo;
	}

	public void setAcctSeqNo(String acctSeqNo) {
		this.acctSeqNo = acctSeqNo;
	}

	public String getSubSeq() {
		return subSeq;
	}

	public void setSubSeq(String subSeq) {
		this.subSeq = subSeq;
	}

	public String getTelTran() {
		return telTran;
	}

	public void setTelTran(String telTran) {
		this.telTran = telTran;
	}

	public String getBrcTran() {
		return brcTran;
	}

	public void setBrcTran(String brcTran) {
		this.brcTran = brcTran;
	}

	public String getBrcName() {
		return brcName;
	}

	public void setBrcName(String brcName) {
		this.brcName = brcName;
	}

	public String getNum1() {
		return num1;
	}

	public void setNum1(String num1) {
		this.num1 = num1;
	}

	public String getCustNam() {
		return custNam;
	}

	public void setCustNam(String custNam) {
		this.custNam = custNam;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFlgCanl() {
		return flgCanl;
	}

	public void setFlgCanl(String flgCanl) {
		this.flgCanl = flgCanl;
	}

	public String getSeqNoR() {
		return seqNoR;
	}

	public void setSeqNoR(String seqNoR) {
		this.seqNoR = seqNoR;
	}

	public String getAccNoA2() {
		return accNoA2;
	}

	public void setAccNoA2(String accNoA2) {
		this.accNoA2 = accNoA2;
	}

	public String getAccNamA2() {
		return accNamA2;
	}

	public void setAccNamA2(String accNamA2) {
		this.accNamA2 = accNamA2;
	}

	public String getBnkNamA2() {
		return bnkNamA2;
	}

	public void setBnkNamA2(String bnkNamA2) {
		this.bnkNamA2 = bnkNamA2;
	}

}