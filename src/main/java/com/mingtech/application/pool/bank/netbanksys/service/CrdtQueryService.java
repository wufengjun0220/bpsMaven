package com.mingtech.application.pool.bank.netbanksys.service;

import java.util.Date;
import java.util.List;
import com.mingtech.framework.core.service.GenericService;


/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: youjin
* @日期: Sep 25, 2010 9:40:14 AM
* @描述: [EBankService] 票据池业务查询接口类
*/
public interface CrdtQueryService extends GenericService{
	/**
	* <p>方法名称: queryUsedCrdtProducts|描述: 票据池-查询已使用信贷产品</p>
	* @param commonId 企业组织机构代码
	* @param draftNb 票号 
	* @param accptrSvcr 承兑人行号
    * @param isseDt 出票日 
	* @param bailAccount 保证金账号
	* @return 查询结果
	* @throws Exception
	*/
	public List queryUsedCrdtProducts(String commonId, String draftNb,
			String accptrSvcr, Date isseDt, String bailAccount)throws Exception;
	
	/**
	 * 票据池额度查询
	 * @param plCommId
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List findCustAseetPoolDetail(String plCommId)throws Exception;
	
	/**
	 * 保证金额度明细查询
	 * @param plCommId
	 * @param bailType TODO
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List findAssetDetailList(String plCommId, String bailType)throws Exception;
}
