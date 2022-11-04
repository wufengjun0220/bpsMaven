package com.mingtech.application.pool.infomanage.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.common.PoolDictionaryCache;
import com.mingtech.application.pool.edu.domain.BailDetail;
/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: yufei
 * @日期: Jun 2, 2009 3:45:00 PM
 * @描述: [CustomerDto]客户信息对象
 */
public class CustomerDto implements java.io.Serializable{

	/** serialVersionUID*/
	private static final long serialVersionUID = -5574368904686919253L;
	// Fields
	private String pkIxBoCustomerId;// 主键
	private String coreCustomerCode ;//客户 号 
	private String SCustAcc;// 客户帐号(网银)
	private String SCustName;// 客户名称
	private String SCustBankName;// 客户开户行名称
	private String SCustBankCode;// 客户开户行行号
	private String SCustBankAddr;// 开户行地址
	
	private int SCustFlag;// 生效标志 0生效；1无效
	private String finaType;//大的客户类型 0:企业客户;1:同业客户
	private String custType;//新客户类型  01企业客户；02：系统内机构；03同业银行；04：非银行金融机构
	private String bankNo;//同业客户使用 行号
	private String SOrgCode;// 组织机构代码
	/**
	 * RC00接入行
	RC01企业
	RC02人民银行
	RC03被代理行
	RC04被代理财务公司
	RC05接入财务公司
	RC20 资管计划接入  【票交所升级】
	 */
	private String roleCode;//参与者类别，参考人行规定
	private String roleCodeString;
	private String certType;//证件类型，兼容三证合一 使用  暂时使用 01：三证合一编码；02组织机构代码
	private String certTypeString;
	private String certCode;//证件号码 统一社会登记码18位，9-17位 为组织机构代码
	private String SLoanCardNo;// 贷款卡号
	private String SLiceCode;// 营业执照号码
	private BigDecimal SCapital;// 注册资金
	private String SCustTele;// 电话
	private String SCustFax;// 传真
	private String SCustPost;// 邮政编码
	private String SCustEmail;// 电子邮件
	private String SCustPro;// 省份
	private String SCustCity;// 城市
	private String SClassification;// 行业分类(金融、it、钢铁) 
	private String SCompanySizeNum;//企业规模分类 0_203：大型企业、中型企业、小型企业、微型企业；【票交所】
	private String agriFlag;//是否三农企业【票交所】
	private String area;//出票人地区 【票交所】
	private String isGreen;//出票人是否是绿色企业 【票交所】
	/** 页面使用字段  */
	private String SCompanySizeNumName;//企业规模分类页面使用
	private String agriFlagName;//是否是三农企业页面使用
	private String areaName;//出票人地区页面使用
	private String isGreenName;//是否是绿色企业页面使用
	
	private String pjsBranchNo;//票交所机构号
	private String pjsBranchName;//票交所机构名称
	private String pjsUserNo;//票交所交易柜员号
	private String pjsUserName;//票交所交易柜员名

	private String groupFlag;//是否是公司总部    01总部  00分部
	private String groupCustId;//总部id
	private String groupCustName;//总部名称
	
	
	private String DCustStartDt;// 有效起始日
	private String DCustEndDt;// 有效截止日

	private String SKeepAcc;// 代保管表外账号
	private String SReceAcc;// 代托收专户账
	private String SCollectAcc;// 代发托收款账
	private String SPareorgCode;// 企业所属机构号
	private String SMbfeBankCode;// 大额支付行号
	
	private String SCustAccout;//客户账号，不与数据库做关联不存储 ，用户页面显示当前大额支付行号下的客户信息使用
	private String SIfCheckTrade;// 是否贸易背景检查(CTN_02: 否;CTY_01: 是)
	
	private float SCustRate;//企业底限利率
	private String SCustLevel;//企业等级
	// wg add
	private String SSignFlag;// 签约标志 null未签约；1已签约
	private String accptAuth;//商票签发权限标志 0无 1 有
	
	//--------票据池新增字段  衣晓龙--------
	
