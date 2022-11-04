package com.mingtech.application.autotask.taskService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.runmanage.domain.RunState;
import com.mingtech.application.runmanage.service.RunStateService;
import com.mingtech.application.runmanage.service.RunStateServiceFactory;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 日终批量处理，处理非复杂逻辑批量处理任务。
 * @author Ju Nana
 * @version v1.0
 * @date 2021-9-26
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class DayEndBatchProcessTask  extends AbstractAutoTask {

	private static final Logger logger = Logger.getLogger(DayEndBatchProcessTask.class);
	PedProtocolService pedProtocolService =  PoolCommonServiceFactory.getPedProtocolService();
	CacheUpdateService cacheUpdateService = PoolCommonServiceFactory.getCacheUpdateService();
	RunStateService runStateService =RunStateServiceFactory.getRunStateService();
	BlackListManageService blackListManageService = PoolCommonServiceFactory.getBlackListManageService();
	PoolBailEduService poolBailEduService = PoolCommonServiceFactory.getPoolBailEduService();
	
	@Override
	public BooleanAutoTaskResult run() throws Exception {

		
		/*
		 * 任务一：日终切日任务
		 */
		try {
			this.txUpdateWorkDate();
		} catch (Exception e) {
			logger.error("日终切日异常：",e);
		}
				
		/*
		 * 任务二：日终自动续约任务
		 */
		try {
			pedProtocolService.contractExtension();
		} catch (Exception e) {
			logger.error("日终自动续约任务异常：",e);
		}
		
		
		/*
		 * 任务三：日终节假日缓存同步
		 */
		try {
			cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_HOLIDAY);
		} catch (Exception e) {
			logger.error("日终节假日缓存同步任务：",e);
		}
		
		/*
		 * 任务四：日终同步mis高风险行信息
		 */
		try {
			ReturnMessageNew response = blackListManageService.txSynchBankMember(null);
			
			/*
			 * 处理同步完成后的操作
			 */
			blackListManageService.txBankMemberChangedHandle(response);
			
		} catch (Exception e) {
			logger.error("日终同步mis高风险行信息失败：",e);
		}
		
		/*
		 * 任务五：日终保证金余额
		 */
		try {
			List<PedProtocolDto> proList = pedProtocolService.queryProtocolInfo( PoolComm.OPEN_01, null,  null,null, null, null);
			for (int i = 0; i < proList.size(); i++) {
				PedProtocolDto pro = proList.get(i);
				poolBailEduService.txUpdateBailDetail(pro.getPoolAgreement());
				
			}
		} catch (Exception e) {
			logger.error("日终同步保证金余额失败：",e);
		}
		
		
		return new BooleanAutoTaskResult(true);
	}

	/**
	 * 日终自动切日任务
	 * @author Ju Nana
	 * @date 2021-9-26下午3:54:13
	 */
	private void txUpdateWorkDate(){
		
		try{
			RunState rs = runStateService.getSysRunState();
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			Date date = format.parse(format.format(new Date()));
			Date date = DateUtils.getCurrDate();
			rs.setOrgnlSysDt(rs.getCurDate());
			rs.setCurDate(date);
			rs.setWorkDate(date);
			runStateService.txStore(rs);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}

	}

	
	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

	
}
