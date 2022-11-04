package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.CpesBranch;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.common.service.OnlineCommonService;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.DateUtils;

/**
 * @Description 通用结果通知   bbsp通知票据池
 * @author 
 * @version v1.0
 * @date 2021-05-08
 */
public class BBSP001RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(BBSP001RequestHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private OnlineCommonService onlineCommonService;
	@Autowired
	private DraftPoolOutService draftPoolOutService;
	@Autowired
	private	AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private	DraftPoolDiscountServer draftPoolDiscountServer;
	@Autowired
	private	DraftPoolInService draftPoolInService;
	@Autowired
	private AutoTaskExeService autoTaskExeService;
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private ConsignService consignService;
	@Autowired
	private AssetRegisterService assetRegisterService;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private PoolCreditProductService productService;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	
	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try {
	        Map map = request.getBody();
	        String transType = getStringVal(map.get("TRAN_TYPE"));//交易类型
			
	        if(PublicStaticDefineTab.BBSP001_200101.equals(transType)){
	        	
	        	//200001-新增票据
	        	response = this.txTransType200101(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_200201.equals(transType)){
	        	
	        	//200201-承兑申请
	        	response = this.txTransType200201(request);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_200202.equals(transType)){
	        	
	        	//200202-承兑签收  
	        	response = this.txTransType200202(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_201801.equals(transType)){
	        	
	        	//201801-质押申请  
	        	response = this.txTransType201801(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_201802.equals(transType)){
	        	
	        	//201802-质押签收 
	        	response = this.txTransType201802(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_201901.equals(transType)){
	        	
	        	//201901-解质押申请 
	        	response = this.txTransType201901(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_201902.equals(transType)){
	        	
	        	//201902-解质押签收 
	        	response = this.txTransType201902(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_201101.equals(transType)){
	        	
	        	//201101-贴现申请  
	        	response = this.txTransType201101(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_201102.equals(transType)){
	        	
	        	//201102-贴现签收
	        	response = this.txTransType201102(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_100140.equals(transType)){
	        	
	        	//100140-撤销出票
	        	response = this.txTransType100140(map);
	        	
	        }else if(PublicStaticDefineTab.BBSP001_200401.equals(transType)){
	        	
	        	//200401-未用退回 
	        	response = this.txTransType200401(map);
	      
	        }else if(PublicStaticDefineTab.BBSP001_202001.equals(transType)){
	        	
	        	/**
	             * 202101-提示付款清算成功  
	             * 
	             */
	        	response = this.txTransType202001(map);
	      
	        }else if(PublicStaticDefineTab.BBSP001_202101.equals(transType)){
	        	
	        	/**
	             * 202101-追索同意清偿  
	             * 
	             */
	        	response = this.txTransType202101(map);
	      
	        }else{
	        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("bbsp001通用通知未上送业务类型！");
		     	response.setRet(ret);
	        }

        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("通用结果通知异常,请联系票据池系统");
	     	response.setRet(ret);
		}
        
        return response;
    }
    
    /**
     * 200101-出票登记 
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午2:17:38
     */
    private ReturnMessageNew  txTransType200101(Map map) throws Exception {
    	logger.info("BBSP001通用通知接口，200101-出票登记："+map.get("OBG_TEXT"));
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String billNo = getStringVal(map.get("OBG_TEXT"));//票号
        String startBillNo = getStringVal(map.get("START_BILL_NO"));//子票起
        String endBillNo = getStringVal(map.get("END_BILL_NO"));//子票止
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        String dueDt = getStringVal(map.get("DEPOSIT_ACCT_NO"));//特别注意：这里用的业务保证金的字段，实际让电票系统传递过来的是到期日
        Date dueDate = DateUtils.StringToDate(dueDt,DateUtils.ORA_DATE_FORMAT);//到期日
        String tranId = getStringVal(map.get("TRAN_ID"));//交易id
        String holdBillId = getStringVal(map.get("HOLD_BILL_ID"));//持票id
        
    	// 200101-出票登记
    	OnlineQueryBean bean = new OnlineQueryBean();
    	bean.setBillId(billId);
    	List list = pedOnlineAcptService.queryOnlinAcptDetails(bean);
    	PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(0);

    	if(null == detail){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

    	if(StringUtils.isNotBlank(billNo)){
    		detail.setBillNo(billNo);
    		if(!detail.getDraftSource().equals(PoolComm.CS01)){
    			detail.setBeginRangeNo(startBillNo);
    			detail.setEndRangeNo(endBillNo);
    		}
    		detail.setDueDate(dueDate);
    	}
    	detail.setTaskDate(new Date());
    	if("2".equals(transResult)){ //处理成功
    		try {
    			logger.info("票号"+detail.getBillNo()+"出票登记成功！");
    			detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005_1);//出票登记成功
    			detail.setTransId(tranId);
    			detail.setHilrId(holdBillId);
    			
    			pedOnlineAcptService.txStore(detail);
    			/*
    			 * 唤醒承兑申请
    			 */
    			autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_ACPT_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_ACPT, null);
    			logger.info("票号"+detail.getBillNo()+"出票登记成功，唤醒承兑申请！");
			} catch (Exception e) {
				logger.error("出票登记成功处理异常", e);
				pedOnlineAcptService.txStore(detail);
				AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_ACPT_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT, detail.getId());
	        	autoTaskExe.setStatus("4");//异常
	        	autoTaskExeService.txStore(autoTaskExe);
			}
    	
    	}else if("3".equals(transResult) || "4".equals(transResult) ){//失败、拒绝
    		logger.info("票号"+detail.getBillNo()+"出票登记失败！");
    		//记录日志
    		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
			onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillSerialNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "出票登记失败", "BBSP001", PublicStaticDefineTab.ACPT_BUSI_NAME_06, "send");
    		try{
    			//登记失败驱动电票删除新增信息 
    			detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
    			detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_005_2);
//    			batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
//				pedOnlineAcptService.txStore(batch);
    			//同步批次状态
//    	    	pedOnlineAcptService.txSyncAcptBatchStatus(batch);
    	    	//驱动电票删除信息
    			pedOnlineAcptService.txApplyDeleteBill(detail);
    			logger.info("票号"+detail.getBillNo()+"登记失败驱动电票删除新增信息 ！");
    			/*
    			 * 结束任务
    			 */
        		autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());
        		logger.info("序列号"+detail.getBillSerialNo()+"出票登记失败，结束任务！");
        		/*
        		 * 发布额度释放
        		 */
        		Map<String, String> reqParam =new HashMap<String,String>();
    			reqParam.put("busiId",detail.getId());
    			reqParam.put("type", "2");//明细类型
    			reqParam.put("isCredit", "1");//信贷释放额度
    			reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
    			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam, detail.getLoanNo(), detail.getBpsNo(), null, null);
    			logger.info("序列号"+detail.getBillSerialNo()+"出票登记失败，发布额度释放！");
    		}catch (Exception e) {
    			pedOnlineAcptService.txStore(detail);
				logger.error("序列号"+detail.getBillSerialNo()+"出票登记失败，删除异常！", e);
				/*
				 * 结束任务
				 */
				autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());
				logger.info("序列号"+detail.getBillSerialNo()+"出票登记失败，结束任务！");
				/*
				 * 发布额度释放
				 */
				Map<String, String> reqParam =new HashMap<String,String>();
				reqParam.put("busiId",detail.getId());
				reqParam.put("type", "2");//明细类型
				reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
    			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam, detail.getLoanNo(), detail.getBpsNo(), null, null);
    			logger.info("序列号"+detail.getBillSerialNo()+"出票登记失败，发布额度释放！");
    			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
    	     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
    	     	response.setRet(ret);
    			return response;
			}
    		
    		
    		
    		//出票登记失败(相当于未用退回)唤醒银承业务明细状态及发生未用退回时金额统计
			logger.info("出票登记失败唤醒银承业务明细状态及发生未用退回时金额统计");
			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

		    Map<String,String> reqParams = new HashMap<String,String>();
		    reqParams.put("acptBatchId", batch.getId());
		    reqParams.put("acptId", detail.getId());
		    reqParams.put("source", "2");  //出票登记失败(相当于未用退回)
			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);

    	
    	}else{//未处理
    		//变更为继续等待
    		AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT, detail.getId());
        	autoTaskExe.setStatus("4");//异常
        	autoTaskExeService.txStore(autoTaskExe);
    	}
    	pedOnlineAcptService.txStore(detail);
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    }
    
    /**
     * 200201-承兑申请	
     * @param request
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午2:18:38
     */
    private ReturnMessageNew  txTransType200201(ReturnMessageNew request) throws Exception{
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        Map map = request.getBody();
        logger.info("BBSP001通用通知接口，200201-承兑申请："+map.get("BILL_ID"));
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        String transId = getStringVal(map.get("TRAN_ID"));//交易id
        String transType = getStringVal(map.get("NOTICE_TYPE"));//通知类型    1：申请交易id  2：签收交易id
//        String hldrId = getStringVal(map.get("HOLD_BILL_ID"));//持票id

    	OnlineQueryBean bean = new OnlineQueryBean();
    	bean.setBillId(billId);
    	List list = pedOnlineAcptService.queryOnlinAcptDetails(bean);
    	PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(0);

		if(null == detail){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

    	detail.setTaskDate(new Date());
    	
    	if(StringUtils.isNotBlank(transType) && transType.equals("1")){
    		detail.setSendTransId(transId);
    	}
    	if((detail.getDraftSource().equals(PoolComm.CS01) && transResult.equals("2")) || (StringUtils.isNotBlank(transType) && transType.equals("2"))){//申请成功
    		logger.info("票号"+detail.getBillNo()+"提示承兑申请成功！");
			detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006_1);
			detail.setTransId(transId);
//			detail.setHilrId(hldrId);
    		/*
    		 * 唤醒承兑申请签收
    		 */
    		autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_SIGN_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_SIGN, null);
    		logger.info("票号"+detail.getBillNo()+"唤醒承兑申请签收成功，唤醒承兑申请签收任务！");
    	}else if(transResult.equals("3")){//失败
    		detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
			detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_006_2);
    		//记录日志
			PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
