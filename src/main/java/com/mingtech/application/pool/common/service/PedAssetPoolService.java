/**
 * 
 */
package com.mingtech.application.pool.common.service;

import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author wbyecheng
 *
 */
public interface PedAssetPoolService extends GenericService {
	
	/**
	 * 根据协议内容查询 AssetPool
	 * @Description TODO
	 * @author Ju Nana
	 * @param protocol
	 * @return
	 * @throws Exception
	 * @date 2019-6-25下午6:19:07
	 */
	public AssetPool queryPedAssetPoolByProtocol(PedProtocolDto protocol) throws Exception;
	
	/**
	 * 根据核心客户号或客户组织机构代码查询资产池实体
	 * @param bpsNo 票据池编号
	 * @param orgCode 客户组织机构代码
	 * @param custNo 核心客户号
	 * @return AssetPool  
	 * @author Ju Nana
	 * @date 2018-12-5 下午1:43:05
	 */
	public AssetPool queryPedAssetPoolByOrgCodeOrCustNo(String bpsNo,String  orgCode,String custNo) throws Exception;
	
	/**
	 * 额度查询方法:查询整个票据池的额度
	 * @param bpsNo 票据池编号
	 * @param @throws Exception
	 * @return EduResult 额度结果集
	 * @author Ju Nana
	 * @date 2019-2-7 下午8:48:06
	 */
	public EduResult queryEduAll(String bpsNo) throws Exception;
	
	/**
	 * 查询集团子户的额度使用及产生情况
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午3:18:54
	 */
	public EduResult queryEduMember(String bpsNo,String custNo) throws Exception;
	
	/**
	 *获取资产池业务处理授权
	 *为解决同一客户资产和融资业务的并发操作，在进行资产或融资业务资金变更业务处理之前，先获授权。
	 *1、手工触发业务-授权获取失败，则直接终止本次业务操作，并给客户返回提示信息
	 *2、自动调度业务-授权获取失败，则直接终止本次业务操作，将任务进行排队处理
	 *@param apId 资产池ID
	 *@return true授权成功、false授权失败
	 */
	public boolean txGetAssetPoolTransAuthority(String apId);
	
	/**
	 *释放资产池业务处理授权，同时更是资产类型+资产池额度使用相关信息
	 *@param apId 资产池ID
	 *@param isUnlock 是否解锁，0-否 1是
	 *@return true释放授权成功、false释放授权失败
	 */
	public boolean txReleaseAssetPoolTransAuthoritySyncCredit(String apId,boolean isUnlock);
	
	/**
	 *释放资产池业务处理授权
	 *@param apId 资产池ID
	 *@return true释放授权成功、false释放授权失败
	 */
	public boolean txReleaseAssetPoolTransAuthority(String apId);
	
	/**
	 * 只做AssetPool解锁
	 * @param apId
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-27下午5:09:16
	 */
	public void txReleaseAssetPoolLock(String apId);
}
