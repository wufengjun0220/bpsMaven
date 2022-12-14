package com.mingtech.application.autotask.taskdispatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

public  class OnlineAcpt1AutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineAcpt1AutoTaskDispatch.class);
	private PedOnlineAcptService pedOnlineAcptService =PoolCommonServiceFactory.getPedOnlineAcptService();
	private OnlineManageService onlineManageService=PoolCommonServiceFactory.getOnlineManageService();
	private AutoTaskPublishService autoTaskPublishService=PoolCommonServiceFactory.getAutoTaskPublishService();
    private AutoTaskExeService autoTaskExeService=PoolCommonServiceFactory.getAutoTaskExeService();
    private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	private AssetRegisterService assetRegisterService = PoolCommonServiceFactory.getAssetRegisterService();
	private CreditRegisterService creditRegisterService = PoolCommonServiceFactory.getCreditRegisterService();
	private PedAssetPoolService pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();
	private PoolBailEduService poolBailEduService = PoolCommonServiceFactory.getPoolBailEduService();

	/**
	 *<p>??????????????????????????????<p/>
	 *@param reqParam ????????????????????????
	 *@param autoTaskExe ??????????????????
	 *@param taskDispatchCfg ??????????????????
	 *@return ??????????????????respCode????????????????????? respDesc ????????????????????????
	 *@?????? ??????????????????????????????
	 *1. ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????bbsp????????????,????????????????????????
	 *   ?????????????????????????????????????????????????????????????????????:
	 *2. ????????????/?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 *    ?????????????????????????????????????????????????????????????????????MIS????????????????????????????????????????????????????????????
	 *			 
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		String batchId = autoTaskExe.getBusiId();//????????????????????????id
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(batchId,PlOnlineAcptBatch.class);
		batch.setTaskDate(new Date());
		List<PlOnlineAcptDetail> details = pedOnlineAcptService.queryOnlineAcptDetailByBatchId(batchId);
		try{
			if(PublicStaticDefineTab.ACPT_BATCH_001.equals(batch.getStatus())){//??????
				OnlineQueryBean queryBean =pedOnlineAcptService.createOnlineAcptApply(batch, details);
				ReturnMessageNew result =pedOnlineAcptService.txAcptCheckApply(queryBean);
				if(!Constants.TX_SUCCESS_CODE.equals(result.getRet().getRET_CODE())){
					pedOnlineAcptService.txFailChangeAcptDetail(batch,PublicStaticDefineTab.ACPT_DETAIL_012);//??????
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					logger.info("?????????????????????????????????"+result.getRet().getRET_MSG());
					return resultMap;
				}
				/**
				 * ????????????????????????
				 */
				List<CreditRegister> crdtRegList = new ArrayList<CreditRegister>();
				Ret crdtCheckRet = new Ret();//????????????????????????
				boolean flag=false;
				PedOnlineAcptProtocol protocol = pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
    			if(new BigDecimal(100).compareTo(protocol.getDepositRatio()) ==0){
    				flag=true;
    			}
				if(!flag){//100%????????????????????????
						try {			
							PedProtocolDto dto = queryBean.getPool();
							AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
							String apId = ap.getApId();
							String bpsNo = dto.getPoolAgreement();
							for(PlOnlineAcptDetail detail : details){
								
								PedCreditDetail crdtDetail = pedOnlineAcptService.creatCrdtDetailByAcptDetail(detail, batch, dto);
								CreditRegister crdtReg = creditRegisterService.createCreditRegister(crdtDetail, dto, apId);
								crdtRegList.add(crdtReg);
							}
							//???????????????
							BailDetail bail = poolBailEduService.queryBailDetailByBpsNo(bpsNo);
							//???????????????????????????
							assetRegisterService.txCurrentDepositAssetChange(bpsNo, bail.getAssetNb(), bail.getAssetLimitFree());
							//????????????
							crdtCheckRet =  financialService.txCreditUsed(crdtRegList, dto);
							if(crdtCheckRet.getRET_CODE().equals(Constants.TX_FAIL_CODE)){//??????????????????,?????????????????????????????? :????????????-????????? ??????
								protocol.setUsedAmt(protocol.getUsedAmt().subtract(batch.getTotalAmt()));
								financialService.txStore(protocol);
								//????????????
								onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "", "???????????????????????????", "", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
								//????????????  
								batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_007);//??????
								batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
								pedOnlineAcptService.txStore(batch);
								for(PlOnlineAcptDetail detail:details){
									detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
									detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);//????????????????????????
									pedOnlineAcptService.txStore(detail);
								}
								//????????????
								autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
								logger.info("?????????"+batch.getBatchNo()+"?????????????????????????????????????????????");
								resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
								resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
								logger.info("??????????????????????????????...");
								return resultMap;
							}
						} catch (Exception e) {
							crdtCheckRet.setRET_CODE(Constants.TX_FAIL_CODE);
							crdtCheckRet.setRET_MSG("???????????????????????????!");
							logger.error("????????????OnlineAcpt1AutoTaskDispatch???????????????????????????!",e);
							//??????????????????,?????????????????????????????? :????????????-????????? ??????
							protocol.setUsedAmt(protocol.getUsedAmt().subtract(batch.getTotalAmt()));
							financialService.txStore(protocol);
							ReturnMessageNew result1  = pedOnlineAcptService.txPJE021Handler(batch,PublicStaticDefineTab.PRODUCT_001,PublicStaticDefineTab.CREDIT_OPERATION_CHECK,null);
							if(result1.isTxSuccess()){
								onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "??????","???????????????????????????! ??????"+ result1.getRet().getRET_MSG(), "PJE021", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
							}
							//????????????  
							batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_007);//??????
							batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
							pedOnlineAcptService.txStore(batch);
							for(PlOnlineAcptDetail detail:details){
								detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);//????????????????????????
								pedOnlineAcptService.txStore(detail);
							}
							//????????????
							autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
							logger.info("?????????"+batch.getBatchNo()+"?????????????????????????????????????????????");
							resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
							resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
							logger.info("??????????????????????????????...");
							return resultMap;
						}
					}
				
				if(flag||Constants.TX_SUCCESS_CODE.equals(crdtCheckRet.getRET_CODE())){
					
					/**
					 * ??????????????????
					 */
					ReturnMessageNew result1 =new ReturnMessageNew();
//					if(!flag){
						 result1  = pedOnlineAcptService.txPJE021Handler(batch,PublicStaticDefineTab.PRODUCT_001,PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE,null);
//					}
//					if(flag||result1.isTxSuccess()){
					if(result1.isTxSuccess()){
						batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_003);// ?????????????????????
						pedOnlineAcptService.txStore(batch);
						
						//???3?????????bbsp???????????? 
						ReturnMessageNew result2 = pedOnlineAcptService.txApplyNewBills(batch);
						if(result2.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
							logger.info(batch.getBatchNo()+"?????????");
							batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_004);
							pedOnlineAcptService.txStore(batch);
							
							//???4????????????????????????
							resultMap = this.publishTask(details);
							return resultMap;
						}else{
							logger.info(batch.getBatchNo()+"???????????????????????????");
							//????????????
							onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, result2.getRet().getRET_MSG(), "BBSP001", PublicStaticDefineTab.ACPT_BUSI_NAME_04, "send");
							//????????????   
							batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_004_1);
							batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
							pedOnlineAcptService.txStore(batch);
							for(PlOnlineAcptDetail detail:details){
								detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
//								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_004_1);//????????????
								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);//??????
								pedOnlineAcptService.txStore(detail);
							}
							/**
							 * ????????????(?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????)
							 */
							Map<String, String> reqParam =new HashMap<String,String>();
							reqParam.put("busiId",batch.getId());
							reqParam.put("type", "1");//?????? 1????????? 2:??????
							reqParam.put("isCredit", "1");//???????????????????????? 1?????? 2:???
							reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//????????????
							autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, batch.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam,batch.getBatchNo(), batch.getBpsNo(), null, null);
							logger.info(batch.getBatchNo()+"????????????????????????????????????????????????");
							//????????????
							List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
							if(msgList != null){
								for(PedOnlineMsgInfo msgInfo:msgList){
									onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), false,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
								}
							}
							/**
							 * ????????????
							 */
							autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
							logger.info("?????????"+batch.getBatchNo()+"??????????????????????????????????????????");
						}
					}else{
						//????????????
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "??????", "??????????????????"+result1.getRet().getRET_MSG(), "PJE021", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
						//????????????  
						batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_003_1);
						pedOnlineAcptService.txStore(batch);
						for(PlOnlineAcptDetail detail:details){
							detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
							detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_003_1);//????????????????????????
							pedOnlineAcptService.txStore(detail);
						}
						/**
						 * ????????????(?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????)
						 */
						Map<String, String> reqParam =new HashMap<String,String>();
						reqParam.put("busiId",batch.getId());
						reqParam.put("type", "1");//?????? 1????????? 2:??????
						reqParam.put("isCredit", "0");//???????????????????????? 0?????? 1:???
						reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//????????????
						autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, batch.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam, batch.getBatchNo(), batch.getBpsNo(), null, null);
						//????????????
						List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
						if(msgList != null){
							for(PedOnlineMsgInfo msgInfo:msgList){
								onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), false,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
							}
						}
						/**
						 * ????????????
						 */
						autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
						logger.info("?????????"+batch.getBatchNo()+"??????????????????????????????????????????");
					}
				}else{
					logger.info("????????????OnlineAcpt1AutoTaskDispatch???????????????????????????!");
					//????????????  
					batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_002_1);
					batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
					pedOnlineAcptService.txStore(batch);
					for(PlOnlineAcptDetail detail:details){
						detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//??????
						detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_002_1);//??????????????????
						pedOnlineAcptService.txStore(detail);
					}
					ReturnMessageNew result1  = pedOnlineAcptService.txPJE021Handler(batch,PublicStaticDefineTab.PRODUCT_001,PublicStaticDefineTab.CREDIT_OPERATION_CHECK,null);
