package com.mingtech.application.pool.report.service;

import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.report.domain.RCreditReportInfo;
import com.mingtech.application.pool.report.domain.RCreditReportInfoBean;
import com.mingtech.application.pool.report.domain.RPoolReportInfo;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


/**
 * 票据池报表功能服务
 * @Description 
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-15
 */
public interface PoolReportService extends GenericService{
	
	
	/**
	 * 票据池报表生成
	 * @Description 
	 * @author Ju Nana
	 * @throws Exception
	 * @date 2019-7-15下午2:14:18
	 */
	public void txCreatPoolReportInfo() throws Exception;  
	
	/**
	 * 票据池融资业务报表生成
	 * @Description 
	 * @author Ju Nana
	 * @throws Exception
	 * @date 2019-7-15下午2:15:17
	 */
	public void txCreatCreditReportInfo(String busiType) throws Exception;  
	
	/**
	 * 
	* <p>方法名称: getDeptList|描述: 查询当前机构及其下属机构，如果没有下属机构则返回本机构列表</p>
	* @param dept 当前用户所在机构
	* @return
	* @throws Exception
	 */
	public List getDeptList(Department dept) throws Exception;
	
	/**
	 * 
	* <p>方法名称: queryReportList|描述: 查询报表JSON数据列表</p>
	* @return
	* @throws Exception
	 */
	public String queryReportJSON( ReportForm reportForm,Page pg,User user) throws Exception;
	
	/**
	 * 
	* <p>方法名称: queryRPoolReportInfoJSON|描述: 查询报表JSON数据列表</p>
	* @return
	* @throws Exception
	 */
	public List<RPoolReportInfo> queryRPoolReportInfoJSON( RPoolReportInfo rPoolReportInfo, Date beginDate, Date endDate,Page pg,User user,String flag) throws Exception;
	
	/**
	 * 
	* <p>方法名称: queryReportList|描述: 查询报表JSON数据列表</p>
	* @return
	* @throws Exception
	 */
	public List<RCreditReportInfoBean> queryreportFinanceJSON( RCreditReportInfoBean rCreditReportInfo, Date beginDate, Date endDate,Page pg,User user) throws Exception;
	/**
	 * id
	 * @param orgcode
	 * @return
	 * @throws Exception
	 * @author liuxiaodong
	 */
	public RPoolReportInfo queryReportPoolById(String id);
	/**
	 * 
	 * 批量导出 票据池
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findReportPoolByBeanExpt(List res, Page page) throws Exception;
	/**
	 * 
	 * 批量导出 票据池
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findReportPoolByBeanAmtExpt(List res, Page page) throws Exception;
	/**
	 * 
	 * 批量导出 票据池
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findReportPoolByBeanBoExpt(List res, Page page) throws Exception;
	
	/**
	 * id
	 * @param orgcode
	 * @return
	 * @throws Exception
	 * @author liuxiaodong
	 */
	public RCreditReportInfo queryRCreditReportById(String id);
	
	/**
	 * 
	 * 批量导出 票据池
	 * 
	 * @param res
	 * @param page
	 * @return
	 */
	public List findRCreditReportExpt(List res, Page page) throws Exception;
}
