package com.mingtech.application.pool.base.web;

import io.jsonwebtoken.impl.TextCodec;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.JWTTokenUtil;
import com.mingtech.framework.common.util.CharacterEncoding;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @author wbliujianfei
 * 
 */
@Controller
public class BaseController {

	private static final Logger logger = Logger.getLogger(BaseController.class);
	protected static final String EXT_SUCCESS = "success";
	protected static final String EXT_MESSAGE = "msg";
	
	private static final ThreadLocal<HttpServletResponse> responseContainer = new ThreadLocal<HttpServletResponse>();
	private static final ThreadLocal<HttpServletRequest> requestContainer = new ThreadLocal<HttpServletRequest>();
	/**
	 * 记录默认的返回空串
	 */
	protected static final String RESULT_EMPTY_DEFAULT = "{\"results\":0,\"rows\":[]}";;
	/**
	 * 信息反馈持有容器
	 */
	protected Map messageHolder = new LinkedHashMap();

	public BaseController() {
		messageHolder.put(EXT_SUCCESS, Boolean.TRUE);
	}
	/**
	 * 前台往后台传日期时，日期格式必须为yyyy-MM-dd，否则无法解析
	 * @param binder
	 */
	@InitBinder
	public void initBinder (WebDataBinder binder){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class,new CustomDateEditor(dateFormat,true));
	}

	/**
	 * 通过@Autowired方式获取response有问题，暂时采取此种方式 换成@ModelAttribute表示在每次请求时都会调用
	 */
	@ModelAttribute
	public void setRequest(HttpServletRequest request) {
		requestContainer.set(request);
	}
	
	/**
	 * 通过@Autowired方式获取response有问题，暂时采取此种方式 换成@ModelAttribute表示在每次请求时都会调用
	 * request采用spring的自动注入方式是线程安全的，response、model是不安全的，采用ThreadLocal可以解决该问题
	 */
	@ModelAttribute
	public void setRepsone(HttpServletResponse response) {
		response.setContentType(CharacterEncoding.TEXT_HTML_UTF8);
		responseContainer.set(response);
	}
	
	 /**
	   * 获取当前线程的response对象
	   * 
	   * @return
	   */
	protected final HttpServletResponse getResponse() {
		HttpServletResponse response = responseContainer.get();
		User curUser = this.getCurrentUser();
		if(curUser!= null){
			String token = JWTTokenUtil.getInstance().getToken(curUser.getDepartment().getPjsMemberCode(), curUser.getLoginName(), curUser.getPassword());
			response.setHeader("authorization", token);
		}
		return response;
	}

	public Page getPage() {
		try {
			Page page = new Page();
			HttpServletRequest request = requestContainer.get();
			if (StringUtils.isNotBlank(request.getParameter("limit"))) {
				int limit = Integer.parseInt((request.getParameter("limit")));
				page.setPageSize(limit);
			}
			if (StringUtils.isNotBlank(request.getParameter("start"))) {
				int start = Integer.parseInt((request.getParameter("start")));
				page.setPageIndex(start / page.getPageSize() + 1);
			}
			if (StringUtils.isNotBlank(request.getParameter("sort"))) {
				String sort = (request.getParameter("sort"));
				page.setSort(sort);
			}
			if (StringUtils.isNotBlank(request.getParameter("dir"))) {
				String dir = (request.getParameter("dir"));
				page.setDir(dir);
			}
			return page;
		} catch (Exception e) {
			logger.error("获取分页信息异常",e);
			throw new RuntimeException("分页发生异常!", e);
		}
	}

	protected String convertListToJSON(List list) throws Exception {
		Map map = new HashMap();
		Page page = this.getPage();
		if(page != null) {
			map.put("totalProperty", "results," + page.getTotalCount());
		} else {
			map.put("totalProperty", "results," + list.size());
		}
		map.put("root", "rows");
		
		String json = JsonUtil.fromCollections(list, map);
		if(!(StringUtil.isNotBlank(json))){
			json = RESULT_EMPTY_DEFAULT;
		}
		return json;
	}
	
		public String getClientIp() {
		try {
			HttpServletRequest request = requestContainer.get();
			String remoteAddr = request.getHeader("X-FORWARDED-FOR");
	        if (remoteAddr == null || "".equals(remoteAddr)) {
	             remoteAddr = request.getRemoteAddr();
	        }
	        return remoteAddr;
		} catch (Exception e) {
			logger.error("获取客户端IP异常",e);
			throw new RuntimeException("获取客户端IP异常!", e);
		}
	}

	/**
	 * 向客户端输出正确/错误JSON格式信息
	 * 
	 * @param writer
	 *            输出流
	 * @param isErr
	 *            是否有错误
	 * @param errMsg
	 *            错误信息
	 * @throws IOException
	 */
	protected void sendJSON(Object obj) {
		String json = "";
		if (obj != null)
			json = obj.toString();
		Class cla = obj.getClass();
		PrintWriter pw = null;
		try {
			HttpServletResponse response = getResponse();
			pw = response.getWriter();
			if (StringUtils.equals(cla.getName(),
					java.lang.String.class.getName())) {
				json = JsonUtil.fromString((String) obj);
			}
			if (StringUtils.equals(cla.getName(),
					java.util.LinkedHashMap.class.getName())) {
				json = JsonUtil.fromObject((Map) obj);
			}
			if (StringUtils.equals(cla.getName(), "[Ljava.lang.Object;")) {
				json = JsonUtil.fromObject((Object[]) obj);
			}
			response.setContentType("text/html;charset=utf-8");
			pw.write(json);
		} catch (Exception e) {
			logger.error("返回应答信息异常",e);
			json = "系统发生异常，请联系系统管理员";
			if(pw != null) {
				pw.write(json);
			}
			
		} finally {
			
			logger.info("【"+DateTimeUtil.toDateTimeString(new Date())+"】向前端反馈请求结果" );
			
			if(pw != null) {
				pw.close();
			}
			responseContainer.remove();
			requestContainer.remove();

		}
	}
	
	/**
	 * 向客户返回登录结果信息
	 * 
	 * @param loginInfo 登录结果信息
	 * @param token 登录令牌
	 * @throws IOException
	 */
	protected void sendLoginJSON(String loginInfo,String token) {
		String json = "";
		PrintWriter pw = null;
		try {
			HttpServletResponse response = responseContainer.get();
			response.setHeader("Authorization", token);
			response.setHeader("authCode", token);
			pw = response.getWriter();
			json = JsonUtil.fromString(loginInfo);
			response.setContentType("text/html;charset=utf-8");
			pw.write(json);
		} catch (Exception e) {
			logger.error("返回应答信息异常",e);
			json = "系统发生异常，请联系系统管理员";
			if(pw != null) {
				pw.write(json);
			}
		} finally {
			if(pw != null) {
				pw.close();
			}
			responseContainer.remove();
			requestContainer.remove();
		}
	}

	/**
	 * 向客户端输出信息
	 * 
	 * @param writer
	 *            输出流
	 * @param isErr
	 *            是否有错误
	 * @param errMsg
	 *            错误信息
	 * @throws IOException
	 */
	protected void sendMessage(String str) {
		PrintWriter pw = null;
		try {
			HttpServletResponse response = getResponse();
			pw = response.getWriter();
			response.setContentType("text/html;charset=utf-8");
			pw.write(str);
		} catch (Exception e) {
			logger.error("返回应答信息异常",e);
			str = "系统发生异常，请联系系统管理员";
			if(pw != null) {
				pw.write(str);
			}
		} finally {
			if(pw != null) {
				pw.close();
			}
			responseContainer.remove();
			requestContainer.remove();

		}
	}
	
	protected User getCurrentUser() {
		HttpServletRequest request = requestContainer.get();
		String authorization = request.getHeader("Authorization");
		if(authorization == null){
			   authorization = request.getParameter("Authorization");
			}
		if(StringUtils.isBlank(authorization)) {
			return null;
		}
		String token="";
		try {
			token = new String(TextCodec.BASE64URL.decode(authorization.split("\\.")[1]),  "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("业务处理异常",e);
		}
		
		JSONObject tokeyObj = JSON.parseObject(token);
        String tokenSub = (String)tokeyObj.get("sub");
        RedisUtils redisrCache = (RedisUtils) SpringContextUtil.getBean("redisrCache");
        //edit 2021209 将redis中存放的用户、资源、机构对象转换为json字符串进行存放
		String strUserInfo = (String)redisrCache.hget(tokenSub, "user");
		User user = JSON.parseObject(strUserInfo, User.class);
		String strDeptInfo = (String)redisrCache.hget(tokenSub, "dept");
		Department dept = JSON.parseObject(strDeptInfo, Department.class);
		if(user!=null)
		user.setDepartment(dept);
		return user;
	}

	public HttpServletRequest getRequest() {
		return requestContainer.get();
	}
	
	//清理当前线程缓存
	public static void removeThreadLocal() {
		requestContainer.remove();
		responseContainer.remove();
	}
}
