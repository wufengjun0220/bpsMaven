package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.utils.ErrorCode;

/**
 * 票据出池校验
 * @author liu Wei
 * @version v1.0
 * @date 2022-9-29
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class PJC073RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC073RequestHandler.class);
	@Autowired
	private DepartmentService departmentService;

	/**
	 * 机构在线业务开关查询
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
    	Ret ret = new Ret();
        try {
	        Map map = request.getBody();
	        String netPointCode = getStringVal(map.get("ENTER_ACCOUNT_BRANCH_NO"));		//	入账机构所号

    		Department dto = departmentService.getDepartmentByInnerBankCode(netPointCode);
    		if(null != dto){
    			if(!("5".equals(dto.getLevel()) || "7".equals(dto.getLevel()))){	//	一级武汉支行和非武汉一级支行直接返回
    				dto = departmentService.getDeptById(dto.getPid());
    			}
    			
    			response.getBody().put("ENTER_ACCOUNT_BRANCH_NO", dto.getInnerBankCode());
    			response.getBody().put("BPS_NO", dto.getId());
    			response.getBody().put("ONLINE_DISCOUNT_FLAG", dto.getTxFlag());
    			response.getBody().put("ONLINE_ACCEPTANCE_FLAG", dto.getYcFlag());
    			response.getBody().put("ONLINE_LOAN_FLAG", dto.getLdFlag());
    			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
    		}else{
    			ret.setRET_CODE(Constants.TX_FAIL_CODE);
    			ret.setRET_MSG("未查询到相关的机构信息！");
    			response.setRet(ret);
    			return response;
    		}
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池机构查询异常,请联系客户经理！");
		
		}

        response.setRet(ret);
        return response;
    }
}
