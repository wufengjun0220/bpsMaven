package com.mingtech.framework.common.jdbcHelper;

import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DBUtil {
	//创建日志对象
	private static final Logger log = Logger.getLogger(DBUtil.class);
	
	//关闭数据库连接
	public static void closeConn(Connection conn){
		try{
			if(conn!=null){
				conn.close();
				conn=null;
			}
		}catch(Exception e){
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********关闭数据库连接时出现异常********");
			log.error("********异常信息如下:********");
			log.error(e);
		}
	}
	
	//关闭记录集
	public static void closeRs(ResultSet rs){
		try{
			if(rs!=null){
				rs.close();
				rs=null;
			}
		}catch(Exception e){
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********关闭查询结果集时出现异常********");
			log.error("********异常信息如下:********");
			log.error(e);
		}
	}
	
	//关闭Statement
	public static void closeStmt(Statement stmt){
		try{
			if(stmt!=null){
				stmt.close();
				stmt=null;
			}
		}catch(Exception e){
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********关闭JDBC Statement对象时出现异常********");
			log.error("********异常信息如下:********");
			log.error(e);
		}
	}
	
	//关闭PrepareStatement
	public static void closePStmt(PreparedStatement pstmt){
		try{
			if(pstmt!=null){
				pstmt.close();
				pstmt=null;
			}
		}catch(Exception e){
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********关闭JDBC PrepareStatement对象时出现异常********");
			log.error("********异常信息如下:********");
			log.error(e);
		}
	}
	
	//关闭IO写入操作对象Writer
	public static void closeWriter(Writer writer){
		try{
			if(writer!=null){
				writer.close();
				writer=null;
			}
		}catch(Exception e){
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********关闭Writer时出现异常********");
			log.error("********异常信息如下:********");
			log.error(e);
		}
	}
	
	

	
	
	
	//设置事务隔离级别
	public static void setIsolationLevel(Connection con,int level) {
		if(con!=null)
			try {
				con.setTransactionIsolation(level);
			}catch (SQLException e) {
				//记录日志
				log.error(e.getMessage(),e);
				log.error("********设置事务隔离级别时出现异常信息:********");
				log.error("********异常信息如下:********");
				log.error(e);
			}
	}
	
	//设置数据库操作是否自动提交标志位
	public static void setCommit(Connection con,boolean isAuto) {
		try {
			con.setAutoCommit(isAuto);
		}catch (SQLException e) {
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********数据库提交属性设置失败：" + e);
			log.error("********异常信息如下:********");
			log.error(e);
		} 
	}
	
	//提交数据库连接
	public static void commitConn(Connection con) {
		try{	
			con.commit();			
		}catch (SQLException e) {
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********数据库提交异常：" + e);
			log.error("********异常信息如下:********");
			log.error(e);
		} 
	}
	
	//回滚数据库
	public static void rollbackConn(Connection con) {
		try {			
			if (con != null){
			    con.rollback();			
			}
		}catch (SQLException e) {
			//记录日志
			log.error(e.getMessage(),e);
			log.error("********数据库回滚异常：" + e);
			log.error("********异常信息如下:********");
			log.error(e);
		} 
	}
	
	
}
