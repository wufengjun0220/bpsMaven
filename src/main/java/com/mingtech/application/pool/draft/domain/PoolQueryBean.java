package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: zhangdapeng
 * @日期: Aug 13, 2009 1:09:59 PM
 * @描述: [QueryBean]用来封装组合查询的条件
 */
public class PoolQueryBean {
    private String id;
    private String ids;
    private ArrayList<String> status;
    private String billNo;// 票号
    private String idNb;// 报文中票号
    private String startDate;// 起始日期
    private String endDate;// 结束日期
    private String billType;// 票据类型

    private String isInit;// 初始化1、0

    private String plApplyNm;//申请人名称

    private String sAcceptorBankCode;// 承兑行行号
    private String sBillType;// 票据类型
    private String rRcvgprsnofrcrscmonid;// 被追索人组织机构代码
    private String rRcrsrcmonid;// 追索人组织机构代码
    /* 承兑 */
    private String SApplyerOrgCode;// 出票人组织机构代码
    private String SStatusFlag;// 票据状态
    private String SCollztnpropsrOrgCode; // 出质人组织机构代码
    private String SCollztnbkBankCode; // 质权人开户行行号

    private String queryParam;// 高级查询参数    查询类型

    private String SIntPayway;// 付息方式
    private String SBatchNo;// 批次号
    private String SCustOrgCode;// 客户组织机构
    private String SCustName;// 客户名称

    private String discOutOrgCode;// 贴出人组织机构代码
    private String discountOutName;// 贴出方名称
    private String discountInName;// 贴入方名称

    public String innerBankCode;// 内部网点号

    private Date startDDueDt;// 到期日起始日
    private Date endDDueDt;// 到期日截止日
    private Date discDateStart;// 贴现日开始
    private Date discDateEnd;// 贴现日结束
    private String invoiceNo;// 发票号

    private BigDecimal FBillAmount; // 票面金额
    private BigDecimal FBillAmountUp; // 票面金额上限
    private String SBillMedia;// 票据介质

    /* 电子合同 */
    private String ctrctNb;// 合同编号

    private String acceptorBankCode;// 承兑开户行行号
    private String acceptorNm; // 承兑人名称
    private String accptrId;   //承兑人账号
    
    /**
     * pjc接收参数
     *
     * @return
     */

    private Date pstartDate;      //票据池接收网银参数使用
    private Date pendDate;        //票据池接收网银参数使用
    private List custAccts;//企业帐号列表，每条记录为一个账号，明细中使用
    private String custAccount;//企业帐号,head头中使用
    private BigDecimal isseAmtStart;    // 票据金额开始
    private BigDecimal isseAmtEnd;    //票据金额结束
    private String creditNo;        //信贷产品号
    private String customernumber;  //客户核心号 (新核心)
    private String marginAccount;  //保证金账号
    private String dieLine;        //定期活期
    private String businessId;        //业务主键id
    private String billOutName;        //出票人名称
    private String payBankName;      // 付款人开户行行名
    private String receMoneName;      // 收款人名称
    private String receMoneAccount;      // 收款人账号
    private String BankMoneNo;      //收款人开户行行号
    private String BankMoneName;      //收款人开户行名称
    private String acceptorBankname;      // 承兑人行名
    private BigDecimal charge;              // 手续费
    private String chargeAccount;              // 手续费扣款账号
    private String recePayType;                //收付类型
    private String protocolNo;               //协议编号
    private String assetType;             //资产类型
    private String cirStage;      //流转阶段
    
    
    
    
    
    
    
    

    private BigDecimal moveMoney;          //划转金额
    private String moveType;          //划转类型
    private String bdPeriod;          //定期期限
    private String moveName;          //转入户名
    private String moveAccount;          //转入账号
    private String isnotFlag;          //能否转让标记
    private String billOutAccount;        //出票人账号
    private String billOutBankNo;        //出票人开户行行号
    private String billOutBankName;        //出票人开户行名称

    private String cardType;               //证件类型
    private String cardNumber;             //证件号

    private Date startPoolIn;            //入池时间开始
    private Date endPoolIn;             // 入池时间结束

    private Date startContract;         //借据起始日
    private Date endContract;           //借据到期日
    private String receiptNum;          //借据号
    private BigDecimal receiptMax;      //借据金额最大值
    private BigDecimal receiptMin;      //借据金额最小值

