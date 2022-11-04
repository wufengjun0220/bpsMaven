/**
 * 
 */
package com.mingtech.application.pool.base.service.impl;

import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * @author wbyecheng
 * 
 *         池基础服务实现类
 * 
 */
public abstract class AbstractPoolBaseServiceImpl<T, E> extends
		GenericServiceImpl  {
	
	
	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}
}
