package com.mingtech.application.pool.bank.creditsys.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtInfo;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.framework.common.util.StringUtil;

/**
 * @Title: MIS接口 PJE016
 * @Description: 在线流贷协议推送接口
 * @author wss
 * @date 2021-4-28
 */
public class PJE016CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE016CreditHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	
	/**
	 * PJE016 在线流贷协议推送接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		QueryResult result = null;
		Ret ret = new Ret();
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
				bean.setOnlineCrdtNo(queryBean.getOnlineCrdtNo());
			}
			
			List list = null;
			if(StringUtil.isNotBlank(queryBean.getCustNumber())&&StringUtil.isNotBlank(queryBean.getCustName())&&StringUtil.isNotBlank(queryBean.getCustOrgcode())&&StringUtil.isNotBlank(queryBean.getOnlineCrdtNo())){				
				list = pedOnlineCrdtService.queryOnlineProtocolList(bean);
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("协议信息填写不全，请检查客户号、客户名称、客户组织机构代码、在线协议编号字段！");
				response.setRet(ret);
				return response;
			}
			
			
			
			//推送类型判断 0 新增；1修改
			if(PublicStaticDefineTab.MOD01.equals(queryBean.getModeType())){ 
				if(null != list && list.size()>0){
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("协议已存在不允许新增|");
				}else{
					//流贷规则校验 
					ret = pedOnlineCrdtService.onlineCrdtCheck(queryBean);
				}
			}else{ 
				if(null != list && list.size()>0){
					//流贷规则校验 
					ret = pedOnlineCrdtService.onlineCrdtCheck(queryBean);
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
				pedOnlineCrdtService.txSaveOnlineProtocol(queryBean);
				ret.setRET_MSG("协议推送成功");
			}
	} catch (Exception ex) {
		logger.error("PJE016-在线流贷协议推送!", ex);
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("在线流贷协议推送!票据池内部执行错误!");
	}
	response.setRet(ret);
	return response;
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
		bean.setModeType(getStringVal(body.get("MOD_TYPE")));//修改模式
		bean.setCustNumber(getStringVal(body.get("CORE_CLIENT_NO")));//核心客户号
		bean.setCustOrgcode(getStringVal(body.get("ORG_CODE"))); //核心客户组织机构代码
		bean.setProtocolStatus(getStringVal(body.get("ONLINE_PROTOCOL_STATUS")));//在线协议状态
		bean.setOnlineCrdtNo(getStringVal(body.get("ONLINE_LOAN_NO")));//在线流贷编号
		bean.setBpsNo(getStringVal(body.get("BPS_NO")));//票据池编号
		bean.setBaseCreditNo(getStringVal(body.get("BASE_CREDIT_LIMIT_NO")));//基本授信额度编号
		bean.setCustName(getStringVal(body.get("CLIENT_NAME")));//客户名称
		bean.setEbkCustNo(getStringVal(body.get("CMS_CLIENT_NO")));//网银客户号
		bean.setPoolCreditRatio(getBigDecimalVal(body.get("BPS_LIMIT_RATE")));//票据池额度比例（%）
		bean.setOnlineLoanTotal(getBigDecimalVal(body.get("ONLINE_LOAN_TOTAL_AMT")));//在线流贷总额
		bean.setBaseRateType(getStringVal(body.get("BASE_RATE_TYPE")));//基准利率类型
		bean.setRateFloatType(getStringVal(body.get("RATE_FLOAT_TYPE")));//利率浮动方式
		bean.setRateFloatValue(getBigDecimalVal(body.get("RATE_FLOAT_VALUE")));//利率浮动值（%）
		bean.setOverRateFloatType(getStringVal(body.get("OVER_RATE_FLOAT_TYPE")));//逾期利率浮动方式
		bean.setOverRateFloatValue(getBigDecimalVal(body.get("OVER_RATE_FLOAT_VALUE")));//逾期利率浮动值（%）
		bean.setMakeLoanType(getStringVal(body.get("LOAN_TYPE")));//放款方式
		bean.setRepaymentType(getStringVal(body.get("REPAY_TYPE")));//还款方式
		bean.setIsAutoDeduct(getStringVal(body.get("AUTO_DEDUCT_FLAG")));//是否自动扣划本息
		bean.setIsDiscInterest(getStringVal(body.get("INTE_FLAG")));//是否贴息
		bean.setLoanAcctNo(getStringVal(body.get("LOAN_ACCT_NO")));//放款账户账号
		bean.setLoanerAcctName(getStringVal(body.get("LOAN_ACCT_NAME")));//放款账户名称
		bean.setDeduAcctNo(getStringVal(body.get("DEDU_ACCT_NO")));//扣款账户账号
		bean.setDeduAcctName(getStringVal(body.get("DEDU_ACCT_NAME")));//扣款账户名称
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
		
		List details = request.getDetails();
		if(null != details && details.size()>0){
			PedOnlineCrdtInfo info;
			PedOnlineMsgInfo msg;
			for(int i=0;i<details.size();i++){
				HashMap map = (HashMap) details.get(i);
				//收款人
				if(StringUtils.isNotEmpty(getStringVal(map.get("PAYEE_CLIENT_NO")))){
					info =  new PedOnlineCrdtInfo();
					info.setPayeeId(getStringVal(map.get("PAYEE_CLIENT_NO")));//收票人编号
					info.setPayeeAcctName(getStringVal(map.get("PAYEE_CLIENT_NAME")));//收票人名称
					if(PublicStaticDefineTab.ZI_ZHU_ZHI_FU.equals(info.getPayeeAcctName())){
						info.setPayType(PublicStaticDefineTab.PAY_1);
					}else{
						info.setPayType(PublicStaticDefineTab.PAY_2);
					}
					info.setPayeeAcctNo(getStringVal(map.get("PAYEE_ACCT_NO")));//收票人账号
					info.setPayeeOpenBankNo(getStringVal(map.get("PAYEE_BANK_NO")));//收票人开户行行号
					info.setPayeeOpenBankName(getStringVal(map.get("PAYEE_BANK_NAME")));//收票人开户行名称
					info.setPayeeTotalAmt(getBigDecimalVal(map.get("PAYEE_TOTAL_AMT")));//收票人收票总额
					info.setIsLocal(getStringVal(map.get("OVER_BANK_FLAG")));//是否跨行
					info.setModeType(getStringVal(map.get("MOD_TYPE")));//修改标识
					info.setOnlineCrdtNo(getStringVal(body.get("ONLINE_LOAN_NO")));//在线流贷编号
					info.setPayeeStatus(getStringVal(map.get("STATUS")));//状态
					info.setCreateTime(new Date());
					bean.getPayees().add(info);
				}
			}
			for(int f=0;f<details.size();f++){
				HashMap map = (HashMap) details.get(f);
				//短信
				if(StringUtils.isNotEmpty(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NO")))){
					msg = new PedOnlineMsgInfo();
					msg.setAddresseeNo(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NO")));
					msg.setAddresseeName(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NAME")));
					msg.setAddresseeRole(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_TYPE")));
					msg.setAddresseePhoneNo(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTACT_PHONE_NO")));
					msg.setModeType(getStringVal(map.get("CONTECT_INFO_ARRAY.MOD_TYPE")));
					msg.setOnlineProtocolType(PublicStaticDefineTab.PRODUCT_002);
					msg.setCreateTime(new Date());
					bean.getDetalis().add(msg);
				}
			}
		}
		return bean;
	}
	
	
}
