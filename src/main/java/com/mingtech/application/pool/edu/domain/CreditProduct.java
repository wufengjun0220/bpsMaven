package com.mingtech.application.pool.edu.domain;
import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Aug 19, 2010 3:23:58 PM
* @描述: [CreditProduct]信贷产品实体
*/
public class CreditProduct  implements java.io.Serializable {
    /** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private String id;
	private String crdtNo;//信贷业务号
	private String crdtType;//信贷产品类型  XD_01银承   XD_02流贷   XD_03保函   XD_04 信用证
	private String crdtTypeName;//信贷产品类型  XD_01银承   XD_02流贷   XD_03保函   XD_04 信用证
	private BigDecimal useAmt;//信贷产品合同金额(不含占用比例)
	private Date crdtIssDt;//借款日期
	private Date crdtDueDt;//信贷到期日
	private String custNo;//核心客户号
	private String custName;//客户名称名称
	private String sttlFlag;//结清标记   JQ_00:已结清   JQ_01：未结清
	private String crdtStatus;//业务状态   RZ_03：额度占用成功   JQ_00 已结清   存储MIS系统发过来的状态：JQ_01 取消放贷  JQ_02 手工提前终止出账   JQ_03 合同到期    JQ_04 合同终止
	private BigDecimal releaseAmt = new BigDecimal(0);//已还额度
	private BigDecimal restUseAmt;   //剩余待占用额度，本数据项不保存到数据库中
	private BigDecimal restReleaseAmt;  //剩余待释放额度，本数据项不保存到数据库中
	private String ccupy;//占用比例
	private BigDecimal restAmt;//业务余额:表示该融资业务剩余的待还金额
	private String risklevel;//风险等级	FX_01 高风险产品 FX_02 低风险产品 
	private String bpsNo;//票据池编号
	private String isOnline;//是否线上 1 是 0 否
	private Date minDueDate;//借据最早到期日
	
	private String ccupy1;//新占用比例--用于额度占用系数调整使用，不落库
	
	//===================================================未启用字段begin==========================================================================//
	private String crdtBankCode;//行号
	private String crdtBankName;//行名
	private String crdtRemark;//备注
	private BigDecimal crdtAmt;//信贷金额(暂时不使用)
	private BigDecimal crdtFrzdUsed = BigDecimal.ZERO;  //冻结已用
	private BigDecimal crdtFrzdFree = BigDecimal.ZERO;  //冻结可用
	private BigDecimal repayemnt = new BigDecimal(0);//已还金额(暂时不使用)	
	private String optEduReason;//额度操作原因，本数据项不保存到数据库中
	private String sumNum;         //总笔数
	private String sumMoney;       //总金额
	private String isForeignCurrency = "0";//是否外币 0人民币：到期自动还款  1外币：手动划款，到期不需要自动还款
	private String isElectron = "0";//是否电子银承 只针对融资类型为银承的信贷产品  0纸票 1电票
	private String sendToCore = "0";//增加发送核心还款状态字段--自动还款使用   是否已发送核心批量还款0 未发送 1已发送
	private String ycbzjAccount;//银承保证金账号
	private BigDecimal thisFreeAmt= BigDecimal.ZERO; ;//已还未释放金融，保存本次释放额度金额，用于外币手动释放时做判断，还款撤销及额度释放时做减额度操作
	private BigDecimal bzjAmt;//保证金余额;
	private String iousNo;//借据编号
	private String iousStatus;//借据状态
	private BigDecimal advanceAmt;//垫款金额
	private String disRelAmt;//手工释放额度描述 xk20130621
	//===================================================未启用字段end==========================================================================//
	
	
	
	
	
	public String getBpsNo() {
		return bpsNo;
	}

	public String getCcupy1() {
		return ccupy1;
	}

	public void setCcupy1(String ccupy1) {
		this.ccupy1 = ccupy1;
	}

	public Date getMinDueDate() {
		return minDueDate;
	}

	public void setMinDueDate(Date minDueDate) {
		this.minDueDate = minDueDate;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getYcbzjAccount() {
		return ycbzjAccount;
	}

	public void setYcbzjAccount(String ycbzjAccount) {
		this.ycbzjAccount = ycbzjAccount;
	}

	public String getSumNum() {
		return sumNum;
	}

	public void setSumNum(String sumNum) {
		this.sumNum = sumNum;
	}

	public String getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(String sumMoney) {
		this.sumMoney = sumMoney;
	}

	public BigDecimal getCrdtFrzdUsed() {
		return crdtFrzdUsed;
	}

	public void setCrdtFrzdUsed(BigDecimal crdtFrzdUsed) {
		this.crdtFrzdUsed = crdtFrzdUsed;
	}

	public BigDecimal getCrdtFrzdFree() {
		return crdtFrzdFree;
	}

	public void setCrdtFrzdFree(BigDecimal crdtFrzdFree) {
		this.crdtFrzdFree = crdtFrzdFree;
	}
	
    public String getCrdtNo() {
        return this.crdtNo;
    }
    
    public void setCrdtNo(String crdtNo) {
        this.crdtNo = crdtNo;
    }

    public String getCrdtType() {
        return this.crdtType;
    }
    
    public void setCrdtType(String crdtType) {
        this.crdtType = crdtType;
    }
    
	public void setCrdtAmt(BigDecimal crdtAmt) {
		this.crdtAmt = crdtAmt;
	}

	public BigDecimal getCrdtAmt(){
		return crdtAmt;
	}

	public BigDecimal getUseAmt(){
		return useAmt;
	}

	
	public void setUseAmt(BigDecimal useAmt){
		this.useAmt = useAmt;
	}

	public Date getCrdtIssDt() {
        return this.crdtIssDt;
    }
    
    public void setCrdtIssDt(Date crdtIssDt) {
        this.crdtIssDt = crdtIssDt;
    }

    public Date getCrdtDueDt() {
        return this.crdtDueDt;
    }
    
    public void setCrdtDueDt(Date crdtDueDt) {
        this.crdtDueDt = crdtDueDt;
    }

    public String getSttlFlag() {
        return this.sttlFlag;
    }
    
    public void setSttlFlag(String sttlFlag) {
        this.sttlFlag = sttlFlag;
    }

    public String getCustNo() {
        return this.custNo;
    }
    
    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

	
	public String getCrdtStatus(){
		return crdtStatus;
	}

	
	public void setCrdtStatus(String crdtStatus){
		this.crdtStatus = crdtStatus;
	}

	
	public String getCrdtBankCode(){
		return crdtBankCode;
	}

	
	public void setCrdtBankCode(String crdtBankCode){
		this.crdtBankCode = crdtBankCode;
	}

	
	public String getCrdtBankName(){
		return crdtBankName;
	}

	
	public void setCrdtBankName(String crdtBankName){
		this.crdtBankName = crdtBankName;
	}

	
	public String getCrdtRemark(){
		return crdtRemark;
	}

	
	public void setCrdtRemark(String crdtRemark){
		this.crdtRemark = crdtRemark;
	}
	public String getCrdtTypeName(){
		return (String)PoolDictionaryCache.getFromPoolDictMap(crdtType);
	}
	public String getCrdtStatusName(){
		return (String)PoolDictionaryCache.getFromPoolDictMap(crdtStatus);
	}
	public String getSttlFlagName(){
		return (String)PoolDictionaryCache.getFromPoolDictMap(sttlFlag);
	}
	/**
	 * 剩余待占用额度，本数据项不保存到数据库中
	 * @return
	 */
	public BigDecimal getRestUseAmt() {
		return restUseAmt;
	}

