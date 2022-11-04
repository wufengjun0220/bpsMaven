package com.mingtech.application.pool.bank.bbsp.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.bbsp.client.EcdsClient;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 票据池交易 电票系统接口服务实现类
 *
 */
@Service("poolEcdsService") 
public class PoolEcdsServiceImpl implements PoolEcdsService {
	
	private static final Logger logger = Logger.getLogger(PoolEcdsServiceImpl.class);
	
	@Autowired
	EcdsClient ecdsClient;
	@Autowired
	PoolBatchNoUtils poolBatchNoUtils;
	
	public ReturnMessageNew txApplyImplawn(ECDSPoolTransNotes poolNotes) throws Exception {
		
		if (poolNotes == null) {
			logger.error("txApplyImplawn:票据质押传入参数错误！");
			throw new Exception("票据质押传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("E_SIGN", poolNotes.getSignature());//电子签名
		request.getBody().put("APPLYER_BATCH_REMARK", "");//申请人批次备注
		request.getBody().put("APPLYER_ACCT_NO", poolNotes.getApplicantAcctNo());//客户帐号
		request.getBody().put("APPLYER_ACCT_TYPE", "");//申请人账号类型
		request.getBody().put("APPLYER_OPEN_BANK_NO", poolNotes.getReceiverBankNo());//申请人账号开户行行号
		request.getBody().put("APPLYER_CLIENT_NO", "");//申请人客户编号
		request.getBody().put("CONTRACT_NO", "");//合同号
		request.getBody().put("INVOICE_NO", "");//发票号码
		request.getBody().put("BATCH_NO", "");//批次号
		
		request.setDetails(poolNotes.getDetails());
		
		ReturnMessageNew response = ecdsClient.processECDS("30600071", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 * 解质押申请输送报文字段(30610001)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	/*public ReturnMessageNew txApplyHypothecation(ECDSPoolTransNotes poolNotes) throws Exception {
		if (poolNotes == null) {
			logger.error("txApplyHypothecation:票据池解质押传入参数错误！");
			throw new Exception("票据池解质押传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_NO", poolNotes.getBillId());//票据号		必输
		request.getBody().put("beginRangeNo", poolNotes.getBeginRangeNo());//票据开始子区间号		必输
		request.getBody().put("endRangeNo", poolNotes.getEndRangeNo());//票据结束子区间号		必输
		request.getBody().put("UNPLEDGE_BRANCH_ID", poolNotes.getBranchNo());//解质押机构号		必输
		request.getBody().put("E_SIGN", poolNotes.getSignature());//电子签名		必输
		request.getBody().put("OBG_TEXT","");//预留字段
		
		*//***************************融合改造新增上送字段  start********************************//*
		
		request.getBody().put("beginRangeNo", poolNotes.getBeginRangeNo());//票据起始号
		request.getBody().put("endRangeNo", poolNotes.getEndRangeNo());//票据截至号
		request.getBody().put("OPERATION_TYPE", poolNotes.getBillNo());//票号
		request.getBody().put("OPERATION_TYPE", poolNotes.getTradeAmt());//交易金额
		request.getBody().put("OPERATION_TYPE", poolNotes.getCorpApplyTransCode());//交易编号
		request.getBody().put("OPERATION_TYPE", poolNotes.getHldrId());//持有id
		request.getBody().put("OPERATION_TYPE", poolNotes.getBillId());//电票id
		request.getBody().put("OPERATION_TYPE", poolNotes.getDataSource());//渠道来源  3-票据池
		request.getBody().put("OPERATION_TYPE", poolNotes.getBillOriginCode());//票据来源  3-票据池
		
		*//***************************融合改造新增上送字段  end********************************//*
		
		ReturnMessageNew response = ecdsClient.processECDS("30610001", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			
			return response;
		}
		throw new Exception(responseCode);
	}
	*/
	/**
	 * 提示付款申请报文输送字段（30610002）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyTSPayment(ECDSPoolTransNotes poolNotes) throws Exception {
		if (poolNotes == null) {
			logger.error("txApplyTSPayment:票据池提示付款传入参数错误！");
			throw new Exception("票据池提示付款传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("ACCT_NO", "");//账号           
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID          必输
		request.getBody().put("CB_E_SIGN", poolNotes.getSignature());//网银电子签名    必输
		request.getBody().put("PROMPT_DATE", poolNotes.getApplyDt());//提示付款日期    必输
		request.getBody().put("OUT_TIME_REASON", poolNotes.getOverdueReason());//逾期原因        必输
		request.getBody().put("REMARK", "");//备注            
		request.getBody().put("ONLINE_SETTLE_FLAG", poolNotes.getOnlineMark());//线上清算标志    必输
		request.getBody().put("PROMPT_BRANCH_NO", poolNotes.getBranchNo());//提示付款机构号  
		request.getBody().put("OBG_TEXT", "");//预留字段        
		request.getBody().put("INPOOL_FLAG", poolNotes.getIfInPool());//入池标志        
		request.getBody().put("OPERATION_TYPE", poolNotes.getTransNo());//操作类型        必输
		ReturnMessageNew response = ecdsClient.processECDS("30610002", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	
	/**
	 *查询票据正面信息报文输送字段（30600105）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBillFace(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyQueryBillFace:查询票据正面信息传入参数错误！");
			throw new Exception("票据正面信息传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID
		request.getBody().put("ACCT_NO", poolNotes.getAcctNo());//票据ID
		request.getBody().put("BILL_NO", poolNotes.getBillNo());//票据号
		request.getBody().put("START_BILL_NO", poolNotes.getBeginRangeNo());//票据号起
		request.getBody().put("END_BILL_NO", poolNotes.getEndRangeNo());//票据号止
		request.getBody().put("BILL_SOURCE", poolNotes.getBillSource());//票据来源
		request.getBody().put("SOURCE_CHANNEL_NO", poolNotes.getDataSource());//渠道来源
		
		request.getAppHead().put("PAGE_NO", poolNotes.getCurrentPage());//
		request.getAppHead().put("PER_PAGE_NUM", poolNotes.getPageSize());//

		
		ReturnMessageNew response = ecdsClient.processECDS("30600105", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 *查询票据背面信息报文输送字段（30600096）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBillCon(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyQueryBillCon:查询票据背面信息传入参数错误！");
			throw new Exception("票据背面信息传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("BILL_NO", poolNotes.getBillNo());//票号	
		request.getBody().put("BILL_SOURCE", poolNotes.getDataSource());//票据来源	必输
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据id 	必输
		request.getBody().put("ACCT_NO", poolNotes.getAcctNo());//申请人账号 	必输
		
		ReturnMessageNew response = ecdsClient.processECDS("30600096", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 *电子票据交易类查询报文输送字段（30600106）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBusiness(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;   // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyQueryBusiness电子票据传入参数错误！");
			throw new Exception("电子票据查询传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("billId", poolNotes.getBillId());//票据ID
		request.getBody().put("applicantAcctNo", poolNotes.getApplicantAcctNo());//申请人账号		必输
		request.getBody().put("billNo", poolNotes.getBillNo());//票号
		request.getBody().put("status", poolNotes.getStatus());//交易状态
		request.getBody().put("startTransDt", "");//交易开始日期
		request.getBody().put("endTransDt", "");//交易结束日期
		request.getBody().put("minStartTransDt", "");//最小交易开始日期
		request.getBody().put("maxStartTransDt", "");//最大交易开始日期
		request.getBody().put("minEndTransDt", "");//最小交易结束日期
		
		request.getBody().put("maxEndTransDt", "");//最大交易结束日期
		request.getBody().put("transType", "");//业务分类
		request.getBody().put("signFlag", "");//签收标记
		request.getBody().put("transNo", poolNotes.getTransNo());//交易类型
		request.getBody().put("XTransNo", poolNotes.getXTransNo());//伪交易编号
		request.getBody().put("statusCode", "");//交易结果
		request.getBody().put("transId", "");//交易ID
		request.getBody().put("reserve1", "");//预留字段
		request.getBody().put("pageSize", poolNotes.getPageSize());//每页显示的行数		必输
		request.getBody().put("currentPage", poolNotes.getCurrentPage());//当前页号		必输
		request.getBody().put("maxAcptDt", "");//最大出票日
		request.getBody().put("minAcptDt", "");//最小出票日
		
		ReturnMessageNew response = ecdsClient.processECDS("30600106", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 *电子票据交易类查询报文输送字段（30610006）(批量文件)
	 *接口可用于质押解质押签收结果查询等
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBusinessBatch(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyQueryBusinessBatch:电子票据查询传入参数错误！");
			throw new Exception("电子票据查询传入参数错误！");
		}
		
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("E_SIGN","");// 电子签名
		request.getBody().put("CLIENT_NO","");// 客户号 
		request.getBody().put("ACCT_NO", poolNotes.getAcctNo());// 客户账号                          必输
		request.getBody().put("BILL_NO",poolNotes.getBillNo());// 票据（包）号码
		request.getBody().put("SOURCE_CHANNEL_NO","3");// 渠道来源                          必输
		request.getBody().put("BILL_SOURCE", poolNotes.getBillSource());// 票据来源                          必输
		request.getBody().put("BILL_ID", poolNotes.getBillId());// 票据ID 
		request.getBody().put("MAX_START_BILL_NO", poolNotes.getBeginRangeNo());//票据起始号
		request.getBody().put("MIN_START_BILL_NO", poolNotes.getBeginRangeNo());//票据起始号
		request.getBody().put("MAX_END_BILL_NO", poolNotes.getEndRangeNo());//票据截至号
		request.getBody().put("MIN_END_BILL_NO", poolNotes.getEndRangeNo());//票据截至号   
		request.getBody().put("TRAN_START_DATE","");// 交易开始日期              
		request.getBody().put("TRAN_END_DATE","");// 交易结束日期              
		request.getBody().put("OBG_TEXT","");// 预留字段                  
		request.getBody().put("MIN_TRAN_START_DATE","");// 最小交易开始日期          
		request.getBody().put("MAX_TRAN_START_DATE","");// 最大交易开始日期          
		request.getBody().put("MIN_TRAN_END_DATE","");// 最小交易结束日期          
		request.getBody().put("MAX_TRAN_END_DATE","");// 最大交易结束日期          
		request.getBody().put("MAX_TRAN_APPLY_DATE","");// 最大交易申请日期          
		request.getBody().put("MIN_TRAN_APPLY_DATE","");// 最小交易申请日期          
		request.getBody().put("MAX_TRAN_RCV_DATE","");// 最大交易签收日期          
		request.getBody().put("MIN_TRAN_RCV_DATE","");// 最小交易签收日期          
		request.getBody().put("BUSS_TYPE", poolNotes.getTransType());// 业务分类                  
		request.getBody().put("BILL_TYPE","");// 票据类型                  
		request.getBody().put("BILL_MEDIUM","");// 票据介质                  
		request.getBody().put("TRAN_TYPE_LIST",poolNotes.getTransNo());// 交易类型集合              
		request.getBody().put("TRAN_RESULT_LIST","");// 交易结果集合              
		request.getBody().put("TRAN_STATUS_LIST","");// 交易状态集合（配合处理失败使用） 
		request.getBody().put("MAX_BILL_AMT","");// 最大票据金额              
		request.getBody().put("MIN_BILL_AMT","");// 最小票据金额              
		request.getBody().put("OPPONENT_TRAN_NAME","");// 交易对手名称              
		request.getBody().put("RCV_FLAG","");// 签收标志                  
		request.getBody().put("QUERY_FLAG","0");// 查询标识                  

		ReturnMessageNew response = ecdsClient.processECDS("30610006", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	/**
	 *电子票据交易类查询报文输送字段（03030337）(批量文件)
	 *接口可用于质押解质押签收结果查询等
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyQueryBusinessBatch2(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyQueryBusinessBatch:电子票据查询传入参数错误！");
			throw new Exception("电子票据查询传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_ID",poolNotes.getBillId());//票据ID          
		request.getBody().put("APPLYER_ACCT_NO",poolNotes.getAcctNo());//申请人账号      
		request.getBody().put("TICKET_NO","");//票号            
		request.getBody().put("TRAN_START_DATE","");//交易开始日期    
		request.getBody().put("TRAN_END_DATE","");//交易结束日期    
		request.getBody().put("MIN_TRAN_START_DATE","");//交易最小开始日期
		request.getBody().put("MAX_TRAN_START_DATE","");//交易最大开始日期
		request.getBody().put("MIN_TRAN_END_DATE","");//交易最小结束日期
		request.getBody().put("MAX_TRAN_END_DATE","");//交易最大结束日期
		request.getBody().put("BUSS_TYPE",poolNotes.getTransType());//业务类型        
		request.getBody().put("SIGN_FLAG","");//签收标志        
		request.getBody().put("TRANS_CODE",poolNotes.getStatusCode());//交易代码        
		request.getBody().put("RESULT_CODE","");//交易结果代码    
		request.getBody().put("TRAN_CODE","");//交易码          
		request.getBody().put("OBG_TEXT","");//预留字段 
		
		ReturnMessageNew response = ecdsClient.processECDS("03030337", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 *查询质押待签收的票据报文输送字段（30610003）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyImplawnForSign(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyImplawnForSign:票据池质押待签收传入参数错误！");
			throw new Exception("票据池质押待签收传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		/*request.getBody().put("INPOOL_FLAG", poolNotes.getIfInPool());//是否入池
		request.getBody().put("BRANCH_NO",poolNotes.getBranchNo());//查询机构号		
		request.getBody().put("APPLYER_ACCT_NO",poolNotes.getApplicantAcctNo());//账号
		request.getBody().put("DRAFT_TYPE","");//汇票类型
		request.getBody().put("ACCEPTOR_ACCT_NAME","");//承兑人名称
		request.getBody().put("BILL_NO",poolNotes.getBillNo());//票据号码
		request.getBody().put("MIN_START_BILL_NO",poolNotes.getBeginRangeNo());//票据开始子区间号
		request.getBody().put("MAX_START_BILL_NO",poolNotes.getBeginRangeNo());//票据开始子区间号
		request.getBody().put("MIN_END_BILL_NO",poolNotes.getEndRangeNo());//票据结束子区间号
		request.getBody().put("MAX_END_BILL_NO",poolNotes.getEndRangeNo());//票据结束子区间号
		request.getBody().put("MAX_ACCEPTOR_DATE","");//出票日期上限
		request.getBody().put("MIN_ACCEPTOR_DATE","");//出票日期下限
		request.getBody().put("MAX_DUE_DATE","");//汇票到期日期上限
		request.getBody().put("MIN_DUE_DATE","");//汇票到期日期下限
		request.getBody().put("MAX_AMT","");//票据最高金额
		request.getBody().put("MIN_AMT","");//票据最低金额
		request.getBody().put("QUERY_TYPE",poolNotes.getPartnerType());//查询类型
		request.getBody().put("TRAN_NO_LIST",poolNotes.getTransNo());//交易编号集合
*/
		request.setDetails(poolNotes.getDetails());
		ReturnMessageNew response = ecdsClient.processECDS("30610003", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 *查询待签收的票据报文输送字段（30600104）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	/*public ReturnMessageNew txApplyForSign(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyForSign:票据池查询待签收的票据传入参数错误！");
			throw new Exception("票据池查询待签收的票据传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("APPLYER_ACCT_NO", poolNotes.getAcceptorAcctNo());//申请人账号	必输
		request.getBody().put("TICKET_NO",poolNotes.getBillNo());//票号
		request.getBody().put("TRAN_TYPE",poolNotes.getTransType());//交易类型
		request.getBody().put("MAX_ACCEPTOR_DATE","");//出票日期上限
		request.getBody().put("MIN_ACCEPTOR_DATE","");//出票日期下限
		request.getBody().put("MAX_DUE_DATE","");//汇票到期日上限
		request.getBody().put("MIN_DUE_DATE","");//汇票到期日下限
		request.getBody().put("MAX_BILL_AMT","");//票据金额上限
		request.getBody().put("MIN_BILL_AMT","");//票据金额下限
		request.getBody().put("OBG_TEXT","");//预留字段
		
		
		*//***************************融合改造新增上送字段  start********************************//*
		request.getBody().put("beginRangeNo", poolNotes.getBeginRangeNo());//票据起始号
		request.getBody().put("endRangeNo", poolNotes.getEndRangeNo());//票据截至号
		request.getBody().put("OPERATION_TYPE", poolNotes.getOperationType());//撤销申请查询
		
		*//***************************融合改造新增上送字段  end********************************//*
		
		ReturnMessageNew response = ecdsClient.processECDS("30600104", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	*/
	/**
	 *查询持有票据信息报文输送字段（30610004）
	 *
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyPossessBill(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyPossessBill:票据池查询持有票据传入参数错误！");
			throw new Exception("票据池查询持有票据传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("ACCT_NO", poolNotes.getApplicantAcctNo());//申请人账号		必输
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID
		request.getBody().put("BILL_NO", poolNotes.getBillNo());//票号
		request.getBody().put("MAX_START_BILL_NO", poolNotes.getMaxBeginRangeNo());//最大子票区间起始
		request.getBody().put("MIN_START_BILL_NO", poolNotes.getMinBeginRangeNo());//最小子票区间起始
		request.getBody().put("MAX_END_BILL_NO", poolNotes.getMaxEndRangeNo());//最大子票区间截至
		request.getBody().put("MIN_END_BILL_NO", poolNotes.getMinEndRangeNo());//最小子票区间截至
		request.getBody().put("MAX_BILL_AMT", poolNotes.getMaxBillMoney());//金额上限
		request.getBody().put("MIN_BILL_AMT",poolNotes.getMinBillMoney());//金额下限
		request.getBody().put("MAX_DRAW_DATE", poolNotes.getMaxAcptDt());//出票日上限
		request.getBody().put("MIN_DRAW_DATE", poolNotes.getMinAcptDt());//出票日下限
		request.getBody().put("MAX_REMIT_EXPIRY_DATE", poolNotes.getMaxDueDt());//到期日上限
		request.getBody().put("MIN_REMIT_EXPIRY_DATE", poolNotes.getMinDueDt());//到期日下限
		request.getBody().put("BILL_TYPE", poolNotes.getBillType());//票据类型
		request.getBody().put("DRAWER_ACCT_NO", poolNotes.getRemitterAcctNo());//出票人账号
		request.getBody().put("PAYEE_ACCT_NO", poolNotes.getPayeeAcctNo());//收款人账号
		request.getBody().put("BUSS_SPONSOR_ACCT_NO", poolNotes.getAcctNo());//业务发起人账号
		request.getBody().put("ACCEPTOR_OPENBANK_NAME", poolNotes.getAcceptorBankName());//承兑人开户行名称
		request.getBody().put("SENDER_TYPE","");//发起方标志
		request.getBody().put("BUSS_LOCK_FLAG","");//经办锁标识  0 查所有
		request.getBody().put("TRAN_NO",PoolComm.CBS_0022007);//交易编号
		request.getBody().put("QUERY_FLAG","1");//查询标识
		request.getBody().put("BILL_SOURCE","CS04");//票据来源
		request.getBody().put("SOURCE_CHANNEL_NO","");//渠道来源
		request.getBody().put("BILL_POWER_TYPE","PRT01");//权责类型  PRT01：正常持有，和查询类型为HQT00相同; PRT02：未付票; PRT03：质入未结清，和查询类型为HQT01相同
		request.getBody().put("QUERY_TYPE","HQT00");//查询类型  HQT00：持有查询
		request.getAppHead().put("PAGE_NO",poolNotes.getCurrentPage());//页号
		request.getAppHead().put("PER_PAGE_NUM",poolNotes.getPageSize());//每页返回
		
		ReturnMessageNew response = ecdsClient.processECDS("30610004", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 *查询全量票据信息报文输送字段（30610005）(批量文件)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyFullBill(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;   // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyFullBill:票据池查询全量票据传入参数错误！");
			throw new Exception("票据池查询全量票据传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.setDetails(poolNotes.getDetails());
		
		request.getBody().put("APPLYER_ACCT_NO", poolNotes.getAcctNo());//账号
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID
		request.getBody().put("BILL_SOURCE", poolNotes.getBillSource());//票据来源
		request.getBody().put("TICKET_NO", poolNotes.getBillNo());//票号
		request.getBody().put("MAX_BILL_AMT", poolNotes.getMaxBillMoney());//金额上限
		request.getBody().put("MIN_BILL_AMT",poolNotes.getMinBillMoney());//金额下限
		request.getBody().put("MAX_ACCEPTOR_DATE", poolNotes.getMaxAcptDt());//出票日上限
		request.getBody().put("MIN_ACCEPTOR_DATE", poolNotes.getMinAcptDt());//出票日下限
		request.getBody().put("MAX_DUE_DATE", poolNotes.getMaxDueDt());//到期日上限
		request.getBody().put("MIN_DUE_DATE", poolNotes.getMinDueDt());//到期日下限
		request.getBody().put("BILL_TYPE", poolNotes.getBillType());//票据类型
		request.getBody().put("BILL_ACCT_NO", poolNotes.getRemitterAcctNo());//出票人账号
		request.getBody().put("PAYEE_ACCT_NO", poolNotes.getPayeeAcctNo());//收款人账号
		request.getBody().put("BUSS_SPONSOR_ACCT_NO", poolNotes.getAcctNo());//业务发起人账号
		request.getBody().put("ACCEPTOR_OPENBANK_NAME", poolNotes.getAcceptorBankName());//承兑人开户行名称
		request.getBody().put("SENDER_TYPE",poolNotes.getStatus());//发起方标志
		request.getBody().put("OBG_TEXT","");//预留字段
		
		
		ReturnMessageNew response = ecdsClient.processECDS("30610005", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	
	/**
	 *统一撤销申请报文输送字段（30600122）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public boolean txApplyRevokeApply(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			return true;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyRevokeApply:票据池撤销传入参数错误！");
			throw new Exception("票据池撤销传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("APPLYER_ACCT_NO", poolNotes.getAcctNo());//客户账号		必输
		request.getBody().put("E_SIGN", poolNotes.getSignature());//网银电子签名		必输
		request.getBody().put("SOURCE_CHANNEL_NO","3");//发起来源
		request.getBody().put("TRAN_NO",poolNotes.getTransNo());//交易编号
		request.getBody().put("SENDER_TYPE","2");//发起方类型
		
		request.setDetails(poolNotes.getDetails());
		
		ReturnMessageNew response = ecdsClient.processECDS("30600122", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	/**
	 * 老票统一撤销申请报文输送字段（03010810）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public boolean txApplyRevokeApplyOld(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			return true;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyRevokeApply:票据池撤销传入参数错误！");
			throw new Exception("票据池撤销传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("CLIENT_ACCT_NO", poolNotes.getApplicantAcctNo());//客户账号		必输
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID		必输
		request.getBody().put("CB_E_SIGN", poolNotes.getSignature());//网银电子签名		必输
		request.getBody().put("TRAN_SRC_TYPE","");//发起来源
		request.getBody().put("OBG_TEXT","");//预留字段
		request.getBody().put("SENDER_TYPE","2");//发起方类型
		
		ReturnMessageNew response = ecdsClient.processECDS("03010810", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(response.getRet().getRET_MSG());
	}
	
	/**
	 *统一签收、拒绝报文输送字段（30600123）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplySignReject(ECDSPoolTransNotes poolNotes) throws Exception {
		if (poolNotes == null) {
			logger.error("txApplySignReject:票据池签收、拒绝传入参数错误！");
			throw new Exception("票据池签收、拒绝传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("APPLYER_ACCT_NO", poolNotes.getAcceptorAcctNo());//客户账号		必输
		request.getBody().put("E_SIGN", poolNotes.getSignature());//网银电子签名		必输
		request.getBody().put("APPLYER_OPEN_BANK_NO", poolNotes.getReceiverBankNo());//质权人开户行行号		必输
		
		
		request.setDetails(poolNotes.getDetails());
		

		ReturnMessageNew response = ecdsClient.processECDS("30600123", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
//		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
//		}else{
//			throw new Exception(response.getRet().getRET_MSG());
//		}
	}
	
	/**
	 * 提示承兑签收
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public boolean txApplyAcptSign(ECDSPoolTransNotes note) throws Exception {
		if (note == null) {
			logger.error("txApplyAcptSign:票据池签收、拒绝传入参数错误！");
			throw new Exception("票据提示承兑签收传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		
		
		request.getBody().put("ACCEPTANCE_AGREE_NO", note.getAcptProtocolNo());//承兑协议编号        
		request.getBody().put("SOURCE_CHANNEL_NO", "3");//渠道来源            
		request.getBody().put("BILL_MEDIUM", "ME02");//票据介质            
		request.getBody().put("ISSUE_TYPE", "1");//签发类型            
		request.getBody().put("BUSS_TYPE", "01010121");//业务类型            
		request.getBody().put("BUSS_SEND_BRANCH_NO", note.getInAcctBranch());//业务发起机构    (出票人开户行行号)    
		request.getBody().put("TRAN_BRANCH_NO", note.getInAcctBranch());//交易机构            
		request.getBody().put("ACCOUNT_OPERATION_BRANCH_NO", note.getInAcctBranch());//账务操作机构        
		request.getBody().put("ACCOUNT_BRANCH_NO", note.getBranchNo());//账务机构            
		request.getBody().put("ISSUE_BRANCH_NO", note.getRemitterBatch());//签发机构            承兑签发机构(承兑人所在机构)
		request.getBody().put("CANCEL_PAY_BRANCH_NO", note.getInAcctBranch());//解付机构号          
		request.getBody().put("AGENT_BRANCH_NO", "");//代理机构行号        
		request.getBody().put("AGENT_BRANCH_NAME", "");//代理机构行名        
		request.getBody().put("CUST_MANAGER_ID", note.getCustNo());//客户经理号          
		request.getBody().put("CUST_MANAGER_NAME", note.getCustName());//客户经理名称        
		request.getBody().put("CUST_MANAGER_BRANCH_NO", "");//客户经理所属机构    
		request.getBody().put("DRAWER_CLIENT_NO", note.getCllentNo());//出票人客户号        
		request.getBody().put("DRAWER_ACCT_NO", note.getRemitterAcctNo());//出票人账号          
		request.getBody().put("DRAWER_NAME", note.getRemitter());//出票人名称          
		request.getBody().put("PAYER_BANK_NO", note.getAcceptorBankNo());//付款行行号          
		request.getBody().put("PAYER_BANK_NAME", note.getAcceptorBankName());//付款行行名          
		request.getBody().put("PAYER_ADDR", "");//付款人地址          
		request.getBody().put("DRAW_BILL_DATE", note.getAcptDt());//出票日期            
		request.getBody().put("REMIT_EXPIRY_DATE", note.getDueDt());//汇票到期日          
		request.getBody().put("EXPEND_SEQ_NO", note.getIouNo());//出账编号            
		request.getBody().put("EXPEND_BATCH_SEQ_NO", note.getIouNo());//出账批次流水号      
		request.getBody().put("GUARANTEE_MODE", "4");//担保方式            
		request.getBody().put("GUARANTEE_NO", note.getPoolDepositAcctNo());//担保编号            
		request.getBody().put("GUARANTEE_SER_NO", "");//担保序号            
		request.getBody().put("GUARANTEE_INT_RATE", "");//担保利率            
		request.getBody().put("GUARANTEE_INT_RATE_TYPE", note.getRateType() == null ? "001" : note.getRateType());//担保利率类型        
		request.getBody().put("DEPOSIT_RATE", note.getDepositRate());//缴存比例            
		request.getBody().put("SETTLE_ACCT_NO", note.getAcctNo());//结算账号            
		request.getBody().put("SETTLE_SUB_ACCT_NO", "");//结算账户子序号      
		request.getBody().put("LOAN_CONTRACT_NO", "");//贷款合同号          
		request.getBody().put("SYNC_ACCT_FLAG", "");//是否同步账号        
		request.getBody().put("OBG_TEXT1", "");//保留字段1           
		request.getBody().put("OBG_TEXT2", "");//保留字段2           
		request.getBody().put("OBG_TEXT3", "");//保留字段3           
		request.getBody().put("DEPOSIT_ACCT_INFO", "");//扩展字段账号数据JSON
		request.getBody().put("RISK_LEVEL", "");//风险等级            
		request.getBody().put("OUT_EXT_INFO", "");//扩展字段(对外)      
		
		request.setDetails(note.getDetails());
		/*request.getBody().put("IOU_NO", note.getIouNo());//借据号
		request.getBody().put("ACCEPTANCE_AGREE_NO", note.getContractNo());//承兑协议编号--经过与MIS系统确认，这里应该传主业务合同号
		request.getBody().put("DEDU_DEPOSIT_ACCT_NO", note.getDeduDepositAcctNo());//保证金扣款账户
		request.getBody().put("SETTLE_ACCT_NO", note.getDeduDepositAcctNo());//结算账号
		request.getBody().put("BILL_NO", note.getBillNo());//票据号码
		request.getBody().put("BILL_ID", note.getBillId());//票据ID
		request.getBody().put("ACCE_DATE", note.getAcptDt());//承兑日期
//		request.getBody().put("DEPOSIT_ACCT_NO", note.getDepositAcctNo());//保证金账户
		request.getBody().put("EXPEND_ACCT_BRANCH_ID", note.getInAcctBranch());//出账机构
		request.getBody().put("DEPOSIT_RATE", BigDecimalUtils.setScale(6, note.getDepositRate()).toPlainString());//保证金比例
		request.getBody().put("RATE_TYPE", note.getRateType());//利率类型
		
		request.getBody().put("INT_FLUCTUATION_MODE", note.getIntFluctuationMode());//利率浮动方式
		if(note.getFloatIntRate() != null){
			logger.info("浮动利率值-------------------:"+note.getFloatIntRate().toPlainString());
//			if(StringUtil.isNotEmpty(note.getIntFluctuationMode()) && note.getIntFluctuationMode().equals("2")){//比例浮动
				request.getBody().put("FLOAT_INT_RATE", BigDecimalUtils.setScale(6, note.getFloatIntRate()).toPlainString());//浮动利率值
//			}else{
//				request.getBody().put("FLOAT_INT_RATE", note.getFloatIntRate().toPlainString());//浮动利率值
//			}
		}
		
		request.getBody().put("FEE_FLAG", note.getFeeFlag());//收费标志
		request.getBody().put("FEE_MODE", note.getFeeMode());//收费方式
		request.getBody().put("FEE_ACCT_NO", note.getFeeAcctNO());//收费账号
		request.getBody().put("AUDIT_STATUS", note.getAuditStatus());//审核状态
		request.getBody().put("FEE_RATE", note.getFEE_RATE().multiply(new BigDecimal("10")));//手续费率(票据池系统存的百分制，传给电票需要千分制)
		request.getBody().put("BPS_DEPOSIT_ACCT_NO", note.getPoolDepositAcctNo());//票据池保证金账号
		request.getBody().put("BILL_AMT", new  BigDecimal(note.getBillMoney()));//票据金额
		request.getBody().put("BILL_ACCT_NO", note.getRemitterAcctNo());//出票人账号
		request.getBody().put("BILL_NAME", note.getRemitter());//出票人名称
		request.getBody().put("REMITTER_OPENBANK_NAME", note.getRemitterBankName());//出票人开户行名称
		request.getBody().put("ACCEPTANCE_BANK_ID", note.getAcceptorBankNo());//承兑行行号
		request.getBody().put("PAYEE_NAME", note.getPayee());//收款人名称
		request.getBody().put("PAYEE_ACCT_NO", note.getPayeeAcctNo());//收款人账号
		request.getBody().put("PAYEE_OPENBANK_NAME", note.getPayeeBankName());//收款人开户行名称
		request.getBody().put("DRAW_DATE", note.getAcptDt());//出票日期
		request.getBody().put("EXPIRY_DATE", note.getDueDt());//到期日期
		request.getBody().put("SENDER_TYPE", "3");//到期日期
*/		
		ReturnMessageNew response = ecdsClient.processECDS("BBSP005", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}else{
			return false;
		}
	}
	

	/**
	 * 贴现申请报文输送字段(30020801)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
//	@Override
	/*public boolean txApplyDiscount(ECDSPoolTransNotes poolNotes)
			throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			return true;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplySignReject:票据池贴现申请传入参数错误！");
			throw new Exception("票据池贴现申请入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("ACCT_NO", poolNotes.getAcceptorAcctNo());//客户账号		必输
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID		必输
		request.getBody().put("CB_E_SIGN", poolNotes.getSignature());//网银电子签名		必输
		request.getBody().put("OPERATION_TYPE", "1");//操作类型  贴现申请
		
		request.getBody().put("DISCOUNT_MODE", poolNotes.getPayIntMode());//贴现方式    
		request.getBody().put("DISCOUNT_INT_RATE", poolNotes.getDiscountIntRate());//贴现利率    
		request.getBody().put("DISCOUNT_DATE", poolNotes.getDiscountDate());//贴现日期    
		request.getBody().put("REDEEM_OPEN_DATE", poolNotes.getRedeemOpemDate());//赎回开放日期
		request.getBody().put("REDEEM_END_DATE", poolNotes.getRedeenEndDate());//赎回截止日期
		request.getBody().put("REDEEM_INT_RATE", poolNotes.getRedeemIntRate());//赎回利率    
		request.getBody().put("REDEEM_AMT", poolNotes.getRedeemAmt());//赎回金额    
		request.getBody().put("DISCOUNT_IN_BANK_CODE", poolNotes.getDiscountInBankCode());//贴入行行号  
		request.getBody().put("DISCOUNT_IN_BANK_NAME", poolNotes.getDiscountInBankName());//贴入行名称  
		request.getBody().put("DISCOUNT_IN_ACCT_NO", poolNotes.getDiscountInAcctNo());//贴入账号    
		request.getBody().put("ENTER_ACCT_NO", poolNotes.getEnterAcctNo());//入账账号    
		request.getBody().put("ENTER_BANK_CODE", poolNotes.getEnterBankCode());//入账行号    
		request.getBody().put("BATCH_NO", poolNotes.getBatchNo());//批次号      
		request.getBody().put("ENDORSE_TRANS_FLAG", poolNotes.getForbidFlag());//背书转让标志
		request.getBody().put("LOCK_FLAG", poolNotes.getIsLock());//是否锁票      
		request.getBody().put("REAL_PAY_AMT", poolNotes.getBillMoney());//实付金额
		request.getBody().put("SENDER_TYPE", "3");//发起方来源 1：网银端2 MIS 3.票据池
		request.getBody().put("FORCE_DISCOUNT_FLAG", poolNotes.getIfInPool());//强贴标志

		ReturnMessageNew response = ecdsClient.processECDS("30020801", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		
		return false;
	}
*/
	@Override
	public boolean txApplyLock(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			return true;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplySignReject:票据池强制贴现加锁传入参数错误！");
			throw new Exception("BBSP加锁传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID		必输
		request.getBody().put("OPERATION_TYPE", poolNotes.getIsLock());//操作类型  1解锁  0加锁
		request.getBody().put("FLAG", "2");//标记  2-经办锁   20210707 改为经办锁
		
		ReturnMessageNew response = ecdsClient.processECDS("30020207", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean txApplyLockEbk(ECDSPoolTransNotes poolNotes) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			return true;    // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplySignReject:票据池强制贴现加锁传入参数错误！");
			throw new Exception("BBSP加锁传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("ACCT_NO", poolNotes.getAcctNo());//客户账号	必输
		request.getBody().put("BILL_SOURCE", poolNotes.getDataSource());//票据来源	必输
		request.getBody().put("BUSS_TYPE", poolNotes.getTransType());//业务类型 	必输
		request.getBody().put("OPERATION_TYPE", poolNotes.getIsLock());//操作标识1：锁定   2：解锁	必输
		request.getBody().put("TRAN_NO", poolNotes.getTransNo());//交易编号	必输
		request.getBody().put("HOLD_BILL_ID_LIST", poolNotes.getHldrId());//持票ID集合	必输
		
		request.setDetails(poolNotes.getDetails());
		
		ReturnMessageNew response = ecdsClient.processECDS("11025116", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 贴现签收及记账接口(30020802)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	@Override
	public ReturnMessageNew txApplySignBookkeep(ECDSPoolTransNotes poolNotes)
			throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;   // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplySignReject:票据池贴现签收及记账传入参数错误！");
			throw new Exception("票据池贴现签收及记账传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("CONTRACT_NO", poolNotes.getConferNo());//合同号                
		request.getBody().put("BILL_MEDIUM", "ME02");//票据介质              
		request.getBody().put("BILL_TYPE", poolNotes.getBillType());//票据类型              
		request.getBody().put("BUSS_TYPE", "010601");//业务类型              
		request.getBody().put("SOURCE_CHANNEL_NO", "3");//渠道来源              
		request.getBody().put("BUSS_SEND_BRANCH_NO", poolNotes.getInAcctBranch());//业务发起机构          
		request.getBody().put("TRAN_BRANCH_NO", poolNotes.getInAcctBranch());//交易机构              
		request.getBody().put("ACCOUNT_OPERATION_BRANCH_NO", poolNotes.getInAcctBranch());//账务操作机构          
		request.getBody().put("ACCOUNT_BRANCH_NO", poolNotes.getRemitterBatch());//账务机构              
		request.getBody().put("SAFEKEEP_BRANCH_NO", poolNotes.getInAcctBranch());//保管机构              
		request.getBody().put("CUST_MANAGER_ID", poolNotes.getCustNo());//客户经理号            
		request.getBody().put("CUST_MANAGER_NAME", "");//客户经理名称          
		request.getBody().put("CUST_MANAGER_BRANCH_NO", "");//客户经理所属机构      
		request.getBody().put("EXPEND_SEQ_NO", poolNotes.getIouNo());//出账编号              
		request.getBody().put("EXPEND_BATCH_SEQ_NO", poolNotes.getIouNo());//出账批次流水号        
		request.getBody().put("DISCOUNT_DATE", poolNotes.getDiscountDate());//贴现日                
		request.getBody().put("DISCOUNT_INT_RATE", poolNotes.getExeIntRate());//贴现利率              
		request.getBody().put("DISCOUNT_INT_RATE_TYPE", "360");//贴现利率类型          
		request.getBody().put("CLIENT_NO", poolNotes.getCllentNo());//客户号                
		request.getBody().put("ACCT_NO", poolNotes.getAcctNo());//客户账号              
		request.getBody().put("CLIENT_NAME", poolNotes.getCllentNoName());//客户名称              
		request.getBody().put("ADD_INTEREST_ACCT_TYPE", "1");//付息账户类型          
		request.getBody().put("INTEREST_PAY_MODE", poolNotes.getPayIntMode());//付息方式              
		request.getBody().put("BUYER_ADD_INTEREST_RATE", poolNotes.getThirdPayRate());//买方付息比例          
		request.getBody().put("BUYER_INTEREST_PERSON_NAME", poolNotes.getThridAcctName());//买方付息人名称        
		request.getBody().put("BUYER_INTEREST_BANK_NO", poolNotes.getThridOpenBranch());//买方付息人开户行行号  
		request.getBody().put("BUYER_INTEREST_CLIENT_NO", "");//买方付息人客户号      
		request.getBody().put("BUYER_INTEREST_ACCT_NO", poolNotes.getThirdAcctNo());//买方付息人账号        
		request.getBody().put("THIRD_INTEREST_PERSON_NAME", "");//第三方付息人名称      
		request.getBody().put("THIRD_INTEREST_BANK_NO", "");//第三方付息人开户行行号
		request.getBody().put("THIRD_INTEREST_CLIENT_NO", "");//第三方付息人客户号    
		request.getBody().put("THIRD_INTEREST_ACCT_NO", "");//第三方付息人账号      
		request.getBody().put("THIRD_ADD_INTEREST_RATE", "");//第三方付息比例        
		request.getBody().put("CCY", "01");//币种                  
		request.getBody().put("CANCEL_NO", poolNotes.getCancelAccNo());//销账编号              
		request.getBody().put("LOANER_ACCT_NAME", poolNotes.getLoanAcctNoName());//放款账户名称          
		request.getBody().put("ORG_CODE",  poolNotes.getOrgNo());//组织机构代码          
		request.getBody().put("AUDIT_STATUS", "0");//审核状态              
		request.getBody().put("FORCE_DISCOUNT_FLAG", "");//强制贴现标志          
		request.getBody().put("GUARANTEE_NO", poolNotes.getGuaranteeNo());//担保编号             
		request.getBody().put("AGENT_NAME", "");//被代理人名称          
		request.getBody().put("AGENT_ACCT_NO", "");//被代理人账号          
		request.getBody().put("AGENT_OPEN_BANK_NO", "");//被代理人开户行行号    
		request.getBody().put("ENTER_ACCT_NO", poolNotes.getEnterAcctNo());//入账账号              
		request.getBody().put("ENTER_BANK_NO", poolNotes.getEnterBankCode());//入账行号              
		request.getBody().put("REDEEM_INT_RATE", "");//赎回利率              
		request.getBody().put("REDEEM_INT_RATE_TYPE", "");//赎回利率类型          
		request.getBody().put("REDEEM_OPEN_DATE", "");//赎回开放日            
		request.getBody().put("REDEEM_END_DATE", "");//赎回截止日            
		request.getBody().put("QUERY_MODE", "");//查询方式              
		request.getBody().put("QUERY_ACCEPTOR_REG_FLAG", "");//是否查询承兑登记状态  
		request.getBody().put("USE_DETAIL_RATE_FLAG", "0");//是否使用明细利率      
		request.getBody().put("DISCOUNT_QUERY_FLAG", "");//是否先贴后查          
		request.getBody().put("ONLINE_SETTLE_FLAG", "ST02");//线上清算标记          
		request.getBody().put("ONLINE_SETTLE_FLAG", "ST02");//线上清算标记          
		request.getBody().put("FORCE_DISCOUNT_FLAG", poolNotes.getIfInPool());//强贴标志
		request.getBody().put("DEPOSIT_ACCT_NO", poolNotes.getMarginAccount());//保证金账号

		request.setDetails(poolNotes.getDetails());

		ReturnMessageNew response = ecdsClient.processECDS("30020802", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}

	/**
	 * 强制贴现结果查询接口即mis同步人行交易接口(30031802)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	@Override
	public ReturnMessageNew txApplySynchronization(ECDSPoolTransNotes poolNotes)
			throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;   // 不经过核心，直接返回
		}
		
		if (poolNotes == null) {
			logger.error("txApplyFullBill:票据池查询全量票据传入参数错误！");
			throw new Exception("票据池查询全量票据传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("QUERY_TYPE", poolNotes.getTransNo());//查询类型		必输
		request.getBody().put("CONTRACT_NO", poolNotes.getConferNo());//合同号
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID
		request.getBody().put("START_BILL_NO", poolNotes.getBeginRangeNo());//子票区间起始
		request.getBody().put("END_BILL_NO", poolNotes.getEndRangeNo());//子票区间截至
		request.getBody().put("BILL_SOURCE", poolNotes.getBillSource());//票据来源
		ReturnMessageNew response = ecdsClient.processECDS("30031802", request);
		String responseCode = response.getRet().getRET_CODE();
		
		if(responseCode.equals(Constants.TX_SUCCESS_CODE)){
			response.setTxSuccess(true);
			return response;  
		}
		throw new Exception(responseCode);
	}


	/**
	 * bbsp背书转让(30020007)
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
//	@Override
	/*public boolean txApplyEndorsee(ECDSPoolTransNotes poolNotes)throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			return true;    // 不经过核心，直接返回记账成功 
		}
		
		if (poolNotes == null) {
			logger.error("txApplyEndorsee:票据池背书转让传入参数错误！");
			throw new Exception("票据池背书转让传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("BILL_ID",poolNotes.getBillId());//票据ID
		request.getBody().put("BILL_NO",poolNotes.getBillNo());//票据号码
		request.getBody().put("CB_E_SIGN",poolNotes.getSignature());//网银电子签名
		request.getBody().put("ACCT_NO",poolNotes.getApplicantAcctNo());//账号
		request.getBody().put("ENDORSEE_NAME",poolNotes.getToName());//被背书人名称
		request.getBody().put("ENDORSEE_ACCT_NO",poolNotes.getPayeeAcctNo());//被背书人账号
		request.getBody().put("ENDORSEE_OPEN_BANK",poolNotes.getPayeeBankNo());//被背书人开户行行号
		request.getBody().put("ENDORSE_TRANS_FLAG",poolNotes.getEnd());//背书转让标志
		request.getBody().put("REMARK",poolNotes.getToRemark());//备注
		request.getBody().put("OPERATION_TYPE",poolNotes.getOperationType());//操作类型
		
		ReturnMessageNew response = ecdsClient.processECDS("30020007", request);
		
		String responseCode = response.getRet().getRET_CODE();
		
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(responseCode);
		
	}
	*/
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 批量新增
	 */
	public ReturnMessageNew txApplyNewBills(ECDSPoolTransNotes note) throws Exception{
		if (note == null) {
			logger.error("txApplyNewBills:票据批量新增传入参数错误！");
			throw new Exception("票据批量新增传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("E_SIGN", "3");//电子签名   必输
		request.getBody().put("APPLYER_REMARK", "");//申请人备注   
		request.getBody().put("APPLYER_ACCT_NO", note.getAcctNo());//申请人账号   必输
		request.getBody().put("APPLYER_ACCT_NAME", "");//申请人账号识别类型  
		request.getBody().put("APPLYER_OPEN_BANK_NO", "");//申请人账号开户行行号   
		request.getBody().put("APPLYER_CLINET_NO", "");//申请人客户编号 
		request.getBody().put("OPERATION_TYPE", "OC00");//操作类型   必输OC00-新增票据   \OC01-修改票据    OC02-删除票据
		List list = note.getDetails();
		List details = new ArrayList();
		if(null != list && list.size()>0){
			Map map = null;
			for(int i=0;i<list.size();i++){
				PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(i);
				logger.info("批量新增数据，银承明细主键id："+detail.getId()+"；收款人账号："+detail.getPayeeAcct()+"；批次id："+detail.getAcptBatchId());
				map = new HashMap();
				map.put("BILL_INFO_ARRAY.BILL_TYPE", "AC01");//票据类型           必输 
				map.put("BILL_INFO_ARRAY.BILL_MEDIUM", "ME02");//汇票类型       必输     
				map.put("BILL_INFO_ARRAY.DRAW_BILL_DATE", DateUtils.formatDateToString(detail.getIsseDate(),DateUtils.ORA_DATE_FORMAT));//出票日期     必输       
				map.put("BILL_INFO_ARRAY.REMIT_EXPIRY_DATE", DateUtils.formatDateToString(detail.getDueDate(),DateUtils.ORA_DATE_FORMAT));//到期日期         必输   
				map.put("BILL_INFO_ARRAY.BILL_ID", "");//票据id   修改票据时使用
				map.put("BILL_INFO_ARRAY.TERM", "");//期限 
				map.put("BILL_INFO_ARRAY.BILL_AMT", detail.getBillAmt());//票据（包）金额        必输
				map.put("BILL_INFO_ARRAY.DRAWER_NAME", detail.getIssuerName());//出票人名称           必输
				map.put("BILL_INFO_ARRAY.DRAWER_ACCT_NO", detail.getIssuerAcct());//出票人账号      必输     
				map.put("BILL_INFO_ARRAY.DRAWER_ACCT_NAME", "");//出票人账号名称        
				map.put("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NAME", detail.getIssuerBankName());//出票人开户行名称     
				map.put("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NO", detail.getIssuerBankCode());//出票人开户行行号     
				if(detail.getDraftSource().equals(PoolComm.CS01)){
					//老票传收票人账户名称
					map.put("BILL_INFO_ARRAY.PAYEE_NAME", detail.getPayeeAcctName());//收款人名称           必输
				}else{
					map.put("BILL_INFO_ARRAY.PAYEE_NAME", detail.getPayeeName());//收款人名称           必输
				}
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", detail.getPayeeAcct());//收款人账号       必输    
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", detail.getPayeeAcctName());//收款人账号名称      ecds为空，供票，金融机构必输    
				map.put("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NAME", detail.getPayeeBankName());//收款人开户行名称     
				map.put("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NO", detail.getPayeeBankCode());//收款人开户行行号      必输  
				map.put("BILL_INFO_ARRAY.PAYEE_TRANSACT_CHANNEL_NO", "");//收款人业务办理    渠道代码 
				map.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", detail.getAcptBankName());//承兑人名称        必输   
				map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", "0");//承兑人账号           必输
				map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", detail.getAcptBankName());//承兑人账号名称          ecds为空，供票，金融机构必输    
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME", detail.getAcptBankName());//承兑人开户行名称     
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO", detail.getAcptBankCode());//承兑人开户行行号    必输   
				map.put("BILL_INFO_ARRAY.ACCEPTOR_TRANSACT_CHANNEL_NO", "");//承兑人业务办理渠道代码
				map.put("BILL_INFO_ARRAY.SPLIT_PACK_FLAG", detail.getSplitFlag());//是否分包流转   0-否 1-是
				map.put("BILL_INFO_ARRAY.INVOOICE_NO", "");//发票号   
				map.put("BILL_INFO_ARRAY.CONTRACT_NO", note.getContractNo());//合同号         
				if(detail.getTransferFlag().equals("0")){//网银传过来的0为不可转让  
					map.put("BILL_INFO_ARRAY.UNENDORSE_FLAG", "EM01");//背书转让标志         必输
				}else if(detail.getTransferFlag().equals("1")){
					map.put("BILL_INFO_ARRAY.UNENDORSE_FLAG", "EM00");//背书转让标志       
				}
				map.put("BILL_INFO_ARRAY.BILL_SOURCE", detail.getDraftSource());//票据来源
				
				String seq = poolBatchNoUtils.txGetFlowNo();
				map.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", detail.getLoanNo());//单笔票据请求流水       必输  
				map.put("BILL_INFO_ARRAY.AUTO_TYPE", "AT00");//自动类型         必输
				
				
				if(PublicStaticDefineTab.LIMIT_TYPE_1.equals(detail.getLimitType())){//期限模式
					map.put("BILL_INFO_ARRAY.REMIT_EXPIRY_DATE",null);//到期日置空	
					map.put("BILL_INFO_ARRAY.TERM", detail.getEndDate());//期限
				}
				map.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO", "3");//渠道来源
				
				details.add(map);
			}
		}
		request.setDetails(details);
		/*String path = FileName.getFileNameClient("BBSP002")+".txt";
		request.getFileHead().put("FILE_FLAG", "1");
		request.getFileHead().put("FILE_PATH", path);
		request.getFileHead().put("DELIMITOR", "|");*/
		ReturnMessageNew response = ecdsClient.processECDS("BBSP002", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		return response;
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 出票登记
	 */
	/*public ReturnMessageNew txApplyDrawBill(ECDSPoolTransNotes note) throws Exception {
		if (note == null) {
			logger.error("txApplyDrawBill:出票登记传入参数错误！");
			throw new Exception("出票登记传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_ID", note.getBillId());//票据ID
		request.getBody().put("ACCT_NO", note.getAcctNo());//账号
		request.getBody().put("CB_E_SIGN", note.getSignature());//网银电子签名
		request.getBody().put("SENDER_TYPE", "3");//发起方类型
		request.getBody().put("OPERATION_TYPE", "2");//操作类型
		request.getBody().put("AUTO_PROMPT_ACCEPTOR_FLAG", "");//自动提示承兑标志
		request.getBody().put("AUTO_PROMPT_RECV_FLAG", "");//自动提示收票标记
		ReturnMessageNew response = ecdsClient.processECDS("BBSP003", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
*/
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 承兑申请
	 */
	/*public ReturnMessageNew txApplyAcception(ECDSPoolTransNotes note) throws Exception {
		if (note == null) {
			logger.error("txApplyAcception:承兑申请传入参数错误！");
			throw new Exception("承兑申请传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("ACCT_NO", note.getAcctNo());//账号
		request.getBody().put("BILL_ID", note.getBillId());//票据ID
		request.getBody().put("CB_E_SIGN", note.getSignature());//网银电子签名
		request.getBody().put("SENDER_TYPE", "3");//发起方类型
		request.getBody().put("OPERATION_TYPE", note.getOperationType());//操作类型
		ReturnMessageNew response = ecdsClient.processECDS("BBSP004", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
	*/
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 承兑签收
	 */
	public ReturnMessageNew txApplyAcceptionSign(ECDSPoolTransNotes note) throws Exception{
		if (note == null) {
			logger.error("txApplyAcceptionSign:承兑签收传入参数错误！");
			throw new Exception("承兑签收传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("CLIENT_ACCT_NO", note.getAcctNo());//客户账号
		request.getBody().put("BILL_ID", note.getBillId());//票据ID
		request.getBody().put("CB_E_SIGN", note.getSignature());//网银电子签名
		request.getBody().put("SIGN_OPINION", "1");//签收意见
		request.getBody().put("BUSS_TYPE", "200302");//业务类型
		request.getBody().put("OBG_TEXT", "");//预留字段
		request.getBody().put("PROTEST_CODE", "");//拒付代码
		request.getBody().put("PROTEST_REASON", "");//拒付原因                
		request.getBody().put("SENDER_TYPE", "3");//发起方类型
		ReturnMessageNew response = ecdsClient.processECDS("待定", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(responseCode);
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 提示收票
	 */
	/*public boolean txApplyRecvBill(ECDSPoolTransNotes note) throws Exception {
		if (note == null) {
			logger.error("txApplyRecvBill:提示收票传入参数错误！");
			throw new Exception("提示收票传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("ACCT_NO", note.getAcctNo());       //客户账号
		request.getBody().put("BILL_ID", note.getBillId());       //票据ID
		request.getBody().put("CB_E_SIGN", note.getSignature());     //网银电子签名
		request.getBody().put("OPERATION_TYPE", note.getOperationType());//操作类型
		request.getBody().put("SENDER_TYPE", "3");
		ReturnMessageNew response = ecdsClient.processECDS("BBSP006", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return true;
		}
		throw new Exception(responseCode);
	}
*/
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 记账查询
	 */
	public ReturnMessageNew txApplyQueryAcctStatus(ECDSPoolTransNotes note) throws Exception {
		if (note == null) {
			logger.error("txApplyQueryAcctStatus:记账查询传入参数错误！");
			throw new Exception("记账查询传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_NO", note.getBillNo());//票据号
		request.getBody().put("ORI_SEQ_NO", note.getInvoiceNo());//原交易流水
		request.getBody().put("ORI_TRAN_DATE", DateUtils.formatDateToString(note.getTradeDate(),DateUtils.ORA_DATE_FORMAT));//原交易日期
		ReturnMessageNew response = ecdsClient.processECDS("BBSP007", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		logger.error("承兑记账查询失败！");
		throw new Exception(responseCode);
	}

	/**
	 * @author wss
	 * @date 2021-5-12
	 * @description 删除电票
	 * @param note
	 * @throws Exception 
	 */
	public ReturnMessageNew txDeleteApplyBill(ECDSPoolTransNotes note) throws Exception {
		if (note == null) {
			logger.error("txDeleteApplyBill:删除电票传入参数错误！");
			throw new Exception("删除电票传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("E_SIGN", note.getSigner());//电子签名
		request.getBody().put("APPLYER_ACCT_NO", note.getAcceptor());//申请人账号
		request.getBody().put("OPERATION_TYPE", "OC02");//操作类型
		request.getBody().put("SOURCE_CHANNEL_NO", "3");//渠道来源
		
		request.setDetails(note.getDetails());
		
		ReturnMessageNew response = ecdsClient.processECDS("BBSP008", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(responseCode);
	}

	@Override
	public ReturnMessageNew txApplyQueryBillRange(ECDSPoolTransNotes poolNotes)
			throws Exception {
		
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("TICKET_NO", poolNotes.getBillNo());//票号
		request.getAppHead().put("PAGE_NO", poolNotes.getCurrentPage());//
		request.getAppHead().put("PER_PAGE_NUM", poolNotes.getPageSize());//
		ReturnMessageNew response = ecdsClient.processECDS("30610007", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(responseCode);
	}
	
	@Override
	public ReturnMessageNew txQueryHildId(ECDSPoolTransNotes poolNotes)
			throws Exception {
		
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("APPLY_BRANCH_NO", poolNotes.getBranchNo());//申请机构号
		request.getBody().put("BILL_ID", poolNotes.getBillId());//票据ID
		request.getBody().put("START_BILL_NO", poolNotes.getBeginRangeNo());//子票区间起始
		logger.info("发送给电票系统的id为:"+poolNotes.getBillId()+"、票据起始号未："+poolNotes.getBeginRangeNo());
		ReturnMessageNew response = ecdsClient.processECDS("11035129", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(responseCode);
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 汇票撤销
	 */
	/*public ReturnMessageNew txApplyCancleBill(ECDSPoolTransNotes note) throws Exception {
		if (note == null) {
			logger.error("txApplyCancleBill:汇票撤销传入参数错误！");
			throw new Exception("汇票撤销传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_ID", note.getBillId());//票据id
		request.getBody().put("ACCT_NO", note.getAcctNo());//账号
		request.getBody().put("CB_E_SIGN", note.getSignature());//电子签名
		request.getBody().put("SENDER_TYPE", "3");//发送类型
		ReturnMessageNew response = ecdsClient.processECDS("BBSP009", request);
		
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode+response.getRet().getRET_MSG());
	}
*/
//	@Override
	/*public ReturnMessageNew txApplyQueryCancle(ECDSPoolTransNotes note) throws Exception {
		if("0".equals(ECDSPoolTransNotes.IS_OPEN_CORE)){ 
			ReturnMessageNew response = new ReturnMessageNew();
			response.setTxSuccess(true);
			return response;    // 不经过核心，直接返回
		}
		
		if (note == null) {
			logger.error("txApplyQueryCancle:电子票据查询传入参数错误！");
			throw new Exception("电子票据查询传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_NO",note.getBillNo());//票号      
		request.getBody().put("ACCT_NO",note.getAcctNo());//申请人账号  
		request.getBody().put("TRANS_TYPE",note.getTransType());//业务类型        
		request.getBody().put("QUERY_KEY","1");//业务类型        
		request.getBody().put("PER_PAGE_NUM","10");//业务类型        
		
		ReturnMessageNew response = ecdsClient.processECDS("BBSP010", request);
		String responseCode = response.getRet().getRET_CODE();
		// 如果响应成功保存
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
			return response;
		}
		throw new Exception(responseCode);
	}
*/
	
	
	/**
	 * 承兑出账撤销报文输送字段（11011502）
	 * @param poolNotes
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txApplyRevokeSign(ECDSPoolTransNotes poolNotes) throws Exception {
		
		if (poolNotes == null) {
			logger.error("txApplyRevokeApply:票据池撤销传入参数错误！");
			throw new Exception("票据池撤销传入参数错误！");
		}
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("BILL_MEDIUM", "ME02");//ME01 纸票 ME02 电票
		request.getBody().put("CANCEL_MODE", "2");//1：批次撤销 2:明细撤销(默认批次撤销)
		request.getBody().put("ACCEPTANCE_AGREE_NO",poolNotes.getConferNo());//合同编号 
		request.getBody().put("EXPEND_SEQ_NO",poolNotes.getXTransNo());//出账编号(借据编号)
		request.getBody().put("EXPEND_BATCH_SEQ_NO",poolNotes.getLoanAcctNo());//出账请求流水,若信贷出账时出账编号不存在，此字段生效，若出账编号存在，此字段同出账编号
		
		request.setDetails(poolNotes.getDetails());
		
		ReturnMessageNew response = ecdsClient.processECDS("11011502", request);
		return response;
	}
}
