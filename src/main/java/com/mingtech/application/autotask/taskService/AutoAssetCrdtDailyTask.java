package com.mingtech.application.autotask.taskService;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.query.service.AssetCrdtDailyService;

/**
 * 每日票据池资产、融资业务快照生成
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class AutoAssetCrdtDailyTask  extends AbstractAutoTask {
	
	AssetCrdtDailyService assetCrdtDailyService = PoolCommonServiceFactory.getAssetCrdtDailyService();

	public BooleanAutoTaskResult run() throws Exception {
			
		System.out.println("每日票据池资产、融资业务快照生成开始。。。。。");
		/*
		 * 【1】每日融资业务明细生成...
		 */
		assetCrdtDailyService.txCrdtDailyTask();
		
		/*
		 * 【2】每日票据资产明细生成...
		 */
		assetCrdtDailyService.txBillAssetDailyTask();
		
		
		/*
		 * 【3】每日保证金资产资产明细生成...
		 */
		assetCrdtDailyService.txBailAssetDailyTask();
		
		/*
		 * 【4】每日资产/融资业务按票据池客户生成...
		 */
		assetCrdtDailyService.txAssetCrdtDailyTask();
		
		/*
		 * 【5】每日票据池资产、融资业务快照生成批次号处理...
		 */
		assetCrdtDailyService.txAssetCrdtDailyBatchNoTask();
		
		System.out.println("每日票据池资产、融资业务快照生成结束。。。。。");
		
		return new BooleanAutoTaskResult(true);
	}
	

	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}


	
}
