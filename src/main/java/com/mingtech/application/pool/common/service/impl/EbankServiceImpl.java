package com.mingtech.application.pool.common.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.pool.common.domain.EbankInfoDto;
import com.mingtech.application.pool.common.service.EbankService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


@Service
public class EbankServiceImpl extends GenericServiceImpl implements EbankService{


	public Class getEntityClass(){
		return EbankInfoDto.class;
	}
	public String getEntityName(){
		return StringUtil.getClass(getEntityClass());
	}
	
	
	/**
	 * 查询json串
	 * 
	 * @param info
	 * @param page
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public String loadEbankJson(EbankInfoDto info, User user, Page page)
			throws Exception {

		List list = this.loadEbankList(info, user, page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
    /**
     * 向前台输出json串,支持模糊查询
     * @param info
     * @param user
     * @param page
     * @return
     * @throws Exception
     */
	public List loadEbankList(EbankInfoDto info, User user, Page page)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select info from EbankInfoDto info where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (info != null) {
			if (!StringUtil.isEmpty(info.getEbankPlCommId())) {
				sb.append(" and info.ebankPlCommId like :ebankPlCommId ");
				keyList.add("ebankPlCommId");
				valueList.add("%"+info.getEbankPlCommId()+"%");
			}
			if (!StringUtil.isEmpty(info.getEbankCustName())) {
				sb.append(" and info.ebankCustName like :ebankCustName ");
				keyList.add("ebankCustName");
				valueList.add("%"+info.getEbankCustName()+"%");
			}
			if (!StringUtil.isEmpty(info.getEbankName())) {
				sb.append(" and info.ebankName like :ebankName ");
				keyList.add("ebankName");
				valueList.add("%"+info.getEbankName()+"%");
			}
		}
		sb.append(" order by ebankCustNum desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
	}

	
	/**
	 * 新增客户名单时使用 
	 * Params：核心客户号 
	 * result：CustomerDto
	 */
	public CustomerDto queryCustomerDtoByEbankParm(String SOrgCode, String custNum) {
		CustomerDto customerDto = new CustomerDto();
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		String hql = "select cd from CustomerDto cd  where cd.SOrgCode=:SOrgCode and cd.custNum=:custNum  ";
		// 组织结构代码
		paramName.add("SOrgCode");
		paramValue.add(SOrgCode);
		// 核心客户号
		paramName.add("custNum");
		paramValue.add(custNum);
		String paramNames[] = (String[]) paramName.toArray(new String[paramName
				.size()]);
		Object paramValues[] = paramValue.toArray();

		List result = this.find(hql, paramNames, paramValues);

		if (result != null && result.size() > 0) {
			customerDto = (CustomerDto) result.get(0);
			return customerDto;
		}
		return null;
	}
	
	
}
