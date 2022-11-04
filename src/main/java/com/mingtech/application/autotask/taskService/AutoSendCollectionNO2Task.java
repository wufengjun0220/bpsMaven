package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.*;

import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetFactory;
import com.mingtech.application.pool.common.domain.CpesBranch;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 获取提示付款结果
 * 
 * @author Administrator
 * 配置为 每日上午16点自动执行 
 * com.mingtech.application.autotask.taskService.AutoSendCollectionApplyTask
 */
public class AutoSendCollectionNO2Task  extends AbstractAutoTask {

	private static final Logger logger = Logger
	.getLogger(AutoSendCollectionNO2Task.class);
	PoolCreditProductService productService = PoolCommonServiceFactory.getPoolCreditProductService();
	BlackListManageService blackListManageService = PoolCommonServiceFactory.getBlackListManageService();
	PoolCreditClientService poolCreditClientService = PoolCommonServiceFactory.getPoolCreditClientService();

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		
		ConsignService consignService = ConsignServiceFactory.getConsignService();
		PoolEcdsService poolEcdsService = PoolCommonServiceFactory.getPoolEcdsService();
		DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
		AssetRegisterService assetRegisterService = PoolCommonServiceFactory.getAssetRegisterService();

		List statusList = new ArrayList();
		statusList.add(PoolComm.TS00); //已发提示付款申请
		
		List consingSendList = consignService.getCollectionSendByStatus(statusList,null);
		
