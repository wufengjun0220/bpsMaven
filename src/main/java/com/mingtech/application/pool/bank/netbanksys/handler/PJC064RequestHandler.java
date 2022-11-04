package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;

/**
 * @Description 在线银承业务跟踪查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC064RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC064RequestHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private CommonQueryService commonQueryService;
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Page page = getPage(request.getAppHead());
        Ret ret = new Ret();
        try {
        	OnlineQueryBean queryBean = QueryParamMap(request);
        	List<PlOnlineAcptDetail> details = pedOnlineAcptService.queryPlOnlineAcptDetailList(queryBean, page);
			QueryResult result =commonQueryService.loadDataByResult(details, "billAmt");

			if (details != null && result.getRecords().size() > 0) {
				for (PlOnlineAcptDetail detail:details) {
					Map addMap = new HashMap();
					addMap.put("BILL_INFO_ARRAY.BILL_NO", detail.getBillNo());   //票号                    
					addMap.put("BILL_INFO_ARRAY.BILL_AMT", detail.getBillAmt());   //票据金额                
					addMap.put("BILL_INFO_ARRAY.DRAW_DATE", DateUtils.toString(detail.getIsseDate(), DateUtils.ORA_DATE_FORMAT));   //出票日                  
					addMap.put("BILL_INFO_ARRAY.EXPIRY_DATE", DateUtils.toString(detail.getDueDate(), DateUtils.ORA_DATE_FORMAT));   //到期日                  
					addMap.put("BILL_INFO_ARRAY.RECV_BILL_CLIENT_NAME", detail.getIssuerName());   //出票人名称              
					addMap.put("BILL_INFO_ARRAY.RECV_BILL_ACCT_NO", detail.getIssuerAcct());   //出票人账号              
					addMap.put("BILL_INFO_ARRAY.RECV_BILL_BANK_NO", detail.getIssuerBankCode());   //出票人开户行行号        
					addMap.put("BILL_INFO_ARRAY.RECV_BILL_BANK_NAME", detail.getIssuerBankName());   //出票人开户行行名        
					addMap.put("BILL_INFO_ARRAY.PAYEE_CLIENT_NAME", detail.getPayeeName());   //收款人名称              
					addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", detail.getPayeeAcct());   //收款人账号              
					addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NO", detail.getPayeeBankCode());   //收款人开户行行名        
					addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NAME", detail.getPayeeBankName());   //收款人开户行号          
					addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NO", detail.getAcptBankCode());   //承兑行行号              
					addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NAME", detail.getAcptBankName());   //承兑行名称              
					addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", detail.getTransferFlag());   //是否可转让              
					addMap.put("BILL_INFO_ARRAY.BILL_STATUS", detail.getStatus());   //票据状态    
					if(StringUtils.isNotBlank(detail.getDraftSource()) && detail.getDraftSource().equals(PoolComm.CS02)){
						addMap.put("BILL_INFO_ARRAY.START_BILL_NO", detail.getBeginRangeNo());   //票据号起
						addMap.put("BILL_INFO_ARRAY.END_BILL_NO", detail.getEndRangeNo());   //票据号止
					}
					addMap.put("BILL_INFO_ARRAY.BILL_SOURCE", detail.getDraftSource());   //票据来源
					addMap.put("BILL_INFO_ARRAY.BILL_ID", detail.getBillId());   //票据id
					addMap.put("BILL_INFO_ARRAY.AUTO_RECV_BILL_FLAG", detail.getIsAutoCallPyee());   //是否联动收票人自动收票  0否1是
					
					response.getDetails().add(addMap);
				}
    			setPage(response.getAppHead(), page,result.getTotalAmount().toString());
    			response.getBody().put("TOTAL_AMT", result.getTotalAmount());      //总金额
				response.getBody().put("ONLINE_NO", queryBean.getOnlineAcptNo());//在线协议编号
	 	        response.getBody().put("ONLINE_CRDT_NO", queryBean.getContractNo());//在线业务合同号
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
			} else {
			    ret.setRET_MSG("无符合条件数据");
				ret.setRET_CODE(Constants.EBK_03);
			}
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG(" 在线银承业务跟踪查询异常");
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
   		Map map = request.getBody();
   		
   		bean.setOnlineAcptNo(getStringVal(map.get("ONLINE_BUSS_PROTOCOL_NO")));//在线业务协议编号
   		bean.setContractNo(getStringVal(map.get("ONLINE_BUSS_CONTRACT_NO")));//在线业务合同号
   		bean.setPayeeName(getStringVal(map.get("RECV_BILL_CLIENT_NAME")));//收票人
   		bean.setBillNo(getStringVal(map.get("BILL_NO")));//票号
   		bean.setStartAmt(getBigDecimalVal(map.get("MIN_BILL_AMT")));//票据金额上限
   		bean.setEndAmt(getBigDecimalVal(map.get("MAX_BILL_AMT")));//票据金额下限
   		
   		return bean;
   	}


}
