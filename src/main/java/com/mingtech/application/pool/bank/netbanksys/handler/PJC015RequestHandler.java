package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;

/**
 * 
 * @Title: 票据池网银接口PJC015
 * @Description: 待办事项-汇总查询接口
 * @author Ju Nana
 * @date 2018-10-23
 */
public class PJC015RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC015RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类
	/**
	 *（1）待办事项一：融资业务到期还款申请，根据票据池编号与核心客户号查询pedcheck表
	 *（2）代办事项二:票据池对账,根据票据池编号与核心客户号查询pedcheck表
	 *（3）代办事项三：票据池年费收取
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List<Map> reList = new ArrayList<Map>();
		try {
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));
			reList = poolEBankService.queryNotifySummaryPJC015(custNo,bpsNo);
			if (reList!=null && reList.size() > 0) {
				response.getAppHead().put("TOTAL_ROWS", reList.size());// 总记录数
				ret.setRET_MSG("查询成功");
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				response.setDetails(reList);
			} else {
				ret.setRET_MSG("无符合条件数据");
				response.getAppHead().put("TOTAL_ROWS", 0);// 总记录数
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			}

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
