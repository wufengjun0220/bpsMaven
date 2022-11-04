package com.mingtech.application.audit.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.audit.domain.ApproveDto;
import com.mingtech.application.audit.domain.AuditNodeDto;
import com.mingtech.application.audit.domain.AuditRouteDto;
import com.mingtech.application.audit.domain.AuditRouteParamterDto;
import com.mingtech.application.audit.domain.RouteBrchProd;
import com.mingtech.application.audit.service.AuditBusiTableConfigService;
import com.mingtech.application.audit.service.AuditRouteService;
import com.mingtech.application.cache.ProductTypeCache;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.ProductTypeDto;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("auditRouteService")
public class AuditRouteServiceImpl extends GenericServiceImpl implements AuditRouteService{
	private Logger logger=Logger.getLogger(this.getClass());

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private AuditBusiTableConfigService auditBusiTableConfigService;
	
	/**
	 * <p>方法说明:查询审核路线</p>
	 * @param user 当前登录用户
	 * @param auditRouteDto
	 * @return list 审核路线信息
	 */
	public List queryAuditRoutes(User user,AuditRouteDto auditRouteDto, Page page) {
		StringBuffer hql=new StringBuffer("from AuditRouteDto  where memberCode=:memberCode ");
		List parasName = new ArrayList();
		List parasValue = new ArrayList();
		parasName.add("memberCode");
		parasValue.add(user.getDepartment().getPjsMemberCode());
		if(StringUtils.isNotBlank(auditRouteDto.getAuditRouteName())){
			hql.append(" and auditRouteName like '%"+auditRouteDto.getAuditRouteName()+"%'");
		}
		hql.append("  order by updateDate ");
		if(page!=null){
			return this.find(hql.toString(), (String[]) parasName.toArray(new String[parasName.size()]), parasValue.toArray(),page);
		}else{
			return this.find(hql.toString(), (String[]) parasName.toArray(new String[parasName.size()]), parasValue.toArray());
		}
	}

	/**
	 *
	 * <p>方法说明:保存审核路线</p>
	 * @param user 当前用户
	 * @param auditRouteDto 审核路线
	 * @return void
	 */
	public void txSaveAuditRoute(User user,AuditRouteDto auditRoute)throws Exception{
		AuditRouteDto oldAuditRoute = null;
		if(StringUtils.isNotBlank(auditRoute.getRouteId())){
			oldAuditRoute = this.getAuditRouteById(auditRoute.getRouteId());
		}else{
			oldAuditRoute = new AuditRouteDto();
		}
		oldAuditRoute.setMemberCode(user.getDepartment().getPjsMemberCode());
		oldAuditRoute.setAuditRouteName(auditRoute.getAuditRouteName());
		oldAuditRoute.setDesc(auditRoute.getDesc());
		oldAuditRoute.setConnThridSys(auditRoute.getConnThridSys());
		oldAuditRoute.setUpdateDate(DateUtils.getWorkDateTime());
		dao.store(oldAuditRoute);
	}
	
	/**
	 * <p>方法名称:根据ID查询审批路线</p>
	 * @param id 主键
	 * @return AuditRouteDto 审批路线
	 */
	public AuditRouteDto getAuditRouteById(String id)throws Exception{
		return (AuditRouteDto)dao.load(AuditRouteDto.class, id);
	}
	
