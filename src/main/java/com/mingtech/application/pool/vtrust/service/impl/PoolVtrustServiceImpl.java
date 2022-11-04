package com.mingtech.application.pool.vtrust.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("poolVtrustService")
public class PoolVtrustServiceImpl extends GenericServiceImpl implements PoolVtrustService{
    

	@Override
	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}

	@Override
	public Class<PoolVtrust> getEntityClass() {
		return PoolVtrust.class;
	}
	@Override
	public void deleteVtrustInfoBySource(String source) throws Exception {
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		keyList.add("vtSource");
		valueList.add(source);
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		
		String hql = "delete from PoolVtrust where vtSource in(:vtSource) "; // 删除关联的节点信息
		this.dao.txBatchUpdate(hql, keyArray, valueList.toArray());
	}


	@Override
	public List<PoolVtrust> queryPoolVtrust(String bpsNo,String vtNb, String vtEntpNo, String vtLogo,
			String payType) throws Exception {
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		
		StringBuffer hql = new StringBuffer(" select pv from PoolVtrust as pv  where 1=1" );
		//票据池编号
		if(bpsNo!=null&&!bpsNo.equals("")){
			hql.append(" and pv.bpsNo=:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(bpsNo);	
		}
		//票号+
		
		if(vtNb!=null&&!vtNb.equals("")){
			hql.append(" and pv.vtNb=:vtNb");
			paramName.add("vtNb");
			paramValue.add(vtNb);	
		}
		//核心客户号
		if(vtEntpNo!=null&&!vtEntpNo.equals("")){
			hql.append(" and pv.vtEntpNo=:vtEntpNo");
			paramName.add("vtEntpNo");
			paramValue.add(vtEntpNo);	
		}
		//托管标记
		if(vtLogo!=null&&!vtLogo.equals("")){
			hql.append(" and pv.vtLogo=:vtLogo");
			paramName.add("vtLogo");
			paramValue.add(vtLogo);	
		}
		//应收应付标记
		if(payType!=null&&!payType.equals("")){
			hql.append(" and pv.payType=:payType");
			paramName.add("payType");
			paramValue.add(payType);	
		}
		
		hql.append(" and pv.vtStatus=:vtStatus");
		paramName.add("vtStatus");
		paramValue.add(PoolComm.DS_00);	

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PoolVtrust> list = this.find(hql.toString(),paramNames,paramValues);
		if(list != null && list.size() >0 ){
			return list;
		}
		return null;
	}

	@Override
	public PoolVtrust queryPoolVtrust(PoolVtrustBeanQuery queryBean) throws Exception {
		
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		
		StringBuffer hql = new StringBuffer(" select pv from PoolVtrust as pv  where 1=1" );

		//票号+
		if(StringUtil.isNotBlank(queryBean.getVtNb())){
			hql.append(" and pv.vtNb=:vtNb");
			paramName.add("vtNb");
			paramValue.add(queryBean.getVtNb());	
		}
		
		//入池批次号
		if(StringUtil.isNotBlank(queryBean.getInBatchNo())){
			hql.append(" and pv.inBatchNo=:inBatchNo");
			paramName.add("inBatchNo");
			paramValue.add(queryBean.getInBatchNo());	
		}
		
		//风险等级
		if(StringUtil.isNotBlank(queryBean.getRiskLevel())){
			hql.append(" and pv.rickLevel=:rickLevel");
			paramName.add("rickLevel");
			paramValue.add(queryBean.getRiskLevel());	
		}
		//占用额度系统标记
		if(StringUtil.isNotBlank(queryBean.getBtFlag())){
			hql.append(" and pv.BtFlag=:BtFlag ");
			paramName.add("BtFlag");
			paramValue.add(queryBean.getBtFlag());
		}
				

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PoolVtrust> list = this.find(hql.toString(),paramNames,paramValues);
		if(list != null && list.size() >0 ){
			PoolVtrust pv =  list.get(0);
			return pv;
		}
		return null;
	}

	@Override
	public List<PoolVtrust> queryPoolVtrustList(PoolVtrustBeanQuery queryBean)
			throws Exception {
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		
		StringBuffer hql = new StringBuffer(" select pv from PoolVtrust as pv  where 1=1" );

		//票号+
		if(StringUtil.isNotBlank(queryBean.getVtNb())){
			hql.append(" and pv.vtNb=:vtNb");
			paramName.add("vtNb");
			paramValue.add(queryBean.getVtNb());	
		}
		
		//入池批次号
		if(StringUtil.isNotBlank(queryBean.getInBatchNo())){
			hql.append(" and pv.inBatchNo=:inBatchNo");
			paramName.add("inBatchNo");
			paramValue.add(queryBean.getInBatchNo());	
		}
		
		//风险等级
		if(StringUtil.isNotBlank(queryBean.getRiskLevel())){
			hql.append(" and pv.rickLevel=:rickLevel");
			paramName.add("rickLevel");
			paramValue.add(queryBean.getRiskLevel());	
		}
		//应收应付标记
		if(StringUtil.isNotBlank(queryBean.getPayType())){
			hql.append(" and pv.payType=:payType ");
			paramName.add("payType");
			paramValue.add(queryBean.getPayType());
		}
		//在池状态
		if(StringUtil.isNotBlank(queryBean.getVtStatus())){
			hql.append(" and pv.vtStatus=:vtStatus ");
			paramName.add("vtStatus");
			paramValue.add(queryBean.getVtStatus());
		}
		//占用额度系统标记
		if(StringUtil.isNotBlank(queryBean.getBtFlag())){
			hql.append(" and pv.BtFlag=:BtFlag ");
			paramName.add("BtFlag");
			paramValue.add(queryBean.getBtFlag());
		}
		
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PoolVtrust> list = this.find(hql.toString(),paramNames,paramValues);
		if(list != null && list.size() >0 ){
			return list;
		}
		return null;
	}


}
