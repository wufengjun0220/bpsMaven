package com.mingtech.application.pool.bank.countersys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.base.service.PoolQueryService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 纸票出池接口（柜面）
 * @author wu fengjun
 * @data 2019-06-12
 */
public class GM007CounterHandler  extends PJCHandlerAdapter{

	private static final Logger logger = Logger.getLogger(GM007CounterHandler.class);
	
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private ConsignService consignService;
	@Autowired
	private PoolVtrustService poolVtrustService;
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private DraftPoolOutService draftPoolOutService;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private DraftPoolInService draftPoolInService ;
	@Autowired
	private PoolQueryService poolQueryService;
	
	/**
	 * 1、通过核心客户号，票据池编号查询协议（）
	 * 2、校验是否开通票据池
	 * 3、校验是否可以出池
	 * 4、可以出池(扣额度)
	 * 5、判断是否商票是否占保贴额度，释放保贴额度
	 * 6、核心记账
	 * 
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		
		List<DraftPool> DraftPoolList = new ArrayList<DraftPool>();
		String apId = null;
		
		try {
			// 验印信息封装  start
			CoreTransNotes checkBean = new CoreTransNotes();
			String checkSeqNo = getStringVal(body.get("CHECK_SEQ_NO"));//验印流水号
			String checkAcctNo = getStringVal(body.get("CHECK_ACCT_NO"));//验印账号
			BigDecimal  checkAmt = getBigDecimalVal(body.get("CHECK_AMT"));//验印金额
			Date checkDrawDate = getDateVal(body.get("CHECK_DRAW_DATE"));//验印签发日期
			String checkCertNo = getStringVal(body.get("CHECK_CERT_NO"));//验印凭证号
			String checkCretCategory = getStringVal(body.get("CHECK_CERT_CATEGORY"));//验印凭证种类
			Date checkTranDate = getDateVal(body.get("CHECK_TRAN_DATE"));//验印交易日期
			
			checkBean.setCheckSeqNo(checkSeqNo);
			checkBean.setCheckAcctNo(checkAcctNo);
			checkBean.setCheckAmt(checkAmt);
			checkBean.setCheckDrawDate(checkDrawDate);
			checkBean.setCheckCertNo(checkCertNo);
			checkBean.setCheckCretCategory(checkCretCategory);
			checkBean.setCheckTranDate(checkTranDate);
			
			//验印信息封装  end
			
			String bpsNO = getStringVal(body.get("BPS_NO"));
			String batch = getStringVal(body.get("OUTPOOL_BATCH_NO"));
			
			String user = getStringVal(request.getSysHead().get("USER_ID"));
			String branch = getStringVal(request.getSysHead().get("BRANCH_ID"));
			List list = request.getDetails();
			List<String> billNos = new ArrayList<String>();
			
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNO, null, null, null);
			
			/*
			 * 冻结校验
			 */
			if (dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_02)|| dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_03)) {
				txApplyUnlock(list);
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户票据池的票据额度已冻结，无法发起出池申请！");
				response.setRet(ret);
				return response;
				
			} 
			/*
			 * 数据校验
			 */
			if (list == null || list.size() == 0){
				txApplyUnlock(list);
				ret.setRET_CODE(Constants.EBK_04);
				ret.setRET_MSG("未获取到出池票据信息申请");
				response.setRet(ret);
				logger.info("未获取到出池票据信息！");
				return response;
			}
			
			/*
			 * 锁AssetPool表
			 */
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
			apId = ap.getApId();
			boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
			if(!isLockedSucss){//加锁失败
				ret.setRET_CODE(Constants.EBK_11);
				ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
				response.setRet(ret);
				logger.info("票据池【"+dto.getPoolAgreement()+"】上锁！");
				return response;
			}
			
			if(StringUtil.isNotBlank(batch)){//网银预约出池，不需再释放额度,只释放保贴额度并记账
				
				logger.info("批次【"+batch+"】网银预约出池申请...");
				PoolQueryBean bean = new PoolQueryBean();
				bean.setSBatchNo(batch);
				List<PoolBillInfo> infoList = draftPoolQueryService.queryPoolBillInfoByPram(bean);
				if (infoList != null && infoList.size() > 0) {
					for (int i = 0; i < infoList.size(); i++) {
						PoolBillInfo info =  infoList.get(i);
						PoolQueryBean poolQueryBean = new PoolQueryBean();                                                         
						poolQueryBean.setBillNo(info.getSBillNo());
						poolQueryBean.setSStatusFlag(PoolComm.DS_03);
						List drafts =consignService.queryDraftByBean(poolQueryBean);
						DraftPoolList.add((DraftPool) drafts.get(0));
					}
					/*
					 * 额度系统额度操作，记账操作
					 */
					response = this.txApplyMisEdu(DraftPoolList ,batch,user ,branch,dto,checkBean);
					
				}else{
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("未查询到出池批次信息");
					response.setRet(ret);
					//解锁
					pedAssetPoolService.txReleaseAssetPoolLock(apId);
					return response;
				}
			}else {//柜面调的批,需释放额度,并释放保贴额度并记账
				List<String> assetNos = new ArrayList();
				Map<String,PoolBillInfo> changeBills= new HashMap<String, PoolBillInfo>();

				logger.info("柜面出池...");
				
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						Map map = (Map) list.get(i);
						String billNo = (String) map.get("BILL_INFO_ARRAY.BILL_NO");// 票号
						PoolQueryBean poolQueryBean = new PoolQueryBean();
						poolQueryBean.setBillNo(billNo);
						poolQueryBean.setSStatusFlag(PoolComm.DS_02);
						List drafts=consignService.queryDraftByBean(poolQueryBean);
						billNos.add(billNo);
						
						/********************融合改造新增 start******************************/
						PoolBillInfo pool = poolQueryService.queryObj(billNo,"0","0");
						String assNo = billNo +"-0" +"-0";
						assetNos.add(assNo);
						changeBills.put(assNo, pool);
						/********************融合改造新增 end******************************/
						
						logger.info("根据票号得到票据"+DraftPoolList);
						DraftPoolList.add((DraftPool) drafts.get(0));
					}
				}else{
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("无出池票据信息！");
					response.setRet(ret);
					//解锁
					pedAssetPoolService.txReleaseAssetPoolLock(apId);
					return response;
				}
				/*
				 * 票据池额度释放
				 */
				PoolQueryBean param = new PoolQueryBean();//传递参数的bean
				
				Ret result = poolEBankService.txApplyDraftPoolOutPJC003(billNos, null, dto,param,changeBills,assetNos);
				
				if (Constants.TX_SUCCESS_CODE.equals(result.getRET_CODE())) {
					/*
					 * 额度系统额度操作，记账操作
					 */
					response = this.txApplyMisEdu(result.getSomeList() ,null,user ,branch,dto,checkBean);
					
				} else if(Constants.EBK_04.equals(result)){
					txApplyUnlock(list);
					ret.setRET_CODE(Constants.EBK_04);
					ret.setRET_MSG("票据额度冻结不允许出池！");
					response.setRet(ret);
					//解锁
					pedAssetPoolService.txReleaseAssetPoolLock(apId);
					return response;
				} else if(Constants.EBK_05.equals(result)){
					txApplyUnlock(list);
					ret.setRET_CODE(Constants.EBK_05);
					if(PoolComm.POOL_MODEL_01.equals(dto.getPoolMode())){//总量模式
						ret.setRET_MSG("池额度不足不允许出池！");
					}else{
						ret.setRET_MSG("池额度不足不允许出池！");
					}
					response.setRet(ret);
					//解锁
					pedAssetPoolService.txReleaseAssetPoolLock(apId);
					return response;
				}else{//出池票据种含有当日及当日之前到期的票，不允许出池操作！
					txApplyUnlock(list);
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据池系统异常！");
					response.setRet(ret);
					//解锁
					pedAssetPoolService.txReleaseAssetPoolLock(apId);
					return response;
				}
				
			}
			
		}catch(Exception e){
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("出池发生错误[" + e.getMessage() + "]");
			response.setRet(ret);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		if (!response.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		//解锁
		if(null != apId){
			pedAssetPoolService.txReleaseAssetPoolLock(apId);
		}
		return response;
		
	}
	/**
	 * 额度系统额度释放及核心系统记账
	 * @param poolList
	 * @param batch
	 * @param user
	 * @param branch
	 * @param dto
	 * @param checkBean
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2019-11-20下午3:45:02
	 */
	public ReturnMessageNew txApplyMisEdu(List<DraftPool> poolList ,String batch ,String user ,String branch,PedProtocolDto dto,CoreTransNotes checkBean) throws Exception{

		Ret ret = new Ret();
		ReturnMessageNew req = new ReturnMessageNew();
		List <DraftPool> draftPools = new ArrayList<DraftPool>();
		List <PedGuaranteeCredit> guList = new ArrayList<PedGuaranteeCredit>();
		List <PoolVtrust> vtList = new ArrayList<PoolVtrust>();

		
		List <DraftPool> draftPoolsBak = new ArrayList<DraftPool>();//回滚使用
		List <PedGuaranteeCredit> guListBak = new ArrayList<PedGuaranteeCredit>();//回滚使用
		List <PoolVtrust> vtListBak = new ArrayList<PoolVtrust>();//回滚使用
		
		List <DraftPool> doBak = new ArrayList<DraftPool>();//进行额度处理的票据记录  回滚使用
		List<PoolBillInfo> billList = new ArrayList<PoolBillInfo>();
		/**
		 * 释放额度系统的相关额度
		 */
		try {
			
			List<Map> reqList = new ArrayList<Map>();//需要释放额度的票据
			for (int i = 0; i < poolList.size(); i++) {
				DraftPool draft = poolList.get(i);
				Map resuMap = new HashMap();
				if(draft.getBtFlag() != null && draft.getBtFlag().equals(PoolComm.SP_01)){//占用成功释放保贴额度				
					
					
					/*
					 * 需要落库的数据加工
					 */
					PoolVtrustBeanQuery query = new PoolVtrustBeanQuery();
					query.setVtNb(draft.getAssetNb());
					PoolVtrust vtrust = (PoolVtrust) poolVtrustService.queryPoolVtrust(query);
					
					/*
					 * 接口数据
					 */
					if(draft.getBtFlag() != null && draft.getBtFlag().equals("1")){
						//保贴额度占用需释放
						resuMap.put("billNo", draft.getAssetNb());
						reqList.add(resuMap);
					}
					
					
					draft.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
					vtrust.setBtFlag(PoolComm.SP_00);
					PoolQueryBean pBean = new PoolQueryBean();
					pBean.setProtocolNo(draft.getPoolAgreement());
					pBean.setBillNo(draft.getAssetNb());
					
					/********************融合改造新增 start******************************/
					pBean.setBeginRangeNo(draft.getBeginRangeNo());
					pBean.setEndRangeNo(draft.getEndRangeNo());
					/********************融合改造新增 end******************************/
					
					PedGuaranteeCredit pedCredit = poolCreditProductService.queryByBean(pBean);
					if(pedCredit != null){
						pedCredit.setStatus(PoolComm.SP_00);
						pedCredit.setCreateTime(new Date());
						guList.add(pedCredit);
						guListBak.add(pedCredit);
					}
					
					vtList.add(vtrust);
					
					vtListBak.add(vtrust);
					
					doBak.add(draft);//释放处理的票据
					
					PoolBillInfo bill = draftPoolInService.loadByBillNo(draft.getAssetNb(),"0","0");
					billList.add(bill);
					
					

				}
				draftPools.add(draft);
				draftPoolsBak.add(draft);
			}
			if(reqList != null && reqList.size() > 0){
				CreditTransNotes creditNotes = new CreditTransNotes();
				creditNotes.setReqList(reqList);//上传文件
				ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
				
				if (response1.isTxSuccess()) {//落库操作
					consignService.txStoreAll(guList);
					consignService.txStoreAll(vtList);
					consignService.txStoreAll(draftPools);
				}else{
					logger.info("额度系统额度释放失败");
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("额度系统额度释放失败");
					req.setRet(ret);
					return req;
				}
			}
			
			
		} catch (Exception e) {
			logger.info("票据池系统内部错误！");
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
			//解锁
			pedAssetPoolService.txReleaseAssetPoolLock(ap.getApId());
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池系统内部错误！");
			req.setRet(ret);
			return req;
		}
		
		
		/**
		 * 调用核心记账
		 *
		 */
		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setUser(user);
		transNotes.setBeatch(branch);
		transNotes.setBrcNo(dto.getCreditDeptNo()); //客户号对应的签约机构
		transNotes.setAccNo(dto.getPoolAccount());//结算账号
		transNotes.setBpsNo(dto.getPoolAgreement());//票据池编号

		ArrayList reList = new ArrayList();
		BigDecimal totalAmt = BigDecimalUtils.ZERO;
		for (int j = 0; j < poolList.size(); j++) {
			DraftPool pool = poolList.get(j);
			totalAmt = totalAmt.add(pool.getAssetAmt());

			Map map = new HashMap();
			map.put("IdxNo", j+1);
			map.put("UserID", pool.getAssetNb());//票号
			map.put("AccNoD", batch);//批号
//					map.put("", );//机构	?
			map.put("FlgCR", "2");//抵押物类型
//					map.put("IDNo", "24");//担保品种类
			map.put("SubStnD", "224");//担保品种类
			map.put("CcyD", "01");//币种
			map.put("IsChkNam", "0");//纸票
			map.put("AmtTran",pool.getAssetAmt().setScale(2,BigDecimal.ROUND_HALF_UP));//票面金额
//					map.put("", );//担保金额   ?
			map.put("AccSubD", pool.getCustNo());//客户号
			map.put("BnkPayee", "");//担保品种类
			map.put("MemoD", pool.getGuaranteeNo());//担保品编号
			map.put("AccNoH", dto.getContract());//担保合同号
			map.put("ClsNoH", pool.getPlDueDt());//到期日
//					map.put("", "");//备注(摘要)
			reList.add(map);
		}
		String str = poolBatchNoUtils.txGetFlowNo();
		transNotes.setDevSeqNo(str);//第三方流水号
		transNotes.setTotalAmt(totalAmt.setScale(2,BigDecimal.ROUND_HALF_UP)+"");
		if(reList.size() >0 ){
			logger.info("需要上传的文件的长度为:"+reList.size());
			String path = poolBatchNoUtils.txGetBatchNo("/BPS/BAS_TRFSND", 4);
			transNotes.setPath(path);
			transNotes.setRemark("1");
		}else {
			transNotes.setRemark("0");
		}
		transNotes.setList(reList);
		transNotes.setiDTypeS("H");//业务种类：票据批量出池
		transNotes.setFlgEnt("0");//收费标枳
		
		//验印相关信息
		if(checkBean.getCheckSeqNo() == null || checkBean.getCheckSeqNo().equals("")){//预约出池验印流水号上送999999
			transNotes.setCheckSeqNo("99999999");
		}else{
			transNotes.setCheckSeqNo(checkBean.getCheckSeqNo());
		}
		transNotes.setCheckAcctNo(checkBean.getCheckAcctNo());
		transNotes.setCheckAmt(checkBean.getCheckAmt());
		transNotes.setCheckDrawDate(checkBean.getCheckDrawDate());
		transNotes.setCheckCertNo(checkBean.getCheckCertNo());
		transNotes.setCheckCretCategory(checkBean.getCheckCretCategory());
		transNotes.setCheckTranDate(checkBean.getCheckTranDate());
		
		/*
		 * 记账接口调用操作
		 */
		ReturnMessageNew resp = poolCoreService.txPledgeAccount(transNotes);
		
		if(resp.isTxSuccess()){
		    if (batch != null ) {
                PoolQueryBean queryBean = new PoolQueryBean();
                queryBean.setSBatchNo(batch);

                PlPdraftBatch draftBatch = (PlPdraftBatch) draftPoolQueryService.queryPlPdraftBatchByBatch(queryBean).get(0);
                draftBatch.setStatus("0");//已出池批次失效
                draftBatch.setIsPoolOutEnd(PoolComm.OUT_00);//批次已出池
                draftPoolQueryService.txStore(draftBatch);
            }

		    for (int j = 0; j < draftPools.size(); j++) {
				DraftPool pool = draftPools.get(j);
				
				//大票表
                PoolBillInfo billInfo = pool.getPoolBillInfo();
                billInfo.setSDealStatus(PoolComm.DS_04);
                billInfo.setDraftOwnerSts(PoolComm.KHCY_00);
                billInfo.setLastOperTm(new Date());
                billInfo.setLastOperName("柜面出池");

                //资产表
                pool.setAssetStatus(PoolComm.DS_04);
				pool.setLastOperTm(new Date());
				pool.setLastOperName("柜面出池");
				
				//vtrust表
                PoolVtrustBeanQuery query = new PoolVtrustBeanQuery();
                query.setPoolAgreement(pool.getAssetNb());
                PoolVtrust vtrust = (PoolVtrust) poolVtrustService.queryPoolVtrust(query);
                vtrust.setVtStatus(null);
				vtrust.setLastOperName("柜面出池落库");
				vtrust.setLastOperTm(new Date());
				
				//出池表
                PoolQueryBean poolBean = new PoolQueryBean();
                poolBean.setBillNo(pool.getAssetNb());
                poolBean.setSStatusFlag(PoolComm.CC_00);
				DraftPoolOut out = draftPoolOutService.getDraftPoolOutBybean(poolBean);
				out.setHostSeqNo(getStringVal(resp.getSysHead().get("SERV_SEQ_NO")));
				out.setPlStatus(PoolComm.CC_05);
				
				draftPoolQueryService.txStore(out);
				draftPoolQueryService.txStore(vtrust);
				draftPoolQueryService.txStore(billInfo);
				draftPoolQueryService.txStore(pool);
		    }
		    
		    ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		    ret.setRET_MSG("出池记账成功");
		    req.getBody().put("HOST_SEQ_NO",resp.getSysHead().get("SERV_SEQ_NO"));
		    
		}else {
			/*
			 * 记账失败需要将原释放的额度重新占用，并将落库数据回滚
			 */
			try {
				String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//财票改造开关 
				if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){
					
					for (int i = 0; i < billList.size(); i++) {
						PoolBillInfo bill = billList.get(i);
						
						draftPoolInService.txMisCreditOccupy(bill);
						
					}
					
				}else{
					
					this.doPJE012(doBak);
				}
				
			} catch (Exception e) {
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG(" 额度系统额度释放成功，但记账失败，重新占用额度操作失败!");
				req.setRet(ret);
				return req;
			}
			
			consignService.txStoreAll(guListBak);
			consignService.txStoreAll(vtListBak);
			consignService.txStoreAll(draftPoolsBak);
			
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("出池记账失败!");
			req.setRet(ret);
			return req;
		}
		
		req.setRet(ret);
		return req;
	}
	
	/**
	 * 额度系统额度释放成功，但记账失败，重新占用操作
	 * @param doBak
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2019-11-20下午4:40:42
	 */
	private ReturnMessageNew doPJE012(List<DraftPool> doBak) throws Exception{
		
		List<Map> reqList = new ArrayList<Map>();
		CreditTransNotes creditNotes = new CreditTransNotes();
		
		for(DraftPool pool : doBak){
			
			String media = pool.getPlDraftMedia();//票据介质
			String type = pool.getAssetType();//票据类型
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
			
			
			resuMap.put("billNo", pool.getAssetNb());//票号                    
			resuMap.put("billsum", pool.getAssetAmt());//票面金额               
			resuMap.put("currency", "156");//币种:人民币                  
			resuMap.put("billType", billType);//票据种类              
			resuMap.put("billBusinessType", "02");//票据业务类型  01 贴现 02 质押  03 转贴现
			
			resuMap.put("customerId", "");//承兑人核心客户号    
			resuMap.put("bankId", pool.getPlAccptrSvcr());//承兑人二代支付系统行号  
			resuMap.put("customerName", pool.getPlAccptrNm());//承兑人名称        
			resuMap.put("execNominalAmount", pool.getAssetAmt());//占用名义金额 
			
			BigDecimal amt = BigDecimal.ZERO;//占用敞口金额:银票输0；商票输票面金额
			if(PoolComm.BILL_TYPE_BUSI.equals(type)){//商票
				amt = pool.getAssetAmt();
			}
			resuMap.put("execExposureAmount", amt);//占用敞口金额
			
			reqList.add(resuMap);
		}
		
		
		creditNotes.setReqList(reqList);//上传文件
		
		/*
		 *额度系统额度占用 
		 */
		ReturnMessageNew response1 = poolCreditClientService.txPJE012(creditNotes);
		return response1;
	}

	/**
	 * 出池失败,解除票据池锁
	 * @throws Exception 
	 */
	private void txApplyUnlock(List list) throws Exception{
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				String billNo = (String) map.get("BILL_INFO_ARRAY.BILL_NO");// 票号
				PoolBillInfo  billInfo = poolQueryService.queryObj(billNo,"0","0");
				
				billInfo.setEbkLock(PoolComm.EBKLOCK_02);
				poolQueryService.txStore(billInfo);
				
			}
		}
	}
}
