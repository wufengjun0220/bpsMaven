package com.mingtech.application.audit.web;

import java.util.List;

import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.pool.base.web.BaseController;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.audit.service.AuditBusiTableConfigService;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 审批业务表字段配置
 * @author H2
 * 描述：系统管理-审批业务配置
 */
@Controller
public class AuditBusiTableConfigController extends BaseController {
	
	
	private static final Logger logger = Logger.getLogger(AuditBusiTableConfigController.class);
	public static final String ERR_MSG_998 = "业务处理异常";
	
	@Autowired
	private AuditBusiTableConfigService auditBusiTableConfigService;
	
   /**
	* 方法说明: 查询审批业务表字段配置信息
	* @param queryBean 查询条件通用bean
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping("/queryAudtiBusiTabConfigs")
	public void queryAudtiBusiTabConfigs(QueryBean queryBean){
		try {
			Page page=this.getPage();
			List result=auditBusiTableConfigService.queryBusiTabConfigs(this.getCurrentUser(),queryBean,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	
	/**
	 *
	 * <p>方法说明:保存审批业务表字段配置信息</p>
	 * @return void
	 */
	@RequestMapping("/saveAuditBusiTabConfig")
	public void saveAuditBusiTabConfig(BusiTableConfig tabConfig){
		try {
			User user = this.getCurrentUser();
			auditBusiTableConfigService.txSaveBusiTabConfig(user,tabConfig);
			this.sendJSON("保存成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("保存失败："+e.getMessage());
		}
	}
	
	/**
	 *
	 * <p>方法说明:删除审批业务表字段配置信息</p>
	 * @return void
	 */
	@RequestMapping("/deleteAuditBusiTabConfig")
	public void deleteAuditBusiTabConfig(String ids){
		try {
			User user = this.getCurrentUser();
			auditBusiTableConfigService.txDeleteBusiTabConfig(user,ids);
			this.sendJSON("删除成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除失败："+e.getMessage());
		}
	}
	
}
