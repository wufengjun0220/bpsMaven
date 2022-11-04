package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.base.service.PoolQueryService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;

/**
 * @Description 票据池出入流水查询
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC067RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC067RequestHandler.class);
	@Autowired
    private PoolQueryService poolQueryService;
	@Autowired
	private DraftPoolOutService draftPoolOutService;
	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Page page = getPage(request.getAppHead());
        Ret ret = new Ret();
        try {
	        Map map = request.getBody();
	        String client = getStringVal(map.get("CORE_CLIENT_NO"));//核心客户号   
	        String bpsNo = getStringVal(map.get("BPS_NO"));//票据池编号   
	        String billNo = getStringVal(map.get("BILL_NO"));//票号         
	        
			/********************融合改造新增 start******************************/
	        String beginRangeNo = "";
			String endRangeNo = "";
			if(StringUtils.isNotEmpty(getStringVal(request.getBody().get("START_BILL_NO")))){
				beginRangeNo = getStringVal(request.getBody().get("START_BILL_NO")); // 子票起始号
			}
			if(StringUtils.isNotEmpty(getStringVal(request.getBody().get("END_BILL_NO")))){
				endRangeNo = getStringVal(request.getBody().get("END_BILL_NO")); // 子票截止
			}
			String billSource = getStringVal(map.get("BILL_SOURCE"));
			
			/********************融合改造新增 end******************************/
	        
	        String billType = getStringVal(map.get("BILL_CLASS"));//票据属性     --网银的字段码值给错了，在这里做转换
	        String billClass = getStringVal(map.get("BILL_TYPE"));//票据类型     --网银的字段码值给错了，在这里做转换
	        String maxAmt = getStringVal(map.get("MAX_BILL_AMT"));//票据金额上限 
	        String minAmt = getStringVal(map.get("MIN_BILL_AMT"));//票据金额下限 
	        String drawStart = getStringVal(map.get("DRAW_START_DATE"));//出票日上限   
	        String drawEnd = getStringVal(map.get("DRAW_END_DATE"));//出票日下限   
	        String dueStart = getStringVal(map.get("EXPIRY_START_DATE"));//到期日上限   
	        String dueEnd = getStringVal(map.get("EXPIRY_END_DATE"));//到期日下限   
	        String operStart = getStringVal(map.get("OPER_START_DATE"));//操作日期上限 
	        String operEnd = getStringVal(map.get("OPER_END_DATE"));//操作日期下限 
	        String operType = getStringVal(map.get("OPERATION_TYPE"));//操作类型     

			List addList = new ArrayList();
			List poolInList = new ArrayList();
			List poolOutList = new ArrayList();
			DraftQueryBean bean=new DraftQueryBean();
			bean.setCustNo(client);
			bean.setPoolAgreement(bpsNo);
			bean.setPlDraftNb(billNo);
			bean.setPlDraftMedia(billClass);
			bean.setAssetType(billType);
			bean.setStartassetAmt(getBigDecimalVal(minAmt));
			bean.setEndassetAmt(getBigDecimalVal(maxAmt));
			bean.setStartplIsseDt(getDateVal(drawStart));
			bean.setEndplIsseDt(getDateVal(drawEnd));
			bean.setStartplDueDt(getDateVal(dueStart));
			bean.setEndplDueDt(getDateVal(dueEnd));
			bean.setStartplReqTime(getDateVal(operStart));
			bean.setEndplReqTime(getDateVal(operEnd));
			
			/********************融合改造新增 start******************************/
			bean.setBeginRangeNo(beginRangeNo);
			bean.setEndRangeNo(endRangeNo);
			bean.setDraftSource(billSource);
			/********************融合改造新增 end******************************/
			
			
			QueryResult result = null;
			QueryResult result1 = null;
			QueryResult result2 = null;
			if(null!=operType && operType.equals("0")){//入池
				result	=poolQueryService.toPoolInByQueryBean(bean, page);
			}else if(null!=operType && operType.equals("1")){//出池
				result1	=draftPoolOutService.toPoolOutByQueryBean(bean, page);
			}
			else if(StringUtil.isEmpty(operType)){//出入池
				
				result2	=poolQueryService.toPoolByQueryBean(bean, page);
			}
			if ((null == result || result.getTotalCount() == 0) && (null == result1 || result1.getTotalCount() == 0 ) && (null == result2 || result2.getTotalCount() == 0 )) {
				ret.setRET_MSG("无符合条件数据");
				ret.setRET_CODE(Constants.EBK_03);
			}else{
				BigDecimal decimal = new BigDecimal(0);
				if(result != null && result.getRecords().size()>0){//入池
					for (int i = 0; i < result.getRecords().size(); i++) {
						DraftPoolIn poolIn=(DraftPoolIn)result.getRecords().get(i);
						Map addMap = new HashMap();
						addMap.put("BILL_INFO_ARRAY.BILL_NO", poolIn.getPlDraftNb()); //票据号码          
						if(StringUtils.isNotBlank(poolIn.getDraftSource()) && poolIn.getDraftSource().equals(PoolComm.CS02)){
							addMap.put("BILL_INFO_ARRAY.START_BILL_NO", poolIn.getBeginRangeNo()); //票据号码       起
							addMap.put("BILL_INFO_ARRAY.END_BILL_NO", poolIn.getEndRangeNo()); //票据号码          止
							addMap.put("BILL_INFO_ARRAY.SPLIT_FLAG", poolIn.getSplitFlag()); //是否可拆分
						}
						addMap.put("BILL_INFO_ARRAY.BILL_CLASS", poolIn.getPlDraftType()); //票据属性          
						addMap.put("BILL_INFO_ARRAY.BILL_TYPE", poolIn.getPlDraftMedia()); //票据类型          
						addMap.put("BILL_INFO_ARRAY.BILL_AMT", poolIn.getPlIsseAmt()); //票据金额          
						addMap.put("BILL_INFO_ARRAY.DRAW_DATE", DateUtils.toString(poolIn.getPlIsseDt(),DateUtils.ORA_DATE_FORMAT)); //出票日期          
						addMap.put("BILL_INFO_ARRAY.EXPIRY_DATE", DateUtils.toString(poolIn.getPlDueDt(),DateUtils.ORA_DATE_FORMAT)); //汇票到期日        
						addMap.put("BILL_INFO_ARRAY.BILL_CLIENT_NAME", poolIn.getPlDrwrNm()); //出票人名称        
						addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NO", poolIn.getPlDrwrAcctId()); //出票人账号        
						addMap.put("BILL_INFO_ARRAY.BILL_BANK_NAME", poolIn.getPlDrwrAcctSvcrNm()); //出票人开户行行名  
						addMap.put("BILL_INFO_ARRAY.BILL_BANK_NO", poolIn.getPlDrwrAcctSvcr()); //出票人开户行行号  
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NAME", poolIn.getPlAccptrSvcrNm()); //承兑人/行名称     
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NO", poolIn.getPlAccptrSvcr()); //承兑行行号        
						addMap.put("BILL_INFO_ARRAY.PAYEE_CLIENT_NAME", poolIn.getPlPyeeNm()); //收款人名称        
						addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", poolIn.getPlPyeeAcctId()); //收款人账号        
						addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NAME", poolIn.getPlPyeeAcctSvcrNm()); //收款人开户行行名  
						addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NO", poolIn.getPlPyeeAcctSvcr()); //收款人开户行号    
						//表中存储的转让标识 0 是可转让 1 不可转让    返给网银时需转换为0不可转让  1 可转让
						if(StringUtil.isNotEmpty(poolIn.getForbidFlag()) && poolIn.getForbidFlag().equals("0")){//可转让
							addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "1"); //可转让        
						}else{
							addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "0"); //不可转让        
						}
						logger.info("操作时间："+poolIn.getPlReqTime());
						addMap.put("BILL_INFO_ARRAY.OPER_DATE", DateUtils.toString(poolIn.getPlReqTime(),DateUtils.ORA_DATE_FORMAT)); //操作日期          
						addMap.put("BILL_INFO_ARRAY.OPER_TIME", DateUtils.toString(poolIn.getPlReqTime(),DateUtils.ORA_DATE_FORMAT3)); //操作时间     
						addMap.put("BILL_INFO_ARRAY.OPERATION_TYPE", "0"); //入池 
						
						addMap.put("BILL_INFO_ARRAY.BILL_SOURCE", poolIn.getDraftSource()); //票据来源
						
						addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NAME", poolIn.getPlDrwrAcctName()); //出票人账号名称
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", poolIn.getPlAccptrNm()); //承兑人名称
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", poolIn.getPlAccptrId()); //承兑人账号
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", poolIn.getPlAccptrAcctName()); //承兑人账户名称
						addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", poolIn.getPlPyeeAcctName()); //收款人账号名称
						
						addMap.put("BILL_INFO_ARRAY.BILL_ID", poolIn.getSOperatorId()); //票据id
						addMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID", poolIn.getAccNo()); //持票账号
						
						addList.add(addMap);
						}
				}else if(result1 != null && result1.getRecords().size()>0){//出池
					for (int i = 0; i < result1.getRecords().size(); i++) {
						DraftPoolOut poolOut=(DraftPoolOut)result1.getRecords().get(i);
						Map addMap = new HashMap();
						addMap.put("BILL_INFO_ARRAY.BILL_NO", poolOut.getPlDraftNb()); //票据号码          
						if(StringUtils.isNotBlank(poolOut.getDraftSource()) && poolOut.getDraftSource().equals(PoolComm.CS02)){
							addMap.put("BILL_INFO_ARRAY.START_BILL_NO", poolOut.getBeginRangeNo()); //票据号码       起
							addMap.put("BILL_INFO_ARRAY.END_BILL_NO", poolOut.getEndRangeNo()); //票据号码          止
							addMap.put("BILL_INFO_ARRAY.SPLIT_FLAG", poolOut.getSplitFlag()); //是否可拆分
						}
						addMap.put("BILL_INFO_ARRAY.BILL_CLASS", poolOut.getPlDraftType()); //票据属性          
						addMap.put("BILL_INFO_ARRAY.BILL_TYPE", poolOut.getPlDraftMedia()); //票据类型          
						addMap.put("BILL_INFO_ARRAY.BILL_AMT", poolOut.getPlIsseAmt()); //票据金额          
						addMap.put("BILL_INFO_ARRAY.DRAW_DATE", DateUtils.toString(poolOut.getPlIsseDt(),DateUtils.ORA_DATE_FORMAT)); //出票日期          
						addMap.put("BILL_INFO_ARRAY.EXPIRY_DATE", DateUtils.toString(poolOut.getPlDueDt(),DateUtils.ORA_DATE_FORMAT)); //汇票到期日        
						addMap.put("BILL_INFO_ARRAY.BILL_CLIENT_NAME", poolOut.getPlDrwrNm()); //出票人名称        
						addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NO", poolOut.getPlDrwrAcctId()); //出票人账号        
						addMap.put("BILL_INFO_ARRAY.BILL_BANK_NAME", poolOut.getPlDrwrAcctSvcrNm()); //出票人开户行行名  
						addMap.put("BILL_INFO_ARRAY.BILL_BANK_NO", poolOut.getPlDrwrAcctSvcr()); //出票人开户行行号  
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NAME", poolOut.getPlAccptrSvcrNm()); //承兑人/行名称     
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NO", poolOut.getPlAccptrSvcr()); //承兑行行号        
						addMap.put("BILL_INFO_ARRAY.PAYEE_CLIENT_NAME", poolOut.getPlPyeeNm()); //收款人名称        
						addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", poolOut.getPlPyeeAcctId()); //收款人账号        
						addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NAME", poolOut.getPlPyeeAcctSvcrNm()); //收款人开户行行名  
						addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NO", poolOut.getPlPyeeAcctSvcr()); //收款人开户行号    
						//表中存储的转让标识 0 是可转让 1 不可转让    返给网银时需转换为0不可转让  1 可转让
						if(StringUtil.isNotEmpty(poolOut.getForbidFlag()) && poolOut.getForbidFlag().equals("0")){//可转让
							addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "1"); //可转让        
						}else{
							addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "0"); //不可转让        
						}
						logger.info("操作时间："+poolOut.getPlReqTime());
						addMap.put("BILL_INFO_ARRAY.OPER_DATE", DateUtils.toString(poolOut.getPlReqTime(),DateUtils.ORA_DATE_FORMAT)); //操作日期          
						addMap.put("BILL_INFO_ARRAY.OPER_TIME", DateUtils.toString(poolOut.getPlReqTime(),DateUtils.ORA_DATE_FORMAT3)); //操作时间     
						addMap.put("BILL_INFO_ARRAY.OPERATION_TYPE", "1"); //出池  
						
						addMap.put("BILL_INFO_ARRAY.BILL_SOURCE", poolOut.getDraftSource()); //票据来源
						
						addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NAME", poolOut.getPlDrwrAcctName()); //出票人账号名称
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", poolOut.getPlAccptrNm()); //承兑人名称
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", poolOut.getPlAccptrId()); //承兑人账号
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", poolOut.getPlAccptrAcctName()); //承兑人账户名称
						addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", poolOut.getPlPyeeAcctName()); //收款人账号名称
						
						addMap.put("BILL_INFO_ARRAY.BILL_ID", poolOut.getSOperatorId()); //票据id
						addMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID", poolOut.getAccNo()); //持票账号
						
						addList.add(addMap);
						}
				}else if(result2 != null && result2.getRecords().size()>0){
					
					for (int i = 0; i < result2.getRecords().size(); i++) {
						Map addMap = new HashMap();
						Object[] obj=(Object[]) result2.getRecords().get(i);
						addMap.put("BILL_INFO_ARRAY.BILL_NO", obj[0]); //票据号码          
						addMap.put("BILL_INFO_ARRAY.BILL_CLASS", obj[1]); //票据属性          
						addMap.put("BILL_INFO_ARRAY.BILL_TYPE", obj[2]); //票据类型          
						addMap.put("BILL_INFO_ARRAY.BILL_AMT", obj[3]); //票据金额          
						addMap.put("BILL_INFO_ARRAY.DRAW_DATE", DateUtils.toString((Date)obj[4],DateUtils.ORA_DATE_FORMAT)); //出票日期          
						addMap.put("BILL_INFO_ARRAY.EXPIRY_DATE", DateUtils.toString((Date)obj[5],DateUtils.ORA_DATE_FORMAT)); //汇票到期日        
						addMap.put("BILL_INFO_ARRAY.BILL_CLIENT_NAME", obj[6]); //出票人名称        
						addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NO", obj[7]); //出票人账号        
						addMap.put("BILL_INFO_ARRAY.BILL_BANK_NAME", obj[8]); //出票人开户行行名  
						addMap.put("BILL_INFO_ARRAY.BILL_BANK_NO", obj[9]); //出票人开户行行号  
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NAME", obj[10]); //承兑人/行名称     
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_BANK_NO", obj[11]); //承兑行行号        
						addMap.put("BILL_INFO_ARRAY.PAYEE_CLIENT_NAME", obj[12]); //收款人名称        
						addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", obj[13]); //收款人账号        
						addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NAME", obj[14]); //收款人开户行行名  
						addMap.put("BILL_INFO_ARRAY.PAYEE_BANK_NO", obj[15]); //收款人开户行号    
						//表中存储的转让标识 0 是可转让 1 不可转让    返给网银时需转换为0不可转让  1 可转让
						if(obj[16] != null && obj[16].equals("0")){//可转让
							addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "1"); //可转让        
						}else{
							addMap.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "0"); //不可转让        
						}
						logger.info("操作时间："+obj[17]);
						addMap.put("BILL_INFO_ARRAY.OPER_DATE", DateUtils.toString(DateUtils.StringToDate((String)obj[17], DateUtils.ORA_DATE_TIMES3_FORMAT),DateUtils.ORA_DATE_FORMAT)); //操作日期          
						addMap.put("BILL_INFO_ARRAY.OPER_TIME", DateUtils.toString(DateUtils.StringToDate((String)obj[17], DateUtils.ORA_DATE_TIMES3_FORMAT),DateUtils.ORA_DATE_FORMAT3)); //操作时间     
						addMap.put("BILL_INFO_ARRAY.OPERATION_TYPE", obj[18]); //出池  
						String draftSource = (String) obj[21];
						if(StringUtils.isNotBlank(draftSource) && draftSource.equals(PoolComm.CS02)){
							addMap.put("BILL_INFO_ARRAY.START_BILL_NO", obj[19]); //票据号码       起
							addMap.put("BILL_INFO_ARRAY.END_BILL_NO", obj[20]); //票据号码          止
							addMap.put("BILL_INFO_ARRAY.SPLIT_FLAG", obj[22]); //是否可拆分
						}
						
						addMap.put("BILL_INFO_ARRAY.BILL_SOURCE", obj[21]); //票据来源
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", obj[23]); //承兑人账户名称
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", obj[24]); //承兑人账号
						addMap.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", obj[25]); //承兑人名称
						addMap.put("BILL_INFO_ARRAY.BILL_ACCT_NAME", obj[26]); //出票人账户名称
						addMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", obj[27]); //收款人账号名称
						
						addMap.put("BILL_INFO_ARRAY.BILL_ID", obj[28]); //票据id
						addMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID", obj[29]); //持票账号
						
						decimal = decimal.add((BigDecimal)obj[3]);
						addList.add(addMap);
						}
				}
				BigDecimal big = new BigDecimal(0);
				if(result != null && result.getTotalAmount() != null){
					big = big.add(result.getTotalAmount());
				}else if(result1 != null && result1.getTotalAmount() != null){
					big = big.add(result1.getTotalAmount());
				}else if(result2 != null && result2.getTotalCount() > 0){
					big = big.add(decimal);
				}
				setPage(response.getAppHead(), page,big.toString());
//				response.getAppHead().put("TOTAL_ROWS", addList.size());// 总记录数
				response.getBody().put("TOTAL_AMT", big);      //总金额
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
				response.setDetails(addList);
			}

	 	     response.getBody().put("CORE_CLIENT_NO", client); //核心客户号   
	 	     response.getBody().put("BPS_NO", bpsNo);      //票据池编号
        
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池出入流水查询异常");
		
		}
		response.setRet(ret);
        return response;
    }

}
