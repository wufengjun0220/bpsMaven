 package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.AutoTaskNoDefine;

/**
 * 在线流贷受托支付付款申请
 * @Description 在线流贷受托支付付款申请
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC060RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC060RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try {
        	List<PlCrdtPayList> payList = this.QueryParamMap(request);
        	if(null!=payList && payList.size()>0){
        		for(PlCrdtPayList cp : payList){
        			OnlineQueryBean queryBean = new  OnlineQueryBean();
        			queryBean.setOnlineCrdtNo(cp.getOnlineCrdtNo());//在线流贷协议编号
        			queryBean.setContractNo(cp.getContractNo());//在线流贷主业务合同号
        			queryBean.setSerialNo(cp.getSerialNo());//支付计划编号
	        		List<PlCrdtPayPlan> list = pedOnlineCrdtService.queryPlCrdtPayPlanListByBean(queryBean, null);
	        		if(null != list){
	        			PlCrdtPayPlan plan = list.get(0);
	        			cp = pedOnlineCrdtService.toCreatPlCrdtPayListByPlan(plan, cp);              
	        			cp.setTransChanel(PublicStaticDefineTab.CHANNEL_NO_EBK);//渠道-网银
	        			cp.setOperatorType(PoolComm.PAY_TYPE_0);//支付类型-支付
	        			cp.setStatus(PoolComm.PAY_STATUS_00);//初始化
	        			
	        			//落库及发送支付申请任务
	        			logger.info("支付记录划转，在线协议编号【"+cp.getOnlineCrdtNo()+"】主业务合同号【"+cp.getContractNo()+"】支付计划编号【"+cp.getSerialNo()+"】");
	        			pedOnlineCrdtService.txStore(cp);

	        		}else{
	        			logger.info("无此支付计划信息，在线协议编号【"+cp.getOnlineCrdtNo()+"】主业务合同号【"+cp.getContractNo()+"】支付计划编号【"+cp.getSerialNo()+"】");
	        			cp.setUpdateTime(new Date());
	        			cp.setStatus(PoolComm.PAY_STATUS_02);//支付失败
	        			cp.setPayDesc("票据池未查询到支付计划信息");//支付结果说明
	        			pedOnlineCrdtService.txStore(cp);
	        		}
        		}
        		
        		for(PlCrdtPayList pay : payList){
        		    autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_PAY_NO, pay.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_PAY, null, pay.getContractNo(), pay.getBpsNo(), null, null);
        		}
        		
        		
        	}else{
        		ret.setRET_CODE(Constants.TX_FAIL_CODE);
    			ret.setRET_MSG("支付申请列表为空！");
    			response.setRet(ret);
    	        return response;
        	}
        	
	        
			// 构建响应对象
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("已接收到您的支付申请，请稍后查询支付结果");
			
        } catch (Exception e) {
			logger.error("PJC060-在线流贷受托支付付款申请处理异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线流贷受托支付付款申请异常");
		}
		response.setRet(ret);
        return response;
    }

    /**
     * 将网银请求数据存入支付计划列表中
     * @param request
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-7-19下午8:30:08
     */
	private List<PlCrdtPayList> QueryParamMap(ReturnMessageNew request) throws Exception {
		
		List<PlCrdtPayList> payList = new ArrayList<PlCrdtPayList>(); 
		Map body = request.getBody();
		String onlinePro = getStringVal(body.get("ONLINE_LOAN_NO"));//在线流贷编号
		
		if(null !=request.getDetails() && request.getDetails().size()>0){
   			PlCrdtPayList info = null;
   			for(int i=0;i<request.getDetails().size();i++){
   				Map map = (Map) request.getDetails().get(i);
   				info = new PlCrdtPayList();
   				info.setOnlineCrdtNo(onlinePro);//在线流贷协议编号
   				info.setContractNo(getStringVal(map.get("PAY_INFO_ARRAY.ONLINE_BUSS_CONTRACT_NO"))); //在线业务合同号
   				info.setSerialNo(getStringVal(map.get("PAY_INFO_ARRAY.PAY_PLAN_NO")));//支付计划编号
   				info.setPayAmt(getBigDecimalVal(map.get("PAY_INFO_ARRAY.THIS_PAY_AMT")));//本次支付金额
   				info.setUsage(getStringVal(map.get("PAY_INFO_ARRAY.USAGE")));//用途
   				info.setPostscript(getStringVal(map.get("PAY_INFO_ARRAY.POSTSCRIPT")));//附言
   				payList.add(info);
   			}
		}
		return payList;
	}
}
