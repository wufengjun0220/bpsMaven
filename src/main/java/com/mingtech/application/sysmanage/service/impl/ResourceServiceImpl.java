package com.mingtech.application.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.application.sysmanage.service.ResourceService;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.application.sysmanage.vo.TreeNodeEx;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl extends GenericServiceImpl implements
		ResourceService {

	public String getResourcesJSON(Resource resource, Page page) throws Exception {
		List resources = this.getResources(resource,page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(resources, map);
	}
	
	/**
	 * 分页返回所有资源
	 * @param roleManager
	 * @return
	 * @throws Exception
	 */
	public List getResources(Resource resource, Page page) throws Exception {
		List paras = new ArrayList();
		String expression = " select resource from Resource as resource where 1 = 1 ";
		if (StringUtil.isNotBlank(resource.getName())) {
			expression += " and resource.name like ?";
			paras.add("%" + resource.getName() + "%");
		}
		if (StringUtil.isNotBlank(resource.getCode())) {
			expression += " and resource.code like ?";
			paras.add("%" + resource.getCode() + "%");
		}
		if(StringUtil.isNotBlank(resource.getActionName())){
			expression += " and resource.actionName like ?";
			paras.add("%" + resource.getActionName() +"%");
		}
		if(resource.getParent()!= null){
			expression += " and resource.parent.id like ?";
			paras.add("%"+resource.getParent().getId()+"%");
		}
		if(StringUtils.isNotBlank(resource.getPid())){
			if(!"-1".equals(resource.getPid())){
				 if("null".equals(resource.getPid())){
					 String dbType = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DATABASE_TYPE);		
					 if(StringUtils.isNotBlank(dbType) && "mysql".equalsIgnoreCase(dbType)){
						 expression += " and (resource.parent.id is null or resource.parent.id = '')";
					 }else{
						 expression += " and resource.parent.id is null";
					 }
				 }else{
					expression += " and resource.parent.id =? ";
					paras.add(resource.getPid());
				 }
				
			}
			
		}
		expression += "order by resource.order ASC";
		return find(expression, paras, page);
	}
	
	public List getChildren(Resource resource) throws Exception {
		String expression = "from Resource resource where resource.parent is null order by resource.order ASC";
		String sql = "from Resource resource where resource.parent.id = ? order by resource.order ASC";
		List resourceList = null;
		List paras = new ArrayList();
		if (resource == null) {
			resourceList = find(expression);
		} else {
			paras.add(resource.getId());
			resourceList = find(sql, paras);
		}
		return resourceList;
	}

	public String getResources(Tree tree) throws Exception {
		Resource resource = null;
		if (tree.getId() != null) {
			if (StringUtil.equals(tree.getId(), "-2")) {// 取根节点
				tree.setId("-1");
				tree.setText("资源树");
				return "[" + JsonUtil.fromObject(tree) + "]";
			}
			resource = (Resource) this.load(tree.getId());
		}
		List children = this.getChildren(resource);
		StringBuffer sb = new StringBuffer();
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		} else {
			List list = new ArrayList();
			for (int i = 0; i < children.size(); i++) {
				Resource child = (Resource) children.get(i);
				TreeNodeEx tmp = new TreeNodeEx();
				tmp.setText(child.getName());
				tmp.setId(child.getId());
				tmp.setCode(child.getUrl());
				if (CollectionUtil.isEmpty(getChildren(child))) {
					tmp.setLeaf(true);
				}
				list.add(tmp);
			}
			sb.append(JsonUtil.fromCollections(list));
		}
		return sb.toString();
	}

	public Class getEntityClass() {
		return Resource.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(Resource.class);
	}

	public List getChildren(String pid) throws Exception {
		String expression = "from Resource resource where resource.parent is null order by resource.order ASC";
		String sql = "from Resource resource where resource.parent.id = ? order by resource.order ASC";
		List resourceList = null;
		List paras = new ArrayList();
		if (pid == null) {
			resourceList = find(expression);
		} else {
			paras.add(pid);
			resourceList = find(sql, paras);
		}
		return resourceList;
	}
	
	public Resource getResourceById(String id) throws Exception{
		String sql = "from Resource resource where resource.id = ?";
		List paras = new ArrayList();
		paras.add(id);
		
		Resource re = (Resource) this.load(id, Resource.class);
		
		return re;
	}
	
	public List getShowResource(){
		List result = null;
		
		String hql = "from Resource resource where resource.isShow = 1 ";
		result = this.find(hql);
		
		return result;
	}
	
	/**
	 * 根据角色ID获取该角色分配的资源信息
	 * @param roleIds 角色id
	 * @return list 资源信息
	 */
	public List queryResourceListByRole(List roleIds){
		StringBuffer sql = new StringBuffer("select r.id,r.r_name,r.r_code,r.r_pid,r.r_url ,r.r_iconcss,r.r_type,r.r_order,r.r_action");
		sql.append(" from t_resource r");
		sql.append(" where  r.id in (");
		sql.append(" select  distinct resourceid  from t_role_resource o where o.roleid in(:roleIds)  ");
		sql.append(" ) order by r.r_order");
		
		//组装查询条件
		List keyList = new ArrayList();
		List valueList = new ArrayList();
		//行号
		keyList.add("roleIds");
		valueList.add(roleIds);
		
		String[] keis = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = dao.SQLQuery(sql.toString(), keis, valueList.toArray(), null);
		int count = list.size();
		List resourceList = new ArrayList();
		for(int i=0; i < count; i++){
			Object[] arrObj = (Object[])list.get(i);
			Resource res = new Resource();
			res.setId((String)arrObj[0]);//资源id
			res.setName((String)arrObj[1]);//资源名称
			res.setCode((String)arrObj[2]);//资源编码
			res.setPid((String)arrObj[3]);//父资源ID
			res.setUrl((String)arrObj[4]);//struct地址
			res.setIconCss((String)arrObj[5]);//图标名称
			res.setType(((BigDecimal)arrObj[6]).intValue());//类型
			res.setOrder(((BigDecimal)arrObj[7]).intValue());//资源序号
			res.setActionName((String)arrObj[8]);//action
			resourceList.add(res);
		}
		return resourceList;
	}

}
