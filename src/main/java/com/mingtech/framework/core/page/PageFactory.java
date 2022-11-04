package com.mingtech.framework.core.page;

import javax.servlet.http.HttpServletRequest;

/**
 * 说明:翻页工厂类
 * 
 * @author E-mail:hexin@
 * @version
 * @since 2008-6-30 下午10:24:52
 * 
 */
public class PageFactory {

	/**
	 * 构造器
	 */
	private PageFactory() {
	}

	/**
	 * 方法说明:取得page实例
	 * 
	 * @param servletrequest
	 *            前台request对象
	 * @return
	 * @author
	 * @since 2008-6-30 下午10:23:01
	 */
	public static final Page getPage(HttpServletRequest servletrequest) {
		Page page = new Page();

		if (servletrequest.getParameter("page_mode") == null) {
			return page;
		} else {
			String index = servletrequest.getParameter("page_index");
			page.setPageIndex(Integer.parseInt(index));
			String s1 = servletrequest.getParameter("page_mode");
			String s2 = servletrequest.getParameter("page_size");
			if (s2 == null || s2.trim().equals("")) {
				page.setPageSize(Page.DEFAULT_PAGE_SIZE);
			}

			int pages = Integer.parseInt(s2);
			String s3 = servletrequest.getParameter("page_count");
			int countPage = Integer.parseInt(s3);

			if ("first".equals(s1)) {
				page = new Page(1, pages, countPage);
				return page;
			}
			if ("priv".equals(s1)) {
				page = new Page(Integer.parseInt(index) - 1, pages, countPage);
				return page;
			}
			if ("next".equals(s1)) {
				page = new Page(Integer.parseInt(index) + 1, pages, countPage);
				return page;
			}
			if ("last".equals(s1)) {
				page = new Page(Integer.parseInt(s3), pages, countPage);
				return page;
			}
		}
		return page;
	}
}
