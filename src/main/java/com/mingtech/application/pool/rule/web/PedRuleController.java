/**
 * 
 */
package com.mingtech.application.pool.rule.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.infomanage.service.impl.AccountServiceImpl;
import com.mingtech.application.pool.rule.domain.PedRule;
import com.mingtech.application.pool.rule.service.PedRuleService;
import com.mingtech.framework.common.util.StringUtil;

/**
 * @author wbliujianfei
 * 
 */
@Controller
public class PedRuleController extends BaseController {
	private static final Logger logger = Logger.getLogger(PedRuleController.class);

	@Autowired
	private PedRuleService pedRuleService;

	@RequestMapping("/toRuleList")
	public String toRuleList() {
		return "/pool/rule/listRule";
	}

	@RequestMapping("/listRuleJson")
	public void list(PedRule rule) {
		try {
			//List ruleList = pedRuleService.findPage(rule, this.getPage());
			//Map r = new HashMap();
			List ruleList = pedRuleService.findRule(rule, this.getPage());
			//List ruleList = pedRuleService.findPage(rule, this.getPage());

			String json = RESULT_EMPTY_DEFAULT;
			if (ruleList != null && ruleList.size() > 0) {
				json = convertListToJSON(ruleList);
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@RequestMapping("/toRuleEdit")
	public String toRuleEdit(PedRule rule, Model mode) {
		//if (rule != null && rule.getId()!=null) {
		if (rule != null && !StringUtil.isEmpty(rule.getId())) {
			PedRule ruleView = (PedRule) pedRuleService.load(rule.getId());
			mode.addAttribute("rule", ruleView);
		}
		return "/pool/rule/ruleEdit";
	}

	@RequestMapping("/saveRule")
	public String txSaveRule(PedRule rule, Model mode) {
		if (rule != null) {
			if (StringUtil.isEmpty(rule.getId())) {
				rule.setCreatTime(Calendar.getInstance().getTime());
				rule.setId(null);
				pedRuleService.txStore(rule);
			} else {
				rule.setUpdateTime(Calendar.getInstance().getTime());
				pedRuleService.txStore(rule);
			}
		}
//		return "redirect:/toRuleList.mvc";
		return "/pool/rule/listRule";
	}

	/**
	 * 校验新增用户的登录名称是否重复
	 */
	@RequestMapping("/verifySaveRule")
	public void verifySaveRule(PedRule rule) {
		String result = "true";
		try {
			if (pedRuleService.isRepeat(rule)) {
				result = "false";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result = "false";
		}
		this.sendJSON("{isOk:\'" + result + "\'}");
	}
	
	@RequestMapping("/delRule")
	public String delRule(String ids, Model mode) {
		if (!StringUtil.isEmpty(ids)) {
			String[] id = ids.split(",");
			PedRule tRule = null;
			List<PedRule> list = new ArrayList<PedRule>();
			for(int i=0;i<id.length;i++) {
				tRule = new PedRule();
				tRule.setId(id[i]);
				list.add(tRule);
				//pedRuleService.txDelete(tRule);
			}
			pedRuleService.txDeleteAll(list);
		}
		return "/pool/rule/listRule";
	}

}
