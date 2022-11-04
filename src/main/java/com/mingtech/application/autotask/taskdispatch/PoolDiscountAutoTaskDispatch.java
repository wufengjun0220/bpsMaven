package com.mingtech.application.autotask.taskdispatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.taskService.AutoTaskPoolDiscountEduService;
import com.mingtech.application.autotask.taskService.AutoTaskPoolDiscountSendService;
import com.mingtech.application.autotask.taskService.AutoTaskPoolDiscountSignService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.StringUtil;

public  class PoolDiscountAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(PoolDiscountAutoTaskDispatch.class);
    
	/**票据池出池
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
		try {
			String busiId=autoTaskExe.getBusiId(); //业务id
			
			//如果调度唤醒来源不为null 则取source,若果唤醒来源为null 则判断错误执行次数是否大于0 大于0取值
			query = StringUtil.isNotBlank(source) ? source : autoTaskExe.getErrorCount()>0?"1":null;
//			String query=autoTaskExe.getErrorCount()>0?"1":null;
			if(taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOLDIS_EDU_TASK_NO)){ //强贴额度校验主任务
				AutoTaskPoolDiscountEduService autoTaskPoolDiscountEduService=new AutoTaskPoolDiscountEduService();
				response=autoTaskPoolDiscountEduService.txHandleRequest(busiId, query);
			}else if(taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOLDIS_SEND_TASK_NO)){ //强贴申请子任务
				AutoTaskPoolDiscountSendService autoTaskPoolDiscountSendService=new AutoTaskPoolDiscountSendService();
				response=autoTaskPoolDiscountSendService.txHandleRequest(busiId, query);
			}else if( taskDispatchCfg.getQueueNode().equals(AutoTaskNoDefine.POOLDIS_SIGN_TASK_NO)){//强贴申请记账子任务
				AutoTaskPoolDiscountSignService autoTaskPoolDiscountSignService=new AutoTaskPoolDiscountSignService();
				response=autoTaskPoolDiscountSignService.txHandleRequest(busiId, query);
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
			logger.error("【票据池贴现】调度任务执行异常：",e);
			return resultMap;		
		}
		
		logger.info("票据池贴现自动任务调度执行完毕...");
		return resultMap;
	}
}
