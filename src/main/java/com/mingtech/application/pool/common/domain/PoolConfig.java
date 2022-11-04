package com.mingtech.application.pool.common.domain;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Aug 19, 2010 3:27:38 PM
* @描述: [PoolConfig]票据池配置项
*/
public class PoolConfig  implements java.io.Serializable {
		
     /** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private String cfgId;//主键
	private String cfgName;//配置项名称
	private String cfgKey;//配置项Key
	private String cfgValue;//配置项值
	private String cfgRemark;//配置备注
	
	
	public String getCfgId() {
		return cfgId;
	}
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	public String getCfgName() {
		return cfgName;
	}
	public void setCfgName(String cfgName) {
		this.cfgName = cfgName;
	}
	public String getCfgKey() {
		return cfgKey;
	}
	public void setCfgKey(String cfgKey) {
		this.cfgKey = cfgKey;
	}
	public String getCfgValue() {
		return cfgValue;
	}
	public void setCfgValue(String cfgValue) {
		this.cfgValue = cfgValue;
	}
	public String getCfgRemark() {
		return cfgRemark;
	}
	public void setCfgRemark(String cfgRemark) {
		this.cfgRemark = cfgRemark;
	}
	
	

	/**异地顺延天数-计算预计回款日**/
	public static final String KEY_POSTPONE_NOT_SAME_CITY = "POSTPONE_NOT_SAME_CITY";
	/**同城顺延天数-计算预计回款日**/
	public static final String KEY_POSTPONE_SAME_CITY = "POSTPONE_SAME_CITY";
	/**资金在途天数-计算预计回款日**/
	public static final String KEY_POSTPONE_BANKROLL_ONWAY = "POSTPONE_BANKROLL_ONWAY";
	/**承兑行缺省级别**/
	public static final String KEY_DEFAULT_ACPTOR_LEVEL = "DEFAULT_ACPTOR_LEVEL";
	/**持票人缺省级别**/
	public static final String KEY_DEFAULT_HOLER_LEVEL = "DEFAULT_HOLER_LEVEL";
	
    
}