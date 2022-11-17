package com.mingtech.application.pool.draft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.service.AuditExtendService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 手工出池审批
 * @author Wu
 *
 */
@Service
public class AuditPoolOutServiceImpl extends GenericServiceImpl implements AuditExtendService {

	@Autowired
	PoolEBankService poolEBankService;
	@Autowired
	PedProtocolService pedProtocolService;
	@Autowired
	AutoTaskPublishService autoTaskPublishService;
	@Autowired
	FinancialService financialService;
	@Autowired
	AssetRegisterService assetRegisterService;
	@Autowired
	PoolBailEduService poolBailEduService;
	@Autowired
	DraftPoolInService draftPoolInService;
	@Autowired
	PoolEcdsService poolEcdsService;
	@Autowired
	DepartmentService departmentService;
	@Autowired
	ConsignService consignService;
	@Autowired
	DraftPoolQueryService draftPoolQueryService;
	
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
			Ret ret = this.txDealAuditPass(busiId,status);
			if(!ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				ApproveAuditDto dto = (ApproveAuditDto) poolEBankService.load(auditId,ApproveAuditDto.class);
				dto.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);
				poolEBankService.txStore(dto);
				json = ret.getRET_MSG();
			}
		}else if(PublicStaticDefineTab.AUDIT_STATUS_GOBACK.equals(status)){
			//当审批驳回时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(busiId, status);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(status)){
			this.txDealAudit(busiId, status);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_STOP.equals(status)){
			//当审批终止时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(busiId, status);
		}
		return json;
	}
	
	/**
	 * <p>方法名称:根据ID查询额度信息</p>
	 * @param id 主键
	 * @return FinancialLimitDto 额度信息
	 */

	//当审批成功时
	public Ret txDealAuditPass(String id,String status) throws Exception{
		//查询票据
		DraftPool pool = (DraftPool) poolEBankService.load(id,DraftPool.class);
		//查询协议
		PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, pool.getPoolAgreement(), null, null, null);
		
		List bills = new ArrayList();
		bills.add(pool.getAssetNb());
		
		
		List<String> assetNos = new ArrayList();
		Map<String,PoolBillInfo> changeBills= new HashMap<String, PoolBillInfo>();
		
		
		PoolBillInfo bill = pool.getPoolBillInfo();
		String assNo;
		if(pool.getDraftSource().equals(PoolComm.CS01) || pool.getSplitFlag().equals("0")){
			assNo = pool.getAssetNb() +"-0" +"-0";
		}else{
			assNo = pool.getAssetNb()+ "-" +pool.getBeginRangeNo() +"-"+pool.getEndRangeNo();
		}
		assetNos.add(assNo);
		changeBills.put(assNo, bill);
		
		/*
		 * 出池申请
		 */
		Ret result = poolEBankService.txApplyDraftPoolOutPJC003(bills, null, dto, null,changeBills,assetNos);
		
		if (Constants.TX_SUCCESS_CODE.equals(result.getRET_CODE())) {
			System.out.println("出池申请成功，判断是否做过拆分："+pool.getSplitId());
			/**
			 * 判断是否做过拆票,若做过需还原原始在池数据
			 */
			List<DraftPool> list = draftPoolQueryService.getDraftPoolList(bill.getBillinfoId());
			if(list != null && list.size() > 0){
				//出池做过拆分
				for (int i = 0; i < list.size(); i++) {
					DraftPool draftPool = list.get(i);
					if(draftPool.getAssetStatus().equals(PoolComm.DS_02)){
						//拆分在池数据
						PoolBillInfo info = draftPool.getPoolBillInfo();
						draftPool.setLockz(PoolComm.BBSPLOCK_02);
						draftPool.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);
						
						info.setEbkLock(PoolComm.EBKLOCK_02);//未锁票
						poolEBankService.txStore(draftPool);
						poolEBankService.txStore(info);
						
					}else{
						//拆分出池数据
						PoolBillInfo info = draftPool.getPoolBillInfo();
						draftPool.setLockz(PoolComm.BBSPLOCK_02);
						draftPool.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_PASS);
						
						info.setEbkLock(PoolComm.EBKLOCK_02);//未锁票
						poolEBankService.txStore(draftPool);
						poolEBankService.txStore(info);
						
					}
				}
			}
			/*
			 * 生成自动任务流水记录 异步执行额度释放 gcj 20210513
			 */
			List<DraftPoolOut> outlist = result.getSomeList3();
			if(outlist.size()>0){					
				Map<String, String> reqParams =new HashMap<String,String>();
				for (DraftPoolOut poolout:outlist) {
					reqParams.put("busiId", poolout.getId());
					autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOLOUT_EDU_TASK_NO, poolout.getId(), AutoTaskNoDefine.BUSI_TYPE_JZY, reqParams, poolout.getPlDraftNb(), null, null, null);
				}
			}
			pool.setAuditStatus(status);
			poolEBankService.txStore(pool);

		}
		return result;
	}
	
	//当审批中
	public void txDealAudit(String id,String status) throws Exception{
		//查询票据
		DraftPool pool = (DraftPool) poolEBankService.load(id,DraftPool.class);
		pool.setAuditStatus(status);
		poolEBankService.txStore(pool);
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
	public void txCancelEdu(String id,String status) throws Exception{
		//查询票据
		DraftPool pool = (DraftPool) poolEBankService.load(id,DraftPool.class);
		PoolBillInfo info = pool.getPoolBillInfo();
		

		/**
		 * 手工出池解除票据池经办锁不通知电票系统加锁
		 */
		info.setEbkLock(PoolComm.EBKLOCK_02);//未锁票

		pool.setTradeAmt(pool.getAssetAmt());
		pool.setLockz(PoolComm.BBSPLOCK_02);//未锁票
		pool.setTradeAmt(info.getFBillAmount());
		
		poolEBankService.txStore(info);
		poolEBankService.txStore(pool);
		
		pool.setAuditStatus(status);
		poolEBankService.txStore(pool);
		
		
	}

}
