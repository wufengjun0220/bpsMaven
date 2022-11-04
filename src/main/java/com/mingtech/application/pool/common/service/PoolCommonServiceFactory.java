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
 * 票据池任务 服务工厂
 * @author Administrator
 *
 */
public class PoolCommonServiceFactory {
	/**
	 * 签约协议 服务
	 * @return
	 */
	public static PedProtocolService getPedProtocolService(){
		PedProtocolService service = (PedProtocolService)SpringContextUtil.getBean("pedProtocolService");
		return service;
	}
	
	/**
	 * 票据池电票接口 服务
	 * @return
	 */
	public static PoolEcdsService getPoolEcdsService(){
		PoolEcdsService service = (PoolEcdsService)SpringContextUtil.getBean("poolEcdsService");
		return service;
	}
	
	/**
	 * 票据池信贷接口 服务
	 * @return
	 */
	public static PoolCreditService getPoolCreditService(){
		PoolCreditService service = (PoolCreditService)SpringContextUtil.getBean("poolCreditService");
		return service;
	}
	
	/**
	 * 票据池核心接口 服务
	 * @return
	 */
	public static PoolCoreService getPoolCoreService(){
		PoolCoreService service = (PoolCoreService)SpringContextUtil.getBean("poolCoreService");
		return service;
	}
	/**
	 * 票据池查询 服务
	 * @return
	 */
	public static DraftPoolQueryService getDraftPoolQueryService(){
		DraftPoolQueryService service = (DraftPoolQueryService)SpringContextUtil.getBean("draftPoolQueryService");
		return service;
	}
	/**
	 * 票据池入池 服务
	 * @return
	 */
	public static DraftPoolInService getDraftPoolInService(){
		DraftPoolInService service = (DraftPoolInService)SpringContextUtil.getBean("draftPoolInService");
		return service;
	}
	/**
	 * 票据池出池 服务
	 * @return
	 */
	public static DraftPoolOutService getDraftPoolOutService(){
		DraftPoolOutService service = (DraftPoolOutService)SpringContextUtil.getBean("draftPoolOutService");
		return service;
	}
	
	/**
	 * 虚拟票据池 服务
	 * @return
	 */
	public static PoolVtrustService getPoolVtrustService(){
		PoolVtrustService service = (PoolVtrustService)SpringContextUtil.getBean("poolVtrustService");
		return service;
	}
	/**
	 * 风险检查 服务
	 * @return
	 */
	public static BlackListManageService getBlackListManageService(){
		BlackListManageService service = (BlackListManageService)SpringContextUtil.getBean("blackListManageService");
		return service;
	}
	/**
	 * MIS票据处理服务
	 * @return
	 */
	public static MisDraftService getMisDraftService(){
		MisDraftService service = (MisDraftService)SpringContextUtil.getBean("misDraftService");
		return service;
	}
	/**
	 * 编号生成服务
	 * @param @return   
	 * @return PoolBatchNoUtils  
	 * @author Ju Nana
	 * @date 2019-2-1 上午10:11:40
	 */
	public static PoolBatchNoUtils getPoolBatchNoUtils(){
		PoolBatchNoUtils service = (PoolBatchNoUtils)SpringContextUtil.getBean("poolBatchNoUtils");
		return service;
	}
	
	/**
	 * 批次号生成服务
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
//	 * 票据背面信息服务
//	 * @return
//	 *@author 吴冯俊
//	 */
//	public static EndorsementLogService getEndorsementLogService(){
//		EndorsementLogService service = (EndorsementLogService)SpringContextUtil.getBean("endorsementLogService");
//		return service;
//	}
	/**
	 * 借据信贷产品服务
	 * @return PoolCreditProductService  
	 * @author Wu Fengjun
	 * @date 2019-2-13 
	 */
	public static PoolCreditProductService getPoolCreditProductService(){
		PoolCreditProductService service = (PoolCreditProductService)SpringContextUtil.getBean("poolCreditProductService");
		return service;
	}
	/**
	 * 获取删除保证金当日表服务
	 * @return PoolBailEduService  
	 * @author Wu Fengjun
	 * @date 2019-2-13 
	 */
	public static PoolBailEduService getPoolBailEduService(){
		PoolBailEduService service = (PoolBailEduService)SpringContextUtil.getBean("poolBailEduService");
		return service;
	}
	
	/**
	 * 票据池额度服务
	 * @param @return   
	 * @return PedAssetPoolService  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-3-3 下午11:43:08
	 */
	public static PedAssetPoolService getPedAssetPoolService(){
		PedAssetPoolService service = (PedAssetPoolService)SpringContextUtil.getBean("pedAssetPoolService");
		return service;
	}
	
