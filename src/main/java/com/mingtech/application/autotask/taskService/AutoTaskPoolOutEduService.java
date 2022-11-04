package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.BeanUtil;

/**
 *自动任务业务处理类
 *解质押额度释放
 *gcj 20210513
 */


public class AutoTaskPoolOutEduService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolOutEduService.class);
	DraftPoolOutService draftPoolOutService=PoolCommonServiceFactory.getDraftPoolOutService();
	ConsignService consignService = ConsignServiceFactory.getConsignService();
	PoolCreditClientService poolCreditClientService = PoolCommonServiceFactory.getPoolCreditClientService();
	PoolCreditProductService productService = PoolCommonServiceFactory.getPoolCreditProductService();
	AutoTaskPublishService autoTaskPublishService =PoolCommonServiceFactory.getAutoTaskPublishService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	BlackListManageService blackListManageService = PoolCommonServiceFactory.getBlackListManageService();

	
	ReturnMessageNew response = new ReturnMessageNew();
	Ret ret = new Ret();
	
	/**
	 * gcj 20210513 解质押额度释放
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {
		/**
		 * 额度释放逻辑:
		 * 	1、判断该出池的票是否有做过拆分出池，若是拆分出池则需要调用信贷大小票置换占用接口
		 * 	2、若不是拆分出池：判断是否可拆分票据，若是可拆分票据，票号字段需上送票号+子票区间
		 */
		
		DraftPoolOut out=  draftPoolOutService.loadByOutId(busiId);
		if(null==out){
			this.response(Constants.TX_FAIL_CODE, "票据id："+busiId+"未能找到出池业务明细实体或出池状态不正确", response, ret);
			return response;
		}
		if(!PoolComm.CC_00.equals(out.getPlStatus())){
			this.response(Constants.TX_FAIL_CODE, "票据号码："+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + " 的出池状态不正确", response, ret);
			return response;
		}
		if(null!=queryType){
			/**
			 * 判断是否已经释放
			 */
			if(PoolComm.SP_00.equals(out.getBtFlag())){//保贴额度释放成功
				this.response(Constants.TX_SUCCESS_CODE, "票号："+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + " 出池额度已释放", response, ret);
			}

		}
		out.setTaskDate(new Date());
		
		PedGuaranteeCredit pedCredit;
		
		PoolQueryBean bean = new PoolQueryBean();
		bean.setBillNo(out.getPlDraftNb());
		bean.setSStatusFlag(PoolComm.DS_03);
		
		/********************融合改造新增 start******************************/
		bean.setBeginRangeNo(out.getBeginRangeNo());
		bean.setEndRangeNo(out.getEndRangeNo());
		/********************融合改造新增 end******************************/
		
		DraftPool pool=consignService.queryDraftByBean(bean).get(0);
		String txFlag = pool.getTXFlag();
		
		logger.info("票据【"+pool.getAssetNb()+"】出池处理开始......");
		
		if(StringUtils.isBlank(out.getSplitId())){//未做过拆分出池
			/**
			 * 保贴额度占用对象需通过未拆分之前票据对象查询
			 */
			PoolQueryBean pBean = new PoolQueryBean();
			pBean.setProtocolNo(out.getPoolAgreement());
			pBean.setBillNo(out.getPlDraftNb());
			
			/********************融合改造新增 start******************************/
			pBean.setBeginRangeNo(out.getBeginRangeNo());
			pBean.setEndRangeNo(out.getEndRangeNo());
			/********************融合改造新增 end******************************/
			pedCredit = productService.queryByBean(pBean);
			if(null!=pedCredit && PoolComm.SP_01.equals(pedCredit.getStatus()) && !(PoolComm.TX_FLAG_1).equals(txFlag)){//占用成功	
				
				if(!this.toPJE013(out, pedCredit)){
					return response;
				}
			}else if((PoolComm.TX_FLAG_1).equals(txFlag)){//未占用
				this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "强贴类型不许额度释放", response, ret);
			}
			else{
				this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "额度系统额度释放成功！", response, ret);
			}
			
		}else{
			if(!this.toPJE027(out, txFlag)){
				return response;
			}
			
		}
		
		/**
		 *  票据池出池记账任务唤醒
		 */
		Map<String, String> reqParams =new HashMap<String,String>();
    	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLOUT_ACC_TASK_NO, out.getId(), AutoTaskNoDefine.BUSI_TYPE_JJZ,reqParams);
		return response;
	}
	
	/**
	 * 未拆分票据出池释放额度
	 * @param out
	 * @param pedCredit
	 * @return
	 */
	public boolean toPJE013(DraftPoolOut out,PedGuaranteeCredit pedCredit) throws Exception{

		logger.info("票据【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "释放商票保贴or银票额度释放处理开始......");
		Map resuMap = new HashMap();
		List<Map> reqList = new ArrayList<Map>();//实际为单条
		CreditTransNotes creditNotes = new CreditTransNotes();
		if(out.getSplitFlag().equals("1")){//可拆分等分化新票
			resuMap.put("billNo", out.getPlDraftNb()+"-"+out.getBeginRangeNo()+"-"+out.getEndRangeNo());
		}else{
			resuMap.put("billNo", out.getPlDraftNb());
		}
		
		reqList.add(resuMap);
		creditNotes.setReqList(reqList);//上传文件
		ReturnMessageNew response1 = poolCreditClientService.txPJE013(creditNotes);
		if(response1.isTxSuccess()){
			out.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
			PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setBillNo(out.getPlDraftNb());
			poolQueryBean.setSStatusFlag(PoolComm.DS_03);
			
			/********************融合改造新增 start******************************/
			poolQueryBean.setBeginRangeNo(out.getBeginRangeNo());
			poolQueryBean.setEndRangeNo(out.getEndRangeNo());
			/********************融合改造新增 end******************************/
			
			DraftPool dpool=consignService.queryDraftByBean(poolQueryBean).get(0);
			dpool.setBtFlag(PoolComm.SP_00);
			pedCredit.setStatus(PoolComm.SP_00);
			pedCredit.setCreateTime(new Date());
			draftPoolOutService.txStore(pedCredit);
			draftPoolOutService.txStore(out);
			draftPoolOutService.txStore(dpool);
			this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】额度系统额度释放成功！", response, ret);
			return true;
		}else{
			this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+"】额度系统额度释放失败,额度系统错误："+response1.getRet().getRET_MSG(), response, ret);	
			return false;
		}
		
	}
	
	/**
	 * 拆分票据出池  大小票置换占用处理
	 * @param out
	 * @param pedCredit
	 * @return
	 * @throws Exception
	 */
	public boolean toPJE027(DraftPoolOut out,String txFlag) throws Exception{
		//拆分前票据基本信息表对象
		PoolBillInfo billInfo = (PoolBillInfo) draftPoolOutService.load(out.getSplitId(),PoolBillInfo.class);
		/**
		 * 查询拆分后在池的票据
		 */
		PoolBillInfo inBill = draftPoolInService.loadBySplit(out.getSplitId(), "DS_02");//拆分后入库的票
		
		/**
		 * 保贴额度占用对象需通过未拆分之前票据对象查询
		 */
		PoolQueryBean pBean = new PoolQueryBean();
		pBean.setProtocolNo(out.getPoolAgreement());
		pBean.setBillNo(out.getPlDraftNb());
		
		pBean.setBeginRangeNo(billInfo.getBeginRangeNo());
		pBean.setEndRangeNo(billInfo.getEndRangeNo());
		PedGuaranteeCredit pedCredit = productService.queryByBean(pBean);
		
		if(null!=pedCredit && PoolComm.SP_01.equals(pedCredit.getStatus()) && !(PoolComm.TX_FLAG_1).equals(txFlag)){//占用成功	
			
			List<Map> reqList = new ArrayList<Map>();
			CreditTransNotes creditNotes = new CreditTransNotes();
			Map resuMap = new HashMap();//拆分前票
			Map resuMapIn = new HashMap();//拆分后入库票
			Map resuMapOut = new HashMap();//拆分后出库票
			
			BigDecimal billAmt = billInfo.getFBillAmount();//票面金额
			String billType = billInfo.getSBillType();//票据类型
			String bankNo = billInfo.getSAcceptorBankCode();//承兑行行号
			String totalBankNo = billInfo.getAcptHeadBankNo();//承兑行行号--总行   二代支付行号
			String totalBankName = billInfo.getAcptHeadBankName();//承兑行行名--总行
			String acceptor = billInfo.getSAcceptor();//承兑人全称
			String acptAcctNo = billInfo.getSAcceptorAccount();//承兑人账号
//			String guarantDiscNo = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
			Map rsuMap = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
			
			
			if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
				totalBankNo ="";
				totalBankName = acceptor;
			}
			resuMap.put("BILL_INFO_ARRAY.BANK_NO", totalBankNo);//二代支付行号
			resuMapIn.put("BILL_INFO_ARRAY.BANK_NO", totalBankNo);//二代支付行号
			resuMapOut.put("BILL_INFO_ARRAY.BANK_NO", totalBankNo);//二代支付行号
			
			
			if(rsuMap != null){
				resuMap.put("BILL_INFO_ARRAY.CLINET_NAME", (String)rsuMap.get("guarantDiscName"));//客户名称
				resuMapIn.put("BILL_INFO_ARRAY.CLINET_NAME", (String)rsuMap.get("guarantDiscName"));//客户名称
				resuMapOut.put("BILL_INFO_ARRAY.CLINET_NAME", (String)rsuMap.get("guarantDiscName"));//客户名称
				
				resuMap.put("BILL_INFO_ARRAY.CLIENT_NO", (String)rsuMap.get("guarantDiscNo"));//客户编号
				resuMapIn.put("BILL_INFO_ARRAY.CLIENT_NO", (String)rsuMap.get("guarantDiscNo"));//客户编号
				resuMapOut.put("BILL_INFO_ARRAY.CLIENT_NO", (String)rsuMap.get("guarantDiscNo"));//客户编号
				
			}else{
				resuMap.put("BILL_INFO_ARRAY.CLINET_NAME", totalBankName);//客户名称
				resuMapIn.put("BILL_INFO_ARRAY.CLINET_NAME", totalBankName);//客户名称
				resuMapOut.put("BILL_INFO_ARRAY.CLINET_NAME", totalBankName);//客户名称
			}
			
			
			resuMap.put("BILL_INFO_ARRAY.BILL_NO", billInfo.getSBillNo()+"-"+billInfo.getBeginRangeNo()+"-"+billInfo.getEndRangeNo());//票号
			resuMap.put("BILL_INFO_ARRAY.OPERATION_TYPE", "02");//操作类型 释放
			resuMap.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT01");//票据拆分类型  BT01-拆分前票据  BT02-拆分后入库票 BT03-拆分后出库票
			resuMap.put("BILL_INFO_ARRAY.BILL_AMT", billInfo.getFBillAmount());//票据金额
			resuMap.put("BILL_INFO_ARRAY.LIMIT_NO", "");//额度编号
			resuMap.put("BILL_INFO_ARRAY.LIMIT_TYPE", "SX0060201910040060");//额度类型
			reqList.add(resuMap);
			
			resuMapIn.put("BILL_INFO_ARRAY.BILL_NO", inBill.getSBillNo()+"-"+inBill.getBeginRangeNo()+"-"+inBill.getEndRangeNo());//票号
			resuMapIn.put("BILL_INFO_ARRAY.OPERATION_TYPE", "01");//操作类型  占用
			resuMapIn.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT02");//票据拆分类型
			resuMapIn.put("BILL_INFO_ARRAY.BILL_AMT", inBill.getFBillAmount());//票据金额
			resuMapIn.put("BILL_INFO_ARRAY.LIMIT_NO", "");//额度编号
			resuMapIn.put("BILL_INFO_ARRAY.LIMIT_TYPE", "SX0060201910040060");//额度类型
			reqList.add(resuMapIn);
			
			resuMapOut.put("BILL_INFO_ARRAY.BILL_NO", out.getPlDraftNb()+"-"+out.getBeginRangeNo()+"-"+out.getEndRangeNo());//票号
			resuMapOut.put("BILL_INFO_ARRAY.OPERATION_TYPE", "02");//操作类型 释放
			resuMapOut.put("BILL_INFO_ARRAY.SPLIT_TYPE", "BT03");//票据拆分类型
			resuMapOut.put("BILL_INFO_ARRAY.BILL_AMT", out.getPlIsseAmt());//票据金额
			resuMapOut.put("BILL_INFO_ARRAY.LIMIT_NO", "");//额度编号
			resuMapOut.put("BILL_INFO_ARRAY.LIMIT_TYPE", "SX0060201910040060");//额度类型
			reqList.add(resuMapOut);
			
			creditNotes.setReqList(reqList);//上传文件
			ReturnMessageNew resp = poolCreditClientService.txPJE027(creditNotes);
			if(resp.isTxSuccess()){
				/**
				 * 入库小票的保贴额度占用记录
				 */
				PedGuaranteeCredit pedCreditIn = new PedGuaranteeCredit();
				BeanUtil.copyValue(pedCredit, pedCreditIn);
				
				pedCreditIn.setId(null);
				pedCreditIn.setBillAmt(inBill.getFBillAmount());
				pedCreditIn.setBeginRangeNo(inBill.getBeginRangeNo());//票据开始子区间号
				pedCreditIn.setEndRangeNo(inBill.getEndRangeNo());//票据结束子区间号
				 
				 
				out.setBtFlag(PoolComm.SP_00);//保贴额度释放成功
				PoolQueryBean poolQueryBean = new PoolQueryBean();
				poolQueryBean.setBillNo(out.getPlDraftNb());
				poolQueryBean.setSStatusFlag(PoolComm.DS_03);
				
				/********************融合改造新增 start******************************/
				poolQueryBean.setBeginRangeNo(out.getBeginRangeNo());
				poolQueryBean.setEndRangeNo(out.getEndRangeNo());
				/********************融合改造新增 end******************************/
				
				DraftPool dpool=consignService.queryDraftByBean(poolQueryBean).get(0);
				dpool.setBtFlag(PoolComm.SP_00);
				pedCredit.setStatus(PoolComm.SP_00);
				pedCredit.setCreateTime(new Date());
				draftPoolOutService.txStore(pedCredit);
				draftPoolOutService.txStore(pedCreditIn);
				draftPoolOutService.txStore(out);
				draftPoolOutService.txStore(dpool);
				this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】额度系统额度释放成功！", response, ret);
			}else{
				this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+"】额度系统额度释放失败,额度系统错误："+resp.getRet().getRET_MSG(), response, ret);	
				return false;
			}
			
		}else if((PoolComm.TX_FLAG_1).equals(txFlag)){//未占用
			this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "强贴类型不许额度释放", response, ret);
		}
		else{
			this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "额度系统额度释放成功！", response, ret);
		}
		return true;
	}
	
	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}
}
