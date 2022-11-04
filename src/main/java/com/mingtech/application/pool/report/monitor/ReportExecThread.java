package com.mingtech.application.pool.report.monitor;


import com.mingtech.application.pool.report.domain.RReportModel;
import com.mingtech.application.pool.report.domain.ReportFile;
import com.mingtech.application.pool.report.queue.ReportDispatchQueue;
import com.mingtech.application.pool.report.service.QueryReportCommonService;
import org.apache.log4j.Logger;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;


/**
 * <p>报表执行线程</p>
 * @author ice
 * @date 2016年9月4日
 * @version 1.0
 * <p>修改记录</p>
 * ice   新建类    2016年9月4日
 */
public class ReportExecThread extends Thread {
	private static final Logger logger = Logger.getLogger(ReportExecThread.class);

	//存放待处理报表的队列
	private ReportDispatchQueue reportDispatchQueue;
	private QueryReportCommonService queryReportCommonService;
	//自定义线程编号
	private boolean stopThread=false;
	
	@Override
	public void run() {//监听//消费者
		long threadId = Thread.currentThread().getId();//线程号
		logger.info("系统线程号threadId="+threadId);
		while(!stopThread) {
			ReportFile reportFile = reportDispatchQueue.take();
			if(reportFile != null) {
				try{
					//先将报表文件状态更新为处理中
					queryReportCommonService.txUpdateReportFileStatus(reportFile,PublicStaticDefineTab.Report_STATUS_3);
					//生成报表
					queryReportCommonService.txCreateReportFile(reportFile);
				}catch(Exception e){
					try {
						queryReportCommonService.txUpdateReportFileStatus(reportFile,PublicStaticDefineTab.Report_STATUS_2);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					logger.error(e.getMessage(),e);
					logger.error("threadId-"+threadId+"-报表生成失败【reportSeqNo="+reportFile.getReportName()
							+",reportName="+reportFile.getReportName()+"】-"+e.getMessage(),e);
				}
			}
		}
		
	}

	public boolean isStopThread() {
		return stopThread;
	}

	public void setStopThread(boolean stopThread) {
		this.stopThread = stopThread;
	}


	public void setQueryReportCommonService(
			QueryReportCommonService queryReportCommonService) {
		this.queryReportCommonService = queryReportCommonService;
	}

	public void setReportDispatchQueue(ReportDispatchQueue reportDispatchQueue) {
		this.reportDispatchQueue = reportDispatchQueue;
	}
	
}
