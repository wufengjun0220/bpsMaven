package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
 * 传值使用的DTO
 * @author Ju Nana
 * @version v1.0
 * @date 2021-10-27
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class PoolBillInfoDto {

	
	private static final long serialVersionUID = 1L;
	
	private String billinfoId;// id
	private String SBillNo; // 票号
	private String discBillId;//票据ID（来源电票系统）
	
	private String SBillType; // 票据类型
	private String SBillMedia;// 票据介质
	private Date DIssueDt; // 出票日
	private Date DDueDt; // 到期日
	private BigDecimal FBillAmount; // 票面金额
	
	
	private String SIssuerName; // 出票人全称
	private String Drwrcmonid;//出票人组织机构代码
	
	private String SIssuerAccount;// 出票人账号
	private String SIssuerBankName;// 出票人开户行名称
	private String SIssuerBankCode;// 出票人开户行行号
	
	private String SPayeeName;// 收款人全称
	private String SPayeeAccount; // 收款人账号
	private String SPayeeBankName;// 收款人开户行全称
	private String SPayeeBankCode;// 收款人开户行行号
	
	private String SAcceptorAccount; // 承兑人账号
	private String SAcceptorBankCode;// 承兑行行号
	private String SAcceptorBankName;// 承兑行名称
	private String SAcceptor; // 承兑人全称
	

	private String SMbfeBankCode;// 票据持有行行号（大额行号）【很重要】
	private String draftOwnerSvcrName; // 票据持有人开户行名称【很重要】
	private String SOwnerAcctId; // 票据当前持有人账号【很重要】
	private String SOwnerOrgCode;// 持有人组织机构号【很重要】
	private String SOwnerBillName; // 持有人名称【很重要】
	
	private String SifPoolFlag;//是否是票据池对象  0：虚拟票据池 1：票据池
	
	private String ifRiskBill;//是否为风险票据 "0"代表是
	private String ifPoolIn; //是否可入池 ：0：否 1：是（新增）
	private String SRemark; // 不可入池原因（备注）
	private String SUsage; // 用途
	
	private String SECDSStatus;// 人行状态【很重要，对应人行的状态码】【很重要】
	private String SECDSStatusDesc;//人行状态描述【很重要】
	private String lastCurEcdsDraftStsCode;// 前置状态
	private String lastCurEcdsDraftSts;//前置状态名
	private String SDealStatus;// 处理标识（DS_000-未处理；DS_001-入池处理中，DS_002：已入池，DS_003：出池处理中，DS_004已出池）【很重要】
	private String overFlag;//票据逾期状态--每天日终定时更新
	
	
	/*******票据池使用***end******/
	
	private String pcdsStatus; // 纸票状态
	private String pcdsStatusName; // 纸票状态名称(取值用，不存数据库)
	private String SIssuerCustId; // 出票人客户号【没有用】
	private String RoleCode;//出票人类别
	private String SBanEndrsmtFlag;//不得转让标记   0：可转让   1：不可转让
	private String isseAmtValue; // 币种【默认CNY】
	private String SAcceptorProto ;//承兑协议号【承兑签收完成后，收到人行033后会写入】
	private Date SAcceptorDt;//承兑日期【出票人收到031签收报文后更新】
	
	private String SIfDirectAccep;// 是否我行承兑【收到002提示承兑申请的034时会赋值】没有地方使用这个属性；
	
	
	private String draftUseAuthrty; // 票据所有状态（完全所有、部分权限、无权限）【很重要】
	private String draftOwnerSts; // 票据持有类型（本行持有、客户持有、本行被代理行持有、他行持有）【很重要】

	private String SOwnerBankName;   //  申请人开户行名称
	private String SOwnerBankCode;  // 申请人开户行行号
	private String SAcceptorAddress;//承兑人地址
	private String SContractNo; // 合同号
	
	private Date DCreateDt; // 创建时间
	private String SBranchId;// 机构ID
	private String stockOrgId;//库管机构id--保存库存保管行信息
	private String product_id;// 产品ID    ----该字段启用目的为标记同一张票二次入池的情况，与pool_in/pool_out/pl_pool 三表中的【终结标识】字段含义一致
	private String oldProductId;//原产品ID   撤销记账使用 
	private String SBillTypeName;// 类型名称
	private String SBillMediaName;// 票据介质名称
	private String StroverSign;// 追索标识
	private String SECDSStatusName;// 人行状态名称
	private String SDealStatusName;
	
	private String ElctrncSgntr;//电子签名
	private Date DResellStartDt;  //回购开始日 没有映射到数据库存放临时值以便前台显示
	private Date DResellEndDt;    //回购截止日 没有映射到数据库存放临时值以便前台显示
	private String ClearingFlag;//清算方式
	private String clearingFlagName;//清算方式名称
	private String overFlagName;//票据逾期状态 - 中文名称
	private String drwrCredit; // 出票人信用等级
	private String drwrCreditAgency; // 出票人评价机构
	private Date drwrCreditDueDt; // 出票人评级到期日
	private String accptrCdtRatgs; // 承兑人信用等级
	private String accptrCdtRatgAgcy; // 承兑人评价机构
	private Date accptrCdtRatgDueDt; // 承兑人评级到期日
	private Date accptrSgnUpDt; // 承兑回复日期
	private Date accptrGuarnteeDt; // 承兑保证日期
	private Date drwrGuarnteeDt; // 出票保证人保证日期
	
	private String draftAccptrTp;//票据承兑类型（本行承兑、本行客户承兑、他行承兑）
	private String SSenderMsgStatus;//发送方报文控制状态
	private String SSenderMsgStatusName;//发送方报文控制状态
	private String SReceiverMsgStatus;//接收方报文控制状态
	private String SifBidirect;//是否双向
	private String BillIfExist;//是否已存在
	private String coreBatchNo;//核心批次号【买入返售存买入批次号】
	private String instorageSts;//出入库状态
	
	private String invcNb; // 发票号码
//	private String hasImage;//是否有影像附件  默认为0没有  1有
	private String proprietorship;//票据所有权(01本行所有、02他行所有)
	private BigDecimal billBuyCostRate;//买入成本利率
//	private String buyCostRateType;//买入成本利率类型
	private Date billPossessDate;//票据持有日期(买断式买入为票据到期日、买入返售的为返售到期日、双买的为双买到期日)

	
	/*票据池*/
	//2010-10-13添加 发托使用
	//private DraftStorage draftStorage;// 代保管对象
	//private DraftPool draftPool;// 票据池对象
	
	
	/**
	 * 最近买入来源
	 * 例如：贴现买入、系统内卖出，最近来源为：系统内买入的ID；
	 *  再经过：卖出回购、回购到期买回，还是系统内买入的ID；
	 * 结清上一笔业务需要此值；
	 * 余额结转会使用此值；
	 */
	private String billBuyMode;//最近买入类型票据买入方式 0_83  PublicStaticDefineTab>> BILL_billBuyMode
	private String lastSourceId;//上次买入来源ID 系统内买入使用此字段 存最近的买入清单ID 
	/**
	 * 最初买入来源
	 * 票据最初进入系统的ID；
	 * 贴现买入、转贴现买入，此值在没出票据系统时，始终不变；
	 * 额度会使用此值；
	 */
	private String sourceId;//买入来源ID  始终存 最初的买入来源ID
	private String sourceBuyMode;//最初来源 业务类型0_83  PublicStaticDefineTab>> BILL_billBuyMode
	private String saleId;//卖出回购业务专用字段，卖出回购式存 卖出清单ID
	private String buyInBatchNo;//买入批次号
	private String tradeBranchId;//交易机构Id  Xu Carry add 20170413

	/*------------------票交所start--------------------------*/
	//票交所
	private String issuerSocialCode;//出票人社会信用代码
	private String issuerClass;//出票人行业分类
	private String issuerComScale;//出票人企业规模
	private String issuerAgriFlag;//出票人是否三农企业【票交所】  1是 0否
	private String issuerArea;//出票人地区
	private String issuerIsGreen;//出票人是否绿色企业  1是  0否
	private String acceptorSocialCode;//承兑人社会信用代码
	private String payeeSocialCode;//收款人社会信用代码
	//票交所
	private String pjsBillStatus;//票据状态
	private String pjsCirculationStatus;//流转状态
	private String pjsRiskStatus= "RS00";//风险状态 默认非风险票
	private String pjsStockStatus;//库存状态
	private String pjsLastBillStatus;//上一票据状态
	private String pjsLastCirculationStatus;//上一流转状态
	private String pjsLastStockStatus;//上一库存状态
	private String pjsLastRiskStatus;//上一风险状态
	private String acptsvcrbrid;//承兑人开户行机构代码
	private String dsctbrid;//贴现行机构代码  
	private String dsctbrName;//贴现行名称
	private String dsctBankNo;//贴现行行号
	private String dsctRgtBrid;//贴现权属登记机构代码
	private String dsctRgtBrName;//贴现权属登记机构名称
	private String dsctRgtBankNo;//贴现权属登记机构行号
	
	private String stockBrchId;//实物保管机构代码
	private String stockBrchName;//实物保管机构名称
	private String stockBankNo;//实物保管机构行号
	
	private String addgrntbrid;//保证增信行机构代码  
	private String acptcfmbrid;//承兑行（确认）机构代码 
	private String acptgrntbrid;//承兑保证行机构代码【目前看保证只录入了行号】
	private String dsctgrntbrid;//贴现保证人机构代码
	
	private String ownerBranchNo;//票据持有机构号
	private String ownerBranchName;//票据持有机构名称
	
	private String sttlBankNo;//解付行行号
	private String dealId;//双买成交单编号【双买+回购嵌套业务 会使用 作为票据包编号】
	//用于票面展示
	private String issuerIsGreenName;
	private String issuerAgriFlagName;
	private String issuerComScaleName;//企业规模
	private String issuerAreaName;
	
	/*------------------票交所end--------------------------*/
	/**
	 * 余额标记：1贴现余额 2同业间回购式卖出余额
	 * 3卖出回购式再贴现余额 4卖断销账
	 * 5返售到期销账 6托收在途余额 7托收销账 8卖出回购到期
	 */
	private String balanceFlag;
	/**
	 * 余额机构号
	 */
	private String balanceBrchId;

	/*------风险-start--------*/
	//风险说明：RISK_COMMENT  =  set  名单信息+风险信息
	private String blackFlag;//黑名单标志	 01灰名单    02黑名单
	private String rickLevel;//风险等级 低风险、高风险、不在风险名单
	private  String riskComment;//风险说明
	private  String creditObjType;//额度主体类型  1-同业额度  2-对公额度
	private String guarantDiscName;//保贴人名称           
	private String guarantDiscNo;  //保贴编号   
	private String acptHeadBankNo;//承兑行总行行号，用于占用mis额度时候使用
	private String acptHeadBankName;//承兑行总行行名，用于占用mis额度时候使用
	/*------风险-end--------*/

	/*-----核心客户信息---start-------*/
	private String custNo;//核心客户号
	private String custName;//客户名称
	/*-----核心客户信息---end-------*/

	private String accNo;// 电票签约账号
	
	private String TXFlag;//强贴标志
	private String poolAgreement;//票据池编号
	private String bbspLock;//BBSP系统锁票标记：MIS系统发起贴现申请后进行锁票，避免票据池系统解质押出池后能够在其他系统对发起贴现申请的票据的操作。
	private String ebkLock;//0：上锁   1：未上锁   网银系统经办锁：网银系统经办提交后避免已经经办的票被再次查到的锁，该锁只对网银系统有效。
	
	private String workerName;//经办人姓名
	private String workerCard;//经办人身份证号
	private String workerPhone;//经办人手机号
	private String accptrOrg;//承兑人组织机构代码
	
	private String pOutBatchNo;//纸票出池批次号：用来记录从网银发起的纸票出池批次
	private Date lastOperTm;//最后一次操作时间
	private String lastOperName;//最后一次操作说明
	
	private String zyFlag;//自动入池标识，自动入池01 在自动入池的自动任务，以及网银查询接口PJC001查询中赋值
	private String cpFlag;//财票标识  1 财票
	
	
	
	
	public String getAcptHeadBankName() {
		return acptHeadBankName;
	}

	public void setAcptHeadBankName(String acptHeadBankName) {
		this.acptHeadBankName = acptHeadBankName;
	}

	public String getCpFlag() {
		return cpFlag;
	}

	public void setCpFlag(String cpFlag) {
		this.cpFlag = cpFlag;
	}

	public String getAcptHeadBankNo() {
		return acptHeadBankNo;
	}

	public void setAcptHeadBankNo(String acptHeadBankNo) {
		this.acptHeadBankNo = acptHeadBankNo;
	}

	public String getIfPoolIn() {
		return ifPoolIn;
	}

	public void setIfPoolIn(String ifPoolIn) {
		this.ifPoolIn = ifPoolIn;
	}

	public String getCreditObjType() {
		return creditObjType;
	}

	public void setCreditObjType(String creditObjType) {
		this.creditObjType = creditObjType;
	}

	public String getGuarantDiscName() {
		return guarantDiscName;
	}

	public void setGuarantDiscName(String guarantDiscName) {
		this.guarantDiscName = guarantDiscName;
	}

	public String getGuarantDiscNo() {
		return guarantDiscNo;
	}

	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}

	public String getZyFlag() {
		return zyFlag;
	}

	public void setZyFlag(String zyFlag) {
		this.zyFlag = zyFlag;
	}

	public Date getLastOperTm() {
		return lastOperTm;
	}

	public void setLastOperTm(Date lastOperTm) {
		this.lastOperTm = lastOperTm;
	}

	public String getLastOperName() {
		return lastOperName;
	}

	public void setLastOperName(String lastOperName) {
		this.lastOperName = lastOperName;
	}

	public String getpOutBatchNo() {
		return pOutBatchNo;
	}

	public void setpOutBatchNo(String pOutBatchNo) {
		this.pOutBatchNo = pOutBatchNo;
	}

	public String getAccptrOrg() {
		return accptrOrg;
	}

	public void setAccptrOrg(String accptrOrg) {
		this.accptrOrg = accptrOrg;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getWorkerCard() {
		return workerCard;
	}

	public void setWorkerCard(String workerCard) {
		this.workerCard = workerCard;
	}

	public String getWorkerPhone() {
		return workerPhone;
	}

	public void setWorkerPhone(String workerPhone) {
		this.workerPhone = workerPhone;
	}

	public String getBbspLock() {
		return bbspLock;
	}

	public void setBbspLock(String bbspLock) {
		this.bbspLock = bbspLock;
	}

	public String getEbkLock() {
		return ebkLock;
	}

	public void setEbkLock(String ebkLock) {
		this.ebkLock = ebkLock;
	}

	public String getPoolAgreement() {
		return poolAgreement;
	}

	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}

	public String getTXFlag() {
		return TXFlag;
	}

	public void setTXFlag(String tXFlag) {
		TXFlag = tXFlag;
	}
	
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getBlackFlag() {
		return blackFlag;
	}

	public void setBlackFlag(String blackFlag) {
		this.blackFlag = blackFlag;
	}

	public String getRickLevel() {
		return rickLevel;
	}

	public void setRickLevel(String rickLevel) {
		this.rickLevel = rickLevel;
	}

	public String getRiskComment() {
		return riskComment;
	}

	public void setRiskComment(String riskComment) {
		this.riskComment = riskComment;
	}

	public String getBalanceFlag() {
		return balanceFlag;
	}


	public void setBalanceFlag(String balanceFlag) {
		this.balanceFlag = balanceFlag;
	}


	public String getBalanceBrchId() {
		return balanceBrchId;
	}


	public void setBalanceBrchId(String balanceBrchId) {
		this.balanceBrchId = balanceBrchId;
	}


	public String getSourceId() {
		return sourceId;
	}


	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}


	public String getLastSourceId() {
		return lastSourceId;
	}


	public void setLastSourceId(String lastSourceId) {
		this.lastSourceId = lastSourceId;
	}