    //PJC020网银接口写文件时使用
    private BigDecimal oneBig;         //票据额度（1个月内）
    private BigDecimal twoBig;         //票据额度（1-2）
    private BigDecimal threeBig;       //票据额度（2-3）
    private BigDecimal fourBig;        //票据额度（3-6）
    private BigDecimal sixBig;         //票据额度（6个月外）
    private BigDecimal sevenBig;       //纸质票据额度
    private BigDecimal eightBig;       //电子票据额度
    private BigDecimal nineBig;        //保证金额度

    private Long poolCount;            //质押份数
    private Long storageCount;         //托管份数

    private String ebankPeopleCard;        //网银人员身份证号
    private String ebankName;              //网银人员姓名
    private String ebankType;              //网银人员证件类型

    private String paymentAccount;   //付款账号
    private String usage;//用途
    private String remark;//备注

    //PJC027网银接口写文件时使用
    private String sumType;         //汇总类型
    private String sumValue;         //汇总值
    
    /**pjc32添加接受报文字段*/
    
    private String isEdu;//是否产生额度
    private String rickLevel;//风险等级
    private String poolEquities;//票据权益
    private String isTrustee;//是否托管
    private String location;//票据持有地
    private String poolProperty;//票据属性
    private String sources;//数据来源
    
    private Date issDate;            //出票日
    private Date dueDate;             // 到期日
    
    private String contractNo;//交易合同号
    private String acceptanceAgreeNo;//承兑协议编号 
    private Double FPED;//分配额度
    
    
    private Date AccDate;   //账务日期
    private String lockString;//锁
	private String ebkLock;//01：上锁   02：未上锁   网银系统经办锁：网银系统经办提交后避免已经经办的票被再次查到的锁，该锁只对网银系统有效。

    private String isPoolOutEnd;//是否出池结束   1批次未出池  0批次已出池

    private String batchid;//机构

    private String user;//柜员
    
    private String taskId;//调度任务ID
    private String treeCode;//树节点
    private String queueName;//队列名称
    private String queueNode;//队列分发节点
    private String productId;//业务类型ID
    private String memberCode;//会员编码
    private String delFlag;//删除标记
    private String taskNo;//任务编号
    private String busiId;//原业务ID
    
    private String zyFlag;// 是否签自动质押协议（00：否 ，01：是）
    
    private List<String> acctNoList;//电票签约账号list
    
    
    
	private String outBatchNo;//PJC037、PJC044 接口的特殊字段
	private String workerName ;//经办人姓名          PJC044 接口的特殊字段
	private String workerPhone;//经办人手机号       PJC044 接口的特殊字段
	private String workerCard ;//经办人身份证号   PJC044 接口的特殊字段
    
	private String creditObjType;//额度主体类型  1-同业额度  2-对公额度
	private String auditStatus;//审批状态
    private String blackFlag;//黑名单
	private String BtFlag;//保贴额度占用标识
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	
	private String plDrwrAcctName;// 出票人账号名称
	private String plPyeeAcctName;// 收款人账号名称
	private String plAccptrAcctName;// 承兑人账号名称
	
	/*** 融合改造新增字段  end*/
	
    public String getBtFlag() {
		return BtFlag;
	}

	public String getPlDrwrAcctName() {
		return plDrwrAcctName;
	}

	public void setPlDrwrAcctName(String plDrwrAcctName) {
		this.plDrwrAcctName = plDrwrAcctName;
	}

	public String getPlPyeeAcctName() {
		return plPyeeAcctName;
	}

	public void setPlPyeeAcctName(String plPyeeAcctName) {
		this.plPyeeAcctName = plPyeeAcctName;
	}

	public String getPlAccptrAcctName() {
		return plAccptrAcctName;
	}

