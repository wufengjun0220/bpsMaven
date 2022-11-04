package com.mingtech.application.audit.service;

import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Jun 15, 2019 10:03:35 AM
 * @描述: [AuditExtendService]审批结果处理扩展服务
 */
public interface AuditExtendService{
	/**
	 * 审批结果处理
	 * @param user 当前登录用户
	 * @param auditId 审批受理ID
	 * @param busiId  原业务ID
	 * @param auditResult审批结果
	 * 01:同意，流程正常结束
	 * 02:同意，流程 继续流转到 下一岗审批人；
	 * 03:拒绝，成功找到配置的拒绝岗  审批人，并将流程流转到 指定的拒绝岗；
	 * 04:拒绝，直接将审批流程结束，需要申请岗重新发起审批流程；
	 * @return void
	 */
	  public String txDealWithAuditResult(User user,String auditId,String busiId,String status,BusiTableConfig busiTabConfig) throws Exception;
	  
}
