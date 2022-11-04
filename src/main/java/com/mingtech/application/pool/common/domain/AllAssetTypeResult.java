package com.mingtech.application.pool.common.domain;

import java.util.List;

import com.mingtech.application.pool.draft.domain.DraftPool;

public class AllAssetTypeResult implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private AssetType ed_pjc ;//低风险票据额度
	private AssetType ed_pjc_01 ;//高风险票据额度
	private AssetType ed_bzj_hq ;//活期保证金额度
	private AssetType ed_bzj_dq ;//定期保证金额度
	
	
	List<DraftPool> DraftPoolList;//出池票据lis   出池校验使用
	String outApplyCheckResult;//出池校验结果      0：不可出池     1：可出池     EBK_10：到期日当天不允许出池操作
	
	
	
	
	public List<DraftPool> getDraftPoolList() {
		return DraftPoolList;
	}
	public void setDraftPoolList(List<DraftPool> draftPoolList) {
		DraftPoolList = draftPoolList;
	}
	public String getOutApplyCheckResult() {
		return outApplyCheckResult;
	}
	public void setOutApplyCheckResult(String outApplyCheckResult) {
		this.outApplyCheckResult = outApplyCheckResult;
	}
	public AssetType getEd_pjc() {
		return ed_pjc;
	}
	public void setEd_pjc(AssetType ed_pjc) {
		this.ed_pjc = ed_pjc;
	}
	public AssetType getEd_pjc_01() {
		return ed_pjc_01;
	}
	public void setEd_pjc_01(AssetType ed_pjc_01) {
		this.ed_pjc_01 = ed_pjc_01;
	}
	public AssetType getEd_bzj_hq() {
		return ed_bzj_hq;
	}
	public void setEd_bzj_hq(AssetType ed_bzj_hq) {
		this.ed_bzj_hq = ed_bzj_hq;
	}
	public AssetType getEd_bzj_dq() {
		return ed_bzj_dq;
	}
	public void setEd_bzj_dq(AssetType ed_bzj_dq) {
		this.ed_bzj_dq = ed_bzj_dq;
	}
	
	
	

	
	

	
	
	
}
