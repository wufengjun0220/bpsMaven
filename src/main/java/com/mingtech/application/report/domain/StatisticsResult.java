package com.mingtech.application.report.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-11 下午01:55:01
 * @描述: [StatisticsResult]统计结果实体
 * @hibernate.class table="R_StatisticsResult"
 * @hibernate.cache usage="read-write"
 */
public class StatisticsResult{

	private String id; // 主键ID
	private String statisticsType; // 统计业务类型     承兑/贴现/转贴现
	private Date statisticsTime; // 统计执行时间
	private String statisticsFrequency; // 统计方式      月度/每周/每天/指定时间
	private String statisticsDim; // 统计维度 ----???
	private int statisticsNum = 0; // 统计结果数量
	private BigDecimal statisticsAmount = new BigDecimal(0); // 统计涉及交易总金额
	private Date statisticsStartDate; // 统计开始日期
	private Date statisticsEndDate; // 统计结束日期
	private List searchResultList = new ArrayList() ; // 查询结果实体List
	private Map  analysisResultMap = new HashMap(); // 分析结果实体Map
	private String deptId; // 机构ID
	
	public StatisticsResult(){
		
	}

	

	/**
	* <p>方法名称: getId|描述: 主键ID</p>
	* @return
	* @hibernate.id generator-class="uuid" type="string" length="40" column="id"
	*/
	public String getId(){
		return id;
	}
	/**
	* <p>方法名称: setId|描述: 主键ID</p>
	* @param id
	*/
	public void setId(String id){
		this.id = id;
	}

	/**
	* <p>方法名称: getStatisticsType|描述: 统计业务类型</p>
	* @return
	* @hibernate.property type="string" column="sr_statisticsType" length="100"
	*/
	public String getStatisticsType(){
		return statisticsType;
	}
	/**
	* <p>方法名称: setStatisticsType|描述: 统计业务类型</p>
	* @param statisticsType
	*/
	public void setStatisticsType(String statisticsType){
		this.statisticsType = statisticsType;
	}

	
	/**
	* <p>方法名称: getStatisticsTime|描述: 统计执行时间</p>
	* @return
	* @hibernate.property type="timestamp" column="sr_statisticsTime"
	*/
	public Date getStatisticsTime(){
		return statisticsTime;
	}
	/**
	* <p>方法名称: setStatisticsTime|描述: 统计执行时间</p>
	* @param statisticsTime
	*/
	public void setStatisticsTime(Date statisticsTime){
		this.statisticsTime = statisticsTime;
	}

	
	/**
	* <p>方法名称: getStatisticsFrequency|描述: 统计方式</p>
	* @return
	* @hibernate.property type="string" column="sr_statisticsFrequency" length="100"
	*/
	public String getStatisticsFrequency(){
		return statisticsFrequency;
	}
	/**
	* <p>方法名称: setStatisticsFrequency|描述: 统计方式</p>
	* @param statisticsFrequency
	*/
	public void setStatisticsFrequency(String statisticsFrequency){
		this.statisticsFrequency = statisticsFrequency;
	}


	/**
	* <p>方法名称: getStatisticsDim|描述: 统计维度</p>
	* @return
	* @hibernate.property type="string" column="sr_statisticsDim" length="300"
	*/
	public String getStatisticsDim(){
		return statisticsDim;
	}
	/**
	* <p>方法名称: setStatisticsDim|描述: 统计维度</p>
	* @param statisticsDim
	*/
	public void setStatisticsDim(String statisticsDim){
		this.statisticsDim = statisticsDim;
	}

	
	/**
	* <p>方法名称: getStatisticsNum|描述: 统计结果数量</p>
	* @return
	* @hibernate.property type="integer" column="sr_statisticsNum"
	*/
	public int getStatisticsNum(){
		return statisticsNum;
	}
	/**
	* <p>方法名称: setStatisticsNum|描述: 统计结果数量</p>
	* @param statisticsNum
	*/
	public void setStatisticsNum(int statisticsNum){
		this.statisticsNum = statisticsNum;
	}

	
	/**
	* <p>方法名称: getStatisticsAmount|描述: 统计涉及交易总金额</p>
	* @return
	* @hibernate.property type="big_decimal" column="sr_statisticsAmount" precision="20" scale="2"
	*/
	public BigDecimal getStatisticsAmount(){
		return statisticsAmount;
	}
	/**
	* <p>方法名称: setStatisticsAmount|描述: 统计涉及交易总金额</p>
	* @param statisticsAmount
	*/
	public void setStatisticsAmount(BigDecimal statisticsAmount){
		this.statisticsAmount = statisticsAmount;
	}


	/**
	* <p>方法名称: getStatisticsStartDate|描述: 统计开始日期</p>
	* @return
	* @hibernate.property type="date" column="sr_statisticsStartDate"
	*/
	public Date getStatisticsStartDate(){
		return statisticsStartDate;
	}
	/**
	* <p>方法名称: setStatisticsStartDate|描述: 统计开始日期</p>
	* @param statisticsStartDate
	*/
	public void setStatisticsStartDate(Date statisticsStartDate){
		this.statisticsStartDate = statisticsStartDate;
	}


	/**
	* <p>方法名称: getStatisticsEndDate|描述: 统计结束日期</p>
	* @return
	* @hibernate.property type="date" column="sr_statisticsEndDate"
	*/
	public Date getStatisticsEndDate(){
		return statisticsEndDate;
	}
	/**
	* <p>方法名称: setStatisticsEndDate|描述: 统计结束日期</p>
	* @param statisticsEndDate
	*/
	public void setStatisticsEndDate(Date statisticsEndDate){
		this.statisticsEndDate = statisticsEndDate;
	}


	/**
	* <p>方法名称: getSearchResultList|描述: 查询结果实体List</p>
	* @return
	*/
	public List getSearchResultList(){
		return searchResultList;
	}
	/**
	* <p>方法名称: setSearchResultList|描述: 查询结果实体List</p>
	* @param searchResultList
	*/
	public void setSearchResultList(List searchResultList){
		this.searchResultList = searchResultList;
	}


	/**
	* <p>方法名称: getAnalysisResultMap|描述: 分析结果实体Map</p>
	* @return
	*/
	public Map getAnalysisResultMap(){
		return analysisResultMap;
	}
	/**
	* <p>方法名称: setAnalysisResultMap|描述: 分析结果实体Map</p>
	* @param analysisResultMap
	*/
	public void setAnalysisResultMap(Map analysisResultMap){
		this.analysisResultMap = analysisResultMap;
	}


	/**
	* <p>方法名称: getDeptId|描述: 机构ID</p>
	* @return
	* @hibernate.property type="string" column="sr_deptId"
	*/
	public String getDeptId(){
		return deptId;
	}
	/**
	* <p>方法名称: setDeptId|描述: 机构ID</p>
	* @param deptId
	*/
	public void setDeptId(String deptId){
		this.deptId = deptId;
	}

	

}
