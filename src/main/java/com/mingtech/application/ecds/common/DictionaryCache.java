package com.mingtech.application.ecds.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.common.domain.Dictionary;
import com.mingtech.application.common.logic.IDictionaryService;
import com.mingtech.application.ecds.common.service.DictionaryCacheService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.sysmanage.domain.ProductTypeDto;
import com.mingtech.application.sysmanage.service.ProductTypeService;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: yufei
 * @日期: Jun 15, 2009 10:03:35 AM
 * @描述: [DictionaryCache]数据字典缓存Cache
 */
public class DictionaryCache extends GenericServiceImpl implements DictionaryCacheService{

	public static final Map dictionaryMap = Collections
			.synchronizedMap(new HashMap());

	public static final Map billTypeMap = Collections
			.synchronizedMap(new HashMap());// 票据类型
	public static final Map billMediaMap = Collections
			.synchronizedMap(new HashMap());// 票据介质
	public static final Map sysLogTypeMap = Collections
			.synchronizedMap(new HashMap());// 日志类型
	
	public static final Map cantAttnMarkMap = Collections
    .synchronizedMap(new HashMap());// 不得转让标记
	
	/**
	 * 账户类型
	 */
	private static final Map accountTypeMap = Collections.synchronizedMap(new HashMap());//账户类型0195
	
	
	private static final Map overFlagTypeMap = Collections.synchronizedMap(new HashMap());// 逾期标识类别状态
	
	/** 票据池字典*/
	private static final Map poolDictMap = Collections.synchronizedMap(new HashMap());
	
	//==============票交所
	
	/** 行业分类*/
	/** 省份*/
	private static final Map AreaMap = Collections.synchronizedMap(new HashMap());
	/** 企业规模*/
	private static final Map SCompanySizeNumMap = Collections.synchronizedMap(new HashMap());
	
	/** 票交所企业规模 */ 
	private static final Map PjsCompanySizeNumMap = Collections.synchronizedMap(new HashMap());
	
	/**票交所地区类型*/
	private static final Map PjsAreaMap  = Collections.synchronizedMap(new HashMap());
	
	/**
	 * 系统产品编码ProductTypeDto
	 */
	private static final Map<String,String> productTypeMap =  Collections.synchronizedMap(new HashMap<String,String>());//系统产品编码ProductTypeDto
	public IDictionaryService dictionaryService;
	private ProductTypeService productTypeService;

	/**
	 * 票据池省份，用于黑灰名单-承兑人所在地区
	 */
	private static final Map pjcAreaMap = Collections.synchronizedMap(new HashMap());
	
	
	
