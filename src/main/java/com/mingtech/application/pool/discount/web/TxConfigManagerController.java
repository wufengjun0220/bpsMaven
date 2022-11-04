package com.mingtech.application.pool.discount.web;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxConfigManagerService;
import com.mingtech.application.pool.query.web.CommonQueryController;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 在线贴现运营管理、在线业务查询
 * 
 * */
@Controller
public class TxConfigManagerController extends BaseController{
	private static final Logger logger = Logger.getLogger(CommonQueryController.class);
	@Autowired
	private TxConfigManagerService txConfigManagerService;
	
	@Autowired
	private CenterPlatformSysService  centerPlatformSysService;
	
	/**
	 * 客户在线业务开关信息查询
	 * */
	@RequestMapping("/OnLineTxCustConfig")
	public void OnLineTxCustConfig(CenterPlatformBean centerPlatformBean){
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
			
			String json = txConfigManagerService.queryCustConfig(centerPlatformBean, page, user);
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
	 * 客户在线业务开关信息变更通知
	 */
	@RequestMapping("changeOnLineTxCustConfig")
	public void changeOnLineTxCustConfig(@RequestBody CenterPlatformBean centerPlatformBean){
		try {
			User user = this.getCurrentUser();
			centerPlatformSysService.txChangeCustConfig(centerPlatformBean,user);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("在线协议开关处理失败"+ e.getMessage());
		}
	}
}
