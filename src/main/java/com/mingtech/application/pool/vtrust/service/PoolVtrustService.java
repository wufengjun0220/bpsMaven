package com.mingtech.application.pool.vtrust.service;

import java.util.List;

import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.framework.core.service.GenericService;

public interface PoolVtrustService extends GenericService{
	
	/**
	 * 
	 * @Description: 根据票据来源删除虚拟票据池表中的票据信息
	 * @param source 票据来源
	 * @return void  
	 * @author Ju Nana
	 * @date 2018-11-9 上午11:01:28
	 */
	public void deleteVtrustInfoBySource(String source) throws Exception;
	
	/**
	 * 虚拟票据池查询
	 * @param bpsNo 票据池编号
	 * @param vtNb票号
	 * @param vtEntpNo客户号
	 * @param vtLogo托管标志
	 * @param payType应收应付类型
	 * @return
	 * @throws Exception
	 */
	public List<PoolVtrust> queryPoolVtrust(String bpsNo,String vtNb ,String vtEntpNo ,String vtLogo ,String payType) throws Exception;
	
	/**
	 * 根据票号查询虚拟票据实体
	 * @author Ju Nana
	 * @param PoolVtrustBeanQuery
	 * @return
	 * @throws Exception
	 * @date 2019-7-7上午12:57:55
	 */
	public PoolVtrust queryPoolVtrust(PoolVtrustBeanQuery queryBean) throws Exception;
	
	/**
	 * 根据票号查询虚拟票据实体
	 * @author Ju Nana
	 * @param PoolVtrustBeanQuery 
	 * @return
	 * @throws Exception
	 * @date 2019-7-7上午12:57:55
	 */
	public List<PoolVtrust>queryPoolVtrustList(PoolVtrustBeanQuery queryBean) throws Exception;
}
