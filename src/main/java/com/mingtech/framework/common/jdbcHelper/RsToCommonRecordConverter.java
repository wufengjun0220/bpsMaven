package com.mingtech.framework.common.jdbcHelper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import com.mingtech.framework.common.util.DateUtils;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RsToCommonRecordConverter  {

	public RsToCommonRecordConverter(){

	}
	
	public Object convert(ResultSet rs) throws SQLException {
		ResultSetMetaData tmpMeta = rs.getMetaData();
		return convert(rs,tmpMeta);
	}
	
	public Object convert(ResultSet rs,ResultSetMetaData meta) throws SQLException {
		CommonRecord rec = new CommonRecord();
		int fieldCount = meta.getColumnCount();
		for (int i = 1; i <= fieldCount; i++) {
			String fieldValue = null;//置空
			String fieldName = meta.getColumnName(i).toLowerCase();
			int fieldType = meta.getColumnType(i);
			switch (fieldType) {
			case Types.DATE:
				Date tmpDate = rs.getDate(i);
				if (tmpDate != null) {
					fieldValue = DateUtils.toDateString(tmpDate);
				}
				break;
			case Types.TIME:
				Time tmpTime = rs.getTime(i);
				if(tmpTime != null){
					fieldValue = DateUtils.toTimeString(tmpTime);
				}
				break;
			case Types.TIMESTAMP:
				Timestamp tmpTimestamp = rs.getTimestamp(i);
				if (tmpTimestamp != null) {
					//fieldValue = DateTimeUtil.toDateTimeString(tmpTimestamp);
					fieldValue = DateUtils.toDateString(tmpTimestamp);
				}
				break;
		
			/***大字段获得值**/
			case Types.BINARY:
			case Types.BLOB:
			case Types.LONGVARBINARY:
			case Types.LONGVARCHAR:
			case Types.VARBINARY:
				byte[]  bts=rs.getBytes(i);
				fieldValue=new String(bts);
				
			
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.INTEGER:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.NUMERIC:
			default:
				fieldValue = rs.getString(i);
				fieldValue = fieldValue == null ? "": fieldValue.trim();
				break;
			}
			rec.addField(new Field(fieldName, fieldType, fieldValue));
		}
		return rec;
	}
}
