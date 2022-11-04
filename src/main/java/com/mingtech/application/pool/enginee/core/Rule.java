package com.mingtech.application.pool.enginee.core;

import com.mingtech.application.pool.rule.domain.PedRule;

/**
 * <p>规则定义</p>
 * @author Albert Li
 * @date 2017年11月3日
 * @version 1.0
 * <p>修改记录</p>
 * Albert Li   新建类    2017年11月3日
 */
public class Rule extends PedRule{
	
	public Rule() {
	}

	public Rule(String name, String when, String then) {
		this.name = name;
		this.whenVal = when;
		this.thenVal = then;
	}
}