//	public String getBillClass() {
//		return billClass;
//	}
//
//
//	public void setBillClass(String billClass) {
//		this.billClass = billClass;
//	}
//	public String getAccount_129Num() {
//		return Account_129Num;
//	}
//
//
//	public void setAccount_129Num(String account_129Num) {
//		Account_129Num = account_129Num;
//	}


//	public String getBatchNo() {
//		return batchNo;
//	}
//
//
//	public void setBatchNo(String batchNo) {
//		this.batchNo = batchNo;
//	}


	public String getSSenderMsgStatus(){
		return SSenderMsgStatus;
	}


	public void setSSenderMsgStatus(String senderMsgStatus){
		SSenderMsgStatus = senderMsgStatus;
	}


	public String getSReceiverMsgStatus(){
		return SReceiverMsgStatus;
	}


	public void setSReceiverMsgStatus(String receiverMsgStatus){
		SReceiverMsgStatus = receiverMsgStatus;
	}

	public String getElctrncSgntr() {
		return ElctrncSgntr;
	}

	public void setElctrncSgntr(String elctrncSgntr) {
		ElctrncSgntr = elctrncSgntr;
	}

	
	public String getBillinfoId(){
		return this.billinfoId;
	}

	public void setBillinfoId(String billinfoId){
		this.billinfoId = billinfoId;
	}

	public String getSBillNo(){
		return this.SBillNo;
	}

	public void setSBillNo(String SBillNo){
		this.SBillNo = SBillNo;
	}

	public String getSBillType(){
		return this.SBillType;
	}

	public void setSBillType(String SBillType){
		this.SBillType = SBillType;
	}

	public String getSBillMedia(){
		return this.SBillMedia;
	}

	public void setSBillMedia(String SBillMedia){
		this.SBillMedia = SBillMedia;
	}

	public Date getDIssueDt(){
		return this.DIssueDt;
	}

	public void setDIssueDt(Date DIssueDt){
		this.DIssueDt = DIssueDt;
	}

	public Date getDDueDt(){
		return this.DDueDt;
	}

	public void setDDueDt(Date DDueDt){
		this.DDueDt = DDueDt;
	}

	public String getSIfDirectAccep(){
		return this.SIfDirectAccep;
	}

	public void setSIfDirectAccep(String SIfDirectAccep){
		this.SIfDirectAccep = SIfDirectAccep;
	}

	public String getSIssuerName(){
		return this.SIssuerName;
	}

	public void setSIssuerName(String SIssuerName){
		this.SIssuerName = SIssuerName;
	}

	public String getSIssuerCustId(){
		return this.SIssuerCustId;
	}

	public void setSIssuerCustId(String SIssuerCustId){
		this.SIssuerCustId = SIssuerCustId;
	}

	public String getSIssuerAccount(){
		return this.SIssuerAccount;
	}

	public void setSIssuerAccount(String SIssuerAccount){
		this.SIssuerAccount = SIssuerAccount;
	}

	public String getSIssuerBankName(){
		return this.SIssuerBankName;
	}

	public void setSIssuerBankName(String SIssuerBankName){
		this.SIssuerBankName = SIssuerBankName;
	}

	public String getSIssuerBankCode(){
		return this.SIssuerBankCode;
	}

	public void setSIssuerBankCode(String SIssuerBankCode){
		this.SIssuerBankCode = SIssuerBankCode;
	}

	public String getSAcceptorBankCode(){
		return this.SAcceptorBankCode;
	}

	public void setSAcceptorBankCode(String SAcceptorBankCode){
		this.SAcceptorBankCode = SAcceptorBankCode;
	}

	public String getSAcceptorBankName(){
		return this.SAcceptorBankName;
	}

	public void setSAcceptorBankName(String SAcceptorBankName){
		this.SAcceptorBankName = SAcceptorBankName;
	}

	public String getSAcceptor(){
		return this.SAcceptor;
	}

	public void setSAcceptor(String SAcceptor){
		this.SAcceptor = SAcceptor;
	}

	public String getSAcceptorAccount(){
		return this.SAcceptorAccount;
	}

	public void setSAcceptorAccount(String SAcceptorAccount){
		this.SAcceptorAccount = SAcceptorAccount;
	}

	public String getSPayeeName(){
		return this.SPayeeName;
	}

	public void setSPayeeName(String SPayeeName){
		this.SPayeeName = SPayeeName;
	}

	public String getSPayeeAccount(){
		return this.SPayeeAccount;
	}

	public void setSPayeeAccount(String SPayeeAccount){
		this.SPayeeAccount = SPayeeAccount;
	}

	public String getSPayeeBankName(){
		return this.SPayeeBankName;
	}

	public void setSPayeeBankName(String SPayeeBankName){
		this.SPayeeBankName = SPayeeBankName;
	}

	public String getSPayeeBankCode(){
		return this.SPayeeBankCode;
	}

	public void setSPayeeBankCode(String SPayeeBankCode){
		this.SPayeeBankCode = SPayeeBankCode;
	}

	public BigDecimal getFBillAmount(){
		return this.FBillAmount;
	}

	public void setFBillAmount(BigDecimal FBillAmount){
		this.FBillAmount = FBillAmount;
	}

	public String getSContractNo(){
		return this.SContractNo;
	}

	public void setSContractNo(String SContractNo){
		this.SContractNo = SContractNo;
	}

	public String getSBanEndrsmtFlag(){
		return this.SBanEndrsmtFlag;
	}

	public void setSBanEndrsmtFlag(String SBanEndrsmtFlag){
		this.SBanEndrsmtFlag = SBanEndrsmtFlag;
	}
	public String getSBanEndrsmtFlagStr(){
		return PoolDictionaryCache.getCantAttnMarkCnName(this.getSBanEndrsmtFlag());
	}

	public String getSUsage(){
		return this.SUsage;
	}

	public void setSUsage(String SUsage){
		this.SUsage = SUsage;
	}

	public String getSRemark(){
		return this.SRemark;
	}

	public void setSRemark(String SRemark){
		this.SRemark = SRemark;
	}

	public Date getDCreateDt(){
		return this.DCreateDt;
	}

	public void setDCreateDt(Date DCreateDt){
		this.DCreateDt = DCreateDt;
	}
