package com.mingtech.application.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftQueryBeanQuery;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.dto.VerifyResult;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.SecurityEncode;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.common.util.SystemConfig;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

public class UserServiceImpl extends GenericServiceImpl implements UserService{

	private SystemConfig systemConfig;// 系统配置文件，提供用户初始密码
	private DepartmentService departmentService;
	@Autowired
	private PedProtocolService pedProtocolService;
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	
	public User validateUser(User user) throws Exception{
		if(user==null)return null;
		if(!StringUtil.isStringEmpty(user.getPassword())){// 将用户输入的密码加密处理
			user.setPassword(SecurityEncode.EncoderByMd5(user.getPassword()));
		}
		//String query = "select u from User u left join u.c dept where u.loginName = ? and u.password = ? and dept.innerBankCode = ?";
		// 20190222 修改去掉机构号登录
		//String query = "select u from User u ,Department dept where u.deptId=dept.id and  u.loginName = ? and u.password = ? and dept.innerBankCode = ?";
		String query = "select u from User u ,Department dept where u.deptId=dept.id and  u.loginName = ? and u.password = ? ";
		List parameters = new ArrayList();
		parameters.add(user.getLoginName());
		parameters.add(user.getPassword());
		//parameters.add(user.getInnerBankCode());
		List users = this.find(query, parameters);
		if(CollectionUtil.isNotEmpty(users)){
			//修改了通过机构号校验登录用户
			User u = (User) users.get(0);
			Department dept = departmentService.getDeptById(u.getDeptId());
			u.setDepartment(dept);
			return u;
		}
		return null;
	}
	
	public User validateUserNoIneerBankCode(User user){
		if(user==null)return null;
		if(!StringUtil.isStringEmpty(user.getPassword())){// 将用户输入的密码加密处理
			user.setPassword(SecurityEncode.EncoderByMd5(user.getPassword()));
		}
		String query = "select u from User u ,Department dept where u.deptId=dept.id and  u.loginName = ? and u.password = ?";
		List parameters = new ArrayList();
		parameters.add(user.getLoginName());
		parameters.add(user.getPassword());
		List users = this.find(query, parameters);
		if(CollectionUtil.isNotEmpty(users)){
			//修改了通过机构号校验登录用户
			User u = (User) users.get(0);
			Department dept = (Department)departmentService.load(u.getDeptId());
			u.setDepartment(dept);
			return u;
		}
		return null;
	}

	public Class getEntityClass(){
		return User.class;
	}

	public String getEntityName(){
		return StringUtil.getClass(User.class);
	}


	public Department getDeptByInnerBankCode(String innerBankCode){
		List paras = new ArrayList();
		 String hql = "from Department department where department.innerBankCode =?" ;
		 paras.add(innerBankCode);
		List list =  this.find(hql, paras);
		return (list!=null&&list.size()>0)?(Department)list.get(0):null;
	}
 
	public List getUsers(User user, Page pageInfo){

		List paras = new ArrayList();
		// 修改了用户和机构的关联关系，原先是用户和机构是一对一的关系，现在改为了用户和机构是一对多的关系
		// String expression = "select user from User as user where
		// user.department.id = ? and user.loginName != 'root'";
		String expression = "select user from User  user,Department  dept where user.deptId=dept.id  ";
		
		if(null != user){
			if(null != user.getLoginName() && user.getLoginName().trim().length() != 0){
				expression += " and user.loginName =?";
				paras.add(user.getLoginName());
			}	
			if(null != user.getName() && user.getName().trim().length() != 0){
				expression += " and user.name like ?";
				paras.add("%"+user.getName()+"%");
			}
			if(user.getDepartment() != null && null != user.getDepartment().getId() && user.getDepartment().getId().trim().length() !=0){
				expression += "and dept.id =?";
				paras.add(user.getDepartment().getId());
			}
			if(user.getStatus() == 1 || user.getStatus() ==0){
				expression += " and user.status = ?";
				paras.add(user.getStatus());
			}
			if(StringUtils.isNotBlank(user.getDeptId())){
				expression+=" and user.deptId =?";
				paras.add(user.getDeptId());
			}
		}	
		expression += " and (user.agentFlag =? or user.agentFlag = null) ";
		paras.add("0");
		expression += " order by user.createTime DESC";
		return find(expression, paras, pageInfo);
	}

