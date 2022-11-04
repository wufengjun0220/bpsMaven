package com.mingtech.application.pool.bank.coresys.service.impl;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.coresys.CoreClient;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;


/**
 * 票据池交易 核心接口服务实现类 
 * @author yixiaolong
 */
@Service("poolCoreService")
public class PoolCoreServiceImpl implements PoolCoreService {
	
	private static final Logger logger = Logger.getLogger(PoolCoreServiceImpl.class);
	@Autowired 
	CoreClient coreClient;


	@Override
	public ReturnMessageNew PJH580314Handler(CoreTransNotes poolNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		if(poolNotes.getAmtAct() != null && BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(poolNotes.getAmtAct())) < 2){
			request.getBody().put("GUARANTEE_AMT", BigDecimalUtils.getStringValue(new BigDecimal(poolNotes.getAmtAct())));//交易金额
		}else{
			request.getBody().put("GUARANTEE_AMT",poolNotes.getAmtAct());//交易金额
		}
		request.getBody().put("BILL_NO", poolNotes.getBilCode());// 票据号码    
		
		/********************融合改造新增 start******************************/
		request.getBody().put("START_BILL_NO",poolNotes.getBeginRangeNo());//票据开始子区间号
		request.getBody().put("END_BILL_NO",poolNotes.getEndRangeNo());//票据结束子区间号
		/********************融合改造新增 end******************************/
		
