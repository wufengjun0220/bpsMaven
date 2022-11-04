package com.mingtech.application.pool.bank.netbanksys.domain;

import java.math.BigDecimal;
import java.util.Date;

public class ProtocolAndAssetParameter {
		
	    /*PedProtocolDto参数*/
//		private String poolInfoId;// 主键id
		private String contract;// 1.担保合同号
		private BigDecimal creditamount;// 2.授信金额
		private String custnumber;// 3.客户号
		private String custname;// 4.客户名称
		private String poolAccount;// 5.客户结算账户
//		private String custlevel;// 6.客户级别
//		private Date effstartdate;// 7.协议生效日期
		private Date effenddate;// 8.协议截止日期
//		private String authperson;// 9.授权人身份证号
//		private String licename;// 10.授权人名称
//		private String phonenumber;// 11.授权人手机号
		private String assetType;// 12.资产类型
//		private String poolBill;// 13.签约功能   // 票据托管：0 否 1 是'; 属性
//		private String poolFnan;// 票据池融资：0 否 1 是'; 属性
//		private String officeNet;// 14.经办网点
		private String marginAccount;// 15.池保证金账户
//		private BigDecimal discountRatio;// 16.池打折比例
		private String poolMode;// 17.池模式
//		private String isGroup;// 18.是否集团
		private String isMarginGroup;// 19.是否保证金归集
		private String poolAgreement;//20.票据池协议号，受理成功生成新增
		
		/*资产池参数*/
//		private String apId;//主键
//		private String apName;//池名称
//		private String poolType;//池种类：ZC_01:票据池
//		private String custId;//池所属客户ID
//		private String custName;//池所属客户名称
		private String custNo;//池所属客户号，固定填写组织机构代码，最为外围系统访问的客户唯一标识
		private String custOrgcode;//客户组织机构代码
//		private Date crtTm;//创建时间
//		private String crtOptid;//创建人ID
		
		/*资产类别参数*/
		private String astType;//池资产类型：ED_10:票据池；ED_21:保证金池-活期保证金；ED_22:保证金池-定期保证金
//		private String asstName;//资产名称
		private BigDecimal crdtTotal;//衍生总额度
		private BigDecimal crdtFree;//可用额度
		private BigDecimal crdtUsed;//已用额度
//		private Date crtTm;//创建时间
		private String apId;//资产池ID
//		private String asstTabname;//资产明细表名
//		private BigDecimal crdtFrzd;//已冻结额度
		
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
		public String getPoolAccount() {
			return poolAccount;
		}
		public void setPoolAccount(String poolAccount) {
			this.poolAccount = poolAccount;
		}
		public Date getEffenddate() {
			return effenddate;
		}
		public void setEffenddate(Date effenddate) {
			this.effenddate = effenddate;
		}
		public String getAssetType() {
			return assetType;
		}
		public void setAssetType(String assetType) {
			this.assetType = assetType;
		}
		public String getMarginAccount() {
			return marginAccount;
		}
		public void setMarginAccount(String marginAccount) {
			this.marginAccount = marginAccount;
		}
		public String getPoolMode() {
			return poolMode;
		}
		public void setPoolMode(String poolMode) {
			this.poolMode = poolMode;
		}
		public String getIsMarginGroup() {
			return isMarginGroup;
		}
		public void setIsMarginGroup(String isMarginGroup) {
			this.isMarginGroup = isMarginGroup;
		}
		public String getPoolAgreement() {
			return poolAgreement;
		}
		public void setPoolAgreement(String poolAgreement) {
			this.poolAgreement = poolAgreement;
		}
		public String getCustNo() {
			return custNo;
		}
		public void setCustNo(String custNo) {
			this.custNo = custNo;
		}
		public String getCustOrgcode() {
			return custOrgcode;
		}
		public void setCustOrgcode(String custOrgcode) {
			this.custOrgcode = custOrgcode;
		}
		public String getAstType() {
			return astType;
		}
		public void setAstType(String astType) {
			this.astType = astType;
		}
		public BigDecimal getCrdtTotal() {
			return crdtTotal;
		}
		public void setCrdtTotal(BigDecimal crdtTotal) {
			this.crdtTotal = crdtTotal;
		}
		public BigDecimal getCrdtFree() {
			return crdtFree;
		}
		public void setCrdtFree(BigDecimal crdtFree) {
			this.crdtFree = crdtFree;
		}
		public BigDecimal getCrdtUsed() {
			return crdtUsed;
		}
		public void setCrdtUsed(BigDecimal crdtUsed) {
			this.crdtUsed = crdtUsed;
		}
		public String getCustname() {
			return custname;
		}
		public void setCustname(String custname) {
			this.custname = custname;
		}
		public String getApId() {
			return apId;
		}
		public void setApId(String apId) {
			this.apId = apId;
		}
		public String getContract() {
			return contract;
		}
		public void setContract(String contract) {
			this.contract = contract;
		}
		
		
		
		

}
