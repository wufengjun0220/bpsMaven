package com.mingtech.application.ecds.draftcollection.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
import com.mingtech.application.ecds.draftcollection.domain.CollectionReceiveDto;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.bank.bbsp.service.impl.PoolEcdsServiceImpl;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.infomanage.domain.AccountDto;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.runmanage.service.RunStateService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者：liuweijun
 * @日期：Jun 2, 2009 11:56:51 AM
 * @描述：[ConsignServiceImpl]委托收款接口实现
 */
@Service
public class ConsignServiceImpl extends GenericServiceImpl implements
		ConsignService {
	private static final Logger logger = Logger.getLogger(ConsignServiceImpl.class);
	private RunStateService runStateService;
	private DepartmentService departmentService;
	@Autowired
	private PoolEcdsServiceImpl ecdsServiceImpl;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private DraftPoolInService draftPoolInService;


	public List queryCollSendByIds(String ids)throws Exception{
		StringBuffer hql = new StringBuffer(" select dto,dept from CollectionSendDto dto,Department dept where  dto.SBranchId=dept.id ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		 
		 
		hql.append(" and dto.collectionSendId in ( :collectionSendId )");
		parasName.add("collectionSendId");
		String[] split = ids.split(",");
		List idsList = new ArrayList();
		Collections.addAll(idsList, split);
		parasValue.add(idsList);
		 
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, null);
		return list;
		
	}
	
	/**
	 * <p> 方法名称: queryBillInfoList|描述: 查询可发起提示付款的票据信息</p>
	 * @param status 票据状态 
	 * @param bankNums 票据持有人大额行号 
	 * @param customer 客户信息
	 * @param account 客户账户
	 * @param user 当前用户
	 * @param billInfo 大票信息
	 * @param queryBean 组合查询
	 * @param page 分页对象
	 * @return list
	 * @throws Exception
	 */
	public List queryBillInfoList(List status,List bankNums,CustomerDto customer ,AccountDto account,User user,BtBillInfo billInfo,QueryBean querybean,Page page)throws Exception{
		 if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" from BtBillInfo as dto where 1=1");
		 List parasValue = new ArrayList();//查询条件值
 		 List parasName = new ArrayList();//查询条件名
 		if(null != bankNums){//当前用户不为空
 	  		if(bankNums.size() == 1){
 	  			hql.append(" and dto.SMbfeBankCode = :bankNum");
 	  			parasName.add("bankNum");
 	  			parasValue.add(bankNums.get(0));
 	  		}else if(bankNums.size()>1){
 	  			hql.append(" and dto.SMbfeBankCode in(:bankList)");
 	  			parasName.add("bankList");
 	 			parasValue.add(bankNums);
 	  		}	
 		 }
 		 if(null != status){//票据状态
 			if(status.size() == 1){
 	  			hql.append(" and dto.SECDSStatus = :statu");
 	  			parasName.add("statu");
 	  			parasValue.add(status.get(0));
 	  		}else if(status.size()>1){
 	  			hql.append(" and dto.SECDSStatus in(:billStatus)");
 	  			parasName.add("billStatus");
 	 			parasValue.add(status);
 	  		}	 
 		 }
 		 if(customer != null){//客户组织机构代码SOwnerOrgCode
 			 if(customer.getSOrgCode() != null && customer.getSOrgCode().length() > 0){
 				hql.append(" and dto.SOwnerOrgCode = :SOwnerOrgCode");
 	  			parasName.add("SOwnerOrgCode");
 	  			parasValue.add(customer.getSOrgCode());
 			 }
 		 }
 		 if(account != null){
 			 if(account.getSAccountNo() != null && account.getSAccountNo().length() > 0){
 				hql.append(" and dto.SOwnerAcctId = :SOwnerAcctId");
 	  			parasName.add("SOwnerAcctId");
 	  			parasValue.add(account.getSAccountNo()); 
 			 }
 		 }else{
 			hql.append(" and dto.SOwnerAcctId = :SOwnerAcctId");
	  		parasName.add("SOwnerAcctId");
	  		parasValue.add("0"); 
 		 }
 		 
 		this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.DDueDt desc,dto.billinfoId");//默认按到票据到期日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}
	
	/**
	 * <p> 方法名称: getQureyHql|描述: 获得组合查询和高级查询的hql</p>
	 * @param hql 查询hql语句
	 * @param dtoName 查询对象别名
	 * @param queryBean 组合查询条件对象
	 * @param parasName 高级查询条件名称
	 * @param parasValue 高级查询条件值
	 * @throws Exception
	 */
	private void getQureyHql(final StringBuffer hql, String dtoName,QueryBean queryBean,List parasName,List parasValue){
		if(queryBean == null ) return;
		//组合查询
		if(queryBean.getBillNo()!= null && queryBean.getBillNo().length()>0){ //票据号码
			/*注意票号如果修改为模糊查询会影响 合同和发票的批量导入功能，应为哪里涉及到安票号查询贴现买入明细，也调用的这里txReadeXslInfo*/
			hql.append(" and ").append(dtoName).append(".SBillNo=:SBillNo");
			parasName.add("SBillNo");
			parasValue.add(queryBean.getBillNo());
		}
		
		if(queryBean.getBillType()!=null && queryBean.getBillType().length()>0){ //票据类型
			hql.append(" and ").append(dtoName).append(".SBillType=:SBillType");
			parasName.add("SBillType");
			parasValue.add(queryBean.getBillType());
		}
		
		if(null != queryBean.getStartDate()){// 判断起始日期
			hql.append(" and ").append(dtoName).append(".DDueDt >=:startDate");
			parasName.add("startDate");
			parasValue.add(queryBean.getStartDate());
		}
		if(null != queryBean.getEndDate()){// 判断结束日期
			hql.append(" and ").append(dtoName).append(".DDueDt <=:endDate");
			parasName.add("endDate");
			parasValue.add(queryBean.getEndDate());
		}
		if(queryBean.getAcceptorBankCode()!=null && queryBean.getAcceptorBankCode().length()>0){ //承兑行号
			hql.append(" and ").append(dtoName).append(".SAcceptorBankCode=:acceptorBankCode");
			parasName.add("acceptorBankCode");
			parasValue.add(queryBean.getAcceptorBankCode());
		}
		if(queryBean.getAcceptAcctSvcr()!=null && queryBean.getAcceptAcctSvcr().length()>0){ //提示付款承兑方行号
			hql.append(" and ").append(dtoName).append(".acceptAcctSvcr=:acceptAcctSvcr");
			parasName.add("acceptAcctSvcr");
			parasValue.add(queryBean.getAcceptAcctSvcr());
		}
		if(queryBean.getCollAcctSvcr()!=null && queryBean.getCollAcctSvcr().length()>0){ //提示付款人行号
			hql.append(" and ").append(dtoName).append(".collAcctSvcr=:collAcctSvcr");
			parasName.add("collAcctSvcr");
			parasValue.add(queryBean.getCollAcctSvcr());
		}
		
		//高级查询
		if(queryBean.getQueryParam()!=null && queryBean.getQueryParam().length()>0){
			hql.append(queryBean.getQueryParam());
		}
	}
	
	/**
	 * <p>方法名称: queryBillInfoByIds|描述: 根据大票id获得所选票据信息 </p>
	 * @param ids 大票ids(格式id,id,id)
	 * @param page 分页对象
	 * @return list
	 * @throws Exception
	 */
	public List queryBillInfoByIds(String ids,Page page) throws Exception{
		if(page == null){
			page = new Page();
			page.setPageSize(Integer.MAX_VALUE);
		}
		List list = StringUtil.splitList(ids, ",");
		Object[] parasValue = new Object[] {list};
		String[] parasName = new String[] {"ids"};
		String hql = "from BtBillInfo as btBillInfo where btBillInfo.billinfoId in (:ids)";
		List resultList = this.find(hql, parasName, parasValue, page);
		return resultList;
	}
	
	/**
	 * <p> 方法名称: queryCollSendInfoList|描述: 查询提示付款申请明细信息</p>
	 * @param status 票据状态 
	 * @param bankNums 提示付款人大额行号 
	 * @param customer 客户信息
	 * @param account 客户账户
	 * @param user 当前用户
	 * @param collSendDto 提示付款申请明细信息
	 * @param queryBean 组合查询
	 * @param page 分页对象
	 * @return list
	 * @throws Exception
	 */
	public List queryCollSendInfoList(List status,List bankNums,CustomerDto customer ,AccountDto account,User user,CollectionSendDto collSendDto,QueryBean querybean,Page page)throws Exception{
		 if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" from CollectionSendDto as dto where 1=1");
		 List parasValue = new ArrayList();//查询条件值
 		 List parasName = new ArrayList();//查询条件名
 		if(null != bankNums){//当前用户不为空
 	  		if(bankNums.size() == 1){
 	  			hql.append(" and dto.collAcctSvcr = :bankNum");
 	  			parasName.add("bankNum");
 	  			parasValue.add(bankNums.get(0));
 	  		}else if(bankNums.size()>1){
 	  			hql.append(" and dto.collAcctSvcr in(:bankList)");
 	  			parasName.add("bankList");
 	 			parasValue.add(bankNums);
 	  		}	
 		 }
 		 if(null != status){//票据状态
 			if(status.size() == 1){
 	  			hql.append(" and dto.SBillStatus = :statu");
 	  			parasName.add("statu");
 	  			parasValue.add(status.get(0));
 	  		}else if(status.size()>1){
 	  			hql.append(" and dto.SBillStatus in(:SBillStatus)");
 	  			parasName.add("SBillStatus");
 	 			parasValue.add(status);
 	  		}	 
 		 }
 		 if(customer != null){//客户组织机构代码SOwnerOrgCode
 			 if(customer.getSOrgCode() != null && customer.getSOrgCode().length() > 0){
 				hql.append(" and dto.collCmonId = :collCmonId");
 	  			parasName.add("collCmonId");
 	  			parasValue.add(customer.getSOrgCode());
 			 }
 		 }
 		 if(account != null){
 			 if(account.getSAccountNo() != null && account.getSAccountNo().length() > 0){
 				hql.append(" and dto.collAcct = :SOwnerAcctId");
 	  			parasName.add("SOwnerAcctId");
 	  			parasValue.add(account.getSAccountNo()); 
 			 }
 		 }else{
 			hql.append(" and dto.collAcct = :SOwnerAcctId");
	  		parasName.add("SOwnerAcctId");
	  		parasValue.add("0"); 
 		 }
 		 
 		this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.applDt desc,dto.collectionSendId");//默认提示付款申请日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}
	
	/**
	 * <p>方法名称: queryCollectionSendInfoByIds|描述: 根据提示付款申请明细id获得所选票据信息 </p>
	 * @param ids 大票ids(格式id,id,id)
	 * @param page 分页对象
	 * @return list
	 * @throws Exception
	 */
	public List queryCollectionSendInfoByIds(String ids,Page page) throws Exception{
		if(page == null){
			page = new Page();
			page.setPageSize(Integer.MAX_VALUE);
		}
		List list = StringUtil.splitList(ids, ",");
		Object[] parasValue = new Object[] {list};
		String[] parasName = new String[] {"ids"};
		String hql = "from CollectionSendDto as dto where dto.collectionSendId in (:ids)";
		List resultList = this.find(hql, parasName, parasValue, page);
		return resultList;
	}
	
	/**
	 * <p>方法名称: queryCollectionReceiveDtoById|描述: 根据提示付款签收明细id获得签收明细对象 </p>
	 * @param id签收明细id
	 * @throws Exception
	 */
	public CollectionReceiveDto queryCollectionReceiveDtoById(String id)throws Exception{
		 return (CollectionReceiveDto)dao.load(CollectionReceiveDto.class, id);
	}
	/**
	 * 收到提示付款  公共查询  
	 * @param querybean
	 * @param status
	 * @param user
	 * @param page
	 * @return 
	 * @throws Exception
	 */
	public List queryCollReceiveForAll(QueryBean querybean,List status,User user,Page page)throws Exception{
		if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" select dto from CollectionReceiveDto as dto,Department dept where 1=1 and dto.SBranchId=dept.id ");
		 List parasValue = new ArrayList();//查询条件值
		 List parasName = new ArrayList();//查询条件名
//		 hql.append(" and dto.SBranchId = :SBranchId");
//		 parasName.add("SBranchId");
//		 parasValue.add(user.getDepartment().getId());
		
		 if (StringUtils.isEmpty(querybean.getDeptlevelCode())) { // 查询业务机构
			 hql.append(" and dept.levelCode like:levelCode");
				parasName.add("levelCode");
				parasValue.add(user.getDepartment().getLevelCode()+ "%");
		} else {
			hql.append(" and dept.levelCode =:levelCode");
			parasName.add("levelCode");
			parasValue.add(querybean.getDeptlevelCode());
		}
		 
		 if(null != status){//票据状态
			if(status.size() == 1){
	  			hql.append(" and dto.SBillStatus = :statu");
	  			parasName.add("statu");
	  			parasValue.add(status.get(0));
	  		}else if(status.size()>1){
	  			hql.append(" and dto.SBillStatus in(:SBillStatus)");
	  			parasName.add("SBillStatus");
	 			parasValue.add(status);
	  		}	 
		 }
		 if(querybean.getApplDtStart()!=null){
			 hql.append(" and dto.applDt >= :applDtStart");
		  		parasName.add("applDtStart");
		  		parasValue.add(querybean.getApplDtStart()); 
		 }
		 if(querybean.getApplDtEnd()!=null){
			 hql.append(" and dto.applDt <= :applDtEnd");
		  		parasName.add("applDtEnd");
		  		parasValue.add(querybean.getApplDtEnd()); 
		 }
		 
		 if(StringUtils.isNotBlank(querybean.getAcceptAccount())){
			hql.append(" and dto.acceptAccount = :SOwnerAcctId");
	  		parasName.add("SOwnerAcctId");
	  		parasValue.add(querybean.getAcceptAccount()); 
		 }
		 
//		this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.applDt desc ");//默认提示付款申请日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}
	public List queryDiscountByIds(String ids)throws Exception{
		StringBuffer hql = new StringBuffer(" select dto from CollectionReceiveDto as dto,Department dept where  dto.SBranchId=dept.id ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		
		hql.append(" and dto.collectionReceiveId in ( :collectionReceiveId )");
  		parasName.add("collectionReceiveId");
  		String[] split = ids.split(",");
  		List idsList = new ArrayList();
  		Collections.addAll(idsList, split);
  		parasValue.add(idsList); 
		 
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, null);
		return list;
	}
	/**
	 * <p> 方法名称: queryCollReceiveInfoList|描述: 查询提示付款签收明细信息</p>
	 * @param status 票据状态 
	 * @param bankNums 提示付款人大额行号 
	 * @param customer 客户信息
	 * @param account 客户账户
	 * @param user 当前用户
	 * @param collReceiveDto 提示付款签收明细信息
	 * @param queryBean 组合查询
	 * @param page 分页对象
	 * @return list
	 * @throws Exception
	 */
	public List queryCollReceiveInfoList(List status,List bankNums,CustomerDto customer ,AccountDto account,User user,CollectionReceiveDto collReceiveDto,QueryBean querybean,Page page)throws Exception{
		if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" from CollectionReceiveDto as dto where 1=1");
		 List parasValue = new ArrayList();//查询条件值
		 List parasName = new ArrayList();//查询条件名
		if(null != bankNums){//当前用户不为空
	  		if(bankNums.size() == 1){
	  			hql.append(" and dto.acceptAcctSvcr = :bankNum");
	  			parasName.add("bankNum");
	  			parasValue.add(bankNums.get(0));
	  		}else if(bankNums.size()>1){
	  			hql.append(" and dto.acceptAcctSvcr in(:bankList)");
	  			parasName.add("bankList");
	 			parasValue.add(bankNums);
	  		}	
		 }
		 if(null != status){//票据状态
			if(status.size() == 1){
	  			hql.append(" and dto.SBillStatus = :statu");
	  			parasName.add("statu");
	  			parasValue.add(status.get(0));
	  		}else if(status.size()>1){
	  			hql.append(" and dto.SBillStatus in(:SBillStatus)");
	  			parasName.add("SBillStatus");
	 			parasValue.add(status);
	  		}	 
		 }
		 if(customer != null){//客户组织机构代码SOwnerOrgCode
			 if(customer.getSOrgCode() != null && customer.getSOrgCode().length() > 0){
				hql.append(" and dto.acceptCmonId = :collCmonId");
	  			parasName.add("collCmonId");
	  			parasValue.add(customer.getSOrgCode());
			 }
		 }
		 if(account != null){
			 if(account.getSAccountNo() != null && account.getSAccountNo().length() > 0){
				hql.append(" and dto.acceptAccount = :SOwnerAcctId");
	  			parasName.add("SOwnerAcctId");
	  			parasValue.add(account.getSAccountNo()); 
			 }
		 }else{
			hql.append(" and dto.acceptAccount = :SOwnerAcctId");
	  		parasName.add("SOwnerAcctId");
	  		parasValue.add("0"); 
		 }
		 
		//增加对纸票电票的判断
		hql.append(" and dto.SBillMedia = '2'");
		this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.applDt desc,dto.collectionReceiveId");//默认提示付款申请日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}
	
	
	/**
	 * <p>方法名称: queryCollectionReceiveInfoByIds|描述: 根据提示付款签收明细id获得所选票据信息 </p>
	 * @param ids 大票ids(格式id,id,id)
	 * @param page 分页对象
	 * @return list
	 * @throws Exception
	 */
	public List queryCollectionReceiveInfoByIds(String ids,Page page) throws Exception{
		if(page == null){
			page = new Page();
			page.setPageSize(Integer.MAX_VALUE);
		}
		List list = StringUtil.splitList(ids, ",");
		Object[] parasValue = new Object[] {list};
		String[] parasName = new String[] {"ids"};
		String hql = "from CollectionReceiveDto as dto where dto.collectionReceiveId in (:ids)";
		List resultList = this.find(hql, parasName, parasValue, page);
		return resultList;
	}
	/**
	 * 处理   余额、原业务结清状态、额度 信息
	 * @param batch
	 * @param info
	 * @param user
	 */
	private void toCmmitForBalanceAndBusiEndFlag(CollectionSendDto info,User user){
		PoolBillInfo btbillinfo = info.getPoolBillInfo();
		btbillinfo.setBalanceFlag(PublicStaticDefineTab.BALANCE_FLAG_4);
		if(PublicStaticDefineTab.BILL_billBuyMode_ZT.equals(btbillinfo.getBillBuyMode())){//最近来源
			/*DiscountDto disc = (DiscountDto)this.load(btbillinfo.getLastSourceId(), DiscountDto.class);
			if(disc!=null){
				disc.setBusiEndDate(user.getWorkDate());
				disc.setBusiEndFlag(PublicStaticDefineTab.BUSI_END_YES);
				disc.setBusiEndType(PublicStaticDefineTab.BUSI_END_SALE);
			}*/
		}else if(PublicStaticDefineTab.BILL_billBuyMode_MDZT.equals(btbillinfo.getBillBuyMode())
				||PublicStaticDefineTab.BILL_billBuyMode_INNERBUY.equals(btbillinfo.getBillBuyMode())){
			/*RediscountBuyInDto buy = (RediscountBuyInDto)this.load(btbillinfo.getLastSourceId(), RediscountBuyInDto.class);
			if(buy!=null){
				buy.setBusiEndDate(user.getWorkDate());
				buy.setBusiEndType(PublicStaticDefineTab.BUSI_END_SALE);
				buy.setBusiEndFlag(PublicStaticDefineTab.BUSI_END_YES);
			}*/
		}
		//额度释放 2017
		//this.quotaApplyService.txCommitReleaseCreditForBusiEnd(info, "提示付款回款", user);
	}
	
	/** 
	 * <p>方法名称: txReleaseQuotaSend|描述: 提示付款申请方额度释放</p>
	 * @param detailList 提示付款申请明细
	 * @param releasedSort 额度释放类型
	 * @throws Exception
	 */
	public void txReleaseQuotaSend(List detailList,String releasedSort)throws Exception{
		/*List quotalList = new ArrayList();
		CollectionSendDto collSend = null;
		for(int i=0;i<detailList.size();i++){
			collSend = (CollectionSendDto)detailList.get(i);
			CreditQuotaApply credit = new CreditQuotaApply();
			credit.setEdraftCode(collSend.getSBillNo());
			credit.setEdraftMedia(collSend.getSBillMedia());
			credit.setReleaseType(PublicStaticDefineTab.RELEASETYPE_02);//正常释放
			credit.setReleasedSort(releasedSort);
			credit.setReleasedTradeId(collSend.getCollectionSendId());
			quotalList.add(credit);
		}
		//释放额度
		quotaApplyService.txReleaseQuota(quotalList);*/
	}
	
	/** 
	 * <p>方法名称: txReleaseQuotaCollReceive|描述: 提示付款签收方额度释放</p>
	 * @param detailList 提示付款签收明细
	 * @param releasedSort 额度释放类型
	 * @throws Exception
	 */
	public void txReleaseQuotaCollReceive(List detailList,String releasedSort)throws Exception{
		/*List quotalList = new ArrayList();
		CollectionReceiveDto collReceive = null;
		for(int i=0;i<detailList.size();i++){
			collReceive = (CollectionReceiveDto)detailList.get(i);
			CreditQuotaApply credit = new CreditQuotaApply();
			credit.setEdraftCode(collReceive.getSBillNo());
			credit.setEdraftMedia(collReceive.getSBillMedia());
			credit.setReleaseType(PublicStaticDefineTab.RELEASETYPE_02);//正常释放
			credit.setReleasedSort(releasedSort);
			credit.setProductId(PublicStaticDefineTab.ACCEPTION_PRODUCTID); //为承兑产品id要释放承兑所扣减的额度
			credit.setReleasedTradeId(collReceive.getCollectionReceiveId());
			quotalList.add(credit);
		}
		//释放额度
		quotaApplyService.txReleaseQuota(quotalList);*/
	}
	
	/****************** 提示付款、逾期提示付款线上清算-040-处理 start ***********************/
	/** 
	 * <p>方法名称: txDealWithConsign|描述: 提示付款、逾期提示付款线上清算-040-处理</p>
	 * @param billNo 票号
	 * @throws Exception
	 */
	public void txDealWithConsign(String billNo)throws Exception{
		StringBuffer hql = new StringBuffer("from CollectionSendDto dto where dto.SBillNo=? and dto.SBillStatus=?");
		List valueList = new ArrayList();
		valueList.add(billNo);
		valueList.add(PublicStaticDefineTab.TS002);//提示付款、逾期提示付款待签收
		List results = this.find(hql.toString(), valueList);
		if(results.size() > 0){
			CollectionSendDto discSendDto = (CollectionSendDto)results.get(0);
			discSendDto.setSBillStatus(PublicStaticDefineTab.QS000);//线上清算失败
		}else{
			logger.info("提示付款、逾期提示付款线上清算-040-处理=申请明细CollectionSendDto=未找到票号:"+billNo+"状态:"+"TX_001的申请明细！");
		}
		hql = new StringBuffer("from CollectionReceiveDto dto where dto.SBillNo=? and dto.SBillStatus=?");
		valueList = new ArrayList();
		valueList.add(billNo);
		valueList.add(PublicStaticDefineTab.TS002);//贴现申请待签收
		results = this.find(hql.toString(), valueList);
		if(results.size() > 0){
			CollectionReceiveDto discReceiveDto = (CollectionReceiveDto)results.get(0);
			discReceiveDto.setSBillStatus(PublicStaticDefineTab.QS000);//线上清算失败
			
		}else{
			logger.info("提示付款、逾期提示付款线上清算-040-处理=接受明细CollectionReceiveDto=未找到票号:"+billNo+"状态:"+"TX_001、TX_002、TX_003、TX_004、TX_005、TX_006的接受明细！");
		}
	}
	
	
	/**
	 * 北部湾新增
	 */
	public CollectionReceiveDto loadReceiveByWithdraw(String billNo,String accountNo)throws Exception{
		StringBuffer hql = new StringBuffer(" from CollectionReceiveDto as dto where 1=1");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		hql.append(" and dto.acceptAccount in(:acceptAccount)");
		parasName.add("acceptAccount");
		parasValue.add(accountNo);
		hql.append(" and dto.SBillNo in(:billNo)");
		parasName.add("billNo");
		parasValue.add(billNo);
		// 明细状态
		hql.append(" and dto.SBillStatus=(:status)");
		parasName.add("status");
		parasValue.add(PublicStaticDefineTab.TS002);
		
		hql.append(" and dto.SMsgStatus in (:msgstatus)");
		parasName.add("msgstatus");
		ArrayList msgstatus = new ArrayList();
		msgstatus.add(PublicStaticDefineTab.BW000);
		msgstatus.add(PublicStaticDefineTab.BW004);
		parasValue.add(msgstatus);
		//parasValue.add(PublicStaticDefineTab.BW004);
		
		
		List results = this.find(hql.toString(), (String[]) parasName
				.toArray(new String[parasName.size()]), parasValue.toArray());
		return results.size() > 0?(CollectionReceiveDto)results.get(0):null;
	}
	
	/****************** 提示付款、逾期提示付款线上清算-040-处理 end ***********************/
	
 /***************** 商票提示付款代理应答处理 start*****************/
	
	/**
	 * <p> 方法名称: queryDeferBusiRecInfoList|描述: 查询商票提示付款未应答票据信息</p>
	 * @param status 票据状态 
	 * @param bankNums 提示付款人大额行号 
	 * @param applDateList 提示付款申请日
	 * @param collReceiveDto 提示付款签收明细信息
	 * @param queryBean 组合查询
	 * @param operType 0当前营业日为商票提示付款应答截止日T+N、1当前营业日期为超期提示付款应答日T+N+1
	 * @param page 分页对象
	 * @return list
	 * @throws Exception
	 */
	public List queryDeferBusiRecInfoList(List status,List bankNums,List applDateList,CollectionReceiveDto collReceiveDto,QueryBean querybean,int operType,Page page)throws Exception{
		if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" from CollectionReceiveDto as dto where 1=1");
		 List parasValue = new ArrayList();//查询条件值
		 List parasName = new ArrayList();//查询条件名
		if(null != bankNums){//当前用户不为空
	  		if(bankNums.size() == 1){
	  			hql.append(" and dto.acceptAcctSvcr = :bankNum");
	  			parasName.add("bankNum");
	  			parasValue.add(bankNums.get(0));
	  		}else if(bankNums.size()>1){
	  			hql.append(" and dto.acceptAcctSvcr in(:bankList)");
	  			parasName.add("bankList");
	 			parasValue.add(bankNums);
	  		}	
		 }
		 if(null != status){//票据状态
			if(status.size() == 1){
	  			hql.append(" and dto.SBillStatus = :statu");
	  			parasName.add("statu");
	  			parasValue.add(status.get(0));
	  		}else if(status.size()>1){
	  			hql.append(" and dto.SBillStatus in(:SBillStatus)");
	  			parasName.add("SBillStatus");
	 			parasValue.add(status);
	  		}	 
		 }
		 if(applDateList!=null && applDateList.size()>0){
			 if(operType == 0){//到期未应答
				 hql.append(" and dto.applDt in(:applDtList)"); 
				 parasName.add("applDtList");
				 parasValue.add(applDateList);//提示付款申请日
			 }else{//超期提示付款
				 hql.append(" and dto.applDt <=:applDts");
				 parasName.add("applDts");
				 parasValue.add(applDateList.get(0));//提示付款申请日applDateList.get(0)
			 }
		 }
		 
		this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.applDt desc,dto.collectionReceiveId");//默认提示付款申请日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}
	
	public CollectionSendDto queryCollectionSendDtoByBillNo(String billNo,String status) throws Exception {
		String hql = " from CollectionSendDto as send  where  send.SBillNo = '"+billNo+"' and send.SBillStatus = '"+status+"' order by send.applDt desc ";
		List list = this.find(hql.toString());
		CollectionSendDto sendDto = null ;
		if(list!=null && list.size()>0){
			sendDto = (CollectionSendDto)list.get(0);
		}
		return sendDto;
	}
	
	/**
	 * <p> 方法名称: txBusinessCollReceiveAcctNotifyCommit|描述: 网银发起商票提示付款扣款通知确认</p>
	 * @param id
	 * @param collReceiveDto 提示付款签收明细
	 * @param user 当前用户
	 * @throws Exception
	 */
	/*public CommonResult txBusinessCollReceiveAcctNotifyCommit(String id,CollectionReceiveDto collReceiveDto,User user)throws Exception{
		CollectionReceiveDto receiveDto = this.queryCollectionReceiveDtoById(id);
		CommonResult cr = new CommonResult();
		cr.setCallResult(false);
		if(PublicStaticDefineTab.BW001.equals(receiveDto.getSMsgStatus()) || PublicStaticDefineTab.BW002.equals(receiveDto.getSMsgStatus())){
			this.logger.error("票号："+receiveDto.getSBillNo()+" 金额："+receiveDto.getFBillAmount()+"的票据当前处理状态："+receiveDto.getSMsgStatusName()+"，不允许该操作!");
			cr.setResults("票号："+receiveDto.getSBillNo()+" 金额："+receiveDto.getFBillAmount()+"的票据当前处理状态："+receiveDto.getSMsgStatusName()+"，不允许该操作!");
			cr.setDescription(cr.getResults());
			return cr;
		}
		if(PublicStaticDefineTab.TS002.equals(receiveDto.getSBillStatus()) == false){
			this.logger.error("票号："+receiveDto.getSBillNo()+" 金额："+receiveDto.getFBillAmount()+" 的票据当前票据状态："+receiveDto.getSBillStatusName()+"，不允许该操作!");
			cr.setResults("票号："+receiveDto.getSBillNo()+" 金额："+receiveDto.getFBillAmount()+"的票据当前处理状态："+receiveDto.getSMsgStatusName()+"，不允许该操作!");
			cr.setDescription(cr.getResults());
			return cr;
		}
		BtBillInfo billInfo = receiveDto.getBtBillInfo();
		if(user == null){
			user = new User();
			user.setLoginName("");
			Department dept = departmentService.getOrgCodeByBankNumber(receiveDto.getAcceptAcctSvcr());//承兑人开户行信息
			user.setDepartment(dept);
		}
		Map dataMap = new HashMap();
		dataMap.put("user", user); 
		dataMap.put("isCallBack", "0"); 
	    //cr = businessCollectionSignAccountBusinessHandler.sendDeal(null, receiveDto, dataMap);
		return cr;
	}
	*/
	public List queryCollReceiveInfoListForNetBank(List status,List bankNums,CustomerDto customer ,AccountDto account,QueryBean querybean,Page page)throws Exception{
		if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" from CollectionReceiveDto as dto where 1=1");
		 List parasValue = new ArrayList();//查询条件值
		 List parasName = new ArrayList();//查询条件名
		if(null != bankNums){//当前用户不为空
	  		if(bankNums.size() == 1){
	  			hql.append(" and dto.acceptAcctSvcr = :bankNum");
	  			parasName.add("bankNum");
	  			parasValue.add(bankNums.get(0));
	  		}else if(bankNums.size()>1){
	  			hql.append(" and dto.acceptAcctSvcr in(:bankList)");
	  			parasName.add("bankList");
	 			parasValue.add(bankNums);
	  		}	
		 }
		 if(null != status){//票据状态
			if(status.size() == 1){
	  			hql.append(" and dto.SBillStatus = :statu");
	  			parasName.add("statu");
	  			parasValue.add(status.get(0));
	  		}else if(status.size()>1){
	  			hql.append(" and dto.SBillStatus in(:SBillStatus)");
	  			parasName.add("SBillStatus");
	 			parasValue.add(status);
	  		}	 
		 }
		 if(customer != null){//客户组织机构代码SOwnerOrgCode
			 if(customer.getSOrgCode() != null && customer.getSOrgCode().length() > 0){
				hql.append(" and dto.acceptCmonId = :collCmonId");
	  			parasName.add("collCmonId");
	  			parasValue.add(customer.getSOrgCode());
			 }
		 }
		 if(account != null){
			 if(account.getSAccountNo() != null && account.getSAccountNo().length() > 0){
				hql.append(" and dto.acceptAccount = :SOwnerAcctId");
	  			parasName.add("SOwnerAcctId");
	  			parasValue.add(account.getSAccountNo()); 
			 }
		 }else{
			hql.append(" and dto.acceptAccount = :SOwnerAcctId");
	  		parasName.add("SOwnerAcctId");
	  		parasValue.add("0"); 
		 }
		 
		this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.applDt desc,dto.collectionReceiveId");//默认提示付款申请日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}
	
	public List queryCollSendInfoListForNetBank(List status,List bankNums,CustomerDto customer ,AccountDto account,User user,CollectionSendDto collSendDto,QueryBean querybean,Page page)throws Exception{
		 if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" from CollectionSendDto as dto where 1=1");
		 List parasValue = new ArrayList();//查询条件值
		 List parasName = new ArrayList();//查询条件名
		if(null != bankNums){//当前用户不为空
	  		if(bankNums.size() == 1){
	  			hql.append(" and dto.collAcctSvcr = :bankNum");
	  			parasName.add("bankNum");
	  			parasValue.add(bankNums.get(0));
	  		}else if(bankNums.size()>1){
	  			hql.append(" and dto.collAcctSvcr in(:bankList)");
	  			parasName.add("bankList");
	 			parasValue.add(bankNums);
	  		}	
		 }
		 if(null != status){//票据状态
			if(status.size() == 1){
	  			hql.append(" and dto.SBillStatus = :statu");
	  			parasName.add("statu");
	  			parasValue.add(status.get(0));
	  		}else if(status.size()>1){
	  			hql.append(" and dto.SBillStatus in(:SBillStatus)");
	  			parasName.add("SBillStatus");
	 			parasValue.add(status);
	  		}	 
			hql.append(" and dto.SMsgStatus in(:msgStatus)");
			parasName.add("msgStatus");
			parasValue.add(PublicStaticDefineTab.BW005);//人行已确认
		 }
		 if(customer != null){//客户组织机构代码SOwnerOrgCode
			 if(customer.getSOrgCode() != null && customer.getSOrgCode().length() > 0){
				hql.append(" and dto.collCmonId = :collCmonId");
	  			parasName.add("collCmonId");
	  			parasValue.add(customer.getSOrgCode());
			 }
		 }
		 if(account != null){
			 if(account.getSAccountNo() != null && account.getSAccountNo().length() > 0){
				hql.append(" and dto.collAcct = :SOwnerAcctId");
	  			parasName.add("SOwnerAcctId");
	  			parasValue.add(account.getSAccountNo()); 
			 }
		 }else{
			hql.append(" and dto.collAcct = :SOwnerAcctId");
	  		parasName.add("SOwnerAcctId");
	  		parasValue.add("0"); 
		 }
		if(collSendDto != null){
			if(StringUtils.isNotBlank(collSendDto.getPoolBillInfo().getSIssuerName())){ // 出票人名称
				hql.append(" and dto.btBillInfo.SIssuerName = :SIssuerName ");
				parasName.add("SIssuerName");
				parasValue.add(collSendDto.getPoolBillInfo().getSIssuerName());
			}
			//edit by lwj 20151119 西安银行网银提示付款撤销按照承兑人、收票人名称查询不到票据信息。 start
			if(StringUtils.isNotBlank(collSendDto.getPoolBillInfo().getSAcceptor())){ // 承兑人名称
				hql.append(" and dto.btBillInfo.SAcceptor = :SAcceptor ");
				parasName.add("SAcceptor");
				parasValue.add(collSendDto.getPoolBillInfo().getSAcceptor());
			}		
			if(StringUtils.isNotBlank(collSendDto.getPoolBillInfo().getSPayeeName())){ // 收款人名称
				hql.append(" and dto.btBillInfo.SPayeeName = :SPayeeName ");
				parasName.add("SPayeeName");
				parasValue.add(collSendDto.getPoolBillInfo().getSPayeeName());
			}	
			//edit by lwj 20151119 西安银行网银提示付款撤销按照承兑人、收票人名称查询不到票据信息。 start
			
			//eidt by lwj 20151119 西安银行 西安银行按照出票日区间查询 start
			if(collSendDto.getPoolBillInfo().getDIssueDt() != null){ // 出票日期
				hql.append(" and dto.btBillInfo.DIssueDt >= :startDate ");
				parasName.add("startDate");
				parasValue.add(collSendDto.getPoolBillInfo().getDIssueDt());
			}
			if(collSendDto.getPoolBillInfo().getDDueDt() != null){ // 到期日期
				hql.append(" and dto.btBillInfo.DIssueDt <= :endDate ");
				parasName.add("endDate");
				parasValue.add(collSendDto.getPoolBillInfo().getDDueDt());
			}
			//eidt by lwj 20151119 西安银行 西安银行按照出票日区间查询end
			if(collSendDto.getPoolBillInfo().getFBillAmount() != null){ // 金额
				hql.append(" and dto.btBillInfo.FBillAmount = :isseAmt ");
				parasName.add("isseAmt");
				parasValue.add(collSendDto.getPoolBillInfo().getFBillAmount());
			}
			if(StringUtils.isNotBlank(collSendDto.getPoolBillInfo().getSBillNo())){ // 票据号码
				hql.append(" and dto.btBillInfo.SBillNo = :idNb ");
				parasName.add("idNb");
				parasValue.add(collSendDto.getPoolBillInfo().getSBillNo());
			}
			if(StringUtils.isNotBlank(collSendDto.getSBillType())){//票据类型
				hql.append(" and dto.SBillType = :SBillType ");
				parasName.add("SBillType");
				parasValue.add(collSendDto.getSBillType());
			}
		 }
		//this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.applDt desc,dto.collectionSendId");//默认提示付款申请日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}	
	
	public List queryBillInfoListForNetBank(List status,List bankNums,CustomerDto customer ,AccountDto account,User user,BtBillInfo billInfo,QueryBean querybean,Page page)throws Exception{
		 if(page == null){
			 page = new Page();
			 page.setPageSize(Integer.MAX_VALUE);
		 }
		 StringBuffer hql = new StringBuffer(" from BtBillInfo as dto where 1=1 ");
		 List parasValue = new ArrayList();//查询条件值
		 List parasName = new ArrayList();//查询条件名
		if(null != bankNums){//当前用户不为空
	  		if(bankNums.size() == 1){
	  			hql.append(" and dto.SMbfeBankCode = :bankNum");
	  			parasName.add("bankNum");
	  			parasValue.add(bankNums.get(0));
	  		}else if(bankNums.size()>1){
	  			hql.append(" and dto.SMbfeBankCode in(:bankList)");
	  			parasName.add("bankList");
	 			parasValue.add(bankNums);
	  		}	
		 }
		 if(null != status){//票据状态
			if(status.size() == 1){
	  			hql.append(" and dto.SECDSStatus = :statu");
	  			parasName.add("statu");
	  			parasValue.add(status.get(0));
	  		}else if(status.size()>1){
	  			hql.append(" and dto.SECDSStatus in(:billStatus)");
	  			parasName.add("billStatus");
	 			parasValue.add(status);
	  		}	 
			hql.append(" and dto.SDealStatus = :dealStatus");
			parasName.add("dealStatus");
	 		parasValue.add(PublicStaticDefineTab.DS_002);
		 }
		 if(customer != null){//客户组织机构代码SOwnerOrgCode
			 if(customer.getSOrgCode() != null && customer.getSOrgCode().length() > 0){
				hql.append(" and dto.SOwnerOrgCode = :SOwnerOrgCode");
	  			parasName.add("SOwnerOrgCode");
	  			parasValue.add(customer.getSOrgCode());
			 }
		 }
		 if(account != null){
			 if(account.getSAccountNo() != null && account.getSAccountNo().length() > 0){
				hql.append(" and dto.SOwnerAcctId = :SOwnerAcctId");
	  			parasName.add("SOwnerAcctId");
	  			parasValue.add(account.getSAccountNo()); 
			 }
		 }else{
			hql.append(" and dto.SOwnerAcctId = :SOwnerAcctId");
	  		parasName.add("SOwnerAcctId");
	  		parasValue.add("0"); 
		 }
		if(billInfo != null){
			if(StringUtils.isNotBlank(billInfo.getSIssuerName())){ // 出票人名称
				hql.append(" and dto.SIssuerName = :SIssuerName ");
				parasName.add("SIssuerName");
				parasValue.add(billInfo.getSIssuerName());
			}
			////edit by lwj 20151119 西安银行网银背书申请撤销按照承兑人、收票人名称查询不到票据信息。 start
			if(StringUtils.isNotBlank(billInfo.getSAcceptor())){ // 承兑人名称
				hql.append(" and dto.SAcceptor = :SAcceptor ");
				parasName.add("SAcceptor");
				parasValue.add(billInfo.getSAcceptor());
			}		
			if(StringUtils.isNotBlank(billInfo.getSPayeeName())){ // 收款人名称
				hql.append(" and dto.SPayeeName = :SPayeeName ");
				parasName.add("SPayeeName");
				parasValue.add(billInfo.getSPayeeName());
			}
			//edit by lwj 20151119 西安银行网银背书申请撤销按照承兑人、收票人名称查询不到票据信息。 start
			//edit by lwj 20151119 西安银行按出票日区间查询 start
			if(billInfo.getDIssueDt() != null){ // 出票日期
				hql.append(" and dto.DIssueDt >= :startDate ");
				parasName.add("startDate");
				parasValue.add(billInfo.getDIssueDt());
			}
			if(billInfo.getDDueDt() != null){ // 到期日期
				hql.append(" and dto.DIssueDt <= :endDate ");
				parasName.add("endDate");
				parasValue.add(billInfo.getDDueDt());
			}
			//edit by lwj 20151119 西安银行按出票日区间查询 end
			if(billInfo.getFBillAmount() != null){ // 金额
				hql.append(" and dto.FBillAmount = :isseAmt ");
				parasName.add("isseAmt");
				parasValue.add(billInfo.getFBillAmount());
			}
			if(StringUtils.isNotBlank(billInfo.getSBillNo())){ // 票据号码
				hql.append(" and dto.SBillNo = :idNb ");
				parasName.add("idNb");
				parasValue.add(billInfo.getSBillNo());
			}	
		 }
		//this.getQureyHql(hql,"dto",querybean,parasName,parasValue);//设置组合查询和高级查询hql
		hql.append(" order by dto.DDueDt desc,dto.billinfoId");//默认按到票据到期日降序
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters, page);
		return list;
	}	
	
	
	
	
	/**
	 * 票据到期后自动发起提示付款申请【日终任务自动配置】
	 * 1、我行持有的电票
	 * 2、未逾期
	 * 3、到期日=当前工作日
	 */
	public void autoCollectionSendNO1Task() throws Exception{

		List result=this.getCollSendDraftList();
		if(result != null && result.size() > 0 ) {
			logger.info("自动发起提示付款申请票据 " + DateUtils.toString(new Date(), "yyyy-MM-dd HH:mm:ss") + " size：" + result.size());
			Iterator ite = result.iterator();
			while (ite.hasNext()) {
				DraftPool dfPool = (DraftPool)ite.next();
				logger.info("自动发起提示付款申请票据 票号：" + dfPool.getAssetNb()+",票据起始号：" + dfPool.getBeginRangeNo() + " ，票据截至号： " + dfPool.getEndRangeNo() + " 金额：" + dfPool.getAssetAmt());
				PoolBillInfo bill = draftPoolInService.loadByBillNo(dfPool.getAssetNb(),dfPool.getBeginRangeNo(),dfPool.getEndRangeNo());
				PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null,dfPool.getPoolAgreement() , null, null, null);
				logger.info("查询协议信息结束,机构号为["+dto.getCreditDeptNo()+"],受理网点为["+dto.getOfficeNet()+"]");
				if(null==bill){
					continue;
				}
				CollectionSendDto collSendTemp = this.loadSendDtoByBillNo(dfPool.getAssetNb(),dfPool.getBeginRangeNo(),dfPool.getEndRangeNo());
				Department dept = null;
				if(collSendTemp == null ){
					collSendTemp = new CollectionSendDto();
					//大票信息
					collSendTemp.setPoolBillInfo(bill);//大票信息
					collSendTemp.setSBillNo(bill.getSBillNo());//票号
					
					/********************融合改造新增 start******************************/
					collSendTemp.setBeginRangeNo(bill.getBeginRangeNo());//
					collSendTemp.setEndRangeNo(bill.getEndRangeNo());//
					collSendTemp.setDraftSource(bill.getDraftSource());
					collSendTemp.setHilrId(dfPool.getHilrId());
					collSendTemp.setSplitFlag(dfPool.getSplitFlag());
					collSendTemp.setTradeAmt(dfPool.getTradeAmt());
					
					/********************融合改造新增 end******************************/
					
					collSendTemp.setFBillAmount(bill.getFBillAmount());//票面金额
					collSendTemp.setDIssueDt(bill.getDIssueDt());//出票日
					collSendTemp.setDDueDt(bill.getDDueDt());//到期日
					collSendTemp.setSBillMedia(bill.getSBillMedia());//票据介质
					collSendTemp.setSBillType(bill.getSBillType());//票据类型
					//承兑方
					collSendTemp.setAcceptNm(bill.getSAcceptor());//承兑方名称
					collSendTemp.setAcceptAccount(bill.getSAcceptorAccount());//承兑方帐号
					collSendTemp.setAcceptAcctSvcr(bill.getSAcceptorBankCode());//承兑方行号
					collSendTemp.setAcceptBankName(bill.getSAcceptorBankName());//承兑方开户行名称
					//提示付款信息
					collSendTemp.setApplDt(DateUtils.getWorkDayDate());//提示付款日期
					collSendTemp.setAmt(bill.getFBillAmount());//提示付款金额
					collSendTemp.setGuaranteeNo(dfPool.getGuaranteeNo());//担保编号
					
					collSendTemp.setSBranchId(dto.getOfficeNet());//存储网点号  用于权限分配
					
					collSendTemp.setBpsNo(dto.getPoolAgreement());//票据池编号
					collSendTemp.setAccptrOrg(dfPool.getAccptrOrg());//承兑人组织机构代码
					//判断是否为本行票据
					logger.info("根据承兑行行号["+bill.getSAcceptorBankCode()+"]查询部门信息开始");
					dept = this.departmentService.getDepartmentByBankNo(bill.getSAcceptorBankCode());
					if(dept!=null){
						logger.info("查询部门信息结束,部门信息不为空");
						//提示付款人（或逾期提示付款人=银行信息）信息
						collSendTemp.setCollNm(dept.getName());//提示付款人(或逾期)名称
						collSendTemp.setCollCmonId(dept.getOrgCode());//提示付款人(或逾期)组织机构代码
						collSendTemp.setCollAcct("0");//提示付款人(或逾期)帐号
						collSendTemp.setCollAcctSvcr(dept.getBankNumber());//提示付款人(或逾期)大额行号
					}
					collSendTemp.setAccNo(dfPool.getAccNo());//电票签约账号
				}
				collSendTemp.setLastOperTm(DateUtils.getWorkDayDate());
				collSendTemp.setLastOperName("托收自动任务,发起提示付款申请");
				
				if(dfPool.getDraftSource() != null && dfPool.getDraftSource().equals(PoolComm.CS02)){
					/**
					 * 新一代票据,票交所自动提示付款
					 */
					//状态统一使用PoolComm对象定义
					collSendTemp.setSBillStatus(PoolComm.TS00);//明细状态
					bill.setSDealStatus(PoolComm.DS_06);//大票表设置票据状态为到期处理中
					//得到pl_pool 对象,为了改变该票的状态为已发托,额度计算式需要
					dfPool.setAssetStatus(PoolComm.DS_06);//已发托
					dfPool.setLastOperTm(DateUtils.getWorkDayDate());
					dfPool.setLastOperName("自动托收过程,票据到期后自动发起提示付款申请");
					bill.setLastOperTm(DateUtils.getWorkDayDate());
					bill.setLastOperName("自动托收过程,票据到期后自动发起提示付款申请");
					this.txStore(collSendTemp);
					this.txStore(bill);
					this.txStore(dfPool);
				}else{
					/**
					 * ecds票调用提示付款接口
					 */
					ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
					poolTransNotes.setBillId(bill.getDiscBillId());//票据id
					//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
					if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
						poolTransNotes.setBatchNo(dto.getCreditDeptNo());//融资机构号
					}else{
						poolTransNotes.setBatchNo(dto.getOfficeNet());//受理网点
					}
					logger.info("根据机构号["+poolTransNotes.getBranchNo()+"]查询机构信息开始");
					dept = this.departmentService.getDepartmentByInnerBankCode(poolTransNotes.getBatchNo());
					logger.info("查询机构信息结束,行号为["+dept.getPjsBrchNo()+"]");
					poolTransNotes.setBranchNo(dept.getAuditBankCode());//解质押机构号

					poolTransNotes.setApplyDt(DateUtils.toString(DateUtils.getWorkDayDate(), "yyyyMMdd"));//提示付款申请日期

					poolTransNotes.setSignature("0");//电子签名
					poolTransNotes.setOverdueReason("已到期");//逾期原因
					poolTransNotes.setIfInPool("1");//入池标志
					poolTransNotes.setTransNo("1");//操作类型
					logger.info("票据id为["+bill.getDiscBillId()+"]的票,提示付款申请开始");
					try {
						ReturnMessageNew resp = ecdsServiceImpl.txApplyTSPayment(poolTransNotes);
						if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
							logger.info("提示付款申请结束");
							//状态统一使用PoolComm对象定义
							collSendTemp.setSBillStatus(PoolComm.TS00);//明细状态
							bill.setSDealStatus(PoolComm.DS_06);//大票表设置票据状态为到期处理中
							//得到pl_pool 对象,为了改变该票的状态为已发托,额度计算式需要
							dfPool.setAssetStatus(PoolComm.DS_06);//已发托
							dfPool.setLastOperTm(DateUtils.getWorkDayDate());
							dfPool.setLastOperName("自动托收过程,票据到期后自动发起提示付款申请");
							bill.setLastOperTm(DateUtils.getWorkDayDate());
							bill.setLastOperName("自动托收过程,票据到期后自动发起提示付款申请");
						}
						this.txStore(collSendTemp);
						this.txStore(bill);
						this.txStore(dfPool);
					} catch (Exception e) {
						logger.error(e);
						continue;
					}
				}
				
			}
		}
		
	}
	
	/*********************自动发起提示付款申请-任务 end*******************/
	
	
	/**
	 * 获取已到期且在入池状态票据信息
	 * @return
	 */
	public List getCollSendDraftList() throws Exception{
	
		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();	
		hql.append("select dto from DraftPool dto where 1=1 ");
		//票据到期日 <= 当前工作日
		hql.append(" and dto.plDueDt <= :plDueDt");
		parasName.add("plDueDt");
		parasValue.add(DateUtils.getWorkDayDate());
		hql.append(" and dto.assetStatus in(:assetStatus) ");
		
		//已入池或票据已到期数据，注意：只有已入池、票据已到期、已发托票据才计算票据池额度。
		List useStates = new ArrayList();
		useStates.add(PoolComm.DS_02);
//		useStates.add(PoolComm.DS_06);
		
		parasName.add("assetStatus");
		parasValue.add(useStates);
		hql.append(" and dto.plDraftMedia = :plDraftMedia");
		parasName.add("plDraftMedia");
		parasValue.add("2");
		
		//资产对象为票据
//		hql.append(" and dto.assetType =:assetType");
//		parasName.add("assetType");
//		parasValue.add(PoolComm.ED_PJC);
		
		List result = this.find(hql.toString(),(String[])parasName.toArray(new String[parasName.size()]),parasValue.toArray());
		
		return result;
	}
	
	@Override
	public List getCollectionSendByStatus(List status,
			String bankNums) throws Exception {

		 StringBuffer hql = new StringBuffer(" from CollectionSendDto as dto where 1=1");
		 List parasValue = new ArrayList();//查询条件值
		 List parasName = new ArrayList();//查询条件名
		if(null != bankNums){//提示付款行大额支付行号
  
  			hql.append(" and dto.collAcctSvcr = :collAcctSvcr");
  			parasName.add("collAcctSvcr");
  			parasValue.add(bankNums);

		 }
		 if(null != status){//票据状态
		
  			hql.append(" and dto.SBillStatus in (:SBillStatus) ");
  			parasName.add("SBillStatus");
  			parasValue.add(status);
	  		
		 }
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		return list;
	
	}
	
	/**
	 * 获取已到期且在入池状态票据信息
	 * @return
	 */
	public DraftPool  getDraftPoolByBillNo(String billNo){
	
		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();	
		hql.append("select dto from DraftPool dto where 1=1 ");
		//票据到期日 <= 当前工作日
		hql.append(" and dto.assetNb = :assetNb ");
		parasName.add("assetNb");
		parasValue.add(billNo);
		hql.append(" and dto.assetStatus != :assetStatus ");
		parasName.add("assetStatus");
		parasValue.add(PoolComm.DS_04);
	
		
		List result = this.find(hql.toString(),(String[])parasName.toArray(new String[parasName.size()]),parasValue.toArray());
		if(result.size()>0){
			return (DraftPool)result.get(0);
		}else{
			return null;
		}
		
	}
	public BtBillInfo loadByBillNo(String billNo,String startNo, String endNo) throws Exception {
		String sql = "select obj from BtBillInfo obj where obj.SBillNo =? and obj.beginRangeNo =? and obj.endRangeNo =?";
		List param = new ArrayList();
		param.add(billNo);
		param.add(startNo);
		param.add(endNo);
		List list = this.find(sql, param);
		if(list!=null&&list.size()>0){
			return (BtBillInfo) list.get(0);
		}
		return null;
	}

	public RunStateService getRunStateService() {
		return runStateService;
	}

	public void setRunStateService(RunStateService runStateService) {
		this.runStateService = runStateService;
	}


	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return CollectionSendDto.class;
	}

	public String getEntityName() {
		// TODO Auto-generated method stub
		return StringUtil.getClass(CollectionSendDto.class);
	}

	public DepartmentService getDepartmentService() {
		return departmentService;
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@Override
	public List<CollectionSendDto> queryCollectionSendDto(QueryBean queryBean ,List bankNums, User user, Page page)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select dto from CollectionSendDto as dto where 1=1");
		if(StringUtil.isNotBlank(queryBean.getBillNo())){
			sb.append(" and dto.SBillNo like:SBillNo ");
			keys.add("SBillNo");
			values.add("%"+queryBean.getBillNo()+"%");
		}
		
		/********************融合改造新增 start******************************/
		if(StringUtil.isNotBlank(queryBean.getBeginRangeNo())){
			sb.append(" and dto.beginRangeNo like:beginRangeNo ");
			keys.add("beginRangeNo");
			values.add("%"+queryBean.getBeginRangeNo()+"%");
		}
		if(StringUtil.isNotBlank(queryBean.getEndRangeNo())){
			sb.append(" and dto.endRangeNo like:endRangeNo ");
			keys.add("endRangeNo");
			values.add("%"+queryBean.getEndRangeNo()+"%");
		}
		
		/**
		 * 是否可拆分
		 */
		if(queryBean.getSplitFlag()!=null&&!queryBean.getSplitFlag().equals("")){
			sb.append(" and dto.splitFlag = :splitFlag");
			keys.add("splitFlag");
			values.add(queryBean.getSplitFlag());
		}
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(queryBean.getDraftSource()) && queryBean.getDraftSource().equals(PoolComm.CS01)){
			sb.append(" and (dto.draftSource is null or dto.draftSource =:draftSource) ");
			keys.add("draftSource");
			values.add(queryBean.getDraftSource());
		}else if(StringUtil.isNotBlank(queryBean.getDraftSource()) && queryBean.getDraftSource().equals(PoolComm.CS02)){
			sb.append(" and dto.draftSource =:draftSource ");
			keys.add("draftSource");
			values.add(queryBean.getDraftSource());
		}else if(StringUtil.isNotBlank(queryBean.getDraftSource()) && queryBean.getDraftSource().equals(PoolComm.CS03)){
			sb.append(" and dto.draftSource =:draftSource ");
			keys.add("draftSource");
			values.add(queryBean.getDraftSource());
		}
		
		/********************融合改造新增 end******************************/
		
		if(StringUtil.isNotBlank(queryBean.getBillType())){
			sb.append(" and dto.SBillType =:SBillType");
			keys.add("SBillType");
			values.add(queryBean.getBillType());
		}
		if(StringUtil.isNotBlank(queryBean.getCollAcctSvcr())){//申请人账号
			sb.append(" and dto.collAcctSvcr = :collAcctSvcr");
			keys.add("collAcctSvcr");
			values.add(queryBean.getCollAcctSvcr());
		}
		if(StringUtil.isNotBlank(queryBean.getAcceptAcctSvcr())){//承兑行行号
			sb.append(" and dto.acceptAcctSvcr = :acceptAcctSvcr");
			keys.add("acceptAcctSvcr");
			values.add(queryBean.getAcceptAcctSvcr());
		}
		
  			if(null != bankNums && bankNums.size() >0 ){//当前用户不为空
			
// 	  		if(bankNums.size() == 1 && !"".equals(bankNums.get(0))){
// 	  			sb.append(" and dto.collAcctSvcr = :bankNum");
// 	  			keys.add("bankNum");
// 	  			values.add(bankNums.get(0));
// 	  		}else if(bankNums.size()>1){
// 	  			sb.append(" and dto.collAcctSvcr in(:bankList)");
// 	  			keys.add("bankList");
// 	  			values.add(bankNums);
// 	  		}	
 		 }
		//到期日开始startplDueDt
		if(queryBean.getStartDate() != null && !"".equals(queryBean.getStartDate())){

			sb.append(" and dto.DDueDt >= :plstartDueDt");
			keys.add("plstartDueDt");
			values.add(queryBean.getStartDate());
		}
		//到期日结束endplDueDt
		if(queryBean.getEndDate() != null && !"".equals(queryBean.getEndDate())){
			sb.append(" and dto.DDueDt <= :plDueDt");
			keys.add("plDueDt");
			values.add(queryBean.getEndDate());
			
		}
		//提示付款申请日开始startplDueDt
		if(queryBean.getApplDtStart() != null && !"".equals(queryBean.getApplDtStart())){
			
			sb.append(" and dto.applDt >= TO_DATE(:plStartApplDtt, 'yyyy-mm-dd hh24:mi:ss')");
			keys.add("plStartApplDtt");
			values.add(queryBean.getApplDtStart() + " 00:00:00");
		}
		//提示付款申请结束endplDueDt
		if(queryBean.getApplDtEnd() != null && !"".equals(queryBean.getApplDtEnd())){
			sb.append(" and dto.applDt <= TO_DATE(:plEndApplDt, 'yyyy-mm-dd hh24:mi:ss')");
			keys.add("plEndApplDt");
			values.add(queryBean.getApplDtEnd() + " 23:59:59");
			
		}
		if(StringUtil.isNotBlank(queryBean.getSStatusFlag())){
			sb.append(" and dto.SBillStatus =:SBillStatus");
			keys.add("SBillStatus");
			values.add(queryBean.getSStatusFlag());
		}
		if(queryBean.getStatusList() != null && queryBean.getStatusList().size() >0 ){
			sb.append(" and dto.SBillStatus in(:SBillStatus)");
			keys.add("SBillStatus");
			values.add(queryBean.getStatusList());
		}
		
		
		// 增加机构筛选条件
		if(user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if(!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				sb.append(" and dto.SBranchId in (:SBranchId) ");
				keys.add("SBranchId");
				values.add(resultList);
			}
		}
		
		sb.append(" ORDER BY dto.applDt DESC");
		
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list = this.find(sb.toString(), paramNames, paramValues, page);
		if(list!=null&&list.size()>0){
			return list;
		}
		return null;
	}

	@Override
	public void sendCollection(String ids) throws Exception {
		List list = StringUtil.splitList(ids, ",");
		Object[] parasValue = new Object[] {list};
		String[] parasName = new String[] {"ids"};
		String hql = "from PoolBillInfo as pool where pool.billinfoId in (:ids)";
		List resultList = this.find(hql, parasName, parasValue);
		for (int i = 0; i < resultList.size(); i++) {
			PoolBillInfo bill = (PoolBillInfo) resultList.get(i);
			
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setBillNo(bill.getSBillNo());
			queryBean.setBeginRangeNo(bill.getBeginRangeNo());
			queryBean.setEndRangeNo(bill.getEndRangeNo());
			DraftPool dfPool = this.queryDraftByBean(queryBean).get(0);
			
	    	PedProtocolDto dto = (PedProtocolDto) pedProtocolService.queryProtocolDto(null, null, dfPool.getPoolAgreement(), null, null, null);

	    	CollectionSendDto collSendTemp = this.loadSendDtoByBillNo(dfPool.getAssetNb(),dfPool.getBeginRangeNo(),dfPool.getEndRangeNo());
	    	if(collSendTemp==null){
	    		collSendTemp = new CollectionSendDto();
	    		//大票信息
				collSendTemp.setPoolBillInfo(bill);//大票信息
				collSendTemp.setSBillNo(bill.getSBillNo());//票号
				collSendTemp.setBpsNo(dto.getPoolAgreement());//票据池编号
				collSendTemp.setFBillAmount(bill.getFBillAmount());//票面金额
				collSendTemp.setDIssueDt(bill.getDIssueDt());//出票日
				collSendTemp.setDDueDt(bill.getDDueDt());//到期日
				collSendTemp.setSBillMedia(bill.getSBillMedia());//票据介质
				collSendTemp.setSBillType(bill.getSBillType());//票据类型
				//承兑方
				collSendTemp.setAcceptNm(bill.getSAcceptor());//承兑方名称
				collSendTemp.setAcceptAccount(bill.getSAcceptorAccount());//承兑方帐号
				collSendTemp.setAcceptAcctSvcr(bill.getSAcceptorBankCode());//承兑方行号
				collSendTemp.setAcceptBankName(bill.getSAcceptorBankName());//承兑方开户行名称
				//提示付款信息
				collSendTemp.setApplDt(DateUtils.getWorkDayDate());//提示付款日期
				collSendTemp.setAmt(bill.getFBillAmount());//提示付款金额
				collSendTemp.setGuaranteeNo(dfPool.getGuaranteeNo());//担保编号
				collSendTemp.setBpsNo(dto.getPoolAgreement());//票据池编号
				
				collSendTemp.setSBranchId(dto.getOfficeNet());//存储网点号  用于分配权限
				//判断是否为本行票据
				Department dept = this.departmentService.getDepartmentByBankNo(bill.getSAcceptorBankCode());
//				if(dept!=null){
//					//系统内全部 走线下清算20180321
//					collSendTemp.setClearWay(PublicStaticDefineTab.BLCE_MODE_FUTV);
//				}else{
//					collSendTemp.setClearWay(PublicStaticDefineTab.BLCE_MODE_ONLN);	
//				}
				if(dept!=null){
					//提示付款人（或逾期提示付款人=银行信息）信息
					collSendTemp.setCollNm(dept.getName());//提示付款人(或逾期)名称
					collSendTemp.setCollCmonId(dept.getOrgCode());//提示付款人(或逾期)组织机构代码
					collSendTemp.setCollAcct("0");//提示付款人(或逾期)帐号
					collSendTemp.setCollAcctSvcr(dept.getBankNumber());//提示付款人(或逾期)大额行号
				}
				collSendTemp.setAccNo(dfPool.getAccNo());//电票签约账号
				
	    	}
			collSendTemp.setLastOperTm(new Date());
			collSendTemp.setLastOperName("界面发起提示付款申请");
			
			collSendTemp.setBeginRangeNo(bill.getBeginRangeNo());//
			collSendTemp.setEndRangeNo(bill.getEndRangeNo());//
			collSendTemp.setDraftSource(bill.getDraftSource());
			collSendTemp.setHilrId(dfPool.getHilrId());
			collSendTemp.setSplitFlag(dfPool.getSplitFlag());
			

		   /**
		    * 调用提示付款接口
		    */
	    	ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
	    	
	    	poolTransNotes.setBillId(bill.getDiscBillId());//票据id
			//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
			if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
				poolTransNotes.setBatchNo(dto.getCreditDeptNo());//融资机构号
			}else{
				poolTransNotes.setBatchNo(dto.getOfficeNet());//受理网点
			}
			logger.info("根据机构号["+poolTransNotes.getBranchNo()+"]查询机构信息开始");
			Department dept1 = this.departmentService.getDepartmentByInnerBankCode(poolTransNotes.getBatchNo());
			logger.info("查询机构信息结束,行号为["+dept1.getPjsBrchNo()+"]");
			poolTransNotes.setBranchNo(dept1.getAuditBankCode());//解质押机构号
//			poolTransNotes.setBankNo("10000");
//			poolTransNotes.setBankNo(dept.getPjsBrchNo());
			poolTransNotes.setApplyDt(DateUtils.toString(new Date(), "yyyyMMdd"));//提示付款申请日期
			//当前查询的是未逾期的票据
			//若电子签名为空设置为0
//			if(dfPool.getElsignature()!=null&&!dfPool.getElsignature().equals("")){
//				poolTransNotes.setSignature(dfPool.getElsignature());//网银发过来的电子签名
//			}else{
				poolTransNotes.setSignature("0");//电子签名
//			}
			poolTransNotes.setOverdueReason("已到期");//逾期原因
			poolTransNotes.setIfInPool("1");//入池标志
			poolTransNotes.setTransNo("1");//操作类型
			logger.info("票据id为["+bill.getDiscBillId()+"]的票,提示付款申请开始");
			
			try {
				ReturnMessageNew resp = ecdsServiceImpl.txApplyTSPayment(poolTransNotes);
				if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					logger.info("提示付款申请结束");
					Map map = resp.getBody();
					collSendTemp.setHilrId((String)(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID")));//持票ID
					collSendTemp.setTransId((String)(map.get("TRAN_RESULT_ARRAY.TRAN_NO")));//交易ID
					
					//状态统一使用PoolComm对象定义
					collSendTemp.setSBillStatus(PoolComm.TS00);//明细状态
					bill.setSDealStatus(PoolComm.DS_06);//大票表设置票据状态为到期处理中
					//得到pl_pool 对象,为了改变该票的状态为已发托,额度计算式需要
					dfPool.setHilrId((String)(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID")));//持票ID
					dfPool.setAssetStatus(PoolComm.DS_06);//已发托
					dfPool.setLastOperTm(DateUtils.getWorkDayDate());
					dfPool.setLastOperName("自动托收过程,票据到期后自动发起提示付款申请");
					bill.setHilrId((String)(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID")));//持票ID
					bill.setLastOperTm(DateUtils.getWorkDayDate());
					bill.setLastOperName("自动托收过程,票据到期后自动发起提示付款申请");
				}
				this.txStore(collSendTemp);
				this.txStore(bill);
				this.txStore(dfPool);
			} catch (Exception e) {
				logger.error(e);
			}
			logger.info("托收 表票据状态为["+collSendTemp.getSBillStatus()+"]");
			pedProtocolService.txStore(collSendTemp);
			logger.info("大票表状态为["+bill.getSDealStatus()+"]");
			pedProtocolService.txStore(bill);
			logger.info("资产表状态为["+dfPool.getAssetStatus()+"]");
			pedProtocolService.txStore(dfPool);
			logger.info("托收 表票据状态为["+collSendTemp.getSBillStatus()+"],大票表状态为["+bill.getSDealStatus()+"],资产表状态为["+dfPool.getAssetStatus()+"]");
		}
		
	}

	public void txCommit(CollectionSendDto sendDto,PoolBillInfo info, DraftPool pool){
		this.txStore(sendDto);
		this.txStore(info);
		this.txStore(pool);
	}
	@Override
	public List getCollSendForBean(QueryBean queryBean,User user,Page page) throws Exception {
		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();	
		hql.append("select dto from PoolBillInfo dto where 1=1 ");
		//票据到期日 <= 当前工作日
		hql.append(" and dto.DDueDt <= :DDueDt");
		parasName.add("DDueDt");
		parasValue.add(DateUtils.getWorkDayDate());
		hql.append(" and dto.SDealStatus in(:SDealStatus) ");
		
		//已入池或票据已到期数据，注意：只有已入池、票据已到期、已发托票据才计算票据池额度。
		List useStates = new ArrayList();
		useStates.add(PoolComm.DS_02);
		useStates.add(PoolComm.TS04);//提示付款撤销
		useStates.add(PoolComm.TS02);//
//		useStates.add(PoolComm.);//
//		useStates.add(PoolComm.);//
//		useStates.add(PoolComm.);//
//		useStates.add(PoolComm.);//
		
		parasName.add("SDealStatus");
		parasValue.add(useStates);
		hql.append(" and dto.SBillMedia =:SBillMedia ");
		parasName.add("SBillMedia");
		parasValue.add(PoolComm.BILL_MEDIA_ELECTRONICAL);//电票
		
		hql.append(" and (dto.draftSource is null or dto.draftSource =:draftSource) ");
		parasName.add("draftSource");
		parasValue.add(PoolComm.CS01);//ecds票据
		
		if(queryBean.getBillNo()!=null&&!queryBean.getBillNo().equals("")){
			hql.append(" and dto.SBillNo like :SBillNo");
			parasName.add("SBillNo");
			parasValue.add("%"+queryBean.getBillNo()+"%");
		}

		/********************融合改造新增 start******************************/
		if(queryBean.getBeginRangeNo()!=null&&!queryBean.getBeginRangeNo().equals("")){
			hql.append(" and dto.beginRangeNo like :beginRangeNo");
			parasName.add("beginRangeNo");
			parasValue.add("%"+queryBean.getBeginRangeNo()+"%");
		}
		if(queryBean.getEndRangeNo()!=null&&!queryBean.getEndRangeNo().equals("")){
			hql.append(" and dto.endRangeNo like :endRangeNo");
			parasName.add("endRangeNo");
			parasValue.add("%"+queryBean.getEndRangeNo()+"%");
		}
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(queryBean.getDraftSource()) && queryBean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (dto.draftSource is null or dto.draftSource =:draftSource) ");
			parasName.add("draftSource");
			parasValue.add(queryBean.getDraftSource());
		}else if(StringUtil.isNotBlank(queryBean.getDraftSource()) && queryBean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and dto.draftSource =:draftSource ");
			parasName.add("draftSource");
			parasValue.add(queryBean.getDraftSource());
		}else if(StringUtil.isNotBlank(queryBean.getDraftSource()) && queryBean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and dto.draftSource =:draftSource ");
			parasName.add("draftSource");
			parasValue.add(queryBean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(queryBean.getSplitFlag()!=null&&!queryBean.getSplitFlag().equals("")){
			hql.append(" and dto.splitFlag = :splitFlag");
			parasName.add("splitFlag");
			parasValue.add(queryBean.getSplitFlag());
		}
		
		
		/********************融合改造新增 end******************************/

		if(queryBean.getBillType()!=null&&!queryBean.getBillType().equals("")){
			hql.append(" and dto.SBillType =:SBillType");
			parasName.add("SBillType");
			parasValue.add(queryBean.getBillType());
		}
		if(queryBean.getAcceptorBankCode()!=null&&!queryBean.getAcceptorBankCode().equals("")){
			hql.append(" and dto.SAcceptorBankCode =:SAcceptorBankCode");
			parasName.add("SAcceptorBankCode");
			parasValue.add(queryBean.getAcceptorBankCode());
		}
		//资产对象为票据
//		hql.append(" and dto.assetType =:assetType");
//		parasName.add("assetType");
//		parasValue.add(PoolComm.ED_PJC);
		
		// 增加机构筛选条件
		if(user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if(!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and dto.SBranchId in (:SBranchId) ");
				parasName.add("SBranchId");
				parasValue.add(resultList);
			}
		}
		
		hql.append(" ORDER BY dto.DDueDt ");
		
		List result = this.find(hql.toString(), (String[]) parasName.toArray(new String[parasName.size()]),
				parasValue.toArray(), page);

		return result;
	}

	@Override
	public CollectionSendDto loadSendDtoByBillNo(String billNo,String beginRangeNo, String endRangeNo)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();	
		hql.append("select dto from CollectionSendDto dto where 1=1 ");
		hql.append(" and dto.SBillNo =:SBillNo");
		parasName.add("SBillNo");
		parasValue.add(billNo);

		/********************融合改造新增 start******************************/
		hql.append(" and dto.beginRangeNo =:beginRangeNo");
		parasName.add("beginRangeNo");
		parasValue.add(beginRangeNo);
		hql.append(" and dto.endRangeNo =:endRangeNo");
		parasName.add("endRangeNo");
		parasValue.add(endRangeNo);
		/********************融合改造新增 end******************************/

		List result = this.find(hql.toString(),(String[])parasName.toArray(new String[parasName.size()]),parasValue.toArray());
		if(result!=null&&result.size()>0){
			return (CollectionSendDto) result.get(0);
		}
		return null;
	}


	
	@Override
	public DraftPool getDraftPoolByParam(String billNo, String TXFlag)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();	
		hql.append("select pool from DraftPool pool where 1=1 ");
		if(StringUtil.isNotBlank(billNo)){
			hql.append(" and pool.assetNb =:assetNb");
			parasName.add("assetNb");
			parasValue.add(billNo);
			hql.append(" and pool.assetStatus =:plStatus");
			parasName.add("plStatus");
			parasValue.add(PoolComm.DS_04);
			
		}
		if(StringUtil.isNotBlank(TXFlag)){
			hql.append(" and pool.TXFlag =:TXFlag");
			parasName.add("TXFlag");
			parasValue.add(TXFlag);
			
		}
		List<DraftPool> result = this.find(hql.toString(),(String[])parasName.toArray(new String[parasName.size()]),parasValue.toArray());
		if(result != null && result.size() >0){
			return result.get(0);
		}
		return null;
	}
	
	@Override
	public List<DraftPool> getPaperCollection() throws Exception {
		
		
		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();	
		
		hql.append("select dto from DraftPool dto where 1=1 ");
		
		//纸票
		hql.append(" and dto.plDraftMedia = :plDraftMedia");
		parasName.add("plDraftMedia");
		parasValue.add(PoolComm.BILL_MEDIA_PAPERY);
		
		//（票据到期日-10天） <= 当前工作日
		hql.append(" and dto.plDueDt >= :plDueDt");
		parasName.add("plDueDt");
		parasValue.add(DateTimeUtil.getDate(DateUtils.getWorkDayDate(), -10));
		
		hql.append(" and dto.assetStatus in(:assetStatus) ");
		
		//已入池数据，注意：只有已入池、票据已到期、已发托票据才计算票据池额度。
		List useStates = new ArrayList();
		useStates.add(PoolComm.DS_02);
		useStates.add(PoolComm.DS_03);
//		useStates.add(PoolComm.DS_06);
		
		parasName.add("assetStatus");
		parasValue.add(useStates);
		
		List result = this.find(hql.toString(),(String[])parasName.toArray(new String[parasName.size()]),parasValue.toArray());
		
		if(result != null && result.size() > 0){
			return result;
		}
		return null;
	}

	@Override
	public List<DraftPool> queryDraftByBean(PoolQueryBean queryBean) throws Exception {
		StringBuffer hql = new StringBuffer();
		List parasName = new ArrayList();
		List parasValue = new ArrayList();
		hql.append("select dto from DraftPool dto where 1=1 ");
		//票号
		if(StringUtil.isNotBlank(queryBean.getBillNo())){
			hql.append(" and dto.assetNb = :assetNb ");
			parasName.add("assetNb");
			parasValue.add(queryBean.getBillNo());
		}
		//票据状态
		if(StringUtil.isNotBlank(queryBean.getSStatusFlag())){
			hql.append(" and dto.assetStatus = :assetStatus ");
			parasName.add("assetStatus");
			parasValue.add(queryBean.getSStatusFlag());
		}
		//票据池编号
		if(StringUtil.isNotBlank(queryBean.getProtocolNo())){
			hql.append(" and dto.poolAgreement = :poolAgreement ");
			parasName.add("poolAgreement");
			parasValue.add(queryBean.getProtocolNo());
		}
		//客户号
		if(StringUtil.isNotBlank(queryBean.getCustomernumber())){
			hql.append(" and dto.custNo = :custNo ");
			parasName.add("custNo");
			parasValue.add(queryBean.getCustomernumber());
		}
		//票据介质
		if (StringUtil.isNotBlank(queryBean.getSBillMedia())){
			hql.append(" and dto.plDraftMedia = :plDraftMedia ");
			parasName.add("plDraftMedia");
			parasValue.add(queryBean.getSBillMedia());
		}
		//机构
		if(StringUtil.isNotBlank(queryBean.getBatchid())){
			hql.append(" and dto.poperBeatch = :poperBeatch ");
			parasName.add("poperBeatch");
			parasValue.add(queryBean.getBatchid());
		}
		//柜员
		if (StringUtil.isNotBlank(queryBean.getUser())){
			hql.append(" and dto.operatoUser = :operatoUser ");
			parasName.add("operatoUser");
			parasValue.add(queryBean.getUser());
		}

		/********************融合改造新增 start******************************/
		//子票区间起始
		if(StringUtils.isNotEmpty(queryBean.getBeginRangeNo())){
			hql.append(" and dto.beginRangeNo =:beginRangeNo  ");
			parasName.add("beginRangeNo");
			parasValue.add(queryBean.getBeginRangeNo());
		}
		//子票区间截至
		if(StringUtils.isNotEmpty(queryBean.getEndRangeNo())){
			hql.append(" and dto.endRangeNo =:endRangeNo  ");
			parasName.add("endRangeNo");
			parasValue.add(queryBean.getEndRangeNo());
		}
		/********************融合改造新增 start******************************/
		
		
		List result = this.find(hql.toString(),(String[])parasName.toArray(new String[parasName.size()]),parasValue.toArray());
		if(result != null && result.size()>0){
			return result;
		}else{
			return null;
		}
	}

	@Override
	public CollectionSendDto queryCollectionSend(String billNo, String startBillNo,
			String endBillNo) throws Exception {
		 StringBuffer hql = new StringBuffer(" from CollectionSendDto as dto where dto.SBillNo = '"+billNo+"' and dto.beginRangeNo <= "+startBillNo+" " +
		 		"and dto.endRangeNo >= "+endBillNo+" and dto.SBillStatus = 'TS_00' ");
		 List result = this.find(hql.toString());
		 if(result != null && result.size() >0){
			 return (CollectionSendDto) result.get(0);
		 }
		return null;
	}
	
	@Override
	public CollectionSendDto queryCollectionSendByStatus(String billNo, String startBillNo,
			String endBillNo) throws Exception {
		 StringBuffer hql = new StringBuffer(" from CollectionSendDto as dto where dto.SBillNo = '"+billNo+"' and dto.beginRangeNo = '"+startBillNo+"' " +
		 		"and dto.endRangeNo = '"+endBillNo+"' and dto.SBillStatus = 'TS_00' ");
		 List result = this.find(hql.toString());
		 if(result != null && result.size() >0){
			 return (CollectionSendDto) result.get(0);
		 }
		return null;
	}


}
