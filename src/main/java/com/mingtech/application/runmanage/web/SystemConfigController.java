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
import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.runmanage.service.SystemConfigService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;



@Controller
public class SystemConfigController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(SystemConfigController.class);
	
	@Autowired
	SystemConfigService systemConfigService;
	@Autowired
	private CacheUpdateService cacheUpdateService;
	
	/**
	 * 查询系统配置信息
	 * @param type 配置标识
	 * @return
	 */
	@RequestMapping(value="/querySystemConfigList",method = RequestMethod.POST)
	public void querySystemConfigList(SystemConfig systemConfig){		
		try {
			Page page = this.getPage();
			List querySystemConfigInfo = systemConfigService.querySystemConfigInfo(systemConfig, page);
			String json = JsonUtil.buildJson(querySystemConfigInfo, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询配置信息失败"+ e.getMessage());
		} 
	}
	
	/**
	 * 新建/修改系统配置信息
	 * @return
	 */	
	@RequestMapping(value="/saveSystemConfigInfo",method = RequestMethod.POST)
	public void saveSystemConfigInfo(SystemConfig systemConfig){
		try {
			if(StringUtils.isNotBlank(systemConfig.getId())){
				SystemConfig codeIsExists = systemConfigService.chkConfigCodeIsExists(systemConfig.getCode(),systemConfig.getId());
				if(codeIsExists != null){
					this.getResponse().setStatus(400);
					this.sendJSON("该配置项编码已被使用，请重新输入。");
					return;
				}
				SystemConfig systemConfig2 = (SystemConfig)systemConfigService.load(systemConfig.getId());
				systemConfig2.setCode(systemConfig.getCode());
				systemConfig2.setDescrip(systemConfig.getDescrip());
				systemConfig2.setItem(systemConfig.getItem());
				systemConfig2.setType(systemConfig.getType());
				systemConfigService.txStore(systemConfig2);
				cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_SYSCONFIG);
				this.sendJSON("更新信息成功");
			}else{
				SystemConfig codeIsExists = systemConfigService.chkConfigCodeIsExists(systemConfig.getCode(),null);
				if(codeIsExists!=null){
					this.getResponse().setStatus(400);
					this.sendJSON("该编码已被使用，请重新输入。");
					return;
				}
				systemConfig.setId(null);
				systemConfigService.txStore(systemConfig);
				cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_SYSCONFIG);
				this.sendJSON(ErrorCode.SUCC_MSG_CH);
			}
		} catch (Exception e) {		
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(ErrorCode.ERR_MSG_998+e.getMessage());
		}
	}
	
	
	

	/**
	 * 删除及其批量删除
	 * @return
	 */
	@RequestMapping(value="/deleteSystemConfigByIds",method = RequestMethod.POST)
	public void deleteSystemConfigByIds(String ids){		
		try {
			systemConfigService.txDeleteSystemConfigInfo(ids);
			this.sendJSON(ErrorCode.SUCC_MSG_CH);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除失败："+ e.getMessage());
		}
	}	
}
