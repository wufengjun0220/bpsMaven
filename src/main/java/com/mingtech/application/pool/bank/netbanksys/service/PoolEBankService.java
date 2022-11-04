package com.mingtech.application.pool.bank.netbanksys.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryParameter;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.domain.PedBlackList;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftAccountManagement;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOutBatch;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: 张永超
 * @日期: Sep 25, 2010 9:40:14 AM
 * @描述: [EBankService] 网银服务接口类
 */
@Service
public interface PoolEBankService extends GenericService {

	// Pjc004 buildQueryResult
	public QueryResult queryPoolBillKind(PoolQueryBean pq, Page page) throws Exception;

	/**
	 * 保证金交易明细查询
	 * 保证金当日信息查询，保证金历史信息查询
	 * @param queryNean
	 * @return QueryResult
	 * @throws @author Ju Nana
	 * @date 2019-2-14 下午3:09:24
	 */
	public QueryResult queryBailTransDetails(PoolQueryBean queryNean) throws Exception;


	// PJC009:票据池-信贷产品查询接口
	public QueryResult queryUsedCreditProductPJC009(PoolQueryBean pool,Page page) throws Exception;

	/**
	 * 判断客户是否开通票据池业务
	 * @param bpsNo 票据池编号
	 * @param orgCode 客户组织机构代码
	 * @param custNum 核心客户号
	 * @param custAccts 客户账号列表（预留功能字段）
	 * @param marginAccount 保证金账号
	 * @return String
	 * @throws @author Ju Nana
	 * @date 2018-11-30 下午1:48:02
	 */
	public String queryPoolCommOpen(String bpsNo,String orgCode, String custNum, List custAccts, String marginAccount);

	/**
	 * 判断客户虚拟票据池是否开通
	 * 
	 * @param poolAgreement 票据池编号
	 * @param custNum 客户核心客户号
	 * @return String 虚拟票据池签约开通标识
	 * @author Ju Nana
	 * @date 2019-1-14 下午2:02:53
	 */
	public String queryVtrustPoolCommOpen(String poolAgreement, String custNum);

	/**
	 * 20180912 yangYu 判断客户票据池是否开通标识 params1 String 企业签约结算账户 params2 核心客户号 result
	 * PedProtocolDto
	 */
	public PedProtocolDto queryPedProtocolDtoByAccount(String SAccountNo, List custAccts, String custNum)
			throws Exception;

	/**
	 * 20180912 yangYu 判断客户票据池是否开通标识 params1 String 企业签约结算账户 params2 核心客户号 result
	 * PedProtocolDto
	 */
	public PedProtocolDto queryPedProtocolDtoByAccount(String SAccountNo, String custNum) throws Exception;


	/**
	 * 取票记账完毕后调用该方法 执行贴现或者质押入池流程 托管票据贴现进行贴现操作，标识为：1 托管票据转质押操作，标识为：2
	 * 
	 */
	public void txAccountQuerytoNext(DraftPoolOutBatch batch) throws Exception;

	/**
	 * 
	 * @Description: PJC010 协议管理—签约管理接口
	 * @param protocol 签约协议表
	 * @return void
	 * @author Ju Nana
	 * @date 2018-10-22 上午10:03:55
	 */
	public String txPedProtocolDtoPJC010(PedProtocolDto protocol) throws Exception;

	/**
	 * 
	 * @Description: PJC011协议管理-签约查询接口
	 * @param coreCustnumber 核心客户号
	 * @return ProtocolAndAssetParameter
	 * @author Ju Nana
	 * @date 2018-10-22 下午2:36:58
	 */
	public List<PedProtocolDto> queryProtocolPjc011(String coreCustnumber, String poolAgreement) throws Exception;

	/**
	 * 
	 * @Description: 网银黑名单登记接口
	 * @param black
	 * @author Ju Nana
	 * @date 2018-10-23 上午9:02:24
	 */
	public void txBlackListHandler(PedBlackList black, String doType) throws Exception;

	/**
	 * 
	 * @Description: 网银黑名单查询方法
	 * @param orgCode 核心客户号
	 * @param type    类型：01黑名单 02灰名单
	 * @author Ju Nana
	 * @date 2018-10-23 上午9:20:01
	 */
	public List queryBlackListPJC018(String orgCode, String type) throws Exception;

	/**
	 * 
	 * @Description: 网银票样查询票据正面查询方法
	 * @param billNo 票号
	 * @param beginRangeNo 子票区间起
	 * @param endRangeNo 子票区间止
	 * @throws Exception
	 * @return List 查询结果集
	 * 融合改造修改
	 * @author wfj
	 * @date 2022-4-7
	 */
	public Map queryDraftFrontPJC016(String billNo,String beginRangeNo,String endRangeNo) throws Exception;