    //公共字典管理
	private static final Map commonYesOrNoMap = Collections.synchronizedMap(new HashMap());//是、否
	//修改模式
	private static final Map modeTypeMap = Collections.synchronizedMap(new HashMap());
	//处理状态
	private static final Map dealStatusMap = Collections.synchronizedMap(new HashMap());
	private static final Map crdtStatusMap = Collections.synchronizedMap(new HashMap());
	private static final Map acptStatusMap = Collections.synchronizedMap(new HashMap());

	
	public void initDictionaryCache(){
		// 获取所有数据字典项
		List list = dictionaryService.queryAllDictionary();
		for(int i = 0; i < list.size(); i++){
			Dictionary dic = (Dictionary) list.get(i);
			if("0_20".equalsIgnoreCase(dic.getParentCode())){
				billTypeMap.put(dic.getCode(), dic.getName());// 票据类型
				continue;
			}
			if("0_21".equalsIgnoreCase(dic.getParentCode())){
				billMediaMap.put(dic.getCode(), dic.getName());// 票据介质
				continue;
			}
			if("0_37".equalsIgnoreCase(dic.getParentCode())){
				dealStatusMap.put(dic.getCode(), dic.getName());// 处理状态
				continue;
			}
			if("0_38".equalsIgnoreCase(dic.getParentCode())){
				crdtStatusMap.put(dic.getCode(), dic.getName());// 处理状态
				continue;
			}
			if("0_9".equalsIgnoreCase(dic.getParentCode())){
				cantAttnMarkMap.put(dic.getCode(), dic.getName());//不得转让标记
				continue;
			}
			if("0_195".equalsIgnoreCase(dic.getParentCode())){//账户类型
				accountTypeMap.put(dic.getCode(),dic.getName());
				continue;
			}
			if("0_71".equalsIgnoreCase(dic.getParentCode())){
				overFlagTypeMap.put(dic.getCode(),dic.getName());//逾期标识类别状态
				dictionaryMap.put(dic.getCode(), dic.getName());
				continue;
			}
			if("pjc_06".equalsIgnoreCase(dic.getParentCode())){//修改模式
				modeTypeMap.put(dic.getCode(),dic.getName());
				continue;
			}
			if("pjc_07".equalsIgnoreCase(dic.getParentCode())){
				acptStatusMap.put(dic.getCode(), dic.getName());// 银承批次状态
				continue;
			}
			
			if ("0_182".equalsIgnoreCase(dic.getParentCode())//信贷产品状态
					|| "0_183".equalsIgnoreCase(dic.getParentCode())//信贷产品类型
					|| "0_184".equalsIgnoreCase(dic.getParentCode())//信贷产品类型
					|| "0_185".equalsIgnoreCase(dic.getParentCode())//票据池|代保管票据状态
					|| "0_187".equalsIgnoreCase(dic.getParentCode())//
					|| "0_188".equalsIgnoreCase(dic.getParentCode())//
					|| "0_196".equalsIgnoreCase(dic.getParentCode())//账户类型
					) {
				dictionaryMap.put(dic.getCode(), dic.getName());
					poolDictMap.put(dic.getCode(), dic.getName());
					continue;
				}else if("CDS00003".equalsIgnoreCase(dic.getParentCode())){//省份
					AreaMap.put(dic.getCode(), dic.getName());
					continue; 
				}else if("0_201".equalsIgnoreCase(dic.getParentCode())){//企业规模
					SCompanySizeNumMap.put(dic.getCode(), dic.getName());
					continue; 
				} else if("0_205_07".equalsIgnoreCase(dic.getParentCode())){//票据所企业规模
					PjsCompanySizeNumMap.put(dic.getCode(), dic.getName());
					continue; 
				} else if("0_205_08".equalsIgnoreCase(dic.getParentCode())){//票交所地区类型
					PjsAreaMap.put(dic.getCode(), dic.getName());
					continue;
				}else if("YES_OR_NO_DIC".equalsIgnoreCase(dic.getParentCode())){//功能字典是否
					commonYesOrNoMap.put(dic.getCode(), dic.getName()+"-"+dic.getCode());
				}else if("SYS_LOG_TYPE".equalsIgnoreCase(dic.getParentCode())){//系统日志类型
					sysLogTypeMap.put(dic.getCode(), dic.getName()+"-"+dic.getCode());
				}
			
			dictionaryMap.put(dic.getCode(), dic.getName());
		}
		List prodType = this.productTypeService.getAllProductList();
		for(int i=0;i<prodType.size();i++){
			ProductTypeDto info = (ProductTypeDto)prodType.get(i);
			productTypeMap.put(info.getProduct_id(), info.getSProdtName());
		}
		// 加载票据池所有区域
		List areaList = dictionaryService.getAllDictionaryByParentCode("PJC_01", 0);
		if(areaList != null && areaList.size() > 0) {
			for(int i=0;i<areaList.size();i++){
				Object[] dic = (Object[]) areaList.get(i);
				if(dic != null && dic.length == 2) {
					if(StringUtil.isNotBlank(dic[0].toString()) && StringUtil.isNotBlank(dic[1].toString())) {
						pjcAreaMap.put(dic[0].toString(), dic[1].toString());
					}
				}
			}
		}
	}
	
