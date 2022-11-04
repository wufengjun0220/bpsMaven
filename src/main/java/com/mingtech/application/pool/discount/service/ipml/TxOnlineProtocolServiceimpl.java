package com.mingtech.application.pool.discount.service.ipml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.IntroBillInfoBean;
import com.mingtech.application.pool.discount.domain.TxBusinessDetail;
import com.mingtech.application.pool.discount.domain.TxBusinessInfo;
import com.mingtech.application.pool.discount.domain.TxContactDetailBean;
import com.mingtech.application.pool.discount.domain.TxExDetailBean;
import com.mingtech.application.pool.discount.domain.TxProtocolDetailBean;
import com.mingtech.application.pool.discount.domain.TxRateAdjustInfo;
import com.mingtech.application.pool.discount.domain.TxRateDetailBeanPO;
import com.mingtech.application.pool.discount.domain.TxReduceInfoBean;
import com.mingtech.application.pool.discount.domain.TxReviewPriceDetail;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxOnlineProtocolService;
import com.mingtech.application.pool.discount.service.TxRateMaintainInfoService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

public class TxOnlineProtocolServiceimpl extends GenericServiceImpl implements TxOnlineProtocolService{
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TxRateMaintainInfoService txRateMaintainInfoService;

	/**
	 * 秒贴协议详情查询
	 * */	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> txQueryOnlineProtocol(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception{
		centerPlatformBean.setPageSize(page.getPageSize());
		centerPlatformBean.setPageNum(page.getPageIndex());
		
		ReturnMessageNew resp = centerPlatformSysService.txQueryOnlineProtocol(centerPlatformBean,user);
		List<Map> lists = resp.getDetails();
		
		List<TxProtocolDetailBean> returnList = new ArrayList<TxProtocolDetailBean>();
		
		/*** 调试代码 start **************************/
		/*TxProtocolDetailBean bean = new TxProtocolDetailBean("001", "01", "1", "策划师",new BigDecimal(10000), new BigDecimal(10), new BigDecimal(10), "30610004", "测试行", "经办1", "010101", "经办机构", "2022-03-25", "2022-09-12", "2022-08-12", "1", "1233456798756", "12555", "01", "020202", "入场机构", "141414", "入账名", "12", "125", "01",null,null);
		
		TxExDetailBean txExDetailBean = new TxExDetailBean("0101", "0101", new BigDecimal(10000), new BigDecimal(10), new BigDecimal(10), "01");
		List<TxExDetailBean> txExDetailBeans = new ArrayList<TxExDetailBean>();
		txExDetailBeans.add(txExDetailBean);
		bean.setExDetailBeans(txExDetailBeans);
		
		TxContactDetailBean txContactDetailBean = new TxContactDetailBean("测试二", "测试二", "02", "01");
		List<TxContactDetailBean> txContactDetailBeans = new ArrayList<TxContactDetailBean>();
		txContactDetailBeans.add(txContactDetailBean);
		bean.setTxContactDetailBeans(txContactDetailBeans);
		
		List<TxProtocolDetailBean> returnList = new ArrayList<TxProtocolDetailBean>();
		returnList.add(bean);*/
		
		/** * 调试代码 end ******************************     */
		
		if(lists.size() > 0){
			for (Map mapss : lists) {
				if(mapss != null){
					List<Map> beanInfo = (List<Map>) mapss.get("SIGN_INFO_ARRAY");
					TxProtocolDetailBean bean = new TxProtocolDetailBean();
					if(beanInfo != null && beanInfo.size() > 0){
						for (Map maps : beanInfo) {
							if(maps != null){
								bean.setId(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.AGREE_ID")));				//	秒贴协议ID
								bean.setProtocolNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.AGREE_NO")));				//	协议编号
								bean.setProtocolType(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.AGREE_TYPE")));			//	协议类型
								bean.setProtocolStatus(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.AGREE_STATUS")));			//	协议状态
								bean.setCreditLineNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BASIC_CREDIT_LIMIT_NO")));	//	基本授信额度编号
								bean.setCustNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.CORE_CLIENT_NO")));			//	信贷客户编号
								bean.setMisCustNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.MIS_CLIENT_NO")));		//	核心客户号
//								bean.setCustName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.CMS_CLIENT_NO_LIST")));	//	网银客户集合
								bean.setCustName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.CLIENT_NAME")));			//	客户名称
								
								bean.setLimitAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.ONLINE_DISCOUNT_TOTAL_AMT")))?
										StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.ONLINE_DISCOUNT_TOTAL_AMT")):"0"));	//	在线贴现总额
								
								bean.setUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.USED_LIMIT_AMT")))?
										StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.USED_LIMIT_AMT")):"0"));			//	已用额度
								
								bean.setAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.UN_USED_LIMIT_AMT")))?
										StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.UN_USED_LIMIT_AMT")):"0"));			//	未用额度
								bean.setProtocolDueDate(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.EXPIRY_DATE")));				//	到期日期
								bean.setIsEx(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_FLAG")));			//	是否启用前手管理
//								bean.setAccountEntryDevNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.LOANER_ACCT_NO")));			//	放款账号
//								bean.setAccountEntryDevName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.LOANER_ACCT_NAME")));			//	放款账户名称
								bean.setAccountEntryDevNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.ENTER_ACCT_BRANCH_NO")));		//	入账机构号
								bean.setAccountEntryDevName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.ENTER_ACCT_BRANCH_NAME")));	//	入账机构名称

								bean.setEntryBankNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.LOANER_ACCT_NO")));		//	入账账号
								bean.setEntryBankName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.LOANER_ACCT_NAME")));		//	入账账号名称
		
								bean.setAccountEntryNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.DISCOUNT_IN_BANK_NO")));		//	贴入行行号
								bean.setAccountEntryName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.DISCOUNT_IN_BANK_NAME")));		//	贴入行名称
								
								bean.setRateFloatType(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.RATE_ADJUST_WAY")));			//	利率调整方式
								bean.setRateFloatNum(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.FLOAT_INT_RATE_VALUE")));		//	利率浮动值
								bean.setContractOrgNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.APP_BRANCH_NO")));				//	经办机构号
								bean.setContractOrgName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.APP_BRANCH_NAME")));			//	经办机构名称
								bean.setHandlerNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.APPER_ID")));					//	经办人编号
								bean.setHandler(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.APPER_NAME")));				//	经办人名称
								
								//	根据员工编号查询号码
								if(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.APPER_NAME")))){
									User use = userService.getUserByLoginName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.APPER_ID")));
									if(user != null){
										bean.setTelphone(use.getTelPhone());
									}
								}
								
								bean.setProtocolOpenDate(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.OPEN_DATE")));					//	开通日期
								bean.setProtocolChangeDate(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.CHANGE_DATE")));				//	变更日期
							}
						}
					}
					
					List<TxExDetailBean> txExDetailBeans = new ArrayList<TxExDetailBean>();
					//	前手信息组
					List<Map> exDetails = (List<Map>) mapss.get("BEFORE_HAND_INFO_ARRAY");
					
					if(exDetails != null && exDetails.size() > 0){
						for (Map maps : exDetails) {
							if(maps != null){
								TxExDetailBean txExDetailBean = new TxExDetailBean(); 
								txExDetailBean.setId(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_KEY")));
								txExDetailBean.setExCustNo(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_ID")));
								txExDetailBean.setExCustName(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_NAME")));
								
								txExDetailBean.setExTotalAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")))?
										StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")):"0"));
								
								txExDetailBean.setExUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")))?
										StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")):"0"));
								
								txExDetailBean.setExAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")))?
										StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")):"0"));
								txExDetailBean.setExStatus(StringUtil.getStringVal(maps.get("SIGN_INFO_ARRAY.BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_STATUS")));
								txExDetailBeans.add(txExDetailBean);
							}
						}
						bean.setExDetailBeans(txExDetailBeans);
					}
					
					//	联系人信息组
					List<Map> conDetails = (List<Map>) mapss.get("AGREE_ARRAY");
					List<TxContactDetailBean> txContactDetailBeans = new ArrayList<TxContactDetailBean>();
					if(conDetails != null && conDetails.size() > 0){
						for (Map map : conDetails) {
							if(map != null){
								TxContactDetailBean txContactDetailBean = new TxContactDetailBean();
								if("40".equals(StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.AGREE_ARRAY.CLIENT_TYPE")))){
									txContactDetailBean.setId(StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.AGREE_ARRAY.SIGN_KEY")));
									txContactDetailBean.setContactsName(StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.AGREE_ARRAY.CLIENT_NAME")));
									txContactDetailBean.setContactsTel(StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.AGREE_ARRAY.MOBILE")));
									txContactDetailBean.setContactsType(StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.AGREE_ARRAY.CLIENT_TYPE")));
									txContactDetailBean.setContactsNo(StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.AGREE_ARRAY.USER_NO")));
									txContactDetailBeans.add(txContactDetailBean);
								}
							}
						}
						bean.setTxContactDetailBeans(txContactDetailBeans);
					}
					//	利率调整组
					List<Map> rates = (List<Map>) mapss.get("INT_RATE_ARRAY");

					List<TxRateDetailBeanPO> txRateDetailBeanPOs = new ArrayList<TxRateDetailBeanPO>();
					for (int i = 1; i < 13; i++) {
						TxRateDetailBeanPO txRateDetailBeanPO = new TxRateDetailBeanPO();
						txRateDetailBeanPO.setTerm(""+i);
						txRateDetailBeanPOs.add(txRateDetailBeanPO);
					}
					System.out.println(rates.size());
					
					if(rates != null && rates.size() > 0){
						for (Map map : rates) {
							if(map != null){
								String term = StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.INT_RATE_ARRAY.REMAIN_EXPIRY_MONTHS"));
								String rate = StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.INT_RATE_ARRAY.INT_RATE"));
								String bankType = StringUtil.getStringVal(map.get("SIGN_INFO_ARRAY.INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE"));
							
								System.out.println( term + "  " + rate + "  " + bankType);
								
								for(int i = 0;i < txRateDetailBeanPOs.size();i++){
									if(term.equals(txRateDetailBeanPOs.get(i).getTerm())){
										if(StringUtil.isNotEmpty(rate)){
											if("00".equals(bankType)){
												txRateDetailBeanPOs.get(i).setOwnBank(new BigDecimal(rate));
											}
											if("01".equals(bankType)){
												txRateDetailBeanPOs.get(i).setStateShares(new BigDecimal(rate));
											}
											if("02".equals(bankType)){
												txRateDetailBeanPOs.get(i).setSharesSys(new BigDecimal(rate));
											}
											if("03".equals(bankType)){
												txRateDetailBeanPOs.get(i).setCityBank(new BigDecimal(rate));
											}
											if("04".equals(bankType)){
												txRateDetailBeanPOs.get(i).setAgriCommBank(new BigDecimal(rate));
											}
											if("05".equals(bankType)){
												txRateDetailBeanPOs.get(i).setType6(new BigDecimal(rate));
											}
											if("06".equals(bankType)){
												txRateDetailBeanPOs.get(i).setType7(new BigDecimal(rate));
											}
											if("07".equals(bankType)){
												txRateDetailBeanPOs.get(i).setType8(new BigDecimal(rate));
											}
											if("08".equals(bankType)){
												txRateDetailBeanPOs.get(i).setType9(new BigDecimal(rate));
											}
											if("09".equals(bankType)){
												txRateDetailBeanPOs.get(i).setType10(new BigDecimal(rate));
											}
										}
									}
								}
							}
						}
						bean.setTxRateDetailBeanPOs(txRateDetailBeanPOs);
					}
					
					returnList.add(bean);
				}
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rows", returnList);
		map.put("results",StringUtil.isNotEmpty(StringUtil.getStringVal(resp.getAppHead().get("TOTAL_ROWS")))?resp.getAppHead().get("TOTAL_ROWS"):0);
		System.out.println(resp.getAppHead().get("TOTAL_ROWS"));
		return map;
	}
	
	/**
	 * 秒贴协议历史变更列表查询
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> OnLineTxAgreeChangeQuery(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception{
		centerPlatformBean.setPageSize(page.getPageSize());
		centerPlatformBean.setPageNum(page.getPageIndex());
		
		ReturnMessageNew resp = centerPlatformSysService.OnLineTxAgreeChangeQuery(centerPlatformBean,user);
		List<Map> lists = resp.getDetails();
		
		List<TxProtocolDetailBean> returnList = new ArrayList<TxProtocolDetailBean>();
		//	返回前台数据处理
		if(lists != null && lists.size() > 0){
			for (Map maps : lists) {
				TxProtocolDetailBean bean = new TxProtocolDetailBean();
				bean.setId(StringUtil.getStringVal(maps.get("AGREE_ARRAY.RECORD_NO")));
				bean.setProtocolNo(StringUtil.getStringVal(maps.get("AGREE_ARRAY.AGREE_NO")));
				bean.setCustName(StringUtil.getStringVal(maps.get("AGREE_ARRAY.CLIENT_NAME")));
				bean.setProtocolType(StringUtil.getStringVal(maps.get("AGREE_ARRAY.AGREE_TYPE")));
				bean.setUpdateCotent(StringUtil.getStringVal(maps.get("AGREE_ARRAY.MODIFY_DESC")));
				bean.setHandler(StringUtil.getStringVal(maps.get("AGREE_ARRAY.APPER_NAME")));
				bean.setHandlerNo(StringUtil.getStringVal(maps.get("AGREE_ARRAY.APPER_ID")));
				bean.setSignBranchName(StringUtil.getStringVal(maps.get("AGREE_ARRAY.APP_BRANCH_NAME")));
				bean.setUpdateDate(StringUtil.getStringVal(maps.get("AGREE_ARRAY.CHANGE_DATE")));
				returnList.add(bean);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rows", returnList);
		map.put("results",StringUtil.isNotEmpty(StringUtil.getStringVal(resp.getAppHead().get("TOTAL_ROWS")))?resp.getAppHead().get("TOTAL_ROWS"):0);
		System.out.println(resp.getAppHead().get("TOTAL_ROWS"));
		return map;
	}
	
	/**
	 * 秒贴协议历史变更详情查询
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> TxAgreeChangeDeailsQuery(CenterPlatformBean centerPlatformBean, User user) throws Exception{
		Map reMap = new HashMap();
		
		ReturnMessageNew resp = centerPlatformSysService.OnLineChangeDetailQuery(centerPlatformBean,user);
		Map maps = resp.getBody();
		
		TxProtocolDetailBean bean = new TxProtocolDetailBean();		//	当前信息
		TxProtocolDetailBean upbean = new TxProtocolDetailBean();	//	变更信息
		bean.setCreditLineNo(StringUtil.getStringVal(maps.get("BASIC_CREDIT_LIMIT_NO")));	//	基本授信额度编号
		bean.setProtocolStatus(StringUtil.getStringVal(maps.get("AGREE_STATUS")));	//	状态
		bean.setCustNo(StringUtil.getStringVal(maps.get("CORE_CLIENT_NO")));		//	核心客户号
		bean.setCustName(StringUtil.getStringVal(maps.get("CLIENT_NAME")));			//	客户名称
		bean.setLimitAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("ONLINE_DISCOUNT_TOTAL_AMT")))?StringUtil.getStringVal(maps.get("ONLINE_DISCOUNT_TOTAL_AMT")):"0"));	//	在线贴现总额
		bean.setUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("USED_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("USED_LIMIT_AMT")):"0"));			//	已用额度
		bean.setAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("UN_USED_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("UN_USED_LIMIT_AMT")):"0"));			//	未用额度
		bean.setProtocolDueDate(StringUtil.getStringVal(maps.get("EXPIRY_DATE")));				//	到期日期
		bean.setIsEx(StringUtil.getStringVal(maps.get("BEFORE_HAND_FLAG")));			//	是否启用前手管理
		bean.setAccountEntryNo(StringUtil.getStringVal(maps.get("DISCOUNT_IN_BANK_NO")));			//	放款账号
		bean.setAccountEntryName(StringUtil.getStringVal(maps.get("DISCOUNT_IN_BANK_NAME")));			//	放款账户名称
		bean.setAccountEntryDevNo(StringUtil.getStringVal(maps.get("ENTER_ACCT_BRANCH_NO")));		//	入账机构号
		bean.setAccountEntryDevName(StringUtil.getStringVal(maps.get("ENTER_ACCT_BRANCH_NAME")));	//	入账机构名称
		
		bean.setEntryBankNo(StringUtil.getStringVal(maps.get("LOANER_ACCT_NO")));		//	贴入行行号
		bean.setEntryBankName(StringUtil.getStringVal(maps.get("LOANER_ACCT_NAME")));		//	贴入行名称
		bean.setRateFloatType(StringUtil.getStringVal(maps.get("RATE_ADJUST_WAY")));			//	利率调整方式
		bean.setRateFloatNum(StringUtil.getStringVal(maps.get("FLOAT_INT_RATE_VALUE")));		//	利率浮动值
		bean.setContractOrgNo(StringUtil.getStringVal(maps.get("APP_BRANCH_NO")));				//	经办机构号
		bean.setContractOrgName(StringUtil.getStringVal(maps.get("APP_BRANCH_NAME")));			//	经办机构名称
		bean.setHandlerNo(StringUtil.getStringVal(maps.get("APPER_ID")));					//	经办人编号
		bean.setHandler(StringUtil.getStringVal(maps.get("APPER_NAME")));				//	经办人名称
		bean.setProtocolOpenDate(StringUtil.getStringVal(maps.get("OPEN_DATE")));					//	开通日期
		bean.setProtocolChangeDate(StringUtil.getStringVal(maps.get("CHANGE_DATE")));				//	变更日期

		upbean.setCreditLineNo(StringUtil.getStringVal(maps.get("HIS_BASIC_CREDIT_LIMIT_NO")));	//	基本授信额度编号
		upbean.setProtocolStatus(StringUtil.getStringVal(maps.get("HIS_AGREE_STATUS")));	//	状态
		upbean.setCustNo(StringUtil.getStringVal(maps.get("HIS_CORE_CLIENT_NO")));		//	核心客户号
		upbean.setCustName(StringUtil.getStringVal(maps.get("HIS_CLIENT_NAME")));			//	客户名称
		upbean.setLimitAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("HIS_ONLINE_DISCOUNT_TOTAL_AMT")))?StringUtil.getStringVal(maps.get("HIS_ONLINE_DISCOUNT_TOTAL_AMT")):"0"));	//	在线贴现总额
		upbean.setUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("HIS_USED_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("HIS_USED_LIMIT_AMT")):"0"));			//	已用额度
		upbean.setAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("HIS_UN_USED_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("HIS_UN_USED_LIMIT_AMT")):"0"));			//	未用额度
		upbean.setProtocolDueDate(StringUtil.getStringVal(maps.get("HIS_EXPIRY_DATE")));				//	到期日期
		upbean.setIsEx(StringUtil.getStringVal(maps.get("HIS_BEFORE_HAND_FLAG")));			//	是否启用前手管理
		upbean.setAccountEntryNo(StringUtil.getStringVal(maps.get("HIS_DISCOUNT_IN_BANK_NO")));			//	放款账号
		upbean.setAccountEntryName(StringUtil.getStringVal(maps.get("HIS_DISCOUNT_IN_BANK_NAME")));			//	放款账户名称
		upbean.setAccountEntryDevNo(StringUtil.getStringVal(maps.get("HIS_ENTER_ACCT_BRANCH_NO")));		//	入账机构号
		upbean.setAccountEntryDevName(StringUtil.getStringVal(maps.get("HIS_ENTER_ACCT_BRANCH_NAME")));	//	入账机构名称
		upbean.setEntryBankNo(StringUtil.getStringVal(maps.get("HIS_LOANER_ACCT_NO")));		//	贴入行行号
		upbean.setEntryBankName(StringUtil.getStringVal(maps.get("HIS_LOANER_ACCT_NAME")));		//	贴入行名称
		upbean.setRateFloatType(StringUtil.getStringVal(maps.get("HIS_RATE_ADJUST_WAY")));			//	利率调整方式
		upbean.setRateFloatNum(StringUtil.getStringVal(maps.get("HIS_FLOAT_INT_RATE_VALUE")));		//	利率浮动值
		upbean.setContractOrgNo(StringUtil.getStringVal(maps.get("HIS_APP_BRANCH_NO")));				//	经办机构号
		upbean.setContractOrgName(StringUtil.getStringVal(maps.get("HIS_APP_BRANCH_NAME")));			//	经办机构名称
		upbean.setHandlerNo(StringUtil.getStringVal(maps.get("HIS_APPER_ID")));					//	经办人编号
		upbean.setHandler(StringUtil.getStringVal(maps.get("HIS_APPER_NAME")));				//	经办人名称
		upbean.setProtocolOpenDate(StringUtil.getStringVal(maps.get("HIS_OPEN_DATE")));					//	开通日期
		
		List<TxExDetailBean> txExDetailBeans = new ArrayList<TxExDetailBean>();
		List<TxExDetailBean> uptxExDetailBeans = new ArrayList<TxExDetailBean>();
		
		List<TxContactDetailBean> txContactDetailBeans = new ArrayList<TxContactDetailBean>();
		List<TxContactDetailBean> uptxContactDetailBeans = new ArrayList<TxContactDetailBean>();
		
		List<TxRateAdjustInfo> adjustInfos = new ArrayList<TxRateAdjustInfo>();
		//	前手信息组
		List<Map> lists = resp.getDetails();
		if(lists.size() > 0){
			for (Map mapss : lists) {
				List<Map> exDetails = (List<Map>) mapss.get("BEFORE_HAND_INFO_ARRAY");
				
				if(exDetails != null && exDetails.size() > 0){
					for (Map map : exDetails) {
						TxExDetailBean txExDetailBean = new TxExDetailBean(); 
						TxExDetailBean histxExDetailBean = new TxExDetailBean(); 
						txExDetailBean.setModeTypeDesc(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.MODIFY_TYPE")));
						
						if("01".equals(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.MODIFY_TYPE")))){	//	新增不展示历史
							txExDetailBean.setExCustNo(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_ID")));
							txExDetailBean.setExCustName(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_NAME")));
							txExDetailBean.setExTotalAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")):"0"));
							txExDetailBean.setExUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")):"0"));
							txExDetailBean.setExAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")):"0"));
							txExDetailBean.setExStatus(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_STATUS")));
						}else if("02".equals(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.MODIFY_TYPE")))){	//	修改
							txExDetailBean.setExCustNo(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_ID")));
							txExDetailBean.setExCustName(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_NAME")));
							txExDetailBean.setExTotalAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")):"0"));
							txExDetailBean.setExUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")):"0"));
							txExDetailBean.setExAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")):"0"));
							txExDetailBean.setExStatus(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_STATUS")));
						
							histxExDetailBean.setExCustNo(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_ID")));
							histxExDetailBean.setExCustName(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_NAME")));
							
							if(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_TOTAL_AMT")))){
								histxExDetailBean.setExTotalAmt(new BigDecimal(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_TOTAL_AMT"))));
							}
							
							if(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_USED_AMT")))){
								histxExDetailBean.setExUsedAmt(new BigDecimal(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_USED_AMT"))));
							}
							
							if(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_AVAIL_AMT")))){
								histxExDetailBean.setExAvailableAmt(new BigDecimal(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_AVAIL_AMT"))));
							}
							histxExDetailBean.setExStatus(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.HIS_BEFORE_HAND_STATUS")));
						}else{

							txExDetailBean.setExCustNo(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_ID")));
							txExDetailBean.setExCustName(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_NAME")));
							txExDetailBean.setExTotalAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_TOTAL_AMT")):"0"));
							txExDetailBean.setExUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_USED_AMT")):"0"));
							txExDetailBean.setExAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")))?StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_AVAIL_AMT")):"0"));
							txExDetailBean.setExStatus(StringUtil.getStringVal(map.get("BEFORE_HAND_INFO_ARRAY.BEFORE_HAND_STATUS")));
						
						}
						
						txExDetailBeans.add(txExDetailBean);		//	当前  
						uptxExDetailBeans.add(histxExDetailBean);	//	历史
					}
				}
				bean.setExDetailBeans(txExDetailBeans);
				upbean.setExDetailBeans(uptxExDetailBeans);
				
				//	联系人信息组
				List<Map> conDetails = (List<Map>) mapss.get("AGREE_ARRAY");
				
				if(conDetails != null && conDetails.size() > 0){
					for (Map map : conDetails) {
						if("40".equals(StringUtil.getStringVal(map.get("AGREE_ARRAY.CLIENT_TYPE")))){
							TxContactDetailBean txContactDetailBean = new TxContactDetailBean();
							TxContactDetailBean uptxContactDetailBean = new TxContactDetailBean();

							txContactDetailBean.setModeTypeDesc(StringUtil.getStringVal(map.get("AGREE_ARRAY.MODIFY_TYPE")));				//	修改类型
							if("01".equals(StringUtil.getStringVal(map.get("AGREE_ARRAY.MODIFY_TYPE")))){	//	新增不展示历史
								txContactDetailBean.setContactsName(StringUtil.getStringVal(map.get("AGREE_ARRAY.CLIENT_NAME")));
								txContactDetailBean.setContactsTel(StringUtil.getStringVal(map.get("AGREE_ARRAY.MOBILE")));
								txContactDetailBean.setContactsType(StringUtil.getStringVal(map.get("AGREE_ARRAY.CLIENT_TYPE")));
								txContactDetailBean.setContactsNo(StringUtil.getStringVal(map.get("AGREE_ARRAY.USER_NO")));
							}else if("03".equals(StringUtil.getStringVal(map.get("AGREE_ARRAY.MODIFY_TYPE")))){	//	删除
								txContactDetailBean.setContactsName(StringUtil.getStringVal(map.get("AGREE_ARRAY.CLIENT_NAME")));
								txContactDetailBean.setContactsTel(StringUtil.getStringVal(map.get("AGREE_ARRAY.MOBILE")));
								txContactDetailBean.setContactsType(StringUtil.getStringVal(map.get("AGREE_ARRAY.CLIENT_TYPE")));
								txContactDetailBean.setContactsNo(StringUtil.getStringVal(map.get("AGREE_ARRAY.USER_NO")));
							}else{
								txContactDetailBean.setContactsName(StringUtil.getStringVal(map.get("AGREE_ARRAY.CLIENT_NAME")));
								txContactDetailBean.setContactsTel(StringUtil.getStringVal(map.get("AGREE_ARRAY.MOBILE")));
								txContactDetailBean.setContactsType(StringUtil.getStringVal(map.get("AGREE_ARRAY.CLIENT_TYPE")));
								txContactDetailBean.setContactsNo(StringUtil.getStringVal(map.get("AGREE_ARRAY.USER_NO")));
								
								uptxContactDetailBean.setContactsName(StringUtil.getStringVal(map.get("AGREE_ARRAY.HIS_CLIENT_NAME")));
								uptxContactDetailBean.setContactsTel(StringUtil.getStringVal(map.get("AGREE_ARRAY.HIS_MOBILE")));
								uptxContactDetailBean.setContactsType(StringUtil.getStringVal(map.get("AGREE_ARRAY.HIS_CLIENT_TYPE")));
								uptxContactDetailBean.setContactsNo(StringUtil.getStringVal(map.get("AGREE_ARRAY.HIS_USER_NO")));
							}
							
							txContactDetailBeans.add(txContactDetailBean);
							uptxContactDetailBeans.add(uptxContactDetailBean);
						}
					}
				}
				bean.setTxContactDetailBeans(txContactDetailBeans);
				upbean.setTxContactDetailBeans(uptxContactDetailBeans);
				//	利率调整组
				List<Map> rateDetails = (List<Map>) mapss.get("INT_RATE_ARRAY");
				
				if(rateDetails != null &&  rateDetails.size() > 0){
					for (Map map : rateDetails) {
						TxRateAdjustInfo adjustInfo = new TxRateAdjustInfo();
						
						adjustInfo.setModifyType(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.MODIFY_TYPE")));
						adjustInfo.setRateAdjustWay(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.RATE_ADJUST_WAY")));
						adjustInfo.setHisRateAdjustWay(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.RATE_ADJUST_WAY")));
						adjustInfo.setTxTerm(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.REMAIN_EXPIRY_MONTHS")));
						adjustInfo.setHisTxTerm(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.REMAIN_EXPIRY_MONTHS")));
						adjustInfo.setBankType(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE")));
						adjustInfo.setHisBankType(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE")));
						adjustInfo.setRate(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.INT_RATE")))?
								StringUtil.getStringVal(map.get("INT_RATE_ARRAY.INT_RATE")):"0"));
						adjustInfo.setHisRate(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.INT_RATE")))?
								StringUtil.getStringVal(map.get("INT_RATE_ARRAY.INT_RATE")):"0"));
						
						adjustInfos.add(adjustInfo);
					}
				}
				bean.setIntroBillInfoBeans(adjustInfos);
			}
		}
		reMap.put("data", bean);
		reMap.put("updateData", upbean);
		
		return reMap;
	}
	
	/**
	 * 票据审价信息查询
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> billreviewPriceQuery(CenterPlatformBean centerPlatformBean,User user)throws Exception{
		List<TxReviewPriceInfo> infos = new ArrayList<TxReviewPriceInfo>();
		ReturnMessageNew messageNew = new ReturnMessageNew();
		if(!StringUtil.isStringEmpty(centerPlatformBean.getOnlineNo())){
			messageNew = centerPlatformSysService.billPriceQuery(centerPlatformBean,user);
		}
		
		List<Map> lists = messageNew.getDetails();
		
		if(lists.size() > 0){
//			返回前台数据处理
			for (Map maps : lists) {
				if(maps != null){
					TxReviewPriceInfo bean = new TxReviewPriceInfo();
					bean.setTjId(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.RECORD_KEY")));								//	记录主键
					bean.setTxReviewPriceBatchNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));		//	贴现定价审批编号
					bean.setOnlineNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.AGREE_NO")));	//	协议编号
					bean.setTxType(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_TYPE")));	//	贴现类型
					bean.setBillNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.BILL_NO")));		//	票据编号
					bean.setApplyState(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_STATUS")));			//	贴现状态
					
					bean.setBillAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.BILL_AMT")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.BILL_AMT")):"0"));			//	票面金额
					
					bean.setCurrentAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")):"0"));			//	当前额度
					
					bean.setReduceAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")):"0"));			//	调减金额
					
					
					bean.setAvailableAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.UN_DISCOUNT_AMT")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.UN_DISCOUNT_AMT")):"0"));	//	未贴现金额

					bean.setUsedAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNTED_AMT")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNTED_AMT")):"0"));		//	已贴现金额
					bean.setBillType(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.BILL_TYPE")));		//	票据类型
					bean.setApplyDate(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DATE")));		//	申请日期
					//	到期日
					Date duedate = DateUtils.adjustDateByDay(DateUtils.parse(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE"))), Integer.parseInt(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS"))));
					
					bean.setDueDate(DateUtils.toDateString(duedate));
					
					bean.setEffDate(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE")));		//	生效日期
//					bean.setBillNo(StringUtil.getStringVal(maps.get("EFFECTIVE_DAYS")));		//	有效天数
					bean.setAcptBankName(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_NAME")));		//	承兑行名称
					bean.setAcptBankNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_NO")));		//	承兑行名称
					bean.setAcptBankType(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_TYPE")));		//	承兑行类别
					
					bean.setApplyTxRate(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")):"0"));		//	申请贴现利率
					
					bean.setGuidanceRate(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.GUIDANCE_INT_RATE")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.GUIDANCE_INT_RATE")):"0"));		//	指导利率
					
					bean.setFavorRate(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_INT_RATE")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_INT_RATE")):"0"));		//	优惠利率
					
					bean.setBestFavorRate(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.MAX_DISCOUNT_INT_RATE")))?
							StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.MAX_DISCOUNT_INT_RATE")):"0"));		//	最优惠利率
//					bean.setBillNo(StringUtil.getStringVal(maps.get("MSG_SOURCE")));		//	信息来源
//					bean.setFinalApproveBranch(StringUtil.getStringVal(maps.get("APPROVE_BRANCH_NO")));		//	审批机构号
					bean.setFinalApproveBranch(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NAME")));		//	审批机构名称
					bean.setApproveBranchType(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_BRANCH_TYPE")));		//	审批机构层级
//					bean.setBillNo(StringUtil.getStringVal(maps.get("APPROVE_DATE")));		//	审批日期
				
					//	根据批次号获取本地信息
					TxReviewPriceDetail txReviewPriceDetail = txRateMaintainInfoService.quertTxReviewPriceDetail(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));
					if(txReviewPriceDetail != null){
						bean.setApplyDate(txReviewPriceDetail.getApplyTxDate());
					}
					
					
//					根据批次号获取主信息
					TxReviewPriceInfo returninfo = new TxReviewPriceInfo();
					CenterPlatformBean centerPlatformBean1 = new CenterPlatformBean();
					centerPlatformBean1.setBatchNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));
					List<TxReviewPriceInfo> info = txRateMaintainInfoService.queryTxReviewPriceInfo(centerPlatformBean1, null);
					if(info.size() > 0){
						returninfo = info.get(0);
					}
					
//					如果状态不一致  更新状态
//					if(!returninfo.getApplyState().equals( StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_STATUS")))){
//						returninfo.setApplyState(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_STATUS")));
//						txRateMaintainInfoService.updateTxReviewPriceInfo(bean);
//					}
					
//					更新状态
					String sql = "update t_bill_intro_info set status = '" + StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_STATUS")) + "' where batch_no = '" + maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO") 
							+ "' and bill_no = '" + StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.BILL_NO")) + "'";
				
					System.out.println(sql);
					dao.updateSQL(sql.toString());
					dao.flush();
					dao.clear();
					
					bean.setWorkerBranch(returninfo.getWorkerBranch());
					bean.setWorkerName(returninfo.getWorkerName());
					bean.setWorkerNo(returninfo.getWorkerNo());
					
					infos.add(bean);
				}
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rows", infos);
		map.put("results",StringUtil.isNotEmpty(StringUtil.getStringVal(messageNew.getAppHead().get("TOTAL_ROWS")))?messageNew.getAppHead().get("TOTAL_ROWS"):0);
		return map;
	}
	
	/**
	 * 额度审价信息查询
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> amtReviewPriceQuery(CenterPlatformBean centerPlatformBean,String pattern,User user)throws Exception{
		List<TxReviewPriceInfo> infos = new ArrayList<TxReviewPriceInfo>();
		ReturnMessageNew messageNew = new ReturnMessageNew();
		if(!StringUtil.isStringEmpty(centerPlatformBean.getOnlineNo())){
			if("1".equals(pattern)){
				centerPlatformBean.setTxType("01");
			}else{
				centerPlatformBean.setTxType("02");
			}
			messageNew = centerPlatformSysService.AmtBillPriceQuery(centerPlatformBean,user);
		}
		
		List<Map> lists = messageNew.getDetails();
		
		if(lists.size() > 0){
//			模式一和模式二处理数据方式不一致
			if("1".equals(pattern)){
				//	返回前台数据处理
				for (Map mapss : lists) {
					if(mapss != null){
						List<Map> listss = (List<Map>) mapss.get("APPROVE_INFO_ARRAY");
						
						if(listss != null && listss.size() > 0){
							for (Map maps : listss) {
								if(maps != null){
//									模式一：一对多模式   一个批次号多个调减对象
									
									TxReviewPriceInfo bean = new TxReviewPriceInfo();	//	本地对象
									if(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")))){
										TxReviewPriceInfo reviewPriceInfo = new TxReviewPriceInfo();
										reviewPriceInfo.setTxReviewPriceBatchNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));
										bean = txRateMaintainInfoService.queryTxReviewPriceInfo(reviewPriceInfo, null);
										
//										//	如果状态不一致  更新状态
//										if(!bean.getApplyState().equals( StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")))){
//											bean.setApplyState(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")));
//											txRateMaintainInfoService.updateTxReviewPriceInfo(bean);
//										}
									}
									
//									List<IntroBillInfoBean> infoBeans = bean.getIntroBillInfoBeans();
//									if(infoBeans.size() > 0){
//										for (IntroBillInfoBean introBillInfoBean : infoBeans) {
//											TxReviewPriceInfo info = new TxReviewPriceInfo();	//	返回结果对象
//											info.setId(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_APPROVE_NO")));
//											info.setTxReviewPriceBatchNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));
//											info.setAcptBankType(introBillInfoBean.getAcptBankType());
//											info.setTxTerm(introBillInfoBean.getTxTerm());
//											info.setApplyTxRate(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")):"0"));
//											info.setApproveAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT")):"0"));
//											info.setCurrentAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")):"0"));
//											info.setUsedAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNTED_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNTED_AMT")):"0"));
//											info.setAvailableAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.UN_DISCOUNT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.UN_DISCOUNT_AMT")):"0"));
//											info.setReduceAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")):"0"));
//											info.setApplyDate(bean.getTxReviewPriceDetail().getApplyDate());
//											info.setEffDate(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE")));
//											int duedate = Integer.parseInt(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE"))) + Integer.parseInt(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS")));
//											System.out.println(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE")));
//											System.out.println(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS")));
//											info.setDueDate(String.valueOf(duedate));
//											info.setApplyState(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")));
//											info.setWorkerName(bean.getWorkerName());
//											info.setWorkerNo(bean.getWorkerNo());
//											info.setWorkerBranch(bean.getWorkerBranch());
//											info.setFinalApproveBranch(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_BRANCH_NAME")));
//											infos.add(info);
//										}
//									}else{
										List<Map> details = (List<Map>) mapss.get("ACCEPTANCE_BANK_INFO_ARRAY");
										if(details != null &&  details.size() > 0){
											for (Map map : details) {
												TxReviewPriceInfo info = new TxReviewPriceInfo();	//	返回结果对象
												info.setTjId(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_APPROVE_NO")));
												info.setTxReviewPriceBatchNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));
												info.setAcptBankType(StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_TYPE")));
												String term = StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_EXPIRY_MONTHS"));
												term = term.replaceAll("\\|\\|", ",").replaceFirst(",", "");
												
												//	更新状态
												List<String> termsList = new ArrayList(Arrays.asList(term.split(",")));
												System.out.println(termsList);
												for (String str : termsList) {
													String sql = "update t_bill_intro_info set status = '" + StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")) + "' where batch_no = '" + maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO") 
															+ "' and tx_term = '" + str + "' and acpt_bank_type = '" + StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_TYPE")) + "'";
												
													System.out.println(sql);
													dao.updateSQL(sql.toString());
													dao.flush();
													dao.clear();
												}
												
												info.setTxTerm(term);
												info.setApplyTxRate(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")):"0"));
												info.setApproveAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT")):"0"));
												info.setCurrentAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")):"0"));
												info.setUsedAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.USED_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.USED_LIMIT_AMT")):"0"));
												info.setAvailableAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.AVAIL_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.AVAIL_LIMIT_AMT")):"0"));
												info.setReduceAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")):"0"));
												info.setApplyDate(bean.getTxReviewPriceDetail().getApplyDate());
												info.setEffDate(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE")));
												Date duedate = DateUtils.adjustDateByDay(DateUtils.parse(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE"))), Integer.parseInt(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS"))));
												
												info.setDueDate(DateUtils.toDateString(duedate));
												info.setApplyState(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")));
												info.setWorkerName(bean.getWorkerName());
												info.setWorkerNo(bean.getWorkerNo());
												info.setWorkerBranch(bean.getWorkerBranch());
												info.setFinalApproveBranch(bean.getApproveBranchName());
												infos.add(info);
											}
										}
									}
								}
							}
//						}
					}
				}
			}else{
//				返回前台数据处理
				System.out.println(lists.size());
				for (Map mapss : lists) {
					if(mapss != null){
						List<Map> listss = (List<Map>) mapss.get("APPROVE_INFO_ARRAY");
						System.out.println(listss.size());
						if(listss != null && listss.size() > 0){
							Map maps = listss.get(0);
							if(maps != null){
//									模式一：一对多模式   一个批次号多个调减对象
								TxReviewPriceInfo bean = new TxReviewPriceInfo();	//	本地对象
								if(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")))){
									TxReviewPriceInfo reviewPriceInfo = new TxReviewPriceInfo();
									reviewPriceInfo.setTxReviewPriceBatchNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));
									bean = txRateMaintainInfoService.queryTxReviewPriceInfo(reviewPriceInfo, null);
//									如果状态不一致  更新状态
//									if(!bean.getApplyState().equals( StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")))){
//										bean.setApplyState(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")));
//										txRateMaintainInfoService.updateTxReviewPriceInfo(bean);
//									}
								}
								
								List<IntroBillInfoBean> infoBeans = bean.getIntroBillInfoBeans();
								
								List<Map> details = (List<Map>) mapss.get("ACCEPTANCE_BANK_INFO_ARRAY");
								System.out.println(details.size());
								if(details != null &&  details.size() > 0){
									TxReviewPriceInfo info = new TxReviewPriceInfo();	//	返回结果对象
									for (Map map : details) {
										info.setTjId(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_APPROVE_NO")));
										info.setTxReviewPriceBatchNo(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO")));
										info.setAcptBankType(StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_TYPE")));
										
										String term = StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_EXPIRY_MONTHS"));
										term = term.replaceAll("\\|\\|", ",").replaceFirst(",", "");
										
										//	更新状态
										List<String> termsList = new ArrayList(Arrays.asList(term.split(",")));
										System.out.println(termsList);
										for (String str : termsList) {
											String sql = "update t_bill_intro_info set status = '" + StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")) + "' where batch_no = '" + maps.get("APPROVE_INFO_ARRAY.DISCOUNT_APPROVE_NO") 
													+ "' and tx_term = '" + str + "' and acpt_bank_type = '" + StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_TYPE")) + "'";
										
											System.out.println(sql);
											dao.updateSQL(sql.toString());
											dao.flush();
											dao.clear();
										}
										
										info.setTxTerm(term);
										info.setApplyTxRate(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPLY_DISCOUNT_INT_RATE")):"0"));
										info.setApproveAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.APPROVE_LIMIT_AMT")):"0"));
										info.setCurrentAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.CURRENT_LIMIT_AMT")):"0"));
										info.setUsedAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.USED_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.USED_LIMIT_AMT")):"0"));
										info.setAvailableAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.AVAIL_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.AVAIL_LIMIT_AMT")):"0"));
										info.setReduceAmt(new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")))?StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.ADJUST_DECREASE_LIMIT_AMT")):"0"));
										info.setApplyDate(bean.getTxReviewPriceDetail().getApplyDate());
										info.setEffDate(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE")));
										Date duedate = DateUtils.adjustDateByDay(DateUtils.parse(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DATE"))), Integer.parseInt(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.EFFECTIVE_DAYS"))));
										
										info.setDueDate(DateUtils.toDateString(duedate));
										info.setApplyState(StringUtil.getStringVal(maps.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")));
										info.setWorkerName(bean.getWorkerName());
										info.setWorkerNo(bean.getWorkerNo());
										info.setWorkerBranch(bean.getWorkerBranch());
										info.setFinalApproveBranch(bean.getApproveBranchName());
										info.setIntroBillInfoBeans(infoBeans);
									}
									infos.add(info);
								}
							}
						}
					}
				}
			}
		}
		
		Map<String, Object> remap = new HashMap<String, Object>();
		remap.put("rows", infos);
		remap.put("results",StringUtil.isNotEmpty(StringUtil.getStringVal(messageNew.getAppHead().get("TOTAL_ROWS")))?messageNew.getAppHead().get("TOTAL_ROWS"):0);
		return remap;
	}

	
	/**
	 * 在线贴现业务跟踪查询
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> onlineTxBusinessQuery(CenterPlatformBean centerPlatformBean, Page page, User user)throws Exception {
		List<TxBusinessInfo> infos = new ArrayList<TxBusinessInfo>();
		ReturnMessageNew messageNew = new ReturnMessageNew();
		
		centerPlatformBean.setPageNum(page.getPageIndex());
		centerPlatformBean.setPageSize(page.getPageSize());
		messageNew = centerPlatformSysService.onlineTxBusinessQuery(centerPlatformBean,user);
		
		List<Map> lists = messageNew.getDetails();
		
		if(lists.size() > 0){
			//	返回前台数据处理
			for (Map maps : lists) {
				if(maps != null){
					TxBusinessInfo bean = new TxBusinessInfo();
					bean.setId(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.ONLINE_DISCOUNT_APPLY_NO")));
					bean.setCustNo(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.CORE_CLIENT_NO")));
					bean.setCustName(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.DISCOUNT_APPLYER_NAME")));
					bean.setOnLineNo(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.AGREE_NO")));
					bean.setBusinessType("01");
					bean.setBusinessNo(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.DISCOUNT_CONTRACT_NO")));
					bean.setBusinessAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.DISCOUNT_TOTAL_AMT")))
							?StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.DISCOUNT_TOTAL_AMT")):"0"));
					bean.setApplyDate(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.DISCOUNT_APPLY_DATE")));
					bean.setApplyType(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.DISCOUNT_APPLY_TYPE")));
					bean.setStatus(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.DISCOUNT_CONTRACT_STATUS")));
					bean.setErrorMsg(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.ERROR_MSG"))+StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.MIS_ERROR_MSG")));
					bean.setErrId(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.ERROR_MSG_KEY")));
					bean.setErrorStatus(StringUtil.getStringVal(maps.get("DISCOUNT_INFO_ARRAY.ERROR_DEAL_STATUS")));
					infos.add(bean);
				}
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rows", infos);
		map.put("results",StringUtil.isNotEmpty(StringUtil.getStringVal(messageNew.getAppHead().get("TOTAL_ROWS")))?messageNew.getAppHead().get("TOTAL_ROWS"):0);
		System.out.println(messageNew.getAppHead().get("TOTAL_ROWS"));
		return map;
	}

	/**
	 * 在线贴现业务详情查询
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> onlineTxBusinessDetail(CenterPlatformBean centerPlatformBean, Page page, User user)throws Exception {
		List<TxBusinessDetail> infos = new ArrayList<TxBusinessDetail>();
		ReturnMessageNew messageNew = new ReturnMessageNew();
		
		messageNew = centerPlatformSysService.onlineTxBusinessDetail(centerPlatformBean,user);
		
		List<Map> lists = messageNew.getDetails();
		
		if(lists.size() > 0){
			//	返回前台数据处理
			for (Map maps : lists) {
				if(maps != null){
					TxBusinessDetail bean = new TxBusinessDetail();
					bean.setId(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.BILL_ID")));
					bean.setIouNo(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.IOU_NO")));
					bean.setBillNo(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.BILL_NO")));
					bean.setChildBillNoBegin(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.START_BILL_NO")));
					bean.setChildBillNoEnd(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.END_BILL_NO")));
					bean.setRealChildBillNoEnd(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.REAL_END_BILL_NO")));
					bean.setBillAmt(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DISCOUNT_AMT")))
							?StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DISCOUNT_AMT")):"0"));
					bean.setBillType(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.BILL_TYPE")));
					bean.setApplyRate(new BigDecimal(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.REAL_INT_RATE")))
							?StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.REAL_INT_RATE")):"0"));
					bean.setIssueDate(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DRAW_BILL_DATE")));
					bean.setDueDate(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.REMIT_EXPIRY_DATE")));
					bean.setDelayDays(!StringUtil.isStringEmpty(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DEFER_DAYS")))
							?Integer.parseInt(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DEFER_DAYS"))):0);
					bean.setIssuerName(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DRAWER_NAME")));
					bean.setIssuerAccountNo(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DRAWER_ACCT_NO")));
					bean.setIssuerBankNo(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")));
					bean.setIssuerBankName(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));
					
					bean.setAcptBankNo(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")));
					bean.setAcptBankName(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));
					bean.setPayeeAccountNo(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.PAYEE_ACCT_NO")));
					bean.setPayeeBankName(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));
					bean.setPayeeBankNo(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")));
					bean.setBillStatus(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.DISCOUNT_BILL_STATUS")));
					
					String errorMsg = "";
					if(StringUtil.isNotEmpty(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.MIS_RET_MSG")))){
						errorMsg = StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.MIS_RET_MSG"));
					}else{
						errorMsg = StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ERROR_MSG"));
					}
					bean.setErrorMsg(errorMsg);
					bean.setErrId(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ERROR_MSG_KEY")));
					bean.setErrorStatus(StringUtil.getStringVal(maps.get("BILL_INFO_ARRAY.ERROR_DEAL_STATUS")));
					infos.add(bean);
				}
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rows", infos);
		map.put("results",StringUtil.isNotEmpty(StringUtil.getStringVal(messageNew.getAppHead().get("TOTAL_ROWS")))?messageNew.getAppHead().get("TOTAL_ROWS"):0);
		System.out.println(messageNew.getAppHead().get("TOTAL_ROWS"));
		return map;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<TxReduceInfoBean> queryReduceInfo(TxReviewPriceInfo info) throws Exception {
		List paras = new ArrayList<String>();
		String sb = " select trm from TxReduceInfoBean as trm where 1=1 ";
		
		if ( null!= info.getTxReviewPriceBatchNo()) {
			sb +=" and trm.batchNo = ?";
			paras.add(info.getTxReviewPriceBatchNo());
		}
		if("04".equals(info.getTxType()) || "05".equals(info.getTxType())){
			if ( null!= info.getAcptBankType()) {
				sb +=" and trm.bankType = ?";
				paras.add(info.getAcptBankType());
			}
			
			if ( null!= info.getTxTerm()) {
				sb +=" and trm.applyTerm = ?";
				paras.add(info.getTxTerm());
			}
			
			if ( null!= info.getApplyTxRate()) {
				sb +=" and trm.applyTxRate = ?";
				paras.add(info.getApplyTxRate());
			}
		}else{
			if ( null!= info.getBillNo()) {
				sb +=" and trm.billNo = ?";
				paras.add(info.getBillNo());
			}
		}
		
		sb +=" order by trm.lastUpdateTime asc";
		
		List<TxReduceInfoBean> txReduceInfoBeans = find(sb, paras);
		return txReduceInfoBeans;
	}

	@Override
	public String getEntityName() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return null;
	}
}
