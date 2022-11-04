package com.mingtech.application.autotask.taskdispatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

public  class OnlineReleseAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(OnlineReleseAutoTaskDispatch.class);
	private PedOnlineAcptService pedOnlineAcptService =PoolCommonServiceFactory.getPedOnlineAcptService();
	private PedOnlineCrdtService pedOnlineCrdtService =PoolCommonServiceFactory.getPedOnlineCrdtService();
	private FinancialService financialService = PoolCommonServiceFactory.getFinancialService();
	private CreditRegisterService creditRegisterService = PoolCommonServiceFactory.getCreditRegisterService();


	/**
	 *<p>在线业务额度释放<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 *@描述 
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){

		Map<String,String> resultMap = new HashMap<String,String>();
		String busiId = autoTaskExe.getBusiId();//业务id
		String busiType = reqParams.get("busiType");//业务类型 1：银承 2:流贷
		String type = reqParams.get("type");//类型 1：批次 2:明细
		String isCredit = reqParams.get("isCredit");//是否释放信贷额度 0：否 1:是
		
		/*《注意》：
		 *    需求中自动任务的  执行时间  和具体内容
		 *    先判断各种额度是否已释放，有些地方可以手工点击去直接释放额度，而不是经过自动任务
		 *    释放额度的时候，怎么确定每种额度之前没有释放，这个要控制，可以通过状态
		 *    释放信贷额度后才释放借据、合同
		 *    先锁业务表
		 * */
		try {
			if(StringUtils.isNotBlank(busiType)){
				if(PublicStaticDefineTab.PRODUCT_001.equals(busiType)){//银承业务
					//出账时：1.释放在线银承协议银承已用额度、票据池担保合同额度、票据池低风险额度、收票人额度，然后将扣除失败金额的通知MIS系统释放对应金额、批次总金额
					//未用退回： 1.“成功”状态银承明细，银承承兑未用退回，次日日间和次日日终，释放在线银承额度、收票人额度、票据池额度、票据池担保合同额度,不通知信贷
					//          2.“成功”状态银承明细，银承承兑未用退回，出账当日日间，1)释放在线银承额度、收票人额度、票据池低风险额度、票据池担保合同额度，并通知信贷系统释放。
					//若ESB反馈释放MIS处理成功，该笔借据、合同任务状态是“失败”，任务终结
					//借据(也有未生成借据的，银承业务完成才会生成借据)
					//合同(次日在线银承的合同项下借据发起未用退回，日终时取回银承票据状态，合同金额不发生变化)
					if(StringUtils.isNotBlank(type)){
						if(type.equals("2")){//明细
							PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(busiId,PlOnlineAcptDetail.class);
							detail.setTaskDate(new Date());
							PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(detail.getAcptBatchId(), PlOnlineAcptBatch.class);
							PedOnlineAcptProtocol pro = pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());//在线银承协议
							if(!PublicStaticDefineTab.ACPT_DETAIL_012.equals(detail.getStatus())){
								try {
									//根据借据号查询资产登记表资产信息ID
									List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getContractNo(),detail.getLoanNo(), PoolComm.VT_1);
									List<String> releseIds = new ArrayList<String>();
									if(null!=crList&&!crList.isEmpty()){
										String id = crList.get(0).getBusiId();
										releseIds.add(id);
									}
									
									/*
									 * 池额度释放
									 */
									financialService.txOnlineBusiReleseCredit(releseIds, detail.getBpsNo());
									detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_002_2);
									logger.error(detail.getBillNo()+"池额度释放成功...");
									
									/*
									 * 通知信贷释放额度
									 * 统一到银承业务明细状态及发生未用退回时金额统计
									 */
									/*if(!"0".equals(isCredit)){
										//信贷额度
										ReturnMessageNew result = pedOnlineAcptService.txPJE021Handler(batch, "1", PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE, null);
										if(!result.isTxSuccess()){
											detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
											pedOnlineAcptService.txStore(detail);
											resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
											resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
											logger.error(batch.getBatchNo()+"信贷额度释放失败...");
											return resultMap;
										}
									}*/
									
									/*
									 * 合同、借据
									 */
//								if(PublicStaticDefineTab.ACPT_DETAIL_010_1.equals(detail.getStatus())){
//									onlineCommonService.txHandlerProductAndDetail(detail.getLoanNo(), detail.getBillAmt(), "1");
//								}else{
//									onlineCommonService.txHandlerProductAndDetail(detail.getLoanNo(), detail.getBillAmt(), "2");
//								}
									pro.setUsedAmt(pro.getUsedAmt().subtract(detail.getBillAmt()));//已用金额恢复:已用金额 - 本次业务金额
									detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
									pedOnlineAcptService.txStore(detail);
									pedOnlineAcptService.txStore(pro);
									
								} catch (Exception e) {
									pedOnlineAcptService.txStore(detail);
									logger.error("序列号："+detail.getBillSerialNo()+"在线银承业务单笔额度释放异常！");
									resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
									resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
									return resultMap;
								}
							}
							
						}else{//批次
							PlOnlineAcptBatch batch = (PlOnlineAcptBatch) pedOnlineAcptService.load(busiId,PlOnlineAcptBatch.class);
							PedOnlineAcptProtocol pro = pedOnlineAcptService.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());//在线银承协议
							List details = pedOnlineAcptService.queryOnlineAcptDetailByBatchId(busiId);
							if(!PublicStaticDefineTab.ACPT_BATCH_007.equals(batch.getStatus())){
								try {
									//根据借据号查询资产登记表资产信息ID
									List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(batch.getContractNo(),null, PoolComm.VT_1);
									List<String> releseIds = new ArrayList<String>();
									if(null!=crList&&!crList.isEmpty()){
										for(CreditRegister register :crList){
											releseIds.add(register.getBusiId());
										}
									}
									/*
									 * 额度释放操作
									 * 100%保证金业务不占用票据池额度，不做票据池额度释放
									 */
									if(new BigDecimal(100).compareTo(pro.getDepositRatio()) != 0){
										financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
										batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_002_2);
									}
									/*
									 * 信贷额度释放
									 * 信贷额度接口新规则：上送原业务金额、出账失败金额（核心未记账）、未用退回金额（核心记账后的未用退回）
									 */
									if(!"0".equals(isCredit)){
										ReturnMessageNew result = pedOnlineAcptService.misRepayAcptPJE028(batch);
										if(!result.isTxSuccess()){
											resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
											resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
											logger.error(batch.getBatchNo()+"信贷额度释放失败...");
											return resultMap;
										}
									}
									List<PlOnlineAcptDetail> list = pedOnlineAcptService.queryOnlineAcptDetailByBatchId(batch.getId());
									if(list != null){
										for(PlOnlineAcptDetail detail:list){
											pro.setUsedAmt(pro.getUsedAmt().subtract(detail.getBillAmt()));//已用金额恢复:已用金额 - 本次业务金额
											detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
											detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
											pedOnlineAcptService.txStore(detail);
											pedOnlineAcptService.txStore(pro);
										}
									}
									batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_007);
									batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
									pedOnlineAcptService.txStore(batch);
									
								} catch (Exception e) {
									pedOnlineAcptService.txStore(batch);
									logger.error("批次号"+batch.getBatchNo()+"在线银承业务批量额度释放异常！");
									resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
									resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
									return resultMap;
								}
							}
						}
						
					}else{
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+"：未告知处理业务类型（整批或者单笔）");
						logger.error("无法区分处理业务类型（整批或者单笔），自动任务调度执行失败...");
						return resultMap;
					}
					
				}else{//流贷业务
					//出账失败：释放在线银承协议银承已用额度、票据池担保合同额度、票据池低风险额度、收票人额度，然后将扣除失败金额的通知MIS系统释放对应金额
					//出账失败，ESB未反馈，票据池该笔借据、合同状态是“处理中”，按如下流程处理：
					//1.自动任务：轮询方式4次（每半小时一次，2小时前的任务不再处理）。（票据池识别自己的状态停留超过5分钟，进入固定时点每30分钟轮询队列，日初8：30第一次，日终19：45加一次处理）运营截止时间设置晚上为19：30
					//2.手工驱动：通过企业网银或票据池系统手工点“同步合同状态”/“同步借据状态”按钮同步任务状态，票据池系统调查证交易，处理方式：
					//   1.若核心未记账，票据池将该借据、合同置为失败，并记录失败原因。先释放在线流贷协议流贷额度、票据池担保合同额度、票据池低风险额度、收款人额度（若有），然后实时通知MIS系统释放
					//若ESB反馈释放MIS处理失败或ESB未反馈，票据池将该借据置为“失败：未释放额度“，合同任务状态置为“失败”，采取如下流程处理：
					//1.自动任务：轮询方式4次（每半小时一次，2小时前的任务不再处理）。（票据池识别自己的状态停留超过5分钟，进入固定时点每30分钟轮询队列，日初8：30第一次，日终19：45加一次处理）运营截止时间设置晚上为19：30
					//2.手工驱动通知MIS系统释放对应金额
					PlOnlineCrdt batch = (PlOnlineCrdt) pedOnlineCrdtService.load(busiId, PlOnlineCrdt.class);
					batch.setTaskDate(new Date());
					PedOnlineCrdtProtocol pro = pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(batch.getOnlineCrdtNo());
					try {
						List<String> releseIds = new ArrayList<String>();
						releseIds.add(batch.getId());
						/*
						 * 额度释放操作
						 */
						financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
						
						/*
						 * 信贷额度释放
						 */
						ReturnMessageNew result = pedOnlineCrdtService.txPJE021(batch, "2",PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
						if(!result.isTxSuccess()){
							resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
							resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
							logger.error(batch.getBatchNo()+"信贷额度释放失败...");
							return resultMap;
						}
						pro.setUsedAmt(pro.getUsedAmt().subtract(batch.getLoanAmt()));//已用金额恢复:已用金额 - 本次业务金额
						pedOnlineCrdtService.txStore(pro);
						
					} catch (Exception e) {
						pedOnlineCrdtService.txStore(batch);
						logger.error("在线流贷业务额度释放异常！",e);
						resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
						resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
						return resultMap;
					}
				}
				
				//释放本地额度
				//mis额度
				//释放在线银承协议银承已用额度、票据池担保合同额度、票据池低风险额度、收票人额度，然后将扣除失败金额的通知MIS系统释放对应金额（类似于信贷系统合同生效后取消）
				//“成功”状态银承明细，银承承兑未用退回，次日日间和次日日终，释放在线银承额度、收票人额度、票据池额度、票据池担保合同额度
				//“成功”状态银承明细，银承承兑未用退回，出账当日日间，1)释放在线银承额度、收票人额度、票据池低风险额度、票据池担保合同额度，并通知信贷系统释放。
				//判断本地额度是否释放
				//判断mis额度是否释放
				//借据(也有未生成借据的，银承业务完成才会生成借据)
				//合同
				//银承明细：处理状态“失败”  释放额度失败或ESB未反馈   失败：未释放额度
				
				//如果信贷释放额度失败，处理状态为失败：未释放额度
				//判断批次下的所有明细都已执行完毕
			}else{
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+"：未告知在线业务类型");
				logger.error("无法区分业务类型，自动任务调度执行失败...");
				return resultMap;
			}
			
		} catch (Exception e) {
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+"：未告知在线业务类型");
			logger.error("【在线业务额度释放】调度任务执行异常：",e);
			return resultMap;
		}
		
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("在线业务额度释放自动任务调度执行完毕...");
		return resultMap;
	}
}
