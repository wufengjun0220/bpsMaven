package com.mingtech.application.runmanage.web;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.runmanage.domain.MicserviceConfig;
import com.mingtech.application.runmanage.service.MicserviceConfigService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 系统微服务配置
 * @author limaosong
 *
 */
@Controller
public class MicserviceConfigController extends BaseController {

	private static final Logger logger = Logger.getLogger(MicserviceConfigController.class);
	@Autowired
	private MicserviceConfigService micserviceConfigService;
	@Autowired
	private CacheUpdateService cacheUpdateService;

	/**
	 * 查询系统微服务配置页面信息
	 * @param micserviceConfig 搜索参数
	 */
	@RequestMapping(value="/queryMicserviceConfig",method = RequestMethod.POST)
	public void queryMicserviceConfig(MicserviceConfig micserviceConfig) {
		String json="";
		try {
			Page page = this.getPage();
			List<MicserviceConfig> resultList=micserviceConfigService.queryMicserviceConfig(micserviceConfig,page);
			json=JsonUtil.buildJson(resultList, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询系统微服务配置失败"+ e.getMessage());
		}
	}
	
	/**
	 * 编辑系统微服务配置信息
	 * @param micserviceConfig 参数
	 */
	@RequestMapping(value = "/editMicserviceConfig", method = RequestMethod.POST)
	public void editMicserviceConfig(MicserviceConfig micserviceConfig) {
		String json = "";
		try {
			micserviceConfigService.txEditMicserviceConfig(micserviceConfig);
			//更新产品类型缓存
			cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_MICSERVICE_CONFIG);
			this.sendJSON(ErrorCode.SUCC_MSG_CH);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("系统微服务配置编辑失败"+e);
		}
		
	}
	
	/**
	* 方法说明: 查询当前机构下的所有用户信息
	* @param User 用户信息
	*/
	
	@RequestMapping("/listUserOfConfig")
	public void listUserOfConfig(User user){
		try {
			Page page = this.getPage();
			List result = micserviceConfigService.listUserOfConfig(user,this.getCurrentUser(),page);
			String json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtil.isBlank(json)) {
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);	
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询失败："+e.getMessage());
		}
	}
}