//
//	public Set getBtRecourses(){
//		return this.btRecourses;
//	}
//
//	public void setBtRecourses(Set btRecourses){
//		this.btRecourses = btRecourses;
//	}
//
//	public Set getBtCollateralizations(){
//		return this.btCollateralizations;
//	}
//
//	public void setBtCollateralizations(Set btCollateralizations){
//		this.btCollateralizations = btCollateralizations;
//	}
//
//	public Set getBtCollections(){
//		return this.btCollections;
//	}
//
//	public void setBtCollections(Set btCollections){
//		this.btCollections = btCollections;
//	}
//
//	public Set getBtRediscountSells(){
//		return this.btRediscountSells;
//	}
//
//	public void setBtRediscountSells(Set btRediscountSells){
//		this.btRediscountSells = btRediscountSells;
//	}
//
//	public Set getBtEndorsementInfos(){
//		return this.btEndorsementInfos;
//	}
//
//	public void setBtEndorsementInfos(Set btEndorsementInfos){
//		this.btEndorsementInfos = btEndorsementInfos;
//	}
//
//	public Set getBtAcceptions(){
//		return this.btAcceptions;
//	}
//
//	public void setBtAcceptions(Set btAcceptions){
//		this.btAcceptions = btAcceptions;
//	}
//
//	public Set getBtDiscounts(){
//		return this.btDiscounts;
//	}
//
//	public void setBtDiscounts(Set btDiscounts){
//		this.btDiscounts = btDiscounts;
//	}
//
//	public Set getBtRediscountBuyins(){
//		return this.btRediscountBuyins;
//	}
//
//	public void setBtRediscountBuyins(Set btRediscountBuyins){
//		this.btRediscountBuyins = btRediscountBuyins;
//	}

	public String getSOwnerBillName(){
		return SOwnerBillName;
	}

	public void setSOwnerBillName(String ownerBillName){
		SOwnerBillName = ownerBillName;
	}

	public String getSOwnerOrgCode(){
		return SOwnerOrgCode;
	}

	public void setSOwnerOrgCode(String ownerOrgCode){
		SOwnerOrgCode = ownerOrgCode;
	}

	public String getSBranchId(){
		return SBranchId;
	}

	public void setSBranchId(String branchId){
		SBranchId = branchId;
	}

	public String getSMbfeBankCode(){
		return SMbfeBankCode;
	}

	public void setSMbfeBankCode(String mbfeBankCode){
		SMbfeBankCode = mbfeBankCode;
	}

	public String getSBillTypeName(){
		SBillTypeName = (String) PoolDictionaryCache.getBillType(this.getSBillType());
		return SBillTypeName;
	}

	public void setSBillTypeName(String billTypeName){
		SBillTypeName = billTypeName;
	}

	public String getSBillMediaName(){
		SBillMediaName = (String) PoolDictionaryCache.getBillMedia(this.getSBillMedia());
		return SBillMediaName;
	}

	public void setSBillMediaName(String billMediaName){
		SBillMediaName = billMediaName;
	}

	// 重写equals方法，通过ID判断两个对象是否一样
	public boolean equals(Object arg0){
		if(null == billinfoId || null == arg0){
			return false;
		}
		PoolBillInfo billInfo = (PoolBillInfo) arg0;
		if(this.billinfoId.equals(billInfo.getBillinfoId()))
			return true;
		else
			return false;
	}

