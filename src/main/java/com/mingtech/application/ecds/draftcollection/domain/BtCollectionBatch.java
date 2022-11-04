package com.mingtech.application.ecds.draftcollection.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * BtCollectionBatch entity.
 * @author MyEclipse Persistence Tools
 */
public class BtCollectionBatch implements java.io.Serializable{
	
	private String collectbatchid;//托收批次id
	private Date collectDt;//托收日期
	private Date cretTime;//创建时间
//	private User user;//操作员id
	private String userId;
	private String userName;//操作员id
	private String SBranchId;//机构号
	
	//---托收对方行 信息
	private String bankNumber;//对方大额行号
	private String collectBank;//对方托收行名称
	private String collectAddr;//对方托收地址
	private String collectPostcode;//对方托收邮编
	private String collectPhone;//对方托收电话
	
	//托收方  我行信息
	private String sendUserName;//联系人
	private String sendCollectName;//托收申请方名称
	private String sendBankAddr;//地址
	private String sendBankPostcode;//邮编
	private String sendBankPhone;//电话
	//我方 回款账户信息
	private String sendAcctNo;//账号
	private String sendAcctBankNo;//账号开户行行号
	private String sendAcctBankName;//账号开户行名称
	
	
	private String collectOperSts;//托收状态
//	private Set collectionDtos = new HashSet(0);//发托明细信息
	private String SBatchNo; // 批次号
	private Integer totalSize; 
	private BigDecimal totalMoney; 
	private String	SOwnerOrgCode	;// (申请人)持有人组织机构号  票据池代保管的票使用  2011-01-14 针对票据池——代保管的票据，组批时，应该校验同一个承兑行+同一个申请人+同一个经办机构才能产生新信封。
	private String	SMbfeBankCode	;// 票据持有行行号（大额行号）票据池代保管的票使用	2011-01-14 针对票据池——代保管的票据，组批时，应该校验同一个承兑行+同一个申请人+同一个经办机构才能产生新信封。
	
	private String	totalMoneyShow	;
	private String	totalSizeShow	;
	private String SifPoolFlag;//是否是票据池对象
	private String product_id;     // 产品ID
	private String batchType;  //批次类型 AC01银票批次AC02商票票批次
	
	private int totalSizeHJ;
	private BigDecimal totalMoneyHJ; 
	private String printFlag; //票据是否打印
	
	public String getPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(String printFlag) {
		this.printFlag = printFlag;
	}

	public String getCollectbatchid(){
		return this.collectbatchid;
	}

	public void setCollectbatchid(String collectbatchid){
		this.collectbatchid = collectbatchid;
	}

	public String getBankNumber(){
		return bankNumber;
	}

	
	public void setBankNumber(String bankNumber){
		this.bankNumber = bankNumber;
	}

	public Date getCollectDt(){
		return this.collectDt;
	}

	public void setCollectDt(Date collectDt){
		this.collectDt = collectDt;
	}

	public String getCollectBank(){
		return this.collectBank;
	}

	public void setCollectBank(String collectBank){
		this.collectBank = collectBank;
	}

	public String getCollectAddr(){
		return this.collectAddr;
	}

	public void setCollectAddr(String collectAddr){
		this.collectAddr = collectAddr;
	}

	public String getCollectPostcode(){
		return this.collectPostcode;
	}

	public void setCollectPostcode(String collectPostcode){
		this.collectPostcode = collectPostcode;
	}

	public String getCollectOperSts(){
		return this.collectOperSts;
	}

	public void setCollectOperSts(String collectOperSts){
		this.collectOperSts = collectOperSts;
	}
	
	public String getSBatchNo() {
		return SBatchNo;
	}

	public void setSBatchNo(String batchNo) {
		SBatchNo = batchNo;
	}


	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
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

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	
	public String getSOwnerOrgCode(){
		return SOwnerOrgCode;
	}

	
	public void setSOwnerOrgCode(String ownerOrgCode){
		SOwnerOrgCode = ownerOrgCode;
	}

	
	public String getSMbfeBankCode(){
		return SMbfeBankCode;
	}

	
	public void setSMbfeBankCode(String mbfeBankCode){
		SMbfeBankCode = mbfeBankCode;
	}

	
	public String getBatchType(){
		return batchType;
	}

	
	public void setBatchType(String batchType){
		this.batchType = batchType;
	}

	
	public int getTotalSizeHJ(){
		return totalSizeHJ;
	}

	
	public void setTotalSizeHJ(int totalSizeHJ){
		this.totalSizeHJ = totalSizeHJ;
	}

	
	public BigDecimal getTotalMoneyHJ(){
		return totalMoneyHJ;
	}

	
	public void setTotalMoneyHJ(BigDecimal totalMoneyHJ){
		this.totalMoneyHJ = totalMoneyHJ;
	}
	public Date getCretTime() {
		return cretTime;
	}

	public void setCretTime(Date cretTime) {
		this.cretTime = cretTime;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getCollectPhone() {
		return collectPhone;
	}

	public void setCollectPhone(String collectPhone) {
		this.collectPhone = collectPhone;
	}

	public String getSendUserName() {
		return sendUserName;
	}

	public void setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
	}

	public String getSendCollectName() {
		return sendCollectName;
	}

	public void setSendCollectName(String sendCollectName) {
		this.sendCollectName = sendCollectName;
	}

	public String getSendBankAddr() {
		return sendBankAddr;
	}

	public void setSendBankAddr(String sendBankAddr) {
		this.sendBankAddr = sendBankAddr;
	}

	public String getSendBankPostcode() {
		return sendBankPostcode;
	}

	public void setSendBankPostcode(String sendBankPostcode) {
		this.sendBankPostcode = sendBankPostcode;
	}


	public String getSendBankPhone() {
		return sendBankPhone;
	}

	public void setSendBankPhone(String sendBankPhone) {
		this.sendBankPhone = sendBankPhone;
	}

	public String getSendAcctNo() {
		return sendAcctNo;
	}

	public void setSendAcctNo(String sendAcctNo) {
		this.sendAcctNo = sendAcctNo;
	}

	public String getSendAcctBankNo() {
		return sendAcctBankNo;
	}

	public void setSendAcctBankNo(String sendAcctBankNo) {
		this.sendAcctBankNo = sendAcctBankNo;
	}

	public String getSendAcctBankName() {
		return sendAcctBankName;
	}

	public void setSendAcctBankName(String sendAcctBankName) {
		this.sendAcctBankName = sendAcctBankName;
	}

	public String getSBranchId() {
		return SBranchId;
	}

	public void setSBranchId(String sBranchId) {
		SBranchId = sBranchId;
	}
	
}