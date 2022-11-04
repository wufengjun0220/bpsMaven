package com.mingtech.application.pool.bank.netbanksys.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecd.domain.EndorsementLog;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryParameter;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetFactory;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedBlackList;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PlFeeScale;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.draft.domain.DraftAccountManagement;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftPoolOutBatch;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.AccTrans;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.AccountService;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.pool.trust.domain.DraftStorage;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.application.utils.DraftRange;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.CurValues;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service
public class PoolEBankServiceImpl extends GenericServiceImpl implements PoolEBankService {
	@Autowired
	PoolBailEduService poolBailEduService;
	@Autowired
	DraftPoolInService draftPoolInService;
	@Autowired
	PoolVtrustService poolVtrustService;
	@Autowired
	BlackListManageService blackListManageService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private PoolEcdsService poolEcdsService;

	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private AssetRegisterService assetRegisterService;
	@Autowired
	private UserService userService;
	

	
	private static final Logger logger = Logger.getLogger(PoolEBankServiceImpl.class);
	
	protected HttpSession session;

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
 
	public PedProtocolService getPedProtocolService() {
		return pedProtocolService;
	}

	public void setPedProtocolService(PedProtocolService pedProtocolService) {
		this.pedProtocolService = pedProtocolService;
	}

	public PoolEcdsService getPoolEcdsService() {
		return poolEcdsService;
	}

	public void setPoolEcdsService(PoolEcdsService poolEcdsService) {
		this.poolEcdsService = poolEcdsService;
	}

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	//

	// PJC005,池票据组合查询
	public List queryDraftInfos(PoolQueryBean pq, Page page) {
		StringBuffer hql = new StringBuffer("select dto from DraftPool dto ,PoolBillInfo bill where dto.assetNb = bill.SBillNo ");
		List keys = new ArrayList();
		List values = new ArrayList();
		
		if (StringUtil.isNotBlank(pq.getProtocolNo())) {// 票据池编号
			hql.append(" and dto.poolAgreement=:poolAgreement");
			keys.add("poolAgreement");
			values.add(pq.getProtocolNo());
		}
		if (StringUtil.isNotBlank(pq.getCustomernumber())) {// 客户号
			hql.append(" and dto.custNo=:custNo");
			keys.add("custNo");
			values.add(pq.getCustomernumber());
		}
		
		if (StringUtil.isNotBlank(pq.getBillNo())) {// 票据号码
			hql.append(" and dto.assetNb=:assetNb");
			keys.add("assetNb");
			values.add(pq.getBillNo());
		}

		if (StringUtil.isNotBlank(pq.getSStatusFlag())) {// 票据状态
			String status = "";
			if ("PCS_001".equals(pq.getSStatusFlag())) {
				status = PoolComm.DS_02;
			} else if ("PCS_002".equals(pq.getSStatusFlag())) {
				status = PoolComm.DS_04;
			} else if ("PCS_003".equals(pq.getSStatusFlag())) {
				status = PoolComm.DS_06;
			}
			hql.append(" and dto.assetStatus=:assetStatus");
			keys.add("assetStatus");
			values.add(status);
		}
		if (StringUtil.isNotBlank(pq.getAuditStatus())) {//审批状态
			hql.append(" and dto.auditStatus!=:auditStatus");
			keys.add("auditStatus");
			values.add(pq.getAuditStatus());
		}
		
		if (StringUtil.isNotBlank(pq.getBillType())) {// 票据种类
			if (!"0000".equals(pq.getBillType())) {
				hql.append(" and dto.assetType=:assetType");
				keys.add("assetType");
				values.add(pq.getBillType());
			}
		}
		if (StringUtil.isNotEmpty(pq.getSBillMedia()) && !pq.getSBillMedia().equals("0")) {// 票据介质
			hql.append(" and dto.plDraftMedia=:plDraftMedia");
			keys.add("plDraftMedia");
			values.add(pq.getSBillMedia());
		}
		if (StringUtil.isNotBlank(pq.getBillOutName())) {// 出票人名称
			hql.append(" and dto.plDrwrNm like:plDrwrNm");
			keys.add("plDrwrNm");
			values.add("%" + pq.getBillOutName() + "%");
		}
		if (StringUtil.isNotBlank(pq.getReceMoneName())) {// 收款人名称
			hql.append(" and dto.plPyeeNm like:plPyeeNm");
			keys.add("plPyeeNm");
			values.add("%" + pq.getReceMoneName() + "%");
		}

		if (pq.getStartDate() != null) {// 出票日开始
			hql.append(" and dto.plIsseDt>=:plIsseDt1");
			keys.add("plIsseDt1");
			values.add(pq.getPstartDate());
		}
		if (pq.getEndDate() != null) {// 出票日结束
			hql.append(" and dto.plIsseDt<=:plIsseDt2");
			keys.add("plIsseDt2");
			values.add(pq.getPendDate());
		}
		if (pq.getStartDDueDt() != null) {// 到期日开始
			hql.append(" and dto.plDueDt>=:plDueDt1");
			keys.add("plDueDt1");
			values.add(pq.getStartDDueDt());
		}
		if (pq.getEndDDueDt() != null) {// 到期日结束
			hql.append(" and dto.plDueDt<=:plDueDt2");
			keys.add("plDueDt2");
			values.add(pq.getEndDDueDt());
		}
		if (pq.getIsseAmtStart() != null) {// 票据金额下限
			hql.append(" and dto.assetAmt>=:assetAmt1");
			keys.add("assetAmt1");
			values.add(pq.getIsseAmtStart());
		}
		if (pq.getIsseAmtEnd() != null) {// 票据金额上限
			hql.append(" and dto.assetAmt<=:assetAmt2");
			keys.add("assetAmt2");
			values.add(pq.getIsseAmtEnd());
		}

		if (pq.getStartPoolIn() != null) { // 入池时间开始
			hql.append(" and dto.plTm>=:plTm1");
			keys.add("plTm1");
			values.add(pq.getStartPoolIn());
		}
		if (pq.getEndPoolIn() != null) { // 入池时间结束
			hql.append(" and dto.plTm<=:plTm2");
			keys.add("plTm2");
			values.add(pq.getEndPoolIn());
		}

		if (StringUtil.isNotBlank(pq.getRickLevel())) {//风险等级
			hql.append(" and dto.rickLevel=:rickLevel");
			keys.add("rickLevel");
			values.add(pq.getRickLevel());
		}
		if (StringUtil.isNotBlank(pq.getIsEdu())) {//是否产生额度
			hql.append(" and dto.isEduExist=:isEduExist");
			keys.add("isEduExist");
			values.add(pq.getIsEdu());
		}
		if (StringUtil.isNotBlank(pq.getBillOutBankName())) {//出票人开户行名称
			hql.append(" and dto.plDrwrAcctSvcrNm=:plDrwrAcctSvcrNm");
			keys.add("plDrwrAcctSvcrNm");
			values.add(pq.getBillOutBankName());
		}
		//网银经办锁
		if(StringUtil.isNotEmpty(pq.getEbkLock())){
			hql.append(" and bill.ebkLock =:ebkLock");
			keys.add("ebkLock");
			values.add(pq.getEbkLock());
		}
		if(StringUtil.isNotBlank(pq.getsAcceptorBankCode())){//承兑行
			hql.append(" and dto.plAccptrSvcr=:plAccptrSvcr");
			keys.add("plAccptrSvcr");
			values.add(pq.getsAcceptorBankCode());
		}
		if(null!= pq.getCustAccts() && pq.getCustAccts().size()>0){//承兑行列表
			hql.append(" and dto.plAccptrSvcr in(:acptBankList)");
			keys.add("acptBankList");
			values.add(pq.getCustAccts());
		}
		if(StringUtil.isNotBlank(pq.getBlackFlag())){//黑名单标识
			hql.append(" and dto.blackFlag=:blackFlag");
			keys.add("blackFlag");
			values.add(pq.getBlackFlag());
		}
		
		/*********************************融合改造新增过滤条件start 2022-03-24 wfj*************************************************/
		if (StringUtil.isNotBlank(pq.getBeginRangeNo())) {//子票起始号
			hql.append(" and dto.beginRangeNo =:beginRangeNo");
			keys.add("beginRangeNo");
			values.add(pq.getBeginRangeNo());
		}
		if (StringUtil.isNotBlank(pq.getEndRangeNo())) {//子票截止
			hql.append(" and dto.endRangeNo =:endRangeNo");
			keys.add("endRangeNo");
			values.add(pq.getEndRangeNo());
		}
		if (StringUtil.isNotBlank(pq.getDraftSource())) {//票据来源
			hql.append(" and dto.draftSource =:draftSource");
			keys.add("draftSource");
			values.add(pq.getDraftSource());
		}
		if (StringUtil.isNotBlank(pq.getSplitFlag())) {//是否允许拆分标记 1是 0否
			hql.append(" and dto.splitFlag =:splitFlag");
			keys.add("splitFlag");
			values.add(pq.getSplitFlag());
		}

		/*********************************融合改造新增过滤条件end 2022-03-24 wfj*************************************************/
		
		hql.append(" order by dto.plDueDt desc ");
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List rsut = this.find(hql.toString(), keis, values.toArray(), page);
		if (rsut != null && rsut.size() > 0) {
			return rsut;
		}
		return null;
	}

	// PJC004 BuildQueryResult
	public QueryResult queryPoolBillKind(PoolQueryBean pq, Page page) throws Exception {
		QueryResult qr = new QueryResult();
		List records = new ArrayList();
		String amountFieldName = "";
		if ("01".equals(pq.getQueryParam())) {// 查询类型为虚拟票据池类型
			amountFieldName = "vtisseAmt";
			records = this.queryVtrustDraftInfos(pq, page);
		} else {// 查询类型为融资票据池
			amountFieldName = "assetAmt";
			pq.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);
			records = this.queryDraftInfos(pq, page);
		}

