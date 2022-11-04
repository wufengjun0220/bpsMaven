package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.common.domain.*;
import com.mingtech.application.pool.common.service.PedProtocolService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;

/**
 * 
 * @Title: 网银接口 PJC024
 * @Description: 票据信息登记接口
 * @author Ju Nana
 * @date 2018-11-08
 */
public class PJC024RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC024RequestHandler.class);
   
	@Autowired
	private PoolEBankService poolEBankService; //网银方法类
	@Autowired
	private PoolVtrustService poolVtrustService;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private PedProtocolService pedProtocolService;
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request)
			throws Exception {
		
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		
		try {
			String busiType = getStringVal(request.getBody().get("BUSS_TYPE"));//业务类型   01-虚拟入池 02-融资入池 03-纸票代付
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));//核心客户号
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));//票据池编号
			String transType = getStringVal(request.getBody().get("OPERATION_TYPE"));//操作类型 01-新增 02-修改 03-删除
			String payType= getStringVal(request.getBody().get("BILL_RIGHTS"));//权益类型QY_01 持有票据  QY_02应付票据
			String custname="";//客户名称
			String poolName="";//票据池名称
			ProtocolQueryBean queryBeanped = new ProtocolQueryBean();
			ProListQueryBean queryBean = new ProListQueryBean();
			queryBeanped.setPoolAgreement(bpsNo);
			queryBeanped.setvStatus(PoolComm.VS_01);
			PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBeanped);
			if(null==pedProtocolDto){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("票据池【"+bpsNo+"】为非签约状态,不允许录票操作!");
				response.setRet(ret);
				return response;
			}
			if(PoolComm.NO.equals(pedProtocolDto.getIsGroup())) {
				custname = pedProtocolDto.getCustname();
				poolName = pedProtocolDto.getPoolName();
			}
			if(PoolComm.YES.equals(pedProtocolDto.getIsGroup())){
				queryBean.setBpsNo(bpsNo);//票据池编号
				queryBean.setCustNo(custNo);//客户号
				queryBean.setStatus(PoolComm.PRO_LISE_STA_01);
				PedProtocolList pedList = pedProtocolService.queryProtocolListByQueryBean(queryBean);//客户号查询
				if(null!=pedList){
					custname = pedList.getCustName();
					poolName = pedList.getBpsName();
				}else{
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据池【"+bpsNo+"】中无客户【"+custNo+"】有效签约信息,不允许操作!");
					response.setRet(ret);
					return response;
				}
			}
			List list = request.getDetails();
			String exBills = "";
			String msg = "";
			if (list != null && list.size() > 0) {
					Map map = null;
					for (int i=0;i<list.size();i++) {
						map = (Map)list.get(i);
						PoolVtrust  vtrust = dataProcess(map ,custNo);
						vtrust.setVtEntpNo(custNo);
						vtrust.setVtEntpName(custname);
						vtrust.setBpsNo(bpsNo);
						vtrust.setBpsName(poolName);
						if(StringUtils.isNotBlank(payType)){
							vtrust.setPayType(payType);
						}
						List<PoolVtrust> vtList = poolVtrustService.queryPoolVtrust(null,vtrust.getVtNb(), custNo, null, null);
						if("01".equals(transType) && vtList!=null && vtList.size()>0){//若同一个客户名下有录入同一张票，则跳过，并记录票号
							exBills=exBills+"【"+vtrust.getVtNb()+"】";	
						}else{
							poolEBankService.txPoolVtrustStore(vtrust,transType);
						}
					}
				}
				if(StringUtils.isNotBlank(exBills)){
					msg = "操作成功，票据："+exBills+"不可重复录入";
				}else{
					msg = "操作成功!";
				}
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(msg);
				} catch (Exception e) {
					logger.error(e, e);
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据池异常");
				}
		response.setRet(ret);
		return response;
	}
	/**
	 * 
	 * @Description: 数据加工处理
	 * @return PoolVtrust  
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2018-11-08 下午3:25:24
	 */
	private PoolVtrust dataProcess (Map map ,String clientNO) throws Exception{
		
		PoolVtrust vtrust = new PoolVtrust();
		
		 String  vtNb = getStringVal(map.get("BILL_INFO_ARRAY.BILL_NO")); //票据号码
		 String draftMedia = getStringVal( map.get("BILL_INFO_ARRAY.BILL_TYPE")); //票据介质
		 String  vtType = getStringVal( map.get("BILL_INFO_ARRAY.BILL_CLASS")); //票据种类 AC01银承	AC02商承
		 String  vtaccptrName = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTOR_NAME")); //承兑人名称
		 
		 String vtaccptrBankName = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTOR_OPENBANK_NAME")); //承兑人开户行全称 
		 String vtaccptrBankAccount = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK")); //承兑人开户行行号 
		 String vtaccptrBankAddr = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_ADDR")); //承兑人开户行地址 
		 String  vtaccptrAccount = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO")); //承兑人账号
		 String  vtpyeeName = getStringVal( map.get("BILL_INFO_ARRAY.PAYEE_NAME")); //收款人名称
		 String vtpyeeBankAccount = getStringVal( map.get("BILL_INFO_ARRAY.PAYEE_OPEN_BRANCH")); //收款人开户行行号 
		 String vtpyeeBankName = getStringVal( map.get("BILL_INFO_ARRAY.PAYEE_OPENBANK_NAME")); //收款人开户行名称 
		 String  vtpyeeAccount = getStringVal( map.get("BILL_INFO_ARRAY.PAYEE_ACCT_NO")); //收款人账号
		 BigDecimal  vtisseAmt = getBigDecimalVal( map.get("BILL_INFO_ARRAY.BILL_AMT")); //票据金额
		 String  vtdrwrName = getStringVal( map.get("BILL_INFO_ARRAY.BILL_NAME")); //出票人名称
		 String  vtdrwrAccount = getStringVal( map.get("BILL_INFO_ARRAY.BILL_ACCT_NO")); //出票人账号
		 String vtdrwrBankNumber = getStringVal( map.get("BILL_INFO_ARRAY.REMITTER_OPEN_BANK")); //出票人开户行行号 
		 String vtdrwrBankName = getStringVal( map.get("BILL_INFO_ARRAY.BILL_OPENBANK_NAME")); //出票人开户行名称 
		 Date  vtisseDt = getDateVal( map.get("BILL_INFO_ARRAY.DRAW_DATE")); //出票日
		 Date  vtdueDt = getDateVal(map.get("BILL_INFO_ARRAY.EXPIRY_DATE")); //到期日
		 Date vtaccptrDate = getDateVal(map.get("BILL_INFO_ARRAY.ACCE_DATE")); //承兑日期 Date
		 String vtTranSfer = getStringVal( map.get("BILL_INFO_ARRAY.TRANSFER_FLAG")); //能否转让标记
		 String endorserInfo = getStringVal( map.get("BILL_INFO_ARRAY.ENDORSER_MSG")); //背书人信息
		 String id = getStringVal( map.get("BILL_INFO_ARRAY.SERIAL_NO")); //业务明细ID
		 String pyerBankNo = getStringVal( map.get("BILL_INFO_ARRAY.PAYER_OPEN_BANK")); //付款人开户行行号
		 String pyerBankAddr = getStringVal( map.get("BILL_INFO_ARRAY.PAYER_OPEN_BANK_ADDR")); //付款人开户行地址
		 String drwrGuarntrNm = getStringVal( map.get("BILL_INFO_ARRAY.DRAWER_GUARANTOR_NAME")); //出票保证人名称  
		 String drwrGuarntrAddr = getStringVal( map.get("BILL_INFO_ARRAY.DRAWER_GUARANTOR_ADDRESS")); //出票保证人地址  
		 String drwrGuarntrDt = getStringVal( map.get("BILL_INFO_ARRAY.DRAWER_GUARANTEE_DATE")); //出票保证时间    
		 String accptrGuarntrNm = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTOR_NAME")); //承兑保证人名称  
		 String accptrGuarntrAddr = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTOR_ADDRESS")); //承兑保证人地址  
		 String accptrGuarntrDt = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTANCE_GUARANTEE_DATE")); //承兑保证时间    
		 String billPosition = getStringVal( map.get("BILL_INFO_ARRAY.BILL_SAVE_ADDR")); //票据保管地      
		 String billPositionAddr = getStringVal( map.get("BILL_INFO_ARRAY.OTHER_BANK_SAVE_ADDR")); //他行保管地址    
		 String remarks = getStringVal( map.get("BILL_INFO_ARRAY.REMARK")); //备注
		 String contractNo = getStringVal( map.get("BILL_INFO_ARRAY.CONTRACT_NO")); //交易合同号
		 String acceptanceAgreeNo = getStringVal( map.get("BILL_INFO_ARRAY.ACCEPTANCE_AGREE_NO")); //承兑协议编号
		 
		 String startBillNo = getStringVal( map.get("BILL_INFO_ARRAY.START_BILL_NO")); //票据号起
		 String endBillNo = getStringVal( map.get("BILL_INFO_ARRAY.END_BILL_NO")); //票据号止
		 String billSource = getStringVal( map.get("BILL_INFO_ARRAY.BILL_SOURCE")); //票据来源
		 String splitFlag = getStringVal( map.get("BILL_INFO_ARRAY.SPLIT_FLAG")); //是否可拆分

		 if(StringUtils.isNotBlank(startBillNo)){
			 vtrust.setBeginRangeNo(startBillNo);
		 }else{
			 vtrust.setBeginRangeNo("0");
		 }
		 if(StringUtils.isNotBlank(endBillNo)){
			 vtrust.setEndRangeNo(endBillNo);
		 }else{
			 vtrust.setEndRangeNo("0");
		 }
		 logger.info("出票人全称："+vtdrwrName);
		 logger.info("承兑人名称："+vtaccptrName);
		 logger.info("收款人名称："+vtpyeeName);
		 vtrust.setDraftSource(billSource);
		 vtrust.setSplitFlag(splitFlag);
		 vtrust.setVtNb(vtNb);
		 vtrust.setVtDraftMedia(draftMedia);
		 vtrust.setVtType(vtType);
		 vtrust.setVtaccptrName(vtaccptrName);
		 vtrust.setVtaccptrBankName(vtaccptrBankName);
		 vtrust.setVtaccptrAccount(vtaccptrAccount);
		 vtrust.setVtpyeeName(vtpyeeName);
		 vtrust.setVtpyeeBankAccount(vtpyeeBankAccount);
		 vtrust.setVtpyeeBankName(vtpyeeBankName);
		 vtrust.setVtpyeeAccount(vtpyeeAccount);
		 vtrust.setVtisseAmt(vtisseAmt);
		 vtrust.setVtdrwrName(vtdrwrName);
		 vtrust.setVtdrwrAccount(vtdrwrAccount);		 
		 vtrust.setVtdrwrBankNumber(vtdrwrBankNumber);
		 vtrust.setVtdrwrBankName(vtdrwrBankName);
		 vtrust.setVtisseDt(vtisseDt);
		 vtrust.setVtdueDt(vtdueDt);
		 vtrust.setVtaccptrDate(vtaccptrDate);
		 vtrust.setVtTranSfer(vtTranSfer);
		 vtrust.setEndorserInfo(endorserInfo);
		 vtrust.setId(id);
		 if(draftMedia!=null && draftMedia.equals("1")){//纸票为【付款人】，电票为【承兑人】    1-纸票   2-电票
			 vtrust.setVtaccptrBankAccount(pyerBankNo);
			 vtrust.setVtaccptrBankAddr(pyerBankAddr);
		 }else{
			 vtrust.setVtaccptrBankAccount(vtaccptrBankAccount);
			 vtrust.setVtaccptrBankAddr(vtaccptrBankAddr); 
		 }
		 
		 //承兑行总行
		 String acptBankNo = vtrust.getVtaccptrBankAccount();
		 Map cpes = blackListManageService.queryCpesMember(acptBankNo);
		if(cpes != null){
			vtrust.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
			String memberName = (String) cpes.get("memberName");//总行行名
			vtrust.setAcptHeadBankName(memberName);//总行行名
		}
		 
		 vtrust.setDrwrGuarntrNm(drwrGuarntrNm);
		 vtrust.setDrwrGuarntrAddr(drwrGuarntrAddr);
		 vtrust.setDrwrGuarntrDt(drwrGuarntrDt);
		 vtrust.setAccptrGuarntrNm(accptrGuarntrNm);
		 vtrust.setAccptrGuarntrAddr(accptrGuarntrAddr);
		 vtrust.setAccptrGuarntrDt(accptrGuarntrDt);
		 vtrust.setBillPosition(billPosition);
		 vtrust.setBillPositionAddr(billPositionAddr);
		 vtrust.setRemarks(remarks);
		 vtrust.setVtSource(PoolComm.SOUR_EBK);
//		 RunState runState=runstateService.getSysRunState();
		 vtrust.setVtDate(new Date());
		 vtrust.setContractNo(contractNo);
		 vtrust.setAcceptanceAgreeNo(acceptanceAgreeNo);
		 vtrust.setVtStatus(PoolComm.DS_00);//初始化状态
		 vtrust.setPlTm(new Date());
		 vtrust.setLastOperTm(new Date());
		 vtrust.setLastOperName("票据信息登记接口落库");
		 //校验黑灰名单
		PoolBillInfo billInfo = new PoolBillInfo();
		
		billInfo.setBeginRangeNo(startBillNo);
		billInfo.setEndRangeNo(endBillNo);
		billInfo.setDraftSource(billSource);
		billInfo.setSplitFlag(splitFlag);
		billInfo.setSBillNo(getStringVal(map.get("BILL_INFO_ARRAY.BILL_NO")));//票号
		billInfo.setSBillType(getStringVal(map.get("BILL_INFO_ARRAY.BILL_CLASS")));//票据类型
		billInfo.setSAcceptor(getStringVal(map.get("BILL_INFO_ARRAY.ACCEPTOR_NAME")));//承兑人名称
		if(draftMedia!=null && draftMedia.equals("1")){//纸票为【付款人】，电票为【承兑人】    1-纸票   2-电票
			billInfo.setSAcceptorBankCode(pyerBankNo);//承兑人开户行行名   纸票用付款行
		}else{
			billInfo.setSAcceptorBankCode(getStringVal(map.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK")));//承兑人开户行行名
		}
		//承兑行总行
		String acptBankNo2 = billInfo.getSAcceptorBankCode();
		Map cpes2 = blackListManageService.queryCpesMember(acptBankNo2);
		if(cpes2 != null){
			billInfo.setAcptHeadBankNo((String)cpes2.get("totalBankNo"));//总行行号
			String transBrchClass = (String) cpes2.get("transBrchClass");
			String memberName = (String) cpes2.get("memberName");//总行行名
			billInfo.setAcptHeadBankName(memberName);//总行行名
			if(transBrchClass.equals("301")){//财务公司行号
				billInfo.setCpFlag("1");
			}
		}
		
		billInfo.setSAcceptorBankName(getStringVal(map.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));//承兑人开户行行名
		billInfo.setSAcceptorAccount(getStringVal(map.get("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO")));//承兑人帐号
		billInfo.setSIssuerName(getStringVal(map.get("BILL_INFO_ARRAY.BILL_NAME")));//出票人名称
		billInfo.setDDueDt(getDateVal(map.get("BILL_INFO_ARRAY.EXPIRY_DATE")));//到期日
		billInfo.setSBanEndrsmtFlag(getStringVal(map.get("BILL_INFO_ARRAY.TRANSFER_FLAG")));//不得转让标识
		billInfo.setFBillAmount(getBigDecimalVal(map.get("BILL_INFO_ARRAY.BILL_AMT")));//票面金额
		billInfo.setSBillMedia("1");//纸票
		
		
		//财票改造开关
		String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//在线业务总开关 
		
		if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){			
			billInfo = blackListManageService.txBlacklistCheck(billInfo, clientNO);
		}else{
			billInfo = blackListManageService.txBlacklistAndRiskCheck(billInfo, clientNO);
		}
		
		
		vtrust.setRickLevel(billInfo.getRickLevel());//风险等级
		vtrust.setBtFlag(billInfo.getBlackFlag());//黑名单标志
		
		return vtrust;
	}
	
	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

	
}
