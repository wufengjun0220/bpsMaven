package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.framework.common.util.StringUtil;

/**
 * PJC043(网银接口)客户经理信息查询
 * @author 
 *
 */
public class PJC043RequestHandler extends PJCHandlerAdapter{
	@Autowired
	private UserService userService;
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
		ReturnMessageNew response = new ReturnMessageNew();
		Map map = request.getBody();
		Ret ret = new Ret();
		User user = new User();
		try {
			String customerNo = getStringVal(map.get("BANK_MANAGER_ID"));
			
			user = userService.getUserByLoginName(customerNo);
			if(user!=null){
				boolean isManager = false;//是否为客户经理
				if(user.getRoleList()!=null && user.getRoleList().size()>0){
					for(Role role : (List<Role>)user.getRoleList()){
						if( StringUtil.isNotBlank(role.getCode())){						
							String roleCode  = role.getCode();
							if(PoolComm.roleCode6.equals(roleCode)){
								isManager = true;
								break;
							}
						}
					}
				}
				
				if(!isManager){
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("无此客户经理信息!");		
				}else{					
					response.getBody().put("CUST_MANAGER_NAME", user.getName());
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("查询成功!");				
				}
			}else{
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("无此客户经理信息!");		
			}
		} catch (Exception e) {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);//
			ret.setRET_MSG("网银接口PJC043-客户经理信息查询异常! 票据池内部执行错误");
		}
		response.setRet(ret);
		return response;
	}
}
