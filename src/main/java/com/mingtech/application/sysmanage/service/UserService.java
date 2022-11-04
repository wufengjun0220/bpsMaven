/**
 * User Service
 */
package com.mingtech.application.sysmanage.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.dto.VerifyResult;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author huboA
 */
public interface UserService extends GenericService{
	
	/**
	 * @描述：分页获取当前机构下所有代理签约用户JSON串
	 * @param user 当前登录用户
	 * @param page 分页对象
	 * @return 代理签约用户列表
	 * @throws Exception
	 */
	public String proSingUserManagementJSON(User user, Page page) throws Exception;
	
	/**
	 * 查询  用户信息  通过登录名
	 * @param loginName
	 * @return 符合条件的用户列表 User
	 * @throws Exception
	 */
	public List getUsersByLoginNames(String loginName) throws Exception ;
	/**
	 * 通过IDS 查询 柜员信息
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List getUsersByIds(String ids) throws Exception ;
	/**
	 * 方法说明: 验证用户是否在数据库中
	 * @param user 要验证的用户对象
	 * @author E-mail: pengdaochang@
	 * @return
	 * @throws Exception 
	 * @date 2009-3-10 下午01:55:39
	 */
	public User validateUser(User user) throws Exception;
	
	/**
	 * 方法说明: 验证用户是否在数据库中
	 * @param user 要验证的用户对象
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-10 下午01:55:39
	 */
	public User validateUserNoIneerBankCode(User user);

	/**
	 * 分页获取当前机构下的用户JSON串
	 * @param
	 * @since Dec 1, 2008
	 * @author zhaoqian
	 * @param userManager
	 * @return
	 */
	public List getUsersJSON(User user, Page page) throws Exception;
	
	public String getUsersJSONNew(User user, Page page) throws Exception;

	/**
	 * 分页获取当前机构下的用户
	 * @since Dec 1, 2008
	 * @author zhaoqian
	 * @param userManager
	 * @param pageInfo 分页信息
	 * @return
	 */
	public List getUsers(User user, Page pageInfo);

	/**
	 * 获取当前机构下的所有用户
	 * @param
	 * @since Dec 1, 2008
	 * @author zhaoqian
	 * @param deptId
	 * @return
	 */
	public List getUsers(String deptId);

	/**
	 * 方法说明: 判断给定部门中是否包含用户
	 * @param departmentId 给定部门ID
	 * @param isActive true 查找正常状态的用户 false 查找所有状态
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-16 下午04:31:42
	 */
	public boolean isDeptHasUser(String departmentId, boolean isActive)
			throws Exception;

	/**
	 * 方法说明: 查询所有用户
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-17 上午10:00:02
	 */
	public List getAllUser() throws Exception;

	/**
	 * 方法说明: 通过用户ID查询用户对象
	 * @param userId 用户ID
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-17 上午10:06:43
	 */
	public User getUserById(String userId) throws Exception;

	/**
	 * 方法说明: 查询所有用户数
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-17 上午10:09:43
	 */
	public int getAllUserCount() throws Exception;

	/**
	 * 方法说明: 通过角色ID查询用户
	 * @param roleId 角色ID
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-17 上午10:16:51
	 */
	public List getUserByRoleId(String roleId) throws Exception;

	/**
	 * 方法说明: 查询用户所属部门列表
	 * @param userId 用户ID
	 * @author E-mail: pengdaochang@
	 * @return list
	 * @date 2009-3-17 上午10:34:10
	 */
	public List getDeptByUserId(String userId) throws Exception;

	/**
	 * 方法说明: 查询用户所用户的角色列表
	 * @param userId 用户ID
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-17 上午10:44:30
	 */
	public List getRoleByUserId(String userId) throws Exception;

	/**
	 * 方法说明: 通过用户ID和用户名查询用户列表
	 * @param userId 用户ID
	 * @param userName 用户名
	 * @param begin 起始查询
	 * @param length 查询数
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-17 上午10:51:52
	 */
	public List getUserByNameId(String userId, String userName, int begin,
			int length) throws Exception;

