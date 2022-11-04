package com.mingtech.application.pool.infomanage.service;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.mingtech.application.pool.infomanage.domain.AccountDto;
import com.mingtech.application.pool.infomanage.domain.AccountDtoRequestbean;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 类说明：客户信息Service
 * @author huangshiqiang@
 * Jun 9, 2009
 */
public interface CustomerService extends GenericService {
	/**
	 * 修改票据池客户部分信息
	 * @param customer 客户Dto
	 * @param user 操作人
	 */
	public String txsaveCustomerInfo(CustomerDto customer,User user) throws Exception;
	/**
	 * 银行代理接入  账号签约 
	 * @param user
	 * @param acct
	 * @throws Exception
	 */
	public void txCommitAcctSignOpen(User user,String id)throws Exception;
	/**
	 * 银行代理接入  账号解约
	 * @param user
	 * @param acct
	 * @throws Exception
	 */
	public void txCommitAcctSignClose(User user,String id)throws Exception;
	/**
	 * 根据查询条件  查询客户账号信息  
	 * @param user
	 * @param searchBean
	 * @param page
	 * @return   AccountDtoRequestbean 
	 */
	public List queryCustomerAccountList(User user,AccountDtoRequestbean searchBean,Page page);
	
	/**
	 * 根据账号  查询 账号详细信息
	 * @param acctNo 客户账号
	 * @return
	 */
	public AccountDto queryCustomerAccountByAcctNo(String acctNo) ;
	/**
	 * 根据账号  查询 账号详细信息
	 * @param acctNo 客户账号
	 * @return 返回一个复合对象，包含账号、客户信息；
	 */
	public AccountDtoRequestbean queryCustomerAcctByAcctNo(String acctNo);
	
	/**
	 * 通过ID 查询 客户信息
	 * @param id
	 * @return
	 */
	public CustomerDto queryCustomerDtoById(String id);
	/**
	 * 查询 生效 的  客户信息   
	 * @param user 当前柜员 
	 * @param customer 客户查询条件对象
	 * @param page
	 * @return
	 */
	public List queryCustomerList(User user ,CustomerDto customer, Page page) ;
	/**
	 * 根据组织机构代码查找客户记录
	 * @param orgCode 组织机构代码
	 * @return 客户信息对象
	 * @time:Jun 9, 2009
	 */
	public CustomerDto loadByOrgCode(String orgCode);
	
	/**
	 * 根据  客户 证件号码  查询客户信息
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public CustomerDto loadCustomerByCertCode(CustomerDto dto)throws Exception;
	/**
	 * @param pid
	 * @param user
	 * @return
	 */
	public List getAllCustChildren(String pid,User user);
	/**
	 * 客户登录
	 * @param customer
	 * @return 客户信息对象
	 * @time:Jun 9, 2009
	 */
	public AccountDto validateCustomer(AccountDto account);
	/**
	 * 根据  账号、账号开户行查询账号信息
	 * @param acct 账号
	 * @param bankNo 账号开户行行号
	 * @return
	 */
	public AccountDto queryCustAccountByAcctAndBankNo(String acct,String bankNo);
	/**
	 * 根据  账号查询账号信息
	 * @param acct 账号
	 * @return
	 */
	public AccountDto queryCustAccountByAcctNo(String acct);
	/**
	 * 查询客户信息
	 * @param customer 客户对象
	 * @param page 分页对象
	 * @param flag 如果为true则查询下属机构，并且只查客户信息表；
	 * 				，false则只查询本机构 客户信息表 关联 客户账号表联合查询
	 * @return string
	 * @throws Exception
	 * @time:Jun 12, 2009
	 */
	public String queryData(User user,CustomerDto customer,Page page,boolean flag)throws Exception;
	
	/**
	 * 查询客户信息
	 * @param customer 客户对象
	 * @param page 分页对象
	 * @return list
	 * @time:Jun 12, 2009
	 */
	public List query(CustomerDto customer,Page page);
	/**
	 * <p>根据客户的名称查询客户信息</p>
	 * @param name 客户名称
	 * @return CustomerDto
	 * @time:Jun 12, 2009
	 */
	public CustomerDto queryCusterByName(String name) ;
	/**
	 * 删除客户记录
	 * 将状态置成无效
	 * @param customer 客户对象
	 * @time:Jun 12, 2009
	 */
	public void txDelete(CustomerDto customer);
	
