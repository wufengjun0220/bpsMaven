package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.base.service.PoolQueryService;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.PlPdraftBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;

/**
 * PJC044(网银接口)纸票出入池申请
 * @author wu fengjun
 * @data 2019-6-20
 *
 */
public class PJC044RequestHandler extends PJCHandlerAdapter{
	
	private static final Logger logger = Logger.getLogger(PJC044RequestHandler.class);
	@Autowired
	private ConsignService consignService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private PoolQueryService poolQueryService;
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private AssetRegisterService assetRegisterService;
	
	/**
	 * 
	 * 出池
	 * ②得到操作类型
	 * 若新增	存放pl_pdraft_batch(纸票出入池批次表),批次号存放大票表,校验额度是否可出池,若可以释放票据占用的额度(额度处理),大票表修改状态DS_03
	 * 若修改	批次信息修改保存,旧信息的大票表删除批次号,修改状态为DS_02,更新额度,校验额度是否可出池,若可以释放票据占用的额度(额度处理),大票表修改状态DS_03
	 * 若删除	批次对象置为失效,大票表删除批次号,修改状态位DS_02
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
		ReturnMessageNew response = new ReturnMessageNew();
		Map body = request.getBody();
		List list = request.getDetails();
		List<String> billList = new ArrayList<String>();//记录票号的List
		Ret ret = new Ret();
		String apId = "";
		try {
			
			/*
			 * 字段整理
			 */
			
			String custNo = getStringVal(body.get("CORE_CLIENT_NO"));//核心客户号
			String bpsNo = getStringVal(body.get("BPS_NO"));//票据池编号
			String sing = getStringVal(body.get("E_SIGN"));//电子签名
			String batch = getStringVal(body.get("BATCH_NO"));//批次号
			String operaType = getStringVal(body.get("OPERATION_TYPE"));//操作类型
			String workerName = getStringVal(body.get("APP_NAME"));//经办人姓名
			String  workerPhone= getStringVal(body.get("APP_MOBIL"));//经办人手机号
			String workerCard = getStringVal(body.get("APP_IDENTITY_GLOBAL_ID"));//经办人身份证号
			String use = getStringVal(body.get("USAGE"));//用途
			String remark = getStringVal(body.get("REMARK"));//用途
			
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setPoolAgreement(bpsNo);
			PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			String CustName ="";//客户名称
			String orgCoge ="";//客户组织机构代码
			
			/*
			 * 池校验
			 */
			if(null==dto){
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("未找到该票据池信息！");
				response.setRet(ret);
				return response;
			}
			
			/*
			 * 融资状态校验
			 */
			if(!PoolComm.OPEN_01.equals(dto.getOpenFlag())){
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("该票据池未融资签约！");
				response.setRet(ret);
				return response;
			}
			
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
			CustName=dto.getCustname();
			orgCoge=dto.getCustOrgcode();
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
				CustName=mem.getCustName();
				orgCoge=mem.getOrgCoge();
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

			/********************融合改造新增 start******************************/
			List<String> assetNos = new ArrayList();
			Map<String,PoolBillInfo> changeBills= new HashMap<String, PoolBillInfo>();
			/********************融合改造新增 end******************************/
		
			/*
			 * 出池处理--新增
			 */
			
			if (operaType.equals("CZLX_01")) {//新增

				String batchNo = poolBatchNoUtils.txGetBatchNo("OUT", 8);//批次号

				PlPdraftBatch draftBatch = new PlPdraftBatch();				
				draftBatch.setBpsNo(bpsNo);
				draftBatch.setBpsName(dto.getPoolName());
				draftBatch.setCustNo(custNo);
				draftBatch.setCustName(CustName);
				draftBatch.setOrgCode(orgCoge);
				draftBatch.setElsignature(sing);
				draftBatch.setBatchType(operaType);
				draftBatch.setWorkerName(workerName);
				draftBatch.setWorlerPhoneNo(workerPhone);
				draftBatch.setWorkerId(workerCard);
				draftBatch.setTotalNum(new BigDecimal(list.size()));
				draftBatch.setBatchNo(batchNo);
				draftBatch.setUseWay(use);
				draftBatch.setRemark(remark);
				draftBatch.setWorkerId(workerCard);
				draftBatch.setStatus("1");
				draftBatch.setIsPoolOutEnd(PoolComm.OUT_01);
				
				BigDecimal Amt = new BigDecimal(0);
				
				/*
				 * 票号收集，金额汇总
				 */
				
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					String billNo = getStringVal(map.get("BILL_INFO_ARRAY.BILL_NO"));
					
					billList.add(billNo);
					
					/********************融合改造适应性修改 start******************************/
					PoolBillInfo pool = poolQueryService.queryObj(billNo,"0","0");
					Amt = Amt.add(pool.getFBillAmount()) ;
					
					String assNo = billNo +"-0" +"-0";
					assetNos.add(assNo);
					changeBills.put(assNo, pool);
					/********************融合改造适应性修改 end******************************/

				}
				draftBatch.setTotalAmt(Amt);
				
				
				/*
				 * 出池及额度处理
				 */
				Ret result = new Ret();
				PoolQueryBean param = new PoolQueryBean();//传递参数的bean
				param.setOutBatchNo(batchNo);
				param.setWorkerCard(workerCard);
				param.setWorkerName(workerName);
				param.setWorkerPhone(workerPhone);
				
