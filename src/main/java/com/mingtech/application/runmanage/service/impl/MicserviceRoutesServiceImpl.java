package com.mingtech.application.runmanage.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.mingtech.application.runmanage.domain.MicserviceRoutes;
import com.mingtech.application.runmanage.service.MicserviceRoutesService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 系统微服务注册
 * @author meng
 *
 */
@Service("micserviceRoutesService")
public class MicserviceRoutesServiceImpl   extends GenericServiceImpl implements MicserviceRoutesService{

	@Override
	public String getEntityName() {
		return  StringUtil.getClass(MicserviceRoutes.class);
	}

	@Override
	public Class getEntityClass() {
		return MicserviceRoutes.class;
	}
	

	/**
	 * 查询微服务信息
	 */
	public List queryMicserviceConfigs()throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("from MicserviceConfig c  ");
		sb.append(" order by c.id desc ");
		List list = this.find(sb.toString());
		return list;
	}
	
	/**
	 * 查询所有路由配置信息
	 */
	public List queryAllMicserviceRoutes(){
		StringBuffer sb = new StringBuffer();
		sb.append("from MicserviceRoutes r  ");
		List list = this.find(sb.toString());
		return list;
	}
	
	/**
	 * 微服务注册列表
	 */
	public List queryMicserviceRoutes(MicserviceRoutes micserviceRoutes,Page page)throws Exception{

		StringBuffer sb = new StringBuffer();
		List keyList = new ArrayList(); // 要查询的字段列表
		List valueList = new ArrayList(); //查询参数值
		sb.append("select m,c.serviceName from MicserviceRoutes as m,MicserviceConfig c where m.serviceId=c.id ");
		if(StringUtils.isNotBlank(micserviceRoutes.getDescription())){
			sb.append(" and m.description like :description ");
			keyList.add("description");
			valueList.add("%"+micserviceRoutes.getDescription()+"%");
		}
		if(StringUtils.isNotBlank(micserviceRoutes.getServiceId())){
			sb.append(" and m.serviceId = :serviceId ");
			keyList.add("serviceId");
			valueList.add(micserviceRoutes.getServiceId());
		}
		if(StringUtils.isNotBlank(micserviceRoutes.getReqUrl())){
			sb.append(" and m.reqUrl like :reqUrl ");
			keyList.add("reqUrl");
			valueList.add("%"+micserviceRoutes.getReqUrl()+"%");
		}
		if(StringUtils.isNotBlank(micserviceRoutes.getStatus())){
			sb.append(" and m.status = :status ");
			keyList.add("status");
			valueList.add(micserviceRoutes.getStatus());
		}
		
		if(StringUtils.isNotBlank(micserviceRoutes.getApiType())){
			sb.append(" and m.apiType = :apiType ");
			keyList.add("apiType");
			valueList.add(micserviceRoutes.getApiType());
		}
		
		if(StringUtils.isNotBlank(micserviceRoutes.getRegOperLog())){
			sb.append(" and m.regOperLog = :regOperLog ");
			keyList.add("regOperLog");
			valueList.add(micserviceRoutes.getRegOperLog());
		}
		
		sb.append(" order by m.updateDate desc ");
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray(), page);
	    int size = list.size();
	    List results = new ArrayList();
	    for(int i=0; i < size; i++){
	    	Object[] objArr = (Object[])list.get(i);
	    	MicserviceRoutes micRoute = (MicserviceRoutes)objArr[0]; 
	    	micRoute.setServiceName((String)objArr[1]);
	    	results.add(micRoute);
	    }
		return results;
	}

	@Override
	public void txMicserviceRoutesAdd(MicserviceRoutes micserviceRoutes) throws Exception {
		
		if(StringUtils.isEmpty(micserviceRoutes.getId())){
			micserviceRoutes.setCreateDate(new Date());
			micserviceRoutes.setUpdateDate(new Date());
			this.txStore(micserviceRoutes);
		}else{
			
			MicserviceRoutes routes = (MicserviceRoutes) this.load(micserviceRoutes.getId(),MicserviceRoutes.class);
			
			routes.setServiceId(micserviceRoutes.getServiceId());//外键微服务ID
			routes.setReqUrl(micserviceRoutes.getReqUrl());//请求地址
			routes.setForwardUrl(micserviceRoutes.getForwardUrl());//重定向请求地址
			routes.setDescription(micserviceRoutes.getDescription());//描述
			routes.setStatus(micserviceRoutes.getStatus());//状态:1启用、0停用
			routes.setApiType(micserviceRoutes.getApiType());//接口类型
			routes.setRegOperLog(micserviceRoutes.getRegOperLog());//是否记录操作日志
			routes.setOpenGray(micserviceRoutes.getOpenGray());//灰度调用
			routes.setUpdateDate(new Date());
			this.txStore(routes);
		}
	}


	@Override
	public void txMicserviceRoutesDel(MicserviceRoutes micserviceRoutes) throws Exception {
		this.txDelete(micserviceRoutes);
	}

	@Override
	public boolean getMicserviceRoutesByReqUrl(MicserviceRoutes micserviceRoutes) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keyList = new ArrayList(); // 要查询的字段列表
		List valueList = new ArrayList(); //查询参数值
		sb.append("select count(m) from MicserviceRoutes as m where 1=1 ");
		
		if(StringUtils.isNotBlank(micserviceRoutes.getReqUrl())){
			sb.append(" and m.reqUrl = :reqUrl ");
			keyList.add("reqUrl");
			valueList.add(micserviceRoutes.getReqUrl());
		}else{
			throw new Exception("reqUrl为空，请检验reqUrl");
		}
		
		if(StringUtils.isNotBlank(micserviceRoutes.getId())){//id为空则是新增
			sb.append(" and m.id is not :id ");
			keyList.add("id");
			valueList.add(micserviceRoutes.getId());
		}
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(sb.toString(), keyArray, valueList.toArray(), null);
		if(list !=null && list.size()>0){
			Long count = (Long) list.get(0);
			if(count == 0){
				return true;
			}else return false;
		}else return true;
	}
	
}
