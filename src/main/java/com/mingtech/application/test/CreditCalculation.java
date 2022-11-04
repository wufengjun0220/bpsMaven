package com.mingtech.application.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.framework.common.util.DateTimeUtil;
import common.Logger;


public class CreditCalculation {
	
	private static final Logger logger = Logger.getLogger(CreditCalculation.class);
	
	/**
	 * 期限配比模式下票据池各区间段额度计算
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-8下午11:46:13
	 */
	public List<CreditDto> creditCal(String bpsNo){
		
		logger.info("期限配比模式，票据池【"+bpsNo+"】各区间段额度计算开始....");
		
		List<CreditDto> creditList = new ArrayList<CreditDto>();
		
		//时间段划分
		creditList = this.creditCalTask1(bpsNo);
		
		//高低风险现金流入数据组装
		creditList = this.creditCalTask2(creditList, bpsNo);
		
		//高低风险现金流出数据组装
		creditList = this.creditCalTask3(creditList, bpsNo);
		
		//最终额度信息组装
		creditList = this.creditCalTask4(creditList, bpsNo);
		
		logger.info("期限配比模式，票据池【"+bpsNo+"】各区间段额度计算结束....");
		
		return creditList;
	}
	
	/**
	 * 时间段划分
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-7上午11:09:01
	 */
	public List<CreditDto> creditCalTask1(String bpsNo){
		
		logger.info("票据池【"+bpsNo+"】额度计算时间段划分开始....");
		
		/*
		 * 时间区间分段
		 * 		1）查询产生额度票据（含高低风险）到期日，按照到期日分组
		 *      2）查询用信合同项下未结清的借据到期日（无借据的按照合同的最早到期日）,按照到期日分组
		 *      3) 查询有效的主业务合同项下无借据的合同的最早到期日,按照到期日分组
		 *      3）将如上三个取并集
		 *      
		 *      
		 *      注意：冻结情况另行考虑
		 */
		List<Date> dueDateList = new ArrayList<Date>(); 
		//  SELECT a.dueDt FROM
		//  (SELECT PL_DUEDT dueDt FROM PL_POOL  WHERE PL_STATUS IN ('DS_02','DS_05') GROUP BY PL_DUEDT -- AND 票据池编号
		//		UNION
		//  SELECT EDN_TIME dueDt FROM PED_CREDIT_DETAIL  WHERE LOAN_STATUS ='JJ_01' GROUP BY EDN_TIME ) a GROUP BY a.dueDt ORDER BY a.dueDt -- AND 票据池编号
		//		UNION
		//  SELECT 最早到期日  from ped_crdt_product where 票据池编号 and 当日  and 线下业务
		
		//假如上面查出来的结果如下：
		dueDateList.add(DateTimeUtil.parse("2021-08-16","yyyy-mm-dd"));
		dueDateList.add(DateTimeUtil.parse("2021-08-19","yyyy-mm-dd"));
		dueDateList.add(DateTimeUtil.parse("2021-08-23","yyyy-mm-dd"));
		dueDateList.add(DateTimeUtil.parse("2021-08-26","yyyy-mm-dd"));
		dueDateList.add(DateTimeUtil.parse("2021-08-28","yyyy-mm-dd"));
		dueDateList.add(DateTimeUtil.parse("2021-08-30","yyyy-mm-dd"));
		
		//时间段划分，起始日、截止日组装，默认最后一条的截至时间为2099-12-31
		
		List<CreditDto> creditList = new ArrayList<CreditDto>();
		if(dueDateList.size()>0){
			for(int i =0;i<dueDateList.size();i++){
				CreditDto crdt = new CreditDto();
				Date dueDt = dueDateList.get(i);
				crdt.setStartDate(dueDt);
				if(i<dueDateList.size()-1){
					Date dueDt2 = dueDateList.get(i+1);
					crdt.setEndDate(DateTimeUtil.getUtilDate(dueDt2, -1));//前一天
				}else{
					crdt.setEndDate(DateTimeUtil.parse("2099-12-31","yyyy-mm-dd"));
				}
				creditList.add(crdt);
			}
			
		}
			
		return creditList;
		
	}
	/**
	 * 高低风险现金流入
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-8下午8:00:56
	 */
	public List<CreditDto> creditCalTask2(List<CreditDto> creditList,String bpsNo){
		
		logger.info("票据池【"+bpsNo+"】额度计算高低风险现金流入数据组装开始....");
		
		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtIn = BigDecimal.ZERO;//低风险额度总额
		BigDecimal highTotalAmtIn = BigDecimal.ZERO;//高风险额度总额
		//核心保证金同步，假设同步到的金额为  10万
		BigDecimal balAmt = new BigDecimal("100000"); 
		lowTotalAmtIn = balAmt;
		
		
		//在池票据查询       SELECT * FROM PL_POOL  WHERE PL_STATUS IN ('DS_02','DS_05') order by PL_DUEDT asc ; -- AND 票据池编号
		List<DraftPool> billList = new ArrayList<DraftPool>();
		//billList = //查询结果
		if(billList.size()>0){
			for(DraftPool billInf : billList){
				// 到期日在[开始时间，截至时间)之内;     即： 开始日期<=到期日，!截止日<=到期日，
				if(DateTimeUtil.compartdate(creditList.get(count).getStartDate(),billInf.getPlDueDt() )&&
						!DateTimeUtil.compartdate(creditList.get(count).getEndDate(),billInf.getPlDueDt() )	){
					if(PoolComm.LOW_RISK.equals(billInf.getRickLevel())){//低风险
						lowTotalAmtIn =  lowTotalAmtIn.add(billInf.getAssetAmt());
						creditList.get(count).setLowRiskIn(lowTotalAmtIn);//低风险现金流入
						
					}else if(PoolComm.HIGH_RISK.equals(billInf.getRickLevel())){//高风险
						highTotalAmtIn =  highTotalAmtIn.add(billInf.getAssetAmt());
						creditList.get(count).setHighRiskIn(highTotalAmtIn);//高风险现金流入
					}
					
				}
			}
			
		}
		
		
		return creditList;
	}
	/**
	 * 高低风险现金流出
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-8下午10:12:12
	 */
	public List<CreditDto> creditCalTask3(List<CreditDto> creditList,String bpsNo){
		
		logger.info("票据池【"+bpsNo+"】额度计算高低风险现金流出数据组装开始....");
		
		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtOut = BigDecimal.ZERO;//低风险用信额度总额
		BigDecimal highTotalAmtOut = BigDecimal.ZERO;//高风险用信额度总额

		//查询该票据池所有未结清借据      SELECT 到期日 ，实际占用额度 ,风险类型 FROM PED_CREDIT_DETAIL  WHERE LOAN_STATUS ='JJ_01' and 票据池编号 GROUP BY EDN_TIME 
		//并上当天线下合同     select 最早到期日 ，合同金额*系数，风险类型  from ped_crdt_product where 票据池编号 and 当日  
		List crdtList = new ArrayList();
		//billList = //查询结果
		if(crdtList.size()>0){
			Date dueDt = null;
			BigDecimal crdtAmt = BigDecimal.ZERO;
			String riskLevel = null;
			
			for(int k=0;k<crdtList.size();k++){
				Object[] obj = (Object[])crdtList.get(k);
				
				dueDt = (Date) obj[0];
				crdtAmt = new BigDecimal(String.valueOf(obj[1]));
				riskLevel = String.valueOf(obj[2]);
				
				// 到期日在[开始时间，截至时间)之内;     即： 开始日期<=到期日，!截止日<=到期日，
				if(DateTimeUtil.compartdate(creditList.get(count).getStartDate(),dueDt )&&
						!DateTimeUtil.compartdate(creditList.get(count).getEndDate(),dueDt )	){
					if(PoolComm.LOW_RISK.equals(riskLevel)){//低风险
						lowTotalAmtOut =  lowTotalAmtOut.add(crdtAmt);
						creditList.get(count).setLowRiskOut(lowTotalAmtOut);//低风险现金流出
						
					}else if(PoolComm.HIGH_RISK.equals(riskLevel)){//高风险
						highTotalAmtOut =  highTotalAmtOut.add(crdtAmt);
						creditList.get(count).setHighRiskOut(highTotalAmtOut);//高风险现金流出
					}
					
				}
			}

		}
		
		
		return creditList;
	}
	/**
	 * 额度信息组装
	 * @param creditList
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-8下午10:26:06
	 */
	public List<CreditDto> creditCalTask4(List<CreditDto> creditList,String bpsNo){
		
		logger.info("票据池【"+bpsNo+"】额度计算最终额度信息数据组装开始....");
		
		for(int i = 0 ;i<creditList.size();i++){
			CreditDto crdt = creditList.get(i);
			
			//(1)
			logger.info("（1）高低风险现金流入流出为0的数据补齐");
			if(i>0){				
				//当在某个区间段内没有低/高风险的票的情况下，当前低/高风险的现金流入与上一时间区间一致
				//当在某个区间段内没有低/高风险的融资业务的情况下，当前低/高风险的现金流出与上一时间区间一致
				
				//低风险流入，0流入补齐
				if(crdt.getLowRiskIn().compareTo(BigDecimal.ZERO)<=0){
					crdt.setLowRiskIn(creditList.get(i-1).getLowRiskIn());
				}
				//高风险流入，0流入补齐
				if(crdt.getHighRiskIn().compareTo(BigDecimal.ZERO)<=0){
					crdt.setHighRiskIn(creditList.get(i-1).getHighRiskIn());
				}
				//低风险流出，0流出补齐
				if(crdt.getLowRiskOut().compareTo(BigDecimal.ZERO)<=0){
					crdt.setLowRiskOut(creditList.get(i-1).getLowRiskOut());
				}
				//高风险流出，0流出补齐
				if(crdt.getHighRiskOut().compareTo(BigDecimal.ZERO)<=0){
					crdt.setHighRiskOut(creditList.get(i-1).getHighRiskOut());
				}
			}
			
			//(2)高低风险现金流时点组装
			logger.info("(2)高低风险现金流时点组装");
			BigDecimal lowCrdt = BigDecimal.ZERO;
			if(crdt.getHighRiskIn().compareTo(crdt.getHighRiskOut())>=0){//高风险额度足够高风险业务占用				
				crdt.setHighRiskCashFlow(crdt.getHighRiskIn().subtract(crdt.getHighRiskOut()));//高风险现金时点=高风险现金流入-高风险现金流出
			}else{
				crdt.setHighRiskCashFlow(crdt.getHighRiskIn());//占完全部高风险额度
				lowCrdt.add(crdt.getHighRiskOut().subtract(crdt.getHighRiskIn()));//缺少部分留待占用低风险额度
			}
			
			
			//(3)低风险现金流时点组装
			logger.info("(3)低风险现金流时点组装");
			crdt.setLowRiskCashFlow(crdt.getLowRiskIn().subtract(crdt.getLowRiskOut()).subtract(lowCrdt));//低风险现金时点=低风险现金流入-低风险现金流出-高风险业务需要占用低风险的额度
			
			
		}
		
		//(4)双重for循环组装可用额度
		
		logger.info("(4)双重for循环组装可用额度");
		CreditDto crdt1 = null;
		CreditDto crdt2 = null;
		for(int j = 0 ;j<creditList.size();j++){
			
			crdt1 = creditList.get(j);
			
			creditList.get(j).setHighRiskCredit(creditList.get(j).getHighRiskCashFlow());//默认高风险可用额度为当前时点的现金流
			creditList.get(j).setLowRiskCredit(creditList.get(j).getLowRiskCredit());//默认低风险可用额度为当前时点的现金流
			
			for(int k = j+1 ;k<creditList.size()-1;k++){
			    crdt2 = creditList.get(k);
				//高风险可用额度
			    if(crdt1.getHighRiskCashFlow().compareTo(crdt2.getHighRiskCashFlow())>0){//只要后续日期中存在时点上比当前可用额度少，则该时点可用额度为后续最小点的值
					creditList.get(j).setLowRiskCredit(crdt2.getHighRiskCashFlow());
				}
				
			    //低风险可用额度
				if(crdt1.getLowRiskCashFlow().compareTo(crdt2.getLowRiskCashFlow())>0){//只要后续日期中存在时点上比当前可用额度少，则该时点可用额度为后续最小点的值
					creditList.get(j).setLowRiskCredit(crdt2.getLowRiskCashFlow());
				}
			}
			
		}
		
		return creditList;
		
	}
	

}
