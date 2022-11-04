package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 
 * @Title: 票据池网银接口PJC018
 * @Description: 风险管理-黑名单查询接口
 * @author Ju Nana
 * @date 2018-10-23
 */
public class PJC018RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC018RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List blackList = new ArrayList();
		try {
			String orgCode = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String type = getStringVal(request.getBody().get("BUSS_TYPE"));
//			String singFlag = poolEBankService.queryPoolCommOpen(null, orgCode, null, null);
//			if (StringUtil.isNotEmpty(singFlag) && singFlag.equals(PoolComm.DRAFT_POOL_OPEN)) {
				blackList = poolEBankService.queryBlackListPJC018(orgCode, type);
				if (blackList != null && blackList.size() > 0) {
					response.getAppHead().put("TOTAL_ROWS", blackList.size());// 总记录数
					response.setDetails(blackList);
					ret.setRET_MSG("查询成功");
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				} else {
					ret.setRET_MSG("没有符合条件的数据");
					ret.setRET_CODE(Constants.EBK_03);
				}

//			} else {
//				ret.setRET_CODE(Constants.EBK_02);
//				ret.setRET_MSG("该客户未开通票据池业务");
//			}
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
