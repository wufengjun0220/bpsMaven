/**
 * 
 */
package com.mingtech.application.pool.rule.domain;

import java.util.Date;

import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
 * @author wbliujianfei
 * 
 */
public class PedRule {
	private String id;
	private String busiType;
	private String ruleType;
	private String code;
	protected String name;
	protected String whenVal;
	protected String thenVal;
	private Date creatTime;
	private Date updateTime;

	public PedRule() {
	}

	public PedRule(String name, String whenVal, String thenVal) {
		this.name = name;
		this.whenVal = whenVal;
		this.thenVal = thenVal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWhenVal() {
		return whenVal;
	}

	public void setWhenVal(String whenVal) {
		this.whenVal = whenVal;
	}

	public String getThenVal() {
		return thenVal;
	}

	public void setThenVal(String thenVal) {
		this.thenVal = thenVal;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getBusiTypeName() {
		return PoolDictionaryCache.getZCTypeName(busiType);
	}
	
	public String getRuleTypeName() {
		return PoolDictionaryCache.getZCRuleName(ruleType);
	}

}
