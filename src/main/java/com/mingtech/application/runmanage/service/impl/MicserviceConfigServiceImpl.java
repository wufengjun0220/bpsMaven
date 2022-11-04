package com.mingtech.application.runmanage.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.mingtech.application.runmanage.domain.MicserviceConfig;
import com.mingtech.application.runmanage.service.MicserviceConfigService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 微服务配置业务实现层
 * @author limaosong
 *
 */
@Service("micServiceConfigService")
public class MicserviceConfigServiceImpl extends GenericServiceImpl implements MicserviceConfigService {

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 查询系统微服务配置页面信息
	 * @param micserviceConfig 搜索参数
	 * @param page 当前页
	 * @return
	 */
	@Override
	public List<MicserviceConfig> queryMicserviceConfig(MicserviceConfig micserviceConfig, Page page) {
		StringBuffer sb = new StringBuffer();
		List keyList = new ArrayList();
		List valueList = new ArrayList();
		sb.append(" select msc from MicserviceConfig msc where 1=1 ");
		if(StringUtils.isNotBlank(micserviceConfig.getServiceName())) {
			sb.append(" and msc.serviceName like :serviceName");
			keyList.add("serviceName");
			valueList.add("%"+micserviceConfig.getServiceName()+"%");
		}
		sb.append(" order by msc.id desc");
		String[] keyArray = (String[])keyList.toArray(new String[keyList.size()]);
		List list= this.find(sb.toString(),keyArray,valueList.toArray(),page);
		return list;
	}

	/**
	 * 编辑系统微服务配置信息
	 * @param micserviceConfig 参数
	 * @throws Exception
	 */
	@Override
	public void txEditMicserviceConfig(MicserviceConfig micserviceConfig) throws Exception {
		List<MicserviceConfig> list=this.queryMicserviceConfigList();
		
 		if(StringUtils.isNotBlank(micserviceConfig.getId())) {
 			MicserviceConfig newMicserviceConfig = this.queryMicserviceConfigById(micserviceConfig.getId());
 			if(!newMicserviceConfig.getServiceName().equals(micserviceConfig.getServiceName())) {
 				for(MicserviceConfig msc : list) {
 					if(msc.getServiceName().equals(micserviceConfig.getServiceName())) {
 						throw new Exception("微服务名称不能重复");
 					}
 				}	
 				newMicserviceConfig.setServiceName(micserviceConfig.getServiceName());
 			}
 			newMicserviceConfig.setDescription(micserviceConfig.getDescription());
 			newMicserviceConfig.setGrayUser(micserviceConfig.getGrayUser());
 			newMicserviceConfig.setGrayUrl(micserviceConfig.getGrayUrl());
		}else {
			for(MicserviceConfig msc : list) {
				if(msc.getServiceName().equals(micserviceConfig.getServiceName())) {
					throw new Exception("微服务名称不能重复");
				}
			}	
			this.txStore(micserviceConfig);
		}
		
	}

	/**
	 * 删除系统微服务配置信息
	 * @param id 主键
	 * @throws Exception
	 */
	@Override
	public void txDeleteMicserviceConfig(String id) throws Exception {
		if(StringUtils.isNotBlank(id)) {
			String[] str = id.split(",");
			for(String s : str) {
				if(StringUtils.isNotBlank(s)) {
					MicserviceConfig micserviceConfig =this.queryMicserviceConfigById(s);
					this.txDelete(micserviceConfig);
				}
			}
		}	
	}
	
	/**
	 * 通过id查询系统微服务配置信息
	 * @param id 主键
	 * @return
	 */
	public MicserviceConfig queryMicserviceConfigById(String id) {
		MicserviceConfig micserviceConfig = (MicserviceConfig) this.load(id,MicserviceConfig.class);
		return micserviceConfig;
	}

	/**
	 * 查询系统微服务配置列表信息
	 * @return
	 */
	public List<MicserviceConfig> queryMicserviceConfigList(){
		StringBuffer sb = new StringBuffer();
		sb.append(" select msc from MicserviceConfig msc ");
		List<MicserviceConfig> list=this.find(sb.toString());
		return list;
	}

	/**
	 * 分页获取当前机构及下属机构的用户
	 * @param user 查询条件
	 * @param curUser 当前登录用户
	 * @param pageInfo 分页信息
	 * @return
	 */
	@Override
	public List listUserOfConfig(User user, User curUser, Page page) {
		
		List paras = new ArrayList();
		StringBuffer hql = new StringBuffer("select user,dept.name,dept.pjsMemberCode from User  user,Department  dept where user.deptId=dept.id");
		if(!"-1".equals(curUser.getDepartment().getPjsMemberCode())){//-1为虚拟机构
			hql.append(" and dept.pjsMemberCode=?");
			paras.add(curUser.getDepartment().getPjsMemberCode());
		}
		if(null != user){
			if(StringUtils.isNotBlank(user.getDeptId())){
				hql.append(" and user.deptId =?");
				paras.add(user.getDeptId());
			}else{
				//查询当前机构及下属所有机构用户信息
				hql.append(" and dept.levelCode like ?");
				Department curDept = curUser.getDepartment();
				paras.add( curDept.getLevelCode() + "%");
			}
			if(StringUtils.isNotBlank(user.getLoginName())){
				hql.append(" and user.loginName =?");
				paras.add(user.getLoginName());
			}	
			if(StringUtils.isNotBlank(user.getName())){
				hql.append(" and user.name like ?");
				paras.add("%"+user.getName()+"%");
			}
			if(StringUtils.isNotBlank(user.getStrStatus())){
				hql.append(" and user.status =?");
				paras.add(user.getStrStatus());
			}
		}else{
			//查询当前机构及下属所有机构用户信息
			hql.append(" and dept.levelCode like ?");
			Department curDept = curUser.getDepartment();
			paras.add( curDept.getLevelCode() + "%");
		}
		
		hql.append(" and user.agentFlag =? ");
		paras.add("0");
		hql.append(" order by dept.order, user.createTime ASC");
		List list = find(hql.toString(), paras, page);
		int size = list.size();
		List userList = new ArrayList();
		for(int i=0; i<size; i++ ){
			Object[] objArr = (Object[])list.get(i);
			User tmpUser = (User)objArr[0];
			//Department tmpDept = (Department)objArr[1];
			tmpUser.setDeptNm((String)objArr[1]);
			tmpUser.setMemCode((String)objArr[2]);
			userList.add(tmpUser);
		}
		return userList;
	}
}
