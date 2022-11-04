/**
 * 
 */
package com.mingtech.application.pool.rule.service;

import java.util.HashMap;
import java.util.List;

import com.mingtech.application.pool.ReturnMsgBean;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.enginee.core.RuleActionResult;
import com.mingtech.application.pool.rule.domain.PedRule;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;



/**
 * @author wbliujianfei
 * 
 */
public interface PedRuleService extends GenericService{
	
	/**
	 * 方法说明: 判断当前规则是否有重复
	 * @param rule 要判断的规则对象
	 * @return 判断结果
	 */
	public boolean isRepeat(PedRule rule) throws Exception;
	
	/**
	 * @param rule
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PedRule> findRule(PedRule rule,Page page) throws Exception;
	
	/**
	 * @param busiType
	 * @param ruleType
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<RuleActionResult> findResult(String busiType, String ruleType, HashMap<String,Object> map) throws Exception;
	
	
	/**
	 * @param busiType
	 * @param ruleType
	 * @return
	 * @throws Exception
	 */
	public List<PedRule> findResult(String busiType, String ruleType) throws Exception;
	/**
	 * 规则执行(票据)
	 * @param product
	 * @return
	 * @throws Exception
	 */
	public ReturnMsgBean executeDraftRole(PedProtocolDto protocol,CreditProduct product) throws Exception;
	/**
	 * 规则执行(保证金)
	 * @param product
	 * @return
	 * @throws Exception
	 */
	public List<BailDetail> executeBailDetailRole(PedProtocolDto protocol) throws Exception;
	/**
	 * 规则执行（占用顺序）
	 * @param product
	 * @return
	 * @throws Exception
	 */
	public String executeUseOrder(CreditProduct product) throws Exception;
}
