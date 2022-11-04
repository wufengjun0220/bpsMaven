package com.mingtech.application.pool.discount.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxConfigManagerService;
import com.mingtech.application.pool.query.web.CommonQueryController;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.application.utils.ExcelUtils;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 在线贴现票据管理
 * */
@Controller
public class TxBillManagerController extends BaseController{
	private static final Logger logger = Logger.getLogger(CommonQueryController.class);
	@Autowired
	private TxConfigManagerService txConfigManagerService;
	
	@Autowired
	private CenterPlatformSysService  centerPlatformSysService;
	
	@Autowired
	private DictCommonService dictCommonService;
	@Autowired
	private BlackListManageService blackListManageService ; 

	/**
	 * 承兑行黑名单查询
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("queryAcceptBankBlackList")
	public void queryAcceptBankBlackList(CenterPlatformBean centerPlatformBean){
		try {
			String  json = RESULT_EMPTY_DEFAULT;
			User user = this.getCurrentUser();
			Page page = this.getPage();
			List<CenterPlatformBean> lists = null;
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap = txConfigManagerService.queryAcceptBankBlackList(centerPlatformBean, page, user);
			lists = (List<CenterPlatformBean>) returnMap.get("result");
			
			if (lists.size() > 0) {
				json = JsonUtil.buildJson(lists, Long.parseLong(StringUtil.getStringVal(returnMap.get("total"))));
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单查询失败"+ e.getMessage());
		}
	}

	/**
	 * 承兑行黑名单维护
	 */
	@RequestMapping("acceptBankBlackListMt")
	public void acceptBankBlackListMt(CenterPlatformBean bean){
		try {
			User user = this.getCurrentUser();
			if(StringUtil.isStringEmpty(bean.getId())){
				bean.setModifyType("01");
			}else{
				bean.setModifyType("03");
			}
			
			if(DateUtils.checkOverLimited(DateUtils.parseDate(bean.getEffDate()), new Date())){
				bean.setEffState("1");
			}else{
				bean.setEffState("2");
			}
			
			List<CenterPlatformBean> centerPlatformBeans = new ArrayList<CenterPlatformBean>();
			centerPlatformBeans.add(bean);
			
			centerPlatformSysService.txAcceptBankBlackListMt(centerPlatformBeans,user);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单维护失败"+ e.getMessage());
		}
	}
	
	/**
	 * 承兑行黑名单删除
	 */
	@RequestMapping("deleteBankBlackList")
	public void deleteBankBlackList(@RequestBody List<CenterPlatformBean> beans){
		try {
			User user = this.getCurrentUser();
			centerPlatformSysService.txAcceptBankBlackListMt(beans,user);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单删除失败"+ e.getMessage());
		}
	}
	
