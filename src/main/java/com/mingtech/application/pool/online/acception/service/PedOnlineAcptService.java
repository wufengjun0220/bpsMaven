package com.mingtech.application.pool.online.acception.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptInfo;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocolHist;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatchUpdate;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptCacheDetail;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface PedOnlineAcptService extends GenericService {
	
	 /**
	  * @description 查询生效协议
	  * @author wss
	  * @date 2021-4-25
	  */
	public List<PedOnlineAcptProtocol> queryOnlineAcptProtocolList(OnlineQueryBean queryBean);
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-4-25
	 * @description 在线银承协议规则校验
	 */
	public Ret onlineAcptCheck(OnlineQueryBean queryBean) throws Exception;
	/**
	 * @description 保存银承协议
	 * @author wss
	 * @date 2021-4-27
	 * @param queryBean 主协议
	 * @param payees 收票人
	 * @param mgs 短信通知人
	 * @throws Exception 
	 */
	public void txSaveOnlineAcptPtl(OnlineQueryBean queryBean) throws Exception;
	/**
	 * @description 查询在线银承收票人
	 * @author wss
	 * @date 2021-4-27
	 * @param onlineAcptNo 协议编号
	 * @param status 0失效；1生效
	 */
	public List queryOnlineAcptPayeeList(String onlineAcptNo, String status);
	/**
	 * @description 查询客户总额度
	 * @author wss
	 * @date 2021-4-28
	 * @param onlineAcptNo
	 * @param payeeId
	 */
	public BigDecimal getOnlineAcptPayeeAmt(String onlineAcptNo, String payeeId);
	/**
	 * @description 查询收票人
	 * @author wss
	 * @date 2021-4-29
	 * @param queryBean
	 * @param page
	 */
	public List queryOnlineAcptPayeeList(OnlineQueryBean queryBean, Page page);
	/**
	 * @description 校验协议有效性
	 * @author wss
	 * @date 2021-5-7
	 * @param bean
	 * @param pool
	 * @param errors
	 */
	public List checkPoolInfoForEBK(OnlineQueryBean bean,PedProtocolDto pool, List errors);
	
	/**
	 * @description 保存在线银承批次和明细信息
	 * @author wss
	 * @return 
	 * @throws Exception 
	 * @date 2021-5-7
	 */
	public OnlineQueryBean createOnlineAcpt(OnlineQueryBean queryBean) throws Exception;
	/**
	 * @description 查询银承业务明细
	 * @author wss
	 * @date 2021-5-10
	 */
	public List queryOnlinAcptDetails(OnlineQueryBean bean);
	/**
	 * 查询承兑明细
	 */
	public List queryOnlinAcptByStatus(OnlineQueryBean queryBean);
	/**
	 * @description 新增票据
	 * @author wss
	 * @date 2021-5-10
	 * @param batch
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyNewBills(PlOnlineAcptBatch batch) throws Exception;
	/**
	 * @description 出票登记
	 * @author wss
	 * @param batch 
	 * @throws Exception 
	 * @date 2021-5-10
	 */
	public boolean txApplyDrawBill(PlOnlineAcptDetail detail, PlOnlineAcptBatch batch) throws Exception;
	/**
	 * @Title txApplyAcception
	 * @author wss
	 * @date 2021-5-25
	 * @Description 提示承兑申请
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean txApplyAcception(PlOnlineAcptDetail detail,String elctrncSign) throws Exception;
	/**
	 * @description 验证核心承兑记账
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 */
	public String txApplyQueryAcctStatus(PlOnlineAcptDetail detail,String ConrtNo) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-11
	 * @description 查询bbsp票据信息
	 * @param  transType 1申请 2签收
	 * @param  transCode 交易码 2001-出票登记、2002-提示承兑、2003-提示收票、2004-撤票
	 */
	public String txApplyQueryBill(PlOnlineAcptDetail detail,String transType,String status) throws Exception;
	/**
	 * @author wss
	 * @param page 
	 * @date 2021-5-11
	 * @description 查询在线银承批次信息
	 */
	public List queryOnlineAcptBatchList(OnlineQueryBean queryBean, Page page);
	/**
	 * 
	 * @author wss
	 * @date 2021-5-11
	 * @description 查询在线银承明细信息
	 */
	public List<PlOnlineAcptDetail> queryPlOnlineAcptDetailList(OnlineQueryBean queryBean, Page page);
	/**
	 * 
	 * @author wss
	 * @date 2021-5-11
	 * @description 查询在线银承缓存明细信息
	 */
	public List<PlOnlineAcptCacheDetail> queryPlOnlineAcptCacheDetailList(OnlineQueryBean queryBean, Page page);
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-12
	 * @description 在线银承全量校验
	 */
	public ReturnMessageNew txAcptFullCheck(OnlineQueryBean queryBean) throws Exception;
	/**
	 * @author wss
	 * @date 2021-5-12
	 * @description 删除电票信息
	 * @param detail
	 */
	public void txApplyDeleteBill(PlOnlineAcptDetail detail);
	/**
	 * @author wss
	 * @date 2021-5-13
	 * @description 提示承兑签收
	 * @param auditStatus 0:审批同意  1：审批拒绝
	 * @throws Exception 
	 */
	public boolean txApplyAcptSign(PlOnlineAcptDetail detail,String elctrncSign,String auditStatus) throws Exception;
	/**
	 * @author wss
	 * @param batch 
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 提示收票
	 */
	public boolean txApplyReceiveBill(PlOnlineAcptDetail detail, PlOnlineAcptBatch batch) throws Exception;
	/**
	 * @author wss
	 * @param batch 
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 汇票撤销
	 */
	public boolean txApplyCancleBill(PlOnlineAcptDetail detail, PlOnlineAcptBatch batch) throws Exception;
	/**
	 * @author wss
	 * @param batch 
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 统一撤销申请
	 */
	public boolean txApplyRevokeApply(PlOnlineAcptDetail detail, PlOnlineAcptBatch batch) throws Exception;
	/**
	 * @Title queryOnlinAcptPtlByNo
	 * @author wss
	 * @date 2021-5-15
	 * @Description 查询在线银承协议
	 * @param onlineAcptNo
	 */
	public PedOnlineAcptProtocol queryOnlinAcptPtlByNo(String onlineAcptNo);
	/**
	 * @Title 信贷风险探测及额度占用
	 * @author wss
	 * @date 2021-5-15
	 * @param batch
	 * @param protocolType 银承 流贷
	 * @param operationType 占用 额度校验
	 */
	public ReturnMessageNew txPJE021Handler(PlOnlineAcptBatch batch,String protocolType,String operationType,Date dudeDate) throws Exception;
	/**
	 * @Title 统一撤销查询
	 * @author wss
	 * @date 2021-5-17
	 * @return String
	 */
	public boolean txApplyQueryCancle(PlOnlineAcptDetail detail,
			String bbspBusiType04) throws Exception;
	/**
	 * @Title txSyncContract
	 * @author wss
	 * @date 2021-5-24
	 * @Description 同步合同信息
	 * @param onlineNo 在线协议编号
	 * @param contractNo 合同号
	 */
	public void txSyncContract(String onlineNo, String contractNo) throws Exception;
	/**
	 * @Title txSyncPedCreditDetail
	 * @author wss
	 * @date 2021-5-24
	 * @Description 同步明细信息
	 * @param PlOnlineAcptDetail
	 */
	public void txSyncPedCreditDetail(PlOnlineAcptDetail detail );
	
	/**
	 * @Title txSyncAcptBatchDealStatus
	 * @author wss
	 * @date 2021-5-26
	 * @Description 同步银承批次处理状态(包含同步批次状态、生成合同、短信通知)
	 */
	public void txSyncAcptBatchStatus(PlOnlineAcptBatch batch);
	/**
	 * @Title queryOnlineAcptDetailByBatchId
	 * @author wss
	 * @date 2021-5-27
	 * @Description 通过批次id查询银承明细
	 * @return List<PlOnlineAcptDetail>
	 */
	public List<PlOnlineAcptDetail> queryOnlineAcptDetailByBatchId(String id);
	/**
	 * @Title queryOnlineAcptContractList
	 * @author wss
	 * @date 2021-5-31
	 * @Description 在线银承合同查询
	 * @return List
	 */
	public List queryOnlineAcptContractList(OnlineQueryBean queryBean, Page page);
	/**
	 * @Title queryOnlineAcptPtlHist
	 * @author wss
	 * @date 2021-6-2
	 * @Description 查询银承协议历史表
	 * @return List
	 */
	public List queryOnlineAcptPtlHist(OnlineQueryBean bean);
	/**
	 * @Title queryOnlinePayeeHistList
	 * @author wss
	 * @date 2021-6-2
	 * @Description 查询收票人历史信息
	 * @return List
	 */
	public List queryOnlinePayeeHistList(OnlineQueryBean bean);
	/**
	 * @description 查询在线银承收票人历史 多表联查 实时查看
	 * @author 
	 * @date 2021-8-18
	 * @param queryBean
	 * @param page
	 */
	public List queryOnlineAcptPayeeHistListByBean(OnlineQueryBean queryBean, Page page);
	/**
	 * @Title compareDto
	 * @author wss
	 * @date 2021-6-18
	 * @Description 比较两个对象值  如果未修改 置为空
	 * @return PedOnlineAcptProtocolHist
	 */
	public PedOnlineAcptProtocolHist compareDto(PedOnlineAcptProtocolHist hist,
			PedOnlineAcptProtocolHist last);
	/**
	 * @Title loadAcptDetailToExpt
	 * @author wss
	 * @date 2021-6-21
	 * @Description 银承明细导出
	 * @return List
	 */
	public List loadAcptDetailToExpt(OnlineQueryBean bean, Page page);
	/**
	 * @Title loadAcptDetailToExpt
	 * @author 
	 * @date 
	 * @Description 银承缓存明细导出
	 * @return List
	 */
	public List loadAcptCacheDetailToExpt(OnlineQueryBean bean, Page page);
	/**
	 * @Title queryOnlineAcptProtocol
	 * @author wss
	 * @date 2021-6-24
	 * @Description TODO
	 * @return PedOnlineAcptProtocol
	 */
	public PedOnlineAcptProtocol queryOnlineAcptProtocol(
			OnlineQueryBean queryBean);
	/**
	 * 根据银承批次表信息，创建主业务合同实体
	 * @param acptBatch
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-4下午11:19:27
	 */
	public CreditProduct creatProductByAcptBatch(PlOnlineAcptBatch acptBatch,PedProtocolDto pro);
	
	/**
	 * 根据银承明细信息，创建在线银承明细信息
	 * @param acptDetail
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-4下午11:20:06
	 */
	public PedCreditDetail creatCrdtDetailByAcptDetail(PlOnlineAcptDetail acptDetail,PlOnlineAcptBatch acptBatch,PedProtocolDto pro);
	
	/**
	 * 根据在线银承批次Id查询在线银承明细ID
	 * @param batchId
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-7上午10:14:05
	 */
	public List<String> queryOnlineAcptDetailIdList(String batchId);
	/**
	 * @param queryBean
	 * @return
	 * @author wss
	 * @date 2021-7-7上午10:14:05
	 */
	public PedOnlineAcptInfo queryonlinePayeeByBean(OnlineQueryBean queryBean);
	/**
	 * @Title txLocalLimitUsed
	 * @author wss
	 * @date 2021-7-9
	 * @Description 协议额度和收票人额度占用
	 * @return void
	 * @throws Exception 
	 * @param usedType 操作 0释放 1占用
	 */
	public Ret txLocalLimitUsed(PlOnlineAcptBatch batch,List<PlOnlineAcptDetail> details,String usedType) throws Exception;
	/**
	 * @Title queryOnlineAcptProtocolList
	 * @author wss
	 * @date 2021-7-15
	 * @Description 查询协议信息
	 * @return List
	 */
	public List queryOnlineAcptProtocolList(OnlineQueryBean queryBean,Page page);
	/**
	 * @Title queryOnlineAcptPayeeListBean
	 * @author wss
	 * @date 2021-7-15
	 * @Description 查询收票人
	 * @return List<OnlineQueryBean>
	 */
	public List<OnlineQueryBean> queryOnlineAcptPayeeListBean(
			OnlineQueryBean bean, Page page);
	/**
	 * @Title queryOnlineAcptPayeeListBean
	 * @author wss
	 * @date 2021-7-17
	 * @Description 查询收票人
	 * @return List
	 */
	public List queryOnlineAcptPayeeListBean(String onlineAcptNo, String string);
	/**
	 * @Title txRepeatAcceptionSign
	 * @author wss
	 * @date 2021-7-17
	 * @Description 承兑签收重复执行
	 * @return void
	 * @throws Exception 不在此方法里处理异常，放在调用层处理异常
	 */
	public void txRepeatAcceptionSign(PlOnlineAcptDetail detail,
			PlOnlineAcptBatch batch) throws Exception;
	
	public void txFailExe(String queueNode,String queueName,String busiId)throws Exception;

	/**
	 * @author 
	 * @throws Exception 
	 * @date 2021-7-29
	 * @description 在线银承复核校验
	 */
	public ReturnMessageNew txAcptCheckApply(OnlineQueryBean queryBean) throws Exception;
	/**
	 * 银承信息 组装queryBean
	 * @date 2021-7-29
	 * @throws Exception 
	 */
	public OnlineQueryBean createOnlineAcptApply(PlOnlineAcptBatch batch , List <PlOnlineAcptDetail> details) throws Exception;

	/**
	 * 银承历史信息保存
	 * 
	 */
	public void txAcptApply(PlOnlineAcptBatch batch , List<PlOnlineAcptDetail>  details) throws Exception;
	/**
	 * @description 查询在线银承收票人
	 * @author 
	 * @date 2021-8-18
	 * @param queryBean
	 * @param page
	 */
	public List queryOnlineAcptPayeeListByBean(OnlineQueryBean queryBean, Page page);
	/**
	 * 在线银承失败后，将明细作废
	 * @param batch
	 * @author 
	 * @date 
	 */
	public void txFailChangeAcptDetail(PlOnlineAcptBatch batch,String status);
	
	/**
	 * 银承撤票查验处理
	 * @param detail
	 * @param batch
	 * @return
	 */
	public Map<String,String> txApplyRevokeCheck(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch);
	
	/**
	 * 银承未用退回查验
	 * @param detail
	 * @param batch
	 * @return
	 */
	public Map<String,String> txApplyUnusedCheck(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch);
	
	/**
	 * 在线银承批次信息查询
	 * @param queryBean
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-27下午4:48:51
	 */
	public PlOnlineAcptBatch queryPlOnlineAcptBatch(OnlineQueryBean queryBean) throws Exception;
	
	/**
	 * 根据借据号查询在线银承明细对象
	 * @param bilNo
	 * @return
	 * @throws Exception
	 * @author Wufengjun
	 * @date 2021-11-19
	 */
	public PlOnlineAcptDetail queryPlOnlineAcptDetailByBillNo(String loanNo) throws Exception;
	
	/**
	 * 信贷合同金额变更
	 * @param batch
	 * @param totalRelsAmt
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew misRepayAcptPJE028(PlOnlineAcptBatch batch) throws Exception;
	
	/**
	 * 根据
	 * @param id
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 2022-1-6
	 */
	public String query(String id) throws Exception;
	
	/**
	 * @Title 信贷合同金额变更
	 * @author wss
	 * @date 2021-5-15
	 * @param batch
	 * @param protocolType 银承 流贷
	 * @param operationType 占用 额度校验
	 */
	public ReturnMessageNew txPJE028Handler(PlOnlineAcptBatch batch) throws Exception;
	
	public PlOnlineAcptBatch calculateBatchAmt(PlOnlineAcptBatch batch) throws Exception;
}
