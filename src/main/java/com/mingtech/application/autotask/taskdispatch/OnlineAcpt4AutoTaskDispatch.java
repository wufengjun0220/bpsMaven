package com.mingtech.application.autotask.taskdispatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;

public  class OnlineAcpt4AutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineAcpt4AutoTaskDispatch.class);
	private PedOnlineAcptService pedOnlineAcptService=PoolCommonServiceFactory.getPedOnlineAcptService();
	private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	private CreditRegisterService creditRegisterService = PoolCommonServiceFactory.getCreditRegisterService();


	/**
	 *<p>在线银承撤票<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 *@描述 
	 * 
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){
		
		Map<String,String> resultMap = new HashMap<String,String>();
		String busiId = reqParams.get("busiId");//业务id
		PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(busiId,PlOnlineAcptDetail.class);
		detail.setTaskDate(new Date());
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
		//业务处理
		if(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01.equals(taskDispatchCfg.getQueueNode())){//撤销
			if(autoTaskExe.getErrorCount()>0){
				resultMap = pedOnlineAcptService.txApplyRevokeCheck(detail, batch);
				if(resultMap != null){
					return resultMap;
				}
			}else{
				try{
                     //统一撤销
					logger.info(busiId+"撤销承兑申请开始执行...");
					boolean succ = pedOnlineAcptService.txApplyRevokeApply(detail, batch);
					if(succ){
						logger.info(busiId+"撤销承兑申请发送成功...");
						//变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_009);
						pedOnlineAcptService.txStore(detail);
					}else{
						logger.error(busiId+"撤销承兑申请失败...");
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
						return resultMap;
					}
					
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"撤销承兑申请异常...",e);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}
		}else if(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_02.equals(taskDispatchCfg.getQueueNode())){//未用退回
			if(autoTaskExe.getErrorCount()>0){
				resultMap = pedOnlineAcptService.txApplyUnusedCheck(detail, batch);
				if(resultMap != null){
					return resultMap;
				}
				
			}else{//第一次执行该任务
				try{
					logger.info("票号"+detail.getBillNo()+"未用退回，第"+(autoTaskExe.getErrorCount()+1)+"轮执行开始！");
					boolean succ = pedOnlineAcptService.txApplyCancleBill(detail, batch);
					if(!succ){
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010_2);
						pedOnlineAcptService.txStore(detail);
						logger.info("票号"+detail.getBillNo()+"未用退回申请失败，第"+autoTaskExe.getErrorCount()+1+"轮执行！");
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
						return resultMap;
					}else{
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010);
						pedOnlineAcptService.txStore(detail);
					}
				}catch (Exception e) {
					logger.error("票据id："+busiId+"未用退回异常...",e);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
					return resultMap;
				}
			}
		}else if(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_03.equals(taskDispatchCfg.getQueueNode())){//额度释放
			try {
				
					/*
					 * 池额度释放
					 */
				
					//根据借据号查询资产登记表资产信息ID
		    		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getContractNo(),detail.getLoanNo(), PoolComm.VT_1);
		    		List<String> releseIds = new ArrayList<String>();
		    		if(null!=crList&&!crList.isEmpty()){
		    			String id = crList.get(0).getBusiId();
		    			 releseIds.add(id);
		    		}

					financialService.txOnlineBusiReleseCredit(releseIds, detail.getBpsNo());
					detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_002_2);
					/*
					 * 通知信贷释放额度
					 */
					/*logger.info("票号"+detail.getBillNo()+"池额度释放成功信贷额度释放开始：票据未用退回时间："+detail.getCancelDate()+"当日时间："+DateUtils.getCurrDate()+"............................");
					if(DateUtils.formatDate(detail.getCancelDate(), DateUtils.ORA_DATES_FORMAT).compareTo(DateUtils.getCurrDate())==0){//当日出票任务通知信贷释放额度
						*//**
						 * 通知信贷释放额度时需要扣减合同金额
						 *//*
						batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
						
						ReturnMessageNew result = pedOnlineAcptService.txPJE021Handler(batch, "1", PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE, null);
						if(!result.isTxSuccess()){
							batch.setTotalAmt(batch.getTotalAmt().add(detail.getBillAmt()));
							detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
							pedOnlineAcptService.txStore(detail);
							resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
							resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
							logger.error(batch.getBatchNo()+"信贷额度释放失败...");
							return resultMap;
						}
		    			pedOnlineAcptService.txStore(batch);

					}*/

					detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
					pedOnlineAcptService.txStore(detail);
					PedOnlineAcptProtocol pro = pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());//在线银承协议
					pro.setUsedAmt(pro.getUsedAmt().subtract(detail.getBillAmt()));//已用金额恢复:已用金额 - 本次业务金额
					pedOnlineAcptService.txStore(pro);
			} catch (Exception e) {
				pedOnlineAcptService.txStore(detail);
				logger.error("票号："+detail.getBillNo()+"在线银承业务单笔额度释放异常！");
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
				return resultMap;
			}
		}
		//判断批次下的所有明细都已执行完毕
		pedOnlineAcptService.txSyncAcptBatchStatus(batch);
		
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度执行完毕...");
		return resultMap;
	}
}
