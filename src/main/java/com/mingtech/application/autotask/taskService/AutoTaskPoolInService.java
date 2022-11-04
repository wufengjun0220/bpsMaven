package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.ecd.domain.EndorsementLog;
import com.mingtech.application.ecd.service.EndorsementLogService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.CpesBranch;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 *自动任务业务处理类
 *质押入池申请发送+背书信息
 *gcj 20210512
 */


public class AutoTaskPoolInService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolInService.class);
	PoolEcdsService poolEcdsService=PoolCommonServiceFactory.getPoolEcdsService();
	DepartmentService departmentService=DepartmentServiceFactory.getDepartmentService();
	PedProtocolService pedProtocolService= PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	EndorsementLogService endorsementLogService=PoolCommonServiceFactory.getEndorsementLogService();
	AutoTaskExeService autoTaskExeService = PoolCommonServiceFactory.getAutoTaskExeService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();
	BlackListManageService blackListManageService = PoolCommonServiceFactory.getBlackListManageService();

	
	/**
	 * gcj 20210511
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		DraftPoolIn poolIn = draftPoolInService.loadByPoolInId(busiId);
		if(null==poolIn){
			this.response(Constants.TX_FAIL_CODE, "根据ID"+busiId+"未能找到入池业务明细实体", response, ret);
			return response;
		}
		PoolBillInfo bill = draftPoolInService.loadByBillNo(poolIn.getPlDraftNb(),poolIn.getBeginRangeNo(),poolIn.getEndRangeNo());

		if(null!=queryType){
			/**
			 * 查询 异常情况发送前判断是否为客户持有票据
			 */
			//调用持有票据查询接口
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.getWorkDayDate());//设置起时间
		    cal.add(Calendar.DATE, 1);//加一天
		    Date dueDt = cal.getTime();//到期日当天的票据不查
			ECDSPoolTransNotes transNotes = new ECDSPoolTransNotes();
			transNotes.setApplicantAcctNo(poolIn.getAccNo());//客户账号
			transNotes.setMinDueDt(DateUtils.toString(dueDt, "yyyyMMdd"));
			transNotes.setBillNo(poolIn.getPlDraftNb());//票号
			
			/********************融合改造新增 start******************************/

			transNotes.setMaxBeginRangeNo(poolIn.getBeginRangeNo());//票据起始号
			transNotes.setMinBeginRangeNo(poolIn.getBeginRangeNo());//票据起始号
			transNotes.setMaxEndRangeNo(poolIn.getEndRangeNo());//票据截至号
			transNotes.setMinEndRangeNo(poolIn.getEndRangeNo());//票据截至号
			/********************融合改造新增 end******************************/

			/*
			 * 调用BBSP系统持有票查询接口
			 */
			logger.info("根据电票签约账号查询持有票据开始");
			ReturnMessageNew retu = poolEcdsService.txApplyPossessBill(transNotes);
			List list=retu.getDetails();
            if(list.size()==0){
            	poolIn.setPlStatus(PoolComm.RC_01);//已发质押申请
            	draftPoolInService.txStore(poolIn);
            	this.response(Constants.TX_SUCCESS_CODE, "该票据"+poolIn.getPlDraftNb()+"不是客户持有票据,跳过质押申请", response, ret);
    			return response;
            }else{
            	//发送bbsp接触经办锁
            	ECDSPoolTransNotes ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
            	/********************融合改造新增 start******************************/
            	ecdsNotes.setAcctNo(bill.getAccNo());
				ecdsNotes.setDataSource(bill.getDraftSource());//票据来源	必输
				ecdsNotes.setTransType("1");//1-持票 2-申请交易 3-应答交易
				ecdsNotes.setIsLock("2");//加锁  1加锁   2解锁
				ecdsNotes.setTransNo(PoolComm.NES_0092010);//交易编号	必输
				ecdsNotes.setHldrId(bill.getHilrId());//持票ID集合
				
				HashMap map = new HashMap();
				map.put("BILL_INFO_ARRAY.BILL_ID", bill.getDiscBillId());// 票据ID
				ecdsNotes.getDetails().add(map);
				/********************融合改造新增 start******************************/
				
    			
            	//调用BBSP系统锁票接口，返回成功标记
				if (poolEcdsService.txApplyLockEbk(ecdsNotes)){//bbsp操作成功
					
					bill.setEbkLock("2");//解锁					
					logger.info("解锁成功!");
				} else {
					logger.info("BBSP锁票/解锁操作失败!");
					this.response(Constants.TX_FAIL_CODE, "BBSP解锁操作失败!", response, ret);
	    			return response;
				}				
            	//发送质押申请失败直接结束调度任务  票据状态修改  poolIn改为异常；cdedraft改为DS_00
            	poolIn.setPlStatus(PoolComm.RC_07);//异常状态
            	poolIn.setTaskDate(new Date());
            	bill.setSDealStatus(PoolComm.DS_00);//入池可处理状态
            	autoTaskExeService.txStore(poolIn);
            	autoTaskExeService.txStore(bill);
            	//结束任务
				autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOLIN_TASK_NO,AutoTaskNoDefine.POOL_AUTO_POOLIN,busiId);
				this.response(Constants.TX_SUCCESS_CODE, "BBSp解锁成功，调度任务结束", response, ret);
				return response;
            }
		}
		/**
		 * 因网银还在改动影响进度，暂时票据池加锁，后需去掉
		 */
		logger.info("帮网银加经办锁-----开始");
	/*	if(bill.getDraftSource().equals(PoolComm.CS02)){
			ECDSPoolTransNotes ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
			*//********************融合改造新增 start******************************//*
			ecdsNotes.setAcctNo(poolIn.getAccNo());
			ecdsNotes.setDataSource(poolIn.getDraftSource());//票据来源	必输
			ecdsNotes.setTransType("1");//1-持票 2-申请交易 3-应答交易
			ecdsNotes.setIsLock("1");//加锁  1加锁   2解锁
			ecdsNotes.setTransNo(PoolComm.NES_0092010);//交易编号	必输
			ecdsNotes.setHldrId(poolIn.getHilrId());//持票ID集合
			
			HashMap mapTemp = new HashMap();
			mapTemp.put("BILL_INFO_ARRAY.BILL_ID", bill.getDiscBillId());// 票据ID
			
			ecdsNotes.getDetails().add(mapTemp);
			
			if (poolEcdsService.txApplyLockEbk(ecdsNotes)){//bbsp操作成功
				logger.info("票据【"+bill.getSBillNo()+"】,子票区间为【"+bill.getBeginRangeNo()+"-"+bill.getEndRangeNo()+"】电票系统经办锁加锁成功!");
			}else{
				logger.info("票据【"+bill.getSBillNo()+"】,子票区间为【"+bill.getBeginRangeNo()+"-"+bill.getEndRangeNo()+"】电票系统经办锁加锁失败，不执行自动入池操作!");
			}
		}*/
		
		
		logger.info("帮网银加经办锁-----结束");
		
		PedProtocolDto dto = pedProtocolService.queryProtocolDto( null, null,poolIn.getPoolAgreement(), null, null, null);
		ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();

		/**
		 * body内需要传送的值
		 */
		poolTransNotes.setApplicantAcctNo(poolIn.getAccNo());//电票签约帐号  多账号|拼接
		//若电子签名为空设置为0
		if(poolIn.getElsignature()!=null&&!poolIn.getElsignature().equals("")){
			poolTransNotes.setSignature(poolIn.getElsignature());//网银发过来的电子签名
		}else{
			poolTransNotes.setSignature("0");//电子签名
		}
		
		/**
		 * 票据信息数组需传送的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",bill.getDraftSource());//票据来源 
		infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型
		infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0092010);//交易编号  质押申请
		infoMap.put("BILL_INFO_ARRAY.BILL_NO",poolIn.getPlDraftNb());//票据（包）号码
		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",bill.getHilrId());//持票id
		infoMap.put("BILL_INFO_ARRAY.BILL_ID",bill.getDiscBillId());//票据id
		infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",poolIn.getBeginRangeNo());//子票区间起始
		infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",poolIn.getEndRangeNo());//子票区间截至
		infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
		infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","1");//经办锁类型 0-未经办锁票 1-已经办锁票
		String seq = poolBatchNoUtils.txGetFlowNo();
		infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
		poolTransNotes.getDetails().add(infoMap);

		/**
		 * 质押信息数组需传送的值
		 */
		Map pledgeMap = new HashMap();
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_DATE",DateUtils.formatDateToString(new Date(), "yyyyMMdd"));//质权日期 必输
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_NAME","汉口银行股份有限公司");//质权人名称 必输
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_NO","0");//质权人账号 必输
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_NAME","");//质权人账户名称
		//质权人用融资机构信息	若果有融资机构号拿融资机构号,若没有拿受理网点
		String orgNo = "10000";
		if(StringUtil.isNotBlank(dto.getCreditDeptNo())){
			orgNo = dto.getCreditDeptNo();
		}else {
			orgNo = dto.getOfficeNet();
		}
		logger.info("根据机构号["+orgNo+"]查询机构信息开始");
		Department ment = departmentService.queryByInnerBankCode(orgNo);
		if(ment!=null){
			logger.info("查询部门信息结束,质权人开户行号为["+ment.getBankNumber()+"],质权人开户行名称为["+ment.getName()+"]");
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_OPEN_BANK_NO",ment.getPjsBrchNo());//质权人开户行行号
			poolTransNotes.setReceiverBankNo(ment.getPjsBrchNo());//质权人开户行行号
		}else {
			logger.info("未查询到部门信息");
		}
		
		ProtocolQueryBean queryBean = new ProtocolQueryBean();
		queryBean.setTransBrchBankNo(poolTransNotes.getReceiverBankNo());
		
		List list = blackListManageService.queryCpesBranch(queryBean, null, null);
		if(list != null && list.size() > 0){
			CpesBranch branch = (CpesBranch) list.get(0);
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_NAME",branch.getBrchFullNameZh());//质权人名称 必输
		}
		
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_OPEN_BANK_NAME","");//质权人开户行行名
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_TRANSACT_CHANNEL_NO","");//质权人业务办理渠道代码 
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_TYPE","");//质权人识别类型
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.INPOOL_FLAG","1");//入池标志 必输
		pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.LOCK_FLAG","");//锁定标志
		poolTransNotes.getDetails().add(pledgeMap);
		
		
		
		//2.调用质押申请接口
		logger.info("票号为["+poolIn.getPlDraftNb()+"]的票,发送质押申请开始");
		ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
		if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			List details = resp.getDetails();
			if(details != null && details.size() > 0){
				for (int i = 0; i < details.size(); i++) {
					Map map = (Map) details.get(i);
					
					if(poolIn.getDraftSource().equals(PoolComm.CS01) || (poolIn.getDraftSource().equals(PoolComm.CS02) && map.get("TRAN_RESULT_ARRAY.TRAN_SEQ_NO").equals(seq))){
						if(map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE").equals(Constants.TX_SUCCESS_CODE)){//成功
							logger.info("返回的持票id["+getStringVal(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID"))+"], 返回的交易id:"+getStringVal(map.get("TRAN_RESULT_ARRAY.TRAN_ID")));

							poolIn.setHilrId(getStringVal(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID")));//持票ID
							poolIn.setTransId(getStringVal(map.get("TRAN_RESULT_ARRAY.TRAN_ID")));//交易ID
							
							poolIn.setPlStatus(PoolComm.RC_01);//已质押申请
							poolIn.setPlReqTime(new Date());// 申请时间
							poolIn.setTaskDate(new Date());
							
							bill.setHilrId(getStringVal(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID")));//持票ID
							bill.setSDealStatus(PoolComm.DS_01);//DS_01-入池处理中
							draftPoolInService.txStore(poolIn);
							draftPoolInService.txStore(bill);
							this.response(Constants.TX_SUCCESS_CODE, "发送质押申请成功", response, ret);
						}else{
							this.response(Constants.TX_FAIL_CODE, "发送质押申请失败", response, ret);
							return response;
						}
					}
				}
			}else{
				this.response(Constants.TX_FAIL_CODE, "发送质押申请失败", response, ret);
			}
		}else{
			this.response(Constants.TX_FAIL_CODE, "发送质押申请失败", response, ret);
			return response;
		}
		
		/*
		 *  调用bbsp系统接口，查询票据背面信息
		 */
		
 		logger.info("根据电票票据ID["+bill.getDiscBillId()+"]查询背面信息开始");
		ECDSPoolTransNotes poolTrans = new ECDSPoolTransNotes();
		
		poolTrans.setBillId(bill.getDiscBillId());//票id
		poolTrans.setDataSource(bill.getDraftSource());//票据来源
		poolTrans.setBillNo(bill.getSBillNo());//票号
		poolTrans.setAcctNo(poolIn.getAccNo());
		
		ReturnMessageNew result = poolEcdsService.txApplyQueryBillCon(poolTrans);
		if (result.isTxSuccess()) {
			Map mapCon = result.getBody();
			List endorList = result.getDetails();
			
			if(endorList!=null && endorList.size()>0){
				for (int k = 0; k < endorList.size(); k++) {
					Map map = (Map) endorList.get(k);
					if(k == 0 ){
						//根据票号判断背书表是否存在,若存在则删除重录
						logger.debug("票号为["+bill.getSBillNo()+"]的票查询背面信息开始");
						List eList = endorsementLogService.getLastEnLogByEdraftNb(bill.getSBillNo());
						if(eList != null &&eList.size()>0){
							pedProtocolService.txDeleteAll(eList);
						}
					}
					EndorsementLog endor = new EndorsementLog();
					endor.setOwnerEDraftId(bill.getDiscBillId());// 票据id
					endor.setOwnerEDraft(bill.getSBillNo());// 票号
					// endor.setCount(array.size());//背书次数
					endor.setMsgTpId(getStringVal(map.get("BILL_INFO_ARRAY.TRAN_TYPE")));// 交易类别
					endor.setEndrseeNm(getStringVal(map.get("BILL_INFO_ARRAY.ENDORSEE_NAME")));// 被背书人名称
					endor.setEndrsrNm(getStringVal(map.get("BILL_INFO_ARRAY.ENDORSER_NAME")));// 背书人名称
					endor.setEndorseDate(getDateVal(map.get("BILL_INFO_ARRAY.ENDORSE_DATE")));// 背书日期
					endor.setSignInDate(getDateVal(map.get("BILL_INFO_ARRAY.RCV_DATE")));// 签收日期
					endor.setRpdOpenDt(getDateVal(map.get("BILL_INFO_ARRAY.REDEEM_OPEN_DATE")));// 赎回开放日期
					endor.setRpdDueDt(getDateVal(map.get("BILL_INFO_ARRAY.REDEEM_END_DATE")));// 赎回截止日期
//					endor.setRate(getBigDecimalVal(map.get("BACK_MSG_ARRAY.REDEEM_INT_RATE")));// 赎回利率
//					endor.setRedeemAmt(getBigDecimalVal(map.get("BACK_MSG_ARRAY.REDEEM_AMT")));// 赎回金额
					endor.setBanEndrsmtMk(getStringVal(map.get("BILL_INFO_ARRAY.UNENDORSE_FLAG")));// 背书转让标志
					endor.setDshnrCd(getStringVal(map.get("BILL_INFO_ARRAY.PROTEST_FLAG")));// 拒付标志
					endor.setDshnrCdText(getStringVal(map.get("BILL_INFO_ARRAY.PROTEST_REASON")));// 拒付原因
					endor.setRcrsTp(getStringVal(map.get("BILL_INFO_ARRAY.RECOURSE_TYPE")));// 追索类型
					endor.setAddress(getStringVal(map.get("BILL_INFO_ARRAY.GUARANTOR_ADDR")));// 保证人地址
//					endor.setDiscountMode(getStringVal(map.get("BACK_MSG_ARRAY.DISCOUNT_MODE")));// 贴现方式
					
					/*** 融合改造新增字段  start*/
					endor.setBeginRangeNo(getStringVal(map.get("BILL_INFO_ARRAY.START_BILL_NO")));//票据开始子区间号
					endor.setEndRangeNo(getStringVal(map.get("BILL_INFO_ARRAY.END_BILL_NO")));//票据结束子区间号
					endor.setDraftSource(getStringVal(map.get("BILL_INFO_ARRAY.BILL_SOURCE")));//票据来源
					/*** 融合改造新增字段  end*/
					
					pedProtocolService.txStore(endor);
					
				}
			 }
			}
		
		this.response(Constants.TX_SUCCESS_CODE, "发送质押申请成功", response, ret);
		return response;
	}
	
	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}
	protected String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}
	protected BigDecimal getBigDecimalVal(Object obj) throws Exception {
		BigDecimal value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = new BigDecimal(temp);
			}
		}
		return value;
	}
	protected Date getDateVal(Object obj) throws Exception {
		Date value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
			}
		}
		return value;
	}
}
