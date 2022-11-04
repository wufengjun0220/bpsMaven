package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.FileName;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * @Description 票据池低风险额度明细查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC058RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC058RequestHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private PedProtocolService pedProtocolService;
	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try {
	        Map map = request.getBody();
	        String coreNo = getStringVal(map.get("CORE_CLIENT_NO"));//核心客户号
	        String clientNo = getStringVal(map.get("CMS_CLIENT_NO"));//网银客户号
	        String onlineNo = getStringVal(map.get("ONLINE_BUSS_NO"));//在线业务编号
	        String onlineType = getStringVal(map.get("ONLINE_BUSS_TYPE"));//在线业务类型0：在线银承1：在线流贷
	        List list = new ArrayList();//在线协议
	        String bpsNo = "";//票据池编号
	        
	        //在线协议查询
	        OnlineQueryBean bean = new OnlineQueryBean();
	        bean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);//生效
	        bean.setCustNumber(coreNo);//核心客户号
	        bean.setEbkCustNo(clientNo); //网银客户号
	        if("1".equals(onlineType)){//在线流贷
	        	list = pedOnlineCrdtService.queryOnlineProtocolList(bean);
	        }else{//在线银承
	        	list = pedOnlineAcptService.queryOnlineAcptProtocolList(bean);
	        	
	        }
	        
	        if(null == list || list.size()==0){
	        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("无生效的协议信息！");
				response.setRet(ret);
		        return response;
	        }
	        
	        if("1".equals(onlineType)){//在线流贷
	        	PedOnlineCrdtProtocol crdtPro = (PedOnlineCrdtProtocol)list.get(0);
	        	bpsNo = crdtPro.getBpsNo();
	        	onlineNo = crdtPro.getOnlineCrdtNo();	        	
	        }else{//在线银承
	        	PedOnlineAcptProtocol acptPro = (PedOnlineAcptProtocol)list.get(0);
	        	bpsNo = acptPro.getBpsNo();
	        	onlineNo = acptPro.getOnlineAcptNo();
	        	
	        }
	        
	        response.getBody().put("ONLINE_BUSS_NO", onlineNo);      //在线业务协议编号
	        if(StringUtil.isNotBlank(bpsNo)){
	        	/**
	        	 * 查询票据池协议信息做额度试算
	        	 */
	        	PedProtocolDto pool = pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, bpsNo, null, null, null);
	        	if(PoolComm.POOL_MODEL_01.equals(pool.getPoolMode())){//总量模式
	        		financialService.txCreditCalculationTotal(pool);
	        		
	        	}else if(PoolComm.POOL_MODEL_02.equals(pool.getPoolMode())){//期限配比
	        		financialService.txCreditCalculationTerm(pool);
	        	}
	        	
	        	List<CreditCalculation> ccList = financialService.queryCreditCalculationListByBpsNo(bpsNo);
	        	if(null != ccList){
	        		List<Map> returnLList = this.transHandler(ccList); 
	        		
	        		logger.info("PJC058返回的列表："+returnLList.toString());
	        		
	        		String path = FileName.getFileNameClient(request.getTxCode())+".txt";
	        		response.getFileHead().put("FILE_FLAG", "2");
	        		response.getFileHead().put("FILE_PATH", path);
	        		response.setDetails(returnLList);
	        		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
	        		ret.setRET_MSG("查询成功！");
	        	}else{
	        		response.getFileHead().put("FILE_FLAG", "0");
	        		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
	        		ret.setRET_MSG("查询成功，无符合条件数据！");
	        	}
	        }else{
	        	response.getFileHead().put("FILE_FLAG", "0");
        		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        		ret.setRET_MSG("查询成功，无符合条件数据！");
	        }
	        
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池低风险额度明细查询异常");
		}
		response.setRet(ret);
        return response;
    }
    /**
     * 返回数据处理
     * @param ccList
     * @return
     * @author Ju Nana
     * @date 2021-7-9上午10:24:57
     */
    private List<Map> transHandler(List<CreditCalculation> ccList) {
		List<Map> list = new ArrayList();
		for(CreditCalculation cc : ccList){
			Map map = new HashMap();
			map.put("ONLINE_BUSS_START_DATE", DateUtils.formatDateToString(cc.getStartDate(),DateUtils.ORA_DATE_FORMAT));//在线业务起始日
			map.put("ONLINE_BUSS_END_DATE", DateUtils.formatDateToString(cc.getEndDate(),DateUtils.ORA_DATE_FORMAT));//在线业务截止日
			map.put("LOW_RISK_LIMIT_BALANCE", cc.getLowRiskCredit());//票据池低风险可用额度
			list.add(map);
		}
		return list;
	}

}
