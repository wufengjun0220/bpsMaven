package com.mingtech.application.report.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-11 下午01:57:22
 * @描述: [SearchResult]查询结果实体
 * @hibernate.class table="R_SearchResult"
 * @hibernate.cache usage="read-write"
 */
public class SearchResult{

	private String id; // 主键ID
	private String idNb; // 票据号码
	private String bussinessFlowDtoId; // 交易流水ID
    private String drwrNm; // 出票人名称
    private String accptrNm; // 承兑人名称
    private String acceptorBankCode; // 承兑行行号
    private BigDecimal isseAmt = new BigDecimal(0); // 票据金额
    private Date isseDt; // 出票日期
	private Date dueDt; // 到期日
	private StatisticsResult statisticsResult; // 关联的统计结果实体
	private String resultSource; // 结果集来源       交易流水表，大票表(不存储数据库)
	private Map expandingProperty = new HashMap(); // 扩展属性(不存储数据库)
	private String deptId; // 机构ID
	
	public SearchResult(){
		
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
	* <p>方法名称: getIdNb|描述: 票据号码</p>
	* @return
	* @hibernate.property type="string" column="sr_idNb" length="40"
	*/
	public String getIdNb(){
		return idNb;
	}
	/**
	* <p>方法名称: setIdNb|描述: 票据号码</p>
	* @param idNb
	*/
	public void setIdNb(String idNb){
		this.idNb = idNb;
	}

	/**
	* <p>方法名称: getBussinessFlowDtoId|描述: 交易流水ID</p>
	* @return
	* @hibernate.property type="string" column="sr_bussinessFlowDtoId" length="40"
	*/
	public String getBussinessFlowDtoId(){
		return bussinessFlowDtoId;
	}
	/**
	* <p>方法名称: setBussinessFlowDtoId|描述: 交易流水ID</p>
	* @param bussinessFlowDtoId
	*/
	public void setBussinessFlowDtoId(String bussinessFlowDtoId){
		this.bussinessFlowDtoId = bussinessFlowDtoId;
	}


	/**
	* <p>方法名称: getDrwrNm|描述: 出票人名称</p>
	* @return
	* @hibernate.property type="string" column="sr_drwrNm" length="180"
	*/
	public String getDrwrNm(){
		return drwrNm;
	}
	/**
	* <p>方法名称: setDrwrNm|描述: 出票人名称</p>
	* @param drwrNm
	*/
	public void setDrwrNm(String drwrNm){
		this.drwrNm = drwrNm;
	}

	
	/**
	* <p>方法名称: getAccptrNm|描述: 承兑人名称</p>
	* @return
	* @hibernate.property type="string" column="sr_accptrNm" length="180"
	*/
	public String getAccptrNm(){
		return accptrNm;
	}
	/**
	* <p>方法名称: setAccptrNm|描述: 承兑人名称</p>
	* @param accptrNm
	*/
	public void setAccptrNm(String accptrNm){
		this.accptrNm = accptrNm;
	}

	
	/**
	* <p>方法名称: getAcceptorBankCode|描述: 获取承兑行行号</p>
	* @return
	* @hibernate.property type="string" column="sr_acceptorBankCode" length="20"
	*/
	public String getAcceptorBankCode(){
		return acceptorBankCode;
	}

	/**
	* <p>方法名称: setAcceptorBankCode|描述: 设置承兑行行号</p>
	* @param acceptorBankCode
	*/
	public void setAcceptorBankCode(String acceptorBankCode){
		this.acceptorBankCode = acceptorBankCode;
	}


	/**
	* <p>方法名称: getIsseAmt|描述: 票据金额</p>
	* @return
	* @hibernate.property type="big_decimal" column="sr_isseAmt" precision="20" scale="2"
	*/
	public BigDecimal getIsseAmt(){
		return isseAmt;
	}
	/**
	* <p>方法名称: setIsseAmt|描述: 票据金额</p>
	* @param isseAmt
	*/
	public void setIsseAmt(BigDecimal isseAmt){
		this.isseAmt = isseAmt;
	}


	/**
	* <p>方法名称: getIsseDt|描述: 出票日期</p>
	* @return
	* @hibernate.property type="date" column="sr_isseDt"
	*/
	public Date getIsseDt(){
		return isseDt;
	}
	/**
	* <p>方法名称: setIsseDt|描述: 出票日期</p>
	* @param isseDt
	*/
	public void setIsseDt(Date isseDt){
		this.isseDt = isseDt;
	}

	
	/**
	* <p>方法名称: getDueDt|描述: 到期日</p>
	* @return
	* @hibernate.property type="date" column="sr_dueDt"
	*/
	public Date getDueDt(){
		return dueDt;
	}
	/**
	* <p>方法名称: setDueDt|描述: 到期日</p>
	* @param dueDt
	*/
	public void setDueDt(Date dueDt){
		this.dueDt = dueDt;
	}

	
	/**
	* <p>方法名称: getResultSource|描述: 结果集来源(不存储数据库)</p>
	* @return
	*/
	public String getResultSource() {
		return resultSource;
	}
	/**
	* <p>方法名称: setResultSource|描述: 结果集来源(不存储数据库)</p>
	* @param resultSource
	*/
	public void setResultSource(String resultSource) {
		this.resultSource = resultSource;
	}


	/**
	* <p>方法名称: getExpandingProperty|描述: 扩展属性(不存储数据库)</p>
	* @return
	*/
	public Map getExpandingProperty(){
		return expandingProperty;
	}
	/**
	* <p>方法名称: setExpandingProperty|描述: 扩展属性(不存储数据库)</p>
	* @param expandingProperty
	*/
	public void setExpandingProperty(Map expandingProperty){
		this.expandingProperty = expandingProperty;
	}


	/**
	* <p>方法名称: getStatisticsResult|描述: 关联的统计结果实体</p>
	* @return
	* @hibernate.many-to-one column="sr_statisticsResultId" class="com.mingtech.application.report.domain.StatisticsResult"
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
	* <p>方法名称: getDeptId|描述: 机构ID</p>
	* @hibernate.property type="string" column="sr_deptId"
	* @return
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
