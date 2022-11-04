package com.mingtech.application.pool.bank.translog.action;

import java.util.Date;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.translog.domain.TransLog;
import com.mingtech.application.pool.bank.translog.service.TransLogService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.action.BaseAction;

public class TransLogAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TransLogAction.class);
	private TransLogService transLogService;
	private TransLog tlog;
	private String beginDate;
	private String endDate;

	public String toTransList() {
		if (null == beginDate || beginDate.trim().length() == 0) {
			beginDate = DateUtils.toString(new Date(),
					DateUtils.ORA_DATES_FORMAT);
		}
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		return SUCCESS;
	}

	/**
	 * <p>
	 * 方法名称: list|描述: 交易列表查询
	 * </p>
	 */
	public void list() {
		if (null == beginDate || beginDate.trim().length() == 0) {
			beginDate = DateUtils.toString(new Date(),
					DateUtils.ORA_DATES_FORMAT);
		}
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		try {
			String json = transLogService.getTransLogJSON(tlog,
					DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT),
					DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT),
					this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.addActionMessage(e.getMessage());
		}
	}

	public TransLog getTlog() {
		return tlog;
	}

	public void setTlog(TransLog tlog) {
		this.tlog = tlog;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setTransLogService(TransLogService transLogService) {
		this.transLogService = transLogService;
	}
}
