package com.mingtech.application.sysmanage.vo;

import java.util.List;

public class TreeNodeEx extends TreeNode{
	/**
	 * code扩展时使用 根据实际赋值有实际含义
	 */
	private String code;
	private String href;
	private String hrefTarget;
	private String url;
	/*********以下字段对接蚂蚁金服UI**********/
	private TreeNodeEx parent;
	private String pid;
	private String icon;
	private List children;
	
	private String value;
	private String lable;
	
	
	
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getHrefTarget() {
		return hrefTarget;
	}

	public void setHrefTarget(String hrefTarget) {
		this.hrefTarget = hrefTarget;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public TreeNodeEx getParent() {
		return parent;
	}

	public void setParent(TreeNodeEx parent) {
		this.parent = parent;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}
	
	
}
