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
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.domain.PedAssetDaily;
import com.mingtech.application.pool.query.service.AssetCrdtDailyService;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @Description 时点票据详情明细查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC069RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC069RequestHandler.class);
	@Autowired
    private AssetCrdtDailyService assetCrdtDailyService;
	@Autowired
    private CommonQueryService commonQueryService;
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        try {
	        Map map = request.getBody();
	        String coreNo = getStringVal(map.get("CORE_CLIENT_NO"));//核心客户号
	        String bpsNo = getStringVal(map.get("BPS_NO"));//票据池编号
	        String queryDate = getStringVal(map.get("QUERY_DATE"));//查询日期
	        Page page = getPage(request.getAppHead());
	
			List addList = new ArrayList();
			List details = new ArrayList(); //查询list 结果
			CommonQueryBean queryBean=new CommonQueryBean();
			queryBean.setBpsNo(bpsNo);
			queryBean.setCustNo(coreNo);
			queryBean.setCreateDate(DateUtils.StringToDate(queryDate,"yyyyMMdd"));
			queryBean.setBusiType("01");//票据
			addList=assetCrdtDailyService.loadPedAssetDaily(queryBean,page);
			QueryResult result =commonQueryService.loadDataByResult(addList, "amt");

			
			if ( result.getRecords().size() > 0) {
				for (int i = 0; i < result.getRecords().size(); i++) {
					PedAssetDaily pedAssetDaily=(PedAssetDaily)result.getRecords().get(i);
					Map addMap = new HashMap();
					addMap.put("BILL_INFO_ARRAY.BILL_NO", pedAssetDaily.getBillNo());   //票据号码                 

					/********************融合改造新增 start******************************/
					logger.info("票据号码 ："+pedAssetDaily.getBillNo()+"；票据号码起："+pedAssetDaily.getBeginRangeNo()+"；票据号码止："+pedAssetDaily.getEndRangeNo()+"；票据来源："+pedAssetDaily.getDraftSource());
					if(StringUtil.isNotBlank(pedAssetDaily.getDraftSource()) && pedAssetDaily.getDraftSource().equals(PoolComm.CS02)){
						addMap.put("BILL_INFO_ARRAY.START_BILL_NO", pedAssetDaily.getBeginRangeNo());   //票据号码起
						addMap.put("BILL_INFO_ARRAY.END_BILL_NO", pedAssetDaily.getEndRangeNo());   //票据号码止
						addMap.put("BILL_INFO_ARRAY.SPLIT_FLAG", pedAssetDaily.getSplitFlag());   //是否可拆分
					}
					addMap.put("BILL_INFO_ARRAY.BILL_SOURCE", pedAssetDaily.getDraftSource());   //票据来源
					addMap.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", pedAssetDaily.getPlAccptr());   //承兑人名称
					addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", pedAssetDaily.getPlPyeeAcctName());   //收款人账户名称
					addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", pedAssetDaily.getPlAccptrAcctNo());   //承兑人账号
					addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", pedAssetDaily.getPlAccptrAcctName());   //承兑人账户名称
					
					
					
					/********************融合改造新增 end******************************/

					addMap.put("BILL_INFO_ARRAY.BILL_CLASS", pedAssetDaily.getBillType());   //票据属性                 
					addMap.put("BILL_INFO_ARRAY.BILL_TYPE", pedAssetDaily.getBillMedia());   //票据类型                 
					addMap.put("BILL_INFO_ARRAY.BILL_AMT", pedAssetDaily.getAmt());   //票据金额                 
					addMap.put("BILL_INFO_ARRAY.DRAW_DATE", DateUtils.toString(pedAssetDaily.getIssueDt(), "yyyyMMdd"));   //出票日期                 
					addMap.put("BILL_INFO_ARRAY.EXPIRY_DATE", DateUtils.toString(pedAssetDaily.getDueDt(), "yyyyMMdd"));   //汇票到期日               
					addMap.put("BILL_INFO_ARRAY.BILL_CLIENT_NAME", pedAssetDaily.getDrwrName());   //出票人名称               
					addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NAME", pedAssetDaily.getSIssuerAcctName());   //出票人账户名称               
					addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NO", pedAssetDaily.getSIssuerAccount());   //出票人账号               
					addMap.put("BILL_INFO_ARRAY.BILL_BANK_NAME", pedAssetDaily.getAcptName());   //出票人开户行行名         
					addMap.put("BILL_INFO_ARRAY.BILL_BANK_NO", pedAssetDaily.getDrwrBankNo());   //出票人开户行行号         
					addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NAME", pedAssetDaily.getAcptName());   //承兑人/行名称            
					addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NO", pedAssetDaily.getAcptBankNo());   //承兑行行号               
					addMap.put("BILL_INFO_ARRAY.PAYEE_CLIENT_NAME", pedAssetDaily.getPyeeName());   //收款人名称               
					addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", pedAssetDaily.getSPayeeAccount());   //收款人账号               
					addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NAME", pedAssetDaily.getPyeeBankName());   //收款人开户行行名         
					addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NO", pedAssetDaily.getPyeeBankNo());   //收款人开户行号           
					if ("1".equals(pedAssetDaily.getBanEndrsmtFlag())) {// 不得转让
						addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "0");   //是否可转让    
					} else {
						addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "1");// 可转让
					}
					details.add(addMap);
					}
				setPage(response.getAppHead(), page,result.getTotalAmount().toString());
			    response.getBody().put("CORE_CLIENT_NO", coreNo);      //核心客户号   
		 	    response.getBody().put("BPS_NO", bpsNo);      //票据池编号
		 	    response.getBody().put("TOTAL_AMT", result.getTotalAmount());      //总金额
				response.setDetails(details);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
			} else {
			    ret.setRET_MSG("无符合条件数据");
				ret.setRET_CODE(Constants.EBK_03);
			}
        
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("时点票据详情明细查询异常");
		
		}
		response.setRet(ret);
        return response;
    }

}