	public void setRestUseAmt(BigDecimal restUseAmt) {
		this.restUseAmt = restUseAmt;
	}

	
	public String getCustName(){
		return custName;
	}

	
	public void setCustName(String custName){
		this.custName = custName;
	}

	public BigDecimal getRestReleaseAmt() {
		return restReleaseAmt;
	}

	public void setRestReleaseAmt(BigDecimal restReleaseAmt) {
		this.restReleaseAmt = restReleaseAmt;
	}

	public String getOptEduReason() {
		return optEduReason;
	}

	public void setOptEduReason(String optEduReason) {
		this.optEduReason = optEduReason;
	}

	
	public BigDecimal getReleaseAmt(){
		return releaseAmt;
	}

	
	public void setReleaseAmt(BigDecimal releaseAmt){
		this.releaseAmt = releaseAmt;
	}

	public BigDecimal getRepayemnt() {
		if(null==repayemnt){
			return  new BigDecimal(0);
		}
		return repayemnt;
	}

	public void setRepayemnt(BigDecimal repayemnt) {
		this.repayemnt = repayemnt;
	}

	public String getIsForeignCurrency() {
		return isForeignCurrency;
	}

	public void setIsForeignCurrency(String isForeignCurrency) {
		this.isForeignCurrency = isForeignCurrency;
	}

	public String getIsElectron() {
		return isElectron;
	}

	public void setIsElectron(String isElectron) {
		this.isElectron = isElectron;
	}

	public String getSendToCore() {
		return sendToCore;
	}

	public void setSendToCore(String sendToCore) {
		this.sendToCore = sendToCore;
	}

	public BigDecimal getThisFreeAmt() {
		if(null==thisFreeAmt){
			return	new BigDecimal(0);
		}
		return thisFreeAmt;
	}

	public void setThisFreeAmt(BigDecimal thisFreeAmt) {
		this.thisFreeAmt = thisFreeAmt;
	}

	public String getDisRelAmt() {
		return disRelAmt;
	}

	public void setDisRelAmt(String disRelAmt) {
		this.disRelAmt = disRelAmt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCcupy() {
		return ccupy;
	}

	public void setCcupy(String ccupy) {
		this.ccupy = ccupy;
	}

	public BigDecimal getRestAmt() {
		return restAmt;
	}

	public void setRestAmt(BigDecimal restAmt) {
		this.restAmt = restAmt;
	}

	public String getRisklevel() {
		return risklevel;
	}

	public void setRisklevel(String risklevel) {
		this.risklevel = risklevel;
	}

	public BigDecimal getBzjAmt() {
		return bzjAmt;
	}

	public void setBzjAmt(BigDecimal bzjAmt) {
		this.bzjAmt = bzjAmt;
	}

	public String getIousNo() {
		return iousNo;
	}

	public void setIousNo(String iousNo) {
		this.iousNo = iousNo;
	}

	public String getIousStatus() {
		return iousStatus;
	}

	public void setIousStatus(String iousStatus) {
		this.iousStatus = iousStatus;
	}

	public BigDecimal getAdvanceAmt() {
		return advanceAmt;
	}

	public void setAdvanceAmt(BigDecimal advanceAmt) {
		this.advanceAmt = advanceAmt;
	}



	public String getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	public void setCrdtTypeName(String crdtTypeName) {
		this.crdtTypeName = crdtTypeName;
	}

	

	 
}