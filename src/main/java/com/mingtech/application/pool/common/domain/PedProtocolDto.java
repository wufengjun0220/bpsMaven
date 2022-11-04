package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.framework.common.util.DateTimeUtil;


/**
 * 票据池协议规则属性Dto
 * @author yy
 *
 */
public class PedProtocolDto implements java.io.Serializable{

	private static final long serialVersionUID = -5915401750425379019L;
	
	/*票据池基本信息*/
	private String poolInfoId;//主键ID
	private String poolAgreement;//票据池协议号
	private String poolName;//票据池名称
	private String custnumber;// 客户号
	private String custOrgcode;//客户组织机构代码
	private String custname;// 客户名称
	private String elecDraftAccount;//电票签约账号  (存多个字段，用 | 分隔)
	private String elecDraftAccountName;//电票签约账号名称  (存多个字段，用 | 分隔)
	private Date effstartdate;// 票据池协议生效日期
	private Date effenddate;// 票据池协议截止日期
	private String effstartdateFormart;//协议生效日格式化，用于界面展示
	private String effenddateFormart;//协议到期日格式化，用于界面展示
	private String isGroup;// 是否集团
	private String isGroupName;// 界面显示字段映射：是否集团（0：否，1：是）
	private String busiType; // 业务类型  02虚拟票据池
	private String signingFunction;//签约功能  01：票据账务管家
	private String authperson;// 客户经办人身份证号
	private String licename;// 客户经办人名称
	private String phonenumber;// 客户经办人手机号	
	private String xyflag;// 是否自动续约（0：否 ，1：是）   汉口银行均为自动续约
	private String xyflagName;// 界面显示字段映射：是否自动续约（0：否 ，1：是    汉口银行均为自动续约）
	private String signDeptNo;//签约机构号
	private String signDeptName;//签约机构名称
	
	/*融资票据池基本信息*/
	private String marginAccount;// 池保证金账户
	private String marginAccountName;// 池保证金账户名称
	private String poolMode;// 池模式 01总量控制02期限配比
	private String poolModeName;// 池模式 01总量控制02期限配比 不落库
	private String zyflag;// 是否签自动质押协议（00：否 ，01：是）
	private String zyflagName;// 界面显示字段映射：是否签自动质押协议（00：否 ，01：是）
	private String poolAccount;// 客户结算账户
	private String poolAccountName;//结算账号名称
	private String officeNet;// 受理网点
	private String officeNetName;//受理网点名称
	private String operatorName1; //受理人
	
	/*担保合同信息基本信息*/
	private String creditDeptNo;//融资机构号
	private String creditDeptName;//融资机构名称
	private String contract;//担保合同号
	private String pawnType;//质物类型    ZW_01:票据池高风险  ZW_02：票据池低风险  
	private BigDecimal creditamount = new BigDecimal("0.00");// 担保合同金额 即 授信金额
	private BigDecimal creditFreeAmount = new BigDecimal("0.00");//担保合同未用金额
	private BigDecimal creditUsedAmount = new BigDecimal("0.00");//担保合同已用金额
	private Date contractTransDt; //担保合同签订日期
	private Date contractEffectiveDt;//担保合同生效日期
	private Date contractDueDt;//担保合同到期日
	
	/*状态控制信息*/
	private String vStatus;//虚拟票据池签约状态   VS_01：签约    VS_02:解约
	private String vtStatusName;//虚拟票据池签约状态说明   用于界面显示
	private String openFlag;//融资业务开通标识：00：未签约    01：已签约  02：已解约
	private String openFlagName;//融资业务开通标识名称，用于界面显示，不存库
	private String approveFlag;//融资业务审批标识：01：签约审批中 02：签约审批拒绝  03：签约审批通过  04：解约审批中 05：解约审批拒绝  06：解约审批通过  ----签约功能转移到网银系统之后该字段座位费
	private String approveFlagName;//融资业务审批标识名称，用于界面显示，不存库
	private String frozenstate;//冻结  DJ_00 ：未冻结 DJ_01：保证金冻结   DJ_02：票据冻结   DJ_03：全冻结
	private Date frozenTime;//冻结操作时间
	private String frozenstateName;//冻结状态名称
	private String frozenFlag;//解冻
	private String frozenFlagName;
	private Date operateTime;//操作时间
	//liuxiaodong 修改为业务类型 add
	private String contractType;//签约类型  (01：单户票据池)-----
	private String contractTypeName;
	
