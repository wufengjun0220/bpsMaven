package com.mingtech.application.report.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-25 上午11:04:34
 * @描述: [ReportForm]报表对象实体
 * @hibernate.class table="R_ReportForm"
 * @hibernate.cache usage="read-write"
 */
public class ReportForm{

	private String id; // 主键ID
	private String name; // 表名
	private Date fillInDate; // 填表日期
	private String tabulator; // 制表人
	private String tabulatorPhone; // 制表人电话
	private String principal; // 负责人 
	private String status; // 报表状态
	private Date statisticBeginDate; // 报表统计开始日期
	private Date statisticEndDate; // 报表统计结束日期
	private String formType; // 报表类型    月度/季度/年度/其他
	private String formData; // 报表数据  XML格式
	private String formContent; // 报表内容  HTML格式
	private Date createTime; // 报表生成时间
	private String formBusType ; //报表业务类型
	private String formBusTypeName;//报表业务类型名称
	private String draftStuff; // 票据介质类型（纸票、电票）
	private String deptId; // 机构ID
	
	private Map map = new HashMap();

	

    
    public String getFormBusTypeName(){
    	/**
    	 * 报表类型集合
    	 */
    	map = getMap();
    	return (String)map.get(formBusType);
    }
    
    public Map getMap(){
    	map.put("001", "银行承兑汇票相关数据统计表2");
        map.put("002", "银行承兑汇票相关数据统计表");
        map.put("003", "银行承兑汇票保证方式");
        map.put("004", "出票企业规模分布");
        map.put("005", "商业汇票相关业务统计表");
        map.put("006", "票据地域（机构）流动统计表（转贴现-买断式）");
        map.put("007", "金融机构票据业务统计季报表（二）");
        map.put("008", "票据业务量机构分布（直贴、买断式转帖）");
        map.put("009", "票据贴现加权平均利率季度表");
        map.put("010", "票据贴现期限与利率机构");
        map.put("011", "出票企业行业分布业务调查");
        map.put("012", "金融机构票据业务统计季报表（一）");
        map.put("013", "银行承兑汇票保证方式");
        map.put("014", "商业承兑汇票业务统计表（月度）");
        map.put("015", "票据业务总体情况");
       
        /**去掉‘日’ ‘月’
        map.put("pj001", "票据业务统计日报表");
        map.put("pj002", "票据业务统计月报表");
        map.put("pj003", "票据业务统计日报表");
        map.put("pj004", "票据业务统计月报表");
        map.put("pj005", "票据业务统计月报表");
        map.put("pj006", "票据业务统计日报表");
        map.put("pj009", "票据业务统计月报表");
        map.put("pj010", "票据业务统计日报表");
        */
        map.put("pj001", "票据业务统计报表");
        map.put("pj002", "票据业务统计报表");
        map.put("pj003", "票据业务统计报表");
        map.put("pj004", "票据业务统计报表");
        map.put("pj005", "票据业务统计报表");
        map.put("pj006", "票据业务统计报表");
        map.put("pj008", "票据业务统计报表");
        map.put("pj009", "票据业务统计报表");
        map.put("pj010", "票据业务统计报表");
        map.put("pj013", "G报表");
        map.put("pjywtz01", "票据业务台帐");
        map.put("pjywtz02", "票据业务台帐");
        
        return map;
    }

