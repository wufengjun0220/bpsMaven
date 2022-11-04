package com.mingtech.framework.springinterceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 初始化ACTION
 * 
 * @author E-mail:hexin@mrhc.com.cn
 * @version
 * @since 2008-7-1 上午12:07:25
 * 
 */
public class ActionParamInterceptor implements HandlerInterceptor {
	private static Logger log = Logger.getLogger(ActionParamInterceptor.class);


	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		log.debug("...");
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		log.debug("...");
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2) throws Exception {
		return true;
	}

}
