package com.mingtech.application.pool.enginee.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.enginee.core.Context;
import com.mingtech.application.pool.enginee.core.ExpressionEngine;
import com.mingtech.application.pool.enginee.core.Rule;
import com.mingtech.application.pool.enginee.core.RuleActionResult;
import com.mingtech.application.pool.enginee.core.RuleEngine;
import com.mingtech.application.pool.rule.domain.PedRule;

/**
 * <p>默认规则引擎</p>
 * @author Albert Li
 * @date 2017年11月3日
 * @version 1.0
 * <p>修改记录</p>
 * Albert Li   新建类    2017年11月3日
 */
@Service
public class DefaultRuleEngine implements RuleEngine {

	public DefaultRuleEngine() {
		this.ee = new JaninoExpressionEngine();
	}

	public void setExpressionEngine(ExpressionEngine ee) {
		this.ee = ee;
	}
	
	public void setRules(List<PedRule> rules) {
		this.rules = rules;
	}
	
	/**
	 * 预处理。可选
	 * */
	public void prepare(Context ctx) {
//		if(rules != null) {
//			for(Rule rule : rules) {
//				try {
//					ee.compile(rule.getWhen(), ctx);
//					ee.compile(rule.getThen(), ctx);
//				} catch (Exception e) {
//				}
//			}
//		}
	}

	@Override
	public List<RuleActionResult> execute(Context context) throws Exception {
		List<RuleActionResult> results = new ArrayList<RuleActionResult>();
		//遍历执行各个规则
		for(PedRule rule : rules) {
			if(when(rule.getWhenVal(), context)) {
				//then
				Object v = getThenValue(rule.getThenVal(), context);
				RuleActionResult result = new RuleActionResult();
				result.set("result", v);
				result.setRuleName(rule.getName());
				results.add(result);
			}
		}
		
		return results;
	}

	/**
	 * @param then
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	private Object getThenValue(String then, Context context) throws Exception {
		Object value = ee.eval(then, context);
		return value;
	}

	/**
	 * 判断when条件是否通过
	 * @param when
	 * @param context 
	 * @return  通过返回true，否则false
	 * @throws Exception 
	 */
	private boolean when(String when, Context context) throws Exception {
		Object value = ee.eval(when, context);
		return value.equals(Boolean.TRUE);
	}

	private List<PedRule> rules;
	private ExpressionEngine ee;
}
