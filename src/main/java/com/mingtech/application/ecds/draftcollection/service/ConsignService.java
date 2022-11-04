package com.mingtech.application.ecds.draftcollection.service;

import java.util.List;

import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.infomanage.domain.AccountDto;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者：liuweijun
 * @日期：Jun 2, 2009 11:56:28 AM 
 * @描述：[ConsignService]委托收款接口
 */
public interface ConsignService extends GenericService {
	
	/**
	 * 获取到期票据，发起提示付款申请
	 * @throws Exception
	 */
	public void autoCollectionSendNO1Task() throws Exception;
	
	/**
	 * 根据状态、行号、账号查询提示付款信息
	 *  @param status 票据状态
	 *  @param account 客户账户
	 *  @param bankNums 票据持有人大额行号
	 * @throws Exception
	 */
	public List getCollectionSendByStatus(List status,String bankNums) throws Exception;
	
	
	/**
	* <p>方法名称: loadByBillNo|描述: 根据票据号码加子票区间查询票据信息对象</p>
	* @param billNo 票据号码
	* @param startNo 子票区间起
	* @param endNo	子票区间止
	* 融合改造修改
	* @return
	*/
	public BtBillInfo loadByBillNo(String billNo,String startNo, String endNo) throws Exception;
	
	
	/**
	 * 界面发送提示付款申请
	 * @return
	 * @throws Exception
	 * @author wufengjun
	 * @date 2019-2-28
	 */
	public void sendCollection(String ids) throws Exception;
	
	/**
	 * 根据查询条件查询托收信息
	 * @param billNo
	 * @param id
	 * @return
	 * @throws Exception
	 * @author wufengjun
	 * @date 2019-3-1
	 */
	public List<CollectionSendDto> queryCollectionSendDto(QueryBean queryBean ,List bankNums, User user, Page page) throws Exception;
	
	/**
	 * 获取已到期且在入池状态票据信息
	 * @return
	 * @throws Exception
	 * @author wufengjun
	 * @date 2019-03-01
	 */
	public List  getCollSendForBean(QueryBean queryBean, User user, Page page) throws Exception;
	
	/**
	 *根据票号获得提示付款对象
	 * @param billNo
	 * @param beginRangeNo 子票区间起
	 * @param endRangeNo 子票区间止
	 * @return
	 * @throws Exception
	 * @author wufengjun
	 * @date 
	 * 融合改造修改
	 */
	public CollectionSendDto loadSendDtoByBillNo(String billNo,String beginRangeNo, String endRangeNo) throws Exception;
	
	/**
	 * 根据状态和标志查询可做贴现的票据
	 * @param plStatus
	 * @param TXFlag
	 * @return
	 * @throws Exception
	 * @author wufengjun
	 */
	public DraftPool getDraftPoolByParam(String billNo, String TXFlag) throws Exception;
	

	/**
	 * 查询纸票托收在途或十天内到期的票据
	 * @return
	 * @throws Exception
	 * @author wufengjun
	 * @Data 2019-06-18 上午11:10
	 */
	public List<DraftPool> getPaperCollection() throws Exception;

	/**
	 * 用分装的查询条件查询池对象
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @author wufengjun
	 * @Data 2019-08-21 下午14:13
	 */
	public List<DraftPool> queryDraftByBean(PoolQueryBean queryBean) throws  Exception;
	
	/**
	 * 根据子票区间查询原始托收票据信息
	 * @param startBillNo
	 * @param endBillNo
	 * @returnCollectionSendDto
	 * @throws Exception
	 * @author wufengjun
	 * @Data 2022-09-01 
	 */
	public CollectionSendDto queryCollectionSend(String billNo, String startBillNo, String endBillNo) throws  Exception;
	
	/**
	 * 	根据通知数据查询提示付款数据
	 * @param startBillNo
	 * @param endBillNo
	 * @param status
	 * @param billNo
	 * @returnCollectionSendDto
	 * @throws Exception
	 */
	public CollectionSendDto queryCollectionSendByStatus(String billNo, String startBillNo, String endBillNo) throws Exception;


}
