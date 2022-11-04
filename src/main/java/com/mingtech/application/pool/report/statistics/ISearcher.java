package com.mingtech.application.pool.report.statistics;

import java.util.Date;
import java.util.List;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.core.service.GenericService;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-11 下午01:42:10
 * @描述: [ISearcher]查询实现接口
 */
public interface ISearcher extends GenericService{
	
	/**
	* <p>方法名称: seracher|描述: 按日期查询</p>
	* @param startDate 开始日期
	* @param endDate 结束日期
	* @param dept 机构对象
	* @param bankLevel 机构级别
	* @return
	*/
	public List seracher(Date startDate, Date endDate, Department dept, String bankLevel);
	
}
