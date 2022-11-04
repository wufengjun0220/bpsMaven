package com.mingtech.application.sysmanage.domain;

import java.util.Date;

import com.mingtech.framework.common.util.DateUtils;
/**
 * 公告实体
 * @author houshuchao
 * @hibernate.class table="t_inform"
 * @hibernate.cache usage="read-write"
 *
 */
public class Inform {

	private String id;//主键
	private String title;//主题
	private String contents;//内容
	private String filepath;//附件路径
	private Date createTime;//创建时间
	private int showLevel;//显示级别
	private String userId;//创建人ID
	private String userName;//创建人名称
	private String userDept;//创建人机构
	private String remark;//备注
	private String status;//使用状态
	private String statusStr;//使用状态字符串
	private String createTimeStr;//时间属性
	private String showLevelStr;//首页属性
	private Date topTime;//置顶时间
	
	private Date distrubiteDate;//发布时间
	private String distrubiteDateStr;//发布时间字符串
	private Date endDate;//截止时间
	private String endDateStr;//截止时间字符串
	private String distributeDepart;//发布部门
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="timestamp" column="topTime"
	 */
	public Date getTopTime() {
		return topTime;
	}
	public void setTopTime(Date topTime) {
		this.topTime = topTime;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="50" column="userName"
	 */
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="2" column="status"
	 */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="50" column="userDept"
	 */
	public String getUserDept() {
		return userDept;
	}
	public void setUserDept(String userDept) {
		this.userDept = userDept;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="50" column="userId"
	 */
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="200" column="title"
	 */
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="2000" column="content"
	 */
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="100" column="filePath"
	 */
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="timestamp" column="cretTime"
	 */
	public Date getCreateTime() {
		return createTime;
	}
	public String getCreateTimeStr() {
		return DateUtils.toString(this.getCreateTime(), "yyyy-MM-dd");
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="int" length="2" column="showLevel"
	 */
	public int getShowLevel() {
		return showLevel;
	}
	public void setShowLevel(int showLevel) {
		this.showLevel = showLevel;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="50" column="remark"
	 */
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="50" column="id"
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShowLevelStr() {
		String level = null;
		if(0 == this.getShowLevel()){
			level = "否";
		}else{
			level = "是";
		}
		return level;
	}
	public String getStatusStr() {
		String str = "";
		if("00".equals(this.getStatus())){
			str = "新增";
		}else if("01".equals(this.getStatus())){
			str = "已发布";
		}else if("02".equals(this.getStatus())){
			str = "已失效";
		}
		return str;
	}
	public Date getDistrubiteDate() {
		return distrubiteDate;
	}
	public void setDistrubiteDate(Date distrubiteDate) {
		this.distrubiteDate = distrubiteDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getDistributeDepart() {
		return distributeDepart;
	}
	public void setDistributeDepart(String distributeDepart) {
		this.distributeDepart = distributeDepart;
	}
	public String getDistrubiteDateStr() {
		return DateUtils.toString(this.getDistrubiteDate(), "yyyy-MM-dd");
	}
	public String getEndDateStr() {
		return DateUtils.toString(this.getEndDate(), "yyyy-MM-dd");
	}
	
}
