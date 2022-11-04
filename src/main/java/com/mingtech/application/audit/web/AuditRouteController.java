package com.mingtech.application.audit.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.framework.common.util.MvelUtil;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.audit.domain.AuditNodeDto;
import com.mingtech.application.audit.domain.AuditRouteDto;
import com.mingtech.application.audit.domain.AuditRouteParamterDto;
import com.mingtech.application.audit.service.AuditRouteService;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.vo.TreeDepartment;
import com.mingtech.framework.common.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import com.mingtech.framework.core.page.Page;

/**
 * 系统管理-审核路线管理
 * @author suijunjie
 * 描述：审批路线、审批节点、审批请求参数维护
 */
@Controller
public class AuditRouteController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(AuditRouteController.class);
	public static final String ERR_MSG_998 = "业务处理异常";
	public static final String ERR_MSG_FAIL="保存失败：";
	public static final String ERR_MSG_DEL="删除失败：";
	public static final String MSG_VAR="动态变量[";
	@Autowired
	private AuditRouteService auditRouteService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private RoleService roleService;
	//审核路线查询
	@RequestMapping("/queryAuditRoutes")
	public void queryAuditRoutes(AuditRouteDto auditRouteDto){
		try {
			Page page=this.getPage();
			User user = this.getCurrentUser();
			List result=auditRouteService.queryAuditRoutes(user,auditRouteDto,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	
	//保存审核路线
	@RequestMapping("/saveAuditRoute")
	public void saveAuditRoute(AuditRouteDto auditRouteDto){
		try {
			User user = this.getCurrentUser();
			auditRouteService.txSaveAuditRoute(user,auditRouteDto);
			this.sendJSON("保存成功");
		} catch (Exception e) {
			logger.error("保存异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(ERR_MSG_FAIL+e.getMessage());
		}
	}
	
	//删除审核路线
	@RequestMapping("/deleteAuditRoute")
	public void deleteAuditRoute(String id){
		try {
			auditRouteService.txDeleteAuditRoute(id);
			this.sendJSON("删除成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(ERR_MSG_DEL+e.getMessage());
		}
	}
	
	//获取审批路线信息
	@RequestMapping("/getAuditRouteInf")
	public void getAuditRouteInf(String id){
		String json = "";
		try {
			AuditRouteDto auditRouteDto= auditRouteService.getAuditRouteById(id);
			json =JsonUtil.fromObject(auditRouteDto);
			if(!(StringUtils.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "查询审核路线信息出错:"+e.getMessage();
		}
		this.sendJSON(json);
	}
	
	/**
	 * <p>方法说明: 获取审核路线下的所有审批节点</p>
	 */
	@RequestMapping("/queryAuditNodes")
	public void queryAuditNodes(String routeId){
		try {
			Page page=this.getPage();
			List result=auditRouteService.getAllNodesByRouteId(routeId,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtils.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	
	/**
	 * <p>方法说明: 保存审批节点</p>
	 */
	@RequestMapping("/saveAuditNode")
	public void saveAuditNode(AuditNodeDto node){
		try {
			auditRouteService.txSaveAuditNode(node);
			this.sendJSON("保存成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_FAIL,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("保存出错："+e.getMessage());
		}
	}
	
	/**
	 * <p>方法说明：删除审批节点</p>
	 * @param ids审批节点id，多个id用,号分割
	 */
	@RequestMapping("/deleteAuditNode")
	public void deleteAuditNode(String ids){
		try {
			auditRouteService.txDeleteAuditNode(ids);
			this.sendJSON("删除成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(ERR_MSG_DEL+e.getMessage());
		}
	}
	
	/**
	 * <p>方法说明：查询驳回审批节点</p>
	 * @param routeId审核路线id
	 */
	@RequestMapping("/queryCallBackAuditNode")
	public void queryCallBackAuditNode(String routeId){
		try {
			List list=auditRouteService.getAllNodesByRouteId(routeId,null);
			List result = new ArrayList();
			TreeDepartment tree=new TreeDepartment();
			tree.setId("-1");
			tree.setPid("0");
			tree.setName("无");
			result.add(tree);
			if(list != null && list.size() > 0){
				for(int i=0;i<list.size();i++){
					AuditNodeDto auditNode = (AuditNodeDto) list.get(i);
				    tree=new TreeDepartment();
					tree.setId(auditNode.getNodeNum());
					tree.setPid("0");
					tree.setName(auditNode.getNodeName()+"-"+auditNode.getNodeNum());
					result.add(tree);
				}
			}
			this.sendJSON(JsonUtil.buildJson(result, 0));
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
			
		}
	}
	
	/**
	 * <p>方法说明：查询审核角色</p>
	 * @param ids审批节点id，多个id用,号分割
	 */
	@RequestMapping("/listTreeAuditRole")
	public void listTreeAuditRole(){
		try {
			String memberCode = this.getCurrentUser().getDepartment().getPjsMemberCode();
			List list = roleService.getRoleJsonByMemberCode(null,null,memberCode);
			List result=new ArrayList();
			for(int i=0;i<list.size();i++){
				Role role = (Role) list.get(i);
				TreeDepartment tree=new TreeDepartment();
				tree.setId(role.getId());
				tree.setPid("0");
				tree.setName(role.getName());
				result.add(tree);
			}
			this.sendJSON(JsonUtil.buildJson(result, 0));
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	
	/**
	 * <p>方法名称: downNode|描述: 审批节点下移</p>
	 * @return
	 */
	@RequestMapping("/downAuditNode")
	public void downAuditNode(String nodeId){
		try {
			auditRouteService.txDownAuditNode(nodeId);
			this.sendJSON(RESULT_EMPTY_DEFAULT);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	/**
	 *
	* <p>方法名称: upAuditNode|描述: 审批节点上移</p>
	* @return
	* @throws Exception
	 */
	@RequestMapping("/upAuditNode")
	public void upAuditNode(String nodeId){
		try {
			auditRouteService.txUpAuditNode(nodeId);
			this.sendJSON(RESULT_EMPTY_DEFAULT);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	
	/**
	 *
	 * <p>描述: 查询审核路线已分配的机构和产品信息</p>
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryAssignBranchAndProduct")
	public void queryAssignBranchAndProduct(String routeId){
		try {
			User user=this.getCurrentUser();
			List result=auditRouteService.queryAuditRouteAssignInfo(user,routeId);
			this.sendJSON(JsonUtil.buildJson(result, 0));
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	/**
	 *
	 * <p>方法名称: assignAuditRoute|描述:为选中的机构和产品分配模板</p>
	 * @throws Exception
	 */
	@RequestMapping("/assignAuditRoute")
	public void assignAuditRoute(String deptIds, String prodIds, String routeIds) throws Exception{
		String msg="审核路线分配成功";
		try{
			auditRouteService.txAssignAuditRoute(deptIds, prodIds, routeIds);
		}catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			msg="审核路线分配失败,原因:"+e.getMessage();
		}
		this.sendJSON(msg);
	}
	
	/**
	 *
	 * <p>方法名称: chkMvelExprPlug|描述: MVEL动态表达式检查</p>
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/chkMvelExprPlug")
	public void chkMvelExprPlug(String mvelVar,String mvelExpr){
		try{
			Map dataMap = new HashMap();
			if(StringUtils.isNotEmpty(mvelVar)){
				String[] fileds = mvelVar.split("\\|");
				for(String filed:fileds){
					if(StringUtils.isNotEmpty(filed)){
						String[] arr = filed.split(";");
						if(arr.length!=3){
							throw new Exception(MSG_VAR+filed+"]设定规则不正确，请查看设定说明！");
						}
						try{
							if("BigDecimal".equalsIgnoreCase(arr[2])){
								dataMap.put(arr[0], new BigDecimal(arr[1]));
							}else if("int".equalsIgnoreCase(arr[2].trim())){
								dataMap.put(arr[0], Integer.valueOf(arr[1].trim()).intValue());
							}else if("String".equalsIgnoreCase(arr[2])){
								dataMap.put(arr[0],arr[1]);
							}else{
								throw new Exception(MSG_VAR+filed+"]数据类型不支持。目前，只支持BigDecimal、String、int！");
							}
						}catch(Exception err){
							throw new Exception(MSG_VAR+filed+"]数据值转换出错："+err.getMessage());
						}
					}
				}
			}
			Object mvelExprExeResult = (Object) MvelUtil.eval(mvelExpr,dataMap);
			this.sendJSON(mvelExprExeResult.toString());
		}catch(Exception e){
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
	
	/**
	 *
	* <p>方法说明: 根据审核路线id查询审核路线请求参数</p>
	* @throws Exception
	 */
	@RequestMapping("/queryAuditRouteParamter")
	public void queryAuditRouteParamter(String routeId){
		try {
			Page page=this.getPage();
			List result=auditRouteService.queryRouteParamtersByRouterId(routeId,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON( e.getMessage());
		}
	}
	
	/**
	 *
	* <p>方法说明: 保存审核路线请求参数</p>
	* @throws Exception
	 */
	@RequestMapping("/saveAuditRouteParam")
	public void saveAuditRouteParam(AuditRouteParamterDto routeParam ){
		try {
			User user = this.getCurrentUser();
			auditRouteService.txSaveAuditRouteParam(user,routeParam);
			this.sendJSON("保存成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_FAIL+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(ERR_MSG_FAIL+e.getMessage());
		}
	}
	
	/**
	 *
	 * <p>方法说明:删除审批业务表字段配置信息</p>
	 * @return void
	 */
	@RequestMapping("/deleteAuditRouteParam")
	public void deleteAuditRouteParam(String ids){
		try {
			User user = this.getCurrentUser();
			auditRouteService.txDeleteAuditRouteParam(user,ids);
			this.sendJSON("删除成功");
		} catch (Exception e) {
			logger.error(ERR_MSG_998,e);
			logger.error(ERR_MSG_DEL+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(ERR_MSG_DEL+e.getMessage());
		}
	}
	
	
	/**
	 * 查询可配置的审批节点
	 * @param routeId 审批路线id
	 * @param isEdit true编辑调用  false其他
	 */
	@RequestMapping("queryApproveNode")
	public void queryApproveNode(String routeId,boolean isEdit){
		String json = "";
		try {
			List approveNode = auditRouteService.queryApproveNode(routeId,isEdit);
			json = JsonUtil.buildJson(approveNode, approveNode.size());
			this.sendJSON(json);
		} catch (Exception e) {
			this.logger.error("查询失败："+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询失败："+e.getMessage());
		}
	}
}
