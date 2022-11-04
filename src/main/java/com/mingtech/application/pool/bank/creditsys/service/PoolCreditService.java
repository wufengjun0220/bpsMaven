package com.mingtech.application.pool.bank.creditsys.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;


/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: youjin
* @日期: Oct 14, 2010 17:06:01 PM
* @描述: [PoolCreditService] 信贷服务接口类
*/
public interface PoolCreditService extends GenericService{
	/**
	* <p>方法名称: judgeCreditAmountC101|描述: 票据池-额度判断</p>
	* @param commId 客户组织机构代码
	 * @param applyEdu 申请占用额度
	 * @param crdtDueDt 信贷产品到期日
	* @return 判断结果
	* @throws Exception
	*/
	public String judgeCreditAmountC101(String commId,BigDecimal applyEdu,Date crdtDueDt) throws Exception;
	
	
	/**
	* <p>方法名称: txJudgeCreditAmountC1014Cal|描述: 信贷产品开立计算 2011-06-29</p>
	* @param commId
	* @param applyEdu
	* @param crdtDueDt
	* @return
	* @throws ExitPoolException
	*/
	public String txJudgeCreditAmountC1024Cal(String cusCommid,BigDecimal[] amountOfCreditProduct,Date[] dueDtOfCreditProduct) throws Exception;
	
	
	/**
	* <p>方法名称: txJudgeAndEmployCreditAmountC102|描述: 票据池-额度判断及占用</p>
	* @param crdQuery 信贷查询对象
	* @throws Exception
	*/
	public void txjudgeAndEmployCreditAmountC102(CreditProduct cp)throws Exception;
	
	/**
	* <p>方法名称: txJudgeAndEmployCreditAmountC102|描述: 票据池-额度判断及占用</p>
	* @param crdQuery 信贷查询对象
	* @throws Exception
	*/
	public void txjudgeAndEmployCreditAmountC102ForDayEnd(CreditProduct cp)throws Exception;
	
	/**
	* <p>方法名称: txReleaseCreditAmountC103|描述: 票据池-额度释放</p>
	* @param crdObject 信贷查询对象
	* @param acctList 持票人账号集合
	* @throws Exception
	*/
	public void releaseCreditAmountC103(CreditProduct crdObject)throws Exception;
	/**
	* <p>方法名称: payBackRequestC104|描述: 票据池-还款请求</p>
	* @param commId 客户组织机构代码
	 * @param payBackAmt 还款金额
	 * @param crdtNo 信贷唯一标识
	 * @param flag 是否结清标识
	* @return 保证金账号
	* @throws Exception
	*/
	public String txpayBackRequestC104(String commId,BigDecimal payBackAmt,String crdtNo,String flag) throws Exception;
	/**
	 * <p>方法名称: payBackRequestC104|描述: 票据池-还款请求</p>
	 * @param commId 客户组织机构代码
	 * @param payBackAmt 还款金额
	 * @param crdtNo 信贷唯一标识
	 * @param flag 是否结清标识
	 * zhanghanyuan 20150601
	 * @param payType 还款类型，用于区分保证金还款和自由资金还款
	 * @param payMoney	自由资金还款资金
	 * **********************************
	 * @return 保证金账号
	 * @throws Exception
	 */
//	public String txpayBackRequestC104(String commId,BigDecimal payBackAmt,String crdtNo,String flag) throws Exception;
	/**
	* <p>方法名称: reCallPayBackRequestC105|描述: 票据池-撤回还款请求</p>
	* @param commId 客户组织机构代码
	 * @param payBackAmt 还款金额
	 * @param cdId 信贷唯一标识
	* @return 保证金账号
	* @throws Exception
	*/
	public void reCallPayBackRequestC105(String commId,BigDecimal payBackAmt,String crdtNo) throws Exception;
	/**
	* <p>方法名称: queryCrdProByCrdtNo|描述: 票据池-通过信贷产品唯一标示查找信贷产品</p>
	* @param crdtObject 信贷产品对象
	* @throws Exception
	*/
	public CreditProduct queryCrdProByCrdtNo(String crdtNo) throws Exception;

	/**
	 * <p>方法名称：releaseCreditAmountC103Nor | 描述：C103 正常释放时用这个逻辑处理
	 * @param cp
	 * @throws Exception
	 */
	public void releaseCreditAmountC103Nor(CreditProduct cp)throws Exception;
	/**
	 * <p>方法名称：saveBlackLists |描述：信贷接口 日终风险票据跑批</p>
	 * @param blackLists
	 * @throws Exception
	 */
	public void saveBlackLists (List blackLists) throws Exception;
	
	public void txDeleteBillBlackLists ()throws Exception;
	
	/** 2015511 zhaoding add
	 * 还款 - 手工释放额度 - 确认释放
	 * @param ids
	 * @param user
	 * @throws Exception
	 */
	public void txReleaseEduByManual(String ids,User user) throws Exception;
	
	
	/**
	 * 描述:MIS接口 PJE007 在池票据查询接口
	 * @param pq
	 * @param page
	 * @return QueryResult
	 * @throws Exception
	 * @author xie cheng
	 * @date 2019-05-23
	 */
	public QueryResult queryDraftPoolPJE007(PoolQueryBean pq, Page page) throws Exception;
	
	/**
	 * 描述:根据票据ID(来源电票系统)查询PoolBillinfo表
	 * @return List<PoolBillInfo>  
	 * @throws Exception
	 * @author xie cheng
	 * @date 2019-05-23
	 */
	public PoolBillInfo queryPoolBillinfoPJE008(String id ,String startNo, String endNo) throws Exception;
	
	/**
	 * 描述:查询DraftPool表
	 * @return List<DraftPool>  
	 * @throws Exception
	 * @author xie cheng
	 * @date 2019-05-23
	 */
	public List<DraftPool> queryDraftInfos(PoolQueryBean pq, Page page) throws Exception;
	
	/**
	 * 描述:MIS接口 PJE010 贴现结果查询接口
	 * @param pq
	 * @param page
	 * @return QueryResult
	 * @throws Exception
	 * @author xie cheng
	 * @date 2019-05-23
	 */
	public QueryResult queryPlDiscountPJE010(PoolQueryBean pq, Page page) throws Exception;
	
	/**
	 * 描述:查询PlDiscount表
	 * @return List<PlDiscount>  
	 * @throws Exception
	 * @author xie cheng
	 * @date 2019-05-23
	 */
	public List<PlDiscount> queryPlDiscounts(PoolQueryBean pq, Page page) throws Exception;
}
