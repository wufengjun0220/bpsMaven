package com.mingtech.application.pool.discount.service.ipml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.discount.client.CenterPlatformClient;
import com.mingtech.application.pool.discount.domain.BankRoleMappingBean;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.IntroBillInfoBean;
import com.mingtech.application.pool.discount.domain.TxRateDetailBean;
import com.mingtech.application.pool.discount.domain.TxReviewPriceDetail;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 票据池交易 电票系统接口服务实现类
 *
 */
@Service("centerPlatformSysService") 
public class CenterPlatformSysServiceImpl extends GenericServiceImpl implements CenterPlatformSysService {
	
	private static final Logger logger = Logger.getLogger(CenterPlatformSysServiceImpl.class);
	
	@Autowired
	CenterPlatformClient centerPlatformClient;
	
	/**
	 * 在线业务开关信息变更通知接口
	 * 
	 * 系统级运营规则维护
	 * */
	@SuppressWarnings("unchecked")
	public boolean txChangeOnlineConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception {		
		if (centerPlatformBean == null) {
			logger.error("txChangeOnlineConfig:在线业务总开关信息变更通知传入参数错误！");
			throw new Exception("在线业务开关信息变更通知传入参数错误！");
		}
		
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("ONLINE_BUSS_ID", centerPlatformBean.getBusi_id());
		request.getBody().put("ONLINE_BUSS_STATUS", centerPlatformBean.getBusiFlag());
		request.getBody().put("ONLINE_DISCOUNT_BUSS_ID", centerPlatformBean.getTx_id());
		request.getBody().put("ONLINE_DISCOUNT_BUSS_STATUS", centerPlatformBean.getTxFlag());
		request.getBody().put("ONLINE_DISCOUNT_START_TIME", centerPlatformBean.getTxFlagBeginTime());
		request.getBody().put("ONLINE_DISCOUNT_END_TIME", centerPlatformBean.getTxFlagEndTime());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220428", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	
	/**
	 * 在线业务开关信息查询接口
	 * 
	 * 系统级运营规则查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryOnlineConfig(CenterPlatformBean centerPlatformBean,User user)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220427", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 机构在线业务开关信息变更通知接口
	 * 
	 * 机构在线运营规则维护
	 * */
	@SuppressWarnings("unchecked")
	public boolean txChangeDepartMentConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception {		
		if (centerPlatformBean == null) {
			logger.error("txChangeOnlineConfig:机构在线业务开关信息变更通知传入参数错误！");
			throw new Exception("机构在线业务开关信息变更通知传入参数错误！");
		}
		
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("BRANCH_ONLINE_DISCOUNT_ID", centerPlatformBean.getTx_id());
		request.getBody().put("BRANCH_ONLINE_DISCOUNT_STATUS", centerPlatformBean.getTxFlag());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220426", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 机构在线业务开关信息查询接口
	 * 
	 * 机构在线运营规则查询
	 * */
	@SuppressWarnings("unchecked")
	public ReturnMessageNew txQueryDepartMentConfig(CenterPlatformBean centerPlatformBean,User user) throws Exception {		
		
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("BRANCH_NO", centerPlatformBean.getBranchCode());
		request.getBody().put("BRANCH_NAME", centerPlatformBean.getBranchName());
		request.getBody().put("SUPER_BRANCH_NO", centerPlatformBean.getSuperBranchNo());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220425", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	
	/**
	 * 客户在线业务开关信息变更通知接口
	 * 
	 * 客户在线运营规则维护
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public boolean txChangeCustConfig(CenterPlatformBean centerPlatformBean,User user)
			throws Exception {
		if (centerPlatformBean == null) {
			logger.error("txChangeDepartMentConfig:客户在线业务开关信息变更通知传入参数错误！");
			throw new Exception("客户在线业务开关信息变更通知接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("CLIENT_ONLINE_DISCOUNT_ID", centerPlatformBean.getId());
		request.getBody().put("CLIENT_ONLINE_DISCOUNT_STATUS", centerPlatformBean.getTxFlag());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220424", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	
	/**
	 * 客户在线业务开关信息查询接口
	 * 
	 * 客户在线运营规则查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txCustConfigQuery(CenterPlatformBean centerPlatformBean,User user)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("CORE_CLIENT_NO", centerPlatformBean.getCustNo());
		request.getBody().put("CLIENT_NAME", centerPlatformBean.getCustName());
		request.getBody().put("DISCOUNT_AGREE_NO", centerPlatformBean.getOnlineNo());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageSize()*10 - 9);
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageNum());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220423", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}


	/**
	 *	承兑行黑名单查询接口
	 *	
	 * 承兑行黑名单信息查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txAcceptBankBlackListQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("ACCEPTANCE_BANK_NO", centerPlatformBean.getAcceptBankNo());		//	承兑行总行行号
		request.getBody().put("ACCEPTANCE_BANK_NAME", centerPlatformBean.getAcceptBankName());	//	承兑行总行行名
//		request.getBody().put("BLACK_LIST_STATUS","1");							//	黑名单状态  0 未生效  1 生效 2 待生效
		request.getBody().put("EFFECTIVE_START_DATE",StringUtil.isNotEmpty(centerPlatformBean.getEffStartDate())?centerPlatformBean.getEffStartDate().replaceAll("-", ""):"");	//	生效日期开始
		request.getBody().put("EFFECTIVE_END_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffEndDate())?centerPlatformBean.getEffEndDate().replaceAll("-", ""):"");		//	生效日期结束
		request.getBody().put("MAINTAIN_CLIENT_NO", centerPlatformBean.getWorkerNo());		//	维护人编号
		request.getBody().put("MAINTAIN_PERSON_NAME", centerPlatformBean.getWorkerName());	//	维护人名称
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220422", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	/**
	 * 承兑行黑名单维护接口
	 * 
	 * 承兑行黑名单设置
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public String txAcceptBankBlackListMt(List<CenterPlatformBean> centerPlatformBeans,User user) throws Exception {
		if (centerPlatformBeans == null) {
			logger.error("txChangeDepartMentConfig:承兑行黑名单维护传入参数错误！");
			throw new Exception("承兑行黑名单维护接口传入参数错误！");
		}
		
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		for (CenterPlatformBean centerPlatformBean : centerPlatformBeans) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("BLACK_LIST_INFO_ARRAY.MODIFY_TYPE", StringUtil.getStringVal(centerPlatformBean.getModifyType()));						//	修改类型
			map.put("BLACK_LIST_INFO_ARRAY.ACCEPTANCE_BANK_NO", StringUtil.getStringVal(centerPlatformBean.getAcceptBankNo()));			//	承兑行总行行号
			map.put("BLACK_LIST_INFO_ARRAY.ACCEPTANCE_BANK_NAME", StringUtil.getStringVal(centerPlatformBean.getAcceptBankName()));		//	承兑行总行行名
			map.put("BLACK_LIST_INFO_ARRAY.BLACK_LIST_STATUS", StringUtil.getStringVal(centerPlatformBean.getEffState()));				//	黑名单状态
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_CLIENT_NO", StringUtil.getStringVal(user.getLoginName()));				//	维护人编号
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_PERSON_NAME", StringUtil.getStringVal(user.getName()));			//	维护人名称	
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NO", StringUtil.getStringVal(user.getDepartment().getInnerBankCode()));				//	维护机构号
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NAME", StringUtil.getStringVal(user.getDepartment().getName()));			//	维护机构名
			map.put("BLACK_LIST_INFO_ARRAY.EFFECTIVE_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffDate())?centerPlatformBean.getEffDate().replaceAll("-", "").replaceAll(",",""):"");						//	生效日期
			request.getDetails().add(map);
		}
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220421", request);
		String responseCode = response.getRet().getRET_CODE();
		if (!responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response.getRet().getRET_MSG();
		}
		
		return "";
	}

	/**
	 * 出票人黑名单查询接口
	 * 
	 * 出票人黑名单信息查询
	 * **/
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txDrawerBlackListQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("DRAWER_NAME", centerPlatformBean.getIssuerName());			//	出票人名称
//		request.getBody().put("BLACK_LIST_STATUS", centerPlatformBean.getBlackStatus());	//	黑名单状态
		request.getBody().put("EFFECTIVE_START_DATE",StringUtil.isNotEmpty(centerPlatformBean.getEffStartDate())?centerPlatformBean.getEffStartDate().replaceAll("-", ""):"");//	生效时间开始
		request.getBody().put("EFFECTIVE_END_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffEndDate())?centerPlatformBean.getEffEndDate().replaceAll("-", ""):"");	//	生效时间结束
		request.getBody().put("MAINTAIN_CLIENT_NO", centerPlatformBean.getWorkerNo());		//	维护人编号
		request.getBody().put("MAINTAIN_PERSON_NAME", centerPlatformBean.getWorkerName());	//	维护人名称
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220420", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 出票人黑名单维护接口
	 * 
	 * 出票人黑名单设置
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public boolean txDrawerBlackListMt(List<CenterPlatformBean> centerPlatformBeans,User user) throws Exception {
		if (centerPlatformBeans == null) {
			logger.error("txChangeDepartMentConfig:出票人黑名单维护传入参数错误！");
			throw new Exception("出票人黑名单维护接口传入参数错误！");
		}
		
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		for (CenterPlatformBean centerPlatformBean : centerPlatformBeans) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("BLACK_LIST_INFO_ARRAY.MODIFY_TYPE", centerPlatformBean.getModifyType());			//	修改类型
			map.put("BLACK_LIST_INFO_ARRAY.BLACK_LIST_KEY", centerPlatformBean.getId());				//	主键ID
			map.put("BLACK_LIST_INFO_ARRAY.DRAWER_NAME", centerPlatformBean.getIssuerName());			//	出票人名称
			map.put("BLACK_LIST_INFO_ARRAY.BLACK_LIST_STATUS", centerPlatformBean.getBlackStatus());	//	黑名单状态
			map.put("BLACK_LIST_INFO_ARRAY.EFFECTIVE_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffDate())?centerPlatformBean.getEffDate().replaceAll("-", "").replaceAll(",",""):"");			//	生效日期
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_CLIENT_NO", user.getLoginName());	//	维护人编号
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_PERSON_NAME", user.getName());//	维护人名称
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NO", user.getDepartment().getInnerBankCode());	//	维护机构号
			map.put("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NAME", user.getDepartment().getName());//	维护机构名
			request.getDetails().add(map);
		}
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220419", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 贴现控制查询接口
	 * 
	 * 贴现票据校验规则查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryConfig(User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220418", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	/**
	 * 贴现控制维护接口
	 * 
	 * 贴现票据校验规则维护
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public boolean txSaveConfig(CenterPlatformBean centerPlatformBean,User user)throws Exception {
		if (centerPlatformBean == null) {
			logger.error("txSaveConfig:！");
			throw new Exception("在线贴现维护接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("AMT_CONTROL_INFO_INFO", centerPlatformBean.getId());
		request.getBody().put("AMT_CONTROL_FLAG", centerPlatformBean.getTxAmtFlag());
		request.getBody().put("MIN_AMT", !StringUtil.isEmpty(centerPlatformBean.getMinAmount())?centerPlatformBean.getMinAmount().replaceAll(",", ""):"");
		request.getBody().put("MAX_AMT", !StringUtil.isEmpty(centerPlatformBean.getMaxAmount())?centerPlatformBean.getMaxAmount().replaceAll(",", ""):"");
		request.getBody().put("DISCOUNT_CONTROL_INFO_INFO_ID", centerPlatformBean.getBusi_id());
		request.getBody().put("DISCOUNT_CONTROL_FLAG", centerPlatformBean.getTxTimeConfig());	//	贴现期限控制信息启用状态
		request.getBody().put("DISCOUNT_CONTROL_TYPE", centerPlatformBean.getTxTimeConfigType());	//	贴现期限控制
		request.getBody().put("START_DATE", centerPlatformBean.getTxMinTimeConfig());
		request.getBody().put("END_DATE", centerPlatformBean.getTxMaxTimeConfig());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220417", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	
	/**
	 * 指导利率查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryGuideRate(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("APPROVE_BATCH_NO", centerPlatformBean.getBatchNo());
		request.getAppHead().put("PER_PAGE_NUM", 120);
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220416", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 贴现有效指导利率查询接口(CP20220415)
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryAviRate(User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getAppHead().put("PER_PAGE_NUM", 120);
//		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum());
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220415", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 优惠利率查询
	 * 
	 * 审批权限利率查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryFavorRate(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("APPROVE_BATCH_NO", centerPlatformBean.getBatchNo());
		request.getAppHead().put("PER_PAGE_NUM", 120);
//		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220414", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 指导利率维护接口
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean txGuideRateMaintain(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		List<TxRateDetailBean> lists = centerPlatformBean.getTxRateDetailBeans();
		if(lists.size() == 0){
			logger.error("txRateMaintain:！");
			throw new Exception("在线贴现指导利率维护接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		request.getBody().put("OPERATION_TYPE", StringUtil.isNotEmpty(centerPlatformBean.getOpreaType())?centerPlatformBean.getOpreaType():"01");	//	新增还是修改取决于是否存在有效数据
		request.getBody().put("APPROVE_BATCH_NO", centerPlatformBean.getBatchNo());
		request.getBody().put("EFFECTIVE_DATE", centerPlatformBean.getEffTime().replaceAll("-",""));
		
		List<Map> dLists  = new ArrayList<Map>();
		for (TxRateDetailBean rateDetailBean : lists) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE", rateDetailBean.getBankType());
			map.put("INT_RATE_ARRAY.GUIDANCE_INT_RATE", rateDetailBean.getRate());
			map.put("INT_RATE_ARRAY.TERM", rateDetailBean.getTerm());
			dLists.add(map);
		}
		request.setDetails(dLists);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220413", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 优惠利率维护接口
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean txFavorRateMaintain(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		List<TxRateDetailBean> lists = centerPlatformBean.getTxRateDetailBeans();
		if(lists.size() == 0){
			logger.error("txRateMaintain:！");
			throw new Exception("在线贴现优惠利率维护接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		request.getBody().put("OPERATION_TYPE", StringUtil.isNotEmpty(centerPlatformBean.getOpreaType())?centerPlatformBean.getOpreaType():"01");	//	新增还是修改取决于是否存在有效数据
		request.getBody().put("APPROVE_BATCH_NO", centerPlatformBean.getBatchNo());
		request.getBody().put("EFFECTIVE_DATE", centerPlatformBean.getEffTime().replaceAll("-",""));
		
		List<Map> dLists  = new ArrayList<Map>();
		for (TxRateDetailBean rateDetailBean : lists) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE", rateDetailBean.getBankType());
			map.put("INT_RATE_ARRAY.AUTH_INT_RATE", rateDetailBean.getRate());
			map.put("INT_RATE_ARRAY.TERM", rateDetailBean.getTerm());
			dLists.add(map);
		}
		request.setDetails(dLists);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220412", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	
	/**
	 * 优惠利率维护接口
	 * */
	/*@SuppressWarnings("unchecked")
	@Override
	public boolean txBestFavorRateMaintain(TxRateMaintainInfo txRateMaintainInfo,User user) throws Exception {
		if(txRateMaintainInfo == null){
			throw new Exception("在线贴现最优惠利率维护接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
//		if(DateUtils.checkOverLimited(DateUtils.parseDate(txRateMaintainInfo.getEffTime()), new Date())){
//			txRateMaintainInfo.setEffState("2");
//		}else{
//			txRateMaintainInfo.setEffState("1");
//		}
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("MAX_DISCOUNT_INT_RATE", txRateMaintainInfo.getBestRate());	
		request.getBody().put("BATCH_NO", txRateMaintainInfo.getBatchNo());	
		request.getBody().put("EFFECTIVE_DATE", txRateMaintainInfo.getEffTime().replaceAll("-", ""));
		request.getBody().put("MAX_DISCOUNT_RATE_STATUS", txRateMaintainInfo.getEffState());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220411", request);

		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}*/
	
	
	/**
	 * 票据审价信息查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew billPriceQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		request.getBody().put("AGREE_NO", centerPlatformBean.getOnlineNo());			//	协议编号
		request.getBody().put("DISCOUNT_APPROVE_NO", centerPlatformBean.getBatchNo());	//	贴现定价审批编号
		request.getBody().put("DISCOUNT_TYPE", centerPlatformBean.getEffState());		//	贴现类型
		request.getBody().put("BILL_NO", centerPlatformBean.getBillNo());				//	票据编号
		request.getBody().put("BILL_MIN_AMT", StringUtil.isNotEmpty(centerPlatformBean.getStartAmount())?centerPlatformBean.getStartAmount().replaceAll(",", ""):"");		
		request.getBody().put("BILL_MAX_AMT",  StringUtil.isNotEmpty(centerPlatformBean.getEndAmount())?centerPlatformBean.getEndAmount().replaceAll(",", ""):"");		
		request.getBody().put("APPROVE_BRANCH_NO", centerPlatformBean.getFinalApproveBranch());	//	审批机构号
		request.getBody().put("APPLY_START_DATE", StringUtil.isNotEmpty(centerPlatformBean.getApplyStartDate())?centerPlatformBean.getApplyStartDate().replaceAll("-",""):"" );		//	申请起始日
		request.getBody().put("APPLY_END_DATE", StringUtil.isNotEmpty(centerPlatformBean.getApplyEndDate())?centerPlatformBean.getApplyEndDate().replaceAll("-",""):"" );		//	申请结束日
		request.getBody().put("EFFECTIVE_START_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffStartDate())?centerPlatformBean.getEffStartDate().replaceAll("-",""):"" );	//	生效开始
		request.getBody().put("EFFECTIVE_END_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffEndDate())?centerPlatformBean.getEffEndDate().replaceAll("-",""):"" );	//	生效结束
		request.getBody().put("EFFECTIVE_START_EXPIRY_DATE", StringUtil.isNotEmpty(centerPlatformBean.getDueStartDate())?centerPlatformBean.getDueStartDate().replaceAll("-",""):"" );	//	到期开始	
		request.getBody().put("EFFECTIVE_END_EXPIRY_DATE", StringUtil.isNotEmpty(centerPlatformBean.getDueEndDate())?centerPlatformBean.getDueEndDate().replaceAll("-",""):"" );	//	到期结束
		
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220410", request);

		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 票据审价发送接口
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txBillPriceMaintain(TxReviewPriceInfo info,User user) throws Exception {
		List<IntroBillInfoBean> lists = info.getIntroBillInfoBeans();
		if(lists.size() == 0){
			logger.error("txBillPriceMaintain:！");
			throw new Exception("票据审价接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		BigDecimal reduceAmt = BigDecimal.ZERO;
		String opreType = "01";
		if(info.getReduceAmt() != null && info.getReduceAmt().compareTo(BigDecimal.ZERO) > 0){	//	调减
			opreType = "02";
			reduceAmt = info.getReduceAmt();
		}
		
		if("0".equals(info.getApplyState())){
			opreType = "02";
		}
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		for (IntroBillInfoBean list : lists) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("APPROVE_INFO_ARRAY.OPERATION_TYPE", opreType);	//	新增还是修改取决于是否存在有效数据
			map.put("APPROVE_INFO_ARRAY.RECORD_KEY", StringUtil.isNotEmpty(info.getTjId())?info.getTjId():"");
			map.put("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO", list.getBatchNo());		//	贴现定价审批编号
			map.put("APPROVE_INFO_ARRAY.DISCOUNT_TYPE", info.getTxType());				//	贴现类型
			map.put("APPROVE_INFO_ARRAY.AGREE_NO", info.getOnlineNo());					//	协议编号
			map.put("APPROVE_INFO_ARRAY.BILL_NO", list.getBillNo());					//	票据编号
			map.put("APPROVE_INFO_ARRAY.DISCOUNT_STATUS","SP_04".equals(info.getApplyState())?"1":info.getApplyState());		//	贴现状态
			map.put("APPROVE_INFO_ARRAY.BILL_AMT", list.getBillAmt());					//	票面金额
			
			BigDecimal currentAmt = BigDecimal.ZERO;
			if(info.getCurrentAmt() != null){
				currentAmt = info.getCurrentAmt();
			}else{
				currentAmt = list.getBillAmt();
			}
			map.put("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT", currentAmt);					//	当前金额
			map.put("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT", info.getReduceAmt()!=null?info.getReduceAmt():BigDecimal.ZERO);					//	当前金额
			
			map.put("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT", list.getBillAmt());					//	票面金额
			map.put("APPROVE_INFO_ARRAY.UN_DISCOUNT_AMT", list.getBillAmt());							//	未贴现金额
			map.put("APPROVE_INFO_ARRAY.DISCOUNTED_AMT", 0);			//	已贴现金额
			map.put("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT", currentAmt);			//	当前额度
			map.put("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT", reduceAmt);			//	调减额度
			map.put("APPROVE_INFO_ARRAY.BILL_TYPE", list.getBillType());				//	票据类型
			map.put("APPROVE_INFO_ARRAY.APPLY_DATE", StringUtil.isNotEmpty(info.getTxReviewPriceDetail().getApplyTxDate())?info.getTxReviewPriceDetail().getApplyTxDate().replaceAll("-", ""):"" );			//	申请日期
			map.put("APPROVE_INFO_ARRAY.BILL_EXPIRY_DATE", list.getDueDate().replaceAll("-", ""));			//	票据到期日
			map.put("APPROVE_INFO_ARRAY.EFFECTIVE_DATE", info.getTxReviewPriceDetail().getEffDate().replaceAll("-", ""));			//	生效日期
			map.put("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS", info.getTxReviewPriceDetail().getApplyValidDate());	//	有效天数
			map.put("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_NAME", list.getAcptBankName());	//	承兑行名称
			map.put("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_NO", list.getAcptBankNo());	//	承兑行行号
			map.put("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_TYPE", list.getAcptBankType());	//	承兑行类别
			map.put("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE", list.getApplyTxRate());//	申请贴现利率
			map.put("APPROVE_INFO_ARRAY.GUIDANCE_INT_RATE", list.getGuidanceRate());	//	指导利率
			map.put("APPROVE_INFO_ARRAY.DISCOUNT_INT_RATE", list.getFavorRate());		//	优惠利率
			map.put("APPROVE_INFO_ARRAY.MAX_DISCOUNT_INT_RATE", list.getBestFavorRate());//	最优惠利率
			map.put("APPROVE_INFO_ARRAY.MSG_SOURCE", list.getDataSource());				//	信息来源
			map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NO", info.getApproveBranchNo());	//	审批机构号
			map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NAME", info.getApproveBranchName());//	审批机构名称
			map.put("APPROVE_INFO_ARRAY.APPROVE_DATE", info.getApproveDate().replaceAll("-", "")); 			//	审批日期
			map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_TYPE", info.getApproveBranchType()); 			//	审批机构层级
			request.getDetails().add(map);
		}
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220409", request);
		return response;
	}
	
	/**
	 * 行别映射关系查询接口
	 * 
	 * 承兑行总行信息查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryBankRoleMapping(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("ACCEPTANCE_BANK_NAME", centerPlatformBean.getAcptHeadBankName());		//	承兑行总行行名
		request.getBody().put("ACCEPTANCE_BANK_NO", centerPlatformBean.getAcptHeadBankNo());			//	承兑行总行行号
		request.getBody().put("MAINTAIN_BANK_TYPE", centerPlatformBean.getMaintainType());			//	维护行别
		request.getBody().put("MAINTAIN_START_DATE", StringUtil.isNotEmpty(centerPlatformBean.getMaintainStartDate())?centerPlatformBean.getMaintainStartDate().replaceAll("-", ""):"");	//	开始
		request.getBody().put("MAINTAIN_END_DATE",StringUtil.isNotEmpty(centerPlatformBean.getMaintainEndDate())?centerPlatformBean.getMaintainEndDate().replaceAll("-", ""):"");		//	结束
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220408", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	
	/**
	 * 行别映射关系维护
	 * 
	 * 承兑行总行信息设置
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public boolean txBankRoleMappingSave(BankRoleMappingBean bankRoleMappingBean,User user)
			throws Exception {
		if (bankRoleMappingBean == null) {
			logger.error("txBankRoleMappingSave:！");
			throw new Exception("行别映射关系维护接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("ACCEPTANCE_BANK_NAME", bankRoleMappingBean.getAcptHeadBankName());	//	承兑行总行行号
		request.getBody().put("ACCEPTANCE_BANK_NO", bankRoleMappingBean.getAcptHeadBankNo());		//	承兑行总行行名
		request.getBody().put("TICKET_EXCHANGE_BANK_TYPE", bankRoleMappingBean.getPjsBankType());	//	票交所行别
		request.getBody().put("DEFAULT_BANK_TYPE", bankRoleMappingBean.getDefaultType());			//	默认行别
		request.getBody().put("MAINTAIN_BANK_TYPE", bankRoleMappingBean.getMaintainType());			//	维护行别
		request.getBody().put("REAL_BANK_TYPE", bankRoleMappingBean.getActualType());				//	真实行别
		request.getBody().put("MAINTAIN_DATE", DateUtils.toString(new Date(), "yyyyMMdd"));				//	维护日期
		request.getBody().put("MAINTAIN_BRANCH_NO", user.getDepartment().getInnerBankCode());			//	维护机构号
		request.getBody().put("MAINTAIN_BRANCH_NAME", user.getDepartment().getName());		//	维护日期
		request.getBody().put("MAINTAIN_CLIENT_NO", user.getLoginName());				//	维护人编号
		request.getBody().put("MAINTAIN_PERSON_NAME", user.getName());			//	维护人名称
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220407", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	/**
	 * 
	 * 秒贴协议列表查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryOnlineProtocol(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("AGREE_NO", centerPlatformBean.getOnlineNo());
		request.getBody().put("AGREE_STATUS", centerPlatformBean.getEffState());
//		request.getBody().put("CORE_CLIENT_NO", centerPlatformBean.getEffState());
		request.getBody().put("CLIENT_NAME", centerPlatformBean.getCustName());
//		request.getBody().put("CMS_CLIENT_NO", centerPlatformBean.getCustName());
		request.getBody().put("APPER_NAME", centerPlatformBean.getWorkerName());
		request.getBody().put("APP_BRANCH_NAME", centerPlatformBean.getSignBranchName());
		request.getBody().put("OPEN_START_DATE", centerPlatformBean.getOpenStartDate());
		request.getBody().put("OPEN_END_DATE", centerPlatformBean.getOpenEndDate());
		request.getBody().put("EXPIRY_START_DATE", centerPlatformBean.getDueStartDate());
		request.getBody().put("EXPIRY_END_DATE", centerPlatformBean.getDueEndDate());
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220406", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	
	/**
	 * 额度审价额度审价接口(CP20220405)
	 * */
	@SuppressWarnings("unchecked")
	public ReturnMessageNew txAmtReciewPriceMaintain1(TxReviewPriceInfo info,User user) throws Exception {
		List<IntroBillInfoBean> lists = info.getIntroBillInfoBeans();
		System.out.println(info.getTxReviewPriceDetail());
		System.out.println(lists.size());
		if(info.getTxReviewPriceDetail() == null || lists.size() == 0){
			logger.error("txRateMaintain:！");
			throw new Exception("票据额度审价接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		//	封装基础数据
		TxReviewPriceDetail detail = info.getTxReviewPriceDetail();
		
		
		String opreType = "01";
		BigDecimal approveAmt = BigDecimal.ZERO;
		BigDecimal currentAmt = BigDecimal.ZERO;
		BigDecimal usedAmt = BigDecimal.ZERO;
		BigDecimal aviableAmt = BigDecimal.ZERO;
		BigDecimal reduceAmt = BigDecimal.ZERO;
		
		if("0".equals(info.getApplyState()) || (info.getReduceAmt() != null && info.getReduceAmt().compareTo(BigDecimal.ZERO) > 0)){
			opreType = "02";
		}
		
		if("02".equals(opreType)){	//	调减
			approveAmt = info.getApproveAmt();
			currentAmt = info.getCurrentAmt();
			usedAmt = info.getUsedAmt()!=null?info.getUsedAmt():BigDecimal.ZERO;
			aviableAmt = info.getAvailableAmt();
			reduceAmt = info.getReduceAmt()!=null?info.getReduceAmt():BigDecimal.ZERO;
		}else{
//			approveAmt = detail.getApplyAmountSum();
			currentAmt = detail.getApplyAmountSum();
			aviableAmt = approveAmt.subtract(usedAmt);
		}
		
		//	额度审价行别信息数组
		String term = "";
		List list1 = new ArrayList();
		for (IntroBillInfoBean list : lists) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("APPROVE_INFO_ARRAY.OPERATION_TYPE",opreType );		//	新增还是修改取决于是否存在有效数据  01-新增   02-修改 03-失败
			map.put("APPROVE_INFO_ARRAY.LIMIT_APPROVE_NO", StringUtil.isNotEmpty(info.getTjId())?info.getTjId():"" );					//	额度审价id
			map.put("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO", detail.getBatchNo());			//	贴现定价审批编号
			map.put("APPROVE_INFO_ARRAY.LIMIT_STATUS","SP_04".equals(info.getApplyState())?"1":info.getApplyState());					//	额度状态		0-失效 1-生效 2-待生效
			map.put("APPROVE_INFO_ARRAY.LIMIT_APPROVE_TYPE","04".equals(info.getTxType())?"01":"02");			//	额度审价类型
			map.put("APPROVE_INFO_ARRAY.AGREE_NO", detail.getOnlineNo());						//	协议编号
			map.put("APPROVE_INFO_ARRAY.CORE_CLIENT_NO", detail.getCustNo());				//	核心客户号
			map.put("APPROVE_INFO_ARRAY.CLIENT_NAME", detail.getCustName());					//	客户名称
			map.put("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE", list.getApplyTxRate());	//	申请贴现利率  待定
			map.put("APPROVE_INFO_ARRAY.APPLY_DATE", StringUtil.isNotEmpty(detail.getApplyTxDate())?detail.getApplyTxDate().replaceAll("-", ""):"" );	//	申请贴现时间
			map.put("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT", "01".equals(opreType)?list.getApplyAmt(): approveAmt);	//	审批额度
			map.put("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT", "01".equals(opreType)?list.getApplyAmt(): currentAmt);			//	当前额度
			map.put("APPROVE_INFO_ARRAY.USED_LIMIT_AMT", usedAmt);			//	已用额度
			map.put("APPROVE_INFO_ARRAY.AVAIL_LIMIT_AMT", "01".equals(opreType)?list.getApplyAmt(): aviableAmt);				//	可用额度
			map.put("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT", reduceAmt);	//	调减额度
			map.put("APPROVE_INFO_ARRAY.EFFECTIVE_DATE", StringUtil.isNotEmpty(detail.getEffDate())?detail.getEffDate().replaceAll("-", ""):"");				//	生效日期
			map.put("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS", detail.getApplyValidDate());				//	有效天数
			map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NO", info.getApproveBranchNo());		//	审批机构号
			map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NAME", info.getApproveBranchName());	//	审批机构名称
			map.put("APPROVE_INFO_ARRAY.APPROVE_DATE", StringUtil.isNotEmpty(info.getApproveDate())?info.getApproveDate().replaceAll("-", ""):""); 				//	审批日期
			map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_TYPE", info.getApproveBranchType()); 	//	审批机构层级
			term = "||" + list.getTxTerm().replaceAll(",", "||");
			map.put("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_EXPIRY_MONTHS", term); 	//	承兑到期月数
			map.put("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_TYPE", list.getAcptBankType()); 		//	承兑行行别
			list1.add(map);
		}
		request.setDetails(list1);
		
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP2022040501", request);
		return response;
	}
	
	/**
	 * 额度审价额度二审价接口(CP20220405)
	 * */
	@SuppressWarnings("unchecked")
	public ReturnMessageNew txAmtReciewPriceMaintain2(TxReviewPriceInfo info,User user) throws Exception {
		List<IntroBillInfoBean> lists = info.getIntroBillInfoBeans();
		System.out.println(info.getTxReviewPriceDetail());
		System.out.println(lists.size());
		if(info.getTxReviewPriceDetail() == null || lists.size() == 0){
			logger.error("txRateMaintain:！");
			throw new Exception("票据额度审价接口传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		//	封装基础数据
		TxReviewPriceDetail detail = info.getTxReviewPriceDetail();
		
		BigDecimal approveAmt = BigDecimal.ZERO;
		BigDecimal currentAmt = BigDecimal.ZERO;
		BigDecimal usedAmt = BigDecimal.ZERO;
		BigDecimal aviableAmt = BigDecimal.ZERO;
		BigDecimal reduceAmt = BigDecimal.ZERO;
		BigDecimal applyTxRate = BigDecimal.ZERO;
		String opreType = "01";
		
		if("0".equals(info.getApplyState()) || (info.getReduceAmt() != null && info.getReduceAmt().compareTo(BigDecimal.ZERO) > 0)){
			opreType = "02";
		}
		
		if("02".equals(opreType)){	//	调减
			approveAmt = info.getApproveAmt();
			currentAmt = info.getCurrentAmt();
			usedAmt = info.getUsedAmt()!=null?info.getUsedAmt():BigDecimal.ZERO;
			aviableAmt = info.getAvailableAmt();
			reduceAmt = info.getReduceAmt()!=null?info.getReduceAmt():BigDecimal.ZERO;
		
			applyTxRate = detail.getApplyTxRate();
		}else{
			approveAmt = detail.getApplyAmountSum();
			currentAmt = detail.getApplyAmountSum();
			aviableAmt = approveAmt.subtract(usedAmt);
			applyTxRate = detail.getApplyTxRate();
		}
		
		//	额度审价行别信息数组
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("APPROVE_INFO_ARRAY.OPERATION_TYPE",opreType);		//	新增还是修改取决于是否存在有效数据  01-新增   02-修改 03-失败
		map.put("APPROVE_INFO_ARRAY.LIMIT_APPROVE_NO", StringUtil.isNotEmpty(info.getTjId())?info.getTjId():"" );					//	额度审价id
		map.put("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO", detail.getBatchNo());			//	贴现定价审批编号
		map.put("APPROVE_INFO_ARRAY.LIMIT_STATUS", "SP_04".equals(info.getApplyState())?"1":info.getApplyState());					//	额度状态		0-失效 1-生效 2-待生效
		map.put("APPROVE_INFO_ARRAY.LIMIT_APPROVE_TYPE","04".equals(info.getTxType())?"01":"02");			//	额度审价类型
		map.put("APPROVE_INFO_ARRAY.AGREE_NO", detail.getOnlineNo());						//	协议编号
		map.put("APPROVE_INFO_ARRAY.CORE_CLIENT_NO", detail.getCustNo());				//	核心客户号
		map.put("APPROVE_INFO_ARRAY.CLIENT_NAME", detail.getCustName());					//	客户名称
		map.put("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE", applyTxRate);	//	申请贴现利率  待定
		map.put("APPROVE_INFO_ARRAY.APPLY_DATE",StringUtil.isNotEmpty(detail.getApplyTxDate())?detail.getApplyTxDate().replaceAll("-", ""):"" );	//	申请贴现时间
		map.put("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT", approveAmt);	//	审批额度
		map.put("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT", currentAmt);			//	当前额度
		map.put("APPROVE_INFO_ARRAY.USED_LIMIT_AMT", usedAmt);			//	已用额度
		map.put("APPROVE_INFO_ARRAY.AVAIL_LIMIT_AMT", aviableAmt);				//	可用额度
		map.put("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT", reduceAmt);	//	调减额度
		map.put("APPROVE_INFO_ARRAY.EFFECTIVE_DATE", StringUtil.isNotEmpty(detail.getEffDate())?detail.getEffDate().replaceAll("-", ""):"");				//	生效日期
		map.put("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS", detail.getApplyValidDate());				//	有效天数
		map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NO", info.getApproveBranchNo());		//	审批机构号
		map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NAME", info.getApproveBranchName());	//	审批机构名称
		map.put("APPROVE_INFO_ARRAY.APPROVE_DATE", StringUtil.isNotEmpty(info.getApproveDate())?info.getApproveDate().replaceAll("-", ""):""); 				//	审批日期
		map.put("APPROVE_INFO_ARRAY.APPROVE_BRANCH_TYPE", info.getApproveBranchType()); 	//	审批机构层级
		request.getDetails().add(map);
		
		List list1 = new ArrayList();
		for (IntroBillInfoBean list : lists) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			String term = "||" + list.getTxTerm().replaceAll(",", "||")+"||";
			map1.put("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_EXPIRY_MONTHS", term); 	//	承兑到期月数
			map1.put("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_TYPE", list.getAcptBankType()); 		//	承兑行行别
			list1.add(map1);
		}
		request.setDetails(list1);
		
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP2022040502", request);
		return response;
	}
	
	/**
	 * 额度审价信息查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew AmtBillPriceQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		request.getBody().put("AGREE_NO", centerPlatformBean.getOnlineNo());
		request.getBody().put("DISCOUNT_APPROVE_NO", centerPlatformBean.getBatchNo());
		request.getBody().put("LIMIT_APPROVE_TYPE", centerPlatformBean.getTxType());
		request.getBody().put("LIMIT_STATUS", centerPlatformBean.getEffState());
		request.getBody().put("ACCEPTANCE_BANK_TYPE", centerPlatformBean.getAcptBankType());
		
		request.getBody().put("APPROVE_BRANCH_NO", centerPlatformBean.getFinalApproveBranch());	//	审批机构号
		request.getBody().put("APPLY_START_DATE", StringUtil.isNotEmpty(centerPlatformBean.getApplyStartDate())?centerPlatformBean.getApplyStartDate().replaceAll("-",""):"" );		//	申请起始日
		request.getBody().put("APPLY_END_DATE", StringUtil.isNotEmpty(centerPlatformBean.getApplyEndDate())?centerPlatformBean.getApplyEndDate().replaceAll("-",""):"" );		//	申请结束日
		request.getBody().put("EFFECTIVE_START_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffStartDate())?centerPlatformBean.getEffStartDate().replaceAll("-",""):"" );	//	生效开始
		request.getBody().put("EFFECTIVE_END_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffEndDate())?centerPlatformBean.getEffEndDate().replaceAll("-",""):"" );	//	生效结束
		request.getBody().put("EFFECTIVE_START_EXPIRY_DATE", StringUtil.isNotEmpty(centerPlatformBean.getDueStartDate())?centerPlatformBean.getDueStartDate().replaceAll("-",""):"" );	//	到期开始	
		request.getBody().put("EFFECTIVE_END_EXPIRY_DATE", StringUtil.isNotEmpty(centerPlatformBean.getDueEndDate())?centerPlatformBean.getDueEndDate().replaceAll("-",""):"" );	//	到期结束
		
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
//		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220404", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	
	/**
	 * 协议历史变更列表查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew OnLineTxAgreeChangeQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("CORE_CLIENT_NO", centerPlatformBean.getCustNo());			//	客户号
		request.getBody().put("AGREE_NO", centerPlatformBean.getOnlineNo());				//	协议编号
		request.getBody().put("CLIENT_NAME", centerPlatformBean.getCustName());				//	客户名称
		request.getBody().put("CHANGE_START_DATE",StringUtil.isNotEmpty(centerPlatformBean.getUpdateStartDate())?  centerPlatformBean.getUpdateStartDate().replaceAll("-", ""):"");//	变更日期开始
		request.getBody().put("CHANGE_END_DATE",StringUtil.isNotEmpty(centerPlatformBean.getUpdateEndDate())?  centerPlatformBean.getUpdateEndDate().replaceAll("-", ""):"");	//	变更日期结束
		
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220403", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	

	/**
	 * 协议历史变更列表详情查询
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew OnLineChangeDetailQuery(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("RECORD_NO", centerPlatformBean.getId());				
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220402", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 * 贴现最优惠利率查询接口(CP20220401)
	 * */
	/*@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew txQueryBestRate(CenterPlatformBean centerPlatformBean,User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
//		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
//		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum());
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("EFFECTIVE_DATE", StringUtil.isNotEmpty(centerPlatformBean.getEffTime())?centerPlatformBean.getEffTime().replaceAll("-", ""):"");
		request.getBody().put("MAX_DISCOUNT_RATE_STATUS",StringUtil.getStringVal(centerPlatformBean.getEffState()));
		request.getBody().put("BATCH_NO",StringUtil.getStringVal("01".equals(centerPlatformBean.getOpreaType())?"1":"0"));
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220401", request);

		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}*/
	
	@Override
	public String getEntityName() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew onlineTxBusinessQuery(CenterPlatformBean centerPlatformBean, User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		request.getBody().put("CORE_CLIENT_NO", centerPlatformBean.getCustNo());					//	核心客户号
		request.getBody().put("DISCOUNT_APPLYER_NAME", centerPlatformBean.getCustName());			//	贴现申请人名称
		request.getBody().put("AGREE_NO", centerPlatformBean.getOnlineNo());						//	协议编号
		request.getBody().put("DISCOUNT_MAX_AMT", StringUtil.isNotEmpty(centerPlatformBean.getEndAmount())?centerPlatformBean.getEndAmount().replaceAll(",", ""):"");				//	贴现申请金额最大值			
		request.getBody().put("DISCOUNT_MIN_AMT", StringUtil.isNotEmpty(centerPlatformBean.getStartAmount())?centerPlatformBean.getStartAmount().replaceAll(",", ""):"");				//	贴现申请金额最小值
		request.getBody().put("DISCOUNT_CONTRACT_NO", centerPlatformBean.getBusinessNo());			//	贴现业务合同号
//		request.getBody().put("ONLINE_DISCOUNT_APPLY_NO", centerPlatformBean.getBusi_id());		//	在线贴现申请ID
//		request.getBody().put("LOANER_ACCT_NAME", centerPlatformBean.getCustName());				//	放款账户名称
		request.getBody().put("DISCOUNT_APPLY_START_DATE",StringUtil.isNotEmpty( centerPlatformBean.getApplyStartDate())? centerPlatformBean.getApplyStartDate().replaceAll("-", ""):"");//	贴现申请日期起始
		request.getBody().put("DISCOUNT_APPLY_END_DATE", StringUtil.isNotEmpty( centerPlatformBean.getApplyEndDate())? centerPlatformBean.getApplyEndDate().replaceAll("-", ""):"");	//	贴现申请日期截止
//		request.getBody().put("DISCOUNT_STATUS", centerPlatformBean.getUpdateEndDate());			//	贴现状态
//		request.getBody().put("DISCOUNT_APPLY_TYPE", centerPlatformBean.getUpdateEndDate());		//	贴现申请类型
//		request.getBody().put("DISCOUNT_CONTRACT_STATUS", centerPlatformBean.getUpdateEndDate());	//	贴现合同状态
		
		request.getAppHead().put("PER_PAGE_NUM", centerPlatformBean.getPageSize());
		System.out.println(centerPlatformBean.getPageSize());
		request.getAppHead().put("QUERY_KEY", centerPlatformBean.getPageNum()*10 - 9);
		System.out.println(centerPlatformBean.getPageNum());
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220429", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}


	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew onlineTxBusinessDetail(CenterPlatformBean centerPlatformBean, User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		
		
		request.getBody().put("ONLINE_DISCOUNT_APPLY_NO", centerPlatformBean.getId());	//	在线贴现申请ID			
//		request.getBody().put("DISCOUNT_STATUS", centerPlatformBean.getTxState());		//	贴现状态
		request.getBody().put("BILL_NO", centerPlatformBean.getBillNo());		//	贴现状态
		request.getBody().put("START_BILL_NO", centerPlatformBean.getChildBillNoBegin());		
		request.getBody().put("END_BILL_NO", centerPlatformBean.getChildBillNoEnd());		
		request.getBody().put("BILL_MIN_AMT", StringUtil.isNotEmpty(centerPlatformBean.getStartAmount())?centerPlatformBean.getStartAmount().replaceAll(",", ""):"");		
		request.getBody().put("BILL_MAX_AMT",  StringUtil.isNotEmpty(centerPlatformBean.getEndAmount())?centerPlatformBean.getEndAmount().replaceAll(",", ""):"");		
		request.getBody().put("MIN_DRAW_BILL_DATE",StringUtil.isNotEmpty( centerPlatformBean.getApplyStartDate())? centerPlatformBean.getApplyStartDate().replaceAll("-", ""):"");		
		request.getBody().put("MAX_DRAW_BILL_DATE",StringUtil.isNotEmpty( centerPlatformBean.getApplyEndDate())? centerPlatformBean.getApplyEndDate().replaceAll("-", ""):"");		
		request.getBody().put("MIN_REMIT_EXPIRY_DATE",StringUtil.isNotEmpty( centerPlatformBean.getDueStartDate())? centerPlatformBean.getDueStartDate().replaceAll("-", ""):"");		
		request.getBody().put("MAX_REMIT_EXPIRY_DATE",StringUtil.isNotEmpty( centerPlatformBean.getDueEndDate())? centerPlatformBean.getDueEndDate().replaceAll("-", ""):"");		
		request.getBody().put("ACCEPTOR_OPEN_BANK_NO", centerPlatformBean.getAcceptBankNo());	
		request.getBody().put("ACCEPTOR_OPEN_BANK_NAME", centerPlatformBean.getAcceptBankName());	
		request.getBody().put("DISCOUNT_BILL_STATUS", centerPlatformBean.getTxState());	
		
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220430", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ReturnMessageNew ActualRateInfos(String protocolNo, User user) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("AGREE_NO", protocolNo);				
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220431", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}


	@SuppressWarnings("unchecked")
	@Override
	public void closeBusiness(User user, CenterPlatformBean centerPlatformBean) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getSysHead().put("BRANCH_ID", user.getDepartment().getInnerBankCode());
		request.getSysHead().put("USER_ID", user.getLoginName());
		request.getBody().put("ERROR_MSG_KEY", centerPlatformBean.getId());				
		request.getBody().put("PRO_TYPE", centerPlatformBean.getOpreaType());				
		
		ReturnMessageNew response = centerPlatformClient.processECDS("CP20220432", request);
		String responseCode = response.getRet().getRET_CODE();
		if (!responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			throw new Exception(response.getRet().getRET_MSG());
		}
	}
}
