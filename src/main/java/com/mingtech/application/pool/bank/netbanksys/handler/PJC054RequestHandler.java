package com.mingtech.application.pool.bank.netbanksys.handler;

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
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;

/**
 * @Title: EBK 接口 PJC054
 * @Description 在线业务短信收件人信息维护
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC054RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC054RequestHandler.class);
	@Autowired
	private OnlineManageService onlineManageService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        try {
        	OnlineQueryBean queryBean = QueryParamMap(request);
        	ret = onlineManageService.txSaveMsgInfo(queryBean);
		} catch (Exception e) {
			logger.error("PJC054-在线业务短信收件人信息维护异常!", e);
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
   		bean.setOnlineNo(getStringVal(body.get("ONLINE_PROTOCOL_NO")));//在线业务协议编号
   		String type =getStringVal(body.get("ONLINE_PROTOCOL_TYPE"));
   		String typeInfo="";
   		if(PublicStaticDefineTab.PRODUCT_YC.equals(type)){
   			typeInfo=PublicStaticDefineTab.PRODUCT_001;
   		}else if(PublicStaticDefineTab.PRODUCT_LD.equals(type)){
   			typeInfo=PublicStaticDefineTab.PRODUCT_002;
   		}
			bean.setOnlineProtocolType(typeInfo);//在线业务类型

   		if(null !=request.getDetails() && request.getDetails().size()>0){
   			List list = new ArrayList();
   			PedOnlineMsgInfo info = null;
   			for(int i=0;i<request.getDetails().size();i++){
   				Map map = (Map) request.getDetails().get(i);
   				info = new PedOnlineMsgInfo();
   				info.setAddresseeNo(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NO")));//联系人编号
   				info.setAddresseeName(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NAME").equals("")?"-":getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_NAME"))));//联系人名称
   				info.setAddresseeRole(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_TYPE").equals("")?"-":getStringVal(map.get("CONTECT_INFO_ARRAY.CONTECT_CLIENT_TYPE"))));//联系人身份
   				info.setAddresseePhoneNo(getStringVal(map.get("CONTECT_INFO_ARRAY.CONTACT_PHONE_NO").equals("")?"-":getStringVal(map.get("CONTECT_INFO_ARRAY.CONTACT_PHONE_NO"))));//联系人电话
   				info.setModeType(PublicStaticDefineTab.MOD01);//默认为新增
   				info.setOnlineProtocolType(typeInfo);
   				info.setCreateTime(new Date());
   				info.setUpdateTime(new Date());
   				list.add(info);
   			}
   			bean.setDetalis(list);
   		}
   		return bean;
   	}

}
