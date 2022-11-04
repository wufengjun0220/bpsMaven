package com.mingtech.application.pool.report.statistics;

import java.util.Date;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.core.service.GenericService;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-11 下午02:06:58
 * @描述: [IStatisticsProcessor]统计实现接口
 */
public interface IStatisticsProcessor extends GenericService{
	
	/**
	* <p>方法名称: statistics|描述: 统计方法</p>
	* @param type 统计类型
	* @param startDate 开始日期
	* @param endDate 结束日期
	* @param dept 机构对象
	* @param bankLevel 报表级别
	*/
	public void statistics(String type, Date startDate, Date endDate, Department dept, String bankLevel) throws Exception;
	
}
