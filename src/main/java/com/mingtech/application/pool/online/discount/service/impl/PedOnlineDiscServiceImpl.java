package com.mingtech.application.pool.online.discount.service.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.online.discount.service.PedOnlineDiscService;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 汉口银行在线贴现服务实现
 * @author Ju Nana
 * @version v1.0
 * @date 2022-3-10
 * @copyright 北明明润（北京）科技有限责任公司
 */

@Service("pedOnlineDiscService")
public class PedOnlineDiscServiceImpl extends GenericServiceImpl implements PedOnlineDiscService {
	private static final Logger logger = Logger.getLogger(PedOnlineDiscServiceImpl.class);
	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public Class getEntityClass() {
		return null;
	}
}
