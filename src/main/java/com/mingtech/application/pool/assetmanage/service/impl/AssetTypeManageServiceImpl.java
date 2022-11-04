package com.mingtech.application.pool.assetmanage.service.impl;


import com.mingtech.application.cache.AssetTypeManageCache;
import com.mingtech.application.ecds.common.service.HolidayService;
import com.mingtech.application.pool.assetmanage.domain.AssetTypeManage;
import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.common.PoolComm;

import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 版权所有: 北明明润（北京）科技有限责任公司
 * </p>
 * 
 * @作者: zjt
 * @日期: 2021-5-18
 * @描述: [AssetTypeManageServiceImpl]资产分类管理实现类
 */
@Service("assetTypeManageService")
public class AssetTypeManageServiceImpl extends GenericServiceImpl implements AssetTypeManageService {
	private static final Logger logger = Logger.getLogger(AssetTypeManageServiceImpl.class);
	@Autowired
	private HolidayService holidayService;


	/**
	* <p>描述: 查询所有资产类型管理信息</p>
	* @return list 资产类型管理信息
	* @throws Exception
	*/
	public List queryAllAssetTypeManages() {
		String  hql = "from AssetTypeManage ";
		return this.find(hql);
	}


	/**
	 * <p>描述: 按条件查询所有资产类型管理信息</p>
	 * @return list 资产类型管理信息
	 * @throws Exception
	 */
	@Override
	public String queryAssetTypeManage(AssetTypeManage assetTypeManage, User user, Page page) throws Exception {
		StringBuffer sb = new StringBuffer();
		List valueList = new ArrayList(); // 要查询的值列表
		sb.append("select asset from AssetTypeManage asset where 1 = 1 ");
		if(StringUtils.isNotBlank(assetTypeManage.getAssetType())){
			sb.append(" and asset.assetType = ? ");
			valueList.add(assetTypeManage.getAssetType());
		}
		if(StringUtils.isNotBlank(assetTypeManage.getRiskType())){
			sb.append(" and asset.riskType = ? ");
			valueList.add(assetTypeManage.getRiskType());
		}
		sb.append(" order by asset.updateDate desc ");
		List list=this.find(sb.toString(),valueList,page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}
	/**
	 * <p>描述: 按条件查询所有资产类型管理历史信息</p>
	 * @return list 资产类型管理历史信息
	 * @throws Exception
	 */
	@Override
	public String queryAssetTypeManageHis(AssetTypeManage assetTypeManage, User user, Page page) throws Exception {
		StringBuffer sb = new StringBuffer();
		List valueList = new ArrayList(); // 要查询的值列表
		sb.append("select asset.assetType,asset.riskType,asset.amountType,asset.duedateType," +
				"his.holidayDelayType,his.assignDelayDay,his.updateUserName,his.updateDate,his.delFlag " +
				" from AssetTypeManage asset,AssetTypeManageHis his  where  asset.id = his.atManageId  ");
		if(StringUtils.isNotBlank(assetTypeManage.getAssetType())){
			sb.append(" and asset.assetType = ? ");
			valueList.add(assetTypeManage.getAssetType());
		}
		if(StringUtils.isNotBlank(assetTypeManage.getRiskType())){
			sb.append(" and asset.riskType = ? ");
			valueList.add(assetTypeManage.getRiskType());
		}
		sb.append(" order by his.updateDate desc ");
		List list = this.find(sb.toString(),valueList,page);
		List qyList = new ArrayList();
		AssetTypeManage asset = null;
		if(null!= list && list.size()>0){
			for (int i=0;i<list.size();i++){
				asset = new AssetTypeManage();
				Object[] obj = (Object[]) list.get(i);
				asset.setAssetType((String) obj[0]);
				asset.setRiskType((String) obj[1]);
				asset.setAmountType((String) obj[2]);
				asset.setDuedateType((String) obj[3]);
				asset.setHolidayDelayType((String) obj[4]);
				asset.setAssignDelayDay((Integer) obj[5]);
				asset.setUpdateUserName((String) obj[6]);
				asset.setUpdateDate((Date) obj[7]);
				asset.setDelFlag((String) obj[8]);
				qyList.add(asset);
			}
			Map map = new HashMap();
			map.put("totalProperty", "results," + page.getTotalCount());
			map.put("root", "rows");
			return  JsonUtil.fromCollections(qyList, map);
		}else{
			return  null;
		}
	}

	@Override
	public String getEntityName() {
		return AssetTypeManageServiceImpl.class.getName();
	}

	@Override
	public Class getEntityClass() {
		return AssetTypeManageServiceImpl.class;
	}


	@Override
	public long queryDelayDays(String riskLevel, Date dueDate) throws Exception {
		
		logger.info("高低风险票据的顺延到期日计算...");
		
		String assetType = "";
		if(PoolComm.LOW_RISK.equals(riskLevel)){
			assetType = PoolComm.ED_PJC;
		}else if(PoolComm.HIGH_RISK.equals(riskLevel)){
			assetType = PoolComm.ED_PJC_01;
		}
		Map<String,String> assetTypeCache = AssetTypeManageCache.getAssetTypeManageMap(assetType);//资产类型-ED_10低风险票据、ED_20高风险票据、ED_21活期保证金、ED_22定期保证金
		int delayDays = 0;//设定的顺延天数 
		String holidayDelayType = null;//节假日顺延与否
		if(assetTypeCache != null) {
			delayDays = Integer.parseInt(assetTypeCache.get("assignDelayDay"));
			holidayDelayType = assetTypeCache.get("holidayDelayType");
		}
		
		//计算节假日顺延					
		Date nextWorkDate =  DateUtils.formatDate(dueDate, DateUtils.ORA_DATE_FORMAT);
		if(PoolComm.YES.equals(holidayDelayType)){//节假日顺延
			nextWorkDate =  holidayService.getNextUnHolidayDate(dueDate);
		}
		
		//计算设定延期天数
		Date actDate =null;
		
		if(PoolComm.NOTIN_RISK.equals(riskLevel)){//不在风险名单中
			actDate = nextWorkDate;//不顺延
		}else{
			actDate = DateUtils.adjustDateByDay(nextWorkDate,delayDays);
		}
		
		long days = DateTimeUtil.getDaysBetween( dueDate,actDate);//顺延天数
		
		return days;
	}
}
