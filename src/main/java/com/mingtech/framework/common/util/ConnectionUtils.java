package com.mingtech.framework.common.util;
import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.mingtech.framework.common.jdbcHelper.DBUtil;
import com.mingtech.framework.common.jdbcHelper.ISqlHandler;
import com.mingtech.framework.common.jdbcHelper.RowSets;
import com.mingtech.framework.common.jdbcHelper.SqlHandler;
public class ConnectionUtils {
	private static final Logger logger = Logger.getLogger(ConnectionUtils.class);

	/**
	 * 获得数据连接
	 * 1、尝试 使用jdbc连接
	 * 2、使用数据源
	 */
	public static Connection getConn() throws Exception{
		Connection conn=null;
		try{
			if("2".equals(ProjectConfig.getInstance().getConnType())){
				Context initCtx = new InitialContext();
				DataSource datasource = (DataSource) initCtx.lookup("jdbc/bps");
				conn=datasource.getConnection();	
			}else{
				DruidDataSource JndiBean=(DruidDataSource)(SpringContextUtil.getBean("ecdsDataSource"));
				conn=JndiBean.getConnection();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		return conn;
	}
	/**
	 * 获取  恒生 迁移系统数据库连接
	 * @return
	 * @throws Exception
	 */
	public static Connection getBbspConn() throws Exception{
		Connection conn = null;
//		try{
//			//【1】jdbc连接方式
//			BasicDataSource JndiBean=(BasicDataSource)(SpringContextUtil.getBean("bbspDataSource"));
//			conn=JndiBean.getConnection();
////			if(conn==null){
////				//【2】数据源jndi
////				Context initCtx = new InitialContext();
////				DataSource datasource = (DataSource) initCtx.lookup("jdbc/ecds");
////			    conn=datasource.getConnection();	
////			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		return conn;
	}
	/**
	 * 执行 查询SQL	
	 * @param sql
	 * @return
	 */
	public static RowSets toQuerySql(String sql){
		Connection conn = null;
		try{
			conn = ConnectionUtils.getConn();
			ISqlHandler ish=new SqlHandler(conn);
			return ish.executeOnlySqlQuery(sql);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBUtil.closeConn(conn);	
		}
		return null;
	}
	
	
	/***
	 * 执行传入的sql语句
	 * @param sql
	 * @return
	 */
	public static boolean toExecuteUpdateSql(String sql)throws Exception{
		Connection conn=null;
		boolean returnValue=false;
		try{
			conn=ConnectionUtils.getConn();
			ISqlHandler ish=new SqlHandler(conn);
			returnValue= ish.executeUpdate(sql);
			DBUtil.commitConn(conn);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			DBUtil.rollbackConn(conn);
			throw new Exception(e.getMessage());
		}finally{
			DBUtil.closeConn(conn);
		}
		return returnValue;
	}
}