//			batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
			pedOnlineAcptService.txStore(batch);
			onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, request.getRet().getRET_MSG(), "BBSP001", PublicStaticDefineTab.ACPT_BUSI_NAME_07, "send");
    		/*
    		 * 结束任务
    		 */
    		autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ACPT_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
    		logger.info("票号"+detail.getBillNo()+"提示承兑申请失败，结束任务！");
    		/*
    		 * 发布未用退回
    		 */
			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_01, null, detail.getBillNo(), detail.getBpsNo(), null, null);
			logger.info("票号"+detail.getBillNo()+"提示承兑申请失败，发布未用退回！");
    	}
    	pedOnlineAcptService.txStore(detail);
    	//同步批次状态
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
    	pedOnlineAcptService.txSyncAcptBatchStatus(batch);
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    }
    /**
     * 200202-承兑签收
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午2:19:50
     */
    private ReturnMessageNew  txTransType200202(Map map) throws Exception{
    	logger.info("BBSP001通用通知接口，200202-承兑签收："+map.get("OBG_TEXT"));
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        String acctResult = getStringVal(map.get("ACCOUNT_STATUS"));//记账结果 0:未记账	1：记账成功	2：记账失败
        String depositAcctNo = getStringVal(map.get("DEPOSIT_ACCT_NO"));//业务保证金账号
        
        String tranId = getStringVal(map.get("TRAN_ID"));//交易id
//        String holdBillId = getStringVal(map.get("HOLD_BILL_ID"));//持票id
        
    	OnlineQueryBean bean = new OnlineQueryBean();
    	bean.setBillId(billId);
    	List list = pedOnlineAcptService.queryOnlinAcptDetails(bean);
    	PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(0);

		if(null == detail){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

    	detail.setTaskDate(new Date());
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);

		try {
			if("2".equals(transResult) && "1".equals(acctResult)){//记账成功+签收成功
			logger.info("票号"+detail.getBillNo()+"承兑签收成功！");
			detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_1);
			detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_003);
			detail.setTransAccount(depositAcctNo);
			
			detail.setTransId(tranId);
//			detail.setHilrId(holdBillId);
			
			//自动提示收票  
			if("1".equals(detail.getIsAutoCallPyee())){
				//发布提示收票
				autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_SEND_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_SEND, null, detail.getBillNo(), detail.getBpsNo(), null, null);
				logger.info("票号"+detail.getBillNo()+"发布提示收票任务！");
			}
			pedOnlineAcptService.txStore(detail);
			/**
			 * 100%保证金数据也生成借据信息   -2022-09-16 WuFengjun
			 */
//			if(new BigDecimal(100).compareTo(batch.getDepositRatio())!=0){
				onlineCommonService.txSavePedCreditDetail(PublicStaticDefineTab.PRODUCT_001,null,detail);
				onlineCommonService.txSaveCreditProduct(PublicStaticDefineTab.PRODUCT_001,batch,detail);
