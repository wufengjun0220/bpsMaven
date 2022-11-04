package com.mingtech.framework.core.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.stat.Statistics;

import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.core.page.Page;

/**
 * 说明:通用DAO接口
 *
 * @author E-mail:hexin@
 * @version
 * @since 2008-6-30 下午11:25:45
 *
 */
public interface GenericDao {

	/**
	 * 加载一个对象
	 *
	 * @param object
	 *            要保存的对象
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public Object load(Class cls, String id);

	public Object load(Class cls, Serializable id);
	
	public Object load(Class cls, String id, LockMode lockMode);

	/**
	 * 保存（持久化）一个对象
	 *
	 * @param object
	 *            要保存的对象
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public void store(Object t);
	/**
	 * 保存 对象 
	 * @param t 
	 * @return
	 */
	public Serializable  saveEntity(Object t);
	/**
	 * 删除一个对象
	 *
	 * @param object
	 *            要更新的对象
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public void delete(Object t);

	/**
	 * 批量删除
	 *
	 * @param Collection
	 *            需要删除的对象集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public void deleteAll(Collection ts);

	/**
	 * 批量实例化或修改实体
	 *
	 * @param Object
	 *            需要实例化或修改的实体集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public void storeAll(Collection ts);

	/**
	 * 取得该类型下的所有记录
	 *
	 * @param clazz
	 * @return
	 */
	public List loadAll(Class clazz);

	/**
	 * 删除一缓存中的对象
	 *
	 * @param obj
	 */
	public void evict(Object obj);

	/**
	 * merge对象
	 *
	 * @param obj
	 */
	public void merge(Object obj);

	/**
	 * 清空所有一级缓存对象
	 *
	 * @param obj
	 */
	public void clear();

	/**
	 * 立即将修改后的对象写入数据库
	 *
	 * @param obj
	 */
	public void flush();

	/**
	 * 根据预设SQL进行查询
	 *
	 * @param queryNamed
	 *            预设SQL的名称
	 * @return List 查询结果集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List findByNamedQuery(String queryNamed);

	/**
	 * 根据预设SQL进行查询（带参数）
	 *
	 * @param queryNamed
	 *            预设SQL的名称
	 * @param parameters
	 *            参数集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List findByNamedQuery(String queryNamed, final Object[] parameters);

	/**
	 * 根据HQL进行查询
	 *
	 * @param hql
	 *            HQL语句
	 * @return List 取得的查询结果集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List find(String hql);

	/**
	 * 根据HQL进行查询
	 *
	 * @param hql
	 *            HQL语句
	 * @param parameter
	 *            参数集合
	 * @return List 取得的查询结果集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List find(final String hql, final Object[] parameter);


	/**
	 * 根据HQL进行查询
	 *
	 * @param hql
	 *            HQL语句
	 * @param countHql
	 *            查询出集合条目数的HQL语句
	 * @param Page
	 *            Page实例对象
	 * @return List 取得的查询结果集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List find(String hql, String countHql, Page page);



	/**
	 * 根据clazz取得DetachedCriteria对象
	 *
	 * @param clazz
	 *            实体的class属性
	 * @return DetachedCriteria 查询对象
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public DetachedCriteria createDetachedCriteria(Class clazz);

	/**
	 * 根据clazz取得DetachedCriteria对象
	 *
	 * @param clazz
	 *            实体的class属性
	 * @param pro
	 *            实体属性名
	 * @return DetachedCriteria 查询对象
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public DetachedCriteria createDetachedCriteria(Class clazz, String pro);

	/**
	 * 执行QBC查询
	 *
	 * @param DetachedCriteria
	 *            查询条件封装事例
	 * @return List 查询结果集
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List findByCriteria(DetachedCriteria detachedCriteria);

	/**
	 * 执行QBC查询（带翻页）
	 *
	 * @param DetachedCriteria
	 *            查询条件封装事例
	 * @param Page
	 *            翻页实例
	 * @return List 查询结果集
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List findByCriteria(DetachedCriteria detachedCriteria, Page page);

	/**
	 * 根据QBE模式进行查询
	 *
	 * @param DetachedCriteria
	 *            查询条件封装事例
	 * @return List 查询结果集
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List findByExample(Object object);

	/**
	 * 根据QBE模式进行查询
	 *
	 * @param object
	 *            查询简单条件封装实例
	 * @param Page
	 *            翻页实例
	 * @return List 查询结果集
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List findByExample(Object object, Page page);

	/**
	 * 根据HQL语句查询结果集数量
	 *
	 * @param countHql
	 *            负责查询结果集数量的HQL语句
	 * @return Integer 查询结果
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public Long getResultCount(String countHql);

	/**
	 * 根据QBC查询结果集数量
	 *
	 * @param detachedCriteria
	 *            负责查询结果集数量的HQL语句
	 * @return Integer 查询结果
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public Long getRowCount(DetachedCriteria detachedCriteria);
	/**
	 * 
	 * @param keyColum  指定的  统计字段 
	 * @param query
	 * @param nameForSetVar
	 * @param parameters
	 * @return
	 */
	public Long getRowCount(final String keyColum,final String query, final String[] nameForSetVar,
			final Object[] parameters);

