package com.mingtech.application.autotask.taskdispatch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

public  class OnlineAcpt3AutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineAcpt3AutoTaskDispatch.class);
	private PedOnlineAcptService pedOnlineAcptService=PoolCommonServiceFactory.getPedOnlineAcptService();

	/**
	 *<p>在线银提示收票<p/>
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
		//业务处理
		if(AutoTaskNoDefine.POOL_ONLINE_SEND_NO.equals(taskDispatchCfg.getQueueNode())){//提示收票
			if(autoTaskExe.getErrorCount()>0){
				try{
					//根据业务状态判断 是否需要重新执行
					String status = null;
					try{
						status = pedOnlineAcptService.txApplyQueryBill(detail,"1",PoolComm.NES_0032000);
					}catch (Exception e) {
						logger.error(busiId+"查询bbsp票据信息异常...",e);
						throw new Exception(busiId+"查询bbsp票据信息异常"+e.getMessage());
					}
					//判断状态
					if(StringUtils.isNotBlank(status) && "2".equals(status)){//提示收票申请成功
						boolean succ = pedOnlineAcptService.txApplyReceiveBill(detail,batch);
						if(succ){
							//成功变更状态
							detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_008);//提示收票申请
						}
					}else if(StringUtils.isBlank(status) || "3".equals(status)){//提示收票失败
						//再次发起
						try{
							boolean succ1 = pedOnlineAcptService.txApplyReceiveBill(detail,batch);
							if(succ1){
								//成功变更状态
								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_008);//提示收票申请
							}
						}catch (Exception e) {
							pedOnlineAcptService.txStore(detail);
							logger.error(busiId+"提示收票异常...",e);
							resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
							resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
							return resultMap;
						}
					}else{
						logger.info(detail.getBillNo()+"提示收票处理中! 第"+autoTaskExe.getErrorCount()+"轮处理");
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
						return resultMap;
					}
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"提示收票异常...",e);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					return resultMap;
				}
			}else{//第一次执行该任务
				try{
					boolean succ = pedOnlineAcptService.txApplyReceiveBill(detail,batch);
					if(succ){
						//成功变更状态
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_008);//提示收票申请
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_003);
					}
				}catch (Exception e) {
					pedOnlineAcptService.txStore(detail);
					logger.error(busiId+"提示收票异常...",e);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					return resultMap;
				}
			}
			pedOnlineAcptService.txStore(detail);
		}
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度执行完毕...");
		return resultMap;
	}
}
