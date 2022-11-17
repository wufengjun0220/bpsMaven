package com.mingtech.application.pool.online.loan.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.service.AuditExtendService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 支付计划贷款归还的审批处理
 * @author Wu
 *
 */
@Service
public class AuditPedOnlineCrdtServiceImpl extends GenericServiceImpl implements AuditExtendService {
	private static final Logger logger = Logger.getLogger(AuditPedOnlineCrdtServiceImpl.class);
	@Autowired
	PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	PoolCreditProductService poolCreditProductService;
	@Autowired
	PoolCoreService poolCoreService;
	@Autowired
	PoolEBankService poolEBankService;
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

	@Override
	public String txDealWithAuditResult(User user, String auditId,String busiId, String status, BusiTableConfig busiTabConfig) throws Exception {
		String json = "success";
		
		if(PublicStaticDefineTab.AUDIT_STATUS_PASS.equals(status)){
			
			//审批通过
			Ret ret = this.txRepayPayPlanAuditPass(busiId,status,auditId);
			
			if(!ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				ApproveAuditDto dto = (ApproveAuditDto) poolEBankService.load(auditId,ApproveAuditDto.class);
				dto.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);
				poolEBankService.txStore(dto);
				json = ret.getRET_MSG();
			}
			
		}else if(PublicStaticDefineTab.AUDIT_STATUS_GOBACK.equals(status)){
			
			//驳回
			this.txRepayPayPlanAuditCancel(busiId, status,auditId);
			
		}else if(PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(status)){
			
			//审批中--多岗情况
			this.txDealAudit(busiId, status,auditId);
			
		}else if(PublicStaticDefineTab.AUDIT_STATUS_STOP.equals(status)){
			
			//审批拒绝
			this.txRepayPayPlanAuditCancel(busiId, status,auditId);
			
		}
		return json;
	}
	

	/**
	 * 在线流贷支付计划【贷款未用归还】审批通过
	 * @param id
	 * @param status
	 * @param auditId
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-25上午9:52:00
	 */
	public Ret txRepayPayPlanAuditPass(String id,String status,String auditId) throws Exception{
	
		logger.info("在线流贷支付计划【贷款未用归还】审批通过，处理的支付计划ID为："+id);
		
		//查询审批对象
		ApproveAuditDto auditDto = (ApproveAuditDto) pedOnlineCrdtService.load(auditId,ApproveAuditDto.class);
		//支付list对象 
		PlCrdtPayList pay = (PlCrdtPayList) pedOnlineCrdtService.load(id,PlCrdtPayList.class);
		
		//查询支付计划对象
		PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(pay.getPayPlanId(),PlCrdtPayPlan.class);
		
		BigDecimal totalRelsAmt = auditDto.getAuditAmt();//还款金额
		
		//生成支付明细-获取流水号
		String flowNo = pay.getRepayFlowNo();	
		
		//查询借据信息
        PedCreditDetail loan = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(null,plan.getLoanNo());
        
       //支付计划未用还款
		Ret ret = pedOnlineCrdtService.txRepayOnlinePayPlan(loan, totalRelsAmt, flowNo);

		//操作失败，抛异常
        if(!Constants.TX_SUCCESS_CODE.equals(ret.getRET_CODE())){
        	return ret;
        }
        plan.setOperaStatus(status);
        this.txStore(plan);
        return ret;
	}
	
	//当审批中
	public void txDealAudit(String id,String status,String auditId) throws Exception{
		
		//支付list对象 
		PlCrdtPayList pay = (PlCrdtPayList) pedOnlineCrdtService.load(id,PlCrdtPayList.class);
		
		//查询支付计划对象
		PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(pay.getPayPlanId(),PlCrdtPayPlan.class);
		plan.setOperaStatus(status);
		this.txStore(plan);
	}

	/**
	 * 在线流贷支付计划【贷款未用归还】审批驳回/不通过
	 * @param id
	 * @param status
	 * @param auditId
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-25上午10:13:32
	 */
	public void txRepayPayPlanAuditCancel(String id,String status,String auditId) throws Exception{
		
		logger.info("在线流贷支付计划【贷款未用归还】审批驳回/不通过，处理的支付计划ID为："+id);
		
		//支付list对象 
		PlCrdtPayList pay = (PlCrdtPayList) pedOnlineCrdtService.load(id,PlCrdtPayList.class);
		
		//查询支付计划对象
		PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(pay.getPayPlanId(),PlCrdtPayPlan.class);
		plan.setOperaStatus(status);
		plan.setOperationType("0");
		plan.setOperaBatch(null);
		this.txStore(plan);
		
		pay.setUpdateTime(new Date());
		pay.setStatus(PoolComm.PAY_STATUS_02);//支付失败
		pay.setPayDesc("票据池贷款归还支付计划审批拒绝");//支付结果说明
		this.txStore(pay);
	}
	
	/**
	 * 生成支付计划明细
	 * @param plan
	 * @param payAmt
	 * @return
	 * @throws Exception
	 */
	public String txCreatePayList(PlCrdtPayPlan plan,BigDecimal payAmt) throws Exception{
		
		logger.info("在线流贷支付计划【贷款未用归还】审批支付计划生成...");
		
		PlCrdtPayList pay = new PlCrdtPayList();
		pay = pedOnlineCrdtService.toCreatPlCrdtPayListByPlan(plan, pay);
		pay.setTransChanel(PublicStaticDefineTab.CHANNEL_NO_BPS);//渠道-票据池
		pay.setOperatorType(PoolComm.PAY_TYPE_1);//支付类型-还款
		pay.setStatus(PoolComm.PAY_STATUS_00);//初始化
		pay.setSerialNo(plan.getSerialNo());//支付计划编号
		pay.setPayAmt(payAmt);//释放金额
		String flowNo = "PAY-"+Long.toString(System.currentTimeMillis());//流水号，用来标记该批申请
		pay.setRepayFlowNo(flowNo);//流水号
		pay.setUsage("票据池-贷款未用归还");//用途
		pay.setPostscript("票据池-贷款未用归还");//附言
		pay = pedOnlineCrdtService.toCreatPlCrdtPayListByPlan(plan, pay);
		
		this.txStore(pay);
		
		return flowNo;

	}

}
