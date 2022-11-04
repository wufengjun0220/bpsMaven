package com.mingtech.application.pool.online.loan.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtInfoHist;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocolHist;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface PedOnlineCrdtService extends GenericService {

	/**
	 * @description 查询在线协议
	 * @author wss
	 * @date 2021-4-28
	 * @param queryBean
	 * @return List
	 */
	public List<PedOnlineCrdtProtocol> queryOnlineProtocolList(OnlineQueryBean queryBean);

	/**
	 * @param queryBean 在线流贷规则校验
	 * @author wss
	 * @date 2021-4-28
	 * @description
	 * @return Ret
	 */
	public Ret onlineCrdtCheck(OnlineQueryBean queryBean);
	
	/**
	 * @description 保存在线流贷协议
	 * @author wss
	 * @date 2021-4-28
	 * @param
	 */
	public void txSaveOnlineProtocol(OnlineQueryBean queryBean) throws  Exception ;

	/**
	 * @description 查询在线流贷收票人
	 * @author wss
	 * @date 2021-4-29
	 * @param onlineCrdtNo
	 * @param status
	 */
	public List queryOnlinePayeeList(String onlineCrdtNo, String status);
	
	/**
	 * @description
	 * @author wss
	 * @date 2021-4-30
	 */
	public List queryOnlineCrdtPayeeList(OnlineQueryBean queryBean, Page page);
	/**
	 * @author wss
	 * @date 2021-5-7
	 * @description
	 */
	public List checkPoolInfoForEBK(OnlineQueryBean queryBean, PedProtocolDto pool,List errors);
	/**
	 * @author wss
	 * @date 2021-5-8
	 * @description
	 * @param queryBean
	 */
	public List<PlCrdtPayPlan> queryPlCrdtPayPlanListByBean(OnlineQueryBean queryBean, Page page);
	/**
	 * @author wss
	 * @date 2021-5-8
	 * @description
	 * @param queryBean
	 */
	public List queryPlCrdtPayCachePlanListByBean(OnlineQueryBean queryBean,Page page);
	/**
	 * @Title 流贷业务校验
	 * @author wss
	 * @date 2021-5-15
	 * @param queryBean
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew txCrdtFullCheck(OnlineQueryBean queryBean) throws Exception;
	/**
	 * @Title 保存受托支付
	 * @author wss
	 * @date 2021-5-16
	 * @return PlOnlineCrdt
	 * @throws Exception 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public OnlineQueryBean createOnlineCrdt(OnlineQueryBean queryBean) throws Exception;
	/**
	 * @Title txPJE021
	 * @author wss
	 * @date 2021-5-17
	 * @Description 风险探测及额度占用
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew txPJE021(PlOnlineCrdt batch, String productType,
			String operationType) throws Exception;
	
	/**
	 * @Title queryOnlineProtocolByCrdtNo
	 * @author wss
	 * @date 2021-5-17
	 * @Description 查询流贷协议
	 * @return PedOnlineCrdtProtocol
	 */
	public PedOnlineCrdtProtocol queryOnlineProtocolByCrdtNo(String onlineCrdtNo);
	/**
	 * @Title applyMakeLoans
	 * @author wss
	 * @date 2021-5-17
	 * @Description 放款申请
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyMakeLoans(PlOnlineCrdt batch) throws Exception;
	/**
	 * @Title queryRatefromLPR
	 * @author wss
	 * @date 2021-5-17
	 * @Description 查询LPR利率
	 * @return BigDecimal
	 * @throws Exception 
	 */
	public BigDecimal queryRatefromLPR() throws Exception;

	/**
	 * @Title txApplyQueryAcct
	 * @author wss
	 * @date 2021-5-19
	 * @Description 查询核心贷款记账状态
	 * @return boolean
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyQueryAcct(PlOnlineCrdt batch) throws Exception;
	/**
	 * @Title txApplyFreeTransfer
	 * @author wss
	 * @param tranAmt 
	 * @param info 
	 * @date 2021-5-19
	 * @Description 核心解圈
	 * @return void
	 * @throws Exception 
	 */
	public void txApplyFreeTransfer(PlCrdtPayPlan info, BigDecimal tranAmt) throws Exception;
	/**
	 * @Title queryonlineCrdtByContractNo
	 * @author wss
	 * @param contractNo 合同号
	 * @date 2021-5-21
	 * @Description 查询在线流贷业务表
	 * @return PlOnlineCrdt
	 */
	public PlOnlineCrdt queryonlineCrdtByContractNo(String contractNo);
	/**
	 * @Title txSavePlCrdtPayPlanHist
	 * @author wss
	 * @date 2021-5-21
	 * @Description 保存支付计划历史
	 * @return void
	 * @throws Exception 
	 */
	public void txSavePlCrdtPayPlanHist(PlCrdtPayPlan info,String operatorType) throws Exception;
	/**
	 * @Title txApplyRepayment
	 * @author wss
	 * @param payType 1提前 2贷款归还 
	 * @param fundNo 
	 * @param loanNo 
	 * @param branchNo 入账机构
	 * @date 2021-5-22
	 * @Description 核心还款
	 * @return void
	 */
	public ReturnMessageNew txApplyRepayment(String fundNo,String loanNo, BigDecimal payAmt, String payType,String accNo,String deduNo,String branchNo) throws Exception;
	/**
	 * @Title queryPlOnlineCrdtList
	 * @author wss
	 * @date 2021-5-24
	 * @Description 流贷业务表综合查询
	 * @return List
	 */
	public List queryPlOnlineCrdtList(OnlineQueryBean queryBean, Page page);
	/**
	 * @Title txSyncContract
	 * @author wss
	 * @date 2021-5-24
	 * @Description 同步合同状态
	 * @param onlineNo 在线协议编号
	 * @param contractNo 在线流贷合同号
	 * @throws Exception 
	 */
	public void txSyncContract(String onlineNo, String contractNo) throws Exception;
	/**
	 * @Title txSyncPedCreditDetail
	 * @author wss
	 * @date 2021-5-24
	 * @return void
	 * @param acptNo
	 * @param onlineType
	 * @throws Exception 
	 */
	public void txSyncPedCreditDetail(String acptNo, String loanNo) throws Exception;
	/**
	 * @Title txSyncPlOnlineCrdtStatus
	 * @author wss
	 * @date 2021-5-27
	 * @Description 同步业务批次信息
	 * @return void
	 */
	public void txSyncPlOnlineCrdtStatus(PlOnlineCrdt crdt);
	/**
	 * @Title queryOnlineCrdtContractList
	 * @author wss
	 * @date 2021-5-31
	 * @Description 在线流贷合同查询
	 * @return List
	 */
	List queryOnlineCrdtContractList(OnlineQueryBean queryBean, Page page);
	/**
	 * @Title queryOnlineAcptPtlHist
	 * @author wss
	 * @date 2021-6-2
	 * @Description 查询流贷协议历史表
	 * @return List
	 */
	public List queryOnlineCrdtPtlHist(OnlineQueryBean bean);
	/**
	 * @Title queryOnlinePayeeHistList
	 * @author wss
	 * @date 2021-6-2
	 * @Description 查询收款人历史表
	 * @return List<PedOnlineCrdtInfoHist>
	 */
	public List<PedOnlineCrdtInfoHist> queryOnlinePayeeHistList(OnlineQueryBean bean);
	/**
	 * @Title queryOnlinePedCreditDetailList
	 * @author wss
	 * @date 2021-6-4
	 * @Description 查询在线流贷借据表
	 * @return List
	 */
	public List queryOnlinePedCreditDetailList(OnlineQueryBean queryBean);
	/**
	 * @Title queryonlineCrdtByLoanNo
	 * @author wss
	 * @date 2021-6-4
	 * @Description 通过借据号查询流贷批次
	 * @return PlOnlineCrdt
	 */
	public PlOnlineCrdt queryonlineCrdtByLoanNo(String loanNo);
	/**
	 * @Title queryCrdtPlanHistList
	 * @author wss
	 * @date 2021-6-10
	 * @Description 在线支付计划修改历史查询
	 * @return List
	 */
	public List queryCrdtPlanHistList(OnlineQueryBean bean, Page page);
	/**
	 * @Title txRepayOnlinePayPlanAudit
	 * @author wss
	 * @date 2021-6-17
	 * @Description 支付计划还款提交审批
	 * @return void
	 * @throws Exception 
	 */
	public Ret txRepayOnlinePayPlanAudit(PlCrdtPayPlan plan, BigDecimal repayAmt,User user) throws Exception;

	/**
	 * @Title compareDto
	 * @author wss
	 * @date 2021-6-18
	 * @Description 比较对应字段是否变动
	 * @return PedOnlineCrdtProtocolHist
	 */
	public PedOnlineCrdtProtocolHist compareDto(PedOnlineCrdtProtocolHist hist,
			PedOnlineCrdtProtocolHist lastdhHist);
	/**
	 * @Title queryOnlineProtocol
	 * @author wss
	 * @date 2021-6-30
	 * @Description 查询在线流贷协议
	 * @return PedOnlineCrdtProtocol
	 */
	public PedOnlineCrdtProtocol queryOnlineProtocol(OnlineQueryBean queryBean);
	
	/**
	 * @Title 查询在线流贷支付计划取消的审批明细数据
	 * @author wfj
	 * @date 2021-7-6
	 * @Description 查询在线流贷协议
	 * @return PedOnlineCrdtProtocol
	 */
	public List queryOnlinePayPlanDetails(OnlineQueryBean queryBean,Page page);
	
	/**
	 * 根据在线流贷主表创建主业务合同对象
	 * @param crdtBatch
	 * @param pro
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-5下午4:28:21
	 */
	public CreditProduct creatProductByPlOnlineCrdt(PedOnlineCrdtProtocol crdtBatch,PedProtocolDto pro);
	
	/**
	 * 根据在线流贷业务主表创建借据对象
	 * @param crdtBatch
	 * @param pro
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-5下午4:29:00
	 */
	public PedCreditDetail creatCrdtDetailByPlOnlineCrdt(PlOnlineCrdt crdtBatch,PedProtocolDto pro);
	/**
	 * @Title queryPlCrdtPayPlanByBatchId
	 * @author wss
	 * @date 2021-7-16
	 * @Description 通过批次id查询明细
	 * @return List<PlCrdtPayPlan>
	 */
	public List<PlCrdtPayPlan> queryPlCrdtPayPlanByBatchId(String id);

	/**
	 * 查询银承流贷的协议信息
	 * @param queryBean
	 * @param page
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 2021-7-19
	 */
	public List queryOnlineContractList(OnlineQueryBean queryBean, Page page) throws Exception;
	
	/**
	 * 根据主业务合同号查询支付计划列表
	 * @param contractNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-19下午2:05:55
	 */
	public List<PlCrdtPayPlan> queryPayPlanListByBatchContractNo(String contractNo);
	
	/**
	 * 根据主业务合同号查询该借据下的所有支付计划的汇总额度
	 * @param contractNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-8-30上午1:13:07
	 */
	public PlCrdtPayPlan queryAllPlanAmt(String contractNo);
	
	/**
	 * @Title queryOnlineCrdtPayeeListByBean
	 * @author wss
	 * @date 2021-7-19
	 * @Description 查询收票人信息
	 * @return List
	 */
	List queryOnlineCrdtPayeeListByBean(OnlineQueryBean queryBean, Page page);
	/**
	 * @Title queryOnlineProtocolList
	 * @author wss
	 * @date 2021-7-19
	 * @Description 查询协议信息
	 * @return List
	 */
	List queryOnlineProtocolList(OnlineQueryBean queryBean, Page page);
	
	/**
	 * 支付计划对外支付--事务方法
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-19下午9:01:39
	 */
	public void txPayPlanPayTask(PlCrdtPayList pay) throws Exception;
	
	/**
	 * 支付计划撤销审批
	 * @param id
	 * @param user
	 * @throws Exception
	 * @date 2021-07-20 10:53
	 * @author wfj
	 */
	public void txCancelAuditPayPlan(String id ,User user) throws Exception;
	
	
	/**
	 * 根据支付计划组装支付列表信息
	 * @param plan
	 * @param pay
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-23上午10:44:32
	 */
	public PlCrdtPayList toCreatPlCrdtPayListByPlan(PlCrdtPayPlan plan,PlCrdtPayList pay);
	
	/**
	 * 根据流水号查询支付列表
	 * @param flowNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-25上午4:02:56
	 */
	public List<PlCrdtPayList> queryPlCrdtPayListByflowNo(String flowNo);
	/**
	 * 受托支付保存
	 * @throws Exception 
	 */
	public OnlineQueryBean createOnlineCrdtApply(PlOnlineCrdt plOnlineCrdt) throws Exception;
	/**
	 * 流贷复核校验
	 * @throws Exception 
	 */
	public ReturnMessageNew txCrdtApplyCheck(OnlineQueryBean queryBean) throws Exception;
	
	/**
	 * 查询生效银承协议
	 */
	public PedOnlineCrdtProtocol queryOnlineCrdtProtocol(OnlineQueryBean queryBean);
	public List queryOnlineCrdtPayeeHistListByBean(OnlineQueryBean queryBean, Page page);

	
	/**
	 * 支付计划修改
	 * @param plan
	 * @param rlsAmt  修改金额
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-24下午8:39:28
	 */
	public Ret txModifyOnlinePayPlan(PlCrdtPayPlan plan,BigDecimal rlsAmt)throws Exception;
	
	/**
	 * 支付计划未用归还
	 * @param loan
	 * @param totalRelsAmt
	 * @param flowNo
	 * @param isRepayFlag 是否归还
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-25上午10:00:31
	 */
	public Ret txRepayOnlinePayPlan(PedCreditDetail loan,BigDecimal totalRelsAmt,String flowNo)throws Exception;

	/**
	 * 查询网银所需未结清的支付计划
	 * @param queryBean
	 * @param page
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 2021-02-28
	 */
	public List queryPlCrdtPayPlanUncleared(OnlineQueryBean queryBean,Page page) throws Exception;
	
	
	/**
	 * 查询在线流贷圈存金额
	 * @param loanNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-28上午9:49:05
	 */
	public BigDecimal queryOnlineCrdtFrozenTotalAmt(String loanNo) throws Exception;
	
	/**
	 * @Title queryOnlineCrdtPayeeListForPjc053
	 * @author wfj
	 * @date 2021-11-18
	 * @Description 查询收票人信息 pjc053 查询没有送在线协议编号
	 * @return List
	 */
	List queryOnlineCrdtPayeeListForPjc053(OnlineQueryBean queryBean, Page page);
	
	/**
	 * 流贷做贷款归还时  信贷额度释放
	 * @param crdt
	 * @param totalRelsAmt
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew misRepayCrdtPJE021(PlOnlineCrdt crdt,BigDecimal totalRelsAmt) throws Exception;
	
	/**
	 * 根据合同号统计支付流水中未用退回的总金额
	 * @param crdtNo
	 * @return
	 * @throws Exception
	 */
	public BigDecimal queryUnUsedReturnAmtOfCrdt(String crdtNo,Date date) throws Exception;
	
}
