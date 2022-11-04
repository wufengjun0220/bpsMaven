package com.mingtech.application.pool.online.common.service.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.LoanProduct;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.common.service.OnlineCommonService;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
@Service("onlineCommonService")
public class OnlineCommonServiceImpl extends GenericServiceImpl implements OnlineCommonService {
	private static final Logger logger = Logger.getLogger(OnlineCommonServiceImpl.class);

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private FinancialService financialService ;
	@Autowired
	private PoolCreditProductService poolCreditProductService;

	
	@Override
	public void txSavePedCreditDetail(String busiType, String creditAcctNo,Object obj) throws Exception {
		
		PedCreditDetail detail = new PedCreditDetail();
		
		if(PublicStaticDefineTab.PRODUCT_001.equals(busiType)){//银承
			PlOnlineAcptDetail acpt = (PlOnlineAcptDetail) obj;
			PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(acpt.getAcptBatchId(),PlOnlineAcptBatch.class);
			
			//查询如果已经存在该借据，则覆盖原借据信息
			CreditQueryBean queryBean = new CreditQueryBean();
			queryBean.setCrdtNo(acpt.getLoanNo());
			List<PedCreditDetail> loanList =  poolCreditProductService.queryCreditDetailList(queryBean); 
			if(null != loanList){
				detail = loanList.get(0);
			}
			
			PedOnlineAcptProtocol protocol = pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
			detail.setLoanNo(acpt.getLoanNo());//借据号
			detail.setCrdtNo(batch.getContractNo());//信贷主业务合同号
			detail.setRiskLevel(PoolComm.LOW_RISK);//风险等级
			detail.setCustNumber(batch.getCustNo());//客户号
			detail.setCustName(acpt.getIssuerName());//客户名称
			detail.setTransTime(new Date());//交易时间
			detail.setStartTime(new Date());//开始时间=交易时间
			detail.setLoanType(PoolComm.XD_01);//交易类型   （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
			detail.setLoanStatus(PoolComm.JJ_01);//交易状态（JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清）
			detail.setTransAccount(creditAcctNo);//交易账号 （表内业务为贷款账号，表外业务为业务保证金账号）
			detail.setLoanAmount(acpt.getBillAmt());//借据总金额
			detail.setLoanBalance(acpt.getBillAmt());//借据余额    （第一次从mis获取时候直接赋值为 借据总金额）
			Date date = DateUtils.formatDate(acpt.getDueDate(), DateUtils.ORA_DATE_FORMAT);
			detail.setEndTime(date);//借据到期日
			detail.setActualAmount(acpt.getBillAmt().multiply(batch.getPoolCreditRatio()).divide(new BigDecimal("100")));//剩余还款总额
			detail.setDetailStatus(PoolComm.LOAN_1);//还需处理
			detail.setBpsNo(protocol.getBpsNo());//票据池编号
			detail.setTransTime(new Date());
			detail.setStartTime(new Date());
			detail.setIfAdvanceAmt(PoolComm.NO);
			detail.setTransAccount(acpt.getTransAccount());//业务保证金账号
			detail.setInitialMarginAmt(acpt.getBillAmt().multiply(batch.getDepositRatio()).divide(new BigDecimal("100")));//初始业务保证金金额
			detail.setIsOnline(PoolComm.YES);//线上线下标识
			this.txStore(detail);
		}else{
			PlOnlineCrdt crdt = (PlOnlineCrdt) obj;
			PedOnlineCrdtProtocol protocol = pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(crdt.getOnlineCrdtNo());
			//查询如果已经存在该借据，则覆盖原借据信息
			CreditQueryBean queryBean = new CreditQueryBean();
			queryBean.setCrdtNo(crdt.getLoanNo());
			List<PedCreditDetail> loanList =  poolCreditProductService.queryCreditDetailList(queryBean); 
			if(null != loanList){
				detail = loanList.get(0);
			}
			
			detail.setCrdtNo(crdt.getContractNo());//信贷主业务合同号
			detail.setCustNumber(crdt.getCustNo());//客户号
			detail.setRiskLevel(PoolComm.LOW_RISK);//风险等级
			detail.setCustName(protocol.getCustName());//客户名称
			detail.setLoanNo(crdt.getLoanNo());//借据号
			detail.setTransTime(crdt.getAcctDate());//交易时间
			detail.setStartTime(crdt.getAcctDate());//开始时间=交易时间
			detail.setLoanType(PoolComm.XD_02);//交易类型   （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
			detail.setLoanStatus(PoolComm.JJ_01);//交易状态（JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清）
			detail.setTransAccount(crdt.getTransAccount());//交易账号 （表内业务为贷款账号，表外业务为业务保证金账号）
			detail.setLoanAmount(crdt.getLoanAmt());//借据总金额
			detail.setLoanBalance(crdt.getLoanAmt());//借据余额    （第一次获取时候直接赋值为 借据总金额）
			detail.setEndTime(crdt.getDueDate());//借据到期日
			detail.setActualAmount(crdt.getLoanAmt());//剩余还款总额
			detail.setDetailStatus(PoolComm.LOAN_1);//还需处理
			detail.setBpsNo(crdt.getBpsNo());//票据池编号
			detail.setTransTime(new Date());
			detail.setStartTime(new Date());
			detail.setIfAdvanceAmt(PoolComm.NO);
			detail.setIsOnline(PoolComm.YES);//线上线下标识
			this.txStore(detail);
			

			//该合同下的全部借据(在线流贷只有一条)
			List<PedCreditDetail> crdtDetailList =  new ArrayList<PedCreditDetail>();
			crdtDetailList.add(detail);
			 // 将原占用detail的额度信息，置换为占用PedCreditDetail
			financialService.txOnlineBusiCreditChange(crdt.getContractNo(), crdtDetailList, detail.getBpsNo());
			
		}
	}

