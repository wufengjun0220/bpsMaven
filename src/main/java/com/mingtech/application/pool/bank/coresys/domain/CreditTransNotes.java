package com.mingtech.application.pool.bank.coresys.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * MIS系统客户端字段组装
 * @author Ju Nana
 * @version v1.0
 * @date 2019-10-29
 */
/**
 * @author Ju Nana
 * @version v1.0
 * @date 2019-11-6
 */
public class CreditTransNotes {
	
	private String      CLIENT_NAME            ;//客户名称
	private String      CREDIT_CODE            ;//额度系统授信编号                        
	private String      CORE_CLIENT_NO         ;//核心客户号                              
	private String      LIMIT_TYPE             ;//额度种类                                
	private String      GENERATE_MODE          ;//创建方式                                
	private String      CREDIT_LIMIT_AMT_CCY   ;//授信币种                                
	private BigDecimal  NOMINAL_AMT            ;//名义金额                  
	private BigDecimal  EXEC_EXPOSURE_AMT      ;//敞口金额                  
	private BigDecimal  AVAIL_NOMINAL_AMT      ;//可用名义金额              
	private BigDecimal  AVAIL_EXEC_EXPOSURE_AMT;//可用敞口金额              
	private BigDecimal  RT_CREDIT_RATE         ;//实时用信率                
	private BigDecimal  HIGH_NOMINAL_AMT       ;//名义金额使用峰值          
	private BigDecimal  HIGH_EXEC_EXPOSURE_AMT ;//敞口金额使用峰值          
	private String      LIMIT_STATUS           ;//额度状态                  
	private String      LIMIT_FROZEN_STATUS    ;//额度冻结状态              
	private String      CYCLE_FLAG             ;//可循环标识                
	private String      TERM                   ;//期限（月）                
	private String      EFFECTIVE_DATE         ;//生效日期                  
	private String      EXPIRY_DATE            ;//到期日                    
	private String      ACCT_MANAGER_TELLER_NO ;//管户人                    
	private String      ACCT_MANAGER_BRANCH_ID ;//管户机构                  
	private String      RULE_CODE              ;//产品组规则编号        		
	private String      BUSS_CATEGORY_CODE     ;//业务品种编号              
	private String      CCY                    ;//币种                      
	private String      billNo                 ;//票号                      
	private BigDecimal  billsum                ;//票面金额                  
	private String      currency               ;//币种                      
	private String      billType               ;//票据种类 
	private String      billMedia               ;//票据介质
	private String      billBusinessType       ;//票据业务类型              
	private String      customerId             ;//承兑人核心客户号          
	private String      bankId                 ;//承兑人二代支付系统行号    
	private String      customerName           ;//承兑人名称                
	private BigDecimal  execNominalAmount      ;//占用名义金额              
	private BigDecimal  execExposureAmount     ;//占用敞口金额              
	private String      retCode                ;//返回码  
	private String      retMessage             ;//返回信息
	private String      isStrongControl        ;//是否强控                
	private String      checkCode              ;//检查项编码              
	private String      checkMessage           ;//检查项不通过检查结果信息               
	private String      creditNo               ;//额度系统授信编号          
	private String      sourceSystem           ;//来源系统
	private List<Map>   reqList                ;//上传/下載文件数据列表
	private String      queryType              ;//查询类型
	private String      onlineNo               ;//在线协议编号
	private String      oNlineCreditNo         ;//基本授信编号
	private String      operationType          ;//操作类型
	private String      onlineType             ;//业务类型
	private String      contractNo             ;//合同编号
	private String      effectiveDate          ;//生效日期
	private String      expireDate             ;//到期日
	private BigDecimal    businessSum        ;//合同金额
	private BigDecimal    expendFailAmt        ;//出账失败金额
	private BigDecimal    unUsedReturnAmt        ;//未用退回金额
	private BigDecimal    oriContractAmt        ;//原合同金额
	
