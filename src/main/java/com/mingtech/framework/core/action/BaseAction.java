package com.mingtech.framework.core.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.CharacterEncoding;
import com.mingtech.framework.common.util.CurValues;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 基本Action基类
 * @author E-mail:hexin@
 * @since Jun 20, 2008
 */
public class BaseAction extends ActionSupport{
	private static final Logger logger = Logger.getLogger(BaseAction.class);

	private static final long serialVersionUID = -2703855456551334899L;
	private Page page;
	protected HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext servletContext;
	private HttpSession session;
	private PageContext pageContext;
	protected static final String EXT_SUCCESS = "success";
	protected static final String EXT_MESSAGE = "msg";
	/**
	 * 记录默认的返回空串
	 */
	protected static final String RESULT_EMPTY_DEFAULT = "{\"results\":0,\"rows\":[]}";;
	/**
	 * 信息反馈持有容器
	 */
	protected Map messageHolder = new LinkedHashMap();

	public BaseAction(){
		messageHolder.put(EXT_SUCCESS, Boolean.TRUE);
	}

	public void setPage(Page page){
		this.page = page;
	}

	public Page getPage(){
		try{
			page = new Page();
			if(StringUtil.isNotBlank(getRequest().getParameter("limit"))){
				int limit = Integer.parseInt((getRequest()
						.getParameter("limit")));
				page.setPageSize(limit);
			}
			if(StringUtil.isNotBlank(getRequest().getParameter("start"))){
				int start = Integer.parseInt((getRequest()
						.getParameter("start")));
				page.setPageIndex(start / page.getPageSize() + 1);
			}
			if(StringUtil.isNotBlank(getRequest().getParameter("sort"))){
				String sort = (getRequest().getParameter("sort"));
				page.setSort(sort);
			}
			if(StringUtil.isNotBlank(getRequest().getParameter("dir"))){
				String dir = (getRequest().getParameter("dir"));
				page.setDir(dir);
			}
			return page;
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			throw new RuntimeException("分页发生异常!!!", e);
		}
	}

	public HttpServletRequest getRequest(){
		return request;
	}

	public void setRequest(HttpServletRequest request){
		this.request = request;
	}

	public HttpServletResponse getResponse(){
		//response.setContentType(CharacterEncoding.TEXT_HTML_UTF8);
		response.setCharacterEncoding(CharacterEncoding.UTF8);

		return response;
	}

	public void setResponse(HttpServletResponse response){
		this.response = response;
	}

	public ServletContext getServletContext(){
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext){
		this.servletContext = servletContext;
	}

	public HttpSession getSession(){
		return session;
	}

	public void setSession(HttpSession session){
		this.session = session;
	}

	public PageContext getPageContext(){
		return pageContext;
	}

	public void setPageContext(PageContext pageContext){
		this.pageContext = pageContext;
	}

	/**
	 * 向客户端输出正确/错误JSON格式信息
	 * @param writer 输出流
	 * @param isErr 是否有错误
	 * @param errMsg 错误信息
	 * @throws IOException
	 */
	protected void sendJSON(Object obj){
		String json = "";
		if(obj!=null)
			json = obj.toString();
		Class cla = obj.getClass();
		PrintWriter pw = null;
		try{
			pw = this.getResponse().getWriter();
			if(StringUtil.equals(cla.getName(), java.lang.String.class.getName())){
				json = JsonUtil.fromString((String) obj);
			}
			if(StringUtil.equals(cla.getName(), java.util.LinkedHashMap.class
					.getName())){
				json = JsonUtil.fromObject((Map) obj);
			}
			if(StringUtil.equals(cla.getName(), "[Ljava.lang.Object;")){
				json = JsonUtil.fromObject((Object[]) obj);
			}
			this.getResponse().setContentType("text/html;charset=utf-8");
			pw.write(json);

			//this.getResponse().getWriter().flush();
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			json="系统发生异常，请联系系统管理员";
			pw.write(json);
		}finally{
		pw.close();

		}
	}
	/**
	 * 向客户端输出信息
	 * @param writer 输出流
	 * @param isErr 是否有错误
	 * @param errMsg 错误信息
	 * @throws IOException
	 */
	protected void sendMessage(String str){
		try{
			this.getResponse().getWriter().write(str);
			this.getResponse().getWriter().flush();
		}catch (IOException e){
			logger.error(e.getMessage(),e);
		}finally{
			// try {
			// //this.getResponse().getWriter().close();
			// } catch (IOException e) {
			// logger.error(e.getMessage(),e);
			// }
		}
	}


	protected User getCurrentUser(){
		return (User) session.getAttribute(CurValues.USER);
	}



	protected Map getResMap(){
		return (Map) session.getAttribute(CurValues.RESOURCEMAP);
	}
}
