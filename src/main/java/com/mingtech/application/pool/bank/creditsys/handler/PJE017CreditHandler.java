package com.mingtech.application.pool.bank.creditsys.handler;

import java.math.BigDecimal;
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
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtInfo;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.framework.common.util.DateUtils;

/**
 * @Title: PJE017
 * @Description: 在线流贷协议查询接口
 * @author wss
 * @date 2021-4-29
 */
public class PJE017CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE017CreditHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCoreService poolCoreService;


	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		QueryResult result = null;
		Ret ret = new Ret();
		try {
				OnlineQueryBean queryBean = QueryParamMap(request);
				queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
				List list = pedOnlineCrdtService.queryOnlineProtocolList(queryBean,null);
				if (null != list && list.size()>0) {
					
					OnlineQueryBean ptl =  (OnlineQueryBean) list.get(0);
					if(StringUtils.isNotBlank(ptl.getBpsId())){
						PedProtocolDto pool = (PedProtocolDto) pedProtocolService.load(ptl.getBpsId(),PedProtocolDto.class);
						response.getBody().put("BUSS_MODE", pool.getPoolMode());//业务模式(池模式)
					}
					response.getBody().put("CORE_CLIENT_NO", ptl.getCustNumber());//核心客户号 
					response.getBody().put("ONLINE_PROTOCOL_STATUS", ptl.getProtocolStatus());//在线协议状态 
					response.getBody().put("ONLINE_LOAN_NO", ptl.getOnlineCrdtNo());//在线流贷编号 
					response.getBody().put("BPS_NO", ptl.getBpsNo());//票据池编号 
					response.getBody().put("BASE_CREDIT_LIMIT_NO", ptl.getBaseCreditNo());//基本授信额度编号 
					response.getBody().put("CLIENT_NAME", ptl.getCustName());//客户名称 
					response.getBody().put("CMS_CLIENT_NO", ptl.getEbkCustNo());//网银客户号 
					response.getBody().put("BPS_LIMIT_RATE", ptl.getPoolCreditRatio());//票据池额度比例（%） 
					response.getBody().put("ONLINE_LOAN_TOTAL_AMT", ptl.getOnlineLoanTotal());//在线流贷总额 
					response.getBody().put("BASE_RATE_TYPE", ptl.getBaseRateType());//基准利率类型 
					response.getBody().put("RATE_FLOAT_TYPE", ptl.getRateFloatType());//利率浮动方式 
					response.getBody().put("RATE_FLOAT_VALUE", ptl.getRateFloatValue());//利率浮动值（%） 
					response.getBody().put("OVER_RATE_FLOAT_TYPE", ptl.getOverRateFloatType());//逾期利率浮动方式 
					response.getBody().put("OVER_RATE_FLOAT_VALUE", ptl.getOverRateFloatValue());//逾期利率浮动值（%） 
					response.getBody().put("LOAN_TYPE", ptl.getMakeLoanType());//放款方式 
					response.getBody().put("REPAY_TYPE", ptl.getRepaymentType());//还款方式 
					response.getBody().put("AUTO_DEDUCT_FLAG", ptl.getIsAutoDeduct());//是否自动扣划本息 
					response.getBody().put("INTE_FLAG", ptl.getIsDiscInterest());//是否贴息 
					BigDecimal rate = pedOnlineCrdtService.queryRatefromLPR();
         			if(null != rate){
         				response.getBody().put("BASE_RATE", rate);//基准利率
         			}
         			//根据基准利率值/利率浮动方式/利率浮动值计算后反馈企业网银
         			//与基准保持一致，取值为基准利率；按实点浮动（1实点=1%），取值为基准利率+利率浮动值
         			if("0".equals(ptl.getRateFloatType())){
         				response.getBody().put("LOAN_INT_RATE", rate);//贷款利率
         			}else if("1".equals(ptl.getRateFloatType())){
         				response.getBody().put("LOAN_INT_RATE", rate.add(ptl.getRateFloatValue()));//贷款利率
         			}
         			
         			/**
         			 * 查询协议对象
         			 */
         			PedOnlineCrdtProtocol pro = (PedOnlineCrdtProtocol) pedOnlineCrdtService.load(ptl.getId(),PedOnlineCrdtProtocol.class);
         			
         			CoreTransNotes transNotes = new CoreTransNotes();
         			transNotes.setAccNo(ptl.getLoanAcctNo());//放款账户账号 
         			transNotes.setCurrentFlag("1");
         			ReturnMessageNew re = poolCoreService.PJH716040Handler(transNotes, "1");
         			if (re.isTxSuccess()) {
         				Map map = re.getBody();
         				pro.setLoanAcctName((String) map.get("CLIENT_NAME"));// 放款账户账号 
         				response.getBody().put("LOAN_ACCT_NAME", (String) map.get("CLIENT_NAME"));//放款账户名称
         			} else{
         				response.getBody().put("LOAN_ACCT_NAME", ptl.getLoanAcctName());//放款账户名称 
         			}
         			
         			transNotes.setAccNo(ptl.getDeduAcctNo());//扣款账户账号 
         			transNotes.setCurrentFlag("1");
         			ReturnMessageNew re2 = poolCoreService.PJH716040Handler(transNotes, "1");
         			if (re2.isTxSuccess()) {
         				Map map = re.getBody();
         				pro.setDeduAcctName((String) map.get("CLIENT_NAME"));// 扣款账户名称 
         				response.getBody().put("DEDU_ACCT_NAME", (String) map.get("CLIENT_NAME"));//扣款账户名称 
         			} else{
         				response.getBody().put("DEDU_ACCT_NAME", ptl.getDeduAcctName());//扣款账户名称 
         			}
         			pedOnlineCrdtService.txStore(pro);
         			
					response.getBody().put("LOAN_ACCT_NO", ptl.getLoanAcctNo());//放款账户账号 
					
					response.getBody().put("DEDU_ACCT_NO", ptl.getDeduAcctNo());//扣款账户账号 
					
					response.getBody().put("IN_ACCT_BRANCH_NO", ptl.getInAcctBranchNo());//入账机构所号 
					response.getBody().put("GUARANTEE_CONTRACT_NO", ptl.getContractNo());//担保合同编号 
					response.getBody().put("GUARANTOR_CLIENT_NAME", ptl.getGuarantor());//担保人名称 
					response.getBody().put("GUARANTOR_CORE_CLIENT_NO", ptl.getCustNumber());//担保人核心客户号 
					response.getBody().put("APP_CLIENT_NAME", ptl.getAppName());//经办人名称 
					response.getBody().put("APP_CLIENT_NO", ptl.getAppNo());//经办人编号 
					response.getBody().put("SIGN_BRANCH_NO", ptl.getSignBranchNo());//签约机构号 
					response.getBody().put("SIGN_BRANCH_NAME", ptl.getSignBranchName());//签约机构名称 
					response.getBody().put("OPEN_DATE", DateUtils.formatDateToString(ptl.getOpenDate(),DateUtils.ORA_DATE_FORMAT));//开通日期 
					if(null != ptl.getChangeDate()){
						response.getBody().put("CHANGE_DATE", DateUtils.formatDateToString(ptl.getChangeDate(),DateUtils.ORA_DATE_FORMAT));//变更日期 
					}else{
						response.getBody().put("CHANGE_DATE", ptl.getChangeDate());//变更日期 
					}
					response.getBody().put("EXPIRY_DATE", DateUtils.formatDateToString(ptl.getDueDate(),DateUtils.ORA_DATE_FORMAT));//到期日期 
					response.getBody().put("USED_AMT", ptl.getUsedAmt());//已用额度 
					response.getBody().put("UNUSED_AMT", ptl.getOnlineLoanTotal().subtract(ptl.getUsedAmt()));//未用额度 
					response.getBody().put("IN_ACCT_BRANCH_NAME", ptl.getInAcctBranchName());//入账机构名称 
					
					
					//收款人
					queryBean.setOnlineCrdtNo(ptl.getOnlineCrdtNo());
					queryBean.setOnlineNo(ptl.getOnlineCrdtNo());
					List payees = pedOnlineCrdtService.queryOnlineCrdtPayeeListByBean(queryBean, null);
					if(null !=payees && payees.size()>0){
						payees = this.payeesDateHandler(payees);
						String path = FileName.getFileNameClient(request.getTxCode())+".txt";
						response.getFileHead().put("FILE_FLAG", "2");
						response.getFileHead().put("FILE_PATH", path);
					}else{
						response.getFileHead().put("FILE_FLAG", "0");
					}
					
					//短信通知人
					List msgs = onlineManageService.queryOnlineMsgInfoList(ptl.getOnlineCrdtNo(),PublicStaticDefineTab.ROLE_1);
					List details = this.detailMsgDateHandler(msgs);
					
					response.setDetails(details);
					response.setDetails(payees);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("在线流贷协议查询");
				}else{
					ret.setRET_CODE(Constants.CREDIT_06);
					ret.setRET_MSG("无生效的在线流贷协议信息");
				}
		} catch (Exception ex) {
			logger.error("PJE017-在线流贷协议查询!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线流贷协议查询!票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}
	
	/**
	 * @Title payeesDateHandler
	 * @author wss
	 * @date 2021-5-25
	 * @Description 处理收款人
	 * @return List
	 */
	private List payeesDateHandler(List payees) {
		List list = new ArrayList();
		for(int i=0;i<payees.size();i++){
			OnlineQueryBean info = (OnlineQueryBean) payees.get(i);
			Map map = new HashMap();
			map.put("PAYEE_CLIENT_NO", info.getPayeeId());//收款人编号
			map.put("PAYEE_CLIENT_NAME", info.getPayeeAcctName());//收款人名称
			map.put("PAYEE_ACCT_NO", info.getPayeeAcctNo());//收款人账号
			map.put("PAYEE_BANK_NO", info.getPayeeOpenBankNo());//收款人开户行行号
			map.put("PAYEE_BANK_NAME", info.getPayeeOpenBankName());//收款人开户行名称
			map.put("PAYEE_TOTAL_AMT", info.getPayeeTotalAmt());//收款人收款总额
			map.put("OVER_BANK_FLAG", info.getIsLocal());//跨行标识
			map.put("MOD_TYPE", info.getModeType());//修改标识
			map.put("PAYEE_USED_AMT", info.getPayeeUsedAmt());//收款人已收票金额
			map.put("STATUS", info.getPayeeStatus());//状态
			map.put("PAYEE_UNUSED_AMT", info.getPayeeTotalAmt().subtract(info.getPayeeUsedAmt()));//收款人可收票金额
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
				map.put("CONTECT_INFO_ARRAY.MOD_TYPE", info.getModeType());
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
		bean.setCustNumber(getStringVal(body.get("CORE_CLIENT_NO")));//票据池编号
		bean.setBpsNo(getStringVal(body.get("BPS_NO"))); //在线银承编号
		bean.setOnlineCrdtNo(getStringVal(body.get("ONLINE_LOAN_NO"))); //在线银承编号
		bean.setCustName(getStringVal(body.get("CLIENT_NAME"))); //客户名称
		bean.setEbkCustNo(getStringVal(body.get("CMS_CLIENT_NO"))); //网银客户号

		return bean;
	}
	
	
}
