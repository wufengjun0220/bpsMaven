package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.utils.DraftRangeHandler;

/**
 * 
 * @Title: 网银接口PJC016
 * @Description: 票样查询-正面查询接口
 * @author Ju Nana
 * @date 2018-10-24
 */
public class PJC016RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC016RequestHandler.class);

	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map map = new HashMap();
		QueryResult result = null;
		try {
			String billNo = getStringVal(request.getBody().get("BILL_NO"));

			/********************融合改造新增 start******************************/
			String beginRangeNo = "0";
			String endRangeNo = "0";
			if(StringUtils.isNotEmpty(getStringVal(request.getBody().get("START_BILL_NO")))){
				beginRangeNo = getStringVal(request.getBody().get("START_BILL_NO")); // 子票起始号
			}
			if(StringUtils.isNotEmpty(getStringVal(request.getBody().get("END_BILL_NO")))){
				endRangeNo = getStringVal(request.getBody().get("END_BILL_NO")); // 子票截止
			}
			/********************融合改造新增 end******************************/

			map = poolEBankService.queryDraftFrontPJC016(billNo,beginRangeNo,endRangeNo);
			if (map != null && map.size() > 0) {
				response.setBody(map);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("查询成功!");
			} else {
				ret.setRET_MSG("无符合条件数据");
				ret.setRET_CODE(Constants.EBK_03);
			}

		} catch (Exception ex) {
			logger.error(ex, ex);
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
