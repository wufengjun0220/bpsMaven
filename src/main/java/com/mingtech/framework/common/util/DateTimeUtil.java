package com.mingtech.framework.common.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.runmanage.service.RunStateServiceFactory;

/**
 * @author Admin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class DateTimeUtil {
	private static final Logger logger = Logger.getLogger(DateTimeUtil.class);

	private static final String[] parsePatterns = new String[]{"yyyy-MM-dd","yyyy/MM/dd","yyyyMMdd"};
//	private static Date workDay=null;
	
	public static Date parse(String s)  {
		if (s == null || "".equals(s) || "null".equalsIgnoreCase(s))   
			return null;
		Date date = null;
		try {
			 date = DateUtils.parseDate(org.springframework.util.StringUtils.trimAllWhitespace(s),
					parsePatterns);
			return date;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return date;
	}
	
	public static Date getNowDateTime(){
		Calendar cd=Calendar.getInstance();
		return cd.getTime();
	}
	
	public static String toDateString(Date date) {
		if(date==null){
			return null;
		}
		return get_YYYY_MM_DD_Date(date);
	}

	public static String formatDate(Date date, String pattern) {
		if(date==null||pattern==null){
			return null;
		}
		return DateFormatUtils.format(date, pattern);
	}
	
	public static String toDateTimeString(Date date) {
		if (date == null)  return null;
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String toTimeString(Date date) {
		if (date == null)  return null;
		return formatDate(date, "HH:mm:ss");
	}

//	public static String convertToyyyyMMdd(String date) {
//		return StringUtils.replaceEach(date, new String[]{"-", "/", " "}, new String[]{"","",""});
//	}
	
	/**
	 * 获得当前日期的格式  yyyyMMdd
	 */
	public static String get_YYYYMMDD_Date(){
		return formatDate(Calendar.getInstance().getTime(), "yyyyMMdd");
	}
	
	/**
	 * 将字符日期yyyy-MM-dd转换为yyyyMMdd.
	*/
	public static String get_YYYYMMDD_Date(Date date){
		if(date==null){
			return null;
		}
		return formatDate(date, "yyyyMMdd");
	}
	
	/**
	 * 获得当前日期的格式  yyyy-MM-dd
	 */
	public static String get_YYYY_MM_DD_Date(){
		return formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd");
	}
	
	/**
	 * 日期格式转换
	 * @param date
	 * @return	返回格式为"yyyy-MM-dd"的字串
	 * @throws Exception
	 */
	public static String get_YYYY_MM_DD_Date(Date date){
		if(date==null){
			return null;
		}
		return formatDate(date, "yyyy-MM-dd");
	}
	
	/**
	 * 获得制定日期的年，月，日，的数组形式
	 */
	public static String[] getYear_Month_Day_Date(Date date){
		if(date==null){
			return null;
		}
		String [] stDate = null;
		try {
			stDate = new SimpleDateFormat("yyyy-MM-dd").format(date).split("-");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return stDate;
	}
	
	/*
	 * 获得当前时间格式的字符串
	 */
	public static String get_hhmmss_time(){
		return formatDate(Calendar.getInstance().getTime(), "HH:mm:ss");
	}
	

	/**
	 * 计算某日期之后N天的日期
	 * 
	 * @param theDateStr
	 * @param days
	 * @return String
	 */
	public static String getDate(String theDateStr, int days) {
		Date theDate = java.sql.Date.valueOf(theDateStr);
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(theDate);
		c.add(GregorianCalendar.DATE, days);
		java.sql.Date d = new java.sql.Date(c.getTime().getTime());
		return d.toString();
	}
	

	/**
	 * 计算一旬的头一天
	 * @param theDate
	 * @param days
	 * @return
	 */
     public static java.sql.Date getDayOfPerMonth(String theDataStr){
     	Date theDate = java.sql.Date.valueOf(theDataStr);
     	Calendar c=  GregorianCalendar.getInstance();
     	c.setTime(theDate);
     	int day=c.get(Calendar.DAY_OF_MONTH);
     	if(day<=10){
     		c.set(Calendar.DAY_OF_MONTH,1);
     	}else if(day>10&&day<=20){
     		c.set(Calendar.DAY_OF_MONTH,11);
     	}else{
     		c.set(Calendar.DAY_OF_MONTH,21);
     	}
     	c.add(Calendar.DAY_OF_MONTH,-1);
     	
     	
     	return new java.sql.Date(c.getTime().getTime());
     }
     /**
      * 判断是否为该月最后一天
      * 当前日期增加一天，如果月份发生了 变化，则为最后一天
      * @param theDate
      * @param days
      * @return
     * @throws ParseException 
      */
     public static boolean isLastDayOfMonth(final String theDataStr) {
     	Date theDate = parse(theDataStr);
     	Calendar c= GregorianCalendar.getInstance();
     	c.setTime(theDate);
     	int beforeMonth = c.get(Calendar.MONTH);
     	c.add(Calendar.DAY_OF_MONTH, 1);
     	int afterMonth=c.get(Calendar.MONTH);
     	if(beforeMonth==afterMonth){//当前日期增加一天，如果月份发生了 变化，则为最后一天
     		return false;
     	}else{
     		return true;
     	}
     }

     /**
      * 获取指定月份的最后一天
      * @param theDataStr
      * @return
      */
     public static Date lastDateOfMonth(String theDataStr){
     	Date theDate = java.sql.Date.valueOf(theDataStr);
     	Calendar c=  GregorianCalendar.getInstance();
     	c.setTime(theDate);
     	c.add(Calendar.MONTH,1);
     	c.set(Calendar.DAY_OF_MONTH,1);
     	c.add(Calendar.DAY_OF_MONTH,-1);
     	return  c.getTime();
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
	 * 计算某日期之后N的日期
	 * 
	 * @param theDate
	 * @param field，如GregorianCalendar.DATE,GregorianCalendar.MONTH
	 * @param amount 数目
	 * @return Date
	 */
	public static java.sql.Date getDate(Date theDate, int field, int amount) {
		Calendar c = new GregorianCalendar();
		c.setTime(theDate);
		c.add(field, amount);
		return new java.sql.Date(c.getTime().getTime());
	}
	
    //获得两个日期(字符串)之间的天数(不算头不算尾)
	public static long getDiffDay(String begin_dt,String end_dt){
		Date end = java.sql.Date.valueOf(end_dt);
		Date begin = java.sql.Date.valueOf(begin_dt);
		return DateTimeUtil.getDaysBetween(begin,end)-1;
	}
	
    //获得两个日期(字符串)之间的天数(算头不算尾)
	public static long getDiffDays(String begin_dt,String end_dt){
		Date end = java.sql.Date.valueOf(end_dt);
		Date begin = java.sql.Date.valueOf(begin_dt);
		return DateTimeUtil.getDaysBetween(begin,end);
	}	
	
	//获得两个日期(字符串)之间的天数(算头算尾)
	public static long getDiffDaysBeginAndEnd(String begin_dt,String end_dt){
		Date end = java.sql.Date.valueOf(end_dt);
		Date begin = java.sql.Date.valueOf(begin_dt);
		return DateTimeUtil.getDaysBetween(begin,end)+1;
	}
	
	//获得两个日期(字符串)之间的天数(算头不算尾)【不取绝对值】
	public static long getDiffDaysNotAbs(String begin_dt,String end_dt){
		Date end = java.sql.Date.valueOf(end_dt);
		Date begin = java.sql.Date.valueOf(begin_dt);
		return DateTimeUtil.getDaysBetweenNotAbs(begin,end);
	}

	/**
	 * 计算两日期之间的天数
	 * 
	 * @param start
	 * @param end
	 * @return int
	 */
	public static long getDaysBetween(final Date start, final Date end) {
		if(start==null || end==null)return 0;
		Date startTemp = (Date)start.clone();
		Date endTemp = (Date)end.clone();
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(startTemp);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(endTemp);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);
		
		long days = (cal.getTimeInMillis()-calEnd.getTimeInMillis())/(24L*60L*60L*1000L);
		days = Math.abs(days);
		
		return days;
	}
	
	/**
	 * 计算两日期之间的天数（不取绝对值）
	 * 
	 * @param start
	 * @param end
	 * @return int
	 */
	public static long getDaysBetweenNotAbs(final Date start, final Date end) {
		if(start==null || end==null)return 0;
		Date startTemp = (Date)start.clone();
		Date endTemp = (Date)end.clone();
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(startTemp);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		GregorianCalendar calEnd = new GregorianCalendar();
		calEnd.setTime(endTemp);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);
		
		long days = (cal.getTimeInMillis()-calEnd.getTimeInMillis())/(24L*60L*60L*1000L);
		return days;
	}

	
	/**
	 * 以指定时间格式返回指定时间
	 * 
	 * @param dt 指定时间
	 * @param format 时间格式，如yyyyMMdd
	 * @return 返回指定格式的时间
	 */
	public static String getTime(Date dt, String format) {
	    if(dt==null)
	        return "";
		SimpleDateFormat st = new SimpleDateFormat(format);
		return st.format(dt);
	}

	/**
	 * 日期解析
	 * 
	 * @param source 日期字符
	 * @param format 解析格式，如果为空，使用系统默认格式解析
	 * @return 日期
	 */
	public static Date parse(String source, String format) {
		if (format == null || format.length() == 0){
			format = "yyyy-MM-dd";
		}
		Date date = null;
		try {
			date = DateUtils.parseDate(source, new String[] { format });
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}
		return date;
	}

	/**
	 * 获得当前营业日
	 * 
	 * @return 当前营业日
	 */
	public static Date getWorkday() {
		return RunStateServiceFactory.getRunStateService().getSysRunState().getWorkDate();
	}

	/**
	 * 切日时调用刷新营业日
	 */
	public static void flushWorkDay(){
		//workDay=BusiDateFactory.getBusiDateService().getBusiDate().getWorkday();
	} 
	
	/**
	 * 获得当前营业日，格式yyyyMMdd
	 * 
	 * @return 当前营业日
	 */
	public static String getWorkday_YYYYMMDD() {
		return getTime(getWorkday(), "yyyyMMdd");
		
	}


	/**
	 * 获得下一个营业日
	 * 
	 * @return 下一个营业日
	 */
	public static Date getNextWorkday() {
		return getDate(getWorkday(), 1);
	}

	/**
	 * 获得下一个营业日，格式yyyyMMdd
	 * 
	 * @return 下一个营业日
	 */
	public static String getNextWorkday_YYYYMMDD() {
		return getTime(getNextWorkday(), "yyyyMMdd");
	}

	/**
	 * 获得下一个营业日，格式yyyy-MM-dd
	 * 
	 * @return 下一个营业日
	 */
	public static String getNextWorkday_YYYY_MM_DD() {
		return getTime(getNextWorkday(), "yyyy-MM-dd");
	}
	/**
	 * 比较2个日期的大小，如果d1在d2之后，则返回false，否则返回true
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean compartdate(Date d1, Date d2) {
		Date formatDate1 = DateUtils.round(d1, Calendar.DATE);//取到日
		Date formatDate2 = DateUtils.round(d2, Calendar.DATE);
		
		if (formatDate1.after(formatDate2)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 比较2个日期的大小，如果d1在d2之后，则返回false，否则返回true
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean compartdate(String d1, String d2) {	
		return compartdate(parse(d1),parse(d2));
	}
	
	/**
	 * 自定义方法
	 * 可解决不同的时区 格式转换错误的问题 --xiaozejun 2009.12.08
	 */
	public static Date parse3(String s) throws Exception {
		try {
			if (s == null)
				return null;
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			TimeZone zone = TimeZone.getTimeZone("GMT+8");
			f.setTimeZone(zone);
			return f.parse(s);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	public static Date parseYYYYMMDD(String date) throws Exception{
		try {
			if (date == null)
				return null;
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
			TimeZone zone = TimeZone.getTimeZone("GMT+8");
			f.setTimeZone(zone);
			return f.parse(date);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	/***
	 * 将当前营业日整合成：当前营业日+HH:mm:ss.SSS格式
	 * @param workday:当前营业日
	 * @return
	 * @throws ParseException 
	 */
	public static Date  workday_append_time(String workday){
		if("".equals(workday) || workday==null){
			return null;
		}
		String current_time=DateTimeUtil.getTime(new Date(),"HH:mm:ss.SSS");
		StringBuffer sbuffer=new StringBuffer(workday);
		sbuffer.append(" ");
		sbuffer.append(current_time);
		return parse(sbuffer.toString(),"yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	public static Date getDateWithMonths(Date date , int month){
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		c.add(GregorianCalendar.MONTH, month);//month如果是负数,那么就是计算之前几个月.
		return c.getTime();
	}
	
	
	/**
	 * 返回两个日期中的最大的一个
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static Date returnMaxDate(Date d1, Date d2) {
		if (compartdate(d1, d2)) {
			return d2;
		} else {
			return d1;
		}
	}
	/***
	 * 获取日期的时分秒毫秒
	 * @param date
	 * @return
	 */
	public static String toDateMillTimeString(Date date) {
		if (date == null) {
			return null;
		}
		return formatDate(date, "yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	/***
	 * 获取日期的时分秒毫秒(17位数字)
	 * @param date
	 * @return
	 */
	public static String toDateMillTimeString17(Date date) {
		if (date == null) {
			return null;
		}
		return formatDate(date, "yyyyMMddHHmmssSSS");
	}
	
	public static Date clone(Date dt){
		if(dt == null) return null;
		return (Date) dt.clone();
	}
	/***
	 * 将当前营业日整合成：当前营业日+HH:mm:ss.SSS格式
	 * @return
	 * @throws ParseException 
	 */
	public static Date workday_append_time(){
		String workday = getTime(getWorkday(), "yyyy-MM-dd");
		String current_time=DateTimeUtil.getTime(new Date(),"HH:mm:ss.SSS");
		StringBuffer sbuffer=new StringBuffer(workday);
		sbuffer.append(" ");
		sbuffer.append(current_time);
		return parse(sbuffer.toString(),"yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	public static String getWorktimeAsyyyyMMddHHmmss(){
		String workday = getTime(getWorkday(), "yyyyMMdd");
		String current_time=getTime(new Date(),"HHmmss");
		
		return workday+current_time;
	}
	
	//计算后一天
	public static Date getUtilDate(Date theDate, int days) {
		Calendar c = new GregorianCalendar();
		c.setTime(theDate);
		c.add(GregorianCalendar.DATE, days);
		return new Date(c.getTime().getTime());
	}
	
	/**
	 * 获得当前营业日，格式yyyy-MM-dd
	 * 
	 * @return 当前营业日
	 */
	public static String getWorkday_YYYY_MM_DD() {
		return getTime(getWorkday(), "yyyy-MM-dd");
	}
	
	/**
	 * 判断日期是否有效
	 * @param dateStr
	 * @return true 有效 false 无效
	 */
	public static boolean isRightFulDate(String dateStr){
	    boolean flag=false;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    try {
            Date tmp = sdf.parse(dateStr);
            flag=dateStr.equals(sdf.format(tmp))?true:false;
        } catch (ParseException e) {
            flag=false;
        }
        return flag;
	}
	
	/**
	 * 获取上个月最后一天
	 * @author Ju Nana
	 * @return
	 * @date 2019-7-19上午8:56:25
	 */
	public static Date getLastDayOfLastMonth(){
		
		Calendar calendar = Calendar.getInstance();  
    	int month = calendar.get(Calendar.MONTH);
    	calendar.set(Calendar.MONTH, month-1);//拨回上个月
    	calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));  //最后一天
    	
    	Date lastDayOfLastMonth = calendar.getTime();
    	
		return lastDayOfLastMonth;  
	}
	
	/**
	 * 获取去年最后一天
	 * @author Ju Nana
	 * @return
	 * @date 2019-7-19上午9:06:40
	 */
	public static Date getLastDayOfLastYear(){

	     Calendar cal =Calendar.getInstance();
	     cal.setTime(new Date());
	     cal.add(Calendar.YEAR, -1);//拨回去年
	     cal.set(Calendar.DAY_OF_YEAR,cal.getActualMaximum(Calendar.DAY_OF_YEAR));//最后一天
	      
	     Date lastDayOfLastYear = cal.getTime();
      
		 return lastDayOfLastYear;
	      
	}
	
	/**
	 * 获取去年最后一天
	 * @author Ju Nana
	 * @return
	 * @date 2019-8-7上午9:06:40
	 */
	public static Date getTodayNextYear(){
		Date today = new Date();//当前日期
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);//增加一年
		Date todayNextYear = cal.getTime();
		return todayNextYear;
		
	}
	
	/**
	 * 判断两个日期是否超过N年,结束日期endDate比开始日期startDate大numYear年，返回true
	 * @param startDate
	 * @param endDate
	 * @param numYear  年数
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-27上午9:32:32
	 */
	public static Boolean isMoreThanNYear(Date startDate,Date endDate,int numYear) {
		
		Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.YEAR, numYear);//增加年数
        
		Date afterNYearDate = c.getTime();//增加 numYear 年后的时间戳
		
		if(afterNYearDate.getTime()<endDate.getTime()){
			return true;
		}
		
		return false;		
	 }
	 

}