package com.mingtech.application.autotask.taskdispatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

/**
 * 额度计算自动任务
 * @author Ju Nana
 * @version v1.0
 * @date 2021-9-13
 * @copyright 北明明润（北京）科技有限责任公司
 */
public  class CreditCalculationAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(CreditCalculationAutoTaskDispatch.class);
	private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();

	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		
		try {
			String proId=reqParams.get("proId"); //票据池协议表ID 
			PedProtocolDto pro = (PedProtocolDto)financialService.load(proId, PedProtocolDto.class);
			
			//额度更新
			financialService.txCreditCalculationByProtocol(pro);
			
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);

		} catch (Exception e) {
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			logger.error("【额度计算】调度任务执行异常：",e);
			return resultMap;		
		}
	
		return resultMap;
	}
}
