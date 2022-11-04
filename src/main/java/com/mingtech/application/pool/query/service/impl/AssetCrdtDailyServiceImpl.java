package com.mingtech.application.pool.query.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedBailTrans;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.domain.PedAssetCrdtDaily;
import com.mingtech.application.pool.query.domain.PedAssetDaily;
import com.mingtech.application.pool.query.domain.PedCrdtDaily;
import com.mingtech.application.pool.query.service.AssetCrdtDailyService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 每日资产&融资业务处理实现类
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 * @copyright 北明明润（北京）科技有限责任公司
 */
@Service("assetCrdtDailyService")
public class AssetCrdtDailyServiceImpl extends GenericServiceImpl implements AssetCrdtDailyService {
	private static final Logger logger = Logger.getLogger(CommonQueryServiceImpl.class);

	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public Class getEntityClass() {
		return null;
	}
	@Override
	public void txCrdtDailyTask() throws Exception {
		
		logger.info("【1】每日融资业务明细生成...");
		
		Date date=DateUtils.modDay(new Date(),-1);//当前系统工作日减1
		CommonQueryBean queryBean=new CommonQueryBean();
		queryBean.setStatus(PoolComm.LOAN_1);//还需要处理的借据
		queryBean.setCreateDate(date);
		List<CommonQueryBean> list=this.loadPedCreditDetailList(queryBean);
		if(null !=list && list.size()>0){
			List<PedCrdtDaily> storeList = new ArrayList<PedCrdtDaily>();
			for(int i=0;i<list.size();i++){
				PedCrdtDaily pedCrdtDaily=new PedCrdtDaily();
				CommonQueryBean query=(CommonQueryBean)list.get(i);
				pedCrdtDaily.setBpsNo(query.getBpsNo());//票据池编号  
				pedCrdtDaily.setBpsName(query.getBpsName());//票据池名称
				pedCrdtDaily.setCustNo(query.getCustNo());//客户号    
				pedCrdtDaily.setCustName(query.getCustName()); //客户名称   
				pedCrdtDaily.setCreateDate(query.getCreateDate());//创建日期  
				pedCrdtDaily.setCrdtType(query.getCrdtType());//融资业务类型    XD_01:银承   XD_02:流贷   XD_03:保函   XD_04:国内信用证
				pedCrdtDaily.setCrdtNo(query.getContractNo()); //合同号    
				pedCrdtDaily.setLoanNo(query.getLoanNo());//借据号   
				pedCrdtDaily.setLoanAmt(query.getLoanAmt());//业务金额  
				pedCrdtDaily.setLoanBalance(query.getLoanBalance());//业务余额 
				pedCrdtDaily.setCcupy(query.getCcupy());//占用额度比例 
				pedCrdtDaily.setRiskLevel(query.getRisklevel()); //占用额度类型/风险类型   FX_01：低风险 FX_02：高风险
				pedCrdtDaily.setStartDate(query.getStartDate());//融资业务起始日
				pedCrdtDaily.setEndDate(query.getEndDate()); //融资业务到期日
				pedCrdtDaily.setCreateTime(new Date());//创建时间   
				pedCrdtDaily.setIsOnline(query.getIsOnline());//是否线上 1 是 0 否
				storeList.add(pedCrdtDaily);
			}
			this.txStoreAll(storeList);
		}
		
	}
	