		if(consingSendList != null && consingSendList.size() > 0) {
//			Iterator it = consingSendList.iterator();
			CollectionSendDto sendDto = null;
			String billId = "";
			List billList = new ArrayList();
			Map map = new HashMap();
			int a = 0;
			logger.info("已发提示付款申请数组长度为["+consingSendList.size()+"]");
			for (int i = 0; i < consingSendList.size(); i++) {
				sendDto =(CollectionSendDto)consingSendList.get(i);
				if(StringUtils.isBlank(sendDto.getDraftSource()) || sendDto.getDraftSource().equals(PoolComm.CS01)){
					map.put(sendDto.getSBillNo(),sendDto );
					billId = billId + "|" +sendDto.getPoolBillInfo().getDiscBillId();
				}
				if(a == 40){
					logger.info("第["+i+"]次分割id存入数组,id为["+billId+"]");
					billId = billId.substring(1,billId.length());
					billList.add(billId);
					billId = "";
					a = 0;
				}
				a++;
			}
			logger.info("票据ID数组长度为["+billList.size()+"]");
			for (int k = 0; k < billList.size(); k++) {
				logger.info("第["+k+"]次循环查询提示付款结果");
				String id = (String) billList.get(k);
				/**
				 * 发送提示付款结果查询接口，设置状态为"TS_02":提示付款申请已拒绝,"TS_03":* 提示付款签收完毕 *"TS_04":提示付款驳回 
				 * 
				 */
				ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
				poolTransNotes.setBillId(id);//票据id
				poolTransNotes.setTransType("1");//业务类型 申请类
				poolTransNotes.setStatusCode("2020|2021");//交易代码
				
				poolTransNotes.setAcctNo("0");//申请方账号
				
				logger.info("票据ID为["+id+"]的票据,发送提示付款签收结果查询开始");
				ReturnMessageNew response = poolEcdsService.txApplyQueryBusinessBatch2(poolTransNotes);
				logger.info("发送提示付款签收结果查询结束,返回的文件有["+response.getDetails().size()+"]条");
				List list = response.getDetails();
				for (int i = 0; i < consingSendList.size(); i++) {
					sendDto =(CollectionSendDto)consingSendList.get(i);
					for (int j = 0; j<list.size(); j++) {
						Map Sendmap = (Map) list.get(j);
						String billNo = getStringVal(Sendmap.get("billNo"));//得到返回文件的票号
						String status = getStringVal(Sendmap.get("status"));
						if(sendDto.getSBillNo().equals(billNo)){//确保多次拒绝情况下能够取到最新的数据
							CollectionSendDto send =(CollectionSendDto) map.get(billNo);//根据票号得到CollectionSendDto

							PoolQueryBean poolQueryBean = new PoolQueryBean();
							poolQueryBean.setBillNo(sendDto.getSBillNo());
							poolQueryBean.setSStatusFlag(PoolComm.DS_06);
							DraftPool pool=consignService.queryDraftByBean(poolQueryBean).get(0);
							PoolBillInfo bill = pool.getPoolBillInfo();
							if(status.equals("TE202001_02")||"TE202101_02".equals(status)){//签收
								send.setClearWay(getStringVal(Sendmap.get("onlineMark")));//线上线下清算标志		1线上 0线下
								send.setBankFlag(getStringVal(Sendmap.get("bankFlag")));//是否我行承兑标志
								send.setSBillStatus(PoolComm.TS03);
								send.setLastOperTm(DateUtils.getWorkDayDate());
								send.setLastOperName("托收自动任务:托收已签收");
								bill.setSDealStatus(PoolComm.DS_07);
							}else if(status.equals("TE202001_03")||"TE202101_03".equals(status)||"TE202011_02".equals(status)
									||"TE202013_02".equals(status)||"TE202015_02".equals(status)
									||"TE202113_02".equals(status)||"TE202115_02".equals(status)){//签收失败或者拒绝
								pool.setAssetStatus(PoolComm.DS_02);//状态改为已入池票据
								send.setSBillStatus(PoolComm.TS02);
								send.setLastOperTm(DateUtils.getWorkDayDate());
								send.setLastOperName("托收自动任务:托收签收失败或拒绝");
								bill.setSDealStatus(PoolComm.TS02);;//提示付款拒绝
								pool.setLastOperTm(DateUtils.getWorkDayDate());
								pool.setLastOperName("自动托收过程,获取提示付款结果,拒绝后改为在池");
								bill.setLastOperTm(DateUtils.getWorkDayDate());
								bill.setLastOperName("自动托收过程,获取提示付款结果,付款已拒绝");
							}
							consignService.txStore(send);
							consignService.txStore(pool);
							consignService.txStore(bill);
							break ;
						}
					}
					
				}
			}
			
			
		}
		return new BooleanAutoTaskResult(true);
	}
	
	/**
	 * 拆分票据出池  大小票置换占用处理
	 * @param out
	 * @param pedCredit
	 * @return
	 * @throws Exception
	 */
	public boolean toPJE027(List sendList,PoolBillInfo billInfo) throws Exception{
		
		/**
		 * 保贴额度占用对象需通过未拆分之前票据对象查询
		 */
		PoolQueryBean pBean = new PoolQueryBean();
		pBean.setProtocolNo(billInfo.getPoolAgreement());
		pBean.setBillNo(billInfo.getSBillNo());
		
		pBean.setBeginRangeNo(billInfo.getBeginRangeNo());
		pBean.setEndRangeNo(billInfo.getEndRangeNo());
		PedGuaranteeCredit pedCredit = productService.queryByBean(pBean);
		
		if(null!=pedCredit && PoolComm.SP_01.equals(pedCredit.getStatus())){//占用成功	
			List resuList = new ArrayList();//保存需要发送信贷接口的数据
			
			CreditTransNotes creditNotes = new CreditTransNotes();
			BigDecimal billAmt = billInfo.getFBillAmount();//票面金额
			String billType = billInfo.getSBillType();//票据类型
			String bankNo = billInfo.getSAcceptorBankCode();//承兑行行号
			String totalBankNo = billInfo.getAcptHeadBankNo();//承兑行行号--总行   二代支付行号
			String totalBankName = billInfo.getAcptHeadBankName();//承兑行行名--总行
			String acceptor = billInfo.getSAcceptor();//承兑人全称
			String acptAcctNo = billInfo.getSAcceptorAccount();//承兑人账号
			Map rsuMap = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
			
			
			
			for (int j = 0; j < sendList.size(); j++) {
				CollectionSendDto dto = (CollectionSendDto) sendList.get(j);
				Map resuMap = new HashMap();
				
				if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
					totalBankNo ="";
					totalBankName = acceptor;
				}
				resuMap.put("BILL_INFO_ARRAY.BANK_NO", totalBankNo);//二代支付行号
				if(rsuMap != null){
					resuMap.put("BILL_INFO_ARRAY.CLINET_NAME", (String)rsuMap.get("guarantDiscName"));//客户名称
					
					resuMap.put("BILL_INFO_ARRAY.CLIENT_NO", (String)rsuMap.get("guarantDiscNo"));//客户编号
					
				}else{
					resuMap.put("BILL_INFO_ARRAY.CLINET_NAME", totalBankName);//客户名称
				}
				
				
				resuMap.put("BILL_INFO_ARRAY.BILL_NO", dto.getSBillNo()+"-"+dto.getBeginRangeNo()+"-"+dto.getEndRangeNo());//票号
				resuMap.put("BILL_INFO_ARRAY.OPERATION_TYPE", "02");//操作类型 释放
				resuMap.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT01");//票据拆分类型  BT01-拆分前票据  BT02-拆分后入库票 BT03-拆分后出库票
				resuMap.put("BILL_INFO_ARRAY.BILL_AMT", dto.getFBillAmount());//票据金额
				resuMap.put("BILL_INFO_ARRAY.LIMIT_NO", "");//额度编号
				resuMap.put("BILL_INFO_ARRAY.LIMIT_TYPE", "SX0060201910040060");//额度类型
				resuList.add(resuMap);
				
			}
			
			creditNotes.setReqList(resuList);//上传文件
			ReturnMessageNew resp = poolCreditClientService.txPJE027(creditNotes);
			if(resp.isTxSuccess()){
				return true;
			}else{
				logger.info("额度转换占用接口处理失败："+ resp.getRet().getError_MSG());
				return false;
			}
		}
		return true;
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

	
}
