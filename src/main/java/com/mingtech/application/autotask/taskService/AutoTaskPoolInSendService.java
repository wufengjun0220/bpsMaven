package com.mingtech.application.autotask.taskService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.framework.common.util.StringUtil;

/**
 *自动任务业务处理类
 *质押入池签收申请发送
 *gcj 20210512
 */


public class AutoTaskPoolInSendService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolInSendService.class);
	PoolEcdsService poolEcdsService=PoolCommonServiceFactory.getPoolEcdsService();
	DepartmentService departmentService=DepartmentServiceFactory.getDepartmentService();
	PedProtocolService pedProtocolService= PoolCommonServiceFactory.getPedProtocolService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();

	
	/**
	 * gcj 20210512
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		DraftPoolIn poolIn = draftPoolInService.loadByPoolInId(busiId);
//		
		PoolBillInfo bill = draftPoolInService.loadByBillNo(poolIn.getPlDraftNb(),poolIn.getBeginRangeNo(),poolIn.getEndRangeNo());

		if(null!=queryType){
			/**
			 * 查询bbsp 状态是否为解质押签收状态  不为此状态发送解质押签收
			 * 调取电票接口，获取解质押签收成功结果。
			 */
			PoolBillInfo info = draftPoolInService.loadByBillNo(poolIn.getPlDraftNb(), poolIn.getBeginRangeNo(), poolIn.getEndRangeNo());
			ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
			PedProtocolDto dto = pedProtocolService.queryProtocolDto( null, null,poolIn.getPoolAgreement(), null, null, null);

			
			logger.info("票据【"+poolIn.getPlDraftNb()+",票据起始号：" + poolIn.getBeginRangeNo() + " ，票据截至号： " + poolIn.getEndRangeNo() + "的票据发送电子交易类查询开始");
			
			
			Map infoMap = new HashMap();
			infoMap.put("QUERY_INFO_ARRAY.INPOOL_FLAG", "1");//是否入池
			
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
				infoMap.put("QUERY_INFO_ARRAY.BRANCH_NO",ment.getAuditBankCode());//机构号
			}else {
				logger.info("未查询到部门信息");
			}
			
			
			infoMap.put("QUERY_INFO_ARRAY.APPLYER_ACCT_NO","0");//账号
			infoMap.put("QUERY_INFO_ARRAY.DRAFT_TYPE","");//汇票类型
			infoMap.put("QUERY_INFO_ARRAY.ACCEPTOR_ACCT_NAME","");//承兑人名称
			infoMap.put("QUERY_INFO_ARRAY.BILL_NO",poolIn.getPlDraftNb());//票据号码
			infoMap.put("QUERY_INFO_ARRAY.MIN_START_BILL_NO",poolIn.getBeginRangeNo());//票据开始子区间号
			infoMap.put("QUERY_INFO_ARRAY.MAX_START_BILL_NO",poolIn.getBeginRangeNo());//票据开始子区间号
			infoMap.put("QUERY_INFO_ARRAY.MIN_END_BILL_NO",poolIn.getEndRangeNo());//票据结束子区间号
			infoMap.put("QUERY_INFO_ARRAY.MAX_END_BILL_NO",poolIn.getEndRangeNo());//票据结束子区间号
			infoMap.put("QUERY_INFO_ARRAY.MAX_ACCEPTOR_DATE",poolIn.getPlIsseDt());//出票日期上限
			infoMap.put("QUERY_INFO_ARRAY.MIN_ACCEPTOR_DATE",poolIn.getPlIsseDt());//出票日期下限
			infoMap.put("QUERY_INFO_ARRAY.MAX_DUE_DATE",poolIn.getPlDueDt());//汇票到期日期上限
			infoMap.put("QUERY_INFO_ARRAY.MIN_DUE_DATE",poolIn.getPlDueDt());//汇票到期日期下限
			infoMap.put("QUERY_INFO_ARRAY.MAX_AMT",poolIn.getTradeAmt());//票据最高金额
			infoMap.put("QUERY_INFO_ARRAY.MIN_AMT",poolIn.getTradeAmt());//票据最低金额
			infoMap.put("QUERY_INFO_ARRAY.QUERY_TYPE","QT00");//查询类型
			infoMap.put("QUERY_INFO_ARRAY.TRAN_NO_LIST",PoolComm.NES_0092010);//交易编号集合
			infoMap.put("QUERY_INFO_ARRAY.BILL_SOURCE_LIST",poolIn.getDraftSource());//集合
			poolTrans.getDetails().add(infoMap);

			
			
			ReturnMessageNew resp= poolEcdsService.txApplyImplawnForSign(poolTrans);
			List list = resp.getDetails();
			if(list==null || list.size() == 0){//单笔数据查看,若有返回数据表示当前票据为质押待签收的票
				this.response(Constants.TX_FAIL_CODE, "该票据"+poolIn.getPlDraftNb()+"不是质押待签收票据，不能发起质押签收", response, ret);
    			return response;
			}
		}
		
		PedProtocolDto dto = pedProtocolService.queryProtocolDto( null, null,poolIn.getPoolAgreement(), null, null, null);

		//发起票据质押签收申请
		ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
		/**
		 * 质押签收  body 需上传的值
		 */
		poolTrans.setSignature("0");//电子签名
		poolTrans.setAcceptorAcctNo("0");//电票签约账号	
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
			poolTrans.setReceiverBankNo(ment.getPjsBrchNo());//质权人开户行行号
		}else {
			logger.info("未查询到部门信息");
		}
		
		
		/**
		 * 质押签收 请求信息数组  需上传的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("REQUEST_INFO_ARRAY.TRAN_ID",poolIn.getTransId());//交易id  必输
		infoMap.put("REQUEST_INFO_ARRAY.BILL_SOURCE",bill.getDraftSource());//票据来源  必输
		infoMap.put("REQUEST_INFO_ARRAY.BILL_NO",bill.getSBillNo());//票据（包）号码
		infoMap.put("REQUEST_INFO_ARRAY.BILL_ID",bill.getDiscBillId());//票据ID
		infoMap.put("REQUEST_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
		infoMap.put("REQUEST_INFO_ARRAY.BUSS_TYPE","201802");//业务类型
		infoMap.put("REQUEST_INFO_ARRAY.SIGN_FLAG","1");//签收标识  必输
		String seq = poolBatchNoUtils.txGetFlowNo();
		infoMap.put("REQUEST_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
		poolTrans.getDetails().add(infoMap);
		
		logger.info("票据ID为["+bill.getDiscBillId()+"]的票,发送签收申请开始");
		ReturnMessageNew resp = poolEcdsService.txApplySignReject(poolTrans);
		if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			List details = resp.getDetails();
			for (int i = 0; i < details.size(); i++) {
				Map map = (Map) details.get(i);
				if(map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE").equals(Constants.TX_SUCCESS_CODE)){
					logger.info("发送签收申请结束");
					poolIn.setTransId(getStringVal(map.get("TRAN_RESULT_ARRAY.TRAN_ID")));//交易ID
					
					poolIn.setPlStatus(PoolComm.RC_03);//变更状态为：已发质押签收申请
					poolIn.setTaskDate(new Date());
					bill.setSDealStatus(PoolComm.DS_05);//签收处理中
					draftPoolInService.txStore(bill);
					draftPoolInService.txStore(poolIn);
					this.response(Constants.TX_SUCCESS_CODE, "质押签收申请发送成功", response, ret);
					return response;
				}else{
					this.response(Constants.TX_FAIL_CODE, "质押签收申请发送失败", response, ret);
					return response;
				}
			}
		}else{
			this.response(Constants.TX_FAIL_CODE, "质押签收申请发送失败", response, ret);
			return response;
		}
		return response;
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
	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}
}
