package com.mingtech.application.sysmanage.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.service.ResourceService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

@Controller
public class ResourceController extends BaseController {
	private static final Logger logger = Logger.getLogger(ResourceController.class);
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private RoleService roleService;
	/**
	 * 前端新UI展示列表
	 * @author Ju Nana
	 * @date 2019-04-28 16:06:06
	 */
	@RequestMapping(value="/listResource")
	public void listResource(Resource resource) {
		try {
			Page page = this.getPage();
			List result = resourceService.getResources(resource,page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			String json = RESULT_EMPTY_DEFAULT;
			sendJSON(json);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			logger.error(ErrorCode.ERR_MSG_998,e);
		}
	}
	/**
	 * 方法说明: 删除
	 * 
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-20 上午09:57:54
	 */
	@RequestMapping(value="/deleteResource",method = RequestMethod.POST)
	public void deleteResource(String id) {
		try {
			Resource tmpResource = (Resource) resourceService.load(id);
			if (tmpResource != null) {
				List roleList = tmpResource.getRoleList();
				List resourceList = null;
				for (int i = 0; i < roleList.size(); i++) {
					Role r = (Role) roleList.get(i);
					resourceList = r.getResourceList();
					resourceList.remove(tmpResource);
					r.setResourceList(resourceList);
					roleService.txStore(r);
				}

				resourceService.txDelete(tmpResource);
				this.sendJSON("删除资源成功。");
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除资源失败："+e.getMessage());
		}
	}
	
	/**
	 * 方法说明: 保存
	 * 
	 * @param
	 * @author E-mail: pengdaochang@
	 * @return
	 * @date 2009-3-20 上午09:57:36
	 */
	@RequestMapping(value="/saveResource",method = RequestMethod.POST)
	public void saveResource(Resource resource) {
		try {
			if (StringUtil.isNotBlank(resource.getId())) {// 更新
				Resource tmpResource = (Resource) resourceService.load(resource
						.getId());
				tmpResource.setName(resource.getName());
				tmpResource.setDesc(resource.getDesc());
				tmpResource.setCode(resource.getCode());
				tmpResource.setType(resource.getType());
				tmpResource.setOrder(resource.getOrder());
				tmpResource.setActionName(resource.getActionName());
				tmpResource.setUrl(resource.getUrl());
				tmpResource.setIconCss(resource.getIconCss());
				tmpResource.setIsShow(resource.getIsShow());
				tmpResource.setShowName(resource.getShowName());
				tmpResource.setSort(resource.getSort());
				if(StringUtils.isNotBlank(resource.getPid()) && ( !resource.getPid().equals(tmpResource.getParent().getId()))){
					Resource parent = resourceService.getResourceById(resource.getPid());
					tmpResource.setParent(parent);
				}
				resourceService.txStore(tmpResource);
				this.sendJSON("更新资源成功！");
			} else {
				if(StringUtils.isNotBlank(resource.getPid())){
					Resource parent = resourceService.getResourceById(resource.getPid());
					resource.setParent(parent);
					}
				resourceService.txStore(resource);
				this.sendJSON("保存资源成功！");
			}
		} catch (Exception e) {
			logger.error("保存资源失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("保存资源失败："+e.getMessage());
		}
	}

	
}
