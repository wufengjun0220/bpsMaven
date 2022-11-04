package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.domain.AssetQueryBean;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.MisCredit;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 日终获取mis融资出账信息
 * @Description 
 * 	        （0）更新所有客户的额度信息
 * 		(1)获取日终数据平台从mis系统取回的主合同下的借据信息MisList
 * 	        （2）若为新产生的借据，则保存到本系统的PedCreditDetail中；若是已存借据则只处理到期的额度释放行为
 * 	        （3）若是已同步过的借据信息，表内业务若已结清则释放额度，表外业务根据状态释放额度，银承到期也释放额度
 *     （4）更新所有客户的额度信息
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-25
 */
public class AutoOutOfAccountNO1Task  extends AbstractAutoTask { 
	private static final Logger logger = Logger.getLogger(AutoOutOfAccountNO1Task.class);
	PoolCreditProductService productService = PoolCommonServiceFactory.getPoolCreditProductService();
	FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	CreditRegisterService creditRegisterService = PoolCommonServiceFactory.getCreditRegisterService();
	PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	PedAssetPoolService pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();	
	AssetRegisterService assetRegisterService = PoolCommonServiceFactory.getAssetRegisterService();
	PedOnlineAcptService pedOnlineAcptService =PoolCommonServiceFactory.getPedOnlineAcptService();
	public BooleanAutoTaskResult run() throws Exception {
		
		logger.info("MIS日终出账处理开始......");
		
		Map<String,PedProtocolDto> proMap = new HashMap<String,PedProtocolDto>();////存放有未结清主业务合同的票据池协议，后续用于重新计算额度
		/*
		 * 查询所有未结清的主业务合同
		 */
		CreditQueryBean queryBean = new CreditQueryBean();
		queryBean.setSttlFlag(PoolComm.JQZT_WJQ);//未结清
		List<CreditProduct> list = productService.queryCedtProductList(queryBean);
		
		Map<String,CreditProduct> termUnOnlineMap = new HashMap<String, CreditProduct>();//期限配比模式票据池，未结清的线下主业务合同
		List<String> termUnOnlineCrdtNolist = new ArrayList<String>();//期限配比模式票据池，未结清的线下主业务合同合同号
		
		
		if(list!=null && list.size()>0){
		
			logger.info("查询所有的主业务合同信息，查询到 "+list.size()+" 笔主业务合同");
			for (int i = 0; i < list.size(); i++) {
				
				
				CreditProduct product = (CreditProduct) list.get(i);
				String crdtNo = product.getCrdtNo();
				String bpsNo = product.getBpsNo();
			
				PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
				AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
				String apId = ap.getApId();
				proMap.put(bpsNo, dto);
				
				
				//期限模式，线下业务
				if(PoolComm.POOL_MODEL_02.equals(dto.getPoolMode())&&PoolComm.NO.equals(product.getIsOnline())){
					termUnOnlineMap.put(product.getCrdtNo(),product);
					termUnOnlineCrdtNolist.add(product.getCrdtNo());
				}
				
				
				
				/*
				 * (1)获取日终数据平台从mis系统取回的主合同下的借据信息MisList
				 */
							
				logger.info("处理的主业务合同号："+crdtNo);
				List MisList = productService.queryDetails(null, crdtNo);
				List<PedCreditDetail> regList = new ArrayList<PedCreditDetail>();
				if(MisList!=null&&MisList.size()>0){
				
					logger.info("主业务合同【"+crdtNo+"】获取到mis的出账信息条数为："+MisList.size());
					
					for (int j = 0; j < MisList.size(); j++) {
							try {
								MisCredit mis = new MisCredit();;
								Object[] obj = (Object[]) MisList.get(j);
								if (obj[0] != null) {
									if(obj[0]!=null){
										mis.setCrdtNo(obj[0].toString());
									}
									if(obj[1]!=null){
										mis.setCustNumber(obj[1].toString());
									}
									if(obj[2].toString()!=null){
										mis.setCustName(obj[2].toString());
									}
									if(obj[3]!=null){
										mis.setLoanNo(obj[3].toString());
									}
									if(obj[4]!=null){
										mis.setTransTime((Date) obj[4]);
									}
									if(obj[5]!=null){
										mis.setLoanType(obj[5].toString());
									}
									if(obj[6].toString()!=null){
										mis.setLoanStatus(obj[6].toString());
									}
									if(obj[7]!=null){
										mis.setTransAccount(obj[7].toString());
									}
									if(obj[8]!=null){
										mis.setLoanAmount(new BigDecimal(obj[8].toString()));
									}
									if(obj[9]!=null){
										mis.setRepaymentTime((Date) obj[9]);
									}
								}
								
								logger.info("查询PedCreditDetail中是否存在该笔借据，开始......");
								
								PedCreditDetail detail = productService.queryCreditDetailByTransAccountOrLoanNo(null,mis.getLoanNo());
								
								logger.info("查询PedCreditDetail中是否存在该笔借据，结束......");
								
								if(detail==null){//一定来自于线下业务
									
									/*
									 * （2）新产生的借据，保存到本系统的PedCreditDetail中
									 */
									
									logger.info("将misCredit中的数据同步到pedCreditDetail中，借据号为：："+mis.getLoanNo());
									detail = new PedCreditDetail();
									detail.setCrdtNo(mis.getCrdtNo());//信贷主业务合同号
									detail.setRiskLevel(product.getRisklevel());//风险等级
									detail.setCustNumber(mis.getCustNumber());//客户号
									detail.setCustName(mis.getCustName());//客户名称
									detail.setLoanNo(mis.getLoanNo());//借据号
									detail.setTransTime(mis.getTransTime());//交易时间
									detail.setStartTime(mis.getTransTime());//开始时间=交易时间
									detail.setLoanType(mis.getLoanType());//交易类型   （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
									detail.setLoanStatus(mis.getLoanStatus());//交易状态（JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清）
									detail.setTransAccount(mis.getTransAccount());//交易账号 （表内业务为贷款账号，表外业务为业务保证金账号）
									detail.setLoanAmount(mis.getLoanAmount());//借据总金额
									detail.setLoanBalance(mis.getLoanAmount());//借据余额    （第一次从mis获取时候直接赋值为 借据总金额）
									
									if(PoolComm.XD_05.equals(mis.getLoanType())){//垫款类借据到期日为当天
										Date date = DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT);
										detail.setEndTime(date);//借据到期日
									}else{
										detail.setEndTime(mis.getRepaymentTime());//借据到期日										
									}
									detail.setActualAmount(mis.getLoanAmount());//剩余还款总额 （第一次取mis的借据金额）
									detail.setDetailStatus(PoolComm.LOAN_1);//还需处理
									detail.setBpsNo(bpsNo);//票据池编号
									detail.setIsOnline(product.getIsOnline());//线上线下标记
									detail.setIfAdvanceAmt(PoolComm.NO);//是否垫款-否
									/*
									 * 用信业务登记
									 */
									regList.add(detail);
									
								
								}else{
									
									/*
									 * （3）若是已同步过的借据信息，表内业务若已结清则释放额度，表外业务根据状态释放额度，银承到期也释放额度
									 */
									
									if(!detail.getDetailStatus().equals(PoolComm.LOAN_0)){
										if(mis.getLoanType().equals(PoolComm.XD_02)||mis.getLoanType().equals(PoolComm.XD_05)){//表内业务（逾期将产生罚息）,流贷/表外业务罚息
											
											logger.info("MIS日终表内业务处理开始......");
											
											if(mis.getLoanStatus().equals(PoolComm.JJ_04)){//结清 释放所有未释放的额度
												detail.setDetailStatus(PoolComm.LOAN_0);//不再处理的借据
												detail.setActualAmount(new BigDecimal("0"));
												detail.setLoanStatus(mis.getLoanStatus());
												
												/*
												 * 用信业务登记表删除数据
												 */
												List<String> busiIds = new ArrayList<String>();
												busiIds.add(detail.getCreditDetailId());
												creditRegisterService.txDelCreditRegisterByBusiIds(busiIds);
											}
											
											logger.info("MIS日终表内业务处理结束......");
											
										}else{//表外业务
											
											logger.info("MIS日终表外业务处理开始......");
											
											if(mis.getLoanType().equals(PoolComm.XD_01)){//银承处理，结清释放，到期也释放
												
												long dueDate = mis.getRepaymentTime().getTime();
												long curDate = (new Date()).getTime();
												
												if(mis.getLoanStatus().equals(PoolComm.JJ_04)||mis.getLoanStatus().equals(PoolComm.JJ_03)||dueDate<=curDate){//JJ_03  垫款    JJ_04结清   或者到期   释放所有未释放的额度
													
													detail.setActualAmount(new BigDecimal("0"));
													detail.setDetailStatus(PoolComm.LOAN_0);//不再处理的借据
													detail.setLoanStatus(mis.getLoanStatus());
													
													if(dueDate<=curDate){//银承的到期
														detail.setLoanStatus(PoolComm.JJ_04);	
													}
													
													/*
													 * 用信业务登记表删除数据
													 */
													List<String> busiIds = new ArrayList<String>();
													busiIds.add(detail.getCreditDetailId());
													creditRegisterService.txDelCreditRegisterByBusiIds(busiIds);
												}
												
												/*
												 * 信贷未用退回（隔日），【线下业务】不释放额度，【线上业务】释放额度，将该出账信息置为JJ_05未用退回状态，该借据不再处理     --Ju Nana 20200629
												 */
												if(mis.getLoanStatus().equals(PoolComm.JJ_05)){//未用退回
													detail.setActualAmount(new BigDecimal("0"));
													detail.setDetailStatus(PoolComm.LOAN_0);//不再处理的借据
													detail.setLoanStatus(mis.getLoanStatus());
													
													if(DateUtils.formatDate(DateUtils.getNextDay(detail.getStartTime()),DateUtils.ORA_DATES_FORMAT).compareTo(DateUtils.getCurrDate()) == 0){
														//mis 同步过来的借据是未用退回并且借据日期是当天(日终任务时五点，作比较时去借据开始日的下一天与 当天时间比较) 未用退回需要释放信贷的额度
														OnlineQueryBean bean = new OnlineQueryBean();
														bean.setContractNo(detail.getCrdtNo());
														PlOnlineAcptBatch batch = pedOnlineAcptService.queryPlOnlineAcptBatch(bean);
														ReturnMessageNew resp = pedOnlineAcptService.misRepayAcptPJE028(batch);
														if(resp.isTxSuccess()){
															//当日未用退回的银承票据   信贷额度释放成功
															batch = pedOnlineAcptService.calculateBatchAmt(batch);
															pedOnlineAcptService.txStore(batch);
														}else{
															logger.info("日终获取mis融资出账信息时，银承当日未用退回数据，去信贷释放额度失败！");
														}
													}
													
													
													/*
													 * 【线上业务】用信业务登记表删除数据
													 */
													if("YCOL".endsWith(detail.getLoanNo().substring(0, 4))){	
														
														/**
														 * 查询银承明细状态修改为作废
														 */
														PlOnlineAcptDetail acpt = pedOnlineAcptService.queryPlOnlineAcptDetailByBillNo(detail.getLoanNo());
														acpt.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
														pedOnlineAcptService.txStore(acpt);
														
														List<String> busiIds = new ArrayList<String>();
														busiIds.add(detail.getCreditDetailId());
														creditRegisterService.txDelCreditRegisterByBusiIds(busiIds);
														
													}
												}
												
											}else{//其他表外业务：结清释放
												if(mis.getLoanStatus().equals(PoolComm.JJ_04)||mis.getLoanStatus().equals(PoolComm.JJ_03)){//JJ_03  垫款    JJ_04结清   释放所有未释放的额度
													
													detail.setActualAmount(new BigDecimal("0"));
													detail.setDetailStatus(PoolComm.LOAN_0);//不再处理的借据
													detail.setLoanStatus(mis.getLoanStatus());
													
													/*
													 * 用信业务登记表删除数据
													 */
													List<String> busiIds = new ArrayList<String>();
													busiIds.add(detail.getCreditDetailId());
													creditRegisterService.txDelCreditRegisterByBusiIds(busiIds);
												}								
												
											}
											
											logger.info("MIS日终表外业务处理结束......");
										}
									}
									
								}
								/*
								 * 借据表落库处理
								 */
								productService.txStore(detail);
								
								/*
								 * 将MIS_CREDIT表中的状态置为“已处理”
								 */
								productService.txUpdateMisCredit(mis);
								
							} catch (Exception e) {
								logger.info("主业务合同【"+crdtNo+"】下的借据处理异常！");
								logger.error(e.getMessage(),e);
							}
						}
					
					
				}
				/*
				 * 用信业务登记
				 */
				if(regList.size()>0){
					for(PedCreditDetail del : regList){			
						if(PoolComm.NO.equals(del.getIsOnline()) && PoolComm.JJ_05.equals(del.getLoanStatus()) ){
							continue;//线下未用退回不登记融资业务
						}
						CreditRegister crdtReg = creditRegisterService.createCreditRegister(del, dto,apId);
						creditRegisterService.txSaveCreditRegister(crdtReg);
					}
				}
				
				/*
				 * 处理用信业务登记表中【线下】主业务合同（因为线上业务不会登记合同）
				 */
				if(PoolComm.NO.equals(product.getIsOnline())){//非线上业务主业务合同处理					
					creditRegisterService.txReleaseCreditByProduct(product, PoolComm.EDSF_02, BigDecimal.ZERO);
				}
				
			}
			
			
			if(termUnOnlineCrdtNolist.size()>0){
				this.txSpecialDueDate(termUnOnlineCrdtNolist, termUnOnlineMap);
			}
			
		}
		
