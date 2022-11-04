package com.mingtech.application.pool.bank.netbanksys.handler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;

/**
 * 
 * @Title: 网银查询入口PJC007
 * @Description: 票据池额度汇总查询
 * @author Ju Nana
 * @date 2018-10-25
 */
public class PJC007RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC007RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; // 网银服务
	@Autowired
	private PedAssetPoolService pedAssetPoolService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
		String bpsNo = getStringVal(request.getBody().get("BPS_NO"));
		try {
			EduResult eduResult = pedAssetPoolService.queryEduAll(bpsNo);
			response.getBody().put("BILL_TOTAL_AMT", eduResult.getTotalBillAmount());// 票据总额度
			response.getBody().put("LOW_RISK_TOTAL_AMT", eduResult.getLowRiskAmount().add(eduResult.getBailAmountTotail()));// 低风险总额度=低风险票据额度+保证金额度
			response.getBody().put("HIGH_RISK_TOTAL_AMT", eduResult.getHighRiskAmount());// 高风险总额度
			response.getBody().put("USED_LOW_RISK_AMT", eduResult.getUsedLowRiskAmount().add(eduResult.getBailAmountUsed()));// 低风险已用额度=低风险票据已用+保证金已用
			response.getBody().put("USED_HIGH_RISK_AMT", eduResult.getUsedHighRiskAmount());// 高风险已用额度
			response.getBody().put("LOW_RISK_LIMIT_BALANCE", eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount()));// 低风险可用额度=低风险票据可用额度+保证金可用
			response.getBody().put("HIGH_RISK_LIMIT_BALANCE", eduResult.getFreeHighRiskAmount());// 高风险可用额度
			response.getBody().put("UNPRODUCT_BILL_TOTAL_AMT", eduResult.getZeroEduAmount());// 不产生额度票据金额
			response.getBody().put("DEPOSIT_BAL_AMT", eduResult.getBailAmount());// 保证金余额
			// 构建响应对象
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("查询成功");
		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询异常");
		}

		response.setRet(ret);
		return response;
	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
