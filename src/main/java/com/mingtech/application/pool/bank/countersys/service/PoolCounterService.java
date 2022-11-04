package com.mingtech.application.pool.bank.countersys.service;

import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.framework.core.service.GenericService;

public interface PoolCounterService extends GenericService{

	/**
	 * 通过客户号(组织机构代码证)查询保证金的相关关联协议已作废
	 * @param ORGCODE
	 * @return
	 * @throws Exception
	 */
	public boolean isUsedEdu(String ORGCODE) throws Exception;
	
	public AssetType getTypebyCode(String ORGCODE) throws Exception;
}