	/**
	 * 承兑行黑名单批量导入
	 * */
	@SuppressWarnings("rawtypes")
	@RequestMapping("acceptBankBlackListImport")
	public void acceptBankBlackListImport(MultipartFile file){
		String res = "承兑行黑名单导入成功";
		String res1 = "";
		String res2 = "";
		String res3 = "";
		try {
			User user = this.getCurrentUser();
			JSONArray retArray = ExcelUtils.readMultipartFile(file);
			List<CenterPlatformBean> centerPlatformBeans = new ArrayList<CenterPlatformBean>();
			
			Set<String> sets = new HashSet<String>();
			
			for(int i = 0;i < retArray.size();i++){
				JSONObject obj = retArray.getJSONObject(i);
				Iterator iter = obj.entrySet().iterator();
				CenterPlatformBean bean =  new CenterPlatformBean();
			    while (iter.hasNext()) {
			      Map.Entry entry = (Map.Entry) iter.next();
			      String bankName = "";
			      if("承兑行行号".equals(entry.getKey().toString())){
			    	  if(sets.add(entry.getKey().toString())){		//	判断行号是否重复
			    		  bean.setAcceptBankNo(entry.getValue().toString());
				      }else{
				    	  if(StringUtil.isNotEmpty(res1)){
				    		  res1 += "," + entry.getValue().toString();
				    	  }else{
				    		  res1 +=  entry.getValue().toString();
				    	  }
				      }
			    	  
			    	  /**
				       * 校验行号是否存在
				       * */
			    	 bankName = blackListManageService.queryPjsAcptName(entry.getKey().toString(),"");
				     if("1".equals(bankName)){
				    	 if(StringUtil.isNotEmpty(res2)){
				    		  res2 += "," + entry.getValue().toString();
				    	  }else{
				    		  res2 +=  entry.getValue().toString();
				    	  }
				     }
				  }
			      
			      if("承兑行行名".equals(entry.getKey().toString())){
			    	  bean.setAcceptBankName(entry.getValue().toString());
			    	  /**
				       * 校验行号行名信息是否正确
				       * */
			    	  if(!"1".equals(bankName) && bankName.equals(entry.getKey().toString())){
			    		  if(StringUtil.isNotEmpty(res3)){
				    		  res3 += "," + entry.getValue().toString();
				    	  }else{
				    		  res3 +=  entry.getValue().toString();
				    	  }
			    	  }
			      }
				  if("状态".equals(entry.getKey().toString())){
					  if("生效".equals(entry.getValue().toString())){
						  bean.setEffState("1");	  
					  }else if("待生效".equals(entry.getValue().toString())){
						  bean.setEffState("2");	  
					  }else{
						  bean.setEffState("0");	  
					  }
				  }
				  if("生效日期".equals(entry.getKey().toString())){
					  bean.setEffDate(entry.getValue().toString());
				  }
				  if("经办人".equals(entry.getKey().toString())){
					  bean.setWorkerName(entry.getValue().toString());
				  }
				  if("经办人工号".equals(entry.getKey().toString())){
					  bean.setWorkerNo(entry.getValue().toString());
				  }
			    }
			    bean.setModifyType("01");
			    centerPlatformBeans.add(bean);
			}
			if(StringUtil.isNotEmpty(res1)){
				this.sendJSON(res1 + "已重复存在！请检查后再试");
				return;
			}
			
			if(StringUtil.isNotEmpty(res2)){
				this.sendJSON(res2 + "不存在！请检查后再试");
				return;
			}

			if(StringUtil.isNotEmpty(res3)){
				this.sendJSON(res3 + "对应行名信息不匹配！请检查再试");
				return;
			}
			
			res = centerPlatformSysService.txAcceptBankBlackListMt(centerPlatformBeans,user);
			this.sendJSON(res);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单导入失败"+ e.getMessage());
		}
	}
	
	/**
	 * 承兑行黑名单批量导入
	 * */
	@RequestMapping("acceptBankBlackListImport2")
	public void acceptBankBlackListImport2(@RequestBody List<CenterPlatformBean> beans){
		String res = "";
		String res1 = "";
		String res2 = "";
		String res3 = "";
		try {
			User user = this.getCurrentUser();
			
			Set<String> sets = new HashSet<String>();
			
			for(int i = 0;i < beans.size();i++){
				CenterPlatformBean bean =  beans.get(i);
				if(!sets.add(bean.getAcceptBankNo())){		//	判断行号是否重复
					if(StringUtil.isNotEmpty(res1)){
						res1 += "," + bean.getAcceptBankNo();
					}else{
						res1 += bean.getAcceptBankNo();
					}
				}
				
				/**
			       * 校验行号是否存在
			       * */
		    	String bankName = blackListManageService.queryPjsAcptName(bean.getAcceptBankNo(),"");
			    if("1".equals(bankName)){
			    	if(StringUtil.isNotEmpty(res2)){
			    		 res2 += "," + bean.getAcceptBankNo();
			    	 }else{
			    		 res2 +=  bean.getAcceptBankNo();
			    	 }
			    }
				
			    /**
			     * 校验行号行名信息是否正确
			     * */
			    if(!"1".equals(bankName) && !bankName.equals(bean.getAcceptBankName())){
			    	if(StringUtil.isNotEmpty(res3)){
			    		res3 += "," + bean.getAcceptBankNo();
			    	}else{
			    		res3 +=  bean.getAcceptBankNo();
			    	}
			    }
			}
			if(StringUtil.isNotEmpty(res1)){
				res += res1 + "已重复存在！";
			}
			
			if(StringUtil.isNotEmpty(res2)){
				res += res2 +  "不存在！";
			}

			if(StringUtil.isNotEmpty(res3)){
				res += res3 + "对应行名信息不匹配！";
			}

			if(StringUtil.isNotEmpty(res)){
				this.sendJSON(res);
				return;
			}
			
			res = centerPlatformSysService.txAcceptBankBlackListMt(beans,user);
			if("".equals(res)){
				res = "导入成功";
			}
			this.sendJSON(res);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单导入失败"+ e.getMessage());
		}
	}
	
