package com.mingtech.application.pool.bank.coresys.domain;


import java.math.BigDecimal;
import java.util.List;

/**
 * 票据池交易  实体
 * @author yixiaolong
 */
public class PoolTransNotes{
	public static final String IS_OPEN_CORE = "1";   //1 是使用票据池核心接口，0 为不使用
	
	
	/** 记账通知、撤回 交易类型 */
	public static final String TRANS_ACCT_TYPE_1 = "1";   //登记
	public static final String TRANS_ACCT_TYPE_2= "2";    //撤回
	
	/** 票据混合属性值 */
	public static final String P_BANK_DRAFT = "1";         //实物银承
	public static final String P_COMMERCIAL_DRAFT = "2";   //实物商承
	public static final String E_BANK_DRAFT = "3";         //电子银承
	public static final String E_COMMERCIAL_DRAFT = "4";   //电子商承
	
	/** 业务类型 */
	public static final String TRADE_TYPE_1 = "1";       //票据池业务
	public static final String TRADE_TYPE_2 = "2";       //代保管业务
	
	/** 登记类型 */
	public static final String ACCOUNT_TYPE_1 = "1";      //入池登记
	public static final String ACCOUNT_TYPE_2 = "2";      //出池登记
	public static final String ACCOUNT_TYPE_3 = "3";      //存票登记 
	public static final String ACCOUNT_TYPE_4 = "4";      //取票登记
	
	/** 记账结果查询类型 */
	public static final String QUERY_TYPE_1 = "1";       //入池
	public static final String QUERY_TYPE_2 = "2";       //出池
	public static final String QUERY_TYPE_3 = "3";       //存票
	public static final String QUERY_TYPE_4 = "4";       //取票
	
	/** 入账科目 */
	public static final String TOACCT_TYPE_1 = "70304";       //代保管
	public static final String TOACCT_TYPE_2 = "70402";       //抵质押
	
	/** 业务类型 */
	public static final String COLL_TYPE_1 = "0";   //不是托收
	public static final String COLL_TYPE_2 = "1";   //是托收
	
	//----------记账通知、撤回-----Start-----------------------
	private String transType;     //交易类型(‘1’登记,‘2’撤回)
	private String batchNo;       //批次号
	private String origBatchNo;   //原批次号
	private String tradeType;     //业务类型('1' 票据池业务  '2' 代保管业务)
	private String toAcctType;    //代保管（703） 抵质押（704）
	private String acctType;      //登记类型('1'-入池登记 '2'-出池登记 '3'-存票登记 '4'-取票登记)
	private String acctOrgCode;   //记账网点号
	private List billDetail;      //报文明细(直接放票据实体即可)
	private String collType;      //业务类型(‘0’不是托收 ‘1’是托收)只有票据托收、汇款传‘1’，其余交易传0
	//----------记账通知、撤回-----End-------------------------
	
	//------------保证金交易--------Start-------------------
	private String bailAcctNo;         //保证金账号
	private String clsAccountNo;       //结算账号
	private String orgCode;            //企业组织机构代码
	private BigDecimal acctAmount;     //记账金额
	
	private String huoAcctNo;            //活期账号
	private String fixAcctNo;            //定期账号
	private BigDecimal amount;           //划转金额
	private String rateTy;               //计息种类
	private String rateMe;               //计息方式
	private String flowNumber;           //流水号
	
	private String sonAcctNo ;
	private String acctName;
	private BigDecimal rate;
	private int batchNum;

	//------------保证金交易--------End---------------------
	
	//------------记账结果查询----------Start--------------
	private String queryType;        //查询类型   (需要的批次号在记账处)
	//------------记账结果查询----------End--------------
	
	//------------保证金账户余额查询------Start----------
	private String mainFlowNO;              //主机流水号
	private String mainTradType="1001";     //填写固定值 1001
	//交易码用 transType 填写固定值 4904
	private String recordDate;           //登记日期
	private String recordTime;           //登记时间
	private String sendPort;             //发送网点
	private String agentPort;            //代理网点
	private String operater;             //操作员
	private String cardNo;               //卡号账号
	//------------保证金账户余额查询------Start----------
	
	private String billMedia;      //票据介质
	private String billType;       //票据类型
	
	//------------2013-01-23 add by zhaoliang--------------
	private BigDecimal stLv;//基准利率 S11406 7S 5 
	private BigDecimal stLv2;//  基准利率2 S11407 7S 5 
	private String changeMe;//浮动方式 PP066 1A 
	private String lvStMe;//利率依据方式 PP067 2A 
	