//	public String getStroverSign(){
//		return StroverSign;
//	}
//
//	public void setStroverSign(String stroverSign){
//		this.StroverSign = stroverSign;
//	}

	public String getSECDSStatus(){
		return SECDSStatus;
	}

	public void setSECDSStatus(String status){
		this.SECDSStatus = status;
	}

	public String getSDealStatus(){
		return SDealStatus;
	}

	public void setSDealStatus(String dealStatus){
		this.SDealStatus = dealStatus;
	}

	public String getIsseAmtValue() {
		return isseAmtValue;
	}

	public void setIsseAmtValue(String isseAmtValue) {
		this.isseAmtValue = isseAmtValue;
	}

	public String getSECDSStatusName(){
		SECDSStatusName = PoolDictionaryCache.getStatusName(this.getSECDSStatus());
		return SECDSStatusName;
	}

	public void setSECDSStatusName(String statusName){
		this.SECDSStatusName = statusName;
	}

	public String getSDealStatusName(){
		SDealStatusName = PoolDictionaryCache.getDealstatusmap(this.getSDealStatus());
		return SDealStatusName;
	}

	public void setSDealStatusName(String dealStatusName){
		this.SDealStatusName = dealStatusName;
	}

	public String getProduct_id(){
		return product_id;
	}

	public void setProduct_id(String product_id){
		this.product_id = product_id;
	}

	public String getLastCurEcdsDraftStsCode(){
		return lastCurEcdsDraftStsCode;
	}

	public void setLastCurEcdsDraftStsCode(String lastCurEcdsDraftStsCode){
		this.lastCurEcdsDraftStsCode = lastCurEcdsDraftStsCode;
	}

	public String getSOwnerAcctId(){
		return SOwnerAcctId;
	}

	public void setSOwnerAcctId(String ownerAcctId){
		SOwnerAcctId = ownerAcctId;
	}