	@Override
	public void txBillAssetDailyTask() throws Exception {
		
		logger.info("【2】每日票据资产明细生成...");
		
		Date date=DateUtils.modDay(new Date(),-1);//当前系统工作日减1
		CommonQueryBean queryBean=new CommonQueryBean();
		queryBean.setStatus(PoolComm.DS_02);//已入池
		queryBean.setCreateDate(date);
		List<CommonQueryBean> list=this.loadDraftPoolDetailList(queryBean);
		
		if(null !=list && list.size()>0){
			List<PedAssetDaily> storeList = new ArrayList<PedAssetDaily>();
			for(int i=0;i<list.size();i++){
				PedAssetDaily pedAssetDaily=new PedAssetDaily();
				CommonQueryBean query=(CommonQueryBean)list.get(i);
				pedAssetDaily.setBpsNo(query.getBpsNo());//票据池编号  
				pedAssetDaily.setBpsName(query.getBpsName());//票据池名称
				pedAssetDaily.setCustNo(query.getCustNo());//客户号    
				pedAssetDaily.setCustName(query.getCustName()); //客户名称   
				pedAssetDaily.setCreateDate(query.getCreateDate());//创建日期  
				pedAssetDaily.setAssetType(query.getAssetType());             //资产类型     01-票据    02-活期保证金      
				pedAssetDaily.setAmt(query.getAmt());                             //金额                          
				pedAssetDaily.setBillNo(query.getBillNo());                   //票号                              
				pedAssetDaily.setBillMedia(query.getBillMedia());             //票据介质     1-纸票	2-电子            
				pedAssetDaily.setBillType(query.getBillType());               //票据类型     AC01-银票  AC02-商票       
				pedAssetDaily.setIssueDt(query.getIssueDt());                 //出票日                             
				pedAssetDaily.setDueDt(query.getDueDt());                     //到期日                             
				pedAssetDaily.setBanEndrsmtFlag(query.getBanEndrsmtFlag());   //不得转让标记   0-可转让 1-不可转让           
				pedAssetDaily.setDrwrName(query.getDrwrName());               //出票人名称                           
				pedAssetDaily.setDrwrBankNo(query.getDrwrBankNo());           //出票人开户行行号                        
				pedAssetDaily.setAcptName(query.getAcceptName());             //承兑人/承兑行名称                       
				pedAssetDaily.setAcptBankName(query.getAcptBankName());       //承兑人开户行名称                        
				pedAssetDaily.setAcptBankNo(query.getAcptBankNo());           //承兑人开户行行号                        
				pedAssetDaily.setPyeeName(query.getDeduAcctName());           //收款人名称                           
				pedAssetDaily.setPyeeBankName(query.getDeduBankName());       //收款人开户行行名                        
				pedAssetDaily.setPyeeBankNo(query.getDeduBankCode());         //收款人开户行行号          
				pedAssetDaily.setSIssuerAccount(query.getSIssuerAccount());   // 出票人账号
				pedAssetDaily.setSPayeeAccount(query.getSPayeeAccount());     // 收款人账号
				
				pedAssetDaily.setBeginRangeNo(query.getBeginRangeNo());//票据开始子区间号
				pedAssetDaily.setEndRangeNo(query.getEndRangeNo());//票据结束子区间号
				pedAssetDaily.setSIssuerAcctName(query.getSIssuerAcctName());//出票人账号名称
				pedAssetDaily.setPlAccptrAcctName(query.getPlAccptrAcctName());//承兑人账号名称
				pedAssetDaily.setPlPyeeAcctName(query.getPlPyeeAcctName());//收款人账号名称
				pedAssetDaily.setPlAccptrAcctNo(query.getPlAccptrAcctNo());//承兑人账号
				pedAssetDaily.setPlAccptr(query.getPlAccptr());//承兑人名称
				pedAssetDaily.setDraftSource(query.getDraftSource());//票据来源
				pedAssetDaily.setSplitFlag(query.getSplitFlag());//是否允许拆分标记 1是 0否
				
				pedAssetDaily.setCreateTime(new Date());          //创建时间                            
				storeList.add(pedAssetDaily);
				
			}
			
			this.txStoreAll(storeList);
		}
		
		
	}
	
