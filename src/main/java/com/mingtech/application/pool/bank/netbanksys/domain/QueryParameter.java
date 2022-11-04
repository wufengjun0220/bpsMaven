package com.mingtech.application.pool.bank.netbanksys.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;

/**
 * 
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
* @作者: gaofubing
* @日期: Jan 21, 2010 2:31:38 PM
* @描述: [QueryParameter]封装网银接口查询参数
 */
public class QueryParameter{
	private String queryType; // 查询种类
	private String billType; // 票据类型
	private String billMedia; // 票据介质
	private Date issueDateBegin; // 出票日开始
	private Date issueDateEnd; // 出票日结束
	private Date dueDateBegin; // 票据到期日开始
	private Date dueDateEnd; // 票据到期日结束
	private BigDecimal billAmountBegin; // 票据金额开始
	private BigDecimal billAmountEnd; // 票据金额结束
	private String billNo; // 票据号码（如票据号码不为空，则除企业帐号外其它选项忽略）
	private String orgCode;//核心客户号
	private List accounts; // 企业帐号
	private String plDrwrAcctSvcrNm;//出票人开户行名称
    private String isGenerateEdu;//是否产生额度
    private String riskType;//风险类型
    private String poolArgumentNo;//票据池编号
    private PedProtocolDto protocolDto;//票据池协议
    private PedProtocolList proList;//票据池协议成员对象
    private String SBanEndrsmtFlag;//不得转让标记   0：可转让   1：不可转让
    
    /*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	
	private String drawerAcctName;//出票人账户名称
	private String acceptorAcctName;//承兑人账户名称
	
	
	/*** 融合改造新增字段  end*/
	
	
	public String getSBanEndrsmtFlag() {
		return SBanEndrsmtFlag;
	}

	

	public String getDrawerAcctName() {
		return drawerAcctName;
	}



	public void setDrawerAcctName(String drawerAcctName) {
		this.drawerAcctName = drawerAcctName;
	}



	public String getAcceptorAcctName() {
		return acceptorAcctName;
	}



	public void setAcceptorAcctName(String acceptorAcctName) {
		this.acceptorAcctName = acceptorAcctName;
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

	public void setSBanEndrsmtFlag(String sBanEndrsmtFlag) {
		SBanEndrsmtFlag = sBanEndrsmtFlag;
	}

	public PedProtocolList getProList() {
		return proList;
	}

	public void setProList(PedProtocolList proList) {
		this.proList = proList;
	}

	public PedProtocolDto getProtocolDto() {
		return protocolDto;
	}

	public void setProtocolDto(PedProtocolDto protocolDto) {
		this.protocolDto = protocolDto;
	}

	public String getPoolArgumentNo() {
		return poolArgumentNo;
	}

	public void setPoolArgumentNo(String poolArgumentNo) {
		this.poolArgumentNo = poolArgumentNo;
	}

	public String getPlDrwrAcctSvcrNm() {
		return plDrwrAcctSvcrNm;
	}

	public void setPlDrwrAcctSvcrNm(String plDrwrAcctSvcrNm) {
		this.plDrwrAcctSvcrNm = plDrwrAcctSvcrNm;
	}

	public String getAcceptorBankCode() {
		return acceptorBankCode;
	}

	public void setAcceptorBankCode(String acceptorBankCode) {
		this.acceptorBankCode = acceptorBankCode;
	}

	private String acceptorBankCode; //票据承兑行行号
	
	

	private int recordCount = 500; // 记录总数
	//private String tradeType; //交易类型，按交易查询时，表示交易的种类
	
	public String getBillNo(){
		return billNo;
	}
	
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public void setBillNo(String billNo){
		this.billNo = billNo;
	}
	
	public List getAccounts(){
		return accounts;
	}
	
	public void setAccounts(List accounts){
		this.accounts = accounts;
	}
	
	
	
	public String getQueryType(){
		return queryType;
	}
	
	public String getBillMedia() {
		return billMedia;
	}

	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}

	public void setQueryType(String queryType){
		this.queryType = queryType;
	}
	
	public String getBillType(){
		return billType;
	}
	
	public void setBillType(String billType){
		this.billType = billType;
	}
	
	public Date getIssueDateBegin(){
		return issueDateBegin;
	}
	
	public void setIssueDateBegin(Date issueDateBegin){
		this.issueDateBegin = issueDateBegin;
	}
	
	public Date getIssueDateEnd(){
		return issueDateEnd;
	}
	
	public void setIssueDateEnd(Date issueDateEnd){
		this.issueDateEnd = issueDateEnd;
	}
	
	public Date getDueDateBegin(){
		return dueDateBegin;
	}
	
	public void setDueDateBegin(Date dueDateBegin){
		this.dueDateBegin = dueDateBegin;
	}
	
	public Date getDueDateEnd(){
		return dueDateEnd;
	}
	
	public void setDueDateEnd(Date dueDateEnd){
		this.dueDateEnd = dueDateEnd;
	}
	
	public BigDecimal getBillAmountBegin(){
		return billAmountBegin;
	}
	
	public void setBillAmountBegin(BigDecimal billAmountBegin){
		this.billAmountBegin = billAmountBegin;
	}
	
	public BigDecimal getBillAmountEnd(){
		return billAmountEnd;
	}
	
	public void setBillAmountEnd(BigDecimal billAmountEnd){
		this.billAmountEnd = billAmountEnd;
	}
	
	public int getRecordCount(){
		return recordCount;
	}
	
	public void setRecordCount(int recordCount){
		this.recordCount = recordCount;
	}

    public String getIsGenerateEdu() {
        return isGenerateEdu;
    }

    public void setIsGenerateEdu(String isGenerateEdu) {
        this.isGenerateEdu = isGenerateEdu;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    //	public String getTradeType(){
//		return tradeType;
//	}
//
//	
//	public void setTradeType(String tradeType){
//		this.tradeType = tradeType;
//	}

}
