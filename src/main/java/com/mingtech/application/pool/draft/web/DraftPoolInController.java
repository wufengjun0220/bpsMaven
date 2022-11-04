package com.mingtech.application.pool.draft.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
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

import com.mingtech.application.ecd.domain.EndorsementLog;
import com.mingtech.application.ecd.service.EndorsementLogService;
import com.mingtech.application.ecds.common.Trans2RMBUtils;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPoolInBatch;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.InPoolBillBean;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;


/**
 * 
 * @author wbmengdepeng
 * 
 */
@Controller
public class DraftPoolInController extends BaseController {

	@Autowired
	private DraftPoolInService draftPoolInService;
	@Autowired
	private EndorsementLogService endorsementLogService;
	@Autowired
	private DraftPoolDiscountServer draftPoolDiscountServer;
	@Autowired
	private DictCommonService dictCommonService;
	private static final Logger logger = Logger.getLogger(DraftPoolInController.class);


	/**
	 * <p>
	 * 方法名称: poolInBatchJSON|描述: 批次管理-批次列表
	 * </p>
	 */
	@RequestMapping("poolInBatchJSON")
	public void poolInBatchJSON(DraftPoolInBatch draftPoolInBatch,PoolQueryBean poolQueryBean ,Model model) {
		try {
			String json = "";
			try {
				Page page = this.getPage();
				User user = this.getCurrentUser();
				List billStatus = new ArrayList();
				billStatus.add(PoolComm.PC_XZPC);// 新增批次
				billStatus.add(PoolComm.PC_TJSP);// 提交审批
				billStatus.add(PoolComm.PC_SPBTG);// 提交审批
				billStatus.add(PoolComm.PC_YTH);// 批次为已退回
				billStatus.add(draftPoolInBatch.getPlStatus());//页面选择的状态
				
				DraftQueryBean draftQueryBean= new DraftQueryBean();
				draftQueryBean.setPlApplyNm(draftPoolInBatch.getPlApplyNm());//申请人名称
				draftQueryBean.setBatchNo(draftPoolInBatch.getBatchNo());//批次号
				draftQueryBean.setPlStatus(billStatus);
				if(null != poolQueryBean.getStartDate() && !"".equals(poolQueryBean.getStartDate())){//开始日期
					draftQueryBean.setStartplReqTime(DateUtils.parse(poolQueryBean.getStartDate(), null));
				}
				if(null != poolQueryBean.getEndDate() && !"".equals(poolQueryBean.getEndDate())){//开始日期
					draftQueryBean.setEndplReqTime(DateUtils.parse(poolQueryBean.getEndDate(), null));
				}
				draftQueryBean.setPlTradeType(PoolComm.BILL_POOL);
				draftQueryBean.setProductId(PoolComm.PRODUCT_TYPE_RC);
				draftQueryBean.setBranchId(this.getCurrentUser().getDepartment().getId());
				List result = draftPoolInService.queryByObj(draftQueryBean, "DraftPoolInBatch", page);
				json = JsonUtil.buildJson(result, page.getTotalCount());
				if (StringUtil.isBlank(json)) {
					json = this.RESULT_EMPTY_DEFAULT;
				}
				model.addAttribute("draftPoolInBatch", draftPoolInBatch);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				// this.addActionMessage(e.getMessage());
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	
	/**
	* <p>方法名称: viewPoolInDraft|描述: 查看票据明细</p>
	* @return
	*/
	@RequestMapping("viewPoolInDraft")
	public String viewPoolInDraft(DraftQueryBean draftQueryBean,Model model){
		try{
			PoolBillInfo billInfo = draftPoolInService.loadByBillNo(draftQueryBean.getPlDraftNb(),draftQueryBean.getBeginRangeNo(),draftQueryBean.getEndRangeNo());
			if(billInfo !=null ){
				model.addAttribute("billInfo", billInfo);
			}
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		return "/pool/viewPoolPaperBill";
	}
	
	/**
	* <p>方法名称: viewPoolInDraft|描述: 查看电子票据明细</p>
	* @return
	*/
	@RequestMapping("baseLookoverPro")
	public String baseLookoverPro(String SBillNo,String beginRangeNo,String endRangeNo,Model model)throws Exception{
		double baseMoney=1000000000.00;
		/* 金额 */
		try {
			String curDate = DateUtils.toDateString(new Date());
			char[] myChar=new char[11];
			/* 查询票据信息 */
			PoolBillInfo billInfo = draftPoolInService.loadByBillNo(SBillNo,beginRangeNo,endRangeNo);
			StringBuffer bills = new StringBuffer(billInfo.getSBillNo()); //得到票据号码
			String space = " ";
			bills.insert(1, space); //在第1个字符后插入一个空格
			bills.insert(14, space); //在第13个字符后插入一个空格
			bills.insert(23, space); //在第21个字符后插入一个空格
			bills.insert(32, space); //在第29个字符后插入一个空格
			billInfo.setSBillNo(bills.toString());

			List endorseInfoList=new ArrayList();
			endorseInfoList =  endorsementLogService.getELogsByEId(billInfo.getDiscBillId()); // 获取票据历史交易背书信息 
	
			List registerGuarnteeList = new ArrayList(); //出票保证信息
			List acceptanceGuarnteeList = new ArrayList(); //承兑保证信息
		    /*取得出票保证和承兑保证信息*/
			if(endorseInfoList != null ){
				for(int i=0; i<endorseInfoList.size(); i++){
					// 判断是否是保证出票业务
					if("017".equals(((EndorsementLog)endorseInfoList.get(i)).getMsgTpId())){
						if(i==endorseInfoList.size()-1){
							registerGuarnteeList.add((EndorsementLog)endorseInfoList.get(i));
							break;
						}
					}
					
					if("017".equals(((EndorsementLog)endorseInfoList.get(i)).getMsgTpId())&&
							"017".equals(((EndorsementLog)endorseInfoList.get(i+1)).getMsgTpId())){
						registerGuarnteeList.add((EndorsementLog)endorseInfoList.get(i));
						
					}
					// 判断是否是保证承兑业务
					if("017".equals(((EndorsementLog)endorseInfoList.get(i)).getMsgTpId())&&
							"002".equals(((EndorsementLog)endorseInfoList.get(i+1)).getMsgTpId())){
						acceptanceGuarnteeList.add((EndorsementLog)endorseInfoList.get(i));
					}
				}
			}
			
			BillBean billbean=new BillBean();
			billbean.setBillInfo(billInfo);
			billbean.setEndorseInfoList(endorseInfoList);
			billbean.setRegisterGuarnteeList(registerGuarnteeList);
			billbean.setAcceptanceGuarnteeList(acceptanceGuarnteeList);
			/* 组装金额 */
			char[] moneyChar=new char[12];
			/* 测试数据未能保存小数点后两位，因此手动加两位小数，后删除 */
			String billMoney=(billInfo.getFBillAmount().setScale(2)).toString();
			/* 设置大写金额 */
			String upperMoney=Trans2RMBUtils.getRMBStr(billMoney);
			
			if(Double.valueOf(billMoney).doubleValue()>=baseMoney){
				throw new Exception("错误："+"当前金额￥"+billMoney+"超过或等于金额最大上限￥1000000000.00！");
			}else if(billMoney.length()-1<moneyChar.length){
				billMoney="￥"+billMoney;
			}
			char[] tmp=billMoney.toCharArray();
			int count=tmp.length-4;
			moneyChar[10]=tmp[tmp.length-1];
			moneyChar[9]=tmp[tmp.length-2];
			for(int i=8;i>=0;i--){
				if(count>=0&&tmp[count]!='.'){
					moneyChar[i]=tmp[count];
				}else{
					moneyChar[i]=' ';
				}
				count--;
			}
			myChar=moneyChar;
			model.addAttribute("billBean", billbean);
			model.addAttribute("myChar", myChar);
			model.addAttribute("upperMoney", upperMoney);
			model.addAttribute("curDate", curDate);
			model.addAttribute("SDealStatus", getSDealStatus(billInfo.getSDealStatus()));
		}catch (Exception e) {
            logger.error(e.getMessage(),e);
		}
		return "/pool/bkBillInfoList";
	}
	public String getSDealStatus(String str){
		if(str.equals(PoolComm.DS_02)){
			return "已入池";
		}else if(str.equals(PoolComm.DS_04)){
			return "已出池";	
		}else if(str.equals(PoolComm.DS_01)){
			return "入池处理中";
		}else if(str.equals(PoolComm.DS_03)){
			return "出池处理中";	
		}else if(str.equals(PoolComm.DS_05)){
			return "签收处理中";
		}else if(str.equals(PoolComm.DS_06)){
			return "到期处理中";	
		}else if(str.equals(PoolComm.DS_07)){
			return "托收已签收";
		}else if(str.equals(PoolComm.DS_08)){
			return "已拒绝";	
		}else if(str.equals(PoolComm.DS_09)){
			return "已驳回";
		}else if(str.equals(PoolComm.DS_10)){
			return "贴现处理中";
		}else if(str.equals(PoolComm.DS_11)){
			return "贴现已完成";
		}else if(str.equals(PoolComm.TS00)){
			return "已发提示付款申请";	
		}else if(str.equals(PoolComm.TS01)){
			return "提示付款申请失败";
		}else if(str.equals(PoolComm.TS02)){
			return "提示付款申请已拒绝";	
		}else if(str.equals(PoolComm.TS03)){
			return "提示付款签收完毕";
		}else if(str.equals(PoolComm.TS04)){
			return "提示付款撤回";
		}else if(str.equals(PoolComm.TS05)){
			return "记账完毕";	
		}else if(str.equals(PoolComm.TS06)){
			return "记账失败";
		}else if(str.equals(PoolComm.DS_00)){
			return "未处理";
		}else if(str.equals(PoolComm.DS_99)){
			return "不属于本行客户持有的票据";
		}
		return str;
	}

	/**
	 * 强制贴现明细查询
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryForcedDiscount")
	public void queryForcedDiscount(DraftQueryBean bean, String SBillNo,String beginRangeNo,String endRangeNo,String SBillStatus,String bpsNo,String bpsName,String busiId,String draftSource,String splitFlag) throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =draftPoolDiscountServer.getDiscountsListByParamView(bean,this.getCurrentUser(),page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
	}
	public static Logger getLogger() {
		return logger;
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
	/**
	 * 强制贴现导出
	 * 
	 */
	@RequestMapping("ForcedDiscountExpt")
	public void ForcedDiscountExpt(DraftQueryBean bean) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {9};
		String[] typeName = { "amount","amount", "amount" };
		List list1 = null;
		try {
			list1 = draftPoolDiscountServer.getDiscountsListByParamView(bean,null,null);
			list = draftPoolDiscountServer.findForcedDiscountExpt(list1, getPage());

			String ColumnNames = "SBillNo,billMedia,bpsName,discBatchDt,DIssueDt,DDueDt,"
					+ "SAgcysvcr,FBillAmount,SBillStatus";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("SBillNo", "票号");
			mapinfo.put("billMedia", "票据介质");
			mapinfo.put("bpsName", "票据池名称");
			mapinfo.put("discBatchDt", "贴现申请日");
			mapinfo.put("DIssueDt", "出票日");
			mapinfo.put("DDueDt", "到期日");
			mapinfo.put("SAgcysvcr", "承兑行行号");
			mapinfo.put("FBillAmount", "票面金额");
			mapinfo.put("SBillStatus", "票据状态");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("强制贴现" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 纸票在池票据查询
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryInPoolBill")
	public void queryInPoolBill(InPoolBillBean bean) throws Exception {
		Page page = this.getPage();
		User user = this.getCurrentUser();
		String json = "";
		try {
			List list =draftPoolDiscountServer.queryInPoolBillByBean(bean,user,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
	}
	/**
	 * 纸票在池票据导出
	 * 
	 */
	@RequestMapping("InPoolBillToExpt")
	public void InPoolBillToExpt(InPoolBillBean bean) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {14};
		String[] typeName = { "amount","amount", "amount" };
		List list1 = null;
		try {
			list1 = draftPoolDiscountServer.queryInPoolBillByBean(bean,this.getCurrentUser(),this.getPage());
			list = draftPoolDiscountServer.findInPoolBillByBeanExpt(list1, getPage());

			String ColumnNames = "assetNb,FBillAmount,SDealStatus,plDrwrNm,plDrwrAcctId,plDrwrAcctSvcrNm,plPyeeNm,plPyeeAcctId,plPyeeAcctSvcrNm,"
					+ "plAccptrNm,plDueDt,marginAccount,marginAccountName,poperBeatch";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("assetNb", "票号");
			mapinfo.put("FBillAmount", "票面金额");
			mapinfo.put("SDealStatus", "状态");
			mapinfo.put("plDrwrNm", "出票人名称");
			mapinfo.put("plDrwrAcctId", "出票人账号");
			mapinfo.put("plDrwrAcctSvcrNm", "出票人开户行名称");
			mapinfo.put("plPyeeNm", "收款人名称");
			mapinfo.put("plPyeeAcctId", "收款人账号");
			mapinfo.put("plPyeeAcctSvcrNm", "收款人开户行名称");
			mapinfo.put("plAccptrNm", "承兑人/付款人");
			mapinfo.put("plDueDt", "到期日");
			mapinfo.put("marginAccount", "保证金账号");
			mapinfo.put("marginAccountName", "保证金账号名称");
			mapinfo.put("poperBeatch", "保管机构");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = this.getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("入池纸票清单" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}
