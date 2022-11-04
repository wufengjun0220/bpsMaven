package com.mingtech.application.pool.query.service;
import java.util.List;

import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


/**
 * 每日资产&融资业务处理服务
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-17
 * @copyright 北明明润（北京）科技有限责任公司
 */
public interface AssetCrdtDailyService extends GenericService {

	/**
	 * 每日票据池资产、融资业务快照生成 AutoAssetCrdtDailyTask：【1】每日融资业务明细生成
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-17下午2:40:55
	 */
	public void txCrdtDailyTask() throws Exception;
	
	/**
	 * 每日票据池资产、融资业务快照生成 AutoAssetCrdtDailyTask：【2】每日票据资产明细生成
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-17下午2:40:55
	 */
	public void txBillAssetDailyTask() throws Exception;
	
	/**
	 * 每日票据池资产、融资业务快照生成 AutoAssetCrdtDailyTask：【3】每日保证金资产资产明细生成
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-17下午2:40:55
	 */
	public void txBailAssetDailyTask() throws Exception;
	
	/**
	 * 每日票据池资产、融资业务快照生成 AutoAssetCrdtDailyTask：【4】每日资产/融资业务按票据池客户生成
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-17下午2:40:55
	 */
	public void txAssetCrdtDailyTask() throws Exception;
	

	
	/**
	 * 每日票据池资产、融资业务快照生成 AutoAssetCrdtDailyTask：【5】每日票据池资产、融资业务快照生成批次号处理
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-17下午2:40:55
	 */
	public void txAssetCrdtDailyBatchNoTask() throws Exception;
	/**
     * 查询单笔借据详细信息
     * 
     * @param queryBean
     * @return
     * @throws Exception
     */
	public List loadPedCreditDetailList(CommonQueryBean queryBean) throws Exception;
	/**
	 * 查询资产信息表协议表
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public List loadDraftPoolDetailList(CommonQueryBean queryBean) throws Exception;
	/**
	 * 查询保证金表协议表
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public List loadBailDetaillList(CommonQueryBean queryBean) throws Exception ;
	/**
	 * 查询 每日资产表汇总信息
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public List loadPedAssetDailyList(CommonQueryBean queryBean) throws Exception;
	/**
	 * 查询 每日融资业务表汇总信息
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public List loadPedCrdtDailyList(CommonQueryBean queryBean) throws Exception;
	/**
	 * 查询 每日资产表
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public List loadPedAssetDaily(CommonQueryBean queryBean,Page page) throws Exception;
	/**
	 * 查询 每日融资表
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public List loadPedCrdtDaily(CommonQueryBean queryBean) throws Exception;
	/**
	 * 查询 每日资产/融资业务汇总表
	 * 
	 * @param queryBean
	 * @return
	 * @throws Exception
	 */
	public List loadPedAssetCrdtDaily(CommonQueryBean queryBean,Page page) throws Exception ;
}
