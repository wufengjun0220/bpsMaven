package com.mingtech.framework.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.runmanage.domain.RunState;
import com.mingtech.application.runmanage.service.RunStateServiceFactory;

/**
 * 时间工具类，保存各种时间工具方法
 *
 * @author hexin@
 * @since Jun 13, 2008
 */
public class DateUtils {

	private static Logger logger = Logger.getLogger(DateUtils.class);

	public static final String DATE_FORMAT = "MM/dd/yyyy";

	public static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm";

	public static final String ORA_DATE_FORMAT = "yyyyMMdd";
	
	public static final String ORA_DATE_FORMAT2 = "yyMMdd";

	public static final String ORA_DATE_FORMAT3 = "HHmmss";
	
	public static final String ORA_DATE_TIME_FORMAT = "yyyyMMddHHmm";

	public static final String ORA_DATE_TIMES_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

	public static final String ORA_DATES_FORMAT = "yyyy-MM-dd";

	public static final String ORA_DATE_TIMES2_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String ORA_DATE_TIMES3_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String ORA_TIME2_FORMAT = "HH:mm:ss";
	public static final String ORA_DATE_TIMES1_FORMAT = "yyyyMMddHHmmssSSS";
	public static final String ORA_DATES_FORMAT_CT = "yyyy年MM月dd日";
	
	private static final int dayArray[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
			31, 30, 31 };

	private DateUtils() {
	}
	
	public synchronized static String getTimeStamp(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return df.format(new Date());
	}
	public static Date parse(String s) throws java.text.ParseException  {
		if (s == null) {
			return null;
		}
		try{
			return parseDate(s);
		}catch(Exception e){
			throw new java.text.ParseException(e.getMessage(),1);
		}
	}
	
