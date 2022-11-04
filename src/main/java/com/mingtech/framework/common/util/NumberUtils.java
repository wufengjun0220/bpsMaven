package com.mingtech.framework.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * 数字工具类
 * @author hexin@
 * @since Jun 13, 2008
 */
public class NumberUtils {

	private NumberUtils() {
	}

	public static final String DEFAULT_DOUBLE_PATTERN = "#,##0.00";

	public static final String DEFAULT_LONG_PATTERN = "#,##0";

	public static final String DEFAULT_CURRENCY_PATTERN = "#,##0.00";

	public static final String DEFAULT_PERCENT_PATTERN = "#,##0.00%";

	/**
	 * 按标准化的数字表现方式格式化浮点型数字样式,默认格式（"#,##0.00")
	 * @param d 被格式化的double型数字
	 * @author hexin@
	 * @return String 返回标准化的长型数字样式（"#,##0.00")
	 * @since Jun 16, 2008
	 */
	public static String format(double d) {
		return format(d, "#,##0.00");
	}

	/**
	 * 按标准化的数字表现方式格式化浮点型数字样式
	 * @param d 被格式化的double型数字
	 * @param pattern 格式化的模板
	 * @author hexin@
	 * @return String 返回标准化的长型数字样式（如####,##)
	 * @since Jun 16, 2008
	 */
	public static String format(double d, String pattern) {
		if (pattern == null || "".equals(pattern)) {
			return format(d);
		} else {
			DecimalFormat df = new DecimalFormat(pattern);
			return df.format(d);
		}
	}

	/**
	 * 按标准化的数字表现方式格式化长型数字样式，默认格式(#,##0)
	 * @param number 被格式化的数字
	 * @author hexin@
	 * @return String 返回标准化的长型数字样式(#,##0)
	 * @since Jun 13, 2008
	 */
	public static String format(long l) {
		return format(l, "#,##0");
	}

	/**
	 * 按标准化的数字表现方式格式化长型数字样式
	 * @param number 被格式化的数字
	 * @param pattern 格式化的模板
	 * @author hexin@
	 * @return String 返回标准化的长型数字样式（如####,##)
	 * @since Jun 13, 2008
	 */
	public static String format(long l, String pattern) {
		if (pattern == null || "".equals(pattern)) {
			return format(l);
		} else {
			DecimalFormat df = new DecimalFormat(pattern);
			return df.format(l);
		}
	}

	/**
	 * 格式化数字(浮点型)，返回本地货币类型
	 * @param number 被格式化的数字
	 * @author hexin@
	 * @return String 返回标准化货币类型样式（如￥8,888.88)
	 * @since Jun 13, 2008
	 */
	public static String currencyFormat(double number) {
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		return currencyFormat.format(number);
	}

	/**
	 * 格式化数字(整型)，返回本地货币类型
	 * @param number 被格式化的数字
	 * @author hexin@
	 * @return String 返回标准化货币类型样式（如￥8,888)
	 * @since Jun 13, 2008
	 */
	public static String currencyFormat(long number) {
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		return currencyFormat.format(number);
	}

	/**
	 * 格式化数字(整型)，返回本地百分比类型
	 * @param number 被格式化的数字
	 * @author hexin@
	 * @return String 返回本地百分比类型（如150%)
	 * @since Jun 13, 2008
	 */
	public static String percentFormat(long number) {
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		return percentFormat.format(number);
	}

	/**
	 * 格式化数字(浮点型)，返回本地百分比类型
	 * @param number 被格式化的数字
	 * @author hexin@
	 * @return String 返回本地百分比类型（如参数为0.5，返回50%)
	 * @since Jun 13, 2008
	 */
	public static String percentFormat(double number) {
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		return percentFormat.format(number);
	}

}