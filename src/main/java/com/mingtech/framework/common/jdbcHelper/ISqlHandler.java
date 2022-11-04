package com.mingtech.framework.common.jdbcHelper;

import java.util.List;


public interface ISqlHandler {
	/**
	 * 列表形式 返回 查询结果
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<String[]> getResultList(String sql) throws Exception;
	
	public RowSets queryExecute(String sql, CommonRecord params)throws Exception;
	
	public RowSets queryExecute(String sql) throws Exception;

	public boolean executeUpdate(String sql) throws Exception;	
	
	public boolean isHasSet(String sql) throws Exception;
	
	public boolean isHasSet(String sql,CommonRecord cr) throws Exception;
	/**
	 * 只 执行查询SQL
	 * @param sql
	 * @return
	 */
	public RowSets executeOnlySqlQuery(String sql);
	
	
}
