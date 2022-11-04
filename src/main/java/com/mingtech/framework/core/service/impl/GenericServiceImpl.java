package com.mingtech.framework.core.service.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.action.BaseAction;
import com.mingtech.framework.core.dao.GenericDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * <p>
 * 说明:通用服务实现类
 * </p>
 *
 * @author E-mail:hexin@
 * @version
 * @since 2008-6-30 下午09:45:10
 *
 */
@Service
public abstract class GenericServiceImpl implements GenericService {
	private static final Logger logger = Logger.getLogger(GenericServiceImpl.class);

	@Autowired
	protected GenericDao dao;

	

   /**
   * 保存 对象 
   * @param t 
   * @return
   */
    public String  txSaveEntity(Object t){
	return (String )dao.saveEntity(t);
    }
	
	

	public GenericServiceImpl() {
	}

	public void setDao(GenericDao dao) {
		this.dao = dao;
	}

	public GenericDao getDao() {
		return dao;
	}
	public Object txSaveStore(Object object) {
		return dao.saveEntity(object);
	}
	
	public void txStore(Object object) {
		dao.store(object);
	}
	
	public void txStoreAll(Collection ts) {
		dao.storeAll(ts);
	}

	public void txDelete(Object object) {
		dao.delete(object);
	}
	public void txDeleteAll(Collection ts) {
		dao.deleteAll(ts);
	}

	public Object load(Serializable id) {
		return dao.load(this.getEntityClass(), id);
	}

	public Object load(String id) {
		return dao.load(this.getEntityClass(), id);
	}
	public Object load(Serializable id,Class cls) {
		return dao.load(cls, id);
	}
	public Object load(String id,Class cls,LockMode lockMode) {
		return dao.load(cls, id, lockMode);
	}

	public DetachedCriteria createDetachedCriteria(Class clazz) {
		return dao.createDetachedCriteria(this.getEntityClass());
	}

	public DetachedCriteria createDetachedCriteria(Class clazz, String pro) {
		return dao.createDetachedCriteria(this.getEntityClass(), pro);
	}

	public List find(String hql) {
		return dao.find(hql);
	}
	/**
	 * 
	 * @param keyColum  分页使用的  关键字段   比如：ID distinct(id) 等等
	 * @param hql sql语句
	 * @param nameForSetVar 条件名称
	 * @param parameters  条件值 
	 * @param pageInfo 分页对象
	 * @return 查询结果
	 */
	public List find(String keyColum,String hql, String [] nameForSetVar,Object[] parameters,Page pageInfo) {
		return dao.find(keyColum,hql, nameForSetVar, parameters, pageInfo);
	}
	public List find(String hql, Object[] parameter) {
		return dao.find(hql, parameter);
	}


	public List find(String hql, String countHql, Page page) {
		return dao.find(hql, countHql, page);
	}


	public List find(String hql, int pageIndex, int pageSize) {
		return dao.find(hql, pageIndex, pageSize);
	}

	public List findByCriteria(DetachedCriteria detachedCriteria) {
		return dao.findByCriteria(detachedCriteria);
	}

	public List findByCriteria(DetachedCriteria detachedCriteria, Page page) {
		return dao.findByCriteria(detachedCriteria, page);
	}

	public List findByCriteria(DetachedCriteria criteria, int pageIndex, int pageSize) {
		return dao.findByCriteria(criteria, pageIndex, pageSize);
	}

	public List findByExample(Object object) {
		return dao.findByExample(object);
	}

	public List findByExample(Object object, Page page) {
		return dao.findByExample(object, page);
	}

	public List findByNamedQuery(String queryNamed) {
		return dao.findByNamedQuery(queryNamed);
	}

	public List findByNamedQuery(String queryNamed, Object[] parameters) {
		return dao.findByNamedQuery(queryNamed, parameters);
	}

	public List getNamedQuery(String queryNamed, Map map) {
		return dao.getNamedQuery(queryNamed, map);
	}

	public Long getResultCount(String countHql) {
		return dao.getResultCount(countHql);
	}

	public Long getRowCount(DetachedCriteria detachedCriteria) {
		return dao.getRowCount(detachedCriteria);
	}

	public List find(String query, List parameters) {
		return dao.find(query, parameters);
	}

	public List find(String query, List parameters, int start, int size) {
		return dao.find(query, parameters, start, size);
	}

	public List find(String hql, List parameters, Page pageInfo) {
		return dao.find(hql, parameters, pageInfo);
	}

	public Long getRowCount(String query, List parameters) {
		return dao.getRowCount(query, parameters);
	}

	public List findAll() {
		return dao.loadAll(getEntityClass());
	}

	public List find(String hql, String [] nameForSetVar,Object[] parameters,Page pageInfo) {
		return dao.find(hql, nameForSetVar, parameters, pageInfo);
	}
	/**
	 * 
	 * @param keyColum  分页使用的  关键字段   比如：ID distinct(id) 等等
	 * @param hql sql语句
	 * @param nameForSetVar 条件名称
	 * @param parameters  条件值 
	 * @return 查询结果
	 */
	public List find(String keyColum,String hql, String [] nameForSetVar,Object[] parameters) {
		return dao.find(keyColum,hql, nameForSetVar, parameters);
	}
	
