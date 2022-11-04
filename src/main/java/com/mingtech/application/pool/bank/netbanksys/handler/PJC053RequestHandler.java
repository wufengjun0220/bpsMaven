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
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;

/**
 * @Title: EBK 接口 PJC053
 * @Description 收款人信息查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC053RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC051RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
    	ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
				OnlineQueryBean queryBean = QueryParamMap(request);
				queryBean.setPayeeStatus(PublicStaticDefineTab.STATUS_1);
				queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
//				List payees = pedOnlineCrdtService.queryOnlineCrdtPayeeListByBean(queryBean, null);
				List payees = pedOnlineCrdtService.queryOnlineCrdtPayeeListForPjc053(queryBean, null);
				if (!payees.isEmpty()) {
					OnlineQueryBean info = (OnlineQueryBean)payees.get(0);
					response.getBody().put("ONLINE_CRDT_NO", info.getOnlineCrdtNo());//在线流贷编号
					//收款人明细数据处理
					List details = this.detailDateHandler(payees);
					response.setDetails(details);
					//文件路径
					String path = FileName.getFileNameClient(request.getTxCode())+".txt";
					response.getFileHead().put("FILE_FLAG", "2");
					response.getFileHead().put("FILE_PATH", path);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("在线流贷收款人查询,共"+payees.size()+"条");
				}else{
					response.getFileHead().put("FILE_FLAG", "0");
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("无符合条件数据");
				}
		} catch (Exception ex) {
			logger.error("PJC053-在线流贷收款人结果查询!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线流贷收款人结果查询!票据池内部执行错误!");
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
				Map map = new HashMap();;
				OnlineQueryBean info = (OnlineQueryBean) payees.get(i);
				map.put("PAYEE_CLIENT_NO",info.getPayeeId());//收款人编号
				map.put("PAYEE_CLIENT_NAME",info.getPayeeAcctName());//收款人名称
				map.put("PAYEE_ACCT_NO",info.getPayeeAcctNo());//收款人账号
				map.put("PAYEE_BANK_NO",info.getPayeeOpenBankNo());//收款人开户行行号
				map.put("PAYEE_BANK_NAME",info.getPayeeOpenBankName());//收款人开户行名称
				map.put("PAYEE_TOTAL_AMT",info.getPayeeTotalAmt());//收款人收款总额
				map.put("PAYEE_USED_AMT",info.getPayeeUsedAmt() );//收款人已收款金额(已付金额)
				map.put("PAYEE_UNUSED_AMT",info.getPayeeTotalAmt().subtract(info.getPayeeUsedAmt()) );//收款人可收款金额(可用余额)
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
   		bean.setOnlineCrdtNo(getStringVal(body.get("ONLINE_CRDT_NO")));//在线流贷编号
   		bean.setPayeeAcctName(getStringVal(body.get("PAYEE_ACCT_NAME")));//收款人名称
   		return bean;
   	}


}
