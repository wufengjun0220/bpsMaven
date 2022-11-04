package com.mingtech.application.pool.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.LimitVo;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
@Service("pedAssetPoolService")
public class PedAssetPoolServiceImpl extends GenericServiceImpl implements PedAssetPoolService{
	private static final Logger logger = Logger.getLogger(PedAssetPoolServiceImpl.class);
	
	@Autowired
	private PedAssetTypeService pedAssetTypeService;//资产类型接口
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private PedProtocolService pedProtocolService;

	@Override
	public String getEntityName() {
		return AssetPool.class.getName();
	}

	@Override
	public Class getEntityClass() {
		return AssetPool.class;
	}

	@Override
	public AssetPool queryPedAssetPoolByProtocol(PedProtocolDto protocol) throws Exception{
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String hql = "select ap from AssetPool ap where ap.bpsNo =:bpsNo and poolType='ZC_01' " ;
		
		paramName.add("bpsNo");
		paramValue.add(protocol.getPoolAgreement());

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<AssetPool>  result = this.find(hql,paramNames,paramValues);
		if(result!=null && result.size()>0){
			return result.get(0);
		}
		return null;
	}

	@Override
	public AssetPool queryPedAssetPoolByOrgCodeOrCustNo(String bpsNo,String orgCode,
			String custNo) throws Exception {
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		
		String hql = "select ap from AssetPool ap where poolType='ZC_01'    " ;
		
		if(StringUtils.isNotEmpty(bpsNo)){
			hql = hql+" and ap.bpsNo =:bpsNo  ";
			paramName.add("bpsNo");
			paramValue.add(bpsNo);	
		}
		
		if(StringUtils.isNotEmpty(custNo)){
			hql = hql+" and ap.custNo =:custNo  ";
			paramName.add("custNo");
			paramValue.add(custNo);	
		}
		if(StringUtils.isNotEmpty(orgCode)){
			hql = hql+" and ap.custOrgcode=:custOrgcode  ";
			paramName.add("custOrgcode");
			paramValue.add(orgCode);
		}

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<AssetPool>  result = this.find(hql,paramNames,paramValues);
		if(result!=null && result.size()>0){
			return result.get(0);
		}
		return null;
	}