	/**
	 * 承兑行黑名单批量导出
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("acceptBankBlackListEXP")
	public void acceptBankBlackListEXP(){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			//	查出需要导出的列表
			List<CenterPlatformBean> lists = new ArrayList<CenterPlatformBean>();
			CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
			
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap = txConfigManagerService.queryAcceptBankBlackList(centerPlatformBean, page, user);
			lists = (List<CenterPlatformBean>) returnMap.get("result");
			
			//	对应EXCEL数据转换
			List excelLists = new ArrayList<String[]>();
			excelLists = this.tranferBankList(lists);
			
			int[] num = {};
			String[] typeName = {};
			
			String[] titles = {"承兑行行号","承兑行行名","状态","生效日期","经办人","经办人工号"};

			Map<String, String> mapfileds = new LinkedHashMap<String, String>();
			byte[] buffer = dictCommonService.creatSheetModel(excelLists,titles, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("承兑行黑名单报表" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单查询失败"+ e.getMessage());
		}
	}

	
	/**
	 * 出票人黑名单查询
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("queryDrawerBlackList")
	public void queryDrawerBlackList(CenterPlatformBean centerPlatformBean){
		try {
			String  json = RESULT_EMPTY_DEFAULT;
			User user = this.getCurrentUser();
			Page page = this.getPage();
			List<CenterPlatformBean> lists = null;
			
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap = txConfigManagerService.queryDrawerBlackList(centerPlatformBean, page, user);;
			lists = (List<CenterPlatformBean>) returnMap.get("result");
			
			if (lists.size() > 0) {
				json = JsonUtil.buildJson(lists, Long.parseLong(StringUtil.getStringVal(returnMap.get("total"))));
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现出票人黑名单查询失败"+ e.getMessage());
		}
	}

	/**
	 * 出票人黑名单维护
	 */
	@RequestMapping("drawerBlackListMt")
	public void drawerBlackListMt(CenterPlatformBean bean){
		try {
			User user = this.getCurrentUser();
			
			if(bean.getIssuerName().length() > 60){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("在线贴现出票人黑名单维护失败：请输入60位之内的名称！");
			}else{
				if(StringUtil.isStringEmpty(bean.getId())){
					bean.setModifyType("01");
				}else{
					bean.setModifyType("03");
				}
				if(DateUtils.checkOverLimited(DateUtils.parseDate(bean.getEffDate()), new Date())){
					bean.setBlackStatus("1");
				}else{
					bean.setBlackStatus("0");
				}
				
				List<CenterPlatformBean> centerPlatformBeans = new ArrayList<CenterPlatformBean>();
				centerPlatformBeans.add(bean);
				centerPlatformSysService.txDrawerBlackListMt(centerPlatformBeans,user);
			}
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现出票人黑名单维护失败"+ e.getMessage());
		}
	}
	
	/**
	 * 出票人黑名单删除
	 */
	@RequestMapping("DeleteDrawerBlackList")
	public void DeleteDrawerBlackList(@RequestBody List<CenterPlatformBean> centerPlatformBeans){
		try {
			User user = this.getCurrentUser();
			centerPlatformSysService.txDrawerBlackListMt(centerPlatformBeans,user);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现出票人黑名单删除失败"+ e.getMessage());
		}
	}
	
