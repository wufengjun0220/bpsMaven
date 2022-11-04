package com.mingtech.application.pool.draft.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.base.domain.CustEduQuery;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.PoolDictionaryCache;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.draft.domain.CorePdraftColl;
import com.mingtech.application.pool.draft.domain.CreditProductQuery;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolBase;
import com.mingtech.application.pool.draft.domain.DraftPoolBaseBatch;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.DraftQueryBeanQuery;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.CreditPrdtQuery;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditProductQueryBean;
import com.mingtech.application.pool.edu.domain.LimitVo;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.manage.domain.QueryPedListBean;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.trust.domain.DraftStorage;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.ConnectionUtils;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("draftPoolQueryService")
public class DraftPoolQueryServiceImpl extends GenericServiceImpl implements DraftPoolQueryService {

	private static final Logger logger = Logger.getLogger(DraftPoolQueryServiceImpl.class);
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private AssetTypeManageService assetTypeManageService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	
	@Override
	public String getEntityName() {
		return DraftPool.class.getName();
	}

	@Override
	public Class getEntityClass() {
		return DraftPool.class;
	}

	@Override
	public List queryDraftPool(DraftQueryBean bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append(
				" from DraftPool po,PedProtocolDto pp where 1=1 and po.poolAgreement=pp.poolAgreement");
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and pp.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		// 票据号码
		if (bean.getAssetNb() != null && !"".equals((bean.getAssetNb()))) {
			hql.append(" and po.assetNb like :assetNb");
			paramName.add("assetNb");
			paramValue.add("%" + bean.getAssetNb() + "%");
		}
		/********************融合改造新增 start******************************/
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}
		
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		
		/********************融合改造新增 end******************************/
		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia())) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())) {
			hql.append(" and po.assetType=:assetType");
			paramName.add("assetType");
			paramValue.add(bean.getAssetType());
		}

		// 企业组织机构代码plCommId
		if (bean.getAssetCommId() != null && !"".equals(bean.getAssetCommId())) {
			hql.append(" and po.assetCommId like :assetCommId");
			paramName.add("assetCommId");
			paramValue.add("%" + bean.getAssetCommId() + "%");
		}

		// 出票日期开始startplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and po.plIsseDt>=TO_DATE(:plstartDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plstartDt");
			paramValue.add(DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 出票日期结束endplIsseDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and po.plIsseDt<=TO_DATE(:plIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plIsseDt");
			paramValue.add(DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 到期日期开始startplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plDueDt>=TO_DATE(:plstartDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plstartDueDt");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日期结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plDueDt<=TO_DATE(:plDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plDueDt");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 是否已产生额度isEduExist
		if (bean.getIsEduExist() != null && !"".equals(bean.getIsEduExist())) {
			hql.append(" and po.isEduExist=:isEduExist");
			paramName.add("isEduExist");
			paramValue.add(bean.getIsEduExist());
			if("1".equals(bean.getIsEduExist())){
				hql.append(" and po.rickLevel !=:rickLevel2");//额度类型
				paramName.add("rickLevel2");
				paramValue.add(PoolComm.NOTIN_RISK);
			}
		}
		//状态
		if (bean.getAssetStatus() != null && !"".equals(bean.getAssetStatus())) {
			hql.append(" and po.assetStatus=:SDealStatus");//票据状态
			paramName.add("SDealStatus");
			paramValue.add(bean.getAssetStatus());
		}else{//没有状态走
			List status = new ArrayList();
			status.add(PoolComm.DS_02);
			status.add(PoolComm.DS_06);
			hql.append(" and po.assetStatus in (:SDealStatus)");//票据状态
			paramName.add("SDealStatus");
			paramValue.add(status);
		}
		// 额度类型rickLevel
		if (bean.getRickLevel() != null && !"".equals(bean.getRickLevel())) {
			hql.append(" and po.rickLevel=:rickLevel");
			paramName.add("rickLevel");
			paramValue.add(bean.getRickLevel());
		}
		if(StringUtil.isNotBlank(bean.getBlackFlag())){//黑名单标识
			hql.append(" and po.blackFlag=:blackFlag");
			paramName.add("blackFlag");
			paramValue.add(bean.getBlackFlag());
		}
		
		if(StringUtil.isNotBlank(bean.getPlAccptrSvcr())){//承兑行
			hql.append(" and po.plAccptrSvcr=:plAccptrSvcr");
			paramName.add("plAccptrSvcr");
			paramValue.add(bean.getPlAccptrSvcr());
		}
		
		if(null!= bean.getList() && bean.getList().size()>0){//承兑行列表
			hql.append(" and po.plAccptrSvcr in(:acptBankList)");
			paramName.add("acptBankList");
			paramValue.add(bean.getList());
		}

		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and po.assetAmt>=:startAmt");
			paramName.add("startAmt");
			paramValue.add(bean.getStartassetAmt());
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and po.assetAmt<=:assetAmt");
			paramName.add("assetAmt");
			paramValue.add(bean.getEndassetAmt());
		}
		//票据池编号
		if(bean.getPoolAgreement() !=  null && !"".equals(bean.getPoolAgreement())){
			hql.append(" and po.poolAgreement =:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		//核心客户号
		if(bean.getCustNo() !=  null && !"".equals(bean.getCustNo())){
			hql.append(" and po.custNo =:custNo");
			paramName.add("poolAgreement");
			paramValue.add(bean.getCustNo());
		}
		if (bean.getPoolName() != null && !"".equals(bean.getPoolName())) {
			hql.append(" and pp.poolName like :poolName");
			paramName.add("poolName");
			paramValue.add("%" + bean.getPoolName() + "%");
		}
		
		hql.append(" order by po.plDueDt desc");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		List retList = new ArrayList();
		if (res != null && res.size() > 0) {
			DraftQueryBeanQuery beanNew = null;
			DraftPool info = null;
			for (int i = 0; i < res.size(); i++) {
				Object[] obj = (Object[]) res.get(i);
				beanNew = new DraftQueryBeanQuery();
				info = (DraftPool) obj[0];
				BeanUtils.copyProperties(beanNew, info);
				PedProtocolDto ped= this.queryProtocolDtoBypoolAgreement(beanNew.getPoolAgreement());
				beanNew.setPoolName(ped.getPoolName());
				retList.add(beanNew);
			}
		}
		return retList;
	}
	/**
	 * 
	 */
	public PedProtocolDto queryProtocolDtoBypoolAgreement(String poolAgreement) throws Exception {

		PedProtocolDto ped = new PedProtocolDto();
		String sql = " from PedProtocolDto ap where ap.poolAgreement ='" + poolAgreement + "'";
		List result = this.find(sql);
		if (result != null && result.size() > 0) {
			ped = (PedProtocolDto) result.get(0);
		}
		return ped;

	}

	/**
	 * 计算票据产生额度
	 */
	public LimitVo queryDraftPoolQuerySumByAssetType(AssetType at,String riskLevel) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		String hql = "select new com.mingtech.application.pool.edu.domain.LimitVo(sum(assetLimitTotal),sum(assetLimitUsed),sum(assetLimitFree),sum(assetLimitFrzd),count(id)) from "
				+ " DraftPool dp where dp.rickLevel =:rickLevel and dp.at=:at and dp.assetStatus in(:assetStatus)  ";

		paramName.add("rickLevel");
		paramValue.add(riskLevel);

		paramName.add("at");
		paramValue.add(at.getId());

		List useStates = new ArrayList();
		useStates.add(PoolComm.DS_02);
//		useStates.add(PoolComm.PJC_PJYDQ);
		useStates.add(PoolComm.DS_06);
		paramName.add("assetStatus");
		paramValue.add(useStates);

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<LimitVo> result = this.find(hql, paramNames, paramValues);
		if (result != null && result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	/**
	 * 查找客户 的 池资产明细
	 * 
	 * @param bpsNo
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String findCustAseetPoolDetail(String bpsNo, Page page) throws Exception {

		String hql = "select at.astType,sum(at.crdtTotal),sum(at.crdtUsed),sum(at.crdtFree),sum(at.crdtFrzd) from AssetType at,AssetPool ap where at.apId=ap.apId and ap.bpsNo=?"
				+ " group by at.astType"; // 按 池资产类型 分类
		List param = new ArrayList();
		param.add(bpsNo);

		List tempres = this.find(hql, param, page);

		List res = new ArrayList();
		if (tempres != null && tempres.size() > 0) {
			for (int i = 0; i < tempres.size(); i++) {
				Object[] obj = (Object[]) tempres.get(i);
				AssetType assetType = new AssetType();
				if(obj[0] != null && !"".equals(obj[0])) {
					assetType.setAstType((String) obj[0]);
				}
				// 按照林丛林要求，先屏蔽掉定期保证金
				if (assetType.getAstType().equals(PoolComm.ED_BZJ_HQ)
						|| assetType.getAstType().equals(PoolComm.ED_PJC) || assetType.getAstType().equals(PoolComm.ED_PJC_01)) {
					if (obj[1] != null && !"".equals(obj[1])) {
						assetType.setCrdtTotal(new BigDecimal(obj[1].toString()));
					}
					if (obj[2] != null && !"".equals(obj[2])) {
						assetType.setCrdtUsed(new BigDecimal(obj[2].toString()));
					}
					if (obj[3] != null && !"".equals(obj[3])) {
						assetType.setCrdtFree(new BigDecimal(obj[3].toString()));
					}
					if (obj[4] != null && !"".equals(obj[4])) {
						assetType.setCrdtFrzd(new BigDecimal(obj[4].toString()));
					}
					res.add(assetType);
				}

			}
		}

		page.setTotalCount(res.size());
		int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
		int endIndex = (page.getPageIndex() * page.getPageSize()) > res.size() ? res.size()
				: page.getPageIndex() * page.getPageSize();
		List returnBatchesList = new ArrayList(); // 本次需要显示的批次
		returnBatchesList.addAll(res.subList(startIndex, endIndex));
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(returnBatchesList, map);
	}

	/**
	 * 企业额度查询只展示总部的：修改hql，加了一个条件 and cd.groupFlag ='01'
	 */
	public String findCustEduJson(String poolAgreement, User user, Page page) throws Exception {
		StringBuffer hql = new StringBuffer();

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		hql.append(
				"select pp.poolAgreement,pp.poolName,sum(at.crdtTotal),sum(at.crdtFree),sum(at.crdtUsed),sum(at.crdtFrzd) ,ap.poolType,pp.creditamount,pp.poolName,pp.custnumber ");
		hql.append(
				" from AssetType at,AssetPool ap ,PedProtocolDto pp where ap.apId=at.apId and ap.custNo=pp.custnumber and pp.openFlag='01' and pp.isGroup ='0' ");
		if (StringUtil.isNotEmpty(poolAgreement)) {
			hql.append(" and pp.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(poolAgreement);
		}

		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and pp.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		hql.append(" group by pp.creditamount,ap.poolType,ap.custOrgcode,pp.poolAgreement,pp.poolName,pp.custnumber");

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List res = this.find(hql.toString(), paramNames, paramValues);
		List resultDto = new ArrayList();
		if (res != null && res.size() > 0) {
			CustEduQuery custEdu = null;
			for (int i = 0; i < res.size(); i++) {
				custEdu = new CustEduQuery();
				Object[] obj = (Object[]) res.get(i);

				custEdu.setPlCommId((String) obj[0]);// 票据池编号
				custEdu.setCustName((String) obj[1]);// 票据池名称
				if (obj[2] != null && !"".equals(obj[2])) {// 总额度（客户高低风险保证金所有额度之和）
					custEdu.setLowerTotalEdu(new BigDecimal(obj[2].toString()));
				}

				if (obj[3] != null && !"".equals(obj[3])) {// 所有可用额度
					custEdu.setBdCrdtFree(new BigDecimal(obj[3].toString()));
				}
				if (obj[4] != null && !"".equals(obj[4])) {// 所有已用额度
					custEdu.setBdCrdtUsed(new BigDecimal(obj[4].toString()));
				}
				if (obj[5] != null && !"".equals(obj[5])) {// 所有冻结额度
					custEdu.setCrdtFrzd(new BigDecimal(obj[5].toString()));
				}

				BigDecimal riskLowCreditTotalAmount = null;// 低风险担保合同限额
				if (obj[7] != null && !"".equals(obj[7])) {
					riskLowCreditTotalAmount = new BigDecimal(obj[7].toString());
				} else {
					riskLowCreditTotalAmount = new BigDecimal("0.0");
				}
				custEdu.setRiskLowCreditTotalAmount(riskLowCreditTotalAmount);

				BigDecimal riskLowCreditUsedAmount = null;// 低风险担保合同已用额度
				if (obj[4] != null && !"".equals(obj[4])) {// 所有已用额度
					riskLowCreditUsedAmount = new BigDecimal(obj[4].toString());
				} else {
					riskLowCreditUsedAmount = new BigDecimal("0.0");
				}
				custEdu.setRiskLowCreditUsedAmount(riskLowCreditUsedAmount);
				custEdu.setRiskLowCreditFreeAmount(riskLowCreditTotalAmount.subtract(riskLowCreditUsedAmount));

				// liuxiaodong add
				if (obj[9] != null && !"".equals(obj[9])) {
					EduResult eduResult = pedAssetPoolService.queryEduAll((String) obj[0]);
					
					/*****注意：该部分字段只有此界面显示这样赋值****/
					
					//界面：总额度
					custEdu.setLowerTotalEdu(eduResult.getLowRiskAmount().add(eduResult.getBailAmountTotail()));// 总额度
					//界面：已用额度
					custEdu.setUsedLowRiskAmount(eduResult.getUsedLowRiskAmount().add(eduResult.getBailAmountUsed()));// 已用低风险额度=低风险票据已用额度+保证金已用额度
					//界面：可用额度
					custEdu.setFreeLowRiskAmount(eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount()));// 可用额度=低风险票据可用额度+保证金可用额度
					
					custEdu.setLowerEffTotalEdu(eduResult.getLowRiskAmount());// 低风险票据总额度
					custEdu.setHeightEffTotalEdu(eduResult.getHighRiskAmount());// 高风险票据额度
					custEdu.setUsedHighRiskAmount(eduResult.getUsedHighRiskAmount());// 已用高风险额度
					custEdu.setFreeHighRiskAmount(eduResult.getFreeHighRiskAmount());// 未用高风险额度
					custEdu.setZeroEduAmount(eduResult.getZeroEduAmount());// 未产生额度票据总金额
					custEdu.setBailAmountTotal(eduResult.getBailAmountTotail());// 保证金总额-----
					custEdu.setBailAmount(eduResult.getBailAmount());// 保证金余额
					custEdu.setCustnumber((String) obj[9]);// 客户号
				}
				if (((String) obj[6]).equals("ZC_01")) {
					custEdu.setPoolType("票据池");
				} else {
					custEdu.setPoolType("集团票据池");
				}

				resultDto.add(custEdu);
			}
		}

		page.setTotalCount(resultDto.size());
		int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
		int endIndex = (page.getPageIndex() * page.getPageSize()) > resultDto.size() ? resultDto.size()
				: page.getPageIndex() * page.getPageSize();
		List returnBatchesList = new ArrayList(); // 本次需要显示的批次
		returnBatchesList.addAll(resultDto.subList(startIndex, endIndex));
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(returnBatchesList, map);
	}
	
	/**
	 * 企业额度查询只展示总部的：修改hql，加了一个条件 and cd.groupFlag ='01'
	 */

	public String findCustEduQuotaJson(String poolAgreement,String isGroup, String poolName, User user, Page page) throws Exception {
		StringBuffer hql = new StringBuffer();

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		hql.append(
				"select pp.poolAgreement,pp.poolName,sum(at.crdtTotal),sum(at.crdtFree),sum(at.crdtUsed),sum(at.crdtFrzd) ,ap.poolType,pp.creditamount,pp.poolName,pp.custnumber,pp.isGroup,ap.custOrgcode,pp.poolMode ");
		hql.append(
				" from AssetType at,AssetPool ap ,PedProtocolDto pp where ap.apId=at.apId and ap.custNo=pp.custnumber and ap.bpsNo = pp.poolAgreement and pp.openFlag='01' ");
		if (StringUtil.isNotEmpty(poolAgreement)) {
			hql.append(" and pp.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(poolAgreement);
		}
		if (StringUtil.isNotEmpty(isGroup)) {
			hql.append(" and pp.isGroup=:isGroup");
			paramName.add("isGroup");
			paramValue.add(isGroup);
		}
		if (StringUtil.isNotEmpty(isGroup)) {
			hql.append(" and pp.isGroup=:isGroup");
			paramName.add("isGroup");
			paramValue.add(isGroup);
		}
		if (StringUtil.isNotEmpty(poolName)) {
			hql.append(" and pp.poolName like:poolName");
			paramName.add("poolName");
			paramValue.add("%"+poolName+"%");
		}
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and pp.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		hql.append(" group by pp.creditamount,ap.poolType,ap.custOrgcode,pp.poolAgreement,pp.poolName,pp.custnumber,pp.isGroup,pp.poolMode");

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List res = this.find(hql.toString(), paramNames, paramValues);
		List resultDto = new ArrayList();
		if (res != null && res.size() > 0) {
			CustEduQuery custEdu = null;
			for (int i = 0; i < res.size(); i++) {
				custEdu = new CustEduQuery();
				Object[] obj = (Object[]) res.get(i);

				custEdu.setPlCommId((String) obj[0]);// 票据池编号
				custEdu.setCustName((String) obj[1]);// 票据池名称
				if (obj[2] != null && !"".equals(obj[2])) {// 总额度（客户高低风险保证金所有额度之和）
					custEdu.setLowerTotalEdu(new BigDecimal(obj[2].toString()));
				}

				if (obj[3] != null && !"".equals(obj[3])) {// 所有可用额度
					custEdu.setBdCrdtFree(new BigDecimal(obj[3].toString()));
				}
				if (obj[4] != null && !"".equals(obj[4])) {// 所有已用额度
					custEdu.setBdCrdtUsed(new BigDecimal(obj[4].toString()));
				}
				if (obj[5] != null && !"".equals(obj[5])) {// 所有冻结额度
					custEdu.setCrdtFrzd(new BigDecimal(obj[5].toString()));
				}

				BigDecimal riskLowCreditTotalAmount = null;// 低风险担保合同限额
				if (obj[7] != null && !"".equals(obj[7])) {
					riskLowCreditTotalAmount = new BigDecimal(obj[7].toString());
				} else {
					riskLowCreditTotalAmount = new BigDecimal("0.0");
				}
				custEdu.setRiskLowCreditTotalAmount(riskLowCreditTotalAmount);

				BigDecimal riskLowCreditUsedAmount = null;// 低风险担保合同已用额度
				if (obj[4] != null && !"".equals(obj[4])) {// 所有已用额度
					riskLowCreditUsedAmount = new BigDecimal(obj[4].toString());
				} else {
					riskLowCreditUsedAmount = new BigDecimal("0.0");
				}
				custEdu.setRiskLowCreditUsedAmount(riskLowCreditUsedAmount);
				custEdu.setRiskLowCreditFreeAmount(riskLowCreditTotalAmount.subtract(riskLowCreditUsedAmount));

				// liuxiaodong add
				if (obj[9] != null && !"".equals(obj[9])) {
					EduResult eduResult = pedAssetPoolService.queryEduAll((String) obj[0]);
					
					/*****注意：该部分字段只有此界面显示这样赋值****/
					
					//界面：总额度
					custEdu.setLowerTotalEdu(eduResult.getLowRiskAmount().add(eduResult.getBailAmountTotail()));// 低风险总额度
					//界面：已用额度
					custEdu.setUsedLowRiskAmount(eduResult.getUsedLowRiskAmount().add(eduResult.getBailAmountUsed()));// 已用低风险额度=低风险票据已用额度+保证金已用额度
					//界面：可用额度
					custEdu.setFreeLowRiskAmount(eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount()));// 可用额度=低风险票据可用额度+保证金可用额度
					
					custEdu.setLowerEffTotalEdu(eduResult.getLowRiskAmount());// 低风险票据总额度
					custEdu.setHeightEffTotalEdu(eduResult.getHighRiskAmount());// 高风险票据额度
					custEdu.setUsedHighRiskAmount(eduResult.getUsedHighRiskAmount());// 已用高风险额度
					custEdu.setFreeHighRiskAmount(eduResult.getFreeHighRiskAmount());// 未用高风险额度
					custEdu.setZeroEduAmount(eduResult.getZeroEduAmount());// 未产生额度票据总金额
					custEdu.setBailAmountTotal(eduResult.getBailAmountTotail());// 保证金总额-----
					custEdu.setBailAmount(eduResult.getBailAmount());// 保证金余额
					custEdu.setCustnumber((String) obj[9]);// 客户号
				}
				if (((String) obj[6]).equals(PoolComm.ZCC_PJC)) {
					custEdu.setPoolType("票据池");
				} else {
					custEdu.setPoolType("集团票据池");
				}
				if (obj[10] != null && !"".equals(obj[10])) {
					custEdu.setIsGroup((String) obj[10]);//是否集团
				}
				if (obj[11] != null && !"".equals(obj[11])) {
					custEdu.setCustOrgcode((String) obj[11]);//组织机构代码
				}
				if (obj[12] != null && !"".equals(obj[12])) {
					custEdu.setPoolMode((String) obj[12]);//额度模式
				}
				resultDto.add(custEdu);
			}
		}

		page.setTotalCount(resultDto.size());
		int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
		int endIndex = (page.getPageIndex() * page.getPageSize()) > resultDto.size() ? resultDto.size()
				: page.getPageIndex() * page.getPageSize();
		List returnBatchesList = new ArrayList(); // 本次需要显示的批次
		returnBatchesList.addAll(resultDto.subList(startIndex, endIndex));
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(returnBatchesList, map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingtech.application.pool.draft.service.DraftPoolQueryService#
	 * findCustEduJsonExpt(java.lang.String,
	 * com.mingtech.application.sysmanage.domain.User,
	 * com.mingtech.framework.core.page.Page)
	 */
	public List findCustEduJsonExpt(String poolAgreement,String isGroup, User currentUser, Page page) throws Exception {
		StringBuffer hql = new StringBuffer();
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		hql.append(
				"select pp.poolAgreement,pp.poolName,sum(at.crdtTotal),sum(at.crdtFree),sum(at.crdtUsed),sum(at.crdtFrzd) ,ap.poolType,pp.creditamount,pp.poolName,pp.custnumber,ap.custOrgcode,pp.poolMode ");
		hql.append(
				" from AssetType at,AssetPool ap ,PedProtocolDto pp where ap.apId=at.apId and ap.custNo=pp.custnumber and pp.openFlag='01' ");
		if (StringUtil.isNotEmpty(poolAgreement)) {
			hql.append(" and pp.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(poolAgreement);
		}
		if (StringUtil.isNotEmpty(isGroup)) {
			hql.append(" and pp.isGroup=:isGroup");
			paramName.add("isGroup");
			paramValue.add(isGroup);
		}
		// 增加机构筛选条件
				if (currentUser != null && currentUser.getDepartment() != null) {
					// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
					if (!PublicStaticDefineTab.isRootDepartment(currentUser)) {
						List resultList = departmentService.getAllChildrenInnerCodeList(currentUser.getDepartment().getInnerBankCode(), -1);
						hql.append(" and pp.officeNet in (:officeNet) ");
						paramName.add("officeNet");
						paramValue.add(resultList);
					}
				}
		hql.append(" group by pp.creditamount,ap.poolType,ap.custOrgcode,pp.poolAgreement,pp.poolName,pp.custnumber,pp.custOrgcode,pp.poolMode");

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List res = this.find(hql.toString(), paramNames, paramValues);
		List resultList = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[16];
				Object[] obj = (Object[]) res.get(i);
				//s[0] poolAgreement", "票据池编号"
				s[0] = (String) obj[0];
				//s[1] custName", "票据池名称"
				s[1] = (String) obj[1];
				//s[2] custnumber", "核心客户号"
				s[2] = (String) obj[9];
				//s[3]  custOrgcode", "组织机构代码"
				s[3] = (String) obj[10];
				if (obj[2] != null && !"".equals(obj[2])) {// 总额度（客户高低风险保证金所有额度之和）
					// s[4]  "lowerTotalEdu", "低风险总额度"
					s[5] = String.valueOf(obj[2]);
				} else {
					// s[4]  "lowerTotalEdu", "低风险总额度"
					s[5] = new BigDecimal("0.00").toString();
				}
				//s[11] "riskLowCreditTotalAmount", "担保合同限额"
				BigDecimal riskLowCreditTotalAmount = null;
				if (obj[7] != null && !"".equals(obj[7])) {
					s[12] = String.valueOf(obj[7]);
					riskLowCreditTotalAmount = new BigDecimal(s[12]);
				} else {
					s[12] = "0.00";
					riskLowCreditTotalAmount = new BigDecimal("0");
				}
				//s[12] "riskLowCreditUsedAmount", "担保合同已用额度"
				BigDecimal riskLowCreditUsedAmount = null;
				if (obj[4] != null && !"".equals(obj[4])) {// 所有已用额度
					s[13] = String.valueOf(obj[4]);
					riskLowCreditUsedAmount = new BigDecimal(s[13]);
				} else {
					s[13] = "0.00";
					riskLowCreditUsedAmount = new BigDecimal("0");
				}
				//s[13] "riskLowCreditFreeAmount", "担保合同剩余额度"
				s[14] = String.valueOf(riskLowCreditTotalAmount.subtract(riskLowCreditUsedAmount));

				// liuxiaodong add
				if (obj[9] != null && !"".equals(obj[9])) {
					EduResult eduResult = pedAssetPoolService.queryEduAll((String) obj[0]);
					//s[4] "lowerTotalEdu", "低风险总额度"
					s[5] = String.valueOf(eduResult.getLowRiskAmount().add(eduResult.getBailAmountTotail()));
					//s[5] lowerEffTotalEdu", "低风险票据额度
					s[6] = String.valueOf(eduResult.getLowRiskAmount());
					//s[6] "usedLowRiskAmount", "低风险已用额度" 低风险已用额度（已用保证金额度 +已用低风险票额度）
					s[7] = String.valueOf(eduResult.getUsedLowRiskAmount().add(eduResult.getBailAmountUsed()));
					//s[7] "freeLowRiskAmount", "低风险可用额度" 低风险可用额度（可用保证金额度 + 可用低风险票额度）
					s[8] = String.valueOf(eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount()));
					//s[8] "heightEffTotalEdu", "高风险额度"
					s[9] = String.valueOf(eduResult.getHighRiskAmount());
					//s[9] "usedHighRiskAmount", "已用高风险额度"
					s[10] = String.valueOf(eduResult.getUsedHighRiskAmount());
					//s[10] "freeHighRiskAmount", "未用高风险额度"
					s[11] = String.valueOf(eduResult.getFreeHighRiskAmount());
					//s[14] "bailAmountTotal", "保证金额度"
					s[15] = String.valueOf(eduResult.getBailAmountTotail());
				}
				if("01".equals((String) obj[11])){
					s[4] ="总量模式";
				}else if("02".equals((String) obj[11])){
					s[4] ="期限配比";
				}
				resultList.add(s);
			}
		}
		return resultList;
	}

	public boolean getIfRoleUser(User roleUser) throws Exception {
		GenericHibernateDao genericHibernateDao = new GenericHibernateDao();
		String detpId = roleUser.getDepartment().getId();
		StringBuffer sb = new StringBuffer();
		List returnList = new ArrayList();
		boolean flag = false;
		String role_xqy = ProjectConfig.getInstance().getkeyValue("role_gly");// 管理员权限，

		sb.append("select  u.*  from  t_user u " + " left join  T_USER_ROLE  ur on u.id=ur.userid"
				+ " left join T_ROLE r on r.id=ur.roleid  where  u.id='" + roleUser.getId() + "'  and r.id in ("
				+ role_xqy + ")");

		// returnList = sessionDao.SQLQuery(sb.toString());
		returnList = this.find(sb.toString());

		if (returnList.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 
	 */

	public String findAssetDetailList(String plCommId, String astType, Page page) throws Exception {
		List res = new ArrayList();
		List total = new ArrayList();
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		if (PoolComm.ED_PJC.equals(astType)) {
			String hql = "from DraftPool dp where dp.assetStatus in (:status) and dp.assetCommId=:plCommId and dp.productId=:productId and dp.rickLevel=:rickLevel ";
			String thql = "select sum(dp.assetAmt),count(dp.id) from DraftPool dp where dp.assetStatus in (:status) and dp.assetCommId=:plCommId and dp.productId=:productId and dp.rickLevel=:rickLevel ";
			List status = new ArrayList(); // 票据状态
			status.add(PoolComm.DS_02); // 已入池 状态
			// status.add(PoolComm.PJC_CCSQ); //出池申请
//			status.add(PoolComm.PJC_PJYDQ);   //票据已到期
//			status.add(PoolComm.DS_02);   //票据已发托
//			status.add(PoolComm.PJC_TSJF);   //托收拒付
			paramName.add("status");
			paramValue.add(status);
			paramName.add("plCommId");
			paramValue.add(plCommId);
			paramName.add("rickLevel");
			paramValue.add(PoolComm.LOW_RISK);
			paramName.add("productId");
			paramValue.add(PoolComm.PRODUCT_TYPE_RC);
			String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
			Object paramValues[] = paramValue.toArray();

			res = this.find(hql, paramNames, paramValues, page);

			// 总笔数、总金额
			total = this.find(thql, paramNames, paramValues);
//			if(total != null && total.size()>0 && res != null && res.size()>0){
//				Object[] obj = (Object[]) total.get(0);
//				if(obj[0] != null){
//					((DraftPool)res.get(0)).setTotalAmt(String.valueOf(obj[0]));
//				}
//				if(obj[1] != null){
//					((DraftPool)res.get(0)).setTotalEdu(String.valueOf(obj[1]));
//				}
//				if(obj[2] != null){
//					((DraftPool)res.get(0)).setTotalNum(String.valueOf(obj[2]));
//				}
//			}
		} else if (PoolComm.ED_CDP.equals(astType)) {
			String hql = "from DraftPool dp where dp.assetStatus in (:status) and dp.assetCommId=:plCommId  and dp.productId=:productId ";

			String thql = "select sum(dp.assetAmt),count(dp.id) from DraftPool dp where dp.assetStatus in (:status) and dp.assetCommId=:plCommId and dp.productId=:productId ";
			List status = new ArrayList(); // 票据状态
			status.add(PoolComm.DS_02); // 已入池 状态
			// status.add(PoolComm.PJC_CCSQ); //出池申请
//			status.add(PoolComm.PJC_PJYDQ);   //票据已到期
			status.add(PoolComm.DS_06); // 票据已发托
//			status.add(PoolComm.PJC_TSJF);   //托收拒付
			paramName.add("status");
			paramValue.add(status);

			paramName.add("plCommId");
			paramValue.add(plCommId);

			String productId = "2000111";
			paramName.add("productId");
			paramValue.add(productId);

			String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
			Object paramValues[] = paramValue.toArray();

			res = this.find(hql, paramNames, paramValues, page);

			// 总笔数、总金额
			total = this.find(thql, paramNames, paramValues);
//			if(total != null && total.size()>0 && res != null && res.size()>0){
//				Object[] obj = (Object[]) total.get(0);
//				if(obj[0] != null){
//					((DraftPool)res.get(0)).setTotalAmt(String.valueOf(obj[0]));
//				}
//				if(obj[1] != null){
//					((DraftPool)res.get(0)).setTotalEdu(String.valueOf(obj[1]));
//				}
//				if(obj[2] != null){
//					((DraftPool)res.get(0)).setTotalNum(String.valueOf(obj[2]));
//				}
//			}
		}

		page.setResult(res);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(res, map);
	}

	public String findAssetBailDetailList(String plCommId, String astType, Page page) throws Exception {
		List tempres = new ArrayList();

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		if (PoolComm.ED_BZJ_HQ.equals(astType) || PoolComm.ED_BZJ_DQ.equals(astType)) {
			String hql = "select bd from BailDetail bd,AssetType at,AssetPool ap where "
					+ " bd.at=at.id and at.apId=ap.apId and ap.custOrgcode=:plCommId and at.astType=:astType "
					+ " and bd.assetStatus='BDS_01'";

			paramName.add("plCommId");
			paramValue.add(plCommId);

			paramName.add("astType");
			paramValue.add(astType);

			String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
			Object paramValues[] = paramValue.toArray();

			tempres = this.find(hql, paramNames, paramValues, page);
		}
		return JsonUtil.buildJson(tempres, tempres.size());
	}

	// 批量导出
	// 池票据查询 按户查询 批量导出
	public List findPoolDraftExpt(List res, Page page) throws Exception {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			DraftQueryBeanQuery pool = null;
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[15];
				pool = (DraftQueryBeanQuery) res.get(i);
				s[0] = pool.getPoolAgreement();
				s[1] = pool.getPoolName();
				s[2] = pool.getAssetNb();
				s[3] = this.reBillMediaName(pool.getPlDraftMedia());
				s[4] = this.reBillTypeName(pool.getAssetType());
				s[5] = this.reAssetStatusName(pool.getAssetStatus());
				s[6] = String.valueOf(pool.getAssetAmt());
				if (pool.getPlIsseDt() != null) {
					s[7] = String.valueOf(pool.getPlIsseDt()).substring(0, 10);
				}else{
					s[7] = "";
				}
				if (pool.getPlDueDt() != null) {
					s[8] = String.valueOf(pool.getPlDueDt()).substring(0, 10);
				}else {
					s[8] = "";
				}
				s[9] = pool.getPlDrwrNm();
				s[10] = pool.getPlDrwrAcctSvcrNm();
				s[11] = pool.getPlPyeeNm();
				s[12] = pool.getPlPyeeAcctSvcrNm();
				s[13] = pool.getIsEduExistName();
				s[14] = pool.getRickLevelName();
				list.add(s);
			}
		}
		return list;
	}

	// 设置 票据类型
	private String reBillTypeName(String value) {
		if (value != null) {
			if (value.equals("AC01")) {
				return "银票";
			} else if (value.equals("AC02")) {
				return "商票";
			}
		}
		return "";
	}

	// 取票据介质中文名
	private String reBillMediaName(String value) {
		if (value != null) {
			if (value.equals("1")) {
				return "纸质";
			} else if (value.equals("2")) {
				return "电子";
			}
		}
		return "";
	}

	// 取票据权益中文名
	private String rePayTypeName(String value) {
		if (value != null) {
			if (value.equals("QY_01")) {
				return "应收票据";
			} else if (value.equals("QY_02")) {
				return "应付票据";
			}
		}
		return "";
	}

	// 取票据状态中文名
	private String reBillStatus(String plStatus) {
		if (plStatus != null) {
			return PoolDictionaryCache.getFromPoolDictMap(plStatus);
		}
		return "";
	}
	public List findStorageDraftExpt(List res) throws Exception {

		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[12];
				DraftStorage draftStorage = (DraftStorage) res.get(i);
				s[0] = draftStorage.getPlDraftNb();
				s[1] = draftStorage.getPlRecSvcrNm();
				s[2] = this.reBillTypeName(draftStorage.getPlDraftType());
				s[3] = this.reBillMediaName(draftStorage.getPlDraftMedia());
				if (draftStorage.getPlDueDt() != null) {
					s[4] = String.valueOf(draftStorage.getPlDueDt()).substring(0, 10);
				}
				if (draftStorage.getPlTm() != null) {
					s[5] = String.valueOf(draftStorage.getPlTm()).substring(0, 10);
				}
				s[6] = draftStorage.getPlApplyNm();
				s[7] = String.valueOf(draftStorage.getPlIsseAmt());
				s[8] = draftStorage.getPlAccptrNm();
				if (null != draftStorage.getTotalCharge()) {
					s[9] = String.valueOf(draftStorage.getTotalCharge());
				}
				if (null != draftStorage.getChargeFlag() && !"".equals(draftStorage.getChargeFlag())) {
					if (1 == Integer.parseInt(draftStorage.getPlStatus().toString())) {
						s[10] = "扣费成功";
					} else if (0 == Integer.parseInt(draftStorage.getChargeFlag().toString())) {
						s[10] = "扣费失败";
					}
				}

				if (null != draftStorage.getPlStatus()) {
					s[11] = String.valueOf(this.reBillStatus(draftStorage.getPlStatus().toString()));
				}
				list.add(s);
			}
		}
		return list;
	}

	public String findCrdtProductListByCust(String custName, String custNo, String crdtType, String sttlFlag,
			String crdtStatus, Date aplyDtStart, Date aplyDtEnd, Date dueDtStart, Date dueDtEnd, BigDecimal startAmount,
			BigDecimal endAmount, String selRange, String bankNos, String selBankNO, String crdtNo, Page page,
			User user) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer dhql = new StringBuffer();
		StringBuffer hql = new StringBuffer();
		dhql.append(
				"select cp.custName,cp.crdtBankName,sum(cp.crdtAmt),sum(cp.useAmt),count(cp.Id) from CreditProduct cp where 1=1");

		// 申请人名称
		if (custName != null && !"".equals(custName)) {
			hql.append(" and cp.custName like :custName");
			paramName.add("custName");
			paramValue.add("%" + custName + "%");
		}

		// 申请人组织机构代码custNo
		if (custNo != null && !"".equals(custNo)) {
			hql.append(" and cp.custNo like :custNo");
			paramName.add("custNo");
			paramValue.add("%" + custNo + "%");
		}

		// 信贷产品类型
		if (crdtType != null && !"".equals(crdtType)) {
			hql.append(" and cp.crdtType=:crdtType");
			paramName.add("crdtType");
			paramValue.add(crdtType);
		}

		// 信贷业务号
		if (crdtNo != null && !"".equals(crdtNo)) {
			hql.append(" and cp.crdtNo like :crdtNo");
			paramName.add("crdtNo");
			paramValue.add("%" + crdtNo + "%");
		}

		// 结清标记
		if (sttlFlag != null && !"".equals(sttlFlag)) {
			hql.append(" and cp.sttlFlag=:sttlFlag");
			paramName.add("sttlFlag");
			paramValue.add(sttlFlag);
		}

		// 业务状态
		if (crdtStatus != null && !"".equals(crdtStatus)) {
			hql.append(" and cp.crdtStatus=:crdtStatus");
			paramName.add("crdtStatus");
			paramValue.add(crdtStatus);
		}

		// 申请日期开始
		if (aplyDtStart != null && !"".equals(aplyDtStart)) {
			hql.append(" and cp.crdtIssDt>=TO_DATE(:aplyDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtStart");
			paramValue.add(DateUtils.toString(aplyDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 申请日期结束
		if (aplyDtEnd != null && !"".equals(aplyDtEnd)) {
			hql.append(" and cp.crdtIssDt<=TO_DATE(:aplyDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtEnd");
			paramValue.add(DateUtils.toString(aplyDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 到期日开始
		if (dueDtStart != null && !"".equals(dueDtStart)) {
			hql.append(" and cp.crdtDueDt>=TO_DATE(:dueDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtStart");
			paramValue.add(DateUtils.toString(dueDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日结束
		if (dueDtEnd != null && !"".equals(dueDtEnd)) {
			hql.append(" and cp.crdtDueDt<=TO_DATE(:dueDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtEnd");
			paramValue.add(DateUtils.toString(dueDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 信贷金额开始
		if (startAmount != null && !"".equals(startAmount)) {
			hql.append(" and cp.crdtAmt>=:startAmount");
			paramName.add("startAmount");
			paramValue.add(startAmount);
		}
		// 信贷金额结束
		if (endAmount != null && !"".equals(endAmount)) {
			hql.append(" and cp.crdtAmt<=:endAmount");
			paramName.add("endAmount");
			paramValue.add(endAmount);
		}
		// 查询范围
		if (selRange.equals("1")) {
			// 机构树的选择
			if (bankNos != null && bankNos.trim().length() > 0) {
				String[] sbank = (bankNos.substring(0, bankNos.length() - 1)).split(",");
				List dept = new ArrayList();
				for (int i = 0; i < sbank.length; i++) {
					dept.add(sbank[i]);
				}
				hql.append(" and cp.crdtBankCode in(:crdtBankCode)");
				paramName.add("crdtBankCode");
				paramValue.add(dept);
			} else {
				List departmentList = new ArrayList();

//				departmentService.getAllChildrenBankCodeList(departmentList,selBankNO, -1);// 20120806 yangyawei  下属机构信息不给本机构开放（查询下级机构去信贷系统查询 ）
				departmentList.add(selBankNO);
				// 设置机构
				hql.append(" and cp.crdtBankCode in(:crdtBankCode)");
				paramName.add("crdtBankCode");
				paramValue.add(departmentList);

			}
		} else if (selRange.equals("1")) { // 本机构
			// 登录的用户
			hql.append(" and cp.crdtBankCode =:crdtBankCode");
			paramName.add("crdtBankCode");
			paramValue.add(selBankNO);
		} else if (selRange.equals("2")) { // 下辖
			List departmentList = new ArrayList();
			departmentService.getAllChildrenBankCodeList(departmentList, selBankNO, -1);
			departmentList.remove(selBankNO);
			if (departmentList.size() == 0) {
				departmentList.add("");
			}
			hql.append(" and cp.crdtBankCode in(:crdtBankCode)");
			paramName.add("crdtBankCode");
			paramValue.add(departmentList);
		}

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		// 总笔数总金额
		StringBuffer sumhql = new StringBuffer();
		sumhql.append("select count(cp.Id),sum(cp.crdtAmt) from CreditProduct cp where 1=1 ");
		sumhql.append(hql);

		String sumMoney = null;
		String sumNum = null;
		List sumList = this.find(sumhql.toString(), paramNames, paramValues);
		if (sumList != null && sumList.size() > 0) {
			Object[] obj = (Object[]) sumList.get(0);
			if (obj[0] != null) {
				sumNum = String.valueOf(obj[0]);
			}
			if (obj[1] != null) {
				sumMoney = String.valueOf(obj[1]);
			}

		}
		StringBuffer hqlCus = new StringBuffer();

		String totalCust = "";
		hqlCus.append("select count(cp.custNo) from CreditProduct cp where 1=1");
		hqlCus.append(hql);
		hqlCus.append(" group by cp.custNo");
		List total = this.find(hqlCus.toString(), paramNames, paramValues);
		if (total != null && total.size() > 0) {
			totalCust = String.valueOf(total.get(0));
		}

		dhql.append(hql);
		dhql.append(" group by cp.custName,cp.crdtBankName");
		List res = this.find(dhql.toString(), paramNames, paramValues);
		List palist = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				CreditPrdtQuery cpq = new CreditPrdtQuery();
				Object[] obj = (Object[]) res.get(i);
				cpq.setCustName((String) obj[0]);
				cpq.setCrdtBankName((String) obj[1]);
				if (obj[2] != null) {
					cpq.setCrdtAmt(new BigDecimal(String.valueOf(obj[2])));
				}
				if (obj[3] != null) {
					cpq.setUseAmt(new BigDecimal(String.valueOf(obj[3])));
				}
				if (obj[4] != null) {
					cpq.setCutalCnt(String.valueOf(obj[4]));
				}

				palist.add(cpq);
			}
		}
		if (palist != null && palist.size() > 0) {
			((CreditPrdtQuery) palist.get(0)).setSumNum(sumNum);
			((CreditPrdtQuery) palist.get(0)).setSumMoney(sumMoney);
			((CreditPrdtQuery) palist.get(0)).setTotalCust(totalCust);

		}

		page.setTotalCount(palist.size());
		int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
		int endIndex = (page.getPageIndex() * page.getPageSize()) > palist.size() ? palist.size()
				: page.getPageIndex() * page.getPageSize();
		List returnBatchesList = new ArrayList(); // 本次需要显示的批次
		returnBatchesList.addAll(palist.subList(startIndex, endIndex));
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(returnBatchesList, map);
	}

	public String findCrdtProductList(CreditProductQueryBean bean, Page page, User user) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer dhql = new StringBuffer();

		StringBuffer hql = new StringBuffer();
		dhql.append(
				"from PedProtocolDto dto,CreditProduct cp,PedCreditDetail pcd where 1=1  and cp.bpsNo = dto.poolAgreement and cp.crdtNo=pcd.crdtNo");

		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and dto.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		// 风险等级
		if (StringUtil.isNotEmpty(bean.getRisklevel())) {
			hql.append(" and cp.risklevel =:risklevel");
			paramName.add("risklevel");
			paramValue.add(bean.getRisklevel());
		}		
		
		// 客户号
		if (StringUtil.isNotEmpty(bean.getCustnumber())) {
			hql.append(" and dto.custnumber = :custnumber");
			paramName.add("custnumber");
			paramValue.add(bean.getCustnumber());
		}

		// 融资人名称
		if (StringUtil.isNotEmpty(bean.getCustName())) {
			hql.append(" and cp.custName like :custName");
			paramName.add("custName");
			paramValue.add("%" + bean.getCustName() + "%");
		}

		// 业务品种
		if (StringUtil.isNotEmpty(bean.getCrdtType())) {
			hql.append(" and pcd.loanType=:crdtType");
			paramName.add("crdtType");
			paramValue.add(bean.getCrdtType());
		}

		// 合同号
		if (StringUtil.isNotEmpty(bean.getCrdtNo())) {
			hql.append(" and cp.crdtNo like :crdtNo");
			paramName.add("crdtNo");
			paramValue.add("%" + bean.getCrdtNo() + "%");
		}

		// 结清标记
		if (StringUtil.isNotEmpty(bean.getSttlFlag())) {
			hql.append(" and cp.sttlFlag=:sttlFlag");
			paramName.add("sttlFlag");
			paramValue.add(bean.getSttlFlag());
		}

		// 业务状态
		if (StringUtil.isNotEmpty(bean.getCrdtStatus())) {
			hql.append(" and pcd.loanStatus = :loanStatus");
			paramName.add("loanStatus");
			paramValue.add(bean.getCrdtStatus());
		}

		// 合同起始日期
		if (bean.getStartCrdtIssDt() != null) {
			hql.append(" and cp.crdtIssDt>=:aplyDtStart1");
			paramName.add("aplyDtStart1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartCrdtIssDt()));
		}
		// 合同起始日期
		if (bean.getEndCrdtIssDt() != null) {
			hql.append(" and cp.crdtIssDt<=:aplyDtStart2");
			paramName.add("aplyDtStart2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndCrdtIssDt()));
		}
		// 合同到期日期
		if (bean.getStartCrdtDueDt() != null) {
			hql.append(" and cp.crdtDueDt>=:aplyDtEnd1");
			paramName.add("aplyDtEnd1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartCrdtDueDt()));
		}
		// 合同到期日期
		if (bean.getEndCrdtDueDt() != null) {
			hql.append(" and cp.crdtDueDt<=:aplyDtEnd2");
			paramName.add("aplyDtEnd2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndCrdtDueDt()));
		}
		// 借据号
		if (StringUtil.isNotEmpty(bean.getLoanNo())) {
			hql.append(" and pcd.loanNo like :loanNo");
			paramName.add("loanNo");
			paramValue.add("%" + bean.getLoanNo() + "%");
		}
		// 借据起始日期
		if (bean.getStartTime1() != null) {
			hql.append(" and pcd.startTime>=:startTime1");
			paramName.add("startTime1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartTime1()));
		}
		// 借据起始日期
		if (bean.getStartTime2() != null) {
			hql.append(" and pcd.startTime<=:startTime2");
			paramName.add("startTime2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getStartTime2()));
		}
		// 借据到期日期
		if (bean.getEndTime1() != null) {
			hql.append(" and pcd.endTime>=:endTime1");
			paramName.add("endTime1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getEndTime1()));
		}
		// 借据到期日期
		if (bean.getEndTime2() != null) {
			hql.append(" and pcd.endTime<=:endTime2");
			paramName.add("endTime2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndTime2()));
		}
		// 借据金额开始
		if (bean.getStartLoanAmount() != null) {
			hql.append(" and pcd.loanAmount>=:startAmount");
			paramName.add("startAmount");
			paramValue.add(bean.getStartLoanAmount());
		}
		// 借据金额结束
		if (bean.getEndLoanAmount() != null) {
			hql.append(" and pcd.loanAmount<=:loanAmount");
			paramName.add("loanAmount");
			paramValue.add(bean.getEndLoanAmount());
		}
		// 借据余额开始
		if (bean.getStartLoanBalance() != null) {
			hql.append(" and pcd.loanBalance>=:loanBalance1");
			paramName.add("loanBalance1");
			paramValue.add(bean.getStartLoanBalance());
		}
		// 借据余额结束
		if (bean.getEndLoanBalance() != null) {
			hql.append(" and pcd.loanBalance<=:loanBalance2");
			paramName.add("loanBalance2");
			paramValue.add(bean.getEndLoanBalance());
		}
		//票据池编号
		if(StringUtil.isNotEmpty(bean.getPoolAgreement())){
			hql.append(" and dto.poolAgreement = :poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		//是否在线业务
		if(StringUtil.isNotEmpty(bean.getIfOnline())){
			hql.append(" and cp.isOnline = :ifOnline");
			paramName.add("ifOnline");
			paramValue.add(bean.getIfOnline());
		}
		//是否垫款
		if(StringUtil.isNotEmpty(bean.getIfAdvanceAmt())){
			hql.append(" and pcd.ifAdvanceAmt = :ifAdvanceAmt");
			paramName.add("ifAdvanceAmt");
			paramValue.add(bean.getIfAdvanceAmt());
			
		}
		
		hql.append(" order by  pcd.transTime desc ");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		
		dhql.append(hql);

		List res = this.find(dhql.toString(), paramNames, paramValues, page);
		List retList = new ArrayList();
		if (res != null && res.size() > 0) {
			CreditProductQuery beanNew = null;
			PedProtocolDto ppd = null;
			CreditProduct cp = null;
			PedCreditDetail pcd = null;
			for (int i = 0; i < res.size(); i++) {
				beanNew = new CreditProductQuery();
				Object[] obj = (Object[]) res.get(i);
				if (obj[0] != null) {
					ppd = (PedProtocolDto) obj[0];
					beanNew.setPoolAgreement(ppd.getPoolAgreement()); // 票据池编号
					beanNew.setPoolName(ppd.getPoolName()); // 票据池名称
				}
				String cupy = "";//占用比例 
				if (obj[1] != null) {
					cp = (CreditProduct) obj[1];
					beanNew.setCustNo(cp.getCustNo()); // 客户号
					beanNew.setCrdtNo(cp.getCrdtNo()); // 合同号
					beanNew.setCustName(cp.getCustName());// 融资人名称
					beanNew.setCrdtIssDt(cp.getCrdtIssDt());// 合同起始日期
					beanNew.setCrdtDueDt(cp.getCrdtDueDt());// 合同到期日
					beanNew.setCcupy(cp.getCcupy()); // 额度占用比例
					beanNew.setSttlFlag(cp.getSttlFlag());// 合同状态
					beanNew.setRisklevel(cp.getRisklevel());//风险等级
					beanNew.setIfOnline(cp.getIsOnline());//是否线上					
					cupy = cp.getCcupy();
				}
				if (obj[2] != null) {
					pcd = (PedCreditDetail) obj[2];
					beanNew.setLoanNo(pcd.getLoanNo());// 借据号
					beanNew.setLoanAmount(pcd.getLoanAmount());// 借据金额
					beanNew.setLoanBalance(pcd.getLoanBalance());// 借据余额--未用
					beanNew.setActualAmount(pcd.getActualAmount());// 实际占用金额
					beanNew.setUseAmt(pcd.getActualAmount().multiply(new BigDecimal(cupy)));// 占用额度
					beanNew.setStartTime(pcd.getStartTime());// 借据起始日
					beanNew.setEndTime(pcd.getEndTime());// 借据到期日
					beanNew.setLoanStatus(pcd.getLoanStatus());// 借据状态
					beanNew.setCrdtType(pcd.getLoanType());// 业务品种
					beanNew.setTransAccount(pcd.getTransAccount());//贷款账号/主业务合同编号
					if(null != pcd.getIfAdvanceAmt()){						
						if(pcd.getIfAdvanceAmt().equals("1")){
							beanNew.setIfAdvanceAmt("是");
						}else{
							beanNew.setIfAdvanceAmt("否");
						}
					}else{
						beanNew.setIfAdvanceAmt("否");
					}
				}
				retList.add(beanNew);
			}
		}
		page.setResult(retList);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(retList, map);
	}

	/**
	 * 信贷产品发生查询 明细批量导出
	 * 
	 * @param bean
	 * @param page
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List findCrdtProductListExpt(CreditProductQueryBean bean, Page page, User user) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append(
				"from PedProtocolDto dto,CreditProduct cp,PedCreditDetail pcd where 1=1  and cp.bpsNo = dto.poolAgreement and cp.crdtNo=pcd.crdtNo");
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and dto.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}

		// 客户号
		if (StringUtil.isNotEmpty(bean.getCustnumber())) {
			hql.append(" and dto.custnumber = :custnumber");
			paramName.add("custnumber");
			paramValue.add(bean.getCustnumber());
		}

		// 融资人名称
		if (StringUtil.isNotEmpty(bean.getCustName())) {
			hql.append(" and cp.custName like :custName");
			paramName.add("custName");
			paramValue.add("%" + bean.getCustName() + "%");
		}

		// 业务品种
		if (StringUtil.isNotEmpty(bean.getCrdtType())) {
			hql.append(" and pcd.loanType=:crdtType");
			paramName.add("crdtType");
			paramValue.add(bean.getCrdtType());
		}

		// 合同号
		if (StringUtil.isNotEmpty(bean.getCrdtNo())) {
			hql.append(" and cp.crdtNo like :crdtNo");
			paramName.add("crdtNo");
			paramValue.add("%" + bean.getCrdtNo() + "%");
		}

		// 结清标记
		if (StringUtil.isNotEmpty(bean.getSttlFlag())) {
			hql.append(" and cp.sttlFlag=:sttlFlag");
			paramName.add("sttlFlag");
			paramValue.add(bean.getSttlFlag());
		}

		// 业务状态
		if (StringUtil.isNotEmpty(bean.getCrdtStatus())) {
			hql.append(" and pcd.loanStatus = :loanStatus");
			paramName.add("loanStatus");
			paramValue.add(bean.getCrdtStatus());
		}

		// 合同起始日期
		if (bean.getStartCrdtIssDt() != null) {
			hql.append(" and cp.crdtIssDt>=:aplyDtStart1");
			paramName.add("aplyDtStart1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartCrdtIssDt()));
		}
		// 合同起始日期
		if (bean.getEndCrdtIssDt() != null) {
			hql.append(" and cp.crdtIssDt<=:aplyDtStart2");
			paramName.add("aplyDtStart2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndCrdtIssDt()));
		}
		// 合同到期日期
		if (bean.getStartCrdtDueDt() != null) {
			hql.append(" and cp.crdtDueDt>=:aplyDtEnd1");
			paramName.add("aplyDtEnd1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartCrdtDueDt()));
		}
		// 合同到期日期
		if (bean.getEndCrdtDueDt() != null) {
			hql.append(" and cp.crdtDueDt<=:aplyDtEnd2");
			paramName.add("aplyDtEnd2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndCrdtDueDt()));
		}
		// 借据号
		if (StringUtil.isNotEmpty(bean.getLoanNo())) {
			hql.append(" and pcd.loanNo like :loanNo");
			paramName.add("loanNo");
			paramValue.add("%" + bean.getLoanNo() + "%");
		}
		// 借据起始日期
		if (bean.getStartTime1() != null) {
			hql.append(" and pcd.startTime>=:startTime1");
			paramName.add("startTime1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartTime1()));
		}
		// 借据起始日期
		if (bean.getStartTime2() != null) {
			hql.append(" and pcd.startTime<=:startTime2");
			paramName.add("startTime2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getStartTime2()));
		}
		// 借据到期日期
		if (bean.getEndTime1() != null) {
			hql.append(" and pcd.endTime>=:endTime1");
			paramName.add("endTime1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getEndTime1()));
		}
		// 借据到期日期
		if (bean.getEndTime2() != null) {
			hql.append(" and pcd.endTime<=:endTime2");
			paramName.add("endTime2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndTime2()));
		}
		// 借据金额开始
		if (bean.getStartLoanAmount() != null) {
			hql.append(" and pcd.loanAmount>=:loanAmount");
			paramName.add("loanAmount");
			paramValue.add(bean.getStartLoanAmount());
		}
		// 借据金额结束
		if (bean.getEndLoanAmount() != null) {
			hql.append(" and pcd.loanAmount<=:loanAmountEnd");
			paramName.add("loanAmountEnd");
			paramValue.add(new BigDecimal(bean.getEndLoanAmount().toString()));
		}
		// 借据余额开始
		if (bean.getStartLoanBalance() != null) {
			hql.append(" and pcd.loanBalance>=:loanBalance1");
			paramName.add("loanBalance1");
			paramValue.add(bean.getStartLoanBalance());
		}
		// 借据余额结束
		if (bean.getEndLoanBalance() != null) {
			hql.append(" and pcd.loanBalance<=:loanBalance2");
			paramName.add("loanBalance2");
			paramValue.add(bean.getEndLoanBalance());
		}
		//票据池编号
		if(StringUtil.isNotEmpty(bean.getPoolAgreement())){
			hql.append(" and dto.poolAgreement = :poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		//是否在线业务
		if(StringUtil.isNotEmpty(bean.getIfOnline())){
			hql.append(" and cp.isOnline = :ifOnline");
			paramName.add("ifOnline");
			paramValue.add(bean.getIfOnline());
		}
		//是否垫款
		if(StringUtil.isNotEmpty(bean.getIfAdvanceAmt())){
			hql.append(" and pcd.ifAdvanceAmt = :ifAdvanceAmt");
			paramName.add("ifAdvanceAmt");
			paramValue.add(bean.getIfAdvanceAmt());
			
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List retList = new ArrayList();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		if (res != null && res.size() > 0) {
			CreditProduct cp = null;
			PedCreditDetail pcd = null;
			BigDecimal ccupy = new BigDecimal("0.00");
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[19];
				Object[] obj = (Object[]) res.get(i);
				if (obj[1] != null) {
					cp = (CreditProduct) obj[1];
					s[0] = cp.getCrdtNo(); // 合同号
					s[3] = cp.getCustName();// 融资人名称
					s[4] = this.fmtCrdtType(cp.getCrdtType());// 业务品种
					s[7] = cp.getCcupy(); // 额度占用比例
					if(s[7]!=null){
						ccupy = new BigDecimal(s[7].toString());
					}
					if(cp.getCrdtIssDt() != null ){
						s[9] = DateUtils.formatDateToString(cp.getCrdtIssDt(), DateUtils.ORA_DATES_FORMAT);// 合同起始日期
					}else{
						s[9] = "";
					}
					if( cp.getCrdtDueDt() != null ){
						s[10] = DateUtils.formatDateToString(cp.getCrdtDueDt(), DateUtils.ORA_DATES_FORMAT);// 合同到期日
					}else {
						s[10] = "";
					}
					s[11] = fmtsFlag(cp.getSttlFlag());// 合同状态
					
					if(cp.getIsOnline().equals("1")){
						s[14] = "是";// 是否线上
					}else{
						s[14] = "否";// 是否线上
					}
					
				}
				s[17] =this.reRisklevelName(cp.getRisklevel());//风险等级
				if (obj[2] != null) {
					pcd = (PedCreditDetail) obj[2];
					s[1] = pcd.getLoanNo();// 借据号
					s[2] = pcd.getBpsNo();// 票据池编号
					s[5] = String.valueOf(pcd.getLoanAmount());// 借据金额
					s[6] = String.valueOf(pcd.getActualAmount());// 借据余额
					if(s[6]!=null){
						BigDecimal balance = new BigDecimal(s[6].toString());
						s[8] = String.valueOf(balance.multiply(ccupy));// 占用额度
					}else{
						s[8]="0.00";
					}
					if( pcd.getStartTime() != null ){
						s[12] = DateUtils.formatDateToString(pcd.getStartTime(), DateUtils.ORA_DATES_FORMAT);// 借据起始日
					}else {
						s[12] = "";
					}
					if( pcd.getEndTime() != null ){
						s[13] = DateUtils.formatDateToString(pcd.getEndTime(), DateUtils.ORA_DATES_FORMAT);// 借据到期日
					}else {
						s[13] = "";
					}
					if(null != pcd.getIfAdvanceAmt()){	//是否垫款					
						if(pcd.getIfAdvanceAmt().equals("1")){
							s[15] = "是";
						}else{
							s[15] = "否";
						}
					}else{
						s[15] = "否";
					}
					s[16] = this.fmtcrdStatus(pcd.getLoanStatus());// 借据状态
					s[18] = pcd.getTransAccount();// 贷款账号/主业务合同编号
				}
				retList.add(s);
			}
		}
		return retList;
	}


	public String findcpEduUseDetailView(String crdtNo, Page page) throws Exception {

		StringBuffer hql = new StringBuffer();
		hql.append("select dp from PedCreditDetail dp where dp.crdtNo=:crdtNo ");

		List param = new ArrayList();
		List paramValue = new ArrayList();// 值
		param.add("crdtNo");
		paramValue.add(crdtNo);
		String paramNames[] = (String[]) param.toArray(new String[param.size()]);
		Object paramValues[] = paramValue.toArray();
		List tempRes = this.find(hql.toString(), paramNames, paramValues, page);

		page.setResult(tempRes);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(tempRes, map);
	}

	// 信贷产品发生查询 机构查询
	public String findCrdtProductListByDept(String custName, String custNo, String crdtType, String sttlFlag,
			String crdtStatus, Date aplyDtStart, Date aplyDtEnd, Date dueDtStart, Date dueDtEnd, BigDecimal startAmount,
			BigDecimal endAmount, String selRange, String selBankNO, String crdtNo, Page page, User user)
			throws Exception {
		List list = new ArrayList();
		BigDecimal sumMoney2 = new BigDecimal(0);
		BigDecimal sumNum2 = new BigDecimal(0);
		StringBuffer tdsql = new StringBuffer();
		tdsql.append("select td.d_banknumber,td.d_name from t_department td ");
		tdsql.append("where td.d_level=2 ");
		List paramList = new ArrayList();

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer dhql = new StringBuffer();
		StringBuffer hql = new StringBuffer();
		dhql.append(
				"select cp.crdtBankName,sum(cp.crdtAmt),sum(cp.useAmt),count(cp.id) from CreditProduct cp where 1=1");

		// 申请人名称
		if (custName != null && !"".equals(custName)) {
			hql.append(" and cp.custName like :custName");
			paramName.add("custName");
			paramValue.add("%" + custName + "%");
		}

		// 申请人组织机构代码custNo
		if (custNo != null && !"".equals(custNo)) {
			hql.append(" and cp.custNo like :custNo");
			paramName.add("custNo");
			paramValue.add("%" + custNo + "%");
		}

		// 信贷产品类型
		if (crdtType != null && !"".equals(crdtType)) {
			hql.append(" and cp.crdtType=:crdtType");
			paramName.add("crdtType");
			paramValue.add(crdtType);
		}

		// 信贷业务号
		if (crdtNo != null && !"".equals(crdtNo)) {
			hql.append(" and cp.crdtNo like :crdtNo");
			paramName.add("crdtNo");
			paramValue.add("%" + crdtNo + "%");
		}

		// 结清标记
		if (sttlFlag != null && !"".equals(sttlFlag)) {
			hql.append(" and cp.sttlFlag=:sttlFlag");
			paramName.add("sttlFlag");
			paramValue.add(sttlFlag);
		}

		// 业务状态
		if (crdtStatus != null && !"".equals(crdtStatus)) {
			hql.append(" and cp.crdtStatus=:crdtStatus");
			paramName.add("crdtStatus");
			paramValue.add(crdtStatus);
		}

		// 申请日期开始
		if (aplyDtStart != null && !"".equals(aplyDtStart)) {
			hql.append(" and cp.crdtIssDt>=TO_DATE(:aplyDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtStart");
			paramValue.add(DateUtils.toString(aplyDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 申请日期结束
		if (aplyDtEnd != null && !"".equals(aplyDtEnd)) {
			hql.append(" and cp.crdtIssDt<=TO_DATE(:aplyDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtEnd");
			paramValue.add(DateUtils.toString(aplyDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 到期日开始
		if (dueDtStart != null && !"".equals(dueDtStart)) {
			hql.append(" and cp.crdtDueDt>=TO_DATE(:dueDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtStart");
			paramValue.add(DateUtils.toString(dueDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日结束
		if (dueDtEnd != null && !"".equals(dueDtEnd)) {
			hql.append(" and cp.crdtDueDt<=TO_DATE(:dueDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtEnd");
			paramValue.add(DateUtils.toString(dueDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 信贷金额开始
		if (startAmount != null && !"".equals(startAmount)) {
			hql.append(" and cp.crdtAmt>=:startAmount");
			paramName.add("startAmount");
			paramValue.add(startAmount);
		}
		// 信贷金额结束
		if (endAmount != null && !"".equals(endAmount)) {
			hql.append(" and cp.crdtAmt<=:endAmount");
			paramName.add("endAmount");
			paramValue.add(endAmount);
		}

		// 查询范围
		if (selRange.equals("1")) { // 本机构
			// 登录的用户
			hql.append(" and cp.crdtBankCode =:crdtBankCode");
			paramName.add("crdtBankCode");
			paramValue.add(selBankNO);
		} else if (selRange.equals("2")) { // 下辖
			List departmentList = new ArrayList();
			departmentService.getAllChildrenBankCodeList(departmentList, selBankNO, -1);
			departmentList.remove(selBankNO);
			if (departmentList.size() == 0) {
				departmentList.add("");
			}
			hql.append(" and cp.crdtBankCode in(:crdtBankCode)");
			paramName.add("crdtBankCode");
			paramValue.add(departmentList);
		}

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		dhql.append(hql);
		dhql.append(" group by cp.crdtBankName");
		List dh = this.find(dhql.toString(), paramNames, paramValues);
		if (dh != null && dh.size() > 0) {
			for (int i = 0; i < dh.size(); i++) {
				CreditPrdtQuery cpq = new CreditPrdtQuery();
				Object[] obj = (Object[]) dh.get(i);
				cpq.setCrdtBankName((String) obj[0]);
				if (obj[1] != null) {
					cpq.setCrdtAmt(new BigDecimal(String.valueOf(obj[1])));
				}
				if (obj[2] != null) {
					cpq.setUseAmt(new BigDecimal(String.valueOf(obj[2])));
				}
				if (obj[3] != null) {
					cpq.setTotalNum(String.valueOf(obj[3]));
				}

				list.add(cpq);
			}
		}

		// 总笔数总金额
		StringBuffer sumhql = new StringBuffer();
		sumhql.append("select count(cp.id),sum(cp.crdtAmt) from CreditProduct cp where 1=1 ");
		sumhql.append(hql);

		String sumMoney = null;
		String sumNum = null;
		List sumList = this.find(sumhql.toString(), paramNames, paramValues);
		if (sumList != null && sumList.size() > 0) {
			Object[] obj = (Object[]) sumList.get(0);
			if (obj[0] != null) {
				sumNum = String.valueOf(obj[0]);
			}
			if (obj[1] != null) {
				sumMoney = String.valueOf(obj[1]);
			}

		}

		if (list != null && list.size() > 0) {
			((CreditPrdtQuery) list.get(0)).setSumNum(sumNum);
			((CreditPrdtQuery) list.get(0)).setSumMoney(sumMoney);
		}

		page.setTotalCount(list.size());
		int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
		int endIndex = (page.getPageIndex() * page.getPageSize()) > list.size() ? list.size()
				: page.getPageIndex() * page.getPageSize();
		List returnBatchesList = new ArrayList(); // 本次需要显示的批次
		returnBatchesList.addAll(list.subList(startIndex, endIndex));
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(returnBatchesList, map);
	}

	// 信贷产品发生查询 按户合计
	public List findCrdtProductListByCustExpt(String custName, String custNo, String crdtType, String sttlFlag,
			String crdtStatus, Date aplyDtStart, Date aplyDtEnd, Date dueDtStart, Date dueDtEnd, BigDecimal startAmount,
			BigDecimal endAmount, String selRange, String bankNos, String selBankNO, String crdtNo, Page page,
			User user) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer dhql = new StringBuffer();
		StringBuffer hql = new StringBuffer();
		dhql.append(
				"select cp.custName,cp.crdtBankName,sum(cp.crdtAmt),sum(cp.useAmt),count(cp.id) from CreditProduct cp where 1=1");

		// 申请人名称
		if (custName != null && !"".equals(custName)) {
			hql.append(" and cp.custName like :custName");
			paramName.add("custName");
			paramValue.add("%" + custName + "%");
		}

		// 申请人组织机构代码custNo
		if (custNo != null && !"".equals(custNo)) {
			hql.append(" and cp.custNo like :custNo");
			paramName.add("custNo");
			paramValue.add("%" + custNo + "%");
		}

		// 信贷产品类型
		if (crdtType != null && !"".equals(crdtType)) {
			hql.append(" and cp.crdtType=:crdtType");
			paramName.add("crdtType");
			paramValue.add(crdtType);
		}

		// 信贷业务号
		if (crdtNo != null && !"".equals(crdtNo)) {
			hql.append(" and cp.crdtNo like :crdtNo");
			paramName.add("crdtNo");
			paramValue.add("%" + crdtNo + "%");
		}

		// 结清标记
		if (sttlFlag != null && !"".equals(sttlFlag)) {
			hql.append(" and cp.sttlFlag=:sttlFlag");
			paramName.add("sttlFlag");
			paramValue.add(sttlFlag);
		}

		// 业务状态
		if (crdtStatus != null && !"".equals(crdtStatus)) {
			hql.append(" and cp.crdtStatus=:crdtStatus");
			paramName.add("crdtStatus");
			paramValue.add(crdtStatus);
		}

		// 申请日期开始
		if (aplyDtStart != null && !"".equals(aplyDtStart)) {
			hql.append(" and cp.crdtIssDt>=TO_DATE(:aplyDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtStart");
			paramValue.add(DateUtils.toString(aplyDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 申请日期结束
		if (aplyDtEnd != null && !"".equals(aplyDtEnd)) {
			hql.append(" and cp.crdtIssDt<=TO_DATE(:aplyDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtEnd");
			paramValue.add(DateUtils.toString(aplyDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 到期日开始
		if (dueDtStart != null && !"".equals(dueDtStart)) {
			hql.append(" and cp.crdtDueDt>=TO_DATE(:dueDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtStart");
			paramValue.add(DateUtils.toString(dueDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日结束
		if (dueDtEnd != null && !"".equals(dueDtEnd)) {
			hql.append(" and cp.crdtDueDt<=TO_DATE(:dueDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtEnd");
			paramValue.add(DateUtils.toString(dueDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 信贷金额开始
		if (startAmount != null && !"".equals(startAmount)) {
			hql.append(" and cp.crdtAmt>=:startAmount");
			paramName.add("startAmount");
			paramValue.add(startAmount);
		}
		// 信贷金额结束
		if (endAmount != null && !"".equals(endAmount)) {
			hql.append(" and cp.crdtAmt<=:endAmount");
			paramName.add("endAmount");
			paramValue.add(endAmount);
		}
		if (selRange.equals("1")) { // 本机构
			// 登录的用户
			hql.append(" and cp.crdtBankCode =:crdtBankCode");
			paramName.add("crdtBankCode");
			paramValue.add(selBankNO);
		} else if (selRange.equals("2")) { // 下辖
			List departmentList = new ArrayList();
			departmentService.getAllChildrenBankCodeList(departmentList, selBankNO, -1);
			departmentList.remove(selBankNO);
			hql.append(" and cp.crdtBankCode in(:crdtBankCode)");
			paramName.add("crdtBankCode");
			paramValue.add(departmentList);
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		dhql.append(hql);
		dhql.append(" group by cp.custName,cp.crdtBankName");
		List res = this.find(dhql.toString(), paramNames, paramValues);
		List palist = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[5];
				Object[] obj = (Object[]) res.get(i);
				s[0] = (String) obj[0];
				s[1] = (String) obj[1];
				if (obj[2] != null) {
					s[2] = String.valueOf(obj[2]);
				}
				if (obj[3] != null) {
					s[3] = String.valueOf(obj[3]);
				}
				if (obj[4] != null) {
					s[4] = String.valueOf(obj[4]);
				}

				palist.add(s);
			}
		}
		return palist;
	}

	// 信贷产品发生查询 机构查询批量导出
	public List findCrdtProductListByDeptExpt(String custName, String custNo, String crdtType, String sttlFlag,
			String crdtStatus, Date aplyDtStart, Date aplyDtEnd, Date dueDtStart, Date dueDtEnd, BigDecimal startAmount,
			BigDecimal endAmount, String selRange, String bankNos, String selBankNO, String crdtNo, Page page,
			User user) throws Exception {

		String sflag = "";
		if (bankNos != null && bankNos.length() > 1) {
			sflag = bankNos.substring(0, 2);
		}
		List list = new ArrayList();

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer dhql = new StringBuffer();
		StringBuffer hql = new StringBuffer();
		dhql.append(
				"select cp.crdtBankName,sum(cp.crdtAmt),sum(cp.useAmt),count(cp.id) from CreditProduct cp where 1=1");

		// 申请人名称
		if (custName != null && !"".equals(custName)) {
			hql.append(" and cp.custName like :custName");
			paramName.add("custName");
			paramValue.add("%" + custName + "%");
		}

		// 申请人组织机构代码custNo
		if (custNo != null && !"".equals(custNo)) {
			hql.append(" and cp.custNo like :custNo");
			paramName.add("custNo");
			paramValue.add("%" + custNo + "%");
		}

		// 信贷产品类型
		if (crdtType != null && !"".equals(crdtType)) {
			hql.append(" and cp.crdtType=:crdtType");
			paramName.add("crdtType");
			paramValue.add(crdtType);
		}

		// 信贷业务号
		if (crdtNo != null && !"".equals(crdtNo)) {
			hql.append(" and cp.crdtNo like :crdtNo");
			paramName.add("crdtNo");
			paramValue.add("%" + crdtNo + "%");
		}

		// 结清标记
		if (sttlFlag != null && !"".equals(sttlFlag)) {
			hql.append(" and cp.sttlFlag=:sttlFlag");
			paramName.add("sttlFlag");
			paramValue.add(sttlFlag);
		}

		// 业务状态
		if (crdtStatus != null && !"".equals(crdtStatus)) {
			hql.append(" and cp.crdtStatus=:crdtStatus");
			paramName.add("crdtStatus");
			paramValue.add(crdtStatus);
		}

		// 申请日期开始
		if (aplyDtStart != null && !"".equals(aplyDtStart)) {
			hql.append(" and cp.crdtIssDt>=TO_DATE(:aplyDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtStart");
			paramValue.add(DateUtils.toString(aplyDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 申请日期结束
		if (aplyDtEnd != null && !"".equals(aplyDtEnd)) {
			hql.append(" and cp.crdtIssDt<=TO_DATE(:aplyDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("aplyDtEnd");
			paramValue.add(DateUtils.toString(aplyDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 到期日开始
		if (dueDtStart != null && !"".equals(dueDtStart)) {
			hql.append(" and cp.crdtDueDt>=TO_DATE(:dueDtStart, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtStart");
			paramValue.add(DateUtils.toString(dueDtStart, "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日结束
		if (dueDtEnd != null && !"".equals(dueDtEnd)) {
			hql.append(" and cp.crdtDueDt<=TO_DATE(:dueDtEnd, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("dueDtEnd");
			paramValue.add(DateUtils.toString(dueDtEnd, "yyyy-MM-dd") + " 23:59:59");
		}
		// 信贷金额开始
		if (startAmount != null && !"".equals(startAmount)) {
			hql.append(" and cp.crdtAmt>=:startAmount");
			paramName.add("startAmount");
			paramValue.add(startAmount);
		}
		// 信贷金额结束
		if (endAmount != null && !"".equals(endAmount)) {
			hql.append(" and cp.crdtAmt<=:endAmount");
			paramName.add("endAmount");
			paramValue.add(endAmount);
		}
		if (selRange.equals("1")) { // 本机构
			// 登录的用户
			hql.append(" and cp.crdtBankCode =:crdtBankCode");
			paramName.add("crdtBankCode");
			paramValue.add(selBankNO);
		} else if (selRange.equals("2")) { // 下辖
			List departmentList = new ArrayList();
			departmentService.getAllChildrenBankCodeList(departmentList, selBankNO, -1);
			departmentList.remove(selBankNO);
			hql.append(" and cp.crdtBankCode in(:crdtBankCode)");
			paramName.add("crdtBankCode");
			paramValue.add(departmentList);
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		dhql.append(hql);
		dhql.append(" group by cp.crdtBankName");
		List dh = this.find(dhql.toString(), paramNames, paramValues);
		if (dh != null && dh.size() > 0) {
			for (int i = 0; i < dh.size(); i++) {
				String[] s = new String[4];
				Object[] obj = (Object[]) dh.get(i);
				s[0] = (String) obj[0];
				if (obj[1] != null) {
					s[1] = String.valueOf(obj[1]);
				}
				if (obj[2] != null) {
					s[2] = String.valueOf(obj[2]);
				}
				if (obj[3] != null) {
					s[3] = String.valueOf(obj[3]);
				}
				list.add(s);
			}
		}
		return list;
	}

	private String fmtCrdtType(String value) {
		if ("XD_01".equals(value)) {
			return "银行承兑汇票";
		} else if ("XD_02".equals(value)) {
			return "流动资金贷款";
		} else if ("XD_03".equals(value)) {
			return "保函";
		} else if ("XD_04".equals(value)) {
			return "信用证";
		}else if ("XD_05".equals(value)) {
			return "表外业务垫款";
		} else {
			return "";
		}
	}

	private String fmtcrdStatus(String value) {
		if ("JJ_01".equals(value)) {
			return "已放款";
		} else if ("JJ_02".equals(value)) {
			return "部分还款";
		} else if ("JJ_03".equals(value)) {
			return "逾期/垫款";
		} else if ("JJ_04".equals(value)) {
			return "结清";
		} else if ("JJ_05".equals(value)) {
			return "已撤销";
		} else {
			return "";
		}
	}

	private String fmtsFlag(String value) {
		if ("JQ_00".equals(value)) {
			return "已结清";
		} else if ("JQ_01".equals(value)) {
			return "未结清";
		} else if ("JQ_02".equals(value)) {
			return "取消放贷";
		}
		return "";
	}

	@Override
	public List toPoolAllInQuery(DraftQueryBean bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append("select po from DraftPoolIn  po, PedProtocolDto dto where po.poolAgreement = dto.poolAgreement ");
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//总行管理员;部门总行角色为查询员、管理员、审批员  可查询所有数据
				
			}else if(str.equals("1") || str.equals("3")){//支行管理员;部门分行角色为查询员、管理员、审批员 可查询分支行下所有数据
					//分行或一级支行看本辖内，网点查看本网点
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and po.branchId in (:branchId) ");
				paramName.add("branchId");
				paramValue.add(resultList);
			}else if(str.equals("4")){//客户经理:只可查询票据池协议为业务员为自己的数据
				hql.append(" and dto.accountManager = :accountManager ");
				paramName.add("accountManager");
				paramValue.add(user.getName());
			}else if(str.equals("5")){//授权结算柜员:无数据
				return null;
			}
		}
		
		// 增加机构筛选条件
//		if (user != null && user.getDepartment() != null) {
//			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
//			if (!PublicStaticDefineTab.isRootDepartment(user)) {
//				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
//				hql.append(" and po.branchId in (:branchId) ");
//				paramName.add("branchId");
//				paramValue.add(resultList);
//			}
//		}
		//id
		if (bean.getBusiId() != null && !"".equals((bean.getBusiId()))) {
			hql.append(" and po.id = :id");
			paramName.add("id");
			paramValue.add(bean.getBusiId());
		}
		if(bean.getIds() != null && !bean.getIds().equals("")){
			hql.append(" and po.id in(:ids)");
			paramName.add("ids");
			List idList = Arrays.asList(bean.getIds().split(",")); //id集合
			paramValue.add(idList);
		}
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.plDraftNb like :plDraftNb");
			paramName.add("plDraftNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		
		/********************融合改造新增 start******************************/
		//子票区间起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		//子票区间止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		
		
		/********************融合改造新增 end******************************/
		
		if (bean.getPlStatus() != null && !bean.getPlStatus().isEmpty()) {
			hql.append(" and po.plStatus in(:plStatus)");
			paramName.add("plStatus");
			paramValue.add(bean.getPlStatus());
		}
		// id
		if (bean.getAssetNb() != null && !"".equals((bean.getAssetNb()))) {
			hql.append(" and po.id like :id");
			paramName.add("id");
			paramValue.add("%" + bean.getAssetNb() + "%");
		}
		// 批次号
		if (bean.getBatchNo() != null && !"".equals((bean.getBatchNo()))) {
			hql.append(" and po.batchNo like :batchNo");
			paramName.add("batchNo");
			paramValue.add("%" + bean.getBatchNo() + "%");
		}

		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia())) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())) {
			hql.append(" and po.plDraftType=:plDraftType");
			paramName.add("plDraftType");
			paramValue.add(bean.getAssetType());
		}

		// 业务经办行assetRecSvcrNm
		if (bean.getAssetRecSvcrNm() != null && !"".equals(bean.getAssetRecSvcrNm())) {
			hql.append(" and po.plRecSvcrNm like :plRecSvcrNm");
			paramName.add("plRecSvcrNm");
			paramValue.add("%" + bean.getAssetRecSvcrNm() + "%");
		}

		// 企业组织机构代码plCommId
		if (bean.getPlCommId() != null && !"".equals(bean.getPlCommId())) {
			hql.append(" and po.plCommId like :plCommId");
			paramName.add("plCommId");
			paramValue.add("%" + bean.getPlCommId() + "%");
		}

		// 企业名称assetApplyNm
		if (bean.getAssetApplyNm() != null && !"".equals(bean.getAssetApplyNm())) {
			hql.append(" and po.plApplyNm=:plApplyNm");
			paramName.add("plApplyNm");
			paramValue.add(bean.getAssetApplyNm());
		}

		// 入池日期开始startplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plReqTime>=TO_DATE(:splReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splReqTime");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 入池申请日期结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plReqTime<=TO_DATE(:eplReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplReqTime");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}
		// 批次状态 assetStatus
		if (bean.getAssetStatus() != null && !"".equals(bean.getAssetStatus())) {
			hql.append(" and po.plStatus=:plStatus");
			paramName.add("plStatus");
			paramValue.add(bean.getAssetStatus());
		}
		// 票据池编号 poolAgreement
		if (bean.getPoolAgreement() != null && !"".equals(bean.getPoolAgreement())) {
			hql.append(" and po.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		// 票据池客户名称CustName
		if (bean.getCustName() != null && !"".equals(bean.getCustName())) {
			hql.append(" and po.custName like:custName");
			paramName.add("custName");
			paramValue.add("%"+bean.getCustName()+"%");
		}
		
		hql.append(" ORDER BY po.plReqTime DESC");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List res = this.find(hql.toString(), paramNames, paramValues, page);

		return res;
	}

	/**
	 * @param 票据入池查询
	 * @param gcj 20210701
	 * @return
	 * @throws Exception
	 */
	public QueryResult toPoolInByQueryBean(DraftQueryBean bean, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append("select po,info from DraftPoolIn  po,PoolBillInfo info  where po.plDraftNb = info.SBillNo ");
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.plDraftNb like :plDraftNb");
			paramName.add("plDraftNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		/********************融合改造新增 start******************************/
		// 票据号码起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo = :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add(bean.getBeginRangeNo());
		}
		// 票据号码止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo = :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add(bean.getEndRangeNo());
		}
		// 票据来源
		if (bean.getDraftSource() != null && !"".equals((bean.getDraftSource()))) {
			hql.append(" and po.draftSource = :draftSource");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		/********************融合改造新增 end******************************/
		if (bean.getPlStatus() != null && !bean.getPlStatus().isEmpty()) {
			hql.append(" and po.plStatus in(:plStatus)");
			paramName.add("plStatus");
			paramValue.add(bean.getPlStatus());
		}
		// id
		if (bean.getAssetNb() != null && !"".equals((bean.getAssetNb()))) {
			hql.append(" and po.id like :id");
			paramName.add("id");
			paramValue.add("%" + bean.getAssetNb() + "%");
		}
		// 批次号
		if (bean.getBatchNo() != null && !"".equals((bean.getBatchNo()))) {
			hql.append(" and po.batchNo like :batchNo");
			paramName.add("batchNo");
			paramValue.add("%" + bean.getBatchNo() + "%");
		}

		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia()) && !"0".equals(bean.getPlDraftMedia() )) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())&& !"0000".equals(bean.getAssetType() )) {
			hql.append(" and po.plDraftType=:plDraftType");
			paramName.add("plDraftType");
			paramValue.add(bean.getAssetType());
		}

		// 入池日期开始startplDueDt
		if (bean.getStartplReqTime() != null && !"".equals(bean.getStartplReqTime())) {
			hql.append(" and po.plReqTime>=TO_DATE(:splReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splReqTime");
			paramValue.add(DateUtils.toString(bean.getStartplReqTime(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 入池申请日期结束endplDueDt
		if (bean.getEndplReqTime() != null && !"".equals(bean.getEndplReqTime())) {
			hql.append(" and po.plReqTime<=TO_DATE(:eplReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplReqTime");
			paramValue.add(DateUtils.toString(bean.getEndplReqTime(), "yyyy-MM-dd") + " 23:59:59");
		}
		// 批次状态 assetStatus
		if (bean.getAssetStatus() != null && !"".equals(bean.getAssetStatus())) {
			hql.append(" and po.plStatus=:plStatus");
			paramName.add("plStatus");
			paramValue.add(bean.getAssetStatus());
		}
		// 票据池编号 poolAgreement
		if (bean.getPoolAgreement() != null && !"".equals(bean.getPoolAgreement())) {
			hql.append(" and po.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		// 票据池客户名称CustName
		if (bean.getCustName() != null && !"".equals(bean.getCustName())) {
			hql.append(" and po.custName=:custName");
			paramName.add("custName");
			paramValue.add(bean.getCustName());
		}
		// 票据池核心客户号CustNo
		if (bean.getCustNo() != null && !"".equals(bean.getCustNo())) {
			hql.append(" and po.custNo=:custNo");
			paramName.add("custNo");
			paramValue.add(bean.getCustNo());
		}
		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and po.plIsseAmt>=:startplIsseAmt");
			paramName.add("startplIsseAmt");
			paramValue.add(bean.getStartassetAmt());
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and po.plIsseAmt<=:endplIsseAmt");
			paramName.add("endplIsseAmt");
			paramValue.add(bean.getEndassetAmt());
		}

		// 出票日期开始StartplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and po.plIsseDt>=TO_DATE(:splIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splIsseDt");
			paramValue.add(DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 出票日期结束endplDueDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and po.plIsseDt<=TO_DATE(:eplIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplIsseDt");
			paramValue.add(DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59");
		}
		// 到期日开始StartplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plDueDt>=TO_DATE(:splDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splDueDt");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plDueDt<=TO_DATE(:eplDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplDueDt");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}
		hql.append(" order by po.plReqTime desc ");

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		QueryResult qr = new QueryResult();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		if(res != null && res.size() > 0){
			List result = new ArrayList();
			for (int i = 0; i < res.size(); i++) {
				Object[] obj = (Object[]) res.get(i);
				DraftPoolIn in = (DraftPoolIn) obj[0];
				PoolBillInfo info = (PoolBillInfo) obj[1];
				in.setPlDrwrAcctSvcr(info.getSIssuerBankCode());
				in.setPlPyeeAcctSvcr(info.getSPayeeBankCode());
				in.setForbidFlag(info.getSBanEndrsmtFlag());
				in.setSOperatorId(info.getDiscBillId());//字段暂存票据id
				result.add(in);
			}
			String amountFieldName = "plIsseAmt";
			if (result != null && result.size() > 0) {
				qr = QueryResult.buildQueryResult(result, amountFieldName);
			}
		}
		return qr;
	}
	
	@Override
	public List toPoolAllOutQuery(DraftQueryBean bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append("select po from DraftPoolOut  po, PedProtocolDto dto where po.poolAgreement = dto.poolAgreement ");
		
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//总行管理员;部门总行角色为查询员、管理员、审批员  可查询所有数据
				
			}else if(str.equals("1") || str.equals("3")){//支行管理员;部门分行角色为查询员、管理员、审批员 可查询分支行下所有数据
					//分行或一级支行看本辖内，网点查看本网点
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and po.branchId in (:branchId) ");
				paramName.add("branchId");
				paramValue.add(resultList);
			}else if(str.equals("4")){//客户经理:只可查询票据池协议为业务员为自己的数据
				hql.append(" and dto.accountManager = :accountManager ");
				paramName.add("accountManager");
				paramValue.add(user.getName());
			}else if(str.equals("5")){//授权结算柜员:无数据
				return null;
			}
		}
		
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and po.branchId in (:branchId) ");
				paramName.add("branchId");
				paramValue.add(resultList);
			}
		}
		// 票号
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.plDraftNb like :plDraftNb");
			paramName.add("plDraftNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		
		/********************融合改造新增 start******************************/
		//子票区间起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		//子票区间止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}
		
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		/********************融合改造新增 end******************************/
		
		// 票据状态
		if (bean.getPlStatus() != null && !bean.getPlStatus().isEmpty()) {
			hql.append(" and po.plStatus in (:plStatus)");
			paramName.add("plStatus");
			paramValue.add(bean.getPlStatus());
		}
		// id
		if (bean.getAssetNb() != null && !"".equals((bean.getAssetNb()))) {
			hql.append(" and po.id like :id");
			paramName.add("id");
			paramValue.add("%" + bean.getAssetNb() + "%");
		}
		if(bean.getIds() != null && !bean.getIds().equals("")){
			hql.append(" and po.id in(:ids)");
			paramName.add("ids");
			List idList = Arrays.asList(bean.getIds().split(",")); //id集合
			paramValue.add(idList);
		}
		if(bean.getBusiId() != null && !bean.getBusiId().equals("")){
			hql.append(" and po.id =:id");
			paramName.add("id");
			paramValue.add(bean.getBusiId());
		}
		// 批次号
		if (bean.getBatchNo() != null && !"".equals((bean.getBatchNo()))) {
			hql.append(" and po.batchNo like :batchNo");
			paramName.add("batchNo");
			paramValue.add("%" + bean.getBatchNo() + "%");
		}

		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia())) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())) {
			hql.append(" and po.plDraftType=:plDraftType");
			paramName.add("plDraftType");
			paramValue.add(bean.getAssetType());
		}

		// 业务经办行assetRecSvcrNm
		if (bean.getAssetRecSvcrNm() != null && !"".equals(bean.getAssetRecSvcrNm())) {
			hql.append(" and po.plRecSvcrNm like :plRecSvcrNm");
			paramName.add("plRecSvcrNm");
			paramValue.add("%" + bean.getAssetRecSvcrNm() + "%");
		}

		// 企业组织机构代码plCommId
		if (bean.getPlCommId() != null && !"".equals(bean.getPlCommId())) {
			hql.append(" and po.plCommId like :plCommId");
			paramName.add("plCommId");
			paramValue.add("%" + bean.getPlCommId() + "%");
		}

		// 企业名称assetApplyNm
		if (bean.getAssetApplyNm() != null && !"".equals(bean.getAssetApplyNm())) {
			hql.append(" and po.plApplyNm=:plApplyNm");
			paramName.add("plApplyNm");
			paramValue.add(bean.getAssetApplyNm());
		}

		// 出池日期开始startplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plReqTime>=TO_DATE(:splReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splReqTime");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 出池申请日期结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plReqTime<=TO_DATE(:eplReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplReqTime");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 批次状态 assetStatus
		if (bean.getAssetStatus() != null && !"".equals(bean.getAssetStatus())) {
			hql.append(" and po.plStatus=:plStatus");
			paramName.add("plStatus");
			paramValue.add(bean.getAssetStatus());
		}
		// 票据池编号 poolAgreement
		if (bean.getPoolAgreement() != null && !"".equals(bean.getPoolAgreement())) {
			hql.append(" and po.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		// 票据池客户名称CustName
		if (bean.getCustName() != null && !"".equals(bean.getCustName())) {
			hql.append(" and po.custName like:custName");
			paramName.add("custName");
			paramValue.add("%"+bean.getCustName()+"%");
		}
		if (bean.getTaskDate() != null) {
			hql.append(" and to_char(po.taskDate,'yyyy-mm-dd') =:taskDate");
			paramName.add("taskDate");
			paramValue.add(DateUtils.toDateString(bean.getTaskDate()));
		}
		
		hql.append(" ORDER BY po.plReqTime DESC");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List res = this.find(hql.toString(), paramNames, paramValues, page);

		return res;
	}

	@Override
	public List toPoolOutSignQuery(DraftPoolOut bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append("select po from DraftPoolOut po where 1=1 and po.plStatus =:plStatus  ");
		paramName.add("plStatus");
		paramValue.add(PoolComm.CC_05_1);
		hql.append(" and po.plDueDt >= :plDueDt");//到期日大于当前工作日的才可重发解质押签收申请
		paramName.add("plDueDt");
		paramValue.add(DateUtils.getCurrDate());
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and po.branchId in (:branchId) ");
				paramName.add("branchId");
				paramValue.add(resultList);
			}
		}
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.plDraftNb like :plDraftNb");
			paramName.add("plDraftNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		
		/********************融合改造新增 start******************************/
		// 子票区间起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		// 子票区间止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		/********************融合改造新增 end******************************/

		
		// 客户号plCommId
		if (bean.getPlCommId() != null && !"".equals(bean.getPlCommId())) {
			hql.append(" and po.plCommId = :plCommId");
			paramName.add("plCommId");
			paramValue.add(bean.getPlCommId());
		}
		// 申请人名称
		if (bean.getPlApplyNm() != null && !"".equals((bean.getPlApplyNm()))) {
			hql.append(" and po.plApplyNm like :plApplyNm");
			paramName.add("plApplyNm");
			paramValue.add("%" + bean.getPlApplyNm() + "%");
		}

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List res = this.find(hql.toString(), paramNames, paramValues, page);

		return res;
	}

	@Override
	public List toPoolOutExpireQuery(CollectionSendDto bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		String time = DateUtils.toDateString(new Date());
		StringBuffer hql = new StringBuffer();
		hql.append(
				"select cs from DraftPoolOut cs where cs.plStatus in(:plStatus) and cs.plDueDt<=TO_DATE(:eplReqTime, 'yyyy-mm-dd hh24:mi:ss') ");
		List list = new ArrayList();
		list.add(PoolComm.CC_05_1);// 解质押签收异常
		list.add(PoolComm.RC_05);// 入池已记账
		paramName.add("plStatus");
		paramValue.add(list);
		paramName.add("eplReqTime");
		paramValue.add(time + " 23:59:59");

		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and cs.branchId in (:branchId) ");
				paramName.add("branchId");
				paramValue.add(resultList);
			}
		}
		if (bean.getSBillNo() != null && !bean.getSBillNo().equals("")) {// 票号查询
			hql.append(" and cs.plDraftNb like :SBillNo");
			paramName.add("SBillNo");
			paramValue.add("%" + bean.getSBillNo() + "%");
		}
		
		/********************融合改造新增 start******************************/
		if (bean.getBeginRangeNo() != null && !bean.getBeginRangeNo().equals("")) {//子票区间起
			hql.append(" and cs.beginRangeNo like :beginRangeNo");
			paramName.add("SBillNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		if (bean.getEndRangeNo() != null && !bean.getEndRangeNo().equals("")) {// 子票区间止
			hql.append(" and cs.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and cs.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (cs.draftSource is null or cs.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and cs.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and cs.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/********************融合改造新增 end******************************/
		
		
		if (bean.getSBillType() != null && !bean.getSBillType().equals("")) {// 票据类型
			hql.append(" and cs.plDraftType = :SBillType");
			paramName.add("SBillType");
			paramValue.add(bean.getSBillType());
		}
		if (bean.getAcceptAcctSvcr() != null && !bean.getAcceptAcctSvcr().equals("")) {// 承兑行行号
			hql.append(" and cs.plAccptrSvcr like :acceptAcctSvcr");
			paramName.add("acceptAcctSvcr");
			paramValue.add("%" + bean.getAcceptAcctSvcr() + "%");
		}

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List res = this.find(hql.toString(), paramNames, paramValues, page);

		return res;
	}

	@Override
	public List InventedBillQuery(DraftQueryBean bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append(
				"select pp.poolAgreement,pp.poolName,po from PoolVtrust po,PedProtocolDto pp where 1=1 and po.bpsNo=pp.poolAgreement");
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and pp.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		hql.append(" and po.vtStatus =:vtStatus");
		paramName.add("vtStatus");
		paramValue.add(PoolComm.DS_00);//未处理
		// 票据号码
		if (bean.getAssetNb() != null && !"".equals((bean.getAssetNb()))) {
			hql.append(" and po.vtNb like :vtNb");
			paramName.add("vtNb");
			paramValue.add("%" + bean.getAssetNb() + "%");
		}
//		子票区间起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		//子票区间止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}
		
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		
		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia())) {
			hql.append(" and po.vtDraftMedia=:vtDraftMedia");
			paramName.add("vtDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}
		
		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())) {
			hql.append(" and po.vtType=:vtType");
			paramName.add("vtType");
			paramValue.add(bean.getAssetType());
		}
		//票据权益
		if (bean.getPayType() != null && !"".equals(bean.getPayType())) {
			hql.append(" and po.payType=:payType");
			paramName.add("payType");
			paramValue.add(bean.getPayType());
		}
		// 企业组织机构代码plCommId
		if (bean.getAssetCommId() != null && !"".equals(bean.getAssetCommId())) {
			hql.append(" and po.vtEntpNo like :vtEntpNo");
			paramName.add("vtEntpNo");
			paramValue.add("%" + bean.getAssetCommId() + "%");
		}

		// 出票日期开始startplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and po.vtisseDt>=TO_DATE(:vtDeDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("vtDeDt");
			paramValue.add(DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 出票日期结束endplIsseDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and po.vtisseDt<=TO_DATE(:vtisseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("vtisseDt");
			paramValue.add(DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 到期日期开始startplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.vtdueDt>=TO_DATE(:vtdueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("vtdueDt");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日期结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.vtdueDt<=TO_DATE(:vtissEndDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("vtissEndDt");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 额度类型rickLevel
		if (bean.getRickLevel() != null && !"".equals(bean.getRickLevel())) {
			hql.append(" and po.rickLevel=:rickLevel");
			paramName.add("rickLevel");
			paramValue.add(bean.getRickLevel());
		}

		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and po.vtisseAmt>=:vstartAmt");
			paramName.add("vstartAmt");
			paramValue.add(bean.getStartassetAmt());
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and po.vtisseAmt<=:vtisseAmt");
			paramName.add("vtisseAmt");
			paramValue.add(bean.getEndassetAmt());
		}
		//票据池编号
		if(StringUtil.isNotEmpty(bean.getPoolAgreement())){
			hql.append(" and pp.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		//票据池名称
		if(StringUtil.isNotEmpty(bean.getPoolName())){
			hql.append(" and pp.poolName like:poolName");
			paramName.add("poolName");
			paramValue.add("%"+bean.getPoolName()+"%");
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		List retList = new ArrayList();
		if (res != null && res.size() > 0) {
			PoolVtrustBeanQuery beanNew = null;
			PoolVtrust info = null;
			for (int i = 0; i < res.size(); i++) {
				Object[] obj = (Object[]) res.get(i);
				beanNew = new PoolVtrustBeanQuery();
				if (obj[0] != null) {
					beanNew.setPoolAgreement(obj[0].toString());// 票据编号
				}
				if (obj[1] != null) {
					beanNew.setPoolName(obj[1].toString());// 票据池名称
				}
				if (obj[2] != null) {
					info = (PoolVtrust) obj[2];
					BeanUtils.copyProperties(beanNew, info);
				}
				
				//顺延天数
				long deferDays = assetTypeManageService.queryDelayDays(info.getRickLevel(), info.getVtdueDt());
				beanNew.setDelayDays((int)deferDays);
				retList.add(beanNew);
			}
		}
		return retList;
	}

	@Override
	public List findPoolDraftExpt1(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			PoolVtrustBeanQuery pool = null;
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[14];
				pool = (PoolVtrustBeanQuery) res.get(i);
				s[0] = pool.getPoolAgreement();
				s[1] = pool.getPoolName();
				s[2] = pool.getVtNb();
				s[3] = this.reBillMediaName(pool.getVtDraftMedia());
				s[4] = this.reBillTypeName(pool.getVtType());
				s[5] = String.valueOf(pool.getVtisseAmt());
				if (pool.getVtisseDt() != null) {
					s[6] = DateUtils.formatDateToString(pool.getVtisseDt(), DateUtils.ORA_DATES_FORMAT);
				}
				if (pool.getVtdueDt() != null) {
					s[7] = DateUtils.formatDateToString(pool.getVtdueDt(), DateUtils.ORA_DATES_FORMAT);
				}
				s[8] = pool.getVtdrwrName();
				s[9] = pool.getVtdrwrBankName();
				s[10] = pool.getVtpyeeName();
				s[11] = pool.getVtpyeeBankName();
				s[12] = this.rePayTypeName(pool.getPayType());
				s[13] = this.reRisklevelName(pool.getRickLevel());
				list.add(s);
			}
		}
		return list;
	}

	@Override
	public List findPoolAllExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[11];
				DraftPoolBaseBatch pool = (DraftPoolBaseBatch) res.get(i);
				s[0] = pool.getBatchNo();
				s[1] = pool.getPlRecSvcrNm();
				s[2] = this.reBillTypeName(pool.getPlDraftType());
				s[3] = this.reBillMediaName(pool.getPlDraftMedia());
				if (pool.getPlReqTime() != null) {
					s[4] = String.valueOf(pool.getPlReqTime()).substring(0, 10);
				}
				s[5] = pool.getPlApplyNm();
				s[6] = pool.getWorkerName();
				s[7] = pool.getPlCommId();
				s[8] = String.valueOf(pool.getTotalCharge());
				s[9] = String.valueOf(pool.getTotleBill());
				s[10] = this.rePlStatusName(pool.getPlStatus());
				list.add(s);
			}
		}
		return list;
	}

	// 取是否可转让标记中文名
	private String reVtTranSferName(String value) {
		if (value != null) {
			if (value.equals("EM00")) {
				return "可再转让";
			} else if (value.equals("EM01")) {
				return "不可再转让";
			}
		}
		return "";
	}

	// 取出入池票据状态标记中文名
	private String rePlStatusName(String value) {
		if (value != null) {
			if (value.equals("CC_00")) {
				return "新建数据";
			} else if (value.equals("CC_01")) {
				return "已发解质押申请";
			} else if (value.equals("CC_02")) {
				return "解质押申请代签收";
			} else if (value.equals("CC_03")) {
				return "已发解质押签收申请";
			} else if (value.equals("CC_04")) {
				return "解质押申请已签收";
			} else if (value.equals("CC_05")) {
				return "出池已记账";
			} else if (value.equals("CC_06")) {
				return "出池记账撤回";
			} else if (value.equals("RC_00")) {
				return "新建数据";
			} else if (value.equals("RC_01")) {
				return "已发质押申请";
			} else if (value.equals("RC_02")) {
				return "质押申请待签收";
			} else if (value.equals("RC_03")) {
				return "已发质押签收申请";
			} else if (value.equals("RC_04")) {
				return "质押申请已签收";
			} else if (value.equals("RC_05")) {
				return "入池已记账";
			}
		}
		return "";
	}

	@Override
	public PoolBillInfo queryObj(String billNo,String beginRangeNo, String endRangeNo) throws Exception {

		String sql = "select obj from PoolBillInfo obj where obj.SBillNo =? and obj.beginRangeNo =? and obj.endRangeNo =? ";
		List param = new ArrayList();
		param.add(billNo);
		param.add(beginRangeNo);
		param.add(endRangeNo);
		List list = this.find(sql, param);
		if (list != null && list.size() > 0) {
			return (PoolBillInfo) list.get(0);
		}
		return null;
	}

	@Override
	public List findPoolTraceAllExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[9];
				DraftPoolBase pool = (DraftPoolBase) res.get(i);
				s[0] = pool.getPlDraftNb();
				s[1] = pool.getPoolAgreement();
				s[2] = pool.getCustName();
				s[3] = String.valueOf(pool.getPlIsseAmt());
				if (pool.getPlIsseDt() != null) {
					s[4] = String.valueOf(pool.getPlIsseDt()).substring(0, 10);
				}else{
					s[4] = "";
				}
				if (pool.getPlDueDt() != null) {
					s[5] = String.valueOf(pool.getPlDueDt()).substring(0, 10);
				}else {
					s[5] = "";
				}
				s[6] = pool.getPlAccptrSvcrNm();
				s[7] = this.rePlStatus(pool.getPlStatus());
				if (pool.getPlReqTime() != null) {
					s[8] = String.valueOf(pool.getPlReqTime()).substring(0, 10);
				}else{
					s[8] = "";
				}
				list.add(s);
			}
		}
		return list;
	}

	public String rePlStatus(String value) {
		if (value != null) {
			if (value.equals(PoolComm.RC_00)) {
				return "新建数据";
			} else if (value.equals(PoolComm.RC_01)) {
				return "已发质押申请";
			} else if (value.equals(PoolComm.RC_02)) {
				return "质押申请待签收";
			} else if (value.equals(PoolComm.RC_03)) {
				return "已发质押签收申请";
			} else if (value.equals(PoolComm.RC_04)) {
				return "质押申请已签收";
			} else if (value.equals(PoolComm.RC_05)) {
				return "入池已记账";
			} else if (value.equals(PoolComm.CC_00)) {
				return "新建数据";
			} else if (value.equals(PoolComm.CC_02)) {
				return "已发解质押申请";
			} else if (value.equals(PoolComm.CC_03)) {
				return "解质押申请待签收";
			} else if (value.equals(PoolComm.CC_04)) {
				return "已发解质押签收申请";
			} else if (value.equals(PoolComm.CC_05)) {
				return "解质押申请已签收";
			} else if (value.equals(PoolComm.CC_01)) {
				return "出池已记账";
			} else if (value.equals(PoolComm.CC_05_1)) {
				return "解质押申请签收失败";
			} else if (value.equals(PoolComm.CC_05_2)) {
				return "解质押到期异常";
			} else if (value == "RC_06") {
				return "出池记账撤回";
			}
		}
		return "";
	}

	public String reTradeTypeName(String value) {
		if (value != null) {
			if (value.equals("YW_01")) {
				return "票据池业务";
			} else if (value.equals("YW_02")) {
				return "代保管业务";
			} else if (value.equals("YW_03")) {
				return "存单池业务";
			}
		}
		return "";

	}

	public PoolBillInfo loadByBillNo(String billNo,String beginRangeNo,String endRangeNo) {
		String sql = "select obj from PoolBillInfo obj where obj.SBillNo =? and obj.beginRangeNo =? and obj.endRangeNo =?";
		List param = new ArrayList();
		param.add(billNo);
		param.add(beginRangeNo);
		param.add(endRangeNo);
		List list = this.find(sql, param);
		if (list != null && list.size() > 0) {
			return (PoolBillInfo) list.get(0);
		}
		return null;
	}

	@Override
	public List<PoolBillInfo> queryPoolBillInfoByPram(PoolQueryBean bean) {
		// 该方法暂时只用了状态
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		List res = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select bill from PoolBillInfo  bill where 1=1 ");

		if (bean != null) {
			// 状态
			if (StringUtil.isNotBlank(bean.getSStatusFlag())) {
				hql.append(" and bill.SDealStatus =:SDealStatus");
				paramName.add("SDealStatus");
				paramValue.add(bean.getSStatusFlag());
			}
			// 去除的状态
			if (bean.getStatus() != null && bean.getStatus().size() > 0) {
				hql.append(" and  bill.SDealStatus not in( :SDealStatus)");
				paramName.add("SDealStatus");
				paramValue.add(bean.getStatus());
			}
			//核心客户号
			if (StringUtil.isNotBlank(bean.getCustomernumber())) {
				hql.append(" and bill.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(bean.getCustomernumber());
			}
			//票据池编号
			if(StringUtil.isNotBlank(bean.getProtocolNo())){
				hql.append(" and bill.poolAgreement =:poolAgreement");
				paramName.add("poolAgreement");
				paramValue.add(bean.getProtocolNo());
			}
			//介质
			if(StringUtil.isNotBlank(bean.getSBillMedia())){
				hql.append(" and bill.SBillMedia =:SBillMedia");
				paramName.add("SBillMedia");
				paramValue.add(bean.getSBillMedia());
			}
			//身份证号
			if(StringUtil.isNotBlank(bean.getEbankPeopleCard())){
				hql.append(" and bill.workerCard =:workerCard");
				paramName.add("workerCard");
				paramValue.add(bean.getEbankPeopleCard());
			}
			//经办人姓名
			if(StringUtil.isNotBlank(bean.getEbankName())){
				hql.append(" and bill.workerName =:workerName");
				paramName.add("workerName");
				paramValue.add(bean.getEbankName());
			}
			//经办人手机号
			if(StringUtil.isNotBlank(bean.getEbankType())){
				hql.append(" and bill.workerPhone =:workerPhone");
				paramName.add("workerPhone");
				paramValue.add(bean.getEbankType());
			}
			//纸票批次号
			if(StringUtil.isNotBlank(bean.getSBatchNo())){
				hql.append(" and bill.pOutBatchNo =:pOutBatchNo");
				paramName.add("pOutBatchNo");
				paramValue.add(bean.getSBatchNo());
			}
			//bbsp票据id
			if(StringUtil.isNotBlank(bean.getBusinessId())){
				hql.append(" and bill.discBillId =:discBillId");
				paramName.add("discBillId");
				paramValue.add(bean.getBusinessId());
			}
			//票号
			if(StringUtil.isNotBlank(bean.getBillNo())){
				hql.append(" and bill.SBillNo =:SBillNo");
				paramName.add("SBillNo");
				paramValue.add(bean.getBillNo());
			}
			
			//是否自动质押入池
			if(StringUtil.isNotBlank(bean.getZyFlag())){
				hql.append(" and bill.zyFlag =:zyFlag");
				paramName.add("zyFlag");
				paramValue.add(bean.getZyFlag());
			}
			// 电票签约账号
			if (bean.getAcctNoList() != null && bean.getAcctNoList().size() > 0) {
				hql.append(" and  bill.accNo  in( :acctNoList)");
				paramName.add("acctNoList");
				paramValue.add(bean.getAcctNoList());
			}
			// 不得转让标记
			if(StringUtil.isNotBlank(bean.getIsnotFlag())){
				hql.append(" and  bill.SBanEndrsmtFlag  =:SBanEndrsmtFlag");
				paramName.add("SBanEndrsmtFlag");
				paramValue.add(bean.getIsnotFlag());
			}
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues);
		if (res != null && res.size() > 0) {
			return res;
		}
		return null;

	}


	// 根据解决同步借据余额
	public PedCreditDetail txUpdateLoanByCoreforQuery(PedCreditDetail detail) throws Exception {
		String loanType = detail.getLoanType();// 信贷业务类型 （XD_01:银承 XD_02:流贷 XD_03:保函 XD_04:信用证 XD_05:表外业务垫款）
		String crdtNo = detail.getCrdtNo();// 信贷产品号
		
		logger.info("同步主业务合同【" + crdtNo + "】下的借据【" + detail.getLoanNo() + "】信息开始...");
		
		CreditProduct product = poolCreditProductService.queryProductByCreditNo(crdtNo,PoolComm.JQZT_WJQ);// 主业务合同
		
		//同步核心借据状态
		detail = poolCreditProductService.txSynchroLoan(detail, product, loanType);
		
		logger.info("同步主业务合同【" + crdtNo + "】下的借据【" + detail.getLoanNo() + "】信息完成！");

		
		return detail;

	}

	@Override
	public void txDeleteDraftAccountManagement(String bpsNo,String custNo) throws Exception {
		logger.debug("删除账务管家表信息开始...");
		String today = DateUtils.toString(DateUtils.getWorkDayDate(), DateUtils.ORA_DATE_FORMAT);
		String sql = "delete  from draft_account_management t where t.draft_nb not in (  " +
				"   select a.draft_nb from draft_account_management a  " +
				"   where a.data_source ='SRC_02' and a.due_dt >= to_date('"+today+"', 'yyyyMMdd') " +
			    ")";
		if(StringUtil.isNotBlank(bpsNo)){
			sql=sql+" and BPS_NO ='"+bpsNo+"'";
		}
		if(StringUtil.isNotBlank(custNo)){
			sql=sql+" and CUST_NO ='"+custNo+"'";
		}
		ConnectionUtils.toExecuteUpdateSql(sql);
		logger.debug("删除账务管家表信息结束...");
		
	}
	
	@Override
	public List<PedCheckBatch> queryCheckBatchByPram(PoolQueryBean bean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		List res = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select batch from PedCheckBatch  batch where 1=1 ");

		if (bean != null) {

			//核心客户号
			if (StringUtil.isNotBlank(bean.getCustomernumber())) {
				hql.append(" and batch.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(bean.getCustomernumber());
			}
			
			//对账日期
			if (bean.getAccDate()!=null) {
				hql.append(" and batch.accountDate =:accountDate");
				paramName.add("accountDate");
				paramValue.add(bean.getAccDate());
			}

		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues);
		if (res != null && res.size() > 0) {
			return res;
		}
		return null;

	}

	@Override
	public List<PlPdraftBatch> queryPlPdraftBatchByBatch(PoolQueryBean queryBean)
			throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		List res = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select batch from PlPdraftBatch  batch where 1=1 ");
		//核心客户号
		if(StringUtil.isNotBlank(queryBean.getCustomernumber())){
			hql.append(" and batch.custNo =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustomernumber());
		}
		//票据池编号
		if(StringUtil.isNotBlank(queryBean.getProtocolNo())){
			hql.append(" and batch.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getProtocolNo());
		}
		//批次号
		if(StringUtil.isNotBlank(queryBean.getSBatchNo())){
			hql.append(" and batch.batchNo =:batchNo");
			paramName.add("batchNo");
			paramValue.add(queryBean.getSBatchNo());
		}
		//身份证号
		if(StringUtil.isNotBlank(queryBean.getEbankPeopleCard())){
			hql.append(" and batch.workerId =:workerId");
			paramName.add("workerId");
			paramValue.add(queryBean.getEbankPeopleCard());
		}
		//经办人姓名
		if(StringUtil.isNotBlank(queryBean.getPlApplyNm())){
			hql.append(" and batch.workerName =:workerName");
			paramName.add("workerName");
			paramValue.add(queryBean.getPlApplyNm());
		}
		//s是否生效  1 生效 0 失效
		if(StringUtil.isNotBlank(queryBean.getSStatusFlag())) {
			hql.append(" and batch.status =:status");
			paramName.add("status");
			paramValue.add(queryBean.getSStatusFlag());
		}
		//是否出池 1批次未出池  0批次已出池
		if(StringUtil.isNotBlank(queryBean.getIsPoolOutEnd())) {
			hql.append(" and batch.isPoolOutEnd =:isPoolOutEnd");
			paramName.add("isPoolOutEnd");
			paramValue.add(queryBean.getIsPoolOutEnd());
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues);
		if (res != null && res.size() > 0) {
			return res;
		}
		return null;
	}

	@Override
	public QueryResult queryPlPdraftBatchByBatch(PoolQueryBean queryBean, Page page) throws Exception {
		QueryResult queryResult = new QueryResult();

		List records = new ArrayList();
		List poolLists = new ArrayList();

		String amountFieldName = "totalAmt";
		records = this.queryPlPdraftBatchByBatchs(queryBean, page);
		if (records != null && records.size() > 0) {
			queryResult = QueryResult.buildQueryResult(records, amountFieldName);
		} else {
			return null;
		}
		return queryResult;
	}

	@Override
	public QueryResult queryPoolBillInfoByPram(PoolQueryBean queryBean, Page page) throws Exception {
		QueryResult queryResult = new QueryResult();

		List records = new ArrayList();
		List poolLists = new ArrayList();

		String amountFieldName = "FBillAmount";
		records = this.queryPoolBillInfoList(queryBean, page);
		if (records != null && records.size() > 0) {
			queryResult = QueryResult.buildQueryResult(records, amountFieldName);
		} else {
			return null;
		}
		return queryResult;
	}


	public List<PlPdraftBatch> queryPlPdraftBatchByBatchs(PoolQueryBean queryBean, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		List res = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select batch from PlPdraftBatch  batch where 1=1 ");
		//核心客户号
		if(StringUtil.isNotBlank(queryBean.getCustomernumber())){
			hql.append(" and batch.custNo =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustomernumber());
		}
		//票据池编号
		if(StringUtil.isNotBlank(queryBean.getProtocolNo())){
			hql.append(" and batch.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getProtocolNo());
		}
		//批次号
		if(StringUtil.isNotBlank(queryBean.getSBatchNo())){
			hql.append(" and batch.batchNo =:batchNo");
			paramName.add("batchNo");
			paramValue.add(queryBean.getSBatchNo());
		}
		//身份证号
		if(StringUtil.isNotBlank(queryBean.getEbankPeopleCard())){
			hql.append(" and batch.workerId =:workerId");
			paramName.add("workerId");
			paramValue.add(queryBean.getEbankPeopleCard());
		}
		//批次生效状态
		if(StringUtil.isNotBlank(queryBean.getIsPoolOutEnd())){
			hql.append(" and batch.status =:Status");
			paramName.add("Status");
			paramValue.add(queryBean.getIsPoolOutEnd());
		}
		hql.append(" order by batch.id desc  ");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		res = this.find(hql.toString(), paramNames, paramValues,page);
		if (res != null && res.size() > 0) {
			return res;
		}
		return null;
	}

	public List<PoolBillInfo> queryPoolBillInfoList(PoolQueryBean queryBean,Page page) {
		// 该方法暂时只用了状态
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		List res = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("select bill from PoolBillInfo  bill where 1=1 ");

		if (queryBean != null) {
			// 状态
			if (StringUtil.isNotBlank(queryBean.getSStatusFlag())) {
				hql.append(" and bill.SDealStatus =:SDealStatus");
				paramName.add("SDealStatus");
				paramValue.add(queryBean.getSStatusFlag());
			}
			// 去除的状态
			if (queryBean.getStatus() != null && queryBean.getStatus().size() > 0) {
				hql.append(" and  bill.SDealStatus not in( :SDealStatus)");
				paramName.add("SDealStatus");
				paramValue.add(queryBean.getStatus());
			}
			//核心客户号
			if (StringUtil.isNotBlank(queryBean.getCustomernumber())) {
				hql.append(" and bill.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustomernumber());
			}
			//票据池编号
			if(StringUtil.isNotBlank(queryBean.getProtocolNo())){
				hql.append(" and bill.poolAgreement =:poolAgreement");
				paramName.add("poolAgreement");
				paramValue.add(queryBean.getProtocolNo());
			}
			//介质
			if(StringUtil.isNotBlank(queryBean.getSBillMedia())){
				hql.append(" and bill.SBillMedia =:SBillMedia");
				paramName.add("SBillMedia");
				paramValue.add(queryBean.getSBillMedia());
			}
			//身份证号
			if(StringUtil.isNotBlank(queryBean.getEbankPeopleCard())){
				hql.append(" and bill.workerCard =:workerCard");
				paramName.add("workerCard");
				paramValue.add(queryBean.getEbankPeopleCard());
			}
			//经办人姓名
			if(StringUtil.isNotBlank(queryBean.getEbankName())){
				hql.append(" and bill.workerName =:workerName");
				paramName.add("workerName");
				paramValue.add(queryBean.getEbankName());
			}
			//经办人手机号
			if(StringUtil.isNotBlank(queryBean.getEbankType())){
				hql.append(" and bill.workerPhone =:workerPhone");
				paramName.add("workerPhone");
				paramValue.add(queryBean.getEbankType());
			}
			//纸票批次号
			if(StringUtil.isNotBlank(queryBean.getSBatchNo())){
				hql.append(" and bill.pOutBatchNo =:pOutBatchNo");
				paramName.add("pOutBatchNo");
				paramValue.add(queryBean.getSBatchNo());
			}
			//bbsp票据id
			if(StringUtil.isNotBlank(queryBean.getBusinessId())){
				hql.append(" and bill.discBillId =:discBillId");
				paramName.add("discBillId");
				paramValue.add(queryBean.getBusinessId());
			}

		}
		hql.append(" order by bill.id desc ,bill.DDueDt desc ");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues ,page);
		if (res != null && res.size() > 0) {
			return res;
		}
		return null;
	}
	
	@Override
	public List<QueryPedListBean> queryPedListBeanDetail(QueryPedListBean bean,Page page) throws Exception {
		List list =new ArrayList();
		
		String sql = "select pdl from PedProtocolList as pdl where 1=1 ";
		List<String> param = new ArrayList<String>();
		if(null != bean){
			if(StringUtils.isNotEmpty(bean.getBpsNo())){
				sql = sql + " and pdl.bpsNo = ? ";
				param.add(bean.getBpsNo());
			}
		}
		sql = sql + " and pdl.status = ? ";
		param.add(PoolComm.PRO_LISE_STA_01);
		List<PedProtocolList> result = this.find(sql, param,page);
		
		ProtocolQueryBean queryBeanPed = new ProtocolQueryBean();
		queryBeanPed.setPoolAgreement(bean.getBpsNo());
		queryBeanPed.setIsGroup(PoolComm.YES);
		queryBeanPed.setOpenFlag(PoolComm.OPEN_01);
		PedProtocolDto  protocol =this.queryProtocolDtoByQueryBean(queryBeanPed);
		
		if(result != null && result.size()>0){
			for(int i=0;i<result.size();i++){
				PedProtocolList ped = result.get(i);
				QueryPedListBean queryBean =new QueryPedListBean();
				
				queryBean.setCustNo(ped.getCustNo());//客户号
				queryBean.setCustName(ped.getCustName());//客户名称
				queryBean.setOrgCoge(ped.getOrgCoge());//组织机构代码
				queryBean.setCustIdentity(ped.getCustIdentity());//客户类型
				
				EduResult eduResult =pedAssetPoolService.queryEduMember(ped.getBpsNo(), ped.getCustNo());
				
				if(PoolComm.JS_01.equals(ped.getRole())){//主户
					AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(protocol, PoolComm.ED_BZJ_HQ);
					queryBean.setTotalBillAmount(eduResult.getTotalBillAmount());//总额度
					queryBean.setLowRiskAmount(eduResult.getLowRiskAmount());//低风险票据总额度
					queryBean.setHighRiskAmount(eduResult.getHighRiskAmount());//高风险票据总额度
					BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());//总用
					if(PoolComm.KHLX_01.equals(ped.getCustIdentity()) && allUsed.compareTo(BigDecimal.ZERO)>0){
						queryBean.setUsedLimitAmt(allUsed);//已使用额度
						
					}else if(PoolComm.KHLX_03.equals(ped.getCustIdentity())){
						queryBean.setAllMaxLimitAmt(ped.getMaxFinancLimit());//分配额度限额
						queryBean.setAllLimitAmt(ped.getFinancLimit());//分配额度
						queryBean.setUsedLimitAmt(allUsed);//已使用额度
						queryBean.setRemainAvailLimitAmt(ped.getFinancLimit().subtract(allUsed));//剩余可用额度
					}	
					
				}else{//分户
					if(PoolComm.KHLX_01.equals(ped.getCustIdentity())){//出质人（出质人一定有出质信息，但是有可能有融资信息【双重身份客户融资解约导致】）
						
						queryBean.setTotalBillAmount(eduResult.getTotalBillAmount());//总额度
						queryBean.setLowRiskAmount(eduResult.getLowRiskAmount());//低风险票据总额度
						queryBean.setHighRiskAmount(eduResult.getHighRiskAmount());//高风险票据总额度
						BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
						if(allUsed.compareTo(BigDecimal.ZERO)>0){
							queryBean.setUsedLimitAmt(allUsed);//已使用额度
						}
						
					}else if(PoolComm.KHLX_02.equals(ped.getCustIdentity())){//融资人（只有融资信息）
						
						BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
						queryBean.setAllMaxLimitAmt(ped.getMaxFinancLimit());//分配额度限额
						queryBean.setAllLimitAmt(ped.getFinancLimit());//分配额度
						queryBean.setUsedLimitAmt(allUsed);//已使用额度
						queryBean.setRemainAvailLimitAmt(ped.getFinancLimit().subtract(allUsed));//剩余可用额度
						
					}else if(PoolComm.KHLX_03.equals(ped.getCustIdentity())){//出质人+融资人（出质信息+融资信息）
						
						queryBean.setTotalBillAmount(eduResult.getTotalBillAmount());//总额度
						queryBean.setLowRiskAmount(eduResult.getLowRiskAmount());//低风险票据总额度
						queryBean.setHighRiskAmount(eduResult.getHighRiskAmount());//高风险票据总额度
						
						BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
						queryBean.setAllMaxLimitAmt(ped.getMaxFinancLimit());//分配额度限额
						queryBean.setAllLimitAmt(ped.getFinancLimit());//分配额度
						queryBean.setUsedLimitAmt(allUsed);//已使用额度
						queryBean.setRemainAvailLimitAmt(ped.getFinancLimit().subtract(allUsed));//剩余可用额度
						
					}else if(PoolComm.KHLX_04.equals(ped.getCustIdentity())){//签约客户（可能有融资信息【融资人解约导致】）
						
						BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
						if(allUsed.compareTo(BigDecimal.ZERO)>0){
							queryBean.setUsedLimitAmt(allUsed);//已使用额度
						}
					}
				}
				list.add(queryBean);
			}
			return list;
		}
		return null;
	}

	@Override
	public void txDeleteAccountManagement(String status, String custNo,String eleAccNos) throws Exception {
		
		logger.debug("删除账务管家表信息开始...");
		
		if(StringUtil.isNotBlank(eleAccNos)){
			String[] nos = eleAccNos.split("\\|");
			for(String no : nos){
				
				String today = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
				String sql = "delete  from draft_account_management t where 1=1 ";
				if(StringUtil.isNotBlank(status)){
					sql=sql+" and STATUS_FLAG ='"+status+"'";
				}
				if(StringUtil.isNotBlank(custNo)){
					sql=sql+" and CUST_NO ='"+custNo+"'";
				}
				sql=sql+" and DATA_SOURCE ='"+PoolComm.SRC_01+"'";
				sql=sql+" and ELEC_DRAFT_ACCOUNT ='"+no+"'";
				ConnectionUtils.toExecuteUpdateSql(sql);
			}
		}
		
		logger.debug("删除账务管家表信息结束...");
	}

	public PedProtocolDto queryProtocolDtoByQueryBean(
			ProtocolQueryBean queryBean) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedProtocolDto as dto where 1=1 ");
		
		if(queryBean!=null){
			if(StringUtil.isNotBlank(queryBean.getPoolAgreement())){//票据池编号
				hql.append(" and dto.poolAgreement =:poolAgreement");
				paramName.add("poolAgreement");
				paramValue.add(queryBean.getPoolAgreement());
				
			}
			if(StringUtil.isNotBlank(queryBean.getIsGroup())){//是否集团	1集团  0单户
				hql.append(" and dto.isGroup =:isGroup");
				paramName.add("isGroup");
				paramValue.add(queryBean.getIsGroup());
			}
			if(StringUtil.isNotBlank(queryBean.getOpenFlag())){//签约状态    00：未签约  01：已签约    02：已解约
				hql.append(" and dto.openFlag =:openFlag");
				paramName.add("openFlag");
				paramValue.add(queryBean.getOpenFlag());
			}
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolDto> result = this.find(hql.toString(), paramNames, paramValues );

		if(result!=null&&result.size()>0){
			PedProtocolDto dto = result.get(0);
			return dto;	
		}
		return null;
	}

	@Override
	public AssetPool queryAssetPoolByBpsNo(String bpsNo) throws Exception {
		AssetPool assetPool = new AssetPool();
		// 通过客户对象得到打折费率
		String sql = " from AssetPool ap where ap.bpsNo ='" + bpsNo + "'";
		List result = this.find(sql);
		if (result != null && result.size() > 0) {
			assetPool = (AssetPool) result.get(0);
		}
		return assetPool;
	}

	@Override
	public void txDelectRepeatDraftAccountManagement() throws Exception {
		
		logger.info("删除账务管家表重复数据...开始...");
		String today = DateUtils.toString(DateUtils.getWorkDayDate(), DateUtils.ORA_DATE_FORMAT);
		String sql =
				" delete FROM draft_account_management t  " +
				" where (t.draft_nb,t.begin_Range_No,t.end_Range_No, t.cust_no) in " +
				" ( select p.draft_nb,p.begin_Range_No,p.end_Range_No,p.cust_no from draft_account_management p group by p.draft_nb,p.begin_Range_No,p.end_Range_No,p.cust_no having count (*)>1) " +
				" and t.rowid not in " +
				" (select min(q.rowid) from draft_account_management q group by q.draft_nb,q.begin_Range_No,q.end_Range_No,q.cust_no having count(*)>1) " ;
		ConnectionUtils.toExecuteUpdateSql(sql);
		logger.info("删除账务管家表重复数据...结束...");
		
	}
	// 设置 出入池中文名称
	private String reAssetStatusName(String value) {
		HashMap map  = new HashMap();
		map.put("DS_00","未处理");
		map.put("DS_01","入池处理中");
		map.put("DS_02","已入池");
		map.put("DS_03","出池处理中");
		map.put("DS_04","已出池");
		map.put("DS_05","签收处理中");
		map.put("DS_06","到期处理中");
		map.put("DS_07","托收已签收");
		map.put("DS_08","已拒绝");
		map.put("DS_09","已驳回");
		map.put("DS_99","不可入池票据");
		map.put("DS_10","贴现处理中");
		map.put("DS_11","贴现已完成");
		return  map.containsKey(value) ? (String) map.get(value) : "";
	}


	private String reRisklevelName(String value) {
		HashMap map  = new HashMap();
		map.put("FX_01","低风险");
		map.put("FX_02","高风险");
		map.put("FX_03","未产生额度");
		return  map.containsKey(value) ? (String) map.get(value) : "";
	}
	
	/**
	 * 入池交易明细查询
	 * @param  入
	 * @param page
	 * @return
	 * @throws Exception
	 * @author gcj
	 * @date 20210429
	 */
	public List<DraftPoolIn> queryDraftPoolIn(DraftQueryBean bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append(
				" from DraftPoolIn po,PedProtocolDto pp,PoolBillInfo bill where 1=1 and po.poolAgreement=pp.poolAgreement and " +
				"po.plDraftNb=bill.SBillNo and po.beginRangeNo = bill.beginRangeNo and po.endRangeNo = bill.endRangeNo ");
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and pp.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.plDraftNb like :plDraftNb");
			paramName.add("plDraftNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		/********************融合改造新增 start******************************/
		//子票区间起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		//子票区间止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}		
		
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		

		/********************融合改造新增 end******************************/
		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia())) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())) {
			hql.append(" and po.plDraftType=:assetType");
			paramName.add("assetType");
			paramValue.add(bean.getAssetType());
		}

		// 企业组织机构代码plCommId
		if (bean.getAssetCommId() != null && !"".equals(bean.getAssetCommId())) {
			hql.append(" and po.assetCommId like :assetCommId");
			paramName.add("assetCommId");
			paramValue.add("%" + bean.getAssetCommId() + "%");
		}

		// 出票日期开始startplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and po.plIsseDt>=TO_DATE(:plstartDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plstartDt");
			paramValue.add(DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 出票日期结束endplIsseDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and po.plIsseDt<=TO_DATE(:plIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plIsseDt");
			paramValue.add(DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 到期日期开始startplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plDueDt>=TO_DATE(:plstartDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plstartDueDt");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日期结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plDueDt<=TO_DATE(:plDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plDueDt");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 是否已产生额度isEduExist
		if (bean.getIsEduExist() != null && !"".equals(bean.getIsEduExist())) {
			hql.append(" and po.isEduExist=:isEduExist");
			paramName.add("isEduExist");
			paramValue.add(bean.getIsEduExist());
			if("1".equals(bean.getIsEduExist())){
				hql.append(" and po.rickLevel !=:rickLevel2");//额度类型
				paramName.add("rickLevel2");
				paramValue.add(PoolComm.NOTIN_RISK);
			}
		}
//		//状态
//		if (bean.getAssetStatus() != null && !"".equals(bean.getAssetStatus())) {
//			hql.append(" and po.assetStatus=:SDealStatus");//票据状态
//			paramName.add("SDealStatus");
//			paramValue.add(bean.getAssetStatus());
//		}else{//没有状态走
//			List status = new ArrayList();
//			status.add(PoolComm.DS_02);
//			status.add(PoolComm.DS_06);
//			hql.append(" and po.assetStatus in (:SDealStatus)");//票据状态
//			paramName.add("SDealStatus");
//			paramValue.add(status);
//		}
		// 额度类型rickLevel
		if (bean.getRickLevel() != null && !"".equals(bean.getRickLevel())) {
			hql.append(" and po.rickLevel=:rickLevel");
			paramName.add("rickLevel");
			paramValue.add(bean.getRickLevel());
		}

		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and po.plIsseAmt>=:startAmt");
			paramName.add("startAmt");
			paramValue.add(bean.getStartassetAmt());
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and po.plIsseAmt<=:assetAmt");
			paramName.add("assetAmt");
			paramValue.add(bean.getEndassetAmt());
		}
		//票据池编号
		if(bean.getPoolAgreement() !=  null && !"".equals(bean.getPoolAgreement())){
			hql.append(" and po.poolAgreement =:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		//核心客户号
		if(bean.getCustNo() !=  null && !"".equals(bean.getCustNo())){
			hql.append(" and po.custNo =:custNo");
			paramName.add("poolAgreement");
			paramValue.add(bean.getCustNo());
		}
		//出票人 开户行名称
		if(bean.getPlDrwrAcctSvcrNm() !=  null && !"".equals(bean.getPlDrwrAcctSvcrNm())){
			hql.append(" and po.plDrwrAcctSvcrNm like :plDrwrAcctSvcrNm");
			paramName.add("plDrwrAcctSvcrNm");
			paramValue.add("%"+bean.getPlDrwrAcctSvcrNm()+"%");
		}	
		// 操作日期开始
		if (bean.getStartplReqTime() != null && !"".equals(bean.getStartplReqTime())) {
			hql.append(" and po.plReqTime>=TO_DATE(:plReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plReqTime");
			paramValue.add(DateUtils.toString(bean.getStartplReqTime(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 操作日期结束
		if (bean.getEndplReqTime() != null && !"".equals(bean.getEndplReqTime())) {
			hql.append(" and po.plReqTime<=TO_DATE(:endplReqTim, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("endplReqTim");
			paramValue.add(DateUtils.toString(bean.getEndplReqTime(), "yyyy-MM-dd") + " 23:59:59");
		}		
		hql.append(" order by po.plDueDt desc");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		List retList = new ArrayList();
		if (res != null && res.size() > 0) {
			DraftQueryBeanQuery beanNew = null;
			DraftPoolIn info = null;
			PoolBillInfo bill =null;
			for (int i = 0; i < res.size(); i++) {
				Object[] obj = (Object[]) res.get(i);
				beanNew = new DraftQueryBeanQuery();
				info = (DraftPoolIn) obj[0];
				bill =(PoolBillInfo)obj[2];
				//BeanUtils.copyProperties(beanNew, info);
				BeanUtil.copyValue(info, beanNew);
				PedProtocolDto ped= this.queryProtocolDtoBypoolAgreement(beanNew.getPoolAgreement());
				beanNew.setPoolName(ped.getPoolName());
				beanNew.setSBanEndrsmtFlag(bill.getSBanEndrsmtFlag());
				retList.add(beanNew);
			}
		}
		return retList;
	}
	
	/**
	 * 出池交易明细查询
	 * @param  入
	 * @param page
	 * @return
	 * @throws Exception
	 * @author gcj
	 * @date 20210429
	 */
	public List<DraftPoolOut> queryDraftPoolOut(DraftQueryBean bean, User user, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append(
				" from DraftPoolOut po,PedProtocolDto pp,PoolBillInfo bill where 1=1 and po.poolAgreement=pp.poolAgreement" +
				" and po.plDraftNb=bill.SBillNo and po.beginRangeNo=bill.beginRangeNo and po.endRangeNo=bill.endRangeNo");
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and pp.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.plDraftNb like :plDraftNb");
			paramName.add("plDraftNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		/********************融合改造新增 start******************************/
		//子票区间起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%" + bean.getBeginRangeNo() + "%");
		}
		//子票区间止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%" + bean.getEndRangeNo() + "%");
		}
		
		
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		

		
		/********************融合改造新增 end******************************/
		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia())) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())) {
			hql.append(" and po.plDraftType=:assetType");
			paramName.add("assetType");
			paramValue.add(bean.getAssetType());
		}

		// 企业组织机构代码plCommId
		if (bean.getAssetCommId() != null && !"".equals(bean.getAssetCommId())) {
			hql.append(" and po.assetCommId like :assetCommId");
			paramName.add("assetCommId");
			paramValue.add("%" + bean.getAssetCommId() + "%");
		}

		// 出票日期开始startplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and po.plIsseDt>=TO_DATE(:plstartDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plstartDt");
			paramValue.add(DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 出票日期结束endplIsseDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and po.plIsseDt<=TO_DATE(:plIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plIsseDt");
			paramValue.add(DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 到期日期开始startplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plDueDt>=TO_DATE(:plstartDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plstartDueDt");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日期结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plDueDt<=TO_DATE(:plDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plDueDt");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}

		// 是否已产生额度isEduExist
		if (bean.getIsEduExist() != null && !"".equals(bean.getIsEduExist())) {
			hql.append(" and po.isEduExist=:isEduExist");
			paramName.add("isEduExist");
			paramValue.add(bean.getIsEduExist());
			if("1".equals(bean.getIsEduExist())){
				hql.append(" and po.rickLevel !=:rickLevel2");//额度类型
				paramName.add("rickLevel2");
				paramValue.add(PoolComm.NOTIN_RISK);
			}
		}
//		//状态
//		if (bean.getAssetStatus() != null && !"".equals(bean.getAssetStatus())) {
//			hql.append(" and po.assetStatus=:SDealStatus");//票据状态
//			paramName.add("SDealStatus");
//			paramValue.add(bean.getAssetStatus());
//		}else{//没有状态走
//			List status = new ArrayList();
//			status.add(PoolComm.DS_02);
//			status.add(PoolComm.DS_06);
//			hql.append(" and po.assetStatus in (:SDealStatus)");//票据状态
//			paramName.add("SDealStatus");
//			paramValue.add(status);
//		}
		// 额度类型rickLevel
		if (bean.getRickLevel() != null && !"".equals(bean.getRickLevel())) {
			hql.append(" and po.rickLevel=:rickLevel");
			paramName.add("rickLevel");
			paramValue.add(bean.getRickLevel());
		}

		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and po.plIsseAmt>=:startAmt");
			paramName.add("startAmt");
			paramValue.add(bean.getStartassetAmt());
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and po.plIsseAmt<=:assetAmt");
			paramName.add("assetAmt");
			paramValue.add(bean.getEndassetAmt());
		}
		//票据池编号
		if(bean.getPoolAgreement() !=  null && !"".equals(bean.getPoolAgreement())){
			hql.append(" and po.poolAgreement =:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		//核心客户号
		if(bean.getCustNo() !=  null && !"".equals(bean.getCustNo())){
			hql.append(" and po.custNo =:custNo");
			paramName.add("poolAgreement");
			paramValue.add(bean.getCustNo());
		}
		//出票人 开户行名称
		if(bean.getPlDrwrAcctSvcrNm() !=  null && !"".equals(bean.getPlDrwrAcctSvcrNm())){
			hql.append(" and po.plDrwrAcctSvcrNm like :plDrwrAcctSvcrNm");
			paramName.add("plDrwrAcctSvcrNm");
			paramValue.add("%"+bean.getPlDrwrAcctSvcrNm()+"%");
		}	
		// 操作日期开始
		if (bean.getStartplReqTime() != null && !"".equals(bean.getStartplReqTime())) {
			hql.append(" and po.plReqTime>=TO_DATE(:plReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("plReqTime");
			paramValue.add(DateUtils.toString(bean.getStartplReqTime(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 操作日期结束
		if (bean.getEndplReqTime() != null && !"".equals(bean.getEndplReqTime())) {
			hql.append(" and po.plReqTime<=TO_DATE(:endplReqTim, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("endplReqTim");
			paramValue.add(DateUtils.toString(bean.getEndplReqTime(), "yyyy-MM-dd") + " 23:59:59");
		}		
		hql.append(" order by po.plDueDt desc");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		List retList = new ArrayList();
		if (res != null && res.size() > 0) {
			DraftQueryBeanQuery beanNew = null;
			DraftPoolOut info = null;
			PoolBillInfo bill=null;
			for (int i = 0; i < res.size(); i++) {
				Object[] obj = (Object[]) res.get(i);
				beanNew = new DraftQueryBeanQuery();
				info = (DraftPoolOut) obj[0];
				bill = (PoolBillInfo)obj[2];
				//BeanUtils.copyProperties(beanNew, info);
				BeanUtil.copyValue(info, beanNew);
				PedProtocolDto ped= this.queryProtocolDtoBypoolAgreement(beanNew.getPoolAgreement());
				beanNew.setPoolName(ped.getPoolName());
				beanNew.setSBanEndrsmtFlag(bill.getSBanEndrsmtFlag());
				retList.add(beanNew);
			}
		}
		return retList;
	}
	
	public String findCrdtProductAcptDetail(CreditProductQueryBean bean, Page page, User user) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer dhql = new StringBuffer();

		StringBuffer hql = new StringBuffer();
		dhql.append(
				"from PedProtocolDto dto,CreditProduct cp,PedCreditDetail pcd,PlOnlineAcptDetail pad,PedOnlineAcptProtocol pop where 1=1  and cp.bpsNo = dto.poolAgreement and cp.crdtNo=pcd.crdtNo and pcd.loanNo=pad.loanNo and pop.bpsId=dto.poolInfoId ");

		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and dto.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		/********************融合改造新增 start******************************/
		if(StringUtil.isNotBlank(bean.getBeginRangeNo())){
			hql.append(" and pad.beginRangeNo like:beginRangeNo ");
			paramName.add("beginRangeNo");
			paramValue.add("%"+bean.getBeginRangeNo()+"%");
		}
		if(StringUtil.isNotBlank(bean.getEndRangeNo())){
			hql.append(" and pad.endRangeNo like:endRangeNo ");
			paramName.add("endRangeNo");
			paramValue.add("%"+bean.getEndRangeNo()+"%");
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and pad.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (pad.draftSource is null or pad.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and pad.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and pad.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/********************融合改造新增 end******************************/
		
		// 风险等级
		if (StringUtil.isNotEmpty(bean.getRisklevel())) {
			hql.append(" and cp.risklevel =:risklevel");
			paramName.add("risklevel");
			paramValue.add(bean.getRisklevel());
		}		
		
		// 客户号
		if (StringUtil.isNotEmpty(bean.getCustnumber())) {
			hql.append(" and dto.custnumber = :custnumber");
			paramName.add("custnumber");
			paramValue.add(bean.getCustnumber());
		}

		// 融资人名称
		if (StringUtil.isNotEmpty(bean.getCustName())) {
			hql.append(" and cp.custName like :custName");
			paramName.add("custName");
			paramValue.add("%" + bean.getCustName() + "%");
		}

		// 业务品种
		if (StringUtil.isNotEmpty(bean.getCrdtType())) {
			hql.append(" and pcd.loanType=:crdtType");
			paramName.add("crdtType");
			paramValue.add(bean.getCrdtType());
		}

		// 合同号
		if (StringUtil.isNotEmpty(bean.getCrdtNo())) {
			hql.append(" and cp.crdtNo like :crdtNo");
			paramName.add("crdtNo");
			paramValue.add("%" + bean.getCrdtNo() + "%");
		}

		// 结清标记
		if (StringUtil.isNotEmpty(bean.getSttlFlag())) {
			hql.append(" and cp.sttlFlag=:sttlFlag");
			paramName.add("sttlFlag");
			paramValue.add(bean.getSttlFlag());
		}

		// 业务状态
		if (StringUtil.isNotEmpty(bean.getCrdtStatus())) {
			hql.append(" and pcd.loanStatus = :loanStatus");
			paramName.add("loanStatus");
			paramValue.add(bean.getCrdtStatus());
		}

		// 合同起始日期
		if (bean.getStartCrdtIssDt() != null) {
			hql.append(" and cp.crdtIssDt>=:aplyDtStart1");
			paramName.add("aplyDtStart1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartCrdtIssDt()));
		}
		// 合同起始日期
		if (bean.getEndCrdtIssDt() != null) {
			hql.append(" and cp.crdtIssDt<=:aplyDtStart2");
			paramName.add("aplyDtStart2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndCrdtIssDt()));
		}
		// 合同到期日期
		if (bean.getStartCrdtDueDt() != null) {
			hql.append(" and cp.crdtDueDt>=:aplyDtEnd1");
			paramName.add("aplyDtEnd1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartCrdtDueDt()));
		}
		// 合同到期日期
		if (bean.getEndCrdtDueDt() != null) {
			hql.append(" and cp.crdtDueDt<=:aplyDtEnd2");
			paramName.add("aplyDtEnd2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndCrdtDueDt()));
		}
		// 借据号
		if (StringUtil.isNotEmpty(bean.getLoanNo())) {
			hql.append(" and pcd.loanNo like :loanNo");
			paramName.add("loanNo");
			paramValue.add("%" + bean.getLoanNo() + "%");
		}
		// 借据起始日期
		if (bean.getStartTime1() != null) {
			hql.append(" and pcd.startTime>=:startTime1");
			paramName.add("startTime1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getStartTime1()));
		}
		// 借据起始日期
		if (bean.getStartTime2() != null) {
			hql.append(" and pcd.startTime<=:startTime2");
			paramName.add("startTime2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getStartTime2()));
		}
		// 借据到期日期
		if (bean.getEndTime1() != null) {
			hql.append(" and pcd.endTime>=:endTime1");
			paramName.add("endTime1");
			paramValue.add(DateUtils.getCurrentDayStartDate(bean.getEndTime1()));
		}
		// 借据到期日期
		if (bean.getEndTime2() != null) {
			hql.append(" and pcd.endTime<=:endTime2");
			paramName.add("endTime2");
			paramValue.add(DateUtils.getCurrentDayEndDate(bean.getEndTime2()));
		}
		// 借据金额开始
		if (bean.getStartLoanAmount() != null) {
			hql.append(" and pcd.loanAmount>=:startAmount");
			paramName.add("startAmount");
			paramValue.add(bean.getStartLoanAmount());
		}
		// 借据金额结束
		if (bean.getEndLoanAmount() != null) {
			hql.append(" and pcd.loanAmount<=:loanAmount");
			paramName.add("loanAmount");
			paramValue.add(bean.getEndLoanAmount());
		}
		//票据池编号
		if(StringUtil.isNotEmpty(bean.getPoolAgreement())){
			hql.append(" and dto.poolAgreement = :poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		//票号
		if(StringUtil.isNotEmpty(bean.getBillNo())){
			hql.append(" and pad.billNo = :billNo");
			paramName.add("billNo");
			paramValue.add(bean.getBillNo());
		}
		//是否在线业务
		if(StringUtil.isNotEmpty(bean.getIfOnline())){
			hql.append(" and cp.ifOnline = :ifOnline");
			paramName.add("ifOnline");
			paramValue.add(bean.getIfOnline());
		}
		//是否垫款
		if(StringUtil.isNotEmpty(bean.getIfAdvanceAmt())){
			 if(bean.getIfAdvanceAmt().equals("1")){
				 hql.append(" and pcd.loanType = :loanType");
					paramName.add("loanType");
					paramValue.add(PoolComm.XD_05);
			 }else  if(bean.getIfAdvanceAmt().equals("2")){
				 hql.append(" and pcd.loanType != :loanType");
					paramName.add("loanType");
					paramValue.add(PoolComm.XD_05);
			 }
		}
		// 借据余额开始
		if (bean.getStartLoanBalance() != null) {
			hql.append(" and pcd.loanBalance>=:loanBalance1");
			paramName.add("loanBalance1");
			paramValue.add(bean.getStartLoanBalance());
		}
		// 借据余额结束
		if (bean.getEndLoanBalance() != null) {
			hql.append(" and pcd.loanBalance<=:loanBalance2");
			paramName.add("loanBalance2");
			paramValue.add(bean.getEndLoanBalance());
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		dhql.append(hql);

		List res = this.find(dhql.toString(), paramNames, paramValues, page);
		List retList = new ArrayList();
		if (res != null && res.size() > 0) {
			CreditProductQuery beanNew = null;
			PedProtocolDto ppd = null;
			CreditProduct cp = null;
			PedCreditDetail pcd = null;
			PlOnlineAcptDetail pad =null;
			PedOnlineAcptProtocol pop =null;
			for (int i = 0; i < res.size(); i++) {
				beanNew = new CreditProductQuery();
				beanNew.setCreditDetailId(i+"");
				Object[] obj = (Object[]) res.get(i);
				if (obj[0] != null) {
					ppd = (PedProtocolDto) obj[0];
					beanNew.setPoolAgreement(ppd.getPoolAgreement()); // 票据池编号
					beanNew.setPoolName(ppd.getPoolName()); // 票据池名称
				}
				String cupy = "";//占用比例 
				if (obj[1] != null) {
					cp = (CreditProduct) obj[1];
					beanNew.setCustNo(cp.getCustNo()); // 客户号
					beanNew.setCrdtNo(cp.getCrdtNo()); // 合同号
					beanNew.setCustName(cp.getCustName());// 融资人名称
//					beanNew.setCrdtType(cp.getCrdtType());// 业务品种
					beanNew.setCrdtIssDt(cp.getCrdtIssDt());// 合同起始日期
					beanNew.setCrdtDueDt(cp.getCrdtDueDt());// 合同到期日
					beanNew.setCcupy(cp.getCcupy()); // 额度占用比例
					beanNew.setUseAmt(cp.getUseAmt());//额度
					beanNew.setSttlFlag(cp.getSttlFlag());// 合同状态
					beanNew.setRisklevel(cp.getRisklevel());//风险等级
					cupy = cp.getCcupy();
				}
				if (obj[2] != null) {
					pcd = (PedCreditDetail) obj[2];
					beanNew.setLoanNo(pcd.getLoanNo());// 借据号
					beanNew.setLoanAmount(pcd.getLoanAmount());// 借据金额
					beanNew.setLoanBalance(pcd.getLoanBalance());// 借据余额--未用
					beanNew.setActualAmount(pcd.getActualAmount());// 实际占用金额
					beanNew.setUseAmt(pcd.getActualAmount().multiply(new BigDecimal(cupy)));// 占用额度
					beanNew.setStartTime(pcd.getStartTime());// 借据起始日
					beanNew.setEndTime(pcd.getEndTime());// 借据到期日
					beanNew.setLoanStatus(pcd.getLoanStatus());// 借据状态
					beanNew.setCrdtType(pcd.getLoanType());// 业务品种
				}
				if (obj[3] != null) {
					pad = (PlOnlineAcptDetail) obj[3];
					beanNew.setBillNo(pad.getBillNo());// 票号
					beanNew.setId(pad.getId());
					beanNew.setBeginRangeNo(pad.getBeginRangeNo());
					beanNew.setEndRangeNo(pad.getEndRangeNo());
					beanNew.setDraftSource(pad.getDraftSource());
					beanNew.setSplitFlag(pad.getSplitFlag());
				}
				if (obj[4] != null) {
					pop = (PedOnlineAcptProtocol) obj[4];
					beanNew.setOnlineAcptNo(pop.getOnlineAcptNo());// 在线银承协议编号
				}
				retList.add(beanNew);
			}
		}
		page.setResult(retList);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(retList, map);
	}
	@Override
	public List findQueryPoolOutList(DraftQueryBean bean, Page page, User user)
			throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append(" select po from DraftPool po,PedProtocolDto dto where po.poolAgreement=dto.poolAgreement and po.assetStatus = 'DS_02' ");
		hql.append(" and po.plDueDt>:plstartDueDt2");//到期日大于当前日期
		paramName.add("plstartDueDt2");
		paramValue.add(new Date());

		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 手工出池界面只可票据的签约客户经理查询
//			hql.append(" and dto.accountManager =:accountManager ");
//			paramName.add("accountManager");
//			paramValue.add(user.getName());
			
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and po.branchId in (:branchId) ");
				paramName.add("branchId");
				paramValue.add(resultList);
			}
		}
		
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.assetNb like :assetNb");
			paramName.add("assetNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia())) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType())) {
			hql.append(" and po.assetType=:assetType");
			paramName.add("assetType");
			paramValue.add(bean.getAssetType());
		}

		// 出票日期开始startplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and po.plIsseDt>=:plstartDt");
			paramName.add("plstartDt");
			paramValue.add(bean.getStartplIsseDt());
		}
		// 出票日期结束endplIsseDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and po.plIsseDt<=:plIsseDt");
			paramName.add("plIsseDt");
			paramValue.add(bean.getEndplIsseDt());
		}

		// 到期日期开始startplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plDueDt>=:plstartDueDt");
			paramName.add("plstartDueDt");
			paramValue.add(bean.getStartplDueDt());
		}
		// 到期日期结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plDueDt<=:plDueDt");
			paramName.add("plDueDt");
			paramValue.add(bean.getEndplDueDt());
		}

		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and po.assetAmt>=:startAmt");
			paramName.add("startAmt");
			paramValue.add(bean.getStartassetAmt());
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and po.assetAmt<=:assetAmt");
			paramName.add("assetAmt");
			paramValue.add(bean.getEndassetAmt());
		}
		//票据池编号
		if(bean.getPoolAgreement() !=  null && !"".equals(bean.getPoolAgreement())){
			hql.append(" and po.poolAgreement =:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}

		//出票人 开户行名称
		if(bean.getPlDrwrAcctSvcrNm() !=  null && !"".equals(bean.getPlDrwrAcctSvcrNm())){
			hql.append(" and po.plDrwrAcctSvcrNm like :plDrwrAcctSvcrNm");
			paramName.add("plDrwrAcctSvcrNm");
			paramValue.add("%"+bean.getPlDrwrAcctSvcrNm()+"%");
		}	
		if(bean.getPlStatus() != null && bean.getPlStatus().size() >0){
			hql.append(" and (po.auditStatus is null or po.auditStatus in (:auditStatus))");
			paramName.add("auditStatus");
			paramValue.add(bean.getPlStatus());
		}
		
		/********************融合改造新增 start******************************/
		if(bean.getBeginRangeNo()!=null&&!bean.getBeginRangeNo().equals("")){
			hql.append(" and po.beginRangeNo like :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add("%"+bean.getBeginRangeNo()+"%");
		}
		if(bean.getEndRangeNo()!=null&&!bean.getEndRangeNo().equals("")){
			hql.append(" and po.endRangeNo like :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add("%"+bean.getEndRangeNo()+"%");
		}
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (po.draftSource is null or po.draftSource =:draftSource) ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and po.draftSource =:draftSource ");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and po.splitFlag = :splitFlag");
			paramName.add("splitFlag");
			paramValue.add(bean.getSplitFlag());
		}
		
		
		/********************融合改造新增 end******************************/
			
		hql.append(" order by po.plDueDt desc");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		if(res != null && res.size() > 0){
			return res;
		}
		return null;
	}

	@Override
	public void txSubmitPoolOutBill(String id, User user) throws Exception {
		//查询票据信息
		DraftPool pool = (DraftPool) this.load(id,DraftPool.class);
		if(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(pool.getAuditStatus()) || PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(pool.getAuditStatus())){
			throw new Exception(pool.getAssetNb()+"当前状态不允许提交审批!");
		}else{
						
			
			//生成审批对象
			ApproveAuditBean  approveAudit = new ApproveAuditBean();
			approveAudit.setAuditType("03");
			approveAudit.setBusiId(id); //报价单id
			approveAudit.setProductId("2001001"); //产品码
//			approveAudit.setCustCertNo(batch.getCpbranch()); //客户组织机构代码
//			approveAudit.setCustName(batch.getCpbankname()); //客户名称
//			approveAudit.setCustBankNm(batch.getCpbankname()); //客户开户行名称
			approveAudit.setAuditAmt(pool.getTradeAmt()); //总金额
			approveAudit.setBillType(pool.getAssetType()); //票据类型
			approveAudit.setBillMedia(pool.getPlDraftMedia()); // 票据介质  纸质
			approveAudit.setTotalNum(1); //总笔数
			approveAudit.setBusiType("20010");
			approveAudit.setApplyNo(pool.getAssetNb());
			Map<String,String> mvelDataMap = new HashMap<String,String>();
			mvelDataMap.put("amount", pool.getAssetAmt().toString());
			mvelDataMap.put("totalNum", String.valueOf(pool.getAssetAmt()));
			AuditResultDto retAudit = auditService.txCommitApplyAudit(user, null, approveAudit, mvelDataMap);
			if(!retAudit.isIfSuccess()){
				//没有配置审批路线
				if("01".equals(retAudit.getRetCode())){
					throw new Exception("没有配置审批路线");
				}else if("02".equals(retAudit.getRetCode())){
					throw new Exception("审批金额过大 ，所有审批节点 都没有权限");
				}else{
					throw new Exception(retAudit.getRetMsg());
				}
			}
			pool.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT); //
			this.txStore(pool);
			
		}
	}

	@Override
	public String txCancelAuditPoolOutBill(String id, User user) throws Exception {
		String str = "success";
		//查询票据信息
		DraftPool pool = (DraftPool) this.load(id,DraftPool.class);
		/*
		 * 解电票经办锁，解票据池锁 
		 */
		String billNo = null;
		String billId = null;
		PoolBillInfo info = this.loadByBillNo(pool.getAssetNb(),pool.getBeginRangeNo(),pool.getEndRangeNo());
			
		/**
		 * 手工出池解除票据池经办锁不通知电票系统加锁
		 */
		logger.info("票据【"+billNo+"】出池撤销审批经办锁解锁成功!");
		info.setEbkLock(PoolComm.EBKLOCK_02);//未锁票

		pool.setTradeAmt(pool.getAssetAmt());
		pool.setLockz(PoolComm.BBSPLOCK_02);//未锁票
		pool.setTradeAmt(info.getFBillAmount());
		
		this.txStore(info);
		this.txStore(pool);
		
		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(pool.getAuditStatus())){
			return "当前授信额度状态不允许撤销审批。";
		}
		auditService.txCommitCancelAudit("2001001", pool.getId());
		
		//状态回滚
		pool.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);
		this.txStore(pool);
		return str;
		
	}
	
	@Override
	public CorePdraftColl getPdraftCollByBill(String bill) throws Exception {
		String query = "from CorePdraftColl as cpc where cpc.afxno=:billNo ";
		List parasNameList = new ArrayList();
		List parameters = new ArrayList();
		parasNameList.add("billNo");
		parameters.add(bill);
		List list = this.find(query.toString(),
				(String[]) parasNameList.toArray(new String[parasNameList.size()]),
				parameters.toArray(), null);
		if(list!=null&& list.size()>0){
			return (CorePdraftColl) list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public QueryResult toPoolByQueryBean(DraftQueryBean bean, Page page)
			throws Exception {
		StringBuffer hql = new StringBuffer("select poolIn.pl_draftNb as plDraftNb,poolIn.pl_draftType as plDraftType,poolIn.pl_draftMedia as plDraftMedia,poolIn.pl_isseAmt as plIsseAmt,poolIn.pl_isseDt as plIsseDt,poolIn.pl_dueDt as plDueDt," +
				" poolIn.pl_drwrNm as plDrwrNm,poolIn.pl_drwrAcctId as plDrwrAcctId,poolIn.pl_drwrAcctSvcrNm as plDrwrAcctSvcrNm,draft.ed_drwrAcctSvcr as plDrwrAcctSvcr,poolIn.pl_accptrSvcrNm as plAccptrSvcrNm,poolIn.pl_accptrSvcr as plAccptrSvcr,poolIn.pl_pyeeNm as plPyeeNm," +
				"poolIn.pl_pyeeAcctId as plPyeeAcctId,poolIn.pl_pyeeAcctSvcrNm as plPyeeAcctSvcrNm,draft.ed_pyeeAcctSvcr as plPyeeAcctSvcr,draft.ed_banEndrsmtMk as forbidFlag,to_char(poolIn.pl_reqTime,'YYYY-MM-DD hh24:mi:ss') as plReqTime,'0' as type, " +
				" poolIn.BEGIN_RANGE_NO as beginRangeNo ,poolIn.END_RANGE_NO as endRangeNo,poolIn.DRAFT_SOURCE as draftSource, poolIn.SPLIT_FLAG as splitFlag, poolIn.pl_accptrAcctName as acceptAcctName, poolIn.pl_accptrId as accptrAcctNo" +
				", poolIn.pl_accptrNm as plAccptrNm, poolIn.pl_drwrAcctName as plDrwrAcctName, poolIn.pl_pyeeAcctName as plPyeeAcctName, draft.discBillId as billId, poolIn.ACC_NO as acctNo   from PL_POOL_IN poolIn, cd_edraft draft WHERE poolIn.pl_draftNb= draft.ed_idnb ");
		// 票据池核心客户号CustNo
		if (bean.getCustNo() != null && !"".equals(bean.getCustNo())) {
			hql.append(" and poolIn.cust_no= '"+bean.getCustNo()+"'");
		}
		// 票据池编号 poolAgreement
		if (bean.getPoolAgreement() != null && !"".equals(bean.getPoolAgreement())) {
			hql.append(" and poolIn.Pool_Agreement= '"+bean.getPoolAgreement()+"'");
		}
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and poolIn.pl_draftNb like '%"+bean.getPlDraftNb()+"%' ");
		}
		/********************融合改造新增 start******************************/
		// 票据号码起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and poolIn.begin_Range_No = '"+bean.getBeginRangeNo()+"' ");
		}
		// 票据号码止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and poolIn.end_Range_No = '"+bean.getEndRangeNo()+"' ");
		}
		// 票据来源
		if (bean.getDraftSource() != null && !"".equals((bean.getDraftSource()))) {
			hql.append(" and poolIn.DRAFT_SOURCE = '"+bean.getDraftSource()+"' ");
		}
		/********************融合改造新增 end******************************/
		// 票据种类plDraftMedia(纸票  银票)
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia()) && !"0".equals(bean.getPlDraftMedia() )) {
			hql.append(" and poolIn.pl_draftMedia= '"+bean.getPlDraftMedia()+"'");
		}
		// 票据介质assetType(AC01  AC02)
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType()) && !"0000".equals(bean.getAssetType() )) {
			hql.append(" and poolIn.pl_draftType= '"+bean.getAssetType()+"'");
		}
		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and poolIn.pl_isseAmt>= '"+bean.getStartassetAmt()+"'");
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and poolIn.pl_isseAmt<= '"+bean.getEndassetAmt()+"'");
		}
		// 出票日期开始StartplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and poolIn.pl_isseDt>=TO_DATE( '"+DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 出票日期结束endplDueDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and poolIn.pl_isseDt<=TO_DATE( '"+DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 到期日开始StartplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and poolIn.pl_dueDt>=TO_DATE( '"+DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 到期日结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and poolIn.pl_dueDt<=TO_DATE( '"+DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 入池日期开始startplDueDt
		if (bean.getStartplReqTime() != null && !"".equals(bean.getStartplReqTime())) {
			hql.append(" and poolIn.pl_reqTime>=TO_DATE( '"+DateUtils.toString(bean.getStartplReqTime(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 入池申请日期结束endplDueDt
		if (bean.getEndplReqTime() != null && !"".equals(bean.getEndplReqTime())) {
			hql.append(" and poolIn.pl_reqTime<=TO_DATE( '"+DateUtils.toString(bean.getEndplReqTime(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}

		hql.append(" union all " +
				"select plOut.pl_draftNb as plDraftNb,plOut.pl_draftType as plDraftType,plOut.pl_draftMedia as plDraftMedia,plOut.pl_isseAmt as plIsseAmt,plOut.pl_isseDt as plIsseDt,plOut.pl_dueDt as plDueDt," +
				" plOut.pl_drwrNm as plDrwrNm,plOut.pl_drwrAcctId as plDrwrAcctId,plOut.pl_drwrAcctSvcrNm as plDrwrAcctSvcrNm,draft.ed_drwrAcctSvcr as plDrwrAcctSvcr,plOut.pl_accptrSvcrNm as plAccptrSvcrNm,plOut.pl_accptrSvcr as plAccptrSvcr,plOut.pl_pyeeNm as plPyeeNm," +
				"plOut.pl_pyeeAcctId as plPyeeAcctId,plOut.pl_pyeeAcctSvcrNm as plPyeeAcctSvcrNm,draft.ed_pyeeAcctSvcr as plPyeeAcctSvcr,draft.ed_banEndrsmtMk as forbidFlag,to_char(plOut.pl_reqTime,'YYYY-MM-DD hh24:mi:ss') as plReqTime,'1' as type ," +
				"plOut.BEGIN_RANGE_NO as beginRangeNo ,plOut.END_RANGE_NO as endRangeNo ,plOut.DRAFT_SOURCE as draftSource, plOut.SPLIT_FLAG as splitFlag, plOut.pl_accptrAcctName as acceptAcctName, plOut.pl_accptrId as accptrAcctNo" +
				", plOut.pl_accptrNm as plAccptrNm, plOut.pl_drwrAcctName as plDrwrAcctName, plOut.pl_pyeeAcctName as plPyeeAcctName, draft.discBillId as billId, plOut.ACC_NO as acctNo    from PL_POOL_OUT plOut, cd_edraft draft WHERE plOut.pl_draftNb= draft.ed_idnb ");
		// 票据池核心客户号CustNo
		if (bean.getCustNo() != null && !"".equals(bean.getCustNo())) {
			hql.append(" and plOut.cust_no= '"+bean.getCustNo()+"'");
		}
		// 票据池编号 poolAgreement
		if (bean.getPoolAgreement() != null && !"".equals(bean.getPoolAgreement())) {
			hql.append(" and plOut.Pool_Agreement= '"+bean.getPoolAgreement()+"'");
		}
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and plOut.pl_draftNb like '%"+bean.getPlDraftNb()+"%' ");
		}
		/********************融合改造新增 start******************************/
		// 票据号码起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and plOut.begin_Range_No = '"+bean.getBeginRangeNo()+"' ");
		}
		// 票据号码止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and plOut.end_Range_No = '"+bean.getEndRangeNo()+"' ");
		}
		// 票据来源
		if (bean.getDraftSource() != null && !"".equals((bean.getDraftSource()))) {
			hql.append(" and plOut.DRAFT_SOURCE = '"+bean.getDraftSource()+"' ");
		}
		/********************融合改造新增 end******************************/
		// 票据种类plDraftMedia(纸票  银票)
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia()) && !"0".equals(bean.getPlDraftMedia() )) {
			hql.append(" and plOut.pl_draftMedia= '"+bean.getPlDraftMedia()+"'");
		}
		// 票据介质assetType(AC01  AC02)
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType()) && !"0000".equals(bean.getAssetType() )) {
			hql.append(" and plOut.pl_draftType= '"+bean.getAssetType()+"'");
		}
		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and plOut.pl_isseAmt>= '"+bean.getStartassetAmt()+"'");
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and plOut.pl_isseAmt<= '"+bean.getEndassetAmt()+"'");
		}
		// 出票日期开始StartplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and plOut.pl_isseDt>=TO_DATE( '"+DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 出票日期结束endplDueDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and plOut.pl_isseDt<=TO_DATE( '"+DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 到期日开始StartplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and plOut.pl_dueDt>=TO_DATE( '"+DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 到期日结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and plOut.pl_dueDt<=TO_DATE( '"+DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 入池日期开始startplDueDt
		if (bean.getStartplReqTime() != null && !"".equals(bean.getStartplReqTime())) {
			hql.append(" and plOut.pl_reqTime>=TO_DATE( '"+DateUtils.toString(bean.getStartplReqTime(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		// 入池申请日期结束endplDueDt
		if (bean.getEndplReqTime() != null && !"".equals(bean.getEndplReqTime())) {
			hql.append(" and plOut.pl_reqTime<=TO_DATE( '"+DateUtils.toString(bean.getEndplReqTime(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')");
		}
		List list = dao.SQLQuery(hql.toString(),page);
		
		QueryResult qr = new QueryResult();
		String amountFieldName = "pl_isseAmt";
		if (list != null && list.size() > 0) {
			qr.setRecords(list);
			qr.setTotalCount(list.size());
			return qr;
		}
		return qr;
	}

	@Override
	public List<DraftPool> getDraftPoolList(String splitId) throws Exception {
		String sql = "from DraftPool where splitId = '" +splitId+ "' ";
		List list = this.find(sql);
		if(list != null && list.size() > 0){
			return list;
		}
		return null;
	}


}
