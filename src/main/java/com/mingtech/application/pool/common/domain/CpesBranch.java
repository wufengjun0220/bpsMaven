package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.sql.Date;


/**
 * 票交所机构信息表--日终数据平台从电票系统抽数获得
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-31
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class CpesBranch implements java.io.Serializable {


	private static final long serialVersionUID = 3005049317563811001L;
	private Long id;                     //主键ID            
	private String memberId;             //会员代码            
	private String brchCode;             //机构代码            
	private String brchNum;              //机构编码            
	private String brchType;             //机构类别编码          
	private String transBrchClass;       //交易机构类型代码        
	private String brchFullNameZh;       //机构全称中文          
	private String brchFullNameEn;       //机构全称英文          
	private String brchShortNameZh;      //机构简称中文          
	private String brchShortNameEn;      //机构简称英文          
	private String socCode;              //统一社会信用代码        
	private String transProvinceCode;    //交易机构省份代码        
	private String transCorpClass;       //交易机构法人级别代码      
	private Date prodBeginDt;            //产品开始日期          
	private Date prodEndDt;              //产品结束日期          
	private String brchStatus;           //机构状态            
	private String transAcctStatus;      //交易账户状态          
	private String regAcctStatus;        //托管账户状态          
	private String transCorpReg;         //交易机构法定代表人或负责人   
	private BigDecimal transRegistCapital;//交易机构注册资本        
	private String address;              //地址              
	private String linkMan;              //联系人             
	private String phone;                //联系电话            
	private String custFax;              //传真              
	private String postCode;             //邮编              
	private String transBrchBankNo;      //交易机构大额行号        
	private String brchBankName;         //机构大额行名          
	private String EAgentBankNo;         //电票代理大额行号        
	private String ecdsAcctNo;           //电票代理账号          
	private String cpesBrchRemark;       //票交所机构备注         
	private Integer operTm;              //操作时间            
	private String brLv;                 //层级              
	private String operType;             //操作类型            
	private String brchInternalAcctNo;   //机构内部账户账号        
	private String brchInternalAcctName; //机构内部账户名称        
	private String memberTxAcctNo;       //会员交易账号          
	private String memberRegAcctNo;      //会员托管账号          
	private String cpesAcctNo;           //票交所资金账户账号       
	private String outAcctBankNo;        //出金账户开户行大额支付系统行号 
	private String outAcctNo;            //出金账户账号          
	private String outAcctName;          //出金账户名称          
	private String firstAdminId;         //机构管理员1用户ID      
	private String firstAdminName;       //机构管理员1用户姓名      
	private String firstAdminStatus;     //机构管理员1用户状态      
	private String secondAdminId;        //机构管理员2用户ID      
	private String secondAdminName;      //机构管理员2用户ID      
	private String secondAdminStatus;    //机构管理员2用户状态      
	private String continueBrchCode;     //承接机构代码          
	private Date createTime;             //创建时间            
	private Date updateTime;             //修改时间            
	private String custNo;               //客户号             
	private String customerName;         //同业客户名称          
	private String certifyType;          //证件类型            
	private String certifyNo;            //证件号码            
	private String isCenterBrch;         //是否总行            
	private String continueBrchName;     //承接机构全称          
	private String reserve1;             //保留字段1           
	private String reserve2;             //保留字段2           
	private String reserve3;             //保留字段3 

	public CpesBranch() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMemberId() {
		return this.memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getBrchCode() {
		return this.brchCode;
	}

	public void setBrchCode(String brchCode) {
		this.brchCode = brchCode;
	}

	public String getBrchNum() {
		return this.brchNum;
	}

	public void setBrchNum(String brchNum) {
		this.brchNum = brchNum;
	}

	public String getBrchType() {
		return this.brchType;
	}

	public void setBrchType(String brchType) {
		this.brchType = brchType;
	}

	public String getTransBrchClass() {
		return this.transBrchClass;
	}

	public void setTransBrchClass(String transBrchClass) {
		this.transBrchClass = transBrchClass;
	}

	public String getBrchFullNameZh() {
		return this.brchFullNameZh;
	}

	public void setBrchFullNameZh(String brchFullNameZh) {
		this.brchFullNameZh = brchFullNameZh;
	}

	public String getBrchFullNameEn() {
		return this.brchFullNameEn;
	}

	public void setBrchFullNameEn(String brchFullNameEn) {
		this.brchFullNameEn = brchFullNameEn;
	}

	public String getBrchShortNameZh() {
		return this.brchShortNameZh;
	}

	public void setBrchShortNameZh(String brchShortNameZh) {
		this.brchShortNameZh = brchShortNameZh;
	}

	public String getBrchShortNameEn() {
		return this.brchShortNameEn;
	}

	public void setBrchShortNameEn(String brchShortNameEn) {
		this.brchShortNameEn = brchShortNameEn;
	}

	public String getSocCode() {
		return this.socCode;
	}

	public void setSocCode(String socCode) {
		this.socCode = socCode;
	}

	public String getTransProvinceCode() {
		return this.transProvinceCode;
	}

	public void setTransProvinceCode(String transProvinceCode) {
		this.transProvinceCode = transProvinceCode;
	}

	public String getTransCorpClass() {
		return this.transCorpClass;
	}

	public void setTransCorpClass(String transCorpClass) {
		this.transCorpClass = transCorpClass;
	}

	public Date getProdBeginDt() {
		return this.prodBeginDt;
	}

	public void setProdBeginDt(Date prodBeginDt) {
		this.prodBeginDt = prodBeginDt;
	}

	public Date getProdEndDt() {
		return this.prodEndDt;
	}

	public void setProdEndDt(Date prodEndDt) {
		this.prodEndDt = prodEndDt;
	}

	public String getBrchStatus() {
		return this.brchStatus;
	}

	public void setBrchStatus(String brchStatus) {
		this.brchStatus = brchStatus;
	}

	public String getTransAcctStatus() {
		return this.transAcctStatus;
	}

	public void setTransAcctStatus(String transAcctStatus) {
		this.transAcctStatus = transAcctStatus;
	}

	public String getRegAcctStatus() {
		return this.regAcctStatus;
	}

	public void setRegAcctStatus(String regAcctStatus) {
		this.regAcctStatus = regAcctStatus;
	}

	public String getTransCorpReg() {
		return this.transCorpReg;
	}

	public void setTransCorpReg(String transCorpReg) {
		this.transCorpReg = transCorpReg;
	}

	public BigDecimal getTransRegistCapital() {
		return this.transRegistCapital;
	}

	public void setTransRegistCapital(BigDecimal transRegistCapital) {
		this.transRegistCapital = transRegistCapital;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLinkMan() {
		return this.linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCustFax() {
		return this.custFax;
	}

	public void setCustFax(String custFax) {
		this.custFax = custFax;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getTransBrchBankNo() {
		return this.transBrchBankNo;
	}

	public void setTransBrchBankNo(String transBrchBankNo) {
		this.transBrchBankNo = transBrchBankNo;
	}

	public String getBrchBankName() {
		return this.brchBankName;
	}

	public void setBrchBankName(String brchBankName) {
		this.brchBankName = brchBankName;
	}

	public String getEAgentBankNo() {
		return this.EAgentBankNo;
	}

	public void setEAgentBankNo(String EAgentBankNo) {
		this.EAgentBankNo = EAgentBankNo;
	}

	public String getEcdsAcctNo() {
		return this.ecdsAcctNo;
	}

	public void setEcdsAcctNo(String ecdsAcctNo) {
		this.ecdsAcctNo = ecdsAcctNo;
	}

	public String getCpesBrchRemark() {
		return this.cpesBrchRemark;
	}

	public void setCpesBrchRemark(String cpesBrchRemark) {
		this.cpesBrchRemark = cpesBrchRemark;
	}

	public Integer getOperTm() {
		return this.operTm;
	}

	public void setOperTm(Integer operTm) {
		this.operTm = operTm;
	}

	public String getBrLv() {
		return this.brLv;
	}

	public void setBrLv(String brLv) {
		this.brLv = brLv;
	}

	public String getOperType() {
		return this.operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getBrchInternalAcctNo() {
		return this.brchInternalAcctNo;
	}

	public void setBrchInternalAcctNo(String brchInternalAcctNo) {
		this.brchInternalAcctNo = brchInternalAcctNo;
	}

	public String getBrchInternalAcctName() {
		return this.brchInternalAcctName;
	}

	public void setBrchInternalAcctName(String brchInternalAcctName) {
		this.brchInternalAcctName = brchInternalAcctName;
	}

	public String getMemberTxAcctNo() {
		return this.memberTxAcctNo;
	}

	public void setMemberTxAcctNo(String memberTxAcctNo) {
		this.memberTxAcctNo = memberTxAcctNo;
	}

	public String getMemberRegAcctNo() {
		return this.memberRegAcctNo;
	}

	public void setMemberRegAcctNo(String memberRegAcctNo) {
		this.memberRegAcctNo = memberRegAcctNo;
	}

	public String getCpesAcctNo() {
		return this.cpesAcctNo;
	}

	public void setCpesAcctNo(String cpesAcctNo) {
		this.cpesAcctNo = cpesAcctNo;
	}

	public String getOutAcctBankNo() {
		return this.outAcctBankNo;
	}

	public void setOutAcctBankNo(String outAcctBankNo) {
		this.outAcctBankNo = outAcctBankNo;
	}

	public String getOutAcctNo() {
		return this.outAcctNo;
	}

	public void setOutAcctNo(String outAcctNo) {
		this.outAcctNo = outAcctNo;
	}

	public String getOutAcctName() {
		return this.outAcctName;
	}

	public void setOutAcctName(String outAcctName) {
		this.outAcctName = outAcctName;
	}

	public String getFirstAdminId() {
		return this.firstAdminId;
	}

	public void setFirstAdminId(String firstAdminId) {
		this.firstAdminId = firstAdminId;
	}

	public String getFirstAdminName() {
		return this.firstAdminName;
	}

	public void setFirstAdminName(String firstAdminName) {
		this.firstAdminName = firstAdminName;
	}

	public String getFirstAdminStatus() {
		return this.firstAdminStatus;
	}

	public void setFirstAdminStatus(String firstAdminStatus) {
		this.firstAdminStatus = firstAdminStatus;
	}

	public String getSecondAdminId() {
		return this.secondAdminId;
	}

	public void setSecondAdminId(String secondAdminId) {
		this.secondAdminId = secondAdminId;
	}

	public String getSecondAdminName() {
		return this.secondAdminName;
	}

	public void setSecondAdminName(String secondAdminName) {
		this.secondAdminName = secondAdminName;
	}

	public String getSecondAdminStatus() {
		return this.secondAdminStatus;
	}

	public void setSecondAdminStatus(String secondAdminStatus) {
		this.secondAdminStatus = secondAdminStatus;
	}

	public String getContinueBrchCode() {
		return this.continueBrchCode;
	}

	public void setContinueBrchCode(String continueBrchCode) {
		this.continueBrchCode = continueBrchCode;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCertifyType() {
		return this.certifyType;
	}

	public void setCertifyType(String certifyType) {
		this.certifyType = certifyType;
	}

	public String getCertifyNo() {
		return this.certifyNo;
	}

	public void setCertifyNo(String certifyNo) {
		this.certifyNo = certifyNo;
	}

	public String getIsCenterBrch() {
		return this.isCenterBrch;
	}

	public void setIsCenterBrch(String isCenterBrch) {
		this.isCenterBrch = isCenterBrch;
	}

	public String getContinueBrchName() {
		return this.continueBrchName;
	}

	public void setContinueBrchName(String continueBrchName) {
		this.continueBrchName = continueBrchName;
	}

	public String getReserve1() {
		return this.reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return this.reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return this.reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

}