	/**
	 * 解析日期字符串成日期
	 * @param dateStr 支持的日期字符格式：yyyy-MM-dd，yyyyMMdd，yyyy/MM/dd。
	 * @return
	 * @throws Exception
	 */
	public static Date parseDate(String dateStr) throws Exception{
		Date result = null;
		SimpleDateFormat sdf = null;
		String msg = "支持的日期格式：yyyy-MM-dd，yyyyMMdd，yyyy/MM/dd。";
		try{
			//8位数字 yyyyMMdd
			if(Pattern.compile("^(\\d{8})").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyyMMdd");
				result = sdf.parse(dateStr);
			}
			
			//短线分隔 yyyy-MM-dd
			else if(Pattern.compile("^(\\d{4})-(\\d{2}-(\\d{2}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				result = sdf.parse(dateStr);
			}else if(Pattern.compile("^(\\d{4})-(\\d{2}-(\\d{1}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy-MM-d");
				result = sdf.parse(dateStr);
			}else if(Pattern.compile("^(\\d{4})-(\\d{1}-(\\d{1}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy-M-d");
				result = sdf.parse(dateStr);
			}else if(Pattern.compile("^(\\d{4})-(\\d{1}-(\\d{2}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy-M-dd");
				result = sdf.parse(dateStr);
			}
			
			//斜线分隔 yyyy/MM/dd
			else if(Pattern.compile("^(\\d{4})/(\\d{2}/(\\d{2}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy/MM/dd");
				result = sdf.parse(dateStr);
			}else if(Pattern.compile("^(\\d{4})/(\\d{2}/(\\d{1}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy/MM/d");
				result = sdf.parse(dateStr);
			}else if(Pattern.compile("^(\\d{4})/(\\d{1}/(\\d{1}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy/M/d");
				result = sdf.parse(dateStr);
			}else if(Pattern.compile("^(\\d{4})/(\\d{1}/(\\d{2}))").matcher(dateStr).matches()){
				sdf = new SimpleDateFormat("yyyy/M/dd");
				result = sdf.parse(dateStr);
			}else {
				throw new Exception("日期格式不正确：dateStr，" + msg);
			}
			
			//把解析出的日期结果格式化为原字符串格式，与原串比较，如一致则是正确的日期。
			String temp = sdf.format(result);
			if(!temp.equals(dateStr)){
				throw new Exception("无效日期数据【" + dateStr + "】，解析结果【" + temp + "】与原数据不一致，" + msg);
			}
		} catch (ParseException e) {
			throw new Exception("无效日期数据【" + dateStr + "】，" + msg);
		}
		return result;
	}
	/**
	 * 类型转换日期
	 *
	 * @param dateString
	 *            日期格式
	 * @param partten
	 *            格式模板(如：yyyy-MM-dd)
	 * @author hexin@
	 * @return StringToDate 类型转换后的日期
	 * @since Jun 13, 2008
	 */
	public synchronized static Date StringToDate(String dateString,
			String partten) {
		SimpleDateFormat df = new SimpleDateFormat(partten);
		Date date = null;
		try {
			date = df.parse(dateString);
		} catch (Exception e) {
			logger.error("DateUtils.StringToDate error!");
			logger.error(e.getMessage(),e);
		}
		return date;
	}

	/**
	 * 日期类型转换成字符型
	 *
	 * @param date
	 *            Date类型的给定日期
	 * @param partten
	 *            格式模板(如：yyyy-MM-dd)
	 * @return 类型转换后的字符型日期
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static String toString(Date date, String partten) {
		if (date == null) {
			return "";
		} else {
			SimpleDateFormat df = new SimpleDateFormat(partten);
			return df.format(date);
		}
	}
	/**
	 * 将日期 转换成  yyyy-MM-dd  返回
	 * @param date
	 * @return
	 */
	public static String toDateString(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdff.format(date);
		return dateStr;
	} 
	/**
	 * 将日期 时间  转化成字符串   日期+时分秒
	 * @param date
	 * @return
	 */
	public static String toDateTimeString(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String s = "";
		s += cal.get(Calendar.YEAR);
		s += "-";
		s += cal.get(Calendar.MONTH) + 1;
		s += "-";
		s += cal.get(Calendar.DATE);
		s += " ";
		s += cal.get(Calendar.HOUR_OF_DAY);
		s += ":";
		s += cal.get(Calendar.MINUTE);
		s += ":";
		s += cal.get(Calendar.SECOND);
		return s;
	}
	/**
	 * 返回 日期 的    时分秒
	 * @param date
	 * @return
	 */
	public static String toTimeString(Date date) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String s = "" + cal.get(Calendar.HOUR_OF_DAY);
		s += ":";
		s += cal.get(Calendar.MINUTE);
		s += ":";
		s += cal.get(Calendar.SECOND);
		return s;
	}
	/**
	 * 给定字符串日期按模板转换成Calendar类型
	 *
	 * @param dateString
	 *            字符串形式的给定日期
	 * @param partten
	 *            格式模板(如：yyyy-MM-dd)
	 * @return 类型转换后的Calendar对象
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static Calendar parseCalendarFormat(String dateString,
			String pattern) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(pattern);
		Calendar cal = null;
		simpledateformat.applyPattern(pattern);
		try {
			simpledateformat.parse(dateString);
			cal = simpledateformat.getCalendar();
		} catch (Exception e) {
			logger.error("DateUtils.parseCalendarFormat error!");
			logger.error(e.getMessage(),e);
		}
		return cal;
	}

	/**
	 * Calendar类型转换成字符型
	 *
	 * @param cal
	 *            Calendar日期类型
	 * @param partten
	 *            格式模板(如：yyyy-MM-dd)
	 * @return 类型转换后的字符型日期
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static String getDateMilliFormat(Calendar cal,
			String pattern) {
		return toString(cal.getTime(), pattern);
	}

	/**
	 * 取得Calendar实例
	 *
	 * @return Calendar类型实例
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static Calendar getCalendar() {
		return GregorianCalendar.getInstance();
	}

	/**
	 * 取得当前年的某一个月的最后一天
	 *
	 * @param month
	 *            数字类型的月份值
	 * @return 参数月份的最后一天
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getLastDayOfMonth(int month) {
		if (month < 1 || month > 12) {
			return -1;
		}

		int retn = 0;

		if (month == 2) {
			if (isLeapYear()) {
				retn = 29;
			} else {
				retn = dayArray[month - 1];
			}
		} else {
			retn = dayArray[month - 1];
		}
		return retn;
	}

	/**
	 * 取得当某年的某一个月的最后一天
	 *
	 * @param year
	 *            数字类型的年份值
	 * @param month
	 *            数字类型的月份值
	 * @return 参数月份的最后一天
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getLastDayOfMonth(int year, int month) {
		if (month < 1 || month > 12) {
			return -1;
		}

		int retn = 0;

		if (month == 2) {
			if (isLeapYear(year)) {
				retn = 29;
			} else {
				retn = dayArray[month - 1];
			}
		} else {
			retn = dayArray[month - 1];
		}
		return retn;
	}

	/**
	 * 取得某日期所在月的最后一天
	 *
	 * @param date
	 *            Date类型的某日期
	 * @return 最后一天的日期型结果
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static Date getLastDayOfMonth(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		switch (gc.get(2)) {
		case 0: // '\0'
			gc.set(5, 31);
			break;

		case 1: // '\001'
			gc.set(5, 28);
			break;

		case 2: // '\002'
			gc.set(5, 31);
			break;

		case 3: // '\003'
			gc.set(5, 30);
			break;

		case 4: // '\004'
			gc.set(5, 31);
			break;

		case 5: // '\005'
			gc.set(5, 30);
			break;

		case 6: // '\006'
			gc.set(5, 31);
			break;

		case 7: // '\007'
			gc.set(5, 31);
			break;

		case 8: // '\b'
			gc.set(5, 30);
			break;

		case 9: // '\t'
			gc.set(5, 31);
			break;

		case 10: // '\n'
			gc.set(5, 30);
			break;

		case 11: // '\013'
			gc.set(5, 31);
			break;
		}

		if (gc.get(2) == 1 && isLeapYear(gc.get(1))) {
			gc.set(5, 29);
		}
		return gc.getTime();
	}

	/**
	 * 取得某日期所在月的最后一天
	 *
	 * @param date
	 *            Calendar类型的某日期
	 * @return 最后一天的日期型结果
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static Calendar getLastDayOfMonth(Calendar gc) {
		switch (gc.get(2)) {
		case 0: // '\0'
			gc.set(5, 31);
			break;

		case 1: // '\001'
			gc.set(5, 28);
			break;

		case 2: // '\002'
			gc.set(5, 31);
			break;

		case 3: // '\003'
			gc.set(5, 30);
			break;

		case 4: // '\004'
			gc.set(5, 31);
			break;

		case 5: // '\005'
			gc.set(5, 30);
			break;

		case 6: // '\006'
			gc.set(5, 31);
			break;

		case 7: // '\007'
			gc.set(5, 31);
			break;

		case 8: // '\b'
			gc.set(5, 30);
			break;

		case 9: // '\t'
			gc.set(5, 31);
			break;

		case 10: // '\n'
			gc.set(5, 30);
			break;

		case 11: // '\013'
			gc.set(5, 31);
			break;
		}
		if (gc.get(2) == 1 && isLeapYear(gc.get(1))) {
			gc.set(5, 29);
		}
		return gc;
	}

	/**
	 * 取得某日期所在周的最后一天
	 *
	 * @param date
	 *            Date类型日期参数
	 * @return 所在周的最后一天日期
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static Date getLastDayOfWeek(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		switch (gc.get(7)) {
		case 1: // '\001'
			gc.add(5, 6);
			break;

		case 2: // '\002'
			gc.add(5, 5);
			break;

		case 3: // '\003'
			gc.add(5, 4);
			break;

		case 4: // '\004'
			gc.add(5, 3);
			break;

		case 5: // '\005'
			gc.add(5, 2);
			break;

		case 6: // '\006'
			gc.add(5, 1);
			break;

		case 7: // '\007'
			gc.add(5, 0);
			break;
		}
		return gc.getTime();
	}

	/**
	 * 取得某日期所在周的第一天
	 *
	 * @param date
	 *            Date类型日期参数
	 * @return 当周的第一天日期
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static Date getFirstDayOfWeek(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		switch (gc.get(7)) {
		case 1: // '\001'
			gc.add(5, 0);
			break;

		case 2: // '\002'
			gc.add(5, -1);
			break;

		case 3: // '\003'
			gc.add(5, -2);
			break;

		case 4: // '\004'
			gc.add(5, -3);
			break;

		case 5: // '\005'
			gc.add(5, -4);
			break;

		case 6: // '\006'
			gc.add(5, -5);
			break;

		case 7: // '\007'
			gc.add(5, -6);
			break;
		}
		return gc.getTime();
	}
	
	/**
	 * 取得某日期所在周几
	 *
	 * @param date
	 *            Date类型日期参数
	 * @return 当周的第一天日期
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getDayOfWeek(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		return gc.get(7);
	}	
	/**
	 * 当前年是否闰年
	 *
	 * @return 返回true为润年，否则不是
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static boolean isLeapYear() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(1);
		return isLeapYear(year);
	}

	/**
	 * 给定年是否闰年
	 *
	 * @param 整数类型的年份
	 * @return 返回true为润年，否则不是
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static boolean isLeapYear(int year) {
		if (year % 400 == 0) {
			return true;
		}
		if (year % 4 == 0) {
			return year % 100 != 0;
		} else {
			return false;
		}
	}

	/**
	 * 给定日期所在年是否闰年
	 *
	 * @param Date
	 *            类型的日期
	 * @return 返回true为润年，否则不是
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static boolean isLeapYear(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		int year = gc.get(1);
		return isLeapYear(year);
	}

	/**
	 * 给定日期所在年是否闰年
	 *
	 * @param Calendar
	 *            类型的日期
	 * @return 返回true为润年，否则不是
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static boolean isLeapYear(Calendar gc) {
		int year = gc.get(1);
		return isLeapYear(year);
	}

	/**
	 * 取得当前日期简体汉字的星期
	 *
	 * @return 取得中文版本的星期,例如:星期三
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static String getWeek() {
		SimpleDateFormat format = new SimpleDateFormat("E");
		Date date = new Date();
		String time = format.format(date);
		return time;
	}

	/**
	 * 取得给定日期简体汉字的星期
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 取得中文版本的星期,例如:星期三
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static String getWeek(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("E");
		String time = format.format(date);
		return time;
	}

	/**
	 * 取得给定日期年份
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 整数类型的年(如:2008)
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getYear(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(1);
	}

	/**
	 * 取得给定日期月份月
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 整数类型的月
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getMonth(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(2) + 1;
	}

	/**
	 * 取得给定日期是日
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 整数类型的日
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getDay(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(5);
	}

	/**
	 * 取得给定日期是日
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 整数类型的日
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getHour(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(11);
	}

	/**
	 * 取得给定日期的分钟
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 整数类型的分钟
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getMinute(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(12);
	}

	/**
	 * 取得给定日期的秒
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 整数类型的秒
	 * @author hexin@
	 * @since Jun 13, 2008
	 */
	public synchronized static int getSecond(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(13);
	}