	private String crdtLevel;  //信用级别
	private String drftOpnStt;//票据池开通标记，已开通：DOP_01，已关闭：DOP_00；客户签约后认为票据池即可开通，解约后票据池关闭
	private String plStorageStt;  //开通代保管业务标志：PST_01开通，PST_00关闭'
	
	private BailDetail curBail;//活期保证金账号，不存数据库，专属于票据池
	private BigDecimal storageLowAmt;       //代保管最低价
	private BigDecimal storagePayRate;      //代保管费率
	private Date storageRateStartDate;         //代保管费用计算有效期起始日
	private Date storageRateEndDate;         //代保管费用计算有效期失效日
	private BigDecimal plDraftLowAmt;      //票据池最低价(单张票的最低手续费收费金额)
	private BigDecimal plDraftPayRate;      //票据池费率
	private Date plDraftRateStartDate;         //票据池费用计算有效期起始日
	private Date plDraftRateEndDate;         //票据池费用计算有效期失效日
	private BigDecimal defDisRate;         //客户打折率
	private Date defDisRateStartDate;         //客户打折率起始日
	private Date defDisRateEndDate;         //客户打折率失效日
	private String rateAdjust;     //费率调整 页面查询使用不存数据库
	private String discountAdjust; //打折率调整 页面查询使用不存数据库
	private String SCheckFlag ;//是否查询查复（0：否；1：是） 
	private String groupCustomer; // 是否集团客户(0或者空: 否; 1: 是)
	private String custNum;//客户编号
	
		//--------票据池新增字段 end--------
	/**
	 * 无效
	 */
	public static final int Flag_NoUse = 0;
	/**
	 * 有效
	 */
	public static final int Flag_InUse = 1;
	
	
	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getGroupCustomer() {
		return groupCustomer;
	}

	public void setGroupCustomer(String groupCustomer) {
		this.groupCustomer = groupCustomer;
	}

	// Constructors
	/** default constructor */
	public CustomerDto(){
	}
	
	/** xingyu add */
	public CustomerDto(String SCustName,String SCustBankCode,String SCustAccout,String SOrgCode){
		this.SCustName = SCustName;
		this.SCustBankCode = SCustBankCode;
		this.SCustAccout = SCustAccout;
		this.SOrgCode = SOrgCode;
	}
	
	public CustomerDto(String SCustName,String SCustBankCode,String SCustAccout){
		this.SCustName = SCustName;
		this.SCustBankCode = SCustBankCode;
		this.SCustAccout = SCustAccout;
	}

	// Property accessors
	public String getPkIxBoCustomerId(){
		return this.pkIxBoCustomerId;
	}

	public void setPkIxBoCustomerId(String pkIxBoCustomerId){
		this.pkIxBoCustomerId = pkIxBoCustomerId;
	}

	/**
	 * <p>方法名称: getSCustAcc|描述: 获取客户帐号(网银)</p>
	 * @return
	 */
	public String getSCustAcc(){
		return this.SCustAcc;
	}

	/**
	 * <p>方法名称: setSCustAcc|描述:set客户账户（网银） </p>
	 * @param SCustAcc
	 */
	public void setSCustAcc(String SCustAcc){
		this.SCustAcc = SCustAcc;
	}

	/**
	 * <p>方法名称: getSCustName|描述:获取客户名称(核心) </p>
	 * @return
	 */
	public String getSCustName(){
		return this.SCustName;
	}

	/**
	 * <p>方法名称: setSCustName|描述:set客户名称(核心) </p>
	 * @param SCustName
	 */
	public void setSCustName(String SCustName){
		this.SCustName = SCustName;
	}

	/**
	 * <p>方法名称: getSOrgCode|描述: 获取组织机构代码</p>
	 * @return
	 */
	public String getSOrgCode(){
		return this.SOrgCode;
	}

	/**
	 * <p>方法名称: setSOrgCode|描述: set组织机构代码</p>
	 * @param SOrgCode
	 */
	public void setSOrgCode(String SOrgCode){
		this.SOrgCode = SOrgCode;
	}

