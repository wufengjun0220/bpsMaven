/**
 * 
 */
package com.mingtech.application.pool.common.service;

import java.util.List;

import com.mingtech.application.pool.common.domain.AllAssetTypeResult;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PlFeeList;
import com.mingtech.application.pool.common.domain.QueryPlFeeListBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author wbyecheng
 *
 */
public interface PedAssetTypeService extends GenericService {
	/**
	 * 通过AssetPool获取AssetType
	 * @param ap
	 * @return
	 * @throws Exception
	 */
	public List<AssetType> queryPedAssetTypeByAssetPool(AssetPool ap) throws Exception;
	/**
	 * 通过AssetPool和池类型,获取AssetType
	 * @param ap
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<AssetType> queryPedAssetTypeByAssetPool(AssetPool ap,String type) throws Exception;
	
	/**
	 * 通过PedProtocolDto protocol和池类型,获取AssetType
	 * @param ap
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public AssetType queryPedAssetTypeByProtocol(PedProtocolDto protocol,String type) throws Exception;
	/**
	 * 通过commId和池类型,获取AssetType
	 * @param type
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 */
	public AssetType queryPedAssetTypeByProtocol(String type,String bpsNo) throws Exception;
	
	/**
	 * 根据客户信息查询该客户担保合同的可用已用额度信息
	 * @param protocol
	 * @return PedProtocolDto  
	 * @author Ju Nana
	 * @date 2019-3-13 下午9:19:53
	 */
	public PedProtocolDto queryPedAssetTypeReturnCredit(PedProtocolDto protocol) throws Exception;
	
	/* * 查询服务费
	 * @param ap
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<PlFeeList> queryserviceCharge(String bpsNo,String bpsName,String feeType,User user,Page page) throws Exception;
	/* * 查询服务费逐笔明细
	 * @param ap
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<QueryPlFeeListBean> queryserviceChargeDetail(String feeBatchNo,Page page) throws Exception;
	/* * 票据池服务费收费标准
	 * @param ap
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<PlFeeList> queryserviceChargeManage(Page page) throws Exception;	
	
	/**
	 * 查询所有AssetType信息
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-2下午7:50:41
	 */
	public AllAssetTypeResult queryAllAssetType(String bpsNo) throws Exception;
	
	
	/**
	 * 出池申请额度校验
	 * @Description TODO
	 * @author Ju Nana
	 * @param billNos
	 * @param dto
	 * @return 
	 * @throws Exception
	 * @date 2019-7-4上午10:34:39
	 */
	public AllAssetTypeResult outApplylimitCheck(List<String> billNos,PedProtocolDto dto)throws Exception;
	
}