	@Override
	public EduResult queryEduAll(String bpsNo) throws Exception {
		
		logger.info("【"+bpsNo+"】额度查询统计方法开始....");
		
		BigDecimal totalBillAmount = BigDecimal.ZERO;// 票据总额度
		
		BigDecimal highRiskAmount = BigDecimal.ZERO;// 高风险额度
		BigDecimal usedHighRiskAmount = BigDecimal.ZERO;// 已用高风险额度
		BigDecimal freeHighRiskAmount = BigDecimal.ZERO;// 未用高风险额度
		
		BigDecimal lowRiskAmount = BigDecimal.ZERO;// 低风险票据总额度
		BigDecimal usedLowRiskAmount = BigDecimal.ZERO;// 已用低风险额度
		BigDecimal freeLowRiskAmount = BigDecimal.ZERO;// 未用低风险额度
		
		
		BigDecimal bailAmountTotail = BigDecimal.ZERO;// 保证金总金额
		BigDecimal bailAmountUsed = BigDecimal.ZERO;// 保证金已用金额
		BigDecimal bailAmount = BigDecimal.ZERO;// 保证金可用金额	
		BigDecimal zeroEduAmount = BigDecimal.ZERO;// 未产生额度票据总金额
		
		/*
		 * 零额度统计查询方法
		 */
		zeroEduAmount=this.queryZeroEduAmount(bpsNo,null);
		
		/*
		 * 票据池AssetType统计查询
		 */
		List queryRet = queryAssetAmount(bpsNo);
		
		if (queryRet != null && queryRet.size() > 0) {
			for(int i = 0; i < queryRet.size(); i++) {
				Object[] obj = (Object[]) queryRet.get(i);
				if (obj[3] != null && !"".equals(obj[3])) { //资产类型
					if(obj[3].toString().equals(PoolComm.ED_PJC)) {//低风险票据
						if (obj[0] != null && !"".equals(obj[0])) {
							lowRiskAmount = new BigDecimal(obj[0].toString());// 低风险总额度
						}
						if (obj[1] != null && !"".equals(obj[1])) {
							usedLowRiskAmount = new BigDecimal(obj[1].toString());// 低风险已用额度
						}
						if (obj[2] != null && !"".equals(obj[2])) {
							freeLowRiskAmount = new BigDecimal(obj[2].toString());// 低风险可用
						}
					} else if(obj[3].toString().equals(PoolComm.ED_PJC_01)) {//高风险额度
						if (obj[0] != null && !"".equals(obj[0])) {
							highRiskAmount = new BigDecimal(obj[0].toString());// 高风险总额度
						}
						if (obj[1] != null && !"".equals(obj[1])) {
							usedHighRiskAmount = new BigDecimal(obj[1].toString());// 高风险已用额度
						}
						if (obj[2] != null && !"".equals(obj[2])) {
							freeHighRiskAmount = new BigDecimal(obj[2].toString());// 高风险可用额度
						}
					} else if(obj[3].toString().equals(PoolComm.ED_BZJ_HQ)) {//活期保证金
						if (obj[0] != null && !"".equals(obj[0])) {
							bailAmountTotail = new BigDecimal(obj[0].toString());// 保证金总额度
						}
						if (obj[1] != null && !"".equals(obj[1])) {
							bailAmountUsed = new BigDecimal(obj[1].toString());// 保证金已用额度
						}
						if (obj[2] != null && !"".equals(obj[2])) {
							bailAmount = new BigDecimal(obj[2].toString());// 保证金可用额度
						}
					} else if(obj[3].toString().equals(PoolComm.ED_BZJ_DQ)) {//定期保证金
						//  暂时没有定期保证金
					} else {
						
					}
				}
			}
		}
		
		EduResult eduResult = new EduResult();
		// 票据总额度
		totalBillAmount = highRiskAmount.add(lowRiskAmount);
		eduResult.setTotalBillAmount(totalBillAmount);
		// 高风险票据
		eduResult.setHighRiskAmount(highRiskAmount);
		eduResult.setUsedHighRiskAmount(usedHighRiskAmount);
		eduResult.setFreeHighRiskAmount(freeHighRiskAmount);
		// 低风险票据
		eduResult.setLowRiskAmount(lowRiskAmount);
		eduResult.setUsedLowRiskAmount(usedLowRiskAmount);
		eduResult.setFreeLowRiskAmount(freeLowRiskAmount);
		// 未产生额度票据
		eduResult.setZeroEduAmount(zeroEduAmount);
		// 保证金
		eduResult.setBailAmount(bailAmount);
		eduResult.setBailAmountUsed(bailAmountUsed);
		eduResult.setBailAmountTotail(bailAmountTotail);

		logger.info("【"+bpsNo+"】额度统计结束，" +
				"票据总额度【"+totalBillAmount+"】高风险票据总额度【"+highRiskAmount+"】高风险票据已用额度【"+usedHighRiskAmount+"】高风险票据可用额度【"+freeHighRiskAmount+"】" +
				"低风险票据总额度【"+lowRiskAmount+"】低风险票据已用额度【"+usedLowRiskAmount+"】低风险票据可用额度【"+freeLowRiskAmount+"】" +
				"未产生额度票据总额度【"+zeroEduAmount+"】保证金总额度【"+bailAmount+"】保证金已用总额度【"+bailAmountUsed+"】保证金可用额度【"+bailAmountTotail+"】");
		return eduResult;
	}
	/**
	 * 未产生额度票据金额查询
	 * @author Ju Nana
	 * @param bpsNo
	 * @return
	 * @date 2019-6-29下午3:10:25
	 */
	private BigDecimal queryZeroEduAmount(String bpsNo,String custNo) {
		
		logger.info("票据池【"+bpsNo+"】零额度查询统计开始....");
		
		String hql = "select sum(pl.assetAmt) from DraftPool pl where rickLevel='FX_03'";
		List param = new ArrayList();
		
		if(StringUtil.isNotBlank(bpsNo)){
			hql = hql+" and pl.poolAgreement=? ";
			param.add(bpsNo);
		}
		if(StringUtil.isNotBlank(custNo)){
			hql = hql+" and pl.custNo=? ";
			param.add(custNo);
		}
		List temp = this.find(hql, param);
		BigDecimal otherEd = new BigDecimal("0");
		if (temp != null && temp.size() > 0) {
			if (null != temp.get(0)) {
				otherEd = new BigDecimal(String.valueOf(temp.get(0)));
			}
		}
		return otherEd;

	}
	
