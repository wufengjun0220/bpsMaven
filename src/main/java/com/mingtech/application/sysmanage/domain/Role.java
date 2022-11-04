package com.mingtech.application.sysmanage.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 * 角色实体
 * @param 
 * @since  Dec 2, 2008
 * @author zhaoqian
 * @hibernate.class table="t_role"
 */
public class Role {
	private String id;						//角色ID
	private String name = "";           	//角色名称
	private String description = "";     	//角色描述
	private Date createTime;    			//创建时间
	private List resourceList = null;	 //角色所拥有的资源对象列表
	private String scope;                   //角色范围
	private BigDecimal amount;				//审批额度
	private String  productType;   				//审批业务类型
	private String code; //角色编号      A00001-管理员   A00002-查询员  A00003-审批员  B00001-行长  C00001-客户经理   D00001-综合柜员  D00002-结算授权员
	private String  memberCode;   		//票交所会员编码

	/***以下属性只作为页面显示或参数传递***/
	private String scopeDesc;//角色描述
	private List scopeList;
	/**
	 * 角色ID
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
	 * 角色名称
	 * 
	 * @hibernate.property type="string" length="100" column="r_name"
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 角色描述
	 * 
	 * @hibernate.property type="string" length="300" column="r_desc"
	 */
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 创建时间
	 * 
	 * @hibernate.property type="timestamp" column="r_time"
	 */
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * @hibernate.bag lazy="false" inverse="false" cascade="save-update" table="t_role_resource"
	 * @hibernate.key column="roleId"
	 * @hibernate.many-to-many class="com.mingtech.application.sysmanage.domain.Resource" column="resourceId"
	 */
	public List getResourceList() {
		return resourceList;
	}
	public void setResourceList(List resourceList) {
		this.resourceList = resourceList;
	}
	/**
	 * 角色范围
	 * 
	 * @hibernate.property type="string" length="50" column="r_scope"
	 */
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public BigDecimal getAmount(){
		return amount;
	}
	
	public void setAmount(BigDecimal amount){
		this.amount = amount;
	}
	
	public String getProductType(){
		return productType;
	}
	
	public void setProductType(String productType){
		this.productType = productType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}


	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getScopeDesc() {
		return scopeDesc;
	}

	public void setScopeDesc(String scopeDesc) {
		this.scopeDesc = scopeDesc;
	}

	public List getScopeList() {
		return scopeList;
	}

	public void setScopeList(List scopeList) {
		this.scopeList = scopeList;
	}
}
