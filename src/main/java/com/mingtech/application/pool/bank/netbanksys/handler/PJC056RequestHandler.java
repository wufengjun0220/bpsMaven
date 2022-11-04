package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.druid.util.StringUtils;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.FileName;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;

/**
 * @Description 在线银承业务申请（经办/复核）
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC056RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC056RequestHandler.class);
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService; 
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	
    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        response.setRet(ret);
        String operationType = "";
        String apId=null;
        try{
        	//【1】请求参数解析
        	OnlineQueryBean queryBean = QueryParamMap(request);
        	List details = queryBean.getDetalis();
        	if(null != details && details.size()>0){
        		//创建银承协议批次、明细对象
        		OnlineQueryBean returnBean = pedOnlineAcptService.createOnlineAcpt(queryBean);
        		operationType = queryBean.getOperationType();
        		
        		//锁AssetPool表
        		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(returnBean.getOperationType())&&new BigDecimal(100).compareTo(returnBean.getAcptPro().getDepositRatio())!=0){//非百分百保证金
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
        		
        		//【2】业务校验
        		response = pedOnlineAcptService.txAcptFullCheck(returnBean);
        		
        		//【4】复核岗业务处理  
        		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(operationType)){
        			if(Constants.TX_SUCCESS_CODE.equals(response.getRet().getRET_CODE())){//业务校验成功
        				//协议已用额度修改
        				PedOnlineAcptProtocol pro = returnBean.getAcptPro();
        				pro.setUsedAmt(pro.getUsedAmt().add(queryBean.getTotalAmt()));//已用累加发送的业务金额
        				pedOnlineAcptService.txStore(pro);
        				//保存
            			PlOnlineAcptBatch batch = returnBean.getAcptBatch();
            			List<PlOnlineAcptDetail> acptDetailList = returnBean.getList();
            			pedOnlineAcptService.txStore(batch);
            			List hisList=new ArrayList();
            			for(PlOnlineAcptDetail detail:acptDetailList){
                			detail.setAcptBatchId(batch.getId());
            			}
            			financialAdviceService.txStoreAll(acptDetailList);
            			//自动任务发布 
            			logger.info("银承推送自动任务发布开始！");
            			autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOL_ONLINE_ADD, batch.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_ADD, null, batch.getBatchNo(), batch.getBpsNo(), null, null);
            			logger.info("银承推送自动任务发布成功！");
        			}
        			//解锁
        			if(null != apId){        				
        				pedAssetPoolService.txReleaseAssetPoolLock(apId);
        			}
        		}
        		if(null != response.getDetails() && response.getDetails().size()>0){
        			String path = FileName.getFileNameClient(request.getTxCode())+".txt";
        			response.getFileHead().put("FILE_FLAG", "2");
        			response.getFileHead().put("FILE_PATH", path);
        			response.getFileHead().put("DELIMITOR", "|");
        		}else{
        			response.getFileHead().put("FILE_FLAG", "0");
        		}
				response.getBody().put("ONLINE_ACPT_NO", queryBean.getOnlineAcptNo());
        	}
        }catch (Exception e) {
        	if(null!=apId){//解锁	
    		   pedAssetPoolService.txReleaseAssetPoolLock(apId);
        	}
        	logger.error("PJC056-在线银承业务申请!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池内部处理异常！");
			response.setRet(ret);
		    return response;
		}
        if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(operationType)){
        	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        	ret.setRET_MSG(response.getRet().getRET_MSG());
			response.setRet(ret);
        }
        return response;
    }
    
	/**
   	 * @Description: 请求数据处理
   	 * @param request
   	 * @return OnlineQueryBean
   	 * @author wss
   	 * @date 2021-4-29
   	 */
   	private OnlineQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {
   		OnlineQueryBean bean = new OnlineQueryBean();
   		Map body = request.getBody();
   		bean.setOnlineAcptNo(getStringVal(body.get("ONLINE_ACPT_NO")));//在线银承编号
   		bean.setOnlineAcptTotal(getBigDecimalVal(body.get("ACPT_TOTAL_AMT")));//银承总金额
   		bean.setElctrncSign(getStringVal(body.get("E_SIGN")));//电子签名
   		bean.setBbspAcctNo(getStringVal(body.get("BBSP_ACCT_NO")));//电票签约账号
   		bean.setApplyBankNo(getStringVal(body.get("REMITTER_OPEN_BANK")));//出票人开户行行号
   		bean.setApplyBankName(getStringVal(body.get("REMITTER_OPENBANK_NAME")));//出票人开户行行名
   		bean.setOperationType(getStringVal(body.get("OPERATION_TYPE")));//操作类型
   		   		
		Date endDate = null;//最晚到期日
   		//开票信息
   		List details = request.getDetails();
   		if(null != details && details.size()>0){
   			PlOnlineAcptDetail detail = null;
   			BigDecimal totalAmt = new BigDecimal(0);
   			for(int i=0;i<details.size();i++){
   				Map map = (Map) details.get(i);
   				detail = new PlOnlineAcptDetail();
   				detail.setBillSerialNo(getStringVal(map.get("SERIAL_NO")));
   				detail.setBillAmt(getBigDecimalVal(map.get("BILL_AMT")));//票面金额
   				detail.setPayeeName(getStringVal(map.get("RECV_BILL_CLIENT_NAME")));//收票人名称
   				detail.setPayeeAcct(getStringVal(map.get("RECV_BILL_ACCT_NO")));//收票人账号
   				detail.setPayeeAcctName(getStringVal(map.get("RECV_BILL_ACCT_NAME")));//收票人账号名称
   				detail.setPayeeBankCode(getStringVal(map.get("RECV_BILL_BANK_NO")));//收票人开户行行号
   				detail.setPayeeBankName(getStringVal(map.get("RECV_BILL_BANK_NAME")));//收票人开户行名称
   				detail.setAcptBankCode(getStringVal(map.get("ACCEPTOR_BANK_NO")));//承兑行行号
   				detail.setAcptBankName(getStringVal(map.get("ACCEPTOR_BANK_NAME")));//承兑行行名
   				detail.setLimitType(getStringVal(map.get("LIMIT_TYPE")));//期限方式
   				detail.setEndDate(getStringVal(map.get("EXPIRY_DATE")));//到期日/期限（月）
   				if(PublicStaticDefineTab.LIMIT_TYPE_0.equals(detail.getLimitType())){
   					detail.setDueDate(getDateVal2(map.get("EXPIRY_DATE")));
   				}else{
   					Date dueDate = DateUtils.getNextNMonth(DateUtils.getCurrDate(), Integer.parseInt(detail.getEndDate()));
   					detail.setDueDate(dueDate);
   				}
   				if(null == endDate){
   					endDate = detail.getDueDate();
   				}else{
   					if(endDate.compareTo(detail.getDueDate())<0){
   						endDate = detail.getDueDate();
   					}
   				}
   				detail.setTransferFlag(getStringVal(map.get("TRANSFER_FLAG")));//是否可转让
   				detail.setIsAutoCallPyee(getStringVal(map.get("AUTO_RECV_BILL_FLAG")));//是否联动收票人自动收票
   				detail.setSplitFlag(StringUtils.isEmpty(getStringVal(map.get("SPLIT_FLAG"))) ? "0" : getStringVal(map.get("SPLIT_FLAG")) );//是否可拆分
   				detail.setDraftSource(getStringVal(map.get("BILL_SOURCE")));//票据来源
   				totalAmt = totalAmt.add(detail.getBillAmt());
   				//获取收票人信息
   				if(!bean.getMap().containsKey(detail.getPayeeName())){
   					bean.setPayeeAcctName(detail.getPayeeAcctName());
   					bean.setPayeeAcctNo(detail.getPayeeAcct());
   					bean.setPayeeStatus(PublicStaticDefineTab.STATUS_1);
   					bean.setAcptBatchId("1");//经办时不生成数据，批次id传值用于查询方法的调用
   					List<OnlineQueryBean> payees = pedOnlineAcptService.queryOnlineAcptPayeeListBean(bean, null);
   					if(null != payees && payees.size()>0){
   						bean.getMap().put(detail.getPayeeAcct(), payees.get(0).getPayeeTotalAmt().subtract(payees.get(0).getPayeeUsedAmt()!=null?payees.get(0).getPayeeUsedAmt():new BigDecimal(0)));
   					    logger.info(payees.get(0).getPayeeUsedAmt()+"------------------收票人已用金额");
   					}
   				}
   				String loanNo = poolBatchNoUtils.txGetIOUNo("YCOL", 8);
   				   				
   				detail.setLoanNo(loanNo);//借据号
   				
   				
   				bean.getDetalis().add(detail);
   			}
   			bean.setTotalAmt(totalAmt);
   			bean.setEndDate(endDate);
   		}
   		return bean;
   	}


}
