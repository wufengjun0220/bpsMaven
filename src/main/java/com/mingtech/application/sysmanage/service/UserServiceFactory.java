package com.mingtech.application.sysmanage.service;

import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * 用户  服务工厂
 * @author Administrator
 *
 */
public class UserServiceFactory {
	public static UserService getUserService(){
		return (UserService) SpringContextUtil.getBean("userService");
	}
}
