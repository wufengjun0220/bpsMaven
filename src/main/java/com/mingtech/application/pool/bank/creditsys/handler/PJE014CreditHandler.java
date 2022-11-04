package com.mingtech.application.pool.bank.creditsys.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.util.StringUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptInfo;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.framework.common.util.StringUtil;

/**
 * @Title: MIS接口 PJE014
 * @Description: 在线银承协议推送
 * @author wss
 * @date 2021-04-25
 */
public class PJE014CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE014CreditHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired
	private CustomerService customerService;

	/**
	 * MIS接口 PJE014 在线协议推送
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		try {
				OnlineQueryBean queryBean = QueryParamMap(request);				
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setCustNumber(queryBean.getCustNumber());
				bean.setCustName(queryBean.getCustName());
				bean.setCustOrgcode(queryBean.getCustOrgcode());
				if(PublicStaticDefineTab.MOD01.equals(queryBean.getModeType())){
					bean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
				}
				if(PublicStaticDefineTab.MOD02.equals(queryBean.getModeType())){
					bean.setOnlineAcptNo(queryBean.getOnlineAcptNo());
				}
				//bean.setEbkCustNo(queryBean.getEbkCustNo());
				
				if(StringUtil.isBlank(queryBean.getCustNumber())||StringUtil.isBlank(queryBean.getCustName())||StringUtil.isBlank(queryBean.getCustOrgcode())
						||StringUtil.isBlank(queryBean.getOnlineAcptNo())||StringUtil.isBlank(queryBean.getEbkCustNo())){
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("协议信息填写不全，请检查客户号、客户名称、客户组织机构代码、在线协议编号、网银客户号");
					response.setRet(ret);
					return response;
				}
				
				PedOnlineAcptProtocol protocol = pedOnlineAcptService.queryOnlineAcptProtocol(bean);
				if(PublicStaticDefineTab.MOD01.equals(queryBean.getModeType())){
					if(null != protocol){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("协议已存在不允许新增|");
					}else{
						//银承规则校验 
						ret = pedOnlineAcptService.onlineAcptCheck(queryBean);
					}
				}else{
					if(null != protocol){
						//银承规则校验 
						ret = pedOnlineAcptService.onlineAcptCheck(queryBean);
					}else{
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("该协议不存在|");
					}
				}
				if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
					if(null != ret.getSomeList()){
						logger.error(ret.getSomeList().toString());
						ret.setRET_MSG(ret.getSomeList().toString());
					}
				}else{
					pedOnlineAcptService.txSaveOnlineAcptPtl(queryBean);
					ret.setRET_MSG("协议推送成功");
					//生成协议客户信息对象
					CustomerRegister customer = new CustomerRegister();
					customer.setCustNo(queryBean.getCustNumber());
					customer.setCustName(queryBean.getCustName());
					customer.setFirstDateSource("PJE014");
					customerService.txSaveCustomerRegister(customer);
				}
		} catch (Exception ex) {
			logger.error("PJE014-在线银承协议推送!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线银承协议推送!票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * @Description: 请求数据处理
	 * @param request
	 * @return OnlineQueryBean
	 * @author wss
	 * @date 2021-4-25
	 */
	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		OnlineQueryBean bean = new OnlineQueryBean();
		Map body = request.getBody();
		//主协议信息
		bean.setCustNumber(getStringVal(body.get("CORE_CLIENT_NO"))); //核心客户号
		bean.setCustOrgcode(getStringVal(body.get("ORG_CODE"))); //核心客户组织机构代码
		bean.setProtocolStatus(getStringVal(body.get("ONLINE_PROTOCOL_STATUS")));//在线协议状态
		bean.setOnlineAcptNo(getStringVal(body.get("ONLINE_ACPT_NO")));//在线银承编号
		bean.setBpsNo(getStringVal(body.get("BPS_NO")));//票据池编号
		bean.setBaseCreditNo(getStringVal(body.get("BASE_CREDIT_LIMIT_NO")));//基本授信额度编号
		bean.setCustName(getStringVal(body.get("CLIENT_NAME")));//客户名称
		bean.setEbkCustNo(getStringVal(body.get("CMS_CLIENT_NO")));//网银客户号
		bean.setOnlineAcptTotal(getBigDecimalVal(body.get("ONLINE_ACPT_TOTAL_AMT")));//在线银承总额
		bean.setAcceptorBankNo(getStringVal(body.get("ACCEPTOR_BANK_NO")));//承兑人承兑行行号
		bean.setAcceptorBankName(getStringVal(body.get("ACCEPTOR_BANK_NAME")));//承兑人承兑行名称
		bean.setDepositAcctNo(getStringVal(body.get("DEPOSIT_ACCT_NO")));//扣收保证金账号
		bean.setDepositAcctName(getStringVal(body.get("DEPOSIT_ACCT_NAME")));//扣收保证金账户名称
		bean.setDepositRateLevel(StringUtils.isEmpty(getStringVal(body.get("DEPOSIT_RATE_LEVEL"))) ? "0" : getStringVal(body.get("DEPOSIT_RATE_LEVEL")));//保证金利率档次
		bean.setDepositRateFloatFlag(getStringVal(body.get("DEPOSIT_RATE_FLOAT_FLAG")));//保证金利率浮动标志
		bean.setDepositRateFloatValue(getBigDecimalVal(body.get("DEPOSIT_RATE_FLOAT_VALUE")));//保证金利率浮动值
		bean.setDepositRatio(getBigDecimalVal(body.get("DEPOSIT_RATE")));//保证金比例（%）
		bean.setPoolCreditRatio(getBigDecimalVal(body.get("BPS_LIMIT_USE_RATE")));//票据池额度占用比例（%）
		bean.setFeeRate(getBigDecimalVal(body.get("FEE_RATE")));//手续费率（%）
		bean.setInAcctBranchNo(getStringVal(body.get("IN_ACCT_BRANCH_NO")));//入账机构所号
		bean.setContractNo(getStringVal(body.get("GUARANTEE_CONTRACT_NO")));//担保合同编号
		bean.setGuarantor(getStringVal(body.get("GUARANTOR_CLIENT_NAME")));//担保人名称
		bean.setGuarantorNo(getStringVal(body.get("GUARANTOR_CORE_CLIENT_NO")));//担保人核心客户号
		bean.setAppName(getStringVal(body.get("APP_CLIENT_NAME")));//经办人名称
		bean.setAppNo(getStringVal(body.get("APP_CLIENT_NO")));//经办人编号
		bean.setSignBranchNo(getStringVal(body.get("SIGN_BRANCH_NO")));//签约机构号
		bean.setSignBranchName(getStringVal(body.get("SIGN_BRANCH_NAME")));//签约机构名称
		bean.setOpenDate(getDateVal(body.get("OPEN_DATE")));//开通日期
		bean.setChangeDate(getDateVal(body.get("CHANGE_DATE")));//变更日期
		bean.setDueDate(getDateVal(body.get("EXPIRY_DATE")));//到期日期
		bean.setModeType(getStringVal(body.get("MOD_TYPE")));//修改标识
		
		//收票人 从文件中取出
		List details = request.getDetails();
		if(null != details && details.size()>0){
			PedOnlineAcptInfo payee;
			PedOnlineMsgInfo msg;
			for(int j=0;j<details.size();j++){
				Map map = (Map) details.get(j);
				if(StringUtil.isNotEmpty(getStringVal(map.get("RECV_BILL_CLIENT_NO")))){
					payee = new PedOnlineAcptInfo();
					payee.setPayeeId(getStringVal(map.get("RECV_BILL_CLIENT_NO")));
					payee.setPayeeCustName(getStringVal(map.get("RECV_CLIENT_NAME")));//收票人客户名称
					payee.setPayeeAcctName(getStringVal(map.get("RECV_BILL_CLIENT_NAME")));
					payee.setPayeeAcctNo(getStringVal(map.get("RECV_BILL_ACCT_NO")));
					payee.setPayeeOpenBankNo(getStringVal(map.get("RECV_BILL_BANK_NO")));
					payee.setPayeeOpenBankName(getStringVal(map.get("RECV_BILL_BANK_NAME")));
					payee.setPayeeTotalAmt(getBigDecimalVal(map.get("RECV_BILL_TOTAL_AMT")));
					payee.setModeType(getStringVal(map.get("MOD_TYPE")));
					payee.setOnlineAcptNo(getStringVal(body.get("ONLINE_ACPT_NO")));//在线银承编号
					payee.setPayeeStatus(getStringVal(map.get("STATUS")));//状态
					payee.setCreateTime(new Date());
					bean.getPayees().add(payee);
				}
			}
			for(int j=0;j<details.size();j++){
				Map map = (Map) details.get(j);
				if(StringUtil.isNotEmpty(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NO")))){
					msg = new PedOnlineMsgInfo();
					msg.setAddresseeNo(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NO")));
					msg.setAddresseeName(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NAME")));
					msg.setAddresseeRole(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_TYPE")));
					msg.setAddresseePhoneNo(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTACT_PHONE_NO")));
					msg.setModeType(getStringVal(map.get("CONTECT_INFO_ARRAY.MOD_TYPE")));
					msg.setOnlineProtocolType(PublicStaticDefineTab.PRODUCT_001);
					msg.setCreateTime(new Date());
					bean.getDetalis().add(msg);
				}
			}
		}
		return bean;
	}
	
}
