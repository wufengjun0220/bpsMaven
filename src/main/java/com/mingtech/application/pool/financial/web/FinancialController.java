package com.mingtech.application.pool.financial.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.financial.domain.CreditCalcuCache;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 资产及额度处理处理器
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-5
 * @copyright 北明明润（北京）科技有限责任公司
 */

@Controller
public class FinancialController extends BaseController {
    private static final Logger logger = Logger.getLogger(FinancialController.class);
    @Autowired
    private FinancialService financialService;
    @Autowired
    private PedAssetPoolService pedAssetPoolService;
    /**
     * <p>
     * 方法名称: queryCreditCalculation|描述: 客户资产现金流列表
     * </p>
     */
    @RequestMapping("/queryCreditCalculation")
    public void queryCreditCalculation(String bpsNo) {
        try {
            String json = financialService.queryCreditCalculation(bpsNo,this.getCurrentUser());
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString(),e);
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * <p>
     * 方法名称: creditCalculCache|描述: 期限匹配额度测算
     * </p>
     * @param rows 融资信息
     * @param busiType 业务类型
     * @param riskType 风险类型
     * @param  poolInfoId 票据池协议id
     */
    @RequestMapping("/creditCalculCache")
    public void creditCalculCache(String rows,String poolInfoId,String busiType,String riskType,String occupyRatio) {
        try {
            BigDecimal ratio = new BigDecimal(occupyRatio);
            BigDecimal r1= new BigDecimal("1");
            BigDecimal r2= new BigDecimal("1.05");

        	if(busiType.equalsIgnoreCase("XD_02") && ratio.compareTo(r2)==-1){
                this.sendJSON("流贷占用系数不小于1.05" );
                return ;
        	}
        	if((busiType.equalsIgnoreCase("XD_01") || busiType.equalsIgnoreCase("XD_03") ||busiType.equalsIgnoreCase("XD_04")) && ratio.compareTo(r1)==-1){
                this.sendJSON("银承/保函/信用证占用系数不小于1" );
                return ;
        	}
        	
        	
        	//信息查询
            List<CreditRegisterCache> creditRegisterCacheList = new ArrayList<CreditRegisterCache>();
            PedProtocolDto pedProtocolDto = (PedProtocolDto) financialService.load(poolInfoId, PedProtocolDto.class);
            AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pedProtocolDto);
            if(null ==ap){
                this.sendJSON("该票据池未签约融资功能，不能进行融资测算！" );
                return ;
            }
            String apId = ap.getApId();
            
            //组装缓存对象并落库
            List<CreditRegisterCache> list = JSON.parseArray(rows, CreditRegisterCache.class);
            String flowNo = Long.toString(System.currentTimeMillis());
            creditRegisterCacheList = this.createCreditCacheList(list,pedProtocolDto,apId,busiType,riskType,occupyRatio,flowNo);
            
            
            //保证金同步及额度计算及资产表重置
	        financialService.txUpdateBailAndCalculationCredit(apId, pedProtocolDto);
	        
            Ret ret= financialService.txCreditUsedCheck(creditRegisterCacheList,pedProtocolDto,flowNo);
            
            
            if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
                this.sendJSON("测算不通过，额度不足！" );
            }else{            	
            	this.sendJSON("测算通过，额度充足！");
            }
            
            
        } catch (Exception e) {
            logger.error("额度测算异常："+e.getMessage(),e);
            this.sendJSON("额度测算功能异常！");
        }
    }
    /**
     * <p>
     * 方法名称: createCreditCacheList|描述: 封装CreditRegisterCache对象
     * </p>
     */
    public List<CreditRegisterCache> createCreditCacheList(List<CreditRegisterCache> list,PedProtocolDto dto,String apId, String busiType,String riskType,String occupyRatio,String flowNo ){
        List<CreditRegisterCache> creditRegisterCacheList = new ArrayList<CreditRegisterCache>();
        for(CreditRegisterCache cache:list){
//        	String uuid = UUID.randomUUID().toString().replaceAll("-","");
//            cache.setId(uuid);
            cache.setApId(apId);
            cache.setBusiType(busiType);//业务类型
            cache.setRiskType(riskType);//风险类型
            cache.setBpsNo(dto.getPoolAgreement());//票据池编号
            cache.setBusiId("111");//原业务id
            cache.setBusiNo("111");//原业务编号
            cache.setVoucherType(PoolComm.VT_0);//凭证类型
            cache.setIsOnline(PoolComm.NO);//是否线上
            BigDecimal ratio = new BigDecimal(occupyRatio);
            cache.setOccupyRatio(ratio);//占用系数
            cache.setOccupyAmount(cache.getBusiAmount());//实际占用金额
            cache.setOccupyCredit(cache.getBusiAmount().multiply(ratio).setScale(2,BigDecimal.ROUND_UP));//实际占用额度
            cache.setCreateDate(new Date());//创建时间
            cache.setUpdateDate(new Date());//更新时间
            cache.setFlowNo(flowNo);//流水号
            creditRegisterCacheList.add(cache);
        }
        return  creditRegisterCacheList;
    }
    /**
     * <p>
     * 方法名称: creditAddCalculCache|描述: 增加高低风险现金流出时，期限匹配额度测算
     * </p>
     * @param rows
     * @param  poolInfoId 票据池协议id
     */
    @RequestMapping("/creditAddCalculCache")
    public void creditAddCalculCache(String rows,String poolInfoId) {
        try {

        	PedProtocolDto pedProtocolDto = (PedProtocolDto) financialService.load(poolInfoId, PedProtocolDto.class);
            
            //查询实际的额度计算区间
            List<CreditCalculation> baseData = financialService.queryCreditCalculationListByBpsNo(pedProtocolDto.getPoolAgreement());
            Map<String,CreditCalculation> calMap = new HashMap<String, CreditCalculation>();
            if(null != baseData){
            	for(CreditCalculation cal : baseData){
            		calMap.put(DateTimeUtil.get_YYYYMMDD_Date(cal.getStartDate())+DateTimeUtil.get_YYYYMMDD_Date(cal.getEndDate()), cal);//key:起始日到期日拼接
            	}
            }
            
            List<CreditCalcuCache> list = JSON.parseArray(rows, CreditCalcuCache.class);
            List<CreditCalcuCache> calcuCachelist = new ArrayList<CreditCalcuCache>();
            String flowNo = Long.toString(System.currentTimeMillis());
            BigDecimal addLowRiskOut = new BigDecimal("0");//新增低风险流出金额
            BigDecimal addHighRiskOut = new BigDecimal("0");//新增高风险流出金额
            for(CreditCalcuCache calcuCache:list){
            	
//            	if(null!=calcuCache.getAddLowRiskOut()) {
//            		addLowRiskOut = addLowRiskOut.add(calcuCache.getAddLowRiskOut());
//            	}
//            	if(null!=calcuCache.getAddHighRiskOut()) {
//            		addHighRiskOut = addHighRiskOut.add(calcuCache.getAddHighRiskOut());
//            	}

            	
            	//在基础计算基础上进行操作，这里的转换是为了多次点击测试时候都应该在额度基础计算表中进行
            	CreditCalculation baseCal = calMap.get(DateTimeUtil.get_YYYYMMDD_Date(calcuCache.getStartDate())+DateTimeUtil.get_YYYYMMDD_Date(calcuCache.getEndDate()));
            	calcuCache.setLowRiskIn(baseCal.getLowRiskIn());                
            	calcuCache.setLowRiskOut(baseCal.getLowRiskOut());         
            	calcuCache.setLowRiskCashFlow(baseCal.getLowRiskCashFlow());    
            	calcuCache.setHighRiskIn(baseCal.getHighRiskIn());         
            	calcuCache.setHighRiskOut(baseCal.getHighRiskOut());        
            	calcuCache.setHighRiskCashFlow(baseCal.getHighRiskCashFlow());   
            	calcuCache.setLowRiskCredit(baseCal.getLowRiskCredit());      
            	calcuCache.setHighRiskCredit(baseCal.getHighRiskCredit());  

            	addLowRiskOut = addLowRiskOut.add(calcuCache.getAddLowRiskOut());
            	addHighRiskOut = addHighRiskOut.add(calcuCache.getAddHighRiskOut());
            	
                calcuCache.setLowRiskOut(calcuCache.getLowRiskOut().add(addLowRiskOut));
                calcuCache.setHighRiskOut(calcuCache.getHighRiskOut().add(addHighRiskOut));

                calcuCache.setLowRiskCashFlowNew(calcuCache.getLowRiskCashFlow());
                calcuCache.setHighRiskCashFlowNew(calcuCache.getHighRiskCashFlow());
                calcuCachelist.add(calcuCache);
            }
            String json = financialService.txCreditCalCahceForAddOut(calcuCachelist,pedProtocolDto,flowNo);
            this.sendJSON(json);
        } catch (Exception e) {
            logger.error("额度测算异常："+e.getMessage(),e);
            this.sendJSON("额度测算异常");
        }

    }

    }
