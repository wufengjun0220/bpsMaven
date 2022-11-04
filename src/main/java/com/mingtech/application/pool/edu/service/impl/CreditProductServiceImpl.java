package com.mingtech.application.pool.edu.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.edu.domain.CreditPedBean;
import com.mingtech.application.pool.edu.service.CreditProductService;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: 张永超
 * @日期: Aug 20, 2010 10:20:45 AM
 * @描述: [CreditProductServiceImpl]信贷资产业务实现类
 */
@Service("creditProductService")
public class CreditProductServiceImpl extends GenericServiceImpl implements
		CreditProductService {
	private static final Logger logger = Logger.getLogger(CreditProductServiceImpl.class);

	@Override
	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}

	@Override
	public Class getEntityClass() {
		return null;
	}

	//查询保贴人明细
		public List loadPosterCountDetail(String acceptorOrg,Page page) throws Exception{
			
			List resultList = new ArrayList();
			List list = new ArrayList();
			List<String> paramName = new ArrayList<String>();// 名称
			List<String> paramValue = new ArrayList<String>();// 值
			String hql= "select b.bpsNo,sum(b.billAmt),b.bpsName,b.guarantDiscName,b.isGroup,b.creditObjType,b.guarantDiscNo from PedGuaranteeCredit b where 1=1 ";
			if(StringUtils.isNotEmpty(acceptorOrg)){
				hql = hql+" and b.guarantDiscName = :guarantDiscName  ";
				paramName.add("guarantDiscName");
				paramValue.add(acceptorOrg);	
			}
			hql = hql+" and b.status = :status and b.creditObjType = '2' ";
			paramName.add("status");
			paramValue.add(PoolComm.SP_01);//占用
			hql = hql+" group by b.bpsNo,b.bpsName,b.guarantDiscName,b.isGroup,b.creditObjType,b.guarantDiscNo  ";
			hql = hql+" order by b.bpsNo  ";
			String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
			Object paramValues[] = paramValue.toArray();
			resultList = this.find(hql,paramNames,paramValues,page);
			if(resultList!=null && resultList.size()>0){
				for(int i =0; i<resultList.size();i++){
//					Object Object=resultList.get(i);
					Object[] obj = (Object[]) resultList.get(i);
					CreditPedBean  bean=new  CreditPedBean();
					if (obj[0] != null) {
						bean.setBpsNo(obj[0].toString());
					}
					if(obj[1] != null){
						bean.setBillAmtCount(Double.valueOf(obj[1].toString()));
					}
					/*if(obj[2] != null){
						bean.setBillAmtCount(Double.valueOf(obj[2].toString()));
					}*/
					if (obj[2] != null) {
						bean.setBpsName(obj[2].toString());
					}
					if (obj[3] != null) {
						bean.setGuarantDiscName(obj[3].toString());
					}
					if (obj[4] != null) {
						bean.setIsGroup(obj[4].toString());
					}
					if (obj[5] != null) {
						bean.setCreditObjType(obj[5].toString());
					}
					if (obj[6] != null) {
						bean.setGuarantDiscNo(obj[6].toString());
					}
					list.add(bean);
				}
				return list;
			}
			return null;
		}


		// 票据池占用保贴额度查询明细
		public List loadPoolPasteDetail(String bpsNo,Page page) throws Exception {
			List resultList = new ArrayList();
			List list = new ArrayList();
			List<String> paramName = new ArrayList<String>();// 名称
			List<String> paramValue = new ArrayList<String>();// 值
			String hql= "select b.guarantDiscName,sum(b.billAmt),b.bpsNo,b.bpsName,b.creditObjType,b.guarantDiscNo from PedGuaranteeCredit b where 1=1 ";
			if(StringUtils.isNotEmpty(bpsNo)){
				hql = hql+" and b.bpsNo = :bpsNo  ";
				paramName.add("bpsNo");
				paramValue.add(bpsNo);	
			}
			hql = hql+" and b.status = :status and b.creditObjType = '2' ";
			paramName.add("status");
			paramValue.add(PoolComm.SP_01);//占用
			hql = hql+" group by b.guarantDiscName,b.bpsNo,b.bpsName,b.creditObjType,b.guarantDiscNo  ";
			hql = hql+" order by b.guarantDiscNo";
			String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
			Object paramValues[] = paramValue.toArray();
			resultList = this.find(hql,paramNames,paramValues);
			if(resultList!=null && resultList.size()>0){
				for(int i =0; i<resultList.size();i++){
//					Object Object=resultList.get(i);
					Object[] obj = (Object[]) resultList.get(i);
					CreditPedBean  bean=new  CreditPedBean();
					if (obj[0] != null) {
						bean.setGuarantDiscName(obj[0].toString());
					}
					if(obj[1] != null){
						bean.setBillAmtCount(Double.valueOf(obj[1].toString()));
					}
					/*if(obj[2] != null){
						bean.setBillAmtCount(Double.valueOf(obj[2].toString()));
					}*/
					if (obj[2] != null) {
						bean.setBpsNo(obj[2].toString());
					}
					if (obj[3] != null) {
						bean.setBpsName(obj[3].toString());
					}
					if (obj[4] != null) {
						bean.setCreditObjType(obj[4].toString());
					}
					if (obj[5] != null) {
						bean.setGuarantDiscNo(obj[5].toString());
					}
					list.add(bean);
				}
				return list;
			}
			return null;
		}
		
}
