package com.mingtech.application.runmanage.service;

import java.util.Date;

import com.mingtech.application.runmanage.domain.RunState;
import com.mingtech.framework.core.service.GenericService;

/**
 * 运行状态类接口定义
 * @author chenwei
 *
 */
public interface RunStateService extends GenericService {
	
	/**
	 * 获取系统当前运行状态对象
	 * @param 
	 * @author chenwei
	 * @return 当前运行状态对象
	 */
	public RunState getSysRunState();
	
	/**
	 * 从数据库中获取系统当前运行状态对象
	 * @param 
	 * @return 当前运行状态对象
	 */
	public RunState getSysRunStateFromDb();
	
	/**
	 * 获取系统营业时间
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-12上午10:43:12
	 */
	public Date getWorkDateTime();
	
}