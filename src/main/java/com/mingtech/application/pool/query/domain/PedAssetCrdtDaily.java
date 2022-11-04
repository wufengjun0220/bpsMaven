package com.mingtech.application.pool.query.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.pool.common.PoolComm;

/**
 * 每日资产/融资业务汇总表：该表日终跑批时候生成数据，记录当日票据池所有客户资产/融资业务汇总数据
 * 该表为批次表，明细表有：每日资产表&每日融资业务表
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 */

public class PedAssetCrdtDaily implements java.io.Serializable {


	private static final long serialVersionUID = 6011912058980076865L;
	private String id;        
	private String bpsNo;     //票据池编号
	private String bpsName;   //票据池名称
	private Date createDate;  //创建时间
	private String busiType;  //业务类型  01：资产-现金 02：资产-票据 03：融资业务-银承 04：融资业务-流贷  05：融资业务-保函  06：融资业务-国内信用证
	private String busiTypeName; 
	private BigDecimal totalAmt;  //总金额
	private Date createTime; //创建时间



	public String getBusiTypeName() {
		String busiTypeName = "";
		if("01".equals(this.getBusiType() )|| StringUtils.isEmpty(this.getBusiType())){
			busiTypeName = "资产-现金";
		}else if("02".equals(this.getBusiType())){
			busiTypeName = "资产-票据";
		}else if("03".equals(this.getBusiType())){
			busiTypeName = "融资业务-银承";
		}else if("04".equals(this.getBusiType())){
			busiTypeName = "融资业务-流贷";
		}else if("05".equals(this.getBusiType())){
			busiTypeName = "融资业务-保函";
		}else if("06".equals(this.getBusiType())){
			busiTypeName = "融资业务-国内信用证";
		}
		return busiTypeName;
	}

	public void setBusiTypeName(String busiTypeName) {
		this.busiTypeName = busiTypeName;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return this.bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getBusiType() {
		return this.busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}


	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}