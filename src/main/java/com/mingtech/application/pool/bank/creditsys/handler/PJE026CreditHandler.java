package com.mingtech.application.pool.bank.creditsys.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.UserService;

/**
 * @Title: MIS接口 PJE026
 * @Description: 在线贴现经办校验
 */
public class PJE026CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE026CreditHandler.class);

	@Autowired
	private UserService userService;
	@Autowired
	private DepartmentService departmentService;

	/**
	 * MIS接口 PJE026 在线贴现经办校验
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		try {
				OnlineQueryBean queryBean = QueryParamMap(request);				
				OnlineQueryBean bean = new OnlineQueryBean();
				
				//经办人
				User user=new User();
				if(StringUtils.isNotBlank(queryBean.getAppNo())){
					
					user = userService.getUserByLoginName(queryBean.getAppNo());
					if(null == user){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("票据池系统无经办客户经理信息");
						response.setRet(ret);
						return response;
					}else{
						boolean isManager = false;//是否为客户经理
		    			if(user.getRoleList()!=null && user.getRoleList().size()>0){
		    				for(Role role : (List<Role>)user.getRoleList()){
		    					if( StringUtils.isNotBlank(role.getCode())){						
		    						String roleCode  = role.getCode();
		    						if(PoolComm.roleCode6.equals(roleCode)){
		    							isManager = true;
		    							break;
		    						}
		    					}
		    				}
		    			}
						if(isManager){
							//经办人名称
							if(StringUtils.isNotBlank(queryBean.getAppName()) && !queryBean.getAppName().equals(user.getName())){
								ret.setRET_CODE(Constants.TX_FAIL_CODE);
								ret.setRET_MSG("经办人名称与票据池系统签约信息不符");
								response.setRet(ret);
								return response;
							}
							
							boolean isBranchNo = false;//入账机构号是否与客户经理归属机构的内部机构号或者其下属机构的“内部机构号”相同
							//入账机构号是否与客户经理归属机构的内部机构号或者其下属机构的“内部机构号”
							//查询客户经理归属机构的内部机构号或者其下属机构的“内部机构号”
							Department dept = (Department) departmentService.load(user.getDeptId());
							List list = departmentService.getAllChildrenInnerCodeList(dept.getInnerBankCode(), 1);
							for (int i = 0; i < list.size(); i++) {
								String branchNo = (String) list.get(i);
								if(branchNo.equals(queryBean.getSignBranchNo())){
									isBranchNo = true;
									break;
								}
							}
							if(!isBranchNo){
								ret.setRET_CODE(Constants.TX_FAIL_CODE);
								ret.setRET_MSG("入账机构与客户经理票据池归属机构不相符");
								response.setRet(ret);
								return response;
							}
		    			}
					}
					
				}
				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("校验成功");
				
				
		} catch (Exception ex) {
			logger.error("PJE026-在线贴现经办校验!", ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线贴现经办校验!票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * @Description: 请求数据处理
	 * @param request
	 * @return OnlineQueryBean
	 */
	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		OnlineQueryBean bean = new OnlineQueryBean();
		Map body = request.getBody();

		bean.setAppName(getStringVal(body.get("APPER_NAME")));//经办人名称
		bean.setAppNo(getStringVal(body.get("APPER_ID")));//经办人编号
		bean.setSignBranchNo(getStringVal(body.get("SIGN_BRANCH_NO")));//签约机构号
		
		return bean;
	}
	
}
