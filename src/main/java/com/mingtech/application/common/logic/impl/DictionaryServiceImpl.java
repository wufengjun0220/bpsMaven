package com.mingtech.application.common.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import com.mingtech.application.common.domain.Dictionary;
import com.mingtech.application.common.logic.IDictionaryService;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.application.sysmanage.vo.TreeNode;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.DAOException;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DictionaryServiceImpl extends GenericServiceImpl implements
		IDictionaryService {

	public Class getEntityClass() {
		return Dictionary.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(Dictionary.class);
	}


	public Dictionary getDictionary(String id) throws DAOException {
		Object obj = dao.load(Dictionary.class, id);
		if (null != obj) {
			return (Dictionary) obj;
		} else {
			return null;
		}
	}

	public Dictionary getDictionaryByCode(String code) throws DAOException {
		String sql = "SELECT commonDic from Dictionary commonDic WHERE commonDic.code=?";
		List param = new ArrayList();
		param.add(code.trim());
		List list = dao.find(sql, param);
		if (null != list && list.size() > 0) {
			return (Dictionary) list.get(0);
		} else {
			return null;
		}
	}

	private String buidSQL(String parentCode, int status) {
		StringBuffer sb = new StringBuffer();
		String sql = " from Dictionary as commonDic WHERE 1=1 ";
		if(StringUtil.isNotBlank(parentCode)) {
			sql += " AND commonDic.parentCode= '"+ parentCode +"' ";
		}
		if (status >= 0) {
			//sql += " AND commonDic.status=" + status;
		}
		sb.append(sql);
		return sb.toString();
	}


	public List getDictionaryByParentCode(String parentCode, int status)
			throws DAOException {
		List param = new ArrayList();
		String sql = "SELECT commonDic " + buidSQL(parentCode, status);
		sql += " order by  commonDic.code ASC";
		return dao.find(sql, param);
	}
	
	public List getAllDictionaryByParentCode(String parentCode, int status)
			throws DAOException {
		StringBuffer sb = new StringBuffer();
		sb.append("select td.d_code,td.d_codeName,td.d_parentCode from t_dictionary td");
		sb.append(" start with td.d_code = '"+parentCode+"' ");
		sb.append(" connect by prior td.d_code=td.d_parentCode ");
		sb.append(" order by td.d_code asc ");
		return this.dao.SQLQuery(sb.toString());
	}



	public boolean txDelete(String id) throws DAOException {
		Dictionary dictionary = getDictionary(id);
		List dicList = this.getDictionaryByParentCode(dictionary.getCode(), 0);
		if (dicList != null && dicList.size() > 0) {
			this.dao.deleteAll(dicList);
		}
		this.dao.delete(dictionary);
		return true;
	}

	public List queryAllDictionary(){
		String hql = " from Dictionary";
		return this.find(hql);
	}


	@Override
	public List queryDictionaryList(Dictionary dic, Page page) throws Exception {
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select commonDic from Dictionary as commonDic where 1=1 ");
		if(null != dic){
			if(!StringUtils.isBlank(dic.getParentCode())){
				sb.append(" and commonDic.parentCode=?");
				param.add(dic.getParentCode());
			}
			if(!StringUtils.isEmpty(dic.getName())){
				sb.append(" and commonDic.name like ?");
				param.add(dic.getName().trim()+"%");
			}
		}
		sb.append("order by commonDic.level asc");
		return dao.find(sb.toString(), param,page);
	}
	/**
	 * 方法说明: 查询下级字典的下一个序号
	 * @param  parentCode 父字典编码
	 */
	public int queryNextLevelOfCHildDic(String parentCode)throws Exception{
		String sql = "select max(dto.level) from Dictionary as dto where dto.parentCode='"+parentCode+"'";
		List list = this.find(sql);
		Integer level = (Integer)list.get(0);
		return level != null ? level.intValue() :0 ;
	}
}
