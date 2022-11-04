package com.mingtech.application.pool.discount.web;

/**
 * 在线贴现业务管理菜单功能列表
 * */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.TxReduceInfoBean;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxOnlineProtocolService;
import com.mingtech.application.pool.discount.service.TxRateMaintainInfoService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.core.page.Page;

@Controller
public class OnlineTxProtocolController extends BaseController{
	private static final Logger logger = Logger.getLogger(OnlineTxProtocolController.class);
	
	@Autowired
	private DictCommonService dictCommonService;
	
	@Autowired
	private TxOnlineProtocolService txOnlineProtocolService;
	
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	
	@Autowired
	private TxRateMaintainInfoService txRateMaintainInfoService; 
	
	/**
	 * 在线贴现协议查询初始化页面
	 * */
	@RequestMapping("/onlineTxProtocolQuery")
	public void onlineTxProtocolQuery(CenterPlatformBean centerPlatformBean){
		User user = this.getCurrentUser();
		Page page = this.getPage();
		
		try {
			String json = RESULT_EMPTY_DEFAULT;
			Map<String, Object> map = txOnlineProtocolService.txQueryOnlineProtocol(centerPlatformBean, page, user);
			json = JSON.toJSONString(map);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 审价列表查询
	 * */
	@RequestMapping("/queryReviewPriceInfos")
	public void  queryReviewPriceInfos(CenterPlatformBean centerPlatformBean){
		User user = this.getCurrentUser();
		Page page = this.getPage();
				
		try {
			String json = RESULT_EMPTY_DEFAULT;
			Map<String, Object> map = txOnlineProtocolService.txQueryOnlineProtocol(centerPlatformBean, page, user);
			json = JSON.toJSONString(map);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 在线贴现协议变更查询
	 * OnLineTxAgreeChangeQuery
	 * */
	@RequestMapping("/OnLineTxAgreeChangeQuery")
	public void OnLineTxAgreeChangeQuery(CenterPlatformBean centerPlatformBean){
		User user = this.getCurrentUser();
		Page page = this.getPage();
		
		try {
			String json = RESULT_EMPTY_DEFAULT;
			Map<String, Object> map = txOnlineProtocolService.OnLineTxAgreeChangeQuery(centerPlatformBean, page, user);
			json = JSON.toJSONString(map);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}

	/**
	 * 票据审价信息查询(中台)
	 * */
	@RequestMapping("/billreviewPriceQuery")
	public void billreviewPriceQuery(CenterPlatformBean centerPlatformBean){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			centerPlatformBean.setPageSize(page.getPageSize());
			centerPlatformBean.setPageNum(page.getPageIndex());
			
			String json = RESULT_EMPTY_DEFAULT;
			Map<String, Object> map =  txOnlineProtocolService.billreviewPriceQuery(centerPlatformBean,user);
			json = JSON.toJSONString(map);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 票据审价失效/调减功能
	 * 
	 * 调用票据审价维护接口(中台)
	 * **/
	@RequestMapping("/maintainBillReviewPrice")
	public void maintainBillReviewPrice(@RequestBody TxReviewPriceInfo info){
		try {
			User user = this.getCurrentUser();
			centerPlatformSysService.txBillPriceMaintain(info,user);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 额度审价失效操作、以及额度调减
	 * */
	@RequestMapping("/maintainAmtReviewPrice")
	public void maintainAmtReviewPrice(@RequestBody TxReviewPriceInfo info){
		String res = "0".equals(info.getApplyState())?"失效成功":"调减成功";
		
		try {
			User user = this.getCurrentUser();
			res = txRateMaintainInfoService.sendBillPrice(info,user);
			if("".equals(res)){
				res = "0".equals(info.getApplyState())?"失效失败"+res:"调减失败"+res;
			}
			this.sendJSON(res);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 额度审价信息查询(中台)
	 * */
	@RequestMapping("/amtReviewPriceQuery")
	public void amtReviewPriceQuery(CenterPlatformBean centerPlatformBean,String pattern){
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			centerPlatformBean.setPageSize(page.getPageSize());
			centerPlatformBean.setPageNum(page.getPageIndex());
			
			String json = RESULT_EMPTY_DEFAULT;
			Map<String, Object> map =  txOnlineProtocolService.amtReviewPriceQuery(centerPlatformBean,pattern,user);
			json = JSON.toJSONString(map);
			sendJSON(json);
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 额度审价调减查询(基础信息)
	 * */
	@RequestMapping("/AmtreviewPriceQuerys")
	public void AmtreviewPriceQuerys(@RequestBody CenterPlatformBean centerPlatformBean,String pattern){
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			centerPlatformBean.setPageSize(page.getPageSize());
			centerPlatformBean.setPageNum(page.getPageIndex());
			
			String json = RESULT_EMPTY_DEFAULT;
			Map<String, Object> map =  txOnlineProtocolService.amtReviewPriceQuery(centerPlatformBean,pattern,user);
			json = JSON.toJSONString(map);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	

	/**
	 * 在线贴现协议历史变更详情
	 * */	
	@RequestMapping("/OnLineTxAgreeChangeQueryByid")
	public void OnLineTxAgreeChangeQueryByid(String id){
		CenterPlatformBean platformBean = new CenterPlatformBean();
		User user = this.getCurrentUser();
		platformBean.setId(id);
		try {
			String json = RESULT_EMPTY_DEFAULT;
			Map<String, Object> map = txOnlineProtocolService.TxAgreeChangeDeailsQuery(platformBean, user);
			json = JSON.toJSONString(map);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 在线贴现业务跟踪查询
	 * */
	@RequestMapping("/onlineTxBusinessQuery")
	public void onlineTxBusinessQuery(CenterPlatformBean centerPlatformBean){
		User user = this.getCurrentUser();
		Page page = this.getPage();
		String  json = RESULT_EMPTY_DEFAULT;
		try {
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap = txOnlineProtocolService.onlineTxBusinessQuery(centerPlatformBean, page, user);
			json = JSON.toJSONString(returnMap);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 业务终止按钮
	 * */
	@RequestMapping("/closeBusiness")
	public void closeBusiness(@RequestBody CenterPlatformBean centerPlatformBean){
		System.out.println(centerPlatformBean.getId());
		System.out.println(centerPlatformBean.getOpreaType());
		User user = this.getCurrentUser();
		try {
			centerPlatformSysService.closeBusiness(user,centerPlatformBean);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	
	/**
	 * 在线贴现业务跟踪详情查询
	 * */
	@RequestMapping("/onlineTxBusinessDetail")
	public void onlineTxBusinessDetail(CenterPlatformBean centerPlatformBean){
		User user = this.getCurrentUser();
		Page page = this.getPage();
		String  json = RESULT_EMPTY_DEFAULT;
		try {
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap = txOnlineProtocolService.onlineTxBusinessDetail(centerPlatformBean, page, user);
			json = JSON.toJSONString(returnMap);
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 获取额度一调减信息
	 * */
	@RequestMapping("/queryReduceInfo")
	public void queryReduceInfo(@RequestBody TxReviewPriceInfo info){
		try {
			List<TxReduceInfoBean> lists = txOnlineProtocolService.queryReduceInfo(info);
			
			String json = JSON.toJSONString(lists);
			sendJSON(json);
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
}
