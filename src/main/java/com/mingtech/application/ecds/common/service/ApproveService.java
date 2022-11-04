package com.mingtech.application.ecds.common.service;

import java.util.List;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface ApproveService extends GenericService{

	/**
	 * <p>方法名称: queryApproveList|描述:根据业务主键ID查询该业务所有批示信息对象 </p>
	 * @param bussinessId 业务主键ID
	 * @return
	 */
	public List queryApproveList(String bussinessId,Page page);
	
	public List queryApproveList1(String bussinessId,Page page);
}
