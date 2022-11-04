package com.mingtech.application.pool.query.web;


import java.io.OutputStream;
import java.math.BigDecimal;
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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.domain.HolidayDto;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.ecds.common.service.HolidayService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.GuarantDiscMapping;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.query.domain.AcptCheckSublist;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.common.util.UUID;
import com.mingtech.framework.core.page.Page;

/**
 * 通用查询处理器
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 * @copyright 北明明润（北京）科技有限责任公司
 */

@Controller
public class CommonQueryController extends BaseController {
	private static final Logger logger = Logger.getLogger(CommonQueryController.class);
	@Autowired
	private CommonQueryService commonQueryService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private DictCommonService dictCommonService;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private CacheUpdateService cacheUpdateService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private BlackListManageService blackListManageService;
	/**
	 * 查询在线业务禁入名单
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210607
	 */
	@RequestMapping("/loadDebarJSON")
	public void loadDebarJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			if(commonQueryBean.getBusiType().equals("0")){
				//查询
				json = commonQueryService.loadDebarJSON(commonQueryBean, null, this.getPage());
			}else{
				//管理
				json = commonQueryService.loadDebarJSON(commonQueryBean, this.getCurrentUser(), this.getPage());
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("查询在线业务禁入名单失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询在线业务禁入名单失败："+e.getMessage());
		}
		
	}

	
	/**
	 * 查询承兑行名单 查询
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210607
	 */
	@RequestMapping("/loadCommercialJSON")
	public void loadCommercialJSON(ProtocolQueryBean pedProtocolDto) {
		
		String json = "";
		try {
			if(pedProtocolDto.getContractType().equals("0")){
				//查询
				json = blackListManageService.loadGuarantDiscMappingList(pedProtocolDto, null, this.getPage());
			}else{
				//管理
				json = blackListManageService.loadGuarantDiscMappingList(pedProtocolDto, this.getCurrentUser(), this.getPage());
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("查询承兑行名单失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询承兑行名单失败："+e.getMessage());
		}
		
		
	}
	
	/**
	 * 映射关系生效失效操作
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210607
	 */
	@RequestMapping("/changeGuarantStatus")
	public void changeGuarantStatus(String id) {
		
		String json = "";
		try {
			GuarantDiscMapping grarant = (GuarantDiscMapping) blackListManageService.load(id,GuarantDiscMapping.class);
			
			
			if(grarant.getStatus().equals("2") ){
				if(grarant.getCheckType().equals("2")){//商票生效操作
					GuarantDiscMapping bean = new GuarantDiscMapping();
					bean.setAcceptor(grarant.getAcceptor().trim());
					bean.setAcptAcctNo(grarant.getAcptAcctNo());//承兑人账号
					bean.setAcptBankCode(grarant.getAcptBankCode());//承兑行行号
					bean.setCheckType(grarant.getCheckType());
					bean.setId(grarant.getId());
					bean.setStatus("1");//生效
					List list = blackListManageService.queryChangeAcceptorMapping(bean, null, null);
					if(list != null && list.size() > 0){
					 	this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
					 	this.sendJSON("映射关系生效失效操作失败：存在生效的商票映射!");
					 	return;//存在生效的商票映射不可修改
					  }
				}else{
					 Map map = blackListManageService.queryCpesMember(grarant.getAcptBankCode());//
					  String bankCode = (String) map.get("totalBankNo");//总行行号
					  List<String> banks = blackListManageService.queryAllBranchBank(bankCode);//总行及下的分行
					  
					  GuarantDiscMapping bean = new GuarantDiscMapping();
//					  bean.setAcptBankCode(bankCode);
					  bean.setCheckType(grarant.getCheckType());
					  bean.setId(grarant.getId());
					  bean.setStatus("1");//生效
					  List list = blackListManageService.queryChangeAcceptorMapping(bean,banks, null);
					  if(list != null && list.size() > 0){
						  this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
						  this.sendJSON("映射关系生效失效操作失败：存在相同总行生效的财票映射!");
						  return;//存在生效的财票映射总行不可修改
					  }
					  
//					  //财票修改时，根据行号判断如果存在承兑人映射则不允许添加
//					  bean.setAcptBankCode(grarant.getAcptBankCode());
//					  bean.setCheckType(grarant.getCheckType());
//					  bean.setId(grarant.getId());
//					  bean.setStatus("1");//生效
//					  List list1 = blackListManageService.queryAcceptorMappingList(bean, null);
//					  if(list1 != null && list1.size() > 0){
//						  this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
//						  this.sendJSON("映射关系生效失效操作失败：存在生效的财票映射!");
//						  return;//存在生效的财票映射不可修改
//					  }
				}
				grarant.setStatus("1");
			}else{
				grarant.setStatus("2");
			}
			blackListManageService.txStore(grarant);
			sendJSON(json);
		} catch (Exception e) {
			logger.error("映射关系生效失效操作失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("映射关系生效失效操作失败："+e.getMessage());
		}
		
		
	}
	
	

	/**
	 * 方法说明: 保存或更新商票承兑行名单
	 * @param
	 * @author gcj
	 * @return
	 * @date 2021-06-07
	 */
	@RequestMapping(value="/saveCommercial",method = RequestMethod.POST)
	public void saveCommercial(AcptCheckSublist acptList){
		try{
			//User loginUser = getCurrentUser();//当前操作人
			String json=commonQueryService.txSavaCommercial(acptList);
			this.sendJSON(json);
		}catch (Exception e){
			logger.error("数据库操作失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("数据库操作失败："+e.getMessage());
		}
	}
	/**
	 * 方法说明: 删除商票承兑行名单
	 * @param
	 * @author gcj
	 * @return
	 * @date 2021-06-07
	 */
	@RequestMapping(value="/deleteCommercial",method = RequestMethod.POST)
	public void deleteCommercial(String busIds){
		try{

			String json=commonQueryService.txDeleteCommercial(busIds);
		    this.sendJSON(json);							
		}catch (Exception e){
			logger.error("删除商票承兑行名单失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除商票承兑行名单失败："+e.getMessage());
		}
	}
	
	
	
	/**
	 * 票据池资产实点查询
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210607
	 */
	@RequestMapping("/loadPointListJSON")
	public void loadPointListJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			json = commonQueryService.loadPointList(commonQueryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("票据池资产实点查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据池资产实点查询失败："+e.getMessage());
		}
		
	}
	/**
	 * 票据池每日资产查询
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210610
	 */
	@RequestMapping("/loadPedAssetDailyJSON")
	public void loadPedAssetDailyJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			json = commonQueryService.loadPedAssetDaily(commonQueryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("票据池每日资产查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据池每日资产查询失败："+e.getMessage());
		}
		
	}
	
	/**
	 * 票据池每日融资业务查询
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210610
	 */
	@RequestMapping("/loadPedCrdtDailyJSON")
	public void loadPedCrdtDailyJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			json = commonQueryService.loadPedCrdtDaily(commonQueryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("票据池每日融资业务查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据池每日融资业务查询失败："+e.getMessage());
		}
		
	}
	
	/**
	 * 查询短信信息
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210608
	 */
	@RequestMapping("/loadInformationNoteJSON")
	public void loadInformationNoteJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			json = commonQueryService.loadInformatioNoteList(commonQueryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("查询短信信息失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询短信信息失败："+e.getMessage());
		}
		
	}
	
	/**
	 * 方法说明: 短信信息重发
	 * @param
	 * @author gcj
	 * @return
	 * @date 2021-06-08
	 */
	@RequestMapping(value="/informationTask",method = RequestMethod.POST)
	public void informationTask(String busIds){
		try{
			Map<String, String> reqParams =new HashMap<String,String>();
	    	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOL_MSS_TASK_NO, busIds, AutoTaskNoDefine.BUSI_TYPE_MSS,reqParams);
		    this.sendJSON("短信信息重发成功");							
		}catch (Exception e){
			logger.error("短信信息重发失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("短信信息重发失败："+e.getMessage());
		}
	}
	
	/**
	 * 在线流贷支付信息
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210608
	 */
	@RequestMapping("/loadOnlinePayJSON")
	public void loadOnlinePayJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			json = commonQueryService.loadOnlinePayList(commonQueryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("查询在线流贷支付失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询在线流贷支付失败："+e.getMessage());
		}
		
	}
	
	/**
	 * 在线流贷信息查询
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210618
	 */
	@RequestMapping("/loadPlOnlineCrdtJSON")
	public void loadPlOnlineCrdtJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			json = commonQueryService.loadPlOnlineCrdtList(commonQueryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("查询在线流贷信息失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询在线流贷信息失败："+e.getMessage());
		}
		
	}
	/**
	 * 在线流贷支付导出
	 * 
	 */
	@RequestMapping("onlinePayToExpt")
	public void onlinePayToExpt(CommonQueryBean bean) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {14};
		String[] typeName = { "amount"};
		try {
			list = commonQueryService.loadOnlinePayByBeanExp(bean,this.getCurrentUser(),this.getPage());

			String ColumnNames = "loanAcctName,custNo,onlineNo,contractNo,loanNo,loanAcctName2,loanAcctNo,loanAmt,surpluslAmt,amt,deduAcctName,"
					+ "deduAcctNo,deduBankName,deduBankCode";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("loanAcctName", "融资人");
			mapinfo.put("custNo", "融资人客户号");
			mapinfo.put("onlineNo", "协议编号");
			mapinfo.put("contractNo", "合同号");
			mapinfo.put("loanNo", "借据号");
			mapinfo.put("loanAcctName2", "付款户名");
			mapinfo.put("loanAcctNo", "付款账号");
			mapinfo.put("loanAmt", "总金额（元）");
			mapinfo.put("surpluslAmt", "剩余支付金额（元）");
			mapinfo.put("amt", "已支付金额（元）");
			mapinfo.put("deduAcctName", "收款人名称");
			mapinfo.put("deduAcctNo", "收款人账号");
			mapinfo.put("deduBankName", "收款人开户行行名");
			mapinfo.put("deduBankCode", "收款人开户行行号");
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("在线流贷支付清单" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 在线流贷支付导出
	 * 
	 */
	@RequestMapping("onlinePayHisToExpt")
	public void onlinePayHisToExpt(CommonQueryBean bean) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {12};
		String[] typeName = { "amount"};
		try {
			list = commonQueryService.loadOnlinePayHisByBeanExp(bean,this.getCurrentUser(),this.getPage());

			String ColumnNames = "loanAcctName,custNo,onlineNo,contractNo,loanNo,loanAcctName,loanAcctNo,surpluslAmt,deduAcctName,"
					+ "deduAcctNo,deduBankName,deduBankCode";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("loanAcctName", "融资申请人名称");
			mapinfo.put("custNo", "融资申请人客户号");
			mapinfo.put("onlineNo", "协议编号");
			mapinfo.put("contractNo", "合同号");
			mapinfo.put("loanNo", "借据号");
			mapinfo.put("loanAcctName", "付款户名");
			mapinfo.put("loanAcctNo", "付款账号");
			mapinfo.put("surpluslAmt", "剩余支付金额（元）");
			mapinfo.put("deduAcctName", "收款人名称");
			mapinfo.put("deduAcctNo", "收款人账号");
			mapinfo.put("deduBankName", "收款人开户行行名");
			mapinfo.put("deduBankCode", "收款人开户行行号");
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("在线流贷支付清单" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	
	/**
	 * 方法说明: 取消支付计划
	 * @param busIds PlCrdtPayPlan支付计划表ID
	 * @author gcj
	 * @return
	 * @date 2021-06-08
	 */
	@RequestMapping(value="/cancelOnlinePay",method = RequestMethod.POST)
	public void cancelOnlinePay(String busIds){
		try{
			/**
			 * 待开发 tx
			 */
		    this.sendJSON("取消支付计划成功");						
		}catch (Exception e){
			logger.error("取消支付计划失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("取消支付计划失败："+e.getMessage());
		}
	}
	
	/**
	 * 方法说明: 取消支付计划
	 * @param busIds PlCrdtPayPlan支付计划表ID
	 * @author gcj
	 * @return
	 * @date 2021-06-08
	 */
	@RequestMapping(value="/submitOnlinePay",method = RequestMethod.POST)
	public void submitOnlinePay(String busIds){
		try{
			/**
			 * 待开发 tx
			 */
		    this.sendJSON("取消支付计划成功");						
		}catch (Exception e){
			logger.error("取消支付计划失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("取消支付计划失败："+e.getMessage());
		}
	}
	
	

	/**
	 * 节假日查询
	 */
	@RequestMapping(value="/queryHolidayList",method = RequestMethod.POST)
	public void queryHolidayList(String month){
		String json = "";
		String day = "";
		try {
			Page page = this.getPage();
			String months[] = month.split("-");
			if(months[1].equals("02")){
				 day = "-29";
			}else if(months[1].equals("01") || months[1].equals("03") || months[1].equals("05") || months[1].equals("07") || months[1].equals("08") ||months[1].equals("10") || months[1].equals("12")){
				day = "-31";
			}else{
				day = "-30";
			}
			String dateStr = month + day;
			Date dateEnd = DateUtils.StringToDate(dateStr, DateUtils.ORA_DATES_FORMAT);
			Date dateStart = DateUtils.StringToDate(month+"-01", DateUtils.ORA_DATES_FORMAT);
			QueryBean bean = new QueryBean();
			bean.setStartDate(dateStart);
			bean.setEndDate(dateEnd);
			bean.setRemindNum(1);
			List result = holidayService.getAllHolidayList(bean,page);
			
			json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
			logger.info("查询节假日失败"+e.getMessage());
		}
	}
	@RequestMapping(value="/queryHolidayByYear",method = RequestMethod.POST)
	public void queryHolidayByYear(String year,Date sDate, Date eDate){
		String json = "";
		try {
			List result = new ArrayList();
			Page page = this.getPage();
			if(StringUtils.isNotEmpty(year)){
				Date dateEnd = DateUtils.StringToDate(year+"-12-31", DateUtils.ORA_DATES_FORMAT);
				Date dateStart = DateUtils.StringToDate(year+"-01-01", DateUtils.ORA_DATES_FORMAT);
				QueryBean bean = new QueryBean();
				bean.setStartDate(dateStart);
				bean.setEndDate(dateEnd);
				bean.setRemindNum(1);
				result = holidayService.getAllHolidayList(bean,page);
			}else{
				if(sDate == null && eDate == null){
					int yar = DateUtils.getYear(new Date());
					Date dateEnd = DateUtils.StringToDate(yar+"-12-31", DateUtils.ORA_DATES_FORMAT);
					Date dateStart = DateUtils.StringToDate(yar+"-01-01", DateUtils.ORA_DATES_FORMAT);
					QueryBean bean = new QueryBean();
					bean.setStartDate(dateStart);
					bean.setEndDate(dateEnd);
					bean.setRemindNum(1);
					result = holidayService.getAllHolidayList(bean,page);
				}else{
					QueryBean bean = new QueryBean();
					bean.setStartDate(sDate);
					bean.setEndDate(eDate);
					bean.setRemindNum(1);
					result = holidayService.getAllHolidayList(bean,page);
				}
			}
			
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			json = JsonUtil.buildJson(result, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
			logger.info("查询节假日失败"+e.getMessage());
		}
	}
	
	/**
	 * 节假日调整
	 */
	@RequestMapping(value="/editHoliday",method = RequestMethod.POST)
	public void editHoliday(String date){
		String json = "";
		
		try {
			String[] dateStr = date.split(",");
			for (int i = 0; i < dateStr.length; i++) {
				Date holiday = DateUtils.parseDate(dateStr[i]);
				
				HolidayDto dto = holidayService.queryHolidayDtoByDate(holiday);
				if(dto != null && dto.getSIfHoliday() == 1){
					//节假日
					dto.setSIfHoliday(0);
					dto.setDataSource("1");
					holidayService.txSaveHoliday(dto);
				}else{
					//非节假日
					if( dto == null ){
						dto=new HolidayDto();
					}
					/* 设置日期 */
					dto.setDDate(holiday);
					String sDay = DateUtils.getWeek(holiday);
					dto.setSDay(sDay);
					/* 其它字段可有可无 */
					dto.setSIfHoliday(1);
					/*日期来源  手动录入*/
					dto.setDataSource("1");
					/* 保存日期 */
					holidayService.txSaveHoliday(dto);
				}
			}
//			HolidayDto dto = holidayService.queryHolidayDtoByDate(date);
			cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_HOLIDAY);
			this.sendJSON("设置成功");	
		} catch (Exception e) {
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
			logger.info("节假日调整失败"+e.getMessage());
		}
	}
	
	/**
	 * 查询综合业务控制管理
	 */
	@RequestMapping(value = "queryConfigList", method = RequestMethod.POST)
	public void queryConfigList() {
		try {
			String json = "";
			Page page = this.getPage();
			User user = this.getCurrentUser();
			SystemConfig config = new SystemConfig();
			//config.setBusiType("pjc");
			List<SystemConfig> result = commonQueryService.queryControlConfigList(config, this.getPage(),user);

			json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.info("查询综合业务控制管理失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	/**
	 * 保存综合业务控制管理
	 */
	@RequestMapping(value = "saveControlConfig", method = RequestMethod.POST)
	public void saveControlConfig(@RequestBody Map<String, String> map) {
	    try {
	        String sec = this.checkParam(map);
	        User user = this.getCurrentUser();

	        if(!StringUtils.isEmpty(sec)){
	            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
	            this.sendJSON(sec);
	        }else{
	            commonQueryService.txSaveControlConfig(map,user);
	            cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_SYSCONFIG);//更新缓存
	            String json = "{'success':true}";
	            this.sendJSON(json);
	        }

	    } catch (Exception e) {
	        logger.info("保存综合业务控制管理失败"+e.getMessage());
	        this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
	        this.sendJSON(e.getMessage());
	    }
	}

	/**
     * <p>
     * 方法名称: queryAcceptName|描述: 获取开户行名称
     * </p>
     */
    @RequestMapping("/queryAcceptName")
    public void queryAcceptName(String bankNo) {
    	String json="";
        try {
        	//获取开户行名称
            if(StringUtils.isNotEmpty(bankNo)){
            	 json=commonQueryService.queryAcceptNameJson(bankNo);
				if(StringUtils.isEmpty(json)){
					json = "1";
				}
			}else{
				json = "2";
			}
        } catch (Exception e) {
        	logger.info("获取开户行名称失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("获取开户行名称失败"+e.getMessage());
        }
        this.sendJSON(json);
    }
	
    /**
	 * 票据池合同协议表查询
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210610
	 */
	@RequestMapping("/loadPoolQuotaListJSON")
	public void loadPoolQuotaListJSON(CommonQueryBean commonQueryBean) {
		
		String json = "";
		try {
			json = commonQueryService.loadPoolQuotaList(commonQueryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("票据池合同协议表查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("票据池合同协议表查询失败："+e.getMessage());
		}
		
	}
	/**
	 * 池额度列表导出
	 * 
	 */
	@RequestMapping("poolQuotaListToExpt")
	public void poolQuotaListToExpt(CommonQueryBean bean) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {12};
		String[] typeName = { "amount"};
		try {
			list = commonQueryService.loadPoolQuotaToExpt(bean,this.getCurrentUser(),this.getPage());

			String ColumnNames = "contractNo,bpsNo,custName,crdtTypeName,ccupy,creditamount,contractEffectiveDt,contractDueDt";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("contractNo", "合同号");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("custName", "融资人名称");
			mapinfo.put("crdtTypeName", "业务品种");
			mapinfo.put("ccupy", "额度占用比例");
			mapinfo.put("creditamount", "合同金额");
			mapinfo.put("contractEffectiveDt", "合同起始日");
			mapinfo.put("contractDueDt", "合同到期日");
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("池额度列表清单" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 额度占用比例调整校验
	 * @param bean
	 * @author Ju Nana
	 * @date 2021-9-12上午1:26:08
	 */
	@RequestMapping("/checkScale")
	public void checkScale(CommonQueryBean bean){
		String json = "";
		String apId = null;
		try {
			String ccupy1 = bean.getCcupy1();//新占用系数			
			String contNo = bean.getContractNo();//主业务合同编号
			String ccupy = bean.getCcupy();//原占用系数
			
			
			//输入校验
			String inputCheckMsg = this.inputNumCheck(ccupy1);
			if(!Constants.TX_SUCCESS_CODE.equals(inputCheckMsg)){
				json = inputCheckMsg; 
				this.sendJSON(json);
				return ;
			}
			
			//占用系数校验
			if(new BigDecimal(ccupy1).compareTo(new BigDecimal(ccupy))<=0){//新占用系数小于原占用系数，直接允许修改
				Map<String, String> mapJson = new HashMap<String, String>();
				mapJson.put("flag", "true");
				JSONObject array = new JSONObject(mapJson);
				json = array.toString();
				this.sendJSON(json);
				return ;
			}
			
			//查询该主业务合同对应的全部资产信息
			List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(contNo, null, null);
			BigDecimal allOccAmt = BigDecimal.ZERO;//当前全部占用金额
			BigDecimal moreAmt = BigDecimal.ZERO;//系数调整后多出来的金额
			if(null != crList){
				for(CreditRegister cr : crList ){
					allOccAmt = allOccAmt.add(cr.getOccupyAmount());
				}
				moreAmt = allOccAmt.multiply(new BigDecimal(ccupy1).subtract(new BigDecimal(ccupy)));
			}else{
				Map<String, String> mapJson = new HashMap<String, String>();
				mapJson.put("flag", "false");
				mapJson.put("msg", "该合同已无额度占用信息，无需进行系数修改。");				
				JSONObject array = new JSONObject(mapJson);
				json = array.toString();
				this.sendJSON(json);
				return ;
			}
			
			//额度校验需要的对象获取
			CreditProduct oldProduct = poolCreditProductService.queryProductByCreditNo(contNo,null);//主业务合同
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, oldProduct.getBpsNo(), null, null, null);//票据池协议
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);//票据池额度对象
			apId = ap.getApId();
			
			//锁AssetPool表
			boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
			if(!isLockedSucss){//加锁失败
				Map<String, String> mapJson = new HashMap<String, String>();
				mapJson.put("flag", "false");
				mapJson.put("msg", "票据池其他额度相关任务正在处理中，请稍后再试。");				
				JSONObject array = new JSONObject(mapJson);
				json = array.toString();
				this.sendJSON(json);
				return ;
			}
			
			//核心保证金同步及额度重算
			financialService.txUpdateBailAndCalculationCredit(apId, dto);
			
			
			CreditProduct product = new CreditProduct();
			BeanUtil.beanCopy(oldProduct, product);
			product.setCrdtNo(Long.toString(System.currentTimeMillis()));//随意生成即可
			product.setUseAmt(moreAmt);//合同金额
			product.setRestUseAmt(moreAmt);//需要占用的额度
			product.setCcupy("1");//占用比例
			product.setId(UUID.randomUUID().toString().replaceAll("-",""));
			
			
			//用信业务登记，额度占用校验
			String flowNo = Long.toString(System.currentTimeMillis());
			CreditRegisterCache crdtReg = creditRegisterService.createCreditRegisterCache(product, dto,apId);
			crdtReg.setFlowNo(flowNo);
			List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
			crdtRegList.add(crdtReg);
			
			Ret crdtCheckRet =  financialService.txCreditUsedCheck(crdtRegList, dto,flowNo);
			
			if(crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				Map<String, String> mapJson = new HashMap<String, String>();
				mapJson.put("flag", "true");
				mapJson.put("msg", "调整后额度充足，是否继续?");				
				JSONObject array = new JSONObject(mapJson);
				json = array.toString();
				
			}else{
				Map<String, String> mapJson = new HashMap<String, String>();
				mapJson.put("flag", "false");
				mapJson.put("msg", "调整后额度不足，不得调整。");				
				JSONObject array = new JSONObject(mapJson);
				json = array.toString();
			}			
			
			//解锁AssetPool表，并重新计算该表数据
    		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
			
			
		} catch (Exception e) {
			logger.error("额度占用比例测算异常"+e.getMessage(),e);
			
			pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
			
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("额度占用比例测算异常"+e.getMessage());
		}
		
		
		this.sendJSON(json);
		return ;
	}
	
	/**
	 * 输入校验：录入必须为[0,10]数字
	 * @param inputNum
	 * @return
	 * @author Ju Nana
	 * @date 2021-8-26下午5:11:39
	 */
	private String inputNumCheck(String inputNum){
		String json = "";
		
		Map<String, String> mapJson = new HashMap<String, String>();
		mapJson.put("flag", "false");

		if(!inputNum.matches("[+-]?[0-9]+(\\.[0-9]+)?")){

			mapJson.put("msg", "录入的新额度占用比例只能为数值！");				
			JSONObject array = new JSONObject(mapJson);
			json = array.toString();
			return json;
		}else{
			BigDecimal num = new BigDecimal(inputNum);
			if(num.compareTo(BigDecimal.ZERO)<0 ||num.compareTo(new BigDecimal(10))>0 ){
				mapJson.put("msg", "请输入[0,10]之间的数值！");				
				JSONObject array = new JSONObject(mapJson);
				json = array.toString();
				return json;	
			}
		}
		
		json = Constants.TX_SUCCESS_CODE;
		return json;
	}
	
	/**
	 *额度占用比例调整
	 * @author gcj
	 * @date 20210610
	 */
	@RequestMapping("/changeScale")
	public void changeScale(CreditProduct creditProduct){
		String json = "";
		try{
			String newCcupy = creditProduct.getCcupy1();
			CreditProduct  cp  = (CreditProduct) commonQueryService.load(creditProduct.getId(),CreditProduct.class);	
			creditRegisterService.txChangeProductCcupy(cp, newCcupy);
			json="额度占用比例调整成功";
		}catch(Exception e){
			logger.error("额度占用比例调整失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("额度占用比例调整失败"+e.getMessage());
		}
		sendJSON(json);
	}
	
	/**
	 * 手工出池
	 * @author gcj
	 * @date 20210620
	 */
	@RequestMapping("/outHandPool")
	public void outHandPool(String ids){
		String json = "";
		try{
        /**增加审批流
         * 
         */
			
			
		json="手工出池成功";
		}catch(Exception e){
			logger.error("手工出池失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("手工出池失败"+e.getMessage());
		}
		this.sendJSON(json);
	}
	
	 /**
		 * 票据池签约客户查询
		 * @Description TODO
		 * @author gcj
		 * @version v1.0
		 * @date 20210618
		 */
		@RequestMapping("/loadCustomerRegisterJSON")
		public void loadCustomerRegisterJSON(CommonQueryBean commonQueryBean) {
			
			String json = "";
			try {
				json = commonQueryService.loadCustomerRegisterList(commonQueryBean, this.getCurrentUser(), this.getPage());
				if (!(StringUtil.isNotBlank(json))) {
					json = RESULT_EMPTY_DEFAULT;
				}
				sendJSON(json);
			} catch (Exception e) {
				logger.error("票据池签约客户查询失败"+e.getMessage(),e);
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("票据池签约客户查询失败："+e.getMessage());
			}
			
		}
		private String checkParam(Map<String, String> map) {
		    String sec = null;
		    if(!map.isEmpty()){
		        String start = "";
		        String end = "";
		        int s = 0;
		        int e = 0;
		        if(map.containsKey("OL_OPENTIME_YC") && map.containsKey("OL_ENDTIME_YC")){
		            start = map.get("OL_OPENTIME_YC");
		            end = map.get("OL_ENDTIME_YC");
		            s = Integer.valueOf(start.replace(":", ""));
		            e = Integer.valueOf(end.replace(":", ""));
		            if(s > e){
		                sec = "在线银承开始时间不能大于在线银承结束时间";
		            }
		        }
		        if(map.containsKey("OL_OPENTIME_LD") && map.containsKey("OL_ENDTIME_LD")){
		            start = map.get("OL_OPENTIME_YC");
		            end = map.get("OL_ENDTIME_YC");
		            s = Integer.valueOf(start.replace(":", ""));
		            e = Integer.valueOf(end.replace(":", ""));
		            if(s > e){
		                sec = "在线流贷开始时间不能大于在线流贷结束时间";
		            }
		        }
		        if(map.containsKey("OL_OPENTIME_TX") && map.containsKey("OL_ENDTIME_TX")){
		            start = map.get("OL_OPENTIME_YC");
		            end = map.get("OL_ENDTIME_YC");
		            s = Integer.valueOf(start.replace(":", ""));
		            e = Integer.valueOf(end.replace(":", ""));
		            if(s > e){
		                sec = "在线贴现开始时间不能大于在线贴现结束时间";
		            }
		        }
		    }
		    return sec;
		}
}
