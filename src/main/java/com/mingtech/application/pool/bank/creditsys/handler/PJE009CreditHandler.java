package com.mingtech.application.pool.bank.creditsys.handler;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;


/**
 * @Title: MIS接口 PJE009
 * @Description: 强制贴现申请
 * @author xie cheng
 * @date 2019-05-23
 */
public class PJE009CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE009CreditHandler.class);
	@Autowired
	private PoolCreditService poolCreditService; 
	@Autowired
	private DraftPoolDiscountServer draftPoolDiscountServer;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;

	/**
	 * MIS接口 PJE009 强制贴现申请
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		
		//申请规则：
		//（1）根据申请的票号查询贴现表判断该票据是否发起过贴现申请
		//（2）若未发起过申请（贴现表查不到数据），则直接保存数据定义初始状态，将强制贴现标识记录到大票表中
		//（3）若已发起过申请则获取贴现表中【reTransStatus】中状态，只有【贴现申请失败】、【签收记账失败】两种状态的票允许修改，其他状态均报错返回报错信息。
		//     【贴现申请失败】的票据将MIS新发过来的数据覆盖原数据，状态改为贴现申请前一状态；
		//     【签收记账失败】的票据将MIS新发过来的数据覆盖原数据，状态改为贴现签收前一状态。
		//（4）贴现额度处理
		// 因票据池系统不做申请数据录入，所以当发生异常操作时候又MIS系统续接流程                    ——2019.07.02 Ju Nana
		
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		
		try {

			String custNo = getStringVal(body.get("CORE_CLIENT_NO"));
			String bpsNo = getStringVal(body.get("BPS_NO"));
			String billNo = getStringVal(body.get("BILL_NO"));
			
			/********************融合改造新增 start******************************/
			String beginRangeNo = StringUtil.isNotEmpty(getStringVal(body.get("START_BILL_NO"))) ? getStringVal(body.get("START_BILL_NO")) : "0";
			String endRangeNo = StringUtil.isNotEmpty(getStringVal(body.get("END_BILL_NO"))) ? getStringVal(body.get("END_BILL_NO")) : "0";
			BigDecimal tradeAmt = getBigDecimalVal(body.get("TRAN_AMT"));//交易金额
			/********************融合改造新增 start******************************/
			
			logger.info("子票区间为:    "+beginRangeNo+"   -      "+endRangeNo);
			
			String marginAccount = getStringVal(body.get("DEPOSIT_ACCT_NO"));//校验保证及账号必须为票据池保证金账号
			PedProtocolDto protorol = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			if(!marginAccount.equals(protorol.getMarginAccount())){//如果回款账号与票据池保证金账号不一致，则报错
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("强制贴现申请的保证金账号与票据池保证金账号不一致！");
			}else{
				//（1）根据申请的票号查询贴现表判断该票据是否发起过贴现申请
				List<PlDiscount> discList = draftPoolDiscountServer.getDiscountsListByParam(null, billNo,beginRangeNo,endRangeNo,null);
				
				if(discList!=null && discList.size()>0){//贴现信息修改
					logger.info("可发体现的票据有["+discList.size()+"]条");
					//（3）若已发起过申请则获取贴现表中【reTransStatus】中状态，只有【贴现申请失败】、【签收记账失败】两种状态的票允许修改，其他状态均报错返回报错信息。
					PlDiscount disc = discList.get(0);
					String reTransStatus = disc.getReTranstatus();
					logger.info("bbsp返回的报文状态为["+reTransStatus+"]");
					if(reTransStatus == null || (!PoolComm.DISCOUNT_02.equals(reTransStatus)&&(!PoolComm.DISCOUNT_03.equals(reTransStatus)))){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("强制贴现申请中的非异常状态票据不允许重新发送申请！");
					}else{
						logger.info("系统工作时间为："+DateUtils.getWorkDayDate());
						if(DateUtils.checkOverLimited(disc.getDDueDt() ,DateUtils.getWorkDayDate())){
							//到期日大于当前时间才可发强贴申请
							if(PoolComm.DISCOUNT_02.equals(reTransStatus)){//签收记账失败
								disc.setReTranstatus(null);
								disc.setSBillStatus(PoolComm.TX_02);
								disc.setAccountStatus("0");//未记账
							}else if(PoolComm.DISCOUNT_03.equals(reTransStatus)){//申请失败
								disc.setReTranstatus(null);
								disc.setSBillStatus(PoolComm.TX_00);
								disc.setAccountStatus("0");//未记账
								//查draftPool对象贴现中的数据
								PoolQueryBean pq = new PoolQueryBean();
								pq.setCirStage(PoolComm.DS_10);
								pq.setRemark("1");//贴现
								pq.setBillNo(disc.getSBillNo());
								logger.info("查询DraftPool对象开始");
								List list = poolCreditService.queryDraftInfos(pq, null);
								logger.info("查询结束!");
								if(list != null && list.size() >0 ){
									logger.info("得到数据有["+list.size()+"]条");
									DraftPool dpoPool = (DraftPool) list.get(0);
									dpoPool.setAssetStatus(PoolComm.DS_04);//出池
									PoolBillInfo billInfo = dpoPool.getPoolBillInfo();
									billInfo.setSDealStatus(PoolComm.DS_04);
									dpoPool.setLastOperTm(new Date());
									dpoPool.setLastOperName("柜面出池");
									billInfo.setLastOperTm(new Date());
									billInfo.setLastOperName("柜面出池");
									poolCreditService.txStore(dpoPool);
									poolCreditService.txStore(billInfo);
								}
							}
							this.doDiscPross(body, disc);
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG(disc.getCustNo()+"强制贴现申请成功！");
						}else {
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("强制贴现申请失败!票据已至到期日不允许发贴现申请！");
						}

					}
					
				}else{//初次贴现申请	（2）若未发起过申请（贴现表查不到数据），则直接保存数据定义初始状态，将强制贴现标识记录到大票表中
					
					/**贴现表字段处理*/
					PlDiscount plDiscount = new PlDiscount();
					plDiscount.setCustNo(custNo);//核心客户号
					plDiscount.setBpsNo(bpsNo);//票据池编号
					
					PoolBillInfo poolBillInfo= draftPoolQueryService.loadByBillNo(billNo,beginRangeNo,endRangeNo);

					plDiscount.setBillType(poolBillInfo.getSBillType());
					plDiscount.setBillMedia(poolBillInfo.getSBillMedia());
					plDiscount.setDraftSource(poolBillInfo.getDraftSource());
					plDiscount.setSplitFlag(poolBillInfo.getSplitFlag());
					logger.info("系统工作时间为："+DateUtils.getWorkDayDate());
					if(DateUtils.checkOverLimited(poolBillInfo.getDDueDt() ,DateUtils.getWorkDayDate())){//到期日大于当前时间才可发强贴申请
						plDiscount.setTransSign(poolBillInfo.getSBanEndrsmtFlag());//禁止背书标记
						plDiscount.setBillinfoId(poolBillInfo);
						plDiscount.setBillMedia(poolBillInfo.getSBillMedia());
						plDiscount.setAccountStatus("0");//未记账
						plDiscount.setTaskDate(new Date());
						plDiscount.setBpsName(protorol.getPoolName());
						this.doDiscPross(body, plDiscount);
						
						/**将强制贴现标识记录到到大票表中，额度处理，将大票表及pl_pool表的状态置为出池申请*/
						PoolQueryBean queryBean = new PoolQueryBean();
						queryBean.setBillNo(poolBillInfo.getSBillNo()); 
					
						/********************融合改造新增 start******************************/
						queryBean.setBeginRangeNo(poolBillInfo.getBeginRangeNo()); 
						queryBean.setEndRangeNo(poolBillInfo.getEndRangeNo()); 
						/********************融合改造新增 start******************************/

						queryBean.setCirStage(PoolComm.DS_02);
						List <DraftPool>  draftPools = poolCreditService.queryDraftInfos(queryBean,null);
						
						if(null!=draftPools&&draftPools.size()>0){
							
							/*贴现票据额度处理及相关表的落库处理*/
							poolEBankService.txDiscountEdu(draftPools, protorol,tradeAmt,plDiscount);
							
							logger.info("强制贴现完成出池资产登记表之后，重新计算该票据池的额度，处理票据池编号【"+ protorol.getPoolAgreement()+"】");
							
							//核心同步保证金并重新计算池额度信息
							financialService.txBailChangeAndCrdtCalculation(protorol);
							
							AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(protorol);
							
							//解锁AssetPool表，并重新计算该表数据
							pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(ap.getApId(),true); 
							
							
							//生成自动任务流水记录 异步执行贴现额度校验生成出池明细 gcj 20210517
							Map<String, String> reqParams =new HashMap<String,String>();
							reqParams.put("busiId", plDiscount.getId());
							autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOLDIS_EDU_TASK_NO,plDiscount.getId(), AutoTaskNoDefine.BUSI_TYPE_TED, reqParams, plDiscount.getSBillNo(), null, null, null);
						}
						
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG(plDiscount.getCustNo()+"强制贴现申请成功！");
					}else {
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("强制贴现申请失败!票据已至到期日不允许发贴现申请！");
					}
				}
			}
			
			
		} catch (Exception e) {
			logger.error("PJE009-强制贴现申请异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("强制贴现申请!票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}
	
	/**
	 * 贴现表落库操作
	 * @Description TODO
	 * @author Ju Nana
	 * @param body
	 * @param plDiscount
	 * @throws Exception
	 * @date 2019-7-2上午9:05:28
	 */
	private void doDiscPross(Map body,PlDiscount plDiscount) throws Exception{
		
		plDiscount.setElecAccNo(getStringVal(body.get("ACCT_NO")));//账号
		plDiscount.setFRate(getBigDecimalVal(body.get("DISCOUNT_INT_RATE")));//贴现利率
		plDiscount.setDiscBatchDt(getDateVal(body.get("DISCOUNT_DATE")));//贴现日期
		plDiscount.setSIntAccount(getStringVal(body.get("DISCOUNT_IN_BANK_CODE")));//贴入行行号
		plDiscount.setDscntRole(getStringVal(body.get("DISCOUNT_IN_BANK_NAME")));//贴入行名称
		plDiscount.setSIntName(getStringVal(body.get("DISCOUNT_IN_PERSON_NAME")));//贴入人名称
		plDiscount.setDiscInAccount(getStringVal(body.get("DISCOUNT_IN_ACCT_NO")));//贴入账号
		plDiscount.setInAccountNum(getStringVal(body.get("ENTER_ACCT_NO")));//入账账号
		plDiscount.setInAccountBankNum(getStringVal(body.get("ENTER_BANK_CODE")));//入账行号
		plDiscount.setDiscBatchId(getStringVal(body.get("BATCH_NO")));//批次号
		plDiscount.setFPayment(getBigDecimalVal(body.get("REAL_PAY_AMT")));//实付金额
		plDiscount.setSJjh(getStringVal(body.get("IOU_NO")));//借据号
		plDiscount.setSBillNo(getStringVal(body.get("BILL_NO")));//票据号码
		plDiscount.setBeginRangeNo(getStringVal(body.get("START_BILL_NO")));//票据号起
		plDiscount.setEndRangeNo(getStringVal(body.get("END_BILL_NO")));//票据号止
		plDiscount.setTradeAmt(getBigDecimalVal(body.get("TRAN_AMT")));//交易金额
		plDiscount.setStandardAmt(new BigDecimal(0.01));
		
		plDiscount.setDiscBillId(getStringVal(body.get("BILL_ID")));//票据id
		plDiscount.setSBranchId(getStringVal(body.get("DRAWER_BRANCH_ID")));//出票机构
		plDiscount.setSAgcyAccNo(getStringVal(body.get("ACCEPTOR_ACCT_NO")));//承兑人账号
		plDiscount.setSAcceptor(getStringVal(body.get("ACCEPTOR_ACCT_NAME")));//承兑人名称
		plDiscount.setSAgcysvcr(getStringVal(body.get("ACCEPTANCE_BANK_ID")));//承兑行行号
		plDiscount.setDIssueDt(getDateVal(body.get("DRAW_DATE")));//出票日期
		plDiscount.setDDueDt(getDateVal(body.get("EXPIRY_DATE")));//到期日期
		plDiscount.setSIfSameCity(getStringVal(body.get("SC_DFC_FLAG")));//同城/异地标志
		plDiscount.setFIntDays(getStringVal(body.get("ADD_INTEREST_TIME_DAYS")));//加息天数
		plDiscount.setCyy("01");//币种
		plDiscount.setFBillAmount(getBigDecimalVal(body.get("BILL_AMT")));//票据金额
		plDiscount.setSIntPayway(getStringVal(body.get("DISCOUNT_PAY_INT_MODE")));//贴现付息方式
		if(getStringVal(body.get("DISCOUNT_PAY_INT_MODE")).equals("1")){
			//卖方付息
			plDiscount.setSIntPayway("2");//贴现付息方式
		}else{
			//协议付息
			plDiscount.setSIntPayway("3");//贴现付息方式
		}
		
		plDiscount.setExeIntRate(getBigDecimalVal(body.get("EXE_INT_RATE")));//执行利率
		plDiscount.setIntRecvAmt(getBigDecimalVal(body.get("INT_RECV_AMT")));//应收利息
		plDiscount.setGuaranteeNo(getStringVal(body.get("GUARANTEE_NO")));//担保品编号
		plDiscount.setAddInterestAcctNo(getStringVal(body.get("ADD_INTEREST_ACCT_NO")));//付息账号
		plDiscount.setAddInterestAcctNoName(getStringVal(body.get("ADD_INTEREST_ACCT_NAME")));//付息账户名称
		plDiscount.setCancelAcctNo(getStringVal(body.get("CANCEL_ACCT_NO")));//销账编号
		plDiscount.setSSwapContNum(getStringVal(body.get("CONTRACT_NO")));//合同号
		plDiscount.setLoanAcctNo(getStringVal(body.get("LOAN_ACCT_NO")));//放款账号
		plDiscount.setLoanerAcctName(getStringVal(body.get("LOANER_ACCT_NAME")));//放款账户名称
		plDiscount.setLoanOpenBank(getStringVal(body.get("LOAN_OPEN_BANK")));//放款账户开户行行号
		plDiscount.setOrgCode(getStringVal(body.get("ORG_CODE")));//组织机构代码
		plDiscount.setAuditStatus(getStringVal(body.get("AUDIT_STATUS")));//审核状态
		plDiscount.setMarginAccount(getStringVal(body.get("DEPOSIT_ACCT_NO")));//保证金账号

		plDiscount.setThirdPayRate(getBigDecimalVal(body.get("THIRD_PAY_RATE")));
		plDiscount.setThirdAcctNo(getStringVal(body.get("THIRD_ACCT_NO")));
		plDiscount.setThridAcctName(getStringVal(body.get("THIRD_ACCT_NAME")));
		plDiscount.setThridOpenBranch(getStringVal(body.get("THIRD_OPEN_BRANCH")));
		plDiscount.setSBillStatus(PoolComm.TX_00);
		plDiscount.setTaskDate(new Date());
		poolCreditService.txStore(plDiscount);
		
	}
	
	
}
