package com.mingtech.application.report.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.report.service.ReportFormService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-25 下午03:37:32
 * @描述: [ReportFormServiceImpl]报表Service
 */
public class ReportFormServiceImpl extends GenericServiceImpl implements
		ReportFormService{


	public Class getEntityClass(){
		return ReportForm.class;
	}

	public String getEntityName(){
		return StringUtil.getClass(this.getEntityClass());
	}

	public String queryReportJSON( ReportForm reportForm,Page page,User user) throws Exception{
		List list = this.queryReportList(reportForm, page, user);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}
	
	public List queryReportList( ReportForm reportForm,Page page,User user) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		sb.append("select rf from ReportForm rf where rf.formBusType is not null ");
		
		List keyList = new ArrayList(); 									// 要查询的字段列表
		List valueList = new ArrayList();								// 要查询的值列表		
		if(reportForm != null){
			if(StringUtil.isNotBlank(reportForm.getFormBusType())){//报表业务类型
				sb.append("and rf.formBusType = :formBusType ");
				keyList.add("formBusType");
				valueList.add(reportForm.getFormBusType());
			}
			if(StringUtil.isNotBlank(reportForm.getName())){//报表名称
				sb.append("and rf.name like :name ");
				keyList.add("name");
				valueList.add("%"+reportForm.getName()+"%");
			}
			if(StringUtil.isNotBlank(reportForm.getDraftStuff())){//票据介质类型（纸票、电票）
				sb.append("and rf.draftStuff = :draftStuff ");
				keyList.add("draftStuff");
				valueList.add(reportForm.getDraftStuff());
			}
			if(StringUtil.isNotBlank(reportForm.getFormType())){//报表类型    月度/季度/年度/其他
				sb.append("and rf.formType = :formType ");
				keyList.add("formType");
				valueList.add(reportForm.getFormType());
			}
			if(reportForm.getStatisticEndDate() != null){//报表查询时间
				sb.append("and fillInDate = :fillInDate ");
				keyList.add("fillInDate");
				//valueList.add(DateUtils.getLastDayOfMonth(reportForm.getStatisticEndDate()));
				
				valueList.add(reportForm.getStatisticEndDate());
			}
//			if(StringUtil.isNotBlank(reportForm.getDeptId())){ // 报表机构
//				Department dept = user.getDepartment();
//				if(dept.getLevel() == 1 && StringUtils.equals(dept.getId(), reportForm.getDeptId())){
//					sb.append("and rf.deptId is null ");
//				}else{
//					sb.append("and rf.deptId = :deptId ");
//					keyList.add("deptId");
//					valueList.add(reportForm.getDeptId());
//				}
//			}
		}
		
		sb.append(" order by rf.createTime desc"); //按报表的创建日期由新往后排列
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if(page != null){ // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		}else{
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
		
	}

	public List getDeptList(Department dept) throws Exception {
		List deptList = new ArrayList();
		deptList.add(dept);
		String hql = "";
		List tmpList = new ArrayList();
		if(dept.getLevel() == 1){
			hql = "select dept from Department dept WHERE dept.level != 1 and dept.status = 1 order by dept.level asc ";
			tmpList = this.find(hql);
		}else{
			hql = "select dept from Department dept WHERE dept.parent = ? and dept.status = 1 order by dept.level asc ";
			List params = new ArrayList();
			params.add(dept.getId());
			tmpList = this.find(hql, params);
		}
		if(!tmpList.isEmpty()){
			deptList.addAll(tmpList);
		}
		return deptList;
		
	}


	
	
	
	
	
	
	
	
	
	
	
	
}
