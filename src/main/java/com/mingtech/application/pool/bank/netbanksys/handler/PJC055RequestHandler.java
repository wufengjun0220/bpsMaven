package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.framework.core.page.Page;

/**
 * @Description  在线业务短信收件人信息查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC055RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC055RequestHandler.class);
	
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired 
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
    	Page page = getPage(request.getAppHead());
        Ret ret = new Ret();
        try{
        	 OnlineQueryBean queryBean = QueryParamMap(request);
        	 //查询具体的在线协议
        	 queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
        	 if(queryBean.getOnlineProtocolType().equals(PublicStaticDefineTab.PRODUCT_001)){
        		 //0：在线银承
        		 if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
        			 queryBean.setOnlineAcptNo(queryBean.getOnlineNo());
        		 }
        		 PedOnlineAcptProtocol acpt = pedOnlineAcptService.queryOnlineAcptProtocol(queryBean);
        		 if(null==acpt){
	    			    logger.info("PJC055-在线银承协议不存在!");
	    				ret.setRET_CODE(Constants.TX_FAIL_CODE);
	    				ret.setRET_MSG("在线银承协议不存在!");
	    				response.setRet(ret);
	    		        return response;
     		 }
        		 queryBean.setOnlineNo(acpt.getOnlineAcptNo());
        	 }else{//1：在线流贷
        		 if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
        			 queryBean.setOnlineCrdtNo(queryBean.getOnlineNo());
        		 }
        		 PedOnlineCrdtProtocol crdt = pedOnlineCrdtService.queryOnlineProtocol(queryBean);
        		 if(null==crdt){
	    			    logger.info("PJC055-在线流贷协议不存在!");
	    				ret.setRET_CODE(Constants.TX_FAIL_CODE);
	    				ret.setRET_MSG("在线流贷协议不存在!");
	    				response.setRet(ret);
	    		        return response;
        		 }
        		 queryBean.setOnlineNo(crdt.getOnlineCrdtNo());
        	 }
             queryBean.setAddresseeRole(PublicStaticDefineTab.ROLE_1);//角色
             List list = onlineManageService.queryOnlineMsgInfoList(queryBean,page);
             if(null != list && list.size()>0){
             	for(int i=0;i<list.size();i++){
             		Map map = new HashMap();
             		PedOnlineMsgInfo info = (PedOnlineMsgInfo) list.get(i);
             		map.put("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NO", info.getAddresseeNo().equals("-")?"":info.getAddresseeNo());//联系人编号
             		map.put("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NAME", info.getAddresseeName().equals("-")?"":info.getAddresseeName());//联系人名称
             		map.put("CONTECT_INFO_ARRAY.CONTECT_CLIENT_TYPE", info.getAddresseeRole().equals("-")?"":info.getAddresseeRole());//联系人身份
             		map.put("CONTECT_INFO_ARRAY.CONTACT_PHONE_NO", info.getAddresseePhoneNo().equals("-")?"":info.getAddresseePhoneNo());//联系人电话
             		response.getDetails().add(map);
             	}
    			setPage(response.getAppHead(), page);
         		response.getBody().put("ONLINE_PROTOCOL_NO", queryBean.getOnlineNo());//在线业务协议编号
         		response.getAppHead().put("TOTAL_ROWS", list.size());//总数
             	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
             }else{
            	response.getAppHead().put("TOTAL_ROWS", "0");//总数
             	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
             	ret.setRET_MSG("在线业务短信收件人信息！：无符合条件数据");
             }
        }catch (Exception e) {
        	logger.error("PJC055-在线业务短信收件人信息查询异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池内部处理异常！");
		}
       
        response.setRet(ret);
        return response;
    }
    /**
   	 * @Description: 请求数据处理
   	 * @param request
   	 * @return OnlineQueryBean
   	 * @author wss
   	 * @date 2021-4-29
   	 */
   	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
   		OnlineQueryBean bean = new OnlineQueryBean();
   		Map body = request.getBody();
   		bean.setCustNumber(getStringVal(body.get("CORE_CLIENT_NO")));//核心客户号
   		bean.setEbkCustNo(getStringVal(body.get("CMS_CLIENT_NO")));//网银客户号
   		bean.setOnlineNo(getStringVal(body.get("ONLINE_PROTOCOL_NO")));//在线业务协议编号 （非必输）
   		bean.setAddresseeName(getStringVal(body.get("CONTECT_CLIENT_NAME")));//联系人名称
   		String type =getStringVal(body.get("ONLINE_PROTOCOL_TYPE"));
   		String typeInfo="";
   		if(PublicStaticDefineTab.PRODUCT_YC.equals(type)){
   			typeInfo=PublicStaticDefineTab.PRODUCT_001;
   		}else if(PublicStaticDefineTab.PRODUCT_LD.equals(type)){
   			typeInfo=PublicStaticDefineTab.PRODUCT_002;
   		}
   		bean.setOnlineProtocolType(typeInfo);//在线业务类型

   		return bean;
   	}


}
