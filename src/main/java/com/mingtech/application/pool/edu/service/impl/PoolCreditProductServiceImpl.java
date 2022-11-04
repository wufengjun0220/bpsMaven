package com.mingtech.application.pool.edu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.CreditPedBean;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.MisCredit;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
@Service("poolCreditProductService")
public class PoolCreditProductServiceImpl extends GenericServiceImpl implements PoolCreditProductService{
	private static final Logger logger = Logger.getLogger(PoolCreditProductServiceImpl.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	@Autowired
	private PoolCreditClientService clientService;
	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public Class getEntityClass() {
		return null;
	}

	@Override
	public List<CreditProduct> queryCedtProductList(CreditQueryBean queryBean)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" from CreditProduct cp where 1=1 ");
		List value = new ArrayList();
		List key = new ArrayList();
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getBpsNo())){
				String bpsNo = queryBean.getBpsNo();
				hql.append(" and cp.bpsNo=:bpsNo ");
				value.add("bpsNo");
				key.add(bpsNo);
			}
			if(StringUtil.isNotBlank(queryBean.getCustNo())){
				String custNo = queryBean.getCustNo();
				hql.append(" and cp.custNo=:custNo ");
				value.add("custNo");
				key.add(custNo);
			}
			if(StringUtil.isNotBlank(queryBean.getCrdtNo())){
				String crdtNo = queryBean.getCrdtNo();
				hql.append(" and cp.crdtNo=:crdtNo ");
				value.add("crdtNo");
				key.add(crdtNo);
			}
			if(StringUtil.isNotBlank(queryBean.getSttlFlag())){
				String sttlFlag = queryBean.getSttlFlag();
				hql.append(" and cp.sttlFlag=:sttlFlag ");
				value.add("sttlFlag");
				key.add(sttlFlag);
			}
			if(StringUtil.isNotBlank(queryBean.getCrdtType())){
				String crdtType = queryBean.getCrdtType();
				hql.append(" and cp.crdtType=:crdtType ");
				value.add("crdtType");
				key.add(crdtType);
			}
			if(StringUtil.isNotBlank(queryBean.getCrdtStatus())){
				String crdtStatus = queryBean.getCrdtStatus();
				hql.append(" and cp.crdtStatus=:crdtStatus ");
				value.add("crdtStatus");
				key.add(crdtStatus);
			}
			if(StringUtil.isNotBlank(queryBean.getRiskLevel())){
				String risklevel = queryBean.getRiskLevel();
				hql.append(" and cp.risklevel=:risklevel ");
				value.add("risklevel");
				key.add(risklevel);
			}
			if(queryBean.getCrdtTypeList()!=null && queryBean.getCrdtTypeList().size()>0){
				List crdtTypeList = queryBean.getCrdtTypeList();
				hql.append(" and cp.crdtType in (:crdtTypeList) ");
				value.add("crdtTypeList");
				key.add(crdtTypeList);
			}
			
			if(queryBean.getCrdtStatusList()!=null && queryBean.getCrdtStatusList().size()>0){
				List crdtStatusList = queryBean.getCrdtStatusList();
				hql.append(" and cp.crdtStatus in (:crdtStatusList) ");
				value.add("crdtStatusList");
				key.add(crdtStatusList);
			}
			
			if(queryBean.getSttlFlagList()!=null && queryBean.getSttlFlagList().size()>0){
				List sttlFlagList = queryBean.getSttlFlagList();
				hql.append(" and cp.sttlFlag in (:sttlFlagList) ");
				value.add("sttlFlagList");
				key.add(sttlFlagList);
			}
			
			if(StringUtil.isNotBlank(queryBean.getIsOnline())){
				String bpsNo = queryBean.getIsOnline();
				hql.append(" and cp.isOnline=:isOnline ");
				value.add("isOnline");
				key.add(queryBean.getIsOnline());
			}
		}
		
		hql.append(" order by cp.crdtDueDt asc ");
		String paramNames[] = (String[]) value.toArray(new String[value.size()]);
		Object paramValues[] = key.toArray();
		List<CreditProduct> rslt = this.find(hql.toString(), paramNames, paramValues);
		if(rslt!=null && rslt.size()>0){
			return rslt;			
		}
		return null;
	}

	@Override
	public CreditProduct queryProductByCreditNo(String creditNo,String sttlFlag)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" from CreditProduct cp where 1=1 ");
		List value = new ArrayList();
		List key = new ArrayList();
		if(StringUtil.isNotBlank(creditNo)){
			hql.append(" and cp.crdtNo=:crdtNo ");
			value.add("crdtNo");
			key.add(creditNo);
		}
		if(StringUtil.isNotBlank(sttlFlag)){
			hql.append(" and cp.sttlFlag=:sttlFlag ");
			value.add("sttlFlag");
			key.add(sttlFlag);
		}
		hql.append(" order by cp.crdtDueDt asc ");
		String paramNames[] = (String[]) value.toArray(new String[value
				.size()]);
		Object paramValues[] = key.toArray();
		List<CreditProduct> rslt = this.find(hql.toString(), paramNames, paramValues);
		if(null != rslt && rslt.size()>0){
			return rslt.get(0);
		}
		return null;
	}


	@Override
	public List<MisCredit> queryDetails(String loanNo ,String crdtNo) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT CRDT_NO ,CUST_NUMBER ,CUST_NAME ,LOAN_NO,TRANS_TIME ,LOAN_TYPE ,LOAN_STATUS ,TRANS_ACCOUNT," +
			            	"LOAN_AMOUNT,REPAYMENT_TIME" +
			            	" FROM MIS_CREDIT WHERE ( END_FLAG ='0' OR END_FLAG IS NULL ) ");
		
		if(crdtNo!=null&&!crdtNo.equals("")){
			hql.append("  AND CRDT_NO='"+crdtNo+"'");
		}
		if(loanNo!=null&&!loanNo.equals("")){
			hql.append("  AND LOAN_NO ='"+loanNo+"'");
		}
		List  rslt = this.dao.SQLQuery(hql.toString());
		if(rslt!=null && rslt.size()>0){
			return rslt;	
		}else{
			return null;				
		}
}
	@Override
	public void txUpdateMisCredit(MisCredit credit) throws Exception{
		StringBuffer hql = new StringBuffer();
		hql.append(" update MisCredit c set c.endFlag ='1' where ( c.endFlag ='0'  or c.endFlag is null ) ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		
		if(credit.getCrdtNo()!=null&&!credit.getCrdtNo().equals("")){
			hql.append("  and c.crdtNo=:crdtNo");
			keyList.add("crdtNo");
			valueList.add(credit.getCrdtNo());	
		}
		if(credit.getLoanNoReal()!=null&&!credit.getLoanNoReal().equals("")){
			hql.append("   and c.loanNoReal=:loanNoReal ");
			keyList.add("loanNoReal");
			valueList.add(credit.getLoanNoReal()); // 已完结	
		}

		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		this.dao.txBatchUpdate(hql.toString(), keyArray, valueList.toArray());
	}

	@Override
	public PedCreditDetail queryCreditDetailByTransAccountOrLoanNo(String transAccount,
			String loanNo) throws Exception {
		
		StringBuffer hql = new StringBuffer();
		hql.append(" select pd  from PedCreditDetail pd where 1=1 ");
		List value = new ArrayList();
		List key = new ArrayList();
		
		if(transAccount!=null&&!transAccount.equals("")){
			hql.append("  and pd.transAccount=:transAccount ");
			value.add("transAccount");
			key.add(transAccount);
		}
		if(loanNo!=null&&!loanNo.equals("")){
			hql.append("  and pd.loanNo=:loanNo ");
			value.add("loanNo");
			key.add(loanNo);
		}
		
		String paramNames[] = (String[]) value.toArray(new String[value.size()]);
		Object paramValues[] = key.toArray();
		List<PedCreditDetail> rslt = this.find(hql.toString(), paramNames, paramValues);
		if(rslt!=null && rslt.size()>0){
			return rslt.get(0);
		}else{
			return null;
		}

	}

	@Override
	public List<PedCreditDetail> queryCreditDetailList(CreditQueryBean queryBean) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" from PedCreditDetail cd where 1=1 ");
		List value = new ArrayList();
		List key = new ArrayList();
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getBpsNo())){
				String bpsNo = queryBean.getBpsNo();
				hql.append(" and cd.bpsNo=:bpsNo ");
				value.add("bpsNo");
				key.add(bpsNo);
			}
			if(StringUtil.isNotBlank(queryBean.getCustNo())){
				String custNo = queryBean.getCustNo();
				hql.append(" and cd.custNo=:custNo ");
				value.add("custNo");
				key.add(custNo);
			}
			if(StringUtil.isNotBlank(queryBean.getCrdtNo())){
				String crdtNo = queryBean.getCrdtNo();
				hql.append(" and cd.crdtNo=:crdtNo ");
				value.add("crdtNo");
				key.add(crdtNo);
			}
			if(StringUtil.isNotBlank(queryBean.getLoanNo())){
				String loanNo = queryBean.getLoanNo();
				hql.append(" and cd.loanNo=:loanNo ");
				value.add("loanNo");
				key.add(loanNo);
			}
			if(StringUtil.isNotBlank(queryBean.getLoanType())){
				String loanType = queryBean.getLoanType();
				hql.append(" and cd.loanType=:loanType ");
				value.add("loanType");
				key.add(loanType);
			}
			if(StringUtil.isNotBlank(queryBean.getLoanStatus())){
				String loanStatus = queryBean.getLoanStatus();
				hql.append(" and cd.loanStatus=:loanStatus ");
				value.add("loanStatus");
				key.add(loanStatus);
			}
			if(StringUtil.isNotBlank(queryBean.getTransAccount())){
				String transAccount = queryBean.getTransAccount();
				hql.append(" and cd.transAccount=:transAccount ");
				value.add("transAccount");
				key.add(transAccount);
			}
			if(StringUtil.isNotBlank(queryBean.getDetailStatus())){
				String detailStatus = queryBean.getDetailStatus();
				hql.append(" and cd.detailStatus=:detailStatus ");
				value.add("detailStatus");
				key.add(detailStatus);
			}
			
			if(queryBean.getLoanTypeList()!=null && queryBean.getLoanTypeList().size()>0){
				List loanTypeList = queryBean.getLoanTypeList();
				hql.append(" and cd.loanType in (:loanTypeList) ");
				value.add("loanTypeList");
				key.add(loanTypeList);
			}
			if(queryBean.getLoanStatusList()!=null && queryBean.getLoanStatusList().size()>0){
				List loanStatusList = queryBean.getLoanStatusList();
				hql.append(" and cd.loanStatus in (:loanStatusList) ");
				value.add("loanStatusList");
				key.add(loanStatusList);
			}
			
			//到期日，准确查询
			if(queryBean.getEndDate()!=null){
				Date endDate = queryBean.getEndDate();
				hql.append(" and cd.endTime  =:endDate) ");
				value.add("endDate");
				key.add(endDate);
			}
			
			//排除的借据状态
			if(queryBean.getLoanStatusNotInLsit()!=null && queryBean.getLoanStatusNotInLsit().size()>0){
				List getLsit = queryBean.getLoanStatusNotInLsit();
				hql.append(" and cd.loanStatus not in (:getLoanStatusNotInLsit) ");
				value.add("getLoanStatusNotInLsit");
				key.add(queryBean.getLoanStatusNotInLsit());
			}
			
		}
		
		hql.append(" order by cd.endTime asc ");
		String paramNames[] = (String[]) value.toArray(new String[value.size()]);
		Object paramValues[] = key.toArray();
		List<PedCreditDetail> rslt = this.find(hql.toString(), paramNames, paramValues);
		if(rslt!=null && rslt.size()>0){
			return rslt;			
		}
		return null;
	}

	@Override
	public void txUpdateCreditProduct(String contractNo, String status) throws Exception {
		CreditProduct product = this.queryProductByCreditNo(contractNo, null);
		product.setCrdtStatus(status);
		this.txStore(product);
	}
	
	
	
	
	//查询保贴人
	public List loadPosterCount(String acceptor,Page page) throws Exception{
		
		List resultList = new ArrayList();
		List list = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		String hql= "select b.guarantDiscName,sum(b.billAmt),b.creditObjType,b.guarantDiscNo from PedGuaranteeCredit b where 1=1 ";
		if(StringUtils.isNotEmpty(acceptor)){
			hql = hql+" and b.guarantDiscName like :guarantDiscName  ";
			paramName.add("guarantDiscName");
			paramValue.add("%"+acceptor+"%");	
		}
		hql = hql+" and b.status = :status and b.creditObjType = '2' ";
		paramName.add("status");
		paramValue.add(PoolComm.SP_01);//占用
		
//		hql = hql+" and b.billType = :billType  ";
//		paramName.add("billType");
//		paramValue.add(PoolComm.BILL_TYPE_BUSI);	
		
		hql = hql+" group by b.guarantDiscName,b.creditObjType,b.guarantDiscNo";
		hql = hql+" order by b.guarantDiscNo";
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues);
		if(resultList!=null && resultList.size()>0){
			for(int i =0; i<resultList.size();i++){
				Object[] obj = (Object[]) resultList.get(i);
				CreditPedBean  bean=new  CreditPedBean();
				if (obj[0] != null) {
					bean.setGuarantDiscName(obj[0].toString());
				}
				if(obj[1] != null){
					bean.setBillAmtCount(Double.valueOf(obj[1].toString()));
				}
				if (obj[2] != null) {
					bean.setCreditObjType(obj[2].toString());
				}
				if (obj[3] != null) {
					bean.setGuarantDiscNo(obj[3].toString());
				}
				list.add(bean);
			}
			return list;
		}
		return null;
	}
	
	//查询保贴人明细
	public PedGuaranteeCredit queryBpsNo(String bpsNo) throws Exception {
		String hql = "from PedGuaranteeCredit ppd where ppd.bpsNo ='"
				+ bpsNo+"'";
			List find = this.find(hql);
			PedGuaranteeCredit ppd = null;
			if (find != null && find.size() > 0) {
				ppd = (PedGuaranteeCredit) find.get(0);
			}
		return ppd;
	}
	
	// 票据池占用保贴额度查询
	public List<CreditPedBean> loadPoolPasteount(String bpsNo,String isGroup,Page page) throws Exception{
		StringBuffer hql = new StringBuffer();
		List list = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		hql.append("select b.bpsNo,sum(b.billAmt) from PedGuaranteeCredit b where 1=1 ");
		
		//只统计商票
//		hql.append(" and b.billType =:billType  ");
//		paramName.add("billType");
//		paramValue.add(PoolComm.BILL_TYPE_BUSI);	
		
		if(StringUtils.isNotEmpty(bpsNo)){
			hql.append(" and b.bpsNo =:bpsNo  ");
			paramName.add("bpsNo");
			paramValue.add(bpsNo);	
		}
		if(StringUtils.isNotEmpty(isGroup)){
			hql.append(" and b.isGroup =:isGroup  ");
			paramName.add("isGroup");
			paramValue.add(isGroup);	
		}
		hql.append(" and b.status = :status and b.creditObjType = '2' ");
		paramName.add("status");
		paramValue.add(PoolComm.SP_01);//占用
		hql.append(" group by b.bpsNo");
		hql.append(" order by b.bpsNo");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List resultList = this.find(hql.toString(), paramNames, paramValues);
		if(resultList!=null && resultList.size()>0){
			for(int i =0; i<resultList.size();i++){
//					Object Object=resultList.get(i);
				Object[] obj = (Object[]) resultList.get(i);
				CreditPedBean  bean=new  CreditPedBean();
				if (obj[0] != null) {
					bean.setBpsNo(obj[0].toString());
					PedGuaranteeCredit ped = this.queryBpsNo(obj[0].toString());
					if(null != ped){
						bean.setBpsName(ped.getBpsName());
						bean.setIsGroup(ped.getIsGroup());
					}
				}
				if(obj[1] != null){
					bean.setBillAmtCount(Double.valueOf(obj[1].toString()));
				}
				list.add(bean);
			}
			return list;
		}
		return null;
	}

	@Override
	public void txEndFinancier() throws Exception {
		logger.info("日终融资人解约后融资生效标识同步开始....");
		/*
		 * 查询非融资人但是融资仍生效的集团客户
		 */
		ProListQueryBean bean = new ProListQueryBean();
		List<String> custIdentityList = new ArrayList<String>();
		bean.setFinancingStatus(PoolComm.SXBZ_01);
		custIdentityList.add(PoolComm.KHLX_01);
		custIdentityList.add(PoolComm.KHLX_04);
		custIdentityList.add(PoolComm.KHLX_05);
		bean.setCustIdentityList(custIdentityList);			
		List<PedProtocolList> proList = pedProtocolService.queryProListByQueryBean(bean);
		
		List<PedProtocolList> modList = new ArrayList<PedProtocolList>();//需要保存的列表
		
		if(proList!=null && proList.size()>0){
			for(PedProtocolList mem : proList){
				String bpsNo = mem.getBpsNo();
				String custNo = mem .getCustNo();
				
				
				CreditQueryBean queryBean = new CreditQueryBean();
				queryBean.setBpsNo(bpsNo);
				queryBean.setCustNo(custNo);
				queryBean.setSttlFlag(PoolComm.JQZT_WJQ);//未结清
				List<CreditProduct> products  = this.queryCedtProductList(queryBean);
				
				if(products==null || products.size()<1){
					mem.setFinancingStatus(PoolComm.SXBZ_00);//失效
					modList.add(mem);
				}
				
			}
			this.txStoreAll(modList);
		}
		
		logger.info("日终融资人解约后融资生效标识同步完成！");
		
	}


	@Override
	public PedGuaranteeCredit queryByBean(PoolQueryBean bean) throws Exception {
		List resultList = new ArrayList();
		List list = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		String hql= "select b from PedGuaranteeCredit b where 1=1 ";
		if(StringUtils.isNotEmpty(bean.getProtocolNo())){//票号查询
			hql = hql+" and b.bpsNo =:bpsNo  ";
			paramName.add("bpsNo");
			paramValue.add(bean.getProtocolNo());
		}
		if(StringUtils.isNotEmpty(bean.getBillNo())){
			hql = hql+" and b.billNo =:billNo  ";
			paramName.add("billNo");
			paramValue.add(bean.getBillNo());
		}
		/********************融合改造新增 start******************************/
		if(StringUtils.isNotEmpty(bean.getBeginRangeNo())){
			hql = hql+" and b.beginRangeNo =:beginRangeNo  ";
			paramName.add("beginRangeNo");
			paramValue.add(bean.getBeginRangeNo());
		}
		if(StringUtils.isNotEmpty(bean.getEndRangeNo())){
			hql = hql+" and b.endRangeNo =:endRangeNo  ";
			paramName.add("endRangeNo");
			paramValue.add(bean.getEndRangeNo());
		}
		/********************融合改造新增 end******************************/
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues);
		if(resultList != null &&  resultList.size() >0 ){
			return (PedGuaranteeCredit) resultList.get(0);
		}
		return  null;
	}
	
	@Override
	public PedCreditDetail txSynchroLoan(PedCreditDetail detail,CreditProduct product,String loanType) throws Exception{
		
		if (PoolComm.XD_02.equals(loanType) || PoolComm.XD_05.equals(loanType)) {// 表内业务
			logger.info("日间表内业务借据同步核心交易开始，贷款账号【" + detail.getTransAccount() + "】");
			
			CoreTransNotes notes = new CoreTransNotes();
			notes.setAccNo(detail.getTransAccount());// 贷款账号
			//核心贷款还款信息查询
			ReturnMessageNew response = poolCoreService.PJH126012Handler(notes);
	
			if (response.isTxSuccess()) {
				Map map = response.getBody();
				BigDecimal loanLeftAmt = detail.getActualAmount();
	        	if(DateUtils.checkOverLimited(new Date(), detail.getEndTime())){//逾期
					loanLeftAmt =  new BigDecimal((String)map.get("LOAN_LEFT_AMT"));//结清贷款需还款总额
				}else{
					loanLeftAmt =  new BigDecimal((String)map.get("RESTITUTE_PRINCIPAL_AMT"));//应还本金
				}

				if (detail.getActualAmount().compareTo(loanLeftAmt) > 0) {// 发生还款，释放 ---日间不存在占用的情况
					
					BigDecimal restReleaseAmt = detail.getActualAmount().subtract(loanLeftAmt);// 需要释放的金额
					product.setRestReleaseAmt(restReleaseAmt.multiply(new BigDecimal(product.getCcupy())));// 释放金额乘比例

					detail.setActualAmount(loanLeftAmt);// 记录结清贷款需还款总额
					this.txStore(detail);
					logger.info("日间额度处理释放借据【" + detail.getLoanNo() + "】额度为：" + product.getRestReleaseAmt()+" 资产登记处理开始...");
					
					/*
					 * 借据变更登记
					 */
					
					PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, product.getBpsNo(), null, null, null);
					AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
					String apId = ap.getApId();
					CreditRegister crdtReg = creditRegisterService.createCreditRegister(detail, dto,apId);
					creditRegisterService.txSaveCreditRegister(crdtReg);
					
					//核心同步保证金并重新计算池额度信息
					financialService.txBailChangeAndCrdtCalculation(dto);
					
					//解锁AssetPool表，并重新计算该表数据
					pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true); 
					
					//协议已用额度修改
					OnlineQueryBean query = new OnlineQueryBean();
					query.setBpsNo(dto.getPoolAgreement());
					query.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
					PedOnlineCrdtProtocol pro =pedOnlineCrdtService.queryOnlineProtocol(query); 	
					if(null!=pro){
						pro.setUsedAmt(pro.getUsedAmt().subtract(restReleaseAmt));
						this.txStore(pro);
					}
					
				}
			} else {
				throw new Exception("表内业务借据同步失败！返回码" + response.getRet().getRET_CODE() + "，返回信息" + response.getRet().getRET_MSG());
			}
			
			logger.info("日间额度处理表内业务处理结束......");
		
		} else {// 表外业务
			
			CoreTransNotes transNotes = new CoreTransNotes();
			transNotes.setAccNo(detail.getTransAccount());
			if(PoolComm.XD_04.equals(loanType)){//信用证为活期（预存保证金账户）
				transNotes.setCurrentFlag("1");
			}else{
				transNotes.setCurrentFlag("2");
			}
			ReturnMessageNew response = null;
			logger.info("日间表外业务借据同步核心交易开始，业务保证金账号【" + detail.getTransAccount() + "】");
			response = poolCoreService.PJH716040Handler(transNotes, "0");
			if (response.isTxSuccess()) {
				Map map = response.getBody();
				BigDecimal val = new BigDecimal("0");// 核心业务保证金账户余额
//				String status = (String)map.get("ACCT_STATUS");//账户状态
				if (map.get("BALANCE") != null) {
					val = BigDecimalUtils.valueOf((String) map.get("BALANCE"));
				}

				
				BigDecimal loanAmt = detail.getLoanAmount();// 借据金额
				BigDecimal amt = detail.getActualAmount();// 借据记录的实占金额
				BigDecimal sfAmt = amt.subtract(loanAmt.subtract(val));// 需要释放金额
				
				/**
				 * 新增逻辑
				 * 是当日发生的在线银承业务，零做了未用退回，查出对应的 PlOnlineAcptDetail ,
				 * 把状态改为012作废
				 */
				PlOnlineAcptDetail acptDetail = pedOnlineAcptService.queryPlOnlineAcptDetailByBillNo(detail.getLoanNo());
				 /*
			     * 调用BBSP系统正面信息查询接口
			     */
				String billStatus ="";
				ECDSPoolTransNotes transNotes1 = new ECDSPoolTransNotes();
				transNotes1.setBillId(acptDetail.getBillId());
				transNotes1.setBeginRangeNo(acptDetail.getBeginRangeNo());
				transNotes1.setEndRangeNo(acptDetail.getEndRangeNo());
				transNotes1.setBillSource(acptDetail.getDraftSource());
				transNotes1.setDataSource("3");
				
				transNotes1.setCurrentPage(1);
				transNotes1.setPageSize(10);
				
				ReturnMessageNew response1 = poolEcdsService.txApplyQueryBillFace(transNotes1);
				if(response1.isTxSuccess()){
					List<Map> list = response1.getDetails();//从BBSP系统查回的正面信息
					if(list !=null && list.size() >0 ){
						for (Map map1 : list) {
							List<Map> bills = (List<Map>) map1.get("BILL_INFO_ARRAY");
							if(bills != null){
								for (Map billMap : bills) {
									if(acptDetail.getDraftSource().equals(PoolComm.CS01)){
										billStatus = (String) billMap.get("BILL_INFO_ARRAY.ECDS_BILL_STATUS");
									}else if (acptDetail.getDraftSource().equals(PoolComm.CS02)){
										billStatus = (String) billMap.get("BILL_INFO_ARRAY.MIS_BILL_STATUS");
									}
								}
							}
						}
					}
					
				}
				logger.info("票据状态："+billStatus);
				
				if(StringUtils.isNotBlank(billStatus) && (billStatus.equals("TE200401_02") || billStatus.equals("CS05")) ){//已撤销出票成功  作未用退回
					
					detail.setLoanStatus(PoolComm.JJ_05);//未用退回
					detail.setDetailStatus(PoolComm.LOAN_0);//已结束借据
					this.txStore(detail);
					this.dao.flush();
					
					/**
					 * 当日未用退回需要释放信贷占用的授信额度
					 */
					if(DateUtils.formatDate(acptDetail.getIsseDate(),DateUtils.DATE_FORMAT).compareTo(DateUtils.formatDate(new Date(), DateUtils.DATE_FORMAT)) == 0){
						PlOnlineAcptBatch batch = (PlOnlineAcptBatch) this.load(acptDetail.getAcptBatchId(),PlOnlineAcptBatch.class);
						
						ReturnMessageNew  resp = pedOnlineAcptService.misRepayAcptPJE028(batch);
						if(resp.isTxSuccess()){
							//信贷额度释放成功
							/**
							 * 更新批次金额
							 */
							batch = pedOnlineAcptService.calculateBatchAmt(batch);
							this.txStore(batch);
						}else{
							logger.info("日间做借据同步,当日的未用退回数据,在做信贷额度释放时失败!");
						}
					}
					
					acptDetail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
					sfAmt = amt;
					
					this.txStore(acptDetail);
					
				}
				
				logger.info("日间同步核心借据信息，核心返回金额：0" + val + "  借据实际占用金额：50" + amt + "  借据金额：100" + loanAmt + "  本次需要释放的金额：50"+ sfAmt);
				
				if (sfAmt.compareTo(new BigDecimal("0")) > 0) {// 如果有补存业务保证金，则释放
					product.setRestReleaseAmt(sfAmt.multiply(new BigDecimal(product.getCcupy())));// 释放的额度
					if(StringUtils.isNotBlank(billStatus) && billStatus.equals("TE200401_02")){//已撤销出票成功  作未用退回
						detail.setActualAmount(new BigDecimal(0));//未用退回后  借据的实际占用金额为0
					}else{
						detail.setActualAmount(loanAmt.subtract(val));
					}
					this.txStore(detail);
					
					/*
					 * 借据变更登记
					 */  
					
					PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, product.getBpsNo(), null, null, null);
					AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
					String apId = ap.getApId();
					CreditRegister crdtReg = creditRegisterService.createCreditRegister(detail, dto,apId);
					creditRegisterService.txSaveCreditRegister(crdtReg);
					
					//核心同步保证金并重新计算池额度信息
					financialService.txBailChangeAndCrdtCalculation(dto);
					
					//解锁AssetPool表，并重新计算该表数据
					pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true); 
					
				}
			} else {
				throw new Exception("表外业务借据同步失败！返回码" + response.getRet().getRET_CODE() + "，返回信息" + response.getRet().getRET_MSG());
			}
		
		}
		
		return detail ;
	}
	

}