	/**
	 * <p>方法名称: getStatusName|描述: 获取到期无条件支付委托中文名</p>
	 * @param statusCode
	 * @return
	 */
	public static String getModeTypeName(String dCode){
		return modeTypeMap.containsKey(dCode)?(String)modeTypeMap.get(dCode):"";
	}

	/**
	 * <p>方法名称: getStatusName|描述: 获取不得转让中文名</p>
	 * @param statusCode
	 * @return
	 */
	public static String getCantAttnMarkCnName(String dCode){
		return cantAttnMarkMap.containsKey(dCode)?(String)cantAttnMarkMap.get(dCode):"";
	}

	/**
	 * <p>方法名称: getStatusName|描述: 获取业务状态的名称</p>
	 * @param statusCode
	 * @return
	 */
	public static String getStatusName(String statusCode){
		return dictionaryMap.containsKey(statusCode) ? (String) dictionaryMap
				.get(statusCode) : "";
	}

	/**
	 * <p>方法名称: getBillType|描述: 获取票据名称</p>
	 * @param statusCode
	 * @return
	 */
	public static String getBillType(String statusCode){
		return billTypeMap.containsKey(statusCode) ? (String) billTypeMap
				.get(statusCode) : "";
	}
	/**
	 * <p>方法名称: getBillMedia|描述: 获取票据介质</p>
	 * @param statusCode
	 * @return
	 */
	public static String getBillMedia(String statusCode){
		return billMediaMap.containsKey(statusCode) ? (String) billMediaMap
				.get(statusCode) : "";
	}

	/**
	* <p>方法名称: getOverflagtypemap|描述: 根据code获取逾期标识类别状态名称</p>
	* @return
	*/
	public static String getOverFlagTypeMap(String statusCode){
		return overFlagTypeMap.containsKey(statusCode) ? (String) overFlagTypeMap
				.get(statusCode) : "";
	}
	

	public IDictionaryService getDictionaryService(){
		return dictionaryService;
	}

	public void setDictionaryService(IDictionaryService dictionaryService){
		this.dictionaryService = dictionaryService;
	}
	
	/**
	 * 根据产品编码获取产品名称
	 * @param prodId
	 * @return
	 */
	public static String getProductName(String prodId){
		return productTypeMap.containsKey(prodId)?productTypeMap.get(prodId):"";
	}
	
	/**
	 * <p>方法名称: getCollStatusName|描述: 获取解付状态中文名</p>
	 * @param statusCode
	 * @return
	 */
	public static String getCollStatusName(String statusCode){
		return dictionaryMap.containsKey(statusCode) ? (String) dictionaryMap
				.get(statusCode) : "";
	}
	 /**
     * 清空交易代码集合
     */
    public static void clear() {
    	dictionaryMap.clear();
    	billTypeMap.clear();
    	billMediaMap.clear();
    	cantAttnMarkMap.clear();
    	overFlagTypeMap.clear();
    	productTypeMap.clear();
    	AreaMap.clear();
    	SCompanySizeNumMap.clear();
    	pjcAreaMap.clear();
    	commonYesOrNoMap.clear();
    	sysLogTypeMap.clear();
    }

    public void reloadDictionary(){
    	initDictionaryCache();

    }

	public Class getEntityClass(){
		// TODO Auto-generated method stubs
		return null;
	}

	public String getEntityName(){
		// TODO Auto-generated method stubs
		return null;
	}
	/**
	 * <p>
	 * 方法名称: getStatusName|描述: 获取业务状态的名称
	 * 首先在票据池字典map里找,如果找不到在去系统字典map里去找
	 * </p>
	 * 
	 * @param statusCode
	 * @return
	 */
	public static String getFromPoolDictMap(String code) {
		return poolDictMap.containsKey(code) ? (String) poolDictMap.get(code) : getStatusName(code);
	}

	public ProductTypeService getProductTypeService() {
		return productTypeService;
	}

	public void setProductTypeService(ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}
 

	public static String getArea(String area) {
		return AreaMap.containsKey(area) ? (String) AreaMap
				.get(area) : "";
	}

