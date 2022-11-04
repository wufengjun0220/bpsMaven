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
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftAccountManagement;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;

/**
 *
 * @Title: 网银接口 PJC032
 * @Description: 客户全量票据查询接口
 * @author wufengjun
 * @date 2018-1-7
 */
public class PJC032RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC032RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;// 网银方法类
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private AssetTypeManageService assetTypeManageService;
	/**
	 * 网银接口 PJC032 全量票据明细查询接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		PoolQueryBean pool = QueryParamMap(request);
		ReturnMessageNew response = new ReturnMessageNew();
		BigDecimal totalAmount = new BigDecimal(0);
		Ret ret = new Ret();
		List detail = new ArrayList();
		List<DraftAccountManagement> acctManageList = null;
		List<DraftAccountManagement> acctManages = null;
		Page page = getPage(request.getAppHead());
		String pageIndex=getStringVal(request.getAppHead().get("QUERY_KEY"));//取得网银当前传值(查询结果定位串),如为第一页则账务管家表实时同步,不为第一页则直接查库
		try {
			/*
			 * 账务管家表实时同步
			 */
			String eleAccNos = null;
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setPoolAgreement(pool.getProtocolNo());
			PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			if(null==dto){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("查询失败，协议编号未关联票据池协议");
				response.setRet(ret);
				return response;
			}
			
			
			if(PoolComm.YES.equals(dto.getIsGroup())){//集团
				ProListQueryBean bean = new ProListQueryBean();
				bean.setBpsNo(dto.getPoolAgreement());
				bean.setCustNo(pool.getCustomernumber());
				PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(bean);
				if(mem!=null){
					if(mem.getElecDraftAccount()!=null && mem.getElecDraftAccount().trim()!=""){							
						eleAccNos = mem.getElecDraftAccount();
					}
				}
				
			}else{
				if(dto.getElecDraftAccount()!=null){					
					eleAccNos = dto.getElecDraftAccount();
				}
			}
			/*
			 * 账务管家表同步操作
			 */
			if(StringUtils.isNotBlank(pageIndex)&&"1".equals(pageIndex)){
				poolEBankService.txAccountManagement(pool.getProtocolNo(),pool.getCustomernumber(),eleAccNos);//账务管家表数据实时更新
			}
			
			/*
			 * 数据查询
			 */
			acctManageList = poolEBankService.queryManagement(pool ,page);
			BigDecimal big = new BigDecimal(0);
			if (acctManageList != null && acctManageList.size() > 0) {
				for (int i = 0; i < acctManageList.size(); i++) {
					DraftAccountManagement acctManagement = acctManageList.get(i);

					PoolQueryBean bean = new PoolQueryBean();
					bean.setBillNo(acctManagement.getDraftNb());// 票号
					
					/********************融合改造新增 start******************************/
					bean.setBeginRangeNo(acctManagement.getBeginRangeNo());// 
					bean.setEndRangeNo(acctManagement.getEndRangeNo());//
					/********************融合改造新增 end******************************/
					
					
					bean.setSBillMedia(acctManagement.getDraftMedia());// 票据介质
					bean.setsBillType(acctManagement.getDraftType());// 票据种类
					bean.setFBillAmount(acctManagement.getIsseAmt());// 票据金额
					bean.setIssDate(DateUtils.formatDate(acctManagement.getIsseDt(),"yyyyMMdd"));// 出票日
					bean.setDueDate(DateUtils.formatDate(acctManagement.getDueDt(),"yyyyMMdd"));// 到期日
					bean.setIsnotFlag(acctManagement.getEdBanEndrsmtMk());// 能否转让标记
					bean.setBillOutName(acctManagement.getDrwrNm());// 出票人全称
					bean.setBillOutAccount(acctManagement.getDrwrAcctId());// 出票人账号
					bean.setBillOutBankNo(acctManagement.getDrwrAcctSvcr());// 出票人开户行行号
					bean.setBillOutBankName(acctManagement.getDrwrAcctSvcrNm());// 出票人开户行名称
					bean.setReceMoneName(acctManagement.getPyeeNm());// 收款人全称
					bean.setReceMoneAccount(acctManagement.getPyeeAcctId());// 收款人账号
					bean.setBankMoneNo(acctManagement.getPyeeAcctSvcr());// 收款人开户行行号
					bean.setBankMoneName(acctManagement.getPyeeAcctSvcrNm());// 收款人开户行名称
					bean.setAcceptorNm(acctManagement.getAccptrNm());// 承兑人全称
					bean.setAccptrId(acctManagement.getAccptrId());// 承兑人账号
					bean.setAcceptorBankCode(acctManagement.getAccptrSvcr());// 承兑人开户行行号
					bean.setAcceptorBankname(acctManagement.getAccptrSvcrNm());// 承兑人开户行名称
					bean.setIsEdu(acctManagement.getIsEdu());// 是否已产生额度 
					bean.setRickLevel(acctManagement.getRiskLevel());// 风险等级
					bean.setPoolEquities(acctManagement.getAssetType());// 票据权益--应收票据
					bean.setLocation(acctManagement.getBillSaveAddr());// 票据保管地
					bean.setRemark(acctManagement.getOtherBankSaveAddr());//他行保管地址
					bean.setSources(acctManagement.getDataSource());// 数据来源
					bean.setContractNo(acctManagement.getContractNo());//交易合同号
					bean.setAcceptanceAgreeNo(acctManagement.getAcceptanceAgreeNo());//承兑协议号
					totalAmount = totalAmount.add(bean.getFBillAmount());
					bean.setIsTrustee(acctManagement.getTrusteeshipFalg());//是否托管票据
					bean.setRemark(acctManagement.getTransferPhase());//是否已入池/票据状态
					
					bean.setDraftSource(acctManagement.getDraftSource());//票据来源
					bean.setSplitFlag(acctManagement.getSplitFlag());//是否可拆分
					bean.setPlDrwrAcctName(acctManagement.getPlDrwrAcctName());//出票人账户名称
					bean.setPlAccptrAcctName(acctManagement.getPlAccptrAcctName());//承兑人账户名称
					bean.setPlPyeeAcctName(acctManagement.getPlPyeeAcctName());//收款人账户名称
					
					bean.setId(acctManagement.getBillId());//票据id
					bean.setChargeAccount(acctManagement.getElecDraftAccount());//电票账号
//					bean.setRemark(acctManagement.getTransferPhase());//付款人开户行行号
//					bean.setRemark(acctManagement.getTransferPhase());//付款人开户行地址
					
					
					big = big.add(bean.getFBillAmount());
					detail.add(bean);
				}
			}
				if(acctManageList != null && acctManageList.size() >0  ){
					response.getBody().put("TOTAL_AMT", big);
					setPage(response.getAppHead(), page);
					response.setDetails(resultDataHandler(detail));
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("查询成功！");
				}else {
					ret.setRET_CODE(Constants.EBK_03);
					ret.setRET_MSG("无符合条件数据！");
				/*}*/
			}

		} catch (Exception ex) {
			logger.error(ex, ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询异常[" + ex.getMessage() + "]");
		}
		response.getBody().put("BPS_NO", pool.getProtocolNo());// 票据池编号
		response.setRet(ret);
		return response;
	}

	/**
	 *
	 * @Description: 请求数据处理
	 * @param request
	 * @return PoolQueryBean
	 * @author tangxiongyu
	 * @date 2018-12-29
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {

		PoolQueryBean pq = new PoolQueryBean();
		Map body = request.getBody();
		pq.setCustomernumber(getStringVal(body.get("CORE_CLIENT_NO"))); // 核心客户号
		pq.setProtocolNo(getStringVal(body.get("BPS_NO"))); // 协议编号
		pq.setIsEdu(getStringVal(body.get("IS_PRODUCED_MONEY"))); // 是否已产生额度
		pq.setRickLevel(getStringVal(body.get("RISK_LEVEL"))); // 风险等级
		pq.setPoolEquities(getStringVal(body.get("BILL_RIGHTS")));// 票据权益
		pq.setIsTrustee(getStringVal(body.get("IS_SPONSOR_BILL")));// 是否托管票据
		pq.setLocation(getStringVal(body.get("BILL_SAVE_ADDR")));// 票据保管地
		pq.setBillNo(getStringVal(body.get("BILL_NO")));// 票号
		if(StringUtils.isNotEmpty(getStringVal(body.get("START_BILL_NO")))){
			pq.setBeginRangeNo(getStringVal(body.get("START_BILL_NO"))); // 子票起始号
		}
		if(StringUtils.isNotEmpty(getStringVal(body.get("END_BILL_NO")))){
			pq.setEndRangeNo(getStringVal(body.get("END_BILL_NO"))); // 子票截止
		}
		pq.setSBillMedia(getStringVal(body.get("BILL_TYPE"))); // 票据类型 纸票电票       
		pq.setBillType(getStringVal(body.get("BILL_CLASS"))); // 票据种类 银承商承
		pq.setPstartDate(getDateVal(body.get("DRAW_START_DATE"))); // 出票日开始
		pq.setPendDate(getDateVal(body.get("DRAW_END_DATE"))); // 出票日结束
		pq.setStartDDueDt(getDateVal(body.get("EXPIRY_START_DATE"))); // 到期日开始
		pq.setEndDDueDt(getDateVal(body.get("EXPIRY_END_DATE"))); // 到期日结束
		pq.setIsseAmtStart(getBigDecimalVal(body.get("HIGH_AMT"))); // 金额上限
		pq.setIsseAmtEnd(getBigDecimalVal(body.get("LOW_AMT"))); // 金额下限
		pq.setBillOutBankNo(getStringVal(body.get("BILL_OPENBANK_NAME")));// 出票人开户行名称
		//用备注字段传值
		pq.setRemark(getStringVal(body.get("INPOOL_FLAG")));//是否入池
		pq.setDraftSource(getStringVal(body.get("BILL_SOURCE")));//票据来源
		return pq;
	}

	/**
	 * 处理响应数据
	 * 
	 * @return List
	 * @author tangxiongyu
	 * @throws Exception 
	 */
	private List resultDataHandler(List result) throws Exception {
		List list = new ArrayList();
		if (result != null && result.size() > 0) {
			HashMap map = null;
			PoolQueryBean bean = null;
			for (int i = 0; i < result.size(); i++) {
				map = new HashMap();
				bean = (PoolQueryBean) result.get(i);
				map.put("BILL_INFO_ARRAY.BILL_NO", bean.getBillNo());// 票据号码
				if(StringUtils.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
					map.put("BILL_INFO_ARRAY.START_BILL_NO", bean.getBeginRangeNo());// 票据号码起
					map.put("BILL_INFO_ARRAY.END_BILL_NO", bean.getEndRangeNo());// 票据号码止
					map.put("BILL_INFO_ARRAY.SPLIT_FLAG", bean.getSplitFlag());//是否可拆分
				}
				
				map.put("BILL_INFO_ARRAY.BILL_CLASS",bean.getsBillType());//票据介质
				map.put("BILL_INFO_ARRAY.BILL_TYPE", bean.getSBillMedia());// 票据种类
				map.put("BILL_INFO_ARRAY.BILL_AMT", bean.getFBillAmount());// 票据金额
				map.put("BILL_INFO_ARRAY.DRAW_DATE", DateUtils.toString(bean.getIssDate(),DateUtils.ORA_DATES_FORMAT));// 出票日
				map.put("BILL_INFO_ARRAY.EXPIRY_DATE",DateUtils.toString(bean.getDueDate(),DateUtils.ORA_DATES_FORMAT));// 到期日
				if(StringUtils.isNotEmpty(bean.getIsnotFlag())){
					if(bean.getIsnotFlag().equals("0")){//可转让
						map.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "1");// 可转让标记
					}else {
						map.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "0");// 不能转让标记
					}
				}
				map.put("BILL_INFO_ARRAY.BILL_NAME", bean.getBillOutName());// 出票人全称
				map.put("BILL_INFO_ARRAY.BILL_ACCT_NO", bean.getBillOutAccount());// 出票人帐号
				map.put("BILL_INFO_ARRAY.BILL_OPENBANK_NAME", bean.getBillOutBankName());// 出票人开户行行号
				map.put("BILL_INFO_ARRAY.REMITTER_OPEN_BANK", bean.getBillOutBankNo());// 出票人开户行名称
				map.put("BILL_INFO_ARRAY.PAYEE_NAME", bean.getReceMoneName());// 收款人全称
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO", bean.getReceMoneAccount());// 收款人帐号
				map.put("BILL_INFO_ARRAY.PAYEE_OPEN_BRANCH", bean.getBankMoneNo());// 收款人开户行行号
				map.put("BILL_INFO_ARRAY.PAYEE_OPENBANK_NAME", bean.getBankMoneName());// 收款人开户行名称
				map.put("BILL_INFO_ARRAY.ACCEPTOR_NAME", bean.getAcceptorNm());// 承兑人全称
				map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO", bean.getAccptrId());// 承兑人帐号
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK", bean.getAcceptorBankCode());// 承兑人开户行全称
				map.put("BILL_INFO_ARRAY.ACCEPTOR_OPENBANK_NAME", bean.getAcceptorBankname());// 承兑人开户行行号
				map.put("BILL_INFO_ARRAY.IS_PRODUCED_MONEY", bean.getIsEdu());// 是否已产生额度
				map.put("BILL_INFO_ARRAY.RISK_LEVEL", bean.getRickLevel());// 风险等级
				map.put("BILL_INFO_ARRAY.BILL_RIGHTS", bean.getPoolEquities());// 票据权益
				map.put("BILL_INFO_ARRAY.IS_SPONSOR_BILL", bean.getIsTrustee());// 是否托管票据
				map.put("BILL_INFO_ARRAY.BILL_SAVE_ADDR", bean.getLocation());// 票据保管地
				map.put("BILL_INFO_ARRAY.OTHER_BANK_SAVE_ADDR", bean.getRemark());// 票据保管地址
				map.put("BILL_INFO_ARRAY.DATA_SOURSE", bean.getSources());// 数据来源
				map.put("BILL_INFO_ARRAY.CONTRACT_NO", bean.getContractNo());//交易合同号
				map.put("BILL_INFO_ARRAY.ACCEPTANCE_AGREE_NO", bean.getAcceptanceAgreeNo());//承兑协议号
				map.put("BILL_INFO_ARRAY.INPOOL_FLAG", bean.getRemark());//是否在池
				map.put("BILL_INFO_ARRAY.BILL_STATUS", bean.getRemark());//票据状态
				
				map.put("BILL_INFO_ARRAY.BILL_SOURCE", bean.getDraftSource());//票据来源
				
				map.put("BILL_INFO_ARRAY.BILL_ACCT_NAME", bean.getPlDrwrAcctName());//出票人账户名称
				map.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NAME", bean.getPlAccptrAcctName());//承兑人账户名称
				map.put("BILL_INFO_ARRAY.PAYEE_ACCT_NAME", bean.getPlPyeeAcctName());//收款人账户名称
				map.put("BILL_INFO_ARRAY.PAYER_OPEN_BANK_NO", bean.getAcceptorBankCode());//付款人开户行行号
				map.put("BILL_INFO_ARRAY.PAYER_OPEN_BANK_ADDR", bean.getAcceptorBankname());//付款人开户行地址
				map.put("BILL_INFO_ARRAY.PRODUCE_LIMIT_ATM_FLAG", bean.getIsEdu());//是否已产生额度
				if ("0".equals(bean.getIsEdu())) {
					map.put("BILL_INFO_ARRAY.LIMIT_AMT", new BigDecimal("0.00"));//
				} else {
					map.put("BILL_INFO_ARRAY.LIMIT_AMT", bean.getFBillAmount());//
				}
				
				map.put("BILL_INFO_ARRAY.BILL_SIGN_ACCT_NO", bean.getChargeAccount());//电票签约账号
				map.put("BILL_INFO_ARRAY.BILL_ID", bean.getId());//票据id
				//顺延天数
				long deferDays = assetTypeManageService.queryDelayDays(bean.getRickLevel(), bean.getDueDate());
				
				map.put("BILL_INFO_ARRAY.DEFER_DATE", deferDays);//顺延天数
				logger.info(map.get("BILL_INFO_ARRAY.BILL_NO")+":"+map.get("BILL_INFO_ARRAY.DRAW_DATE")+":"+map.get("BILL_INFO_ARRAY.DATA_SOURSE"));
				list.add(map);
			}
		}
		return list;
	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
