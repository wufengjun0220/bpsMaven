package com.mingtech.application.autotask.taskService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.application.utils.AutoTaskNoDefine;

/**
 *自动任务业务处理类
 *贴现额度校验 生成出池明细
 *gcj 20210519
 */


public class AutoTaskPoolDiscountEduService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolDiscountEduService.class);
	PoolEcdsService poolEcdsService=PoolCommonServiceFactory.getPoolEcdsService();
	DepartmentService departmentService=DepartmentServiceFactory.getDepartmentService();
	PedProtocolService pedProtocolService= PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	DraftPoolDiscountServer draftPoolDiscountServer=PoolCommonServiceFactory.getDraftPoolDiscountServer();
	DraftPoolOutService draftPoolOutService = PoolCommonServiceFactory.getDraftPoolOutService();
	ConsignService consignService = ConsignServiceFactory.getConsignService();
    PoolEBankService poolEBankService=PoolCommonServiceFactory.getPoolEBankService();
    AutoTaskPublishService autoTaskPublishService= PoolCommonServiceFactory.getAutoTaskPublishService();
	/**贴现额度校验 生成贴现明细
	 * gcj 20210519
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		PlDiscount discount =draftPoolDiscountServer.loadByPlDiscountId(busiId);
		if(null==discount){
			this.response(Constants.TX_FAIL_CODE, "根据ID"+busiId+"未能找到入池业务明细实体", response, ret);
			return response;
		}
		/**
		 * queryType 
		 */
		if(null!=queryType){
			
		}
		discount.setTaskDate(new Date());
		List list=new ArrayList();
		list.add(discount.getSBillNo());
		
		ProtocolQueryBean queryBean = new ProtocolQueryBean();
		queryBean.setPoolAgreement(discount.getBpsNo());
		
		PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
		
		PoolQueryBean param = new PoolQueryBean();//传递参数的bean
		String assNo = discount.getSBillNo() +"-" +discount.getBeginRangeNo() +"-" +discount.getEndRangeNo();
		
		List assetNos=new ArrayList();
		assetNos.add(assNo);
		
		Map<String,PoolBillInfo> changeBills= new HashMap<String, PoolBillInfo>();
		PoolBillInfo info = new PoolBillInfo();
		info.setSBillNo(discount.getSBillNo());
		info.setBeginRangeNo(discount.getBeginRangeNo());
		info.setEndRangeNo(discount.getEndRangeNo());
		info.setTradeAmt(discount.getTradeAmt());
		changeBills.put(assNo, info);//网银发送过来的数据
		
		
		Ret result = poolEBankService.txApplyDraftPoolOutPJC003(list, "0", dto,param,changeBills,assetNos);
		
		if (Constants.TX_SUCCESS_CODE.equals(result.getRET_CODE())) {
			this.response(Constants.TX_SUCCESS_CODE, "出池申请成功！", response, ret);
		} else if(Constants.EBK_04.equals(result.getRET_CODE())){
			this.response(Constants.TX_FAIL_CODE, "池额度不足不允许出池！", response, ret);
			return response;
		} else if(Constants.EBK_05.equals(result.getRET_CODE())){
			this.response(Constants.TX_FAIL_CODE, "池额度不足不允许出池！", response, ret);
			return response;
		}else{//出池票据种含有当日及当日之前到期的票，不允许出池操作！
			this.response(Constants.TX_FAIL_CODE, "出池票据中含有当日及当日之前到期的票，不允许出池操作！", response, ret);
			return response;
			
		}
	
	
	   //生成自动任务流水记录 异步执行额度释放 gcj 20210519  解质押额度释放主任务
	   Map<String, String> reqParams =new HashMap<String,String>();
	   PoolQueryBean poolQueryBean=new PoolQueryBean();
       poolQueryBean.setBillNo(discount.getSBillNo());
       
       poolQueryBean.setBeginRangeNo(discount.getBeginRangeNo());
       poolQueryBean.setEndRangeNo(discount.getEndRangeNo());
       
	   poolQueryBean.setSStatusFlag(PoolComm.CC_00);
	   DraftPoolOut poolout=draftPoolOutService.getDraftPoolOutBybean(poolQueryBean);
	   reqParams.put("busiId", poolout.getId());
	   autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOLOUT_EDU_TASK_NO, poolout.getId(), AutoTaskNoDefine.BUSI_TYPE_JZY, reqParams, poolout.getPlDraftNb(), null, null, null);
	   
	   
	   
	   
	   
	   
		return response;
	}
	
	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}
}
