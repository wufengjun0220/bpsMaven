package com.mingtech.application.pool.report.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mingtech.application.pool.report.domain.RReportModel;
import com.mingtech.application.pool.report.domain.ReportFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>ReportDispatchQueue报表分发调度队列</p>
 * @author ice
 * @date 2019年06月12日
 * @version 1.0
 * <p>修改记录</p>
 * ice   新建类    2019年06月12日
 */
public class ReportDispatchQueue {
	private static final Log logger = LogFactory.getLog(ReportDispatchQueue.class);
	/**
	 * 消息发送队列
	 */
	private BlockingQueue<ReportFile> queue = new LinkedBlockingQueue<ReportFile>();//队列-用于存放待处理的电票和票交所的报文
	
	/**
	 * 将待处理报文放入队列
	 * */
	public void putRequest(ReportFile report) {
		long threadId = Thread.currentThread().getId();//线程号
		try {
			queue.put(report);
		} catch (Exception e) {
			logger.error("threadId-"+threadId+"消息入队出错:"+e.getMessage(),e);
		}
	}
	
	/**
	 * 获取待处理消息
	 * */
	public ReportFile take() {
		long threadId = Thread.currentThread().getId();//线程号
		try {
			//从队列中获取消息，等待时间为60S，如果60S没有读到消息，则返回null
			ReportFile report =  queue.poll(60, TimeUnit.SECONDS);
			return report;
		} catch (Exception e) {
			logger.error("threadId-"+threadId+"-读取消息出错-"+e.getMessage(),e);
			return null;
		}
	}
	
}
