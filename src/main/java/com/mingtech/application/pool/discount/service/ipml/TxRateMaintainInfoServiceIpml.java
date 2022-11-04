package com.mingtech.application.pool.discount.service.ipml;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.excel.util.StringUtils;
import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.ecds.common.BatchNoUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.discount.domain.BankRoleMappingBean;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.domain.IntroBillInfoBean;
import com.mingtech.application.pool.discount.domain.TxRateDetailBean;
import com.mingtech.application.pool.discount.domain.TxRateDetailBeanPO;
import com.mingtech.application.pool.discount.domain.TxRateMaintainInfo;
import com.mingtech.application.pool.discount.domain.TxReduceInfoBean;
import com.mingtech.application.pool.discount.domain.TxReviewPriceDetail;
import com.mingtech.application.pool.discount.domain.TxReviewPriceInfo;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.discount.service.TxRateMaintainInfoService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.ConnectionUtils;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.common.util.UUID;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


public class TxRateMaintainInfoServiceIpml extends GenericServiceImpl implements TxRateMaintainInfoService{
	private static final Logger logger = Logger.getLogger(TxRateMaintainInfoServiceIpml.class);
	
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	
	@Autowired
	private BatchNoUtils batchNoUtils;
	
	@Autowired
	private PoolEcdsService poolEcdsService; 
	
	@Autowired
	private AuditService auditService;
	
	@Autowired
	private BlackListManageService blackListManageService ; 
	
	/**
	 * 查询中台利率数据并更新本地数据
	 * */
	@Override
	public boolean queryAndUpdateRates(User user)throws Exception{
		boolean flag = false;
		if(queryGuideRateAndUpdate("",user)){
			flag = queryFavorRateAndUpdate("",user);
		}
		return flag;
	}
	
	/**
	 * 查询中台最优惠利率并更新
	 * @throws Exception 
	 * */
	@Override
	public boolean queryAndUpdateBest(User user) throws Exception{
		TxRateMaintainInfo info = new TxRateMaintainInfo();
		info.setRateType("03");
		info.setEffState("2");
		List<TxRateMaintainInfo> infos = getTxRateMaintainInfoJSON("isNotHis",info , null, user);
		
		for (TxRateMaintainInfo txRateMaintainInfo : infos) {
			if(!DateUtils.checkOverLimited(DateUtils.parse(txRateMaintainInfo.getEffTime()), new Date())){
				String sql = "update T_RATE_MAINTAININFO set EFFSTATE = '0' where RATETYPE = '03' and EFFSTATE = '1'";
				dao.updateSQL(sql);
				
				txRateMaintainInfo.setEffState("1");
				updateTxRateInfo(txRateMaintainInfo);
			}
		}
		return true;
	}
	