	/**
	 *
	* <p>方法名称: txDelete|描述:根据ID删除模板</p>
	* @param id
	* @return
	 */
	public void txDeleteAuditRoute(String id) throws Exception{
		//查询该审批路线是否有机构在使用
		String hql="from RouteBrchProd where routeId='"+id+"'";
		List list=this.find(hql);
		if(!list.isEmpty()){
			throw new Exception("当前审批路线已被分配给机构使用，不允许删除。");
		}
		AuditRouteDto auditRouteDto = getAuditRouteById(id);
		//查询审批节点-进行删除
		List nodeList = this.getAllNodesByRouteId(id,null);
		if(!nodeList.isEmpty()){
			dao.deleteAll(nodeList);
		}
		//查询审批路线申请参数-进行删除
		List routeParamList = queryRouteParamtersByRouterId(id,null);
		if(!routeParamList.isEmpty()){
			dao.deleteAll(routeParamList);
		}
		this.dao.delete(auditRouteDto);//删除审批路线
	}
	
	
	
	
	/**
	 *
	* <p>方法名称: getAuditRouteBuyId|描述:根据ID获取模板</p>
	* @param distinctCode
	* @return
	 */
	public AuditNodeDto getAuditRouteBynodeNum(String routeId,String num){
		StringBuffer hql=new StringBuffer("from AuditNodeDto where 1=1 ");
		List parasName = new ArrayList();
		List parasValue = new ArrayList();
		if(StringUtils.isNotBlank(routeId)){
			hql.append(" and routeId =:routeId");
			parasName.add("routeId");
			parasValue.add(routeId);
		}
		if(StringUtils.isNotBlank(num)){
			hql.append(" and nodeNum =:nodeNum");
			parasName.add("nodeNum");
			parasValue.add(num);
		}
		List list=this.find(hql.toString(), (String[]) parasName.toArray(new String[parasName.size()]), parasValue.toArray());
		return (AuditNodeDto) list.get(0);
	}
	public void txUpAuditNode(String nodeId) throws Exception{
		AuditNodeDto node=(AuditNodeDto) this.dao.load(AuditNodeDto.class, nodeId);
		if(!node.getNodeNum().equals("1")){
			int nodeNum=Integer.parseInt(node.getNodeNum());
			AuditNodeDto node2=this.getAuditNodeByNodeNum(node.getRouteId(),String.valueOf(nodeNum-1));
			this.txExchangeNodeNum(node,node2);
			dao.store(node);
			dao.store(node2);
		}
	}

	public void txDownAuditNode(String nodeId) throws Exception{
		AuditNodeDto node=(AuditNodeDto) this.dao.load(AuditNodeDto.class, nodeId);
		List list=this.getAllNodesByRouteId(node.getRouteId(),null);
		int nodeNum=Integer.parseInt(node.getNodeNum());
		if(nodeNum!=list.size()){
			AuditNodeDto node2=this.getAuditNodeByNodeNum(node.getRouteId(),String.valueOf(nodeNum+1));
			this.txExchangeNodeNum(node,node2);
			dao.store(node);
			dao.store(node2);
		}
	}
	
	private void txExchangeNodeNum(AuditNodeDto node1,AuditNodeDto node2) throws Exception{
		String tempNodeNum=node1.getNodeNum();
		node1.setNodeNum(node2.getNodeNum());
		node2.setNodeNum(tempNodeNum);
	}
	
	public List getAllNodesByRouteId(String routeId , Page pg) throws Exception{
		String hql="from AuditNodeDto node where node.routeId=? order by node.nodeNum asc";
		List params=new ArrayList();
		params.add(routeId);
		List list;
		if(pg!=null){
			list=this.find(hql, params,pg);
		}else{
			list=this.find(hql, params);
		}
		AuditNodeDto node=null;
		Role role=null;
		if(list != null && list.size() > 0){
			for(int i=0;i<list.size();i++){
				node=(AuditNodeDto) list.get(i);
				if(StringUtils.isNotBlank(node.getOptRole())){
					role=(Role) this.dao.load(Role.class, node.getOptRole());
					node.setRoleName(role.getName());
				}
			}
			return list;
		}
		return null;
	}
	
	public List queryRouteParamtersByRouterId(String routeId , Page pg) throws Exception{
		String hql="from AuditRouteParamterDto dto where dto.routeId=? ";
		List params=new ArrayList();
		params.add(routeId);
		List list;
		if(pg!=null){
			list=this.find(hql, params,pg);
		}else{
			list=this.find(hql, params);
		}
	    return list;
	}
	
