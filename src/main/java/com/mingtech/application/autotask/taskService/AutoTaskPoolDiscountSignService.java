package com.mingtech.application.autotask.taskService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 *自动任务业务处理类
 *贴现签收记账
 *gcj 20210517
 */


public class AutoTaskPoolDiscountSignService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolDiscountSignService.class);
	PoolEcdsService poolEcdsService=PoolCommonServiceFactory.getPoolEcdsService();
	DepartmentService departmentService=DepartmentServiceFactory.getDepartmentService();
	PedProtocolService pedProtocolService= PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	DraftPoolDiscountServer draftPoolDiscountServer=PoolCommonServiceFactory.getDraftPoolDiscountServer();
	DraftPoolOutService draftPoolOutService = PoolCommonServiceFactory.getDraftPoolOutService();
	ConsignService consignService = ConsignServiceFactory.getConsignService();
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
		 * queryType 去BBSP查询是否已发起过贴现签收
		 * 若记账返回失败;查询账务(时时返回)
		 */
		if(null!=queryType){
			try{
		        PoolBillInfo bill = draftPoolInService.loadByBillNo(discount.getSBillNo(),discount.getBeginRangeNo(),discount.getEndRangeNo());
				PoolQueryBean poolQueryBean = new PoolQueryBean();
				poolQueryBean.setBillNo(bill.getSBillNo());
				
				poolQueryBean.setBeginRangeNo(bill.getBeginRangeNo());
				poolQueryBean.setEndRangeNo(bill.getEndRangeNo());
				
				poolQueryBean.setSStatusFlag(PoolComm.DS_10);
				DraftPool pool=consignService.queryDraftByBean(poolQueryBean).get(0);

				ECDSPoolTransNotes poolTrans = new ECDSPoolTransNotes();
				poolTrans.setTransNo("2");//查询类型
				poolTrans.setConferNo(discount.getSSwapContNum());//合同号
				poolTrans.setBillId(bill.getDiscBillId());//票据id
				poolTrans.setBeginRangeNo(bill.getBeginRangeNo());//子票区间起始
				poolTrans.setEndRangeNo(bill.getEndRangeNo());//子票区间截至
				poolTrans.setBillSource(bill.getDraftSource());//票据来源
				
				ReturnMessageNew resp = poolEcdsService.txApplySynchronization(poolTrans);
				if(resp.isTxSuccess()){
					List list = resp.getDetails();
					String billId = "";//票据id
					Map map = new HashMap();
						String billNo = discount.getSBillNo();
						for (int j2 = 0; j2 < list.size(); j2++) {
							map = (Map) list.get(j2);
							billId = getStringVal(map.get("BILL_MSG_ARRAY.BILL_ID"));
							if(discount.getBillinfoId().getDiscBillId().equals(billId)){
								/*	
								 * 未处理: TRAN_STATUS  1：未处理
								 * 失败：TRAN_STATUS  3签收失败；4：拒绝成功；
								 * 	1：未处理；
									2：签收成功；
									3：签收失败；
									4：拒绝成功；
									5：记账成功；（成功终态）
									6：记账失败
								 * ——BBSP系统对该接口码值作出修改                
								*/
								String status = getStringVal(map.get("BILL_MSG_ARRAY.TRAN_STATUS"));
								discount = draftPoolDiscountServer.getDiscountsListByParam(null, billNo,discount.getBeginRangeNo(),discount.getEndRangeNo(),null).get(0);
								if(status.equals("1")){
									continue;//未处理状态结束循环
								}
								if(status.equals("2")){
									//签收成功 不做处理  等待bbsp 的通知
									continue;
								}
								if(status.equals("6")){
									//记账失败  查询账务(时时返回)
									
									ECDSPoolTransNotes note =new ECDSPoolTransNotes();
									note.setBillNo(bill.getSBillNo());//票号
									note.setTradeDate(DateUtils.getCurrDate());//交易日期
									response = poolEcdsService.txApplyQueryAcctStatus(note);
									String acctFlag = (String) response.getBody().get("ACCOUNT_STATUS");//记账状态
									if(!acctFlag.equals("1")){
										//记账失败
										discount.setSBillStatus(PoolComm.TX_02);//失败 →	回退
										discount.setAccountStatus(status);//记账失败
										discount.setLastOperTm(new Date());
										discount.setLastOperName("强贴自动任务,记账失败");
										discount.setReTranstatus(getStringVal(map.get("BILL_MSG_ARRAY.TRAN_STATUS")));//报文交易状态
										discount.setReTranmsg(getStringVal(map.get("BILL_MSG_ARRAY.TRAN_MSG")));//交易描述
										draftPoolDiscountServer.txStore(discount);
										this.response(Constants.TX_FAIL_CODE, "贴现记账失败", response, ret);
									}
								}
								if(status.equals("5")){
									discount.setSBillStatus(PoolComm.TX_05);//成功	记账成功
									discount.setAccountStatus(status);//记账成功
									discount.setLastOperTm(new Date());
									discount.setLastOperName("强贴自动任务,记账成功");
									pool.setAssetStatus(PoolComm.DS_11);//贴现已完成
									bill.setSDealStatus(PoolComm.DS_11);//贴现已完成
									pool.setLastOperTm(new Date());
									pool.setLastOperName("强贴自动任务,贴现签收及记账申请结果查询");
									bill.setLastOperTm(new Date());
									bill.setLastOperName("强贴自动任务,贴现签收及记账申请结果查询");
									//this.response(Constants.TX_FAIL_CODE, "贴现记账失败", response, ret);


								}
								discount.setReTranstatus(getStringVal(map.get("BILL_MSG_ARRAY.TRAN_STATUS")));//报文交易状态
								discount.setReTranmsg(getStringVal(map.get("BILL_MSG_ARRAY.TRAN_MSG")));//交易描述
								draftPoolDiscountServer.txStore(discount);
								draftPoolDiscountServer.txStore(pool);
								draftPoolDiscountServer.txStore(bill);
							}
						}
						
				
				
			}

			}catch(Exception e){
				this.response(Constants.TX_FAIL_CODE, "贴现签收记账查询失败", response, ret);
				return response;
			}finally{
				
			}
		}

		PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, discount.getBpsNo(), null, null, null);
		
		
		PoolBillInfo bill = draftPoolInService.loadByBillNo(discount.getSBillNo(),discount.getBeginRangeNo(),discount.getEndRangeNo());

		PoolQueryBean poolQueryBean = new PoolQueryBean();
		poolQueryBean.setBillNo(bill.getSBillNo());
		
		poolQueryBean.setBeginRangeNo(bill.getBeginRangeNo());
		poolQueryBean.setEndRangeNo(bill.getEndRangeNo());
		
		poolQueryBean.setSStatusFlag(PoolComm.DS_10);
		DraftPool pool=consignService.queryDraftByBean(poolQueryBean).get(0);
		ECDSPoolTransNotes poolTrans = new ECDSPoolTransNotes();
		
		poolTrans.setConferNo(discount.getSSwapContNum());//合同号
		poolTrans.setBillType(discount.getBillType());//票据类型  
		poolTrans.setInAcctBranch(discount.getSIntAccount());//业务发起机构
		poolTrans.setIouNo(discount.getSJjh());//借据号
		poolTrans.setCustNo(dto.getAccountManagerId());//客户经理号     
		poolTrans.setDiscountDate(DateUtils.toString(discount.getDiscBatchDt(),"yyyyMMdd"));//贴现日 
		poolTrans.setExeIntRate(discount.getFRate().toString());//贴现利率
		poolTrans.setCllentNo(discount.getCustNo());//客户号   
		poolTrans.setAcctNo(bill.getAccNo());//客户账号     
		poolTrans.setCllentNoName(dto.getCustname());//客户名称 
		poolTrans.setEnterAcctNo(discount.getInAccountNum());//入账账号      
		poolTrans.setEnterBankCode(discount.getInAccountBankNum());//入账行号    
		poolTrans.setMarginAccount(discount.getMarginAccount());//保证金账号
		poolTrans.setIfInPool("1");//强贴标志  0否 1是
		poolTrans.setRemitterBatch(dto.getCreditDeptNo());//账务机构
	    poolTrans.setGuaranteeNo(discount.getGuaranteeNo());//担保编号
	    poolTrans.setPayIntMode(discount.getSIntPayway());//付息方式
	    poolTrans.setCancelAccNo(discount.getCancelAcctNo());//销账编号
	    
	    if(discount.getSIntPayway().equals("3")){//协议付息
	    	poolTrans.setThirdPayRate(discount.getThirdPayRate().toString());//付息比例
	    	poolTrans.setThirdAcctNo(discount.getThirdAcctNo());//付息账号
	    	poolTrans.setThridOpenBranch(discount.getThridOpenBranch());//付息行号
	    	poolTrans.setThridAcctName(discount.getThridAcctName());//付息名称
	    }
	    poolTrans.setLoanAcctNoName(discount.getLoanerAcctName());//放款账号名称
	    poolTrans.setOrgNo(discount.getOrgCode());//组织机构代码
		
		/**
		 * 票据信息数组需传送的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("BILL_INFO_ARRAY.IOU_NO",discount.getSJjh());//借据编号            
		infoMap.put("BILL_INFO_ARRAY.EXPEND_DETAIL_SEQ_NO",discount.getSJjh());//出账清单流水号      
		infoMap.put("BILL_INFO_ARRAY.TRAN_ID",discount.getTransId());//交易ID              
		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",discount.getHilrId());//持票编号            
		infoMap.put("BILL_INFO_ARRAY.BILL_ID",bill.getDiscBillId());//票据编号            
		infoMap.put("BILL_INFO_ARRAY.BILL_NO",discount.getSBillNo());//票据（包）号码      
		infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",discount.getBeginRangeNo());//子票区间起始        
		infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",discount.getEndRangeNo());//子票区间截止        
		infoMap.put("BILL_INFO_ARRAY.BILL_AMT",discount.getFBillAmount());//票据(包)金额        
		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",discount.getDraftSource());//票据来源            
		infoMap.put("BILL_INFO_ARRAY.DRAW_BILL_DATE",DateUtils.toString(pool.getPlIsseDt(),"yyyyMMdd"));//出票日期            
		infoMap.put("BILL_INFO_ARRAY.EXPIRY_DATE",DateUtils.toString(pool.getPlDueDt(),"yyyyMMdd"));//到期日              
		infoMap.put("BILL_INFO_ARRAY.DRAWER_NAME","");//出票人名称          
		infoMap.put("BILL_INFO_ARRAY.DRAWER_ACCT_NO","");//出票人账号          
		infoMap.put("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NO","");//出票人开户行行号    
		infoMap.put("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NAME","");//出票人开户行行名    
		infoMap.put("BILL_INFO_ARRAY.PAYEE_NAME","");//收款人名称          
		infoMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO","");//收款人账号          
		infoMap.put("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NO","");//收款人开户行行号    
		infoMap.put("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NAME","");//收款人开户行行名    
		infoMap.put("BILL_INFO_ARRAY.PAYER_BANK_NO","");//付款行行号          
		infoMap.put("BILL_INFO_ARRAY.PAYER_BANK_NAME","");//付款行行名          
		infoMap.put("BILL_INFO_ARRAY.PAYER_BANK_ADDR","");//付款行地址          
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_NAME",discount.getSAcceptor());//承兑人名称          
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO",discount.getSAgcyAccNo());//承兑人账号          
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO",discount.getSAgcysvcr());//承兑人开户行行号    
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME","");//承兑人开户行行名    
		infoMap.put("BILL_INFO_ARRAY.ACCEPTANCE_AGREE_NO","");//承兑协议编号        
		infoMap.put("BILL_INFO_ARRAY.OWN_BANK_ACCE_FLAG","");//是否我行承兑        
		infoMap.put("BILL_INFO_ARRAY.SC_DFC_FLAG",discount.getSIfSameCity());//是否同城标识        
		infoMap.put("BILL_INFO_ARRAY.DEFER_DAYS",discount.getFIntDays());//顺延天数            
		infoMap.put("BILL_INFO_ARRAY.INTEREST_DAYS",discount.getFIntDays());//利息计算天数(加息天数)        
		infoMap.put("BILL_INFO_ARRAY.INTEREST",BigDecimalUtils.getStringValue(discount.getIntRecvAmt()));//利息                
		infoMap.put("BILL_INFO_ARRAY.SELLER_INTEREST","");//卖方利息            
		infoMap.put("BILL_INFO_ARRAY.BUYER_INTEREST","");//买方利息            
		infoMap.put("BILL_INFO_ARRAY.THIRD_INTEREST","");//第三方利息          
		infoMap.put("BILL_INFO_ARRAY.REAL_PAY_AMT",discount.getFPayment() == null ? 0 : discount.getFPayment());//实付金额            
		infoMap.put("BILL_INFO_ARRAY.INTERES_EXPIRY_DATE","");//计息到期日          
		infoMap.put("BILL_INFO_ARRAY.ALREADY_QUERY_CHECK_FLAG","0");//是否已查询查复      
		infoMap.put("BILL_INFO_ARRAY.QUERY_CHECK_TYPE","");//查询查复类型        
		infoMap.put("BILL_INFO_ARRAY.DEPOSIT_AMT","");//保证金金额          
		infoMap.put("BILL_INFO_ARRAY.INTEREST_FLAG","1");//是否票据系统计算利息
		infoMap.put("BILL_INFO_ARRAY.DISCOUNT_INT_RATE",discount.getExeIntRate());//贴现利率            
		infoMap.put("BILL_INFO_ARRAY.DISCOUNT_INT_RATE_TYPE","360");//贴现利率类型        
		infoMap.put("BILL_INFO_ARRAY.ASSET_TYPE","");//资产分类            
		infoMap.put("BILL_INFO_ARRAY.OUT_EXT_INFO","");//扩展字段(对外)      

		poolTrans.getDetails().add(infoMap);
		
		
		ReturnMessageNew res = poolEcdsService.txApplySignBookkeep(poolTrans);
		
		if(res.isTxSuccess()){
			Map map = res.getBody();
			String billId = getStringVal(map.get("BILL_ID"));
			logger.info("对象票据id["+bill.getDiscBillId()+"],报文返回票据ID["+billId+"]");
//			if(bill.getDiscBillId().equals(billId)){
				logger.info("相同用来改变状态 ");
				discount.setSBillStatus(PoolComm.TX_04);
				discount.setLastOperTm(new Date());
				discount.setLastOperName("强贴自动任务,贴现签收及记账申请");
				pedProtocolService.txStore(discount);
				this.response(Constants.TX_SUCCESS_CODE, "强贴自动任务,贴现签收及记账申请成功", response, ret);

//			}
		}else{
			this.response(Constants.TX_FAIL_CODE, "票号："+discount.getSBillNo()+"强贴自动任务,贴现签收及记账申请失败", response, ret);
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
