package com.mingtech.application.pool.report.service;

import com.mingtech.application.pool.report.domain.*;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 票据池报表功能服务
 * @Description 
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-15
 */
public interface ReportModelService extends GenericService{

	/**
	 *
	 * <p>方法名称: queryReportList|描述: 查询报表模板JSON数据列表</p>
	 * @return
	 * @throws Exception
	 */
	public String queryReportModelJSON(RReportModel reportForm, Page pg, User user) throws Exception;
	
	public List queryReportModelList(RReportModel rReportModel, Page page, User user) throws Exception;
	/**
	 *
	 * <p>方法名称: queryReportList|描述: 查询报表模板JSON数据列表</p>
	 * @return
	 * @throws Exception
	 */
	public String queryReportFileJSON(ReportFile reportForm, Page pg, User user) throws Exception;
	/**
	 *根据业务类型查询list
	 */
	public List findBusiList(ReportFile reportForm) throws  Exception;
	/**
	 *根据业务类型组装初始数据list
	 */
	public List<ReportModelAmtBean> findBusiModelList(String busiType) throws  Exception;
	/**
	 *根据业务类型查询报表头
	 */
	public List<Map> findReportHeads(String busiType) throws  Exception;
	/**
	 * 模板上传
	 */
	public void txUploadReportModel(CommonsMultipartFile file, User user, String remark) throws Exception;
	/**
	 * 保存通用报表文件对象
	 * @param user 当前用户
	 * @return RReportModel 报表生成对象
	 * @throws Exception
	 */
	public ReportFile txSaveCommonReportFile(User user,String id,String timeModel,String timeSelect)throws Exception;
	/**
	 * 根据id查询报表生成文件信息
	 * @param id 报表生成文件主键
	 * @throws Exception
	 * @return ReportFile 报表生成文件
	 */
	public RReportModel getReportFileById(String id)throws Exception;

}
