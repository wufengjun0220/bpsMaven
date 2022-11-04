package com.mingtech.application.audit.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.pool.base.web.BaseController;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 业务审批受理
 * @author ice
 * 描述：审批子系统-业务审批受理功能实现
 */
@Controller
public class AuditController extends BaseController {
	
	
	private static final Logger logger = Logger.getLogger(AuditController.class);
	public static final String ERR_MSG_998 = "业务处理异常";

	@Autowired
	private AuditService auditService;
	
   /**
	* 方法说明: 查询待受理审批业务信息
	* @param queryBean 查询条件通用bean
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping("/queryAuditBusinessAccepts")
	public void queryAuditBusinessAccepts(QueryBean queryBean){
		try {
			Page page=this.getPage();
			List result=auditService.queryAuditAcceptsBusiness(this.getCurrentUser(),queryBean,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	 /**
		* 方法说明: 查询历史审批业务信息
		* @param queryBean 查询条件通用bean
		* @author  ice
		* @date 2019-03-15 上午10:30:47
		*/
	@RequestMapping("/queryHistoryAuditBusinessAccepts")
	public void queryHistoryAuditBusinessAccepts(QueryBean queryBean){
		try {
			Page page=this.getPage();
			List result=auditService.queryHistoryAuditBusiness(this.getCurrentUser(),queryBean,page);
			
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}

	 /**
	* 方法说明: 根据Id查询审批信息
	* @param id 审批id
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	
	@RequestMapping("/queryAuditBusinessById")
	public void queryAuditBusinessById(String id){
		try {
			ApproveAuditDto result = auditService.queryAuditInfoById(id);
			String json = JsonUtil.fromObject(result);
			this.sendJSON(json);
			
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
		
	}

	 /**
	* 方法说明: 管理员查询审批流程
	* @param queryBean 查询条件通用bean
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping("/queryAuditProcessMonitor")
	public void queryAuditProcessMonitor(QueryBean queryBean){
		try {
			Page page=this.getPage();
			List result=auditService.queryAuditProcess(this.getCurrentUser(),queryBean,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	
	
	/**
	* 方法说明: 查询审批受理详情
	* @param id 审批受理详情ID
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping("/queryAuditAcceptsDetail")
	public void queryAuditAcceptsDetail(String id){
		try {
			ApproveAuditDto auditDto = auditService.getApproveAuditDtoById(id);
			Map dataMap  = new HashMap();
			dataMap.put("auditInf", auditDto);
			String json = JSON.toJSONString(dataMap);
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("获取审批受理申请信息失败："+ e.getMessage());
		}
	}
	
	/**
	* 方法说明: 查询审批受理业务审核路线
	* @param id 审批受理详情ID
	* @param srcBusiId 原业务ID
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping("/queryAuditBusiAuditRoute")
	public void queryAuditBusiAuditRoute(String id,String srcBusiId){
		try {
			User user = this.getCurrentUser();
			Map auditMap = auditService.queryAuditBusiAuditRoute(id,srcBusiId,user);
			String json = JSON.toJSONString(auditMap);
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询审核路线信息失败："+ e.getMessage());
		}
	}
	
	/**
	* 方法说明: 查询审批结果
	* @param srcBusiId 原业务ID
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping("/queryAuditResult")
	public void queryAuditResult(String srcBusiId){
		try {
			Page page = this.getPage();
			List result = auditService.queryAuditResultByBusiIdAndAuditId(srcBusiId,null,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询审批结果信息失败："+ e.getMessage());
		}
	}
	
	/**
	 *
	* <p>方法说明:通用审批</p>
	* @param approveId 审批受理ID
	* @param srcBusiId 原业务id
	* @param approveFlag 审批结果：0不同意、1同意
	* @param auditComment 审批意见
	* @param curAuditNodeId 当前审批节点id
	* @param nextUserId 下一岗审批人ID，多个id使用,逗号分隔
	* <p>2020/10/16 审批优化 增加审批驳回时可选择驳回节点及驳回人信息</p>
	* @param rejustNodeNum  驳回节点
	* @param rejustUserId   驳回人员
	* @throws Exception
	 */
	@RequestMapping("/commonCommitAudit")
	public void commonCommitAudit(String approveId,String srcBusiId, String approveFlag,
			String approveComment,String curAuditNodeId,String nextUserId,
			String rejustNodeNum,String rejustUserId){
		User user = this.getCurrentUser();
		try {
			String json = auditService.txCommonCommitAudit(user,approveId, approveFlag, 
					approveComment, curAuditNodeId, nextUserId,rejustNodeNum,rejustUserId);
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			logger.error("审批失败："+ e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("审批失败："+ e.getMessage());
		}
	}
	
	/**
	* 方法说明: 直接终止审批
	* @param id 审批受理id
	* @param auditComment 审批意见
	* @author  ice
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping("/stopAudit")
	public void stopAudit(String id, String auditComment){
		try {
			 auditService.txStopAudit(this.getCurrentUser(),id,auditComment);
			this.sendJSON("终止成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("审批受理申请终止失败："+ e.getMessage());
		}
	}
	
	/**
	 *
	* <p>方法名称: queryAuditTask|描述: 首页代办任务查询</p>
	* @return
	* @throws Exception
	 */
	@RequestMapping("/queryAuditTask")
	public void queryAuditTask(){
		try {
			List taskList = auditService.queryAuditTask(this.getCurrentUser());
			String json = JsonUtil.buildJson(taskList, taskList.size());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询代办任务失败："+ e.getMessage());
		}
	}
	/************************************2020/10/15**********************************************/
	/**
	 * 提交审批时 选择第一岗审批人信息
	 * @param productId 产品id
	 */
	@RequestMapping("/queryChooseSubmit")
	public void queryChooseSubmit(String productNo){
		String json = "";
		try {
			User user = this.getCurrentUser();
			List list = auditService.queryChooseSubmit(productNo, user);
			json = JsonUtil.buildJson(list, list.size());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询首岗审批人失败："+ e.getMessage());
		}
	}
	/************************************2020/10/15**********************************************/
	
	
	
}
