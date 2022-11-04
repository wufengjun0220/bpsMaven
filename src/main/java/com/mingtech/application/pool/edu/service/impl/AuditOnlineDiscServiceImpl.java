package com.mingtech.application.pool.edu.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.audit.service.AuditExtendService;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.discount.domain.IntroBillInfoBean;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxRateMaintainInfoService;
import com.mingtech.application.runmanage.domain.BusiTableConfig;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 在线贴现审价流程审批
 * @author Wu
 *
 */
public class AuditOnlineDiscServiceImpl extends GenericServiceImpl implements AuditExtendService {
	private static final Logger logger = Logger.getLogger(AuditOnlineDiscServiceImpl.class);


	@Autowired
	private AuditService auditService;
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	@Autowired
	private TxRateMaintainInfoService txRateMaintainInfoService;
	 
	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String txDealWithAuditResult(User user, String auditId,
			String busiId, String status, BusiTableConfig busiTabConfig)
			throws Exception {
		String json = "success";
		String approveComment = "success";
		
		if(PublicStaticDefineTab.AUDIT_STATUS_PASS.equals(status)){
			//当审批成功时 推送中台系统
			Ret ret = this.txDealAuditPass(approveComment,busiId,auditId,status,user);
			json = ret.getRET_MSG();
			
		}else if(PublicStaticDefineTab.AUDIT_STATUS_GOBACK.equals(status)){
			//当审批驳回时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(approveComment,busiId, status);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_RUNNING.equals(status)){
			this.txDealAudit(approveComment,busiId, status);
		}else if(PublicStaticDefineTab.AUDIT_STATUS_STOP.equals(status)){
			//当审批终止时  根据操作标识修改额度对象数据  撤销额度
			this.txCancelEdu(approveComment,busiId, status);
		}
		return json;
	}

