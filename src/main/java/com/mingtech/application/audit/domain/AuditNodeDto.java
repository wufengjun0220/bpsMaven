package com.mingtech.application.audit.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: h2
* @日期: 2019-7-2 下午02:25:05
* @描述: [AuditRouteDto]请在此简要描述类的功能
*/
public class AuditNodeDto implements java.io.Serializable{

	private String nodeId;// 主键
	private String nodeName;//岗位名称
	private String backNode;//驳回岗位
	private String nextNode;//下一岗位
	private String optRole;//执行角色
	private String nodeNum;//执行序号
	private String desc;//描述
	private String routeId;//审核路线ID
	private BigDecimal limit;//审批金额
	private String nodeLevel;//审批级别（0-本级、1-本级+上级、2-本级+上级+上级、3-总行级）
    
	
	
	private String endFilg; // 是否权限内结束
	private String mvelExpr;//MVEL动态表达式。当[MVEL动态表达式]设定不为空时，系统根据MVEL动态表达式计算下一审核节点序号。当动态表达式计算结果为-1，则结束当前审批流
	private String nodeType;//节点类型：1自动节点、0人工节点
	private String cpAuditFlag;//交易对手审批标记1是、0否

	private String conditions; //下一节点审批条件 
	private String conditionsDesc; //下一节点审批条件描述
	
	/*******以下字段只做参数传递或页面显示*******/
	private String userName;//该节点审批人
	private boolean flag;//是否是当前审批节点
	private String endFilgName; // 页面显示中文 翻译：是否权限内结束
	private String nodeTypeName;//页面显示自动节点名称
	private String cpAuditFlagName;//交易对手审批标记
	private String roleName;//角色名称
	private String tmpNodeName;//因为ant前端框架中nodeName为关键字，所以这里需要转换
	private String nodeLevelDesc;

	public String getBackNode() {
		return backNode;
	}
	public void setBackNode(String backNode) {
		this.backNode = backNode;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeNum() {
		return nodeNum;
	}
	public void setNodeNum(String nodeNum) {
		this.nodeNum = nodeNum;
	}
	public String getOptRole() {
		return optRole;
	}
	public void setOptRole(String optRole) {
		this.optRole = optRole;
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
	public BigDecimal getLimit() {
		return limit;
	}
	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}
	public String getNextNode() {
		return nextNode;
	}
	public void setNextNode(String nextNode) {
		this.nextNode = nextNode;
	}
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
 
	public String getNodeLevel(){
		return nodeLevel;
	}

	public void setNodeLevel(String nodeLevel){
		this.nodeLevel = nodeLevel;
	}

	public String getUserName(){
		return userName;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public boolean isFlag(){
		return flag;
	}

	public void setFlag(boolean flag){
		this.flag = flag;
	}
	
	public String getEndFilg() {
		return endFilg;
	}
	public void setEndFilg(String endFilg) {
		this.endFilg = endFilg;
	}
	
	public String getEndFilgName() {
		endFilgName="1".equals(endFilg)?"是-1":"否-0";
		return endFilgName;
	}
	
	public String getMvelExpr() {
		return mvelExpr;
	}
	public void setMvelExpr(String mvelExpr) {
		this.mvelExpr = mvelExpr;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getNodeTypeName() {
		nodeTypeName="1".equals(nodeType)?"自动节点-1":"人工节点-0";
		return nodeTypeName;
	}
	
	public String getCpAuditFlag() {
		return cpAuditFlag;
	}
	public void setCpAuditFlag(String cpAuditFlag) {
		this.cpAuditFlag = cpAuditFlag;
	}
	public String getCpAuditFlagName() {
		if(StringUtils.isNotBlank(cpAuditFlag) && "1".equals(cpAuditFlag)){
			cpAuditFlagName = "是-1";
		}else{
			cpAuditFlagName = "否-0";
		}
		return cpAuditFlagName;
	}
	public String getTmpNodeName() {
		tmpNodeName = nodeName;
		return tmpNodeName;
	}
	public String getNodeLevelDesc() {
		if("0".equals(nodeLevel)){
			nodeLevelDesc="本级-0";
		}else if("1".equals(nodeLevel)){
			nodeLevelDesc="本级+上级-1";
		}else if("2".equals(nodeLevel)){
			nodeLevelDesc="本级+上级+上级-2";
		}else if("3".equals(nodeLevel)){
			nodeLevelDesc="总行-3";
		}
		return nodeLevelDesc;
	}
	public String getConditions() {
		return conditions;
	}
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	public String getConditionsDesc() {
		return conditionsDesc;
	}
	public void setConditionsDesc(String conditionsDesc) {
		this.conditionsDesc = conditionsDesc;
	}
	

}