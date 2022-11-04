package com.mingtech.application.pool.common.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: tangxiongyu
 * @描述: [PedBlackList]黑灰名单管理实体
 * @hibernate.class table="PED_BlackList"
 * @hibernate.cache usage="read-write"
 */
public class PedBlackList {

	private String id; // 主键
	private Date createTime; // 创建时间
	private String type; // 黑名单/灰名单   01-黑名单  02-灰名单  
	private String keywords; // 校验内容-关键词（类型）
	private String content; // 校验内容-关键值
	private String province; // 当关键词为承兑人所在地区保存省份
	private String city; // 当关键词为承兑人所在地区保存城市
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dueDt; // 有效期至
	private String billMedia; // 票据介质
	private String billType; // 票据类别
	private String dataFrom; // 数据来源(网银/票据池)
	// 客户录入时添加字段
	private String orgCode;// 组织机构代码 ---实际放入客户号
	private String customerName; // 客户名称
	// 银行录入时添加字段
	private String netNo; // 网点号
	private String tellerNo; // 柜员号
	private String remark;//备注
	
	private String contentTmp;
	/**
	 * <p>
	 * 方法名称: getId|描述: 主键
	 * </p>
	 * 
	 * @return
	 * @hibernate.id generator-class="uuid" type="string" length="40" column="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * <p>
	 * 方法名称: setId|描述: 主键
	 * </p>
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * <p>
	 * 方法名称: getCreateTime|描述: 创建时间
	 * </p>
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * <p>
	 * 方法名称: setCreateTime|描述: 创建时间
	 * </p>
	 * 
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * <p>
	 * 方法名称: getBillMedia|描述: 票据介质
	 * </p>
	 * 
	 * @return
	 */
	public String getBillMedia() {
		return billMedia;
	}

	/**
	 * <p>
	 * 方法名称: setBillMedia|描述: 票据介质
	 * </p>
	 * 
	 * @param billMedia
	 */
	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}

	public String getBillMediaStr() {
		return PoolDictionaryCache.getBillMedia(this.billMedia);
	}

	/**
	 * <p>
	 * 方法名称: getBillType|描述: 票据类别
	 * </p>
	 * 
	 * @return
	 */
	public String getBillType() {
		return billType;
	}

	/**
	 * <p>
	 * 方法名称: setBillType|描述: 票据类别
	 * </p>
	 * 
	 * @param billType
	 */
	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillTypeStr() {
		return PoolDictionaryCache.getBillType(this.billType);
	}

	/**
	 * <p>
	 * 方法名称: getDueDt|描述: 到期日
	 * </p>
	 * 
	 */
	public Date getDueDt() {
		return dueDt;
	}

	/**
	 * <p>
	 * 方法名称: setDueDt|描述: 到期日
	 * </p>
	 * 
	 * @param dueDt
	 */
	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getKeywordsStr() {
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_01)) {
			return "开票人";
		}
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_02)) {
			return "承兑人";
		}
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_03)) {
			return "承兑行";
		}
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_04)) {
			return "承兑行所在地区";
		}
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_05)) {
			return "票号";
		}
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_06)) {
			return "背书人";
		}
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_07)) {
			return "票据流转次数";
		}
		if (StringUtils.equals(this.getKeywords(), PoolComm.KEY_WAYS_08)) {
			return "剩余期限";
		}
		return null;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getNetNo() {
		return netNo;
	}

	public void setNetNo(String netNo) {
		this.netNo = netNo;
	}

	public String getTellerNo() {
		return tellerNo;
	}

	public void setTellerNo(String tellerNo) {
		this.tellerNo = tellerNo;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContentTmp() {
		return contentTmp;
	}

	public void setContentTmp(String contentTmp) {
		this.contentTmp = contentTmp;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}
