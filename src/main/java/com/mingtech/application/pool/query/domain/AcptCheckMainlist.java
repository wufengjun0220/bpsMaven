package com.mingtech.application.pool.query.domain;

import java.sql.Timestamp;

/**
 * 商票承兑行名单主表，存放汉口银行电票行信息
 * 		该表用于财票、商票入池校验，入池票据在该表中，可产生额度
 * @author gcj
 * @version v1.0
 * @date 2021-6-7
 */

public class AcptCheckMainlist implements java.io.Serializable {

	private static final long serialVersionUID = 6172505808489764948L;
	private String id;              //主键ID    
	private String bankNo;          //开户行行号
	private String bankName;        //开户行行名
	private Timestamp createTime;   //创建时间   
	private Timestamp updateTime;   //最近修改时间


	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}


}