	/**
	 * 查询中台已生效指导利率并更新处理
	 * @throws Exception 
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean queryAviGuideRateAndUpdate(User user) throws Exception{
		ReturnMessageNew messageNew = new ReturnMessageNew();
		//	1、调用中台查询已生效的指导利率
		messageNew = centerPlatformSysService.txQueryAviRate(user);
		
		List<Map> lists = messageNew.getDetails();
		
		TxRateMaintainInfo rateMaintainInfo = new TxRateMaintainInfo();
		List<TxRateDetailBean> txRateDetailBeans = new ArrayList<TxRateDetailBean>();
		
		if(lists.size() > 0){
			for (Map map : lists) {
				if(map != null){
					TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
					txRateDetailBean.setBatchNo(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.APPROVE_BATCH_NO")));			//	批次号
					txRateDetailBean.setBankType(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE")));		//	承兑行别
					txRateDetailBean.setEffTime(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.EFFECTIVE_DATE")));				//	生效日期
					txRateDetailBean.setStatus(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE_STATUS")));	//	指导利率状态		
					txRateDetailBean.setRate(new BigDecimal(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE"))));	//	利率值
//					txRateDetailBean.setId(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE_KEY")));					
					txRateDetailBean.setTerm((Integer) map.get("INT_RATE_ARRAY.TERM"));									//	期限
					
					rateMaintainInfo.setBatchNo(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.APPROVE_BATCH_NO")));
					rateMaintainInfo.setEffState(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE_STATUS")));
					txRateDetailBeans.add(txRateDetailBean);
				}
			}
			rateMaintainInfo.setRateDetailBeans(txRateDetailBeans);
			if(txRateDetailBeans.size() > 0){
				rateMaintainInfo.setRateType("01");
				updateTxAviRate(rateMaintainInfo);
			}
		}
		
		return true;
	}
	
	/**
	 * 查询中台指导利率并更新处理
	 * @throws Exception 
	 * */
	@Override
	public boolean queryGuideRateAndUpdate(String batchNo,User user) throws Exception{
		if(StringUtil.isEmpty(batchNo)){
			TxRateMaintainInfo info =  new TxRateMaintainInfo();
			info.setRateType("01");
			List<TxRateMaintainInfo> infos = getTxRateMaintainInfoJSON("isSyn",info, null, user);
			if(infos.size() > 0){
				for (TxRateMaintainInfo txRateMaintainInfo : infos) {
					guideRateDeal(txRateMaintainInfo.getBatchNo(), user);
				}
			}
		}else{
			guideRateDeal(batchNo, user);
		}
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean guideRateDeal(String batchNo,User user) throws Exception{
		ReturnMessageNew messageNew = new ReturnMessageNew();
		//	1、调用中台查询指导利率
		CenterPlatformBean bean = new CenterPlatformBean();
		bean.setBatchNo(batchNo);
		messageNew = centerPlatformSysService.txQueryGuideRate(bean,user);
		
		List<Map> lists = messageNew.getDetails();
		
		TxRateMaintainInfo rateMaintainInfo = new TxRateMaintainInfo();
		List<TxRateDetailBean> txRateDetailBeans = new ArrayList<TxRateDetailBean>();
		//	2、返回有数据和本地数据做更新处理
		if(lists.size() > 0){
			for (Map map : lists) {
				if(map != null){
					TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
					txRateDetailBean.setBatchNo(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.APPROVE_BATCH_NO")));					//	批次号
					txRateDetailBean.setBankType(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE")));				//	承兑行别
					txRateDetailBean.setEffTime(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.EFFECTIVE_DATE")));					//	生效日期
					txRateDetailBean.setStatus(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE_STATUS")));			//	指导利率状态		
					txRateDetailBean.setRate(new BigDecimal(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE"))));	//	利率值
//					txRateDetailBean.setId(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE_KEY")));					
					txRateDetailBean.setTerm(Integer.parseInt(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.TERM"))));									//	期限
					
					rateMaintainInfo.setBatchNo(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.APPROVE_BATCH_NO")));
					rateMaintainInfo.setEffState(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.GUIDANCE_INT_RATE_STATUS")));
					txRateDetailBeans.add(txRateDetailBean);
				}
			}
			
			rateMaintainInfo.setRateDetailBeans(txRateDetailBeans);
			
			if(txRateDetailBeans.size() > 0){
				rateMaintainInfo.setRateType("01");
				updateTxAviRate(rateMaintainInfo);
			}
		}
		return true;
	}
	
	/**
	 * 查询中台优惠利率并更新处理
	 * */
	@Override
	public boolean queryFavorRateAndUpdate(String batchNo,User user) throws Exception{
		if(StringUtil.isEmpty(batchNo)){
			TxRateMaintainInfo rateMaintainInfo = new TxRateMaintainInfo();
			rateMaintainInfo.setRateType("02");
			List<TxRateMaintainInfo> infos = getTxRateMaintainInfoJSON("isSyn", rateMaintainInfo, null, user);
			if(infos.size() > 0){
				for (TxRateMaintainInfo txRateMaintainInfo : infos) {
					favorRateDeal(txRateMaintainInfo.getBatchNo(), user);
				}
			}
		}else{
			favorRateDeal(batchNo, user);
		}
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean favorRateDeal(String batchNo,User user) throws Exception{
		ReturnMessageNew messageNew = new ReturnMessageNew();
		//	1、调用中台查询优惠利率
		CenterPlatformBean bean = new CenterPlatformBean();
		bean.setBatchNo(batchNo);
		messageNew = centerPlatformSysService.txQueryFavorRate(bean,user);
		
		List<Map> lists = messageNew.getDetails();
		
		//	2、返回结果更新处理
		TxRateMaintainInfo rateMaintainInfo = new TxRateMaintainInfo();
		List<TxRateDetailBean> txRateDetailBeans = new ArrayList<TxRateDetailBean>();
		
		if(lists.size() > 0){
			for (Map map : lists) {
				if(map != null){
					TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
					txRateDetailBean.setId(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.AUTH_INT_RATE_KEY")));				//	id
					txRateDetailBean.setBatchNo(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.APPROVE_BATCH_NO")));			//	批次号
					txRateDetailBean.setBankType(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE")));		//	承兑行别
					txRateDetailBean.setEffTime(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.EFFECTIVE_DATE")));				//	生效日期
					txRateDetailBean.setStatus(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ORG_INT_RATE_STATUS")));		//	状态
					txRateDetailBean.setRate(new BigDecimal(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.AUTH_INT_RATE"))));	//	利率值
//					txRateDetailBean.setId(StringUtil.getStringVal(map.get("AUTH_INT_RATE_KEY")));					
					txRateDetailBean.setTerm(Integer.parseInt(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.TERM"))));									//	期限
					
					rateMaintainInfo.setBatchNo(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.APPROVE_BATCH_NO")));
					rateMaintainInfo.setEffState(StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ORG_INT_RATE_STATUS")));
					txRateDetailBeans.add(txRateDetailBean);
				}
			}
			rateMaintainInfo.setRateDetailBeans(txRateDetailBeans);
			
			if(txRateDetailBeans.size() > 0){
				rateMaintainInfo.setRateType("02");
				updateTxAviRate(rateMaintainInfo);
			}
		}
		return true;
	}
	
	/**
	 * 查询中台票据审价信息并更新保存本地数据
	 * */
	public boolean queryAndUpdatebill() throws Exception{
		//	1、调用中台接口
		
		
		
		return false;
	}

	/**
	 * 查询在线贴现利率
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public List<TxRateMaintainInfo> getTxRateMaintainInfoJSON(
			String queryType,TxRateMaintainInfo txRateMaintainInfo, Page page, User user) throws Exception {
		List<String> paras = new ArrayList<String>();
		String sb = " select trm from TxRateMaintainInfo as trm where 1=1 ";
		if (StringUtil.isNotBlank(txRateMaintainInfo.getHandler())) {
			sb += " and trm.handler like ?";
			paras.add("%" + txRateMaintainInfo.getHandler() + "%");
		}
		
		if (StringUtil.isNotBlank(txRateMaintainInfo.getHandlerNo())) {
			sb += " and trm.handlerNo = ?";
			paras.add(txRateMaintainInfo.getHandlerNo());
		}
		
		if (StringUtil.isNotBlank(txRateMaintainInfo.getRateType())) {
			sb += " and trm.rateType = ?";
			paras.add(txRateMaintainInfo.getRateType());
		}
		
		if ( null!=txRateMaintainInfo.getMaintainStartDate()) {
			sb +=" and trm.maintainTime >= '" + txRateMaintainInfo.getMaintainStartDate() + "'";
		}
		
		if ( null!=txRateMaintainInfo.getMaintainEndDate()) {
			sb +=" and trm.maintainTime <= '" + txRateMaintainInfo.getMaintainEndDate() + "'";
		}
		
		if ( null!=txRateMaintainInfo.getEffStartDate()) {
			sb +=" and trm.effTime >= '" + txRateMaintainInfo.getEffStartDate() + "'";
		}
		
		if ( null!=txRateMaintainInfo.getEffEndDate()) {
			sb +=" and trm.effTime <= '" + txRateMaintainInfo.getEffEndDate() + "'";
		}
		
		//  失效时间条件
		if ( null!=txRateMaintainInfo.getExpireStartDate()) {
			sb +=" and trm.lastUpdateTime >= TO_DATE('" + txRateMaintainInfo.getExpireStartDate() + "', 'yyyy-MM-dd')";
		}
		
		if ( null!=txRateMaintainInfo.getExpireEndDate()) {
			sb +=" and trm.lastUpdateTime <= TO_DATE('" + txRateMaintainInfo.getExpireEndDate() + "', 'yyyy-MM-dd')";
		}
		
		if(null!=txRateMaintainInfo.getExpireStartDate() || null!=txRateMaintainInfo.getExpireEndDate()){
			sb += " and trm.effState = '0'";
		}else{
			if("isNotHis".equals(queryType)){		//	普通查询不查失效数据
				sb += " and trm.effState not in ('0','SP_00')";
			}else if("isHis".equals(queryType)){	//	历史记录查询   查询生效和失效数据
				sb += " and trm.effState in('1','0','SP_00')";
			}else if("isSyn".equals(queryType)){	//	查询可以和中台交互的数据
				sb += " and trm.effState in('1','2','SP_04')";
			}
		}
		
		if(null != txRateMaintainInfo.getEffState()){
			if("01".equals(txRateMaintainInfo.getEffState())){	//	审批中的数据
				sb += " and trm.effState in('SP_01','SP_02','SP_04')";
			}else{
				sb +=" and trm.effState = ?";
				paras.add(txRateMaintainInfo.getEffState());
			}
		}
		
		if (StringUtil.isNotBlank(txRateMaintainInfo.getId())) {
			sb +=" and trm.id = ?";
			paras.add(txRateMaintainInfo.getId());
		}
		
		if (StringUtil.isNotBlank(txRateMaintainInfo.getBatchNo())) {
			sb +=" and trm.batchNo = ?";
			paras.add(txRateMaintainInfo.getBatchNo());
		}
		
		sb += " order by trm.batchNo DESC";
		
		List<TxRateMaintainInfo> txRateMaintainInfos = find(sb, paras, page);
		return txRateMaintainInfos;
	}
	
	
	/**
	 * 查询在线贴现利率
	 * */
	@SuppressWarnings("unchecked")
	private List<TxRateDetailBean> getTxRateDetail(TxRateMaintainInfo txRateMaintainInfo) throws Exception {
		List<String> paras = new ArrayList<String>();
		String sb = " select trm from TxRateDetailBean as trm where 1=1 ";
		
		
		if ( null!=txRateMaintainInfo.getBatchNo()) {
			sb +=" and trm.batchNo = ?";
			paras.add(txRateMaintainInfo.getBatchNo());
		}
		
//		sb += " order by trm.lastUpdateTime DESC";
		
		List<TxRateDetailBean> txRateDetailBeans = find(sb, paras);
		return txRateDetailBeans;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TxRateDetailBean getTxRateDetail(TxRateDetailBean txRateMaintainInfo) throws Exception {
		List paras = new ArrayList();
		String sb = " select trm from TxRateDetailBean as trm where 1=1 ";
		
		
		if ( null!=txRateMaintainInfo.getBatchNo()) {
			sb +=" and trm.batchNo = ?";
			paras.add(txRateMaintainInfo.getBatchNo());
		}
		
		if (txRateMaintainInfo.getTerm() != 0) {
			sb +=" and trm.term = ?";
			paras.add(txRateMaintainInfo.getTerm());
		}
		
		if ( null!=txRateMaintainInfo.getBankType()) {
			sb +=" and trm.bankType = ?";
			paras.add(txRateMaintainInfo.getBankType());
		}
		
//		sb += " order by trm.lastUpdateTime DESC";
		
		List<TxRateDetailBean> txRateDetailBeans = find(sb, paras);
		
		if(txRateDetailBeans.size() > 0){
			return txRateDetailBeans.get(0);
		}
		
		return null;
	}
	
	/**
	 * 查询贴现利率详情
	 * */
	@Override
	public List<TxRateDetailBeanPO> queryTxRateDetails(TxRateMaintainInfo txRateMaintainInfo,User user) throws Exception {
		if("1".equals(txRateMaintainInfo.getEffState()) || txRateMaintainInfo.getEffState() == null){
			//  查询中台有效利率并更新处理
			/*if("03".equals(txRateMaintainInfo.getRateType())){	//	查询中台最优惠利率并更新处理
				CenterPlatformBean bean = new CenterPlatformBean();
				queryAndUpdateBest(bean,user);
			}else */if ("02".equals(txRateMaintainInfo.getRateType())) {	//	查询中台优惠利率并更新处理
				queryFavorRateAndUpdate(txRateMaintainInfo.getBatchNo(),user);
			}else if ("01".equals(txRateMaintainInfo.getRateType())) {	//	查询中台指导利率并更新处理
				queryGuideRateAndUpdate(txRateMaintainInfo.getBatchNo(),user);
			}
		}
		
		return queryRateDetails(txRateMaintainInfo);
	}
	
	/**
	 * 查询贴现测算利率详情
	 * */
	@Override
	public List<TxRateDetailBeanPO> queryTxRateDetails1(TxRateMaintainInfo txRateMaintainInfo,User user) throws Exception {
		if("1".equals(txRateMaintainInfo.getEffState())){
			//  查询中台有效利率并更新处理
			/*if("03".equals(txRateMaintainInfo.getRateType())){	//	查询中台最优惠利率并更新处理
				CenterPlatformBean bean = new CenterPlatformBean();
				queryAndUpdateBest(bean,user);
			}else */if ("02".equals(txRateMaintainInfo.getRateType())) {	//	查询中台优惠利率并更新处理
				queryFavorRateAndUpdate("",user);
			}else if ("01".equals(txRateMaintainInfo.getRateType())) {	//	查询中台指导利率并更新处理
				queryGuideRateAndUpdate("",user);
			}
		}
		
		return queryRateDetails1(txRateMaintainInfo,user);
	}
	
	/**
	 * 删除利率相关信息
	 * */
	@Override
	public boolean deleteTxRate(TxRateMaintainInfo txRateMaintainInfo) throws Exception {
		logger.debug("删除利率信息表开始...");
		
		if(StringUtil.isNotBlank(txRateMaintainInfo.getBatchNo())){
			String sql = "delete from t_rate_maintainInfo t where 1 = 1 and effState <> '04'";
			
			sql += " and batchNo ='"+txRateMaintainInfo.getBatchNo()+"'";
			ConnectionUtils.toExecuteUpdateSql(sql);
			
			if(txRateMaintainInfo.getRateType() != "03"){
				String sql1 = "delete from t_ratedetails t where 1 = 1";
				
				sql1 += " and ratetype = '" + txRateMaintainInfo.getRateType() + "'";
				sql1 += " and batchNo = '" + txRateMaintainInfo.getBatchNo() + "'";
				ConnectionUtils.toExecuteUpdateSql(sql1);
			}
		}
		
		logger.debug("删除利率信息表结束...");
		
		return false;
	}
	
	/**
	 * 查询审价基础信息
	 * */
	@Override
	public List<TxReviewPriceInfo> queryTxReviewPriceInfo(CenterPlatformBean centerPlatformBean,Page page) throws Exception {
		List<String> paras = new ArrayList<String>();
		String sb = " select trm from TxReviewPriceInfo as trm where 1=1 ";
		//	经办人
		if (StringUtil.isNotBlank(centerPlatformBean.getWorkerName())) {
			sb += " and trm.workerName like ?";
			paras.add("%" + centerPlatformBean.getWorkerName() + "%");
		}
		
		//	贴现定价审批编号
		if (StringUtil.isNotBlank(centerPlatformBean.getBatchNo())) {
			sb += " and trm.txReviewPriceBatchNo = ?";
			paras.add(centerPlatformBean.getBatchNo());
		}
		
		//	申请日期
		if ( null != centerPlatformBean.getApplyStartDate()) {
			sb +=" and TO_DATE(trm.applyDate, 'yyyy-MM-dd') >= TO_DATE('" + centerPlatformBean.getApplyStartDate() + "', 'yyyy-MM-dd')";
		}
		
		if ( null != centerPlatformBean.getApplyEndDate()) {
			sb += " and TO_DATE(trm.applyDate, 'yyyy-MM-dd') <= TO_DATE('" + centerPlatformBean.getApplyEndDate() + "', 'yyyy-MM-dd')";
		}
		
		//	状态
		if (StringUtil.isNotBlank(centerPlatformBean.getTxState())) {
			
			
			String currTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			if ("0".equals(centerPlatformBean.getTxState())) {
				sb +=" and TO_DATE(trm.dueDate, 'yyyy-MM-dd') < TO_DATE('" + currTime + "', 'yyyy-MM-dd')";
				sb += " and (trm.applyState = '0' or trm.applyState = '1')";
			}else if ("1".equals(centerPlatformBean.getTxState())) {
				sb +=" and TO_DATE(trm.dueDate, 'yyyy-MM-dd') >= TO_DATE('" + currTime + "', 'yyyy-MM-dd')";
				sb += " and trm.applyState = '1'";
			}else{
				sb += " and trm.applyState = ?";
				paras.add(centerPlatformBean.getTxState());
			}
		}
		
		//	在线贴现协议号
		if (StringUtil.isNotBlank(centerPlatformBean.getOnlineNo())) {
			sb += " and trm.onlineNo = ?";
			paras.add(centerPlatformBean.getOnlineNo());
		}
		
		//	客户名称
		if (StringUtil.isNotBlank(centerPlatformBean.getCustName())) {
			sb += " and trm.custName like ?";
			paras.add("%" + centerPlatformBean.getCustName() + "%");
		}
		
		//	在线贴现协议号
		if (StringUtil.isNotBlank(centerPlatformBean.getCustNo())) {
			sb += " and trm.custNo = ?";
			paras.add(centerPlatformBean.getCustNo());
		}
		
		//		在线贴现协议号
		if (StringUtil.isNotBlank(centerPlatformBean.getTxType())) {
			sb += " and trm.txType = ?";
			paras.add(centerPlatformBean.getTxType());
		}
		
		if (StringUtil.isNotBlank(centerPlatformBean.getId())) {
			sb += " and trm.id = ?";
			paras.add(centerPlatformBean.getId());
		}
		
		sb += " order by trm.txReviewPriceBatchNo DESC";
		
		@SuppressWarnings("unchecked")
		List<TxReviewPriceInfo> txRateMaintainInfos = find(sb, paras, page);;

		return txRateMaintainInfos;
	}
	
	/**
	 * 查询审价详情信息
	 * */
	public TxReviewPriceInfo queryTxReviewPriceInfo(TxReviewPriceInfo info,User user) throws Exception{
		String batchNo = info.getTxReviewPriceBatchNo();
		if(info.getApplyState() == "03"){	//	查看已生效详情
//			queryAndUpdatebill();
		}
		TxReviewPriceInfo returninfo = new TxReviewPriceInfo();
		
		CenterPlatformBean qureyInfo = new CenterPlatformBean();
		qureyInfo.setBatchNo(batchNo);
		
		//	根据批次号获取主信息
		List<TxReviewPriceInfo> infos = queryTxReviewPriceInfo(qureyInfo, null);
		if(infos.size() > 0){
			returninfo = infos.get(0);
		}
		
		
//		TxReviewPriceInfo txReviewPriceInfo = new TxReviewPriceInfo();
		TxReviewPriceDetail txReviewPriceDetail = quertTxReviewPriceDetail(batchNo);	//	查询详情信息
//		List<TxReduceInfoBean> txReduceInfoBeans = quertTxReduceInfo(batchNo);			//	查询调减信息	
//		txReviewPriceDetail.setId(info.getId());
		returninfo.setTxReviewPriceDetail(txReviewPriceDetail);
//		txReviewPriceInfo.setTxReduceInfoBeans(txReduceInfoBeans);
		
		/**
		 * 新增前台传递过来的期限,用以判断具体操作的是哪一笔数据
		 * 2022-10-13 16:09:24 wfj
		 */
		logger.info("前台传递的期限为:"+info.getTxTerm());
		if(StringUtil.isNotEmpty(info.getTxTerm())){
			returninfo.setTxTerm(info.getTxTerm());
			returninfo.setAcptBankType(info.getAcptBankType());
		}
		
		if(StringUtil.isNotEmpty(info.getBillNo())){
			returninfo.setBillNo(info.getBillNo());
		}
		
		List<IntroBillInfoBean> introBillInfoBeans = new ArrayList<IntroBillInfoBean>();
		if("01".equals(returninfo.getTxPattern())){
			introBillInfoBeans = queryTxIntroduceInfo(returninfo,user);
		}else{
			introBillInfoBeans = queryTxIntroduceInfo2(returninfo);
		}
		
		if(introBillInfoBeans == null || introBillInfoBeans.size() <= 0){
			returninfo.setApplyState("0");
			updateTxReviewPriceInfo(returninfo);
		}else{
			int count = 0;
			for (IntroBillInfoBean introBillInfoBean : introBillInfoBeans) {
				if("1".equals(introBillInfoBean.getStatus())){
					returninfo.setApplyState("1");
				}
				if("0".equals(introBillInfoBean.getStatus())){
					count++;
				}		
			}
			if(count == introBillInfoBeans.size()){
				returninfo.setApplyState("0");
			}
			updateTxReviewPriceInfo(returninfo);
		}
		
		returninfo.setIntroBillInfoBeans(introBillInfoBeans);
		
		return returninfo;
	}
	
	/**
	 * 根据批次号查询审价详情信息
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public TxReviewPriceDetail quertTxReviewPriceDetail(String batchNo){
		List<String> paras = new ArrayList<String>();
		String sb = " select trm from TxReviewPriceDetail as trm where trm.batchNo = ?";
		paras.add(batchNo);
		
		List<TxReviewPriceDetail> txReviewPriceDetails = find(sb, paras);
		
		if(txReviewPriceDetails.size() > 0){
			return txReviewPriceDetails.get(0);
		}

		return null;
	}
	
	/**
	 * 根据批次号查询审价详情信息
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<TxReduceInfoBean> quertTxReduceInfo(TxReduceInfoBean queryBean){
		List paras = new ArrayList();
		String sb = " select trm from TxReduceInfoBean as trm where 1 = 1 ";
		
		if(StringUtil.isNotEmpty(queryBean.getBillNo())){
			sb += " and trm.billNo = ?";
			paras.add(queryBean.getBillNo());
		}
		
		if(StringUtil.isNotEmpty(queryBean.getBatchNo())){
			sb += " and trm.batchNo = ?";
			paras.add(queryBean.getBatchNo());
		}
		
		if(StringUtil.isNotEmpty(queryBean.getBankType())){
			sb += " and trm.bankType = ?";
			paras.add(queryBean.getBankType());
		}
		
		if(null != queryBean.getApplyTxRate()){
			sb += " and trm.applyTxRate = ?";
			paras.add(queryBean.getApplyTxRate());
		}
		
		if(null != queryBean.getApplyTerm()){
			sb += " and trm.applyTerm = ?";
			paras.add(queryBean.getApplyTerm());
		}
		
		List<TxReduceInfoBean> txReduceInfoBeans = find(sb, paras);

		return txReduceInfoBeans;
	}
	
	/**
	 * 根据批次号查询审价引入信息(票据审价)
	 * @throws Exception 
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<IntroBillInfoBean> queryTxIntroduceInfo(TxReviewPriceInfo info,User user) throws Exception{
		List paras = new ArrayList();
		
		String sb = " select trm from IntroBillInfoBean as trm where 1 = 1";
		
		//	批次号
		if (StringUtil.isNotBlank(info.getTxReviewPriceBatchNo())) {
			sb += " and trm.batchNo = ?";
			paras.add(info.getTxReviewPriceBatchNo());
		}
		
		//	票据号
		if (StringUtil.isNotBlank(info.getBillNo())) {
			sb += " and trm.billNo = ?";
			paras.add(info.getBillNo());
		}
		
		List<IntroBillInfoBean> introBillInfoBeans = find(sb, paras);
		
//		queryGuideRateAndUpdate("", user);		//	更新最新指导利率
		for(int i = 0;i < introBillInfoBeans.size();i++){
			
			TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
			txRateDetailBean.setBankType(introBillInfoBeans.get(i).getAcptBankType());
			
			IntroBillInfoBean billInfo = new IntroBillInfoBean();	//	获取最新的利率信息
			if(!StringUtil.isEmpty(introBillInfoBeans.get(i).getTxTerm())){
				txRateDetailBean.setTerm(Integer.parseInt(introBillInfoBeans.get(i).getTxTerm()));
			}else{
				txRateDetailBean.setTerm(DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),introBillInfoBeans.get(i).getDueDate().replaceAll("-", "")));
			}
			billInfo = queryRateByTermAndType(txRateDetailBean,user);
		
			introBillInfoBeans.get(i).setGuidanceRate(billInfo.getGuidanceRate());
			introBillInfoBeans.get(i).setBestFavorRate(billInfo.getBestFavorRate());
			introBillInfoBeans.get(i).setFavorRate(billInfo.getFavorRate());
			
			txUpdateIntroBillInfo(introBillInfoBeans.get(i));
		}

		return introBillInfoBeans;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<IntroBillInfoBean> queryTxIntroduceInfo(TxReviewPriceInfo info) throws Exception{
		List paras = new ArrayList();
		
		String sb = " select trm from IntroBillInfoBean as trm where 1 = 1";
		
		//	批次号
		if (StringUtil.isNotBlank(info.getTxReviewPriceBatchNo())) {
			sb += " and trm.batchNo = ?";
			paras.add(info.getTxReviewPriceBatchNo());
		}
		
		//	票据号
		if (StringUtil.isNotBlank(info.getBillNo())) {
			sb += " and trm.billNo = ?";
			paras.add(info.getBillNo());
			
			/**
			 * 提交票据状态为00
			 * 提交审批过程中为01
			 * 提交中台成功后为0
			 * */
			if("00".equals(info.getApplyState())){	//	提交审批校验在途
				sb += " and trm.status != '00'";
			}
		}
		
		List<IntroBillInfoBean> introBillInfoBeans = find(sb, paras);

		return introBillInfoBeans;
	}
	
	/**
	 * 返回前台拼接展示(额度审价)
	 * @throws Exception 
	 * */
	@SuppressWarnings("rawtypes")
	private List<IntroBillInfoBean> queryTxIntroduceInfo2(TxReviewPriceInfo info) throws Exception{
		
		List paras = new ArrayList();
		List<IntroBillInfoBean> introBillInfoBeans = new ArrayList<IntroBillInfoBean>();
		
		String sb = " select trm from IntroBillInfoBean as trm where 1 = 1";
		
		//	批次号
		if (StringUtil.isNotBlank(info.getTxReviewPriceBatchNo())) {
			sb += " and trm.batchNo = ?";
			paras.add(info.getTxReviewPriceBatchNo());
		}
		
		//	票据号
		if (StringUtil.isNotBlank(info.getBillNo())) {
			sb += " and trm.billNo = ?";
			paras.add(info.getBillNo());
		}
		
		introBillInfoBeans = find(sb, paras);
		
		for(int i = 0;i < introBillInfoBeans.size();i++){
			
			TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
			txRateDetailBean.setBankType(introBillInfoBeans.get(i).getAcptBankType());
			
			IntroBillInfoBean billInfo = new IntroBillInfoBean();	//	获取最新的利率信息
			if(!StringUtil.isEmpty(introBillInfoBeans.get(i).getTxTerm())){
				txRateDetailBean.setTerm(Integer.parseInt(introBillInfoBeans.get(i).getTxTerm()));
			}else{
				txRateDetailBean.setTerm(DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),introBillInfoBeans.get(i).getDueDate().replaceAll("-", "")));
			}
			billInfo = queryRateByTermAndType(txRateDetailBean,null);
		
			introBillInfoBeans.get(i).setGuidanceRate(billInfo.getGuidanceRate());
			introBillInfoBeans.get(i).setBatchNo(info.getTxReviewPriceBatchNo());
			introBillInfoBeans.get(i).setBestFavorRate(billInfo.getBestFavorRate());
			introBillInfoBeans.get(i).setFavorRate(billInfo.getFavorRate());
			
			txUpdateIntroBillInfo(introBillInfoBeans.get(i));
		}
		
		String sql = "select a.status,a.acpt_bank_type,a.apply_tx_rate,a.apply_amt,to_char(listagg(a.tx_term,',') within group(order by to_number(tx_term))) from t_bill_intro_info a where 1 = 1 ";

		//		批次号
		if (StringUtil.isNotBlank(info.getTxReviewPriceBatchNo())) {
			sql += " and batch_no = '" + info.getTxReviewPriceBatchNo() + "'";
		}
		
		//		承兑行别
		if (StringUtil.isNotBlank(info.getAcptBankType())) {
			sql += " and acpt_bank_type = '" + info.getAcptBankType() + "'";
		}
		
		//	期限
		if (StringUtil.isNotBlank(info.getTxTerm())) {
			sql += " and tx_term in ('" + info.getTxTerm().replaceAll(",", "','") + "') ";
		}
		
		//	利率
		if (null != info.getApplyTxRate()) {
			sql += " and apply_tx_rate = '" + info.getApplyTxRate() + "'";
		}
		
		sql += " group by a.acpt_bank_type, a.apply_tx_rate, a.apply_amt,a.status";
		introBillInfoBeans.clear();
		System.out.println("额度审价查询语句:"+sql);
		List list = this.dao.SQLQuery(sql);
		if(list != null && list.size() > 0){
			for(int i=0;i<list.size();i++){
				IntroBillInfoBean bean = new IntroBillInfoBean();
				Object[] obj = (Object[]) list.get(i);
				bean.setStatus(obj[0].toString());
				bean.setAcptBankType(obj[1].toString());
				bean.setApplyTxRate(new BigDecimal(obj[2].toString()));
				bean.setApplyAmt(new BigDecimal(obj[3].toString()));
				bean.setTxTerm(obj[4].toString());
				introBillInfoBeans.add(bean);
			}
		}
		
		return introBillInfoBeans;
	}
	
	@Override
	public String getEntityName() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntityClass() {
		return null;
	}

	/**
	 * 获取行别映射信息
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> queryBankRoleMapping(
			CenterPlatformBean centerPlatformBean, Page page,User user) throws Exception {
		centerPlatformBean.setPageNum(page.getPageIndex());
		centerPlatformBean.setPageSize(page.getPageSize());
		
		ReturnMessageNew messageNew = centerPlatformSysService.txQueryBankRoleMapping(centerPlatformBean,user);
		List<Map<String, Object>> lists = messageNew.getDetails();
		List<BankRoleMappingBean> bankRoleMappingBeans = new ArrayList<BankRoleMappingBean>();
		
		if(lists.size() > 0){
			for (Map<String, Object> map : lists) {
				BankRoleMappingBean mappingBean = new BankRoleMappingBean();
				mappingBean.setAcptHeadBankName(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_NAME").toString()));
				mappingBean.setAcptHeadBankNo(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.ACCEPTANCE_BANK_NO").toString()));
				mappingBean.setPjsBankType(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.TICKET_EXCHANGE_BANK_TYPE").toString()));
				mappingBean.setDefaultType(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.DEFAULT_BANK_TYPE").toString()));
				mappingBean.setMaintainType(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.MAINTAIN_BANK_TYPE").toString()));
				mappingBean.setActualType(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE").toString()));
				mappingBean.setMaintainTime(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.MAINTAIN_DATE").toString()));
				mappingBean.setWorkerName(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.MAINTAIN_PERSON_NAME").toString()));
				mappingBean.setWorkerNo(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.MAINTAIN_CLIENT_NO").toString()));
				mappingBean.setBranchCode(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.MAINTAIN_BRANCH_NO").toString()));
				mappingBean.setBranchName(StringUtil.getStringVal(map.get("ACCEPTANCE_BANK_INFO_ARRAY.MAINTAIN_BRANCH_NAME").toString()));
				bankRoleMappingBeans.add(mappingBean);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", bankRoleMappingBeans);
		map.put("total",StringUtil.isNotEmpty(StringUtil.getStringVal(messageNew.getAppHead().get("TOTAL_ROWS")))?messageNew.getAppHead().get("TOTAL_ROWS"):0);
		System.out.println(messageNew.getAppHead().get("TOTAL_ROWS"));
		return map;
	}

	/**
	 * 插入利率相关信息
	 * */
	private boolean insertTxRate(TxRateMaintainInfo Info) throws Exception {
		String batchNo = batchNoUtils.txGetBatchNo();
		Info.setBatchNo(batchNo);
		//	1、插入维护信息表
		insertTxRateInfo(Info);
		
		//	2、插入详情表
		List<TxRateDetailBean> txRateDetailBeans = Info.getRateDetailBeans();
		for (TxRateDetailBean txRateDetailBean : txRateDetailBeans) {
			if(txRateDetailBean.getRate() == null || txRateDetailBean.getRate().compareTo(BigDecimal.ZERO) <= 0){
				continue;
			}
			
			txRateDetailBean.setBatchNo(batchNo);
			txRateDetailBean.setStatus("00");
//			txRateDetailBean.setEffTime(Info.getEffTime());
//			txRateDetailBean.setRateType(Info.getRateType());
			insertTxRateDetail(txRateDetailBean);
		}
		
		return true;
	}

	/**
	 * 修改利率相关信息
	 * */
	@Override
	public boolean updateTxRate(TxRateMaintainInfo Info)
			throws Exception {
		//	1、修改维护信息表
		updateTxRateInfo(Info);
		
		//	2、修改详情表
		List<TxRateDetailBean> txRateDetailBeans = Info.getRateDetailBeans();
		for (TxRateDetailBean txRateDetailBean : txRateDetailBeans) {
			txRateDetailBean.setBatchNo(Info.getBatchNo());
			txRateDetailBean.setEffTime(Info.getEffTime());
			txRateDetailBean.setRateType(Info.getRateType());
			updateTxRateDetail(txRateDetailBean);
		}
		
		return true;
	}

	/**
	 * 新增或者维护利率
	 * */
	@Override
	public boolean maintainTxRate(TxRateMaintainInfo txRateMaintainInfo)throws Exception {
		boolean falg = false;
		if(StringUtil.isEmpty(txRateMaintainInfo.getBatchNo())){
			txRateMaintainInfo.setEffState("00");
			falg = insertTxRate(txRateMaintainInfo);
		}else{
			falg = updateTxRate(txRateMaintainInfo);
		}
		return falg;
	}
	
	/**
	 * 插入利率维护基础表
	 * @throws Exception 
	 * */
	public void insertTxRateInfo(TxRateMaintainInfo Info) throws Exception{
		StringBuffer sql = new StringBuffer(" insert into t_rate_maintainInfo(id,Batchno,Ratetype,Bestrate,Effstate,Maintaintime,");
		sql.append("Efftime,Handler,Handlerno,Reviewer,Reviewerno)");
		sql.append(" values('").append(UUID.randomUUID().toString()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getBatchNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getRateType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		if(Info.getBestRate() == null){
			sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		}else{
			sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(Info.getBestRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getEffState())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getMaintainTime())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getEffTime())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getHandler())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getHandlerNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getReviewer())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(Info.getReviewerNo())).append("'");
		sql.append(")");
		try {
			dao.updateSQL(sql.toString());
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}
	
	/**
	 * 插入利率详情表
	 * @throws Exception 
	 * */
	public void insertTxRateDetail(TxRateDetailBean bean) throws Exception{
		StringBuffer sql = new StringBuffer(" insert into t_rateDetails(id,bankType,status,effTime,rateType,batchNo,rate,term)");
		sql.append(" values('").append(UUID.randomUUID().toString()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getBankType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getStatus())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getEffTime())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getRateType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getBatchNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getTerm()).append("'");
		sql.append(")");
		try {
			dao.updateSQL(sql.toString());
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}

	/**
	 * 修改利率维护基础表
	 * */
	public void updateTxRateInfo(TxRateMaintainInfo Info){		
		String currTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("维护时间："+currTime);
		String sql = "update t_rate_maintainInfo set LASTUPDATETIME = to_date('" + currTime + "','YYYY-MM-DD HH24:mi:ss')";
		if(!StringUtils.isEmpty(Info.getBatchNo())){
			if(!StringUtils.isEmpty(Info.getMaintainTime())){
				sql += ",MAINTAINTIME = '" + Info.getMaintainTime() +"'";
			}
			
			if(!StringUtils.isEmpty(Info.getBestRate())){
				sql += ",bestRate = '" + Info.getBestRate() +"'";
			}
			if(!StringUtils.isEmpty(Info.getEffState())){
				sql += ",EFFSTATE = '" + Info.getEffState() +"'";
			}
			if(!StringUtils.isEmpty(Info.getEffTime())){
				sql += ",EFFTIME = '" + Info.getEffTime() +"'";
			}
			if(!StringUtils.isEmpty(Info.getHandler())){
				sql += ",HANDLER = '" + Info.getHandler() +"'";
			}
			if(!StringUtils.isEmpty(Info.getHandlerNo())){
				sql += ",HANDLERNO = '" + Info.getHandlerNo() +"'";
			}
			if(!StringUtils.isEmpty(Info.getReviewer())){
				sql += ",REVIEWER = '" + Info.getReviewer() +"'";
			}
			if(!StringUtils.isEmpty(Info.getReviewerNo())){
				sql += ",REVIEWERNO = '" + Info.getReviewerNo() +"'";
			}
			
			sql += "where BATCHNO = '" + Info.getBatchNo() +"'";
		}else{
			if("03".equals(Info.getRateType())){	//	更新最优惠利率
				if(!StringUtils.isEmpty(Info.getBestRate())){
					sql += ",bestRate = '" + Info.getBestRate() +"'";
				}
				if(!StringUtils.isEmpty(Info.getEffState())){
					sql += ",EFFSTATE = '" + Info.getEffState() +"'";
				}
				if(!StringUtils.isEmpty(Info.getEffTime())){
					sql += ",EFFTIME = '" + Info.getEffTime() +"'";
				}
				if(!StringUtils.isEmpty(Info.getMaintainTime())){
					sql += ",MAINTAINTIME = '" + Info.getMaintainTime() +"'";
				}
				
				sql += "where RATETYPE = '" + Info.getRateType() +"' and EFFTIME = '" + Info.getEffTime() +"'" ;
				if(!StringUtils.isEmpty(Info.getBatchNo())){
					sql += " and BATCHNO = " + Info.getBatchNo() + "'";
				}
			}
		}
		System.out.println(sql);
		try {
			dao.updateSQL(sql.toString());
			dao.flush();
			dao.clear();
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}
	
	/**
	 * 修改利率详情表
	 * @throws Exception 
	 * */
	public void updateTxRateDetail(TxRateDetailBean bean) throws Exception{
		//	如果存在则修改   不存在则插入
		
		if(null != getTxRateDetail(bean)){
			String sql = "update t_rateDetails set rate = '" + bean.getRate() 
					+ "',effTime = '" + bean.getEffTime() 
					+ "',batchNo = '" + bean.getBatchNo() 
					+ "',bankType = '" + bean.getBankType() 
					+ "',term = '" + bean.getTerm() 
					+ "',status = '" + bean.getStatus() 
					+ "',rateType = '" + bean.getRateType();
			
			if(!StringUtil.isStringEmpty(bean.getId())){
				sql += "',id = '" + bean.getId();
			}
			
			sql += "' where bankType = '" + bean.getBankType() + "' and term ='" +  bean.getTerm() + "'";	//	行别+期限
			
			if("04".equals(bean.getStatus())){
				sql += "and status = '" + bean.getStatus() +"'";
			}else{
				sql += "and batchNo = '" + bean.getBatchNo() +"'";
			}
			
			if(!StringUtil.isStringEmpty(bean.getRateType())){
				sql += "and rateType = '" + bean.getRateType() +"'";
			}
			
			
			try {
				dao.updateSQL(sql.toString());
				dao.flush();
				dao.clear();
			}catch(Exception e) {
				logger.info("log sql="+sql.toString());
				logger.error(ErrorCode.ERR_MSG_996,e);
			}
		}else{
			if(bean.getRate() != null && bean.getRate().compareTo(BigDecimal.ZERO) > 0){
				insertTxRateDetail(bean);
			}
		}
	}

	/**
	 * 根据票据号获取引入票据对应信息
	 * */
	@SuppressWarnings("rawtypes")
	@Override
	public Map txBillIntroduceQuery(String billNo,User user, Page page) throws Exception {
		
		Map resultMap = new HashMap();
		
		List<IntroBillInfoBean> lists = new ArrayList<IntroBillInfoBean>();
		System.out.println();
		ECDSPoolTransNotes poolNotes = new ECDSPoolTransNotes();
		poolNotes.setBillNo(billNo);
		poolNotes.setDataSource("3");
		poolNotes.setCurrentPage(page.getPageIndex());
		poolNotes.setPageSize(page.getPageSize());
		/**
		 * 通过票号查询票据子票区间
		 */
		if(billNo.substring(0, 1).equals("1") || billNo.substring(0, 1).equals("2")) {
			//票号首号为1或2 表示是ecds来源的票 直接查询正面接口
			poolNotes.setBillSource(PoolComm.CS01);
			
			ReturnMessageNew billResPose = poolEcdsService.txApplyQueryBillFace(poolNotes);
			if(billResPose.isTxSuccess()){
				List<Map> list = billResPose.getDetails();	//从BBSP系统查回的正面信息
				if(list !=null && list.size() >0 ){
					for (Map map : list) {
						List<Map> bills = (List<Map>) map.get("BILL_INFO_ARRAY");
						System.out.println(bills);
						if(bills != null){
							for (Map billMap : bills) {
								IntroBillInfoBean billInfo = new IntroBillInfoBean();
								
//								billInfo = getRateByBankNoAndDueDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")), 
//										StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.EXPIRY_DATE")),user);
//								
								billInfo.setBillNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_NO")));
								billInfo.setBillAmt(new BigDecimal(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_AMT"))));
								billInfo.setBillType(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_TYPE")));
								billInfo.setChildBillNoBegin("0");
								billInfo.setChildBillNoEnd("0");
								billInfo.setIssueDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAW_BILL_DATE")));
								billInfo.setIssuerBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NAME")));
								billInfo.setIssuerBankNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NO")));
								billInfo.setIssuerName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_NAME")));
								billInfo.setPayeeName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_NAME")));
								billInfo.setPayeeBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NAME")));
								billInfo.setPayeeBankNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NO")));
//								billInfo.setApplyTxDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_NO")));
								billInfo.setDueDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.EXPIRY_DATE")));
//								billInfo.setLimitDays(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_GUARANTEE_DATE")));
								billInfo.setAcptBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));
								billInfo.setAcptBankNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")));
								
								//	根据承兑行号获取利率和行别信息
								CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
								
//								获取上级行号
								Map<String, String> remap = blackListManageService.queryCpesMember(billInfo.getAcptBankNo());
								if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
									centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
								}else{
									centerPlatformBean.setAcptHeadBankNo(billInfo.getAcptBankNo());
								}
								
								centerPlatformBean.setPageSize(10);
								centerPlatformBean.setPageNum(1);
								ReturnMessageNew messageNew = centerPlatformSysService.txQueryBankRoleMapping(centerPlatformBean,user);
								List list2 = messageNew.getDetails();
								
								TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
								if(list2.size() > 0){
									Map map2 = (Map) list2.get(0); 
									if(!StringUtil.isEmpty(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")))){
										txRateDetailBean.setBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")));
									}else{
										txRateDetailBean.setBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.DEFAULT_BANK_TYPE")));
									}
								}
								
								txRateDetailBean.setTerm(DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),billInfo.getDueDate().replaceAll("-", "")));
								
								IntroBillInfoBean rbillInfo = this.queryRateByTermAndType(txRateDetailBean,user);
								
								billInfo.setLimitDays(DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),billInfo.getDueDate().replaceAll("-", "")).toString());
								billInfo.setAcptBankType(txRateDetailBean.getBankType());
								billInfo.setBestFavorRate(rbillInfo.getBestFavorRate());
								billInfo.setFavorRate(rbillInfo.getFavorRate());
								billInfo.setGuidanceRate(rbillInfo.getGuidanceRate());
//								billInfo.setAcptBankType(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_NO")));
								billInfo.setDataSource("02");
								lists.add(billInfo);
							}
						}
					}
				}
			}
			resultMap.put("rows", lists);
			resultMap.put("results", lists.size());
			return resultMap;
			
		}else {
			//等分化票据  先查询子票区间再查询详情信息
			ReturnMessageNew response = poolEcdsService.txApplyQueryBillRange(poolNotes);
			List<Map> billSection = response.getDetails();
			if(billSection != null){
				poolNotes.setBillSource(PoolComm.CS02);
				
				List<Map> bills = new ArrayList<Map>();
				IntroBillInfoBean billInfo = new IntroBillInfoBean();
				
				for (int j = 0; j < billSection.size(); j++) {
					IntroBillInfoBean temp = new IntroBillInfoBean();
					Map billNoMap = billSection.get(j);
					
					String beginNo = (String) billNoMap.get("BILL_INFO_ARRAY.START_BILL_NO");
					String endNo = (String) billNoMap.get("BILL_INFO_ARRAY.END_BILL_NO");
					BigDecimal billAmt = new BigDecimal(StringUtil.isNotEmpty(StringUtil.getStringVal(billNoMap.get("BILL_INFO_ARRAY.BILL_AMT")))?StringUtil.getStringVal(billNoMap.get("BILL_INFO_ARRAY.BILL_AMT")):"0");
					String billSource = (String) billNoMap.get("BILL_INFO_ARRAY.BILL_SOURCE");
					temp.setId(j+"");
					if(j > 0){
						BeanUtil.copyValue(billInfo, temp);
						temp.setChildBillNoBegin(beginNo);
						temp.setChildBillNoEnd(endNo);
						temp.setBillAmt(billAmt);
						lists.add(temp);
					}else{
						poolNotes.setBeginRangeNo(beginNo);
						poolNotes.setEndRangeNo(endNo);
						poolNotes.setCurrentPage(1);
						poolNotes.setPageSize(10);
						
						ReturnMessageNew billResPose = poolEcdsService.txApplyQueryBillFace(poolNotes);
						if(billResPose.isTxSuccess()){

							List<Map> list = billResPose.getDetails();	//从BBSP系统查回的正面信息
							if(list !=null && list.size() >0 ){
								for (Map map : list) {
									bills = (List<Map>) map.get("BILL_INFO_ARRAY");
									if(bills != null){
										for (Map billMap : bills) {
											billInfo.setBillNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_NO")));
											billInfo.setBillAmt(billAmt);
											billInfo.setBillType(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_TYPE")));
											billInfo.setChildBillNoBegin(beginNo);
											billInfo.setChildBillNoEnd(endNo);
											billInfo.setIssueDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAW_BILL_DATE")));
											billInfo.setIssuerBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NAME")));
											billInfo.setIssuerBankNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NO")));
											billInfo.setIssuerName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_NAME")));
											billInfo.setPayeeName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_NAME")));
											billInfo.setPayeeBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NAME")));
											billInfo.setPayeeBankNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NO")));
//											billInfo.setApplyTxDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.BILL_NO")));
											billInfo.setDueDate(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.EXPIRY_DATE")));
//											billInfo.setLimitDays(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.DRAWER_GUARANTEE_DATE")));
											billInfo.setAcptBankName(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME")));
											billInfo.setAcptBankNo(StringUtil.getStringVal(billMap.get("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO")));
											
//											根据承兑行号获取利率和行别信息
											CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
											//	获取上级行号
											Map<String, String> remap = blackListManageService.queryCpesMember(billInfo.getAcptBankNo());
											if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
												centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
											}else{
												centerPlatformBean.setAcptHeadBankNo(billInfo.getAcptBankNo());
											}
											
											centerPlatformBean.setPageSize(10);
											centerPlatformBean.setPageNum(1);
											
											ReturnMessageNew messageNew = centerPlatformSysService.txQueryBankRoleMapping(centerPlatformBean,user);
											List list2 = messageNew.getDetails();
											
											TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
											if(list2.size() > 0){
												Map map2 = (Map) list2.get(0); 
												if(!StringUtil.isEmpty(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")))){
													txRateDetailBean.setBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")));
												}else{
													txRateDetailBean.setBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.DEFAULT_BANK_TYPE")));
												}
											}
											
											txRateDetailBean.setTerm(DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),billInfo.getDueDate().replaceAll("-", "")));
											
											IntroBillInfoBean rbillInfo = this.queryRateByTermAndType(txRateDetailBean,user);
											
											billInfo.setLimitDays(DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),billInfo.getDueDate().replaceAll("-", "")).toString());
											billInfo.setAcptBankType(txRateDetailBean.getBankType());
											billInfo.setBestFavorRate(rbillInfo.getBestFavorRate());
											billInfo.setFavorRate(rbillInfo.getFavorRate());
											billInfo.setGuidanceRate(rbillInfo.getGuidanceRate());
											
											billInfo.setDataSource("02");
											BeanUtil.copyValue(billInfo, temp);
											temp.setChildBillNoBegin(beginNo);
											temp.setChildBillNoEnd(endNo);
											lists.add(temp);
										}
									}
								}
							}
						}
					}
					
				}
			}
			
			resultMap.put("rows", lists);
			resultMap.put("results", response.getAppHead().get("TOTAL_ROWS"));
			return resultMap;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private IntroBillInfoBean getRateByBankNoAndDueDate(String bankNo,String duedate,User user) throws Exception{
		IntroBillInfoBean billInfo = new IntroBillInfoBean();
		//	获取利率信息   并赋值
		TxRateDetailBean txRateDetailBean = new TxRateDetailBean();
		
		CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
		
		//	获取上级行号
		Map<String, String> remap = blackListManageService.queryCpesMember(billInfo.getAcptBankNo());
		if(StringUtil.isNotEmpty(remap.get("totalBankNo"))){
			centerPlatformBean.setAcptHeadBankNo(remap.get("totalBankNo"));
		}else{
			centerPlatformBean.setAcptHeadBankNo(billInfo.getAcptBankNo());
		}
		
		centerPlatformBean.setPageSize(10);
		centerPlatformBean.setPageNum(1);
		
		ReturnMessageNew messageNew = centerPlatformSysService.txQueryBankRoleMapping(centerPlatformBean,user);
		List list2 = messageNew.getDetails();
		if(list2.size() > 0){
			Map map2 = (Map) list2.get(0); 
			txRateDetailBean.setBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")));
			billInfo.setAcptBankType(StringUtil.getStringVal(map2.get("ACCEPTANCE_BANK_INFO_ARRAY.REAL_BANK_TYPE")));
		}
		
		txRateDetailBean.setTerm(DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),duedate.replaceAll("-", "")));
		billInfo.setTxTerm((DateUtils.getMonth1(new SimpleDateFormat("yyyyMMdd").format(new Date()),duedate.replaceAll("-", ""))).toString());
		
		billInfo = queryRateByTermAndType(txRateDetailBean,user);
		return billInfo;
	}
	
	/**
	 * 更新本地指导/优惠利率
	 * */
	private void updateTxAviRate(TxRateMaintainInfo txRateMaintainInfo) throws Exception{
		//	1、修改基础表信息
		updateTxRateInfo(txRateMaintainInfo);
		
		//	2、修改详情表
		List<TxRateDetailBean> txRateDetailBeans = txRateMaintainInfo.getRateDetailBeans();
		if(txRateDetailBeans.size() > 0){
			for (TxRateDetailBean txRateDetailBean : txRateDetailBeans) {
				txRateDetailBean.setStatus(txRateMaintainInfo.getEffState());
				txRateDetailBean.setRateType(txRateMaintainInfo.getRateType());
				updateTxRateDetail(txRateDetailBean);
			}
		}
	}
	
	/**
	 * 查询利率详情展示页面信息
	 * */	
	@SuppressWarnings({ "rawtypes" })
	private List<TxRateDetailBeanPO> queryRateDetails(TxRateMaintainInfo txRateMaintainInfo) throws Exception{
		List<TxRateDetailBeanPO> txRateDetailBeanPOs = new ArrayList<TxRateDetailBeanPO>();
		
		List list = queryRateDetailsSql(txRateMaintainInfo);
		if(list != null && list.size() > 0){
			for(int i=0;i<list.size();i++){
				TxRateDetailBeanPO txRateDetailBeanPO = new TxRateDetailBeanPO();
				Object[] obj = (Object[]) list.get(i);
				txRateDetailBeanPO.setTerm(obj[0].toString());
				if(obj[1] != null){
					txRateDetailBeanPO.setOwnBank(new BigDecimal(obj[1].toString()));
				}
				if(obj[2] != null){
					txRateDetailBeanPO.setStateShares(new BigDecimal(obj[2].toString()));
				}
				if(obj[3] != null){
					txRateDetailBeanPO.setSharesSys(new BigDecimal(obj[3].toString()));
				}
				if(obj[4] != null){
					txRateDetailBeanPO.setCityBank(new BigDecimal(obj[4].toString()));
				}
				if(obj[5] != null){
					txRateDetailBeanPO.setAgriCommBank(new BigDecimal(obj[5].toString()));
				}
				if(obj[6] != null){
					txRateDetailBeanPO.setType6(new BigDecimal(obj[6].toString()));
				}
				if(obj[7] != null){
					txRateDetailBeanPO.setType7(new BigDecimal(obj[7].toString()));
				}
				if(obj[8] != null){
					txRateDetailBeanPO.setType8(new BigDecimal(obj[8].toString()));
				}
				if(obj[9] != null){
					txRateDetailBeanPO.setType9(new BigDecimal(obj[9].toString()));
				}
				if(obj[10] != null){
					txRateDetailBeanPO.setType10(new BigDecimal(obj[10].toString()));
				}
				txRateDetailBeanPOs.add(txRateDetailBeanPO);
			}
		}
		return txRateDetailBeanPOs;
	}
	
	/**
	 * 测算页面利率展示
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<TxRateDetailBeanPO> queryRateDetails1(TxRateMaintainInfo txRateMaintainInfo,User user) throws Exception{
		List<TxRateDetailBeanPO> txRateDetailBeanPOs = new ArrayList<TxRateDetailBeanPO>();
		
		List list = queryRateDetailsSql(txRateMaintainInfo);	//	获取指导利率
		
		for(int i = 0;i < list.size();i++){
			TxRateDetailBeanPO txRateDetailBeanPO = new TxRateDetailBeanPO();
			Object[] obj = (Object[]) list.get(i);
			txRateDetailBeanPO.setTerm(obj[0].toString());
			
			if(null != obj[1]){
				txRateDetailBeanPO.setOwnBank(new BigDecimal(obj[1].toString()));
			}
			
			if(null != obj[2]){
				txRateDetailBeanPO.setStateShares(new BigDecimal(obj[2].toString()));
			}
			
			if(null != obj[3]){
				txRateDetailBeanPO.setSharesSys(new BigDecimal(obj[3].toString()));
			}
			
			if(null != obj[4]){
				txRateDetailBeanPO.setCityBank(new BigDecimal(obj[4].toString()));
			}
			
			if(null != obj[5]){
				txRateDetailBeanPO.setAgriCommBank(new BigDecimal(obj[5].toString()));
			}
			
			if(null != obj[6]){
				txRateDetailBeanPO.setType6(new BigDecimal(obj[6].toString()));
			}

			if(null != obj[7]){
				txRateDetailBeanPO.setType6(new BigDecimal(obj[7].toString()));
			}
			
			if(null != obj[8]){
				txRateDetailBeanPO.setType6(new BigDecimal(obj[8].toString()));
			}
			
			if(null != obj[9]){
				txRateDetailBeanPO.setType6(new BigDecimal(obj[9].toString()));
			}
			
			if(null != obj[10]){
				txRateDetailBeanPO.setType6(new BigDecimal(obj[10].toString()));
			}
			
			txRateDetailBeanPOs.add(txRateDetailBeanPO);
		}
		
		//	根据协议号调用中台获取实际利率
		ReturnMessageNew messageNew = centerPlatformSysService.ActualRateInfos(txRateMaintainInfo.getProtocolNo(), user);
		
		List<Map> lists = messageNew.getDetails();
		if(lists.size() > 0){
			for (Map map : lists) {
				if(map != null){
					String term = StringUtil.getStringVal(map.get("INT_RATE_ARRAY.REMAIN_EXPIRY_MONTHS"));
					String rate = StringUtil.getStringVal(map.get("INT_RATE_ARRAY.INT_RATE"));
					String bankType = StringUtil.getStringVal(map.get("INT_RATE_ARRAY.ACCEPTANCE_BANK_TYPE"));
					
					for(int i = 0;i < txRateDetailBeanPOs.size();i++){
						if(term.equals(txRateDetailBeanPOs.get(i).getTerm())){
							if("00".equals(bankType)){
								txRateDetailBeanPOs.get(i).setOwnBank1(new BigDecimal(rate));
							}
							if("01".equals(bankType)){
								txRateDetailBeanPOs.get(i).setStateShares1(new BigDecimal(rate));
							}
							if("02".equals(bankType)){
								txRateDetailBeanPOs.get(i).setSharesSys1(new BigDecimal(rate));
							}
							if("03".equals(bankType)){
								txRateDetailBeanPOs.get(i).setCityBank1(new BigDecimal(rate));
							}
							if("04".equals(bankType)){
								txRateDetailBeanPOs.get(i).setAgriCommBank1(new BigDecimal(rate));
							}
							if("05".equals(bankType)){
								txRateDetailBeanPOs.get(i).setType61(new BigDecimal(rate));
							}
							if("06".equals(bankType)){
								txRateDetailBeanPOs.get(i).setType71(new BigDecimal(rate));
							}
							if("07".equals(bankType)){
								txRateDetailBeanPOs.get(i).setType81(new BigDecimal(rate));
							}
							if("08".equals(bankType)){
								txRateDetailBeanPOs.get(i).setType91(new BigDecimal(rate));
							}
							if("09".equals(bankType)){
								txRateDetailBeanPOs.get(i).setType101(new BigDecimal(rate));
							}
						}
					}
				}
			}
		}
		
		return txRateDetailBeanPOs;
	}

	
	@SuppressWarnings("rawtypes")
	private List queryRateDetailsSql(TxRateMaintainInfo txRateMaintainInfo) throws Exception {
		String sql = "select a.term," + 
				"sum(decode(a.banktype, '00', a.rate, ''))," + 
				"sum(decode(a.banktype, '01', a.rate, ''))," +
				"sum(decode(a.banktype, '02', a.rate, ''))," +
				"sum(decode(a.banktype, '03', a.rate, ''))," +
				"sum(decode(a.banktype, '04', a.rate, ''))," +
				"sum(decode(a.banktype, '05', a.rate, ''))," +
				"sum(decode(a.banktype, '06', a.rate, ''))," +
				"sum(decode(a.banktype, '07', a.rate, ''))," +
				"sum(decode(a.banktype, '08', a.rate, ''))," +
				"sum(decode(a.banktype, '09', a.rate, '')) from t_rateDetails a,t_rate_maintainInfo b where a.batchno = b.batchno";
		
		if(StringUtil.isNotEmpty(txRateMaintainInfo.getBatchNo())){
			sql += " and b.batchno = '" + txRateMaintainInfo.getBatchNo() + "'";
		}else{
			if(StringUtil.isNotEmpty(txRateMaintainInfo.getRateType())){
				sql += " and b.rateType = '" + txRateMaintainInfo.getRateType() + "' and b.effState = '1' ";
			}
		}
		sql += " group by a.term order by a.term";
		

		List list = this.dao.SQLQuery(sql);
		return list;
	}
	
	/**
	 * 根据期限和承兑行别查询对应利率
	 * @throws Exception 
	 * */
	public IntroBillInfoBean queryRateByTermAndType(TxRateDetailBean txRateDetailBean,User user) throws Exception{
		IntroBillInfoBean bean = new IntroBillInfoBean();
		
		Map<String, BigDecimal> map = queryGuidRate(txRateDetailBean);
		
		bean.setBestFavorRate(queryBestRate().getBestRate());
		
		bean.setGuidanceRate(map.get("GuidanceRate"));
		bean.setFavorRate(map.get("FavorRate"));
		
		return bean;
	}
	
	/**
	 * 查询本地生效最优惠利率
	 * */ 	
	@SuppressWarnings("rawtypes")
	private TxRateMaintainInfo queryBestRate(){
		String sb = "select trm from TxRateMaintainInfo as trm where 1=1 and trm.rateType = '03' and trm.effState = '1'";
		List lists = find(sb);
		TxRateMaintainInfo info = new TxRateMaintainInfo();
		if(lists != null && lists.size() > 0){
			return (TxRateMaintainInfo) lists.get(0);
		}
		
		return info;
	}
	
	/**
	 * 获取指导利率和优惠利率
	 * */
	@SuppressWarnings({ "rawtypes" })
	private Map<String, BigDecimal> queryGuidRate(TxRateDetailBean txRateDetailBean) throws Exception{
//		queryAndUpdateRates(user);
//		queryAndUpdateBest(new CenterPlatformBean(), user);
		
		String sql = "select a.rate from t_rateDetails a,t_rate_maintainInfo b where a.batchno = b.batchno "
				+ "and b.rateType = '01' and a.term = '" + txRateDetailBean.getTerm() + "' and a.bankType = '" 
				+ txRateDetailBean.getBankType() + "' and b.effState = '1'";
		
		List lists = this.dao.SQLQuery(sql);
		
		Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		if(lists != null && lists.size() > 0){
			BigDecimal obj =  (BigDecimal) lists.get(0);
			map.put("GuidanceRate", obj);
		}
		
		sql = "select a.rate from t_rateDetails a,t_rate_maintainInfo b where a.batchno = b.batchno "
				+ "and b.rateType = '02' and a.term = '" + txRateDetailBean.getTerm() + "' and a.bankType = '" 
				+ txRateDetailBean.getBankType() + "' and b.effState = '1'";
		
		lists = this.dao.SQLQuery(sql);
		
		if(lists != null && lists.size() > 0){
			BigDecimal obj = (BigDecimal) lists.get(0);
			map.put("FavorRate", obj);
		}
		
		return map;
	}
	
	
	/**
	 * 校验是否存在已生效或在途数据以及是否超出时间
	 * */
	public String checkBillInfo(String id) throws Exception{
		TxReviewPriceInfo info = (TxReviewPriceInfo) this.load(id,TxReviewPriceInfo.class);
		
//		String currTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
//		
//		if(Integer.parseInt(info.getDueDate().replaceAll("-", "")) < Integer.parseInt(currTime.replaceAll("-", ""))){
//			return "已超过申请日期，请重新申请";
//		}
		
		String result = "";
		if("01".equals(info.getTxPattern())){
			List<IntroBillInfoBean> list = queryTxIntroduceInfo(info);
			for (IntroBillInfoBean introBillInfoBean : list) {
				if(!checkBillNo(introBillInfoBean.getBillNo())){
					result += "," + introBillInfoBean.getBillNo();
				}
			}
			
			if(StringUtil.isNotEmpty(result)){
				result += "票据已有生效或者在途的票据审价信息";
			}
		}else{
			//	额度审价校验协议对应行别和期限是否存在
			TxReviewPriceDetail txReviewPriceDetail = quertTxReviewPriceDetail(info.getTxReviewPriceBatchNo());	//	查询详情信息
			List<IntroBillInfoBean> list = queryTxIntroduceInfo(info, null);
			
			Map<String, String> map = new HashMap<String, String>();	//	存放需要返回的信息
			map.put("00", "");
			map.put("01", "");
			map.put("02", "");
			map.put("03", "");
			map.put("04", "");
			
			for (int i = 0;i < list.size();i++) {
				map.put(list.get(i).getAcptBankType(), "");
				String value = "";
				if(!checkTxProtocol(txReviewPriceDetail.getOnlineNo(),list.get(i).getAcptBankType(),list.get(i).getTxTerm())){
					value += "," + (map.get(list.get(i).getAcptBankType()) + list.get(i).getTxTerm());
					map.put(list.get(i).getAcptBankType(), value);
				}
			}
			
			System.out.println(map);
			
			for (String key : map.keySet()) {
				if(StringUtil.isNotEmpty(map.get(key))){
					String rebackType = "";
					if("00".equals(key)){
						rebackType = "汉口银行";
					}else if ("01".equals(key)) {
						rebackType = "国有银行";
					}else if ("02".equals(key)) {
						rebackType = "股份制银行";
					}else if ("03".equals(key)) {
						rebackType = "城市商业银行";
					}else if ("04".equals(key)) {
						rebackType = "农商银行";
					}
					result += result + "," + rebackType + map.get(key) + "月已有在途或已生效的额度审价信息;";
				}
			}
		}
		
		return result.replaceFirst(",", "");
	}
	
	private boolean checkBillNo(String billNo) throws Exception{
		List paras = new ArrayList();
		String sql = "select i from IntroBillInfoBean as i, TxReviewPriceInfo as t where i.batchNo = t.txReviewPriceBatchNo"
				+ " and t.applyState in ('SP_01','SP_02','SP_04','1','2') and i.status != '0' and i.billNo = ?";
		
		paras.add(StringUtil.getStringVal(billNo));
		
		List txRateMaintainInfos = find(sql, paras);
		
		if(txRateMaintainInfos.size() > 0){
			return false;
		}
		return true;
	}
	
	/**
	 * 校验是否存在协议对应在途行别和期限数据
	 * */
	private boolean checkTxProtocol(String protocolNo,String bankType,String term) throws Exception{
		String sql = "select c.* from t_bill_review_price_detail a, T_BILL_REVIEW_PRICE_INFO b,t_bill_intro_info c where a.batch_no = b.batch_no and b.batch_no = c.batch_no"
				+ " and b.APPLY_STATE in ('SP_01','SP_02','SP_04','1','2') and c.status = '1'"
				+ " and a.online_no = '" + protocolNo + "'"
				+ " and c.tx_term = '" + term + "'"
				+ " and c.acpt_bank_type = '" + bankType + "'";
		
		List list = this.dao.SQLQuery(sql);
		
		if(list.size() > 0){
			return false;
		}
		return true;
	}

	@Override
	public void txSubmitPriceQuery(String id, User user) throws Exception {
		
		TxReviewPriceInfo info = (TxReviewPriceInfo) this.load(id,TxReviewPriceInfo.class);
		
//		PedProtocolDto dto = pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, trans.getBpsNo(), trans.getCustomer(), null, trans.getDrAcctNo()) ;

		
		ApproveAuditBean approveAudit = new ApproveAuditBean();
		approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
		approveAudit.setProductId(PublicStaticDefineTab.APPROVAL_ROUTE_01);//产品类型为票据审价-分支行审批
		approveAudit.setCustCertNo(info.getCustNo()); //客户证件号码
		approveAudit.setBusiId(info.getId()); 
		approveAudit.setAuditAmt(info.getApproveAmt()); // 总金额
		
		approveAudit.setBusiType(info.getTxType());//业务类型为：票据审价-直贴：01 、票据审价-直转通： 02、票据审价-再贴现：03 、额度审价-模式一：04 、额度审价-模式二：05
		approveAudit.setApplyNo(info.getTxReviewPriceBatchNo());
		
		Map<String, BigDecimal> mvelDataMap = new HashMap<String, BigDecimal>();
		mvelDataMap.put("amount", info.getApproveAmt());
	
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
		
		info.setApplyState(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);// 提交审批
		info.setWorkerName(user.getName());
		info.setWorkerNo(user.getLoginName());
		info.setWorkerBranch(user.getDepartment().getName());
		
		/**
		 * 提交票据状态为00
		 * 提交审批过程中为01
		 * 提交中台成功后为0
		 * */
		String sql = "update t_bill_intro_info set status = '01' where batch_no = '" + info.getTxReviewPriceBatchNo()+"'";
		System.out.println("更新票据状态信息:"+sql);
		dao.updateSQL(sql.toString()); 
		
		dao.store(info);
	}
	
	public void txCancelPriceQuery(String id , User user) throws Exception{
		TxReviewPriceInfo info = (TxReviewPriceInfo) this.load(id,TxReviewPriceInfo.class);

		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(info.getApplyState())){
			throw new Exception("当前授信额度状态不允许撤销审批。");
		}
		auditService.txCommitCancelAudit(PublicStaticDefineTab.APPROVAL_ROUTE_01, id);
		
		String sql = "update t_bill_intro_info set status = '00' where batch_no = '" + info.getTxReviewPriceBatchNo()+"'";
		System.out.println("更新票据状态信息:"+sql);
		dao.updateSQL(sql.toString()); 
		
		//状态回滚
		info.setApplyState("00");//回滚为新增
		this.txStore(info);
		
	}
	
	/**
	 * 校验提交日期是否超出生效日期
	 * */
	public String checkAndUpdate(String id){
		TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(id,TxRateMaintainInfo.class);
		
		String currTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		if(Integer.parseInt(info.getEffTime().replaceAll("-", "")) < Integer.parseInt(currTime.replaceAll("-", ""))){
			//	修改状态
			info.setEffState("SP_00");	// 	提交失败
			
			updateTxRateInfo(info);
			
			return "已超过申请日期，请重新申请";
		}
		return "";
	}
	
	
	public void txSubmittxRate(String id , User user) throws Exception{
		// TODO Auto-generated method stub
		TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(id,TxRateMaintainInfo.class);
		
		ApproveAuditBean approveAudit = new ApproveAuditBean();
		approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
		approveAudit.setProductId("5001006");//利率审批
//		approveAudit.setCustCertNo(info.getCustNo()); //客户证件号码
		approveAudit.setBusiId(info.getId()); 
//		approveAudit.setAuditAmt(info.getApproveAmt()); // 总金额
		approveAudit.setBusiType("5001006");//业务类型为利率审批
		approveAudit.setApplyNo(info.getBatchNo());
		
		Map<String, BigDecimal> mvelDataMap = new HashMap<String, BigDecimal>();
//		mvelDataMap.put("amount", info.getApproveAmt());
	
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
		
		info.setEffState(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);// 提交审批
		info.setHandler(user.getName());
		info.setHandlerNo(user.getLoginName());
		info.setMaintainTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		dao.store(info);
	}
	
	public void txCanceltxRate(String id , User user) throws Exception{
		TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(id,TxRateMaintainInfo.class);

		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(info.getEffState())){
			throw new Exception("当前授信额度状态不允许撤销审批。");
		}
		auditService.txCommitCancelAudit("5001006", id);
		
		//状态回滚
		info.setEffState("00");//回滚为新增
		this.txStore(info);
	}
	
	
	public void txSubmitBestFavor(String id , User user) throws Exception{
		// TODO Auto-generated method stub
		TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(id,TxRateMaintainInfo.class);
		
		ApproveAuditBean approveAudit = new ApproveAuditBean();
		approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
		approveAudit.setProductId("5001007");//最优惠利率审批
//				approveAudit.setCustCertNo(info.getCustNo()); //客户证件号码
		approveAudit.setBusiId(info.getId()); 
//				approveAudit.setAuditAmt(info.getApproveAmt()); // 总金额
		approveAudit.setBusiType("5001007");//业务类型为最优惠利率审批
		approveAudit.setApplyNo(info.getBatchNo());
		
		Map<String, BigDecimal> mvelDataMap = new HashMap<String, BigDecimal>();
//				mvelDataMap.put("amount", info.getApproveAmt());
	
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
		
		info.setEffState(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);// 提交审批
		info.setHandler(user.getName());
		info.setHandlerNo(user.getLoginName());
		dao.store(info);
	}
	
	public void txCancelBestFavor(String id , User user) throws Exception{
		TxRateMaintainInfo info = (TxRateMaintainInfo) this.load(id,TxRateMaintainInfo.class);

		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(info.getEffState())){
			throw new Exception("当前授信额度状态不允许撤销审批。");
		}
		auditService.txCommitCancelAudit("5001007", id);
		
		//状态回滚
		info.setEffState("00");//回滚为新增
		this.txStore(info);
	}

	
	/***
	 * 指导、优惠利率发送
	 * */
	@Override
	public boolean txRateSend(TxRateMaintainInfo txRateMaintainInfo,User user) throws Exception{
		CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
		
		centerPlatformBean.setOpreaType(txRateMaintainInfo.getOpreType());
		centerPlatformBean.setBatchNo(txRateMaintainInfo.getBatchNo());
		centerPlatformBean.setEffTime(txRateMaintainInfo.getEffTime());
		
//		if("03".equals(txRateMaintainInfo.getRateType())){
//			centerPlatformSysService.txBestFavorRateMaintain(txRateMaintainInfo,user);
//		}else{
			//	1、根据批次号查询本地待发送的利率详情
			List<TxRateDetailBean> lists = getTxRateDetail(txRateMaintainInfo);
			//	2、调用新增和维护接口
			centerPlatformBean.setTxRateDetailBeans(lists);
			if("01".equals(txRateMaintainInfo.getRateType())){	//	指导利率发送
				centerPlatformSysService.txGuideRateMaintain(centerPlatformBean,user);
			}else if("02".equals(txRateMaintainInfo.getRateType())){	//	优惠利率发送
				centerPlatformSysService.txFavorRateMaintain(centerPlatformBean,user);
			}
//		}
		
		//	查询中台已提交数据  并更新本地
		if("01".equals(txRateMaintainInfo.getRateType())){
			queryGuideRateAndUpdate(txRateMaintainInfo.getBatchNo(), user);
		}else if("02".equals(txRateMaintainInfo.getRateType())){
			queryFavorRateAndUpdate(txRateMaintainInfo.getBatchNo(),user);
		}/*else{
			queryAndUpdateBest(centerPlatformBean,user);
		}*/
		
		return true;
	}
	
	/**
	 * 票据审价保存
	 * @throws Exception 
	 * */
	public boolean txSaveBillOprea(TxReviewPriceInfo info) throws Exception{
		try {
			if(StringUtil.isStringEmpty(info.getTxReviewPriceBatchNo())){
				
				String batchNo = batchNoUtils.txGetBatchNo();
				info.setTxReviewPriceBatchNo(batchNo);
				//	新增信息
				insertTxReviewPriceInfo(info);
				info.getTxReviewPriceDetail().setBatchNo(batchNo);
				insertTxReviewPriceDetail(info.getTxReviewPriceDetail());
				
				List<IntroBillInfoBean> listts = info.getIntroBillInfoBeans();
				for (IntroBillInfoBean introBillInfoBean : listts) {
					if("04".equals(info.getTxType()) || "05".equals(info.getTxType())){	//	额度审价利率赋值
						
						IntroBillInfoBean billInfo = new IntroBillInfoBean();	//	获取最新的利率信息
						TxRateDetailBean txRateDetailBean = new TxRateDetailBean(); 
						txRateDetailBean.setTerm(Integer.parseInt(introBillInfoBean.getTxTerm()));
						
						billInfo = queryRateByTermAndType(txRateDetailBean,null);
					
						introBillInfoBean.setGuidanceRate(billInfo.getGuidanceRate());
						introBillInfoBean.setBestFavorRate(billInfo.getBestFavorRate());
					}
					introBillInfoBean.setBatchNo(batchNo);
					insertIntroBillInfo(introBillInfoBean);
				}
			}else{
				//	修改信息
				updateTxReviewPriceInfo(info);
				info.getTxReviewPriceDetail().setBatchNo(info.getTxReviewPriceBatchNo());
				updateTxReviewPriceDetail(info.getTxReviewPriceDetail());
				
				List<IntroBillInfoBean> listts = info.getIntroBillInfoBeans();
				IntroBillInfoBean bean = new IntroBillInfoBean();
				bean.setBatchNo(info.getTxReviewPriceBatchNo());
				deleteIntroBillInfo(bean);
				for (IntroBillInfoBean introBillInfoBean : listts) {
					introBillInfoBean.setBatchNo(info.getTxReviewPriceBatchNo());
					
					if("04".equals(info.getTxType()) || "05".equals(info.getTxType())){	//	额度审价利率赋值
						
						IntroBillInfoBean billInfo = new IntroBillInfoBean();	//	获取最新的利率信息
						TxRateDetailBean txRateDetailBean = new TxRateDetailBean(); 
						txRateDetailBean.setTerm(Integer.parseInt(introBillInfoBean.getTxTerm()));
						
						billInfo = queryRateByTermAndType(txRateDetailBean,null);
					
						introBillInfoBean.setGuidanceRate(billInfo.getGuidanceRate());
						introBillInfoBean.setBestFavorRate(billInfo.getBestFavorRate());
					}
					insertIntroBillInfo(introBillInfoBean);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		return true;
	}
	
	/**
	 * 插入审价基本信息表
	 * @throws Exception 
	 * */
	private void insertTxReviewPriceInfo(TxReviewPriceInfo info) throws Exception{
		StringBuffer sql = new StringBuffer(" insert into T_BILL_REVIEW_PRICE_INFO(id,batch_no,tx_type,tx_pattern,cust_no,cust_name,");
		sql.append("online_no,apply_state,apply_date,due_date,worker_name,worker_no,worker_branch)");
		sql.append(" values('").append(UUID.randomUUID().toString()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getTxReviewPriceBatchNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getTxType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getTxPattern())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getCustNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getCustName())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getOnlineNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyState())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getDueDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getWorkerName())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getWorkerNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getWorkerBranch())).append("'");
		sql.append(")");
		try {
			dao.updateSQL(sql.toString());
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}
	
	/**
	 * 插入 审价详情表信息
	 * */
	private void insertTxReviewPriceDetail(TxReviewPriceDetail info) throws Exception{
		StringBuffer sql = new StringBuffer(" insert into t_bill_review_price_detail(id,batch_no,apply_branch,apply_date,cust_Manager,emp_no,");
		sql.append("telPhone,landline,cust_name,cust_no,mis_cust_no,online_no,account_type,is_micro,is_pribusi,is_prifarm,is_green,is_tech,apply_amount_sum,audit_type,apply_tx_date,");
		sql.append("apply_valid_date,ave_daily_deposit,inner_busi_income,other_busi_income,apply_reason,effect,apply_tx_rate,best_favor_rate,eff_date,other_remark)");
		sql.append(" values('").append(UUID.randomUUID().toString()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getBatchNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyBranch())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getCustManager())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getEmpNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getTelPhone())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getLandLine())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getCustName())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getCustNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getMisCustNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getOnlineNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getAccountType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIsMicro())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIsPriBusi())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIsPriFarm())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIsGreen())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIsTech())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getApplyAmountSum()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getAuditType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyTxDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyValidDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getAveDailyDeposit())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getInnerBusiIncome())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getOtherBusiIncome())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyReason())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getEffect())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		if(info.getApplyTxRate() == null){
			info.setApplyTxRate(BigDecimal.ZERO);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getApplyTxRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		if(info.getBestFavorRate() == null){
			info.setBestFavorRate(BigDecimal.ZERO);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getBestFavorRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getEffDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getOtherRemark())).append("'");
		sql.append(")");
		try {
			dao.updateSQL(sql.toString());
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}

	/**
	 * 插入引入信息表
	 * */
	private void insertIntroBillInfo(IntroBillInfoBean info) throws Exception{
		StringBuffer sql = new StringBuffer(" insert into t_bill_intro_info(id,batch_no,bill_no,bill_amt,bill_type,apply_tx_date,");
		sql.append("due_date,issue_date,limit_days,acpt_bank_name,acpt_bank_no,acpt_bank_type,apply_tx_rate,guidance_rate,favor_rate,best_favor_rate,tx_term,apply_amt,"
				+ "issuer_name,issuer_bank_name,issuer_bank_no,payee_bank_name,payee_bank_no,status,data_source)");
		sql.append(" values('").append(UUID.randomUUID().toString()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getBatchNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getBillNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		if(info.getBillAmt() == null){
			info.setBillAmt(BigDecimal.ZERO);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getBillAmt()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getBillType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getApplyTxDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getDueDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIssueDate())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getLimitDays())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getAcptBankName())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getAcptBankNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getAcptBankType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		if(info.getApplyTxRate() == null){
			info.setApplyTxRate(BigDecimal.ZERO);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getApplyTxRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		if(info.getGuidanceRate() == null){
			info.setGuidanceRate(BigDecimal.ZERO);
		}
		
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getGuidanceRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		if(info.getFavorRate() == null){
			info.setFavorRate(BigDecimal.ZERO);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getFavorRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		if(info.getBestFavorRate() == null){
			info.setBestFavorRate(BigDecimal.ZERO);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getBestFavorRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		if(StringUtil.isBlank(info.getTxTerm()) || info.getTxTerm().equals("null")){
			info.setTxTerm("");
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getTxTerm()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		if(info.getApplyAmt() == null){
			info.setApplyAmt(BigDecimal.ZERO);
		}
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(info.getApplyAmt()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIssuerName())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIssuerBankName())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getIssuerBankNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getPayeeBankName())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getPayeeBankNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append("00").append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(info.getDataSource())).append("'");
		sql.append(")");
		try {
			dao.updateSQL(sql.toString());
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}

	/**
	 * 更新审价基本信息表
	 * */
	public void updateTxReviewPriceInfo(TxReviewPriceInfo info) throws Exception{
		String  usql = "update T_BILL_REVIEW_PRICE_INFO set tx_type = '" + StringUtil.getStringVal(info.getTxType()) + "'," 
					+ "tx_pattern = '" + StringUtil.getStringVal(info.getTxPattern()) + "',"
					+ "cust_no = '" + StringUtil.getStringVal(info.getCustNo()) + "',"
					+ "cust_name = '" + StringUtil.getStringVal(info.getCustName()) + "',"
					+ "online_no = '" + StringUtil.getStringVal(info.getOnlineNo()) + "',"
					+ "apply_state = '" + StringUtil.getStringVal(info.getApplyState()) + "',"
					+ "apply_date = '" + StringUtil.getStringVal(info.getApplyDate()) + "',"
					+ "worker_name = '" + StringUtil.getStringVal(info.getWorkerName()) + "',"
					+ "worker_no = '" + StringUtil.getStringVal(info.getWorkerNo()) + "',"
					+ "worker_branch = '" + StringUtil.getStringVal(info.getWorkerBranch()) + "' "
					+ "where batch_no = '" + StringUtil.getStringVal(info.getTxReviewPriceBatchNo()) +"'";
		try {
			System.out.println(usql);
			dao.updateSQL(usql.toString());
			dao.flush();
			dao.clear();
		}catch(Exception e) {
			logger.info("log sql = " + usql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}
	
	/**
	 * 修改审价详情表
	 * @throws Exception 
	 * */
	private void updateTxReviewPriceDetail(TxReviewPriceDetail info) throws Exception {
		String  usql = "update t_bill_review_price_detail set apply_branch = '" + StringUtil.getStringVal(info.getApplyBranch()) + "'," 
				+ "apply_date = '" + StringUtil.getStringVal(info.getApplyDate()) + "',"
				+ "cust_Manager = '" + StringUtil.getStringVal(info.getCustManager()) + "',"
				+ "emp_no = '" + StringUtil.getStringVal(info.getEmpNo()) + "',"
				+ "telPhone = '" + StringUtil.getStringVal(info.getTelPhone()) + "',"
				+ "landline = '" + StringUtil.getStringVal(info.getLandLine()) + "',"
				+ "cust_name = '" + StringUtil.getStringVal(info.getCustName()) + "',"
				+ "cust_no = '" + StringUtil.getStringVal(info.getCustNo()) + "',"
				+ "mis_cust_no = '" + StringUtil.getStringVal(info.getMisCustNo()) + "',"
				+ "online_no = '" + StringUtil.getStringVal(info.getOnlineNo()) + "',"
				+ "account_type = '" + StringUtil.getStringVal(info.getAccountType()) + "',"
				+ "is_micro = '" + StringUtil.getStringVal(info.getIsMicro()) + "',";
				
				if(info.getApplyAmountSum() == null){
					info.setApplyAmountSum(BigDecimal.ZERO);
				}
				
				usql += "apply_amount_sum = '" + info.getApplyAmountSum() + "',"
				+ "audit_type = '" + StringUtil.getStringVal(info.getAuditType()) + "',"
				+ "apply_tx_date = '" + StringUtil.getStringVal(info.getApplyTxDate()) + "',"
				+ "apply_valid_date = '" + StringUtil.getStringVal(info.getApplyValidDate()) + "',"
				+ "ave_daily_deposit = '" + StringUtil.getStringVal(info.getAveDailyDeposit()) + "',"
				+ "inner_busi_income = '" + StringUtil.getStringVal(info.getInnerBusiIncome()) + "',"
				+ "other_busi_income = '" + StringUtil.getStringVal(info.getOtherBusiIncome()) + "',"
				+ "apply_reason = '" + StringUtil.getStringVal(info.getApplyReason()) + "',"
				+ "eff_date = '" + StringUtil.getStringVal(info.getEffDate()) + "',"
				+ "effect = '" + StringUtil.getStringVal(info.getEffect()) + "',";
				
				if(info.getApplyTxRate() == null){
					info.setApplyTxRate(BigDecimal.ZERO);
				}
				
				usql += "apply_tx_rate = '" + info.getApplyTxRate() + "',";
				
				if(info.getBestFavorRate() == null){
					info.setBestFavorRate(BigDecimal.ZERO);
				}
				usql += "best_favor_rate = '" + info.getBestFavorRate() + "',"
				+ "other_remark = '" + StringUtil.getStringVal(info.getOtherRemark()) + "' "
				+ "where batch_no = '" + StringUtil.getStringVal(info.getBatchNo()) +"'";
	try {
		dao.updateSQL(usql.toString());
	}catch(Exception e) {
		logger.info("log sql = " + usql.toString());
		logger.error(ErrorCode.ERR_MSG_996,e);
	}
	}
	
	/**
	 * 修改引入表状态
	 * 
	 * */
	public void txUpdateIntroStatus(IntroBillInfoBean info)throws Exception{
		String  usql = "";
		String sqlWhere = "";
		try {
			usql += "update t_bill_intro_info set batch_no = '" + StringUtil.getStringVal(info.getBatchNo()) + "'," ;
			
			usql += "status = '" + StringUtil.getStringVal(info.getStatus()) + "' ";
			
			if(StringUtil.isNotEmpty(info.getTxTerm()) || info.getTxTerm().equals("null")){
				sqlWhere = " and tx_term = '" + info.getTxTerm() + "'";
			}
			
			if(StringUtil.isNotEmpty(info.getAcptBankType())){
				sqlWhere += " and acpt_bank_type = '" + info.getAcptBankType() + "'";
			}
			
			if(StringUtil.isNotEmpty(info.getBillNo())){
				sqlWhere += " and bill_no = '" + info.getBillNo() + "'";
			}
			
			usql += "where batch_no = '" + StringUtil.getStringVal(info.getBatchNo()) +"' " + sqlWhere;
			
			System.out.println("更新审价详情状态语句:"+usql);
			dao.updateSQL(usql.toString());
		}catch(Exception e) {
			logger.info("log sql = " + usql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}
	
	/**
	 * 修改引入信息表
	 * */
	private void txUpdateIntroBillInfo(IntroBillInfoBean info) throws Exception{
		String  usql = "";
		String sqlWhere = "";
		try {
			usql += "update t_bill_intro_info set bill_no = '" + StringUtil.getStringVal(info.getBillNo()) + "'," ;
			if(info.getBillAmt() == null){
				info.setBillAmt(BigDecimal.ZERO);
			}
			usql += "bill_amt = '" + info.getBillAmt() + "',"
					+ "bill_type = '" + StringUtil.getStringVal(info.getBillType()) + "',"
					+ "apply_tx_date = '" + StringUtil.getStringVal(info.getApplyTxDate()) + "',"
					+ "due_date = '" + StringUtil.getStringVal(info.getDueDate()) + "',"
					+ "status = '" + StringUtil.getStringVal(info.getStatus()) + "',"
					+ "issue_date = '" + StringUtil.getStringVal(info.getIssueDate()) + "',"
					+ "limit_days = '" + StringUtil.getStringVal(info.getLimitDays()) + "',"
					+ "acpt_bank_name = '" + StringUtil.getStringVal(info.getAcptBankName()) + "',"
					+ "issuer_name = '" + StringUtil.getStringVal(info.getIssuerName()) + "',"
					+ "issuer_bank_name = '" + StringUtil.getStringVal(info.getIssuerBankName()) + "',"
					+ "issuer_bank_no = '" + StringUtil.getStringVal(info.getIssuerBankNo()) + "',"
					+ "payee_bank_name = '" + StringUtil.getStringVal(info.getPayeeBankName()) + "',"
					+ "payee_bank_no = '" + StringUtil.getStringVal(info.getPayeeBankNo()) + "',"
					+ "acpt_bank_type = '" + StringUtil.getStringVal(info.getAcptBankType()) + "',";
			if(info.getApplyTxRate() == null){
				info.setGuidanceRate(BigDecimal.ZERO);
			}
			usql += "apply_tx_rate = '" + info.getApplyTxRate() + "',";
			
			
			if(info.getGuidanceRate() == null){
				info.setGuidanceRate(BigDecimal.ZERO);
			}
			usql += "guidance_rate = '" + info.getGuidanceRate() + "',";
			
			if(info.getFavorRate() == null){
				info.setFavorRate(BigDecimal.ZERO);
			}
			usql += "favor_rate = '" + info.getFavorRate() + "',";
			if(info.getBestFavorRate() == null){
				info.setBestFavorRate(BigDecimal.ZERO);
			}
			
			usql += "best_favor_rate = '" + info.getBestFavorRate() + "',";
			
			if(StringUtil.isBlank(info.getTxTerm()) || info.getTxTerm().equals("null")){
				info.setTxTerm("");
			}else{
				sqlWhere = " and tx_term = '" + info.getTxTerm() + "'";
			}
			
			if(StringUtil.isNotEmpty(info.getAcptBankType())){
				sqlWhere += " and acpt_bank_type = '" + info.getAcptBankType() + "'";
			}
			
			if(StringUtil.isNotEmpty(info.getBillNo())){
				sqlWhere += " and bill_no = '" + info.getBillNo() + "'";
			}
			
			usql += "tx_term = '" + info.getTxTerm() + "',";
			
			if(info.getApplyAmt() == null){
				info.setApplyAmt(BigDecimal.ZERO);
			}else{
				sqlWhere += " and apply_amt = '" + info.getApplyAmt() + "'";
			}
			usql += "apply_amt = '" + info.getApplyAmt() + "',"
					+ "data_source = '" + StringUtil.getStringVal(info.getDataSource()) + "' "
					+ "where batch_no = '" + StringUtil.getStringVal(info.getBatchNo()) +"' " + sqlWhere;
			
			System.out.println("更新利率信息语句:"+usql);
			dao.updateSQL(usql.toString());
		}catch(Exception e) {
			logger.info("log sql = " + usql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}

	/**
	 * 删除审价详情表
	 * *//*
	private void deleteTxReviewPriceDetail(TxReviewPriceDetail info)throws Exception{
		String sql = "delete from t_bill_review_price_detail where batch_no = '" + info.getBatchNo() + "'";
		dao.updateSQL(sql.toString());
	}
	
	*//**
	 * 删除审价基本信息表
	 * *//*
	private void deleteTxReviewPriceInfo(TxReviewPriceInfo info)throws Exception{
		String sql = "delete from T_BILL_REVIEW_PRICE_INFO where batch_no = '" + info.getTxReviewPriceBatchNo() + "'";
		dao.updateSQL(sql.toString());
	}
	
	*//**
	 * 删除引入信息表
	 * */
	private void deleteIntroBillInfo(IntroBillInfoBean info)throws Exception{
		String sql = "delete from t_bill_intro_info where batch_no = '" + info.getBatchNo() + "'";
		if(StringUtil.isNotEmpty(info.getId())){
			sql += " and id = '" + info.getId() + "'";
		}
		System.out.println(sql);
		dao.updateSQL(sql.toString());
	}
	
	
	@Override
	public void deleteBillPrice(TxReviewPriceInfo info) throws Exception {
		//	根据批次号删除对应信息
		String sql = "delete from t_bill_review_price_info where batch_no = '" + info.getTxReviewPriceBatchNo() + "'";
		dao.updateSQL(sql.toString());
		
		sql = "delete from t_bill_review_price_detail where batch_no = '" + info.getTxReviewPriceBatchNo() + "'";
		dao.updateSQL(sql.toString());
		
		sql = "delete from t_bill_intro_info where batch_no = '" + info.getTxReviewPriceBatchNo() + "'";
		dao.updateSQL(sql.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String sendBillPrice(TxReviewPriceInfo info,User user) throws Exception {
		BigDecimal reduceAmt = BigDecimal.ZERO;
		String applyState =  info.getApplyState();		//	0为失效操作
		//	先查询原来调减过的数据
		TxReduceInfoBean queryBean  = new TxReduceInfoBean();
		if(info.getReduceAmt() != null){		//	调减金额为空是首次发送    非空时是调减操作
			reduceAmt = info.getReduceAmt();
		}
		
//		if(!"0".equals(info.getApplyState())){
//			applyState = "1";
//		}
		
//		String id = info.getId();
		if(reduceAmt != null && !"0".equals(applyState)){
			queryBean.setApplyTerm(info.getTxTerm());
			queryBean.setBillNo(info.getBillNo());
			queryBean.setApplyTxRate(info.getApplyTxRate());
			queryBean.setBankType(info.getAcptBankType());
			queryBean.setBatchNo(info.getTxReviewPriceBatchNo());
			queryBean.setApproveAmt(info.getApproveAmt()==null?info.getBillAmt():info.getApproveAmt());
			queryBean.setCurrentAmt(info.getCurrentAmt());
			queryBean.setUsedAmt(info.getUsedAmt());
			queryBean.setAviableAmt(info.getAvailableAmt());
		}
		
		//	根据批次号获取对应信息
		TxReviewPriceInfo returninfo = queryTxReviewPriceInfo(info,user);
		if(reduceAmt != null && !"0".equals(applyState) && reduceAmt.compareTo(BigDecimal.ZERO) > 0){
			returninfo.setReduceAmt(reduceAmt);
		}
		
		if("0".equals(applyState)){
			returninfo.setApplyState(applyState);
		}
		
		if(StringUtil.isNotEmpty(info.getTjId())){
			returninfo.setTjId(info.getTjId());
			returninfo.setApproveAmt(info.getApproveAmt());
			returninfo.setCurrentAmt(info.getCurrentAmt());
			returninfo.setAvailableAmt(info.getAvailableAmt());
			returninfo.setUsedAmt(info.getUsedAmt());
		}
		
		if("01".equals(returninfo.getTxType()) || "02".equals(returninfo.getTxType()) || "03".equals(returninfo.getTxType())){	//	调用票据审价
			ReturnMessageNew messageNew = centerPlatformSysService.txBillPriceMaintain(returninfo,user);
			if(messageNew.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				if(returninfo.getReduceAmt() != null && !"0".equals(applyState) && reduceAmt.compareTo(BigDecimal.ZERO) > 0){
					//	额度审价结束后   初始化调减信息表
					TxReduceInfoBean bean  = new TxReduceInfoBean();
					bean.setBatchNo(returninfo.getTxReviewPriceBatchNo());
					bean.setBillNo(queryBean.getBillNo());
					bean.setBankType(queryBean.getBankType());
					bean.setApplyTxRate(queryBean.getApplyTxRate());
					bean.setApplyTerm(queryBean.getApplyTerm());
					bean.setTxType(returninfo.getTxType());
					bean.setApproveAmt(queryBean.getApproveAmt()!=null?queryBean.getApproveAmt():BigDecimal.ZERO);
					bean.setCurrentAmt(queryBean.getCurrentAmt()!=null?queryBean.getCurrentAmt().subtract(reduceAmt):BigDecimal.ZERO);
					bean.setUsedAmt(queryBean.getUsedAmt()!=null?queryBean.getUsedAmt():BigDecimal.ZERO);
					bean.setAviableAmt(queryBean.getAviableAmt()!=null?queryBean.getAviableAmt():BigDecimal.ZERO);
					bean.setReduceAmt(returninfo.getReduceAmt()!=null?returninfo.getReduceAmt():BigDecimal.ZERO);
					bean.setWorkerNo(user.getLoginName());
					bean.setWorkerName(user.getName());
					
					List<TxReduceInfoBean> txReduceInfoBeans = quertTxReduceInfo(queryBean);			//	查询调减信息	
					BigDecimal total = BigDecimal.ZERO;
					for (TxReduceInfoBean txReduceInfoBean : txReduceInfoBeans) {
						total = total.add(txReduceInfoBean.getReduceAmt());
					}
					
					bean.setTotalReduceAmt(returninfo.getReduceAmt()!=null?returninfo.getReduceAmt().add(total):total);					//	累计调减额度
					System.out.println("开始插入调减信息表");
					insertTxReduceInfo(bean);
				}else{
					//	回调查询接口更新状态
					CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
					centerPlatformBean.setTxType("04".equals(returninfo.getTxType())?"01":"02");
					centerPlatformBean.setBatchNo(returninfo.getTxReviewPriceBatchNo());
					messageNew = centerPlatformSysService.billPriceQuery(centerPlatformBean,user);
					
					List<Map> lists = messageNew.getDetails();
					if(lists.size() > 0){
						for (Map map : lists) {
							returninfo.setApplyState(StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.DISCOUNT_STATUS")));
						}
					}
					
//					updateTxReviewPriceInfo(returninfo);
					
					/**
					 * 提交票据状态为00
					 * 提交审批过程中为01
					 * 提交中台成功后为1
					 * */
					String sql = "update t_bill_intro_info set status = '" + returninfo.getApplyState() + "' where batch_no = '" + returninfo.getTxReviewPriceBatchNo()+"'";
					System.out.println("更新票据状态信息:"+sql);
					dao.updateSQL(sql.toString()); 
					dao.flush();
					dao.clear();
					queryTxReviewPriceInfo(returninfo,user);
				}
				
			}else{
				return messageNew.getRet().getRET_MSG();
			}
		}else{	//	额度审价
			ReturnMessageNew messageNew = new ReturnMessageNew();
			if("04".equals(returninfo.getTxType())){
				messageNew = centerPlatformSysService.txAmtReciewPriceMaintain1(returninfo,user);
			}else{
				messageNew = centerPlatformSysService.txAmtReciewPriceMaintain2(returninfo,user);
			}
			
			if(messageNew.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				if(returninfo.getReduceAmt() != null && returninfo.getReduceAmt().compareTo(BigDecimal.ZERO) > 0){
					//	额度审价结束后   初始化调减信息表
					TxReduceInfoBean bean  = new TxReduceInfoBean();
					bean.setBatchNo(returninfo.getTxReviewPriceBatchNo());
					bean.setBillNo(queryBean.getBillNo());
					bean.setBankType(queryBean.getBankType());
					bean.setApplyTxRate(queryBean.getApplyTxRate());
					bean.setApplyTerm(queryBean.getApplyTerm());
					bean.setTxType(returninfo.getTxType());
					bean.setApproveAmt(queryBean.getApproveAmt()!=null?queryBean.getApproveAmt():BigDecimal.ZERO);
					System.out.println("当前额度："+queryBean.getCurrentAmt() +"=========调减额度"+reduceAmt);
					bean.setCurrentAmt(queryBean.getCurrentAmt()!=null?queryBean.getCurrentAmt().subtract(reduceAmt):BigDecimal.ZERO);
					bean.setUsedAmt(queryBean.getUsedAmt()!=null?queryBean.getUsedAmt():BigDecimal.ZERO);
					bean.setAviableAmt(queryBean.getAviableAmt()!=null?queryBean.getAviableAmt():BigDecimal.ZERO);
					bean.setReduceAmt(returninfo.getReduceAmt()!=null?returninfo.getReduceAmt():BigDecimal.ZERO);
					bean.setWorkerNo(user.getLoginName());
					bean.setWorkerName(user.getName());
					
					List<TxReduceInfoBean> txReduceInfoBeans = quertTxReduceInfo(queryBean);			//	查询调减信息	
					BigDecimal total = BigDecimal.ZERO;
					for (TxReduceInfoBean txReduceInfoBean : txReduceInfoBeans) {
						total = total.add(txReduceInfoBean.getReduceAmt());
					}
					
					bean.setTotalReduceAmt(returninfo.getReduceAmt()!=null?returninfo.getReduceAmt().add(total):total);					//	累计调减额度
					System.out.println("开始插入调减信息表");
					insertTxReduceInfo(bean);
				}else{
//					回调查询接口更新状态
					CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
					centerPlatformBean.setTxType("04".equals(returninfo.getTxType())?"01":"02");
					centerPlatformBean.setBatchNo(returninfo.getTxReviewPriceBatchNo());
					messageNew = centerPlatformSysService.AmtBillPriceQuery(centerPlatformBean,user);
					
					//	主信息状态根据详情判断
					List<Map> lists = messageNew.getDetails();
					if(lists.size() > 0){
						for (Map mapss : lists) {
							if(mapss != null){
								List<Map> listss = (List<Map>) mapss.get("APPROVE_INFO_ARRAY");
								for (Map map : listss) {
									if(map != null){
										returninfo.setApplyState(StringUtil.getStringVal(map.get("APPROVE_INFO_ARRAY.LIMIT_STATUS")));
									}
								}
							}
						}
					}
//					updateTxReviewPriceInfo(returninfo);
					/**
					 * 提交票据状态为00
					 * 提交审批过程中为01
					 * 提交中台成功后为1
					 * */
					String sql = "update t_bill_intro_info set status = '" + returninfo.getApplyState() + "' where batch_no = '" + returninfo.getTxReviewPriceBatchNo()+"'";
					System.out.println("更新票据状态信息:"+sql);
					dao.updateSQL(sql.toString()); 
					dao.flush();
					dao.clear();
					
					queryTxReviewPriceInfo(returninfo,user);
				}
				
			}else{
				return messageNew.getRet().getRET_MSG();
			}
		}
		return "";
	}
	
	//	插入调减信息记录表
	private boolean insertTxReduceInfo(TxReduceInfoBean bean) throws Exception{
		String currTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		StringBuffer sql = new StringBuffer(" insert into t_review_price_reduce_info(id,batch_no,bill_no,tx_type,bank_type,apply_tx_rate,apply_term,approve_Amt,current_amt,used_amt,aviable_amt,total_reduce_amt,worker_no,worker_name,LASTUPDATETIME,reduce_amt)");
		sql.append(" values('").append(UUID.randomUUID().toString()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getBatchNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getBillNo())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getTxType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getBankType())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getApplyTxRate()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(StringUtil.getStringVal(bean.getApplyTerm())).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getApproveAmt()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getCurrentAmt()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getUsedAmt()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getAviableAmt()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getTotalReduceAmt()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getWorkerNo()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getWorkerName()).append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(ConstantFields.VAR_JOIN_COMMA);
		sql.append("to_date('").append(currTime).append("','YYYY-MM-DD HH24:mi:ss')").append(ConstantFields.VAR_JOIN_COMMA);
		sql.append(ConstantFields.VAR_JOIN_SINGLE_QUOTES).append(bean.getReduceAmt()).append("'");
		sql.append(")");
		try {
			System.out.println("插入调减信息表数据："+sql);
			dao.updateSQL(sql.toString());
		}catch(Exception e) {
			logger.info("log sql="+sql.toString());
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
		return true;
	}
	
	//	根据客户号查询反显信息
	@Override
	public TxReviewPriceDetail queryReviewInfoByCustName(String custNo) throws Exception{
		
		return null;
	}
}
