package com.mingtech.application.sysmanage.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.ResourceService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.vo.ToDoList;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
@Controller
public class RoleController extends BaseController{
	private static final long serialVersionUID = 8429409871595811853L;
	private static final Logger logger = Logger.getLogger(RoleController.class);
	@Autowired
	private RoleService roleService;
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private ConsignService consignService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	/**
	* 方法说明: 查询并展示角色列表
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午10:30:47
	*/
	@RequestMapping(value="/listRole")
	public void listRole(Role role) {
		try {

			String json = roleService.getRolesJSON(role,this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("查询角色信息失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询角色信息失败："+e.getMessage());
		}
	}
	
	/**
	* 方法说明: 保存或更新角色
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午10:29:57
	*/
	@RequestMapping(value="/saveRole",method = RequestMethod.POST)
	public void saveRole(Role role) {
		try {
			List temp = new ArrayList();
//			List rList = role.getResourceList();
//			if(rList != null && rList.size()>0){
//				for(int i = 0; i < role.getResourceList().size(); i++){
//					List list =resourceService.getChildren(rList.get(i).toString());
//					for(int j=0;j<list.size();j++){
//						Resource resource = (Resource) resourceService.load(((Resource)list.get(j)).getId());
//						temp.add(resource);
//					}
//					Resource resource1 = (Resource) resourceService.load(rList.get(i).toString());
//					temp.add(resource1);
//				}
//			}
//			role.setResourceList(temp);	
			//if(!roleService.isRepeatRole(role)){
				if (role.getId()!=null&&!role.getId().equals("")) {// 更新
					Role tmpRole = (Role) roleService.load(role.getId());
					tmpRole.setName(role.getName());
					tmpRole.setCode(role.getCode());
					tmpRole.setDescription(role.getDescription());
					tmpRole.setAmount(role.getAmount());
					tmpRole.setCreateTime(new Date());
					tmpRole.setScope(null);
					tmpRole.setMemberCode("0");
					tmpRole.setResourceList(role.getResourceList());
					//tmpRole.setProductType(StringUtil.getStringFromArray(audit));
					tmpRole.setCode(role.getCode());
					roleService.txStore(tmpRole);
					this.sendJSON("更新角色成功！");
				} else { // 新建
					role.setCreateTime(new Date());
					role.setScope(role.getScope());
					role.setMemberCode("0");
					//role.setProductType(StringUtil.getStringFromArray(audit));
					//当前role的id为空，将之设置为null，否则保存失败
					role.setId(null);
					roleService.txStore(role);
					role = new Role();
					this.sendJSON("保存角色成功！");
				}
		} catch (Exception e) {
			logger.error("保存角色失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("保存角色失败："+e.getMessage());
		}
		role = null;
	}

	/**
	* 方法说明: 删除角色
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-3-19 上午11:20:26
	*/
	@RequestMapping(value="/deleteRole",method = RequestMethod.POST)
	public void deleteRole(String id) {
		String msg = "";
		try{
			if(StringUtils.isNotBlank(id)){
				String[] roleIds = id.split(",");
				for(int i = 0;i < roleIds.length;i++){
					if(StringUtils.isNotBlank(roleIds[i])){
						Role tmp = (Role) roleService.load(roleIds[i]);
						if(!roleService.isRoleHasUser(roleIds[i])){
							roleService.txDelete(tmp);
						}else{
							msg += tmp.getName() + " ";
							
							throw new Exception(msg += "角色有关联用户，不能删除！");
						}
					}
				}
			}
			msg = "删除成功！";
		}catch(Exception e){
			logger.error("删除角色失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除角色失败："+e.getMessage());
		}
		this.sendJSON(msg);
	}

	/**
	* 方法说明: 获取机构可分配角色
	* @param deptId 机构id
	* @author  ice
	* @date 20190827
	*/
	@RequestMapping("/getRoleOfDept")
	public void getRoleOfDept(String deptId){
		User user = this.getCurrentUser();
		try {
			if(StringUtils.isBlank(deptId)){
				deptId = user.getDepartment().getId();
			}
			List roleList = roleService.getRolesByMemberCodeAndDeptId(user.getDepartment().getPjsMemberCode(),deptId);
			String json = JsonUtil.buildJson(roleList, 0);
			if (StringUtil.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	
	
	/**
	 * 方法说明: 展示角色所拥有的resource
	 * 

	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-19 下午06:18:40
	 */
	@RequestMapping(value="/getResourceByRoleId")
	public void getResourceByRoleId(String id) {
		try{
			String json = RESULT_EMPTY_DEFAULT;
			if (StringUtils.isNotBlank(id)) {
				Role role = roleService.getRoleById(id);
				List list = role.getResourceList();
				json = JSON.toJSONString(getResourceListId(list));
			}
			sendJSON(json);
		}catch (Exception e){
			logger.error("展示角色失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("展示角色失败："+e.getMessage());
		}
	}
	private String[] getResourceListId(List resList) throws Exception {
		if(resList.size() > 0){
			String[] ids = new String[resList.size()];
			for(int i = 0;i < resList.size();i++){
				ids[i] = ((Resource) resList.get(i)).getId();
			}
			return ids;
		}
		return null;
	}
	
	/**
	 * 方法说明:根据角色ID获取角色已分配范围
	 * @param roleId 角色id
	 * @author  ZJY
	 * @date 20190529
	 */
	@RequestMapping("/getRoleScopeByRoleId")
	public void getRoleScopeByRoleId(String roleId){
		try {
			Role role = roleService.getRoleById(roleId);
			if(StringUtils.isNotBlank(role.getScope())){
				String[] arrScope = role.getScope().split(",");
				List result = new ArrayList();
				for(String scope : arrScope){
					Role tmpRole = new Role();
					tmpRole.setScope(scope);
					result.add(tmpRole);
				}
				String json = JsonUtil.buildJson(result, 0);
				this.sendJSON(json);
			}else{
				this.sendJSON(RESULT_EMPTY_DEFAULT);
			}
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("角色使用访问查询失败：" + e.getMessage());
		}
		
	}
	
	/**
	 *
	 * <p>描述: 查询角色已分配资源信息</p>
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/queryRoleAssignResource",method = RequestMethod.POST)
	public void queryRoleAssignResource(String roleId){
		try {
			User user=this.getCurrentUser();
			Page page=this.getPage();
			Role role = roleService.getRoleById(roleId);
			List resources = role.getResourceList();
			String json = RESULT_EMPTY_DEFAULT;
			if(resources != null){
				 json = JsonUtil.buildJson(resources, 0);
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.info("查询角色已分配资源信息"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	
	/**
	 *
	 * <p>描述:为选中的角色分配资源信息</p>
	 * @param roleIds 角色id集合(多个id逗号分隔)
	 * @param resourceIds 资源id集合(多个id逗号分隔)
	 */
	@RequestMapping(value="/assigRoleResource",method = RequestMethod.POST)
	public void assigRoleResource(String roleIds, String resourceIds) throws Exception{
		String msg="机构资源分配成功";
		try{
			roleService.txAssigRoleResource(roleIds, resourceIds);
		}catch (Exception e) {
			logger.info("机构资源分配失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			msg="机构资源分配失败,原因:"+e.getMessage();
		}
		this.sendJSON(msg);
	}
	
	//待办事项汇总
	@RequestMapping(value="/mainPoolProtocolConfrmListAsJson",method = RequestMethod.POST)
		public void mainPoolProtocolConfrmListAsJson() {
			User user = this.getCurrentUser();
			List<ToDoList> listAll = new ArrayList<ToDoList>();
			try {
				ToDoList toDo1 = new ToDoList();
				ToDoList toDo2 = new ToDoList();
				ToDoList toDo3 = new ToDoList();
				ToDoList toDo4 = new ToDoList();
				
				toDo1.setToDoName("提示付款申请");
				toDo1.setToDoUrl("/collManager/Consign/toCollSendApplyBank");
				
				toDo2.setToDoName("提示付款记账");
				toDo2.setToDoUrl("/collManager/Consign/toCollctionSendAccount");
				
				toDo3.setToDoName("解质押签收异常处理");
				toDo3.setToDoUrl("/collManager/poolBillOutException/poolOutExceptionHandler");
				
				toDo4.setToDoName("解质押到期异常处理");
				toDo4.setToDoUrl("/collManager/poolBillOutException/poolOutExpireHandler");
				
				List list1 = consignService.getCollSendForBean(new QueryBean(), user, null);
				if(list1!=null&&list1.size()>0){
					toDo1.setToDoCount(list1.size()+"");
				}else{
					toDo1.setToDoCount("0");
				}
				
				List bankNums = new ArrayList();
				QueryBean querybean = new QueryBean();
				querybean.setSStatusFlag(PoolComm.TS03);
				List list2 = consignService.queryCollectionSendDto(querybean, bankNums, user, null);
				if(list2!=null&&list2.size()>0){
					toDo2.setToDoCount(list2.size()+"");
				}else{
					toDo2.setToDoCount("0");
				}
				
				List list3 = draftPoolQueryService.toPoolOutSignQuery(new DraftPoolOut(), user, null);
				if(list3!=null&&list3.size()>0){
					toDo3.setToDoCount(list3.size()+"");
				}else{
					toDo3.setToDoCount("0");
				}
				
				List list4 = draftPoolQueryService.toPoolOutExpireQuery(new CollectionSendDto(), user, null);
				if(list4!=null&&list4.size()>0){
					toDo4.setToDoCount(list4.size()+"");
				}else{
					toDo4.setToDoCount("0");
				}
				
				listAll.add(toDo1);
				listAll.add(toDo2);
				listAll.add(toDo3);
				listAll.add(toDo4);
			String json = JsonUtil.fromCollections(listAll);
			this.sendJSON(json);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
	
}
