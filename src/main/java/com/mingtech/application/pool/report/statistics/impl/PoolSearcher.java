package com.mingtech.application.pool.report.statistics.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
import com.mingtech.application.report.domain.SearchResult;
import com.mingtech.application.report.statistics.ISearcher;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-12 下午03:26:19
 * @描述: [AcceptionAmountByYearSearcher]承兑年累计发生额查询Service实现(包括电票的银票和商票)
 */
@Service("iSearcher")
public class PoolSearcher extends GenericServiceImpl implements
		ISearcher{

	//private AcceptionPropertyCollecter acceptionPropertyCollecter;
	
	public List seracher(Date startDate, Date endDate, Department dept, String bankLevel){
		
		List keyList = new ArrayList(); // 要查询的字段列表
		List valueList = new ArrayList(); // 要查询的值列表
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct(bi.billinfoId) from BusiAcctFlow bfd, BtBillInfo bi where bfd.prodId = :SProductId and bfd.billInfoId = bi.billinfoId and bi.SBillMedia = :SBillMedia ");
		sb.append("and ((bi.SBillType = :SBillType and bi.SIfDirectAccep = :SIfDirectAccep) or (bi.SBillType = :SBillType2)) ");
		if(dept != null){
			if(StringUtils.equals(bankLevel, "3")){
				sb.append("and bi.SAcceptorBankCode = :SAcceptorBankCode ");
			}else if(StringUtils.equals(bankLevel, "2")){
				sb.append("and bi.SAcceptorBankCode in (:SAcceptorBankCode) ");
			}
		}
		
		keyList.add("SProductId");
		valueList.add(PublicStaticDefineTab.ACCEPTION_PRODUCTID); // 电票承兑产品类型ID
		
		keyList.add("SBillMedia");
		valueList.add(PublicStaticDefineTab.BILL_MEDIA_ELECTRONICAL_CODE); // 电票
		
//		keyList.add("SECDSStatus");
//		List statusList = new ArrayList(); // 要查询的票据状态列表
//		statusList.add(EdraftStatus.Finish_Abandon_Code); // 结束已作废
//		valueList.add(statusList);
		
		keyList.add("SIfDirectAccep");
		valueList.add(PublicStaticDefineTab.VALUE_YES); // 我行承兑
		
		keyList.add("SBillType");
		valueList.add(PublicStaticDefineTab.BILL_TYPE_BANK); // 票据类型    银承
		
		keyList.add("SBillType2");
		valueList.add(PublicStaticDefineTab.BILL_TYPE_BUSI); // 票据类型    银承
		
		if(dept != null){
			keyList.add("SAcceptorBankCode");
			if(StringUtils.equals(bankLevel, "3")){
				valueList.add(dept.getBankNumber()); // 承兑行行号
			}else if(StringUtils.equals(bankLevel, "2")){
				String query = "select dept.bankNumber from Department dept where dept.parent = ? ";
				List params = new ArrayList();
				params.add(dept);
				List tmp =  this.find(query, params);
				tmp.add(dept.getBankNumber());
				valueList.add(tmp); // 承兑行行号列表
			}
		}
		
		if(startDate != null && endDate != null){
			sb.append("and bi.SAcceptorDt >= :startDate and bi.SAcceptorDt <= :endDate ");
			keyList.add("startDate");
			valueList.add(startDate); // 开始日期
			keyList.add("endDate");
			valueList.add(endDate); // 结束日期
		}else if(startDate != null){
			sb.append("and bi.SAcceptorDt >= :startDate ");
			keyList.add("startDate");
			valueList.add(startDate); // 开始日期
		}else if(endDate != null){
			sb.append("and bi.SAcceptorDt <= :endDate ");
			keyList.add("endDate");
			valueList.add(endDate); // 结束日期
		}
		List list = this.find(sb.toString(), (String[]) keyList.toArray(new String[keyList.size()]), valueList.toArray());
		List searchResultList = new ArrayList();
		if(!list.isEmpty()){
			SearchResult sr = null;
			int size = list.size();
			for(int i = 0; i < size; i++){
				String id = (String) list.get(i);
				BtBillInfo bi =  (BtBillInfo) this.load(id, BtBillInfo.class);
				sr = new SearchResult();
				sr.setIdNb(bi.getSBillNo());
				sr.setDrwrNm(bi.getSIssuerName());
				sr.setAccptrNm(bi.getSAcceptor());
				sr.setIsseAmt(bi.getFBillAmount());
				sr.setIsseDt(bi.getDIssueDt());
				sr.setDueDt(bi.getDDueDt());
				sr.setAcceptorBankCode(bi.getSAcceptorBankCode());
				if(dept != null){
					sr.setDeptId(dept.getId());
				}
				//acceptionPropertyCollecter.setPropertyObjcets(sr);
				searchResultList.add(sr);
			}
		}
		return searchResultList;
		
	}

	
	public Class getEntityClass(){
		return null;
	}

	public String getEntityName(){
		return null;
	}
	
}