	/**
	 * 根据预设SQL查询结果集数量
	 *
	 * @param queryNamed
	 *            预设SQL语句的标记
	 * @param map
	 *            参数值与参数名称影射
	 * @return List 查询结果集合
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public List getNamedQuery(String queryNamed, final Map map);

	/**
	 * 根据预设SQL查询结果集数量
	 *
	 * @param pageIndex
	 *            当面页号
	 * @param pageSize
	 *            每页大小
	 * @return List 查询结果集合
	 *
	 */
	public List find(String hql, int pageIndex, int pageSize);

	/**
	 * 根据预设SQL查询结果集数量
	 *
	 * @param pageIndex
	 *            当面页号
	 * @param pageSize
	 *            每页大小
	 * @return List 查询结果集合
	 *
	 */
	public List findByCriteria(DetachedCriteria criteria, int pageIndex, int pageSize);

	/**
	 * 根据预设SQL查询结果集
	 *
	 * @param query
	 *            hql语句
	 * @param parameters
	 *            要传的参数集合
	 * @return List 查询结果集合
	 *
	 */
	public List find(final String query, final List parameters);

	/**
	 * 根据预设SQL查询结果集
	 *
	 * @param query
	 *            hql语句
	 * @param parameters
	 *            要传的参数集合
	 * @param pageIndex
	 *            当面页号
	 * @param pageSize
	 *            每页大小
	 * @return List 查询结果集合
	 *
	 */
	public List find(final String query, final List parameters, final int start, final int size);

	/**
	 * 根据QBC查询结果集数量
	 *
	 * @param query
	 *            hql语句
	 * @return parameters 要传的参数集合
	 */
	public Long getRowCount(final String query, final List parameters);

	/**
	 * 根据预设SQL查询结果集
	 *
	 * @param query
	 *            hql语句
	 * @param parameters
	 *            要传的参数集合
	 * @param pageInfo
	 *            翻页实例
	 * @return List 查询结果集合
	 *
	 */
	public List find(final String hql, final List parameters, final Page pageInfo);

	/**
	 * 取得受管理数据库连接
	 *
	 * @return Connection 取得受管理数据库连接
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public Connection getConnection();
	public List find(final String hql, final String[] nameForSetVar,
			final Object[] parameters, final Page pageInfo);
	
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
			final Object[] parameters, final Page pageInfo);
	/**
	 * 查询 数据库
	 * @param keyColum 分页使用的关键字段  比如 ID  distinct(id)
	 * @param hql 
	 * @param nameForSetVar
	 * @param parameters
	 * @return
	 */
	public List find(final String keyColum,final String hql, final String[] nameForSetVar,
			final Object[] parameters);
	/**
	 *
	* <p>方法名称: find|描述: 查询结果集（不分页）</p>
	* @param hql
	* @param nameForSetVar
	* @param parameters
	* @return
	 */
	public List find(final String hql, final String[] nameForSetVar,final Object[] parameters);

	/**
	 * 根据预设SQL查询结果集
	 *
	 * @param sql
	 *            sql语句
	 * @return List 查询结果集合
	 *
	 */
	public List SQLQuery(final String sql);
	
	/**
	 * 根据预设SQL查询结果集
	 * 
	 * @param sql sql语句
	 * @param parameters 请求参数
	 * @return List 查询结果集合
	 *
	 */
	public List SQLQuery(final String sql,final List parameters);
	/**
	 * 
	* <p>方法名称: SQLQuery|描述: </p>
	* @param sql
	*     sql语句
	* @param pageInfo
	*       翻页实例
	* @return
	* List 查询结果集合
	 */
	public List SQLQuery(final String sql, final Page pageInfo);

	public  Statistics  getStatics();
	
	/**
	* <p>方法名称: executeUpdateHQL|描述: 批量更新</p>
	* @param hql HQL语句
	* @param values 参数数组
	* @return
	*/
	public int executeUpdateHQL(String hql, Object[] values);
	/**
	* <p>方法名称: txBatchUpdate|描述: 批量更新数据</p>
	* @param hql
	* @param nameForSetVar
	* @param parameters
	* @throws Exception
	*/
	public void txBatchUpdate(final String hql, final String[] nameForSetVar,final Object[] parameters) throws Exception;

	/**
	* <p>根据SQL查询需要去除重复记录的数据</p>
	* @param distinctColumn 需要去重的查询字段，例如：distinct field1,field2
	* @param sql 原查询语句
	* @param nameForSetVar
	* @param parameters
	* @throws Exception
	*/
	public List SQLQuery(final String distinctColumn,final String sql, final String[] nameForSetVar,final Object[] parameters, final Page pageInfo);
	
	public List SQLQuery(final String sql, final String[] nameForSetVar,final Object[] parameters, final Page pageInfo);
	
	public List SQLQuery4Map(final String sql, final String[] nameForSetVar, final Object[] parameters,final Page pageInfo);
    
	/**
	 * <p> 方法名称: updateSQL|描述:通过JDBC API来执行该SQL语句，主要用批量的更新、删除 </p>
	 * @param sql
	 * @throws DAOException
	 */
	public void updateSQL(final String sql);
	
	
	/**
	 * <p> 方法名称: updateSQL|描述:通过JDBC API来执行该SQL语句，主要用批量的更新、删除 </p>
	 * @param sql
	 * @return int 更新行数
	 * @throws DAOException
	 */
	public int updateSQLReturnRows(final String sql);
	
	/**
	 * <p> 方法名称：queryList|描述：查询某节点下的所有子孙节点数据
	 * @param sql 
	 * @param alias 别名
	 * @param class1 类名.class
	 * @return
	 */
	public List queryList(final String sql,final String alias,final Class class1);
	
	
}
