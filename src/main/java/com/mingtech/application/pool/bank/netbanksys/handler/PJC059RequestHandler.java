package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @Description 在线流贷资金支付计划查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC059RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC059RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private  CommonQueryService commonQueryService;
	@Autowired
	private PoolCreditProductService poolCreditProductService ;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;

	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try {
        	OnlineQueryBean queryBean = QueryParamMap(request);
        	Page page = getPage(request.getAppHead());
        	queryBean.setStatusDesc(PoolComm.JJ_04);
        	List result = pedOnlineCrdtService.queryPlCrdtPayPlanUncleared(queryBean,page);
			QueryResult results =commonQueryService.loadDataByResult(result, "totalAmt");

			if (result != null && result.size() > 0) {
				setPage(response.getAppHead(), page);
				List addList = new ArrayList();
				String onlineCrdtNo="";//在线业务协议编号
				Map addMap = null;
				for (int i = 0; i < result.size(); i++) {
					PlCrdtPayPlan plan = (PlCrdtPayPlan) result.get(i);
					PedCreditDetail detail = null;
					if(StringUtil.isNotBlank(queryBean.getPayeeSerialNo())){//支付计划不为空时（有且只查出一条），需要同步借据信息，并实时返回【可释放金额总额】
		        		
						//根据支付计划中的借据号，查询借据信息，并同步核心借据状态
						detail = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(null, plan.getLoanNo());
						draftPoolQueryService.txUpdateLoanByCoreforQuery(detail);
						
						//查回更新后借据
						detail = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(null, plan.getLoanNo());
						
		        	}
					
					onlineCrdtNo = plan.getOnlineCrdtNo();
					addMap = new HashMap();
					addMap.put("PAY_INFO_ARRAY.PAY_PLAN_NO", plan.getSerialNo());// 支付计划编号
					addMap.put("PAY_INFO_ARRAY.ONLINE_BUSS_CONTRACT_NO", plan.getContractNo());// 在线业务合同号
					addMap.put("PAY_INFO_ARRAY.LOAN_ACCT_NO", plan.getLoanAcctNo());// 放款账户账号
					addMap.put("PAY_INFO_ARRAY.LOAN_ACCT_NAME", plan.getLoanAcctName());// 放款账户名称
					addMap.put("PAY_INFO_ARRAY.REMAINING_PAY_AMT", plan.getTotalAmt().subtract(plan.getRepayAmt()).subtract(plan.getUsedAmt()));// 剩余待支付金额 = 支付计划总额-取消支付总额 - 已支付总额 
					addMap.put("PAY_INFO_ARRAY.PAY_TOTAL_AMT", plan.getTotalAmt().subtract(plan.getRepayAmt()));// 待支付总额：支付计划总额-取消支付总额
					String status = plan.getStatus();//状态0：已支付1：未支付
					if(status.equals(PublicStaticDefineTab.PAY_PLAN_03)){//已支付
						addMap.put("PAY_INFO_ARRAY.PAY_STATUS", "0");
					}else if(status.equals(PublicStaticDefineTab.PAY_PLAN_02)){//可支付
						addMap.put("PAY_INFO_ARRAY.PAY_STATUS", "1");
						
					}
//					addMap.put("PAY_INFO_ARRAY.PAY_STATUS", plan.getStatus());// 状态
					addMap.put("PAY_INFO_ARRAY.PAYEE_CLIENT_NAME", plan.getDeduAcctName());// 收款人名称
					addMap.put("PAY_INFO_ARRAY.PAYEE_ACCT_NO", plan.getDeduAcctNo());// 收款人账号
					addMap.put("PAY_INFO_ARRAY.PAYEE_BANK_NO", plan.getDeduBankCode());// 收款人开户行行号
					addMap.put("PAY_INFO_ARRAY.PAYEE_BANK_NAME", plan.getDeduBankName());//收款人开户行名称
					
					if(StringUtil.isNotBlank(queryBean.getPayeeSerialNo())){
						//该合同下所有支付计划总和
						PlCrdtPayPlan allPlanAmt =  pedOnlineCrdtService.queryAllPlanAmt(plan.getContractNo());
						//支付计划理论可修改金额 = 支付总额-取消支付金额
						BigDecimal planCanRelsAmt = allPlanAmt.getTotalAmt().subtract(allPlanAmt.getRepayAmt());
						BigDecimal canRelsAmt = planCanRelsAmt.subtract(detail.getActualAmount());//可释放金额总额 = 支付计划总额 - 取消支付的金额 - 借据余额
						canRelsAmt = canRelsAmt.compareTo(BigDecimal.ZERO)>0 ? canRelsAmt : BigDecimal.ZERO;
						addMap.put("PAY_INFO_ARRAY.ALLOW_RELEASE_TOTAL_AMT", canRelsAmt);// 可释放金额总额
					}
					
					addList.add(addMap);
					}
    			setPage(response.getAppHead(), page,results.getTotalAmount().toString());
	 	        response.getBody().put("ONLINE_LOAN_NO", onlineCrdtNo);      //在线业务协议编号     
				response.setDetails(addList);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
			} else {
			    ret.setRET_MSG("无符合条件数据");
				ret.setRET_CODE(Constants.EBK_03);
			}
        } catch (Exception e) {
			logger.error("PJC059-在线流贷资金支付计划查询异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线流贷资金支付计划查询异常");
		}

		
		response.setRet(ret);
        return response;
    }

	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		OnlineQueryBean queryBean = new OnlineQueryBean();
		Map map = request.getBody();
        queryBean.setCustNumber(getStringVal(map.get("CORE_CLIENT_NO")));//核心客户号
        queryBean.setEbkCustNo(getStringVal(map.get("CMS_CLIENT_NO")));//网银客户号
        queryBean.setOnlineCrdtNo(getStringVal(map.get("ONLINE_LOAN_NO")));//在线流贷编号
        queryBean.setContractNo(getStringVal(map.get("ONLINE_BUSS_CONTRACT_NO")));//在线业务合同号
        queryBean.setPayeeSerialNo(getStringVal(map.get("PAY_PLAN_NO")));//支付计划编号
        queryBean.setDeduAcctName(getStringVal(map.get("PAYEE_CLIENT_NAME")));//收款人名称
        queryBean.setStartAmt(getBigDecimalVal(map.get("MIN_PAY_AMT")));//支付金额上限
        queryBean.setEndAmt(getBigDecimalVal(map.get("MAX_PAY_AMT")));//支付金额下限
        String status = getStringVal(map.get("PAY_STATUS"));//状态0：已支付1：未支付
        
        queryBean.setOperatorType(status);
        
        //根据网银调整：
        // 0：已支付
        // 1：未支付
        // 2：部分支付
        // 3：全部
        
        if(status.equals("0")){//已支付   剩余支付金额为0
        	queryBean.setStatus(PublicStaticDefineTab.PAY_PLAN_03);
        }else if(status.equals("1")){//未支付  已支付金额为0
        	queryBean.setStatus(PublicStaticDefineTab.PAY_PLAN_02);
        }else if(status.equals("2")){ //部分支付   剩余支付金额不为0  已支付金额不为0
        	queryBean.setStatus(PublicStaticDefineTab.PAY_PLAN_02);
        }else{
//        	List list = new ArrayList();
//        	list.add(PublicStaticDefineTab.PAY_PLAN_03);
//        	list.add(PublicStaticDefineTab.PAY_PLAN_02);
//        	queryBean.setStatuList(list);
        }
		return queryBean;
	}

}
