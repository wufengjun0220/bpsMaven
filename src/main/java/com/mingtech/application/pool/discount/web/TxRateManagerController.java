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
 *在线贴现利率管理 
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
	 * 指导、优惠、最优惠利率查询接口    默认查询本地数据
	 * 01-指导   02-优惠   03-最优惠
	 * */
	@RequestMapping("/rateManegerQuery")
	public void rateManegerQuery(TxRateMaintainInfo txRateMaintainInfo){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			
			//  查询中台有效利率并更新处理
			if("03".equals(txRateMaintainInfo.getRateType())){	//	查询中台最优惠利率并更新处理
				txRateMaintainInfoService.queryAndUpdateBest(user);
			}else if ("02".equals(txRateMaintainInfo.getRateType())) {	//	查询中台优惠利率并更新处理
				txRateMaintainInfoService.queryFavorRateAndUpdate("",user);
			}else if ("01".equals(txRateMaintainInfo.getRateType())) {	//	查询中台指导利率并更新处理
				txRateMaintainInfoService.queryGuideRateAndUpdate("",user);
			}
			
			
			
			//	获取指导利率
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
			this.sendJSON("查询失败：" + e.getMessage());
		}
	}
	
	/**
	 * 利率历史查询接口    默认查询本地数据
	 * */
	@RequestMapping("/rateHisQuery")
	public void rateHisQuery(TxRateMaintainInfo txRateMaintainInfo){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			
			//  查询中台有效利率并更新处理
			if("03".equals(txRateMaintainInfo.getRateType())){	//	查询中台最优惠利率并更新处理
				txRateMaintainInfoService.queryAndUpdateBest(user);
			}else if ("02".equals(txRateMaintainInfo.getRateType())) {	//	查询中台优惠利率并更新处理
				txRateMaintainInfoService.queryFavorRateAndUpdate("",user);
			}else if ("01".equals(txRateMaintainInfo.getRateType())) {	//	查询中台指导利率并更新处理
				txRateMaintainInfoService.queryGuideRateAndUpdate("",user);
			}
			
			//	获取指导利率
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
			this.sendJSON("查询失败：" + e.getMessage());
		}
	}

	/**
	 * 利率详情接口
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
			this.sendJSON("查询失败：" + e.getMessage());
		}
	}
	
	/**
	 * 利率测算接口
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
			this.sendJSON("查询失败：" + e.getMessage());
		}
	}
	
	/**
	 * 利率删除操作
	 * */
	@RequestMapping("/deleteTxRate")
	public void deleteTxRate(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		try {
			txRateMaintainInfoService.deleteTxRate(txRateMaintainInfo);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除失败：" + e.getMessage());
		}
	}
	
	
	/**
	 * 指导、优惠利率新增和维护
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
			this.sendJSON("在线贴现利率维护失败"+ e.getMessage());
		}
	}
	
	/**
	 * 最优惠利率新增和维护
	 * */
	@RequestMapping(value = "maintainTxBestRate")
	public void maintainTxBestRate(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		try {
			String currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			txRateMaintainInfo.setMaintainTime(currTime);
			if(StringUtil.isStringEmpty(txRateMaintainInfo.getBatchNo())){
				String batchNo = batchNoUtils.txGetBatchNo();
				txRateMaintainInfo.setBatchNo(batchNo);
				txRateMaintainInfo.setEffState("00"); 	//	新建状态
				txRateMaintainInfoService.insertTxRateInfo(txRateMaintainInfo);
			}else{
				txRateMaintainInfoService.updateTxRateInfo(txRateMaintainInfo);
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_996,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("利率数据处理失败"+ e.getMessage());
		}
	}
	
	/**
	 * 利率发送
	 * */
	@RequestMapping("/txRateSend")
	public void  txRateSend(@RequestBody TxRateMaintainInfo txRateMaintainInfo){
		String json = "发送成功!";
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
			this.sendJSON("利率发送失败："+ e.getMessage());;
		}
	}
	
	/**
	 * 利率提交审批
	 * */
	@RequestMapping("/txRateSubmit")
	public void  txRateSubmit(String id){
		//	1、修改待提交的数据状态为已提交
		String json = "提交成功!";
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
			json = "提交审批失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	
	
	/**
	 * 最优惠利率提交审批
	 * */
	@RequestMapping("/txBestFavorRateubmit")
	public void  txBestFavorRateubmit(String id){
		//	1、修改待提交的数据状态为已提交
		String json = "提交成功!";
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
			json = "撤销审批失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * 最优惠利率撤回审批
	 * */
	@RequestMapping("/txCancelBestFavorRateubmit")
	public void  txCancelBestFavorRateubmit(String id){
		//	1、修改待提交的数据状态为已提交
		String json = "提交成功!";
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
			json = "撤销审批失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	
	/**
	 *	利率撤回审批
	 * */	
	@RequestMapping("/cancelRateReBack")
	public void  cancelRateReBack(String id){
		//	1、修改待提交的数据状态为已撤回
		String json = "提交成功!";
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
			json = "撤销审批失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * 票据审价查询
	 * */
	@RequestMapping("/reviewPriceQuery")
	public void reviewPriceQuery(CenterPlatformBean centerPlatformBean){
		//	只查询本地数据
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
			this.sendJSON("票据审价查询失败："+ e.getMessage());
		}
	}

	/**
	 * 票据审价信息导出
	 * */	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/reviewPriceQueryExpt")
	public void reviewPriceQueryExpt(String onlineNo){
		//	只查询本地数据
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
			
				String[] titles = {"贴现定价审批编号","票据号码/票据包号","票据类型","承兑行名","承兑行号",
						"票面金额（元）","已贴现金额（元）","未贴现金额（元）","申请贴现利率（%）",
						"贴现类型","申请日期","生效起始日","生效到期日","状态","经办人","经办人工号","经办机构","终审机构"};

				Map<String, String> mapfileds = new LinkedHashMap<String, String>();
				byte[] buffer = dictCommonService.creatSheetModel(list,titles, mapfileds, num, typeName);
				HttpServletResponse response = this.getResponse();
				OutputStream os = response.getOutputStream();
				response.setContentType("application/octet-stream");
				response.addHeader("Content-Disposition",
						"attachment; filename=" + URLEncoder.encode("在线贴现票据审价报表" + ".xls", "utf-8"));
				os.write(buffer);
				os.flush();
				os.close();
				os = null;
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据审价查询失败："+ e.getMessage());
		}
	}
	
	/**
	 * 票据审价详情查询
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
			this.sendJSON("票据审价详情信息查询失败："+ e.getMessage());
		}
	}
	
	
	/**
	 * 根据客户名称反显
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
	 * 审批界面查询明细信息
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
			this.sendJSON("票据审价详情信息查询失败："+ e.getMessage());
		}
	}
	
	/**
	 * 票据引入查询  
	 * */
	@RequestMapping("/txBillIntroduceQuery")
	public void txBillIntroduceQuery(String billNo){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			/*List<IntroBillInfoBean> lists = new ArrayList<IntroBillInfoBean>();
			IntroBillInfoBean bean = new IntroBillInfoBean("12121212", "000001", "0001000", "20221010", "测试一", "测试行1", "12121000", "收款人1", "收款行1", "7545425", new BigDecimal(1000), "AC01", "20220819", "20221015"
					, null, "出票行1", "45454212", "01", new BigDecimal(1.2), new BigDecimal(1.8), new BigDecimal(1.3), new BigDecimal(1.5), "CS01");
			IntroBillInfoBean bean2 = new IntroBillInfoBean("12121212", "000001", "0001000", "20221010", "测试2", "测试行2", "12121000", "收款人2", "收款行2", "7545425", new BigDecimal(2000), "AC01", "20220819", "20221015"
					, null, "出票行2", "25242684251", "01", new BigDecimal(1.2), new BigDecimal(1.8), new BigDecimal(1.3), new BigDecimal(1.5), "CS01");
			IntroBillInfoBean bean3 = new IntroBillInfoBean("12121212", "000001", "0001000", "20221010", "测试3", "测试行3", "12121000", "收款人3", "收款行3", "7545425", new BigDecimal(3000), "AC01", "20220819", "20221015"
					, null, "出票行3", "2323353535", "01", new BigDecimal(1.2), new BigDecimal(1.8), new BigDecimal(1.3), new BigDecimal(1.5), "CS01");
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
			this.sendJSON("票据引入信息查询失败："+ e.getMessage());
		}
	}
	
	/**
	 * 票据导入信息查询
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
					if(StringUtil.isNotEmpty(beans.get(i).getAcptBankNo())){	//	承兑行号
						//	获取上级行号
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
							
							System.out.println("行别："+beans.get(i).getAcptBankType());
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
					if(StringUtil.isNotEmpty(beans.get(i).getIssuerBankNo())){	//	承兑行号
//						获取上级行号
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
					if(StringUtil.isNotEmpty(beans.get(i).getPayeeBankNo())){	//	承兑行号
//						获取上级行号
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
				results += "票据号：" + result.replaceFirst(",", "") + "已重复";
			}
			
			if(StringUtil.isNotEmpty(result1)){
				results += "承兑行号：" + result1.replaceFirst(",", "") + "查无对应信息";
			}
			
			if(StringUtil.isNotEmpty(result2)){
				results += "出票人开户行号：" + result2.replaceFirst(",", "") + "查无对应信息";
			}
			
			if(StringUtil.isNotEmpty(result3)){
				results += "收款人开户行号：" + result3.replaceFirst(",", "") + "查无对应信息";
			}
			
			if(StringUtil.isNotEmpty(results)){
				this.sendJSON(results);
			}else{
				this.sendJSON(JsonUtil.buildJson(beans,beans.size()));
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据导入信息查询失败："+ e.getMessage());
		}
	}
	
	/**
	 * 票据审价新增、修改 保存操作
	 * */
	@RequestMapping("saveBillOprea")
	public void saveBillOprea(@RequestBody TxReviewPriceInfo info){
		try {
			txRateMaintainInfoService.txSaveBillOprea(info);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据审价保存失败："+ e.getMessage());
		}
	}
	
	/**
	 * 票据审价删除操作
	 * */
	@RequestMapping("deleteBillPrice")
	public void deleteBillPrice(@RequestBody TxReviewPriceInfo info){
		try {
			txRateMaintainInfoService.deleteBillPrice(info);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据审价删除失败："+ e.getMessage());
		}
	}
	
	@RequestMapping("/sendBillPrice")
	public void  sendBillPrice(@RequestBody TxReviewPriceInfo info){
		String res = "发送成功";
		try {
			User user = this.getCurrentUser();
			res = txRateMaintainInfoService.sendBillPrice(info,user);
			this.sendJSON(res);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据审价发送失败："+ e.getMessage());
		}
	}
	
	
	/**
	 * 票据审价-提交审批
	 */
	@RequestMapping("sumbitPriceQuery")
	public void sumbitPriceQuery(String id){
		String json = "提交成功!";
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
			json = "提交审批失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * 票据审价-撤销审批
	 */
	@RequestMapping("cancelPriceQuery")
	public void cancelPriceQuery(String id){
		String json = "撤销成功!";
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
			json = "撤销审批失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * 行别映射关系查询
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping("/bankRoleMappingQuery")
	public void bankRoleMappingQuery(CenterPlatformBean centerPlatformBean){
		//	查询中台行别映射关系
		Page page = this.getPage();
		User user = this.getCurrentUser();
		
		String  json = RESULT_EMPTY_DEFAULT;
		List<BankRoleMappingBean> lists = null;
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			if(StringUtil.isNotEmpty(centerPlatformBean.getIsHis())){
				//		获取上级行号
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
			json = "查询行别映射关系失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * 行别映射关系维护
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
	 * 根据银行类型和期限获取对应利率   queryTxRateByBankAndTerm
	 * */
	@SuppressWarnings("rawtypes")
	@RequestMapping("queryTxRateByBankAndTerm")
	public void queryTxRateByBankAndTerm(@RequestBody IntroBillInfoBean bean){
		String json = RESULT_EMPTY_DEFAULT;
		//		获取利率信息   并赋值
		try {
			User user = this.getCurrentUser();
			TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
			
			if(!StringUtil.isEmpty(bean.getAcptBankType())){
				txRateDetailBean.setBankType(bean.getAcptBankType());
			}else{
				CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
				
				//	获取上级行号
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
			json = "获取对应利率信息出错:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * 获取审批单打印信息
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryApprovePrintInfo")
	public void queryApprovePrintInfo(@RequestBody TxReviewPriceInfo info){
		User user = this.getCurrentUser();
		try {
			TxReviewPriceInfo reviewPriceInfo = txRateMaintainInfoService.queryTxReviewPriceInfo(info,user);
			
			//	获取审批经办人信息
			List<ApproveAuditDto> approveAuditDtos = auditService.getOpenApproveAuditDto("5001001", info.getId());
			reviewPriceInfo.setApproveAuditDtos(approveAuditDtos);
			
			//	获取审批信息
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
			this.sendJSON("票据审价详情信息查询失败："+ e.getMessage());
		}
	}

	/**
	 * 导出引票模板
	 * @return
	 */
	@RequestMapping(value="/exportIntroExcel",method= RequestMethod.POST)
	public ResponseEntity<byte[]> exportIntroExcel(String reportName){
		//读取文件
		try {
			//根据ID查询出对应文件
			RReportModel reportForm = new RReportModel();
			reportForm.setReportName(reportName);
			List lists = reportModelService.queryReportModelList(reportForm, null,null);
			
			RReportModel file = null;
			if(lists.size() > 0){
				file = (RReportModel) lists.get(0);
			}else{
				return null;
			}
			
			String filePath =file.getFilePath(); //相对路径
			String full = filePath+File.separator+file.getReportName();
			File files = new File(full);
			InputStream is = new FileInputStream(files);
			byte[] bytes = new byte[is.available()];
			is.read(bytes);
			//创建请求头
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attchement;filename=" + file.getReportName());
			//设置HTTP响应状态。
			org.springframework.http.HttpStatus statusCode = org.springframework.http.HttpStatus.OK;
			//创建响应实体对象
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
