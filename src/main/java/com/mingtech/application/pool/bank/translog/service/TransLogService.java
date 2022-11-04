package com.mingtech.application.pool.bank.translog.service;

import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.translog.domain.TransLog;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: 张永超
 * @日期: Dec 8, 2009 2:35:08 PM
 * @描述: [TransLogService] 行内接口日志表
 */
public interface TransLogService extends GenericService {

	public TransLog txStoreRecMsg(byte[] message, ReturnMessageNew request) throws Exception;

	public boolean txUpdateTransLogStatus(String id, boolean b, String remark, String setSeqno, byte[] reqMsg);

	public String getTransLogJSON(TransLog tlog, Date beginDate, Date endDate, Page page) throws Exception;

	public TransLog txStoreSendMsg(String txCode, byte[] msg, ReturnMessageNew request) throws Exception;

	public boolean txUpdateTransLogRec(TransLog tlog, boolean isSuccess, ReturnMessageNew request, String remark)
			throws Exception;

	public List queryTransLog(Date txDate) throws Exception;
	
	/**
	 * 查询系统内报文日志列表
	 * @param mmlog
	 * @param beginDate
	 * @param endDate
	 * @param page
	 * @return
	 */
	public List querySysReportMsgLogList(TransLog log,Date beginDate,Date endDate,Page page);
	
	/**
	 * 更新接受或发送的报文信息
	 * @param tlog
	 * @param msg
	 * @param flag: true 发送时更新log   false  接受时更新log
	 * @return
	 * @throws Exception
	 * @author wfj
	 * @date 2021-8-7
	 */
	public TransLog txUpdateLog(TransLog tlog, byte[] msg, boolean flag) throws Exception;
}