	/**
	 * 根据行号和账号查询客户信息
	 * @param 收款人账号
	 * @param 收款人行号
	 * @return 客户对象
	 */
	public CustomerDto qryCustInfoByActsAndBkcd(String acts,String bkcd)throws Exception;
	
	/**
	 * 根据账号查询客户信息
	 * @param 收款人账号
	 * @param 收款人行号
	 * @return 客户对象
	 */
	public CustomerDto qryCustInfoByActs(String acts)throws Exception;
	
	/**
	 * 根据客户ID信息
	 * @param 客户ID
	 * @return 账号信息集合
	 */
	public List qryAccountInfoByActsAndBkcd(String pri)throws Exception;
	
	/**
	 * 根据客户ID信息删除对应账号信息
	 * @param 客户ID
	 */
	public void txdelCustAccountByActsAndBkcd(String pri)throws Exception;
	
	/**
	 * 根据客户账号获取customer对象
	 * @param netBankAccount 客户账号
	 * @return customer 客户对象
	 */
	public CustomerDto loadByNetbankAccount(String netBankAccount) throws Exception;
	
	/**
	 * 根据客户账号.账号开户行 查询 客户信息
	 * @param accountNo
	 * @param bankCodeNum
	 * @return customer
	 */
	public CustomerDto getCustomerByAccountBankCodeNum(String accountNo,String bankCodeNum);
	
	/**
	 * core  4103
	 * 通过对公客户的组织机构代码从核心系统查询出相应客户的详细档案信息，客户信息新增及维护时调用（输入组织机构代码）
	* <p>方法名称: getCustomerByCommOrgCodeFromCoreSys|描述:通过对公客户的组织机构代码从核心系统查询出相应客户的详细档案信息，客户信息新增及维护时调用（输入组织机构代码） </p>
	* @param commOrg	  组织机构代码
	* @return CustomerDto 客户对象
	* @throws Exception
	 */
	public CustomerDto getCustomerByCommOrgCodeFromCoreSys(String commOrg) throws Exception;
	
	/**
	 * core 4104
	 * 向核心系统查询某一客户账号是否存在，并返回其账户状态。
	* <p>方法名称: getCustomerStateByAccount|描述:向核心系统查询某一客户账号是否存在，并返回其账户状态。 </p>
	* @param accountNo      查询账号
	* @return 账户状态(0－待开户1－正常2－封锁3－待销4－部冻5－只进6－只出7－不进不出*－销户)
	* @throws Exception
	 */
	public String getCustomerStateByAccount(String accountNo) throws Exception;
	/*北京银行 20090917*/
	public String getCustomerStateByAccountBJBank(String accountNo) throws Exception;
	/*通过帐号查询帐号状态，如状态为可用，通过核心返回的内部行号查询大额支付系统行号与传入行号比对北京银行20090921*/
	public boolean getCustomerStateByAccountAndBankCodeBJBank(String accountNo, String bankCode) throws Exception;
	
	/**初始化编辑页面时的企业类型数据
	 * @param code
	 * @return
	 */
	public String getClassificationNameBySClassification(String code);
	/**
	 * 根据账号  开户行行号  查询 客户信息
	 * @param acctNo 账号 
	 * @param bankNo 行号
	 * @return
	 */
	public CustomerDto queryCustInfoByAccountAndBankNo(String acctNo,String bankNo) ;
	/**
	 * queryCustInfoAndCustAccout 获得客户信息
	 * @param commOrg     组织机构代码
	 * @param bankCode    大额支付行号
	 * @return
	 * @throws Exception
	 */
	public CustomerDto queryCustInfoAndCustAccout(String commOrg,String bankCode)throws Exception;
	
	public CustomerDto loadCustomerByOrgCode(CustomerDto dto)throws Exception;
	/**
	 * queryCusterByRate 根据客户底限利率获得客户对象
	 * @param rate
	 * @return CustomerDto 客户对象
	 */
	public List queryCusterByRate(float rate) ;
	/**
	 * queryCusterByRate 根据客户规模获得客户对象
	 * @param sCustLevel
	 * @return CustomerDto 客户对象
	 */
	public List queryCusterBySCustLevel(String sCustLevel)  ;
	
	/**
	 *<p>getCustomerTrees |描述：获得企业客户树</p>
	 *@param tree 树节点
	 *@param user 当前用户
	 */
	public String getCustomerTrees(Tree tree,User user) throws JSONException;
	
