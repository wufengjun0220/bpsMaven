package com.mingtech.application.report.service;

import java.util.List;

import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-25 下午03:35:11
 * @描述: [ReportFormService]报表对象Service接口
 */
public interface ReportFormService extends GenericService{
	
	/**
	 * 
	* <p>方法名称: queryReportList|描述: 查询报表JSON数据列表</p>
	* @return
	* @throws Exception
	 */
	public String queryReportJSON( ReportForm reportForm,Page pg,User user) throws Exception;
	
	/**
	 * 
	* <p>方法名称: getDeptList|描述: 查询当前机构及其下属机构，如果没有下属机构则返回本机构列表</p>
	* @param dept 当前用户所在机构
	* @return
	* @throws Exception
	 */
	public List getDeptList(Department dept) throws Exception;
	
}
