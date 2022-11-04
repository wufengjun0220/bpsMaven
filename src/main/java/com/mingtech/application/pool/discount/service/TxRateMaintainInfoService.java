package com.mingtech.application.pool.discount.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.IntroBillInfoBean;
import com.mingtech.application.pool.discount.domain.TxRateDetailBean;
import com.mingtech.application.pool.discount.domain.TxRateDetailBeanPO;
import com.mingtech.application.pool.discount.domain.TxRateMaintainInfo;
import com.mingtech.application.pool.discount.domain.TxReviewPriceDetail;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface TxRateMaintainInfoService extends GenericService {
	/**
	 * 查询中台有效指导、优惠利率   并统一更新处理
	 * */
	boolean queryAndUpdateRates(User user) throws Exception;
	
	/**
	 * 查询中台最优惠利率并更新处理
	 * */
	boolean queryAndUpdateBest(User user) throws Exception;
	
	/**
	 * 查询中台有效指导利率、并更新处理
	 * */	
	public boolean queryAviGuideRateAndUpdate(User user) throws Exception;
	
	/**
	 * 查询中台指导利率、并更新处理
	 * @throws Exception 
	 * */	
	public boolean queryGuideRateAndUpdate(String batchNo,User user) throws Exception;
	
	public List<IntroBillInfoBean> queryTxIntroduceInfo(TxReviewPriceInfo info) throws Exception;
	
	/**
	 * 查询中台优惠利率、并更新处理
	 * */	
	public boolean queryFavorRateAndUpdate(String batchNo,User user) throws Exception;
	
	/**
	 * 查询在线贴现利率
	 * */
	List<TxRateMaintainInfo> getTxRateMaintainInfoJSON(String queryType,TxRateMaintainInfo txRateMaintainInfo,Page page, User user) throws Exception;

	/**
	 * 查询贴现利率详情
	 * */
	List<TxRateDetailBeanPO> queryTxRateDetails(TxRateMaintainInfo txRateMaintainInfo,User user)throws Exception;
	
	/**
	 * 查询贴现利率测算详情
	 * */
	List<TxRateDetailBeanPO> queryTxRateDetails1(TxRateMaintainInfo txRateMaintainInfo,User user)throws Exception;
	
	/**
	 * 删除本地利率信息
	 * */
	boolean deleteTxRate(TxRateMaintainInfo txRateMaintainInfo)throws Exception;
	
	/**
	 * 更新本地利率信息
	 * */
	boolean updateTxRate(TxRateMaintainInfo txRateMaintainInfo)throws Exception;
	
	/**
	 * 维护本地利率信息
	 * */
	boolean maintainTxRate(TxRateMaintainInfo txRateMaintainInfo)throws Exception;
	
	boolean txRateSend(TxRateMaintainInfo txRateMaintainInfo,User user)throws Exception;
	
	
	/**
	 * 查询票据审价信息
	 * */
	List<TxReviewPriceInfo> queryTxReviewPriceInfo(CenterPlatformBean centerPlatformBean,Page page) throws Exception;

	/**
	 * 查询票据详情信息
	 * */
	TxReviewPriceInfo queryTxReviewPriceInfo(TxReviewPriceInfo info,User user) throws Exception;
	
	Map<String, Object> queryBankRoleMapping(CenterPlatformBean centerPlatformBean,Page page,User user) throws Exception;

	Map txBillIntroduceQuery(String billNo,User user,Page page) throws Exception;
	
	/**
	 * 审价分支行提交审批
	 * @param id
	 * @param user
	 * @throws Exception
	 * @author wfj
	 * @date 20220622
	 */
	public void txSubmitPriceQuery(String id , User user) throws Exception;
	
	/**
	 * 审价分支行撤销审批
	 * @param id
	 * @param user
	 * @throws Exception
	 * @author wfj
	 * @date 20220622
	 */
	public void txCancelPriceQuery(String id , User user) throws Exception;
	
	/**
	 * 利率提交审批
	 * @param id
	 * @param user
	 * @throws Exception
	 * @author wfj
	 * @date 20220627
	 */
	public void txSubmittxRate(String id , User user) throws Exception;
	
	/**
	 * 利率撤回审批
	 * @param id
	 * @param user
	 * @throws Exception
	 * @author wfj
	 * @date 20220627
	 */
	public void txCanceltxRate(String id , User user) throws Exception;
	
	/**
	 * 最优惠利率提交审批
	 * @param id
	 * @param user
	 * @throws Exception
	 * @author wfj
	 * @date 20220627
	 */
	public void txSubmitBestFavor(String id , User user) throws Exception;
	
	/**
	 * 最优惠利率撤回审批
	 * @param id
	 * @param user
	 * @throws Exception
	 * @author wfj
	 * @date 20220627
	 */
	public void txCancelBestFavor(String id , User user) throws Exception;
	
	void insertTxRateInfo(TxRateMaintainInfo Info) throws Exception;
	
	void updateTxRateInfo(TxRateMaintainInfo Info);
	
	IntroBillInfoBean queryRateByTermAndType(TxRateDetailBean txRateDetailBean,User user) throws Exception;
	
	public boolean txSaveBillOprea(TxReviewPriceInfo info) throws Exception;
	
	void deleteBillPrice(TxReviewPriceInfo info)throws Exception;
	
	String sendBillPrice(TxReviewPriceInfo info,User user)throws Exception;
	
	TxReviewPriceDetail queryReviewInfoByCustName(String custNo) throws Exception;
	
	public TxReviewPriceDetail quertTxReviewPriceDetail(String batchNo);
	
	
	public String checkAndUpdate(String id);
	
	public String checkBillInfo(String id) throws Exception;
//	public IntroBillInfoBean getRateByBankNoAndDueDate(String bankNo,String duedate,User user) throws Exception;
	
	public void updateTxReviewPriceInfo(TxReviewPriceInfo info) throws Exception;
}
