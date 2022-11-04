package com.mingtech.framework.springinterceptors;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingtech.application.cache.MicServiceRouteCache;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.LogService;
import com.mingtech.framework.common.util.SpringContextUtil;

import io.jsonwebtoken.impl.TextCodec;


/**
 * 日志拦截器
 * @author h2
 *
 */
public class LogInterceptor implements HandlerInterceptor {
	private static final Logger logger = Logger.getLogger(LogInterceptor.class);

	private String getAction(String url){
		return url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));
	}
	
	protected String getRemoteIp(HttpServletRequest arg0){
		return arg0.getRemoteAddr();
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		BaseController.removeThreadLocal();//清除controller中临时存放的request response
		String authorization = request.getHeader("Authorization");
		if(authorization == null){
			   authorization = request.getParameter("Authorization");
			}
		if(StringUtils.isBlank(authorization)) {
			return;
		}
		String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null || "".equals(remoteAddr)) {
             remoteAddr = request.getRemoteAddr();
        }
        logger.info("真实ip:"+remoteAddr);
		String token="";
		try {
			token = new String(TextCodec.BASE64URL.decode(authorization.split("\\.")[1]),  "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("token解析异常",e);
		}
		
		JSONObject tokeyObj = JSON.parseObject(token);
        String tokenSub = (String)tokeyObj.get("sub");
        RedisUtils redisrCache = (RedisUtils) SpringContextUtil.getBean("redisrCache");
        //edit 20201209 2021209 将redis中存放的用户、资源、机构对象转换为json字符串进行存放
        String strUserInfo = (String)redisrCache.hget(tokenSub, "user");
		 if(StringUtils.isNotBlank(strUserInfo)) {
		    User user = JSON.parseObject(strUserInfo, User.class);
			String strDeptInfo = (String)redisrCache.hget(tokenSub, "dept");
			Department dept = JSON.parseObject(strDeptInfo, Department.class);
			user.setDepartment(dept);
			String actionName = getAction(request.getRequestURL().toString());
			LogService logService = (LogService) SpringContextUtil.getBean("logService");
			 Map paramMap = request.getParameterMap();
		     String json = JSON.toJSONString(paramMap);
			String routeInfo = MicServiceRouteCache.getRoutesDescMap(actionName+".mvc");
			JSONObject routeInfoObj = JSON.parseObject(routeInfo);
			String desc = "";
			String apiType = "4";
			String regOperLog = "0";
			if(routeInfoObj != null) {
				desc = routeInfoObj.getString("description");
				apiType = routeInfoObj.getString("apiType");
				regOperLog = routeInfoObj.getString("regOperLog");
			}
			if("1".equals(regOperLog)){
				logService.saveSysOperLogBySql(user,apiType,remoteAddr,desc,json);
			}
		 }
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2) throws Exception {
		return true;
	}

}