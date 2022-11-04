package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.page.Page;

/**
 * 网银接口(pjc047)——网银纸票出入池申请批次明细信息查询
 * @Description 
 * @author wu fengjun
 * @version v1.0
 * @date 2019-6-21
 */
public class PJC047RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC047RequestHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private AssetTypeManageService assetTypeManageService;
	
	/**
	 * 
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List<Map> list = new ArrayList<Map>();
		Page page = getPage(request.getAppHead());
		try {
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String bpsNo = getStringVal(request.getBody().get("BPS_NO"));
			String batchNo = getStringVal(request.getBody().get("BATCH_NO"));
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			
			if (dto == null || !PoolComm.OPEN_01.equals(dto.getOpenFlag())) {
				throw new Exception("客户票据池功能不属于开通状态!");
			}else {
				PoolQueryBean query = new PoolQueryBean();
				query.setCustomernumber(custNo);
				query.setProtocolNo(bpsNo);
				query.setSBatchNo(batchNo);
				PlPdraftBatch draftBatch = (PlPdraftBatch) draftPoolQueryService.queryPlPdraftBatchByBatch(query).get(0);

				response.getBody().put("CORE_CLIENT_NO", draftBatch.getCustNo());
				response.getBody().put("BPS_NO", draftBatch.getBpsNo());
				response.getBody().put("BATCH_NO", draftBatch.getBatchNo());
				response.getBody().put("APP_MOBIL", draftBatch.getWorlerPhoneNo());
				response.getBody().put("APP_NAME", draftBatch.getWorkerName());
				response.getBody().put("APP_IDENTITY_GLOBAL_ID", draftBatch.getWorkerId());
				response.getBody().put("USAGE", draftBatch.getUseWay());
				response.getBody().put("REMARK", draftBatch.getRemark());//备注

				QueryResult queryResult  =draftPoolQueryService.queryPoolBillInfoByPram(query ,page);
				if(null!=queryResult){
				List billInfos = queryResult.getRecords();
				for (int i = 0; i < billInfos.size(); i++) {
					PoolBillInfo info = (PoolBillInfo) billInfos.get(i);
					Map map = new HashMap();
					map.put("BILL_INFO_ARRAY.BILL_NO", info.getSBillNo());//票号
					map.put("BILL_INFO_ARRAY.BILL_CLASS", info.getSBillType());//票据类型
					map.put("BILL_INFO_ARRAY.BILL_AMT", info.getFBillAmount()); //票据金额
					map.put("BILL_INFO_ARRAY.DRAW_DATE", DateUtils.toString(info.getDIssueDt(),"yyyyMMdd")); //出票日
					map.put("BILL_INFO_ARRAY.EXPIRY_DATE", DateUtils.toString(info.getDDueDt(),"yyyyMMdd"));//到期日
					map.put("BILL_INFO_ARRAY.BILL_ID", info.getDiscBillId());
					map.put("BILL_INFO_ARRAY.BILL_TYPE", info.getSBillMedia());//票据介质
					if ("1".equals(info.getSBanEndrsmtFlag())) {// 不得转让
						map.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "0");// 不得转让
					} else {
						map.put("BILL_INFO_ARRAY.TRANSFER_FLAG", "1");// 可转让
					}
					map.put("BILL_INFO_ARRAY.BILL_NAME", info.getSIssuerName());//出票人名称
					map.put("BILL_INFO_ARRAY.BILL_OPENBANK_NAME", info.getSIssuerBankName());//出票人开户行名
					map.put("BILL_INFO_ARRAY.PAYEE_NAME", info.getSPayeeName());//收款人名称
					map.put("BILL_INFO_ARRAY.PAYEE_OPENBANK_NAME", info.getSPayeeBankName());//收款人开户行名
					//map.put("BILL_INFO_ARRAY.IS_PRODUCE_MONEY", info.getSPayeeBankName());//收款人开户行名
					
					//顺延天数
					long deferDays = assetTypeManageService.queryDelayDays(info.getRickLevel(), info.getDDueDt());
					
					map.put("BILL_INFO_ARRAY.DEFER_DAYS", deferDays);// 顺延天数
					
					// 是否产生额度
					String idEdu = "";
					if (PoolComm.BLACK.equals(info.getBlackFlag()) || PoolComm.NOTIN_RISK.equals(info.getRickLevel())) {// 黑名单以及不在风险名单的票据不产生额度
						idEdu = "0";
					} else {
						idEdu = "1";
					}
					map.put("BILL_INFO_ARRAY.IS_PRODUCE_MONEY", idEdu);// 是否产生额度
					if ("0".equals(idEdu)) { //额度金额
						map.put("BILL_INFO_ARRAY.LIMIT_AMT", new BigDecimal("0.00"));
					} else {
						map.put("BILL_INFO_ARRAY.LIMIT_AMT", info.getFBillAmount());
					}
					if (PoolComm.BLACK.equals(info.getBlackFlag())) {//黑名单
						map.put("BILL_INFO_ARRAY.RISK_FLAG", "02");// 风险标识 -黑名单票据不可入池
						map.put("BILL_INFO_ARRAY.RISK_LEVEL", info.getRickLevel());// 风险等级
					}else {//没黑名单
						if(PoolComm.NOTIN_RISK.equals(info.getRickLevel())){//不产生额度
							map.put("BILL_INFO_ARRAY.RISK_LEVEL", info.getRickLevel());// 风险等级
						}else{
							map.put("BILL_INFO_ARRAY.RISK_LEVEL", info.getRickLevel());// 风险等级
						}
						map.put("BILL_INFO_ARRAY.RISK_FLAG", info.getBlackFlag());// 风险标识 -灰名单票据
					}
					map.put("BILL_INFO_ARRAY.BUSI_ID", info.getBillinfoId());// 业务明细ID
					map.put("BILL_INFO_ARRAY.REMARK", "");//备注
					list.add(map);
				}
				response.getBody().put("TOTAL_AMT", queryResult.getTotalAmount());
				setPage(response.getAppHead(), page);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("网银纸票出入池申请批次明细信息查询成功!");
				}else{
					ret.setRET_CODE(Constants.EBK_03);
					ret.setRET_MSG("该客户未查询到网银纸票出入池申请明细信息!");
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("网银纸票出入池申请批次明细信息查询异常");
		}
		response.setDetails(list);
		response.setRet(ret);
		return response;
	}
	


}
