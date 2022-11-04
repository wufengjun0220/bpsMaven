package com.mingtech.framework.common.jdbcHelper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mingtech.framework.common.util.DateUtils;




/**
 * @author Admin
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class SqlHandler implements ISqlHandler {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SqlHandler.class);

	private Connection conn = null;

	private PreparedStatement pstmt = null;

	private Statement stmt = null;
	
	

	public SqlHandler(Connection conn) {
		this.conn=conn;
	}
	
	private Connection getConn(){
		
			return this.conn;
	}
	
	public List<String[]> getResultList(String sql) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = this.createStatement();
			rs = stmt.executeQuery(sql);
			return resultSet2List(rs);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception ex) {
			}
		}
		return null;
	}
	private List<String[]> resultSet2List(ResultSet rs) throws Exception {
		int count = rs.getMetaData().getColumnCount();
		List<String[]> list = new ArrayList<String[]>();
		while (rs.next()) {
			String[] data = new String[count];
			for (int i = 0; i < count; i++) {
				Object value = rs.getObject(i + 1);
				data[i] = convert2String(value);
			}
			list.add(data);
		}
		return list;
	}
	private String convert2String(Object obj) {
		String str = "";

		if (obj != null) {
			if (obj instanceof String) {
				str = (String) obj;
			} else if (obj instanceof Integer || obj instanceof Long) {
				str = String.valueOf(obj);
			} else if (obj instanceof Double) {
				str = new DecimalFormat("#.000000").format(((Double) obj).doubleValue());
			} else if (obj instanceof Float) {
				str = new DecimalFormat("#.000000").format(((Float) obj).doubleValue());
			} else if (obj instanceof BigDecimal) {
				str = new DecimalFormat("#.000000").format(((BigDecimal) obj).doubleValue());
			} else if (obj instanceof Date) {
				str = new SimpleDateFormat("yyyyMMdd").format((Date) obj);
			} else {
				str = obj.toString();
			}
		}
		return str;
	}
	private Statement createStatement() throws Exception {
		boolean isNew = false;
		if (conn == null) {
			isNew = true;
		}
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (Exception ex) {
			if (isNew) {
				throw ex;
			} else {
				stmt = conn.createStatement();
			}
		}

		return stmt;
	}
	
	private ResultSet executeQuery_1(String sql) throws Exception {
		ResultSet rs = null;
		try {
			stmt = getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException sqlExp) {
			throw new Exception(sqlExp);
		}
		try {
			logger.debug("sql:" + sql);
			rs = stmt.executeQuery(sql);
		} catch (SQLException sqlExp) {
			throw new Exception(sqlExp);
		}
		return rs;
	}

	/**
	 * excute query sql by statement and parameters.
	 * 
	 * @param sql
	 *            the sql statement
	 * @params params the sql statement's parameter fields should be filled in
	 * 
	 * @return ResultSet that be queried .
	 * @throws Exception
	 * @throws Exception
	 */

	private ResultSet executeQuery_1(String sql, CommonRecord params)
			throws Exception {

		ResultSet rs = null;
		try {
			logger.debug("sql:" + sql);
			pstmt = getConn().prepareStatement(sql);
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			throw new Exception(sqlExp);
		}
		setParameters(params);
		try {
			long s1=System.currentTimeMillis();
			rs = pstmt.executeQuery();
			long s2=System.currentTimeMillis();
			if(s2-s1>5000)
			logger.debug("sql:" + sql + "\n"+params.displayContent()+"\n"+";耗时="+(s2-s1));
		} catch (SQLException sqlExp_1) {
			sqlExp_1.printStackTrace();
			throw new Exception(sqlExp_1);
		}
		return rs;
	}
	/**
	 * 没有双向游标的查询结果集，
	 */
	private ResultSet executeQuery_without_scroll(String sql,
			CommonRecord params) throws Exception {

		ResultSet rs = null;
		try {			
			pstmt = getConn().prepareStatement(sql);
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			throw new Exception(sqlExp);
		}
		setParameters(params);
		try {
			long b=System.currentTimeMillis();
			rs = pstmt.executeQuery();
			long e=System.currentTimeMillis();
			if((e -b)>5000){//如大于5秒
				logger.debug("sql+\n"+params.displayContent()+"\n"+";耗时="+(e-b));
			}
		} catch (SQLException sqlExp_1) {
			sqlExp_1.printStackTrace();
			throw new Exception(sqlExp_1);
		}
		return rs;
	}
	/**
	 * 判断结果集是否有列表
	 */
	public boolean isHasSet(String sql) throws Exception {
		boolean ret = false;
		ResultSet rs = null;
		try {
			pstmt = getConn().prepareStatement(sql);
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			throw new Exception(sqlExp);
		}
		try {
			rs = pstmt.executeQuery();
			if (rs.next())
				ret = true;
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			throw new Exception(sqlExp);
		} finally {
			DBUtil.closeRs(rs);
			DBUtil.closePStmt(pstmt);
		}

		return ret;
	}

	public boolean isHasSet(String sql, CommonRecord params)
			throws Exception {
		boolean ret = false;
		ResultSet rs = null;
		try {
			pstmt = getConn().prepareStatement(sql);
		} catch (SQLException sqlExp) {
			sqlExp.printStackTrace();
			throw new Exception(sqlExp );
		}
		setParameters(params);
		try {
			rs = pstmt.executeQuery();
			if (rs.next())
				ret = true;
		} catch (SQLException sqlExp) {
			throw new Exception(sqlExp );
		} finally {
			DBUtil.closeRs(rs);
			DBUtil.closePStmt(pstmt);
		}

		return ret;
	}
	/**
	 * 只 执行查询SQL
	 * @param sql
	 * @return
	 */
	public RowSets executeOnlySqlQuery(String sql){
		RowSets records = null;
		ResultSet rs = null;
		Statement stm =null;
		try {			
			stm = getConn().createStatement();
			rs=stm.executeQuery(sql);
			records = new RowSets(rs);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(stm!=null){
					stm.close();
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
		return records;
	}
	public RowSets queryExecute(String sql, CommonRecord params)
			throws Exception {
		RowSets records = null;
		ResultSet rs = null;
		try {
			logger.debug("sql:" + sql);
			rs = executeQuery_without_scroll(sql, params);
			records = new RowSets(rs);
		} catch (Exception daoE) {
			throw daoE;
		} finally {
			DBUtil.closeRs(rs);
			DBUtil.closePStmt(pstmt);
		}

		return records;
	}

	/**
	 * also can use return RowSets method excute a query sql statement with only
	 * one parameters
	 * 
	 * @return List that be queried.
	 * @throws Exception
	 */

	/**
	 * excute a query sql statement no parameters and get record set
	 * 
	 * @return RowSets that be queried .
	 * @throws Exception
	 */
	public RowSets queryExecute(String sql) throws Exception {
		return this.queryExecute(sql, new CommonRecord());
	}
	
	/**
	 * excute update sql by statement and parameters.
	 * 
	 * @param sql
	 *            the sql statement
	 * @params params the sql statement's parameter fields should be filled in
	 * 
	 * @return if success return true else false
	 * @throws Exception
	 */
	public boolean executeUpdate(String sql, CommonRecord params)
			throws Exception {
		boolean ret = false;
		long s = System.currentTimeMillis();
		try {
			logger.debug("sql:" + sql);
			pstmt = getConn().prepareStatement(sql);
		} catch (SQLException sqlExp) {
			throw new Exception(sqlExp);

		}
		setParameters(params);
		try {
			pstmt.executeUpdate();

		} catch (SQLException sqlExp_1) {
			
			throw new Exception(sqlExp_1);

		} finally {
			DBUtil.closePStmt(pstmt);
		}
		ret = true;
		return ret;
	}
	/**
	 * excute update sql with a statement which is no need to fill in parameters
	 * any more.
	 * 
	 * @return if success return true else false
	 * @throws Exception
	 */
	public boolean executeUpdate(String sql) throws Exception {
		CommonRecord comru = new CommonRecord();
		return this.executeUpdate(sql, comru);
	}
	/**
	 * @param params
	 */
	private void setParameters(CommonRecord params) throws Exception {
		int i = 0, count = params.size();
		for (i = 0; i < count; i++) {
			setParameter(i, params.getField(i));
		}
	}
	/**
	 * @param i
	 * @param field
	 */
	protected void setParameter(int index, Field field) throws Exception {
		String value = null;
		try {
			switch (field.getType()) {
			case Types.BIGINT:
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				value = (String) field.getValue();
				if (value == null) {
					pstmt.setNull(index + 1, field.getType());
				} else {
					pstmt.setInt(index + 1, Integer.parseInt(value));
				}
				break;
			case Types.FLOAT:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.REAL:
				value = (String) field.getValue();
				if (value == null) {
					pstmt.setNull(index + 1, field.getType());
				} else {
					pstmt.setDouble(index + 1, Double.parseDouble(value));
				}
				break;
			case Types.BINARY:
			case Types.BLOB:
			case Types.VARBINARY:
				if (field.getValue() == null) {
					pstmt.setNull(index + 1, field.getType());
				} else {
					String strTmp = "null value";
					pstmt.setObject(index + 1, strTmp.getBytes());
				}
				break;
			case Types.LONGVARBINARY:
			case Types.LONGVARCHAR:
				if (field.getValue() == null) {
					pstmt.setNull(index + 1, field.getType());
				} else {
					pstmt.setObject(index + 1, field.getValue());
				}
				break;
			case Types.DATE: // ckq add
				if (field.getValue() == null) {
					pstmt.setNull(index + 1, field.getType());
				} else {
					String tmpValue = (String) field.getValue();
					pstmt.setDate(index + 1, new Date(DateUtils.parse(
							tmpValue).getTime()));
				}
				break;
			case Types.TIMESTAMP:
				if (field.getValue() == null) {
					pstmt.setNull(index + 1, Types.TIMESTAMP);
				} else {
					String tmpValue = (String) field.getValue();
					Timestamp tstr = new Timestamp(DateUtils.parse(tmpValue)
							.getTime());
					pstmt.setTimestamp(index + 1, tstr);
				}
				break;
			default:
				value = (String) field.getValue();
				if (value == null) {
					pstmt.setNull(index + 1, field.getType());
				} else {
					pstmt.setString(index + 1, value);
				}
				break;
			}
		} catch (SQLException sqlExp) {
			throw new Exception(sqlExp);
		} catch (java.text.ParseException parseExp) {
				logger.error("解析失败："+field.getName()+"="+field.getValue());
				throw new Exception(parseExp );
		}
		catch (Exception e){
			logger.error("解析失败："+field.getName()+"="+field.getValue());
		}
	}	
	public void finalRelease(){
		DBUtil.closeStmt(stmt);
		DBUtil.closePStmt(pstmt);		
	}
}