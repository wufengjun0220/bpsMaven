/**
 * 
 */
package com.mingtech.application.pool.base.service;

import java.util.List;

import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.DraftQueryBeanQuery;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author wbyecheng
 * 
 * 资产查询服务
 *
 */
public interface PoolQueryService extends GenericService {
	/**
	 * 根据票据池编号查询  池资产明细
	 * @param plCommId
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String findCustAseetPoolDetail(String bpsNo, Page page) throws Exception ;

	/**
	 * 集团企业额度  查询
	 * @param selRang
	 * @param custName
	 * @param plCommId
	 * @param currentUser
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String findCustEduQuotaJson(String poolAgreement,String isGroup,String poolName,
		User currentUser, Page page)throws Exception;
	
	
	/**
	 * 根据票据池编号查询资产表
	 * @author Ju Nana
	 * @param bpsNo 票据池编号
	 * @return
	 * @throws Exception
	 * @date 2019-8-24上午10:00:04
	 */
	public AssetPool queryAssetPoolByBpsNo(String bpsNo) throws Exception;
	
	/**
	 * 查客户的池资产明细：票据，大额存单
	 * @param plCommId
	 * @param astType
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String findAssetDetailList(String plCommId, String astType, Page page) throws Exception;
	
	/**
	 *  查客户的池资产明细:活期保证金
	 * @param plCommId
	 * @param astType
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String findAssetBailDetailList(String plCommId, String astType, Page page) throws Exception;
	
	/**
	 * 查询入池批次信息wfj 2018/10/11
	 * @param bean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List toPoolAllInQuery(DraftQueryBean bean,User user,Page page) throws Exception;
	
	/**
	 * 查询出池批次信息wfj 2018/10/11
	 * @param bean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List toPoolAllOutQuery(DraftQueryBean bean,User user,Page page) throws Exception;
	
	/**
	 * 查询出池签收异常信息
	 * @param bean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List toPoolOutSignQuery(DraftPoolOut bean,User user,Page page) throws Exception;
	
	/**
	 * 查询出池到期异常信息
	 * @param bean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List toPoolOutExpireQuery(CollectionSendDto bean, User user, Page page) throws Exception;
	
	/**
	 * 查询入池批次信息wfj 2018/10/11
	 * @param bean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List InventedBillQuery(DraftQueryBean bean,User user,Page page) throws Exception;

	/**
	 * 根据票号,加子票区间查对象
	 * @param billNo
	 * @param beginRangeNo
	 * @param endRangeNo
	 * @return
	 * @throws Exception
	 */
	public PoolBillInfo queryObj(String billNo,String beginRangeNo, String endRangeNo) throws Exception;

	/**
	 * @param 票据入池查询
	 * @param gcj 20210701
	 * @return
	 * @throws Exception
	 */
	public QueryResult toPoolInByQueryBean(DraftQueryBean bean, Page page) throws Exception;
	
	/**
	 * @param 票据出入池查询
	 * @param gcj 20210701
	 * @return
	 * @throws Exception
	 */
	public QueryResult toPoolByQueryBean(DraftQueryBean bean, Page page) throws Exception;
}