	private BigDecimal changeValue=new BigDecimal(0);//浮动值 
	private BigDecimal changeBL=new BigDecimal(0);//浮动比例
	//------------2013-01-23 add by zhaoliang--------------
	
	
	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getOrigBatchNo() {
		return origBatchNo;
	}

	public void setOrigBatchNo(String origBatchNo) {
		this.origBatchNo = origBatchNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public List getBillDetail() {
		return billDetail;
	}

	public void setBillDetail(List billDetail) {
		this.billDetail = billDetail;
	}

	public String getBailAcctNo() {
		return bailAcctNo;
	}

	public void setBailAcctNo(String bailAcctNo) {
		this.bailAcctNo = bailAcctNo;
	}

	public String getClsAccountNo() {
		return clsAccountNo;
	}

	public void setClsAccountNo(String clsAccountNo) {
		this.clsAccountNo = clsAccountNo;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public BigDecimal getAcctAmount() {
		return acctAmount;
	}

	public void setAcctAmount(BigDecimal acctAmount) {
		this.acctAmount = acctAmount;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	public PoolTransNotes(){}

	public String getAcctOrgCode() {
		return acctOrgCode;
	}

	public void setAcctOrgCode(String acctOrgCode) {
		this.acctOrgCode = acctOrgCode;
	}

	public String getToAcctType() {
		return toAcctType;
	}

	public void setToAcctType(String toAcctType) {
		this.toAcctType = toAcctType;
	};

	public String getBillMedia() {
		return billMedia;
	}

	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}
	
	public String getFixAcctNo() {
		return fixAcctNo;
	}

	public void setFixAcctNo(String fixAcctNo) {
		this.fixAcctNo = fixAcctNo;
	}

	public String getSonAcctNo() {
		return sonAcctNo;
	}

	public void setSonAcctNo(String sonAcctNo) {
		this.sonAcctNo = sonAcctNo;
	}


	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}



	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getCollType() {
		return collType;
	}

	public void setCollType(String collType) {
		this.collType = collType;
	}

	public String getMainFlowNO() {
		return mainFlowNO;
	}

	public void setMainFlowNO(String mainFlowNO) {
		this.mainFlowNO = mainFlowNO;
	}

	public String getMainTradType() {
		return mainTradType;
	}

	public void setMainTradType(String mainTradType) {
		this.mainTradType = mainTradType;
	}

	public String getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getSendPort() {
		return sendPort;
	}

	public void setSendPort(String sendPort) {
		this.sendPort = sendPort;
	}

	public String getAgentPort() {
		return agentPort;
	}

	public void setAgentPort(String agentPort) {
		this.agentPort = agentPort;
	}

	public String getOperater() {
		return operater;
	}

	public void setOperater(String operater) {
		this.operater = operater;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getHuoAcctNo() {
		return huoAcctNo;
	}

	public void setHuoAcctNo(String huoAcctNo) {
		this.huoAcctNo = huoAcctNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRateTy() {
		return rateTy;
	}

	public void setRateTy(String rateTy) {
		this.rateTy = rateTy;
	}

	public String getRateMe() {
		return rateMe;
	}

	public void setRateMe(String rateMe) {
		this.rateMe = rateMe;
	}

	public int getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(int batchNum) {
		this.batchNum = batchNum;
	}

	public String getFlowNumber() {
		return flowNumber;
	}

	public void setFlowNumber(String flowNumber) {
		this.flowNumber = flowNumber;
	}

	public BigDecimal getStLv() {
		return stLv;
	}

	public void setStLv(BigDecimal stLv) {
		this.stLv = stLv;
	}

	public BigDecimal getStLv2() {
		return stLv2;
	}

	public void setStLv2(BigDecimal stLv2) {
		this.stLv2 = stLv2;
	}

	public String getChangeMe() {
		return changeMe;
	}

	public void setChangeMe(String changeMe) {
		this.changeMe = changeMe;
	}

	public String getLvStMe() {
		return lvStMe;
	}

	public void setLvStMe(String lvStMe) {
		this.lvStMe = lvStMe;
	}

	public BigDecimal getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(BigDecimal changeValue) {
		this.changeValue = changeValue;
	}

	public BigDecimal getChangeBL() {
		return changeBL;
	}

	public void setChangeBL(BigDecimal changeBL) {
		this.changeBL = changeBL;
	}

 

	 
}