	/**
	 * <p>方法名称: getSLiceCode|描述: 获取营业执照号码</p>
	 * @return
	 */
	public String getSLiceCode(){
		return this.SLiceCode;
	}

	public void setSLiceCode(String SLiceCode){
		this.SLiceCode = SLiceCode;
	}

	/**
	 * <p>方法名称: getSCapital|描述:获取客户注册资金 </p>
	 * @return
	 */
	public BigDecimal getSCapital(){
		return this.SCapital;
	}

	/**
	 * <p>方法名称: setSCapital|描述:set客户注册资金 </p>
	 * @param SCapital
	 */
	public void setSCapital(BigDecimal SCapital){
		this.SCapital = SCapital;
	}

	/**
	 * <p>方法名称: getSLoanCardNo|描述: 获取贷款卡号</p>
	 * @return
	 */
	public String getSLoanCardNo(){
		return this.SLoanCardNo;
	}

	/**
	 * <p>方法名称: setSLoanCardNo|描述: set贷款卡号</p>
	 * @param SLoanCardNo
	 */
	public void setSLoanCardNo(String SLoanCardNo){
		this.SLoanCardNo = SLoanCardNo;
	}

	/**
	 * <p>方法名称: getSCustTele|描述: 获取客户电话</p>
	 * @return
	 */
	public String getSCustTele(){
		return this.SCustTele;
	}

	/**
	 * <p>方法名称: setSCustTele|描述:set客户电话 </p>
	 * @param SCustTele
	 */
	public void setSCustTele(String SCustTele){
		this.SCustTele = SCustTele;
	}

	/**
	 * <p>方法名称: getSCustFax|描述:获取客户传真 </p>
	 * @return
	 */
	public String getSCustFax(){
		return this.SCustFax;
	}

	/**
	 * <p>方法名称: setSCustFax|描述:set客户传真 </p>
	 * @param SCustFax
	 */
	public void setSCustFax(String SCustFax){
		this.SCustFax = SCustFax;
	}

	/**
	 * <p>方法名称: getSCustPost|描述:获取客户邮政编码 </p>
	 * @return
	 */
	public String getSCustPost(){
		return this.SCustPost;
	}

	public void setSCustPost(String SCustPost){
		this.SCustPost = SCustPost;
	}

	/**
	 * <p>方法名称: getSCustEmail|描述: 获取客户Email</p>
	 * @return
	 */
	public String getSCustEmail(){
		return this.SCustEmail;
	}

	public void setSCustEmail(String SCustEmail){
		this.SCustEmail = SCustEmail;
	}

	/**
	 * <p>方法名称: getSCustPro|描述: 获取客户省份</p>
	 * @return
	 */
	public String getSCustPro(){
		return this.SCustPro;
	}

	public void setSCustPro(String SCustPro){
		this.SCustPro = SCustPro;
	}

	/**
	 * <p>方法名称: getSCustCity|描述: 获取客户所在城市</p>
	 * @return
	 */
	public String getSCustCity(){
		return this.SCustCity;
	}

	public void setSCustCity(String SCustCity){
		this.SCustCity = SCustCity;
	}

	/**
	 * <p>方法名称: getSClassification|描述:获取行业分类 </p>
	 * @return
	 */
	public String getSClassification(){
		return this.SClassification;
	}

	public void setSClassification(String SClassification){
		this.SClassification = SClassification;
	}

	/**
	 * <p>方法名称: getSCustFlag|描述: 获取用户生效标志0无效；1生效</p>
	 * @return
	 */
	public int getSCustFlag(){
		return this.SCustFlag;
	}

	public void setSCustFlag(int SCustFlag){
		this.SCustFlag = SCustFlag;
	}


	/**
	 * <p>方法名称: getDCustStartDt|描述:获取有效起始日 </p>
	 * @return
	 */
	public String getDCustStartDt(){
		return this.DCustStartDt;
	}

	public void setDCustStartDt(String DCustStartDt){
		this.DCustStartDt = DCustStartDt;
	}