	@Override
	public CreditProduct txSaveCreditProduct(String busiType,Object obj,PlOnlineAcptDetail detail) throws Exception {
		
		logger.info("在线业务主业务合同组装...");		
		CreditProduct product  =new CreditProduct();
		
		if(PublicStaticDefineTab.PRODUCT_001.equals(busiType)){//银承
			PlOnlineAcptBatch batch = (PlOnlineAcptBatch) obj;
			
			//查询如果已经存在该主业务合同，则覆盖原主业务合同
			CreditQueryBean queryBean = new CreditQueryBean();
			queryBean.setCrdtNo(batch.getContractNo());
			List<CreditProduct> prodList =  poolCreditProductService.queryCedtProductList(queryBean); 
			if(null != prodList){
				CreditProduct product1 = prodList.get(0);
				PedOnlineAcptProtocol protocol =  pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setAcptBatchId(batch.getId());
				bean.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_1);
				//获取最晚到期日也就是银承合同的到期日
				List list = pedOnlineAcptService.queryOnlinAcptByStatus(bean);
				Date dueDate = (Date)list.get(0);
				product1.setBpsNo(protocol.getBpsNo());
				product1.setCrdtNo(batch.getContractNo());//信贷业务号 在线银承协议编号+YYYYMMDD+序列号（5位，每日从00001开始）
				product1.setCustNo(protocol.getCustNumber());
				product1.setCustName(protocol.getCustName());
				product1.setCrdtType(PoolComm.XD_01);//信贷产品类型
				product1.setCrdtIssDt(batch.getApplyDate());//开始时间
				product1.setCrdtDueDt(dueDate);//结束时间
				product1.setMinDueDate(dueDate);//最早到期日
				if(null!=detail){
					if(null!=product1.getUseAmt()){
						product1.setUseAmt(product1.getUseAmt().add(detail.getBillAmt()));
						product1.setRestAmt(product1.getUseAmt().add(detail.getBillAmt()));//业务余额:表示该融资业务剩余的待还金额
					}else{
						product1.setUseAmt(detail.getBillAmt());
						product1.setRestAmt(detail.getBillAmt());//业务余额:表示该融资业务剩余的待还金额
					}
				}else{
					product1.setUseAmt(batch.getTotalAmt());
					product1.setRestAmt(batch.getTotalAmt());//业务余额:表示该融资业务剩余的待还金额
				}
				product1.setCcupy("1");//占用比例--在线银承业务均为1
				product1.setRisklevel(PoolComm.LOW_RISK);//风险等级
				product1.setSttlFlag(PoolComm.JQZT_WJQ);//结清状态--未结清
				product1.setCrdtStatus(PoolComm.RZCP_YQS);//合同状态--额度占用成功
				product1.setIsOnline(PoolComm.YES);//线上业务
				this.txStore(product1);
				return product1;
			}
			
			PedOnlineAcptProtocol protocol =  pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
			OnlineQueryBean bean = new OnlineQueryBean();
			bean.setAcptBatchId(batch.getId());
			bean.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_1);
			//获取最晚到期日也就是银承合同的到期日
			List list = pedOnlineAcptService.queryOnlinAcptByStatus(bean);
			Date dueDate = (Date)list.get(0);
			product.setBpsNo(protocol.getBpsNo());
			product.setCrdtNo(batch.getContractNo());//信贷业务号 在线银承协议编号+YYYYMMDD+序列号（5位，每日从00001开始）
			product.setCustNo(protocol.getCustNumber());
			product.setCustName(protocol.getCustName());
			product.setCrdtType(PoolComm.XD_01);//信贷产品类型
			product.setCrdtIssDt(batch.getApplyDate());//开始时间
			product.setCrdtDueDt(dueDate);//结束时间
			product.setMinDueDate(dueDate);//最早到期日
			if(null!=detail){
				if(null!=product.getUseAmt()){
					product.setUseAmt(product.getUseAmt().add(detail.getBillAmt()));
					product.setRestAmt(product.getUseAmt().add(detail.getBillAmt()));//业务余额:表示该融资业务剩余的待还金额
				}else{
					product.setUseAmt(detail.getBillAmt());
					product.setRestAmt(detail.getBillAmt());//业务余额:表示该融资业务剩余的待还金额
				}
			}else{
				product.setUseAmt(batch.getTotalAmt());
				product.setRestAmt(batch.getTotalAmt());//业务余额:表示该融资业务剩余的待还金额
			}
			product.setCcupy("1");//占用比例--在线银承业务均为1

		}else{
			PlOnlineCrdt batch = (PlOnlineCrdt) obj;
			
			//查询如果已经存在该主业务合同，则覆盖原主业务合同
			CreditQueryBean queryBean = new CreditQueryBean();
			queryBean.setCrdtNo(batch.getContractNo());
			List<CreditProduct> prodList =  poolCreditProductService.queryCedtProductList(queryBean); 
			if(null != prodList){
				product = prodList.get(0);
			}	
			PedOnlineCrdtProtocol protocol = pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(batch.getOnlineCrdtNo());
			product.setBpsNo(protocol.getBpsNo());
			product.setCrdtNo(batch.getContractNo());//信贷业务号 在线银承协议编号+YYYYMMDD+序列号（5位，每日从00001开始）
			product.setCustNo(protocol.getCustNumber());
			product.setCustName(protocol.getCustName());
			product.setCrdtType(PoolComm.XD_02);//信贷产品类型
			product.setCrdtIssDt(batch.getApplyDate());//开始时间
			product.setCrdtDueDt(batch.getDueDate());//结束时间
			product.setUseAmt(batch.getLoanAmt());
			product.setRestAmt(batch.getLoanAmt());//业务余额:表示该融资业务剩余的待还金额
			product.setCcupy(batch.getPoolCreditRatio().divide(new BigDecimal("100")).toString());//占用比例
		}
		product.setRisklevel(PoolComm.LOW_RISK);//风险等级
		product.setSttlFlag(PoolComm.JQZT_WJQ);//结清状态--未结清
		product.setCrdtStatus(PoolComm.RZCP_YQS);//合同状态--额度占用成功
		product.setIsOnline(PoolComm.YES);//线上业务
		this.txStore(product);
		return product;
	}

	@Override
	public String getDeductionProduct(String loanProductNo) {

		StringBuffer hql = new StringBuffer( " select loan from LoanProduct loan where loan.loanProductNo = '"+loanProductNo+"'");
		List<LoanProduct> res = (List<LoanProduct>)this.find(hql.toString());
		if(null!=res && res.size()>0){
			LoanProduct loan = res.get(0);
			return loan.getDeductionProductNo();
		}
		return null;
	}
	/**
	 * 更新在线流贷已用额度
	 * @param PlOnlineCrdt
	 * @date 2021-8-26
	 */
    public void txSavePedOnlineCrtdProtocol(PlOnlineCrdt crdt) throws Exception {

		PedOnlineCrdtProtocol protocol = pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(crdt.getOnlineCrdtNo());
		protocol.setUsedAmt(protocol.getUsedAmt().add(crdt.getLoanAmt()));
		this.txStore(protocol);
	}
    

}
