package com.mingtech.application.pool.discount.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;

public interface TxConfigManagerService {
	String queryCustConfig(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception;

	Map<String, Object> queryAcceptBankBlackList(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception;
	
	Map<String, Object> queryDrawerBlackList(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception;

	Map<String, String> queryTxConfig(User user) throws Exception;
}
