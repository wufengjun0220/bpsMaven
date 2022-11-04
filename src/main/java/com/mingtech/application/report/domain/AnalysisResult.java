package com.mingtech.application.report.domain;

import java.math.BigDecimal;
import com.mingtech.framework.common.util.UUID;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-11 下午01:58:08
 * @描述: [AnalysisResult]分析结果实体
 * @hibernate.class table="R_AnalysisResult"
 * @hibernate.cache usage="read-write"
 */
public class AnalysisResult{

	private String id = UUID.randomUUID().toString(); // 主键ID 
	private String statisticsDim; // 统计维度
	private String statisticsDimDesc; // 统计维度说明
	private String statisticsClassification; // 统计分类    要统计的表中的Y轴
	private String statisticsRange; // 统计范围  要统计的表中的X轴
	private int statisticsNum = 0; // 统计结果数量
	private BigDecimal statisticsAmount = new BigDecimal(0); // 统计涉及交易总金额
	private String valueType; // 属性值类型      数值型，金额型，字符串型
	private String otherValue = ""; // 非统计金额值存放在这里
	private StatisticsResult statisticsResult; // 关联的统计结果实体
	private String analysisResultMapKey; // 用于存放StatisticsResult对象中analysisResultMap的key
	private String deptId; // 机构ID
	
	public AnalysisResult(){
		
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
	* <p>方法名称: getStatisticsDim|描述: 统计维度</p>
	* @return
	* @hibernate.property type="string" column="ar_statisticsDim" length="300"
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
	* <p>方法名称: getStatisticsDimDesc|描述: 统计维度说明</p>
	* @return
	* @hibernate.property type="string" column="ar_statisticsDimDesc" length="300"
	*/
	public String getStatisticsDimDesc(){
		return statisticsDimDesc;
	}
	/**
	* <p>方法名称: setStatisticsDimDesc|描述: 统计维度说明</p>
	* @param statisticsDimDesc
	*/
	public void setStatisticsDimDesc(String statisticsDimDesc){
		this.statisticsDimDesc = statisticsDimDesc;
	}

	
	/**
	* <p>方法名称: getStatisticsClassification|描述: 统计分类</p>
	* @return
	* @hibernate.property type="string" column="ar_statisticsClassification" length="300"
	*/
	public String getStatisticsClassification(){
		return statisticsClassification;
	}
	/**
	* <p>方法名称: setStatisticsClassification|描述: 统计分类</p>
	* @param statisticsClassification
	*/
	public void setStatisticsClassification(String statisticsClassification){
		this.statisticsClassification = statisticsClassification;
	}


	/**
	* <p>方法名称: getStatisticsNum|描述: 统计结果数量</p>
	* @return
	* @hibernate.property type="integer" column="ar_statisticsNum"
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
	* @hibernate.property type="big_decimal" column="ar_statisticsAmount" precision="20" scale="2"
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
	* <p>方法名称: getStatisticsRange|描述: 统计范围</p>
	* @return
	* @hibernate.property type="string" column="ar_statisticsRange" length="300"
	*/
	public String getStatisticsRange(){
		return statisticsRange;
	}
	/**
	* <p>方法名称: setStatisticsRange|描述: 统计范围</p>
	* @param statisticsRange
	*/
	public void setStatisticsRange(String statisticsRange){
		this.statisticsRange = statisticsRange;
	}


	/**
	* <p>方法名称: getValueType|描述:  属性值类型</p>
	* @return
	* @hibernate.property type="string" column="ar_valueType" length="100"
	*/
	public String getValueType(){
		return valueType;
	}
	/**
	* <p>方法名称: setValueType|描述:  属性值类型</p>
	* @param valueType
	*/
	public void setValueType(String valueType){
		this.valueType = valueType;
	}


	/**
	* <p>方法名称: getOtherValue|描述: 非统计金额值存放在这里</p>
	* @return
	* @hibernate.property type="string" column="ar_otherValue" length="300"
	*/
	public String getOtherValue(){
		return otherValue;
	}
	/**
	* <p>方法名称: setOtherValue|描述: 非统计金额值存放在这里</p>
	* @param value
	*/
	public void setOtherValue(String otherValue){
		this.otherValue = otherValue;
	}

	
	/**
	* <p>方法名称: getStatisticsResult|描述: 关联的统计结果实体</p>
	* @return
	* @hibernate.many-to-one column="ar_statisticsResultId" class="com.mingtech.application.report.domain.StatisticsResult"
	*/
	public StatisticsResult getStatisticsResult(){
		return statisticsResult;
	}
	/**
	* <p>方法名称: setStatisticsResult|描述: 关联的统计结果实体</p>
	* @param statisticsResult
	*/
	public void setStatisticsResult(StatisticsResult statisticsResult){
		this.statisticsResult = statisticsResult;
	}


	
	/**
	* <p>方法名称: getAnalysisResultMapKey|描述: 用于存放StatisticsResult对象中analysisResultMap的key</p>
	* @return
	* @hibernate.property type="string" column="ar_analysisResultMapKey" length="500"
	*/
	public String getAnalysisResultMapKey(){
		return analysisResultMapKey;
	}
	/**
	* <p>方法名称: setAnalysisResultMapKey|描述: 用于存放StatisticsResult对象中analysisResultMap的key</p>
	* @param analysisResultMapKey
	*/
	public void setAnalysisResultMapKey(String analysisResultMapKey){
		this.analysisResultMapKey = analysisResultMapKey;
	}

	
	/**
	* <p>方法名称: getDeptId|描述: 机构ID</p>
	* @return
	* @hibernate.property type="string" column="ar_deptId" length="40"
	*/
	public String getDeptId(){
		return deptId;
	}
	/**
	* <p>方法名称: setDeptId|描述: </p>
	* @param deptId
	*/
	public void setDeptId(String deptId){
		this.deptId = deptId;
	}

	
	
	
}
