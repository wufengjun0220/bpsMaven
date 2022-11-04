/**
 * 
 */
package com.mingtech.application.pool.draft.domain;


/**
 * @author wbyecheng
 * 
 *         票据资产出池业务明细实体
 * 
 */
public class DraftPoolOut extends DraftPoolBase {
	private static final long serialVersionUID = 2537391403469865649L;
	private DraftPoolOutBatch draftPoolOutBatch;

	public DraftPoolOutBatch getDraftPoolOutBatch() {
		return draftPoolOutBatch;
	}

	public void setDraftPoolOutBatch(DraftPoolOutBatch draftPoolOutBatch) {
		this.draftPoolOutBatch = draftPoolOutBatch;
	}
	
}
