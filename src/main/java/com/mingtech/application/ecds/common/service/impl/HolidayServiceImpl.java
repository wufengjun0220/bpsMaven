package com.mingtech.application.ecds.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.mingtech.application.cache.HolidayCache;
import com.mingtech.application.ecds.common.domain.HolidayDto;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.ecds.common.service.HolidayService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HolidayServiceImpl extends GenericServiceImpl implements
		HolidayService{

	/**
	 * 判断给定日期天是否为假期
	 * @param date 查询holiday表，如存在该日期，则返回true
	 */
	public boolean judgeDateHoliday(Date date)  {
		List paras = new ArrayList();
		String hql = "FROM HolidayDto AS holidayDto WHERE holidayDto.DDate = ?";
		paras.add(date);
		List list = this.find(hql, paras);
		if(list!=null && list.size()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断给定日期天是否为假期并返回顺延天数
	 * @param date 查询holiday表，如存在该日期，则返回true
	 * @throws Exception 
	 */
	public int getPostponedDay(Date date) throws Exception  {
		Date date1 = this.getNextUnHolidayDate(date);
		if(date.compareTo(date1)==0){
			return 0;
		}else{
			return DateUtils.getDayInRange(date, date1);
		}
	}
	
	/**
	 * 根据日期 查询 节假日对象
	 * @param date
	 * @return
	 */
	public HolidayDto queryHolidayDtoByDate(Date date){
		List paras = new ArrayList();
		String hql = "FROM HolidayDto AS holidayDto WHERE holidayDto.DDate = ?";
		paras.add(date);
		List list = this.find(hql, paras);
		if(list!=null && list.size()>0){
			return (HolidayDto)list.get(0);
		}
		return null;
	}
	/**
	 * 取得下一非假期的日期，如当天为非假期，则取当天
	 */
	public Date getNextUnHolidayDate(Date date) throws Exception{
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		String curDate =DateUtils.toString(date, "yyyy-MM-dd");
		while(HolidayCache.getHolidayList().contains(curDate)){//{原始方法this.judgeDateHoliday(date)}{6月19日更新为于飞方法，减少数据库访问量}
			gc.add(GregorianCalendar.DAY_OF_MONTH, 1);
			date = gc.getTime();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			date = format.parse(format.format(date));
			curDate =DateUtils.toString(date, "yyyy-MM-dd");
		}
		return date;
	}


	public Class getEntityClass(){
		return HolidayServiceImpl.class;
	}

	public String getEntityName(){
		return HolidayServiceImpl.class.getName();
	}
	public List getAllHolidayList() throws Exception{
		StringBuffer hql = new StringBuffer("FROM HolidayDto AS holidayDto where holidayDto.SIfHoliday = 1");
		List keyList = new ArrayList();
		List valueList = new ArrayList();
		hql.append(" and holidayDto.DDate  >= :dateStart ");
		keyList.add("dateStart");
		String strDate = DateUtils.toString(new Date(), DateUtils.ORA_DATES_FORMAT);
		valueList.add(DateUtils.StringToDate(strDate, DateUtils.ORA_DATES_FORMAT));
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(hql.toString(), keyArray, valueList.toArray());
		
		return list;
	}

	@Override
	public List getAllHolidayList(QueryBean bean, Page page) throws Exception {
		StringBuffer sb = new StringBuffer(" select holidayDto FROM HolidayDto AS holidayDto WHERE 1=1 ");
		List keyList = new ArrayList();
		List valueList = new ArrayList();
		if(bean != null){
			if (bean.getStartDate() != null) {
				sb.append(" and holidayDto.DDate  >= :dateStart ");
				keyList.add("dateStart");
				valueList.add(bean.getStartDate());
			}
			if (bean.getEndDate() != null) {
				sb.append(" and holidayDto.DDate  <= :dateEnd ");
				keyList.add("dateEnd");
				valueList.add(bean.getEndDate());
			}
			sb.append(" and holidayDto.SIfHoliday  = :num ");
			keyList.add("num");
			valueList.add(bean.getRemindNum());
		}
		
		sb.append(" order by DDate desc ");
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List result = this.find(sb.toString(), keyArray, valueList.toArray(),page);
		return result;
	}
	public void txSaveHoliday(HolidayDto dto) throws Exception {
		this.txStore(dto);
	}
}