//	public String getClearingFlag(){
//		return ClearingFlag;
//	}
//
//	public void setClearingFlag(String clearingFlag){
//		ClearingFlag = clearingFlag;
//	}

//	public String getClearingFlagName(){
//		clearingFlagName = (String)PoolDictionaryCache.getSClearWayMap(getClearingFlag());
//		return clearingFlagName;
//	}
//
//	public void setClearingFlagName(String clearingFlagName){
//		this.clearingFlagName = clearingFlagName;
//	}

	public String getRoleCode(){
		return RoleCode;
	}

	public void setRoleCode(String roleCode){
		RoleCode = roleCode;
	}

	public String getSECDSStatusDesc(){
		return SECDSStatusDesc;
	}

	public void setSECDSStatusDesc(String statusDesc){
		SECDSStatusDesc = statusDesc;
	}

	public String getDrwrcmonid() {
		return Drwrcmonid;
	}

	public void setDrwrcmonid(String drwrcmonid) {
		Drwrcmonid = drwrcmonid;
	}

	public String getDrwrCredit(){
		return drwrCredit;
	}

	public void setDrwrCredit(String drwrCredit){
		this.drwrCredit = drwrCredit;
	}

	public String getDrwrCreditAgency(){
		return drwrCreditAgency;
	}

	public void setDrwrCreditAgency(String drwrCreditAgency){
		this.drwrCreditAgency = drwrCreditAgency;
	}

	public Date getDrwrCreditDueDt(){
		return drwrCreditDueDt;
	}

	public void setDrwrCreditDueDt(Date drwrCreditDueDt){
		this.drwrCreditDueDt = drwrCreditDueDt;
	}

	public String getAccptrCdtRatgs(){
		return accptrCdtRatgs;
	}

	public void setAccptrCdtRatgs(String accptrCdtRatgs){
		this.accptrCdtRatgs = accptrCdtRatgs;
	}

	public String getAccptrCdtRatgAgcy(){
		return accptrCdtRatgAgcy;
	}

	public void setAccptrCdtRatgAgcy(String accptrCdtRatgAgcy){
		this.accptrCdtRatgAgcy = accptrCdtRatgAgcy;
	}

	public Date getAccptrCdtRatgDueDt(){
		return accptrCdtRatgDueDt;
	}

	public void setAccptrCdtRatgDueDt(Date accptrCdtRatgDueDt){
		this.accptrCdtRatgDueDt = accptrCdtRatgDueDt;
	}

	public Date getAccptrSgnUpDt(){
		return accptrSgnUpDt;
	}

	public void setAccptrSgnUpDt(Date accptrSgnUpDt){
		this.accptrSgnUpDt = accptrSgnUpDt;
	}

	public String getOverFlag() {
		return overFlag;
	}

	public void setOverFlag(String overFlag) {
		this.overFlag = overFlag;
	}

	public String getOverFlagName() {
		overFlagName = (String)PoolDictionaryCache.getOverFlagTypeMap(this.getOverFlag());
		return overFlagName;
	}


	public void setOverFlagName(String overFlagName) {
		this.overFlagName = overFlagName;
	}


	public String getSAcceptorAddress() {
		return SAcceptorAddress;
	}

	public void setSAcceptorAddress(String acceptorAddress) {
		SAcceptorAddress = acceptorAddress;
	}

	public String getSAcceptorProto() {
		return SAcceptorProto;
	}

	public void setSAcceptorProto(String acceptorProto) {
		SAcceptorProto = acceptorProto;
	}

	public Date getSAcceptorDt() {
		return SAcceptorDt;
	}

	public void setSAcceptorDt(Date acceptorDt) {
		SAcceptorDt = acceptorDt;
	}


	public String getDraftOwnerSvcrName(){
		return draftOwnerSvcrName;
	}


	public void setDraftOwnerSvcrName(String draftOwnerSvcrName){
		this.draftOwnerSvcrName = draftOwnerSvcrName;
	}

	public String getLastCurEcdsDraftSts() {
		return lastCurEcdsDraftSts;
	}

	public void setLastCurEcdsDraftSts(String lastCurEcdsDraftSts) {
		this.lastCurEcdsDraftSts = lastCurEcdsDraftSts;
	}

	public String getDraftAccptrTp() {
		return draftAccptrTp;
	}

	public void setDraftAccptrTp(String draftAccptrTp) {
		this.draftAccptrTp = draftAccptrTp;
	}

	public String getSSenderMsgStatusName(){
		SSenderMsgStatusName = (String)PoolDictionaryCache.getStatusName(getSSenderMsgStatus());
		return SSenderMsgStatusName;
	}

	public void setSSenderMsgStatusName(String senderMsgStatusName){
		SSenderMsgStatusName = senderMsgStatusName;
	}

	public String getSifBidirect(){
		return SifBidirect;
	}

	public void setSifBidirect(String sifBidirect){
		SifBidirect = sifBidirect;
	}

	public Date getAccptrGuarnteeDt(){
		return accptrGuarnteeDt;
	}

	public void setAccptrGuarnteeDt(Date accptrGuarnteeDt){
		this.accptrGuarnteeDt = accptrGuarnteeDt;
	}

	public Date getDrwrGuarnteeDt(){
		return drwrGuarnteeDt;
	}

	public void setDrwrGuarnteeDt(Date drwrGuarnteeDt){
		this.drwrGuarnteeDt = drwrGuarnteeDt;
	}

	public String getBillIfExist() {
		return BillIfExist;
	}

	public void setBillIfExist(String billIfExist) {
		BillIfExist = billIfExist;
	}



	public Date getDResellStartDt(){
		return DResellStartDt;
	}
 
	public String getCoreBatchNo() {
		return coreBatchNo;
	}
 
	public void setDResellStartDt(Date resellStartDt){
		DResellStartDt = resellStartDt;
	}
	
	public Date getDResellEndDt(){
		return DResellEndDt;
	}
	
	public void setDResellEndDt(Date resellEndDt){
		DResellEndDt = resellEndDt;
	}
 
	public void setCoreBatchNo(String coreBatchNo) {
		this.coreBatchNo = coreBatchNo;
	}

	public String getDiscBillId() {
		return discBillId;
	}

	public void setDiscBillId(String discBillId) {
		this.discBillId = discBillId;
	}

	public String getInstorageSts(){
		return instorageSts;
	}

	public void setInstorageSts(String instorageSts){
		this.instorageSts = instorageSts;
	}

	public String getInvcNb(){
		return invcNb;
	}

	public void setInvcNb(String invcNb){
		this.invcNb = invcNb;
	}


	public String getDraftOwnerSts() {
		return draftOwnerSts;
	}

	public void setDraftOwnerSts(String draftOwnerSts) {
		this.draftOwnerSts = draftOwnerSts;
	}


