package com.mingtech.application.pool.report.monitor;


import java.util.ArrayList;
import java.util.List;

import com.mingtech.application.pool.report.queue.ReportDispatchQueue;
import com.mingtech.application.pool.report.service.QueryReportCommonService;
import org.apache.log4j.Logger;

/**
 * <p>MessageDispatchServer报文分发调度服务</p>
 * @author ice
 * @date 2019年06月12日
 * @version 1.0
 * <p>修改记录</p>
 * ice   新建类    2019年06月12日
 */
public class ReportTaskMonitor {
	private static final Logger logger = Logger.getLogger(ReportTaskMonitor.class);
	private ReportDispatchQueue reportDispatchQueue;
	private QueryReportCommonService queryReportCommonService;
	List<ReportExecThread> threadList = new ArrayList<ReportExecThread>();
	public void start(){
		for(int i=1; i <=this.threads; i++){
			ReportExecThread reportExecThread = new ReportExecThread();
			reportExecThread.setReportDispatchQueue(reportDispatchQueue);
			reportExecThread.setQueryReportCommonService(queryReportCommonService);
			reportExecThread.start();
			threadList.add(reportExecThread);
			logger.info("ReportExecThread thread 第" + i + "个报表生成线程启动完成" );
		}
		logger.info("ReportExecThread running...");
		
	}
	
	
	public void stop() throws Exception{
		logger.info("MessageDispatchServer stop...");
		for(ReportExecThread thread : threadList){
			try{
				if(thread!=null && thread.isAlive()){
					logger.info(thread.getName()+" stop...");
					thread.setStopThread(true);
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		
	}
	
	
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	private int threads=1;//线程数
	
	public void setReportDispatchQueue(ReportDispatchQueue reportDispatchQueue) {
		this.reportDispatchQueue = reportDispatchQueue;
	}


	public void setQueryReportCommonService(
			QueryReportCommonService queryReportCommonService) {
		this.queryReportCommonService = queryReportCommonService;
	}
	
	
}
