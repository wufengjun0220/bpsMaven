package com.mingtech.application.pool.infomanage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.infomanage.domain.AccountDto;
import com.mingtech.application.pool.infomanage.domain.AccountDtoRequestbean;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.pool.infomanage.service.AccountService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.SecurityEncode;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.common.util.SystemConfig;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 类说明：客户账户处理实现
 * @author huangshiqiang@ Jun 9, 2009
 */
public class AccountServiceImpl extends GenericServiceImpl implements
		AccountService{
	private static final Logger logger = Logger.getLogger(AccountServiceImpl.class);

	private SystemConfig systemConfig;// 系统配置文件，提供用户初始密码
	
	
	public SystemConfig getSystemConfig() {
		return systemConfig;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public Class getEntityClass(){
		return AccountDto.class;
	}

	public String getEntityName(){
		return StringUtil.getClass(AccountDto.class);
	}
	/**
	 * 银行代理接入 账号签约查询  
	 * 查询 客户为代理接入行、财务公司的 账号
	 * @param account
	 * @param acc
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List queryAccountListForBankAgent(CustomerDto account,AccountDto acc,User user, Page page){
		List keyList = new ArrayList();
		List valueList = new ArrayList();
		List requestList = new ArrayList();
		StringBuffer strHql = new StringBuffer();
		
		strHql.append( " select account,cust from AccountDto account,CustomerDto cust " +
				" where account.SCustId=cust.pkIxBoCustomerId and  cust.roleCode in('RC03','RC04') " );
		if(null != account){
			if( null != account.getSCustName() && account.getSCustName().trim().length() !=0){
				strHql.append( " and cust.SCustName like :SCustName");
				keyList.add("SCustName");
				valueList.add("%"+account.getSCustName()+"%");
			}
			if(StringUtils.isNotBlank(account.getSOrgCode())){
				strHql.append( " and cust.SOrgCode =:SOrgCode");
				keyList.add("SOrgCode");
				valueList.add(account.getSOrgCode());
			}
		}
		if( null != acc){
			if(StringUtils.isNotBlank(acc.getSAccountNo())){
				strHql.append( " and account.SAccountNo =:SAccountNo");
				keyList.add("SAccountNo");
				valueList.add(acc.getSAccountNo());
			}	
			if(StringUtils.isNotBlank(acc.getSignFlag())){
				strHql.append( " and account.signFlag =:signFlag ");
				keyList.add("signFlag");
				valueList.add(acc.getSignFlag()); 
			}
		}
		
		AccountDto accs = null;
		CustomerDto cust = null;
		strHql.append(" order by account.pkAccountId desc");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List ifoList = this.find(strHql.toString(),keyArray,valueList.toArray(),page);
		
		for (int i = 0; i < ifoList.size(); i++) {
			Object []obj= (Object[])ifoList.get(i);
			accs =(AccountDto)obj[0];
			cust =(CustomerDto)obj[1];
			AccountDtoRequestbean bean =  new  AccountDtoRequestbean();
			try {
				BeanUtils.copyProperties(bean, cust);
				BeanUtils.copyProperties(bean, accs);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			} // copy两个对象函数
			bean.setSCustName(cust.getSCustName());
			bean.setSOrgCode(cust.getSOrgCode());
			requestList.add(bean);
		}
		return requestList;
	}
	
	public List queryByCustomerId(String customerId,Page pg){
		String hql =" from AccountDto acc where acc.SCustId=?";
		List param = new ArrayList();
		param.add(customerId);
		if(null != param && param.size()>0){
			return this.find(hql,param,pg);
		}else{
			return null;
		}
	}
	public List queryByCustomerId(String customerId, Page pg, User user){
		String sql = "select account from AccountDto as account where account.SCustId=?";
		List param = new ArrayList();
		param.add(customerId);
		if(null != user){
//			sql = sql +" and account.SCustBankCode=? and account.SAccountType!="+PublicStaticDefineTab.ACCT_BZJC+"";
//			param.add(user.getDepartment().getBankNumber());
			sql = sql +" and account.SAccountType!="+PoolComm.ACCT_BZJC+"";
		}
		if(null != pg){
			return this.find(sql, param, pg);
		}else{
			return this.find(sql,param);
		}
	}
	
	public AccountDto queryByAccountNo(AccountDto account){
		String sql = "select account from AccountDto as account where account.SAccountNo=?";
		List param = new ArrayList();
		param.add(account.getSAccountNo());
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (AccountDto)this.find(sql,param).get(0);
		}
		return null;
	}
	
	public AccountDto queryByAccountNo4Pool(AccountDto account,boolean ifAccount){
		String sql = "";
		if(ifAccount){
			sql = "select account from AccountDto as account where   (account.SAccountType='1' OR account.SAccountType='2')  and account.SAccountNo=? ";
		}else{
			sql = "select account from AccountDto as account where   account.SAccountType=? and account.SCustId=? ";
		}
		List param = new ArrayList();
		
		if(ifAccount){
			param.add(account.getSAccountNo());
		}else{
			param.add(account.getSAccountType());
			param.add(account.getSCustId());
		}
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (AccountDto)this.find(sql,param).get(0);
		}
		return null;
	}
	
	public List queryAccountByLoginName(String loginName,String SCustBankCode,Page pg){
		String sql = "select account from AccountDto as account where account.SLoginName = ? and account.SCustBankCode = ? ";
//		String sql = "select account from AccountDto as account where account.SLoginName = ? ";
		List param = new ArrayList();
		param.add(loginName);
		param.add(SCustBankCode);
		if(null != pg){
			return this.find(sql, param, pg);
		}else{
			return this.find(sql,param);
		}
	}
	
	public void txDeleteAccountByIds(String ids) throws Exception{
		String tmp[] = ids.split(",");
		for(int i=0;i<tmp.length;i++){
			AccountDto ac = (AccountDto)this.dao.load(AccountDto.class, tmp[i]);
			CustomerDto customer = (CustomerDto)this.dao.load(CustomerDto.class,ac.getSCustId() );
			if("1".equals(ac.getSAccountType())){
				if("DOP_01".equals(customer.getDrftOpnStt())){
					throw new Exception("客户正在使用票据池业务，不可删除！");
				}
			}
			if("2".equals(ac.getSAccountType())){
				if("PST_01".equals(customer.getPlStorageStt())){
					throw new Exception("客户正在使用代保管业务，不可删除！");
				}
			}
			
			
			//删除用户信息
			this.txDelete(this.dao.load(AccountDto.class, tmp[i]));
		}
	}
	
	public List getCustomerInfo(String custOrg) throws Exception{
		StringBuffer hql = new StringBuffer();
		hql.append("select new com.mingtech.application.pool.infomanage.domain.CustomerAccount(cd.SCustName,ac.SAccountNo) from AccountDto ac,CustomerDto cd where ac.SCustId=cd.pkIxBoCustomerId" +
				"  and cd.SOrgCode=:custOrg and ac.SAccountType!='"+PoolComm.ACCT_BZJC+"'" );
		List paramName = new ArrayList();
		List paramValue = new ArrayList();
		paramName.add("custOrg");
		paramValue.add(custOrg);
		List list =this.find(hql.toString(), (String[])paramName.toArray(new String[paramName.size()]), paramValue.toArray());
		return list;
		
	}
	public List getApplyAccountInfo(String SAccountNo,User user) throws Exception{
		StringBuffer hql = new StringBuffer();
		hql.append("select account from AccountDto account where account.SAccountNo=:SAccountNo  and account.SCustBankCode=:SCustBankCode and account.SAccountType!="+PoolComm.ACCT_BZJC);
		List paramName = new ArrayList();
		List paramValue = new ArrayList();
		paramName.add("SAccountNo");
		paramValue.add(SAccountNo);
		paramName.add("SCustBankCode");
		paramValue.add(user.getDepartment().getBankNumber());
		List list =this.find(hql.toString(), (String[])paramName.toArray(new String[paramName.size()]), paramValue.toArray());
		return list;
		
	}
	public AccountDto queryAccountByAcctId(String AccountNo) {
		String sql = "select account from AccountDto as account where account.SAccountNo=?";
		List param = new ArrayList();
		param.add(AccountNo);
		if(this.find(sql, param).size()>0){
		   return (AccountDto)this.find(sql, param).get(0);
		}else{
		   return null;
		}
	}
	
	public List queryAccountByCustId(String custId, String acctType)throws Exception {
		List res = new ArrayList();
		
		String hql = "select ac from AccountDto ac where "
					 +" ac.SCustId=:custId and ac.SAccountType=:acctType";
		
		List paramName = new ArrayList();
		List paramValue = new ArrayList();
		
		paramName.add("custId");
		paramValue.add(custId);
		
		paramName.add("acctType");
		paramValue.add(acctType);
		
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		
		res =  this.find(hql.toString(),paramNames,paramValues);
		
		return res;
	}
	public List queryAccountByCustOrg(String custOrg, String acctType) {
		List res = new ArrayList();
		
		String hql = "select ac from AccountDto ac,CustomerDto cd where ac.SCustId=cd.pkIxBoCustomerId"
					 +" and cd.SOrgCode=:custOrg and ac.SAccountType=:acctType";
		
		List paramName = new ArrayList();
		List paramValue = new ArrayList();
		
		paramName.add("custOrg");
		paramValue.add(custOrg);
		
		paramName.add("acctType");
		paramValue.add(acctType);
		
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		
		res =  this.find(hql.toString(),paramNames,paramValues);
		
		return res;
	}
	
	public String getOrgCodeByAccount(String accountNo) throws Exception {
		String orgCode = "";
		String hql = "select ct.SOrgCode from AccountDto at,CustomerDto ct where at.SCustId=ct.pkIxBoCustomerId"
					+ " and at.SAccountNo=?";
		List param = new ArrayList();
		param.add(accountNo);
		
		List res = this.find(hql, param);
		if(res != null && res.size()>0){
			orgCode = String.valueOf(res.get(0));
		}
		return orgCode;
	}
	public String queryCustomerJSON(AccountDto account,CustomerDto customer, User user, Page page)throws Exception{
		List list = this.queryCustomerList(account, customer,user, page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);
		
	}
	/**
	* <p>方法名称: queryCustomerList|描述: 查询企业客户信息列表</p>
	* @param account 企业客户账户对象
	* @param customer 企业客户对象
	* @param user 当前用户
	* @param page 分页对象
	* @return
	* @throws Exception
	*/
	private List queryCustomerList(AccountDto account,CustomerDto customer, User user, Page page) throws Exception{
		StringBuffer sb = new StringBuffer();
		//sb.append("select new CustomerDto(cm.SCustName, '', account.SAccountNo,cm.SOrgCode) from AccountDto account, CustomerDto cm " +
		//		" where account.SCustId = cm.pkIxBoCustomerId and cm.finaType='0' and account.SCustBankCode = :SCustBankCode ");
		sb.append("select account,cm from AccountDto account, CustomerDto cm " +
		" where account.SCustId = cm.pkIxBoCustomerId and cm.finaType='0' " );
//				+
//		"and account.SCustBankCode = :SCustBankCode ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
//		keyList.add("SCustBankCode"); // 大额行号
//		valueList.add(user.getDepartment().getBankNumber());
		if(customer != null){
			if(StringUtils.isNotBlank(customer.getSOrgCode())){ // 组织机构代码
				sb.append("and cm.SOrgCode = :SOrgCode ");
				keyList.add("SOrgCode"); // 大额行号
				valueList.add(customer.getSOrgCode().trim());
			}
		}
		
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		if(page != null){ // 有分页对象
			list = this.find(sb.toString(), keyArray, valueList.toArray(), page);
		}else{
		    list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		List newList = new ArrayList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				AccountDto acc = (AccountDto) ((Object[])list.get(i))[0];
				CustomerDto cus = (CustomerDto) ((Object[])list.get(i))[1];
				cus.setSCustAccout(acc.getSAccountNo());
				newList.add(cus);
			}
		}
		
		
		return newList;
		
	}
	
	/**
	 * 初始化加载客户账号信息列表
	 */
	public List queryAccountList(CustomerDto account,AccountDto acc,User user, Page page)throws Exception{
		List keyList = new ArrayList();
		List valueList = new ArrayList();
		List requestList = new ArrayList();
		StringBuffer strHql = new StringBuffer();
		AccountDto accs = null;
		CustomerDto cust = null;
		
		strHql.append( " select account,cust from AccountDto account,CustomerDto cust where account.SCustId=cust.pkIxBoCustomerId " );
		
		if(null != account){ // 客户账户对象
			
			if( null != account.getSCustName() && account.getSCustName().trim().length() !=0){//客户名称
				strHql.append( " and cust.SCustName like :SCustName");
				keyList.add("SCustName");
				valueList.add("%"+account.getSCustName()+"%");
			}
			
			if(StringUtils.isNotBlank(account.getSOrgCode())){// 组织机构代码
				strHql.append( " and cust.SOrgCode =:SOrgCode");
				keyList.add("SOrgCode");
				valueList.add(account.getSOrgCode());
			}
			
			if(null !=account.getFinaType() && account.getFinaType().trim().length()>0){// 大客户类型   0:企业客户;1:同业客户
				strHql.append( " and cust.finaType =:finaType");
				keyList.add("finaType");
				valueList.add(account.getFinaType());
			}
			
			if(null != account.getRoleCode() && account.getRoleCode().trim().length()>0){ // 参与者类别 RC00接入行 、RC01企业、 RC02人民银行 、RC03被代理行、 RC04被代理财务公司、RC05接入财务公司 等
				strHql.append( " and cust.roleCode =:roleCode");
				keyList.add("roleCode");
				valueList.add(account.getRoleCode());
			}
		}
		
		if( null != acc){ // 客户账号对象
			
			if(StringUtils.isNotBlank(acc.getSAccountNo())){ // 客户账号
				strHql.append( " and account.SAccountNo =:SAccountNo");
				keyList.add("SAccountNo");
				valueList.add(acc.getSAccountNo());
			}	
			
			if(StringUtils.isNotBlank(acc.getSignFlag())){ // 账号签约标记   01已签约 00未签约 
				strHql.append( " and account.signFlag =:signFlag ");
				keyList.add("signFlag");
				valueList.add(acc.getSignFlag()); 
			}
		}
		
		strHql.append(" order by account.pkAccountId desc");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List ifoList = this.find(strHql.toString(),keyArray,valueList.toArray(),page); //返回结果集
		
		for (int i = 0; i < ifoList.size(); i++) {
			
			Object []obj= (Object[])ifoList.get(i);
			accs =(AccountDto)obj[0];
			cust =(CustomerDto)obj[1];
			AccountDtoRequestbean bean =  new  AccountDtoRequestbean();
			BeanUtils.copyProperties(bean, accs); // copy两个对象函数
			bean.setSCustName(cust.getSCustName());
			bean.setSOrgCode(cust.getSOrgCode());
			requestList.add(bean);
		}	
		return requestList;
	}
	
	/**
     * 根据账户id 查询账户信息列表
     * @returnn
     * @throws Exception
     */
	public AccountDtoRequestbean queryIdToAccountList(String id) throws  Exception{
		
		AccountDto acc =(AccountDto)this.load(id, AccountDto.class);
		AccountDtoRequestbean bean =  new  AccountDtoRequestbean();
		if(null != acc){
			BeanUtils.copyProperties(bean, acc); // copy两个对象函数
			CustomerDto cust = (CustomerDto)this.load(acc.getSCustId(),CustomerDto.class);
			if(null != cust){
				bean.setSCustName(cust.getSCustName());
				bean.setSCustId(cust.getPkIxBoCustomerId());
			}
		}
		
	    return 	bean;
	}
	
	public List queryIdToAccDeptList(Department dept,User user,String redisFlag,Page page)throws Exception{
		StringBuffer str = new StringBuffer();
		List valueList = new ArrayList();
		List sumlist = new  ArrayList();
		
		if(null == user.getDeptId()){
			str.append( "select dept from  Department dept where dept.parent is null");
		}else{
			str.append( "select dept from  Department dept where 1=1");// where dept.parent =?  注：这是查询所有的机构信息
			//valueList.add(user.getDeptId());
		}
		
		if(null != dept){
			
			if( null != dept.getName() && dept.getName().trim().length() >0){ // 机构名称
				str.append(" and dept.name like ? ");
				valueList.add("%"+dept.getName()+"%");
			}
			
			if( null != dept.getBankNumber() && dept.getBankNumber().trim().length() >0){ // 机构号
				str.append(" and dept.bankNumber =? ");
				valueList.add(dept.getBankNumber());
			}
		}
		if(null!= redisFlag &&redisFlag.equals("0")){
			str.append(" and dept.id !=? ");
			valueList.add(user.getDepartment().getId());
		}
		List zongList = this.find(str.toString(), valueList);

		/*sumlist.add(zongList);
		long size = 0;
		for (int i = 0; i < zongList.size(); i++) {
			size=this.getRowCount(str.toString(), valueList).longValue();
			if(size>0){
				List ziList = this.find(str.toString(), valueList);
				sumlist.add(ziList);
			}
		}*/
		
		return zongList;
	}
	
	/**
	 * 添加账户保存
	 * @throws Exception
	 */
	
	public String txSaveOrUpdateToAccountDto(String saveOrUapdateType,AccountDto acc) throws Exception{
		AccountDto account = new AccountDto();
		CustomerDto cust = new CustomerDto();
		String info = "";
		if("1".equals(saveOrUapdateType)){// 添加保存
			if(null != acc){
				cust=(CustomerDto)this.load(acc.getSCustId(),CustomerDto.class);
				if(null !=cust ){
//					account.setPkAccountId(acc.getPkAccountId());
					account.setSCustId(acc.getSCustId());
					account.setSAccountName(acc.getSAccountName());
					account.setSAccountNo(acc.getSAccountNo());
					account.setSAccountType(acc.getSAccountType());
					account.setSCustBankName(acc.getSCustBankName());
					account.setSCustBankCode(acc.getSCustBankCode());
					account.setSLoginName(cust.getSOrgCode());
					account.setSPassword(SecurityEncode.EncoderByMd5(systemConfig
							.getInitPassword()));
					this.txStore(account);
					return info = "保存账号信息成功!";
				}else{
					return info = "账户信息未维护!";
				}
			}
		}
		
		if("2".equals(saveOrUapdateType)){// 编辑保存
			account=(AccountDto)this.load(acc.getPkAccountId(),AccountDto.class);
			if( null != account){
				account.setSAccountName(acc.getSAccountName());
				account.setSAccountNo(acc.getSAccountNo());
				account.setSAccountType(acc.getSAccountType());
				account.setSCustBankName(acc.getSCustBankName());
				account.setSCustBankCode(acc.getSCustBankCode());
				this.txStore(account);
				return info = "更新账号信息成功!";
			}else{
				return info = "账号信息未维护!";
			}
		}
		
		return info = "保存账号信息失败!";
	}
	/**
	 * 删除单条账户信息
	 * @param acc
	 * @throws Exception
	 */
	public String txDeleteToAccountInfo(AccountDto acc) throws Exception{
		String info = "";
		AccountDto account = (AccountDto)this.load(acc.getPkAccountId(), AccountDto.class);
		if(null != account){
			this.txDelete(account);
			return info ="删除账户成功!";
		}else{
			return info = "删除账户失败!";
		}
	}
/*----------------------------三菱银行电票 start----------------------------*/
	
	private DepartmentService departmentService;

	

	public AccountDto queryByAccountNoForLast(AccountDto account){
		String sql = "select account from AccountDto as account where substr(account.SAccountNo,-6)=? and account.SCustBankCode=?";
		List param = new ArrayList();
		param.add(account.getSAccountNo());//账号
		param.add(account.getSCustBankCode());//开户行行号
		List result = this.find(sql, param);
		if (null != result && result.size() > 0) {
			return (AccountDto)this.find(sql,param).get(0);
		}
		return null;
	}

	public AccountDto loadAccountDtoForGLcodeAccount(String acts,String bkcd) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select account from AccountDto as account where substr(account.SAccountNo,-6)=? and account.SCustBankCode=? ");
		sql.append("and account.accountType=? ");
		List param = new ArrayList();
		if(StringUtils.equals(acts, "0") || acts.length()<6){
			param.add(acts);//银行账号
		}else{
		param.add(acts.substring(acts.length()-6, acts.length()));//账号
		}
		param.add(bkcd);//开户行行号
		List result = this.find(sql.toString(), param);
		
		return result.size()>0?(AccountDto)result.get(0):null;
	}
	
	
	public List queryByAccountList(AccountDto account){
		String sql = "select account from AccountDto as account where account.accountId=? and account.SCustBankCode=?";
		List param = new ArrayList();
		param.add(account.getSAccountNo());//账号
		param.add(account.getSCustBankCode());//开户行行号
		List result = this.find(sql, param);
		return result;
	}
	

	
	/*----------------------------三菱银行电票 start----------------------------*/
	
	public DepartmentService getDepartmentService() {
		return departmentService;
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
}