//	public Date getDBuydate() {
//		return DBuydate;
//	}
//
//
//	public void setDBuydate(Date buydate) {
//		DBuydate = buydate;
//	}
//
//
	public String getPcdsStatus(){
		return pcdsStatus;
	}
	
	public void setPcdsStatus(String pcdsStatus){
		this.pcdsStatus = pcdsStatus;
	}


	public String getPcdsStatusName(){
		pcdsStatusName = PoolDictionaryCache.getFromPoolDictMap(this.getPcdsStatus());
		return pcdsStatusName;
	}

	public void setPcdsStatusName(String pcdsStatusName){
		this.pcdsStatusName = pcdsStatusName;
	}


//	public String getHasImage() {
//		return hasImage;
//	}
//
//
//	public void setHasImage(String hasImage) {
//		this.hasImage = hasImage;
//	}

	public String getProprietorship() {
		return proprietorship;
	}


	public void setProprietorship(String proprietorship) {
		this.proprietorship = proprietorship;
	}



	public String getBillBuyMode() {
		return billBuyMode;
	}


	public void setBillBuyMode(String billBuyMode) {
		this.billBuyMode = billBuyMode;
	}



	public BigDecimal getBillBuyCostRate() {
		return billBuyCostRate;
	}


	public void setBillBuyCostRate(BigDecimal billBuyCostRate) {
		this.billBuyCostRate = billBuyCostRate;
	}


	public Date getBillPossessDate() {
		return billPossessDate;
	}


	public void setBillPossessDate(Date billPossessDate) {
		this.billPossessDate = billPossessDate;
	}

//
//	public String getBuyCostRateType() {
//		return buyCostRateType;
//	}
//
//
//	public void setBuyCostRateType(String buyCostRateType) {
//		this.buyCostRateType = buyCostRateType;
//	}
	
	/**
	* <p>方法名称: getNegotiability|描述: 票据流通性</p>
	* @return
	*/
//	public String getNegotiability(){
//		return negotiability;
//	}
	/**
	* <p>方法名称: setNegotiability|描述: 票据流通性</p>
	* @param negotiability
	*/
//	public void setNegotiability(String negotiability){
//		this.negotiability = negotiability;
//	}
//
//	
//	/**
//	* <p>方法名称: getNegotiabilityReason|描述: 瑕疵原因</p>
//	* @return
//	*/
//	public String getNegotiabilityReason(){
//		return negotiabilityReason;
//	}
//	/**
//	* <p>方法名称: setNegotiabilityReason|描述: 瑕疵原因</p>
//	* @param negotiabilityReason
//	*/
//	public void setNegotiabilityReason(String negotiabilityReason){
//		this.negotiabilityReason = negotiabilityReason;
//	}
//
//	
//	/**
//	* <p>方法名称: getNegotiabilityRmk|描述: 瑕疵原因备注</p>
//	* @return
//	*/
//	public String getNegotiabilityRmk(){
//		return negotiabilityRmk;
//	}
//	/**
//	* <p>方法名称: setNegotiabilityRmk|描述: 瑕疵原因备注</p>
//	* @param negotiabilityRmk
//	*/
//	public void setNegotiabilityRmk(String negotiabilityRmk){
//		this.negotiabilityRmk = negotiabilityRmk;
//	}


	
//	/**
//	* <p>方法名称: getDraftStorePlace|描述: 是否存放在本行</p>
//	* @return
//	*/
//	public String getDraftStorePlace(){
//		return draftStorePlace;
//	}
//	/**
//	* <p>方法名称: setDraftStorePlace|描述: 是否存放在本行</p>
//	* @param draftStorePlace
//	*/
//	public void setDraftStorePlace(String draftStorePlace){
//		this.draftStorePlace = draftStorePlace;
//	}


	
//	/**
//	* <p>方法名称: getDraftStorePlaceName|描述: 票据存放地名称</p>
//	* @return
//	*/
//	public String getDraftStorePlaceName(){
//		return draftStorePlaceName;
//	}
//	/**
//	* <p>方法名称: setDraftStorePlaceName|描述: 票据存放地名称</p>
//	* @param draftStorePlaceName
//	*/
//	public void setDraftStorePlaceName(String draftStorePlaceName){
//		this.draftStorePlaceName = draftStorePlaceName;
//	}


//	/**
//	* <p>方法名称: getDraftStorePlaceBankCode|描述: 票据存放地行号</p>
//	* @return
//	*/
//	public String getDraftStorePlaceBankCode(){
//		return draftStorePlaceBankCode;
//	}
//	/**
//	* <p>方法名称: setDraftStorePlaceBankCode|描述: 票据存放地行号</p>
//	* @param draftStorePlaceBankCode
//	*/
//	public void setDraftStorePlaceBankCode(String draftStorePlaceBankCode){
//		this.draftStorePlaceBankCode = draftStorePlaceBankCode;
//	}


//	
//	public String getMrjg(){
//		return mrjg;
//	}
//
//
//	
//	public void setMrjg(String mrjg){
//		this.mrjg = mrjg;
//	}
//
//
//	
//	public String getCdjg(){
//		return cdjg;
//	}
//
//
//	
//	public void setCdjg(String cdjg){
//		this.cdjg = cdjg;
//	}


	
	public String getIfRiskBill(){
		return ifRiskBill;
	}


	
	public void setIfRiskBill(String ifRiskBill){
		this.ifRiskBill = ifRiskBill;
	}


	
	/*public DraftStorage getDraftStorage(){
		return draftStorage;
	}


	
	public void setDraftStorage(DraftStorage draftStorage){
		this.draftStorage = draftStorage;
	}


	
	public DraftPool getDraftPool(){
		return draftPool;
	}


	
	public void setDraftPool(DraftPool draftPool){
		this.draftPool = draftPool;
	}*/


	
	public String getSifPoolFlag(){
		return SifPoolFlag;
	}


	
	public void setSifPoolFlag(String sifPoolFlag){
		SifPoolFlag = sifPoolFlag;
	}


	
	public String getSOwnerBankName(){
		return SOwnerBankName;
	}


	
	public void setSOwnerBankName(String sOwnerBankName){
		SOwnerBankName = sOwnerBankName;
	}


	
	public String getSOwnerBankCode(){
		return SOwnerBankCode;
	}


	
	public void setSOwnerBankCode(String sOwnerBankCode){
		SOwnerBankCode = sOwnerBankCode;
	}


