package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.domain.PlFeeScale;
import com.mingtech.application.pool.common.service.PedProtocolService;

/**
 * PJC042(网银接口)票据池服务费收费标准查询
 * @author 
 *
 */
public class PJC042RequestHandler extends PJCHandlerAdapter{
	@Autowired
	private PedProtocolService pedProtocolService;
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
		ReturnMessageNew response = new ReturnMessageNew();
		Map map = request.getBody();
		Ret ret = new Ret();
		try {
			
			PlFeeScale  psScale = pedProtocolService.queryFeeScale();
			if(psScale!=null){
				response.getBody().put("ANNUAL_FEE_STANDARD", psScale.getEveryYear());//年费
				response.getBody().put("PER_TRANS_FEE_STANDARD", psScale.getEveryPiece());//单笔
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);//
				ret.setRET_MSG("票据池服务费收费标准查询交易成功");
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);//
				ret.setRET_MSG("票据池服务费收费标准未维护！");
			}

			
		} catch (Exception e) {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);//
			ret.setRET_MSG("网银接口PJC042-票据池服务费收费标准查询异常! 票据池内部执行错误");
		}
		response.setRet(ret);
		return response;
	}
}
