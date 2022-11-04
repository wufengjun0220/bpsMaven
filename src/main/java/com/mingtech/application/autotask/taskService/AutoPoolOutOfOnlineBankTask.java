package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;


/**
 * 解质押后续操作自动任务
 * ①查询解质押后期操作表(PL_BATCH_INFO),处理标志为未处理,并且都已出池完成的票
 * ②根据出池模式调用相应的接口
 * @author wu fengjun
 *
 */
public class AutoPoolOutOfOnlineBankTask extends AbstractAutoTask{

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	private static final Logger logger = Logger.getLogger(AutoPoolOutOfOnlineBankTask.class);
	DraftPoolDiscountServer discountServer = PoolCommonServiceFactory.getDraftPoolDiscountServer();
	PoolCreditService creditService = PoolCommonServiceFactory.getPoolCreditService();
	DraftPoolOutService draftPoolOutService = PoolCommonServiceFactory.getDraftPoolOutService();
	PoolEcdsService poolEcdsService = PoolCommonServiceFactory.getPoolEcdsService();
	PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	DepartmentService departmentService = DepartmentServiceFactory.getDepartmentService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();
	
	@Override
	public BooleanAutoTaskResult run() throws Exception {
		
		List<PlBatchInfo> infoList = discountServer.getBatchInfoByParam("0", null);//未处理的批次
		if(infoList != null && infoList.size() > 0){
			PlBatchInfo info  = null;
			
			for (int i = 0; i < infoList.size(); i++) {
				info = infoList.get(i);
				PoolQueryBean pq = new PoolQueryBean();
				pq.setSBatchNo(info.getDoBatchNo());//批次号
				pq.setProtocolNo(info.getBpsNo());//票据池编号
				List<DraftPool> draftList = creditService.queryDraftInfos(pq, null);
				//出池模式	质押出池
				if(info.getOutMode().equals("CCMS_01")){
					boolean flag = true;
					for (int j = 0; j < draftList.size(); j++) {
						if(!draftList.get(j).getAssetStatus().equals(PoolComm.DS_04)){
							flag = false;
							break;
						}
					}
					try {
						if(flag){
							sendPoolOutMsg(draftList, info);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						continue;
					}
				}
				//出池模式		贴现出池
				if(info.getOutMode().equals("CCMS_02")){
					boolean flag = true;
					for (int j = 0; j < draftList.size(); j++) {
						if(!draftList.get(j).getAssetStatus().equals(PoolComm.DS_04)){
							flag = false;
							break;
						}
					}
					try {
						if(flag){
							sendDiscountMsg(draftList, info);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						continue;
					}
				}
				//出池模式		背书出池
				if(info.getOutMode().equals("CCMS_03")){
					boolean flag = true;
					if(null != draftList && draftList.size()>0){						
						for (int j = 0; j < draftList.size(); j++) {
							if(!draftList.get(j).getAssetStatus().equals(PoolComm.DS_04)){
								flag = false;
								break;
							}
						}
						try {
							if(flag){
								sendendorseeMsg(draftList, info);
							}
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
							continue;
						}
					}

				}
			}
		}
		
		return new BooleanAutoTaskResult(true);
	}

	//质押出池
	public void sendPoolOutMsg(List<DraftPool> draftPools ,PlBatchInfo info) throws Exception{
		logger.info("解质押后续操作自动任务:质押出池");
		DraftPool pool = null;
		PedProtocolDto dto = null;
		if(draftPools != null && draftPools.size() > 0){
			for (int i = 0; i < draftPools.size(); i++) {
				pool = draftPools.get(i);
				dto = pedProtocolService.queryProtocolDto( null, null,pool.getPoolAgreement(), null, null, null);
				
				PoolBillInfo billInfo = draftPoolOutService.loadByBillNo(pool.getAssetNb(), pool.getBeginRangeNo(), pool.getEndRangeNo());
				
				ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
				
				
				/**
				 * body内需要传送的值
				 */
				poolTransNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
				//若电子签名为空设置为0
				poolTransNotes.setSignature(info.getESign());//电子签名
				
				/**
				 * 票据信息数组需传送的值
				 */
				Map infoMap = new HashMap();
				infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",pool.getDraftSource());//票据来源 
				infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0092000);//交易编号  质押申请
				infoMap.put("BILL_INFO_ARRAY.BILL_NO",pool.getAssetNb());//票据（包）号码
				infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",pool.getHilrId());//持票id
				infoMap.put("BILL_INFO_ARRAY.BILL_ID",pool.getPoolBillInfo().getDiscBillId());//票据id
				infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",pool.getBeginRangeNo());//子票区间起始
				infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",pool.getEndRangeNo());//子票区间截至
				infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","1");//渠道来源  3-票据池
				infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
				if(pool.getDraftSource().equals(PoolComm.CS01)){
					infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型1：申请、0：撤销
				}
				
				String seq = poolBatchNoUtils.txGetFlowNo();
				infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
				
				poolTransNotes.getDetails().add(infoMap);

				/**
				 * 质押信息数组需传送的值
				 */
				Map pledgeMap = new HashMap();
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_DATE",DateUtils.toString(new Date(),"yyyyMMdd"));//质权日期 必输
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_NAME",info.getPledgeeName());//质权人名称 必输
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_NO",info.getPledgeeAcctNo());//质权人账号 必输
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_NAME",info.getPledgeeName());//质权人账户名称
				//质权人用融资机构信息	若果有融资机构号拿融资机构号,若没有拿受理网点
				String orgNo = "10000";
				if(StringUtil.isNotBlank(dto.getCreditDeptNo())){
					orgNo = dto.getCreditDeptNo();
				}else {
					orgNo = dto.getOfficeNet();
				}
				logger.info("根据机构号["+orgNo+"]查询机构信息开始");
				Department ment = departmentService.queryByInnerBankCode(orgNo);
				if(ment!=null){
					logger.info("查询部门信息结束,质权人开户行号为["+ment.getBankNumber()+"],质权人开户行名称为["+ment.getName()+"]");
					pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_OPEN_BANK_NO",ment.getPjsBrchNo());//质权人开户行行号
					poolTransNotes.setReceiverBankNo(ment.getPjsBrchNo());//质权人开户行行号
				}else {
					logger.info("未查询到部门信息");
				}
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_OPEN_BANK_NAME","");//质权人开户行行名
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_TRANSACT_CHANNEL_NO","");//质权人业务办理渠道代码 3-票据池
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_TYPE","");//质权人识别类型
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.INPOOL_FLAG","0");//入池标志 必输
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.LOCK_FLAG","");//锁定标志
				poolTransNotes.getDetails().add(pledgeMap);
				
				//2.调用质押申请接口
				logger.info("票号为["+pool.getAssetNb()+"]的票,发送质押申请开始");
				ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
				if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					Map map = resp.getBody();
					pool.setHilrId(getStringVal(map.get("hilrId")));//持票ID
					
					info.setDoFlag(PoolComm.DO_01);
					pedProtocolService.txStore(info);
					logger.info("发送质押申请结束");
				}
			}
			
		}
	}
	//贴现出池
	public void sendDiscountMsg(List<DraftPool> draftPools, PlBatchInfo info) throws Exception{
		logger.info("解质押后续操作自动任务:贴现出池");
		DraftPool pool = null;
		for (int i = 0; i < draftPools.size(); i++) {

			pool = draftPools.get(i);
			
			PoolBillInfo billInfo = draftPoolOutService.loadByBillNo(pool.getAssetNb(),pool.getBeginRangeNo(),pool.getEndRangeNo());
			ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
			
			/**
			 * body内需要传送的值
			 */
			poolTransNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
			//若电子签名为空设置为0
			poolTransNotes.setSignature(pool.getElsignature());//电子签名
			
			/**
			 * 票据信息数组需传送的值
			 */
			Map infoMap = new HashMap();
			infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",pool.getDraftSource());//票据来源 
			infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0072000);//交易编号  贴现申请
			infoMap.put("BILL_INFO_ARRAY.BILL_NO",pool.getAssetNb());//票据（包）号码
			infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",billInfo.getHilrId());//持票id
			infoMap.put("BILL_INFO_ARRAY.BILL_ID",billInfo.getDiscBillId());//票据id
			infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",pool.getBeginRangeNo());//子票区间起始
			infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",pool.getEndRangeNo());//子票区间截至
			infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
			infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
			if(pool.getDraftSource().equals(PoolComm.CS01)){
				infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型1：申请、0：撤销
			}
			
			String seq = poolBatchNoUtils.txGetFlowNo();
			infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
			
			poolTransNotes.getDetails().add(infoMap);

			/**
			 * 贴现信息数组需传送的值
			 * 结算方式 必输  没值
			 */
			Map discountMap = new HashMap();
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_TYPE","RM00");//贴现类型 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_INT_RATE",info.getDiscountIntRate().setScale(6, BigDecimal.ROUND_UP).toString());//贴现利率 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_INT_RATE_TYPE","");//贴现利率类型
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.REAL_PAY_AMT",pool.getAssetAmt());//贴现实付金额 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_DATE",DateUtils.toString(info.getDiscountDate(),"yyyyMMdd"));//贴现日 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_BANK_NAME",info.getDiscountInBankName());//贴入行名 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_PERSON_NAME",info.getDiscountInBankName());//贴入人名 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_BANK_NO",info.getDiscountInBankCode());//贴入人行号 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_ACCT_NO","0");//贴入行账号 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_ACCT_NO",info.getEnterAcctNo());//入账账号 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_ACCT_NAME",pool.getCustName());//入账账号名称 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_BANK_NO",info.getEnterBankCode());//入账行号 必输
			if(pool.getPoolBillInfo().getSBanEndrsmtFlag().equals("0")){
				//可转让
				discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.UNENDORSE_FLAG","EM00");//禁止背书标记 必输EM00可再转让   EM01不得转让
			}else{
				discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.UNENDORSE_FLAG","EM01");//禁止背书标记 必输EM00可再转让   EM01不得转让
			}
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.LOCK_FLAG","0");//锁票标志
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.FORCE_DICOUNT_FLAG","");//强制贴现标志
			if(pool.getDraftSource().equals(PoolComm.CS02)){
				discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","ST02");//结算方式 必输
			}else{
				if(info.getOnlineSettleFlag().equals("0")){
					//线下清算  SM01线下清算
					discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","SM01");//结算方式 必输
				}else{
					//SM00线上清算 
					discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","SM00");//结算方式 必输
				}
			}
			
			poolTransNotes.getDetails().add(discountMap);
			
			
			/*poolTransNotes.setPayIntMode(info.getDiscountMode());//贴现方式
			poolTransNotes.setRedeemOpemDate(DateUtils.toString(info.getRedeemOpenDate(),"yyyyMMdd"));//赎回开放日期
			poolTransNotes.setRedeenEndDate(DateUtils.toString(info.getRedeemEndDate(),"yyyyMMdd"));//赎回截止日期
			poolTransNotes.setRedeemIntRate(info.getRedeemIntRate().setScale(2, BigDecimal.ROUND_UP).toString());//赎回利率
			*/
			
			//2.调用贴现申请接口
			logger.info("票号为["+pool.getAssetNb()+"]的票,发送贴现申请开始");
			ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				info.setDoFlag(PoolComm.DO_01);
				pedProtocolService.txStore(info);
				//发送贴现申请,成功改变贴现状态
			}
		}
		
		
	}
	//背书出池
	public void sendendorseeMsg(List<DraftPool> draftPools ,PlBatchInfo info) throws Exception{
		logger.info("解质押后续操作自动任务:背书出池");
		if(draftPools != null && draftPools.size() >0 ){
			DraftPool pool = null;
			for (int i = 0; i < draftPools.size(); i++) {
				pool = draftPools.get(i);
				ECDSPoolTransNotes poolNotes =new ECDSPoolTransNotes();
				
				/**
				 * body内需要传送的值
				 */
				poolNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
				//若电子签名为空设置为0
				poolNotes.setSignature(pool.getElsignature());//电子签名
				
				/**
				 * 票据信息数组需传送的值
				 */
				Map infoMap = new HashMap();
				infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",pool.getDraftSource());//票据来源 
				infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0062000);//交易编号 背书申请
				infoMap.put("BILL_INFO_ARRAY.BILL_NO",pool.getAssetNb());//票据（包）号码
				infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",pool.getHilrId());//持票id
				infoMap.put("BILL_INFO_ARRAY.BILL_ID",pool.getPoolBillInfo().getDiscBillId());//票据id
				infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",pool.getBeginRangeNo());//子票区间起始
				infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",pool.getEndRangeNo());//子票区间截至
				infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
				infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
				if(pool.getDraftSource().equals(PoolComm.CS01)){
					infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型1：申请、0：撤销
				}
				
				String seq = poolBatchNoUtils.txGetFlowNo();
				infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
				
				poolNotes.getDetails().add(infoMap);

				/**
				 * 背书信息数组需传送的值
				 * 结算方式 必输  没值
				 */
				Map endorseMap = new HashMap();
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSE_DATE", DateUtils.toString(new Date(),"yyyyMMdd"));//背书日期 必输
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_NAME", info.getEndorseeName());//被背书人名称 必输
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_ACCT_NO",info.getEndorseeAcctNo());//被背书人账号 必输
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_ACCT_NAME", info.getEndorseeName());//被背书人账户名称
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_OPEN_BANK_NO", info.getEndorseeOpenBank());//被背书人开户行号
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_OPEN_BANK_NAME", "");//被背书人开户行名
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_TRANSACT_CHANNEL_NO", "");//被背书人业务办理渠道代码
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_ACCT_TYPE","");//被背书人识别类型
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.REMARK", "");//备注
				if(pool.getPoolBillInfo().getSBanEndrsmtFlag().equals("0")){
					//可转让
					endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.UNENDORSE_FLAG","EM00");//禁止背书标记 必输EM00可再转让   EM01不得转让
				}else{
					endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.UNENDORSE_FLAG","EM01");//禁止背书标记 必输EM00可再转让   EM01不得转让
				}
				
				poolNotes.getDetails().add(endorseMap);
				

				//2.调用背书申请接口
				logger.info("票号为["+pool.getAssetNb()+"]的票,发送背书申请开始");
				ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolNotes);
				if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					info.setDoFlag(PoolComm.DO_01);
					pedProtocolService.txStore(info);
				}
				
			}
		}
	}
}
