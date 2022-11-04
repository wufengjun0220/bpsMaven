package com.mingtech.application.pool.discount.service.ipml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxConfigManagerService;
import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

public class TxConfigManagerServiceimpl extends GenericServiceImpl implements TxConfigManagerService{
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	
	@Autowired
	private RoleService roleService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String queryCustConfig(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception {
		
		centerPlatformBean.setPageSize(page.getPageIndex());
		centerPlatformBean.setPageNum(page.getPageSize());
		
		ReturnMessageNew resp = centerPlatformSysService.txCustConfigQuery(centerPlatformBean,user);
		List<Map> lists = resp.getDetails();
		
		List<CenterPlatformBean> returnList = new ArrayList<CenterPlatformBean>();
		
		if(lists != null && lists.size() > 0){
			//	返回前台数据处理
			for (Map list : lists) {
				CenterPlatformBean bean = new CenterPlatformBean();
				bean.setId(StringUtil.getStringVal(list.get("RULE_ARRAY.CLIENT_ONLINE_DISCOUNT_ID")));
				bean.setCustNo(StringUtil.getStringVal(list.get("RULE_ARRAY.CORE_CLIENT_NO")));
				bean.setCustName(StringUtil.getStringVal(list.get("RULE_ARRAY.CLIENT_NAME")));
				bean.setOnlineNo(StringUtil.getStringVal(list.get("RULE_ARRAY.DISCOUNT_AGREE_NO")));
				bean.setTxFlag(StringUtil.getStringVal(list.get("RULE_ARRAY.CLIENT_ONLINE_DISCOUNT_STATUS")));
				returnList.add(bean);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("results",StringUtil.isNotEmpty(StringUtil.getStringVal(resp.getAppHead().get("TOTAL_ROWS")))?resp.getAppHead().get("TOTAL_ROWS"):0);
		map.put("rows", returnList);
		
		return JSON.toJSONString(map);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> queryAcceptBankBlackList(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception{
		centerPlatformBean.setPageSize(page.getPageSize());
		centerPlatformBean.setPageNum(page.getPageIndex());
		
		ReturnMessageNew resp = centerPlatformSysService.txAcceptBankBlackListQuery(centerPlatformBean,user);
		List<Map> lists = resp.getDetails();
		
		List<CenterPlatformBean> returnList = new ArrayList<CenterPlatformBean>();
		
		if(lists != null && lists.size() > 0){
			//	返回前台数据处理
			for (Map list : lists) {
				CenterPlatformBean bean = new CenterPlatformBean();
				bean.setAcceptBankNo(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.ACCEPTANCE_BANK_NO")));
				bean.setAcceptBankName(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.ACCEPTANCE_BANK_NAME")));
				bean.setEffState(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.BLACK_LIST_STATUS")));
				bean.setEffDate(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.EFFECTIVE_DATE")));
				bean.setWorkerName(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_PERSON_NAME")));
				bean.setWorkerNo(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_CLIENT_NO")));
				bean.setBranchCode(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NO")));
				bean.setBranchName(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NAME")));
				returnList.add(bean);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", returnList);
		map.put("total",StringUtil.isNotEmpty(StringUtil.getStringVal(resp.getAppHead().get("TOTAL_ROWS")))?resp.getAppHead().get("TOTAL_ROWS"):0);
		System.out.println(resp.getAppHead().get("TOTAL_ROWS"));
		return map;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> queryDrawerBlackList(CenterPlatformBean centerPlatformBean,Page page, User user) throws Exception{
		centerPlatformBean.setPageSize(page.getPageSize());
		centerPlatformBean.setPageNum(page.getPageIndex());
		
		ReturnMessageNew resp = centerPlatformSysService.txDrawerBlackListQuery(centerPlatformBean,user);
		List<Map> lists = resp.getDetails();
		
		List<CenterPlatformBean> returnList = new ArrayList<CenterPlatformBean>();
		
		if(lists != null && lists.size() > 0){
			//	返回前台数据处理
			for (Map list : lists) {
				CenterPlatformBean bean = new CenterPlatformBean();
				bean.setId(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.BLACK_LIST_KEY")));			//	主键ID
				bean.setIssuerName(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.DRAWER_NAME")));		//	出票人名称
				bean.setBlackStatus(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.BLACK_LIST_STATUS")));	//	黑名单状态
				bean.setWorkerNo(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_CLIENT_NO")));	//	维护人编号
				bean.setWorkerName(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_PERSON_NAME")));	//	维护人名称
				bean.setBranchCode(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NO")));		//	维护机构号
				bean.setBranchName(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.MAINTAIN_BRANCH_NAME")));			//	维护机构名
				bean.setEffDate(StringUtil.getStringVal(list.get("BLACK_LIST_INFO_ARRAY.EFFECTIVE_DATE")));		//	生效日期
				returnList.add(bean);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", returnList);
		map.put("total",StringUtil.isNotEmpty(StringUtil.getStringVal(resp.getAppHead().get("TOTAL_ROWS")))?resp.getAppHead().get("TOTAL_ROWS"):0);
		System.out.println(resp.getAppHead().get("TOTAL_ROWS"));
		return map;
		
	}

	@Override
	public Map<String, String> queryTxConfig(User user) throws Exception {
		Map<String, String> map = new HashMap<String, String>();

		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//总行管理员可查
				ReturnMessageNew resp = centerPlatformSysService.txQueryConfig(user);
				Map<String, String>  resMap = resp.getBody();
				
				map.put("id", StringUtil.getStringVal(resMap.get("AMT_CONTROL_INFO_INFO_ID")));
				map.put("busi_id", StringUtil.getStringVal(resMap.get("DISCOUNT_CONTROL_INFO_INFO_ID")));
				map.put("txAmtFlag", StringUtil.getStringVal(resMap.get("AMT_CONTROL_FLAG")));		//	单笔贴现金额控制启用状态
				map.put("minAmount", StringUtil.getStringVal(resMap.get("MIN_AMT")));
				map.put("maxAmount", StringUtil.getStringVal(resMap.get("MAX_AMT")));
				map.put("txTimeConfig", StringUtil.getStringVal(resMap.get("DISCOUNT_CONTROL_FLAG")));	//	贴现期限控制启用状态
				map.put("txTimeConfigType", StringUtil.getStringVal(resMap.get("DISCOUNT_CONTROL_TYPE")));	//	M-月  D-天
				map.put("txMinTimeConfig", StringUtil.getStringVal(resMap.get("START_DATE")));
				map.put("txMaxTimeConfig", StringUtil.getStringVal(resMap.get("END_DATE")));
			}
		}
		
		return map;
	}

	@Override
	public String getEntityName() {
		return null;
	}


	@Override
	public Class getEntityClass() {
		return null;
	}


	
}