	/**
	 * 取得给定日期的下一天日期
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 下一天的日期
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static Date getNextDay(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(5, 1);
		return gc.getTime();
	}

	/**
	 * 取得给定日期的一个月后的日期
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 给定日期的一个月后的日期
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static Date getNextMonth(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(2, 1);
		return gc.getTime();
	}
	
	/**
	* <p>方法名称: getProMonth|描述: 取上一个月</p>
	* @param date
	* @return
	*/
	public synchronized static Date getProMonth(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(2, -1);
		return gc.getTime();
	}

	/**
	 * 取得给定日期的一个星期后的日期
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 给定日期的一个星期后的日期
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static Date getNextWeek(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(5, 7);
		return gc.getTime();
	}

	/**
	 * 取得给定日期的所在月的第一天
	 *
	 * @param date
	 *            Date 类型的日期
	 * @return 给定日期的所在月的第一天
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static Date getFirstDayOfMonth(Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.set(5, 1);
		return gc.getTime();
	}

	/**
	 * 取得两个给定日期之差的天数
	 *
	 * @param lowerLimitDate
	 *            Date 类型的前日期
	 * @param upperLimitDate
	 *            Date 类型的后日期
	 * @return 整数类型的两个日期之差的天数
	 * @exception java.lang.IllegalArgumentException
	 *                如果前日期大于后日期，抛出java.lang.IllegalArgumentException异常
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static int getDayInRange(Date lowerLimitDate,
			Date upperLimitDate) {
		long upperTime, lowerTime;
		upperTime = upperLimitDate.getTime();
		lowerTime = lowerLimitDate.getTime();

		if (upperTime < lowerTime) {
			logger.error("param is error!",
					new java.lang.IllegalArgumentException());
		}

		Long result = new Long((upperTime - lowerTime) / (1000 * 60 * 60 * 24));
		return result.intValue();
	}

	/**
	 * 比较两个日期
	 *
	 * @param lowerLimitDate
	 *            给定比较日期1
	 * @param upperLimitDate
	 *            给定比较日期2
	 * @return 如果 给定比较日期1大于给定比较日期2返回true，否则返回false
	 * @exception java.lang.IllegalArgumentException
	 *                如果给定参数为空，抛出java.lang.IllegalArgumentException异常
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static boolean checkOverLimited(Date beginLimitDate,
			Date endLimitDate) {
		if (beginLimitDate == null || endLimitDate == null)
			logger.error("param is error!",
					new java.lang.IllegalArgumentException());

		if (((Date)beginLimitDate).compareTo((Date)endLimitDate) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
     * 将标准日期字符串转换成日期时间对象
     */
    public static Date stringToDateTime(String dateString){   		
    	
    	if(dateString.trim().length() <=10 ){
    		
    		//构建带当前时间的日期字符串
    		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    		String time = df.format(new Date());
    		dateString = dateString.trim() + " " + time;    		 		
    	}   	
    	    	  	
    	return (Date)java.sql.Timestamp.valueOf(dateString.trim()); 
    }
    
