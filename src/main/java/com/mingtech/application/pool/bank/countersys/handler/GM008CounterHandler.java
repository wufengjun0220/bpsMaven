package com.mingtech.application.pool.bank.countersys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.common.PoolComm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;

/**
 * 票据池服预约取票批次接口
 * 根据身份证号查询批次信息返回柜面
 * @author Wu Fengjun
 * @date 2019-7-8
 *
 */
public class GM008CounterHandler extends PJCHandlerAdapter{
	private static final Logger logger = Logger
							.getLogger(GM008CounterHandler.class);
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		List details = new ArrayList();
		try {
			String mobil = getStringVal(body.get("APP_MOBIL"));//手机号
			String name = getStringVal(body.get("APP_NAME"));//姓名
			String identity = getStringVal(body.get("APP_IDENTITY_GLOBAL_ID"));//身份证号
			
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setEbankPeopleCard(identity);
			queryBean.setSStatusFlag("1");
			queryBean.setIsPoolOutEnd(PoolComm.OUT_01);
			queryBean.setPlApplyNm(name);//姓名
			List<PlPdraftBatch> draftList  = draftPoolQueryService.queryPlPdraftBatchByBatch(queryBean);
			
			PlPdraftBatch plPdraftBatch = null;
			if(draftList != null){
				for (int i = 0; i < draftList.size(); i++) {
					plPdraftBatch = draftList.get(i);
					Map map = new HashMap();
					map.put("BATCH_MSG_ARRAY.BPS_NO", plPdraftBatch.getBpsNo());//票据池编号
					map.put("BATCH_MSG_ARRAY.BPS_NAME", plPdraftBatch.getBpsName());//票据池名称
					map.put("BATCH_MSG_ARRAY.CORE_CLIENT_NO", plPdraftBatch.getCustNo());//核心客户号
					map.put("BATCH_MSG_ARRAY.CORE_CLIENT_NAME", plPdraftBatch.getCustName());//核心客户名称
					map.put("BATCH_MSG_ARRAY.APP_MOBIL", plPdraftBatch.getWorlerPhoneNo());//经办人手机
					map.put("BATCH_MSG_ARRAY.APP_NAME", plPdraftBatch.getWorkerName());//经办人名称
					map.put("BATCH_MSG_ARRAY.APP_IDENTITY_GLOBAL_ID", plPdraftBatch.getWorkerId());//经办人身份证
					map.put("BATCH_MSG_ARRAY.GETER_BILL_NUM", plPdraftBatch.getTotalNum());//取票张数
					map.put("BATCH_MSG_ARRAY.GETER_BILL_TOTAL_AMT", plPdraftBatch.getTotalAmt().setScale(2, BigDecimal.ROUND_DOWN)+"");//取票总金额
					map.put("BATCH_MSG_ARRAY.OUTPOOL_BATCH_NO", plPdraftBatch.getBatchNo());//出池批次号
					details.add(map);
				}
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("查询成功!");
				
			}else {
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户没有预约票据");
			}
			
		}catch(Exception e){
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("预约查询发生错误[" + e.getMessage() + "]");
		}
		response.setRet(ret);
		response.setDetails(details);
		return response;
		
	}
}