//			}
			
			/*//同步批次状态
			pedOnlineAcptService.txSyncAcptBatchStatus(batch);
			
			 * 若该批次下全部票据都出成功，则置换额度信息
			 
			logger.info("======================在线银承为何没存主业务合同跟踪日志==========================批次状态："+batch.getStatus());
			if(PublicStaticDefineTab.ACPT_BATCH_005.equals(batch.getStatus())){
    			if(new BigDecimal(100).compareTo(batch.getDepositRatio())!=0){//100%保证金不校验额度
    				logger.info("======================在线银承为何没存主业务合同跟踪日志==========================非百分百保证金");
					//该合同下的全部借据
					CreditQueryBean queryBean = new CreditQueryBean();
					queryBean.setCrdtNo(batch.getContractNo());
					List<PedCreditDetail> crdtDetailList =  poolCreditProductService.queryCreditDetailList(queryBean) ;
					// 将原占用detail的额度信息，置换为占用PedCreditDetail
					financialService.txOnlineBusiCreditChange(batch.getContractNo(), crdtDetailList, batch.getBpsNo());
				}
    			//短信通知
				List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
				for(PedOnlineMsgInfo msgInfo:msgList){
					onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), true,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
				}
			 }*/
			
			
			//签收记账成功唤醒银承业务明细状态及发生未用退回时金额统计
			logger.info("签收记账成功唤醒银承业务明细状态及发生未用退回时金额统计");
			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

		    Map<String,String> reqParams = new HashMap<String,String>();
		    reqParams.put("acptBatchId", batch.getId());
		    reqParams.put("acptId", detail.getId());
		    reqParams.put("source", "1");  //签收成功
			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);
			
			
			
			}else if("1".equals(acctResult) && ("1".equals(transResult) || "3".equals(transResult))){//记账成功+未签收或签收失败
				logger.info("票号"+detail.getBillNo()+"承兑签收记账成功，签收失败！");
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_2);
				detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_001);
				pedOnlineAcptService.txStore(detail);
				//驱动承兑签收 继续签收
				pedOnlineAcptService.txFailExe(AutoTaskNoDefine.POOL_ONLINE_SIGN_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT, detail.getId());

				
			}else if("2".equals(acctResult)){//记账失败
				logger.info("票号"+detail.getBillNo()+"承兑签收记账失败！");

				pedOnlineAcptService.txFailExe(AutoTaskNoDefine.POOL_ONLINE_SIGN_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT, detail.getId());
				
				/**
				 * 记账失败;不知道核心的具体记账状态,直接调用承兑签收的重复执行方法
				 * 先执行核心查账接口;根据记账状态执行后续的流程
				 */
				//pedOnlineAcptService.txRepeatAcceptionSign(detail, batch);
				

			}
		} catch (Exception e) {
			pedOnlineAcptService.txStore(detail);
			logger.error("票号"+detail.getBillNo()+"提示承兑签收处理异常！", e);
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
	     	ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
	     	response.setRet(ret);
			return response;
		}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    
    }
   

	/**
     * 201801-质押申请
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午2:20:40
     */
    private ReturnMessageNew  txTransType201801(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，201801-质押申请："+map.get("BILL_ID"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止
        String transId = getStringVal(map.get("TRAN_ID"));//交易id
        String transType = getStringVal(map.get("NOTICE_TYPE"));//通知类型    1：申请交易id  2：签收交易id
        
        if(StringUtils.isEmpty(billRangeStart)){
        	billRangeStart = "0";
        	billRangeEnd = "0";
        }
        logger.info("票据区间:"+billRangeStart+"-"+billRangeEnd);
		PoolBillInfo info = draftPoolOutService.loadByBillDiscID(billId,billRangeStart,billRangeEnd);

		if(null == info){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}
		
    	DraftPoolIn poolIn=draftPoolInService.getDraftPoolInByDraftNb(info.getSBillNo(),PoolComm.RC_01,billRangeStart,billRangeEnd);

		if(null == poolIn){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

		poolIn.setTaskDate(new Date());
    	if((info.getDraftSource().equals(PoolComm.CS01) && transResult.equals("2")) || (StringUtils.isNotBlank(transType) && transType.equals("2"))){
    		poolIn.setPlStatus(PoolComm.RC_02);//质押待签收
    		poolIn.setTransId(transId);//交易id
        	draftPoolInService.txStore(poolIn);
            /**
             * 唤醒质押签收子任务。。。
             */
    		Map<String, String> reqParams =new HashMap<String,String>();

        	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLIN_SIGN_TASK_NO, poolIn.getId(), AutoTaskNoDefine.BUSI_TYPE_QS,reqParams);
    	}else if(transResult.equals("3")){//申请失败
    		/**
             * 将质押申请子任务标记异常重新发送
             * 质押申请失败 异常页面手工执行质押出池申请任务
             */
        	poolIn.setPlStatus(PoolComm.RC_00);//新建数据
        	draftPoolInService.txStore(poolIn);
        	AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLIN_TASK_NO, AutoTaskNoDefine.POOL_AUTO_POOLIN, poolIn.getId());
        	autoTaskExe.setStatus("4");//异常
        	draftPoolInService.txStore(autoTaskExe);
    	}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    
    }
    /**
     * 201802质押签收
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:09:05
     */
    private ReturnMessageNew  txTransType201802(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，201802质押签收："+map.get("BILL_ID"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止
        String hldrId = getStringVal(map.get("HOLD_BILL_ID"));//持票id
        String tranId = getStringVal(map.get("TRAN_ID"));//交易id
        if(StringUtils.isEmpty(billRangeStart)){
        	billRangeStart = "0";
        	billRangeEnd = "0";
        }
        
		PoolBillInfo info = draftPoolOutService.loadByBillDiscID(billId,billRangeStart,billRangeEnd);

		if(null == info){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}
		
		System.out.println("质押签收通知时，票号："+info.getSBillNo()+"，持票id为："+hldrId);
		//201802-质押签收
		DraftPoolIn poolIn=draftPoolInService.getDraftPoolInByDraftNb(info.getSBillNo(),billRangeStart,billRangeEnd);

		if(null == poolIn){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

		poolIn.setTaskDate(new Date());
    	if(transResult.equals("2")){
			poolIn.setPlStatus(PoolComm.RC_04);//变更状态为：质押申请已签收
			poolIn.setHilrId(hldrId);
			poolIn.setTransId(tranId);
			
			info.setHilrId(hldrId);
			
			draftPoolInService.txStore(info);
			draftPoolInService.txStore(poolIn);
            /**
             * 唤醒入池记账子任务。。。
             */
			Map<String, String> reqParams =new HashMap<String,String>();

        	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLIN_EDU_TASK_NO, poolIn.getId(), AutoTaskNoDefine.BUSI_TYPE_ED,reqParams);
    	}else if(transResult.equals("3")){
    		/**
    		 * 子任务回退
    		 */
			poolIn.setPlStatus(PoolComm.RC_02);//状态变更为质押待签收重新发送质押签收
			draftPoolInService.txStore(poolIn);
			AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLIN_SIGN_TASK_NO, AutoTaskNoDefine.POOL_AUTO_POOLIN, poolIn.getId());
        	autoTaskExe.setStatus("4");//异常
        	draftPoolInService.txStore(autoTaskExe);
    	}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;

    }
    /**
     * 201901-解质押申请
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:10:17
     */
    private ReturnMessageNew  txTransType201901(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，201901-解质押申请："+map.get("BILL_ID"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止
        String transId = getStringVal(map.get("TRAN_ID"));//交易id
        String transType = getStringVal(map.get("NOTICE_TYPE"));//通知类型    1：申请交易id  2：签收交易id
        String hldrId = getStringVal(map.get("HOLD_BILL_ID"));//持票id
        
        if(StringUtils.isEmpty(billRangeStart)){
        	billRangeStart = "0";
        	billRangeEnd = "0";
        }
		PoolBillInfo info = draftPoolOutService.loadByBillDiscID(billId,billRangeStart,billRangeEnd);

		if(null == info){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}
		
		System.out.println("解质押申请通知时，票号："+info.getSBillNo()+"，持票id为："+hldrId);
		//201901-解质押申请
    	PoolQueryBean poolQuery = new PoolQueryBean();
    	poolQuery.setBillNo(info.getSBillNo());
    	poolQuery.setSStatusFlag(PoolComm.CC_02);
    	
    	poolQuery.setBeginRangeNo(info.getBeginRangeNo());
    	poolQuery.setEndRangeNo(info.getEndRangeNo());
    	
    	DraftPoolOut draftPoolOut=draftPoolOutService.getDraftPoolOutBybean(poolQuery);

		if(null == draftPoolOut){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

    	draftPoolOut.setTaskDate(new Date());
    	if((info.getDraftSource().equals(PoolComm.CS01) && transResult.equals("2")) || (StringUtils.isNotBlank(transType) && transType.equals("2"))){
        	draftPoolOut.setPlStatus(PoolComm.CC_03);//解质押待签收
        	draftPoolOut.setTransId(transId);
        	draftPoolOut.setHilrId(hldrId);
        	draftPoolOutService.txStore(draftPoolOut);

            /**
             * 唤醒解质押签收子任务。。。
             */
    		Map<String, String> reqParams =new HashMap<String,String>();

        	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLOUT_SIGN_TASK_NO, draftPoolOut.getId(), AutoTaskNoDefine.BUSI_TYPE_JQS,reqParams);
    	}else if(transResult.equals("3")){
    		/**
    		 * 子任务回退
    		 */
    		draftPoolOut.setPlStatus(PoolComm.CC_01);//新建数据	重新发送质押申请
        	draftPoolOutService.txStore(draftPoolOut);
        	AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLOUT_SEND_TASK_NO, AutoTaskNoDefine.POOL_AUTO_POOLOUT, draftPoolOut.getId());
        	autoTaskExe.setStatus("4");//异常
        	draftPoolInService.txStore(autoTaskExe);
    	}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    
    }
    /**
     * 201902-解质押签收
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:11:22
     */
    private ReturnMessageNew  txTransType201902(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，201902-解质押签收："+map.get("BILL_ID"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止
        
        String hilrId = getStringVal(map.get("HOLD_BILL_ID"));//持票id
        
        if(StringUtils.isEmpty(billRangeStart)){
        	billRangeStart = "0";
        	billRangeEnd = "0";
        }
        PoolBillInfo info = draftPoolOutService.loadByBillDiscID(billId,billRangeStart,billRangeEnd);
        PoolQueryBean bean = new PoolQueryBean();
		bean.setBillNo(info.getSBillNo());
		
		/********************融合改造新增 start******************************/
		bean.setBeginRangeNo(info.getBeginRangeNo());
		bean.setEndRangeNo(info.getEndRangeNo());
		/********************融合改造新增 end******************************/
		
		DraftPool pool=consignService.queryDraftByBean(bean).get(0);

		if(null == info){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

        System.out.println("解质押申请通知时，票号："+info.getSBillNo()+"，持票id为："+hilrId);
        
    	PoolQueryBean poolQuery = new PoolQueryBean();
    	poolQuery.setBillNo(info.getSBillNo());

    	poolQuery.setBeginRangeNo(info.getBeginRangeNo());
    	poolQuery.setEndRangeNo(info.getEndRangeNo());
    	
    	DraftPoolOut draftPoolOut=draftPoolOutService.getDraftPoolOutBybean(poolQuery);

		if(null == draftPoolOut){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

    	draftPoolOut.setTaskDate(new Date());
    	draftPoolOut.setHilrId(hilrId);
    	
    	info.setHilrId(hilrId);
    	pool.setHilrId(hilrId);
    	
    	draftPoolInService.txStore(pool);
    	draftPoolInService.txStore(info);
    	
    	response = draftPoolOutService.txTransTypePoolOut(draftPoolOut, transResult, info);
    	
		return response;
    
    }
    /**
     * 201101-贴现申请
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:12:00
     */
    private ReturnMessageNew  txTransType201101(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，201101-贴现申请："+map.get("BILL_ID"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止
        String transId = getStringVal(map.get("TRAN_ID"));//交易id
        String transType = getStringVal(map.get("NOTICE_TYPE"));//通知类型    1：申请交易id  2：签收交易id
        String hilrId = getStringVal(map.get("HOLD_BILL_ID"));//持票id

        if(StringUtils.isEmpty(billRangeStart)){
        	billRangeStart = "0";
        	billRangeEnd = "0";
        }
        PoolBillInfo info = draftPoolOutService.loadByBillDiscID(billId,billRangeStart,billRangeEnd);

		if(null == info){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}
		
        System.out.println("票号："+info.getSBillNo()+"票号起："+info.getBeginRangeNo()+"票号止："+info.getEndRangeNo()+"  " +PoolComm.TX_01);
        DraftQueryBean dqueryBean = new DraftQueryBean();
		dqueryBean.setAssetStatus(PoolComm.TX_01);
		dqueryBean.setAssetNb(info.getSBillNo());
		dqueryBean.setBeginRangeNo(info.getBeginRangeNo());
		dqueryBean.setEndRangeNo(info.getEndRangeNo());
		
		List list=draftPoolDiscountServer.getDiscountsListByParamView(dqueryBean,null,null);
		System.out.println(list);
		PlDiscount discount=(PlDiscount)list.get(0);

		if(null == discount){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

		discount.setTaskDate(new Date());
    	if((info.getDraftSource().equals(PoolComm.CS01) && transResult.equals("2")) || (StringUtils.isNotBlank(transType) && transType.equals("2"))){//贴现申请成功
    		discount.setSBillStatus(PoolComm.TX_02);//贴现申请已成功
    		discount.setTransId(transId);
    		discount.setHilrId(hilrId);
			discount.setLastOperTm(new Date());
			discount.setLastOperName("强贴自动任务,贴现申请已成功");
			draftPoolDiscountServer.txStore(discount);

			/**
			 *   唤醒贴现签收记账申请子任务。。。
			 */
			Map<String, String> reqParams =new HashMap<String,String>();

        	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLDIS_SIGN_TASK_NO, discount.getId(), AutoTaskNoDefine.BUSI_TYPE_TJZ,reqParams);

    	}else if(transResult.equals("3")){//贴现申请失败
    		discount.setSBillStatus(PoolComm.TX_06);//贴现失败
			discount.setLastOperTm(new Date());
			discount.setLastOperName("强贴自动任务,申请处理失败");
			draftPoolDiscountServer.txStore(discount);
			/**
			 * 任务回退
			 */
			AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLDIS_SEND_TASK_NO, AutoTaskNoDefine.POOL_AUTO_POOLDIS, discount.getId());
        	autoTaskExe.setStatus("4");
        	draftPoolDiscountServer.txStore(autoTaskExe);
    	}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    
    
    }
    /**
     * 201102-贴现签收及记账通知
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:13:08
     */
    private ReturnMessageNew  txTransType201102(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，201102-贴现签收及记账通知："+map.get("BILL_ID"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        String acctResult = getStringVal(map.get("ACCOUNT_STATUS"));//记账结果 0:未记账	1：记账成功	2：记账失败
        
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止

        if(StringUtils.isEmpty(billRangeStart)){
        	billRangeStart = "0";
        	billRangeEnd = "0";
        }
        
        PoolBillInfo info = draftPoolOutService.loadByBillDiscID(billId,billRangeStart,billRangeEnd);

		if(null == info){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

		DraftQueryBean dqueryBean = new DraftQueryBean();
		dqueryBean.setAssetStatus(PoolComm.TX_04);
		dqueryBean.setAssetNb(info.getSBillNo());
		dqueryBean.setBeginRangeNo(info.getBeginRangeNo());
		dqueryBean.setEndRangeNo(info.getEndRangeNo());
			
		List list=draftPoolDiscountServer.getDiscountsListByParamView(dqueryBean,null,null);
		if(null!=list && list.size()>0){
			PlDiscount discount=(PlDiscount)list.get(0);
			
			draftPoolDiscountServer.txTaskDiscountSynchroniza(discount, transResult, acctResult);
		}else {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
		 	ret.setRET_MSG(ErrorCode.ERR_MSG_998);
		 	response.setRet(ret);
			return response;
		}
		
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
	 	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
	 	response.setRet(ret);
		return response;
    	
    
    }
    /**
     * //100140-未用退回
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:13:51
     */
    private ReturnMessageNew  txTransType200401(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，100140-未用退回："+map.get("BILL_ID"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止

    	OnlineQueryBean bean = new OnlineQueryBean();
    	bean.setBillId(billId);
    	List list = pedOnlineAcptService.queryOnlinAcptDetails(bean);
    	PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(0);

		if(null == detail){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

    	detail.setTaskDate(new Date());
    	PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);

		if(null == batch){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}
    	
    	if("2".equals(transResult)){
    		//变更状态
    		detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010_1);
    		detail.setCancelDate(new Date());
            //该笔借据置为“成功”，但是后续客户自行发起了未用退回，该借据任务状态为“已撤销”。“已撤销”属于“成功”类数据。后续合同状态判断按“成功”识别。
    		if(PublicStaticDefineTab.ONLINE_DS_003.equals(detail.getDealStatus())){
    			detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_004);
    		}
    		pedOnlineAcptService.txStore(detail);
    		Map<String, String> reqParam =new HashMap<String,String>();
    		reqParam.put("busiId",detail.getId());
    		reqParam.put("type", "2");//明细类型
    		reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
    		
    		
    		//未用退回成功唤醒银承业务明细状态及发生未用退回时金额统计
			logger.info("未用退回成功唤醒银承业务明细状态及发生未用退回时金额统计");
			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

		    Map<String,String> reqParams = new HashMap<String,String>();
		    reqParams.put("acptBatchId", batch.getId());
		    reqParams.put("acptId", detail.getId());
		    
		    
		    
    		//考虑次日未用退回，不用通知信贷释放额度， 查询信贷额度占用流水，判断时间
    		if(DateUtils.getCurrDate().compareTo(DateUtils.formatDate(detail.getCreateTime(),DateUtils.ORA_DATES_FORMAT))!=0){//隔日未用退回
				reqParam.put("isCredit", "0");//是否通知信贷
        		autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam,detail.getBillNo(), detail.getBpsNo(), null, null);
    		}else{
//    			PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
//    			batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
//    			pedOnlineAcptService.txStore(batch);
    			//本次未用退回成功  可能是撤票或者未用退回队列或者客户主动退回
    			AutoTaskExe autoTaskExe1=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_CANCLE, detail.getId());
    			reqParam.put("isCredit", "1");//是否通知信贷
    			if(null != autoTaskExe1 && "N".equals(autoTaskExe1.getDelFlag())){//唤醒撤票队列
    				autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_03, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_03, null);
    			}else {
        			AutoTaskExe autoTaskExe2=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_UNUSED, detail.getId());
        			if(null != autoTaskExe2 && "N".equals(autoTaskExe2.getDelFlag())){//唤醒未用退回队列
        				autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_02, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_02, null);
        			}else{//客户主动退回
                		autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam,detail.getBillNo(), detail.getBpsNo(), null, null);
        			}
    			}
    			
    			reqParams.put("source", "2");  //未用退回成功
    		}
    		
			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);
			
			
			
    		
    	}else{
    		logger.info("票号"+detail.getBillNo()+"未用退回失败！");
    		detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010_2);
    		pedOnlineAcptService.txStore(detail);
    		//记录日志
			onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "电票未用退回失败", "30600122", PublicStaticDefineTab.ACPT_BUSI_NAME_08, "receive");
			//变成可以再次执行
			//可能是撤票队列  也可能是成功后的未用退回队列
			AutoTaskExe autoTaskExe1=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_CANCLE, detail.getId());
			if(null != autoTaskExe1 && "N".equals(autoTaskExe1.getDelFlag())){
				autoTaskExe1.setStatus("4");//异常
				draftPoolInService.txStore(autoTaskExe1);
			}else{
				AutoTaskExe autoTaskExe2=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_UNUSED, detail.getId());
				if(null != autoTaskExe2 && "N".equals(autoTaskExe2.getDelFlag())){
					autoTaskExe2.setStatus("4");//异常
					draftPoolInService.txStore(autoTaskExe2);
				}
			}
    	}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    
    }
    /**
     * 200401-统一撤销
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:14:53
     */
    private ReturnMessageNew  txTransType100140(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，200401-统一撤销："+map.get("BILL_ID"));
    	
    	logger.info("收到统一撤销申请回复通知！");
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        //
        OnlineQueryBean bean = new OnlineQueryBean();
        bean.setBillId(billId);
        List list = pedOnlineAcptService.queryOnlinAcptDetails(bean);
        PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(0);

		if(null == detail){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询结果为空");
			response.setRet(ret);
			return response;
		}

        detail.setTaskDate(new Date());
        
        try {
        	if("2".equals(transResult)){
        		//变更状态
        		detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_1);
        		detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_003);
        		/**
        		 * 唤醒未用退回
        		 */
        		autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_02, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_02, null);
        		logger.info("统一撤销申请成功，唤醒未用退回任务！");
        	}else{
        		logger.info("票据id"+billId+"统一撤销申请失败！");
        		//记录日志
        		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
        		onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "统一撤销电票失败", "30600122", PublicStaticDefineTab.ACPT_BUSI_NAME_08, "receive");
        		//变更状态
        		detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_008);
        		/**
        		 * 继续执行撤销或者页面可以手动执行
        		 */
        		AutoTaskExe autoTaskExe2=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_CANCLE, detail.getId());
        		autoTaskExe2.setStatus("4");//异常
        		draftPoolInService.txStore(autoTaskExe2);
        	}
		} catch (Exception e) {
			logger.error("票据id"+billId+"收到撤销出票通用结果通知，处理失败", e);
			pedOnlineAcptService.txStore(detail);
			/**
    		 * 继续执行撤销或者页面可以手动执行
    		 */
    		AutoTaskExe autoTaskExe2=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_CANCLE, detail.getId());
    		autoTaskExe2.setStatus("4");//异常
    		draftPoolInService.txStore(autoTaskExe2);
		}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
     	response.setRet(ret);
		return response;
    
    }
    
    /**
     * 202001-提示付款通知
     * @param map
     * @return
     * @author Ju Nana
     * @throws Exception 
     * @date 2021-7-7下午3:14:53
     */
    private ReturnMessageNew  txTransType202001(Map map) throws Exception{
    	
    	logger.info("BBSP001通用通知接口，202001-提示付款通知：票号："+getStringVal(map.get("OBG_TEXT")));

    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billNo = getStringVal(map.get("OBG_TEXT"));//票号
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止
        String transId = getStringVal(map.get("TRAN_ID"));//交易id
        String hilrId = getStringVal(map.get("HOLD_BILL_ID"));//持票id
        String transType = getStringVal(map.get("NOTICE_TYPE"));//线上线下标识    1：线上  2：线下
        
        try {
        	if("2".equals(transResult)){//清算成功
        		/**
        		 * 查询票据是否为票据池质押的到期数据
        		 */
    		  if(StringUtils.isEmpty(billRangeStart)){
    	        	billRangeStart = "0";
    	        	billRangeEnd = "0";
    	        }
        		CollectionSendDto sendDto = consignService.queryCollectionSendByStatus(billNo, billRangeStart, billRangeEnd);
        		
        		if(sendDto != null){
        			PoolBillInfo info = sendDto.getPoolBillInfo();
        			//不为空是 票据池的票
        			ProtocolQueryBean queryBean = new ProtocolQueryBean();
					queryBean.setTransBrchBankNo(sendDto.getAcceptAcctSvcr());//承兑行行号
					
					List banks = blackListManageService.queryCpesBranch(queryBean, null, null);
					if(banks != null && banks.size() > 0){
						CpesBranch branch = (CpesBranch) banks.get(0);
						if(branch.getMemberId().equals("100023")){
							//我行承兑
							sendDto.setBankFlag("1");//是否我行承兑标志 1是  0否
						}else{
							sendDto.setBankFlag("0");//是否我行承兑标志 1是  0否
						}
					}
					sendDto.setClearWay(transType);//线上线下清算标志		1线上 0线下
        			sendDto.setTransId(transId);
        			sendDto.setHilrId(hilrId);
        			sendDto.setSBillStatus(PoolComm.TS03);
        			sendDto.setLastOperTm(DateUtils.getWorkDayDate());
        			sendDto.setLastOperName("托收自动任务:托收已签收");
        			
        			info.setHilrId(hilrId);
        			info.setSDealStatus(PoolComm.DS_07);
        			consignService.txStore(sendDto);
					consignService.txStore(info);
        			
        		}
        	}
		} catch (Exception e) {
			logger.error("票据票号："+getStringVal(map.get("OBG_TEXT"))+"收到提示付款结果通知，处理失败", e);
			
		}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
     	response.setRet(ret);
		return response;
    
    }
    
    /**
     * 202101-追索同意清偿  
     * 
     */
    private ReturnMessageNew  txTransType202101(Map map) throws Exception{
    	logger.info("BBSP001通用通知接口，202101-追索同意清偿："+map.get("OBG_TEXT"));
    	
    	ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        
        String billNo = getStringVal(map.get("OBG_TEXT"));//票号
        String billId = getStringVal(map.get("BILL_ID"));//票据id
        String transResult = getStringVal(map.get("TRAN_STATUS"));//交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
        
        String billRangeStart =  getStringVal(map.get("START_BILL_NO"));//子票区间起始
        String billRangeEnd =  getStringVal(map.get("END_BILL_NO"));//子票区间截止
        String transId = getStringVal(map.get("TRAN_ID"));//交易id
        String transType = getStringVal(map.get("NOTICE_TYPE"));//线上线下标识    1：线上  2：线下
        String hilrId = getStringVal(map.get("HOLD_BILL_ID"));//持票id
        
        if(StringUtils.isEmpty(billRangeStart)){
        	billRangeStart = "0";
        	billRangeEnd = "0";
        }
        
        if(transResult.equals("2")){//清偿成功
        	
        	List list = new ArrayList();//拆分释放信贷额度列表
        	
        	 /**
             * 通过通知的子票区间查询原票据
             */
            CollectionSendDto sendDto = consignService.queryCollectionSend(billNo, billRangeStart, billRangeEnd);
           
            if(null == sendDto){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("查询结果为空");
				response.setRet(ret);
				return response;
			}
            
            //不为空是 票据池的票
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setTransBrchBankNo(sendDto.getAcceptAcctSvcr());//承兑行行号
			
			List banks = blackListManageService.queryCpesBranch(queryBean, null, null);
			if(banks != null && banks.size() > 0){
				CpesBranch branch = (CpesBranch) banks.get(0);
				if(branch.getMemberId().equals("100023")){
					//我行承兑
					sendDto.setBankFlag("1");//是否我行承兑标志 1是  0否
				}else{
					sendDto.setBankFlag("0");//是否我行承兑标志 1是  0否
				}
			}

			sendDto.setClearWay(transType);//清算方式
            
            //原发托收时的资产表对象
			PoolQueryBean bean = new PoolQueryBean();
			bean.setBillNo(sendDto.getSBillNo());
			
			/********************融合改造新增 start******************************/
			bean.setBeginRangeNo(sendDto.getBeginRangeNo());
			bean.setEndRangeNo(sendDto.getEndRangeNo());
			/********************融合改造新增 end******************************/
			
			DraftPool pool=consignService.queryDraftByBean(bean).get(0);

			if(null == pool){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("查询结果为空");
				response.setRet(ret);
				return response;
			}
    		
    		//原发托收时的票据基本信息表对象
    		PoolBillInfo info = pool.getPoolBillInfo();

			if(null == info){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("查询结果为空");
				response.setRet(ret);
				return response;
			}
    		
    		/**
    		 * 查询资产登记表(额度表)
    		 */
    		AssetRegister assetRegister = assetRegisterService.getAssetRegisterByCustSignNoAndAssetNo(pool.getPoolAgreement(), pool.getAssetNb()+"-"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo());
    		
    		
    		
    		
    		
    		/**
    		 * 1、若原始子票区间起小于追索通知的子票起始区间则拆分一条追索未清偿的小票
    		 * 2、若原始子票区间起大于追索通知的子票起始区间则拆分一条追索未清偿的小票
    		 * 3、若原始子票区间起大于追索通知的子票起始区间则拆分一条追索未清偿的小票
    		 * 一次通知最多拆成3张小票
    		 */
    		CollectionSendDto sendDtoOut = new CollectionSendDto();
    		CollectionSendDto sendDtoIn = new CollectionSendDto();
    		CollectionSendDto sendDtoIn1 = new CollectionSendDto();
    		CollectionSendDto sendDtoIn2 = new CollectionSendDto();
    		boolean falg1 = false;//是否拆分小票
    		
    		if(BigDecimalUtils.valueOf(sendDto.getBeginRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeStart)) == 0 
    				&& BigDecimalUtils.valueOf(sendDto.getEndRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeEnd)) > 0) {
    			/**
    			 * 拆分的两张票号为：①通知的起票号-通知的止票号  ②通知的止票号+1  - 原始的止票号
    			 */
    			
    			falg1 = true;
    			
    			DraftPool poolOut = new DraftPool();
    			PoolBillInfo infoOut = new PoolBillInfo();
    			BigDecimal billAmtOut = DraftRangeHandler.formatBillNos(billRangeStart, billRangeEnd);//票据金额
    			
    			//大票表小票信息
    			BeanUtil.copyValue(info, infoOut);
    			infoOut.setBillinfoId(null);
    			infoOut.setFBillAmount(billAmtOut);
    			infoOut.setTradeAmt(billAmtOut);
    			infoOut.setBeginRangeNo(billRangeStart);
    			infoOut.setEndRangeNo(billRangeEnd);
    			infoOut.setSDealStatus(PoolComm.DS_07);
    			infoOut.setSplitId(info.getBillinfoId());
    			consignService.txSaveEntity(infoOut);
    			
    			//资产表小票信息
    			BeanUtil.copyValue(pool, poolOut);
    			poolOut.setId(null);
    			poolOut.setPoolBillInfo(infoOut);
    			poolOut.setAssetAmt(billAmtOut);
    			poolOut.setTradeAmt(billAmtOut);
    			poolOut.setBeginRangeNo(billRangeStart);
    			poolOut.setEndRangeNo(billRangeEnd);
    			poolOut.setSplitId(info.getBillinfoId());
    			consignService.txSaveEntity(poolOut);
    			
    			//托收表小票信息
    			BeanUtil.copyValue(sendDto, sendDtoOut);
    			sendDtoOut.setCollectionSendId(null);
    			sendDtoOut.setPoolBillInfo(infoOut);
    			sendDtoOut.setFBillAmount(billAmtOut);
    			sendDtoOut.setTradeAmt(billAmtOut);
    			sendDtoOut.setBeginRangeNo(billRangeStart);
    			sendDtoOut.setEndRangeNo(billRangeEnd);
    			
    			sendDtoOut.setClearWay(transType);//清算方式
				sendDtoOut.setSBillStatus(PoolComm.TS03);
				sendDtoOut.setLastOperTm(DateUtils.getWorkDayDate());
				sendDtoOut.setLastOperName("托收自动任务:托收已签收");
				sendDtoOut.setIsPursue("1");//追索清偿数据
    			consignService.txSaveEntity(sendDtoOut);
    			
    			
    			DraftPool poolIn = new DraftPool();
    			PoolBillInfo infoIn = new PoolBillInfo();
    			
    			int strBillNo = Integer.parseInt(billRangeEnd)+1;
    			String strBillNoStr = strBillNo+"";
    			BigDecimal billAmtIn = DraftRangeHandler.formatBillNos(strBillNoStr, info.getEndRangeNo());//票据金额
    			
    			//大票表小票信息
    			BeanUtil.copyValue(info, infoIn);
    			infoIn.setBillinfoId(null);
    			infoIn.setFBillAmount(billAmtIn);
    			infoIn.setTradeAmt(billAmtIn);
    			infoIn.setBeginRangeNo(strBillNoStr);
    			infoIn.setEndRangeNo(info.getEndRangeNo());
    			infoIn.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(infoIn);
    			
    			//资产表小票信息
    			BeanUtil.copyValue(pool, poolIn);
    			poolIn.setId(null);
    			poolIn.setPoolBillInfo(infoIn);
    			poolIn.setAssetAmt(billAmtIn);
    			poolIn.setTradeAmt(billAmtIn);
    			poolIn.setBeginRangeNo(strBillNoStr);
    			poolIn.setEndRangeNo(info.getEndRangeNo());
    			poolIn.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(poolIn);
    			
    			//托收表小票信息
    			BeanUtil.copyValue(sendDto, sendDtoIn);
    			sendDtoIn.setCollectionSendId(null);
    			sendDtoIn.setPoolBillInfo(infoIn);
    			sendDtoIn.setFBillAmount(billAmtIn);
    			sendDtoIn.setTradeAmt(billAmtIn);
    			sendDtoIn.setBeginRangeNo(strBillNoStr);
    			sendDtoIn.setEndRangeNo(info.getEndRangeNo());
    			list.add(sendDtoIn);
				
    			consignService.txSaveEntity(sendDtoIn);
    			
    			/**
				 * 作废原始数据
				 */
				sendDto.setSBillStatus(PoolComm.DS_12);
				info.setSDealStatus(PoolComm.DS_12);
				pool.setAssetStatus(PoolComm.DS_12);
				consignService.txSaveEntity(sendDto);
				consignService.txSaveEntity(info);
				consignService.txSaveEntity(pool);
				
				if(transType.equals("2")){//如果线下结算  修改状态记账成功；若有占用信贷的保贴额度，直接释放信贷的保贴额度
					
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						//部分清偿
	        			AssetRegister arSPlitNew = new AssetRegister();
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew);//原始数据历史记录
	    				
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew.setId(null);
	    				arSPlitNew.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn.getBeginRangeNo() + "-" + sendDtoIn.getEndRangeNo());
	    				arSPlitNew.setAssetAmount(pool.getTradeAmt());
	    				//保存删除历史
	    				//保存新拆分的资产登记表信息
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txStore(arSPlitNew);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				
	    				/**
	    				 * 信贷额度置换，方便记账时额度释放
	    				 */
	    				this.toPJE027(info,"2", sendDtoOut,list);
					}
					
					sendDtoOut.setSBillStatus(PoolComm.TS05);//记账成功
					sendDtoOut.setLastOperTm(new Date());
					sendDtoOut.setLastOperName("自动托收:线下清偿，记账成功");
					consignService.txStore(sendDtoOut);
					
					infoOut.setSDealStatus(PoolComm.TS05);
					poolOut.setAssetStatus(PoolComm.TS05);//记账成功
					poolOut.setLastOperTm(DateUtils.getWorkDayDate());
					poolOut.setLastOperName("自动托收过程,线下清偿，记账成功");
					infoOut.setLastOperTm(DateUtils.getWorkDayDate());
					infoOut.setLastOperName("自动托收过程,线下清偿，记账成功");
					consignService.txStore(infoOut);
					consignService.txStore(poolOut);
					
				}else{//线上
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						//部分清偿
	        			AssetRegister arSPlitNew = new AssetRegister();
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew);//原始数据历史记录
	    				
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew.setId(null);
	    				arSPlitNew.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn.getBeginRangeNo() + "-" + sendDtoIn.getEndRangeNo());
	    				arSPlitNew.setAssetAmount(pool.getTradeAmt());
	    				//保存删除历史
	    				//保存新拆分的资产登记表信息
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txStore(arSPlitNew);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				
	    				/**
	    				 * 信贷额度置换，方便记账时额度释放
	    				 */
	    				this.toPJE027(info,"1", sendDtoOut,list);
					}
				}
    			
    		}
    		
    		if(BigDecimalUtils.valueOf(sendDto.getBeginRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeStart)) < 0 
    				&& BigDecimalUtils.valueOf(sendDto.getEndRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeEnd)) == 0){
    			/**
    			 * 拆分的两张票号为：①原始的起票号  -  通知的起票号-1  ②通知的起票号  - 通知的止票号
    			 */
    			
    			falg1 = true;
    			
    			DraftPool poolOut = new DraftPool();
    			PoolBillInfo infoOut = new PoolBillInfo();
    			BigDecimal billAmtOut = DraftRangeHandler.formatBillNos(billRangeStart, billRangeEnd);//票据金额
    			
    			//大票表小票信息
    			BeanUtil.copyValue(info, infoOut);
    			infoOut.setBillinfoId(null);
    			infoOut.setFBillAmount(billAmtOut);
    			infoOut.setTradeAmt(billAmtOut);
    			infoOut.setBeginRangeNo(billRangeStart);
    			infoOut.setEndRangeNo(billRangeEnd);
    			infoOut.setSDealStatus(PoolComm.DS_07);
    			infoOut.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(infoOut);
    			
    			//资产表小票信息
    			BeanUtil.copyValue(pool, poolOut);
    			poolOut.setId(null);
    			poolOut.setPoolBillInfo(infoOut);
    			poolOut.setAssetAmt(billAmtOut);
    			poolOut.setTradeAmt(billAmtOut);
    			poolOut.setBeginRangeNo(billRangeStart);
    			poolOut.setEndRangeNo(billRangeEnd);
    			poolOut.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(poolOut);
    			
    			//托收表小票信息
    			BeanUtil.copyValue(sendDto, sendDtoOut);
    			sendDtoOut.setCollectionSendId(null);
    			sendDtoOut.setPoolBillInfo(infoOut);
    			sendDtoOut.setFBillAmount(billAmtOut);
    			sendDtoOut.setTradeAmt(billAmtOut);
    			sendDtoOut.setBeginRangeNo(billRangeStart);
    			sendDtoOut.setEndRangeNo(billRangeEnd);
    			
    			
    			sendDtoOut.setClearWay(transType);//清算方式
				sendDtoOut.setSBillStatus(PoolComm.TS03);
				sendDtoOut.setLastOperTm(DateUtils.getWorkDayDate());
				sendDtoOut.setLastOperName("托收自动任务:托收已签收");
				sendDtoOut.setIsPursue("1");//追索清偿数据
    			consignService.txSaveEntity(sendDtoOut);
    			
    			
    			DraftPool poolIn = new DraftPool();
    			PoolBillInfo infoIn = new PoolBillInfo();
    			
    			int endBillNo = Integer.parseInt(billRangeStart)-1;
    			String endBillNoStr = endBillNo+"";
    			BigDecimal billAmtIn = DraftRangeHandler.formatBillNos(info.getBeginRangeNo(), endBillNoStr);//票据金额
    			
    			//大票表小票信息
    			BeanUtil.copyValue(info, infoIn);
    			infoIn.setBillinfoId(null);
    			infoIn.setFBillAmount(billAmtIn);
    			infoIn.setTradeAmt(billAmtIn);
    			infoIn.setBeginRangeNo(info.getBeginRangeNo());
    			infoIn.setEndRangeNo(endBillNoStr);
    			consignService.txSaveEntity(infoIn);
    			
    			//资产表小票信息
    			BeanUtil.copyValue(pool, poolIn);
    			poolIn.setId(null);
    			poolIn.setPoolBillInfo(infoIn);
    			poolIn.setAssetAmt(billAmtIn);
    			poolIn.setTradeAmt(billAmtIn);
    			poolIn.setBeginRangeNo(info.getBeginRangeNo());
    			poolIn.setEndRangeNo(endBillNoStr);
    			consignService.txSaveEntity(poolIn);
    			
    			//托收表小票信息
    			BeanUtil.copyValue(sendDto, sendDtoIn);
    			sendDtoIn.setCollectionSendId(null);
    			sendDtoIn.setPoolBillInfo(infoIn);
    			sendDtoIn.setFBillAmount(billAmtIn);
    			sendDtoIn.setTradeAmt(billAmtIn);
    			sendDtoIn.setBeginRangeNo(info.getBeginRangeNo());
    			sendDtoIn.setEndRangeNo(endBillNoStr);
    			
    			consignService.txSaveEntity(sendDtoIn);
    			list.add(sendDtoIn);
    			
    			/**
				 * 作废原始数据
				 */
				sendDto.setSBillStatus(PoolComm.DS_12);
				info.setSDealStatus(PoolComm.DS_12);
				pool.setAssetStatus(PoolComm.DS_12);
				consignService.txSaveEntity(sendDto);
				consignService.txSaveEntity(info);
				consignService.txSaveEntity(pool);
				
				if(transType.equals("2")){//如果线下结算  修改状态记账成功
					
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						//部分清偿
	        			AssetRegister arSPlitNew = new AssetRegister();
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew);//原始数据历史记录
	    				
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew.setId(null);
	    				arSPlitNew.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn.getBeginRangeNo() + "-" + sendDtoIn.getEndRangeNo());
	    				arSPlitNew.setAssetAmount(pool.getTradeAmt());
	    				//保存删除历史
	    				//保存新拆分的资产登记表信息
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txStore(arSPlitNew);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				
	    				/**
	    				 * 信贷额度置换，方便记账时额度释放
	    				 */
	    				this.toPJE027(info,"2", sendDtoOut,list);
					}
					
					sendDtoOut.setSBillStatus(PoolComm.TS05);//记账成功
					sendDtoOut.setLastOperTm(new Date());
					sendDtoOut.setLastOperName("自动托收:线下清偿，记账成功");
					consignService.txStore(sendDtoOut);
					
					infoOut.setSDealStatus(PoolComm.TS05);
					poolOut.setAssetStatus(PoolComm.TS05);//记账成功
					poolOut.setLastOperTm(DateUtils.getWorkDayDate());
					poolOut.setLastOperName("自动托收过程,线下清偿，记账成功");
					infoOut.setLastOperTm(DateUtils.getWorkDayDate());
					infoOut.setLastOperName("自动托收过程,线下清偿，记账成功");
					consignService.txStore(infoOut);
					consignService.txStore(poolOut);
				}else{//线上
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						//部分清偿
	        			AssetRegister arSPlitNew = new AssetRegister();
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew);//原始数据历史记录
	    				
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew.setId(null);
	    				arSPlitNew.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn.getBeginRangeNo() + "-" + sendDtoIn.getEndRangeNo());
	    				arSPlitNew.setAssetAmount(pool.getTradeAmt());
	    				//保存删除历史
	    				//保存新拆分的资产登记表信息
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txStore(arSPlitNew);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				
	    				/**
	    				 * 信贷额度置换，方便记账时额度释放
	    				 */
	    				this.toPJE027(info,"1", sendDtoOut,list);
					}
				}
    		}
    		if(BigDecimalUtils.valueOf(sendDto.getBeginRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeStart)) < 0 
    				&& BigDecimalUtils.valueOf(sendDto.getEndRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeEnd)) > 0){

    			/**
    			 * 拆分的三张票号为：①原始的起票号  -  通知的起票号-1 ②通知的起票号  - 通知的止票号  ③通知的止票号+1  - 原始的止票号
    			 */
    			
    			
    			DraftPool poolOut = new DraftPool();
    			PoolBillInfo infoOut = new PoolBillInfo();
    			BigDecimal billAmtOut = DraftRangeHandler.formatBillNos(billRangeStart, billRangeEnd);//票据金额
    			
    			//大票表小票信息
    			BeanUtil.copyValue(info, infoOut);
    			infoOut.setBillinfoId(null);
    			infoOut.setFBillAmount(billAmtOut);
    			infoOut.setTradeAmt(billAmtOut);
    			infoOut.setBeginRangeNo(billRangeStart);
    			infoOut.setEndRangeNo(billRangeEnd);
    			infoOut.setSDealStatus(PoolComm.DS_07);
    			infoOut.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(infoOut);
    			
    			//资产表小票信息
    			BeanUtil.copyValue(pool, poolOut);
    			poolOut.setId(null);
    			poolOut.setPoolBillInfo(infoOut);
    			poolOut.setAssetAmt(billAmtOut);
    			poolOut.setTradeAmt(billAmtOut);
    			poolOut.setBeginRangeNo(billRangeStart);
    			poolOut.setEndRangeNo(billRangeEnd);
    			poolOut.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(poolOut);
    			
    			//托收表小票信息
    			BeanUtil.copyValue(sendDto, sendDtoOut);
    			sendDtoOut.setCollectionSendId(null);
    			sendDtoOut.setPoolBillInfo(infoOut);
    			sendDtoOut.setFBillAmount(billAmtOut);
    			sendDtoOut.setTradeAmt(billAmtOut);
    			sendDtoOut.setBeginRangeNo(billRangeStart);
    			sendDtoOut.setEndRangeNo(billRangeEnd);
    			
    			sendDtoOut.setClearWay(transType);//清算方式
				sendDtoOut.setSBillStatus(PoolComm.TS03);
				sendDtoOut.setLastOperTm(DateUtils.getWorkDayDate());
				sendDtoOut.setLastOperName("托收自动任务:托收已签收");
				sendDtoOut.setIsPursue("1");//追索清偿数据
    			consignService.txSaveEntity(sendDtoOut);
    			
    			
    			DraftPool poolIn1 = new DraftPool();
    			PoolBillInfo infoIn1 = new PoolBillInfo();
    			
    			int endBillNo = Integer.parseInt(billRangeStart)-1;
    			String endBillNoStr = endBillNo+"";
    			BigDecimal billAmtIn = DraftRangeHandler.formatBillNos(info.getBeginRangeNo(), endBillNoStr);//票据金额
    			
    			//大票表小票信息
    			BeanUtil.copyValue(info, infoIn1);
    			infoIn1.setBillinfoId(null);
    			infoIn1.setFBillAmount(billAmtIn);
    			infoIn1.setTradeAmt(billAmtIn);
    			infoIn1.setBeginRangeNo(info.getBeginRangeNo());
    			infoIn1.setEndRangeNo(endBillNoStr);
    			infoIn1.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(infoIn1);
    			
    			//资产表小票信息
    			BeanUtil.copyValue(pool, poolIn1);
    			poolIn1.setId(null);
    			poolIn1.setPoolBillInfo(infoIn1);
    			poolIn1.setAssetAmt(billAmtIn);
    			poolIn1.setTradeAmt(billAmtIn);
    			poolIn1.setBeginRangeNo(info.getBeginRangeNo());
    			poolIn1.setEndRangeNo(endBillNoStr);
    			poolIn1.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(poolIn1);
    			
    			//托收表小票信息
    			BeanUtil.copyValue(sendDto, sendDtoIn1);
    			sendDtoIn1.setCollectionSendId(null);
    			sendDtoIn1.setPoolBillInfo(infoIn1);
    			sendDtoIn1.setFBillAmount(billAmtIn);
    			sendDtoIn1.setTradeAmt(billAmtIn);
    			sendDtoIn1.setBeginRangeNo(info.getBeginRangeNo());
    			sendDtoIn1.setEndRangeNo(endBillNoStr);
    			
    			consignService.txSaveEntity(sendDtoIn1);
    			list.add(sendDtoIn1);
    			
    			DraftPool poolIn2 = new DraftPool();
    			PoolBillInfo infoIn2 = new PoolBillInfo();
    			
    			int strBillNo = Integer.parseInt(billRangeEnd)+1;
    			String startBillNoStr = strBillNo+"";
    			BigDecimal billAmtIn2 = DraftRangeHandler.formatBillNos(startBillNoStr, info.getEndRangeNo());//票据金额
    			
    			//大票表小票信息
    			BeanUtil.copyValue(info, infoIn2);
    			infoIn2.setBillinfoId(null);
    			infoIn2.setFBillAmount(billAmtIn);
    			infoIn2.setTradeAmt(billAmtIn);
    			infoIn2.setBeginRangeNo(startBillNoStr);
    			infoIn2.setEndRangeNo(info.getEndRangeNo());
    			infoIn2.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(infoIn2);
    			
    			//资产表小票信息
    			BeanUtil.copyValue(pool, poolIn2);
    			poolIn2.setId(null);
    			poolIn2.setPoolBillInfo(infoIn2);
    			poolIn2.setAssetAmt(billAmtIn);
    			poolIn2.setTradeAmt(billAmtIn);
    			poolIn2.setBeginRangeNo(startBillNoStr);
    			poolIn2.setEndRangeNo(info.getEndRangeNo());
    			poolIn2.setSplitId(info.getBillinfoId());
    			
    			consignService.txSaveEntity(poolIn2);
    			
    			//托收表小票信息
    			BeanUtil.copyValue(sendDto, sendDtoIn2);
    			sendDtoIn2.setCollectionSendId(null);
    			sendDtoIn2.setPoolBillInfo(infoIn2);
    			sendDtoIn2.setFBillAmount(billAmtIn);
    			sendDtoIn2.setTradeAmt(billAmtIn);
    			sendDtoIn2.setBeginRangeNo(startBillNoStr);
    			sendDtoIn2.setEndRangeNo(info.getEndRangeNo());
    			
    			consignService.txSaveEntity(sendDtoIn2);
    			list.add(sendDtoIn2);
    			
    			/**
				 * 作废原始数据
				 */
				sendDto.setSBillStatus(PoolComm.DS_12);
				info.setSDealStatus(PoolComm.DS_12);
				pool.setAssetStatus(PoolComm.DS_12);
				consignService.txSaveEntity(sendDto);
				consignService.txSaveEntity(info);
				consignService.txSaveEntity(pool);
				
				if(transType.equals("2")){//如果线下结算  修改状态记账成功
					
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						//部分清偿
	        			AssetRegister arSPlitNew = new AssetRegister();
	        			AssetRegister arSPlitNew2 = new AssetRegister();
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew);//原始数据历史记录
	    				
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew.setId(null);
	    				arSPlitNew.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn1.getBeginRangeNo() + "-" + sendDtoIn1.getEndRangeNo());
	    				arSPlitNew.setAssetAmount(pool.getTradeAmt());
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew2);
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew2.setId(null);
	    				arSPlitNew2.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn2.getBeginRangeNo() + "-" + sendDtoIn2.getEndRangeNo());
	    				arSPlitNew2.setAssetAmount(pool.getTradeAmt());
	    				
	    				//保存删除历史
	    				//保存新拆分的资产登记表信息
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txStore(arSPlitNew);
	    				assetRegisterService.txStore(arSPlitNew2);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew2, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				
	    				/**
	    				 * 信贷额度置换，方便记账时额度释放
	    				 */
	    				this.toPJE027(info,"2", sendDtoOut,list);
					}
					
					sendDtoOut.setSBillStatus(PoolComm.TS05);//记账成功
					sendDtoOut.setLastOperTm(new Date());
					sendDtoOut.setLastOperName("自动托收:线下清偿，记账成功");
					consignService.txStore(sendDtoOut);
					
					infoOut.setSDealStatus(PoolComm.TS05);
					poolOut.setAssetStatus(PoolComm.TS05);//记账成功
					poolOut.setLastOperTm(DateUtils.getWorkDayDate());
					poolOut.setLastOperName("自动托收过程,线下清偿，记账成功");
					infoOut.setLastOperTm(DateUtils.getWorkDayDate());
					infoOut.setLastOperName("自动托收过程,线下清偿，记账成功");
					consignService.txStore(infoOut);
					consignService.txStore(poolOut);
				}else{//线上
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						//部分清偿
	        			AssetRegister arSPlitNew = new AssetRegister();
	        			AssetRegister arSPlitNew2 = new AssetRegister();
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew);//原始数据历史记录
	    				
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew.setId(null);
	    				arSPlitNew.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn1.getBeginRangeNo() + "-" + sendDtoIn1.getEndRangeNo());
	    				arSPlitNew.setAssetAmount(pool.getTradeAmt());
	    				
	    				BeanUtil.copyValue(assetRegister, arSPlitNew2);
	    				/**
	    				 * 拆分做数据变更
	    				 */
	    				arSPlitNew2.setId(null);
	    				arSPlitNew2.setAssetNo(pool.getAssetNb()+ "-" + sendDtoIn2.getBeginRangeNo() + "-" + sendDtoIn2.getEndRangeNo());
	    				arSPlitNew2.setAssetAmount(pool.getTradeAmt());
	    				
	    				//保存删除历史
	    				//保存新拆分的资产登记表信息
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txStore(arSPlitNew);
	    				assetRegisterService.txStore(arSPlitNew2);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				assetRegisterService.txSaveAssetRegisterHis(arSPlitNew2, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				
	    				/**
	    				 * 信贷额度置换，方便记账时额度释放
	    				 */
	    				this.toPJE027(info,"1", sendDtoOut,list);
					}
				}
    		
    		}
    		
    		if(BigDecimalUtils.valueOf(sendDto.getBeginRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeStart)) == 0 
    				&& BigDecimalUtils.valueOf(sendDto.getEndRangeNo()).compareTo(BigDecimalUtils.valueOf(billRangeEnd)) == 0){

    			/**
    			 * 全部清偿成功
    			 */
    			
				sendDto.setSBillStatus(PoolComm.TS03);
				consignService.txSaveEntity(sendDto);
				
				if(transType.equals("2")){//如果线下结算  修改状态记账成功
					
					
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						/**
	        			 * 删除额度
	        			 */
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txSaveAssetRegisterHis(assetRegister, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				
	    				/**
	    				 * 释放信贷额度
	    				 */
	    				Map resuMap = new HashMap();
						List<Map> reqList = new ArrayList<Map>();//实际为单条
						CreditTransNotes creditNotes = new CreditTransNotes();
						
						resuMap.put("billNo", sendDto.getSBillNo()+"-"+sendDto.getBeginRangeNo()+"-"+sendDto.getEndRangeNo());
						reqList.add(resuMap);
						creditNotes.setReqList(reqList);//上传文件
						
						ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
						
						if(response1.isTxSuccess()){
							pool.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
							PoolQueryBean pBean = new PoolQueryBean();
							pBean.setProtocolNo(pool.getPoolAgreement());
							pBean.setBillNo(pool.getAssetNb());
							
							pBean.setBeginRangeNo(pool.getBeginRangeNo());
							pBean.setEndRangeNo(pool.getEndRangeNo());
							
							PedGuaranteeCredit pedCredit = productService.queryByBean(pBean);
							pedCredit.setStatus(PoolComm.SP_00);
							pedCredit.setCreateTime(DateUtils.getWorkDayDate());
							consignService.txStore(pedCredit);
							consignService.txStore(pool);
							logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度释放成功...");
							
						}
					}
					
					sendDto.setSBillStatus(PoolComm.TS05);//记账成功
					sendDto.setLastOperTm(new Date());
					sendDto.setLastOperName("自动托收:线下清偿，记账成功");
					consignService.txStore(sendDto);
					
					info.setSDealStatus(PoolComm.TS05);
					pool.setAssetStatus(PoolComm.TS05);//记账成功
					pool.setLastOperTm(DateUtils.getWorkDayDate());
					pool.setLastOperName("自动托收过程,线下清偿，记账成功");
					info.setLastOperTm(DateUtils.getWorkDayDate());
					info.setLastOperName("自动托收过程,线下清偿，记账成功");
					consignService.txStore(info);
					consignService.txStore(pool);
				}else{//线上
					/**
					 * 释放保贴额度及票据池额度
					 */
					if(assetRegister != null){
						/**
	        			 * 删除额度
	        			 */
	    				assetRegisterService.txDelete(assetRegister);
	    				
	    				assetRegisterService.txSaveAssetRegisterHis(assetRegister, PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
	    				/**
	    				 * 释放信贷额度
	    				 */
	    				Map resuMap = new HashMap();
						List<Map> reqList = new ArrayList<Map>();//实际为单条
						CreditTransNotes creditNotes = new CreditTransNotes();
						
						resuMap.put("billNo", sendDto.getSBillNo()+"-"+sendDto.getBeginRangeNo()+"-"+sendDto.getEndRangeNo());
						reqList.add(resuMap);
						creditNotes.setReqList(reqList);//上传文件
						
						ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
						
						if(response1.isTxSuccess()){
							pool.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
							PoolQueryBean pBean = new PoolQueryBean();
							pBean.setProtocolNo(pool.getPoolAgreement());
							pBean.setBillNo(pool.getAssetNb());
							
							pBean.setBeginRangeNo(pool.getBeginRangeNo());
							pBean.setEndRangeNo(pool.getEndRangeNo());
							
							PedGuaranteeCredit pedCredit = productService.queryByBean(pBean);
							pedCredit.setStatus(PoolComm.SP_00);
							pedCredit.setCreateTime(DateUtils.getWorkDayDate());
							consignService.txStore(pedCredit);
							consignService.txStore(pool);
							logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度释放成功...");
							
						}
	        			
	        			//全部清偿
	        			sendDto.setIsPursue("1");//追索清偿数据
	        			sendDto.setSBillStatus(PoolComm.TS05);
	        			sendDto.setLastOperTm(DateUtils.getWorkDayDate());
	        			sendDto.setLastOperName("托收自动任务:清偿成功，已记账");
	        			sendDto.setClearWay(transType);//清算方式
	        			
	        			info.setSDealStatus(PoolComm.TS05);
						pool.setAssetStatus(PoolComm.TS05);//记账成功
						pool.setLastOperTm(DateUtils.getWorkDayDate());
						pool.setLastOperName("自动托收过程,清偿成功，已记账");
						info.setLastOperTm(DateUtils.getWorkDayDate());
						info.setLastOperName("自动托收过程,清偿成功，已记账");
						consignService.txStore(info);
						consignService.txStore(pool);
						
						assetRegisterService.txStore(sendDto);
						assetRegisterService.txStore(info);
					}
				}
    		
    		}
    		
    		/**
			 * 已发追索并部分清偿的数据
			 * （1）如果查询回存在票据子票区间内的部分子票的状态为【已清偿】且清算方式为【线下】，票据池系统将【已清偿】的子票按照出池进行处理：原票状态置为失效，
			 * 		并生成两张【已清偿】和【未清偿】的子票记录，然后释放【已清偿】子票的额度及信贷的保贴额度。票据池对【已清偿】子票的托收回款资金不进行处理，
			 * 		这部分资金由线下人工进行处理。【未清偿】子票与原始大票的状态保持一致，等待清偿结果。（注意顺序）。
			 * （2）如果查询回存在票据子票区间内的部分子票的状态为【已清偿】且清算方式为【线上】，票据池系统将【已清偿】的子票按照出池进行处理：原票状态置为失效，
			 * 		并生成两张【已清偿】和【未清偿】的子票记录，然后释放【已清偿】子票的额度及信贷的保贴额度。票据池自动将【已清偿】子票的托收回款资金从专有账户扣划至票据对应的票据池保证金账户。
			 * 		【未清偿】子票与原始大票的状态保持一致，等待清偿结果。（注意顺序）。
			 */
        }
		
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
     	response.setRet(ret);
		return response;
    
    	
    }
    
    /**
     * 拆分票据出池  大小票置换占用处理
     * @param billInfo	拆分前票据基本信息表对象
     * @param type		1:线上清算大票额度置换成小票（占用）额度；2：线下清算，大票额度置换成小票（占用与释放）额度
     * @param sendDto	要释放的数据
     * @param list		拆分后占用的数据
     * @param eduType  1:大票换两张小票占用   2：大票换一张小票占用一张小票释放
     * @return
     * @throws Exception
     */
	public boolean toPJE027(PoolBillInfo billInfo,String type,CollectionSendDto sendDto,List list) throws Exception{
		
		/**
		 * 查寻原保贴额度对象
		 */
		PoolQueryBean pBean = new PoolQueryBean();
		pBean.setProtocolNo(billInfo.getPoolAgreement());
		pBean.setBillNo(sendDto.getSBillNo());
		
		pBean.setBeginRangeNo(billInfo.getBeginRangeNo());
		pBean.setEndRangeNo(billInfo.getEndRangeNo());
		PedGuaranteeCredit pedCredit = productService.queryByBean(pBean);
		
			
		List<Map> reqList = new ArrayList<Map>();
		CreditTransNotes creditNotes = new CreditTransNotes();
		Map resuMap = new HashMap();//拆分前票
		
		Map resuMapOut = new HashMap();//拆分后出库票
		
		BigDecimal billAmt = billInfo.getFBillAmount();//票面金额
		String billType = billInfo.getSBillType();//票据类型
		String bankNo = billInfo.getSAcceptorBankCode();//承兑行行号
		String totalBankNo = billInfo.getAcptHeadBankNo();//承兑行行号--总行   二代支付行号
		String totalBankName = billInfo.getAcptHeadBankName();//承兑行行名--总行
		String acceptor = billInfo.getSAcceptor();//承兑人全称
		String acptAcctNo = billInfo.getSAcceptorAccount();//承兑人账号
		Map rsuMap = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
		
		if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
			totalBankNo ="";
			totalBankName = acceptor;
		}
		
		for (int i = 0; i < list.size(); i++) {
			CollectionSendDto send = (CollectionSendDto) list.get(i);
			
			Map resuMapIn = new HashMap();//拆分后入库票
			
			resuMapIn.put("BILL_INFO_ARRAY.BANK_NO", totalBankNo);//二代支付行号
			if(rsuMap != null){
				resuMapIn.put("BILL_INFO_ARRAY.CLINET_NAME", (String)rsuMap.get("guarantDiscName"));//客户名称
				resuMapIn.put("BILL_INFO_ARRAY.CLIENT_NO", (String)rsuMap.get("guarantDiscNo"));//客户编号
			}else{
				resuMapIn.put("BILL_INFO_ARRAY.CLINET_NAME", totalBankName);//客户名称
			}
			
			resuMapIn.put("BILL_INFO_ARRAY.BILL_NO", send.getSBillNo()+"-"+send.getBeginRangeNo()+"-"+send.getEndRangeNo());//票号
			resuMapIn.put("BILL_INFO_ARRAY.OPERATION_TYPE", "01");//操作类型 01 占用  02 释放
			resuMapIn.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT02");//票据拆分类型 票据拆分类型  BT01-拆分前票据  BT02-拆分后入库票 BT03-拆分后出库票
			resuMapIn.put("BILL_INFO_ARRAY.BILL_AMT", send.getFBillAmount());//票据金额
			resuMapIn.put("BILL_INFO_ARRAY.LIMIT_NO", "");//额度编号
			resuMapIn.put("BILL_INFO_ARRAY.LIMIT_TYPE", "SX0060201910040060");//额度类型
			reqList.add(resuMapIn);
			
		}
		
		resuMap.put("BILL_INFO_ARRAY.BANK_NO", totalBankNo);//二代支付行号
		
		resuMapOut.put("BILL_INFO_ARRAY.BANK_NO", totalBankNo);//二代支付行号
		
		
		if(rsuMap != null){
			resuMap.put("BILL_INFO_ARRAY.CLINET_NAME", (String)rsuMap.get("guarantDiscName"));//客户名称
			resuMapOut.put("BILL_INFO_ARRAY.CLINET_NAME", (String)rsuMap.get("guarantDiscName"));//客户名称
			
			resuMap.put("BILL_INFO_ARRAY.CLIENT_NO", (String)rsuMap.get("guarantDiscNo"));//客户编号
			resuMapOut.put("BILL_INFO_ARRAY.CLIENT_NO", (String)rsuMap.get("guarantDiscNo"));//客户编号
			
		}else{
			resuMap.put("BILL_INFO_ARRAY.CLINET_NAME", totalBankName);//客户名称
			
			resuMapOut.put("BILL_INFO_ARRAY.CLINET_NAME", totalBankName);//客户名称
		}
		
		resuMap.put("BILL_INFO_ARRAY.BILL_NO", billInfo.getSBillNo()+"-"+billInfo.getBeginRangeNo()+"-"+billInfo.getEndRangeNo());//票号
		resuMap.put("BILL_INFO_ARRAY.OPERATION_TYPE", "02");//操作类型 释放
		resuMap.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT01");//票据拆分类型  BT01-拆分前票据  BT02-拆分后入库票 BT03-拆分后出库票
		resuMap.put("BILL_INFO_ARRAY.BILL_AMT", billInfo.getFBillAmount());//票据金额
		resuMap.put("BILL_INFO_ARRAY.LIMIT_NO", "");//额度编号
		resuMap.put("BILL_INFO_ARRAY.LIMIT_TYPE", "SX0060201910040060");//额度类型
		reqList.add(resuMap);
		
		
		
		resuMapOut.put("BILL_INFO_ARRAY.BILL_NO", sendDto.getSBillNo()+"-"+sendDto.getBeginRangeNo()+"-"+sendDto.getEndRangeNo());//票号
		if(type.equals("1")){
			resuMapOut.put("BILL_INFO_ARRAY.OPERATION_TYPE", "01");//操作类型 占用
			resuMapOut.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT02");//票据拆分类型
		}else{
			resuMapOut.put("BILL_INFO_ARRAY.OPERATION_TYPE", "02");//操作类型 释放
			resuMapOut.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT03");//票据拆分类型
		}
		resuMapOut.put("BILL_INFO_ARRAY.BILL_AMT", sendDto.getFBillAmount());//票据金额
		resuMapOut.put("BILL_INFO_ARRAY.LIMIT_NO", "");//额度编号
		resuMapOut.put("BILL_INFO_ARRAY.LIMIT_TYPE", "SX0060201910040060");//额度类型
		reqList.add(resuMapOut);
		
		creditNotes.setReqList(reqList);//上传文件
		ReturnMessageNew resp = poolCreditClientService.txPJE027(creditNotes);
		if(resp.isTxSuccess()){
			/**
			 * 入库小票的保贴额度占用记录
			 */
			for (int i = 0; i < list.size(); i++) {
				CollectionSendDto send = (CollectionSendDto) list.get(i);
				PedGuaranteeCredit pedCreditIn = new PedGuaranteeCredit();
				BeanUtil.copyValue(pedCredit, pedCreditIn);
				pedCreditIn.setId(null);
				pedCreditIn.setBillAmt(send.getFBillAmount());
				pedCreditIn.setBeginRangeNo(send.getBeginRangeNo());//票据开始子区间号
				pedCreditIn.setEndRangeNo(send.getEndRangeNo());//票据结束子区间号
				pedCreditIn.setCreateTime(new Date());
				
				draftPoolOutService.txStore(pedCreditIn);
			}
			
			PedGuaranteeCredit pedCreditOut = new PedGuaranteeCredit();
			BeanUtil.copyValue(pedCredit, pedCreditOut);
			pedCreditOut.setId(null);
			pedCreditOut.setBillAmt(sendDto.getFBillAmount());
			pedCreditOut.setBeginRangeNo(sendDto.getBeginRangeNo());//票据开始子区间号
			pedCreditOut.setEndRangeNo(sendDto.getEndRangeNo());//票据结束子区间号
			if(type.equals("2")){
				pedCreditOut.setStatus(PoolComm.SP_00);
			}
			pedCreditOut.setCreateTime(new Date());
			
			draftPoolOutService.txStore(pedCreditOut);
			
			PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setBillNo(sendDto.getSBillNo());
			poolQueryBean.setSStatusFlag(PoolComm.DS_06);
			
			/********************融合改造新增 start******************************/
			poolQueryBean.setBeginRangeNo(sendDto.getBeginRangeNo());
			poolQueryBean.setEndRangeNo(sendDto.getEndRangeNo());
			/********************融合改造新增 end******************************/
			
			DraftPool dpool=consignService.queryDraftByBean(poolQueryBean).get(0);
			pedCredit.setStatus(PoolComm.SP_00);
			pedCredit.setCreateTime(new Date());
			draftPoolOutService.txStore(pedCredit);
			
			draftPoolOutService.txStore(dpool);
			logger.info("电票出池【"+sendDto.getSBillNo()+"】额度系统额度释放成功！");
			return true;
		}else{
			logger.info("电票出池【"+sendDto.getSBillNo()+"】额度系统额度释放失败,额度系统错误："+resp.getRet().getRET_MSG());	
			return false;
		}
			
	}
    
	
    public void txCheckBill(DraftPool pool,PoolBillInfo info,String startBillNo,String endBillNo,BigDecimal billAmt) throws Exception{
    	DraftPool pool1 = new DraftPool();
		PoolBillInfo info1 = new PoolBillInfo();
		
		//大票表小票信息
		BeanUtil.copyValue(info, info1);
		info1.setBillinfoId(null);
		info1.setFBillAmount(billAmt);
		info1.setTradeAmt(billAmt);
		info1.setBeginRangeNo(startBillNo);
		info1.setEndRangeNo(endBillNo);
		consignService.txSaveEntity(info1);
		
		//资产表小票信息
		BeanUtil.copyValue(pool, pool1);
		pool1.setId(null);
		pool1.setPoolBillInfo(info1);
		pool1.setAssetAmt(billAmt);
		pool1.setTradeAmt(billAmt);
		pool1.setBeginRangeNo(startBillNo);
		pool1.setEndRangeNo(endBillNo);
		consignService.txSaveEntity(pool1);

    }
    
    


}
