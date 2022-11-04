package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.service.HolidayService;
import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 
 * @Title: 网银接口 PJC005
 * @Description: 池票据查询接口
 * @author Ju Nana
 * @date 2018-10-16
 */
public class PJC005RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC005RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;// 网银方法类
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private AssetTypeManageService assetTypeManageService;

	/**
	 * 网银接口 PJC005 池票据查询接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		QueryResult result = null;
		Ret ret = new Ret();

		try {
			PoolQueryBean queryBean = QueryParamMap(request);
			String queryType = queryBean.getQueryParam();
				Page page = getPage(request.getAppHead());
				
				result = poolEBankService.queryPoolBillKind(queryBean, page);
				
				if (null == result || result.getTotalCount() == 0) {
					ret.setRET_CODE(Constants.EBK_03);
					ret.setRET_MSG("无符合条件数据");
				} else {
					setPage(response.getAppHead(), page,result.getTotalAmount()+"");
					response.getBody().put("TOTAL_AMT", result.getTotalAmount());
					List details = new ArrayList();
					if ("02".equals(queryType)) {
						details = this.detailProcess(result.getRecords());
					} else {
						details = this.vtrustDetailProcess(result.getRecords());
					}
					// 放入details中
					response.setDetails(details);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("查询成功！");
				}

		} catch (Exception ex) {
			logger.error(ex, ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池内部查询异常！");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * 
	 * @Description: 请求数据处理
	 * @param request
	 * @return PoolQueryBean
	 * @author Ju Nana
	 * @date 2018-10-16 上午10:11:31
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		PoolQueryBean pq = new PoolQueryBean();
		Map body = request.getBody();
		pq.setCustomernumber(getStringVal(body.get("CORE_CLIENT_NO"))); // 核心客户号
		pq.setProtocolNo(getStringVal(body.get("BPS_NO")));// 票据池编号
		pq.setQueryParam(getStringVal(body.get("QUERY_TYPE"))); // 查询类型
		pq.setSStatusFlag((getStringVal(body.get("BILL_STATUS")))); // 票据状态
		pq.setRecePayType(getStringVal(body.get("BILL_RIGHTS"))); // 收付类型
		pq.setBillNo(getStringVal(body.get("BILL_NO"))); // 票据号码
		
		/********************融合改造新增 start******************************/
		if(StringUtils.isNotEmpty(getStringVal(body.get("START_BILL_NO")))){
			pq.setBeginRangeNo(getStringVal(body.get("START_BILL_NO"))); // 子票起始号
		}
		if(StringUtils.isNotEmpty(getStringVal(body.get("END_BILL_NO")))){
			pq.setEndRangeNo(getStringVal(body.get("END_BILL_NO"))); // 子票截止
		}
		/********************融合改造新增 end******************************/
		
		pq.setBillType(getStringVal(body.get("BILL_CLASS"))); // 票据种类
		pq.setSBillMedia(getStringVal(body.get("BILL_TYPE"))); // 票据介质
		pq.setBillOutName(getStringVal(body.get("BILL_NAME")));// 出票人名称
		pq.setReceMoneName(getStringVal(body.get("PAYEE_NAME")));// 收款人名称
		pq.setIsseAmtEnd(getBigDecimalVal(body.get("MAX_BILL_AMT"))); // 票据金额上限
		pq.setIsseAmtStart(getBigDecimalVal(body.get("MIN_BILL_AMT"))); // 票据金额下限
		pq.setPstartDate(getDateVal(body.get("DRAW_START_DATE")));//出票开始日期
		pq.setPendDate(getDateVal(body.get("DRAW_END_DATE")));//出票结束日期
		pq.setStartDDueDt(getDateVal(body.get("EXPIRY_START_DATE")));//到期日开始日期
		pq.setEndDDueDt(getDateVal(body.get("EXPIRY_END_DATE")));//到期日结束日期
		pq.setStartPoolIn(getDateVal(body.get("INPOOL_START_DATE")));//入池开始日期
		pq.setEndPoolIn(getDateVal(body.get("INPOOL_END_DATE")));//入池结束日期
		pq.setRickLevel(getStringVal(body.get("RISK_LEVEL")));// 风险等级
		pq.setIsEdu(getStringVal(body.get("IS_PRODUCE_MONEY")));// 是否产生额度
		pq.setBillOutBankName(getStringVal(body.get("BILL_OPENBANK_NAME")));// 出票人开户行名称
		return pq;
	}

	/**
	 * 返回details加工处理
	 * 
	 * @param result
	 * @param @return 融资票据池details
	 * @return List
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2019-1-30 上午10:40:24
	 */
	private List detailProcess(List result) throws Exception {
		ArrayList infoList = new ArrayList();
		if (result != null && result.size() > 0) {
			HashMap map = null;
			for (int i = 0; i < result.size(); i++) {
				map = new HashMap();
				DraftPool draftPool = (DraftPool) result.get(i);
				map.put("BILL_INFO_ARRAY.BILL_NO", draftPool.getAssetNb());// 票据号码
				map.put("BILL_INFO_ARRAY.BILL_CLASS", draftPool.getAssetType());// 票据种类
				map.put("BILL_INFO_ARRAY.SC_DFC_FLAG", ""); // 同城异地标识
				map.put("BILL_INFO_ARRAY.BILL_AMT", draftPool.getAssetAmt());// 票据金额
				map.put("BILL_INFO_ARRAY.DRAW_DATE", draftPool.getPlIsseDt());// 出票日
				map.put("BILL_INFO_ARRAY.EXPIRY_DATE", draftPool.getPlDueDt());// 到期日
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK", draftPool.getPlAccptrSvcr());// 承兑人行号
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPENBANK_NAME", draftPool.getPlAccptrSvcrNm());// 承兑人名称
				map.put("BILL_INFO_ARRAY.PAYEE_NAME", draftPool.getPlPyeeNm());// 收款人名称
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", draftPool.getPlPyeeAcctId());// 收款人账号
				map.put("BILL_INFO_ARRAY.PAYEE_OPENBANK_NAME", draftPool.getPlPyeeAcctSvcrNm());// 收款人开户行名称
				map.put("BILL_INFO_ARRAY.HOLDER_ACCT_NO", draftPool.getAccNo());// 持有人账号
				map.put("BILL_INFO_ARRAY.BUSI_NO", draftPool.getId());// 业务明细ID
				map.put("BILL_INFO_ARRAY.KEEP_ARTICLES_BANK_NAME", "");// 代保管行名称
				String status = draftPool.getAssetStatus();
				if (PoolComm.DS_002.equals(status)) {
					status = "PCS_001";
				} else if (PoolComm.DS_04.equals(status)) {
					status = "PCS_002";
				} else if (PoolComm.DS_06.equals(status)) {
					status = "PCS_003";
				}
				map.put("BILL_INFO_ARRAY.BILL_STATUS", status);// 票据状态

				if (PoolComm.BLACK.equals(draftPool.getBlackFlag())
						|| PoolComm.NOTIN_RISK.equals(draftPool.getRickLevel())) {// 黑名单以及不在风险名单的票据不产生额度
					map.put("BILL_INFO_ARRAY.LIMIT_AMT", new BigDecimal("0"));// 额度金额
				} else {
					map.put("BILL_INFO_ARRAY.LIMIT_AMT", draftPool.getAssetAmt());// 额度金额
				}
				map.put("BILL_INFO_ARRAY.BILL_TYPE", draftPool.getPlDraftMedia());// 票据介质
				map.put("BILL_INFO_ARRAY.INPOOL_DATE", draftPool.getPlTm());// 入池时间
				map.put("BILL_INFO_ARRAY.BILL_NAME", draftPool.getPlDrwrNm());// 出票人名称
				map.put("BILL_INFO_ARRAY.BILL_ACCT_NO", draftPool.getPlDrwrAcctId());// 出票人账号
				map.put("BILL_INFO_ARRAY.REMITTER_OPEN_BANK", draftPool.getPlDrwrAcctSvcr());// 出票人开户行行号
				map.put("BILL_INFO_ARRAY.BILL_OPENBANK_NAME", draftPool.getPlDrwrAcctSvcrNm());// 出票人开户行名称
				map.put("BILL_INFO_ARRAY.RISK_LEVEL", draftPool.getRickLevel());// 风险等级
				String comment = "";// 风险标识说明
				if (PoolComm.BLACK.equals(draftPool.getBlackFlag())) {
					map.put("BILL_INFO_ARRAY.RISK_FLAG", "02");// 风险标识 -黑名单票据不可入池
					comment = "黑名单票据";
				} else if (PoolComm.GRAY.equals(draftPool.getBlackFlag())) {
					map.put("BILL_INFO_ARRAY.RISK_FLAG", "01");// 风险标识 -灰名单票据
					comment = "灰名单票据";
				} else {
					map.put("BILL_INFO_ARRAY.RISK_FLAG", "00");// 风险标识 -非黑名单与黑名单票据
					comment = "非黑灰名单";
				}
				if (PoolComm.NOTIN_RISK.equals(draftPool.getRickLevel())) {
					comment = comment + ",非我行名单票据不产生额度";
				} else if (PoolComm.LOW_RISK.equals(draftPool.getRickLevel())) {
					comment = comment + ",可产生低风险额度";
				} else if (PoolComm.HIGH_RISK.equals(draftPool.getRickLevel())) {
					comment = comment + ",可产生高风险额度";
				}
				map.put("BILL_INFO_ARRAY.RISK_FLAG_REMARK", comment);// 风险标识说明
				map.put("BILL_INFO_ARRAY.PAYER_OPEN_BANK", "");// 收付类型 --只有虚拟票据池有
				map.put("BILL_INFO_ARRAY.PAYER_OPEN_BANK", draftPool.getPlAccptrSvcr());// 付款人开户行行号
				map.put("BILL_INFO_ARRAY.PAYER_OPEN_BANK_ADDR", draftPool.getPlAccptrSvcrNm());// 付款人开户行地址
				map.put("BILL_INFO_ARRAY.DRAWER_GUARANTOR_NAME", "");// 出票保证人名称
				map.put("BILL_INFO_ARRAY.DRAWER_GUARANTOR_ADDRESS", "");// 出票保证人地址
				map.put("BILL_INFO_ARRAY.DRAWER_GUARANTEE_DATE", "");// 出票保证时间
				map.put("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTOR_NAME", "");// 承兑保证人名称
				map.put("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTOR_ADDRESS", "");// 承兑保证人地址
				map.put("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTEE_DATE", "");// 承兑保证时间
				map.put("BILL_INFO_ARRAY.BILL_SAVE_ADDR", "01");// 票据保管地——该接口查到的均为本行票据
				map.put("BILL_INFO_ARRAY.OTHER_BANK_SAVE_ADDR", "");// 他行保管地址
				

				//顺延天数
				long deferDays = assetTypeManageService.queryDelayDays(draftPool.getRickLevel(), draftPool.getPlDueDt());
				
				map.put("BILL_INFO_ARRAY.DEFER_DATE",deferDays);//顺延天数
				
//				map.put("BILL_INFO_ARRAY.REMARK", comment);// 备注
				
				/*
				 * comment  风险说明字段，即网银备注字段，按照业务最新要求改，规则
				 * （1）产生额度该字段不显示；
					       （2）不可入池的显示：黑名单票据不可入池；
					       （3）不产生额的显示：非额度名单票据。
					    --2019-03-24  Ju Nana
				 */
				String comment2 = "";// 风险标识说明
				if(PoolComm.BLACK.equals(draftPool.getBlackFlag())){//黑名单票据
					comment2 = "黑名单票据不可入池";
				}else if(PoolComm.NOTIN_RISK.equals(draftPool.getRickLevel())){//入池不产生额度票据
					comment2 = "非额度名单票据";
				}else{
					comment2 ="";
				}
				map.put("BILL_INFO_ARRAY.REMARK", comment2);// 风险标识说明
				
				
				/*********************************融合改造新增过滤条件start 2022-03-24 wfj*************************************************/
				if(!draftPool.getDraftSource().equals(PoolComm.CS01)){
					map.put("BILL_INFO_ARRAY.START_BILL_NO", draftPool.getBeginRangeNo());// 子票起始号
					map.put("BILL_INFO_ARRAY.END_BILL_NO", draftPool.getEndRangeNo());// 子票截止
					map.put("BILL_INFO_ARRAY.SPLIT_FLAG", draftPool.getSplitFlag());// 是否允许拆分标记 1是 0否
				}
				map.put("BILL_INFO_ARRAY.BILL_SOURCE", draftPool.getDraftSource());// 票据来源

				map.put("BILL_INFO_ARRAY.BILL_ACCT_NAME", draftPool.getPlDrwrAcctName());// 出票人账户名称
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", draftPool.getPlPyeeAcctName());// 收款人账户名称
				map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", draftPool.getPlAccptrAcctName());// 承兑人账户名称

				/*********************************融合改造新增过滤条件end 2022-03-24 wfj*************************************************/
				
				infoList.add(map);
			}
		}
		return infoList;
	}

	/**
	 * 虚拟票据池返回detail数据整理
	 * 
	 * @param result
	 * @return List
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2019-1-30 下午2:54:08
	 */
	private List vtrustDetailProcess(List result) throws Exception {
		ArrayList infoList = new ArrayList();
		if (result != null && result.size() > 0) {
			HashMap map = null;
			for (int i = 0; i < result.size(); i++) {
				map = new HashMap();
				PoolVtrust vPool = (PoolVtrust) result.get(i);
				map.put("BILL_INFO_ARRAY.BILL_NO", vPool.getVtNb());// 票据号码
				if(StringUtil.isNotBlank(vPool.getDraftSource()) && vPool.getDraftSource().equals(PoolComm.CS02)){
					map.put("BILL_INFO_ARRAY.START_BILL_NO", vPool.getBeginRangeNo());// 票据号码起
					map.put("BILL_INFO_ARRAY.END_BILL_NO", vPool.getEndRangeNo());// 票据号码止
				}
				map.put("BILL_INFO_ARRAY.BILL_SOURCE", vPool.getDraftSource());// 票据来源
				map.put("BILL_INFO_ARRAY.BILL_CLASS", vPool.getVtType());// 票据种类
				map.put("BILL_INFO_ARRAY.SC_DFC_FLAG", ""); // 同城异地标识
				map.put("BILL_INFO_ARRAY.BILL_AMT", vPool.getVtisseAmt());// 票据金额
				map.put("BILL_INFO_ARRAY.DRAW_DATE", vPool.getVtisseDt());// 出票日
				map.put("BILL_INFO_ARRAY.EXPIRY_DATE", vPool.getVtdueDt());// 到期日
				
				map.put("BILL_INFO_ARRAY.PAYEE_NAME", vPool.getVtpyeeName());// 收款人名称
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", vPool.getVtpyeeAccount());// 收款人账号
				map.put("BILL_INFO_ARRAY.PAYEE_OPENBANK_NAME", vPool.getVtpyeeBankName());// 收款人开户行名称
				map.put("BILL_INFO_ARRAY.HOLDER_ACCT_NO", vPool.getVtplApplyAcctId());// 持有人账号
				map.put("BILL_INFO_ARRAY.BUSI_NO", vPool.getId());// 业务明细ID
				map.put("BILL_INFO_ARRAY.KEEP_ARTICLES_BANK_NAME", "");// 代保管行名称
				map.put("BILL_INFO_ARRAY.BILL_STATUS", "");// 票据状态
				if(PoolComm.LOW_RISK.equals(vPool.getRickLevel())||PoolComm.HIGH_RISK.equals(vPool.getRickLevel())){
					map.put("BILL_INFO_ARRAY.LIMIT_AMT",vPool.getVtisseAmt());// 额度金额
					map.put("BILL_INFO_ARRAY.IS_PRODUCE_MONEY","1");// 受否产生额度
				}else{
					map.put("BILL_INFO_ARRAY.LIMIT_AMT", new BigDecimal("0"));// 额度金额
					map.put("BILL_INFO_ARRAY.IS_PRODUCE_MONEY","0");// 受否产生额度
				}
				map.put("BILL_INFO_ARRAY.BILL_TYPE", vPool.getVtDraftMedia());// 票据介质
				map.put("BILL_INFO_ARRAY.INPOOL_DATE","");// 入池时间
				map.put("BILL_INFO_ARRAY.BILL_NAME", vPool.getVtdrwrName());// 出票人名称
				map.put("BILL_INFO_ARRAY.BILL_ACCT_NO", vPool.getVtdrwrAccount());// 出票人账号
				map.put("BILL_INFO_ARRAY.REMITTER_OPEN_BANK", vPool.getVtdrwrBankNumber());// 出票人开户行行号
				map.put("BILL_INFO_ARRAY.BILL_OPENBANK_NAME", vPool.getVtdrwrBankName());// 出票人开户行名称
				map.put("BILL_INFO_ARRAY.RISK_LEVEL",vPool.getRickLevel());// 风险等级
				map.put("BILL_INFO_ARRAY.RISK_FLAG", "");// 风险标识  ——未用
				map.put("BILL_INFO_ARRAY.RISK_FLAG_REMARK", "虚拟票据池票据");// 风险标识说明
				map.put("BILL_INFO_ARRAY.PAY_TYPE", vPool.getPayType());// 收付类型 --只有虚拟票据池有
				if("1".equals(vPool.getVtDraftMedia())){//纸票
					map.put("BILL_INFO_ARRAY.PAYER_OPEN_BANK", vPool.getVtaccptrBankAccount());// 付款人开户行行号
					map.put("BILL_INFO_ARRAY.PAYER_OPEN_BANK_ADDR", vPool.getVtaccptrBankAddr());// 付款人开户行地址
				}else{//电票
					map.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK", vPool.getVtaccptrBankAccount());// 承兑人行号
					map.put("BILL_INFO_ARRAY.ACCEPTOR_OPENBANK_NAME", vPool.getVtaccptrBankName());// 承兑人名称
					map.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", vPool.getVtaccptrName());// 承兑人名称
					map.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_ADDR", vPool.getVtaccptrBankAddr());// 承兑人开户行地址
					map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", vPool.getVtaccptrAccount());// 承兑人帐号
					map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", vPool.getPlAccptrAcctName());// 承兑人账户名称
				}
				map.put("BILL_INFO_ARRAY.DRAWER_GUARANTOR_NAME", vPool.getDrwrGuarntrNm());// 出票保证人名称
				map.put("BILL_INFO_ARRAY.DRAWER_GUARANTOR_ADDRESS", vPool.getDrwrGuarntrAddr());// 出票保证人地址
				map.put("BILL_INFO_ARRAY.DRAWER_GUARANTEE_DATE", vPool.getDrwrGuarntrDt());// 出票保证时间
				map.put("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTOR_NAME", vPool.getAccptrGuarntrNm());// 承兑保证人名称
				map.put("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTOR_ADDRESS", vPool.getAccptrGuarntrAddr());// 承兑保证人地址
				if(vPool.getAccptrGuarntrDt()!=null){
					/*Date acptGuDt = DateTimeUtil.parseYYYYMMDD(vPool.getAccptrGuarntrDt());*/
					map.put("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTEE_DATE",vPool.getAccptrGuarntrDt());// 承兑保证时间
				}
				map.put("BILL_INFO_ARRAY.BILL_SAVE_ADDR", vPool.getBillPosition());// 票据保管地
				map.put("BILL_INFO_ARRAY.OTHER_BANK_SAVE_ADDR", vPool.getBillPositionAddr());// 他行保管地址
				map.put("BILL_INFO_ARRAY.REMARK", vPool.getRemarks());// 备注
				
				map.put("BILL_INFO_ARRAY.PAYEE_OPEN_BRANCH", vPool.getVtpyeeBankAccount());// 收款人开户行行号
				if(vPool.getVtaccptrDate()!=null){
					/*Date acceptDt = DateUtils.formatDate(vPool.getVtaccptrDate(),DateUtils.ORA_DATE_FORMAT);*/
					map.put("BILL_INFO_ARRAY.ACCE_DATE",vPool.getVtaccptrDate());// 承兑日期
				}
				
				map.put("BILL_INFO_ARRAY.TRANSFER_FLAG", vPool.getVtTranSfer());// 不得转让标识
				map.put("BILL_INFO_ARRAY.ENDORSER_MSG", vPool.getEndorserInfo());//背书人信息
				map.put("BILL_INFO_ARRAY.CONTRACT_NO", vPool.getContractNo());//交易合同号
				map.put("BILL_INFO_ARRAY.ACCEPTANCE_AGREE_NO", vPool.getAcceptanceAgreeNo());//承兑协议号
				//20210626 wss
				map.put("BILL_INFO_ARRAY.DEFER_DATE", holidayService.getPostponedDay(vPool.getVtdueDt()));//顺延天数
				
				map.put("BILL_INFO_ARRAY.BILL_ACCT_NAME", vPool.getPlDrwrAcctName());// 出票人账户名称
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", vPool.getPlPyeeAcctName());// 收款人账户名称
				

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