	/**
	 * 
	 * @Description: 网银票样查询票据正面查询方法
	 * @param billNo 票号
	 * @param beginRangeNo 子票区间起
	 * @param endRangeNo 子票区间止
	 * @throws Exception
	 * @return List 查询结果集
	 * 融合改造修改
	 * @author wfj
	 * @date 2022-4-7
	 */
	public List queryDraftBackPJC017(String billNo,String beginRangeNo,String endRangeNo) throws Exception;

	/**
	 * 
	 * @Description: 网银查询保证金账号方法
	 * @param coreCustnumber 核心客户号
	 * @param poolAgreement  协议编号
	 * @param                @throws Exception
	 * @return Map
	 * @author Ju Nana
	 * @date 2018-10-24 上午11:35:33
	 */
	public Map queryMarginAccountPJC020(String coreCustnumber, String poolAgreement) throws Exception;

	/**
	 * 保证金可支取金额查询
	 * @param custNo        客户号
	 * @param poolAgreement 协议编号
	 * @param               @throws Exception
	 * @return Map
	 * @author Ju Nana
	 * @date 2018-10-24 下午1:52:21
	 */
	public Map queryMarginBalance(String custNo, String poolAgreement) throws Exception;


	/**
	 * 
	 * @Description: 入池申请
	 * @param pool
	 * @param eSign 电子签名
	 * @param bpsNo 票据池编号
	 * @param changePool 入池申请拆分的数据 拆分金额
	 * @author Ju Nana
	 * @date 2018-10-25 上午10:44:08
	 */
	public DraftPoolIn txApplyDraftPoolInPJC002(PoolBillInfo pool, String eSign, String bpsNo, PoolBillInfo changePool) throws Exception;

	/**
	 * 
	 * @Description: 出池申请
	 * @param outIds 出池申请业务Id
	 * @author Ju Nana
	 * @date 2018-10-25 下午1:38:02
	 */
	public Ret txApplyDraftPoolOutPJC003(List<String> billNos, String elsignature, PedProtocolDto dto,PoolQueryBean param,Map<String,PoolBillInfo> bills,List<String> assteNos) throws Exception;

	/**
	 * 
	 * @Description: 网银虚拟票据录入接口服务
	 * @param vtrust 虚拟票据实体
	 * @param transType 操作类型 01-新增;02-修改;03-删除
	 * @param billProperty 操作类型 QY_01持有票据 ; QY_02 应付票据
	 * @return void
	 * @author Ju Nana
	 * @date 2018-11-8 上午11:09:05
	 */
	public void txPoolVtrustStore(PoolVtrust vtrust, String transType) throws Exception;

	/**
	 * @Description: 调用核心接口 查询客户信息
	 * @param SAccountNo
	 * @return list
	 * @throws Exception
	 * @author Wu Fengjun
	 * @date 2018-11-9
	 */
	public List queryCustomert(String SAccountNo) throws Exception;

	/**
	 * @Description: 调用核心接口 查询保证金账号信息
	 * @param SAccountNo
	 * @return list
	 * @throws Exception
	 * @author Wu Fengjun
	 * @date 2018-11-9
	 */
	public List queryAccount(String poolAccount) throws Exception;
	
	/**
	 * @Description: 调用核心接口 查询结算账号信息
	 * @param SAccountNo
	 * @return list
	 * @throws Exception
	 * @author Wu Fengjun
	 * @date 2019-2-25
	 */
	public List queryAccNo(String poolAccount) throws Exception;

	/**
	 * @Description //待办事项-明细查询
	 * @Param [orgCode]
	 * @return com.mingtech.application.pool.bank.netbanksys.domain.QueryResult
	 * @Author tangxiongyu
	 * @Date 13:40 2018/12/28
	 **/
	public List<Map> queryNotifySummaryPJC015(String custNO,String bpsNO) throws Exception;

	public QueryResult queryDraftAccount(PoolQueryBean pool) throws Exception;
	
	/**查询帐务管家的方法
	 * @param pool
	 * @return
	 * @throws Exception
	 * @Author YeCheng
	 * @Date 13:40 2019/02/23
	 */
	public QueryResult queryDraftAccountManagement(PoolQueryBean pool) throws Exception;

	/**
	 * 根据传递参数查询poolBillInfo信息
	 * 
	 * @param pq
	 * @param page
	 * @param @throws Exception
	 * @return QueryResult
	 * @author Ju Nana
	 * @date 2019-1-29 下午3:09:38
	 */
	public QueryResult queryPoolBillInfoByParams(QueryParameter pq, Page page) throws Exception;
	
	/**
	 * 账务管家查询
	 * @param bean
	 * @param @throws Exception   
	 * @return List<DraftAccountManagement>  
	 * @author Ju Nana
	 * @date 2019-2-28 下午3:06:52
	 */
	public List<DraftAccountManagement> queryManagement(PoolQueryBean bean ,Page page) throws Exception;
	
	/**
	 * 根据电票签约账号以及类型查询大票表票据信息
	 * @param elecAcc 电票签约账号
	 * @param type 查询类型 0：查询初始化票据（DS_00） 查询类型1：其他状态
	 * @param @throws Exception   
	 * @return List<PoolBillInfo>  
	 * @author Ju Nana
	 * @date 2019-3-10 下午2:33:21
	 */
	public List<PoolBillInfo> queryBillByElecAccAndType(String elecAcc,String status)throws Exception;
	
