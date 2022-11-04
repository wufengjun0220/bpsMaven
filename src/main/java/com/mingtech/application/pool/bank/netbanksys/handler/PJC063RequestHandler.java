package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Date;
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
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;

/**
 * @Description 在线业务查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC063RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC063RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private CommonQueryService commonQueryService;
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Page page = getPage(request.getAppHead());
        Ret ret = new Ret();
        try {
        	OnlineQueryBean queryBean = QueryParamMap(request);
        	List list = null;//在线银承
        	List list2 = null;//在线流贷
        	//0：在线银承  1：在线流贷
        	if(PublicStaticDefineTab.PRODUCT_YC.equals(queryBean.getOnlineProtocolType())){
        		list = pedOnlineAcptService.queryOnlineAcptContractList(queryBean,page);
        	}else if(PublicStaticDefineTab.PRODUCT_LD.equals(queryBean.getOnlineProtocolType())){
        		list = pedOnlineCrdtService.queryOnlineCrdtContractList(queryBean,page);
        	}else{
        		list = pedOnlineCrdtService.queryOnlineContractList(queryBean,page);
        	}
			QueryResult result =commonQueryService.loadDataByResult(list, "UnusedAmt");

        	if(null != list && list.size()>0){
        		OnlineQueryBean bean = null;
    			for(int i=0;i<list.size();i++){
    				Map map = new HashMap();
    				bean = (OnlineQueryBean) list.get(i);
    				map.put("BUSS_INFO_ARRAY.ONLINE_BUSS_TYPE", bean.getOnlineProtocolType());      //在线业务类型    
    				map.put("BUSS_INFO_ARRAY.ONLINE_BUSS_PROTOCOL_NO", bean.getOnlineAcptNo());     //在线业务协议编号     
		 	        map.put("BUSS_INFO_ARRAY.ONLINE_BUSS_CONTRACT_NO", bean.getContractNo());      //在线业务合同号    
		 	        map.put("BUSS_INFO_ARRAY.BUSS_BALANCE", bean.getUnusedAmt());                       //业务余额 
		 	       	Date operDate = bean.getChangeDate();
		 	       	String date = DateUtils.toString(operDate, DateUtils.ORA_DATE_FORMAT);
		 	        String time = DateUtils.toString(operDate, DateUtils.ORA_TIME2_FORMAT);
		 	        map.put("BUSS_INFO_ARRAY.OPER_DATE", date);      //操作日期    
		 	        map.put("BUSS_INFO_ARRAY.OPER_TIME", time);      //操作时间     
		 	        map.put("BUSS_INFO_ARRAY.BUSS_STATUS",this.getStatusTrans(bean.getStatus()));   //状态     
		 	        response.getDetails().add(map);
    			}
    			setPage(response.getAppHead(), page,result.getTotalAmount().toString());
    		}else{
    			response.getAppHead().put("TOTAL_NUM", 0); // 本页记录总数
    			response.getAppHead().put("TOTAL_ROWS", 0); // 总笔数
    		}
        	response.getBody().put("CORE_CLIENT_NO", queryBean.getCustNumber());      //核心客户号  
        	response.getBody().put("TOTAL_AMT", result.getTotalAmount());      //总金额

			// 构建响应对象
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线业务查询异常");
		
		}
		response.setRet(ret);
        return response;
    }
    
    /**
     * 码值翻译
     * @param status
     * @return
     * @author Ju Nana
     * @date 2021-8-17下午3:20:57
     */
    private String getStatusTrans(String status){
    	String returnStatus = "";
    	if(PublicStaticDefineTab.ONLINE_DS_001.equals(status)||PublicStaticDefineTab.ONLINE_DS_002.equals(status)||PublicStaticDefineTab.ONLINE_DS_006.equals(status)||PublicStaticDefineTab.ONLINE_DS_007.equals(status)){
    		returnStatus = "0";//处理中
    	}
    	if(PublicStaticDefineTab.ONLINE_DS_004.equals(status)||PublicStaticDefineTab.ONLINE_DS_005.equals(status)){
    		returnStatus = "2";//失败
    	}
    	if(PublicStaticDefineTab.ONLINE_DS_003.equals(status)){
    		returnStatus = "1";//成功
    	}
		return returnStatus;
    	
    }
    
    /**
	 * @Description: 请求数据处理
	 * @param request
	 * @return OnlineQueryBean
	 * @author wss
	 * @date 2021-5-08
	 */
	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		OnlineQueryBean bean = new OnlineQueryBean();
		Map map = request.getBody();
		bean.setCustNumber(getStringVal(map.get("CORE_CLIENT_NO")));//核心客户号  必输
		bean.setEbkCustNo(getStringVal(map.get("CMS_CLIENT_NO")));//网银客户号 必输
		bean.setOnlineProtocolType(getStringVal(map.get("ONLINE_BUSS_TYPE")));//在线业务类型  必输
		bean.setOnlineNo(getStringVal(map.get("ONLINE_BUSS_PROTOCOL_NO")));//在线业务协议编号
		bean.setContractNo(getStringVal(map.get("ONLINE_BUSS_CONTRACT_NO")));//在线业务合同号
		bean.setStartDate(getDateVal(map.get("BUSS_START_DATE")));//业务开始日期
		bean.setEndDate(getDateVal(map.get("BUSS_END_DATE")));//业务结束日期
		
		return bean;
	}

}