    public static String formatfulld(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date != null) {
            return df.format(date);
        } else {
            return "";
        }
    }
	/**
	 * 计算两个 calendar days 之间的天数
	 *
	 * @param begin
	 *            开始日期
	 * @param end
	 *            结束日期
	 * @author zhouxusheng
	 * @return 使用SimpleDateFormat，规定好格式，parser出错即为非法
	 *         使用DateToStr或者StrToDate格式化，返回的永远是大于零的数字
	 */
	public synchronized static int getDaysBetween(Calendar begin, Calendar end) {
		if (begin.after(end)) {
			Calendar swap = begin;
			begin = end;
			end = swap;
		}

		int days = end.get(Calendar.DAY_OF_YEAR)
				- begin.get(Calendar.DAY_OF_YEAR);
		int y2 = end.get(Calendar.YEAR);

		if (begin.get(Calendar.YEAR) != y2) {
			begin = (Calendar) begin.clone();
			do {
				days += begin.getActualMaximum(Calendar.DAY_OF_YEAR);
				begin.add(Calendar.YEAR, 1);
			} while (begin.get(Calendar.YEAR) != y2);
		}
		return days;
	}
	
	/**
	 * 比较两个日期， 相等返回0，stDt>endDt 返回1，stDt<endDt 返回-1
	 * @param stDt
	 * @param endDt
	 * @return
	 */
	public synchronized static int compareDate(Date stDt,Date endDt){
		int i = getDayInRange2(stDt,endDt);
		if(i>0){
			return -1;
		}else if(i<0){
			return 1;
		}else{
			return 0;
		}
	}
	//计算日期的差，可以为负
	private static int getDayInRange2(Date lowerLimitDate,Date upperLimitDate){
		long upperTime, lowerTime;
		upperTime = upperLimitDate.getTime();
		lowerTime = lowerLimitDate.getTime();
		Long result = new Long((upperTime - lowerTime) / (1000 * 60 * 60 * 24));
		return result.intValue();
	}

	/**
	 * 计算给定日期与给定天数的计算结果（N天后，或N天前）
	 *
	 * @param date
	 *            给定的日期类型
	 * @param count
	 *            整数天，如果为负数；代表day天前。如果为正数：代表day天后
	 * @return Date型计算后的日期对象
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static Date modDay(Date date, int day) {
		try {
			Calendar cd = Calendar.getInstance();
			cd.setTime(date);
			cd.add(Calendar.DATE, day);
			return cd.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 计算给定日期与给定小时数的计算结果（N小时后，或N小时前）
	 *
	 * @param date
	 *            给定的日期类型
	 * @param count
	 *            整数小时，如果为负数；代表time小时前。如果为正数：代表time小时后
	 * @return Date型计算后的日期对象
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static Date modHour(Date date, int time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, time);
		return cal.getTime();
	}
	/**
	 *
	* <p>方法名称: formatDate|描述: </p>
	* @param formatDate
	* @param format_str
	* @return
	 */
	public static Date formatDate(Date formatDate,String format_str){
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(format_str);
		try{
			date = format.parse(format.format(formatDate));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return date;
	}
	/**
	 *格式化 日期类型  为字符串
	* <p>方法名称: formatDate|描述: </p>
	* @param formatDate
	* @param format_str
	* @return
	 */
	public static String formatDateToString(Date formatDate,String datePatten){
		SimpleDateFormat df = new SimpleDateFormat(datePatten);
		return df.format(formatDate);
	}

	/**
	 * <p>方法名称: dtuGetCurDatTimStr|描述:获得当前日期字符串格式</p>
	 * @param
	 * @return String
	 */
	public static String dtuGetCurDatTimStr()throws Exception{
		String str="";
		try{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
			Calendar cd=Calendar.getInstance();
			str=sdf.format(cd.getTime());
		}catch(Exception e){
			throw new Exception("【错误：获得当前日期字符串格式服用异常！】");
		}
		return str;
	}

	/**
	 * <p>方法名称: dtuGetDatTimFmt|描述:格式化提供的日期</p>
	 * @param
	 * @return String
	 */
	public static String dtuGetDatTimFmt(Date date){
		String dt="";
		try{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			dt=sdf.format(date);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return dt;
	}

	/**
	 * <p>方法名称: dtuGetDatTimFmt|描述:将字符串转换成日期</p>
	 * @param 2009-06-19
	 * @return Fri Jun 19 00:00:00 CST 2009
	 */
	public static Date parseDatStr2Date(String datStr,String pattern)throws java.text.ParseException{
		if(datStr==null){
			return null;
		}
		pattern=pattern==null||pattern.equals("")?"yyyy-MM-dd":pattern;
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		return sdf.parse(datStr);
	}

	/**
	 * <p>方法名称: getCurrentDayEndDate|描述:获得本日的结束日期(yyyy-MM-dd 23:59:59) </p>
	 * @param endDate
	 * @return
	 */
	public static Date getCurrentDayEndDate(Date endDate){
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try{
			endDate = dateFormat2.parse(dateFormat1.format(endDate)
					+ " 23:59:59");
		}catch (Exception e){
			logger.error("获得本日的结束日期出错:", e);
		}
		return endDate;
	}

	/**
	 * <p>方法名称: getCurrentDayStartDate|描述: 获得本日的开始日期(yyyy-MM-dd 00:00:00)</p>
	 * @param startDate
	 * @return
	 */
	public static Date getCurrentDayStartDate(Date startDate){
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try{
			startDate = dateFormat2.parse(dateFormat1.format(startDate)
					+ " 00:00:00");
		}catch (Exception e){
			logger.error("获得本日的开始日期出错:", e);
		}
		return startDate;
	}

	/**
     * 获取指定月份的最后一天
     * @param theDataStr
     * @return
     */
    public static String LastDateOfMonth(String theDataStr){
    	Date theDate = java.sql.Date.valueOf(theDataStr);
    	Calendar c= new GregorianCalendar();
    	c.setTime(theDate);
    	c.set(Calendar.DAY_OF_MONTH,1);
    	c.add(Calendar.MONTH,1);
    	c.add(Calendar.DAY_OF_MONTH,-1);
    	return (new java.sql.Date(c.getTime().getTime())).toString();
    }
	/**
	 * 获得指定格式的当前日期字符串
	 */
    public static String getDatStrAsStyle(String part)throws Exception{
		String str="";
		if(part==null||part.equals("")){
			part="yyyy-MM-dd";
		}
		try{
			SimpleDateFormat sdf=new SimpleDateFormat(part);
			Calendar cd=Calendar.getInstance();
			str=sdf.format(cd.getTime());
		}catch(Exception e){
			throw new Exception("【错误：获得当前日期字符串格式服用异常！】");
		}
		return str;
	}

    /**
     *
    * <p>方法名称: getCurrDate|描述:获取当前日期 </p>
    * @return
     */
  public static Date getCurrDate(){
      Date date = null;
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      try{
       date = format.parse(format.format(new Date()));
      }catch(Exception e){
       logger.error(e.getMessage(),e);
      }
      return date;

     }
  
  /**
  *
 * <p>方法名称: getCurrDate|描述:获取当前时间 </p>
 * @return
  */
  public static Date getCurrDateTime(){
	   Date date = null;
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   try{
	    date = format.parse(format.format(new Date()));
	   }catch(Exception e){
	    logger.error(e.getMessage(),e);
	   }
	   return date;

  }

  /**
	 * 日期解析
	 *
	 * @param source 日期字符
	 * @param format 解析格式，如果为空，使用系统默认格式解析
	 * @return 日期
	 */
	public static Date parse(String source, String format) {
		if (StringUtil.isBlank(source)) {
			return null;
		}

		DateFormat df = null;
		if (format != null) {
			df = new SimpleDateFormat(format);
		} else {
			df = DateFormat.getDateInstance(DateFormat.DEFAULT);
		}
		try {
			return df.parse(source);
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
			return new Date();
		}
	}
	
	
	/**
	* <p>方法名称: calRange|描述: 计算出票日和到期日之间相差天数所在的范围</p>
	* @param isseDt 出票日
	* @param dueDt 到期日
	* @return
	*/
	public static String calRange(Date isseDt,Date dueDt){
		int i = getDayInRange(isseDt,dueDt);
		if(i <= 90 && i > 0){
			return PublicStaticDefineTab.ONETOTHREEMONTH; // 1-3个月
		}else if( i <= 180 && i >90){
			return PublicStaticDefineTab.THREETOSIXMONTH; // 3-6个月
		}else{
			return PublicStaticDefineTab.MORETHANSIXMONTH; // 6个月以上
		}
		
	}
	
	/**
	* <p>方法名称: calRange|描述: 计算出票日和到期日之间是否超过六个月(和祁永沟通，六个月不按180天算)</p>
	* @param isseDt 出票日
	* @param dueDt 到期日
	* @return  在六个月之内的返回true,否则返回false
	*/
	public static boolean calRangePcds(Date isseDt,Date dueDt){
		Date afterSix = getNextNMonth(isseDt,6);
		
		int a = compareDate(afterSix,dueDt);
		if(a>=0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 计算2个日期之间相差的  以年、月、日为单位，各自计算结果是多少
	 * 比如：2011-02-02 到  2017-03-02
	 *                                以年为单位相差为：6年
	 *                                以月为单位相差为：73个月
	 *                                以日为单位相差为：2220天
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static int dayCompare(Date fromDate,Date toDate){
		Calendar  from  =  Calendar.getInstance();
		from.setTime(fromDate);
		Calendar  to  =  Calendar.getInstance();
		to.setTime(toDate);
		//只要年月
		int fromYear = from.get(Calendar.YEAR);
		int fromMonth = from.get(Calendar.MONTH);

		int toYear = to.get(Calendar.YEAR);
		int toMonth = to.get(Calendar.MONTH);

		int year = toYear  -  fromYear;
		int month = toYear *  12  + toMonth  -  (fromYear  *  12  +  fromMonth);
		int day = (int) ((to.getTimeInMillis()  -  from.getTimeInMillis())  /  (24  *  3600  *  1000));
		return  month;
	}
	/**
	 * 取得给定日期后的某n个年后的日期
	 * @param Date 类型的日期
	 * @return 给定日期的n个年后的日期
	 */
	public synchronized static Date getNextNYear(Date date,int n) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.YEAR, n);
		return gc.getTime();
	}

	/**
	 * 取得给定日期后的某n个月后的日期
	 * @author wanggang
	 * @param date
	 *            Date 类型的日期
	 * @return 给定日期的n个月后的日期
	 * @author hexin@
	 * @since Jun 14, 2008
	 */
	public synchronized static Date getNextNMonth(Date date,int n) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.MONTH, n);
		return gc.getTime();
	}
	
	/**
	 * 取本周的最后一天<br>
	 * <br>
	 * @param currdate 要判断的时间<br>
	 */
	public static String getLastDayofWeek(Calendar currdate){
		 String lastDayofWeek="";
	    DateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
	      Calendar c = Calendar.getInstance();
	      String currdateStr = format.format(currdate.getTime());
	      try {
			c.setTime(format.parse(currdateStr));
		} catch (ParseException e) {
			logger.error(e.getMessage(),e);
		}
		  /**
	     * 判断当前日期是星期几<br>
	     */
	      int dayForWeek = 0;
	      if(c.get(Calendar.DAY_OF_WEEK) == 1){
	       dayForWeek = 7;
	      }else{
	       dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
	      }
			
	      if(7==dayForWeek){
	    	  lastDayofWeek=currdateStr;
	      }else{
	    	  c.set(c.DAY_OF_WEEK, 2);
	          c.add(Calendar.DAY_OF_MONTH, +6);
	          lastDayofWeek = format.format(c.getTime());
	      }
			return lastDayofWeek;
		}
	
	public static String firstDayForWeek(Calendar currdateStr) {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        int dayForWeek = 0;
        if(currdateStr.get(Calendar.DAY_OF_WEEK) == 1){
        	dayForWeek = 7;
        }else{
        	dayForWeek = currdateStr.get(Calendar.DAY_OF_WEEK) - 1;
        }
        if(7==dayForWeek){
        	currdateStr.add(Calendar.DAY_OF_MONTH, -6);
        }else{
        	currdateStr.set(currdateStr.DAY_OF_WEEK, 2);
        }
        String week_strat = df.format(currdateStr.getTime());
        return week_strat;
    }
	
	/**
	 * @北京明润华创科技有限责任公司
	 * @作者：xingyu
	 * @时间：Aug 12, 2011 2011 3:08:56 PM
	 * @param：
	 * @详细描述：dateToCTString将date转换为yyyy年MM月dd日
	 */
	public static String dateToCTString(Date date){
		SimpleDateFormat dateformat=new SimpleDateFormat(DateUtils.ORA_DATES_FORMAT_CT);   
        String ctDate=dateformat.format(date);
        return ctDate;
	}
	 /**
	 * @描述：取得两个日期之间的秒数
	 */
	public static int getSecdBetweenDate(Date begin,Date end){
		long date1 = begin.getTime();
		long date2 = end.getTime();
		int res = Integer.parseInt(String.valueOf(Math.abs((date1-date2)/1000)));
		return res;
	}
	/**
	 * 为日期增加或者减少n天
	 * @param sourceDate
	 * @param n
	 * @return
	 * @author chenzhigang
	 */
	public static Date adjustDateByDay(Date sourceDate, int n ){
		Calendar cd = Calendar.getInstance();
        cd.setTime(sourceDate);
        cd.add(Calendar.DATE,n);
        return cd.getTime();
	}
