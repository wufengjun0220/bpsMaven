package com.mingtech.application.pool.edu.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.draft.domain.PedBailFlowQuery;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedBailFlow;
import com.mingtech.application.pool.edu.domain.PedBailHis;
import com.mingtech.application.pool.edu.domain.PedBailTrans;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import common.Logger;

@Service("poolBailEduService")
@Order(2)
public class PoolBailEduServiceImpl extends GenericServiceImpl  implements PoolBailEduService {

	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private RoleService roleService;

	private static final Logger logger = Logger.getLogger(PoolBailEduServiceImpl.class);

	public String getEntityName() {
		return "bail";
	}

	@Override
	public Class getEntityClass() {
		return BailDetail.class;
	}


	@Override
	public List queryFlowByAcc(String account,User user,Page page) throws Exception {
		List res = new ArrayList();
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select pbf,ppt from PedBailFlow pbf,PedProtocolDto ppt where 1=1");
		hql.append(" and pbf.accNo=ppt.marginAccount");
		
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and pbf.brcTran in (:brcTran) ");
				paramName.add("brcTran");
				paramValue.add(resultList);
			}
		}
		
		if (account != null && !account.equals("")) {
			hql.append(" and pbf.accNo =:marginAccount");
			paramName.add("marginAccount");
			paramValue.add(account);
		}
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues,page);
		List retList = new ArrayList();
		PedBailFlow info = null;
		PedProtocolDto batch = null;
		for (int i = 0; i < res.size(); i++) {
			Object[] obj = (Object[]) res.get(i);
			if (obj[0] != null && obj[1] != null) {
				info = (PedBailFlow) obj[0];
				batch = (PedProtocolDto) obj[1];
				PedBailFlowQuery beanNew = new PedBailFlowQuery();
				BeanUtils.copyProperties(beanNew, info);
				beanNew.setPoolAgreement(batch.getPoolAgreement());// 票据编号
				beanNew.setPoolName(batch.getPoolName());// 票据池名称
				retList.add(beanNew);
			}
		}
		return retList;
	}

	@Override
	public List queryFlowByAHistorycc(QueryBean bean,User user,Page page) throws Exception {
		List res = new ArrayList();
		List paramName = new ArrayList();
		List paramValue = new ArrayList();
		StringBuffer hql = new StringBuffer();
		hql.append("from PedBailHis pbh,PedProtocolDto dto where pbh.accNo = dto.marginAccount");
		
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and dto.officeNet in (:officeNet) ");
				paramName.add("officeNet");
				paramValue.add(resultList);
			}
		}
		
		if (bean.getMarginAccount() != null && !bean.getMarginAccount().equals("")) {
			hql.append(" and pbh.accNo =:accNo");
			paramName.add("accNo");
			paramValue.add(bean.getMarginAccount());
		}
		if (bean.getStartplDueDt() != null && !bean.getStartplDueDt().equals("")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String sDate = sdf.format(bean.getStartplDueDt());
			hql.append(" and pbh.dateTran>=:stransTime");
			paramName.add("stransTime");
			paramValue.add(sDate);
		}
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String sDate = sdf.format(bean.getEndplDueDt());
			hql.append(" and pbh.dateTran<=:etransTime");
			paramName.add("etransTime");
			paramValue.add(sDate);
		}
		if(bean.getEndplDueDt() == null || "".equals(bean.getEndplDueDt())){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String dateEnd = sdf.format(new Date());
			hql.append(" and pbh.dateTran < :etransTime");
			paramName.add("etransTime");
			paramValue.add(dateEnd);
		}
		if (bean.getPoolAgreement() != null && !bean.getPoolAgreement().equals("")) {
			hql.append(" and dto.poolAgreement =:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		hql.append(" order by pbh.timeMch asc");

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		res = this.find(hql.toString(), paramNames, paramValues,page);
		List retList = new ArrayList();
		for (int i = 0; i < res.size(); i++) {
			Object[] obj = (Object[]) res.get(i);
			PedBailHis info = (PedBailHis) obj[0];
			PedProtocolDto batch = (PedProtocolDto) obj[1];
			PedBailFlowQuery beanNew = new PedBailFlowQuery();
			BeanUtils.copyProperties(beanNew, info);
			beanNew.setBailFlowId(info.getBailHisId());
			beanNew.setPoolAgreement(batch.getPoolAgreement());// 票据编号
			beanNew.setPoolName(batch.getPoolName());// 票据池名称
			retList.add(beanNew);
		}
		return retList;
	}
	@Override
	public void txDeleteBailFlow(String accNo) throws Exception {
		logger.debug("保证金当日流水删除....");
		String hql = "select flow from PedBailFlow as flow "; // 删除保证金当日表数据
		if (accNo != null) {
			hql = hql + "  where flow.accNo ='" + accNo + "'";
		}
		List list = this.find(hql);
		if (list != null && list.size() > 0) {
			this.txDeleteAll(list);
		}
	}

	@Override
	public void txBailDetail(CoreTransNotes trans) throws Exception {
		ReturnMessageNew response = poolCoreService.PJH584141Handler(trans);
		List Deatils = response.getDetails();
		if(Deatils!=null && Deatils.size()>0){
			this.txDeleteBailFlow(trans.getAccNo());
			for (int j = 0; j < Deatils.size(); j++) {
				logger.debug("保证金当日交易流水查询获得核心保证金交易流水条数为：【"+Deatils.size()+"】条");
				Map map = (Map) Deatils.get(j);
				PedBailFlow bail = new PedBailFlow();
				bail.setAccNo((String) map.get("AccNo"));
				bail.setCcy((String) map.get("Ccy"));
				bail.setVouTyp((String) map.get("VouTyp"));
				bail.setVouNo((String) map.get("VouNo"));
				bail.setDateTran((String) map.get("DateTran"));
				bail.setTimeMch((String) map.get("TimeMch"));
				bail.setMemoNo((String) map.get("MemoNo"));
				bail.setMemo((String) map.get("Memo"));
				bail.setAmtTran((String) map.get("AmtTran"));
				bail.setBal((String) map.get("Bal"));
				bail.setSerSeqNo((String) map.get("SerSeqNo"));
				bail.setCSeqNo((String) map.get("CSeqNo"));
				bail.setFlgCd((String) map.get("FlgCD"));
				bail.setFlagCt((String) map.get("FlagCT"));
				bail.setAccNoA((String) map.get("AccNoA"));
				bail.setAcctSeqNo((String) map.get("AcctSeqNo"));
				bail.setSubSeq((String) map.get("SubSeq"));
				bail.setTelTran((String) map.get("TelTran"));
				bail.setBrcTran((String) map.get("BrcTran"));
				bail.setBrcName((String) map.get("BrcName"));
				bail.setNum1((String) map.get("Num1"));
				bail.setCustNam((String) map.get("CustNam"));
				bail.setField((String) map.get("Field"));
				bail.setFlgCanl((String) map.get("FlgCanl"));
				bail.setSeqNoR((String) map.get("SeqNoR"));
				bail.setAccNoA2((String) map.get("AccNoA2"));
				bail.setAccNamA2((String) map.get("AccNamA2"));
				bail.setBnkNamA2((String) map.get("BnkNamA2"));
				pedProtocolService.txStore(bail);
			}
		}else{
			logger.info("无保证金当日交易记录，核心错误码：【"+response.getRet().getRET_CODE()+"】核心错误信息：【"+response.getRet().getRET_MSG()+"】");
		}

	}

	@Override
	public void txBailQueryFromCore(PedProtocolDto pro) throws Exception {
		List pedList = new ArrayList();
		if (pro != null) {
			logger.debug("保证金当日流水查询，网银系统客户【"+pro.getCustnumber()+"】查询开始....");
			pedList.add(pro);
		} else {
			logger.debug("保证金当日流水查询，票据池系统查询开始....");
			pedList =  pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, PoolComm.VS_01, null,null, null, null);
		}
		PedProtocolDto dto = null;
		if (pedList != null && pedList.size() > 0) {
			for (int i = 0; i < pedList.size(); i++) {
				dto = (PedProtocolDto) pedList.get(i);
				CoreTransNotes trans = new CoreTransNotes();
				trans.setTranDate(DateUtils.toString(DateUtils.getCurrDate(), "yyyy-MM-dd"));// 交易日期
				trans.setDateEnd(DateUtils.toString(DateUtils.getCurrDate(), "yyyy-MM-dd"));// 终止日期
				trans.setDateStr(DateUtils.toString(DateUtils.getCurrDate(), "yyyy-MM-dd"));// 开始日期
				trans.setAccNo(dto.getMarginAccount());// 保证金账号
				trans.setAmt("0.00");// 金额
				trans.setIsSucced("");// 查询类型
				logger.info("保证金当日流水查询，查询核心接口操作开始....");
				try {
					this.txBailDetail(trans);
				} catch (Exception e) {
					logger.error("保证金当日流水查询核心接口返回异常",e);
					continue;
				}
			}
			logger.info("保证金当日流水查询，查询核心接口操作结束....");
		}

	}
	
	@Override
	public BailDetail queryBailDetail(String atId) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		String hql = "from  BailDetail dp where dp.at=:at   ";
		paramName.add("at");
		paramValue.add(atId);


		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<BailDetail> result = this.find(hql, paramNames, paramValues);
		if (result != null && result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	@Override
	public BigDecimal queryCoreBailFree(String accNo) throws Exception {
		CoreTransNotes transNotes = new CoreTransNotes();
		
		//核心保证金账户可用额度
		BigDecimal freeAmt = BigDecimal.ZERO;
		transNotes.setAccNo(accNo);
		transNotes.setCurrentFlag("1");
		ReturnMessageNew response = poolCoreService.PJH716040Handler(transNotes, "0");
		if (response.isTxSuccess()) {
			Map map = response.getBody();
			// 获取核心的余额，可用余额，借方控制金额（冻结）
			// 分别对应总额度，（不处理），冻结额度
			BigDecimal totalAmt = new BigDecimal("0.0");
			BigDecimal frzedAmt = new BigDecimal("0.0");
			if (map.get("BALANCE") != null) { // 核心账户余额
				totalAmt = BigDecimalUtils.valueOf((String) map.get("BALANCE"));
			}
			if (map.get("DR_CONTROL_BALANCE") != null) { // 核心账户余额
				frzedAmt = BigDecimalUtils.valueOf((String) map.get("DR_CONTROL_BALANCE"));
			}
			// 核心存在超额冻结（冻结金额大于余额），此时核心可用余额为0
			if (frzedAmt.compareTo(totalAmt) > 0) {
				if (map.get("AVAL_BALANCE") != null) {
					totalAmt = BigDecimalUtils.valueOf((String) map.get("AVAL_BALANCE"));
				} else {
					totalAmt = new BigDecimal("0.0");
				}
			}
			
			freeAmt = totalAmt.subtract(frzedAmt);
		}else{//查询失败
			return null;
		}
		
		return freeAmt;
	}

	@Override
	public BailDetail txUpdateBailDetail(String bpsNo) throws Exception {

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		String hql = " select bd from BailDetail bd,AssetType at,AssetPool ap where "
				   + " bd.at=at.id and at.apId=ap.apId and ap.bpsNo=:bpsNo and bd.assetStatus='BDS_01'";
		
		paramName.add("bpsNo");
		paramValue.add(bpsNo);

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List temp = this.find(hql, paramNames, paramValues);
		BailDetail bail = null;
		if (null != temp && temp.size() > 0) {
			for (int i = 0; i < temp.size(); i++) {
				bail = (BailDetail) temp.get(i);
				/*
				 * 去核心同步保证金账户信息
				 */
				this.txUpdateBailByCoreforQuery(bail);
			}
		}
		return bail;
	}
	
	/**
	 * 核心保证金账户信息同步
	 * @param bail
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-28下午4:14:40
	 */
	public BailDetail txUpdateBailByCoreforQuery(BailDetail bail) throws Exception {
		
		logger.info("------同步完保证金【"+bail.getAssetNb()+"】资产开始----------");
		
		try {
			CoreTransNotes transNotes = new CoreTransNotes();
			transNotes.setAccNo(bail.getAssetNb());
			transNotes.setCurrentFlag("1");
			ReturnMessageNew response = poolCoreService.PJH716040Handler(transNotes, "0");
			
			if (response.isTxSuccess()) {
				Map map = response.getBody();
				// 获取核心的余额，可用余额，借方控制金额（冻结） 分别对应总额度，（不处理），冻结额度
				BigDecimal totalAmt = new BigDecimal("0.0");
				BigDecimal frzedAmt = new BigDecimal("0.0");
				BigDecimal freedAmt = new BigDecimal("0.0");
				
				if (map.get("BALANCE") != null) { // 账户余额
					totalAmt = BigDecimalUtils.valueOf((String) map.get("BALANCE"));
				}
				logger.info("账户余额："+totalAmt);
				if (map.get("DR_CONTROL_BALANCE") != null) { // 借方控制金额
					frzedAmt = BigDecimalUtils.valueOf((String) map.get("DR_CONTROL_BALANCE"));
				}
				logger.info("借方控制金额："+frzedAmt);
				// 核心存在超额冻结（冻结金额大于余额），此时核心可用余额为0
				if (frzedAmt.compareTo(totalAmt) > 0) {
					if (map.get("AVAL_BALANCE") != null) {
						freedAmt = BigDecimalUtils.valueOf((String) map.get("AVAL_BALANCE"));//可用余额
					} else {
						freedAmt = new BigDecimal("0.0");
					}
				}
				logger.info("可用余额："+freedAmt);
				
				bail.setAssetLimitTotal(totalAmt);//核心账户余额
				bail.setAssetLimitFrzd(frzedAmt);
				bail.setAssetLimitFree(freedAmt);//可用额度
				bail.initLimit();// 
				this.txStore(bail);

				logger.info("------已同步完保证金资产----------");
			} else {
				throw new Exception("同步保证金失败！返回码" + response.getRet().getRET_CODE() + "，返回信息" + response.getRet().getRET_MSG());
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("-------updateBailByCore-------", e);
		}
		return bail;
	}

	@Override
	public BailDetail queryBailDetailByBpsNo(String bpsNo) throws Exception {
		String sql = "select  bail from BailDetail as bail where bail.poolAgreement = '"+bpsNo+"' ";
		List result = this.find(sql);
		if(result!=null&&result.size()>0){
			BailDetail bailDetail = (BailDetail) result.get(0);
			return bailDetail;
		}else{
			return null;
		}
	}

	@Override
	public List queryBailTrans(QueryBean bean,Page page,User user) throws Exception {
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String sql = "select  bail from PedBailTrans as bail where 1=1 ";
		if(StringUtil.isNotEmpty(bean.getId())){
			sql = sql + " and bail.id = :id ";
			paramName.add("id");
			paramValue.add(bean.getId());
		}
		
		if(StringUtil.isNotEmpty(bean.getPoolAgreement())){
			sql = sql + " and bail.bpsNo = :bpsNo ";
			paramName.add("bpsNo");
			paramValue.add(bean.getPoolAgreement());
		}
		
		if(StringUtil.isNotEmpty(bean.getCustNumber())){
			sql = sql + " and bail.customer = :customer ";
			paramName.add("customer");
			paramValue.add(bean.getCustNumber());
		}
		
		//创建时间
		if(bean.getStartDate() != null){
			sql = sql + " and bail.createTime <=TO_DATE(:createTime, 'yyyy-mm-dd hh24:mi:ss') ";
			paramName.add("createTime");
			paramValue.add(DateUtils.toDateTimeString(bean.getStartDate()));
			logger.info("时间为:"+DateUtils.toDateTimeString(bean.getStartDate())+"............................");
		}

		//作废标识
		if(StringUtil.isNotEmpty(bean.getFlag())){
			//作废数据时的查询,已支付成功的数据不作废
			sql = sql + " and bail.planStatus not in ('3','1') ";
		}
		if(StringUtils.isNotEmpty(bean.getSStatusFlag())){
			sql = sql + " and bail.planStatus = :planStatus ";
			paramName.add("planStatus");
			paramValue.add(bean.getSStatusFlag());
		}
		if(user != null){
			boolean flag = false;
			List roles = roleService.getRoleByBean(user);
			
			
			for (Object object : roles) {
				String code = (String) object;
				if(code.equals("A00001")){//超级管理员
					flag = true;
				}
			}
			if(!flag){
				sql = sql + " and bail.userName = :userName ";
				paramName.add("userName");
				paramValue.add(user.getName());
			}
		}
		sql = sql + "order by createTime desc";
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List result = this.find(sql, paramNames, paramValues,page);
		if(result!=null&&result.size()>0){
			return result;
		}else{
			return null;
		}
	}

	@Override
	public void txSubmitPedBailTrans(String id, User user)
			throws Exception {
		PedBailTrans trans = (PedBailTrans) this.load(id,PedBailTrans.class);
		
//		PedProtocolDto dto = pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, trans.getBpsNo(), trans.getCustomer(), null, trans.getDrAcctNo()) ;

		
		ApproveAuditBean approveAudit = new ApproveAuditBean();
		approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
		approveAudit.setProductId("10010");//保证金支取
		approveAudit.setCustCertNo(trans.getCustomer()); //客户证件号码
		approveAudit.setBusiId(trans.getId()); 
		approveAudit.setAuditAmt(trans.getTranAmt()); // 总金额
		approveAudit.setBusiType("10010");//业务类型为保证金支取
		approveAudit.setApplyNo(trans.getCretNo());
		
		Map<String, String> mvelDataMap = new HashMap<String, String>();
		mvelDataMap.put("amount", trans.getTranAmt().toString());
	
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
		
		trans.setStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);// 提交审批
		dao.store(trans);
	}

	@Override
	public void txCancelAudit(String id, User user) throws Exception {
		PedBailTrans trans = (PedBailTrans) this.load(id,PedBailTrans.class);

		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(trans.getStatus())){
//			throw new Exception("当前授信额度状态为"+trans.getStatusDesc()+"，不允许撤销审批。");
		}
		auditService.txCommitCancelAudit("10010", trans.getId());
		
		trans.setStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);
		trans.setUpdateTime(DateUtils.getWorkDateTime());
		dao.store(trans);
		
	}

	@Override
	public void queryPedBailTransDetail(String id, User user) throws Exception {
		PedBailTrans trans = (PedBailTrans) this.load(id,PedBailTrans.class);
		
		//核心保证金支取记录查询
		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setSerSeqNo(trans.getSerSeqNo());//流水号
		/*
		 * 查证记账操作
		 */
		String status ="";
		ReturnMessageNew response  = poolCoreService.CORE002Handler(transNotes);
		if(response.isTxSuccess()){//查证返回成功
			trans.setPlanStatus(PoolComm.BAIL_TRANS_1);
		}else{
			trans.setPlanStatus(PoolComm.BAIL_TRANS_2);//划转失败
		}
		dao.store(trans);
		
	}

	@Override
	public Ret sendPedBailTranAgain(String id, User user) throws Exception {
		PedBailTrans trans = (PedBailTrans) this.load(id,PedBailTrans.class);
		
		CoreTransNotes transNotes = new CoreTransNotes();
		Ret ret = new Ret();
		transNotes.setTranAmt(com.mingtech.application.ecds.common.BigDecimalUtils.getStringValue(trans.getTranAmt()));//交易金额   待定
		transNotes.setDrAcctNo(trans.getDrAcctNo());//借方账号
		transNotes.setDrAcctNoName(trans.getDrAcctName());//借方账号名称
		transNotes.setCrAcctNo(trans.getCrAcctNo());//贷款账号
		transNotes.setCrAcctNoName(trans.getCrAcctName());//贷款账号名称
		transNotes.setFrntDate(DateUtils.toString(new Date(), "yyyyMMdd"));//第三方日期
		String str = poolBatchNoUtils.txGetFlowNo();
		
		transNotes.setSerSeqNo(str);//第三方流水号
		transNotes.setRemark(trans.getUsage()+" "+trans.getRemark());//备注
		ReturnMessageNew response = poolCoreService.doMarginWithdrawal(transNotes);
		
		if (response.isTxSuccess()) {
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("保证金划转成功！");
			trans.setPlanStatus(PoolComm.BAIL_TRANS_1);//划转成功
		} else {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("核心保证金划转失败！返回码" + response.getRet().getRET_CODE() + "，返回信息" + response.getRet().getRET_MSG());
			trans.setPlanStatus("2");//划转失败
		}
		trans.setSerSeqNo(str);
		dao.store(trans);
		return ret;
	}

	@Override
	public void txCancelPedBailTran(QueryBean queryBean) throws Exception {
		List list = this.queryBailTrans(queryBean,null,null);
		if(list != null && list.size() > 0){
			List<PedBailTrans> transList = new ArrayList<PedBailTrans>();
			for (int i = 0; i < list.size(); i++) {
				PedBailTrans trans = (PedBailTrans) list.get(i);
				trans.setPlanStatus(PoolComm.BAIL_TRANS_3);//超过一天的数据值为作废
				if(trans.getStatus().equals(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT) || trans.getStatus().equals(PublicStaticDefineTab.AUDIT_STATUS_RUNNING)){
					//若有审批流则关闭
					auditService.txCommitCancelAudit("10010", trans.getId());
					trans.setUpdateTime(DateUtils.getWorkDateTime());
					trans.setStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);//未处理数据
				}
				transList.add(trans);
			}
			this.txStoreAll(transList);
		}
		
	}
	
	@Override
	public List queryBailAccountChange(QueryBean bean,Page page) throws Exception {
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		String sql = "select  bail from MarginAcctChangeHist as bail where 1=1 ";
		if(StringUtil.isNotEmpty(bean.getId())){
			sql = sql + " and bail.id = :id ";
			paramName.add("id");
			paramValue.add(bean.getId());
		}
		
		if(StringUtil.isNotEmpty(bean.getPoolAgreement())){
			sql = sql + " and bail.bpsNo = :bpsNo ";
			paramName.add("bpsNo");
			paramValue.add(bean.getPoolAgreement());
		}
		
		
		if(StringUtil.isNotEmpty(bean.getOldAcctNo())){
			sql = sql + " and bail.oldAcctNo = :oldAcctNo ";
			paramName.add("oldAcctNo");
			paramValue.add(bean.getOldAcctNo());
		}
		if(StringUtil.isNotEmpty(bean.getNewAcctNo())){
			sql = sql + " and bail.newAcctNo = :newAcctNo ";
			paramName.add("newAcctNo");
			paramValue.add(bean.getNewAcctNo());
		}
		
		//创建时间
		if(bean.getStartDate() != null){
			sql = sql + " and bail.createTime >=TO_DATE(:createTime, 'yyyy-mm-dd hh24:mi:ss') ";
			paramName.add("createTime");
			paramValue.add(DateUtils.toDateTimeString(bean.getStartDate()));
		}
		
		sql = sql+ "order by createDate desc";
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();

		List result = this.find(sql, paramNames, paramValues,page);
		if(result!=null&&result.size()>0){
			return result;
		}else{
			return null;
		}
	}
	
	@Override
	public String txBailQueryFromCoreJM(PedProtocolDto pro) throws Exception {
		List pedList = new ArrayList();
		if (pro != null) {
			logger.debug("保证金当日流水查询，网银系统客户【"+pro.getCustnumber()+"】查询开始....");
			pedList.add(pro);
		} else {
			logger.debug("保证金当日流水查询，票据池系统查询开始....");
			pedList =  pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null,null, null, null);
		}
		PedProtocolDto dto = null;
		if (pedList != null && pedList.size() > 0) {
			for (int i = 0; i < pedList.size(); i++) {
				dto = (PedProtocolDto) pedList.get(i);
				CoreTransNotes trans = new CoreTransNotes();
				trans.setTranDate(DateUtils.toString(DateUtils.getCurrDate(), "yyyy-MM-dd"));// 交易日期
				trans.setDateEnd(DateUtils.toString(DateUtils.getCurrDate(), "yyyy-MM-dd"));// 终止日期
				trans.setDateStr(DateUtils.toString(DateUtils.getCurrDate(), "yyyy-MM-dd"));// 开始日期
				trans.setAccNo(dto.getMarginAccount());// 保证金账号
				trans.setAmt("0.00");// 金额
				trans.setIsSucced("");// 查询类型
				logger.info("保证金当日流水查询，查询核心接口操作开始....");
				try {
					this.txBailDetail(trans);
				} catch (Exception e) {
					logger.error("保证金当日流水查询核心接口返回异常",e);
				}
				logger.info("保证金当日流水查询，查询核心接口操作结束....");
			}
		}
		return "1";//更新成功
	}
	
}
