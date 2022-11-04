/**
 * 
 */
package com.mingtech.application.pool.rule.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingtech.application.pool.ReturnMsgBean;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.enginee.core.Context;
import com.mingtech.application.pool.enginee.core.RuleActionResult;
import com.mingtech.application.pool.enginee.impl.DefaultContext;
import com.mingtech.application.pool.enginee.impl.DefaultRuleEngine;
import com.mingtech.application.pool.rule.domain.PedRule;
import com.mingtech.application.pool.rule.service.PedRuleService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * @author wbliujianfei
 * 
 */
@Service
@Transactional
public class PedRuleServiceImpl extends GenericServiceImpl implements
		PedRuleService {

	@Autowired
	private DefaultRuleEngine engine;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;

	public boolean isRepeat(PedRule rule) throws Exception {
		List<PedRule> list = findRule(rule, null);
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<PedRule> findRule(PedRule rule, Page page) throws Exception {
		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		StringBuffer sb = new StringBuffer("from PedRule p where 1=1 ");
		if (!StringUtil.isEmpty(rule.getName())) {
			sb.append(" and p.name like :name ");
			keys.add("name");
			//values.add("%" + rule.getName() + "%");
			values.add( rule.getName());
		}
		if (!StringUtil.isEmpty(rule.getId())) {
			sb.append(" and p.id != :id ");
			keys.add("id");
			values.add(rule.getId());
		}
		sb.append(" order by busiType,ruleType ");
		List<PedRule> list = find(sb.toString(),
				(String[]) keys.toArray(new String[keys.size()]),
				values.toArray(), page);
		return list;
		// return findByExample(rule,page);
	}

	public List<RuleActionResult> findResult(String busiType, String ruleType,
			HashMap<String, Object> map) throws Exception {
		List<RuleActionResult> results = null;
		PedRule tempRule = new PedRule();
		tempRule.setBusiType(busiType);
		tempRule.setRuleType(ruleType);
		List<PedRule> list = this.findByExample(tempRule);
		// 1.设置数据源
		engine.setRules(list);
		// 2.执行查询
		Context ctx = new DefaultContext();
		// ctx.setVariable("val1", 0.6);
		// ctx.setVariable("val2", 0.3);
		// ctx.setVariable("val3", 0.1);
		if(map != null && map.size() > 0) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				ctx.setVariable(entry.getKey(), entry.getValue());
			}
		}
		engine.prepare(ctx);
		results = engine.execute(ctx);
		// 3。处理结果
		return results;
	}
	
	public List<PedRule> findResult(String busiType, String ruleType) throws Exception {
		List<PedRule> results = null;
		PedRule tempRule = new PedRule();
		tempRule.setBusiType(busiType);
		tempRule.setRuleType(ruleType);
		results = this.findByExample(tempRule);
		return results;
	}

	@Override
	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}

	@Override
	public Class<PedRule> getEntityClass() {
		return PedRule.class;
	}
	
	/**
	 * 规则执行
	 * @param product
	 * @return
	 * @throws Exception
	 */
	@Override
	public ReturnMsgBean executeDraftRole(PedProtocolDto protocol,CreditProduct product) throws Exception {
		
		ReturnMsgBean ret = new ReturnMsgBean();
		List<DraftPool> lowRiskDraftPool = new ArrayList<DraftPool>();//低风险票据信息列表
		List<DraftPool> highRiskDraftPool = new ArrayList<DraftPool>();//高风险票据信息列表
		
		lowRiskDraftPool = this.queryDraftPool(protocol, PoolComm.LOW_RISK);
		highRiskDraftPool = this.queryDraftPool(protocol, PoolComm.HIGH_RISK);
		
		ret.setLowRiskDraftPool(lowRiskDraftPool);
		ret.setHighRiskDraftPool(highRiskDraftPool);
		
		return ret;

		
	}
	private List<DraftPool> queryDraftPool(PedProtocolDto protocol,String riskLevel){		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		StringBuffer hql = new StringBuffer(" select dp from DraftPool as dp where dp.assetLimitFree>0 and dp.poolAgreement =:poolAgreement and dp.isEduExist=:isEduExist " +
				" and dp.assetStatus in(:assetStatus) ");
		
		paramName.add("poolAgreement");//票据池编号
		paramValue.add(protocol.getPoolAgreement());		
		
		paramName.add("assetStatus");//状态
		List useStates = new ArrayList();
		useStates.add(PoolComm.DS_02);//已入池
		useStates.add(PoolComm.DS_06);//托收处理中
		paramValue.add(useStates);

		paramName.add("isEduExist");
		paramValue.add(PoolComm.EDU_EXIST);
		
		hql.append(" and dp.rickLevel =:rickLevel");
		paramName.add("rickLevel");
		paramValue.add(riskLevel);	

		hql.append(" order by dp.plPaymentTm desc ");

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<DraftPool> result = this.find(hql.toString(),paramNames,paramValues);
		return result;
		
	}
	@Override
	public String executeUseOrder(CreditProduct product) throws Exception {
		/**
		 * 占用顺序
		 */
		List<PedRule> resultList = this.findResult("ZC_00", "RT_05");
		String order =  "";
		if(resultList != null && resultList.size() > 0) {
			order =  (String) resultList.get(0).getThenVal();
		}
		return order;
	}
	/**
	 * 规则执行(保证金)
	 * @param product
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<BailDetail> executeBailDetailRole(PedProtocolDto protocol)
			throws Exception {
		
		AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(protocol, PoolComm.ED_BZJ_HQ);
		if(at!=null){
			/**
			 * 按规则提取数据
			 */
			List paramName = new ArrayList();// 名称
			List paramValue = new ArrayList();// 值
			
			StringBuffer hql = new StringBuffer(" select bd from BailDetail as bd where bd.at =:at and bd.bailType=:bailType " +
					" and bd.assetStatus=:assetStatus ");
			//客户
			paramName.add("at");
			paramValue.add(at.getId());	
			//类型
			paramName.add("bailType");
			paramValue.add(PoolComm.BZJ_HQ);	
			//状态	
			paramName.add("assetStatus");
			paramValue.add(PoolComm.BAIL_STATUS_ACTIVE);	
//			hql.append(" order by dp.plPaymentTm desc ");
			String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
			Object paramValues[] = paramValue.toArray();
			List<BailDetail> result = this.find(hql.toString(),paramNames,paramValues);
			return result;
		}
		return new ArrayList<BailDetail>();
	}

}