	/**
	 * <p>方法名称: getDCustEndDt|描述:获取有效截止日 </p>
	 * @return
	 */
	public String getDCustEndDt(){
		return this.DCustEndDt;
	}

	public void setDCustEndDt(String DCustEndDt){
		this.DCustEndDt = DCustEndDt;
	}

	/**
	 * <p>方法名称: getSCustBankName|描述: 获取客户开户行名称</p>
	 * @return
	 */
	public String getSCustBankName(){
		return this.SCustBankName;
	}

	public void setSCustBankName(String SCustBankName){
		this.SCustBankName = SCustBankName;
	}

	/**
	 * <p>方法名称: getSCustBankCode|描述: 获取户开户行行号(网银)</p>
	 * @return
	 */
	public String getSCustBankCode(){
		return this.SCustBankCode;
	}

	public void setSCustBankCode(String SCustBankCode){
		this.SCustBankCode = SCustBankCode;
	}

	/**
	 * <p>方法名称: getSCustBankAddr|描述: 获取开户行地址</p>
	 * @return
	 */
	public String getSCustBankAddr(){
		return this.SCustBankAddr;
	}

	public void setSCustBankAddr(String SCustBankAddr){
		this.SCustBankAddr = SCustBankAddr;
	}

	/**
	 * <p>方法名称: getSKeepAcc|描述: 获取代保管表外账号</p>
	 * @return
	 */
	public String getSKeepAcc(){
		return this.SKeepAcc;
	}

	public void setSKeepAcc(String SKeepAcc){
		this.SKeepAcc = SKeepAcc;
	}

	/**
	 * <p>方法名称: getSReceAcc|描述:获取代托收专户账 </p>
	 * @return
	 */
	public String getSReceAcc(){
		return this.SReceAcc;
	}

	public void setSReceAcc(String SReceAcc){
		this.SReceAcc = SReceAcc;
	}

	/**
	 * <p>方法名称: getSCollectAcc|描述: 获取代发托收款账</p>
	 * @return
	 */
	public String getSCollectAcc(){
		return this.SCollectAcc;
	}

	public void setSCollectAcc(String SCollectAcc){
		this.SCollectAcc = SCollectAcc;
	}

	/**
	 * <p>方法名称: getSPareorgCode|描述:获取企业所属机构号 </p>
	 * @return
	 */
	public String getSPareorgCode(){
		return this.SPareorgCode;
	}

	public void setSPareorgCode(String SPareorgCode){
		this.SPareorgCode = SPareorgCode;
	}

	/**
	 * <p>方法名称: getSMbfeBankCode|描述: 获取大额支付行号</p>
	 * @return
	 */
	public String getSMbfeBankCode(){
		return this.SMbfeBankCode;
	}

	public void setSMbfeBankCode(String SMbfeBankCode){
		this.SMbfeBankCode = SMbfeBankCode;
	}

	public String getSCompanySizeNum() {
		return SCompanySizeNum;
	}

	public void setSCompanySizeNum(String companySizeNum) {
		SCompanySizeNum = companySizeNum;
	}

	public String getSCustAccout() {
		return SCustAccout;
	}

	public void setSCustAccout(String custAccout) {
		SCustAccout = custAccout;
	}

	public String getSIfCheckTrade() {
		return SIfCheckTrade;
	}

	public void setSIfCheckTrade(String SIfCheckTrade) {
		this.SIfCheckTrade = SIfCheckTrade;
	}

	public float getSCustRate() {
		return SCustRate;
	}

	public void setSCustRate(float custRate) {
		SCustRate = custRate;
	}

	public String getSCustLevel() {
		return SCustLevel;
	}

