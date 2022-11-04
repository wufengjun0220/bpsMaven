package com.mingtech.application.pool.infomanage.service;

import java.util.List;

import com.mingtech.application.pool.infomanage.domain.AccountDto;
import com.mingtech.application.pool.infomanage.domain.AccountDtoRequestbean;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * 类说明：客户账户处理service
 * @author huangshiqiang@
 * Jun 9, 2009
 */
public interface AccountService extends GenericService {
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
	public List queryAccountListForBankAgent(CustomerDto account,AccountDto acc,User user, Page page);
	/**
	 * 根据客户id加载账号信息
	 * @param customerId 客户记录id
	 * @param user TODO
	 * @return
	 * @time:Jun 9, 2009
	 */
	public List queryByCustomerId(String customerId,Page pg, User user);
	public List queryByCustomerId(String customerId,Page pg);
	/**
	 * 根据账户加载信息
	 * @param AccountDto 
	 * @return
	 * @time:Jun 9, 2009
	 */
	public AccountDto queryByAccountNo(AccountDto account);
	
	/**
	 * 根据账户和账户类型加载信息
	 * @param account
	 * @return
	 */
	public AccountDto queryByAccountNo4Pool(AccountDto account,boolean ifAccount);
	/**
	 * 根据客户loginName加载账号信息
	 * @param loginName  登录名
	 * @param SCustBankCode  开户行行号
	 * @return
	 * @time:Jun 9, 2009
	 */
	public List queryAccountByLoginName(String loginName,String SCustBankCode,Page pg);
	
	//河北银行
	public void txDeleteAccountByIds(String ids) throws Exception;
	
	/**
	 * getCustomerInfo:根据客户组织结构码查询客户和账号信息
	 * @param custOrg
	 * @return
	 * @throws Exception
	 */
	public List getCustomerInfo(String custOrg) throws Exception;
	
	/**
	 * getApplyAccountInfo:根据账号查行号和名称
	 * @param SAccountNo
	 * @return
	 * @throws Exception
	 */
	public List getApplyAccountInfo(String SAccountNo,User user) throws Exception;
	/**
	 * 票据池增加-游锦
	 * 根据客户开户行行号加载账号住处
	 * @param customerId 客户开户行行号
	 * @return
	 * @time:Jun 9, 2009
	 */
	public AccountDto queryAccountByAcctId(String AccountNo);
	
	/**
	 * 根据客户id、账户类型查账户      add By yixiaolong
	 * @param custId
	 * @param acctType
	 * @return
	 * @throws Exception
	 */
	public List queryAccountByCustId(String custId, String acctType)throws Exception;
	
	/**
	 * 根据组织机构代码、账户类型查账户      add By yixiaolong
	 * @param custOrg  客户组织机构代码
	 * @param acctType  账户类型
	 * @return
	 */
	public List queryAccountByCustOrg(String custOrg,String acctType);
	
	/**
	 * 通过账户找组织机构代码
	 * @param accountNo
	 * @return
	 * @throws Exception
	 */
	public String getOrgCodeByAccount(String accountNo)throws Exception;
	
	/**
	* <p>方法名称: queryCustomerJSON|描述: 查询企业客户信息列表JSON</p>
	* @param account 企业客户账户对象
	* @param customer 企业客户对象
	* @param user 当前用户
	* @param page 分页对象
	* @return
	* @throws Exception
	*/
	public String queryCustomerJSON(AccountDto account, CustomerDto customer, User user, Page page) throws Exception;
    
	/**
	 * 初始化加载账号信息列表
	 */
	public List queryAccountList(CustomerDto account,AccountDto acc,User user, Page page)throws Exception;
    /**
     * 根据账号id 查询账号信息列表
     * @returnn
     * @throws Exception
     */
	public AccountDtoRequestbean queryIdToAccountList(String id) throws  Exception;
	/**
	 * 根据本机构id查询下属机构信息列表
	 * @param id  机构id
	 * @return
	 * @throws Exception
	 */
	public List queryIdToAccDeptList(Department dept,User user,String redisFlag,Page page)throws Exception; 
	/**
	 * 添加账号保存
	 * @param saveOrUapdateType 保存或更新
	 * @param acc               要保存或更新的数据对象
	 * @return
	 * @throws Exception        
	 */
	public String txSaveOrUpdateToAccountDto(String saveOrUapdateType,AccountDto acc) throws Exception;
	
	/**
	 * 删除单条账户信息
	 * @param acc
	 * @throws Exception
	 */
	public String txDeleteToAccountInfo(AccountDto acc) throws Exception;
	
	/*---------------三菱银行电票-----------*/
	/**
	* <p>方法名称: txSynAccountInfo|描述: 同步客户信息</p>
	* @param params
	* @throws Exception
	*/
	//public Map txSynAccountInfo(ReturnMessage msg) throws Exception;
	/*---------------三菱银行电票-----------*/
	
	
	/*---------------三菱银行电票二期-------------*/
	/**
	 * loadAccountDtoForGLcodeAccount 查询GLcode账号
	 * acts 账号
	 * bkcd 行号
	 * @return 客户账号信息
	 */
	public AccountDto loadAccountDtoForGLcodeAccount(String acts,String bkcd) throws Exception;
	/*---------------三菱银行电票二期-------------*/
}


