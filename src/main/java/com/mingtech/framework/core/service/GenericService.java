package com.mingtech.framework.core.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;

import com.mingtech.framework.core.page.Page;

/**
 * <p>
 * 说明:通用服务接口（一般类型服务)
 * </p>
 * 
 * @author E-mail:hexin@
 * @version
 * @since 2008-6-30 下午09:45:10
 * 
 */
public interface GenericService {

	/**
	 * 保存 对象
	 * 
	 * @param t
	 * @return
	 */
	public String txSaveEntity(Object t);

	/**
	 * 
	 * @param keyColum
	 *            分页使用的 关键字段 比如：ID distinct(id) 等等
	 * @param hql
	 *            sql语句
	 * @param nameForSetVar
	 *            条件名称
	 * @param parameters
	 *            条件值
	 * @param pageInfo
	 *            分页对象
	 * @return 查询结果
	 */
	public List find(String keyColum, String hql, String[] nameForSetVar,
			Object[] parameters, Page pageInfo);

	/**
	 * 抽象方法，要求子类返回其对应的实体在HQL查询中的名字，可以是全类名
	 * 
	 * @return Entity Name
	 */
	public String getEntityName();

	/**
	 * 抽象方法，要求子类返回其对应的实体的Class
	 * 
	 * @return Entity Class
	 */
	public Class getEntityClass();

	/**
	 * <p>
	 * 通用创建实体
	 * </p>
	 * 
	 * @param Object
	 *            等待持久化的实体对象
	 * @return Object 持久化以后的实体对象
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public void txStore(Object o);
	
	/**
	* <p>方法名称: txStoreAll|描述: 批量实例化或修改实体</p>
	* @param ts 需要实例化或修改的实体集合
	*/
	public void txStoreAll(Collection ts);

	/**
	 * <p>
	 * 删除实体
	 * </p>
	 * 
	 * @param Object
	 *            等待被删除的持久化实体
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public void txDelete(Object o);

	/**
	 * <p>
	 * 删除实体集合
	 * </p>
	 * 
	 * @param ts
	 *            等待被删除的持久化实体集合
	 * @since Jun 13, 2008
	 */
	public void txDeleteAll(Collection ts);

	/**
	 * 保存一个对象实体 并返回主键ID
	 * 
	 * @param object
	 * @return
	 */
	public Object txSaveStore(Object object);

	/**
	 * <p>
	 * 根据主健确定一个实体
	 * </p>
	 * 
	 * @param id
	 *            实体类OID
	 * @return 根据OID确定的唯一主健
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public Object load(Serializable id);

	public Object load(String id);

	public Object load(Serializable id, Class cls);

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
	 * 
	 * @param keyColum  分页使用的  关键字段   比如：ID distinct(id) 等等
	 * @param hql sql语句
	 * @param nameForSetVar 条件名称
	 * @param parameters  条件值 
	 * @return 查询结果
	 */
	public List find(String keyColum,String hql, String [] nameForSetVar,Object[] parameters) ;
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
	 * @return Long 查询结果
	 * @author hexin@
	 * @since Jun 24, 2008
	 */
	public Long getRowCount(DetachedCriteria detachedCriteria);

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

	public List find(String hql, int pageIndex, int pageSize);

	public List findByCriteria(DetachedCriteria criteria, int pageIndex,
			int pageSize);

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
	public List find(final String query, final List parameters,
			final int start, final int size);

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
	public List find(final String hql, final List parameters,
			final Page pageInfo);
	/**
	 * 查找所有
	 * @param 
	 * @since  Dec 4, 2008
	 * @author zhaoqian
	 * @param cls
	 * @return
	 */
	public List findAll();
	public List find(String hql, String [] nameForSetVar,Object[] parameters,Page pageInfo);
	
	public List find(String hql, String [] nameForSetVar,Object[] parameters);
	
	/**
	 * 
	* <p>方法名称: getTotalMoney|描述:通过批次获取明细票据总金额 </p>
	* @param batchId
	* @return
	 */
	public BigDecimal getTotalMoney(String batchId);

	//************************//
	// 查询数据元数据
	//************************//
//	public ClassMetadata getClassMetadata(Class claz) throws Exception;
//	public String[] getPropertyName(ClassMetadata md) throws Exception;
//	public String getTableName(ClassMetadata md) throws Exception;
//	public List getOptions(String tableName,String fieldName) throws Exception;
	//public FieldCodeMap getAOptionCode(String tableName, String fieldName)throws Exception;
	//*********End************//
	/**
	 * 根据查询bean查询数据
	 * @param t
	 * @return
	 */
	public <T> List queryByObj(T t,String name);
	public <T> String queryByObjJson(T t,String name);
	public <T> String queryByObjJson(T t,String name,Page page);
	public <T> List queryByObj(T t,String name,Page page);
	public <T> String queryByObjJson(T t,String name,Page p,String keyWord,String order);
	/**
	 * 强制同步obj的缓存
	 * @param obj
	 */
	public void evictObjCache(Object obj);
	/**
	 * 清理所有缓存
	 */
	public void clearCache();
	/**
	 * 强制同步所以类的缓存
	 */
	public void flushCache();
}
