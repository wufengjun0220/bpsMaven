package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @Description 票据池额度查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC070RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC070RequestHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;

	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
    	Page page = getPage(request.getAppHead());
        Ret ret = new Ret();
        String apId = "";
        try {
	        Map map = request.getBody();
	        String coreNo = getStringVal(map.get("CORE_CLIENT_NO"));//核心客户号
	        String bpsNo = getStringVal(map.get("BPS_NO"));//票据池编号

	        ProtocolQueryBean pBean = new ProtocolQueryBean();
    		pBean.setPoolAgreement(bpsNo);
    		pBean.setCustnumber(coreNo);
    		pBean.setOpenFlag(PoolComm.OPEN_01);
    		PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(pBean);
    		if(null != dto){
    			response.getBody().put("BUSS_MODE", dto.getPoolMode());
    		}else{
    			ret.setRET_CODE(Constants.TX_FAIL_CODE);
    			ret.setRET_MSG("未查询到有效的票据池协议信息！");
    			response.setRet(ret);
    			return response;
    		}
			
			/*
			 * 根据核心保证金是否发生变化，判断是否需要进行额度更新
			 */
			financialService.txUpdateBailAndCalculationCredit(apId, dto);
			
			/*
			 * 各阶段额度列表查询
			 */
			List<CreditCalculation> ccList = null;
			
			
			if(PoolComm.POOL_MODEL_02.equals(dto.getPoolMode())){//期限配比模式
				ccList = financialService.queryCreditCalculationList(bpsNo);
				if (ccList != null ) {
					
					//返回明细列表组装
					
					List<Map> returnList = this.dataChange(ccList);
					response.setDetails(returnList);
					
					response.getAppHead().put("TOTAL_ROWS", ccList.size());
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
					
				} else {
				    ret.setRET_MSG("无符合条件数据");
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				}
			}else{//总量模式
				
				//返回明细列表组装
				
				List<Map> returnList = this.dataChange(bpsNo);
				response.setDetails(returnList);
				
				response.getAppHead().put("TOTAL_ROWS", "1");
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
				
				
			}
			
			
        
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池额度查询异常,请联系客户经理！");
		
		}

        response.setRet(ret);
        return response;
    }
    
    /**
     * 结果集转换--总量模式
     * @param bpsNo
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-9-14下午2:59:31
     */
    private List<Map> dataChange(String bpsNo) throws Exception{
    	
    	EduResult eduResult = pedAssetPoolService.queryEduAll(bpsNo);
    	
    	List<Map> returnList = new ArrayList<Map>();
    	Map ccMap = new HashMap();
    	ccMap.put("LIMIT_INFO_ARRAY.START_DATE", DateTimeUtil.get_YYYYMMDD_Date(new Date()));  //起始日    
    	ccMap.put("LIMIT_INFO_ARRAY.END_DATE", "2099-12-31");  //到期日    
    	ccMap.put("LIMIT_INFO_ARRAY.LOW_RISK_TOTAL_AMT", eduResult.getLowRiskAmount().add(eduResult.getBailAmountTotail()));  //低风险总额
    	ccMap.put("LIMIT_INFO_ARRAY.LOW_RISK_USED_AMT", eduResult.getUsedLowRiskAmount().add(eduResult.getBailAmountUsed()));  //低风险已用
    	ccMap.put("LIMIT_INFO_ARRAY.LOW_RISK_LIMIT_BALANCE", eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount()));  //低风险可用
    	ccMap.put("LIMIT_INFO_ARRAY.HIGH_RISK_TOTAL_AMT", eduResult.getHighRiskAmount());  //高风险总额
    	ccMap.put("LIMIT_INFO_ARRAY.HIGH_RISK_USED_AMT", eduResult.getUsedHighRiskAmount());  //高风险已用
    	ccMap.put("LIMIT_INFO_ARRAY.HIGH_RISK_LIMIT_BALANCE", eduResult.getFreeHighRiskAmount());  //高风险可用  
    	returnList.add(ccMap);
    	
    	return returnList;
    	
    }
    
    /**
     * 结果集转换--期限配比模式
     * @param ccList
     * @return
     * @author Ju Nana
     * @date 2021-7-10下午5:42:34
     */
    private List<Map> dataChange(List<CreditCalculation> ccList){
    	
    	List<Map> returnList = new ArrayList<Map>();
    	for(CreditCalculation cc : ccList){
    		Map ccMap = new HashMap();
    		ccMap.put("LIMIT_INFO_ARRAY.START_DATE", cc.getStartDate());  //起始日    
    		ccMap.put("LIMIT_INFO_ARRAY.END_DATE", cc.getEndDate());  //到期日    
    		ccMap.put("LIMIT_INFO_ARRAY.LOW_RISK_TOTAL_AMT", cc.getLowRiskIn());  //低风险总额
    		ccMap.put("LIMIT_INFO_ARRAY.LOW_RISK_USED_AMT", cc.getLowRiskOut());  //低风险已用
    		ccMap.put("LIMIT_INFO_ARRAY.LOW_RISK_LIMIT_BALANCE", cc.getLowRiskCredit());  //低风险可用
    		ccMap.put("LIMIT_INFO_ARRAY.HIGH_RISK_TOTAL_AMT", cc.getHighRiskIn());  //高风险总额
    		ccMap.put("LIMIT_INFO_ARRAY.HIGH_RISK_USED_AMT", cc.getHighRiskOut());  //高风险已用
    		ccMap.put("LIMIT_INFO_ARRAY.HIGH_RISK_LIMIT_BALANCE", cc.getHighRiskCredit());  //高风险可用  
    		returnList.add(ccMap);
    	}
    	
    	return returnList;
    	
    }

}