//	public String getJjh() {
//		return jjh;
//	}
//
//
//	public void setJjh(String jjh) {
//		this.jjh = jjh;
//	}
//	
//	
//	public String getTsStoreOutType() {
//		return tsStoreOutType;
//	}
//
//
//	public void setTsStoreOutType(String tsStoreOutType) {
//		this.tsStoreOutType = tsStoreOutType;
//	}


//	public String getOutStockbatchNo() {
//		return outStockbatchNo;
//	}
//
//
//	public void setOutStockbatchNo(String outStockbatchNo) {
//		this.outStockbatchNo = outStockbatchNo;
//	}


//	public String getCdEdraftBatchId() {
//		return cdEdraftBatchId;
//	}
//
//
//	public void setCdEdraftBatchId(String cdEdraftBatchId) {
//		this.cdEdraftBatchId = cdEdraftBatchId;
//	}
//
//
//	public String getTsBackRmk() {
//		return tsBackRmk;
//	}
//
//
//	public void setTsBackRmk(String tsBackRmk) {
//		this.tsBackRmk = tsBackRmk;
//	}


	public String getStockOrgId() {
		return stockOrgId;
	}


	public void setStockOrgId(String stockOrgId) {
		this.stockOrgId = stockOrgId;
	}

//
//	public Date getDIssueDt_begin() {
//		return DIssueDt_begin;
//	}
//
//
//	public void setDIssueDt_begin(Date issueDt_begin) {
//		DIssueDt_begin = issueDt_begin;
//	}
//
//
//	public Date getDIssueDt_end() {
//		return DIssueDt_end;
//	}
//
//
//	public void setDIssueDt_end(Date issueDt_end) {
//		DIssueDt_end = issueDt_end;
//	}
//
//
//	public Date getDDueDt_begin() {
//		return DDueDt_begin;
//	}
//
//
//	public void setDDueDt_begin(Date dueDt_begin) {
//		DDueDt_begin = dueDt_begin;
//	}
//
//
//	public Date getDDueDt_end() {
//		return DDueDt_end;
//	}
//
//
//	public void setDDueDt_end(Date dueDt_end) {
//		DDueDt_end = dueDt_end;
//	}
//
//
//	public String getFBillAmount_begin() {
//		return FBillAmount_begin;
//	}
//
//
//	public void setFBillAmount_begin(String billAmount_begin) {
//		FBillAmount_begin = billAmount_begin;
//	}
//
//
//	public String getFBillAmount_end() {
//		return FBillAmount_end;
//	}
//
//
//	public void setFBillAmount_end(String billAmount_end) {
//		FBillAmount_end = billAmount_end;
//	}
//
//
//	
//	public String getFindBankNumber(){
//		return findBankNumber;
//	}
//
//
//	
//	public void setFindBankNumber(String findBankNumber){
//		this.findBankNumber = findBankNumber;
//	}
//
//	
//	public String getQueryBankNumber(){
//		return queryBankNumber;
//	}
//
//
//	
//	public void setQueryBankNumber(String queryBankNumber){
//		this.queryBankNumber = queryBankNumber;
//	}


	
//	public String getDueBillNum(){
//		return dueBillNum;
//	}
//
//
//	
//	public void setDueBillNum(String dueBillNum){
//		this.dueBillNum = dueBillNum;
//	}


	public String getSourceBuyMode() {
		return sourceBuyMode;
	}


	public void setSourceBuyMode(String sourceBuyMode) {
		this.sourceBuyMode = sourceBuyMode;
	}


	public String getOldProductId() {
		return oldProductId;
	}


	public void setOldProductId(String oldProductId) {
		this.oldProductId = oldProductId;
	}

	public String getBuyInBatchNo() {
		return buyInBatchNo;
	}


	public void setBuyInBatchNo(String buyInBatchNo) {
		this.buyInBatchNo = buyInBatchNo;
	}


	public String getSaleId() {
		return saleId;
	}


	public void setSaleId(String saleId) {
		this.saleId = saleId;
	}


	public String getDraftUseAuthrty() {
		return draftUseAuthrty;
	}


	public void setDraftUseAuthrty(String draftUseAuthrty) {
		this.draftUseAuthrty = draftUseAuthrty;
	}


	public String getTradeBranchId() {
		return tradeBranchId;
	}


	public void setTradeBranchId(String tradeBranchId) {
		this.tradeBranchId = tradeBranchId;
	}


	public String getIssuerSocialCode() {
		return issuerSocialCode;
	}


	public void setIssuerSocialCode(String issuerSocialCode) {
		this.issuerSocialCode = issuerSocialCode;
	}


	public String getIssuerClass() {
		return issuerClass;
	}


	public void setIssuerClass(String issuerClass) {
		this.issuerClass = issuerClass;
	}


	public String getIssuerComScale() {
		return issuerComScale;
	}


	public void setIssuerComScale(String issuerComScale) {
		this.issuerComScale = issuerComScale;
	}


	public String getIssuerAgriFlag() {
		return issuerAgriFlag;
	}


	public void setIssuerAgriFlag(String issuerAgriFlag) {
		this.issuerAgriFlag = issuerAgriFlag;
	}



	/**
	 * 票据状态
	 * @return
	 */
	public String getPjsBillStatus() {
		return pjsBillStatus;
	}

	/**
	 * 票据状态
	 * @param pjsBillStatus
	 */
	public void setPjsBillStatus(String pjsBillStatus) {
		this.pjsBillStatus = pjsBillStatus;
	}


	public String getPjsCirculationStatus() {
		return pjsCirculationStatus;
	}


	public void setPjsCirculationStatus(String pjsCirculationStatus) {
		this.pjsCirculationStatus = pjsCirculationStatus;
	}


	public String getPjsRiskStatus() {
		return pjsRiskStatus;
	}


	public void setPjsRiskStatus(String pjsRiskStatus) {
		this.pjsRiskStatus = pjsRiskStatus;
	}


	public String getPjsStockStatus() {
		return pjsStockStatus;
	}


	public void setPjsStockStatus(String pjsStockStatus) {
		this.pjsStockStatus = pjsStockStatus;
	}


	public String getPjsLastBillStatus() {
		return pjsLastBillStatus;
	}


	public void setPjsLastBillStatus(String pjsLastBillStatus) {
		this.pjsLastBillStatus = pjsLastBillStatus;
	}


	public String getPjsLastCirculationStatus() {
		return pjsLastCirculationStatus;
	}


	public void setPjsLastCirculationStatus(String pjsLastCirculationStatus) {
		this.pjsLastCirculationStatus = pjsLastCirculationStatus;
	}


	public String getPjsLastStockStatus() {
		return pjsLastStockStatus;
	}


	public void setPjsLastStockStatus(String pjsLastStockStatus) {
		this.pjsLastStockStatus = pjsLastStockStatus;
	}


	public String getAcptsvcrbrid() {
		return acptsvcrbrid;
	}


	public void setAcptsvcrbrid(String acptsvcrbrid) {
		this.acptsvcrbrid = acptsvcrbrid;
	}


	public String getDsctbrid() {
		return dsctbrid;
	}


	public void setDsctbrid(String dsctbrid) {
		this.dsctbrid = dsctbrid;
	}


	public String getAddgrntbrid() {
		return addgrntbrid;
	}


	public void setAddgrntbrid(String addgrntbrid) {
		this.addgrntbrid = addgrntbrid;
	}


	public String getAcptcfmbrid() {
		return acptcfmbrid;
	}


	public void setAcptcfmbrid(String acptcfmbrid) {
		this.acptcfmbrid = acptcfmbrid;
	}


	public String getAcptgrntbrid() {
		return acptgrntbrid;
	}


	public void setAcptgrntbrid(String acptgrntbrid) {
		this.acptgrntbrid = acptgrntbrid;
	}


	public String getDsctgrntbrid() {
		return dsctgrntbrid;
	}


	public void setDsctgrntbrid(String dsctgrntbrid) {
		this.dsctgrntbrid = dsctgrntbrid;
	}


	public String getDsctbrName() {
		return dsctbrName;
	}


	public void setDsctbrName(String dsctbrName) {
		this.dsctbrName = dsctbrName;
	}


	public String getDsctBankNo() {
		return dsctBankNo;
	}


	public void setDsctBankNo(String dsctBankNo) {
		this.dsctBankNo = dsctBankNo;
	}


	public String getDsctRgtBrid() {
		return dsctRgtBrid;
	}


	public void setDsctRgtBrid(String dsctRgtBrid) {
		this.dsctRgtBrid = dsctRgtBrid;
	}


	public String getDsctRgtBrName() {
		return dsctRgtBrName;
	}


	public void setDsctRgtBrName(String dsctRgtBrName) {
		this.dsctRgtBrName = dsctRgtBrName;
	}


	public String getDsctRgtBankNo() {
		return dsctRgtBankNo;
	}


	public void setDsctRgtBankNo(String dsctRgtBankNo) {
		this.dsctRgtBankNo = dsctRgtBankNo;
	}


	public String getStockBrchId() {
		return stockBrchId;
	}


	public void setStockBrchId(String stockBrchId) {
		this.stockBrchId = stockBrchId;
	}


	public String getStockBrchName() {
		return stockBrchName;
	}


	public void setStockBrchName(String stockBrchName) {
		this.stockBrchName = stockBrchName;
	}


	public String getStockBankNo() {
		return stockBankNo;
	}


	public void setStockBankNo(String stockBankNo) {
		this.stockBankNo = stockBankNo;
	}


	public String getIssuerArea() {
		return issuerArea;
	}


	public void setIssuerArea(String issuerArea) {
		this.issuerArea = issuerArea;
	}


	public String getIssuerIsGreen() {
		return issuerIsGreen;
	}


	public void setIssuerIsGreen(String issuerIsGreen) {
		this.issuerIsGreen = issuerIsGreen;
	}


	public String getAcceptorSocialCode() {
		return acceptorSocialCode;
	}


	public void setAcceptorSocialCode(String acceptorSocialCode) {
		this.acceptorSocialCode = acceptorSocialCode;
	}


	public String getPayeeSocialCode() {
		return payeeSocialCode;
	}


	public void setPayeeSocialCode(String payeeSocialCode) {
		this.payeeSocialCode = payeeSocialCode;
	}


	public String getPjsLastRiskStatus() {
		return pjsLastRiskStatus;
	}


	public void setPjsLastRiskStatus(String pjsLastRiskStatus) {
		this.pjsLastRiskStatus = pjsLastRiskStatus;
	}


	public String getSttlBankNo() {
		return sttlBankNo;
	}


	public void setSttlBankNo(String sttlBankNo) {
		this.sttlBankNo = sttlBankNo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getIssuerIsGreenName() {
		if("0".equals(this.getIssuerIsGreen())){
			issuerIsGreenName="否";
		}else{
			issuerIsGreenName="是";
		}
		return issuerIsGreenName;
	}


	public void setIssuerIsGreenName(String issuerIsGreenName) {
		this.issuerIsGreenName = issuerIsGreenName;
	}


	public String getIssuerAgriFlagName() {
		if("0".equals(this.getIssuerAgriFlag())){
			issuerAgriFlagName="否";
		}else{
			issuerAgriFlagName="是";
		}
		return issuerAgriFlagName;
	}


	public void setIssuerAgriFlagName(String issuerAgriFlagName) {
		this.issuerAgriFlagName = issuerAgriFlagName;
	}


	public String getIssuerComScaleName() {
		return issuerComScaleName=PoolDictionaryCache.getPjsCompanySizeNum(this.getIssuerComScale())==null?"":PoolDictionaryCache.getPjsCompanySizeNum(this.getIssuerComScale()).toString();
	}


	public void setIssuerComScaleName(String issuerComScaleName) {
		this.issuerComScaleName=issuerComScaleName;
	}


	public String getDealId() {
		return dealId;
	}


	public void setDealId(String dealId) {
		this.dealId = dealId;
	}


	public String getStroverSign() {
		return StroverSign;
	}


	public void setStroverSign(String stroverSign) {
		StroverSign = stroverSign;
	}


	public String getOwnerBranchNo() {
		return ownerBranchNo;
	}


	public void setOwnerBranchNo(String ownerBranchNo) {
		this.ownerBranchNo = ownerBranchNo;
	}


	public String getOwnerBranchName() {
		return ownerBranchName;
	}


	public void setOwnerBranchName(String ownerBranchName) {
		this.ownerBranchName = ownerBranchName;
	}


	public String getIssuerAreaName() {
		return issuerAreaName = (String) PoolDictionaryCache.getPjsAreaName(this.getIssuerArea());
		 
	}


	public void setIssuerAreaName(String issuerAreaName) {
		this.issuerAreaName = issuerAreaName;
	}


	public String getClearingFlag() {
		return ClearingFlag;
	}


	public void setClearingFlag(String clearingFlag) {
		ClearingFlag = clearingFlag;
	}


	public String getClearingFlagName() {
		return clearingFlagName;
	}


	public void setClearingFlagName(String clearingFlagName) {
		this.clearingFlagName = clearingFlagName;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	
	
}