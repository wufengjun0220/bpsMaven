package com.mingtech.application.pool.common.domain;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.edu.domain.BailDetail;

public class AssetFactory {

	public static DraftPool newDraftPool() {

		return new DraftPool(PoolComm.ED_PJC, PoolComm.ED_FD_NO,
				PoolComm.LOW_RISK);
	}

	public static BailDetail newCurBailDetail() {

		return new BailDetail(PoolComm.ED_BZJ_HQ, PoolComm.ED_FD_YES,
				PoolComm.LOW_RISK);
	}

	public static BailDetail newTimeBailDetail() {

		return new BailDetail(PoolComm.ED_BZJ_HQ, PoolComm.ED_FD_NO,
				PoolComm.LOW_RISK);
	}
}
