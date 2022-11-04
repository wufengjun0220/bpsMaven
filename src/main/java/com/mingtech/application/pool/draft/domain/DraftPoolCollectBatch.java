/**
 * 
 */
package com.mingtech.application.pool.draft.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.mingtech.application.pool.base.domain.PoolCollectBatch;
import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
 * 托收批次（票据池）
 * @author wbyecheng
 * 
 */
public class DraftPoolCollectBatch extends PoolCollectBatch {

	private static final long serialVersionUID = -2134391932822531268L;
	/**
	 * 废弃，与基类重复  
	 */
	//private String id;// 托收批次id
	//private String SBatchNo; // 批次号
	//private Date collectDt;// 托收日期
	//private Integer totalSize;      //从数据库提取，废弃
	//private BigDecimal totalMoney;  //从数据库提取，废弃
	
	private String operId;// 操作员id
	private String bankNumber;// 兑付行行号
	private String collectBank;//兑付行名称
	private String collectAddr;// 兑付地址
	private String collectPostcode;// 兑付邮编
	private String collectOperSts;// 兑付状态
	private String collectOperStsName;// 兑付状态
	private Set<DraftPoolCollect> collectionDtos = new HashSet<DraftPoolCollect>(0);// 发托明细信息

	private Date createTime;// 创建时间
	
//	public String getId() {
//		return this.id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

	/* 票据状态名 */
	public String getCollectOperStsName() {
		return collectOperStsName = PoolDictionaryCache
				.getStatusName(this.getCollectOperSts());
	}

	public void setCollectOperStsName(String collectOperStsName) {
		this.collectOperStsName = collectOperStsName;
	}
	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

//	public Date getCollectDt() {
//		return this.collectDt;
//	}
//
//	public void setCollectDt(Date collectDt) {
//		this.collectDt = collectDt;
//	}

	public String getOperId() {
		return operId;
	}

	public void setOperId(String operId) {
		this.operId = operId;
	}

	public String getCollectBank() {
		return this.collectBank;
	}

	public void setCollectBank(String collectBank) {
		this.collectBank = collectBank;
	}

	public String getCollectAddr() {
		return this.collectAddr;
	}

	public void setCollectAddr(String collectAddr) {
		this.collectAddr = collectAddr;
	}

	public String getCollectPostcode() {
		return this.collectPostcode;
	}

	public void setCollectPostcode(String collectPostcode) {
		this.collectPostcode = collectPostcode;
	}

	public String getCollectOperSts() {
		return this.collectOperSts;
	}

	public void setCollectOperSts(String collectOperSts) {
		this.collectOperSts = collectOperSts;
	}

	public Set<DraftPoolCollect> getCollectionDtos() {
		return collectionDtos;
	}

	public void setCollectionDtos(Set<DraftPoolCollect> collectionDtos) {
		this.collectionDtos = collectionDtos;
	}

//	public String getSBatchNo() {
//		return SBatchNo;
//	}
//
//	public void setSBatchNo(String batchNo) {
//		SBatchNo = batchNo;
//	}
//
//	public Integer getTotalSize() {
//		/*
//		 * Integer totalSize=new Integer("0"); if(null!=collectionDtos){
//		 * totalSize=new Integer(collectionDtos.size()); }
//		 */
//		return totalSize;
//	}
//
//	public void setTotalSize(Integer totalSize) {
//		this.totalSize = totalSize;
//	}

//	public BigDecimal getTotalMoney() {
//		/*
//		 * BigDecimal totalMoney=new BigDecimal("0"); if(null!=collectionDtos){
//		 * for(Iterator it=collectionDtos.iterator();it.hasNext();){
//		 * DraftCollectionDto collectionDto=(DraftCollectionDto)it.next();
//		 * if(null!=collectionDto){ BigDecimal
//		 * invAmt=collectionDto.getFBillAmount();
//		 * totalMoney=totalMoney.add(invAmt); } } }
//		 * totalMoney=totalMoney.setScale(2, BigDecimal.ROUND_HALF_UP);
//		 */
//		return totalMoney;
//	}
//
//	public void setTotalMoney(BigDecimal totalMoney) {
//		this.totalMoney = totalMoney;
//	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
