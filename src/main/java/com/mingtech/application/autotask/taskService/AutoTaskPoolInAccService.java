package com.mingtech.application.autotask.taskService;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 *自动任务业务处理类
 *质押入池签收、记账
 *gcj 20210512
 */


public class AutoTaskPoolInAccService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolInAccService.class);
	DepartmentService departmentService=DepartmentServiceFactory.getDepartmentService();
	PedProtocolService pedProtocolService= PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();
	PoolCoreService poolCoreService = PoolCommonServiceFactory.getPoolCoreService();
	PoolEcdsService poolEcdsService =PoolCommonServiceFactory.getPoolEcdsService();
	AutoTaskPublishService autoTaskPublishService =PoolCommonServiceFactory.getAutoTaskPublishService();
	PedAssetPoolService  pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();
	
	/**
	 * gcj 20210512
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		DraftPoolIn poolIn = draftPoolInService.loadByPoolInId(busiId);
		poolIn.setTaskDate(new Date());
		if(null==poolIn){
			this.response(Constants.TX_FAIL_CODE, "根据ID"+busiId+"未能找到入池业务明细实体", response, ret);
			return response;
		}
		PedProtocolDto dto = pedProtocolService.queryProtocolDto( null, null,poolIn.getPoolAgreement(), null, null, null);
		
		/*
		 * 锁AssetPool表
		 */
		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
		String apId = ap.getApId();
		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
		if(!isLockedSucss){//加锁失败
			ret.setRET_CODE(Constants.EBK_11);
			ret.setRET_MSG("票据池任务处理中，请稍后再试！");
			response.setRet(ret);
			logger.info("票据池【"+dto.getPoolAgreement()+"】上锁！");
			return response;
		}
		
		
		if(null!=queryType){//非第一次进行记账操作
			/*
			 * 记账之前需要查证
			 */
			ReturnMessageNew res=this.doAccountConfirm(poolIn);
			if(res.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){//查证成功
				
				//记账成功继续后续操作
				DraftPool poolOld = draftPoolInService.getDraftPoolByDraftNb(poolIn.getPlDraftNb(),poolIn.getBeginRangeNo(),poolIn.getEndRangeNo());
				if(null != poolOld){
					poolOld.setAssetStatus(PoolComm.DS_01);
					pedProtocolService.txStore(poolOld);
				}
				draftPoolInService.txSendPoolNotify(poolIn,null);
				this.response(Constants.TX_SUCCESS_CODE, "票号为["+poolIn.getPlDraftNb()+"]的票"+"记账成功", response, ret);
				
				pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
				
				return response;
			}else{
				//失败换流水号
				String str = poolBatchNoUtils.txGetFlowNo();
				poolIn.setDevSeqNo(str);//保存流水号
			}
			
		}
		
		
		
		/*
		 * 记账处理 
		 */
		
		response=this.txAccountProcess(poolIn, dto);

		
		/*
		 * 解锁AssetPool表，并重新计算该表数据
		 */
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
		
		return response;
	}
	
	
	
	/**
	 * 入池核心记账操作
	 * @param poolIn
	 * @param dto
	 * @throws Exception
	 * @author Ju Nana
	 * @date 20210512
	 */
	private ReturnMessageNew txAccountProcess(DraftPoolIn poolIn,PedProtocolDto dto) throws Exception{
		
		logger.info("入池电票"+poolIn.getPlDraftNb()+"记账及额度处理-----------------开始...");
		
		ReturnMessageNew res=new ReturnMessageNew();
		Ret ret = new Ret();
		
		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setAmtAct(BigDecimalUtils.setScale(poolIn.getPlIsseAmt()).toString());//担保金额
		transNotes.setBilCode(poolIn.getPlDraftNb());//票号
		if(StringUtils.isNotBlank(poolIn.getDraftSource()) && !poolIn.getDraftSource().equals(PoolComm.CS01)){
			transNotes.setBeginRangeNo(poolIn.getBeginRangeNo());//票据开始子区间号
			transNotes.setEndRangeNo(poolIn.getEndRangeNo());//票据结束子区间号
		}
		
		
		if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
			transNotes.setBrcNo(dto.getCreditDeptNo());//融资机构号
		}else{
			transNotes.setBrcNo(dto.getOfficeNet());//受理网点
		}
		Department dept = departmentService.getDepartmentByInnerBankCode(transNotes.getBrcNo());
		if(dept != null){
			transNotes.setBrcBld(dept.getAuditBankCode());//报文头需赋值的机构
		}else{
			logger.info("未查询到部门信息");
			throw new Exception("未查询到部门信息");
		}
		transNotes.setCustId(poolIn.getCustNo());//核心客户号 
		String colNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);//流水号
		transNotes.setColNo(colNo);//担保编号
		transNotes.setDateDue(DateUtils.toString(poolIn.getPlDueDt(), "yyyyMMdd"));//到期日
		if(poolIn.getDevSeqNo() != null && !"".equals(poolIn.getDevSeqNo())){
			transNotes.setDevSeqNo(poolIn.getDevSeqNo());//第三方流水号
		}else{
			String str = poolBatchNoUtils.txGetFlowNo();
			transNotes.setDevSeqNo(str);//第三方流水号
			poolIn.setDevSeqNo(str);//保存流水号
			draftPoolInService.txStore(poolIn);
		}
		transNotes.setNoVouCom(poolIn.getPlTradeProto());//合同号
		transNotes.setNumBatch(DateUtils.dtuGetCurDatTimStr().substring(0, 8));//批号
		transNotes.setTypGag("2");//抵质押物类型
		transNotes.setNoVouCom(dto.getContract());//合同号
		try {
			/*
			 * 入池记账操作
			 */
			ReturnMessageNew response = poolCoreService.PJH580314Handler(transNotes);
			if(response.isTxSuccess()){//记账成功
				poolIn.setGuaranteeNo(transNotes.getColNo());
				logger.info("票号为["+transNotes.getBilCode()+"]的票,记账成功plPool、plPoolIn与cdEdraft表落库...");			
				
				
				draftPoolInService.txSendPoolNotify(poolIn,null);
				
				
				poolIn.setHostSeqNo((String)response.getBody().get("HOST_SEQ_NO"));//记账完成后记录

				draftPoolInService.txStore(poolIn);
				this.response(Constants.TX_SUCCESS_CODE, "票号为["+transNotes.getBilCode()+"]的票"+"记账成功", res, ret);
			}else{
				this.response(Constants.TX_FAIL_CODE, "票号为["+transNotes.getBilCode()+"]的票"+"记账失败", res, ret);
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			this.response(Constants.TX_FAIL_CODE, "票号为["+transNotes.getBilCode()+"]的票"+"记账失败", res, ret);
		}
		return res;
	}
	
	/**
	 * 入池核心记账查证
	 * @param poolIn
	 * @param dto
	 * @throws Exception
	 * @author gcj
	 * @date 20210524
	 */
	private ReturnMessageNew doAccountConfirm(DraftPoolIn poolIn) throws Exception{
		
		logger.info("入池电票"+poolIn.getPlDraftNb()+"查证-----------------开始...");
		ReturnMessageNew res=new ReturnMessageNew();
		Ret ret = new Ret();
		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setSerSeqNo(poolIn.getDevSeqNo());//流水号
		transNotes.setAcctDate(DateUtils.formatDate(new Date(),DateUtils.ORA_DATE_FORMAT));//记账日期
		/*
		 * 查证记账操作
		 */
		String status ="";
		ReturnMessageNew response  = poolCoreService.CORE002Handler(transNotes);
		if(response.isTxSuccess()){//查证返回成功
			this.response(Constants.TX_SUCCESS_CODE, "票号为["+poolIn.getPlDraftNb()+"]的票"+"查证成功,不用再次记账", res, ret);
		}else{
			   this.response(Constants.TX_FAIL_CODE, "票号为["+poolIn.getPlDraftNb()+"]的票"+"查证失败", res, ret);
		}
		return res;
	}
	
	protected String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}
	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}
	
}