	/**
	 * <p>方法说明:保存审核路线请求参数</p>
	 * @param user 当前用户
	 * @param routeParam 审核路线请求参数
	 * @return void
	 */
	public void txSaveAuditRouteParam(User user ,AuditRouteParamterDto routeParam)throws Exception{
		//检查当前审核路线下是否有同名的请求参数
		StringBuffer hql= new StringBuffer("from AuditRouteParamterDto dto where dto.routeId=? ");
		List params=new ArrayList();
		params.add(routeParam.getRouteId());
		hql.append(" and dto.paramCode = ?");
		params.add(routeParam.getParamCode());
		if(StringUtils.isNotBlank(routeParam.getId())){
			hql.append(" and dto.id != ?");
			params.add(routeParam.getId());
		}
		List list = this.find(hql.toString(), params);
		if(!list.isEmpty()){
			throw new Exception("该参数已经存在，不允许再次添加");
		}
		
		AuditRouteParamterDto oldRouteParam = null;
		if(StringUtils.isNotBlank(routeParam.getId())){
			oldRouteParam = this.getAuditRouteParamById(routeParam.getId());
			oldRouteParam.setParamCode(routeParam.getParamCode().trim());
			oldRouteParam.setParamName(routeParam.getParamName().trim());
			oldRouteParam.setDataType(routeParam.getDataType());
		}else{
			oldRouteParam = routeParam;
		}
		dao.store(oldRouteParam);
		
	}
	
	/**
	 * 根据id查询审核路线请求参数
	 * @param id 审核路线请求参数主键
	 * @return AuditRouteParamterDto 审核路线请求参数
	 * @throws Exception
	 */
	public AuditRouteParamterDto getAuditRouteParamById(String id) throws Exception{
		return (AuditRouteParamterDto) dao.load(AuditRouteParamterDto.class, id);
	}
	
