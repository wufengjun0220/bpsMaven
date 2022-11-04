package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 *自动任务业务处理类
 *贴现申请发送
 *gcj 20210514
 */


public class AutoTaskPoolDiscountSendService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolDiscountSendService.class);
	PoolEcdsService poolEcdsService=PoolCommonServiceFactory.getPoolEcdsService();
	DepartmentService departmentService=DepartmentServiceFactory.getDepartmentService();
	PedProtocolService pedProtocolService= PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	DraftPoolDiscountServer draftPoolDiscountServer=PoolCommonServiceFactory.getDraftPoolDiscountServer();
	DraftPoolOutService draftPoolOutService = PoolCommonServiceFactory.getDraftPoolOutService();
	ConsignService consignService = ConsignServiceFactory.getConsignService();
	AutoTaskPublishService autoTaskPublishService = PoolCommonServiceFactory.getAutoTaskPublishService();
	/**
	 * gcj 20210514
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		PlDiscount discount =draftPoolDiscountServer.loadByPlDiscountId(busiId);
		if(null==discount){
			this.response(Constants.TX_FAIL_CODE, "根据ID"+busiId+"未能找到入池业务明细实体", response, ret);
			return response;
		}
		discount.setTaskDate(new Date());
		/**
		 * queryType 去BBSP查询是否已发起过贴现申请
		 */
		if(null!=queryType){
			try{
				PoolBillInfo bill = draftPoolInService.loadByBillNo(discount.getSBillNo(),discount.getBeginRangeNo(),discount.getEndRangeNo());
				ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
				
				
				poolTrans.setAcctNo("0");//电票签约账号
				
				poolTrans.setBeginRangeNo(bill.getBeginRangeNo());//票据开始子区间号
				poolTrans.setEndRangeNo(bill.getEndRangeNo());//票据结束子区间号
				poolTrans.setDataSource("3");//渠道来源  3-票据池
				poolTrans.setTransNo(PoolComm.CBS_0022012);//交易类型集合 提示付款
				poolTrans.setBillNo(bill.getSBillNo());//票号
				poolTrans.setTransType("1");//业务类型  申请类
				
				ReturnMessageNew resp = poolEcdsService.txApplyQueryBusinessBatch(poolTrans);
				logger.info("电子交易类查询结束,返回的交易信息的笔数为["+resp.getDetails()+"]笔");
				List list = resp.getDetails();
				String billNo = "";
				String beginRangeNo = "";
				String endRangeNo = "";
					for (int j = 0; j < list.size(); j++) {
						Map map = (Map) list.get(j);
						String statusCode = getStringVal(map.get("statusCode"));//交易结果
						if(statusCode.equals("2")){//申请成功
							/**
							 *   唤醒贴现签收记账申请子任务。。。
							 */
							Map<String, String> reqParams =new HashMap<String,String>();

				        	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLDIS_SIGN_TASK_NO, discount.getId(), AutoTaskNoDefine.BUSI_TYPE_TJZ,reqParams);

							
							discount.setSBillStatus(PoolComm.TX_02);//贴现申请已成功
							discount.setLastOperTm(new Date());
							discount.setLastOperName("强贴自动任务,贴现申请已成功");
							draftPoolDiscountServer.txStore(discount);
							this.response(Constants.TX_FAIL_CODE, "贴现申请已发送，无需重复发送任务", response, ret);
							return response;
						}else if(statusCode.equals("3")){//申请失败
							discount.setSBillStatus(PoolComm.TX_06);//贴现失败
							discount.setLastOperTm(new Date());
							discount.setLastOperName("强贴自动任务,申请处理失败");
							draftPoolDiscountServer.txStore(discount);
						}
						break;
					}
			}catch(Exception e){
				this.response(Constants.TX_FAIL_CODE, "贴现申请查询失败", response, ret);
				return response;
			}finally{
				
			}
		}

		
		
		
		//贴现申请
		PoolBillInfo billInfo = draftPoolOutService.loadByBillNo(discount.getSBillNo(),discount.getBeginRangeNo(),discount.getEndRangeNo());
		ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
		DraftPool pool =consignService.getDraftPoolByParam(discount.getSBillNo(), "1");

		/**
		 * body内需要传送的值
		 */
		poolTransNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
		//若电子签名为空设置为0
		poolTransNotes.setSignature("0");//电子签名
		
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
		infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","5");//渠道来源  3-票据池
		infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
		if(pool.getDraftSource().equals(PoolComm.CS01)){
			infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型1：申请、0：撤销
		}
		poolTransNotes.getDetails().add(infoMap);

		/**
		 * 贴现信息数组需传送的值
		 */
		Map discountMap = new HashMap();
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_TYPE","RM00");//贴现类型 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_INT_RATE",BigDecimalUtils.setScale(6,discount.getFRate().divide(new BigDecimal(100) ,6, BigDecimal.ROUND_HALF_EVEN)));//贴现利率 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_INT_RATE_TYPE","");//贴现利率类型
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.REAL_PAY_AMT",BigDecimalUtils.getStringValue(discount.getFPayment()));//贴现实付金额 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_DATE",DateUtils.toString(discount.getDiscBatchDt(),"yyyyMMdd"));//贴现日 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_BANK_NAME",getStringVal(discount.getDscntRole()));//贴入行名 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_PERSON_NAME",getStringVal(discount.getDscntRole()));//贴入人名 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_BANK_NO",discount.getSIntAccount());//贴入人行号 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_ACCT_NO","0");//贴入行账号 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_ACCT_NO",pool.getAccNo());//入账账号 必输
		
		PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, pool.getPoolAgreement(), null, null, null);
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_ACCT_NAME",dto.getCustname());//入账账号名称 必输
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_BANK_NO",discount.getInAccountBankNum());//入账行号 必输
		
		if(discount.getTransSign().equals("0")){
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.UNENDORSE_FLAG","EM00");//禁止背书标记 必输
			
		}else{
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.UNENDORSE_FLAG","EM01");//禁止背书标记 必输
			
		}
		
		
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.LOCK_FLAG","0");//锁票标志 0否 1是
		discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.FORCE_DICOUNT_FLAG","1");//强制贴现标志
		if(pool.getDraftSource().equals(PoolComm.CS02)){
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","ST02");//结算方式 必输
		}else{
			//线下清算  SM01线下清算
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","SM01");//结算方式 必输
		}
		
		poolTransNotes.getDetails().add(discountMap);
		
		logger.info("票号为["+pool.getAssetStatus()+"],票据id为["+billInfo.getDiscBillId()+"]的票,发送贴现申请start");
		ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
		
		if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			List details = resp.getDetails();
			for (int i = 0; i < details.size(); i++) {
				Map map = (Map) details.get(i);
				if(map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE").equals(Constants.TX_SUCCESS_CODE)){//成功
					//发送贴现申请,成功改变贴现状态
					discount.setSBillStatus(PoolComm.TX_01);//已发贴现申请
					discount.setLastOperTm(new Date());
					discount.setLastOperName("强贴自动任务,发出贴现申请");
					pool.setAssetStatus(PoolComm.DS_10);//贴现处理中
					pool.setLastOperTm(new Date());
					pool.setLastOperName("强贴自动任务,发出贴现申请");
					billInfo.setLastOperTm(new Date());
					billInfo.setLastOperName("强贴自动任务,发出贴现申请");
					billInfo.setSDealStatus(PoolComm.DS_10);
					draftPoolOutService.txStore(pool);
					draftPoolOutService.txStore(billInfo);
					draftPoolOutService.txStore(discount);
					logger.info("发送贴现申请end");
//					PoolQueryBean pq = new PoolQueryBean();
//					pq.setSBatchNo(pool.getDoBatchNo());//批次号
//					pq.setSStatusFlag(PoolComm.DO_00);//状态 未处理
//					PlBatchInfo  plBatchInf = draftPoolOutService.getPlBatchInfoBybean(pq);
//					plBatchInf.setDoFlag(PoolComm.DO_01);//出池后续处理批次表更新
//					draftPoolOutService.txStore(plBatchInf);
					
					this.response(Constants.TX_SUCCESS_CODE, "贴现申请发送成功", response, ret);
				}
			}
			
		}else{
			this.response(Constants.TX_FAIL_CODE, "贴现申请发送失败", response, ret);

		}
		return response;
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
