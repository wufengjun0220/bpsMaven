package com.mingtech.application.runmanage.service;

import java.util.List;

import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


public interface SystemConfigService extends GenericService{
	
	/**
	* <p>方法名称: query|描述:根据类别查询配置项 </p>
	* @param type 类别
	* @param page 分页对象 可以为null
	* @return
	*/
	public List query(String type,Page page);
	/**
	 * 获取系统配置表 的item值，就是配置的值；
	 * @param type 配置标识
	 * @return
	 */
	public String getConfigItemValue(String type);
	/**
	 * 根据分类标识查询对应的系统参数配置
	 * @param type 分类标识
	 * @return
	 */
	public List<SystemConfig> queryConfigsByType(String type);
	
	/**
	 * 获取所有系统参数配置
	 * @return List 系统参数配置集合
	 */
	public List queryAllConfigs();
	
	/**
	 * 根据参数编码获取系统参数配置
	 * @return SystemConfig 系统参数配置
	 */
	public SystemConfig getSystemConfigByCode(String code);
	
	
	/**
	 * 根据配置项码。配置项描述，类型获取系统参数配置
	 * @return List 系统参数配置
	 */
	public List querySystemConfigInfo(SystemConfig systemConfig,Page page);
	

	/**
	 * 查询给配置信息是否已存在
	 * @param 配置项编码
	 * @param id 配置项id
	 */
	public SystemConfig chkConfigCodeIsExists(String code,String id) throws Exception;
	

	/**
	 * 根据配置信息ID查询详细信息
	 * 
	 * 
	 */
	public List querySystemConfigById(String id,Page page) throws Exception;
	

	/**
	 * 根据ID删除配置信息
	 * 
	 * 
	 */
	public void txDeleteSystemConfigInfo(String ids) throws Exception;
}
