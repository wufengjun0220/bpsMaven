package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;

/**
 * @Description 在线流贷受托支付计划修改
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC061RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC061RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
    	
    	/*
    	 *	注意：经过业务确认，支付计划修改支持单笔修改 
    	 */
    	
    	Ret ret = new Ret();
    	ret.setRET_CODE(Constants.TX_FAIL_CODE);//默认失败
    	ret.setRET_MSG("支付计划修改处理失败！");

    	
        ReturnMessageNew response = new ReturnMessageNew();
        OnlineQueryBean queryBean = QueryParamMap(request);        
        String onlineCrdtNo = queryBean.getOnlineCrdtNo();//在线流贷协议编号
        String contractNo = queryBean.getContractNo();//在线业务合同号
        String serialNo = queryBean.getSerialNo();//支付计划编号
        BigDecimal rlsAmt =  queryBean.getRepayAmt();//释放金额
		
		if(rlsAmt.compareTo(BigDecimal.ZERO)<0){
			ret.setRET_MSG("释放金额不得为负数！");
			response.setRet(ret);
	        return response;
		}
		
		try {
			
			//查询支付计划
			OnlineQueryBean bean = new  OnlineQueryBean();
			bean.setOnlineCrdtNo(onlineCrdtNo);//在线流贷协议编号
			bean.setContractNo(contractNo);//在线流贷主业务合同号
			bean.setSerialNo(serialNo);//支付计划编号
			List<PlCrdtPayPlan> list = pedOnlineCrdtService.queryPlCrdtPayPlanListByBean(bean, null);

    		if(null != list){
    			
    			//修改的支付计划
    			PlCrdtPayPlan plan = list.get(0);
    			ret = pedOnlineCrdtService.txModifyOnlinePayPlan(plan, rlsAmt);

    		}else{
    			logger.error("PJC061-在线流贷受托支付支付计划修改失败,未查询到支付计划信息！");
    			ret.setRET_CODE(Constants.TX_FAIL_CODE);
    			ret.setRET_MSG("在线流贷受托支付支付计划修改失败,未查询到支付计划信息！");
    			
    		}
		} catch (Exception e) {
			logger.error("PJC061-在线流贷受托支付支付计划修改失败", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线流贷受托支付支付计划修改异常");
		}

		response.setRet(ret);
        return response;
    }
    
    /**
	 * @Description: 请求数据处理
	 * @param request
	 * @return OnlineQueryBean
	 * @author wss
	 * @date 2021-5-08
	 */
	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		
		/*
		 * 按照传过来单笔处理，因为修改只允许修改单笔
		 */
		
		OnlineQueryBean bean = new OnlineQueryBean();
		Map body = request.getBody();
		bean.setOnlineCrdtNo(getStringVal(body.get("ONLINE_LOAN_NO")));//在线流贷编号 必输
		bean.setContractNo(getStringVal(body.get("ONLINE_BUSS_CONTRACT_NO"))); //在线业务合同号 必输
		
		if(null !=request.getDetails() && request.getDetails().size()>0){
   			for(int i=0;i<request.getDetails().size();i++){
   				Map map = (Map) request.getDetails().get(0);
   				bean.setSerialNo(getStringVal(map.get("PAY_INFO_ARRAY.PAY_PLAN_NO")));//支付计划编号
   				bean.setRepayAmt(getBigDecimalVal(map.get("PAY_INFO_ARRAY.RELEASE_AMT")));//释放金额
   			}
		}
		return bean;
	}
	

}
