package com.mingtech.application.pool.edu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import sun.util.logging.resources.logging;

import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.domain.ApproveDto;
import com.mingtech.application.audit.service.AuditExtendService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.creditmanage.service.impl.CreditRegisterServiceImpl;
import com.mingtech.application.pool.edu.domain.PedBailTrans;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 保证金支取的审批
 * @author Wu
 *
 */
@Service("AuditPedBailTransService")
public class AuditPedBailTransServiceImpl extends GenericServiceImpl implements AuditExtendService {
	private static final Logger logger = Logger.getLogger(AuditPedBailTransServiceImpl.class);

	@Autowired
	PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	PoolCoreService poolCoreService;
	@Autowired
	PoolBailEduService poolBailEduService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private PoolEBankService poolEBankService;
	
	 
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
		String approveComment = "";//审批意见
		//查询审批意见
		List<ApproveDto> list = this.queryApproveDtoList(busiId);
		if(list != null){
			for (int i = 0; i < list.size(); i++) {
				ApproveDto dto = list.get(i);
				approveComment = approveComment + "第"+(i+1)+"次审批意见为："+ dto.getApproveComment() + "；";
			}
			if(approveComment.length() > 0){
				approveComment.substring(0,approveComment.length()-1);
			}
		}
		
		if(PublicStaticDefineTab.AUDIT_STATUS_PASS.equals(status)){
			//当审批成功时 根据操作标识修改额度对象数据
			Ret ret = this.txDealAuditPass(approveComment,busiId,status);

			if(!ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				ApproveAuditDto dto = (ApproveAuditDto) poolEBankService.load(auditId,ApproveAuditDto.class);
				dto.setAuditStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);
				poolEBankService.txStore(dto);
				json = ret.getRET_MSG();
			}
			
		}else if(PublicStaticDefineTab.AUDIT_STATUS_GOBACK.equals(status)){
			//当审批驳回时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(approveComment,busiId, status);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(status)){
			this.txDealAudit(approveComment,busiId, status);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_STOP.equals(status)){
			//当审批终止时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(approveComment,busiId, status);
		}
		return json;
	}
	
	/**
	 * <p>方法名称:根据ID查询额度信息</p>
	 * @param id 主键
	 * @return FinancialLimitDto 额度信息
	 */

	//当审批成功时
	public Ret txDealAuditPass(String approveComment,String busiId,String status) throws Exception{
		PedBailTrans trans = (PedBailTrans) poolBailEduService.load(busiId,PedBailTrans.class);

		String apId = null;
		String proId = null;
		String bpsNo =  null;
		Ret ret = new Ret();
		
		/**
		 * 校验票据池额度是否充足
		 */
		PedProtocolDto dto = pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, trans.getBpsNo(), trans.getCustomer(), null, null) ;
		
		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
		apId = ap.getApId();
		
		proId = dto.getPoolInfoId();
		bpsNo = dto.getPoolAgreement();
		 //assetPool锁
		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
		if(!isLockedSucss){//加锁失败
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("该票据池有其他额度相关任务正在处理中，请稍后再试！");
			return ret;
		}
		
		try {
			//同步核心保证金，并重新计算额度
			financialService.txBailChangeAndCrdtCalculation(dto);
		} catch (Exception e) {
			pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG(e.getMessage());
			return ret;
		}
		
		//解锁AssetPool表，并重新计算该表数据
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);

		//获取最新保证金
		AssetType type = pedAssetTypeService.queryPedAssetTypeByProtocol(dto, PoolComm.ED_BZJ_HQ);
		Map map  = poolEBankService.queryMarginBalance(dto.getCustnumber(), dto.getPoolAgreement());
		BigDecimal avalBalance = (BigDecimal)map.get("AVAL_BALANCE");//保证金可支取金额
		
		
		if(avalBalance.compareTo(trans.getTranAmt())<0){//可支付金额  < 支取 则不允许
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("转出金额高于票据池可支取金额，交易失败！");
			return ret;
		}
		
		
		ret =this.doMarginWithdrawal(trans);
		poolBailEduService.txUpdateBailDetail(trans.getBpsNo());
		if(ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			trans.setPlanStatus(PoolComm.BAIL_TRANS_1);//成功
			trans.setAuditRemark(approveComment);
			trans.setStatus(status);
			poolBailEduService.txStore(trans);
		}else{
			trans.setPlanStatus(PoolComm.BAIL_TRANS_2);//失败
			trans.setErrLog(ret.getRET_MSG());
			trans.setAuditRemark(approveComment);
			poolBailEduService.txStore(trans);
			return ret;
		}
		
		return ret;
	}
	
	//当审批中
	public void txDealAudit(String approveComment,String id,String status) throws Exception{
		PedBailTrans trans = (PedBailTrans) poolBailEduService.load(id,PedBailTrans.class);
		trans.setStatus(status);
		trans.setAuditRemark(approveComment);
		poolBailEduService.txStore(trans);
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
	public void txCancelEdu(String approveComment,String id,String status) throws Exception{
		PedBailTrans trans = (PedBailTrans) poolBailEduService.load(id,PedBailTrans.class);
		trans.setStatus(status);
		trans.setAuditRemark(approveComment);
		poolBailEduService.txStore(trans);
	}

	/**
	 * 调用核心保证金划转接口
	 * @Description TODO
	 * @author Ju Nana
	 * @param queryParam
	 * @return
	 * @throws Exception
	 * @date 2019-6-16上午2:03:59
	 */
	private Ret doMarginWithdrawal(PedBailTrans trans) throws Exception{
		CoreTransNotes transNotes = new CoreTransNotes();
		Ret ret = new Ret();
		transNotes.setTranAmt(BigDecimalUtils.getStringValue(trans.getTranAmt()));//交易金额   待定
		transNotes.setDrAcctNo(trans.getDrAcctNo());//借方账号
		transNotes.setDrAcctNoName(trans.getDrAcctName());//借方账号名称
		transNotes.setCrAcctNo(trans.getCrAcctNo());//贷款账号
		transNotes.setCrAcctNoName(trans.getCrAcctName());//贷款账号名称
		transNotes.setFrntDate(DateUtils.toString(new Date(), "yyyyMMdd"));//第三方日期
		String str = poolBatchNoUtils.txGetFlowNo();
		
		transNotes.setSerSeqNo(str);//第三方流水号
		transNotes.setRemark(trans.getUsage()+" "+trans.getRemark());//备注
		ReturnMessageNew response = poolCoreService.doMarginWithdrawal(transNotes);
		
		try {	
			String bpsNo = trans.getBpsNo();
			
			logger.info("保证金支取核心支取成功后，额度更新，处理票据池编号【"+bpsNo+"】....");
			
			PedProtocolDto pool  =  pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, bpsNo, null, null, null);
			String   id = 	bpsNo +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成
			Map<String,String> reqParams = new HashMap<String,String>();
			reqParams.put("proId", pool.getPoolInfoId());
			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_CALCU_NO, id, AutoTaskNoDefine.BUSI_TYPE_CAL, reqParams, bpsNo, bpsNo, null, null);
		} catch (Exception e) {
			logger.error("保证金支取审批通过后调度异常：",e);
		}
		
		
		if (response.isTxSuccess()) {
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("保证金划转成功！");
		} else {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("核心保证金划转失败！返回码" + response.getRet().getRET_CODE() + "，返回信息" + response.getRet().getRET_MSG());
		}
		trans.setSerSeqNo(str);
		return ret;
	}

	private List<ApproveDto> queryApproveDtoList(String busiId) throws Exception{
		String sql ="select dto from ApproveDto dto where dto.bussinessId = '"+busiId+"' order by dto.approveDate asc";
		List list = this.find(sql.toString());
		return list;
	}
	
}