	/**
	 * 查询客户Asset信息
	 * @author Ju Nana
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午3:11:52
	 */
	private List queryAssetAmount(String bpsNo) throws Exception {
		
		logger.info("票据池【"+bpsNo+"】AssetType额度统计方法开始....");
		
		String hql = "select sum(at.crdtTotal),sum(at.crdtUsed),sum(at.crdtFree),at.astType from AssetType at,AssetPool ap "
				+ "where at.apId=ap.apId and ap.bpsNo=? group by at.astType";
		List param = new ArrayList();
		param.add(bpsNo);
		List queryRet = this.find(hql, param);

		return queryRet;

	}

	@Override
	public EduResult queryEduMember(String bpsNo, String custNo)
			throws Exception {
		
		logger.info("集团成员额度查询方法开始....");
		
		BigDecimal totalBillAmount = BigDecimal.ZERO;//成员客户生成的票据总额度
		
		BigDecimal highRiskAmount = BigDecimal.ZERO;//成员客户生成的高风险额度
		BigDecimal usedHighRiskAmount = BigDecimal.ZERO;// 成员客户已用高风险额度
		
		BigDecimal lowRiskAmount = BigDecimal.ZERO;// 成员客户生成的低风险票据总额度
		BigDecimal usedLowRiskAmount = BigDecimal.ZERO;// 成员客户已用低风险额度		
		
		BigDecimal zeroEduAmount = BigDecimal.ZERO;// 成员客户生成的未产生额度票据总金额
		
		zeroEduAmount=this.queryZeroEduAmount(bpsNo,custNo);
		
		
		/**客户出质的票据的额度信息*/
		highRiskAmount = this.queryMemDraftPoolEdu(bpsNo, custNo, PoolComm.HIGH_RISK).getTotal();
		if(highRiskAmount == null ) {
			highRiskAmount = BigDecimal.ZERO;
		}
		lowRiskAmount = this.queryMemDraftPoolEdu(bpsNo, custNo, PoolComm.LOW_RISK).getTotal();
		if(lowRiskAmount == null ) {
			lowRiskAmount = BigDecimal.ZERO;
		}
		totalBillAmount = highRiskAmount.add(lowRiskAmount);
		
				
		usedHighRiskAmount = creditRegisterService.queryCreditBalance(bpsNo, PoolComm.HIGH_RISK);
		usedLowRiskAmount = creditRegisterService.queryCreditBalance(bpsNo, PoolComm.LOW_RISK);;

		
		EduResult eduResult = new EduResult();
		//成员客户生成的票据总额度
		eduResult.setTotalBillAmount(totalBillAmount);
		// 成员客户生成的高风险额度
		eduResult.setHighRiskAmount(highRiskAmount);
		// 成员客户已用高风险额度
		eduResult.setUsedHighRiskAmount(usedHighRiskAmount);
		// 成员客户生成的低风险票据总额度
		eduResult.setLowRiskAmount(lowRiskAmount);
		// 成员客户已用低风险额度
		eduResult.setUsedLowRiskAmount(usedLowRiskAmount);
		// 未产生额度票据
		eduResult.setZeroEduAmount(zeroEduAmount);

		return eduResult;
	
	}
	
