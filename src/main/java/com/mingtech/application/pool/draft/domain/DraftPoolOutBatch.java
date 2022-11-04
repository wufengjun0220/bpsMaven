/**
 * 
 */
package com.mingtech.application.pool.draft.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wbyecheng
 * 
 * 票据资产出池业务批次实体
 *
 */
public class DraftPoolOutBatch extends DraftPoolBaseBatch {
	
	private static final long serialVersionUID = 4399208629057187521L;
	private Set<DraftPoolOut> poolOuts = new HashSet<DraftPoolOut>(0);// 批次内的明细

	public Set<DraftPoolOut> getPoolOuts() {
		return poolOuts;
	}

	public void setPoolOuts(Set<DraftPoolOut> poolOuts) {
		this.poolOuts = poolOuts;
	}



}
