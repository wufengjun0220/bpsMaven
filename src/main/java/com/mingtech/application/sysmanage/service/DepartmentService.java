package com.mingtech.application.sysmanage.service;

import java.util.List;

import org.json.JSONException;

import com.mingtech.application.pool.bank.message.ReturnMessage;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface DepartmentService extends GenericService {
	
	/**
	* 方法说明: 分页获取所有机构JSON串
	* @param  role 机构实体
	* @param  page 分页实体
	*
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午09:12:47
	*/
	public String getDeptsJSON(Department dept, Page page,User user) throws Exception;
	public String getDeptsJSON1(Department dept, Page page,User user) throws Exception;
	/**
	 * 根据机构层级编码 查询所有的 本机构及子机构
	 * @param levCode 层级编码
	 * @return
	 */
	public List getAllSubDepartmentByLevelCode(String levCode);
	/**
	 * 根据ID返回部门的JSON串
	 * @param
	 * @since  Nov 24, 2008
	 * @author zhaoqian
	 * @param id
	 * @return
	 */
//	public String getDepartment(String id);
	/**
	 * 获取孩子节点的JSON串
	 * @param
	 * @since  Nov 26, 2008
	 * @author zhaoqian
	 * @param treeManager
	 * @return
	 * @throws JSONException
	 */
	public String getDepartments(Tree tree)throws JSONException;
	/**
	 * 根据父亲节点获取所有孩子
	 * @param
	 * @since  Nov 26, 2008
	 * @author zhaoqian
	 * @param pid 为空 代表取根节点
	 * @param status 不使用 设置成-1
	 * @return
	 */
	public List getAllChildren(String pid,int status);
	/**
	 * 根据父亲节点获取所有子节点ID
	 * @param
	 * @since  Nov 26, 2008
	 * @author zhaoqian
	 * @param pid 为空 代表取根节点
	 * @param status 不使用 设置成-1
	 * @return
	 */
	public List getAllChildrenIdList(String pid,int status);

	/**
	 * 根据父亲节点获取所有子节点大额行号
	 * @param
	 * @since  Nov 26, 2008
	 * @author qiyong
	 * @param pid 为空 代表取根节点
	 * @param status 不使用 设置成-1
	 * @return
	 */
	public List getAllChildrenBankCodeList(String pid, int status);

	/**
	 * 判断当前部门下是否有子节点
	 * @param departmentId 当前部门或单位ID
	 * @param type 包含的类型 0 子部门 1子单位 -1 所有
	 * @return true 有 false 没有
	 */
	public boolean hasChildren(String departmentId , int type);
	
	/**
	 * 
	* <p>方法名称: hasChildren|描述:判断当前部门是否有子节点 </p>
	* @param departmentId 当前部门或单位ID
	* @param type 包含的类型 0 子部门 1子单位 -1 所有
	* @param status 状态 0 停用 1 启用 -1 所有
	* @return true 有 false 没有
	 */
	public boolean hasChildren(String departmentId , int type, int status);

	/**
	* 方法说明:
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-16 上午11:18:47
	*/
	public String getDeptXml(String pid, int status) throws Exception;

	/**
	* 方法说明: 查询所有部门
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-17 上午09:41:31
	*/
	public List getAllDept() throws Exception;
	/**
	 * 查询所有无重复的部门
	 * @author Ju Nana
	 * @return
	 * @throws Exception
	 * @date 2019-9-7上午11:26:30
	 */
	public List<Department> getAllDeptNoDif() throws Exception;

	/**
	* 方法说明: 通过部门ID查询部门对象
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-17 上午10:33:27
	*/
	public Department getDeptById(String deptId) throws Exception;
	/**
	 * 根据大额行号 查询 机构信息
	 * @param netPointCode
	 * @return
	 * @throws Exception
	 */
	public Department getDepartmentByBankNo(String bankNo)throws Exception;
	/**
	 * 根据机构的  机构号查询机构信息
	 * @param netPointCode
	 * @return
	 * @throws Exception
	 */
	public Department getDepartmentByInnerBankCode(String netPointCode) throws Exception;
	/**
	 * 根据大额12位行号 查询 机构信息
	 * @param bankNumber 行号
	 * @return
	 * @throws Exception
	 */
	public Department getDepartmentByInnerBankNumber(String bankNumber) throws Exception;
	/**
	 * 根据机构id递归加载机构及子机构json串
	 * @param id 机构id
	 * @return
	 * @author huangshiqiang
	 * @date Mar 18, 2009
	 * @comment
	 */
	public String getDepartments(String deptId) throws Exception;

	/**
	 * 查询得到所有机构的列表 Msg065专用
	 * by chenyuefeng
	 */
	public List getAllDepartmentsList(int status, int isOrg) throws Exception;
	/**
	 * 根据大额支付行号查找机构
	* <p>方法名称: getOrgCodeByBankNumber|描述: </p>
	* @param bankNumber
	* @return
	* @throws Exception
	 */
	public Department getOrgCodeByBankNumber(String bankNumber);
	/**
	 * 根据大额支付行号查找机构
	* <p>方法名称: getOrgCodeByBankNumber|描述: </p>
	* @param bankNumber
	* @return
	* @throws Exception
	 */
	public void  txmodifyOrgIds(String id,String p_id)throws Exception;

	/**
	* <p>方法名称: getAllChildrenBankCodeList|描述:根据大额行号递归查找所有下级机构的大额行号 包括本行大额行号 </p>
	* @param numberList 存放大额行号的list
	* @param bankNumber 当前大额行号
	* @param status 机构状态
	*/
	public void getAllChildrenBankCodeList(List numberList,String bankNumber, int status);
	
	/**
	* <p>方法名称: getAllChildrenInnerCodeList|描述:根据内部机构号递归查找所有下级机构的机构号 包括机构号 </p>
	* @param resultList 存放机构号的list
	* @param innerCode 当前内部机构号
	* @param status 机构状态
	*/
	public List getAllChildrenInnerCodeList(String innerCode, int status);

	/**
	* <p>方法名称: queryByInnerBankCode|描述:根据机构网点号查询机构对象 </p>
	* @param innerBankCode  机构网点号
	* @return
	*/
	public Department queryByInnerBankCode(String innerBankCode);

	/**
	* <p>方法名称: verifyAddDeptIsRight|描述:增加部门校验输入信息是否合法 </p>
	* @param Department 部门对象
	* @return 是否校验通过 "right"通过
	*/
	public String verifyAddDeptIsRight(Department _dept) throws Exception;

	public String getDepartments(Tree tree,User user) throws JSONException;
	
	/**
	 * <P>方法名称：根据大额行号取机构，如果是本机构就返回当前机构，如果不是本机构就返回上级机构
	 * @param bankNum
	 * @return
	 * @throws Exception
	 */
	public String getDepartByisBranch(String bankNum) throws Exception;
	public String getProIdJson(Tree tree, String billMedia)throws Exception;
	
	public Department queryByOrgCode(String orgCode);
	
	public Department getCenterAcptDept() throws Exception;
	
	/**
	 * 根据机构id查询所有下级行的某个属性，如查询行号则returnParam填写department中的bankNumber属性
	 * @param param 机构id
	 * @param onlyOrg 是否只查询大额机构 true
	 * @param onlyUsed 是否只查询生效状态的
	 * @param returnParam department实体属性
	 * @return
	 * @throws Exception
	 */
	public List getAllChildrenDepartmentByDeptId(String param,boolean onlyOrg,boolean onlyUsed,String returnParam)throws Exception;
	
	
	/**
	 * 根据机构id查询所有库存机构为本机构的大额行号
	 * @param deptId
	 * @return
	 * @throws Exception
	 */
	public List getAllStockDepartmentByDeptId(String deptId)throws Exception;
	public List getAllOrgCod ()throws Exception;
	
	public List getAllChildrenDepartmentDtosByDeptId(String deptId,boolean onlyOrg,boolean onlyUsed)throws Exception;
	/**
	* <p>方法名称: getHeadBank|描述: 查询总行对象</p>
	* @return
	* @throws Exception
	*/
	public Department getHeadBank() throws Exception;
	
	/**
	* <p>方法名称: getSubsidiaryBank|描述: 通过行号查询分行对象</p>
	* @param bankNumber 行号
	* @return
	* @throws Exception
	*/
	public Department getSubsidiaryBank(String bankNumber) throws Exception;
	
	/**
	* <p>方法名称: getParentBank|描述: 通过行号查询父节点是总行和分行对象</p>
	* @param bankNumber 行号
	* @return
	* @throws Exception
	*/
	public Department getParentBank(String bankNumber) throws Exception;
	
	/**
	* <p>方法名称: getAcceptionBankList|描述: 查询承兑状态是‘独立承兑’的行</p>
	* @return
	* @throws Exception
	*/
	public List getAcceptionBankList() throws Exception;
	
	/**
	* <p>方法名称: getAcceptionBankNumber|描述: 查询承兑状态是‘独立承兑’的行号</p>
	* @return
	* @throws Exception
	*/
	public List getAcceptionBankNumber() throws Exception;
	
	/**
	 * <P>方法名称：与电票客户端的机构信息同步
	 * @param request 收到电票客户端发送的报文信息
	 * @return 返回保存或修改结果
	 *  2012-03-20 xingyu add
	 * @throws Exception
	 */
	public String txDepartmentToSave(ReturnMessage request) throws Exception;
	
	public String getDepartByBranch(String bankNum) throws Exception;
	
	/**
	 * <P>方法名称：查询已当前机构为库存机构的所有机构
	 * @param deptId
	 * @return
	 * @throws Exception
	 */
	public List getAllStockDeptIdByDeptId(String deptId)throws Exception;
	
	/**
	 * Xu Carry 20170413 增加主业务机构的条件
	 * 根据大额支付行号查找机构
	* <p>方法名称: getOrgCodeByBankNumber4RecMsg|描述: </p>
	* @param bankNumber
	* @return
	* @throws Exception
	 */
	public Department getOrgCodeByBankNumber4RecMsg(String bankNumber);
	/**
	* <p>方法名称: getDeptByPjsBrchNo|描述: 通过票交所机构号</p>
	* @param 票交所机构号
	* @return
	* @throws Exception
	*/
	public Department getDeptByPjsBrchNo(String pjsBrchNo);
	/**
	 * <p>方法说明:查机构已分配的资源树</p>
	 * @param user 当前登录用户
	 * @param pid 资源父id
	 * @param sync 是否异步加载 1是、0否
	 * @throws Exception
	 */
	public List queryAssignBranchAndResourceTree(User user,String pid,String sync)throws Exception;

	/**
	 * <p>方法名称: getHeadBankByMemberCode|描述: 根据票交所会员编码查询该所属总行信息</p>
	 * @param memberCode
	 * @param return Department 总行
	 */
	public Department getHeadBankByMemberCode(String memberCode);
	/**
	 * 根据机构父id查询当前法人下属机构信息
	 * 如果pid为空，则查询当前法人下属 所有机构信息
	 * @param pid
	 * @param user 当前用户
	 * @param status 状态
	 * @param async 是否异步加载 1是、0否
	 * @return  list 机构信息
	 * @throws Exception
	 */
	public List getAllDepartments(String pid,User user,int status,String async)throws JSONException;
	
	public String queryDeptInnerbankcode(String innerCode, int status) throws Exception;
	
	/**
     * 查询新保证金账号的归属机构，是否与客户经理的归属机构隶属同一分行
	 * 查询回来的核算机构与核算中心网点号比较判断是否隶属同一分行
     * @param marginAccount  保证金账号
     * @param managerId		客户经理
     * @return	true 同一分行   false  不同分行
     * @throws Exception
     */
	public boolean checkBranch (String marginAccount,String managerId) throws Exception;
//	void queryAndUpdateDept(Department dept, User user)
//			throws Exception;
	
	/**
	 * 根据机构id查询机构为武汉一级支行或非武汉分行的数据
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String queryParentBranch(String id) throws Exception;
}
