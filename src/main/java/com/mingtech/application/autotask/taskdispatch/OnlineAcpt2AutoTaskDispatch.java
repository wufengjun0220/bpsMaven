package com.mingtech.application.autotask.taskdispatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.StringUtil;

public  class OnlineAcpt2AutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineAcpt2AutoTaskDispatch.class);
	private PedOnlineAcptService pedOnlineAcptService=PoolCommonServiceFactory.getPedOnlineAcptService();
	private OnlineManageService onlineManageService=PoolCommonServiceFactory.getOnlineManageService();
	private AutoTaskPublishService autoTaskPublishService=PoolCommonServiceFactory.getAutoTaskPublishService();
    private AutoTaskExeService autoTaskExeService=PoolCommonServiceFactory.getAutoTaskExeService();


	/**
	 *<p>出票登记、承兑、提示收票自动任务<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 *@描述 出票自动任务：顺流程：出票登记-提示承兑申请-提示承兑签收-提示收票；执行逻辑：每次执行任务前判断改任务是否执行过，如果不是第一次执行，先去bbsp做状态查询；异常处理：出现异常要把异常捕捉，让任务能继续再次循环，同时保存之前的业务状态和处理状态；
	 * 业务失败：业务顺流程中断要先处理撤回动作，再结束任务
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		String busiId = autoTaskExe.getBusiId();//业务id
		String source = reqParams.get("source");//调度唤醒来源
		String query = null ;//是否进行查证 有值则 需要走查证
		
//		String transType = reqParams.get("transType");//业务类型
		PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(busiId,PlOnlineAcptDetail.class);
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
		
		//如果调度唤醒来源不为null 则取source,若果唤醒来源为null 则判断错误执行次数是否大于0 大于0取值
		query = StringUtil.isNotBlank(source) ? source : autoTaskExe.getErrorCount()>0?"1":null;

		//业务处理
		if(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO.equals(taskDispatchCfg.getQueueNode())){//出票登记
			
			resultMap = this.txHandlerRegister(busiId,query);
			
		}else if(AutoTaskNoDefine.POOL_ONLINE_ACPT_NO.equals(taskDispatchCfg.getQueueNode())){//承兑申请
			
			resultMap = this.txHandlerAcception(busiId,query);
			
		}else if(AutoTaskNoDefine.POOL_ONLINE_SIGN_NO.equals(taskDispatchCfg.getQueueNode())){//承兑签收
			
			resultMap = this.txHandlderAcceptionSign(busiId,query);
			
		}
		
		//处理批次
		pedOnlineAcptService.txSyncAcptBatchStatus(batch);
		return resultMap;
	}
	
	/**
	 * @Title txHandlerRegister
	 * @author wss
	 * @date 2021-7-14
	 * @Description 出票登记
	 * @return Map<String,String>
	 */
	private Map<String, String> txHandlerRegister(String busiId,String queryType) {
		Map<String,String> resultMap = new HashMap<String,String>();
		PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(busiId,PlOnlineAcptDetail.class);
		detail.setTaskDate(new Date());
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
		try {
			//非首次执行先做查询
			if(null!=queryType){
				try{
					//根据bbsp系统票据的业务状态判断
					String status = null;
					try{
						status = pedOnlineAcptService.txApplyQueryBill(detail,"1",PoolComm.NES_0012000);
					}catch (Exception e) {
						logger.error(busiId+"查询bbsp票据信息异常...",e);
						throw new Exception("查询bbsp票据信息异常"+e.getMessage());
					}
					//判断状态 
					if(StringUtils.isBlank(status)){//发送失败
						boolean  succ = pedOnlineAcptService.txApplyDrawBill(detail,batch);
						if(succ){
							//成功变更状态
							detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005);//出票登记申请
							pedOnlineAcptService.txStore(detail);
						}else{
							//记录日志
							onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "删除电票失败", "BBSP008", PublicStaticDefineTab.ACPT_BUSI_NAME_05, "send");
							//登记失败驱动电票删除新增信息  
							pedOnlineAcptService.txApplyDeleteBill(detail);
							//变更状态
							detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005_2);
							detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
							pedOnlineAcptService.txStore(detail);
//							batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
//							pedOnlineAcptService.txStore(batch);
							/**
							 * 发布额度释放
							 */
							Map<String, String> reqParam =new HashMap<String,String>();
							reqParam.put("busiId",detail.getId());
							reqParam.put("type", "2");//明细类型
							reqParam.put("isCredit", "1");//信贷释放额度
							reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
							autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam, null, null, null, null);
							/**
							 * 结束任务
							 */
							autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
	    	        		logger.info("票号"+detail.getBillNo()+"登记出票申请失败，结束任务！");
	    	        		
	    	        		
	    	        		//出票登记失败(相当于未用退回)唤醒银承业务明细状态及发生未用退回时金额统计
	    	    			logger.info("出票登记失败唤醒银承业务明细状态及发生未用退回时金额统计");
	    	    			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

	    	    		    Map<String,String> reqParams = new HashMap<String,String>();
	    	    		    reqParams.put("acptBatchId", batch.getId());
	    	    		    reqParams.put("acptId", detail.getId());
	    	    		    reqParams.put("source", "2");  //出票登记失败(相当于未用退回)
	    	    			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);

						}
					}else if("2".equals(status)){//登记成功
						//变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005_1);//出票登记成功
						pedOnlineAcptService.txStore(detail);
						//唤醒提示承兑申请
						autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_ACPT_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_ACPT, null);
		        		logger.info("票号"+detail.getBillNo()+"唤醒提示承兑申请！");
						
					}else if("3".equals(status)||"0".equals(status)){//登记失败
						//记录日志
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "出票登记失败", "30610006", PublicStaticDefineTab.ACPT_BUSI_NAME_05, "send");
						//1.驱动电票删除新增信息
						pedOnlineAcptService.txApplyDeleteBill(detail);
						//3.变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005_2);//出票登记失败
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
//						batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
//						pedOnlineAcptService.txStore(batch);
						/*
						 * 发布额度释放
						 */
						Map<String, String> reqParam =new HashMap<String,String>();
						reqParam.put("busiId",detail.getId());
						reqParam.put("type", "2");//明细类型
						reqParam.put("isCredit", "1");//信贷释放额度
						reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
						autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam, null, null, null, null);
						
						/**
						 * 结束任务
						 */
						autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
		        		logger.info("票号"+detail.getBillNo()+"登记出票申请失败，结束任务！");
		        		
		        		
		        		//出票登记失败(相当于未用退回)唤醒银承业务明细状态及发生未用退回时金额统计
		    			logger.info("出票登记失败唤醒银承业务明细状态及发生未用退回时金额统计");
		    			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

		    		    Map<String,String> reqParams = new HashMap<String,String>();
		    		    reqParams.put("acptBatchId", batch.getId());
		    		    reqParams.put("acptId", detail.getId());
		    		    reqParams.put("source", "2");  //出票登记失败(相当于未用退回)
		    			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);

						
					}else if( "1".equals(status)){//初始化、待处理
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
						return resultMap;
					}
					pedOnlineAcptService.txStore(detail);
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"出票登记异常...",e);
					onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, e.getMessage(), "30610006", PublicStaticDefineTab.ACPT_BUSI_NAME_05, "send");
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}else{//第一次执行该任务
				try {
					logger.info("票号"+detail.getBillNo()+"登记出票任务开始执行！");
					boolean succ = pedOnlineAcptService.txApplyDrawBill(detail,batch);
					if(succ){
						logger.info("票号"+detail.getBillNo()+"登记出票申请成功！");
						//变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005);//出票登记申请
					}else{
						logger.info("票号"+detail.getBillNo()+"登记出票申请失败！");
						//记录日志
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "出票登记失败", "30610006", PublicStaticDefineTab.ACPT_BUSI_NAME_05, "send");
						//1.驱动电票删除新增信息
						pedOnlineAcptService.txApplyDeleteBill(detail);
						logger.info("票号"+detail.getBillNo()+"登记出票申请失败，驱动bbsp删除信息！");
						//3.变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005_2);//出票登记失败
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
//						batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
//						pedOnlineAcptService.txStore(batch);
						/*
						 * 发布额度释放
						 */
						Map<String, String> reqParam =new HashMap<String,String>();
		    			reqParam.put("busiId",detail.getId());
		    			reqParam.put("type", "2");//明细类型
		    			reqParam.put("isCredit", "1");//信贷释放额度
		    			reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
						autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam, null, null, null, null);
						logger.info("票号"+detail.getBillNo()+"登记出票申请失败，发布额度释放！");
						/**
						 * 结束任务
						 */
						autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
		        		logger.info("票号"+detail.getBillNo()+"登记出票申请失败，结束任务！");
						
		        		
		        		//出票登记失败(相当于未用退回)唤醒银承业务明细状态及发生未用退回时金额统计
		    			logger.info("出票登记失败唤醒银承业务明细状态及发生未用退回时金额统计");
		    			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

		    		    Map<String,String> reqParams = new HashMap<String,String>();
		    		    reqParams.put("acptBatchId", batch.getId());
		    		    reqParams.put("acptId", detail.getId());
		    		    reqParams.put("source", "2");  //出票登记失败(相当于未用退回)
		    			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);

					}
					pedOnlineAcptService.txStore(detail);
				} catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"出票登记异常...",e);
					onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, e.getMessage(), "bbsp002", PublicStaticDefineTab.ACPT_BUSI_NAME_06, "send");
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}
		} catch (Exception e) {
			logger.error(busiId+"出票登记异常...",e);
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			return resultMap;
		}
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度执行完毕...");
		return resultMap;
	}
	
	/**
	 * @Title txHandlerAcception
	 * @author wss
	 * @date 2021-7-14
	 * @Description 提示承兑申请：每次进入方法，先判断任务是否执行过，如果执行过，先做业务状态查询
	 * @return Map<String,String>
	 */
	private Map<String, String> txHandlerAcception(String busiId,String queryType) {
		Map<String,String> resultMap = new HashMap<String,String>();
		PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(busiId,PlOnlineAcptDetail.class);
		detail.setTaskDate(new Date());
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
		try {
			//非首次执行先做查询
			if(null != queryType){
				try{
					//根据业务状态判断 是否需要重新执行
					String status = null;
					try{
						status = pedOnlineAcptService.txApplyQueryBill(detail,"1",PoolComm.NES_0022000);
					}catch (Exception e) {
						logger.error(busiId+"查询bbsp票据信息异常...",e);
						throw new Exception(busiId+"查询bbsp票据信息异常"+e.getMessage());
					}
					//判断BBSP状态
					if(StringUtils.isBlank(status)){//发送失败
						boolean succ = pedOnlineAcptService.txApplyAcception(detail,batch.getElctrncSign());
						if(succ){
							//成功变更状态
							detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006);//提示承兑申请
							pedOnlineAcptService.txStore(detail);
						}else{
							//变更状态
							detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006_2);//提示承兑申请失败
							detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
//							batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
							pedOnlineAcptService.txStore(batch);
							//记录日志
							onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "承兑申请失败", "BBSP004", PublicStaticDefineTab.ACPT_BUSI_NAME_07, "send");
							/*
							 * 发布未用退回
							 */
							autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_01, null, null, null, null, null);
							/**
							 * 结束任务
							 */
							autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ACPT_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
	    	        		logger.info("票号"+detail.getBillNo()+"提示承兑申请失败，结束任务！");
							
						}
					}else if("2".equals(status)){//承兑申请成功
						//变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006_1);//提示承兑申请成功
						pedOnlineAcptService.txStore(detail);
						//唤醒承兑签收
						autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_SIGN_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_SIGN, null);
		        		logger.info("票号"+detail.getBillNo()+"唤醒提示承兑签收！");
					}else if("3".equals(status)){//承兑申请失败
						//变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006_2);//承兑申请失败
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
						pedOnlineAcptService.txStore(detail);
//						batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
						pedOnlineAcptService.txStore(batch);
						//记录日志
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "承兑申请失败", "BBSP004", PublicStaticDefineTab.ACPT_BUSI_NAME_07, "send");
						/*
						 * 发布未用退回
						 */
						autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_01, null, null, null, null, null);
						/**
						 * 结束任务
						 */
						autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ACPT_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
		        		logger.info("票号"+detail.getBillNo()+"提示承兑申请失败，结束任务！");
						
					}else if("1".equals(status)){//
						logger.error(busiId+"提示承兑申请处理中...");
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
						return resultMap;
					}
					pedOnlineAcptService.txStore(detail);
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"提示承兑异常...",e);
					onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, e.getMessage(), "BBSP004", PublicStaticDefineTab.ACPT_BUSI_NAME_07, "send");
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}else{
				try{
					logger.info("票号"+detail.getBillNo()+"提示承兑申请任务开始执行！");
					boolean succ = pedOnlineAcptService.txApplyAcception(detail, batch.getElctrncSign());
					if(succ){
						logger.info("票号"+detail.getBillNo()+"提示承兑申请发送成功！");
						//成功变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006);//提示承兑申请
						pedOnlineAcptService.txStore(detail);
					}else{
						logger.info("票号"+detail.getBillNo()+"提示承兑申请失败！");
						//记录日志
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "承兑申请失败", "BBSP004", PublicStaticDefineTab.ACPT_BUSI_NAME_07, "send");
						//变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006_2);
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
						pedOnlineAcptService.txStore(detail);
						batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
						pedOnlineAcptService.txStore(batch);
						/*
						 * 发布未用退回
						 */
						autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_01, null, null, null, null, null);
						logger.info("票号"+detail.getBillNo()+"提示承兑申请失败，发布额度释放！");
						/*
						 * 结束任务
						 */
						autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ACPT_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
		        		logger.info("票号"+detail.getBillNo()+"提示承兑申请失败，结束任务！");
					}
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"提示承兑异常...",e);
					onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, e.getMessage(), "BBSP004", PublicStaticDefineTab.ACPT_BUSI_NAME_07, "send");
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}
		} catch (Exception e) {
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度执行完毕...");
		return resultMap;
	}

	/**
	 * @Title txHandlderAcceptionSign
	 * @author wss
	 * @date 2021-7-14
	 * @Description 提示承兑签收
	 * @return Map<String,String>
	 */
	private Map<String, String> txHandlderAcceptionSign(String busiId,String queryType) {
		Map<String,String> resultMap = new HashMap<String,String>();
		PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(busiId,PlOnlineAcptDetail.class);
		detail.setTaskDate(new Date());
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
		try {
			//非首次执行先做查询
			if(null != queryType){
				try{
					
					pedOnlineAcptService.txRepeatAcceptionSign(detail,batch);
					
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"承兑签收异常...",e);
					onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, e.getMessage(), "BBSP009", PublicStaticDefineTab.ACPT_BUSI_NAME_10, "send");
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}else{//第一次执行该任务
				try{
					logger.info("票号"+detail.getBillNo()+"提示承兑签收任务开始执行！");
					boolean succ = pedOnlineAcptService.txApplyAcptSign(detail,batch.getElctrncSign(),"0");
					if(succ){
						logger.info("票号"+detail.getBillNo()+"提示承兑签收申请成功！");
						//成功变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007);//提示承兑签收申请
					}else{
						logger.info("票号"+detail.getBillNo()+"提示承兑签收申请失败！");
						//记录日志
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "提示承兑签收申请失败", "BBSP009", PublicStaticDefineTab.ACPT_BUSI_NAME_10, "send");
						//变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_2);//提示承兑签收申请失败
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
//						batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
						pedOnlineAcptService.txStore(batch);
						/**
						 * 发布统一撤销
						 */
						autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_01, null, null, null, null, null);
						logger.info("票号"+detail.getBillNo()+"提示承兑签收申请失败，发布汇票撤销！");
						/**
						 * 结束任务
						 */
						autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_SIGN_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
		        		logger.info("票号"+detail.getBillNo()+"提示承兑签收申请失败，结束任务！");
					}
					pedOnlineAcptService.txStore(detail);
				}catch (Exception e) {
					detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_2);//提示承兑签收申请失败
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"承兑签收异常...",e);
					onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, e.getMessage(), "BBSP009", PublicStaticDefineTab.ACPT_BUSI_NAME_10, "send");
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}
		} catch (Exception e) {
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度执行完毕...");
		return resultMap;
	}
	
	

	
}
