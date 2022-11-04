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

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.base.service.PoolQueryService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.DraftRangeHandler;


/**
 * @Title: 网银接口PJC037出池后续操作信息录入接口
 * @author xie cheng
 * @date 2019-05-27
 */
public class PJC037RequestHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC037RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private PedProtocolService pedProtocolService;// 协议服务
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private PoolQueryService poolQueryService;
	
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		PlBatchInfo  plBatchInf = new PlBatchInfo();
		List list = request.getDetails();
		List<String> billNos = new ArrayList<String>();

		/********************融合改造新增 start******************************/
		List<String> assetNos = new ArrayList();
		Map<String,PoolBillInfo> changeBills= new HashMap<String, PoolBillInfo>();
		/********************融合改造新增 end******************************/
		
		String batchNo=poolBatchNoUtils.txGetBatchNo("PJC",10);
		String bpsNo = getStringVal(body.get("BPS_NO"));//票据池编号
		String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));//核心客户号
		PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
		
		/*
		 * 冻结校验
		 */
		if (dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_02)|| dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_03)) {
			ret.setRET_CODE(Constants.EBK_04);
			ret.setRET_MSG("该客户票据池的票据额度已冻结，无法发起出池申请！");
			response.setRet(ret);
			logger.info("票据池【"+dto.getPoolAgreement()+"】额度冻结，不允许出池！");
			return response;
			
		} 
		
		/*
		 * 数据校验
		 */
		if (list == null || list.size() == 0){
			ret.setRET_CODE(Constants.EBK_04);
			ret.setRET_MSG("未获取到出池票据信息申请");
			response.setRet(ret);
			logger.info("未获取到出池票据信息！");
			return response;
		}
		
		/*
		 * 集团成员身份校验
		 */

		if(dto.getIsGroup().equals(PoolComm.YES)){
			ProListQueryBean listBean = new ProListQueryBean();
			listBean.setBpsNo(bpsNo);
			listBean.setCustNo(custNo);
			PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(listBean);
			if(!PoolComm.KHLX_01.equals(mem.getCustIdentity())&&!PoolComm.KHLX_03.equals(mem.getCustIdentity())){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户:"+custNo+"身份不为出质人或融资人不允许操作");
				response.setRet(ret);
				return response;
			}
		}
		
		/*
		 * 锁AssetPool表
		 */
		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
		String apId = ap.getApId();
		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
		if(!isLockedSucss){//加锁失败
			ret.setRET_CODE(Constants.EBK_11);
			ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
			response.setRet(ret);
			logger.info("票据池【"+dto.getPoolAgreement()+"】上锁！");
			return response;
		}
		
		
		/*
		 * 出池数据组装
		 */
		try {
			plBatchInf.setId(null);
			plBatchInf.setBpsNo(bpsNo);//票据池编号
			plBatchInf.setCustNo(getStringVal(body.get("CORE_CLIENT_NO")));//核心客户号
			plBatchInf.setDoBatchNo(batchNo);//批次号
			plBatchInf.setESign(getStringVal(body.get("E_SIGN")));//电子签名
			plBatchInf.setOutMode(getStringVal(body.get("OUTPOOL_MODE")));//出池模式
			plBatchInf.setEndorseeAcctNo(getStringVal(body.get("ENDORSEE_ACCT_NO")));//被背书人账号
			plBatchInf.setEndorseeName(getStringVal(body.get("ENDORSEE_NAME")));//被背书人 
			plBatchInf.setEndorseeOpenBank(getStringVal(body.get("ENDORSEE_OPEN_BANK")));//被背书行
			plBatchInf.setRemark(getStringVal(body.get("REMARK")));//备注
			plBatchInf.setDiscountInBankCode(getStringVal(body.get("DISCOUNT_IN_BANK_CODE")));//贴入行行号
			plBatchInf.setDiscountInBankName(getStringVal(body.get("DISCOUNT_IN_BANK_NAME")));//贴入行名称
			plBatchInf.setDiscountMode(getStringVal(body.get("DISCOUNT_MODE")));//贴入方式
			plBatchInf.setOnlineSettleFlag(getStringVal(body.get("ONLINE_SETTLE_FLAG")));//清算标识
			plBatchInf.setUnendorseFlag(getStringVal(body.get("UNENDORSE_FLAG")));//禁止背书标志
			plBatchInf.setDiscountIntRate(getBigDecimalVal(body.get("DISCOUNT_INT_RATE")));//贴现利率
			plBatchInf.setDiscountDate(getDateVal(body.get("DISCOUNT_DATE")));//贴现日期
			plBatchInf.setRedeemIntRate(getBigDecimalVal(body.get("REDEEM_INT_RATE")));//赎回利率
			plBatchInf.setRedeemOpenDate(getDateVal(body.get("REDEEM_OPEN_DATE")));//赎回开放日
			plBatchInf.setRedeemEndDate(getDateVal(body.get("REDEEM_END_DATE")));//赎回截止日
			plBatchInf.setEnterAcctNo(getStringVal(body.get("ENTER_ACCT_NO")));//入账账号
			plBatchInf.setEnterAcctName(getStringVal(body.get("ENTER_ACCT_NAME")));//入账户名
			plBatchInf.setEnterBankCode(getStringVal(body.get("ENTER_BANK_CODE")));//入账行号
			plBatchInf.setInvoiceNo(getStringVal(body.get("INVOICE_NO")));//发票号
			plBatchInf.setContractNo(getStringVal(body.get("CONTRACT_NO")));//合同编号
			plBatchInf.setPledgeeType(getStringVal(body.get("PLEDGEE_TYPE")));//质权类型
			plBatchInf.setPledgeeAcctNo(getStringVal(body.get("PLEDGEE_ACCT_NO")));//质权账号
			plBatchInf.setPledgeeName(getStringVal(body.get("PLEDGEE_NAME")));//质权名称
			plBatchInf.setPledgeeOpenBank(getStringVal(body.get("PLEDGEE_OPEN_BANK")));//质权人开户行行号
			plBatchInf.setOutTime(new Date());//出池时间
			plBatchInf.setDoFlag(PoolComm.DO_00);//处理标识    0：未处理    1：已处理
			
			plBatchInf.setDiscountRateMade(getStringVal(body.get("DISCOUNT_INTEREST_MODE")));//贴现利息方式
			plBatchInf.setThirdPayRate(getStringVal(body.get("THIRD_PAY_RATE")));//第三方支付比例
			plBatchInf.setRateAmt(getBigDecimalVal(body.get("REAL_PAY_INTEREST")));//实付利息金额
			
			
			if (list!=null&&list.size()>0){
				for (int i=0;i<list.size();i++) {

					/********************融合改造适应性改造 start******************************/
					Map map = (Map) list.get(i);
					PoolBillInfo info = dataProcess(map);
					
					/**
					 * 查询票据金额
					 */
					PoolBillInfo billInfo = poolQueryService.queryObj(info.getSBillNo(),info.getBeginRangeNo(),info.getEndRangeNo());
					
					info.setFBillAmount(billInfo.getFBillAmount());
					info.setStandardAmt(billInfo.getStandardAmt());
					
					String billNo = (String) map.get("BILL_INFO_ARRAY.BILL_NO");// 票号
					billNos.add(billNo);
					
					String startNo = (String) map.get("BILL_INFO_ARRAY.START_BILL_NO");// 票号起始
					String endNo = (String) map.get("BILL_INFO_ARRAY.END_BILL_NO");// 票号截止
					String assNo = billNo+ "-" +startNo +"-" +endNo;
					assetNos.add(assNo);
					changeBills.put(assNo, info);//网银发送过来的数据
					/********************融合改造适应性改造 end******************************/
					
				}
			}
			
			
            /*
             * 出池申请
             */
			PoolQueryBean param = new PoolQueryBean();//传递参数的bean
			param.setOutBatchNo(batchNo);//这里只用于传递批次号使用
			Ret result = poolEBankService.txApplyDraftPoolOutPJC003(billNos, plBatchInf.getESign(), dto,param,changeBills,assetNos);
			
			
			if (Constants.TX_SUCCESS_CODE.equals(result.getRET_CODE())) {
				
				
			    //保存批次信息
				poolEBankService.txStore(plBatchInf);
				
				/*
				 * 生成自动任务流水记录 异步执行额度释放 gcj 20210513
				 */
				List<DraftPoolOut> outlist = result.getSomeList3();
				if(outlist.size()>0){					
					Map<String, String> reqParams =new HashMap<String,String>();
					for (DraftPoolOut poolout:outlist) {
						reqParams.put("busiId", poolout.getId());
						autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOLOUT_EDU_TASK_NO, poolout.getId(), AutoTaskNoDefine.BUSI_TYPE_JZY, reqParams, poolout.getPlDraftNb(), null, null, null);
					}
				}
				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("出池申请成功！");
				
				
			} else if(Constants.EBK_05.equals(result)){
				ret.setRET_CODE(Constants.EBK_05);
				ret.setRET_MSG("池额度不足不允许出池！");
			}

		} catch (Exception e) {
			logger.error("PJC037-出池后续操作信息录入异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池内部执行错误");
		}
		
		/*
		 * 解锁AssetPool表，并重新计算该表数据
		 */
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
		
		
		response.setRet(ret);
		return response;
	}
	/**
	 * 
	 * 
	 * @Description: 数据加工处理
	 * @return DraftPoolIn
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 
	 * 融合改造新增 
	 */
	private PoolBillInfo dataProcess(Map map) throws Exception {

		PoolBillInfo info = new PoolBillInfo();
		String id = (String) map.get("BILL_INFO_ARRAY.BUSI_NO"); // 业务明细ID
		String plDraftNb = (String) map.get("BILL_INFO_ARRAY.BILL_NO"); // 电票票号
		String beginRangeNo = (String) map.get("BILL_INFO_ARRAY.START_BILL_NO"); // 票据开始子区间号
		String endRangeNo = (String) map.get("BILL_INFO_ARRAY.END_BILL_NO"); // 票据结束子区间号
		BigDecimal tradeAmt = getBigDecimalVal(map.get("BILL_INFO_ARRAY.TRAN_AMT")); // 交易金额(等分化票据实际交易金额)
		String isSplitFlag = (String) map.get("BILL_INFO_ARRAY.isSplitFlag"); //拆分标记 1是 0否
		
		
		info.setBillinfoId(id);
		info.setSBillNo(plDraftNb);
		if(StringUtils.isNotEmpty(beginRangeNo)){
			beginRangeNo = "0";
		}
		if(StringUtils.isNotEmpty(endRangeNo)){
			endRangeNo = "0";
		}
		info.setBeginRangeNo(beginRangeNo);
		info.setEndRangeNo(endRangeNo);
		info.setSplitFlag(isSplitFlag);
		info.setTradeAmt(tradeAmt);
		return info;
	}
}

