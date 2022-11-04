/**
 * 
 */
package com.mingtech.application.pool.draft.service;

import java.util.List;

import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.base.service.PoolQueryService;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.CorePdraftColl;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.CreditProductQueryBean;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.manage.domain.QueryPedListBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;

/**
 * @author wbyecheng
 * 
 *         票据资产业务查询服务
 *
 */
public interface DraftPoolQueryService extends PoolQueryService {
	
	/**
	 * 池票据查询
	 * @param bean
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List queryDraftPool(DraftQueryBean bean, User user, Page page) throws Exception;

	/**
	 * 
	 * 批量导出 池票据查询 按户查询 批量导出
	 * 
	 * @param res
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List findPoolDraftExpt(List res, Page page) throws Exception;


	/**
	 * 信贷产品发生查询 查询明细
	 * 
	 * @return
	 * @throws Exception
	 */
	public String findCrdtProductList(CreditProductQueryBean bean, Page page, User user) throws Exception;

	/**
	 * 信贷产品发生查询 明细批量导出
	 * 
	 * @return
	 * @throws Exception
	 */
	public List findCrdtProductListExpt(CreditProductQueryBean bean, Page page,
			User user) throws Exception;


	/**
	 * 
	 * 虚拟票据查询 批量导出
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findPoolDraftExpt1(List res, Page page) throws Exception;

	/**
	 * 
	 * 出入池票据查询 批量导出
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findPoolAllExpt(List res, Page page) throws Exception;

	/**
	 * 
	 * 批量导出 出入池票据跟踪查询 批量导出
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findPoolTraceAllExpt(List res, Page page) throws Exception;

	/**
	 * 企业额度批量导出
	 * 
	 * @param poolAgreement
	 * @param page
	 * @return
	 */
	public List findCustEduJsonExpt(String poolAgreement,String isGroup, User currentUser, Page page) throws Exception;

	/**
	 * 根据票号子票区间查询票据基本信息对象
	 * @param plDraftNb 票号
	 * @param beginRangeNo 子票区间起
	 * @param endRangeNo 子票区间止
	 * @return
	 */
	public PoolBillInfo loadByBillNo(String plDraftNb,String beginRangeNo,String endRangeNo) throws Exception;


	/**
	 * 根据queryBean查询大票表对象
	 * 
	 * @param queryBean
	 * @return PoolBillInfo
	 * @author Ju Nana
	 * @date 2019-2-28 上午10:45:00
	 */
	public List<PoolBillInfo> queryPoolBillInfoByPram(PoolQueryBean queryBean) throws Exception;

	/**
	 * 根据借据从核心同步借据余额，并释放已还部分额度
	 * @param detail
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-27下午9:20:15
	 */
	public PedCreditDetail txUpdateLoanByCoreforQuery(PedCreditDetail detail) throws Exception;
	
	/**
	 * 删除账务管家表数据
	 * 
	 * @param @throws Exception
	 * @return void
	 * @param bpsNo
	 * @param custNo
	 * @throws @author Ju Nana
	 * @date 2019-3-25 下午11:36:20
	 */
	public void txDeleteDraftAccountManagement(String bpsNo,String custNo) throws Exception;

	/**
	 * 删除账务管家表去除查询出的重复数据
	 * @return void
	 * @throws @author Ju Nana
	 * @date 2019-8-26 下午19:36:20
	 */
	public void txDelectRepeatDraftAccountManagement() throws Exception;
	
	/**
	 * 根据参数查询每日对账批次表
	 * @Description TODO
	 * @author Ju Nana
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @date 2019-6-13上午9:25:55
	 */
	public List<PedCheckBatch> queryCheckBatchByPram(PoolQueryBean queryBean) throws Exception;
	
	/**
	 * 通过批次号查询网银纸票出入池批次信息
	 * @param queryBean
	 * @return
	 * @author wu fengjun
	 * @throws Exception
	 * @date 2019-6-21 上午10:48
	 */
	public List<PlPdraftBatch> queryPlPdraftBatchByBatch(PoolQueryBean queryBean) throws Exception;

	/**
	 * @Description 通过批次号查询网银纸票出入池批次信息
	 * @param queryBean
	 * @param page
	 * @param @throws Exception
	 * @return QueryResult
	 * @author xieCheng
	 */
	public QueryResult queryPlPdraftBatchByBatch(PoolQueryBean queryBean, Page page) throws Exception;

	/**
	 * @Description 根据queryBean查询大票表对象
	 * @param queryBean
	 * @param page
	 * @return QueryResult
	 * @author xieCheng
	 */
	public QueryResult queryPoolBillInfoByPram(PoolQueryBean queryBean ,Page page) throws Exception;
	
	/**
	 * 成员单位额度明细查询
	 * @param bean
	 * @param page
	 * @return
	 * @throws Exception
	 * @author liu xiaodong
	 */
	public List<QueryPedListBean> queryPedListBeanDetail(QueryPedListBean bean,Page page) throws Exception;

	/**
	 * 删除账务管家表自动任务的跑出来持有数据数据
	 *
	 * @param @throws Exception
	 * @return void
	 * @param status
	 * @param custNo
	 * @throws @author Ju Nana
	 * @date 2019-3-25 下午11:36:20
	 */
	public void txDeleteAccountManagement(String status,String custNo,String eleAccNos) throws Exception;
	/**
	 * 入池交易明细查询
	 * @param  
	 * @param page
	 * @return
	 * @throws Exception
	 * @author gcj
	 * @date 20210429
	 */
	public List<DraftPoolIn> queryDraftPoolIn(DraftQueryBean bean, User user, Page page) throws Exception;
	/**
	 * 出池交易明细查询
	 * @param  
	 * @param page
	 * @return
	 * @throws Exception
	 * @author gcj
	 * @date 20210429
	 */
	public List<DraftPoolOut> queryDraftPoolOut(DraftQueryBean bean, User user, Page page) throws Exception;
	/**
	 * 银票关联合同信息查询 gcj 
	 * @param  
	 * @param page
	 * @return
	 * @throws Exception
	 * @author gcj
	 * @date 20210510
	 */
	public String findCrdtProductAcptDetail(CreditProductQueryBean bean, Page page, User user) throws Exception;
	
	/**
	 * 可出池票据查询
	 * @param  
	 * @param page
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 20210701
	 */
	public List findQueryPoolOutList(DraftQueryBean bean, Page page, User user) throws Exception;
	
	/**
	 * 出池票据提交申请
	 * @param  
	 * @param page
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 20210701
	 */
	public void txSubmitPoolOutBill(String id , User user) throws Exception;
	
	/**
	 * 出池申请数据撤回申请
	 * @param  
	 * @param page
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 20210701
	 */
	public String txCancelAuditPoolOutBill(String id, User user) throws Exception;
	
	/**
	 * 根据票号查询核心纸票托收信息对象
	 * @param bill
	 * @return
	 * @throws Exception
	 * @author wu Fengjun @data 2019-06-18 上午11:38
	 */
	public CorePdraftColl getPdraftCollByBill(String bill) throws Exception;
	
	/**
	 * 根据拆分的id查询拆分的数据
	 * @param splitId
	 * @return
	 * @throws Exception
	 */
	public List<DraftPool> getDraftPoolList(String splitId) throws Exception;
	
}
