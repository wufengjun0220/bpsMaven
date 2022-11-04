package com.mingtech.application.autotask.taskService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;

/**		目前提示付款记账暂时不使用
 * 自动发起提示付款申请服务
 * 
 * @author Administrator
 * 配置为 每日上午10点自动执行 
 * 0 0 10 ? * *
 * com.mingtech.application.autotask.taskService.AutoSendCollectionApplyTask
 */
public class AutoSendCollectionNO3Task  extends AbstractAutoTask {

	private static final Logger logger = Logger.getLogger(AutoSendCollectionNO3Task.class);
	ConsignService consignService = ConsignServiceFactory.getConsignService();
	DepartmentService departmentService = DepartmentServiceFactory.getDepartmentService();
	PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	PoolCoreService poolcoreService = PoolCommonServiceFactory.getPoolCoreService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();
	PoolCreditClientService poolCreditClientService = PoolCommonServiceFactory.getPoolCreditClientService();
	AssetRegisterService assetRegisterService = PoolCommonServiceFactory.getAssetRegisterService();
	FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	PedAssetPoolService pedAssetPoolService = PoolCommonServiceFactory.getPedAssetPoolService();
	PoolCreditProductService productService = PoolCommonServiceFactory.getPoolCreditProductService();
	AutoTaskPublishService autoTaskPublishService = PoolCommonServiceFactory.getAutoTaskPublishService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		
		/*
		 * （1）查询提示付款签收完毕的票据
		 */
		List statusList = new ArrayList();
		statusList.add(PoolComm.TS03); //提示付款签收完毕
		List consingSendList = consignService.getCollectionSendByStatus(statusList,null);
		
		if(consingSendList != null && consingSendList.size() > 0 ){
			
			logger.info("可发送托收记账的电票有["+consingSendList.size()+"]张");
			
			
			List<DraftPool> releList = new ArrayList<DraftPool>();//需要释放额度的，即需要从资产登记表中删除的票据信息 
			
			Iterator it = consingSendList.iterator();
			while(it.hasNext()){
				try {
					CollectionSendDto sendDto =(CollectionSendDto)it.next();
					
					logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度处理...");
										
					/*
					 * （2）发送核心提示付款记账 
					 */
					
					logger.info("托收电票["+sendDto.getSBillNo()+"]核心系统记账处理...");
					
					CoreTransNotes core = new CoreTransNotes();
					core.setAmtPay(BigDecimalUtils.setScale(sendDto.getFBillAmount()).toString());//支付金额
					if(sendDto.getAcctFlowNo() != null && !"".equals(sendDto.getAcctFlowNo())){
						core.setDevSeqNo(sendDto.getAcctFlowNo());//第三方流水号
					}else{
						String str = poolBatchNoUtils.txGetFlowNo();
						core.setDevSeqNo(str);//第三方流水号
						sendDto.setAcctFlowNo(str);//保存流水号
						consignService.txStore(sendDto);
					}
					
					if(sendDto.getSBillMedia().equals("1")){
						core.setIssWay("0");//不是电票
					}else{
						core.setIssWay("1");
					}
					
					PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, sendDto.getBpsNo(), null, null, null);
					core.setAccNoPye(dto.getMarginAccount());//保证金账号
					if(sendDto.getClearWay().equals("1")){//清算标志	电票返回：1、线上  2、线下   核心需要的：1、线上  0、线下
						core.setFlgWay("1");
					}else{
						core.setFlgWay("0");
					}
					core.setColNo(sendDto.getGuaranteeNo());//担保编号
					core.setBilCode(sendDto.getSBillNo());//票号
					
					/********************融合改造新增 start******************************/
					if(StringUtils.isNotBlank(sendDto.getDraftSource()) && !sendDto.getDraftSource().equals(PoolComm.CS01)){
						core.setBeginRangeNo(sendDto.getBeginRangeNo());
						core.setEndRangeNo(sendDto.getEndRangeNo());
					}
					
					/**
					 * 判断是否做过拆分
					 */
					boolean flag = false;
					PoolBillInfo info = sendDto.getPoolBillInfo();
					if(StringUtils.isNotEmpty(info.getSplitId())){
						//已做拆分，需送入库担保编号及出库担保编号
						String inColNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);//流水号
						String OutcolNo = poolBatchNoUtils.txGetCuarNoBySession("P",6);//流水号
						core.setInColNo(inColNo);//入库担保编号
						core.setOutColNo(OutcolNo);//出库担保编号
						flag = true;
					}
					
					core.setColNo(sendDto.getGuaranteeNo());//担保编号
					
					
					/********************融合改造新增 end******************************/
					
					
					//管理机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
					if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
						core.setBrcNo(dto.getCreditDeptNo());//融资机构号
					}else{
						core.setBrcNo(dto.getOfficeNet());//受理网点
					}
					Department ment = departmentService.getDepartmentByInnerBankCode(core.getBrcNo());
					//交易机构赋值
					core.setBrcNo(ment.getAuditBankCode());//报文头里的机构号
					core.setIsTerm(sendDto.getBankFlag());//是否我行承兑标志
					
					/*
					 * 记账接口处理
					 */
					ReturnMessageNew response = poolcoreService.PJH580311Handler(core);
					
					/**
					 * 融合改造针对可拆分的新票：如果是线下已清算的票据，不去核心记账（业务人员手工记账）
					 */
					
