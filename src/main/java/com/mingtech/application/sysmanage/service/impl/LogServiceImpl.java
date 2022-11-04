package com.mingtech.application.sysmanage.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Log;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.LogService;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.UUID;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

public class LogServiceImpl extends GenericServiceImpl implements LogService {
	private static final Logger logger = Logger.getLogger(LogServiceImpl.class);

	public Class getEntity() {
	
		return null;
	}

	public Class getEntityClass() {
		
		return null;
	}

	public String getEntityName() {
		
		return null;
	}

	/**
     * 方法说明: 分页获取所有日志
	 * @param  log  日志实体
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @param  page 分页实体
	 * @return list
	 * @throws Exception
	 */
	public List queryLogList(User user,Log log,Date beginDate,Date endDate,Page page) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		sb.append("select log from Log as log where 1=1 ");
//		if(!"-1".equals(user.getDepartment().getId())){//非虚拟根节点 
//			sb.append(" where log.memberCode = ?  ");
//			paras.add(user.getDepartment().getPjsMemberCode());
//		}else{
//			sb.append(" where 1=1  ");
//		}
		if(StringUtils.isNotBlank(log.getDeptName())){//机构名称
			sb.append(" and log.deptName like ?");
			paras.add("%"+log.getDeptName()+"%");
		}
		if(StringUtils.isNotBlank(log.getName())){//操作员名称
			sb.append(" and log.name like ?");
			paras.add("%"+log.getName()+"%");
		}
		if(StringUtils.isNotBlank(log.getOperType())){//操作员类型
			sb.append(" and log.operType =?");
			paras.add(log.getOperType());
		}
		if(beginDate != null){
			sb.append(" and log.operTime >= ?");
			paras.add(DateUtils.getCurrentDayStartDate(beginDate));
		}
		if(endDate != null){
			sb.append(" and log.operTime <= ?");
			paras.add(DateUtils.getCurrentDayEndDate(endDate));
		}
		
		if(StringUtils.isNotBlank(log.getIp())){
			sb.append(" and log.ip like ?");
			paras.add("%"+log.getIp()+"%");
		}
		if(StringUtils.isNotBlank(log.getDesc())){
			sb.append(" and log.desc like ?");
			paras.add("%"+log.getDesc()+"%");
		}
		//增加排序功能
		sb.append(" order by log.operTime desc ");
		return find(sb.toString(), paras, page);
	}
	
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
	public void saveSysOperLogBySql(User user,String operType,String ip,String desc,String content) {
	    Department dept = user.getDepartment();
		StringBuffer sql = new StringBuffer(" insert into t_log(ID,L_MEMBER_CODE,L_DEPTNAME,");
		sql.append("L_DEPTID,L_LOGINNAME,L_NAME,L_USERID,L_OPER_TYPE,L_IP,L_SERVER_IP,");
		sql.append("L_OPERCONTENT,L_DESC,L_OPERTIME)");
		sql.append(" values('").append(UUID.randomUUID().toString()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//uuid
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(dept.getPjsMemberCode()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//会员编码
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(dept.getName()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//机构名称
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(dept.getId()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//机构id
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(user.getLoginName()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//用户登录账号
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(user.getName()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//用户名
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(user.getId()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//用户id
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(operType).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//操作类型
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ip).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//客户端ip
		try {
			InetAddress address = InetAddress.getLocalHost();
			sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(address.getHostAddress()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//服务端IP
		} catch (UnknownHostException e) {
			sql.append("'',");//服务端IP
			logger.error(ErrorCode.ERR_GET_SERVER_IP,e);
		}//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
		content = content.replaceAll("[,|\'\"]", "’");
		if(content.length() > 1500) {
			content = content.substring(0,1500);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(content).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);//操作类容
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(desc).append("',");//功能描述
		String dateTime = DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIMES3_FORMAT);
		String dbType = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.DATABASE_TYPE);
		if(StringUtils.isNotBlank(dbType) && "mysql".equalsIgnoreCase(dbType)){
			sql.append("str_to_date('").append(dateTime).append("', '%Y-%m-%d %H:%i:%s')");//操作时间
		}else{
			sql.append("to_date('").append(dateTime).append("', 'yyyy-mm-dd hh24:mi:ss')");//操作时间
		}
		sql.append(")");//功能描述
		try {
			dao.updateSQL(sql.toString());
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
		
	}

}
