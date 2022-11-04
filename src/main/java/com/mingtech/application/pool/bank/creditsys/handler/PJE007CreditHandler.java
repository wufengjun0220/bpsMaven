package com.mingtech.application.pool.bank.creditsys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.framework.core.page.Page;

/**
 * @Title: MIS接口 PJE007
 * @Description: 在池票据查询接口
 * @author xie cheng
 * @date 2019-05-23
 */
public class PJE007CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE007CreditHandler.class);
	@Autowired
	private PoolCreditService poolCreditService;
	@Autowired
	private PedProtocolService pedProtocolService;
	
	/**
	 * MIS接口 PJE007 在池票据查询接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		QueryResult result = null;
		Ret ret = new Ret();
		try {
				PoolQueryBean queryBean = QueryParamMap(request);
				Page page = getPage(request.getAppHead());
				PedProtocolDto dto ;
				//根据请求参数查询DraftPool表,默认条件:DS_02已入池和电票
//				PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, queryBean.getProtocolNo(), null, null, null);
				queryBean.setEbkLock(PoolComm.EBKLOCK_02);
				result = poolCreditService.queryDraftPoolPJE007(queryBean, page);
				if (null == result || result.getTotalCount() == 0) {
					ret.setRET_CODE(Constants.CREDIT_05);
					ret.setRET_MSG("在池票据查询结束"+queryBean.getCustomernumber()+"客户无符合条件数据!");
					logger.info("PJE007-在池票据查询结束"+queryBean.getCustomernumber()+"客户无符合条件数据!");
				} else {
					if(queryBean.getProtocolNo() != null && !queryBean.getProtocolNo().equals("")){
						dto = pedProtocolService.queryProtocolDto(null, null, queryBean.getProtocolNo(), null, null, null);
					}else {
						DraftPool pool  = (DraftPool) result.getRecords().get(0);
						dto = pedProtocolService.queryProtocolDto(null, null, pool.getPoolAgreement(), null, null, null);
					}

					setPage(response.getAppHead(), page);
					List details = this.detailProcess(result.getRecords());
					response.getBody().put("DEPOSIT_ACCT_NO",dto.getMarginAccount());
					response.getBody().put("DEPOSIT_ACCT_NAME",dto.getMarginAccountName());
					response.getBody().put("BPS_NO",dto.getPoolAgreement());

					response.setDetails(details);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("查询成功!");
					logger.info("PJE007-在池票据查询结束"+queryBean.getCustomernumber()+"客户查询成功!共"+result.getTotalCount()+"条");
				}

		} catch (Exception e) {
			logger.error("PJE007-在池票据查询异常!",e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在池票据查询异常! 票据池内部执行错误!");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * @Description: 请求数据处理
	 * @param request
	 * @return PoolQueryBean
	 * @author xie cheng
	 * @date 2019-05-23 
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		PoolQueryBean pq = new PoolQueryBean();
		Map body = request.getBody();
		pq.setCustomernumber(getStringVal(body.get("CORE_CLIENT_NO"))); // 核心客户号
		pq.setProtocolNo(getStringVal(body.get("BPS_NO")));// 票据池编号 
		pq.setBillNo(getStringVal(body.get("BILL_NO"))); // 票据编号
		pq.setBusinessId(getStringVal(body.get("BILL_ID")));//票据id
		pq.setBeginRangeNo(getStringVal(body.get("START_BILL_NO")));//票据号起
		pq.setEndRangeNo(getStringVal(body.get("END_BILL_NO")));//票据号止
		pq.setDraftSource(getStringVal(body.get("BILL_SOURCE")));//票据来源
//		pq.setIsEdu(PoolComm.YES);//0额度的票据不允许做强制贴现
		String draftType = getStringVal(body.get("DRAFT_TYPE")); // 票据种类 0000全部  AC01银承 AC02商承
		if(draftType != null && !"".equals(draftType)){
			pq.setsBillType(draftType);
		}else {
			pq.setsBillType("0000");
		}
		pq.setsBillType(getStringVal(body.get("DRAFT_TYPE"))); // 票据种类 0000全部  AC01银承 AC02商承
		pq.setsAcceptorBankCode(getStringVal(body.get("ACCEPTANCE_BANK_ID"))); // 承兑人开户行行号
		pq.setSBillMedia("2"); //票据类型 只做电票的在池票据查询
		pq.setCirStage(PoolComm.DS_02); //融资池状态
		pq.setLockString(PoolComm.BBSPLOCK_01);//加锁
		pq.setRickLevel(getStringVal(body.get("LIMIT_USE_TYPE")));//风险类型
		pq.setCreditObjType(getStringVal(body.get("LIMIT_MAIN_TYPE")));//额度主体类型
		return pq;
	}
	
	/**
	 * 返回details加工处理
     * 融资票据池details
	 * @author xie cheng
	 * @throws Exception 
	 * @date 2019-5-23
	 */
	private List detailProcess(List result) throws Exception {
		ArrayList infoList = new ArrayList();
		if (result != null && result.size() > 0) {
			HashMap map = null;
			for (int i = 0; i < result.size(); i++) {
				map = new HashMap();
				DraftPool draftPool = (DraftPool) result.get(i);
				map.put("BILL_INFO_ARRAY.BILL_NO", draftPool.getAssetNb());// 票据号码
				map.put("BILL_INFO_ARRAY.START_BILL_NO", draftPool.getBeginRangeNo());// 票据号起
				map.put("BILL_INFO_ARRAY.END_BILL_NO", draftPool.getEndRangeNo());// 票据号止
				map.put("BILL_INFO_ARRAY.BILL_SOURCE", draftPool.getDraftSource());// 票据来源
				map.put("BILL_INFO_ARRAY.SPLIT_FLAG", draftPool.getSplitFlag());// 是否可拆分
				map.put("BILL_INFO_ARRAY.BILL_ID", draftPool.getPoolBillInfo().getDiscBillId());//票据ID（来源电票系统）
				map.put("BILL_INFO_ARRAY.DRAFT_TYPE", draftPool.getAssetType());// 票据种类
				map.put("BILL_INFO_ARRAY.BILL_AMT", draftPool.getAssetAmt());// 票据金额
				map.put("BILL_INFO_ARRAY.DRAW_DATE", draftPool.getPlIsseDt());// 出票日
				map.put("BILL_INFO_ARRAY.EXPIRY_DATE", draftPool.getPlDueDt());// 到期日
				map.put("BILL_INFO_ARRAY.BILL_NAME", draftPool.getPlDrwrNm());// 出票人名称
				map.put("BILL_INFO_ARRAY.PAYEE_NAME", draftPool.getPlPyeeNm());// 收款人名称
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK", draftPool.getPlAccptrSvcr());// 承兑人行号
				map.put("BILL_INFO_ARRAY.BILL_ACCT_NO", draftPool.getPlDrwrAcctId());// 出票人账号
				map.put("BILL_INFO_ARRAY.REMITTER_OPENBANK_NAME", draftPool.getPlDrwrAcctSvcrNm());// 出票人开户行名称
				map.put("BILL_INFO_ARRAY.REMITTER_OPEN_BANK", draftPool.getPlDrwrAcctSvcr());// 出票人开户行行号
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", draftPool.getPlPyeeAcctId());// 收款人账号
				map.put("BILL_INFO_ARRAY.PAYEE_OPENBANK_NAME", draftPool.getPlPyeeAcctSvcrNm());// 收款人开户行名称
				map.put("BILL_INFO_ARRAY.PAYEE_OPEN_BRANCH", draftPool.getPlPyeeAcctSvcr());//收款人开户行行号
				map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", draftPool.getPlAccptrNm());// 承兑人名称
				map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", draftPool.getPlAccptrId());//承兑人账号
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPENBANK_NAME", draftPool.getPlAccptrSvcrNm());//承兑人开户行行名
				map.put("BILL_INFO_ARRAY.ENDORSE_TRANS_FLAG", draftPool.getPoolBillInfo().getSBanEndrsmtFlag());//背书转让标志
				map.put("BILL_INFO_ARRAY.ONLINE_SETTLE_FLAG", "");//线上清算标志
				map.put("BILL_INFO_ARRAY.ENTER_BANK_NAME","" );//入账行名称
				map.put("BILL_INFO_ARRAY.ENTER_BANK_CODE","" );//入账行号
				map.put("BILL_INFO_ARRAY.ENTER_ACCT_NO", "");//入账账号
				map.put("BILL_INFO_ARRAY.LIMIT_USE_TYPE",draftPool.getRickLevel());//额度占用类型
				map.put("BILL_INFO_ARRAY.LIMIT_MAIN_TYPE", draftPool.getCreditObjType());//额度主体类型
				map.put("BILL_INFO_ARRAY.APPLYER_ACCT_NO", draftPool.getAccNo());//申请人账号
				map.put("BILL_INFO_ARRAY.HOLD_BILL_ID", draftPool.getHilrId());//持票ID
				map.put("BILL_INFO_ARRAY.TRAN_ID", draftPool.getTranId());//交易ID
				
				
				
				infoList.add(map);
			}
		}
		return infoList;
	}

	
}
