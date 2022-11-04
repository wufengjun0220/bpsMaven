package com.mingtech.application.pool.bank.creditsys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.framework.core.page.Page;

/**
 * @Title: MIS接口 PJE010
 * @Description: 贴现结果查询接口
 * @author xie cheng
 * @date 2019-05-23
 */
public class PJE010CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE010CreditHandler.class);
	@Autowired
	private PoolCreditService poolCreditService;

	/**
	 * MIS接口 PJE010 贴现结果查询接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		QueryResult result = null;
		Ret ret = new Ret();
		try {
			PoolQueryBean queryBean = QueryParamMap(request);
				Page page = getPage(request.getAppHead());
				result = poolCreditService.queryPlDiscountPJE010(queryBean, page);
				if (null == result || result.getTotalCount() == 0) {
					ret.setRET_CODE(Constants.CREDIT_05);
					ret.setRET_MSG("无符合条件数据");
				} else {
					setPage(response.getAppHead(), page);
					List details = this.detailPlDiscount(result.getRecords());
					response.setDetails(details);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("贴现结果查询"+queryBean.getCustomernumber()+"客户查询成功!共"+result.getTotalCount()+"条");
				}

		} catch (Exception ex) {
			logger.error("PJE010-贴现结果查询!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("强制贴现申请!票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * @Description: 请求数据处理
	 * @param request
	 * @return PoolQueryBean
	 * @author xie cheng
	 * @date 2019-05-23 
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		PoolQueryBean pq = new PoolQueryBean();
		Map body = request.getBody();
		pq.setCustomernumber(getStringVal(body.get("CORE_CLIENT_NO"))); //核心客户号
		pq.setProtocolNo(getStringVal(body.get("BPS_NO")));//票据池编号
		pq.setBillNo(getStringVal(body.get("QUERY_TYPE"))); //2：贴现  : 未启用
		pq.setCtrctNb(getStringVal(body.get("CONTRACT_NO"))); //合同号：未启用
		pq.setBusinessId(getStringVal(body.get("BILL_ID")));//票据ID
		return pq;
	}
	
	/**
	 * 返回details加工处理
     * 融资票据池details
	 * @author xie cheng
	 * @date 2019-5-23
	 */
	private List detailPlDiscount(List result) {
		ArrayList infoList = new ArrayList();
		if (result != null && result.size() > 0) {
			HashMap map = null;
			for (int i = 0; i < result.size(); i++) {
				map = new HashMap();
				PlDiscount PlDiscount = (PlDiscount) result.get(i);
				if(PlDiscount.getReTranstatus() == null ){
					map.put("BILL_MSG_ARRAY.TRAN_STATUS", "1");// 未处理
				}else{
					map.put("BILL_MSG_ARRAY.TRAN_STATUS", PlDiscount.getReTranstatus());// 交易状态
				}
				map.put("BILL_MSG_ARRAY.TRAN_MSG", PlDiscount.getReTranmsg());// 交易描述
				map.put("BILL_MSG_ARRAY.REVOKE_FLAG", "0");// 撤回标志0：不允许撤回1：允许撤回 只传0！
				map.put("BILL_MSG_ARRAY.BILL_ID", PlDiscount.getBillinfoId().getDiscBillId());// 票据ID
				map.put("BILL_MSG_ARRAY.ACCOUNT_STATUS", PlDiscount.getAccountStatus());// 记账状态
				infoList.add(map);
			}
		}
		return infoList;
	}

	
}
