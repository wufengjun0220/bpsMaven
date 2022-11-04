package com.mingtech.application.autotask.taskdispatch;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

/**
 * 在线流贷支付计划提前还款/贷款归还任务
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-22
 * @copyright 北明明润（北京）科技有限责任公司
 */

public  class OnlineCrdtRepayAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineCrdtRepayAutoTaskDispatch.class);
	private PedOnlineCrdtService pedOnlineCrdtService =PoolCommonServiceFactory.getPedOnlineCrdtService();

	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		String flowNo = reqParams.get("flowNo");//payList表流水号
		BigDecimal totalRelsAmt = new BigDecimal(reqParams.get("totalRelsAmt"));//还款金额
		
		try{
			
			PedCreditDetail loan = (PedCreditDetail) pedOnlineCrdtService.load(autoTaskExe.getBusiId(),PedCreditDetail.class);//获取借据
			loan.setTaskDate(new Date());
			
			//支付计划未用还款
			pedOnlineCrdtService.txRepayOnlinePayPlan(loan, totalRelsAmt, flowNo);
			
		}catch (Exception e) {
			
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
			logger.error("在线流贷申请主任务OnlineCrdtAutoTaskDispatch调度执行异常...",e);
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		return resultMap;
	}

}
