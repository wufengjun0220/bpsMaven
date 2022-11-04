package com.mingtech.application.autotask.taskService;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.edu.service.PoolBailEduService;

/**
 * 
 * @Title: 保证金当日查询
 * @Description: 
 * @author Wu Fengjun
 * @date 2018-11-8
 */
public class AutomarginAccountTodayTask  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutomarginAccountTodayTask.class);
	BlackListManageService blackListManageService  = PoolCommonServiceFactory.getBlackListManageService();
	PoolBailEduService bailEduService = PoolCommonServiceFactory.getPoolBailEduService();
	/**
	 * （1）删除表中数据重新插入
	 * 
	 * （2）
	 */
	public BooleanAutoTaskResult run() throws Exception {
		logger.info("保证金当日流水查询开始....");
		try {
			bailEduService.txBailQueryFromCore(null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return new BooleanAutoTaskResult(true);
	}
	

	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}





	
}
