/**
 * Resource Service
 */
package com.mingtech.application.sysmanage.service;

import java.util.List;

import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author huboA
 * 
 */
public interface ResourceService extends GenericService {
	/**
	* 方法说明: 分页获取所有资源JSON串
	* @param  role 资源实体
	* @param  page 分页实体
	*
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午09:12:47
	*/
	public String getResourcesJSON(Resource resource, Page page) throws Exception;
	/**
	 * 分页返回所有资源
	 * @param roleManager
	 * @return
	 * @throws Exception
	 */
	public List getResources(Resource resource, Page page) throws Exception;
	/**
	 * 根据tree的ID获取子资源
	 * 
	 * @param
	 * @since Dec 5, 2008
	 * @author zhaoqian
	 * @param tree
	 * @return
	 * @throws Exception
	 */
	public String getResources(Tree tree) throws Exception;

	/**
	 * 根据父亲节点获得孩子List
	 * 
	 * @param
	 * @since Dec 5, 2008
	 * @author zhaoqian
	 * @param resource
	 * @return
	 * @throws Exception
	 */
	public List getChildren(Resource resource) throws Exception;

	
	/**
	 * 根据父id查找子资源
	 * 
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	public List getChildren(String pid) throws Exception;
	/**
	 * 根据id获得Resource
	 * 
	 * @param
	 * @param id
	 * @return
	 */
	public Resource getResourceById(String id) throws Exception;
	
	/**查找主页菜单
	 * @return
	 */
	public List getShowResource();
	
	/**
	 * 根据角色ID获取该角色分配的资源信息
	 * @param roleIds 角色id
	 * @return list 资源信息
	 */
	public List queryResourceListByRole(List roleIds);
	
	
}
