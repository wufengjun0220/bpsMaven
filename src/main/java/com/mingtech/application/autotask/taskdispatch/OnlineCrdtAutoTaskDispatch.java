package com.mingtech.application.autotask.taskdispatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.common.service.OnlineCommonService;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineHandleLog;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

/**
 *<p>在线流贷自动任务调度执行事件<p/>
 *@param reqParam 任务调度请求参数
 *@param autoTaskExe 任务执行流水
 *@param taskDispatchCfg 任务调度配置
 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
 *@描述
 */

public  class OnlineCrdtAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineCrdtAutoTaskDispatch.class);
	private PedOnlineCrdtService pedOnlineCrdtService =PoolCommonServiceFactory.getPedOnlineCrdtService();
	private OnlineCommonService onlineCommonService=PoolCommonServiceFactory.getOnlineCommonService();
	private OnlineManageService onlineManageService=PoolCommonServiceFactory.getOnlineManageService();
	private FinancialAdviceService financialAdviceService=PoolCommonServiceFactory.getFinancialAdviceService();
	private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	private CreditRegisterService creditRegisterService = PoolCommonServiceFactory.getCreditRegisterService();
	private PoolCoreService poolCoreService = PoolCommonServiceFactory.getPoolCoreService();

	
	
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		PlOnlineCrdt batch = (PlOnlineCrdt) pedOnlineCrdtService.load(autoTaskExe.getBusiId(),PlOnlineCrdt.class);
		
		try{
			OnlineQueryBean queryBean = new OnlineQueryBean();
     		BeanUtils.copyProperties(queryBean, batch);

			if(PublicStaticDefineTab.CRDT_BATCH_001.equals(batch.getStatus())){//新增
				
				this.txOlineCreditApply001(batch);
				
			}else if(PublicStaticDefineTab.CRDT_BATCH_002.equals(batch.getStatus())){//票据池额度占用成功
				
				this.txOlineCreditApply002(batch);
				
			}else if(PublicStaticDefineTab.CRDT_BATCH_003.equals(batch.getStatus())){//信贷额度占用成功
				
				this.txOlineCreditApply003(batch);
				
			}else if(PublicStaticDefineTab.CRDT_BATCH_004.equals(batch.getStatus())&& !PublicStaticDefineTab.ONLINE_DS_003.equals(batch.getDealStatus())){//核心放款成功，但是票据池内部处理异常时候的操作
				
				this.txOlineCreditApply004(batch);
			}else if(PublicStaticDefineTab.CRDT_BATCH_004_1.equals(batch.getStatus())){//核心放款失败，额度接口通讯失败
				
				this.txOlineCreditApply0041(batch);
			}

			
		}catch (Exception e) {
			pedOnlineCrdtService.txStore(batch);
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
			logger.error("在线流贷申请主任务OnlineCrdtAutoTaskDispatch调度执行异常...",e);
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		return resultMap;
	}
	
	/**
	 * 新增状态的处理
	 * @param batch
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-17下午9:49:36
	 */
	private void txOlineCreditApply001(PlOnlineCrdt batch) throws Exception{
		
		 Ret ApplyCheckRet = fullApplyCheck(batch);
		 if(!Constants.TX_SUCCESS_CODE.equals(ApplyCheckRet.getRET_CODE())){
			//作废支付计划
			this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
			return;
		 }
		
		 //贷款产品代码检查
		 Ret deduCheckRet = this.deductionProductNoCheck(batch);
		 if(Constants.TX_SUCCESS_CODE.equals(deduCheckRet.getRET_CODE())){
			 batch.setDeductionProductNo(deduCheckRet.getRET_MSG());
		 }else{
			 //作废支付计划
			 this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
			 return;
		 }
		
		
		/*
		 * 【1】票据池额度占用
		 */
		logger.info("在线流贷【"+batch.getContractNo()+"】业务票据池额度占用...");
		Ret crdtCheckRet = financialService.txOnlineCreditUsed(batch);
		
		if(Constants.TX_SUCCESS_CODE.equals(crdtCheckRet.getRET_CODE())){//票据池池额度占用成功
			
			/*
			 * 用独立事务保存状态
			 */
			batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_002);
			List<PlOnlineCrdt> batchList = new ArrayList<PlOnlineCrdt>();
			batchList.add(batch);
			financialAdviceService.txCreateList(batchList);
			
			/*
			 * 【2】信贷系统（额度系统）额度占用
			 */
			logger.info("在线流贷【"+batch.getContractNo()+"】业务额度系统额度占用...");
			ReturnMessageNew misResult  = pedOnlineCrdtService.txPJE021(batch,PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
			if(misResult.isTxSuccess()){
				/*
				 * 用独立事务保存状态
				 */
				batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_003);//额度占成功
				batchList = new ArrayList<PlOnlineCrdt>();
				batchList.add(batch);
				financialAdviceService.txCreateList(batchList);
				
				/*
				 * 【3】核心系统放款申请
				 */
				
				logger.info("在线流贷【"+batch.getContractNo()+"】业务核心放款申请...");
				ReturnMessageNew coreResult  = pedOnlineCrdtService.txApplyMakeLoans(batch);
				if(coreResult.isTxSuccess()){//核心放款成功
					
					logger.info("在线流贷【"+batch.getContractNo()+"】业务核心放款成功，处理后续操作...");
					/*
					 * 用独立事务保存状态
					 */
					batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004);//核心放款成功
					batchList = new ArrayList<PlOnlineCrdt>();
					batchList.add(batch);
					financialAdviceService.txCreateList(batchList);
					
					/*
					 * 放款成功后系统内数据处理
					 */
					this.txAfterSucc(batch);
					
				}else{//核心放款失败--有反馈的失败
					/*
					 * 用独立事务保存状态
					 */
					
					batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
					batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
					batchList = new ArrayList<PlOnlineCrdt>();
					batchList.add(batch);
					financialAdviceService.txCreateList(batchList);
					
					logger.info("在线流贷【"+batch.getContractNo()+"】业务核心放款失败，回滚额度系统已占用额度，回滚票据池额度...");
					
					//记录失败日志
		    		this.txSaveCrdtLog(batch, PublicStaticDefineTab.CHANNEL_NO_CORE,"在线流贷申请核心放款失败："+coreResult.getRet().getRET_MSG());
					
		    		
		    		/*
		    		 * 用独立事务保存状态
		    		 */
					batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
					batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
					batchList = new ArrayList<PlOnlineCrdt>();
					batchList.add(batch);
					financialAdviceService.txCreateList(batchList);
					
					
					//额度系统额度释放
					ReturnMessageNew relsResult  = pedOnlineCrdtService.txPJE021(batch,PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_CANCEL);
					//票据池池额度释放
					if(relsResult.isTxSuccess()){//额度系统额度释放成功
						
						/*
			    		 * 回滚票据池额度
			    		 */
			    		
			    		//根据借据号查询资产登记表资产信息ID
			    		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getContractNo(),null, PoolComm.VT_1);
			    		String id = crList.get(0).getBusiId();//因为在线流贷的借据有且只有一条，所以可以这样获取
			    		List<String> releseIds = new ArrayList<String>();
			    		releseIds.add(id);
			    		
			    		//额度回滚
			    		financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
			    		
			    		/*
						 * 作废支付计划
						 */
						this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
						
					}
							    		
				}
			}else{
				
				/*
				 * 用独立事务保存状态
				 */
				batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_003_1);
				batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
				batchList = new ArrayList<PlOnlineCrdt>();
				batchList.add(batch);
				financialAdviceService.txCreateList(batchList);
				
				logger.info("在线流贷【"+batch.getContractNo()+"】业务额度系统额度占用失败，记录失败日志，回滚票据池额度...");
				
				/*
				 * 记录额度占用失败
				 */
				String failMsg = misResult.getRet().getRET_MSG();
				failMsg = failMsg.length()<1000?failMsg:failMsg.substring(0, 1000);
	    		this.txSaveCrdtLog(batch, PublicStaticDefineTab.CHANNEL_NO_MIS, "在线流贷申请额度系统额度占用失败:"+ failMsg);
				
	    		/*
	    		 * 回滚票据池额度
	    		 */
	    		
	    		//根据借据号查询资产登记表资产信息ID
	    		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getContractNo(),null, PoolComm.VT_1);
	    		String id = crList.get(0).getBusiId();//因为在线银承的借据有且只有一条，所以可以这样获取
	    		List<String> releseIds = new ArrayList<String>();
	    		releseIds.add(id);
	    		
	    		//额度回滚
	    		financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
	    		
	    		/*
				 * 作废支付计划
				 */
				this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
				
			}
			
		}else{
			
			/*
			 * 票据池池额度占用失败（已回滚），到信贷系统进行风险探测，若探测不通过，记录不通过信息
			 */
			
			
			/*
			 * 用独立事务保存状态
			 */
			batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_002_1);//本地额度占用失败
			batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
			List<PlOnlineCrdt> batchList = new ArrayList<PlOnlineCrdt>();
			batchList.add(batch);
			financialAdviceService.txCreateList(batchList);
			
			/*
			 * 作废支付计划
			 */
			this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
			
			
			/*
			 * 信贷风险探测
			 */
			ReturnMessageNew result1  = pedOnlineCrdtService.txPJE021(batch,PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_CHECK);
			
			
			if(!result1.isTxSuccess()){//风险探测不通过，记录风险探测结果信息
				
				//记录失败日志
	    		this.txSaveCrdtLog(batch, PublicStaticDefineTab.CHANNEL_NO_MIS,result1.getRet().getRET_MSG().length()<1000 ? result1.getRet().getRET_MSG() : result1.getRet().getRET_MSG().substring(0, 1000));
			}			
			
			//记录失败日志
			this.txSaveCrdtLog(batch, PublicStaticDefineTab.CHANNEL_NO_EBK, "当前票据池低风险可用额度不足");
			
		}
	
	}
	
	/**
	 * 日志记录
	 * @param batch
	 * @param channel
	 * @param resultMsg
	 * @author Ju Nana
	 * @date 2021-7-20下午7:06:41
	 */
	private void txSaveCrdtLog(PlOnlineCrdt batch ,String channel,String resultMsg){
		/*
		 * 记录票据池池额度占用失败日志
		 */
		PedOnlineHandleLog log = new PedOnlineHandleLog();
		log.setBusiName("在线流贷申请调度池额度占用失败日志");     //业务名称   
		log.setTradeName(channel);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
		log.setTradeCode("PJC071");    //报文码  
		log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
		if(resultMsg.length() > 3000){
			log.setTradeResult(resultMsg.substring(0,2999));  //处理结果
		}else{
			log.setTradeResult(resultMsg);  //处理结果
		}
		log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
		log.setOperationType(PublicStaticDefineTab.OPERATION_TYPE_02);//岗位复核
		log.setBillNo(batch.getLoanNo());       //票号、批次号
		log.setBusiId(batch.getId());       //业务id  
		log.setOnlineNo(batch.getOnlineCrdtNo());     //在线协议编号
		log.setCustNumber(batch.getCustNo());   //客户号
		log.setBpsNo(batch.getBpsNo());
		onlineManageService.txSaveTrdeLog(log);
	}
	
	/**
	 * 在线流贷核心出账成功后票据池系统内操作：生成主业务合同、生成借据、登记资产、短信通知
	 * @param batch
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-18下午10:17:35
	 */
	private void txAfterSucc(PlOnlineCrdt batch) throws Exception{
		
		//生成合同
		logger.info("在线流贷【"+batch.getContractNo()+"】业务主业务合同生成...");
		onlineCommonService.txSaveCreditProduct(PublicStaticDefineTab.PRODUCT_002,batch,null);
		
		//生成借据
		logger.info("在线流贷【"+batch.getContractNo()+"】借据信息生成...");
		onlineCommonService.txSavePedCreditDetail(PublicStaticDefineTab.PRODUCT_002,batch.getTransAccount(),batch);
		
		 //生效支付计划
		this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_02);
		
		/*
		 * 短信通知
		 */
		logger.info("在线流贷【"+batch.getContractNo()+"】业务短信发送...");
		PedOnlineCrdtProtocol protocol = pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(batch.getOnlineCrdtNo());
		List<PedOnlineMsgInfo> msgs = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineCrdtNo(),null);
		
		if(null!=msgs){			
			for(PedOnlineMsgInfo msgInfo:msgs){
				try{					
					onlineManageService.txSendMsg(msgInfo.getAddresseeRole(),protocol.getCustName(),msgInfo.getAddresseePhoneNo(),PublicStaticDefineTab.PRODUCT_002,batch.getLoanAmt(),batch.getLoanAmt(),true,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
				}catch (Exception e) {
					logger.error("短信通知失败！", e);
					continue;
				}
			}
		}
		
		/*
		 * 最终完成的状态赋值
		 */
		batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_003);
		List<PlOnlineCrdt> batchList = new ArrayList<PlOnlineCrdt>();
		batchList.add(batch);
		financialAdviceService.txCreateList(batchList);
		//更新票据池已用额度
		onlineCommonService.txSavePedOnlineCrtdProtocol(batch);
	}
	/**
	 * 在线流贷失败后，将支付计划作废
	 * @param batch
	 * @author Ju Nana
	 * @date 2021-7-18上午2:51:03
	 */
	private void txFailChangePayPlan(PlOnlineCrdt batch,String status){
		List<PlCrdtPayPlan> list = pedOnlineCrdtService.queryPayPlanListByBatchContractNo(batch.getContractNo());
		//在线协议已用额度恢复:已用额度 - 本次业务金额
		PedOnlineCrdtProtocol protocol = pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(batch.getOnlineCrdtNo());
		protocol.setUsedAmt(protocol.getUsedAmt().subtract(batch.getLoanAmt()));
		pedOnlineCrdtService.txStore(protocol);
		batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
		pedOnlineCrdtService.txStore(batch);
		if(null != list){	
			List<PlCrdtPayPlan> storPlanList = new ArrayList<PlCrdtPayPlan>();
			for(PlCrdtPayPlan plan : list){
				plan.setStatus(status);
				storPlanList.add(plan);
			}
			pedOnlineCrdtService.txStoreAll(storPlanList);
		}
	}
	
	/**
	 * 本地额度占用成功后的处理
	 * @param batch
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-17下午9:48:55
	 */
	private void txOlineCreditApply002(PlOnlineCrdt batch) throws Exception{
		
		 //贷款产品代码检查
		
		 Ret deduCheckRet = this.deductionProductNoCheck(batch);
		 if(Constants.TX_SUCCESS_CODE.equals(deduCheckRet.getRET_CODE())){
			 batch.setDeductionProductNo(deduCheckRet.getRET_MSG());
		 }else{
			 return;
		 }
		
		
		/*
		 * 【2】信贷系统（额度系统）额度占用
		 */
		logger.info("在线流贷【"+batch.getContractNo()+"】业务额度系统额度占用...");
		ReturnMessageNew misResult  = pedOnlineCrdtService.txPJE021(batch,PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
		
		if(misResult.isTxSuccess()){
			/*
			 * 用独立事务保存状态
			 */
			batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_003);//额度占成功
			List<PlOnlineCrdt> batchList = new ArrayList<PlOnlineCrdt>();
			batchList.add(batch);
			financialAdviceService.txCreateList(batchList);
			
			/*
			 * 【3】核心系统放款申请
			 */
			logger.info("在线流贷【"+batch.getContractNo()+"】业务核心放款申请...");
			ReturnMessageNew coreResult  = pedOnlineCrdtService.txApplyMakeLoans(batch);//注意：如果出现异常，直接网上层抛出即可
			
			if(coreResult.isTxSuccess()){//核心放款成功
				
				/*
				 * 用独立事务保存状态
				 */
				batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004);//核心放款成功
				batchList = new ArrayList<PlOnlineCrdt>();
				batchList.add(batch);
				financialAdviceService.txCreateList(batchList);
				
				logger.info("在线流贷【"+batch.getContractNo()+"】业务核心放款成功，处理后续操作...");
				
				/*
				 * 放款成功后系统内数据处理
				 */
				this.txAfterSucc(batch);
				
			}else{//核心放款失败--有反馈的失败
				
				/*
				 * 用独立事务保存状态
				 */
				batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
				batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
				batchList = new ArrayList<PlOnlineCrdt>();
				batchList.add(batch);
				financialAdviceService.txCreateList(batchList);
				
				logger.info("在线流贷【"+batch.getContractNo()+"】业务核心放款失败，回滚额度系统已占用额度，回滚票据池额度...");
				
				/*
				 * 记录核心出账失败日志
				 */
	    		PedOnlineHandleLog log = new PedOnlineHandleLog();
	    		log.setBusiName("在线流贷申请核心放款失败");     //业务名称   
	    		log.setTradeName(PublicStaticDefineTab.CHANNEL_NO_EBK);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
	    		log.setTradeCode("PJC071");    //报文码  
	    		log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
	    		if(coreResult.getRet().getRET_MSG().length() > 3000){
	    			log.setTradeResult("在线流贷申请核心放款失败："+coreResult.getRet().getRET_MSG().substring(0,2999));  //处理结果
	    		}else{
	    			log.setTradeResult("在线流贷申请核心放款失败："+coreResult.getRet().getRET_MSG());  //处理结果
	    		}
	    		log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
	    		log.setOperationType(PublicStaticDefineTab.OPERATION_TYPE_02);//岗位复核
	    		log.setBillNo(batch.getLoanNo());       //票号、批次号
	    		log.setBusiId(batch.getId());       //业务id  
	    		log.setOnlineNo(batch.getOnlineCrdtNo());     //在线协议编号
	    		log.setCustNumber(batch.getCustNo());   //客户号
				log.setBpsNo(batch.getBpsNo());
	    		onlineManageService.txSaveTrdeLog(log);
	    		
	    		
				
	    		/*
	    		 * 用独立事务保存状态
	    		 */
				batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
				batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
				batchList = new ArrayList<PlOnlineCrdt>();
				batchList.add(batch);
				financialAdviceService.txCreateList(batchList);
				
				//额度系统额度释放
				ReturnMessageNew relsResult  = pedOnlineCrdtService.txPJE021(batch,PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_CANCEL);
				
				//票据池池额度释放
				if(relsResult.isTxSuccess()){//额度系统额度释放成功
					
					/*
		    		 * 回滚票据池额度
		    		 */
		    		
		    		//根据借据号查询资产登记表资产信息ID
		    		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getLoanNo(),null, PoolComm.VT_1);
		    		String id = crList.get(0).getBusiId();//因为在线流贷的借据有且只有一条，所以可以这样获取
		    		List<String> releseIds = new ArrayList<String>();
		    		releseIds.add(id);
		    		
		    		//额度回滚
		    		financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
		    		
		    		/*
					 * 作废支付计划
					 */
					this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
					
				}
						    		
			}
		}else{
			
			/*
			 * 用独立事务保存状态
			 */
			batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_003_1);
			batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
			List<PlOnlineCrdt> batchList = new ArrayList<PlOnlineCrdt>();
			batchList.add(batch);
			financialAdviceService.txCreateList(batchList);
			
			
			logger.info("在线流贷【"+batch.getContractNo()+"】业务额度系统额度占用失败，记录失败日志，回滚票据池额度...");
			
			/*
			 * 记录额度占用失败
			 */
    		PedOnlineHandleLog log = new PedOnlineHandleLog();
    		String failMsg = misResult.getRet().getRET_MSG();
			failMsg = failMsg.length()<1000?failMsg:failMsg.substring(0, 1000);
    		log.setBusiName("在线流贷申请额度系统额度占用失败");     //业务名称   
    		log.setTradeName(PublicStaticDefineTab.CHANNEL_NO_EBK);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
    		log.setTradeCode("PJC071");    //报文码  
    		log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
    		if(failMsg.length() > 3000){
    			log.setTradeResult("在线流贷申请额度系统额度占用失败:"+failMsg.substring(0,2999));  //处理结果
    		}else{
    			log.setTradeResult("在线流贷申请额度系统额度占用失败:"+failMsg);  //处理结果
    		}
    		log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
    		log.setOperationType(PublicStaticDefineTab.OPERATION_TYPE_02);//岗位复核
    		log.setBillNo(batch.getLoanNo());       //票号、批次号
    		log.setBusiId(batch.getId());       //业务id  
    		log.setOnlineNo(batch.getOnlineCrdtNo());     //在线协议编号
    		log.setCustNumber(batch.getCustNo());   //客户号
			log.setBpsNo(batch.getBpsNo());
    		onlineManageService.txSaveTrdeLog(log);
			
    		/*
    		 * 回滚票据池额度
    		 */
    		
    		//根据借据号查询资产登记表资产信息ID
    		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getLoanNo(),null, PoolComm.VT_1);
    		String id = crList.get(0).getId();//因为在线银承的借据有且只有一条，所以可以这样获取
    		List<String> releseIds = new ArrayList<String>();
    		releseIds.add(id);
    		
    		//额度回滚
    		financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
    		
    		/*
			 * 作废支付计划
			 */
			this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
			
		}
		
		
	}
	/**
	 * 信贷额度占用成功后的处理
	 * @param batch
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-17下午9:48:30
	 */
	private void txOlineCreditApply003(PlOnlineCrdt batch) throws Exception{//信贷额度占用成功
		
		 //贷款产品代码检查
		
		 Ret deduCheckRet = this.deductionProductNoCheck(batch);
		 if(Constants.TX_SUCCESS_CODE.equals(deduCheckRet.getRET_CODE())){
			 batch.setDeductionProductNo(deduCheckRet.getRET_MSG());
		 }else{
			 return;
		 }
		
		
		/*
		 * 查证
		 */
		ReturnMessageNew  checkRet = pedOnlineCrdtService.txApplyQueryAcct(batch);
		
		if(checkRet.isTxSuccess()){//核心已记账，则只处理票据池记账后续操作
			
			/*
			 * 放款成功后系统内数据处理
			 */
			this.txAfterSucc(batch);
			
		}else{//核心无记录，或者记账失败，直接作废
			
			/*
			 * 用独立事务保存状态
			 */
			batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
			batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
			List<PlOnlineCrdt> batchList = new ArrayList<PlOnlineCrdt>();
			batchList.add(batch);
			financialAdviceService.txCreateList(batchList);
			
			logger.info("在线流贷【"+batch.getContractNo()+"】业务核心放款失败，回滚额度系统已占用额度，回滚票据池额度...");
			
			/*
			 * 记录核心出账失败日志
			 */
    		PedOnlineHandleLog log = new PedOnlineHandleLog();
    		log.setBusiName("在线流贷申请核心放款失败");     //业务名称   
    		log.setTradeName(PublicStaticDefineTab.CHANNEL_NO_EBK);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
    		log.setTradeCode("PJC071");    //报文码  
    		log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
//    		log.setTradeResult("在线流贷申请核心放款失败："+checkRet.getRet().getRET_MSG());  //处理结果
    		log.setTradeResult("在线流贷申请核心放款失败：核心返回的错误原因");  //测试代码
    		log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
    		log.setOperationType(PublicStaticDefineTab.OPERATION_TYPE_02);//岗位复核
    		log.setBillNo(batch.getLoanNo());       //票号、批次号
    		log.setBusiId(batch.getId());       //业务id  
    		log.setOnlineNo(batch.getOnlineCrdtNo());     //在线协议编号
    		log.setCustNumber(batch.getCustNo());   //客户号
			log.setBpsNo(batch.getBpsNo());
    		onlineManageService.txSaveTrdeLog(log);
			
    		/*
    		 * 用独立事务保存状态
    		 */
			batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
			batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
			batchList = new ArrayList<PlOnlineCrdt>();
			batchList.add(batch);
			financialAdviceService.txCreateList(batchList);
			
			//额度系统额度释放
			ReturnMessageNew relsResult  = pedOnlineCrdtService.txPJE021(batch,PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_CANCEL);
						
			//票据池池额度释放
			if(relsResult.isTxSuccess()){//额度系统额度释放成功
				
				/*
	    		 * 回滚票据池额度
	    		 */
	    		
	    		//根据借据号查询资产登记表资产信息ID
	    		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getContractNo(),null, PoolComm.VT_1);
	    		if(null!=crList){	    			
	    			String id = crList.get(0).getBusiId();//因为在线流贷的借据有且只有一条，所以可以这样获取
	    			List<String> releseIds = new ArrayList<String>();
	    			releseIds.add(id);
	    			
	    			//额度回滚
	    			financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
	    			
	    		}
	    		/*
				 * 作废支付计划
				 */
				this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
				
			}
		}
		
	
	}
	/**
	 * 核心出账成功，票据池出现内部错误的情况——该情况应该只会在测试环境出现
	 * @param batch
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-19上午11:06:35
	 */
	private void txOlineCreditApply004(PlOnlineCrdt batch) throws Exception{
		/*
		 * 放款成功后系统内数据处理
		 */
		this.txAfterSucc(batch);
		
	}
	/**
	 * 核心出账失败，通讯交互失败情况
	 * @param batch
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-19上午11:06:35
	 */
	private void txOlineCreditApply0041(PlOnlineCrdt batch) throws Exception{
		/*
		 * 核心出账失败系统内数据处理
		 */
		//额度系统额度释放
		ReturnMessageNew relsResult  = pedOnlineCrdtService.txPJE021(batch,PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_CANCEL);
//		throw new Exception();
		
		//票据池池额度释放
		if(relsResult.isTxSuccess()){//额度系统额度释放成功
			
			/*
    		 * 回滚票据池额度
    		 */
    		
    		//根据借据号查询资产登记表资产信息ID
    		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getContractNo(),null, PoolComm.VT_1);
    		String id = crList.get(0).getBusiId();//因为在线流贷的借据有且只有一条，所以可以这样获取
    		List<String> releseIds = new ArrayList<String>();
    		releseIds.add(id);
    		
    		//额度回滚
    		financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
    		
    		/*
			 * 作废支付计划
			 */
			this.txFailChangePayPlan(batch,PublicStaticDefineTab.PAY_PLAN_04);
			
		}
		
	}
	
	/**
	 * 贷款产品代码检查
	 * @param batch
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-20下午7:23:26
	 */
	private Ret deductionProductNoCheck(PlOnlineCrdt batch){
		/*
		 * 注意：该场景比较特殊，注意如下说明
		 * 		 核心出账接口中需要传【产品代码】字段，该字段根据客户的【放款账号】到核心系统查回该账号对应的的【放款产品代码】（LoanProduct.LoanProduct）
		 *       然后根据放款产品代码查询到对应的【贷款产品代码】
		 *       所以首先查询校验产品代码信息，如果不通过，则直接不处理后续任务，只记录日志
		 */
		
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);//失败
		
		try {
			CoreTransNotes note = new CoreTransNotes();
			note.setAccNo(batch.getCreditAcct());//放款账行号
			note.setCurrentFlag("1");
			ReturnMessageNew result = poolCoreService.PJH716040Handler(note, "0");
			
			if(result.isTxSuccess()){
				//扣收账户状态
				String loanProductNo = (String) result.getBody().get("PRODUCT_NO");////放款产品代码
				String deductionProductNo = onlineCommonService.getDeductionProduct(loanProductNo);//获取贷款产品代码
				if(null == deductionProductNo){
					
					//记录失败日志
		    		this.txSaveCrdtLog(batch, PublicStaticDefineTab.CHANNEL_NO_CORE,"放款产品代码有误");
		    		return ret;
		    		
				}else{
					
					//成功
					ret.setRET_MSG(deductionProductNo);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					return ret;
					
				}
			}else{			
				
				//记录失败日志
	    		this.txSaveCrdtLog(batch, PublicStaticDefineTab.CHANNEL_NO_CORE,"核心放款账户信息查询失败");
	    		return ret;
			}
		} catch (Exception e) {
			
			//记录失败日志
    		this.txSaveCrdtLog(batch, PublicStaticDefineTab.CHANNEL_NO_CORE,"核心放款账户查询异常");
    		return ret;
		}
		
	}
	
	
	private Ret fullApplyCheck(PlOnlineCrdt batch){
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);//失败
		try {
			OnlineQueryBean queryBean=pedOnlineCrdtService.createOnlineCrdtApply(batch);
			ReturnMessageNew result=pedOnlineCrdtService.txCrdtApplyCheck(queryBean);
			if(!Constants.TX_SUCCESS_CODE.equals(result.getRet().getRET_CODE())){
				ret.setRET_MSG(result.getRet().getRET_MSG());
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				logger.info("在线流贷复核校验失败："+result.getRet().getRET_MSG());
				return ret;
			}else if(Constants.TX_SUCCESS_CODE.equals(result.getRet().getRET_CODE())){
				ret.setRET_MSG(result.getRet().getRET_MSG());
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				logger.info("在线流贷复核校验成功");
				return ret;
			}
		
	} catch (Exception e) {
		
		ret.setRET_MSG("在线流贷复核校验失败");
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		logger.info("在线流贷复核校验失败："+e.getMessage());
		return ret;
	}
		return ret;
	}	
}
