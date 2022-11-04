package com.mingtech.application.autotask.taskService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 *自动任务业务处理类
 *解质押申请
 *gcj 20210513
 */


public class AutoTaskPoolOutSendService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolOutSendService.class);
	DraftPoolOutService draftPoolOutService = PoolCommonServiceFactory.getDraftPoolOutService();
	PoolEcdsService poolEcdsService = PoolCommonServiceFactory.getPoolEcdsService();
	PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	DepartmentService departmentService = DepartmentServiceFactory.getDepartmentService();
	AutoTaskPublishService autoTaskPublishService = PoolCommonServiceFactory.getAutoTaskPublishService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();

	/**
	 * gcj 20210513 解质押申请
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		DraftPoolOut out=  draftPoolOutService.loadByOutId(busiId);
		if(null==out&&!PoolComm.CC_01.equals(out.getPlStatus())){
			this.response(Constants.TX_FAIL_CODE, "根据票号："+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "未能找到出池业务明细实体或出池申请前置状态不为已记账", response, ret);
			return response;
		}
		if(null!=queryType){
			/**
			 * 查询bbsp 状态是否为解质押申请状态  不为此状态发送解质押申请
			 */
			ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
			
			/*poolTrans.setBillNo(out.getPlDraftNb());//票号
			
			*//********************融合改造新增 start******************************//*
			poolTrans.setMaxBeginRangeNo(out.getBeginRangeNo());//票据开始子区间号
			poolTrans.setMinBeginRangeNo(out.getBeginRangeNo());//票据开始子区间号
			poolTrans.setMaxEndRangeNo(out.getEndRangeNo());//票据结束子区间号
			poolTrans.setMinEndRangeNo(out.getEndRangeNo());//票据结束子区间号
			
			poolTrans.setAcceptorAcctNo(out.getAccNo());//账号
			poolTrans.setTransType(PoolComm.NES_0102000);//解质押
			poolTrans.setOperationType("QT01");//撤销申请查询  
*/			
			logger.info("票号为["+out.getPlDraftNb()+"],票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "的票,发送查询解质押待签收接口开始");
			/********************融合改造新增 end******************************/
			
			
			Map infoMap = new HashMap();
//			infoMap.put("INPOOL_FLAG", "1");//是否入池
//			infoMap.put("BRANCH_NO",poolNotes.getBranchNo());//查询机构号		
			infoMap.put("QUERY_INFO_ARRAY.APPLYER_ACCT_NO",out.getAccNo());//账号
			infoMap.put("QUERY_INFO_ARRAY.DRAFT_TYPE","");//汇票类型
			infoMap.put("QUERY_INFO_ARRAY.ACCEPTOR_ACCT_NAME","");//承兑人名称
			infoMap.put("QUERY_INFO_ARRAY.BILL_NO",out.getPlDraftNb());//票据号码
			infoMap.put("QUERY_INFO_ARRAY.MIN_START_BILL_NO",out.getBeginRangeNo());//票据开始子区间号
			infoMap.put("QUERY_INFO_ARRAY.MAX_START_BILL_NO",out.getBeginRangeNo());//票据开始子区间号
			infoMap.put("QUERY_INFO_ARRAY.MIN_END_BILL_NO",out.getEndRangeNo());//票据结束子区间号
			infoMap.put("QUERY_INFO_ARRAY.MAX_END_BILL_NO",out.getEndRangeNo());//票据结束子区间号
			infoMap.put("QUERY_INFO_ARRAY.MAX_DRAW_BILL_DATE",DateUtils.toString(out.getPlIsseDt(),"yyyyMMdd"));//出票日期上限
			infoMap.put("QUERY_INFO_ARRAY.MIN_DRAW_BILL_DATE",DateUtils.toString(out.getPlIsseDt(),"yyyyMMdd"));//出票日期下限
			infoMap.put("QUERY_INFO_ARRAY.MAX_REMIT_EXPIRY_DATE",DateUtils.toString(out.getPlDueDt(),"yyyyMMdd"));//汇票到期日期上限
			infoMap.put("QUERY_INFO_ARRAY.MIN_REMIT_EXPIRY_DATE",DateUtils.toString(out.getPlDueDt(),"yyyyMMdd"));//汇票到期日期下限
			infoMap.put("QUERY_INFO_ARRAY.MAX_BILL_AMT",out.getTradeAmt());//票据最高金额
			infoMap.put("QUERY_INFO_ARRAY.MIN_BILL_AMT",out.getTradeAmt());//票据最低金额
			infoMap.put("QUERY_INFO_ARRAY.QUERY_TYPE","QT00");//查询类型
			infoMap.put("QUERY_INFO_ARRAY.TRAN_NO_LIST",PoolComm.NES_0102010);//交易编号集合
			infoMap.put("QUERY_INFO_ARRAY.BILL_SOURCE_LIST",out.getDraftSource());//
			poolTrans.getDetails().add(infoMap);

			ReturnMessageNew resp = poolEcdsService.txApplyImplawnForSign(poolTrans);
			logger.info("发送查询解质押待签收接口结束,返回的数据长度为["+response.getDetails().size()+"],返回的数据为["+response.getDetails()+"]");
			List rest = resp.getDetails();
            if(rest.size()>0){
            	out.setPlStatus(PoolComm.CC_03);//解质押待签收
            	draftPoolOutService.txStore(out);

                /**
                 * 唤醒解质押签收子任务。。。
                 */
        		Map<String, String> reqParams =new HashMap<String,String>();

            	autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLOUT_SIGN_TASK_NO, out.getId(), AutoTaskNoDefine.BUSI_TYPE_JQS,reqParams);

            	this.response(Constants.TX_SUCCESS_CODE, "票号："+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "解质押申请成功，唤醒解质押签收", response, ret);
    			return response;
            }
		}
		out.setTaskDate(new Date());

		logger.debug("根据核心客户号["+out.getCustNo()+"]查询协议信息开始");
		PedProtocolDto dto = (PedProtocolDto) pedProtocolService.queryProtocolDto(null, null, out.getPoolAgreement(), null, null, null);
		logger.info("查询协议信息结束,机构号为["+dto.getCreditDeptNo()+"],受理网点为["+dto.getOfficeNet()+"]");
		
		PoolBillInfo bill = draftPoolOutService.loadByBillNo(out.getPlDraftNb(),out.getBeginRangeNo(),out.getEndRangeNo());
		
		/**
		 * 调取解质押申请接口wfj
		 */
		ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
		
		
		
		/**
		 * body内需要传送的值
		 */
		poolTransNotes.setApplicantAcctNo("0");//电票签约帐号  多账号|拼接
		//若电子签名为空设置为0
		if(out.getElsignature()!=null&&!out.getElsignature().equals("")){
			poolTransNotes.setSignature(out.getElsignature());//网银发过来的电子签名
		}else{
			poolTransNotes.setSignature("0");//电子签名
		}
		
		
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
			poolTransNotes.setReceiverBankNo(ment.getPjsBrchNo());//质权人开户行行号
		}else {
			logger.info("未查询到部门信息");
		}
		
		
		
		/**
		 * 票据信息数组需传送的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",bill.getDraftSource());//票据来源 
		infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型
		infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0102010);//交易编号  解质押申请
		infoMap.put("BILL_INFO_ARRAY.BILL_NO",out.getPlDraftNb());//票据（包）号码
		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",out.getHilrId());//持票id
		infoMap.put("BILL_INFO_ARRAY.BILL_ID",bill.getDiscBillId());//票据id
		infoMap.put("BILL_INFO_ARRAY.START_BILL_NO","");//子票区间起始
		infoMap.put("BILL_INFO_ARRAY.END_BILL_NO","");//子票区间截至
		infoMap.put("BILL_INFO_ARRAY.TRAN_AMT",out.getPlIsseAmt());//交易金额
		if(StringUtil.isNotBlank(out.getTXFlag()) && out.getTXFlag().equals("1")){//票据池贴现
			infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","5");//渠道来源  
		}else{
			infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
		}
		infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
		String seq = poolBatchNoUtils.txGetFlowNo();
		infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
		poolTransNotes.getDetails().add(infoMap);

		/**
		 * 解质押信息数组需传送的值
		 */
		Map unpledgeMap = new HashMap();
		unpledgeMap.put("BILL_INFO_ARRAY.UNPLEDGE_INFO_ARRAY.UNPLEDGE_DATE",DateUtils.formatDateToString(new Date(), "yyyyMMdd"));//质押解除日期 必输
		
		//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
		if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
			poolTransNotes.setBatchNo(dto.getCreditDeptNo());//融资机构号
		}else{
			poolTransNotes.setBatchNo(dto.getOfficeNet());//受理网点
		}
		//解质押机构号选分行机构号
		Department dept = departmentService.getDepartmentByInnerBankCode(poolTransNotes.getBatchNo());
		
		poolTransNotes.setBranchNo(dept.getAuditBankCode());//解质押机构号
		unpledgeMap.put("BILL_INFO_ARRAY.UNPLEDGE_INFO_ARRAY.UNPLEDGE_BRANCH_NO",poolTransNotes.getBranchNo());//质押解机构号 必输
		
		poolTransNotes.getDetails().add(unpledgeMap);
		
		
		
		
		/*poolTransNotes.setBillNo(out.getPlDraftNb());//票据号码
		
		
		//机构号	若果有融资机构号拿融资机构号,若没有拿受理网点
		if(dto.getCreditDeptNo()!=null&&!dto.getCreditDeptNo().equals("")){
			poolTransNotes.setBatchNo(dto.getCreditDeptNo());//融资机构号
		}else{
			poolTransNotes.setBatchNo(dto.getOfficeNet());//受理网点
		}
		//解质押机构号选分行机构号
		Department dept = departmentService.getDepartmentByInnerBankCode(poolTransNotes.getBatchNo());
		
		poolTransNotes.setBranchNo(dept.getAuditBankCode());//解质押机构号
		poolTransNotes.setSignature("0");//电子签名
		
		*//***************************融合改造新增上送字段  start********************************//*
		if(!bill.getDraftSource().equals(PoolComm.CS01)){ // 非 ecds 票据可拆分
			poolTransNotes.setBeginRangeNo(out.getBeginRangeNo());//票据开始子区间号
			poolTransNotes.setEndRangeNo(out.getEndRangeNo());//票据结束子区间号
			poolTransNotes.setTradeAmt(out.getTradeAmt());//交易金额
		}
		
		poolTransNotes.setCorpApplyTransCode(PoolComm.NES_0092000);//交易编号
		poolTransNotes.setHldrId(bill.getHilrId());//持有id
		poolTransNotes.setBillId(bill.getDiscBillId());//电票id
		poolTransNotes.setDataSource("3");//渠道来源  3-票据池
		poolTransNotes.setBillOriginCode(bill.getDraftSource());//票据来源 
		

		*//***************************融合改造新增上送字段  end********************************//*
		*/
		
		
		logger.info("票号为["+out.getPlDraftNb()+"],票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "的票,发送解质押申请开始");
		try {
			ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
			
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				List details = resp.getDetails();
				for (int i = 0; i < details.size(); i++) {
					Map map = (Map) details.get(i);
					if(map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE").equals(Constants.TX_SUCCESS_CODE)){//成功
						out.setDevSeqNo("");//清空出池流水  为解质押签收异常到期时再次发入池记账用
						out.setPlStatus(PoolComm.CC_02);//已发解质押申请
						
						out.setHilrId(getStringVal(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID")));//持票ID
						out.setTransId(getStringVal(map.get("TRAN_RESULT_ARRAY.TRAN_NO")));//交易ID
						draftPoolOutService.txStore(out);
						
						bill.setHilrId(getStringVal(map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID")));//持票ID
						draftPoolOutService.txStore(bill);
						this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "发送解质押申请成功", response, ret);
					}else{
						this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "发送解质押申请失败", response, ret);			
					}
				}
			}else{
				this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "发送解质押申请失败", response, ret);			
			}
		} catch (Exception e) {
			this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+"】,票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "发送解质押申请失败"+e.getMessage(), response, ret);
		}
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
