package com.mingtech.application.pool.bank.translog.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.pool.bank.hkb.HKBConstants;
import com.mingtech.application.pool.bank.translog.domain.TransLog;
import com.mingtech.application.pool.bank.translog.service.TransLogService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.web.PedProtocolController;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

@Controller
public class TransLogController extends BaseController {

	private static final Logger logger = Logger.getLogger(PedProtocolController.class);
	@Autowired
	private TransLogService transLogService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("tlog.");    
	}

	@RequestMapping("/toTransList")
	public String toTransList(Model mode) {
		String beginDate = DateUtils.toString(new Date(), DateUtils.ORA_DATES_FORMAT);
		String endDate = beginDate;
		mode.addAttribute("beginDate", beginDate);
		mode.addAttribute("endDate", endDate);
		return "/pool/transLog/transLogList";
	}

	/**
	 * <p>
	 * 方法名称: list|描述: 交易列表查询
	 * </p>
	 */
	@RequestMapping("/transLogList")
	public void list(TransLog tlog, String beginDate, String endDate) {
		if (null == beginDate || beginDate.trim().length() == 0) {
			beginDate = DateUtils.toString(new Date(), DateUtils.ORA_DATES_FORMAT);
		}
		if (null == endDate || endDate.trim().length() == 0) {
			endDate = beginDate;
		}
		try {

			Page page = this.getPage();
			
			List result = transLogService.querySysReportMsgLogList(tlog,DateUtils.getCurrentDayStartDate(DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT)),
					DateUtils.getCurrentDayEndDate(DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT)), page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
	        if(StringUtil.isBlank(json)){
	         json = this.RESULT_EMPTY_DEFAULT;
	        }
			this.sendJSON(json);
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
	}

	@RequestMapping("/viewTransLog")
	public String viewTransLog(String id, Model mode) {
		try {
			if (StringUtil.isNotEmpty(id)) {
				TransLog tl = (TransLog) transLogService.load(id);
				if (tl != null) {
					String msgContent = new String(tl.getMsgContent(), HKBConstants.ENCODING);
					mode.addAttribute("tlog", tl);
					mode.addAttribute("msgContent", "<xmp>"+msgContent+"</xmp>");

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.toString(),e);
		}
		return "/pool/transLog/viewTransLog";
	}

}