					if(response.isTxSuccess()){//记账成功
						
						if(flag){
							//若做过拆分数据，保存未记账的票据的担保品编号
							PoolBillInfo InBillInfo = draftPoolInService.loadBySplit(info.getSplitId(), "DS_06");
							
							CollectionSendDto sendDtoIn = consignService.queryCollectionSendByStatus(InBillInfo.getSBillNo(), InBillInfo.getBeginRangeNo(), InBillInfo.getEndRangeNo());
							sendDtoIn.setGuaranteeNo(core.getInColNo());
							consignService.txStore(sendDto);
							
						}
						
						/*
						 * （3）记账成功后进行额度处理
						 */						
						
						logger.info("托收电票["+sendDto.getSBillNo()+"]核心系统记账成功...");
						
						sendDto.setSBillStatus(PoolComm.TS05);//记账成功
						sendDto.setLastOperTm(new Date());
						sendDto.setLastOperName("自动托收:记账成功");
						consignService.txStore(sendDto);
						
						PoolQueryBean poolQueryBean = new PoolQueryBean();
						poolQueryBean.setBillNo(sendDto.getSBillNo());
						poolQueryBean.setBeginRangeNo(sendDto.getBeginRangeNo());
						poolQueryBean.setEndRangeNo(sendDto.getEndRangeNo());
						poolQueryBean.setSStatusFlag(PoolComm.DS_06);
						DraftPool pool=consignService.queryDraftByBean(poolQueryBean).get(0);
						if(pool != null){
							PoolBillInfo bill = pool.getPoolBillInfo();
							bill.setSDealStatus(PoolComm.TS05);
							pool.setAssetStatus(PoolComm.TS05);//记账成功
							pool.setLastOperTm(DateUtils.getWorkDayDate());
							pool.setLastOperName("自动托收过程,提示付款核心记账成功");
							bill.setLastOperTm(DateUtils.getWorkDayDate());
							bill.setLastOperName("自动托收过程,提示付款核心记账成功");
							consignService.txStore(bill);
							consignService.txStore(pool);
							
							/*
							 * 记账成功后，释放已用额度，即从资产登记表中删除数据
							 */
							
							logger.info("托收电票["+sendDto.getSBillNo()+"]核心系统记账成功");
							
							releList.add(pool);
							
							/*
							 * 记账成功,释放保贴额度 
							 */
							if(pool.getBtFlag() != null && pool.getBtFlag().equals(PoolComm.SP_01)){//占用额度系统额度的的票据进行额度释放
								
								logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度释放...");
								
								Map resuMap = new HashMap();
								List<Map> reqList = new ArrayList<Map>();//实际为单条
								CreditTransNotes creditNotes = new CreditTransNotes();
								
								if(bill.getSplitFlag().equals("1")){//可拆分的等分化票据
									resuMap.put("billNo", sendDto.getSBillNo()+"-"+sendDto.getBeginRangeNo()+"-"+sendDto.getEndRangeNo());
								}else{
									resuMap.put("billNo", sendDto.getSBillNo());
								}
								
								reqList.add(resuMap);
								creditNotes.setReqList(reqList);//上传文件
								
								ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
								
								if(response1.isTxSuccess()){
									pool.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
									PoolQueryBean pBean = new PoolQueryBean();
									pBean.setProtocolNo(pool.getPoolAgreement());
									pBean.setBillNo(pool.getAssetNb());
									
									pBean.setBeginRangeNo(pool.getBeginRangeNo());
									pBean.setEndRangeNo(pool.getEndRangeNo());
									
									PedGuaranteeCredit pedCredit = productService.queryByBean(pBean);
									pedCredit.setStatus(PoolComm.SP_00);
									pedCredit.setCreateTime(DateUtils.getWorkDayDate());
									consignService.txStore(pedCredit);
									consignService.txStore(pool);
									logger.info("托收电票["+sendDto.getSBillNo()+"]额度系统额度释放成功...");
									
								}
							}
							
							
						}
						
					}

				} catch (Exception e) {
					logger.info("电票托收记账失败：",e);
					logger.error("电票托收记账失败：",e);
					continue;
				}
			}
			
			/*
			 * 资产登记表处理
			 */
			
			List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>();//需要重新计算额度的协议 
			if(releList.size()>0){
				for(DraftPool draft : releList){				
				
					logger.info("已托收电票资产登记表处理，票号【"+draft.getAssetNb()+"】");
					
					PedProtocolDto	pro = pedProtocolService.queryProtocolDto( null, null,  draft.getPoolAgreement(),null, null, null);
					
					//资产登记表处理
					assetRegisterService.txDraftStockOutAssetChange(pro.getPoolAgreement(), draft,draft.getTradeAmt(), PublicStaticDefineTab.STOCK_OUT_TYPE_DUE);
					
					if(!proList.contains(pro)){
						proList.add(pro);
					}
					
				}
			}
			
			/*
			 * 重新计算发生额度变动的票据池的额度列表
			 */
			
			if(proList.size()>0){
				

				for(PedProtocolDto dto : proList){
					
					logger.info("电票托收处理完成资产登记表之后，调度任务重新计算该票据池的额度，处理票据池编号【"+ dto.getPoolAgreement()+"】");
					
					String bpsNo = dto.getPoolAgreement();
					String proId = dto.getPoolInfoId();
					String   id = 	bpsNo +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成
				    Map<String,String> reqParams = new HashMap<String,String>();
				    reqParams.put("proId", proId);
					autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_CALCU_NO, id, AutoTaskNoDefine.BUSI_TYPE_CAL, reqParams, bpsNo, bpsNo, null, null);

				}
				
			}
		}
		
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}


	
}
