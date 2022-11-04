package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;

/**
 * @Title: EBK 接口 PJC050
 * @Description 在线银承协议信息查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC050RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC050RequestHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try{
        	 BigDecimal lowRiskAmt = BigDecimal.ZERO;//票据池低风险额度 
        	 OnlineQueryBean queryBean = QueryParamMap(request);
        	 queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
             List list = pedOnlineAcptService.queryOnlineAcptProtocolList(queryBean);
             if(null != list && list.size()>0){
             	PedOnlineAcptProtocol protocol = (PedOnlineAcptProtocol) list.get(0);
             	if(new BigDecimal(100).compareTo(protocol.getDepositRatio())!=0){
             		List<PedProtocolDto> pools =  pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, protocol.getBpsNo(), null, null, null);
             		if(null != pools && pools.size()>0){
             			PedProtocolDto pool = pools.get(0);
             			
             			
             			 //票据池保证金资产额度
    	     	        AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);
    	     	        //低风险票资产额度
    	     	        AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
    	     	        lowRiskAmt = atBail.getCrdtFree().add(atBillLow.getCrdtFree());   //票据池低风险可用额度 
             			
             			response.getBody().put("BUSS_MODE", pool.getPoolMode());//业务模式(池模式)
             			//协议校验
             			List errors = pedOnlineAcptService.checkPoolInfoForEBK(null,pool,null);
             			if(null !=errors && errors.size()>0){
             				ret.setSomeList(errors);
             				ret.setRET_MSG("未查询到生效状态票据池协议！");
             				ret.setRET_CODE(Constants.TX_FAIL_CODE);
             				response.setRet(ret);
             		        return response;
             			}
             		}else{
             			ret.setRET_MSG("未查询到生效状态票据池协议！");
             			ret.setRET_CODE(Constants.TX_FAIL_CODE);
             			response.setRet(ret);
             	        return response;
             		}
             	}else{
             		response.getBody().put("BUSS_MODE", PoolComm.POOL_MODEL_01);//业务模式
             	}
             	response.getBody().put("CORE_CLIENT_NO", protocol.getCustNumber());//核心客户号
 				response.getBody().put("ONLINE_ACPT_NO", protocol.getOnlineAcptNo());//在线银承编号
 				response.getBody().put("CLIENT_NAME", protocol.getCustName());//客户名称
 				response.getBody().put("ONLINE_ACPT_TOTAL_AMT", protocol.getOnlineAcptTotal());//在线银承总额
 				response.getBody().put("DEPOSIT_ACCT_NO", protocol.getDepositAcctNo());//扣收账号
 				response.getBody().put("DEPOSIT_ACCT_NAME", protocol.getDepositAcctName());//扣收账户名称
 				CoreTransNotes note = new CoreTransNotes();
 				note.setAccNo(protocol.getDepositAcctNo());
 				ReturnMessageNew result = poolCoreService.PJH716040Handler(note,"0");
 				if (result.isTxSuccess()) {
 					Map map = result.getBody();
 					if(map.get("BALANCE")!=null){
 						response.getBody().put("DEPOSIT_ACCT_BALANCE", getBigDecimalVal(map.get("BALANCE")));//扣款账户余额
 					}
 				}
 				response.getBody().put("DEPOSIT_RATE", protocol.getDepositRatio());//保证金比例（%）
 				response.getBody().put("BPS_LIMIT_USE_RATE", protocol.getPoolCreditRatio());//票据池额度占用比例（%）
 				response.getBody().put("FEE_RATE", protocol.getFeeRate());//手续费率（%）
 				response.getBody().put("USED_AMT", protocol.getUsedAmt());//已用额度
 				response.getBody().put("UNUSED_AMT", protocol.getOnlineAcptTotal().subtract(protocol.getUsedAmt()));//未用额度
             	response.getBody().put("ACCEPTOR_BANK_NO", protocol.getAcceptorBankNo());//出票人开户行行号
     			response.getBody().put("ACCEPTOR_BANK_NAME", protocol.getAcceptorBankName());//出票人开户行名称
     			response.getBody().put("LOW_RISK_TOTAL_AMT", lowRiskAmt);//低风险总额度
     			
     			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     			ret.setRET_MSG("查询成功!");
             }else{
             	ret.setRET_CODE(Constants.EBK_03);
             	ret.setRET_MSG("无生效状态的在线银承协议！");
             }
        }catch (Exception e) {
        	logger.error("PJC050-在线银承协议信息查询异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池内系统部处理异常！");
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
		bean.setEbkCustNo(getStringVal(body.get("CMS_CLIENT_NO"))); //网银客户号
		return bean;
	}

}
