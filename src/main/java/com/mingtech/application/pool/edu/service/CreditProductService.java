package com.mingtech.application.pool.edu.service;

import java.util.List;

import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: 张永超
 * @日期: Aug 20, 2010 10:16:26 AM
 * @描述: [CreditProductService]信贷资产业务接口类
 */
public interface CreditProductService extends GenericService {
	
		
	/**
	 * 保贴人的保贴额度统计明细
	 * 
	 * @param query
	 *            查询条件
	 * @return
	 * @throws Exception
	 */
	public List loadPosterCountDetail(String acceptorOrg,Page page) throws Exception;
	
	
	/**
	 * 票据池占用保贴额度查询明细
	 * 
	 * @param query
	 *            查询条件
	 * @return
	 * @throws Exception
	 */
	public List loadPoolPasteDetail(String bpsNo,Page page) throws Exception;
	

}
