package com.mingtech.application.pool.bank.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.ReturnMessage;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

public abstract class PJCHandlerAdapter extends AbstractRequestHandler {

	private static final Logger logger = Logger.getLogger(PJCHandlerAdapter.class);

	@Override
	public ReturnMessageNew txHandleRequest(String code, ReturnMessage request) throws Exception {
		return null;
	}

	protected String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}

	protected Date getDateVal(Object obj) throws Exception {
		Date value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
			}
		}
		return value;
	}
	
	protected Date getDateVal2(Object obj) throws Exception {
		Date value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				if(temp.contains("-")){
					temp = temp.replaceAll("-", "");
				}
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
			}
		}
		return value;
	}

	protected BigDecimal getBigDecimalVal(Object obj) throws Exception {
		BigDecimal value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = new BigDecimal(temp);
			}
		}
		return value;
	}

	public Page getPage(Map map) throws Exception {
		Page page = null;
		try {
			page = new Page();
			// 要求每页返回记录总数PER_PAGE_NUM
			if (StringUtil.isNotBlank(getStringVal(map.get("PER_PAGE_NUM")))) {
				int limit = Integer.parseInt(getStringVal(map.get("PER_PAGE_NUM")));
				page.setPageSize(limit);
			}
			// 根据QUERY_KEY计算页号，默认为第一页
			if (StringUtil.isNotBlank(getStringVal(map.get("QUERY_KEY")))) {
				// 记录起始位置
				int pageIndex = Integer.parseInt(getStringVal(map.get("QUERY_KEY")));
				page.setPageIndex(pageIndex);//当前页数
			} else {
				page.setPageIndex(1);
			}
			logger.debug("分页参数="+page+",当前页="+page.getPageIndex());
			return page;
		} catch (Exception e) {
			throw new Exception("分页发生异常!!!", e);
		}
	}

	public void setPage(Map appHead, Page page) throws Exception {
		try {
			logger.debug("分页参数="+page+",当前页="+page.getPageIndex());
			int totalNum = page.getPageSize();// 默认为页大小
			if (page.getPageCount() == 1) {
				// 只有一页
				totalNum = (int)page.getTotalCount();
			}
			// 为最后页时
			if(page.getPageIndex() == page.getPageCount()) {
				totalNum = (int) (page.getTotalCount() % page.getPageSize());
			}
			appHead.put("TOTAL_NUM", totalNum); // 本页记录总数
			appHead.put("CURRENT_NUM", page.getFirstResult()); // 当前记录号
			appHead.put("TOTAL_PAGES", page.getPageCount()); // 总页数
			appHead.put("TOTAL_ROWS", page.getTotalCount()); // 总笔数
		} catch (Exception e) {
			throw new Exception("分页发生异常!!!", e);
		}
	}
	public void setPage(Map appHead, Page page,String totalAmt) throws Exception {
		try {
			logger.debug("分页参数="+page+",当前页="+page.getPageIndex());
			int totalNum = page.getPageSize();// 默认为页大小
			if (page.getPageCount() == 1) {
				// 只有一页
				totalNum = (int)page.getTotalCount();
			}
			// 为最后页时
			if(page.getPageIndex() == page.getPageCount()) {
				totalNum = (int) (page.getTotalCount() % page.getPageSize());
			}
			appHead.put("TOTAL_NUM", totalNum == 0 ? 0 : totalNum); // 本页记录总数
			appHead.put("CURRENT_NUM", page.getFirstResult()); // 当前记录号
			appHead.put("TOTAL_PAGES", page.getPageCount()); // 总页数
			appHead.put("TOTAL_ROWS", page.getTotalCount() == 0 ? 0 : page.getTotalCount()); // 总笔数
			appHead.put("TOTAL_AMT",totalAmt); // 总金额

		} catch (Exception e) {
			throw new Exception("分页发生异常!!!", e);
		}
	}
}
