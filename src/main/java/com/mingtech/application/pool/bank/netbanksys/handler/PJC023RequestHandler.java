package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.PedBailFlow;
import com.mingtech.application.pool.edu.domain.PedBailHis;
import com.mingtech.application.pool.edu.service.PoolBailEduService;

/**
 * 网银接口 PJC007 保证金明细查询接口
 */
public class PJC023RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC023RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;// 网银方法类
	@Autowired
	private PoolBailEduService poolBailEduService;
	@Autowired
	private PedProtocolService pedProtocolService;// 协议服务
	/**
	 * 网银接口 PJC023 保证金明细查询接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		// 构建查询条件
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			PoolQueryBean queryBean = QueryParamMap(request);
			String custNo = queryBean.getSCustOrgCode();
			String bpsNo = queryBean.getProtocolNo();
			// 执行查询
			QueryResult result = null;
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, custNo, null, null);
			if (dto!=null && !PoolComm.OPEN_01.equals(dto.getOpenFlag())) {
				ret.setRET_CODE(Constants.EBK_02);
				ret.setRET_MSG("该客户未开通票据池业务");
			} else {
				//调用核心接口直接更新该客户名下的保证金交易流水信息
				try {
					poolBailEduService.txBailQueryFromCore(dto);
				} catch (Exception e1) {
					ret.setRET_MSG(e1.getMessage());
					ret.setRET_CODE(Constants.EBK_03);
				}
				
				result = poolEBankService.queryBailTransDetails(queryBean);
				if (null == result || result.getTotalCount() == 0) {
					ret.setRET_MSG("无符合条件数据");
					ret.setRET_CODE(Constants.EBK_03);
				} else {
					List details = ResponseDataProcess(result.getRecords(), queryBean.getQueryParam());
					response.getAppHead().put("TOTAL_ROWS", result.getTotalCount());
					response.setDetails(details);
					ret.setRET_MSG("查询成功");
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				}

			}

		} catch (Exception ex) {
			logger.error(ex, ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询异常");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * 
	 * @Description: 构建查询对象
	 * @param request 报文请求信息
	 * @return PoolQueryBean
	 * @author Ju Nana
	 * @throws Exception
	 * @date 2018-10-16 上午11:46:46
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {

		PoolQueryBean pq = new PoolQueryBean();
		Map body = request.getBody();
		pq.setProtocolNo(getStringVal(body.get("BPS_NO")));//票据池编号
		pq.setSCustOrgCode(getStringVal(body.get("CORE_CLIENT_NO")));// 核心客户号
		pq.setQueryParam(getStringVal(body.get("QUERY_TYPE")));// 查询类型
		pq.setStartDate(getStringVal(body.get("START_DATE")));// 开始日期
		pq.setEndDate(getStringVal(body.get("EXPIRY_DATE")));// 结束日期
		return pq;

	}

	/**
	 *
	 * @Description: 构建响应数据
	 * @param result
	 * @return List
	 * @author tangxiongyu
	 */
	private List ResponseDataProcess(List result, String queryType) {
		ArrayList bailList = new ArrayList();
		if (queryType.equals("01")) {// 保证金当日划转明细
			for (int i = 0; i < result.size(); i++) {
				PedBailFlow bail = (PedBailFlow) result.get(i);
				Map infoMap = new HashMap();
				infoMap.put("TRAN_LIST_ARRAY.ACCOUNT_SEQ_NO", bail.getSerSeqNo());// 记账流水号
				infoMap.put("TRAN_LIST_ARRAY.PLATFORM_TRAN_DATE", bail.getDateTran());// 平台交易日期
				String str = bail.getTimeMch();//核心给的交易时间格式为： "2009-03-12-09.5.39.560000";
				String time = null;//返回给网银的时间格式为    09:5:39
				if(str!=null){
					String transTime =str.substring(11);
					String [] arr = transTime.split("\\.");
					 time = arr[0]+":"+arr[1]+":"+arr[2];
				}
				infoMap.put("TRAN_LIST_ARRAY.TRAN_TIME", time);// 交易时间
				
				infoMap.put("TRAN_LIST_ARRAY.CERT_NO", bail.getVouNo());// 凭证号
				if ("C".equals(bail.getFlgCd())) {// 借贷标志：来账
					infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_AMT", bail.getAmtTran());// 存入金额
					infoMap.put("TRAN_LIST_ARRAY.DRAW_AMT", "0");// 支取金额
				} else {// 往账
					infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_AMT", "0");// 存入金额
					infoMap.put("TRAN_LIST_ARRAY.DRAW_AMT", bail.getAmtTran());// 支取金额
				}
				infoMap.put("TRAN_LIST_ARRAY.MEMO_CODE", bail.getMemo());// 摘要码——换成摘要
				infoMap.put("TRAN_LIST_ARRAY.USAGE", bail.getField());// 用途
				infoMap.put("TRAN_LIST_ARRAY.TARG_ACCT_NO", bail.getAccNoA());// 对方账号
				infoMap.put("TRAN_LIST_ARRAY.TARG_ACCT_NAME", bail.getAccNamA2());// 对方账户名称
				infoMap.put("TRAN_LIST_ARRAY.BAL_AMT", bail.getBal());// 余额
				infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_ACCT_NO", bail.getAccNo());//保证金账户
				infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_ACCT_NAME", bail.getCustNam());// 保证金账户名称
				
				bailList.add(infoMap);
			}
		} else {// 保证金历史划转明细
			for (int i = 0; i < result.size(); i++) {
				PedBailHis bail = (PedBailHis) result.get(i);
				Map infoMap = new HashMap();
				infoMap.put("TRAN_LIST_ARRAY.ACCOUNT_SEQ_NO", bail.getSerSeqNo());// 记账流水号
				infoMap.put("TRAN_LIST_ARRAY.PLATFORM_TRAN_DATE", bail.getDateTran());// 平台交易日期
				String str = bail.getTimeMch();//核心给的交易时间格式为： "2009-03-12-09.5.39.560000";
				String time = null;//返回给网银的时间格式为    09:5:39
				if(str!=null){
					String transTime =str.substring(11);
					String [] arr = transTime.split("\\.");
					 time = arr[0]+":"+arr[1]+":"+arr[2];
				}
				infoMap.put("TRAN_LIST_ARRAY.TRAN_TIME", time);// 交易时间
				infoMap.put("TRAN_LIST_ARRAY.CERT_NO", bail.getVouNo());// 凭证号
				if ("C".equals(bail.getFlgCd())) {// 借贷标志：来账
					infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_AMT", bail.getAmtTran());// 存入金额
					infoMap.put("TRAN_LIST_ARRAY.DRAW_AMT", "0");// 支取金额
				} else {// 往账
					infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_AMT", "0");// 存入金额
					infoMap.put("TRAN_LIST_ARRAY.DRAW_AMT", bail.getAmtTran());// 支取金额
				}
				infoMap.put("TRAN_LIST_ARRAY.MEMO_CODE", bail.getMemo());// 摘要码--改为摘要
				infoMap.put("TRAN_LIST_ARRAY.USAGE", bail.getField());// 用途
				infoMap.put("TRAN_LIST_ARRAY.TARG_ACCT_NO", bail.getAccNoA());// 对方账号
				infoMap.put("TRAN_LIST_ARRAY.TARG_ACCT_NAME", bail.getAccNamA2());// 对方账户名称
				infoMap.put("TRAN_LIST_ARRAY.BAL_AMT", bail.getBal());// 余额
				infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_ACCT_NO", bail.getAccNo());//保证金账户
				infoMap.put("TRAN_LIST_ARRAY.DEPOSIT_ACCT_NAME", bail.getCustNam());// 保证金账户名称
				bailList.add(infoMap);
			}
		}

		return bailList;
	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
