/**
 * 
 */
package com.mingtech.application.pool.manage.web;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.ecds.common.BatchNoUtils;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolInBatch;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftPoolOutBatch;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.CreditProductQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.manage.domain.QueryPedListBean;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @author wbyecheng
 *
 */
@Controller
public class PoolQueryController extends BaseController {
	private static final Logger logger = Logger.getLogger(PoolQueryController.class);
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	@Autowired
	private DictCommonService dictCommonService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private ConsignService consignService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private PoolCoreService poolcoreService;
	@Autowired
	private DraftPoolOutService draftPoolOutService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private DraftPoolInService draftPoolInService;
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private PoolBailEduService poolBailEduService ;
	@Autowired
	AssetRegisterService assetRegisterService;
	@Autowired
	FinancialService financialService;
	
	
	@RequestMapping("toStorageDraftQuery")
	public String toStorageDraftQuery() {
		return "/pool/query/storageDraftQuery";
	}

	@RequestMapping("topoolDraftQuery")
	public String topoolDraftQuery() {
		return "/pool/query/poolDraftQuery";
	}

	/**
	 * 池票据查询
	 */
	@RequestMapping("poolDraftList")
	public void poolDraftList(DraftQueryBean bean) {
		String json = "";
		try {
			json = draftPoolQueryService.queryByObjJson(bean, "DraftPool", getPage());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	/**
	 * liuxiaodong 新池票据查询
	 */
	@RequestMapping("poolDraftListNew")
	public void poolDraftListNew(DraftQueryBean bean) {
		String json = "";
		try {
			Page page = getPage();
			List list = draftPoolQueryService.queryDraftPool(bean, this.getCurrentUser(), page);
			Map jsonMap = new HashMap();
			jsonMap.put("totalProperty", "results," + page.getTotalCount());
			jsonMap.put("root", "rows");
			json = JsonUtil.fromCollections(list, jsonMap);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}

	/**
	 * 池票据查询批量导出
	 * 
	 */
	@RequestMapping("poolDraftListExpt")
	public void poolDraftListExpt(DraftQueryBean bean) {

		int[] num = { 6 };
		String[] typeName = { "amount" };
		try {
			List list1 = draftPoolQueryService.queryDraftPool(bean, this.getCurrentUser(), getPage());
			List list = draftPoolQueryService.findPoolDraftExpt(list1, getPage());

			String ColumnNames = "poolAgreement,poolName,plDraftNb,plDraftType,plDraftMedia,assetStatus,plIsseAmt,plIsseDt,plDueDt,plDrwrNm,"
					+ "plDrwrAcctSvcrNm,plPyeeNm,plPyeeAcctSvcrNm,isEduExistName,rickLevelName";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("poolAgreement", "票据池编号");
			mapinfo.put("poolName", "票据池名称");
			mapinfo.put("plDraftNb", "票据号码");
			mapinfo.put("plDraftType", "票据属性");
			mapinfo.put("plDraftMedia", "票据种类");
			mapinfo.put("assetStatus", "出入池状态");
			mapinfo.put("plIsseAmt", "票据金额");
			mapinfo.put("plIsseDt", "出票日期");
			mapinfo.put("plDueDt", "到期日期");
			mapinfo.put("plDrwrNm", "出票人名称");
			mapinfo.put("plDrwrAcctSvcrNm", "出票人开户行行名");
			mapinfo.put("plPyeeNm", "收款人名称");
			mapinfo.put("plPyeeAcctSvcrNm", "收款人开户行行名");
			mapinfo.put("isEduExistName", "是否已产生额度");
			mapinfo.put("rickLevelName", "额度类型");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("池票据.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	@RequestMapping("custEduListQuota")
	public void custEduListQuota(String poolAgreement,String isGroup ,String poolName) {
		String resJson = "";

		try {
			resJson = draftPoolQueryService.findCustEduQuotaJson(poolAgreement,isGroup,poolName,this.getCurrentUser(), this.getPage());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(resJson);
	}
	@RequestMapping("custEduListExpt")
	public void custEduListExpt(String poolAgreement,String isGroup) {
		int[] num = { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		String[] typeName = { "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount" };
		try {
			List list = draftPoolQueryService.findCustEduJsonExpt(poolAgreement,isGroup,this.getCurrentUser(), this.getPage());

			String ColumnNames = "poolAgreement,custName,custnumber,custOrgcode,poolMode,lowerTotalEdu,lowerEffTotalEdu," +
								 "usedLowRiskAmount,freeLowRiskAmount,heightEffTotalEdu,usedHighRiskAmount" +
					",freeHighRiskAmount,riskLowCreditTotalAmount,riskLowCreditUsedAmount,riskLowCreditFreeAmount,bailAmountTotal";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("poolAgreement", "票据池编号");
			mapinfo.put("custName", "票据池名称");
			mapinfo.put("custnumber", "核心客户号");
			mapinfo.put("custOrgcode", "组织机构代码");
			mapinfo.put("poolMode", "额度模式");
			mapinfo.put("lowerTotalEdu", "低风险总额度");
			mapinfo.put("lowerEffTotalEdu", "低风险票据额度");
			mapinfo.put("usedLowRiskAmount", "低风险已用额度");
			mapinfo.put("freeLowRiskAmount", "低风险可用额度");
			mapinfo.put("heightEffTotalEdu", "高风险额度");
			mapinfo.put("usedHighRiskAmount", "已用高风险额度");
			mapinfo.put("freeHighRiskAmount", "未用高风险额度");
			mapinfo.put("riskLowCreditTotalAmount", "担保合同限额");
			mapinfo.put("riskLowCreditUsedAmount", "担保合同已用额度");
			mapinfo.put("riskLowCreditFreeAmount", "担保合同剩余额度");
			mapinfo.put("bailAmountTotal", "保证金额度");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("企业额度.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	@RequestMapping("custAseetPoolDetailList")
	public void custAseetPoolDetailList(String plCommId) {//plCommId：票据池编号
		String resJson = "";
		try {
			PedProtocolDto dto = null;
			List<PedProtocolDto> proList = pedProtocolService.queryProtocolInfo( PoolComm.OPEN_01, null,  plCommId,null, null, null);
			if(proList!=null && proList.size()>0){
				dto = proList.get(0);
			}
			if(dto != null) {
				poolBailEduService.txUpdateBailDetail(dto.getPoolAgreement());
				//将assetType赋值到assetPool中
				EduResult eduResult = pedAssetPoolService.queryEduAll(dto.getPoolAgreement());
				AssetPool assetPool = draftPoolQueryService.queryAssetPoolByBpsNo(dto.getPoolAgreement());
				assetPool.setCrdtTotal(eduResult.getLowRiskAmount().add(eduResult.getBailAmountTotail()));//衍生总额度
				assetPool.setCrdtFree(eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount()));//可用额度
				assetPool.setCrdtUsed(eduResult.getUsedLowRiskAmount().add(eduResult.getBailAmountUsed()));//已用额度
				poolEBankService.txStore(assetPool);			
			}
			resJson = draftPoolQueryService.findCustAseetPoolDetail(plCommId, this.getPage());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(resJson);
	}


	/**
	 * 查询统计-信贷发生查询-查询明细
	 */
	@RequestMapping("crdtProductList")
	public void crdtProductList(CreditProductQueryBean bean) {
		String resJson = "";
		try {
			resJson = draftPoolQueryService.findCrdtProductList(bean,getPage(),this.getCurrentUser());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(resJson);
	}



	// 信贷产品导出-查询明细
	@RequestMapping("crdtProductListExpt")
	public void crdtProductListExpt(String isQuery, String selRange, CreditProductQueryBean bean) {
		int[] num = { 5, 6, 8 };
		String[] typeName = { "amount", "amount", "amount" };
		try {
			List list = draftPoolQueryService.findCrdtProductListExpt(bean, this.getPage(), getCurrentUser());
			String ColumnNames = "crdtNo,crdtNob,bpsNo,custName,crdtType,loanAmount,actualAmount,ccupy,useAmt,"
					+ "crdtIssDt,crdtDueDt,sttlFlag,startTime,endTime,ifOnlineName,ifAdvanceAmt,loanStatus,risklevel,transAccount";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("crdtNo", "合同号");
			mapinfo.put("crdtNob", "借据号");
			mapinfo.put("bpsNo", "票据池编号");
			mapinfo.put("custName", "融资人名称");
			mapinfo.put("crdtType", "业务品种");
			mapinfo.put("loanAmount", "借据金额");
			mapinfo.put("actualAmount", "借据余额");
			mapinfo.put("ccupy", "额度占用比例");
			mapinfo.put("useAmt", "占用额度");
			mapinfo.put("crdtIssDt", "合同起始日");
			mapinfo.put("crdtDueDt", "合同到期日");
			mapinfo.put("sttlFlag", "合同状态");
			mapinfo.put("startTime", "借据起始日");
			mapinfo.put("endTime", "借据到期日");
			
			mapinfo.put("ifOnlineName", "是否在线业务");
			mapinfo.put("ifAdvanceAmt", "是否垫款");
			
			mapinfo.put("loanStatus", "借据状态");
			mapinfo.put("risklevel", "风险等级");
			
			mapinfo.put("transAccount", "贷款账号/业务保证金账号");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("融资业务发生" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	
	//更新日间出账的信息
	@RequestMapping("reFreshLoad")
	public void reFreshLoad(String loanNo){
		String resJson = "{success:'true'}";
		try {
			PedCreditDetail detail = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(null,loanNo);
			if(detail != null) {
				logger.debug("根据借据号["+loanNo+"]查询PedCreditDetail成功");
				draftPoolQueryService.txUpdateLoanByCoreforQuery(detail);
				logger.info("根据借据号["+loanNo+"]同步借据余额成功");
			} else {
				throw new Exception("根据借据号["+loanNo+"]查询PedCreditDetail失败");
			}
		} catch (Exception e) {
			logger.error(e, e);
			resJson = "{'success':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(resJson);
	}



	/**
	 * 生成JSON
	 * 
	 * @param page
	 * @param list
	 * @return
	 * @throws Exception
	 */
	protected String toJson(Page page, List list) throws Exception {

		Map jsonMap = new HashMap();
		jsonMap.put("totalProperty", "results," + page.getTotalCount());
		jsonMap.put("root", "rows");
		String json = JsonUtil.fromCollections(list, jsonMap);
		if (!(StringUtil.isNotBlank(json))) {
			json = RESULT_EMPTY_DEFAULT;
		}

		return json;
	}


	/**
	 * wfj 2018-10 入池查询界面
	 */
	@RequestMapping("toqueryPlPoolIn")
	public String toqueryPlPoolIn() {
		return "/pool/query/poolInQuery";
	}

	/**
	 * 出池批次查询
	 */
	@RequestMapping("topoolOutList")
	public void topoolOutList(DraftQueryBean bean) {
		Page page = this.getPage();
		String json = "";
		try {
			List list = draftPoolQueryService.toPoolAllOutQuery(bean, this.getCurrentUser(), page);
			json = this.toJson(page, list);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}

	/**
	 * 出池签收异常查询
	 */
	@RequestMapping("topoolOutSignList")
	public void topoolOutSignList(DraftPoolOut bean) {
		User user = this.getCurrentUser();
		Page page = this.getPage();
		String json = "";
		try {
			List list = draftPoolQueryService.toPoolOutSignQuery(bean, user, page);
			json = this.toJson(page, list);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 出池到期异常查询
	 */
	@RequestMapping("topoolOutExpireList")
	public void topoolOutExpireList(CollectionSendDto bean) {
		User user = this.getCurrentUser();
		Page page = this.getPage();
		String json = "";
		try {
			List list = draftPoolQueryService.toPoolOutExpireQuery(bean, user, page);
			json = this.toJson(page, list);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}


	/**
	 * 出池明细查询
	 */
	@RequestMapping("topoolAllOutDetailList")
	public void topoolAllOutDetailList(String dpid) {
		DraftPoolOutBatch draftPoolOutBatch = new DraftPoolOutBatch();
		DraftQueryBean bean = new DraftQueryBean();
		bean.setAssetNb(dpid);
		User user = this.getCurrentUser();
		Page page = this.getPage();
		String json = "";
		List list = new ArrayList();
		try {
			draftPoolOutBatch = (DraftPoolOutBatch) draftPoolQueryService.toPoolAllOutQuery(bean, null, null).get(0);
			Set set = draftPoolOutBatch.getPoolOuts();
			Iterator<DraftPoolOut> it = set.iterator();
			while (it.hasNext()) {
				DraftPoolOut str = it.next();
				list.add(str);
			}
			json = this.toJson(this.getPage(), list);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}

	/**
	 * 出池查询界面
	 */
	@RequestMapping("toqueryPlPoolOut")
	public String toqueryPlPoolOut() {
		return "/pool/query/poolOutQuery";
	}

	/**
	 * 入池批次查询
	 */
	@RequestMapping("topoolInList")
	public void topoolInList(DraftQueryBean bean,String busiId) {
		Page page = this.getPage();
		String json = "";
		try {
			List list = draftPoolQueryService.toPoolAllInQuery(bean, this.getCurrentUser(), page);
			json = this.toJson(page, list);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}

	/**
	 * 入池明细查询
	 */
	@RequestMapping("topoolAllInDetailList")
	public void topoolAllInDetailList(String dpid) {
		DraftPoolInBatch draftPoolInBatch = new DraftPoolInBatch();
		DraftQueryBean bean = new DraftQueryBean();
		bean.setAssetNb(dpid);
		User user = this.getCurrentUser();
		Page page = this.getPage();
		String json = "";
		List list = new ArrayList();
		try {
			draftPoolInBatch = (DraftPoolInBatch) draftPoolQueryService.toPoolAllInQuery(bean, null, null).get(0);
			if(draftPoolInBatch != null){
				Set set = draftPoolInBatch.getPoolIns();
				Iterator<DraftPoolIn> it = set.iterator();
				while (it.hasNext()) {
					DraftPoolIn str = it.next();
					list.add(str);
				}
				json = this.toJson(this.getPage(), list);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}

	/**
	 * wfj 2018-10 虚拟票据查询界面
	 */
	@RequestMapping("toqueryInventedBill")
	public String toqueryInventedBill() {
		return "/pool/query/InventedBill";
	}

	/**
	 * 虚拟票据查询josn
	 */
	@RequestMapping("queryInvented")
	public void queryInvented(DraftQueryBean bean) {
		Page page = this.getPage();
		String json = "";
		try {
			List list = draftPoolQueryService.InventedBillQuery(bean, this.getCurrentUser(), page);
			json = this.toJson(page, list);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}

	/**
	 * 虚拟票据查询批量导出
	 * 
	 */
	@RequestMapping("InventedListExpt")
	public void InventedListExpt(DraftQueryBean bean) {
		
		int[] num = { 5 };
		String[] typeName = { "amount" };
		try {
			List list1 = draftPoolQueryService.InventedBillQuery(bean, this.getCurrentUser(), getPage());
			List list = draftPoolQueryService.findPoolDraftExpt1(list1, getPage());

			String ColumnNames = "poolAgreement,poolName,vtNb,vtTypeName,plDraftMedia,vtisseAmt,vtisseDt,vtdueDt,vtdrwrName,vtdrwrBankName,vtpyeeName,vtpyeeBankName,payType,rickLevel";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("poolAgreement", "票据池编号");
			mapinfo.put("poolName", "票据池名称");
			mapinfo.put("vtNb", "票据号码");
			mapinfo.put("vtTypeName", "票据类型");
			mapinfo.put("plDraftMedia", "票据介质");
			mapinfo.put("vtisseAmt", "票据金额");
			mapinfo.put("vtisseDt", "出票日");
			mapinfo.put("vtdueDt", "到期日");
			mapinfo.put("vtdrwrName", "出票人名称");
			mapinfo.put("vtdrwrBankName", "出票人开户行行名");
			mapinfo.put("vtpyeeName", "收款人名称");
			mapinfo.put("vtpyeeBankName", "收款人开户行行名");
			mapinfo.put("payType", "票据权益");
			mapinfo.put("rickLevel", "额度类型");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("虚拟票据" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 出入池票据查询批量导出
	 * 
	 */
	@RequestMapping("PoolAllListExpt")
	public void PoolAllListExpt(DraftQueryBean bean, String name) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = { 8, 9 };
		String[] typeName = { "amount", "amount" };
		List list1 = null;
		try {
			if (name.equals("出池") && name != null) {
				list1 = draftPoolQueryService.toPoolAllOutQuery(bean, user, getPage());
			} else {
				list1 = draftPoolQueryService.toPoolAllInQuery(bean, user, getPage());
			}
			list = draftPoolQueryService.findPoolAllExpt(list1, getPage());

			String ColumnNames = "batchNo,plRecSvcrNm,plDraftType,plDraftMedia,"
					+ "plReqTime,plApplyNm,workerName,plCommId," + "totalCharge,totleBill,plStatus";
			Map mapinfo = new LinkedHashMap();

			mapinfo.put("batchNo", "批次号");
			mapinfo.put("plRecSvcrNm", "业务经办行");
			mapinfo.put("plDraftType", "票据类型");
			mapinfo.put("plDraftMedia", "票据介质");
			if (name.equals("出池")) {
				mapinfo.put("plReqTime", "出池日期");
				mapinfo.put("plApplyNm", "出池申请人");
				mapinfo.put("workerName", "出池受理人");
				mapinfo.put("plCommId", "出池受理机构");
			} else {
				mapinfo.put("plReqTime", "入池日期");
				mapinfo.put("plApplyNm", "入池申请人");
				mapinfo.put("workerName", "入池受理人");
				mapinfo.put("plCommId", "入池受理机构");
			}
			mapinfo.put("totalCharge", "总费用");
			mapinfo.put("totleBill", "总笔数");
			mapinfo.put("plStatus", "批次状态");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("出入池票据" + ".xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 出池跟踪查询界面
	 * 
	 * @return
	 */
	@RequestMapping("toQueryPoolOutList")
	public String queryTraceOut() {
		return "/pool/query/TraceOut";
	}

	/**
	 * 入池跟踪查询界面
	 * 
	 * @return
	 */
	@RequestMapping("toQueryPoolInList")
	public String toQueryPoolInList() {
		return "/pool/query/TraceIn";
	}

	/**
	 * 出入池票据跟踪查询批量导出
	 * 
	 */
	@RequestMapping("PoolTraceListExpt")
	public void PoolTraceListExpt(DraftQueryBean bean, String name) {
		List list = null;
		User user = this.getCurrentUser();
		String bankNo = user.getDepartment().getBankNumber();
		int[] num = {3};
		String[] typeName = { "amount","amount", "amount" };
		List list1 = null;
		try {
			if (null != name && name.equals("出")) {
				list1 = draftPoolQueryService.toPoolAllOutQuery(bean, user, getPage());
			} else {
				list1 = draftPoolQueryService.toPoolAllInQuery(bean, user, getPage());
			}
			list = draftPoolQueryService.findPoolTraceAllExpt(list1, getPage());

			String ColumnNames = "plDraftNb,poolAgreement,custName,plIsseAmt,plIsseDt,plDueDt,"
					+ "plAccptrSvcrNm,plStatus,plReqTime";
			Map mapinfo = new LinkedHashMap();
			
			mapinfo.put("plDraftNb", "票号");
			mapinfo.put("poolAgreement", "票据池编号");
			mapinfo.put("custName", "票据池客户名称");
			mapinfo.put("plIsseAmt", "票面金额");
			mapinfo.put("plIsseDt", "出票日");
			mapinfo.put("plDueDt", "到期日");
			mapinfo.put("plAccptrSvcrNm", "承兑行名称");
			mapinfo.put("plStatus", "票据状态");
			mapinfo.put("plReqTime", "申请时间");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			HttpServletResponse response = getResponse();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/octet-stream");
			if(StringUtils.isNotBlank(name)&&"出".equals(name)){
				response.addHeader("Content-Disposition",
						"attachment; filename=" + URLEncoder.encode("出池业务跟踪查询" + ".xls", "utf-8"));
			}else {
				response.addHeader("Content-Disposition",
						"attachment; filename=" + URLEncoder.encode("入池业务跟踪查询" + ".xls", "utf-8"));
			}
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	/**
	 * 重新发送解质押签收申请
	 * @author txy
	 */
	@RequestMapping("poolOutNewSend")
	public void poolOutNewSend(String dpid) {
		DraftPoolOut out = new DraftPoolOut();
		ECDSPoolTransNotes poolTrans = new ECDSPoolTransNotes();
		String json = "";
		try {
			out = (DraftPoolOut) draftPoolQueryService.load(dpid, DraftPoolOut.class);
			PoolBillInfo bill = draftPoolQueryService.loadByBillNo(out.getPlDraftNb(),out.getBeginRangeNo(),out.getEndRangeNo());
			PedProtocolDto dto = pedProtocolService.queryProtocolDto( null, null,out.getPoolAgreement(), null, null, null);

			//若电子签名为空设置为0
			if(out.getElsignature()!=null&&!out.getElsignature().equals("")){
				poolTrans.setSignature(out.getElsignature());//网银发过来的电子签名
			}else{
				poolTrans.setSignature("0");//电子签名
			}
			poolTrans.setAcceptorAcctNo(out.getAccNo());//电票签约账号	
			//质权人用融资机构信息	若果有融资机构号拿融资机构号,若没有拿受理网点
				String orgNo = "10000";
				if(StringUtil.isNotBlank(dto.getCreditDeptNo())){
					orgNo = dto.getCreditDeptNo();
				}else {
					orgNo = dto.getOfficeNet();
				}
				logger.info("根据机构号["+orgNo+"]查询机构信息开始");
				Department ment = departmentService.queryByInnerBankCode(orgNo);
				if(ment!=null){
					logger.info("查询部门信息结束,质权人开户行号为["+ment.getBankNumber()+"],质权人开户行名称为["+ment.getName()+"]");
					poolTrans.setReceiverBankNo(ment.getPjsBrchNo());//质权人开户行行号
				}else {
					logger.info("未查询到部门信息");
				}
			
			/**
			 * 质押签收 请求信息数组  需上传的值
			 * 缺少强制贴现标志
			 */
			Map infoMap = new HashMap();
//			infoMap.put("REQUEST_INFO_ARRAY.TRAN_ID","");//交易id  必输
			infoMap.put("REQUEST_INFO_ARRAY.TRAN_ID",out.getTransId());//交易id  必输
			infoMap.put("REQUEST_INFO_ARRAY.BILL_SOURCE",bill.getDraftSource());//票据来源  必输
			infoMap.put("REQUEST_INFO_ARRAY.BILL_NO",bill.getSBillNo());//票据（包）号码
			infoMap.put("REQUEST_INFO_ARRAY.BILL_ID",bill.getDsctbrid());//票据ID
			infoMap.put("REQUEST_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
			infoMap.put("REQUEST_INFO_ARRAY.FORCE_DISCOUNT_FLAG",out.getTXFlag());//强制贴现标志 0否 1是
			infoMap.put("REQUEST_INFO_ARRAY.BUSS_TYPE","201902");//业务类型
			infoMap.put("REQUEST_INFO_ARRAY.SIGN_FLAG","1");//签收标识  必输
			String seq = poolBatchNoUtils.txGetFlowNo();
			infoMap.put("REQUEST_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
			poolTrans.getDetails().add(infoMap);
			
			ReturnMessageNew resp = poolEcdsService.txApplySignReject(poolTrans);
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				List details = resp.getDetails();
				logger.info("接收到解质押签收返回，返回数据："+details);
				for (int i = 0; i < details.size(); i++) {
					Map map = (Map) details.get(i);
					logger.info("接收到解质押签收返回，返回TRAN_RESULT_ARRAY.TRAN_RET_CODE值："+map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE"));
					if(map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE").equals(Constants.TX_SUCCESS_CODE)){
						logger.info("发送签收申请结束");
						out.setTransId((String)(map.get("TRAN_RESULT_ARRAY.TRAN_ID")));//交易ID
						
						out.setPlStatus(PoolComm.CC_04);//已发解质押签收申请
						bill.setSDealStatus(PoolComm.DS_05);//签收处理中
						
						/**
						 *若为拆分票据这里需对网银经办锁解锁 
						 */
						if(out.getSplitId() != null && StringUtils.isNotBlank(out.getSplitId())){
							PoolBillInfo billIn = draftPoolInService.loadBySplit(out.getSplitId(),PoolComm.DS_02);
							if(billIn != null){
								billIn.setEbkLock(PoolComm.EBKLOCK_02);
								draftPoolInService.txStore(billIn);
								//DOTO查询持票id
								
							}
						}
						
						draftPoolOutService.txStore(bill);
						draftPoolOutService.txStore(out);
						json = "{'result':true}";
					}else{
						json = "{'result':false,'message':'" + map.get("TRAN_RESULT_ARRAY.TRAN_RET_MSG") + "'}";
					}
				}
			}else{
				json = "{'result':false,'message':'" + resp.getRet().getRET_MSG() + "'}";
			}
		} catch (Exception e) {
			logger.error(e, e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}
	
	/**
	 * 解质到期异常提示付款申请
	 * @author txy
	 */
	@RequestMapping("poolOutExpireNewSend")
	public void poolOutExpireNewSend(String plDraftNb,String beginRangeNo, String endRangeNo) {
		String json = "";
		try {
			//1.根据id得到出池表中的数据
			//2.创建CollectionSendDto对象并赋值
			//3.发托收申请
			
			//4.改变出池对象状态为签收失败
			
			PoolBillInfo bill = draftPoolInService.loadByBillNo(plDraftNb,beginRangeNo,endRangeNo);
			PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setBillNo(plDraftNb);
			/********************融合改造新增 start******************************/
			poolQueryBean.setBeginRangeNo(beginRangeNo);
			poolQueryBean.setEndRangeNo(endRangeNo);
			/********************融合改造新增 end******************************/
			poolQueryBean.setSStatusFlag(PoolComm.DS_02);
			DraftPool pool =consignService.queryDraftByBean(poolQueryBean).get(0);
			DraftPoolOut out = draftPoolOutService.getDraftPoolOutByDraftNb(plDraftNb,beginRangeNo,endRangeNo);
			PedProtocolDto dto = (PedProtocolDto) pedProtocolService.queryProtocolDto(null, null, out.getPoolAgreement(), null, null, null);

	    	CollectionSendDto collSendTemp = consignService.loadSendDtoByBillNo(bill.getSBillNo(),beginRangeNo,endRangeNo);
	    	if(collSendTemp==null){
	    		collSendTemp = new CollectionSendDto();
	    		//大票信息
				collSendTemp.setPoolBillInfo(bill);//大票信息
				collSendTemp.setSBillNo(bill.getSBillNo());//票号
				collSendTemp.setFBillAmount(bill.getFBillAmount());//票面金额
				collSendTemp.setDIssueDt(bill.getDIssueDt());//出票日
				collSendTemp.setDDueDt(bill.getDDueDt());//到期日
				collSendTemp.setSBillMedia(bill.getSBillMedia());//票据介质
				collSendTemp.setSBillType(bill.getSBillType());//票据类型
				//承兑方
				collSendTemp.setAcceptNm(bill.getSAcceptor());//承兑方名称
				collSendTemp.setAcceptAccount(bill.getSAcceptorAccount());//承兑方帐号
				collSendTemp.setAcceptAcctSvcr(bill.getSAcceptorBankCode());//承兑方行号
				collSendTemp.setAcceptBankName(bill.getSAcceptorBankName());//承兑方开户行名称
				//提示付款信息
				collSendTemp.setApplDt(DateUtils.getWorkDayDate());//提示付款日期
				collSendTemp.setAmt(bill.getFBillAmount());//提示付款金额
				collSendTemp.setGuaranteeNo(out.getGuaranteeNo());//担保编号
				collSendTemp.setSBranchId(dto.getOfficeNet());//存储网点号  用于分配权限
				collSendTemp.setBpsNo(dto.getPoolAgreement());//票据池编号
				
				//判断是否为本行票据
				Department dept = this.departmentService.getDepartmentByBankNo(bill.getSAcceptorBankCode());
				if(dept!=null){
					//提示付款人（或逾期提示付款人=银行信息）信息
					collSendTemp.setCollNm(dept.getName());//提示付款人(或逾期)名称
					collSendTemp.setCollCmonId(dept.getOrgCode());//提示付款人(或逾期)组织机构代码
					collSendTemp.setCollAcct("0");//提示付款人(或逾期)帐号
					collSendTemp.setCollAcctSvcr(dept.getBankNumber());//提示付款人(或逾期)大额行号
				}
				collSendTemp.setAccNo(pool.getAccNo());//电票签约账号
				
	    	}

		   /*
		    * 调用提示付款接口
		    */
			ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
			
			/**
			 * body内需要传送的值
			 */
			poolTransNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
			poolTransNotes.setSignature("0");//电子签名
			
			/**
			 * 票据信息数组需传送的值
			 */
			Map infoMap = new HashMap();
			infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",bill.getDraftSource());//票据来源 
			infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0112001);//交易编号  提示付款申请
			infoMap.put("BILL_INFO_ARRAY.BILL_NO",bill.getSBillNo());//票据（包）号码
			infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",bill.getHilrId());//持票id
			infoMap.put("BILL_INFO_ARRAY.BILL_ID",bill.getDiscBillId());//票据id
			infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",collSendTemp.getBeginRangeNo());//子票区间起始
			infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",collSendTemp.getEndRangeNo());//子票区间截至
			infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
			infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
			poolTransNotes.getDetails().add(infoMap);

			/**
			 * 提示付款信息数组需传送的值
			 * 缺少结算方式
			 */
			Map pledgeMap = new HashMap();
			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.INPOOL_FLAG","1");//入池标志 必输
			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.LOCK_FLAG","汉口银行");//锁定标志
			//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
			if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
				poolTransNotes.setBatchNo(dto.getCreditDeptNo());//融资机构号
			}else{
				poolTransNotes.setBatchNo(dto.getOfficeNet());//受理网点
			}
			logger.info("根据机构号["+poolTransNotes.getBranchNo()+"]查询机构信息开始");
			Department dept = this.departmentService.getDepartmentByInnerBankCode(poolTransNotes.getBatchNo());
			logger.info("查询机构信息结束,行号为["+dept.getPjsBrchNo()+"]");
			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.PROMPT_PAY_BRANCH_NO",dept.getAuditBankCode());//提示付款机构号 必输

			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.REMARK","");//备注
			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.PROMPT_PAY_DATE", new Date());//提示付款申请日期  必输
			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.PROMPT_PAY_AMT","");//提示付款金额
			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.SETTLE_TYPE","");//结算方式 必输
			pledgeMap.put("BILL_INFO_ARRAY.PROMPT_PAY_INFO_ARRAY.OUT_TIME_REASON","已到期");//逾期原因说明
			poolTransNotes.getDetails().add(pledgeMap);
			
			
			logger.debug("票据id为["+bill.getDiscBillId()+"]的票,提示付款申请开始");
			ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				//状态统一使用PoolComm对象定义
				Map map = resp.getBody();
				collSendTemp.setHilrId((String)(map.get("hilrId")));//持票ID
				collSendTemp.setTransId((String)(map.get("transId")));//交易ID

				collSendTemp.setSBillStatus(PoolComm.TS00);//明细状态
				bill.setSDealStatus(PoolComm.DS_06);//大票表设置票据状态为到期处理中
				//得到pl_pool 对象,为了改变该票的状态为已发托,额度计算式需要
				pool.setAssetStatus(PoolComm.DS_06);//已发托
				out.setPlStatus(PoolComm.CC_05_2);//解质押到期异常
				pool.setGuaranteeNo(out.getGuaranteeNo());//担保品编号
				collSendTemp.setGuaranteeNo(out.getGuaranteeNo());
				
				bill.setHilrId((String)(map.get("hilrId")));//持票ID
				pool.setHilrId((String)(map.get("hilrId")));//持票ID

				
 				json = "{'result':true}";
			}
			consignService.txStore(collSendTemp);
			consignService.txStore(bill);
			consignService.txStore(pool);
			consignService.txStore(out);
		} catch (Exception e) {
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	/**
	 * 解质押到期异常处理：用于处理出池过程出现异常，一直到票据到期没做处理，这里需要补记一下入池的账，后续会发起托收操作
	 * @param plStatus
	 */
	@RequestMapping("poolOutExpireNewCommid")
	public void poolOutExpireNewCommid(String plDraftNb,String beginRangeNo,String endRangeNo){
		String json = "";
		try {
			DraftPoolOut out = draftPoolOutService.getDraftPoolOutByDraftNb(plDraftNb,beginRangeNo,endRangeNo);
			logger.debug("根据核心客户号["+out.getCustNo()+"]查询协议信息开始");
			PedProtocolDto dto = (PedProtocolDto) pedProtocolService.queryProtocolDto(null, null, out.getPoolAgreement(), null, null, null);
			logger.info("查询协议信息结束,机构号为["+dto.getCreditDeptNo()+"],受理网点为["+dto.getOfficeNet()+"]");
			//调取记账接口，产生票据池额度。
			CoreTransNotes transNotes = new CoreTransNotes();
			transNotes.setAmtAct(BigDecimalUtils.setScale(out.getPlIsseAmt()).toString());//担保金额
			transNotes.setBilCode(out.getPlDraftNb());//票号
			//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
			if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
				transNotes.setBrcNo(dto.getCreditDeptNo());//融资机构号
			}else{
				transNotes.setBrcNo(dto.getOfficeNet());//受理网点
			}
			Department dept = departmentService.getDepartmentByInnerBankCode(transNotes.getBrcNo());
			if(dept != null){
				transNotes.setBrcBld(dept.getAuditBankCode());//报文头需赋值的机构
			}else{
				throw new Exception("机构信息有误,未查询到机构信息");
//				transNotes.setBrcBld("10000");
			}
			transNotes.setCustId(out.getCustNo());//核心客户号 
			String colNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);
			transNotes.setColNo(colNo);//担保编号
			transNotes.setDateDue(DateUtils.toString(out.getPlDueDt(), "yyyyMMdd"));//到期日
			if(out.getDevSeqNo() != null && !"".equals(out.getDevSeqNo())){
				transNotes.setDevSeqNo(out.getDevSeqNo());//第三方流水号
			}else{
				String str = poolBatchNoUtils.txGetFlowNo();
				transNotes.setDevSeqNo(str);//第三方流水号
				out.setDevSeqNo(str);//保存流水号
				draftPoolInService.txStore(out);
			}
			transNotes.setNoVouCom(out.getPlTradeProto());//合同号
			transNotes.setNumBatch(DateUtils.dtuGetCurDatTimStr().substring(0, 8));//批号
			transNotes.setTypGag("2");//抵质押物类型
			transNotes.setNoVouCom(dto.getContract());//合同号
			logger.info("票号为["+transNotes.getBilCode()+"]的票,发送核心记账开始");
			ReturnMessageNew response = poolcoreService.PJH580314Handler(transNotes);
			logger.debug("发送核心记账开结束,状态为["+response.isTxSuccess()+"]");
			if(response.isTxSuccess()){//记账成功
				out.setGuaranteeNo(transNotes.getColNo());
				out.setHostSeqNo((String)response.getBody().get("HOST_SEQ_NO"));//记账完成后记录
				out.setPlStatus(PoolComm.RC_05);
				draftPoolInService.txStore(out);
				json = "true";
				PoolQueryBean bean = new PoolQueryBean();
				bean.setBillNo(out.getPlDraftNb());
				
				/********************融合改造新增 start******************************/
				bean.setBeginRangeNo(beginRangeNo);
				bean.setEndRangeNo(endRangeNo);
				/********************融合改造新增 end******************************/
				
				DraftPool draft=consignService.queryDraftByBean(bean).get(0);
				draft.setAssetStatus(PoolComm.DS_02);//更改为已入池
				draftPoolInService.txStore(draft);
				
				//重新生成额度：即登记到资产表 
				AssetRegister ar = assetRegisterService.txBillAssetRegister(draft, dto);
				
				//同步保证金，并重新计算池额度信息
				financialService.txBailChangeAndCrdtCalculation(dto);
				
				//解锁AssetPool表，并重新计算该表数据
				AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
				String apId = ap.getApId();
				pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
				
			}
			json = "{'result':true}";
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}

	
	/*****************电票提示付款记账 wfj start  20190301*****************/
	/**
	 * 跳转到提示付款记账界面
	 */
	@RequestMapping("toCollctionSendAccount")
	public String toCollctionSendAccount(){
		return "draftcollection/queryCollSendAccountInfoList";
	}
	@RequestMapping("queryCollSendAccountInfoList")
	public void queryCollSendAccountInfoList(QueryBean querybean ,Model model){
		String json = "";
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			List bankNums = new ArrayList();
			String bankNum = user.getDepartment().getBankNumber();
			querybean.setSStatusFlag(PoolComm.TS03);
			List result = consignService.queryCollectionSendDto(querybean, bankNums, user, page);
			json = this.toJson(page, result);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	/**
	 * 提示付款记账功能
	 */
	@RequestMapping("collReceiveAccountCommit")
	public void collReceiveAccountCommit(CollectionSendDto collectionSendDto){
		String json = "";
		BatchNoUtils BatchNoUtils = PoolCommonServiceFactory.getBatchNoUtils();
		String apId = null;
		try {
			CollectionSendDto sendDto =pedProtocolService.queryDtoById(collectionSendDto.getCollectionSendId());//得到提示付款对象
			
			logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度处理...");
			
			/*
			 * （1）发送核心提示付款记账 
			 */
			logger.info("托收电票["+sendDto.getSBillNo()+"]核心系统记账处理...");
			
			CoreTransNotes core = new CoreTransNotes();
			core.setAmtPay(BigDecimalUtils.setScale(sendDto.getFBillAmount()).toString());//支付金额
			if(sendDto.getAcctFlowNo() != null && !"".equals(sendDto.getAcctFlowNo())){
				core.setDevSeqNo(sendDto.getAcctFlowNo());//第三方流水号
			}else{
				String str = poolBatchNoUtils.txGetFlowNo();
				core.setDevSeqNo(str);//第三方流水号
				sendDto.setAcctFlowNo(str);//保存流水号
				consignService.txStore(sendDto);
			}
			logger.debug("第三方流水号为["+core.getSerSeqNo()+"]支付金额为["+core.getAmtPay()+"]");
			if(sendDto.getSBillMedia().equals("1")){
				logger.debug("不是电票");
				core.setIssWay("0");//不是电票
			}else{
				logger.debug("是电票");
				core.setIssWay("1");
			}
			logger.debug("*********************"+sendDto.getPoolBillInfo()+"**************************");
			logger.debug("根据核心客户号["+sendDto.getPoolBillInfo().getCustNo()+"]查询协议信息开始");
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, sendDto.getPoolBillInfo().getPoolAgreement(), null, null, null);
			logger.info("查询协议信息结束,保证金账号为["+dto.getMarginAccount()+"]");
			core.setAccNoPye(dto.getMarginAccount());//保证金账号
			if(sendDto.getClearWay().equals("1")){//清算标志	电票返回：1、线上  2、线下   核心需要的：1、线上  0、线下
				core.setFlgWay("1");
			}else{
				core.setFlgWay("0");
			}
			
			/**
			 * 判断是否做过拆分
			 */
			boolean flag = false;
			PoolBillInfo info = sendDto.getPoolBillInfo();
			if(StringUtils.isNotEmpty(info.getSplitId())){
				//已做拆分，需送入库担保编号及出库担保编号
				String inColNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);//流水号
				String OutcolNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);//流水号
				core.setInColNo(inColNo);//入库担保编号
				core.setOutColNo(OutcolNo);//出库担保编号
				flag = true;
			}
			core.setColNo(sendDto.getGuaranteeNo());//担保编号
			
			
			core.setBilCode(sendDto.getSBillNo());//票号
			if(StringUtils.isNotBlank(sendDto.getDraftSource()) && !sendDto.getDraftSource().equals(PoolComm.CS01)){
				core.setBeginRangeNo(sendDto.getBeginRangeNo());
				core.setEndRangeNo(sendDto.getEndRangeNo());
			}
			
			//管理机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
			if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
				core.setBrcNo(dto.getCreditDeptNo());//融资机构号
			}else{
				core.setBrcNo(dto.getOfficeNet());//受理网点
			}
			logger.debug("根据机构号["+core.getBrcNo()+"]查询部门信息开始");
			Department ment = departmentService.getDepartmentByInnerBankCode(core.getBrcNo());
			//交易机构赋值
			if(ment==null){
				throw new Exception("机构信息不全");
			}else{
				logger.debug("查询部门信息不为null");
				core.setBrcNo(ment.getAuditBankCode());//报文头里的机构号
			}
			core.setIsTerm(sendDto.getBankFlag());//是否我行承兑标志
			logger.debug("交易机构号为["+core.getBrcNo()+"]");
			logger.info("票号为["+core.getBilCode()+"]的票,发送核心解质押记账开始");
			
				
			 /* 记账接口处理*/
			 
			ReturnMessageNew response = poolcoreService.PJH580311Handler(core);
			logger.debug("发送核心解质押记账结束,状态为["+response.isTxSuccess()+"]");
			if(response.isTxSuccess()){//记账成功
				
				if(flag){
					//若做过拆分数据，保存未记账的票据的担保品编号
					PoolBillInfo InBillInfo = draftPoolInService.loadBySplit(info.getSplitId(), "DS_06");
					
					CollectionSendDto sendDtoIn = consignService.queryCollectionSendByStatus(InBillInfo.getSBillNo(), InBillInfo.getBeginRangeNo(), InBillInfo.getEndRangeNo());
					sendDtoIn.setGuaranteeNo(core.getInColNo());
					consignService.txStore(sendDto);
					
				}
				
				/***记账成功***/
				sendDto.setSBillStatus(PoolComm.TS05);//记账成功
				sendDto.setLastOperTm(new Date());
				sendDto.setLastOperName("界面发起提示付款记账");
				consignService.txStore(sendDto);
				PoolQueryBean poolQueryBean1 = new PoolQueryBean();
				poolQueryBean1.setBillNo(sendDto.getSBillNo());
				
				/********************融合改造新增 start******************************/
				poolQueryBean1.setBeginRangeNo(sendDto.getBeginRangeNo());
				poolQueryBean1.setEndRangeNo(sendDto.getEndRangeNo());
				/********************融合改造新增 end******************************/
				
				poolQueryBean1.setSStatusFlag(PoolComm.DS_06);
				DraftPool pool=consignService.queryDraftByBean(poolQueryBean1).get(0);
				if(pool != null){
					PoolBillInfo bill = pool.getPoolBillInfo();
					bill.setSDealStatus(PoolComm.TS05);
					pool.setAssetStatus(PoolComm.TS05);//记账成功
					consignService.txStore(bill);
					consignService.txStore(pool);
					
					/*
					 * 记账成功,释放保贴额度 
					 */
					if(pool.getBtFlag() != null && pool.getBtFlag().equals(PoolComm.SP_01)){//占用额度系统额度的的票据进行额度释放
						
						logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度释放...");
						
						Map resuMap = new HashMap();
						List<Map> reqList = new ArrayList<Map>();//实际为单条
						CreditTransNotes creditNotes = new CreditTransNotes();
						
						if(bill.getSplitFlag().equals("1")){//可拆分的等分化票据
							resuMap.put("billNo", sendDto.getSBillNo()+"-"+sendDto.getBeginRangeNo()+"-"+sendDto.getEndRangeNo());
						}else{
							resuMap.put("billNo", sendDto.getSBillNo());
						}
						reqList.add(resuMap);
						creditNotes.setReqList(reqList);//上传文件
						
						ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
						
						if(response1.isTxSuccess()){
							pool.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
							PoolQueryBean pBean = new PoolQueryBean();
							pBean.setProtocolNo(pool.getPoolAgreement());
							pBean.setBillNo(pool.getAssetNb());
							
							/********************融合改造新增 start******************************/
							pBean.setBeginRangeNo(pool.getBeginRangeNo());
							pBean.setEndRangeNo(pool.getEndRangeNo());
							/********************融合改造新增 end******************************/
							
							PedGuaranteeCredit pedCredit = poolCreditProductService.queryByBean(pBean);
							pedCredit.setStatus(PoolComm.SP_00);
							pedCredit.setCreateTime(DateUtils.getWorkDayDate());
							consignService.txStore(pedCredit);
							consignService.txStore(pool);
							logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度释放成功...");
							
						}
					}
					
					//资产登记表处理
					assetRegisterService.txDraftStockOutAssetChange(dto.getPoolAgreement(), pool,sendDto.getTradeAmt(), PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
					
					logger.info("界面提示付款记账功能触发完成记账及额度系统额度处理后，重新计算该票据池的额度，处理票据池编号【"+ dto.getPoolAgreement()+"】");
					
					//核心同步保证金并重新计算池额度信息
					financialService.txBailChangeAndCrdtCalculation(dto);

					AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
					apId = ap.getApId();
					
					//解锁AssetPool表，并重新计算该表数据
					pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true); 
					
				}
				
			}
			json = "{'result':true}";
		} catch (Exception e) {
			if(StringUtil.isNotBlank(apId)){
				pedAssetPoolService.txReleaseAssetPoolLock(apId);
			}
			logger.error(e, e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}
	/*****************电票提示付款记账 end  20190301*****************/

	/*****************电票提示付款撤回 wfj  start  20190301*****************/
	
	/**
	 * 跳转到提示付款撤回界面
	 */
	@RequestMapping("toRecedeCollctionSendApply")
	public String toRecedeCollctionSendApply(){
		return "draftcollection/queryRecedeCollSendInfoList";
	}
	@RequestMapping("queryBlackSendAccountInfoList")
	public void queryBlackSendAccountInfoList(QueryBean querybean ,Model model){
		String json = "";
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			List bankNums = new ArrayList();
			String bankNum = user.getDepartment().getBankNumber();
			querybean.setSStatusFlag(PoolComm.TS00);
			querybean.setDraftSource(PoolComm.CS01);
			List result = consignService.queryCollectionSendDto(querybean, bankNums, user, page);
			json = this.toJson(page, result);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 发送提示付款撤回的方法
	 */
	@RequestMapping("recedeCollSellApplyCommit")
	public void recedeCollSellApplyCommit(CollectionSendDto collectionSendDto){
		String json = "";
		try {
			CollectionSendDto sendDto =pedProtocolService.queryDtoById(collectionSendDto.getCollectionSendId());//得到提示付款对象
			PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setBillNo(sendDto.getSBillNo());
			poolQueryBean.setSStatusFlag(PoolComm.DS_06);
			DraftPool pool=consignService.queryDraftByBean(poolQueryBean).get(0);
			
			ECDSPoolTransNotes transNotes = new ECDSPoolTransNotes();
			transNotes.setApplicantAcctNo(sendDto.getAccNo());
			transNotes.setBillId(sendDto.getPoolBillInfo().getDiscBillId());
			if(StringUtil.isNotBlank(pool.getElsignature())){
				transNotes.setSignature(pool.getElsignature());
			}else{
				transNotes.setSignature("0");
			}
			
			if(poolEcdsService.txApplyRevokeApplyOld(transNotes)){
				sendDto.setSBillStatus(PoolComm.TS02);//提示付款撤回
				sendDto.setLastOperTm(new Date());
				sendDto.setLastOperName("发起提示付款撤回");
				PoolBillInfo bill = pool.getPoolBillInfo();
				pool.setLastOperTm(new Date());
				pool.setLastOperName("自动托收过程,票据到期后发起提示付款撤回");
				pool.setAssetStatus(PoolComm.DS_02);
				bill.setLastOperTm(new Date());
				bill.setLastOperName("自动托收过程,票据到期后发起提示付款撤回");
				bill.setSDealStatus(PoolComm.DS_02);
				pedProtocolService.txStore(sendDto);
			}
			json = "{'result':true}";
		} catch (Exception e) {
			logger.error(e, e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
		
		
	}
	
	/*****************电票提示付款撤回 end  20190301*****************/
	
	/*****************电票提示付款申请 wfj start  20190301*****************/

	/**
	 * 跳转提示付款界面
	 */
	@RequestMapping("toCollSendApply")
	public String toCollSendApply(){
		return "draftcollection/queryCollSendInfoList";
	}
	
	@RequestMapping("queryCollSendInfoList")
	public void queryCollSendInfoList(QueryBean querybean){
		String json = "";
		try {
			Page page = this.getPage();
			List result = consignService.getCollSendForBean(querybean, this.getCurrentUser(), page);//得到可发托收申请的票
			json = JsonUtil.buildJson(result, page.getTotalCount());
	        if(StringUtil.isBlank(json)){
	         json = this.RESULT_EMPTY_DEFAULT;
	        }
	        this.toJson(page, result);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}
	
	@RequestMapping("collSendApplyCommit")
	public void collSendApplyCommit(String ids){
		String json = "";
		try {
			
			consignService.sendCollection(ids);
			json = "{'result':true}";
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	/*****************电票提示付款申请 wfj end  20190301*****************/

	/*****************电票提示付款跟踪查询 wfj start  20190306*****************/
	/**
	 * 跳转到提示付款跟踪查询界面
	 */
	@RequestMapping("toQueryCollctionSendList")
	public String toQueryCollctionSendList(){
		return "draftcollection/toQueryCollSendInfoBusiList";
	}
	
	/**
	 * 提示付款跟踪查询方法
	 * @param querybean
	 * @param model
	 */
	@RequestMapping("toQueryCollctionSendListJOSON")
	public void toQueryCollctionSendListJOSON(QueryBean querybean ,Model model){
		String json = "";
//		List result = consignService.getCollectionSendByStatus(null, null);
		try {
			Page page = this.getPage();
			/* 票据状态 */
			List status = new ArrayList();
			status.add(PoolComm.TS00);//已发提示付款申请 
			status.add(PoolComm.TS01);//提示付款申请失败
			status.add(PoolComm.TS02);//提示付款申请已拒绝
			status.add(PoolComm.TS03);//提示付款签收完毕
			status.add(PoolComm.TS04);//提示付款撤回
			status.add(PoolComm.TS05);//记账完毕 
			status.add(PoolComm.TS06);//记账失败
			querybean.setStatusList(status);
			List bankNums = new ArrayList();
			if(StringUtil.isNotEmpty(querybean.getCollAcctSvcr())){
				bankNums.add(querybean.getCollAcctSvcr());
			}
			List result = consignService.queryCollectionSendDto(querybean, bankNums, this.getCurrentUser(), page);
			json = JsonUtil.buildJson(result, page.getTotalCount());
	        if(StringUtil.isBlank(json)){
	         json = this.RESULT_EMPTY_DEFAULT;
	        }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}
	/*****************电票提示付款跟踪查询 wfj end  20190306*****************/
	
	/**
	 * 成员单位额度明细查询
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/pedProEduDetailList")
	public void pedProEduDetailList(QueryPedListBean bean) throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =draftPoolQueryService.queryPedListBeanDetail(bean,page);
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
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 *  入池交易明细查询  gcj 20210429
	 */
	@RequestMapping("draftPoolInList")
	public void draftPoolInList(DraftQueryBean bean) {
		String json = "";
		try {
			Page page = getPage();
			List list = draftPoolQueryService.queryDraftPoolIn(bean, this.getCurrentUser(), page);
			Map jsonMap = new HashMap();
			jsonMap.put("totalProperty", "results," + page.getTotalCount());
			jsonMap.put("root", "rows");
			json = JsonUtil.fromCollections(list, jsonMap);
			sendJSON(json);
		} catch (Exception e) {
			logger.error("入池交易明细查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("入池交易明细查询失败："+e.getMessage());
		}
	}
	
	/**
	 *  出池交易明细查询  gcj 20210429
	 */
	@RequestMapping("draftPoolOutList")
	public void draftPoolOutList(DraftQueryBean bean) {
		String json = "";
		try {
			Page page = getPage();
			List list = draftPoolQueryService.queryDraftPoolOut(bean, this.getCurrentUser(), page);
			Map jsonMap = new HashMap();
			jsonMap.put("totalProperty", "results," + page.getTotalCount());
			jsonMap.put("root", "rows");
			json = JsonUtil.fromCollections(list, jsonMap);
			sendJSON(json);
		} catch (Exception e) {
			logger.error("出池交易明细查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("出池交易明细查询失败："+e.getMessage());
		}
	}
	
	/**
	 * 查询统计-银票关联合同信息查询 gcj 20210510
	 */
	@RequestMapping("crdtProductAcptDetail")
	public void crdtProductAcptDetail(CreditProductQueryBean bean) {
		String resJson = "";
		try {
			resJson = draftPoolQueryService.findCrdtProductAcptDetail(bean,getPage(),null);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(resJson);
	}
	
	/**
	 * 查询统计-票据池手工出池查询 gcj 20210510
	 */
	@RequestMapping("queryPoolOutHandList")
	public void queryPoolOutHandList(String id) {
		Page page = this.getPage();
		String json = "";
		try {
			DraftPool pool = (DraftPool) draftPoolInService.load(id,DraftPool.class);
			List list = new ArrayList();
			list.add(pool);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			json = JsonUtil.buildJson(list, list.size());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 可出池票据查询 wfj 20210701
	 */
	@RequestMapping("queryPoolOutList")
	public void queryPoolOutList(DraftQueryBean bean) {
		String json = "";
		try {
			//List list = draftPoolQueryService.queryByObj(bean, "DraftPool", getPage());
			Page page = getPage();
			User user = this.getCurrentUser();
			List list = new ArrayList();
			list.add(PublicStaticDefineTab.AUDIT_STATUS_STOP);
			list.add(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);
			list.add(PublicStaticDefineTab.AUDIT_STATUS_RUNNING);
			list.add(PublicStaticDefineTab.AUDIT_STATUS_GOBACK);
			list.add(PublicStaticDefineTab.DS_002);
			list.add(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);
			
			bean.setPlStatus(list);
			
			List result = draftPoolQueryService.findQueryPoolOutList(bean, page, user);
			json = JsonUtil.buildJson(result, page.getTotalCount());
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("查询失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "查询批失败:"+e.getMessage();
		}
		this.sendJSON(json);
	}
	
	/**
	 * 出池申请提交  wfj  20210701
	 * @param id 
	 */
	@RequestMapping("submitAuditPoolOut")
	public void submitAuditPoolOut(String id,BigDecimal tradeAmt){
		String json = "";
		String billNo = null;
		String billId = null;
		DraftPool dp = null;
		PoolBillInfo pool = null;
			
		/*
		 * 加电票经办锁，加票据池锁 
		 */
		try {
			
			dp = (DraftPool) draftPoolQueryService.load(id,DraftPool.class);
			pool = draftPoolQueryService.loadByBillNo(dp.getAssetNb(),dp.getBeginRangeNo(),dp.getEndRangeNo());
			if(pool.getEbkLock().equals(PoolComm.EBKLOCK_01) || dp.getLockz().equals(PoolComm.BBSPLOCK_01)){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				json = "提交申请失败该票已锁定!不允许操作";
			}else{
				dp.setTradeAmt(tradeAmt);
				pool.setTradeAmt(tradeAmt);
				billNo = dp.getAssetNb();
				billId = pool.getDiscBillId();
				/**
				 * 手工出池加票据池经办锁不通知电票系统加锁
				 */
				logger.info("票据【"+billNo+"】出池提交审批电票系统经办锁加锁成功!");
				pool.setEbkLock(PoolComm.EBKLOCK_01);//锁票
				draftPoolQueryService.txStore(pool);
				dp.setLockz(PoolComm.BBSPLOCK_01);//锁票
				draftPoolQueryService.txStore(dp);
				
				User user = this.getCurrentUser();
				//出池申请提交
				draftPoolQueryService.txSubmitPoolOutBill(id, user);
				json = "提交审批成功";
			}
			
			
			/**
			 * 以下代码作废
			 */
			/*ECDSPoolTransNotes ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
			ecdsNotes.setBillId(pool.getDiscBillId());//票据ID
			ecdsNotes.setIsLock("0");//加锁
			//调用BBSP系统锁票接口，返回成功标记
			if (poolEcdsService.txApplyLock(ecdsNotes)){//bbsp操作成功
				logger.info("票据【"+billNo+"】出池提交审批电票系统经办锁加锁成功!");
				pool.setEbkLock(PoolComm.EBKLOCK_01);//锁票
				draftPoolQueryService.txStore(pool);
				
			}else{
				logger.info("票据【"+billNo+"】出池提交审批电票系统经办锁加锁失败!");
				try {
					ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
					ecdsNotes.setBillId(billId);//票据ID
					ecdsNotes.setIsLock("1");//解锁
					if (poolEcdsService.txApplyLock(ecdsNotes)){//bbsp操作成功
						logger.info("票据【"+billNo+"】加锁失败后解锁成功!");
						pool.setEbkLock(PoolComm.EBKLOCK_02);//未锁票
						
						dp.setTradeAmt(dp.getAssetAmt());
						pool.setTradeAmt(pool.getFBillAmount());
						
						draftPoolQueryService.txStore(dp);
						draftPoolQueryService.txStore(pool);
					}else{
						logger.info("票据【"+billNo+"】加锁失败后解锁失败!");
					}
				} catch (Exception e2) {
					logger.info("票据【"+billNo+"】加锁失败后解锁异常：",e2);
				}
				this.sendJSON("票据【"+billNo+"】出池提交审批电票系统经办锁加锁失败!");
				return;
			}*/
		} catch (Exception e) {
			logger.info("票据【"+billNo+"】出池提交审批加锁异常：",e);
			/**
			 * 手工出池解除票据池经办锁不通知电票系统加锁
			 */
			logger.info("票据【"+billNo+"】出池提交审批经办锁解锁成功!");
			pool.setEbkLock(PoolComm.EBKLOCK_02);//未锁票

			dp.setTradeAmt(dp.getAssetAmt());
			dp.setLockz(PoolComm.BBSPLOCK_02);//未锁票
			pool.setTradeAmt(pool.getFBillAmount());
			
			draftPoolQueryService.txStore(dp);
			draftPoolQueryService.txStore(pool);
			logger.error(e.toString());
			logger.error("提交申请失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "提交申请失败:"+e.getMessage();
			
			/**
			 * 以下代码作废
			 */
			/*
			 * 异常中的解锁操作
			 */
			/*try {
				ECDSPoolTransNotes ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
				ecdsNotes.setBillId(billId);//票据ID
				ecdsNotes.setIsLock("1");//解锁
				if (poolEcdsService.txApplyLock(ecdsNotes)){//bbsp操作成功
					logger.info("票据【"+billNo+"】加锁异常后解锁成功!");
					pool.setEbkLock(PoolComm.EBKLOCK_02);//未锁票

					dp.setTradeAmt(dp.getAssetAmt());
					pool.setTradeAmt(pool.getFBillAmount());
					
					draftPoolQueryService.txStore(dp);
					draftPoolQueryService.txStore(pool);
				}else{
					logger.info("票据【"+billNo+"】加锁异常后解锁失败!");
				}
			} catch (Exception e2) {
				logger.info("票据【"+billNo+"】加锁异常后解锁异常：",e);
			}
			
			logger.info("票据【"+billNo+"】出池提交审批通知电票系统加锁异常后防止锁票的解锁操作完成");
			
			this.sendJSON("票据【"+billNo+"】出池提交审批加锁异常："+e.getMessage());
			return;*/
		}
		
		this.sendJSON(json);
	}
	
	/**
	 * 出池申请 撤销审批
	 */
	@RequestMapping("/cancelAuditPoolOut")
	public void cancelAuditPoolOut(String id) {
		String msg = "";
		try {
			User user = this.getCurrentUser();
			String str = draftPoolQueryService.txCancelAuditPoolOutBill(id, user);
			if(str.equals("success")){
				msg = "撤销审批成功";
			}else{
				msg = str;
			}
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			msg = "撤销审批失败:"+e.getMessage();
		}
		this.sendJSON(msg);
	}
	
	
	@RequestMapping("refreshFinancial")
	public void refreshFinancial(String bpsNo) {//票据池编号
		String resJson = "";
		String apId = "";
		try {
			PedProtocolDto dto  = pedProtocolService.queryProtocolDto( null, null,  bpsNo,null, null, null);
			financialService.txRefreshFinancial(dto,null);
			resJson = "额度更新成功！";

		} catch (Exception e) {
			pedAssetPoolService.txReleaseAssetPoolLock(apId);
			logger.error(e.getMessage(),e);
			resJson = "额度更新异常!";
		}
		this.sendJSON(resJson);
	}
	
	
}
