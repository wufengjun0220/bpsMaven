package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.application.pool.edu.domain.PedCheckList;
import com.mingtech.framework.core.page.Page;

/**
 * 网银接口——票据池对账明细接口
 * @Description 
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-13
 */
public class PJC045RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC045RequestHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类
	/**
	 * （1）查询出客户需要对账的文件（PED_CHECK）
	 * （2）查询出需要对账文件对应的明细（PED_CHECK_LIST）
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Page page = getPage(request.getAppHead());
		try {
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String poolAgreement = getStringVal(request.getBody().get("BPS_NO"));
			//String batchNo = getStringVal(request.getBody().get("RECON_BATCH_NO"));
			//获取对账信息
			List<PedCheck> bodyList = poolEBankService.queryPedCheck(poolAgreement, custNo);
			if(bodyList!=null && bodyList.size()>0){
				
				PedCheck bodyInfo = bodyList.get(0);
				response.setBody(dataProcessBody(bodyInfo));// 报文体主数据组装
				if(StringUtils.isNotBlank(bodyInfo.getBatchNo())){//获取对账明细
					QueryResult queryResult= pedProtocolService.queryPedCheckList(bodyInfo.getBatchNo(),page);
					List<PedCheckList> detailList = queryResult.getRecords();
					response.setDetails(dataProcess(detailList));// 报文体明细数据组装
					response.getBody().put("TOTAL_AMT", queryResult.getTotalAmount());
					setPage(response.getAppHead(), page);
				}else{
					response.getAppHead().put("TOTAL_NUM", "0");
					response.getAppHead().put("CURRENT_NUM", "0");
					response.getAppHead().put("TOTAL_ROWS", "0");
					response.getAppHead().put("TOTAL_PAGES", "0");
					response.getBody().put("TOTAL_AMT", "0.00");
				}
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("查询成功！");
			}else{
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("无符合条件数据！");
			}

		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池签约查询异常");
		}
		response.setRet(ret);
		return response;
	}
	/**
	 * 返回报文体组装
	 * @Description TODO
	 * @author Ju Nana
	 * @param ch
	 * @return
	 * @date 2019-6-13下午8:21:18
	 */
	private Map dataProcessBody(PedCheck ch) {
		Map body = new HashMap();
		body.put("IS_GROUP", ch.getIsGroup());//是否集团
		body.put("ACCOUNT_DATE", ch.getAccountDate());//账务日期
		body.put("CLIENT_NAME", ch.getCustName());//出质人名称(客户名称)
		body.put("SIGN_BANK_NAME", ch.getCollztnBkNm());//质权人名称(签约行名)
		body.put("DEPOSIT_ACCT_NO", ch.getMarginAccount());//保证金账户
		body.put("DEPOSIT_ACCT_NAME", ch.getMarginAccountName());//保证金账户名称
		body.put("DEPOSIT_ACCT_BALANCE", ch.getMarginBalance());//保证金账户余额
		body.put("BILL_NUM", ch.getBillTotalNum());//票据张数总计
		body.put("BILL_TOTAL_AMT", ch.getBillTotalAmount());//票据金融总计
		body.put("RECON_BATCH_NO", ch.getBatchNo());//对账批次号
		return body;
	}
	/**
	 * 返回报文数组明细组装
	 * @Description TODO
	 * @author Ju Nana
	 * @param chList
	 * @return
	 * @throws Exception
	 * @date 2019-6-13下午8:21:32
	 */
	private List<Map> dataProcess(List<PedCheckList> chList) throws Exception {
		List<Map> detail = new ArrayList();
		for (PedCheckList ch : chList) {
			Map chListMap = new HashMap<String, String>();
			chListMap.put("BILL_INFO_ARRAY.BILL_TYPE", ch.getSbillMedia());//票据介质
			chListMap.put("BILL_INFO_ARRAY.BILL_CLASS", ch.getBillType());//票据类型
			chListMap.put("BILL_INFO_ARRAY.BILL_NO", ch.getBillNo());//票据号码
			

			/********************融合改造新增 start******************************/
			if((!ch.getBillNo().substring(0, 1).equals("1") && !ch.getBillNo().substring(0, 1).equals("2")) && !ch.getSbillMedia().equals("1")){
				chListMap.put("BILL_INFO_ARRAY.START_BILL_NO", ch.getBeginRangeNo());//票据号码起
				chListMap.put("BILL_INFO_ARRAY.END_BILL_NO", ch.getEndRangeNo());//票据号码止
			}
			
			chListMap.put("BILL_INFO_ARRAY.GUARANTY_NO", ch.getPledgeNo());//质押清单编号
			/********************融合改造新增 end******************************/
			
			chListMap.put("BILL_INFO_ARRAY.BILL_AMT", ch.getBillAmount());//票据金额
			chListMap.put("BILL_INFO_ARRAY.DRAW_DATE", ch.getIsseDt());//出票日期
			chListMap.put("BILL_INFO_ARRAY.EXPIRY_DATE", ch.getDueDt());//到期日期
			chListMap.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", ch.getAccptrNm());//承兑人名称
			detail.add(chListMap);
		}
		return detail;
	}
	


}
