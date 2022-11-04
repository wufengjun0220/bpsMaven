package com.mingtech.application.ecds.common.domain;

import java.util.Date;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: yufei
 * @日期: Jun 2, 2009 11:34:40 AM
 * @描述: [HolidayDto]节假日对象
 */
public class HolidayDto implements java.io.Serializable{

	/** serialVersionUID*/
	private static final long serialVersionUID = -5268616447853814879L;
	// Fields
	private String pkIxBoHolidayId;// 主键ID
	private int SIfHoliday; // 是否是节假日 0-否 1-是
	private String SDay; // 星期几
	private Date DDate; // 日期
	private String dataSource;//节假日来源  1手动录入   2票交所录入
	// Constructors
	/** default constructor */
	public HolidayDto(){
	}

	/** full constructor */
	public HolidayDto(int SIfHoliday, String SDay, Date DDate){
		this.SIfHoliday = SIfHoliday;
		this.SDay = SDay;
		this.DDate = DDate;
	}

	/**
	 * <p>方法名称: getPkIxBoHolidayId|描述: </p>
	 * @return
	 */
	public String getPkIxBoHolidayId(){
		return this.pkIxBoHolidayId;
	}

	/**
	 * <p>方法名称: setPkIxBoHolidayId|描述: </p>
	 * @param pkIxBoHolidayId
	 */
	public void setPkIxBoHolidayId(String pkIxBoHolidayId){
		this.pkIxBoHolidayId = pkIxBoHolidayId;
	}

	/**
	 * <p>方法名称: getSIfHoliday|描述:获取是否是节假日 </p>
	 * @return
	 */
	public int getSIfHoliday(){
		return this.SIfHoliday;
	}

	/**
	 * <p>方法名称: setSIfHoliday|描述: set是否是节假日</p>
	 * @param SIfHoliday
	 */
	public void setSIfHoliday(int SIfHoliday){
		this.SIfHoliday = SIfHoliday;
	}

	/**
	 * <p>方法名称: getSDay|描述: 获取星期几</p>
	 * @return
	 */
	public String getSDay(){
		return this.SDay;
	}

	/**
	 * <p>方法名称: setSDay|描述: set星期几</p>
	 * @param SDay
	 */
	public void setSDay(String SDay){
		this.SDay = SDay;
	}

	/**
	 * <p>方法名称: getDDate|描述:获取日期 </p>
	 * @return
	 */
	public Date getDDate(){
		return this.DDate;
	}

	/**
	 * <p>方法名称: setDDate|描述:set日期 </p>
	 * @param DDate
	 */
	public void setDDate(Date DDate){
		this.DDate = DDate;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
}