	//河北银行
	public Map txSaveHbAccountInfo(CustomerDto customer,AccountDto account)throws Exception;
	/**
	 * <p></p>
	 * @param sCustAcc 客户名称
	 * @return CustomerDto
	 * @time:Jun 06, 2010
	 */
	
	public Map txSaveHbCustomerInfo(CustomerDto customer)throws Exception;
	
	/**
	 * <p>根据客户账号查询客户组织机构代码</p>
	 * @param sCustAcc 客户名称
	 * @return CustomerDto
	 * @time:Jun 06, 2010
	 */
	public String queryOrgCodeBySCustAcc(String sCustAcc) ;
	
	/**
	 * 根据核心客户号查询客户信息
	 * @param coreCustomerCode
	 * @return
	 */
	public CustomerDto queryCustomerByCoreCustCode(String coreCustomerCode);
	
	public void txupdateCustByStoreage(CustomerDto customer1, String openFlag)throws Exception;
	
	/**
	 * 保存票据池费率修改
	 * @param customer
	 * @param pkCustIds 
	 * @throws Exception
	 */
	public void txUpdateCustPoolAmt(CustomerDto customer, String pkCustIds)throws Exception;
	
	/**
	* <p>方法名称: txUpdateAllCustPoolAmt|描述: 修改全部费率</p>
	* @param customer
	* @param pkCustIds
	* @throws Exception
	*/
	public void txUpdateAllCustPoolAmt(CustomerDto customer)throws Exception;
	
	/**
	 * 根据客户 ID取
	 * @param pkIxBoCustomerId
	 * @return
	 * @throws Exception
	 */
	public String getplStoreByCust(String pkIxBoCustomerId)throws Exception;
	
	/**
	 * 更新打折率
	 * @param customer
	 * @param pkCustIds
	 * @throws Exception
	 */
	public void txUpdateCustPoolDisRate(CustomerDto customer, String pkCustIds)throws Exception;
	
	/**
	* <p>方法名称: txUpdateCustPoolDisRate|描述: 修改全部打折率</p>
	* @param customer
	* @throws Exception
	*/
	public void txUpdateAllCustPoolDisRate(CustomerDto customer)throws Exception;
	

	public CustomerDto loadCustomerByOrgCodeOrPK(CustomerDto dto)throws Exception;
	
	/**
	 * 查询同一机构风险管理员和公司管理员的其他人员
	 * @param user
	 * @param riskAdmin 是否风险管理员
	 * @return
	 * @throws Exception
	 */
	public List getUserByRoleUser(User roleUser,boolean riskAdmin) throws Exception;
	
	/**
	 * 查询同一机构银承收件登记岗的其他人员
	 * @param user
	 * @param riskAdmin 是否风险管理员
	 * @return
	 * @throws Exception
	 */
	public List getUserByRoleUser(User roleUser) throws Exception;
	public boolean getIfRoleUser(User roleUser) throws Exception;
	/**
	 * 查询同一机构记账复合人员
	 * @param user
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public List getJizhangRoleUser(User roleUser) throws Exception;
	
	public boolean getIfRoleFhcyUser(User roleUser) throws Exception;
	
	public boolean getIfPLUser(User roleUser) throws Exception ;
	
	/**
	 * getCustAcctByCustIdAndAcctNo  根据客户Id和账号获得客户账号信息
	 * @param custId 客户id
	 * @param acctNo 账号
	 * @return AccountDto 账户对象
	 */
	public AccountDto getCustAcctByCustIdAndAcctNo(String custId,String acctNo);
	
	/**
	 *<p>queryHistoeyOpt |描述：查询客户存在的未结清，未承兑，当前持有的票据</p>
	 *@param request 客户号和组织机构代码
	 */
	public List queryHistoeyOpt(String cifCode,String custOrgCode) throws Exception;
	
	public String queryData2() throws Exception;
	
	/**
	 * 汉口银行客户信息登记表数据落库处理
	 * @param customer
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-17上午11:07:04
	 */
	public String txSaveCustomerRegister(CustomerRegister customer) throws Exception;
	/**
	 * 查询汉口银行客户信息登记表
	 * 
	 * @param CustomerRegister
	 * @return
	 * @throws Exception
	 */
	public CustomerRegister loadCustomerRegister(CustomerRegister customer) throws Exception;
 
	
}