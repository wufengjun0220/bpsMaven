package com.mingtech.application.pool.bank.countersys.handler;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
/**
 * 保证金销户校验
 * @author Administrator
 *
 */
public class GM000CounterHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger
		.getLogger(GM000CounterHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			String custNo = (String) request.getBody().get("ORG_CODE");
			PedProtocolDto dto = null;
			List<PedProtocolDto> proList =  pedProtocolService.queryProtocolInfo(null, null, null,custNo, null, null);
			if(proList!=null && proList.size()>0){
				dto = proList.get(0);
			}
			
			String marAcc = (String)request.getBody().get("DEPOSIT_ACCT_NO");
			if(dto.getMarginAccount().equals(marAcc)){//校验保证金账号是否是票据池的账号
					//票据池关闭时
				   if(PoolComm.OPEN_01.equals(dto.getOpenFlag())){
					   ret.setRET_CODE(Constants.TX_FAIL_CODE);
					   ret.setRET_MSG("票据池融资功能未关闭,不能注销保证金账号!");
					}else{
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("校验通过！");									
					}
			}else{
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("该账号与票据池无关联!");
			}
		} catch (Exception e) {
			logger.error(e,e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池查询异常!");
		}
		response.setRet(ret);
		return response;
		}
	
		public PedProtocolService getPedProtocolService() {
			return pedProtocolService;
		}
		
		public void setPedProtocolService(PedProtocolService pedProtocolService) {
			this.pedProtocolService = pedProtocolService;
		}

}
