 package com.mingtech.application.pool.bank.countersys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetFactory;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PlFeeList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 纸票入池申请接口（柜面）
 * @author wu fengjun
 * @data 2019-06-10
 */
public class GM005CounterHandler  extends PJCHandlerAdapter{

	private static final Logger logger = Logger.getLogger(GM005CounterHandler.class);
	
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolVtrustService poolVtrustService;
	@Autowired
	private DraftPoolInService draftPoolInService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private AssetRegisterService assetRegisterService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private AssetTypeManageService assetTypeManageService;
	@Autowired
	private BlackListManageService blackListManageService;

	/**
	 * （0）将传入数据存入虚拟表中
	 * （1）根据票号及入池批次号从虚拟表中查询待入池的高风险票据，占用保贴额度
	 * （2）调用核心入池记账接口（批量接口，批量成功失败）
	 * （3）记账成功后将虚拟票据存到poolIn表、大票表、pl_pool表
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		String apId ="";
		try {
			/*
			 * （0）柜面传入的数据整理
			 */
			logger.info("GM005:（0）柜面传入的数据整理");
			String clientNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));			
			String inBatchNo = getStringVal(request.getBody().get("INPOOL_BATCH_NO"));
			String FeeDecrease = getStringVal(request.getBody().get("FEE_DECREASE"));//减免金额  
			String realCharAmt = getStringVal(request.getBody().get("REAL_CHAR_AMT"));//实收金额  

			List details = request.getDetails();			
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			
			PoolVtrustBeanQuery queryBean = new PoolVtrustBeanQuery();
			queryBean.setInBatchNo(inBatchNo);
			List<PoolVtrust> bills =  poolVtrustService.queryPoolVtrustList(queryBean);
			
			/*
			 *（1）入池票据校验 
			 */
			logger.info("GM005:（1）入池票据校验 ");
			if(bills==null || bills.size()<1){								
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("无入池票据,入池失败!");
				logger.info(ret.getRET_MSG());
				response.setRet(ret);
				return response;
			}
			

			/*
			 * （2）收费校验
			 */
			logger.info("GM005:（2）收费校验 ");
			BigDecimal fee1 = new BigDecimal(FeeDecrease);//减免金额
			BigDecimal fee2 = new BigDecimal(realCharAmt);//实收金额
			BigDecimal fee = fee1.add(fee2);
			if(PoolComm.SFMS_01.equals(dto.getFeeType())&&(fee.compareTo(BigDecimal.ZERO)>0)){//年费
				if(pedProtocolService.isPaid(dto)){//年费已收
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据池【"+dto.getPoolAgreement()+"】已收过年费,不允许重复收费!");
					logger.info(ret.getRET_MSG());
					response.setRet(ret);
					return response;
				}
			}
			

			/*
			 * （3）将传入数据存入虚拟表中
			 */
			logger.info("GM005:（3）将传入数据存入虚拟表中 ");
			if(details!=null && details.size()>0){		
				
				ret = this.vtrustStore(details, clientNo, bpsNo);	
				
				if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
					logger.info(ret.getRET_MSG());
					response.setRet(ret);
					return response;
				}
			}
			
			/*
			 * （4）根据票号及入池批次号占用额度系统额度
			 */
			logger.info("GM005:（4）根据票号及入池批次号占用额度系统额度 ");
			bills =  poolVtrustService.queryPoolVtrustList(queryBean);
			List<String> sucessList = new ArrayList<String>();//用来记录占用成功的票号
			
			
			//如果入池申请的票，有已经在池的票号，则反馈核心
			if(null != bills && bills.size()>0){
				for(PoolVtrust pv : bills){
					PoolBillInfo bill = draftPoolQueryService.queryObj(pv.getVtNb(),"0","0");
					if(null !=bill && StringUtil.isNotBlank(bill.getSDealStatus())){
						if(bill.getSDealStatus().equals(PoolComm.DS_01)||//入池处理中
								bill.getSDealStatus().equals(PoolComm.DS_02)||//已入池
								bill.getSDealStatus().equals(PoolComm.DS_03)||//出池处理中
								bill.getSDealStatus().equals(PoolComm.DS_05)||//签收处理中 
								bill.getSDealStatus().equals(PoolComm.DS_06)){//到期处理中
							
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("票据【"+pv.getVtNb()+"】在票据池系统已存在有效的数据，不允许入池，请确认!");
							logger.info(ret.getRET_MSG());
							response.setRet(ret);
							return response;
							
						}
					}
				}
			}
			
			
			String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//财票改造开关 
			if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){
				 
				sucessList = draftPoolInService.txMisCreditOccupy(bills, clientNo, dto);
			
			}else{
				
				sucessList = this.doPJE012Process(bills, clientNo, dto);
			}
			
			/*
			 * （5）调用核心入池记账接口（批量接口，批量成功失败）
			 */
			logger.info("GM005:（5）调用核心入池记账接口（批量接口，批量成功失败）");
			bills =  poolVtrustService.queryPoolVtrustList(queryBean);
			response = this.doAccountProcess(bills, clientNo, dto, request);
			
			
			/*
			 * （6）如果记账失败处理释放额度系统的额度占用
			 */
			logger.info("GM005:（6）如果记账失败处理释放额度系统的额度占用");
			String accountResult = response.getRet().getRET_CODE();
			
			if(Constants.TX_FAIL_CODE.equals(accountResult)){
				if(bills!=null && bills.size()>0){						
					this.doFailProcess(sucessList);
				}
			}else{
				try {					
					/*
					 * 锁AssetPool表
					 */
					AssetPool pool = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
					apId = pool.getApId();
					boolean isLockedSucss = pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
					if(!isLockedSucss){//加锁失败
						throw new Exception("票据池纸票入池成功，加锁失败，不做入池回滚动作");
					}
					/*
					 * 额度重新计算
					 */
					try {
						financialService.txBailChangeAndCrdtCalculation(dto);
					} catch (Exception e) {
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG(e.getMessage());
						response.setRet(ret);
//						return response;
					}
					
					/*
					 * 解锁AssetPool表，并重新计算该表数据
					 */
					pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
				
				} catch (Exception e) {
					logger.info("票据池纸票入池成功，加锁失败，不做入池回滚动作");
				}
			}
			
		}catch(Exception e){
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("入池失败[" + e.getMessage() + "]");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
			return response;
		}
		logger.info("GM005:逻辑处理完毕");
		return response;
		
	}
	
	/**
	 * 虚拟票据数据处理
	 * @author Ju Nana
	 * @param map
	 * @return
	 * @throws Exception
	 * @date 2019-7-7上午1:25:41
	 */
	public PoolVtrust getPoolVtrust(Map map) throws Exception{
		//根据票号查询虚拟表是否存在
		String billNo = getStringVal(map.get("BILL_NO"));//票号
		
		PoolVtrustBeanQuery queryBean = new PoolVtrustBeanQuery();
		queryBean.setVtNb(billNo);
		PoolVtrust vtrust =  poolVtrustService.queryPoolVtrust(queryBean);
		
		vtrust.setVtNb(billNo);
		vtrust.setVtDraftMedia("1");//纸票
		vtrust.setVtType(getStringVal(map.get("BILL_CLASS")));//票据种类        
		vtrust.setVtaccptrName(getStringVal(map.get("ACCEPTOR_NAME")));//承兑人名称      
		vtrust.setVtaccptrBankName(getStringVal(map.get("ACCEPTOR_OPENBANK_NAME")));//承兑人开户行行名
		vtrust.setVtaccptrAccount(getStringVal(map.get("ACCEPTOR_ACCT_NO")));//承兑人帐号      
		vtrust.setVtpyeeName(getStringVal(map.get("PAYEE_NAME")));//收款人名称      
		vtrust.setVtpyeeBankAccount(getStringVal(map.get("PAYEE_OPEN_BRANCH")));//收款人开户行行号
		vtrust.setVtpyeeBankName(getStringVal(map.get("PAYEE_OPENBANK_NAME")));//收款人开户行行名
		vtrust.setVtpyeeAccount(getStringVal(map.get("PAYEE_ACCT_NO")));//收款人账号

		vtrust.setVtisseAmt(getBigDecimalVal(map.get("BILL_AMT")));//票据金额
		vtrust.setVtdrwrName(getStringVal(map.get("BILL_NAME")));//出票人名称
		vtrust.setVtdrwrAccount(getStringVal(map.get("BILL_ACCT_NO")));//出票人账号      
		vtrust.setVtdrwrBankNumber(getStringVal(map.get("REMITTER_OPEN_BANK")));//出票人开户行行号
		vtrust.setVtdrwrBankName(getStringVal(map.get("BILL_OPENBANK_NAME")));//出票人开户行行名
		vtrust.setVtisseDt(getDateVal(map.get("DRAW_DATE")));//出票日          
		vtrust.setVtdueDt(getDateVal(map.get("EXPIRY_DATE")));//到期日          
		vtrust.setVtaccptrDate(getDateVal(map.get("ACCE_DATE")));//承兑日期       
		vtrust.setVtTranSfer(PoolComm.NOT_ATTRON_FLAG_YES);//不得转让标识    虚拟表落库 0不可转让  1可转让 与大票表相反
		
		vtrust.setTranRriorName(getStringVal(map.get("TRAN_RRIOR_NAME")));//票据直接前手
		vtrust.setDrwrGuarntrNm(getStringVal(map.get("DRAWER_GUARANTOR_NAME")));//出票保证人名称  
		vtrust.setDrwrGuarntrAddr(getStringVal(map.get("DRAWER_GUARANTOR_ADDRESS")));//出票保证人地址  
		vtrust.setDrwrGuarntrDt(getStringVal(map.get("DRAWER_GUARANTEE_DATE")));//出票保证时间    
		vtrust.setAccptrGuarntrNm(getStringVal(map.get("ACCEPTANCE_GUARANTOR_NAME")));//承兑保证人名称  
		vtrust.setAccptrGuarntrAddr(getStringVal(map.get("ACCEPTANCE_GUARANTOR_ADDRESS")));//承兑保证人地址  
		vtrust.setAccptrGuarntrDt(getStringVal(map.get("ACCEPTANCE_GUARANTEE_DATE")));//承兑保证时间    
		vtrust.setRemarks(getStringVal(map.get("REMARK")));//备注            
		vtrust.setContractNo(getStringVal(map.get("CONTRACT_NO")));//交易合同号      
		vtrust.setAcceptanceAgreeNo(getStringVal(map.get("ACCEPTANCE_AGREE_NO")));//承兑协议编号    
		if(vtrust.getVtDraftMedia()!=null && vtrust.getVtDraftMedia().equals("1")){//纸票为【付款人】，电票为【承兑人】    1-纸票   2-电票
			 vtrust.setVtaccptrBankAccount(getStringVal(map.get("PAYER_OPEN_BANK")));
			 vtrust.setVtaccptrBankAddr(getStringVal(map.get("PAYER_OPEN_BANK_ADDR")));
		 }else{
			 vtrust.setVtaccptrBankAccount(getStringVal(map.get("ACCEPTOR_OPEN_BANK")));
			 vtrust.setVtaccptrBankAddr(getStringVal(map.get("ACCEPTOR_OPEN_BANK_ADDR"))); 
		 }
		 //承兑行总行
		String acptBankNo = vtrust.getVtaccptrBankAccount();
		Map cpes = blackListManageService.queryCpesMember(acptBankNo);
		if(cpes != null){
			vtrust.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
			String memberName = (String) cpes.get("memberName");//总行行名
			vtrust.setAcptHeadBankName(memberName);//总行行名
		}
		 
		vtrust.setBillPosition("01");//本行持有
		return vtrust;
		
	}
	
	/**
	 * poolIn数据整理
	 * @author Ju Nana
	 * @param vtrust
	 * @param custNO
	 * @param bspNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-7上午1:26:09
	 */
	public DraftPoolIn getPoolIn(PoolVtrust vtrust ,String custNO, String bspNo ,String guaranteeNo) throws Exception{
		
		logger.info("柜面入池PoolIn表数据处理......");
		
		DraftPoolIn poolIn = draftPoolInService.getDraftPoolInByDraftNb(vtrust.getVtNb(),"0","0");
		if(poolIn == null ){
			poolIn = new DraftPoolIn();
		}else{
			draftPoolInService.txDelete(poolIn);
			poolIn = new DraftPoolIn();
		}
		
		poolIn.setPlDraftNb(vtrust.getVtNb());//票据号码        
		poolIn.setBeginRangeNo("0");//票据号码  起      
		poolIn.setEndRangeNo("0");//票据号码        止
		poolIn.setPlDraftMedia(vtrust.getVtDraftMedia());//票据介质        
		poolIn.setPlDraftType(vtrust.getVtType());//票据种类        
		poolIn.setPlAccptrNm(vtrust.getVtaccptrName());//承兑人名称      
		poolIn.setPlAccptrSvcrNm(vtrust.getVtaccptrBankName());//承兑人开户行行名
		poolIn.setPlAccptrSvcr(vtrust.getVtaccptrBankAccount());//付款人开户行行号
		poolIn.setAcptHeadBankNo(vtrust.getAcptHeadBankNo());//承兑人开户行总行
		poolIn.setAcptHeadBankName(vtrust.getAcptHeadBankName());//承兑人开户行名总行
		poolIn.setPlAccptrAddress(vtrust.getVtaccptrBankAddr());//付款人开户行地址
		poolIn.setPlAccptrId(vtrust.getVtaccptrAccount());//承兑人帐号      
		poolIn.setPlPyeeNm(vtrust.getVtpyeeName());//收款人名称      
		poolIn.setPlPyeeAcctSvcr(vtrust.getVtpyeeBankAccount());//收款人开户行行号
		poolIn.setPlPyeeAcctSvcrNm(vtrust.getVtpyeeBankName());//收款人开户行行名
		poolIn.setPlPyeeAcctId(vtrust.getVtpyeeAccount());//收款人账号      
		poolIn.setPlIsseAmt(vtrust.getVtisseAmt().setScale(2,BigDecimal.ROUND_HALF_UP));//票据金额
		poolIn.setPlDrwrNm(vtrust.getVtdrwrName());//出票人名称      
		poolIn.setPlDrwrAcctId(vtrust.getVtdrwrAccount());//出票人账号      
		poolIn.setPlDrwrAcctSvcr(vtrust.getVtdrwrBankNumber());//出票人开户行行号
		poolIn.setPlDrwrAcctSvcrNm(vtrust.getVtdrwrBankName());//出票人开户行行名
		poolIn.setPlIsseDt(vtrust.getVtisseDt());//出票日          
		poolIn.setPlDueDt(vtrust.getVtdueDt());//到期日
		
		poolIn.setPlReqTime(new Date());// 申请时间
		poolIn.setPlStatus(PoolComm.RC_05);// 
		poolIn.setPlTradeType("YW_01");// 代保管YW_02/票据池 YW_01 存单池YW_03
		poolIn.setCustNo(custNO);// 核心客户号
		poolIn.setPoolAgreement(bspNo);//票据池编号
		poolIn.setBlackFlag(vtrust.getBlackFlag());//是否在风险名单
		poolIn.setRickLevel(vtrust.getRickLevel());//高低风险等级
		poolIn.setBtFlag(vtrust.getBtFlag());//保贴额度
		poolIn.setGuaranteeNo(guaranteeNo);
		
		return poolIn;
		
	}
	
	/**
	 * plpoo表数据整理
	 * @author Ju Nana
	 * @param poolIn
	 * @return
	 * @throws Exception
	 * @date 2019-7-7上午1:26:27
	 */
	public DraftPool getDraftPool(DraftPoolIn poolIn, PedProtocolDto dto ,PoolBillInfo bill) throws Exception{
		
		logger.info("柜面入池DraftPool表数据处理......");
		
		DraftPool pool = AssetFactory.newDraftPool();
		BeanUtil.DraftPoolCopy(poolIn, pool);
		pool.setBeginRangeNo("0");
		pool.setEndRangeNo("0");
		pool.setStandardAmt(new BigDecimal(0.01));
		pool.setTradeAmt(poolIn.getPlIsseAmt());
		pool.setSplitFlag("0");
		
		pool.setRickLevel(bill.getRickLevel());
		pool.setBlackFlag(bill.getBlackFlag());
		pool.setPlTm(DateUtils.getCurrDate());
		pool.setAssetLimitTotal(poolIn.getPlIsseAmt());//衍生额度
		pool.setAssetLimitFree(poolIn.getPlIsseAmt());//可用额度
		pool.setAssetLimitUsed(new BigDecimal(0));//已用额度
		pool.setAssetLimitFrzd(new BigDecimal(0));//已冻结额度
		pool.initLimit();
		pool.setLastTransId(poolIn.getId());
		pool.setLastTransType("入池");
		pool.setBtFlag(poolIn.getBtFlag()); //保贴额度
		pool.setAssetStatus(PoolComm.DS_02);//已入池
		pool.setLastOperTm(new Date());
		pool.setLastOperName("柜面纸票入池");
		pool.setAssetCommId(poolIn.getCustNo());
		pool.setBranchId(dto.getOfficeNet());
		String type = null;//额度类型：低风险额度-ED_10  高风险额度-ED_20
		if(PoolComm.LOW_RISK.equals(bill.getRickLevel())){//低风险票据
			type = PoolComm.ED_PJC;
			/**
			 * 查询并保存客户assetType消息
			 */
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(type ,pool.getPoolAgreement());
			pool.setAt(at.getId());//关联资产对象
		}else if(PoolComm.HIGH_RISK.equals(bill.getRickLevel())){//高风险票据
			type = PoolComm.ED_PJC_01;
			/**
			 * 查询并保存客户assetType消息
			 */
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(type, pool.getPoolAgreement());
			pool.setAt(at.getId());//关联资产对象
		}else{
			type = PoolComm.ED_PJC;
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(type, pool.getPoolAgreement());
			pool.setAt(at.getId());//关联资产对象
			pool.setAssetLimitTotal(new BigDecimal(0));//衍生额度
			pool.setAssetLimitFree(new BigDecimal(0));//可用额度
			pool.setAssetLimitUsed(new BigDecimal(0));//已用额度
			pool.setAssetLimitFrzd(new BigDecimal(0));//已冻结额度
		}

		 pool.setPoolAgreement(poolIn.getPoolAgreement());//票据池编号
		 pool.setPoolBillInfo(bill);
//		 poolCommonService.txBuildAllLimit(dto);
		 
		//顺延日期 
		Long deferDays = assetTypeManageService.queryDelayDays(pool.getRickLevel(), pool.getPlDueDt());
		pool.setDelayDays(deferDays.intValue());
			
		return pool;
		
	}
	
	/**
	 * 大票表数据整理
	 * @author Ju Nana
	 * @param vtrust
	 * @param pedDto
	 * @return
	 * @throws Exception
	 * @date 2019-7-7上午1:26:54
	 */
	public PoolBillInfo getPoolBillInfo(PoolVtrust vtrust,  PedProtocolDto pedDto) throws Exception{
		
		logger.info("柜面入池PoolBillInfo表数据处理......");
		PoolBillInfo billInfo = null;	
		billInfo = draftPoolQueryService.queryObj(vtrust.getVtNb(),"0","0");
		if(billInfo == null){
			billInfo = new PoolBillInfo();
			billInfo.setSBillNo(vtrust.getVtNb());
			billInfo.setSBillMedia(vtrust.getVtDraftMedia());
			billInfo.setSBillType(vtrust.getVtType());//票据种类
			billInfo.setSAcceptor(vtrust.getVtaccptrName());//承兑人名称
			billInfo.setSAcceptorBankName(vtrust.getVtaccptrBankName());//承兑人开户行行名
			billInfo.setSAcceptorBankCode(vtrust.getVtaccptrBankAccount());//承兑人开户行行号
			billInfo.setSAcceptorAddress(vtrust.getVtaccptrBankAddr());//承兑人开户行地址
			billInfo.setSAcceptorAccount(vtrust.getVtaccptrAccount());//承兑人帐号
			billInfo.setSPayeeName(vtrust.getVtpyeeName());//收款人名称
			billInfo.setSPayeeBankCode(vtrust.getVtpyeeBankAccount());//收款人开户行行号
			billInfo.setSPayeeBankName(vtrust.getVtpyeeBankName());//收款人开户行行名
			billInfo.setSPayeeAccount(vtrust.getVtpyeeAccount());//收款人账号
			billInfo.setFBillAmount(vtrust.getVtisseAmt().setScale(2,BigDecimal.ROUND_HALF_UP));//票据金额
			billInfo.setSIssuerName(vtrust.getVtdrwrName());//出票人名称
			billInfo.setSIssuerAccount(vtrust.getVtdrwrAccount());//出票人账号
			billInfo.setSIssuerBankCode(vtrust.getVtdrwrBankNumber());//出票人开户行行号
			billInfo.setSIssuerBankName(vtrust.getVtdrwrBankName());//出票人开户行行名
			billInfo.setDIssueDt(vtrust.getVtisseDt());//出票日
			billInfo.setDDueDt(vtrust.getVtdueDt());//到期日
			billInfo.setSBanEndrsmtFlag(PoolComm.NOT_ATTRON_FLAG_NO);//不得转让标识
			if(vtrust.getDrwrGuarntrDt() != null && !vtrust.getDrwrGuarntrDt().equals("")){
				billInfo.setDrwrGuarnteeDt(DateUtils.StringToDate(vtrust.getDrwrGuarntrDt(),"yyyyMMdd"));//出票保证时间
			}
			billInfo.setSContractNo(vtrust.getContractNo());//交易合同号
			billInfo.setSAcceptorProto(vtrust.getAcceptanceAgreeNo());//承兑协议编号
			billInfo.setSBranchId(pedDto.getOfficeNet());//存储网点信息 用于配置权限
			billInfo.setPoolAgreement(pedDto.getPoolAgreement());//票据池编号
			billInfo.setRickLevel(vtrust.getRickLevel());
			billInfo.setBlackFlag(vtrust.getBlackFlag());
			billInfo.setSDealStatus(PoolComm.DS_02);//已入池
			billInfo.setEbkLock(PoolComm.EBKLOCK_02);
			billInfo.setDiscBillId(vtrust.getVtNb());//纸票票号赋值入票据id
		}else {
			billInfo.setSAcceptor(vtrust.getVtaccptrName());//承兑人名称
			billInfo.setSAcceptorBankName(vtrust.getVtaccptrBankName());//承兑人开户行行名
			billInfo.setSAcceptorBankCode(vtrust.getVtaccptrBankAccount());//承兑人开户行行号
			billInfo.setSAcceptorAddress(vtrust.getVtaccptrBankAddr());//承兑人开户行地址
			billInfo.setSAcceptorAccount(vtrust.getVtaccptrAccount());//承兑人帐号
			billInfo.setFBillAmount(vtrust.getVtisseAmt().setScale(2,BigDecimal.ROUND_HALF_UP));//票据金额
			billInfo.setSIssuerName(vtrust.getVtdrwrName());//出票人名称
			billInfo.setSIssuerAccount(vtrust.getVtdrwrAccount());//出票人账号
			billInfo.setSIssuerBankCode(vtrust.getVtdrwrBankNumber());//出票人开户行行号
			billInfo.setSIssuerBankName(vtrust.getVtdrwrBankName());//出票人开户行行名
			billInfo.setSBanEndrsmtFlag(PoolComm.NOT_ATTRON_FLAG_NO);//不得转让标识
			if(vtrust.getDrwrGuarntrDt() != null && !vtrust.getDrwrGuarntrDt().equals("")){
				billInfo.setDrwrGuarnteeDt(DateUtils.StringToDate(vtrust.getDrwrGuarntrDt(),"yyyyMMdd"));//出票保证时间
			}
			billInfo.setSContractNo(vtrust.getContractNo());//交易合同号
			billInfo.setSAcceptorProto(vtrust.getAcceptanceAgreeNo());//承兑协议编号
			billInfo.setDIssueDt(vtrust.getVtisseDt());//出票日
			billInfo.setDDueDt(vtrust.getVtdueDt());//到期日
			billInfo.setSPayeeName(vtrust.getVtpyeeName());//收款人名称
			billInfo.setSPayeeBankCode(vtrust.getVtpyeeBankAccount());//收款人开户行行号
			billInfo.setSPayeeBankName(vtrust.getVtpyeeBankName());//收款人开户行行名
			billInfo.setSPayeeAccount(vtrust.getVtpyeeAccount());//收款人账号
			billInfo.setSBranchId(pedDto.getOfficeNet());//存储网点信息 用于配置权限
			billInfo.setPoolAgreement(pedDto.getPoolAgreement());//票据池编号
			billInfo.setSDealStatus(PoolComm.DS_02);//已入池
			billInfo.setRickLevel(vtrust.getRickLevel());
			billInfo.setBlackFlag(vtrust.getBlackFlag());
			billInfo.setEbkLock(PoolComm.EBKLOCK_02);
			billInfo.setDiscBillId(vtrust.getVtNb());//纸票票号赋值入票据id
		}

		billInfo.setTradeAmt(billInfo.getFBillAmount());
				
		billInfo.setLastOperTm(new Date());
		billInfo.setLastOperName("柜面纸票入池");
		//承兑行总行
		String acptBankNo = billInfo.getSAcceptorBankCode();
		Map cpes = blackListManageService.queryCpesMember(acptBankNo);
		if(cpes != null){
			billInfo.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
			String transBrchClass = (String) cpes.get("transBrchClass");
			String memberName = (String) cpes.get("memberName");//总行行名
			billInfo.setAcptHeadBankName(memberName);//总行行名
			if(transBrchClass.equals("301")){//财务公司行号
				billInfo.setCpFlag("1");
			}
		}
		
		return billInfo;
	}

	/**
	 * vtrust表落库处理
	 * @param details
	 * @param clientNo
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2019-11-18上午9:12:08
	 */
	private Ret vtrustStore(List details,String clientNo,String bpsNo) throws Exception{
		
		logger.info("柜面入池vtrust表落库处理，开始....");
		
		Ret ret = new Ret();
		List<PoolVtrust> vtrustList = new ArrayList<PoolVtrust>();//存放虚拟对象——初次保存
		for (int i = 0; i < details.size(); i++) {
			Map map = (Map) details.get(i);
			
			/*
			 * 重复票据校验
			 */

			PoolBillInfo bill = draftPoolQueryService.queryObj(getStringVal(map.get("BILL_NO")),"0","0");
			if(null !=bill && StringUtil.isNotBlank(bill.getSDealStatus())){
				if(bill.getSDealStatus().equals(PoolComm.DS_01)||//入池处理中
						bill.getSDealStatus().equals(PoolComm.DS_02)||//已入池
						bill.getSDealStatus().equals(PoolComm.DS_03)||//出池处理中
						bill.getSDealStatus().equals(PoolComm.DS_05)||//签收处理中 
						bill.getSDealStatus().equals(PoolComm.DS_06)){//到期处理中
					
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票号"+bill.getSBillNo()+"已在票据池系统存在,不允许重复入池!");
					return ret;
					
				}
			}
			
			/*
			 * vtrust表票据整理
			 */
			PoolVtrustBeanQuery query = new PoolVtrustBeanQuery();
			query.setVtNb(getStringVal(map.get("BILL_NO")));
			PoolVtrust vtrust = (PoolVtrust) poolVtrustService.queryPoolVtrust(query);
			
			vtrust = this.getPoolVtrust(map);
			vtrust.setVtEntpNo(clientNo);
			vtrust.setBpsNo(bpsNo);
			vtrustList.add(vtrust);
		}
		
		pedProtocolService.txStoreAll(vtrustList);
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		return ret;
	}
	
	/**
	 * 占用额度系统额度
	 * @param bills
	 * @param clientNo
	 * @param dto
	 * @author Ju Nana
	 * @date 2019-11-18上午9:27:54
	 */
	private List<String> doPJE012Process(List<PoolVtrust> bills,String clientNo,PedProtocolDto dto)throws Exception{
		List<String> sucessList = new ArrayList<String>();
		
		for(PoolVtrust vtrust :bills){
			
			if(vtrust.getBtFlag() != PoolComm.SP_01){
				
				String media = vtrust.getVtDraftMedia();//票据介质
				String type = vtrust.getVtType();//票据类型
				String billType ="";//票据种类  01 纸质银票 02 纸质商票 03电子银票  04电子商票
				if(PoolComm.BILL_MEDIA_PAPERY.equals(media) && PoolComm.BILL_TYPE_BANK.equals(type)){//纸质银票
					billType = "01";
				}
				if(PoolComm.BILL_MEDIA_PAPERY.equals(media)&&PoolComm.BILL_TYPE_BUSI.equals(type)){//纸质商票
					billType = "02";
				}
				if(PoolComm.BILL_MEDIA_ELECTRONICAL.equals(media)&&PoolComm.BILL_TYPE_BANK.equals(type)){//电子银票
					billType = "03";
				}
				if(PoolComm.BILL_MEDIA_ELECTRONICAL.equals(media)&&PoolComm.BILL_TYPE_BUSI.equals(type)){//电子商票
					billType = "04";
				}
				
				Map resuMap = new HashMap();
				List<Map> reqList = new ArrayList<Map>();//实际为单条
				CreditTransNotes creditNotes = new CreditTransNotes();
				
				resuMap.put("billNo", vtrust.getVtNb());//票号                    
				resuMap.put("billsum", vtrust.getVtisseAmt());//票面金额               
				resuMap.put("currency", "156");//币种:人民币                  
				resuMap.put("billType", billType);//票据种类              
				resuMap.put("billBusinessType", "02");//票据业务类型  01 贴现 02 质押  03 转贴现
				
				resuMap.put("customerId", "");//承兑人核心客户号    
				resuMap.put("bankId", vtrust.getVtaccptrBankAccount());//承兑人二代支付系统行号  
				resuMap.put("customerName", vtrust.getVtaccptrName());//承兑人名称        
				resuMap.put("execNominalAmount", vtrust.getVtisseAmt());//占用名义金额 
				
				BigDecimal amt = BigDecimal.ZERO;//占用敞口金额:银票输0；商票输票面金额
				if(PoolComm.BILL_TYPE_BUSI.equals(type)){//商票
					amt = vtrust.getVtisseAmt();
				}
				resuMap.put("execExposureAmount", amt);//占用敞口金额
				
				reqList.add(resuMap);
				creditNotes.setReqList(reqList);//上传文件
				
				
				try {
					ReturnMessageNew response1 = poolCreditClientService.txPJE012(creditNotes);
					if (response1.isTxSuccess()) {
						
						
						/*
						 * 额度系统额度占用记录表记录
						 */
						logger.info("柜面入池PedGuaranteeCredit额度系统额度占用记录表数据处理......");
						
						PoolQueryBean pBean = new PoolQueryBean();
						pBean.setProtocolNo(dto.getPoolAgreement());
						pBean.setBillNo(vtrust.getVtNb());
						
						/********************融合改造新增 start******************************/
						pBean.setBeginRangeNo(vtrust.getBeginRangeNo());
						pBean.setEndRangeNo(vtrust.getEndRangeNo());
						/********************融合改造新增 end******************************/
						
						PedGuaranteeCredit pedCredit = poolCreditProductService.queryByBean(pBean);
						
						if(pedCredit == null ){
							pedCredit = new PedGuaranteeCredit();
						}
						vtrust.setBtFlag(PoolComm.SP_01);//占用成功
						pedCredit.setBpsNo(dto.getPoolAgreement());
						pedCredit.setBpsName(dto.getPoolName());
						if(dto.getIsGroup().equals(PoolComm.YES)){
							//集团户
							
							ProListQueryBean quer = new ProListQueryBean();
							quer.setBpsNo(dto.getPoolAgreement());
							quer.setCustNo(clientNo);
							PedProtocolList ped = pedProtocolService.queryProtocolListByQueryBean(quer);
							pedCredit.setCustName(ped.getCustName());
							vtrust.setVtEntpName(ped.getCustName());
						}else {
							pedCredit.setCustName(dto.getCustname());
							vtrust.setVtEntpName(dto.getCustname());
						}
						pedCredit.setCustNo(clientNo);
						pedCredit.setBillNo(vtrust.getVtNb());
						pedCredit.setBillType(vtrust.getVtType());
						pedCredit.setBillAmt(vtrust.getVtisseAmt());
						pedCredit.setStatus(PoolComm.SP_01);
						pedCredit.setAcceptor(vtrust.getVtaccptrName());
						pedCredit.setCreateTime(new Date());
						pedCredit.setIsGroup(dto.getIsGroup());
						draftPoolInService.txStore(pedCredit);
						draftPoolInService.txStore(vtrust);
						
						//记录占用成功的票号
						sucessList.add(vtrust.getVtNb());
						logger.info("票据【"+vtrust.getVtNb()+"】额度系统额度占用成功！");
					}else{
						vtrust.setRickLevel(PoolComm.NOTIN_RISK);
					}
				} catch (Exception e) {//保贴额度占用失败
					vtrust.setRickLevel(PoolComm.NOTIN_RISK);
					draftPoolInService.txStore(vtrust);
					logger.info("票据【"+vtrust.getVtNb()+"】额度系统额度占用失败！"+e);
				}
			}
		}
		return sucessList;
		
	}
	
	/**
	 * 核心记账处理
	 * @param bills
	 * @param clientNo
	 * @param dto
	 * @return
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2019-11-18上午9:43:21
	 */
	private ReturnMessageNew doAccountProcess(List<PoolVtrust> bills,String clientNo,PedProtocolDto dto,ReturnMessageNew request) throws Exception{
		
		ReturnMessageNew response = new ReturnMessageNew();
		
		/*
		 * （1）传入数据整理
		 */
		String user = getStringVal(request.getSysHead().get("USER_ID"));//柜员号
		String branch = getStringVal(request.getSysHead().get("BRANCH_ID"));//机构号

		String realCharAmt = getStringVal(request.getBody().get("REAL_CHAR_AMT"));//实收金额  
		String feeRateCode = getStringVal(request.getBody().get("FEE_RATE_CODE"));//费率代码  
		String deductionAcctNo = getStringVal(request.getBody().get("DEDUCTION_ACCT_NO"));//扣款账号  
		String inpoolBatchNo = getStringVal(request.getBody().get("INPOOL_BATCH_NO"));//入池批次号 
		String branchId = getStringVal(request.getSysHead().get("BRANCH_ID"));//机构号 
		String acctCode = getStringVal(request.getBody().get("ACCT_CODE"));//款项代码
		
		List<PoolVtrust> vtrustList2 = new ArrayList<PoolVtrust>();//存放虚拟对象——再次保存
		List<DraftPoolIn> poolInList = new ArrayList<DraftPoolIn>();//存放入池对象
		List<DraftPool> poolList = new ArrayList<DraftPool>();//存放资产对象
		List<PoolBillInfo> billList = new ArrayList<PoolBillInfo>();//大票表对象
		Ret ret = new Ret();
		
		String bpsNo = dto.getPoolAgreement();
		
		/*
		 * （2）记账数据整理
		 */
		CoreTransNotes transNotes = new CoreTransNotes();
		String path = poolBatchNoUtils.txGetBatchNo("/BPS/BAS_TRFSND", 4);
		transNotes.setUser(user);
		transNotes.setBeatch(branch);
		transNotes.setDeductionAcctNo(deductionAcctNo);
		transNotes.setFeeRateCode(feeRateCode);
		transNotes.setAccSub(acctCode);
		transNotes.setBrcNo(dto.getCreditDeptNo()); //客户号对应的签约机构
		transNotes.setBpsNo(dto.getPoolAgreement());
		transNotes.setAccNo(dto.getPoolAccount());//结算账号

		if((PoolComm.SFMS_01).equals(dto.getFeeType()) && !pedProtocolService.isPaid(dto) ){//年费未缴
			transNotes.setFeeAmt(new BigDecimal(realCharAmt).setScale(2,BigDecimal.ROUND_HALF_UP)+"");
		}else {//年费已缴
			transNotes.setFeeAmt("");
		}
		if((PoolComm.SFMS_02).equals(dto.getFeeType())){//逐笔缴费
			transNotes.setFeeAmt(new BigDecimal(realCharAmt).setScale(2,BigDecimal.ROUND_HALF_UP)+"");
		}

		transNotes.setDateStr(DateUtils.toString(new Date(), "yyyyMMdd"));
		/*********************body字段处理**********************************/
		
		/*********************明细字段处理**********************************/
		ArrayList reList = new ArrayList();
		BigDecimal totalAmt = new BigDecimal("0");
		for (int i = 0 ; i < bills.size(); i++) {
			PoolVtrust vtrust = bills.get(i);
			totalAmt = totalAmt.add(vtrust.getVtisseAmt());
			Map map = new HashMap();
			map.put("IdxNo", i+1);
			map.put("UserID", vtrust.getVtNb());//票号
			map.put("AccNoD", inpoolBatchNo);//批号
			map.put("BrcNoSD", dto.getOfficeNet());//机构	纸票记账,记交易机构  签约时的签约机构
			map.put("FlgCR", "2");//抵押物类型
			map.put("SubStnD", "224");//担保品种类
			map.put("CcyD", "01");//币种
			map.put("AmtTran",vtrust.getVtisseAmt().setScale(2,BigDecimal.ROUND_HALF_UP));//票面金额
			map.put("AccSubD", clientNo);//客户号
			map.put("BnkPayee", "");//担保品种类
			map.put("IsChkNam", "0");//纸票
			//担保品编号
			String guaranteeNo = poolBatchNoUtils.txGetPoperCuarNoBySession("P",6);
			map.put("MemoD", guaranteeNo);//担保品编号
			map.put("AccNoH", dto.getContract());//担保合同号
			map.put("ClsNoH", DateUtils.toString(vtrust.getVtdueDt(),"yyyyMMdd"));//到期日
			reList.add(map);
			
			
			/*
			 * （3）将vtrust表中的数据集成到pooolIn、plPool、cdDraft表中
			 */
			DraftPoolIn poolIn = this.getPoolIn(vtrust, clientNo, bpsNo ,guaranteeNo);
			PoolBillInfo bill = this.getPoolBillInfo(vtrust, dto);
			bill.setRickLevel(poolIn.getRickLevel());
			DraftPool pool = this.getDraftPool(poolIn, dto ,bill);

			
			poolIn.setDraftPool(pool);//入池明细表关联draftpool
			poolIn .setBtFlag(PoolComm.SP_00);//更新pl_pool_in对象保贴额度占用标识+
			vtrust.setVtStatus(PoolComm.DS_02);
			vtrust.setLastOperName("柜面入池落库");
			vtrust.setLastOperTm(new Date());
			
			poolIn .setCreditObjType(vtrust.getCreditObjType());
			poolIn .setGuarantDiscName(vtrust.getGuarantDiscName());
			poolIn .setGuarantDiscNo(vtrust.getGuarantDiscNo());
			
			pool .setCreditObjType(vtrust.getCreditObjType());
			pool .setGuarantDiscName(vtrust.getGuarantDiscName());
			pool .setGuarantDiscNo(vtrust.getGuarantDiscNo());
			
			if(dto.getIsGroup().equals(PoolComm.NO)){//单户
				bill.setCustNo(clientNo);
				bill.setCustName(dto.getCustname());
				pool.setCustName(dto.getCustname());
				pool.setSocialCode(dto.getPlUSCC());
			}else{
				ProListQueryBean quer = new ProListQueryBean();
				quer.setBpsNo(bpsNo);
				quer.setCustNo(clientNo);
				PedProtocolList ped = pedProtocolService.queryProtocolListByQueryBean(quer);
				bill.setCustNo(clientNo);
				bill.setCustName(ped.getCustName());
				pool.setCustName(ped.getCustName());
				pool.setSocialCode(ped.getSocialCode());
			}
			//保存对象
			poolInList.add(poolIn);
			poolList.add(pool);
			billList.add(bill);
			vtrustList2.add(vtrust);
			
			
		}
		transNotes.setTotalNum(reList.size()+"");
		String str = poolBatchNoUtils.txGetFlowNo();
		transNotes.setDevSeqNo(str);//第三方流水号
		transNotes.setTotalAmt(totalAmt.setScale(2,BigDecimal.ROUND_HALF_UP)+"");
		transNotes.setList(reList);
		if ( reList.size() > 0 ){
			transNotes.setPath(path+".txt");
			transNotes.setRemark("1");
		}else {
			transNotes.setRemark("0");
		}
		transNotes.setiDTypeS("G");//业务种类
		transNotes.setFlgEnt("1");//收费标枳
		ReturnMessageNew resp = poolCoreService.txPledgeAccount(transNotes);
		if(resp.isTxSuccess()){		
			
			
			/*
			 * 收费信息记录并落库
			 */
			logger.info("柜面入池PlFeeList票据池服务费记录表数据处理......");
			String batch = this.doFeeList(clientNo, dto, request);
			
			/*
			 * （3）记账成功后将虚拟票据存到poolIn表、大票表、pl_pool表
			 */
			
			draftPoolInService.txStoreAll(billList);
			draftPoolInService.txStoreAll(vtrustList2);
			
			List<DraftPool> draftPoolList = new ArrayList<DraftPool>();
			List<DraftPoolIn> inlList = new ArrayList<DraftPoolIn>();
			
			for (int i = 0; i < poolList.size(); i++) {
				DraftPool pool = poolList.get(i);
				PoolVtrustBeanQuery query = new PoolVtrustBeanQuery();
				query.setVtNb(pool.getAssetNb());
				PoolVtrust vtrust = (PoolVtrust) poolVtrustService.queryPoolVtrust(query);
				pool.setFeeBatchNo(batch);
				pool.setRickLevel(vtrust.getRickLevel());
				pool.setAcptHeadBankNo(vtrust.getAcptHeadBankNo());
				pool.setAcptHeadBankName(vtrust.getAcptHeadBankName());
				pool.setPoperBeatch(branchId);
				pool.setOperatoUser(user);
				draftPoolList.add(pool);
				
			}
			for (int i = 0; i < poolInList.size(); i++){
				DraftPoolIn poolIn = poolInList.get(i);
				poolIn.setHostSeqNo(getStringVal(resp.getSysHead().get("SERV_SEQ_NO")));
				poolIn.setPlStatus(PoolComm.RC_05);
				inlList.add(poolIn);
				
			}
			pedProtocolService.txStoreAll(draftPoolList);
			pedProtocolService.txStoreAll(inlList);
			
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("核心入池记账成功！");

			/*
			 * (4)记账成功后资产登记
			 */
			response.getBody().put("HOST_SEQ_NO",resp.getSysHead().get("SERV_SEQ_NO"));
			for(DraftPool draftPool : draftPoolList ){	
				if(PoolComm.LOW_RISK.equals(draftPool.getRickLevel())||PoolComm.HIGH_RISK.equals(draftPool.getRickLevel())){					
					assetRegisterService.txBillAssetRegister(draftPool, dto);
				}
			}
			
			
		}else{
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("核心入池记账失败！"+resp.getSysHead().get("RET.RET_MSG"));
			logger.info(ret.getRET_MSG());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

		}
		
		response.setRet(ret);
		return response;
		
		
	}
	
	/**
	 * 记账失败，将占用额度系统的额度释放
	 * @param bills
	 * @author Ju Nana
	 * @date 2019-11-18上午11:06:44
	 */
	private void doFailProcess(List<String> bills)throws Exception{
		
		for(String billNo : bills){
			
			Map resuMap = new HashMap();
			List<Map> reqList = new ArrayList<Map>();//实际为单条
			CreditTransNotes creditNotes = new CreditTransNotes();
			
			resuMap.put("billNo", billNo);
			reqList.add(resuMap);
			creditNotes.setReqList(reqList);//上传文件
			
			ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
			
			if (!response1.isTxSuccess()) {
				
				logger.info("票号["+billNo+"]入池记账失败后额度系统额度释放失败！");
				throw new Exception("票号["+billNo+"]入池记账失败后额度系统额度释放失败！");
			}
		}
	}
	
	/**
	 * 收费记录
	 * @param clientNo
	 * @param dto
	 * @param request
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2019-11-19上午10:50:17
	 */
	private String doFeeList(String clientNo,PedProtocolDto dto,ReturnMessageNew request)throws Exception{
		
		String recvFeeAmt = getStringVal(request.getBody().get("RECV_FEE_AMT"));//应收金额
		String FeeDecrease = getStringVal(request.getBody().get("FEE_DECREASE"));//减免金额  
		String realCharAmt = getStringVal(request.getBody().get("REAL_CHAR_AMT"));//实收金额  
		String bpsNo  = dto.getPoolAgreement();
		String batch = "";
		if(dto.getFeeType().equals(PoolComm.SFMS_01)){//年费
			if(!pedProtocolService.isPaid(dto)){
				//服务到期日小于当前工作日  表示费用未交
				PlFeeList plFeeList = new PlFeeList();
				if(dto.getIsGroup().equals(PoolComm.YES)){//集团
					ProListQueryBean Bean = new ProListQueryBean();
					Bean.setBpsNo(bpsNo);
					Bean.setCustNo(clientNo);
					PedProtocolList protocol = pedProtocolService.queryProtocolListByQueryBean(Bean);//查询主户信息
					plFeeList.setCustName(protocol.getCustName());
					plFeeList.setBpsName(protocol.getBpsName());
				}else {
					plFeeList.setCustName(dto.getCustname());
					plFeeList.setBpsName(dto.getPoolName());
				}
				plFeeList.setCustNo(clientNo);//核心客户号 
				plFeeList.setBpsNo(bpsNo);//票据池编号
				plFeeList.setFeeType(PoolComm.SFMS_01);//收费模式
				plFeeList.setRealAmt(BigDecimalUtils.valueOf(realCharAmt));//实收金额
				plFeeList.setRecvAmt(BigDecimalUtils.valueOf(recvFeeAmt));//应收金额
				plFeeList.setLessAmt(BigDecimalUtils.valueOf(FeeDecrease));//减免金额
				plFeeList.setChargeDate(new Date());//收费时间
				plFeeList.setSource(PoolComm.SFLY_01);
				Calendar cuur = Calendar.getInstance();
				dto.setFeeIssueDt(cuur.getTime());//生效日
				cuur.set(Calendar.YEAR, cuur.get(Calendar.YEAR)+1);
				Date date = cuur.getTime();
				dto.setFeeDueDt(date);//到期日
				pedProtocolService.txStore(plFeeList);
				pedProtocolService.txStore(dto);
			}
		}else if(dto.getFeeType().equals(PoolComm.SFMS_02)){//单笔
			batch = poolBatchNoUtils.txGetBatchNo("SF",16);
			PlFeeList plFeeList = new PlFeeList();
			if(dto.getIsGroup().equals(PoolComm.YES)){//集团
				ProListQueryBean Bean = new ProListQueryBean();
				Bean.setBpsNo(bpsNo);
				Bean.setCustNo(clientNo);
				PedProtocolList protocol = pedProtocolService.queryProtocolListByQueryBean(Bean);//查询主户信息
				plFeeList.setCustName(protocol.getCustName());
				plFeeList.setBpsName(protocol.getBpsName());
			}else {
				plFeeList.setCustName(dto.getCustname());
				plFeeList.setBpsName(dto.getPoolName());
			}
			plFeeList.setCustNo(clientNo);//核心客户号 
			plFeeList.setBpsNo(bpsNo);//票据池编号
			plFeeList.setFeeType(PoolComm.SFMS_02);//收费模式
			plFeeList.setRealAmt(BigDecimalUtils.valueOf(realCharAmt));//实收金额
			plFeeList.setRecvAmt(BigDecimalUtils.valueOf(recvFeeAmt));//应收金额
			plFeeList.setLessAmt(BigDecimalUtils.valueOf(FeeDecrease));//减免金额
			plFeeList.setFeeBatchNo(batch);//收费批次号
			plFeeList.setChargeDate(new Date());//收费时间
			plFeeList.setSource(PoolComm.SFLY_01);
			pedProtocolService.txStore(plFeeList);
		}
		return batch;
	}
}
