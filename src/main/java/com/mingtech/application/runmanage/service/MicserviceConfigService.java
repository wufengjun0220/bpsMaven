package com.mingtech.application.runmanage.service;

import java.util.List;

import com.mingtech.application.runmanage.domain.MicserviceConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 微服务配置业务层
 * @author limaosong
 *
 */
public interface MicserviceConfigService extends GenericService {

	/**
	 * 查询系统微服务配置页面信息
	 * @param micserviceConfig 搜索参数
	 * @param page 当前页
	 * @return
	 */
	List<MicserviceConfig> queryMicserviceConfig(MicserviceConfig micserviceConfig, Page page) ;
	
	/**
	 * 编辑系统微服务配置信息
	 * @param micserviceConfig 参数
	 * @throws Exception
	 */
	void txEditMicserviceConfig(MicserviceConfig micserviceConfig) throws Exception ;
	
	/**
	 * 删除系统微服务配置信息
	 * @param id 主键
	 * @throws Exception
	 */
	void txDeleteMicserviceConfig(String id) throws Exception ;
	
	/**
	 * 分页获取当前机构及下属机构的用户
	 * @param user 当前用户
	 * @param currentUser
	 * @param page 当前页
	 * @return
	 */
	List listUserOfConfig(User user, User currentUser, Page page);
	
	/**
	 * 查询系统微服务配置列表信息
	 * @return
	 */
	public List<MicserviceConfig> queryMicserviceConfigList();
}
