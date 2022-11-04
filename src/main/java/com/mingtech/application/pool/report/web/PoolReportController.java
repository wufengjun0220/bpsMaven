package com.mingtech.application.pool.report.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.web.PedProtocolController;
import com.mingtech.application.pool.report.domain.RCreditReportInfoBean;
import com.mingtech.application.pool.report.domain.RPoolReportInfo;
import com.mingtech.application.pool.report.service.PoolReportService;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
@Controller
public class PoolReportController extends BaseController {
	private static final Logger logger = Logger.getLogger(PedProtocolController.class);
	@Autowired
	private PoolReportService poolReportService;
	@Autowired
	private DictCommonService dictCommonService;
	/**
	 * <p>
	 * 方法名称: list|描述: 交易列表查询
	 * </p>
	 */
	@RequestMapping("/reportQuery")
	public void reportQuery(ReportForm reportForm) {
		try {
			String json = poolReportService.queryReportJSON(reportForm, this.getPage(),null);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
	}
	
	@RequestMapping("/viewReport")
	public String viewReport(String id, Model mode) {
		try {
			if (StringUtil.isNotEmpty(id)) {
				ReportForm report = (ReportForm) poolReportService.load(id);
				if (report != null) {
					mode.addAttribute("report", report);
					mode.addAttribute("formContentString", "<xmp>"+report.getFormData()+"</xmp>");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
		return "/pool/transLog/viewTransLog";
	}
	@RequestMapping("/reportPoolQuery1")
	public void reportPoolQuery1(RPoolReportInfo rPoolReportInfo, String beginDate, String endDate) {
		String flag ="1"; //协议报表
		this.reportPoolQuery(rPoolReportInfo, beginDate, endDate,flag);
	}
	@RequestMapping("/reportPoolQuery2")
	public void reportPoolQuery2(RPoolReportInfo rPoolReportInfo, String beginDate, String endDate) {
		String flag ="2"; //票据报表
		this.reportPoolQuery(rPoolReportInfo, beginDate, endDate,flag);
	}
	@RequestMapping("/reportPoolQuery3")
	public void reportPoolQuery3(RPoolReportInfo rPoolReportInfo, String beginDate, String endDate) {
		String flag ="3"; //保证金报表
		this.reportPoolQuery(rPoolReportInfo, beginDate, endDate,flag);
	}
	
	/**
	 * <p>
	 * 方法名称: list|描述: 票据池报表
	 * </p>
	 */
	@RequestMapping("/reportPoolQuery")
	public void reportPoolQuery(RPoolReportInfo rPoolReportInfo, String beginDate, String endDate,String flag) {
		if (null == beginDate || beginDate.trim().length() == 0) {
			Date dNow = new Date();   //当前时间
			Date dBefore = new Date();

			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(dNow);//把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
			dBefore = calendar.getTime();   //得到前一天的时间
			beginDate = DateUtils.toString(dBefore, DateUtils.ORA_DATES_FORMAT);

		}
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		String json = "";
		Page page = this.getPage();
		try {
			List list = poolReportService.queryRPoolReportInfoJSON(rPoolReportInfo, DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT),page,null,flag);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
	}
	
	/**
	 * <p>
	 * 方法名称: list|描述: 票据池融资业务报表
	 * </p>
	 */
	@RequestMapping("/reportFinanceQuery")
	public void reportFinanceQuery(RCreditReportInfoBean rCreditReportInfo, String beginDate, String endDate) {
		if (null == beginDate || beginDate.trim().length() == 0) {
			Date dNow = new Date();   //当前时间
			Date dBefore = new Date();

			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(dNow);//把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
			dBefore = calendar.getTime();   //得到前一天的时间
			beginDate = DateUtils.toString(dBefore, DateUtils.ORA_DATES_FORMAT);
		}
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		String json = "";
		Page page = this.getPage();
		try {
			/*String json = poolReportService.queryreportFinanceJSON(rCreditReportInfo, DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT), this.getPage(),null);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}*/
			List list =poolReportService.queryreportFinanceJSON(rCreditReportInfo, DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT), page,null);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
	}
	
	/**
	 * 票据池签约数量报表导出
	 * 
	 */
	@RequestMapping("reportPoolQueryExpt")
	public void reportPoolQueryExpt(RPoolReportInfo bean, String beginDate) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {10};
		String[] typeName = { "amount","amount", "amount" };
		List list1 = null;
		Page page = this.getPage();
		if (null == beginDate || beginDate.trim().length() == 0) {
			Date dNow = new Date();   //当前时间
			Date dBefore = new Date();

			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(dNow);//把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
			dBefore = calendar.getTime();   //得到前一天的时间
			beginDate = DateUtils.toString(dBefore, DateUtils.ORA_DATES_FORMAT);
		}
		String endDate = null;
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		try {
			/*if (!StringUtil.isEmpty(ids)) {
				String[] id = ids.split(",");
				RPoolReportInfo rPoolReportInfo = null;
				for(int i=0;i<id.length;i++) {
					rPoolReportInfo = poolReportService.queryReportPoolById(id[i]);
					list1.add(rPoolReportInfo);
				}
			}*/
			list1 = poolReportService.queryRPoolReportInfoJSON(bean, DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT),null,null,"1");
//			list1 = poolReportService.queryreportPoolByids(ids);
			list = poolReportService.findReportPoolByBeanExpt(list1, getPage());

			String ColumnNames = "branchName,createDate,proCompYestNum,proCompYestRatio,proCompLastMNum,proCompLastMRatio,"
					+ "proCompLastYNum,proCompLastYRatio,proRank";//,createDate,draftCompYestNum,draftCompYestRatio,draftCompLastMNum,draftCompLastMRatio,"
					//+ "draftCompLastYNum,draftCompLastYRatio,draftRank,createDate,marginCompYestNum,marginCompYestRatio,marginCompLastMNum,marginCompLastMRatio," 
					//+ "marginCompLastYNum,marginCompLastYRatio,marginRank
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("branchName", "机构名称");
			mapinfo.put("createDate", "统计日期");
			mapinfo.put("proCompYestNum", "较上日增/减值");
			mapinfo.put("proCompYestRatio", "较上日增/减比例");
			mapinfo.put("proCompLastMNum", "较上月末增/减值");
			mapinfo.put("proCompLastMRatio", "较上月末增/减比例");
			mapinfo.put("proCompLastYNum", "较上年增/减值");
			mapinfo.put("proCompLastYRatio", "较上年增/减比例");
			mapinfo.put("proRank", "全行排名");
			
/*//			mapinfo.put("createDate", "统计日期");
			mapinfo.put("draftCompYestNum", "入池票据较上日增减值");
			mapinfo.put("draftCompYestRatio", "入池票据较上日增减比例");
			mapinfo.put("draftCompLastMNum", "入池票据较上月末增减值");
			mapinfo.put("draftCompLastMRatio", "入池票据较上月末增减比率");
			mapinfo.put("draftCompLastYNum", "入池票据较上年增减值");
			mapinfo.put("draftCompLastYRatio", "入池票据较上年增减比率");
			mapinfo.put("draftRank", "全行排名");
			
//			mapinfo.put("createDate", "统计日期");
			mapinfo.put("marginCompYestNum", "保证金较上日增减值");
			mapinfo.put("marginCompYestRatio", "保证金较上日增减比例");
			mapinfo.put("marginCompLastMNum", "保证金较上月末增减值");
			mapinfo.put("marginCompLastMRatio", "保证金较上月末增减比率");
			mapinfo.put("marginCompLastYNum", "保证金较上年增减值");
			mapinfo.put("marginCompLastYRatio", "保证金较上年增减比率");
			mapinfo.put("marginRank", "全行排名");*/

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("票据池签约数量报表" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 入池票据金额余额报表导出
	 * 
	 */
	@RequestMapping("reportPoolQueryAmtExpt")
	public void reportPoolQueryAmtExpt(RPoolReportInfo bean, String beginDate) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {9};
		String[] typeName = { "amount","amount", "amount" };
		List list1 = null;
		Page page = this.getPage();
		if (null == beginDate || beginDate.trim().length() == 0) {
			Date dNow = new Date();   //当前时间
			Date dBefore = new Date();

			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(dNow);//把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
			dBefore = calendar.getTime();   //得到前一天的时间
			beginDate = DateUtils.toString(dBefore, DateUtils.ORA_DATES_FORMAT);
		}
		String endDate = null;
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		try {
			/*if (!StringUtil.isEmpty(ids)) {
				String[] id = ids.split(",");
				RPoolReportInfo rPoolReportInfo = null;
				for(int i=0;i<id.length;i++) {
					rPoolReportInfo = poolReportService.queryReportPoolById(id[i]);
					list1.add(rPoolReportInfo);
				}
			}*/
			list1 = poolReportService.queryRPoolReportInfoJSON(bean, DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT),null,null,"2");
//			list1 = poolReportService.queryreportPoolByids(ids);
			list = poolReportService.findReportPoolByBeanAmtExpt(list1, getPage());

			String ColumnNames = "branchName,createDate,draftCompYestNum,draftCompYestRatio,draftCompLastMNum,draftCompLastMRatio,"
					+ "draftCompLastYNum,draftCompLastYRatio,draftRank";//,createDate,draftCompYestNum,draftCompYestRatio,draftCompLastMNum,draftCompLastMRatio,"
					//+ "draftCompLastYNum,draftCompLastYRatio,draftRank,createDate,marginCompYestNum,marginCompYestRatio,marginCompLastMNum,marginCompLastMRatio," 
					//+ "marginCompLastYNum,marginCompLastYRatio,marginRank
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("branchName", "机构名称");
			mapinfo.put("createDate", "统计日期");
			mapinfo.put("draftCompYestNum", "较上日增/减值");
			mapinfo.put("draftCompYestRatio", "较上日增/减比例");
			mapinfo.put("draftCompLastMNum", "较上月末增/减值");
			mapinfo.put("draftCompLastMRatio", "较上月末增/减比率");
			mapinfo.put("draftCompLastYNum", "较上年增/减值");
			mapinfo.put("draftCompLastYRatio", "较上年增/减比率");
			mapinfo.put("draftRank", "全行排名");
			
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("入池票据余额报表" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 入池保证金余额报表导出
	 * 
	 */
	@RequestMapping("reportPoolQueryBoExpt")
	public void reportPoolQueryBoExpt(RPoolReportInfo bean, String beginDate) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {9};
		String[] typeName = { "amount","amount", "amount" };
		List list1 = null;
		Page page = this.getPage();
		if (null == beginDate || beginDate.trim().length() == 0) {
			Date dNow = new Date();   //当前时间
			Date dBefore = new Date();

			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(dNow);//把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
			dBefore = calendar.getTime();   //得到前一天的时间
			beginDate = DateUtils.toString(dBefore, DateUtils.ORA_DATES_FORMAT);
		}
		String endDate = null;
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		try {
			/*if (!StringUtil.isEmpty(ids)) {
				String[] id = ids.split(",");
				RPoolReportInfo rPoolReportInfo = null;
				for(int i=0;i<id.length;i++) {
					rPoolReportInfo = poolReportService.queryReportPoolById(id[i]);
					list1.add(rPoolReportInfo);
				}
			}*/
			list1 = poolReportService.queryRPoolReportInfoJSON(bean, DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT),null,null,"3");
//			list1 = poolReportService.queryreportPoolByids(ids);
			list = poolReportService.findReportPoolByBeanBoExpt(list1, getPage());

			String ColumnNames = "branchName,createDate,marginCompYestNum,marginCompYestRatio,marginCompLastMNum,marginCompLastMRatio,"
					+ "marginCompLastYNum,marginCompLastYRatio,marginRank";//,createDate,draftCompYestNum,draftCompYestRatio,draftCompLastMNum,draftCompLastMRatio,"
					//+ "draftCompLastYNum,draftCompLastYRatio,draftRank,createDate,marginCompYestNum,marginCompYestRatio,marginCompLastMNum,marginCompLastMRatio," 
					//+ "marginCompLastYNum,marginCompLastYRatio,marginRank
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("branchName", "机构名称");
			mapinfo.put("createDate", "统计日期");
			mapinfo.put("marginCompYestNum", "较上日增/减值");
			mapinfo.put("marginCompYestRatio", "较上日增/减比例");
			mapinfo.put("marginCompLastMNum", "较上月末增/减值");
			mapinfo.put("marginCompLastMRatio", "较上月末增/减比率");
			mapinfo.put("marginCompLastYNum", "较上年增/减值");
			mapinfo.put("marginCompLastYRatio", "较上年增/减比率");
			mapinfo.put("marginRank", "全行排名");
			
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("入池保证金余额报表" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 融资池报表导出
	 * 
	 */
	@RequestMapping("reportFinanceQueryExpt")
	public void reportFinanceQueryExpt(RCreditReportInfoBean rCreditReportInfo, String beginDate) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {10};
		String[] typeName = { "amount","amount", "amount" };
		List list1 = null;
		if (null == beginDate || beginDate.trim().length() == 0) {
			Date dNow = new Date();   //当前时间
			Date dBefore = new Date();

			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(dNow);//把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
			dBefore = calendar.getTime();   //得到前一天的时间
			beginDate = DateUtils.toString(dBefore, DateUtils.ORA_DATES_FORMAT);
		}
		String endDate = null;
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		try {
			list1 = poolReportService.queryreportFinanceJSON(rCreditReportInfo, DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT), null,null);
//			list1 = poolReportService.queryreportPoolByids(ids);
			list = poolReportService.findRCreditReportExpt(list1, getPage());

			String ColumnNames = "branchName,busiType,createDate,yestNum,yestRatio,lastMNum,"
					+ "lastMRatio,lastYNum,lastYRatio,bankRank";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("branchName", "机构名称");
			mapinfo.put("busiType", "业务类型");
			mapinfo.put("createDate", "统计日期");
			mapinfo.put("yestNum", "协议较上日增减值");
			mapinfo.put("yestRatio", "协议较上日增减比例");
			mapinfo.put("lastMNum", "协议较上月末增减值");
			mapinfo.put("lastMRatio", "协议较上月末增减比例");
			mapinfo.put("lastYNum", "协议较上年增减值");
			mapinfo.put("lastYRatio", "协议较上年增减比例");
			mapinfo.put("bankRank", "全行排名");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("票据池融资业务报表" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 生成JSON
	 * @param page
	 * @param list
	 * @return
	 * @throws Exception
	 */
	protected String toJson(Page page,List list) throws Exception{
		
		Map jsonMap = new HashMap();
		jsonMap.put("totalProperty", "results," + page.getTotalCount());
		jsonMap.put("root", "rows");
		String json= JsonUtil.fromCollections(list, jsonMap);
		if(!(StringUtil.isNotBlank(json))){
			json = RESULT_EMPTY_DEFAULT;
		}
		
		return json;
	}
}
