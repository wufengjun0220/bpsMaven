package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.AutoTaskNoDefine;

/**
 * @Description 在线流贷受托支付计划取消（还款）
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC062RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC062RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("处理失败！");

		OnlineQueryBean queryBean = QueryParamMap(request);
		String contractNo = queryBean.getContractNo();//主业务合同号
		String onlineCrdtNo = queryBean.getOnlineCrdtNo();//在线协议编号
		BigDecimal totalRelsAmt = queryBean.getTotalAmt();//本次释放总金额--还借据的金额
		String flowNo = queryBean.getFlowNo();//流水号
		
        try {
        	
        	// 主业务合同
			CreditProduct product = poolCreditProductService.queryProductByCreditNo(contractNo,null);// 主业务合同 
			
			//查询借据--在线流贷合同下只有一笔借据
			PedCreditDetail loan = null;
			CreditQueryBean queryLoanBean = new CreditQueryBean();
			queryLoanBean.setCrdtNo(product.getCrdtNo());
			List<PedCreditDetail> loanList =  poolCreditProductService.queryCreditDetailList(queryLoanBean); 
			if(null != loanList){
				loan = loanList.get(0);
			}
			
			//同步借据信息，并更新最新的额度信息
			loan = poolCreditProductService.txSynchroLoan(loan, product, PoolComm.XDCP_LD);
        	
			if(totalRelsAmt.compareTo(loan.getActualAmount())>0){//若释放金额大于当前借据实际金额，则不允许
				ret.setRET_MSG("还借据的金额大于该笔业务的实际占用金额！");
				response.setRet(ret);
				return response;
			}
        	
        	List<PlCrdtPayList> payList = (List<PlCrdtPayList>)queryBean.getDetalis();
        	List<PlCrdtPayList> storePayList = new ArrayList<PlCrdtPayList>();
			if(null != payList && payList.size()>0){
				for(PlCrdtPayList pay : payList){
        			OnlineQueryBean queryPlanBean = new  OnlineQueryBean();
        			queryPlanBean.setOnlineCrdtNo(onlineCrdtNo);//在线流贷协议编号
        			queryPlanBean.setContractNo(contractNo);//在线流贷主业务合同号
        			queryPlanBean.setSerialNo(pay.getSerialNo());//支付计划编号
        			List<PlCrdtPayPlan> list = pedOnlineCrdtService.queryPlCrdtPayPlanListByBean(queryPlanBean, null);
        			//支付列表组装
        			PlCrdtPayPlan plan = list.get(0);//有且只有一条
        			pay = pedOnlineCrdtService.toCreatPlCrdtPayListByPlan(plan, pay);
        			pay.setTransChanel(PublicStaticDefineTab.CHANNEL_NO_EBK);//渠道-网银
        			pay.setOperatorType(PoolComm.PAY_TYPE_1);//支付类型-还款
        			pay.setStatus(PoolComm.PAY_STATUS_00);//初始化
        			storePayList.add(pay);


				}
				
				//支付列表组装
				pedOnlineCrdtService.txStoreAll(storePayList);
				
				//将支付计划还款申请发布到队列中
				logger.info("网银支付计划贷款未用归还任务发布，在线协议编号【"+onlineCrdtNo+"】主业务合同号【"+contractNo+"】处理流水号【"+flowNo+"】");
				Map<String,String> reqParams = new HashMap<String,String>();
				reqParams.put("totalRelsAmt", totalRelsAmt.toString());//释放总金额
				reqParams.put("flowNo", flowNo);//流水号
				autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_REPAY_NO, loan.getCreditDetailId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_REPAY, reqParams, loan.getLoanNo(), loan.getBpsNo(), null, null);
				
				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);//成功
				ret.setRET_MSG("支付计划贷款未用归还申请受理成功，请稍后查询归还结果。");
				response.setRet(ret);
				return response;
				
			}else{
				ret.setRET_MSG("票据池系统未收到贷款未用归还信息！");
				response.setRet(ret);
				return response;
			}
        	
	        
        } catch (Exception e) {
			logger.error("合同号:"+queryBean.getContractNo()+"在线流贷受托支付计划取消（还款）失败", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线流贷受托支付支付计划取消（还款）异常，请联系票据池系统！");
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
		OnlineQueryBean bean = new OnlineQueryBean();
		Map body = request.getBody();
		bean.setOnlineCrdtNo(getStringVal(body.get("ONLINE_LOAN_NO")));//在线流贷编号
		bean.setContractNo(getStringVal(body.get("ONLINE_BUSS_CONTRACT_NO"))); //在线业务合同号
		BigDecimal totalRelsAmt = BigDecimal.ZERO;//本次释放总金额
		String flowNo = "PAY-"+Long.toString(System.currentTimeMillis());//流水号，用来标记该批申请
		
		if(null !=request.getDetails() && request.getDetails().size()>0){
   			PlCrdtPayList info = null;
   			for(int i=0;i<request.getDetails().size();i++){
   				Map map = (Map) request.getDetails().get(i);
   				info = new PlCrdtPayList();
   				info.setSerialNo(getStringVal(map.get("PAY_INFO_ARRAY.PAY_PLAN_NO")));//支付计划编号
   				BigDecimal relsAmt = getBigDecimalVal(map.get("PAY_INFO_ARRAY.RELEASE_AMT"));//可释放金额
   				info.setPayAmt(relsAmt);//释放金额
   				info.setRepayFlowNo(flowNo);//流水号
   				info.setUsage("网银-贷款未用归还");//用途
   				info.setPostscript("网银-贷款未用归还");//附言
   				totalRelsAmt = totalRelsAmt.add(relsAmt);
   				bean.getDetalis().add(info);
   			}
		}
		bean.setFlowNo(flowNo);
		bean.setTotalAmt(totalRelsAmt);
		return bean;
	}

}
