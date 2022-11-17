package com.mingtech.application.pool.infomanage.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.infomanage.domain.AccountDto;
import com.mingtech.application.pool.infomanage.domain.AccountDtoRequestbean;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.pool.infomanage.domain.CustomerDtoNode;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.AccountService;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.common.util.SecurityEncode;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.common.util.SystemConfig;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 类说明：客户信息管理service实现
 *
 * @author huangshiqiang@ Jun 9, 2009
 */
@Service
public class CustomerServiceImpl extends GenericServiceImpl implements CustomerService {
	private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class);
	//private CoreClient coreClient = null;
	private DepartmentService departmentService = null;
	private AccountService accountService;
	private SystemConfig systemConfig;// 系统配置文件，提供用户初始密码
	protected GenericHibernateDao sessionDao;
	public GenericHibernateDao getSessionDao() {
		return sessionDao;
	}

	public void setSessionDao(GenericHibernateDao sessionDao) {
		this.sessionDao = sessionDao;
	}

	public Class getEntityClass() {
		return CustomerDto.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(CustomerDto.class);
	}
	
	/**
	 * 修改票据池客户部分信息
	 * @param customer 客户Dto
	 * @param user 操作人
	 */
	public String txsaveCustomerInfo(CustomerDto customer,User user) throws Exception{
		String errInfo="";
		CustomerDto cust=(CustomerDto)this.load(customer.getPkIxBoCustomerId(),CustomerDto.class);
		
		if(null !=cust){
		if(null !=customer.getStorageLowAmt()&& !StringUtil.isStringEmpty(customer.getStorageLowAmt().toString())){  //代保管最低价
			cust.setStorageLowAmt(customer.getStorageLowAmt());
		}
		if(null !=customer.getStoragePayRate()&& !StringUtil.isStringEmpty(customer.getStoragePayRate().toString())){ //代保管费率
			cust.setStoragePayRate(customer.getStoragePayRate());
		}
		if(null !=customer.getStorageRateStartDate()){ //代保管费用计算有效期起始日
			try {
				String	getStorageRateStartDate = DateUtils.toDateString(customer.getStorageRateStartDate());
				if(StringUtil.isDateLegal(getStorageRateStartDate)){
					cust.setStorageRateStartDate(DateUtils.StringToDate(getStorageRateStartDate,"yyyy-MM-dd"));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				return errInfo="代保管费用计算有效期起始日日期格式错误!如：yyyy-MM-dd";
			}
		}
		if(null !=customer.getStorageRateEndDate()){ //代保管费用计算有效期失效日
			try {
				String	getStorageRateEndDate = DateUtils.toDateString(customer.getStorageRateEndDate());
				if(StringUtil.isDateLegal(getStorageRateEndDate)){
					cust.setStorageRateEndDate(DateUtils.StringToDate(getStorageRateEndDate,"yyyy-MM-dd"));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				return errInfo="代保管费用计算有效期失效日日期格式错误!如：yyyy-MM-dd";
			}
		}
		if(null !=customer.getPlDraftLowAmt()&&!StringUtil.isStringEmpty(customer.getPlDraftLowAmt().toString())){  //票据池最低价
			cust.setPlDraftLowAmt(customer.getPlDraftLowAmt());
		}
		if(null !=customer.getPlDraftPayRate()){  // 票据池费率
			cust.setPlDraftPayRate(customer.getPlDraftPayRate());
		}
		if(null !=customer.getPlDraftRateStartDate()){ //票据池费用计算有效期起始日
			try {
				String	getPlDraftRateStartDate = DateUtils.toDateString(customer.getPlDraftRateStartDate());
				if( StringUtil.isDateLegal(getPlDraftRateStartDate)){
					cust.setPlDraftRateStartDate(DateUtils.StringToDate(getPlDraftRateStartDate,"yyyy-MM-dd"));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				return errInfo="票据池费用计算有效期起始日日期格式错误!如：yyyy-MM-dd";
			}
		}	
		if(null !=customer.getPlDraftRateEndDate()){  //票据池费用计算有效期失效日
			try {
				String	getPlDraftRateEndDate = DateUtils.toDateString(customer.getPlDraftRateEndDate());
				if( StringUtil.isDateLegal(getPlDraftRateEndDate)){
					cust.setPlDraftRateEndDate(DateUtils.StringToDate(getPlDraftRateEndDate,"yyyy-MM-dd"));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				return errInfo="票据池费用计算有效期失效日日期格式错误!如：yyyy-MM-dd";
			}
		}
		if(null !=customer.getDefDisRate()&& !StringUtil.isStringEmpty(customer.getDefDisRate().toString())){  //客户打折率
			cust.setDefDisRate(customer.getDefDisRate());
		}
		if(null !=customer.getDefDisRateStartDate()){ //客户打折率起始日
			try {
				String getDefDisRateStartDate= DateUtils.toDateString(customer.getDefDisRateStartDate());
				if(StringUtil.isDateLegal(getDefDisRateStartDate)){
					cust.setDefDisRateStartDate(DateUtils.StringToDate(getDefDisRateStartDate,"yyyy-MM-dd"));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				return errInfo="客户打折率起始日日期格式错误!如：yyyy-MM-dd";
			}
		}
		if(null !=customer.getDefDisRateEndDate()){ //客户打折率失效日
			try {
				String getDefDisRateEndDate= DateUtils.toDateString(customer.getDefDisRateEndDate());
				if(StringUtil.isDateLegal(getDefDisRateEndDate)){
					cust.setDefDisRateEndDate(DateUtils.StringToDate(getDefDisRateEndDate,"yyyy-MM-dd"));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				return errInfo="客户打折率失效日日期格式错误!如：yyyy-MM-dd";
			}
		}
		this.txStore(cust);
		return errInfo = "更新成功！";
		}else{
		return errInfo = "账号信息未维护！";
		}
	}
	/**
	 * 银行代理接入  账号签约 
	 * @param user
	 * @param acct
	 * @throws Exception
	 */
	public void txCommitAcctSignOpen(User user,String id)throws Exception{
		AccountDto acct = (AccountDto)this.load(id, AccountDto.class);
		if("01".equals(acct.getSignFlag())){
			throw new Exception("当前账户已签约成功!不允许重复签约！");
		}
		acct.setSignFlag("01");
		acct.setSignOperId(user.getId());
		acct.setSignDate(user.getWorkDate());
		this.txStore(acct);
		
		/***签约完成后生成代理签约用户***/
	
		User acctSignUser =  new User();
		UserService  userService = (UserService)SpringContextUtil.getBean("userService");
		User acctSign = userService.getUserByLoginName(acct.getSAccountNo());
		if(null ==acctSign){
			acctSignUser.setId(null);  // id
			acctSignUser.setLoginName(acct.getSAccountNo()); // 用户登录名
			if(StringUtil.isStringEmpty(acctSignUser.getPassword())){// 初始化用户密码
				acctSignUser.setPassword(SecurityEncode
						.EncoderByMd5(systemConfig.getInitPassword()));
			}
			acctSignUser.setDeptId(user.getDepartment().getId()); // 机构
			acctSignUser.setName(acct.getSAccountName()); // 用户名称
			acctSignUser.setAgentFlag("1"); // 代理标记 1代理登录用户 0是默认
			this.txStore(acctSignUser); // 保存代理签约用户信息
		}else{
			throw new Exception(acctSign.getLoginName()+"用户已经在系统存在!请联系系统管理员!");
		}
		
//		new SysOrgManagementImpl().setNotify(true);
	}
	/**
	 * 银行代理接入  账号解约
	 * @param user
	 * @param acct
	 * @throws Exception
	 */
	public void txCommitAcctSignClose(User user,String id)throws Exception{
		AccountDto acct = (AccountDto)this.load(id, AccountDto.class);
		acct.setSignFlag("00");
		acct.setSignCancelOperId(user.getId());
		acct.setSignCancelDate(user.getWorkDate());
		this.txStore(acct);
		/***解约完成后禁用代理签约用户***/
		UserService  userService = (UserService)SpringContextUtil.getBean("userService");
		User acctSignUser = userService.getUserByLoginName(acct.getSAccountNo());
		if(null !=acctSignUser ){
			this.txDelete(acctSignUser);
		}
	}
	
	/**
	 * 根据查询条件  查询客户账号信息  
	 * @param user
	 * @param searchBean
	 * @param page
	 * @return   AccountDtoRequestbean 
	 */
	public List queryCustomerAccountList(User user,AccountDtoRequestbean searchBean,Page page){
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		List parasValue = new ArrayList();//查询条件值
 		List parasName = new ArrayList();//查询条件名
 		 
		sb.append(" select cust,acct from CustomerDto cust ,AccountDto acct " +
				"  where cust.pkIxBoCustomerId=acct.SCustId ");
		
		if(searchBean!=null){
			if(StringUtils.isNotBlank(searchBean.getSOrgCode())){//组织机构代码
				sb.append(" and cust.SOrgCode=:SOrgCode");
				parasName.add("SOrgCode");
 	  			parasValue.add(searchBean.getSOrgCode());
			}
			if(StringUtils.isNotBlank(searchBean.getFinaType())){//客户类别 
				sb.append(" and cust.finaType=:finaType");
				parasName.add("finaType");
 	  			parasValue.add(searchBean.getFinaType());
			}
			if(StringUtils.isNotBlank(searchBean.getRoleCode())){//参与者类别
				sb.append(" and cust.roleCode=:roleCode");
				parasName.add("roleCode");
 	  			parasValue.add(searchBean.getRoleCode());
			}
			if(StringUtils.isNotBlank(searchBean.getSCustName())){//客户名称
				sb.append(" and cust.SCustName like :SCustName");
				parasName.add("SCustName");
 	  			parasValue.add("%"+searchBean.getSCustName()+"%");
			}
			
			if(StringUtils.isNotBlank(searchBean.getSAccountType())){//账户类别
				sb.append(" and acct.SAccountType =:SAccountType");
				parasName.add("SAccountType");
 	  			parasValue.add(searchBean.getSAccountType());
			}
			if(StringUtils.isNotBlank(searchBean.getSAccountNo())){//账号
				sb.append(" and acct.SAccountNo =:SAccountNo");
				parasName.add("SAccountNo");
 	  			parasValue.add(searchBean.getSAccountNo());
			}
			if(StringUtils.isNotBlank(searchBean.getSAccountName())){//账号户名
				sb.append(" and cust.SAccountName like:SAccountName");
				parasName.add("SAccountName");
 	  			parasValue.add("%"+searchBean.getSAccountName()+"%");
			}
		}
		sb.append(" order by  acct.pkAccountId desc ");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list =new ArrayList();
		if(page==null){
			list = this.find(" distinct acct.pkAccountId ",sb.toString(), nameForSetVar, parameters);
		}
		list = this.find(" distinct acct.pkAccountId ",sb.toString(), nameForSetVar, parameters, page);
		List result  = new ArrayList();
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[])list.get(i);
			CustomerDto cust = (CustomerDto)obj[0];
			AccountDto acct = (AccountDto)obj[1];
			AccountDtoRequestbean info  =new AccountDtoRequestbean();
			try {
				BeanUtils.copyProperties(info, cust);
				BeanUtils.copyProperties(info, acct);
				info.setCustBankCode(cust.getSCustBankCode());//客户开户行行号
				info.setCustBankName(cust.getSCustBankName());//客户开户行
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			result.add(info);
		}
		return result;
	}
	/**
	 * 根据账号  查询 账号详细信息
	 * @param acctNo 客户账号
	 * @return 返回一个复合对象，包含账号、客户信息；
	 */
	public AccountDtoRequestbean queryCustomerAcctByAcctNo(String acctNo) {
		String sql = "select cust,acct from CustomerDto cust,AccountDto acct  where cust.pkIxBoCustomerId=acct.SCustId" +
				" and  acct.SAccountNo=?";
		List param = new ArrayList();
		param.add(acctNo);
		List list = this.find(sql, param);
		if(list!=null && list.size()>0){
			Object[] obj = (Object[])list.get(0);
			CustomerDto cust = (CustomerDto)obj[0];
			AccountDto acct = (AccountDto)obj[1];
			AccountDtoRequestbean info  =new AccountDtoRequestbean();
			try {
				BeanUtils.copyProperties(info, cust);
				BeanUtils.copyProperties(info, acct);
				info.setCustBankCode(cust.getSCustBankCode());//客户开户行行号
				info.setCustBankName(cust.getSCustBankName());//客户开户行
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			return info;
		}
		return null;
	}
	/**
	 * 根据账号  查询 账号详细信息
	 * @param acctNo 客户账号
	 * @return
	 */
	public AccountDto queryCustomerAccountByAcctNo(String acctNo) {
		String sql = "select acct from CustomerDto cust,AccountDto acct  where cust.pkIxBoCustomerId=acct.SCustId" +
				" and  acct.SAccountNo=?";
		List param = new ArrayList();
		param.add(acctNo);
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (AccountDto) result.get(0);
		}
		return null;
	}
	
	public List query(CustomerDto customer, Page page) {
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select distinct customer from CustomerDto as customer where 1=1 ");
		if (null != customer) {
			if (StringUtil.isNotBlank(customer.getSOrgCode())) {
				sb.append(" and customer.SOrgCode like ?");
				param.add("%" + customer.getSOrgCode() + "%");
			}
			if (StringUtil.isNotBlank(customer.getSCustAcc())) {
				sb.append(" and customer.SCustAcc=?");
				param.add(customer.getSCustAcc());
			}
			if (StringUtil.isNotBlank(customer.getRoleCode())) {
				sb.append(" and customer.roleCode=?");
				param.add(customer.getRoleCode());
			}
			if (StringUtil.isNotBlank(customer.getSCustName())) {
				sb.append(" and customer.SCustName like ?");
				param.add("%" + customer.getSCustName() + "%");
			}
			if (StringUtil.isNotBlank(customer.getSSignFlag())) {
				sb.append(" and customer.SSignFlag=?");
				param.add(customer.getSSignFlag());
			}
			if (StringUtil.isNotBlank(customer.getDrftOpnStt())) {
				sb.append(" and customer.drftOpnStt=?");
				param.add(customer.getDrftOpnStt());
			}
			if (StringUtil.isNotBlank(customer.getPlStorageStt())) {
				sb.append(" and customer.plStorageStt=?");
				param.add(customer.getPlStorageStt());
			}
			if (StringUtil.isNotBlank(customer.getSCustBankCode())) {
				sb.append(" and customer.SCustBankCode=?");
				param.add(customer.getSCustBankCode());
			}
			if (StringUtil.isNotBlank(customer.getFinaType())) {
				sb.append(" and customer.finaType=?");
				param.add(customer.getFinaType());
			}
			if (StringUtil.isNotBlank(customer.getGroupFlag())) {
				sb.append(" and customer.groupFlag=?");
				param.add(customer.getGroupFlag());
			}
			if (StringUtil.isNotBlank(customer.getGroupCustName())) {
				sb.append(" and customer.groupCustName like ?");
				param.add("%"+customer.getGroupCustName()+"%");
			}
			
			
			if ("0".equals(customer.getRateAdjust())) {
				sb.append(" and customer.storageLowAmt='50' and customer.storagePayRate='0.05'  ");
				sb.append(" and customer.plDraftLowAmt='500' and customer.plDraftPayRate='0.5'  ");
			}else if ("1".equals(customer.getRateAdjust())){
				sb.append(" and (customer.storageLowAmt!='50' or customer.storagePayRate!='0.05'  ");
				sb.append(" or customer.plDraftLowAmt!='500' or customer.plDraftPayRate!='0.5'  ");
				sb.append(" or customer.storagePayRate is null or customer.plDraftPayRate is null ) ");
			}else if ("2".equals(customer.getRateAdjust())){//全部
				
			}
			
			if ("0".equals(customer.getDiscountAdjust())) {
				sb.append(" and customer.defDisRate  is null ");
			}else if ("1".equals(customer.getDiscountAdjust())){
				sb.append(" and customer.defDisRate  is not null ");
				if (null!=customer.getDefDisRateEndDate()) {
					sb.append(" and customer.defDisRateStartDate<=? ");
					param.add(customer.getDefDisRateEndDate());
				}if (null!=customer.getDefDisRateStartDate()) {
					sb.append(" and customer.defDisRateEndDate>=? ");
					param.add(customer.getDefDisRateStartDate());
				}
			}else if ("2".equals(customer.getDiscountAdjust())){//全部
				
			}
			sb.append(" and customer.SCustFlag=?");
			param.add(new Integer(customer.getSCustFlag()));
		}
		return this.find(sb.toString(), param, page);
	}
	public List query1(CustomerDto customer, Page page) {
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select distinct customer from CustomerDto1 as customer where 1=1 ");
		if (null != customer) {
			if (StringUtil.isNotBlank(customer.getSOrgCode())) {
				sb.append(" and customer.SOrgCode like ?");
				param.add("%" + customer.getSOrgCode() + "%");
			}
			if (StringUtil.isNotBlank(customer.getSCustAcc())) {
				sb.append(" and customer.SCustAcc=?");
				param.add(customer.getSCustAcc());
			}
			if (StringUtil.isNotBlank(customer.getRoleCode())) {
				sb.append(" and customer.roleCode=?");
				param.add(customer.getRoleCode());
			}
			if (StringUtil.isNotBlank(customer.getSCustName())) {
				sb.append(" and customer.SCustName like ?");
				param.add("%" + customer.getSCustName() + "%");
			}
			if (StringUtil.isNotBlank(customer.getSSignFlag())) {
				sb.append(" and customer.SSignFlag=?");
				param.add(customer.getSSignFlag());
			}
			if (StringUtil.isNotBlank(customer.getDrftOpnStt())) {
				sb.append(" and customer.drftOpnStt=?");
				param.add(customer.getDrftOpnStt());
			}
			if (StringUtil.isNotBlank(customer.getPlStorageStt())) {
				sb.append(" and customer.plStorageStt=?");
				param.add(customer.getPlStorageStt());
			}
			if (StringUtil.isNotBlank(customer.getSCustBankCode())) {
				sb.append(" and customer.SCustBankCode=?");
				param.add(customer.getSCustBankCode());
			}
			if (StringUtil.isNotBlank(customer.getFinaType())) {
				sb.append(" and customer.finaType=?");
				param.add(customer.getFinaType());
			}
			if (StringUtil.isNotBlank(customer.getGroupFlag())) {
				sb.append(" and customer.groupFlag=?");
				param.add(customer.getGroupFlag());
			}
			if (StringUtil.isNotBlank(customer.getGroupCustName())) {
				sb.append(" and customer.groupCustName like ?");
				param.add("%"+customer.getGroupCustName()+"%");
			}
			
			
			if ("0".equals(customer.getRateAdjust())) {
				sb.append(" and customer.storageLowAmt='50' and customer.storagePayRate='0.05'  ");
				sb.append(" and customer.plDraftLowAmt='500' and customer.plDraftPayRate='0.5'  ");
			}else if ("1".equals(customer.getRateAdjust())){
				sb.append(" and (customer.storageLowAmt!='50' or customer.storagePayRate!='0.05'  ");
				sb.append(" or customer.plDraftLowAmt!='500' or customer.plDraftPayRate!='0.5'  ");
				sb.append(" or customer.storagePayRate is null or customer.plDraftPayRate is null ) ");
			}else if ("2".equals(customer.getRateAdjust())){//全部
				
			}
			
			if ("0".equals(customer.getDiscountAdjust())) {
				sb.append(" and customer.defDisRate  is null ");
			}else if ("1".equals(customer.getDiscountAdjust())){
				sb.append(" and customer.defDisRate  is not null ");
				if (null!=customer.getDefDisRateEndDate()) {
					sb.append(" and customer.defDisRateStartDate<=? ");
					param.add(customer.getDefDisRateEndDate());
				}if (null!=customer.getDefDisRateStartDate()) {
					sb.append(" and customer.defDisRateEndDate>=? ");
					param.add(customer.getDefDisRateStartDate());
				}
			}else if ("2".equals(customer.getDiscountAdjust())){//全部
				
			}
			sb.append(" and customer.SCustFlag=?");
			param.add(new Integer(customer.getSCustFlag()));
		}
		return this.find(sb.toString(), param, page);
	}
	/**
	 * 查询 生效 的  客户信息   
	 * @param user 当前柜员 
	 * @param customer 客户查询条件对象
	 * @param page
	 * @return
	 */
	public List queryCustomerList(User user ,CustomerDto customer, Page page) {
		StringBuffer sb = new StringBuffer();
		sb.append("select customer from CustomerDto as customer where 1=1");
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer dhql = new StringBuffer(); 
		
		if (null != customer) {
			if (StringUtil.isNotBlank(customer.getSOrgCode())) {
				sb.append(" and customer.SOrgCode =:getSOrgCode ");
				paramName.add("getSOrgCode");
				paramValue.add(customer.getSOrgCode());
			}
			if (StringUtil.isNotBlank(customer.getSCustAcc())) {
				sb.append(" and customer.SCustAcc=:SCustAcc");
				paramName.add("SCustAcc");
				paramValue.add(customer.getSCustAcc());
			}
			if (StringUtil.isNotBlank(customer.getBankNo())) {
				sb.append(" and customer.bankNo=:bankNo");
				paramName.add("bankNo");
				paramValue.add(customer.getBankNo());
			
			}
			if (StringUtil.isNotBlank(customer.getRoleCode())) {
				sb.append(" and customer.roleCode=:roleCode");
				paramName.add("roleCode");
				paramValue.add(customer.getRoleCode());
			}
			
			if (StringUtil.isNotBlank(customer.getSCustName())) {
				sb.append(" and customer.SCustName like :SCustName");
				paramName.add("SCustName");
				paramValue.add("%" + customer.getSCustName() + "%");
			}
			if (StringUtil.isNotBlank(customer.getSSignFlag())) {
				sb.append(" and customer.SSignFlag=:SSignFlag");
				paramName.add("SSignFlag");
				paramValue.add(customer.getSSignFlag());
			}
			if (StringUtil.isNotBlank(customer.getDrftOpnStt())) {
				sb.append(" and customer.drftOpnStt=:drftOpnStt");
				paramName.add("drftOpnStt");
				paramValue.add(customer.getDrftOpnStt());
			
			}
			if (StringUtil.isNotBlank(customer.getPlStorageStt())) {
				sb.append(" and customer.plStorageStt=:plStorageStt");
				paramName.add("plStorageStt");
				paramValue.add(customer.getPlStorageStt());
			}
			/*if (StringUtil.isNotBlank(customer.getSCustBankCode())) {
			
				List departmentList = new ArrayList();
				try {
					departmentList=departmentService.getAllChildrenDepartmentByDeptId(user.getDepartment().getId(), false, true, "bankNumber");
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				departmentList.add(customer.getSCustBankCode());
				// 设置机构
				sb.append(" and customer.SCustBankCode in(:departmentList)");
				paramName.add("departmentList");
				paramValue.add(departmentList);
			}*/
			//客户类别  
			if (StringUtil.isNotBlank(customer.getCustType())) {
				sb.append(" and customer.custType in(:custType)");
				paramName.add("custType");
				paramValue.add(StringUtil.getListFromString(customer.getCustType(), ","));
			}
			sb.append(" and customer.SCustFlag=:SCustFlag");
			paramName.add("SCustFlag");
			paramValue.add(customer.getSCustFlag());
		}
	
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		
		return this.find(sb.toString(),paramNames,paramValues,page);
	}
	public List queryMore(User user ,CustomerDto customer, Page page) {
		StringBuffer sb = new StringBuffer();
		sb.append("select customer from CustomerDto as customer where 1=1");
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer dhql = new StringBuffer(); 
		
		if (null != customer) {
			if (StringUtil.isNotBlank(customer.getSOrgCode())) {
				sb.append(" and customer.SOrgCode like:getSOrgCode ");
				paramName.add("getSOrgCode");
				paramValue.add("%"+customer.getSOrgCode()+"%");
			}
			if (StringUtil.isNotBlank(customer.getSCustAcc())) {
				sb.append(" and customer.SCustAcc=:SCustAcc");
				paramName.add("SCustAcc");
				paramValue.add(customer.getSCustAcc());
			}
			if (StringUtil.isNotBlank(customer.getRoleCode())) {
				sb.append(" and customer.roleCode=:roleCode");
				paramName.add("roleCode");
				paramValue.add(customer.getRoleCode());
			} 
			if (StringUtil.isNotBlank(customer.getFinaType())) { // 大的客户类型 0:企业客户;1:同业客户
				sb.append(" and customer.finaType=:finaType");
				paramName.add("finaType");
				paramValue.add(customer.getFinaType());
			}
			if(StringUtil.isNotBlank(customer.getRoleCode())){ // 参与者类别，参考人行规定
				sb.append(" and customer.roleCode=:roleCode");
				paramName.add("roleCode");
				paramValue.add(customer.getRoleCode());
			}
			if(StringUtil.isNotBlank(customer.getBankNo())){ // 同业客户使用 行号
				sb.append(" and customer.bankNo=:bankNo");
				paramName.add("bankNo");
				paramValue.add(customer.getBankNo());
			}
			if (StringUtil.isNotBlank(customer.getSCustName())) {
				sb.append(" and customer.SCustName like :SCustName");
				paramName.add("SCustName");
				paramValue.add("%" + customer.getSCustName() + "%");
			}
			if (StringUtil.isNotBlank(customer.getSSignFlag())) {
				sb.append(" and customer.SSignFlag=:SSignFlag");
				paramName.add("SSignFlag");
				paramValue.add(customer.getSSignFlag());
			}
			if (StringUtil.isNotBlank(customer.getDrftOpnStt())) {
				sb.append(" and customer.drftOpnStt=:drftOpnStt");
				paramName.add("drftOpnStt");
				paramValue.add(customer.getDrftOpnStt());
			
			}
			if (StringUtil.isNotBlank(customer.getPlStorageStt())) {
				sb.append(" and customer.plStorageStt=:plStorageStt");
				paramName.add("plStorageStt");
				paramValue.add(customer.getPlStorageStt());
			}
			if (StringUtil.isNotBlank(customer.getGroupFlag())) {
				sb.append(" and customer.groupFlag= :groupFlag");
				paramName.add("groupFlag");
				paramValue.add(customer.getGroupFlag());
			}
			if (StringUtil.isNotBlank(customer.getGroupCustName())) {
				sb.append(" and customer.groupCustName like :groupCustName ");
				paramName.add("groupCustName");
				paramValue.add("%"+customer.getGroupCustName()+"%");
			}
			
			if (StringUtil.isNotBlank(customer.getSCustBankCode())) {
			
				List departmentList = new ArrayList();
				try {
					departmentList=departmentService.getAllChildrenDepartmentByDeptId(user.getDepartment().getId(), false, true, "bankNumber");
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				departmentList.add(customer.getSCustBankCode());
				// 设置机构
				sb.append(" and customer.SCustBankCode in(:departmentList)");
				paramName.add("departmentList");
				paramValue.add(departmentList);
			}
		
			if ("0".equals(customer.getRateAdjust())) {
				sb.append(" and customer.storageLowAmt='50' and customer.storagePayRate='0.05'  ");
				sb.append(" and customer.plDraftLowAmt='500' and customer.plDraftPayRate='0.5'  ");
			}else if ("1".equals(customer.getRateAdjust())){
				sb.append(" and (customer.storageLowAmt!='50' or customer.storagePayRate!='0.05'  ");
				sb.append(" or customer.plDraftLowAmt!='500' or customer.plDraftPayRate!='0.5'  ");
				sb.append(" or customer.storagePayRate is null or customer.plDraftPayRate is null ) ");
			}else if ("2".equals(customer.getRateAdjust())){//全部
				
			}
			
			if ("0".equals(customer.getDiscountAdjust())) {
				sb.append(" and customer.defDisRate  is null ");
			}else if ("1".equals(customer.getDiscountAdjust())){
				sb.append(" and customer.defDisRate  is not null ");
				if (null!=customer.getDefDisRateEndDate()) {
					sb.append(" and customer.defDisRateStartDate<=:defDisRateStartDate ");
					paramName.add("defDisRateStartDate");
					paramValue.add(customer.getDefDisRateEndDate());
				}if (null!=customer.getDefDisRateStartDate()) {
					sb.append(" and customer.defDisRateEndDate>=:defDisRateEndDate ");
					paramName.add("defDisRateEndDate");
					paramValue.add(customer.getDefDisRateStartDate());
				}
			}else if ("2".equals(customer.getDiscountAdjust())){//全部
				
			}
			
			
			
			
			sb.append(" and customer.SCustFlag=:SCustFlag");
			paramName.add("SCustFlag");
			paramValue.add(customer.getSCustFlag());
		}
	
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		
		return this.find(sb.toString(),paramNames,paramValues,page);
	}
	
	public List queryMore1(User user ,CustomerDto customer, Page page) {
		StringBuffer sb = new StringBuffer();
		sb.append("select customer from CustomerDto1 as customer where 1=1");
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer dhql = new StringBuffer(); 
		
		if (null != customer) {
			if (StringUtil.isNotBlank(customer.getSOrgCode())) {
				sb.append(" and customer.SOrgCode like:getSOrgCode ");
				paramName.add("getSOrgCode");
				paramValue.add("%"+customer.getSOrgCode()+"%");
			}
			if (StringUtil.isNotBlank(customer.getSCustAcc())) {
				sb.append(" and customer.SCustAcc=:SCustAcc");
				paramName.add("SCustAcc");
				paramValue.add(customer.getSCustAcc());
			}
			if (StringUtil.isNotBlank(customer.getRoleCode())) {
				sb.append(" and customer.roleCode=:roleCode");
				paramName.add("roleCode");
				paramValue.add(customer.getRoleCode());
			}
			if (StringUtil.isNotBlank(customer.getFinaType())) { // 大的客户类型 0:企业客户;1:同业客户
				sb.append(" and customer.finaType=:finaType");
				paramName.add("finaType");
				paramValue.add(customer.getFinaType());
			}
			if(StringUtil.isNotBlank(customer.getRoleCode())){ // 参与者类别，参考人行规定
				sb.append(" and customer.roleCode=:roleCode");
				paramName.add("roleCode");
				paramValue.add(customer.getRoleCode());
			}
			if(StringUtil.isNotBlank(customer.getBankNo())){ // 同业客户使用 行号
				sb.append(" and customer.bankNo=:bankNo");
				paramName.add("bankNo");
				paramValue.add(customer.getBankNo());
			}
			if (StringUtil.isNotBlank(customer.getSCustName())) {
				sb.append(" and customer.SCustName like :SCustName");
				paramName.add("SCustName");
				paramValue.add("%" + customer.getSCustName() + "%");
			}
			if (StringUtil.isNotBlank(customer.getSSignFlag())) {
				sb.append(" and customer.SSignFlag=:SSignFlag");
				paramName.add("SSignFlag");
				paramValue.add(customer.getSSignFlag());
			}
			if (StringUtil.isNotBlank(customer.getDrftOpnStt())) {
				sb.append(" and customer.drftOpnStt=:drftOpnStt");
				paramName.add("drftOpnStt");
				paramValue.add(customer.getDrftOpnStt());
			
			}
			if (StringUtil.isNotBlank(customer.getPlStorageStt())) {
				sb.append(" and customer.plStorageStt=:plStorageStt");
				paramName.add("plStorageStt");
				paramValue.add(customer.getPlStorageStt());
			}
			if (StringUtil.isNotBlank(customer.getGroupFlag())) {
				sb.append(" and customer.groupFlag= :groupFlag");
				paramName.add("groupFlag");
				paramValue.add(customer.getGroupFlag());
			}
			if (StringUtil.isNotBlank(customer.getGroupCustName())) {
				sb.append(" and customer.groupCustName like :groupCustName ");
				paramName.add("groupCustName");
				paramValue.add("%"+customer.getGroupCustName()+"%");
			}
			
			if (StringUtil.isNotBlank(customer.getSCustBankCode())) {
			
				List departmentList = new ArrayList();
				try {
					departmentList=departmentService.getAllChildrenDepartmentByDeptId(user.getDepartment().getId(), false, true, "bankNumber");
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				departmentList.add(customer.getSCustBankCode());
				// 设置机构
				sb.append(" and customer.SCustBankCode in(:departmentList)");
				paramName.add("departmentList");
				paramValue.add(departmentList);
			}
		
			if ("0".equals(customer.getRateAdjust())) {
				sb.append(" and customer.storageLowAmt='50' and customer.storagePayRate='0.05'  ");
				sb.append(" and customer.plDraftLowAmt='500' and customer.plDraftPayRate='0.5'  ");
			}else if ("1".equals(customer.getRateAdjust())){
				sb.append(" and (customer.storageLowAmt!='50' or customer.storagePayRate!='0.05'  ");
				sb.append(" or customer.plDraftLowAmt!='500' or customer.plDraftPayRate!='0.5'  ");
				sb.append(" or customer.storagePayRate is null or customer.plDraftPayRate is null ) ");
			}else if ("2".equals(customer.getRateAdjust())){//全部
				
			}
			
			if ("0".equals(customer.getDiscountAdjust())) {
				sb.append(" and customer.defDisRate  is null ");
			}else if ("1".equals(customer.getDiscountAdjust())){
				sb.append(" and customer.defDisRate  is not null ");
				if (null!=customer.getDefDisRateEndDate()) {
					sb.append(" and customer.defDisRateStartDate<=:defDisRateStartDate ");
					paramName.add("defDisRateStartDate");
					paramValue.add(customer.getDefDisRateEndDate());
				}if (null!=customer.getDefDisRateStartDate()) {
					sb.append(" and customer.defDisRateEndDate>=:defDisRateEndDate ");
					paramName.add("defDisRateEndDate");
					paramValue.add(customer.getDefDisRateStartDate());
				}
			}else if ("2".equals(customer.getDiscountAdjust())){//全部
				
			}
			
			
			
			
			sb.append(" and customer.SCustFlag=:SCustFlag");
			paramName.add("SCustFlag");
			paramValue.add(customer.getSCustFlag());
		}
	
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		
		return this.find(sb.toString(),paramNames,paramValues,page);
	}
	
	public String queryData(User user, CustomerDto customer, Page page,boolean flag) throws Exception {
		List result = new ArrayList();
		if(flag==false){
			result= this.query(customer, page);
		}else{
			result= this.queryMore(user,customer, page);
		}
		
		
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(result, map);
	} 

	public String queryData2() throws Exception{
		
		List list =new ArrayList();
	
		String hql ="from JtEduDto ";
		list=this.find(hql);

		
		return JsonUtil.buildJson(list,1L);
	}
	
	public CustomerDto loadByOrgCode(String orgCode) {
		String sql = "select customer from CustomerDto as customer where customer.SOrgCode=?";
		List param = new ArrayList();
		param.add(orgCode);
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (CustomerDto) result.get(0);
		}
		return null;
	}
	
	public AccountDto validateCustomer(AccountDto account){
		if(account==null)return null;
		if(!StringUtil.isStringEmpty(account.getSPassword())){// 将用户输入的密码加密处理
			account.setSPassword(SecurityEncode.EncoderByMd5(account.getSPassword()));
		}
		StringBuffer hql = new StringBuffer("from AccountDto account where account.SLoginName=? and account.SPassword=? ");
		if(StringUtils.isNotBlank(account.getSAccountNo())){
			hql.append(" and account.SAccountNo = ? ");
		}
		
		List parameters = new ArrayList();
		parameters.add(account.getSLoginName());
		parameters.add(account.getSPassword());
		if(StringUtils.isNotBlank(account.getSAccountNo())){
			parameters.add(account.getSAccountNo());
		}
		List accounts = this.find(hql.toString(), parameters);
		if(CollectionUtil.isNotEmpty(accounts)){
			AccountDto acc = (AccountDto) accounts.get(0);
			return acc;
		}
		return null;
	}
	/**
	 * 根据  账号、账号开户行查询账号信息
	 * @param acct
	 * @param bankNo
	 * @return
	 */
	public AccountDto queryCustAccountByAcctAndBankNo(String acct,String bankNo){
		StringBuffer hql = new StringBuffer("from AccountDto account where account.SAccountNo=? and account.SCustBankCode=? ");
		List parameters = new ArrayList();
		parameters.add(acct);
		parameters.add(bankNo);
		List list = this.find(hql.toString(), parameters);
		if(list!=null && list.size()>0){
			return (AccountDto)list.get(0);
		}
		return null;
	}
	/**
	 * 根据  账号查询账号信息
	 * @param acct
	 * @return
	 */
	public AccountDto queryCustAccountByAcctNo(String acct){
		StringBuffer hql = new StringBuffer("from AccountDto account where account.SAccountNo=?  ");
		List parameters = new ArrayList();
		parameters.add(acct);
		List list = this.find(hql.toString(), parameters);
		if(list!=null && list.size()>0){
			return (AccountDto)list.get(0);
		}
		return null;
	}
	/**
	 * 根据  客户 证件号码  查询客户信息
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public CustomerDto loadCustomerByCertCode(CustomerDto dto)throws Exception{
		StringBuffer sql = new StringBuffer("select customer from " +
				"CustomerDto as customer where customer.certCode=? ");
		List param = new ArrayList();
		param.add(dto.getCertCode());
		//if update
		if(dto.getPkIxBoCustomerId()!=null&&
				!dto.getPkIxBoCustomerId().equals("")){
			sql.append(" and customer.pkIxBoCustomerId!=? ");
			param.add(dto.getPkIxBoCustomerId());
		}
		List result = this.find(sql.toString(), param);
		if (null != result && result.size() > 0) {
			return (CustomerDto) result.get(0);
		}
		return null;
	}
	public CustomerDto loadCustomerByOrgCode(CustomerDto dto)throws Exception{
		StringBuffer sql = new StringBuffer("select customer from " +
				"CustomerDto as customer where customer.SOrgCode=? ");
		List param = new ArrayList();
		param.add(dto.getSOrgCode());
		//if update
		if(dto.getPkIxBoCustomerId()!=null&&
				!dto.getPkIxBoCustomerId().equals("")){
			sql.append(" and customer.pkIxBoCustomerId!=? ");
			param.add(dto.getPkIxBoCustomerId());
		}
		List result = this.find(sql.toString(), param);
		if (null != result && result.size() > 0) {
			return (CustomerDto) result.get(0);
		}
		return null;
	}

	public void txDelete(CustomerDto customer) {
		customer = (CustomerDto) this.load(customer.getPkIxBoCustomerId());
		customer.setSCustFlag(CustomerDto.Flag_NoUse);
		this.txStore(customer);
	}

	/**
	 * 根据行号和账号查询客户信息
	 * @param 收款人账号
	 * @param 收款人行号
	 * @return 客户信息
	 */
	public CustomerDto qryCustInfoByActsAndBkcd(String acts,String bkcd)throws Exception{
		if(acts==null||acts.equals("")||bkcd==null||bkcd.equals(""))
			throw new Exception("####错误：根据行号和账号查询客户信息提供参数为空！####");
		String hql="select cdto from CustomerDto as cdto where cdto.pkIxBoCustomerId=(" +
				"select SCustId from AccountDto as adto where adto.SAccountNo=? and adto.SCustBankCode=?)";
		List list=this.find(hql,new Object[]{acts,bkcd});
		CustomerDto dto=list!=null&&list.size()>0?(CustomerDto)list.get(0):new CustomerDto();
		return dto;
	}
	/**
	 * 根据账号查询客户信息
	 * @param 收款人账号
	 * @param 收款人行号
	 * @return 客户对象
	 */
	public CustomerDto qryCustInfoByActs(String acts)throws Exception{
		String hql = "SELECT cdto FROM CustomerDto as cdto, AccountDto as adto WHERE cdto.pkIxBoCustomerId = adto.SCustId and adto.SAccountNo = '" + acts + "'";
		List results = find(hql);
		return results.size() == 0?null:(CustomerDto)results.get(0);

	}

	/**
	 * 根据客户ID信息
	 * @param 客户ID
	 * @return 账号信息集合
	 */
	public List qryAccountInfoByActsAndBkcd(String pri)throws Exception{
		List param = new ArrayList(); param.add(pri);
		String hql = "SELECT dto FROM AccountDto  dto WHERE dto.SCustId = ?  order by dto.pkAccountId";
		return this.find(hql, param);

	}

	public void txdelCustAccountByActsAndBkcd(String pri) throws Exception {
		//删除用户信息
		this.txDelete(this.dao.load(CustomerDto.class, pri));
		//删除账号信息
		List list = qryAccountInfoByActsAndBkcd(pri);
		for(int m = 0; m < list.size() ; m++ ){
			txDelete(list.get(m));
		}
	}
	/**
	 * 通过ID 查询 客户信息
	 * @param id
	 * @return
	 */
	public CustomerDto queryCustomerDtoById(String id){
		return (CustomerDto)this.dao.load(CustomerDto.class, id);
	}
	public CustomerDto loadByNetbankAccount(String netBankAccount) throws Exception{
		String sql = "select customer from CustomerDto customer,AccountDto account where account.SCustId=customer.pkIxBoCustomerId and account.SAccountNo = ?";
		List param = new ArrayList();
		param.add(netBankAccount);
		List result = this.find(sql, param);
		return (null != result && result.size() > 0)?(CustomerDto) result.get(0):null;
	}

	public CustomerDto getCustomerByAccountBankCodeNum(String accountNo,String bankCodeNum) {
		String sql = "select customer from CustomerDto customer,AccountDto account " +
				"where account.SCustId=customer.pkIxBoCustomerId and account.SAccountNo = ? and account.SCustBankCode =?";
		List param = new ArrayList();param.add(accountNo);param.add(bankCodeNum);
		List result = this.find(sql, param);
		return (null != result && result.size() > 0)?(CustomerDto) result.get(0):null;
	}

	public CustomerDto getCustomerByCommOrgCodeFromCoreSys(String commOrg) throws Exception{
		if(null == commOrg || commOrg.trim().length() == 0){
			return null;
		}
		/*ReturnMessage rm = new ReturnMessage();
		String sequence = sequenceService.getCoreInterfaceSequence();	        //得到流水号
		rm.getHead().put(Constants.PP011, sequence);				            //流水号
		rm.getHead().put(Constants.PP013, msgCommonService.getWorkDateTime());	//登记日期
		rm.getHead().put(Constants.PP012, msgCommonService.getWorkDateTime());	//登记时间
		rm.getHead().put(Constants.PP114, commOrg);					//客户组织机构代码
		rm.getHead().put(Constants.PP002, "");						//客户账号

		// 调用核心处理请求
		ReturnMessage response = null;//coreClient.process("4103", rm);	//调用核心请求

		String responsecode = (String)response.getHead().get(Constants.PP039);	//返回码

		if(Constants.RESPONSE_CODE_0000.equals(responsecode) ||
				Constants.RESPONSE_CODE_CF04.equals(responsecode) || "59**".equals(responsecode)){
			CustomerDto customer = new CustomerDto();				//客户对象
			customer.setSLiceCode((String)response.getHead().get(Constants.PP034));	//营业执照号码
			String taxNum = (String)response.getHead().get(Constants.PP101);		//税务证号
			customer.setSCustName((String)response.getHead().get(Constants.PP104));	//客户名称
			Date custFoundDate = (Date)response.getHead().get(Constants.PP073);		//客户成立日期

			String sPP115 = (String)response.getHead().get(Constants.PP115);
			if(sPP115!=null){
				byte[] PP115 = sPP115.getBytes();			//pp115	16	 S11501	1	 S11502 1   S11503 2	S11504	2	S11505	3	S11506 3  S11507 4	
				String basicBankNum = MessageUtil.substring(PP115, 12, 4).trim();	             //基本账户银行
				String ecoOrgType = MessageUtil.substring(PP115, 5, 3).trim();		             //经济组织类型
				String industryKind = MessageUtil.substring(PP115, 5, 3).trim();		    	 //行业分类
				String custCorporateCredentialType = MessageUtil.substring(PP115, 0, 1).trim();  //客户法人代表证件类型
			}

			String currencyType = (String)response.getHead().get(Constants.PP049); 	 //注册资金币种

			String sPP114 = (String)response.getHead().get(Constants.PP114);
			if(sPP114!=null){
				byte[] PP114 = sPP114.getBytes();	
				BigDecimal regisFundAmount = (BigDecimal)valueOfBigDecimal(MessageUtil.substring(PP114, 154, 19).trim(), "2");		//注册资金金额											//注册资金金额
				String regionCode = MessageUtil.substring(PP114, 148, 6).trim();													//地区代码
				String custRegisterAddr = MessageUtil.substring(PP114, 45, 52).trim();												//客户注册地址
				String custCorporateCredentialId = MessageUtil.substring(PP114, 15, 20).trim();									    //客户法人代表证件号码
			}

			customer.setSCustPost((String)response.getHead().get(Constants.PP084));	 //邮编

			String sPP119 = (String)response.getHead().get(Constants.PP119);
			if(sPP119!=null){
				byte[] PP119 = sPP119.getBytes();
				String custWorkAddr = MessageUtil.substring(PP119, 98, 52).trim();	          //客户经营地址
				customer.setSCustTele(MessageUtil.substring(PP119, 230, 20).trim()); 		  //客户联系电话
				String corporateName = (String)response.getHead().get(Constants.PP095);	 	  //客户法人代表姓名
			}

			return customer;
		}*/
		return null;
	}

	public String getCustomerStateByAccount(String accountNo) throws Exception{
		if(null == accountNo || accountNo.trim().length() == 0){
			return null;
		}
		/*ReturnMessage rm = new ReturnMessage();
		String sequence = sequenceService.getCoreInterfaceSequence();		//得到流水号
		rm.getHead().put(Constants.PP011, sequence);						//流水号
		rm.getHead().put(Constants.PP013, msgCommonService.getWorkDateTime());	//登记日期
		rm.getHead().put(Constants.PP012, msgCommonService.getWorkDateTime());	//登记时间
		rm.getHead().put(Constants.PP002, accountNo);				//账号
		rm.getHead().put(Constants.PP027, "2");						//查询类型
		rm.getHead().put(Constants.PP119, "");						//组织机构代码(因查询类型为2，所以传空)


		// 调用核心处理请求
		ReturnMessage response = null;//coreClient.process("4103", rm);	//调用核心请求

		String responsecode = (String)response.getHead().get(Constants.PP039);	//返回码
		if(Constants.RESPONSE_CODE_0000.equals(responsecode)){
			return (String)response.getHead().get(Constants.PP066);
		}else{
			//处理账户不存在的情况
		}*/
		return "#";
	}

	public String getCustomerStateByAccountBJBank(String accountNo) throws Exception{
		if(null == accountNo || accountNo.trim().length() == 0){
			return null;
		}
		/*ReturnMessage response = this.getCustomerState(accountNo);

		String responsecode = (String)response.getHead().get(Constants.PP039);	//返回码
		if(Constants.RESPONSE_CODE_0000.equals(responsecode) && ((String)response.getHead().get(Constants.PP066)).equals("1")){
			return (String)response.getHead().get(Constants.PP090);
		}else{
			return null;
		}*/
		return null;
	}
	public boolean getCustomerStateByAccountAndBankCodeBJBank(String accountNo, String bankCode) throws Exception{
		if(null == accountNo || accountNo.trim().length() == 0){
			return false;
		}
		/*ReturnMessage response = this.getCustomerState(accountNo);//向核心发送报文，得到返回

		String responsecode = (String)response.getHead().get(Constants.PP039);	//返回码
		if(Constants.RESPONSE_CODE_0000.equals(responsecode) && ((String)response.getHead().get(Constants.PP066)).equals("1")){
			String innerBankCodeTemp =  (String)response.getHead().get(Constants.PP090);//内部行号
			String innerBankCode = innerBankCodeTemp.substring(1, innerBankCodeTemp.length());//因配置文件中定义的是bigdecimal(6,0),需取后五位
			Department d = this.departmentService.getDepartmentByInnerBankCode(innerBankCode);//通过内部网点号码拿到机构对象
			if(d == null){//机构查不到
				return false;
			}
			if(StringUtil.equals(bankCode, d.getBankNumber())){//机构大额支付系统行号与传入行号相符
				return true;
			}
			return false;
		}else{
			return false;
		}*/
		return false;
	}
	/*private ReturnMessage getCustomerState(String accountNo) throws Exception{
		ReturnMessage rm = new ReturnMessage();
		String sequence = sequenceService.getCoreInterfaceSequence();		//得到流水号
		rm.getHead().put(Constants.PP011, sequence);						//流水号
		rm.getHead().put(Constants.PP013, msgCommonService.getWorkDateTime());	//登记日期
		rm.getHead().put(Constants.PP012, msgCommonService.getWorkDateTime());	//登记时间
		rm.getHead().put(Constants.PP002, accountNo);				//账号
		rm.getHead().put(Constants.PP027, "2");						//查询类型
		rm.getHead().put(Constants.PP119, "");						//组织机构代码(因查询类型为2，所以传空)


		// 调用核心处理请求
		ReturnMessage response = null;//coreClient.process("4105", rm);	//调用核心请求
		return response;

	}*/
	public Object valueOfBigDecimal(String value, String format){
		if(value == null) return null;
		//解码
		String sText = value;
		if(value.indexOf(".") != -1)
			return new BigDecimal(sText);
		int decimalStartIndex = sText.length() - Integer.parseInt(format);
		String decimalPart = sText.substring(decimalStartIndex,sText.length());
		String intPart = sText.substring(0,decimalStartIndex);
		intPart = intPart.replaceAll("^0+", "");
		intPart = intPart.length() == 0?"0":intPart;
		return new BigDecimal(intPart + "." + decimalPart);
	}
	/**
	 * 根据账号  开户行行号  查询 客户信息
	 * @param acctNo 账号 
	 * @param bankNo 行号
	 * @return
	 */
	public CustomerDto queryCustInfoByAccountAndBankNo(String acctNo,String bankNo)  {
		String hql = "select customer from CustomerDto customer,AccountDto account " +
		" where account.SCustId=customer.pkIxBoCustomerId and account.SAccountNo= ? and account.SCustBankCode =?";
		List param = new ArrayList();
		param.add(acctNo);
		param.add(bankNo);
		List result = this.find(hql, param);
		if(result!=null && result.size()>0){
			return (CustomerDto) result.get(0);
		}
		return null;
	}
	
	public CustomerDto queryCustInfoAndCustAccout(String commOrg,String bankCode) throws Exception {
		String hql = "select customer from CustomerDto customer,AccountDto account " +
		"where account.SCustId=customer.pkIxBoCustomerId and customer.SOrgCode = ? and account.SCustBankCode =?";
		List param = new ArrayList();param.add(commOrg);param.add(bankCode);
		List result = this.find(hql, param);
		CustomerDto customerDto=(null != result && result.size() > 0)?(CustomerDto) result.get(0):null;
		if(null !=customerDto){
			hql="select accountDto.SAccountNo From AccountDto accountDto where accountDto.SCustId=? and accountDto.SCustBankCode =?";
			param.clear();param.add(customerDto.getPkIxBoCustomerId());param.add(bankCode);
			result = this.find(hql, param);
			String accountNo=(null != result && result.size() > 0)?(String) result.get(0):null;
			customerDto.setSCustAccout(accountNo);
		}
		return customerDto;
	}
	
	/**
	 *<p>getCustomerTrees |描述：获得企业客户树</p>
	 *@param tree 树节点
	 *@param user 当前用户
	 */
	public String getCustomerTrees(Tree tree,User user) throws JSONException{
		List children =new ArrayList();
		if (StringUtil.equals(tree.getId(), "-1")) {// 取根节点
			children = this.getAllCustChildren(null,user);

		} else {
			//children = this.getAllChildren(department.getId(), 1,dept);
		}
		StringBuffer sb = new StringBuffer();
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		} else {
			List list = new ArrayList();
			for (int i = 0; i < children.size(); i++) {
				CustomerDto child = (CustomerDto) children.get(i);
				CustomerDtoNode tmp = new CustomerDtoNode(child);
				tmp.setText(child.getSCustName());
				tmp.setLeaf(true);
				list.add(tmp);
			}
			sb.append(JsonUtil.fromCollections(list));
		}
		return sb.toString();
	}
	
	public List getAllCustChildren(String pid,User user) {
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		sb.append("from CustomerDto cust WHERE 1=1");
		return this.find(sb.toString(), paras);
	}

	public void setDepartmentService(DepartmentService departmentService){
		this.departmentService = departmentService;
	}

	public String getClassificationNameBySClassification(String code) {

		String hql = "select SClassification from CompanyClassifyDto companyClassifyDto where companyClassifyDto.SClassifyNumber='"
				+ code + "'";
		List list = this.find(hql);
		String classifyName = "";
		if(list.size()>0)
			classifyName = list.get(0).toString();
		else
			classifyName = "没找到对应数据，请重新编辑";
		return classifyName;
	}
	
	public CustomerDto queryCusterByName(String name) {
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select customer from CustomerDto as customer where 1=1");
		if (null != name) {
			sb.append(" and customer.SCustName =?");
			param.add(name.trim());
		}
		List result = this.find(sb.toString(), param);
		return (null != result && result.size() > 0) ? (CustomerDto) result
				.get(0) : null;
	}
	public List queryCusterByRate(float rate) {
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select customer from CustomerDto as customer where customer.SCustRate =?");
		
		param.add(new Float(rate));
	
		List result = this.find(sb.toString(), param);
		return result;
	}
	public List queryCusterBySCustLevel(String sCustLevel) {
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select customer from CustomerDto as customer where customer.SCustLevel =?");
		if(null!=sCustLevel){
			sb.append(" and customer.SCustLevel =?");
			param.add(sCustLevel);
		}
		List result = this.find(sb.toString(), param);
		return result;
	}
	
	public CustomerDto queryCustomerByCoreCustCode(String coreCustomerCode){
		if(coreCustomerCode==null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select customer from CustomerDto as customer where customer.coreCustomerCode = ?");
		param.add(coreCustomerCode);
		List result = this.find(sb.toString(), param);
		if(result!=null && result.size()>0){
			return (CustomerDto)result.get(0);
		}
		return null;
	}
	
	public Map txSaveHbAccountInfo(CustomerDto customer,AccountDto account)throws Exception{
		Map map = new HashMap();
		CustomerDto customer1 = this.loadCustomerByOrgCode(customer);
		AccountDto dto2 = accountService.queryByAccountNo4Pool(account,true);
		if(null != dto2){//如果存在此账号
			customer1.setSCustName(customer.getSCustName());
			this.txStore(customer1);
			logger.error("客户的代保管托收账户或票据池保证金账户修改"+new Date());
			map.put("success", "ture");
			map.put("msg","客户修改成功！");
			return map;
		}
		if(null != customer1){//存在此组织机构代码
			logger.info("已存在组织机构代码:"+customer.getSOrgCode());
			if(PoolComm.ACCT_BZJC.equals(account.getSAccountType()) || PoolComm.ACCT_JS.equals(account.getSAccountType())){
				account.setSCustId(customer1.getPkIxBoCustomerId());
				AccountDto dto1 = accountService.queryByAccountNo(account);//获取该账户客户信息
				AccountDto dto3 = accountService.queryByAccountNo4Pool(account,false);//查看该组织机构是否已存在代保管或票据池账户
				if(null != dto3){
					throw new Exception ("客户已存在代保管托收账户或票据池保证金账户！") ; 
				}
				if(null != dto1){//如果存在此账号
					logger.info("已存在账号，为更新");
					dto1.setSCustId(customer1.getPkIxBoCustomerId());
					dto1.setSCustBankCode(account.getSCustBankCode());
					dto1.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto1.setSAccountNo(account.getSAccountNo());
					dto1.setSLoginName(customer.getSOrgCode());//登录名
					dto1.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto1.setSAccountType(account.getSAccountType());//2012-04-26 wg
					this.txStore(dto1);
					map.put("success", "ture");
					map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；更新账号："+account.getSAccountNo()+"成功");
				}else{
					AccountDto dto = new AccountDto();// 客户账号表AccountDto
					dto.setSCustId(customer1.getPkIxBoCustomerId());
					dto.setSCustBankCode(account.getSCustBankCode());
					dto.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto.setSAccountNo(account.getSAccountNo());
					dto.setSLoginName(customer.getSOrgCode());//登录名
					dto.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto.setSAccountType(account.getSAccountType());
					map.put("success", "ture");
					if(PoolComm.ACCT_BZJC.equals(account.getSAccountType()) ){
						map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；添加保证金账号："+account.getSAccountNo()+"成功");
					}else{
						map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；添加结算账号："+account.getSAccountNo()+"成功");
					}
					
					this.txStore(dto);
				}
			}else{
				AccountDto dto1 = accountService.queryByAccountNo(account);
				if(null != dto1){//如果存在此账号
					logger.info("已存在账号，为更新");
					dto1.setSCustId(customer1.getPkIxBoCustomerId());
					dto1.setSCustBankCode(account.getSCustBankCode());
					dto1.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto1.setSAccountNo(account.getSAccountNo());
					dto1.setSLoginName(customer.getSOrgCode());//登录名
					dto1.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto1.setSAccountType(account.getSAccountType());//2012-04-26 wg
					this.txStore(dto1);
					map.put("success", "ture");
					map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；更新账号："+account.getSAccountNo()+"成功");
				}else{//不存在此账号
					AccountDto dto = new AccountDto();// 客户账号表AccountDto
					dto.setSCustId(customer1.getPkIxBoCustomerId());
					dto.setSCustBankCode(account.getSCustBankCode());
					dto.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto.setSAccountNo(account.getSAccountNo());
					dto.setSLoginName(customer.getSOrgCode());//登录名
					dto.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto.setSAccountType(account.getSAccountType());
					this.txStore(dto);
					map.put("success", "ture");
					map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；添加账号："+account.getSAccountNo()+"成功");
				}
			}
			customer1.setSCustName(customer.getSCustName());//户名
			customer1.setCoreCustomerCode(customer.getCoreCustomerCode());//核心客户号
			if("".equals(customer.getCrdtLevel())||"0"==customer.getCrdtLevel()){//客户信用级别
				//customer1.setCrdtLevel("3");//此处不需要设置默认值  07-13
			}
			customer1 =this.poolInit(customer1);//维护客户费率
			customer1.setSCustBankCode(account.getSCustBankCode());
			this.txStore(customer1);
		}else{//不存在此组织机构代码
			logger.info("不存在组织机构代码:"+customer.getSOrgCode());
			customer.setSCustName(customer.getSCustName());//户名
			customer.setSCustBankCode(account.getSCustBankCode());
			customer =this.poolInit(customer);//维护客户费率
			//customer.setCrdtLevel("3");//此处不需要设置默认值  07-13
			this.txStore(customer);// 客户信息表customer
			if(PoolComm.ACCT_BZJC.equals(account.getSAccountType()) || PoolComm.ACCT_JS.equals(account.getSAccountType())){
				account.setSCustId(customer.getPkIxBoCustomerId());
				AccountDto dto1 = accountService.queryByAccountNo(account);
				if(null != dto1){//如果存在此账号
					logger.info("已存在账号，为更新");
					dto1.setSCustId(customer.getPkIxBoCustomerId());
					dto1.setSCustBankCode(account.getSCustBankCode());
					dto1.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto1.setSAccountNo(account.getSAccountNo());
					dto1.setSLoginName(customer.getSOrgCode());//登录名
					dto1.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto1.setSAccountType(account.getSAccountType());//2012-04-26 wg
					this.txStore(dto1);
					map.put("success", "ture");
					map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；更新账号："+account.getSAccountNo()+"成功");
				}else{
					AccountDto dto = new AccountDto();// 客户账号表AccountDto
					dto.setSCustId(customer.getPkIxBoCustomerId());
					dto.setSCustBankCode(account.getSCustBankCode());
					dto.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto.setSAccountNo(account.getSAccountNo());
					dto.setSLoginName(customer.getSOrgCode());//登录名
					dto.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto.setSAccountType(account.getSAccountType());
					this.txStore(dto);
					map.put("success", "ture");
					if(PoolComm.ACCT_BZJC.equals(account.getSAccountType()) ){
						map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；添加保证金账号："+account.getSAccountNo()+"成功");
					}else{
						map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；添加结算账号："+account.getSAccountNo()+"成功");
					}
					
				}
			}else{
				AccountDto dto1 = accountService.queryByAccountNo(account);
				if(null != dto1){//如果存在此账号
					logger.info("已存在账号，为更新");
					dto1.setSCustId(customer.getPkIxBoCustomerId());//更新为新组织机构代码
					dto1.setSCustBankCode(account.getSCustBankCode());
					dto1.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto1.setSAccountNo(account.getSAccountNo());
					dto1.setSLoginName(customer.getSOrgCode());//登录名
					dto1.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto1.setSAccountType(account.getSAccountType());
					this.txStore(dto1);
					map.put("success", "ture");
					map.put("msg","组织机构代码:"+customer.getSOrgCode()+"；更新账号："+account.getSAccountNo()+"成功");
				}else{
					AccountDto dto = new AccountDto();// 客户账号表AccountDto
					dto.setSCustId(customer.getPkIxBoCustomerId());
					dto.setSCustBankCode(account.getSCustBankCode());
					dto.setSCustBankName(departmentService.getDepartmentByInnerBankNumber(account.getSCustBankCode()).getName());
					dto.setSAccountNo(account.getSAccountNo());
					dto.setSLoginName(customer.getSOrgCode());//登录名
					dto.setSPassword(SecurityEncode.EncoderByMd5(systemConfig.getInitPassword()));
					dto.setSAccountType(account.getSAccountType());
					this.txStore(dto);
					map.put("success", "ture");
					map.put("msg","添加组织机构代码:"+customer.getSOrgCode()+"成功；添加账号："+account.getSAccountNo()+"成功");
				}
			}
			
		}
		return map;
	}
	public String queryOrgCodeBySCustAcc(String sCustAcc) {
		String orgCode=null;
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select customer from CustomerDto  customer ,AccountDto ato where customer.pkIxBoCustomerId = ato.SCustId ");
		if (null != sCustAcc) {
			sb.append(" and ato.SAccountNo =?");
			param.add(sCustAcc.trim());
		}
		List result = this.find(sb.toString(), param);
		if(null != result && result.size() > 0){
			CustomerDto cus=(CustomerDto) result.get(0);
			orgCode=cus.getSOrgCode();
		}
		return orgCode;
	}
	
	public void txupdateCustByStoreage(CustomerDto customer1, String openFlag)
			throws Exception {
		customer1.setPlStorageStt(openFlag);
		this.txStore(customer1);
	}
	
	public void txUpdateCustPoolAmt(CustomerDto customer,String pkCustIds) throws Exception {
		String[] custId = pkCustIds.substring(0,pkCustIds.length()-1).split(",");
		for(int i=0;i<custId.length;i++){
			CustomerDto sCust = (CustomerDto)this.load(custId[i]);
			if(null==customer.getStorageLowAmt()){
				sCust.setStorageLowAmt(BigDecimal.ZERO);
			}else{
				sCust.setStorageLowAmt(customer.getStorageLowAmt());
			}
			if(null==customer.getStoragePayRate()){
				sCust.setStoragePayRate(BigDecimal.ZERO);
			}else{
				sCust.setStoragePayRate(customer.getStoragePayRate());
			}
			if(null==customer.getPlDraftLowAmt()){
				sCust.setPlDraftLowAmt(BigDecimal.ZERO);
			}else{
				sCust.setPlDraftLowAmt(customer.getPlDraftLowAmt());
			}
			if(null==customer.getPlDraftPayRate()){
				sCust.setPlDraftPayRate(BigDecimal.ZERO);
			}else{
				sCust.setPlDraftPayRate(customer.getPlDraftPayRate());
			}
			sCust.setStorageRateStartDate(customer.getStorageRateStartDate());
			sCust.setStorageRateEndDate(customer.getStorageRateEndDate());
			sCust.setPlDraftRateStartDate(customer.getPlDraftRateStartDate());
			sCust.setPlDraftRateEndDate(customer.getPlDraftRateEndDate());
			
			this.txStore(sCust);
		}
		
	}
	
	public void txUpdateAllCustPoolAmt(CustomerDto customer)throws Exception{
		Page page = new Page();
		page.setPageSize(Integer.MAX_VALUE);
		List result = this.query(null, page);
		Iterator it = result.iterator();
		List value = new ArrayList();
		while(it.hasNext()){
			CustomerDto sCust = (CustomerDto)it.next();
			if(null==customer.getStorageLowAmt()){
				sCust.setStorageLowAmt(BigDecimal.ZERO);
			}else{
				sCust.setStorageLowAmt(customer.getStorageLowAmt());
			}
			if(null==customer.getStoragePayRate()){
				sCust.setStoragePayRate(BigDecimal.ZERO);
			}else{
				sCust.setStoragePayRate(customer.getStoragePayRate());
			}
			if(null==customer.getPlDraftLowAmt()){
				sCust.setPlDraftLowAmt(BigDecimal.ZERO);
			}else{
				sCust.setPlDraftLowAmt(customer.getPlDraftLowAmt());
			}
			if(null==customer.getPlDraftPayRate()){
				sCust.setPlDraftPayRate(BigDecimal.ZERO);
			}else{
				sCust.setPlDraftPayRate(customer.getPlDraftPayRate());
			}
			sCust.setStorageRateStartDate(customer.getStorageRateStartDate());
			sCust.setStorageRateEndDate(customer.getStorageRateEndDate());
			sCust.setPlDraftRateStartDate(customer.getPlDraftRateStartDate());
			sCust.setPlDraftRateEndDate(customer.getPlDraftRateEndDate());
			value.add(sCust);
		}
		this.dao.storeAll(value);
	}

	public String getplStoreByCust(String pkIxBoCustomerId) throws Exception {
		String plStore = "";
		CustomerDto cust = (CustomerDto)this.load(pkIxBoCustomerId);
		if(null != cust){
			plStore = cust.getPlStorageStt();
		}
		return plStore;
	}
	
	public void txUpdateAllCustPoolDisRate(CustomerDto customer)throws Exception{
		Page page = new Page();
		page.setPageSize(Integer.MAX_VALUE);
		List result = this.query(null, page);
		Iterator it = result.iterator();
		List value = new ArrayList();
		while(it.hasNext()){
			CustomerDto sCust = (CustomerDto)it.next();
			if(customer.getDefDisRate() != null && !"".equals(customer.getDefDisRate())){
				sCust.setDefDisRate(customer.getDefDisRate().divide(new BigDecimal(100)));
			}else{
				sCust.setDefDisRate(null);
			}
			if(customer.getDefDisRateStartDate() != null && !"".equals(customer.getDefDisRateStartDate())){
				sCust.setDefDisRateStartDate(customer.getDefDisRateStartDate());
			}else{
				sCust.setDefDisRateStartDate(null);
			}
			if(customer.getDefDisRateEndDate() != null && !"".equals(customer.getDefDisRateEndDate())){
				sCust.setDefDisRateEndDate(customer.getDefDisRateEndDate());
			}else{
				sCust.setDefDisRateEndDate(null);
			}
			value.add(sCust);
		}
		this.dao.storeAll(value);
	}

	public void txUpdateCustPoolDisRate(CustomerDto customer, String pkCustIds)
			throws Exception {
		
		CustomerDto sCust = (CustomerDto)this.load(pkCustIds);
		if(customer.getDefDisRate() != null && !"".equals(customer.getDefDisRate())){
			sCust.setDefDisRate(customer.getDefDisRate().divide(new BigDecimal(100)));
		}else{
			sCust.setDefDisRate(null);
		}
		if(customer.getDefDisRateStartDate() != null && !"".equals(customer.getDefDisRateStartDate())){
			sCust.setDefDisRateStartDate(customer.getDefDisRateStartDate());
		}else{
			sCust.setDefDisRateStartDate(null);
		}
		if(customer.getDefDisRateEndDate() != null && !"".equals(customer.getDefDisRateEndDate())){
			sCust.setDefDisRateEndDate(customer.getDefDisRateEndDate());
		}else{
			sCust.setDefDisRateEndDate(null);
		}
		this.txStore(sCust);
	}

	public void setAccountService(AccountService accountService){
		this.accountService = accountService;
	}

	
	public void setSystemConfig(SystemConfig systemConfig){
		this.systemConfig = systemConfig;
	}
	/**
	 * @author yangyawei
	 * 
	 * 
	 */
	
	public CustomerDto loadCustomerByOrgCodeOrPK(CustomerDto dto)throws Exception{
		StringBuffer sql = new StringBuffer("select customer from " +
				"CustomerDto as customer where customer.SOrgCode=? ");
		List param = new ArrayList();
		param.add(dto.getSOrgCode());
		//if update
		if(dto.getPkIxBoCustomerId()!=null&&
				!dto.getPkIxBoCustomerId().equals("")){
			sql.append(" and customer.pkIxBoCustomerId=? ");
			param.add(dto.getPkIxBoCustomerId());
		}
		List result = this.find(sql.toString(), param);
		if (null != result && result.size() > 0) {
			return (CustomerDto) result.get(0);
		}
		return null;
	}
	
	public Map txSaveHbCustomerInfo(CustomerDto customer)throws Exception{
		Map map = new HashMap();
		CustomerDto customer1 = this.loadCustomerByOrgCodeOrPK(customer);
			customer1.setSCustName(customer.getSCustName());//户名
			customer1.setCrdtLevel(customer.getCrdtLevel());//客户级别
			customer1.setSCheckFlag(customer.getSCheckFlag());//查询查复
			
			try{
				this.txStore(customer1);
			}catch(Exception e){
				throw new Exception ("修改客户信息失败！");
			}
			
			map.put("success", "ture");
			map.put("msg","组织机构代码:"+customer.getSOrgCode()+"客户等级："+customer.getCrdtLevel()+"修改成功！");
			
			
		return map;
	}
	
	/**
	 * <p>票据池费率初始化 </p>
	 * @param customer
	 * @return 
	 */
	public CustomerDto poolInit(CustomerDto customer){
		if(null==customer.getStorageLowAmt()||"".equals(customer.getStorageLowAmt())){
			customer.setStorageLowAmt(new BigDecimal(50));
		}
		if(null==customer.getStoragePayRate()||"".equals(customer.getStoragePayRate())){
			customer.setStoragePayRate(new BigDecimal(0.05));
		}

		if(null==customer.getPlDraftLowAmt()||"".equals(customer.getPlDraftLowAmt())){
			customer.setPlDraftLowAmt(new BigDecimal(500));
		}
		if(null==customer.getPlDraftPayRate()||"".equals(customer.getPlDraftPayRate())){
			customer.setPlDraftPayRate(new BigDecimal(0.5));
		}
		
		return customer;
	}

	
	public List getUserByRoleUser(User roleUser,boolean riskAdmin) throws Exception {
		String detpId = roleUser.getDepartment().getId();
		StringBuffer sb = new StringBuffer(); 
		List returnList = new ArrayList();
		String role_xqy =ProjectConfig.getInstance().getkeyValue("role_xqy");//小企业管理员ID
		if(riskAdmin){
			sb.append("select  u.* from  t_user u " +
					" left join  T_USER_ROLE  ur on u.id=ur.userid" +
					" left join T_ROLE r on r.id=ur.roleid " +
					" where r.r_name in (select  r.r_name  from  t_user u " +
					" left join  T_USER_ROLE  ur on u.id=ur.userid" +
					" left join T_ROLE r on r.id=ur.roleid  where  u.id='" +roleUser.getId()+
					"'  and r.id in ("+role_xqy+")"+
					")" +
					"  and u.id !='" +roleUser.getId()+"'"+
					" and u.u_deptid= '" +roleUser.getDepartment().getId()+"'");
		}else{
			sb.append("select  u.* from  t_user u " +
					" left join  T_USER_ROLE  ur on u.id=ur.userid" +
					" left join T_ROLE r on r.id=ur.roleid " +
					" where r.r_name in (select  r.r_name  from  t_user u " +
					" left join  T_USER_ROLE  ur on u.id=ur.userid" +
					" left join T_ROLE r on r.id=ur.roleid  where  u.id='" +roleUser.getId()+
					"'  and r.id in ("+role_xqy+")"+
					")" +
					" and u.id !='" +roleUser.getId()+"'"+
					" and u.u_deptid= '" +roleUser.getDepartment().getId()+"'");
		}
		
		returnList = sessionDao.SQLQuery(sb.toString());
		return returnList;
	}
	
	
	public List getUserByRoleUser(User roleUser) throws Exception {
		String detpId = roleUser.getDepartment().getId();
		StringBuffer sb = new StringBuffer(); 
		List returnList = new ArrayList();
		sb.append("select  u.U_LOGINNAME from  t_user u " +
				" left join  T_USER_ROLE  ur on u.id=ur.userid" +
				" left join T_ROLE r on r.id=ur.roleid " +
				" where r.r_name like '%银承收件登记岗%'  " +
				" and u.u_deptid= '" +roleUser.getDepartment().getId()+"'");
		
		returnList = sessionDao.SQLQuery(sb.toString());
		return returnList;
	}
	
	public boolean getIfRoleUser(User roleUser) throws Exception {
		GenericHibernateDao genericHibernateDao = new GenericHibernateDao();
		String detpId = roleUser.getDepartment().getId();
		StringBuffer sb = new StringBuffer(); 
		List returnList = new ArrayList();
		boolean flag = false;
		String role_xqy =ProjectConfig.getInstance().getkeyValue("role_gly");//管理员权限，
	
			sb.append("select  u.*  from  t_user u " +
						" left join  T_USER_ROLE  ur on u.id=ur.userid" +
						" left join T_ROLE r on r.id=ur.roleid  where  u.id='" +roleUser.getId()+
						"'  and r.id in ("+role_xqy+")");

		
		returnList = sessionDao.SQLQuery(sb.toString());
		if(returnList.size()>0){
			flag=true;
		}
		return flag;
	}
	public List getJizhangRoleUser(User roleUser) throws Exception {
		String detpId = roleUser.getDepartment().getId();
		StringBuffer sb = new StringBuffer(); 
		List returnList = new ArrayList();
		sb.append("select  u.U_LOGINNAME from  t_user u " +
				" left join  T_USER_ROLE  ur on u.id=ur.userid" +
				" left join T_ROLE r on r.id=ur.roleid " +
				" where r.r_name like '%托收记账复核岗%'  " +
				" and u.u_deptid= '" +roleUser.getDepartment().getId()+"'");
		
		returnList = sessionDao.SQLQuery(sb.toString());
		return returnList;
		
	}
	
	public boolean getIfRoleFhcyUser(User roleUser) throws Exception {
		GenericHibernateDao genericHibernateDao = new GenericHibernateDao();
		String detpId = roleUser.getDepartment().getId();
		StringBuffer sb = new StringBuffer(); 
		List returnList = new ArrayList();
		boolean flag = false;
		String role_xqy =ProjectConfig.getInstance().getkeyValue("role_fhcyg");//分行查验岗
	
			sb.append("select  u.*  from  t_user u " +
						" left join  T_USER_ROLE  ur on u.id=ur.userid" +
						" left join T_ROLE r on r.id=ur.roleid  where  u.id='" +roleUser.getId()+
						"'  and r.id in ("+role_xqy+")");

		
		returnList = sessionDao.SQLQuery(sb.toString());
		if(returnList.size()>0){
			flag=true;
		}
		return flag;
	}
	/**
	 * 检查是否批量管理员
	 * @param roleUser
	 * @return
	 * @throws Exception
	 */
	public boolean getIfPLUser(User roleUser) throws Exception {
		GenericHibernateDao genericHibernateDao = new GenericHibernateDao();
		String detpId = roleUser.getDepartment().getId();
		StringBuffer sb = new StringBuffer(); 
		List returnList = new ArrayList();
		boolean flag = false;
		String role_plgl =ProjectConfig.getInstance().getkeyValue("role_plgl");//管理员权限，
	
			sb.append("select  u.*  from  t_user u " +
						" left join  T_USER_ROLE  ur on u.id=ur.userid" +
						" left join T_ROLE r on r.id=ur.roleid  where  u.id='" +roleUser.getId()+
						"'  and r.id in ("+role_plgl+")");

		
		returnList = sessionDao.SQLQuery(sb.toString());
		if(returnList.size()>0){
			flag=true;
		}
		return flag;
	}
	
	/**
	 * getCustAcctByCustIdAndAcctNo  根据客户Id和账号获得客户账号信息
	 * @param custId 客户id
	 * @param acctNo 账号
	 * @return AccountDto 账户对象
	 */
	public AccountDto getCustAcctByCustIdAndAcctNo(String custId,String acctNo){
		List param = new ArrayList(); 
		String hql = "FROM AccountDto  dto WHERE dto.SCustId = ?  and dto.SAccountNo = ?";
		param.add(custId);
		param.add(acctNo);
		List acctList = this.find(hql, param);
		return acctList.size() > 0?(AccountDto)acctList.get(0):null;
	}
	public List queryHistoeyOpt(String cifCode,String custOrgCode) throws Exception{
		//查询企业客户对象
		CustomerDto cust = null;
		String custHql = "from CustomerDto ct where 1=1 and ct.cifCode = ? and ct.SOrgCode = ?";
		List param = new ArrayList();
		param.add(cifCode);
		param.add(custOrgCode);
		List result = this.find(custHql, param);
		if(result.size() > 0){
			cust = (CustomerDto) result.get(0); 
		}
		
		List acctList = accountService.queryByCustomerId(cust.getPkIxBoCustomerId(),null);
		
		//查询客户承兑且未结清的票据
		StringBuffer sql = new StringBuffer();
		List keyList = new ArrayList(); // 要查询的字段列表
		List valueList = new ArrayList(); // 要查询的值列表	
		List stateList = new ArrayList(); // 要查询的票据状态列表
		sql.append("select  btBillInfo from BtBillInfo as btBillInfo where btBillInfo.SAcceptorAccount in (:SAcceptorAccount)");
		keyList.add("SAcceptorAccount");
		for(int i = 0;i<acctList.size();i++){
		AccountDto acc = (AccountDto) acctList.get(i);	
		stateList.add(acc.getSAccountNo()); // 添加账号
		}
		valueList.add(stateList);
		sql.append(" and btBillInfo.SECDSStatus not in ('000000')");
		List list = this.find(sql.toString(),(String[])keyList.toArray(new String[keyList.size()]),valueList.toArray());
		
		
		//查询客户为出票人且未结清的票据
		String hql1 = "select  btBillInfo from BtBillInfo as btBillInfo where btBillInfo.Drwrcmonid = :Drwrcmonid and btBillInfo.SECDSStatus not in ('000000')";
		
		List list1 = this.find(hql1,new String[]{"Drwrcmonid"},new Object[]{custOrgCode});
		
		//查询客户当前持有的未结清票据
		String hql2 = "select  btBillInfo from BtBillInfo as btBillInfo where btBillInfo.SOwnerOrgCode = :SOwnerOrgCode and btBillInfo.SECDSStatus not in ('000000')";
		
		List list2 = this.find(hql2,new String[]{"SOwnerOrgCode"},new Object[]{custOrgCode});
		
		//构造返回结果
		List records =new ArrayList();
		
		
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				records.add(list.get(i));
			}
		}
		
		if(list1.size() > 0){
			for(int i=0;i<list1.size();i++){
				records.add(list1.get(i));
			}
		}
		
		if(list2.size() > 0){
			for(int i=0;i<list2.size();i++){
				records.add(list2.get(i));
			}
		}
		
		return records;
	}

	/**
	 * 汉口银行客户信息登记表数据落库处理
	 * @param customer
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-17上午11:07:04
	 */
	public String txSaveCustomerRegister(CustomerRegister customer)
			throws Exception {
		String res = "";
		CustomerRegister custNew=this.loadCustomerRegister(customer);
		if(null==custNew){
			customer.setId(null);
			customer.setFirstSignDate(new Date());
			customer.setCreateDate(new Date());
			customer.setUpdateDate(new Date());
			this.txStore(customer);
			res = "1";//保存成功
		}else{
			res = "2";//已存在
		}
		return res;
	}

	
	/**
	 * 查询汉口银行客户信息登记表
	 * 
	 * @param CustomerRegister
	 * @return
	 * @throws Exception
	 */
	public CustomerRegister loadCustomerRegister(CustomerRegister customer) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select cust from CustomerRegister cust where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (customer != null) {
			if (StringUtils.isNotBlank(customer.getCustNo())) {//客户号
				sb.append(" and cust.custNo = :custNo ");
				keyList.add("custNo");
				valueList.add(customer.getCustNo());
			}
			if (StringUtils.isNotBlank(customer.getCustName())) {//客户名称
				sb.append(" and cust.custName = :custName ");
				keyList.add("custName");
				valueList.add(customer.getCustName());
			}
		}
		sb.append("order by cust.createDate desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		if(list.size()>0){
			return (CustomerRegister)list.get(0);
		}else{
			return null;
		}
	}
}