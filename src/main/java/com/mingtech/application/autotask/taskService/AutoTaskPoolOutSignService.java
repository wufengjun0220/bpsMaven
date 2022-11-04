package com.mingtech.application.autotask.taskService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;
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
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.DepartmentServiceFactory;
import com.mingtech.framework.common.util.StringUtil;

/**
 *自动任务业务处理类
 *解质押签收
 *gcj 20210513
 */


public class AutoTaskPoolOutSignService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolOutSignService.class);
	DraftPoolOutService draftPoolOutService = PoolCommonServiceFactory.getDraftPoolOutService();
	PoolEcdsService poolEcdsService = PoolCommonServiceFactory.getPoolEcdsService();
	PedProtocolService pedProtocolService = PoolCommonServiceFactory.getPedProtocolService();
	DepartmentService departmentService = DepartmentServiceFactory.getDepartmentService();
	PoolBatchNoUtils poolBatchNoUtils = PoolCommonServiceFactory.getPoolBatchNoUtils();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	ConsignService consignService = ConsignServiceFactory.getConsignService();
	
	/**
	 * gcj 20210513 解质押签收
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
			this.response(Constants.TX_FAIL_CODE, "票据【"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "未能找到出池业务明细实体或出池申请前置状态不为已记账", response, ret);
			return response;
		}
		
		if(null!=queryType){
			/**
			 * 查询bbsp 状态是否为解质押签收状态  不为此状态发送解质押签收
			 * 调取电票接口，获取解质押签收成功结果。
			 */
			PoolBillInfo info = draftPoolOutService.loadByBillNo(out.getPlDraftNb(), out.getBeginRangeNo(), out.getEndRangeNo());
			ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
			
			
			logger.info("票据【"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "的票据发送电子交易类查询开始");
			
			
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
			infoMap.put("QUERY_INFO_ARRAY.MAX_ACCEPTOR_DATE",out.getPlIsseDt());//出票日期上限
			infoMap.put("QUERY_INFO_ARRAY.MIN_ACCEPTOR_DATE",out.getPlIsseDt());//出票日期下限
			infoMap.put("QUERY_INFO_ARRAY.MAX_DUE_DATE",out.getPlDueDt());//汇票到期日期上限
			infoMap.put("QUERY_INFO_ARRAY.MIN_DUE_DATE",out.getPlDueDt());//汇票到期日期下限
			infoMap.put("QUERY_INFO_ARRAY.MAX_AMT",out.getTradeAmt());//票据最高金额
			infoMap.put("QUERY_INFO_ARRAY.MIN_AMT",out.getTradeAmt());//票据最低金额
			infoMap.put("QUERY_INFO_ARRAY.QUERY_TYPE","QT00");//查询类型
			infoMap.put("QUERY_INFO_ARRAY.TRAN_NO_LIST",PoolComm.NES_0102010);//交易编号集合
			infoMap.put("QUERY_INFO_ARRAY.BILL_SOURCE_LIST",out.getDraftSource());//集合
			poolTrans.getDetails().add(infoMap);

			
			
			ReturnMessageNew resp= poolEcdsService.txApplyImplawnForSign(poolTrans);
			List listDetails = resp.getDetails();
			if(listDetails == null || listDetails.size() == 0){//表示查询的结果为签收成功
				//已签收唤醒签收后的相应流程
				draftPoolOutService.txTransTypePoolOut(out, "2", info);
				this.response(Constants.TX_SUCCESS_CODE, "票据【"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "】状态质押已签收，无需再次发起", response, ret);
    			return response;
			}else {//表示查询的结果为签收失败
				out.setPlStatus(PoolComm.CC_03);//状态变更为质押待签收重新发送签收申请
				draftPoolOutService.txStore(out);
			}
		}
		
		out.setTaskDate(new Date());
		
		PedProtocolDto dto = pedProtocolService.queryProtocolDto( null, null,out.getPoolAgreement(), null, null, null);

		
		/**
		 * 调取解质押签收申请
		 * wfj
		 */
		ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
		PoolBillInfo bill = draftPoolOutService.loadByBillNo(out.getPlDraftNb(),out.getBeginRangeNo(),out.getEndRangeNo());
	
		/**
		 * 质押签收  body 需上传的值
		 */
		//若电子签名为空设置为0
		if(out.getElsignature()!=null&&!out.getElsignature().equals("")){
			poolTrans.setSignature(out.getElsignature());//网银发过来的电子签名
		}else{
			poolTrans.setSignature("0");//电子签名
		}
		poolTrans.setAcceptorAcctNo(out.getAccNo());//电票签约账号	
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
		 * 缺少强制贴现标志
		 */
		Map infoMap = new HashMap();
