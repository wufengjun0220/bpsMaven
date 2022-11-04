package com.mingtech.application.pool.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.framework.common.util.StringUtil;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: 张永超
 * @日期: Aug 24, 2010 2:00:15 PM
 * @描述: [PoolDictionaryCache]票据池 -字典缓存Cache
 */
public class PoolDictionaryCache extends DictionaryCache {
	/**
	 * 资产类型
	 */
	private static final Map<String, String> zcTypeMap = Collections.synchronizedMap(new HashMap<String, String>());
	/**
	 * 资产-规则类型
	 */
	private static final Map<String, String> zcRuleMap = Collections.synchronizedMap(new HashMap<String, String>());
	/**
	 * 票据池省份，用于黑灰名单-承兑人所在地区
	 */
	private static final Map<String, String> pjcAreaMap = Collections.synchronizedMap(new HashMap<String, String>());
	/**
	 * 信用证类型
	 */
	private static final Map<String, String> xyzTypeMap = Collections.synchronizedMap(new HashMap<String, String>());
	/**
	 * 信用证介质
	 */
	private static final Map<String, String> xyzMediaMap = Collections.synchronizedMap(new HashMap<String, String>());

	public void initDictionaryCache() {
		// 加载票据池所有区域
		List areaList = dictionaryService.getAllDictionaryByParentCode("PJC_01", 0);
		if (areaList != null && areaList.size() > 0) {
			for (int i = 0; i < areaList.size(); i++) {
				Object[] dic = (Object[]) areaList.get(i);
				if (dic != null) {
					if (StringUtil.isNotBlank(dic[0].toString()) && StringUtil.isNotBlank(dic[1].toString())) {
						pjcAreaMap.put(dic[0].toString(), dic[1].toString());
					}
				}
			}
		}

		// 加载资产项下字典项
		List zcList = dictionaryService.getAllDictionaryByParentCode("ZCC", 0);
		if (zcList != null && zcList.size() > 0) {
			String code = "";
			String name = "";
			String parentCode = "";
			for (int i = 0; i < zcList.size(); i++) {
				Object[] dic = (Object[]) zcList.get(i);
				if (dic != null) {
					code = dic[0].toString();
					name = dic[1].toString();
					parentCode = dic[2].toString();
					if ("ZCC_01".equalsIgnoreCase(parentCode)) {
						zcTypeMap.put(code, name);// 资产类型
						continue;
					} else if ("ZCC_02".equalsIgnoreCase(parentCode)) {
						zcRuleMap.put(code, name);// 资产-规则类型
						continue;
					}
				}
			}
		}

		// 加载信用证池项下字典项
		List dicList = dictionaryService.getAllDictionaryByParentCode("XYZ", 0);
		if (dicList != null && dicList.size() > 0) {
			String code = "";
			String name = "";
			String parentCode = "";
			for (int i = 0; i < dicList.size(); i++) {
				Object[] dic = (Object[]) dicList.get(i);
				if (dic != null) {
					code = dic[0].toString();
					name = dic[1].toString();
					parentCode = dic[2].toString();
					if ("XYZ_01".equalsIgnoreCase(parentCode)) {
						xyzTypeMap.put(code, name);// 信用证类型
						continue;
					} else if ("XYZ_02".equalsIgnoreCase(parentCode)) {
						xyzMediaMap.put(code, name);// 信用证介质
						continue;
					}
				}
			}
		}
	}

	/**
	 * 方法名称: getPjcAreaName|描述: 票据池省份
	 */
	public static String getPjcAreaName(String province) {
		return pjcAreaMap.containsKey(province) ? (String) pjcAreaMap.get(province) : "";
	}

	/**
	 * 方法名称: getPlDraftTypeName|描述: 获取信用证类型对应的中文
	 */
	public static String getPlDraftTypeName(String code) {
		return xyzTypeMap.containsKey(code) ? (String) xyzTypeMap.get(code) : "";
	}

	/**
	 * 方法名称: getPlDraftMediaName|描述: 获取信用证介质对应的中文
	 */
	public static String getPlDraftMediaName(String code) {
		return xyzMediaMap.containsKey(code) ? (String) xyzMediaMap.get(code) : "";
	}

	/**
	 * 方法名称: getZCTypeName|描述: 获取资产类型对应的中文
	 */
	public static String getZCTypeName(String code) {
		return zcTypeMap.containsKey(code) ? (String) zcTypeMap.get(code) : "";
	}

	/**
	 * 方法名称: getZCRuleName|描述: 获取资产-规则类型对应的中文
	 */
	public static String getZCRuleName(String code) {
		return zcRuleMap.containsKey(code) ? (String) zcRuleMap.get(code) : "";
	}

	/**
	 * 清空交易代码集合
	 */
	public static void clear() {
		pjcAreaMap.clear();
	}

	public void reloadDictionary() {
		initDictionaryCache();
	}
}