	public List getUsers(String deptId){
		List paras = new ArrayList();
		// 修改了用户和机构的关联关系，原先是用户和机构是一对一的关系，现在改为了用户和机构是一对多的关系
		// String expression = "select user from User as user where
		// user.department.id = ? and user.loginName != 'root'";
		String expression = "select user from User  user, Department  dept where user.deptId=dept.id and  dept.id = ? and user.loginName != 'root'";
		paras.add(deptId);
		expression += " order by user.createTime ASC";
		return find(expression, paras);
	}

	public List getUsersJSON(User user, Page page) throws Exception{
		List usrs = new ArrayList();
		List userList = new ArrayList();
		if(user != null){
			usrs = this.getUsers(user, page);
			for (int i = 0; i < usrs.size(); i++ ){
				User user1 = (User) usrs.get(i);
				Department dept = pedProtocolService.queryDertById(user1.getDeptId());
				user1.setPjsUserName(dept.getName());
				
				String roleStr = "";
				List roleList = this.getRoleByUserId(user1.getId());
				if(roleList != null && roleList.size() > 0){
					for (Object object : roleList) {
						Role role = (Role) object;
						roleStr = roleStr + role.getName() + "、";
					}
				}
				if(roleStr.length() > 0){
					roleStr = roleStr.substring(0, roleStr.length()-1);
				}
				user1.setRoleStr(roleStr);
				userList.add(user1);
			}
		}
		return userList;
//		Map map = new HashMap();
//		map.put("totalProperty", "results," + page.getTotalCount());
//		map.put("root", "rows");
//		return JsonUtil.fromCollections(userList, map);
	}
	