	public void setPlAccptrAcctName(String plAccptrAcctName) {
		this.plAccptrAcctName = plAccptrAcctName;
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

	public void setBtFlag(String btFlag) {
		BtFlag = btFlag;
	}

	public String getBlackFlag() {
		return blackFlag;
	}

	public void setBlackFlag(String blackFlag) {
		this.blackFlag = blackFlag;
	}

	public String getEbkLock() {
		return ebkLock;
	}

	public void setEbkLock(String ebkLock) {
		this.ebkLock = ebkLock;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getCreditObjType() {
		return creditObjType;
	}

	public void setCreditObjType(String creditObjType) {
		this.creditObjType = creditObjType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOutBatchNo() {
		return outBatchNo;
	}

	public void setOutBatchNo(String outBatchNo) {
		this.outBatchNo = outBatchNo;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getWorkerPhone() {
		return workerPhone;
	}

	public void setWorkerPhone(String workerPhone) {
		this.workerPhone = workerPhone;
	}

	public String getWorkerCard() {
		return workerCard;
	}

	public void setWorkerCard(String workerCard) {
		this.workerCard = workerCard;
	}

	public List<String> getAcctNoList() {
		return acctNoList;
	}

	public void setAcctNoList(List<String> acctNoList) {
		this.acctNoList = acctNoList;
	}

	public String getZyFlag() {
		return zyFlag;
	}

	public void setZyFlag(String zyFlag) {
		this.zyFlag = zyFlag;
	}

	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	public String getQueueNode() {
		return queueNode;
	}

	public void setQueueNode(String queueNode) {
		this.queueNode = queueNode;
	}

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getBatchid() {
        return batchid;
    }

    public void setBatchid(String batchid) {
        this.batchid = batchid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIsPoolOutEnd() {
        return isPoolOutEnd;
    }

    public void setIsPoolOutEnd(String isPoolOutEnd) {
        this.isPoolOutEnd = isPoolOutEnd;
    }

    public String getLockString() {
		return lockString;
	}

	public void setLockString(String lockString) {
		this.lockString = lockString;
	}

	public Date getAccDate() {
		return AccDate;
	}

	public void setAccDate(Date accDate) {
		AccDate = accDate;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getAcceptanceAgreeNo() {
		return acceptanceAgreeNo;
	}

	public void setAcceptanceAgreeNo(String acceptanceAgreeNo) {
		this.acceptanceAgreeNo = acceptanceAgreeNo;
	}

	public Date getIssDate() {
		return issDate;
	}

	public void setIssDate(Date issDate) {
		this.issDate = issDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getIsEdu() {
        return isEdu;
    }

    public void setIsEdu(String isEdu) {
        this.isEdu = isEdu;
    }

    public String getRickLevel() {
        return rickLevel;
    }

    public void setRickLevel(String rickLevel) {
        this.rickLevel = rickLevel;
    }

    public String getPoolEquities() {
        return poolEquities;
    }

    public void setPoolEquities(String poolEquities) {
        this.poolEquities = poolEquities;
    }

    public String getIsTrustee() {
        return isTrustee;
    }

    public void setIsTrustee(String isTrustee) {
        this.isTrustee = isTrustee;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPoolProperty() {
        return poolProperty;
    }

    public void setPoolProperty(String poolProperty) {
        this.poolProperty = poolProperty;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public String getEbankType() {
        return ebankType;
    }

    public void setEbankType(String ebankType) {
        this.ebankType = ebankType;
    }

    public String getEbankPeopleCard() {
        return ebankPeopleCard;
    }

    public void setEbankPeopleCard(String ebankPeopleCard) {
        this.ebankPeopleCard = ebankPeopleCard;
    }

    public String getEbankName() {
        return ebankName;
    }

    public void setEbankName(String ebankName) {
        this.ebankName = ebankName;
    }

    public Long getPoolCount() {
        return poolCount;
    }

    public void setPoolCount(Long poolCount) {
        this.poolCount = poolCount;
    }

    public Long getStorageCount() {
        return storageCount;
    }

    public void setStorageCount(Long storageCount) {
        this.storageCount = storageCount;
    }

    public Date getPstartDate() {
        return pstartDate;
    }

    public void setPstartDate(Date pstartDate) {
        this.pstartDate = pstartDate;
    }

    public Date getPendDate() {
        return pendDate;
    }

    public void setPendDate(Date pendDate) {
        this.pendDate = pendDate;
    }

    public BigDecimal getSevenBig() {
        return sevenBig;
    }

    public void setSevenBig(BigDecimal sevenBig) {
        this.sevenBig = sevenBig;
    }

    public BigDecimal getEightBig() {
        return eightBig;
    }

    public void setEightBig(BigDecimal eightBig) {
        this.eightBig = eightBig;
    }

    public BigDecimal getNineBig() {
        return nineBig;
    }

    public void setNineBig(BigDecimal nineBig) {
        this.nineBig = nineBig;
    }

    public BigDecimal getOneBig() {
        return oneBig;
    }

    public void setOneBig(BigDecimal oneBig) {
        this.oneBig = oneBig;
    }

    public BigDecimal getTwoBig() {
        return twoBig;
    }

    public void setTwoBig(BigDecimal twoBig) {
        this.twoBig = twoBig;
    }

    public BigDecimal getThreeBig() {
        return threeBig;
    }

    public void setThreeBig(BigDecimal threeBig) {
        this.threeBig = threeBig;
    }

    public BigDecimal getFourBig() {
        return fourBig;
    }

    public void setFourBig(BigDecimal fourBig) {
        this.fourBig = fourBig;
    }

    public BigDecimal getSixBig() {
        return sixBig;
    }

    public void setSixBig(BigDecimal sixBig) {
        this.sixBig = sixBig;
    }

    public Date getStartPoolIn() {
        return startPoolIn;
    }

    public void setStartPoolIn(Date startPoolIn) {
        this.startPoolIn = startPoolIn;
    }

    public Date getEndPoolIn() {
        return endPoolIn;
    }

    public void setEndPoolIn(Date endPoolIn) {
        this.endPoolIn = endPoolIn;
    }

    public ArrayList<String> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBankMoneNo() {
        return BankMoneNo;
    }

    public void setBankMoneNo(String bankMoneNo) {
        BankMoneNo = bankMoneNo;
    }

    public String getBankMoneName() {
        return BankMoneName;
    }

    public void setBankMoneName(String bankMoneName) {
        BankMoneName = bankMoneName;
    }

    public String getReceMoneAccount() {
        return receMoneAccount;
    }

    public void setReceMoneAccount(String receMoneAccount) {
        this.receMoneAccount = receMoneAccount;
    }

    public String getBillOutBankNo() {
        return billOutBankNo;
    }

    public void setBillOutBankNo(String billOutBankNo) {
        this.billOutBankNo = billOutBankNo;
    }

    public String getBillOutBankName() {
        return billOutBankName;
    }

    public void setBillOutBankName(String billOutBankName) {
        this.billOutBankName = billOutBankName;
    }

    public String getBillOutAccount() {
        return billOutAccount;
    }

    public void setBillOutAccount(String billOutAccount) {
        this.billOutAccount = billOutAccount;
    }

    public String getIsnotFlag() {
        return isnotFlag;
    }

    public void setIsnotFlag(String isnotFlag) {
        this.isnotFlag = isnotFlag;
    }

    public BigDecimal getMoveMoney() {
        return moveMoney;
    }

    public void setMoveMoney(BigDecimal moveMoney) {
        this.moveMoney = moveMoney;
    }

    public String getMoveType() {
        return moveType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public String getBdPeriod() {
        return bdPeriod;
    }

    public void setBdPeriod(String bdPeriod) {
        this.bdPeriod = bdPeriod;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public String getMoveAccount() {
        return moveAccount;
    }

    public void setMoveAccount(String moveAccount) {
        this.moveAccount = moveAccount;
    }

    public String getChargeAccount() {
        return chargeAccount;
    }

    public void setChargeAccount(String chargeAccount) {
        this.chargeAccount = chargeAccount;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public String getAcceptorBankname() {
        return acceptorBankname;
    }

    public void setAcceptorBankname(String acceptorBankname) {
        this.acceptorBankname = acceptorBankname;
    }

    public String getReceMoneName() {
        return receMoneName;
    }

    public void setReceMoneName(String receMoneName) {
        this.receMoneName = receMoneName;
    }

    public String getPayBankName() {
        return payBankName;
    }

    public void setPayBankName(String payBankName) {
        this.payBankName = payBankName;
    }

    public String getBillOutName() {
        return billOutName;
    }

    public void setBillOutName(String billOutName) {
        this.billOutName = billOutName;
    }

    public String getCustAccount() {
        return custAccount;
    }

    public void setCustAccount(String custAccount) {
        this.custAccount = custAccount;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getDieLine() {
        return dieLine;
    }

    public void setDieLine(String dieLine) {
        this.dieLine = dieLine;
    }

    public String getMarginAccount() {
        return marginAccount;
    }

    public void setMarginAccount(String marginAccount) {
        this.marginAccount = marginAccount;
    }

    public String getAccptrId() {
        return accptrId;
    }

    public void setAccptrId(String accptrId) {
        this.accptrId = accptrId;
    }

    public List getCustAccts() {
        return custAccts;
    }

    public void setCustAccts(List custAccts) {
        this.custAccts = custAccts;
    }

    public BigDecimal getIsseAmtStart() {
        return isseAmtStart;
    }

    public void setIsseAmtStart(BigDecimal isseAmtStart) {
        this.isseAmtStart = isseAmtStart;
    }

    public BigDecimal getIsseAmtEnd() {
        return isseAmtEnd;
    }

    public void setIsseAmtEnd(BigDecimal isseAmtEnd) {
        this.isseAmtEnd = isseAmtEnd;
    }

    public String getCreditNo() {
        return creditNo;
    }

    public void setCreditNo(String creditNo) {
        this.creditNo = creditNo;
    }

    public String getCustomernumber() {
        return customernumber;
    }

    public void setCustomernumber(String customernumber) {
        this.customernumber = customernumber;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getSApplyerOrgCode() {
        return SApplyerOrgCode;
    }

    public void setSApplyerOrgCode(String applyerOrgCode) {
        SApplyerOrgCode = applyerOrgCode;
    }

    public String getSStatusFlag() {
        return SStatusFlag;
    }

    public void setSStatusFlag(String statusFlag) {
        SStatusFlag = statusFlag;
    }

    public String getDiscOutOrgCode() {
        return discOutOrgCode;
    }

    public void setDiscOutOrgCode(String discOutOrgCode) {
        this.discOutOrgCode = discOutOrgCode;
    }

    public String getSAcceptorBankCode() {
        return sAcceptorBankCode;
    }

    public void setSAcceptorBankCode(String acceptorBankCode) {
        sAcceptorBankCode = acceptorBankCode;
    }

    public String getSBillType() {
        return sBillType;
    }

    public void setSBillType(String billType) {
        sBillType = billType;
    }

    public String getRRcvgprsnofrcrscmonid() {
        return rRcvgprsnofrcrscmonid;
    }

    public void setRRcvgprsnofrcrscmonid(String rcvgprsnofrcrscmonid) {
        rRcvgprsnofrcrscmonid = rcvgprsnofrcrscmonid;
    }

    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    public String getRRcrsrcmonid() {
        return rRcrsrcmonid;
    }

    public void setRRcrsrcmonid(String rcrsrcmonid) {
        rRcrsrcmonid = rcrsrcmonid;
    }

    public String getSCollztnpropsrOrgCode() {
        return SCollztnpropsrOrgCode;
    }

    public void setSCollztnpropsrOrgCode(String collztnpropsrOrgCode) {
        SCollztnpropsrOrgCode = collztnpropsrOrgCode;
    }

    public String getSCollztnbkBankCode() {
        return SCollztnbkBankCode;
    }

    public void setSCollztnbkBankCode(String collztnbkBankCode) {
        SCollztnbkBankCode = collztnbkBankCode;
    }

    public BigDecimal getFBillAmount() {
        return FBillAmount;
    }

    public void setFBillAmount(BigDecimal billAmount) {
        FBillAmount = billAmount;
    }

    public String getSBillMedia() {
        return SBillMedia;
    }

    public void setSBillMedia(String billMedia) {
        SBillMedia = billMedia;
    }

    public String getSIntPayway() {
        return SIntPayway;
    }

    public void setSIntPayway(String intPayway) {
        SIntPayway = intPayway;
    }

    public String getSBatchNo() {
        return SBatchNo;
    }

    public void setSBatchNo(String batchNo) {
        SBatchNo = batchNo;
    }

    public String getSCustOrgCode() {
        return SCustOrgCode;
    }

    public void setSCustOrgCode(String custOrgCode) {
        SCustOrgCode = custOrgCode;
    }

    public String getSCustName() {
        return SCustName;
    }

    public void setSCustName(String custName) {
        SCustName = custName;
    }

    public String getInnerBankCode() {
        return innerBankCode;
    }

    public void setInnerBankCode(String innerBankCode) {
        this.innerBankCode = innerBankCode;
    }

    public Date getDiscDateStart() {
        return discDateStart;
    }

    public void setDiscDateStart(Date discDateStart) {
        this.discDateStart = discDateStart;
    }

    public Date getDiscDateEnd() {
        return discDateEnd;
    }

    public void setDiscDateEnd(Date discDateEnd) {
        this.discDateEnd = discDateEnd;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getDiscountOutName() {
        return discountOutName;
    }

    public void setDiscountOutName(String discountOutName) {
        this.discountOutName = discountOutName;
    }

    public String getDiscountInName() {
        return discountInName;
    }

    public void setDiscountInName(String discountInName) {
        this.discountInName = discountInName;
    }

    public String getIsInit() {
        return isInit;
    }

    public void setIsInit(String isInit) {
        this.isInit = isInit;
    }

    public String getIdNb() {
        return idNb;
    }

    public void setIdNb(String idNb) {
        this.idNb = idNb;
    }

    public String getCtrctNb() {
        return ctrctNb;
    }

    public void setCtrctNb(String ctrctNb) {
        this.ctrctNb = ctrctNb;
    }

    public String getAcceptorBankCode() {
        return acceptorBankCode;
    }

    public void setAcceptorBankCode(String acceptorBankCode) {
        this.acceptorBankCode = acceptorBankCode;
    }

    public BigDecimal getFBillAmountUp() {
        return FBillAmountUp;
    }

    public void setFBillAmountUp(BigDecimal billAmountUp) {
        FBillAmountUp = billAmountUp;
    }

    public Date getStartDDueDt() {
        return startDDueDt;
    }

    public void setStartDDueDt(Date startDDueDt) {
        this.startDDueDt = startDDueDt;
    }

    public Date getEndDDueDt() {
        return endDDueDt;
    }

    public void setEndDDueDt(Date endDDueDt) {
        this.endDDueDt = endDDueDt;
    }

    public String getAcceptorNm() {
        return acceptorNm;
    }

    public void setAcceptorNm(String acceptorNm) {
        this.acceptorNm = acceptorNm;
    }

    public String getsAcceptorBankCode() {
        return sAcceptorBankCode;
    }

    public void setsAcceptorBankCode(String sAcceptorBankCode) {
        this.sAcceptorBankCode = sAcceptorBankCode;
    }

    public String getsBillType() {
        return sBillType;
    }

    public void setsBillType(String sBillType) {
        this.sBillType = sBillType;
    }

    public String getrRcvgprsnofrcrscmonid() {
        return rRcvgprsnofrcrscmonid;
    }

    public void setrRcvgprsnofrcrscmonid(String rRcvgprsnofrcrscmonid) {
        this.rRcvgprsnofrcrscmonid = rRcvgprsnofrcrscmonid;
    }

    public String getrRcrsrcmonid() {
        return rRcrsrcmonid;
    }

    public void setrRcrsrcmonid(String rRcrsrcmonid) {
        this.rRcrsrcmonid = rRcrsrcmonid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPlApplyNm() {
        return plApplyNm;
    }

    public void setPlApplyNm(String plApplyNm) {
        this.plApplyNm = plApplyNm;
    }

    public Date getStartContract() {
        return startContract;
    }

    public void setStartContract(Date startContract) {
        this.startContract = startContract;
    }

    public Date getEndContract() {
        return endContract;
    }

    public void setEndContract(Date endContract) {
        this.endContract = endContract;
    }

    public String getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(String receiptNum) {
        this.receiptNum = receiptNum;
    }

    public BigDecimal getReceiptMax() {
        return receiptMax;
    }

    public void setReceiptMax(BigDecimal receiptMax) {
        this.receiptMax = receiptMax;
    }

    public BigDecimal getReceiptMin() {
        return receiptMin;
    }

    public void setReceiptMin(BigDecimal receiptMin) {
        this.receiptMin = receiptMin;
    }

    public String getRecePayType() {
        return recePayType;
    }

    public void setRecePayType(String recePayType) {
        this.recePayType = recePayType;
    }

    public String getProtocolNo() {
        return protocolNo;
    }

    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getCirStage() {
        return cirStage;
    }

    public void setCirStage(String cirStage) {
        this.cirStage = cirStage;
    }

	public String getSumType() {
		return sumType;
	}

	public void setSumType(String sumType) {
		this.sumType = sumType;
	}

	public String getSumValue() {
		return sumValue;
	}

	public void setSumValue(String sumValue) {
		this.sumValue = sumValue;
	}

	public Double getFPED() {
		return FPED;
	}

	public void setFPED(Double fPED) {
		FPED = fPED;
	}
    
    
}
