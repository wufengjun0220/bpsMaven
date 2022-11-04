package com.mingtech.application.audit.service;

import java.util.List;

import com.mingtech.application.audit.domain.ApproveDto;
import com.mingtech.application.audit.domain.AuditNodeDto;
import com.mingtech.application.audit.domain.AuditRouteDto;
import com.mingtech.application.audit.domain.AuditRouteParamterDto;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: suijunjie
* @日期: 2019-7-3 上午10:28:06
* @描述: [AuditRouteService]审核路线服务
*/
public interface AuditRouteService extends GenericService{
	/**
	 *
	 * <p>方法说明:查询审核路线</p>
	 * @param user 当前登录用户
	 * @param auditRouteDto 查询条件
	 * @return list 审核路线信息
	 */
	public List queryAuditRoutes(User user,AuditRouteDto auditRouteDto, Page page);
	
	/**
	 *
	 * <p>方法说明:保存审核路线</p>
	 * @param user 当前用户
	 * @param auditRouteDto 审核路线
	 * @return void
	 */
	public void txSaveAuditRoute(User user,AuditRouteDto auditRoute)throws Exception;
	
	/**
	 * <p>方法名称:根据ID查询审批路线</p>
	 * @param id 主键
	 * @return AuditRouteDto 审批路线
	 */
	public AuditRouteDto getAuditRouteById(String id)throws Exception;
	
	/**
	 *
	 * <p>方法说明:根据ids删除审批路线</p>
	 * @param ids 主键
	 * @return void
	 */
	public void txDeleteAuditRoute(String id)throws Exception;
	/**
	 *
	 * <p>方法说明:节点上移</p>
	 * @param nodeId
	 * @return
	 */
	public void txUpAuditNode(String nodeId) throws Exception;
	/**
	 *
	 * <p>方法说明:节点下移</p>
	 * @param nodeId
	 * @return
	 */
	public void txDownAuditNode(String nodeId) throws Exception;
	/**
	 *
	 * <p>方法说明:|描述:根据模板获取所有节点</p>
	 * @param routeId
	 * @return
	 */
	public List getAllNodesByRouteId(String routeId , Page pg) throws Exception;
	
	/**
	 * <p>方法说明:根据审核路线id查询配置的请求参数</p>
	 * @param routeId
	 * @return list 审批路线请求参数
	 */
	public List queryRouteParamtersByRouterId(String routeId,Page pg) throws Exception;
	
	/**
	 * <p>方法说明:保存审核路线请求参数</p>
	 * @param user 当前用户
	 * @param routeParam 审核路线请求参数
	 * @return void
	 */
	public void txSaveAuditRouteParam(User user ,AuditRouteParamterDto routeParam)throws Exception;
	
	/**
	 * 根据id查询审核路线请求参数
	 * @param id 审核路线请求参数主键
	 * @return AuditRouteParamterDto 审核路线请求参数
	 * @throws Exception
	 */
	public AuditRouteParamterDto getAuditRouteParamById(String id) throws Exception;
	
	/**
	 * 删除审核路线请求参数
	 * @param user 当前登录用户
	 * @param ids id集合
	 * @return void 
	 * @throws Exception
	 */
	public void txDeleteAuditRouteParam(User user,String ids) throws Exception;
	
	/**
	 * <p>方法说明:保存审批节点信息</p>
	 * @param node 审批节点
	 * @return void
	 */
	public void txSaveAuditNode(AuditNodeDto node) throws Exception;
	
	/**
	 * <p>方法说明:删除审批节点</p>
	 * @param ids 主键 多个审批节点使用,逗号分隔
	 * @return void
	 */
	public void txDeleteAuditNode(String ids) throws Exception;

	/**
	 * <p>描述:为选中的机构和产品分配审核模板</p>
	 * @param branchIds 机构id-可为空
	 * @param prodIds 产品编号-可为空
	 * @param routeIds 审核路线id-不允许为空
	 * @throws Exception
	 */
	public void txAssignAuditRoute(String brchIds, String prodIds,String routeIds) throws Exception;

	/**
	 * <p>方法说明:查询审核路线已分配信息</p>
	 * @param user 当前登录用户
	 * @param routeId 审核路线id
	 * @throws Exception
	 */
	public List queryAuditRouteAssignInfo(User user,String routeId)throws Exception;
	
	
	/**
	 * 查询当前用户可驳回的节点信息
	 * @param id
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List<ApproveDto> queryCanRejustdNodes(String id,User user,String routeId,String curAuditNodeId,String productNo) throws Exception;
	
	/**
	 * 查询可配置的审批节点
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public List queryApproveNode(String routeId,boolean isEdit) throws Exception;
}
