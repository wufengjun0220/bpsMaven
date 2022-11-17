package com.mingtech.application.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.application.sysmanage.vo.TreeNode;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.CurValues;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends GenericServiceImpl implements RoleService {

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	protected GenericHibernateDao sessionDao;
	public Class getEntityClass() {
		return Role.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(Role.class);
	}

	/**
	 * 分页返回所有角色
	 * @param roleManager
	 * @return
	 * @throws Exception
	 */
	public List getRoles(Role role, Page page) throws Exception {
		List paras = new ArrayList();
		paras.add(CurValues.ANONYMOUS);
		paras.add(CurValues.DEFAULT);
		paras.add(CurValues.ROOT);
		String expression = " select role from Role as role where role.name != ? and role.name != ? and role.name != ?";
		if (StringUtil.isNotBlank(role.getName())) {
			expression = expression + "  and role.name like ?";
			paras.add("%" + role.getName() + "%");
		}
		if (StringUtil.isNotBlank(role.getCode())) {
			expression = expression + "  and role.code = ?";
			paras.add(role.getCode());
		}
		expression += " order by role.createTime DESC";
		return find(expression, paras, page);
	}
	/**
	 * 返回所有角色
	 * @return
	 * @throws Exception
	 */
	public List getRoles()	throws Exception{
		List paras = new ArrayList();
		paras.add(CurValues.ANONYMOUS);
		paras.add(CurValues.DEFAULT);
		paras.add(CurValues.ROOT);
		String expression = " select role from Role as role where role.name != ? and role.name != ? and role.name != ?";
		expression += " order by role.createTime DESC";
		return find(expression, paras);
	}

	public String getRolesJSON(Role role, Page page) throws Exception {
		List roles = this.getRoles(role,page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(roles, map);
	}

	public String getRolesJSON() throws Exception {
		List roles=getRoles();
		Map map = new HashMap();
		map.put("totalProperty", "results," + roles.size());
		map.put("root", "rows");
		for(int i=0;i<roles.size();i++){
			Role role=(Role)roles.get(i);
			role.setResourceList(null);
		}
		return JsonUtil.fromCollections(roles,map);
	}

	public Role getRoleByName(String name) throws Exception {
		String sql = "select role from Role as role where role.name = ?";
		Role r = null;
		List paras = new ArrayList();
		paras.add(name);
		List roleList = this.find(sql, paras);
		if (roleList.size() > 0) {
			r = (Role) roleList.get(0);
		}
		return r;
	}

	public String getRole(String id) {
		Role role = (Role) this.load(id);
		// 转换为JSON串
		return JsonUtil.fromObject(role);
	}

	public String getRoles(Tree tree ,int level) throws Exception {
		List children = null;
		if (StringUtil.equals(tree.getId(), "-2")) {// 取根节点
			tree.setId("-1");
			tree.setText("角色树");
			return "["+JsonUtil.fromObject(tree)+"]";
		}
		if (StringUtil.equals(tree.getId(), "-1")) {// 取根节点
			children = findAll();
		}
		StringBuffer sb = new StringBuffer();
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		} else {
			List list = new ArrayList();
			for (int i = 0; i < children.size(); i++) {
				Role child = (Role) children.get(i);
				// YeCheng 调整适用范围的逻辑调整
				if (StringUtil.isNotBlank(child.getScope()) && 
						(child.getScope().indexOf(level + "") >= 0 
						|| child.getScope().indexOf((level + 1) + "") >= 0
						|| child.getScope().indexOf((level + 2) + "") >= 0
						|| child.getScope().indexOf((level + 3) + "") >= 0
						|| child.getScope().indexOf((level + 4) + "") >= 0
						|| child.getScope().indexOf((level + 5) + "") >= 0
						|| child.getScope().indexOf((level + 6) + "") >= 0
						|| child.getScope().indexOf((level + 7) + "") >= 0)) {
					TreeNode tmp = new TreeNode();
					tmp.setText(child.getName());
					tmp.setId(child.getId());
					tmp.setLeaf(true);
					list.add(tmp);
				}
			}
			sb.append(JsonUtil.fromCollections(list));
		}
		return sb.toString();
	}

	public List getAllRole() throws Exception {
		return dao.loadAll(getEntityClass());
	}

	public Role getRoleById(String roleId) throws Exception {
		return (Role) dao.load(getEntityClass(), roleId);
	}

	public boolean isRoleHasUser(String roleId) throws Exception {
		String query = "select count(*) from User as user where ? in elements(user.roleList)";
		List paras = new ArrayList();
		paras.add(roleId);
		long size = dao.getRowCount(query, paras).longValue();
		if(size > 0){
			return true;
		}
		return false;
	}

	public boolean isRepeatRole(Role role) throws Exception {
		String query = "select role from Role as role where role.name = ?";
		List paras = new ArrayList();
		paras.add(role.getName());
		if(StringUtils.isNotBlank(role.getId())){
			query += " and role.id != ?";
			paras.add(role.getId());
		}
		List list = dao.find(query, paras);
		if (list.size() > 0)
			return true;
		return false;
	}

	public String[] getRoleListId(List roleList) throws Exception {
		if(roleList.size() > 0){
			String[] ids = new String[roleList.size()];
			for(int i = 0;i < roleList.size();i++){
				ids[i] = ((Role) roleList.get(i)).getId();
			}
			return ids;
		}
		return null;
	}

	public List getCommonRoles(String scope) throws Exception {
		List paras = new ArrayList();
		String query = "select role from Role as role where role.name != ? and role.name != ? and role.name != ? " ;
		paras.add(CurValues.ANONYMOUS);
		paras.add(CurValues.DEFAULT);
		paras.add(CurValues.ROOT);
		if(StringUtils.isNotBlank(scope)){
			query += " and role.scope >= ?";
			paras.add(scope);
		}
		return dao.find(query, paras);
	}

	public List getRoles(String scope) throws Exception {
		List paras = new ArrayList();
		List result=new ArrayList();
		String query = "select role from Role as role where role.name != ? and role.name != ? and role.name != ? " ;
		paras.add(CurValues.ANONYMOUS);
		paras.add(CurValues.DEFAULT);
		paras.add(CurValues.ROOT);
		List list=dao.find(query, paras);
		//遍历查询结果
		Iterator it=list.iterator();
		while(it.hasNext()){
			Role role=(Role)it.next();
			String tmpStr=role.getScope();
			String[] scopes=tmpStr!=null?role.getScope().split(","):new String[]{};//3,2,1
			for(int i=0;i<scopes.length;i++){
				if(new Integer(scopes[i]).intValue()==
					new Integer(scope).intValue()){
					result.add(role);
					break;
				}
			}
		}
		return result;
	}
	/**
	 * <p>描述:为选中的角色分配资源信息</p>
	 * @param roleIds 角色id集合(多个id逗号分隔)
	 * @param resourceIds 资源id集合(多个id逗号分隔)
	 */
	public void txAssigRoleResource(String roleIds, String resourceIds) throws Exception{
		//如果资源id为空，则取消该角色已分配的所有资源信息
		String[] arrRoleId = roleIds.split(",");
		String[] arrResourceIds=resourceIds.split(",");
		
		List resourceList = null;
		//根据id查询资源信息
		if(StringUtils.isNotBlank(resourceIds)){
			StringBuffer sb = new StringBuffer();
			sb.append(" from Resource as dto where dto.id in (:ids)  ");
			
			List keyList = new ArrayList(); // 要查询的字段列表
			keyList.add("ids");
			List valueList = new ArrayList(); // 要查询的值列表
			List idList = Arrays.asList(resourceIds.split(",")); //id集合
			valueList.add(idList);
			String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
			resourceList = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		for(String roleId : arrRoleId){
			Role role = this.getRoleById(roleId);
			role.setResourceList(resourceList);
			this.txStore(role);
		}
	}
	/**
	 * 方法说明: 获取该会员下所有角色信息
	 * @param  role 角色实体
	 * @param  page 分页实体
	 * @param  memberCode 会员编码
	 * @author  zjy
	 * @return list
	 * @date 20190529
	 */
	public List<Role> getRoleJsonByMemberCode(Role role, Page page, String memberCode)
			throws Exception {
		List paras = new ArrayList();
		paras.add(CurValues.ANONYMOUS);
		paras.add(CurValues.DEFAULT);
		paras.add(CurValues.ROOT);
		String expression = " select role from Role as role where role.name != ? and role.name != ? and role.name != ?";
		if(StringUtils.isNotBlank(memberCode)){
			expression+=" and role.memberCode = '"+memberCode+"'";
		}
		if(role != null){
			if (StringUtils.isNotBlank(role.getName())) {
				expression += " and role.name like ?";
				paras.add("%" + role.getName() + "%");
			}
		}

		expression += " order by role.createTime DESC";
		return find(expression, paras, page);
	}
	
	
	/**
	* 方法说明: 根据会员编码和机构ID获取该机构可使用的角色信息
	* @param memberCode 会员编码
	* @param deptId 机构ID
	* @return
	*/ 
	public List getRolesByMemberCodeAndDeptId(String memberCode,String deptId) throws Exception{
		StringBuffer sql = new StringBuffer("  from Role as role where ");
		sql.append("  role.memberCode = '").append(memberCode).append("'");
		if (StringUtil.isNotBlank(deptId)) {
			Department deptObj = departmentService.getDeptById(deptId);
//			if(deptObj != null){
//				sql.append(" and role.scope like '%").append(String.valueOf(deptObj.getLevel())).append("%'");
//			}
		}
		sql.append(" order by role.createTime");
		List roles = dao.find(sql.toString());
		return roles;
	}

	@Override
	public List getRoleByBean(User user) throws Exception {
		StringBuffer sql = new StringBuffer(" select  r.r_code from T_ROLE r, t_user u ,T_USER_ROLE ur where " +
				"u.id=ur.userid and r.id=ur.roleid and u.id = '"+user.getId()+"'  ");
//		sql.append("  role.memberCode = '").append(memberCode).append("'");
//		if (StringUtil.isNotBlank(deptId)) {
//			Department deptObj = departmentService.getDeptById(deptId);
//			if(deptObj != null){
//				sql.append(" and role.scope like '%").append(String.valueOf(deptObj.getLevel())).append("%'");
//			}
//		}
		List roles = dao.SQLQuery(sql.toString());
		return roles;
	}

	@Override
	public String queryRoleDeptByUser(User user) throws Exception {
		
		/**
		 * 1、查询用户角色是否有总行管理员或支行管理员 
		 * 
		 * 
		 * 
		 * 2、先查询该用户所属机构
		 * 		总行：角色为查询员、管理员、审批员  可查询所有数据
		 * 		分行：角色为查询员、管理员、审批员、行长 可查询分支行下所有数据
		 * 		客户经理: 只可查询票据池协议为业务员为自己的数据
		 * 
		 * 返回:	0：总行管理员； 1：支行管理员； 2：总行管理、审批、查询员； 3：分行管理、审批、查询员； 4：客户经理； 5：授权结算柜员； 6：； 7：； 
		 * 
		 */
		String rsu;
		if(user != null){
			List roles = this.getRoleByBean(user);
			for (Object object : roles) {
				String code = (String) object;
				if(code.equals("A00001")){//超级管理员
					return rsu = "0";
				}
//				if(code.equals("A00004")){//支行管理员 
//					return rsu = "1";
//				}
				
			}
			
			Department dept = user.getDepartment();
			
			if(dept.getLevel() == 1){//总行
				for (Object object : roles) {
					String code = (String) object;
					if(code.equals("A00004") || code.equals("A00002") || code.equals("A00003")){//查询员、管理员、审批员
						return rsu = "2";
					}
				}
			}else if(dept.getLevel() == 3 || dept.getLevel() == 5){//分行
				for (Object object : roles) {
					String code = (String) object;
					if(code.equals("A00004") || code.equals("A00002") || code.equals("A00003") || code.equals("B00001")){//查询员、管理员、审批员、行长
						return rsu = "3";
					}
				}
			}
			
			
			for (Object object : roles) {
				String code = (String) object;
				if(code.equals("C00001")){//客户经理
					return rsu = "4";
				}
			}
			for (Object object : roles) {
				String code = (String) object;
				if(code.equals("D00001") || code.equals("D00002")){
					return rsu = "5";
				}
			}
		}
		
		return null;
	}
}
