package com.mingtech.application.pool.bank.netbanksys.handler.handlermapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.ReturnMessage;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryParameter;

public class QueryParamMapping {
	public static QueryParameter QueryParamMap(ReturnMessage request) {
		// 构建查询对象
		QueryParameter params = new QueryParameter(); // 新建查询对象
		Map headers = request.getHead();

		params.setQueryType(((String) headers.get(Constants.CXLX))); // 查询种类
		params.setBillType((String) headers.get(Constants.PJZL)); // 票据类型
		params.setBillMedia((String) headers.get(Constants.PJJZ)); // 票据介质
		params.setIssueDateBegin((Date) headers.get(Constants.CPRS)); // 出票日开始
		params.setIssueDateEnd((Date) headers.get(Constants.CPRE)); // 出票日结束
		params.setDueDateBegin((Date) headers.get(Constants.DQRS)); // 票据到期日开始
		params.setDueDateEnd((Date) headers.get(Constants.DQRE)); // 票据到期日结束
		params.setBillAmountBegin((BigDecimal) headers.get(Constants.PMJEXX)); // 票据金额开始
		params.setBillAmountEnd((BigDecimal) headers.get(Constants.PMJESX)); // 票据金额结束
		params.setBillNo((String) headers.get(Constants.PJHM)); // 票据号码

		List numberList = new ArrayList();
		List list = request.getDetails();
		if (list != null && list.size() > 0) {
			Map map = null;
			for (Iterator<Map> iterator = list.iterator(); iterator.hasNext();) {
				map = iterator.next();
				// 得到明细文件的值
				String zhanghao = (String) map.get("QYZH"); // 企业账号
				numberList.add(zhanghao);
			}
		}
		params.setAccounts(numberList);
		return params;
	}
	
	public static QueryParameter QueryParamMap(ReturnMessageNew request) {
		// 构建查询对象
		QueryParameter params = new QueryParameter(); // 新建查询对象
		Map headers = request.getBody();

		params.setQueryType(((String) headers.get(Constants.CXLX))); // 查询种类
		params.setBillType((String) headers.get(Constants.PJZL)); // 票据类型
		params.setBillMedia((String) headers.get(Constants.PJJZ)); // 票据介质
		params.setIssueDateBegin((Date) headers.get(Constants.CPRS)); // 出票日开始
		params.setIssueDateEnd((Date) headers.get(Constants.CPRE)); // 出票日结束
		params.setDueDateBegin((Date) headers.get(Constants.DQRS)); // 票据到期日开始
		params.setDueDateEnd((Date) headers.get(Constants.DQRE)); // 票据到期日结束
		params.setBillAmountBegin((BigDecimal) headers.get(Constants.PMJEXX)); // 票据金额开始
		params.setBillAmountEnd((BigDecimal) headers.get(Constants.PMJESX)); // 票据金额结束
		params.setBillNo((String) headers.get(Constants.PJHM)); // 票据号码
		
		List numberList = new ArrayList();
		List list = request.getDetails();
		if (list != null && list.size() > 0) {
			Map map = null;
			for (Iterator<Map> iterator = list.iterator(); iterator.hasNext();) {
				map = iterator.next();
				// 得到明细文件的值
				String zhanghao = (String) map.get("QYZH"); // 企业账号
				numberList.add(zhanghao);
			}
		}
		params.setAccounts(numberList);

		return params;
	}
}
