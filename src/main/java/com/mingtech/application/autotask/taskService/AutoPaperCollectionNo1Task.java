package com.mingtech.application.autotask.taskService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.CorePdraftColl;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.draft.service.MisDraftService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 纸票发托日终自动任务
 * @Description 日终数据平台会从核心获取托收登记簿中的数据放入CorePdraftColl表，若票据在该表中有数据，则表示已发出托收，已发出托收的纸票视同已回款，需要处理掉额度。
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-23
 */
public class AutoPaperCollectionNo1Task extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoPaperCollectionNo1Task.class);
	
	PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolQueryService draftPoolQueryService = PoolCommonServiceFactory.getDraftPoolQueryService();
	DepartmentService departmentService = DepartmentServiceFactory.getDepartmentService();	
	ConsignService consignService = ConsignServiceFactory.getConsignService();
	MisDraftService misDraftService =  PoolCommonServiceFactory.getMisDraftService();
	AssetRegisterService assetRegisterService = PoolCommonServiceFactory.getAssetRegisterService();
	FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	PedAssetPoolService pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();


	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

	/**
	 * (1)查询pl_pool表中到期日十天以内的【在池】纸票
	 * (2)根据上一步查出的票号检索CORE_PDRAFT_COLL表，若有数据，则处理掉额度，改为托收已回款
	 * (4)托收表进行记录及同步状态
	 */
	@Override
	public BooleanAutoTaskResult run() throws Exception {
		
		logger.info("纸票发托日终自动任务开始............................");
		
		/*
		 * (1)查询pl_pool表中到期日十天以内的【在池】及【托收处理中】纸票
		 */
		List<DraftPool> list = consignService.getPaperCollection();
		
		List<DraftPool> releList = new ArrayList<DraftPool>();//需要释放额度的，即需要从资产登记表中删除的票据信息 
		
		if(list != null && list.size()>0){
			
			logger.info("得到十日内到期的的纸票有："+list.size()+"张");
			
			for (DraftPool pool : list) {
				
				logger.info("得到发托纸票【"+pool.getAssetNb()+"】票据信息");
				/*
				 * (2)根据上一步查出的票号检索CORE_PDRAFT_COLL表，若有数据，则表示该票据已发出托收，将额度扣减
				 */
				CorePdraftColl coll = draftPoolQueryService.getPdraftCollByBill(pool.getAssetNb());
				if(coll != null){
					logger.info("纸票【"+pool.getAssetNb()+"】已登记到核心登记簿中，已发托。可进行托收处理");	
					
					/*
					 * 托收表数据处理
					 */
					PoolBillInfo bill = null;//大票表对象
					CollectionSendDto collSendTemp = consignService.loadSendDtoByBillNo(pool.getAssetNb(),null,null);
					if(collSendTemp ==null){						
						bill = draftPoolInService.loadByBillNo(pool.getAssetNb(),null,null);
						Department dept = departmentService.getDepartmentByBankNo(bill.getSAcceptorBankCode());
						PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, pool.getPoolAgreement(), null, null, null);
						collSendTemp = this.doCollectionPross(dept, dto, collSendTemp, bill, pool);
					}
					
					if(PoolComm.DS_03.equals(pool.getAssetStatus())){//如果该票据在出池处理中（网银预约取票），则将包含该票据的预约批次均取消取票状态
						
						PoolQueryBean queryBean = new PoolQueryBean();
						queryBean.setSBatchNo(bill.getpOutBatchNo());	
						queryBean.setSStatusFlag("1");
						queryBean.setIsPoolOutEnd(PoolComm.OUT_01);
						this.outBatchProcess(queryBean);
						
					}
					
					/*
					 * 额度系统额度处理
					 */
					Ret ret  = new Ret();
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					
					if(pool.getBtFlag() != null && pool.getBtFlag().equals(PoolComm.SP_01)){//占用成功释放保贴额度
						logger.info("纸票【"+pool.getAssetNb()+"】额度系统额度释放处理..");	
						/*
						 * 额度系统额度处理
						 */
						ret = misDraftService.releaseMisEdu(pool);
					}
					if(Constants.TX_SUCCESS_CODE.equals(ret.getRET_CODE())){
						
						bill.setSDealStatus(PoolComm.TS05);//大票表--记账完
						pool.setAssetStatus(PoolComm.TS05);//pl_pool表--记账完
						pool.setLastOperTm(new Date());
						pool.setLastOperName("纸票发托日终自动任务");
						pool.getPoolBillInfo().setLastOperTm(new Date());
						pool.getPoolBillInfo().setLastOperName("纸票发托日终自动任务,记账完成");
						collSendTemp.setSBillStatus(PoolComm.TS05);//托收表--记账完
						
						logger.info("对象赋值完毕！开始保存数据！");
						/*
						 * (3)托收表进行记录及同步状态
						 */
						pedProtocolService.txStore(bill);//大票表
						pedProtocolService.txStore(pool);//pl_pool表
						pedProtocolService.txStore(collSendTemp);//托收表
						
						
						logger.info("纸票【"+pool.getAssetNb()+"】自动托收额度系统额度处理成功,准备处理额度!");
						releList.add(pool);

					}else{
						logger.error("纸票["+pool.getAssetNb()+"]托收回款保贴额度释放失败，额度系统返回信息："+ret.getRET_MSG());
						logger.info("纸票["+pool.getAssetNb()+"]托收回款保贴额度释放失败,额度系统返回信息："+ret.getRET_MSG());
						continue;
					}
					
					
				}
			}
			
			/*
			 * （4）资产登记表处理
			 */
			
			List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>();//需要重新计算额度的协议 
			if(releList.size()>0){
				for(DraftPool draft : releList){	
					
					logger.info("已托纸票资产登记表处理，票号【"+draft.getAssetNb()+"】");
					
					PedProtocolDto	pro = pedProtocolService.queryProtocolDto( null, null,  draft.getPoolAgreement(),null, null, null);
					
					//资产登记表处理
					assetRegisterService.txDraftStockOutAssetChange(pro.getPoolAgreement(), draft,draft.getTradeAmt(), PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
					
					if(!proList.contains(pro)){
						proList.add(pro);
					}
					
				}
			}
			
			/*
			 * (5)重新计算发生额度变动的票据池的额度列表
			 */
			
			if(null!=proList && proList.size()>0){
				for(PedProtocolDto dto : proList){
					
					logger.info("纸票托收处理完成资产登记表之后，重新计算该票据池的额度，处理票据池编号【"+ dto.getPoolAgreement()+"】");
					
					//核心同步保证金并重新计算池额度信息
					financialService.txBailChangeAndCrdtCalculation(dto);

					AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
					
					//解锁AssetPool表，并重新计算该表数据
					pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(ap.getApId(),true); 
				}
				
			}
			
			
		}
		logger.info("纸票发托日终自动任务结束............................");
		
		return new BooleanAutoTaskResult(true);
	}
	
	/**
	 * 托收表对象处理
	 * @Description TODO
	 * @author Ju Nana
	 * @param dept
	 * @param dto
	 * @param collSendTemp
	 * @param bill
	 * @param dfPool
	 * @throws Exception
	 * @date 2019-7-5上午11:26:55
	 */
	private CollectionSendDto doCollectionPross(Department dept,PedProtocolDto dto,CollectionSendDto collSendTemp,PoolBillInfo bill,DraftPool dfPool) throws Exception{
		if(collSendTemp == null ){
			collSendTemp = new CollectionSendDto();
			//大票信息
			collSendTemp.setPoolBillInfo(bill);//大票信息
			collSendTemp.setSBillNo(bill.getSBillNo());//票号
			collSendTemp.setFBillAmount(bill.getFBillAmount());//票面金额
			collSendTemp.setDIssueDt(bill.getDIssueDt());//出票日
			collSendTemp.setDDueDt(bill.getDDueDt());//到期日
			collSendTemp.setSBillMedia(bill.getSBillMedia());//票据介质
			collSendTemp.setSBillType(bill.getSBillType());//票据类型
			//承兑方
			collSendTemp.setAcceptNm(bill.getSAcceptor());//承兑方名称
			collSendTemp.setAcceptAccount(bill.getSAcceptorAccount());//承兑方帐号
			collSendTemp.setAcceptAcctSvcr(bill.getSAcceptorBankCode());//承兑方行号
			collSendTemp.setAcceptBankName(bill.getSAcceptorBankName());//承兑方开户行名称
			//提示付款信息
			collSendTemp.setApplDt(DateUtils.getWorkDayDate());//提示付款日期
			collSendTemp.setAmt(bill.getFBillAmount());//提示付款金额
			collSendTemp.setGuaranteeNo(dfPool.getGuaranteeNo());//担保编号
			
			collSendTemp.setSBranchId(dto.getOfficeNet());//存储网点号  用于权限分配
			
			collSendTemp.setBpsNo(dto.getPoolAgreement());//票据池编号
			collSendTemp.setAccptrOrg(dfPool.getAccptrOrg());//承兑人组织机构代码
			//判断是否为本行票据
			logger.info("根据承兑行行号["+bill.getSAcceptorBankCode()+"]查询部门信息开始");
			dept = departmentService.getDepartmentByBankNo(bill.getSAcceptorBankCode());
			if(dept!=null){
				logger.info("查询部门信息结束,部门信息不为空");
				//提示付款人（或逾期提示付款人=银行信息）信息
				collSendTemp.setCollNm(dept.getName());//提示付款人(或逾期)名称
				collSendTemp.setCollCmonId(dept.getOrgCode());//提示付款人(或逾期)组织机构代码
				collSendTemp.setCollAcct("0");//提示付款人(或逾期)帐号
				collSendTemp.setCollAcctSvcr(dept.getBankNumber());//提示付款人(或逾期)大额行号
			}
			collSendTemp.setAccNo(dfPool.getAccNo());//电票签约账号
		}
		return collSendTemp;
		
	}
	
	/**
	 * 将含有到期托收票据的预约取票批次取消
	 * @author Ju Nana
	 * @param queryBean
	 * @throws Exception
	 * @date 2019-7-23下午7:57:11
	 */
	private void outBatchProcess(PoolQueryBean queryBean) throws Exception{
		
		PlPdraftBatch draftBatch = null;
		List<PoolBillInfo>   billList = new ArrayList<PoolBillInfo>();
		List<DraftPool>   draftList = new ArrayList<DraftPool>();
		
		List<PlPdraftBatch> batchList =  draftPoolQueryService.queryPlPdraftBatchByBatch(queryBean);
		if(batchList!=null&&batchList.size()>0){
			draftBatch = batchList.get(0);
			draftBatch.setStatus("0");//预约取票状态置为无效
			draftPoolQueryService.txStore(draftBatch);

			PoolQueryBean bean = new PoolQueryBean();
			bean.setSBatchNo(draftBatch.getBatchNo());
			List<PoolBillInfo> infotList = draftPoolQueryService.queryPoolBillInfoByPram(bean);
			if(infotList!=null){
				for(PoolBillInfo bill:infotList){

					//大票表置为在池
					bill.setSDealStatus(PoolComm.DS_02);
					bill.setLastOperTm(new Date());
					bill.setLastOperName("纸票发托日终自动任务操作");
					billList.add(bill);

					//pl_pool表置为在池

					PoolQueryBean poolQueryBean = new PoolQueryBean();
					poolQueryBean.setBillNo(bill.getSBillNo());
					
					poolQueryBean.setBeginRangeNo(bill.getBeginRangeNo());
					poolQueryBean.setEndRangeNo(bill.getEndRangeNo());
					
					poolQueryBean.setSStatusFlag(PoolComm.DS_03);
					DraftPool draft=consignService.queryDraftByBean(poolQueryBean).get(0);
					draft.setAssetStatus(PoolComm.DS_02);
					draft.setLastOperTm(new Date());
					draft.setLastOperName("纸票发托日终自动任务操作");
					
					draftList.add(draft);
				}
				draftPoolQueryService.txStoreAll(billList);
				draftPoolQueryService.txStoreAll(draftList);
				
				
				/*
				 * 重新生成额度
				 */

				//资产登记表处理
								
				List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>();//需要重新计算额度的协议 
				if(draftList.size()>0){
					for(DraftPool draft : draftList){	
						
						logger.info(" 将含有到期托收票据的预约取票批次取消,重新产生额度资产登记表处理，票号【"+draft.getAssetNb()+"】");
						
						PedProtocolDto	pro = pedProtocolService.queryProtocolDto( null, null,  draft.getPoolAgreement(),null, null, null);
						
						//资产登记表处理
						assetRegisterService.txBillAssetRegister(draft,pro);
						
						if(!proList.contains(pro)){
							proList.add(pro);
						}
						
					}
				}
				
				//重新计算发生额度变动的票据池的额度列表
				
				if(proList.size()>0){
					for(PedProtocolDto dto : proList){
						
						logger.info("纸票托收处理完成资产登记表之后，重新计算该票据池的额度，处理票据池编号【"+ dto.getPoolAgreement()+"】");
						
						//核心同步保证金并重新计算池额度信息
						financialService.txBailChangeAndCrdtCalculation(dto);

						AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
						
						//解锁AssetPool表，并重新计算该表数据
						pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(ap.getApId(),true); 
					}
					
				}

			}
		}

	}

}
