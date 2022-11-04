package com.mingtech.application.pool.report.service;

import java.util.List;

import com.mingtech.application.pool.report.domain.RReportModel;
import com.mingtech.application.pool.report.domain.ReportFile;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: h2
* @描述: [QueryReportCommonService]通用报表查询Service接口类
*/
public interface QueryReportCommonService extends GenericService{



	/**
	 * 报表生成
	 * @param reportFile 报表生成对象
	 * @throws Exception 
	 */
	public void txCreateReportFile(ReportFile reportFile)throws Exception;
	
	/**
	 * 更新报表生成文件状态
	 * @param reportFile 报表生成对象
	 * @parm status 更新状态
	 * @throws Exception 
	 */
	public void txUpdateReportFileStatus(ReportFile reportFile, String status)throws Exception;

	/**
	 * 报表生成结果查询
	 * @param user 当前用户
	 * @author reportFile 查询条件
	 * @param page 分页对象
	 * @throws Exception
	 * List 报表模板
	 */
	public List queryCreateReportResult(User user,RReportModel reportFile,Page page)throws Exception;
	
	/**
	 * 根据id查询报表生成文件信息
	 * @param id 报表生成文件主键
	 * @throws Exception
	 * @return ReportFile 报表生成文件
	 */
	public RReportModel getReportFileById(String id)throws Exception;
}
