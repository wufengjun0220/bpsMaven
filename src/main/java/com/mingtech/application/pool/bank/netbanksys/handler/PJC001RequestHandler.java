package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryParameter;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 网银接口 PJC001 待查询列表接口
 * 
 */
public class PJC001RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC001RequestHandler.class);

	// 网银方法类
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private DraftPoolInService draftPoolInService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private AssetTypeManageService assetTypeManageService;

	/**
	 * 网银接口 查询类型 PJC001 待查询列表接口
	 * 
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		QueryResult billResult = null;// 查询数据集合
		List details = new ArrayList();// 报文details数据集合
		try {
			QueryParameter params = queryParamMap(request);// 构建查询条件

			/*
			 * 【1】查询校验：
			 * 		(1)判断票据池必须签约融资功能
			 * 		(2)集团票据池判断是否推担保合同且客户身份是否为出质人
			 */
			
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setPoolAgreement(params.getPoolArgumentNo());
			queryBean.setOpenFlag(PoolComm.OPEN_01);//签约融资票据池功能
			PedProtocolDto protocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			
			if(protocolDto == null ){
				ret.setRET_CODE(Constants.EBK_02);
				ret.setRET_MSG("无此票据池信息!");
				response.setRet(ret);
				return response;
			}
			
			params.setProtocolDto(protocolDto);//放入协议对象
			if(protocolDto.getIsGroup().equals(PoolComm.YES)){//集团
				
				if(protocolDto.getContract() == null){//担保合同
					ret.setRET_CODE(Constants.EBK_02);
					ret.setRET_MSG("票据池["+protocolDto.getPoolAgreement()+"],未推担保合同!");
					response.setRet(ret);
					return response;
				}else{
					
					//集团成员判断
					
					ProListQueryBean bean = new ProListQueryBean();
					bean.setCustNo(params.getOrgCode());//客户号
					bean.setBpsNo(params.getPoolArgumentNo());//票据池编号
					List<String> identityList = new ArrayList<String>();
					identityList.add(PoolComm.KHLX_01);
					identityList.add(PoolComm.KHLX_03);
					bean.setStatus(PoolComm.PRO_LISE_STA_01);
					bean.setCustIdentityList(identityList);
					List<PedProtocolList> pedList = pedProtocolService.queryProListByQueryBean(bean);
					
					if(pedList == null || pedList.size() == 0){
						ret.setRET_CODE(Constants.EBK_02);
						ret.setRET_MSG("票据池["+protocolDto.getPoolAgreement()+"]的成员中,"+params.getOrgCode()+"非出质人!");
						response.setRet(ret);
						return response;
					}
					
					params.setProList(pedList.get(0));//放入协议成员对象
					
				}
				
			}

			/*
			 * 实时向BBSP系统同步票据信息，完成名单校验、风险校验、MIS额度校验
			 */
			if(params.getQueryType().equals("01")){
				
                this.txQueryBills(params);
                
                
            }

			/*
			 * 查询Asset表额度信息
			 */
			EduResult eduResult = pedAssetPoolService.queryEduAll(params.getPoolArgumentNo());
						
			response.getBody().put("BILL_TOTAL_AMT", eduResult.getTotalBillAmount());// 票据总额度
			response.getBody().put("LOW_RISK_TOTAL_AMT", eduResult.getLowRiskAmount().add(eduResult.getBailAmountTotail()));// 低风险总额度=低风险票据额度+保证金额度
			response.getBody().put("HIGH_RISK_TOTAL_AMT", eduResult.getHighRiskAmount());// 高风险总额度
			response.getBody().put("USED_LOW_RISK_AMT", eduResult.getUsedLowRiskAmount().add(eduResult.getBailAmountUsed()));// 低风险已用额度=低风险票据已用+保证金已用
			response.getBody().put("USED_HIGH_RISK_AMT", eduResult.getUsedHighRiskAmount());// 高风险已用额度
			response.getBody().put("LOW_RISK_LIMIT_BALANCE", eduResult.getFreeLowRiskAmount().add(eduResult.getBailAmount()));// 低风险可用额度=低风险票据可用额度+保证金可用
			response.getBody().put("HIGH_RISK_LIMIT_BALANCE", eduResult.getFreeHighRiskAmount());// 高风险可用额度
			response.getBody().put("OTHER_BILL_LIMIT_AMT", eduResult.getZeroEduAmount());// 不产生额度票据金额
			response.getBody().put("DEPOSIT_LIMIT_AMT", eduResult.getBailAmount());// 保证金余额
			Page page = getPage(request.getAppHead());
			
			/*
			 *数据库查询结果 
			 */
			params.setSBanEndrsmtFlag(PoolComm.NOT_ATTRON_FLAG_NO);
			billResult = poolEBankService.queryPoolBillInfoByParams(params, page);
			
			if(null != billResult){
				details = this.detailProcess(billResult.getRecords(),params.getQueryType());// 集成报文details信息

//				response.getAppHead().put("TOTAL_ROWS", billResult.getTotalCount());
				response.getBody().put("TOTAL_AMT", billResult.getTotalAmount());
				
				setPage(response.getAppHead(), page);
				response.setDetails(details);// 放入details中
	
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("查询成功！");
								
			}else{
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("无符合条件数据！");
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
	 * @Description: 请求数据加工处理
	 * @return QueryParameter
	 * @author Ju Nana
	 * @date 2018-10-17 下午3:37:01
	 */
	private QueryParameter queryParamMap(ReturnMessageNew request) throws Exception {
		// 构建查询对象
		QueryParameter params = new QueryParameter(); // 新建查询对象
		Map body = request.getBody();
		params.setOrgCode(getStringVal(body.get("CORE_CLIENT_NO")));// 核心客户号
		params.setPoolArgumentNo(getStringVal(body.get("BPS_NO")));// 票据池编号
		params.setQueryType(getStringVal(body.get("QUERY_TYPE"))); // 查询类型
		params.setBillType(getStringVal(body.get("BILL_CLASS"))); // 票据种类
		params.setBillMedia(getStringVal(body.get("BILL_TYPE"))); // 票据介质
		params.setIssueDateBegin(getDateVal(body.get("DRAW_START_DATE")));
		params.setIssueDateEnd(getDateVal(body.get("DRAW_END_DATE")));
		params.setDueDateBegin(getDateVal(body.get("EXPIRY_START_DATE")));
		params.setDueDateEnd(getDateVal(body.get("EXPIRY_END_DATE")));
		params.setPlDrwrAcctSvcrNm(getStringVal(body.get("REMITTER_OPENBANK_NAME")));// 出票人开户行名称
		params.setBillAmountBegin(getBigDecimalVal(body.get("MIN_BILL_AMT"))); // 票据金额下限
		params.setBillAmountEnd(getBigDecimalVal(body.get("MAX_BILL_AMT"))); // 票据金额上限
		params.setBillNo(getStringVal(body.get("BILL_NO"))); // 票据号码

		/********************融合改造新增 start******************************/
		if(StringUtils.isNotEmpty(getStringVal(body.get("START_BILL_NO")))){
			params.setBeginRangeNo(getStringVal(body.get("START_BILL_NO"))); // 子票起始号
		}
		if(StringUtils.isNotEmpty(getStringVal(body.get("END_BILL_NO")))){
			params.setEndRangeNo(getStringVal(body.get("END_BILL_NO"))); // 子票截止
		}
		params.setDraftSource(getStringVal(body.get("BILL_SOURCE"))); // 票据来源
		params.setDrawerAcctName(getStringVal(body.get("DRAWER_CLIENT_NAME"))); // 出票人账户名称
		params.setAcceptorAcctName(getStringVal(body.get("ACCEPTOP_CLIENT_NAME"))); // 承兑人账户名称
		/********************融合改造新增 end******************************/

		params.setAcceptorBankCode(getStringVal(body.get("ACCEPTANCE_BANK_ID")));// 票据承兑行行号
		params.setIsGenerateEdu(getStringVal(body.get("IS_PRODUCE_MONEY"))); // 是否产生额度
		params.setRiskType(getStringVal(body.get("RISK_LEVEL")));// 风险类型
		return params;
	}

	/**
	 * 返回查询结果detail处理
	 * 
	 * @param result
	 * @return List
	 * @throws @author Ju Nana
	 * @date 2019-1-29 下午3:25:14
	 */
	public List detailProcess(List result,String type) throws Exception{
		ArrayList infoList = new ArrayList();
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				PoolBillInfo pool = (PoolBillInfo) result.get(i);
//				if (!DateUtils.checkOverLimited(DateUtils.getCurrDateTime(),pool.getDDueDt())) {
				if (!DateUtils.checkOverLimited(DateUtils.getWorkDayDate(),pool.getDDueDt())) {
					Map infoMap = new HashMap();
					infoMap.put("TASK_INFO_ARRAY.BILL_NO", pool.getSBillNo());// 票据号码
					
					/***********************************融合改造新增字段start******************/
					infoMap.put("TASK_INFO_ARRAY.BILL_SOURCE", pool.getDraftSource());// 票据来源
					if(StringUtils.isNotBlank(pool.getDraftSource()) && pool.getDraftSource().equals(PoolComm.CS02)){
						infoMap.put("TASK_INFO_ARRAY.START_BILL_NO", pool.getBeginRangeNo());// 子票起始号
						infoMap.put("TASK_INFO_ARRAY.END_BILL_NO", pool.getEndRangeNo());// 子票截止
						infoMap.put("TASK_INFO_ARRAY.SPLIT_FLAG", pool.getSplitFlag());// 是否允许拆分标记 1是 0否
					}
					/***********************************融合改造新增字段end******************/
					
					infoMap.put("TASK_INFO_ARRAY.BILL_CLASS", pool.getSBillType());// 票据种类
					infoMap.put("TASK_INFO_ARRAY.BILL_TYPE", pool.getSBillMedia());// 票据介质
					infoMap.put("TASK_INFO_ARRAY.BILL_AMT", pool.getFBillAmount());// 票据金额
					infoMap.put("TASK_INFO_ARRAY.DRAW_DATE", DateUtils.toString(pool.getDIssueDt(),DateUtils.ORA_DATE_FORMAT));// 出票日
					infoMap.put("TASK_INFO_ARRAY.EXPIRY_DATE", DateUtils.toString(pool.getDDueDt(),DateUtils.ORA_DATE_FORMAT));// 到期日
					infoMap.put("TASK_INFO_ARRAY.ACCEPTOR_OPEN_BANK", pool.getSAcceptorBankCode());// 承兑人行号
					infoMap.put("TASK_INFO_ARRAY.ACCEPTOR_OPENBANK_NAME", pool.getSAcceptorBankName());// 承兑人行名
					infoMap.put("TASK_INFO_ARRAY.ACCEPTOR_NAME", pool.getSAcceptor());// 承兑人客户名称
					infoMap.put("TASK_INFO_ARRAY.HOLDER_ACCT_NO", pool.getAccNo());// 持有人账号(电票签约账号)
					infoMap.put("TASK_INFO_ARRAY.HOLDER_NAME", pool.getCustName());// 持票人名称
					infoMap.put("TASK_INFO_ARRAY.BILL_NAME", pool.getSIssuerName());// 出票人客户名称
					infoMap.put("TASK_INFO_ARRAY.BILL_ACCT_NO", pool.getSIssuerAccount());// 出票人账号
					infoMap.put("TASK_INFO_ARRAY.BILL_OPENBANK_NAME", pool.getSIssuerBankName());// 出票人开户行名称
					infoMap.put("TASK_INFO_ARRAY.PAYEE_NAME", pool.getSPayeeName());// 收款人客户名称
					infoMap.put("TASK_INFO_ARRAY.PAYEE_ACCT_NO", pool.getSPayeeAccount());// 收款人账号
					infoMap.put("TASK_INFO_ARRAY.PAYEE_OPENBANK_NAME", pool.getSPayeeBankName());// 收款人开户行
					infoMap.put("TASK_INFO_ARRAY.HOLDER_OPENBANK_NAME", "");// 持票人开户行名称:操作人开户行——无
					infoMap.put("TASK_INFO_ARRAY.KEEP_ARTICLES_BANK_NAME", "");// 代保管行名称
					infoMap.put("TASK_INFO_ARRAY.BUSI_STATUS", "持有");// 业务状态 ???字段不确定
					infoMap.put("TASK_INFO_ARRAY.BUSI_NO", pool.getBillinfoId());// 业务明细ID

					//顺延天数
					long deferDays = assetTypeManageService.queryDelayDays(pool.getRickLevel(), pool.getDDueDt());
					
					infoMap.put("TASK_INFO_ARRAY.DEFER_DATE", deferDays);// 顺延天数
					
					if ("1".equals(pool.getSBanEndrsmtFlag())) {// 不得转让
						infoMap.put("TASK_INFO_ARRAY.TRANSFER_FLAG", "0");// 不得转让
					} else {
						infoMap.put("TASK_INFO_ARRAY.TRANSFER_FLAG", "1");// 可转让
					}
					// 是否产生额度
					String idEdu = "0";
					if(pool.getBlackFlag() != null && pool.getRickLevel() != null ){
						if (PoolComm.BLACK.equals(pool.getBlackFlag()) || PoolComm.NOTIN_RISK.equals(pool.getRickLevel())) {// 黑名单以及不在风险名单的票据不产生额度
							idEdu = "0";
						} else {
							idEdu = "1";
						}
					}
					infoMap.put("TASK_INFO_ARRAY.IS_PRODUCE_MONEY", idEdu);// 是否产生额度
					if ("0".equals(idEdu)) {
						infoMap.put("TASK_INFO_ARRAY.LIMIT_AMT", new BigDecimal("0.00"));//
					} else {
						infoMap.put("TASK_INFO_ARRAY.LIMIT_AMT", pool.getFBillAmount());//
					}
					if (PoolComm.BLACK.equals(pool.getBlackFlag())) {//黑名单
						infoMap.put("TASK_INFO_ARRAY.RISK_FLAG", "01");// 风险标识 -黑名单票据不可入池
						infoMap.put("TASK_INFO_ARRAY.RISK_LEVEL", pool.getRickLevel());// 风险等级
					}else {//没黑名单
						if(PoolComm.NOTIN_RISK.equals(pool.getRickLevel())){//不产生额度
							infoMap.put("TASK_INFO_ARRAY.RISK_LEVEL", pool.getRickLevel());// 风险等级
						}else{
							infoMap.put("TASK_INFO_ARRAY.RISK_LEVEL", pool.getRickLevel());// 风险等级
						}
						infoMap.put("TASK_INFO_ARRAY.RISK_FLAG", pool.getBlackFlag());// 风险标识 -灰名单票据
					}
					

					String comment = "";// 风险标识说明
					if(PoolComm.BLACK.equals(pool.getBlackFlag())){//黑名单票据
						comment = "黑名单票据不可入池";
					}else if(PoolComm.NOTIN_RISK.equals(pool.getRickLevel())){//入池不产生额度票据
						comment = "非额度名单票据";
					}else{
						comment ="";
					}
					infoMap.put("TASK_INFO_ARRAY.RISK_FLAG_REMARK", comment);// 风险标识说明
					infoMap.put("TASK_INFO_ARRAY.BILL_ID", pool.getDiscBillId());// 票据ID
					
					infoMap.put("TASK_INFO_ARRAY.BILL_ACCT_NAME", pool.getSIssuerAcctName());// 出票人账户名称
					infoMap.put("TASK_INFO_ARRAY.BILL_OPENBANK_NO", pool.getSIssuerBankCode());// 出票人开户行行号
					infoMap.put("TASK_INFO_ARRAY.PAYEE_ACCT_NAME", pool.getSPayeeAcctName());// 收款人账户名称
					infoMap.put("TASK_INFO_ARRAY.PAYEE_OPENBANK_NO", pool.getSPayeeBankCode());// 收款人开户行行号
					infoMap.put("TASK_INFO_ARRAY.ACCEPTOR_ACCT_NO", pool.getSAcceptorAccount());// 承兑人账号
					infoMap.put("TASK_INFO_ARRAY.ACCEPTOR_ACCT_NAME", pool.getSAcceptorAcctName());// 承兑人账户名称
					infoMap.put("TASK_INFO_ARRAY.TRAN_RRIOR_NAME", pool.getTranRpiorName());// 直接前手名称
					infoMap.put("TASK_INFO_ARRAY.HOLD_BILL_ID", pool.getHilrId());// 持票id
					
					
					
					infoList.add(infoMap);
				}
			}
		}
		return infoList;
	}
	
	/**
	 * 向BBSP查询票据
	 * @param params
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-26上午10:52:41
	 */
	public void txQueryBills(QueryParameter params) throws Exception{

		
		PedProtocolDto dto = params.getProtocolDto();		
		List<String> acctNoList = new ArrayList<String>();//电票签约客户号
		if( dto.getIsGroup().equals(PoolComm.NO)){
			
			
			/*
			 * 单户票据池票据同步
			 */
			this.txGetSingleBills(dto);                                       
			
			if(StringUtils.isNotBlank(dto.getElecDraftAccount())){
				String acc[]=dto.getElecDraftAccount().split("\\|");
				for(int i=0;i<acc.length;i++){
					acctNoList.add(acc[i]);
				}
			}
		}
		
		if( dto.getIsGroup().equals(PoolComm.YES) ){
			PedProtocolList proList= params.getProList();
			
			/*
			 * 集团票据池票据同步
			 */
			this.txGetGroupBills(dto,proList);
			
			if(StringUtils.isNotBlank(proList.getElecDraftAccount())){
				String acc[]=proList.getElecDraftAccount().split("\\|");
				for(int i=0;i<acc.length;i++){
					acctNoList.add(acc[i]);
				}
			}
		}
		
		/*
		 * 向信贷系统进行额度校验
		 */

//		blackListManageService.txMisCreditCheck(this.queryCheckBills(params.getOrgCode(), acctNoList));
		
		
	}
	
	
	/**
	 * 从BBSP获取票据--单户
	 * @param dto
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-26上午11:16:40
	 */
	public List<String> txGetSingleBills(PedProtocolDto dto) throws Exception{
		
		logger.info("PJC001--从BBSP获取票据--单户");
		
		/*
		 * 收集该客户全部电票签约账号，并记录电票签约账号与协议的关系到accMap中
		 */
		String accNos = "" ;//用于拼接电票签约账号
		List<String> acctNoList =  new ArrayList<String>();//电票签约账号list

		Map accMap = new HashMap();//用于存放电票签约账号和协议的对应关系
		String accNo = dto.getElecDraftAccount();//电票签约账号
		if(accNo != null && !"".equals(accNo)){
			String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
			for (int j = 0; j < arr.length; j++) {
				accMap.put(arr[j], dto);
				accNos = accNos + "|" + arr[j];
				acctNoList.add(arr[j]);
			}
		}
		
		/*
		 * 查询数据库中该客户该票据池签约账号下所有DS_00的新建数据放入billMap集合中
		 */
		Map<String, PoolBillInfo> billMap = new HashMap<String, PoolBillInfo>();
		PoolQueryBean query = new PoolQueryBean();
		query.setSStatusFlag(PoolComm.DS_00);
		query.setCustomernumber(dto.getCustnumber());
		query.setAcctNoList(acctNoList);//电票签约账号
		List poolList = draftPoolQueryService.queryPoolBillInfoByPram(query);

		if(poolList != null ){
			logger.info("客户 "+dto.getCustnumber()+" 电票签约账号 "+accNos+" 查询到票据状态为DS_00的票有["+poolList.size()+"]张");
			for (int i = 0; i < poolList.size(); i++) {
				PoolBillInfo info = (PoolBillInfo) poolList.get(i);
				if(info.getDraftSource().equals(PoolComm.CS01)){
					billMap.put(info.getDiscBillId(), info);
					logger.info("老电票票据id为["+info.getDiscBillId()+"]，票号为："+info.getSBillNo());
				}else{
					billMap.put(info.getHilrId(), info);
					logger.info("新电票持票id为["+info.getHilrId()+"]，票号为："+info.getSBillNo());
				}
			}
		}


		if(StringUtil.isNotBlank(accNos)){
			accNos = accNos.substring(1, accNos.length());

			/*
			 * 调用BBSP持有票据查询，风险校验并落库
			 */
			draftPoolInService.txQueryBillsFromBbsp(accNos, billMap, accMap,false);			


		}
		
		
		return acctNoList;
	}
	

	/**
	 * 从BBSP获取票据--集团
	 * @param pedDto
	 * @param proSub
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-26上午11:16:13
	 */
	public List<String> txGetGroupBills(PedProtocolDto pedDto,PedProtocolList proSub) throws Exception{
		
		logger.info("PJC001--从BBSP获取票据--集团");
		
		
		/*
		 * 收集该客户全部电票签约账号，并记录电票签约账号与协议的关系到accMap中
		 */
		String accNos = "" ;//用于拼接电票签约账号
		List<String> acctNoList =  new ArrayList<String>();//电票签约账号list
		Map accMap = new HashMap();//用于存放电票签约账号和协议的对应关系

		String accNo = proSub.getElecDraftAccount();//电票签约账号
		if(accNo != null && !"".equals(accNo)){
			String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
			for (int j = 0; j < arr.length; j++) {
				accMap.put(arr[j], pedDto);
				accNos = accNos + "|" + arr[j];
				acctNoList.add(arr[j]);
			}
		}
		
		/*
		 * 查询数据库中该客户该票据池签约账号下所有DS_00的新建数据放入billMap集合中
		 */
		
		Map<String, PoolBillInfo> billMap = new HashMap<String, PoolBillInfo>();
		PoolQueryBean query = new PoolQueryBean();
		query.setSStatusFlag(PoolComm.DS_00);
		query.setCustomernumber(proSub.getCustNo());
		query.setAcctNoList(acctNoList);//电票签约账号
		List poolList = draftPoolQueryService.queryPoolBillInfoByPram(query);

		if(poolList != null ){
			for (int i = 0; i < poolList.size(); i++) {
				PoolBillInfo info = (PoolBillInfo) poolList.get(i);
				if(info.getDraftSource().equals(PoolComm.CS01)){
					billMap.put(info.getDiscBillId(), info);
				}else {
					billMap.put(info.getHilrId(), info);
				}
			}
		}

		
		if(StringUtil.isNotBlank(accNos)){
			accNos = accNos.substring(1, accNos.length());

			/*
			 * 调用BBSP持有票据查询，风险校验并落库
			 */
			draftPoolInService.txQueryBillsFromBbsp(accNos, billMap, accMap,false);

		}
		return acctNoList;
	}
	
	/**
	 * 查询所有需要向额度系统校验额度的票据列表
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-26上午11:39:13
	 */
	private List<PoolBillInfo> queryCheckBills(String custNo,List acctNoLsit)throws Exception{		
		StringBuffer hql = new StringBuffer("select bill from PoolBillInfo bill ");
		List keys = new ArrayList();
		List values = new ArrayList();
		
		hql.append(" where 1=1 ");
		hql.append(" and bill.SDealStatus ='DS_00'");//初始化
		hql.append(" and bill.blackFlag != '02' ");//不在黑名单
		hql.append(" and bill.SBanEndrsmtFlag = '0' ");//可转让		
		hql.append(" and bill.ebkLock != '0' ");//BBSP锁票
		//hql.append(" and (bill.rickLevel != 'FX_03' or bill.rickLevel is null) ");//风险标识不为不在风险名单
		
		hql.append(" and custNo =:custNo ");//客户号
		keys.add("custNo");
		values.add(custNo);
		
		hql.append(" and accNo in(:acctNoLsit) ");//电票签约账号
		keys.add("acctNoLsit");
		values.add(acctNoLsit);
		
		List billList = draftPoolInService.find(hql.toString(), (String[]) keys.toArray(new String[keys.size()]), values.toArray());
		return billList;
	}
	

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
