package com.mingtech.application.pool.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AllAssetTypeResult;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PlFeeList;
import com.mingtech.application.pool.common.domain.QueryPlFeeListBean;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
@Service("pedAssetTypeService")
public class PedAssetTypeServiceImpl extends GenericServiceImpl implements PedAssetTypeService{
	private static final Logger logger = Logger.getLogger(PedAssetTypeServiceImpl.class);
	@Autowired
	private ConsignService consignService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private PoolBailEduService poolBailEduService ;
	@Override
	public String getEntityName() {
		return AssetType.class.getName();
	}

	@Override
	public Class getEntityClass() {
		return AssetType.class;
	}

	@Override
	public List<AssetType> queryPedAssetTypeByAssetPool(AssetPool ap) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String hql = "select at from AssetType at where at.apId =:apId" ;
				
		paramName.add("apId");
		paramValue.add(ap.getApId());

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<AssetType>  result = this.find(hql,paramNames,paramValues);
		return result;
	}

	@Override
	public List<AssetType> queryPedAssetTypeByAssetPool(AssetPool ap,
			String type) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String hql = "select at from AssetType at where at.apId =:apId and at.astType=:astType" ;
				
		paramName.add("apId");
		paramValue.add(ap.getApId());
		if(StringUtils.isNotEmpty(type)){
			paramName.add("astType");
			paramValue.add(type);
		}

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<AssetType>  result = this.find(hql,paramNames,paramValues);
		return result;
	}

	@Override
	public AssetType queryPedAssetTypeByProtocol(PedProtocolDto protocol,
			String type) throws Exception {
		logger.info("通过票据池信息【"+protocol.getPoolAgreement()+"】信息获取资产池...");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String hql = "select at from AssetType at,AssetPool ap where at.apId =ap.apId  " +
				" and ap.custId =:custId and ap.poolType='ZC_01' and at.astType=:astType " ;
				
		paramName.add("custId");
		paramValue.add(protocol.getPoolInfoId());
		
		paramName.add("astType");
		paramValue.add(type);

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<AssetType>  result = this.find(hql,paramNames,paramValues);
		if(result!=null && result.size()>0){
			return result.get(0);
		}else{
			return null;
		}
	}

	@Override
	public AssetType queryPedAssetTypeByProtocol( String type, String bpsNo)
			throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String hql = "select at from AssetType at,AssetPool ap,PedProtocolDto p where at.apId =ap.apId and ap.custId=p.poolInfoId " +
				"  and ap.poolType='ZC_01' and p.poolAgreement =:poolAgreement and at.astType=:astType and  p.openFlag=:openFlag" ;
				
		
		paramName.add("poolAgreement");
		paramValue.add(bpsNo);
		
		paramName.add("astType");
		paramValue.add(type);
		
		paramName.add("openFlag");
		paramValue.add(PoolComm.OPEN_01);

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<AssetType>  result = this.find(hql,paramNames,paramValues);
		return result.get(0);
	}

	@Override
	public PedProtocolDto queryPedAssetTypeReturnCredit(PedProtocolDto protocol)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" select sum(a.crdt_free),sum(a.crdt_used) from ped_asset_type a ,ped_asset_pool b " +
						   "where a.ap_id = b.ap_id and b.bps_no='"+protocol.getPoolAgreement()+"'");
		List  rslt = this.dao.SQLQuery(hql.toString());
		if(rslt!=null && rslt.size()>0){
			Object[] obj = (Object[]) rslt.get(0);
			BigDecimal creditFreeAmount = new BigDecimal("0");//全部未用金额
			BigDecimal creditUsedAmount = new BigDecimal("0");//担保合同已用金额
			if(obj[1]!=null){
				creditUsedAmount= new BigDecimal(obj[1].toString());
			}
			if(protocol.getCreditamount()!=null){
				creditFreeAmount = protocol.getCreditamount().subtract(creditUsedAmount);
			}
			protocol.setCreditFreeAmount(creditFreeAmount);
			protocol.setCreditUsedAmount(creditUsedAmount);
			return protocol;	
		}else{
			return null;				
		}
	}

	@Override
	public List<PlFeeList> queryserviceCharge(String bpsNo, String bpsName,String feeType,User user,Page page)
			throws Exception {
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List  paramValue = new ArrayList();// 值
		String hql= "select b from PlFeeList b,PedProtocolDto ped  where 1=1 and b.bpsNo = ped.poolAgreement ";
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultListNew = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql = hql+" and ped.signDeptNo in (:signDeptNo) ";
				paramName.add("signDeptNo");
				paramValue.add(resultListNew);
			}
		}		
		if(StringUtils.isNotEmpty(bpsNo)){
			hql = hql+" and b.bpsNo =:bpsNo  ";
			paramName.add("bpsNo");
			paramValue.add(bpsNo);	
		}
		if(StringUtils.isNotEmpty(feeType)){
			hql = hql+" and b.feeType =:feeType  ";
			paramName.add("feeType");
			paramValue.add(feeType);	
		}
		if(StringUtils.isNotEmpty(bpsName)){
			hql = hql+" and b.bpsName like :bpsName  ";
			paramName.add("bpsName");
			paramValue.add("%"+bpsName+"%");	
		}
		hql = hql+" order by b.chargeDate ";
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues,page);
		if(resultList!=null && resultList.size()>0){
			return resultList;
		}
		return null;
	}
	@Override
	public List<QueryPlFeeListBean> queryserviceChargeDetail(String feeBatchNo,Page page)
			throws Exception {
		List resultList = new ArrayList();
		List list = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		String hql= "from DraftPool b where 1=1 ";
		if(StringUtils.isNotEmpty(feeBatchNo)){
			hql = hql+" and b.feeBatchNo =:feeBatchNo  ";
			paramName.add("feeBatchNo");
			paramValue.add(feeBatchNo);	
		}
		hql = hql+" order by b.plDueDt  ";
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues,page);
		if(resultList!=null && resultList.size()>0){
			for(int i=0;i<resultList.size();i++){
				DraftPool draPool=(DraftPool) resultList.get(i);
				QueryPlFeeListBean queryBean = new QueryPlFeeListBean();
				PedProtocolList pedPro=this.queryProtocolListByQuery(draPool.getCustNo());
				BeanUtils.copyProperties(queryBean, draPool);
				queryBean.setBpsNo(pedPro.getBpsNo());
				queryBean.setBpsName(pedPro.getBpsName());
				queryBean.setCustName(pedPro.getCustName());
				queryBean.setSBillNo(draPool.getPoolBillInfo().getSBillNo());
				queryBean.setSBillType(draPool.getPoolBillInfo().getSBillType());
				queryBean.setFBillAmount(draPool.getPoolBillInfo().getFBillAmount());
				list.add(queryBean);
			}
			return list;
		}
		return null;
	}

	public PedProtocolList queryProtocolListByQuery(
			String custNo) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select proList from PedProtocolList as proList where 1=1 ");
		if(StringUtil.isNotBlank(custNo)){//核心客户号
			hql.append(" and proList.custNo =:custNo");
			paramName.add("custNo");
			paramValue.add(custNo);
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolList> result = this.find(hql.toString(), paramNames, paramValues );

		if(result!=null&&result.size()>0){
			PedProtocolList proList = result.get(0);
			return proList;	
		}
		return null;
	}

	@Override
	public List<PlFeeList> queryserviceChargeManage(Page page) throws Exception {
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		String hql= "from PlFeeScale b order by b.createDate";
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues,page);
		if(resultList!=null && resultList.size()>0){
			return resultList;
		}
		return null;
	}

	@Override
	public AllAssetTypeResult queryAllAssetType(String bpsNo) throws Exception {
		
		AllAssetTypeResult allType = new AllAssetTypeResult();
		
		AssetType ed_pjc =  this.queryPedAssetTypeByProtocol( PoolComm.ED_PJC, bpsNo);
		AssetType ed_pjc_01 =  this.queryPedAssetTypeByProtocol( PoolComm.ED_PJC_01, bpsNo);
		AssetType ed_bzj_hq =  this.queryPedAssetTypeByProtocol( PoolComm.ED_BZJ_HQ, bpsNo);
		AssetType ed_bzj_dq =  this.queryPedAssetTypeByProtocol( PoolComm.ED_BZJ_DQ, bpsNo);
		
		allType.setEd_pjc(ed_pjc);
		allType.setEd_pjc_01(ed_pjc_01);
		allType.setEd_bzj_hq(ed_bzj_hq);
		allType.setEd_bzj_dq(ed_bzj_dq);

		return allType;
	}
	
	@Override
	public AllAssetTypeResult outApplylimitCheck(List<String> billNos, PedProtocolDto dto)
			throws Exception {
		
		AllAssetTypeResult result = new AllAssetTypeResult();
		String outApplyCheckResult = null;
		
		poolBailEduService.txUpdateBailDetail(dto.getPoolAgreement());//同步保证金		
		AllAssetTypeResult allType = this.queryAllAssetType(dto.getPoolAgreement());
		AssetType ed_pjc =  allType.getEd_pjc();//低风险票据额度
		AssetType ed_pjc_01 = allType.getEd_pjc_01();//高风险票据额度 
		AssetType ed_bzj_hq = allType.getEd_bzj_hq();//保证金额度
		
		
		/*
		 * 获取冻结状态
		 */
		String djFlag = dto.getFrozenstate();//冻结标识      DJ_00 ：未冻结 DJ_01：保证金冻结   DJ_02：票据冻结   DJ_03：全冻结
		if(PoolComm.FROZEN_STATUS_01.equals(djFlag)){
			ed_bzj_hq.setCrdtUsed(ed_bzj_hq.getCrdtTotal());//置为全部已用（实际等于保证金可用额度置为0）			
		}

		BigDecimal lowAllDe = ed_pjc.getCrdtTotal().add(ed_bzj_hq.getCrdtTotal());
		BigDecimal lowUsedDe = ed_pjc.getCrdtUsed().add(ed_bzj_hq.getCrdtUsed());
		
		double lowAll = lowAllDe.doubleValue();// 低风险总额度
		double lowUsed = lowUsedDe.doubleValue();// 低风险已用额度
		double lowOut =  0;//出池的低风险
		double highUsed = ed_pjc_01.getCrdtUsed().doubleValue();//高风险已用
		double highOut =  0;//出池的高风险额度
		double highAll = ed_pjc_01.getCrdtTotal().doubleValue();//池高风险总额度
		
		List<DraftPool> draftPoolList = new ArrayList<DraftPool>();
		
		for (String billNo : billNos) {
			logger.info("票号为:"+billNo);
			PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setBillNo(billNo);
			poolQueryBean.setSStatusFlag(PoolComm.DS_03);
			DraftPool pool = consignService.queryDraftByBean(poolQueryBean).get(0);
			
			if(pool.getPlDueDt().getTime()<=(new Date().getTime())){//到期日>=当日时间不允许出池操作
				outApplyCheckResult =  Constants.EBK_10;//出池票据种含有当日及当日之前到期的票，不允许出池操作！
				result.setOutApplyCheckResult(outApplyCheckResult);
				return result;
			}
			
			if(PoolComm.LOW_RISK.equals(pool.getRickLevel())){//低风险
				lowOut = lowOut + pool.getAssetAmt().doubleValue();
				
			}else if(PoolComm.HIGH_RISK.equals(pool.getRickLevel())){//高风险
				highOut = highOut+pool.getAssetAmt().doubleValue();

			}else{
				logger.info(pool.getAssetNb()+":该票据不在风险名单中，不计入出池额度");
			}
			
			draftPoolList.add(pool);
		}
		
		
		/**
		 * 
		 * 出池公式：  票据池低风险总额度-低风险业务已用额度-出池的低风险额度-[MAX（票据池高风险业务已用额度+出池的高风险额度-票据池高风险总额度，0)]≥0
		 * 
		 */
		
		logger.info("纸票出池额度校验【"+billNos.toString()+"】低风险总额度【"+lowAll+"】低风险已用额度【"+lowUsed+"】出池的低风险【"+lowOut+"】高风险已用【"+highUsed+"】出池的高风险额度【"+highOut+"】高风险总额度【"+highAll+"】");
		
		double amt1 = lowAll - lowUsed - lowOut;
		double amt2 = ((highUsed + highOut - highAll)>0) ? (highUsed + highOut - highAll) : 0;
		
		if((amt1 - amt2) >= 0){
			outApplyCheckResult =  PoolComm.YES;
		}
		logger.info("纸票出池额度校验 amt1【"+amt1+"】amt2【"+amt2+"】校验结果:"+outApplyCheckResult);
		
		result.setOutApplyCheckResult(outApplyCheckResult);
		result.setDraftPoolList(draftPoolList);
		return result;
		
		
	}
}
