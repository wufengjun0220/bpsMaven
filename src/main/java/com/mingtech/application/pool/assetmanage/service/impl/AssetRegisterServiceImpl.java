package com.mingtech.application.pool.assetmanage.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.cache.AssetTypeManageCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.service.HolidayService;
import com.mingtech.application.pool.assetmanage.domain.AssetQueryBean;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.domain.AssetRegisterHis;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.utils.DraftRange;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: h2
 * @日期: 2010-12-16 下午01:25:56
 * @描述: [BlackListBankManageServiceImpl]黑名单管理实现类
 */
@Service("assetRegisterService")
public class AssetRegisterServiceImpl extends GenericServiceImpl implements AssetRegisterService {
	private static final Logger logger = Logger.getLogger(AssetRegisterServiceImpl.class);
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private PoolBailEduService poolBailEduService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	
	/**
	 * 计算顺延到期日
	 * @param assetReg
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-7上午9:05:47
	 */
	public Date queryDelayDate(AssetRegister assetReg) throws Exception{
		Date delayDate = DateUtils.formatDate(assetReg.getAssetDueDt(), DateUtils.ORA_DATE_FORMAT);//顺延后的日期
		
		//从缓存中获取资产类型管理参数
		Map<String,String> assetTypeCache = AssetTypeManageCache.getAssetTypeManageMap(assetReg.getAssetType());
		int delayDays = 0;//设定的顺延天数 
		String holidayDelayType = null;//节假日顺延与否
		if(assetTypeCache != null) {
			delayDays = Integer.parseInt(assetTypeCache.get("assignDelayDay"));
			holidayDelayType = assetTypeCache.get("holidayDelayType");
		}

		//计算节假日顺延
		Date nextWorkDate =  DateUtils.formatDate(assetReg.getAssetDueDt(), DateUtils.ORA_DATE_FORMAT);
		if(PoolComm.YES.equals(holidayDelayType)){//节假日顺延
			nextWorkDate =  holidayService.getNextUnHolidayDate(assetReg.getAssetDueDt());
		}
		
		//计算设定延期天数
		if(delayDays != 0) {
			delayDate = DateUtils.adjustDateByDay(nextWorkDate,delayDays); 
		}else{
			delayDate = nextWorkDate;
		}
		
		delayDate = DateUtils.formatDate(delayDate, DateUtils.ORA_DATE_FORMAT);
		return delayDate;
	}

	/**
	* <p>方法名称: txAssetRegister|描述: 资产类型入池登记</p>
	* @param assetReg 资产登记信息
	* (资产登记对象必输项- apId、atId、custPoolName、certType、certCode、custSignNo、assetNo、assetType、riskType、assetAmount、assetDueDt;//资产到期日)
	* @return
	* @throws Exception
	*/
	public void txSaveAssetRegister(AssetRegister assetReg) throws Exception{
		
		Date delayDate = this.queryDelayDate(assetReg);//顺延到期日
		assetReg.setAssetDelayDueDt(delayDate);
		assetReg.setTransDate( new Date());//交易日期(不含时分秒)
		List<AssetRegister> list = new ArrayList<AssetRegister>();
		list.add(assetReg);
		financialAdviceService.txCreateList(list);
		
		//统一汉口历史表更新规则，都将最新一笔保存到历史表
		this.txSaveAssetRegisterHis(assetReg, PublicStaticDefineTab.STOCK_OUT_TYPE_DEPOSIT);
	}
	
