package com.mingtech.application.pool.enginee.core;

import java.util.List;

import com.mingtech.application.pool.rule.domain.PedRule;


public interface RuleEngine {

	/**
	 * 设置规则。
	 * @param rules 本实例要处理的规则
	 * */
	public void setRules(List<PedRule> rules);

	/**
	 * 预处理。可选
	 * */
	public void prepare(Context context);
	
	public List<RuleActionResult> execute(Context context) throws Exception;
}
