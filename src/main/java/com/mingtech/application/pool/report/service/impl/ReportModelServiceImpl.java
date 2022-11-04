package com.mingtech.application.pool.report.service.impl;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.report.domain.*;
import com.mingtech.application.pool.report.service.ReportModelService;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.utils.ExcelUtil;
import com.mingtech.application.utils.FTPUtils;
import com.mingtech.framework.common.util.*;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 票据池报表功能实现服务
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-15
 */
@Service("reportModelService")
public class ReportModelServiceImpl extends GenericServiceImpl implements ReportModelService {
	private static final Logger logger = Logger.getLogger(ReportModelService.class);
	@Autowired
	DepartmentService departmentService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	public Class getEntityClass(){
		return ReportForm.class;
	}

	public String getEntityName(){
		return StringUtil.getClass(this.getEntityClass());
	}

	@Override
	public String queryReportModelJSON( RReportModel reportForm,Page page,User user) throws Exception{
		List list = this.queryReportModelList(reportForm, page, user);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}
	@Override
	public List queryReportModelList(RReportModel rReportModel, Page page, User user) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("select rf from RReportModel rf where 1=1 ");
		List keyList = new ArrayList(); 									// 要查询的字段列表
		List valueList = new ArrayList();								// 要查询的值列表
		if(rReportModel != null){
			if(StringUtil.isNotBlank(rReportModel.getReportName())){//报表名称
				sb.append("and rf.reportName like :reportName ");
				keyList.add("reportName");
				valueList.add("%"+rReportModel.getReportName()+"%" );
			}
			if(null!=rReportModel.getStartDate()){//报表上传时间开始
				sb.append("and uploadDate >= :startDate ");
				keyList.add("startDate");
				valueList.add(rReportModel.getStartDate());
			}
			if(null!=rReportModel.getEndDate()){//报表上传时间结束
				sb.append("and uploadDate <= :endDate ");
				keyList.add("endDate");
				valueList.add(rReportModel.getEndDate());
			}
			if(null!=rReportModel.getEndDate()){//报表上传时间结束
				sb.append("and uploadDate <= :endDate ");
				keyList.add("endDate");
				valueList.add(rReportModel.getEndDate());
			}
			if(null!=rReportModel.getStatus()){//报表状态
				sb.append("and status = :status ");
				keyList.add("status");
				valueList.add(rReportModel.getStatus());
			}
		}
		sb.append(" order by rf.uploadDate desc"); //按报表的创建日期由新往后排列
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray(),page);
		return list;
	}
	@Override
	public String queryReportFileJSON( ReportFile reportForm,Page page,User user) throws Exception{
		List list = this.queryReportFileList(reportForm, page, user);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
	}
	public List queryReportFileList(ReportFile reportForm, Page page, User user) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("select rf from ReportFile rf where 1=1 ");
		List keyList = new ArrayList(); 									// 要查询的字段列表
		List valueList = new ArrayList();								// 要查询的值列表
		if(reportForm != null){
			if(StringUtil.isNotBlank(reportForm.getReportName())){//报表模板名称
				sb.append("and rf.reportName like :reportName ");
				keyList.add("reportName");
				valueList.add("%"+reportForm.getReportName()+"%" );
			}
			if(StringUtil.isNotBlank(reportForm.getFileName())){//报表名称
				sb.append("and rf.fileName like :fileName ");
				keyList.add("fileName");
				valueList.add("%"+reportForm.getFileName()+"%" );
			}
			if(null!=reportForm.getStartDate()){//报表生成时间开始
				sb.append("and finishTime >= :startDate ");
				keyList.add("startDate");
				valueList.add(reportForm.getStartDate());
			}
			if(null!=reportForm.getEndDate()){//报表生成时间结束
				sb.append("and finishTime <= :endDate ");
				keyList.add("endDate");
				valueList.add(reportForm.getEndDate());
			}
			if(null!=reportForm.getStatus()){//报表状态
				sb.append("and status = :status ");
				keyList.add("status");
				valueList.add(reportForm.getStatus());
			}
		}
		sb.append(" order by rf.finishTime desc"); //按报表的创建日期由新往后排列
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray(),page);
		return list;
	}
	@Override
	public List findBusiList(ReportFile reportForm) throws  Exception{
		String sql = "";
		String type ="";
		String timeModel = reportForm.getTimeModel();
		String timeSelect = reportForm.getTimeSelect();
		List<ReportModelAmtBean> list = new ArrayList();
		//1、封装时间
		Map map = this.createTime(timeModel,timeSelect, reportForm);
		List<ReportModelAmtBean> beanList = (List) map.get("listBean");
		//2、封装查询数据
		RReportModel model = (RReportModel) this.load(reportForm.getTemplateId(),RReportModel.class);
		String[] types = model.getBusiType().split(",");
		for (int i=0;i<types.length;i++){
			type = types[i];
			String whereSql = "1=1";
			if(PoolComm.BUSI_TYPE_01.equals(type)){
				whereSql = this.createWhereSql(timeModel,"P_EFFSTARTDATE",map);
				sql = "SELECT TO_CHAR(ds.P_EFFSTARTDATE,'yyyy') as d1,TO_CHAR(ds.P_EFFSTARTDATE,'MM') as d2,count(P_EFFSTARTDATE) as count1" +
						" FROM PED_PROTOCOL   ds where " +whereSql+
						" GROUP BY TO_CHAR(ds.P_EFFSTARTDATE,'yyyy'),TO_CHAR(ds.P_EFFSTARTDATE,'MM') " +
						" ORDER BY TO_CHAR(ds.P_EFFSTARTDATE,'yyyy') asc,TO_CHAR(ds.P_EFFSTARTDATE,'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_02.equals(type)){
				whereSql = this.createWhereSql(timeModel,"P_EFFSTARTDATE",map);
				sql = "SELECT TO_CHAR(ds.P_EFFSTARTDATE,'yyyy') as d1,TO_CHAR(ds.P_EFFSTARTDATE,'MM') as d2,count(P_EFFSTARTDATE) as count2" +
						" FROM PED_PROTOCOL   ds where " +whereSql+
						" GROUP BY TO_CHAR(ds.P_EFFSTARTDATE,'yyyy'),TO_CHAR(ds.P_EFFSTARTDATE,'MM') " +
						" ORDER BY TO_CHAR(ds.P_EFFSTARTDATE,'yyyy') asc,TO_CHAR(ds.P_EFFSTARTDATE,'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_03.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = "select TO_CHAR(START_TIME,'yyyy') as d1,TO_CHAR(START_TIME,'MM') as d2 ,sum(LOAN_AMOUNT) as amt3 " +
						" from PED_CREDIT_DETAIL where " +whereSql+" and LOAN_TYPE = 'XD_01' and IS_ONLINE = '0' "+
						" GROUP BY TO_CHAR(START_TIME,'yyyy'),TO_CHAR(START_TIME,'MM')" +
						" ORDER BY TO_CHAR(START_TIME,'yyyy') asc,TO_CHAR(START_TIME,'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_04.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = "select TO_CHAR(START_TIME,'yyyy') as d1,TO_CHAR(START_TIME,'MM') as d2 ,sum(LOAN_AMOUNT) as amt4" +
						" from PED_CREDIT_DETAIL where " +whereSql+" and LOAN_TYPE = 'XD_02' and IS_ONLINE = '0' "+
						" GROUP BY TO_CHAR(START_TIME,'yyyy'),TO_CHAR(START_TIME,'MM')" +
						" ORDER BY TO_CHAR(START_TIME,'yyyy') asc,TO_CHAR(START_TIME,'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_05.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = "select TO_CHAR(START_TIME,'yyyy') as d1,TO_CHAR(START_TIME,'MM') as d2 ,sum(LOAN_AMOUNT) as amt5" +
						" from PED_CREDIT_DETAIL where " +whereSql+" and LOAN_TYPE = 'XD_03' and IS_ONLINE = '0' "+
						" GROUP BY TO_CHAR(START_TIME,'yyyy'),TO_CHAR(START_TIME,'MM')" +
						" ORDER BY TO_CHAR(START_TIME,'yyyy') asc,TO_CHAR(START_TIME,'MM') asc";

			}
			if(PoolComm.BUSI_TYPE_06.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = "select TO_CHAR(START_TIME,'yyyy') as d1,TO_CHAR(START_TIME,'MM') as d2 ,sum(LOAN_AMOUNT) as amt6" +
						" from PED_CREDIT_DETAIL where " +whereSql+" and LOAN_TYPE = 'XD_04' and IS_ONLINE = '0' "+
						" GROUP BY TO_CHAR(START_TIME,'yyyy'),TO_CHAR(START_TIME,'MM')" +
						" ORDER BY TO_CHAR(START_TIME,'yyyy') asc,TO_CHAR(START_TIME,'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_07.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = "select TO_CHAR(START_TIME,'yyyy') as d1,TO_CHAR(START_TIME,'MM') as d2 ,sum(LOAN_AMOUNT) as amt3 " +
						" from PED_CREDIT_DETAIL where " +whereSql+" and LOAN_TYPE = 'XD_01' and IS_ONLINE = '1' "+
						" GROUP BY TO_CHAR(START_TIME,'yyyy'),TO_CHAR(START_TIME,'MM')" +
						" ORDER BY TO_CHAR(START_TIME,'yyyy') asc,TO_CHAR(START_TIME,'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_08.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = "select TO_CHAR(START_TIME,'yyyy') as d1,TO_CHAR(START_TIME,'MM') as d2 ,sum(LOAN_AMOUNT) as amt4" +
						" from PED_CREDIT_DETAIL where " +whereSql+" and LOAN_TYPE = 'XD_02' and IS_ONLINE = '1' "+
						" GROUP BY TO_CHAR(START_TIME,'yyyy'),TO_CHAR(START_TIME,'MM')" +
						" ORDER BY TO_CHAR(START_TIME,'yyyy') asc,TO_CHAR(START_TIME,'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_09.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = " select  TO_CHAR(START_TIME, 'yyyy') as d1," +
						"       TO_CHAR(START_TIME, 'MM') as d2," +
						"       sum(LOAN_AMOUNT) as amt7 " +
						"  from PED_CREDIT_DETAIL  where  " +whereSql+" and LOAN_TYPE != 'XD_05' and IS_ONLINE = '0' "+
						"  GROUP BY TO_CHAR(START_TIME, 'yyyy'), " +
						"          TO_CHAR(START_TIME, 'MM') " +
						" ORDER BY TO_CHAR(START_TIME, 'yyyy') asc," +
						"          TO_CHAR(START_TIME, 'MM') asc";
			}
			if(PoolComm.BUSI_TYPE_10.equals(type)){
				whereSql = this.createWhereSql(timeModel,"START_TIME",map);
				sql = "select TO_CHAR(START_TIME,'yyyy') as d1,TO_CHAR(START_TIME,'MM') as d2 ,sum(LOAN_AMOUNT) as amt4" +
						" from PED_CREDIT_DETAIL where " +whereSql+" and LOAN_TYPE in ('XD_02','XD_01') and IS_ONLINE = '1' "+
						" GROUP BY TO_CHAR(START_TIME,'yyyy'),TO_CHAR(START_TIME,'MM')" +
						" ORDER BY TO_CHAR(START_TIME,'yyyy') asc,TO_CHAR(START_TIME,'MM') asc";
			}
			if(StringUtils.isNotBlank(sql)){
				list = dao.SQLQuery(sql);
				beanList = this.createModel(beanList,list,type);
			}
		}
		return beanList;
	}
	public String createWhereSql(String timeModel,String item,Map map){
		String startDt = (String) map.get("startDt");
		String whereSql = "";
		if (PoolComm.TIME_MODEL_01.equals(timeModel) || PoolComm.TIME_MODEL_03.equals(timeModel)){
			whereSql = "to_char("+item+",'yyyy') = '"+startDt+"' ";
		}
		if (PoolComm.TIME_MODEL_02.equals(timeModel) ||PoolComm.TIME_MODEL_04.equals(timeModel) ){
			whereSql = "to_char("+item+",'yyyy-MM') = '"+startDt+"' ";
		}
		if(PoolComm.TIME_MODEL_05.equals(timeModel) || PoolComm.TIME_MODEL_06.equals(timeModel)){
			whereSql = "to_char("+item+",'yyyy-MM') >= '"+startDt+"' and " +
					" to_char("+item+",'yyyy-MM') <= '"+map.get("endDt")+"' ";
		}
		return  whereSql;
	}

	public List<ReportModelAmtBean> createModel(List<ReportModelAmtBean> list1,List list2,String type){
		int count = 0;//creditList计数器
		if (null!=list1 &&list1.size()>0  && null !=list2 && list2.size()>0){
			for (int i=0;i<list2.size();i++){
				ReportModelAmtBean bean1 = list1.get(count);
				Object[] obj = (Object[])list2.get(i);
				for (int j=count;j<list1.size();j++){
					bean1 = list1.get(count);
					if(bean1.getD1() == Integer.parseInt((String)obj[0]) && bean1.getD2() == Integer.parseInt((String)obj[1])){
						break;
					}else{
						count++;
					}
				}
				if (PoolComm.BUSI_TYPE_01.equals(type)){
					bean1.setCount1((Integer.parseInt(obj[2]+"")));
				}
				if (PoolComm.BUSI_TYPE_02.equals(type)){
					bean1.setCount2(Integer.parseInt((obj[2]+"")));
				}
				if (PoolComm.BUSI_TYPE_03.equals(type)){
					bean1.setAmt3((BigDecimal) obj[2]);
				}
				if (PoolComm.BUSI_TYPE_04.equals(type)){
					bean1.setAmt4((BigDecimal) obj[2]);
				}
				if (PoolComm.BUSI_TYPE_05.equals(type)){
					bean1.setAmt5((BigDecimal) obj[2]);
				}
				if (PoolComm.BUSI_TYPE_06.equals(type)){
					bean1.setAmt6((BigDecimal) obj[2]);
				}
				if (PoolComm.BUSI_TYPE_07.equals(type)){
					bean1.setAmt7((BigDecimal) obj[2]);
				}
				if (PoolComm.BUSI_TYPE_08.equals(type)){
					bean1.setAmt8((BigDecimal) obj[2]);
				}
				if (PoolComm.BUSI_TYPE_09.equals(type)){
					bean1.setAmt9((BigDecimal) obj[2]);
				}
				if (PoolComm.BUSI_TYPE_10.equals(type)){
					bean1.setAmt10((BigDecimal) obj[2]);
				}
				count = 0;
			}
		}
		return  list1;
	}
	public Map createTime(String timeModel,String timeSelect, ReportFile reportForm){
		Map map = new HashMap();
		List<ReportModelAmtBean> listBean  = new ArrayList();
		String[] times = timeSelect.split(",");
		//模式1
		if(PoolComm.TIME_MODEL_01.equals(timeModel)){
			int time = Integer.parseInt(times[0]);
			Date afterDate = DateUtils.getNextNYear(new Date(),time);
			for(int i=1;i<=12;i++){
				ReportModelAmtBean bean = new ReportModelAmtBean();
				bean.setD1(DateUtils.getYear(afterDate));//年
				bean.setD2(i);//月
				listBean.add(bean);
			}
			map.put("startDt",DateUtils.getYear(afterDate)+"" );
		}
		//模式2
		if(PoolComm.TIME_MODEL_02.equals(timeModel)){
			int time = Integer.parseInt(times[0]);
			ReportModelAmtBean bean = new ReportModelAmtBean();
			bean.setD1(DateUtils.getYear(new Date()));//年
			bean.setD2(time);//月
			listBean.add(bean);
			if(time < 10){
				map.put("startDt",bean.getD1()+"-0"+bean.getD2() );
			}else{
				map.put("startDt",bean.getD1()+"-"+bean.getD2() );
			}
//			map.put("startDt",bean.getD1()+"-"+bean.getD2());
			
		}
		//模式3
		if(PoolComm.TIME_MODEL_03.equals(timeModel)){
			int year = Integer.parseInt(times[0]);
			for(int i=1;i<=12;i++){
				ReportModelAmtBean bean = new ReportModelAmtBean();
				bean.setD1(year);//年
				bean.setD2(i);//月
				listBean.add(bean);
			}
			map.put("startDt",year+"" );
		}
		//模式4
		if(PoolComm.TIME_MODEL_04.equals(timeModel)){
			int year = Integer.parseInt(times[0]);
			int month = Integer.parseInt(times[1]);
			ReportModelAmtBean bean = new ReportModelAmtBean();
			bean.setD1(year);//年
			bean.setD2(month);//月
			listBean.add(bean);
			if(month < 10){
				map.put("startDt",year+"-0"+month );
			}else{
				map.put("startDt",year+"-"+month );
			}
		}
		//模式5
		if(PoolComm.TIME_MODEL_05.equals(timeModel)){
			int year1 = Integer.parseInt(times[0]);
			int month1 = Integer.parseInt(times[1]);
			int year2 = Integer.parseInt(times[2]);
			int month2 = Integer.parseInt(times[3]);
			Date date1 = DateUtils.parse(year1+"-"+month1,"yyyy-MM");
			Date date2 = DateUtils.parse(year2+"-"+month2,"yyyy-MM");
			for (int i=0;i<200;i++){
				Date date =  DateUtils.getNextNMonth(date1,i);
				ReportModelAmtBean bean = new ReportModelAmtBean();
				bean.setD1(DateUtils.getYear(date));//年
				bean.setD2(DateUtils.getMonth(date));//月
				listBean.add(bean);
				date = DateUtils.formatDate(date,"yyyy-MM");
				if(date.compareTo(date2)==0){
					break;
				}
			}
			if(month1 < 10){
				map.put("startDt",year1+"-0"+month1 );
			}else{
				map.put("startDt",year1+"-"+month1 );
			}
			if(month2 < 10){
				map.put("endDt",year2+"-0"+month2 );
			}else{
				map.put("endDt",year2+"-"+month2 );
			}
		}
		//模式6
		if(PoolComm.TIME_MODEL_06.equals(timeModel)){
			int todayYear = DateUtils.getYear(new Date());//当前年份
			int year1 = todayYear+Integer.parseInt(times[0]);
			int month1 = Integer.parseInt(times[1]);
			int year2 = todayYear+Integer.parseInt(times[2]);
			int month2 = Integer.parseInt(times[3]);
			Date date1 = DateUtils.parse(year1+"-"+month1,"yyyy-MM");
			Date date2 = DateUtils.parse(year2+"-"+month2,"yyyy-MM");
			for (int i=0;i<200;i++){
				Date date =  DateUtils.getNextNMonth(date1,i);
				ReportModelAmtBean bean = new ReportModelAmtBean();
				bean.setD1(DateUtils.getYear(date));//年
				bean.setD2(DateUtils.getMonth(date));//月
				listBean.add(bean);
				date = DateUtils.formatDate(date,"yyyy-MM");
				if(date.compareTo(date2)==0){
					 break;
				}
			}
			if(month1 < 10){
				map.put("startDt",year1+"-0"+month1 );
			}else{
				map.put("startDt",year1+"-"+month1 );
			}
			if(month2 < 10){
				map.put("endDt",year2+"-0"+month2 );
			}else{
				map.put("endDt",year2+"-"+month2 );
			}
		}
		map.put("listBean",listBean);
		return  map;
	}
	@Override
	public List<ReportModelAmtBean> findBusiModelList(String busiType) throws  Exception{
		String sql = "";
		String type ="";
		String[] types = busiType.split(",");
		ReportModelAmtBean bean1 = new ReportModelAmtBean();
		ReportModelAmtBean bean2 = new ReportModelAmtBean();
		List<ReportModelAmtBean> list = new ArrayList();
		for (int i=0;i<types.length;i++){
			type = types[i];
			if(PoolComm.BUSI_TYPE_01.equals(type)){
				bean1.setCount1(100);
			}
			if(PoolComm.BUSI_TYPE_02.equals(type)){
				bean1.setCount2(100);
			}
			if(PoolComm.BUSI_TYPE_03.equals(type)){
				bean1.setAmt3(new BigDecimal("100"));
			}
			if(PoolComm.BUSI_TYPE_04.equals(type)){
				bean1.setAmt4(new BigDecimal("100"));
			}
			if(PoolComm.BUSI_TYPE_05.equals(type)){
				bean1.setAmt5(new BigDecimal("100"));
			}
			if(PoolComm.BUSI_TYPE_06.equals(type)){
				bean1.setAmt6(new BigDecimal("100"));
			}
			if(PoolComm.BUSI_TYPE_07.equals(type)){
				bean1.setAmt7(new BigDecimal("100"));
			}
			if(PoolComm.BUSI_TYPE_08.equals(type)){
				bean1.setAmt8(new BigDecimal("100"));
			}
			if(PoolComm.BUSI_TYPE_09.equals(type)){
				bean1.setAmt9(new BigDecimal("100"));
			}
			if(PoolComm.BUSI_TYPE_10.equals(type)){
				bean1.setAmt10(new BigDecimal("100"));
			}
		}
		bean1.setD1(DateUtils.getYear(new Date()));
		bean1.setD2(DateUtils.getMonth(new Date()));
		BeanUtil.copyValue(bean1,bean2);
		bean2.setD2(DateUtils.getMonth(new Date())-1);
		list.add(bean2);
		list.add(bean1);
		return list;
	}

	@Override
	public List<Map> findReportHeads(String busiType) throws Exception {
		List<Map> list = new ArrayList<Map>();
		String reportName = "";
		String type ="";
		String[] types = busiType.split(",");
		Map map = new LinkedHashMap();
		map.put("d1","年份");
		map.put("d2","月份");
		for (int i=0;i<types.length;i++){
			type = types[i];
			if(PoolComm.BUSI_TYPE_01.equals(type)){
				if(i==types.length-1){
					reportName = "票据池签约客户数";
				}else{
					reportName = "票据池签约客户数&";
				}
				map.put("count1","票据池签约客户数");
			}
			if(PoolComm.BUSI_TYPE_02.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池总签约客户数";
				}else{
					reportName =reportName+ "票据池总签约客户数&";
				}
				map.put("count2","票据池总签约客户数");
			}
			if(PoolComm.BUSI_TYPE_03.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "&票据池线下银承发生金额";
				}else {
					reportName =reportName+ "票据池线下银承发生金额&";
				}
				map.put("amt3","票据池线下银承发生金额");
			}
			if(PoolComm.BUSI_TYPE_04.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池线下流贷发生金额";
				}else {
					reportName =reportName+ "票据池线下流贷发生金额&";
				}
				map.put("amt4","票据池线下流贷发生金额");
			}
			if(PoolComm.BUSI_TYPE_05.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池线下保函发生金额";
				}else{
					reportName =reportName+ "票据池线下保函发生金额&";
				}
				map.put("amt5","票据池线下保函发生金额");
			}
			if(PoolComm.BUSI_TYPE_06.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池线下信用证发生金额";
				}else {
					reportName =reportName+ "票据池线下信用证发生金额&";
				}
				map.put("amt6","票据池线下信用证发生金额");
			}
			if(PoolComm.BUSI_TYPE_07.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池线上银承发生金额";
				}else {
					reportName =reportName+ "票据池线上银承发生金额&";
				}
				map.put("amt7","票据池线上银承发生金额");
			}
			if(PoolComm.BUSI_TYPE_08.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池线上流贷发生金额";
				}else {
					reportName =reportName+ "票据池线上流贷发生金额&";
				}
				map.put("amt8","票据池线上流贷发生金额");
			}
			if(PoolComm.BUSI_TYPE_09.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池线下融资金额";
				}else {
					reportName =reportName+ "票据池线下融资金额&";
				}
				map.put("amt9","票据池线下融资金额");
			}
			if(PoolComm.BUSI_TYPE_10.equals(type)){
				if(i==types.length-1){
					reportName =reportName+ "票据池线上融资金额";
				}else {
					reportName =reportName+ "票据池线上融资金额&";
				}
				map.put("amt10","票据池线上融资金额");
			}
		}
		list.add(map);
		Map mapName = new LinkedHashMap();

		mapName.put("reportName",reportName);
		list.add(0,mapName);
		return list;
	}

	@Override
	public void txUploadReportModel(CommonsMultipartFile file, User user, String remark) throws Exception {
		InputStream is = null;
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		String fileName = file.getOriginalFilename();  //获取文件名
		String fileXlsx = fileName.substring(fileName.length()-5);       //获取文件的后缀名为xlsx
		String fileXls = fileName.substring(fileName.length()-4);
		if(!(fileXlsx.equals(".xlsx") || fileXls.equals(".xls"))){   //如果不是excel文件
			throw new Exception("文件格式不正确！");
		}
		//获取文件流
		is = file.getInputStream();
		//获取excel文件表头内容，并校验和法性
		Workbook wkbook = WorkbookFactory.create(is);
		Sheet rs = wkbook.getSheetAt(0);
		Row row = rs.getRow(0);
		for (int i=0; i<row.getLastCellNum(); i++){
			String cellData = (String) ExcelUtil.getCellValueByCell(row.getCell(i));
			result.add(cellData.toString());
		}
		//校验表头合法性 ,并获取业务类型
		String  busiType = this.txCheckExcelHead(result);
		//excel文件上传到服务器（路径放在redis配置中）
		String rootPath = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DOCUMENT_REPORT_TEMPLATE_PATH);