	public List find(String hql, String [] nameForSetVar,Object[] parameters) {
		return dao.find(hql, nameForSetVar, parameters);
	}
	
	public BigDecimal getTotalMoney(String batchId){
		return new BigDecimal(0);
	}
	public <T> List queryByObj(T t,String name){
		return queryByObj(t,name, new Page());
	}
	public <T> List queryByObj(T t,String name,Page page){
		return queryByObj(t, name, page, null,null);
	}
	public <T> String queryByObjJson(T t,String name){
		return queryByObjJson(t,name,new Page());
	}
	
	public <T> String queryByObjJson(T t,String name,Page p){
		return queryByObjJson(t, name, p, null, null);
	}
	
	public <T> String queryByObjJson(T t,String name,Page p,String keyWord,String order){
		String json = "";
		try {
			List list = queryByObj(t,name, p,keyWord,order);
			Map jsonMap = new HashMap();
			jsonMap.put("totalProperty", "results," + p.getTotalCount());
			jsonMap.put("root", "rows");
			json = JsonUtil.fromCollections(list, jsonMap);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} 
		if (StringUtil.isEmpty(json)) {
			json = "{\"results\":0,\"rows\":[]}";
		}
		return json;
	}
	
	public <T> List queryByObj(T t,String name,Page page,String keyWord,String order){
		StringBuffer sql = new StringBuffer(" select a from "+name+" as a where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		Method[] mList = t.getClass().getMethods();
		Method m = null;
		String para;
		for(int i=0;i<mList.length;i++){
			m = mList[i];
			try{
				if(m.getName().contains("getStart") ){
					para = m.getName().substring(8);
					para = para.substring(0, 1)+para.substring(1);
					if(m.invoke(t)!=null && !m.invoke(t).equals("")){
						paramName.add(para+"Start");
						paramValue.add(m.invoke(t));
						sql.append(" and a."+para+" >=:"+ para+"Start"); 
					}
				}else if(m.getName().contains("getEnd")){
					para = m.getName().substring(6);
					para = para.substring(0, 1).toLowerCase()+para.substring(1);
					if(m.invoke(t)!=null && !m.invoke(t).equals("")){
						paramName.add(para+"End");
						paramValue.add(m.invoke(t));
						sql.append(" and a."+para+" <=:"+ para+"End");
					}	
				}else if(m.getName().contains("getEqual")){
					para = m.getName().substring(8);
					para = para.substring(0, 1).toLowerCase()+para.substring(1);
					if(m.invoke(t)!=null && !m.invoke(t).equals("")){
						paramName.add(para);
						paramValue.add(m.invoke(t));
						sql.append(" and a."+para+" =:"+ para);
					}	
				}else if(m.getName().contains("get") && m.getReturnType().equals(String.class)){
					para = m.getName().substring(3);
					para = para.substring(0, 1).toLowerCase()+para.substring(1);
					if(m.invoke(t)!=null && !m.invoke(t).equals("")){
						paramName.add(para);
						paramValue.add( "%" + m.invoke(t) + "%"  );
						sql.append(" and a."+para+" like:"+ para);
					}	
				}else if(m.getName().contains("get") && !m.getReturnType().equals(String.class) && !m.getReturnType().equals(List.class) && !m.getName().contains("getClass")){
					para = m.getName().substring(3);
					para = para.substring(0, 1).toLowerCase()+para.substring(1);
					if(m.invoke(t)!=null && !m.invoke(t).equals("")){
						paramName.add(para);
						paramValue.add(m.invoke(t));
						sql.append(" and a."+para+" =:"+ para);
					}	
				}else if(m.getName().contains("get") && m.getReturnType().equals(List.class)){
					para = m.getName().substring(3);
					para = para.substring(0, 1).toLowerCase()+para.substring(1);
					List l = (List) m.invoke(t);
					if(l!=null && l.size()>0){
						paramName.add(para);
						paramValue.add( m.invoke(t) );
						sql.append(" and a."+para+" in(:"+ para+") ");
					}	
				}else{
					
				}
				
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		
		//排序，默认倒序
		if(keyWord!=null && !keyWord.equals("")){
			if(order != null && !order.equals("")){
				sql.append(" order by a."+keyWord+" "+order+" ");
			}else{
				sql.append(" order by a."+keyWord+" desc ");
			}
		}
		
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List list =  this.find(sql.toString(), paramNames, paramValues,page);
		return list;
	}
	/**
	 * 强制同步obj的缓存
	 * @param obj
	 */
	public void evictObjCache(Object obj){
		this.dao.evict(obj);
	}
	/**
	 * 清理所有缓存
	 */
	public void clearCache(){
		dao.clear();
	}
	/**
	 * 强制同步所以类的缓存
	 */
	public void flushCache(){
		dao.flush();
	}
}
