package com.mingtech.application.sysmanage.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.application.sysmanage.vo.TreeDepartment;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
@Controller
public class DepartmentController extends BaseController {

	private static final Logger logger = Logger.getLogger(DepartmentController.class);
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	/**
	 * 机构查询列表显示
	 */
	@RequestMapping(value="/listDept")
	public void listDept(Department department){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			String json = departmentService.getDeptsJSON(department,page,user);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}

	/**
	 * 机构开关展示
	 */
	@RequestMapping(value="/listDept1")
	public void listDept1(Department department){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			String json = departmentService.getDeptsJSON1(department,page,user);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_997,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}

	/**
	 * 方法说明: 保存部门,ID为空为新建,否则为更新
	 *
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-10 上午10:05:44
	 */
	@RequestMapping("saveDepartment")
	public void saveDepartment(Department department) {
		try {
			if (StringUtil.isNotBlank(department.getId())) {// 更新
				Department tmpDepartment = (Department) departmentService
						.load(department.getId());
				tmpDepartment.setName(department.getName());
				tmpDepartment.setIsOrg(Department.DEPT_ORG);
//				if (department.getIsOrg() == Department.DEPT_DEPARTMENT) {
//					tmpDepartment.setLevel(0);
//				}
				tmpDepartment.setDescription(department.getDescription());
				tmpDepartment.setInnerBankCode(department.getInnerBankCode());
//				tmpDepartment.setOrder(department.getOrder());
				tmpDepartment.setLevel(department.getLevel());
				tmpDepartment.setOrgCode(department.getOrgCode());
				tmpDepartment.setBankNumber(department.getBankNumber());
				//核算中心网点号
				tmpDepartment.setAuditBankCode(department.getAuditBankCode());
//				tmpDepartment.setStockDept((Department) departmentService.load(stockDeptId));
//				tmpDepartment.setDiscountDtockDept((Department) departmentService.load(discountDtockDeptId));
				//票交所机构号，交易柜员号，交易柜员名
				tmpDepartment.setPjsMemberCode(department.getPjsMemberCode());
				tmpDepartment.setPjsBrchNo(department.getPjsBrchNo());
				tmpDepartment.setPjsUserNo(department.getPjsUserNo());
				tmpDepartment.setPjsUserName(department.getPjsUserName());
				
				tmpDepartment.setDepartSocialCode(department.getDepartSocialCode());
				tmpDepartment.setPjsDepartName(department.getPjsDepartName());
				tmpDepartment.setPjsRole(department.getPjsRole());
				tmpDepartment.setCprBrchNo(department.getCprBrchNo());
				tmpDepartment.setYcFlag(department.getYcFlag());
				tmpDepartment.setLdFlag(department.getLdFlag());
				

				Department parent = department.getParent();
				if (parent != null && !StringUtil.isStringEmpty(parent.getId())) {
					parent = (Department) departmentService
							.load(parent.getId());
					department.setParent(parent);
				}
				departmentService.txStore(tmpDepartment);
				this.sendJSON("更新成功！");
			} else {
				String levelCode = "1000";
				Department parent = department.getParent();
				if (parent != null && !StringUtil.isStringEmpty(parent.getId())) {
					parent = (Department) departmentService
							.load(parent.getId());
					department.setParent(parent);
					String pLevCode = parent.getLevelCode();
					List list= this.departmentService.getAllChildren(parent.getId(), -1);
					int lve = list.size()+1;
					levelCode=getSubDeptLevelCode(pLevCode,lve+"");
				}
				department.setLevelCode(levelCode);
				departmentService.txStore(department);
				this.sendJSON("更保存成功！");
			}
		} catch (Exception e) {
			logger.error("操作失败！"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("操作失败！"+e.getMessage());
		}
	}
	/**
	 * 根据 父机构  层级编码获取 子机构编码
	 * @param pLev
	 * @param num
	 * @return
	 */
	private String  getSubDeptLevelCode(String pLev,String num){
		String lev = pLev;
		if(num.length()==1){
			lev=lev+"100"+num;
		}else if(num.length()==2){
			lev=lev+"10"+num;
		}else if(num.length()==3){
			lev=lev+"1"+num;
		}else{
			lev=lev+num;
		}
		return lev;
	}

	/**
	 * 方法说明: 删除部门,并未实际删除 设置状态位 未完成：有孩子部门 有人员均不让删除
	 *
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-10 上午10:31:38
	 */
	@RequestMapping(value="/deleteDepartment",method = RequestMethod.POST)
	public void deleteDepartment(String departmentIds) {
		String result="true";
		try {
			Department dept = (Department) departmentService.load(departmentIds);

			if (dept.getParent() == null) {
				result="根部门不允许被删除！";
			}else if (departmentService.hasChildren(dept.getId(), -1, -1)) {
				result="该部门下已包含子部门，不允许删除！";
			}else if(userService.isDeptHasUser(dept.getId(), false)){
				result="该部门下已包含用户信息，不允许删除！";
			}else{
				departmentService.txDelete(dept);
				result=RESULT_EMPTY_DEFAULT;
			}
		} catch (Exception e) {
			logger.error("操作失败！"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			result="数据库异常"+e.getMessage();
		}
		this.sendJSON(result);
	}
	/**
	 * 方法说明: 改变部门状态
	 *
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-16 下午04:26:24
	 */
	@RequestMapping(value="/changeDeptStatus",method = RequestMethod.POST)
	public void changeDeptStatus(Department department) {
		try {
			if (department != null
					&& StringUtils.isNotEmpty(department.getId())) {
				department = (Department) departmentService.load(department
						.getId());
				if (null != department) {
					if (department.getStatus() == 1) {
						// 判断该部门下是否有子部门或包含用户（未停用的）
						if (departmentService
								.hasChildren(department.getId(), 1)
								|| userService.isDeptHasUser(
										department.getId(), true)) {
							this.sendJSON("当前部门包含未停用的子部门或用户，不允许停用！");
						} else {
							department.setStatus(0);
						}
					} else {
						department.setStatus(1);
					}
					departmentService.txStore(department);
					this.sendJSON("部门状态更改成功！");
					}
			}
		} catch (Exception e) {
			logger.error("操作失败！"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("操作失败！"+e.getMessage());
		}
	}
	/**
	* 方法说明: 查询机构已分配的资源树
	* @param pid 资源父id
	* @param sync 是否异步加载 1是、0否
	* @return void
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping(value="/listTreeDeptAssignResorce",method = RequestMethod.POST)
	public void listTreeDeptAssignResorce(String pid,String sync){
		try {
			List resources = departmentService.queryAssignBranchAndResourceTree(this.getCurrentUser(),pid,sync);
			List result = new ArrayList();
			for(int i=0;i<resources.size();i++){
				Resource res = (Resource) resources.get(i);
				TreeDepartment tree=new TreeDepartment();
				tree.setId(String.valueOf(res.getId()));
				if(res.getParent() == null){
					tree.setPid("null");
				}else{
					tree.setPid(res.getParent().getId());
				}
				tree.setName(res.getName());
				result.add(tree);
			}
			this.sendJSON(JsonUtil.buildJson(result, 0));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}

	/**
	 * 方法说明: 查询并展示机构树
	 * @param pid 父产品id
	 * @param showSelf 显示当前机构1是、否0
	 * @param showRoot 显示虚拟机构1是、否0
	 * @param async 是否异步加载 1是、否0
	 * @author  h2
	 * @return void
	 * @date 2019-03-15 上午10:30:47
	 */
	@RequestMapping(value="/listTreeDept")
	public void listTreeDept(String pid,String showSelf,String showRoot,String async){
		try {
			List deptList = departmentService.getAllDepartments(pid,this.getCurrentUser(),1,async);
			List result=new ArrayList();
			Department curDept = this.getCurrentUser().getDepartment();
			if(StringUtils.isBlank(pid)){
				if("1".equals(showRoot)){
					//虚拟机构或一级机构需要显示虚拟根机构
					if(curDept.getParent() != null && "-1".equals(curDept.getParent().getId())){
						TreeDepartment tree=new TreeDepartment();
						tree.setId("-1");
						tree.setPid("null");
						tree.setName("虚拟机构");
						result.add(tree);
					}

				}
				if(!"0".equals(async) && "1".equals(showSelf)){//异步加载并且显示自己
					//虚拟机构或一级机构需要显示虚拟根机构
					if(curDept.getParent() != null && "-1".equals(curDept.getParent().getId())){
						TreeDepartment tree=new TreeDepartment();
						tree.setId(curDept.getId());
						if(curDept.getParent() == null){
							tree.setPid("null");
						}else{
							tree.setPid(curDept.getParent().getId());
						}
						tree.setName(curDept.getName());
						result.add(tree);
					}else{
						TreeDepartment tree=new TreeDepartment();
						tree.setId(curDept.getId());
						tree.setPid("null");
						tree.setName(curDept.getName());
						result.add(tree);
					}
				}

			}

			for(int i=0;i<deptList.size();i++){
				Department dept = (Department) deptList.get(i);
				if(!"1".equals(showSelf) && curDept.getId().equals(dept.getId())){
					continue;
				}
				if(dept.getName().equals("汉口银行")){
					continue;
				}
				TreeDepartment tree=new TreeDepartment();
				tree.setId(String.valueOf(dept.getId()));
				if(dept.getParent() == null){
					tree.setPid("null");
				}else{
					tree.setPid(dept.getParent().getId());
				}
				tree.setName(dept.getName());
				result.add(tree);
			}
			this.sendJSON(JsonUtil.buildJson(result, 0));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 * 机构在线协议开关
	 * @param id
	 * @param type  0:在线银承   1:在线流贷
	 */
	@RequestMapping(value="changeDepartOnline")
	public void changeDepartOnline(String id , String type, boolean status){
		try {
//			User user = this.getCurrentUser();
//			CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
			Department dept = (Department) departmentService.load(id,Department.class);
//			boolean flag = true;
			if(type.equals(PoolComm.XD_01)){
				if(status){
					dept.setYcFlag("0");
				}else{
					dept.setYcFlag("1");
				}
			}else if(type.equals(PoolComm.XD_02)){
				if(status){
					dept.setLdFlag("0");
				}else{
					dept.setLdFlag("1");
				}
			}else if(type.equals(PoolComm.XD_07)){
			    if(status){
			        dept.setTxFlag("0");
			    }else{
			        dept.setTxFlag("1");
			    }
			    
//			    //	调用机构在线业务开关信息变更通知接口
//			    
//			    centerPlatformBean.setTxFlag(dept.getTxFlag());
//			    centerPlatformBean.setId(id);
//			    centerPlatformBean.setBranchCode(dept.getInnerBankCode());;
//			    centerPlatformBean.setBranchName(dept.getName());;
//			    flag = centerPlatformSysService.txChangeDepartMentConfig(centerPlatformBean,user);
			}
//			if(flag){
//				departmentService.queryAndUpdateDept(dept, user);
//			}
			departmentService.txStore(dept);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线协议机构开关处理失败"+ e.getMessage());
		}
	}

}
