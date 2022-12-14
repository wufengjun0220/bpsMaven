package com.mingtech.application.pool.discount.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.domain.ApproveDto;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.ecds.common.BatchNoUtils;
import com.mingtech.application.ecds.common.service.ApproveService;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.base.domain.BoCcmsPartyinf;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.discount.domain.BankRoleMappingBean;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.IntroBillInfoBean;
import com.mingtech.application.pool.discount.domain.TxProtocolDetailBean;
import com.mingtech.application.pool.discount.domain.TxRateDetailBean;
import com.mingtech.application.pool.discount.domain.TxRateDetailBeanPO;
import com.mingtech.application.pool.discount.domain.TxRateMaintainInfo;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxOnlineProtocolService;
import com.mingtech.application.pool.discount.service.TxRateMaintainInfoService;
import com.mingtech.application.pool.report.domain.RReportModel;
import com.mingtech.application.pool.report.service.ReportModelService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 *???????????????????????? 
 * */
@Controller
public class TxRateManagerController extends BaseController{
	private static final Logger logger = Logger.getLogger(TxRateManagerController.class);
	@Autowired
	private TxRateMaintainInfoService txRateMaintainInfoService;
	
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	
	@Autowired
	private DictCommonService dictCommonService; 
	
	@Autowired
	private TxOnlineProtocolService txOnlineProtocolService;
	
	@Autowired
	private BatchNoUtils batchNoUtils;
	
	@Autowired
	private ApproveService approveService;
	
	@Autowired
	private AuditService auditService;
	
	@Autowired
	private ReportModelService reportModelService;
	