    private String poolMode; //票据池模式
    private String bpsNo;//票据池编号
    private String memberId;//会员代码
	private String guarantDiscNo;//保贴人编号
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	/*** 融合改造新增字段  end*/
	
	
	public String getBillMedia() {
		return billMedia;
	}
	public BigDecimal getExpendFailAmt() {
		return expendFailAmt;
	}
	public void setExpendFailAmt(BigDecimal expendFailAmt) {
		this.expendFailAmt = expendFailAmt;
	}
	public BigDecimal getUnUsedReturnAmt() {
		return unUsedReturnAmt;
	}
	public void setUnUsedReturnAmt(BigDecimal unUsedReturnAmt) {
		this.unUsedReturnAmt = unUsedReturnAmt;
	}
	public BigDecimal getOriContractAmt() {
		return oriContractAmt;
	}
	public void setOriContractAmt(BigDecimal oriContractAmt) {
		this.oriContractAmt = oriContractAmt;
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
	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}
	public String getGuarantDiscNo() {
		return guarantDiscNo;
	}
	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getPoolMode() {
		return poolMode;
	}
	public void setPoolMode(String poolMode) {
		this.poolMode = poolMode;
	}
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getCLIENT_NAME() {
		return CLIENT_NAME;
	}
	public void setCLIENT_NAME(String cLIENT_NAME) {
		CLIENT_NAME = cLIENT_NAME;
	}
	public String getCREDIT_CODE() {
		return CREDIT_CODE;
	}
	public void setCREDIT_CODE(String cREDIT_CODE) {
		CREDIT_CODE = cREDIT_CODE;
	}
	public String getCORE_CLIENT_NO() {
		return CORE_CLIENT_NO;
	}
	public void setCORE_CLIENT_NO(String cORE_CLIENT_NO) {
		CORE_CLIENT_NO = cORE_CLIENT_NO;
	}
	public String getLIMIT_TYPE() {
		return LIMIT_TYPE;
	}
	public void setLIMIT_TYPE(String lIMIT_TYPE) {
		LIMIT_TYPE = lIMIT_TYPE;
	}
	public String getGENERATE_MODE() {
		return GENERATE_MODE;
	}
	public void setGENERATE_MODE(String gENERATE_MODE) {
		GENERATE_MODE = gENERATE_MODE;
	}
	public String getCREDIT_LIMIT_AMT_CCY() {
		return CREDIT_LIMIT_AMT_CCY;
	}
	public void setCREDIT_LIMIT_AMT_CCY(String cREDIT_LIMIT_AMT_CCY) {
		CREDIT_LIMIT_AMT_CCY = cREDIT_LIMIT_AMT_CCY;
	}
	public BigDecimal getNOMINAL_AMT() {
		return NOMINAL_AMT;
	}
	public void setNOMINAL_AMT(BigDecimal nOMINAL_AMT) {
		NOMINAL_AMT = nOMINAL_AMT;
	}
	public BigDecimal getEXEC_EXPOSURE_AMT() {
		return EXEC_EXPOSURE_AMT;
	}
	public void setEXEC_EXPOSURE_AMT(BigDecimal eXEC_EXPOSURE_AMT) {
		EXEC_EXPOSURE_AMT = eXEC_EXPOSURE_AMT;
	}
	public BigDecimal getAVAIL_NOMINAL_AMT() {
		return AVAIL_NOMINAL_AMT;
	}
	public void setAVAIL_NOMINAL_AMT(BigDecimal aVAIL_NOMINAL_AMT) {
		AVAIL_NOMINAL_AMT = aVAIL_NOMINAL_AMT;
	}
	public BigDecimal getAVAIL_EXEC_EXPOSURE_AMT() {
		return AVAIL_EXEC_EXPOSURE_AMT;
	}
	public void setAVAIL_EXEC_EXPOSURE_AMT(BigDecimal aVAIL_EXEC_EXPOSURE_AMT) {
		AVAIL_EXEC_EXPOSURE_AMT = aVAIL_EXEC_EXPOSURE_AMT;
	}
	public BigDecimal getRT_CREDIT_RATE() {
		return RT_CREDIT_RATE;
	}
	public void setRT_CREDIT_RATE(BigDecimal rT_CREDIT_RATE) {
		RT_CREDIT_RATE = rT_CREDIT_RATE;
	}
	public BigDecimal getHIGH_NOMINAL_AMT() {
		return HIGH_NOMINAL_AMT;
	}
	public void setHIGH_NOMINAL_AMT(BigDecimal hIGH_NOMINAL_AMT) {
		HIGH_NOMINAL_AMT = hIGH_NOMINAL_AMT;
	}
	public BigDecimal getHIGH_EXEC_EXPOSURE_AMT() {
		return HIGH_EXEC_EXPOSURE_AMT;
	}
	public void setHIGH_EXEC_EXPOSURE_AMT(BigDecimal hIGH_EXEC_EXPOSURE_AMT) {
		HIGH_EXEC_EXPOSURE_AMT = hIGH_EXEC_EXPOSURE_AMT;
	}
	public String getLIMIT_STATUS() {
		return LIMIT_STATUS;
	}
	public void setLIMIT_STATUS(String lIMIT_STATUS) {
		LIMIT_STATUS = lIMIT_STATUS;
	}
	public String getLIMIT_FROZEN_STATUS() {
		return LIMIT_FROZEN_STATUS;
	}
	public void setLIMIT_FROZEN_STATUS(String lIMIT_FROZEN_STATUS) {
		LIMIT_FROZEN_STATUS = lIMIT_FROZEN_STATUS;
	}
	public String getCYCLE_FLAG() {
		return CYCLE_FLAG;
	}
	public void setCYCLE_FLAG(String cYCLE_FLAG) {
		CYCLE_FLAG = cYCLE_FLAG;
	}
	public String getTERM() {
		return TERM;
	}
	public void setTERM(String tERM) {
		TERM = tERM;
	}
	public String getEFFECTIVE_DATE() {
		return EFFECTIVE_DATE;
	}
	public void setEFFECTIVE_DATE(String eFFECTIVE_DATE) {
		EFFECTIVE_DATE = eFFECTIVE_DATE;
	}
	public String getEXPIRY_DATE() {
		return EXPIRY_DATE;
	}
	public void setEXPIRY_DATE(String eXPIRY_DATE) {
		EXPIRY_DATE = eXPIRY_DATE;
	}
	public String getACCT_MANAGER_TELLER_NO() {
		return ACCT_MANAGER_TELLER_NO;
	}
	public void setACCT_MANAGER_TELLER_NO(String aCCT_MANAGER_TELLER_NO) {
		ACCT_MANAGER_TELLER_NO = aCCT_MANAGER_TELLER_NO;
	}
	public String getACCT_MANAGER_BRANCH_ID() {
		return ACCT_MANAGER_BRANCH_ID;
	}
	public void setACCT_MANAGER_BRANCH_ID(String aCCT_MANAGER_BRANCH_ID) {
		ACCT_MANAGER_BRANCH_ID = aCCT_MANAGER_BRANCH_ID;
	}
	public String getRULE_CODE() {
		return RULE_CODE;
	}
	public void setRULE_CODE(String rULE_CODE) {
		RULE_CODE = rULE_CODE;
	}
	public String getBUSS_CATEGORY_CODE() {
		return BUSS_CATEGORY_CODE;
	}
	public void setBUSS_CATEGORY_CODE(String bUSS_CATEGORY_CODE) {
		BUSS_CATEGORY_CODE = bUSS_CATEGORY_CODE;
	}
	public String getCCY() {
		return CCY;
	}
	public void setCCY(String cCY) {
		CCY = cCY;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public BigDecimal getBillsum() {
		return billsum;
	}
	public void setBillsum(BigDecimal billsum) {
		this.billsum = billsum;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getBillBusinessType() {
		return billBusinessType;
	}
	public void setBillBusinessType(String billBusinessType) {
		this.billBusinessType = billBusinessType;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public BigDecimal getExecNominalAmount() {
		return execNominalAmount;
	}
	public void setExecNominalAmount(BigDecimal execNominalAmount) {
		this.execNominalAmount = execNominalAmount;
	}
	public BigDecimal getExecExposureAmount() {
		return execExposureAmount;
	}
	public void setExecExposureAmount(BigDecimal execExposureAmount) {
		this.execExposureAmount = execExposureAmount;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMessage() {
		return retMessage;
	}
	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}
	public String getIsStrongControl() {
		return isStrongControl;
	}
	public void setIsStrongControl(String isStrongControl) {
		this.isStrongControl = isStrongControl;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public String getCheckMessage() {
		return checkMessage;
	}
	public void setCheckMessage(String checkMessage) {
		this.checkMessage = checkMessage;
	}
	public String getCreditNo() {
		return creditNo;
	}
	public void setCreditNo(String creditNo) {
		this.creditNo = creditNo;
	}
	public String getSourceSystem() {
		return sourceSystem;
	}
	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}
	public List<Map> getReqList() {
		return reqList;
	}
	public void setReqList(List<Map> reqList) {
		this.reqList = reqList;
	}
	public String getOnlineNo() {
		return onlineNo;
	}
	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}
	public String getoNlineCreditNo() {
		return oNlineCreditNo;
	}
	public void setoNlineCreditNo(String oNlineCreditNo) {
		this.oNlineCreditNo = oNlineCreditNo;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public String getOnlineType() {
		return onlineType;
	}
	public void setOnlineType(String onlineType) {
		this.onlineType = onlineType;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public BigDecimal getBusinessSum() {
		return businessSum;
	}
	public void setBusinessSum(BigDecimal businessSum) {
		this.businessSum = businessSum;
	}
	
	


}