	public String getUsersJSONNew(User user, Page page) throws Exception{
		List usrs = new ArrayList();
		if(user != null){
			usrs = this.getUsersNew(user, page);
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(usrs, map);
	}
	public List getUsersNew(User user, Page pageInfo){

		List paras = new ArrayList();
		// 修改了用户和机构的关联关系，原先是用户和机构是一对一的关系，现在改为了用户和机构是一对多的关系
		// String expression = "select user from User as user where
		// user.department.id = ? and user.loginName != 'root'";
		String expression = "select user from User  user,Department  dept where user.deptId=dept.id  and user.loginName != 'root' ";
		
		if(null != user){
			if(null != user.getLoginName() && user.getLoginName().trim().length() != 0){
				expression += " and user.loginName =?";
				paras.add(user.getLoginName());
			}	
			if(null != user.getName() && user.getName().trim().length() != 0){
				expression += " and user.name like ?";
				paras.add("%"+user.getName()+"%");
			}
			if(null != user.getDeptId()){
				expression += "and dept.id =?";
				paras.add(user.getDeptId());
			}
			if(!"".equals(user.getStatus())){
				expression += " and user.status =? ";
				paras.add(user.getStatus());
			}
			if (null != user.getCreateTimeSta()) {
				expression +=" and user.createTime>=?";
				String beginDate=user.getCreateTimeSta();
				paras.add(DateUtils.parse(beginDate, DateUtils.ORA_DATES_FORMAT));
			}
			if (null != user.getCreateTimeEnd()) {
				expression +=" and user.createTime<=?";
				String endDate=user.getCreateTimeEnd();
				paras.add(DateUtils.parse(endDate, DateUtils.ORA_DATES_FORMAT));
			}
		}
		expression += " and (user.agentFlag =? or user.agentFlag = null) ";
		paras.add("0");
		expression += " order by user.createTime ASC";
		return find(expression, paras, pageInfo);
	}
	public boolean isDeptHasUser(String departmentId, boolean isActive)
			throws Exception{
		String query = "";
		long size = 0;
		List paras = new ArrayList();
		paras.add(departmentId);
		if(isActive){
			query = "select user from User as user where user.deptId = ? and user.status = ?";
			paras.add("1");
			size = dao.getRowCount(query, paras).longValue();
		}else{
			query = "select user from User as user where user.deptId = ?";
			size = dao.getRowCount(query, paras).longValue();
		}
		if(size > 0)
			return true;
		return false;
	}

	public List getAllUser() throws Exception{
		return dao.loadAll(getEntityClass());
	}

	public User getUserById(String userId) throws Exception{
		return (User) dao.load(getEntityClass(), userId);
	}

	public User getUserByLoginName(String loginName) throws Exception {
		String query = "select u from User as u where u.loginName = ?";
		List parameters = new ArrayList();
		parameters.add(loginName);
		List users = this.find(query, parameters);
		if(CollectionUtil.isNotEmpty(users)){
			User u = (User) users.get(0);
			return u;
		}
		return null;
	}
	/**
	 * 查询  用户信息  通过登录名
	 * @param loginName
	 * @return 符合条件的用户列表 User
	 * @throws Exception
	 */
	public List getUsersByLoginNames(String loginName) throws Exception {
		if(loginName==null || loginName.length()<1){
			return new ArrayList();
		}
		String query = "select u from User as u where u.loginName in(:name)";
		String[] nameValues = loginName.split(",");
		List parameters = new ArrayList();
		for(int i=0;i<nameValues.length;i++){
			parameters.add(nameValues[i]);	
		}
		List users =this.find(query, new String[]{"name"}, new Object[]{parameters});
		return users;
	}
	/**
	 * 通过IDS 查询 柜员信息
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List getUsersByIds(String ids) throws Exception {
		if(ids==null || ids.length()<1){
			return new ArrayList();
		}
		String query = "select u from User as u where u.id in(:name)";
		String[] nameValues = ids.split(",");
		List parameters = new ArrayList();
		for(int i=0;i<nameValues.length;i++){
			parameters.add(nameValues[i].trim());	
		}
		List users =this.find(query, new String[]{"name"}, new Object[]{parameters});
		return users;
	}
	public int getAllUserCount() throws Exception{
		String hql = "select count(*) from User";
		return dao.find(hql).size();
	}

	public List getUserByRoleId(String roleId) throws Exception{
		String query = "select user from User as user where ? in elements(user.roleList)";
		List paras = new ArrayList();
		paras.add(roleId);
		return dao.find(query, paras);
	}

	public List getDeptByUserId(String userId) throws Exception{
		String query = "select dept from User user,Department  dept where user.deptId=dept.id and  user.id = ?";
		List paras = new ArrayList();
		paras.add(userId);
		return dao.find(query, paras);
	}

	public List getRoleByUserId(String userId) throws Exception{
		return ((User) dao.load(getEntityClass(), userId)).getRoleList();
	}

	public List getUserByNameId(String userId, String userName, int begin,
			int length) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("select user from User as user where 1=1 ");
		List paras = new ArrayList();
		if(StringUtils.isNotEmpty(userId)){
			sb.append(" and user.id = ?");
			paras.add(userId);
		}
		if(StringUtils.isNotEmpty(userName)){
			sb.append(" and user.name like ?");
			paras.add("%" + userName + "%");
		}
		return dao.find(sb.toString(), paras, begin, length);
	}

	public boolean isRepeat(User user) throws Exception{
		String query = "select user from User as user where user.loginName = ?";
		List paras = new ArrayList();
		paras.add(user.getLoginName());
		if(StringUtils.isNotEmpty(user.getId())){
			query += " and user.id != ?";
			paras.add(user.getId());
		}
		List list = dao.find(query, paras);
		if(list.size() > 0)
			return true;
		return false;
	}

	public Map getUserMapByRoleId(String id) throws Exception{
		Map userMap = new HashMap();
		List paras = new ArrayList();
		String query = "select user from User as user where ? in elements(user.roleList)";
		paras.add(id);
		List list = dao.find(query, paras);
		if(list.size() > 0){
			int size = list.size();
			for(int i = 0; i < size; i++){
				User u = (User) list.get(i);
				userMap.put(u.getId(), u.getName());
			}
		}
		return userMap;
	}

	public Map getUserMapByDeptId(String id) throws Exception{
		Map userMap = new HashMap();
		List paras = new ArrayList();
		String query = "select user from User as user where user.deptId = ?";
		paras.add(id);
		List list = dao.find(query, paras);
		if(list.size() > 0){
			int size = list.size();
			for(int i = 0; i < size; i++){
				User u = (User) list.get(i);
				userMap.put(u.getId(), u.getName());
			}
		}
		return userMap;
	}

	public List getAllUsersNotInDeptId(String deptId, Page page){ 
		List paras = new ArrayList();
		// String expression = "select user from User as user where
		// user.department.id = ? and user.loginName != 'root'";
		String expression = "select user from User user, Department  dept where user.deptId=dept.id and dept.id = ? "
				+ " and u.loginName != 'root'";
		paras.add(deptId);
		expression += " order by u.createTime ASC ";
		return find(expression, paras, page);
	}

	public List getAllUsersNotInDeptId(User user, Page page){
		List paras = new ArrayList();
		StringBuffer expression = new StringBuffer();
		expression.append("select u from User u ");
		if(user.getDepartment()!=null){
			paras.add(user.getDepartment().getId());
			expression.append(" where not exists(");
			expression.append(" select user.id from User  user ,Department  dept where  user.deptId=dept.id  and dept.id = ? and u.id=user.id");
			expression.append(" )");
			expression.append(" and u.loginName != 'root'");
		}
		if(!"".equalsIgnoreCase(user.getLoginName())){
			expression.append( " and u.loginName like ? ");
			paras.add(user.getLoginName()+"%");
		}
		if(!"".equalsIgnoreCase(user.getName())){
			expression.append( " and u.name like ? ");
			paras.add(user.getName()+"%");
		}

		expression.append( " order by u.createTime ASC ");
		return find(expression.toString(), paras, page);
	}

	public String getAllUsersNotInDeptIdJSON(User user, Page page)
			throws Exception{
		List usrs = new ArrayList();
		if(user.getDepartment() != null){
			usrs = this.getAllUsersNotInDeptId(user,
					page);
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(usrs, map);
	}
	
	public List getUserListByDeptIdAndRoleId(String deptId, String roleId){
		List paras = new ArrayList();
		String query = "select user from User as user left join user.roleList role where user.deptId = ? and role.id = ? and user.loginName != 'root'";
		paras.add(deptId);
		paras.add(roleId);
		return this.find(query, paras);
	}
	
	public List getUserListByBankcodeAndRoleId(String bankCode, String roleId){
		List userList = new ArrayList();
		Department dept = departmentService.getOrgCodeByBankNumber(bankCode);
		if(StringUtils.isNotBlank(bankCode) && StringUtils.isNotBlank(roleId)){
			if(dept != null){
				userList = getUserListByDeptIdAndRoleId(dept.getId(),roleId);
			}else{
				logger.error("----------大额支付行号为"+bankCode+"的机构为空！");
			}
		}else if(StringUtils.isNotBlank(bankCode)){
			if(dept != null){
				userList = getUsers(dept.getId());
			}else{
				logger.error("----------大额支付行号为"+bankCode+"的机构为空！");
			}
		}else if(StringUtils.isNotBlank(roleId)){
			try{
				userList = getUserByRoleId(roleId);
			}catch (Exception e){
				logger.error(e,e);
			}
		}
		return userList;
	}
	
	/**
	 *通过机构+角色+当前审批节点级别   查询所有符合条件的 用户信息  
	 *@param brachId 当前机构
	 * @param roleIds 角色ID列表  
	 * @param nodeLevel 审核岗位级别 0-本机构、1-本级+上级、2-本级+上级+上级
	 *@param strSign(角色id分割符标识如','或'@'等)
	 *@return List 用户信息list
	 */
	public List getAuditUsersOfNextRoute(String brachId,String roleIds,String nodeLevel,String strSign, User startUser) throws Exception{
		if("1".equals(nodeLevel)){//本级+上级
			Department deptDto = departmentService.getDeptById(brachId);
			if(deptDto.getParent()!=null && StringUtils.isNotEmpty(deptDto.getParent().getId())){
				brachId = deptDto.getId()+","+deptDto.getParent().getId();	
			}
		}else if("2".equals(nodeLevel)){//本级+上级+上级
			//当前机构 
			Department deptDtoCur = departmentService.getDeptById(brachId);
			if(deptDtoCur.getParent()!=null && StringUtils.isNotEmpty(deptDtoCur.getParent().getId())){
				//上级
				brachId = deptDtoCur.getId()+","+deptDtoCur.getParent().getId();	
				//当前机构的上级机构 
				Department deptDtoParent = departmentService.getDeptById(deptDtoCur.getParent().getId());
				if(deptDtoParent.getParent()!=null&& StringUtils.isNotEmpty(deptDtoParent.getParent().getId())){
					//当前机构的 父机构  的 父机构ID
					brachId+=","+deptDtoParent.getParent().getId();
				}
			}
		}
		String query = "select user from User as user  left join user.roleList role where user.deptId in(:brachId) and role.id in(:roleIds)";
//		String query ="select user from User as user,Department as dept,user.roleList as roles,Role as role  where user.deptId=dept.id and roles.userId=user.id and roles.roleId=role.id and dept.id =:brachId and role.id in(:roleIds)";
		List parasName = new ArrayList();
		List parasValue = new ArrayList();
		parasName.add("brachId");
		parasValue.add(Arrays.asList(brachId.split(",")));//下一岗审核人员所在机构
		parasName.add("roleIds");
		parasValue.add(Arrays.asList(roleIds.split(strSign)));//审核人角色
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(query, nameForSetVar, parameters);
		return list;
	}

	
	/**
	 *  获取操作员操作机器 IP 地址
	 */
	public  String getUserIp(HttpServletRequest request) {
       String ip = request.getHeader("x-forwarded-for");
       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
           ip = request.getHeader("Proxy-Client-IP");
       }
       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
           ip = request.getHeader("WL-Proxy-Client-IP");
       }
       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
           ip = request.getRemoteAddr();
       }
       return ip;
   }	

	public User validateUserFromCoreSys(User user, boolean checkFinger)throws Exception{
		if(user==null)return null;
//		if(!StringUtil.isStringEmpty(user.getPassword())){// 将用户输入的密码加密处理
//			user.setPassword(SecurityEncode.EncoderByMd5(user.getPassword()));
//		}
		String query = "select u from User u ,Department dept where u.deptId=dept.id and u.loginName = ? and dept.innerBankCode = ?";
		List parameters = new ArrayList();
		parameters.add(user.getLoginName());
		parameters.add(user.getInnerBankCode());
		List users = this.find(query, parameters);
		if(CollectionUtil.isNotEmpty(users)){
			//修改了通过机构号校验登录用户
			User u = (User) users.get(0);
			Department dept = getDeptByInnerBankCode(user.getInnerBankCode());
//			dept.setAgcySvcrList(msgCommonService.loadOrgnlPtcptAcctList(dept.getBankNumber()));
			u.setDepartment(dept);
			
			//去核心校验密码
			return u;
		}else{
			throw new Exception("票据系统不存在此用户："+user.getLoginName());
			
		}
	}

	/**
	 * @描述：分页获取当前机构下所有代理签约用户JSON串
	 * @param user 当前登录用户
	 * @param page 分页对象
	 * @return 代理签约用户列表
	 * @throws Exception
	 */
	public String proSingUserManagementJSON(User user, Page page) throws Exception{
		List paras = new ArrayList();
		
		String SingUserHql = "select user from User  user,Department  dept where user.deptId=dept.id  and user.loginName != 'root' and user.agentFlag ='1' ";
		
		if(null != user){
			if(null != user.getLoginName() && user.getLoginName().trim().length() != 0){
				SingUserHql += " and user.loginName =?";
				paras.add(user.getLoginName());
			}	
			if(null != user.getName() && user.getName().trim().length() != 0){
				SingUserHql += " and user.name like ?";
				paras.add("%"+user.getName()+"%");
			}
			if(null != user.getDepartment().getId() && user.getDepartment().getId().trim().length() !=0){
				SingUserHql += "and dept.id =?";
				paras.add(user.getDepartment().getId());
			}
		}	
		SingUserHql += " order by user.createTime ASC";
		
		List singUserList = find(SingUserHql, paras, page);
		
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		
		return JsonUtil.fromCollections(singUserList, map);
	}
	
	public SystemConfig getSystemConfig(){
		return systemConfig;
	}
	public void setSystemConfig(SystemConfig systemConfig){
		this.systemConfig = systemConfig;
	}
	public DepartmentService getDepartmentService(){
		return departmentService;
	}
	public void setDepartmentService(DepartmentService departmentService){
		this.departmentService = departmentService;
	}

	public boolean isExecutive(User user)throws Exception{
		String query = "select user from User as user where user.custExecutiveFlag = ?";
		List paras = new ArrayList();
		paras.add(user.getCustExecutiveFlag());
		if("1".equals(user.getCustExecutiveFlag())){
			query += " and user.department = ? ";
			paras.add(user.getDepartment().getId());
		}
		List list = dao.find(query, paras);
		if(list.size() > 0)
			return true;
		return false;
		
	}
	

	public User validateBusManager(String loginName, String password,String bankNumber) throws Exception {
		String hql = "select u from User u,Department dept" +
				" where u.deptId=dept.id and u.loginName = ? and u.password = ? and dept.bankNumber = ? and u.custExecutiveFlag = ?";
		List paras = new ArrayList();
		paras.add(loginName);
		paras.add(password);
		paras.add(bankNumber);
		paras.add("1");
		List list = this.find(hql, paras);
		if(list.size() > 0){
			return (User) list.get(0);
		}
		return null;
		
	}
	
	
	public VerifyResult txChangePassword(User user,String id) throws Exception{
		VerifyResult result = new VerifyResult();
		result.setResult(true);
		if (validateUser(user) == null) {
			result.setResult(false);
			result.setDesc("用户原密码验证失败");
		} else {
			String regex = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.PASSWORD_RESULT);
//			if(StringUtils.isNotBlank(regex)){
//				boolean matches = user.getNewPassword().matches(regex);
//				if(!matches){
//					result.setResult(false);
//					result.setDesc(SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.PASSWORD_RESULT_DESC));
//				}
//			}				
			if(user.getNewPassword().equals(user.getLoginName())){
				result.setResult(false);
				result.setDesc("密码不允许包含用户名");
			}
			
			if(result.getResult()){
				User userTemp = getUserById(id);
				userTemp.setPassword(SecurityEncode.EncoderByMd5(user.getNewPassword()));
				userTemp.setPsswdUpdate(new Date());
				txStore(userTemp);
				result.setDesc("修改成功");
			}
		}
		return result;
	}

	/**
	 * 新增/修改是检验数据库是否存在同名用户
	 * @param editUser 待修改用户
	 * @param loginUser 当前登录用户
	 * @return user 已存在用户
	 * @throws Exception
	 */
	public User checkUserExistence(User editUser,User loginUser) throws Exception {
		StringBuffer hql = new StringBuffer("select u from User as u,Department dept where u.deptId=dept.id and  u.loginName ='"+editUser.getLoginName()+"' ");
		hql.append(" and dept.pjsMemberCode='").append(loginUser.getDepartment().getPjsMemberCode()).append("'");
		if(StringUtil.isNotBlank(editUser.getId())){
			hql.append(" and u.id !='"+editUser.getId()+"'");
		}
		List u = this.find(hql.toString());
		return u.size() > 0 ? (User)u.get(0):null;
	}

	@Override
	public List findUserExpt(List res, Page page) throws Exception {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[15];
				User user = (User) res.get(i);
				s[0] = user.getLoginName();
				s[1] = user.getName();
				s[2] = user.getStatusStr();
				s[3] = user.getPjsUserName();
				s[4] = user.getRoleStr();
				if (user.getCreateTime() != null) {
					s[5] = String.valueOf(user.getCreateTime()).substring(0, 10);
				}else{
					s[5] = "";
				}
				list.add(s);
			}
		}
		return list;
	}
}
