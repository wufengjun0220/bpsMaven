package com.mingtech.framework.core.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.stat.Statistics;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.DAOException;
import com.mingtech.framework.core.dao.GenericDao;
import com.mingtech.framework.core.page.Page;

/**
 * 说明:通用DAO实现类（针对Hibernate）
 * @author E-mail:hexin@
 * @version
 * @since 2008-6-30 下午11:25:45
 */
public class GenericHibernateDao extends HibernateDaoSupport implements
		GenericDao{

	public GenericHibernateDao(){
	}
	/**
	 * 保存 对象 
	 * @param t 
	 * @return
	 */
	public Serializable  saveEntity(Object t){
		return getHibernateTemplate().save(t);
	}
	
	public void store(Object t){
		getHibernateTemplate().saveOrUpdate(t);
	}

	public DetachedCriteria createDetachedCriteria(Class clazz){
		return DetachedCriteria.forClass(clazz);
	}

	public DetachedCriteria createDetachedCriteria(Class clazz, String pro){
		return DetachedCriteria.forClass(clazz, pro);
	}

	public void delete(Object t){
		getHibernateTemplate().delete(t);
	}

	public void deleteAll(Collection ts){
		getHibernateTemplate().deleteAll(ts);
	}

	public void storeAll(Collection ts){
		getHibernateTemplate().saveOrUpdateAll(ts);
	}

	public List find(String hql){
		return getHibernateTemplate().find(hql);
	}

	public List find(final String hql, final int pageIndex, final int pageSize){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Query query = session.createQuery(hql);
				int startIndex = (pageIndex - 1) * pageSize;
				query.setFirstResult(startIndex);
				query.setMaxResults(pageSize);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}

	public List find(String hql, Object[] parameter){
		return getHibernateTemplate().find(hql, parameter);
	}

	public List find(String hql, String countHql, Object[] parameters, Page page){
		page.setTotalCount(getResultCount(countHql).intValue());
		return find(hql, parameters, page.getPageIndex(), page.getPageSize());
	}

	public List find(final String hql, final Object[] parameters,
			final int pageIndex, final int pageSize){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Query query = session.createQuery(hql);
				if(parameters != null && parameters.length > 0){
					for(int i = 0; i < parameters.length; i++){
						query.setParameter(i, parameters[i]);
					}
				}
				int startIndex = (pageIndex - 1) * pageSize;
				query.setFirstResult(startIndex);
				query.setMaxResults(pageSize);
				// query.setCacheable(true);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}

	public List loadAll(Class clazz){
		return getHibernateTemplate().loadAll(clazz);
	}

	public List findByExample(final Object entity){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Criteria criteria = session.createCriteria(entity.getClass());
				Example example = Example.create(entity);
				example.ignoreCase().enableLike(MatchMode.ANYWHERE);//
				example.excludeZeroes();
				criteria.add(example);
				return criteria.list();
			}
		});
	}

	public List findByExample(final Object entity, final Page page){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Criteria criteria = session.createCriteria(entity.getClass());
				Example example = Example.create(entity);
				example.ignoreCase().enableLike(MatchMode.ANYWHERE);//
				example.excludeZeroes();
				int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
				criteria.add(example);
				page.setTotalCount(getChargeProcessCount(criteria));
				return criteria.setFirstResult(startIndex).setMaxResults(
						page.getPageSize()).list();
			}
		});
	}

	public List findByNamedQuery(String queryNamed){
		return getHibernateTemplate().findByNamedQuery(queryNamed);
	}

	public List findByNamedQuery(String queryNamed, Object[] parameters){
		return getHibernateTemplate().findByNamedQuery(queryNamed, parameters);
	}

	public List findByNamedQuery(final String queryNamed,
			final Object[] parameters, final int pageIndex, final int pageSize){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Query query = session.getNamedQuery(queryNamed);
				if(parameters != null && parameters.length > 0){
					for(int i = 0; i < parameters.length; i++){
						query.setParameter(i, parameters[i]);
					}
				}
				int startIndex = (pageIndex - 1) * pageSize;
				query.setFirstResult(startIndex);
				query.setMaxResults(pageSize);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}

	public List getNamedQuery(final String queryNamed, final Map map){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Query query = session.getNamedQuery(queryNamed);
				Set entiys = map.entrySet();
				Iterator it = entiys.iterator();
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next();
					String objKey = (String) entry.getKey();
					Object objValue = (Object) entry.getValue();
					query.setParameter(objKey, objValue);
				}
				return query.list();
			}
		});
	}

	public Connection getConnection(){
		return (Connection) getHibernateTemplate().execute(
				new HibernateCallback(){

					public Object doInHibernate(Session session){
						Connection connection = session.connection();
						return connection;
					}
				});
	}

	public List findByCriteria(final DetachedCriteria detachedCriteria){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				return detachedCriteria.getExecutableCriteria(session).list();
			}
		});
	}

	public List findByCriteria(DetachedCriteria criteria, int pageIndex,
			int pageSize){
		int startIndex = (pageIndex - 1) * pageSize;
		return criteria.getExecutableCriteria(this.getSession())
				.setFirstResult(startIndex).setMaxResults(pageSize).list();
	}

	public List findByCriteria(DetachedCriteria detachedCriteria, Page page){
		page.setTotalCount(getChargeProcessCount(detachedCriteria));
		int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
		return detachedCriteria.getExecutableCriteria(this.getSession())
				.setFirstResult(startIndex).setMaxResults(page.getPageSize())
				.list();
	}

	public List find(final String hql, final String countHql,
			final Page pageInfo){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				pageInfo.setTotalCount(getResultCount(countHql).intValue());
				Query query = session.createQuery(hql);
				int startIndex = (pageInfo.getPageIndex() - 1)
						* pageInfo.getPageSize();
				query.setFirstResult(startIndex);
				query.setMaxResults(pageInfo.getPageSize());
				// query.setCacheable(true);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}

	public Long getResultCount(final String countHql){
		List list = this.getHibernateTemplate().find(countHql);
		return (Long) list.get(0);
	}

	public Long getRowCount(DetachedCriteria detachedCriteria){
		detachedCriteria.setProjection(Projections.rowCount());
		List list = this.findByCriteria(detachedCriteria);
		if(null!=list&&list.size()>0){
			return (Long) list.get(0);	
		}
		return new Long("0");
	}

	public int getChargeProcessCount(final DetachedCriteria detachedCriteria){
		Integer count = new Integer(0);
		if(detachedCriteria != null){
			count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback(){

						public Object doInHibernate(Session session)
								throws HibernateException{
							Criteria criteria = detachedCriteria
									.getExecutableCriteria(session);
							CriteriaImpl impl = (CriteriaImpl) criteria;
							Projection projection = impl.getProjection();
							List orderEntries = new ArrayList();
							Field field = null;
							Integer count = new Integer(0);
							try{
								field = CriteriaImpl.class
										.getDeclaredField("orderEntries");
								field.setAccessible(true);
								orderEntries = (List) field.get(impl);
								field.set(criteria, new ArrayList());
								count = (Integer) criteria.setProjection(
										Projections.rowCount()).uniqueResult();
								criteria.setProjection(projection);
								if(projection == null){
									criteria
											.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
								}
								field.set(criteria, orderEntries);
							}catch (Exception e){
							}
							return count;
						}
					}, true);
		}
		return count.intValue();
	}

	public int getChargeProcessCount(final Criteria criteria){
		Integer count = new Integer(0);
		if(criteria != null){
			count = (Integer) getHibernateTemplate().execute(
					new HibernateCallback(){

						public Object doInHibernate(Session session)
								throws HibernateException{
							CriteriaImpl impl = (CriteriaImpl) criteria;
							Projection projection = impl.getProjection();
							List orderEntries = new ArrayList();
							Field field = null;
							Integer count = new Integer(0);
							try{
								field = CriteriaImpl.class
										.getDeclaredField("orderEntries");
								field.setAccessible(true);
								orderEntries = (List) field.get(impl);
								field.set(criteria, new ArrayList());
								count = (Integer) criteria.setProjection(
										Projections.rowCount()).uniqueResult();
								criteria.setProjection(projection);
								if(projection == null){
									criteria
											.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
								}
								field.set(criteria, orderEntries);
							}catch (Exception e){
							}
							return count;
						}
					}, true);
		}
		return count.intValue();
	}

	public List find(final String query, final List parameters){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						Query qu = session.createQuery(query);
						setParameters(qu, parameters);
						List l = qu.list();
						return l;
					}
				});
		return result;
	}

	public List SQLQuery(final String sql){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						Query qu = session.createSQLQuery(sql);
						List l = qu.list();
						return l;
					}
				});
		return result;
	}
	
	public List SQLQuery(final String sql,final List parameters){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						Query qu = session.createSQLQuery(sql);
						setParameters(qu, parameters);
						List l = qu.list();
						return l;
					}
				});
		return result;
	}
	
	public List SQLQuery(final String sql,final Page pageInfo){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						if(null != pageInfo){
							pageInfo.setTotalCount(getRowCount(sql).longValue());
						}
						Query qu = session.createSQLQuery(sql);
						if(null != pageInfo){
							int startIndex = (pageInfo.getPageIndex() - 1)
									* pageInfo.getPageSize();
							qu.setFirstResult(startIndex);
							qu.setMaxResults(pageInfo.getPageSize());
						}
						List l = qu.list();
						return l;
					}
				});
		return result;

	}



	public List find(final String query, final List parameters,
			final int start, final int size){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						Query qu = session.createQuery(query);
						qu.setMaxResults(size);
						qu.setFirstResult(start - 1);
						setParameters(qu, parameters);
						List l = qu.list();
						return l;
					}
				});
		return result;
	}

	public Long getRowCount(final String query, final List parameters){
		Long i = (Long) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException{
				StringBuffer coutSql = new StringBuffer("select count(*) ");
				String trimSQL = query;
				String tempSQL = query.toLowerCase();
				if(tempSQL.trim().startsWith("select ", 0)){
					if(query.indexOf("distinct")>0){//包含distinct语句
						coutSql = new StringBuffer("select count("+tempSQL.substring(tempSQL.indexOf("distinct"),tempSQL.indexOf("from"))+") ");
					}
					trimSQL = query.substring(tempSQL.indexOf(" from "), query
							.length());
					tempSQL = tempSQL.substring(tempSQL.indexOf(" from "),
							tempSQL.length());
					
				}
				if(StringUtil.contains(tempSQL, " order by ")){
					trimSQL = trimSQL.substring(0, tempSQL
							.indexOf(" order by "));
				}
				coutSql.append(trimSQL);
				Query qu = session.createQuery(coutSql.toString());
				setParameters(qu, parameters);
				Iterator irr = qu.iterate();
				return (Long) irr.next();
			}
		});
		return i;
	}

	public Long getRowCount( final String query){

		StringBuffer coutSql = new StringBuffer("select count(*) from ( ");
//		String trimSQL = query;
//		String tempSQL = query.toLowerCase();
//		if(tempSQL.trim().startsWith("select ", 0)){
//			trimSQL = query.substring(tempSQL.indexOf(" from "), query
//					.length());
//			tempSQL = tempSQL.substring(tempSQL.indexOf(" from "),
//					tempSQL.length());
//		}
//		if(StringUtil.contains(tempSQL, " order by ")){
//			trimSQL = trimSQL.substring(0, tempSQL
//					.indexOf(" order by "));
//		}
		coutSql.append(query);
		coutSql.append(" )");
		List list = SQLQuery(coutSql.toString());
		Long l = new Long("0");
		if(list != null && list.size() != 0){
			BigDecimal b = (BigDecimal) list.get(0);// BigDecimal[] d =
			l = new Long(b.longValue());
		}

		return l;
	}

	// public Long getRowCount(final String query, final Object[] parameters) {
	// Long i = (Long) getHibernateTemplate().execute(new HibernateCallback() {
	// public Object doInHibernate(Session session) throws HibernateException {
	// StringBuffer coutSql = new StringBuffer("select count(*) ");
	// String trimSQL = query;
	// String tempSQL = query.toLowerCase();
	// if (tempSQL.trim().startsWith("select ",0)) {
	// trimSQL = query.substring(tempSQL.indexOf(" from "), query.length());
	// tempSQL = tempSQL.substring(tempSQL.indexOf(" from "), tempSQL.length());
	// }
	//
	// if (StringUtil.contains(tempSQL, " order by ")) {
	// trimSQL = trimSQL.substring(0, tempSQL.indexOf(" order by "));
	// }
	// coutSql.append(trimSQL);
	// Query qu = session.createQuery(coutSql.toString());
	// setParameters(qu, parameters);
	// Iterator irr = qu.iterate();
	// return (Long) irr.next();
	// }
	// });
	// return i;
	// }
	public List find(final String hql, final List parameters,
			final Page pageInfo){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				if(null != pageInfo){
					Long countL = getRowCount(hql, parameters);
					if(null!=countL){
						pageInfo.setTotalCount(countL.longValue());
					}
				}
				Query query = session.createQuery(hql);
				setParameters(query, parameters);
				if(null != pageInfo){
					int startIndex = (pageInfo.getPageIndex() - 1)
							* pageInfo.getPageSize();
					query.setFirstResult(startIndex);
					query.setMaxResults(pageInfo.getPageSize());
				}
				// query.setCacheable(true);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}

	// public List find(final String hql, final Object[] parameters, final Page
	// pageInfo) {
	// return (List) getHibernateTemplate().execute(new HibernateCallback() {
	// public Object doInHibernate(Session session) {
	// pageInfo.setTotalCount(getRowCount(hql, parameters).longValue());
	// Query query = session.createQuery(hql);
	// setParameters(query, parameters);
	// int startIndex = (pageInfo.getPageIndex() - 1) * pageInfo.getPageSize();
	// query.setFirstResult(startIndex);
	// query.setMaxResults(pageInfo.getPageSize());
	// query.setCacheable(true);
	// Object obj = query.list();
	// if (obj == null) {
	// obj = new ArrayList();
	// }
	// return (List) obj;
	// }
	// });
	// }
	/**
	 * 设置查询的属性
	 * @param query
	 * @param parameters
	 */
	private void setParameters(Query query, List parameters){
		for(int i = 0; i < parameters.size(); i++){
			if(parameters.get(i).getClass().getName().equals(
					java.sql.Date.class.getName())){
				query.setDate(i, (java.sql.Date) parameters.get(i));
			}else if(parameters.get(i).getClass().getName().equals(
					java.util.Date.class.getName())){
				query.setTimestamp(i, (java.util.Date) parameters.get(i));
			}else if(parameters.get(i).getClass().getName().equals(
					java.lang.Integer.class.getName())){
				query.setInteger(i, ((Integer)parameters.get(i)).intValue());
			}else{
				query.setString(i, parameters.get(i).toString());
			}
		}
	}

	public Long getRowCount(final String query, final String[] nameForSetVar,
			final Object[] parameters){
		Long i = (Long) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException{
				StringBuffer coutSql = new StringBuffer("select count(*) ");
				String trimSQL = query;
				String tempSQL = query.toLowerCase();
				if(tempSQL.trim().startsWith("select ", 0)){
					trimSQL = query.substring(tempSQL.indexOf(" from "), query
							.length());
					tempSQL = tempSQL.substring(tempSQL.indexOf(" from "),
							tempSQL.length());
				}
				if(StringUtil.contains(tempSQL, " order by ")){
					trimSQL = trimSQL.substring(0, tempSQL
							.indexOf(" order by "));
				}
				coutSql.append(trimSQL);
				Query qu = session.createQuery(coutSql.toString());
				setParameters(qu, nameForSetVar, parameters);
				Iterator irr = qu.iterate();
				return (Long) irr.next();
			}
		});
		return i;
	}
	/**
	 * 
	 * @param keyColum  指定的  统计字段 
	 * @param query
	 * @param nameForSetVar
	 * @param parameters
	 * @return
	 */
	public Long getRowCount(final String keyColum,final String query, final String[] nameForSetVar,
			final Object[] parameters){
		Long i = (Long) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException{
				StringBuffer coutSql = new StringBuffer("");
				if(keyColum!=null && keyColum.length()>0){
					coutSql.append(" select  count("+keyColum+") ");
				}else{
					coutSql.append(" select count(*) ");
				}
				String trimSQL = query;
				String tempSQL = query.toLowerCase();
				if(tempSQL.trim().startsWith("select ", 0)){
					trimSQL = query.substring(tempSQL.indexOf(" from "), query
							.length());
					tempSQL = tempSQL.substring(tempSQL.indexOf(" from "),
							tempSQL.length());
				}
				if(StringUtil.contains(tempSQL, " order by ")){
					trimSQL = trimSQL.substring(0, tempSQL
							.indexOf(" order by "));
				}
				coutSql.append(trimSQL);
				Query qu = session.createQuery(coutSql.toString());
				setParameters(qu, nameForSetVar, parameters);
				List retlist = qu.list();
				return (Long) retlist.get(0);
			}
		});
		return i;
	}

	public List find(final String hql, final String[] nameForSetVar,
			final Object[] parameters, final Page pageInfo){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				if(null != pageInfo){
					Long countL = getRowCount(hql, nameForSetVar,
							parameters);
					if(null!=countL){
						pageInfo.setTotalCount(countL.longValue());
					}
				}
				Query query = session.createQuery(hql);
				setParameters(query, nameForSetVar, parameters);
				if(null != pageInfo){
					int startIndex = (pageInfo.getPageIndex() - 1)
							* pageInfo.getPageSize();
					query.setFirstResult(startIndex);
					query.setMaxResults(pageInfo.getPageSize());
				}
				// query.setCacheable(true);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}
	/**
	 * 查询 数据库
	 * @param keyColum 分页使用的关键字段  比如 ID  distinct(id)
	 * @param hql 
	 * @param nameForSetVar
	 * @param parameters
	 * @param pageInfo
	 * @return
	 */
	public List find(final String keyColum,final String hql, final String[] nameForSetVar,
			final Object[] parameters, final Page pageInfo){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				if(null != pageInfo){
					Long countL = getRowCount(keyColum,hql, nameForSetVar,
							parameters);
					if(null!=countL){
						pageInfo.setTotalCount(countL.longValue());
					}
				}
				Query query = session.createQuery(hql);
				setParameters(query, nameForSetVar, parameters);
				if(null != pageInfo){
					int startIndex = (pageInfo.getPageIndex() - 1)
							* pageInfo.getPageSize();
					query.setFirstResult(startIndex);
					query.setMaxResults(pageInfo.getPageSize());
				}
				// query.setCacheable(true);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}
	/**
	 * 查询 数据库
	 * @param keyColum 分页使用的关键字段  比如 ID  distinct(id)
	 * @param hql 
	 * @param nameForSetVar
	 * @param parameters
	 * @return
	 */
	public List find(final String keyColum,final String hql, final String[] nameForSetVar,
			final Object[] parameters){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Query query = session.createQuery(hql);
				setParameters(query, nameForSetVar, parameters);
				// query.setCacheable(true);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}

	public List find(final String hql, final String[] nameForSetVar,
			final Object[] parameters){
		return (List) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session){
				Query query = session.createQuery(hql);
				setParameters(query, nameForSetVar, parameters);
				Object obj = query.list();
				if(obj == null){
					obj = new ArrayList();
				}
				return (List) obj;
			}
		});
	}

	private void setParameters(Query query, String[] nameForSetVar,
			Object[] parameters){
		for(int i = 0; i < parameters.length; i++){
			if(parameters[i] instanceof List){
				if(null!=nameForSetVar && StringUtils.isNotEmpty(nameForSetVar[i])){
					query.setParameterList(nameForSetVar[i], (List) parameters[i]);
				}
			}else{
				if(nameForSetVar == null){
					query.setParameter(i, parameters[i]);
				}else{
					query.setParameter(nameForSetVar[i], parameters[i]);
				}
			}
		}
	}

	// *******************//
	// 获得数据元数据
	// *******************//
	public String[] getPropertyName(ClassMetadata md) throws Exception{
		try{
			return md.getPropertyNames();
		}catch (Exception e){
			throw new Exception(e);
		}
	}

	public Type[] getPropertyType(ClassMetadata md) throws Exception{
		try{
			return md.getPropertyTypes();
		}catch (Exception e){
			throw new Exception(e);
		}
	}

	public ClassMetadata getClassMetadata(Class claz) throws Exception{
		try{
			return this.getHibernateTemplate().getSessionFactory()
					.getClassMetadata(claz);
		}catch (Exception e){
			throw new Exception(e);
		}
	}

	public String getTableName(ClassMetadata md) throws Exception{
		try{
			return md.getEntityName();
		}catch (Exception e){
			throw new Exception(e);
		}
	}

	public Object load(Class cls, String id){
		return this.getHibernateTemplate().get(cls, id);
	}

	public Object load(Class cls, Serializable id){
		return this.getHibernateTemplate().get(cls, id);
	}

	public void evict(Object obj){
		this.getHibernateTemplate().evict(obj);
	}

	public void merge(Object obj){
		this.getHibernateTemplate().merge(obj);
	}

	public void clear(){
		this.getHibernateTemplate().clear();
	}

	public void flush(){
		this.getHibernateTemplate().flush();
	}

	public  Statistics  getStatics(){
		return this.getHibernateTemplate().getSessionFactory().getStatistics();
	}

	public Object load(Class cls, String id, LockMode lockMode){
		return this.getHibernateTemplate().load(cls, id, lockMode);
	}

	public int executeUpdateHQL(String hql, Object[] values){
		return this.getHibernateTemplate().bulkUpdate(hql, values);
	}
	public void txBatchUpdate(final String hql , final String[] nameForSetVar,final Object[] parameters) throws Exception{
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query query =  session.createQuery(hql);
				setParameters(query, nameForSetVar, parameters);
				query.executeUpdate();
				return null;
			}
		});
	}
	public Long getRowCountSQL( final String distinctColumn,final String query, final String[] nameForSetVar,
			final Object[] parameters){
		Long i = (Long) getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException{
				StringBuffer coutSql = new StringBuffer("select count(");
				if(StringUtils.isNotBlank(distinctColumn)) {
					coutSql.append(distinctColumn).append(") ");
				}else {
					coutSql.append("1) ");
				}
				String trimSQL = query;
				String tempSQL = query.toLowerCase();
				if(tempSQL.trim().startsWith("select ", 0)){
					trimSQL = query.substring(tempSQL.indexOf(" from "), query
							.length());
					tempSQL = tempSQL.substring(tempSQL.indexOf(" from "),
							tempSQL.length());
				}
				if(StringUtil.contains(tempSQL, " order by ")){
					trimSQL = trimSQL.substring(0, tempSQL
							.indexOf(" order by "));
				}
				coutSql.append(trimSQL);
				Query qu = session.createSQLQuery(coutSql.toString());
				setParameters(qu, nameForSetVar, parameters);
				List list = qu.list();
				Long l = new Long("0");
				String dbType = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DATABASE_TYPE);		
				if(list != null && list.size() != 0){
					BigDecimal b = null;
					if(StringUtils.isNotBlank(dbType) && "mysql".equalsIgnoreCase(dbType)){	
						b = new BigDecimal((BigInteger) list.get(0));
					}else {
					    b = (BigDecimal) list.get(0);// BigDecimal[] d =
					}
					l = new Long(b.longValue());
				}

				return l;
			}
		});
		return i;
	}
	public List SQLQuery(final String sql, final String[] nameForSetVar,
			final Object[] parameters,final Page pageInfo){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						if(null != pageInfo){
							Long countL = getRowCountSQL(null,sql,nameForSetVar,parameters);
							if(null!=countL){
								pageInfo.setTotalCount(countL.longValue());
							}
						}
						/**此处有BUG,如传Page为null则set值会报空指针异常*/
						//pageInfo.setTotalCount(getRowCountSQL(sql,nameForSetVar,parameters).longValue());
						Query qu = session.createSQLQuery(sql);
						setParameters(qu, nameForSetVar, parameters);
						if(null != pageInfo){
							int startIndex = (pageInfo.getPageIndex() - 1)
									* pageInfo.getPageSize();
							qu.setFirstResult(startIndex);
							qu.setMaxResults(pageInfo.getPageSize());
						}
						List l = qu.list();
						return l;
					}
				});
		return result;
	}
	
	public List SQLQuery(final String distinctColumn,final String sql, final String[] nameForSetVar,
			final Object[] parameters,final Page pageInfo){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						if(null != pageInfo){
							Long countL = getRowCountSQL(distinctColumn,sql,nameForSetVar,parameters);
							if(null!=countL){
								pageInfo.setTotalCount(countL.longValue());
							}
						}
						/**此处有BUG,如传Page为null则set值会报空指针异常*/
						//pageInfo.setTotalCount(getRowCountSQL(sql,nameForSetVar,parameters).longValue());
						Query qu = session.createSQLQuery(sql);
						setParameters(qu, nameForSetVar, parameters);
						if(null != pageInfo){
							int startIndex = (pageInfo.getPageIndex() - 1)
									* pageInfo.getPageSize();
							qu.setFirstResult(startIndex);
							qu.setMaxResults(pageInfo.getPageSize());
						}
						List l = qu.list();
						return l;
					}
				});
		return result;
	}
	
	
	
	public List SQLQuery4Map(final String sql, final String[] nameForSetVar,
			final Object[] parameters,final Page pageInfo){
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						Query qu = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						setParameters(qu, nameForSetVar, parameters);
						if(null != pageInfo){
							Long countL = getRowCountSQL(null,sql,nameForSetVar,parameters);
							if(null!=countL){
								pageInfo.setTotalCount(countL.longValue());
							}
							int startIndex = (pageInfo.getPageIndex() - 1)
									* pageInfo.getPageSize();
							qu.setFirstResult(startIndex);
							qu.setMaxResults(pageInfo.getPageSize());
						}
						List l = qu.list();
						return l;
					}
				});
		return result;
	}
	
	
	
	/**
	 * <p> 方法名称: updateSQL|描述:通过JDBC API来执行该SQL语句，主要用批量的更新、删除 </p>
	 * @param sql
	 * @throws DAOException
	 */
	public void updateSQL(final String sql){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException{
				Connection con = session.connection();
				PreparedStatement stmt = con.prepareStatement(sql);
				int updateEntities = stmt.executeUpdate();
				stmt.close();
				con.close();
				return updateEntities;
			}
		});
	}
	
	
	/**
	 * <p> 方法名称: updateSQL|描述:通过JDBC API来执行该SQL语句，主要用批量的更新、删除 </p>
	 * @param sql
	 * @return int 更新行数
	 * @throws DAOException
	 */
	public int updateSQLReturnRows(final String sql){
		Integer rows = (Integer)getHibernateTemplate().execute(new HibernateCallback(){

			   public Integer doInHibernate(Session session)
			     throws HibernateException, SQLException{
				   Connection con = null;
				   PreparedStatement stmt = null;
				   try {
					    con = session.connection();
					     stmt = con.prepareStatement(sql);
					    int updateEntities = stmt.executeUpdate();
					    return new Integer(updateEntities);
				   }catch(HibernateException e) {
					   throw e;
				   }catch(SQLException e) {
					   throw e;
				   }finally {
					   if(stmt  != null) {
						   stmt.close();
					   }
					   if(con  != null) {
						   con.close(); 
					   }
					   
				   }
			    
			   }
			  });
		if(rows != null) {
			return rows.intValue();
		}else {
			return 0;
		}
	}
	@Override
	public List queryList(final String sql,final String alias,final Class className) {
		List result = getHibernateTemplate().executeFind(
				new HibernateCallback(){

					public Object doInHibernate(Session session)
							throws HibernateException{
						List list = session.createSQLQuery(sql).addEntity(alias, className)
						 .list();
						return list;
					}
				});
		return result;
	}
}
