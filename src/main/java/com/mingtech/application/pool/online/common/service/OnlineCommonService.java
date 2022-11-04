package com.mingtech.application.pool.online.common.service;

import java.math.BigDecimal;

import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.framework.core.service.GenericService;

public interface OnlineCommonService extends GenericService{

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 生成借据
	 * @param busiType 业务类型 001 银承 002流贷
	 * @param creditAcctNo 贷款账号
	 * @param obj 业务对象
	 */
	public void txSavePedCreditDetail(String busiType, String creditAcctNo,Object obj) throws Exception;
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-12
	 * @description 生成合同
	 */
	public CreditProduct txSaveCreditProduct(String busiType,Object obj,PlOnlineAcptDetail detail) throws Exception;

	
	/**
	 * 根据放款产品代码查询对应的贷款产品代码
	 * @param loanProductNo 放款产品代码
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-20下午6:08:51
	 */
	public String getDeductionProduct(String loanProductNo);
	/**
	 * 更新在线银承已用额度
	 * @param PlOnlineCrdt
	 * @date 2021-7-20下午6:08:51
	 */
	public void txSavePedOnlineCrtdProtocol(PlOnlineCrdt crdt) throws Exception;

}
