package com.mingtech.application.pool.trust.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.base.domain.PoolBase;
import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Aug 19, 2010 2:26:53 PM
* @描述: [DraftPool]票据池-代保管票据实体PL_STORAGE
*/
public class DraftStorage extends PoolBase implements java.io.Serializable {
	private static final long serialVersionUID = 4017336342201719327L;


	private String plBatchId;// 批次ID


	/** 基本信息 **/
	private String plDraftNb;//票据号码
	private String plDraftMedia;//票据介质
	private String plDraftType;//票据类型
	private Date plIsseDt;//出票日
	private Date plDueDt;//到期日
	
	/** 出票人信息 **/
	private String plDrwrNm;//出票人名称
	private String plDrwrAcctId;//出票人账号
	private String plDrwrAcctSvcr;//出票人开户行行号
	private String plDrwrAcctSvcrNm;//出票人开户行名称
	
	/** 收款人信息 **/
	private String plPyeeNm;//收款人名称
	private String plPyeeAcctId;//收款人账号
	private String plPyeeAcctSvcr;//收款人开户行行号
	private String plPyeeAcctSvcrNm;//收款人开户行名称
	
	/** 承兑人信息 **/
	private String plAccptrNm;//承兑人名称
	private String plAccptrId;//承兑人账号
	private String plAccptrSvcr;//承兑人开户行行号
	private String plAccptrSvcrNm;//承兑人开户行名称
	
	/** 池业务信息 **/
	private Date plTm;//入池日期
	private String plSameCity;//同城异地标识
	private String lastTransType;//最后一次业务类型
	private String lastTransId;//最后一次业务id，关联业务表di
	private String plRecSvcr;//业务经办行行号
	private String plRecSvcrNm;//业务经办行名称
	private BigDecimal totalCharge;//总费用

	//2010-10-13添加 发托使用
	private PoolBillInfo poolBillInfo;// 票据信息对象
	
	private String draftFlag;     //托管票据贴现为：1,；托管转质押为：2
	
	/** 待确认 **/
	//2010-9-19添加，与业务表保持一致
	private String plAccptrAddress;     // 承兑人地址
	private String plAccptrProto;//承兑协议号
	private Date plAccptrDt;//承兑日
	private String plTradeProto;//交易合同号码
	private String productId;//产品id
	//2010-11-26增加存票业务批次号，记账时使用，存票记账查询成功后更新此批次号
	/** 存票业务批次号，记账时使用，存票记账查询成功后更新此批次号*/
	private String inBatchNum;
	private String acctBankNum;    //记账网点
	private String workerName;// 经办人姓名
	private String chargeFlag;// 扣费成功失败标识
	
	/* 页面显示中文名称,不保存数据库 */
	private String plDraftMediaName;//票据介质名称
	private String plDraftTypeName;//票据类型名称
	private String plStatusName;//状态中文注释
	
	/**
	弃用
	private String totalAmt;   //总金额
	private String totalNum;   //总笔数
	 */

	
	public BigDecimal getTotalCharge() {
		return totalCharge;
	}

