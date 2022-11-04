package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;

/**
 * @Title: EBK 接口 PJC051
 * @Description 在线流贷协议信息查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC051RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC051RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private PedProtocolService pedProtocolService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try{
        	 OnlineQueryBean queryBean = QueryParamMap(request);
        	 queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
             List<PedOnlineCrdtProtocol> list = pedOnlineCrdtService.queryOnlineProtocolList(queryBean);
             if(null != list && list.size()>0){
             	PedOnlineCrdtProtocol protocol = list.get(0);
             	PedProtocolDto pool = (PedProtocolDto) pedProtocolService.load(protocol.getBpsId());
             	if(null != pool && PoolComm.OPEN_01.equals(pool.getOpenFlag())){
             		response.getBody().put("BUSS_MODE", pool.getPoolMode());//业务模式(池模式)
             		//校验协议
             		List errors = pedOnlineCrdtService.checkPoolInfoForEBK(queryBean,pool,list);
             		if(null != errors && errors.size()>0){
             			ret.setSomeList(errors);
             			ret.setRET_CODE(Constants.EBK_03);
                     	ret.setRET_MSG("未查询到生效状态的票据池！");
             		}else{
             			response.getBody().put("CORE_CLIENT_NO", protocol.getCustNumber());//核心客户号
             			response.getBody().put("ONLINE_LOAN_NO", protocol.getOnlineCrdtNo());//在线流贷编号
             			response.getBody().put("CLIENT_NAME", protocol.getCustName());//客户名称
             			response.getBody().put("BPS_LIMIT_RATE", protocol.getPoolCreditRatio());//票据池额度比例（%）
             			response.getBody().put("BASE_RATE_TYPE", protocol.getBaseRateType());//基准利率类型
             			response.getBody().put("RATE_FLOAT_TYPE", protocol.getRateFloatType());//利率浮动方式
             			response.getBody().put("RATE_FLOAT_VALUE", protocol.getRateFloatValue());//利率浮动值（%）
             			BigDecimal rate = pedOnlineCrdtService.queryRatefromLPR();
             			if(null != rate){
             				response.getBody().put("LOAN_INT_RATE", rate);//贷款利率
             			}
             			//放款账户账号 实时查询
             			response.getBody().put("LOAN_ACCT_NO", protocol.getLoanAcctNo());//放款账户账号
             			response.getBody().put("LOANER_ACCT_NAME", protocol.getLoanAcctName());//放款账户名称
             			response.getBody().put("DEDU_ACCT_NO", protocol.getDeduAcctNo());//扣款账户账号（还款账号）
             			response.getBody().put("DEDU_ACCT_NAME", protocol.getDeduAcctName());//扣款账户名称（还款户名）
             			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
             			ret.setRET_MSG("查询成功!");
             		}
             	}else{
             		ret.setRET_CODE(Constants.EBK_03);
                 	ret.setRET_MSG("未查询到生效状态的票据池！");
             	}
             }else{
             	ret.setRET_CODE(Constants.EBK_03);
             	ret.setRET_MSG("未查询到生效状态的在线流贷协议！");
             }
        }catch (Exception e) {
        	logger.error("PJC051-在线流贷协议信息查询异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池内部处理异常！");
		}
        response.setRet(ret);
        return response;
    }
    /**
   	 * @Description: 请求数据处理
   	 * @param request
   	 * @return OnlineQueryBean
   	 * @author wss
   	 * @date 2021-4-29
   	 */
   	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
   		OnlineQueryBean bean = new OnlineQueryBean();
   		Map body = request.getBody();
   		bean.setCustNumber(getStringVal(body.get("CORE_CLIENT_NO")));//核心客户号
   		bean.setBpsNo(getStringVal(body.get("CMS_CLIENT_NO"))); //网银客户号
   		return bean;
   	}

}
