package com.mingtech.application.pool.bank.codec.config;

import java.util.List;

/**
 * <p> * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司  * </p> 
 * @作者: chenwei
 * @日期: Jun 17, 2009 4:31:40 PM
 * @描述: [MessageTemplate]报文数据映射模板对象
 */
public class MessageTemplate {
	
	private List mainItems;	       //报文主包数据项描述条目列表
	private List detailItems;	   //报文明细数据项描述条目列表
	
	public List getMainItems() {
		return mainItems;
	}
	public void setMainItems(List mainItems) {
		this.mainItems = mainItems;
	}
	public List getDetailItems() {
		return detailItems;
	}
	public void setDetailItems(List detailItems) {
		this.detailItems = detailItems;
	}

}