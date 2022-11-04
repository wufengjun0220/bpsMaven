package com.mingtech.application.audit.service;

import java.util.List;

import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: h2
* @日期: 2019-07-15 上午10:28:06
* @描述: [AuditBusiTableConfigService]审批业务配置接口定义
*/
public interface AuditBusiTableConfigService extends GenericService{
	
	/**
	 * 查询审批业务表字段配置信息
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception
	 */
	public List queryBusiTabConfigs(User user, QueryBean queryBean, Page page) throws Exception;
	
	
	/**
	 * 保存审批业务表字段配置信息
	 * @param user 当前登录用户
	 * @param BusiTableConfig 业务表字段配置
	 * @return void 
	 * @throws Exception
	 */
	public void txSaveBusiTabConfig(User user,BusiTableConfig tabConfig) throws Exception;
	
	/**
	 * 根据id查询审批业务表字段配置信息
	 * @param id 审批业务表字段配置主键
	 * @return BusiTableConfig 业务表字段配置 
	 * @throws Exception
	 */
	public BusiTableConfig getBusiTabConfigById(String id) throws Exception;
	
	/**
	 * 删除审批业务表字段配置信息
	 * @param user 当前登录用户
	 * @param ids id集合
	 * @return void 
	 * @throws Exception
	 */
	public void txDeleteBusiTabConfig(User user,String ids) throws Exception;
	
	
	/**
	 * 根据产品id查询审批业务表字段配置信息
	 * @param productId 产品ID
	 * @return list 业务表字段配置 
	 * @throws Exception
	 */
	public List queryBusiTabConfigsByProductId(String productId) throws Exception; 
	
}
