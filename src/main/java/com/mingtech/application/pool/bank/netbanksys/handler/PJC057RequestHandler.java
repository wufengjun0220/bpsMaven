
package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
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
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * @Description 融资测算
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC057RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC057RequestHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        String apId = null;//AssetPool表主键
        try {
	        Map map = request.getBody();
	        String onlineNo = getStringVal(map.get("ONLINE_BUSS_NO"));//在线业务编号
	        String onlineType = getStringVal(map.get("ONLINE_BUSS_TYPE"));//在线业务类型 0：在线银承 1：在线流贷
	        String billAmt = getStringVal(map.get("BILL_AMT"));//开票金额
	        String dueDate = getStringVal(map.get("BUSS_EXPIRY_DATE"));//业务到期日
	        
	        BigDecimal billAmount = BigDecimal.ZERO;//开票金额
	        if(StringUtil.isNotBlank(billAmt)){
		        billAmount = new BigDecimal(billAmt);
	        }
	        Date dueDt = null;//银承业务到期日
	        boolean isPoolCreditEnough = false;//池额度是否充足
	        boolean isProCreditEnough = false;//担保额度是否充足
	        boolean isOnlineCreditEnough = false;//在线合同是否充足

	        String msg = "";//测算结果说明
	        boolean isAllBail = false;//是否100%保证金的银承
	        BigDecimal poolCreditRatio = new BigDecimal(0);//票据池额度占用比例
	        
	        BigDecimal  prptocolBalance = new BigDecimal(0);//在线银承合同可用余额 
	        BigDecimal  contractBalance = new BigDecimal(0);//票据池担保合同可用余额
	        BigDecimal  limitBalance = new BigDecimal(0);    //当前区间票据池低风险可用额度 
	        BigDecimal  availableAmt = new BigDecimal(0);   //当前可开票额度=MIN[在线银承合同可用余额、Rounddown（票据池担保合同可用余额/票据池额度比例，2）、Rounddown（票据池低风险可用额度/票据池额度比例，2）]（若传来银承业务到期日不为空）
	        
	        //输入校验
	        if(StringUtil.isBlank(onlineNo)||StringUtil.isBlank(onlineType)){
	        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
		     	ret.setRET_MSG("在线协议编号与业务类型均为必送字段！");
		     	response.setRet(ret);
		        return response;
	        }
	                
	        
	        //在线协议查询
	        OnlineQueryBean bean = new OnlineQueryBean();
	        List list = null;
	        bean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);//生效
	        if("1".equals(onlineType)){//在线流贷
	        	bean.setOnlineCrdtNo(onlineNo);
	        	list = pedOnlineCrdtService.queryOnlineProtocolList(bean);
	        }else{//在线银承
	        	bean.setOnlineAcptNo(onlineNo);
	        	list = pedOnlineAcptService.queryOnlineAcptProtocolList(bean,null);
	        	
	        }
	        //校验：在线银承签约校验
	        if(null == list || list.size()==0){
	        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("无生效的协议信息！");
				response.setRet(ret);
		        return response;
	        }

        	PedProtocolDto pool = null;
        	if("1".equals(onlineType)){//在线流贷
        		//在线银承合同可用余额 
    	        PedOnlineCrdtProtocol crdtPro = (PedOnlineCrdtProtocol) list.get(0);
            	prptocolBalance =crdtPro.getOnlineLoanTotal().subtract(crdtPro.getUsedAmt());
            	//票据池担保合同可用余额
            	pool  =  pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, crdtPro.getBpsNo(), null, null, null);
            	
            	poolCreditRatio = crdtPro.getPoolCreditRatio().divide(new BigDecimal("100"));
            	billAmount = billAmount.multiply(poolCreditRatio);//开票金额  * 系数 = 实际占用金额
        	}else{
        		//在线银承合同可用余额 
    	        OnlineQueryBean onlinePro = (OnlineQueryBean) list.get(0);
            	prptocolBalance =onlinePro.getOnlineAcptTotal().subtract(onlinePro.getUsedAmt());
            	poolCreditRatio = onlinePro.getPoolCreditRatio().divide(new BigDecimal("100"));
            	billAmount = billAmount.multiply(poolCreditRatio);//开票金额  * 系数 = 实际占用金额
            	if(new BigDecimal("100").compareTo(onlinePro.getDepositRatio())==0){//100%业务保证金模式
            		isAllBail = true;
            	}
            	//票据池担保合同可用余额
            	if(!isAllBail){//100%保证金不查
            		pool  =  pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, onlinePro.getBpsNo(), null, null, null);
            		
            		//锁AssetPool表
        			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pool);
        			apId = ap.getApId();
        			boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
        			if(!isLockedSucss){//加锁失败
        				ret.setRET_CODE(Constants.EBK_11);
        				ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
        				response.setRet(ret);
        				logger.info("票据池【"+pool.getPoolAgreement()+"】上锁！");
        				return response;
        			}
            	}
        	}
        	
	        //输入校验
        	if(!isAllBail && PoolComm.POOL_MODEL_01.equals(pool.getPoolMode())){//总量模式--为了校验规则的统一设定默认到期日
        		dueDate = "2099-01-01";
        	}
        	
	        if(StringUtil.isNotBlank(billAmt)&&StringUtil.isBlank(dueDate)){
	    		
	        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
		     	ret.setRET_MSG("期限匹配模式，开票金额不为空时，业务到期日为必输！");
		     	response.setRet(ret);
		     	
		     	//解锁AssetPool表，并重新计算该表数据
		     	pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
		        return response;
	        }
        	

        	if(null != pool && !isAllBail){
        		contractBalance = pool.getCreditFreeAmount();
        		pool.getPoolMode();
        	}else{
        		
        		if(!isAllBail){        			
        			ret.setRET_CODE(Constants.TX_FAIL_CODE);
        			ret.setRET_MSG("无生效的融资票据池协议信息！");
        			response.setRet(ret);
        			
        			//解锁AssetPool表，并重新计算该表数据
        			pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
        			return response;
        		}
        	}
        	
        	response.getBody().put("ONLINE_BUSS_NO", onlineNo);//在线业务编号       
	        response.getBody().put("ONLINE_ACPT_AVAL_BALANCE", prptocolBalance);//在线银承合同可用余额     
	        response.getBody().put("GUARANTEE_CONTRACT_AVAL_BALANCE",contractBalance);//票据池担保合同可用余额
        		
	        if(StringUtil.isBlank(dueDate)){ //业务到期日为空只返回在线银承合同可用余额、票据池担保合同可用余额
	        	String msg1="";
	        	if(billAmount.compareTo(contractBalance)<=0){
	        		isProCreditEnough = true;//担保额度充足
	        	}
	        	
	        	if(billAmount.compareTo(prptocolBalance)<=0){
	        		isOnlineCreditEnough = true;//在线银承合同可用余额充足 
	        	}
	        	if(!isOnlineCreditEnough){
	        		if("1".equals(onlineType)){//在线流贷
	        			msg1+= "在线流贷合同可用余额、";
	        		}else{
	        			msg1+= "在线银承合同可用余额、";
	        		}
    			}
    			if(!isProCreditEnough){
    				msg1+= "票据池担保合同可用余额、";
    				
    			}
    			if(isOnlineCreditEnough && isProCreditEnough){
    				//可开票额度 = 票据池低风险可用额度 、票据池担保合同可用余额 、在线银承合同可用余额     中最小的一个  ，然后除以协议中的比例
    		        availableAmt = prptocolBalance.compareTo(contractBalance)<0?prptocolBalance:contractBalance;
    		        availableAmt = availableAmt.compareTo(limitBalance)<0?availableAmt:limitBalance;
    		        if(availableAmt.compareTo(BigDecimal.ZERO)==0){
    		        	availableAmt = BigDecimal.ZERO;
    		        }else{    
        		        if(poolCreditRatio.compareTo(BigDecimal.ZERO)!=0){
        		        	availableAmt = availableAmt.divide(poolCreditRatio,2,BigDecimal.ROUND_DOWN);
        		        }
    		        }
    		        
    		        
    		        if(isAllBail){	        	
    		        	response.getBody().put("LOW_RISK_LIMIT_BALANCE", "0");//票据池低风险可用额度--100%保证金给网银返回0   
    		        	response.getBody().put("AVAILABLE_LIMIT_AMT", prptocolBalance);//当前可开票额度
    		        }else{
    		        	response.getBody().put("LOW_RISK_LIMIT_BALANCE", limitBalance);//票据池低风险可用额度       
    		        	response.getBody().put("AVAILABLE_LIMIT_AMT", availableAmt);//当前可开票额度
    		        }
        			if("1".equals(onlineType)){//在线流贷
        				msg1="当前可贷款额度满足您的融资金额需求";//测算结果说明
	        		}else{
	        			msg1="当前可开票额度满足您的融资金额需求";//测算结果说明
	        		}
            		response.getBody().put("CHECK_RESULT_REMARK", msg1);//测算结果说明
            		response.getBody().put("CHECK_RESULT", "0");//测算结果  0：通过 1：不通过
    			}else{
    				msg1+="不足，请联系客户经理。";
        			msg1 = msg1.replace("、不足", "不足");//去除最后一个顿号
            		response.getBody().put("CHECK_RESULT_REMARK", msg1);//测算结果说明
            		response.getBody().put("CHECK_RESULT", "1");//测算结果  0：通过 1：不通过
    			}
    			//解锁AssetPool表，并重新计算该表数据
    			if(null != apId){    				
    				pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
    			}
        		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
    			ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
    			response.setRet(ret);
    	        return response;
	        }
	        
	        
	        //若到期日不为空，开票金额默认为0
	        if(StringUtil.isBlank(billAmt)){
	        	billAmt = "0";
	        }
	        

	        dueDt = DateTimeUtil.parse(dueDate);
	        
	        if(!isAllBail){//非100%业务保证金
	        	
	        	//保证金同步及额度计算及资产表重置
	        	AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pool);
	        	apId = ap.getApId();
	        	financialService.txUpdateBailAndCalculationCredit(apId, pool);
	        	
	        	
	        	//额度重算
	        	List<CreditCalculation> ccList = new ArrayList<CreditCalculation>();
	        	if(PoolComm.POOL_MODEL_01.equals(pool.getPoolMode())){//总量模式
	        		ccList.add(financialService.txCreditCalculationTotal(pool));
	        		
	        	}else if(PoolComm.POOL_MODEL_02.equals(pool.getPoolMode())){//期限配比
	        		ccList = financialService.txCreditCalculationTerm(pool);
	        	}
	        	
	        	//解锁AssetPool表，并重新计算该表数据
	    		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
	    		
	        	
	        	 if(StringUtil.isBlank(dueDate)){

	        		 //票据池保证金资产额度
	     	        AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);
	     	        
	     	        //低风险票资产额度
	     	        AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
	     	        
	     	        limitBalance = atBail.getCrdtFree().add(atBillLow.getCrdtFree());   //票据池低风险可用额度 
	     	        
	        	 }else{	        		 
	        		 //循环找到给定到期日所在的区间的低风险可用额度
	        		 for(CreditCalculation cc : ccList){
	        			 if(DateUtils.isBetweenTowDay(cc.getStartDate(), dueDt, cc.getEndDate())){
	        				 limitBalance = cc.getLowRiskCredit(); //当前区间票据池低风险可用额度 
	        				 break;
	        			 }
	        			 
	        		 }
	        	 }
	        }else{
	        	limitBalance = new BigDecimal("99900000000");//100%保证金设置池额度默认值为999亿（这里没有实际意义，为了下一步的比较规则与非100%银承一致而设置）
	        }

	        
	        //可开票额度 = 票据池低风险可用额度 、票据池担保合同可用余额 、在线银承合同可用余额     中最小的一个  ，然后除以协议中的比例
	        logger.info("票据池低风险可用额度："+limitBalance+"、票据池担保合同可用余额："+contractBalance+"、在线银承合同可用余额："+prptocolBalance);
	        availableAmt = prptocolBalance.compareTo(contractBalance)<0?prptocolBalance:contractBalance;
	        availableAmt = availableAmt.compareTo(limitBalance)<0?availableAmt:limitBalance;
	        if(poolCreditRatio.compareTo(BigDecimal.ZERO)!=0){
		        availableAmt = availableAmt.divide(poolCreditRatio,2,BigDecimal.ROUND_DOWN);
	        }
	        
	        if(isAllBail){	        	
	        	response.getBody().put("LOW_RISK_LIMIT_BALANCE", "0");//票据池低风险可用额度--100%保证金给网银返回0   
	        	response.getBody().put("AVAILABLE_LIMIT_AMT", prptocolBalance);//当前可开票额度
	        }else{
	        	response.getBody().put("LOW_RISK_LIMIT_BALANCE", limitBalance);//票据池低风险可用额度       
	        	response.getBody().put("AVAILABLE_LIMIT_AMT", availableAmt);//当前可开票额度
	        }
	        
	        
	        if(StringUtil.isNotBlank(billAmt)&&StringUtil.isNotBlank(dueDate)){
	        	
	        	if(isAllBail){//100%业务保证金银承
	        		isPoolCreditEnough=true;//池额度充足	
		        }else{
		        	
		        	String flowNo  = Long.toString(System.currentTimeMillis());
		        	CreditProduct product = new CreditProduct();
		        	product.setId(Long.toString(System.currentTimeMillis()));
		        	product.setCrdtNo(flowNo);
		        	product.setCustNo(pool.getCustnumber());
		        	product.setCustName(pool.getCustname());
		        	if("0".equals(onlineType)){ 				
		        		product.setCrdtType(PoolComm.XDCP_YC);//融资类型--银承
		        	}
		        	if("1".equals(onlineType)){ 				
		        		product.setCrdtType(PoolComm.XDCP_LD);//融资类型--流贷
		        	}
		        	product.setCrdtIssDt(new Date());//生效日
		        	product.setCrdtDueDt(dueDt);//到期日
		        	product.setUseAmt(billAmount);//合同金额-已处理系数
		        	product.setRestUseAmt(billAmount);//需要占用的额度
		        	product.setCrdtStatus(PoolComm.RZCP_YQS);//业务状态   RZ_03：额度占用成功   JQ_00 已结清   存储MIS系统发过来的状态：JQ_01 取消放贷  JQ_02 手工提前终止出账   JQ_03 合同到期    JQ_04 合同终止
		        	product.setSttlFlag(PoolComm.JQZT_WJQ);//结清标记   JQ_00:已结清   JQ_01：未结清
		        	product.setCrdtBankCode(pool.getOfficeNet());//网点
		        	product.setRisklevel(PoolComm.LOW_RISK);//风险等级--低风险
		        	product.setCcupy("1");//占用比例
		        	product.setBpsNo(pool.getPoolAgreement());
		        	product.setIsOnline(PoolComm.YES);//线上
		        	product.setMinDueDate(dueDt);//借据最早到期日
		        	
		        	/*
		        	 * 票据池额度校验
		        	 */
		        	CreditRegisterCache crdtReg = creditRegisterService.createCreditRegisterCache(product, pool,apId);
		        	List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
		        	crdtReg.setFlowNo(flowNo);
		        	crdtRegList.add(crdtReg);
		        	Ret crdtCheckRet =  financialService.txCreditUsedCheck(crdtRegList, pool,flowNo);
		        	
		        	if(crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
		        		
		        		isPoolCreditEnough=true;//池额度充足	
		        	}
		        	
		        }
	        	
		        
	        	if(billAmount.compareTo(contractBalance)<=0){
	        		isProCreditEnough = true;//担保额度充足
	        	}
	        	
	        	if(billAmount.compareTo(prptocolBalance)<=0){
	        		isOnlineCreditEnough = true;//在线银承合同可用余额充足 
	        	}
	        	
	        	logger.info(" 担保金额:"+contractBalance+" 在线银承合同可用余额 "+ prptocolBalance);
	        	logger.info("池额度充足:"+isPoolCreditEnough+" 担保额度充足:"+isProCreditEnough+" 在线银承合同可用余额充足 "+ isOnlineCreditEnough);
	        	
        		if(new BigDecimal(billAmt).compareTo(availableAmt)<=0){//若【开票金额】小于等于【当前可开票额度（元）】
        			
        			response.getBody().put("CHECK_RESULT", "0");//测算结果  0：通过 1：不通过
        			if("1".equals(onlineType)){//在线流贷
    					msg="当前可贷款额度满足您的融资金额需求";//测算结果说明
	        		}else{
	        			msg="当前可开票额度满足您的融资金额需求";//测算结果说明
	        		}
        			
        		
        		}else{//不足
        			
        			
        			//若【在线银承合同可用余额】、Rounddown（票据池担保合同可用余额/票据池额度比例，2）、Rounddown（票据池低风险可用额度/票据池额度比例，2）中至少两个小于【开票金额】，反馈文本：
        			// “在线银承合同可用余额、票据池担保合同可用余额、票据池低风险可用额度（三个字段按顺序，哪个值小于就显示对应的文字）不足，请联系客户经理。”
        			
        			response.getBody().put("CHECK_RESULT", "1");//测算结果  0：通过 1：不通过
        			
        			if(!isOnlineCreditEnough){
        				if("1".equals(onlineType)){//在线流贷
        					msg+= "在线流贷合同可用余额、";
    	        		}else{
    	        			msg+= "在线银承合同可用余额、";
    	        		}
        			}
        			if(!isProCreditEnough){
        				msg+= "票据池担保合同可用余额、";
        				
        			}
        			if(!isPoolCreditEnough){
        				msg+= "票据池低风险可用额度、";
        			}
        			if(msg !=""){
        				msg+="不足，请联系客户经理。";
        				msg = msg.replace("、不足", "不足");//去除最后一个顿号
        			}
        			
        			
        			if(!isOnlineCreditEnough && isPoolCreditEnough && isProCreditEnough){//若仅【在线银承合同可用余额】小于【开票金额】
        				if("1".equals(onlineType)){//在线流贷
        					msg= "在线流贷合同可用余额不足，请联系客户经理。";//测算结果说明
    	        		}else{
    	        			msg= "在线银承合同可用余额不足，请联系客户经理。";//测算结果说明
    	        		}
        			}
        			
        			if(isOnlineCreditEnough && isPoolCreditEnough && !isProCreditEnough){//若仅Rounddown（票据池担保合同余额/票据池额度比例，2）小于【开票金额】
        				msg= "票据池担保合同可用余额不足，请联系客户经理。";//测算结果说明
        			}
        			
        			if(isOnlineCreditEnough && !isPoolCreditEnough && isProCreditEnough){//若仅当前可开票额度小于Rounddown（票据池低风险可用/票据池额度比例，2）
        				msg= "当前票据池低风险额度不足，建议补充票据池资产或者调低融资金额。";//测算结果说明
        			}
        			        			
        		}
        		response.getBody().put("CHECK_RESULT_REMARK", msg);//测算结果说明
	        	
	        }
	        
	      //解锁AssetPool表，并重新计算该表数据
    		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
    		
	        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);

        } catch (Exception e) {
        	if(null != apId){        		
        		pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
        	}
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("融资测算异常，");
		}
		response.setRet(ret);
        return response;
    }

}
