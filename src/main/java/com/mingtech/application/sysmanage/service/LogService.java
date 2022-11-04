package com.mingtech.application.sysmanage.service;

import java.util.Date;
import java.util.List;

import com.mingtech.application.sysmanage.domain.Log;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;

/**
 * 说明: 日志处理Service
 * 
 * @author E-mail: pengdaochang@
 * @version
 * @date 2009-2-27 上午11:36:13
 * 
 */
public interface LogService {

	/**
     * 方法说明: 分页获取所有日志
	 * @param  log  日志实体
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param  page 分页实体
	 * @return list
	 * @throws Exception
	 */
	public List queryLogList(User user,Log log,Date beginDate,Date endDate,Page page) throws Exception;
	
	/**
	 * 方法说明: 保存系统操作日志
	 * @param user 当前登录用户
	 * @param operType 操作类型-PublicStaticDefineTab.LOG_OPERTYPE_
	 * @param ip 客户端访问ip
	 * @param content 日志类容
	 * @param desc 功能描述
	 * @return
	 * @date 2009-2-27 上午11:40:49
	 */
	public void saveSysOperLogBySql(User user,String operType,String ip,String desc,String content);

}
