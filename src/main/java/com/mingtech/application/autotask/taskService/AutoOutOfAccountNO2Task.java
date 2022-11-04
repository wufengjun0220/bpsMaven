

package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 日终同步核心的借据信息
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-29
 */
public class AutoOutOfAccountNO2Task  extends AbstractAutoTask { 
	private static final Logger logger = Logger.getLogger(AutoOutOfAccountNO2Task.class);
	PoolCreditProductService productService = PoolCommonServiceFactory.getPoolCreditProductService();
	PoolCoreService poolCoreService = PoolCommonServiceFactory.getPoolCoreService();
	PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	PedAssetPoolService pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();
	CreditRegisterService creditRegisterService = PoolCommonServiceFactory.getCreditRegisterService();
	FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	PedOnlineAcptService pedOnlineAcptService = PoolCommonServiceFactory.getPedOnlineAcptService();
	PedOnlineCrdtService pedOnlineCrdtService=PoolCommonServiceFactory.getPedOnlineCrdtService();
	public BooleanAutoTaskResult run() throws Exception {
		

		logger.info("日终同步核心出账信息--获取所有未结清的主业务合同开始......");
		
		
		//(1)获取所有仍需要处理的借据信息
		CreditQueryBean queryBean = new CreditQueryBean();
		queryBean.setDetailStatus(PoolComm.LOAN_1);
		List<PedCreditDetail> list = productService.queryCreditDetailList(queryBean);
		
		
		//(2)循环处理每张借据信息
		if(list!=null && list.size()>0){
			
			logger.info("查询出所有PedCreditDetail借据的信息，合计："+list.size() +" 笔");
			
			for (int i = 0; i < list.size(); i++) {
				PedCreditDetail detail = (PedCreditDetail) list.get(i);
				
				boolean loanIsChange = false;//借据是否发生变化，用来判断是否需要更新用信业务登记
				
				
				PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, detail.getBpsNo(), null, null, null);
				AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
				String apId = ap.getApId();
				
				
				try {
					logger.info("获取PedCreditDetail借据的借据号："+detail.getLoanNo());		
			
					String loanType =  detail.getLoanType();//信贷业务类型  （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
					
					if(PoolComm.XD_02.equals(loanType)||PoolComm.XD_05.equals(loanType)){//表内业务:流贷、表外业务垫款
					
						logger.info("日终额度处理表内业务处理开始......");		
						
						CoreTransNotes notes = new CoreTransNotes();
						notes.setAccNo(detail.getTransAccount());//贷款账号
						try {
							ReturnMessageNew response = poolCoreService.PJH126012Handler(notes);
							if (response.isTxSuccess()) {
								Map map = response.getBody();
								
								if(PoolComm.XD_02.equals(loanType)){//在线流贷会出现展期（即到期日延后的情况）
									if(null != map.get("EXPIRY_DATE")){//销户情况下会没有这个字段										
										String dueDateStr = (String)map.get("EXPIRY_DATE");									
										Date dueDate = DateUtils.parse(dueDateStr, DateUtils.ORA_DATE_FORMAT);
										if(!dueDate.equals(detail.getEndTime())){
											loanIsChange = true;
											detail.setEndTime(dueDate);
										}
										
									}
								}else if(PoolComm.XD_05.equals(loanType)){//表外业务垫款，到期日每次都置为当天
									Date date = DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT);
									detail.setEndTime(date);
									loanIsChange = true;
								} 
								BigDecimal loanLeftAmt = detail.getActualAmount();
								if(DateUtils.checkOverLimited(new Date(), detail.getEndTime())){//逾期
									loanLeftAmt =  new BigDecimal((String)map.get("LOAN_LEFT_AMT"));//结清贷款需还款总额
								}else{
									loanLeftAmt =  new BigDecimal((String)map.get("RESTITUTE_PRINCIPAL_AMT"));//应还本金
								}
								BigDecimal restReleaseAmt = detail.getActualAmount().subtract(loanLeftAmt);// 需要释放的金额
								if(loanLeftAmt.compareTo(detail.getActualAmount())!=0){//有变化
									detail.setActualAmount(loanLeftAmt);//记录结清贷款需还款总额
									loanIsChange = true;
								} 
								
								if(loanIsChange){
									
									//更新后的借据落库处理
									productService.txStore(detail);
									
									//用信业务登记
									CreditRegister crdtReg = creditRegisterService.createCreditRegister(detail, dto,apId);
									creditRegisterService.txSaveCreditRegister(crdtReg);

								}
								
							}
							
							logger.info("日终额度处理表内业务处理结束......");	
							
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							logger.error("日终核心同步借据信息调用核心接口出错！出错借据号："+detail.getLoanNo(),e);
						}
					}else{//表外业务处理
						
						logger.info("日终额度处理表外业务处理开始......");	
						
						CoreTransNotes transNotes = new CoreTransNotes();
						transNotes.setAccNo(detail.getTransAccount());
						if(PoolComm.XD_04.equals(detail.getLoanType())){
							transNotes.setCurrentFlag("1");//信用证为活期
						}else{
							transNotes.setCurrentFlag("2");
						}
						ReturnMessageNew response = null;
						try {
							response = poolCoreService.PJH716040Handler(transNotes,"0");
							if (response.isTxSuccess()) {
								Map map = response.getBody();
								BigDecimal loanAmt = detail.getLoanAmount();//借据金额
								
								
								BigDecimal val = new BigDecimal("0");//核心业务保证金账户余额
								if(map.get("BALANCE")!=null){
									val = BigDecimalUtils.valueOf((String) map.get("BALANCE"));
								}
								
								BigDecimal oldActualAmount = detail.getActualAmount();//原实际金额
								BigDecimal newActualAmount = loanAmt.subtract(val);//更新后实际金额
								
								if(oldActualAmount.compareTo(newActualAmount)!=0){									
									detail.setActualAmount(newActualAmount);
									loanIsChange = true;
								}
								
								if(loanIsChange){//只有发生变换，才重新登记
									
									//更新后的借据落库处理
									productService.txStore(detail);
									
									//用信业务登记
									CreditRegister crdtReg = creditRegisterService.createCreditRegister(detail, dto,apId);
									creditRegisterService.txSaveCreditRegister(crdtReg);
								}
								
							}
							logger.info("日终额度处理表外业务处理结束......");	
							
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							logger.error("日终核心同步借据信息调用核心接口出错！出错借据号："+detail.getLoanNo(),e);
						}
						
					}
				} catch (Exception e) {
					logger.info("处理PedCreditDetail的借据【"+detail.getLoanNo()+"】异常");
					logger.error(e.getMessage(),e);
				}
				
				
			}
			
		}
		
		
		/*
		 * 查询所有【未结清】的【线上】主业务合同，若该合同下的所有借据均已结清，则该合同置为已结清
		 * 
		 * 注意：因为线上业务已生效的合同不存在用合同占用额度的情况，所以这里没有对应的资产登记的信息需要处理
		 * 
		 */
		logger.info("日终，检索所有【未结清】的【线上】主业务合同，若合同下没有有效的借据，则将该合同置为结清......");
		
		CreditQueryBean crdtQueryBean = new CreditQueryBean();
		crdtQueryBean.setSttlFlag(PoolComm.JQZT_WJQ);//未结清
		crdtQueryBean.setIsOnline(PoolComm.YES);//线上
		List<CreditProduct> prdtList = productService.queryCedtProductList(crdtQueryBean);
		
		if(null != prdtList){
			
			List<CreditProduct> clearedPrdtList = new ArrayList<CreditProduct>();//已结清的合同
			
			for(CreditProduct prdt : prdtList){
				CreditQueryBean dtlQueryBean = new CreditQueryBean();
				dtlQueryBean.setCrdtNo(prdt.getCrdtNo());
				List<String>  loanStatusNotInLsit = new ArrayList<String>();
				loanStatusNotInLsit.add(PoolComm.JJ_04);//已结清
				loanStatusNotInLsit.add(PoolComm.JJ_05);//未用退回
				dtlQueryBean.setLoanStatusNotInLsit(loanStatusNotInLsit);
				
				//查询该主业务合同下所有有效的借据
				List<PedCreditDetail> unclearedLoans = productService.queryCreditDetailList(dtlQueryBean);
				if(null == unclearedLoans){
					prdt.setSttlFlag(PoolComm.JQZT_YJQ);//已结清
					clearedPrdtList.add(prdt);//已结清的合同
				}
			}
			
			//将所有已结清的线上合同置为结清
			if(clearedPrdtList.size()>0){
				productService.txStoreAll(clearedPrdtList);
			}
		}
		
		
		/*
		 * 已解约融资人生效标识日终同步
		 */
		
		productService.txEndFinancier();
		
		
		
		/*
		 * 所有签约融资票据池的客户额度信息更新
		 */
		
		logger.info("日终同步核心出账信息--更新所有签约融资票据池的客户的额度信息开始......");
		
		List<PedProtocolDto> proInfos = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null, null, null);
		if(proInfos!=null && proInfos.size()>0){
			financialService.txReCreditCalculationTask(proInfos);
		}
		
		logger.info("日终同步核心出账信息--更新所有签约融资票据池的客户的额度信息结束......");
		
		
		return new BooleanAutoTaskResult(true);
	}
	

	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}


}
