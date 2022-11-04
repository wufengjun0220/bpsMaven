package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.base.service.PoolQueryService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 
 * @Title: 网银接口PJC002
 * @Description: 票据池-入池申请接口
 * @author Ju Nana
 * @date 2018-10-25
 */
public class PJC002RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC002RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;// 网银服务
	@Autowired
	private PedProtocolService pedProtocolService;// 协议服务
	@Autowired
	private PoolQueryService poolQueryService;//
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		
		
		String operType = getStringVal(request.getBody().get("OPERATION_TYPE"));
		ReturnMessageNew response = new ReturnMessageNew();
		
		if ("0".equals(operType)) { 
			/*
			 * 入池申请
			 */
			response = poolInApply(request);
		} else {
			/*
			 *  出池申请
			 */
			response = poolOutApply(request);
		}
		
		
		return response;
	}

	/**
	 * 入池申请方法
	 * 
	 * @param request
	 * @return ReturnMessageNew
	 * @author Ju Nana
	 * @date 2019-2-6 下午10:24:25
	 */
	public ReturnMessageNew poolInApply(ReturnMessageNew request) {
		
		// （1）根据核心客户号判断是否开通票据池业务
		// （2）请求数据加工处理
		// （3）调用service服务实现入池申请功能
		// （4）返回处理结果
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String eSign = getStringVal(request.getBody().get("E_SIGN"));
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));
			
			
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setPoolAgreement(bpsNo);
			PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			
			/*
			 * 协议校验
			 */
			if(dto == null ){
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("根据票据池编号未查到票据池签约信息！");
				response.setRet(ret);
				return response;
			}
			
			/*
			 * 融资业务校验
			 */
			if(StringUtil.isBlank(dto.getOpenFlag()) || !dto.getOpenFlag().equals(PoolComm.OPEN_01)){
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("该企业未开通融资票据池业务，无法发起入池申请！");
				response.setRet(ret);
				return response;
			}
			/*
			 * 集团票据池校验
			 */
			if(StringUtil.isNotBlank(dto.getIsGroup())&&PoolComm.YES.equals(dto.getIsGroup())){				
				
				//担保协议信息校验
				
				if(StringUtil.isBlank(dto.getContract())){
					ret.setRET_CODE(Constants.EBK_02);
					ret.setRET_MSG("该票据池未推送担保合同，不允许入池！");
					response.setRet(ret);
					return response;
				}
				
				//客户身份校验
				
				ProListQueryBean qb = new ProListQueryBean();
				qb.setBpsNo(bpsNo);
				qb.setCustNo(custNo);
				PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(qb);
				String custIdentity = mem.getCustIdentity();
				
				if(PoolComm.KHLX_01.equals(custIdentity)||PoolComm.KHLX_03.equals(custIdentity)){
					//校验通过
				}else{
					ret.setRET_CODE(Constants.EBK_02);
					ret.setRET_MSG("该客户身份非融资人不允许入池！");
					response.setRet(ret);
					return response;
				}
								
			}
			/*
			 * 申请数据校验
			 */
			List list = request.getDetails();
			if (list == null && list.size() == 0) {				
				ret.setRET_CODE(Constants.EBK_02);
				ret.setRET_MSG("未获取到入池申请的票据信息！");
				response.setRet(ret);
				return response;
			}
			
			/*
			 * 循环处理入池票据
			 */
			String blackStr = "";
			int blackSize = 0;
			String dateStr = "";
			int dateSize = 0;
			String smtStr = "";
			int smtSize = 0;
			List<DraftPoolIn> inList=new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				PoolBillInfo info = dataProcess(map);
				PoolBillInfo billInfo = poolQueryService.queryObj(info.getSBillNo(),info.getBeginRangeNo(),info.getEndRangeNo());
				
				if(billInfo==null){
					logger.info("票据："+billInfo.getSBillNo()+" 未查询到信息");
					continue;
				}
				
				//财票改造开关
				String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//在线业务总开关 
				
				if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){			
					billInfo = blackListManageService.txBlacklistCheck(billInfo, custNo);
				}else{
					billInfo = blackListManageService.txBlacklistAndRiskCheck(billInfo, custNo);
				}
				
				String blackFlag = billInfo.getBlackFlag();
				
				/*
				 * 票的到期日是否已至，若已过则过滤
				 */
				if(DateUtils.checkOverLimited(DateUtils.getWorkDayDate(),billInfo.getDDueDt())){
					dateSize = ++dateSize;
					if(dateSize < list.size()){
						dateStr = "到期票据已过滤,";
						logger.info("票据："+billInfo.getSBillNo()+" 已知到期日，已过滤");
					}
					continue;
				}
				/*
				 * 黑名单过滤
				 */
				if(StringUtil.isNotBlank(blackFlag)&&blackFlag.equals(PoolComm.BLACK)){//如果在黑名单中，则不允许入池
					blackSize = ++blackSize;
					if(blackSize < list.size()){
						blackStr = "黑名单票据已过滤,";
						logger.info("票据："+billInfo.getSBillNo()+" 在黑名单中，已过滤");
					}
					poolEBankService.txStore(billInfo);
					continue;
				}
				
				/*
				 * 不可转让票据过滤
				 */
				if (billInfo.getSBanEndrsmtFlag().equals("1")) {//不可转让
					smtSize = ++smtSize;
					if(smtSize < list.size()){
						smtStr = "不可转让票据已过滤,";
						logger.info("票据："+billInfo.getSBillNo()+" 不可转让，已过滤");
					}
					continue;
				}else{
					/*
					 * 入池申请
					 */
					DraftPoolIn inPool = poolEBankService.txApplyDraftPoolInPJC002(billInfo, eSign, bpsNo, info);
					inList.add(inPool);
				}
				
			}
			
			/*
			 * 入池自动任务发布
			 */
			if(inList.size()>0){
				for(DraftPoolIn inPool:inList){
					Map<String, String> reqParams =new HashMap<String,String>();
					reqParams.put("busiId", inPool.getId());
					autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOLIN_TASK_NO, inPool.getId(), AutoTaskNoDefine.BUSI_TYPE_ZY, reqParams, inPool.getPlDraftNb(), dto.getPoolAgreement(), dto.getPoolName(), null);
				}
			}
			
			
			if(blackSize+dateSize+smtSize == list.size()){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				if(blackSize == list.size()){
					ret.setRET_MSG("所选票据全为黑名单票据,入池申请失败");
				}else if (dateSize == list.size()) {
					ret.setRET_MSG("所选票据全为过期票据,入池申请失败");
				}else if (smtSize == list.size()){
					ret.setRET_MSG("所选票据全为禁止背书票据,入池申请失败");
				}else {
					ret.setRET_MSG("入池申请失败，没有可入池票据！");
				}
			}else {
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(smtStr+dateStr+blackStr+"入池申请成功！");
			}

		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("入池申请异常[" + e.getMessage() + "]");
		}
		
		
		response.setRet(ret);
		return response;
	}

	/**
	 * 出池申请方法
	 * 
	 * @param request
	 * @return ReturnMessageNew
	 * @author Ju Nana
	 * @date 2019-2-6 下午10:24:45
	 */
	public ReturnMessageNew poolOutApply(ReturnMessageNew request) {

		// （1）根据核心客户号判断是否开通票据池业务
		// （2）解析请求信息
		// （3）调用service服务实现出池申请功能
		// （4）返回处理结果
		logger.info("PJC002网银发起出池申请处理开始...");
		
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List<String> billNos = new ArrayList();
		List<String> assetNos = new ArrayList();
		Map<String,PoolBillInfo> changeBills= new HashMap<String, PoolBillInfo>();
		String apId ="";//AssetPool表ID
		List list = null;
		
		try {
			String bpsNo = (String) request.getBody().get("BPS_NO");
			String elsignature = (String) request.getBody().get("E_SIGN");// 电子签名
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
			list = request.getDetails();		
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
			apId = ap.getApId();
			boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
			if(!isLockedSucss){//加锁失败
				ret.setRET_CODE(Constants.EBK_11);
				ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
				response.setRet(ret);
				logger.info("票据池【"+dto.getPoolAgreement()+"】上锁！");
				return response;
			}
			
			
			/*
			 * 出池票号组装
			 */
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);

				/********************融合改造修改 start******************************/
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
				System.out.println("-------------------------------");
				System.out.println(startNo);
				System.out.println(endNo);
				
				String assNo = billNo+ "-" +startNo +"-" +endNo;
				assetNos.add(assNo);
				changeBills.put(assNo, info);//网银发送过来的数据
				/********************融合改造修改 end******************************/

			}
			

			/*
			 * 出池申请
			 */
			ret = poolEBankService.txApplyDraftPoolOutPJC003(billNos, elsignature, dto,null,changeBills,assetNos);
			
			if (Constants.TX_SUCCESS_CODE.equals(ret.getRET_CODE())) {
				
				/*
				 * 生成自动任务流水记录 异步执行额度释放 gcj 20210513
				 */
				List<DraftPoolOut> outlist = ret.getSomeList3();
				if(null!=outlist && outlist.size()>0){					
					Map<String, String> reqParams =new HashMap<String,String>();
					for (DraftPoolOut poolout:outlist) {
						reqParams.put("busiId", poolout.getId());
						autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOLOUT_EDU_TASK_NO, poolout.getId(), AutoTaskNoDefine.BUSI_TYPE_JZY, reqParams, poolout.getPlDraftNb(), dto.getPoolAgreement(), dto.getPoolName(), null);
					}
				}
				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("出池申请成功！");
				
			}else{
				/**
				 * 出池额度不足需解除票据池锁
				 */
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);

					PoolBillInfo info = dataProcess(map);
					PoolBillInfo billInfo = poolQueryService.queryObj(info.getSBillNo(),info.getBeginRangeNo(),info.getEndRangeNo());
					
					billInfo.setEbkLock(PoolComm.EBKLOCK_02);
					poolQueryService.txStore(billInfo);

				}
				
				ret.setRET_CODE(Constants.EBK_05);
				ret.setRET_MSG("池额度不足不允许出池！");
			}
			
			
		} catch (Exception e) {
			pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
			/**
			 * 出池异常需解除票据池锁
			 */
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				try {
				PoolBillInfo info = dataProcess(map);
				PoolBillInfo billInfo;
				
				billInfo = poolQueryService.queryObj(info.getSBillNo(),info.getBeginRangeNo(),info.getEndRangeNo());
				billInfo.setEbkLock(PoolComm.EBKLOCK_02);
				poolQueryService.txStore(billInfo);
				} catch (Exception e1) {
					logger.error(e, e);
					logger.info("出池做解锁异常");
				}
			}
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("出池申请票据池系统异常，请联系票据池系统！");
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
	 * @date 2018-10-17 下午3:25:24
	 */
	private PoolBillInfo dataProcess(Map map) throws Exception {

		PoolBillInfo info = new PoolBillInfo();
		String id = (String) map.get("BILL_INFO_ARRAY.BUSI_NO"); // 业务明细ID
		String plDraftNb = (String) map.get("BILL_INFO_ARRAY.BILL_NO"); // 电票票号

		/********************融合改造新增 start******************************/
		String beginRangeNo = (String) map.get("BILL_INFO_ARRAY.START_BILL_NO"); // 票据开始子区间号
		String endRangeNo = (String) map.get("BILL_INFO_ARRAY.END_BILL_NO"); // 票据结束子区间号
		BigDecimal tradeAmt = getBigDecimalVal(map.get("BILL_INFO_ARRAY.TRAN_AMT")); // 交易金额(等分化票据实际交易金额)
		/*** 融合改造新增字段  end*/
		
		
		info.setBillinfoId(id);
		info.setSBillNo(plDraftNb);
		info.setBeginRangeNo(beginRangeNo);
		info.setEndRangeNo(endRangeNo);
		info.setTradeAmt(tradeAmt);
		return info;
	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
