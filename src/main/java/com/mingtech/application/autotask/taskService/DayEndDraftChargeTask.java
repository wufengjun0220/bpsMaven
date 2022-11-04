package com.mingtech.application.autotask.taskService;

import java.util.Date;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 自动发起票据到期扣款日终任务
 * 
 * @author bjm
 * 配置为 每日上午10点自动执行 
 * 0 0 10 ? * *
 * com.mingtech.application.autotask.taskService.DayEndDraftChargeTask
 */
public class DayEndDraftChargeTask  extends AbstractAutoTask {

	private static final Logger logger = Logger.getLogger(DayEndDraftChargeTask.class);
	
	@Override
	public BooleanAutoTaskResult run() throws Exception {
		//获取当期系统工作日期
		Date wkDate = DateUtils.getWorkDayDate();
		//电票到期扣款
		this.chargeForDraft(wkDate);
		//纸票到期扣款
		this.chargeForPaperDraft(wkDate);
		
		return new BooleanAutoTaskResult(true);
	}

	/**
	 * <P>方法名称：chargeForDraft|描述：电票到期扣款</P>
	 * @param wkDate 当前系统工作日期
	 */
	private void chargeForDraft(Date wkDate){
		//根据Spring配置获取上下文服务
		/*AcceptanceService acceptanceService = (AcceptanceService) SpringContextUtil.getBean("acceptanceService");
		try{
			List result = acceptanceService.queryChargeBillListByStatus(wkDate);
			for(int i=0;i<result.size();i++){
				AcceptionDto acception = (AcceptionDto) result.get(i);
				acceptanceService.txAutoAcceptionCharging(acception);
			}
		}catch(Exception e){
			logger.error(e,e);
		}*/
	}
	
	/**
	 * <P>方法名称：chargeForPaperDraft|描述：纸票到期扣款</P>
	 * @param wkDate 当前系统工作日期
	 */
	private void chargeForPaperDraft(Date wkDate){
		//根据Spring配置获取上下文服务
		/*PcdsAcceptanceService pcdsAcceptanceService = (PcdsAcceptanceService) SpringContextUtil.getBean("pcdsAcceptanceService");
		try{
			List result = pcdsAcceptanceService.queryChargeBillListByStatus(wkDate);
			for(int i=0;i<result.size();i++){
				AcceptionDto acception = (AcceptionDto) result.get(i);
				pcdsAcceptanceService.txAutoAcceptionCharging(acception);
			}
		}catch(Exception e){
			logger.error(e,e);
		}*/
	}
	
	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

	
}