	public void setSCustLevel(String custLevel) {
		SCustLevel = custLevel;
	}

	
	public String getCrdtLevel(){
		return crdtLevel;
	}

	
	public void setCrdtLevel(String crdtLevel){
		this.crdtLevel = crdtLevel;
	}

	
	public String getDrftOpnStt(){
		return drftOpnStt;
	}

	
	public void setDrftOpnStt(String drftOpnStt){
		this.drftOpnStt = drftOpnStt;
	}

	
	public BailDetail getCurBail(){
		return curBail;
	}

	
	public void setCurBail(BailDetail curBail){
		this.curBail = curBail;
	}

	
	public String getPlStorageStt(){
		return plStorageStt;
	}

	
	public void setPlStorageStt(String plStorageStt){
		this.plStorageStt = plStorageStt;
	}

	
	public BigDecimal getStorageLowAmt(){
		return storageLowAmt;
	}

	
	public void setStorageLowAmt(BigDecimal storageLowAmt){
		this.storageLowAmt = storageLowAmt;
	}

	
	public BigDecimal getStoragePayRate(){
		return storagePayRate;
	}

	
	public void setStoragePayRate(BigDecimal storagePayRate){
		this.storagePayRate = storagePayRate;
	}

	
	public BigDecimal getPlDraftLowAmt(){
		return plDraftLowAmt;
	}

	
	public void setPlDraftLowAmt(BigDecimal plDraftLowAmt){
		this.plDraftLowAmt = plDraftLowAmt;
	}

	
	public BigDecimal getPlDraftPayRate(){
		return plDraftPayRate;
	}

	
	public void setPlDraftPayRate(BigDecimal plDraftPayRate){
		this.plDraftPayRate = plDraftPayRate;
	}

	
	public BigDecimal getDefDisRate(){
		return defDisRate;
	}

	
	public void setDefDisRate(BigDecimal defDisRate){
		this.defDisRate = defDisRate;
	}

	
	public Date getDefDisRateStartDate(){
		return defDisRateStartDate;
	}

	
	public void setDefDisRateStartDate(Date defDisRateStartDate){
		this.defDisRateStartDate = defDisRateStartDate;
	}

	
	public Date getDefDisRateEndDate(){
		return defDisRateEndDate;
	}

	
	public void setDefDisRateEndDate(Date defDisRateEndDate){
		this.defDisRateEndDate = defDisRateEndDate;
	}

	
	public String getCoreCustomerCode() {
		return coreCustomerCode;
	}

	public void setCoreCustomerCode(String coreCustomerCode) {
		this.coreCustomerCode = coreCustomerCode;
	}

	public String getRateAdjust(){
		return rateAdjust;
	}

	
	public void setRateAdjust(String rateAdjust){
		this.rateAdjust = rateAdjust;
	}

	
	public String getDiscountAdjust(){
		return discountAdjust;
	}

	
	public void setDiscountAdjust(String discountAdjust){
		this.discountAdjust = discountAdjust;
	}

	public String getSSignFlag() {
		return SSignFlag;
	}

	public void setSSignFlag(String sSignFlag) {
		SSignFlag = sSignFlag;
	}

	public String getSCheckFlag() {
		return SCheckFlag;
	}

	public void setSCheckFlag(String checkFlag) {
		SCheckFlag = checkFlag;
	}

	public Date getStorageRateStartDate() {
		return storageRateStartDate;
	}

	public void setStorageRateStartDate(Date storageRateStartDate) {
		this.storageRateStartDate = storageRateStartDate;
	}

	public Date getStorageRateEndDate() {
		return storageRateEndDate;
	}

	public void setStorageRateEndDate(Date storageRateEndDate) {
		this.storageRateEndDate = storageRateEndDate;
	}

	public Date getPlDraftRateStartDate() {
		return plDraftRateStartDate;
	}

	public void setPlDraftRateStartDate(Date plDraftRateStartDate) {
		this.plDraftRateStartDate = plDraftRateStartDate;
	}

	public Date getPlDraftRateEndDate() {
		return plDraftRateEndDate;
	}

	public void setPlDraftRateEndDate(Date plDraftRateEndDate) {
		this.plDraftRateEndDate = plDraftRateEndDate;
	}


	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getCertTypeString() {
		//01：三证合一编码；02组织机构代码
		if("01".equals(this.getCertType())){
			return "三证合一代码";
		}
		return "组织机构代码";
	}

