package com.mingtech.application.sysmanage.service.impl;

import java.util.List;
import java.util.Map;

import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.framework.common.util.StringUtil;

public class ResourceFactoryManager {
	

	/**
	 * 将资源列表中Action名称不为空的资源以ActionName为key注入Map
	 */
	public static void putResource(Map map, List resList) {
		Resource res = null;
		for (int i = 0; i < resList.size(); i++) {
			res = (Resource) resList.get(i);
			if (!StringUtil.isBlank(res.getActionName()))
				map.put(res.getActionName(), res);
		}
	}

}
