package com.mingtech.application.report.statistics;

import java.util.Date;

import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.core.service.GenericService;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-26 下午03:54:09
 * @描述: [ICreateXML]生成XML数据Service
 */
public interface ICreateXML extends GenericService{
	
	/**
	* <p>方法名称: createXML|描述: 生成XML数据</p>
	* @param startDate 开始日期
	* @param endDate 结束日期
	* @param dept 机构对象
	*/
	public ReportForm createXML(Date startDate, Date endDate, Department dept);
	
	
	/**
	* <p>方法名称: createXML|描述: 生成XML数据 日报表</p>
	* @param 统计开始日期
	* @param dept 机构对象
	*/
	public ReportForm createXML(Date currDate, Department dept);
	
}