	/**
	 * 方法说明: 判断当前用户是否有重复
	 * @param user 要判断的用户对象
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-18 下午04:37:57
	 */
	public boolean isRepeat(User user) throws Exception;

	/**
	 * 方法说明: 通过角色ID查询属于该角色的用户信息Map(userId,userName)
	 * @param name 角色ID
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-5-8 上午09:37:38
	 */
	public Map getUserMapByRoleId(String id) throws Exception;

	/**
	 * 方法说明: 通过角色ID查询属于该部门的用户信息Map(userId,userName)
	 * @param id 部门或机构ID
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-5-8 上午10:02:25
	 */
	public Map getUserMapByDeptId(String id) throws Exception;

	/**
	 *
	* <p>方法名称: getAllUsersNotInDeptIdJSON|描述:分页获取不是当前机构下的所有用户JSON串 </p>
	* @param user
	* @param page
	* @return
	* @throws Exception
	 */
	public String getAllUsersNotInDeptIdJSON(User user,Page page) throws Exception;
	
	/**
	* <p>方法名称: getUserListByDeptIdAndRoleId|描述: 查询直属该机构ID下并且属于该角色ID的用户列表</p>
	* @param deptId 机构ID
	* @param roleId 角色ID
	* @return
	*/
	public List getUserListByDeptIdAndRoleId(String deptId,String roleId);
	

	/**
	* <p>方法名称: getUserListByBankcode|描述: 通过大额支付行号和角色ID查询用户列表,行号和角色ID可以只填其一或者二者都填</p>
	* @param bankCode 大额支付行号
	* @param roleId 角色ID
	* @return
	*/
	public List getUserListByBankcodeAndRoleId(String bankCode,String roleId);
	
	/**
	 *通过机构+角色+当前审批节点级别   查询所有符合条件的 用户信息  
	 *@param brachId 当前机构
	 * @param roleIds 角色ID列表  
	 * @param nodeLevel 审核岗位级别 0-本机构、1-本级+上级、2-本级+上级+上级
	 *@param strSign(角色id分割符标识如','或'@'等)
	 *@return List 用户信息list
	 */
	public List getAuditUsersOfNextRoute(String brachId,String roleIds,String nodeLevel,String strSign, User startUser) throws Exception;

//	/**
//	 * <p>方法名称: getOperateDate|描述:获取营业日期 </p>
//	 * @return
//	 */
//	public String getOperateDate();
	
	//河北银行
	public User validateUserFromCoreSys(User user, boolean checkFinger) throws Exception;
	
	/**
	 * 方法说明: 通过用户登录名查询用户对象
	 * @param loginName 用户登录名
	 * @author 
	 * @return
	 * @date 2012-7-27 上午10:06:43
	 */
	public User getUserByLoginName(String loginName) throws Exception;
	//添加IP地址
	public String getUserIp(HttpServletRequest request);
	
	/**
	 * 检验业务主管是否存在
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean isExecutive(User user) throws Exception;
	
	/**
	 * 检验是否是本行业务主管
	 * @param loginName 业务主管登录名
	 * @param password 业务主管登录密码
	 * @param bankNumber 当前登录行行号
	 * @return
	 * @throws Exception
	 */
	public User validateBusManager(String loginName, String password, String bankNumber) throws Exception;
	
	
	/**
	 * 修改密码
	 * @param user 用户信息
	 * @param id 操作用户id
	 * @return {@link VerifyResult}
	 * @throws Exception
	 */
	VerifyResult txChangePassword(User user,String id) throws Exception;

	/**
	 * 新增/修改是检验数据库是否存在同名用户
	 * @param editUser 待修改用户
	 * @param loginUser 当前登录用户
	 * @return user 已存在用户
	 * @throws Exception
	 */
	public User checkUserExistence(User editUser,User loginUser) throws Exception;
	
	/**
	 * 批量导出 用户管理查询 
	 * @param list
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List findUserExpt(List list, Page page) throws Exception;
	
}
