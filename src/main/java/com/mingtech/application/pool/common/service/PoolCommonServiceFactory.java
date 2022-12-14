package com.mingtech.application.pool.common.service;

import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.ecd.service.EndorsementLogService;
import com.mingtech.application.ecds.common.BatchNoUtils;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.message.service.PoolMssService;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.draft.service.MisDraftService;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.service.OnlineCommonService;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.pool.query.service.AssetCrdtDailyService;
import com.mingtech.application.pool.report.service.PoolReportService;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * ??????????????? ????????????
 * @author Administrator
 *
 */
public class PoolCommonServiceFactory {
	/**
	 * ???????????? ??????
	 * @return
	 */
	public static PedProtocolService getPedProtocolService(){
		PedProtocolService service = (PedProtocolService)SpringContextUtil.getBean("pedProtocolService");
		return service;
	}
	
	/**
	 * ????????????????????? ??????
	 * @return
	 */
	public static PoolEcdsService getPoolEcdsService(){
		PoolEcdsService service = (PoolEcdsService)SpringContextUtil.getBean("poolEcdsService");
		return service;
	}
	
	/**
	 * ????????????????????? ??????
	 * @return
	 */
	public static PoolCreditService getPoolCreditService(){
		PoolCreditService service = (PoolCreditService)SpringContextUtil.getBean("poolCreditService");
		return service;
	}
	
	/**
	 * ????????????????????? ??????
	 * @return
	 */
	public static PoolCoreService getPoolCoreService(){
		PoolCoreService service = (PoolCoreService)SpringContextUtil.getBean("poolCoreService");
		return service;
	}
	/**
	 * ??????????????? ??????
	 * @return
	 */
	public static DraftPoolQueryService getDraftPoolQueryService(){
		DraftPoolQueryService service = (DraftPoolQueryService)SpringContextUtil.getBean("draftPoolQueryService");
		return service;
	}
	/**
	 * ??????????????? ??????
	 * @return
	 */
	public static DraftPoolInService getDraftPoolInService(){
		DraftPoolInService service = (DraftPoolInService)SpringContextUtil.getBean("draftPoolInService");
		return service;
	}
	/**
	 * ??????????????? ??????
	 * @return
	 */
	public static DraftPoolOutService getDraftPoolOutService(){
		DraftPoolOutService service = (DraftPoolOutService)SpringContextUtil.getBean("draftPoolOutService");
		return service;
	}
	
	/**
	 * ??????????????? ??????
	 * @return
	 */
	public static PoolVtrustService getPoolVtrustService(){
		PoolVtrustService service = (PoolVtrustService)SpringContextUtil.getBean("poolVtrustService");
		return service;
	}
	/**
	 * ???????????? ??????
	 * @return
	 */
	public static BlackListManageService getBlackListManageService(){
		BlackListManageService service = (BlackListManageService)SpringContextUtil.getBean("blackListManageService");
		return service;
	}
	/**
	 * MIS??????????????????
	 * @return
	 */
	public static MisDraftService getMisDraftService(){
		MisDraftService service = (MisDraftService)SpringContextUtil.getBean("misDraftService");
		return service;
	}
	/**
	 * ??????????????????
	 * @param @return   
	 * @return PoolBatchNoUtils  
	 * @author Ju Nana
	 * @date 2019-2-1 ??????10:11:40
	 */
	public static PoolBatchNoUtils getPoolBatchNoUtils(){
		PoolBatchNoUtils service = (PoolBatchNoUtils)SpringContextUtil.getBean("poolBatchNoUtils");
		return service;
	}
	
	/**
	 * ?????????????????????
	 * @param @return   
	 * @return PoolBatchNoUtils  
	 * @author Wu Fengjun
	 * @date 2019-2-22 
	 */
	public static BatchNoUtils getBatchNoUtils(){
		BatchNoUtils service = (BatchNoUtils)SpringContextUtil.getBean("batchNoUtils");
		return service;
	}
//	/**
//	 * ????????????????????????
//	 * @return
//	 *@author ?????????
//	 */
//	public static EndorsementLogService getEndorsementLogService(){
//		EndorsementLogService service = (EndorsementLogService)SpringContextUtil.getBean("endorsementLogService");
//		return service;
//	}
	/**
	 * ????????????????????????
	 * @return PoolCreditProductService  
	 * @author Wu Fengjun
	 * @date 2019-2-13 
	 */
	public static PoolCreditProductService getPoolCreditProductService(){
		PoolCreditProductService service = (PoolCreditProductService)SpringContextUtil.getBean("poolCreditProductService");
		return service;
	}
	/**
	 * ????????????????????????????????????
	 * @return PoolBailEduService  
	 * @author Wu Fengjun
	 * @date 2019-2-13 
	 */
	public static PoolBailEduService getPoolBailEduService(){
		PoolBailEduService service = (PoolBailEduService)SpringContextUtil.getBean("poolBailEduService");
		return service;
	}
	
	/**
	 * ?????????????????????
	 * @param @return   
	 * @return PedAssetPoolService  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-3-3 ??????11:43:08
	 */
	public static PedAssetPoolService getPedAssetPoolService(){
		PedAssetPoolService service = (PedAssetPoolService)SpringContextUtil.getBean("pedAssetPoolService");
		return service;
	}
	