	/**
	 * 审批通过时通知中台系统  比较审价批次下的票的利率与指导利率、优惠利率
	 * @return
	 * @throws Exception
	 */
	public Ret txDealAuditPass(String approveComment,String busiId,String auditId,String status,User user) throws Exception{
		ReturnMessageNew resp = new ReturnMessageNew();
		//查询审价信息
		TxReviewPriceInfo price = (TxReviewPriceInfo) this.load(busiId, TxReviewPriceInfo.class);
		price = txRateMaintainInfoService.queryTxReviewPriceInfo(price,user);
		
		String currTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		price.getTxReviewPriceDetail().setEffDate(currTime);
		
		//	过期释放票据
		if(Integer.parseInt(price.getDueDate().replaceAll("-", "")) < Integer.parseInt(currTime.replaceAll("-", ""))){
			String sql = "update t_bill_intro_info set status = '00' where batch_no = '" + price.getTxReviewPriceBatchNo()+"'";
			System.out.println("更新票据状态信息:"+sql);
			dao.updateSQL(sql.toString()); 
			
			throw new Exception("已超过申请有效日期，请重新申请");
		}
		
		//判断是那一条线审批
		ApproveAuditDto dto = (ApproveAuditDto) auditService.load(auditId,ApproveAuditDto.class);
		String approveBranchName = "";
		if(dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_01)){
			approveBranchName = "经办行";
		}else if (dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_0201)) {
			approveBranchName = "公司银行部";
		}else if (dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_0202)) {
			approveBranchName = "小企业金融部";
		}else if (dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_0203)) {
			approveBranchName = "科技金融部";
		}else if (dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_03)) {
			approveBranchName = "资产负债管理部";
		}else if (dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_08)) {
			approveBranchName = "金融市场部";
		}
		
		if(dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_01)){
			//分支行行长审批通过时	查询最新指导利率
			//保存指导利率方法
		
			txRateMaintainInfoService.queryAndUpdateRates(user);
			
			/**
			 * 若为直转通或再贴现终审为金融市场部(并且不需要比较利率)
			 */
			if(price.getTxType().equals("02") || price.getTxType().equals("03")){
				ApproveAuditBean approveAudit = new ApproveAuditBean();
				approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
				approveAudit.setProductId(PublicStaticDefineTab.APPROVAL_ROUTE_08);//金融市场部审批
				
				approveAudit.setCustCertNo(price.getCustNo()); //客户证件号码
				approveAudit.setBusiId(price.getId()); 
				approveAudit.setAuditAmt(dto.getAuditAmt()); // 总金额
				approveAudit.setBusiType(dto.getBusiType());//业务类型为条线权限审批
				approveAudit.setApplyNo(price.getTxReviewPriceBatchNo());
				
				Map<String, BigDecimal> mvelDataMap = new HashMap<String, BigDecimal>();
				mvelDataMap.put("amount", price.getApproveAmt());
			
				AuditResultDto retAudit = auditService.txCommitApplyAudit(user,null, approveAudit, mvelDataMap);
				if (!retAudit.isIfSuccess()) {
					// 没有配置审批路线
					if ("01".equals(retAudit.getRetCode())) {
						throw new Exception("没有配置审批路线");
					} else if ("02".equals(retAudit.getRetCode())) {
						throw new Exception("审批金额过大 ，所有审批节点 都没有权限");
					} else {
						throw new Exception("未找到审批人员");
					}
				}
				
				price.setApplyState(PublicStaticDefineTab.AUDIT_STATUS_RUNNING);// 提交审批
				dao.store(price);
				Ret ret = new Ret();
				ret.setRET_MSG("提交成功，已提交至金融市场部审批");
				resp.setRet(ret);
			}else{
				//判断贴现利率与优惠利率、指导利率：若贴现利率大于优惠利率且大于指导利率则直接在分支行结束审批
				boolean flag = false;
				List<IntroBillInfoBean> beans = txRateMaintainInfoService.queryTxIntroduceInfo(price);
//				price.setIntroBillInfoBeans(beans);
				for (int i = 0; i < beans.size(); i++) {
					IntroBillInfoBean info = beans.get(i);
					logger.info("审价明细的贴现利率为："+info.getApplyTxRate()+"审价明细的优惠利率为："+info.getFavorRate()+"审价明细的指导利率为："+info.getGuidanceRate());
					if(info.getApplyTxRate().compareTo(info.getGuidanceRate()) < 0 || info.getApplyTxRate().compareTo(info.getFavorRate()) < 0 ){
						//若有任意一条贴现利率小于优惠利率或者小于指导利率则需提交至下一条线审批
						flag = true;
						break;
					}
				}
				if(flag){
					String pro = "";
					//需提交至下一条线审批
					ApproveAuditBean approveAudit = new ApproveAuditBean();
					approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
					if(price.getTxReviewPriceDetail().getAuditType().equals(PublicStaticDefineTab.AUDIT_TYPE_01)){
						approveAudit.setProductId(PublicStaticDefineTab.APPROVAL_ROUTE_0201);//公司条线权限审批
						pro = "公司条线";
					}else if (price.getTxReviewPriceDetail().getAuditType().equals(PublicStaticDefineTab.AUDIT_TYPE_02)) {
						approveAudit.setProductId(PublicStaticDefineTab.APPROVAL_ROUTE_0202);//小微条线权限审批
						pro = "小微条线";
					}else{
						approveAudit.setProductId(PublicStaticDefineTab.APPROVAL_ROUTE_0203);//科金条线权限审批
						pro = "科金条线";
					}
					
					approveAudit.setCustCertNo(price.getCustNo()); //客户证件号码
					approveAudit.setBusiId(price.getId()); 
					approveAudit.setAuditAmt(dto.getAuditAmt()); // 总金额
					approveAudit.setBusiType(dto.getBusiType());//业务类型为条线权限审批
					approveAudit.setApplyNo(price.getTxReviewPriceBatchNo());
					
					Map<String, BigDecimal> mvelDataMap = new HashMap<String, BigDecimal>();
					mvelDataMap.put("amount", price.getApproveAmt());
				
					AuditResultDto retAudit = auditService.txCommitApplyAudit(user,null, approveAudit, mvelDataMap);
					if (!retAudit.isIfSuccess()) {
						// 没有配置审批路线
						if ("01".equals(retAudit.getRetCode())) {
							throw new Exception("没有配置审批路线");
						} else if ("02".equals(retAudit.getRetCode())) {
							throw new Exception("审批金额过大 ，所有审批节点 都没有权限");
						} else {
							throw new Exception("未找到审批人员");
						}
					}
					
					price.setApplyState(PublicStaticDefineTab.AUDIT_STATUS_RUNNING);// 提交审批
					dao.store(price);
					Ret ret = new Ret();
					ret.setRET_MSG("提交成功，已提交至"+pro+"（审批条线）审批");
					resp.setRet(ret);
				}else{
					
					
					/**
					 * 审批日期、机构
					 */
					price.setApproveDate(DateUtils.toString(new Date(),"yyyyMMdd"));
					price.setApproveBranchNo(user.getDepartment().getInnerBankCode());
					price.setApproveBranchName(approveBranchName);
					
					//通知中台系统审批结束：根据业务类型判断是额度审价通知还是票据审价通知
					price.setApproveBranchType("02");//分支行
					price.setApplyState("1");//通知中台生效
					if(price.getTxPattern().equals("01")){
						resp = centerPlatformSysService.txBillPriceMaintain(price,user);
					}else if (price.getTxPattern().equals("02")){
						resp = centerPlatformSysService.txAmtReciewPriceMaintain1(price,user);
					}else{
						resp = centerPlatformSysService.txAmtReciewPriceMaintain2(price,user);
					}
					if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
						//通知中台成功变更状态为生效
						price.setApplyState("1");
						resp.getRet().setRET_MSG("已生效");
					}else{
						//通知中台失败变更状态待发送
						price.setApplyState("SP_04");
					}
				}
				
			}
			
		}else if (dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_0201) || dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_0202)
					|| dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_0203)) {
			
			//保存优惠利率方法
			txRateMaintainInfoService.queryAndUpdateRates(user);
			
			//判断贴现利率与优惠利率、指导利率：若指导利率  > 每一条票据的贴现利率 > 优惠利率则在条线部分结束审批
			boolean flag = false;
			List<IntroBillInfoBean> beans = txRateMaintainInfoService.queryTxIntroduceInfo(price);
			for (int i = 0; i < beans.size(); i++) {
				IntroBillInfoBean info = beans.get(i);
				logger.info("审价明细的贴现利率为："+info.getApplyTxRate()+"审价明细的优惠利率为："+info.getFavorRate());
				
				if(info.getApplyTxRate().compareTo(info.getFavorRate()) < 0 ){
					//若有任意一条贴现利率小于优惠利率则需提交至下一条线审批
					flag = true;
					break;
				}
			}
			
			if(flag){
				//需提交至下一条线审批
				ApproveAuditBean approveAudit = new ApproveAuditBean();
				approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
				approveAudit.setProductId(PublicStaticDefineTab.APPROVAL_ROUTE_03);//资产负债部权限审批
				approveAudit.setCustCertNo(price.getCustNo()); //客户证件号码
				approveAudit.setBusiId(price.getId()); 
				approveAudit.setAuditAmt(dto.getAuditAmt()); // 总金额
				approveAudit.setBusiType(dto.getBusiType());//业务类型为保证金支取
				approveAudit.setApplyNo(price.getTxReviewPriceBatchNo());
				
				Map<String, BigDecimal> mvelDataMap = new HashMap<String, BigDecimal>();
				mvelDataMap.put("amount", price.getApproveAmt());
			
				AuditResultDto retAudit = auditService.txCommitApplyAudit(user,null, approveAudit, mvelDataMap);
				if (!retAudit.isIfSuccess()) {
					// 没有配置审批路线
					if ("01".equals(retAudit.getRetCode())) {
						throw new Exception("没有配置审批路线");
					} else if ("02".equals(retAudit.getRetCode())) {
						throw new Exception("审批金额过大 ，所有审批节点 都没有权限");
					} else {
						throw new Exception("未找到审批人员");
					}
				}
				
				price.setApplyState(PublicStaticDefineTab.AUDIT_STATUS_RUNNING);// 提交审批
				dao.store(price);
				Ret ret = new Ret();
				ret.setRET_MSG("提交成功，已提交至资产管理负债部审批");
				resp.setRet(ret);
			}else{
				price = txRateMaintainInfoService.queryTxReviewPriceInfo(price,user);
				/**
				 * 审批日期、机构
				 */
				price.setApproveDate(DateUtils.toString(new Date(),"yyyyMMdd"));
				price.setApproveBranchNo(user.getDepartment().getInnerBankCode());
				price.setApproveBranchName(approveBranchName);
				
				//通知中台系统审批结束：根据业务类型判断是额度审价通知还是票据审价通知
				price.setApproveBranchType("01");//条线
				price.setApplyState("1");//通知中台生效
				if(price.getTxPattern().equals("01")){
					resp = centerPlatformSysService.txBillPriceMaintain(price,user);
				}else if (price.getTxPattern().equals("02")){
					resp = centerPlatformSysService.txAmtReciewPriceMaintain1(price,user);
				}else{
					resp = centerPlatformSysService.txAmtReciewPriceMaintain2(price,user);
				}
				if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					//通知中台成功变更状态为生效
					price.setApplyState("1");
					resp.getRet().setRET_MSG("已生效");
				}else{
					//通知中台失败变更状态待发送
					price.setApplyState("SP_04");
				}
				
			}
			
		}else if (dto.getProductId().equals(PublicStaticDefineTab.APPROVAL_ROUTE_03)){
			
			price = txRateMaintainInfoService.queryTxReviewPriceInfo(price,user);
			/**
			 * 审批日期、机构
			 */
			price.setApproveDate(DateUtils.toString(new Date(),"yyyyMMdd"));
			price.setApproveBranchNo(user.getDepartment().getInnerBankCode());
			price.setApproveBranchName(approveBranchName);
			
			//资产负债部审批通过时
			//通知中台系统审批结束：根据业务类型判断是额度审价通知还是票据审价通知
			price.setApproveBranchType("03");//资产负债部
			price.setApplyState("1");//通知中台生效
			if(price.getTxPattern().equals("01")){
				resp = centerPlatformSysService.txBillPriceMaintain(price,user);
			}else if (price.getTxPattern().equals("02")){
				resp = centerPlatformSysService.txAmtReciewPriceMaintain1(price,user);
			}else{
				resp = centerPlatformSysService.txAmtReciewPriceMaintain2(price,user);
			}
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				//通知中台成功变更状态为生效
				price.setApplyState("1");
				resp.getRet().setRET_MSG("已生效");
			}else{
				//通知中台失败变更状态待发送
				price.setApplyState("SP_04");
			}
		}else{
			/**
			 * 再贴现还需资产负债部审批
			 */
			if(price.getTxType().equals("03")){
				ApproveAuditBean approveAudit = new ApproveAuditBean();
				approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
				approveAudit.setProductId(PublicStaticDefineTab.APPROVAL_ROUTE_03);//
				
				approveAudit.setCustCertNo(price.getCustNo()); //客户证件号码
				approveAudit.setBusiId(price.getId()); 
				approveAudit.setAuditAmt(dto.getAuditAmt()); // 总金额
				approveAudit.setBusiType(dto.getBusiType());//业务类型为条线权限审批
				approveAudit.setApplyNo(price.getTxReviewPriceBatchNo());
				
				Map<String, BigDecimal> mvelDataMap = new HashMap<String, BigDecimal>();
				mvelDataMap.put("amount", price.getApproveAmt());
			
				AuditResultDto retAudit = auditService.txCommitApplyAudit(user,null, approveAudit, mvelDataMap);
				if (!retAudit.isIfSuccess()) {
					// 没有配置审批路线
					if ("01".equals(retAudit.getRetCode())) {
						throw new Exception("没有配置审批路线");
					} else if ("02".equals(retAudit.getRetCode())) {
						throw new Exception("审批金额过大 ，所有审批节点 都没有权限");
					} else {
						throw new Exception("未找到审批人员");
					}
				}
				
				price.setApplyState(PublicStaticDefineTab.AUDIT_STATUS_RUNNING);// 提交审批
				dao.store(price);
				Ret ret = new Ret();
				ret.setRET_MSG("提交成功，已提交至资产管理负债部审批");
				resp.setRet(ret);
			}else{
				price = txRateMaintainInfoService.queryTxReviewPriceInfo(price,user);
				
				/**
				 * 审批日期、机构
				 */
				price.setApproveDate(DateUtils.toString(new Date(),"yyyyMMdd"));
				price.setApproveBranchNo(user.getDepartment().getInnerBankCode());
				price.setApproveBranchName(approveBranchName);
				
				//直转通、再贴现终审通知中台
				price.setApproveBranchType("04");//金融市场部
				price.setApplyState("1");//通知中台生效
				if(price.getTxType().equals("02") || price.getTxType().equals("03")){
					resp = centerPlatformSysService.txBillPriceMaintain(price,user);
				}
				if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					//通知中台成功变更状态为生效
					price.setApplyState("1");
					resp.getRet().setRET_MSG("已生效");
				}else{
					//通知中台失败变更状态待发送
					price.setApplyState("SP_04");
				}
			}
			
			
		}
		
		/**
		 * 提交票据状态为00
		 * 提交审批过程中为01
		 * 提交中台成功后为1
		 * */
		String billStatus = "1";
		if(!"1".equals(price.getApplyState())){	//	调用中台成功更新票据状态
			billStatus = "00";
//		}else{
		}
		
		String sql = "update t_bill_review_price_detail set eff_date = '" + currTime + "' where batch_no = '" + price.getTxReviewPriceBatchNo()+"'";
		System.out.println("更新审价生效日期信息:"+sql);
		dao.updateSQL(sql.toString()); 
		dao.flush();
		dao.clear();


		sql = "update t_bill_intro_info set status = '" + billStatus + "' where batch_no = '" + price.getTxReviewPriceBatchNo()+"'";
		System.out.println("更新票据状态信息:"+sql);
		dao.updateSQL(sql.toString()); 
		
		centerPlatformSysService.txStore(price);
	
		return resp.getRet();
	}
	
	//当审批中
	public void txDealAudit(String approveComment,String id,String status) throws Exception{
		//查询票据
		TxReviewPriceInfo price = (TxReviewPriceInfo) this.load(id, TxReviewPriceInfo.class);
		price.setApplyState(status);
		centerPlatformSysService.txStore(price);
	}

	/**
	 * 	当审批驳回或拒绝时 
	 * @param id
	 * @param status
	 * @throws Exception
	 */
	public void txCancelEdu(String approveComment,String id,String status) throws Exception{
		//查询票据
		TxReviewPriceInfo price = (TxReviewPriceInfo) this.load(id, TxReviewPriceInfo.class);
		price.setApplyState(status);
		centerPlatformSysService.txStore(price);
	}


	
}