	/**
	 * 出票人黑名单批量导入
	 * */
	@SuppressWarnings("rawtypes")
	@RequestMapping("drawerBlackListImport")
	public void drawerBlackListImport(MultipartFile file){
		try {
			User user = this.getCurrentUser();
			JSONArray retArray = ExcelUtils.readMultipartFile(file);
			
			List<CenterPlatformBean> centerPlatformBeans = new ArrayList<CenterPlatformBean>();
			
			for(int i = 0;i < retArray.size();i++){
				JSONObject obj = retArray.getJSONObject(i);
				Iterator iter = obj.entrySet().iterator();
				CenterPlatformBean bean = new CenterPlatformBean();
			    while (iter.hasNext()) {
			    	Map.Entry entry = (Map.Entry) iter.next();
				    if("出票人名称".equals(entry.getKey().toString())){
				    	bean.setIssuerName(entry.getValue().toString());
				    }
				    if("状态".equals(entry.getKey().toString())){
				    	if("生效".equals(entry.getValue().toString())){
				    		bean.setBlackStatus("1");	  
				    	}else if("待生效".equals(entry.getValue().toString())){
							bean.setBlackStatus("2");	  
						}else{
							bean.setBlackStatus("0");	  
						}
				    }
				    if("生效日期".equals(entry.getKey().toString())){
				    	bean.setEffDate(entry.getValue().toString());
				    }
				    if("经办人".equals(entry.getKey().toString())){
				    	bean.setWorkerName(entry.getValue().toString());
				    }
				    if("经办人工号".equals(entry.getKey().toString())){
				    	bean.setWorkerNo(entry.getValue().toString());
				    }
				    bean.setModifyType("01");
			    }
			    centerPlatformBeans.add(bean);
			}
			
			centerPlatformSysService.txDrawerBlackListMt(centerPlatformBeans,user);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单查询失败"+ e.getMessage());
		}
	}
	
	/**
	 * 出票人黑名单批量导出
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("drawerBlackListEXP")
	public void drawerBlackListEXP(CenterPlatformBean centerPlatformBean){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			//	查出需要导出的列表
			List<CenterPlatformBean> lists = null;

			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap = txConfigManagerService.queryDrawerBlackList(centerPlatformBean, page, user);;
			lists = (List<CenterPlatformBean>) returnMap.get("result");
			
			//	对应EXCEL数据转换
			List excelLists = new ArrayList<String[]>();
			excelLists = this.tranferDrawerList(lists);
			
			int[] num = {};
			String[] typeName = {};
			
			String[] titles = {"出票人名称","状态","生效日期","经办人","经办人工号"};

			Map<String, String> mapfileds = new LinkedHashMap<String, String>();
			byte[] buffer = dictCommonService.creatSheetModel(excelLists,titles, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("出票人黑名单报表" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现承兑行黑名单查询失败"+ e.getMessage());
		}
	}

	/**
	 * 贴现控制初始页面
	 * */
	@SuppressWarnings("rawtypes")
	@RequestMapping("OnLineTxControll")
	public void OnLineTxControll(CenterPlatformBean centerPlatformBean){
		try {
			User user = this.getCurrentUser();
			Map map = txConfigManagerService.queryTxConfig(user);
			String json = JSON.toJSONString(map);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现控制查询失败"+ e.getMessage());
		}
	}
	
	/**
	 * 贴现控制保存操作
	 * */
	@RequestMapping("saveOnLineTxControll")
	public void saveOnLineTxControll(@RequestBody CenterPlatformBean centerPlatformBean){
		try {
			User user = this.getCurrentUser();
			centerPlatformSysService.txSaveConfig(centerPlatformBean,user);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线贴现控制保存操作失败"+ e.getMessage());
		}
	}
	
	private List<String[]> tranferBankList(List<CenterPlatformBean> lists) {
		List<String[]> excelLists = new ArrayList<String[]>();
		for (CenterPlatformBean bean : lists) {
			String [] s = new String[6];
			s[0] = bean.getAcceptBankNo();
			s[1] = bean.getAcceptBankName();
			if("1".equals(bean.getEffState())){
				s[2] = "生效";
			}else if("2".equals(bean.getEffState())){
				s[2] = "待生效";
			}else{
				s[2] = "失效";
			}
			s[3] = bean.getEffDate();
			s[4] = bean.getWorkerName();
			s[5] = bean.getWorkerNo();
			excelLists.add(s);
		}
		return excelLists;
	}
	
	private List<String[]> tranferDrawerList(List<CenterPlatformBean> lists) {
		List<String[]> excelLists = new ArrayList<String[]>();
		for (CenterPlatformBean bean : lists) {
			String [] s = new String[5];
			s[0] = bean.getIssuerName();
			if("1".equals(bean.getBlackStatus())){
				s[1] = "生效";
			}else if("2".equals(bean.getBlackStatus())){
				s[1] = "待生效";
			}else{
				s[1] = "失效";
			}
			s[2] = bean.getEffDate();
			s[3] = bean.getWorkerName();
			s[4] = bean.getWorkerNo();
			excelLists.add(s);
		}
		return excelLists;
	}
}
