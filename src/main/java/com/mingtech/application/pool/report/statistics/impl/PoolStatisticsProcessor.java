package com.mingtech.application.pool.report.statistics.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mingtech.application.report.common.ReportUtils;
import com.mingtech.application.report.domain.StatisticsResult;
import com.mingtech.application.report.statistics.IAnalyzer;
import com.mingtech.application.report.statistics.IStatisticsProcessor;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2019-6-12 下午06:02:06
 * @描述: [PoolStatisticsProcessor]票据池统计Service实现
 */
@Service("iStatisticsProcessor")
public class PoolStatisticsProcessor extends GenericServiceImpl implements
		IStatisticsProcessor{

	private PoolSearcher poolSearcher;
	private List resultAnalyzer;
	
	public void statistics(String type, Date startDate, Date endDate, Department dept, String bankLevel) throws Exception{
		
		StatisticsResult statisticsResult = new StatisticsResult();
		//statisticsResult.setStatisticsType(PublicStaticDefineTab.BILL_MEDIA_ELECTRONICAL_NAME + "-" + ReportUtils.DIM_ITEM_NAME_BK_ACPT + "-" + ReportUtils.AMOUNT + "-" +ReportUtils.BALANCE); // 统计业务类型
		statisticsResult.setStatisticsType(ReportUtils.ACCEPTIONBUSINESS ); // 统计业务类型
		statisticsResult.setStatisticsStartDate(startDate);
		statisticsResult.setStatisticsEndDate(endDate);
		List list = poolSearcher.seracher(startDate, endDate, dept, bankLevel); // 查询
		if(list != null){
			statisticsResult.setSearchResultList(list);
			if(!resultAnalyzer.isEmpty()){
				for(int i = 0; i < list.size(); i++){
					IAnalyzer analyzer = (IAnalyzer)list.get(i);
					if(analyzer != null){
						analyzer.analysis(statisticsResult,dept); // 分析
					}
				}
			}
			statisticsResult.setStatisticsTime(DateUtils.getWorkDateTime()); // 统计时间
			if(dept != null){
				statisticsResult.setDeptId(dept.getId());
			}
			this.txStore(statisticsResult);
		}
		
		
	}
	


	
	public Class getEntityClass(){
		return null;
	}

	
	public String getEntityName(){
		return null;
	}

	public PoolSearcher getPoolSearcher() {
		return poolSearcher;
	}

	public void setPoolSearcher(PoolSearcher poolSearcher) {
		this.poolSearcher = poolSearcher;
	}




	public List getResultAnalyzer(){
		return resultAnalyzer;
	}
	public void setResultAnalyzer(List resultAnalyzer){
		this.resultAnalyzer = resultAnalyzer;
	}
	
}
