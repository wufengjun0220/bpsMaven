package com.mingtech.application.pool.online.acception.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
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

import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptInfoHist;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocolHist;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptCacheBatch;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;



@Controller
public class PedOnlineAcptController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(PedOnlineAcptController.class);
	
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private DictCommonService dictCommonService;
	
	/**
	 * 查询在线银承协议
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptPtlById",method=RequestMethod.POST)
	public void queryOnlineAcptPtlById(String id){
		try {
			PedOnlineAcptProtocol acptProtocol = (PedOnlineAcptProtocol) pedOnlineAcptService.load(id,PedOnlineAcptProtocol.class);
			this.sendJSON(JsonUtil.fromObject(acptProtocol));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 查询在线协议收票人明细
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptPayeeByAcptId",method=RequestMethod.POST)
	public void queryOnlineAcptPayeeByAcptId(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineAcptService.queryOnlineAcptPayeeListByBean(bean, page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 查询在线银承明细
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptDetailByAcptId",method=RequestMethod.POST)
	public void queryOnlineAcptDetailByAcptId(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineAcptService.queryPlOnlineAcptDetailList(bean, page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	/**
	 * 查询在线银承明细
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptDetailCacheByAcptId",method=RequestMethod.POST)
	public void queryOnlineAcptDetailCacheByAcptId(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineAcptService.queryPlOnlineAcptCacheDetailList(bean, page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	/**
	 * 查询在线银承业务
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptById",method=RequestMethod.POST)
	public void queryOnlineAcptById(String id){
		try {
			PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(id,PlOnlineAcptBatch.class);
			this.sendJSON(JsonUtil.fromObject(batch));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	/**
	 * 查询在线银承历史业务
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptHisById",method=RequestMethod.POST)
	public void queryOnlineAcptHisById(String id){
		try {
			PlOnlineAcptCacheBatch batch = (PlOnlineAcptCacheBatch) pedOnlineAcptService.load(id,PlOnlineAcptCacheBatch.class);
			this.sendJSON(JsonUtil.fromObject(batch));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询协议信息失败"+ e.getMessage());
		} 
	}
	/**
	 * 查询在线银承协议历史
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptPtlHist",method=RequestMethod.POST)
	public void queryOnlineAcptPtlHist(OnlineQueryBean bean){
		try {
			if(StringUtils.isNotBlank(bean.getId())){
				Map map = new HashMap();
				PedOnlineAcptProtocolHist hist = (PedOnlineAcptProtocolHist) pedOnlineAcptService.load(bean.getId(), PedOnlineAcptProtocolHist.class);//本次
				map.put("data", hist);
				if(StringUtils.isNotBlank(hist.getLastSourceId())){
					PedOnlineAcptProtocolHist last = (PedOnlineAcptProtocolHist) pedOnlineAcptService.load(hist.getLastSourceId(), PedOnlineAcptProtocolHist.class);
					last = pedOnlineAcptService.compareDto(hist,last);
					map.put("updateData", last);
				}
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
	 * 查询在线银承收票人修改历史
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineAcptPayeeHistList",method=RequestMethod.POST)
	public void queryOnlineAcptPayeeHistList(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list =  pedOnlineAcptService.queryOnlineAcptPayeeHistListByBean(bean,null);
			if(null != list && list.size()>0){
				Map map = new HashMap();
				List lastList = new ArrayList();
				OnlineQueryBean hist = (OnlineQueryBean) list.get(0);
				if(StringUtils.isNotBlank(hist.getLastSourceId())){
					PedOnlineAcptInfoHist last = (PedOnlineAcptInfoHist) pedOnlineAcptService.load(hist.getLastSourceId(), PedOnlineAcptInfoHist.class);
					bean.setModeMark(last.getModeMark());
					lastList =  pedOnlineAcptService.queryOnlineAcptPayeeHistListByBean(bean,null);
				}
				map.put("updateData", lastList);
				map.put("data", list);
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
	 * 银承明细导出
	 * 
	 */
	@RequestMapping("onlineAcptDetailToExpt")
	public void onlineAcptDetailToExpt(OnlineQueryBean bean) {
		int[] num = {15};
		String[] typeName = { "amount"};
		try {
			List list = pedOnlineAcptService.loadAcptDetailToExpt(bean,this.getPage());
			String ColumnNames = "billNo,loanNo,billAmt,isseDate,dueDate,issuerName,issuerBankName,issuerBankCode,payeeName,payeeAcct," +
					"payeeBankName,payeeBankCode,acptBankCode,acptBankName,transferFlag";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("billNo", "票号");
			mapinfo.put("loanNo", "借据号");
			mapinfo.put("billAmt", "票面金额");
			mapinfo.put("isseDate", "出票日");
			mapinfo.put("dueDate", "到期日");
			mapinfo.put("issuerName", "出票人名称");
			mapinfo.put("issuerBankName", "出票人开户行名");
			mapinfo.put("issuerBankCode", "出票人开户行号");
			mapinfo.put("payeeName", "收票人");
			mapinfo.put("payeeAcct", "收票人账户");
			mapinfo.put("payeeBankName", "收票人开户行名");
			mapinfo.put("payeeBankCode", "收票人开户行号");
			mapinfo.put("acptBankCode", "承兑行行号");
			mapinfo.put("acptBankName", "承兑行行名");
			mapinfo.put("transferFlag", "转让标志");
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("承兑明细清单" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 银承缓存明细导出
	 * 
	 */
	@RequestMapping("onlineAcptCacheDetailToExpt")
	public void onlineAcptCacheDetailToExpt(OnlineQueryBean bean) {
		int[] num = {15};
		String[] typeName = { "amount"};
		try {
			List list = pedOnlineAcptService.loadAcptCacheDetailToExpt(bean,this.getPage());
			String ColumnNames = "billNo,loanNo,billAmt,isseDate,dueDate,issuerName,issuerBankName,issuerBankCode,payeeName,payeeAcct," +
					"payeeBankName,payeeBankCode,acptBankCode,acptBankName,transferFlag";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("billNo", "票号");
			mapinfo.put("loanNo", "借据号");
			mapinfo.put("billAmt", "票面金额");
			mapinfo.put("isseDate", "出票日");
			mapinfo.put("dueDate", "到期日");
			mapinfo.put("issuerName", "出票人名称");
			mapinfo.put("issuerBankName", "出票人开户行名");
			mapinfo.put("issuerBankCode", "出票人开户行号");
			mapinfo.put("payeeName", "收票人");
			mapinfo.put("payeeAcct", "收票人账户");
			mapinfo.put("payeeBankName", "收票人开户行名");
			mapinfo.put("payeeBankCode", "收票人开户行号");
			mapinfo.put("acptBankCode", "承兑行行号");
			mapinfo.put("acptBankName", "承兑行行名");
			mapinfo.put("transferFlag", "转让标志");
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("承兑明细清单" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	
		
}
