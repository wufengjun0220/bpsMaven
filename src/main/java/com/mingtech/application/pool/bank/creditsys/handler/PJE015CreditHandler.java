package com.mingtech.application.pool.bank.creditsys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.FileName;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptInfo;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.framework.common.util.DateUtils;

/**
 * @Title: PJE015
 * @Description: 在线银承协议查询接口
 * @author wss
 * @date 2021-4-27
 */
public class PJE015CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE015CreditHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PedProtocolService pedProtocolService;

	/**
	 * PJE015 在线银承协议查询接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
				OnlineQueryBean queryBean = QueryParamMap(request);
				queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
				List list = pedOnlineAcptService.queryOnlineAcptProtocolList(queryBean, null);
				if (null == list || list.size()==0) {
					ret.setRET_CODE(Constants.CREDIT_06);
					ret.setRET_MSG("无生效的在线银承协议信息");
				} else {
					OnlineQueryBean ptl = (OnlineQueryBean) list.get(0);
					if(StringUtils.isNotBlank(ptl.getBpsId())){
						PedProtocolDto pool = (PedProtocolDto) pedProtocolService.load(ptl.getBpsId(),PedProtocolDto.class);
						response.getBody().put("BUSS_MODE", pool.getPoolMode());//业务模式(池模式)
					}
					
					response.getBody().put("CORE_CLIENT_NO", ptl.getCustNumber());//核心客户号 
					response.getBody().put("ONLINE_PROTOCOL_STATUS", ptl.getProtocolStatus());//在线协议状态 
					response.getBody().put("ONLINE_ACPT_NO", ptl.getOnlineAcptNo());//在线银承编号 
					response.getBody().put("BPS_NO", ptl.getBpsNo());//票据池编号 
					response.getBody().put("BASE_CREDIT_LIMIT_NO", ptl.getBaseCreditNo());//基本授信额度编号 
					response.getBody().put("CLIENT_NAME", ptl.getCustName());//客户名称 
					response.getBody().put("CMS_CLIENT_NO", ptl.getEbkCustNo());//网银客户号 
					response.getBody().put("ONLINE_ACPT_TOTAL_AMT", ptl.getOnlineAcptTotal());//在线银承总额 
//					response.getBody().put("ACCEPTOR_BANK_NO",  "????");//出票人开户行行号
//					response.getBody().put("ACCEPTOR_BANK_NAME",  "????");//出票人开户行名称
					response.getBody().put("ACCEPTOR_BANK_NO", ptl.getAcceptorBankNo());//承兑人承兑行行号 
					response.getBody().put("ACCEPTOR_BANK_NAME", ptl.getAcceptorBankName());//承兑人承兑行名称 
					
					/**
         			 * 查询协议对象
         			 */
         			PedOnlineAcptProtocol pro = (PedOnlineAcptProtocol) pedOnlineAcptService.load(ptl.getId(),PedOnlineAcptProtocol.class);

         			response.getBody().put("DEPOSIT_ACCT_NO", ptl.getDepositAcctNo());//扣收保证金账号 
					
					CoreTransNotes note = new CoreTransNotes();
	        		note.setAccNo(ptl.getDepositAcctNo());
	        		ReturnMessageNew reslut = poolCoreService.PJH716040Handler(note,"0");
	        		 if (reslut.isTxSuccess()) {
	        			 Map map = reslut.getBody();
	        			 if(null !=map.get("BALANCE")){
			            	response.getBody().put("DEPOSIT_ACCT_BALANCE", BigDecimalUtils.valueOf((String) map.get("BALANCE")));//扣款账户余额
	        			 }
	        			 pro.setDepositAcctName((String) map.get("CLIENT_NAME"));// 放款账户账号 
         				 response.getBody().put("DEPOSIT_ACCT_NAME", (String) map.get("CLIENT_NAME"));//放款账户名称
	        		 }else{
	        			 response.getBody().put("DEPOSIT_ACCT_NAME", ptl.getDepositAcctName());//扣收保证金账户名称 
	        		 }
	        		 pedOnlineAcptService.txStore(pro);
	        		 
					response.getBody().put("DEPOSIT_RATE_LEVEL", ptl.getDepositRateLevel());//保证金利率档次 
					response.getBody().put("DEPOSIT_RATE_FLOAT_FLAG", ptl.getDepositRateFloatFlag());//保证金利率浮动标志 
					response.getBody().put("DEPOSIT_RATE_FLOAT_VALUE", ptl.getDepositRateFloatValue());//保证金利率浮动值 
					response.getBody().put("DEPOSIT_RATE", ptl.getDepositRatio());//保证金比例（%） 
					response.getBody().put("BPS_LIMIT_USE_RATE", ptl.getPoolCreditRatio());//票据池额度占用比例（%） 
					response.getBody().put("FEE_RATE", ptl.getFeeRate());//手续费率（%） 
					response.getBody().put("IN_ACCT_BRANCH_NO", ptl.getInAcctBranchNo());//入账机构所号 
					response.getBody().put("GUARANTEE_CONTRACT_NO", ptl.getContractNo());//担保合同编号 
					response.getBody().put("GUARANTOR_CLIENT_NAME", ptl.getGuarantor());//担保人名称 
					response.getBody().put("GUARANTOR_CORE_CLIENT_NO", ptl.getCustNumber());//担保人核心客户号 
					response.getBody().put("APP_CLIENT_NAME", ptl.getAppName());//经办人名称 
					response.getBody().put("APP_CLIENT_NO", ptl.getAppNo());//经办人编号 
					response.getBody().put("SIGN_BRANCH_NO", ptl.getSignBranchNo());//签约机构号 
					response.getBody().put("SIGN_BRANCH_NAME", ptl.getSignBranchName());//签约机构名称 
					response.getBody().put("OPEN_DATE", DateUtils.formatDateToString(ptl.getOpenDate(),DateUtils.ORA_DATE_FORMAT));//开通日期 
					response.getBody().put("CHANGE_DATE", DateUtils.formatDateToString(ptl.getChangeDate(),DateUtils.ORA_DATE_FORMAT));//变更日期 
					response.getBody().put("EXPIRY_DATE", DateUtils.formatDateToString(ptl.getDueDate(),DateUtils.ORA_DATE_FORMAT));//到期日期 
					response.getBody().put("USED_AMT", ptl.getUsedAmt());//已用额度 
					response.getBody().put("UNUSED_AMT", ptl.getOnlineAcptTotal().subtract(ptl.getUsedAmt()));//未用额度 
					response.getBody().put("IN_ACCT_BRANCH_NAME", ptl.getInAcctBranchName());//入账机构名称 
					
					//收票人
					List payees = pedOnlineAcptService.queryOnlineAcptPayeeListBean(ptl.getOnlineAcptNo(),null);
					
					payees = this.payeesDateHandler(payees);
					if(null !=payees && payees.size()>0){
						String path = FileName.getFileNameClient(request.getTxCode())+".txt";
						response.getFileHead().put("FILE_FLAG", "2");
						response.getFileHead().put("FILE_PATH", path);
					}else{
						response.getFileHead().put("FILE_FLAG", "0");
					}
					
					//短信通知人
					List msgs = onlineManageService.queryOnlineMsgInfoList(queryBean.getOnlineAcptNo(),PublicStaticDefineTab.ROLE_1);
					List details = this.detailMsgDateHandler(msgs);
					
					response.setDetails(details);
					response.setDetails(payees);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("在线银承协议查询");
				}
		} catch (Exception ex) {
			logger.error("PJE015-在线银承协议查询!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线银承协议查询!票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}
	
	/**
	 * @Title payeesDateHandler
	 * @author wss
	 * @date 2021-5-25
	 * @Description 处理收票人
	 * @return List
	 */
	private List payeesDateHandler(List payees) {
		List list = new ArrayList();
		for(int i= 0;i<payees.size();i++){
			OnlineQueryBean info = (OnlineQueryBean) payees.get(i);
			Map map = new HashMap();
			map.put("RECV_BILL_CLIENT_NO", info.getPayeeId());//收票人编号
			map.put("RECV_BILL_CLIENT_NAME", info.getPayeeAcctName());//收票人名称
			map.put("RECV_BILL_ACCT_NO", info.getPayeeAcctNo());//收票人账号
			map.put("RECV_BILL_BANK_NO", info.getPayeeOpenBankNo());//收票人开户行行号
			map.put("RECV_BILL_BANK_NAME", info.getPayeeOpenBankName());//收票人开户行名称
			map.put("RECV_BILL_TOTAL_AMT", info.getPayeeTotalAmt());//收票人收票总额
			map.put("MOD_TYPE", info.getModeType());//修改标识
			map.put("RECV_BILL_USED_AMT", info.getPayeeUsedAmt());//收票人已收票金额
			map.put("RECV_BILL_UNUSED_AMT", info.getPayeeTotalAmt().subtract(info.getPayeeUsedAmt()));//收票人可收票金额
			map.put("STATUS", info.getPayeeStatus());//状态

			list.add(map);
		}
		return list;
	}

	private List detailMsgDateHandler(List msgs) {
		ArrayList infoList =  new ArrayList();
		if(null != msgs && msgs.size()>0){
			HashMap map = null;
			for(int i=0;i<msgs.size();i++){
				PedOnlineMsgInfo info = (PedOnlineMsgInfo) msgs.get(i);
				map = new HashMap();
				map.put("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NO", info.getAddresseeNo());
				map.put("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NAME", info.getAddresseeName());
				map.put("CONTECT_INFO_ARRAY.CONTECT_CLIENT_TYPE", info.getAddresseeRole());
				map.put("CONTECT_INFO_ARRAY.CONTACT_PHONE_NO", info.getAddresseePhoneNo());
				infoList.add(map);
			}
		}
		return infoList;
	}

	/**
	 * @Description: 请求数据处理
	 * @param request
	 * @return OnlineQueryBean
	 * @author wss
	 * @date 2021-4-27
	 */
	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		OnlineQueryBean bean = new OnlineQueryBean();
		Map body = request.getBody();
		bean.setCustNumber(getStringVal(body.get("CORE_CLIENT_NO")));//客户号
		bean.setOnlineAcptNo(getStringVal(body.get("ONLINE_ACPT_NO"))); //在线银承编号
//		bean.setBpsNo(getStringVal(body.get("BPS_NO"))); //票据池编号
		bean.setCustName(getStringVal(body.get("CLIENT_NAME"))); //客户名称
		bean.setEbkCustNo(getStringVal(body.get("CMS_CLIENT_NO"))); //网银客户号
		return bean;
	}
	
	
}
