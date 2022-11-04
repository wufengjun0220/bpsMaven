package com.mingtech.application.sysmanage.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.dto.VerifyResult;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.SecurityEncode;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.common.util.SystemConfig;
import com.mingtech.framework.core.page.Page;
@Controller
public class UserController extends BaseController{

	private static final Logger logger = Logger.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	private SystemConfig systemConfig;// 系统配置文件，提供用户初始密码
	@Autowired
	private DictCommonService dictCommonService;
	/**
	 * 指定机构下的所有用户
	 * @param userManager.deptId
	 * @since Dec 1, 2008
	 * @author zhaoqian
	 */
	@RequestMapping(value="/listUser")
	public void listUser(User user){
		try{
			Page page = this.getPage();
			List list = userService.getUsersJSON(user, page);
			
			Map map = new HashMap();
			map.put("totalProperty", "results," + page.getTotalCount());
			map.put("root", "rows");
			String json = JsonUtil.fromCollections(list, map);
			
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
			
		}catch (Exception e){
			logger.error("查询用户失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询用户失败："+e.getMessage());
		}
	}
	
	/**
	 * 用户管理数据导出
	 * 
	 */
	@RequestMapping(value="/listUserExpt")
	public void listUserExpt(User user){
		int[] num = { };
		String[] typeName = { "amount" };
		try{
			String json = "";
			Page page = this.getPage();
			List list = userService.getUsersJSON(user, page);
			List list1 = userService.findUserExpt(list, getPage());
			
			String ColumnNames = "loginName,name,statusStr,pjsUserName,roleStr,createTime";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("loginName", "登录名");
			mapinfo.put("name", "姓名");
			mapinfo.put("statusStr", "状态");
			mapinfo.put("pjsUserName", "机构");
			mapinfo.put("roleStr", "角色描述");
			mapinfo.put("createTime", "创建日期");

			
			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list1, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("用户列表.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
			
		}catch (Exception e){
			logger.error("用户列表导出失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("用户列表导出失败："+e.getMessage());
		}
	}

	/**
	 * 方法说明:根据用户信息获取用户角色信息
	 * @param userId 用户id
	 * @author  ZJY
	 * @date 20190529
	 */
	@RequestMapping("/getRoleByUserId")
	public void getRoleByUserId(String userId){
		String json = "";
		try {
			List roleByUserId = userService.getRoleByUserId(userId);
			if(roleByUserId != null && roleByUserId.size()>0)
			json = JsonUtil.buildJson(roleByUserId, this.getPage().getTotalCount());
			if(StringUtil.isBlank(json)){
				json=RESULT_EMPTY_DEFAULT;
			}
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "数据查询失败"+e.getMessage();
		}
		this.sendJSON(json);
	}

	/**
	 * 重置密码 <p>方法名称: resetPassword|描述: </p>
	 */
	@RequestMapping(value="/resetPassword",method = RequestMethod.POST)
	public void resetPassword(String userId){
		try{
			User user = (User) userService.load(userId,User.class);
			String pwd = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.SYSTEM_MANAGE_INIT_USER_PWD);
			if(pwd == null){
				throw new Exception("用户初始化密码未设定，请联系系统管理员");
				}
			user.setPassword(SecurityEncode.EncoderByMd5(pwd));
			user.setPsswdUpdate(new Date());
			userService.txStore(user);
			this.sendJSON("重置密码成功");
		}catch (Exception e){
			logger.error("系统错误"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("系统错误"+e.getMessage());// 系统错误
		}
	}



	/**
	 * 修改密码 1.检查session是否过期 2.核对原始密码输入是否正确 3.修改密码 两次新密码输入是否相同放在页面检查 <p>方法名称:
	 * changePassword|描述: </p>
	 * @return
	 */
	@RequestMapping(value="/changePassword",method = RequestMethod.POST)
	public void changePassword(User user){
		try {
			VerifyResult result = userService.txChangePassword(user, getCurrentUser().getId());
			this.sendJSON(JSON.toJSONString(result));
		} catch (Exception e) {
			logger.error("系统错误"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("系统错误"+e.getMessage());// 系统错误
		}
	}
	
	/**
	 * 根据用户ID获取用户及角色信息
	 * getUserById|描述: </p>
	 * @return
	 */
	@RequestMapping(value="/getUserById")
	public void getUserById(String userId){
		try{
			List list = userService.getRoleByUserId(userId);
			String[] ids = roleService.getRoleListId(list);
			String json = JSON.toJSONString(ids);
			this.sendJSON(json);
		}catch (Exception e){
			logger.error("根据用户ID获取用户及角色信息失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("根据用户ID获取用户及角色信息失败："+e.getMessage());
		}
	}

	
	/**
	 * 方法说明: 保存或更新用户对象
	 * @param
	 * @author E-mail:
	 * @return
	 * @date 2009-3-9 上午11:26:39
	 */
	@RequestMapping(value="/saveUser",method = RequestMethod.POST)
	public void saveUser(User user){
		try{
			User loginUser = getCurrentUser();//当前操作人
			
			User uu = userService.checkUserExistence(user,loginUser);
			if(uu!=null){
				if(uu.getLoginName().equals(user.getLoginName())){
					this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
					this.sendJSON("登录名【"+user.getLoginName()+"】已存在，请重新输入");
					return;
				}
			}
				if(user.getId()!=null&&!user.getId().equals("")){// 更新
					User tmpUser = (User) userService.load(user.getId());
					tmpUser.setName(user.getName());
					tmpUser.setLoginName(user.getLoginName());
					tmpUser.setTelPhone(user.getTelPhone());
					tmpUser.setEmail(user.getEmail());
					List tmp = new ArrayList();
//					if(ids != null && ids.length > 0){
//						for(int i = 0; i < ids.length; i++){
//							Role role = (Role) roleService.load(ids[i]);
//							tmp.add(role);
//						}
//					}
					List rList = user.getRoleList();
					if(rList != null && rList.size()>0){
						for(int i = 0; i < user.getRoleList().size(); i++){
							Role role = (Role) roleService.load(rList.get(i).toString());
							tmp.add(role);
						}
					}
					tmpUser.setRoleList(tmp);
					tmpUser.setStatus(user.getStatus());
					tmpUser.setCreateTime(new Date());
					tmpUser.setDeptId(user.getDeptId());
					tmpUser.setPsswdUpdate(new Date());
					userService.txStore(tmpUser);
					this.sendJSON("更新用户成功");
					logger.debug("更新用户【"+user.getName()+"】操作人【"+loginUser.getLoginName()+"】！");
				}else{
//					if(ids != null && ids.length > 0){
//						List roleList = new ArrayList();
//						for(int i = 0; i < ids.length; i++){
//							Role role = (Role) roleService.load(ids[i]);
//							roleList.add(role);
//						}
						List temp = new ArrayList();
						List rList = user.getRoleList();
						if(rList != null && rList.size()>0){
							for(int i = 0; i < user.getRoleList().size(); i++){
								Role role = (Role) roleService.load(rList.get(i).toString());
								temp.add(role);
							}
						}
						user.setRoleList(temp);	
//					if(StringUtil.isStringEmpty(user.getPassword())){// 初始化用户密码
//						user.setPassword(SecurityEncode
//								.EncoderByMd5(systemConfig.getInitPassword()));
//					}
					if(StringUtils.isEmpty(user.getPassword())){// 初始化用户密码
						String pwd = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.SYSTEM_MANAGE_INIT_USER_PWD);
						if(pwd == null){
							this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
							this.sendJSON("用户初始化密码未设定，请联系系统管理员。");
							return;
						}
						user.setPassword(SecurityEncode
								.EncoderByMd5(pwd));
					}
					//liuxiaodong  update start
					if(null != user.getDepartment()){
						user.setDeptId(user.getDepartment().getId());
					}
//					user.setDeptId(user.getDeptId());
					user.setCreateTime(new Date());
					//liuxiaodong  update end
					user.setId(null);
					user.setCreateTime(new Date());
					user.setPsswdUpdate(new Date());
					userService.txStore(user);
					this.sendJSON("保存用户成功");
					logger.debug("添加新用户【"+user.getName()+"】操作人【"+loginUser.getLoginName()+"】！");
				}
				//new SysOrgManagementImpl().setNotify(true);
//			}else{
//				this.addActionMessage("登录名重复，请修改！");
//				return ERROR;
//			}
		}catch (Exception e){
			logger.error("数据库操作失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("数据库操作失败："+e.getMessage());
		}
	}


	
	/**
	 * 方法说明: 删除用户
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-18 下午03:47:34
	 */
	@RequestMapping(value="/deleteUser",method = RequestMethod.POST)
	public void deleteUser(String userIds){
		try{

			String[] ids = userIds.split(",");
			List list = Arrays.asList(ids);
			int size = list.size();
			for(int i=0;i<size;i++){
			   User loadUser = (User)userService.load(list.get(i).toString());
			   loadUser.getRoleList().clear();
			   userService.txDelete(loadUser);
			}
		    this.sendJSON("删除成功");							
		}catch (Exception e){
			logger.error("删除用户失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除用户失败："+e.getMessage());
		}
	}
	public SystemConfig getSystemConfig(){
		return systemConfig;
	}

	public void setSystemConfig(SystemConfig systemConfig){
		this.systemConfig = systemConfig;
	}


}