	/**
	 * 计算集团成员生成票据池额度的票据的信息
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @param riskLevel
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午3:41:52
	 */
	public LimitVo queryMemDraftPoolEdu(String bpsNo,String custNo,String riskLevel) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		String hql = "select new com.mingtech.application.pool.edu.domain.LimitVo(sum(assetLimitTotal),sum(assetLimitUsed),sum(assetLimitFree),sum(assetLimitFrzd),count(id)) from "
				+ " DraftPool dp where dp.assetStatus in(:assetStatus)  ";
		
		List useStates = new ArrayList();
		useStates.add(PoolComm.DS_02);
//		useStates.add(PoolComm.DS_06);
		paramName.add("assetStatus");
		paramValue.add(useStates);
		
		if(StringUtil.isNotBlank(bpsNo)){
			hql = hql+" and dp.poolAgreement=:bpsNo ";
			paramName.add("bpsNo");
			paramValue.add(bpsNo);
			
		}
		if(StringUtil.isNotBlank(custNo)){
			hql = hql+" and dp.assetCommId=:custNo ";
			paramName.add("custNo");
			paramValue.add(custNo);
			
		}
		if(StringUtil.isNotBlank(riskLevel)){
			hql = hql+" and dp.rickLevel=:rickLevel ";
			paramName.add("rickLevel");
			paramValue.add(riskLevel);
			
		}

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<LimitVo> result = this.find(hql, paramNames, paramValues);
		if (result != null && result.size() > 0) {
			return result.get(0);
		}
		return null;
	}
	
	/**
	 *获取资产池业务处理授权
	 *为解决同一客户资产和融资业务的并发操作，在进行资产或融资业务资金变更业务处理之前，先获授权。
	 *1、手工触发业务-授权获取失败，则直接终止本次业务操作，并给客户返回提示信息
	 *2、自动调度业务-授权获取失败，则直接终止本次业务操作，将任务进行排队处理
	 *@param apId 资产池ID
	 *@return true授权成功、false授权失败
	 */
	public boolean txGetAssetPoolTransAuthority(String apId) {
//		return true;
//		
		logger.info("获取资产池["+apId+"]业务处理授权...");
		
		try{
			StringBuffer sql = new StringBuffer("update PED_ASSET_POOL set DEAL_STATUS='");
			sql.append(PublicStaticDefineTab.DS_001).append("'");
			
			sql.append(" where");
			sql.append(" AP_ID='").append(apId).append("' and DEAL_STATUS='").append(PublicStaticDefineTab.DS_002).append("'");
			logger.info("加锁sql:"+sql.toString());
			int rowCount = dao.updateSQLReturnRows(sql.toString());
			return rowCount > 0  ? true : false;
		}catch(Exception e){
			logger.error(ErrorCode.ERR_MSG_996,e);
			return false;
		}
	}
	
	/**
	  *释放资产池业务处理授权，同时更是资产类型+资产池额度使用相关信息
	 *@param apId 资产池ID
	 *@return true释放授权成功、false释放授权失败
	 */
	public boolean txReleaseAssetPoolTransAuthoritySyncCredit(String apId,boolean isUnlock) {
		
		logger.info("释放资产池["+apId+"]业务处理授权，同时更是资产类型+资产池额度使用相关信息...");
		
		try{
			
			//根据票据池资产ID查询资产表归集表中可用额度
			StringBuffer hqlAssetSum = new StringBuffer("select sum(dto.assetAmount),dto.assetType from AssetRegister dto where dto.apId=? and (dto.riskType=? or dto.riskType=?) and dto.delFlag !='D'  group by dto.assetType");
			List assetSumParams = new ArrayList();
			assetSumParams.add(apId);
			assetSumParams.add(PoolComm.LOW_RISK);//低风险
			assetSumParams.add(PoolComm.HIGH_RISK);//高风险
			List assetSumList = dao.find(hqlAssetSum.toString(), assetSumParams);
			int size = assetSumList.size();
			Map<String,AssetRegister> assetRegSumMap = new HashMap<String,AssetRegister>();
			for(int i=0; i < size; i++) {
				Object[] arrObj = (Object[])assetSumList.get(i);
				AssetRegister assetSumReg = new AssetRegister();
				assetSumReg.setAssetAmount((BigDecimal)arrObj[0]);
				assetSumReg.setAssetType((String)arrObj[1]);//资产类型
				assetRegSumMap.put(assetSumReg.getAssetType(), assetSumReg);
			}
			BigDecimal zeroAmt = new BigDecimal(0);
			//高风险票据资产
			BigDecimal highRiskBillAmount = assetRegSumMap.containsKey(PoolComm.ED_PJC_01)?assetRegSumMap.get(PoolComm.ED_PJC_01).getAssetAmount() : new BigDecimal(0);
			BigDecimal surplusHighRiskBillAmount  = highRiskBillAmount.subtract(zeroAmt);//剩余高风险票据资产
			//低风险票据资产
			BigDecimal lowRiskBillAmount = assetRegSumMap.containsKey(PoolComm.ED_PJC)?assetRegSumMap.get(PoolComm.ED_PJC).getAssetAmount() : new BigDecimal(0);
			BigDecimal surplusLowRiskBillAmount = lowRiskBillAmount.subtract(zeroAmt);//剩余低风险票据资产
			//活期保证金
			BigDecimal currentDepositAmout = assetRegSumMap.containsKey(PoolComm.ED_BZJ_HQ)?assetRegSumMap.get(PoolComm.ED_BZJ_HQ).getAssetAmount() : new BigDecimal(0);
			BigDecimal surplusCurrentDepositAmout  = currentDepositAmout.subtract(zeroAmt);//剩余活期保证金
			//定期保证金--定期先不做处理
			//BigDecimal fixDepositAmout = assetRegSumMap.containsKey(PoolComm.ED_BZJ_DQ)?assetRegSumMap.get(PoolComm.ED_BZJ_DQ).getAssetAmount() : new BigDecimal(0);
			
			//根据票据池签约编号查询融资业务归集登记表已用额度
			StringBuffer hqlCreditSum = new StringBuffer("select sum(dto.occupyCredit),dto.riskType from CreditRegister dto where dto.apId=? and (dto.riskType=? or dto.riskType=?)   group by dto.riskType");
			List assetCreditParams = new ArrayList();
			assetCreditParams.add(apId);
			assetCreditParams.add(PoolComm.LOW_RISK);//低风险
			assetCreditParams.add(PoolComm.HIGH_RISK);//高风险
			List creditRegSumList = dao.find(hqlCreditSum.toString(), assetCreditParams);
		    size = creditRegSumList.size();
			Map<String,CreditRegister> creditRegSumMap = new HashMap<String,CreditRegister>();
			for(int i=0; i < size; i++) {
				Object[] arrObj = (Object[])creditRegSumList.get(i);
				CreditRegister creditSumReg = new CreditRegister();
				creditSumReg.setOccupyCredit((BigDecimal)arrObj[0]);
				creditSumReg.setRiskType((String)arrObj[1]);//风险类型
				creditRegSumMap.put(creditSumReg.getRiskType(), creditSumReg);
			}
			
			
			
			//高风险已用额度
			BigDecimal highRiskCredit = creditRegSumMap.containsKey(PoolComm.HIGH_RISK)?creditRegSumMap.get(PoolComm.HIGH_RISK).getOccupyCredit() : new BigDecimal(0);
			//低风险已用额度
			BigDecimal lowRiskCredit = creditRegSumMap.containsKey(PoolComm.LOW_RISK)?creditRegSumMap.get(PoolComm.LOW_RISK).getOccupyCredit() : new BigDecimal(0);
			
			/*1.PED_ASSET_TYPE计算顺序：先计算高风险，再计算低风险，再计算保证金
			 *2.高风险总已用若大于高风险总资产，则高风险总已用为高风险总资产，并记录已用高风险多于总资产的金额
			 *3.【低风险总已用+高风险多于总资产的金额】，若小于低风险资产则已用等于【低风险总已用+高风险多于总资产的金额】，若大于，则已用等于低风险总资产，并记录剩余需要占用的金额。
			 *4.将剩余需要占用的全部占用保证金
			 */
			//高风险融资业务使用额度计算
			BigDecimal surplusHighRiskCreditUsedHigh = highRiskCredit.subtract(surplusHighRiskBillAmount);
			 if(surplusHighRiskCreditUsedHigh.compareTo(zeroAmt) > 0) {
				 //说明-高风险票据金额已经用完
				 surplusHighRiskBillAmount = new BigDecimal(0);
				 //高风险剩余要占活期保证金的风险额度
				 BigDecimal surplusHighRiskCreditUsedLow = surplusHighRiskCreditUsedHigh.subtract(surplusLowRiskBillAmount);
				 if(surplusHighRiskCreditUsedLow.compareTo(zeroAmt) > 0) {
					 //说明-低风险票据金额已经用完
					 surplusLowRiskBillAmount = new BigDecimal(0);
					 //剩余高风险额度全部占用活期保证金
					 surplusCurrentDepositAmout = surplusCurrentDepositAmout.subtract(surplusHighRiskCreditUsedLow);
				 }else {
					 //低风险票据剩余可用额度
					 surplusLowRiskBillAmount  = surplusLowRiskBillAmount.subtract(surplusHighRiskCreditUsedHigh);
				 }
			 }else {
				 //高风险票据剩余可用额度
				 surplusHighRiskBillAmount =  surplusHighRiskBillAmount.subtract(highRiskCredit);
			 }
			 //低风险融资业务使用额度计算
			 BigDecimal surplusLowRiskCreditUsedLow = lowRiskCredit.subtract(surplusLowRiskBillAmount);
			 if(surplusLowRiskCreditUsedLow.compareTo(zeroAmt) > 0) {
				//说明-低风险票据金额已用用完
				 surplusLowRiskBillAmount = new BigDecimal(0);
				//剩余低风险额度全部占用活期保证金
				 surplusCurrentDepositAmout = surplusCurrentDepositAmout.subtract(surplusLowRiskCreditUsedLow);
			 }else {
				 surplusLowRiskBillAmount = surplusLowRiskBillAmount.subtract(lowRiskCredit);
			 }
			 //查询资产池对象
			 AssetPool ap = (AssetPool)dao.load(AssetPool.class, apId);
			 //查询资产类型
			 List<AssetType> assetTypeList = pedAssetTypeService.queryPedAssetTypeByAssetPool(ap);
			 //更新资产类型额度信息
			 for(AssetType atTmp : assetTypeList) {
				//高风险票据
				if(PoolComm.ED_PJC_01.equals(atTmp.getAstType())) {
					//衍生总额度
					atTmp.setCrdtTotal(highRiskBillAmount);
					//可用额度
					atTmp.setCrdtFree(surplusHighRiskBillAmount);
					//已用额度
					atTmp.setCrdtUsed(highRiskBillAmount.subtract(surplusHighRiskBillAmount));
				}else if(PoolComm.ED_PJC.equals(atTmp.getAstType())) {//低风险
					//衍生总额度
					atTmp.setCrdtTotal(lowRiskBillAmount);
					//可用额度
					atTmp.setCrdtFree(surplusLowRiskBillAmount);
					//已用额度
					atTmp.setCrdtUsed(lowRiskBillAmount.subtract(surplusLowRiskBillAmount));
				}else if(PoolComm.ED_BZJ_HQ.equals(atTmp.getAstType())) {//活期保证金
					//衍生总额度
					atTmp.setCrdtTotal(currentDepositAmout);
					//可用额度
					atTmp.setCrdtFree(surplusCurrentDepositAmout);
					//已用额度
					atTmp.setCrdtUsed(currentDepositAmout.subtract(surplusCurrentDepositAmout));
				}
			 }
			 this.txStoreAll(assetTypeList);
			//更新资产池处理标识及额度信息
			    StringBuffer sql = new StringBuffer("update PED_ASSET_POOL set ");
			    //衍生总额度=活期保证金+低风险票据+高风险票据
			    sql.append("  CRDT_TOTAL = ").append(currentDepositAmout.add(lowRiskBillAmount).add(highRiskBillAmount));
			    //可用额度=活期保证金剩余+低风险票据剩余+高风险票据剩余
			    sql.append(" , CRDT_FREE = ").append(surplusCurrentDepositAmout.add(surplusLowRiskBillAmount).add(surplusHighRiskBillAmount));
			    //已用额度=高风险额度+低风险额度
			    sql.append(" , CRDT_USED = ").append(highRiskCredit.add(lowRiskCredit));
			    
			    if(isUnlock){//需要解锁 
			     sql.append(" , DEAL_STATUS = '").append(PublicStaticDefineTab.DS_002).append("'");
			    }
			    
			    sql.append(" where");
			    sql.append(" AP_ID='").append(apId).append("'");
			    
			    logger.info("重新结算解锁sql"+sql.toString());
			    
			    dao.updateSQLReturnRows(sql.toString());
			    
			//   if(isUnlock){//需要解锁    
//			    ap.setDealStatus(PublicStaticDefineTab.DS_002);
//			    ap.setCrtTm(new Date());
			//   } 
			//   //衍生总额度=活期保证金+低风险票据+高风险票据
			//   ap.setCrdtTotal(currentDepositAmout.add(lowRiskBillAmount).add(highRiskBillAmount));
			//   //可用额度=活期保证金剩余+低风险票据剩余+高风险票据剩余
			//   ap.setCrdtFree(surplusCurrentDepositAmout.add(surplusLowRiskBillAmount).add(surplusHighRiskBillAmount));
			//   //已用额度=高风险额度+低风险额度
			//   ap.setCrdtUsed(highRiskCredit.add(lowRiskCredit));
//			      this.txStore(ap);
			      
			      //更新协议中的担保额度
			      PedProtocolDto pro = pedProtocolService.queryProtocolDto(null,null,ap.getBpsNo(),null,null,null);
//			      pro.setCreditUsedAmount(ap.getCrdtUsed());
//			      pro.setCreditFreeAmount(pro.getCreditamount().subtract(ap.getCrdtUsed()));
			      
			      pro.setCreditUsedAmount(highRiskCredit.add(lowRiskCredit));
			      pro.setCreditFreeAmount(pro.getCreditamount().subtract(highRiskCredit.add(lowRiskCredit)));
			      
			      pro.setOperateTime(new Date());
			      
			      this.txStore(pro);
		    
		    
			return true;
		}catch(Exception e){
			logger.error(ErrorCode.ERR_MSG_996,e);
			return false;
		}
	}
	
	/**
	  *释放资产池业务处理授权
	 *@param apId 资产池ID
	 *@return true释放授权成功、false释放授权失败
	 */
	public boolean txReleaseAssetPoolTransAuthority(String apId) {
		try{
			 //查询资产池对象
			 AssetPool ap = (AssetPool)dao.load(AssetPool.class, apId);
			ap.setDealStatus(PublicStaticDefineTab.DS_002);
		    dao.store(ap);
			return true;
		}catch(Exception e){
			logger.error(ErrorCode.ERR_MSG_996,e);
			return false;
		}
	}

	@Override
	public void txReleaseAssetPoolLock(String apId) {
		//查询资产池对象
		StringBuffer sql = new StringBuffer("update PED_ASSET_POOL set DEAL_STATUS='");
		sql.append(PublicStaticDefineTab.DS_002).append("'");
//		sql.append(" ,CRT_TM = (select sysdate from dual) ");
		sql.append(" ,CRT_TM = to_date('").append(DateUtils.getTime(new Date(),DateUtils.ORA_DATE_TIMES3_FORMAT)).append("','").append("yyyy-MM-dd HH24:mi:ss").append("') ");
		sql.append(" where");
		sql.append(" AP_ID='").append(apId).append("'");
		logger.info("解锁sql:"+sql.toString());
		dao.updateSQLReturnRows(sql.toString());
//		  
//		 AssetPool ap = (AssetPool)dao.load(AssetPool.class, apId);
//		 ap.setDealStatus(PublicStaticDefineTab.DS_002);
//		 ap.setCrtTm(new Date());
//		 this.txStore(ap);
	}

}
