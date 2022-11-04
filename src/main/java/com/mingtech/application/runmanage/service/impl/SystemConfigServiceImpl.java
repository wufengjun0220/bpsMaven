package com.mingtech.application.runmanage.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.runmanage.service.SystemConfigService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: ice
* @日期: Aug 1, 2019 5:19:56 PM
* @描述: [SystemConfigServiceImpl]系统配置service
*/
@Service("systemConfigService")
public class SystemConfigServiceImpl extends GenericServiceImpl implements
		SystemConfigService{

	public Class getEntityClass(){
		return SystemConfig.class;
	}

	public String getEntityName(){
		return StringUtil.getClass(SystemConfig.class);
	}
	
	public List query(String type,Page page){
		List param = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append(" from SystemConfig config where config.type=?");
		param.add(type);
		if(null != page){
			return this.find(sb.toString(), param,page);
		}else{
			return this.find(sb.toString(), param);
		}
	}
	
	/**
	 * 获取系统配置表 的item值，就是配置的值；
	 * @param type 配置标识
	 * @return
	 */
	public String getConfigItemValue(String type){
		List param = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append(" from SystemConfig config where config.type=?");
		param.add(type);
		List retList = this.find(sb.toString(), param);
		if(retList!=null && retList.size()>0){
			SystemConfig confg = (SystemConfig)retList.get(0);
			return confg.getItem();
		}else{
			return "";
		}
	}
	
	/**
	 *根据分类标识查询对应的系统参数配置
	 * @param type 分类标识
	 * @return
	 */
	public List<SystemConfig> queryConfigsByType(String type){
		List param = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append(" from SystemConfig config where config.type=?");
		param.add(type);
		List retList = this.find(sb.toString(), param);
		return retList;
	}
	
	/**
	 * 获取所有系统参数配置
	 * @return List 系统参数配置集合
	 */
	public List queryAllConfigs(){
		StringBuffer sql = new StringBuffer("select dto.id,dto.c_code,dto.c_item,dto.c_type");
		sql.append(" from t_config dto ");
		List list = dao.SQLQuery(sql.toString());
		List results = new ArrayList();
		int count = list.size();
		for(int i=0; i < count; i++){
			Object[] arrObj = (Object[])list.get(i);
			SystemConfig conf = new SystemConfig();
			conf.setId((String)arrObj[0]);//ID
			conf.setCode((String)arrObj[1]);//编码
			conf.setItem((String)arrObj[2]);//值
			conf.setType((String)arrObj[3]);//分类
			results.add(conf);
		}
		return results;
	}
	
	/**
	 * 根据参数编码获取系统参数配置
	 * @return SystemConfig 系统参数配置
	 */
	public SystemConfig getSystemConfigByCode(String code){
		StringBuffer sql = new StringBuffer("select dto.id,dto.c_code,dto.c_item,dto.c_type");
		sql.append(" from t_config dto ");
		sql.append(" where  dto.c_code='").append(code).append("'");
		List list = dao.SQLQuery(sql.toString());
		List results = new ArrayList();
		if(list.size() > 0){
			Object[] arrObj = (Object[])list.get(0);
			SystemConfig conf = new SystemConfig();
			conf.setId((String)arrObj[0]);//ID
			conf.setCode((String)arrObj[1]);//编码
			conf.setItem((String)arrObj[2]);//值
			conf.setType((String)arrObj[3]);//分类
			return conf;
		}else{
			return null;
		}
	}

	

	/**
	 * 根据配置项码。配置项描述，类型获取系统参数配置
	 * @return List 系统参数配置
	 * @author LT
	 * @2019
	 * 
	 */
	
	public List querySystemConfigInfo(SystemConfig systemConfig,Page page) {
		
        List valueList = new ArrayList(); //查询参数值
		List keyList = new ArrayList(); // 要查询的字段列表
		StringBuffer sb = new StringBuffer();
		sb.append(" from SystemConfig as config where 1=1 ");
		
		if(systemConfig !=null ){
			if(StringUtils.isNotBlank(systemConfig.getCode())){
				sb.append(" and config.code like:code ");
				keyList.add("code");
				valueList.add("%"+systemConfig.getCode()+"%");
			}
			if(StringUtils.isNotBlank(systemConfig.getDescrip())){
				sb.append(" and config.descrip like:descrip");
				keyList.add("descrip");
				valueList.add("%"+systemConfig.getDescrip()+"%");
			}
			if(StringUtils.isNotBlank(systemConfig.getType())){
				sb.append(" and config.type=:type");
				keyList.add("type");
				valueList.add(systemConfig.getType());
			}
		}
		sb.append(" order by config.type");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray(), page);
		return list;
			
		
			
	}

	//查询该code是否已存在
	public SystemConfig chkConfigCodeIsExists(String code,String id) throws Exception {
		
		StringBuffer hql = new StringBuffer(" from SystemConfig as config where config.code=?");
		List paras = new ArrayList(); // 要查询的字段列表
		paras.add(code);
		if(StringUtils.isNotBlank(id)){
			hql.append(" and config.id != ?");
			paras.add(id);
		}
		List resourceList = find(hql.toString(), paras);
		return resourceList.size() > 0 ? (SystemConfig)resourceList.get(0):null;
	}

	//根据ID查询配置详细信息
	public List querySystemConfigById(String ids,Page page) throws Exception {
		List configIds = StringUtil.splitList(ids, ",");
		String hql = "from SystemConfig as config where config.id in (:ids)";
		String[] parasName = new String[] {"ids"};
		Object[] parasValue = new Object[] {configIds};
		List list = null;
		if(null==page) list = this.find(hql, parasName, parasValue);
		else list = this.find(hql, parasName, parasValue, page);
		return list;
		 
	}

	//根据Id删除系统配置信息
	public void txDeleteSystemConfigInfo(String ids) throws Exception {
		List list = this.querySystemConfigById(ids, null);
		for(int i=0 ;i<list.size() ;i++){
			SystemConfig systemConfig = (SystemConfig)list.get(i);
			dao.delete(systemConfig);
			
		}
		
	}

	
}