	/**
	 * 票据池额度明细服务
	 * @param @return   
	 * @return PedAssetTypeService  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-3-4 上午12:02:29
	 */
	public static PedAssetTypeService getPedAssetTypeService(){
		PedAssetTypeService service = (PedAssetTypeService)SpringContextUtil.getBean("pedAssetTypeService");
		return service;
	}
	/**
	 * 背面信息服务
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
	 * 贴现服务
	 * @return
	 */
	public static DraftPoolDiscountServer getDraftPoolDiscountServer(){
		DraftPoolDiscountServer service = (DraftPoolDiscountServer)SpringContextUtil.getBean("draftPoolDiscountServer");
		return service;
	}
	/**
	 * 网银接口服务
	 * @author Ju Nana
	 * @return
	 * @date 2019-6-12下午8:57:59
	 */
	public static PoolEBankService getPoolEBankService(){
		PoolEBankService service = (PoolEBankService)SpringContextUtil.getBean("poolEBankService");
		return service;
	}
	
	/**
	 * 票据池报表服务
	 * @author Ju Nana
	 * @return
	 * @date 2019-7-16上午10:35:38
	 */
	public static PoolReportService getPoolReportService(){
		PoolReportService service = (PoolReportService)SpringContextUtil.getBean("poolReportService");
		return service;
	}
	
	/**
	 * mis系统客户端服务
	 * @author Ju Nana
	 * @return
	 * @date 2019-11-1下午4:16:44
	 */
	public static PoolCreditClientService getPoolCreditClientService(){
		PoolCreditClientService service = (PoolCreditClientService)SpringContextUtil.getBean("poolCreditClientService");
		return service;
	}
	
	/**
	 * 自动任务发布服务
	 * @author gcj 20210518
	 * @return
	 */
	public static AutoTaskPublishService getAutoTaskPublishService(){
		AutoTaskPublishService service = (AutoTaskPublishService)SpringContextUtil.getBean("autoTaskPublishService");
		return service;
	}
	/**
	 * 线上业务服务
	 * @author gcj 20210528
	 * @return
	 */
	public static OnlineManageService getOnlineManageService(){
		OnlineManageService service = (OnlineManageService)SpringContextUtil.getBean("onlineManageService");
		return service;
	}
	/**
	 * 短信服务
	 * @author gcj 20210528
	 * @return
	 */
	public static PoolMssService getPoolMssService(){
		PoolMssService service = (PoolMssService)SpringContextUtil.getBean("poolMssService");
		return service;
	}
	
	/**
	 * 每日资产&融资业务处理服务
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-17下午4:35:18
	 */
	public static AssetCrdtDailyService getAssetCrdtDailyService(){
		AssetCrdtDailyService service = (AssetCrdtDailyService)SpringContextUtil.getBean("assetCrdtDailyService");
		return service;
	}

	/**
	 * 在线银承服务
	 * @return
	 * @author gcj
	 * @date 2021-6-28
	 */
	public static PedOnlineAcptService getPedOnlineAcptService(){
		PedOnlineAcptService service = (PedOnlineAcptService)SpringContextUtil.getBean("pedOnlineAcptService");
		return service;
	}
	/**
	 * 在线业务服务
	 * @return
	 * @author gcj
	 * @date 2021-6-28
	 */
	public static OnlineCommonService getOnlineCommonService(){
		OnlineCommonService service = (OnlineCommonService)SpringContextUtil.getBean("onlineCommonService");
		return service;
	}
	
	
	/**
	 * 业务自动处理登记接口服务
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-17下午4:35:18
	 */
	public static AutoTaskExeService getAutoTaskExeService(){
		AutoTaskExeService service = (AutoTaskExeService)SpringContextUtil.getBean("autoTaskExeService");
		return service;
	}
	
	/**
	 * 额度计算服务
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-30上午10:19:16
	 */
	public static FinancialService getFinancialService(){
		FinancialService service = (FinancialService)SpringContextUtil.getBean("financialService");
		return service;
	}
	
	/**
	 * 用信业务登记服务
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-30上午10:18:53
	 */
	public static CreditRegisterService getCreditRegisterService(){
		CreditRegisterService service = (CreditRegisterService)SpringContextUtil.getBean("creditRegisterService");
		return service;
	}
	/**
	 * 资产业务登记服务
	 * @return
	 * @author Ju Nana
	 * @date 2021-6-30下午12:21:14
	 */
	public static AssetRegisterService getAssetRegisterService(){
		AssetRegisterService service = (AssetRegisterService)SpringContextUtil.getBean("assetRegisterService");
		return service;
	}
	
	
	/**
	 * 在线流贷服务
	 * @return
	 * @author gcj
	 * @date 2021-6-30
	 */
	public static PedOnlineCrdtService getPedOnlineCrdtService(){
		PedOnlineCrdtService service = (PedOnlineCrdtService)SpringContextUtil.getBean("pedOnlineCrdtService");
		return service;
	}
	
	/**
	 * 资产及额度处理独立事务
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-19上午8:29:40
	 */
	public static FinancialAdviceService getFinancialAdviceService(){
		FinancialAdviceService service = (FinancialAdviceService)SpringContextUtil.getBean("financialAdviceService");
		return service;
	}
	
	/**
	 * 缓存更新服务
	 * @return
	 * @author Ju Nana
	 * @date 2021-9-26下午3:51:11
	 */
	public static CacheUpdateService getCacheUpdateService(){
		CacheUpdateService service = (CacheUpdateService)SpringContextUtil.getBean("cacheUpdateService");
		return service;
	}
	

}
