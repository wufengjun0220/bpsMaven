package com.mingtech.application.pool.query.domain;

import java.util.Date;


/**
 * 秒贴业务控制管理表
 * @author Administrator
 *
 */
public class BusiolControlConfig implements java.io.Serializable{
	
	public static final String CON_GLOBAL_SWITCH = "GLOBAL_SWITCH"; //总开关
	public static final String CON_SEC_DIS_SWITCH = "SEC_DIS_SWITCH";//秒贴开关
	public static final String CON_HOLIDAY_SWITCH = "HOLIDAY_SWITCH";//节假日开关
	public static final String CON_SEC_SINGLE_QUOTA = "SEC_SINGLE_QUOTA";//秒贴单张票限额
	public static final String CON_OL_OPENTIME = "OL_OPENTIME";//线上贴现每日开放时间
	public static final String CON_OL_ENDTIME = "OL_ENDTIME";//线上贴现每日截止时间
	public static final String CON_SEC_OPENTIME = "SEC_OPENTIME";//秒贴每日开放时间
	public static final String CON_SEC_ENDTIME = "SEC_ENDTIME";//秒贴每日截止时间
	public static final String CON_MAX_DIS_BALANCE = "MAX_DIS_BALANCE";//贴现余额上限
	public static final String CON_MAX_BALANCE_ENDDATE = "MAX_BALANCE_ENDDATE";//贴现余额上限截止日期
	public static final String CON_BASE_RATE_RAISE = "BASE_RATE_RAISE";//指导利率上调幅度
	public static final String CON_MICRO_PREFE_RATE = "MICRO_PREFE_RATE";//小微企业优惠利率
	public static final String CON_GRENN_PREFE_RATE = "GRENN_PREFE_RATE";//绿色企业优惠利率
	public static final String CON_RURAL_PREFE_RATE = "RURAL_PREFE_RATE";//三农企业优惠利率
	public static final String CON_CLOSE_INSTRUCTION = "CLOSE_INSTRUCTION";//关闭说明
	
	public static final String I9_BUSS_MODEL = "I9_BUSS_MODEL"; //i9业务模式开关
	public static final String PASS_SPPI_FLAG = "PASS_SPPI_FLAG"; //SPPI测试开关
	public static final String I9_CAL_CLASS = "I9_CAL_CLASS"; //三分类开关
	
	
	
	//速e通
	public static final String GLOBAL_SWITCH_ACPT    ="GLOBAL_SWITCH_ACPT";//票e通总开关      
	public static final String HOLIDAY_SWITCH_ACPT   ="HOLIDAY_SWITCH_ACPT";//票e通节假日开关  
	public static final String SINGLE_AMOUNT_ACPT ="SINGLE_AMOUNT_ACPT";//票e通单张票限额  
	public static final String OL_OPENTIME_ACPT      ="OL_OPENTIME_ACPT";//速e通每日开放时间
	public static final String OL_ENDTIME_ACPT       ="OL_ENDTIME_ACPT";//速e通每日截止时间
	public static final String SF_TOP_LIMIT          ="SF_TOP_LIMIT";//影像资料上限     
	public static final String BB_TOP_LIMIT          ="BB_TOP_LIMIT";//单批票据张数上限 
	public static final String AUDIT_END_TIME        ="AUDIT_END_TIME";//行内审批截止时间 
	public static final String CLOSE_INSTRUCTION_ACPT="CLOSE_INSTRUCTION_ACPT";//票e通关闭说明 
	
	public static final String STATE_OPEN ="1";//启用
	public static final String STATE_CLOSE ="0";//停用
	
	private String conCode;//码值  主键
	private String conName;//名称
	private String dataType;//数据类型
	private String conValue;//字段值
	private int order;//页面顺序
	private String state;//状态 1-启用 0-停用
	private String pconPreValue;//上一值
	private String creater;//创建人
	private String updater;//修改人
	private Date updatetime;//更新时间
	private String conDesc;//控制描述
	private String createrNo;//创建人柜员号
	private String updaterNo;//修改人柜员号
	private String busiType;//业务类型 disc 贴现 acpt 承兑
	
	public String getConCode() {
		return conCode;
	}
	public void setConCode(String conCode) {
		this.conCode = conCode;
	}
	public String getConName() {
		return conName;
	}
	public void setConName(String conName) {
		this.conName = conName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getConValue() {
		return conValue;
	}
	public void setConValue(String conValue) {
		this.conValue = conValue;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPconPreValue() {
		return pconPreValue;
	}
	public void setPconPreValue(String pconPreValue) {
		this.pconPreValue = pconPreValue;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getConDesc() {
		return conDesc;
	}
	public void setConDesc(String conDesc) {
		this.conDesc = conDesc;
	}
	public String getCreaterNo() {
		return createrNo;
	}
	public void setCreaterNo(String createrNo) {
		this.createrNo = createrNo;
	}
	public String getUpdaterNo() {
		return updaterNo;
	}
	public void setUpdaterNo(String updaterNo) {
		this.updaterNo = updaterNo;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	
}
