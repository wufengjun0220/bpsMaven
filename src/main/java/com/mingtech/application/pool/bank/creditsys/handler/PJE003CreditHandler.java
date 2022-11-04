package com.mingtech.application.pool.bank.creditsys.handler;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;

/**
 * @author zhaoding
 * 
 * @描述：票据池的释放
 */
public class PJE003CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE003CreditHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PoolBailEduService poolBailEduService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private FinancialService financialService ;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			Map map = request.getBody();
			String creditNo = getStringVal(map.get("MIS_PRODUCT_NO"));// 主业务合同号
			CreditProduct product = poolCreditProductService.queryProductByCreditNo(creditNo,null);
			
			if(product == null){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("未找到该借据信息！");
			}else{	
				BigDecimal oldReleaseAmt = null;
				if (PoolComm.JQZT_YJQ.equals(product.getSttlFlag())) {//已结清
					ret.setRET_CODE(Constants.CREDIT_04);
					ret.setRET_MSG(product.getCustNo() + "客户" + creditNo + "主业务合同已结清，请勿重复释放！");
				} else {//未结清
					
					/*
					 * 锁AssetPool表
					 */
					PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, product.getBpsNo(), null, null, null);
					AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
					String apId = ap.getApId();
					boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
					if(!isLockedSucss){//加锁失败
						ret.setRET_CODE(Constants.CREDIT_11);
						ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
						response.setRet(ret);
						logger.info("票据池【"+dto.getPoolAgreement()+"】AssetPool表上锁！");
						return response;
					}
					
					/*
					 * 额度信息整理
					 */
					oldReleaseAmt = product.getReleaseAmt();////已还额度
					String SFLX = getStringVal(map.get("RELEASE_TYPE"));// 释放类型 01结清释放  02未用释放
					String JQBS = getStringVal(map.get("PAY_OFF_FLAG"));// 结清标识 JQ_02 手工提前终止出账   JQ_04 合同终止--全部释放额度   JQ_05 合同失效--MIS一定未出账
					
					BigDecimal ED = getBigDecimalVal(map.get("RELEASE_AMT")); // 释放金额
					BigDecimal ZYBL = getBigDecimalVal(map.get("RELEASE_RATE"));// 占用比例
					BigDecimal SFED = new BigDecimal("0.0");// 需要释放的额度
					
					if (ED != null && ZYBL != null) {
						SFED = ED.multiply(ZYBL);
					}
					
					// 比较信贷发过来的释放金额与该合同下占用金额，若大于该合同下实际占用金额，则不允许释放
					
					if (PoolComm.EDSF_01.equals(SFLX)) {// 结清释放：对应信贷业务的  JQ_03：合同到期(MIS未启用)  JQ_04：合同终止  JQ_05：合同失效--MIS一定未出账
						
						//01 需要释放该笔合同下的所有占用额度，该额度不接受信贷传过来的金额，因罚息情况信贷未考虑

						product.setCrdtStatus(JQBS);
						product.setRestReleaseAmt(product.getUseAmt().multiply(ZYBL));
						product.setSttlFlag(PoolComm.JQZT_YJQ);//已结清
						
						/*
						 * 额度释放
						 */
						creditRegisterService.txReleaseCreditByProduct(product, PoolComm.EDSF_01, null);
						
						product.setReleaseAmt(oldReleaseAmt.add(ED));
					} else if (PoolComm.EDSF_02.equals(SFLX)) {// 未用释放 JQ_02 手工提前终止出账
						product.setCrdtStatus(JQBS);
						product.setRestReleaseAmt(SFED);
						product.setSttlFlag(PoolComm.JQZT_WJQ);//未结清
						
						/*
						 * 额度释放
						 */
						creditRegisterService.txReleaseCreditByProduct(product, PoolComm.EDSF_02, ED);
						
						
						product.setReleaseAmt(oldReleaseAmt.add(ED));
					} else {
						throw new Exception("释放类型错误!");
					}
					
					/*
					 *  保存信贷产品消息
					 */
					creditRegisterService.txStore(product);

					
					/*
					 * 重新计算池额度信息
					 */
					if(PoolComm.POOL_MODEL_01.equals(dto.getPoolMode())){//总量模式
						financialService.txCreditCalculationTotal(dto);
					}else{//期限配比					
						financialService.txCreditCalculationTerm(dto);
					}
					
					
					/*
					 * 解锁AssetPool表，并重新计算该表数据
					 */
					pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
					
					
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("释放额度成功！");
				}
			}



		} catch (Exception e) {
			logger.error("PJE003释放额度失败!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("PJE003释放额度失败，票据池内部执行错误");
		}
		response.setRet(ret);
		return response;
	}

	public PedProtocolService getPedProtocolService() {
		return pedProtocolService;
	}

	public void setPedProtocolService(PedProtocolService pedProtocolService) {
		this.pedProtocolService = pedProtocolService;
	}

	public PoolCreditProductService getPoolCreditProductService() {
		return poolCreditProductService;
	}

	public void setPoolCreditProductService(PoolCreditProductService poolCreditProductService) {
		this.poolCreditProductService = poolCreditProductService;
	}

	public PoolBailEduService getPoolBailEduService() {
		return poolBailEduService;
	}

	public void setPoolBailEduService(PoolBailEduService poolBailEduService) {
		this.poolBailEduService = poolBailEduService;
	}


	

}
