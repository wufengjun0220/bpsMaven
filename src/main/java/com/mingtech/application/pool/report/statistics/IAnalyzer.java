package com.mingtech.application.pool.report.statistics;

import com.mingtech.application.report.domain.StatisticsResult;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.core.service.GenericService;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-11 下午01:47:17
 * @描述: [IAnalyzer]分析实现接口
 */
public interface IAnalyzer extends GenericService{
	
	/**
	* <p>方法名称: analysis|描述: 按统计结果进行分析</p>
	* @param statisticsResult 统计结果对象
	* @param dept 机构对象
	*/
	public void analysis(StatisticsResult statisticsResult,Department dept) throws Exception;
	
}
