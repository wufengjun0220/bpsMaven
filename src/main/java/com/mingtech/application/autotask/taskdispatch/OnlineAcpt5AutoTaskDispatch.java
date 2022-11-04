package com.mingtech.application.autotask.taskdispatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.service.OnlineCommonService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

public  class OnlineAcpt5AutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineAcpt5AutoTaskDispatch.class);
	private PedOnlineAcptService pedOnlineAcptService=PoolCommonServiceFactory.getPedOnlineAcptService();
	private AutoTaskPublishService autoTaskPublishService=PoolCommonServiceFactory.getAutoTaskPublishService();
	private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();

	/**
	 *<p>在线银承未用退回、额度释放<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 *@描述 
	 * 所有的count均为执行次数
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		String busiId = reqParams.get("busiId");//业务id
		PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(busiId,PlOnlineAcptDetail.class);
		detail.setTaskDate(new Date());
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
		
		if(AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01.equals(taskDispatchCfg.getQueueNode())){//未用退回
			if(autoTaskExe.getErrorCount()>0){
				try{
					//根据业务状态判断 是否需要重新执行
					String status = null;
					try{
						status = pedOnlineAcptService.txApplyQueryBill(detail,"1",PoolComm.NES_0142000);
					}catch (Exception e) {
						logger.error(busiId+"查询bbsp票据信息异常...",e);
						throw new Exception(busiId+"查询bbsp票据信息异常"+e.getMessage());
					}
					//判断状态
					if(StringUtils.isNotBlank(status) && "2".equals(status)){//未用退回成功
						logger.info("票号"+detail.getBillNo()+"未用退回成功！");
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010_1);
						detail.setCancelDate(new Date());
//						batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
						pedOnlineAcptService.txStore(batch);
						if(PublicStaticDefineTab.ONLINE_DS_003.equals(detail.getDealStatus())){
		        			detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_004);
		        		}
						logger.info("票号"+detail.getBillNo()+"未用退回成功，唤醒额度释放！");
						/**
						 * 唤醒额度释放
						 */
						autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), PublicStaticDefineTab.PRODUCT_001, null);
    	        		logger.info("票号"+detail.getBillNo()+"未用退回成功，唤醒额度释放！");
    	        		
    	        		
    	        		//未用退回成功唤醒银承业务明细状态及发生未用退回时金额统计
    	    			logger.info("未用退回成功唤醒银承业务明细状态及发生未用退回时金额统计");
    	    			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成
    	    		    Map<String,String> reqParams1 = new HashMap<String,String>();
    	    		    reqParams1.put("acptBatchId", batch.getId());
    	    		    reqParams1.put("acptId", detail.getId());
    	    		    reqParams1.put("source", "2");  //未用退回成功
    	    			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams1, batch.getBatchNo(), batch.getBpsNo(), null, null);
    	    			
					}else if(StringUtils.isNotBlank(status) && "3".equals(status)){//未用退回成功失败
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010_2);
						pedOnlineAcptService.txStore(detail);
						logger.info("票号"+detail.getBillNo()+"未用退回失败，第"+autoTaskExe.getErrorCount()+1+"轮执行未用退回开始！");
						boolean succ = pedOnlineAcptService.txApplyCancleBill(detail, batch);
						if(!succ){
							logger.info("票号"+detail.getBillNo()+"未用退回申请失败，第"+autoTaskExe.getErrorCount()+1+"轮执行！");
							resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
							resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
							return resultMap;
						}
					}else{//处理中
						logger.info("票号"+detail.getBillNo()+"未用退回申请处理中，第"+autoTaskExe.getErrorCount()+1+"轮执行！");
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
						return resultMap;
					}
					pedOnlineAcptService.txStore(detail);
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"未用退回异常...",e);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					return resultMap;
				}
			}else{//第一次执行该任务
				try{
					logger.info("票号"+detail.getBillNo()+"未用退回，第"+autoTaskExe.getErrorCount()+1+"轮执行开始！");
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
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"未用退回异常...",e);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					return resultMap;
				}
			}
			pedOnlineAcptService.txStore(detail);
		}else if(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO.equals(taskDispatchCfg.getQueueNode())){//额度释放
			try {
				List<String> releseIds = new ArrayList<String>();
				releseIds.add(detail.getId());
				/*
				 * 池额度释放
				 */
				financialService.txOnlineBusiReleseCredit(releseIds, detail.getBpsNo());
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_002_2);
				
				/*
				 * 通知信贷释放额度
				 */
				/*ReturnMessageNew result = pedOnlineAcptService.txPJE021Handler(batch, "1", PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE, null);
				if(!result.isTxSuccess()){
					detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
					pedOnlineAcptService.txStore(detail);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					logger.error(batch.getBatchNo()+"信贷额度释放失败...");
					return resultMap;
				}*/
				
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
				pedOnlineAcptService.txStore(detail);
				PedOnlineAcptProtocol pro = pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());//在线银承协议
				pro.setUsedAmt(pro.getUsedAmt().subtract(detail.getBillAmt()));//已用金额恢复:已用金额 - 本次业务金额
				pedOnlineAcptService.txStore(pro);

			} catch (Exception e) {
				pedOnlineAcptService.txStore(detail);
				logger.error("序列号："+detail.getBillSerialNo()+"在线银承业务单笔额度释放异常！");
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
