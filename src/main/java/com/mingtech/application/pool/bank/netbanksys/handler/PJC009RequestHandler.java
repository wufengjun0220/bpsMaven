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
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.framework.core.page.Page;

/**
 * 
 * @Title: 网银查询接口PJC009
 * @Description: 信贷产品查询接口
 * @author Ju Nana
 * @date 2018-10-22
 */
public class PJC009RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC009RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;// 网银方法类

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Page page = getPage(request.getAppHead());
		Ret ret = new Ret();
		QueryResult result = null;
		try {
			PoolQueryBean queryBean = QueryParamMap(request);
			result = poolEBankService.queryUsedCreditProductPJC009(queryBean,page);
			if (null == result || result.getTotalCount() == 0) {
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("没有符合条件的数据！");
			} else {
				setPage(response.getAppHead(), page, result.getTotalAmount()+"");
				response.getBody().put("TOTAL_AMT", result.getTotalAmount());
//				response.getAppHead().put("TOTAL_ROWS", result.getTotalCount());// 总记录数
//				response.getAppHead().put("TOTAL_AMT", result.getTotalAmount());// 总金额
				List details = this.resultDataHandler(result.getRecords());
				response.setDetails(details);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("查询成功！");
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
	 * @throws @author Ju Nana
	 * @date 2018-10-16 上午11:29:55
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {

		PoolQueryBean pq = new PoolQueryBean();
		Map body = request.getBody();
		pq.setSCustOrgCode(getStringVal(body.get("CORE_CLIENT_NO"))); // 核心客户号
		pq.setProtocolNo(getStringVal(body.get("BPS_NO"))); // 协议编号
		pq.setCtrctNb(getStringVal(body.get("CONTRACT_NO"))); // 融资业务合同号
		pq.setPstartDate(getDateVal(body.get("CONTRACT_START_DATE"))); // 合同开始时间
		pq.setPendDate(getDateVal(body.get("CONTRACT_EXPIRY_DATE"))); // 合同结束时间
		pq.setQueryParam(getStringVal(body.get("FINANCING_TYPE"))); // 融资类型
		pq.setReceiptNum(getStringVal(body.get("IOU_NO"))); // 借据号
		pq.setStartContract(getDateVal(body.get("IOU_START_DATE"))); // 借据起始日
		pq.setEndContract(getDateVal(body.get("IOU_END_DATE"))); // 借据到期日
		pq.setReceiptMax(getBigDecimalVal(body.get("MAX_IOU_AMT"))); // 借据金额最大值
		pq.setReceiptMin(getBigDecimalVal(body.get("MIN_IOU_AMT"))); // 借据金额最小值
		System.out.println("借据金额最大值ReceiptMax:"+getBigDecimalVal(body.get("MAX_IOU_AMT"))+"借据金额最小值ReceiptMin:"+getBigDecimalVal(body.get("MIN_IOU_AMT")));
		return pq;
	}

	/**
	 * 处理响应数据
	 * 
	 * @return List
	 * @author tangxiongyu
	 */
	private List resultDataHandler(List result) {
		ArrayList infoList = new ArrayList();
		if (result != null && result.size() > 0) {
			HashMap map = null;
			PedCreditDetail detail = null;
			for (int i = 0; i < result.size(); i++) {
				map = new HashMap();
				detail = (PedCreditDetail) result.get(i);
				map.put("IOU_INFO_ARRAY.CONTRACT_NO", detail.getCrdtNo());// 融资业务合同号
				map.put("IOU_INFO_ARRAY.IOU_NO", detail.getLoanNo());// 借据号
				map.put("IOU_INFO_ARRAY.FINANCING_TYPE", detail.getLoanType());// 融资类型
				map.put("IOU_INFO_ARRAY.IOU_AMT", detail.getLoanAmount());// 借据金额
				map.put("IOU_INFO_ARRAY.IOU_BAL_AMT", detail.getActualAmount());// 借据余额
				map.put("IOU_INFO_ARRAY.DEPOSIT_BAL_AMT", detail.getBailAccAmt());// 保证金余额
				map.put("IOU_INFO_ARRAY.IOU_APPLY_DATE", detail.getStartTime());// 借据申请日
				map.put("IOU_INFO_ARRAY.IOU_EXPIRY_DATE", detail.getEndTime());// 借据到期日

				infoList.add(map);
			}
		}
		return infoList;
	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
