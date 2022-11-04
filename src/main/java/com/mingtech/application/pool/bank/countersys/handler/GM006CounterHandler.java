package com.mingtech.application.pool.bank.countersys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.bank.message.FileName;
import com.mingtech.application.pool.draft.domain.DraftPool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 票据信息查询接口（柜面）
 * @author wu fengjun
 * @data 2019-06-10
 */
public class GM006CounterHandler  extends PJCHandlerAdapter{

	private static final Logger logger = Logger
	.getLogger(GM006CounterHandler.class);
	
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private ConsignService consignService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	/**
	 * 1、通过核心客户号，票据池编号查询协议（）
	 * 2、校验是否开通票据池
	 * 3、根据查询种类查询纸票信息并返回
	 * 	①在池纸票（不含出池，托收记账）
	 * 	②网银预约取票（加经办锁的纸票）
	 * 4、....
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		PoolBillInfo billInfo = null;
		Map map = request.getBody();
		try {
			String clientNO = getStringVal(map.get("CORE_CLIENT_NO"));
			String bpsNO = getStringVal(map.get("BPS_NO"));
			String phone = getStringVal(map.get("APP_MOBIL"));
			String name = getStringVal(map.get("APP_NAME"));
			String crde = getStringVal(map.get("APP_IDENTITY_GLOBAL_ID"));
			String type = getStringVal(map.get("QUERY_TYPE"));
			String batch = getStringVal(map.get("OUTPOOL_BATCH_NO"));
			String user = getStringVal(request.getSysHead().get("USER_ID"));//用户
			String branchNo = getStringVal(request.getSysHead().get("BRANCH_ID"));//机构
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNO, null, null, null);//得到协议信息
			
			if(dto != null && dto.getOpenFlag().equals(PoolComm.OPEN_01)){//开通票据池
				List details = new ArrayList();
				if (type.equals("CXLX_01")) {//在池票据查询
					PoolQueryBean pq = new PoolQueryBean();
					pq.setCustomernumber(clientNO);//客户号
					pq.setProtocolNo(bpsNO);//票据池编号
					pq.setSBillMedia("1");//票据介质  纸质
					pq.setSStatusFlag(PoolComm.DS_02);
					pq.setBatchid(branchNo);
//					pq.setUser(user);
					logger.info("用户:"+user+"机构"+branchNo+"***********************************");
					List draftPool = consignService.queryDraftByBean(pq);
//					List vtrustList = consignService.getPaperPool(bpsNO, clientNO ,"1",null ,PoolComm.DS_02,branchNo,user);
//					logger.info("查询到的在池票据有["+draftPool.size()+"]张");
					details = vtrustDetailProcess(draftPool ,user);
					response.getAppHead().put("TOTAL_ROWS", draftPool.size());//总数
				}else {//网银预约取票查询
					PoolQueryBean pq = new PoolQueryBean();
					pq.setCustomernumber(clientNO);//客户号
					pq.setProtocolNo(bpsNO);//票据池编号
					pq.setSBillMedia("1");//票据介质  纸质
//					pq.setEbankPeopleCard(crde);//身份证号
//					pq.setEbankName(name);//姓名
//					pq.setEbankType(type);//经办人手机号
					pq.setSBatchNo(batch);
					
					
					List billList = draftPoolQueryService.queryPoolBillInfoByPram(pq);

					details = detailProcess(billList);
					
					response.getAppHead().put("TOTAL_ROWS", billList.size());//总数
				}
				if(details.size() > 0 ){
					String path = FileName.getFileNameClient(request.getTxCode())+".txt";
					response.getFileHead().put("FILE_FLAG", "2");
					response.getFileHead().put("FILE_PATH", path);
				}else {
					response.getFileHead().put("FILE_FLAG", null);
				}
				response.setDetails(details);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("查询成功!");
			}else{
				logger.info("该客户未开通票据池业务");
				throw new Exception("该企业开通票据池业务，无法查询");	
			}
			
		}catch(Exception e){
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询异常[" + e.getMessage() + "]");
		}
		response.setRet(ret);
		return response;
		
	}
	
	/**
	 * 在池纸票查询
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public List vtrustDetailProcess(List result,String user) throws Exception{
		ArrayList infoList = new ArrayList();
		if (result != null && result.size() > 0) {
			HashMap map = null;
			for (int i = 0; i < result.size(); i++) {
				map = new HashMap();
				DraftPool pool = (DraftPool) result.get(i);
				map.put("BILL_NO", pool.getAssetNb());// 票据号码
				map.put("BILL_CLASS", pool.getAssetType());// 票据种类
				map.put("SC_DFC_FLAG", ""); // 同城异地标识
				map.put("BILL_AMT", pool.getAssetAmt().setScale(2, BigDecimal.ROUND_DOWN));// 票据金额
				map.put("DRAW_DATE", DateUtils.toString(pool.getPlIsseDt(), "yyyyMMdd"));// 出票日
				map.put("EXPIRY_DATE", DateUtils.toString(pool.getPlDueDt(), "yyyyMMdd"));// 到期日
				map.put("ACCEPTOR_OPEN_BANK", pool.getPlAccptrSvcr());// 承兑人行号
				map.put("ACCEPTOR_OPENBANK_NAME", pool.getPlAccptrNm());// 承兑人名称
				
				map.put("PAYEE_NAME", pool.getPlPyeeNm());// 收款人名称
				map.put("PAYEE_ACCT_NO", pool.getPlPyeeAcctId());// 收款人账号
				map.put("PAYEE_OPENBANK_NAME", pool.getPlPyeeAcctSvcrNm());// 收款人开户行名称
				map.put("HOLDER_ACCT_NO", pool.getPoolBillInfo().getSOwnerAcctId());// 持有人账号
//				map.put("BUSI_NO", vPool.getId());// 业务明细ID
				map.put("KEEP_ARTICLES_BANK_NAME", "");// 代保管行名称
				
				if(pool.getRickLevel() == null || pool.getRickLevel().equals(PoolComm.NOTIN_RISK)){//不产生额度
					map.put("LIMIT_AMT", new BigDecimal("0"));// 额度金额
				}else {
					map.put("LIMIT_AMT", pool.getAssetAmt().setScale(2, BigDecimal.ROUND_DOWN));// 额度金额
				}
				
				map.put("INPOOL_DATE","");// 入池时间
				map.put("BILL_NAME", pool.getPlDrwrNm());// 出票人名称
				map.put("BILL_ACCT_NO", pool.getPlDrwrAcctId());// 出票人账号
				map.put("REMITTER_OPEN_BANK", pool.getPlDrwrAcctSvcr());// 出票人开户行行号
				map.put("BILL_OPENBANK_NAME", pool.getPlDrwrAcctSvcrNm());// 出票人开户行名称
				map.put("RISK_LEVE", pool.getRickLevel());// 风险等级
				map.put("RISK_FLAG", "");// 风险标识  ——未用
				map.put("RISK_FLAG_REMARK", "");// 风险标识说明
				
				map.put("DRAWER_GUARANTOR_NAME", "");// 出票保证人名称
				map.put("DRAWER_GUARANTOR_ADDRESS", "");// 出票保证人地址
				map.put("DRAWER_GUARANTEE_DATE", pool.getPoolBillInfo().getDrwrGuarnteeDt());// 出票保证时间
				map.put("ACCEPTANCE_GUARANTOR_NAME", "");// 承兑保证人名称
				map.put("ACCEPTANCE_GUARANTOR_ADDRESS", "");// 承兑保证人地址
				if(pool.getPoolBillInfo().getAccptrGuarnteeDt()!=null){
					/*Date acptGuDt = DateTimeUtil.parseYYYYMMDD(vPool.getAccptrGuarntrDt());*/
					map.put("ACCEPTANCE_GUARANTEE_DATE",pool.getPoolBillInfo().getAccptrGuarnteeDt());// 承兑保证时间
				}
				map.put("BILL_SAVE_ADDR", "");// 票据保管地
				map.put("OTHER_BANK_SAVE_ADDR", "");// 他行保管地址
				map.put("REMARK", "");// 备注
				
				if(pool.getPoolBillInfo().getSAcceptorDt()!=null){
					/*Date acceptDt = DateUtils.formatDate(vPool.getVtaccptrDate(), DateUtils.ORA_DATE_FORMAT);*/
					map.put("ACCE_DATE",DateUtils.toString(pool.getPoolBillInfo().getSAcceptorDt(), DateUtils.ORA_DATE_FORMAT));// 承兑日期
				}
				
				map.put("CONTRACT_NO", pool.getPoolBillInfo().getSContractNo());//交易合同号
				map.put("ACCEPTANCE_AGREE_NO", pool.getPoolBillInfo().getSAcceptorProto());//承兑协议号
				map.put("OPERATOR_NO", user);//柜员

				infoList.add(map);
			}
		}
		return infoList;
	}
	
	public List detailProcess(List result) throws Exception{
		List infoList = new ArrayList();
		if(result != null && result.size()>0){
			Map map = null;
			for (int i = 0; i < result.size(); i++) {
				 map = new HashMap();
				 PoolBillInfo billInfo = (PoolBillInfo) result.get(i);
				 	map.put("BILL_NO", billInfo.getSBillNo());// 票据号码
					map.put("BILL_CLASS", billInfo.getSBillType());// 票据种类
					map.put("SC_DFC_FLAG", ""); // 同城异地标识
					map.put("BILL_AMT", billInfo.getFBillAmount().setScale(2, BigDecimal.ROUND_DOWN));// 票据金额
					map.put("DRAW_DATE", DateUtils.toString(billInfo.getDIssueDt(), "yyyyMMdd"));// 出票日
					map.put("EXPIRY_DATE", DateUtils.toString(billInfo.getDDueDt(), "yyyyMMdd"));// 到期日
					map.put("ACCEPTOR_OPEN_BANK", billInfo.getSAcceptorBankCode());// 承兑人行号
					map.put("ACCEPTOR_OPENBANK_NAME", billInfo.getSAcceptor());// 承兑人名称
					
					map.put("PAYEE_NAME", billInfo.getSPayeeName());// 收款人名称
					map.put("PAYEE_ACCT_NO", billInfo.getSPayeeAccount());// 收款人账号
					map.put("PAYEE_OPENBANK_NAME", billInfo.getSPayeeBankName());// 收款人开户行名称
					map.put("HOLDER_ACCT_NO", billInfo.getSOwnerAcctId());// 持有人账号
//					map.put("BUSI_NO", billInfo.getId());// 业务明细ID
					map.put("KEEP_ARTICLES_BANK_NAME", "");// 代保管行名称
					
					if(billInfo.getRickLevel().equals(PoolComm.LOW_RISK)||billInfo.getRickLevel().equals(PoolComm.HIGH_RISK)){//风险等级是高低风险
						map.put("LIMIT_AMT", billInfo.getFBillAmount().setScale(2, BigDecimal.ROUND_DOWN));// 额度金额
					}else {
						map.put("LIMIT_AMT", new BigDecimal("0"));// 额度金额
					}
					
					map.put("INPOOL_DATE","");// 入池时间
					map.put("BILL_NAME", billInfo.getSIssuerName());// 出票人名称
					map.put("BILL_ACCT_NO", billInfo.getSIssuerAccount());// 出票人账号
					map.put("REMITTER_OPEN_BANK", billInfo.getSIssuerBankCode());// 出票人开户行行号
					map.put("BILL_OPENBANK_NAME", billInfo.getSIssuerBankName());// 出票人开户行名称
					map.put("RISK_LEVE", billInfo.getRickLevel());// 风险等级
					map.put("RISK_FLAG", "");// 风险标识  ——未用
					map.put("RISK_FLAG_REMARK", "");// 风险标识说明
					
					map.put("DRAWER_GUARANTOR_NAME", "");// 出票保证人名称
					map.put("DRAWER_GUARANTOR_ADDRESS", "");// 出票保证人地址
					map.put("DRAWER_GUARANTEE_DATE", billInfo.getDrwrGuarnteeDt());// 出票保证时间
					map.put("ACCEPTANCE_GUARANTOR_NAME", "");// 承兑保证人名称
					map.put("ACCEPTANCE_GUARANTOR_ADDRESS", "");// 承兑保证人地址
					if(billInfo.getAccptrGuarnteeDt()!=null){
						/*Date acptGuDt = DateTimeUtil.parseYYYYMMDD(billInfo.getAccptrGuarntrDt());*/
						map.put("ACCEPTANCE_GUARANTEE_DATE",billInfo.getAccptrGuarnteeDt());// 承兑保证时间
					}
					map.put("BILL_SAVE_ADDR", "");// 票据保管地
					map.put("OTHER_BANK_SAVE_ADDR", "");// 他行保管地址
					map.put("REMARK", "");// 备注
					
					if(billInfo.getSAcceptorDt()!=null){
						/*Date acceptDt = DateUtils.formatDate(billInfo.getVtaccptrDate(), DateUtils.ORA_DATE_FORMAT);*/
						map.put("ACCE_DATE",DateUtils.toString(billInfo.getSAcceptorDt(), DateUtils.ORA_DATE_FORMAT ));// 承兑日期
					}
					
					map.put("CONTRACT_NO", billInfo.getSContractNo());//交易合同号
					map.put("ACCEPTANCE_AGREE_NO", billInfo.getSAcceptorProto());//承兑协议号

				infoList.add(map);
			}
		}
		return infoList;
	}
}
