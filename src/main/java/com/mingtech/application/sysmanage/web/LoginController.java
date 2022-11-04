package com.mingtech.application.sysmanage.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.runmanage.service.RunStateService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.ResourceService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.application.sysmanage.vo.TreeNodeEx;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.application.utils.JWTTokenUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.common.util.SecurityEncode;

/**
 *<p>系统登录登出控制层逻辑实现</p>
 *@author h2 2021-04-19
 */
@Controller
public class LoginController extends BaseController {

	private static final Logger logger = Logger.getLogger(LoginController.class);
	@Autowired
	private UserService userService;//用户管理服务
	@Autowired
	private RunStateService runStateService;
	@Autowired
	private RedisUtils redisrCache;
	@Autowired
	private ResourceService resourceService;//资源管理服务
	
	@Autowired
	private RoleService roleService;

	
    /**
     *<p>票据池管理系统登录</p>
     * @param user 当前登录用户
     */
	@RequestMapping(value="/login",method = RequestMethod.POST)
	public void login(User user ) {
		try {
			if(StringUtils.isBlank(user.getLoginName())){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("用户名不能为空！");
				return;
			}
//			if(StringUtils.isNotBlank(user.getPassword())&& user.getPassword().equals("111111")){
//				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
//				this.sendJSON("密码不为初始密码！");
//				return;
//			}
			
			User curUser = null;
			if (ProjectConfig.getInstance().isValidateFromCoreSys()
					&& !user.getLoginName().equals("root")
					&& !user.getLoginName().equals("009898")
					&& !user.getLoginName().equals("009897")) {// root，009898用户不去核心校验
				logger.info("去核心校验密码");
				curUser = userService.validateUserFromCoreSys(user, false);
			} else {
				curUser = userService.validateUserNoIneerBankCode(user);
			}
			if (curUser == null) {
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("用户名或密码输入错误！");
				return;
			}
			if(curUser.getStatus() == 0){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("用户名已禁用，请联系管理员！");
				return;
			}
			if ("1".equals(curUser.getAgentFlag())) {//
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("代理行登录柜员，无权登录票据系统!");
				return;
			}
			// 根据当前用户的角色\角色权限
			List roleList = curUser.getRoleList();
			List<String> roleIds = new ArrayList<String>();
			int roleCount = roleList.size();
			if(roleCount == 0){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("该用户未分配权限!");
				return;
			}
			String type = "0";//签约维护界面的资源分配
			for(int i=0; i < roleCount; i++){
				Role role = (Role) roleList.get(i);
				if(role.getCode().equals("A00001")){
					//角色为管理员,部门为分行则展示
					if(curUser.getDepartment().getLevel() == 1){//总行管理员
						//分配签约维护(总行)
						type = "1";
					}else{
						type = "2";
					}
				}
				roleIds.add(role.getId());
			}
			/* 营业日期 */
			Date busiDate = runStateService.getWorkDateTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(busiDate);

			
			List tmpResourceList = resourceService.queryResourceListByRole(roleIds);
			// 资源结果集
			List resourceList = new ArrayList();
			Map<String,String> resMap = new HashMap<String,String>();
			Resource res = null;
			for (int j = 0; j <tmpResourceList.size(); j++) {
				res = (Resource) tmpResourceList.get(j);
				if(res.getCode().equals("openpoollist")){//签约维护总行
					if(!type.equals("1")){
						continue;
					}
				}
				if(res.getCode().equals("openpoolListSubbranch")){//签约维护分行
					if(!type.equals("2")){
						continue;
					}
				}
				if (res.getType() == Resource.menu) {
					TreeNodeEx tmp = new TreeNodeEx();
					tmp.setText(res.getName());
					tmp.setCode(res.getCode());
					tmp.setId(res.getId());
					tmp.setUrl(res.getUrl());
					if(StringUtils.isEmpty(res.getIconCss())) {
						tmp.setIcon("api");
					} else {
						tmp.setIcon(res.getIconCss());
					}
					if(res.getPid() != null) {
						tmp.setPid(res.getPid());
					}	
					//将资源信息放入map中，后续用于权限拦截
					if(StringUtils.isNotBlank(res.getActionName())) {
						resMap.put(res.getActionName(), res.getUrl());
					}
					resourceList.add(tmp);
				}
			}
			//生成令牌迁移-将登录产生的令牌作为账号登录唯一标识，用于一个账号多次登录校验
			String token = JWTTokenUtil.getInstance().getToken(curUser.getDepartment().getPjsMemberCode(), curUser.getLoginName(), curUser.getPassword());
			
			this.putSessionContent(curUser,resMap,token);
			
			
			Map map = new HashMap();
			User simpleUser = new User();
			simpleUser.setName(curUser.getName());
			simpleUser.setLoginName(curUser.getLoginName());
			simpleUser.setInnerBankCode(curUser.getInnerBankCode());
			
			
			/********************************2021-09-28判断密码剩余天数**********************/
			long diff = (new Date()).getTime() - curUser.getPsswdUpdate().getTime() ;
            long da = diff / (1000 * 60 * 60 * 24);
            int dayInRange = Integer.parseInt(String.valueOf(da));
//			int dayInRange = DateUtils.getDayInRange(curUser.getPsswdUpdate(),new Date());
			int vDays = Integer.parseInt(SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.PASSWORD_VOLID_DAYS));
			int days = vDays-dayInRange;
			simpleUser.setSurplusDays(days);
			/********************************2021-09-28判断密码剩余天数**********************/

			/********************************2021-09-28判断初始化密码**********************/
			String pwdInit = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.SYSTEM_MANAGE_INIT_USER_PWD);
			if(pwdInit == null){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("用户初始化密码未设定，请联系系统管理员。");
				return;
			}
			String md5PwdInit = SecurityEncode.EncoderByMd5(pwdInit);
			if(md5PwdInit.equals(curUser.getPassword())){
				//跳转到密码修改界面
				simpleUser.setIsOriginalPassword(0);
			}
			
