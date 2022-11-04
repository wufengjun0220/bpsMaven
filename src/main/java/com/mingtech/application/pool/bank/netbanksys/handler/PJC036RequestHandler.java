package com.mingtech.application.pool.bank.netbanksys.handler;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.PedBailTrans;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;

/**
 * @Title: 网银接口PJC036保证金划转（存取）接口
 * @Description:
 * @author xie cheng
 * @date 2019-05-27
 */
public class PJC036RequestHandler extends PJCHandlerAdapter{
	private static final Logger logger = Logger.getLogger(PJC036RequestHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private PoolBailEduService poolBailEduService ;
	@Autowired
    private PedAssetPoolService pedAssetPoolService;
    @Autowired
    private FinancialService financialService;
    @Autowired
	private AutoTaskPublishService autoTaskPublishService;
    @Autowired
	private PoolEBankService poolEBankService; //网银方法类

	/**
	 * ①查询协议信息
	 * ②判断是否开通融资票据池'
	 * ③查询存取标志	存则直接增加	取先判断是否保证金足额
	 * ④同步保证金余额
	 * ⑤保证金支取校验		
		保证金冻结或者全冻结——不允许支取
		支取金额大于可用——不允许支取
	 */
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		PoolQueryBean queryParam = this.QueryParamMap(request);
		logger.info("PJC036:网银保证金划转开始....");
		String apId = null;
		String proId = null;
		String bpsNo =  null;
		try {
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, queryParam.getProtocolNo(), queryParam.getCustomernumber(), null, null) ;
			
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
    		apId = ap.getApId();
    		
			if(dto!=null){//开通融资池
				proId = dto.getPoolInfoId();
				bpsNo = dto.getPoolAgreement();
				
				 //assetPool锁
				boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
				if(!isLockedSucss){//加锁失败
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("该票据池有其他额度相关任务正在处理中，请稍后再试！");
					response.setRet(ret);
					return response;
				}
				
				try {
					//同步核心保证金，并重新计算额度
					financialService.txBailChangeAndCrdtCalculation(dto);
				} catch (Exception e) {
					pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG(e.getMessage());
					response.setRet(ret);
					return response;
				}
				
				//不解锁AssetPool表，并重新计算该表数据
				pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,false);

				//获取最新保证金
				AssetType type = pedAssetTypeService.queryPedAssetTypeByProtocol(dto, PoolComm.ED_BZJ_HQ);
				Map map  = poolEBankService.queryMarginBalance(dto.getCustnumber(), dto.getPoolAgreement());
				BigDecimal avalBalance = (BigDecimal)map.get("AVAL_BALANCE");//保证金可支取金额
				
				if(avalBalance.compareTo(queryParam.getMoveMoney())<0){//可支付金额  < 支取 则不允许
					response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
					response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("转出金额高于票据池可支取金额，交易失败！");
				}else{
					if(type!=null && type.getCrdtFree().compareTo(queryParam.getMoveMoney())>=0){//保证金充足的情况

						if(dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_01)||dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_03)){//若保证金被冻结，不允许支取
							response.getBody().put("CHECK_RESULT", PoolComm.CHECK_FLAG_01);
							response.getBody().put("AVAL_BALANCE",type.getCrdtFree());
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("保证金已冻结不允许支取！");
						}else{
							
							//保证金支取
							if(PoolComm.NO.equals(dto.getIsAcctCheck())){//无需申请批
								
								/*
								 * 调核心系统进行保证金支取
								 */
								ret =this.doMarginWithdrawal(queryParam);
								
								if(ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){//划转成功									
									
									//生成支付记录
									PedBailTrans trans = this.toCreateBailTrans(dto, queryParam);
									trans.setStatus(PublicStaticDefineTab.AUDIT_STATUS_PASS);//未处理
									trans.setPlanStatus(PoolComm.BAIL_TRANS_1);//支付成功
									poolBailEduService.txStore(trans);
								}
								
							}else{
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG("保证金划转申请已收到，请后续查询支付结果！");
								
								//生成支付记录
								PedBailTrans trans = this.toCreateBailTrans(dto, queryParam);
								trans.setStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);//未处理
								trans.setPlanStatus(PoolComm.BAIL_TRANS_0);//未处理
								poolBailEduService.txStore(trans);
								
							}
						}
					}else{//保证金不足的情况
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("保证金可用余额不足！");
					}
					
				}
				
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户未开通票据池融资功能,不允许操作！");
			}
		} catch (Exception e) {
			
			pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
			
			logger.error("PJC036-保证金划转(存取)异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("保证金划转(存取)异常! 票据池内部执行错误");
		}
		
		pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
		
		//额度更新
		if(null != proId){		
		    String   id = 	bpsNo +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成
		    Map<String,String> reqParams = new HashMap<String,String>();
		    reqParams.put("proId", proId);
			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_CALCU_NO, id, AutoTaskNoDefine.BUSI_TYPE_CAL, reqParams, bpsNo, bpsNo, null, null);
		}

		
		response.setRet(ret);
		return response;
	}
	
	/**
	 * 生成网银划转记录对象
	 * @param dto
	 * @param queryParam
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-27下午6:00:01
	 */
	private PedBailTrans toCreateBailTrans(PedProtocolDto dto,PoolQueryBean queryParam){
		
		PedBailTrans trans = new PedBailTrans();
		trans.setBpsNo(queryParam.getProtocolNo());//票据池编号
		trans.setCustomer(queryParam.getCustomernumber());//客户号
		trans.setCustName(dto.getCustname());//客户名称
		trans.setCretNo(queryParam.getCardNumber());//凭证编号
		trans.setTranAmt(queryParam.getMoveMoney());//交易金额
		trans.setTranType("1");//保证金支取
		trans.setDrAcctNo(dto.getMarginAccount());//借方账号(保证金账号)
		trans.setDrAcctName(dto.getMarginAccountName());//借方账号名称
		trans.setCrAcctNo(queryParam.getReceMoneAccount());//贷款账号
		trans.setCrAcctName(queryParam.getReceMoneName());//贷款账号名称
		trans.setCreateDate(new Date());//创建日期
		trans.setCreateTime(new Date());
		trans.setUserNo(dto.getAccountManagerId());//客户经理编号
		trans.setUserName(dto.getAccountManager());//客户经理名称
		trans.setBrcBld(dto.getSignDeptNo());//客户经理所在机构(签约机构号)
		trans.setRemark(queryParam.getRemark());//备注
		trans.setUsage(queryParam.getUsage());//用途
		trans.setBrcUser(queryParam.getEbankName());//上传核心柜员
		return trans;
		
		
	}
	/**
	 * 调用核心保证金划转接口
	 * @Description TODO
	 * @author Ju Nana
	 * @param queryParam
	 * @return
	 * @throws Exception
	 * @date 2019-6-16上午2:03:59
	 */
	private Ret doMarginWithdrawal(PoolQueryBean queryParam) throws Exception{
		CoreTransNotes transNotes = new CoreTransNotes();
		Ret ret = new Ret();
		transNotes.setTranAmt(BigDecimalUtils.getStringValue(queryParam.getMoveMoney()));//交易金额   待定
		transNotes.setDrAcctNo(queryParam.getPaymentAccount());//借方账号
		transNotes.setDrAcctNoName(queryParam.getPayBankName());//借方账号名称
		transNotes.setCrAcctNo(queryParam.getReceMoneAccount());//贷款账号
		transNotes.setCrAcctNoName(queryParam.getReceMoneName());//贷款账号名称
		transNotes.setFrntDate(DateUtils.toString(new Date(), "yyyyMMdd"));//第三方日期
		String str = poolBatchNoUtils.txGetFlowNo();
		
		transNotes.setSerSeqNo(str);//第三方流水号
		transNotes.setRemark(queryParam.getUsage()+" "+queryParam.getRemark());//备注
		ReturnMessageNew response = poolCoreService.doMarginWithdrawal(transNotes);
		
		if (response.isTxSuccess()) {
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("保证金划转成功！");
		} else {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("核心保证金划转失败！返回码" + response.getRet().getRET_CODE() + "，返回信息" + response.getRet().getRET_MSG());
		}
		return ret;
		
	}

	/**
	 * @Description: 请求数据处理
	 * @param request
	 * @return PoolQueryBean
	 * @author xie cheng
	 * @date 2019-05-27
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request)
			throws Exception {
		PoolQueryBean pq = new PoolQueryBean();
		Map body = request.getBody();
		pq.setCustomernumber(getStringVal(body.get("CORE_CLIENT_NO"))); //核心客户号
		pq.setProtocolNo(getStringVal(body.get("BPS_NO")));//票据池编号
		pq.setMarginAccount(getStringVal(body.get("DEPOSIT_ACCT_NO")));//保证金账号--不传
		pq.setIsnotFlag(getStringVal(body.get("DEPOSIT_DRAW_FLAG")));//存取标识--不传
		pq.setCardNumber(getStringVal(body.get("CERT_NO")));//凭证号
		pq.setPaymentAccount(getStringVal(body.get("PAYER_ACCT_NO")));//付款账户
		pq.setPayBankName(getStringVal(body.get("PAYER_NAME")));//付款户名（票据池保证金账户户名）
		pq.setBankMoneNo(getStringVal(body.get("BOOK_NO")));//账簿号
		pq.setBankMoneName(getStringVal(body.get("BOOK_NAME")));//账簿名
		pq.setReceMoneAccount(getStringVal(body.get("PAYEE_ACCT_NO")));//收款账号
		pq.setReceMoneName(getStringVal(body.get("PAYEE_NAME")));//收款户名
		pq.setMoveMoney(getBigDecimalVal(body.get("DEDUCT_AMT")));//划转金额
		pq.setMoveType(getStringVal(body.get("CAPS_AMT")));//大写金额
		pq.setUsage(getStringVal(body.get("USAGE")));//用途
		pq.setRemark(getStringVal(body.get("POSTSCRIPT")));//附言
		pq.setEbankName(getStringVal(request.getSysHead().get("USER_ID")));//用户
		pq.setBusinessId(getStringVal(request.getSysHead().get("BRANCH_ID")));//机构
		return pq;
	}
}