		/*
		 * 将到期日为今天以及今天之前的资产登记表中的资产置为不计算
		 */
		logger.info("MIS日终出账任务，将到期日为今天以及今天之前的资产登记表中的资产置为不计算,处理开始...");
		this.txChangeAssetRegisterToDel();
		
		/*
		 * 重新计算票据池额度
		 */
		
		logger.info("MIS日终出账任务，重新计算票据池额度开始...");
		
		if(!proMap.isEmpty()){
			List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>(proMap.values());
			financialService.txReCreditCalculationTask(proList);
		}
		
		logger.info("MIS日终出账任务，重新计算票据池额度结束！");
		
		
		return new BooleanAutoTaskResult(true);
	}
	
	
	/**
	 * 需求【3.4.3.3.2】特殊数据到期日规则,线下业务期限配比模式，未用部分到期日的计算规则：
			（1）“借据最早到期日”晚于等于合同到期日
				1)系统时间在合同到期日（含）前，未用部分到期日为“借据最早到期日”。---实际是不用处理的
				2)系统时间在合同到期日（不含）后，未用部分释放，不再纳入期限配比计算，可视为未用部分到期日为合同到期日。
			（2）“借据最早到期日”早于合同到期日
				1)系统时间在“借据最早到期日”（含），未用部分到期日为“借据最早到期日”。---实际是不用处理的
				2)系统时间在“借据最早到期日”（不含）后但在合同到期日（含）前，未用部未分释放，仍需纳入期限配比计算，未用部分的到期日应为系统次日即T+1。（新增内容）
				3)系统时间在合同到期日（不含）后，未用部分释放，不再纳入期限配比计算，可视为未用部分到期日为合同到期日。
	 * @param termUnOnlineCrdtNolist
	 * @param termUnOnlineMap
	 * @author Ju Nana
	 * @date 2021-9-16下午3:07:59
	 */
	
	private void txSpecialDueDate(List<String> termUnOnlineCrdtNolist,Map<String,CreditProduct> termUnOnlineMap){

		List<CreditRegister> delArList = new ArrayList<CreditRegister>();//需要删除的融资业务登记表的信息
		List<CreditRegister> modArList = new ArrayList<CreditRegister>();//需要修改的融资业务登记表的信息
		
		logger.info("日终任务，特殊数据到期日规则,线下业务期限配比模式，未用部分到期日的计算处理...");
		
		try {
			List<CreditRegister> crList = creditRegisterService.queryCreditRegisterBycontractNos(termUnOnlineCrdtNolist, PoolComm.VT_0);
			if(null!=crList){
				for(CreditRegister cr : crList){
					
					try {
						CreditProduct cp = termUnOnlineMap.get(cr.getContractNo());
						Date dueDate = cp.getCrdtDueDt();//合同到期日
						Date minDate = cp.getMinDueDate();//合同最早到期日
						Date today = DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT);//今天
						
						if(DateTimeUtil.compartdate(dueDate, minDate)){//“借据最早到期日”晚于等于合同到期日
							
							if(DateTimeUtil.compartdate(today,dueDate)){//1)系统时间在合同到期日（含）前，未用部分到期日为“借据最早到期日”。
								
								//不做处理
								
							}else{//2)系统时间在合同到期日（不含）后，未用部分释放，不再纳入期限配比计算，可视为未用部分到期日为合同到期日。
								
								delArList.add(cr);
								
							}
							
						}else{//“借据最早到期日”早于合同到期日
							
							if(DateTimeUtil.compartdate(today,minDate)){//1)系统时间在“借据最早到期日”（含），未用部分到期日为“借据最早到期日”。---实际是不用处理的
								
								//不做处理
							
							}else{//2)系统时间在“借据最早到期日”（不含）后但在合同到期日（含）前，未用部未分释放，仍需纳入期限配比计算，未用部分的到期日应为系统次日即T+1。（新增内容）
								
								cr.setDueDt(DateUtils.getNextDay(today));
								modArList.add(cr);
								
							}
							
							if(DateTimeUtil.compartdate(today,dueDate)){//系统时间在合同到期日（含）前
								
								//不做处理
								
							}else{//3)系统时间在合同到期日（不含）后，未用部分释放，不再纳入期限配比计算，可视为未用部分到期日为合同到期日。
								
								delArList.add(cr);
							}
							
							
						}
						
					} catch (Exception e) {
						logger.error("日终处理特殊数据到期日规则,线下业务期限配比模式，未用部分到期日的计算合同【"+cr.getContractNo()+"】异常：",e);
						continue;
					}
					
				}
				
				
				//资产上登记表删除
				if(delArList.size()>0){		
					
					//删除资产登记信息，并保存历史
					creditRegisterService.txDeleteAll(delArList);
					for(CreditRegister creditReg : delArList){
						creditReg.setDueDt(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));//作为删除的标记字段
						creditRegisterService.txCreateRegisterHis(creditReg);
					}
				}
				
				//资产登记表修改
				if(modArList.size()>0){
					for(CreditRegister creditReg : modArList){
						//更新资产登记信息,并保存历史
						creditRegisterService.txUpdateCreditRegister(creditReg);

					}
				}
				
			}
			
		} catch (Exception e) {
			logger.error("日终处理特殊数据到期日规则,线下业务期限配比模式，未用部分到期日的计算异常：",e);
		}
		
		
	}
	
	/**
	 * 将资产登记表中已到期（含到期日当天）的高低风险票据标记为已删除（即不计算额度）
	 * 
	 * @author Ju Nana
	 * @date 2021-10-14下午1:20:21
	 */
	private void txChangeAssetRegisterToDel(){
		try {			
			AssetQueryBean bean = new AssetQueryBean();
			String date = DateUtils.toString(new Date(), DateUtils.ORA_DATE_FORMAT);
			Date today = DateUtils.StringToDate(date,DateUtils.ORA_DATE_FORMAT);
			bean.setEndDate(today);//截至到今天（含）
			
			List<String> assetTypeList = new ArrayList<String>();
			assetTypeList.add(PoolComm.ED_PJC);//低风险票据
			assetTypeList.add(PoolComm.ED_PJC_01);//高风险票据
			bean.setAssetTypeList(assetTypeList);
			List<AssetRegister> arList = assetRegisterService.queryAssetRegisterList(bean);
			if(null != arList){
				List<AssetRegister> arStoreList = new ArrayList<AssetRegister>();
				for(AssetRegister ar :arList){
					ar.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_YES);
					ar.setUpdateDate(new Date());
					arStoreList.add(ar);
					
					//保存到历史表
					assetRegisterService.txSaveAssetRegisterHis(ar, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
				}
				assetRegisterService.txStoreAll(arStoreList);
			}
		} catch (Exception e) {
			logger.error("日终任务处理将资产登记表中已到期（含到期日当天）的高低风险票据标记为已删除时异常："+e.getMessage(),e);
		}
	}

	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}


}
