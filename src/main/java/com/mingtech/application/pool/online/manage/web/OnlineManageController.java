package com.mingtech.application.pool.online.manage.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineBlackInfo;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfoHist;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.application.utils.ExcelUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;



@Controller
public class OnlineManageController extends BaseController {
	private static final Logger logger = Logger.getLogger(OnlineManageController.class);
	
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private DictCommonService dictCommonService;
	
	/**
	 * 查询在线协议信息
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAgreementList",method=RequestMethod.POST)
	public void queryOnlineAgreementList(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.txqueryOnlineAgreementList(bean,user,page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 在线协议数据导出
	 * @author wfj
	 * @date 2021-12-13
	 */
	@RequestMapping(value="OnlineAgreementListExpt",method=RequestMethod.POST)
	public void OnlineAgreementListExpt(HttpServletResponse response,OnlineQueryBean bean){
		ExcelWriter excelWriter = null;
		try {
			User user = this.getCurrentUser();
			List result = new ArrayList();
			String dateTime = DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIME_FORMAT);
			response.setContentType("application/vnd.ms-excel");
		    response.setCharacterEncoding("utf-8");
		    String fileName = URLEncoder.encode("在线协议信息查询", "UTF-8");
		    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
			Map<String, String> mapinfo = new LinkedHashMap();
			
			mapinfo.put("onlineNo", "协议编号");
			mapinfo.put("onlineProtocolTypeDesc", "协议类型");
			mapinfo.put("statusDesc", "状态");
			mapinfo.put("custName", "客户名称");
			mapinfo.put("totalAmt", "在线业务限额");
			mapinfo.put("usedAmt", "已用额度");
			mapinfo.put("availableAmt", "可用额度");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("bpsName", "票据池名称");
			mapinfo.put("appName", "签约客户经理");
			mapinfo.put("poolCreditRatio", "票据池额度占用比例（%）");
			mapinfo.put("signBranchName", "签约机构");
			mapinfo.put("dueDate", "协议到期日");
			mapinfo.put("openDate", "协议开通日期");
			mapinfo.put("changeDate", "协议变更日期");
			
			excelWriter = EasyExcel.write(response.getOutputStream()).head(ExcelUtil.head(mapinfo)).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("在线协议信息查询").head(ExcelUtil.head(mapinfo)).build();
			Page page = new Page();
			do{
				page.setPageSize(ExcelUtil.PageSize);
				page.setPageIndex(ExcelUtil.pageIndex);
				result = onlineManageService.txqueryOnlineAgreementList(bean,user,page);
				List values1 = ExcelUtil.convertBeanToArray(result, mapinfo);
				excelWriter.write(ExcelUtil.dataList(values1), writeSheet);
				ExcelUtil.pageIndex++;	
			}while(result.size()!=0);
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
		} catch (Exception e) {
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
			logger.error("在线协议信息导出失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		
		/*int[] num = { };
		String[] typeName = { "amount" };
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.txqueryOnlineAgreementList(bean,user,page);
			
			List list1 = onlineManageService.findOnlineListExpt(list, getPage());
			
			String ColumnNames = "onlineNo,onlineProtocolTypeDesc,statusDesc,custName,totalAmt,usedAmt,availableAmt,bpsNo,bpsName,appName," +
					"poolCreditRatio,signBranchName,dueDate,openDate,changeDate ";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("onlineNo", "协议编号");
			mapinfo.put("onlineProtocolTypeDesc", "协议类型");
			mapinfo.put("statusDesc", "状态");
			mapinfo.put("custName", "客户名称");
			mapinfo.put("totalAmt", "在线业务限额");
			mapinfo.put("usedAmt", "已用额度");
			mapinfo.put("availableAmt", "可用额度");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("bpsName", "票据池名称");
			mapinfo.put("appName", "签约客户经理");
			mapinfo.put("poolCreditRatio", "票据池额度占用比例（%）");
			mapinfo.put("signBranchName", "签约机构");
			mapinfo.put("dueDate", "协议到期日");
			mapinfo.put("openDate", "协议开通日期");
			mapinfo.put("changeDate", "协议变更日期");
			

			
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list1, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("在线协议信息.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
			
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线协议信息导出失败"+ e.getMessage());
		} */
	}
	
	/**
	 * 查询在线协议明细信息
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineProtocolByCondition",method=RequestMethod.POST)
	public void queryOnlineProtocolByCondition(OnlineQueryBean bean){
		try {
			if(PublicStaticDefineTab.PRODUCT_001.equals(bean.getOnlineProtocolType())){
				PedOnlineAcptProtocol protocol = (PedOnlineAcptProtocol) pedOnlineAcptService.load(bean.getId());
			}else{
				PedOnlineCrdtProtocol protocol = (PedOnlineCrdtProtocol) pedOnlineCrdtService.load(bean.getId());
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 查询在线协议短信通知人
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineMsgByOnlineNo",method=RequestMethod.POST)
	public void queryOnlineMsgByOnlineNo(OnlineQueryBean bean){
		try {
			String json = RESULT_EMPTY_DEFAULT;
			List list = onlineManageService.queryOnlineMsgInfoList(bean.getOnlineNo(), null);
			if(null != list && list.size()>0){
				json = JsonUtil.buildJson(list, list.size());
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 新增/修改在线业务禁入名单
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="saveOrUpdateBlackList",method=RequestMethod.POST)
	public void saveOrUpdateBlackList(PedOnlineBlackInfo info){
		try {
			Boolean bo=info.getValidDate().matches("[0-9]{1,}");
			if(!bo){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("有效期必须为数字");
			}else{
				if(info.getStartDate().compareTo(DateUtils.getCurrDate())<0){
					this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
					this.sendJSON("起始日期应大于等于"+DateUtils.toDateString(new Date()));
				}else{
					onlineManageService.txSaveOrUpdateBlackList(info);
					this.sendJSON("操作成功");
				}
			}
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("操作失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 客户信息查询
	 * @author wfj
	 * @date 2021-11-5
	 */
	@RequestMapping(value="queryCustorByCore",method=RequestMethod.POST)
	public void queryCustorByCore(String custNo){
		String json="";
		try {
			json = onlineManageService.toQueryCustorForCore(custNo);
			
		} catch (Exception e) {
			json ="2";//查询失败，请稍候再试...
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	* 方法说明: 删除角色
	* @param
	* @return
	* @date 2021-10-14
	*/
	@RequestMapping(value="/deleteOnlineBlackList",method = RequestMethod.POST)
	public void deleteOnlineBlackList(String id) {
		String msg = "";
		try{
			if(StringUtils.isNotBlank(id)){
				String[] blackIds = id.split(",");
				for(int i = 0;i < blackIds.length;i++){
					if(StringUtils.isNotBlank(blackIds[i])){
						PedOnlineBlackInfo tmp = (PedOnlineBlackInfo) onlineManageService.load(blackIds[i],PedOnlineBlackInfo.class);
						if(null ==tmp){
							throw new Exception( "关联禁入名单失败");
						}else{
							onlineManageService.txDelete(tmp);
						}
					}
				}
			}
			msg = "删除成功！";
		}catch(Exception e){
			logger.error("删除角色失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除角色失败："+e.getMessage());
		}
		this.sendJSON(msg);
	}
	
	
	/**
	 * 查询在线协议历史信息
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAgreementHistList",method=RequestMethod.POST)
	public void queryOnlineAgreementHistList(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.queryOnlineAgreementHistList(bean,user,page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 查询在线协议历史信息
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="OnlineHistListExpt",method=RequestMethod.POST)
	public void OnlineHistListExpt(HttpServletResponse response,OnlineQueryBean bean){

		ExcelWriter excelWriter = null;
		try {
			User user = this.getCurrentUser();
			List result = new ArrayList();
			String dateTime = DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIME_FORMAT);
			response.setContentType("application/vnd.ms-excel");
		    response.setCharacterEncoding("utf-8");
		    String fileName = URLEncoder.encode("在线协议历史信息", "UTF-8");
		    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
			Map<String, String> mapinfo = new LinkedHashMap();
			
			mapinfo.put("onlineNo", "在线协议编号");
			mapinfo.put("custName", "客户名称");
			mapinfo.put("onlineProtocolTypeDesc", "在线协议类型");
			mapinfo.put("modeContent", "修改内容");
			mapinfo.put("appName", "申请人");
			mapinfo.put("appNo", "申请人工号");
			mapinfo.put("signBranchName", "申请人机构");
			
			excelWriter = EasyExcel.write(response.getOutputStream()).head(ExcelUtil.head(mapinfo)).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("在线协议历史信息").head(ExcelUtil.head(mapinfo)).build();
			Page page = new Page();
			do{
				page.setPageSize(ExcelUtil.PageSize);
				page.setPageIndex(ExcelUtil.pageIndex);
				result = onlineManageService.queryOnlineAgreementHistList(bean,user,page);
				List values1 = ExcelUtil.convertBeanToArray(result, mapinfo);
				excelWriter.write(ExcelUtil.dataList(values1), writeSheet);
				ExcelUtil.pageIndex++;	
			}while(result.size()!=0);
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
		} catch (Exception e) {
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
			logger.error("在线协议历史信息导出失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		/*int[] num = { };
		String[] typeName = { "amount" };
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.queryOnlineAgreementHistList(bean,user,page);
			
			
			List list1 = onlineManageService.findOnlineListHistExpt(list, getPage());
			
			String ColumnNames = "onlineNo,custName,onlineProtocolTypeDesc,modeContent,appName,appNo,signBranchName ";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("onlineNo", "在线协议编号");
			mapinfo.put("custName", "客户名称");
			mapinfo.put("onlineProtocolTypeDesc", "在线协议类型");
			mapinfo.put("modeContent", "修改内容");
			mapinfo.put("appName", "申请人");
			mapinfo.put("appNo", "申请人工号");
			mapinfo.put("signBranchName", "申请人机构");
			

			
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list1, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("在线协议历史信息.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
			
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线协议历史信息导出"+ e.getMessage());
		} */
	}
	
	/**
	 * 查询在线业务综合查询
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineBusiList",method=RequestMethod.POST)
	public void queryOnlineBusiList(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = onlineManageService.queryOnlineBusiList(bean,page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 查询在线银承业务综合查询
	 * @author wss
	 * @date 2021-7-30
	 */
	@RequestMapping(value="queryOnlineAcptList",method=RequestMethod.POST)
	public void queryOnlineAcptList(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.queryOnlineAcptList(bean,user,page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 查询在线银承业务跟踪业务查询数据导出
	 * @author wfj
	 * @date 2021-12-13
	 */
	@RequestMapping(value="findOnlineAcptListExpt",method=RequestMethod.POST)
	public void findOnlineAcptListExpt(HttpServletResponse response,OnlineQueryBean bean){
		
		ExcelWriter excelWriter = null;
		try {
			User user = this.getCurrentUser();
			List result = new ArrayList();
			String dateTime = DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIME_FORMAT);
			response.setContentType("application/vnd.ms-excel");
		    response.setCharacterEncoding("utf-8");
		    String fileName = URLEncoder.encode("在线银承业务信息_"+dateTime, "UTF-8");
		    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
			Map<String, String> mapinfo = new LinkedHashMap();
			
			mapinfo.put("custName", "客户名称");
			mapinfo.put("onlineProtocolTypeDesc", "业务类型");
			mapinfo.put("applyBankNo", "申请人开户行行号");
			mapinfo.put("applyBankName", "申请人开户行行名");
			mapinfo.put("payeeAcct", "申请人账号");
			mapinfo.put("totalAmt", "业务金额");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("onlineAcptNo", "在线银承编号");
			mapinfo.put("contractNo", "在线业务合同号");
			mapinfo.put("depositRatio", "保证金比例(%)");
			mapinfo.put("poolCreditRatio", "票据池额度占用比例(%)");
			mapinfo.put("typeName", "阶段");
			mapinfo.put("dealStatusDesc", "流程状态");
			mapinfo.put("statusDesc", "业务状态");
			mapinfo.put("createTime", "操作日期");
			
			excelWriter = EasyExcel.write(response.getOutputStream()).head(ExcelUtil.head(mapinfo)).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("在线银承业务跟踪查询").head(ExcelUtil.head(mapinfo)).build();
			Page page = new Page();
			do{
				page.setPageSize(ExcelUtil.PageSize);
				page.setPageIndex(ExcelUtil.pageIndex);
				result = onlineManageService.queryOnlineAcptList(bean,user,page);
				List values1 = ExcelUtil.convertBeanToArray(result, mapinfo);
				excelWriter.write(ExcelUtil.dataList(values1), writeSheet);
				ExcelUtil.pageIndex++;	
			}while(result.size()!=0);
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
		} catch (Exception e) {
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
			logger.error("在线银承业务信息导出失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		/*int[] num = { };
		String[] typeName = { "amount" };
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.queryOnlineAcptList(bean,user,page);
			
			List list1 = onlineManageService.findOnlineAcptListExpt(list, getPage());
			
			String ColumnNames = "custName,onlineProtocolTypeDesc,applyBankNo,applyBankName,payeeAcct,totalAmt,bpsNo,onlineAcptNo,contractNo,depositRatio," +
					"poolCreditRatio,type,dealStatusDesc,statusDesc,createTime ";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("custName", "客户名称");
			mapinfo.put("onlineProtocolTypeDesc", "业务类型");
			mapinfo.put("applyBankNo", "申请人开户行行号");
			mapinfo.put("applyBankName", "申请人开户行行名");
			mapinfo.put("payeeAcct", "申请人账号");
			mapinfo.put("totalAmt", "业务金额");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("onlineAcptNo", "在线银承编号");
			mapinfo.put("contractNo", "在线业务合同号");
			mapinfo.put("depositRatio", "保证金比例(%)");
			mapinfo.put("poolCreditRatio", "票据池额度占用比例(%)");
			mapinfo.put("type", "阶段");
			mapinfo.put("dealStatusDesc", "流程状态");
			mapinfo.put("statusDesc", "业务状态");
			mapinfo.put("createTime", "操作日期");
			

			
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list1, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("在线银承业务信息.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
			
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线银承业务信息导出失败"+ e.getMessage());
		} */
	}
	/**
	 * 查询在线流贷业务综合查询
	 * @author wss
	 * @date 2021-7-30
	 */
	@RequestMapping(value="queryOnlineCrdtList",method=RequestMethod.POST)
	public void queryOnlineCrdtList(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.queryOnlineCrdtList(bean,user,page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 在线流贷业务跟踪查询数据导出
	 * @author wfj
	 * @date 2021-12-13
	 */
	@RequestMapping(value="findOnlineCrdtListExpt",method=RequestMethod.POST)
	public void findOnlineCrdtListExpt(HttpServletResponse response,OnlineQueryBean bean){
		ExcelWriter excelWriter = null;
		try {
			User user = this.getCurrentUser();
			List result = new ArrayList();
			String dateTime = DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIME_FORMAT);
			response.setContentType("application/vnd.ms-excel");
		    response.setCharacterEncoding("utf-8");
		    String fileName = URLEncoder.encode("在线流贷业务跟踪查询", "UTF-8");
		    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
			Map<String, String> mapinfo = new LinkedHashMap();
			
			mapinfo.put("custName", "客户名称");
			mapinfo.put("onlineCrdtNo", "在线协议编号");
			mapinfo.put("onlineProtocolTypeDesc", "业务类型");
			mapinfo.put("totalAmt", "业务金额");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("custNo", "客户号");
			mapinfo.put("startDate", "申请日期");
			mapinfo.put("endDate", "到期日期");
			mapinfo.put("contractNo", "在线业务合同号");
			mapinfo.put("loanNo", "在线业务借据号");
			mapinfo.put("transAccount", "贷款账号");
			mapinfo.put("typeName", "阶段");
			mapinfo.put("dealStatusDesc", "流程状态");
			mapinfo.put("statusDesc", "业务状态");
			mapinfo.put("createTime", "操作日期");
			
			excelWriter = EasyExcel.write(response.getOutputStream()).head(ExcelUtil.head(mapinfo)).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("在线流贷业务跟踪查询").head(ExcelUtil.head(mapinfo)).build();
			Page page = new Page();
			do{
				page.setPageSize(ExcelUtil.PageSize);
				page.setPageIndex(ExcelUtil.pageIndex);
				result = onlineManageService.queryOnlineCrdtList(bean,user,page);
				List values1 = ExcelUtil.convertBeanToArray(result, mapinfo);
				excelWriter.write(ExcelUtil.dataList(values1), writeSheet);
				ExcelUtil.pageIndex++;	
			}while(result.size()!=0);
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
		} catch (Exception e) {
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
			logger.error("在线流贷业务跟踪查询导出失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		/*int[] num = { 3 };1
		String[] typeName = { "amount" };
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			List list = onlineManageService.queryOnlineCrdtList(bean,user,page);
			
			List list1 = onlineManageService.findOnlineCrdtListExpt(list, getPage());
			
			String ColumnNames = "custName,onlineCrdtNo,onlineProtocolTypeDesc,totalAmt,bpsNo,custNo,startDate,endDate,contractNo," +
					"loanNo,transAccount,type,dealStatusDesc,statusDesc,createTime ";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("custName", "客户名称");
			mapinfo.put("onlineCrdtNo", "在线协议编号");
			mapinfo.put("onlineProtocolTypeDesc", "业务类型");
			mapinfo.put("totalAmt", "业务金额");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("custNo", "客户号");
			mapinfo.put("startDate", "申请日期");
			mapinfo.put("endDate", "到期日期");
			mapinfo.put("contractNo", "在线业务合同号");
			mapinfo.put("loanNo", "在线业务借据号");
			mapinfo.put("transAccount", "贷款账号");
			mapinfo.put("type", "阶段");
			mapinfo.put("dealStatusDesc", "流程状态");
			mapinfo.put("statusDesc", "业务状态");
			mapinfo.put("createTime", "操作日期");
			

			
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list1, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("在线流贷业务跟踪查询.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
			
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线流贷业务跟踪查询导出失败"+ e.getMessage());
		} */
	}
	
	/**
	 * 查询在线业务短信通知人历史
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineMsgHist",method=RequestMethod.POST)
	public void queryOnlineMsgHist(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			Map map = new HashMap();
			List list = onlineManageService.queryOnlineMsgHist(bean,page);
			if(null != list && list.size()>0){
				map.put("data", list);
				PedOnlineMsgInfoHist lastHist = (PedOnlineMsgInfoHist) list.get(0);
				PedOnlineMsgInfoHist last =new PedOnlineMsgInfoHist();
				if(StringUtils.isNotBlank(lastHist.getLastSourceId())){
					last= (PedOnlineMsgInfoHist) onlineManageService.load(lastHist.getLastSourceId(), PedOnlineMsgInfoHist.class);
				}
				bean.setModeMark(last.getModeMark());
				List lastList = onlineManageService.queryOnlineMsgHist(bean,page);
				map.put("updateData", lastList);
				this.sendJSON(JsonUtil.fromObject(map));
			}else{
				this.sendJSON(RESULT_EMPTY_DEFAULT);
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 在线业务日志-查询
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryErrorLog",method=RequestMethod.POST)
	public void queryErrorLog(OnlineQueryBean bean){
		try {
			String log = onlineManageService.queryHandleLog(bean); 
			if("null".equals(log)){
				log="";
			}
			this.sendJSON(JsonUtil.fromString(log));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 估算给定日期后的到期日
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="calculateDueDate",method=RequestMethod.POST)
	public void calculateDueDate(String validDate,String validDateType,Date startDate){
		try {
			Boolean bo=validDate.matches("[0-9]{1,}");

			if(bo){
				String dueDate = onlineManageService.calculateDueDate(validDate,validDateType,startDate);
				if(null == dueDate){
					this.sendJSON(RESULT_EMPTY_DEFAULT);
				}else{
					this.sendJSON(JsonUtil.fromString(dueDate));
				}
			}else{
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("有效期只允许是数字");
			}
			
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 
	 * @param id
	 * @param type 0:在线银承	1：在线流贷
	 * @param status	false:打开开关	true：关闭开关
	 */
	@RequestMapping("changeOnlineManage")
	public void changeOnlineManage(String id , String type, boolean status){
		try {
			if(type.equals("0")){//在线银承
				PedOnlineAcptProtocol acpt = (PedOnlineAcptProtocol) onlineManageService.load(id,PedOnlineAcptProtocol.class);
				if(status){
					acpt.setYcFlag("0");
				}else{
					acpt.setYcFlag("1");
				}
				onlineManageService.txStore(acpt);
			}else{
				PedOnlineCrdtProtocol crdt = (PedOnlineCrdtProtocol) onlineManageService.load(id,PedOnlineCrdtProtocol.class);
				if(status){
					crdt.setLdFlag("0");
				}else{
					crdt.setLdFlag("1");
				}
				onlineManageService.txStore(crdt);
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线协议开关处理失败"+ e.getMessage());
		}
	}
		
}
