package com.mingtech.application.runmanage.service;

import java.util.List;

import com.mingtech.application.runmanage.domain.MicserviceRoutes;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 系统微服务注册
 * @author meng
 *
 */
public interface MicserviceRoutesService extends GenericService{
	
	/**
	 * 查询微服务信息
	 */
	public List queryMicserviceConfigs()throws Exception;
	
	/**
	 * 查询所有路由配置信息
	 */
	public List queryAllMicserviceRoutes();
	
	/**
	 * 微服务注册-列表
	 */
	public List queryMicserviceRoutes(MicserviceRoutes micserviceRoutes,Page page)throws Exception;
	/**
	 * 微服务注册-新增/修改
	 */
	public void txMicserviceRoutesAdd(MicserviceRoutes micserviceRoutes)throws Exception;
	/**
	 * 微服务注册-删除
	 */
	public void txMicserviceRoutesDel(MicserviceRoutes micserviceRoutes)throws Exception;
	
	/**
	 * 根据id和ReqUrl，判断ReqUrl是否唯一,唯一返回ture
	 * @param micserviceRoutes
	 * @param user
	 * @throws Exception
	 */
	public boolean getMicserviceRoutesByReqUrl(MicserviceRoutes micserviceRoutes)throws Exception;

	
	
}
