package com.mingtech.application.ecds.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mingtech.application.audit.domain.ApproveDto;
import com.mingtech.application.ecds.common.service.ApproveService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApproveServiceImpl extends GenericServiceImpl implements
		ApproveService{

	public List queryApproveList(String bussinessId,Page page){
		List paras = new ArrayList();
		String hql="select approve from ApproveDto as approve where approve.bussinessId=? order by approve.approveDate desc";
		paras.add(bussinessId);
		return this.find(hql,paras,page);
	}
	
	public List queryApproveList1(String bussinessId,Page page){
		List paras = new ArrayList();
		String hql="select approve from ApproveDto as approve where approve.bussinessId=? order by approve.approveDate asc";
		paras.add(bussinessId);
		return this.find(hql,paras,page);
	}

	public Class getEntityClass(){
		return ApproveDto.class;
	}

	public String getEntityName(){
		return StringUtil.getClass(ApproveDto.class);
	}
}
