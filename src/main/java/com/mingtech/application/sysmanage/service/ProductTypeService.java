package com.mingtech.application.sysmanage.service;

import java.util.List;

import com.mingtech.application.sysmanage.domain.ProductTypeDto;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface ProductTypeService extends GenericService{
	
	/**
	* 方法说明: 分页获取所有产品JSON串
	* @param  role 产品实体
	* @param  page 分页实体
	*
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午09:12:47
	*/
	public String getProductTypesJSON(ProductTypeDto productType, Page page) throws Exception;
	
	public String getProducts(Tree tree) throws Exception;
	/**
	 * 根据 父产品编码 获取所有子产品编码
	 * 如果pid为空，则查询所有 父id为空的产品编码
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	public List getAllChildren(String pid) throws Exception;
	/**
	 * 获取所有产品列表
	 * @return
	 * @throws Exception
	 */
	public List getAllProductList() ;
	/**
	 * 根据 父产品编码 获取所有子产品编码
	 * 如果pid为空，则查询所有 父id为空的产品编码
	 * @param pid
	 * @param sync 1同步、0异步
	 * @throws Exception
	 */
	public List getAllChildrens(String pid,String sync) throws Exception;
}