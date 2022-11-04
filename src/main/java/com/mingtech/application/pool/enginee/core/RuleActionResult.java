package com.mingtech.application.pool.enginee.core;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>规则执行结果</p>
 * @author Albert Li
 * @date 2017年8月15日
 * @version 1.0
 * <p>修改记录</p>
 * Albert Li   新建类    2017年8月15日
 */
public class RuleActionResult {

	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public void setResult(Map<String, Object> result) {
		this.result = result;
	}
	
	/**
	 * @param string
	 * @param v
	 */
	public void set(String string, Object v) {
		if(result == null) {
			result = new HashMap<String, Object>();
		}
		result.put(string, v);
	}

	private String ruleName;
	private Map<String, Object> result;

	
}
