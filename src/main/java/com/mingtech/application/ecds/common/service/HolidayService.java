package com.mingtech.application.ecds.common.service;

import java.util.Date;
import java.util.List;

import com.mingtech.application.ecds.common.domain.HolidayDto;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 假期维护类
 * 方法：1判断一天是否假期
 * 		2取得某一天的下一个非假期天，如当天不是假期，则取当日
 *
 * */
public interface HolidayService extends GenericService {
	/**
	 * 判断给定日期天是否为假期
	 * @param date 查询holiday表，如存在该日期，则返回true
	 * @return boolean
	 */
	public boolean judgeDateHoliday(Date date) ;
	/**
	 * 根据日期 查询 节假日对象
	 * @param date
	 * @return
	 */
	public HolidayDto queryHolidayDtoByDate(Date date);

	/**
	 * 取得下一非假期的日期，如当天为非假期，则取当天
	 * @param date
	 * @return date 返回非节假日的日期
	 */
	public Date getNextUnHolidayDate(Date date) throws Exception;


	/**
	 * 查询所有的节假日数据
	* <p>方法名称: getAllHolidayList|描述: </p>
	* @return
	* @throws Exception
	 */
	public List getAllHolidayList()throws Exception;
	/**
	 * 根据查询条件查询所有的节假日数据
	 * <p>
	 * 方法名称: getAllHolidayList|描述:
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 * @autore wufengjun
	 * @date 2019-11-13
	 */
	public List getAllHolidayList(QueryBean bean, Page page) throws Exception;
	/**
	 * 保存节假日
	 * 
	 * @throws Exception
	 * @autore wufengjun
	 * @date 2019-11-13
	 */
	public void txSaveHoliday(HolidayDto dto) throws Exception;
	/**
	 * @Title getPostponedDay
	 * @author wss
	 * @date 2021-6-26
	 * @Description 根据给定日期获取顺延天数
	 * @return Object
	 * @throws Exception 
	 */
	public int getPostponedDay(Date plDueDt) throws Exception;

}
