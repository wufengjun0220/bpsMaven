package com.mingtech.application.autotask.taskdispatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.taskService.AutoTaskPoolMSSService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

public  class PoolMSSAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(PoolMSSAutoTaskDispatch.class);
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
		//处理业务逻辑
		ReturnMessageNew response = new ReturnMessageNew();
		try {
			String busiId=autoTaskExe.getBusiId(); //业务id
			String query=autoTaskExe.getErrorCount()>0?"1":null;
			  Map<String,String>reqParamsMap = new HashMap<String,String>();
			    //将json请求参数，转换成MAP
			    if(StringUtils.isNotBlank(autoTaskExe.getReqParams())) {
			    	JSONObject jsonObj = JSON.parseObject(autoTaskExe.getReqParams());
			    	reqParamsMap = JSON.toJavaObject(jsonObj, Map.class);
			    }			
			    if(taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOL_MSS_TASK_NO)){ //短信申请发送
				AutoTaskPoolMSSService autoTaskPoolMSSService=new AutoTaskPoolMSSService();
				response=autoTaskPoolMSSService.txHandleRequest(busiId,query,reqParamsMap);
			}
			    if(response.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
				}else{
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, response.getRet().getRET_MSG());
				}
		} catch (Exception e) {
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			logger.error("【入池】调度任务执行异常：",e);
			return resultMap;		
		}
		
		logger.info("入池任务调度执行完毕...");
		return resultMap;
	}
}