	private String accountManager;//客户经理
	private String accountManagerId;//客户经理id
	/*未启用字段*/
	private String custlevel;// 客户级别
	private String busiTypeName; //界面显示字段映射： 业务类型（BT_01开通；BT_02解约；BT_03续约；）---该字段作废  Ju Nana 20190109
	private String authperson2;// 客户经办人2身份证号
	private String licename2; // 客户经办人2名称
	private String phonenumber2;// 客户经办人2手机号
	private String operatorName2; //受理审核人
	private String officeNet2;//审核网点
	private String assetType;// 12.资产类型
	private String poolBill;// 票据托管  1：签约          2：未签约
	private String poolFnan;// 票据池融资  1：签约          2：未签约
	private BigDecimal discountRatio;// 16.池打折比例
	private String isMarginGroup;// 19.是否保证金归集
	private String plCommId;// 客户组织机构代码
	private String effestate;//开通标记 ---该字段作废  Ju Nana 20190109
	private BigDecimal disRate;// 费率
	private String guarantNumber;//质押担保合同编号
	private String plUSCC;//申请人统一信用代码
	private String effestateName; // 生效状态：1.未开通；2.已开通.....  ---该字段作废  Ju Nana 20190109
	private String custlevelName; // 客户级别 0：AAA;1：BBB
	private String isHighRisk;//是否高风险  (02：否    01：是)(风险等级)

	private String ebankFlag;//网银签约标识 ，用于网银参数传递，不入库    01：签约  02：解约 03：修改
	
	/**
	 * 新增信贷产品开立
	 * 类型：银承，流贷，短贷，超短贷   短贷利率
	 */
	private String slivBear;//银承,1:为开立;2:为未开立
	private String flowLoan;//流贷,1:为开立;2:为未开立
	private String shortLoan;//短贷,1:为开立;2:为未开立
	private String ultraShortLoan;//超短贷,1:为开立;2:为未开立
	private BigDecimal shortLoanRate;//短贷利率
	
	/**
	 * 
	 * 
	 */
	private String feeType;//缴费模式01:年费 02:逐笔
	private Date feeIssueDt;//年费生效日
	private Date feeDueDt;//年费到期日

 	
	private String pSignType;//签约类型   --不落库
	private String pBreakType;//解约类型 --不落库
	private String feeModel;//收费标准  --不落库
	
	
	private String isAcctCheck;//票据池保证金支取是否人工审核   0-否  1-是     基础签约时候赋值，总量模式默认为是，期限配比模式默认为否，在【保证金支取管理】界面可修改
	
	


	public String getIsAcctCheck() {
		return isAcctCheck;
	}
	
	public void setIsAcctCheck(String isAcctCheck) {
		this.isAcctCheck = isAcctCheck;
	}
	
	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public Date getFeeIssueDt() {
		return feeIssueDt;
	}

	public void setFeeIssueDt(Date feeIssueDt) {
		this.feeIssueDt = feeIssueDt;
	}

	public Date getFeeDueDt() {
		return feeDueDt;
	}

	public void setFeeDueDt(Date feeDueDt) {
		this.feeDueDt = feeDueDt;
	}





