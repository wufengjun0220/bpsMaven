package com.mingtech.application.pool.edu.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.edu.service.CreditProductService;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 
 * @author Orange
 * 
 * @copyright 北京明润华创科技有限责任公司
 * 
 * @description 信贷产品处理器
 * 
 */
@Controller
public class CreditProductController extends BaseController {
	
	private Logger logger = Logger.getLogger(CreditProductController.class);
	@Autowired
	private PoolCreditProductService poolCreditProductService;// 信贷产品
	
	private String acceptor;
	@Autowired
	private CreditProductService creditProductService;// 信贷产品


	/**
	 * 生成JSON
	 * 
	 * @param page
	 * @param list
	 * @return
	 * @throws Exception
	 */
	protected String toJson(Page page, List list) throws Exception {

		Map jsonMap = new HashMap();
		jsonMap.put("totalProperty", "results," + page.getTotalCount());
		jsonMap.put("root", "rows");
		String json = JsonUtil.fromCollections(list, jsonMap);
		if (!(StringUtil.isNotBlank(json))) {
			json = RESULT_EMPTY_DEFAULT;
		}

		return json;
	}

	
	/**
	 * 保贴人的保贴额度统计
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/posterInformationCount")
	public void posterInformationCount(String acceptor) throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =poolCreditProductService.loadPosterCount(acceptor,page);
			if (list != null && list.size() > 0) {
				page.setTotalCount(list.size());
				int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
				int endIndex = (page.getPageIndex() * page.getPageSize()) > list.size() ? list.size()
						: page.getPageIndex() * page.getPageSize();
				List returnBatchesList = new ArrayList(); // 本次需要显示的批次
				returnBatchesList.addAll(list.subList(startIndex, endIndex));
				Map map = new HashMap();
				map.put("totalProperty", "results," + page.getTotalCount());
				map.put("root", "rows");
				json = JsonUtil.fromCollections(returnBatchesList, map);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
	}
	/**
	 * 保贴人的保贴额度统计明细
	 *  liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryPosterDetail")
	public void queryPosterDetail(String plCommId) throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =creditProductService.loadPosterCountDetail(plCommId,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("系统异常", e);
		}
	}
	/**
	 * 票据池占用保贴额度查询
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryPoolPasteInformation")
	public void queryPoolPasteInformation(String bpsNo,String isGroup) throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =poolCreditProductService.loadPoolPasteount(bpsNo,isGroup,page);
			if (list != null && list.size() > 0) {
				page.setTotalCount(list.size());
				int startIndex = (page.getPageIndex() - 1) * page.getPageSize();
				int endIndex = (page.getPageIndex() * page.getPageSize()) > list.size() ? list.size()
						: page.getPageIndex() * page.getPageSize();
				List returnBatchesList = new ArrayList(); // 本次需要显示的批次
				returnBatchesList.addAll(list.subList(startIndex, endIndex));
				Map map = new HashMap();
				map.put("totalProperty", "results," + page.getTotalCount());
				map.put("root", "rows");
				json = JsonUtil.fromCollections(returnBatchesList, map);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
	}
	/**
	 * 票据池占用保贴额度查询明细
	 *  liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryPoolPasteDetail") 
	public void queryPoolPasteDetail(String plCommId) throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =creditProductService.loadPoolPasteDetail(plCommId,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("系统异常", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	public String getAcceptor() {
		return acceptor;
	}

	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	
}