		if (records != null && records.size() > 0) {
			qr = QueryResult.buildQueryResult(records, amountFieldName);
		}
		return qr;
	}

	// 查询票据总额度
	private BigDecimal queryDraftEduByOrgcode(String orgcode) {
		String hql = "select sum(at.crdtTotal) from AssetType at,AssetPool ap where at.apId=ap.apId and at.astType='ED_10' and ap.custOrgcode=?";
		List param = new ArrayList();
		param.add(orgcode);
		List temp = this.find(hql, param);
		BigDecimal draftEdu = new BigDecimal("0");
		if (temp != null && temp.size() > 0) {
			Object[] obj = (Object[]) temp.get(0);
			draftEdu = new BigDecimal(obj[0].toString());
			return draftEdu;
		}
		return draftEdu;
	}

	public QueryResult queryBailTransDetails(PoolQueryBean queryNean) throws Exception {
		QueryResult qr = new QueryResult();
		List list = new ArrayList();
		if (queryNean.getQueryParam().equals("01")) {// 账户当日交易数据查询
			list = this.queryBailFlowDetails(queryNean);
		} else {// 账户历史数据查询
			list = this.queryBailHisDetails(queryNean);
		}
		if (list != null && list.size() > 0) {
			qr.setTotalCount(list.size());
			qr.setRecords(list);
		}
		return qr;
	}

	/**
	 * 保证金当日流水查询
	 * 
	 * @param @throws Exception
	 * @return List
	 * @author Ju Nana
	 * @date 2019-2-14 下午3:29:06
	 */
	private List queryBailFlowDetails(PoolQueryBean queryNean) throws Exception {
		String marginAccount = "";// 保证金账号
		String sql = "select pro.marginAccount from PedProtocolDto as pro where pro.custnumber=? and pro.poolAgreement=? ";
		List param = new ArrayList();
		param.add(queryNean.getSCustOrgCode());
		param.add(queryNean.getProtocolNo());
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			marginAccount = (String) this.find(sql, param).get(0);
		}

		String bailSql = "select bail from PedBailFlow as bail where bail.accNo=?  order by bail.timeMch desc ";
		List queryParam = new ArrayList();
		queryParam.add(marginAccount);
		List bailDetails = this.find(bailSql, queryParam);
		if (bailDetails != null && bailDetails.size() > 0) {
			return bailDetails;
		}
		return null;
	}

	/**
	 * 保证金历史交易查询
	 * 
	 * @param @throws Exception
	 * @return List
	 * @author Ju Nana
	 * @date 2019-2-14 下午3:29:31
	 */
	private List queryBailHisDetails(PoolQueryBean queryNean) throws Exception {
		String marginAccount = "";// 保证金账号
		String sql = "select pro.marginAccount from PedProtocolDto as pro where pro.custnumber=? and pro.poolAgreement=? ";
		List param = new ArrayList();
		param.add(queryNean.getSCustOrgCode());
		param.add(queryNean.getProtocolNo());
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			marginAccount = (String) this.find(sql, param).get(0);
		}

		StringBuffer bailSql = new StringBuffer("select bail from PedBailHis as bail where 1=1 ");
		List keys = new ArrayList();
		List values = new ArrayList();

		bailSql.append(" and bail.accNo=:accNo");
		keys.add("accNo");
		values.add(marginAccount);

		if (StringUtil.isNotBlank(queryNean.getStartDate())) {
			bailSql.append(" and bail.dateTran>=:dateTranStart");
			keys.add("dateTranStart");
			values.add(queryNean.getStartDate());
		}
		if (StringUtil.isNotBlank(queryNean.getEndDate())) {
			bailSql.append(" and bail.dateTran<=:dateTranEnd");
			keys.add("dateTranEnd");
			values.add(queryNean.getEndDate());
		}
		bailSql.append(" order by bail.dateTran desc ");

		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List bailDetails = this.find(bailSql.toString(), keis, values.toArray());

		if (bailDetails != null && bailDetails.size() > 0) {
			return bailDetails;
		}
		return null;
	}





	// PJC009:票据池-信贷产品查询接口
	public QueryResult queryUsedCreditProductPJC009(PoolQueryBean pool,Page page) throws Exception {
		QueryResult qr = new QueryResult();
		qr = qr.buildQueryResult(this.queryUsedCrdtProduct(pool,page), "loanAmount");
		return qr;
	}

	// PJC009：票据池-信贷产品查询接口 wfj 1228
	public List queryUsedCrdtProduct(PoolQueryBean pool,Page page) {
		List list = new ArrayList();
		List keys = new ArrayList(); // key
		List values = new ArrayList(); // value
		StringBuffer sql = new StringBuffer("select dto.crdtNo from CreditProduct dto where 1=1");
		
		sql.append(" and dto.custNo=:custNo");
		keys.add("custNo");
		values.add(pool.getSCustOrgCode());
		
		sql.append(" and dto.bpsNo=:bpsNo");
		keys.add("bpsNo");
		values.add(pool.getProtocolNo());

		if (StringUtil.isNotEmpty(pool.getCtrctNb())) {// 融资业务合同号
			sql.append(" and dto.crdtNo=:crdtNo");
			keys.add("crdtNo");
			values.add(pool.getCtrctNb());
		}
		if (StringUtil.isNotEmpty(pool.getQueryParam())) {// 融资业务类型
			sql.append(" and dto.crdtType=:crdtType");
			keys.add("crdtType");
			values.add(pool.getQueryParam());
		}
/*		if (StringUtil.isNotEmpty(pool.getReceiptNum())) {// 借据号
			sql.append(" and dto.iousNo=:iousNo");
			keys.add("iousNo");
			values.add(pool.getReceiptNum());
		}*/
		if (pool.getPstartDate() != null) { // 合同开始时间
			sql.append(" and to_char(dto.crdtIssDt,'yyyyMMdd')>=:crdtIssDt");
			keys.add("crdtIssDt");
			values.add(DateUtils.toString(pool.getPstartDate(), "yyyyMMdd"));
		}
		if (pool.getPendDate() != null) { // 合同结束时间
			sql.append(" and to_char(dto.crdtDueDt,'yyyyMMdd')<=:crdtDueDt");
			keys.add("crdtDueDt");
			values.add(DateUtils.toString(pool.getPendDate(), "yyyyMMdd"));
		}
		if (pool.getMoveType() != null) {//结清标识
			sql.append(" and dto.sttlFlag=:sttlFlag");
			keys.add("sttlFlag");
			values.add(pool.getMoveType());
		}
		sql.append(" order by dto.crdtIssDt desc ");
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		list = this.find(sql.toString(), keis, values.toArray());

		if(list!=null && list.size()>0){
				StringBuffer hql = new StringBuffer("select pcd from PedCreditDetail as pcd where 1=1 ");
				List paramKeys = new ArrayList();
				List paramValues = new ArrayList();
				if (list != null && list.size() > 0) {
					hql.append(" and pcd.crdtNo in(:crdtNos)");
					paramKeys.add("crdtNos");
					paramValues.add(list);
				}
		
				if (StringUtil.isNotEmpty(pool.getReceiptNum())) {// 借据号
					hql.append(" and pcd.loanNo=:loanNo");
					paramKeys.add("loanNo");
					paramValues.add(pool.getReceiptNum());
				}
		
				if (pool.getStartContract() != null) {// 借据起始日
					hql.append(" and to_char(pcd.startTime,'yyyyMMdd')=:startTime");
					paramKeys.add("startTime");
					paramValues.add(DateUtils.toString(pool.getStartContract(), "yyyyMMdd"));
				}
				if (pool.getEndContract() != null) {// 借据到期日
					hql.append(" and to_char(pcd.endTime,'yyyyMMdd')=:endTime");
					paramKeys.add("endTime");
					paramValues.add(DateUtils.toString(pool.getEndContract(), "yyyyMMdd"));
				}
				if (pool.getReceiptMax() != null) { // 票据金额最大值
					hql.append(" and pcd.loanAmount<=:maxLloanAmount");
					paramKeys.add("maxLloanAmount");
					paramValues.add(pool.getReceiptMax());
				}
				if (pool.getReceiptMin() != null) { // 票据金额最小值
					hql.append(" and pcd.loanAmount>=:minLoanAmount");
					paramKeys.add("minLoanAmount");
					paramValues.add(pool.getReceiptMin());
				}
		
				/* 以借款日期排序 */
				hql.append(" order by pcd.transTime desc ");
				String[] ks = (String[]) paramKeys.toArray(new String[paramKeys.size()]);
				List result = this.find(hql.toString(), ks, paramValues.toArray(),page);
				if (result != null && result.size() > 0) {
					return result;
				}
				return null;
		}
		return null;
	}

	public void queryAccTransByOrgcode(String cusCommid, PoolQueryBean pool) {
		AccTrans acctrans = new AccTrans();
		acctrans.setTransType(pool.getMoveType()); // 划转类型
		acctrans.setCusCommid(cusCommid); // 组织机构代码
		acctrans.setCusCommname(pool.getSCustName());// 客户名称
		acctrans.setFromAccnumber(pool.getPaymentAccount());// 付款账号
		acctrans.setTransAmt(pool.getMoveMoney()); // 划转金额
		acctrans.setToAccnumber(pool.getMoveAccount()); // 转入账号
		if (pool.getBdPeriod() != null && !"".equals(pool.getBdPeriod())) {
			acctrans.setToAcclimit(Integer.parseInt(pool.getBdPeriod())); // 定期期限，网银传来的是String类型，转换成Integer类型的
		}
		acctrans.setUsage(pool.getUsage());// 用途：划转类型
		acctrans.setRemark(pool.getRemark());// 备注
		acctrans.setTransStatus(PoolComm.MARGIN_TRANSFER_STATUS_1);// 交易状态:保证金预划转
		acctrans.setOperTime(new Date());// 交易时间

		// 保存实体
		this.txStore(acctrans);
	}


	/**
	 * CustomerService中的方法
	 */
	public String queryOrgCodeBySCustAcc(String sCustAcc) {
		String orgCode = null;
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append(
				"select customer from CustomerDto  customer ,AccountDto ato where customer.pkIxBoCustomerId = ato.SCustId ");
		if (null != sCustAcc) {
			sb.append(" and ato.SAccountNo =?");
			param.add(sCustAcc.trim());
		}
		List result = this.find(sb.toString(), param);
		if (null != result && result.size() > 0) {
			CustomerDto cus = (CustomerDto) result.get(0);
			orgCode = cus.getSOrgCode();
		}
		return orgCode;
	}

	/**
	 * 20180912 yangYu 判断客户票据池是否开通标识 params1 String 企业签约结算账户 params2 核心客户号 result
	 * PedProtocolDto
	 */
	public PedProtocolDto queryPedProtocolDtoByAccount(String SAccountNo, List custAccts, String custNum)
			throws Exception {

		List keys = new ArrayList();
		List values = new ArrayList();
		if (StringUtils.isNotEmpty(SAccountNo) || custAccts != null && custAccts.size() > 0
				|| StringUtils.isNotEmpty(custNum)) {
			StringBuffer hql = new StringBuffer("select pp from PedProtocolDto pp,CustomerDto cd,AccountDto ad "
					+ "where pp.custnumber=cd.custNum and ad.SCustId=cd.pkIxBoCustomerId and pp.openFlag=:openFlag ");
			if (StringUtils.isNotEmpty(SAccountNo)) {
				hql.append(" and ad.SAccountNo=:SAccountNo");
				keys.add("SAccountNo");
				values.add(SAccountNo);

			}
			if (custAccts != null && custAccts.size() > 0) {
				hql.append(" and ad.SAccountNo in (:custAccts)");
				keys.add("custAccts");
				values.add(custAccts);
			}
			if (StringUtils.isNotEmpty(custNum)) {
				hql.append(" and cd.custNum=:custNum");
				keys.add("custNum");
				values.add(custNum);

			}

			keys.add("openFlag");
			values.add(PoolComm.OPEN_01);

			String[] keis = (String[]) keys.toArray(new String[keys.size()]);
			List resList = this.find(hql.toString(), keis, values.toArray());

			if (resList != null && resList.size() > 0) {
				return (PedProtocolDto) resList.get(0);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public PedProtocolDto queryPedProtocolDtoByAccount(String SAccountNo, String custNum) throws Exception {
		return queryPedProtocolDtoByAccount(SAccountNo, null, custNum);
	}


	@Override
	public String txPedProtocolDtoPJC010(PedProtocolDto protocolDto) throws Exception {
		String ebankFlag = protocolDto.getEbankFlag();// 签约标识
		String agreementNo = "";// 票据池编号
		String PoolInfoId =null;//主键ID
		Date sysDate =DateUtils.formatDate(new Date(),DateUtils.ORA_DATE_FORMAT);//机器时间
		if ("01".equals(ebankFlag)) {// 签约
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setCustnumber(protocolDto.getCustnumber());
			queryBean.setIsGroup(PoolComm.NO);
			PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			PedProtocolDto pro = new PedProtocolDto();
			if (null!= pedProtocolDto) {// 非初次签约沿用原有票据池编号
				agreementNo = pedProtocolDto.getPoolAgreement();
				PoolInfoId=pedProtocolDto.getPoolInfoId();
				reflectClassValueToNull(pedProtocolDto);//字段属性清空
				pro=pedProtocolDto;
			} else {// 初次签约自动生成新的票据池编号
				agreementNo = poolBatchNoUtils.txGetBatchNoBySession("DR", 6);
			}
			pro.setPoolInfoId(PoolInfoId);
			pro.setCustnumber(protocolDto.getCustnumber());//客户号
			pro.setCustOrgcode(protocolDto.getCustOrgcode());//组织机构代码
			pro.setPoolAgreement(agreementNo);//票据池编号
			pro.setElecDraftAccount(protocolDto.getElecDraftAccount());//电票账号
			pro.setElecDraftAccountName(protocolDto.getElecDraftAccountName());//电票账号名称
			pro.setCustname(protocolDto.getCustname());//客户名称
			pro.setEbankFlag(ebankFlag);
			pro.setPoolName("票据池（" + protocolDto.getCustname() + "）");// 票据池名称
 			pro.setBusiType(protocolDto.getBusiType());// 业务类型 02虚拟票据池
			pro.setSigningFunction(protocolDto.getSigningFunction()); //签约功能  01：票据账务管家
			pro.setPoolMode(protocolDto.getPoolMode());// 池模式 01总量控制
			pro.setIsGroup(protocolDto.getIsGroup());// 是否集团
			pro.setEffstartdate(sysDate);// 票据池协议生效日期
			pro.setEffenddate(DateUtils.formatDate(DateUtils.getNextNMonth(sysDate,12),DateUtils.ORA_DATE_FORMAT));// 票据池协议到期日
			pro.setAuthperson(protocolDto.getAuthperson());// 客户经办人身份证号
			pro.setLicename(protocolDto.getLicename());// 客户经办人名称
			pro.setPhonenumber(protocolDto.getPhonenumber());// 客户经办人手机号
			pro.setSignDeptNo(protocolDto.getSignDeptNo());//签约机构号
			pro.setSignDeptName(protocolDto.getSignDeptName());//签约机构名称
			pro.setOfficeNet(protocolDto.getSignDeptNo());//受理网点号--默认赋值签约机构
			pro.setOfficeNetName(protocolDto.getSignDeptName());//受理网点名称
			pro.setXyflag(PoolComm.YES);
			pro.setvStatus(PoolComm.VS_01);
			pro.setOperateTime(new Date());
			pro.setOpenFlag(PoolComm.OPEN_00);// 未开通
			pro.setFrozenstate(PoolComm.FROZEN_STATUS_00);
			pro.setApproveFlag(null);// 初始化
			this.dao.merge(pro);
			//票据池客户信息数据落库
			CustomerRegister customer=new CustomerRegister();
			customer.setCustNo(protocolDto.getCustnumber());
			customer.setCustName(protocolDto.getCustname());
			customer.setFirstDateSource("PJC010");
			customerService.txSaveCustomerRegister(customer);
		}

		if ("03".equals(ebankFlag)) {// 修改----只有电票签约账号可以改   增加保证金账号
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setCustnumber(protocolDto.getCustnumber());
			queryBean.setPoolAgreement(protocolDto.getPoolAgreement());
			queryBean.setIsGroup(PoolComm.NO);
			PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);

			pedProtocolDto.setElecDraftAccount(protocolDto.getElecDraftAccount());
			pedProtocolDto.setElecDraftAccountName(protocolDto.getElecDraftAccountName());
			pedProtocolDto.setOperateTime(new Date());
			this.txStore(pedProtocolDto);
			agreementNo = pedProtocolDto.getPoolAgreement();
		}
		if ("02".equals(ebankFlag)) {// 解约
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setCustnumber(protocolDto.getCustnumber());
			queryBean.setPoolAgreement(protocolDto.getPoolAgreement());
			queryBean.setIsGroup(PoolComm.NO);
			PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);

			pedProtocolDto.setvStatus(PoolComm.VS_02);
			pedProtocolDto.setOpenFlag(PoolComm.OPEN_02);
			pedProtocolDto.setOperateTime(new Date());
			this.txStore(pedProtocolDto);
			agreementNo = pedProtocolDto.getPoolAgreement();
		}
		return agreementNo;

	}

	@Override
	public List<PedProtocolDto> queryProtocolPjc011(String coreCustnumber, String poolAgreement) throws Exception {
		String sql = "select pedProtocolDto from PedProtocolDto as pedProtocolDto where 1=1 ";
		List<String> param = new ArrayList<String>();

		if (StringUtils.isNotEmpty(poolAgreement)) {
			sql = sql + " and pedProtocolDto.poolAgreement = ? ";
			param.add(poolAgreement);
		}
		if (StringUtils.isNotEmpty(coreCustnumber)) {
			sql = sql + " and pedProtocolDto.custnumber = ? ";
			param.add(coreCustnumber);
		}

		List<PedProtocolDto> result = this.find(sql, param);
		if (result != null && result.size() > 0) {
			return result;
		}
		return null;

	}

	@Override
	public void txBlackListHandler(PedBlackList black, String doType) throws Exception {
		if (doType.equals("01")) {// 新增
			this.txStore(black);
		} else if (doType.equals("02")) {// 修改
			PedBlackList black1 = (PedBlackList) blackListManageService.load(black.getId());
			black1.setId(black.getId());
			black1.setType(black.getType());
			black1.setKeywords(black.getKeywords());
			black1.setContent(black.getContent());
			// YeCheng 修改的时候重新赋值省份及城市
			black1.setProvince(black.getProvince());
			black1.setCity(black.getCity());
			black1.setDueDt(black.getDueDt());
			black1.setDataFrom(black.getDataFrom());
			black1.setCreateTime(black.getCreateTime());
			this.txStore(black1);
		} else if (doType.equals("03")) {// 删除
			PedBlackList black2 = (PedBlackList) blackListManageService.load(black.getId());
			this.txDelete(black2);
		} else {
			throw new Exception("不支持这种类型的操作!");
		}
	}

	@Override
	public List queryBlackListPJC018(String orgCode, String type) throws Exception {

		List blackList = new ArrayList();
		List keys = new ArrayList();
		List values = new ArrayList();
		List res = new ArrayList();
		StringBuffer hql = new StringBuffer("from PedBlackList black where dataFrom='00' ");

		if (StringUtil.isNotEmpty(orgCode)) {
			hql.append(" and black.orgCode=:orgCode");
			keys.add("orgCode");
			values.add(orgCode);
		}
		if (StringUtil.isNotEmpty(type)) {
			hql.append(" and black.type=:type");
			keys.add("type");
			values.add(type);
		}
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List result = this.find(hql.toString(), keis, values.toArray());
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {

				PedBlackList black = (PedBlackList) result.get(i);
				Map blackMap = new HashMap();
				blackMap.put("BUSINESS_ARRAY.KEY_WORDS", black.getKeywords());// 内容
				if (StringUtil.isNotBlank(black.getKeywords())
						&& StringUtils.equals(black.getKeywords(), PoolComm.KEY_WAYS_04)) {
					blackMap.put("BUSINESS_ARRAY.CONTENT", black.getProvince()+";"+black.getCity());// 关键字
				} else {
					blackMap.put("BUSINESS_ARRAY.CONTENT", black.getContent());// 关键字
				}
				blackMap.put("BUSINESS_ARRAY.VALIDITY_DATE", black.getDueDt());// 到期日
				blackMap.put("BUSINESS_ARRAY.BUSS_TYPE", black.getType());// 名单类型
				blackMap.put("BUSINESS_ARRAY.SERIAL_NO", black.getId());// 主键ID

				blackList.add(blackMap);
			}

		}
		return blackList;
	}

	@Override
		public List<Map> queryNotifySummaryPJC015(String custNo,String bpsNO) throws Exception {
		List reList = new ArrayList();
		
		
		//（1）待办事项一：融资业务到期还款申请，根据票据池编号与核心客户号查询pedcheck表
		StringBuffer hql1 = new StringBuffer(
				"select pd from PedCreditDetail pd where pd.crdtNo in (select cp.crdtNo from CreditProduct cp where cp.bpsNo = '" + bpsNO +
				"' and cp.custNo = '" + custNo + "') and pd.endTime <= ?  and pd.loanStatus<>'JJ_04' and pd.loanStatus<>'JJ_05' ");
		List param1 = new ArrayList();
		param1.add(DateUtils.modDay(new Date(), 7));// 当前日期加7天后的日期
		List result1 = this.find(hql1.toString(), param1);
		if(result1!=null && result1.size()>0){
			for (int i = 0; i < result1.size(); i++) {
				PedCreditDetail pedCreditDetail = (PedCreditDetail) result1.get(i);
				HashMap map = new HashMap();
				map.put("BUSINESS_ARRAY.BUSS_TYPE", "DB_01"); // 代办业务类型 --融资业务到期还款提醒
				map.put("BUSINESS_ARRAY.EXPIRY_DATE",pedCreditDetail.getEndTime()); //到期日
				map.put("BUSINESS_ARRAY.RELATE_AMT", pedCreditDetail.getLoanAmount()); // 金额
				map.put("BUSINESS_ARRAY.IOU_BAL_AMT", pedCreditDetail.getActualAmount()); // 借据余额
				map.put("BUSINESS_ARRAY.IOU_NO", pedCreditDetail.getLoanNo()); // 借据编号
				map.put("BUSINESS_ARRAY.CONTRACT_NO",pedCreditDetail.getCrdtNo()); //合同号
				map.put("BUSINESS_ARRAY.BUSS_CATEGORY",pedCreditDetail.getLoanType()); //业务品种
				reList.add(map);
			}
		}
		
		//（2）代办事项二:票据池对账,根据票据池编号与核心客户号查询pedcheck表
		StringBuffer hql2 = new StringBuffer("select ch from PedCheck ch where ch.poolAgreement = ? and ch.custNo = ? order by ch.accountDate desc , ch.curTime desc ");
		List param2 = new ArrayList();
		param2.add(bpsNO);
		param2.add(custNo);
		List result2 = this.find(hql2.toString(), param2);
		
		if(result2!=null && result2.size()>0){
			PedCheck ch = (PedCheck) result2.get(0);
			if(PoolComm.DZJG_00.equals(ch.getCheckResult())){//未对账
				HashMap map = new HashMap();
				map.put("BUSINESS_ARRAY.BUSS_TYPE", "DB_03"); // 代办业务类型--票据池对账
				map.put("BUSINESS_ARRAY.RECON_BATCH_NO", ch.getBatchNo());//对账批次号
				reList.add(map);
			}
		}
		
		//（3）代办事项三：票据池年费收取
		StringBuffer hql3 = new StringBuffer("select pro from PedProtocolDto pro where  pro.poolAgreement= ? " +
				" and pro.feeType ='"+PoolComm.SFMS_01+"' and pro.feeDueDt < ? " );
		List param3 = new ArrayList();
		param3.add(bpsNO);
		param3.add(DateTimeUtil.getWorkday());
		List result3 = this.find(hql3.toString(), param3);
		if(result3!=null && result3.size()>0){
			PlFeeScale feeSc = pedProtocolService.queryFeeScale();
			BigDecimal fee = new BigDecimal("0");//收取年费标准
			if(feeSc!=null){
				fee = feeSc.getEveryYear();
			}

			HashMap map = new HashMap();
			map.put("BUSINESS_ARRAY.BUSS_TYPE", "DB_04"); // 代办业务类型--年费收取
			map.put("BUSINESS_ARRAY.ANNUAL_FEE_STANDARD", fee);//年费收取标准
			
			/*
			 * 只有单户或者集团主户可以查收费标准
			 */
			ProtocolQueryBean dtoBean = new ProtocolQueryBean();
			dtoBean.setPoolAgreement(bpsNO);
			dtoBean.setCustnumber(custNo);
			PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(dtoBean);
			if(dto!=null){				
				reList.add(map);
			}
			
			
		}
		
		
		if(reList!=null && reList.size()>0){
			return reList;
		}
		return null;
	}

	@Override
	public Map queryDraftFrontPJC016(String billNo,String beginRangeNo,String endRangeNo) throws Exception {
		List keys = new ArrayList();
		List values = new ArrayList();
		Map map = new HashMap();
		StringBuffer hql = new StringBuffer("from PoolBillInfo info where 1=1");

		hql.append(" and info.SBillNo=:SBillNo");
		keys.add("SBillNo");
		values.add(billNo);
		
		/********************融合改造新增 start******************************/
		hql.append(" and info.beginRangeNo=:beginRangeNo");
		keys.add("beginRangeNo");
		values.add(beginRangeNo);
		
		hql.append(" and info.endRangeNo=:endRangeNo");
		keys.add("endRangeNo");
		values.add(endRangeNo);
		/********************融合改造新增 end******************************/
		
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List result = this.find(hql.toString(), keis, values.toArray());

		if (result != null && result.size() > 0) {
			PoolBillInfo pool = (PoolBillInfo) result.get(0);
			return poolBillInfoDataHandler(pool);
		} else {
			StringBuffer hql2 = new StringBuffer("select pv from PoolVtrust pv where pv.vtNb = '" + billNo + "'" + " and pv.beginRangeNo = '" + beginRangeNo+ "' " + " and pv.endRangeNo = '" + endRangeNo+ "' ");
			List result2 = this.find(hql2.toString());
			if (result2 != null && result2.size() > 0) {
				PoolVtrust pool = (PoolVtrust) result2.get(0);
				return poolVtrustDataHandler(pool);
			}else {
				StringBuffer hql3 = new StringBuffer("select dam from DraftAccountManagement dam where dam.draftNb = '" + billNo + "'"+ " and dam.beginRangeNo = '" + beginRangeNo+ "' " + " and dam.endRangeNo = '" + endRangeNo+ "' ");
				List result3 = this.find(hql3.toString());
				if (result3 != null && result3.size() > 0) {
					DraftAccountManagement draftAccountManagement = (DraftAccountManagement) result3.get(0);
					return poolManagementDataHandler(draftAccountManagement);
				}
			}
		}
		return map;
	}

	private Map poolVtrustDataHandler(PoolVtrust billInfo) {
		Map infoMap = new HashMap();
		infoMap.put("SERIAL_NO", "");// BBSP票据ID
		infoMap.put("BILL_NO", billInfo.getVtNb());// 票据号码
		infoMap.put("BILL_CLASS", billInfo.getVtType());// 票据种类
		infoMap.put("BILL_TYPE", billInfo.getVtDraftMedia());// 票据介质
		infoMap.put("BILL_AMT", billInfo.getVtisseAmt());// 票据金额
		infoMap.put("DRAW_DATE", billInfo.getVtisseDt());// 出票日
		infoMap.put("EXPIRY_DATE", billInfo.getVtdueDt());// 到期日
		infoMap.put("TRANSFER_FLAG", billInfo.getVtTranSfer());// 能否转让标记
		infoMap.put("BILL_NAME", billInfo.getVtdrwrName());// 出票人全称
		infoMap.put("BILL_ACCT_NO", billInfo.getVtdrwrAccount());// 出票人帐号
		infoMap.put("REMITTER_OPEN_BANK", billInfo.getVtdrwrBankNumber());// 出票人开户行行号
		infoMap.put("BILL_OPENBANK_NAME", billInfo.getVtdrwrBankName());// 出票人开户行名称
		infoMap.put("PAYEE_NAME", billInfo.getVtpyeeName());// 收款人全称
		infoMap.put("PAYEE_ACCT_NO", billInfo.getVtpyeeAccount());// 收款人帐号
		infoMap.put("PAYEE_OPEN_BRANCH", billInfo.getVtpyeeBankAccount());// 收款人开户行行号
		infoMap.put("PAYEE_OPENBANK_NAME", billInfo.getVtpyeeBankName());// 收款人开户行名称
		infoMap.put("ACCE_DATE", billInfo.getVtaccptrDate());// 承兑日期
		infoMap.put("ACCEPTOR_NAME", billInfo.getVtaccptrName());// 承兑人全称

		if("1".equals(billInfo.getVtDraftMedia())){//纸票-给付款人赋值
			infoMap.put("PAYER_OPEN_BANK", billInfo.getVtaccptrBankAccount());// 付款人开户行行号
			infoMap.put("PAYER_OPEN_BANK_ADDR", billInfo.getVtaccptrBankAddr());// 付款人开户行地址
		}else{//电票-给承兑人赋值
			infoMap.put("ACCEPTOR_ACCT_NO", billInfo.getVtaccptrAccount());// 承兑人帐号
			infoMap.put("ACCEPTOR_OPENBANK_NAME", billInfo.getVtaccptrBankName());// 承兑人开户行全称
			infoMap.put("ACCEPTOR_OPEN_BANK", billInfo.getVtaccptrBankAccount());// 承兑人开户行行号			
		}
		
		infoMap.put("ACCEPTANCE_AGREE_NO", "");// 承兑协议编号
		infoMap.put("CONTRACT_NO", billInfo.getContractNo());// 合同号
		infoMap.put("DRAWER_GUARANTOR_NAME", billInfo.getDrwrGuarntrNm());// 出票保证人名称
		infoMap.put("DRAWER_GUARANTOR_ADDRESS", billInfo.getDrwrGuarntrAddr());// 出票保证人地址
		infoMap.put("DRAWER_GUARANTEE_DATE", billInfo.getDrwrGuarntrDt());// 出票保证日期
		infoMap.put("ACCEPTANCE_GUARANTOR_NAME", billInfo.getAccptrGuarntrNm());// 承兑保证人名称
		infoMap.put("ACCEPTANCE_GUARANTOR_ADDRESS", billInfo.getAccptrGuarntrAddr());// 承兑保证人地址
		infoMap.put("ACCEPTANCE_GUARANTEE_DATE", billInfo.getAccptrGuarntrDt());// 承兑保证日期
		infoMap.put("BILL_SAVE_ADDR", billInfo.getBillPosition());// 票据保管地
		infoMap.put("OTHER_BANK_SAVE_ADDR", billInfo.getBillPositionAddr());// 他行保管地址
		infoMap.put("REMARK", billInfo.getRemarks());// 备注
		infoMap.put("BILL_STATUS", "");// 票据状态
		infoMap.put("BILL_STATUS_NAME", "虚拟票据池录入票据");// 票据状态名称
		infoMap.put("ACCEPTANCE_AGREE_NO", billInfo.getAcceptanceAgreeNo());// 承兑协议编号
		
		infoMap.put("BEGIN_RANGE_NO", billInfo.getBeginRangeNo());//  票据号起
		infoMap.put("END_RANGE_NO", billInfo.getEndRangeNo());//票据号止

		return infoMap;
	}

	private Map poolBillInfoDataHandler(PoolBillInfo billInfo) {
		Map infoMap = new HashMap();
		infoMap.put("SERIAL_NO", billInfo.getDiscBillId());// BBSP票据ID
		infoMap.put("BILL_NO", billInfo.getSBillNo());// 票据号码
		infoMap.put("BILL_CLASS", billInfo.getSBillType());// 票据种类
		infoMap.put("BILL_TYPE", billInfo.getSBillMedia());// 票据介质
		infoMap.put("BILL_AMT", billInfo.getFBillAmount());// 票据金额
		infoMap.put("DRAW_DATE", billInfo.getDIssueDt());// 出票日
		infoMap.put("EXPIRY_DATE", billInfo.getDDueDt());// 到期日
		if ("1".equals(billInfo.getSBanEndrsmtFlag())) {// 不得转让
			infoMap.put("TRANSFER_FLAG", "0");// 不得转让
		} else {
			infoMap.put("TRANSFER_FLAG", "1");// 可转让
		}
		infoMap.put("BILL_NAME", billInfo.getSIssuerName());// 出票人全称
		infoMap.put("BILL_ACCT_NO", billInfo.getSIssuerAccount());// 出票人帐号
		infoMap.put("REMITTER_OPEN_BANK", billInfo.getSIssuerBankCode());// 出票人开户行行号
		infoMap.put("BILL_OPENBANK_NAME", billInfo.getSIssuerBankName());// 出票人开户行名称
		infoMap.put("PAYEE_NAME", billInfo.getSPayeeName());// 收款人全称
		infoMap.put("PAYEE_ACCT_NO", billInfo.getSPayeeAccount());// 收款人帐号
		infoMap.put("PAYEE_OPEN_BRANCH", billInfo.getSPayeeBankCode());// 收款人开户行行号
		infoMap.put("PAYEE_OPENBANK_NAME", billInfo.getSPayeeBankName());// 收款人开户行名称
		infoMap.put("ACCEPTOR_NAME", billInfo.getSAcceptor());// 承兑人全称
		infoMap.put("ACCEPTOR_ACCT_NO", billInfo.getSAcceptorAccount());// 承兑人帐号
		infoMap.put("ACCEPTOR_OPENBANK_NAME", billInfo.getSAcceptorBankName());// 承兑人开户行全称
		infoMap.put("ACCEPTOR_OPEN_BANK", billInfo.getSAcceptorBankCode());// 承兑人开户行行号
		infoMap.put("ACCE_DATE", billInfo.getSAcceptorDt());// 承兑日期
		infoMap.put("PAYER_OPEN_BANK","");// 付款人开户行行号
		infoMap.put("PAYER_OPEN_BANK_ADDR", "");// 付款人开户行地址
		infoMap.put("ACCEPTANCE_AGREE_NO", billInfo.getSAcceptorProto());// 承兑协议编号
		infoMap.put("CONTRACT_NO", billInfo.getSContractNo());// 合同号
		infoMap.put("DRAWER_GUARANTOR_NAME", "");// 出票保证人名称
		infoMap.put("DRAWER_GUARANTOR_ADDRESS", "");// 出票保证人地址
		infoMap.put("DRAWER_GUARANTEE_DATE", "");// 出票保证日期
		infoMap.put("ACCEPTANCE_GUARANTOR_NAME", "");// 承兑保证人名称
		infoMap.put("ACCEPTANCE_GUARANTOR_ADDRESS", "");// 承兑保证人地址
		infoMap.put("ACCEPTANCE_GUARANTEE_DATE", "");// 承兑保证日期
		infoMap.put("BILL_SAVE_ADDR", "");// 票据保管地
		infoMap.put("OTHER_BANK_SAVE_ADDR", "");// 他行保管地址
		infoMap.put("REMARK", "");// 备注
		infoMap.put("BILL_STATUS", billInfo.getSDealStatus());// 票据状态
		infoMap.put("BILL_STATUS_NAME", "");// 票据状态名称
		infoMap.put("ACCEPTANCE_AGREE_NO", "");// 合同号

		return infoMap;
	}

	private Map poolManagementDataHandler(DraftAccountManagement billInfo) {
		Map infoMap = new HashMap();
		infoMap.put("SERIAL_NO", "");// BBSP票据ID
		infoMap.put("BILL_NO", billInfo.getDraftNb());// 票据号码
		infoMap.put("BILL_CLASS", billInfo.getDraftType());// 票据种类
		infoMap.put("BILL_TYPE", billInfo.getDraftMedia());// 票据介质
		infoMap.put("BILL_AMT", billInfo.getIsseAmt());// 票据金额
		infoMap.put("DRAW_DATE", billInfo.getIsseDt());// 出票日
		infoMap.put("EXPIRY_DATE", billInfo.getDueDt());// 到期日
		if(billInfo.getEdBanEndrsmtMk().equals("0")){//账务管家表转让标识  0可转让  返给网银 1可转让
			infoMap.put("TRANSFER_FLAG", "1");// 可转让
		}else if (billInfo.getEdBanEndrsmtMk().equals("1")){
			infoMap.put("TRANSFER_FLAG", "0");// 不可转让
		}
		infoMap.put("BILL_NAME", billInfo.getDrwrNm());// 出票人全称
		infoMap.put("BILL_ACCT_NO", billInfo.getDrwrAcctId());// 出票人帐号
		infoMap.put("REMITTER_OPEN_BANK", billInfo.getDrwrAcctSvcr());// 出票人开户行行号
		infoMap.put("BILL_OPENBANK_NAME", billInfo.getDrwrAcctSvcrNm());// 出票人开户行名称
		infoMap.put("PAYEE_NAME", billInfo.getPyeeNm());// 收款人全称
		infoMap.put("PAYEE_ACCT_NO", billInfo.getPyeeAcctId());// 收款人帐号
		infoMap.put("PAYEE_OPEN_BRANCH", billInfo.getPyeeAcctSvcr());// 收款人开户行行号
		infoMap.put("PAYEE_OPENBANK_NAME", billInfo.getPyeeAcctSvcrNm());// 收款人开户行名称
		infoMap.put("ACCEPTOR_NAME", billInfo.getAccptrNm());// 承兑人全称
		infoMap.put("ACCEPTOR_ACCT_NO", billInfo.getAccptrId());// 承兑人帐号
		infoMap.put("ACCEPTOR_OPENBANK_NAME", billInfo.getAccptrSvcr());// 承兑人开户行全称
		infoMap.put("ACCEPTOR_OPEN_BANK", billInfo.getAccptrSvcrNm());// 承兑人开户行行号
		infoMap.put("ACCE_DATE", "");// 承兑日期
		infoMap.put("PAYER_OPEN_BANK","");// 付款人开户行行号
		infoMap.put("PAYER_OPEN_BANK_ADDR", "");// 付款人开户行地址
		infoMap.put("ACCEPTANCE_AGREE_NO", billInfo.getAcceptanceAgreeNo());// 承兑协议编号
		infoMap.put("CONTRACT_NO", billInfo.getContractNo());// 合同号
		infoMap.put("DRAWER_GUARANTOR_NAME", "");// 出票保证人名称
		infoMap.put("DRAWER_GUARANTOR_ADDRESS", "");// 出票保证人地址
		infoMap.put("DRAWER_GUARANTEE_DATE", "");// 出票保证日期
		infoMap.put("ACCEPTANCE_GUARANTOR_NAME", "");// 承兑保证人名称
		infoMap.put("ACCEPTANCE_GUARANTOR_ADDRESS", "");// 承兑保证人地址
		infoMap.put("ACCEPTANCE_GUARANTEE_DATE", "");// 承兑保证日期
		infoMap.put("BILL_SAVE_ADDR", "");// 票据保管地
		infoMap.put("OTHER_BANK_SAVE_ADDR", "");// 他行保管地址
		infoMap.put("REMARK", "");// 备注
		infoMap.put("BILL_STATUS", "");// 票据状态
		infoMap.put("BILL_STATUS_NAME", "");// 票据状态名称
		infoMap.put("BEGIN_RANGE_NO", billInfo.getBeginRangeNo());//  票据号起
		infoMap.put("END_RANGE_NO", billInfo.getEndRangeNo());//票据号止

		return infoMap;
	}
	@Override
	public List queryDraftBackPJC017(String billNo,String beginRangeNo,String endRangeNo) throws Exception {
		List endorsementList = new ArrayList();
		// 根据票号查询票据ID
		EndorsementLog endorsementLog = new EndorsementLog();
		String sql = "select el from EndorsementLog as el where el.ownerEDraft = '" + billNo + "' and el.beginRangeNo  = '" + beginRangeNo + "' and el.endRangeNo  = '" + endRangeNo+"' ";
		List details = this.find(sql);

		if (details != null && details.size() > 0) {
			for (int i = 0; i < details.size(); i++) {
				EndorsementLog endorsement = (EndorsementLog) details.get(i);
				Map endorsementMap = new HashMap();
				
				endorsementMap.put("BILL_INFO_ARRAY.BILL_NO", endorsement.getOwnerEDraft());// 票据号码
				if(null!=endorsement.getMsgTpId()){
					endorsementMap.put("BILL_INFO_ARRAY.TRAN_TYPE", endorsement.getMsgTypeText());// 交易类别代码
				}else{
					endorsementMap.put("BILL_INFO_ARRAY.TRAN_TYPE", "");// 交易类别代码
				}
				endorsementMap.put("BILL_INFO_ARRAY.ENDORSER_NAME", endorsement.getEndrsrNm());// 背书人名称
				endorsementMap.put("BILL_INFO_ARRAY.ENDORSEE_NAME", endorsement.getEndrseeNm());// 被背书人名称
				endorsementMap.put("BILL_INFO_ARRAY.TRAN_APPLY_DATE", endorsement.getReqDate());// 交易申请日期
				endorsementMap.put("BILL_INFO_ARRAY.TRAN_REPLY_DATE", endorsement.getDate());// 交易回复日期
				
				endorsementMap.put("BILL_INFO_ARRAY.START_BILL_NO", endorsement.getBeginRangeNo());// 票据号起
				endorsementMap.put("BILL_INFO_ARRAY.END_BILL_NO", endorsement.getEndRangeNo());// 票据号止
				

				endorsementList.add(endorsementMap);
			}
		}else{//虚拟票据池录入票据的背面信息
			 List<EndorsementLog> vtList = new ArrayList<EndorsementLog>();
			 vtList = queryVtrustEndor(billNo,beginRangeNo,endRangeNo);
			 endorsementList.addAll(vtList);
		}
		
		return endorsementList;

	}
	private List<EndorsementLog> queryVtrustEndor(String billNo,String beginRangeNo,String endRangeNo)throws Exception{
		
		List vtList = new ArrayList();
		
		StringBuffer hql = new StringBuffer("from PoolVtrust as dto where 1=1");
		List keys = new ArrayList();
		List values = new ArrayList();
		
		if (StringUtil.isNotBlank(billNo)) {// 票据号码
			hql.append(" and dto.vtNb=:vtNb");
			keys.add("vtNb");
			values.add(billNo);
		}
		if (StringUtil.isNotBlank(beginRangeNo)) {// 票据号码起
			hql.append(" and dto.beginRangeNo=:beginRangeNo");
			keys.add("beginRangeNo");
			values.add(beginRangeNo);
		}
		if (StringUtil.isNotBlank(endRangeNo)) {// 票据号码至
			hql.append(" and dto.endRangeNo=:endRangeNo");
			keys.add("endRangeNo");
			values.add(endRangeNo);
		}
		
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List rsut = this.find(hql.toString(), keis, values.toArray(), null);
		if(rsut!=null && rsut.size()>0){
			PoolVtrust vtrust = (PoolVtrust)rsut.get(0);
			if(StringUtil.isNotBlank(vtrust.getEndorserInfo())){
				String[] endorserInfos = vtrust.getEndorserInfo().split("\\,");//分隔符为英文逗号
				if(endorserInfos!=null && endorserInfos.length>0){
					logger.info("背书人数组的长度【"+endorserInfos.length+"】");
					int count = endorserInfos.length;
					for(int i=0;i<count;i++){
						logger.info("背书人数组循环第【"+i+"】次");
						Map endorsementMap = new HashMap();
						endorsementMap.put("BILL_INFO_ARRAY.BILL_NO", billNo);// 票据号码
						
						endorsementMap.put("BILL_INFO_ARRAY.END_BILL_NO", vtrust.getBeginRangeNo());// 票据号起
						endorsementMap.put("BILL_INFO_ARRAY.END_BILL_NO", vtrust.getEndRangeNo());// 票据号止
						
						if(StringUtil.isNotBlank(endorserInfos[i])){
							endorsementMap.put("BILL_INFO_ARRAY.ENDORSER_NAME",endorserInfos[i]);// 背书人名称							
						}else{
							endorsementMap.put("BILL_INFO_ARRAY.ENDORSER_NAME","");// 背书人名称
						}
						if(i<(count-1)){
							if(StringUtil.isNotBlank(endorserInfos[i+1])){
								endorsementMap.put("BILL_INFO_ARRAY.ENDORSEE_NAME", endorserInfos[i+1]);// 被背书人名称
							}else{
								endorsementMap.put("BILL_INFO_ARRAY.ENDORSEE_NAME", "");// 被背书人名称
							}
						}else{
							endorsementMap.put("BILL_INFO_ARRAY.ENDORSEE_NAME", "");// 被背书人名称
						}
						vtList.add(endorsementMap);
					}
					
				}
			}
		}
		
		return vtList;
	}

	@Override
	public Map queryMarginAccountPJC020(String coreCustnumber, String poolAgreement) throws Exception {
		Map proMap = new HashMap();
		List keys = new ArrayList();
		List values = new ArrayList();
		StringBuffer hql = new StringBuffer("select pro from PedProtocolDto pro where 1=1 ");

		hql.append("and pro.custnumber=:custnumber ");
		keys.add("custnumber");
		values.add(coreCustnumber);

		hql.append("and pro.poolAgreement=:poolAgreement ");
		keys.add("poolAgreement");
		values.add(poolAgreement);

		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List result = this.find(hql.toString(), keis, values.toArray());
		if (result != null && result.size() > 0) {
			PedProtocolDto pro = (PedProtocolDto) result.get(0);
			proMap.put("DEPOSIT_ACCT_NO", pro.getMarginAccount());
			proMap.put("DEPOSIT_ACCT_NAME", pro.getMarginAccountName());

		}
		return proMap;

	}

	@Override
	public Map queryMarginBalance(String custNo, String poolAgreement) throws Exception {
		
		
		BigDecimal allLowCredit = BigDecimal.ZERO;//期限匹配表中第一条的低风险可用额度
		try {
			List<CreditCalculation> ccList = financialService.queryCreditCalculationList(poolAgreement);
			CreditCalculation cc = ccList.get(0);
			allLowCredit = cc.getLowRiskCredit();//低风险可用额度			
		} catch (Exception e) {
			logger.error("额度计算表查询失败！",e);
		}
		
		
		QueryResult queryResult = new QueryResult();
		BigDecimal otherEdu = null;
		Map map = new HashMap();
		String apId = "";
		String frozenFlag = "";// 冻结标识
		StringBuffer hql = new StringBuffer(
				"select pp.marginAccount,pp.marginAccountName,at.crdtTotal,at.crdtFree,at.crdtUsed,at.crdtFrzd "
				+ "from PedProtocolDto pp,AssetPool ap,AssetType at where 1=1"
				+ " and pp.poolAgreement=ap.bpsNo and ap.id=at.apId and at.astType='ED_21' and pp.custnumber= '" + custNo
				+ "' and pp.poolAgreement='"+poolAgreement+"'");
		List result = this.find(hql.toString());
		if (result != null && result.size() > 0) {
			Object[] obj = (Object[])result.get(0);
			String marginAccount = "";
			if (obj[0] != null && !"".equals(obj[0])) {
				marginAccount = String.valueOf(obj[0]);
			} 
			String marginAccountName = "";
			if (obj[1] != null && !"".equals(obj[1])) {
				marginAccountName = String.valueOf(obj[1]);
			} 
			BigDecimal crdtTotal = new BigDecimal("0.0");
			if (obj[2] != null && !"".equals(obj[2])) {
				crdtTotal =  new BigDecimal(obj[2].toString());
			}
//			BigDecimal crdtFree = new BigDecimal("0.0");
//			if (obj[3] != null && !"".equals(obj[3])) {
//				crdtFree =  new BigDecimal(obj[3].toString());
//			}
			BigDecimal crdtUsed = new BigDecimal("0.0");
			if (obj[4] != null && !"".equals(obj[4])) {
				crdtUsed =  new BigDecimal(obj[4].toString());
			}
			BigDecimal crdtFrzd = new BigDecimal("0.0");
			if (obj[5] != null && !"".equals(obj[5])) {
				crdtFrzd =  new BigDecimal(obj[5].toString());
			}
			
			map.put("DEPOSIT_ACCT_NO", marginAccount);// 保证金账号
			map.put("DEPOSIT_ACCT_NAME", marginAccountName);// 保证金账号名称
			map.put("CCY", "01");// 币种：人民币
			map.put("DEPOSIT_BAL_AMT", crdtTotal);// 保证金金额
			map.put("LIMIT_USE_AMT", crdtUsed);// 已用金额
//			map.put("FROZEN_AMT", crdtFrzd);// 冻结金额
			
			//可用余额，取低风险可用额度与保证金额度的最小值
			BigDecimal avalBalance = new BigDecimal("0.0");
			avalBalance = crdtTotal.compareTo(allLowCredit)>0?allLowCredit:crdtTotal;
			if(avalBalance.signum()==-1){//负数
				map.put("AVAL_BALANCE", new BigDecimal("0.0"));// 可用余额
			}else {
				map.put("AVAL_BALANCE", avalBalance);// 可用余额
			}
			
		}
		return map;
	}

	@Override
	public DraftPoolIn txApplyDraftPoolInPJC002(PoolBillInfo bill, String eSign, String bpsNo, PoolBillInfo changeBill) throws Exception {


		if(changeBill.getTradeAmt().compareTo(bill.getFBillAmount()) < 0){
			/** 网银发送的拆分的数据	需做拆票动作
			 * 1、
			 * 2、生成拆分后的入池数据
			 * 3、原始大票数据状态置为失效
			 * 
			 */
			PoolBillInfo newBill = new PoolBillInfo();
			BeanUtil.copyValue(bill, newBill);
			newBill.setBillinfoId(null);
			/**
			 * 生成拆票的子票区间并赋新的子票区间到拆分入池的新票中
			 * 原始票据置为失效
			 */
			/**
			 * 生成拆票的子票区间
			 */
			DraftRange rangeIn = DraftRangeHandler.buildLimitBeginAndEndDraftRange(bill.getFBillAmount(), changeBill.getTradeAmt(), bill.getStandardAmt(), bill.getBeginRangeNo(), bill.getEndRangeNo());
			
			newBill.setBeginRangeNo(rangeIn.getBeginDraftRange());
			newBill.setEndRangeNo(rangeIn.getEndDraftRange());
			newBill.setTradeAmt(changeBill.getTradeAmt());//网银发送过来的交易金额
			newBill.setFBillAmount(changeBill.getTradeAmt());
			
			
			DraftPoolIn inPool = draftPoolInService.copyBillToPoolIn(newBill);
			inPool.setSplitId(bill.getBillinfoId());//拆分前的大票id
			inPool.setElsignature(eSign);
			bill.setSDealStatus(PoolComm.DS_12);// 做过拆分操作的失效数据
			bill.setEbkLock(PoolComm.EBKLOCK_02);//解锁
			bill.setPoolAgreement(bpsNo);
			
			newBill.setSDealStatus(PoolComm.DS_01);// 入池处理中
			newBill.setEbkLock(PoolComm.EBKLOCK_02);//解锁
			newBill.setPoolAgreement(bpsNo);
			
			inPool.setPoolAgreement(bpsNo);
			this.txStore(inPool);
			this.txStore(bill);
			this.txStore(newBill);
			
			return inPool;
			
		}else{
			DraftPoolIn inPool = draftPoolInService.copyBillToPoolIn(bill);
			inPool.setElsignature(eSign);
			bill.setSDealStatus(PoolComm.DS_01);// 入池处理中
			bill.setEbkLock(PoolComm.EBKLOCK_02);//解锁
			bill.setPoolAgreement(bpsNo);
			inPool.setPoolAgreement(bpsNo);
			this.txStore(inPool);
			this.txStore(bill);
			
			return inPool;
		}
		
		

	}

	@Override
	public Ret txApplyDraftPoolOutPJC003(List<String> billNos, String elsignature, PedProtocolDto dto,PoolQueryBean param,Map<String,PoolBillInfo> bills,List<String> assteNos) throws Exception {
		
		logger.info("出池额度扣减处理方法txApplyDraftPoolOutPJC003处理开始...");
		
		Ret returnResult = new Ret();//返回对象
		returnResult.setRET_CODE(Constants.TX_FAIL_CODE);//失败
		
		/*
		 * 获取要出池的资产对象
		 */
		List<AssetRegister> assetOutList = assetRegisterService.queryAssetRegisterByAssetNo(assteNos);
		
		/*
		 * 出池资产额度扣减
		 */
		try {
			
			returnResult =  financialService.txAssetOut(assetOutList,dto,bills);
			
		} catch (Exception e) {
			logger.info("资产出池额度校验系统内异常，出错的方法financialService.txAssetOut，出错位置：",e);
			return returnResult;//池额度不足不允许出池
			
		}

		
		if (Constants.TX_SUCCESS_CODE.equals(returnResult.getRET_CODE())) {
			/*
			 * 获取要出池的大票表对象
			 */
			
			List<DraftPool> draftlist = new ArrayList<DraftPool>();//需要落库处理的票据资产表对象
			List<PoolBillInfo> billlist = new ArrayList<PoolBillInfo>();//需要落库处理的大票表对象
			List<DraftPoolOut> outlist = new ArrayList<DraftPoolOut>();//需要落库处理的出池资产对象
			
			/**
			 * 获取需修改的对象若有可拆分的数据，需要处理
			 */
			if(assteNos != null && assteNos.size() > 0){
				for (int i = 0; i < assteNos.size(); i++) {
					PoolBillInfo tempBill = bills.get(assteNos.get(i));
					DraftPool draft = draftPoolInService.getDraftPoolByDraftNbOrStatus(tempBill.getSBillNo(),tempBill.getBeginRangeNo(),tempBill.getEndRangeNo(),PoolComm.DS_02);
					
					if(draft != null){

						
						if(null != param){//PJC037字段
							if(StringUtil.isNotBlank(param.getOutBatchNo())){
								draft.setDoBatchNo(param.getOutBatchNo());////PJC037字段出池录入后续操作接口						
							}

						}
						draft.setProtocol(dto);
						draft.setAssetStatus(PoolComm.DS_03);// 出池处理中
						PoolBillInfo bill = draft.getPoolBillInfo();
						String plDraftMedia = draft.getPlDraftMedia();
						
						if(plDraftMedia.equals(PoolComm.BILL_MEDIA_PAPERY)){	//纸票				
							PoolVtrustBeanQuery query = new PoolVtrustBeanQuery();
							query.setVtNb(draft.getAssetNb());
							PoolVtrust vtrust = poolVtrustService.queryPoolVtrust(query);
							vtrust.setVtStatus(PoolComm.DS_03);
							this.txStore(vtrust);
						}
						
						bill.setSDealStatus(PoolComm.DS_03);
						
						
						if(null != param){
							
							if(StringUtil.isNotBlank(param.getOutBatchNo())){
								bill.setpOutBatchNo(param.getOutBatchNo());////pjc044纸票出池字段						
							}
							if(StringUtil.isNotBlank(param.getWorkerCard())){//pjc044纸票出池字段
								bill.setWorkerCard(param.getWorkerCard());
							}
							if(StringUtil.isNotBlank(param.getWorkerName())){//pjc044纸票出池字段
								bill.setWorkerName(param.getWorkerName());
								
							}
							if(StringUtil.isNotBlank(param.getWorkerPhone())){//pjc044纸票出池字段
								bill.setWorkerPhone(param.getWorkerPhone());
								
							}
						}
						
						
						
						/**
						 * 若有拆分的数据出池,需做数据处理
						 * 
						 */
						if(tempBill.getTradeAmt().compareTo(draft.getAssetAmt()) < 0){
							/**
							 * 生成拆票的子票区间
							 */
							DraftRange rangeIn = DraftRangeHandler.buildLimitNewBeginAndEndDraftRange(bill.getFBillAmount(), tempBill.getTradeAmt(), bill.getStandardAmt(), bill.getBeginRangeNo(), bill.getEndRangeNo());
							DraftRange rangeOut = DraftRangeHandler.buildLimitBeginAndEndDraftRange(bill.getFBillAmount(), tempBill.getTradeAmt(), bill.getStandardAmt(), bill.getBeginRangeNo(), bill.getEndRangeNo());
							//1、生成一条出池的资产池表数据
							DraftPool outPool = AssetFactory.newDraftPool();
							PoolBillInfo outBill = new PoolBillInfo();
							BeanUtil.copyValue(draft, outPool);
							outPool.setId(null);
							outPool.setBeginRangeNo(rangeOut.getBeginDraftRange());
							outPool.setEndRangeNo(rangeOut.getEndDraftRange());
							outPool.setSplitId(bill.getBillinfoId());//拆分前的大票表主键id
							outPool.setAssetAmt(tempBill.getTradeAmt());
							outPool.setTradeAmt(tempBill.getTradeAmt());
							outPool.setLastOperName("票据池出池生成的新的一条出池拆分后的数据！");
							outPool.setLastOperTm(new Date());
							outPool.setElsignature(elsignature);
							
							
							//2、生成一条出池的票据基本信息表数据
							BeanUtil.copyValue(bill, outBill);
							outBill.setBillinfoId(null);
							outBill.setFBillAmount(tempBill.getTradeAmt());
							outBill.setTradeAmt(tempBill.getTradeAmt());
							outBill.setBeginRangeNo(rangeOut.getBeginDraftRange());
							outBill.setEndRangeNo(rangeOut.getEndDraftRange());
							outBill.setSplitId(bill.getBillinfoId());//拆分前的大票表主键id
							outBill.setLastOperName("票据池出池生成的新的一条出池拆分后的数据！");
							outBill.setLastOperTm(new Date());
							
							
							draftlist.add(outPool);
							billlist.add(outBill);
							
							
							DraftPoolOut poolout = poolOutDataProcess(outPool);
							poolout.setSplitId(bill.getBillinfoId());//拆分前的大票表主键id
							poolout.setElsignature(elsignature);
							poolout.setPoolAgreement(draft.getPoolAgreement());
							
							outlist.add(poolout);
							
							//4、生成一条在池的票据基本信息表数据,修改在池状态
							PoolBillInfo inBill = new PoolBillInfo();
							BeanUtil.copyValue(bill, inBill);
							inBill.setBillinfoId(null);
							inBill.setSplitId(bill.getBillinfoId());//拆分前的大票表主键id
							inBill.setBeginRangeNo(rangeIn.getBeginDraftRange());
							inBill.setEndRangeNo(rangeIn.getEndDraftRange());
							inBill.setSDealStatus(PoolComm.DS_02);
							inBill.setFBillAmount(bill.getFBillAmount().subtract(tempBill.getTradeAmt()));
							inBill.setTradeAmt(bill.getFBillAmount().subtract(tempBill.getTradeAmt()));
							inBill.setLastOperName("票据池出池生成的新的一条在池拆分后的数据！");
							inBill.setLastOperTm(new Date());
							
							this.txStore(inBill);
							
							//3、生成一条在池的资产池表数据,修改在池状态
							DraftPool inPool = AssetFactory.newDraftPool();
							
							BeanUtil.copyValue(draft, inPool);
							inPool.setId(null);
							inPool.setSplitId(bill.getBillinfoId());//拆分前的大票表主键id
							inPool.setBeginRangeNo(rangeIn.getBeginDraftRange());
							inPool.setEndRangeNo(rangeIn.getEndDraftRange());
							inPool.setAssetStatus(PoolComm.DS_02);
							inPool.setPoolBillInfo(inBill);
							inPool.setAssetAmt(bill.getFBillAmount().subtract(tempBill.getTradeAmt()));
							inPool.setTradeAmt(bill.getFBillAmount().subtract(tempBill.getTradeAmt()));
							inPool.setDoBatchNo(null);
							
							inPool.setLastOperName("票据池出池生成的新的一条在池拆分后的数据！");
							inPool.setLastOperTm(new Date());
							this.txStore(inPool);
							
							
							
							//5、修改原资产池表数据为做过拆分操作的失效
							draft.setAssetStatus(PoolComm.DS_12);//做过拆分操作的失效数据
							draft.setLastOperName("票据池出池拆分后失效的原始数据！");
							draft.setLastOperTm(new Date());
							this.txStore(draft);
							
							//6、修改原票据基本信息表数据为做过拆分操作的失效
							bill.setSDealStatus(PoolComm.DS_12);//做过拆分操作的失效数据
							bill.setLastOperName("票据池出池拆分后失效的原始数据！");
							bill.setLastOperTm(new Date());
							this.txStore(bill);
							
						}else{
							DraftPoolOut poolout = poolOutDataProcess(draft);
							
							poolout.setSplitId(null);
							poolout.setElsignature(elsignature);
							poolout.setPoolAgreement(draft.getPoolAgreement());
							
							draft.setElsignature(elsignature);
							
							//不拆分票据直接解锁，拆分票据发完解质押申请后解锁
							bill.setEbkLock(PoolComm.EBKLOCK_02);//解锁
							
							draftlist.add(draft);
							billlist.add(bill);
							outlist.add(poolout);
						}
					}
					
					
				}
			}
			
			
			/*
			 * 落库处理
			 */
			this.txStoreAll(draftlist);
			this.txStoreAll(billlist);
			this.txStoreAll(outlist);
			
			returnResult.setSomeList(draftlist);
			returnResult.setSomeList2(billlist);
			returnResult.setSomeList3(outlist);
			
			
			returnResult.setRET_CODE(Constants.TX_SUCCESS_CODE);//出池成功
			return returnResult;//出池成功
		}
		
		return returnResult;

	}
	

	/**
	 * @param pool
	 * @return DraftPoolOut
	 * @Description: 出池申请数据加工处理
	 * @author Ju Nana
	 * @date 2018-10-25 下午2:04:34
	 */
	private DraftPoolOut poolOutDataProcess(DraftPool pool) {

		DraftPoolOut poolout = new DraftPoolOut();
		poolout.setPlTradeType(PoolComm.BILL_POOL);// 业务类型
		poolout.setPlReqTime(new Date()); // 申请时间
		poolout.setProductId(PoolComm.PRODUCT_TYPE_CC);// 产品类型
		poolout.setPlStatus(PoolComm.OUT_YTJSQ);// 已提交申请
		poolout.setPlDraftNb(pool.getAssetNb());// 票号
		poolout.setPlDraftMedia(pool.getPlDraftMedia());// 票据介质
		poolout.setPlDraftType(pool.getAssetType());// 票据类型
		poolout.setPlIsseAmtValue(pool.getPlIsseAmtValue());// 币种
		poolout.setPlIsseAmt(pool.getAssetAmt());// 票面金额
		poolout.setPlIsseDt(pool.getPlIsseDt());// 出票日
		poolout.setPlDueDt(pool.getPlDueDt());// 到期日
		poolout.setPlDrwrNm(pool.getPlDrwrNm());// 出票人
		poolout.setPlDrwrAcctId(pool.getPlDrwrAcctId());// 出票人账号
		poolout.setPlDrwrAcctSvcr(pool.getPlDrwrAcctSvcr());// 出票人开户行行号
		poolout.setPlDrwrAcctSvcrNm(pool.getPlDrwrAcctSvcrNm());// 出票人开户行
		poolout.setPlPyeeNm(pool.getPlPyeeNm());// 收款人
		poolout.setPlPyeeAcctId(pool.getPlPyeeAcctId());// 收款人账号
		poolout.setPlPyeeAcctSvcr(pool.getPlPyeeAcctSvcr());// 收款人开户行行号
		poolout.setPlPyeeAcctSvcrNm(pool.getPlPyeeAcctSvcrNm());// 收款人开户行
		poolout.setPlAccptrNm(pool.getPlAccptrNm());// 承兑人
		poolout.setPlAccptrId(pool.getPlAccptrId());// 承兑人账号
		poolout.setPlAccptrSvcr(pool.getPlAccptrSvcr());// 承兑行行号
		poolout.setPlAccptrSvcrNm(pool.getPlAccptrSvcrNm());// 承兑行
		poolout.setPlApplyNm(pool.getAssetApplyNm());// 申请人
		poolout.setPlCommId(pool.getAssetCommId());// 申请人组织机构代码
		poolout.setPlApplyAcctSvcr(pool.getAssetApplyAcctSvcr());// 申请人开户行行号
		poolout.setPlApplyAcctSvcrNm(pool.getAssetApplyAcctSvcrNm());// 申请人开户行名称
		poolout.setPlApplyAcctId(pool.getAssetApplyAcctId());// 申请人账号
		poolout.setReqSource(PoolComm.DATA_LY_WY);// 数据来源
		poolout.setDraftPool(pool);// 对应票据池表
		poolout.setPlRecSvcr(pool.getAssetRecSvcr());// 业务经办行行号
		poolout.setPlRecSvcrNm(pool.getAssetRecSvcrNm());// 业务经办行名称
		poolout.setDraftPool(pool);// 票据池对象
		poolout.setWorkerName(pool.getWorkerName());// 经办人姓名
		poolout.setPlStatus(PoolComm.CC_00);

		poolout.setGuaranteeNo(pool.getGuaranteeNo());// 担保编号
		poolout.setCustNo(pool.getAssetCommId());//核心客户号
		poolout.setCustName(pool.getCustName());//核心客户号
		
		poolout.setAccNo(pool.getAccNo());//点票签约账号
		poolout.setElsignature(pool.getElsignature());//电子签名
		
		poolout.setBranchId(pool.getBranchId());//存储网点信息  用于权限分配
		poolout.setBtFlag(pool.getBtFlag());//商票保贴额度处理标识
		poolout.setAccptrOrg(pool.getAccptrOrg());//承兑人组织机构代码
		poolout.setTXFlag(pool.getTXFlag());//强贴标识
		poolout.setForbidFlag(pool.getPoolBillInfo().getSBanEndrsmtFlag());//禁止背书
		poolout.setTaskDate(new Date());
		poolout.setBeginRangeNo(pool.getBeginRangeNo());
		poolout.setEndRangeNo(pool.getEndRangeNo());
		poolout.setTradeAmt(pool.getTradeAmt());
		poolout.setStandardAmt(pool.getStandardAmt());
		poolout.setSplitFlag(pool.getSplitFlag());
		poolout.setDraftSource(pool.getDraftSource());
		poolout.setHilrId(pool.getHilrId());
		
		poolout.setPlDrwrAcctName(pool.getPlDrwrAcctName());
		poolout.setPlPyeeAcctName(pool.getPlPyeeAcctName());
		poolout.setPlAccptrAcctName(pool.getPlAccptrAcctName());
		
		/*
		 * if (null != pool.getPlCollateralizationId()) { CollateralReceiveDto
		 * collateralReceiveDto = (CollateralReceiveDto) this
		 * .load(pool.getPlCollateralizationId(), CollateralReceiveDto.class);
		 * poolout.setCollateralReceiveDto(collateralReceiveDto); }
		 */
		return poolout;

	}

	/**
	 * 取票记账完毕后调用该方法 执行贴现或者质押入池流程 托管票据贴现进行贴现操作，标识为：1 托管票据转质押操作，标识为：2
	 */
	public void txAccountQuerytoNext(DraftPoolOutBatch batch) throws Exception {
		List keys = new ArrayList();
		List values = new ArrayList();
		PoolBillInfo poolBillInfo = null;

		Set<DraftPoolOut> pools = batch.getPoolOuts();
		Iterator<DraftPoolOut> it = pools.iterator();
		while (it.hasNext()) {
			String draftFlag = this.txDiscountOrPoolIn(it.next().getPlDraftNb());

			if (draftFlag != null && !"".equals(draftFlag)) {
				// 执行贴现申请
				if (draftFlag.equals("1")) {
					List result = this.queryDiscBillInfoDraftStorage(it.next().getPlApplyAcctId(),
							it.next().getPlDraftNb());
					for (int i = 0; i < result.size(); i++) {
						poolBillInfo = (PoolBillInfo) result.get(i);
						// 查询DiscountSellDto卖出实体
						/*
						 * DiscountSellDto dsd = this.txDiscountSellByBILLNO(poolBillInfo); if(dsd
						 * !=null){ //贴现申请 this.txReplaceDiscountFromPJC022(dsd); }
						 */
					}
				}
				// 执行质押入池申请
				if (draftFlag.equals("2")) {
					DraftStorage draftStorage = this.txDraftToPoolIn(it.next().getPlDraftNb());
				} else {
					logger.info("申请人名称： " + it.next().getPlApplyNm() + "票号： " + it.next().getPlDraftNb()
							+ "代保管取票结束时无执行质押和贴现的标识，结束");
				}

			}
		}

	}

	/**
	 * 取票记账完毕后， 执行贴现或者质押入池流程 托管票据贴现进行贴现操作，标识为：1 托管票据转质押操作，标识为：2
	 */
	public String txDiscountOrPoolIn(String plDraftNb) throws Exception {
		List keys = new ArrayList();
		List values = new ArrayList();
		DraftStorage draftStorage = null;
		String draftFlag = "";

		StringBuffer hql = new StringBuffer(
				"select storage from DraftStorage storage,DraftPoolOut poolOut where storage.id=poolOut.draftStroage ");

		if (StringUtil.isNotEmpty(plDraftNb)) {// 票据号码
			hql.append(" and poolOut.plDraftNb=:PplDraftNb");
			keys.add("PplDraftNb");
			values.add(plDraftNb);
		}

		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List result = this.find(hql.toString(), keis, values.toArray());

		if (result != null && result.size() == 1) {
			draftStorage = (DraftStorage) result.get(0);
			draftFlag = draftStorage.getDraftFlag();
		}

		return draftFlag;
	}

	/**
	 * 托管表有标识：1 查询大票表票据是否符合贴现申请
	 */
	public List queryDiscBillInfoDraftStorage(String plApplyAcctId, String SBillNo) throws Exception {

		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();
		String amountFieldName = "FBillAmount";

		// 过滤逾期
		hql.append("select dto from PoolBillInfo dto where ");

		// 比原来的方法新增条件,票号
		hql.append(" and dto.SBillNo = :SBillNo");
		parasName.add("SBillNo");
		parasValue.add(SBillNo);

		// 大票状态
		hql.append("  dto.SECDSStatus in (:SECDSStatus)");
		List status = StringUtil.splitList(PoolComm.QUERY_BIG_TABLE_STATUS, ",");
		parasName.add("SECDSStatus");
		parasValue.add(status);
		// 逾期标识
		hql.append(" and dto.overFlag = :overFlag");
		parasName.add("overFlag");
		parasValue.add(PoolComm.CP_QRY_TYP_WDQ);
		// 处理状态 及 不得转让标记
		hql.append(" and dto.SDealStatus = (:SDealStatus)  and dto.SBanEndrsmtFlag = :SBanEndrsmtFlag");
		parasName.add("SDealStatus");
		parasName.add("SBanEndrsmtFlag");
		parasValue.add(PoolComm.DS_002);
		parasValue.add(PoolComm.NOT_ATTRON_FLAG_NO);// 不得转让标记

		// 企业帐号
		if (plApplyAcctId != null && !"".equals(plApplyAcctId)) {
			hql.append(" and dto.SOwnerAcctId = :plApplyAcctId");
			parasName.add("account");
			parasValue.add(plApplyAcctId);
		}

		// 按到期日升序
		hql.append(" order by dto.DDueDt asc");

		// 执行查询
		Page page = new Page();
		page.setPageSize(500);
		List records = this.find(hql.toString(), (String[]) parasName.toArray(new String[parasName.size()]),
				parasValue.toArray(), page);

		return records;

	}

	/**
	 * 取票记账完毕后， 查询DraftStorage实体， return draftStorage
	 */
	public DraftStorage txDraftToPoolIn(String plDraftNb) throws Exception {
		List keys = new ArrayList();
		List values = new ArrayList();
		DraftStorage draftStorage = null;
		String draftFlag = "";

		StringBuffer hql = new StringBuffer(
				"select poolOut from DraftStorage storage,DraftPoolOut poolOut where storage.id=poolOut.draftStroage ");

		if (StringUtil.isNotEmpty(plDraftNb)) {// 票据号码
			hql.append(" and poolOut.plDraftNb=:plDraftNb");
			keys.add("plDraftNb");
			values.add(plDraftNb);
		}

		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List result = this.find(hql.toString(), keis, values.toArray());

		if (result != null && result.size() == 1) {
			draftStorage = (DraftStorage) result.get(0);
		}

		return draftStorage;
	}

	@Override
	public void txPoolVtrustStore(PoolVtrust vtrust, String transType) throws Exception {
		if ("03".equals(transType)) {// 01-新增 02-修改 03-删除
			PoolVtrust vt1 = (PoolVtrust) poolVtrustService.load(vtrust.getId());
			this.txDelete(vt1);
		} else if ("01".equals(transType)) {// 新增
//    		vtrust = this.vtrustToPool(vtrust);//黑名单校验——目前将实现注销
			vtrust.setId(null);
			this.txStore(vtrust);
		} else {
			PoolVtrust vt2 = (PoolVtrust) poolVtrustService.load(vtrust.getId());
			vt2.setVtNb(vtrust.getVtNb());
			if(vtrust.getBpsNo()!=null){
				vt2.setBpsNo(vtrust.getBpsNo());	
			}
			if(vtrust.getVtDraftMedia()!=null){
				vt2.setVtDraftMedia(vtrust.getVtDraftMedia());	
			}
			if(vtrust.getVtType()!=null){
				vt2.setVtType(vtrust.getVtType());
			}
			if(vtrust.getVtaccptrName()!=null){
				vt2.setVtaccptrName(vtrust.getVtaccptrName());
			}
			if(vtrust.getVtaccptrBankName()!=null){
				vt2.setVtaccptrBankName(vtrust.getVtaccptrBankName());
			}
			if(vtrust.getVtaccptrBankAccount()!=null){
				vt2.setVtaccptrBankAccount(vtrust.getVtaccptrBankAccount());
			}
			if(vtrust.getVtaccptrBankAddr()!=null){
				vt2.setVtaccptrBankAddr(vtrust.getVtaccptrBankAddr());
			}
			if(vtrust.getVtaccptrAccount()!=null){
				vt2.setVtaccptrAccount(vtrust.getVtaccptrAccount());
			}
			if(vtrust.getVtpyeeName()!=null){
				vt2.setVtpyeeName(vtrust.getVtpyeeName());
			}
			if(vtrust.getVtpyeeBankAccount()!=null){
				vt2.setVtpyeeBankAccount(vtrust.getVtpyeeBankAccount());
			}
			if(vtrust.getVtpyeeBankName()!=null){
				vt2.setVtpyeeBankName(vtrust.getVtpyeeBankName());
			}
			if(vtrust.getVtpyeeAccount()!=null){
				vt2.setVtpyeeAccount(vtrust.getVtpyeeAccount());
			}
			if(vtrust.getVtisseAmt()!=null){
				vt2.setVtisseAmt(vtrust.getVtisseAmt());
				
			}
			if(vtrust.getVtdrwrName()!=null){
				vt2.setVtdrwrName(vtrust.getVtdrwrName());
			}
			if(vtrust.getVtdrwrAccount()!=null){
				vt2.setVtdrwrAccount(vtrust.getVtdrwrAccount());
			}
			if(vtrust.getVtdrwrBankNumber()!=null){
				vt2.setVtdrwrBankNumber(vtrust.getVtdrwrBankNumber());
			}
			if(vtrust.getVtdrwrBankName()!=null){
				vt2.setVtdrwrBankName(vtrust.getVtdrwrBankName());
			}
			if(vtrust.getVtisseDt()!=null){
				vt2.setVtisseDt(vtrust.getVtisseDt());
			}
			if(vtrust.getVtdueDt()!=null){
				vt2.setVtdueDt(vtrust.getVtdueDt());
			}
			if(vtrust.getVtaccptrDate()!=null){
				vt2.setVtaccptrDate(vtrust.getVtaccptrDate());
			}
			if(vtrust.getVtTranSfer()!=null){
				vt2.setVtTranSfer(vtrust.getVtTranSfer());
			}
			if(vtrust.getEndorserInfo()!=null){
				vt2.setEndorserInfo(vtrust.getEndorserInfo());
			}
			if(vtrust.getVtaccptrBankAccount()!=null){
				vt2.setVtaccptrBankAccount(vtrust.getVtaccptrBankAccount());
			}
			if(vtrust.getVtaccptrBankAddr()!=null){
				vt2.setVtaccptrBankAddr(vtrust.getVtaccptrBankAddr());
			}
			if(vtrust.getDrwrGuarntrNm()!=null){
				vt2.setDrwrGuarntrNm(vtrust.getDrwrGuarntrNm());
			}
			if(vtrust.getDrwrGuarntrAddr()!=null){
				vt2.setDrwrGuarntrAddr(vtrust.getDrwrGuarntrAddr());
			}
			if(vtrust.getDrwrGuarntrDt()!=null){
				vt2.setDrwrGuarntrDt(vtrust.getDrwrGuarntrDt());
			}
			if(vtrust.getAccptrGuarntrNm()!=null){
				vt2.setAccptrGuarntrNm(vtrust.getAccptrGuarntrNm());
			}
			if(vtrust.getAccptrGuarntrAddr()!=null){
				vt2.setAccptrGuarntrAddr(vtrust.getAccptrGuarntrAddr());
			}
			if(vtrust.getAccptrGuarntrDt()!=null){
				vt2.setAccptrGuarntrDt(vtrust.getAccptrGuarntrDt());
			}
			if(vtrust.getBillPosition()!=null){
				vt2.setBillPosition(vtrust.getBillPosition());
			}
			if(vtrust.getBillPositionAddr()!=null){
				vt2.setBillPositionAddr(vtrust.getBillPositionAddr());
			}
			if(vtrust.getRemarks()!=null){
				vt2.setRemarks(vtrust.getRemarks());
			}
			if(vtrust.getContractNo()!=null){
				vt2.setContractNo(vtrust.getContractNo());	
			}
			if(vtrust.getAcceptanceAgreeNo()!=null){
				vt2.setAcceptanceAgreeNo(vtrust.getAcceptanceAgreeNo());
			}
			if(vtrust.getBeginRangeNo()!=null){
				vt2.setBeginRangeNo(vtrust.getBeginRangeNo());
			}
			if(vtrust.getEndRangeNo()!=null){
				vt2.setEndRangeNo(vtrust.getEndRangeNo());
			}
			if(vtrust.getDraftSource()!=null){
				vt2.setDraftSource(vtrust.getDraftSource());
			}
			
			 //承兑行总行
			String acptBankNo = vt2.getVtaccptrBankAccount();
			Map cpes = blackListManageService.queryCpesMember(acptBankNo);
			if(cpes != null){
				vt2.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
				String memberName = (String) cpes.get("memberName");//总行行名
				vt2.setAcptHeadBankName(memberName);//总行行名
			}
			
			vt2.setVtSource(PoolComm.SOUR_EBK);
			this.txStore(vt2);
		}

	}


	@Override
	public List queryCustomert(String SAccountNo) throws Exception {

		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setCustIdA(SAccountNo);
		List list = new ArrayList();
		ReturnMessageNew response = poolCoreService.PJH854111Handler(transNotes);
		if (response.isTxSuccess()) {
			Map map = response.getBody();
			PedProtocolDto pedProtocolDto = new PedProtocolDto();
			if(map.get("CLIENT_NAME")!=null){				
				String clientName = (String) map.get("CLIENT_NAME");
				pedProtocolDto.setCustname(clientName.trim());// 客户名称
			}
			list.add(pedProtocolDto);
			return list;
		}
		return null;
	}

	@Override
	public List queryAccount(String poolAccount) throws Exception {

		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setAccNo(poolAccount);
		transNotes.setCurrentFlag("1");
		List list = new ArrayList();
		ReturnMessageNew response = poolCoreService.PJH716040Handler(transNotes, "1");
		Map reMap = new HashMap();

		if (response.isTxSuccess()) {
			Map map = response.getBody();
			String proNo = (String) map.get("PRODUCT_NO");// 产品编号
			if (proNo != null && proNo.equals("2209022")) {// 码值表示票据池保证金
				reMap.put("code", "1");
				reMap.put("msg", (String) map.get("CLIENT_NAME"));
				reMap.put("Org", (String) map.get("ORG_CODE"));
				reMap.put("clientNo", (String) map.get("CLIENT_NO"));//客户号
				reMap.put("productNo", (String) map.get("PRODUCT_NO"));//产品编号
				reMap.put("balnce", (String) map.get("AVAL_BALANCE"));//余额
				list.add(reMap);
			} else {
				return null;
			}
		} else {
			reMap.put("code", "2");
			reMap.put("msg", response.getSysHead().get("RET.RET_MSG"));
			list.add(reMap);
		}
		return list;
	}

	@Override
	public List queryAccNo(String poolAccount) throws Exception {

		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setAccNo(poolAccount);
		transNotes.setCurrentFlag("1");
		List list = new ArrayList();
		ReturnMessageNew response = poolCoreService.PJH716040Handler(transNotes, "1");
		Map reMap = new HashMap();
		if (response.isTxSuccess()) {
			Map map = response.getBody();
			PedProtocolDto pedProtocolDto = new PedProtocolDto();
			pedProtocolDto.setPoolAccountName((String) map.get("CLIENT_NAME"));// 结算账号名称
			reMap.put("code", "1");
			reMap.put("msg", pedProtocolDto.getPoolAccountName());
			list.add(reMap);
		} else {
			reMap.put("code", "2");
			reMap.put("msg", response.getSysHead().get("RET.RET_MSG"));
			list.add(reMap);
		}
		return list;
	}

	@Override
	public String queryPoolCommOpen(String bpsNo,String orgCode, String custNum, List custAccts, String marginAccount) {
		String openFlag = PoolComm.DRAFT_POOL_CLOSE;
		List keys = new ArrayList();
		List values = new ArrayList();
		StringBuffer hql = new StringBuffer(" select pro.openFlag from PedProtocolDto pro where 1=1 ");
		
		if (StringUtils.isNotEmpty(bpsNo)) {
			hql.append(" and pro.poolAgreement=:bpsNo ");
			keys.add("bpsNo");
			values.add(bpsNo);
		}
		
		if (StringUtils.isNotEmpty(custNum)) {
			hql.append(" and pro.custnumber=:custnumber ");
			keys.add("custnumber");
			values.add(custNum);
		}
		if (StringUtils.isNotEmpty(orgCode)) {
			hql.append(" and pro.custOrgcode=:custOrgcode ");
			keys.add("custOrgcode");
			values.add(orgCode);
		}

		if (StringUtils.isNotEmpty(marginAccount)) {
			hql.append(" and pro.marginAccount=:marginAccount ");
			keys.add("marginAccount");
			values.add(marginAccount);
		}

		if (custAccts != null && custAccts.size() > 0) {// 该字段为预留字段，后续可能开放使用
			hql.append(" and ad.SAccountNo in (:custAccts) ");
			keys.add("custAccts");
			values.add(custAccts);
		}

		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List resList = this.find(hql.toString(), keis, values.toArray());

		if (resList != null && resList.size() > 0) {
			if (resList.get(0) != null) {
				openFlag = String.valueOf(resList.get(0));
				if (openFlag.equals(PoolComm.OPEN_01)) {// 已签约
					openFlag = PoolComm.DRAFT_POOL_OPEN;//
				}
			} else {
				openFlag = PoolComm.DRAFT_POOL_CLOSE;
			}
		}

		return openFlag;
	}

	@Override
	public String queryVtrustPoolCommOpen(String poolAgreement, String custNum) {
		String openFlag = "";
		List keys = new ArrayList();
		List values = new ArrayList();

		StringBuffer hql = new StringBuffer(" select pro.vStatus from PedProtocolDto pro where 1=1 ");

		if (StringUtils.isNotEmpty(custNum)) {
			hql.append(" and pro.custnumber=:custnumber ");
			keys.add("custnumber");
			values.add(custNum);
		}
		if (StringUtils.isNotEmpty(poolAgreement)) {
			hql.append(" and pro.poolAgreement=:poolAgreement ");
			keys.add("poolAgreement");
			values.add(poolAgreement);
		}

		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List resList = this.find(hql.toString(), keis, values.toArray());

		if (resList != null && resList.size() > 0) {
			if (resList.get(0) != null) {
				openFlag = String.valueOf(resList.get(0));
			}
		}

		return openFlag;
	}

	public QueryResult queryDraftAccountManagement(PoolQueryBean pool) throws Exception {
		List recvList = new ArrayList();
		List needList = new ArrayList();
		this.dao.flush();
		if("QY_00".equals(pool.getPoolEquities())){
			// QY_01 应收票据
			recvList = this.dao.SQLQuery(getQuerySql(pool, "QY_01").toString());
			// QY_02 应付票据
			 needList = this.dao.SQLQuery(getQuerySql(pool, "QY_02").toString());
		}else if("QY_01".equals(pool.getPoolEquities())){
			// QY_01 应收票据
			 recvList = this.dao.SQLQuery(getQuerySql(pool, "QY_01").toString());
		}else if("QY_02".equals(pool.getPoolEquities())){
			// QY_02 应付票据
			 needList = this.dao.SQLQuery(getQuerySql(pool, "QY_02").toString());
		}
		if (recvList == null && recvList.size() == 0 && needList == null && needList.size() == 0) {
			throw new Exception("没有符合条件的数据!");
		} else {
			List list = new ArrayList();
			QueryResult queryResult = new QueryResult();
			// 根据返回的结果集再使用MAP去处理
			Map resultMap = new HashMap();
			Map amrMap = null;
			BigDecimal recvAmount = new BigDecimal("0");// 应收金额
			BigDecimal needAmount = new BigDecimal("0");// 应付金额（负值）
			if (recvList != null && recvList.size() > 0) {
				for (int i = 0; i < recvList.size(); i++) {
					Object[] obj = (Object[]) recvList.get(i);
					if (obj[0] != null && !resultMap.containsKey(obj[0])) {
						recvAmount = new BigDecimal(obj[1] + "");
						needAmount = new BigDecimal("0");
						amrMap = new HashMap();
						amrMap.put("BILL_INFO_ARRAY.SUM_DATE", obj[0]);
						amrMap.put("BILL_INFO_ARRAY.RECV_BILL_AMT", recvAmount);
						amrMap.put("BILL_INFO_ARRAY.NEED_PAY_BILL_AMT", needAmount);
						amrMap.put("BILL_INFO_ARRAY.BILL_RIGHTS_AMT", recvAmount.add(needAmount));
						list.add(amrMap);
						resultMap.put(obj[0], amrMap);
					}
				}
			}
			if (needList != null && needList.size() > 0) {
				for (int j = 0; j < needList.size(); j++) {
					Object[] obj = (Object[]) needList.get(j);
					if (obj[0] != null) {
						// 已经有应收金额
						if (resultMap.containsKey(obj[0])) {
							amrMap = (Map) resultMap.get(obj[0]);
							needAmount = new BigDecimal(obj[1] + "");
							needAmount = BigDecimalUtils.multiply(needAmount, new BigDecimal(-1));//负数
							amrMap.put("BILL_INFO_ARRAY.NEED_PAY_BILL_AMT", needAmount);
							// 先获取应收金额，权益=应收+应付（负值）
							recvAmount = (BigDecimal) amrMap.get("BILL_INFO_ARRAY.RECV_BILL_AMT");
							amrMap.put("BILL_INFO_ARRAY.BILL_RIGHTS_AMT", recvAmount.add(needAmount));
						} else {
							// 没有应收金额，只有应付金额（负值）
							recvAmount = new BigDecimal("0");
							needAmount = new BigDecimal(obj[1] + "");
							needAmount = BigDecimalUtils.multiply(needAmount, new BigDecimal(-1));//负数
							amrMap = new HashMap();
							amrMap.put("BILL_INFO_ARRAY.SUM_DATE", obj[0]);
							amrMap.put("BILL_INFO_ARRAY.RECV_BILL_AMT", recvAmount);
							amrMap.put("BILL_INFO_ARRAY.NEED_PAY_BILL_AMT", needAmount);
							amrMap.put("BILL_INFO_ARRAY.BILL_RIGHTS_AMT", recvAmount.add(needAmount));
							list.add(amrMap);
							resultMap.put(obj[0], amrMap);
						}
					}
				}
			}
			queryResult.setRecords(list);
			queryResult.setTotalCount(list.size());
			return queryResult;
		}
	}

	private StringBuffer getQuerySql(PoolQueryBean pool, String assetType) throws Exception {
		StringBuffer sql = new StringBuffer();
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(" WHERE 1=1");
		if (StringUtil.isNotEmpty(pool.getSCustOrgCode())) {
			whereSql.append("  and  (( t.cust_no = '" + pool.getSCustOrgCode() + "'");
		}
		if (StringUtil.isNotEmpty(pool.getProtocolNo())) {
			whereSql.append(" and t.BPS_NO = '" + pool.getProtocolNo() + "' AND t.STATUS_FLAG = '1' ");
		}
			
		whereSql.append(" ) OR ( t.cust_no = '" + pool.getSCustOrgCode() + "' AND t.STATUS_FLAG = '0' )) ");
		
		if (StringUtil.isNotEmpty(assetType)) {
			whereSql.append(" and t.asset_type = '" + assetType + "'");
		}
		if (StringUtil.isNotEmpty(pool.getBillType())) {
			if (!"0000".equals(pool.getBillType())) {
				whereSql.append(" and t.DRAFT_TYPE = '" + pool.getBillType() + "'");
			}
		}
		if (StringUtil.isNotEmpty(pool.getSBillMedia())) {
			if (!"0".equals(pool.getSBillMedia())) {
				whereSql.append(" and t.DRAFT_MEDIA = '" + pool.getSBillMedia() + "'");
			}
		}
		int sumValue = 0;
		String pstartDate = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
		String pendDate = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
		Date fDate = new Date();
		Date sDate = new Date();
		if (null != pool.getPstartDate()) {
			fDate = pool.getPstartDate();
			pstartDate = DateUtils.toString(pool.getPstartDate(), DateUtils.ORA_DATE_FORMAT);
		}
		if (null != pool.getPendDate()) {
			sDate = pool.getPendDate();
			pendDate = DateUtils.toString(pool.getPendDate(), DateUtils.ORA_DATE_FORMAT);
		}
		int range = DateUtils.getDayInRange(fDate, sDate);
		/**
		 * PL_01 每月月末 PL_02 间隔天数，即每隔几天 PL_03 每周定期，即每周几 PL_04 每月定日，每月几号 PL_05每季度定日 ，每季度几号
		 */
		sql.append("select to_char(tt.rq, 'yyyyMMdd') , sum(dam.isse_amt) from (");
		sql.append(" select * from DRAFT_ACCOUNT_MANAGEMENT t");
		sql.append(whereSql);
		sql.append(") dam,");
		if (pool.getSumType().equals("PL_01")) {// PL_01 每月月末
			sql.append(" (select distinct last_day(to_date('" + pstartDate + "', 'yyyyMMdd') + (ROWNUM - 1)) as rq");
			sql.append(" from dual");
			sql.append(" CONNECT BY rownum <= " + range + ") tt");
		} else if (pool.getSumType().equals("PL_02")) {// 间隔天数，即每隔几天
			if (StringUtil.isNotEmpty(pool.getSumValue())) {
				sumValue = Integer.valueOf(pool.getSumValue());
			} else {
				throw new Exception("每隔的选项为空");
			}
			range = (int) Math.ceil(range / sumValue);
			sql.append(" (select distinct to_date('" + pstartDate + "', 'yyyyMMdd') + (ROWNUM - 1)*" + sumValue
					+ " as rq");
			sql.append(" from dual");
			sql.append(" CONNECT BY rownum <= " + range + ") tt");
		} else if (pool.getSumType().equals("PL_03")) {// 每周定期，即每周几
			if (StringUtil.isNotEmpty(pool.getSumValue())) {
				sumValue = Integer.valueOf(pool.getSumValue());
			} else {
				throw new Exception("每周的选项为空");
			}
			// 先获取开始日期为周几，再此基础上获取到下个每周sumValue的日期
			Date theDate = fDate;
			int num = DateUtils.getDayOfWeek(fDate);
			if (num > sumValue + 1) {
				theDate = DateUtils.getDate(theDate, 7 - num + sumValue + 1);
			} else {
				theDate = DateUtils.getDate(theDate, sumValue + 1 - num);
			}
			pstartDate = DateUtils.toString(theDate, DateUtils.ORA_DATE_FORMAT);
			range = (int) Math.ceil(range / 7);
			sql.append(" (select distinct to_date('" + pstartDate + "', 'yyyyMMdd') + (ROWNUM - 1)*7 as rq");
			sql.append(" from dual");
			sql.append(" CONNECT BY rownum <= " + range + ") tt");
		} else if (pool.getSumType().equals("PL_04")) {// 每月定日，每月几号
			if (StringUtil.isNotEmpty(pool.getSumValue())) {
				// 每月开始为0
				sumValue = Integer.valueOf(pool.getSumValue()) - 1;
			} else {
				throw new Exception("每月的选项为空");
			}
			sql.append(" (select distinct add_months(trunc(to_date('" + pstartDate + "', 'yyyyMMdd')+ " + sumValue+"), ROWNUM-1) as rq");
			sql.append(" from dual");
			sql.append(" CONNECT BY rownum <= ceil(MONTHS_BETWEEN(to_date('" + pendDate + "', 'yyyyMMdd'),to_date('"
					+ pstartDate + "', 'yyyyMMdd')))) tt");
		} else if (pool.getSumType().equals("PL_05")) {// 每季度定日 ，每季度几月几号
			StringBuffer monDayBuffer = new StringBuffer();
			if (StringUtil.isNotEmpty(pool.getSumValue())) {
				try {
					String[] monDay = pool.getSumValue().split("\\|", 2);
					int months = Integer.valueOf(monDay[0]);
					int days = Integer.valueOf(monDay[1]);
					Date theDate = getTheRecentDay(months, days, fDate, sDate);
					String newStrDate = DateUtils.toString(theDate, DateUtils.ORA_DATE_FORMAT);
					
					sql.append(" (select distinct add_months(trunc(to_date('" + newStrDate
							+ "', 'yyyyMMdd')), (ROWNUM-1)*3) as rq");
					sql.append(" from dual");
					sql.append(" CONNECT BY rownum <= ceil(MONTHS_BETWEEN(to_date('" + pendDate + "', 'yyyyMMdd'),to_date('"
							+ pstartDate + "', 'yyyyMMdd'))/3)) tt");
				} catch (Exception e) {
					logger.error("每季度的选项["+pool.getSumValue()+"]", e);
					throw new Exception("每季度的选项["+pool.getSumValue()+"]有误!");
				}
			} else {
				throw new Exception("每季度的选项为空");
			}
		} else {// 默认按天
			sql.append(" (select distinct to_date('" + pstartDate + "', 'yyyyMMdd') + (ROWNUM - 1) as rq");
			sql.append(" from dual");
			sql.append(" CONNECT BY rownum <= " + range + ") tt");
		}
		sql.append(" where tt.rq between dam.isse_dt and dam.due_dt");
		sql.append("  group by to_char(tt.rq, 'yyyyMMdd')");
		sql.append(" order by to_char(tt.rq, 'yyyyMMdd')");
		return sql;
	}

	// 将起始日期与季度的第一个月份日期比较，如果小于则递加三个月，确定最近的季度月份，并且小于结束日期
	private Date getTheRecentDay(int months, int days, Date fDate, Date eDate) throws Exception{
		// 获取当年months月days的日期
		Date sourceDate = DateUtils.getNextNMonth(DateUtils.getFirstDayOfYear(fDate), months-1);
		Date dayDate = DateUtils.adjustDateByDay(sourceDate, days-1);
		Date lastDayDate = DateUtils.getLastDayOfMonth(sourceDate);
		Date fdo = dayDate;
		if(DateUtils.checkOverLimited(dayDate, lastDayDate)) {
			fdo = lastDayDate;
		}
		if (DateUtils.checkOverLimited(fdo, fDate)) {
			if (DateUtils.checkOverLimited(fdo, eDate) == false) {
				return fdo;
			}
		} else {
			// 递归查找同季度的下一个月份
			return getTheRecentDay(months + 3, days, fDate, eDate);
		}
		// 没有找到合适日期，返回null
		return null;
	}
	

	public QueryResult queryDraftAccount(PoolQueryBean pool) throws Exception {
		List keys = new ArrayList();
		List values = new ArrayList();
		StringBuffer hql = new StringBuffer("select dam from DraftAccountManagement dam where 1=1");

		if (StringUtil.isNotEmpty(pool.getAssetType())) { // 资产类型
			hql.append(" and dam.assetType=:assetType");
			keys.add("assetType");
			values.add(pool.getAssetType());
		}

		if (StringUtil.isNotEmpty(pool.getCirStage())) { // 流转阶段
			hql.append(" dam.transferPhase=:transferPhase");
			keys.add("transferPhase");
			values.add(pool.getCirStage());
		}

		if (StringUtil.isNotEmpty(pool.getBillType()) && !"0000".equals(pool.getBillType())) { // 票据种类
			hql.append(" dam.draftType=:draftType");
			keys.add("draftType");
			values.add(pool.getBillType());
		}

		if (StringUtil.isNotEmpty(pool.getSBillMedia())&& !"0".equals(pool.getBillType())) { // 票据介质
			hql.append(" dam.draftMedia=:draftMedia");
			keys.add("draftMedia");
			values.add(pool.getSBillMedia());
		}

		if (pool.getPstartDate() != null) {// 出票日开始
			hql.append(" and dam.isseDt>=:isseDt");
			keys.add("isseDt");
			values.add(pool.getPstartDate());
		}
		if (pool.getPendDate() != null) {// 出票日结束
			hql.append(" and dam.isseDt<=:isseDt2");
			keys.add("isseDt2");
			values.add(pool.getPendDate());
		}
		if (pool.getStartDDueDt() != null) {// 到期日开始
			hql.append(" and dam.dueDt>=:dueDt");
			keys.add("dueDt");
			values.add(pool.getStartDDueDt());
		}
		if (pool.getEndDDueDt() != null) {// 到期日结束
			hql.append(" and dam.dueDt<=:dueDt2");
			keys.add("dueDt2");
			values.add(pool.getEndDDueDt());
		}

		if (pool.getIsseAmtStart() != null) {// 票据金额开始
			hql.append(" and dam.isseAmt>=:isseAmt");
			keys.add("isseAmt");
			values.add(pool.getIsseAmtStart());
		}
		if (pool.getIsseAmtEnd() != null) {// 票据金额结束
			hql.append(" and dam.isseAmt<=:isseAmt2");
			keys.add("isseAmt2");
			values.add(pool.getIsseAmtEnd());
		}

		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List result = this.find(hql.toString(), keis, values.toArray());

		if (result != null && result.size() > 0) {
			return responseDataHandlerPJC028(result);
		}
		throw new Exception("没有符合条件的数据!");
	}

	private QueryResult responseDataHandlerPJC028(List result) {
		ArrayList list = new ArrayList();
		BigDecimal totalAmount = new BigDecimal(0);
		QueryResult queryResult = new QueryResult();
		for (int i = 0; i < result.size(); i++) {
			DraftAccountManagement draftAccount = (DraftAccountManagement) result.get(i);
			HashMap map = new HashMap();
			map.put("ORGCODE", draftAccount.getCustNo()); // 客户号
			map.put("ZCLX", draftAccount.getAssetType()); // 资产类型
			map.put("LZJD", draftAccount.getTransferPhase()); // 流转阶段
			map.put("PJHM", draftAccount.getDraftNb()); // 票据号码
			map.put("PJZL", draftAccount.getDraftType()); // 票据种类
			map.put("PJJZ", draftAccount.getDraftMedia()); // 票据介质
			map.put("CPR", draftAccount.getIsseDt()); // 出票日
			map.put("DQR", draftAccount.getDueDt()); // 到期日
			map.put("PJJE", draftAccount.getIsseAmt()); // 票面金额
			totalAmount = totalAmount.add(new BigDecimal(draftAccount.getIsseAmt() + ""));
			list.add(map);
		}
		queryResult.setRecords(list);
		queryResult.setTotalAmount(totalAmount);
		queryResult.setTotalCount(result.size());
		return queryResult;
	}

	@Override
	public QueryResult queryPoolBillInfoByParams(QueryParameter pq, Page page) throws Exception {
		QueryResult qr = new QueryResult();

		List records = new ArrayList();
		List poolLists = new ArrayList();

		String amountFieldName = "FBillAmount";
		records = this.queryPoolBillInfoList(pq, page);
		
		/*
		 * 向信贷系统进行额度校验
		 */
		if(pq.getQueryType().equals("01")){
			blackListManageService.txMisCreditCheck(records);
		}
		
		if (records != null && records.size() > 0) {
			qr = QueryResult.buildQueryResult(records, amountFieldName);
		} else {
			return null;
		}
		return qr;
	}

	 public List queryPoolBillInfoList(QueryParameter pq, Page page) throws Exception {
	        List keys = new ArrayList();
	        List values = new ArrayList();
	        StringBuffer hql = null;
	        String queryType = pq.getQueryType();// 查询类型

	        hql = new StringBuffer(" from PoolBillInfo pool where 1=1 ");
	        if("01".equals(queryType)){//可入池	只用客户号查询  客户自己决定入哪个池
	        	hql.append(" and pool.SDealStatus=:SDealStatus ");
	            keys.add("SDealStatus");
	            values.add(PoolComm.DS_00);
	            hql.append(" and (pool.ebkLock !=:ebkLock or pool.ebkLock is null) ");
	            keys.add("ebkLock");
	            values.add(PoolComm.EBKLOCK_01);
	            
	            hql.append(" and ((pool.SBillType = 'AC01' and pool.SAcceptorAccount = '0') or pool.SBillType = 'AC02') ");//银承时承兑行账号为0的可入池
	        }else{//可出池	用票据池编号客户号确定唯一
	        	hql.append(" and pool.SDealStatus=:SDealStatus ");
	            keys.add("SDealStatus");
	            values.add(PoolComm.DS_02);
	            hql.append(" and pool.poolAgreement=:poolAgreement ");
		        keys.add("poolAgreement");
		        values.add(pq.getPoolArgumentNo());
		        hql.append(" and (pool.ebkLock !=:ebkLock or pool.ebkLock is null) ");
	            keys.add("ebkLock");
	            values.add(PoolComm.EBKLOCK_01);
	            hql.append(" and (pool.bbspLock is null or pool.bbspLock =:bbspLock) ");
	            keys.add("bbspLock");
	            values.add(PoolComm.BBSPLOCK_02);
	            
	        }
	        
	        hql.append(" and pool.custNo=:custNo ");
	        keys.add("custNo");
	        values.add(pq.getOrgCode());
	        
        /* hql.append(" and pool.DDueDt>=:sysDate");
         keys.add("sysDate");
         values.add(DateUtils.getWorkDayDate());*/
         
	        if (StringUtil.isNotBlank(pq.getBillNo())) {// 票据号码
	            hql.append(" and pool.SBillNo=:SBillNo ");
	            keys.add("SBillNo");
	            values.add(pq.getBillNo());
	        }
	        
	        /***********************************融合改造新增字段start******************/
	        if (StringUtil.isNotBlank(pq.getBeginRangeNo())) {// 子票起始号
	            hql.append(" and pool.beginRangeNo=:beginRangeNo ");
	            keys.add("beginRangeNo");
	            values.add(pq.getBeginRangeNo());
	        }
	        if (StringUtil.isNotBlank(pq.getEndRangeNo())) {// 子票截止
	            hql.append(" and pool.endRangeNo=:endRangeNo ");
	            keys.add("endRangeNo");
	            values.add(pq.getEndRangeNo());
	        }
	        if (StringUtil.isNotBlank(pq.getDraftSource())) {// 票据来源
	            hql.append(" and pool.draftSource=:draftSource ");
	            keys.add("draftSource");
	            values.add(pq.getDraftSource());
	        }
	        if (StringUtil.isNotBlank(pq.getSplitFlag())) {// 是否允许拆分标记 1是 0否
	            hql.append(" and pool.splitFlag=:splitFlag ");
	            keys.add("splitFlag");
	            values.add(pq.getSplitFlag());
	        }
			/***********************************融合改造新增字段end******************/

	            if (StringUtil.isNotBlank(pq.getBillType()) && !"0000".equals(pq.getBillType())) {// 票据种类
         		hql.append(" and pool.SBillType=:SBillType ");
                 keys.add("SBillType");
                 values.add(pq.getBillType());
	            }
	            if (StringUtil.isNotBlank(pq.getBillMedia())) {// 票据介质
	            	if(!("0").equals(pq.getBillMedia())){
	            		hql.append(" and pool.SBillMedia=:SBillMedia ");
	            		keys.add("SBillMedia");
	            		values.add(pq.getBillMedia());
	            	}
	            }
	            if (pq.getIssueDateBegin() != null) {// 出票日开始
	                hql.append(" and pool.DIssueDt>=:DIssueDt1");
	                keys.add("DIssueDt1");
	                values.add(pq.getIssueDateBegin());
	            }
	            if (pq.getIssueDateEnd() != null) {// 出票日结束
	                hql.append(" and pool.DIssueDt<=:DIssueDt2");
	                keys.add("DIssueDt2");
	                values.add(pq.getIssueDateEnd());
	            }
	            Date today = DateUtils.getWorkDayDate();
	            if (pq.getDueDateBegin() != null) {// 到期日开始
	            	if (DateUtils.checkOverLimited(pq.getDueDateBegin(),today)) {
	            		hql.append(" and pool.DDueDt>=:DDueDt1");
	            		keys.add("DDueDt1");
	            		values.add(pq.getDueDateBegin());
	            	} else {
	            		hql.append(" and pool.DDueDt>:DDueDt1");
	            		keys.add("DDueDt1");
	            		values.add(today);
	            	}
	            } else {
         		hql.append(" and pool.DDueDt>:DDueDt1");
         		keys.add("DDueDt1");
         		values.add(today);
	            }
	            if (pq.getDueDateEnd() != null) {// 到期日结束
	            	hql.append(" and pool.DDueDt<=:DDueDt2");
	            	keys.add("DDueDt2");
	            	values.add(pq.getDueDateEnd());
	            } 
	            if (StringUtil.isNotBlank(pq.getPlDrwrAcctSvcrNm())) {//出票人开户行名称
	                hql.append(" and pool.SIssuerBankName like :SIssuerBankName ");
	                keys.add("SIssuerBankName");
	                values.add("%" + pq.getPlDrwrAcctSvcrNm() + "%");
	            }
	            if (pq.getBillAmountBegin() != null) {// 票据金额下限
	                hql.append(" and pool.FBillAmount>=:FBillAmount1 ");
	                keys.add("FBillAmount1");
	                values.add(pq.getBillAmountBegin());
	            }
	            if (pq.getBillAmountEnd() != null) {// 票据金额上限
	                hql.append(" and pool.FBillAmount<=:FBillAmount2 ");
	                keys.add("FBillAmount2");
	                values.add(pq.getBillAmountEnd());
	            }
	           
	            if (StringUtil.isNotBlank(pq.getAcceptorBankCode())) {//票据承兑行行号
	                hql.append(" and pool.SAcceptorBankCode=:SAcceptorBankCode ");
	                keys.add("SAcceptorBankCode");
	                values.add(pq.getAcceptorBankCode());
	            }
	            if (StringUtil.isNotBlank(pq.getIsGenerateEdu())) {//是否产生额度
	                if("0".equals(pq.getIsGenerateEdu())){//不产生额度
	                	hql.append(" and pool.rickLevel=:rickLevel ");
		                keys.add("rickLevel");
		                values.add(PoolComm.NOTIN_RISK);
	                }else if("1".equals(pq.getIsGenerateEdu())){//产生额度
	                	if (StringUtil.isNotBlank(pq.getRiskType())) {//风险等级
	    	                hql.append(" and pool.rickLevel=:rickLevel ");
	    	                keys.add("rickLevel");
	    	                values.add(pq.getRiskType());
	    	            } else {
	    	            	List riskList = new ArrayList();
	    	            	riskList.add(PoolComm.LOW_RISK);
	    	            	riskList.add(PoolComm.HIGH_RISK);
	    	            	hql.append(" and pool.rickLevel in (:rickLevel) ");
	    	            	keys.add("rickLevel");
	    	            	values.add(riskList);
	    	            }
	                } else {
	                	// 其他情况暂时不考虑
	                }
	            }else{
	            	if (StringUtil.isNotBlank(pq.getRiskType())) {//风险等级
    	                hql.append(" and pool.rickLevel=:rickLevel ");
    	                keys.add("rickLevel");
    	                values.add(pq.getRiskType());
    	            }
	            }
	            
	            if (StringUtil.isNotBlank(pq.getSBanEndrsmtFlag())) {//不得转让标记
	            	hql.append(" and pool.SBanEndrsmtFlag=:SBanEndrsmtFlag ");
	                keys.add("SBanEndrsmtFlag");
	                values.add(pq.getSBanEndrsmtFlag());
	            }
	            
	            if (StringUtil.isNotBlank(pq.getDrawerAcctName())) {//出票人账户名称
	            	hql.append(" and pool.SIssuerAcctName=:SIssuerAcctName ");
	                keys.add("SIssuerAcctName");
	                values.add(pq.getDrawerAcctName());
	            }
	            
	            if (StringUtil.isNotBlank(pq.getAcceptorAcctName())) {//承兑人账户名称
	            	hql.append(" and pool.SAcceptorAcctName=:SAcceptorAcctName ");
	                keys.add("SAcceptorAcctName");
	                values.add(pq.getAcceptorAcctName());
	            }
	            

	        hql.append(" order by pool.DDueDt desc ");
	        String[] keis = (String[]) keys.toArray(new String[keys.size()]);
	        List result = this.find(hql.toString(), keis, values.toArray(), page);
	        if(result != null && result.size() > 0){
	        	return result;
	        }
	        return null;
	    }

	// PJC005,池票据组合查询——虚拟票据池查询
	public List queryVtrustDraftInfos(PoolQueryBean pq, Page page) {
		StringBuffer hql = new StringBuffer("from PoolVtrust as dto where 1=1");
		List keys = new ArrayList();
		List values = new ArrayList();
		
		hql.append(" and dto.vtStatus =:vtStatus");
		keys.add("vtStatus");
		values.add(PoolComm.DS_00);
		
		if (StringUtil.isNotBlank(pq.getProtocolNo())) {// 票据池编号
			hql.append(" and dto.bpsNo=:bpsNo");
			keys.add("bpsNo");
			values.add(pq.getProtocolNo());
		}
		
		if (StringUtil.isNotBlank(pq.getCustomernumber())) {// 客户编号
			hql.append(" and dto.vtEntpNo=:vtEntpNo");
			keys.add("vtEntpNo");
			values.add(pq.getCustomernumber());
		}
		
		if (StringUtil.isNotBlank(pq.getBillNo())) {// 票据号码
			hql.append(" and dto.vtNb=:vtNb");
			keys.add("vtNb");
			values.add(pq.getBillNo());
		}

		if (StringUtil.isNotBlank(pq.getRecePayType())) {// 收付类型
			if(!"QY_00".equals(pq.getRecePayType())){
				hql.append(" and dto.payType=:payType");
				keys.add("payType");
				values.add(pq.getRecePayType());
			}
		}

		if (StringUtil.isNotBlank(pq.getBillType())) {// 票据种类
			if (!"0000".equals(pq.getBillType())) {
				hql.append(" and dto.vtType=:vtType");
				keys.add("vtType");
				values.add(pq.getBillType());
			}
		}
		if (StringUtil.isNotEmpty(pq.getSBillMedia()) && !pq.getSBillMedia().equals("0")) {// 票据介质
			hql.append(" and dto.vtDraftMedia=:vtDraftMedia");
			keys.add("vtDraftMedia");
			values.add(pq.getSBillMedia());
		}
		if (StringUtil.isNotBlank(pq.getBillOutName())) {// 出票人名称
			hql.append(" and dto.vtdrwrName like:vtdrwrName");
			keys.add("vtdrwrName");
			values.add("%" + pq.getBillOutName() + "%");
		}
		if (StringUtil.isNotBlank(pq.getReceMoneName())) {// 收款人名称
			hql.append(" and dto.vtpyeeName like:vtpyeeName");
			keys.add("vtpyeeName");
			values.add("%" + pq.getReceMoneName() + "%");
		}

		if (pq.getPstartDate() != null) {// 出票日开始
			hql.append(" and dto.vtisseDt>=:vtisseDt1");
			keys.add("vtisseDt1");
			values.add(pq.getPstartDate());
		}
		if (pq.getPendDate() != null) {// 出票日结束
			hql.append(" and dto.vtisseDt<=:vtisseDt2");
			keys.add("vtisseDt2");
			values.add(pq.getPendDate());
		}
		if (pq.getStartDDueDt() != null) {// 到期日开始
			hql.append(" and dto.vtdueDt>=:vtdueDt1");
			keys.add("vtdueDt1");
			values.add(pq.getStartDDueDt());
		}
		if (pq.getEndDDueDt() != null) {// 到期日结束
			hql.append(" and dto.vtdueDt<=:vtdueDt2");
			keys.add("vtdueDt2");
			values.add(pq.getEndDDueDt());
		}
		if (pq.getIsseAmtStart() != null) {// 票据金额下限
			hql.append(" and dto.vtisseAmt>=:vtisseAmt1");
			keys.add("vtisseAmt1");
			values.add(pq.getIsseAmtStart());
		}
		if (pq.getIsseAmtEnd() != null) {// 票据金额上限
			hql.append(" and dto.vtisseAmt<=:vtisseAmt2");
			keys.add("vtisseAmt2");
			values.add(pq.getIsseAmtEnd());
		}

		if (StringUtil.isNotBlank(pq.getBillOutBankName())) {//出票人开户行名称
			hql.append(" and dto.vtdrwrBankName like:vtdrwrBankName");
			keys.add("vtdrwrBankName");
			values.add("%"+pq.getBillOutBankName()+"%");
		}
		if (StringUtil.isNotBlank(pq.getIsEdu())&&pq.getIsEdu().equals("1")) {//是否已产生额度 1 是
			hql.append(" and dto.rickLevel in (:rickLevel) ");
			keys.add("rickLevel");
			List rickLevel = new ArrayList();
			rickLevel.add("FX_01"); //FX_01 低风险
			rickLevel.add("FX_02"); //FX_02 高风险
			values.add(rickLevel);
		}else if (StringUtil.isNotBlank(pq.getIsEdu())&&pq.getIsEdu().equals("0")) {//是否已产生额度 0否
			hql.append(" and dto.rickLevel =:rickLevel ");
			keys.add("rickLevel");
			values.add("FX_03"); //FX_03 不在风险名单
		}
		if (StringUtil.isNotBlank(pq.getRickLevel())) {//风险等级
			hql.append(" and dto.rickLevel =:rickLevel");
			keys.add("rickLevel");
			values.add(pq.getRickLevel());
		}
		
		/*********************************融合改造新增过滤条件start 2022-03-24 wfj*************************************************/
		if (StringUtil.isNotBlank(pq.getBeginRangeNo())) {//子票起始号
			hql.append(" and dto.beginRangeNo =:beginRangeNo");
			keys.add("beginRangeNo");
			values.add(pq.getBeginRangeNo());
		}
		if (StringUtil.isNotBlank(pq.getEndRangeNo())) {//子票截止
			hql.append(" and dto.endRangeNo =:endRangeNo");
			keys.add("endRangeNo");
			values.add(pq.getEndRangeNo());
		}
		if (StringUtil.isNotBlank(pq.getDraftSource())) {//票据来源
			hql.append(" and dto.draftSource =:draftSource");
			keys.add("draftSource");
			values.add(pq.getDraftSource());
		}
		if (StringUtil.isNotBlank(pq.getSplitFlag())) {//是否允许拆分标记 1是 0否
			hql.append(" and dto.splitFlag =:splitFlag");
			keys.add("splitFlag");
			values.add(pq.getSplitFlag());
		}

		/*********************************融合改造新增过滤条件end 2022-03-24 wfj*************************************************/


		hql.append(" order by dto.vtdueDt desc ");
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List rsut = this.find(hql.toString(), keis, values.toArray(), page);
		if (rsut != null && rsut.size() > 0) {
			return rsut;
		}
		return null;
	}
	

	// 票据总额度查询
	private BigDecimal queryTotalBillAmount(String custNo) {
		String hql = "select sum(at.crdtTotal) from AssetType at,AssetPool ap where at.apId=ap.apId and at.astType='ED_10' and ap.custNo=?";
		List param = new ArrayList();
		param.add(custNo);
		List temp = this.find(hql, param);
		BigDecimal totalBillAmount = new BigDecimal("0");
		if (temp != null && temp.size() > 0) {
			if (null != temp.get(0)) {
				totalBillAmount = new BigDecimal(String.valueOf(temp.get(0)));
			}
		}
		return totalBillAmount;

	}

	// 高风险额度查询
	private EduResult queryHighRiskAmount(String custNo) {
		EduResult eduResult = new EduResult();
		String hql = "select sum(at.crdtTotal),sum(at.crdtUsed),sum(at.crdtFree) from AssetType at,AssetPool ap where at.apId=ap.apId and at.astType ='ED_20' and ap.custNo=?";
		List param = new ArrayList();
		param.add(custNo);
		List queryRet = this.find(hql, param);
		if (queryRet != null && queryRet.size() > 0) {
			Object[] obj = (Object[]) queryRet.get(0);
			if (obj[0] != null && !"".equals(obj[0])) {
				eduResult.setHighRiskAmount(new BigDecimal(obj[0].toString()));// 高风险总额度
			}
			if (obj[1] != null && !"".equals(obj[1])) {
				eduResult.setUsedHighRiskAmount(new BigDecimal(obj[1].toString()));// 高风险已用额度
			}
			if (obj[2] != null && !"".equals(obj[2])) {
				eduResult.setFreeHighRiskAmount(new BigDecimal(obj[2].toString()));// 高风险可用额度
			}
		}

		return eduResult;

	}

	// 低风险额度查询
	private EduResult queryLowRiskAmount(String custNo) {
		EduResult eduResult = new EduResult();
		String hql = "select sum(at.crdtTotal),sum(at.crdtUsed),sum(at.crdtFree) from AssetType at,AssetPool ap where at.apId=ap.apId and (at.astType ='ED_10' ) and ap.custNo=?";
		List param = new ArrayList();
		param.add(custNo);

		List queryRet = this.find(hql, param);

		if (queryRet != null && queryRet.size() > 0) {
			Object[] obj = (Object[]) queryRet.get(0);
			if (obj[0] != null && !"".equals(obj[0])) {
				eduResult.setLowRiskAmount(new BigDecimal(obj[0].toString()));// 低风险总额度
			}
			if (obj[1] != null && !"".equals(obj[1])) {
				eduResult.setUsedLowRiskAmount(new BigDecimal(obj[1].toString()));// 低风险已用额度
			}
			if (obj[2] != null && !"".equals(obj[2])) {
				eduResult.setFreeLowRiskAmount(new BigDecimal(obj[2].toString()));// 低风险可用
			}
		}
		return eduResult;
	}

	// 保证金余额查询
	private EduResult querybailAmount(String custNo) {
		String hql = "select sum(at.crdtTotal) , sum(at.crdtFree) , sum(at.crdtUsed)  from AssetType at,AssetPool ap where at.apId=ap.apId and at.astType ='ED_21' and ap.custNo=?";
		List param = new ArrayList();
		param.add(custNo);
		List temp = this.find(hql, param);
		EduResult bailResult = new EduResult();
		if (temp != null && temp.size() > 0) {
			Object[] obj = (Object[]) temp.get(0);
			if (obj[0] != null && !"".equals(obj[0])) {
				bailResult.setBailAmountTotail(new BigDecimal(obj[0].toString()));// 保证金总额度
			}
			if (obj[1] != null && !"".equals(obj[1])) {
				bailResult.setBailAmount(new BigDecimal(obj[1].toString()));// 保证金已用额度
			}
			if (obj[2] != null && !"".equals(obj[2])) {
				bailResult.setBailAmountUsed(new BigDecimal(obj[2].toString()));// 保证金可用
			}
		}

		return bailResult;
	}

	@Override
	public List queryManagement(PoolQueryBean bean, Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append("select dm from DraftAccountManagement  dm where 1=1 ");

		if (bean.getCustomernumber() != null && !"".equals(bean.getCustomernumber())){
			hql.append(" and dm.custNo = :custNo");
			paramName.add("custNo");
			paramValue.add(bean.getCustomernumber());
		}

		//票据池编号
		if(bean.getProtocolNo() != null && !"".equals(bean.getProtocolNo())){
			hql.append(" and ( dm.bpsNo = :bpsNo  or (dm.statusFlag ='"+PoolComm.NO+"'))");
			paramName.add("bpsNo");
			paramValue.add(bean.getProtocolNo());
		}
//		hql.append(" or (dm.statusFlag ='"+PoolComm.DS_00+"'))");
		logger.info("hql*************"+hql);
		// 票据权益
		if (!"".equals((bean.getPoolEquities()))&& !"QY_00".equals(bean.getPoolEquities())) {
			hql.append(" and dm.assetType = :assetType");
			paramName.add("assetType");
			String qy = bean.getPoolEquities();
			if ("QY_01".equals(qy)) {// 应收
				qy = PoolComm.QY_01;
			} else {// 应付
				qy = PoolComm.QY_02;
			}
			paramValue.add(qy);
			logger.info("票据权益为["+qy+"]");
		}

		// 是否托管票据
		if (bean.getIsTrustee() != null && !"".equals((bean.getIsTrustee()))) {
			hql.append(" and dm.trusteeshipFalg = :trusteeshipFalg");
			paramName.add("trusteeshipFalg");
			paramValue.add(bean.getIsTrustee());
		}

		// 票据保管地
		if (bean.getLocation() != null && !"".equals((bean.getLocation()))) {
				hql.append(" and dm.billSaveAddr = :billSaveAddr");
				paramName.add("billSaveAddr");
				paramValue.add(bean.getLocation());
		}
		// 票号
		if (bean.getBillNo() != null && !"".equals((bean.getBillNo()))) {
			hql.append(" and dm.draftNb = :draftNb");
			paramName.add("draftNb");
			paramValue.add(bean.getBillNo());
		}
		// 票据类型 纸票/电票
		if (StringUtil.isNotEmpty(bean.getSBillMedia())&&!"0".equals(bean.getSBillMedia())) {
			hql.append(" and dm.draftMedia = :draftMedia");
			paramName.add("draftMedia");
			paramValue.add(bean.getSBillMedia());
		}
		// 票据种类 银/商
		if (StringUtil.isNotBlank(bean.getBillType())&& !"0000".equals(bean.getBillType())) {
			hql.append(" and dm.draftType = :draftType");
			paramName.add("draftType");
			paramValue.add(bean.getBillType());
		} 

		// 金额上限
		if (bean.getIsseAmtStart() != null && !"".equals((bean.getIsseAmtStart()))) {
			hql.append(" and dm.isseAmt <= :isseAmt1");
			paramName.add("isseAmt1");
			paramValue.add(bean.getIsseAmtStart());
		}
		// 金额下限
		if (bean.getIsseAmtEnd() != null && !"".equals((bean.getIsseAmtEnd()))) {
			hql.append(" and dm.isseAmt >= :isseAmt2");
			paramName.add("isseAmt2");
			paramValue.add(bean.getIsseAmtEnd());
		}

		// 出票日期开始startplReqTime
		if (bean.getPstartDate() != null && !"".equals(bean.getPstartDate())) {
			hql.append(" and dm.isseDt>=:isseDt1");
			paramName.add("isseDt1");
			paramValue.add(bean.getPstartDate());
		}
		// 出票日期结束endplReqTime
		if (bean.getPendDate() != null && !"".equals(bean.getPendDate())) {
			hql.append(" and dm.isseDt<=:isseDt2");
			paramName.add("isseDt2");
			paramValue.add(bean.getPendDate());
		}
		// 到期日期开始startplDueDt
		if (bean.getStartDDueDt() != null && !"".equals(bean.getStartDDueDt())) {
			hql.append(" and dm.dueDt>=:dueDt1");
			paramName.add("dueDt1");
			paramValue.add(bean.getStartDDueDt());
		}
		// 到期日期结束endplDueDt
		if (bean.getEndDDueDt() != null && !"".equals(bean.getEndDDueDt())) {
			hql.append(" and dm.dueDt<=:dueDt2");
			paramName.add("dueDt2");
			paramValue.add(bean.getEndDDueDt());
		}
		// 出票人开户行名称
		if (bean.getBillOutBankNo() != null && !"".equals((bean.getBillOutBankNo()))) {
			hql.append(" and dm.drwrAcctSvcrNm like :drwrAcctSvcrNm");
			paramName.add("drwrAcctSvcrNm");
			paramValue.add("%" + bean.getBillOutBankNo() + "%");
		}
		//是否已产生额度
		if(StringUtil.isNotBlank(bean.getIsEdu())){
			hql.append(" and dm.isEdu = :isEdu");
			paramName.add("isEdu");
			paramValue.add(bean.getIsEdu());
		}
		
		// 风险结果
		if (bean.getRickLevel() != null && !"".equals((bean.getRickLevel()))) {
			hql.append(" and dm.riskLevel = :riskLevel");
			paramName.add("riskLevel");
			paramValue.add(bean.getRickLevel());
		}
		//是否已产生额度
		if(StringUtil.isNotBlank(bean.getRemark())&&bean.getRemark().equals(PoolComm.JD_02)){//PoolComm.JD_02   在池
			hql.append(" and dm.transferPhase = :transferPhase");
			paramName.add("transferPhase");
			paramValue.add(bean.getRemark());
		}
		//是否已产生额度
		if(StringUtil.isNotBlank(bean.getRemark())&&bean.getRemark().equals(PoolComm.NO)){//PoolComm.JD_02   不在池
			hql.append(" and dm.transferPhase != :transferPhase");
			paramName.add("transferPhase");
			paramValue.add(PoolComm.JD_02);
		}
		
        
        /***********************************融合改造新增字段start******************/
        if (StringUtil.isNotBlank(bean.getBeginRangeNo())) {// 子票起始号
            hql.append(" and dm.beginRangeNo=:beginRangeNo ");
            paramName.add("beginRangeNo");
            paramValue.add(bean.getBeginRangeNo());
        }
        if (StringUtil.isNotBlank(bean.getEndRangeNo())) {// 子票截止
            hql.append(" and dm.endRangeNo=:endRangeNo ");
            paramName.add("endRangeNo");
            paramValue.add(bean.getEndRangeNo());
        }
        
        if (StringUtil.isNotBlank(bean.getDraftSource())) {// 票据来源
            hql.append(" and dm.draftSource=:draftSource ");
            paramName.add("draftSource");
            paramValue.add(bean.getDraftSource());
        }
        
        

		/***********************************融合改造新增字段end******************/

		
        hql.append(" order by dm.draftNb desc ");

		/*//持有票据标识(账务管家自动任务)
		if(bean.getSStatusFlag() != null && !"".equals(bean.getSStatusFlag())){
			hql.append(" and dm.statusFlag = :statusFlag");
			paramName.add("statusFlag");
			paramValue.add(bean.getSStatusFlag());
		}*/

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		logger.info("查询参数["+paramNames.length+"],查询值["+paramValue+"]");
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		return res;
	}

	@Override
	public List<PoolBillInfo> queryBillByElecAccAndType(String elecAcc,
			String type) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		List res = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select pool from PoolBillInfo  pool where 1=1 ");
		if(StringUtil.isNotBlank(elecAcc)){
			hql.append(" and pool.accNo = :accNo");
			paramName.add("accNo");
			paramValue.add(elecAcc);
		}
		if(type!=null && "0".equals(type)){//只查初始状态的票
			hql.append(" and pool.SDealStatus = :SDealStatus");
			paramName.add("SDealStatus");
			paramValue.add(PoolComm.DS_00);	
		}else if(type!=null && "1".equals(type)){//去除
			List status = new ArrayList();
			status.add(PoolComm.DS_00);//非初始化的
			status.add(PoolComm.DS_04);//非已经出池的
			status.add(PoolComm.TS05);//非托收记账完毕的
			status.add(PoolComm.DS_99);//
			hql.append(" and  pool.SDealStatus not in(:SDealStatus)");
			paramName.add("SDealStatus"); 
			paramValue.add(status);
		}
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues );
		if(res!=null &&res.size()>0){
			return res;
		}
		return null;
	}
	@Override
	public List<PoolBillInfo> queryBillByElecAccAndTypeNew(String elecAcc,
			String type,String poolAgreement) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		List res = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select pool from PoolBillInfo  pool where 1=1 ");
		if(StringUtil.isNotBlank(elecAcc)){
			hql.append(" and pool.accNo = :accNo");
			paramName.add("accNo");
			paramValue.add(elecAcc);
		}
		if(StringUtil.isNotBlank(poolAgreement)){
			hql.append(" and pool.poolAgreement = :poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(poolAgreement);
		}
		if(type!=null && "0".equals(type)){//只查初始状态的票
			hql.append(" and pool.SDealStatus = :SDealStatus");
			paramName.add("SDealStatus");
			paramValue.add(PoolComm.DS_00);	
		}else if(type!=null && "1".equals(type)){//去除
			List status = new ArrayList();
			status.add(PoolComm.DS_00);//非初始化的
			status.add(PoolComm.DS_04);//非已经出池的
			status.add(PoolComm.TS05);//非托收记账完毕的
			hql.append(" and  pool.SDealStatus not in(:SDealStatus)");
			paramName.add("SDealStatus"); 
			paramValue.add(status);
		}
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues );
		if(res!=null &&res.size()>0){
			return res;
		}
		return null;
	}
	
	public PedCheckBatch queryPedCheckBatchParams(PoolQueryBean pq)
			throws Exception {
		StringBuffer hql = new StringBuffer("from PedCheckBatch as bat where 1=1");
		List keys = new ArrayList();
		List values = new ArrayList();
		// 客户核心号
		if (StringUtil.isNotBlank(pq.getCustomernumber())) {
			hql.append(" and bat.custNo=:custNo");
			keys.add("custNo");
			values.add(pq.getCustomernumber());
		}
		//票据池编号
		if (StringUtil.isNotBlank(pq.getProtocolNo())) {
			hql.append(" and bat.poolAgreement=:poolAgreement");
			keys.add("poolAgreement");
			values.add(pq.getProtocolNo());
		}
		//账务时间
		if (null!=pq.getPstartDate()&&!pq.getPstartDate().equals("")) {
			hql.append(" and dto.accountDate=:accountDate");
			keys.add("accountDate");
			values.add(pq.getProtocolNo());
		}
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List<PedCheckBatch> rsut = this.find(hql.toString(), keis, values.toArray());
		if (rsut != null && rsut.size() > 0) {
			return rsut.get(0);
		}
			return null;
	}


	public List<PedProtocolList> queryPedProtocolListParams(PoolQueryBean pq,Page page)
			throws Exception {
		StringBuffer hql = new StringBuffer("from PedProtocolList as dto where 1=1");
		List keys = new ArrayList();
		List values = new ArrayList();
		//票据池编号
		if (StringUtil.isNotBlank(pq.getProtocolNo())) {
			hql.append(" and dto.bpsNo=:bpsNo");
			keys.add("bpsNo");
			values.add(pq.getProtocolNo());
		}
		//核心客户号(成员)
		if (StringUtil.isNotBlank(pq.getCustomernumber())) {
			hql.append(" and dto.custNo=:custNo");
			keys.add("custNo");
			values.add(pq.getCustomernumber());
		}
			String[] keis = (String[]) keys.toArray(new String[keys.size()]);
			List<PedProtocolList> rsut = this.find(hql.toString(), keis, values.toArray(), page);
			if (rsut != null && rsut.size() > 0) {
				return rsut;
			}
				return null;
		}
	public String txPedProtocolDtoPJC033(PedProtocolDto dto) throws Exception {
		String ebankFlag = dto.getEbankFlag();// 签约标识
		ProtocolQueryBean  queryBean= new ProtocolQueryBean();
		String agreementNo = "";// 票据池编号
		String PoolInfoId =null;//主键ID
		Date sysDate =DateUtils.formatDate(new Date(),DateUtils.ORA_DATE_FORMAT);//机器时间
		if ("01".equals(ebankFlag)) {// 签约
			queryBean.setCustnumber(dto.getCustnumber());//客户号
			queryBean.setIsGroup(PoolComm.NO);//单户
			//a.查询当前客户是否存在单户签约数据
			PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			PedProtocolDto protocolDto = new PedProtocolDto();//copy传值
			//1.基础签约+融资签约
			if(dto.getpSignType().equals(PoolComm.QYLX_02)){
				//b.如根据当前客户号查询不到单户签约数据则new PedProtocolDto一个对象
				if(null == pedProtocolDto){
					agreementNo = poolBatchNoUtils.txGetBatchNoBySession("DR", 6);
					PedProtocolDto pro = new PedProtocolDto();
					pro.setPoolInfoId(null);//主键ID
					pro.setPoolAgreement(agreementNo);//票据池编号
					pro.setEffstartdate(sysDate);// 票据池协议生效日期
					pro.setEffenddate(DateUtils.formatDate(DateUtils.getNextNMonth(sysDate,12),DateUtils.ORA_DATE_FORMAT));// 票据池协议到期日

					//基础签约
					pro.setCustOrgcode(dto.getCustOrgcode());//组织机构代码
					pro.setPlUSCC(dto.getPlUSCC());//社会信用代码
					pro.setCustnumber(dto.getCustnumber());//客户号
					pro.setCustname(dto.getCustname());//客户名称
					pro.setEbankFlag(ebankFlag);
					pro.setElecDraftAccount(dto.getElecDraftAccount());//电票账号
					pro.setElecDraftAccountName(dto.getElecDraftAccountName());//电票账号名称
					pro.setPoolName("票据池（" + dto.getCustname() + "）");// 票据池名称
					pro.setBusiType(dto.getBusiType());// 业务类型 02虚拟票据池
					pro.setSigningFunction(dto.getSigningFunction()); //签约功能  01：票据账务管家
					pro.setPoolMode(dto.getPoolMode());// 池模式 01总量控制
					pro.setIsGroup(dto.getIsGroup());// 是否集团
					pro.setSignDeptNo(dto.getSignDeptNo());//签约机构号
					pro.setSignDeptName(dto.getSignDeptName());//签约机构名称
					pro.setOfficeNet(dto.getOfficeNet());//受理网点
					pro.setOfficeNetName(dto.getOfficeNetName());//受理网点名称
					pro.setAccountManagerId(dto.getAccountManagerId());//客户经理id
					pro.setAccountManager(dto.getAccountManager());//客户经理
					
					User user = userService.getUserByLoginName(dto.getAccountManagerId());//客户经理ID
					if(null != user){    						
						Department dept =  (Department)userService.load(user.getDeptId(),Department.class);
						String deptNo = dept.getInnerBankCode();
						String deptName = dept.getName();
						pro.setOfficeNet(deptNo);
						pro.setOfficeNetName(deptName);
						
					}
					pro.setXyflag(PoolComm.YES);
					pro.setvStatus(PoolComm.VS_01);
					pro.setFrozenstate(PoolComm.FROZEN_STATUS_00);
					pro.setApproveFlag(null);// 初始化

					//融资签约
					pro.setZyflag(dto.getZyflag());//是否自动入池
					pro.setSignDeptNo(dto.getSignDeptNo());//签约机构号
					pro.setSignDeptName(dto.getSignDeptName());//签约机构名称
					pro.setMarginAccount(dto.getMarginAccount());//保证金账号
					pro.setMarginAccountName(dto.getMarginAccountName());//保证金账号名称
					pro.setPoolAccount(dto.getPoolAccount());//结算账号
					pro.setPoolAccountName(dto.getPoolAccountName());//结算账号名称
					pro.setOfficeNet(dto.getOfficeNet());//受理网点
					pro.setOfficeNetName(dto.getOfficeNetName());//受理网点名称
					pro.setAccountManagerId(dto.getAccountManagerId());//客户经理id
					pro.setAccountManager(dto.getAccountManager());//客户经理
					pro.setOperateTime(new Date());
					pro.setFrozenstate(PoolComm.FROZEN_STATUS_00);
					pro.setOpenFlag(PoolComm.OPEN_01);
					pro.setApproveFlag(PoolComm.APPROVE_03);// 签约审批通过
					pro.setFeeType(dto.getFeeType());//收费模式
					pro.setFeeIssueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//年费生效日
					pro.setFeeDueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//年费到期日

					pro.setContractTransDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT)); //担保合同签订日期
					pro.setContractEffectiveDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//担保合同生效日期
					pro.setContractDueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//担保合同到期日
					pro.setCreditamount(dto.getCreditamount());
					pro.setCreditUsedAmount(new BigDecimal(0.00));
					pro.setIsAcctCheck(dto.getIsAcctCheck());//划转审批字段
					this.txStore(pro);
					BeanUtils.copyProperties(pro,protocolDto);//copy传值处理
					
					//票据池客户信息数据落库
					CustomerRegister customer=new CustomerRegister();
					customer.setCustNo(dto.getCustnumber());
					customer.setCustName(dto.getCustname());
					customer.setFirstDateSource("PJC033");
					customerService.txSaveCustomerRegister(customer);
				}
			    /*c.如根据当前客户号查询到单户签约数据则判断是否基础签约,
				如未基础签约则判断该客户数据为历史解约数据(文档规定如客户存在单户签约数据则沿用原票据池编号),
				在此需重置PedProtocolDto沉余数据.
				*/
				else if(!PoolComm.VS_01.equals(pedProtocolDto.getvStatus())){
					agreementNo = pedProtocolDto.getPoolAgreement();
					PoolInfoId=pedProtocolDto.getPoolInfoId();
					reflectClassValueToNull(pedProtocolDto);//字段属性清空
					pedProtocolDto.setPoolInfoId(PoolInfoId);//主键ID
					pedProtocolDto.setEffstartdate(sysDate);// 票据池协议生效日期
					pedProtocolDto.setEffenddate(DateUtils.formatDate(DateUtils.getNextNMonth(sysDate,12),DateUtils.ORA_DATE_FORMAT));// 票据池协议到期日
					pedProtocolDto.setPoolAgreement(agreementNo);//票据池编号

					//基础签约
					pedProtocolDto.setCustOrgcode(dto.getCustOrgcode());//组织机构代码
					pedProtocolDto.setPlUSCC(dto.getPlUSCC());//社会信用代码
					pedProtocolDto.setCustnumber(dto.getCustnumber());//客户号
					pedProtocolDto.setCustname(dto.getCustname());//客户名称
					pedProtocolDto.setEbankFlag(ebankFlag);
					pedProtocolDto.setElecDraftAccount(dto.getElecDraftAccount());//电票账号
					pedProtocolDto.setElecDraftAccountName(dto.getElecDraftAccountName());//电票账号名称
					pedProtocolDto.setPoolName("票据池（" + dto.getCustname() + "）");// 票据池名称
					pedProtocolDto.setBusiType(dto.getBusiType());// 业务类型 02虚拟票据池
					pedProtocolDto.setSigningFunction(dto.getSigningFunction()); //签约功能  01：票据账务管家
					pedProtocolDto.setPoolMode(dto.getPoolMode());// 池模式 01总量控制
					pedProtocolDto.setIsGroup(dto.getIsGroup());// 是否集团
					pedProtocolDto.setSignDeptNo(dto.getSignDeptNo());//签约机构号
					pedProtocolDto.setSignDeptName(dto.getSignDeptName());//签约机构名称
					pedProtocolDto.setOfficeNet(dto.getOfficeNet());//受理网点
					pedProtocolDto.setOfficeNetName(dto.getOfficeNetName());//受理网点名称
					pedProtocolDto.setAccountManagerId(dto.getAccountManagerId());//客户经理id
					pedProtocolDto.setAccountManager(dto.getAccountManager());//客户经理
					pedProtocolDto.setXyflag(PoolComm.YES);
					pedProtocolDto.setvStatus(PoolComm.VS_01);
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
					pedProtocolDto.setApproveFlag(null);// 初始化

					//融资签约
					pedProtocolDto.setZyflag(dto.getZyflag());//是否自动入池
					pedProtocolDto.setSignDeptNo(dto.getSignDeptNo());//签约机构号
					pedProtocolDto.setSignDeptName(dto.getSignDeptName());//签约机构名称
					pedProtocolDto.setMarginAccount(dto.getMarginAccount());//保证金账号
					pedProtocolDto.setMarginAccountName(dto.getMarginAccountName());//保证金账号名称
					pedProtocolDto.setPoolAccount(dto.getPoolAccount());//结算账号
					pedProtocolDto.setPoolAccountName(dto.getPoolAccountName());//结算账号名称
					pedProtocolDto.setOfficeNet(dto.getOfficeNet());//受理网点
					pedProtocolDto.setOfficeNetName(dto.getOfficeNetName());//受理网点名称
					pedProtocolDto.setAccountManagerId(dto.getAccountManagerId());//客户经理id
					pedProtocolDto.setAccountManager(dto.getAccountManager());//客户经理
					pedProtocolDto.setOperateTime(new Date());
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
					pedProtocolDto.setOpenFlag(PoolComm.OPEN_01);
					pedProtocolDto.setApproveFlag(PoolComm.APPROVE_03);// 签约审批通过
					pedProtocolDto.setFeeType(dto.getFeeType());//收费模式
					pedProtocolDto.setFeeIssueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//年费生效日
					pedProtocolDto.setFeeDueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//年费到期日
					pedProtocolDto.setCreditamount(new BigDecimal(0.00));
					pedProtocolDto.setCreditUsedAmount(new BigDecimal(0.00));
					pedProtocolDto.setContractTransDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT)); //担保合同签订日期
					pedProtocolDto.setContractEffectiveDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//担保合同生效日期
					pedProtocolDto.setContractDueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//担保合同到期日

					pedProtocolDto.setIsAcctCheck(dto.getIsAcctCheck());//网银划转审批
					this.txStore(pedProtocolDto);
					BeanUtils.copyProperties(pedProtocolDto,protocolDto);
					//票据池客户信息数据落库
					CustomerRegister customer=new CustomerRegister();
					customer.setCustNo(dto.getCustnumber());
					customer.setCustName(dto.getCustname());
					customer.setFirstDateSource("PJC033");
					customerService.txSaveCustomerRegister(customer);
				}
				//d.已在网银客户端或者银行做过基础签约
				else{
					//基础签约
					pedProtocolDto.setCustOrgcode(dto.getCustOrgcode());//组织机构代码
					pedProtocolDto.setPlUSCC(dto.getPlUSCC());//社会信用代码
					pedProtocolDto.setCustnumber(dto.getCustnumber());//客户号
					pedProtocolDto.setCustname(dto.getCustname());//客户名称
					pedProtocolDto.setEbankFlag(ebankFlag);
					pedProtocolDto.setElecDraftAccount(dto.getElecDraftAccount());//电票账号
					pedProtocolDto.setElecDraftAccountName(dto.getElecDraftAccountName());//电票账号名称
					pedProtocolDto.setPoolName("票据池（" + dto.getCustname() + "）");// 票据池名称
					pedProtocolDto.setBusiType(dto.getBusiType());// 业务类型 02虚拟票据池
					pedProtocolDto.setSigningFunction(dto.getSigningFunction()); //签约功能  01：票据账务管家
					pedProtocolDto.setPoolMode(dto.getPoolMode());// 池模式 01总量控制
					pedProtocolDto.setIsGroup(dto.getIsGroup());// 是否集团
					pedProtocolDto.setSignDeptNo(dto.getSignDeptNo());//签约机构号
					pedProtocolDto.setSignDeptName(dto.getSignDeptName());//签约机构名称
					pedProtocolDto.setOfficeNet(dto.getOfficeNet());//受理网点
					pedProtocolDto.setOfficeNetName(dto.getOfficeNetName());//受理网点名称
					pedProtocolDto.setAccountManagerId(dto.getAccountManagerId());//客户经理id
					pedProtocolDto.setAccountManager(dto.getAccountManager());//客户经理
					pedProtocolDto.setXyflag(PoolComm.YES);
					pedProtocolDto.setvStatus(PoolComm.VS_01);
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
					pedProtocolDto.setApproveFlag(null);// 初始化

					//融资签约
					pedProtocolDto.setZyflag(dto.getZyflag());//是否自动入池
					pedProtocolDto.setSignDeptNo(dto.getSignDeptNo());//签约机构号
					pedProtocolDto.setSignDeptName(dto.getSignDeptName());//签约机构名称
					pedProtocolDto.setMarginAccount(dto.getMarginAccount());//保证金账号
					pedProtocolDto.setMarginAccountName(dto.getMarginAccountName());//保证金账号名称
					pedProtocolDto.setPoolAccount(dto.getPoolAccount());//结算账号
					pedProtocolDto.setPoolAccountName(dto.getPoolAccountName());//结算账号名称
					pedProtocolDto.setOfficeNet(dto.getOfficeNet());//受理网点
					pedProtocolDto.setOfficeNetName(dto.getOfficeNetName());//受理网点名称
					pedProtocolDto.setAccountManagerId(dto.getAccountManagerId());//客户经理id
					pedProtocolDto.setAccountManager(dto.getAccountManager());//客户经理
					pedProtocolDto.setOperateTime(new Date());
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
					pedProtocolDto.setOpenFlag(PoolComm.OPEN_01);
					pedProtocolDto.setApproveFlag(PoolComm.APPROVE_03);// 签约审批通过
					pedProtocolDto.setFeeType(dto.getFeeType());//收费模式
					pedProtocolDto.setFeeIssueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//年费生效日
					pedProtocolDto.setFeeDueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//年费到期日
					pedProtocolDto.setCreditamount(new BigDecimal(0.00));
					pedProtocolDto.setCreditUsedAmount(new BigDecimal(0.00));
					
					pedProtocolDto.setContractTransDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT)); //担保合同签订日期
					pedProtocolDto.setContractEffectiveDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//担保合同生效日期
					pedProtocolDto.setContractDueDt(DateUtils.formatDate(DateUtils.modDay(sysDate,-1),DateUtils.ORA_DATE_FORMAT));//担保合同到期日
					pedProtocolDto.setIsAcctCheck(dto.getIsAcctCheck());//网银划转审批
					this.txStore(pedProtocolDto);
					BeanUtils.copyProperties(pedProtocolDto,protocolDto);
					//票据池客户信息数据落库
					CustomerRegister customer=new CustomerRegister();
					customer.setCustNo(dto.getCustnumber());
					customer.setCustName(dto.getCustname());
					customer.setFirstDateSource("PJC033");
					customerService.txSaveCustomerRegister(customer);
				}
					AssetType assetTypeDto = pedAssetTypeService.queryPedAssetTypeByProtocol(protocolDto,PoolComm.ED_BZJ_HQ);
					if(assetTypeDto!=null){
						BailDetail detail = poolBailEduService.queryBailDetail(assetTypeDto.getId());
						detail.setAssetNb(protocolDto.getMarginAccount());//保证金账号
						poolBailEduService.txStore(detail);
					}else{
						logger.error("签约信息维护，获取资产池为空！");	
					}
					AssetPool ap =pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(protocolDto.getPoolAgreement(),null, null);
					if(ap==null){
						//创建asstPool信息
						pedProtocolService.createAssetPoolInfo(protocolDto);
						//创建assetType信息
						for(int i=0;i<4;i++){
							String assetType = null;//产生额度类型
							if(i==0){
								assetType = PoolComm.ED_PJC;//低风险票据额度
							}else if(i==1){
								assetType = PoolComm.ED_PJC_01;//高风险票据额度
							}else if(i==2){
								assetType = PoolComm.ED_BZJ_HQ;//活期保证金
							}else if(i==3){
								assetType = PoolComm.ED_BZJ_DQ;//定期保证金
							}
							//修改方法 
							pedProtocolService.txCreateAssetTypeInfo(protocolDto,assetType);
						}
					}
				    agreementNo = protocolDto.getPoolAgreement();
			  }
			
			//2.基础签约
			if(dto.getpSignType().equals(PoolComm.QYLX_01)){
				PedProtocolDto pro = new PedProtocolDto();
				if (null!= pedProtocolDto) {// 非初次签约沿用原有票据池编号
					agreementNo = pedProtocolDto.getPoolAgreement();
					PoolInfoId=pedProtocolDto.getPoolInfoId();
                    reflectClassValueToNull(pedProtocolDto);//字段属性清空
                    pro=pedProtocolDto;
				} else {// 初次签约自动生成新的票据池编号
					agreementNo = poolBatchNoUtils.txGetBatchNoBySession("DR", 6);
				}
				pro.setPoolInfoId(PoolInfoId);
				pro.setPoolAgreement(agreementNo);
				pro.setCustOrgcode(dto.getCustOrgcode());//组织机构代码
				pro.setPlUSCC(dto.getPlUSCC());//社会信用代码
				pro.setCustnumber(dto.getCustnumber());//客户号
				pro.setCustname(dto.getCustname());//客户名称
				pro.setEbankFlag(ebankFlag);
				pro.setElecDraftAccount(dto.getElecDraftAccount());//电票账号
				pro.setElecDraftAccountName(dto.getElecDraftAccountName());//电票账号名称
				pro.setPoolName("票据池（" + dto.getCustname() + "）");// 票据池名称
				pro.setBusiType(dto.getBusiType());// 业务类型 02虚拟票据池
				pro.setSigningFunction(dto.getSigningFunction()); //签约功能  01：票据账务管家
				pro.setPoolMode(dto.getPoolMode());// 池模式 01总量控制
				pro.setIsGroup(dto.getIsGroup());// 是否集团
				pro.setEffstartdate(sysDate);// 票据池协议生效日期
				pro.setEffenddate(DateUtils.formatDate(DateUtils.getNextNMonth(sysDate,12),DateUtils.ORA_DATE_FORMAT));// 票据池协议到期日
				pro.setSignDeptNo(dto.getSignDeptNo());//签约机构号
				pro.setSignDeptName(dto.getSignDeptName());//签约机构名称
				pro.setOfficeNet(protocolDto.getSignDeptNo());//受理网点号--默认赋值签约机构
				pro.setOfficeNetName(protocolDto.getSignDeptName());//受理网点名称
				pro.setXyflag(PoolComm.YES);
				pro.setvStatus(PoolComm.VS_01);
				pro.setOperateTime(new Date());
				pro.setOpenFlag(PoolComm.OPEN_00);// 未开通
				pro.setFrozenstate(PoolComm.FROZEN_STATUS_00);
				pro.setApproveFlag(null);// 初始化
				pro.setIsAcctCheck(dto.getIsAcctCheck());//网银划转审批
				this.dao.merge(pro);
			}
		}
		if ("03".equals(ebankFlag)) {// 修改----只有电票签约账号可以改，集团成员信息修改
			queryBean.setCustnumber(dto.getCustnumber());
			queryBean.setPoolAgreement(dto.getPoolAgreement());
			queryBean.setIsGroup(PoolComm.NO);
			PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			if(null != dto.getpBreakType() && dto.getpBreakType().equals(PoolComm.JYLX_01)){//融资功能解约
				pedProtocolDto.setOpenFlag(PoolComm.OPEN_02);//已解约
				pedProtocolDto.setApproveFlag(PoolComm.APPROVE_06);//解约审核通过
				pedProtocolDto.setElecDraftAccount(dto.getElecDraftAccount());//电票签约账号
				pedProtocolDto.setElecDraftAccountName(dto.getElecDraftAccountName());//电票签约账号名

			}
			pedProtocolDto.setCustname(dto.getCustname());//客户名称
			pedProtocolDto.setPoolName("票据池（" + pedProtocolDto.getCustname() + "）");// 票据池名称
			pedProtocolDto.setPlUSCC(dto.getPlUSCC());//社会信用代码
			pedProtocolDto.setCustOrgcode(dto.getCustOrgcode());//组织机构代码
			pedProtocolDto.setElecDraftAccount(dto.getElecDraftAccount());//电票签约账号
			pedProtocolDto.setElecDraftAccountName(dto.getElecDraftAccountName());//电票签约账号名
			pedProtocolDto.setOperateTime(new Date());
			pedProtocolDto.setZyflag(dto.getZyflag());//自动入池
			pedProtocolDto.setSignDeptNo(dto.getSignDeptNo());//签约机构号
			pedProtocolDto.setSignDeptName(dto.getSignDeptName());//签约机构名称
			pedProtocolDto.setMarginAccount(dto.getMarginAccount());//保证金账号
			pedProtocolDto.setMarginAccountName(dto.getMarginAccountName());//保证金账号名
			pedProtocolDto.setPoolAccount(dto.getPoolAccount());//结算账户
			pedProtocolDto.setPoolAccountName(dto.getPoolAccountName());//结算账号名
			pedProtocolDto.setOfficeNet(dto.getOfficeNet());//受理网点
			pedProtocolDto.setOfficeNetName(dto.getOfficeNetName());//受理网点名
			pedProtocolDto.setAccountManagerId(dto.getAccountManagerId());//客户经理id
			pedProtocolDto.setAccountManager(dto.getAccountManager());//客户经理
			pedProtocolDto.setFeeType(dto.getFeeType());//收费模式
			this.txStore(pedProtocolDto);
			agreementNo = pedProtocolDto.getPoolAgreement();
		}
		if ("02".equals(ebankFlag)) {// 解约
			queryBean.setCustnumber(dto.getCustnumber());
			queryBean.setPoolAgreement(dto.getPoolAgreement());
			queryBean.setIsGroup(PoolComm.NO);
			PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			if(dto.getpBreakType().equals("JYLX_02")){//全部解约
				//基本解约
				pedProtocolDto.setvStatus(PoolComm.VS_02);
				//融资功能解约
				pedProtocolDto.setOpenFlag(PoolComm.OPEN_02);//已解约
				pedProtocolDto.setApproveFlag(PoolComm.APPROVE_06);//解约审核通过
			}else{
				//基本解约
				pedProtocolDto.setvStatus(PoolComm.VS_02);
				pedProtocolDto.setOpenFlag(PoolComm.OPEN_02);
			}
			pedProtocolDto.setOperateTime(new Date());
			this.txStore(pedProtocolDto);
			agreementNo = pedProtocolDto.getPoolAgreement();
		}
		return agreementNo;

	}
	
	
	public List<PedProtocolList> queryPoolAgreement(String poolAgreement) throws Exception {
		String hql = "from PedProtocolList ppd where ppd.bpsNo ='"
			+ poolAgreement+"'";
		List find = this.find(hql);
		
	return find;
	}
	protected void setCurrentUser() {
		// SpringMVC需要重新设置到页面
		session.setAttribute(CurValues.USER, this.getCurrentUser());
	}

	protected User getCurrentUser() {
		return (User) session.getAttribute(CurValues.USER);
	}

	@Override
	public void txAccountManagement(String bpsNo, String custNo,String eleAccNos)
			throws Exception {
		//删除数据
		logger.info("删除开始");
		if(eleAccNos!=null){			
			draftPoolQueryService.txDeleteAccountManagement(PoolComm.NO,custNo,eleAccNos);
		}
        draftPoolQueryService.txDeleteDraftAccountManagement(bpsNo, custNo);
        this.dao.flush();
        logger.info("删除结束");
    	List pedList = pedProtocolService.queryProtocolInfo(null,PoolComm.VS_01, bpsNo, null, null, null);
    	
    	/*
		 * 全部签约的电票和电票签约账户的关系
		 */
		String eAccs = null; 
		Map accMap = new HashMap();//用于存放电票签约账号和客户的对应关系
		
		if(pedList!=null && pedList.size()>0){ 
			for(int i=0;i<pedList.size();i++){
				PedProtocolDto pedDto= (PedProtocolDto)pedList.get(i);
				if(PoolComm.NO.equals(pedDto.getIsGroup())){//非集团
					String accNo = pedDto.getElecDraftAccount();
					if(accNo!=null){
						eAccs = accNo;
						String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
						for (int j = 0; j < arr.length; j++) {
							ProtocolQueryBean queryBean = new ProtocolQueryBean();
							queryBean.setCustnumber(pedDto.getCustnumber());
							queryBean.setCustname(pedDto.getCustname());
							
							accMap.put(arr[j], queryBean);
						}
					}
				}else{
					ProListQueryBean bean = new ProListQueryBean();
					bean.setBpsNo(pedDto.getPoolAgreement());
					bean.setCustNo(custNo);
					PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(bean);
					String accNo = mem.getElecDraftAccount();
					if(accNo!=null){
						eAccs = accNo;						
						String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
						for (int k = 0; k < arr.length; k++) {
							ProtocolQueryBean queryBean = new ProtocolQueryBean();
							queryBean.setCustnumber(mem.getCustNo());
							queryBean.setCustname(mem.getCustName());
							accMap.put(arr[k], queryBean);
						}
					}
				}
			}
		}
    	
    	if(pedList!=null && pedList.size()>0){
    		logger.info("票据池编号【"+bpsNo+"】"+"客户号【"+custNo+"】"+"************JD01客户持有票据落库开始************");
    		this.doProcessJD01(bpsNo, custNo);	//JD_01：客户BBSP系统持有票据落库
    		this.dao.flush();
    		
    		logger.info("票据池编号【"+bpsNo+"】"+"客户号【"+custNo+"】"+"************JD02客户已质押票据落库开始************");
    		this.doProcessJD02(bpsNo, custNo);	//JD_02：客户已质押票据落库
    		this.dao.flush();
    		
    		logger.info("票据池编号【"+bpsNo+"】"+"客户号【"+custNo+"】"+"************JD03虚拟票据池录入应收票据落库开始************");
    		this.doProcessJD03(bpsNo, custNo);	//JD_03：客户虚拟票据池录入应收票据落库
    		this.dao.flush();
    		
    		logger.info("票据池编号【"+bpsNo+"】"+"客户号【"+custNo+"】"+"************JD04客户BBSP系统提示承兑已签收票据落库开始************");
    		this.doProcessJD04(eAccs,accMap);	//JD_04：客户BBSP系统提示承兑已签收票据落库
    		this.dao.flush();
    		
    		logger.info("票据池编号【"+bpsNo+"】"+"客户号【"+custNo+"】"+"************JD06虚拟票据池录入应付票据开始************");
    		this.doProcessJD06(bpsNo, custNo);	//JD_06：客户虚拟票据池录入应付票据落库
    		this.dao.flush();
    		logger.info("票据池编号【"+bpsNo+"】"+"客户号【"+custNo+"】"+"************JD06结束************");
    	}
    	draftPoolQueryService.txDelectRepeatDraftAccountManagement();
	}
	
	/**
	 * 账务管家：JD_01—客户BBSP系统持有票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	private void doProcessJD01(String bpsNo,String custNo){
		try{
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setSStatusFlag(PoolComm.DS_00);//持有票据
			queryBean.setCustomernumber(custNo);
//			queryBean.setProtocolNo(bpsNo);
			List<PoolBillInfo> infos =  draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
			//1、客户持有票据
			if(infos!=null && infos.size()>0){
				logger.info("持有票据查询结束,合计票据有["+infos.size()+"]条");
				for (PoolBillInfo info :infos) {
					DraftAccountManagement management ;
					management = new DraftAccountManagement();
					management.setCustNo(info.getCustNo());//客户号(用的是承兑人账号)
					management.setCustName(info.getCustName());//客户名称
					management.setAssetType(PoolComm.QY_01);//资产类型
					management.setTransferPhase("JD_01");//交易阶段
					management.setDraftNb(info.getSBillNo());//票号
					
					/********************融合改造新增 start******************************/
					management.setBeginRangeNo(info.getBeginRangeNo());//子票区间起
					management.setEndRangeNo(info.getEndRangeNo());//子票区间止
					management.setDraftSource(info.getDraftSource());//
					management.setSplitFlag(info.getSplitFlag());//
					management.setPlDrwrAcctName(info.getSIssuerAcctName());//出票人账号名称
					management.setPlPyeeAcctName(info.getSPayeeAcctName());// 收款人账号名称
					management.setPlAccptrAcctName(info.getSAcceptorAcctName());//承兑人账号名称
					
					
					/********************融合改造新增 end******************************/
					
					
					management.setDraftMedia("2");//票据介质
					management.setDraftType(info.getSBillType());//票据类型
					management.setIsseAmt(info.getFBillAmount());//票面金额
					
					management.setIsseDt(DateUtils.formatDate(info.getDIssueDt(), DateUtils.ORA_DATE_FORMAT));//出票日
					management.setDueDt(DateUtils.formatDate(info.getDDueDt(), DateUtils.ORA_DATE_FORMAT));//到期日
					
					management.setIsseDt(info.getDIssueDt());//出票日
					management.setDueDt(info.getDDueDt());//到期日
					management.setEdBanEndrsmtMk(info.getSBanEndrsmtFlag());//大票表 0-转让  1-不转让
					management.setDrwrNm(info.getSIssuerName());//出票人全称
					management.setDrwrAcctId(info.getSIssuerAccount());//出票人账号
					management.setDrwrAcctSvcr(info.getSIssuerBankCode());//出票人开户行行号
					management.setDrwrAcctSvcrNm(info.getSIssuerBankName());//出票人开户行名称
					management.setPyeeNm(info.getSPayeeName());//收款人全称
					management.setPyeeAcctId(info.getSPayeeAccount());//收款人账号
					management.setPyeeAcctSvcr(info.getSPayeeBankCode());//收款人开户行行号
					management.setPyeeAcctSvcrNm(info.getSPayeeBankName());//收款人开户行名称
					management.setAccptrNm(info.getSAcceptor());//承兑人全称
					management.setAccptrId(info.getSAcceptorAccount());//承兑人账号
					management.setAccptrSvcr(info.getSAcceptorBankCode());//承兑人开户行全称
					management.setAccptrSvcrNm(info.getSAcceptorBankName());//承兑人开户行行名
					management.setTrusteeshipFalg("0");//是否托管
					management.setDraftOwnerSts(PoolComm.QY_01);//票据持有类型
					
					if(PoolComm.LOW_RISK.equals(info.getRickLevel())||PoolComm.HIGH_RISK.equals(info.getRickLevel())){
						management.setIsEdu("1");//是否产生额度:是
					}else{
						management.setIsEdu("0");//是否产生额度: 否
					}
					management.setRiskLevel(info.getRickLevel());//风险等级
					management.setRecePayType(PoolComm.QY_01);//票据权益（QY_01:持有票据    QY_02:应付票据）
					management.setDataSource("SRC_01");//数据来源:BBSP
					management.setBillSaveAddr("02");//票据保管地(01:本行   02：自持  03：他行)
					management.setOtherBankSaveAddr("");//他行保管地
					management.setElecDraftAccount(info.getAccNo());//电票签约账号
					management.setStatusFlag(PoolComm.NO);
					management.setBillId(info.getDiscBillId());
					
					
					pedProtocolService.txStore(management);
					}
			
				}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 账务管家：JD_02—客户已质押票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	private void doProcessJD02(String  bpsNo,String custNo){
		try{
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setSStatusFlag(PoolComm.DS_02);//已入池
			queryBean.setProtocolNo(bpsNo);//票据池编号
			queryBean.setCustomernumber(custNo);//客户号
			List<PoolBillInfo> infos =  draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
			if(null!=infos&&infos.size()>0){
			logger.info("持有票据查询结束,合计票据有["+infos.size()+"]条");
			for (PoolBillInfo info :infos) {
				DraftAccountManagement management ;
				management = new DraftAccountManagement();
				management.setCustNo(info.getCustNo());//客户号(用的是承兑人账号)
				management.setCustName(info.getCustName());//客户名称
				management.setAssetType(PoolComm.QY_01);//资产类型
				management.setTransferPhase("JD_02");//交易阶段
				management.setDraftNb(info.getSBillNo());//票号

				/********************融合改造新增 start******************************/
				management.setBeginRangeNo(info.getBeginRangeNo());//子票区间起
				management.setEndRangeNo(info.getEndRangeNo());//子票区间止
				
				management.setDraftSource(info.getDraftSource());//
				management.setSplitFlag(info.getSplitFlag());//
				management.setPlDrwrAcctName(info.getSIssuerAcctName());//出票人账号名称
				management.setPlPyeeAcctName(info.getSPayeeAcctName());// 收款人账号名称
				management.setPlAccptrAcctName(info.getSAcceptorAcctName());//承兑人账号名称
				
				/********************融合改造新增 end******************************/
				
				
				management.setDraftMedia(info.getSBillMedia());//票据介质
				management.setDraftType(info.getSBillType());//票据类型
				management.setIsseAmt(info.getFBillAmount());//票面金额
				management.setIsseDt(info.getDIssueDt());//出票日
				management.setDueDt(info.getDDueDt());//到期日
				management.setEdBanEndrsmtMk(info.getSBanEndrsmtFlag());
				management.setDrwrNm(info.getSIssuerName());//出票人全称
				management.setDrwrAcctId(info.getSIssuerAccount());//出票人账号
				management.setDrwrAcctSvcr(info.getSIssuerBankCode());//出票人开户行行号
				management.setDrwrAcctSvcrNm(info.getSIssuerBankName());//出票人开户行名称
				management.setPyeeNm(info.getSPayeeName());//收款人全称
				management.setPyeeAcctId(info.getSPayeeAccount());//收款人账号
				management.setPyeeAcctSvcr(info.getSPayeeBankCode());//收款人开户行行号
				management.setPyeeAcctSvcrNm(info.getSPayeeBankName());//收款人开户行名称
				management.setAccptrNm(info.getSAcceptor());//承兑人全称
				management.setAccptrId(info.getSAcceptorAccount());//承兑人账号
				management.setAccptrSvcr(info.getSAcceptorBankCode());//承兑人开户行号
				management.setAccptrSvcrNm(info.getSAcceptorBankName());//承兑人开户行行名
				management.setTrusteeshipFalg("0");//是否托管
				management.setDraftOwnerSts(PoolComm.QY_01);//票据持有类型
				management.setDataSource("SRC_03");//数据来源 SRC_03:票据池系统（融资票据池）
				management.setBillSaveAddr("01");//票据保管地(01:本行   02：自持  03：他行)
				management.setOtherBankSaveAddr("");//他行保管地
				management.setBpsNo(info.getPoolAgreement());//票据池编号
				PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDto(null,null,info.getPoolAgreement(),null,null,null);
				if(null!=pedProtocolDto){
					management.setBpsName(pedProtocolDto.getPoolName());//票据池名称
				}
				management.setStatusFlag(PoolComm.YES);
				if(PoolComm.LOW_RISK.equals(info.getRickLevel())||PoolComm.HIGH_RISK.equals(info.getRickLevel())){
					management.setIsEdu("1");//是否产生额度:是
				}else{
					management.setIsEdu("0");//是否产生额度: 否
				}
				management.setRiskLevel(info.getRickLevel());//风险等级
				
				management.setElecDraftAccount(info.getAccNo());//电票签约账号
				management.setBillId(info.getDiscBillId());
				
				poolVtrustService.txStore(management);
			}
				}else{
					logger.info("持有票据查询结束,合计票据有["+0+"]条");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 账务管家：JD_03—客户虚拟票据池录入应收票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	private void doProcessJD03(String bpsNo, String custNo){
		try{
			//3、虚拟票据池录入应收票据
			logger.info("虚拟票据池录入应收票据开始");
			List list = poolVtrustService.queryPoolVtrust(bpsNo,null, custNo, null, PoolComm.QY_01);
			if(list != null && list.size() >0 ){
				logger.info("虚拟票据池录入应收票据结束,可落库的数据有["+list.size()+"]条");
				for (int i = 0; i < list.size(); i++) {
					PoolVtrust pool = (PoolVtrust) list.get(i);
					DraftAccountManagement management = new DraftAccountManagement();
					management.setCustNo(pool.getVtEntpNo());//客户号
					management.setCustName(pool.getVtEntpName());//客户名称
					management.setAssetType(pool.getPayType());//资产类型
					management.setTransferPhase("JD_03");//交易阶段
					management.setDraftNb(pool.getVtNb());//票号

					/********************融合改造新增 start******************************/
					management.setBeginRangeNo(pool.getBeginRangeNo());//子票区间起
					management.setEndRangeNo(pool.getEndRangeNo());//子票区间止
					
					management.setDraftSource(pool.getDraftSource());//
					management.setSplitFlag(pool.getSplitFlag());//
					management.setPlDrwrAcctName(pool.getPlDrwrAcctName());//出票人账号名称
					management.setPlPyeeAcctName(pool.getPlPyeeAcctName());// 收款人账号名称
					management.setPlAccptrAcctName(pool.getPlAccptrAcctName());//承兑人账号名称
					/********************融合改造新增 end******************************/
				
					
					management.setDraftMedia(pool.getVtDraftMedia());//票据介质
					management.setDraftType(pool.getVtType());//票据类型
					management.setIsseAmt(pool.getVtisseAmt());//票面金额
					management.setIsseDt(pool.getVtisseDt());//出票日
					management.setDueDt(pool.getVtdueDt());//到期日
//					management.setEdBanEndrsmtMk(pool.getVtTranSfer());//能否转让标记
					System.out.println("票号："+pool.getVtNb()+"，能否转让标记："+pool.getVtTranSfer());
					if(pool.getVtTranSfer().equals("1")){
						management.setEdBanEndrsmtMk("0");//可转让
					}else {
						management.setEdBanEndrsmtMk("1");//不可转让
					}
					System.out.println("账务管家表票号："+pool.getVtNb()+"，能否转让标记："+management.getEdBanEndrsmtMk());
					management.setDrwrNm(pool.getVtdrwrName());//出票人全称
					management.setDrwrAcctId(pool.getVtdrwrAccount());//出票人账号
					management.setDrwrAcctSvcr(pool.getVtdrwrBankNumber());//出票人开户行行号
					management.setDrwrAcctSvcrNm(pool.getVtdrwrBankName());//出票人开户行名称
					
					management.setPyeeNm(pool.getVtpyeeName());//收款人全称
					management.setPyeeAcctId(pool.getVtaccptrAccount());//收款人账号
					management.setPyeeAcctSvcr(pool.getVtpyeeBankAccount());//收款人开户行行号
					management.setPyeeAcctSvcrNm(pool.getVtpyeeBankName());//收款人开户行名称
					management.setAccptrNm(pool.getVtaccptrName());//承兑人全称
					management.setAccptrId(pool.getVtpyeeAccount());//承兑人账号
					management.setAccptrSvcr(pool.getVtaccptrBankAccount());//承兑人开户行行号
					management.setAccptrSvcrNm(pool.getVtaccptrBankName());//承兑人开户行全称
					management.setTrusteeshipFalg(pool.getVtLogo());//是否托管(1:已托管  2：未托管)
					management.setDraftOwnerSts(pool.getPayType());//票据持有类型
					management.setIsEdu("");//是否产生额度:虚拟票据池均不产生额度
					management.setRiskLevel("");//风险等级：虚拟票据池无风险等级
//					management.setRecePayType(pool.getPayType());//票据权益（QY_01:持有票据    QY_02:应付票据）
					management.setDataSource("SRC_04");//数据来源:虚拟票据池录入
					management.setBillSaveAddr(pool.getBillPosition());//票据保管地(01:本行   02：自持  03：他行)
					management.setOtherBankSaveAddr(pool.getBillPositionAddr());//他行保管地
					management.setPyeeNm(pool.getVtpyeeName());
					management.setContractNo(pool.getContractNo());//交易合同号
					management.setAcceptanceAgreeNo(pool.getAcceptanceAgreeNo());//承兑协议编号
					management.setBpsNo(pool.getBpsNo());//票据池编号
					management.setBpsName(pool.getBpsName());//票据池名称
					management.setStatusFlag(PoolComm.YES);
					
					management.setElecDraftAccount(pool.getVtplApplyAcctId());//电票签约账号

					pedProtocolService.txStore(management);
				}
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 账务管家：JD_04—客户BBSP系统提示承兑已签收票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	private void doProcessJD04(String  AccNos,Map accMap){
		try{
			//4、:BBSP系统提示承兑已签收票据
			List draftList = new ArrayList();//承兑已签收票据信息列表
			if(AccNos!=null){
				ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
				poolTrans.setStatus("TE200202_02");//发起方标志 质押已签收
				poolTrans.setAcctNo(AccNos);
				
				ReturnMessageNew response = poolEcdsService.txApplyFullBill(poolTrans);
				draftList = response.getDetails();
				if (response.isTxSuccess()) {
					if(draftList!=null && draftList.size()>0){
						this.doJD04(draftList, accMap);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

		
	}
	/**
	 * PD04落库处理
	 * @Description TODO
	 * @author Ju Nana
	 * @param draftList
	 * @param queryBean
	 * @throws Exception
	 * @date 2019-6-18下午7:50:54
	 */
	private void doJD04(List draftList,Map accMap) throws Exception{

		for (int j = 0; j < draftList.size(); j++) {
			Map map = (Map) draftList.get(j);
			String accNo = null;//电票签约账号
			if( map.get("payeeAcctNo")!=null){
				accNo = (String) map.get("payeeAcctNo");//电票签约账号
			}
			ProtocolQueryBean queryBean = (ProtocolQueryBean)accMap.get(accNo);
			DraftAccountManagement management = new DraftAccountManagement();
			management.setCustNo(queryBean.getCustnumber());//客户号
			management.setCustName(queryBean.getCustname());//客户名称
			
			management.setElecDraftAccount(accNo);//电票签约账号
			management.setAssetType(PoolComm.QY_02);//资产类型
			management.setTransferPhase("JD_04");//交易阶段
			management.setDraftNb((String)map.get("billNo"));//票号
			
			/********************融合改造新增 start******************************/
			management.setBeginRangeNo((String)map.get("startBillNo"));//子票区间起
			management.setEndRangeNo((String)map.get("endBillNo"));//子票区间止
			
			management.setDraftSource((String)map.get("billSource"));//
//			management.setSplitFlag(pool.getSplitFlag());//
//			management.setPlDrwrAcctName(pool.getPlDrwrAcctName());//出票人账号名称
//			management.setPlPyeeAcctName(pool.getPlPyeeAcctName());// 收款人账号名称
//			management.setPlAccptrAcctName(pool.getPlAccptrAcctName());//承兑人账号名称
			
			/********************融合改造新增 end******************************/
			
			management.setDraftMedia("2");//票据介质
			if("1".equals(map.get("billType"))){//银承
				management.setDraftType(PoolComm.BILL_TYPE_BANK);//票据类型-银承
			}else{
				management.setDraftType(PoolComm.BILL_TYPE_BUSI);//票据类型-商承
			}
			management.setIsseAmt(new BigDecimal((String)map.get("billMoney")));//票面金额
			String issDate = (String)map.get("acptDt");//出票日
			String dueDate = (String)map.get("dueDt");//到期日
			management.setIsseDt(DateUtils.parse(issDate, "yyyyMMdd"));//出票日
			management.setDueDt(DateUtils.parse(dueDate, DateUtils.ORA_DATE_FORMAT));//到期日
			management.setEdBanEndrsmtMk((String)map.get("forbidFlag"));//能否转让标记   BBSP数据 0-转让  1-不转让
			if(map.get("remitter")!=null){
				management.setDrwrNm((String)map.get("remitter"));//出票人全称
			}
			if(map.get("remitterAcctNo")!=null){
				management.setDrwrAcctId((String)map.get("remitterAcctNo"));//出票人账号
			}
			if(map.get("remitterBankNo")!=null){
				management.setDrwrAcctSvcr((String)map.get("remitterBankNo"));//出票人开户行行号
			}
			if(map.get("remitterBankName")!=null){
				management.setDrwrAcctSvcrNm((String)map.get("remitterBankName"));//出票人开户行名称
			}
			if(map.get("payee")!=null){
				management.setPyeeNm((String)map.get("payee"));//收款人全称
			}
			if(map.get("payeeAcctNo")!=null){
				management.setPyeeAcctId((String)map.get("payeeAcctNo"));//收款人账号
			}
			if(map.get("payeeBankNo")!=null){
				management.setPyeeAcctSvcr((String)map.get("payeeBankNo"));//收款人开户行行号
			}
			if(map.get("payeeBankName")!=null){
				management.setPyeeAcctSvcrNm((String)map.get("payeeBankName"));//收款人开户行名称
			}
			if(map.get("acceptor")!=null){
				management.setAccptrNm((String)map.get("acceptor"));//承兑人全称
			}
			if(map.get("acceptorAcctNo")!=null){
				management.setAccptrId((String)map.get("acceptorAcctNo"));//承兑人账号
			}
			if(map.get("acceptorBankNo")!=null){
				management.setAccptrSvcr((String)map.get("acceptorBankNo"));//承兑人开户行号
			}
			if(map.get("acceptorBankName")!=null){
				management.setAccptrSvcrNm((String)map.get("acceptorBankName"));//承兑人开户行行名
			}
			management.setTrusteeshipFalg("0");//是都托管
			management.setDraftOwnerSts(PoolComm.QY_02);//票据持有类型
			
			management.setIsEdu("");//是否产生额度:提示承兑已签收状态不校验额度
			management.setRiskLevel("");//风险等级：提示承兑票据不校验风险
			management.setDataSource("SRC_01");//数据来源:BBSP系统
			management.setBillSaveAddr("02");//票据保管地(01:本行   02：自持  03：他行)
			management.setOtherBankSaveAddr("");//他行保管地
			management.setStatusFlag(PoolComm.NO);
			management.setElecDraftAccount((String)map.get("payeeAcctNo"));
			pedProtocolService.txStore(management);
			
		}
	
	}

	/**
	 * 账务管家：JD_06—客户虚拟票据池录入应付票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	private void doProcessJD06(String bpsNo,String custNo)throws Exception{
		//6、虚拟票据池录入应付票据
		try {
			logger.info("虚拟票据池录入应付票据开始");
			List list = poolVtrustService.queryPoolVtrust(bpsNo,null, custNo, null, "QY_02");
			if(list != null && list.size() > 0 ){
				logger.info("虚拟票据池录入应付票据结束,可落库数据有["+list.size()+"]条");
				for (int i = 0; i < list.size(); i++) {
					PoolVtrust pool = (PoolVtrust) list.get(i);
					DraftAccountManagement management = new DraftAccountManagement();
					management.setCustNo(pool.getVtEntpNo());//客户号
					management.setCustName(pool.getVtEntpName());//客户名称
					management.setAssetType(pool.getPayType());//资产类型
					management.setTransferPhase("JD_06");//交易阶段
					management.setDraftNb(pool.getVtNb());//票号
					
					/********************融合改造新增 start******************************/
					management.setBeginRangeNo(pool.getBeginRangeNo());//子票区间起
					management.setEndRangeNo(pool.getEndRangeNo());//子票区间止
					
					management.setDraftSource(pool.getDraftSource());//
					management.setSplitFlag(pool.getSplitFlag());//
					management.setPlDrwrAcctName(pool.getPlDrwrAcctName());//出票人账号名称
					management.setPlPyeeAcctName(pool.getPlPyeeAcctName());// 收款人账号名称
					management.setPlAccptrAcctName(pool.getPlAccptrAcctName());//承兑人账号名称
					
					/********************融合改造新增 end******************************/
					
					management.setDraftMedia(pool.getVtDraftMedia());//票据介质
					management.setDraftType(pool.getVtType());//票据类型
					management.setIsseAmt(pool.getVtisseAmt());//票面金额
					management.setIsseDt(pool.getVtisseDt());//出票日
					management.setDueDt(pool.getVtdueDt());//到期日
					System.out.println("票号："+pool.getVtNb()+"，能否转让标记："+pool.getVtTranSfer());
					if(pool.getVtTranSfer().equals("1")){
						management.setEdBanEndrsmtMk("0");//可转让
					}else {
						management.setEdBanEndrsmtMk("1");//不可转让
					}
					System.out.println("账务管家表票号："+pool.getVtNb()+"，能否转让标记："+management.getEdBanEndrsmtMk());
					management.setDrwrNm(pool.getVtdrwrName());//出票人全称
					management.setDrwrAcctId(pool.getVtdrwrAccount());//出票人账号
					management.setDrwrAcctSvcr(pool.getVtdrwrBankNumber());//出票人开户行行号
					management.setDrwrAcctSvcrNm(pool.getVtdrwrBankName());//出票人开户行名称
					management.setPyeeNm(pool.getVtpyeeName());//收款人全称
					management.setPyeeAcctId(pool.getVtpyeeAccount());//收款人账号
					management.setPyeeAcctSvcr(pool.getVtpyeeBankAccount());//收款人开户行行号
					management.setPyeeAcctSvcrNm(pool.getVtpyeeBankName());//收款人开户行名称
					management.setAccptrNm(pool.getVtaccptrName());//承兑人全称
					management.setAccptrId(pool.getVtaccptrAccount());//承兑人账号
					management.setAccptrSvcr(pool.getVtaccptrBankAccount());//承兑人开户行全称
					management.setAccptrSvcrNm(pool.getVtaccptrBankName());//承兑人开户行行名
					management.setTrusteeshipFalg(pool.getVtLogo());//是都托管
					management.setDraftOwnerSts("");//票据持有类型
					management.setIsEdu("");//是否产生额度:虚拟票据池均不产生额度
					management.setRiskLevel("");//风险等级：虚拟票据池无风险等级
					management.setRecePayType(pool.getPayType());//票据权益（QY_01:持有票据    QY_02:应付票据）
					management.setDataSource("SRC_04");//数据来源:虚拟票据池录入
					management.setBillSaveAddr(pool.getBillPosition());//票据保管地(01:本行   02：自持  03：他行)
					management.setOtherBankSaveAddr(pool.getBillPositionAddr());//他行保管地
					management.setContractNo(pool.getContractNo());//交易合同号
					management.setAcceptanceAgreeNo(pool.getAcceptanceAgreeNo());//承兑协议编号
					management.setBpsNo(pool.getBpsNo());//票据池编号
					management.setBpsName(pool.getBpsName());//票据池名称
					management.setStatusFlag(PoolComm.YES);
					
					management.setElecDraftAccount(pool.getVtplApplyAcctId());//电票签约账号
					
					pedProtocolService.txStore(management);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public void txDiscountEdu(List<DraftPool> draftList, PedProtocolDto dto,BigDecimal tradeAmt,PlDiscount plDiscount)
			throws Exception {
		
		for (DraftPool pool : draftList) {
			if(pool.getDraftSource().equals(PoolComm.CS01) || pool.getTradeAmt().compareTo(tradeAmt) == 0){
				//PL_POOL表处理
				pool.setTXFlag("1");//强贴标志
				pool.setAssetStatus(PoolComm.DS_03);
				
				//大票表处理
				PoolBillInfo billInfo = pool.getPoolBillInfo();
				billInfo.setSDealStatus(PoolComm.DS_03);
				
				plDiscount.setBeginRangeNo(billInfo.getBeginRangeNo());
				plDiscount.setEndRangeNo(billInfo.getEndRangeNo());
				plDiscount.setTradeAmt(tradeAmt);
				
				//出池表处理
				DraftPoolOut out = this.poolOutDataProcess(pool);
				out.setPoolAgreement(pool.getPoolAgreement());
				this.txStore(out);
				this.txStore(pool);
				this.txStore(billInfo);

				//资产登记表处理
				assetRegisterService.txDraftStockOutAssetChange(dto.getPoolAgreement(), pool,tradeAmt, PublicStaticDefineTab.STOCK_OUT_TYPE_OUTPOOL);	
			}else{
				//拆分票据贴现
				/**
				 * 生成拆票的子票区间
				 */
				DraftRange rangeIn = DraftRangeHandler.buildLimitNewBeginAndEndDraftRange(pool.getAssetAmt(), tradeAmt, pool.getStandardAmt(), pool.getBeginRangeNo(), pool.getEndRangeNo());
				DraftRange rangeOut = DraftRangeHandler.buildLimitBeginAndEndDraftRange(pool.getAssetAmt(), tradeAmt, pool.getStandardAmt(), pool.getBeginRangeNo(), pool.getEndRangeNo());

				PoolBillInfo billInfo = pool.getPoolBillInfo();
				
				
				//1、生成一条出池的资产池表数据
				DraftPool outPool = AssetFactory.newDraftPool();
				PoolBillInfo outBill = new PoolBillInfo();
				BeanUtil.copyValue(pool, outPool);
				outPool.setId(null);
				outPool.setBeginRangeNo(rangeOut.getBeginDraftRange());
				outPool.setEndRangeNo(rangeOut.getEndDraftRange());
				outPool.setAssetAmt(tradeAmt);
				outPool.setTradeAmt(tradeAmt);
				
				
				outPool.setSplitId(billInfo.getBillinfoId());//拆分前的大票表主键id
				outPool.setTXFlag("1");//强贴标志
				outPool.setAssetStatus(PoolComm.DS_03);
				outPool.setLastOperName("票据池贴现生成的新的一条出池拆分后的数据！");
				outPool.setLastOperTm(new Date());
				this.txStore(outPool);
				
				//2、生成一条出池的票据基本信息表数据
				BeanUtil.copyValue(billInfo, outBill);
				outBill.setBillinfoId(null);
				outBill.setBeginRangeNo(rangeOut.getBeginDraftRange());
				outBill.setEndRangeNo(rangeOut.getEndDraftRange());
				outBill.setTradeAmt(tradeAmt);
				outBill.setFBillAmount(tradeAmt);
				outBill.setSplitId(billInfo.getBillinfoId());//拆分前的大票表主键id
				outBill.setSDealStatus(PoolComm.DS_03);
				outBill.setLastOperName("票据池贴现生成的新的一条出池拆分后的数据！");
				outBill.setLastOperTm(new Date());
				this.txStore(outBill);
				
				
				
				plDiscount.setSplitId(billInfo.getBillinfoId());//拆分前的大票表主键id
				plDiscount.setBeginRangeNo(rangeOut.getBeginDraftRange());
				plDiscount.setEndRangeNo(rangeOut.getEndDraftRange());
				plDiscount.setTradeAmt(tradeAmt);
				
				this.txStore(plDiscount);
				
				
				//4、生成一条在池的票据基本信息表数据,修改在池状态
				PoolBillInfo inBill = new PoolBillInfo();
				
				BeanUtil.copyValue(billInfo, inBill);
				inBill.setBillinfoId(null);
				inBill.setTradeAmt(billInfo.getFBillAmount().subtract(tradeAmt));
				inBill.setFBillAmount(billInfo.getFBillAmount().subtract(tradeAmt));
				inBill.setSplitId(billInfo.getBillinfoId());//拆分前的大票表主键id
				inBill.setBeginRangeNo(rangeIn.getBeginDraftRange());
				inBill.setEndRangeNo(rangeIn.getEndDraftRange());
				inBill.setSDealStatus(PoolComm.DS_02);
				inBill.setLastOperName("票据池贴现生成的新的一条在池拆分后的数据！");
				inBill.setLastOperTm(new Date());
				this.txStore(inBill);
				
				//3、生成一条在池的资产池表数据,修改在池状态
				DraftPool inPool = AssetFactory.newDraftPool();
				
				BeanUtil.copyValue(pool, inPool);
				inPool.setId(null);
				inPool.setSplitId(billInfo.getBillinfoId());//拆分前的大票表主键id
				inPool.setBeginRangeNo(rangeIn.getBeginDraftRange());
				inPool.setEndRangeNo(rangeIn.getEndDraftRange());
				inPool.setAssetStatus(PoolComm.DS_02);
				inPool.setPoolBillInfo(billInfo);
				inPool.setAssetAmt(pool.getAssetAmt().subtract(tradeAmt));
				inPool.setTradeAmt(pool.getAssetAmt().subtract(tradeAmt));
				inPool.setLastOperName("票据池贴现生成的新的一条在池拆分后的数据！");
				inPool.setLastOperTm(new Date());
				
				this.txStore(inPool);
				
				
				
				//5、修改原资产池表数据为做过拆分操作的失效
				pool.setAssetStatus(PoolComm.DS_12);//做过拆分操作的失效数据
				pool.setLastOperName("票据池贴现拆分后失效的原始数据！");
				pool.setLastOperTm(new Date());
				this.txStore(pool);
				
				//6、修改原票据基本信息表数据为做过拆分操作的失效
				billInfo.setSDealStatus(PoolComm.DS_12);//做过拆分操作的失效数据
				billInfo.setLastOperName("票据池贴现拆分后失效的原始数据！");
				billInfo.setLastOperTm(new Date());
				this.txStore(billInfo);
				
				
				//出池表处理
				DraftPoolOut out = this.poolOutDataProcess(outPool);
				out.setSplitId(billInfo.getBillinfoId());//
				out.setPoolAgreement(pool.getPoolAgreement());
				this.txStore(out);

				//资产登记表处理
				assetRegisterService.txDraftStockOutAssetChange(dto.getPoolAgreement(), pool,tradeAmt, PublicStaticDefineTab.STOCK_OUT_TYPE_OUTPOOL);	
				
			}
					
		}

		
	}

	/**
	 * @Description: 实体类字段制空
	 * @author xiecheng
	 */
	public static void reflectClassValueToNull(Object model) throws Exception {

		//获取此类的所有父类
		List<Class<?>> listSuperClass = new ArrayList();
		Class<?> superclass = model.getClass().getSuperclass();
		while (superclass != null) {
			if (superclass.getName().equals("java.lang.Object")) {
				break;
			}
			listSuperClass.add(superclass);
			superclass = superclass.getSuperclass();
		}

		//遍历处理所有父类的字段
		for (Class<?> clazz : listSuperClass) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName();
				Class type = fields[i].getType();
				Method method = clazz.getDeclaredMethod("set" + name.replaceFirst(name.substring(0, 1),
						name.substring(0, 1).toUpperCase()), type);
				method.invoke(model, new Object[]{null});
			}
		}

		//处理此类自己的字段
		Field[] fields = model.getClass().getDeclaredFields();
		for (int i = 1; i < fields.length; i++) {
			String name = fields[i].getName();
			Class type = fields[i].getType();
			//获取属性的set方法
            if(i == 45||i == 90||i == 91){
            	
                Method method = model.getClass().getDeclaredMethod("set" + name ,type);
                //将值设为null
                method.invoke(model, new Object[]{null});
            }else {
                Method method = model.getClass().getDeclaredMethod("set" + name.replaceFirst(name.substring(0, 1),
                        name.substring(0, 1).toUpperCase()), type);
                //将值设为null
                method.invoke(model, new Object[]{null});
            }
		}
	}

	@Override
	public Ret eleAccNoCheck(String newAccNo, String custNo, String bpsNo)
			throws Exception {
		
		logger.debug("【电票签约账号校验】....开始......");
		
		Ret ret = new Ret();
		
		ProListQueryBean bean = new ProListQueryBean();
		bean.setBpsNo(bpsNo);
		bean.setCustNo(custNo);
		bean.setStatus(PoolComm.PRO_LISE_STA_01);//已签约
		PedProtocolList oldPro = pedProtocolService.queryProtocolListByQueryBean(bean);
		
		
		String[] oldElecAccounts = null;// 原电票签约账号
		String[] newElecAccounts = null; // 新电票签约账号s
		List<String> inOldNotinNew = new LinkedList<String>(); // 在原签约账号中有在新签约账号中没有的账号信息
		
		if (null != oldPro) {
			if (oldPro.getElecDraftAccount() != null) {
				oldElecAccounts = oldPro.getElecDraftAccount().split("\\|");
			}
		}
		
		if (newAccNo != null) {
			newElecAccounts = newAccNo.split("\\|");
		}
		
		if (oldElecAccounts != null && oldElecAccounts.length > 0) {
			for (String old : oldElecAccounts) {
				if(newElecAccounts!=null){
					if (!ArrayUtils.contains(newElecAccounts,old)) {// 如果新的电票签约账号中不包含原电票签约账号（即删除原电票签约账号的行为）,记录下来
						inOldNotinNew.add(old);
					}
				}else{
					inOldNotinNew.add(old);
				}
			}
		}
		
		
		if (inOldNotinNew != null && inOldNotinNew.size() > 0) {
			List<PoolBillInfo> poolList1 = new LinkedList<PoolBillInfo>();// 大票表中初始化的票据集合
			List<PoolBillInfo> poolList2 = new LinkedList<PoolBillInfo>();// 大票表中非初始化的票据集合
			
			for (String acc : inOldNotinNew) {
				List<PoolBillInfo> poolListA = this.queryBillByElecAccAndTypeNew(acc, "0",bpsNo);// 初始化的票
				if (poolListA != null&& poolListA.size() > 0) {
					poolList1.addAll(poolListA);
				}
				List<PoolBillInfo> poolListB = this.queryBillByElecAccAndTypeNew(acc, "1",bpsNo);// 全部
				if (poolListB != null&& poolListB.size() > 0) {
					poolList2.addAll(poolListB);
				}
			}
			
			if (poolList2 != null && poolList2.size() > 0) {
				
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("【"+custNo+"】该客户已签约电票签约账户下有未处理完毕的业务，不允许更换电票账号！");
				return ret;
			} else {// 删除所有大票表中初始化的票据
				
				if (poolList1 != null&& poolList1.size() > 0) {// 如果该电票签约账号下只有初始化的票据，则删除所有初始化的数据，然后可以修改签约
					this.txDeleteAll(poolList1);
				}
			}
		}
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		return ret;
		
	}

	@Override
	public Ret endCreditCheck(PedProtocolDto ppd) throws Exception {
		Ret ret = new Ret();
		
		/*主户解约要求
		 * 
		 * (1)集团无票据（集团主户及集团成员都无票据，都没有对外担保。包含在途的也无票据）；
		 * (2)保证金为0；
         * (3)无融资业务；
		 * (4)MIS系统融资担保合同到期。
		 */
		
		/*
		 * 锁AssetPool表
		 */
		AssetPool pool = pedAssetPoolService.queryPedAssetPoolByProtocol(ppd);
		String bpsNo = ppd.getPoolAgreement();
		String apId = pool.getApId();
		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
		if(!isLockedSucss){//加锁失败
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
			return ret;
		}
		
		/*
		 * 同步核心保证金，并重新计算额度
		 */
		financialService.txBailChangeAndCrdtCalculation(ppd);
		
		/*
		 * 解锁AssetPool表，并重新计算该表数据
		 */
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
		
		
		
		pool = pedAssetPoolService.queryPedAssetPoolByProtocol(ppd);
		BigDecimal used = pool.getCrdtUsed();//全部已用
		BigDecimal free = pool.getCrdtFree();//全部未用
		
		if(BigDecimal.ZERO.compareTo(used)!=0){
			logger.info("该票据池已用额度不为0，不允许解约融资协议");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("该票据池已用额度不为0，不允许解约融资协议");
			return ret;
		}
		
		if(BigDecimal.ZERO.compareTo(free)!=0){
			logger.info("该票据池可用额度不为0，不允许解约融资协议");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("该票据池可用额度不为0，不允许解约融资协议");
			return ret;
		}
		
		
		//根据票据池编号查询有无在途票据校验
		PoolQueryBean queryBean = new PoolQueryBean();
		ArrayList<String> statusList = new ArrayList<String>();
		statusList.add(PoolComm.DS_00);//非初始化的票据
		statusList.add(PoolComm.DS_04);//非初出池完毕的
		statusList.add(PoolComm.DS_99);//不可入池票据
		statusList.add(PoolComm.TS05);//非初托收记账完毕的
		queryBean.setStatus(statusList);
		queryBean.setProtocolNo(ppd.getPoolAgreement());
		
		List<PoolBillInfo> infos = draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
		
		if(infos!=null && infos.size()>0){
			logger.info("该票据池有【"+infos.size()+"】张在途票据，不允许解约融资协议");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("该票据池有【"+infos.size()+"】张在途票据，不允许解约融资协议");
			return ret;
		}
		
		
		//(4)MIS系统融资担保合同到期校验。
		ProtocolQueryBean bean = new ProtocolQueryBean();
		bean.setPoolAgreement(ppd.getPoolAgreement());
		PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean( bean);
		Date contractDueDt = dto.getContractDueDt();
		long dueDate = contractDueDt.getTime();
		long today = new Date().getTime();
		if(today<dueDate){
			logger.info("融资担保合同未到期，不允许解约融资协议！");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("融资担保合同未到期，不允许解约融资协议！");
			return ret;
		}
			
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG("可进行融资解约");
		return ret;
			
	}

	@Override
	public String createProtocolStorePJC034(PedProtocolDto pro)
			throws Exception {
		Date today = DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT);//当前日期
		Date todayNextYear = DateUtils.formatDate(DateTimeUtil.getTodayNextYear(), DateUtils.ORA_DATE_FORMAT);//today加一年
		String poolAgreementNo = null;
		if(PoolComm.QYLX_01.equals(pro.getpSignType())){//基础签约
			poolAgreementNo = poolBatchNoUtils.txGetBatchNoBySession("JR", 6);
			String poolName="集团票据池("+pro.getCustname()+")";
			pro.setPoolAgreement(poolAgreementNo);//票据池编号
			pro.setPoolName(poolName);//票据池名称
			pro.setEffstartdate(today);//协议生效日
			pro.setEffenddate(todayNextYear);//协议到期日
			pro.setFeeDueDt(DateUtils.modDay(today,-1));
			pro.setFeeIssueDt(DateUtils.modDay(today,-1));
			pro.setOfficeNet(pro.getSignDeptNo());//受理网点号--默认赋值签约机构
			pro.setOfficeNetName(pro.getSignDeptName());//受理网点名称
			pro.setvStatus(PoolComm.VS_01);
			pro.setOperateTime(new Date());
			pro.setOpenFlag(PoolComm.OPEN_00);// 未开通
			this.txStore(pro);

		}
		//基础签约+融资签约
		if(pro.getpSignType().equals(PoolComm.QYLX_02)){
			PedProtocolDto protocolDto = new PedProtocolDto() ;//copy传值
			PedProtocolDto oldPro = null;
			if(pro.getPoolAgreement()!=null){//已基础签约
				ProtocolQueryBean queryBean = new ProtocolQueryBean();
				queryBean.setPoolAgreement(pro.getPoolAgreement());
				oldPro = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);//根据票据池编号取得原已基础签约数据
				Date effStartDate = oldPro.getEffstartdate();//协议生效日
				Date effEndDate = oldPro.getEffstartdate();//协议到期日
				pro.setPoolInfoId(oldPro.getPoolInfoId());
				pro.setEffstartdate(effStartDate);
				pro.setEffenddate(effEndDate);

				String poolName="集团票据池("+pro.getCustname()+")";
				pro.setPoolName(poolName);//票据池名称
				Date yesterday = DateUtils.modDay(today,-1);//默认赋值当前日期减一天
				pro.setFeeDueDt(yesterday);//年费生效日
				pro.setFeeIssueDt(yesterday);//年费截止日
				pro.setContractTransDt(yesterday);//担保合同签订日期
				pro.setContractEffectiveDt(yesterday);////担保合同生效日期
				pro.setContractDueDt(yesterday);////担保合同到期日
				pro.setCreditamount(new BigDecimal(0.00));
				pro.setCreditUsedAmount(new BigDecimal(0.00));
				pro.setvStatus(PoolComm.VS_01);
				pro.setOperateTime(new Date());
				pro.setOpenFlag(PoolComm.OPEN_01);// 开通
				pro.setApproveFlag(PoolComm.APPROVE_03);// 签约审批通过
				pro.setFrozenstate(PoolComm.FROZEN_STATUS_00);
				this.dao.merge(pro);
				BeanUtils.copyProperties(pro,protocolDto);//copy传值处理
			}
			if(oldPro==null){//未空则未基础签约
				poolAgreementNo = poolBatchNoUtils.txGetBatchNoBySession("JR", 6);
				pro.setPoolAgreement(poolAgreementNo);//票据池编号
				pro.setPoolInfoId(null);
				pro.setEffstartdate(today);//协议生效日
				pro.setEffenddate(todayNextYear);//协议到期日

				String poolName="集团票据池("+pro.getCustname()+")";
				pro.setPoolName(poolName);//票据池名称
				Date yesterday = DateUtils.modDay(today,-1);//默认赋值当前日期减一天
				pro.setFeeDueDt(yesterday);//年费生效日
				pro.setFeeIssueDt(yesterday);//年费截止日
				pro.setContractTransDt(yesterday);//担保合同签订日期
				pro.setContractEffectiveDt(yesterday);////担保合同生效日期
				pro.setContractDueDt(yesterday);////担保合同到期日
				pro.setCreditamount(new BigDecimal(0.00));
				pro.setCreditUsedAmount(new BigDecimal(0.00));

				pro.setvStatus(PoolComm.VS_01);
				pro.setOperateTime(new Date());
				pro.setOpenFlag(PoolComm.OPEN_01);// 开通
				pro.setApproveFlag(PoolComm.APPROVE_03);// 签约审批通过
				pro.setFrozenstate(PoolComm.FROZEN_STATUS_00);
				this.txStore(pro);
				BeanUtils.copyProperties(pro,protocolDto);//copy传值处理
				
			}
			AssetType assetTypeDto = pedAssetTypeService.queryPedAssetTypeByProtocol(protocolDto,PoolComm.ED_BZJ_HQ);
			if(assetTypeDto!=null){
				BailDetail detail = poolBailEduService.queryBailDetail(assetTypeDto.getId());
				detail.setAssetNb(protocolDto.getMarginAccount());//保证金账号
				poolBailEduService.txStore(detail);
			}else{
				logger.error("签约信息维护，获取资产池为空！");
			}
			AssetPool ap =pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(protocolDto.getPoolAgreement(),null, null);
			if(ap==null){
				//创建asstPool信息
				pedProtocolService.createAssetPoolInfo(protocolDto);
				//创建assetType信息
				for(int i=0;i<4;i++){
					String assetType = null;//产生额度类型
					if(i==0){
						assetType = PoolComm.ED_PJC;//低风险票据额度
					}else if(i==1){
						assetType = PoolComm.ED_PJC_01;//高风险票据额度
					}else if(i==2){
						assetType = PoolComm.ED_BZJ_HQ;//活期保证金
					}else if(i==3){
						assetType = PoolComm.ED_BZJ_DQ;//定期保证金
					}
					//修改方法 
					pedProtocolService.txCreateAssetTypeInfo(protocolDto,assetType);
				}
			}
			poolAgreementNo = pro.getPoolAgreement();
		}
			
		return poolAgreementNo;
	}

	@Override
	public List<Object> queryProListCustNo(String bpsNo) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select proList.custNo from PedProtocolList as proList where 1=1 ");
		
		
		if(StringUtil.isNotBlank(bpsNo)){//票据池编号
			hql.append(" and proList.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(bpsNo);
			
		}
			
			
		hql.append(" and proList.status =:status");
		paramName.add("status");
		paramValue.add(PoolComm.PRO_LISE_STA_01);//已签约
			
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues );
		
		if(result!=null&&result.size()>0){
			return result;	
		}
		
		return null;
	}

	@Override
	public boolean memIsCanEndContractCheck(String bpsNo, List custNos)throws Exception {
		/*
		 * 成员子户是否可解约校验：校验传入客户下的电票签约账户下是否有有效票据
		 */
		
		
		ProListQueryBean bean = new ProListQueryBean();
		bean.setBpsNo(bpsNo);
		bean.setCustNos(custNos);
		List<PedProtocolList> oldMems = pedProtocolService.queryProListByQueryBean(bean);
		
		
		for(PedProtocolList mem : oldMems){
			
			String[] oldElecAccounts = null;// 原电票签约账号数组
			List<String> oldElecAccountList = new LinkedList<String>(); //  原电票签约账号LIST
			
			if (mem.getElecDraftAccount() != null) {
				oldElecAccounts = mem.getElecDraftAccount().split("\\|");
			}
			
			
			if (oldElecAccounts != null && oldElecAccounts.length > 0) {
				for (String old : oldElecAccounts) {
					oldElecAccountList.add(old);
				}
			}
			
			
			if (oldElecAccountList != null && oldElecAccountList.size() > 0) {
				List<PoolBillInfo> poolList1 = new LinkedList<PoolBillInfo>();// 大票表中初始化的票据集合
				List<PoolBillInfo> poolList2 = new LinkedList<PoolBillInfo>();// 大票表中非初始化的票据集合
				
				for (String acc : oldElecAccountList) {
					List<PoolBillInfo> poolListA = this.queryBillByElecAccAndTypeNew(acc, "0",bpsNo);// 初始化的票
					if (poolListA != null&& poolListA.size() > 0) {
						poolList1.addAll(poolListA);
					}
					List<PoolBillInfo> poolListB = this.queryBillByElecAccAndTypeNew(acc, "1",bpsNo);// 全部
					if (poolListB != null&& poolListB.size() > 0) {
						poolList2.addAll(poolListB);
					}
				}
				
				if (poolList2 != null && poolList2.size() > 0) {
					
					return false;
					
				} else {// 删除所有大票表中初始化的票据
					
					if (poolList1 != null&& poolList1.size() > 0) {// 如果该电票签约账号下只有初始化的票据，则删除所有初始化的数据，然后可以修改签约
						this.txDeleteAll(poolList1);
					}
				}
			}
			
		}
		
		return true;
	}

	@Override
	public List<Map> queryPoolFee(String custNO, String bpsNO) throws Exception {
		logger.info("根据客户号["+custNO+"],票据池编号:["+bpsNO+"],查询协议开始!");
		List reList = new ArrayList();
		StringBuffer hql = new StringBuffer("select pro from PedProtocolDto pro where  pro.poolAgreement= ? " +
				"and pro.feeType ='"+PoolComm.SFMS_01+"' and pro.feeDueDt < ? ");
		List param = new ArrayList();
		param.add(bpsNO);
		param.add(new Date());
		
		List result = this.find(hql.toString(), param);
		logger.debug("得到协议为:["+result+"]");
		String isCharge = null;
		if(result!=null && result.size()>0){//需要收年费
			isCharge = PoolComm.YES;			
		}else{//不需要收年费
			isCharge = PoolComm.NO;
		}
		
		/*
		 * 年费标准查询
		 */
		BigDecimal fee = new BigDecimal("0");//收取年费标准
		PlFeeScale feeSc = pedProtocolService.queryFeeScale();
		if(feeSc!=null){
			fee = feeSc.getEveryYear();
		}
		
		HashMap map = new HashMap();
		
		map.put("ANNUAL_FEE_STANDARD", fee);//年费收取标准
		map.put("IS_CHARGE", isCharge);//是否需要收费

		reList.add(map);
		
		if(reList!=null && reList.size()>0){
			return reList;
		}
		return null;
	}

	@Override
	public List<PedCheck> queryPedCheck(String bpsNo, String custNo)
			throws Exception {
			
		//票据池对账,根据票据池编号与核心客户号查询pedcheck表
		StringBuffer hql = new StringBuffer("select ch from PedCheck ch where ch.poolAgreement = ? and ch.custNo = ? order by ch.accountDate desc ,ch.curTime desc");		
		List param = new ArrayList();
		param.add(bpsNo);
		param.add(custNo);
		List result = this.find(hql.toString(), param);
		
		if(result!=null && result.size()>0){
			PedCheck ch = (PedCheck) result.get(0);
			if(PoolComm.DZJG_00.equals(ch.getCheckResult())){//未对账
				return result;
			}
		}
		return null;
	}

	@Override
	public void txChangeBailAccNo(String bpsNo,String marginAccount,String oldMarginAccount) throws Exception {
		//查询资产登记信息
		AssetRegister assetRegister = assetRegisterService.getAssetRegisterByCustSignNoAndAssetNo(bpsNo, oldMarginAccount);
		if( null != assetRegister) {
			assetRegister.setAssetNo(marginAccount);
			this.txStore(assetRegister);
		}
		
	}

}
