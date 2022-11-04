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
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.base.domain.BoCcmsPartyinf;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 在线流贷受托支付支付任务
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-20
 * @copyright 北明明润（北京）科技有限责任公司
 */

public  class OnlineCrdtPayAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineCrdtPayAutoTaskDispatch.class);
	private PedOnlineCrdtService pedOnlineCrdtService =PoolCommonServiceFactory.getPedOnlineCrdtService();
	private PoolCoreService poolCoreService = PoolCommonServiceFactory.getPoolCoreService();
	private PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();
	private DepartmentService departmentService = DepartmentServiceFactory.getDepartmentService();
	private BlackListManageService blackListManageService = PoolCommonServiceFactory.getBlackListManageService();
	private FinancialAdviceService financialAdviceService = PoolCommonServiceFactory.getFinancialAdviceService();
	
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		
		try{
			
			//本次支付记录
			PlCrdtPayList pay = (PlCrdtPayList) pedOnlineCrdtService.load(autoTaskExe.getBusiId(),PlCrdtPayList.class);
			pay.setUpdateTime(new Date());
			//支付计划
			OnlineQueryBean queryBean = new  OnlineQueryBean();
			queryBean.setOnlineCrdtNo(pay.getOnlineCrdtNo());//在线流贷协议编号
			queryBean.setContractNo(pay.getContractNo());//在线流贷主业务合同号
			queryBean.setSerialNo(pay.getSerialNo());//支付计划编号
			PlCrdtPayPlan plan = pedOnlineCrdtService.queryPlCrdtPayPlanListByBean(queryBean, null).get(0);//有且只有一条
			
			//主业务合同
			PlOnlineCrdt crdt = pedOnlineCrdtService.queryonlineCrdtByContractNo(pay.getContractNo());
			
			//在线流贷协议
			PedOnlineCrdtProtocol protocol = pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(plan.getOnlineCrdtNo());
			
			if(PublicStaticDefineTab.PAY_PLAN_02.equals(plan.getStatus())){
				
				BigDecimal canPay = plan.getTotalAmt().subtract(plan.getUsedAmt()).subtract(plan.getRepayAmt());//支付计划总金额 - 已支付金额 - 取消支付金额
				
				if(pay.getPayAmt().compareTo(canPay)>0){//本次支付金额大于圈存金额
					
					//支付失败
					this.txPayFail(pay, "支付失败，本次支付金额【"+pay.getPayAmt()+"】大于可支付金额【"+canPay+"】");
					
				}else{//支付
					String isLocal = plan.getIsLocal();
					
					if(PoolComm.NO.equals(isLocal)){//跨行--智汇宝
						
						//跨行支付先解圈存，后调用智慧宝，若失败，则重新圈存，注意重新圈存时候更新圈存编号
						this.txPayByZHB(crdt, pay, plan, protocol);
						
					}else{//本行--核心转账
						
						//本行支付，解圈存，调转账划款接口，则重新圈存，注意重新圈存时候更新圈存编号
						this.txPayByCore(crdt, pay, plan);
					}
				}
			}else{
				
				//支付失败
				this.txPayFail(pay,"支付失败，该笔支付信息对应的支付计划不是P02未完成状态");
			}

			
		}catch (Exception e) {
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
			logger.error("在线流贷申请主任务OnlineCrdtAutoTaskDispatch调度执行异常...",e);
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		return resultMap;
	}
	
	/**
	 * 通过智汇宝转账
	 * @param crdt
	 * @param pay
	 * @param plan
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-20下午9:23:18
	 */
	private void txPayByZHB(PlOnlineCrdt crdt,PlCrdtPayList pay,PlCrdtPayPlan plan,PedOnlineCrdtProtocol protocol) throws Exception{
		
		logger.info("智汇宝转账处理开始，转账在线流贷编号【"+pay.getOnlineCrdtNo()+"】主业务合同号【"+pay.getContractNo()+"】支付计划序列号【"+pay.getSerialNo()+"】转账金额【"+pay.getPayAmt()+"】");

		//跨行支付先解圈存，后调用智慧宝，若失败，则重新圈存，注意重新圈存时候更新圈存编号
		
		if(StringUtil.isNotBlank(pay.getPayReqSerNo())){//若记账流水号不为空，则先调用查证接口
			
			logger.info("智汇宝转账处理,原流水号不为空，查证...");
			
			//查证
			CoreTransNotes checkNote = new CoreTransNotes();
			checkNote.setSerSeqNo(pay.getPayReqSerNo());//交易流水号
			ReturnMessageNew checkResponse = poolCoreService.txApplyZHS002Handler(checkNote);
			
			if(checkResponse.isTxSuccess()){//查证结果为支付成功--直接改状态为支付成功，修改plan表的金额
				
				//支付成功处理
				this.txPaySucc(pay, plan);
				
			}else{//支付失败--改状态为支付失败，重新圈存
				
				String msg = "该笔支付智汇宝划转失败";
				CoreTransNotes frozNote = new CoreTransNotes();
				frozNote.setTranAmt(pay.getPayAmt().toString());//圈存金额
				frozNote.setFundNo(crdt.getFundNo());//圈存编号
				frozNote.setUser(crdt.getBranchNo());//柜员
				frozNote.setBrcBld(crdt.getBranchNo());//机构

				ReturnMessageNew frozResponse = poolCoreService.CORE007Handler(frozNote);
				if(frozResponse.isTxSuccess()){
					String popeNo = (String)frozResponse.getBody().get("ROPE_NO");//圈存编号
					crdt.setFundNo(popeNo);
					pedOnlineCrdtService.txStore(crdt);

				}else{
					msg += "，且重新圈存失败，请尽快处理！"; 
				}
				
				//支付失败处理
				this.txPayFail(pay, msg);
			}
			
		}else{//调解圈存，成功后调支付申请
			
			logger.info("智汇宝转账处理,解圈存...");
			
			//解圈存
			ReturnMessageNew unFrozResponse = null;
			try {
				CoreTransNotes unFroznote = new CoreTransNotes();
				unFroznote.setTranAmt(pay.getPayAmt().toString());//解圈金额
				unFroznote.setFundNo(crdt.getFundNo());//圈存编号
				unFroznote.setBrcNo(protocol.getInAcctBranchNo());//机构上送入账机构
				unFrozResponse = poolCoreService.CORE008Handler(unFroznote);				
			} catch (Exception e) {
				
				//支付失败，但是支付金额增加
				logger.info("解圈存调核心系统异常:"+e);
				this.txPayFailAddRepayAmt(pay, "解圈存核心系统异常,核心异常信息："+e.getMessage(),plan);
			}

			
			if(unFrozResponse.isTxSuccess()){//解圈成功，调用智汇宝划款
				
				logger.info("智汇宝转账接口调用...");
				
				CoreTransNotes payNote = new CoreTransNotes();
				payNote.setTranAmt(pay.getPayAmt().toString());//交易金额
				String serNo = poolBatchNoUtils.txGetFlowNo();
				payNote.setLoanAcctNo(plan.getLoanAcctNo());//付款账号        
				payNote.setLoanAcctName(plan.getLoanAcctName());//付款户名        
				payNote.setDeduAcctNo(plan.getDeduAcctNo());//收款人账户
				payNote.setDeduAcctName(plan.getDeduAcctName());//收款人名称       
				payNote.setDeduBankCode(plan.getDeduBankCode());//收款人开户行行号    
				payNote.setDeduBankName(plan.getDeduBankName());//收款人开户行名称    
				payNote.setLoanBankNo(protocol.getInAcctBranchNo());//付款人开户行行号    
				payNote.setLoanBankName(protocol.getInAcctBranchName());//付款人开户行行名
				Department dept = departmentService.queryByInnerBankCode(protocol.getInAcctBranchNo());
				
				BoCcmsPartyinf inf = blackListManageService.queryByPrcptcdNo(dept.getBankNumber());
				payNote.setLoanClearBank(inf.getSubdrtbkcd());//付款清算行
				BoCcmsPartyinf inf1 = blackListManageService.queryByPrcptcdNo(plan.getDeduBankCode());
				payNote.setDeduClearBank(inf1.getSubdrtbkcd());//收款清算行
				payNote.setUsage(pay.getUsage());
				payNote.setPostscript(pay.getPostscript());

//				payNote.setUser("c"+crdt.getBranchNo());//柜员
//				payNote.setBrcBld(crdt.getBranchNo());//机构

				pay.setPayReqSerNo(serNo);
				pay.setPayAcctDate(new Date());
				List<PlCrdtPayList> list = new ArrayList<PlCrdtPayList>();
				//用事务方法保存支付流水号，防止报错回滚
				financialAdviceService.txCreateList(list);
				
				//智汇宝支付
				ReturnMessageNew zhbResponse = null ;
				try {
					
					zhbResponse = poolCoreService.txApplyZHS001Handler(payNote);				
					
				} catch (Exception e) {
					
					//调智汇宝出现异常，重新圈存
					logger.info("智汇宝转账接口调用异常:"+e);
					
					//重新圈存
					String msg = "支付失败，但是支付金额已增加，该笔支付智汇宝划转接口调用异常信息："+e ;

					CoreTransNotes frozNote = new CoreTransNotes();
					frozNote.setTranAmt(pay.getPayAmt().toString());//圈存金额
					frozNote.setFundNo(crdt.getFundNo());//圈存编号

					frozNote.setUser(crdt.getBranchNo());//柜员
					frozNote.setBrcBld(crdt.getBranchNo());//机构
					ReturnMessageNew frozResponse = null;
					
					try {						
						frozResponse = poolCoreService.CORE007Handler(frozNote);
					} catch (Exception e2) {
						msg += ",调用核心圈存异常："+ e ; 
					}
					
					if(frozResponse.isTxSuccess()){
						String popeNo = (String)frozResponse.getBody().get("ROPE_NO");//圈存编号
						crdt.setFundNo(popeNo);
						pedOnlineCrdtService.txStore(crdt);
						msg += ",调用核心圈存成功！"; 
						
					}else{
						msg += ",调用核心圈存失败:"+ frozResponse.getRet().getRET_MSG(); 
					}
					
					//解圈成功 + 智汇宝支付异常 + 圈存异常 后均走该方法
					this.txPayFailAddRepayAmt(pay, msg,plan);
				}
				
				if(zhbResponse.isTxSuccess()){//记账成功
					
					String respNo = ((String) zhbResponse.getBody().get("CORE_SEQ_NO"));
					pay.setPayRespSerNo(respNo);//记账流水
					this.txPaySucc(pay, plan);
					
				}else{
					
					logger.info("智汇宝转账失败，重新圈存...");
					
					//重新圈存
					CoreTransNotes frozNote = new CoreTransNotes();
					frozNote.setTranAmt(pay.getPayAmt().toString());//圈存金额
					frozNote.setFundNo(crdt.getFundNo());//圈存编号

					frozNote.setUser(crdt.getBranchNo());//柜员
					frozNote.setBrcBld(crdt.getBranchNo());//机构

					ReturnMessageNew frozResponse = null;
					try {						
						frozResponse = poolCoreService.CORE007Handler(frozNote);
					} catch (Exception e) {
						logger.info("调用智汇宝系统支付异常:"+e);
						
						//支付失败，支付金额增加
						this.txPayFailAddRepayAmt(pay, "调用智汇宝系统支付异常，异常信息："+e.getMessage(),plan);
						
					}
					
					if(frozResponse.isTxSuccess()){
						String popeNo = (String)frozResponse.getBody().get("ROPE_NO");//圈存编号
						crdt.setFundNo(popeNo);
						pedOnlineCrdtService.txStore(crdt);
						
						//支付失败
						this.txPayFail(pay, "支付失败，解圈存成功，调用智汇宝系统支付失败，重新圈存成功。智汇宝报错信息："+ zhbResponse.getRet().getRET_MSG());
						
					}else{
						
						//支付失败，支付金额增加
						this.txPayFailAddRepayAmt(pay, "解圈存成功，调智汇宝系统支付失败，重新圈存失败。智汇宝报错信息："+zhbResponse.getRet().getRET_MSG()+"。圈存报错信息"+frozResponse.getRet().getRET_MSG() ,plan);
					}
					
				}
				
				
			}else{//解圈失败，支付置为失败，记录失败原因
				
				//支付失败
				this.txPayFail(pay, "支付失败，核心解圈存失败："+unFrozResponse.getRet().getRET_MSG());
			}
			
			
		}
	}
	
	/**
	 * 通过本行转账接口转账
	 * @param crdt
	 * @param pay
	 * @param plan
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-20下午9:23:48
	 */
	private void txPayByCore(PlOnlineCrdt crdt,PlCrdtPayList pay,PlCrdtPayPlan plan) throws Exception{
		
		logger.info("核心转账处理开始，转账在线流贷编号【"+pay.getOnlineCrdtNo()+"】主业务合同号【"+pay.getContractNo()+"】支付计划序列号【"+pay.getSerialNo()+"】转账金额【"+pay.getPayAmt()+"】");

		//跨行支付先解圈存，后调用核心转账接口，若失败，则重新圈存，注意重新圈存时候更新圈存编号
		
		if(StringUtil.isNotBlank(pay.getPayReqSerNo())){//若记账流水号不为空，则先调用查证接口
			
			logger.info("行内转账处理,原流水号不为空，查证...");

			//查证
			CoreTransNotes transNotes = new CoreTransNotes();
			transNotes.setSerSeqNo(pay.getPayReqSerNo());//流水号
			transNotes.setAcctDate(pay.getPayAcctDate());
			ReturnMessageNew checkResponse  = poolCoreService.CORE002Handler(transNotes);
			
			if(checkResponse.isTxSuccess()){//查证返回成功--直接改状态为支付成功，修改plan表的金额
				
				//支付成功处理
				this.txPaySucc(pay, plan);
				
			}else{//支付失败--改状态为支付失败
				
				//支付失败处理
				this.txPayFail(pay, "核心转账失败");
			}
			
		}else{//调解圈存，成功后调支付申请
			
			logger.info("行内转账处理,解圈存...");
			
			//解圈存
			ReturnMessageNew unFrozResponse = null;
			try {
				CoreTransNotes unFroznote = new CoreTransNotes();
				unFroznote.setTranAmt(pay.getPayAmt().toString());//解圈金额
				unFroznote.setFundNo(crdt.getFundNo());//圈存编号
				unFroznote.setBrcNo(crdt.getBranchNo());//机构上送入账机构
				unFrozResponse = poolCoreService.CORE008Handler(unFroznote);				
			} catch (Exception e) {
				
				//支付失败，但是支付金额增加
				logger.info("解圈存调核心系统异常:"+e);
				this.txPayFailAddRepayAmt(pay, "解圈存核心系统异常,核心异常信息："+e.getMessage(),plan);
			}

			
			if(unFrozResponse.isTxSuccess()){//解圈成功，调用行内划款交易
				
				logger.info("行内转账接口调用...");
				
				CoreTransNotes transNotes = new CoreTransNotes();
				Ret ret = new Ret();
				transNotes.setTranAmt(BigDecimalUtils.getStringValue(pay.getPayAmt()));//交易金额 
				transNotes.setDrAcctNo(pay.getLoanAcctNo());//借方账号
				transNotes.setDrAcctNoName(pay.getLoanAcctName());//借方账号名称
				transNotes.setCrAcctNo(pay.getDeduAcctNo());//贷款账号
				transNotes.setCrAcctNoName(pay.getDeduAcctName());//贷款账号名称
				transNotes.setFrntDate(DateUtils.toString(new Date(), "yyyyMMdd"));//第三方日期
				String seqNo = poolBatchNoUtils.txGetFlowNo();
				
				pay.setPayReqSerNo(seqNo);
				pay.setPayAcctDate(new Date());
				List<PlCrdtPayList> list = new ArrayList<PlCrdtPayList>();
				//用事务方法保存支付流水号，防止报错回滚
				financialAdviceService.txCreateList(list);
				
				transNotes.setSerSeqNo(seqNo);//第三方流水号
				transNotes.setUser("c"+crdt.getBranchNo());//柜员
				transNotes.setBrcBld(crdt.getBranchNo());//机构
				transNotes.setRemark(pay.getUsage()+" "+pay.getPostscript());//备注
				
				//核心支付
				ReturnMessageNew transResponse = null ;
				try {
					
					transResponse = poolCoreService.doMarginWithdrawal(transNotes);			
					
				} catch (Exception e) {
					
					//调喊个你转账出现异常，重新圈存
					logger.info("行内转账接口调用异常:"+e);
					
					//重新圈存
					String msg = "支付失败，但是支付金额已增加，该笔支付行内划转接口调用异常信息："+e ;

					CoreTransNotes frozNote = new CoreTransNotes();
					frozNote.setTranAmt(pay.getPayAmt().toString());//圈存金额
					frozNote.setFundNo(crdt.getFundNo());//圈存编号

					frozNote.setUser(crdt.getBranchNo());//柜员
					frozNote.setBrcBld(crdt.getBranchNo());//机构
					ReturnMessageNew frozResponse = null;
					
					try {						
						frozResponse = poolCoreService.CORE007Handler(frozNote);
					} catch (Exception e2) {
						msg += ",调用核心圈存异常："+ e ; 
					}
					
					if(frozResponse.isTxSuccess()){
						String popeNo = (String)frozResponse.getBody().get("ROPE_NO");//圈存编号
						crdt.setFundNo(popeNo);
						pedOnlineCrdtService.txStore(crdt);
						msg += ",调用核心圈存成功！"; 
						
					}else{
						msg += ",调用核心圈存失败:"+ frozResponse.getRet().getRET_MSG(); 
					}
					
					//解圈成功 + 核心支付异常 + 圈存异常 后均走该方法
					this.txPayFailAddRepayAmt(pay, msg,plan);
				}
				
				if(transResponse.isTxSuccess()){//记账成功
					
					String respNo = ((String) transResponse.getSysHead().get("SERV_SEQ_NO"));
					pay.setPayRespSerNo(respNo);//记账流水
					this.txPaySucc(pay, plan);
					
				}else{
					
					logger.info("核心转账失败，重新圈存...");
					
					//重新圈存
					CoreTransNotes frozNote = new CoreTransNotes();
					frozNote.setTranAmt(pay.getPayAmt().toString());//圈存金额
					frozNote.setFundNo(crdt.getFundNo());//圈存编号

					frozNote.setUser(crdt.getBranchNo());//柜员
					frozNote.setBrcBld(crdt.getBranchNo());//机构

					ReturnMessageNew frozResponse = null;
					try {						
						frozResponse = poolCoreService.CORE007Handler(frozNote);
					} catch (Exception e) {
						logger.info("调用核心系统支付异常:"+e);
						
						//支付失败，支付金额增加
						this.txPayFailAddRepayAmt(pay, "调用核心系统支付异常，异常信息："+e.getMessage(),plan);
						
					}
					
					if(frozResponse.isTxSuccess()){
						String popeNo = (String)frozResponse.getBody().get("ROPE_NO");//圈存编号
						crdt.setFundNo(popeNo);
						pedOnlineCrdtService.txStore(crdt);
						
						//支付失败
						this.txPayFail(pay, "支付失败，解圈存成功，调用核心系统支付失败，重新圈存成功。智汇宝报错信息："+ transResponse.getRet().getRET_MSG());
						
					}else{
						
						//支付失败，支付金额增加
						this.txPayFailAddRepayAmt(pay, "解圈存成功，调核心系统支付失败，重新圈存失败。智汇宝报错信息："+transResponse.getRet().getRET_MSG()+"。圈存报错信息"+frozResponse.getRet().getRET_MSG() ,plan);
					}
					
				}
				
				
			}else{//解圈失败，支付置为失败，记录失败原因
				
				//支付失败
				this.txPayFail(pay, "支付失败，核心解圈存失败："+unFrozResponse.getRet().getRET_MSG());
			}
			
		}
	}
	
	
	/**
	 * 支付成功处理--记录支付流水，支付计划中的已支付金额增加
	 * @param pay
	 * @param plan
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-20下午9:19:08
	 */
	private void txPaySucc(PlCrdtPayList pay,PlCrdtPayPlan plan) throws Exception {
		
		logger.info("支付成功，支付流水、支付计划、支付历史信息落库...");
		
		pay.setUpdateTime(new Date());
		pay.setStatus(PoolComm.PAY_STATUS_01);//支付成功
		pay.setPayDesc("网银-支付成功");//支付结果说明

		
		BigDecimal newUsedAmt = plan.getUsedAmt().add(pay.getPayAmt()); 
		plan.setUsedAmt(newUsedAmt);//变更支付计划中已支付金额
		
		BigDecimal allUsed = plan.getUsedAmt().add(plan.getRepayAmt());
		if(allUsed.compareTo(plan.getTotalAmt())==0){
			plan.setStatus(PublicStaticDefineTab.PAY_PLAN_03);//支付完成
		}
		
		
		pedOnlineCrdtService.txStore(pay);
		pedOnlineCrdtService.txStore(plan);
		pedOnlineCrdtService.txSavePlCrdtPayPlanHist(plan,PoolComm.PAY_TYPE_0);//保存历史
	}
	
	/**
	 * 支付失败--记录支付流水，支付计划不变
	 * @param pay
	 * @param failMsg
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-20下午9:29:36
	 */
	private void txPayFail(PlCrdtPayList pay,String failMsg) throws Exception{
		
		logger.info("支付失败处理，失败原因：" + failMsg);
		failMsg = failMsg.length()<1000 ? failMsg : failMsg.substring(0, 1000);
		
		pay.setUpdateTime(new Date());
		pay.setStatus(PoolComm.PAY_STATUS_02);//支付失败
		pay.setPayDesc(failMsg);//支付结果说明
		
		pedOnlineCrdtService.txStore(pay);
		
	}
	
	/**
	 * 支付失败--记录支付流水，支付计划中的已支付金额增加
	 * @param pay
	 * @param failMsg
	 * @param plan
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-28下午2:19:23
	 */
	private void txPayFailAddRepayAmt(PlCrdtPayList pay,String failMsg,PlCrdtPayPlan plan) throws Exception{
		
		logger.info("支付失败但是支付金额增加的处理，失败原因：" + failMsg);
		failMsg = failMsg.length()<1000 ? failMsg : failMsg.substring(0, 1000);
		
		pay.setUpdateTime(new Date());
		pay.setStatus(PoolComm.PAY_STATUS_02);//支付失败
		pay.setPayDesc("支付失败，但是支付计划中的已支付金额已增加，失败原因："+failMsg);//支付结果说明
		
		BigDecimal newUsedAmt = plan.getUsedAmt().add(pay.getPayAmt()); 
		plan.setUsedAmt(newUsedAmt);//变更支付计划中已支付金额
		
		BigDecimal allUsed = plan.getUsedAmt().add(plan.getRepayAmt());
		if(allUsed.compareTo(plan.getTotalAmt())==0){
			plan.setStatus(PublicStaticDefineTab.PAY_PLAN_03);//支付完成
		}
				
		pedOnlineCrdtService.txStore(pay);
		pedOnlineCrdtService.txStore(plan);
		pedOnlineCrdtService.txSavePlCrdtPayPlanHist(plan,PoolComm.PAY_TYPE_0);//保存历史
		
	}


	
	
}