			/********************************2021-10-18判断初始化密码**********************/

			
			map.put("user", simpleUser);
			map.put("menu", resourceList);
			Department simpleDept = new Department();
			simpleDept.setId(simpleDept.getId());
			simpleDept.setName(curUser.getDepartment().getName());
			simpleDept.setInnerBankCode(curUser.getDepartment().getInnerBankCode());
			simpleDept.setBankNumber(curUser.getDepartment().getBankNumber());
			simpleDept.setPjsMemberCode(curUser.getDepartment().getPjsMemberCode());
			simpleDept.setPjsBrchNo(curUser.getDepartment().getPjsBrchNo());//票交所机构码
			simpleDept.setPjsUserNo(curUser.getPjsUserNo());//票交所默认交易员ID
			simpleDept.setPjsUserName(curUser.getDepartment().getPjsUserName());//票交所默认交易员名称 
			simpleDept.setOrgCode(curUser.getDepartment().getOrgCode());
			map.put("dept", simpleDept);
			Map sysRunMap = new HashMap();
			sysRunMap.put("ecdsDate", DateUtils.toString(new Date(), DateUtils.ORA_DATES_FORMAT));
			map.put("sysRun", sysRunMap);
			String json = JSON.toJSONString(map);
			this.sendLoginJSON(json,token);
			logger.info("用户 [ " + curUser.getName() + " ] 登录成功");
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_LOGIN,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(ErrorCode.ERR_LOGIN+ e.getMessage());
		}
	}
	
	//	判断当前用户权限
	@RequestMapping(value="/checkUser")
	public void checkUser(User user){
		try {
//			if(user== null){
				user = this.getCurrentUser();
//			}
			String str = roleService.queryRoleDeptByUser(user);
			String flag = "0";
			if(str != null){
				if(str.equals("0") || str.equals("2")){//总行管理员可查
					flag = "1";
				}
			}
			sendJSON(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * edit 2021209 将redis中存放的用户、资源、机构对象转换为json字符串进行存放
	 */
	private void putSessionContent(User user,Map resMap,String token) throws Exception{
		// 缓存票交所会员代码 
		user.setMemCode(user.getDepartment().getPjsMemberCode());
		user.setRoleList(null);
		String strTimeOut = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.SYS_SESSION_TIMEOUT);
		if(StringUtils.isEmpty(strTimeOut)){
			strTimeOut = "20";
		}
		int timeOut = Integer.valueOf(strTimeOut);//分钟
		timeOut = timeOut * 60;//描述
		//redis中key使用票交所会员码+登录账号组成，格式为:pjsMemberCode_loginName
		String key = user.getDepartment().getPjsMemberCode()+"_"+user.getLoginName();
		Map dataMap = new HashMap();
		dataMap.put("user",JSON.toJSONStringWithDateFormat(user, "yyyy-MM-dd", SerializerFeature.WriteDateUseDateFormat));
		dataMap.put("dept",JSON.toJSONString(user.getDepartment()));
		dataMap.put("res", resMap);
		dataMap.put("authCode", token);//账号登录标识号
		redisrCache.hmset(key, dataMap, timeOut);
	}

	@RequestMapping(value="/logout",method = RequestMethod.POST)
	public void logout() {
		// 清掉缓存
		User curUser = (User) this.getCurrentUser();
		if (curUser != null) {
			//redis中key使用票交所会员码+登录账号组成，格式为:pjsMemberCode_loginName
			String key = curUser.getDepartment().getPjsMemberCode()+"_"+curUser.getLoginName();
			try{
				redisrCache.remove(key);
			}catch(Exception e){
				logger.error("业务处理异常",e);
			}
//			userLoginMonitorService.txDeleteUserLoginInfo(curUser.getId());//删除用户登录信息
			logger.info("用户 [ " + curUser.getName() + " ] 注销成功");
		}
	}


}