	public static String getSCompanySizeNum(String sCompanySizeNum) {
		return SCompanySizeNumMap.containsKey(sCompanySizeNum) ? (String) SCompanySizeNumMap
				.get(sCompanySizeNum) : "";
	}
	/**
	 * <p>方法名称:  获取系统日志类型</p>
	 * @param code 日志类型
	 */
	public static String getSysLogTypeMap(String code){
//		if(startRedis){
//			return getDicNameFromRedis("sysLogTypeMap",code);
//		}
		return sysLogTypeMap.containsKey(code)?(String)sysLogTypeMap.get(code):code;
	}
	/**
	 * 账号类型 数据字典
	 * @param statusCode
	 * @return
	 */
	public static String getAccountTypeMapMap(String statusCode){
		return accountTypeMap.containsKey(statusCode) ? (String) accountTypeMap
				.get(statusCode) : "";
	}
	public static String getPjsCompanySizeNum(String pjsCompanySizeNum) {
		return PjsCompanySizeNumMap.containsKey(pjsCompanySizeNum) ? (String) PjsCompanySizeNumMap
				.get(pjsCompanySizeNum) : "";
	}

	public static String getPjsAreaName(String pjsAreaCode) {
		return PjsAreaMap.containsKey(pjsAreaCode) ? (String) PjsAreaMap
				.get(pjsAreaCode) : "";
	}
	public static String getbillMediaMapName(String pjsAreaCode) {
		return billMediaMap.containsKey(pjsAreaCode) ? (String) billMediaMap
				.get(pjsAreaCode) : "";
	}
	public static String getbillTypeMapName(String pjsAreaCode) {
		return billTypeMap.containsKey(pjsAreaCode) ? (String) billTypeMap
				.get(pjsAreaCode) : "";
	}
	
	/**
	 * <p>方法名称: getPjcAreaName|描述: 票据池省份</p>
	 * @param province
	 * @return
	 */
	public static String getPjcAreaName(String province) {
		return pjcAreaMap.containsKey(province) ?  (String) pjcAreaMap.get(province) : "";
	}
	
	public static String getCommonYesOrNo(String code){
//		if(startRedis){
//			return getDicNameFromRedis("commonYesOrNoMap",code);
//		}
		return commonYesOrNoMap.containsKey(code) ? (String) commonYesOrNoMap
				.get(code) : code;
	}
	
	/**    20210619 审批系统添加 start    */
	public static String getDicNameFromRedis(String mapName,String code){
		if(code == null){
			return code;
		}
		RedisUtils redisrCache = (RedisUtils) SpringContextUtil.getBean("redisrCache");
		String name = (String)redisrCache.hget(mapName,code);
		return StringUtils.isNotBlank(name)?name:code;
	}
	public static String getAuditStatusMap(String code){

		return getDicNameFromRedis("auditStatusMap",code);
	}
	public static String getAuditTypeMap(String code){
		return getDicNameFromRedis("auditTypeMap",code);
	}
	public static String getAuditBusiTypeMap(String code){
		return getDicNameFromRedis("auditBusiTypeMap",code);
	}
	public static String getBillTypeMapName(String code) {
		return getDicNameFromRedis("billTypeMap",code);
	}
	public static String getBillMediaMapName(String code) {
		return getDicNameFromRedis("billMediaMap",code);
	}
	//获取审批参数数据类型
	public static String getAuditParamDataTypeMap(String code){
		return getDicNameFromRedis("auditParamDataTypeMap",code);
	}

	public static String getDealstatusmap(String dCode) {
		return dealStatusMap.containsKey(dCode)?(String)dealStatusMap.get(dCode):"";
	}
	public static String getCrdtStatuspMap(String dCode) {
		return crdtStatusMap.containsKey(dCode)?(String)crdtStatusMap.get(dCode):"";
	}
	public static String getAcptStatusMap(String dCode) {
		return acptStatusMap.containsKey(dCode)?(String)acptStatusMap.get(dCode):"";
	}
	/**    20210619 审批系统添加 start    */
}
