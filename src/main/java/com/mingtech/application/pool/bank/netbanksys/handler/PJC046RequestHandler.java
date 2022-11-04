package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.framework.core.page.Page;

/**
 * 网银接口(pjc046)——网银纸票出入池申请批次信息查询
 * @Description 
 * @author wu fengjun
 * @version v1.0
 * @date 2019-6-21
 */
public class PJC046RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC046RequestHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	
	/**
	 * 
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List<Map> list = new ArrayList<Map>();
		Page page = getPage(request.getAppHead());
		try {
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			
			if (dto == null || !PoolComm.OPEN_01.equals(dto.getOpenFlag())) {
				throw new Exception("客户票据池功能不属于开通状态!");
			}else {
				PoolQueryBean query = new PoolQueryBean();
				query.setCustomernumber(custNo);
				query.setProtocolNo(bpsNo);
				query.setIsPoolOutEnd("1");
				QueryResult queryResult = draftPoolQueryService.queryPlPdraftBatchByBatch(query, page);
				if(null != queryResult){
					List batch = queryResult.getRecords();
					for (int i = 0; i < batch.size(); i++) {
						PlPdraftBatch draft = (PlPdraftBatch) batch.get(i);
						Map map = new HashMap();
						map.put("BATCH_MSG_ARRAY.BATCH_NO", draft.getBatchNo());
						map.put("BATCH_MSG_ARRAY.APP_MOBIL", draft.getWorlerPhoneNo());
						map.put("BATCH_MSG_ARRAY.APP_NAME", draft.getWorkerName());
						map.put("BATCH_MSG_ARRAY.APP_IDENTITY_GLOBAL_ID", draft.getWorkerId());
						map.put("BATCH_MSG_ARRAY.USAGE", draft.getUseWay());
						map.put("BATCH_MSG_ARRAY.BILL_NUM", draft.getTotalNum());
						map.put("BATCH_MSG_ARRAY.BILL_TOTAL_AMT", draft.getTotalAmt());
						map.put("BATCH_MSG_ARRAY.REMARK", draft.getRemark());
						list.add(map);
					}
					setPage(response.getAppHead(), page);
					response.getBody().put("TOTAL_AMT", queryResult.getTotalAmount());
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("网银纸票出入池申请批次信息查询成功!");
				}else{
					ret.setRET_CODE(Constants.EBK_03);
					ret.setRET_MSG("该客户未查询到网银纸票出入池申请批次信息!");
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("网银纸票出入池申请批次信息查询异常");
		}
		response.setDetails(list);
		response.setRet(ret);
		return response;
	}
	


}
