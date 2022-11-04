package com.mingtech.application.pool.assetmanage.service;

import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.assetmanage.domain.AssetTypeManage;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 资产类型管理 服务 接口
 * @author zjt
 *
 */
public interface AssetTypeManageService extends GenericService{
	
	/**
	* <p>描述: 查询所有资产类型管理信息</p>
	* @return list 资产类型管理信息
	* @throws Exception
	*/
	public List queryAllAssetTypeManages();

	/**
	 * <p>描述: 根据条件查询资产类型管理信息</p>
	 * @return list 资产类型管理信息
	 * @throws Exception
	 */
	public String queryAssetTypeManage(AssetTypeManage assetTypeManage, User user, Page page) throws Exception;
	/**
	 * <p>描述: 根据条件查询资产类型管理历史信息</p>
	 * @return list 资产类型管理信息
	 * @throws Exception
	 */
	public String queryAssetTypeManageHis(AssetTypeManage assetTypeManage, User user, Page page) throws Exception;
	
	/**
	 * 计算顺延天数
	 * @param riskLevel 风险等级
	 * @param dueDate 到期日
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-4上午11:36:42
	 */
	public long queryDelayDays(String riskLevel,Date dueDate) throws Exception;
	
}