	public void setTotalCharge(BigDecimal totalCharge) {
		this.totalCharge = totalCharge;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

//	public String getId(){
//		return id;
//	}
//	
//	public void setId(String id){
//		this.id = id;
//	}
	
	public String getPlDraftNb(){
		return plDraftNb;
	}
	
	public String getDraftFlag() {
		return draftFlag;
	}

	public void setDraftFlag(String draftFlag) {
		this.draftFlag = draftFlag;
	}

	public void setPlDraftNb(String plDraftNb){
		this.plDraftNb = plDraftNb;
	}
	
	public String getPlDraftMedia(){
		return plDraftMedia;
	}
	
	public void setPlDraftMedia(String plDraftMedia){
		this.plDraftMedia = plDraftMedia;
	}
	
	public String getPlDraftType(){
		return plDraftType;
	}
	
	public void setPlDraftType(String plDraftType){
		this.plDraftType = plDraftType;
	}
	
//	public String getPlIsseAmtValue(){
//		return plIsseAmtValue;
//	}
//	
//	public void setPlIsseAmtValue(String plIsseAmtValue){
//		this.plIsseAmtValue = plIsseAmtValue;
//	}
//	
//	public BigDecimal getPlIsseAmt(){
//		return plIsseAmt;
//	}
//	
//	public void setPlIsseAmt(BigDecimal plIsseAmt){
//		this.plIsseAmt = plIsseAmt;
//	}
	
	public Date getPlIsseDt(){
		return plIsseDt;
	}
	
	public void setPlIsseDt(Date plIsseDt){
		this.plIsseDt = plIsseDt;
	}
	
	public Date getPlDueDt(){
		return plDueDt;
	}
	
	public void setPlDueDt(Date plDueDt){
		this.plDueDt = plDueDt;
	}
	
	public String getPlDrwrNm(){
		return plDrwrNm;
	}
	
	public void setPlDrwrNm(String plDrwrNm){
		this.plDrwrNm = plDrwrNm;
	}
	
	public String getPlDrwrAcctId(){
		return plDrwrAcctId;
	}
	
	public void setPlDrwrAcctId(String plDrwrAcctId){
		this.plDrwrAcctId = plDrwrAcctId;
	}
	
	public String getPlDrwrAcctSvcr(){
		return plDrwrAcctSvcr;
	}
	
	public void setPlDrwrAcctSvcr(String plDrwrAcctSvcr){
		this.plDrwrAcctSvcr = plDrwrAcctSvcr;
	}
	
	public String getPlDrwrAcctSvcrNm(){
		return plDrwrAcctSvcrNm;
	}
	
	public void setPlDrwrAcctSvcrNm(String plDrwrAcctSvcrNm){
		this.plDrwrAcctSvcrNm = plDrwrAcctSvcrNm;
	}
	
	public String getPlPyeeNm(){
		return plPyeeNm;
	}
	
	public void setPlPyeeNm(String plPyeeNm){
		this.plPyeeNm = plPyeeNm;
	}
	
	public String getPlPyeeAcctId(){
		return plPyeeAcctId;
	}
	
	public void setPlPyeeAcctId(String plPyeeAcctId){
		this.plPyeeAcctId = plPyeeAcctId;
	}
	
	public String getPlPyeeAcctSvcr(){
		return plPyeeAcctSvcr;
	}
	
	public void setPlPyeeAcctSvcr(String plPyeeAcctSvcr){
		this.plPyeeAcctSvcr = plPyeeAcctSvcr;
	}
	
	public String getPlPyeeAcctSvcrNm(){
		return plPyeeAcctSvcrNm;
	}
	
	public void setPlPyeeAcctSvcrNm(String plPyeeAcctSvcrNm){
		this.plPyeeAcctSvcrNm = plPyeeAcctSvcrNm;
	}
	
	public String getPlAccptrNm(){
		return plAccptrNm;
	}
	
	public void setPlAccptrNm(String plAccptrNm){
		this.plAccptrNm = plAccptrNm;
	}
	
	public String getPlAccptrId(){
		return plAccptrId;
	}
	
	public void setPlAccptrId(String plAccptrId){
		this.plAccptrId = plAccptrId;
	}
	
	public String getPlAccptrSvcr(){
		return plAccptrSvcr;
	}
	
	public void setPlAccptrSvcr(String plAccptrSvcr){
		this.plAccptrSvcr = plAccptrSvcr;
	}
	
	public String getPlAccptrSvcrNm(){
		return plAccptrSvcrNm;
	}
	
	public void setPlAccptrSvcrNm(String plAccptrSvcrNm){
		this.plAccptrSvcrNm = plAccptrSvcrNm;
	}
	
//	public String getPlApplyNm(){
//		return plApplyNm;
//	}
//	
//	public void setPlApplyNm(String plApplyNm){
//		this.plApplyNm = plApplyNm;
//	}
//	
//	public String getPlCommId(){
//		return plCommId;
//	}
//	
//	public void setPlCommId(String plCommId){
//		this.plCommId = plCommId;
//	}
//	
//	public String getPlApplyAcctId(){
//		return plApplyAcctId;
//	}
//	
//	public void setPlApplyAcctId(String plApplyAcctId){
//		this.plApplyAcctId = plApplyAcctId;
//	}
//	
//	public String getPlApplyAcctSvcr(){
//		return plApplyAcctSvcr;
//	}
//	
//	public void setPlApplyAcctSvcr(String plApplyAcctSvcr){
//		this.plApplyAcctSvcr = plApplyAcctSvcr;
//	}
//	
//	public String getPlApplyAcctSvcrNm(){
//		return plApplyAcctSvcrNm;
//	}
//	
//	public void setPlApplyAcctSvcrNm(String plApplyAcctSvcrNm){
//		this.plApplyAcctSvcrNm = plApplyAcctSvcrNm;
//	}
//	
//	public String getPlRecSvcr(){
//		return plRecSvcr;
//	}
//	
//	public void setPlRecSvcr(String plRecSvcr){
//		this.plRecSvcr = plRecSvcr;
//	}
//	
//	public String getPlRecSvcrNm(){
//		return plRecSvcrNm;
//	}
//	
//	public void setPlRecSvcrNm(String plRecSvcrNm){
//		this.plRecSvcrNm = plRecSvcrNm;
//	}
//	
//	public String getPlStatus(){
//		return plStatus;
//	}
//	
//	public void setPlStatus(String plStatus){
//		this.plStatus = plStatus;
//	}
	
	public Date getPlTm(){
		return plTm;
	}
	
	public void setPlTm(Date plTm){
		this.plTm = plTm;
	}

	
	public String getPlSameCity(){
		return plSameCity;
	}

	
	public void setPlSameCity(String plSameCity){
		this.plSameCity = plSameCity;
	}

	
	public String getLastTransType(){
		return lastTransType;
	}

	
	public void setLastTransType(String lastTransType){
		this.lastTransType = lastTransType;
	}

	
	public String getLastTransId(){
		return lastTransId;
	}

	
	public void setLastTransId(String lastTransId){
		this.lastTransId = lastTransId;
	}

	public String getPlDraftMediaName() {
		return PoolDictionaryCache.getBillMedia(this.plDraftMedia);
	}

	public String getPlDraftTypeName() {
		return PoolDictionaryCache.getBillType(this.plDraftType);
	}

	public String getPlStatusName() {
		return PoolDictionaryCache.getFromPoolDictMap(this.getPlStatus());
	}

	
	public String getPlAccptrAddress(){
		return plAccptrAddress;
	}

	
	public void setPlAccptrAddress(String plAccptrAddress){
		this.plAccptrAddress = plAccptrAddress;
	}

	
	public String getPlAccptrProto(){
		return plAccptrProto;
	}

	
	public void setPlAccptrProto(String plAccptrProto){
		this.plAccptrProto = plAccptrProto;
	}

	
	public Date getPlAccptrDt(){
		return plAccptrDt;
	}

	
	public void setPlAccptrDt(Date plAccptrDt){
		this.plAccptrDt = plAccptrDt;
	}

	
	public String getPlTradeProto(){
		return plTradeProto;
	}

	
	public void setPlTradeProto(String plTradeProto){
		this.plTradeProto = plTradeProto;
	}

	
//	public String getPlRemark(){
//		return plRemark;
//	}
//
//	
//	public void setPlRemark(String plRemark){
//		this.plRemark = plRemark;
//	}

	
	public PoolBillInfo getPoolBillInfo(){
		return poolBillInfo;
	}

	
	public void setPoolBillInfo(PoolBillInfo poolBillInfo){
		this.poolBillInfo = poolBillInfo;
	}

	
	public String getProductId(){
		return productId;
	}

	
	public void setProductId(String productId){
		this.productId = productId;
	}

	
	public String getInBatchNum(){
		return inBatchNum;
	}

	
	public void setInBatchNum(String inBatchNum){
		this.inBatchNum = inBatchNum;
	}

	public String getAcctBankNum() {
		return acctBankNum;
	}

	public void setAcctBankNum(String acctBankNum) {
		this.acctBankNum = acctBankNum;
	}

//	public String getTotalAmt() {
//		return totalAmt;
//	}
//
//	public void setTotalAmt(String totalAmt) {
//		this.totalAmt = totalAmt;
//	}
//
//	public String getTotalNum() {
//		return totalNum;
//	}
//
//	public void setTotalNum(String totalNum) {
//		this.totalNum = totalNum;
//	}

	public String getChargeFlag() {
		return chargeFlag;
	}

	public void setChargeFlag(String chargeFlag) {
		this.chargeFlag = chargeFlag;
	}

//	public String getBranchId() {
//		return branchId;
//	}
//
//	public void setBranchId(String branchId) {
//		this.branchId = branchId;
//	}

	public String getPlBatchId() {
		return plBatchId;
	}

	public void setPlBatchId(String plBatchId) {
		this.plBatchId = plBatchId;
	}

	public String getPlRecSvcr() {
		return plRecSvcr;
	}

	public void setPlRecSvcr(String plRecSvcr) {
		this.plRecSvcr = plRecSvcr;
	}

	public String getPlRecSvcrNm() {
		return plRecSvcrNm;
	}

	public void setPlRecSvcrNm(String plRecSvcrNm) {
		this.plRecSvcrNm = plRecSvcrNm;
	}

//	public String getPlTradeType() {
//		return plTradeType;
//	}
//
//	public void setPlTradeType(String plTradeType) {
//		this.plTradeType = plTradeType;
//	}

}