	/**
	 * 删除审核路线请求参数
	 * @param user 当前登录用户
	 * @param ids id集合
	 * @return void 
	 * @throws Exception
	 */
	public void txDeleteAuditRouteParam(User user,String ids) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" from AuditRouteParamterDto as dto where dto.id in (:ids)  ");
		
		List keyList = new ArrayList(); // 要查询的字段列表
		keyList.add("ids");
		List valueList = new ArrayList(); // 要查询的值列表
		List idList = Arrays.asList(ids.split(",")); //id集合
		valueList.add(idList);
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray());
		if(! list.isEmpty()){
			dao.deleteAll(list);
		}
	}
	

	/**
	 * <p>方法说明:删除审批节点</p>
	 * @param nodeIds多个审批节点使用,逗号分隔
	 * @return void
	 */
	public void txSaveAuditNode(AuditNodeDto node) throws Exception {
		if(node.getNodeId()==null){
			List list=this.getAllNodesByRouteId(node.getRouteId(),null);
			if(list != null && list.size() > 0){
				node.setNodeNum(String.valueOf(list.size()+1));
			}
			this.txStore(node);
		}else{
			AuditNodeDto nodeDto = (AuditNodeDto) this.load(node.getNodeId(),AuditNodeDto.class);
			
//			AuditNodeDto tempNode=getAuditNodeByNodeNum(node.getRouteId(),node.getNextNode());
//			if(tempNode==null){
//				nodeDto.setNextNode("0");
//			}
			AuditNodeDto tempNodeBack=getAuditNodeByNodeNum(node.getRouteId(),node.getBackNode());
			if(tempNodeBack==null){
				nodeDto.setBackNode("-1");
			}
			nodeDto.setNodeNum(node.getNodeNum());
			nodeDto.setNodeName(node.getNodeName());
			nodeDto.setNodeType(node.getNodeType());
			nodeDto.setOptRole(node.getOptRole());
			nodeDto.setNodeLevel(node.getNodeLevel());
			nodeDto.setEndFilg(node.getEndFilg());
			nodeDto.setCpAuditFlag(node.getCpAuditFlag());
			nodeDto.setMvelExpr(node.getMvelExpr());
			nodeDto.setConditions(node.getConditions());
			nodeDto.setNextNode(node.getNextNode());
			this.txStore(nodeDto);
		}
	}

	/**
	 * <p>方法说明:删除审批节点</p>
	 * @param ids 主键 多个审批节点使用,逗号分隔
	 * @return void
	 */
	public void txDeleteAuditNode(String ids) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		sb.append(" from AuditNodeDto as dto where dto.nodeId in (:ids)  ");
		
		List keyList = new ArrayList(); // 要查询的字段列表
		keyList.add("ids");
		List valueList = new ArrayList(); // 要查询的值列表
		List idList = Arrays.asList(ids.split(",")); //id集合
		valueList.add(idList);
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray());
		if(!list.isEmpty()){
			dao.deleteAll(list);
		}
	}
	/**
	 * 通过 审批模板ID+审批节点号  查询 审批节点信息
	 * @param routeId 审批模板ID
	 * @param nodeNum 审批节点号
	 * @return
	 * @throws Exception
	 */
	public AuditNodeDto getAllNodesByRouteIdAndNodeNo(String routeId,String nodeNum) throws Exception {
		String hql="from AuditNodeDto node where node.routeId=? and node.nodeNum=? ";
		List params=new ArrayList();
		params.add(routeId);
		params.add(nodeNum);
		List list=this.find(hql, params);
		if(list!=null && list.size()>0){
			return (AuditNodeDto)list.get(0);
		}
		return null;
	}
	
	public AuditNodeDto getAuditNodeByNodeNum(String routeId,String nodeNum) throws Exception{
		String hql="from AuditNodeDto node where node.routeId=:routeId and node.nodeNum=:nodeNum";
		List list=this.find(hql,new String[]{"routeId","nodeNum"},new Object[]{routeId,nodeNum},new Page());
		if(list.size()>0)
			return (AuditNodeDto)list.get(0);
		else
			return null;
	}
	/**
	 * <p>描述:为选中的机构和产品分配审核模板</p>
	 * @param branchIds 机构id-可为空
	 * @param prodIds 产品编号-可为空
	 * @param routeIds 审核路线id-不允许为空
	 * @throws Exception
	 */
	public void txAssignAuditRoute(String branchIds, String prodIds, String routeIds) throws Exception {
		if(StringUtils.isBlank(routeIds)){
			throw new Exception("请选择需要分配的审核路线");
		}
		String[] arrRouteIds = routeIds.split(",");
		int routeIdCount = arrRouteIds.length;
		if(routeIdCount > 1){
			throw new Exception("只允许选择单笔审核路线进行机构产品分配");
		}
		
		//如果产品prodIds或机构branchIds为空，则取消该审批路线已分配的所有机构产品信息
		if(StringUtils.isBlank(branchIds)){
			//删除审核路线已分配的机构和产品信息
			StringBuffer delhql = new StringBuffer("delete from RouteBrchProd where ");
			List params = new ArrayList();	
			delhql.append(" routeId=?");
			params.add(arrRouteIds[0]);
			dao.executeUpdateHQL(delhql.toString(),params.toArray());
			
		}else{//审核路线分配
			String[] arrBranchIds=branchIds.split(",");
			String[] arrProdIds=prodIds.split(",");
			//查询机构产品是否已分配了其它审核路线
			StringBuffer hql= new StringBuffer(" from RouteBrchProd ref where ref.routeId !=:routeId");
			
			List params = new ArrayList();
			List values = new ArrayList();
			//待分配审核路线id
			params.add("routeId");
			values.add(arrRouteIds[0]);
			//待分配机构id
			hql.append(" and ref.brchId in (:brchIds)");
			List brchIdList = new ArrayList();
			Collections.addAll(brchIdList,arrBranchIds);
			params.add("brchIds");
			values.add(brchIdList);
			//待分配产品ID
			hql.append(" and ref.prodId in (:prodIds)");
			List prodIdList = new ArrayList();
			Collections.addAll(prodIdList,arrProdIds);
			params.add("prodIds");
			values.add(prodIdList);
			
			String [] nameForSetVar = (String [])params.toArray(new String[params.size()]);
			Object[] parameters = values.toArray();
			List otherList = this.find(hql.toString(), nameForSetVar, parameters);
			if(!otherList.isEmpty()){
				RouteBrchProd otherRouteBrchProd = (RouteBrchProd) otherList.get(0);
				
				//判断是否是父节点，如果是父节点，则不校验
				ProductTypeDto dto = (ProductTypeDto)this.load(otherRouteBrchProd.getProdId(), ProductTypeDto.class);
				if(!"-1".equals(dto.getSSupeprodtId())){
					Department dept = departmentService.getDeptById(otherRouteBrchProd.getBrchId());
					String prodNm = ProductTypeCache.getProductName(otherRouteBrchProd.getProdId());
					AuditRouteDto auditRoute = this.getAuditRouteById(otherRouteBrchProd.getRouteId());
					throw new Exception(dept.getName()+"下的产品【"+prodNm+"】已分配了审核路线【"+auditRoute.getAuditRouteName()+"】，不允许再次分配其它审核路线。");
				} 
			}
			
			//删除审核路线已分配的机构和产品信息
			StringBuffer delhql = new StringBuffer("delete from RouteBrchProd where ");
		    params = new ArrayList();
			delhql.append(" routeId=?");
			params.add(arrRouteIds[0]);
			dao.executeUpdateHQL(delhql.toString(),params.toArray());
			//重新保存已分配的机构和产品信息
            int branchCount = arrBranchIds.length;
            int prodCount = arrProdIds.length;
            List<RouteBrchProd> newObjList = new ArrayList();
			for(int i=0; i<branchCount; i++){
				for(int j=0; j<prodCount; j++){
					RouteBrchProd ref=new RouteBrchProd();
					ref.setBrchId(arrBranchIds[i].trim());
					ref.setProdId(arrProdIds[j].trim());
					ref.setRouteId(arrRouteIds[0]);
					newObjList.add(ref);
				}
			}
			this.txStoreAll(newObjList);
		}
		
	}
	
	/**
	 * <p>方法说明:查询审核路线已分配信息</p>
	 * @param user 当前登录用户
	 * @param routeId 审核路线id
	 * @throws Exception
	 */
	public List queryAuditRouteAssignInfo(User user,String routeId)throws Exception{
		StringBuffer hql= new StringBuffer("from RouteBrchProd ref where ref.routeId=:routeId");
		List keyParam = new ArrayList();
		List valueParam = new ArrayList();
		keyParam.add("routeId");
		valueParam.add(routeId);
		String [] nameForSetVar = (String [])keyParam.toArray(new String[keyParam.size()]);
		Object[] parameters = valueParam.toArray();
		return this.find(hql.toString(), nameForSetVar, parameters);
	}
	
	
	
	/**
	 * 查询当前用户可驳回的节点信息
	 * @param routes
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List<ApproveDto> queryCanRejustdNodes(String id,User user,String routeId,String curAuditNodeId,String productId) throws Exception{
		
		List result1 = new ArrayList();
		List result2 = new ArrayList();
		//根据产品编号查询该产品是否配置 可选审批岗
		List busiTabConfList = auditBusiTableConfigService.queryBusiTabConfigsByProductId(productId);
		if(busiTabConfList != null && busiTabConfList.size() > 0){
			BusiTableConfig busiTableConfig = (BusiTableConfig) busiTabConfList.get(0);
			if(!"true".equals(busiTableConfig.getSubmitNode())){
				return null;
			}
		}
		//根据当前节点号查询审批节点信息( 驳回节点编号需小于当前节点编号)
		AuditNodeDto node = (AuditNodeDto)this.load(curAuditNodeId,AuditNodeDto.class);
		String nodeNum = node.getNodeNum();
		//查询已审批的节点信息(确定驳回范围)
		StringBuffer sql = new StringBuffer("select dto from ApproveDto dto where dto.processId = '"+id+"' and dto.approveFlag = '1' ");
		sql.append("and dto.nodeNum < '"+nodeNum+"' ");
		List list = dao.find(sql.toString());
		List userId = new ArrayList();
		//校验当前审批流（已审批人审批节点是否发生变化）
		if(list != null && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				ApproveDto dto = (ApproveDto)list.get(i);
				//查询当前审批流所有审批节点
				StringBuffer sql1 = new StringBuffer("select dto from AuditNodeDto dto ,Role role where dto.routeId = '"+routeId+"' ");
				sql1.append(" and dto.optRole = role.id ");
				sql1.append(" and dto.nodeNum = '"+dto.getNodeNum()+"' ");
				sql1.append(" and role.name = '"+dto.getApproveRole()+"' ");
				sql1.append(" and role.memberCode = '"+user.getDepartment().getPjsMemberCode()+"'");
				List list1 = dao.find(sql1.toString());
				if(list1!=null && list1.size() > 0){
					if(!userId.contains(dto.getApproveUserId())){
						dto.setApproveComment("审批节点"+dto.getNodeNum()+"--"+dto.getApproveUserNm());
						result1.add(dto);
					}
					userId.add(dto.getApproveUserId());
				}
			}
		}
		
		if(result1 != null && result1.size() > 0){
			//校验已审批人当前状态（机构、角色、用户信息是否正常）
			for (int i = 0; i < result1.size(); i++) {
				ApproveDto dto = (ApproveDto)result1.get(i);
				StringBuffer sql2 = new StringBuffer("select dto.id from T_USER dto,T_USER_ROLE ur,T_ROLE role,T_DEPARTMENT dept ");
				sql2.append(" where dto.ID = ur.USERID AND  ur.ROLEID = role.ID ");
				sql2.append(" AND dto.U_DEPTID = dept.ID ");
				sql2.append(" and dto.ID = '"+dto.getApproveUserId()+"' ");
				sql2.append(" and role.r_name = '"+dto.getApproveRole()+"' ");
				sql2.append(" and role.R_MEMBER_CODE = '"+user.getDepartment().getPjsMemberCode()+"'");
				sql2.append(" and dept.ID = '"+dto.getApproveDeptId()+"' ");
				List sqlQuery = dao.SQLQuery(sql2.toString());
				if(sqlQuery!=null && sqlQuery.size()>0){
					result2.add(dto);
				}
			}
		}
		
		//添加驳回节点（-1） 驳回节点为业务申请人
		ApproveDto dto = new ApproveDto();
		dto.setNodeNum("-1");
		dto.setApproveComment("审批节点-1--业务申请人");
		result2.add(dto);
		return result2;
	}
	
	
	/**
	 * 查询可配置的审批节点
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public List queryApproveNode(String routeId,boolean isEdit) throws Exception{
		String auditNodeNum = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.AUDIT_NODE_NUM);
		if(StringUtils.isBlank(auditNodeNum)){
			auditNodeNum = "10";
		}
		List list = new ArrayList();
		if(isEdit==true){
			for (int i = 1; i <= Integer.valueOf(auditNodeNum); i++) {
				AuditNodeDto auditNodeDto = new AuditNodeDto();
				auditNodeDto.setNodeNum(String.valueOf(i));
				auditNodeDto.setNodeName("审批节点-"+i);
				list.add(auditNodeDto);
			}
			return list;
		}
		
		//查询该审批路线下所有节点信息
		String hql="from AuditNodeDto node where node.routeId=? order by node.nodeNum asc ";
		List params=new ArrayList();
		params.add(routeId);
		List list1 = this.find(hql, params);
		String[] nodeNums = new String[Integer.valueOf(auditNodeNum)];
		if(list1 != null && list1.size() > 0 ){
			for (int i = 0; i < list1.size(); i++) {
				AuditNodeDto dto = (AuditNodeDto)list1.get(i);
				nodeNums[i] = dto.getNodeNum();
			}
		}
		for (int i = 1; i <= Integer.valueOf(auditNodeNum); i++) {
			if(String.valueOf(i).equals(nodeNums[i-1])){
				continue;
			}
			AuditNodeDto auditNodeDto = new AuditNodeDto();
			auditNodeDto.setNodeNum(String.valueOf(i));
			auditNodeDto.setNodeName("审批节点-"+i);
			list.add(auditNodeDto);
		}
		return list;
	}
	
	public Class getEntityClass() {
		return AuditRouteServiceImpl.class;
	}

	public String getEntityName() {
		return null;
	}
	
}