	/**
	 * 根据传递参数查询PedCheckBatch信息
	 * @param pq
	 * @param @throws Exception
	 * @return QueryResult
	 * @author xie cheng
	 * @date 2019-05-27 
	 */
	public PedCheckBatch queryPedCheckBatchParams(PoolQueryBean pq) throws Exception;
	
	/**
	 * 根据传递参数查询PedProtocolList信息
	 * @param pq
	 * @param @throws Exception
	 * @return QueryResult
	 * @author xie cheng
	 * @date 2019-05-30 
	 */
	public List<PedProtocolList> queryPedProtocolListParams(PoolQueryBean pq,Page page) throws Exception;
	
	/**
	 * 
	 * @Description: PJC033 协议管理—单户签约管理接口
	 * @param protocol 签约协议表
	 * @return void
	 * @author liu xiaodong 
	 * @date 2019-06-06 上午10:03:55
	 */
	public String txPedProtocolDtoPJC033(PedProtocolDto protocol) throws Exception;
	
	/**
	 * 账务管家表更新操作
	 * @Description 根据票据池编号与核心客户号更新账务管家表信息
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @param eleAccNos
	 * @return
	 * @throws Exception
	 * @date 2019-6-18上午10:34:54
	 */
	public void txAccountManagement(String bpsNo,String custNo,String eleAccNos) throws Exception;
	
	/**
	 * 强制贴现票据额度处理
	 * @Description TODO
	 * @author wfj
	 * @param draftList
	 * @param dto
	 * @param 交易金额
	 * @param plDiscount 贴现对象
	 * @throws Exception
	 * @date 2022-4-7
	 */
	public void txDiscountEdu(List <DraftPool> draftList,PedProtocolDto dto ,BigDecimal tradeAmt,PlDiscount plDiscount)throws Exception;
	
	/**
	 * 根据电票签约账号,票据池编号以及类型查询大票表票据信息
	 * @param elecAcc 电票签约账号
	 * @param type 查询类型 0：查询初始化票据（DS_00） 查询类型1：其他状态
	 * @param @throws Exception   
	 * @return List<PoolBillInfo>  
	 * @author liu xiaodong 
	 * @date 2019-3-10 下午2:33:21
	 */
	public List<PoolBillInfo> queryBillByElecAccAndTypeNew(String elecAcc,String status ,String poolAgreement)throws Exception;
	
	/**
	 * 电票签约账号校验（含删掉解约账号下的初始化票据的操作）
	 * @author Ju Nana
	 * @param newAccNo
	 * @param custNo
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-31上午1:28:03
	 */
	public Ret eleAccNoCheck(String newAccNo,String custNo,String bpsNo) throws Exception;
	
	/**
	 * 融资解约判断
	 * @author Ju Nana
	 * @param ppd
	 * @return
	 * @throws Exception
	 * @date 2019-7-31上午1:32:06
	 */
	public Ret endCreditCheck(PedProtocolDto ppd) throws Exception;
	
	/**
	 * 网银签约字段落库处理
	 * @author Ju Nana
	 * @param protocol：传入协议内容
	 * @return
	 * @throws Exception
	 * @date 2019-7-31下午4:46:41
	 */
	public String createProtocolStorePJC034(PedProtocolDto protocol) throws Exception;
	
	/**
	 * 根据票据池编号返回所有已签约的客户号
	 * @author Ju Nana
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-31下午7:48:00
	 */
	public List<Object> queryProListCustNo(String bpsNo) throws Exception;
	
	/**
	 * 成员子户是否可解约校验
	 * @author Ju Nana
	 * @return
	 * @throws Exception
	 * @date 2019-7-31下午8:24:47
	 */
	public boolean memIsCanEndContractCheck(String bpsNo,List custNos)throws Exception;
	
	/**
	 * 网银客户服务费收取查询
	 * @author Ju Nana
	 * @param custNO
	 * @param bpsNO
	 * @return
	 * @throws Exception
	 * @date 2019-8-1下午7:02:32
	 */
	public List<Map> queryPoolFee(String custNO,String bpsNO) throws Exception;
	
	/**
	 * 网银对账任务查询
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @date 2019-8-1下午7:33:47
	 */
	public List<PedCheck> queryPedCheck(String bpsNo,String custNo)throws Exception;
	
	/**
	 * 保证金更改后的 相应数据的更新
	 * @param bpsNo
	 * @param marginAccount 新保证金账号
	 * @param oldMarginAccount 原保证金账号
	 * @throws Exception
	 */
	public void txChangeBailAccNo(String bpsNo,String marginAccount,String oldMarginAccount) throws Exception;
	
	// PJC005,池票据组合查询
	public List queryDraftInfos(PoolQueryBean pq, Page page) ;
}