	public String getAccountManager() {
		return accountManager;
	}

	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}

	public String getAccountManagerId() {
		return accountManagerId;
	}

	public void setAccountManagerId(String accountManagerId) {
		this.accountManagerId = accountManagerId;
	}

	public String getElecDraftAccountName() {
		return elecDraftAccountName;
	}

	public String getOfficeNetName() {
		return officeNetName;
	}

	public void setOfficeNetName(String officeNetName) {
		this.officeNetName = officeNetName;
	}

	public void setElecDraftAccountName(String elecDraftAccountName) {
		this.elecDraftAccountName = elecDraftAccountName;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getSignDeptNo() {
		return signDeptNo;
	}

	public void setSignDeptNo(String signDeptNo) {
		this.signDeptNo = signDeptNo;
	}

	public String getSignDeptName() {
		return signDeptName;
	}

	public void setSignDeptName(String signDeptName) {
		this.signDeptName = signDeptName;
	}

	public String getCreditDeptNo() {
		return creditDeptNo;
	}

	public void setCreditDeptNo(String creditDeptNo) {
		this.creditDeptNo = creditDeptNo;
	}

	public String getCreditDeptName() {
		return creditDeptName;
	}

	public void setCreditDeptName(String creditDeptName) {
		this.creditDeptName = creditDeptName;
	}

	public BigDecimal getCreditFreeAmount() {
		return creditFreeAmount;
	}

	public void setCreditFreeAmount(BigDecimal creditFreeAmount) {
		this.creditFreeAmount = creditFreeAmount;
	}

	public BigDecimal getCreditUsedAmount() {
		return creditUsedAmount;
	}

	public void setCreditUsedAmount(BigDecimal creditUsedAmount) {
		this.creditUsedAmount = creditUsedAmount;
	}

	public Date getContractTransDt() {
		return contractTransDt;
	}

	public void setContractTransDt(Date contractTransDt) {
		this.contractTransDt = contractTransDt;
	}

	public Date getContractEffectiveDt() {
		return contractEffectiveDt;
	}

	public void setContractEffectiveDt(Date contractEffectiveDt) {
		this.contractEffectiveDt = contractEffectiveDt;
	}

	public Date getContractDueDt() {
		return contractDueDt;
	}

	public void setContractDueDt(Date contractDueDt) {
		this.contractDueDt = contractDueDt;
	}

	public String getVtStatusName() {
		if(PoolComm.VS_01.equals(this.getvStatus())){
			vtStatusName = "已签约";
		}else if (PoolComm.VS_02.equals(this.getvStatus())){
			vtStatusName = "已解约";
		}
		return vtStatusName;
	}

	public void setVtStatusName(String vtStatusName) {
		this.vtStatusName = vtStatusName;
	}

	public String getApproveFlag() {
		return approveFlag;
	}

	public void setApproveFlag(String approveFlag) {
		this.approveFlag = approveFlag;
	}

	public String getApproveFlagName() {
		if(PoolComm.APPROVE_01.equals(this.getApproveFlag())){
			approveFlagName="审批中";
		}else if(PoolComm.APPROVE_02.equals(this.getApproveFlag())){
			approveFlagName="签约审批拒绝";
		}else if(PoolComm.APPROVE_03.equals(this.getApproveFlag())){
			approveFlagName="签约审批通过";
		}else if(PoolComm.APPROVE_04.equals(this.getApproveFlag())){
			approveFlagName="审批中";
		}else if(PoolComm.APPROVE_05.equals(this.getApproveFlag())){
			approveFlagName="解约审批拒绝";
		}else if(PoolComm.APPROVE_06.equals(this.getApproveFlag())){
			approveFlagName="解约审批通过";
		}else if(PoolComm.APPROVE_00.equals(this.getApproveFlag())){
			approveFlagName="初始化";
		}
		return approveFlagName;
	}

	public void setApproveFlagName(String approveFlagName) {
		this.approveFlagName = approveFlagName;
	}

	public String getOpenFlagName() {
		if(PoolComm.OPEN_00.equals(this.getOpenFlag())){
			openFlagName="未签约";
		}else if(PoolComm.OPEN_01.equals(this.getOpenFlag())){
			openFlagName="已签约";
		}else if(PoolComm.OPEN_02.equals(this.getOpenFlag())){
			openFlagName="已解约";
		}
		return openFlagName;
	}

	public void setOpenFlagName(String openFlagName) {
		this.openFlagName = openFlagName;
	}

	public String getOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}

	public String getMarginAccountName() {
			return marginAccountName;
		}

		public void setMarginAccountName(String marginAccountName) {
			this.marginAccountName = marginAccountName;
		}

	public String getSigningFunction() {
		return signingFunction;
	}

	public void setSigningFunction(String signingFunction) {
		this.signingFunction = signingFunction;
	}

	public String getEbankFlag() {
		return ebankFlag;
	}

	public void setEbankFlag(String ebankFlag) {
		this.ebankFlag = ebankFlag;
	}

	public String getvStatus() {
		return vStatus;
	}

	public void setvStatus(String vStatus) {
		this.vStatus = vStatus;
	}

	public String getEffstartdateFormart() {
		effstartdateFormart = DateTimeUtil.toDateString(this.getEffstartdate());
		return effstartdateFormart;
	}

	public String  getEffenddateFormart() {
		effenddateFormart = DateTimeUtil.toDateString(this.getEffenddate());
		return effenddateFormart;
	}


	public Date getFrozenTime() {
		return frozenTime;
	}

	public void setFrozenTime(Date frozenTime) {
		this.frozenTime = frozenTime;
	}

	public String getFrozenFlag() {
		return frozenFlag;
	}

	public void setFrozenFlag(String frozenFlag) {
		this.frozenFlag = frozenFlag;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getCustOrgcode() {
		return custOrgcode;
	}

	public void setCustOrgcode(String custOrgcode) {
		this.custOrgcode = custOrgcode;
	}

	public String getIsHighRisk() {
		return isHighRisk;
	}

	public void setIsHighRisk(String isHighRisk) {
		this.isHighRisk = isHighRisk;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getContractTypeName() {
		if(PoolComm.BT_01.equals(this.getContractType())){
			contractTypeName="开通";
		}else if(PoolComm.BT_02.equals(this.getContractType())){
			contractTypeName="解约";
		}else if(PoolComm.BT_03.equals(this.getContractType())){
			contractTypeName="续约";
		}else if(PoolComm.BT_04.equals(this.getContractType())){
			contractTypeName="暂停";
		}else if(PoolComm.BT_05.equals(this.getContractType())){
			contractTypeName="重启";
		}else if(PoolComm.BT_06.equals(this.getContractType())){
			contractTypeName="冻结审批中";
		}else if(PoolComm.BT_07.equals(this.getContractType())){
			contractTypeName="解冻审批中";
		}else if(PoolComm.BT_08.equals(this.getContractType())){
			contractTypeName="";//无业务状态
		}else if(PoolComm.BT_09.equals(this.getContractType())){
			contractTypeName="解冻审批通过";
		}else if(PoolComm.BT_10.equals(this.getContractType())){
			contractTypeName="冻结审批通过";
		}else if(PoolComm.BT_11.equals(this.getContractType())){
			contractTypeName="解冻审批不通过";
		}else if(PoolComm.BT_12.equals(this.getContractType())){
			contractTypeName="冻结审批不通过";
		}
		return contractTypeName;
	}

	public void setContractTypeName(String contractTypeName) {
		this.contractTypeName = contractTypeName;
	}

	public String getElecDraftAccount() {
		return elecDraftAccount;
	}

	public void setElecDraftAccount(String elecDraftAccount) {
		this.elecDraftAccount = elecDraftAccount;
	}

	public String getOperatorName1() {
		return operatorName1;
	}

	public void setOperatorName1(String operatorName1) {
		this.operatorName1 = operatorName1;
	}

	public String getOperatorName2() {
		return operatorName2;
	}

	public void setOperatorName2(String operatorName2) {
		this.operatorName2 = operatorName2;
	}
	



	public String getOfficeNet2() {
		return officeNet2;
	}

	public void setOfficeNet2(String officeNet2) {
		this.officeNet2 = officeNet2;
	}

	public String getFrozenstateName() {
		String frozenstateName = "";
		if(PoolComm.FROZEN_STATUS_00.equals(this.getFrozenstate() )|| StringUtils.isEmpty(frozenstate)){
			frozenstateName = "未冻结";
		}else if(PoolComm.FROZEN_STATUS_01.equals(this.getFrozenstate())){
			frozenstateName = "保证金额度冻结";
		}else if(PoolComm.FROZEN_STATUS_02.equals(this.getFrozenstate())){
			frozenstateName = "票据额度冻结";
		}else if(PoolComm.FROZEN_STATUS_03.equals(this.getFrozenstate())){
			frozenstateName = "全冻结";
		}/*else if(PoolComm.FROZEN_STATUS_04.equals(this.getFrozenstate())){
			frozenstateName = "部分冻结";
		}else if(PoolComm.FROZEN_STATUS_05.equals(this.getFrozenstate())){
			frozenstateName = "部分冻结";
		}*/
		return frozenstateName;
	}


	public String getFrozenFlagName() {
		String frozenFlagName = "";
		if(PoolComm.FROZEN_STATUS_OPEN_00.equals(this.getFrozenFlag() )|| StringUtils.isEmpty(frozenFlag)){
			frozenFlagName = "";
		}else if(PoolComm.FROZEN_STATUS_OPEN_01.equals(this.getFrozenFlag())){
			frozenFlagName = "保证金额度解冻";
		}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(this.getFrozenFlag())){
			frozenFlagName = "票据额度解冻";
		}else if(PoolComm.FROZEN_STATUS_OPEN_03.equals(this.getFrozenFlag())){
			frozenFlagName = "全解冻";
		}
		return frozenFlagName;
	}

	public void setFrozenFlagName(String frozenFlagName) {
		this.frozenFlagName = frozenFlagName;
	}

	public String getGuarantNumber() {
		return guarantNumber;
	}
	
	public void setGuarantNumber(String guarantNumber) {
		this.guarantNumber = guarantNumber;
	}
	
	public String getCustlevelName() {
		String name = "";
		if ("1".equals(this.getCustlevel())) {
			name = "一等级";
		} else if ("2".equals(this.getCustlevel())) {
			name = "二等级 ";
		}else if("3".equals(this.getCustlevel())){
			name="三等级";
		}
		return name;
	}

	public void setCustlevelName(String custlevelName) {
		this.custlevelName = custlevelName;
	}

	public String getEffestateName() {

		String name = "";
		if ("SP_002".equals(this.getEffestate())) {
			name = "未开通 ";
		} else if ("SP_003".equals(this.getEffestate())) {
			name = "审批中 ";
		} else if ("SP_004".equals(this.getEffestate())) {
			name = "已开通";
		} else if ("SP_005".equals(this.getEffestate())) {
			name = "已暂停";
		} else if ("SP_006".equals(this.getEffestate())) {
			name = "已解约";
		} else if ("SP_001".equals(this.getEffestate())) {
			name = "已拒绝";
		}
		return name;
	}
	
	
	public void setEffestateName(String effestateName) {
		this.effestateName = effestateName;
	}
	
	public String getZyflagName(){
		String name = "";
		if("00".equals(this.getZyflag())){
			name="否";
		}else if("01".equals(this.getZyflag())){
			name="是";
		}
		return name;
	}
	
	public void setZyflagName(String zyflagName) {
		this.zyflagName = zyflagName;
	}
	
	public String getXyflagName(){
		String name = "";
		if("00".equals(this.getXyflag())){
			name="否";
		}else if("01".equals(this.getXyflag())){
			name="是";
		}
		return name;
	}
	
	public void setXyflagName(String xyflagName) {
		this.xyflagName = xyflagName;
	}
	/**
	 * 页面展示自定义   end
	 * 
	 */
	public String getIsGroupName() {
		String name="";
		if("0".equals(this.getIsGroup())){
			name="否";
		}else if("1".equals(this.getIsGroup())){
			name="是";
		}
		return name;
	}

	public void setIsGroupName(String isGroupName) {
		this.isGroupName = isGroupName;
	}
	
	
	
	
	

	/**
	 * 
	 * Dto,set/get方法
	 */
	public String getPoolInfoId() {
		return poolInfoId;
	}
	public void setPoolInfoId(String poolInfoId) {
		this.poolInfoId = poolInfoId;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public BigDecimal getCreditamount() {
		return creditamount;
	}
	public void setCreditamount(BigDecimal creditamount) {
		this.creditamount = creditamount;
	}
	public String getCustnumber() {
		return custnumber;
	}
	public void setCustnumber(String custnumber) {
		this.custnumber = custnumber;
	}
	public String getCustname() {
		return custname;
	}
	public void setCustname(String custname) {
		this.custname = custname;
	}
	public String getPoolAccount() {
		return poolAccount;
	}
	public void setPoolAccount(String poolAccount) {
		this.poolAccount = poolAccount;
	}
	public String getCustlevel() {
		return custlevel;
	}
	public void setCustlevel(String custlevel) {
		this.custlevel = custlevel;
	}
	public Date getEffstartdate() {
		return effstartdate;
	}
	public void setEffstartdate(Date effstartdate) {
		this.effstartdate = effstartdate;
	}
	public Date getEffenddate() {
		return effenddate;
	}
	public void setEffenddate(Date effenddate) {
		this.effenddate = effenddate;
	}
	public String getAuthperson() {
		return authperson;
	}
	public void setAuthperson(String authperson) {
		this.authperson = authperson;
	}
	public String getLicename() {
		return licename;
	}
	public void setLicename(String licename) {
		this.licename = licename;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getPoolBill() {
		return poolBill;
	}
	public void setPoolBill(String poolBill) {
		this.poolBill = poolBill;
	}
	public String getPoolFnan() {
		return poolFnan;
	}
	public void setPoolFnan(String poolFnan) {
		this.poolFnan = poolFnan;
	}
	public String getOfficeNet() {
		return officeNet;
	}
	public void setOfficeNet(String officeNet) {
		this.officeNet = officeNet;
	}
	public String getMarginAccount() {
		return marginAccount;
	}
	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}

	public BigDecimal getDiscountRatio() {
		return discountRatio;
	}

	public void setDiscountRatio(BigDecimal discountRatio) {
		this.discountRatio = discountRatio;
	}

	public String getPoolMode() {
		return poolMode;
	}
	public void setPoolMode(String poolMode) {
		this.poolMode = poolMode;
	}
	public String getIsGroup() {
		return isGroup;
	}
	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}
	public String getIsMarginGroup() {
		return isMarginGroup;
	}

	public String getZyflag() {
		return zyflag;
	}

	public void setZyflag(String zyflag) {
		this.zyflag = zyflag;
	}

	public void setIsMarginGroup(String isMarginGroup) {
		this.isMarginGroup = isMarginGroup;
	}
	public String getEffestate() {
		return effestate;
	}
	public void setEffestate(String effestate) {
		this.effestate = effestate;
	}

	public String getPoolAgreement() {
		return poolAgreement;
	}

	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}

	public BigDecimal getDisRate() {
		return disRate;
	}

	public void setDisRate(BigDecimal disRate) {
		this.disRate = disRate;
	}

	public String getPlCommId() {
		return plCommId;
	}

	public void setPlCommId(String plCommId) {
		this.plCommId = plCommId;
	}

	public String getPlUSCC() {
		return plUSCC;
	}

	public void setPlUSCC(String plUSCC) {
		this.plUSCC = plUSCC;
	}

	public String getSlivBear() {
		return slivBear;
	}

	public void setSlivBear(String slivBear) {
		this.slivBear = slivBear;
	}

	public String getFlowLoan() {
		return flowLoan;
	}

	public void setFlowLoan(String flowLoan) {
		this.flowLoan = flowLoan;
	}

	public String getShortLoan() {
		return shortLoan;
	}

	public void setShortLoan(String shortLoan) {
		this.shortLoan = shortLoan;
	}

	public String getUltraShortLoan() {
		return ultraShortLoan;
	}

	public void setUltraShortLoan(String ultraShortLoan) {
		this.ultraShortLoan = ultraShortLoan;
	}

	public BigDecimal getShortLoanRate() {
		return shortLoanRate;
	}

	public void setShortLoanRate(BigDecimal shortLoanRate) {
		this.shortLoanRate = shortLoanRate;
	}
	
	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getXyflag() {
		return xyflag;
	}

	public void setXyflag(String xyflag) {
		this.xyflag = xyflag;
	}

	public String getAuthperson2() {
		return authperson2;
	}

	public void setAuthperson2(String authperson2) {
		this.authperson2 = authperson2;
	}

	public String getLicename2() {
		return licename2;
	}

	public void setLicename2(String licename2) {
		this.licename2 = licename2;
	}

	public String getPhonenumber2() {
		return phonenumber2;
	}

	public void setPhonenumber2(String phonenumber2) {
		this.phonenumber2 = phonenumber2;
	}

	public String getPoolAccountName() {
		return poolAccountName;
	}

	public void setPoolAccountName(String poolAccountName) {
		this.poolAccountName = poolAccountName;
	}

	public String getFrozenstate() {
		return frozenstate;
	}

	public void setFrozenstate(String frozenstate) {
		this.frozenstate = frozenstate;
	}

	public String getPawnType() {
		return pawnType;
	}

	public void setPawnType(String pawnType) {
		this.pawnType = pawnType;
	}

	public String getBusiTypeName() {
		return busiTypeName;
	}

	public void setBusiTypeName(String busiTypeName) {
		this.busiTypeName = busiTypeName;
	}

	public void setEffstartdateFormart(String effstartdateFormart) {
		this.effstartdateFormart = effstartdateFormart;
	}

	public void setEffenddateFormart(String effenddateFormart) {
		this.effenddateFormart = effenddateFormart;
	}

	public void setFrozenstateName(String frozenstateName) {
		this.frozenstateName = frozenstateName;
	}


	public String getpSignType() {
		return pSignType;
	}

	public void setpSignType(String pSignType) {
		this.pSignType = pSignType;
	}

	public String getpBreakType() {
		return pBreakType;
	}

	public void setpBreakType(String pBreakType) {
		this.pBreakType = pBreakType;
	}

	public String getFeeModel() {
		return feeModel;
	}

	public void setFeeModel(String feeModel) {
		this.feeModel = feeModel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPoolModeName() {
		String name = "";
		if("01".equals(this.poolMode)){
			name="总量模式";
		}else if("02".equals(this.poolMode)){
			name="期限配比";
		}
		return name;
	}

	public void setPoolModeName(String poolModeName) {
		this.poolModeName = poolModeName;
	}
	
	


	
}
