package com.mingtech.application.sysmanage.domain;

import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;

/**
 * 日志实体
 * 
 * @author pengdaochang
 * 
 * @hibernate.class table="t_log"
 * @hibernate.cache usage="read-write"
 */
public class Log {

	private String id; // 主键ID
	private String memberCode;//会员编码
	private String deptName; // 单位名称
	private String deptId; // 单位ID
	private String loginName; // 登录名
	private String name; // 姓名
	private String userId;//用户id
	private String operType;//操作类型:1增加、2删除、3修改、4功能访问
	private String ip; // IP地址
	private String serverIp;//服务端IP
	private String desc;//功能描述
	private String operContent; // 操作内容
	private Date operTime; // 操作时间
	
	/***以下字段只做页面显示或参数传递***/
	private String operTypeDesc;

	/**
	 * @hibernate.id generator-class="uuid.hex" type="string" length="50"
	 *               column="id"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property type="string" length="100" column="l_deptName"
	 */
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	/**
	 * @hibernate.property type="string" length="50" column="l_deptId"
	 */
	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	/**
	 * @hibernate.property type="string" length="50" column="l_loginName"
	 */
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @hibernate.property type="string" length="50" column="l_name"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property type="string" length="50" column="l_ip"
	 */
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @hibernate.property type="string" length="200" column="l_operContent"
	 * @return
	 */
	public String getOperContent() {
		return operContent;
	}

	public void setOperContent(String operContent) {
		this.operContent = operContent;
	}

	/**
	 * @hibernate.property type="timestamp" column="l_operTime"
	 * @return
	 */
	public Date getOperTime() {
		return operTime;
	}

	public void setOperTime(Date operTime) {
		this.operTime = operTime;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getOperTypeDesc() {
		operTypeDesc = DictionaryCache.getSysLogTypeMap(operType);
		return operTypeDesc;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

}
