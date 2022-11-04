package com.mingtech.application.pool.bank.lprsys.service;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;


/**
 * 
 * @author wss
 * @copyright 北京明润
 * @description 
 */
@Service
public interface PoolLprService {
	/**
	 * @Title txhandlerLPS001
	 * @author wss
	 * @date 2021-7-8
	 * @Description 查询LPR利率
	 * @return boolean
	 */
	public ReturnMessageNew txhandlerLPS001(String string) throws Exception ;

}
