package com.mingtech.application.pool.bank.creditsys.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("poolCreditService")
public class PoolCreditServiceImpl extends GenericServiceImpl implements
		PoolCreditService {
	private static final Logger logger = Logger
			.getLogger(PoolCreditServiceImpl.class);

	public Class getEntityClass() {
		return CreditProduct.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(CreditProduct.class);
	}

	public String txJudgeCreditAmountC1024Cal(String cusCommid,
			BigDecimal[] amountOfCreditProduct, Date[] dueDtOfCreditProduct)
			throws Exception {
		Exception exp = null;
		String msg = "";
		int count = amountOfCreditProduct != null ? amountOfCreditProduct.length
				: 0;
		CreditProduct cp = null;
		for (int m = 0; m < count; m++) {
			cp = new CreditProduct();
			cp.setCustNo(cusCommid);// 申请人组织机构代码
			cp.setCrdtAmt(amountOfCreditProduct[m]); // 信贷金额
			cp.setCrdtDueDt(dueDtOfCreditProduct[m]);// 信贷到期日
			cp.setUseAmt(amountOfCreditProduct[m]); // 额度扣减金额
			cp.setRestUseAmt(amountOfCreditProduct[m]); // 剩余待占用额度
			try {
				this.txjudgeAndEmployCreditAmountC102(cp);
			} catch (Exception e) {
				// 抛出异常，说明不能出池
				logger.info("出异常说明客户" + cusCommid + "的信贷产品不可以开立！");
				msg = e.getMessage();
				exp = new Exception(msg, e);
				// exp.setPass(false);
				throw exp;
			}
		}
		logger.info("没出异常说明客户" + cusCommid + "的信贷产品可以开立！");
		exp = new Exception(msg);
		// exp.setPass(true);
		throw exp;
	}

	public String judgeCreditAmountC101(String commId, BigDecimal applyEdu,
			Date crdtDueDt) throws Exception {
		return null;
	}

	public String txpayBackRequestC104(String commId, BigDecimal payBackAmt,
			String crdtNo, String flag) throws Exception {
		return null;
	}

	/**
	 * zhanghanyuan 2015/6/3 在自由资金还款时添加还款类型和自由资金还款金额字段
	 */
	public String txpayBackRequestC104(String commId, BigDecimal payBackAmt,
			String crdtNo, String flag, String payType, BigDecimal payMoney)
			throws Exception {
		return null;
	}

	public void reCallPayBackRequestC105(String commId, BigDecimal payBackAmt,
			String crdtNo) throws Exception {
	}

	public void txjudgeAndEmployCreditAmountC102(CreditProduct crdProduct)
			throws Exception {
	}

	public void txjudgeAndEmployCreditAmountC102ForDayEnd(
			CreditProduct crdProduct) throws Exception {
	}

	public void releaseCreditAmountC103(CreditProduct crdObject)
			throws Exception {
	}

	public void releaseCreditAmountC103Nor(CreditProduct crdObject)
			throws Exception {
	}

	public CreditProduct queryCrdProByCrdtNo(String crdtNo) throws Exception {
		CreditProduct crdt = new CreditProduct();
		List param = new ArrayList();
		String hql = "select dt from CreditProduct dt where dt.crdtNo=?";
		param.add(crdtNo);
		List result = this.find(hql, param);
		if (result.size() > 0) {
			crdt = (CreditProduct) result.get(0);
		}
		return crdt;
	}

	/**
	 * <p>
	 * 批量保存风险票据实体
	 * </p>
	 */
	public void saveBlackLists(List blackLists) throws Exception {

	}

	/**
	 * add 201555 zhaoding 还款 - 手工释放额度 - 确认释放
	 * 
	 * @param ids
	 * @param user
	 * @throws Exception
	 */
	public void txReleaseEduByManual(String ids, User user) throws Exception {
	}

	public void txDeleteBillBlackLists() throws Exception {

	}

	//MIS接口 PJE007 在池票据查询接口
	public QueryResult queryDraftPoolPJE007(PoolQueryBean pq, Page page)
			throws Exception {
		QueryResult qr = new QueryResult();
		List records = queryDraftInfos(pq);//根据条件查询融资池
		if (records != null && records.size() > 0) {
			qr = QueryResult.buildQueryResult(records, "assetAmt");
		}
		return qr;
	}
	
	//强制贴现引票查询
	public List<DraftPool> queryDraftInfos(PoolQueryBean pq) {
		logger.info("强制贴现引票查询开始。。。。。。。。。。");
		StringBuffer hql = new StringBuffer("select dto from DraftPool  dto,PoolBillInfo bill  where dto.assetNb = bill.SBillNo and dto.beginRangeNo = bill.beginRangeNo and dto.endRangeNo = bill.endRangeNo and bill.SDealStatus != 'DS_12' ");
		List keys = new ArrayList();
		List values = new ArrayList();
		// 客户核心号
		if (StringUtil.isNotBlank(pq.getCustomernumber())) {
			hql.append(" and dto.assetCommId=:assetCommId");
			keys.add("assetCommId");
			values.add(pq.getCustomernumber());
		}
		//票据池编号
		if (StringUtil.isNotBlank(pq.getProtocolNo())) {
				hql.append(" and dto.poolAgreement=:poolAgreement");
				keys.add("poolAgreement");
				values.add(pq.getProtocolNo());
		}
		//票据号码
		if (StringUtil.isNotBlank(pq.getBillNo())) {
			hql.append(" and dto.assetNb=:assetNb");
			keys.add("assetNb");
			values.add(pq.getBillNo());
		}
		//票据种类
		if (StringUtil.isNotBlank(pq.getsBillType())) {
			if (!"0000".equals(pq.getsBillType())) {
				hql.append(" and dto.assetType=:assetType");
				keys.add("assetType");
				values.add(pq.getsBillType());
				if(pq.getsBillType().equals(PublicStaticDefineTab.BILL_TYPE_BANK)){
					hql.append(" and (dto.isEduExist= '1' or dto.cpFlag = '1') ");
				}
			}else{
				hql.append(" and ((dto.isEduExist= '1' and dto.assetType = 'AC01') or dto.assetType = 'AC02' or dto.cpFlag = '1') ");
			}
		}
		//承兑人开户行行号
		if (StringUtil.isNotBlank(pq.getsAcceptorBankCode())) {
			hql.append(" and dto.plAccptrSvcr=:plAccptrSvcr");
			keys.add("plAccptrSvcr");
			values.add(pq.getsAcceptorBankCode());
		}
		//票据池状态 
		if (StringUtil.isNotBlank(pq.getCirStage())) {
			hql.append(" and dto.assetStatus=:assetStatus");
			keys.add("assetStatus");
			values.add(pq.getCirStage());
		}
		// 票据介质
		if (StringUtil.isNotEmpty(pq.getSBillMedia())) {
			hql.append(" and dto.plDraftMedia=:plDraftMedia");
			keys.add("plDraftMedia");
			values.add(pq.getSBillMedia());
		}
		// 票据ID
		if (StringUtil.isNotEmpty(pq.getBusinessId())) {
			hql.append(" and dto.poolBillInfo.discBillId=:discBillId");
			keys.add("discBillId");
			values.add(pq.getBusinessId());
		}
		//风险类型
		if(StringUtil.isNotEmpty(pq.getRickLevel())){
			hql.append(" and dto.rickLevel =:rickLevel");
			keys.add("rickLevel");
			values.add(pq.getRickLevel());
		}
		//额度占用类型
		if(StringUtil.isNotEmpty(pq.getCreditObjType())){
			hql.append(" and dto.creditObjType =:creditObjType");
			keys.add("creditObjType");
			values.add(pq.getCreditObjType());
		}
		//信贷强贴锁票标识	查询时不查已锁的票
		if(StringUtil.isNotEmpty(pq.getLockString())){
			hql.append(" and dto.lockz !=:lockz");
			keys.add("lockz");
			values.add(pq.getLockString());
		}
		
		//网银经办锁
		if(StringUtil.isNotEmpty(pq.getEbkLock())){
			hql.append(" and (bill.ebkLock =:ebkLock OR bill.ebkLock is null) ");
			keys.add("ebkLock");
			values.add(pq.getEbkLock());
		}
		
		//票据来源
		if(StringUtil.isNotEmpty(pq.getDraftSource())){
			hql.append(" and bill.draftSource =:draftSource");
			keys.add("draftSource");
			values.add(pq.getDraftSource());
		}
		
		//票据号起
		if(StringUtil.isNotEmpty(pq.getBeginRangeNo())){
			hql.append(" and bill.beginRangeNo =:beginRangeNo");
			keys.add("beginRangeNo");
			values.add(pq.getBeginRangeNo());
		}
		
		//票据号止
		if(StringUtil.isNotEmpty(pq.getEndRangeNo())){
			hql.append(" and bill.endRangeNo =:endRangeNo");
			keys.add("endRangeNo");
			values.add(pq.getEndRangeNo());
		}
		
		
		hql.append(" order by dto.plDueDt desc ");
		String[] keis = (String[]) keys.toArray(new String[keys.size()]);
		List<DraftPool> rsut = this.find(hql.toString(), keis, values.toArray());
		if (rsut != null && rsut.size() > 0) {
			return rsut;
		}
		
		logger.info("强制贴现引票查询结束。。。。。。。。。。");
		return null;
		
	}
	
	
	//根据条件查询融资池
	public List<DraftPool> queryDraftInfos(PoolQueryBean pq, Page page) {
		StringBuffer hql = new StringBuffer("select dto from DraftPool  dto ,PoolBillInfo bill where dto.assetNb = bill.SBillNo ");
		List keys = new ArrayList();
		List values = new ArrayList();
		// 客户核心号
		if (StringUtil.isNotBlank(pq.getCustomernumber())) {
			hql.append(" and dto.assetCommId=:assetCommId");
			keys.add("assetCommId");
			values.add(pq.getCustomernumber());
		}
		//票据池编号
		if (StringUtil.isNotBlank(pq.getProtocolNo())) {
				hql.append(" and dto.poolAgreement=:poolAgreement");
				keys.add("poolAgreement");
				values.add(pq.getProtocolNo());
		}
		//票据号码
		if (StringUtil.isNotBlank(pq.getBillNo())) {
			hql.append(" and dto.assetNb=:assetNb");
			keys.add("assetNb");
			values.add(pq.getBillNo());
		}
		/********************融合改造新增 start******************************/
		//子票区间起
		if (StringUtil.isNotBlank(pq.getBeginRangeNo())) {
			hql.append(" and dto.beginRangeNo=:beginRangeNo");
			keys.add("beginRangeNo");
			values.add(pq.getBeginRangeNo());
		}
		//子票区间止
		if (StringUtil.isNotBlank(pq.getEndRangeNo())) {
			hql.append(" and dto.endRangeNo=:endRangeNo");
			keys.add("endRangeNo");
			values.add(pq.getEndRangeNo());
		}
		/********************融合改造新增 start******************************/
		//票据种类
		if (StringUtil.isNotBlank(pq.getsBillType())) {
			if (!"0000".equals(pq.getsBillType())) {
			hql.append(" and dto.assetType=:assetType");
			keys.add("assetType");
			values.add(pq.getsBillType());
			}
		}
		//承兑人开户行行号
		if (StringUtil.isNotBlank(pq.getsAcceptorBankCode())) {
			hql.append(" and dto.plAccptrSvcr=:plAccptrSvcr");
			keys.add("plAccptrSvcr");
			values.add(pq.getsAcceptorBankCode());
		}
		//票据池状态 
		if (StringUtil.isNotBlank(pq.getCirStage())) {
			hql.append(" and dto.assetStatus=:assetStatus");
			keys.add("assetStatus");
			values.add(pq.getCirStage());
		}
		// 票据介质
		if (StringUtil.isNotEmpty(pq.getSBillMedia())) {
			hql.append(" and dto.plDraftMedia=:plDraftMedia");
			keys.add("plDraftMedia");
			values.add(pq.getSBillMedia());
		}
		//批次号
		if(StringUtil.isNotEmpty(pq.getSBatchNo())){
			hql.append(" and dto.doBatchNo=:doBatchNo");
			keys.add("doBatchNo");
			values.add(pq.getSBatchNo());
		}
		// 票据ID
		if (StringUtil.isNotEmpty(pq.getBusinessId())) {
			hql.append(" and dto.poolBillInfo.discBillId=:discBillId");
			keys.add("discBillId");
			values.add(pq.getBusinessId());
		}
		//状态集合
		if(pq.getStatus() != null && pq.getStatus().size()>0){
			hql.append(" and dto.assetStatus in(:status)");
			keys.add("status");
			values.add(pq.getStatus());
		}
		//0额度
		if(StringUtil.isNotEmpty(pq.getIsEdu())){
			hql.append(" and dto.isEduExist=:isEdu");
			keys.add("isEdu");
			values.add(pq.getIsEdu());
		}
		//贴现标识
		if(StringUtil.isNotEmpty(pq.getRemark())){
			hql.append(" and dto.TXFlag =:TXFlag");
			keys.add("TXFlag");
			values.add(pq.getRemark());
		}
		//风险类型
		if(StringUtil.isNotEmpty(pq.getRickLevel())){
			hql.append(" and dto.rickLevel =:rickLevel");
			keys.add("rickLevel");
			values.add(pq.getRickLevel());
		}
		//额度占用类型
		if(StringUtil.isNotEmpty(pq.getCreditObjType())){
			hql.append(" and dto.creditObjType =:creditObjType");
			keys.add("creditObjType");
			values.add(pq.getCreditObjType());
		}
		//信贷强贴锁票标识	查询时不查已锁的票
		if(StringUtil.isNotEmpty(pq.getLockString())){
			hql.append(" and dto.lockz !=:lockz");
			keys.add("lockz");
			values.add(pq.getLockString());
		}
		
		//网银经办锁
		if(StringUtil.isNotEmpty(pq.getEbkLock())){
			hql.append(" and bill.ebkLock =:ebkLock");
			keys.add("ebkLock");
			values.add(pq.getEbkLock());
		}
		
		
			hql.append(" order by dto.plDueDt desc ");
			String[] keis = (String[]) keys.toArray(new String[keys.size()]);
			List<DraftPool> rsut = this.find(hql.toString(), keis, values.toArray(), page);
			if (rsut != null && rsut.size() > 0) {
				return rsut;
			}
			return null;
		}


	public PoolBillInfo queryPoolBillinfoPJE008(String ids,String startNo, String endNo)
			throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		List <PoolBillInfo>  poolListAll = new LinkedList<PoolBillInfo>();
		StringBuffer hql = new StringBuffer();
		hql.append("select pool from PoolBillInfo  pool where 1=1 ");
		hql.append(" and pool.discBillId in (:discBillId)");
		paramName.add("discBillId");
		paramValue.add(StringUtil.splitList(ids, "\\|"));
		
		hql.append(" and pool.beginRangeNo =:beginRangeNo");
		paramName.add("beginRangeNo");
		paramValue.add(startNo);
		
		hql.append(" and pool.endRangeNo =:endRangeNo");
		paramName.add("endRangeNo");
		paramValue.add(endNo);
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		poolListAll = this.find(hql.toString(), paramNames, paramValues );
		if(poolListAll!=null &&poolListAll.size()>0){
			return poolListAll.get(0);
		}
		return null;
	}


	public QueryResult queryPlDiscountPJE010(PoolQueryBean pq, Page page)
			throws Exception {
		QueryResult qr = new QueryResult();
		List records = queryPlDiscounts(pq, page);//根据条件查询贴现表
		if (records != null && records.size() > 0) {
			qr = QueryResult.buildQueryResult(records, "FBillAmount");
		}
		return qr;
	}
	
	//根据条件查询贴现表
	public List<PlDiscount> queryPlDiscounts(PoolQueryBean pq, Page page) {
		StringBuffer hql = new StringBuffer("from PlDiscount as dto where 1=1");
		List keys = new ArrayList();
		List values = new ArrayList();
		// 客户核心号
		if (StringUtil.isNotBlank(pq.getCustomernumber())) {
			hql.append(" and dto.custNo=:custNo");
			keys.add("custNo");
			values.add(pq.getCustomernumber());
		}
		//票据池编号
		if (StringUtil.isNotBlank(pq.getProtocolNo())) {
			hql.append(" and dto.bpsNo=:bpsNo");
			keys.add("bpsNo");
			values.add(pq.getProtocolNo());
		}
		//票据ID
		if (StringUtil.isNotBlank(pq.getBusinessId())) {
			hql.append(" and dto.billinfoId.discBillId=:discBillId");
			keys.add("discBillId");
			values.add(pq.getBusinessId());
		}
			hql.append(" order by dto.DDueDt desc ");
			String[] keis = (String[]) keys.toArray(new String[keys.size()]);
			List<PlDiscount> rsut = this.find(hql.toString(), keis, values.toArray(), page);
			if (rsut != null && rsut.size() > 0) {
				return rsut;
			}
				return null;
		}
}
