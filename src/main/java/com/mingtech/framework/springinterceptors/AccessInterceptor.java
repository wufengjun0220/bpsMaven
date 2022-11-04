package com.mingtech.framework.springinterceptors;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.SpringContextUtil;

/**
 * 类说明:访问控制
 * @author E-mail:
 * @version
 * @since 2008-6-30 下午10:32:14
 */
public class AccessInterceptor implements HandlerInterceptor{
	private static Logger log = Logger.getLogger(AccessInterceptor.class);
	private static final String LOGOUT_MVC = "/logout.mvc";
	private static final String CONTENT_TYPE="text/html;charset=utf-8";
	private static final String ENCODING_UTF8 ="UTF-8";
	private static final String AUTH_CODE = "authCode";

	/**
	 * 方法说明: 校验当前用户是否可执行当前Action
	 * @param
	 * @author E-mail:
	 * @return
	 * @date 2009-3-9 上午09:49:27
	 */
	private boolean isAuthenticated(User user,Map  resMap){
	
		if(user != null){
			if(resMap == null){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}

	/**
	 * 方法说明: 在完整的url路径中截取当前执行的Action名称
	 * @param
	 * @author E-mail:
	 * @return
	 * @date 2009-3-9 上午09:37:15
	 */
	private String getAction(String url){
		if(url.lastIndexOf(".") != -1){
			return url
					.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
		}
		return "";
	}

	private boolean needCheckSession(String path){
		File pathfile = new File(path);
		String p_path = null;
		if(pathfile.getParent() != null){
			p_path = pathfile.getParent().replace('\\', '/');
		}
		// all except AuthenticateAction.action need
		if(path.endsWith("/login.mvc") || path.endsWith("/code.mvc")){
			return false;
		}
		if(path.endsWith(".mvc")){
			return true;
		}
		// all root file don't need
		if(p_path == null || p_path.equals("/ecds") || p_path.equals("/"))
			return false;
		return false;
	}

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
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {

		HttpServletRequest httpReq = request;
		String reqUri = httpReq.getRequestURI();
		
		log.info("【"+DateTimeUtil.toDateTimeString(new Date())+"】收到界面【"+ reqUri +"】请求...");
		 
		
		if(needCheckSession(reqUri)){
			//从head中获取token
			String authorization = request.getHeader("Authorization");
			if(authorization == null){
				authorization = request.getParameter("Authorization");
			}
			if(authorization == null){
				authorization = request.getHeader("authorization");
			}
			if(authorization == null){
				authorization = request.getParameter("authorization");
			}
			if(authorization == null){
				log.info("111111111111");
			}
			if(authorization == null || authorization.split("\\.").length < 2){
				log.info("22222222");
				if(reqUri.endsWith(LOGOUT_MVC) == false){
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				}
				String json = "无效的请求信息";
				PrintWriter pw = null;
				try {
					response.setCharacterEncoding(ENCODING_UTF8);
					response.setContentType(CONTENT_TYPE);
					pw = response.getWriter();
					json = JsonUtil.fromString(json);
					pw.write(json);
				} catch (Exception e) {
					json = "系统发生异常，请联系系统管理员.";
					log.error(json,e);
					if(pw != null) {
						pw.write(json);
					}
					
				} finally {
					if(pw != null) {
						pw.close();
					}
					
				}
				return false;
			}
			
			String token = new String(TextCodec.BASE64URL.decode(authorization.split("\\.")[1]),  ENCODING_UTF8);
			JSONObject tokeyObj = JSON.parseObject(token);
	        String tokenSub = (String)tokeyObj.get("sub");
	        RedisUtils redisrCache = (RedisUtils) SpringContextUtil.getBean("redisrCache");
	        //edit 20201209 2021209 将redis中存放的用户、资源、机构对象转换为json字符串进行存放
	        Map cacheUserInfoMap = redisrCache.hmget(tokenSub);
			String strUserInfo = (String)cacheUserInfoMap.get("user");
			User user = JSON.parseObject(strUserInfo, User.class);
			Map<String,String>  resMap = (HashMap<String,String>)cacheUserInfoMap.get("res");
			String authCode = (String)cacheUserInfoMap.get("authCode");
			if(!isAuthenticated(user,resMap)){
				//如果是登出则不设定http错误状态
				if(reqUri.endsWith(LOGOUT_MVC) == false){
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				}
				
				String json = "Session已过期。";
				PrintWriter pw = null;
				try {
					response.setCharacterEncoding(ENCODING_UTF8);
					response.setContentType(CONTENT_TYPE);
					pw = response.getWriter();
					json = JsonUtil.fromString(json);
					pw.write(json);
				} catch (Exception e) {
					json = "系统发生异常，请联系系统管理员.";
					log.error(json,e);
					if(pw != null) {
						pw.write(json);
					}
				} finally {
					if(pw != null) {
						pw.close();
					}
				}
				return false;
			}else{
				String singleLogin = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.SYS_SINGLE_USE_LOGIN);
				String reqAuthCode = request.getHeader(AUTH_CODE);
				//一个账号只允许登录一次
				if("true".equalsIgnoreCase(singleLogin) && !reqAuthCode.equals(authCode)){
					if(reqUri.endsWith(LOGOUT_MVC) == false){
						response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					}
					String json = "该用户已在其它设备登录,请重新登录。";
					PrintWriter pw = null;
					try {
						response.setCharacterEncoding(ENCODING_UTF8);
						response.setContentType(CONTENT_TYPE);
						pw = response.getWriter();
						json = JsonUtil.fromString(json);
						pw.write(json);
					} catch (Exception er) {
						json = "系统发生异常，请联系系统管理员";
						log.error(json,er);
						if(pw != null) {
							pw.write(json);
						}
					} finally {
						if(pw != null) {
							pw.close();
						}
					}
					return false;
				}
				 try {
					 String tokenJson = Jwts.parser().setSigningKey(user.getPassword())
	        		 .parseClaimsJws(authorization).getBody().toString();
			        } catch (Exception e) {
			            //如果是登出则不设定http错误状态
			            if(reqUri.endsWith(LOGOUT_MVC) == false){
			            	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			            }
						String json = "无效的令牌。";
						PrintWriter pw = null;
						try {
							response.setCharacterEncoding(ENCODING_UTF8);
							response.setContentType(CONTENT_TYPE);
							pw = response.getWriter();
							json = JsonUtil.fromString(json);
							pw.write(json);
						} catch (Exception er) {
							json = "系统发生异常，请联系系统管理员";
							log.error(json,er);
							if(pw != null) {
								pw.write(json);
							}
						} finally {
							if(pw != null) {
								pw.close();
							}
						}
			            return false;
			        }
				   //更新redis中缓存
					String strTimeOut = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.SYS_SESSION_TIMEOUT);
					if(StringUtils.isEmpty(strTimeOut)){
						strTimeOut = "20";
					}
				    int timeOut = Integer.valueOf(strTimeOut);//分钟
					timeOut = timeOut * 60;//描述
					redisrCache.hmset(tokenSub, cacheUserInfoMap, timeOut);
			}
		}
		
		return true;
	}
}
