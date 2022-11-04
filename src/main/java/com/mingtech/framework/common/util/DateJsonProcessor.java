package com.mingtech.framework.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class DateJsonProcessor  implements JsonValueProcessor {

	/**
	 * 默认的转换格式
	 */
	private String datPattern = "yyyy-MM-dd";
	public static String Default_Format_Date = "yyyy-MM-dd";

	public DateJsonProcessor() {
		super();
	}

	/**
	 * 传递转化的日期格式的字符串
	 * 
	 * @param format
	 */
	public DateJsonProcessor(String format) {
		super();
		this.datPattern = format;
	}

	public Object processArrayValue(Object value, JsonConfig arg1) {
		// TODO Auto-generated method stub
		return process(value);
	}

	public Object processObjectValue(String arg0, Object value, JsonConfig arg2) {
		// TODO Auto-generated method stub
		return process(value);
	}

	/**
	 * 处理时间问题
	 * 
	 * @param value
	 * @return
	 */
	private Object process(Object value) {
		try {
			if (value instanceof Date) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						this.datPattern);
				return simpleDateFormat.format(value);
			}
		} catch (Exception e) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					this.datPattern);
			return simpleDateFormat.format(value);
		}
		return "";
	}

}
