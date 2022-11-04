package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.FileName;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.AutoTaskNoDefine;

/**
 * @Description 在线流贷业务申请（经办/复核）
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC071RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC071RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        String operationType = "";
        String apId=null;
        try {
        	//【1】参数解析
        	OnlineQueryBean queryBean = QueryParamMap(request);
        	List details = queryBean.getDetalis();
        	if(null != details && details.size()>0){
        		
        		//【2】创建在线流贷批次、明细对象
        		OnlineQueryBean returnBean = pedOnlineCrdtService.createOnlineCrdt(queryBean);
        		operationType = queryBean.getOperationType();

        		//锁AssetPool表
        		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(returnBean.getOperationType())){
        			PedProtocolDto dto=returnBean.getPool();
            		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
            	    apId = ap.getApId();
            		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
            		if(!isLockedSucss){//加锁失败
            			ret.setRET_CODE(Constants.EBK_11);
            			ret.setRET_MSG("票据池任务处理中，请稍后再试！");
            			response.setRet(ret);
            			logger.info("票据池【"+dto.getPoolAgreement()+"】上锁！");
            			return response;
            		}
        		}

        		//【3】业务校验
        		response = pedOnlineCrdtService.txCrdtFullCheck(returnBean);
        		
        		//【3】复核岗业务落库及任务处理  
        		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(operationType)){
        		
        			if(Constants.TX_SUCCESS_CODE.equals(response.getRet().getRET_CODE())){//业务校验成功
        				
        				logger.info("在线流贷业务复核落库及任务发布操作...");
        				//协议已用额度修改
        				PedOnlineCrdtProtocol pro = returnBean.getCrdtPro();
        				pro.setUsedAmt(pro.getUsedAmt().add(queryBean.getLoanAmt()));//已用累加发送的业务金额
        				pedOnlineCrdtService.txStore(pro);
        				
        			  //保存在线流贷合同信息及支付计划
        			  List<PlOnlineCrdt> list = returnBean.getCrdtBatchList();
        			  List<PlCrdtPayPlan> payList  = returnBean.getPayList();
        			  financialAdviceService.txForcedSaveList(list);
        			  String crdtId="";
        			  for(PlOnlineCrdt crdt : list){
        				  if(PublicStaticDefineTab.PAY_2.equals(crdt.getPayType())){
        					  crdtId=crdt.getId();
        				  }
        			  }
        			  for(PlCrdtPayPlan detail:payList){
              			detail.setCrdtId(crdtId);
          			  }
        			  financialAdviceService.txForcedSaveList(payList);	
        			  
        			  
        			  for(PlOnlineCrdt crdt:list){
        				//自动任务发布 
        			      autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_CRDT_NO, crdt.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_CRDT, null, crdt.getContractNo(), crdt.getBpsNo(), null, null);
        			  }
        			}   
        			//解锁
            		pedAssetPoolService.txReleaseAssetPoolLock(apId);
        		}

        		
        		if(null != response.getDetails() && response.getDetails().size()>0){
        			String path = FileName.getFileNameClient(request.getTxCode())+".txt";
        			response.getFileHead().put("FILE_FLAG", "2");
        			response.getFileHead().put("FILE_PATH", path);
        			response.getFileHead().put("DELIMITOR", "|");
        		}else{
        			response.getFileHead().put("FILE_FLAG", "0");
        		}

        	}
        } catch (Exception e) {    	
        	if(null!=apId){//解锁	
        		pedAssetPoolService.txReleaseAssetPoolLock(apId);
        	}
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线流贷业务申请（经办/复核）异常,请联系票据池系统！");
			response.setRet(ret);
		}
        if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(operationType)){
        	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        	ret.setRET_MSG(response.getRet().getRET_MSG());
			response.setRet(ret);
			response.getRet().getRET_MSG();
        }
        return response;
    }

	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
		OnlineQueryBean queryBean = new OnlineQueryBean();
		Map map = request.getBody();
		queryBean.setOnlineCrdtNo(getStringVal(map.get("ONLINE_LOAN_NO")));//在线流贷编号
		queryBean.setLoanAmt(getBigDecimalVal(map.get("LOAN_TOTAL_AMT")));//流贷总金额
		queryBean.setDueDate(getDateVal(map.get("LOAN_EXPIRY_DATE")));//流贷到期日
		queryBean.setElctrncSign(getStringVal(map.get("E_SIGN")));//电子签名
		queryBean.setOperationType(getStringVal(map.get("OPERATION_TYPE")));//操作类型
		queryBean.setOnlineLoanTotal(getBigDecimalVal(map.get("LOAN_TOTAL_AMT")));//流贷总金额
		queryBean.setLprRate(getBigDecimalVal(map.get("LPR_RATE")));//基准利率
		
		//支付计划OnlineLoanTotal
		List list = request.getDetails();
		if(null != list && list.size()>0){
			PlCrdtPayPlan plan = null;
			for(int i=0;i<list.size();i++){
				plan = new PlCrdtPayPlan();
				Map detail = (Map) list.get(i);
				plan.setSerialNo(getStringVal(detail.get("SERIAL_NO")));//序号
				plan.setTotalAmt(getBigDecimalVal(detail.get("PAY_AMT")));//支付金额
				plan.setDeduAcctName(getStringVal(detail.get("PAYEE_CLIENT_NAME")));//收款人名称
				plan.setDeduAcctNo(getStringVal(detail.get("PAYEE_ACCT_NO")));//收款人账号
				plan.setDeduBankCode(getStringVal(detail.get("PAYEE_BANK_NO")));//收款人开户行行号
				plan.setDeduBankName(getStringVal(detail.get("PAYEE_BANK_NAME")));//收款人开户行行名
				plan.setOnlineCrdtNo(getStringVal(map.get("ONLINE_LOAN_NO")));//在线流贷编号
				plan.setCreateDate(new Date());
				
				//区分支付方式
				if(PublicStaticDefineTab.ZI_ZHU_ZHI_FU.equals(getStringVal(detail.get("PAYEE_CLIENT_NAME")))){
					queryBean.setSelfPay(true);
				}else{
					queryBean.setEntrustedPay(true);
				}
				queryBean.getDetalis().add(plan);
				
				//获取收款人剩余可用额
   				if(!queryBean.getMap().containsKey(plan.getDeduAcctName())){
   					queryBean.setPayeeAcctName(plan.getDeduAcctName());
   					queryBean.setPayeeAcctNo(plan.getDeduAcctNo());
   					queryBean.setPayeeStatus(PublicStaticDefineTab.STATUS_1);
   					queryBean.setPayeeStatus(PublicStaticDefineTab.STATUS_1);
   					queryBean.setOnlineNo(queryBean.getOnlineCrdtNo());
   					List<OnlineQueryBean> payees = pedOnlineCrdtService.queryOnlineCrdtPayeeListByBean(queryBean, null);
   					System.out.println(payees);
   					if(null != payees && payees.size()>0){
   						logger.info("收款人名称："+plan.getDeduAcctName());
   						if(plan.getDeduAcctName().equals("自主支付")){
   							queryBean.getMap().put(plan.getDeduAcctName(), payees.get(0).getPayeeTotalAmt().subtract(payees.get(0).getPayeeUsedAmt()!=null?payees.get(0).getPayeeUsedAmt():new BigDecimal(0)));
   						}else{
   							queryBean.getMap().put(plan.getDeduAcctNo(), payees.get(0).getPayeeTotalAmt().subtract(payees.get(0).getPayeeUsedAmt()!=null?payees.get(0).getPayeeUsedAmt():new BigDecimal(0)));
   						}
   						System.out.println(queryBean.getMap());
   					}
   				}
				
			}
		}

		
		return queryBean;
	}

}
