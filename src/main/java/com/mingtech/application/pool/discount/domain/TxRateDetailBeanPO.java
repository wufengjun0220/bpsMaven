package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;

import com.mingtech.application.utils.ExcelImport;

public class TxRateDetailBeanPO {
	//  利率详情字段
	private String id;
	private String rateType;
	
	@ExcelImport("期限(月)")
	private String term;						//	期限(月)
	@ExcelImport("本行")
	private BigDecimal ownBank;					//	本行
	private BigDecimal ownBank1;					//	本行
	@ExcelImport("国股")
	private BigDecimal stateShares;				//	国股
	private BigDecimal stateShares1;				//	国股
	@ExcelImport("股份制")
	private BigDecimal sharesSys;				//	股份制
	private BigDecimal sharesSys1;				//	股份制
	@ExcelImport("城商行")
	private BigDecimal cityBank;				//	城商行
	private BigDecimal cityBank1;				//	城商行
	@ExcelImport("农商行")
	private BigDecimal agriCommBank;			//	农商行
	private BigDecimal agriCommBank1;			//	农商行
	@ExcelImport("TYPE6")
	private BigDecimal type6;					//	预留字段
	private BigDecimal type61;					//	预留字段
	@ExcelImport("TYPE7")
	private BigDecimal type7;					//	预留字段
	private BigDecimal type71;					//	预留字段
	@ExcelImport("TYPE8")
	private BigDecimal type8;					//	预留字段
	private BigDecimal type81;					//	预留字段
	@ExcelImport("TYPE9")
	private BigDecimal type9;					//	预留字段
	private BigDecimal type91;					//	预留字段
	@ExcelImport("TYPE10")
	private BigDecimal type10;					//	预留字段
	private BigDecimal type101;					//	预留字段
	private String batchNo;
	private String effTime;
	
	public BigDecimal getOwnBank1() {
		return ownBank1;
	}
	public void setOwnBank1(BigDecimal ownBank1) {
		this.ownBank1 = ownBank1;
	}
	public BigDecimal getStateShares1() {
		return stateShares1;
	}
	public void setStateShares1(BigDecimal stateShares1) {
		this.stateShares1 = stateShares1;
	}
	public BigDecimal getSharesSys1() {
		return sharesSys1;
	}
	public void setSharesSys1(BigDecimal sharesSys1) {
		this.sharesSys1 = sharesSys1;
	}
	public BigDecimal getCityBank1() {
		return cityBank1;
	}
	public void setCityBank1(BigDecimal cityBank1) {
		this.cityBank1 = cityBank1;
	}
	public BigDecimal getAgriCommBank1() {
		return agriCommBank1;
	}
	public void setAgriCommBank1(BigDecimal agriCommBank1) {
		this.agriCommBank1 = agriCommBank1;
	}
	public BigDecimal getType61() {
		return type61;
	}
	public void setType61(BigDecimal type61) {
		this.type61 = type61;
	}
	public BigDecimal getType71() {
		return type71;
	}
	public void setType71(BigDecimal type71) {
		this.type71 = type71;
	}
	public BigDecimal getType81() {
		return type81;
	}
	public void setType81(BigDecimal type81) {
		this.type81 = type81;
	}
	public BigDecimal getType91() {
		return type91;
	}
	public void setType91(BigDecimal type91) {
		this.type91 = type91;
	}
	public BigDecimal getType101() {
		return type101;
	}
	public void setType101(BigDecimal type101) {
		this.type101 = type101;
	}
	private TxRateMaintainInfo txRateMaintainInfo;
	
	public TxRateMaintainInfo getTxRateMaintainInfo() {
		return txRateMaintainInfo;
	}
	public void setTxRateMaintainInfo(TxRateMaintainInfo txRateMaintainInfo) {
		this.txRateMaintainInfo = txRateMaintainInfo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public BigDecimal getOwnBank() {
		return ownBank;
	}
	public void setOwnBank(BigDecimal ownBank) {
		this.ownBank = ownBank;
	}
	public BigDecimal getStateShares() {
		return stateShares;
	}
	public void setStateShares(BigDecimal stateShares) {
		this.stateShares = stateShares;
	}
	public BigDecimal getSharesSys() {
		return sharesSys;
	}
	public void setSharesSys(BigDecimal sharesSys) {
		this.sharesSys = sharesSys;
	}
	public BigDecimal getCityBank() {
		return cityBank;
	}
	public void setCityBank(BigDecimal cityBank) {
		this.cityBank = cityBank;
	}
	public BigDecimal getAgriCommBank() {
		return agriCommBank;
	}
	public void setAgriCommBank(BigDecimal agriCommBank) {
		this.agriCommBank = agriCommBank;
	}
	public BigDecimal getType6() {
		return type6;
	}
	public void setType6(BigDecimal type6) {
		this.type6 = type6;
	}
	public BigDecimal getType7() {
		return type7;
	}
	public void setType7(BigDecimal type7) {
		this.type7 = type7;
	}
	public BigDecimal getType8() {
		return type8;
	}
	public void setType8(BigDecimal type8) {
		this.type8 = type8;
	}
	public BigDecimal getType9() {
		return type9;
	}
	public void setType9(BigDecimal type9) {
		this.type9 = type9;
	}
	public BigDecimal getType10() {
		return type10;
	}
	public void setType10(BigDecimal type10) {
		this.type10 = type10;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getEffTime() {
		return effTime;
	}
	public void setEffTime(String effTime) {
		this.effTime = effTime;
	}					
	
	
}
