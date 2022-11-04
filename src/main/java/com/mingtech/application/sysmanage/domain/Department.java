package com.mingtech.application.sysmanage.domain;

import java.util.List;

/**
 * 部门
 *
 * @param
 * @since Nov 24, 2008
 * @author zhaoqian
 *
 * @hibernate.class table="t_department"
 * @hibernate.cache usage="read-write"
 */
public class Department {

	private String id; // ID

	private String innerBankCode; //系统内部行号 网点号

	private String name; // 名称

	private String description; // 描述

	private int order = 0; // 序号

	private Department parent; // 父部门ID
	
	private String pid;

	private int status = 1; // 状态 0 已删除 1 启用

	private int level = 1; // 机构级别 层级 根为1 依次2、3

	private String treeCode; // ID层级字串儿，用/分隔

	private String orgCode; //组织机构代码

	private int isOrg=0; //0 部门 1 大额机构
	private String bankNumber;//大额支付系统行号

	public static int DEPT_ORG=1;//机构
	public static int DEPT_DEPARTMENT=0;//部门

	private  List agcySvcrList=null;//被承接行名称

	private String auditBankCode;//核算中心网点号

	private String isBranch = "Y";        //是否本机构(供汇总信息用) Y：是(默认值) ，N：否
	
	private Department stockDept; // 库存机构ID
	
	private Department discountDtockDept; // 贴现库存机构ID
	//private List userList;//用户列表
	private String address;//地址
	//内部使用层级编码   根机构固定：1000
	//下级  10001001、10001002...
	private String levelCode;
	private String mainBraFlg;//是否主业务机构 0 是 、 1 否（为实现多机构共用行号增加）Xu Carry add 20170413
	
	private String pjsMemberCode;//会员代码
	private String pjsBrchNo;//票交所机构号（汉口银行用于电票系统行号）
	private String pjsUserNo;//票交所交易柜员号
	private String pjsUserName;//票交所交易柜员名称
	
	
	private String departSocialCode;//社会信用代码
	private String pjsDepartName;//票交所对应的机构名称（汉口银行用于电票系统名）
	
	private String pjsRole;//票交所开通权限 0：未开通 1：全直联2：非交易直联3.交易直联
	private String pjsRoleName;
	private String cprBrchNo;//贴现权属机构号
	private String ycFlag;//在线银承开关 0关 1开
	private String ldFlag;//在线流贷开关 0关 1开
	private String TxFlag;//在线贴现开关 0关 1开
	private String txId;	//	中台贴现机构id
	
	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	private String parentName;//父类机构名称
	
	
	
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getCprBrchNo() {
		return cprBrchNo;
	}