	@Override
	public void txBailAssetDailyTask() throws Exception {

		logger.info("【3】每日保证金资产资产明细生成...");
		
		Date date=DateUtils.modDay(new Date(),-1);//当前系统工作日减1
		CommonQueryBean queryBean=new CommonQueryBean();
		queryBean.setStatus(PoolComm.OPEN_01);//已开通
		queryBean.setCreateDate(date);
		List<CommonQueryBean> list=this.loadBailDetaillList(queryBean);
		
		if(null !=list && list.size()>0){
			List<PedAssetDaily> storeList = new ArrayList<PedAssetDaily>();
			for(int i=0;i<list.size();i++){
				PedAssetDaily pedAssetDaily=new PedAssetDaily();
				CommonQueryBean query=(CommonQueryBean)list.get(i);
				pedAssetDaily.setBpsNo(query.getBpsNo());//票据池编号  
				pedAssetDaily.setBpsName(query.getBpsName());//票据池名称
				pedAssetDaily.setCustNo(query.getCustNo());//客户号    
				pedAssetDaily.setCustName(query.getCustName()); //客户名称   
				pedAssetDaily.setCreateDate(query.getCreateDate());//创建日期  
				pedAssetDaily.setAssetType(query.getAssetType());             //资产类型     01-票据    02-活期保证金      
				pedAssetDaily.setAmt(query.getAmt());                             //金额                          
				pedAssetDaily.setBillNo(query.getBillNo());                   //票号                              
				pedAssetDaily.setBillMedia(query.getBillMedia());             //票据介质     1-纸票	2-电子            
				pedAssetDaily.setBillType(query.getBillType());               //票据类型     AC01-银票  AC02-商票       
				pedAssetDaily.setIssueDt(query.getIssueDt());                 //出票日                             
				pedAssetDaily.setDueDt(query.getDueDt());                     //到期日                             
				pedAssetDaily.setBanEndrsmtFlag(query.getBanEndrsmtFlag());   //不得转让标记   0-可转让 1-不可转让           
				pedAssetDaily.setDrwrName(query.getDrwrName());               //出票人名称                           
				pedAssetDaily.setDrwrBankNo(query.getDrwrBankNo());           //出票人开户行行号                        
				pedAssetDaily.setAcptName(query.getAcceptName());             //承兑人/承兑行名称                       
				pedAssetDaily.setAcptBankName(query.getAcptBankName());       //承兑人开户行名称                        
				pedAssetDaily.setAcptBankNo(query.getAcptBankNo());           //承兑人开户行行号                        
				pedAssetDaily.setPyeeName(query.getDeduAcctName());           //收款人名称                           
				pedAssetDaily.setPyeeBankName(query.getDeduBankCode());       //收款人开户行行名                        
				pedAssetDaily.setPyeeBankNo(query.getDeduBankName());         //收款人开户行行号                        
				pedAssetDaily.setCreateTime(new Date());          //创建时间                            
				storeList.add(pedAssetDaily);
				
			}
			this.txStoreAll(storeList);
		}
	}

