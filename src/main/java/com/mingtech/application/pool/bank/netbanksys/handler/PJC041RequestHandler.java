package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.base.service.PoolQueryService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.utils.DraftRangeHandler;

/**
 * @Title: 网银接口PJC041-经办锁接口
 * @author wu fengjun
 * @date 2019-06-03
 */
public class PJC041RequestHandler extends PJCHandlerAdapter{

	private static final Logger logger = Logger.getLogger(PJC041RequestHandler.class);
	@Autowired
	PoolCreditService poolCreditService ;
	@Autowired
	PoolQueryService poolQueryService;
	/**
	 * 网银发送出池申请时,先提交经办,向票据池发送经办锁交易
	 * 出池申请成功后解锁
	 */
	
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception{
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		List details = request.getDetails();
		String ids = "";
		List list = new ArrayList();
		try {
			String type = getStringVal(body.get("OPERATION_TYPE"));//操作类型	CZLX_01:加锁; CZLX_02:解锁
			String custNo = getStringVal(body.get("CORE_CLIENT_NO"));//核心客户号
			String bpsNo = getStringVal(body.get("BPS_NO"));//票据池编号
			
			for (int i = 0; i < details.size(); i++) {
				Map map = (Map) details.get(i);
				String id = getStringVal(map.get("BILL_INFO_ARRAY.BILL_ID"));//票据id
				ids = ids+"|"+id;
			}

			logger.info("操作类型为["+type+"]的票,经办锁申请开始");
			/********************融合改造适应性修改 start******************************/
			
			if(type.equals("CZLX_01")){//加锁
				for (int i = 0; i < details.size(); i++) {
					Map map = (Map) details.get(i);
					String billNo = getStringVal(map.get("BILL_INFO_ARRAY.BILL_NO"));//票据id
					String beginRangeNo = getStringVal(map.get("BILL_INFO_ARRAY.START_BILL_NO"));//票据号起
					String endRangeNo = getStringVal(map.get("BILL_INFO_ARRAY.END_BILL_NO"));//票据号止
					if(StringUtils.isEmpty(beginRangeNo)){
						beginRangeNo = "0";
					}
					if(StringUtils.isEmpty(endRangeNo)){
						endRangeNo = "0";
					}
					PoolBillInfo pool = poolQueryService.queryObj(billNo, beginRangeNo, endRangeNo);
					pool.setEbkLock(PoolComm.EBKLOCK_01);//加锁
					list.add(pool);
				}
				logger.info("加锁成功");
			}else {//解锁
				for (int i = 0; i < details.size(); i++) {
					Map map = (Map) details.get(i);
					String billNo = getStringVal(map.get("BILL_INFO_ARRAY.BILL_NO"));//票据id
					String beginRangeNo = getStringVal(map.get("BILL_INFO_ARRAY.START_BILL_NO"));//票据号起
					String endRangeNo = getStringVal(map.get("BILL_INFO_ARRAY.END_BILL_NO"));//票据号止
					if(StringUtils.isEmpty(beginRangeNo)){
						beginRangeNo = "0";
					}
					if(StringUtils.isEmpty(endRangeNo)){
						endRangeNo = "0";
					}
					PoolBillInfo pool = poolQueryService.queryObj(billNo, beginRangeNo, endRangeNo);
					pool.setEbkLock(PoolComm.EBKLOCK_02);//解锁
					list.add(pool);
				}
				/********************融合改造适应性修改 end******************************/
				logger.info("解锁成功");
			}
			poolCreditService.txStoreAll(list);
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("票据池经办锁交易成功！");
		} catch (Exception e) {
			logger.info(e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池交易异常! 票据池内部执行错误");
		}
		response.setRet(ret);
		return response;
		
	}
}
