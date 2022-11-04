package com.mingtech.application.sysmanage.service;

import java.util.List;

import org.json.JSONException;

import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface RoleService extends GenericService {


	/**
	* 方法说明: 分页获取所有角色JSON串(不包括匿名角色、缺省角色和root角色)
	* @param  role 角色实体
	* @param  page 分页实体
	*
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午09:12:47
	*/
	public String getRolesJSON(Role role, Page page) throws Exception;


	public String getRolesJSON() throws Exception;

	/**
	 * 根据ID返回角色的JSON串
	 *
	 * @param
	 * @since Nov 24, 2008
	 * @author zhaoqian
	 * @param id
	 * @return
	 */
	public String getRole(String id);

	/**
	 * 返回角色树
	 *
	 * @param
	 * @since Dec 4, 2008
	 * @author zhaoqian
	 * @param tree
	 * @param level 分级 1总行，2分行，3支行
	 * @return
	 * @throws JSONException
	 */
	public String getRoles(Tree tree,int level) throws Exception;

	/**
	* 方法说明: 查询所有角色
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-17 上午09:56:32
	*/
	public List getAllRole() throws Exception;

	/**
	* 方法说明: 通过角色ID查询角色对象
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-17 上午10:29:55
	*/
	public Role getRoleById(String roleId) throws Exception;

	/**
	* 方法说明: 判断角色对象是否有关联的用户
	* @param  角色ID
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午10:54:51
	*/
	public boolean isRoleHasUser(String roleId) throws Exception;

	/**
	* 方法说明: 判断当前角色是否有重复
	* @param role 要判断的角色对象
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午11:11:55
	*/
	public boolean isRepeatRole(Role role) throws Exception;

	/**
	* 方法说明: 以数组的形式获取角色列表的ID
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-20 下午05:57:19
	*/
	public String[] getRoleListId(List roleList) throws Exception;

	/**
	* 方法说明: 按角色使用范围获取除anonymous和default之外的角色记录
	* @param scope 角色使用范围
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-20 下午05:59:22
	*/
	public List getCommonRoles(String scope) throws Exception;

	public Role getRoleByName(String name) throws Exception;

	public List getRoles(String scope) throws Exception;
	/**
	 * <p>描述:为选中的角色分配资源信息</p>
	 * @param roleIds 角色id集合(多个id逗号分隔)
	 * @param resourceIds 资源id集合(多个id逗号分隔)
	 */
	public void txAssigRoleResource(String roleIds, String resourceIds) throws Exception;
	/**
	 * 方法说明: 获取该会员下所有角色信息
	 * @param  role 角色实体
	 * @param  page 分页实体
	 * @param  memberCode 会员编码
	 * @author  zjy
	 * @return list 角色信息
	 * @date 20190529
	 */
	public List getRoleJsonByMemberCode(Role role,Page page,String memberCode) throws Exception;
	
	/**
	* 方法说明: 根据会员编码和机构ID获取该机构可使用的角色信息
	* @param memberCode 会员编码
	* @param deptId 机构ID
	* @return
	*/ 
	public List getRolesByMemberCodeAndDeptId(String memberCode,String deptId) throws Exception;
	
	/**
	 * 查询用户的角色有哪些
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List getRoleByBean(User user) throws Exception;
	
	
	/**
	 * 根据用户查询该用户所属角色与机构,用于菜单权限查询
	 * @param user
	 * @return
	 * @throws Exception
	 * @author Wu
	 * @date 2021-10-18
	 */
	public String queryRoleDeptByUser(User user) throws Exception;
	
}