/**
	 * 取得给定日期所在年的第一天
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfYear(Date date){
		int year = DateUtils.getYear(date);
		Calendar cld = Calendar.getInstance();
		cld.set(year,0,1);
		
		return cld.getTime();
	}
	
	/**
	 * 获取 当前系统 工作日+时间
	 * @return
	 */
	public static Date getWorkDateTime(){
		RunState rs = RunStateServiceFactory.getRunStateService().getSysRunState();
		Date curDate = rs.getCurDate();
		if(curDate != null){
			String d = curDate.toString();
			String t = DateUtils.toString(new Date(),
					DateUtils.ORA_TIME2_FORMAT);
			String dt = d + " " + t;
			return DateUtils.StringToDate(dt, DateUtils.ORA_DATE_TIMES3_FORMAT);
		}
		return new Date();
	}
	/**
	 * 获取系统当前工作日 
	 * @return
	 */
	public static Date getWorkDayDate(){
		
		RunState rs = RunStateServiceFactory.getRunStateService().getSysRunState();
		return rs.getWorkDate();
//		Date date = null;
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		try{
//			date = format.parse(format.format(new Date()));
//		}catch(Exception e){
//			logger.error(e.getMessage(),e);
//		}
//		return date;
	}
	/**
	 * 获取系统当前工作日 yyyyMMdd
	 * @return
	 */
	public static String getWorkDayDate_yyyyMMdd(){
		RunState rs = RunStateServiceFactory.getRunStateService().getSysRunState();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(rs.getWorkDate());
	}
	/**
	 * 获取当前工作日   默认yyyy-MM-dd
	 * @param datePatten 日期格式  
	 * @return
	 */
	public static String getWorkDayDateString(String datePatten){
		RunState rs = RunStateServiceFactory.getRunStateService().getSysRunState();
		if(datePatten==null || datePatten.length()<8){
			datePatten="yyyy-MM-dd";
		}
		SimpleDateFormat df = new SimpleDateFormat(datePatten);
		return df.format(rs.getWorkDate());
	}
	/**
	 * 获得下一个营业日
	 * 
	 * @return 下一个营业日
	 */
	public static Date getNextWorkday() {
		return getDate(getWorkDayDate(), 1);
	}
	/**
	 * 计算某日期之后N天的日期
	 * 
	 * @param theDate
	 * @param days
	 * @return Date
	 */
	public static java.sql.Date getDate(Date theDate, int days) {
		Calendar c = new GregorianCalendar();
		c.setTime(theDate);
		c.add(GregorianCalendar.DATE, days);
		return new java.sql.Date(c.getTime().getTime());
	}
	/**
	 * 以指定时间格式返回指定时间
	 * 
	 * @param dt
	 *            指定时间
	 * @param format
	 *            时间格式，如yyyyMMdd
	 * @return 返回指定格式的时间
	 */
	public static String getTime(Date dt, String format) {
		SimpleDateFormat st = new SimpleDateFormat(format);
		return st.format(dt);
	}
	
	/**
	 * @Title isBetweenTimes
	 * @author wss
	 * @date 2021-6-4
	 * @Description 判断是否在某个时间段内
	 * @param strTime 开始时间
	 * @param dueTime 结束时间
	 * @param tiemType 时间类型
	 * @return boolean
	 */
	public static boolean isBetweenTimes(String strTime, String dueTime,String tiemType) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat(tiemType);
	    Date now = df.parse(df.format(new Date()));
	    Date begin = df.parse(strTime);
	    Date end = df.parse(dueTime);
	    Calendar nowTime = Calendar.getInstance();
	    nowTime.setTime(now);
	    Calendar beginTime = Calendar.getInstance();
	    beginTime.setTime(begin);
	    Calendar endTime = Calendar.getInstance();
	    endTime.setTime(end);
	    if (nowTime.before(endTime) && nowTime.after(beginTime)) {
	    	return true;
	    }else{
	    	return false;
	    }
	}
	
	/**
	 * 判断 checkDate 是否在 starDate 与 endDate 之间，前后均包含，
	 * @param starDate
	 * @param checkDate
	 * @param endDate
	 * @return
	 * @author Ju Nana
	 * @throws ParseException 
	 * @date 2021-7-21下午6:24:54
	 */
	public static boolean isBetweenTowDay(Date starDate,Date checkDate,Date endDate) throws ParseException{
		
		//时间格式化，防止带有时间戳的情况
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String starDateStr = sdf.format(starDate);
		starDate =  sdf.parse(starDateStr);
		
		String checkDateStr = sdf.format(checkDate);
		checkDate =  sdf.parse(checkDateStr);
		
		String endDateStr = sdf.format(endDate);
		endDate =  sdf.parse(endDateStr);

		
		int startCheck = DateUtils.compareDate(checkDate,starDate);
		int endCheck = DateUtils.compareDate(endDate,checkDate);
		
		if(startCheck>=0 && endCheck>=0){//开始日期<=检查日期<=结束日期
			return true;
		}
		
		return false;
		
		
	}
	
	/**
	 * 获取两个时间之间月数  "yyyymmdd"格式
	 * 
	 * **/
	public static int getMonth(String beginDate,String endDate){
		int i = 0;
		if(Integer.parseInt(endDate.substring(6, 7)) - Integer.parseInt(beginDate.substring(6, 7)) > 0){
			i = 1;
		};
		
		return (Integer.parseInt(endDate.substring(0, 4)) - Integer.parseInt(beginDate.substring(0, 4))) * 12 
				+ (Integer.parseInt(endDate.substring(4, 6)) - Integer.parseInt(beginDate.substring(4, 6))) 
				+ i;
	}
	
	/**
	 * 获取两个时间之间月数  "yyyymmdd"格式  期限
	 * 
	 * **/
	public static Integer getMonth1(String beginDate,String endDate){
		System.out.println(beginDate + "========="+endDate);
		int i = 0;
		if(Integer.parseInt(endDate.substring(endDate.length() - 2)) - Integer.parseInt(beginDate.substring(beginDate.length() - 2)) >= 0){
			i = 1;
		}
		
		int year = Integer.parseInt(endDate.substring(0, 4)) - Integer.parseInt(beginDate.substring(0, 4));
		int month = Integer.parseInt(endDate.substring(4, 6)) - Integer.parseInt(beginDate.substring(4, 6));
		
		int ri = year*12 + month;
		
		if(ri >= 12){	//	年月一致
			return 12;
		}
		
		if(ri <= 0){
			return 1;
		}
		
		return ri + i;
	}
}
