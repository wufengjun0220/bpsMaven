package com.mingtech.application.autotask.taskdispatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.taskService.AutoTaskPoolInAccService;
import com.mingtech.application.autotask.taskService.AutoTaskPoolInEduService;
import com.mingtech.application.autotask.taskService.AutoTaskPoolInSendService;
import com.mingtech.application.autotask.taskService.AutoTaskPoolInService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.StringUtil;

public  class PoolInAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(PoolInAutoTaskDispatch.class);
	AutoTaskPublishService autoTaskPublishService =PoolCommonServiceFactory.getAutoTaskPublishService();
	PedAssetPoolService  pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	
	/**票据池入池
	 *<p>自动任务调度执行前调用的事件<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		String source = reqParams.get("source");//调度唤醒来源
		String query = null ;//是否进行查证 有值则 需要走查证
		//处理业务逻辑
		ReturnMessageNew response = new ReturnMessageNew();
		String busiId=autoTaskExe.getBusiId(); //业务id
		try {
			//如果调度唤醒来源不为null 则取source,若果唤醒来源为null 则判断错误执行次数是否大于0 大于0取值
			query = StringUtil.isNotBlank(source) ? source : autoTaskExe.getErrorCount()>0?"1":null;
//			String query=autoTaskExe.getErrorCount()>0?"1":null;
			if(taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOLIN_TASK_NO)){ //入池申请
				AutoTaskPoolInService autoTaskPoolInService=new AutoTaskPoolInService();
				response=autoTaskPoolInService.txHandleRequest(busiId,query);
			}else if( taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOLIN_SIGN_TASK_NO)){//入池通知签收
				AutoTaskPoolInSendService autoTaskPoolInSendService=new AutoTaskPoolInSendService();
				response=autoTaskPoolInSendService.txHandleRequest(busiId, query);
			}else if( taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOLIN_EDU_TASK_NO)){//入池额度占用
				AutoTaskPoolInEduService autoTaskPoolInEduService=new AutoTaskPoolInEduService();
				response=autoTaskPoolInEduService.txHandleRequest(busiId, query);
			}else if( taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOLIN_ACC_TASK_NO)){//入池记账
				AutoTaskPoolInAccService autoTaskPoolInAccService=new AutoTaskPoolInAccService();
				response=autoTaskPoolInAccService.txHandleRequest(busiId, query);
			}
//			if(response.getRet().getRET_CODE().equals(Constants.EBK_11)){
//				//重新唤醒记账任务
//				autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLIN_ACC_TASK_NO, busiId, AutoTaskNoDefine.BUSI_TYPE_JZ,reqParams);
//			}
			if(null!=response.getRet()){
				if(response.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
				}else{
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, response.getRet().getRET_MSG());
				}
			}else{
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, "未找到任务编号");
			}
		} catch (Exception e) {
			try {
				AssetPool ap = pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(draftPoolInService.loadByPoolInId(busiId).getPoolAgreement(),null,null);
				if(ap.getDealStatus().equals(PoolComm.DS_001)){
					//接口发生异常；自己已锁表，可以 解锁
					pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(ap.getApId(),true);
				}
			} catch (Exception e1) {
				logger.info(e1.getMessage());
			}
			
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			logger.error("【入池】调度任务执行异常：",e);
			return resultMap;		
		}
		
		logger.info("入池调度执行完毕...");
		return resultMap;
	}
}
