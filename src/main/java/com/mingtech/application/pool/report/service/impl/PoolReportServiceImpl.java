package com.mingtech.application.pool.report.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.report.domain.RCreditReportInfo;
import com.mingtech.application.pool.report.domain.RCreditReportInfoBean;
import com.mingtech.application.pool.report.domain.RPoolReportInfo;
import com.mingtech.application.pool.report.service.PoolReportService;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 票据池报表功能实现服务
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-15
 */
@Service("poolReportService")
public class PoolReportServiceImpl extends GenericServiceImpl implements PoolReportService{
	private static final Logger logger = Logger.getLogger(PoolReportService.class);
	@Autowired
	DepartmentService departmentService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	public Class getEntityClass(){
		return ReportForm.class;
	}

	public String getEntityName(){
		return StringUtil.getClass(this.getEntityClass());
	}
	
	@Override
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
	
	
	@Override
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
		}
		
		sb.append(" order by rf.createTime desc"); //按报表的创建日期由新往后排列
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		if(page != null){ // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		}else{
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
		
	}

	@Override
	public void txCreatPoolReportInfo() throws Exception {
		
		logger.info("RPoolReportInfo报表生成......开始......");
		
		List<RPoolReportInfo> infos = new ArrayList<RPoolReportInfo>();
		
		Date yesterday = DateUtils.adjustDateByDay(new Date(), -1);
    	Date yesterdayFormat =  sdf.parse(sdf.format(yesterday));
    	
		
		/*
		 * 1.按机构统计有效签约客户数量
		 */

		List result1 = this.queryProNumForBranch();//有签约的机构及签约数量
		
		List<Department> deptList = departmentService.getAllDeptNoDif();//全部机构
		
		
		List<Department> noSignDeptList = new ArrayList<Department>();//无签约信息的机构
		
		String signDeptNo = null;//所有签约机构号
		if(result1!=null){
			for(int i=0;i<result1.size();i++){
				Object[] obj = (Object[])result1.get(i);
				if(obj[0]!=null){
					if(signDeptNo == null){
						signDeptNo = "'"+obj[0].toString()+"'";
					}else{
						signDeptNo = signDeptNo + ",'"+obj[0].toString()+"'";
					}
				}
				
			}
		}
		noSignDeptList = this.getNoSignDept(signDeptNo);

		
		int proI = 0;//用于记录排名
		
		/*
		 * 2.循环处理每个机构下的信息
		 */
		if(result1!=null){
			for(int i=0;i<result1.size();i++){
				proI = result1.size() + 2;
				Object[] obj = (Object[])result1.get(i);
				RPoolReportInfo info = new RPoolReportInfo();
		    	
				info.setCreateDate(yesterdayFormat);//跑批在t+1日生成t日报表
				
				if(obj[0]!=null){
					info.setBranchNo(obj[0].toString());//机构号					
					Department dept = departmentService.queryByInnerBankCode(obj[0].toString());
					if(dept!=null && dept.getName()!=null){						
						info.setBranchName(dept.getName());//机构名称
					}
				}
				
				if(obj[1]!=null){
					BigDecimal proTotalNum = new BigDecimal(obj[1].toString()); 
					info.setProTotalNum(proTotalNum);//协议总笔数
				}
				info.setProRank(i+1+"");//截至当日签约数量全行排名
				
				/*
				 * 3.获取该机构下所有的票据池编号
				 */
				List result2 = null;
				if(obj[0]!=null){					
					result2 = this.queryBpsNoByBranchNo(obj[0].toString());
				}
				
				if(result2!=null &&result2.size()>0){
					
					/*
					 * 4.在池票面金额汇总
					 */
					
					List result3 = this.queryDraftTotalAmt(result2);				
					if(result3!=null){
						BigDecimal draftAmt = (BigDecimal) result3.get(0);
						info.setDraftTotalAmt(draftAmt);//入池票面金额汇总
					}else{
						info.setDraftTotalAmt(BigDecimal.ZERO);//入池票面金额汇总			
					}
					
					/*
					 * 5.保证金余额汇总
					 */
					
					List result4 = this.queryMarginTotal(result2);	
					if(result4!=null){
						BigDecimal bailAmt =  (BigDecimal) result4.get(0);
						info.setMarginTotalAmt(bailAmt);//保证金余额汇总						
					}else{
						info.setMarginTotalAmt(BigDecimal.ZERO);//保证金余额汇总
					}
					
				}else{
					info.setDraftTotalAmt(BigDecimal.ZERO);//入池票面金额汇总	
					info.setMarginTotalAmt(BigDecimal.ZERO);//保证金余额汇总
				}
				
				infos.add(info);
			}
			this.txStoreAll(infos);
			this.dao.flush();
		}
		
		/*
		 * 6.全行名次信息更新
		 */
		
		//【截至当日的票据余额全行排名】更新
		List<RPoolReportInfo> draftRank= this.queryReportList("DRAFT");
		if(draftRank!=null){
			List<RPoolReportInfo> saveDraftRanks = new ArrayList<RPoolReportInfo>();
			for(int i=0;i<draftRank.size();i++){
				RPoolReportInfo info = draftRank.get(i);
				info.setDraftRank(i+1+"");
				saveDraftRanks.add(info);
			}
			this.txStoreAll(saveDraftRanks);
			this.dao.flush();
		}
		
		//【截至当日保证金余额全行排名】更新
		List<RPoolReportInfo> marginRank= this.queryReportList("MARGIN");
		if(marginRank!=null){
			List<RPoolReportInfo> saveMarginRanks = new ArrayList<RPoolReportInfo>();
			for(int i=0;i<marginRank.size();i++){
				RPoolReportInfo info = marginRank.get(i);
				info.setMarginRank(i+1+"");
				saveMarginRanks.add(info);
			}
			this.txStoreAll(saveMarginRanks);
			this.dao.flush();
			
		}
		
		
		/*
		 * 6-2处理无签约信息的机构
		 */
		if(noSignDeptList!=null){
			List<RPoolReportInfo> saveMarginRanks = new ArrayList<RPoolReportInfo>();
			for(int j=0;j<noSignDeptList.size();j++){
				Department dept = noSignDeptList.get(j);
				RPoolReportInfo info = new  RPoolReportInfo();	
				String rank = (proI + j)+"";
				
				info.setCreateDate(yesterdayFormat);//跑批在t+1日生成t日报表
				info.setBranchNo(dept.getInnerBankCode());//机构号	
				info.setBranchName(dept.getName());//机构名称
				info.setProTotalNum(BigDecimal.ZERO);//协议总笔数
				info.setProRank(rank);//截至当日签约数量全行排名
				info.setDraftTotalAmt(BigDecimal.ZERO);//入池票面金额汇总		
				info.setMarginTotalAmt(BigDecimal.ZERO);//保证金余额汇总
				info.setDraftRank(rank);
				info.setMarginRank(rank);
				saveMarginRanks.add(info);
			}
			this.txStoreAll(saveMarginRanks);
			this.dao.flush();
		}
		
		
		/*
		 * 7.数据增减值处理
		 * 		（1）较上日增减值、增减比例数据处理 
		 * 		（2）较上月末增减值、增减比例数据处理
		 * 		（3）较上年末增减值、增减比例数据处理
		 */
		if(deptList!=null){
			this.txpoolFormDataHandle(deptList);
		}
		
	}
	/**
	 * 按机构统计有效签约客户数量
	 * @author Ju Nana
	 * @return
	 * @throws Exception
	 * @date 2019-7-15下午8:25:47
	 */
	private List queryProNumForBranch()throws Exception{
		
		logger.info("按机构统计有效签约客户数量............");
		
		StringBuffer sqlStr1 = new StringBuffer("select pedProtocolDto.officeNet,count(pedProtocolDto.officeNet) from PedProtocolDto as pedProtocolDto where 1=1 "); 
		sqlStr1.append(" and pedProtocolDto.openFlag='"+PoolComm.OPEN_01+"'");
		sqlStr1.append(" and pedProtocolDto.vStatus='"+PoolComm.VS_01+"'");
		sqlStr1.append(" group by  pedProtocolDto.officeNet");
		sqlStr1.append(" order by count(pedProtocolDto.officeNet) desc");
		List result1 = this.find(sqlStr1.toString());
		
		if(result1!=null &&result1.size()>0){
			return result1;			
		}
		return null;
	}
	
	/**
	 * 根据机构号查询票据池编号列表
	 * @author Ju Nana
	 * @param brchNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-15下午8:30:43
	 */
	private List queryBpsNoByBranchNo(String brchNo)throws Exception{
		
		StringBuffer sqlStr2 = new StringBuffer("select pedProtocolDto.poolAgreement from PedProtocolDto as pedProtocolDto where 1=1 "); 
		sqlStr2.append(" and pedProtocolDto.officeNet='"+brchNo+"'");
		List result2 = this.find(sqlStr2.toString());
		if(result2!=null && result2.size()>0){
			return result2;
		}
		return null;
		
	}
	
	/**
	 * 在池票面金额汇总
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNos
	 * @return
	 * @date 2019-7-15下午8:34:29
	 */
	private List queryDraftTotalAmt(List bpsNos){
		
		List paramName3 = new ArrayList();// 名称
		List paramValue3 = new ArrayList();// 值
		StringBuffer sqlStr3 = new StringBuffer("select sum(bill.FBillAmount) from PoolBillInfo as bill where 1=1 "); 
		
		sqlStr3.append(" and SDealStatus=:SDealStatus");
		paramName3.add("SDealStatus");
		paramValue3.add(PoolComm.DS_02);
		
		sqlStr3.append(" and poolAgreement in (:bpsNos)");
		paramName3.add("bpsNos");
		paramValue3.add(bpsNos);
		
		String paramNames3[] = (String[]) paramName3.toArray(new String[paramName3.size()]);
		Object paramValues3[] = paramValue3.toArray();
		
		List result3 = this.find(sqlStr3.toString(), paramNames3, paramValues3);
		
		if(result3!=null && result3.size()>0){
			if(result3.get(0)!=null){
				return result3;					
			}
		}
		
		return null;
		
	}
	
	/**
	 * 查询保证金总金额
	 * @Description TODO
	 * @author Ju Nana
	 * @return
	 * @date 2019-7-15下午8:37:19
	 */
	private List queryMarginTotal(List bpsNos){
		List paramName4 = new ArrayList();// 名称
		List paramValue4 = new ArrayList();// 值
		StringBuffer sqlStr4 = new StringBuffer("select sum(bail.assetLimitTotal) from BailDetail as bail where 1=1 "); 
		
		
		sqlStr4.append(" and bail.poolAgreement in (:bpsNos)");
		paramName4.add("bpsNos");
		paramValue4.add(bpsNos);
		
		String paramNames4[] = (String[]) paramName4.toArray(new String[paramName4.size()]);
		Object paramValues4[] = paramValue4.toArray();
		List result4 = this.find(sqlStr4.toString(), paramNames4, paramValues4);
		
		if(result4!=null && result4.size()>0){
			if(result4.get(0)!=null){
				return result4;
			}
		}
		return null;
	}
	
	/**
	 * 查询当日生成的报表信息列表
	 * @author Ju Nana
	 * @param flag
	 * @return
	 * @throws Exception
	 * @date 2019-7-16上午9:57:49
	 */
	private List<RPoolReportInfo>  queryReportList(String flag)throws Exception{	
		Date yesterday = DateUtils.adjustDateByDay(new Date(), -2);
		Date tomorrow = DateUtils.adjustDateByDay(new Date(), 0);
		
		List paramName5 = new ArrayList();// 名称
		List paramValue5 = new ArrayList();// 值
		StringBuffer sqlStr5 = new StringBuffer("select port from RPoolReportInfo as port where 1=1 "); 
		
		
		sqlStr5.append(" and port.createDate > :yesterday");
		paramName5.add("yesterday");
		paramValue5.add(yesterday);
		
		sqlStr5.append(" and port.createDate < :tomorrow");
		paramName5.add("tomorrow");
		paramValue5.add(tomorrow);
		
		if("DRAFT".equals(flag)){//按照【入池票面金额汇总】排序
			
			sqlStr5.append(" order by port.draftTotalAmt desc ");			
		
		}else if("MARGIN".equals(flag)){//按照【入池保证金余额】排序
			
			sqlStr5.append(" order by port.marginTotalAmt desc ");			
		}
		
		String paramNames5[] = (String[]) paramName5.toArray(new String[paramName5.size()]);
		Object paramValues5[] = paramValue5.toArray();
		List result5 = this.find(sqlStr5.toString(), paramNames5, paramValues5);
		
		if(result5!=null && result5.size()>0){
			return result5;
		}
		
		return null;
		
	}
	
	/**
	 * 查询处理每个机构的较上日、上月末、上年末的数据源
	 * @author Ju Nana
	 * @param branchNo
	 * @throws Exception
	 * @date 2019-7-19上午9:40:50
	 */
	private List<RPoolReportInfo> queryFormDateSrc(String branchNo) throws Exception{
		
		List<Date> dayLsit = new ArrayList<Date>();
		
		Date beforeYesterday = DateUtils.adjustDateByDay(new Date(), -2);
    	Date beforeYesterdayFormat =  sdf.parse(sdf.format(beforeYesterday));//前天
    	dayLsit.add(beforeYesterdayFormat);
    	
    	
    	Date lastDayOfLastMonth = DateTimeUtil.getLastDayOfLastMonth();  
    	Date lastDayOfLastMonthFormart =  sdf.parse(sdf.format(lastDayOfLastMonth));//上月末最后一天
    	dayLsit.add(lastDayOfLastMonthFormart);
    	
    	Date lastDayOfLastYear = DateTimeUtil.getLastDayOfLastYear();
    	Date lastDayOfLastYearFormart =  sdf.parse(sdf.format(lastDayOfLastYear));//上年末最后一天
    	dayLsit.add(lastDayOfLastYearFormart);
    	
		List paramName7 = new ArrayList();// 名称
		List paramValue7 = new ArrayList();// 值
		StringBuffer sqlStr7 = new StringBuffer("select port from RPoolReportInfo as port where 1=1 "); 
		
		sqlStr7.append(" and port.branchNo = :branchNo");
		paramName7.add("branchNo");
		paramValue7.add(branchNo);
		
		sqlStr7.append(" and port.createDate in (:dayLsit)");
		paramName7.add("dayLsit");
		paramValue7.add(dayLsit);
		
		sqlStr7.append(" order by port.createDate desc ");	
		
		String paramNames7[] = (String[]) paramName7.toArray(new String[paramName7.size()]);
		Object paramValues7[] = paramValue7.toArray();
		List result7 = this.find(sqlStr7.toString(), paramNames7, paramValues7);
		
		if(result7!=null && result7.size()>0){
			return result7;
		}
		
		return null;
		
	}
	
	/**
	 * 根据日期与机构号查询票据池报表实体
	 * @author Ju Nana
	 * @param date
	 * @param branchNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-19上午10:44:47
	 */
	private RPoolReportInfo queryPoolForm(Date date,String branchNo) throws Exception{
		
		List paramName8 = new ArrayList();// 名称
		List paramValue8 = new ArrayList();// 值
		StringBuffer sqlStr8 = new StringBuffer("select port from RPoolReportInfo as port where 1=1 "); 
		
		sqlStr8.append(" and port.branchNo = :branchNo");
		paramName8.add("branchNo");
		paramValue8.add(branchNo);
		
		sqlStr8.append(" and port.createDate =:date");
		paramName8.add("date");
		paramValue8.add(date);
		
		sqlStr8.append(" order by port.createDate desc ");	
		
		String paramNames8[] = (String[]) paramName8.toArray(new String[paramName8.size()]);
		Object paramValues8[] = paramValue8.toArray();
		List result8 = this.find(sqlStr8.toString(), paramNames8, paramValues8);
		
		if(result8!=null && result8.size()>0){
			return (RPoolReportInfo) result8.get(0);
		}
		
		return null;
		
	}
	
	/**
	 * 票据池报表数据比对处理
	 * @author Ju Nana
	 * @param result1
	 * @throws Exception
	 * @date 2019-7-19上午11:34:26
	 */
	public void txpoolFormDataHandle(List<Department> result1) throws Exception{
		
		logger.info("票据池报表数据比对处理............");
		
		List<RPoolReportInfo> allForms = new ArrayList<RPoolReportInfo>();
		
		Date yesterday = DateUtils.adjustDateByDay(new Date(), -1);
    	Date yesterdayFormat =  sdf.parse(sdf.format(yesterday));//昨天

    	Date beforeYesterday = DateUtils.adjustDateByDay(new Date(), -2);
    	Date beforeYesterdayFormat =  sdf.parse(sdf.format(beforeYesterday));//前天
    	
    	Date lastDayOfLastMonth = DateTimeUtil.getLastDayOfLastMonth();  
    	Date lastDayOfLastMonthFormart =  sdf.parse(sdf.format(lastDayOfLastMonth));//上月末最后一天
    	
    	Date lastDayOfLastYear = DateTimeUtil.getLastDayOfLastYear();
    	Date lastDayOfLastYearFormart =  sdf.parse(sdf.format(lastDayOfLastYear));//上年末最后一天
    	
		for(Department dept : result1){
			String branchNo = dept.getInnerBankCode();
			RPoolReportInfo form = this.queryPoolForm(yesterdayFormat, branchNo);
			if(form==null){
				logger.info("当日未生成该机构报表，机构号为【"+branchNo+"】");
				continue;
			}
			BigDecimal ZERO = BigDecimal.ZERO;
			form.setProCompYestNum(ZERO);//协议较上日增减值              ;
			form.setProCompYestRatio(ZERO);//协议较上日增减比例          ;
			form.setProCompLastMNum(ZERO);//协议较上月末增减值           ;
			form.setProCompLastMRatio(ZERO);//协议较上月末增减比率       ;
			form.setProCompLastYNum(ZERO);//协议较上年增减值             ;
			form.setProCompLastYRatio(ZERO);//协议较上年增减比率         ;
			form.setDraftCompYestNum(ZERO);//入池票据较上日增减值        ;
			form.setDraftCompYestRatio(ZERO);//入池票据较上日增减比例    ;
			form.setDraftCompLastMNum(ZERO);//入池票据较上月末增减值     ;
			form.setDraftCompLastMRatio(ZERO);//入池票据较上月末增减比率 ;
			form.setDraftCompLastYNum(ZERO);//入池票据较上年增减值       ;
			form.setDraftCompLastYRatio(ZERO);//入池票据较上年增减比率   ;
			form.setMarginCompYestNum(ZERO);//保证金较上日增减值         ;
			form.setMarginCompYestRatio(ZERO);//保证金较上日增减比例     ;
			form.setMarginCompLastMNum(ZERO);//保证金较上月末增减值      ;
			form.setMarginCompLastMRatio(ZERO);//保证金较上月末增减比率  ;
			form.setMarginCompLastYNum(ZERO);//保证金较上年增减值        ;
			form.setMarginCompLastYRatio(ZERO);//保证金较上年增减比率    ;
			
			List<RPoolReportInfo> list = this.queryFormDateSrc(branchNo);
			
			if(list!=null){
				for(RPoolReportInfo data : list ){
					if(beforeYesterdayFormat.equals(data.getCreateDate())){//前天
						logger.info("比较前一天");
						BigDecimal proNum = form.getProTotalNum().subtract(data.getProTotalNum());
						BigDecimal proRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getProTotalNum())!=0){
							proRatio = proNum.divide(data.getProTotalNum(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						BigDecimal draftNum = form.getDraftTotalAmt().subtract(data.getDraftTotalAmt());
						BigDecimal draftRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getDraftTotalAmt())!=0){
							draftRatio = draftNum.divide(data.getDraftTotalAmt() ,4, BigDecimal.ROUND_HALF_UP);
						}
						
						BigDecimal marginNum = form.getMarginTotalAmt().subtract(data.getMarginTotalAmt());
						BigDecimal marginRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getMarginTotalAmt())!=0){
							marginRatio = marginNum.divide(data.getMarginTotalAmt(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						form.setProCompYestNum(proNum);//协议较上日增减值              ;
						form.setProCompYestRatio(proRatio);//协议较上日增减比例          ;
						
						form.setDraftCompYestNum(draftNum);//入池票据较上日增减值        ;
						form.setDraftCompYestRatio(draftRatio);//入池票据较上日增减比例    ;
						
						form.setMarginCompYestNum(marginNum);//保证金较上日增减值         ;
						form.setMarginCompYestRatio(marginRatio);//保证金较上日增减比例     ;
						
					}else if(lastDayOfLastMonthFormart.equals(data.getCreateDate())){//上月末最后一天
						logger.info("上月末最后一天");
						BigDecimal proNum = form.getProTotalNum().subtract(data.getProTotalNum());
						BigDecimal proRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getProTotalNum())!=0){
							proRatio = proNum.divide(data.getProTotalNum(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						BigDecimal draftNum = form.getDraftTotalAmt().subtract(data.getDraftTotalAmt());
						BigDecimal draftRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getDraftTotalAmt())!=0){
							draftRatio = draftNum.divide(data.getDraftTotalAmt() ,4, BigDecimal.ROUND_HALF_UP);
						}
						
						BigDecimal marginNum = form.getMarginTotalAmt().subtract(data.getMarginTotalAmt());
						BigDecimal marginRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getMarginTotalAmt())!=0){
							marginRatio = marginNum.divide(data.getMarginTotalAmt(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						
						form.setProCompLastMNum(proNum);//协议较上月末增减值           ;
						form.setProCompLastMRatio(proRatio);//协议较上月末增减比率       ;
						
						form.setDraftCompLastMNum(draftNum);//入池票据较上月末增减值     ;
						form.setDraftCompLastMRatio(draftRatio);//入池票据较上月末增减比率 ;
						
						form.setMarginCompLastMNum(marginNum);//保证金较上月末增减值      ;
						form.setMarginCompLastMRatio(marginRatio);//保证金较上月末增减比率  ;
						
						
					}else if(lastDayOfLastYearFormart.equals(data.getCreateDate())){//上年末最后一天
						logger.info("上年末最后一天");
						BigDecimal proNum = form.getProTotalNum().subtract(data.getProTotalNum());
						BigDecimal proRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getProTotalNum())!=0){
							proRatio = proNum.divide(data.getProTotalNum(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						BigDecimal draftNum = form.getDraftTotalAmt().subtract(data.getDraftTotalAmt());
						BigDecimal draftRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getDraftTotalAmt())!=0){
							draftRatio = draftNum.divide(data.getDraftTotalAmt() ,4, BigDecimal.ROUND_HALF_UP);
						}
						
						BigDecimal marginNum = form.getMarginTotalAmt().subtract(data.getMarginTotalAmt());
						BigDecimal marginRatio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getMarginTotalAmt())!=0){
							marginRatio = marginNum.divide(data.getMarginTotalAmt(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						
						form.setProCompLastYNum(proNum);//协议较上年增减值             ;
						form.setProCompLastYRatio(proRatio);//协议较上年增减比率         ;
						
						form.setDraftCompLastYNum(draftNum);//入池票据较上年增减值       ;
						form.setDraftCompLastYRatio(draftRatio);//入池票据较上年增减比率   ;
						
						form.setMarginCompLastYNum(marginNum);//保证金较上年增减值        ;
						form.setMarginCompLastYRatio(marginRatio);//保证金较上年增减比率    ;
						
					}
					logger.info("机构号："+form.getBranchNo()+",机构名称："+form.getBranchName()+",创建日期："+form.getCreateDate()+",协议总比数："+form.getProTotalNum()
							+",截至当日签约数量全行排名："+form.getProRank()+"，截至当日的票据余额全行排名："+form.getDraftRank()+"，截至当日保证金余额全行排名："+form.getMarginRank()
							+",协议较上日增减值："+form.getProCompYestNum()+"，总票据金额："+form.getDraftTotalAmt()+"，总保证金金额："+form.getMarginTotalAmt()
							+",协议较上日增减比例："+form.getProCompYestRatio()+"，协议较上月末增减比率 ："+form.getProCompLastMRatio()
							+"，协议较上月末增减值："+form.getProCompLastMNum()+"，协议较上年增减值："+form.getProCompLastYNum()+"，协议较上年增减比率："+form.getProCompLastYRatio()
							+"，入池票据较上日增减值："+form.getDraftCompYestNum()+"，入池票据较上日增减比例："+form.getDraftCompYestRatio()+"，入池票据较上月末增减值："+form.getDraftCompLastMNum()
							+"，入池票据较上年增减值："+form.getDraftCompLastYNum()+"，入池票据较上年增减比率 ："+form.getDraftCompLastYRatio()+"，保证金较上日增减值："+form.getMarginCompYestRatio()
							+"，保证金较上日增减比例  ："+form.getMarginCompYestRatio()+"，保证金较上月末增减值："+form.getMarginCompLastMNum()+"，保证金较上月末增减比率："+form.getMarginCompLastMRatio()
							+"，保证金较上年增减值："+form.getMarginCompLastYNum()+"，保证金较上年增减比率："+form.getMarginCompLastYRatio());
				}				
				allForms.add(form);
			}
			logger.info("总数居："+allForms.size());
			
		this.txStoreAll(allForms);
		this.dao.flush();
		logger.info("--------------------------");
		}
		
		logger.info("机构总数居："+result1.size());
	
	}
	

	@Override
	public void txCreatCreditReportInfo(String busiType) throws Exception {

		List<RCreditReportInfo> infos = new ArrayList<RCreditReportInfo>();
		
		Date yesterday = DateUtils.adjustDateByDay(new Date(), -1);
    	Date yesterdayFormat =  sdf.parse(sdf.format(yesterday));
		
		
		/*
		 * 1.按机构统计有效签约客户数量
		 */

		List<Department> result1 = departmentService.getAllDeptNoDif();
		
		/*
		 * 2.循环处理每个机构下的信息
		 */
		if(result1==null){
			return;
		}
		for(int i=0;i<result1.size();i++){
			Department dept = (Department) result1.get(i);
			
			
			/*
			 * 3.获取该机构下所有的票据池编号
			 */
			List result2 = null;//票据池编号列表
			if(dept.getInnerBankCode()!=null){					
				result2 = this.queryBpsNoByBranchNo(dept.getInnerBankCode().toString());
			}
			
			if(result2!=null &&result2.size()>0){
				RCreditReportInfo info = new RCreditReportInfo();
				
				info.setCreateDate(yesterdayFormat);//跑批在t+1日生成t日报表
				
				
				if(dept.getInnerBankCode()!=null){
					info.setBranchNo(dept.getInnerBankCode().toString());//机构号					
				}
				if(dept.getName()!=null){
					info.setBranchName(dept.getName().toString());//机构名称
				}
				
				/*
				 * 4.根据票据池编号及业务类型统计未结清的融资主业务合同号
				 */
				List productNos = this.queryCedtProductList(result2, busiType);
				
				/*
				 * 5.根据未结清的主业务合同号统计业务发生总额
				 */
				if(productNos!=null){
					BigDecimal usedBalance = creditRegisterService.queryCreditBalance(productNos);
					info.setBusiAmt(usedBalance);//总业务金额	
				}else{
					info.setBusiAmt(BigDecimal.ZERO);//总业务金额	
				}
				
				info.setBusiType(busiType);//业务类型
				infos.add(info);

				
			}else{//如果该机构下未签约票据池业务，则存0
				RCreditReportInfo info = new RCreditReportInfo();
				
				info.setCreateDate(yesterdayFormat);//跑批在t+1日生成t日报表
				
				
				if(dept.getInnerBankCode()!=null){
					info.setBranchNo(dept.getInnerBankCode().toString());//机构号					
				}
				if(dept.getName()!=null){
					info.setBranchName(dept.getName().toString());//机构名称
				}
				
				info.setBusiAmt(BigDecimal.ZERO);//总业务金额	
				info.setBusiType(busiType);//业务类型
				infos.add(info);
			}
			
		}
		/*
		 * 6.保存额度报表当日基本信息
		 */
		this.txStoreAll(infos);
		this.dao.flush();
		
		/*
		 * 7.全行名次信息更新
		 */
		
		//【全行排名】更新
		List<RCreditReportInfo> rank= this.queryRCreditReportInfoList(busiType);
		if(rank!=null){
			List<RCreditReportInfo> ranks = new ArrayList<RCreditReportInfo>();
			for(int i=0;i<rank.size();i++){
				RCreditReportInfo info = rank.get(i);
				info.setBankRank(i+1+"");
				ranks.add(info);
			}
			this.txStoreAll(ranks);
			this.dao.flush();
		}
		
		/*
		 * 7.数据增减值处理
		 * 		（1）较上日增减值、增减比例数据处理 
		 * 		（2）较上月末增减值、增减比例数据处理
		 * 		（3）较上年末增减值、增减比例数据处理
		 */
		if(result1!=null){
			this.txCreditFormDataHandle(result1,busiType);
		}
		
	
		
	}

	@Override
	public List<RPoolReportInfo> queryRPoolReportInfoJSON( RPoolReportInfo rPoolReportInfo, Date beginDate, Date endDate,Page page,User user,String flag) throws Exception{
		
		List list= new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append("select rf from RPoolReportInfo rf where 1=1 ");
		
		List keyList = new ArrayList(); 									// 要查询的字段列表
		List valueList = new ArrayList();								// 要查询的值列表		
		if(rPoolReportInfo != null){
			if(StringUtil.isNotBlank(rPoolReportInfo.getBranchNo())){//报表业务类型
				sb.append("and rf.branchNo = :branchNo ");
				keyList.add("branchNo");
				valueList.add(rPoolReportInfo.getBranchNo());
			}
			if(StringUtil.isNotBlank(rPoolReportInfo.getBranchName())){//报表名称
				sb.append("and rf.branchName like :branchName ");
				keyList.add("branchName");
				valueList.add("%"+rPoolReportInfo.getBranchName()+"%");
			}
			/*if(StringUtil.isNotBlank(reportForm.getDraftStuff())){//票据介质类型（纸票、电票）
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
			}*/
		}
		if (null != beginDate) {
			sb.append(" and rf.createDate = :beginDate");
			keyList.add("beginDate");
			valueList.add(beginDate);
		}
		/*if (null != endDate) {
			sb.append(" and rf.createDate<= :endDate");
			keyList.add("endDate");
			valueList.add(DateUtils.modDay(endDate, 1));
		}*/
		if("2".equals(flag)){
			sb.append(" order by cast ( rf.draftRank as int)  "); //按票据金额排名
		}else if("3".equals(flag)){
			sb.append(" order by cast ( rf.marginRank as int)  "); //按保证金排名
		}else{			
			sb.append(" order by cast ( rf.proRank as int)  "); //按协议排名
		}
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		if(page != null){ // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		}else{
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		return list;
	}
	
	@Override
	public List<RCreditReportInfoBean> queryreportFinanceJSON( RCreditReportInfoBean rCreditReportInfo,Date beginDate, Date endDate,Page page,User user) throws Exception{
		
		List list= new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append("select rf from RCreditReportInfo rf where 1=1 ");
		
		List keyList = new ArrayList(); 									// 要查询的字段列表
		List valueList = new ArrayList();								// 要查询的值列表		
		if(rCreditReportInfo != null){
			if(StringUtil.isNotBlank(rCreditReportInfo.getBranchNo())){//业务机构号
				sb.append("and rf.branchNo = :branchNo ");
				keyList.add("branchNo");
				valueList.add(rCreditReportInfo.getBranchNo());
			}
			if(StringUtil.isNotBlank(rCreditReportInfo.getBranchName())){//业务机构名称
				sb.append("and rf.branchName like :branchName ");
				keyList.add("branchName");
				valueList.add("%"+rCreditReportInfo.getBranchName()+"%");
			}
			if(StringUtil.isNotBlank(rCreditReportInfo.getBusiType())){//业务类型
				sb.append("and rf.busiType = :busiType ");
				keyList.add("busiType");
				valueList.add(rCreditReportInfo.getBusiType());
			}else{
				sb.append("and rf.busiType = :busiType ");
				keyList.add("busiType");
				valueList.add(PoolComm.XD_06);//默认展示全部
			}
			//查询开始日期
			/*if (rCreditReportInfo.getCreateDateStart() != null && !"".equals(rCreditReportInfo.getCreateDateStart())) {
				sb.append(" and po.createDate>=TO_DATE(:createDateStart, 'yyyy-mm-dd hh24:mi:ss')");
				keyList.add("createDateStart");
				valueList.add(DateUtils.toString(rCreditReportInfo.getCreateDateStart(), "yyyy-MM-dd") + " 00:00:00");
			}
			//查询结束日期 
			if (rCreditReportInfo.getCreateDateEnd() != null && !"".equals(rCreditReportInfo.getCreateDateEnd())) {
				sb.append(" and po.createDate<=TO_DATE(:createDateEnd, 'yyyy-mm-dd hh24:mi:ss')");
				keyList.add("createDateEnd");
				valueList.add(DateUtils.toString(rCreditReportInfo.getCreateDateEnd(), "yyyy-MM-dd") + " 23:59:59");
			}*/

			/*if(StringUtil.isNotBlank(reportForm.getDraftStuff())){//票据介质类型（纸票、电票）
				sb.append("and rf.draftStuff = :draftStuff ");
				keyList.add("draftStuff");
				valueList.add(reportForm.getDraftStuff());
			}
			if(StringUtil.isNotBlank(reportForm.getFormType())){//报表类型    月度/季度/年度/其他
				sb.append("and rf.formType = :formType ");
				keyList.add("formType");
				valueList.add(reportForm.getFormType());
			}*/
		}
		if (null != beginDate) {
			sb.append(" and rf.createDate = :beginDate");
			keyList.add("beginDate");
			valueList.add(beginDate);
		}
		/*if (null != endDate) {
			sb.append(" and rf.createDate<= :endDate");
			keyList.add("endDate");
			valueList.add(DateUtils.modDay(endDate, 1));
		}*/
		sb.append(" order by cast ( rf.bankRank as int ) "); //按报表的创建日期由新往后排列
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		if(page != null){ // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		}else{
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		
		return list;
	}
	
	@Override
	public RPoolReportInfo queryReportPoolById(String id){
			String hql = "from RPoolReportInfo ppd where ppd.id ='"
				+ id+"'";
			List find = this.find(hql);
			RPoolReportInfo rPoolReportInfo = null;
			if (find != null && find.size() > 0) {
				rPoolReportInfo = (RPoolReportInfo) find.get(0);
			}
		return rPoolReportInfo;
	}
	
	
	@Override
	public RCreditReportInfo queryRCreditReportById(String id){
			String hql = "from RCreditReportInfo ppd where ppd.id ='"
				+ id+"'";
			List find = this.find(hql);
			RCreditReportInfo rCreditReportInfo = null;
			if (find != null && find.size() > 0) {
				rCreditReportInfo = (RCreditReportInfo) find.get(0);
			}
		return rCreditReportInfo;
	}
	
	@Override
	public List findReportPoolByBeanExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[9];
				RPoolReportInfo bill = (RPoolReportInfo) res.get(i);
				s[0] = bill.getBranchName();//机构名称
				if (bill.getCreateDate() != null) {
					s[1] = String.valueOf(bill.getCreateDate()).substring(0, 10);
				}else{
					s[1] = "";
				}
				s[2] = String.valueOf(bill.getProCompYestNum());//协议
				s[3] = String.valueOf(bill.getProCompYestRatio());//协议
				s[4] = String.valueOf(bill.getProCompLastMNum());//协议
				s[5] = String.valueOf(bill.getProCompLastMRatio());//协议
				s[6] = String.valueOf(bill.getProCompLastYNum());//协议
				s[7] = String.valueOf(bill.getProCompLastYRatio());//协议
				s[8] = bill.getProRank();
				
				/*if (bill.getCreateDate() != null) {
					s[9] = String.valueOf(bill.getCreateDate()).substring(0, 10);
				}else{
					s[9] = "";
				}*/
				/*s[9] = String.valueOf(bill.getDraftCompYestNum());//入池票据
				s[10] = String.valueOf(bill.getDraftCompYestRatio());//入池票据
				s[11] = String.valueOf(bill.getDraftCompLastMNum());//入池票据
				s[12] = String.valueOf(bill.getDraftCompLastMRatio());//入池票据
				s[13] = String.valueOf(bill.getDraftCompLastYNum());//入池票据
				s[14] = String.valueOf(bill.getDraftCompLastYRatio());//入池票据
				s[15] = bill.getDraftRank();
				
				if (bill.getCreateDate() != null) {
					s[17] = String.valueOf(bill.getCreateDate()).substring(0, 10);
				}else{
					s[17] = "";
				}
				s[16] = String.valueOf(bill.getMarginCompYestNum());//保证金
				s[17] = String.valueOf(bill.getMarginCompYestRatio());//保证金
				s[18] = String.valueOf(bill.getMarginCompLastMNum());//保证金
				s[19] = String.valueOf(bill.getMarginCompLastMRatio());//保证金
				s[20] = String.valueOf(bill.getMarginCompLastYNum());//保证金
				s[21] = String.valueOf(bill.getMarginCompLastYRatio());//保证金
				s[22] = bill.getMarginRank();*/

				list.add(s);
			}
		}
		return list;
	}
	@Override
	public List findReportPoolByBeanAmtExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[9];
				RPoolReportInfo bill = (RPoolReportInfo) res.get(i);
				s[0] = bill.getBranchName();//机构名称
				if (bill.getCreateDate() != null) {
					s[1] = String.valueOf(bill.getCreateDate()).substring(0, 10);
				}else{
					s[1] = "";
				}
				s[2] = String.valueOf(bill.getDraftCompYestNum());//入池票据
				s[3] = String.valueOf(bill.getDraftCompYestRatio());//入池票据
				s[4] = String.valueOf(bill.getDraftCompLastMNum());//入池票据
				s[5] = String.valueOf(bill.getDraftCompLastMRatio());//入池票据
				s[6] = String.valueOf(bill.getDraftCompLastYNum());//入池票据
				s[7] = String.valueOf(bill.getDraftCompLastYRatio());//入池票据
				s[8] = bill.getDraftRank();
				list.add(s);
			}
		}
		return list;
	}
	@Override
	public List findReportPoolByBeanBoExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[9];
				RPoolReportInfo bill = (RPoolReportInfo) res.get(i);
				s[0] = bill.getBranchName();//机构名称
				if (bill.getCreateDate() != null) {
					s[1] = String.valueOf(bill.getCreateDate()).substring(0, 10);
				}else{
					s[1] = "";
				}
				s[2] = String.valueOf(bill.getMarginCompYestNum());//保证金
				s[3] = String.valueOf(bill.getMarginCompYestRatio());//保证金
				s[4] = String.valueOf(bill.getMarginCompLastMNum());//保证金
				s[5] = String.valueOf(bill.getMarginCompLastMRatio());//保证金
				s[6] = String.valueOf(bill.getMarginCompLastYNum());//保证金
				s[7] = String.valueOf(bill.getMarginCompLastYRatio());//保证金
				s[8] = bill.getMarginRank();
				list.add(s);
			}
		}
		return list;
	}
	@Override
	public List findRCreditReportExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[10];
				RCreditReportInfo bill = (RCreditReportInfo) res.get(i);
				s[0] = bill.getBranchName();//机构名称
				s[1] = this.busiType(bill.getBusiType());
				if (bill.getCreateDate() != null) {
					s[2] = String.valueOf(bill.getCreateDate()).substring(0, 10);
				}else{
					s[2] = "";
				}
				s[3] = String.valueOf(bill.getYestNum());//协议
				s[4] = String.valueOf(bill.getYestRatio());//协议
				s[5] = String.valueOf(bill.getLastMNum());//协议
				s[6] = String.valueOf(bill.getLastMRatio());//协议
				s[7] = String.valueOf(bill.getLastYNum());//协议
				s[8] = String.valueOf(bill.getLastYRatio());//协议
				s[9] = bill.getBankRank();
				
				

				list.add(s);
			}
		}
		return list;
	}
	public String busiType(String value) {
		if (value != null) {
			if (value.equals(PoolComm.XD_01)) {
				return "银承";
			} else if (value.equals(PoolComm.XD_02)) {
				return "流贷";
			} else if (value.equals(PoolComm.XD_03)) {
				return "保函";
			} else if (value.equals(PoolComm.XD_04)) {
				return "信用证";
			} else if (value.equals(PoolComm.XD_05)) {
				return "表外业务垫款";
			} else if (value.equals(PoolComm.XD_06)) {
				return "全部信贷业务交易类型";
			}
		}
		return "";
	}
	
	/**
	 * 根据票据池编号查询未结清的主业无合同号
	 * @author Ju Nana
	 * @param crdtNo
	 * @param crdtType
	 * @return
	 * @throws Exception
	 * @date 2019-8-10下午3:50:38
	 */
	public List queryCedtProductList(List bpsNos,String crdtType)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" select cp.crdtNo from CreditProduct cp where 1=1 ");
		List value = new ArrayList();
		List key = new ArrayList();
		
		hql.append(" and cp.bpsNo in (:bpsNos) ");
		value.add("bpsNos");
		key.add(bpsNos);
		
		hql.append(" and cp.sttlFlag=:sttlFlag ");
		value.add("sttlFlag");
		key.add(PoolComm.JQZT_WJQ);//未结清
		
		if(!PoolComm.XD_06.equals(crdtType)){
			hql.append(" and cp.crdtType =:crdtType ");
			value.add("crdtType");
			key.add(crdtType);
		}
		
		String paramNames[] = (String[]) value.toArray(new String[value.size()]);
		Object paramValues[] = key.toArray();
		List rslt = this.find(hql.toString(), paramNames, paramValues);
		if(rslt!=null && rslt.size()>0){
			return rslt;			
		}
		return null;
	}
	
	/**
	 * 查询当日生成的报表信息列表
	 * @author Ju Nana
	 * @return
	 * @throws Exception
	 * @date 2019-8-16上午9:57:49
	 */
	private List<RCreditReportInfo>  queryRCreditReportInfoList(String busiType)throws Exception{	
		Date yesterday = DateUtils.adjustDateByDay(new Date(), -2);
		Date tomorrow = DateUtils.adjustDateByDay(new Date(), 0);
		
		List paramName5 = new ArrayList();// 名称
		List paramValue5 = new ArrayList();// 值
		StringBuffer sqlStr5 = new StringBuffer("select port from RCreditReportInfo as port where 1=1 "); 
		
		
		sqlStr5.append(" and port.createDate > :yesterday");
		paramName5.add("yesterday");
		paramValue5.add(yesterday);
		
		sqlStr5.append(" and port.createDate < :tomorrow");
		paramName5.add("tomorrow");
		paramValue5.add(tomorrow);
		
		sqlStr5.append(" and port.busiType = :busiType");
		paramName5.add("busiType");
		paramValue5.add(busiType);
		
		sqlStr5.append(" order by  port.busiAmt desc  ");			
		
		String paramNames5[] = (String[]) paramName5.toArray(new String[paramName5.size()]);
		Object paramValues5[] = paramValue5.toArray();
		List result5 = this.find(sqlStr5.toString(), paramNames5, paramValues5);
		
		if(result5!=null && result5.size()>0){
			return result5;
		}
		
		return null;
		
	}
	
	/**
	 * 票据池融资业务报表数据比对处理
	 * @author Ju Nana
	 * @param result1
	 * @throws Exception
	 * @date 2019-7-19上午11:34:26
	 */
	public void txCreditFormDataHandle(List<Department> result1,String busiType) throws Exception{
		
		List<RCreditReportInfo> allForms = new ArrayList<RCreditReportInfo>();
		
		Date yesterday = DateUtils.adjustDateByDay(new Date(), -1);
    	Date yesterdayFormat =  sdf.parse(sdf.format(yesterday));//昨天

    	Date beforeYesterday = DateUtils.adjustDateByDay(new Date(), -2);
    	Date beforeYesterdayFormat =  sdf.parse(sdf.format(beforeYesterday));//前天
    	
    	Date lastDayOfLastMonth = DateTimeUtil.getLastDayOfLastMonth();  
    	Date lastDayOfLastMonthFormart =  sdf.parse(sdf.format(lastDayOfLastMonth));//上月末最后一天
    	
    	Date lastDayOfLastYear = DateTimeUtil.getLastDayOfLastYear();
    	Date lastDayOfLastYearFormart =  sdf.parse(sdf.format(lastDayOfLastYear));//上年末最后一天
    	
    	for(Department dept : result1){
			String branchNo = dept.getInnerBankCode();
			String branchName = dept.getName();
			RCreditReportInfo form = this.queryPoolCreditForm(yesterdayFormat, branchNo,busiType);
			if(form==null){
				continue;
			}
			form.setBranchNo(branchNo);
			form.setBranchName(branchName);
			BigDecimal ZERO = BigDecimal.ZERO;
			form.setYestNum(ZERO);//较上日增减值         ;
			form.setYestRatio(ZERO);//较上日增减比例     ;
			form.setLastMNum(ZERO);//较上月末增减值      ;
			form.setLastMRatio(ZERO);//上月末增减比率  ;
			form.setLastYNum(ZERO);//较上年增减值        ;
			form.setLastYRatio(ZERO);//较上年增减比率    ;
			
			List<RCreditReportInfo> list = this.queryCreditFormDateSrc(branchNo,busiType);
			if(list==null){
				continue;
			}
			
			if(list!=null){
				for(RCreditReportInfo data : list ){
					if(beforeYesterdayFormat.equals(data.getCreateDate())){//前天						
						BigDecimal num = form.getBusiAmt().subtract(data.getBusiAmt());
						BigDecimal ratio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getBusiAmt())!=0){
							ratio = num.divide(data.getBusiAmt(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						
						form.setYestNum(num);//较上日增减值         ;
						form.setYestRatio(ratio);//较上日增减比例     ;
						
					}else if(lastDayOfLastMonthFormart.equals(data.getCreateDate())){//上月末最后一天
						
						
						BigDecimal num = form.getBusiAmt().subtract(data.getBusiAmt());
						BigDecimal ratio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getBusiAmt())!=0){
							ratio = num.divide(data.getBusiAmt(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						
						form.setLastMNum(num);//较上月末增减值      ;
						form.setLastMRatio(ratio);//较上月末增减比率  ;
						
						
					}else if(lastDayOfLastYearFormart.equals(data.getCreateDate())){//上年末最后一天
						
						
						BigDecimal num = form.getBusiAmt().subtract(data.getBusiAmt());
						BigDecimal ratio = ZERO;
						if(BigDecimal.ZERO.compareTo(data.getBusiAmt())!=0){
							ratio = num.divide(data.getBusiAmt(),4, BigDecimal.ROUND_HALF_UP);
						}
						
						form.setLastYNum(num);//较上年增减值        ;
						form.setLastYRatio(ratio);//较上年增减比率    ;
						
					}
				}
				
				allForms.add(form);
			}
			
			
		}
		this.txStoreAll(allForms);
		this.dao.flush();
	
	}
	
	
	/**
	 * 根据日期与机构号查询票据池融资业务报表实体
	 * @author Ju Nana
	 * @param date
	 * @param branchNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-19上午10:44:47
	 */
	private RCreditReportInfo queryPoolCreditForm(Date date,String branchNo,String busiType) throws Exception{
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer sqlStr = new StringBuffer("select port from RCreditReportInfo as port where 1=1 "); 
		
		sqlStr.append(" and port.branchNo = :branchNo");
		paramName.add("branchNo");
		paramValue.add(branchNo);
		
		sqlStr.append(" and port.createDate =:date");
		paramName.add("date");
		paramValue.add(date);
		
		sqlStr.append(" and port.busiType =:busiType");
		paramName.add("busiType");
		paramValue.add(busiType);
		
		sqlStr.append(" order by port.createDate desc ");	
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(sqlStr.toString(), paramNames, paramValues);
		
		if(result!=null && result.size()>0){
			return (RCreditReportInfo) result.get(0);
		}
		
		return null;
		
	}
	
	/**
	 * 查询处理每个机构的较上日、上月末、上年末的融资业务数据源
	 * @author Ju Nana
	 * @param branchNo
	 * @throws Exception
	 * @date 2019-8-19上午9:40:50
	 */
	private List<RCreditReportInfo> queryCreditFormDateSrc(String branchNo,String busiType) throws Exception{
		
		List<Date> dayLsit = new ArrayList<Date>();
		
		Date beforeYesterday = DateUtils.adjustDateByDay(new Date(), -2);
    	Date beforeYesterdayFormat =  sdf.parse(sdf.format(beforeYesterday));//前天
    	dayLsit.add(beforeYesterdayFormat);
    	
    	
    	Date lastDayOfLastMonth = DateTimeUtil.getLastDayOfLastMonth();  
    	Date lastDayOfLastMonthFormart =  sdf.parse(sdf.format(lastDayOfLastMonth));//上月末最后一天
    	dayLsit.add(lastDayOfLastMonthFormart);
    	
    	Date lastDayOfLastYear = DateTimeUtil.getLastDayOfLastYear();
    	Date lastDayOfLastYearFormart =  sdf.parse(sdf.format(lastDayOfLastYear));//上年末最后一天
    	dayLsit.add(lastDayOfLastYearFormart);
    	
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer sqlStr = new StringBuffer("select port from RCreditReportInfo as port where 1=1 "); 
		
		sqlStr.append(" and port.branchNo = :branchNo");
		paramName.add("branchNo");
		paramValue.add(branchNo);
		
		sqlStr.append(" and port.createDate in (:dayLsit)");
		paramName.add("dayLsit");
		paramValue.add(dayLsit);
		
		sqlStr.append(" and port.busiType =:busiType");
		paramName.add("busiType");
		paramValue.add(busiType);
		
		sqlStr.append(" order by port.createDate desc ");	
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(sqlStr.toString(), paramNames, paramValues);
		
		if(result!=null && result.size()>0){
			return result;
		}
		
		return null;
		
	}
	
	/**
	 * 查询无签约票据池系统的的机构信息
	 * @author Ju Nana
	 * @param signDeptNo  签约信息的机构号字符串
	 * @throws Exception
	 * @date 2019-8-19上午9:40:50
	 */
	private List<Department> getNoSignDept(String signDeptNo) {
		List list = new ArrayList(); 	
		List list1 = new ArrayList(); 		
		List list2 = new ArrayList(); 	
		
		/*
		 * 有重复的机构去重之后的机构数
		 */
		String sql1 = " SELECT a.d_innerbankcode ,a.d_name FROM t_department a where  ";
		sql1 = sql1 + "  a.d_innerbankcode  in (   SELECT b.d_innerbankcode  FROM  t_department b GROUP BY  b.d_innerbankcode having count(*)>1 )  ";
		sql1 = sql1 + "  and a.rowid not in (   SELECT min(rowid)  FROM  t_department c GROUP BY  c.d_innerbankcode having count(*)>1 ) ";
		sql1 = sql1 + "  and d_innerbankcode not in ( "+signDeptNo+" ) ";
		list1 = this.dao.SQLQuery(sql1);
		
		/*
		 * 无重复机构的机构
		 */
		String sql2 = " SELECT a.d_innerbankcode ,a.d_name FROM t_department a where   ";
		sql2 = sql2 + "  a.d_innerbankcode not in  ( SELECT b.d_innerbankcode  FROM  t_department b GROUP BY  b.d_innerbankcode having count(*)>1 )  ";
		sql2 = sql2 + "  and d_innerbankcode not in ( "+signDeptNo+" ) ";
		list2 = this.dao.SQLQuery(sql2);
		
		if(list1!=null && list1.size()>0){
			list.addAll(list1);
		}
		if(list2!=null && list2.size()>0){
			list.addAll(list2);
		}
		
		List<Department> Deptlist = new ArrayList<Department>(); 
		if(list!=null && list.size()>0){
			for(int i = 0 ;i<list.size();i++ ){
				Department dept = new Department();
				Object[] obj = (Object[]) list.get(i);
				if(obj[0]!=null){
					dept.setInnerBankCode(obj[0].toString());
				}
				if(obj[1]!=null){
					dept.setName(obj[1].toString());
				}
				Deptlist.add(dept);
			}
			return Deptlist;
		}
		
		
		return null;
	}
	
	
}
