/**
 * 展示
 */
package com.mingtech.application.sysmanage.vo;

import java.util.Date;
import java.util.List;

import com.mingtech.framework.common.util.DateUtils;





public class UserShow {
	private String id; // 用户ID

	private String loginName = ""; // 用户登录名

	private String password; // 密码

	private String name; // 用户姓名

	private int status = 1; // 状态 0停用 1启用

	private Date createTime; // 创建时间

	private List roleList = null; // 用户拥有的角色列表
	private String deptId;//数据库中的 机构ID
	private String deptName;//数据库中的 机构名称
	private String newPassword;//新密码

	private String innerBankCode;//机构号

	private String email;//电子邮件
	private String telPhone;//电话

//	private List deptList;//所属部门列表
	
	private String ip;//ip地址

	private Date psswdUpdate;//密码修改时间
	
	private String custManagerId;    // 客户经理对象主键ID
	
	private String custNumber;    //客户经理编号
	
	private String custName;   //客户经理姓名
	
	private String custManagerFlag; // 是否客户经理    1 是  0 否
	private String custExecutiveFlag; // 是否业务主管    1 是  0 否
	private Date workDate;//当前工作日  无数据库字段
	private String agentBankName;//被代理行名称
	private String agentBankOrgCode;//被代理行组织机构代码
	private String acctNo="0";//账号；默认0；代理银行登录时，存代理行的账号；
	private String agentBankRole;//代理人 参与者类别；
	private String agentFlag="0";//代理标记 1代理登录用户；【有数据库字段】
	private String adminFlag;//管理员标志 1超级管理员
	
	private String pjsUserNo;//票交所交易员柜员编码
	private String pjsUserName;//票交所交易柜员名称
	
	
	public UserShow() {

	}
	
	public Date getPsswdUpdate() {
		return psswdUpdate;
	}

	public void setPsswdUpdate(Date psswdUpdate) {
		this.psswdUpdate = psswdUpdate;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}



	/**
	 * 用户ID
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
	 * 用户登录名
	 *
	 * @hibernate.property type="string" length="50" column="u_loginName"
	 */
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * 密码
	 *
	 * @hibernate.property type="string" length="50" column="u_password"
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 用户名
	 *
	 * @hibernate.property type="string" length="50" column="u_name"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 状态
	 *
	 * @hibernate.property type="int" column="u_status" length="8"
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatusStr(){
		if(this.getStatus() == 1){
			return "启用";
		}else{
			return "停用";
		}
	}

	/**
	 * 创建时间
	 *
	 * @hibernate.property type="timestamp" column="u_time"
	 */
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 角色列表
	 *
	 * @hibernate.bag lazy="true" table="t_user_role"
	 * @hibernate.key column="userId"
	 * @hibernate.many-to-many class="com.mingtech.application.sysmanage.domain.Role" column="roleId"
	 */
	public List getRoleList() {
		return roleList;
	}

	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}


	public String getNewPassword(){
		return newPassword;
	}


	public void setNewPassword(String newPassword){
		this.newPassword = newPassword;
	}


//	public List getDeptList(){
//		return deptList;
//	}
//
//
//	public void setDeptList(List deptList){
//		this.deptList = deptList;
//	}


	public String getInnerBankCode(){
		return innerBankCode;
	}


	public void setInnerBankCode(String innerBankCode){
		this.innerBankCode = innerBankCode;
	}

	/**
	 *
	 *电子邮箱
	 * @hibernate.property type="string" length="50" column="u_email"
	 */
	public String getEmail(){
		return email;
	}


	public void setEmail(String email){
		this.email = email;
	}


	/**
	 *
	 * 电话
	 * @hibernate.property type="string" length="50" column="u_telphone"
	 */
	public String getTelPhone(){
		return telPhone;
	}


	public void setTelPhone(String telPhone){
		this.telPhone = telPhone;
	}

	public String getCustManagerId() {
		return custManagerId;
	}

	public void setCustManagerId(String custManagerId) {
		this.custManagerId = custManagerId;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	
	public String getCustManagerFlag(){
		return custManagerFlag;
	}

	
	public void setCustManagerFlag(String custManagerFlag){
		this.custManagerFlag = custManagerFlag;
	}

	public String getCustExecutiveFlag() {
		return custExecutiveFlag;
	}

	public void setCustExecutiveFlag(String custExecutiveFlag) {
		this.custExecutiveFlag = custExecutiveFlag;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Date getWorkDate() {
		if(workDate==null){
			workDate = DateUtils.getWorkDayDate();
		}
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getAgentBankName() {
		return agentBankName;
	}

	public void setAgentBankName(String agentBankName) {
		this.agentBankName = agentBankName;
	}

	public String getAgentBankOrgCode() {
		return agentBankOrgCode;
	}

	public void setAgentBankOrgCode(String agentBankOrgCode) {
		this.agentBankOrgCode = agentBankOrgCode;
	}

	public String getAgentBankRole() {
		return agentBankRole;
	}

	public void setAgentBankRole(String agentBankRole) {
		this.agentBankRole = agentBankRole;
	}

	public String getAgentFlag() {
		return agentFlag;
	}

	public void setAgentFlag(String agentFlag) {
		this.agentFlag = agentFlag;
	}

	public String getAdminFlag() {
		return adminFlag;
	}

	public void setAdminFlag(String adminFlag) {
		this.adminFlag = adminFlag;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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
	
	
}