	public void setCertTypeString(String certTypeString) {
		this.certTypeString = certTypeString;
	}

	public String getRoleCodeString() {
		/**
		 * RC00接入行
		RC01企业
		RC02人民银行
		RC03被代理行
		RC04被代理财务公司
		RC05接入财务公司
		 */
		if("RC00".equals(this.getRoleCode())){
			return "接入行";
		}else if("RC01".equals(this.getRoleCode())){
			return "企业";
		}else if("RC02".equals(this.getRoleCode())){
			return "人民银行";
		}else if("RC03".equals(this.getRoleCode())){
			return "被代理行";
		}else if("RC04".equals(this.getRoleCode())){
			return "被代理财务公司";
		}else if("RC05".equals(this.getRoleCode())){
			return "接入财务公司";
		}else if("RC20".equals(this.getRoleCode())){
			return "其他";
		}
		return "";
	}

	public void setRoleCodeString(String roleCodeString) {
		this.roleCodeString = roleCodeString;
	}

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getFinaType() {
		return finaType;
	}

	public void setFinaType(String finaType) {
		this.finaType = finaType;
	}

	public String getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(String groupFlag) {
		this.groupFlag = groupFlag;
	}

	public String getGroupCustId() {
		return groupCustId;
	}

	public void setGroupCustId(String groupCustId) {
		this.groupCustId = groupCustId;
	}

	public String getGroupCustName() {
		return groupCustName;
	}

	public void setGroupCustName(String groupCustName) {
		this.groupCustName = groupCustName;
	}

	public String getAgriFlag() {
		return agriFlag;
	}

	public void setAgriFlag(String agriFlag) {
		this.agriFlag = agriFlag;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getIsGreen() {
		return isGreen;
	}

	public void setIsGreen(String isGreen) {
		this.isGreen = isGreen;
	}

	public String getSCompanySizeNumName() {
		return SCompanySizeNumName = PoolDictionaryCache.getSCompanySizeNum(this
				.getSCompanySizeNum());
		/**
		if("01".equals(this.getSCompanySizeNum())){
			return "大型企业";
		}else if("02".equals(this.getSCompanySizeNum())){
			return "小微企业";
		}else if("03".equals(this.getSCompanySizeNum())){
			return "中型企业";
		}else{
			return "小型企业"; 
		}*/
	}

	public void setSCompanySizeNumName(String sCompanySizeNumName) {
		this.SCompanySizeNumName = sCompanySizeNumName;
	}

	public String getAgriFlagName() {
		if("01".equals(this.getAgriFlag())){
			return "是";
		}
		return "否";
	}

	public void setAgriFlagName(String agriFlagName) {
		this.agriFlagName = agriFlagName;
	}

	public String getAreaName() {
		return areaName = PoolDictionaryCache.getArea(this
				.getArea());
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getIsGreenName() {
		if("01".equals(this.getIsGreen())){
			return "是";
		}else{
			return "否";
		}
	}

	public void setIsGreenName(String isGreenName) {
		this.isGreenName = isGreenName;
	}

	public String getPjsBranchNo() {
		return pjsBranchNo;
	}

	public void setPjsBranchNo(String pjsBranchNo) {
		this.pjsBranchNo = pjsBranchNo;
	}

	public String getPjsBranchName() {
		return pjsBranchName;
	}

	public void setPjsBranchName(String pjsBranchName) {
		this.pjsBranchName = pjsBranchName;
	}

	public String getPjsUserNo() {
		return pjsUserNo;
	}

	public void setPjsUserNo(String pjsUserNo) {
		this.pjsUserNo = pjsUserNo;
	}

	public String getPjsUserName() {
		return pjsUserName;
	}

	public void setPjsUserName(String pjsUserName) {
		this.pjsUserName = pjsUserName;
	}

	public String getAccptAuth() {
		return accptAuth;
	}

	public void setAccptAuth(String accptAuth) {
		this.accptAuth = accptAuth;
	}

	public String getCustNum() {
		return custNum;
	}

	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}
	
}