package com.mingtech.application.pool.bank.codec.config;

import java.util.List;

/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 
 *
 */
public class MessageTemplateNew extends MessageTemplate{
	private List sysHeaderItems;	       //报文SysHeader项描述条目列表
	private List appHeaderItems;	   	   //报文AppHeader描述条目列表
	private List localHeaderItems;	   	   //报文LocalHeader描述条目列表
	private List fileHeaderItems;	   	   //报文FileHeader描述条目列表
	private List bodyItems;	   	   	   	   //报文Body描述条目列表
	public List getSysHeaderItems() {
		return sysHeaderItems;
	}
	public void setSysHeaderItems(List sysHeaderItems) {
		this.sysHeaderItems = sysHeaderItems;
	}
	public List getAppHeaderItems() {
		return appHeaderItems;
	}
	public void setAppHeaderItems(List appHeaderItems) {
		this.appHeaderItems = appHeaderItems;
	}
	public List getLocalHeaderItems() {
		return localHeaderItems;
	}
	public void setLocalHeaderItems(List localHeaderItems) {
		this.localHeaderItems = localHeaderItems;
	}
	public List getFileHeaderItems() {
		return fileHeaderItems;
	}
	public void setFileHeaderItems(List fileHeaderItems) {
		this.fileHeaderItems = fileHeaderItems;
	}
	public List getBodyItems() {
		return bodyItems;
	}
	public void setBodyItems(List bodyItems) {
		this.bodyItems = bodyItems;
	}
	
}