	public void setCprBrchNo(String cprBrchNo) {
		this.cprBrchNo = cprBrchNo;
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

	public String getPjsBrchNo() {
		return pjsBrchNo;
	}

	public void setPjsBrchNo(String pjsBrchNo) {
		this.pjsBrchNo = pjsBrchNo;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAcptDept() {
		return acptDept;
	}

	public void setAcptDept(String acptDept) {
		this.acptDept = acptDept;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getCollectionAccount() {
		return collectionAccount;
	}

	public void setCollectionAccount(String collectionAccount) {
		this.collectionAccount = collectionAccount;
	}

	private String acptDept;//承兑状态，用于承兑签发时回显承兑行名称     1 独自承兑(承兑行是本行) 2 总行承兑(承兑行是总行) 3 分行承兑(承兑行是自己的上级分行)
	private String areaCode;
	private String collectionAccount;//托收账号，只有总行或者分行才需要维护
	/**
	 * 部门ID
	 *
	 * @hibernate.id generator-class="uuid" type="string" length="50"
	 *               column="id"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    /**
     * 系统内部行号
     * @hibernate.property type="string" length="100" column="d_innerBankCode"
     */
	public String getInnerBankCode() {
		return innerBankCode;
	}

	public void setInnerBankCode(String innerBankCode) {
		this.innerBankCode = innerBankCode;
	}

	/**
	 * 部门名称
	 *
	 * @hibernate.property type="string" length="100" column="d_name"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 部门描述
	 *
	 * @hibernate.property type="string" length="300" column="d_desc"
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 部门序号
	 *
	 * @hibernate.property type="int" column="d_order" length="8"
	 */
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * 父部门ID
	 *
	 * @hibernate.many-to-one column="d_pid" class="com.mingtech.application.sysmanage.domain.Department"
	 */
	public Department getParent() {
		return parent;
	}

	public void setParent(Department parent) {
		this.parent = parent;
	}

	public String getPid() {
		return parent == null ? null : parent.getId();
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * 状态
	 *
	 * @hibernate.property type="int" column="d_status" length="8"
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 机构级别
	 *
	 * @hibernate.property type="int" column="d_level" length="8"
	 */
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * ID层级字串儿，用/分隔
	 *
	 * @hibernate.property type="string" length="100" column="d_treecode"
	 */
	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	/**
	 * 是否机构
	 *
	 * @hibernate.property type="int"  column="d_isOrg"
	 */
	public int getIsOrg() {
		return isOrg;
	}

	public void setIsOrg(int isOrg) {
		this.isOrg = isOrg;
	}
	/**
	 * 行号
	 *
	 * @hibernate.property type="string" length="20" column="d_bankNumber"
	 */
	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

	/**
	 * 获取部门所在的机构
	 * @return
	 * @author:huangshiqiang@
	 * @time:May 13, 2009
	 */
	public Department getOrg() {
		Department org = this;
		while(org!=null && org.getIsOrg()!=1){
			org = org.getParent();
		}
		return org;
	}

	/**
	 * 组织机构代码
	 * @hibernate.property type="string" length="10" column="d_orgCOde"
	 */
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}


//	public List getUserList(){
//		return userList;
//	}
//
//
//	public void setUserList(List userList){
//		this.userList = userList;
//	}

	// 重写equals方法，通过ID判断两个对象是否一样
	public boolean equals(Object arg0){
		if(null == id || null == arg0){
			return false;
		}
		Department dept = (Department) arg0;
		if(this.id.equals(dept.getId()))
			return true;
		else
			return false;
	}

    /**
     * 获取被承接行名称
    * <p>方法名称: getAgcySvcrList|描述:被承接行名称 </p>
    * @return
     */
	public  List getAgcySvcrList(){
		return agcySvcrList;
	}


	public  void setAgcySvcrList(List agcySvcrList){
		this.agcySvcrList = agcySvcrList;
	}


	public String getAuditBankCode(){
		return auditBankCode;
	}


	public void setAuditBankCode(String auditBankCode){
		this.auditBankCode = auditBankCode;
	}

	public String getIsBranch() {
		return isBranch;
	}

	public void setIsBranch(String isBranch) {
		this.isBranch = isBranch;
	}

	public Department getStockDept() {
		return stockDept;
	}

	public void setStockDept(Department stockDept) {
		this.stockDept = stockDept;
	}

	public Department getDiscountDtockDept() {
		return discountDtockDept;
	}

	public void setDiscountDtockDept(Department discountDtockDept) {
		this.discountDtockDept = discountDtockDept;
	}

	public String getMainBraFlg() {
		return mainBraFlg;
	}

	public void setMainBraFlg(String mainBraFlg) {
		this.mainBraFlg = mainBraFlg;
	}


	public String getDepartSocialCode() {
		return departSocialCode;
	}

	public void setDepartSocialCode(String departSocialCode) {
		this.departSocialCode = departSocialCode;
	}

	public String getPjsDepartName() {
		return pjsDepartName;
	}

	public void setPjsDepartName(String pjsDepartName) {
		this.pjsDepartName = pjsDepartName;
	}

	public String getPjsRole() {
		return pjsRole;
	}

	public void setPjsRole(String pjsRole) {
		this.pjsRole = pjsRole;
	}

	public String getPjsRoleName() {
		if("1".equals(this.getPjsRole())){
			pjsRoleName="全直联";
		}else if("2".equals(this.getPjsRole())){
			pjsRoleName="非交易直联";
		}else if("3".equals(this.getPjsRole())){
			pjsRoleName="交易直联";
		}else {
			pjsRoleName="未开通";
		}
		return pjsRoleName;
	}

	public void setPjsRoleName(String pjsRoleName) {
		this.pjsRoleName = pjsRoleName;
	}

	public String getPjsMemberCode() {
		return pjsMemberCode;
	}

	public void setPjsMemberCode(String pjsMemberCode) {
		this.pjsMemberCode = pjsMemberCode;
	}

	public String getYcFlag() {
		return ycFlag;
	}

	public void setYcFlag(String ycFlag) {
		this.ycFlag = ycFlag;
	}

	public String getLdFlag() {
		return ldFlag;
	}

	public void setLdFlag(String ldFlag) {
		this.ldFlag = ldFlag;
	}

	public String getTxFlag() {
		return TxFlag;
	}

	public void setTxFlag(String txFlag) {
		TxFlag = txFlag;
	}
	

}