	@Autowired
	private BlackListManageService blackListManageService ; 
	/**
	 * ?????????????????????????????????????????????    ????????????????????????
	 * 01-??????   02-??????   03-?????????
	 * */
	@RequestMapping("/rateManegerQuery")
	public void rateManegerQuery(TxRateMaintainInfo txRateMaintainInfo){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			
			//  ???????????????????????????????????????
			if("03".equals(txRateMaintainInfo.getRateType())){	//	??????????????????????????????????????????
				txRateMaintainInfoService.queryAndUpdateBest(user);
			}else if ("02".equals(txRateMaintainInfo.getRateType())) {	//	???????????????????????????????????????
				txRateMaintainInfoService.queryFavorRateAndUpdate("",user);
			}else if ("01".equals(txRateMaintainInfo.getRateType())) {	//	???????????????????????????????????????
				txRateMaintainInfoService.queryGuideRateAndUpdate("",user);
			}
			
			
			
			//	??????????????????
			List<TxRateMaintainInfo> lists = txRateMaintainInfoService.getTxRateMaintainInfoJSON("isNotHis",txRateMaintainInfo,page,user);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("results",page.getTotalCount());
			map.put("rows", lists);
			
			String json = JSON.toJSONString(map);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????" + e.getMessage());
		}
	}
	
	/**
	 * ????????????????????????    ????????????????????????
	 * */
	@RequestMapping("/rateHisQuery")
	public void rateHisQuery(TxRateMaintainInfo txRateMaintainInfo){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			
			//  ???????????????????????????????????????
			if("03".equals(txRateMaintainInfo.getRateType())){	//	??????????????????????????????????????????
				txRateMaintainInfoService.queryAndUpdateBest(user);
			}else if ("02".equals(txRateMaintainInfo.getRateType())) {	//	???????????????????????????????????????
				txRateMaintainInfoService.queryFavorRateAndUpdate("",user);
			}else if ("01".equals(txRateMaintainInfo.getRateType())) {	//	???????????????????????????????????????
				txRateMaintainInfoService.queryGuideRateAndUpdate("",user);
			}
			
			//	??????????????????
			List<TxRateMaintainInfo> lists = txRateMaintainInfoService.getTxRateMaintainInfoJSON("isHis",txRateMaintainInfo,page,user);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("results",page.getTotalCount());
			map.put("rows", lists);
			
			String json = JSON.toJSONString(map);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????" + e.getMessage());
		}
	}

	/**
	 * ??????????????????
	 * */
	@RequestMapping("/queryTxRateDetails")
	public void queryTxRateDetails(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		try {
			User user = this.getCurrentUser();
			List<TxRateDetailBeanPO> lists = txRateMaintainInfoService.queryTxRateDetails(txRateMaintainInfo,user);
			txRateMaintainInfo.setRateDetailBeanPOs(lists);
			
			String json = JSON.toJSONString(txRateMaintainInfo);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????" + e.getMessage());
		}
	}
	
	/**
	 * ??????????????????
	 * */
	@RequestMapping("/queryTxRateDetails1")
	public void queryTxRateDetails1(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		try {
			User user = this.getCurrentUser();
			List<TxRateDetailBeanPO> lists = txRateMaintainInfoService.queryTxRateDetails1(txRateMaintainInfo,user);
			txRateMaintainInfo.setRateDetailBeanPOs(lists);
			
			String json = JSON.toJSONString(txRateMaintainInfo);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????" + e.getMessage());
		}
	}
	
	/**
	 * ??????????????????
	 * */
	@RequestMapping("/deleteTxRate")
	public void deleteTxRate(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		try {
			txRateMaintainInfoService.deleteTxRate(txRateMaintainInfo);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????" + e.getMessage());
		}
	}
	
	
	/**
	 * ????????????????????????????????????
	 * */
	@RequestMapping("/maintainTxRate")
	public void  maintainTxRate(@RequestBody List<TxRateDetailBean> list){
		try {
			User user = this.getCurrentUser();
			TxRateMaintainInfo txRateMaintainInfo = new TxRateMaintainInfo();
			String rateType = list.get(0).getRateType();
			txRateMaintainInfo.setRateType(rateType);
			txRateMaintainInfo.setEffTime(list.get(0).getEffTime());
			txRateMaintainInfo.setRateDetailBeans(list);
			txRateMaintainInfo.setHandler(user.getCustName());
			txRateMaintainInfo.setHandlerNo(user.getCustNumber());
			System.out.println(list.get(0).getBatchNo());
			txRateMaintainInfo.setBatchNo(list.get(0).getBatchNo());
			String currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			txRateMaintainInfo.setMaintainTime(currTime);
			txRateMaintainInfoService.maintainTxRate(txRateMaintainInfo);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("??????????????????????????????"+ e.getMessage());
		}
	}
	
	/**
	 * ??????????????????????????????
	 * */
	@RequestMapping(value = "maintainTxBestRate")
	public void maintainTxBestRate(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		try {
			String currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			txRateMaintainInfo.setMaintainTime(currTime);
			if(StringUtil.isStringEmpty(txRateMaintainInfo.getBatchNo())){
				String batchNo = batchNoUtils.txGetBatchNo();
				txRateMaintainInfo.setBatchNo(batchNo);
				txRateMaintainInfo.setEffState("00"); 	//	????????????
				txRateMaintainInfoService.insertTxRateInfo(txRateMaintainInfo);
			}else{
				txRateMaintainInfoService.updateTxRateInfo(txRateMaintainInfo);
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_996,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		}
	}
	
	/**
	 * ????????????
	 * */
	@RequestMapping("/txRateSend")
	public void  txRateSend(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		String json = "????????????!";
		try {
			User user = this.getCurrentUser();
			String result =  txRateMaintainInfoService.checkAndUpdate(txRateMaintainInfo.getId());
			
			if(StringUtil.isNotEmpty(result)){
				json = result;
			}else{
				txRateMaintainInfoService.txRateSend(txRateMaintainInfo,user);
			}
			
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("?????????????????????"+ e.getMessage());;
		}
	}
	
	/**
	 * ??????????????????
	 * */
	@RequestMapping("/txRateSubmit")
	public void  txRateSubmit(String id){
		//	1?????????????????????????????????????????????
		String json = "????????????!";
		try {
			User user = this.getCurrentUser();
			String result =  txRateMaintainInfoService.checkAndUpdate(id);
			
			if(StringUtil.isNotEmpty(result)){
				json = result;
			}else{
				txRateMaintainInfoService.txSubmittxRate(id,user);
			}
			
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	
	
	/**
	 * ???????????????????????????
	 * */
	@RequestMapping("/txBestFavorRateubmit")
	public void  txBestFavorRateubmit(String id){
		//	1?????????????????????????????????????????????
		String json = "????????????!";
		try {
			User user = this.getCurrentUser();
			
			String result =  txRateMaintainInfoService.checkAndUpdate(id);
			
			if(StringUtil.isNotEmpty(result)){
				json = result;
			}else{
				txRateMaintainInfoService.txSubmitBestFavor(id,user);
			}
			
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * ???????????????????????????
	 * */
	@RequestMapping("/txCancelBestFavorRateubmit")
	public void  txCancelBestFavorRateubmit(String id){
		//	1?????????????????????????????????????????????
		String json = "????????????!";
		try {
			User user = this.getCurrentUser();
			
			txRateMaintainInfoService.txCancelBestFavor(id,user);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	
	/**
	 *	??????????????????
	 * */	
	@RequestMapping("/cancelRateReBack")
	public void  cancelRateReBack(String id){
		//	1?????????????????????????????????????????????
		String json = "????????????!";
		try {
			User user = this.getCurrentUser();
			
			txRateMaintainInfoService.txCanceltxRate(id,user);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * ??????????????????
	 * */
	@RequestMapping("/reviewPriceQuery")
	public void reviewPriceQuery(CenterPlatformBean centerPlatformBean){
		//	?????????????????????
		Page page = this.getPage();
		
		List<TxReviewPriceInfo> lists;
		try {
			lists = txRateMaintainInfoService.queryTxReviewPriceInfo(centerPlatformBean, page);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("results",page.getTotalCount());
			map.put("rows", lists);
			
			String json = JSON.toJSONString(map);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????"+ e.getMessage());
		}
	}

	/**
	 * ????????????????????????
	 * */	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/reviewPriceQueryExpt")
	public void reviewPriceQueryExpt(String onlineNo){
		//	?????????????????????
		Page page = this.getPage();
		try {
			CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
			centerPlatformBean.setOnlineNo(onlineNo);
			List<TxReviewPriceInfo> lists = txRateMaintainInfoService.queryTxReviewPriceInfo(centerPlatformBean, page);
			
			int[] num = {5,6,7,8};
			String[] typeName = { "amount","amount", "amount","rate" };
			
			List list = new ArrayList();
			
			for (TxReviewPriceInfo bean : lists) {
				String [] s = new String[18];
				s[0] = bean.getTxReviewPriceBatchNo();
				s[1] = bean.getBillNo();
				s[2] = bean.getBillType();
				s[3] = bean.getAcptBankName();
				s[4] = bean.getAcptBankNo();
				s[5] = bean.getBillAmt().toString();
				s[6] = bean.getUsedAmt().toString();
				s[7] = bean.getAvailableAmt().toString();
				s[8] = bean.getApplyTxRate().toString();
				s[9] = bean.getTxType();
				s[10] = bean.getApplyDate();
				s[11] = bean.getEffDate();
				s[12] = bean.getEffDate();
				s[13] = bean.getApplyState();
				s[14] = bean.getWorkerName();
				s[15] = bean.getWorkerNo();
				s[16] = bean.getWorkerBranch();
				s[17] = bean.getFinalApproveBranch();
				list.add(s);
			}
			
				String[] titles = {"????????????????????????","????????????/????????????","????????????","????????????","????????????",
						"?????????????????????","????????????????????????","????????????????????????","?????????????????????%???",
						"????????????","????????????","???????????????","???????????????","??????","?????????","???????????????","????????????","????????????"};

				Map<String, String> mapfileds = new LinkedHashMap<String, String>();
				byte[] buffer = dictCommonService.creatSheetModel(list,titles, mapfileds, num, typeName);
				HttpServletResponse response = this.getResponse();
				OutputStream os = response.getOutputStream();
				response.setContentType("application/octet-stream");
				response.addHeader("Content-Disposition",
						"attachment; filename=" + URLEncoder.encode("??????????????????????????????" + ".xls", "utf-8"));
				os.write(buffer);
				os.flush();
				os.close();
				os = null;
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????"+ e.getMessage());
		}
	}
	
	/**
	 * ????????????????????????
	 * */
	@RequestMapping("/reviewPriceDetailQuery")
	public void reviewPriceDetailQuery(@RequestBody TxReviewPriceInfo info){
		User user = this.getCurrentUser();
		try {
			TxReviewPriceInfo reviewPriceInfo = txRateMaintainInfoService.queryTxReviewPriceInfo(info,user);
			
			String json = JSON.toJSONString(reviewPriceInfo);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????????????????"+ e.getMessage());
		}
	}
	
	
	/**
	 * ????????????????????????
	 * 	 
	 * * */
	@SuppressWarnings("unchecked")
	@RequestMapping("queryReviewInfoByCustName")
	public void queryReviewInfoByCustName(String custName){
		User user = this.getCurrentUser();
		Page page = this.getPage();
		
		try {
			CenterPlatformBean centerPlatformBean = new CenterPlatformBean(); 
			centerPlatformBean.setCustName(custName);
			
			Map<String, Object> map = txOnlineProtocolService.txQueryOnlineProtocol(centerPlatformBean, page, user);
			
			List<TxProtocolDetailBean> lists = (List<TxProtocolDetailBean>) map.get("rows");
			String json = RESULT_EMPTY_DEFAULT;
			
			for (TxProtocolDetailBean txProtocolDetailBean : lists) {
				if("1".equals(txProtocolDetailBean.getProtocolStatus())){
					json = JSON.toJSONString(txProtocolDetailBean);
				}
			}
//			
//			if(lists.size() > 0){
//				json = JSON.toJSONString(lists.get(0));
//			}
			sendJSON(json);
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * ??????????????????????????????
	 * */
	@RequestMapping("/queryPriceDetailByAudit")
	public void queryPriceDetailByAudit(String id){
		User user = this.getCurrentUser();
		try {
			TxReviewPriceInfo reviewPriceInfo = (TxReviewPriceInfo) txRateMaintainInfoService.load(id,TxReviewPriceInfo.class);
			TxReviewPriceInfo info = txRateMaintainInfoService.queryTxReviewPriceInfo(reviewPriceInfo,user);
			reviewPriceInfo.setTxReviewPriceDetail(info.getTxReviewPriceDetail());
			reviewPriceInfo.setIntroBillInfoBeans(info.getIntroBillInfoBeans());
			
			
			String json = JSON.toJSONString(reviewPriceInfo);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????????????????"+ e.getMessage());
		}
	}
	
	/**
	 * ??????????????????  
	 * */
	@RequestMapping("/txBillIntroduceQuery")
	public void txBillIntroduceQuery(String billNo){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			/*List<IntroBillInfoBean> lists = new ArrayList<IntroBillInfoBean>();
			IntroBillInfoBean bean = new IntroBillInfoBean("12121212", "000001", "0001000", "20221010", "?????????", "?????????1", "12121000", "?????????1", "?????????1", "7545425", new BigDecimal(1000), "AC01", "20220819", "20221015"
					, null, "?????????1", "45454212", "01", new BigDecimal(1.2), new BigDecimal(1.8), new BigDecimal(1.3), new BigDecimal(1.5), "CS01");
			IntroBillInfoBean bean2 = new IntroBillInfoBean("12121212", "000001", "0001000", "20221010", "??????2", "?????????2", "12121000", "?????????2", "?????????2", "7545425", new BigDecimal(2000), "AC01", "20220819", "20221015"
					, null, "?????????2", "25242684251", "01", new BigDecimal(1.2), new BigDecimal(1.8), new BigDecimal(1.3), new BigDecimal(1.5), "CS01");
			IntroBillInfoBean bean3 = new IntroBillInfoBean("12121212", "000001", "0001000", "20221010", "??????3", "?????????3", "12121000", "?????????3", "?????????3", "7545425", new BigDecimal(3000), "AC01", "20220819", "20221015"
					, null, "?????????3", "2323353535", "01", new BigDecimal(1.2), new BigDecimal(1.8), new BigDecimal(1.3), new BigDecimal(1.5), "CS01");
			lists.add(bean);
			lists.add(bean2);
			lists.add(bean3);*/
			String json = RESULT_EMPTY_DEFAULT;
			if(!StringUtil.isEmpty(billNo)){
				
				Map result = txRateMaintainInfoService.txBillIntroduceQuery(billNo,user,page); 
				
				/*Map<String, Object> map = new HashMap<String, Object>();
				map.put("results",lists.size());
				map.put("rows", lists);*/
				
				json = JSON.toJSONString(result);
			}
			
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("?????????????????????????????????"+ e.getMessage());
		}
	}
	
	/**
	 * ????????????????????????
	 * */
	@RequestMapping("/exportBillQuery")
	public void exportBillQuery(@RequestBody List<IntroBillInfoBean> beans){
		Page page = this.getPage();
		User user = this.getCurrentUser();
		
		Set<String> sets = new HashSet<String>();
		String result = "";
		String result1 = "";
		String result2 = "";
		String result3 = "";
		String results = "";
		
		System.out.println(beans.size());
		try {
			for (int i = 0; i < beans.size(); i++) {
				CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
				Map<String, Object> returnMap = new HashMap<String, Object>();
				
				if(sets.add(beans.get(i).getBillNo())){
					if(StringUtil.isNotEmpty(beans.get(i).getAcptBankNo())){	//	????????????
						//	??????????????????
						Map<String, String> remap = blackListManageService.queryCpesMember(beans.get(i).getAcptBankNo());
						if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
							centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
						}else{
							centerPlatformBean.setAcptHeadBankNo(beans.get(i).getAcptBankNo());
						}
						
						returnMap = txRateMaintainInfoService.queryBankRoleMapping(centerPlatformBean, page,user);
						List<BankRoleMappingBean> lists = (List<BankRoleMappingBean>) returnMap.get("result");
						if(lists.size() > 0){
							beans.get(i).setAcptBankType(StringUtil.isNotEmpty(lists.get(0).getActualType())?lists.get(0).getActualType():lists.get(0).getDefaultType());
							TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
							
							System.out.println("?????????"+beans.get(i).getAcptBankType());
							txRateDetailBean.setBankType(beans.get(i).getAcptBankType());
							
							txRateDetailBean.setTerm(DateUtils.getMonth1(DateUtils.getWorkDayDate_yyyyMMdd(),beans.get(i).getDueDate().replaceAll("-", "")));
							IntroBillInfoBean billInfo = txRateMaintainInfoService.queryRateByTermAndType(txRateDetailBean,user);
							beans.get(i).setGuidanceRate(billInfo.getGuidanceRate());
							beans.get(i).setFavorRate(billInfo.getFavorRate());
							beans.get(i).setBestFavorRate(billInfo.getBestFavorRate());
							beans.get(i).setLimitDays(DateUtils.getMonth1(DateUtils.getWorkDayDate_yyyyMMdd(),beans.get(i).getDueDate().replaceAll("-", "")).toString());
							beans.get(i).setDataSource("01");
							beans.get(i).setBillType("AC01");
//							String bankName = blackListManageService.queryPjsAcptName(centerPlatformBean.getAcptHeadBankNo(),"");
							if(StringUtil.isNotEmpty(remap.get("memberName"))){
								beans.get(i).setAcptBankName(remap.get("memberName"));
							}else{
								BoCcmsPartyinf bofNew = blackListManageService.queryByPrcptcdNo(beans.get(i).getAcptBankNo());
								if(bofNew != null){
									beans.get(i).setAcptBankName(bofNew.getPtcptnm());
								}
							}
							
							if(StringUtil.isEmpty(beans.get(i).getAcptBankName())){
								result1 += "," + beans.get(i).getAcptBankNo();
							}
						}else{
							result1 += "," + beans.get(i).getAcptBankNo();
						}
					}
					if(StringUtil.isNotEmpty(beans.get(i).getIssuerBankNo())){	//	????????????
//						??????????????????
						Map<String, String> remap = blackListManageService.queryCpesMember(beans.get(i).getIssuerBankNo());
						if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
							centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
						}else{
							centerPlatformBean.setAcptHeadBankNo(beans.get(i).getIssuerBankNo());
						}
//						returnMap = txRateMaintainInfoService.queryBankRoleMapping(centerPlatformBean, page,user);
//						List<BankRoleMappingBean> lists = (List<BankRoleMappingBean>) returnMap.get("result");
//						if(lists.size() > 0){
//							String bankName = blackListManageService.queryPjsAcptName(centerPlatformBean.getAcptHeadBankNo(),"");
						if(StringUtil.isNotEmpty(remap.get("memberName"))){
							beans.get(i).setIssuerBankName(remap.get("memberName"));
						}else{
							BoCcmsPartyinf bofNew = blackListManageService.queryByPrcptcdNo(beans.get(i).getIssuerBankNo());
							if(bofNew != null){
								beans.get(i).setIssuerBankName(bofNew.getPtcptnm());
							}
						}
						
						if(StringUtil.isEmpty(beans.get(i).getIssuerBankName())){
							result1 += "," + beans.get(i).getIssuerBankNo();
						}
					}
//					}
					if(StringUtil.isNotEmpty(beans.get(i).getPayeeBankNo())){	//	????????????
//						??????????????????
						Map<String, String> remap = blackListManageService.queryCpesMember(beans.get(i).getPayeeBankNo());
						if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
							centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
						}else{
							centerPlatformBean.setAcptHeadBankNo(beans.get(i).getPayeeBankNo());
						}
//						returnMap = txRateMaintainInfoService.queryBankRoleMapping(centerPlatformBean, page,user);
//						List<BankRoleMappingBean> lists = (List<BankRoleMappingBean>) returnMap.get("result");
//						if(lists.size() > 0){
//							String bankName = blackListManageService.queryPjsAcptName(centerPlatformBean.getAcptHeadBankNo(),"");
						if(StringUtil.isNotEmpty(remap.get("memberName"))){
							beans.get(i).setPayeeBankName(remap.get("memberName"));
						}else{
							BoCcmsPartyinf bofNew = blackListManageService.queryByPrcptcdNo(beans.get(i).getPayeeBankNo());
							if(bofNew != null){
								beans.get(i).setPayeeBankName(bofNew.getPtcptnm());
							}
						}
//						}
						if(StringUtil.isEmpty(beans.get(i).getPayeeBankName())){
							result1 += "," + beans.get(i).getPayeeBankNo();
						}
					}
				}else{
					result += "," + beans.get(i).getBillNo();
				}
			}	
			
			if(StringUtil.isNotEmpty(result)){
				results += "????????????" + result.replaceFirst(",", "") + "?????????";
			}
			
			if(StringUtil.isNotEmpty(result1)){
				results += "???????????????" + result1.replaceFirst(",", "") + "??????????????????";
			}
			
			if(StringUtil.isNotEmpty(result2)){
				results += "????????????????????????" + result2.replaceFirst(",", "") + "??????????????????";
			}
			
			if(StringUtil.isNotEmpty(result3)){
				results += "????????????????????????" + result3.replaceFirst(",", "") + "??????????????????";
			}
			
			if(StringUtil.isNotEmpty(results)){
				this.sendJSON(results);
			}else{
				this.sendJSON(JsonUtil.buildJson(beans,beans.size()));
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("?????????????????????????????????"+ e.getMessage());
		}
	}
	
	/**
	 * ??????????????????????????? ????????????
	 * */
	@RequestMapping("saveBillOprea")
	public void saveBillOprea(@RequestBody TxReviewPriceInfo info){
		try {
			txRateMaintainInfoService.txSaveBillOprea(info);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????"+ e.getMessage());
		}
	}
	
	/**
	 * ????????????????????????
	 * */
	@RequestMapping("deleteBillPrice")
	public void deleteBillPrice(@RequestBody TxReviewPriceInfo info){
		try {
			txRateMaintainInfoService.deleteBillPrice(info);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????"+ e.getMessage());
		}
	}
	
	@RequestMapping("/sendBillPrice")
	public void  sendBillPrice(@RequestBody TxReviewPriceInfo info){
		String res = "????????????";
		try {
			User user = this.getCurrentUser();
			res = txRateMaintainInfoService.sendBillPrice(info,user);
			this.sendJSON(res);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????"+ e.getMessage());
		}
	}
	
	
	/**
	 * ????????????-????????????
	 */
	@RequestMapping("sumbitPriceQuery")
	public void sumbitPriceQuery(String id){
		String json = "????????????!";
		try {
			User user = this.getCurrentUser();
			
			String reslut = txRateMaintainInfoService.checkBillInfo(id);
			if(StringUtil.isNotEmpty(reslut)){
				json = reslut;
			}else{
				txRateMaintainInfoService.txSubmitPriceQuery(id,user);
			}
			
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * ????????????-????????????
	 */
	@RequestMapping("cancelPriceQuery")
	public void cancelPriceQuery(String id){
		String json = "????????????!";
		try {
			User user = this.getCurrentUser();
			
			txRateMaintainInfoService.txCancelPriceQuery(id,user);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * ????????????????????????
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping("/bankRoleMappingQuery")
	public void bankRoleMappingQuery(CenterPlatformBean centerPlatformBean){
		//	??????????????????????????????
		Page page = this.getPage();
		User user = this.getCurrentUser();
		
		String  json = RESULT_EMPTY_DEFAULT;
		List<BankRoleMappingBean> lists = null;
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			if(StringUtil.isNotEmpty(centerPlatformBean.getIsHis())){
				//		??????????????????
				Map<String, String> remap = blackListManageService.queryCpesMember(centerPlatformBean.getAcptHeadBankNo());
				if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
					centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
				}
				
				returnMap = txRateMaintainInfoService.queryBankRoleMapping(centerPlatformBean, page,user);
				lists = (List<BankRoleMappingBean>) returnMap.get("result");
				if(lists.size() > 0){
//					String bankName = blackListManageService.queryPjsAcptName(centerPlatformBean.getAcptHeadBankNo(),"");
					if(StringUtil.isNotEmpty(remap.get("memberName"))){
						lists.get(0).setAcptHeadBankName(remap.get("memberName"));
					}else{
						BoCcmsPartyinf bofNew = blackListManageService.queryByPrcptcdNo(centerPlatformBean.getAcptHeadBankNo());
						if(bofNew != null){
							lists.get(0).setAcptHeadBankName(bofNew.getPtcptnm());
						}
					}
				}
			}else{
				returnMap = txRateMaintainInfoService.queryBankRoleMapping(centerPlatformBean, page,user);
				lists = (List<BankRoleMappingBean>) returnMap.get("result");
			}
			
			if (lists.size() > 0) {
				json = JsonUtil.buildJson(lists, Long.parseLong(StringUtil.getStringVal(returnMap.get("total"))));
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * ????????????????????????
	 * */
	@RequestMapping("/bankRoleMappingSave")
	@ResponseBody
	public void bankRoleMappingSave(@RequestBody String bankRoleMapping){
		BankRoleMappingBean bankRoleMappingBean = JSON.parseObject(bankRoleMapping, BankRoleMappingBean.class);
		try {
			User user = this.getCurrentUser();
			centerPlatformSysService.txBankRoleMappingSave(bankRoleMappingBean,user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List tranferRateDetailList(List<TxRateDetailBeanPO> lists) {
		List<String[]> excelLists = new ArrayList<String[]>();
		for (TxRateDetailBeanPO bean : lists) {
			String [] s = new String[11];
			s[0] = bean.getTerm();
			s[1] = bean.getOwnBank().toString();
			s[2] = bean.getStateShares().toString();
			s[3] = bean.getSharesSys().toString();
			s[4] = bean.getCityBank().toString();
			s[5] = bean.getAgriCommBank().toString();
			s[6] = bean.getType6().toString();
			s[7] = bean.getType7().toString();
			s[8] = bean.getType8().toString();
			s[9] = bean.getType9().toString();
			s[10] = bean.getType10().toString();
			excelLists.add(s);
		}
		return excelLists;
	}
	
	/**
	 * ?????????????????????????????????????????????   queryTxRateByBankAndTerm
	 * */
	@SuppressWarnings("rawtypes")
	@RequestMapping("queryTxRateByBankAndTerm")
	public void queryTxRateByBankAndTerm(@RequestBody IntroBillInfoBean bean){
		String json = RESULT_EMPTY_DEFAULT;
		//		??????????????????   ?????????
		try {
			User user = this.getCurrentUser();
			TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
			
			if(!StringUtil.isEmpty(bean.getAcptBankType())){
				txRateDetailBean.setBankType(bean.getAcptBankType());
			}else{
				CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
				
				//	??????????????????
				Map<String, String> remap = blackListManageService.queryCpesMember(bean.getAcptBankNo());
				if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
					centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
				}else{
					centerPlatformBean.setAcptHeadBankNo(bean.getAcptBankNo());
				}
				
				centerPlatformBean.setPageSize(1);
				centerPlatformBean.setPageNum(10);
				ReturnMessageNew messageNew = centerPlatformSysService.txQueryBankRoleMapping(centerPlatformBean,user);
				List list2 = messageNew.getDetails();
				if(list2.size() > 0){
					Map map2 = (Map) list2.get(0); 
					if(!StringUtil.isEmpty(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")))){
						txRateDetailBean.setBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")));
					}else{
						txRateDetailBean.setBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.DEFAULT_BANK_TYPE")));
					}
				}
			}
			
			txRateDetailBean.setTerm(DateUtils.getMonth1(DateUtils.getWorkDayDate_yyyyMMdd(),bean.getDueDate().replaceAll("-", "")));
			IntroBillInfoBean billInfo = txRateMaintainInfoService.queryRateByTermAndType(txRateDetailBean,user);
			billInfo.setLimitDays(DateUtils.getMonth1(DateUtils.getWorkDayDate_yyyyMMdd(),bean.getDueDate().replaceAll("-", "")).toString());
			if (billInfo != null) {
				json = JSON.toJSONString(billInfo);
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "??????????????????????????????:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * ???????????????????????????
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryApprovePrintInfo")
	public void queryApprovePrintInfo(@RequestBody TxReviewPriceInfo info){
		User user = this.getCurrentUser();
		try {
			TxReviewPriceInfo reviewPriceInfo = txRateMaintainInfoService.queryTxReviewPriceInfo(info,user);
			
			//	???????????????????????????
			List<ApproveAuditDto> approveAuditDtos = auditService.getOpenApproveAuditDto("5001001", info.getId());
			reviewPriceInfo.setApproveAuditDtos(approveAuditDtos);
			
			//	??????????????????
			List<ApproveDto> approveDtos = approveService.queryApproveList1(info.getId(), null);
			reviewPriceInfo.setApproveDtos(approveDtos);
			
			String json = JSON.toJSONString(reviewPriceInfo);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("???????????????????????????????????????"+ e.getMessage());
		}
	}

	/**
	 * ??????????????????
	 * @return
	 */
	@RequestMapping(value="/exportIntroExcel",method= RequestMethod.POST)
	public ResponseEntity<byte[]> exportIntroExcel(String reportName){
		//????????????
		try {
			//??????ID?????????????????????
			RReportModel reportForm = new RReportModel();
			reportForm.setReportName(reportName);
			List lists = reportModelService.queryReportModelList(reportForm, null,null);
			
			RReportModel file = null;
			if(lists.size() > 0){
				file = (RReportModel) lists.get(0);
			}else{
				return null;
			}
			
			String filePath =file.getFilePath(); //????????????
			String full = filePath+File.separator+file.getReportName();
			File files = new File(full);
			InputStream is = new FileInputStream(files);
			byte[] bytes = new byte[is.available()];
			is.read(bytes);
			//???????????????
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attchement;filename=" + file.getReportName());
			//??????HTTP???????????????
			org.springframework.http.HttpStatus statusCode = org.springframework.http.HttpStatus.OK;
			//????????????????????????
			ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(bytes, headers, statusCode);
			return entity;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
}
