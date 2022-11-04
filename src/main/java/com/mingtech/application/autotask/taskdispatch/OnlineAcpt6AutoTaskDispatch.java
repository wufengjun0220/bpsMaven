package com.mingtech.application.autotask.taskdispatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatchUpdate;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.jdbcHelper.RowSets;
import com.mingtech.framework.common.util.ConnectionUtils;

/**
 * 银承业务明细状态及发生未用退回时金额统计
 * @author wfj
 * @version v1.0
 * @date 2022-1-5
 * @copyright 北明明润（北京）科技有限责任公司
 */
public  class OnlineAcpt6AutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineAcpt6AutoTaskDispatch.class);
	private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	private PedOnlineAcptService pedOnlineAcptService = PoolCommonServiceFactory.getPedOnlineAcptService();
	private PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	private PoolCreditProductService poolCreditProductService = PoolCommonServiceFactory.getPoolCreditProductService();
	private OnlineManageService onlineManageService = PoolCommonServiceFactory.getOnlineManageService();

	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		
		try {
			String batchId = reqParams.get("acptBatchId"); //票据池协议表ID 
			String acptId = reqParams.get("acptId"); //明细id
			String source = reqParams.get("source"); //来源  1  签收成功   2 当日未用退回成功
			/**
			 * 通过批次id查询明细
			 */
			List<PlOnlineAcptDetail> details = pedOnlineAcptService.queryOnlineAcptDetailByBatchId(batchId);
			int size = details.size();//批次下名明细笔数
			
			//查询批次的成功笔数记录
			String id = pedOnlineAcptService.query(batchId);
			PlOnlineAcptBatchUpdate update;
			if(id != null){
				update = (PlOnlineAcptBatchUpdate) pedOnlineAcptService.load(id, PlOnlineAcptBatchUpdate.class);
			}else{
				update = new PlOnlineAcptBatchUpdate();
				update.setBatchId(batchId);
				update.setIds("");
			}

			
			update.setCount(update.getCount()+1);
			if(source.equals("1")){
				//银承签收成功
				update.setSuccCount(update.getSuccCount()+1);
			}else if(source.equals("2")){
				//未用退回成功
				update.setFailCount(update.getFailCount()+1);
				//未用退回的明细中存记录表的主键id
				PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(acptId,PlOnlineAcptDetail.class);
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
				logger.info("本次未用退回数据票号为："+detail.getBillNo()+"，借据号："+detail.getLoanNo());
				update.setIds(detail.getId() + "," + update.getIds());
				logger.info("累计未用退回的id为："+update.getIds());
				pedOnlineAcptService.txStore(detail);
				
			}
			
			pedOnlineAcptService.txStore(update);
			
			if(update.getCount() == size){
				/**
				 * 批次下的数据已全部处理完成
				 * 1、更新批次状态
				 * 2、若有未用退回处理额度
				 * 3、刷新票据池额度
				 */
				//1、更新批次状态
				PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(batchId,PlOnlineAcptBatch.class);
				pedOnlineAcptService.txSyncAcptBatchStatus(batch);
				
				//2、若有未用退回处理额度
				if(update.getFailCount() != 0){
					BigDecimal totalAmt = new BigDecimal(0);//累计未用退回金额
					//有未用退回数据	得到未用退回的明细id
					String detailIds = update.getIds();
					detailIds = detailIds.substring(0, detailIds.length()-1);
					String[] str = detailIds.split(",");
					logger.info(str.toString());
					logger.info(str.length);
					for (int i = 0; i < str.length; i++) {
						logger.info("第"+i+"次-----------------------------------------------------------------");
						PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(str[i],PlOnlineAcptDetail.class);
						if(detail != null){
							logger.info("明细金额为："+detail.getBillAmt());
							totalAmt = totalAmt.add(detail.getBillAmt());
						}
					}
					
					//更新批次金额
					batch = pedOnlineAcptService.calculateBatchAmt(batch);
					ReturnMessageNew result = pedOnlineAcptService.misRepayAcptPJE028(batch);
					if(!result.isTxSuccess()){
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
						logger.error(batch.getBatchNo()+"信贷额度释放失败...");
						return resultMap;
					}
	    			pedOnlineAcptService.txStore(batch);
	    			
				}
				
				/*
				 * 若该批次下全部票据都出成功，则置换额度信息
				 */
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
				 }
				
				
				//3、刷新票据池额度
				ProtocolQueryBean queryBean = new ProtocolQueryBean();
				queryBean.setPoolAgreement(batch.getBpsNo());
				PedProtocolDto pro = (PedProtocolDto)pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
				financialService.txCreditCalculationByProtocol(pro);
				
			}
			
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);

		} catch (Exception e) {
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			logger.error("银承业务明细状态及发生未用退回时金额统计：",e);
			return resultMap;		
		}
	
		return resultMap;
	}
}
