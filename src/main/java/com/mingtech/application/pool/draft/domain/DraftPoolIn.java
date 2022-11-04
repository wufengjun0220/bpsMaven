/**
 * 
 */
package com.mingtech.application.pool.draft.domain;


/**
 * @author wbyecheng
 * 
 *         票据资产入池业务明细实体
 * 
 */
public class DraftPoolIn extends DraftPoolBase {
	private static final long serialVersionUID = -2735381747172869380L;
	private DraftPoolInBatch draftPoolInBatch;// 批次ID

	public DraftPoolInBatch getDraftPoolInBatch() {
		return draftPoolInBatch;
	}

	public void setDraftPoolInBatch(DraftPoolInBatch draftPoolInBatch) {
		this.draftPoolInBatch = draftPoolInBatch;
	}


}