	@Override
	public boolean txCurrentDepositAssetChange(String bpsNo,String account,BigDecimal assetAmount ) throws Exception{
		
		//根据票据池编号查询资产登记表中的该票据池的资产登记信息
		AssetRegister bailReg = this.getBailAssetRegisterByBpsNo(bpsNo);
		if(null != bailReg && !bailReg.getAssetNo().equals(account)){//保证金账号发生变化的情况
			
			//删除登记的原保证金资产,事务删除，因为存在调该方法的同一事务中jdbc查库的情况
			List<AssetRegister> bailList = new ArrayList<AssetRegister>();
			bailList.add(bailReg);
			financialAdviceService.txDelList(bailList);
		}
		
		//查询资产登记信息
		AssetRegister assetRegister = this.getAssetRegisterByCustSignNoAndAssetNo(bpsNo, account);
		if( null != assetRegister) {
			int cmpResult = assetRegister.getAssetAmount().compareTo(assetAmount);
			
			assetRegister.setAssetDueDt(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT));//每次都将保证金资产对象的到期日置为当日
			Date delayDate = this.queryDelayDate(assetRegister);//顺延到期日
			assetRegister.setAssetDelayDueDt(delayDate);//每次都将保证金资产对象的到期日置为当日

			//判断账户余额是否发生变化 
			if(cmpResult == 0) {//余额未变
				logger.info("活期保证金余额未发生变化 custSignNo="+bpsNo+" account="+account);
				List<AssetRegister> list = new ArrayList<AssetRegister>();
				list.add(assetRegister);
				financialAdviceService.txCreateList(list);
				return false;
			}else {
				assetRegister.setAssetAmount(assetAmount);
				assetRegister.setUpdateDate(new Date());
				List<AssetRegister> list = new ArrayList<AssetRegister>();
				list.add(assetRegister);
				financialAdviceService.txCreateList(list);
				//统一汉口历史表更新规则，都将最新一笔保存到历史表
				if(cmpResult > 0) {//余额减少
					this.txSaveAssetRegisterHis(assetRegister, PublicStaticDefineTab.STOCK_OUT_TYPE_DRAW);
				}else if(cmpResult < 0) {//余额增加
					this.txSaveAssetRegisterHis(assetRegister, PublicStaticDefineTab.STOCK_OUT_TYPE_DEPOSIT);
				}
			}
			
		}else {
			BailDetail bail = poolBailEduService.queryBailDetailByBpsNo(bpsNo);
			PedProtocolDto pro = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			this.txBailAssetRegister(bail, pro);
		}
		return true;
	}
	
	@Override
	public void txDraftStockOutAssetChange(String bpsNo, DraftPool pool,BigDecimal tradeAmt,String stockOutType) throws Exception{
		
		logger.info("资产登记表变更操作，变更票据池编号【"+bpsNo+"】变更票号【"+pool.getAssetNb()+"】子票号起【"+pool.getAssetNb()+"】子票号止【"+pool.getAssetNb()+"】变更类型【"+stockOutType+"】");
		AssetRegister assetRegister = null;
		if(pool.getDraftSource().equals(PoolComm.CS01)){//ecds票据资产表存储记录为票号加0-0
			//查询资产登记信息
			assetRegister = this.getAssetRegisterByCustSignNoAndAssetNo(bpsNo, pool.getAssetNb()+"-0-0");
		}else{//非ecds票据资产表存储记录为票号加子票起-子票止
			//查询资产登记信息
			assetRegister = this.getAssetRegisterByCustSignNoAndAssetNo(bpsNo, pool.getAssetNb()+"-"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo());
		}
		
		if(assetRegister != null) {
			if(assetRegister.getAssetAmount().compareTo(tradeAmt) == 0){
				//删除票据资产登记信息
				dao.delete(assetRegister);
				//保存删除历史
				assetRegister.setDelFlag(PublicStaticDefineTab.VALUE_YES);//删除标记
				this.txSaveAssetRegisterHis(assetRegister, PublicStaticDefineTab.STOCK_OUT_TYPE_OUTPOOL);
			}else{
				//需要做额度拆分
				/**
				 * 生成拆票的子票区间
				 */
				DraftRange range = DraftRangeHandler.buildLimitNewBeginAndEndDraftRange(pool.getAssetAmt(), tradeAmt, pool.getStandardAmt(), pool.getBeginRangeNo(), pool.getEndRangeNo());
				
				
				AssetRegister arSPlitNew = new AssetRegister();
				
				BeanUtil.beanCopy(assetRegister, arSPlitNew);//原始数据历史记录
				
				/**
				 * 拆分做数据变更
				 */
				assetRegister.setAssetNo(pool.getAssetNb()+ "-" + range.getEndDraftRange() + "-" + pool.getEndRangeNo());
				assetRegister.setAssetAmount(DraftRangeHandler.formatBillNos(range.getEndDraftRange(), pool.getEndRangeNo()));
				//保存删除历史
				arSPlitNew.setDelFlag(PublicStaticDefineTab.VALUE_YES);//删除标记
				arSPlitNew.setAssetNo(pool.getAssetNb()+ "-" + pool.getBeginRangeNo() + "-" + range.getEndDraftRange());
				arSPlitNew.setAssetAmount(DraftRangeHandler.formatBillNos(pool.getBeginRangeNo(), range.getEndDraftRange()));
				//保存新拆分的资产登记表信息
				
				dao.store(assetRegister);
				this.txSaveAssetRegisterHis(arSPlitNew, PublicStaticDefineTab.STOCK_OUT_TYPE_OUTPOOL);
			}
			

		}else {
			logger.error(ErrorCode.ERR_NO_ASSET_REGISTER+" bpsNo="+bpsNo+" draftNo="+pool.getAssetNb()+"beginRangeNo"+pool.getBeginRangeNo()+"endRangeNo"+pool.getEndRangeNo());
		}
	}
	
	
	 /**
		* <p>描述:保存资产变更历史信息 </p>
		* @param assetRegister 发生变更前的资产登记信息
		* @param stockOutType 出库类型-01支取、02到期、03到期（从PublicStaticDefineTab中获取）
		* @param assetAmount 账户余额
		* @return
		* @throws Exception
		*/
	@Override
	public void txSaveAssetRegisterHis(AssetRegister assetRegister,String stockOutType) throws Exception{
    	    //历史资产登记信息
    	    AssetRegisterHis assetRegHis = new AssetRegisterHis();
    	    assetRegHis.setApId(assetRegister.getApId());//资产池表主键
    	    assetRegHis.setAtId(assetRegister.getAtId());//资产类表id
    	    assetRegHis.setCustPoolName(assetRegister.getCustPoolName());//客户资产池名称
    	    assetRegHis.setCertType(assetRegister.getCertType());//证件类型:01组织机构代码、02统一授信编码、03客户号
    	    assetRegHis.setCertCode(assetRegister.getCertCode());//证件号码
    	    assetRegHis.setBpsNo(assetRegister.getBpsNo());//客户签约编号
    	    assetRegHis.setAssetNo(assetRegister.getAssetNo());//资产编号-存账号或票据号
    	    assetRegHis.setAssetType(assetRegister.getAssetType());//资产类型-ED_10低风险票据、ED_20高风险票据、ED_21活期保证金、ED_22定期保证金
    	    assetRegHis.setRiskType(assetRegister.getRiskType());//风险类型-低风险FX_01、高风险FX_02、不在风险名单FX_03
    	    assetRegHis.setAssetAmount(assetRegister.getAssetAmount());//资金金额
    	    assetRegHis.setAssetDueDt(assetRegister.getAssetDueDt());//资产到期日
    	    assetRegHis.setAssetDelayDueDt(assetRegister.getAssetDelayDueDt());//资产延迟到期日
    	    assetRegHis.setStockOutType(stockOutType);//出库类型-01支取、02到期、03存入
    	    assetRegHis.setTransDate(DateUtils.getWorkDayDate());//交易日期(不含时分秒)
    	    assetRegHis.setCreateDate(new Date());
    	    assetRegHis.setUpdateDate(new Date());
    	    dao.store(assetRegHis);
    	    
		
		}
	
	/**
	* <p>描述:根据客户池签约编号和资产编号查询资产登记信息 </p>
	* @param bpsNo 签约池编号
	* @param account 账号
	* @param assetAmount 账户余额
	* @return 资产登记信息
	*/
	public AssetRegister getAssetRegisterByCustSignNoAndAssetNo(String bpsNo,String account) {
		StringBuffer hql = new StringBuffer(" from AssetRegister dto where dto.bpsNo=? and dto.assetNo=?");
		// 查询条件-签约池编号、资产编号
		List params = new ArrayList(); 
		params.add(bpsNo);
		params.add(account);//
		List list = dao.find(hql.toString(), params);
		if(list.isEmpty() == false) {
			return (AssetRegister) list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据票据池编号查询该票据池所对应的资产登记信息
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-9-15下午2:22:27
	 */
	public AssetRegister getBailAssetRegisterByBpsNo(String bpsNo) {
		StringBuffer hql = new StringBuffer(" from AssetRegister dto where dto.bpsNo=? and dto.assetType=?");
		List params = new ArrayList(); 
		params.add(bpsNo);
		params.add(PoolComm.ED_BZJ_HQ);//
		List list = dao.find(hql.toString(), params);
		if(list.isEmpty() == false) {
			return (AssetRegister) list.get(0);
		}
		return null;
	}


	
	@Override
	public List<AssetRegister> queryAssetRegisterByAssetNo(List<String> assetNos) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select asset from AssetRegister as asset where 1=1 ");
		if(assetNos!=null){
			sb.append("and asset.assetNo in (:assetNos) ");
			keys.add("assetNos");
			values.add(assetNos);
		}

		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List<AssetRegister> list = this.find(sb.toString(), paramNames, paramValues);
		if(null != list &&  list.size()>0){
			return list;
		}
		return null;
	}
	

	@Override
	public String getEntityName() {
		return AssetRegisterServiceImpl.class.getName();
	}


	@Override
	public Class getEntityClass() {
		return AssetRegisterServiceImpl.class;
	}

	@Override
	public AssetRegister txBillAssetRegister(DraftPool draftPool,PedProtocolDto pro)throws Exception {
		
		
		List<String> billNo = new ArrayList<String>();
		billNo.add(draftPool.getAssetNb());
		List<AssetRegister> arList = queryAssetRegisterByAssetNo(billNo);
		
		AssetType at = null;
		if(PoolComm.LOW_RISK.equals(draftPool.getRickLevel())){//低风险			
			at = pedAssetTypeService.queryPedAssetTypeByProtocol(pro, PoolComm.ED_PJC);
		}else{//高风险	
			at = pedAssetTypeService.queryPedAssetTypeByProtocol(pro, PoolComm.ED_PJC_01);
		}
		AssetRegister ar = null;
		
		if(null == arList || arList.size() == 0){//首次登记			
			ar = new AssetRegister();
		}else{
			ar = arList.get(0);
		}
		
		ar.setApId(at.getApId());//资产池表主键
		ar.setAtId(at.getId());//资产类表id
		ar.setCustPoolName(pro.getPoolName());//客户资产池名称
		ar.setCertType(PublicStaticDefineTab.CERT_TYPE_03);//证件类型:03客户号
		ar.setCertCode(draftPool.getCustNo());//证件号码
		ar.setBpsNo(pro.getPoolAgreement());//客户签约编号
		
		/**
		 * 资产编号存储
		 * 用票号拼接票据起始截止号
		 * 原ecds或等分化的不可拆分的票起始截止区间都是0-0;可拆分的等分化票据票起始截止区间为正常的起始截止拼接
		 */
		if(StringUtils.isNotBlank(draftPool.getSplitFlag()) && draftPool.getSplitFlag().equals("1")){//可拆分的等分化票据
			ar.setAssetNo(draftPool.getAssetNb()+ "-" + draftPool.getBeginRangeNo() + "-" + draftPool.getEndRangeNo());//资产编号-存账号或票据号
		}else{
			ar.setAssetNo(draftPool.getAssetNb()+"-0-0");//资产编号-存账号或票据号
		}
		ar.setRiskType(draftPool.getRickLevel());//风险类型-低风险FX_01、高风险FX_02、不在风险名单FX_03
		if(PoolComm.LOW_RISK.equals(draftPool.getRickLevel())){//低风险
			ar.setAssetType(PoolComm.ED_PJC);//资产类型-ED_10低风险票据				
		}else{
			ar.setAssetType(PoolComm.ED_PJC_01);//资产类型-ED_20高风险票据
		}
		ar.setAssetAmount(draftPool.getAssetAmt());//资金金额
		ar.setCreateDate(new Date());//创建日期(含时分秒)
		ar.setUpdateDate(new Date());//更新日期(含时分秒)
		ar.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//逻辑删除标记 N未删除
		ar.setAssetDueDt(draftPool.getPlDueDt());//资产到期日
		

	    /*
	     * 落库
	     */
	    this.txSaveAssetRegister(ar);
		
		return ar;
	}

	@Override
	public AssetRegister txBailAssetRegister(BailDetail bail,PedProtocolDto pro) throws Exception {
		
		List<String> accNo = new ArrayList<String>();
		accNo.add(bail.getAssetNb());
		List<AssetRegister> arList = queryAssetRegisterByAssetNo(accNo);
		AssetRegister ar = null;
		
		if(null == arList){//首次登记
			
			ar = new AssetRegister();
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(pro, PoolComm.ED_BZJ_HQ);
			ar.setApId(at.getApId());//资产池表主键
		    ar.setAtId(at.getId());//资产类表id
		    ar.setCustPoolName(pro.getPoolName());//客户资产池名称
		    ar.setCertType(PublicStaticDefineTab.CERT_TYPE_03);//证件类型:03客户号
		    ar.setCertCode(pro.getCustnumber());//证件号码
		    ar.setBpsNo(pro.getPoolAgreement());//客户签约编号
		    ar.setAssetNo(bail.getAssetNb());//资产编号-存账号或票据号
		    ar.setAssetType(PoolComm.ED_BZJ_HQ);//资产类型-ED_10低风险票据
		    ar.setRiskType(PoolComm.LOW_RISK);//风险类型-低风险FX_01、高风险FX_02、不在风险名单FX_03
		    ar.setAssetAmount(bail.getAssetLimitFree());//资金金额
		    ar.setAssetDueDt(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT));//到期日
		    ar.setCreateDate(new Date());//创建日期(含时分秒)
		    ar.setUpdateDate(new Date());//更新日期(含时分秒)
		    ar.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//逻辑删除标记 N未删除
		    
		}else{
			
			ar = arList.get(0);
			ar.setAssetNo(bail.getAssetNb());//资产编号-存账号或票据号
			ar.setAssetAmount(bail.getAssetLimitFree());//资金金额
			ar.setUpdateDate(new Date());//更新日期(含时分秒)
			ar.setAssetDueDt(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT));//到期日
			
		}

		/*
	     * 落库
	     */
	    this.txSaveAssetRegister(ar);
	    
		return ar;
	}

	@Override
	public List<AssetRegister> queryAssetRegisterExceptAssetNos(String bpsNo,List<String> assetNos) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select asset from AssetRegister as asset where 1=1 ");
		
		sb.append("and asset.bpsNo =:bpsNo ");
		keys.add("bpsNo");
		values.add(bpsNo);
		
		if(assetNos!=null){
			sb.append("and asset.assetNo not in (:assetNos) ");
			keys.add("assetNos");
			values.add(assetNos);
		}

		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List<AssetRegister> list = this.find(sb.toString(), paramNames, paramValues);
		if(null != list &&  list.size()>0){
			return list;
		}
		return null;
	}

	@Override
	public List<AssetRegister> queryAssetRegisterList(AssetQueryBean bean) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select asset from AssetRegister as asset where 1=1 ");
	
		//该方法后续根据需要的条件拓展完善
		if(bean!=null){
			
			
			if(null != bean.getStartDate()){				
				sb.append("and asset.assetDelayDueDt >=:assetDelayDueDtStart) ");
				keys.add("assetDelayDueDtStart");
				values.add(bean.getStartDate());
			}
			if(null != bean.getEndDate()){				
				sb.append("and asset.assetDelayDueDt <=:assetDelayDueDtEnd ");
				keys.add("assetDelayDueDtEnd");
				values.add(bean.getEndDate());
			}
			
			if(null!=bean.getAssetTypeList() && bean.getAssetTypeList().size()>0){				
				sb.append("and asset.assetType in (:assetTypeList) ");
				keys.add("assetTypeList");
				values.add(bean.getAssetTypeList());
			}
			
		}

		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List<AssetRegister> list = this.find(sb.toString(), paramNames, paramValues);
		if(null != list &&  list.size()>0){
			return list;
		}
		return null;
	}
	
}
