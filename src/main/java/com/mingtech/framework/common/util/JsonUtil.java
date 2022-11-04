package com.mingtech.framework.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mingtech.framework.core.page.Page;

public class JsonUtil {

	/**
	 * 通过对象数组得到Json
	 * 
	 * @param objs
	 * @return
	 */
	public static String fromArray(Object[] objs) {
		JSONArray array = new JSONArray();
		if (!ArrayUtils.isEmpty(objs)) {
			for (int i = 0; i < objs.length; i++) {
				array.put(objs[i]);
			}
		}
		return array.toString();
	}

	/**
	 * 通过对象得到Json
	 * 
	 * @param objs
	 * @return
	 */
	public static String fromObject(Object obj) {
		JSONObject array = new JSONObject(obj);
		return array.toString();
	}

	/**
	 * 通过String得到Json
	 * 
	 * @param objs
	 * @return
	 */
	public static String fromString(String obj) {
		if (StringUtil.isNotBlank(obj))
			return obj;
		return "";
	}

	/**
	 * 通过Map得到Json
	 * 
	 * @param objs
	 * @return
	 */
	public static String fromObject(Map obj) {
		JSONObject array = new JSONObject(obj);
		return array.toString();
	}

	/**
	 * 通过对象List以及要筛选的字段List得到Array Json
	 * 
	 * @return
	 */
	public static String fromArrays(Collection colls, Collection props) throws SecurityException,
			NoSuchFieldException, JSONException {
		JSONObject array = null;
		if (CollectionUtil.isEmpty(colls) || CollectionUtil.isEmpty(props)) {
			return "";
		}
		Iterator objects = colls.iterator();
		StringBuffer fields = new StringBuffer();
		fields.append("[");
		while (objects.hasNext()) {
			array = new JSONObject(objects.next());
			fields.append("[");
			Iterator it2 = props.iterator();
			while (it2.hasNext()) {
				String str = (String) it2.next();
				fields.append("'" + array.getString(str) + "',");
			}
			fields.deleteCharAt(fields.length() - 1);
			fields.append("],");
		}
		fields.deleteCharAt(fields.length() - 1);
		fields.append("]");
		return fields.toString();
	}

	/**
	 * 通过对象List以及要筛选的字段List得到Object Json
	 * 
	 * @param colls
	 * @param props
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws JSONException
	 */
	public static String fromCollections(Collection colls) throws JSONException {
		JSONObject jObject = null;
		if (CollectionUtil.isEmpty(colls)) {
			return "";
		}

		StringBuffer jsons = new StringBuffer();
		jsons.append("["); // json 最外层括号 start
		Iterator objects = colls.iterator();

		while (objects.hasNext()) {
			Object obj = objects.next();
			jObject = new JSONObject(obj);
			jsons.append(jObject.toString() + ",");
		}

		jsons.deleteCharAt(jsons.length() - 1);
		jsons.append("]"); // json 最外层括号 end
		return jsons.toString();
	}

	/**
	 * 通过对象List以及要筛选的字段List得到Object Json
	 * 
	 * @param colls
	 * @param props
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws JSONException
	 */
	public static String fromCollections(Collection colls, Collection props, Map map)
			throws SecurityException, NoSuchFieldException, JSONException {
		JSONObject jObject = null;
		if (CollectionUtil.isEmpty(colls)) {
			return "";
		}

		StringBuffer jsons = new StringBuffer();
		jsons.append("{"); // json 最外层括号 start

		String propertys = (String) map.get("totalProperty");
		String root = (String) map.get("root");
		
		String[] arr = StringUtil.getArrayFromString(propertys, StringUtil.COMMA);
		jsons.append("\"" + arr[0] + "\"" + ":" + arr[1] + ",");
		
		jsons.append("\"" + root + "\"" + ":");
		jsons.append("["); // json 数据外层括号 start

		Iterator objects = colls.iterator();
		String fields = getFields(props);

		while (objects.hasNext()) {
			Object obj = objects.next();
			jObject = new JSONObject(obj);
			jObject = getFinalObject(jObject, fields);
			jsons.append(jObject.toString() + ",");
		}

		jsons.deleteCharAt(jsons.length() - 1);
		jsons.append("]"); // json 数据外层括号 end

		jsons.append("}"); // json 最外层括号 end
		return jsons.toString();
	}

	/**
	 * 通过对象List得到Object Json
	 * 
	 * @param colls
	 * @param map
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws JSONException
	 */
	public static String fromCollections(Collection colls, Map map) throws SecurityException,
			NoSuchFieldException, JSONException {
		if (CollectionUtil.isEmpty(colls)) {
			StringBuffer jsons = new StringBuffer();
			jsons.append("{"); // json 最外层括号 start

			String propertys = (String) map.get("totalProperty");
			String root = (String) map.get("root");
			
			String[] arr = StringUtil.getArrayFromString(propertys, StringUtil.COMMA);
			jsons.append("\"" + arr[0] + "\"" + ":" + arr[1] + ",");
			
			jsons.append("\"" + root + "\"" + ":");
			jsons.append("["); // json 数据外层括号 start
			jsons.append("]");
			jsons.append("}");
			return jsons.toString();
		}
		return fromCollections(colls, null, map);
	}

	/**
	 * 得到要筛选的字段
	 * 
	 * @param props
	 * @return
	 */
	private static String getFields(Collection props) {
		if (CollectionUtil.isNotEmpty(props)) {
			StringBuffer sb = new StringBuffer();
			Iterator it = props.iterator();
			while (it.hasNext()) {
				String str = (String) it.next();
				sb.append(str + ",");
			}
			return sb.toString();
		}
		return "";
	}

	/**
	 * 得到字段筛选过后的对象
	 * 
	 * @param jObject
	 * @param fields
	 * @return
	 */
	private static JSONObject getFinalObject(JSONObject jObject, String fields) {
		if (StringUtil.isNotBlank(fields)) {
			Iterator keys = jObject.keys();
			StringBuffer sb = new StringBuffer();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (!StringUtil.contains(fields, key)) {
					sb.append(key + ",");
				}
			}
			String[] arrs = StringUtil.getArrayFromString(sb.toString(), StringUtil.COMMA);
			for (int i = 0; i < arrs.length; i++) {
				if (StringUtil.isNotBlank(arrs[i])) {
					jObject.remove(arrs[i]);
				}
			}
		}
		return jObject;
	}
	/**
	 * 通过集合对象得到JSON字符
	 * 
	 * @param obj 仅支持Page和List对象
	 * @return String
	 */
	public static String getSonString(Object obj) throws Exception{
		Map map = new HashMap();
		List list=null;
		
		if(obj instanceof Page)
			list=((Page)obj).getResult();
		else if(obj instanceof List)
			list=(List)obj;
		else
			throw new Exception("不支持该对象");
		
		map.put("totalProperty", "results," + list.size());
		map.put("root", "rows");
		String json=fromCollections(list,map);
		return json;
	}
	
	/**
	* <p>方法名称: buildJson|描述: 将数据list生成json串</p>
	* @param dataList 数据
	* @param countSize 总条数
	* @return
	* @throws JSONException
	* @throws NoSuchFieldException
	*/
	public static String buildJson(List dataList,long countSize) throws JSONException,NoSuchFieldException{
		Map map = new HashMap();
		map.put("totalProperty", "results," + countSize);
		map.put("root", "rows");
		String json=fromCollections(dataList,map);
		return json;
	}
}
