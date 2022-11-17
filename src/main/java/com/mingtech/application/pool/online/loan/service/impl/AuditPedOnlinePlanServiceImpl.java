package com.mingtech.application.pool.online.loan.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.service.AuditExtendService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtInfo;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 支付计划修改的审批处理
 * @author Wu
 *
 */
@Service
public class AuditPedOnlinePlanServiceImpl extends GenericServiceImpl implements AuditExtendService {

	@Autowired
	PedOnlineCrdtService pedOnlineCrdtService;
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
	public String txDealWithAuditResult(User user, String auditId,
			String busiId, String status, BusiTableConfig busiTabConfig)
			throws Exception {
		String json = "success";
		if(PublicStaticDefineTab.AUDIT_STATUS_PASS.equals(status)){
			//当审批成功时 根据操作标识修改额度对象数据
			this.txDealAuditPass(busiId,status,auditId);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_GOBACK.equals(status)){
			//当审批驳回时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(busiId, status,auditId);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(status)){
			this.txDealAudit(busiId, status,auditId);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_STOP.equals(status)){
			//当审批终止时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(busiId, status,auditId);
		}
		return json;
	}
	
	/**
	 * <p>方法名称:根据ID查询额度信息</p>
	 * @param id 主键
	 * @return FinancialLimitDto 额度信息
	 */

	//当审批成功时
	public void txDealAuditPass(String id,String status,String auditId) throws Exception{
		//查询审批对象
		ApproveAuditDto auditDto = (ApproveAuditDto) pedOnlineCrdtService.load(auditId,ApproveAuditDto.class);
		//查询支付计划对象
		PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(id,PlCrdtPayPlan.class);
		//查询借据对象
		PlOnlineCrdt crdt = (PlOnlineCrdt) pedOnlineCrdtService.load(plan.getCrdtId(),PlOnlineCrdt.class);
		
		//修改支付计划
		//收款人释放额度
		plan.setUsedAmt(plan.getUsedAmt().add(auditDto.getAuditAmt()));
		if(plan.getTotalAmt().equals(plan.getUsedAmt().add(plan.getRepayAmt()))){
			plan.setStatus(PublicStaticDefineTab.PAY_PLAN_02);
		}
		plan.setOperaStatus(status);
		PedOnlineCrdtInfo payee = this.queryOnlinePayeeByAcctNo(plan);
		if(payee != null){
			payee.setPayeeUsedAmt(payee.getPayeeUsedAmt().subtract(plan.getTotalAmt()));
			this.dao.store(payee);
		}
		this.dao.store(plan);
		//保存历史
		pedOnlineCrdtService.txSavePlCrdtPayPlanHist(plan,null);
		pedOnlineCrdtService.txSyncPlOnlineCrdtStatus(crdt);
		
	}
	
	//当审批中
	public void txDealAudit(String id,String status,String auditId) throws Exception{
		//查询支付计划对象
		PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(id,PlCrdtPayPlan.class);
		plan.setOperaStatus(status);
		this.txStore(plan);
	}

	/**
	 * 	当审批驳回或拒绝时 
	 * 新增的拒绝需将额度还原,状态回滚;
	 * 生效修改的拒绝需将原始对象还原,此条额度对象置为历史,额度重新计算;
	 * 失效的拒绝  状态回滚,额度重新计算
	 * 失效再生效拒绝  状态回滚,额度重新计算
	 * @param id
	 * @param status
	 * @throws Exception
	 */
	public void txCancelEdu(String id,String status,String auditId) throws Exception{
		//查询审批的支付计划
		PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(id,PlCrdtPayPlan.class);
		plan.setOperaStatus(status);
		plan.setOperationType("0");
		plan.setOperaBatch(null);
		this.txStore(plan);
	}
	
	public PedOnlineCrdtInfo queryOnlinePayeeByAcctNo(PlCrdtPayPlan plan) throws Exception{
		OnlineQueryBean queryBean = new OnlineQueryBean();
		PedOnlineCrdtInfo PedOnlineCrdtInfo = null;
		queryBean.setPayeeAcctNo(plan.getDeduAcctNo());
		queryBean.setOnlineCrdtNo(plan.getOnlineCrdtNo());
		List<PedOnlineCrdtInfo> payees = pedOnlineCrdtService.queryOnlineCrdtPayeeList(queryBean, null);
		if(payees != null){
			PedOnlineCrdtInfo = payees.get(0);
		}
		return PedOnlineCrdtInfo;
	}

}
