/**
 * 
 */
package com.mingtech.application.pool.base.domain;

import com.mingtech.application.pool.draft.domain.PoolQueryBean;

/**
 * @author wbyecheng
 * 
 * 资产到期批次实体
 *
 */
public class PoolCollectBatch extends PoolBaseBatch {
	
	private PoolQueryBean queryBean;

	public PoolQueryBean getQueryBean() {
		return queryBean;
	}

	public void setQueryBean(PoolQueryBean queryBean) {
		this.queryBean = queryBean;
	}
	
	

}