//		String rootPath = "D:\\report";
		File fpath = new File(rootPath);
		if(!fpath.exists()){
			fpath.mkdirs();
		}
		String filePath = rootPath+File.separator+fileName;
		File localFile = new File(filePath);
		file.transferTo(localFile);
//		upLoadExcelFile(is,fileName);
		//生成模板对象
		RReportModel model = new RReportModel();
		model.setBusiType(busiType);
		model.setReportName(fileName);
		model.setFilePath(rootPath);
		model.setUploadDate(new Date());
		model.setUserId(user.getId());
		model.setUserNm(user.getName());
		model.setDeptId(user.getDeptId());
		model.setDeptName(user.getDeptNm());
		model.setRemark(remark);
		this.txStore(model);
	}

	/*public void upLoadExcelFile(InputStream is,String fileName ) throws  Exception{
		String rootPath = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DOCUMENT_ROOT_PATH);
		String serverName ="";
		String userName = "";
		String password ="";
		String port = "";
		try{
			FTPClient ftpClient  = new FTPClient();
			// 连接至服务器，端口默认为21时，可直接通过URL连接
			ftpClient.connect(serverName, Integer.parseInt(port));
			// 登录服务器
			ftpClient.login(userName, password);
			// 判断返回码是否合法
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				// 不合法时断开连接
				ftpClient.disconnect();
				// 结束程序
				throw new Exception("连接服务器失败！");
			}
			ftpClient.changeWorkingDirectory(rootPath);
			// 设置文件类型，二进制
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			// 设置缓冲区大小
			ftpClient.setBufferSize(3072);
			// 设置字符编码
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.storeFile(fileName, is);
		}catch (IOException e){
			logger.error(e.getMessage(),e);
		}finally {
			// 判断输入流是否存在
			if (null != is) {
				try {
					// 关闭输入流
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
			// 登出服务器并断开连接
			is.close();
		}
	}*/


	public String txCheckExcelHead(LinkedHashSet<String> result) throws  Exception {
		String busiType = "";
		Iterator i = result.iterator();
		while (i.hasNext()) {
			String type = (String) i.next();
			if ("年份".equals(type) || "月份".equals(type)) {
				continue;
			}else if ("票据池签约客户数".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_01;
				continue;
			} else if ("票据池总签约客户数".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_02;
				continue;
			} else if ("票据池线下银承发生金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_03;
				continue;
			} else if ("票据池线下流贷发生金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_04;
				continue;
			} else if ("票据池线下保函发生金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_05;
				continue;
			} else if ("票据池线下信用证发生金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_06;
				continue;
			} else if ("票据池线上银承发生金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_07;
				continue;
			} else if ("票据池线上流贷发生金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_08;
				continue;
			} else if ("票据池线下融资金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_09;
				continue;
			} else if ("票据池线上融资金额".equals(type)) {
				busiType = busiType + "," + PoolComm.BUSI_TYPE_10;
				continue;
			} else {
				throw new Exception("表头输入不合法！");
			}
		}
		return busiType;
	}
	/**
	 * 保存通用报表文件对象
	 * @param user 当前用户
	 * @return ReportFile 报表生成对象
	 * @throws Exception
	 */
	public ReportFile txSaveCommonReportFile(User user,String id,String timeModel,String timeSelect)throws Exception{
		 RReportModel model = (RReportModel) this.load(id,RReportModel.class);
		 if(null == model){
		 	throw  new Exception("模板不存在！");
		 }
		 model.setTimeModel(timeModel);
		 model.setTimeSelect(timeSelect);
		 //生成报表文件对象
		ReportFile reportFile = new ReportFile();
		reportFile.setTemplateId(model.getId());//模板id
		reportFile.setReportName(model.getReportName());//模板名称
		reportFile.setFilePath(model.getFilePath());//文件路径
		//产生四位随机数
		String  round = String.format("%04d",new Random().nextInt(9999));
		reportFile.setReportSeqNo(DateUtils.getTime(new Date(),"yyyyMMddHHmmss")+round);//报表序列号--yyyyMMddHHmmss+四位随机序列号
		reportFile.setFileName(model.getReportName().substring(0,model.getReportName().length()-5)+reportFile.getReportSeqNo()+".xlsx");//规则为 模板名称+yyyyMMddHHmmss+四位随机序列号
		reportFile.setTimeModel(timeModel);//时间模式
		reportFile.setTimeSelect(timeSelect);//时间选择
		reportFile.setStatus(PublicStaticDefineTab.Report_STATUS_0);//报表状态
		reportFile.setCreateTime(new Date());//创建时间
		reportFile.setFinishTime(new Date());//报表完成时间
		reportFile.setUserId(user.getId());//用户id
		reportFile.setUserNm(user.getName());//用户名称
		reportFile.setDeptId(user.getDeptId());//机构id
		reportFile.setDeptName(user.getDeptNm());//机构名称
		this.txStore(reportFile);
		this.txStore(model);
		return reportFile;
	}
	/**
	 * 根据id查询报表生成文件信息
	 * @param id 报表生成文件主键
	 * @throws Exception
	 * @return ReportFile 报表生成文件
	 */
	public RReportModel getReportFileById(String id)throws Exception{
		return (RReportModel) dao.load(RReportModel.class, id);
	}
}