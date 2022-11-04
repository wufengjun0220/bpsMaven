package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.online.manage.domain.PedOnlineBlackInfo;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.utils.ErrorCode;

/**
 * 在线协议禁入名单客户查询
 * @author liu Wei
 * @version v1.0
 * @date 2022-9-29
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class PJC074RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC074RequestHandler.class);
	@Autowired
	private CommonQueryService commonQueryService;

	/**
	 * 在线协议禁入名单客户查询
	 * */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
    	Ret ret = new Ret();
        try {
	        Map map = request.getBody();
	        String custNo = getStringVal(map.get("CLIENT_NO"));		//	入账机构所号
	        
	        CommonQueryBean queryBean = new CommonQueryBean();
	        queryBean.setCustNo(custNo);

	        List<PedOnlineBlackInfo> infos = commonQueryService.loadDebarList(queryBean,null,null);
    		if(null != infos && infos.size() > 0){
    			for (PedOnlineBlackInfo pedOnlineBlackInfo : infos) {
    				response.getBody().put("CLIENT_NO", pedOnlineBlackInfo.getCustNo());
    				response.getBody().put("CLIENT_NAME", pedOnlineBlackInfo.getCustName());
    				response.getBody().put("OPEN_FLAG", pedOnlineBlackInfo.getOpenFlag());
    				response.getBody().put("STATUS", pedOnlineBlackInfo.getStatus());
    				response.getBody().put("VALIDITY_DATE", pedOnlineBlackInfo.getValidDate());
    				response.getBody().put("VALIDITY_DATE_TYPE", pedOnlineBlackInfo.getDateType());
				}
    			
    			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
    			response.setRet(ret);
    			return response;
    		}else{
    			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
    			response.getBody().put("CLIENT_NO", custNo);
				response.getBody().put("OPEN_FLAG", "0");
    		}
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池协议禁入名单查询异常,请联系客户经理！");
		
		}

        response.setRet(ret);
        return response;
    }
}