	/**
	 * ???????????????????????????
	 * @param @return   
	 * @return PedAssetTypeService  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-3-4 ??????12:02:29
	 */
	public static PedAssetTypeService getPedAssetTypeService(){
		PedAssetTypeService service = (PedAssetTypeService)SpringContextUtil.getBean("pedAssetTypeService");
		return service;
	}
	/**
	 * ??????????????????
	 * @param @return   
	 * @return EndorsementLogService  
	 * @throws
	 * @author Wu Fengjun
	 * @date 2019-3-5 
	 */
	public static EndorsementLogService getEndorsementLogService(){
		EndorsementLogService service = (EndorsementLogService)SpringContextUtil.getBean("endorsementLogService");
		return service;
	}
	
	/**
	 * ????????????
	 * @return
	 */
	public static DraftPoolDiscountServer getDraftPoolDiscountServer(){
		DraftPoolDiscountServer service = (DraftPoolDiscountServer)SpringContextUtil.getBean("draftPoolDiscountServer");
		return service;
	}
	/**
	 * ??????????????????
	 * @author Ju Nana
	 * @return
	 * @date 2019-6-12??????8:57:59
	 */
	public static PoolEBankService getPoolEBankService(){
		PoolEBankService service = (PoolEBankService)SpringContextUtil.getBean("poolEBankService");
		return service;
	}
	
	/**
	 * ?????????????????????
	 * @author Ju Nana
	 * @return
	 * @date 2019-7-16??????10:35:38
	 */
	public static PoolReportService getPoolReportService(){
		PoolReportService service = (PoolReportService)SpringContextUtil.getBean("poolReportService");
		return service;
	}
	
	/**
	 * mis?????????????????????
	 * @author Ju Nana
	 * @return
	 * @date 2019-11-1??????4:16:44
	 */
	public static PoolCreditClientService getPoolCreditClientService(){
		PoolCreditClientService service = (PoolCreditClientService)SpringContextUtil.getBean("poolCreditClientService");
		return service;
	}
	
	/**
	 * ????????????????????????
	 * @author gcj 20210518
	 * @return
	 */
	public static AutoTaskPublishService getAutoTaskPublishService(){
		AutoTaskPublishService service = (AutoTaskPublishService)SpringContextUtil.getBean("autoTaskPublishService");
		return service;
	}
	/**
	 * ??????????????????
	 * @author gcj 20210528
	 * @return
	 */
	public static OnlineManageService getOnlineManageService(){
		OnlineManageService service = (OnlineManageService)SpringContextUtil.getBean("onlineManageService");
		return service;
	}
	/**
	 * ????????????
	 * @author gcj 20210528
	 * @return
	 */
	public static PoolMssService getPoolMssService(){
		PoolMssService service = (PoolMssService)SpringContextUtil.getBean("poolMssService");
		return service;
	}
	
	/**
	 * ????????????&????????????????????????
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-17??????4:35:18
	 */
	public static AssetCrdtDailyService getAssetCrdtDailyService(){
		AssetCrdtDailyService service = (AssetCrdtDailyService)SpringContextUtil.getBean("assetCrdtDailyService");
		return service;
	}

	/**
	 * ??????????????????
	 * @return
	 * @author gcj
	 * @date 2021-6-28
	 */
	public static PedOnlineAcptService getPedOnlineAcptService(){
		PedOnlineAcptService service = (PedOnlineAcptService)SpringContextUtil.getBean("pedOnlineAcptService");
		return service;
	}
	/**
	 * ??????????????????
	 * @return
	 * @author gcj
	 * @date 2021-6-28
	 */
	public static OnlineCommonService getOnlineCommonService(){
		OnlineCommonService service = (OnlineCommonService)SpringContextUtil.getBean("onlineCommonService");
		return service;
	}
	
	
	/**
	 * ????????????????????????????????????
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-17??????4:35:18
	 */
	public static AutoTaskExeService getAutoTaskExeService(){
		AutoTaskExeService service = (AutoTaskExeService)SpringContextUtil.getBean("autoTaskExeService");
		return service;
	}
	
	/**
	 * ??????????????????
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-30??????10:19:16
	 */
	public static FinancialService getFinancialService(){
		FinancialService service = (FinancialService)SpringContextUtil.getBean("financialService");
		return service;
	}
	
	/**
	 * ????????????????????????
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-30??????10:18:53
	 */
	public static CreditRegisterService getCreditRegisterService(){
		CreditRegisterService service = (CreditRegisterService)SpringContextUtil.getBean("creditRegisterService");
		return service;
	}
	/**
	 * ????????????????????????
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-30??????12:21:14
	 */
	public static AssetRegisterService getAssetRegisterService(){
		AssetRegisterService service = (AssetRegisterService)SpringContextUtil.getBean("assetRegisterService");
		return service;
	}
	
	
	/**
	 * ??????????????????
	 * @return
	 * @author gcj
	 * @date 2021-6-30
	 */
	public static PedOnlineCrdtService getPedOnlineCrdtService(){
		PedOnlineCrdtService service = (PedOnlineCrdtService)SpringContextUtil.getBean("pedOnlineCrdtService");
		return service;
	}
	
	/**
	 * ?????????????????????????????????
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-19??????8:29:40
	 */
	public static FinancialAdviceService getFinancialAdviceService(){
		FinancialAdviceService service = (FinancialAdviceService)SpringContextUtil.getBean("financialAdviceService");
		return service;
	}
	
	/**
	 * ??????????????????
	 * @return
	 * @author Ju Nana
	 * @date 2021-9-26??????3:51:11
	 */
	public static CacheUpdateService getCacheUpdateService(){
		CacheUpdateService service = (CacheUpdateService)SpringContextUtil.getBean("cacheUpdateService");
		return service;
	}
	

}
