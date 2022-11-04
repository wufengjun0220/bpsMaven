package com.mingtech.application.ecds.common.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.dao.DataAccessException;
import com.mingtech.application.ecds.common.domain.FlowNoDto;
import com.mingtech.application.ecds.common.service.FlowNumberService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


public class FlowNumberServiceImpl extends GenericServiceImpl implements
		FlowNumberService{
	private Logger logger = Logger.getLogger(FlowNumberServiceImpl.class);
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return FlowNoDto.class;
	}

	public String getEntityName() {
		// TODO Auto-generated method stub
		return StringUtil.getClass(FlowNoDto.class);
	}

	/**
	 * 流水号生成器20091017于飞修改增加了事物并发引起的流水号生成相同的问题，使用了LockMode.UPGRADE属性，前提是必须使用
	 */
	public synchronized String txGetFlowNo() throws Exception {
		/* 设置前辍 */
		String prefix="";
		/* 设置后续位数 */
		int length = 5;
		String fSeq = "0";
		StringBuffer flowNo = new StringBuffer(prefix);
		/* 首先获得当前系统日期 */
		String curDate = DateUtils.dtuGetCurDatTimStr().substring(0, 8);
		flowNo.append(curDate);
		FlowNoDto dto=null;
		try{
		/* 根据系统日期查询序列 */
		dto = (FlowNoDto) this.load(curDate, FlowNoDto.class,LockMode.UPGRADE);
		fSeq = Integer.parseInt(dto.getSSeq()) + 1 + "";
		}catch(DataAccessException e){
		   dto = new FlowNoDto();
		   dto.setFlowId(curDate);
		}
		if (fSeq.length() < length) {
			for (int i = 0; i < length - fSeq.length(); i++) {
				flowNo.append("0");
			}
			flowNo.append(fSeq);
			/* 保存并更新序列对象 */
			dto.setSDate(curDate);
			dto.setSSeq(fSeq);
			this.txStore(dto);
			logger.info("FlowNumberServiceImpl 生成流水号为~~~~~~~~~~~~~~~~~~~"+flowNo.toString());
			return flowNo.toString();
		} else {
			throw new Exception("错误：序号长度超过规定上限！");
		}
	}

	/**
	 * 根据日期查询对应序号
	 */
	public FlowNoDto getSeqNoByCurDate(String curDate) throws Exception {
		List param = new ArrayList();
		String hql = "select dto from FlowNoDto as dto where dto.SDate=?";
		param.add(curDate);
		List list = this.find(hql, param);
		FlowNoDto dto = list != null && list.size() > 0 ? ((FlowNoDto) list.get(0)):new FlowNoDto();
		return dto;
	}
}