	@Override
	public void txAssetCrdtDailyTask() throws Exception {
		
		
		logger.info("【4】每日资产/融资业务按票据池客户生成...");
		
		Date date=DateUtils.modDay(new Date(),-1);//当前系统工作日减1
		CommonQueryBean queryBean=new CommonQueryBean();
		queryBean.setCreateDate(date);
		queryBean.setAssetType("01");//01-票据
		List<CommonQueryBean> billList=this.loadPedAssetDailyList(queryBean);
		
		CommonQueryBean queryBean1=new CommonQueryBean();
		queryBean1.setCreateDate(date);
		queryBean1.setAssetType("02");//02-活期保证金
		List<CommonQueryBean> BailList=this.loadPedAssetDailyList(queryBean1);
		
		billList.addAll(BailList);
		
		logger.info("【4.1】每日资产按票据池客户生成开始...");
		if(null !=billList && billList.size()>0){
			List<PedAssetCrdtDaily> storeList = new ArrayList<PedAssetCrdtDaily>();
			for(int i=0;i<billList.size();i++){ 
				CommonQueryBean query=(CommonQueryBean)billList.get(i);
				PedAssetCrdtDaily pedAssetCrdtDaily=new PedAssetCrdtDaily();
				pedAssetCrdtDaily.setBpsNo(query.getBpsNo());    //票据池编号 
				pedAssetCrdtDaily.setBpsName(query.getBpsName());   //票据池名称
				pedAssetCrdtDaily.setCreateDate(date);  //交易时间
				pedAssetCrdtDaily.setBusiType(query.getBusiType());   //业务类型  01：资产-现金 02：资产-票据 03：融资业务-银承 04：融资业务-流贷  05：融资业务-保函  06：融资业务-国内信用证
				pedAssetCrdtDaily.setTotalAmt(query.getTotalAmt());  //总金额
				pedAssetCrdtDaily.setCreateTime(new Date());          //创建时间                            
				storeList.add(pedAssetCrdtDaily);
			}
			this.txStoreAll(storeList);
		}
		logger.info("【4.1】每日资产按票据池客户生成结束...");
		

		CommonQueryBean queryBean3=new CommonQueryBean();
		queryBean3.setCreateDate(date);
		queryBean3.setCrdtType("XD_01");////融资业务类型    XD_01:银承   XD_02:流贷   XD_03:保函   XD_04:国内信用证
		List<CommonQueryBean> totalList=this.loadPedCrdtDailyList(queryBean3);
		
		CommonQueryBean queryBean4=new CommonQueryBean();
		queryBean4.setCreateDate(date);
		queryBean4.setCrdtType("XD_02");//XD_02:流贷
		List<CommonQueryBean> branchList=this.loadPedCrdtDailyList(queryBean4);
		totalList.addAll(branchList);
		
		CommonQueryBean queryBean5=new CommonQueryBean();
		queryBean5.setCreateDate(date);
		queryBean5.setCrdtType("XD_03");//XD_03:保函
		List<CommonQueryBean> branchList5=this.loadPedCrdtDailyList(queryBean5);
		totalList.addAll(branchList5);
		
		CommonQueryBean queryBean6=new CommonQueryBean();
		queryBean6.setCreateDate(date);
		queryBean6.setCrdtType("XD_04");//XD_04:信用证
		List<CommonQueryBean> branchList6=this.loadPedCrdtDailyList(queryBean6);
		totalList.addAll(branchList6);
		
		
		logger.info("【4.2】每日融资业务按票据池客户生成开始...");
		
		if(null !=totalList && totalList.size()>0){
			List<PedAssetCrdtDaily> storeList = new ArrayList<PedAssetCrdtDaily>();
			for(int i=0;i<totalList.size();i++){
				CommonQueryBean query=(CommonQueryBean)totalList.get(i);
				PedAssetCrdtDaily pedAssetCrdtDaily=new PedAssetCrdtDaily();
				pedAssetCrdtDaily.setBpsNo(query.getBpsNo());    //票据池编号 
				pedAssetCrdtDaily.setBpsName(query.getBpsName());   //票据池名称
				pedAssetCrdtDaily.setCreateDate(date);  //交易时间
				pedAssetCrdtDaily.setBusiType(query.getBusiType());   //业务类型  01：资产-现金 02：资产-票据 03：融资业务-银承 04：融资业务-流贷  05：融资业务-保函  06：融资业务-国内信用证
				pedAssetCrdtDaily.setTotalAmt(query.getTotalAmt());  //总金额
				pedAssetCrdtDaily.setCreateTime(new Date());          //创建时间                            
				storeList.add(pedAssetCrdtDaily);
			}
			this.txStoreAll(storeList);
		}
		
		logger.info("【4.2】每日融资业务按票据池客户生成结束...");
		
		
	}

	