	public ReportForm(){
		
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
	* <p>方法名称: getName|描述: 表名</p>
	* @return
	* @hibernate.property type="string" column="rf_name" length="300"
	*/
	public String getName(){
		return name;
	}
	/**
	* <p>方法名称: setName|描述: 表名</p>
	* @param name
	*/
	public void setName(String name){
		this.name = name;
	}


	/**
	* <p>方法名称: getFillInDate|描述: 填表日期</p>
	* @return
	* @hibernate.property type="date" column="rf_fillInDate"
	*/
	public Date getFillInDate(){
		return fillInDate;
	}
	/**
	* <p>方法名称: setFillInDate|描述: 填表日期</p>
	* @param fillInDate
	*/
	public void setFillInDate(Date fillInDate){
		this.fillInDate = fillInDate;
	}


	/**
	* <p>方法名称: getTabulator|描述: 制表人</p>
	* @return
	* @hibernate.property type="string" column="rf_tabulator" length="300"
	*/
	public String getTabulator(){
		return tabulator;
	}
	/**
	* <p>方法名称: setTabulator|描述: 制表人</p>
	* @param tabulator
	*/
	public void setTabulator(String tabulator){
		this.tabulator = tabulator;
	}

	
	/**
	* <p>方法名称: getTabulatorPhone|描述: 制表人电话</p>
	* @return
	* @hibernate.property type="string" column="rf_tabulatorPhone" length="100"
	*/
	public String getTabulatorPhone(){
		return tabulatorPhone;
	}
	/**
	* <p>方法名称: setTabulatorPhone|描述: 制表人电话</p>
	* @param tabulatorPhone
	*/
	public void setTabulatorPhone(String tabulatorPhone){
		this.tabulatorPhone = tabulatorPhone;
	}


	/**
	* <p>方法名称: getPrincipal|描述: 负责人</p>
	* @return
	* @hibernate.property type="string" column="rf_principal" length="100"
	*/
	public String getPrincipal(){
		return principal;
	}
	/**
	* <p>方法名称: setPrincipal|描述: 负责人</p>
	* @param principal
	*/
	public void setPrincipal(String principal){
		this.principal = principal;
	}


	/**
	* <p>方法名称: getStatus|描述: 报表状态</p>
	* @return
	* @hibernate.property type="string" column="rf_status" length="100"
	*/
	public String getStatus(){
		return status;
	}
	/**
	* <p>方法名称: setStatus|描述: 报表状态</p>
	* @param status
	*/
	public void setStatus(String status){
		this.status = status;
	}


	/**
	* <p>方法名称: getStatisticBeginDate|描述: 报表统计开始日期</p>
	* @return
	* @hibernate.property type="date" column="rf_statisticBeginDate"
	*/
	public Date getStatisticBeginDate(){
		return statisticBeginDate;
	}
	/**
	* <p>方法名称: setStatisticBeginDate|描述: 报表统计开始日期</p>
	* @param statisticBeginDate
	*/
	public void setStatisticBeginDate(Date statisticBeginDate){
		this.statisticBeginDate = statisticBeginDate;
	}

	
	/**
	* <p>方法名称: getStatisticEndDate|描述: 报表统计结束日期</p>
	* @return
	* @hibernate.property type="date" column="rf_statisticEndDate"
	*/
	public Date getStatisticEndDate(){
		return statisticEndDate;
	}
	/**
	* <p>方法名称: setStatisticEndDate|描述: 报表统计结束日期</p>
	* @param statisticEndDate
	*/
	public void setStatisticEndDate(Date statisticEndDate){
		this.statisticEndDate = statisticEndDate;
	}


	
	/**
	* <p>方法名称: getFormType|描述: 报表类型</p>
	* @return
	* @hibernate.property type="string" column="rf_formType" length="100"
	*/
	public String getFormType(){
		return formType;
	}
	/**
	* <p>方法名称: setFormType|描述: 报表类型</p>
	* @param formType
	*/
	public void setFormType(String formType){
		this.formType = formType;
	}

	
	/**
	* <p>方法名称: getFormData|描述: 报表数据</p>
	* @return
	* @hibernate.property type="org.springframework.orm.hibernate3.support.ClobStringType" column="rf_formData"
	*/
	public String getFormData(){
		return formData;
	}
	/**
	* <p>方法名称: setFormData|描述: 报表数据</p>
	* @param formData
	*/
	public void setFormData(String formData){
		this.formData = formData;
	}


	/**
	* <p>方法名称: getFormContent|描述: 报表内容</p>
	* @return
	* @hibernate.property type="org.springframework.orm.hibernate3.support.ClobStringType" column="rf_formContent"
	*/
	public String getFormContent(){
		return formContent;
	}
	/**
	* <p>方法名称: setFormContent|描述: 报表内容</p>
	* @param formContent
	*/
	public void setFormContent(String formContent){
		this.formContent = formContent;
	}


	/**
	* <p>方法名称: getCreateTime|描述: 报表生成时间</p>
	* @return
	* @hibernate.property type="timestamp" column="rf_createTime"
	*/
	public Date getCreateTime(){
		return createTime;
	}
	/**
	* <p>方法名称: setCreateTime|描述: 报表生成时间</p>
	* @param createTime
	*/
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}



	/**
	* <p>方法名称: getFormBusType|描述: 获取报表业务类型</p>
	* @return
	* @hibernate.property type="string" column="rf_FormBusType"
	*/
	public String getFormBusType(){
		return formBusType;
	}

	/**
	* <p>方法名称: setFormBusType|描述: 设置报表业务类型</p>
	* @param formBusType
	*/
	public void setFormBusType(String formBusType){
		this.formBusType = formBusType;
	}

	/**
	* <p>方法名称: getDraftStuff|描述: 获取票据介质类型（纸票、电票）</p>
	* @return
	* @hibernate.property type="string" column="rf_draftStuff"
	*/
	public String getDraftStuff(){
		return draftStuff;
	}

	/**
	* <p>方法名称: setDraftStuff|描述: 设置票据介质类型（纸票、电票）</p>
	* @param draftStuff
	*/
	public void setDraftStuff(String draftStuff){
		this.draftStuff = draftStuff;
	}

	
	/**
	* <p>方法名称: getDeptId|描述: 机构ID</p>
	* @return
	* @hibernate.property type="string" column="rf_deptId"
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
