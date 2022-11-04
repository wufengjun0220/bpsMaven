package com.mingtech.application.sysmanage.web;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.sysmanage.domain.Log;
import com.mingtech.application.sysmanage.service.LogService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;
@Controller
public class LogController extends BaseController{
	private static final Logger logger = Logger.getLogger(LogController.class);
	@Autowired
	private LogService logService;
	
	/**
	 * 方法说明: 系统交易日志信息查询
	 * @param log 日志信息查询条件
	 * @param beginDate开始日期
	 * @param endDate结束日期
	 * @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping(value="/listLog",method = RequestMethod.POST)
	public void listLog(Log log,Date beginDate,Date endDate){
		try {
			Page page=this.getPage();
			List result = logService.queryLogList(this.getCurrentUser(),log,beginDate,endDate,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
}
