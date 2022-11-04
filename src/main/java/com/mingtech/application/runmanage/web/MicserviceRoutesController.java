package com.mingtech.application.runmanage.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.runmanage.domain.MicserviceRoutes;
import com.mingtech.application.runmanage.service.MicserviceRoutesService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 系统微服务注册Controller
 * @author meng
 *
 */

@Controller
public class MicserviceRoutesController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(MicserviceRoutesController.class);
	@Autowired
	private MicserviceRoutesService micserviceRoutesService;
	@Autowired
	private CacheUpdateService cacheUpdateService;
	
	/**
	 * 查询微服务信息
	 * @return
	 */
	@RequestMapping(value="/queryMicserviceConfigs",method = RequestMethod.POST)
	public void queryMicserviceConfigs(){
		try {
				List list = micserviceRoutesService.queryMicserviceConfigs();
				String json = JsonUtil.buildJson(list, list.size());
				if(StringUtils.isBlank(json)){
					json = RESULT_EMPTY_DEFAULT;
				}
				this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询微服务注册列表失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 查询微服务注册列表
	 * @param
	 * @return
	 */
	@RequestMapping(value="/micserviceRoutesList",method = RequestMethod.POST)
	public void micserviceRoutesList(MicserviceRoutes micserviceRoutes){
		try {
				Page page = this.getPage();
				List list = micserviceRoutesService.queryMicserviceRoutes(micserviceRoutes, page);
				String json = JsonUtil.buildJson(list, page.getTotalCount());
				if(StringUtils.isBlank(json)){
					json = RESULT_EMPTY_DEFAULT;
				}
				this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询微服务注册列表失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 微服务注册-新增/修改
	 */
	@RequestMapping(value="/txMicserviceRoutesAdd",method = RequestMethod.POST)
	public void txMicserviceRoutesAdd(MicserviceRoutes micserviceRoutes)throws Exception{
		try {
			if(micserviceRoutesService.getMicserviceRoutesByReqUrl(micserviceRoutes)){
				micserviceRoutesService.txMicserviceRoutesAdd(micserviceRoutes);
				//更新产品类型缓存
				cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_MICSERVICE_ROUTE);
				this.sendJSON("成功");
			}else{
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("新增/修改失败，请求地址reqUrl已存在");
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("新增/修改失败"+ e.getMessage());
		} 
	}
	/**
	 * 微服务注册-删除
	 */
	@RequestMapping(value="/txMicserviceRoutesDel",method = RequestMethod.POST)
	public void txMicserviceRoutesDel(String id)throws Exception{
		try {
			MicserviceRoutes micserviceRoutes = (MicserviceRoutes) micserviceRoutesService.load(id, MicserviceRoutes.class);
			if(micserviceRoutes==null){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("删除失败");
			}
			micserviceRoutesService.txMicserviceRoutesDel(micserviceRoutes);
			//更新产品类型缓存
			cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_MICSERVICE_ROUTE);
			this.sendJSON("删除成功");
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除失败："+ e.getMessage());
		}
	}
}
