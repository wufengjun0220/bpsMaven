package com.mingtech.application.pool.bank.creditsys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.UUID;

/**
 * 信贷系统额度占用校验  
 * 校验规则：
 * （1）若票据池额度全被冻结	不允许占用，返回额度不足
 * （2）占用额度小于担保合同最高限额
 * （3）若为集团子户融资，则需要占用额度小于融资人融资限额
 * （4）若主业务合同类型为高风险，则校验总可用额度是否>本次占用额度
 * （5）若主业务合同类型为低风险，则校验除去高风险可用额度的总可用额是否>本次占用额度
 * （6）担保合同到期日控制：如果当前日期 > 担保合同到期日  则给信贷返回担保合同已过期，不允许占用额度
 *  若满足上述条件则可以进行额度占用
 * 
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-24
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class PJE001CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE001CreditHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private CreditRegisterService creditRegisterService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		String apId="";
		try {
			
			
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));// 票据池编号
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));// 核心客户号
			String RZLX = getStringVal(request.getBody().get("FINANCING_TYPE"));// 融资类型
			String WDH = getStringVal(request.getBody().get("BANK_BRANCH_ID"));// 网点号
			String FXDJ = getStringVal(request.getBody().get("RISK_LEVEL"));// 风险等级
			Date QSR = getDateVal(request.getBody().get("START_DATE"));
			Date DQR = getDateVal(request.getBody().get("EXPIRY_DATE"));
			BigDecimal HTJE = getBigDecimalVal(request.getBody().get("CONTRACT_AMT")); // 合同金额
			BigDecimal ZYBL = getBigDecimalVal(request.getBody().get("USE_RATE"));// 占用比例
			
			//最早到期日校验时候不传值，设置默认值
			Date minDate =DateUtils.StringToDate("2099-01-01",DateUtils.ORA_DATE_FORMAT);
			BigDecimal ED = new BigDecimal("0.0");
			if (HTJE != null && ZYBL != null) {
				ED = HTJE.multiply(ZYBL);// 需要占用的额度
			}
			
			
			/*
			 * 获取协议信息			 */
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
			apId = ap.getApId();
			PedProtocolList mem = null;
			
			/*
			 * （1）校验--票据池开通校验
			 */
			logger.info("（1）校验--票据池开通校验");
			if(dto==null){
				ret.setRET_CODE(Constants.CREDIT_01);
				ret.setRET_MSG(custNo + ":该客户未开通票据池功能！");
				response.setRet(ret);
				return response;
			}
			String frozenstate = dto.getFrozenstate();//冻结状态
			
			
			
			
			/*
			 * （2）校验--融资功能校验
			 */
			logger.info("（2）校验--融资功能校验");
			if (!PoolComm.OPEN_01.equals(dto.getOpenFlag())) {
				ret.setRET_CODE(Constants.CREDIT_01);
				ret.setRET_MSG(custNo + ":该客户未开通融资票据池业务！");
			}
			
			/*
			 * （3）校验--担保合同到期日控制：如果当前日期 > 担保合同到期日  则给信贷返回担保合同已过期，不允许占用额度
			 */
			logger.info("（3）校验--担保合同到期日控制：如果当前日期 > 担保合同到期日  则给信贷返回担保合同已过期，不允许占用额度");
			Date dueDate = dto.getContractDueDt();//担保合同到期日
			Date today = new Date();//当前日期		
			if(today.getTime()>dueDate.getTime()){
				ret.setRET_CODE(Constants.CREDIT_08);
				ret.setRET_MSG("担保合同已到期！");
				response.setRet(ret);
				return response;
			}
			
			/*
			 * （4）校验--集团融资客户信息校验
			 */
			logger.info("（4）校验--集团融资客户信息校验");
			if(dto!=null&&PoolComm.YES.equals(dto.getIsGroup())){
				ProListQueryBean qb = new ProListQueryBean();
				qb.setBpsNo(bpsNo);
				qb.setCustNo(custNo);			
				List<String> custIdentityList = new ArrayList<String>();
				custIdentityList.add(PoolComm.KHLX_02);
				custIdentityList.add(PoolComm.KHLX_03);
				qb.setCustIdentityList(custIdentityList);
				mem  = pedProtocolService.queryProtocolListByQueryBean(qb);
				
				if(mem==null){
					ret.setRET_CODE(Constants.CREDIT_01);
					ret.setRET_MSG(custNo + ":该票据池下无该融资客户信息！");
					response.setRet(ret);
					return response;
				}
			}
			
			/*
			 * （5）校验--票据池对账校验
			 */
			logger.info("（5）校验--票据池对账校验");
			Ret canCreateCredit = pedProtocolService.isCanCreateCreditByCheckResult(dto);
			if(canCreateCredit.getRET_CODE().equals(Constants.CREDIT_10)){//客户最新一笔对账结果为不一致或近两个月未对账
				ret.setRET_CODE(Constants.CREDIT_10);
				ret.setRET_MSG(canCreateCredit.getRET_MSG());
				response.setRet(ret);
				return response;
			}
			
			/*
			 * （6）校验--主业务合同号重复校验
			 */
			logger.info("（6）校验--主业务合同号重复校验");
			String creditNo = getStringVal(request.getBody().get("MIS_PRODUCT_NO"));// 信贷产品号
			CreditProduct product = poolCreditProductService.queryProductByCreditNo(creditNo,null);
			if (product != null && !product.getCrdtStatus().trim().equals(PoolComm.JQ_05)) {
				ret.setRET_CODE(Constants.CREDIT_03);
				ret.setRET_MSG(custNo + "客户" + creditNo + ":该主业务合同已占用，不允许重复占用！");
				response.setRet(ret);
				return response;
			}
			/*
			 * （7）校验--票据池额度冻结校验
			 */
			logger.info("7）校验--票据池额度冻结校验");
			if(dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_03)){
				ret.setRET_CODE(Constants.CREDIT_02);
				ret.setRET_MSG("票据池额度不足!");
				logger.info("票据池额度全被冻结,不允许占用，返回额度不足.");
				response.setRet(ret);
				return response;
			}
			
			/*
			 * 核心保证金同步及额度重算
			 */
			
			financialService.txUpdateBailAndCalculationCredit(apId, dto);
			/*
			 * 额度统计查询
			 */
			EduResult eduResult = pedAssetPoolService.queryEduAll(bpsNo);
			BigDecimal creditamount = new BigDecimal("0");//最高额担保合同金额
			BigDecimal financLimit = new BigDecimal("0");//集团客户融资限额
			BigDecimal totalAmt = new BigDecimal("0");//票据池总可用额度：高风险票可用+低风险票可用+保证金可用
			BigDecimal lowRiskTotalAmt = new BigDecimal("0");//低风险总可用额度：低风险票可用+保证金可用
			BigDecimal allUsed = new BigDecimal("0");//全部已用

			
			BigDecimal billUsed = eduResult.getUsedHighRiskAmount().add(eduResult.getUsedLowRiskAmount());
			allUsed = billUsed.add(eduResult.getBailAmountUsed());
			
			if(frozenstate.equals(PoolComm.FROZEN_STATUS_03)){//全冻结
				totalAmt = new BigDecimal("0");
				lowRiskTotalAmt = new BigDecimal("0");
			}else if(frozenstate.equals(PoolComm.FROZEN_STATUS_02)){//票据冻结
				totalAmt = eduResult.getBailAmount();
				lowRiskTotalAmt = eduResult.getBailAmount();
			}else if(frozenstate.equals(PoolComm.FROZEN_STATUS_01)){//保证金冻结
				totalAmt =  eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount());
				lowRiskTotalAmt = eduResult.getFreeLowRiskAmount();
			}else{
				BigDecimal low = eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount());//低风险
				totalAmt = low.add(eduResult.getFreeHighRiskAmount());//总额度
				lowRiskTotalAmt = eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount());
				
			}
			
			
			
			/*
			 * （8）校验--占用额度需要小于担保合同最高限额
			 */
			logger.info("（8）校验--占用额度需要小于担保合同最高限额");
			creditamount = dto.getCreditamount();//最高额担保合同金额
			BigDecimal use = allUsed.add(ED);
			BigDecimal result = creditamount.subtract(use);
			if(result.doubleValue()<0){//如果该客户    已用额度 + 本次需要占用额度 > 担保金额    则不允许占用
				
				ret.setRET_CODE(Constants.CREDIT_07);
				ret.setRET_MSG("票据池担保余额不足");
				response.setRet(ret);
				return response;
			}
			logger.info("PJE001额度校验：最高额担保合同金额：【"+creditamount+"】  票据池总可用额度：【"+totalAmt+"】" +"低风险总可用额度：【"+lowRiskTotalAmt+"】  全部已用：【"+allUsed+"】  信贷主业务合同本次需要 占用金额：【"+ED+"】");
			
			/*
			 * （9）校验--若为集团子户融资，则需要占用额度小于融资人融资限额
			 */
			logger.info("（9）校验--若为集团子户融资，则需要占用额度小于融资人融资限额");

			if(PoolComm.YES.equals(dto.getIsGroup())){
				BigDecimal usedBalance = creditRegisterService.queryCreditBalance(bpsNo, null);//占用的额度余额
				BigDecimal all = usedBalance.add(ED);
				financLimit = mem.getMaxFinancLimit();			
				logger.info("需占用的额度ED："+ED+"占用的额度余额usedBalance："+usedBalance+"融资人最高限额financLimit："+financLimit);
				if(financLimit.compareTo(all)<0){
					
					ret.setRET_CODE(Constants.CREDIT_09);
					ret.setRET_MSG("该集团子户占用额度大于融资人最高限额！");
					response.setRet(ret);
					return response;
				}
			}
			
			/*
			 * （10）校验--若主业务合同类型为高风险，则校验总可用额度是否>本次占用额度
			 */
			logger.info("（10）校验--若主业务合同类型为高风险，则校验总可用额度是否>本次占用额度");
			if(PoolComm.HIGH_RISK.equals(FXDJ)){
				if(totalAmt.compareTo(ED)<0){
					
					ret.setRET_CODE(Constants.CREDIT_02);
					ret.setRET_MSG("票据池额度不足!");
					logger.info("主业务合同类型为高风险，可用额度是否>本次占用额度.");
					response.setRet(ret);
					return response;
				}
				
			}
			/*
			 * （11）校验--若主业务合同类型为低风险，则校验除去高风险可用额度的总可用额是否>本次占用额度
			 */
			logger.info("（11）校验--若主业务合同类型为低风险，则校验除去高风险可用额度的总可用额是否>本次占用额度");
			if(PoolComm.LOW_RISK.equals(FXDJ)){
				if(lowRiskTotalAmt.compareTo(ED)<0){
					ret.setRET_CODE(Constants.CREDIT_02);
					ret.setRET_MSG("票据池额度不足!");
					logger.info("主业务合同类型为低风险，则校验除去高风险可用额度的总可用额是否>本次占用额度");
					response.setRet(ret);
					return response;
				}
			}
			
			
			
			product = new CreditProduct();
			product.setCrdtNo(creditNo);
			product.setCustNo(custNo);
			product.setCustName(dto.getCustname());
			product.setCrdtType(RZLX);//融资类型
			product.setCrdtIssDt(QSR);//生效日
			product.setCrdtDueDt(DQR);//到期日
			product.setUseAmt(HTJE);//合同金额
			product.setRestUseAmt(ED);//需要占用的额度
			product.setCrdtStatus(PoolComm.RZCP_YQS);//业务状态   RZ_03：额度占用成功   JQ_00 已结清   存储MIS系统发过来的状态：JQ_01 取消放贷  JQ_02 手工提前终止出账   JQ_03 合同到期    JQ_04 合同终止
			product.setSttlFlag(PoolComm.JQZT_WJQ);//结清标记   JQ_00:已结清   JQ_01：未结清
			product.setCrdtBankCode(WDH);//网点
			product.setRisklevel(FXDJ);//风险等级
			product.setCcupy(ZYBL.doubleValue() + "");//占用比例
			product.setBpsNo(bpsNo);
			product.setIsOnline(PoolComm.NO);//线下
			product.setMinDueDate(minDate);//借据最早到期日
			product.setId(UUID.randomUUID().toString().replaceAll("-",""));
			/*
			 * 用信业务登记，额度占用校验
			 */
			String flowNo = Long.toString(System.currentTimeMillis());
			CreditRegisterCache crdtReg = creditRegisterService.createCreditRegisterCache(product, dto,apId);
			crdtReg.setFlowNo(flowNo);
			List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
			crdtRegList.add(crdtReg);
			Ret crdtCheckRet =  financialService.txCreditUsedCheck(crdtRegList, dto,flowNo);
			
			if(crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("额度充足!");
				
			}else{
				if(dto.getPoolMode().equals(PoolComm.POOL_MODEL_01)){
					//总量模式
					logger.info("票据池额度额度不足!");
					ret.setRET_CODE(Constants.CREDIT_02);
					ret.setRET_MSG("票据池额度不足!");
				}else{
					logger.info("票据池额度期限匹配不通过");
					ret.setRET_CODE(Constants.CREDIT_02);
					ret.setRET_MSG("票据池额度期限匹配不通过!");
					
				}
				
			}

		} catch (Exception e) {
			logger.error("PJE001占用额度失败!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("PJE001占用额度失败票据池内部错误");
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

}
