package com.mingtech.framework.core.page;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 说明:全局翻页类
 * 
 * @author E-mail:hexin@
 * @version
 * @since 2008-6-30 下午10:17:17
 * 
 */
public class Page implements Serializable {

	private static final long serialVersionUID = 474686333071715399L;

	public static final int DEFAULT_PAGE_SIZE = 5;// 默认页数

	private int pageSize = DEFAULT_PAGE_SIZE;

	private int pageIndex = 1;// 当前页

	private long totalCount = 1;// 总行数

	private int pageCount = 1;// 总页数

	private String dir = "DESC"; // ASC或DESC排序方法
	private String sort = null; // 排序字段名
	private List result = null;// 查询结果集

	public Page() {
		ResourceBundle res = ResourceBundle.getBundle("config.project");
		String strPageSize = res.getString("defaultPageSize");
		if (strPageSize != null && !strPageSize.equals(""))
			pageSize = Integer.parseInt(res.getString("defaultPageSize"));
	}

	/**
	 * @param pageIndex
	 * @param pageSize
	 */
	public Page(int pageIndex, int pageSize) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.pageCount = (int) Math.ceil((double) totalCount
				/ (double) pageSize);
	}

	public Page(int pageIndex, int pageSize, int pageCount) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		if (pageSize < 1) {
			pageSize = 1;
		}
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.pageCount = pageCount;
	}

	public Page(int pageIndex) {
		this(pageIndex, DEFAULT_PAGE_SIZE);
	}

	public int getPageIndex() {
		if (pageIndex < 1) {
			return 1;
		}
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageCount() {
		int i = (int) Math.ceil((double) totalCount / (double) pageSize);
		return i;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getFirstResult() {
		return (pageIndex - 1) * pageSize;
	}

	public boolean getHasPrevious() {
		return pageIndex > 1;
	}

	public boolean getHasNext() {
		return pageIndex < pageCount;
	}

	public boolean isEmpty() {
		return totalCount == 0;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List getResult() {
		return result;
	}

	public void setResult(List result) {
		this.result = result;
	}

}
