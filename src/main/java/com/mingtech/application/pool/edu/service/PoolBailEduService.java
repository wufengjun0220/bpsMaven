/**
 * 
 */
package com.mingtech.application.pool.edu.service;

import java.math.BigDecimal;
import java.util.List;

import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * @author wbyecheng
 *
 */
public interface PoolBailEduService extends GenericService{

	/**
	 * 通过保证金账号查询保证金流水信息
	 * (可能会改动,根据后期接口规范新增的子弹增加查询的参数)
	 * @param account
	 * @return
	 * @throws Exception
	 * @author wufengjun	20181224
	 */
	public List queryFlowByAcc(String account,User user,Page page) throws Exception;
	
	/**
	 * 通过保证金账号查询保证金历史信息
	 * (可能会改动,根据后期接口规范新增的子弹增加查询的参数)
	 * @param bean
	 * @return
	 * @throws Exception
	 * @author wufengjun	20181224
	 */
	public List queryFlowByAHistorycc(QueryBean bean,User user,Page page) throws Exception;
	
	
	/**
	 * 查询核心保证金明细查询接口，保存保证金明细
	 * @param trans
	 * @return void  
	 * @author Ju Nana
	 * @date 2019-3-1 下午3:51:28
	 */
	public void txBailDetail(CoreTransNotes trans) throws Exception;
	
	/**
	 * 保证金当日查询：删除全部数据并重新从核心查回
	 * @param @throws Exception   
	 * @return void  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-3-13 下午6:43:37
	 */
	public void txBailQueryFromCore(PedProtocolDto pro) throws Exception;
	
	
	/**
	 * 根据atId查询保证金明细信息
	 * @param @throws Exception   
	 * @return void  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-3-22 下午6:43:37
	 */
	public BailDetail queryBailDetail(String atId) throws Exception;
	
	/**
	 * 删除保证金当日流水表中的数据
	 * @param accNo
	 * @author Ju Nana
	 * @date 2019-3-27 下午2:43:37
	 * @throws Exception
	 */
	public void txDeleteBailFlow(String accNo) throws Exception ;
	
	/**
	 * 查询核心保证金余额
	 * @author Ju Nana
	 * @param accNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-30下午2:21:39
	 */
	public BigDecimal queryCoreBailFree(String accNo) throws Exception;

	/**
	 * 核心同步票据池保证金
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-28下午4:07:40
	 */
	public BailDetail txUpdateBailDetail(String bpsNo) throws Exception;
	
	/**
	 * 根据票据池编号查询额度明细表信息
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-28下午4:56:59
	 */
	public BailDetail queryBailDetailByBpsNo(String bpsNo) throws Exception;
	
	/**
	 * 根据ID查询保证金支取审批信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List queryBailTrans(QueryBean bean,Page page,User user) throws Exception;
	
	/**
	 * 网银保证金支取提交审批
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void txSubmitPedBailTrans(String id, User user) throws Exception;
	
	/**
	 * 网银保证金支取撤销审批
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void txCancelAudit(String id, User user) throws Exception;
	
	/**
	 * 网银保证金支取核心划转记录查询
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void queryPedBailTransDetail(String id, User user) throws Exception;
	
	/**
	 * 网银保证金支取重新划转
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Ret sendPedBailTranAgain(String id, User user) throws Exception;
	
	/**
	 * 网银保证金支取超过一天的数据作废
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void txCancelPedBailTran(QueryBean queryBean) throws Exception;
	/**
	 * 保证金账户变更信息查询
	 * @param QueryBean
	 * @return
	 * @throws Exception
	 */
	public List queryBailAccountChange(QueryBean bean,Page page) throws Exception;
	
	/**
	 * 保证金当日查询：删除全部数据并重新从核心查回
	 * @param @throws Exception   
	 * @return void  
	 * @throws
	 * @author Ju Nana
	 * @date 2019-3-13 下午6:43:37
	 */
	public String txBailQueryFromCoreJM(PedProtocolDto pro) throws Exception;
}