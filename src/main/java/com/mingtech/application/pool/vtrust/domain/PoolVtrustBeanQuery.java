package com.mingtech.application.pool.vtrust.domain;

/**
 * 封装查询条件
 * @author wbmengdepeng
 *
 */

public class PoolVtrustBeanQuery extends PoolVtrust{
	//票据池编号 liuxiaodong add
	private String poolAgreement;
	private String poolName;//票据池名称
	
	private String inBatchNo;//入池批次号
	private String riskLevel;//风险等级
	

	public String getInBatchNo() {
		return inBatchNo;
	}
	public void setInBatchNo(String inBatchNo) {
		this.inBatchNo = inBatchNo;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getPoolAgreement() {
		return poolAgreement;
	}
	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
}
