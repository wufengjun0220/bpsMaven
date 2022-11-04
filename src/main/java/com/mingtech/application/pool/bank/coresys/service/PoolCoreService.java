package com.mingtech.application.pool.bank.coresys.service;

import java.util.Map;

import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.PoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;


/**
 * 票据池交易  核心服务
 * @author yixiaolong
 */
public interface PoolCoreService {
	
	/**
	 * 核心客户信息查询
	 * @param transNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew PJH854111Handler(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 核心账户信息及账户余额信息查询
	 * @param transNotes
	 * @param queryType 查询类型 0：保证金余额查询  1：保证金账户查询
	 * @param @return
	 * @param @throws Exception   
	 * @return ReturnMessageNew  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-1-18 上午11:12:21
	 */
	public ReturnMessageNew PJH716040Handler(CoreTransNotes transNotes,String queryType) throws Exception;
	
	/**
	 * 质押记账	核心接口
	 * @param transNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew PJH580314Handler(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 解质押记账		核心接口
	 * @param transNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew PJH580316Handler(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 核心托收回款记账服务
	 * @param transNotes
	 * @param @return
	 * @param @throws Exception   
	 * @return ReturnMessageNew  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-1-18 上午11:14:01
	 */
	public ReturnMessageNew PJH580311Handler(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 核心保证金交易查询
	 * @param transNotes
	 * @param @return
	 * @param @throws Exception   
	 * @return ReturnMessageNew  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-1-18 上午11:15:35
	 */
	public ReturnMessageNew PJH584141Handler(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 核心贷款还款信息查询
	 * @Description: TODO
	 * @param transNotes
	 * @param @return
	 * @param @throws Exception   
	 * @return ReturnMessageNew  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-1-18 上午11:16:21
	 */
	public ReturnMessageNew PJH126012Handler(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 核心保证金划转接口
	 * @Description TODO
	 * @author Ju Nana
	 * @param transNotes
	 * @return
	 * @throws Exception
	 * @date 2019-6-16上午1:05:22
	 */
	public ReturnMessageNew doMarginWithdrawal(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 核心收费记账接口
	 * @param transNotes
	 * @author Wu fengjun
	 * @return
	 * @throws Exception
	 * @date 2019-6-25
	 */
	public ReturnMessageNew doFeeScaleCoreAccount(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 质押记账接口
	 * @param transNotes
	 * @return
	 * @throws Exception
	 * @author Wu fengjun
	 * @date 2019-6-25
	 */
	public ReturnMessageNew txPledgeAccount(CoreTransNotes transNotes) throws Exception;
	/**
	 * @Title CORE001Handler
	 * @author wss
	 * @date 2021-5-18
	 * @Description 放款申请
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE001Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title CORE002Handler
	 * @author wss
	 * @date 2021-5-19
	 * @Description 查询核心贷款记账状态
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE002Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title txApplyZHS001Handler
	 * @author wss
	 * @date 2021-5-20
	 * @Description 智汇宝支付
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyZHS001Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title txApplyZHS002Handler
	 * @author wss
	 * @date 2021-5-20
	 * @Description 智汇宝平台转账明细查询
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyZHS002Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title CORE008Handler
	 * @author wss
	 * @date 2021-5-21
	 * @Description 解圈
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE008Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title CORE007Handler
	 * @author wss
	 * @date 2021-5-21
	 * @Description 圈存
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE007Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title CORE003Handler
	 * @author wss
	 * @date 2021-5-22
	 * @Description 提前还款
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE003Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title Core009Handler
	 * @author wss
	 * @date 2021-5-24
	 * @Description 贷款归还 
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew Core009Handler(CoreTransNotes note) throws Exception;
	/**
	 * @Title core006Handler
	 * @author gcj
	 * @date 20210526
	 * @Description 保证金开户 
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew core006Handler(CoreTransNotes transNotes) throws Exception ;
	/**
	 * 保证金开户变更 20210531 gcj
	 * @throws Exception 
	 */
	public ReturnMessageNew core010Handler(CoreTransNotes transNotes) throws Exception;
	
	/**
	 * 核心利率查询
	 * @param transNotes
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-9-3下午4:46:13
	 */
	public ReturnMessageNew core011Handler(CoreTransNotes transNotes) throws Exception ;
}
