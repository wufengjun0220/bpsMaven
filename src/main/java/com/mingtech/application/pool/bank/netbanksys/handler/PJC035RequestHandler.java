package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.edu.domain.PedCheck;

/**
 * @Title: 网银接口PJC035对账结果推送接口
 * @Description:查询网银对账批次信息,如果存在则更新数据
 * @author xie cheng
 * @date 2019-05-27
 */
public class PJC035RequestHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC035RequestHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			Map body = request.getBody();
			String bpsNo = getStringVal(body.get("BPS_NO"));//票据池编号
			String custNo = getStringVal(body.get("CORE_CLIENT_NO"));//核心客户号
			String batchNo = getStringVal(body.get("RECON_BATCH_NO"));//对账批次号
			String checkResult = getStringVal(body.get("RECON_RESULT"));//对账结果
			String remark = getStringVal(body.get("REMARK"));//备注
			
			List<PedCheck> checkList = pedProtocolService.queryPedCheck(bpsNo, custNo, null, batchNo,null);
			if(checkList!=null && checkList.size()>0){
				PedCheck check = checkList.get(0);
				check.setCheckResult(checkResult);
				check.setRemark(remark);
				check.setCheckDate(new Date());//对账时间
				pedProtocolService.txStore(check);			
				

				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("该客户对账结果数据更新成功!");
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户未查询到对账信息!");
			}
		} catch (Exception e) {
			logger.error("PJC035-对账结果数据更新异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("对账结果数据更新异常! 票据池内部执行错误");
		}
		response.setRet(ret);
		return response;
	}

}
