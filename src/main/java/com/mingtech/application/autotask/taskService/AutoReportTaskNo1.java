package com.mingtech.application.autotask.taskService;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.report.service.PoolReportService;

/**
 * 票据池报表生成
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-15
 */
public class AutoReportTaskNo1  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoReportTaskNo1.class);
	
	PoolReportService poolReportService =  PoolCommonServiceFactory.getPoolReportService();

	public BooleanAutoTaskResult run() throws Exception {
		
		logger.info("票据池报表生成......开始......");
		poolReportService.txCreatPoolReportInfo();
		logger.info("票据池报表生成.... 完成......");
		
		
		logger.info("票据池【融资业务】报表生成......");
		List<String> busiTypes = new ArrayList<String>();
    	busiTypes.add(PoolComm.XD_01);//银承
    	busiTypes.add(PoolComm.XD_02);//流贷
    	busiTypes.add(PoolComm.XD_03);//保函
    	busiTypes.add(PoolComm.XD_04);//信用证
//    	busiTypes.add(PoolComm.XD_05);//表外业务垫款：该部分业务会统计到原发生的融资业务中去
    	busiTypes.add(PoolComm.XD_06);//全部融资业务
    	
    	for(String busiType : busiTypes){    		
    		poolReportService.txCreatCreditReportInfo(busiType);
    	}
		logger.info("票据池【融资业务】报表生成....完成....");
		
		return new BooleanAutoTaskResult(true);
	}
	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	

	
}
