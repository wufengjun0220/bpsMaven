package com.mingtech.application.autotask.taskdispatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

public  class OnlineAcpt1AutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineAcpt1AutoTaskDispatch.class);
	private PedOnlineAcptService pedOnlineAcptService =PoolCommonServiceFactory.getPedOnlineAcptService();
	private OnlineManageService onlineManageService=PoolCommonServiceFactory.getOnlineManageService();
	private AutoTaskPublishService autoTaskPublishService=PoolCommonServiceFactory.getAutoTaskPublishService();
    private AutoTaskExeService autoTaskExeService=PoolCommonServiceFactory.getAutoTaskExeService();
    private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	private AssetRegisterService assetRegisterService = PoolCommonServiceFactory.getAssetRegisterService();
	private CreditRegisterService creditRegisterService = PoolCommonServiceFactory.getCreditRegisterService();
	private PedAssetPoolService pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();
	private PoolBailEduService poolBailEduService = PoolCommonServiceFactory.getPoolBailEduService();

	/**
	 *<p>自动任务调度执行事件<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 *@描述 在线银承申请复核岗：
	 *1. 执行的流程为全量校验业务，根据批次状态判断执行步骤，校验本地额度及额度占用，生成合同信息，去信贷系统风险探测及额度占用，生成借据信息，去bbsp批量新增,发布出票自动任务
	 *   如果因为异常执行失败，可以根据批次状态重复执行:
	 *2. 在线银承/流贷业务票据池校验规则及额度占用任一条件不通过，向信用风险管理系统发送风险探测
	 *    （包含授权查询交易）及额度查询交易，并分别记录MIS系统风险探测结果及额度系统额度擦查询结果
	 *			 
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		String batchId = autoTaskExe.getBusiId();//在线银承业务批次id
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(batchId,PlOnlineAcptBatch.class);
		batch.setTaskDate(new Date());
		List<PlOnlineAcptDetail> details = pedOnlineAcptService.queryOnlineAcptDetailByBatchId(batchId);
		try{
			if(PublicStaticDefineTab.ACPT_BATCH_001.equals(batch.getStatus())){//新增
				OnlineQueryBean queryBean =pedOnlineAcptService.createOnlineAcptApply(batch, details);
				ReturnMessageNew result =pedOnlineAcptService.txAcptCheckApply(queryBean);
				if(!Constants.TX_SUCCESS_CODE.equals(result.getRet().getRET_CODE())){
					pedOnlineAcptService.txFailChangeAcptDetail(batch,PublicStaticDefineTab.ACPT_DETAIL_012);//作废
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					logger.info("在线银承复核校验失败："+result.getRet().getRET_MSG());
					return resultMap;
				}
				/**
				 * 票据池池额度占用
				 */
				List<CreditRegister> crdtRegList = new ArrayList<CreditRegister>();
				Ret crdtCheckRet = new Ret();//额度校验返回对象
				boolean flag=false;
				PedOnlineAcptProtocol protocol = pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
    			if(new BigDecimal(100).compareTo(protocol.getDepositRatio()) ==0){
    				flag=true;
    			}
				if(!flag){//100%保证金不校验额度
						try {			
							PedProtocolDto dto = queryBean.getPool();
							AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
							String apId = ap.getApId();
							String bpsNo = dto.getPoolAgreement();
							for(PlOnlineAcptDetail detail : details){
								
								PedCreditDetail crdtDetail = pedOnlineAcptService.creatCrdtDetailByAcptDetail(detail, batch, dto);
								CreditRegister crdtReg = creditRegisterService.createCreditRegister(crdtDetail, dto, apId);
								crdtRegList.add(crdtReg);
							}
							//保证金同步
							BailDetail bail = poolBailEduService.queryBailDetailByBpsNo(bpsNo);
							//保证金资产登记处理
							assetRegisterService.txCurrentDepositAssetChange(bpsNo, bail.getAssetNb(), bail.getAssetLimitFree());
							//额度占用
							crdtCheckRet =  financialService.txCreditUsed(crdtRegList, dto);
							if(crdtCheckRet.getRET_CODE().equals(Constants.TX_FAIL_CODE)){//额度占用失败,在线协议已用额度恢复 :已用额度-业务的 金额
								protocol.setUsedAmt(protocol.getUsedAmt().subtract(batch.getTotalAmt()));
								financialService.txStore(protocol);
								//保存日志
								onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "", "票据池额度占用失败", "", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
								//变更状态  
								batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_007);//作废
								batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
								pedOnlineAcptService.txStore(batch);
								for(PlOnlineAcptDetail detail:details){
									detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
									detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);//信贷额度占用失败
									pedOnlineAcptService.txStore(detail);
								}
								//结束任务
								autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
								logger.info("批次号"+batch.getBatchNo()+"票据池额度占用失败，结束任务！");
								resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
								resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
								logger.info("自动任务调度执行完毕...");
								return resultMap;
							}
						} catch (Exception e) {
							crdtCheckRet.setRET_CODE(Constants.TX_FAIL_CODE);
							crdtCheckRet.setRET_MSG("票据池额度处理异常!");
							logger.error("在线银承OnlineAcpt1AutoTaskDispatch票据池额度占用异常!",e);
							//额度占用失败,在线协议已用额度恢复 :已用额度-业务的 金额
							protocol.setUsedAmt(protocol.getUsedAmt().subtract(batch.getTotalAmt()));
							financialService.txStore(protocol);
							ReturnMessageNew result1  = pedOnlineAcptService.txPJE021Handler(batch,PublicStaticDefineTab.PRODUCT_001,PublicStaticDefineTab.CREDIT_OPERATION_CHECK,null);
							if(result1.isTxSuccess()){
								onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "信贷","票据池额度处理异常! 但："+ result1.getRet().getRET_MSG(), "PJE021", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
							}
							//变更状态  
							batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_007);//作废
							batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
							pedOnlineAcptService.txStore(batch);
							for(PlOnlineAcptDetail detail:details){
								detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);//信贷额度占用失败
								pedOnlineAcptService.txStore(detail);
							}
							//结束任务
							autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
							logger.info("批次号"+batch.getBatchNo()+"票据池额度占用失败，结束任务！");
							resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
							resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
							logger.info("自动任务调度执行完毕...");
							return resultMap;
						}
					}
				
				if(flag||Constants.TX_SUCCESS_CODE.equals(crdtCheckRet.getRET_CODE())){
					
					/**
					 * 信贷额度占用
					 */
					ReturnMessageNew result1 =new ReturnMessageNew();
//					if(!flag){
						 result1  = pedOnlineAcptService.txPJE021Handler(batch,PublicStaticDefineTab.PRODUCT_001,PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE,null);
//					}
//					if(flag||result1.isTxSuccess()){
					if(result1.isTxSuccess()){
						batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_003);// 信贷额度占成功
						pedOnlineAcptService.txStore(batch);
						
						//【3】发送bbsp批量新增 
						ReturnMessageNew result2 = pedOnlineAcptService.txApplyNewBills(batch);
						if(result2.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
							logger.info(batch.getBatchNo()+"进来！");
							batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_004);
							pedOnlineAcptService.txStore(batch);
							
							//【4】发布出票主任务
							resultMap = this.publishTask(details);
							return resultMap;
						}else{
							logger.info(batch.getBatchNo()+"电票批量新增失败！");
							//保存日志
							onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, result2.getRet().getRET_MSG(), "BBSP001", PublicStaticDefineTab.ACPT_BUSI_NAME_04, "send");
							//新增失败   
							batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_004_1);
							batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
							pedOnlineAcptService.txStore(batch);
							for(PlOnlineAcptDetail detail:details){
								detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
//								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_004_1);//推送失败
								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);//作废
								pedOnlineAcptService.txStore(detail);
							}
							/**
							 * 释放额度(释放在线银承协议银承已用额度、票据池担保合同额度、票据池低风险额度、收票人额度)
							 */
							Map<String, String> reqParam =new HashMap<String,String>();
							reqParam.put("busiId",batch.getId());
							reqParam.put("type", "1");//类型 1：批次 2:明细
							reqParam.put("isCredit", "1");//是否释放信贷额度 1：是 2:否
							reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
							autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, batch.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam,batch.getBatchNo(), batch.getBpsNo(), null, null);
							logger.info(batch.getBatchNo()+"电票批量新增失败，发布额度释放！");
							//短信通知
							List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
							if(msgList != null){
								for(PedOnlineMsgInfo msgInfo:msgList){
									onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), false,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
								}
							}
							/**
							 * 结束任务
							 */
							autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
							logger.info("批次号"+batch.getBatchNo()+"电票批量新增失败，结束任务！");
						}
					}else{
						//保存日志
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "信贷", "风险探测失败"+result1.getRet().getRET_MSG(), "PJE021", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
						//变更状态  
						batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_003_1);
						pedOnlineAcptService.txStore(batch);
						for(PlOnlineAcptDetail detail:details){
							detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
							detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_003_1);//信贷额度占用失败
							pedOnlineAcptService.txStore(detail);
						}
						/**
						 * 释放额度(释放在线银承协议银承已用额度、票据池担保合同额度、票据池低风险额度、收票人额度)
						 */
						Map<String, String> reqParam =new HashMap<String,String>();
						reqParam.put("busiId",batch.getId());
						reqParam.put("type", "1");//类型 1：批次 2:明细
						reqParam.put("isCredit", "0");//是否释放信贷额度 0：否 1:是
						reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
						autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, batch.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam, batch.getBatchNo(), batch.getBpsNo(), null, null);
						//短信通知
						List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
						if(msgList != null){
							for(PedOnlineMsgInfo msgInfo:msgList){
								onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), false,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
							}
						}
						/**
						 * 结束任务
						 */
						autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
						logger.info("批次号"+batch.getBatchNo()+"信贷额度占用失败，结束任务！");
					}
				}else{
					logger.info("在线银承OnlineAcpt1AutoTaskDispatch票据池额度占用失败!");
					//变更状态  
					batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_002_1);
					batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
					pedOnlineAcptService.txStore(batch);
					for(PlOnlineAcptDetail detail:details){
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_002_1);//额度占用失败
						pedOnlineAcptService.txStore(detail);
					}
					ReturnMessageNew result1  = pedOnlineAcptService.txPJE021Handler(batch,PublicStaticDefineTab.PRODUCT_001,PublicStaticDefineTab.CREDIT_OPERATION_CHECK,null);