	@Override
	public void txAssetCrdtDailyBatchNoTask() throws Exception {

		logger.info("【5】每日票据池资产、融资业务快照生成批次号处理...");
		
		Date date=DateUtils.modDay(new Date(),-1);//当前系统工作日减1
		StringBuffer sb = new StringBuffer();
		sb.append("select pc from PedAssetCrdtDaily pc where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表		
		sb.append(" and pc.createDate >= :createDate1 and  pc.createDate <= :createDate2 ");
	    keyList.add("createDate1");
		valueList.add(DateUtils.getCurrentDayStartDate(date));
		keyList.add("createDate2");
		valueList.add(DateUtils.getCurrentDayEndDate(date));
		sb.append("order by pc.createTime asc");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List<PedAssetCrdtDaily> list = new ArrayList<PedAssetCrdtDaily>();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		if(list.size()>0){
			for(int i=0;i<list.size();i++){
				PedAssetCrdtDaily pedAssetCrdtDaily=(PedAssetCrdtDaily)list.get(i);
				if(pedAssetCrdtDaily.getBusiType().equals("01")||pedAssetCrdtDaily.getBusiType().equals("02")){ //01：资产-现金 02：资产-票据
					CommonQueryBean query=new CommonQueryBean();
					query.setCreateDate(date);
					query.setBpsNo(pedAssetCrdtDaily.getBpsNo());
					if(pedAssetCrdtDaily.getBusiType().equals("01")){
						query.setBusiType("02");
					}else{
						query.setBusiType("01");
					}
					List pedAssetDaily=this.loadPedAssetDaily(query,null);
					for(int j=0;j<pedAssetDaily.size();j++){
						PedAssetDaily detail=(PedAssetDaily)pedAssetDaily.get(j);
						detail.setBatchId(pedAssetCrdtDaily.getId());
						this.txStore(detail);
					}
				}else {// 03：融资业务-银承 04：融资业务-流贷 
					CommonQueryBean query=new CommonQueryBean();
					query.setCreateDate(date);
					query.setBpsNo(pedAssetCrdtDaily.getBpsNo());
					if(pedAssetCrdtDaily.getBusiType().equals("03")){
						query.setBusiType("XD_01");
					}
					if(pedAssetCrdtDaily.getBusiType().equals("04")){
						query.setBusiType("XD_02");
					}
					if(pedAssetCrdtDaily.getBusiType().equals("05")){
						query.setBusiType("XD_03");
					}
					if(pedAssetCrdtDaily.getBusiType().equals("06")){
						query.setBusiType("XD_04");
					}
					List PedCrdtDaily=this.loadPedCrdtDaily(query);
					
					if(null !=PedCrdtDaily && PedCrdtDaily.size()>0){
						List<PedCrdtDaily> storeList = new ArrayList<PedCrdtDaily>();
						for(int j=0;j<PedCrdtDaily.size();j++){
							PedCrdtDaily detail=(PedCrdtDaily)PedCrdtDaily.get(j);
							detail.setBatchId(pedAssetCrdtDaily.getId());
							storeList.add(detail);
						}
						this.txStoreAll(storeList);
					}
					
				}
			}
			
		}
	}
	
	@Override
	public List<CommonQueryBean> loadPedCreditDetailList(CommonQueryBean queryBean) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select pcd,cp from PedCreditDetail pcd,CreditProduct cp where pcd.crdtNo=cp.crdtNo ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {	
			if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//交易时间
				
				sb.append(" and  pcd.transTime<=TO_DATE(:transTime1, 'yyyy-mm-dd hh24:mi:ss')");
				keyList.add("transTime1");
				valueList.add(DateUtils.toString(queryBean.getCreateDate(), "yyyy-MM-dd") + " 23:59:59");
			}
			
			if (StringUtils.isNotBlank(queryBean.getStatus())) { //借据状态
				sb.append(" and pcd.detailStatus =:detailStatus ");
				keyList.add("detailStatus");
				valueList.add(queryBean.getStatus());
			}		
		}
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		List<CommonQueryBean> temp=new ArrayList<CommonQueryBean>();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			PedCreditDetail pcd= (PedCreditDetail)obj[0];
			CreditProduct cp= (CreditProduct)obj[1];
			String bpsNo = pcd.getBpsNo();
			String bpsName = "";
			query.setBpsNo(bpsNo);        //票据池编号            
			if(StringUtil.isNotBlank(bpsNo)){
				if(bpsNo.contains("DR")){
					bpsName = "票据池（"+cp.getCustName()+"）";
				}else{
					bpsName = "集团票据池（"+cp.getCustName()+"）";
				}
			}else{
				continue;
			}
			query.setBpsName(bpsName);           //票据池名称                                                         
			query.setCustNo(cp.getCustNo());               //客户号                                                           
			query.setCustName(cp.getCustName());           //客户名称                                                          
			query.setCreateDate(queryBean.getCreateDate());       //创建日期                                                          
			query.setCrdtType(cp.getCrdtType());           //融资业务类型    XD_01:银承   XD_02:流贷   XD_03:保函   XD_04:国内信用证        
			query.setContractNo(pcd.getCrdtNo());          //合同号                                                           
			query.setLoanNo(pcd.getLoanNo());              //借据号                                                           
			query.setLoanAmt(pcd.getLoanAmount());         //业务金额                                                      
			query.setLoanBalance(pcd.getLoanBalance());    //业务余额                                                      
			query.setCcupy(cp.getCcupy());                 //占用额度比例                                                        
			query.setRisklevel(cp.getRisklevel());         //占用额度类型/风险类型   FX_01：低风险 FX_02：高风险                             
			query.setStartDate(pcd.getStartTime());        //融资业务起始日                                                       
			query.setEndDate(pcd.getEndTime());            //融资业务到期日              
			query.setIsOnline(pcd.getIsOnline()) ;         //是否线上 1 是 0 否
			temp.add(query);
		}
		return temp;
	
	}
	@Override
	public List<CommonQueryBean> loadDraftPoolDetailList(CommonQueryBean queryBean) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select dp,bill from DraftPool dp,PoolBillInfo bill where dp.assetNb=bill.SBillNo ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//入池交易时间
				
				sb.append(" and  dp.plTm<=TO_DATE(:plTm1, 'yyyy-mm-dd hh24:mi:ss')");
				keyList.add("plTm1");
				valueList.add(DateUtils.toString(queryBean.getCreateDate(), "yyyy-MM-dd") + " 23:59:59");
			}
			
			if (StringUtils.isNotBlank(queryBean.getStatus())) { //状态
				sb.append(" and dp.assetStatus =:assetStatus ");
				keyList.add("assetStatus");
				valueList.add(queryBean.getStatus());
			}	
		}
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		List<CommonQueryBean> temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			DraftPool dp= (DraftPool)obj[0];
			PoolBillInfo bill=(PoolBillInfo)obj[1];
			String bpsNo = dp.getPoolAgreement();
			String bpsName = "";
			String custNo = dp.getCustNo();
			String custName = dp.getCustName();
			query.setBpsNo(bpsNo);        //票据池编号            
			if(bpsNo.contains("DR")){
				bpsName = "票据池（"+custName+"）";
			}else{
				bpsName = "集团票据池（"+custName+"）";
			}
			query.setBpsNo(bpsNo);           //票据池编号                         
			query.setBpsName(bpsName);              //票据池名称                         
			query.setCustNo(custNo);             //客户号                           
			query.setCustName(custName);             //客户名称                          
			query.setCreateDate(queryBean.getCreateDate());               //创建日期                          
			query.setAssetType("01");                         //资产类型     01-票据    02-活期保证金    
			query.setAmt(dp.getAssetAmt());                   //金额                        
			query.setBillNo(dp.getAssetNb());                 //票号                            
			query.setBillMedia(dp.getPlDraftMedia());         //票据介质     1-纸票	2-电子          
			query.setBillType(dp.getAssetType());             //票据类型     AC01-银票  AC02-商票     
			query.setIssueDt(dp.getPlIsseDt());               //出票日                           
			query.setDueDt(dp.getPlDueDt());                  //到期日                           
			query.setBanEndrsmtFlag(bill.getSBanEndrsmtFlag());  //不得转让标记   0-可转让 1-不可转让
			query.setDrwrName(dp.getPlDrwrNm());              //出票人名称                         
			query.setDrwrBankNo(dp.getPlDrwrAcctSvcr());      //出票人开户行行号                      
			query.setAcceptName(dp.getPlDrwrAcctSvcrNm());    //承兑人/承兑行名称                     
			query.setAcptBankName(dp.getPlAccptrSvcrNm());    //承兑人开户行名称                      
			query.setAcptBankNo(dp.getPlAccptrSvcr());        //承兑人开户行行号                      
			query.setDeduAcctName(dp.getPlPyeeNm());          //收款人名称                         
			query.setDeduBankCode(dp.getPlPyeeAcctSvcr());    //收款人开户行行名                      
			query.setDeduBankName(dp.getPlPyeeAcctSvcrNm());  //收款人开户行行号      
			query.setSIssuerAccount(dp.getPlDrwrAcctId());// 出票人账号
			query.setSPayeeAccount(dp.getPlPyeeAcctId());// 收款人账号
			
			query.setBeginRangeNo(dp.getBeginRangeNo());//票据开始子区间号
			query.setEndRangeNo(dp.getEndRangeNo());//票据结束子区间号
			query.setSIssuerAcctName(dp.getPlDrwrAcctName());//出票人账号名称
			query.setPlAccptrAcctName(dp.getPlAccptrAcctName());//承兑人账号名称
			query.setPlPyeeAcctName(dp.getPlPyeeAcctName());//收款人账号名称
			query.setPlAccptrAcctNo(dp.getPlAccptrId());//承兑人账号
			query.setPlAccptr(dp.getPlAccptrNm());//承兑人名称
			query.setDraftSource(dp.getDraftSource());//票据来源
			query.setSplitFlag(dp.getSplitFlag());//是否允许拆分标记 1是 0否
			
			
			
			temp.add(query);
		}
		return temp;
	
	}

	@Override
	public List<CommonQueryBean> loadBailDetaillList(CommonQueryBean queryBean) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(" select bail,dto from BailDetail bail, PedProtocolDto dto " +
				  " where  bail.poolAgreement=dto.poolAgreement and bail.assetNb=dto.marginAccount ");
		
		
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			/*if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//交易时间 
				
				sb.append(" and  bail.createDate<=TO_DATE(:createDate1, 'yyyy-mm-dd hh24:mi:ss')");
				keyList.add("createDate1");
				valueList.add(DateUtils.toString(queryBean.getCreateDate(), "yyyy-MM-dd") + " 23:59:59");
			}*/
			
			if (StringUtils.isNotBlank(queryBean.getStatus())) { //状态
				sb.append(" and dto.openFlag =:openFlag ");
				keyList.add("openFlag");
				valueList.add(queryBean.getStatus());
			}	
		}
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		List<CommonQueryBean> temp=new ArrayList<CommonQueryBean>();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			BailDetail bail= (BailDetail)obj[0];
			PedProtocolDto dto= (PedProtocolDto)obj[1];
			query.setBpsNo(dto.getPoolAgreement());           //票据池编号                         
			query.setBpsName(dto.getPoolName());              //票据池名称                         
			query.setCustNo(dto.getCustnumber());             //客户号                           
			query.setCustName(dto.getCustname());             //客户名称                          
			query.setCreateDate(queryBean.getCreateDate());               //创建日期                          
			query.setAssetType("02");                         //资产类型     01-票据    02-活期保证金    
			query.setAmt(bail.getAssetLimitFree());                   //金额                        
			temp.add(query);
		}
		return temp;
	
	}
	
	@Override
	public List<CommonQueryBean> loadPedAssetDailyList(CommonQueryBean queryBean) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select pd.bpsNo,pd.bpsName,sum(pd.amt) from PedAssetDaily pd where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		String busiType=null;
		if (queryBean != null) {
			if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//交易时间
				sb.append(" and pd.createDate >= :createDate1 and  pd.createDate <= :createDate2 ");
				keyList.add("createDate1");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getCreateDate()));
				keyList.add("createDate2");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getCreateDate()));
			}
			if (StringUtils.isNotBlank(queryBean.getAssetType())) { ////资产类型     01-票据    02-活期保证金
				sb.append(" and pd.assetType =:assetType ");
				keyList.add("assetType");
				valueList.add(queryBean.getAssetType());
				if(queryBean.getAssetType().equals("01")){
					busiType="02";
				}else if(queryBean.getAssetType().equals("02")){
					busiType="01";
				}
				
			}
		}
		sb.append("group by pd.bpsNo,pd.bpsName");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		List<CommonQueryBean> temp=new ArrayList<CommonQueryBean>();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			query.setBpsNo((String)obj[0]);          //票据池编号                         
			query.setBpsName((String)obj[1]);        //票据池名称   
			query.setTotalAmt((BigDecimal)obj[2]);   //总金额
			query.setCreateDate(queryBean.getCreateDate());       //创建时间
			query.setBusiType(busiType);  //业务类型  01：资产-现金 02：资产-票据 03：融资业务-银承 04：融资业务-流贷  05：融资业务-保函  06：融资业务-国内信用证    			
			temp.add(query);
		}
		return temp;
	
	}
	
	@Override
	public List<CommonQueryBean> loadPedCrdtDailyList(CommonQueryBean queryBean) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select pd.bpsNo,pd.bpsName,sum(pd.loanAmt) from PedCrdtDaily pd where 1=1 and pd.bpsNo is not null");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		String busiType=null;
		if (queryBean != null) {
			if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//交易时间
				sb.append(" and pd.createDate >= :createDate1 and  pd.createDate <= :createDate2 ");
				keyList.add("createDate1");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getCreateDate()));
				keyList.add("createDate2");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getCreateDate()));
			}
			if (StringUtils.isNotBlank(queryBean.getCrdtType())) { //融资业务类型    XD_01:银承   XD_02:流贷   XD_03:保函   XD_04:国内信用证
				sb.append(" and pd.crdtType =:crdtType ");
				keyList.add("crdtType");
				valueList.add(queryBean.getCrdtType());
				
				if(queryBean.getCrdtType().equals("XD_01")){
					busiType="03";
				}
				if(queryBean.getCrdtType().equals("XD_02")){
					busiType="04";
				}
				if(queryBean.getCrdtType().equals("XD_03")){
					busiType="05";
				}
				if(queryBean.getCrdtType().equals("XD_04")){
					busiType="06";
				}
				
			}
		}
		sb.append("group by pd.bpsNo,pd.bpsName");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		List temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			query.setBpsNo((String)obj[0]);          //票据池编号                         
			query.setBpsName((String)obj[1]);        //票据池名称   
			query.setTotalAmt((BigDecimal)obj[2]);   //总金额
			query.setCreateDate(queryBean.getCreateDate());       //创建时间
			query.setBusiType(busiType);  //业务类型  01：资产-现金 02：资产-票据 03：融资业务-银承 04：融资业务-流贷  05：融资业务-保函  06：融资业务-国内信用证    			
			temp.add(query);
		}
		return temp;
	
	}
	
	@Override
	public List<PedAssetDaily> loadPedAssetDaily(CommonQueryBean queryBean ,Page page) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select pd from PedAssetDaily pd where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//交易时间
				sb.append(" and pd.createDate >= :createDate1 and  pd.createDate <= :createDate2 ");
				keyList.add("createDate1");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getCreateDate()));
				keyList.add("createDate2");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getCreateDate()));
			}
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) { //票据池编号
				sb.append(" and pd.bpsNo =:bpsNo ");
				keyList.add("bpsNo");
				valueList.add(queryBean.getBpsNo());
			}
			if (StringUtils.isNotBlank(queryBean.getCustNo())) { //客户号
				sb.append(" and pd.custNo =:custNo ");
				keyList.add("custNo");
				valueList.add(queryBean.getCustNo());
			}
			if (StringUtils.isNotBlank(queryBean.getBusiType())) { //业务类型
				sb.append(" and pd.assetType =:assetType ");
				keyList.add("assetType");
				valueList.add(queryBean.getBusiType());
			}
		}
		sb.append("order by pd.createTime asc");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List<PedAssetDaily> list = new ArrayList<PedAssetDaily>();
		if(null==page){
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}else{
			list = this.find(sb.toString(), keyArray, valueList.toArray(),page);
		}
		return list;
	}
	
	@Override
	public List<PedCrdtDaily> loadPedCrdtDaily(CommonQueryBean queryBean) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select pd from PedCrdtDaily pd where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//交易时间
				sb.append(" and pd.createDate >= :createDate1 and  pd.createDate <= :createDate2 ");
				keyList.add("createDate1");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getCreateDate()));
				keyList.add("createDate2");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getCreateDate()));
			}
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) { //票据池编号
				sb.append(" and pd.bpsNo =:bpsNo ");
				keyList.add("bpsNo");
				valueList.add(queryBean.getBpsNo());
			}
			if (StringUtils.isNotBlank(queryBean.getBusiType())) { //业务类型
				sb.append(" and pd.crdtType =:crdtType ");
				keyList.add("crdtType");
				valueList.add(queryBean.getBusiType());
			}
		}
		sb.append("order by pd.createTime asc");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
		                                                          .size()]);
		List<PedCrdtDaily> list = new ArrayList<PedCrdtDaily>();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		return list;	
	}
		
	@Override
	public List<PedAssetCrdtDaily> loadPedAssetCrdtDaily(CommonQueryBean queryBean,Page page) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select pd from PedAssetCrdtDaily pd where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (null!=queryBean.getCreateDate()&& !"".equals(queryBean.getCreateDate())) {//交易时间
				sb.append(" and pd.createDate >= :createDate1 and  pd.createDate <= :createDate2 ");
			    keyList.add("createDate1");
				valueList.add(DateUtils.getCurrentDayStartDate(queryBean.getCreateDate()));
				keyList.add("createDate2");
				valueList.add(DateUtils.getCurrentDayEndDate(queryBean.getCreateDate()));
		    }
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) { //票据池编号
				   sb.append(" and pd.bpsNo =:bpsNo ");
				   keyList.add("bpsNo");
			       valueList.add(queryBean.getBpsNo());
			  }
		}
		sb.append("order by pd.createTime desc");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List<PedAssetCrdtDaily> list = new ArrayList<PedAssetCrdtDaily>();
		if(page==null){
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}else{
			list = this.find(sb.toString(), keyArray, valueList.toArray(),page);
		}
		return list;
		
	}

		
}
