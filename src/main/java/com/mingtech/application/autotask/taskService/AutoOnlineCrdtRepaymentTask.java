package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 线上流贷到期自动还款自动任务处理 ：到期日上午十点执行
 * @author Ju Nana
 * @version v1.0
 * @date 2021-9-27
 * @copyright 北明明润（北京）科技有限责任公司
 */

public class AutoOnlineCrdtRepaymentTask  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoOnlineCrdtRepaymentTask.class);
	
	private PedOnlineCrdtService pedOnlineCrdtService =PoolCommonServiceFactory.getPedOnlineCrdtService();
	private PoolCreditProductService PoolCreditProductService = PoolCommonServiceFactory.getPoolCreditProductService();
	private DraftPoolQueryService draftPoolQueryService = PoolCommonServiceFactory.getDraftPoolQueryService();
	private AutoTaskPublishService autoTaskPublishService = PoolCommonServiceFactory.getAutoTaskPublishService();
	private PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();

	public AutoOnlineCrdtRepaymentTask() {
	}
	
	
	public BooleanAutoTaskResult run() throws Exception {
		try {
			
			logger.info("查询当日到期的全部【未结清】【线上】【流贷】借据信息...");

			CreditQueryBean dtlQueryBean = new CreditQueryBean();
			dtlQueryBean.setEndDate( DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT));//今天
			dtlQueryBean.setDetailStatus(PoolComm.LOAN_1);//未结清
			dtlQueryBean.setIsOnline(PoolComm.YES);//线上
			dtlQueryBean.setLoanType(PoolComm.XD_02);//流贷
			List<PedCreditDetail> drtlList =  PoolCreditProductService.queryCreditDetailList(dtlQueryBean);
			
			
			logger.info("循环每笔借据，按照MIN（解圈金额，借据本息和）调核心提前还款交易，同时将该借据项下所有支付计划剩余支付金额置为0，并将支付计划任务置为【已完成】");

			if(null != drtlList){
				
				Map<String,PedProtocolDto> proMap = new HashMap<String,PedProtocolDto>();////存放有票据池协议，后续用于重新计算额度

				for(PedCreditDetail loan : drtlList){
					
					String bpsNo = loan.getBpsNo();
					PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
					proMap.put(bpsNo, dto);
					
					try {
						BigDecimal frozAmt = BigDecimal.ZERO;//圈存金额
						BigDecimal blanceAmt = BigDecimal.ZERO;//清贷款需还款总额
						BigDecimal refFrozenAmt = BigDecimal.ZERO;//需要解圈存的金额
						
						//更新借据,获取结清贷款需还款总额
						loan = draftPoolQueryService.txUpdateLoanByCoreforQuery(loan);
						blanceAmt = loan.getActualAmount();
						
						//查询圈存金额
						frozAmt = pedOnlineCrdtService.queryOnlineCrdtFrozenTotalAmt(loan.getLoanNo());
						
						//解圈金额 = MIN（解圈金额，借据本息和）
						if(BigDecimal.ZERO.compareTo(frozAmt) != 0){
							refFrozenAmt = frozAmt.compareTo(blanceAmt)>0 ? blanceAmt : frozAmt;
						}
						
						//调用核心提前还款交易
						if(BigDecimal.ZERO.compareTo(refFrozenAmt)!=0){
							
							String flowNo = "PAY-"+Long.toString(System.currentTimeMillis());//流水号，用来标记该批申请
							
							//组装支付列表
							this.txStorePayList(flowNo, loan.getLoanNo());
							
							//将支付计划还款申请发布到队列中
							logger.info("线上流贷到期自动还款自动任务处理,任务发布，票据池编号编号【"+loan.getBpsNo()+"】主业务合同号【"+loan.getCrdtNo()+"】处理流水号【"+flowNo+"】");
							Map<String,String> reqParams = new HashMap<String,String>();
							reqParams.put("totalRelsAmt", refFrozenAmt.toString());//释放总金额
							reqParams.put("flowNo", flowNo);//流水号
							autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_REPAY_NO, loan.getCreditDetailId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_REPAY, reqParams, loan.getLoanNo(), loan.getBpsNo(), null, null);
							
							
						}
					} catch (Exception e) {
						logger.error("在线流贷到期处理自动任务处理借据【"+loan.getLoanNo()+"】异常:",e);
						continue;
					}
					
				}
				
				
				
				logger.info("线上流贷到期自动还款自动任务处理，重新计算票据池额度开始...");
				
				Thread.sleep(5000);
				if(!proMap.isEmpty()){
					List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>(proMap.values());
					financialService.txReCreditCalculationTask(proList);
				}
				
				logger.info("线上流贷到期自动还款自动任务处理，重新计算票据池额度结束！");
				
			}

		} catch (Exception e) {
			logger.error("在线流贷到期处理自动任务异常:",e);
		}
		
		return new BooleanAutoTaskResult(true);
		
	}
	
	/**
	 * 支付明细列表落库
	 * @param flowNo
	 * @param loanNo
	 * @author Ju Nana
	 * @date 2021-9-28上午11:43:36
	 */
	private void txStorePayList(String flowNo,String loanNo){
		OnlineQueryBean queryBean = new OnlineQueryBean();
		queryBean.setStatus(PublicStaticDefineTab.PAY_PLAN_02);//有余额
		queryBean.setLoanNo(loanNo);
		List<PlCrdtPayPlan> planList = pedOnlineCrdtService.queryPlCrdtPayPlanListByBean(queryBean, null);
		if(null != planList){
			List<PlCrdtPayList> storePayList = new ArrayList<PlCrdtPayList>();
			for(PlCrdtPayPlan plan : planList){
				PlCrdtPayList pay = new PlCrdtPayList();
				pay.setPayPlanId(plan.getId());     //对应支付计划表ID      
				pay.setSerialNo(plan.getSerialNo());      //序列号
				pay.setLoanNo(loanNo);        //借据编号                                    
				pay.setContractNo(plan.getContractNo());    //合同编号                                    
				pay.setOnlineCrdtNo(plan.getOnlineCrdtNo());  //在线流贷协议编号                                
				pay.setBpsNo(plan.getBpsNo());         //票据池编号                                   
				pay.setLoanAcctNo(plan.getLoanAcctNo());    //付款账号                                    
				pay.setLoanAcctName(plan.getLoanAcctName());  //付款户名                                    
				pay.setDeduAcctNo(plan.getDeduAcctNo());    //收款人名称                                   
				pay.setDeduAcctName(plan.getDeduAcctName());  //收款人账号                                   
				pay.setDeduBankCode(plan.getDeduBankCode());  //收款人开户行行号                                
				pay.setDeduBankName(plan.getDeduBankName());  //收款人开户行名称                                
				pay.setIsLocal(plan.getIsLocal());       //是否跨行 0-否 1-是                            
				pay.setPayAmt(plan.getTotalAmt().subtract(plan.getRepayAmt()).subtract(plan.getUsedAmt()));//本次支付金额--全部圈存金额                                  
				pay.setStatus(PoolComm.PAY_STATUS_00);        //状态 00-初始化 01-支付成功 02-支付失败               
				pay.setOperatorType(PoolComm.PAY_TYPE_1);  //操作类型 0-付款交易  1-还款交易   2-修改                 
				pay.setTransChanel(PublicStaticDefineTab.CHANNEL_NO_BPS);   //渠道 EBK-网银 BPS-票据池                       
				pay.setUsage("还款");         //用途 网银码值                                 
				pay.setPostscript("票据池-贷款到期自动还款");    //附言                                      
				pay.setPayDesc("贷款到期支付计划自动还款-初始化");       //支付结果说明                                  
				pay.setPayReqSerNo(null);   //支付申请流水号                                 
				pay.setPayAcctDate(new Date());   //账务日期，即支付申请日期，核心查证支付结果的时候需要用到
				pay.setPayRespSerNo(null);  // 支付结果流水号     
				pay.setRepayFlowNo(flowNo);   //贷款归还/提前还款流水号--用来标记同一个批次的还款申请
				pay.setCreateDate(new Date()); //创建时间                                    
				pay.setUpdateTime(new Date()); //最近修改时间           
				pay.setTaskDate(new Date()); //最近修改时间    
				
				storePayList.add(pay);
			}
			pedOnlineCrdtService.txStoreAll(storePayList);
			
		}
		
	}

	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

}