//		infoMap.put("REQUEST_INFO_ARRAY.TRAN_ID","");//交易id  必输
		infoMap.put("REQUEST_INFO_ARRAY.TRAN_ID",out.getTransId());//交易id  必输
		infoMap.put("REQUEST_INFO_ARRAY.BILL_SOURCE",bill.getDraftSource());//票据来源  必输
		infoMap.put("REQUEST_INFO_ARRAY.BILL_NO",bill.getSBillNo());//票据（包）号码
		infoMap.put("REQUEST_INFO_ARRAY.BILL_ID",bill.getDiscBillId());//票据ID
		infoMap.put("REQUEST_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
		infoMap.put("REQUEST_INFO_ARRAY.FORCE_DISCOUNT_FLAG",out.getTXFlag());//强制贴现标志 0否 1是
		infoMap.put("REQUEST_INFO_ARRAY.BUSS_TYPE","201902");//业务类型
		infoMap.put("REQUEST_INFO_ARRAY.SIGN_FLAG","1");//签收标识  必输
		String seq = poolBatchNoUtils.txGetFlowNo();
		infoMap.put("REQUEST_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
		poolTrans.getDetails().add(infoMap);
		
		
		
		logger.info("票据【"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "】的票,发送签收申请开始");
		try {
			
			/**
			 * 判断是否做过拆票,若做过需更新在池票据的持票id
			 */
			if(StringUtils.isNotBlank(out.getSplitId())){
				PoolBillInfo info = draftPoolInService.loadBySplit(out.getSplitId(), "DS_02");

				PoolQueryBean bean = new PoolQueryBean();
				bean.setBillNo(out.getPlDraftNb());
				bean.setSStatusFlag(PoolComm.DS_02);
				
				/********************融合改造新增 start******************************/
				bean.setBeginRangeNo(info.getBeginRangeNo());
				bean.setEndRangeNo(info.getEndRangeNo());
				/********************融合改造新增 end******************************/
				
				DraftPool draft=consignService.queryDraftByBean(bean).get(0);
				draft.setLockz(PoolComm.BBSPLOCK_02);
				/**
				 * 通过电票查询持票id
				 */
				ECDSPoolTransNotes note = new ECDSPoolTransNotes();
				note.setBranchNo(ment.getAuditBankCode());//解质押机构号
				note.setBillId(info.getDiscBillId());
				note.setBeginRangeNo(draft.getBeginRangeNo());
				logger.info("查询到的在池票据的电票系统id为:"+info.getDiscBillId()+"、票据起始号未："+draft.getBeginRangeNo());
				ReturnMessageNew message = poolEcdsService.txQueryHildId(note);
				if(message.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					Map map = message.getBody();
					String hilrId = getStringVal(map.get("BILL_ID"));
					draft.setHilrId(hilrId);
					info.setHilrId(hilrId);
					draftPoolOutService.txStore(info);
					draftPoolOutService.txStore(draft);
					logger.info("拆分后在池票据查询的持票id为:"+hilrId);
				}else{
					logger.info("持票id查询失败,错误信息为:"+message.getRet().getRET_MSG());
				}
				
			}
			
			ReturnMessageNew resp = poolEcdsService.txApplySignReject(poolTrans);
			logger.info("接收到解质押签收返回，返回code值："+resp.getRet().getRET_CODE());
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				List details = resp.getDetails();
				logger.info("接收到解质押签收返回，返回数据："+details);
				for (int i = 0; i < details.size(); i++) {
					Map map = (Map) details.get(i);
					logger.info("接收到解质押签收返回，返回TRAN_RESULT_ARRAY.TRAN_RET_CODE值："+map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE"));
					if(map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE").equals(Constants.TX_SUCCESS_CODE)){
						logger.info("发送签收申请结束");
						out.setTransId(getStringVal(map.get("TRAN_RESULT_ARRAY.TRAN_ID")));//交易ID
						
						out.setPlStatus(PoolComm.CC_04);//已发解质押签收申请
						bill.setSDealStatus(PoolComm.DS_05);//签收处理中
						
						/**
						 *若为拆分票据这里需对网银经办锁解锁 
						 */
						if(out.getSplitId() != null && StringUtils.isNotBlank(out.getSplitId())){
							PoolBillInfo billIn = draftPoolInService.loadBySplit(out.getSplitId(),PoolComm.DS_02);
							if(billIn != null){
								billIn.setEbkLock(PoolComm.EBKLOCK_02);
								
								draftPoolInService.txStore(billIn);
								//DOTO查询持票id
							}
						}
						
						draftPoolOutService.txStore(bill);
						draftPoolOutService.txStore(out);
						this.response(Constants.TX_SUCCESS_CODE, "电票出池【"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() +"】发解质押签收成功", response, ret);
						return response;
					}else{
						out.setPlStatus(PoolComm.CC_05_1);//已发解质押签收申请
						draftPoolOutService.txStore(out);
						this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() +"】发解质押签收失败", response, ret);
						return response;
					}
				}
			}else{
				out.setPlStatus(PoolComm.CC_05_1);//已发解质押签收申请
				draftPoolOutService.txStore(out);
				this.response(Constants.TX_FAIL_CODE, "电票出池【"+out.getPlDraftNb()+",票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() +"】发解质押签收失败", response, ret);
				return response;
			}
		} catch (Exception e) {
			logger.error("发送签收申请异常", e);
			out.setPlStatus(PoolComm.CC_05_1);//已发解质押签收申请
			draftPoolOutService.txStore(out);
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
}
