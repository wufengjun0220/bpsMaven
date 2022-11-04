package com.mingtech.framework.common.jdbcHelper;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class RowSets implements ITransformable {
	private static final Logger logger = Logger.getLogger(RowSets.class);


	//private Log log = Logger.getLogger(RowSets.class);
	private Collection _records = new Collection();
	private int _colCount = 0;

	/**
	 * default constructor with no parameter
	 */
	public RowSets() {
	}

	/**
	 * resultSets to RowSets Oject
	 * 
	 * @param rs
	 */
	public RowSets(ResultSet rs) throws Exception {
		int count=0;
		if (rs == null)
			return;
		RsToCommonRecordConverter converter = new RsToCommonRecordConverter();
		try {
			ResultSetMetaData mData = rs.getMetaData();
			_colCount = mData.getColumnCount();
			while (rs.next()) {
				count++;
				CommonRecord rec = (CommonRecord) converter.convert(rs, mData);
				this._records.addElement(rec);
			}
			if(count>100000){
				throw new Exception(new Exception("返回的结果集太多,超过100000条,请检查程序进行调整"));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param rs
	 * @param p_begin
	 * @param p_end
	 */

	public RowSets(ResultSet rs, int p_begin, int p_end) {
		if (rs == null)
			return;
		int count = 0;
		RsToCommonRecordConverter converter = new RsToCommonRecordConverter();
		try {
			ResultSetMetaData mData = rs.getMetaData();
			_colCount = mData.getColumnCount();
			while (rs.next()) {
				count++;
				if ((count >= p_begin) && (count <= p_end)) {
					CommonRecord rec = (CommonRecord) converter.convert(rs,
							mData);
					this._records.addElement(rec);
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
	}
	/**
	 * Add a common Record to RowSets,key is a GUID
	 * 
	 * @param commonRecord
	 *            a record
	 * @return the position of this record
	 */
	public synchronized int addRecord(CommonRecord commonRecord) {
		this._records.addElement(commonRecord);
		int i = commonRecord.length();
		if (i > this._colCount) {
			this._colCount = i;
		}
		return this._records.length();
	}

	/**
	 * Add a column.
	 * 
	 * @param columnName
	 *            the column's name
	 * @param data
	 *            object(include array)
	 * 
	 * @return the new column count
	 */
	public synchronized int addColumn(String columnName, Object data) {
		try {
			String str = (String) data;
			for (int i = 0; i < recordCount(); i++) {
				((CommonRecord) _records.getElement(i)).addField(columnName,
						new Field(columnName, str));
			}
			return (++_colCount);
		} catch (ClassCastException castExp) {
			String errStr = castExp.getMessage();
			boolean isArray = false;

			if ((data.toString().indexOf("[L") == 0))
				isArray = true;

			if (isArray) {
				Object[] obj = (Object[]) data;
				int count = obj.length;

				for (int i = 0; i < recordCount(); i++) {
					Object o = null;
					if (i < count)
						o = obj[i];
					else
						o = "";
					((CommonRecord) _records.getElement(i)).addField(
							columnName, new Field(columnName, o));
				}
				return (++_colCount);
			} else { //if not an array, for example, an object that can not
				// cast to string;
				for (int i = 0; i < recordCount(); i++) {
					((CommonRecord) _records.getElement(i)).addField(
							columnName, new Field(columnName, data));
				}
				return (++_colCount);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return _colCount;
	}
	
	public synchronized int appendRowSets(RowSets rs) {
		for(int i = 0 ;i<rs.recordCount();i++) {
			this._records.addElement(rs.getRecord(i));
		}
		return this._records.length();

	}

	/**
	 * Append a RowSets object.
	 * 
	 * @param rs
	 *            another RowSets
	 */
	public synchronized void append(RowSets rs) {
		int len = _records.length();
		int row = (len == rs.recordCount()) ? len : 0;
//		int row = rs.recordCount();
		int col = rs.columnCount();

		if (row != 0) {
			for (int i = 0; i < row; i++) {
				CommonRecord rec = new CommonRecord();
				rec = rs.getRecord(i);
				for (int j = 0; j < col; j++) {
					String columnName = rec.getField(j).getName();
					Object value = rec.getField(j).getValue();

					((CommonRecord) _records.getElement(i)).addField(
							columnName, new Field(columnName, value));
				}
			}
			_colCount += col;
		} else {
			//            log.warn("* [RowSets.java - append() method] =====> recordCount
			// invalid!");
		}
	}

	/**
	 * Get a record by index
	 * 
	 * @param index
	 *            the specified index
	 * @return Return the object of CommonRecord
	 */
	public CommonRecord getRecord(int index) {
		return (CommonRecord) _records.getElement(index);
	}

	/**
	 * Get the number of the all records
	 * 
	 * @return Return the number of the all records
	 */
	public int recordCount() {
		return _records.length();
	}

	/**
	 * Get the number of the all columns
	 * 
	 * @return Return the number of the all columns
	 */
	public int columnCount() {
		return _colCount;
	}

	/**
	 * Display all records of the RowSets
	 *  
	 */
	public String displayContent() {
		int i = 0, j = 0;
		CommonRecord rec = null;
		StringBuffer sb = new StringBuffer();
		for (i = 0; i < recordCount(); i++) {
			rec = getRecord(i);
			for (j = 0; j < columnCount(); j++) {
				sb.append(rec.getFieldName(j) + "\t=\t" + rec.getString(j)
						+ "\n");
			}
			sb.append("--------------- record " + i
					+ " end ------------------\n");
		}
		return sb.toString();
	}

	/**
	 * Tests if this record has no components.
	 * 
	 * @return true if this record has no components; false otherwise.
	 */
	public boolean isEmpty() {
		return recordCount() < 1;
	}

	public void removeField(String name) {
		int i = 0, len = this.recordCount();
		CommonRecord rec = null;
		Field field = null;
		for (i = 0; i < len; i++) {
			rec = this.getRecord(i);
			field = rec.getField(name);
			if (field != null) {
				rec.removeField(name);
			}
		}
	}

	public void clear() {
		_records.clear();
	}

	/**
	 * remove a record specified by index from the set
	 * 
	 * @param index
	 *            index of record in set
	 *  
	 */
	public void removeRecord(int index) {
		_records.removeElement(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.ccb.bdb.framework.core.ITransformable#rename(java.lang.Object,
	 *      java.lang.Object)
	 */
	public void rename(Object oldKey, Object newKey) {
		CommonRecord rec = null;
		for (int i = 0; i < this.recordCount(); i++) {
			rec = this.getRecord(i);
			Field fld = rec.removeField((String) oldKey);
			if (fld != null) {
				fld.setName((String) newKey);
				rec.addField(fld);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.ccb.bdb.framework.core.ITransformable#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		RowSets rs = new RowSets();
		CommonRecord rec = null;
		CommonRecord tmp = null;
		Field fld = null;
		for (int i = 0; i < this.recordCount(); i++) {
			tmp = new CommonRecord();
			rec = this.getRecord(i);
			fld = rec.removeField((String) key);
			if (fld != null) {
				tmp.addField(fld);
			}
			rs.addRecord(tmp);
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.com.ccb.bdb.framework.core.ITransformable#getValue(java.lang.Object)
	 */
	public Object getValue(Object key) {
		Object[] values = new Object[this.recordCount()];
		for (int i = 0; i < this.recordCount(); i++) {
			values[i] = this.getRecord(i).getValue(key);
		}
		return values;
	}

	/**
	 * put all ViewBean to ViewBean[]
	 * 
	 * @return
	 */
	public ViewBean[] toViewBeans() {
		int i = 0, count = this.recordCount();
		ViewBean viewBean[] = new ViewBean[count];
		for (i = 0; i < count; i++) {
			viewBean[i] = (this.getRecord(i)).toViewBean();
		}
		return viewBean;
	}

}