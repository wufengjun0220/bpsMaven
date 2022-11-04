package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.FileName;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;

/**
 * @Title: EBK 接口 PJC052
 * @Description 收票人信息查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC052RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC051RequestHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
    	ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
				OnlineQueryBean queryBean = QueryParamMap(request);
				queryBean.setPayeeStatus(PublicStaticDefineTab.STATUS_1);
				queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
				List payees = pedOnlineAcptService.queryOnlineAcptPayeeListByBean(queryBean, null);
				if (null != payees && payees.size()>0) {
					OnlineQueryBean info = (OnlineQueryBean) payees.get(0);
					response.getBody().put("ONLINE_ACPT_NO", info.getOnlineAcptNo());//在线银承编号
					//数据处理
					List details = this.detailDateHandler(payees);
					response.setDetails(details);
					//文件路径
					String path = FileName.getFileNameClient(request.getTxCode())+".txt";
					response.getFileHead().put("FILE_FLAG", "2");
					response.getFileHead().put("FILE_PATH", path);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("在线银承收票人查询,共"+payees.size()+"条");
				} else {
					response.getFileHead().put("FILE_FLAG", "0");
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("无符合条件数据");
				}
		} catch (Exception ex) {
			logger.error("PJC052-在线银承收票人查询!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线银承收票人查询!票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
    }
    /**
     * @author wss
     * @date 2021-4-29
     * @description 返回数据处理
     */
    private List detailDateHandler(List payees) {
    	List list = new ArrayList();
		if(null != payees && payees.size()>0){
			for(int i=0;i<payees.size();i++){
				Map map = new HashMap();
				OnlineQueryBean info = (OnlineQueryBean) payees.get(i);
				map.put("RECV_BILL_CLIENT_NO",info.getPayeeId());//收票人编号
				map.put("RECV_BILL_CLIENT_NAME",info.getPayeeAcctName());//收票人名称
				map.put("RECV_BILL_ACCT_NO",info.getPayeeAcctNo());//收票人账号
				map.put("RECV_BILL_BANK_NO",info.getPayeeOpenBankNo());//收票人开户行行号
				map.put("RECV_BILL_BANK_NAME",info.getPayeeOpenBankName());//收票人开户行名称
				map.put("RECV_BILL_TOTAL_AMT",info.getPayeeTotalAmt());//收票人收票总额
				map.put("RECV_BILL_USED_AMT",info.getPayeeUsedAmt());//收票人已收票金额(已付金额)
				map.put("RECV_BILL_UNUSED_AMT",info.getPayeeTotalAmt().subtract(info.getPayeeUsedAmt()) );//收票人可收票金额(可用余额)
				list.add(map);
			}
		}
		return list;
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
   		bean.setEbkCustNo(getStringVal(body.get("CMS_CLIENT_NO")));//网银客户号
   		bean.setOnlineAcptNo(getStringVal(body.get("ONLINE_ACPT_NO")));//在线银承编号
   		bean.setPayeeAcctName(getStringVal(body.get("PAYEE_ACCT_NAME")));//收票人名称
   		return bean;
   	}

}
