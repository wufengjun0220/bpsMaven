/**
 * 
 */
package com.mingtech.application.pool.base.domain;

import com.mingtech.application.pool.draft.domain.PoolQueryBean;

/**
 * @author wbyecheng
 * 
 *         资产到期明细实体
 * 
 */
public class PoolCollect {
	private PoolQueryBean queryBean;

	private String id;// 主键ID
	private String SBranchId;// 机构ID
	private String plStatus;// 状态
	private String plRemark;// 备注
	
	
	public PoolQueryBean getQueryBean() {
		return queryBean;
	}

	public void setQueryBean(PoolQueryBean queryBean) {
		this.queryBean = queryBean;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlStatus() {
		return plStatus;
	}

	public void setPlStatus(String plStatus) {
		this.plStatus = plStatus;
	}

	public String getPlRemark() {
		return plRemark;
	}

	public void setPlRemark(String plRemark) {
		this.plRemark = plRemark;
	}

	public String getSBranchId() {
		return SBranchId;
	}

	public void setSBranchId(String sBranchId) {
		SBranchId = sBranchId;
	}


}