//					ReturnMessageNew result1 = new ReturnMessageNew();
//					result1.setTxSuccess(true);
					if(result1.isTxSuccess()){
						onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId() ,"1", PublicStaticDefineTab.OPERATION_TYPE_02, "??????", "???????????????????????????", "PJE021", PublicStaticDefineTab.ACPT_BUSI_NAME_02, "send");
					}
					//????????????
					List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
					if(msgList != null){
						for(PedOnlineMsgInfo msgInfo:msgList){
							onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), false,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
						}
					}
					/**
					 * ????????????
					 */
					autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_ADD,AutoTaskNoDefine.POOL_ONLINE_ACPT_ADD,batch.getId());	        		
					logger.info("?????????"+batch.getBatchNo()+"??????????????????????????????????????????");
				}
			
			}else if(PublicStaticDefineTab.ACPT_BATCH_003.equals(batch.getStatus())){//????????????????????????
				ReturnMessageNew result2 = pedOnlineAcptService.txApplyNewBills(batch);
				if(result2.isTxSuccess()){
					batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_004);
					pedOnlineAcptService.txStore(batch);
					//?????????????????????
					resultMap = this.publishTask(details);
					return resultMap;
				}
			}else if(PublicStaticDefineTab.ACPT_BATCH_004.equals(batch.getStatus())){//????????????
				resultMap = this.publishTask(details);
				return resultMap;
			}
		}catch (Exception e) {
			pedOnlineAcptService.txStore(batch);
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			logger.error("?????????????????????1??????????????????????????????",e);
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("?????????????????????1??????????????????");
		return resultMap;
	}
	
	/**
	 * @Title ??????????????????
	 * @author wss
	 * @date 2021-5-15
	 * @param details
	 */
	private Map publishTask(List<PlOnlineAcptDetail> details) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		for(PlOnlineAcptDetail detial:details){
			try {
				PoolQueryBean poolQueryBean = new PoolQueryBean();
				poolQueryBean.setBusiId(detial.getId());
				poolQueryBean.setProductId(AutoTaskNoDefine.BUSI_TYPE_ONLINE_REGISTER);
				AutoTaskExe task = autoTaskExeService.queryAutoTaskExeByParm(poolQueryBean);
				if(null == task){
					autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO, detial.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_REGISTER, null, detial.getLoanNo(), detial.getBpsNo(), null, null);
					logger.info("?????????"+detial.getBillSerialNo()+"?????????????????????????????????...");
				}
			} catch (Exception e) {
				logger.error("??????????????????????????????????????????...",e);
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_TASK_PUBLISH+e.getMessage());
			}
		}
		return resultMap;
	}
}
