package com.mingtech.application.pool.discount.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.TxReduceInfoBean;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;

public interface TxOnlineProtocolService {
	Map<String, Object> txQueryOnlineProtocol(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception;
	Map<String, Object> OnLineTxAgreeChangeQuery(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception;
	public Map<String, Object> billreviewPriceQuery(CenterPlatformBean centerPlatformBean,User user)throws Exception;
	public Map<String, Object> amtReviewPriceQuery(CenterPlatformBean centerPlatformBean,String pattern,User user)throws Exception;
	public Map<String, Object> TxAgreeChangeDeailsQuery(CenterPlatformBean centerPlatformBean, User user) throws Exception;
	
	/**
	 * 在线贴现业务跟踪查询
	 * */
	Map<String, Object> onlineTxBusinessQuery(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception;
	
	/**
	 * 在线贴现业务详情查询
	 * */	
	Map<String, Object> onlineTxBusinessDetail(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception;

	/**
	 * 获取审价调减信息
	 * */
	List<TxReduceInfoBean> queryReduceInfo(TxReviewPriceInfo info) throws Exception;
	
}