		request.getBody().put("MGM_BRANCH_ID", poolNotes.getBrcNo());// 管理机构         
		request.getBody().put("CCY", "01");// 币种                     
		request.getBody().put("GUARANTEE_NO", poolNotes.getColNo());// 担保品编号    
		request.getBody().put("CLIENT_NO", poolNotes.getCustId());// 客户号          
		request.getBody().put("EXPIRY_DATE", poolNotes.getDateDue());// 到期日期     
		request.getBody().put("THIRD_SEQ_NO", poolNotes.getDevSeqNo());// 第三方流水?
		request.getBody().put("CONTRACT_NO", poolNotes.getNoVouCom());// 合同号      
		request.getBody().put("PRINT_BATCH_NO", poolNotes.getNumBatch());// 打印批号 
		request.getBody().put("GUARANTY_TYPE", poolNotes.getTypGag());// 抵质押物类型
		request.getBody().put("ASSURE_CLASS", "224");// 担保品种类
		request.getSysHead().put("BRANCH_ID", poolNotes.getBrcBld());//报文头的机构号
		request.getSysHead().put("USER_ID", "p"+poolNotes.getBrcBld());//柜员号
		ReturnMessageNew response = coreClient.processCore("580314", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew PJH580316Handler(CoreTransNotes poolNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_NO", poolNotes.getBilCode());// 票据号码 
		
		if(poolNotes.getAmt() != null && BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(poolNotes.getAmt())) < 2){
			request.getBody().put("BILL_AMT", BigDecimalUtils.getStringValue(new BigDecimal(poolNotes.getAmt())));//交易金额
		}else{
			request.getBody().put("BILL_AMT",poolNotes.getAmt());//交易金额
		}
		
		request.getBody().put("GUARANTEE_NO", poolNotes.getColNo());// 担保编号
		
		
		/********************融合改造新增 start******************************/
		request.getBody().put("START_BILL_NO",poolNotes.getBeginRangeNo());//票据开始子区间号
		request.getBody().put("END_BILL_NO",poolNotes.getEndRangeNo());//票据结束子区间号
		request.getBody().put("IN_STORE_GUARANTEE_NO", poolNotes.getInColNo());// 入库担保编号
		request.getBody().put("OUT_STORE_GUARANTEE_NO", poolNotes.getOutColNo());// 出库担保编号
		
		/********************融合改造新增 end******************************/
		
		
		
		request.getBody().put("THIRD_SEQ_NO", poolNotes.getDevSeqNo());// 第三方流水号 
		request.getBody().put("OUTGOING_REASON", poolNotes.getRemark());// 出库原因   
		request.getSysHead().put("BRANCH_ID", poolNotes.getBrcBld());//报文头的机构号
		request.getSysHead().put("USER_ID", "p"+poolNotes.getBrcBld());//柜员号
		ReturnMessageNew response = coreClient.processCore("580316", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew PJH854111Handler(CoreTransNotes poolNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("OPEN_BRANCH", "");// 账号
		request.getBody().put("CLIENT_NO", poolNotes.getCustIdA ());//客户号 
		request.getBody().put("GLOBAL_ID", "");//证件号码
		request.getBody().put("GLOBAL_TYPE","");//证件类型
		request.getSysHead().put("BRANCH_ID", poolNotes.getBrcNo());//头机构号

		ReturnMessageNew response = coreClient.processCore("854111", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew PJH716040Handler(CoreTransNotes transNotes,String queryType)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("ACCT_NO", transNotes.getAccNo());//账号                     
		request.getBody().put("QUERY_FLAG", "10000000000");//查询标识      
		/*
		if("0".equals(queryType)){//保证金余额查询
			request.getBody().put("QUERY_STATICG_FLAG", "0");//是否只查询账户静态信息   ：否
		}else {//保证金账户查询
			request.getBody().put("QUERY_STATICG_FLAG", "1");//是否只查询账户静态信息   ：是	
		}
		*/
		request.getBody().put("QUERY_STATICG_FLAG", "0");//是否只查询账户静态信息   
		request.getBody().put("QUERY_CLO_FLAG", "1");//是否只查询已销户账户信息 
		request.getBody().put("CCY", "01");//币种                     
		request.getBody().put("SUCC_FLAG", "");//汇钞标志                 
		request.getBody().put("TRK_CHECK_FLAG", "");//是否检查磁道             
		request.getBody().put("TRK_MANUAL_FLAG", "");//手工/刷磁标志            
		request.getBody().put("REFUSE_FLAG", "");//拒绝标志                 
		request.getBody().put("CURRENT_FLAG", transNotes.getCurrentFlag());//定活标志                 
		request.getBody().put("TRACK_INFO2", "");//磁道信息2                
		request.getBody().put("TRACK_INFO3", "");//磁道信息3                
		request.getBody().put("IC_DEV_STATUS", "");//IC卡设备状态

		ReturnMessageNew response = coreClient.processCore("716040", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
		} else {
			response.setTxSuccess(false);
		}
		return response;
		//throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew PJH580311Handler(CoreTransNotes transNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		if(transNotes.getAmtPay() != null && BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(transNotes.getAmtPay())) < 2){
			request.getBody().put("PAY_AMT", BigDecimalUtils.getStringValue(new BigDecimal(transNotes.getAmtPay())));//交易金额
		}else{
			request.getBody().put("PAY_AMT",transNotes.getAmtPay());//支付金额       
		}
		request.getBody().put("KEEP_ARTICLES_NO", "");//代保管品编号             
		request.getBody().put("THIRD_SEQ_NO", transNotes.getDevSeqNo());//第三方流水号             
		request.getBody().put("GATHER_FLAG", "1");//收款标志                 
		request.getBody().put("IS_ELEC_BILL", transNotes.getIssWay());//是否电子票据             
		request.getBody().put("PAY_MODE", "1");//支付方式                 
		request.getBody().put("PROTEST_REASON", "");//拒付原因                 
		request.getBody().put("IS_ASSET_BILL", "0");//是否理财票据             
		request.getBody().put("PAYEE_ACCT_NO", transNotes.getAccNoPye());//收款人账号               
		request.getBody().put("ONLINE_SETTLE_FLAG", transNotes.getFlgWay());//线上清算标志             
		request.getBody().put("COLLECTION_NO", "");//托收编号                 
		request.getBody().put("GUARANTEE_NO", transNotes.getColNo());//担保编号                 
		request.getBody().put("BILL_NO", transNotes.getBilCode());//票据号码                 
		request.getBody().put("ACCEPTANCE_BANK_FLAG", transNotes.getIsTerm());//承兑行标志  
		request.getBody().put("OPERATION_TYPE", "1");//操作类型  
		request.getSysHead().put("BRANCH_ID", transNotes.getBrcNo());//报文头的机构号
		request.getSysHead().put("USER_ID", "p"+transNotes.getBrcNo());//柜员号
		
		
		/********************融合改造新增 start******************************/
		request.getBody().put("START_BILL_NO",transNotes.getBeginRangeNo());//票据开始子区间号
		request.getBody().put("END_BILL_NO",transNotes.getEndRangeNo());//票据结束子区间号
		request.getBody().put("IN_STORE_GUARANTEE_NO", transNotes.getInColNo());// 入库担保编号
		request.getBody().put("OUT_STORE_GUARANTEE_NO", transNotes.getOutColNo());// 出库担保编号
		
		/********************融合改造新增 end******************************/

		ReturnMessageNew response = coreClient.processCore("580311", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}

	@Override
	public ReturnMessageNew PJH584141Handler(CoreTransNotes transNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("REQ_ROWS", "10000");//请求笔数 
		request.getBody().put("START_DATE", transNotes.getDateStr());//开始日期 
		request.getBody().put("END_DATE", transNotes.getDateEnd());//终止日期 
		request.getBody().put("ACCT_NO", transNotes.getAccNo());//账号     
		request.getBody().put("ACCT_CODE", transNotes.getSubSeq());//款项代码 
		request.getBody().put("TRAN_AMT", transNotes.getAmt());//交易金额 
		request.getBody().put("QUERY_TYPE", transNotes.getIsSucced());//查询类型 
		request.getBody().put("SUB_SERIAL_NO","1");//款项序号 
		request.getBody().put("LIQU_MODE", "");//账务方向 
		request.getBody().put("TRAN_TIME", "");//交易时间 
		request.getBody().put("OPERATION_TYPE", "0");//操作类型 

		ReturnMessageNew response = coreClient.processCore("584141", request);
		String responseCode = response.getRet().getRET_CODE();
		logger.info("RET_CODE**********"+responseCode);
		logger.info("RET_MSG**********"+StringUtils.isNotBlank(response.getRet().getRET_MSG()));
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		if (responseCode.equals("ET1011")){
			response.setTxSuccess(false);
			return response;
		}
		throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew PJH126012Handler(CoreTransNotes transNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("CREDIT_ACCT_NO", transNotes.getAccNo());//贷款账号         
		request.getBody().put("QUERY_AUTH_FLAG", "");//查询权限标志     
		request.getBody().put("LOAN_INCARD_FLAG", "");//是否卡内自助贷款 
		request.getBody().put("REPAY_IMMED_FLAG", "");//是否随借随还标志

		ReturnMessageNew response = coreClient.processCore("126012", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		if (responseCode.trim().equals("LN1005")) {//针对销户情况的特殊处理，当借据结清时候销户
			response.setTxSuccess(true);
			Map map = new HashMap();
			map.put("LOAN_LEFT_AMT","0");
			map.put("RESTITUTE_PRINCIPAL_AMT","0");
			response.setBody(map);
			return response;
		}
		throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew doMarginWithdrawal(CoreTransNotes transNotes)
			throws Exception {
		
		logger.info("核心资金划转接口调起....");
		
		ReturnMessageNew request = new ReturnMessageNew();
		String remark = "";
		if(StringUtils.isNotBlank(transNotes.getRemark())){
			remark = transNotes.getRemark();
			if(remark.contains("null")){//这里处理网银有时候传过来的值是null字符串的情况
				remark = remark.replace("null", "");
			}
		}
		request.getBody().put("REMARK", remark);//备注
		if(StringUtils.isNotBlank(transNotes.getTranAmt())){
			if(BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(transNotes.getTranAmt())) < 2){
				request.getBody().put("TRAN_AMT", BigDecimalUtils.getStringValue(new BigDecimal(transNotes.getTranAmt())));//交易金额  
			}else{
				request.getBody().put("TRAN_AMT",transNotes.getTranAmt());//交易金额  
			}
		}
		request.getBody().put("ORI_CONSUMER_SVR_ID", "BPS");//原发起方服务器标识                        
		request.getBody().put("NAME_CHECK_FLAG", "0");//是否检查用户姓名                          
		request.getBody().put("AUTH_PASSWORD_FLAG", "0");//是否校验密码标记                          
		request.getBody().put("CCY", "01");//
		request.getBody().put("POW_FLAG", "0");//是否含权
		request.getBody().put("WITH_SEAL_FLAG", "0");//是否凭印鉴                                
		request.getBody().put("FEE_FLAG", "0");//收费标志                                  
		request.getBody().put("DR_ACCT_NO", transNotes.getDrAcctNo());//借方账号                                  
		request.getBody().put("CR_ACCT_SRC", "3");//贷方账户来源                              
		request.getBody().put("DR_ACCT_SRC", "3");//借方账户来源
		request.getBody().put("DR_SUB_NO", "1");//借方款项序号
		request.getBody().put("CR_SUB_NO", "1");//贷方款项序号
		request.getBody().put("CR_Z_MEMO", "");//贷方打折摘要                               
		request.getBody().put("CR_ACCT_NAME", transNotes.getCrAcctNoName());//贷方账户名称                              
		request.getBody().put("DR_ACCT_NAME", transNotes.getDrAcctNoName());//借方账户名称                              
		request.getBody().put("CR_ACCT_NO", transNotes.getCrAcctNo());//贷方账号                                  
		request.getBody().put("CR_MEMO", "转入");//贷方摘要                                  
		request.getBody().put("DR_MEMO", "转出");//借方摘要                                  
		request.getBody().put("CR_MEMO_CODE", "4017");//贷方摘要码                                
		request.getBody().put("DR_MEMO_CODE", "4018");//借方摘要码                                
		request.getBody().put("DR_Z_MEMO", "");//借方打折摘要                               
		request.getBody().put("THIRD_DATE", transNotes.getFrntDate());//第三方日期                                
		request.getBody().put("THIRD_SEQ_NO", transNotes.getSerSeqNo());//第三方流水号 

//		request.getSysHead().put("BRANCH_ID", transNotes.getBrcBld());//机构
//		request.getSysHead().put("USER_ID", transNotes.getUser());//柜员

		ReturnMessageNew response = coreClient.processCore("580062", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}else {
			response.setTxSuccess(false);
			return response;
		}
	}

	@Override
	public ReturnMessageNew doFeeScaleCoreAccount(CoreTransNotes transNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("FEE_FLAG", "0");//收费标志
		request.getBody().put("CCY_1", "01");//币种_1
		request.getBody().put("ACCT_NO_1", transNotes.getDeductionAcctNo());//账号_1
		request.getBody().put("FEE_RATE_1", "FEE00338");//费率_1
		request.getBody().put("FEE_REMARK", transNotes.getBpsNo()+"票据池票据管理服务费");//备注
		request.getBody().put("FEE_DR_MEMO_CODE", "4706");//公共收费贷方摘要码
//		request.getBody().put("FEE_DR_MEMO_CODE", transNotes.get);//公共收费借方摘要码
		request.getBody().put("FEE_SOURSE1", "3");//收费来源1         
		request.getBody().put("FEE_TIMES1", "1");//收费次数1   
		if(StringUtils.isNotBlank(transNotes.getAmt())){
			if(BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(transNotes.getAmt())) < 2){
				request.getBody().put("FEE_AMT_1", BigDecimalUtils.getStringValue(new BigDecimal(transNotes.getAmt())));//收费金额_1
			}else{
				request.getBody().put("FEE_AMT_1",transNotes.getAmt());//收费金额_1
			}
		}
		request.getBody().put("SPECIFY_BRANCH_NO", transNotes.getBrcNo());//指定机构号 客户号对应的融资签约机构
		request.getSysHead().put("BRANCH_ID", transNotes.getBrcBld());//机构
		request.getSysHead().put("USER_ID", transNotes.getUser());//柜员

		ReturnMessageNew response = coreClient.processCore("999002", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}else {
			response.setTxSuccess(false);
			return response;
		}
	}

	@Override
	public ReturnMessageNew txPledgeAccount(CoreTransNotes transNotes)
			throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getSysHead().put("USER_ID", transNotes.getUser());//用户    
		request.getSysHead().put("BRANCH_ID", transNotes.getBeatch());//机构   
		request.getAppHead().put("AUTH_FLAG", "");//授权标志
		request.getAppHead().put("AUTH_USER_ID_ARRAY.AUTH_BRANCH_ID", "");//授权柜员归属机构ID
		request.getAppHead().put("AUTH_USER_ID_ARRAY.AUTH_USER_ID", "");//授权柜员标识
		
		request.getBody().put("FEE_FLAG",transNotes.getFlgEnt());//收费标志
		request.getBody().put("CCY_1","01");//币种_1     
		request.getBody().put("ACCT_NO_1", transNotes.getDeductionAcctNo());//账号_1         
		request.getBody().put("ACCT_CODE1", transNotes.getAccSub());//款项代码
		request.getBody().put("FEE_RATE_1", transNotes.getFeeRateCode());//费率_1
		request.getBody().put("FEE_CR_MEMO_CODE","");//公共收费贷方摘要码
		request.getBody().put("FEE_DR_MEMO_CODE","4706");//公共收费借方摘要码
		request.getBody().put("FEE_SOURSE1","2");//收费来源1
		request.getBody().put("FEE_SOURSE2","2");//收费来源 2
		request.getBody().put("FEE_TIMES1","1");//收费时间1
		request.getBody().put("FEE_TIMES2","1");//收费时间2
		request.getBody().put("FEE_CANCLE_FLAG","0");//收费冲销标志
		request.getBody().put("FEE_AMT_1", transNotes.getFeeAmt());//收费金额_1
		request.getBody().put("CONSIGN_CODE","");//委托编号     
		request.getFileHead().put("FILE_PATH", transNotes.getPath());//文件路径 
		request.getFileHead().put("FILE_FLAG", transNotes.getRemark());//文件路上传标识
		request.getBody().put("INPUT_TOTAL_AMT", transNotes.getTotalAmt());//录入总金额    
		request.getBody().put("BATCH_MODE", "1");//批处理模式    
		request.getBody().put("AVAL_BALANCE","0.00");//可用余额
		request.getBody().put("INPUT_TOTAL_ROWS", transNotes.getTotalNum());//录入总笔数     
		request.getBody().put("BUSS_CATEGORY", transNotes.getiDTypeS());//业务种类
		request.getBody().put("THIRD_SEQ_NO", transNotes.getSerSeqNo());//第三方流水号
		request.getBody().put("THIRD_DATE", new Date());//第三方日期
		request.getBody().put("SPECIFY_BRANCH_NO", transNotes.getBrcNo());//指定机构号 客户号对应的签约机构
		request.getBody().put("FEE_REMARK",transNotes.getBpsNo()+"票据池票据管理服务费");//费率说明     
		request.getBody().put("ACCT_NO", transNotes.getAccNo());//账号：票据池系统结算账号
		
		request.getBody().put("CHECK_SEQ_NO", transNotes.getCheckSeqNo()       );//验印流水号  
		request.getBody().put("CHECK_ACCT_NO", transNotes.getCheckAcctNo()      );//验印账号    
		request.getBody().put("CHECK_AMT", transNotes.getCheckAmt()         );//验印金额    
		logger.info("看下签发日期为啥没有值："+transNotes.getCheckDrawDate());
		logger.info("看下验印交易日期为啥没有值："+transNotes.getCheckTranDate());
		
		
		request.getBody().put("CHECK_ISSUE_DATE", transNotes.getCheckDrawDate() != null ? DateUtils.formatDateToString(transNotes.getCheckDrawDate(), "yyyyMMdd") : ""    );//验印签发日期
		request.getBody().put("CHECK_CERT_NO", transNotes.getCheckCertNo()      );//验印凭证号  
		request.getBody().put("CHECK_CERT_CATEGORY", transNotes.getCheckCretCategory());//验印凭证种类
		request.getBody().put("CHECK_TRAN_DATE", transNotes.getCheckTranDate() != null ? DateUtils.formatDateToString(transNotes.getCheckTranDate(), "yyyyMMdd") : ""    );//验印交易日期 
		


		/*
		 * 
		 * 明细字段
		 * 
		 */
		request.setDetails(transNotes.getList());
		request.setCodeSign("1");//核心标识
		
		ReturnMessageNew response = coreClient.processCore("580280", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}else {
			response.setTxSuccess(false);
			return response;
		}
	}

	/**
	 * 核心贷款记账
	 */
	public ReturnMessageNew CORE001Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("LOAN_ACCT_NO",note.getCreditAcct());//扣款
		request.getBody().put("DEDUCTION_ACCT_NO",note.getDeduAcctNo());//放款账户
		request.getBody().put("LOAN_ACCT_CODE","");//放款账号款项代码
		request.getBody().put("DEDUCTION_ACCT_CODE","");//扣款账号款项代码
		request.getBody().put("CCY","01");//币种
		request.getBody().put("CASH_TRAN_FLAG","2");//现转标识
		request.getBody().put("START_DATE","");//开始日期
		request.getBody().put("END_DATE",DateUtils.toString(note.getDueDate(), "yyyyMMdd"));//结束日期
		request.getBody().put("EFFECTIVE_DATE","");//生效日期
		request.getBody().put("PLATFORM_TRAN_DATE","");//交易日期
		if(note.getRateFloatType().equals("1")){
			//时点浮动,上送给核心的值应该为2
			request.getBody().put("FLUCTUATION_MODE","2");//正常浮动方式
			//利率应该转换成月利率 = 浮动值/100(百分比)/12(月)
			BigDecimal rate = BigDecimalUtils.setScale(6,note.getRateFloatValue().divide(new BigDecimal(1.2) ,6, BigDecimal.ROUND_HALF_EVEN));
			request.getBody().put("INT_FLUCTUATION_RATE",rate);//正常浮动值
		}else if(note.getRateFloatType().equals("2")){
			//比例浮动,上送给核心的值应该为2
			request.getBody().put("FLUCTUATION_MODE","1");//正常浮动方式
			request.getBody().put("INT_FLUCTUATION_RATE",note.getRateFloatValue());//正常浮动值
		}else{
			request.getBody().put("FLUCTUATION_MODE",note.getRateFloatType());//正常浮动方式
			request.getBody().put("INT_FLUCTUATION_RATE",note.getRateFloatValue());//正常浮动值
		}
		if(note.getOverRateFloatType().equals("1")){
			//时点浮动,上送给核心的值应该为2
			request.getBody().put("OUT_INT_FLUCTUATION_MODE","2");//逾期利率浮动方式
			//利率应该转换成月利率 = 浮动值/100(百分比)/12(月)
			BigDecimal rate = BigDecimalUtils.setScale(6,note.getOverRateFloatValue().divide(new BigDecimal(1.2) ,6, BigDecimal.ROUND_HALF_EVEN));
			request.getBody().put("OUT_INT_FLUCTUATION_RATE",rate);//逾期利率浮动值
		}else if(note.getOverRateFloatType().equals("2")){
			//比例浮动,上送给核心的值应该为2
			request.getBody().put("OUT_INT_FLUCTUATION_MODE","1");//逾期利率浮动方式
			request.getBody().put("OUT_INT_FLUCTUATION_RATE",note.getOverRateFloatValue());//逾期利率浮动值
		}else{
			request.getBody().put("OUT_INT_FLUCTUATION_MODE",note.getOverRateFloatType());//逾期利率浮动方式
			request.getBody().put("OUT_INT_FLUCTUATION_RATE",note.getOverRateFloatValue());//逾期利率浮动值
		}
		request.getBody().put("RATE_ADJUST_WAY","1");//利率调整方式
		if(BigDecimalUtils.getNumberOfDecimalPlace(note.getLoanAmt()) < 2){
			request.getBody().put("CONTRACT_AMT",BigDecimalUtils.setScale(note.getLoanAmt()));//合同金额
		}else{
			request.getBody().put("CONTRACT_AMT",note.getLoanAmt());//合同金额
		}
		request.getBody().put("TRAN_AMT","");//交易金额
		request.getBody().put("REPAY_TYPE","33");//还款方式
		request.getBody().put("IOU_NO",note.getLoanNo());//借据号
		request.getBody().put("MGM_BRANCH_ID",note.getBranchNo());//管理机构
		request.getBody().put("PRODUCT_NO","1004001");//产品代码  不传(目前写死)
		request.getBody().put("REMARK","贷款发放");//备注
		request.getBody().put("COMMISSION","0.00");//手续费
		if(note.getPayMode().equals(PublicStaticDefineTab.PAY_2)){//受托支付送圈存金额
			if(note.getLoanAmt() != null){
				if(BigDecimalUtils.getNumberOfDecimalPlace(note.getLoanAmt()) < 2){
					request.getBody().put("ROPE_AMT",BigDecimalUtils.setScale(note.getLoanAmt()));//圈存金额
				}else{
					request.getBody().put("ROPE_AMT",note.getLoanAmt());//圈存金额
				}
			}
		}
		
		request.getBody().put("FEE_RATE_CODE","");//费率代码
		request.getBody().put("COMMISSION_PAY_ACCT_NO","");//手续费付款账号
		request.getBody().put("CLIENT_TYPE","2");//客户类型
		request.getBody().put("INT_ADJUST_TERM","");//利率调整周期
		request.getBody().put("INTEREST_SETTLE_PERIOD","");//结息周期
		request.getBody().put("LOAN_TYPE","0");//放款方式
		request.getBody().put("THIS_LOAN_ISSUE_FLAG","1");//是否本次发放
		request.getBody().put("IS_AUTO_REPAY","1");//是否自动扣划本息
		request.getBody().put("INER_INDEPENDENT_LOAN_FLAG","0");//是否卡内自主贷款
		request.getBody().put("FEE_STAMP_TAX_FLAG","0");//是否收取印花税
		request.getBody().put("INTE_FLAG","0");//是否贴息
		request.getBody().put("CONSIGN_LOAN_FLAG","0");//是否委托贷款
		request.getBody().put("MEMO_CODE","0105");//摘要码
		request.getBody().put("FEE_CANCLE_FLAG","");//收费冲销标识
		request.getBody().put("FEE_RATE_CODE1","");//公共费率代码1
		request.getBody().put("FEE_AMT1","");//公共收费金额1
		request.getBody().put("FEE_SOURSE1","");//公共收费来源 1
		request.getBody().put("FEE_PUBLIC_TIME1","");//公共收费时间1
		request.getBody().put("CLIENT_NO",note.getCustNo());//客户号
		request.getBody().put("FEE_CCY1","");//公共费币种1
		request.getBody().put("FEE_ACCT_NO1","");//公共收费账号1
		request.getBody().put("FEE_CCY2","");//公共费币种2
		request.getBody().put("FEE_AMT2","");//公共收费金额2
		request.getBody().put("FEE_ACCT_NO2","");//公共收费账号3
		request.getBody().put("FEE_PAYEE_ACCT_NO","");//代扣平台手续费收款账号
		request.getBody().put("RATE_TYPE","1");//利率类型
		request.getBody().put("INTEST_RATE_CODE","901");//利率计划代码  
		request.getBody().put("TERM","");//期限
		request.getBody().put("TERM_TYPE","");//期限类型
		request.getBody().put("BUYER_INTEREST_ACCT_NO","");//买方付息账号
		request.getBody().put("DEV_SEQ_NO",note.getDevSeqNo());//设备流水号
		request.getBody().put("BPS_DEPOSIT_ACCT_NO",note.getDepositAcctNo());//保证金账户 
		request.getBody().put("THIRD_SETTLE_DATE",note.getTranDate());//第三方清算日期
		request.getBody().put("CONTRACT_NO",note.getContractNo());//主业务合同号
		request.getSysHead().put("SOURCE_TYPE", "");//渠道
		request.getSysHead().put("BRANCH_ID", note.getBranchNo());//机构
		request.getSysHead().put("USER_ID", "c"+note.getBranchNo());//柜员
		
		

		ReturnMessageNew response = coreClient.processCore("CORE001", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		return response;
	}

	/**
	 * 核心贷款记账查询
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE002Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("CHANNEL_NO", "54");//渠道标识号
		if(note.getAcctDate() != null){
			request.getBody().put("START_DATE", DateUtils.toString(note.getAcctDate(), "yyyyMMdd"));//开始日期
		}
		request.getBody().put("THIRD_SEQ_NO", note.getSerSeqNo());//第三方流水号
		request.getBody().put("OPERATION_TYPE", "1");//操作类型
		ReturnMessageNew response = coreClient.processCore("CORE002", request);
		String responseCode = response.getRet().getRET_CODE();
		
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			if(null!= response.getBody().get("SUCC_FLAG")){
				String status = (String) response.getBody().get("SUCC_FLAG");//成功标志:核心只有两个码值 1 表示成功  0 表示失败，没有记录的时候返回也是0
				if("1".equals(status)){					
					response.setTxSuccess(true);
				}else{
					response.setTxSuccess(false);
				}
			}
			return response;
		}else{
			response.setTxSuccess(false);
			return response;
		}
	}

	/**
	 * 智汇宝支付
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyZHS001Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("TRANSFER_TYPE", "1");//转账类型 1 实时2普通 3次日；
		request.getBody().put("BOOK_TIME", null);//预约时间
		request.getBody().put("PAYEE_BANK_CODE", note.getDeduBankCode());//收款行行号
		request.getBody().put("CCY", "01");//币种
		if(note.getTranAmt() != null){
			if(BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(note.getTranAmt())) < 2){
				request.getBody().put("TRAN_AMT", BigDecimalUtils.getStringValue(new BigDecimal(note.getTranAmt())));//交易金额
			}else{
				request.getBody().put("TRAN_AMT",note.getTranAmt());//交易金额
			}
		}
		request.getBody().put("CASH_TRAN_FLAG", "2");//现转标志
		request.getBody().put("COMM_CHAR_FLAG", "0");//手续费收取标志
		request.getBody().put("COMMISSION", "");//手续费金额
		request.getBody().put("COMM_RELATE_TYPE", "");//手续费关联标志
		request.getBody().put("COMM_CASH_TRAN_FLAG", "");//手续费现转标志
		request.getBody().put("DOUBLE_AUTH_FLAG", "1");//疑似重账授权标志
		request.getBody().put("AMT_SPLIT_AUTH_FLAG", "");//金额拆分授权标志
		request.getBody().put("PAYER_ACCT_NO", note.getLoanAcctNo());//付款人账号
		request.getBody().put("PAYER_NAME", note.getLoanAcctName());//付款人名称
		request.getBody().put("PAYER_ADDR", "");//付款人地址
		request.getBody().put("PAYER_OPEN_BANK", note.getLoanBankNo());//付款人开户行行号
		request.getBody().put("PAYER_OPENBANK_NAME", note.getLoanBankName());//付款人开户行名称
		request.getBody().put("PAYER_ACCT_TYPE", "1");//付款人账户类型
		request.getBody().put("PAYER_ACCT_PASSWORD", "");//付款账号密码
		request.getBody().put("PAYER_ACCT_CITY_CODE", "");//付款账户城市代码
		request.getBody().put("PAYER_SETTLE_BANK", note.getLoanClearBank());//付款清算行行号
		request.getBody().put("PAYEE_ACCT_NO", note.getDeduAcctNo());//收款人账号
		request.getBody().put("PAYEE_NAME", note.getDeduAcctName());//收款人名称
		request.getBody().put("PAYEE_ADDR", "");//收款人地址
		request.getBody().put("PAYEE_OPEN_BRANCH", note.getDeduBankCode());//收款人开户行行号
		request.getBody().put("PAYEE_OPENBANK_NAME", note.getDeduBankName());//收款人开户行名称
		request.getBody().put("PAYEE_ACCT_TYPE", "");//收款人账户类型
		request.getBody().put("PAYEE_SETTLE_BANK", note.getDeduClearBank());//收款清算行行号
		request.getBody().put("REMARK1", note.getUsage());//备注1
		request.getBody().put("REMARK2", "");//备注2
		request.getBody().put("POSTSCRIPT", note.getPostscript());//附言
		request.getBody().put("POSTSCRIPT2", "");//附言2
		request.getBody().put("REVIEW_TELLER_NO", "");//复核柜员
//		request.getBody().put("TRANSFER_USAGE", "");//转账用途
		request.getBody().put("ACCT_MGM_BRANCH_ID", "");//账户管理机构
		request.getBody().put("FEE_FLAG", "0");//收费标志
		request.getBody().put("FEE_RATE_CODE1", "");//费率代码1
		request.getBody().put("FEE_CCY_1", "01");//公共收费币种1
		request.getBody().put("FEE_AMT1", "0");//收费金额1
		request.getBody().put("FEE_SOURSE1", "");//收费来源1
		request.getBody().put("FEE_ACCT_NO_1", "");//收费账号_1
		request.getBody().put("FEE_TIMES1", "");//收费次数1
		request.getBody().put("FEE_ACCT_CODE_1", "");//公共收费账号款项代码1
		request.getBody().put("FEE_RATE_CODE2", "");//费率代码2
		request.getBody().put("FEE_CCY_2", "");//公共收费币种2
		request.getBody().put("FEE_AMT2", "");//收费金额2
		request.getBody().put("FEE_SOURSE2", "2");//收费来源2
		request.getBody().put("FEE_ACCT_NO_2", "");//收费账号_2
		request.getBody().put("FEE_TIMES2", "");//收费次数2
		request.getBody().put("FEE_ACCT_CODE_2", "");//公共收费账号款项代码2
		request.getBody().put("ACCT_CLIENT_NO", "");//总户号或分户号
		request.getBody().put("KEY_INDEX", "");//密钥索引
		request.getBody().put("USAGE", note.getUsage());//用途
		request.getBody().put("OBG_TEXT1", "");//预留字段1
		request.getBody().put("OBG_TEXT2", "");//预留字段2
		request.getBody().put("OBG_TEXT3", "");//预留字段3
		request.getBody().put("OBG_TEXT4", "");//预留字段4
		request.getBody().put("BUSS_TYPE", "");//业务类型
		request.getBody().put("BUSS_CATEGORY", "");//业务种类
		request.getBody().put("CR_MEMO_CODE", "");//贷方摘要码
		request.getBody().put("DR_MEMO_CODE", "");//借方摘要码
		request.getBody().put("CR_MEMO", "");//贷方摘要
		request.getBody().put("DR_MEMO", "");//借方摘要
		request.getBody().put("FUNCTION_CODE", "");//功能码
		request.getBody().put("DRAFT_TYPE", "");//汇票类型
		
		request.getSysHead().put("BRANCH_ID", note.getBrcBld());//机构
		request.getSysHead().put("USER_ID", note.getUser());//柜员
		
		
		ReturnMessageNew response = coreClient.processCore("ZHS001", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		response.setTxSuccess(false);
		return response;
	}

	/**
	 * 智汇宝平台转账明细查询
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyZHS002Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("CHANNEL_DATE", "");//平台日期
		request.getBody().put("CHANNEL_SEQ_NO", "");//平台流水号
		ReturnMessageNew response = coreClient.processCore("ZHS002", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			
			//只有支付成功返回true，其他情况均返回false
			
			response.setTxSuccess(true);
			return response;
		}
		return response;
	}

	/**
	 * 解圈
	 */
	public ReturnMessageNew CORE008Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		if(note.getTranAmt() != null){
			if(BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(note.getTranAmt())) < 2){
				request.getBody().put("UNROPE_AMT", BigDecimalUtils.getStringValue(new BigDecimal(note.getTranAmt())));//解圈金额
			}else{
				request.getBody().put("UNROPE_AMT",note.getTranAmt());//解圈金额
			}
		}
		request.getBody().put("ROPE_NO", note.getFundNo());//圈存编号
//		request.getSysHead().put("BRANCH_ID", note.getBrcNo());//机构上送入账机构
//		request.getSysHead().put("USER_ID", "c"+note.getBrcNo());//柜员
		ReturnMessageNew response = coreClient.processCore("CORE008", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		return response;
	}

	/**
	 * 圈存
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE007Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		if(note.getTranAmt() != null){
			if(BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(note.getTranAmt())) < 2){
				request.getBody().put("ADD_ROPE_AMT", BigDecimalUtils.getStringValue(new BigDecimal(note.getTranAmt())));//追加圈存金额
			}else{
				request.getBody().put("ADD_ROPE_AMT",note.getTranAmt());//追加圈存金额
			}
		}
		request.getBody().put("ROPE_EXPIRY_DATE", "");//圈存到期日期
		request.getBody().put("FORCE_ROPE_FLAG", "0");//强制圈存标识
		request.getBody().put("ORI_ROPE_NO", note.getFundNo());//原圈存编号

//		request.getSysHead().put("BRANCH_ID", note.getBrcBld());//机构
//		request.getSysHead().put("USER_ID", "c"+note.getBrcBld());//柜员
		ReturnMessageNew response = coreClient.processCore("CORE007", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		return response;
	}

	/**
	 * 提前还款
	 * @throws Exception 
	 */
	public ReturnMessageNew CORE003Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("DEDUCTION_ACCT_NO", note.getDeduAcctNo());//扣款账号
		request.getBody().put("CREDIT_ACCT_NO", note.getCreditAcct());//贷款账号
		if(note.getTranAmt() != null){
			if(BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(note.getTranAmt())) < 2){
				request.getBody().put("REPAYMENT_AMT", BigDecimalUtils.getStringValue(new BigDecimal(note.getTranAmt())));//还款金额
			}else{
				request.getBody().put("REPAYMENT_AMT",note.getTranAmt());//还款金额
			}
		}
		request.getBody().put("CCY", "01");//币种
		request.getBody().put("REMARK", note.getLoanNo());//备注（借据号）
		request.getBody().put("CASH_TRAN_FLAG", "2");//现转标志
		request.getBody().put("ROPE_NO", note.getFundNo());//圈存编号
//		request.getSysHead().put("BRANCH_ID", note.getBranchNo());//机构
//		request.getSysHead().put("USER_ID", "c"+note.getBranchNo());//柜员
		ReturnMessageNew response = coreClient.processCore("CORE003", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		return response;
	}

	/**
	 * 贷款归还
	 * @throws Exception 
	 */
	public ReturnMessageNew Core009Handler(CoreTransNotes note) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("DEDUCTION_ACCT_NO", note.getDeduAcctNo());//扣款账号
		request.getBody().put("CREDIT_ACCT_NO", note.getCreditAcct());//贷款账号
		if(note.getTranAmt() != null){
			if(BigDecimalUtils.getNumberOfDecimalPlace(new BigDecimal(note.getTranAmt())) < 2){
				request.getBody().put("REPAYMENT_AMT", BigDecimalUtils.getStringValue(new BigDecimal(note.getTranAmt())));//还款金额
			}else{
				request.getBody().put("REPAYMENT_AMT",note.getTranAmt());//还款金额
			}
		}
		request.getBody().put("REMARK", note.getLoanNo());//备注（借据号）
		request.getBody().put("AmtAhead", "");//提前归还本金
		request.getBody().put("AmtIntFR", "");//实还复利
		request.getBody().put("AmtIntHR", "");//实还逾期罚息
		request.getBody().put("AmtIntP", "");//结清至当日利息
		request.getBody().put("AmtIntR", "");//实还应收利息
		request.getBody().put("AmtPrpS", "");//实还本金
		request.getBody().put("AmtOugD", "");//应还复利
		request.getBody().put("AmtOugE", "");//应还逾期罚息
		request.getBody().put("AmtOugI", "");//应还应收利息
		request.getBody().put("AmtOugP", "");//应还本金
		request.getBody().put("BrcMgm", "");//管理机构
		request.getBody().put("CustType", "");//客户类别
		request.getBody().put("FlgCT", "");//现在标识
		request.getBody().put("FlagPbk", "1");//还款标识
		request.getBody().put("AccNameC", "");//贷方户名
		request.getBody().put("AccNameD", "");//借方户名
		request.getBody().put("AccNoCt", "");//贷方账号
		request.getBody().put("AccNoD", "");//借款账号
		request.getBody().put("AccSrcC", "");//贷方账户来源
		request.getBody().put("AccSrcD", "");//借方账户来源
		request.getBody().put("AmtC", "");//贷方金额
		request.getBody().put("AmtD", "");//借方发生额
		request.getBody().put("CcyC", "");//贷方币种
		request.getBody().put("CcyD", "");//借方币种
		request.getBody().put("DateClr", "");//第三方清算日期
		request.getBody().put("DevSeqNo", "");//设备流水号
		request.getBody().put("FlgCRC", "");//贷方钞汇标志
		request.getBody().put("FlgCRD", "");//借方钞汇标志                               
		request.getBody().put("ROPE_NO", note.getFundNo());//圈存编号
		request.getBody().put("AUTO_REPAY_FLAG", "1");//是否自动分配还款金额
//		request.getSysHead().put("BRANCH_ID", note.getBranchNo());//机构
//		request.getSysHead().put("USER_ID", "c"+note.getBranchNo());//柜员
		ReturnMessageNew response = coreClient.processCore("CORE009", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		return response;
	}
	/**
	 * 保证金开户 20210526 gcj
	 * @throws Exception 
	 */
	public ReturnMessageNew core006Handler(CoreTransNotes transNotes) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("DEDUCTION_ACCT_NO",  transNotes.getDeductionAcctNo());//扣款账号
		request.getBody().put("TRAN_AMT", "0.00");//金额
		request.getBody().put("CCY", "01");//币种
		request.getBody().put("CANCEL_ACCT_NO", "");//销账编号
		request.getBody().put("CLIENT_NO", transNotes.getCustIdA());//客户号                     
		request.getBody().put("CLIENT_NAME", "汉口银行保证金（"+transNotes.getCustNam()+"）");//备注    
		request.getBody().put("EXPIRY_DATE", "");//到期日期
		request.getBody().put("INTE_START_DATE", DateUtils.toString(new Date(), "yyyyMMdd"));//起息日期
		request.getBody().put("REAL_INT_RATE", transNotes.getRate());//实际利率
		request.getBody().put("INT_RATE_FLOAT_TYPE", "");//利率浮动方式
		request.getBody().put("INTEREST_COUNT_MODE", "1");//入息方式
		request.getBody().put("INT_FLUCTUATION_RATE", "");//利率浮动值
		request.getBody().put("INT_RATE_LEVEL", "");//利率档次
		request.getBody().put("PRODUCT_NO", "2209022");//产品编号 2209022票据池保证金
		request.getBody().put("PRODUCT_GROUP_TYPE", "");//产品组别
		request.getBody().put("REMARK", "");//备注
		request.getSysHead().put("BRANCH_ID", transNotes.getBrcNo());//发送方机构ID
		request.getSysHead().put("USER_ID","c"+transNotes.getBrcNo());//服务请求者身份
		ReturnMessageNew response = coreClient.processCore("CORE006", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
		} else {
			response.setTxSuccess(false);
		}
		return response;
		//throw new Exception(responseCode);
	}
	/**
	 * 保证金开户变更 20210531 gcj
	 * @throws Exception 
	 */
	public ReturnMessageNew core010Handler(CoreTransNotes transNotes) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("OPERATION_TYPE", "");//操作类型
		request.getBody().put("BILL_NO", "");//票号
		request.getBody().put("BPS_DEPOSIT_ACCT_NO", transNotes.getDepositAcctNo());//票据池保证金账号
		request.getBody().put("NEW_BPS_DEPOSIT_ACCT_NO",transNotes.getDrAcctNo());//新票据池保证金账号
		request.getBody().put("SETTLE_ACCT_NO", "");//结算账号
		ReturnMessageNew response = coreClient.processCore("CORE010", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
		} else {
			response.setTxSuccess(false);
		}
		return response;
		//throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew core011Handler(CoreTransNotes transNotes)throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("CCY","01");//币种  
		request.getBody().put("START_DATE",  DateTimeUtil.get_YYYY_MM_DD_Date());//开始日期
		request.getBody().put("RATE_TYPE","DPS");//利率类型
		request.getBody().put("INTEST_RATE_CODE","03");//利率代码
		request.getBody().put("TERM","");//期限  
		request.getBody().put("TERM_TYPE","");//期限类型
		request.getBody().put("ACCT_NO","");//账号  
		request.getBody().put("CA_TT_FLAG","");//钞汇标志
		request.getBody().put("REQ_AMT","");//请求金额
		request.getBody().put("PRODUCT_NO","");//产品编号
		ReturnMessageNew response = coreClient.processCore("CORE011", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
		} else {
			response.setTxSuccess(false);
		}
		return response;

	}
}
