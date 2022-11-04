package com.mingtech.application.utils;

import java.text.DecimalFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import com.mingtech.application.pool.common.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.dao.GenericDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@SuppressWarnings("all")


public class ExcelUtil{
	private static final Logger logger = Logger.getLogger(ExcelUtil.class);
	public static final int PageSize = 5000;  //分页 每页行数
	public static int pageIndex = 1;  //定义初始当前页
	private StringBuffer sql;  //数据库语句
	private String[] keyArray;  //对应key
	private Object[] valArray;  //对应value

	private  static FormulaEvaluator evaluator;
	// heads

	public static List<List<String>> head(Map<String, String> map) {
		List<String> heads = new ArrayList();
		for (String value : map.values()) {
			heads.add(value);
		}

		logger.info("开始构建表头");
		List<List<String>> list = new ArrayList<List<String>>();
		for (int i = 0; i < heads.size(); i++) {
			List<String> head1 = new ArrayList<String>();
			head1.add(heads.get(i).toString());
			list.add(head1);
		}
		return list;
	}

	// values

	public static List<List<Object>> dataList(List<Object[]> values) {
		logger.info("开始处理数据");
		List<List<Object>> list = new ArrayList<List<Object>>();
		for (Integer j = 0; j < values.size(); j++) {
			List<Object> data = new ArrayList<Object>();
			for (Integer k = 0; k < values.get(j).length; k++) {
				data.add(values.get(j)[k]);
			}
			list.add(data);
		}
		return list;
	}

	/**
	 * heads -- > values
	 *
	 * @param beans
	 * @return
	 * @throws Exception
	 */
	public static List convertBeanToArray(List beans, Map heads)
			throws Exception {

		List result = new ArrayList();
		Object columnKeys[] = heads.keySet().toArray();
		for (Integer b = 0; b < beans.size(); b++) {
			Object bean = beans.get(b);
			Object values[] = new Object[columnKeys.length];
			for (Integer i = 0; i < columnKeys.length; i++) {
				String propertyName = (String) columnKeys[i];
				values[i] = BeanUtil.getValue(propertyName, bean);
				if(values[i] == null){
					values[i] = "";
				}
				// 处理 关于date类型数据convert
				if (values[i] instanceof Date) {
					Date d = (Date) values[i];
					values[i] = values[i]!=null ? DateUtils.toString(d,DateUtils.ORA_DATE_TIMES3_FORMAT) : "";
				}
			}
			result.add(values);
		}
		return result;
	}
	//获取单元格各类型值，返回字符串类型
	public static  String getCellValueByCell(Cell cell) {
		//判断是否为null或空串
		if  (cell== null  || cell.toString().trim().equals( "" )) {
			return  "" ;
		}
		String cellValue =  "" ;
		int  cellType=cell.getCellType();
		if (cellType==Cell.CELL_TYPE_FORMULA){  //表达式类型
			cellType=evaluator.evaluate(cell).getCellType();
		}
		switch  (cellType) {
			case  Cell.CELL_TYPE_STRING:  //字符串类型
				cellValue= cell.getStringCellValue().trim();
				cellValue= StringUtils.isEmpty(cellValue) ?  ""  : cellValue;
				break ;
			case  Cell.CELL_TYPE_BOOLEAN:   //布尔类型
				cellValue = String.valueOf(cell.getBooleanCellValue());
				break ;
			case  Cell.CELL_TYPE_NUMERIC:  //数值类型
				if  (HSSFDateUtil.isCellDateFormatted(cell)) {   //判断日期类型
					cellValue = DateUtils.formatDateToString(cell.getDateCellValue(),  "yyyy-MM-dd" );
				}  else  {   //否
					cellValue =  new DecimalFormat( "#.######" ).format(cell.getNumericCellValue());
				}
				break ;
			default :  //其它类型，取空串吧
				cellValue =  "" ;
				break ;
		}
		return  cellValue;
	}





	public StringBuffer getSql() {
		return sql;
	}

	public void setSql(StringBuffer sql) {
		this.sql = sql;
	}

	public String[] getKeyArray() {
		return keyArray;
	}

	public void setKeyArray(String[] keyArray) {
		this.keyArray = keyArray;
	}

	public Object[] getValArray() {
		return valArray;
	}

	public void setValArray(Object[] valArray) {
		this.valArray = valArray;
	}






}
