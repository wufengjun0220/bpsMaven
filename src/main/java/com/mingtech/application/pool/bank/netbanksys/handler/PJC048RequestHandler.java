package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PedProtocolService;

/**
 * 自动入池校验接口
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-6
 */
public class PJC048RequestHandler extends PJCHandlerAdapter{
	@Autowired
	private PedProtocolService pedProtocolService;
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
		ReturnMessageNew response = new ReturnMessageNew();
		Map map = request.getBody();
		Ret ret = new Ret();
		try {
			String bpsNo = getStringVal(map.get("BPS_NO"));//票据池编号
			String custNo = getStringVal(map.get("CORE_CLIENT_NO"));//核心客户号		
			boolean isAuto = pedProtocolService.isAutoCheck(bpsNo,custNo);
			if(isAuto){
				response.getBody().put("AUTO_INPOOL_FLAG", PoolComm.YES);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			}else{
				response.getBody().put("AUTO_INPOOL_FLAG", PoolComm.NO);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			}
			ret.setRET_MSG("查询成功!");	
		} catch (Exception e) {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);//
			ret.setRET_MSG("信息查询异常! 票据池内部执行错误");
		}
		response.setRet(ret);
		return response;
	}
}