				//出池操作
				result = poolEBankService.txApplyDraftPoolOutPJC003(billList, sing, dto,param,changeBills,assetNos);
				
				
				if (Constants.TX_SUCCESS_CODE.equals(result.getRET_CODE())) {
					
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("出池申请成功！");
					/*
					 * 保存批次表
					 */
					pedProtocolService.txStore(draftBatch);
					
				} else if(Constants.EBK_05.equals(result.getRET_CODE())){
					
					ret.setRET_CODE(Constants.EBK_05);
					ret.setRET_MSG("池额度不足不允许出池！");
					
				}else{
					
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("出池票据池系统处理异常！");
					
				}
				
				
			}
			
			/*
			 * 出池处理--修改
			 */
			
			if (operaType.equals("CZLX_02")) {//修改
				
				List<PoolBillInfo> infos = new ArrayList<PoolBillInfo>();
				List<DraftPool> draftList = new ArrayList<DraftPool>();
				List<AssetRegister> arList = new ArrayList<AssetRegister>();//资产登记列表
				
				PoolQueryBean query = new PoolQueryBean();
				query.setSBatchNo(batch);
				PlPdraftBatch draftBatch = (PlPdraftBatch) draftPoolQueryService.queryPlPdraftBatchByBatch(query).get(0);
				
				draftBatch.setBpsNo(bpsNo);
				draftBatch.setCustNo(custNo);
				draftBatch.setElsignature(sing);
				draftBatch.setBatchType(operaType);
				draftBatch.setWorkerName(workerName);
				draftBatch.setWorlerPhoneNo(workerPhone);
				draftBatch.setWorkerId(workerCard);
				draftBatch.setTotalNum(new BigDecimal(list.size()));
				draftBatch.setUseWay(use);
				draftBatch.setRemark(remark);
				
				BigDecimal Amt = new BigDecimal(0);
				//校验新发送的票据
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					String billNo = getStringVal(map.get("BILL_INFO_ARRAY.BILL_NO"));
					billList.add(billNo);
					
					/********************融合改造适应性修改 start******************************/
					PoolBillInfo pool = poolQueryService.queryObj(billNo,"0","0");
					Amt = Amt.add(pool.getFBillAmount()) ;
					String assNo = billNo +"-0" +"-0";
					assetNos.add(assNo);
					changeBills.put(assNo, pool);
					/********************融合改造适应性修改 end******************************/

				}
				draftBatch.setTotalAmt(Amt);
				
				
				//先查询到有批次号的票,修改为在池状态,去掉纸票批次号
				PoolQueryBean bean = new PoolQueryBean();
				bean.setSBatchNo(batch);
				List billInfos = draftPoolQueryService.queryPoolBillInfoByPram(bean);
				
				for (int i = 0; i < billInfos.size(); i++) {
					
					//获取大票表对象
					PoolBillInfo info = (PoolBillInfo) billInfos.get(i);
					//判断之前发送数据是否有修改之前的数据,若有则不做以下处理
					if(billList.contains(info.getSBillNo())){
						info.setWorkerCard(workerCard);
						info.setWorkerName(workerName);
						info.setWorkerPhone(workerPhone);
						infos.add(info);
						continue;
					}
					info.setpOutBatchNo("");
					info.setWorkerCard(workerCard);
					info.setWorkerName(workerName);
					info.setWorkerPhone(workerPhone);
					info.setSDealStatus(PoolComm.DS_02);
					info.setEbkLock(PoolComm.EBKLOCK_02);
					
					infos.add(info);
					
					
					//获取池票据表对象
					PoolQueryBean poolQueryBean = new PoolQueryBean();
					poolQueryBean.setBillNo(info.getSBillNo());
					poolQueryBean.setSStatusFlag(PoolComm.DS_03);
					DraftPool pool = consignService.queryDraftByBean(poolQueryBean).get(0);
					
					pool.setAssetStatus(PoolComm.DS_02);
					pool.setLastOperTm(new Date());
					pool.setLastOperName("网银预约出池,批次票据修改");
					
					draftList.add(pool);
					
					/*
					 * 重新生成额度：登记到资产表中
					 */
					String riskFlag = pool.getRickLevel();
					if(PoolComm.LOW_RISK.equals(riskFlag)||PoolComm.HIGH_RISK.equals(riskFlag)){			
						AssetRegister ar = assetRegisterService.txBillAssetRegister(pool, dto);
						arList.add(ar);
					}
					
				}
				
				
				
				
				
				PoolQueryBean param = new PoolQueryBean();//传递参数的bean
				param.setOutBatchNo(batch);
				param.setWorkerCard(workerCard);
				param.setWorkerName(workerName);
				param.setWorkerPhone(workerPhone);
				
				Ret result = poolEBankService.txApplyDraftPoolOutPJC003(billList, sing, dto,param,changeBills,assetNos);
				
				if (Constants.TX_SUCCESS_CODE.equals(result.getRET_CODE())) {
					/*
					 * 保存批次信息
					 */
					draftPoolQueryService.txStoreAll(infos);
					draftPoolQueryService.txStoreAll(draftList);
					pedProtocolService.txStore(draftBatch);
					
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("出池申请成功！");
					
				} else if(Constants.EBK_05.equals(result)){
					
					ret.setRET_CODE(Constants.EBK_05);
					ret.setRET_MSG("池额度不足不允许出池！");
					
					/*
					 * 若不成功，则删除上面登记到资产登记表的数据
					 */
					draftPoolQueryService.txDeleteAll(arList);
					
				}else{
					
					/*
					 * 若不成功，则删除上面登记到资产登记表的数据
					 */
					draftPoolQueryService.txDeleteAll(arList);
					
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据出池异常，请联系票据池系统！");
					
				}

				
			}
			
			/*
			 * 出池处理--删除
			 */
			
			if (operaType.equals("CZLX_03")) {//删除
				
				List<PoolBillInfo> infos = new ArrayList<PoolBillInfo>();
				List<DraftPool> draftList = new ArrayList<DraftPool>();
				
				PoolQueryBean query = new PoolQueryBean();
				query.setSBatchNo(batch);
				
				PlPdraftBatch draftBatch = (PlPdraftBatch) draftPoolQueryService.queryPlPdraftBatchByBatch(query).get(0);
				draftBatch.setStatus("0");
				draftPoolQueryService.txStore(draftBatch);
				
				//先查询到有批次号的票,修改为在池状态,去掉纸票批次号
				PoolQueryBean bean = new PoolQueryBean();
				bean.setSBatchNo(batch);
				List billInfos = draftPoolQueryService.queryPoolBillInfoByPram(bean);
				
				for (int i = 0; i < billInfos.size(); i++) {
					
					PoolBillInfo info = (PoolBillInfo) billInfos.get(i);
					info.setpOutBatchNo("");
					info.setWorkerCard(workerCard);
					info.setWorkerName(workerName);
					info.setWorkerPhone(workerPhone);
					info.setSDealStatus(PoolComm.DS_02);
					info.setEbkLock(PoolComm.EBKLOCK_02);
					
					infos.add(info);
					
					
					PoolQueryBean poolQueryBean = new PoolQueryBean();
					poolQueryBean.setBillNo(info.getSBillNo());
					poolQueryBean.setSStatusFlag(PoolComm.DS_03);
					DraftPool pool = consignService.queryDraftByBean(poolQueryBean).get(0);
					pool.setAssetStatus(PoolComm.DS_02);
					pool.setLastOperTm(new Date());
					pool.setLastOperName("网银预约出池,批次票据删除");
					
					draftList.add(pool);
					
					/*
					 * 重新生成额度：登记到资产表中
					 */
					String riskFlag = pool.getRickLevel();
					if(PoolComm.LOW_RISK.equals(riskFlag)||PoolComm.HIGH_RISK.equals(riskFlag)){			
						assetRegisterService.txBillAssetRegister(pool, dto);
					}
				}
				
				draftPoolQueryService.txStoreAll(infos);
				draftPoolQueryService.txStoreAll(draftList);
				
				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("删除成功！");
			}
			
			/*
			 * 解锁AssetPool表，并重新计算该表数据
			 */
			pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
			
			
		} catch (Exception e) {
			pedAssetPoolService.txReleaseAssetPoolLock(apId);//解锁
			ret.setRET_CODE(Constants.TX_FAIL_CODE);//
			ret.setRET_MSG("纸票出池票据池系统异常，请联系票据池系统!");
			logger.error("网银接口PJC044-纸票出池异常:"+e.getMessage(),e);
		}
		response.setRet(ret);
		return response;
	}
}
