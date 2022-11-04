package com.mingtech.application.pool.common.service;

import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.pool.common.domain.EbankInfoDto;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface EbankService extends GenericService{
	
	/**
	 * 查询json串,支持模糊查询
	 * @param info
	 * @param page
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public String loadEbankJson(EbankInfoDto info, User user, Page page)throws Exception ;
		
	/**
	 * 新增客户名单时使用 
	 * Params：核心客户号 
	 * result：CustomerDto
	 */
	public CustomerDto queryCustomerDtoByEbankParm(String SOrgCode, String custNum) ;
	
}
