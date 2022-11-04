package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.utils.AutoTaskNoDefine;

/**
 *自动任务业务处理类
 *质押入池额度占用
 *gcj 20210512
 */


public class AutoTaskPoolInEduService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolInEduService.class);
	PedProtocolService pedProtocolService= PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	PoolCreditClientService poolCreditClientService = PoolCommonServiceFactory.getPoolCreditClientService();
	AutoTaskPublishService autoTaskPublishService =PoolCommonServiceFactory.getAutoTaskPublishService();
	DraftPoolInService DraftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();

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
		Map<String, String> reqParams =new HashMap<String,String>();
		DraftPoolIn poolIn = draftPoolInService.loadByPoolInId(busiId);
		if(null==poolIn){
			this.response(Constants.TX_FAIL_CODE, "根据ID"+busiId+"未能找到入池业务明细实体", response, ret);
			return response;
		}
		PoolBillInfo bill = draftPoolInService.loadByBillNo(poolIn.getPlDraftNb(),poolIn.getBeginRangeNo(),poolIn.getEndRangeNo());

		if(null!=queryType){
			/*
			 * 查询是否占用过额度  已占用额度直接唤醒记账任务
			 */
			if(PoolComm.SP_01.equals(poolIn.getBtFlag())){
				autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLIN_ACC_TASK_NO, poolIn.getId(), AutoTaskNoDefine.BUSI_TYPE_JZ,reqParams);
				this.response(Constants.TX_SUCCESS_CODE, "票号"+poolIn.getPlDraftNb()+"已经占用过额度", response, ret);
				return response;
			}
			
		}
		/*
		 * 占用额度系统额度处理
		 */
		PedProtocolDto dto = pedProtocolService.queryProtocolDto( null, null,poolIn.getPoolAgreement(), null, null, null);
		
		String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//财票改造开关 
		if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){
			 
			response=DraftPoolInService.txMisCreditOccupy(bill, poolIn,dto);			 
		
		}else{
			
			response=this.txdoPJE012Process(poolIn, bill, dto);
			
		}
		
		//额度任务成功 唤醒记账任务
		if(response.getRet().getRET_CODE().equalsIgnoreCase(Constants.TX_SUCCESS_CODE)){			
			autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLIN_ACC_TASK_NO, poolIn.getId(), AutoTaskNoDefine.BUSI_TYPE_JZ,reqParams);
		}
		
		return response;
	}
	
	
	
	/**
	 * 额度系统占用额度
	 * @param poolIn
	 * @param poolBillInfo
	 * @param dto
	 * @throws Exception
	 * @author Ju Nana
	 * @date 20210512
	 */
	private ReturnMessageNew txdoPJE012Process(DraftPoolIn poolIn,PoolBillInfo poolBillInfo,PedProtocolDto dto) throws Exception{
		
		logger.info("入池电票"+poolIn.getPlDraftNb()+"占用额度系统额度处理开始...");
		
		Ret ret = new Ret();
		ReturnMessageNew response = new ReturnMessageNew();
		try {
			/*
			 *保贴额度占用 
			 */
			
			String media = poolIn.getPlDraftMedia();//票据介质
			String type = poolIn.getPlDraftType();//票据类型
			String billType ="";//票据种类  01 纸质银票 02 纸质商票 03电子银票  04电子商票
			if(PoolComm.BILL_MEDIA_PAPERY.equals(media) && PoolComm.BILL_TYPE_BANK.equals(type)){//纸质银票
				billType = "01";
			}
			if(PoolComm.BILL_MEDIA_PAPERY.equals(media)&&PoolComm.BILL_TYPE_BUSI.equals(type)){//纸质商票
				billType = "02";
			}
			if(PoolComm.BILL_MEDIA_ELECTRONICAL.equals(media)&&PoolComm.BILL_TYPE_BANK.equals(type)){//电子银票
				billType = "03";
			}
			if(PoolComm.BILL_MEDIA_ELECTRONICAL.equals(media)&&PoolComm.BILL_TYPE_BUSI.equals(type)){//电子商票
				billType = "04";
			}
			
			Map resuMap = new HashMap();
			List<Map> reqList = new ArrayList<Map>();//实际为单条
			CreditTransNotes creditNotes = new CreditTransNotes();
			
			resuMap.put("billNo", poolIn.getPlDraftNb());//票号                    
			/********************融合改造新增 start******************************/

			resuMap.put("beginRangeNo", poolIn.getPlDraftNb());//票据开始子区间号
			resuMap.put("endRangeNo", poolIn.getPlDraftNb());//票据结束子区间号                
			/********************融合改造新增 end******************************/
			
			resuMap.put("billsum", poolIn.getPlIsseAmt());//票面金额               
			resuMap.put("currency", "156");//币种:人民币                  
			resuMap.put("billType", billType);//票据种类              
			resuMap.put("billBusinessType", "02");//票据业务类型  01 贴现 02 质押  03 转贴现
			
			resuMap.put("customerId", "");//承兑人核心客户号    
			resuMap.put("bankId", poolIn.getPlAccptrSvcr());//承兑人二代支付系统行号  
			resuMap.put("customerName", poolIn.getPlAccptrNm());//承兑人名称        
			resuMap.put("execNominalAmount", poolIn.getPlIsseAmt());//占用名义金额 
			
			BigDecimal amt = BigDecimal.ZERO;//占用敞口金额:银票输0；商票输票面金额
			if(PoolComm.BILL_TYPE_BUSI.equals(type)){//商票
				amt = poolIn.getPlIsseAmt();
			}
			resuMap.put("execExposureAmount", amt);//占用敞口金额
			
			reqList.add(resuMap);
			creditNotes.setReqList(reqList);//上传文件
			
			response = poolCreditClientService.txPJE012(creditNotes);
			
			if(response.isTxSuccess()){
				poolIn.setBtFlag(PoolComm.SP_01);//占用成功
				poolIn.setTaskDate(new Date());
				DraftPoolInService.txSavePedGuaranteeCredit(poolIn, dto);
				draftPoolInService.txStore(poolIn);
				this.response(Constants.TX_SUCCESS_CODE, "额度占用成功", response, ret);
			}else{
				poolBillInfo.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
				poolIn.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
				poolIn.setTaskDate(new Date());
				draftPoolInService.txStore(poolIn);
				draftPoolInService.txStore(poolBillInfo);
				
				//注意：额度占用失败也返回成功，只是不产生额度
				this.response(Constants.TX_SUCCESS_CODE,"额度占用失败", response, ret);
			}
			
			
		} catch (Exception e) {
			poolBillInfo.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
			poolIn.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
			poolIn.setTaskDate(new Date());
			draftPoolInService.txStore(poolIn);
			draftPoolInService.txStore(poolBillInfo);
			
			//注意：额度占用失败也返回成功，只是不产生额度
			this.response(Constants.TX_SUCCESS_CODE,"额度系统额度占用接口异常", response, ret);
			logger.error("额度系统接口异常！",e);
		}
		
		return response;
		
	}

	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}


	
}
