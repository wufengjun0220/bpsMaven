/**
 * 
 */
package com.mingtech.application.pool.draft.domain;

import java.util.HashSet;
import java.util.Set;


/**
 * @author wbyecheng
 * 
 *         票据资产入池业务批次实体
 * 
 */
public class DraftPoolInBatch extends DraftPoolBaseBatch {
	
	private static final long serialVersionUID = 9008833543038283333L;
	private Set<DraftPoolIn> poolIns = new HashSet<DraftPoolIn>();// 批次内的明细

	public Set<DraftPoolIn> getPoolIns() {
		return poolIns;
	}

	public void setPoolIns(Set<DraftPoolIn> poolIns) {
		this.poolIns = poolIns;
	}


	
	
}
