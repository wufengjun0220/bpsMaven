package com.mingtech.application.audit.domain;

import java.util.Date;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: 盘古
* @日期: 2013-12-01 下午02:25:05
* @描述: [AuditRouteDto]审批路线主表
*/
public class AuditRouteDto implements java.io.Serializable{

	private String routeId;// 主键
	private String memberCode;//会员编码
	private String auditRouteName;//审核名称路线
	private String desc;//描述
	private int connThridSys;//是否对接第三方系统（默认为0）：0否、1是
	private Date updateDate;//最后更新时间
	
	/**以下字段只做参数传递**/
    private String connThridSysDesc;//是否对接第三方系统
	
	public String getAuditRouteName() {
		return auditRouteName;
	}
	public void setAuditRouteName(String auditRouteName) {
		this.auditRouteName = auditRouteName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public int getConnThridSys() {
		return connThridSys;
	}
	public void setConnThridSys(int connThridSys) {
		this.connThridSys = connThridSys;
	}
	public String getConnThridSysDesc() {
		 connThridSysDesc = (1 == connThridSys ? "是-1":"否-0");
		 return connThridSysDesc;
	}
	public void setConnThridSysDesc(String connThridSysDesc) {
		this.connThridSysDesc = connThridSysDesc;
	}

}