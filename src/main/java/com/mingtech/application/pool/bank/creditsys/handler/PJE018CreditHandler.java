package com.mingtech.application.pool.bank.creditsys.handler;


import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;


/**
 * @Title: PJE018
 * @Description: 票据池模式变更
 * @author 
 * @date 2021-05-08
 */
public class PJE018CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE018CreditHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	/**
	 * PJE018 票据池模式变更
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		QueryResult result = null;
		Ret ret = new Ret();
		try {
				OnlineQueryBean queryBean = QueryParamMap(request);
				ProtocolQueryBean bean=new ProtocolQueryBean();
				bean.setPoolAgreement(queryBean.getBpsNo());
				PedProtocolDto pro=	pedProtocolService.queryProtocolDtoByQueryBean(bean);
				pro.setPoolMode(queryBean.getPoolMode());
				pedProtocolService.txStore(pro);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("票据池模式变更成功");
		} catch (Exception ex) {
			logger.error("PJE018-票据池模式变更!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池模式变更票据池内部执行错误!");
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
		bean.setBpsNo(getStringVal(body.get("BPS_NO")));//票据池编号
		bean.setPoolMode(getStringVal(body.get("POOL_MODE"))); //额度模式
		return bean;
	}
	
	
}
