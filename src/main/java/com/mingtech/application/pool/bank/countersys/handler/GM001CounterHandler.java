package com.mingtech.application.pool.bank.countersys.handler;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.service.FinancialService;

/**
 * 保证金手工还款校验
 * @author Administrator
 *
 */
public class GM001CounterHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(GM001CounterHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private PoolCreditService poolCreditService;

	/**
	 * 
	0.支取或还借据前，
		①同步核心保证金账户
		③融资功能未开通——允许支取及还借据
		④不是该客户签约票据池的保证金账号——允许支取及还借据
		⑤支取或者还借据金额 > 还借据总额 ——不允许支取及还借据
		②若可用余额 < 支取或者还借据金额   则循环合同信息并重占
		
	1.保证金支取校验		
		②保证金冻结或者全冻结——不允许支取
		③支取金额大于可用——不允许支取
		
	2.保证金还还借据校验
		②保证金冻结或者全冻结——允许还不校验
		③大于占用的额度——不允许还
		④不是该客户的借据——不允许还
	 */
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			String custNo = getStringVal(request.getBody().get("ORG_CODE"));//核心客户号
			String transAccount = getStringVal(request.getBody().get("IOU_NO"));//业务账号，表内业务为【贷款账号】表外业务为【业务保证金账号】
			String usage = getStringVal(request.getBody().get("TRA_USAGE"));//划转用途
			String marAcc = getStringVal(request.getBody().get("DEPOSIT_ACCT_NO"));//票据池保证金账号
			BigDecimal amount = getBigDecimalVal(request.getBody().get("DRAW_AMT")) ;//支取金额
			

			PedProtocolDto dto = pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, null, null, null, marAcc) ;
			if(null == dto){
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("未查询到该客户融资票据池协议信息！");
				response.setRet(ret);
				return response;
			}
			
			
			logger.info("GM001:客户["+dto.getPoolAgreement()+"]保证金支取额度试算开始....");
			
			/*
			 * 锁AssetPool表
			 */
			AssetPool pool = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
			String apId = pool.getApId();
			boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
			if(!isLockedSucss){//加锁失败
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
				response.setRet(ret);
				return response;
			}
			
			/*
			 * 减少支取保证金后的额度，进行额度重新计算
			 */
			Ret creditCheckRet = null;
			
			if(!"01".equals(usage)){//[非]保证金还借据
				creditCheckRet = this.txCrdtCal(dto, amount,apId);
			}else{//保证金还借据
				financialService.txUpdateBailAndCalculationCredit(apId, dto);
			}
			
			/*
			 * 解锁AssetPool表，并重新计算该表数据
			 */
			pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
						
			logger.info("GM001:客户["+dto.getPoolAgreement()+"]保证金支取额度试算结束！");
			
			
			
			AssetType type = pedAssetTypeService.queryPedAssetTypeByProtocol(dto, PoolComm.ED_BZJ_HQ);
			if(type.getCrdtTotal()!=null && type.getCrdtTotal().compareTo(amount)<0){//保证金账户中的总额  < 支取 则不允许
				response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
				response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("支取金额大于保证金账户总额！");
				response.setRet(ret);
				return response;
			}
			
			if(type!=null && type.getCrdtFree().compareTo(amount)>=0){//保证金充足的情况
				
				if("01".equals(usage)){//保证金还借据
					logger.info("GM001:保证金还借据，余额充足查询借据明细信息....");
					PedCreditDetail detail = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(transAccount,null);
					
					if(detail==null ){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("非该票据池下借据信息！");
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_02);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						response.setRet(ret);
						return response;
						
					}
					if(!detail.getBpsNo().equals(dto.getPoolAgreement())){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("非该票据池下借据信息！");
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_03);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						response.setRet(ret);
						return response;
						
					}
					CreditProduct product = poolCreditService.queryCrdProByCrdtNo(detail.getCrdtNo());
					if(detail.getLoanType().equals(PoolComm.XDCP_LD)){
						if(detail.getActualAmount().multiply(new BigDecimal(product.getCcupy())).compareTo(amount)<0){
							response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
							response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("保证金还借据金额大于该借据占用金额，不允许支取！");
							response.setRet(ret);
							return response;
						}
					}else{
						if(detail.getActualAmount().compareTo(amount)<0){
							response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
							response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("保证金还借据金额大于该借据占用金额，不允许支取！");
							response.setRet(ret);
							return response;
						}
					}
					
					
					response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_00);
					response.getBody().put("AVAL_BALANCE", type.getCrdtFree());
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("保证金足额！");
					
					//期限配比模式下的校验：Ju Nana 2021-09-03 
					//票据池系统增加判断及提示语，票据池判断该还款对应的票据池融资模式为期限配比模式，原规则校验通过后，
					//还要进行期限配比规则测算，如果校验规则不通过，要反馈核心系统“该笔还款操作后将导致客户后续融资业务到期还款资金不足，是否继续”，仅作提示。
					
					List<CreditCalculation>  calcuList = financialService.queryCreditCalculationListByBpsNo(dto.getPoolAgreement());
					CreditCalculation calcu = calcuList.get(0);
					
					if(calcu.getLowRiskCredit().compareTo(amount)<0){
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_04);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("该笔还款操作后将导致客户后续融资业务到期还款资金不足，是否继续？");
					}
					
					
				}else{
					logger.info("GM001:保证金支取，余额充足查询借据明细信息....");
					
					if(dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_01)||dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_03)){//若保证金被冻结，不允许支取
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("保证金已冻结不允许支取！");
						response.setRet(ret);
						return response;
					}
					
					if(Constants.TX_FAIL_CODE.equals(creditCheckRet.getRET_CODE())){
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("保证金划转后票据池额度不足，不允许支取！");
						response.setRet(ret);
						return response;
					}
					
					response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_00);
					response.getBody().put("AVAL_BALANCE", type.getCrdtFree());
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("保证金足额！");
					response.setRet(ret);
					return response;
				}
			}else{//保证金不足的情况
				
				if("01".equals(usage)){//保证金还借据
					logger.info("GM001:保证金还借据，余额不足查询借据明细信息....");
					//得到借据信息
					PedCreditDetail detail = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(transAccount,null);
					if(detail==null ){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("非该票据池下借据信息！");
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_02);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						response.setRet(ret);
						return response;
						
					}
					if(!detail.getBpsNo().equals(dto.getPoolAgreement())){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("非该票据池下借据信息！");
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_03);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						response.setRet(ret);
						return response;
						
					}
					//得到信贷产品实体
					CreditProduct product = poolCreditProductService.queryProductByCreditNo(detail.getCrdtNo(),PoolComm.JQZT_WJQ);
					if(product==null){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("未找到该借据对应的主业务合同！");
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_02);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						response.setRet(ret);
						return response;
					}
					
					
					//判断通过条件：(1)保证金账户总额 >= 还借据支取金额  
					//             (2)还借据支取金额 （不乘系数）<= 占用金额（不乘系数）
					if(detail.getActualAmount().compareTo(amount)>=0){
						
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_00);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("保证金足额！");
						
						//期限配比模式下的校验：Ju Nana 2021-09-03 
						//票据池系统增加判断及提示语，票据池判断该还款对应的票据池融资模式为期限配比模式，原规则校验通过后，
						//还要进行期限配比规则测算，如果校验规则不通过，要反馈核心系统“该笔还款操作后将导致客户后续融资业务到期还款资金不足，是否继续”，仅作提示。
						
						List<CreditCalculation>  calcuList = financialService.queryCreditCalculationListByBpsNo(dto.getPoolAgreement());
						CreditCalculation calcu = calcuList.get(0);
						
						if(calcu.getLowRiskCredit().compareTo(amount)<0){
							response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_04);
							response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("该笔还款操作后将导致客户后续融资业务到期还款资金不足，是否继续？");
						}
						
						
					}else{
						response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
						response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("保证金还借据金额大于该借据占用金额，不允许支取！");
						response.setRet(ret);
						return response;
					}
				} 
				
				if("02".equals(usage)){//保证金支取
					response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
					response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("保证金可用余额不足！");
					response.setRet(ret);
					return response;
				}
			}	

		} catch (Exception e) {
			logger.error(e,e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG( e.getMessage());
		}
		response.setRet(ret);
		return response;
		}
	
		/**
		 * 支取额度校验：
		 * 			将支取的保证金看做是一笔同等金额的融资业务，计算额度是否充足
		 * @param pro
		 * @param transAmt
		 * @param apId
		 * @return
		 * @throws Exception
		 * @author Ju Nana
		 * @date 2021-7-11下午7:33:58
		 */
		private Ret txCrdtCal(PedProtocolDto pro ,BigDecimal transAmt , String apId) throws Exception{
			
			Ret checkRet = new Ret();
			checkRet.setRET_CODE(Constants.TX_FAIL_CODE);
			checkRet.setRET_MSG("额度不足！");
			
			String flowNo = Long.toString(System.currentTimeMillis());
			
			CreditProduct product = new CreditProduct();
			product.setId(flowNo);
			product.setCrdtNo("BZJ-"+ flowNo);
			product.setCustNo(pro.getCustnumber());
			product.setCustName(pro.getCustname());
			product.setCrdtType(PoolComm.XDCP_LD);//融资类型:用流贷
			product.setCrdtIssDt(new Date());//生效日
			product.setCrdtDueDt(new Date());//到期日
			product.setUseAmt(transAmt);//合同金额
			product.setRestUseAmt(transAmt);//需要占用的额度
			product.setCrdtStatus(PoolComm.RZCP_YQS);//业务状态   RZ_03：额度占用成功   JQ_00 已结清   存储MIS系统发过来的状态：JQ_01 取消放贷  JQ_02 手工提前终止出账   JQ_03 合同到期    JQ_04 合同终止
			product.setSttlFlag(PoolComm.JQZT_WJQ);//结清标记   JQ_00:已结清   JQ_01：未结清
			product.setCrdtBankCode(pro.getOfficeNet());//网点
			product.setRisklevel(PoolComm.LOW_RISK);//风险等级
			product.setCcupy("1");//占用比例
			product.setBpsNo(pro.getPoolAgreement());
			product.setIsOnline(PoolComm.NO);//线下
			product.setMinDueDate(new Date());//借据最早到期日
			
			/*
			 * 用信业务登记，额度占用校验
			 */
			
			 //保证金同步及额度计算及资产表重置
	        AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pro);
	        apId = ap.getApId();
	        financialService.txUpdateBailAndCalculationCredit(apId, pro);
			
			CreditRegisterCache crdtReg = creditRegisterService.createCreditRegisterCache(product, pro,apId);
			crdtReg.setFlowNo(flowNo);
			List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
			crdtRegList.add(crdtReg);
			checkRet =  financialService.txCreditUsedCheck(crdtRegList, pro,flowNo);
			
			
			return checkRet;
			
			
		}
	
		public PedProtocolService getPedProtocolService() {
			return pedProtocolService;
		}
		
		public void setPedProtocolService(PedProtocolService pedProtocolService) {
			this.pedProtocolService = pedProtocolService;
		}

}
