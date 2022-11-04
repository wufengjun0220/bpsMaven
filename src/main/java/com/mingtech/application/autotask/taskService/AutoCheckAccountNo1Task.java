package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.edu.domain.PedCheckList;
import com.mingtech.framework.common.util.DateTimeUtil;

/**
 * 票据池对账任务
 * @Description 每日生成票据池对账批次及明细信息
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-12
 */
public class AutoCheckAccountNo1Task  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoCheckAccountNo1Task.class);
	
	PedProtocolService pedProtocolService =  PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolQueryService draftPoolQueryService = PoolCommonServiceFactory.getDraftPoolQueryService();
    PoolEBankService poolEBankService = PoolCommonServiceFactory.getPoolEBankService();
    PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();


    /**每日对账文件生成规则
     *（1）查询出协议表中已签约单户票据池融资协议的的客户及已签约集团融资协议的出质人信息
	 *（2）从大票表中查询出如上客户名下的在池票据
	 *（3）将查出的信息存入ped_check_batch及ped_check_info表中
     */
	public BooleanAutoTaskResult run() throws Exception {
		
		List<PedProtocolDto> protocol =  pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null, null, null);
		
		if(protocol!=null && protocol.size()>0){
			logger.info("票据池日终对账文件生成开始，本次合计生成【"+protocol.size()+"】个票据池的对账信息。");
			
			for(PedProtocolDto pro : protocol){
				/*
				 * 若担保合同失效，则不推送
				 */
				Date contractDueDt = pro.getContractDueDt();
				long today = new Date().getTime();
				if(contractDueDt == null || contractDueDt.getTime()<=today){
					continue;
				}
				
				
				if(PoolComm.YES.equals(pro.getIsGroup())){//集团票据池
					ProListQueryBean bean = new ProListQueryBean();
					bean.setStatus(PoolComm.PRO_LISE_STA_01);//已签约状态
					bean.setBpsNo(pro.getPoolAgreement());				
					List<String> custIdentityList = new ArrayList<String>();
					custIdentityList.add(PoolComm.KHLX_01);
					custIdentityList.add(PoolComm.KHLX_03);
					bean.setCustIdentityList(custIdentityList);

					List<PedProtocolList> pedproLists = pedProtocolService.queryProListByQueryBean(bean);
					if(pedproLists!=null){						
						for (PedProtocolList proMem : pedproLists ) {
							//判断成员身份 出质人才有对账
							if(proMem.getRole().equals(PoolComm.JS_01)){//主户	
								//用票据池编号及客户号查询
								this.getCoreAccount(pro);
							}else {
								//分户查询该客户下的
								this.getCheckAccount(pro, proMem);
							}
						}
					}
					
				}else{//单户票据池
					
					this.getCoreAccount(pro);
					
				}
			}
		}
		logger.info("票据池日终对账文件生成结束............................");
		
		
		/*
		 * 若为月末，则生成月底对账文件
		 */
		String workDay = DateTimeUtil.getWorkday_YYYY_MM_DD();
		boolean isLastDayOfMonth = DateTimeUtil.isLastDayOfMonth(workDay);
		if(isLastDayOfMonth){
			pedProtocolService.flush();
			this.CreateMonthCheck();
		}
		
		
		
		
		
		
		return new BooleanAutoTaskResult(true);
	}
	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	
	/**
	 * 单户及集团主户对账记录生成
	 * @param pro
	 * @param queryBean
	 * @throws Exception
	 */
	public void getCoreAccount(PedProtocolDto pro) throws Exception{
		PoolQueryBean queryBean = new PoolQueryBean();
		queryBean.setSStatusFlag(PoolComm.DS_02);//已入池状态
		queryBean.setCustomernumber(pro.getCustnumber());//核心客户号
		queryBean.setProtocolNo(pro.getPoolAgreement());//票据池编号
		List<PoolBillInfo> billList = draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
		
		PedCheckBatch batch = new PedCheckBatch();
		BigDecimal billTotalAmount = BigDecimal.ZERO;
		String batchNo = "";
		batchNo = poolBatchNoUtils.txGetCuarNoBySession("CH",10);
		String pledgeNo = poolBatchNoUtils.txGetCuarNoBySession(pro.getPoolAgreement(),6);
		
		if(billList!=null && billList.size()>0){
			//生成批次号
			for(PoolBillInfo bill : billList){
				
				PedCheckList info = new PedCheckList();
				info.setBatchNo(batchNo); //批次号
				info.setBillType(bill.getSBillType());//票据类型
				info.setSbillMedia(bill.getSBillMedia());//票据介质
				info.setBillNo(bill.getSBillNo());//票据号码
				
				/********************融合改造新增 start******************************/
				info.setBeginRangeNo(bill.getBeginRangeNo());//票据号码起
				info.setEndRangeNo(bill.getEndRangeNo());//票据号码止
				info.setPledgeNo(pledgeNo);
				/********************融合改造新增 end******************************/

				info.setBillAmount(bill.getFBillAmount());//票面金额
				info.setIsseDt(bill.getDIssueDt());//出票日
				info.setDueDt(bill.getDDueDt());//到期日
				info.setAccptrNm(bill.getSAcceptor());//承兑人名称
				this.draftPoolQueryService.txStore(info);
				billTotalAmount = billTotalAmount.add(bill.getFBillAmount());
			}
			
			batch.setBatchNo(batchNo);//批次号
			batch.setBillTotalNum(billList.size()+"");//票据总张数
			batch.setBillTotalAmount(billTotalAmount);//票据总金额
		}else{
			batch.setBatchNo("");//批次号
			batch.setBillTotalNum("0");//票据总张数
			batch.setBillTotalAmount(new BigDecimal("0.00"));//票据总金额
		}
		
		batch.setPoolAgreement(pro.getPoolAgreement());//票据池编号
		batch.setPoolAgreementName(pro.getPoolName());//票据池名称
		batch.setCustNo(pro.getCustnumber());//客户号（出质人）
		batch.setCustName(pro.getCustname());//客户名称（出质人姓名）
		batch.setCollztnBkNm(pro.getOfficeNetName());//质权人--签约的网点机构名称
		batch.setIsGroup(pro.getIsGroup());//是否集团
		batch.setAccountDate(DateTimeUtil.getWorkday());//账务日期
		batch.setMarginAccount(pro.getMarginAccount());//保证金账号
		batch.setMarginAccountName(pro.getMarginAccountName());//保证金账号名称
		batch.setCurTime(DateTimeUtil.workday_append_time());//创建时间
		
		
		batch.setPledgeNo(pledgeNo);
		
		// 查询资产数据
		Map map = poolEBankService.queryMarginBalance(pro.getCustnumber(),pro.getPoolAgreement());
		batch.setMarginBalance(new BigDecimal(map.get("AVAL_BALANCE")+""));//保证金余额
		this.draftPoolQueryService.txStore(batch);
		
		logger.info("票据池对账文件生成结束");
	}

	/**
	 * 生成集团分户的对账记录
	 * @param pedProtocolList
	 * @param bean
	 * @throws Exception
	 */
	public void getCheckAccount(PedProtocolDto dto ,PedProtocolList pro) throws Exception{
		PoolQueryBean queryBean = new PoolQueryBean();
		queryBean.setSStatusFlag(PoolComm.DS_02);//已入池状态
		queryBean.setCustomernumber(pro.getCustNo());//核心客户号
		queryBean.setProtocolNo(dto.getPoolAgreement());
		
		List<PoolBillInfo> billList = draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
		
		PedCheckBatch batch = new PedCheckBatch();
		BigDecimal billTotalAmount = BigDecimal.ZERO;
		String batchNo = "";//批次号
		batchNo = poolBatchNoUtils.txGetCuarNoBySession("CH",10);
		String pledgeNo = poolBatchNoUtils.txGetCuarNoBySession(dto.getPoolAgreement(),6);
		
		if(billList!=null && billList.size()>0){
			
			for(PoolBillInfo bill : billList){
				PedCheckList info = new PedCheckList();
				info.setBatchNo(batchNo); //批次号
				info.setBillType(bill.getSBillType());//票据类型
				info.setSbillMedia(bill.getSBillMedia());//票据介质
				info.setBillNo(bill.getSBillNo());//票据号码
				/********************融合改造新增 start******************************/
				info.setBeginRangeNo(bill.getBeginRangeNo());//票据号码起
				info.setEndRangeNo(bill.getEndRangeNo());//票据号码止
				info.setPledgeNo(pledgeNo);
				/********************融合改造新增 end******************************/

				info.setBillAmount(bill.getFBillAmount());//票面金额
				info.setIsseDt(bill.getDIssueDt());//出票日
				info.setDueDt(bill.getDDueDt());//到期日
				info.setAccptrNm(bill.getSAcceptor());//承兑人名称
				this.draftPoolQueryService.txStore(info);
				billTotalAmount = billTotalAmount.add(bill.getFBillAmount());
			}
			
			batch.setBatchNo(batchNo);//批次号
			batch.setBillTotalNum(billList.size()+"");//票据总张数
			batch.setBillTotalAmount(billTotalAmount);//票据总金额
		}else{
			batch.setBatchNo("");//批次号
			batch.setBillTotalNum("0");//票据总张数
			batch.setBillTotalAmount(BigDecimal.ZERO);//票据总金额
		}
		
		batch.setPoolAgreement(pro.getBpsNo());//票据池编号
		batch.setPoolAgreementName(pro.getBpsName());//票据池名称
		batch.setCustNo(pro.getCustNo());//客户号（出质人）
		batch.setCustName(pro.getCustName());//客户名称（出质人姓名）
		batch.setCollztnBkNm(dto.getOfficeNetName());//质权人--签约的网点机构名称
		batch.setIsGroup(dto.getIsGroup());//是否集团
		batch.setAccountDate(DateTimeUtil.getWorkday());//账务日期
		batch.setCurTime(DateTimeUtil.workday_append_time());//创建时间
		
		batch.setPledgeNo(pledgeNo);
		this.draftPoolQueryService.txStore(batch);
		
		logger.info("票据池对账文件生成结束");
	}
	
	/**
	 * 月末对账任务生成
	 * @author Ju Nana
	 * @throws Exception
	 * @date 2019-8-12下午8:14:20
	 */
	private void CreateMonthCheck() throws Exception{
		logger.info("票据池月末对账文件生成开始............................");	
		
		PoolQueryBean queryBean = new PoolQueryBean();
		queryBean.setAccDate(DateTimeUtil.getWorkday());
		List<PedCheckBatch> batchList = draftPoolQueryService.queryCheckBatchByPram(queryBean);
		int i = 0 ;
		if(batchList!=null && batchList.size()>0){
			for(PedCheckBatch batch : batchList ){
				i++;
				PedCheck check = new PedCheck();
				check.setBatchNo(batch.getBatchNo());//批次号
				check.setPoolAgreement(batch.getPoolAgreement());//票据池编号
				check.setPoolAgreementName(batch.getPoolAgreementName());//票据池名称
				check.setCustNo(batch.getCustNo());//客户号（出质人）
				check.setCustName(batch.getCustName());//客户名称（出质人姓名）
				check.setCollztnBkNm(batch.getCollztnBkNm());//质权人
				check.setIsGroup(batch.getIsGroup());//是否集团
				check.setAccountDate(batch.getAccountDate());//账务日期
				check.setPledgeNo(batch.getPledgeNo());
				
				String marginAcc = null;
				String marginName = null;
				BigDecimal marginbalance = null;
				if(batch.getMarginAccount()!=null && batch.getMarginAccount().trim()!=""){					
					marginAcc = batch.getMarginAccount();
					marginName = batch.getMarginAccountName();
					marginbalance = batch.getMarginBalance();
				}else{
					marginAcc = batch.getMarginAccount();
					marginName = batch.getMarginAccountName();
					marginbalance = BigDecimal.ZERO;
				}
				check.setMarginAccount(marginAcc);//保证金账号
				check.setMarginAccountName(marginName);//保证金账号名称
				check.setMarginBalance(marginbalance);//保证金余额
				
				check.setMarginAccount(batch.getMarginAccount());//保证金账号
				check.setMarginAccountName(batch.getMarginAccountName());//保证金账号名称
				check.setMarginBalance(batch.getMarginBalance());//保证金余额
				
				
				check.setBillTotalNum(batch.getBillTotalNum());//票据总张数
				check.setBillTotalAmount(batch.getBillTotalAmount());//票据总金额
				
				String checkResult = "";
				if("0".equals(batch.getBillTotalNum())&& marginbalance.compareTo(BigDecimal.ZERO)!=0){//保证金金额小于等于0且出质票据总金额为0，则无需对账，默认为核对无误
					checkResult = PoolComm.DZJG_01;//对账结果--核对一致 
				}else{
					checkResult = PoolComm.DZJG_00;//对账结果--未对账 
				}
				
				check.setCheckResult(checkResult);//对账结果
				check.setCurTime(DateTimeUtil.workday_append_time());//创建时间
				check.setIsAuto(PoolComm.YES);
				draftPoolQueryService.txStore(check);
				
			}
		}
		logger.info("票据池月末对账文件生成结束............................");	 
	}

	
}
