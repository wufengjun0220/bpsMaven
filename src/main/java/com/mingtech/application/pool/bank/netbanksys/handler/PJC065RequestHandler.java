package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.ErrorCode;

/**
 * @Description 在线业务合同状态同步
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC065RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC065RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try {
        	OnlineQueryBean queryBean = QueryParamMap(request);
        	//0：在线银承  1：在线流贷
        	if(PublicStaticDefineTab.PRODUCT_YC.equals(queryBean.getOnlineProtocolType())){
        		pedOnlineAcptService.txSyncContract(queryBean.getOnlineNo(),queryBean.getContractNo());
        	}else{
        		//票据池系统调查证交易驱动后续流程
        		//a.若核心已记账成功，票据池将该任务置为成功；
        		//b.若核心未记账，票据池将该借据、合同置为失败，并记录失败原因。先释放在线流贷协议流贷额度、票据池担保合同额度、票据池低风险额度、收款人额度（若有），然后实时通知MIS系统释放
        		pedOnlineCrdtService.txSyncContract(queryBean.getOnlineNo(),queryBean.getContractNo());
        	}
			// 构建响应对象
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线业务合同状态同步异常");
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
		bean.setOnlineNo(getStringVal(body.get("ONLINE_BUSS_NO")));//在线协议编号 必输
		bean.setOnlineProtocolType(getStringVal(body.get("ONLINE_BUSS_TYPE")));//在线业务类型 必输
		bean.setContractNo(getStringVal(body.get("ONLINE_BUSS_CONTRACT_NO"))); //在线业务合同号 必输
		return bean;
	}

}
