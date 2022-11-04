package com.mingtech.application.pool.report.statistics.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.mingtech.application.report.common.ReportUtils;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.report.statistics.ICreateXML;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 创建xml文件轮询的开始类
 * @author shenyang
 * @date 2010-10-09 17:24
 *
 */
public class CreateXMLDateManagerService  extends GenericServiceImpl{

	private static final Logger logger = Logger.getLogger(CreateXMLDateManagerService.class);
	private List createXmlList;
	
	public List getCreateXmlList() {
		return createXmlList;
	}

	public void setCreateXmlList(List createXmlList) {
		this.createXmlList = createXmlList;
	}

	public void txCreateXml(Date startDate,Date endDate) throws Exception{
		if(!createXmlList.isEmpty()){
			
			String bankLevel = ProjectConfig.getInstance().getBankLevel(); // 报表级别
			int size = createXmlList.size();
			String hql = "select dept from Department dept where dept.level = :level ";
			List keyList = new ArrayList(); // 要查询的字段列表
			List valueList = new ArrayList(); // 要查询的值列表
			List deptList = new ArrayList();
			logger.info("-------------------开始生成报表XML数据，开始时间: " + DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIMES3_FORMAT));
			if(bankLevel.equals("3")){ // 支行级别
				keyList.add("level");
				valueList.add(new Integer(bankLevel));
				deptList = this.find(hql, (String[]) keyList.toArray(new String[keyList.size()]), valueList.toArray());
				if(!deptList.isEmpty()){
					this.createXMLByDept(startDate, endDate, deptList);
				}
				valueList = new ArrayList();
				valueList.add(new Integer(2));
				deptList = this.find(hql, (String[]) keyList.toArray(new String[keyList.size()]), valueList.toArray());
				if(!deptList.isEmpty()){
					this.createXMLByDept(startDate, endDate, deptList);
				}
			}else if(bankLevel.equals("2")){ // 分行级别
				keyList.add("level");
				valueList.add(new Integer(bankLevel));
				deptList = this.find(hql, (String[]) keyList.toArray(new String[keyList.size()]), valueList.toArray());
				if(!deptList.isEmpty()){
					this.createXMLByDept(startDate, endDate, deptList);
				}
			}
			logger.info("-------------------生成报表XML数据结束，结束时间: " + DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIMES3_FORMAT));
			
		}
		
	}
	
	/**
	* <p>方法名称: createXMLByDept|描述: 生成报表XML数据</p>
	* @param startDate 开始日期
	* @param endDate 结束日期
	* @param deptList 机构对象列表
	* @throws Exception
	*/
	private void createXMLByDept(Date startDate,Date endDate, List deptList) throws Exception{
		int deptSize = deptList.size();
		Department dept = null;
		int size = createXmlList.size();
		for(int i = 0; i < deptSize; i++){
			//dept = (Department) deptList.get(i);
			for(int j = 0; j < size; j++){
				ICreateXML iCreateXML = (ICreateXML)createXmlList.get(j);
				ReportForm form = iCreateXML.createXML(startDate, endDate,dept);
				this.txStore(form);
			}
		}
		
	}
	
	public Class getEntityClass() {
		return null;
	}

	public String getEntityName() {
		return null;
	}

}
