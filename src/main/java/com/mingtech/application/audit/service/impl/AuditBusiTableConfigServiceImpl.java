package com.mingtech.application.audit.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mingtech.application.ecds.common.domain.QueryBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.audit.service.AuditBusiTableConfigService;
import com.mingtech.application.audit.service.AuditExtendService;
import com.mingtech.application.audit.service.AuditExtendServiceFactory;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: h2
* @日期: 2019-07-15 上午10:28:06
* @描述: [AuditBusiTableConfigServiceImpl]审批接口实现
*/
@Service("auditBusiTableConfigService")
public class AuditBusiTableConfigServiceImpl extends GenericServiceImpl implements AuditBusiTableConfigService{
	private Logger logger=Logger.getLogger(this.getClass());
	
	//审批扩展服务工厂
	private AuditExtendServiceFactory auditExtendServiceFactory;
	
	/**
	 * 查询审批业务表配置信息
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception
	 */
	public List queryBusiTabConfigs(User user, QueryBean queryBean, Page page) throws Exception{
		StringBuffer sb = new StringBuffer(" from BusiTableConfig as dto where 1=1 ");
		List valueList = new ArrayList(); //查询参数值
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			//产品id
			if(StringUtils.isNotBlank(queryBean.getProductId())){
				sb.append(" and dto.productId =:productId");
				keyList.add("productId");
				valueList.add(queryBean.getProductId());
			}
			
			//表名
			if(StringUtils.isNotBlank(queryBean.getTabNm())){
				sb.append(" and dto.busiTable like :busiTable");
				keyList.add("busiTable");
				valueList.add("%"+queryBean.getTabNm()+"%");
			}
			//表中文名称
			if (StringUtils.isNotBlank(queryBean.getTabCNm())) {
				sb.append(" and dto.busiTableNm like :busiTableNm ");
				keyList.add("busiTableNm");
				valueList.add("%"+queryBean.getTabCNm()+"%");
			}
		}
		
		sb.append(" order by dto.productId ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		return this.find(sb.toString(), keyArray, valueList.toArray(), page);
	}
	
	/**
	 * 保存审批业务表字段配置信息
	 * @param user 当前登录用户
	 * @param BusiTableConfig 业务表字段配置
	 * @return void 
	 * @throws Exception
	 */
	public void txSaveBusiTabConfig(User user,BusiTableConfig tabConfig) throws Exception{
		//检查业务表名、字段名称是否合法
		Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
		Matcher isNum = pattern.matcher(tabConfig.getBusiIdField());
		if (isNum.matches()) {
			throw new Exception("主键字段格式检查不通过。");
		}
		isNum = pattern.matcher(tabConfig.getBusiStatusField());
		if (isNum.matches()) {
			throw new Exception("状态字段格式检查不通过。");
		}
		try{
			StringBuffer sql = new StringBuffer("select ");
			sql.append(tabConfig.getBusiIdField()).append(",").append(tabConfig.getBusiStatusField());
			sql.append(" from ").append(tabConfig.getBusiTable()).append(" where 1=0");
			dao.SQLQuery(sql.toString());
		}catch(Exception e){
			throw new Exception("业务表名或字段设定有错误，请检查。");
		}
		if(StringUtils.isNotBlank(tabConfig.getExtendService())){
			AuditExtendService auditExtendService = auditExtendServiceFactory.getAuditExtendService(tabConfig.getExtendService().trim());
		    if(auditExtendService==null){
		    	throw new Exception("根据扩展服务名未找到对应的扩展服务，请检查。");
		    }
		}
		
		BusiTableConfig oldTabConfig = null;
		if(StringUtils.isNotBlank(tabConfig.getId())){
			oldTabConfig = this.getBusiTabConfigById(tabConfig.getId());
		}else{
			oldTabConfig = new BusiTableConfig();
		}
		oldTabConfig.setProductId(tabConfig.getProductId());
		oldTabConfig.setBusiTable(tabConfig.getBusiTable().trim());
		oldTabConfig.setBusiTableNm(tabConfig.getBusiTableNm().trim());
		oldTabConfig.setBusiIdField(tabConfig.getBusiIdField().trim());
		oldTabConfig.setBusiStatusField(tabConfig.getBusiStatusField());
		oldTabConfig.setAuditPassStatus(tabConfig.getAuditPassStatus());//审批通过状态
		oldTabConfig.setAuditRefuseStatus(tabConfig.getAuditRefuseStatus());//审批不通过状态
		oldTabConfig.setAuditRunningStatus(tabConfig.getAuditRunningStatus());//审批中状态
		oldTabConfig.setExtendService(tabConfig.getExtendService());//扩展服务名
		oldTabConfig.setSubmitNode(tabConfig.getSubmitNode());
		oldTabConfig.setRejustNode(tabConfig.getRejustNode());
		dao.store(oldTabConfig);
	}
	
	/**
	 * 根据id查询审批业务表字段配置信息
	 * @param id 审批业务表字段配置主键
	 * @return BusiTableConfig 业务表字段配置 
	 * @throws Exception
	 */
	public BusiTableConfig getBusiTabConfigById(String id) throws Exception{
		return (BusiTableConfig) dao.load(BusiTableConfig.class, id);
	}
	
	/**
	 * 删除审批业务表字段配置信息
	 * @param user 当前登录用户
	 * @param ids id集合
	 * @return void 
	 * @throws Exception
	 */
	public void txDeleteBusiTabConfig(User user,String ids) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" from BusiTableConfig as dto where dto.id in (:ids)  ");
		
		List keyList = new ArrayList(); // 要查询的字段列表
		keyList.add("ids");
		List valueList = new ArrayList(); // 要查询的值列表
		List idList = Arrays.asList(ids.split(",")); //id集合
		valueList.add(idList);
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray());
		if(!list.isEmpty()){
			dao.deleteAll(list);
		}
	}
	
	/**
	 * 根据产品id查询审批业务表字段配置信息
	 * @param productId 产品ID
	 * @return BusiTableConfig 业务表字段配置 
	 * @throws Exception
	 */
	public List queryBusiTabConfigsByProductId(String productId) throws Exception{
		StringBuffer hql = new StringBuffer("from  BusiTableConfig as dto where dto.productId=?");
		List paraValues = new ArrayList();
		paraValues.add(productId);
		return dao.find(hql.toString(), paraValues);
	}

	public Class getEntityClass() {
		return AuditBusiTableConfigServiceImpl.class;
	}

	public String getEntityName() {
		return null;
	}

	public void setAuditExtendServiceFactory(
			AuditExtendServiceFactory auditExtendServiceFactory) {
		this.auditExtendServiceFactory = auditExtendServiceFactory;
	}
	
}