//					ReturnMessageNew result1 = new ReturnMessageNew();
//					result1.setTxSuccess(true);
					if(result1.isTxSuccess()){
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "信贷", "票据池额度占用失败", "PJE021", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
					}
					//短信通知
					List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
					if(msgList != null){
						for(PedOnlineMsgInfo msgInfo:msgList){
							onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), false,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
						}
					}
					/**
					 * 结束任务
					 */
					autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
					logger.info("批次号"+batch.getBatchNo()+"信贷额度占用失败，结束任务！");
				}
			
			}else if(PublicStaticDefineTab.ACPT_BATCH_003.equals(batch.getStatus())){//信贷额度占用成功
				ReturnMessageNew result2 = pedOnlineAcptService.txApplyNewBills(batch);
				if(result2.isTxSuccess()){
					batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_004);
					pedOnlineAcptService.txStore(batch);
					//发布出票主任务
					resultMap = this.publishTask(details);
					return resultMap;
				}
			}else if(PublicStaticDefineTab.ACPT_BATCH_004.equals(batch.getStatus())){//推送成功
				resultMap = this.publishTask(details);
				return resultMap;
			}
		}catch (Exception e) {
			pedOnlineAcptService.txStore(batch);
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			logger.error("【银承调度任务1】调度任务执行异常：",e);
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("【银承调度任务1】执行完毕！");
		return resultMap;
	}
	
	/**
	 * @Title 发布自动任务
	 * @author wss
	 * @date 2021-5-15
	 * @param details
	 */
	private Map publishTask(List<PlOnlineAcptDetail> details) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		for(PlOnlineAcptDetail detial:details){
			try {
				PoolQueryBean poolQueryBean = new PoolQueryBean();
				poolQueryBean.setBusiId(detial.getId());
				poolQueryBean.setProductId(AutoTaskNoDefine.BUSI_TYPE_ONLINE_REGISTER);
				AutoTaskExe task = autoTaskExeService.queryAutoTaskExeByParm(poolQueryBean);
				if(null == task){
					autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO, detial.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_REGISTER, null, detial.getLoanNo(), detial.getBpsNo(), null, null);
					logger.info("序列号"+detial.getBillSerialNo()+"出票登记任务发布成功！...");
				}
			} catch (Exception e) {
				logger.error("在线银承登记自动任务发布失败...",e);
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_TASK_PUBLISH+e.getMessage());
			}
		}
		return resultMap;
	}
}
