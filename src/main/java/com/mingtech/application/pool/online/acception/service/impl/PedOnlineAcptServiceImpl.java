package com.mingtech.application.pool.online.acception.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.service.TaskDispatchConfigService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptInfo;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptInfoHist;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocolHist;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptCacheBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptCacheDetail;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.common.service.OnlineCommonService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfoHist;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
@Service("pedOnlineAcptService")
public class PedOnlineAcptServiceImpl extends GenericServiceImpl implements PedOnlineAcptService {
	private static final Logger logger = Logger.getLogger(PedOnlineAcptServiceImpl.class);
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
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserService userService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private OnlineCommonService onlineCommonService;
	@Autowired
	private GenericHibernateDao sessionDao;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private RedisUtils redisQueueCache;
	@Autowired
	private AutoTaskExeService autoTaskExeService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private FinancialAdviceService  financialAdviceService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private TaskDispatchConfigService taskDispatchConfigService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	/**
	 * 查询生效银承协议
	 */
	public List<PedOnlineAcptProtocol> queryOnlineAcptProtocolList(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineAcptProtocol as dto where 1=1 ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and dto.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			hql.append(" and dto.custName like :custName");
			paramName.add("custName");
			paramValue.add("%"+queryBean.getCustName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			hql.append(" and dto.custOrgcode =:custOrgcode");
			paramName.add("custOrgcode");
			paramValue.add(queryBean.getCustOrgcode());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and dto.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and dto.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and dto.ebkCustNo like :ebkCustNo ");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getAppNo())){
			hql.append(" and dto.appNo =:appNo");
			paramName.add("appNo");
			paramValue.add(queryBean.getAppNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and dto.appName like:appName");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
			hql.append(" and dto.signBranchNo =:signBranchNo");
			paramName.add("signBranchNo");
			paramValue.add(queryBean.getSignBranchNo());
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and dto.signBranchName like:signBranchName");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and dto.dueDate >=:startDate");
			paramName.add("startDate");
			paramValue.add(queryBean.getStartDate());
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and dto.dueDate <=:endDate");
			paramName.add("endDate");
			paramValue.add(queryBean.getEndDate());
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			hql.append(" and dto.protocolStatus =:protocolStatus");
			paramName.add("protocolStatus");
			paramValue.add(queryBean.getProtocolStatus());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineAcptProtocol> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}
	
	public List<PedOnlineAcptProtocol> queryOnlineAcptProtocolList(OnlineQueryBean queryBean,Page page) {
		StringBuffer hql = new StringBuffer();
		hql.append("select dto.ID,dto.BPS_ID,dto.BPS_NO,dto.BPS_NAME,dto.CUST_NUMBER ,dto.CUST_ORGCODE ,dto.CUST_NAME ,dto.PROTOCOL_STATUS ,dto.ONLINE_ACPT_NO ,dto.BASE_CREDIT_NO ,dto.EBK_CUST_NO ,dto.ONLINE_ACPT_TOTAL ,dto.ACCEPTOR_BANK_NO ,dto.ACCEPTOR_BANK_NAME ,dto.DEPOSIT_ACCT_NO ,dto.DEPOSIT_ACCT_NAME ,dto.DEPOSIT_RATE_LEVEL ,dto.DEPOSIT_RATE_FLOAT_FLAG ,dto.DEPOSIT_RATE_FLOAT_VALUE ,dto.DEPOSIT_RATIO ,dto.POOL_CREDIT_RATIO ,dto.FEE_RATE ,dto.IN_ACCT_BRANCH_NO ,dto.IN_ACCT_BRANCH_NAME,dto.CONTRACT_NO,dto.GUARANTOR,dto.APP_NAME,dto.APP_NO,dto.SIGN_BRANCH_NO,dto.SIGN_BRANCH_NAME,dto.OPEN_DATE,dto.CHANGE_DATE,dto.DUE_DATE,dto.CREATE_TIME,dto.UPDATE_TIME,info.usedAmt " +
				"from PED_ONLINE_ACPT_PROTOCOL dto left join (select sum(detail.BILL_AMT) usedAmt,ONLINE_ACPT_NO from PL_ONLINE_ACPT_DETAIL detail where detail.STATUS not in(:status) group by ONLINE_ACPT_NO) info on dto.ONLINE_ACPT_NO = info.ONLINE_ACPT_NO where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		queryBean.getStatuList().add(PublicStaticDefineTab.ACPT_DETAIL_012);
		paramName.add("status");
		paramValue.add(queryBean.getStatuList());
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				hql.append(" and dto.CUST_NUMBER =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getCustName())){
				hql.append(" and dto.CUST_NAME like :custName");
				paramName.add("custName");
				paramValue.add("%"+queryBean.getCustName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
				hql.append(" and dto.CUST_ORGCODE =:CUST_ORGCODE");
				paramName.add("CUST_ORGCODE");
				paramValue.add(queryBean.getCustOrgcode());
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
				hql.append(" and dto.ONLINE_ACPT_NO =:ONLINE_ACPT_NO");
				paramName.add("ONLINE_ACPT_NO");
				paramValue.add(queryBean.getOnlineAcptNo());
			}
			if(StringUtils.isNotBlank(queryBean.getBpsNo())){
				hql.append(" and dto.BPS_NO =:BPS_NO");
				paramName.add("BPS_NO");
				paramValue.add(queryBean.getBpsNo());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				hql.append(" and dto.EBK_CUST_NO like:EBK_CUST_NO");
				paramName.add("EBK_CUST_NO");
				paramValue.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getAppNo())){
				hql.append(" and dto.APP_NO =:APP_NO");
				paramName.add("APP_NO");
				paramValue.add(queryBean.getAppNo());
			}
			if(StringUtils.isNotBlank(queryBean.getAppName())){
				hql.append(" and dto.APP_NAME like :APP_NAME");
				paramName.add("APP_NAME");
				paramValue.add("%"+queryBean.getAppName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
				hql.append(" and dto.SIGN_BRANCH_NO =:SIGN_BRANCH_NO");
				paramName.add("SIGN_BRANCH_NO");
				paramValue.add(queryBean.getSignBranchNo());
			}
			if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
				hql.append(" and dto.SIGN_BRANCH_NAME like :SIGN_BRANCH_NAME");
				paramName.add("SIGN_BRANCH_NAME");
				paramValue.add("%"+queryBean.getSignBranchName()+"%");
			}
			if(null !=queryBean.getStartDate()){
				hql.append(" and dto.DUE_DATE >=:sDUE_DATE");
				paramName.add("sDUE_DATE");
				paramValue.add(queryBean.getStartDate());
			}
			if(null !=queryBean.getEndDate()){
				hql.append(" and dto.DUE_DATE <=:eDUE_DATE");
				paramName.add("eDUE_DATE");
				paramValue.add(queryBean.getEndDate());
			}
			if(StringUtils.isNotBlank(queryBean.getContractNo())){
				hql.append(" and dto.CONTRACT_NO =:CONTRACT_NO");
				paramName.add("CONTRACT_NO");
				paramValue.add(queryBean.getContractNo());
			}
			if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
				hql.append(" and dto.PROTOCOL_STATUS =:PROTOCOL_STATUS");
				paramName.add("PROTOCOL_STATUS");
				paramValue.add(queryBean.getProtocolStatus());
			}
		}
		hql.append(" order by dto.UPDATE_TIME desc");
		
  		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List list = new ArrayList();
  		if (page != null) { // 有分页对象 sql
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,page);
  		} else {
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,null);
  		}
  		if(list.size()>0){
  			List result = new ArrayList();
  			for(int i=0;i<list.size();i++){
  				OnlineQueryBean bean = new OnlineQueryBean();
  				Object[] obj= (Object[])list.get(i);
  				bean.setId((String)obj[0]);
  				bean.setBpsId((String)obj[1]);
  				bean.setBpsNo((String)obj[2]);
  				bean.setBpsName((String)obj[3]);
  				bean.setCustNumber((String)obj[4]);
  				bean.setCustOrgcode((String)obj[5]);
  				bean.setCustName((String)obj[6]);
  				bean.setProtocolStatus((String)obj[7]);
  				bean.setOnlineAcptNo((String)obj[8]);
  				bean.setBaseCreditNo((String)obj[9]);
  				bean.setEbkCustNo((String)obj[10]);
  				bean.setOnlineAcptTotal((BigDecimal)obj[11]);
  				bean.setAcceptorBankNo((String)obj[12]);
  				bean.setAcceptorBankName((String)obj[13]);
  				bean.setDepositAcctNo((String)obj[14]);
  				bean.setDepositAcctName((String)obj[15]);
  				bean.setDepositRateLevel((String)obj[16]);
  				bean.setDepositRateFloatFlag((String)obj[17]);
  				bean.setDepositRateFloatValue((BigDecimal)obj[18]);
  				bean.setDepositRatio((BigDecimal)obj[19]);
  				bean.setPoolCreditRatio((BigDecimal)obj[20]);
  				bean.setFeeRate((BigDecimal)obj[21]);
  				bean.setInAcctBranchNo((String)obj[22]);
  				bean.setInAcctBranchName((String)obj[23]);
  				bean.setContractNo((String)obj[24]);
  				bean.setGuarantor((String)obj[25]);
  				bean.setAppName((String)obj[26]);
  				bean.setAppNo((String)obj[27]);
  				bean.setSignBranchNo((String)obj[28]);
  				bean.setSignBranchName((String)obj[29]);
  				bean.setOpenDate((Date)obj[30]);
  				bean.setChangeDate((Date)obj[31]);
  				bean.setDueDate((Date)obj[32]);
  				bean.setCreateTime((Date)obj[33]);
  				bean.setUpdateTime((Date)obj[34]);
  				bean.setUsedAmt((BigDecimal)obj[35]!=null?(BigDecimal)obj[35]:new BigDecimal(0));
  				result.add(bean);
  			}
  			return result;
  		}
		return list;
	}
	
	/**
	 * 查询生效银承协议
	 */
	public PedOnlineAcptProtocol queryOnlineAcptProtocol(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineAcptProtocol as dto where 1=1 ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and dto.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			hql.append(" and dto.custName like:custName");
			paramName.add("custName");
			paramValue.add("%"+queryBean.getCustName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			hql.append(" and dto.custOrgcode =:custOrgcode");
			paramName.add("custOrgcode");
			paramValue.add(queryBean.getCustOrgcode());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and dto.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and dto.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and dto.ebkCustNo like :ebkCustNo ");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getAppNo())){
			hql.append(" and dto.appNo =:appNo");
			paramName.add("appNo");
			paramValue.add(queryBean.getAppNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and dto.appName like:appName");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
			hql.append(" and dto.signBranchNo =:signBranchNo");
			paramName.add("signBranchNo");
			paramValue.add(queryBean.getSignBranchNo());
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and dto.signBranchName like :signBranchName");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and dto.dueDate >=:startDate");
			paramName.add("startDate");
			paramValue.add(queryBean.getStartDate());
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and dto.dueDate <=:endDate");
			paramName.add("endDate");
			paramValue.add(queryBean.getEndDate());
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			hql.append(" and dto.protocolStatus =:protocolStatus");
			paramName.add("protocolStatus");
			paramValue.add(queryBean.getProtocolStatus());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineAcptProtocol> result = this.find(hql.toString(), paramNames, paramValues );
		if(null != result && result.size()>0){
			PedOnlineAcptProtocol dto = (PedOnlineAcptProtocol)result.get(0);
			OnlineQueryBean bean = new OnlineQueryBean();
			bean.setOnlineAcptNo(dto.getOnlineAcptNo());
			BigDecimal usedAmt = this.getOnlineAcptPtlAmt(bean);
			dto.setUsedAmt(usedAmt != null?usedAmt:new BigDecimal(0));
			return (PedOnlineAcptProtocol)result.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * @description 通过在线银承协议号查询
	 * @author wss
	 * @date 2021-4-27
	 * @param onlineAcptNo 在线银承协议号
	 */
	public PedOnlineAcptProtocol queryOnlinAcptPtlByNo(String onlineAcptNo) {
		String sql ="select dto from PedOnlineAcptProtocol dto where dto.onlineAcptNo=? ";
		List param = new ArrayList();
		param.add(onlineAcptNo);
		List result = this.find(sql, param);
		if(null != result && result.size()>0){
			return (PedOnlineAcptProtocol) result.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 在线银承协议规则校验
	 * @throws Exception 
	 */
	public Ret onlineAcptCheck(OnlineQueryBean protocol) throws Exception {
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
  		//错误信息
		List errors = new ArrayList();
		//关联票据池
		PedProtocolDto pool=null;
		if(null != protocol.getDepositRatio() && new BigDecimal(100).compareTo(protocol.getDepositRatio())!=0){
			try {
				ProtocolQueryBean pBean = new ProtocolQueryBean();
				pBean.setPoolAgreement(protocol.getBpsNo());
				pBean.setIsGroup(PoolComm.NO);
				pool = pedProtocolService.queryProtocolDtoByQueryBean(pBean);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			if(null == pool){
				errors.add("该客户无对应生效票据池信息");
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setSomeList(errors);
				return ret;
			}
			
		}
		//在线业务禁入名单校验 
		protocol.setStatus(PublicStaticDefineTab.STATUS_1);
		boolean flag = onlineManageService.onlineBlackListCheck(protocol);
		if(flag){
			errors.add("该客户属于在线业务禁入名单客户|");
		}
		boolean check = true;//是否校验 票据池协议的保证金账号所属机构与在线协议的入账机构所号跨分行  生效的修改或新增生效校验,失效时不校验
		//修改标识   
		if(PublicStaticDefineTab.MOD01.equals(protocol.getModeType())){//新增
			//在线银承协议编号
			if(StringUtils.isNotBlank(protocol.getOnlineAcptNo())){
				String acptNo =  protocol.getOnlineAcptNo();
				OnlineQueryBean bean =  new OnlineQueryBean();
				bean.setOnlineAcptNo(acptNo);
				List list = this.queryOnlineAcptProtocolList(bean);
				if(null != list && list.size()>0){
					errors.add("在线银承协议编号重复|");
				}
				if(!acptNo.startsWith("YCOL") || !acptNo.substring(4).matches("[0-9]+")){
					errors.add("在线银承协议编号格式有误|");
				}
			}
			//保证金比列
			if(null != protocol.getDepositRatio() && new BigDecimal(100).compareTo(protocol.getDepositRatio())!=0){
				//票据池编号
				errors = this.checkPoolInfo(protocol, pool, errors);
				if(null != pool){
					//客户名称
					if(StringUtils.isNotBlank(protocol.getCustName()) && !protocol.getCustName().equals(pool.getCustname())){
						errors.add("客户名称与票据池客户名称不一致|");
					}
					//核心客户号
					if(StringUtils.isNotBlank(protocol.getCustNumber()) && !protocol.getCustNumber().equals(pool.getCustnumber())){
						errors.add("核心客户号与票据池核心客户号不一致|");
					}
					//担保合同
					if(StringUtils.isNotBlank(protocol.getGuarantorNo())){
						//票据池担保合同是否生效
						if(pool.getContractDueDt().compareTo(DateUtils.getWorkDayDate())<=0){
							errors.add("关联票据池最高额担保合已到期|");
						}
						//【担保合同编号】【担保合同关联票据池编号】【担保人名称】【担保人核心客户号】为该客户票据池关联的“生效”状态的最高额担保合同
						if(!protocol.getContractNo().equals(pool.getContract())){
							errors.add("关联票据池最高额担保合同的担保合同编号不一致|");
						}
						if(!protocol.getBpsNo().equals(pool.getPoolAgreement())){
							errors.add("关联票据池最高额担保合同的票据池编号不一致|");
						}
//						if(!protocol.getGuarantor().equals(pool.getCustname())){
//							errors.add("关联票据池最高额担保合同的担保人名称不一致|");
//						}
						if(!protocol.getGuarantorNo().equals(pool.getCustnumber())){
							errors.add("关联票据池最高额担保合同的担保人核心客户号不一致|");
						}
					}
				}
			}
		}else{ 
			PedOnlineAcptProtocol oldProtocol = this.queryOnlinAcptPtlByNo(protocol.getOnlineAcptNo());
			//在线协议状态 0失效；1生效
			if(PublicStaticDefineTab.STATUS_0.equals(protocol.getProtocolStatus())){
				//修改做失效  不需校验跨分行
				check = false;
			}
//			if(PublicStaticDefineTab.STATUS_1.equals(protocol.getProtocolStatus()) && !oldProtocol.getProtocolStatus().equals(protocol.getProtocolStatus())){
//				if("0".equals(oldProtocol.getProtocolStatus()) && oldProtocol.getDueDate().compareTo(DateUtils.getWorkDayDate())>=0){
//					errors.add("协议已过到期日|");
//				}
//				if(null != protocol.getOpenDate() && !protocol.getOpenDate().equals(oldProtocol.getOpenDate())){
//					errors.add("开通日不可以修改|");
//				}
			//信用风险管理系统传输过来的字段含不可修改字段
		     	errors = this.unableModifyFieldCheck(protocol,oldProtocol,errors);
//			}
			//票据池编号
			if(StringUtils.isNotBlank(protocol.getBpsNo())){
				//保证金比例为100%，但票据池编号必须为空值
				if(null != protocol.getDepositRatio() && new BigDecimal(100).compareTo(protocol.getDepositRatio())==0){
					errors.add("100%保证金担保无需使用票据池担保|");
					logger.info("100%保证金担保无需使用票据池担保|");
				}
				if(null != protocol.getDepositRatio() && new BigDecimal(100).compareTo(protocol.getDepositRatio())!=0){
					//校验关联票据池是否生效
					errors = this.checkPoolInfo(protocol,pool,errors);
				}
			}
			//在线银承总额
			if(null != protocol.getOnlineAcptTotal()){
				BigDecimal oldAmt = this.getOnlineAcptPayeeAmt(protocol.getOnlineAcptNo(),null);
				//客户可融资的最大限额（即发生额，不同在线银承协议的发生额不累计）,可以大于、等于、小于所有【收票人收票总额】的汇总值
				if(null != oldAmt && protocol.getOnlineAcptTotal().compareTo(oldAmt)<0){
					errors.add("在线银承总额小于客户已用额度|");
				}
			}
		}
		//========================================== 通用校验 =======================================
		//保证金全信息校验
		errors = this.checkDepositInfo(protocol,errors);
		//校验收票人信息
		errors = this.onlinPayeesCheck(protocol, errors);
		//承兑行行号
		if(StringUtils.isNotBlank(protocol.getAcceptorBankNo()) && ((12 !=(protocol.getAcceptorBankNo().length()) && !protocol.getAcceptorBankNo().matches("[0-9]+")))){
			errors.add("承兑人承兑行号格式非12位数字|");
		}
		//经办人
		User user=new User();
		if(StringUtils.isNotBlank(protocol.getAppNo())){
			try {
				 user = userService.getUserByLoginName(protocol.getAppNo());
				if(null == user){
					errors.add("票据池系统无经办客户经理信息|");
				}else{
					Department dept=(Department) userService.load(user.getDeptId(),Department.class);
					boolean isManager = false;//是否为客户经理
	    			if(user.getRoleList()!=null && user.getRoleList().size()>0){
	    				for(Role role : (List<Role>)user.getRoleList()){
	    					if( StringUtils.isNotBlank(role.getCode())){						
	    						String roleCode  = role.getCode();
	    						if(PoolComm.roleCode6.equals(roleCode)){
	    							isManager = true;
	    							break;
	    						}
	    					}
	    				}
	    			}
					if(isManager){
						//经办人名称
						if(StringUtils.isNotBlank(protocol.getAppName()) && !protocol.getAppName().equals(user.getName())){
							errors.add("经办人名称与票据池系统签约信息不符|");
						}
						
						boolean isBranchNo = false;//入账机构号是否与客户经理归属机构的内部机构号或者其下属机构的“内部机构号”相同
						//入账机构号是否与客户经理归属机构的内部机构号或者其下属机构的“内部机构号”
						//查询客户经理归属机构的内部机构号或者其下属机构的“内部机构号”
						List list = departmentService.getAllChildrenInnerCodeList(dept.getInnerBankCode(), 1);
						for (int i = 0; i < list.size(); i++) {
							String branchNo = (String) list.get(i);
							if(branchNo.equals(protocol.getInAcctBranchNo())){
								isBranchNo = true;
								break;
							}
						}
						if(!isBranchNo){
							errors.add("入账机构与客户经理票据池归属机构不相符|");
						}else{
							//查询入账机构号的“武汉一级支行”或“非武汉分行”
							String name = departmentService.queryParentBranch(dept.getId());
							if(name != null){
								protocol.setInAcctBranchName(name);
							}
						}
						
	    			}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		logger.info("在线银承协议推送签约机构号："+protocol.getSignBranchNo());
		//签约机构
		if(StringUtils.isNotBlank(protocol.getSignBranchNo())){
//			if(null != pool && !pool.getSignDeptNo().equals(protocol.getSignBranchNo())){
//				errors.add("签约机构与票据池系统签约信息不符|");
//			}
			if(null!=user && null!=user.getDeptId()){
				Department dept=(Department) userService.load(user.getDeptId(),Department.class);
				logger.info("在线银承协议推送通过客户经理查询的客户经理归属机构号："+dept.getInnerBankCode());
				if(null ==dept){
					errors.add("经办人签约机构与票据池系统签约信息不符|");
				}else{
					if(!dept.getInnerBankCode().equals(protocol.getSignBranchNo())){
						errors.add("经办人签约机构与票据池系统签约信息不符|");
					}
				}
			}
		}
		//入账机构所号 
		if(StringUtils.isNotBlank(protocol.getInAcctBranchNo())){
			Department dep = departmentService.queryByInnerBankCode(protocol.getInAcctBranchNo());
			if(null == dep){
				errors.add("入账机构未在票据池系统登记|");
			}else{
//				protocol.setInAcctBranchName(dep.getName());
				if(pool != null && null != protocol.getDepositRatio() && new BigDecimal(100).compareTo(protocol.getDepositRatio())!=0){
					if(check){
						/**
						 * 在线银承协议的入账机构与保证金账号的归属机构需要相同
						 * （在线协议的入账机构与保证金账号的归属机构不允许跨分行)
						 */
						CoreTransNotes transNotes = new CoreTransNotes();
						transNotes.setAccNo(pool.getMarginAccount());
						transNotes.setCurrentFlag("1");
						ReturnMessageNew response1 = poolCoreService.PJH716040Handler(transNotes, "0");
						if(response1.isTxSuccess()){
							String branch = (String) response1.getBody().get("OPEN_BRANCH");//机构号
							String accBranch = (String) response1.getBody().get("ACCOUNT_BRANCH_ID");//核算机构
							
					        if(!dep.getAuditBankCode().equals(accBranch)){
					        	errors.add("票据池协议的保证金账号所属机构与在线协议的入账机构所号跨分行，不允许修改！|");
					        }
						}
					}
				}
				
				
			}
			
			
			
		}
		//手续费率
		if(null != protocol.getFeeRate()){
			if(protocol.getFeeRate().doubleValue()<=0 || protocol.getFeeRate().doubleValue()> 0.05){
				errors.add("手续费率（%）范围为0-0.05%|");
			}
		}
		//到期日
		if(null != protocol.getDueDate()){
			if(protocol.getDueDate().compareTo(protocol.getOpenDate())>1){
				errors.add("不得早于开通期日期|");
			}
			if(protocol.getDueDate().compareTo(protocol.getChangeDate())>1){
				errors.add("不得早于变更日期|");
			}
		}
		if(errors.size()>0){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setSomeList(errors);
		}
		return ret;
	}
	
	/**
	 * @author wss
	 * @date 2021-5-7
	 * @description 在线银承业务规则校验  全量校验
	 * @param queryBean
	 * @param detail
	 * @throws Exception 
	 */
	public Map acptApplyCheck(OnlineQueryBean queryBean,PedOnlineAcptProtocol protocol,PedProtocolDto pool) throws Exception {
		Map map = new HashMap();
		String error = "";//禁止
		String msg = "";//提示
		if(new BigDecimal(100).compareTo(protocol.getDepositRatio()) !=0){
			if(null == pool){
				error += "该"+queryBean.getOnlineAcptNo()+"在线银承协议关联票据池不存在^";
			}else{
				map.put("pool", pool);
				//票据池对账  100%保证金模式不校验本条；若为连续两个月对账任务未处理或最新对账结果为“核对不一致”，拒绝。
				Ret canCreateCredit = pedProtocolService.isCanCreateCreditByCheckResult(pool); 
				if(canCreateCredit.getRET_CODE().equals(Constants.CREDIT_10)) { 
					error = error+"核对不一致或连续两个月票据池业务未对账^";
				}
				//票据池收费 100%保证金模式不校验本条；若有欠费，拒绝。
				if(!pedProtocolService.isPaid(pool)){
					error = error+"票据池有欠费^";
				}
				
				//票据池状态 100%保证金模式不校验本条；若为“已冻结”，拒绝。
				if(PoolComm.FROZEN_STATUS_03.equals(pool.getFrozenstate())){
					error = error+"票据池已冻结^";
				}
				//票据池融资签约状态 100%保证金模式不校验本条；若未签约融资功能，拒绝。
				if(!PoolComm.OPEN_01.equals(pool.getOpenFlag())){
					error = error+"票据池未签约融资功能^";
				}
				//在线银承协议关联的票据池担保合同状态   100%保证金模式不校验本条；非100%保证金模式，票据池担保合同失效(或超过到期日)，拒绝。
				if(pool.getContractDueDt().compareTo(new Date())<=0){
					error = error+"票据池担保合同已失效^";
				}
				//票据池担保合同号与在线协议担保合同号一致
				if(!pool.getContract().equals(protocol.getContractNo())){
					error = error+"票据池担保合同号与在线协议担保合同号不一致^";
				}
				
//				if(!pool.getContract().equals(protocol.getContractNo())){
//					error = error+"票据池担保合同已失效^";
//				}
				//票据池担保合同可用额度 非100%保证金模式，票据池担保合同可用额度小于∑[每张银行承兑汇票票面金额-Roundup（每张银行承兑汇票票面金额*对应在线银承协议—保证金比例，2）]，拒绝。
				BigDecimal big = new BigDecimal(0);
				big = protocol.getPoolCreditRatio().divide(new BigDecimal(100));
				big = big.multiply(queryBean.getOnlineAcptTotal());//需占用额度
				logger.info("票据池担保合同可用余额..................."+pool.getCreditFreeAmount());
				logger.info("需占用额度..................."+big);
				if(pool.getCreditFreeAmount().compareTo(big.setScale(2, RoundingMode.UP))<0){
					error = error+"票据池担保合同余额不足|";
				}
				
				//票据池低风险可用余额(与所有票总额的比较)  (提示校验)
				if(PoolComm.POOL_MODEL_01.equals(pool.getPoolMode())){
					//票据池低风险可用额度小于∑[每张银行承兑汇票票面金额-Roundup（每张银行承兑汇票票面金额*对应在线银承协议—保证金比例，2）]
//					msg = msg +"当前票据池低风险可用额度不足|";
				}else if(PoolComm.POOL_MODEL_02.equals(pool.getPoolMode())){
					//每张银行承兑汇票票面金额*对应在线银承协议—票据池额度比例，根据票面到期日，进行不同区段扣减，不能保证期限配比各时点低风险可用额度均大于0
//					msg = msg +"当前票据池低风险可用额度不足|";
//					msg = msg +"票据池额度期限匹配不通过|";
				}
			}
		}
		//在线银承协议状态   若为“失效”或超过到期日
		if(!PublicStaticDefineTab.STATUS_1.equals(protocol.getProtocolStatus()) || (protocol.getDueDate().compareTo(DateUtils.getWorkDayDate())<=0)){
			error = error+"在线银承协议已失效^";
		}
		/**
		 * 外层调用方法已做合同金额的校验(此校验暂时不用)
		 */
		//在线银承合同可用余额（即在线银承协议可用余额与所有票面金额比较）
		/*List list = this.queryOnlineAcptProtocolList(queryBean, null);
		if(null != list && list.size()>0){
			OnlineQueryBean pro = (OnlineQueryBean) list.get(0);
			logger.info("在线银承已用金额:"+pro.getUsedAmt()+"..................");
			logger.info("在线银承未用金额:"+protocol.getOnlineAcptTotal().subtract(pro.getUsedAmt())+"..................");
			if(queryBean.getOnlineAcptTotal().compareTo(protocol.getOnlineAcptTotal().subtract(pro.getUsedAmt()))>0){
				error = error+"在线银承合同余额小于客户申请金额^";
			}
		}*/
		//开关校验
		if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
		   Ret	ret = onlineManageService.checkOnlineSwitch(protocol.getSignBranchNo(),PublicStaticDefineTab.PRODUCT_001);
		   if(!ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			msg = msg+ret.getRET_MSG();
			error = error+ret.getError_MSG();
		   }
		}

		//复核岗校验
		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
			CoreTransNotes note = new CoreTransNotes();
			note.setAccNo(protocol.getDepositAcctNo());
			note.setCurrentFlag("1");
			ReturnMessageNew result = poolCoreService.PJH716040Handler(note, "0");
			if(result.isTxSuccess()){
				//扣收账户状态
				String status = (String) result.getBody().get("ACCT_STATUS");
				//是否未年检
				String ANNUAL_CHECK_FLAG = (String) result.getBody().get("ANNUAL_CHECK_FLAG");
				//核准状态
				String APPROVE_FLAG = (String) result.getBody().get("APPROVE_FLAG");
				//是否冻结
				String IS_FRZ_FLAG = (String) result.getBody().get("IS_FRZ_FLAG");
				//是否止付
				String IS_STOP_FLAG = (String) result.getBody().get("IS_STOP_FLAG");
				
				String CR_CONTROL_BALANCE = (String) result.getBody().get("CR_CONTROL_BALANCE");//贷方控制余额
				BigDecimal totalAmt = BigDecimalUtils.valueOf((String) result.getBody().get("BALANCE"));
				BigDecimal fee = queryBean.getOnlineAcptTotal().multiply(protocol.getFeeRate().divide(new BigDecimal(100)));
				BigDecimal deposit = queryBean.getOnlineAcptTotal().multiply(protocol.getDepositRatio().divide(new BigDecimal(100)));
				
				if("9".equals(status)){
					error = error+"扣款账户状态异常|";
				}else if("1".equals(ANNUAL_CHECK_FLAG)){
					error = error+"扣款账户状态异常|";
				}else if("0".equals(APPROVE_FLAG)){
					error = error+"扣款账户状态异常|";
				}else if("1".equals(IS_STOP_FLAG)){
					error = error+"扣款账户状态异常|";
				}else if(new BigDecimal(CR_CONTROL_BALANCE).compareTo(fee.add(deposit))<0){//冻结（不付）  即：交易金额 > 贷方控制金额
					error = error+"扣款账户状态异常|";
				}
				
				//扣收金额(单张票校验)
				List details = queryBean.getDetalis();
				boolean falg = false;
				if(details != null){
					for (int i = 0; i < details.size(); i++) {
						
						PlOnlineAcptDetail detail = (PlOnlineAcptDetail) details.get(i);
						
						BigDecimal amt = detail.getBillAmt().multiply(protocol.getFeeRate().divide(new BigDecimal(100)));
						if(totalAmt.compareTo(amt.add(deposit))>0){
							falg = true;
						}
					}
				}
				if(!falg){
					error = error+"扣收账户余额不足^";
				}
			}
		}
		map.put("error", error);
		map.put("msg", msg);
		return map;
	}
	/**
	 * @author wss
	 * @date 2021-5-7
	 * @description 在线银承收款人校验
	 * @param queryBean
	 * @param detail
	 * @throws Exception 
	 */
	public Map acptPayeeCheck(OnlineQueryBean queryBean,PedOnlineAcptProtocol protocol,PlOnlineAcptDetail detail,String msg,String error) throws Exception {
		Map map = new HashMap();
		logger.info("在线银承协议承兑人行号为："+protocol.getAcceptorBankNo()+"发送过来的明细行号为："+detail.getAcptBankCode());
		//字段校验
		if(!protocol.getAcceptorBankName().equals(detail.getAcptBankName()) || !protocol.getAcceptorBankNo().equals(detail.getAcptBankCode())){
			error = error+"申请信息与在线协议信息不一致^";
		}
		queryBean.setPayeeAcctName(detail.getPayeeAcctName());
		queryBean.setPayeeAcctNo(detail.getPayeeAcct());
		queryBean.setPayeeOpenBankName(detail.getPayeeBankName());
		queryBean.setPayeeOpenBankNo(detail.getPayeeBankCode());
		queryBean.setOnlineAcptNo(queryBean.getOnlineAcptNo());
		
		List payees = null;
		//如果输入中有一项是空值，则认为收票人信息不存在
		if(StringUtil.isNotBlank(detail.getPayeeAcctName())&&StringUtil.isNotBlank(detail.getPayeeAcct())
				&&StringUtil.isNotBlank(detail.getPayeeBankCode())&&StringUtil.isNotBlank(queryBean.getOnlineAcptNo())){			
			payees = this.queryOnlineAcptPayeeList(queryBean, null);
		}
		
		if(null == payees || 0 == payees.size()){
			//收票人信息准确
			error = error+"第"+detail.getBillSerialNo()+"行收票人信息不存在^";
		}else{
			PedOnlineAcptInfo payee = (PedOnlineAcptInfo) payees.get(0);
			if(!detail.getPayeeAcctName().equals(payee.getPayeeAcctName()) || !detail.getPayeeBankCode().equals(payee.getPayeeOpenBankNo()) || !detail.getPayeeAcct().equals(payee.getPayeeAcctNo())){
				error = error+"第"+detail.getBillSerialNo()+"行收票人信息有误^";
			}
			if(!PublicStaticDefineTab.STATUS_1.equals(payee.getPayeeStatus())){
				//收票人状态
				error = error+"第"+detail.getBillSerialNo()+"行收票人信息已失效^";
			}
			Iterator<Map.Entry<String, BigDecimal>> it=queryBean.getMap().entrySet().iterator();
			while(it.hasNext()){
				Entry<String, BigDecimal> entry=it.next();
				if(entry.getKey().equals(payee.getPayeeAcctNo())){
					if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
						if(detail.getBillAmt().compareTo(entry.getValue())>0){
							//收票人可用余额
							error = error+"第"+detail.getBillSerialNo()+"行收票人可用余额不足^";
						}else{
							queryBean.getMap().put(entry.getKey(), entry.getValue().subtract(detail.getBillAmt()));
						}

					}else{
						logger.info("票面金额："+detail.getBillAmt());
						logger.info("收票人可用金额："+entry.getValue());
						
						if(detail.getBillAmt().compareTo(entry.getValue())>0){
							//收票人可用余额
							error = error+"第"+detail.getBillSerialNo()+"行收票人可用余额不足^";
						}else{
							queryBean.getMap().put(entry.getKey(), entry.getValue().subtract(detail.getBillAmt()));
						}

					}
					
				}
			}
		}
		boolean flag = DateTimeUtil.isMoreThanNYear(DateUtils.getCurrDate(), detail.getDueDate(), 1);
		if(flag){
			error = error+"第"+detail.getBillSerialNo()+"行票据期限超过一年^";
		}
		map.put("error", error);
		map.put("msg", msg);
		return map;
	}
	
	/**
	 * @author wss
	 * @date 2021-5-8
	 * @description 校验出票日期和到期日是否间隔一年
	 */
	public boolean verfiyDate(Date iDate, Date dDate) throws Exception {
		if (iDate == null || iDate.equals("") || dDate == null
				|| dDate.equals(""))
			throw new Exception("!!!!错误信息：调用verfiyDate服务传入参数为空!!!!");
		boolean rsut = false;
		int year = 365;
		Long result = new Long((dDate.getTime() - iDate.getTime())
				/ (1000 * 60 * 60 * 24));
		/* 判断到期日期所在年是否为闰年 */
		year = DateUtils.isLeapYear(dDate) ? 366 : year;
		rsut = result.intValue() >= 0 && result.intValue() <= year ? true
				: rsut;
		return rsut;
	}
	
	/**
	 * @description 查询客户总额度
	 * @author wss
	 * @date 2021-4-28
	 * @param onlineAcptNo
	 * @param payeeId
	 */
	public BigDecimal getOnlineAcptPayeeAmt(String onlineAcptNo,String payeeId) {
		String sql ="select sum(dto.payeeUsedAmt) from PedOnlineAcptInfo dto where dto.onlineAcptNo=? ";
		List param = new ArrayList();
		param.add(onlineAcptNo);
		if(StringUtils.isNotBlank(payeeId)){
			sql= sql+" and dto.payeeId=?";
			param.add(payeeId);
		}
		List result = this.find(sql, param);
		if(null != result && result.size()>0){
			return (BigDecimal) result.get(0);
		}else{
			return null;
		}
	}

	/**
	 * @description 保证金全信息校验
	 * @author wss
	 * @date 2021-4-27
	 * @param protocol 银承协议信息
	 * @param errors 错误提示
	 * @throws Exception 
	 */
	private List checkDepositInfo(OnlineQueryBean protocol, List errors) throws Exception {
		//票据池额度占用比例（%）
		if(null != protocol.getPoolCreditRatio() && !(protocol.getPoolCreditRatio().intValue()>=0) && !(protocol.getPoolCreditRatio().intValue()<=100)){
			errors.add("票据池额度占用比例（%）应为0%（含）-100%（含）|");
		}
		//保证金比例（%）
		if(null != protocol.getDepositRatio() && !(protocol.getDepositRatio().intValue()>=0) && !(protocol.getDepositRatio().intValue()<=100)){
			errors.add("保证金比例应为0%（含）-100%（含）|");
		}
		//总比例
		if(new BigDecimal(100).compareTo(protocol.getPoolCreditRatio().add(protocol.getDepositRatio()))!=0){
			errors.add("保证金比例（%）+票据池额度占用比例（%）不为100%|");
		}
		//保证金利率浮动值
		if(null != protocol.getDepositRateFloatValue()){
			//保证金利率浮动标志 
			if(StringUtils.isNotBlank(protocol.getDepositRateFloatFlag()) && protocol.getDepositRateFloatFlag().equals("0") && new BigDecimal(0).compareTo(protocol.getDepositRateFloatValue())!=0){
				errors.add("保证金利率浮动标志与基准保持一致模式，保证金利率浮动值应为0|");
			}
			//保证金利率档次是活期，只能是与基准保持一致，利率浮动值只能为0
			if(StringUtils.isNotBlank(protocol.getDepositRateLevel()) && "0".equals(protocol.getDepositRateLevel()) && ("0".equals(protocol.getDepositRateFloatFlag()) && new BigDecimal(0).compareTo(protocol.getDepositRateFloatValue())!=0)){
				errors.add("保证金利率档次格式不正确|");
			}
		}
		CoreTransNotes note = new CoreTransNotes();
		note.setAccNo(protocol.getDepositAcctNo());
		note.setCurrentFlag("1");
		ReturnMessageNew result = poolCoreService.PJH716040Handler(note, "0");
		if(result.isTxSuccess()){
			String OPEN_BRANCH = (String) result.getBody().get("OPEN_BRANCH");
			String BRANCH_NAME = (String) result.getBody().get("BRANCH_NAME");
			if(StringUtils.isNotBlank(OPEN_BRANCH) && StringUtils.isNotBlank(BRANCH_NAME)){
				Department dept = departmentService.queryByInnerBankCode(OPEN_BRANCH);
				if(null== dept){
					throw new Exception("扣收保证金账户在核心对应机构"+OPEN_BRANCH+"在票据池系统不存在");
				}
				protocol.setDepositBranchNo(dept.getBankNumber());
				protocol.setDepositBranchName(BRANCH_NAME);
			}else{
				throw new Exception("扣收保证金账户未查询到开户信息!");
			}
		}else{
			throw new Exception("扣收保证金账户未查询到开户信息!");
		}
		
		
		return errors;
	}

	/**
	 * @description 校验关联票据池信息
	 * @author wss
	 * @date 2021-4-26
	 * @param protocol 信贷传参
	 * @param pool 票据池信息
	 * @param errors 错误日志
	 */
	public List checkPoolInfo(OnlineQueryBean protocol,PedProtocolDto pool, List errors) {
		if(null == errors){
			errors = new ArrayList();
		}
		if(null == pool){
			errors.add("该客户无对应生效票据池信息|");
		}else{
			if(StringUtils.isNotBlank(pool.getContract()) && !PoolComm.OPEN_01.equals(pool.getOpenFlag())){
				errors.add("该客户票据池未签约融资功能|");
			}
			if(protocol.getChangeDate().compareTo(pool.getContractEffectiveDt())<0){
				errors.add("该客户票据池未关联最高额担保合同|");
			}
			protocol.setBpsId(pool.getPoolInfoId());
			protocol.setBpsName(pool.getPoolName());
		}
		return errors;
	}
	
	/**
	 * @description 校验关联票据池信息
	 * @author wss
	 * @date 2021-4-26
	 * @param protocol 信贷传参
	 * @param pool 票据池信息
	 * @param errors 错误日志
	 */
	public List checkPoolInfoForEBK(OnlineQueryBean protocol,PedProtocolDto pool, List errors) {
		if(null == errors){
			errors = new ArrayList();
		}
		if(null == pool){
			errors.add("无生效状态的单户票据池|");
		}else{
			if(StringUtils.isNotBlank(pool.getContract()) && !PoolComm.OPEN_01.equals(pool.getOpenFlag())){
				errors.add("无生效状态的单户票据池|");
			}else{
				if(pool.getEffstartdate().compareTo(pool.getContractEffectiveDt())<0 || pool.getEffenddate().compareTo(pool.getContractDueDt())>0){
					errors.add("无生效状态票据池最高额担保合同|");
				}
			}
		}
		return errors;
	}
	
	/**
	 * @description 查询在线银承收票人
	 * @author wss
	 * @date 2021-4-27
	 * @param onlineAcptNo 协议编号
	 * @param status 0失效；1生效
	 */
	public List queryOnlineAcptPayeeList(String onlineAcptNo, String status) {
		String sql ="select dto from PedOnlineAcptInfo dto where dto.onlineAcptNo=? ";
		List param = new ArrayList();
		param.add(onlineAcptNo);
		if(StringUtils.isNotBlank(status)){
			sql= sql+" and dto.payeeStatus=?";
			param.add(status);
		}
		List result = this.find(sql, param);
		return result;
	}
	
	/**
	 * @description 查询在线银承收票人
	 * @author wss
	 * @date 2021-4-27
	 * @param queryBean
	 * @param page
	 */
	public List queryOnlineAcptPayeeList(OnlineQueryBean queryBean, Page page) {
		String sql ="select dto from PedOnlineAcptInfo dto,PedOnlineAcptProtocol info where dto.acptId=info.id ";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
				sql= sql+" and dto.onlineAcptNo=?";
				param.add(queryBean.getOnlineAcptNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				sql= sql+" and dto.payeeStatus=?";
				param.add(queryBean.getPayeeStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getAcptId())){
				sql= sql+" and dto.acptId=?";
				param.add(queryBean.getAcptId());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and info.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
				sql= sql+" and dto.payeeAcctName like ?";
				param.add("%"+queryBean.getPayeeAcctName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctNo())){
				sql= sql+" and dto.payeeAcctNo =?";
				param.add(queryBean.getPayeeAcctNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankNo())){
				sql= sql+" and dto.payeeOpenBankNo =?";
				param.add(queryBean.getPayeeOpenBankNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankName())){
				sql= sql+" and dto.payeeOpenBankName like ?";
				param.add("%"+queryBean.getPayeeOpenBankName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getId())){
				sql= sql+" and info.id=?";
				param.add(queryBean.getId());
			}
		}
		System.out.println(param);
		List result = this.find(sql, param, page);
		return result;
	}
	
	/**
	 * @description 查询在线银承收票人 多表联查 实时查看
	 * @author 
	 * @date 2021-8-18
	 * @param queryBean
	 * @param page
	 */
	public List queryOnlineAcptPayeeListByBean(OnlineQueryBean queryBean, Page page) {
		StringBuffer hql = new StringBuffer();
		hql.append("select info.ID,info.PAYEE_ID,info.PAYEE_ACCT_NAME,info.PAYEE_ACCT_NO,info.PAYEE_OPEN_BANK_NO,info.PAYEE_OPEN_BANK_NAME,info.PAYEE_TOTAL_AMT,info.PAYEE_STATUS,info.ONLINE_ACPT_NO,info.ACPT_ID,info.CREATE_TIME,info.UPDATE_TIME,d.usedAmt,info.PAYEE_CUST_NAME " +
				"from PED_ONLINE_ACPT_INFO info left join PED_ONLINE_ACPT_PROTOCOL dto on info.ACPT_ID = dto.id " +
				"left join (select sum(detail.BILL_AMT) usedAmt,PAYEE_ACCT,ONLINE_ACPT_NO from PL_ONLINE_ACPT_DETAIL detail" +
				" where detail.STATUS not in(:status)  group by PAYEE_ACCT,ONLINE_ACPT_NO) d on info.PAYEE_ACCT_NO = d.PAYEE_ACCT AND D.ONLINE_ACPT_NO = INFO.ONLINE_ACPT_NO " +
				"  where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("status");
		queryBean.getStatuList().add(PublicStaticDefineTab.ACPT_DETAIL_012);
		paramValue.add(queryBean.getStatuList());

		
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				hql.append(" and info.ONLINE_ACPT_NO =:onlineAcptNo");
				paramName.add("onlineAcptNo");
				paramValue.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				hql.append(" and info.PAYEE_STATUS=:payeeStatus");
				paramName.add("payeeStatus");
				paramValue.add(queryBean.getPayeeStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
				hql.append(" and dto.PROTOCOL_STATUS=:protocolStatus");
				paramName.add("protocolStatus");
				paramValue.add(queryBean.getProtocolStatus());
			}
			
			if(StringUtils.isNotBlank(queryBean.getAcptId())){
				hql.append(" and info.ACPT_ID=:acptId");
				paramName.add("acptId");
				paramValue.add(queryBean.getAcptId());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				hql.append(" and dto.EBK_CUST_NO like:ebkCustNo");
				paramName.add("ebkCustNo");
				paramValue.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
				hql.append(" and info.PAYEE_ACCT_NAME like:payeeAcctName");
				paramName.add("payeeAcctName");
				paramValue.add("%"+queryBean.getPayeeAcctName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctNo())){
				hql.append(" and info.PAYEE_ACCT_NO =:payeeAcctNo");
				paramName.add("payeeAcctNo");
				paramValue.add(queryBean.getPayeeAcctNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankNo())){
				hql.append(" and info.PAYEE_OPEN_BANK_NO =:payeeOpenBankNo");
				paramName.add("payeeOpenBankNo");
				paramValue.add(queryBean.getPayeeOpenBankNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankName())){
				hql.append(" and info.PAYEE_OPEN_BANK_NAME like :payeeOpenBankName");
				paramName.add("payeeOpenBankName");
				paramValue.add("%"+queryBean.getPayeeOpenBankName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
				hql.append(" and info.ONLINE_ACPT_NO =:onlineAcptNo");
				paramName.add("onlineAcptNo");
				paramValue.add(queryBean.getOnlineNo());
			}
		}
		
  		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List list = new ArrayList();
  		if (page != null) { // 有分页对象 sql
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,page);
  		} else {
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,null);
  		}
  		if(list.size()>0){
  			List result = new ArrayList();
  			for(int i=0;i<list.size();i++){
  				OnlineQueryBean bean = new OnlineQueryBean();
  				Object[] obj= (Object[])list.get(i);
  				bean.setId((String)obj[0]);
  				bean.setPayeeId((String)obj[1]);
  				bean.setPayeeAcctName((String)obj[2]);
  				bean.setPayeeAcctNo((String)obj[3]);
  				bean.setPayeeOpenBankNo((String)obj[4]);
  				bean.setPayeeOpenBankName((String)obj[5]);
  				bean.setPayeeTotalAmt((BigDecimal)obj[6]);
  				bean.setPayeeStatus((String)obj[7]);
  				bean.setOnlineCrdtNo((String)obj[8]);
  				bean.setCrdtId((String)obj[9]);
  				bean.setCreateTime((Date)obj[10]);
  				bean.setUpdateTime((Date)obj[11]);
  				bean.setPayeeUsedAmt((BigDecimal)obj[12]!= null?(BigDecimal)obj[12]:new BigDecimal(0));
  				bean.setPayeeFreeAmt(bean.getPayeeTotalAmt().subtract(bean.getPayeeUsedAmt()));
  				bean.setPayeeCustName((String)obj[13]);
  				result.add(bean);    
  			}
  			return result;
  		}
		return list;
	}
	
	
	public List queryOnlineAcptPayeeListBean(OnlineQueryBean queryBean, Page page) {
		StringBuffer hql = new StringBuffer();
		hql.append("select info.ID,info.PAYEE_ID,info.PAYEE_ACCT_NAME,info.PAYEE_ACCT_NO,info.PAYEE_OPEN_BANK_NO,info.PAYEE_OPEN_BANK_NAME,info.PAYEE_TOTAL_AMT,info.PAYEE_STATUS,info.ONLINE_ACPT_NO,info.ACPT_ID,info.CREATE_TIME,info.UPDATE_TIME,d.usedAmt " +
				"from PED_ONLINE_ACPT_INFO info left join PED_ONLINE_ACPT_PROTOCOL dto on info.ACPT_ID = dto.id left join (select sum(detail.BILL_AMT) usedAmt,PAYEE_ACCT,ONLINE_ACPT_NO from PL_ONLINE_ACPT_DETAIL detail where detail.STATUS not in(:status) and detail.ACPT_BATCH_ID != :batchId group by PAYEE_ACCT,ONLINE_ACPT_NO) d on info.PAYEE_ACCT_NO = d.PAYEE_ACCT AND D.ONLINE_ACPT_NO = INFO.ONLINE_ACPT_NO  where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("status");
		queryBean.getStatuList().add(PublicStaticDefineTab.ACPT_DETAIL_012);
		paramValue.add(queryBean.getStatuList());
		paramName.add("batchId");
		paramValue.add(queryBean.getAcptBatchId());
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
				hql.append(" and info.ONLINE_ACPT_NO =:onlineAcptNo");
				paramName.add("onlineAcptNo");
				paramValue.add(queryBean.getOnlineAcptNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				hql.append(" and info.PAYEE_STATUS=:payeeStatus");
				paramName.add("payeeStatus");
				paramValue.add(queryBean.getPayeeStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getAcptId())){
				hql.append(" and info.ACPT_ID=:acptId");
				paramName.add("acptId");
				paramValue.add(queryBean.getAcptId());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				hql.append(" and dto.EBK_CUST_NO like:ebkCustNo");
				paramName.add("ebkCustNo");
				paramValue.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
				hql.append(" and info.PAYEE_ACCT_NAME like:payeeAcctName");
				paramName.add("payeeAcctName");
				paramValue.add("%"+queryBean.getPayeeAcctName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctNo())){
				hql.append(" and info.PAYEE_ACCT_NO =:payeeAcctNo");
				paramName.add("payeeAcctNo");
				paramValue.add(queryBean.getPayeeAcctNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankNo())){
				hql.append(" and info.PAYEE_OPEN_BANK_NO =:payeeOpenBankNo");
				paramName.add("payeeOpenBankNo");
				paramValue.add(queryBean.getPayeeOpenBankNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankName())){
				hql.append(" and info.PAYEE_OPEN_BANK_NAME like :payeeOpenBankName");
				paramName.add("payeeOpenBankName");
				paramValue.add("%"+queryBean.getPayeeOpenBankName()+"%");
			}
		}
		
  		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List list = new ArrayList();
  		if (page != null) { // 有分页对象 sql
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,page);
  		} else {
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,null);
  		}
  		if(list.size()>0){
  			List result = new ArrayList();
  			for(int i=0;i<list.size();i++){
  				OnlineQueryBean bean = new OnlineQueryBean();
  				Object[] obj= (Object[])list.get(i);
  				bean.setId((String)obj[0]);
  				bean.setPayeeId((String)obj[1]);
  				bean.setPayeeAcctName((String)obj[2]);
  				bean.setPayeeAcctNo((String)obj[3]);
  				bean.setPayeeOpenBankNo((String)obj[4]);
  				bean.setPayeeOpenBankName((String)obj[5]);
  				bean.setPayeeTotalAmt((BigDecimal)obj[6]);
  				bean.setPayeeStatus((String)obj[7]);
  				bean.setOnlineAcptNo((String)obj[8]);
  				bean.setAcptId((String)obj[9]);
  				bean.setCreateTime((Date)obj[10]);
  				bean.setUpdateTime((Date)obj[11]);
  				bean.setPayeeUsedAmt((BigDecimal)obj[12]);
  				result.add(bean);    
  			}
  			return result;
  		}
		return list;
	}
	
	
	/**
	 * @description 查询在线银承收票人历史 多表联查 实时查看
	 * @author 
	 * @date 2021-8-18
	 * @param queryBean
	 * @param page
	 */
	public List queryOnlineAcptPayeeHistListByBean(OnlineQueryBean queryBean, Page page) {
		StringBuffer hql = new StringBuffer();
		hql.append("select info.ID,info.PAYEE_ID,info.PAYEE_ACCT_NAME,info.PAYEE_ACCT_NO,info.PAYEE_OPEN_BANK_NO,info.PAYEE_OPEN_BANK_NAME,info.PAYEE_TOTAL_AMT,info.PAYEE_STATUS,info.ONLINE_ACPT_NO,info.ACPT_ID,info.CREATE_TIME,info.UPDATE_TIME,d.usedAmt,info.mode_type " +
				"from PED_ONLINE_ACPT_INFO_HIST info left join PED_ONLINE_ACPT_PROTOCOL dto on info.ACPT_ID = dto.id left join (select sum(detail.BILL_AMT) usedAmt,PAYEE_ACCT from PL_ONLINE_ACPT_DETAIL detail where detail.STATUS not in(:status) group by PAYEE_ACCT) d on info.PAYEE_ACCT_NO = d.PAYEE_ACCT where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("status");
		queryBean.getStatuList().add(PublicStaticDefineTab.ACPT_DETAIL_012);
		paramValue.add(queryBean.getStatuList());
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				hql.append(" and info.ONLINE_ACPT_NO =:onlineAcptNo");
				paramName.add("onlineAcptNo");
				paramValue.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				hql.append(" and info.PAYEE_STATUS=:payeeStatus");
				paramName.add("payeeStatus");
				paramValue.add(queryBean.getPayeeStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getAcptId())){
				hql.append(" and info.ACPT_ID=:acptId");
				paramName.add("acptId");
				paramValue.add(queryBean.getAcptId());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				hql.append(" and dto.EBK_CUST_NO like:ebkCustNo");
				paramName.add("ebkCustNo");
				paramValue.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
				hql.append(" and info.PAYEE_ACCT_NAME like:payeeAcctName");
				paramName.add("payeeAcctName");
				paramValue.add("%"+queryBean.getPayeeAcctName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctNo())){
				hql.append(" and info.PAYEE_ACCT_NO =:payeeAcctNo");
				paramName.add("payeeAcctNo");
				paramValue.add(queryBean.getPayeeAcctNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankNo())){
				hql.append(" and info.PAYEE_OPEN_BANK_NO =:payeeOpenBankNo");
				paramName.add("payeeOpenBankNo");
				paramValue.add(queryBean.getPayeeOpenBankNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankName())){
				hql.append(" and info.PAYEE_OPEN_BANK_NAME like :payeeOpenBankName");
				paramName.add("payeeOpenBankName");
				paramValue.add("%"+queryBean.getPayeeOpenBankName()+"%");
			}
			
			if(StringUtils.isNotBlank(queryBean.getModeMark())){
				hql.append(" and info.MODE_MARK =:MODE_MARK");
				paramName.add("MODE_MARK");
				paramValue.add(queryBean.getModeMark());
			}
			hql.append(" order by info.update_time desc");

		}
		
  		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List list = new ArrayList();
  		if (page != null) { // 有分页对象 sql
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,page);
  		} else {
  			list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,null);
  		}
  		if(list.size()>0){
  			List result = new ArrayList();
  			for(int i=0;i<list.size();i++){
  				OnlineQueryBean bean = new OnlineQueryBean();
  				Object[] obj= (Object[])list.get(i);
  				bean.setId((String)obj[0]);
  				bean.setPayeeId((String)obj[1]);
  				bean.setPayeeAcctName((String)obj[2]);
  				bean.setPayeeAcctNo((String)obj[3]);
  				bean.setPayeeOpenBankNo((String)obj[4]);
  				bean.setPayeeOpenBankName((String)obj[5]);
  				bean.setPayeeTotalAmt((BigDecimal)obj[6]);
  				bean.setPayeeStatus((String)obj[7]);
  				bean.setOnlineCrdtNo((String)obj[8]);
  				bean.setCrdtId((String)obj[9]);
  				bean.setCreateTime((Date)obj[10]);
  				bean.setUpdateTime((Date)obj[11]);
  				bean.setPayeeUsedAmt((BigDecimal)obj[12]!= null?(BigDecimal)obj[12]:new BigDecimal(0));
  				bean.setPayeeFreeAmt(bean.getPayeeTotalAmt().subtract(bean.getPayeeUsedAmt()));
  				bean.setModeType((String)obj[13]);
  				result.add(bean);    
  			}
  			return result;
  		}
		return list;
	}
	
	
	/**
	 * @Title queryOnlineAcptPayeeListBean
	 * @author wss
	 * @date 2021-7-17
	 * @Description 查询收票人
	 * @return List
	 */
	public List queryOnlineAcptPayeeListBean(String onlineAcptNo, String flag) {
		StringBuffer hql = new StringBuffer();
		hql.append("select info.ID,info.PAYEE_ID,info.PAYEE_ACCT_NAME,info.PAYEE_ACCT_NO,info.PAYEE_OPEN_BANK_NO,info.PAYEE_OPEN_BANK_NAME,info.PAYEE_TOTAL_AMT,info.PAYEE_STATUS,info.ONLINE_ACPT_NO,info.ACPT_ID,info.CREATE_TIME,info.UPDATE_TIME,d.usedAmt " +
				"from PED_ONLINE_ACPT_INFO info left join PED_ONLINE_ACPT_PROTOCOL dto on info.ACPT_ID = dto.id left join (select sum(detail.BILL_AMT) usedAmt,PAYEE_ACCT,ONLINE_ACPT_NO from PL_ONLINE_ACPT_DETAIL detail where detail.STATUS not in(:status) group by PAYEE_ACCT,ONLINE_ACPT_NO) d on info.PAYEE_ACCT_NO = d.PAYEE_ACCT and d.ONLINE_ACPT_NO = info.ONLINE_ACPT_NO where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("status");
		List status = new ArrayList();
		status.add(PublicStaticDefineTab.ACPT_DETAIL_012);
		paramValue.add(status);
		if(StringUtils.isNotBlank(flag)){
			hql.append(" and info.PAYEE_STATUS =:PAYEE_STATUS");
			paramName.add("PAYEE_STATUS");
			paramValue.add(flag);
		}
		if(StringUtils.isNotBlank(onlineAcptNo)){
			hql.append(" and info.ONLINE_ACPT_NO =:ONLINE_ACPT_NO");
			paramName.add("ONLINE_ACPT_NO");
			paramValue.add(onlineAcptNo);
		}
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List list = sessionDao.SQLQuery(hql.toString(),paramNames,paramValues,null);
		if(list.size()>0){
			List result = new ArrayList();
			for(int i=0;i<list.size();i++){
				OnlineQueryBean bean = new OnlineQueryBean();
				Object[] obj= (Object[])list.get(i);
				bean.setId((String)obj[0]);
				bean.setPayeeId((String)obj[1]);
				bean.setPayeeAcctName((String)obj[2]);
				bean.setPayeeAcctNo((String)obj[3]);
				bean.setPayeeOpenBankNo((String)obj[4]);
				bean.setPayeeOpenBankName((String)obj[5]);
				bean.setPayeeTotalAmt((BigDecimal)obj[6]);
				bean.setPayeeStatus((String)obj[7]);
				bean.setOnlineAcptNo((String)obj[8]);
				bean.setAcptId((String)obj[9]);
				bean.setCreateTime((Date)obj[10]);
				bean.setUpdateTime((Date)obj[11]);
				bean.setPayeeUsedAmt((BigDecimal)obj[12] != null?(BigDecimal)obj[12]:new BigDecimal(0));
				result.add(bean);    
			}
			return result;
		}
		return list;
	}

	/**
	 * @description 校验收票人
	 * @author wss
	 * @date 2021-4-28
	 * @param protocol 收票人
	 */
	public List onlinPayeesCheck(OnlineQueryBean protocol,List errors) {
		List payees =protocol.getPayees();
		if(null != payees && payees.size()>0){
			for(int i=0;i<payees.size();i++){
				PedOnlineAcptInfo info = (PedOnlineAcptInfo) payees.get(i);
				
				if(null == info.getPayeeTotalAmt() 
						|| StringUtil.isBlank(info.getPayeeAcctName())
						|| StringUtil.isBlank(info.getModeType())
						|| StringUtil.isBlank(info.getPayeeOpenBankNo())
						|| StringUtil.isBlank(info.getPayeeId())
						){
					errors.add("收票人录入信息不完整，请检查收票人信息！");
					continue;
				}
				
				if(info.getPayeeTotalAmt().compareTo(new BigDecimal("0"))<0){
					errors.add(info.getPayeeAcctName()+"收票人收票总额金额不得为负|");
				}
				if(info.getPayeeTotalAmt().compareTo(new BigDecimal("100000000000"))>0){
					errors.add(info.getPayeeAcctName()+"收票人收票总额金额不得超过1000亿|");
				}
				BigDecimal acptAmt = this.getOnlineAcptPayeeAmt(protocol.getOnlineAcptNo(), info.getPayeeId());
				if(null != acptAmt && info.getPayeeTotalAmt().compareTo(acptAmt)<0){
					errors.add(info.getPayeeAcctName()+"收票人收票总额小于收票人已收票额度|");
				}
				if(PublicStaticDefineTab.MOD03.equals(info.getModeType())){
					OnlineQueryBean bean = new OnlineQueryBean();
					bean.setPayeeAcct(info.getPayeeAcctNo());
					List list = this.queryOnlineAcptPayeeList(bean, null);
					if(null != list && list.size()>0){
						PedOnlineAcptInfo old = (PedOnlineAcptInfo) list.get(0);
						if(old.getPayeeUsedAmt().compareTo(new BigDecimal(0))>0){
							errors.add(info.getPayeeAcctName()+"不得删除已发生收票的信息|");
						}
					}
				}
				if(!info.getPayeeAcctNo().matches("[0-9]+")){
					errors.add(info.getPayeeAcctName()+"收票人账号非数字|");
				}
				if(!info.getPayeeOpenBankNo().matches("[0-9]+") || 12 != (info.getPayeeOpenBankNo().length())){
					errors.add(info.getPayeeAcctName()+"收票人开户行行号非12位数字|");
				}
				if(PublicStaticDefineTab.MOD02.equals(info.getModeType())){
					OnlineQueryBean bean = new OnlineQueryBean();
					bean.setPayeeId(info.getPayeeId());
					List list = this.queryOnlineAcptPayeeList(bean, null);
					if(null != list && list.size()>0){
						PedOnlineAcptInfo old = (PedOnlineAcptInfo) list.get(0);
						if(old.getPayeeUsedAmt().compareTo(info.getPayeeTotalAmt())>0){
							errors.add(info.getPayeeAcctName()+"收票人收票总额小于收票人已收票额度|");
						}
					}
				}
			}
		}
		return errors;
	}
	
	/**
	 * @description 保存银承协议
	 * @author wss
	 * @date 2021-4-27
	 * @param queryBean 主协议
	 * @param payees 收票人
	 * @param mgs 短信通知人
	 * @throws Exception 
	 */
	public void txSaveOnlineAcptPtl(OnlineQueryBean queryBean) throws Exception {
		//在线银承/流贷协议的机构归属以信用风险管理系统发过来的【签约机构】归属确认。
		//在线银承/流贷协议的客户经理归属以在线银承/流贷协议融资功能开通时【经办人名称】归属确认
		PedOnlineAcptProtocol ptl = new PedOnlineAcptProtocol();
		PedOnlineAcptProtocolHist hist = new PedOnlineAcptProtocolHist();
		if(PublicStaticDefineTab.MOD02.equals(queryBean.getModeType())){ //修改
			
			/**
			 * 1、判断主协议的内容是否变动；根据新推的主协议信息与库存中的协议信息作比较
			 * 
			 */
			//保存修改信息
			ptl = this.queryOnlinAcptPtlByNo(queryBean.getOnlineAcptNo());
			//修改内容
			String modeContent = "";//主协议|收票人|短信|担保合同|签约信息
			//判断主协议是否是否变动
			modeContent = this.judgeProtocoIsMode(queryBean,ptl);
			/**
			 * 2、保存本次协议修改信息
			 */
			
			this.copyProperties(ptl, queryBean);
			ptl.setUpdateTime(new Date());
			this.txStore(ptl);
			/**
			 * 3、先查询本协议的历史最新的一条数据 
			 */
			List<PedOnlineAcptProtocolHist> oldList = this.queryPedOnlineAcptPtlHistList(queryBean,null);
			
			/**
			 * 4、保存本次修改的协议历史
			 */
			//保存历史
			BeanUtils.copyProperties(hist, ptl);
			hist.setId(null);
			hist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());
			hist.setCreateTime(new Date());
			
			if(null != oldList && oldList.size()>0){
				PedOnlineAcptProtocolHist old = oldList.get(0);
				hist.setLastSourceId(old.getId());
			}
			
			/**
			 * 5、收票人信息保存；是否变更：查询库存中的收票人信息，与推送过来的收票人信息做对比
			 * 		保若有修改查询上次历史收票人，并保存本次历史收票人的LastSourceId
			 */
			//收票人
			List payees = queryBean.getPayees();
			if(null != payees && payees.size()>0){
				PedOnlineAcptInfoHist acptHist = null;
				List acptHists = new ArrayList();
				boolean isPayee = true;//收票人是否改动
				//协议下的所有收票人
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setOnlineAcptNo(queryBean.getOnlineAcptNo());
				List list = this.queryOnlineAcptPayeeList(bean, null);//推送前协议下的收票人信息
				
//				PedOnlineAcptInfoHist old=new PedOnlineAcptInfoHist();
//				 old=(PedOnlineAcptInfoHist)oldLists.get(0);
//				this.txDeleteAll(list);
				List payList = new ArrayList();
				List payeeList = new ArrayList();
				for(int i=0;i<payees.size();i++){//推送过来的收票人集合
					acptHist = new PedOnlineAcptInfoHist();
					PedOnlineAcptInfo payee = (PedOnlineAcptInfo) payees.get(i);
                    if(PublicStaticDefineTab.MOD00.equals(payee.getModeType())){
						payee.setAcptId(ptl.getId());
						payee.setModeType(PublicStaticDefineTab.MOD00);
						isPayee = false;
					}else if(PublicStaticDefineTab.MOD01.equals(payee.getModeType())||PublicStaticDefineTab.MOD02.equals(payee.getModeType())){
						payee.setAcptId(ptl.getId());
						if(PublicStaticDefineTab.MOD01.equals(payee.getModeType())){
							payee.setModeType(PublicStaticDefineTab.MOD01);
						}else{
							payee.setModeType(PublicStaticDefineTab.MOD02);
							if(null!=list){
								for(int j=0;j<list.size();j++){
									PedOnlineAcptInfo info = (PedOnlineAcptInfo) list.get(j);
									if(info.getPayeeId().equals(payee.getPayeeId())){
										//查询上次收票人历史数据
										bean.setPayeeAcctName(payee.getPayeeAcctName());
										bean.setPayeeId(payee.getPayeeId());
										List oldLists = this.queryOnlinePayeeHistList(bean);//本条修改数据的上一次历史
										if(oldLists != null && oldLists.size() >0){
											PedOnlineAcptInfoHist old=(PedOnlineAcptInfoHist)oldLists.get(0);
											acptHist.setLastSourceId(old.getId());
										}
									}
								}
							}
						}
						BeanUtils.copyProperties(acptHist, payee);
					}else if(PublicStaticDefineTab.MOD03.equals(payee.getModeType())){
						payee.setModeType(PublicStaticDefineTab.MOD03);
						payee.setAcptId(ptl.getId());
						payee.setPayeeStatus(PublicStaticDefineTab.STATUS_0);
						BeanUtils.copyProperties(acptHist, payee);
					}
					acptHist.setId(null);
					acptHist.setCreateTime(new Date());
					acptHist.setModeMark(hist.getModeMark());
					if(!PublicStaticDefineTab.MOD00.equals(payee.getModeType())){
						acptHists.add(acptHist);
					}
					payee.setId(null);
					payee.setCreateTime(new Date());
					payee.setUpdateTime(new Date());
					payee.setOnlineAcptNo(ptl.getOnlineAcptNo());
					if(!PublicStaticDefineTab.MOD03.equals(payee.getModeType())){
						payeeList.add(payee);
					}
				}
				this.txDeleteAll(list);
				this.txStoreAll(acptHists);
				this.txStoreAll(payeeList);
				if(isPayee){
					modeContent = modeContent+"收票人|";
				}
			}
			
			/**
			 * 6、短信通知人信息保存；是否变更：查询库存中的短信通知人信息，与推送过来的短信通知人信息做对比
			 * 		保若有修改查询上次历史短信通知人，并保存本次历史短信通知人的LastSourceId
			 */
			//短信通知人
			List details = queryBean.getDetalis();
			if(null != details && details.size()>0){
				PedOnlineMsgInfoHist msgHist = null;
				List msgList = new ArrayList();
				List hisList = new ArrayList();
				boolean isMsg = true;//短信通知人是否改动
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setOnlineNo(queryBean.getOnlineAcptNo());
				bean.setAddresseeRole(PublicStaticDefineTab.ROLE_0);
//				List list = onlineManageService.queryOnlineMsgInfoList(queryBean.getOnlineAcptNo(), PublicStaticDefineTab.ROLE_0);
				List list = onlineManageService.queryOnlineMsgInfoList(bean,null);
				
				//PedOnlineMsgInfoHist old=new PedOnlineMsgInfoHist();
				 //old=(PedOnlineMsgInfoHist)lastList.get(0);
				
				for(int i=0;i<details.size();i++){
					msgHist = new PedOnlineMsgInfoHist();
					PedOnlineMsgInfo msg = (PedOnlineMsgInfo) details.get(i);
					if(PublicStaticDefineTab.MOD00.equals(msg.getModeType())) {
						msg.setOnlineProtocolId(ptl.getId());
						msg.setModeType(PublicStaticDefineTab.MOD00);
						isMsg = false;
					}else if(PublicStaticDefineTab.MOD01.equals(msg.getModeType())||PublicStaticDefineTab.MOD02.equals(msg.getModeType())) {
						msg.setOnlineProtocolId(ptl.getId());
						if(PublicStaticDefineTab.MOD01.equals(msg.getModeType())){
							//新增情况下通知短信至联系人
							String Template = "您已签约"+ptl.getCustName()+"在汉口银行办理在线银承的短信通知功能。";
							onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, msg.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,msg.getAddresseeName(),msg.getOnlineNo());
							msg.setModeType(PublicStaticDefineTab.MOD01);
						}else{
							//修改情况下通知短信至联系人
							msg.setModeType(PublicStaticDefineTab.MOD02);
							if(null!=list){
								for(int j=0;j<list.size();j++){
									PedOnlineMsgInfo info=(PedOnlineMsgInfo)list.get(j);
									if(info.getAddresseeNo().equals(msg.getAddresseeNo())){
										
										//比较电话是否变更  若变更则发短信通知联系人
										if(!info.getAddresseePhoneNo().equals(msg.getAddresseePhoneNo())){
											String Template = "您已签约"+ptl.getCustName()+"在汉口银行办理在线银承的短信通知功能。";
											onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, msg.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,msg.getAddresseeName(),msg.getOnlineNo());
											String Temp = "您已取消"+ptl.getCustName()+"在汉口银行办理在线银承的短信通知功能。";
											onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, info.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Temp,msg.getAddresseeName(),msg.getOnlineNo());
										}
										
									}
								}
							}
							bean.setAddresseeNo(msg.getAddresseeNo());
							List lastList = onlineManageService.queryOnlineMsgHist(bean,null);
							if(lastList != null && lastList.size() > 0){
								PedOnlineMsgInfoHist old = (PedOnlineMsgInfoHist) lastList.get(0);
								//查询上次短信通知人历史数据
								msgHist.setLastSourceId(old.getId());
							}
							
						}
						BeanUtils.copyProperties(msgHist, msg);
						msgHist.setModeMark(hist.getModeMark());
					}else if(PublicStaticDefineTab.MOD03.equals(msg.getModeType())) {
						String Template = "您已取消"+ptl.getCustName()+"在汉口银行办理在线银承的短信通知功能。";
						onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, msg.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,msg.getAddresseeName(),msg.getOnlineNo());
					
						msg.setOnlineProtocolId(ptl.getId());
						msg.setModeType(PublicStaticDefineTab.MOD03);
						BeanUtils.copyProperties(msgHist, msg);
						msgHist.setModeMark(hist.getModeMark());
					}
					msgHist.setId(null);
					msgHist.setCreateTime(new Date());
					msgHist.setModeMark(hist.getModeMark());
					msgHist.setOnlineNo(ptl.getOnlineAcptNo());
					if(!PublicStaticDefineTab.MOD00.equals(msg.getModeType())){
						hisList.add(msgHist);
					}
					msg.setId(null);
					msg.setCreateTime(new Date());
					msg.setUpdateTime(new Date());
					msg.setOnlineNo(ptl.getOnlineAcptNo());
					if(!PublicStaticDefineTab.MOD03.equals(msg.getModeType())){
						msgList.add(msg);
					}
				}
				if(isMsg){
					modeContent = modeContent+"短信通知人";
				}
				this.txDeleteAll(list);
				this.txStoreAll(msgList);
				this.txStoreAll(hisList);

			}
			hist.setModeContent(modeContent);//修改内容  
			this.txStore(hist);
			
		}else{//新增
			//查询前六个月内是否有同类型的在线协议;若有则将原协议下的短信联系人迁移至当前协议下
			OnlineQueryBean query = new OnlineQueryBean();
			query.setCustNumber(queryBean.getCustNumber());
			query.setBpsNo(queryBean.getBpsNo());
			ProtocolQueryBean querys =new ProtocolQueryBean();
			querys.setCustnumber(queryBean.getCustNumber());
			querys.setPoolAgreement(queryBean.getBpsNo());
			PedProtocolDto proto=pedProtocolService.queryProtocolDtoByQueryBean(querys);
			BeanUtils.copyProperties(ptl, queryBean);
			if(null!=proto){
				ptl.setBpsId(proto.getPoolInfoId());
				ptl.setBpsName(proto.getPoolName());
			}
			ptl.setUpdateTime(new Date());
			ptl.setCreateTime(new Date());
			this.txStore(ptl);
			//保存历史
			BeanUtils.copyProperties(hist, ptl);
			hist.setId(null);
			hist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());//修改标志
			hist.setCreateTime(new Date());
			this.txStore(hist);
			//票据池客户信息数据落库
			CustomerRegister customer=new CustomerRegister();
			customer.setCustNo(queryBean.getCustNumber());
			customer.setCustName(queryBean.getCustName());
			customer.setFirstDateSource("PJE014");
			customerService.txSaveCustomerRegister(customer);	 
			if(null != queryBean.getPayees() && queryBean.getPayees().size()>0){
				List payList = new ArrayList();
				PedOnlineAcptInfoHist payHist = null;
				for(int i=0;i<queryBean.getPayees().size();i++){
					PedOnlineAcptInfo info = (PedOnlineAcptInfo) queryBean.getPayees().get(i);
					info.setAcptId(ptl.getId());
					info.setOnlineAcptNo(ptl.getOnlineAcptNo());
					payHist =  new PedOnlineAcptInfoHist();
					BeanUtils.copyProperties(payHist, info);
					payHist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());
					payList.add(payHist);
				}
				this.txStoreAll(queryBean.getPayees());
				this.txStoreAll(payList);
			}
			OnlineQueryBean acptBean =new OnlineQueryBean();
			acptBean.setProtocolStatus("0");
			//acptBean.setBpsNo(queryBean.getBpsNo());
			acptBean.setCustNumber(queryBean.getCustNumber());
			acptBean.setCustName(queryBean.getCustName());
			PedOnlineAcptProtocol acpt=this.queryOnlineAcptProtocol(acptBean);
			if(null!=acpt){
				if(DateUtils.calRangePcds(acpt.getChangeDate(), DateUtils.getCurrDate())){
					//原始失效的协议日期与当前日期在六个月之内需要迁移短信联系人
					List<PedOnlineMsgInfo> newList = new ArrayList<PedOnlineMsgInfo>();
					List<PedOnlineMsgInfoHist> histList = new ArrayList<PedOnlineMsgInfoHist>();
					List msgList = onlineManageService.queryOnlineMsgInfoList(acpt.getOnlineAcptNo(), PublicStaticDefineTab.ROLE_1);
					for (int i = 0; i < msgList.size(); i++) {
						PedOnlineMsgInfo newInfo = new PedOnlineMsgInfo();
						PedOnlineMsgInfoHist msgHist = new PedOnlineMsgInfoHist();
						PedOnlineMsgInfo old = (PedOnlineMsgInfo) msgList.get(i);
						newInfo.setOnlineNo(ptl.getOnlineAcptNo());//在线协议编号
						newInfo.setOnlineProtocolId(ptl.getId());
						newInfo.setAddresseeNo(old.getAddresseeNo());
    					newInfo.setAddresseeName(old.getAddresseeName());
    					newInfo.setAddresseeRole(old.getAddresseeRole());
    					newInfo.setAddresseePhoneNo(old.getAddresseePhoneNo());
    					newInfo.setOnlineProtocolType(old.getOnlineProtocolType());
    					newInfo.setUpdateTime(new Date());
						//生成短信历史
						BeanUtils.copyProperties(msgHist, newInfo);
						msgHist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());
    					msgHist.setUpdateTime(new Date());
    					newList.add(newInfo);
    					histList.add(msgHist);
					}
					 if(null!=newList && null!=histList){
		    		    	financialAdviceService.txCreateList(newList);
		    		    	financialAdviceService.txCreateList(histList);
		    		    }
				}
			}
			
			if(null != queryBean.getDetalis() && queryBean.getDetalis().size()>0){
				List msgList = new ArrayList();
				PedOnlineMsgInfoHist msgHist = null;
				for(int i=0;i<queryBean.getDetalis().size();i++){
					PedOnlineMsgInfo info = (PedOnlineMsgInfo) queryBean.getDetalis().get(i);
					//新增员工信息通知联系人
					String Template = "您已签约"+ptl.getCustName()+"在汉口银行办理在线银承的短信通知功能。";
					onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, info.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,info.getAddresseeName(),info.getOnlineNo());
				
					info.setOnlineProtocolId(ptl.getId());
					info.setOnlineNo(ptl.getOnlineAcptNo());
					msgHist = new PedOnlineMsgInfoHist();
					BeanUtils.copyProperties(msgHist, info);
					msgHist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());
					msgList.add(msgHist);
				}
				this.txStoreAll(queryBean.getDetalis());
				this.txStoreAll(msgList);
			}
		}
	}
	
	private void copyMsgInfo(PedOnlineMsgInfo old, PedOnlineMsgInfo msg) {
		old.setAddresseeNo(msg.getAddresseeNo());
		old.setAddresseeName(msg.getAddresseeName());
		old.setAddresseePhoneNo(msg.getAddresseePhoneNo());
		old.setModeType(msg.getModeType());
	}

	private void copyPayees(PedOnlineAcptInfo old, PedOnlineAcptInfo payee) {
		old.setPayeeId(payee.getPayeeId());
		old.setPayeeAcctName(payee.getPayeeAcctName());
		old.setPayeeAcctNo(payee.getPayeeAcctNo());
		old.setPayeeOpenBankNo(payee.getPayeeOpenBankNo());
		old.setPayeeOpenBankName(payee.getPayeeOpenBankName());
		old.setPayeeTotalAmt(payee.getPayeeTotalAmt());
		old.setModeType(payee.getModeType());
	}

	/**
	 * @Title copyProperties
	 * @author wss
	 * @date 2021-6-2
	 * @Description 赋值修改字段
	 * @return void
	 */
	private void copyProperties(PedOnlineAcptProtocol ptl,OnlineQueryBean bean) {
		ptl.setProtocolStatus(bean.getProtocolStatus());
		ptl.setBpsNo(bean.getBpsNo());
		ptl.setBpsId(bean.getBpsId());
		ptl.setBpsName(bean.getBpsName());
		ptl.setBaseCreditNo(bean.getBaseCreditNo());
		ptl.setEbkCustNo(bean.getEbkCustNo());
		ptl.setOnlineAcptTotal(bean.getOnlineAcptTotal());
		ptl.setAcceptorBankNo(bean.getAcceptorBankNo());
		ptl.setAcceptorBankName(bean.getAcceptorBankName());
		ptl.setDepositAcctNo(bean.getDepositAcctNo());
		ptl.setDepositAcctName(bean.getDepositAcctName());
		ptl.setDepositRateLevel(bean.getDepositRateLevel());
		ptl.setDepositRateFloatFlag(bean.getDepositRateFloatFlag());
		ptl.setDepositRateFloatValue(bean.getDepositRateFloatValue());
		ptl.setPoolCreditRatio(bean.getPoolCreditRatio());
		ptl.setInAcctBranchNo(bean.getInAcctBranchNo());
		ptl.setInAcctBranchName(bean.getInAcctBranchName());
		
		ptl.setDepositRatio(bean.getDepositRatio());
		ptl.setFeeRate(bean.getFeeRate());
		ptl.setSignBranchNo(bean.getSignBranchNo());
		ptl.setSignBranchName(bean.getSignBranchName());
		ptl.setDueDate(DateUtils.formatDate(bean.getDueDate(), DateUtils.ORA_DATES_FORMAT));
		ptl.setChangeDate(DateUtils.formatDate(bean.getChangeDate(), DateUtils.ORA_DATES_FORMAT));
		ptl.setContractNo(bean.getContractNo());
	}

	public List queryPedOnlineAcptPtlHistList(OnlineQueryBean queryBean,Page page) {
		String sql ="select dto from PedOnlineAcptProtocolHist dto where 1=1 ";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
				sql= sql+" and dto.onlineAcptNo=?";
				param.add(queryBean.getOnlineAcptNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				sql= sql+" and dto.payeeStatus=?";
				param.add(queryBean.getPayeeStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and dto.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
				sql= sql+" and dto.payeeAcctName like ?";
				param.add("%"+queryBean.getPayeeAcctName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctNo())){
				sql= sql+" and dto.payeeAcctNo =?";
				param.add(queryBean.getPayeeAcctNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankNo())){
				sql= sql+" and dto.payeeOpenBankNo =?";
				param.add(queryBean.getPayeeOpenBankNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankName())){
				sql= sql+" and dto.payeeOpenBankName like ?";
				param.add("%"+queryBean.getPayeeOpenBankName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getLastSourceId())){
				sql= sql+" and dto.lastSourceId =?";
				param.add(queryBean.getLastSourceId());
			}
			sql= sql+" order by dto.createTime  desc";

		}
		List result = this.find(sql, param, page);
		return result;
	}

	private String judgeProtocoIsMode(OnlineQueryBean queryBean, PedOnlineAcptProtocol ptl) {
		String str = "";
		//主协议
		if(StringUtil.isEmpty(queryBean.getProtocolStatus()) && !queryBean.getProtocolStatus().equals(ptl.getProtocolStatus())){//在线协议状态
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getOnlineAcptNo()) && !queryBean.getOnlineAcptNo().equals(ptl.getOnlineAcptNo())){//票据池编号
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getBaseCreditNo()) && !queryBean.getBaseCreditNo().equals(ptl.getBaseCreditNo())){//基本授信额度编号
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getEbkCustNo()) && !queryBean.getEbkCustNo().equals(ptl.getEbkCustNo())){//网银客户号
			str = "主协议|";
		}else if(queryBean.getOnlineAcptTotal() != null && !queryBean.getOnlineAcptTotal().equals(ptl.getOnlineAcptTotal())){//在线银承总额
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getAcceptorBankNo()) && !queryBean.getAcceptorBankNo().equals(ptl.getAcceptorBankNo())){//承兑人承兑行行号
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getAcceptorBankName()) && !queryBean.getAcceptorBankName().equals(ptl.getAcceptorBankName())){//承兑人承兑行名称
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getDepositAcctNo()) && !queryBean.getDepositAcctNo().equals(ptl.getDepositAcctNo())){//扣收账号
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getDepositAcctName()) && !queryBean.getDepositAcctName().equals(ptl.getDepositAcctName())){//扣收账户名称
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getDepositRateLevel()) && !queryBean.getDepositRateLevel().equals(ptl.getDepositRateLevel())){//保证金利率档次
			str = "主协议|";
		}else if(StringUtil.isEmpty(queryBean.getDepositRateFloatFlag()) && !queryBean.getDepositRateFloatFlag().equals(ptl.getDepositRateFloatFlag())){//保证金利率浮动标志
			str = "主协议|";
		}else if(queryBean.getDepositRateFloatValue() != null && !queryBean.getDepositRateFloatValue().equals(ptl.getDepositRateFloatValue())){//保证金利率浮动值
			str = "主协议|";
		}else if(queryBean.getPoolCreditRatio() != null && !queryBean.getPoolCreditRatio().equals(ptl.getPoolCreditRatio())){//票据池额度占用比例（%）
			str = "主协议|";
		}else if(queryBean.getDepositRatio() != null && !queryBean.getDepositRatio().equals(ptl.getDepositRatio())){//保证金比例（%）
			str = "主协议|";
		}else if(queryBean.getFeeRate() != null && !queryBean.getFeeRate().equals(ptl.getFeeRate())){//手续费率（%）
			str = "主协议|";
		}else if(!queryBean.getInAcctBranchNo().equals(ptl.getInAcctBranchNo())){//入账机构所号
			str = "主协议|";
		}else if(!queryBean.getInAcctBranchName().equals(ptl.getInAcctBranchName())){//入账机构名称
			str = "主协议|";
		}
		//担保合同
		if(!queryBean.getContractNo().equals(ptl.getContractNo())){//担保合同编号
			str = str+"担保合同|";
		}
		//签约信息
		if(!queryBean.getAppName().equals(ptl.getAppName())){//经办人名称 
			logger.info("报文发送："+queryBean.getAppName()+"，原数据："+ptl.getAppName()+"经办人名称  不同");
			logger.info("经办人名称 不同");
			str = str+"签约信息|";
		}else if(!queryBean.getAppNo().equals(ptl.getAppNo())){//经办人编号
			logger.info("报文发送："+queryBean.getAppNo()+"，原数据："+ptl.getAppNo()+"经办人编号 不同");
			logger.info("经办人编号 不同");
			str = str+"签约信息|";
		}else if(!queryBean.getSignBranchNo().equals(ptl.getSignBranchNo())){//签约机构所号
			logger.info("报文发送："+queryBean.getSignBranchNo()+"，原数据："+ptl.getSignBranchNo()+"签约机构所号 不同");
			logger.info("签约机构所号 不同");
			str = str+"签约信息|";
		}else if(!queryBean.getSignBranchName().equals(ptl.getSignBranchName())){//签约机构名称
			logger.info("报文发送："+queryBean.getSignBranchName()+"，原数据："+ptl.getSignBranchName()+"签约机构名称  不同");
			logger.info("签约机构名称 不同");
			str = str+"签约信息|";
		}else if(!queryBean.getDueDate().equals(ptl.getDueDate())){//到期日期
			logger.info("报文发送："+queryBean.getDueDate()+"，原数据："+ptl.getDueDate()+"经办人名称 不同");
			logger.info("到期日期 不同");
			str = str+"签约信息|";
		}
		logger.info("--------------------------");
		logger.info(str);
		return str;
	}

	/**
	 * @description 查询在线银承协议信息
	 * @author wss
	 * @date 2021-4-29
	 * @param queryBean 
	 * @return List
	 */
	public List queryOnlineAcptPtlByQueryBean(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineAcptProtocol as dto where 1=1 ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and dto.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			hql.append(" and dto.custName like :custName");
			paramName.add("custName");
			paramValue.add("%"+queryBean.getCustName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			hql.append(" and dto.custOrgcode =:custOrgcode");
			paramName.add("custOrgcode");
			paramValue.add(queryBean.getCustOrgcode());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and dto.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and dto.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and dto.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getAppNo())){
			hql.append(" and dto.appNo =:appNo");
			paramName.add("appNo");
			paramValue.add(queryBean.getAppNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and dto.appName like:appName");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
			hql.append(" and dto.signBranchNo =:signBranchNo");
			paramName.add("signBranchNo");
			paramValue.add(queryBean.getSignBranchNo());
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and dto.signBranchName like :signBranchName");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and dto.dueDate >=:startDate");
			paramName.add("startDate");
			paramValue.add(queryBean.getStartDate());
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and dto.dueDate <=:endDate");
			paramName.add("endDate");
			paramValue.add(queryBean.getEndDate());
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		hql.append(" order by dto.updateTime desc");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineAcptProtocol> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}

	/**
	 * 保存银承信息
	 * 
	 */
	public OnlineQueryBean createOnlineAcpt(OnlineQueryBean queryBean) throws Exception {
		
		OnlineQueryBean returnBean = queryBean;
		
		PedOnlineAcptProtocol protocol = this.queryOnlinAcptPtlByNo(queryBean.getOnlineAcptNo());
		PlOnlineAcptBatch batch = new PlOnlineAcptBatch();
		BeanUtils.copyProperties(batch, queryBean);
		PedProtocolDto pool = null;
		
		boolean isAllRatio = false;//是否100%保证金，true-是 false-否
		if(new BigDecimal(100).compareTo(protocol.getDepositRatio())==0){
			isAllRatio = true;//百分之百保证金
		}
		
		if(!isAllRatio){//非100%保证金			
			pool = (PedProtocolDto) this.dao.load(PedProtocolDto.class, protocol.getBpsId());
		}
		
		//生成批次号
		String batchNo = Long.toString(System.currentTimeMillis());
		batch.setBatchNo("YC"+batchNo);
		batch.setBranchNo("");//机构
		
		//序列号生成
		CustomerRegister cust = pedProtocolService.queryCustomerRegisterByCustNo(protocol.getCustNumber());
		String serialNo = pedProtocolService.txCreateOnlineProductNo(cust.getId(), 5);
		
		String contractNo =queryBean.getOnlineAcptNo()+ serialNo;		
		batch.setContractNo(contractNo);//合同号
		batch.setBpsNo(protocol.getBpsNo());//票据池编号
		batch.setOnlineAcptNo(protocol.getOnlineAcptNo());//在线银承协议号
		batch.setFeeRate(protocol.getFeeRate());//手续费率
		batch.setCreateTime(new Date());//创建日期
		batch.setApplyDate(new Date());//申请日期
		batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_001);//新增
		batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_001);
		batch.setElctrncSign(queryBean.getElctrncSign());
		batch.setCustNo(protocol.getCustNumber());//核心客户号
		batch.setApplyAcct(protocol.getDepositAcctNo());
		batch.setApplyName(protocol.getCustName());
		//TODO 网银上送
		batch.setPoolCreditRatio(protocol.getPoolCreditRatio());
		batch.setDepositRatio(protocol.getDepositRatio());
		batch.setDepositRateFloatFlag(protocol.getDepositRateFloatFlag());
		batch.setDepositRateFloatValue(protocol.getDepositRateFloatValue());
		batch.setInAcctBranchNo(protocol.getInAcctBranchNo());
		batch.setInAcctBranchName(protocol.getInAcctBranchName());
		batch.setManageerNo(protocol.getAppNo());
		batch.setTotalAmt(queryBean.getTotalAmt());
		batch.setApplyOrgcode(protocol.getCustOrgcode());
		batch.setManageerName(protocol.getAppName());
		batch.setManageerNo(protocol.getAppNo());
		batch.setUpdateTime(new Date());
		batch.setTaskDate(new Date());
		batch.setOriContractAmt(queryBean.getTotalAmt());

		
		
		List details = queryBean.getDetalis();
		
		List<PlOnlineAcptDetail> acptDetailList = new ArrayList<PlOnlineAcptDetail>();//银承明细表
		List<PedCreditDetail> crdtDetailList = new ArrayList<PedCreditDetail>();//额度明细表
		for(int i=0;i<details.size();i++){
			details.get(i);
			PlOnlineAcptDetail detail = (PlOnlineAcptDetail) details.get(i);
			detail.setOnlineAcptNo(protocol.getOnlineAcptNo());
			detail.setBpsNo(protocol.getBpsNo());
			detail.setCreateTime(new Date());
			detail.setIsseDate(DateUtils.getCurrDate());
			detail.setAcptBatchId(batch.getId());
			detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_001);
			detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_001);
			detail.setIssuerName(batch.getApplyName());
			detail.setIssuerAcct(batch.getApplyAcct());
			detail.setIssuerBankCode(batch.getApplyBankNo());
			detail.setIssuerBankName(batch.getApplyBankName());
			detail.setIssuerOrgcode(protocol.getCustOrgcode());
			detail.setAcptBankCode(protocol.getAcceptorBankNo());
			detail.setAcptBankName(protocol.getAcceptorBankName());
			detail.setUpdateTime(new Date());
			detail.setTaskDate(new Date());
			acptDetailList.add(detail);
			
			if(!isAllRatio){//非100%保证金					
				//创建借据对象
				PedCreditDetail crdtDetail = this.creatCrdtDetailByAcptDetail(detail, batch, pool);
				crdtDetailList.add(crdtDetail);
			}
			
		}
		
		returnBean.setAcptBatch(batch);//返回在线银承批次信息
		returnBean.setList(acptDetailList);//返回在线银承明细信息
		returnBean.setAcptPro(protocol);
		
		if(!isAllRatio){//非100%保证金		
			
			//创建主业务合同表对象
			CreditProduct product = this.creatProductByAcptBatch(batch, pool);
			returnBean.setProduct(product);//返回主业务合同对象
			returnBean.setCrdtDetailList(crdtDetailList);//返回借据列表
			returnBean.setPool(pool);
		}
		
		return returnBean;
	}

	/**
	 * 查询承兑明细
	 */
	public List queryOnlinAcptDetails(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PlOnlineAcptDetail dto,PlOnlineAcptBatch batch,PedOnlineAcptProtocol pro where dto.acptBatchId=batch.id and batch.onlineAcptNo=pro.onlineAcptNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			hql.append(" and pro.custName like :custName");
			paramName.add("custName");
			paramValue.add("%"+queryBean.getCustName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			hql.append(" and pro.custOrgcode =:custOrgcode");
			paramName.add("custOrgcode");
			paramValue.add(queryBean.getCustOrgcode());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and pro.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and pro.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and pro.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getAppNo())){
			hql.append(" and pro.appNo =:appNo");
			paramName.add("appNo");
			paramValue.add(queryBean.getAppNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and pro.appName like :appName");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
			hql.append(" and pro.signBranchNo =:signBranchNo");
			paramName.add("signBranchNo");
			paramValue.add(queryBean.getSignBranchNo());
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and pro.signBranchName like :signBranchName");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and pro.dueDate >=:startDate");
			paramName.add("startDate");
			paramValue.add(queryBean.getStartDate());
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and pro.dueDate <=:endDate");
			paramName.add("endDate");
			paramValue.add(queryBean.getEndDate());
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and batch.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getLoanNo())){
			hql.append(" and dto.loanNo =:loanNo");
			paramName.add("loanNo");
			paramValue.add(queryBean.getLoanNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAcptBatchId())){
			hql.append(" and dto.acptBatchId =:acptBatchId");
			paramName.add("acptBatchId");
			paramValue.add(queryBean.getAcptBatchId());
		}
		if(StringUtils.isNotBlank(queryBean.getBillId())){
			hql.append(" and dto.billId =:billId");
			paramName.add("billId");
			paramValue.add(queryBean.getBillId());
		}
		if(StringUtils.isNotBlank(queryBean.getStatus())){
			hql.append(" and dto.status =:status");
			paramName.add("status");
			paramValue.add(queryBean.getStatus());
		}else if(null != queryBean.getStatuList() && queryBean.getStatuList().size()>0){
			hql.append(" and dto.status in(:status)");
			paramName.add("status");
			paramValue.add(queryBean.getStatuList());
		}
		if(StringUtils.isNotBlank(queryBean.getMsgStatus())){
			hql.append(" and dto.status not in(:status)");
			paramName.add("status");
			paramValue.add(queryBean.getMsgStatus());
		}
		if(null !=queryBean.getTaskDateStart()){
			hql.append(" and dto.taskDate >=:taskDateStart");
			paramName.add("taskDateStart");
			paramValue.add(queryBean.getTaskDateStart());
		}
		if(null !=queryBean.getTaskDateEnd()){
			hql.append(" and dto.taskDate <=:taskDateEnd");
			paramName.add("taskDateEnd");
			paramValue.add(queryBean.getTaskDateEnd());
		}
		hql.append(" order by dto.updateTime desc");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineAcptProtocol> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}

	/**
	 * 查询承兑明细
	 */
	public List queryOnlinAcptByStatus(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto.dueDate from PlOnlineAcptDetail dto,PlOnlineAcptBatch batch,PedOnlineAcptProtocol pro where dto.acptBatchId=batch.id and batch.onlineAcptNo=pro.onlineAcptNo ");
		
		if(StringUtils.isNotBlank(queryBean.getStatus())){
			hql.append(" and dto.status =:status");
			paramName.add("status");
			paramValue.add(queryBean.getStatus());
		}
		if(null != queryBean.getStatuList() && queryBean.getStatuList().size()>0){
			hql.append(" and dto.status in(:status)");
			paramName.add("status");
			paramValue.add(queryBean.getStatuList());
		}
		if(StringUtils.isNotBlank(queryBean.getAcptBatchId())){
			hql.append(" and dto.acptBatchId =:acptBatchId");
			paramName.add("acptBatchId");
			paramValue.add(queryBean.getAcptBatchId());
		}
		
		hql.append(" group by dto.dueDate order by dto.dueDate desc");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}
	
	/**
	 * @author wss
	 * @date 2021-5-10
	 * @description 新增票据
	 * @param batch
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyNewBills(PlOnlineAcptBatch batch) throws Exception {
		List list = this.queryOnlineAcptDetailByBatchId(batch.getId());
		ECDSPoolTransNotes  note = new ECDSPoolTransNotes();
		note.setDetails(list);
		note.setContractNo(batch.getContractNo());
		note.setAcctNo(batch.getApplyAcct());//申请人账号
		ReturnMessageNew response = poolEcdsService.txApplyNewBills(note);
		String error = "";
		if(response.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			logger.info(batch.getBatchNo()+"电票批量新增推送成功！");
			List result = response.getDetails();
			if(null != result && result.size()>0){
				for(int i=0;i<result.size();i++){
					Map map = (Map) result.get(i);
					String code = (String) map.get("TRAN_RESULT_ARRAY.TRAN_RET_CODE");
					if(code.equals(Constants.TX_SUCCESS_CODE)){
						String loanNo = (String) map.get("TRAN_RESULT_ARRAY.TRAN_SEQ_NO");//借据号
						String billId = (String) map.get("TRAN_RESULT_ARRAY.BILL_ID");//票据id
						String holdId = (String) map.get("TRAN_RESULT_ARRAY.HOLD_BILL_ID");//持票id
						if(null==billId || StringUtils.isBlank(billId) || "null".equals(billId)){
							logger.info("借据号：【"+map.get("TRAN_RESULT_ARRAY.TRAN_SEQ_NO")+"】批量新增失败，报错原因：未返回票据id");
							error = error + (String) map.get("TRAN_RESULT_ARRAY.TRAN_SEQ_NO") + ",";
							
							continue;
//							response.getRet().setRET_CODE(Constants.TX_FAIL_CODE);
//							response.getRet().setRET_MSG("批量新增推送失败！");
//							return response;
						}
						for(int j=0;j<list.size();j++){
							PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(j);
							logger.info("借据号"+loanNo);
							logger.info("ming借据号"+detail.getLoanNo());
							if(loanNo.equals(detail.getLoanNo())){
								detail.setBillId(billId);
								detail.setHilrId(holdId);
								this.dao.store(detail);
							}
						}
					}else{
//						response.getRet().setRET_CODE(Constants.TX_FAIL_CODE);
//						response.getRet().setRET_MSG("批量新增推送失败！");
//						return response;
						logger.info("借据号：【"+map.get("TRAN_RESULT_ARRAY.TRAN_SEQ_NO")+"】批量新增失败，报错原因：【"+map.get("TRAN_RESULT_ARRAY.TRAN_RET_MSG")+"】");
						error = error + (String) map.get("TRAN_RESULT_ARRAY.TRAN_SEQ_NO") + ",";
					}
					
				}
			}
			if(error.length() > 0){
				error.substring(0, error.length()-2);
				error = "批量新增失败的借据包含【"+error+"】";
			}
		}else{
			response.getRet().setRET_CODE(Constants.TX_FAIL_CODE);
			response.getRet().setRET_MSG("批量新增推送失败！");
			return response;
		}
		return response;
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 出票登记
	 */
	public boolean txApplyDrawBill(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch) throws Exception {
		logger.info("出票登记自动任务:出票登记");
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
		
		
		/**
		 * body内需要传送的值
		 */
		note.setApplicantAcctNo(detail.getIssuerAcct());//电票签约帐号  多账号|拼接
		note.setSignature(batch.getElctrncSign());//电子签名
		
		/**
		 * 票据信息数组需传送的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",detail.getDraftSource());//票据来源 
		if(detail.getDraftSource().equals(PoolComm.CS01)){
			infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","2");//操作类型 票据来源为CS01-ECDS 可输，2-出票登记
			infoMap.put("BILL_INFO_ARRAY.AUTO_PROMPT_RECV_FLAG","0");//自动提示收票标志  为CS01-ECDS 可输 1：是；0：否；
			infoMap.put("BILL_INFO_ARRAY.AUTO_PROMPT_ACCEPTOR_FLAG","AT00");//自动提示承兑标志 票据来源为CS01-ECDS 可输 1：是；0：否；
		}
		infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0012000);//交易编号  出票登记
		infoMap.put("BILL_INFO_ARRAY.BILL_NO",detail.getBillNo());//票据（包）号码
		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",detail.getHilrId());//持票id
		infoMap.put("BILL_INFO_ARRAY.BILL_ID",detail.getBillId());//票据id
		infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO",detail.getLoanNo());//流水号

		infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
		infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","1");//经办锁类型 0-未经办锁票 1-已经办锁票
		note.getDetails().add(infoMap);
		
		
		ReturnMessageNew response = poolEcdsService.txApplyImplawn(note);
		//区分通讯异常 抛出去   还是业务失败返回错误
		String responseCode = response.getRet().getRET_CODE();
        if(Constants.TX_SUCCESS_CODE.equals(responseCode)){
        	return true;
        }else{
        	return false;
        }
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-10
	 * @description 验证核心承兑记账
	 * @return String 0-未记账 1-已记账 2-已冲销
	 */
	public String txApplyQueryAcctStatus(PlOnlineAcptDetail detail,String ConrtNo) throws Exception {
		logger.info("在线银承出票自动任务:记账查询");
		ReturnMessageNew response = new ReturnMessageNew();
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
		note.setTransNo("1");//承兑
		note.setBillId(detail.getBillId());//票据id
		if(detail.getDraftSource().equals(PoolComm.CS02) && detail.getSplitFlag().equals("1")){
			note.setBeginRangeNo(detail.getBeginRangeNo());//子票区间起
			note.setEndRangeNo(detail.getEndRangeNo());//
		}
		note.setBillSource(detail.getDraftSource());
		note.setConferNo(ConrtNo);
		
		response = poolEcdsService.txApplySynchronization(note);
		List rsult = response.getDetails();
		if(rsult != null && rsult.size() > 0){
			Map map = (Map) rsult.get(0);
			String acctFlag = (String) map.get("APPROVE_INFO_ARRAY.ACCOUNT_STATUS");//记账状态
			return acctFlag;
		}else{
			return "9";//出账申请失败不需要再做出账撤回
		}
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-11
	 * @description 查询bbsp票据信息
	 * @param  transType 1申请 2签收
	 * @param  transCode 交易码 NES.001.20.00P-出票登记、NES.002.20.00P-提示承兑、NES.003.20.00P-提示收票、NES.014.20.00P-撤票
	 */
	public String txApplyQueryBill(PlOnlineAcptDetail detail,String transType,String transCode) throws Exception {
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
		if(PoolComm.NES_0022000.equalsIgnoreCase(transCode)&&"2".equalsIgnoreCase(transType)){
			note.setAcctNo("0");//作为银行签收
		}else{
			note.setAcctNo(detail.getIssuerAcct());//账号
		}
		note.setBillId(detail.getBillId());//票据id
		note.setTransType(transType);//业务类型
//		note.setStatusCode(transCode);//交易代码
		note.setBillSource(detail.getDraftSource());//
		note.setTransNo(transCode);// 交易类型集合    
		ReturnMessageNew response = poolEcdsService.txApplyQueryBusinessBatch(note);
		String status="";
		List list = response.getDetails();
		if(null != list && list.size()>0){
			Map map = (Map) list.get(0);
			String bussType = getStringVal(map.get("TRAN_INFO_ARRAY.BUSS_TYPE"));//业务分类  0-申请类 1-签收类
			status = getStringVal(map.get("TRAN_INFO_ARRAY.TRAN_RESULT"));//交易结果 0-待处理 1-处理中 2-处理成功 3-处理失败
		}
		return status;
	}

	@Override
	public List queryOnlineAcptBatchList(OnlineQueryBean queryBean,Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct dto from PlOnlineAcptBatch dto,PedOnlineAcptProtocol pro where dto.onlineAcptNo=pro.onlineAcptNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			hql.append(" and pro.custName like :custName");
			paramName.add("custName");
			paramValue.add("%"+queryBean.getCustName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			hql.append(" and pro.custOrgcode =:custOrgcode");
			paramName.add("custOrgcode");
			paramValue.add(queryBean.getCustOrgcode());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and dto.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and pro.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and pro.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getAppNo())){
			hql.append(" and pro.appNo =:appNo");
			paramName.add("appNo");
			paramValue.add(queryBean.getAppNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and pro.appName like:appName");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
			hql.append(" and pro.signBranchNo =:signBranchNo");
			paramName.add("signBranchNo");
			paramValue.add(queryBean.getSignBranchNo());
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and pro.signBranchName like :signBranchName");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and pro.dueDate >=:startDate");
			paramName.add("startDate");
			paramValue.add(queryBean.getStartDate());
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and pro.dueDate <=:endDate");
			paramName.add("endDate");
			paramValue.add(queryBean.getEndDate());
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			hql.append(" and dto.id =:id");
			paramName.add("id");
			paramValue.add(queryBean.getId());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PlOnlineAcptBatch> result = new ArrayList<PlOnlineAcptBatch>();
		if(null != page){
			result = this.find(hql.toString(), paramNames, paramValues,page);
		}else{
			result = this.find(hql.toString(), paramNames, paramValues );
		}
		return result;
	}

	@Override
	public List<PlOnlineAcptDetail> queryPlOnlineAcptDetailList(OnlineQueryBean queryBean, Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PlOnlineAcptDetail dto,PlOnlineAcptBatch batch,PedOnlineAcptProtocol pro where dto.acptBatchId=batch.id and batch.onlineAcptNo=pro.onlineAcptNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			hql.append(" and pro.custName like :custName");
			paramName.add("custName");
			paramValue.add("%"+queryBean.getCustName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			hql.append(" and pro.custOrgcode =:custOrgcode");
			paramName.add("custOrgcode");
			paramValue.add(queryBean.getCustOrgcode());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and pro.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and pro.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and dto.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getAppNo())){
			hql.append(" and pro.appNo =:appNo");
			paramName.add("appNo");
			paramValue.add(queryBean.getAppNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and pro.appName like :appName");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
			hql.append(" and pro.signBranchNo =:signBranchNo");
			paramName.add("signBranchNo");
			paramValue.add(queryBean.getSignBranchNo());
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and pro.signBranchName like :signBranchName");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and pro.dueDate >=:startDate");
			paramName.add("startDate");
			paramValue.add(queryBean.getStartDate());
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and pro.dueDate <=:endDate");
			paramName.add("endDate");
			paramValue.add(queryBean.getEndDate());
		}
		if(null !=queryBean.getStartAmt()){
			hql.append(" and dto.billAmt >=:startAmt");
			paramName.add("startAmt");
			paramValue.add(queryBean.getStartAmt());
		}
		if(null !=queryBean.getEndAmt()){
			hql.append(" and dto.billAmt <=:endAmt");
			paramName.add("endAmt");
			paramValue.add(queryBean.getEndAmt());
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and batch.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			hql.append(" and dto.id =:id");
			paramName.add("id");
			paramValue.add(queryBean.getId());
		}else{
			if(StringUtils.isNotBlank(queryBean.getIds())){
				hql.append(" and dto.id in(:ids)");
				paramName.add("ids");
				paramValue.add(Arrays.asList(queryBean.getIds().split(",")));
			}
		}
		if(StringUtils.isNotBlank(queryBean.getBatchId())){
			hql.append(" and dto.acptBatchId =:id");
			paramName.add("id");
			paramValue.add(queryBean.getBatchId());
		}
		if(StringUtils.isNotBlank(queryBean.getBillNo())){
			hql.append(" and dto.billNo =:billNo");
			paramName.add("billNo");
			paramValue.add(queryBean.getBillNo());
		}
		if(StringUtils.isNotBlank(queryBean.getPayeeName())){
			hql.append(" and dto.payeeName =:payeeName");
			paramName.add("payeeName");
			paramValue.add(queryBean.getPayeeName());
		}
		if(StringUtils.isNotBlank(queryBean.getStatus())){
			hql.append(" and dto.status =:status");
			paramName.add("status");
			paramValue.add(queryBean.getStatus());
		}
		if(queryBean.getCreateDate() != null){
			hql.append(" and to_char(dto.taskDate,'yyyy-mm-dd') =:taskDate");
			paramName.add("taskDate");
			paramValue.add(DateUtils.toDateString(queryBean.getCreateDate()));
		}
		
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PlOnlineAcptDetail> result = new ArrayList<PlOnlineAcptDetail> ();
		if(null!=page){
			result = this.find(hql.toString(), paramNames, paramValues,page);
		}else{
			result = this.find(hql.toString(), paramNames, paramValues );
		}
		return result;
	}

	
	public List<PlOnlineAcptCacheDetail> queryPlOnlineAcptCacheDetailList(OnlineQueryBean queryBean, Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PlOnlineAcptCacheDetail dto,PlOnlineAcptCacheBatch batch where dto.acptBatchId=batch.id ");
		
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and dto.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		
		if(null !=queryBean.getStartAmt()){
			hql.append(" and dto.billAmt >=:startAmt");
			paramName.add("startAmt");
			paramValue.add(queryBean.getStartAmt());
		}
		if(null !=queryBean.getEndAmt()){
			hql.append(" and dto.billAmt <=:endAmt");
			paramName.add("endAmt");
			paramValue.add(queryBean.getEndAmt());
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			hql.append(" and dto.id =:id");
			paramName.add("id");
			paramValue.add(queryBean.getId());
		}else{
			if(StringUtils.isNotBlank(queryBean.getIds())){
				hql.append(" and dto.id in(:id)");
				paramName.add("id");
				paramValue.add(Arrays.asList(queryBean.getIds().split(",")));
			}
		}
		if(StringUtils.isNotBlank(queryBean.getBatchId())){
			hql.append(" and dto.acptBatchId =:id");
			paramName.add("id");
			paramValue.add(queryBean.getBatchId());
		}
		if(StringUtils.isNotBlank(queryBean.getBillNo())){
			hql.append(" and dto.billNo =:billNo");
			paramName.add("billNo");
			paramValue.add(queryBean.getBillNo());
		}
		if(StringUtils.isNotBlank(queryBean.getPayeeName())){
			hql.append(" and dto.payeeName =:payeeName");
			paramName.add("payeeName");
			paramValue.add(queryBean.getPayeeName());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PlOnlineAcptCacheDetail> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}
	
	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-12
	 * @description 在线银承全量校验
	 */
	public ReturnMessageNew txAcptFullCheck(OnlineQueryBean queryBean) throws Exception {
		ReturnMessageNew response =  new ReturnMessageNew();
		response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);
		Ret ret = new Ret();
		
        List<PlOnlineAcptDetail> details = queryBean.getDetalis();
        if(null != details && details.size()>0){
        	String commError ="";//禁止
			String commMsg = "";//提示
			PedOnlineAcptProtocol protocol = this.queryOnlinAcptPtlByNo(queryBean.getOnlineAcptNo());
			
			//在线流贷协议客户开关校验
	        if(protocol.getYcFlag().equals(PoolComm.NO)){
	        	//流贷协议开关为关闭
	        	response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
				response.getBody().put("CHECK_INFO", "银承协议客户开关为关闭！");
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("银承协议客户开关为关闭！");
				response.setRet(ret);
				return response;
	        }
	        
			//【1】在线银承业务开关(总开关)、运营时间
			if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
				ret = onlineManageService.checkOnlineSwitch(protocol.getSignBranchNo(),PublicStaticDefineTab.PRODUCT_001);
				if(!ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
		        	String msg=ret.getRET_MSG()+ret.getError_MSG();
					if(msg.length() > 0){
						ret.setRET_MSG(msg.substring(0, msg.length()-1));
					}
					response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
					response.getBody().put("CHECK_INFO", ret.getRET_MSG());
	    			response.setRet(ret);
	    			return response;
	    		}
			}
			
			
			//【2】协议校验
			if(null == protocol){
				response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
				response.getBody().put("CHECK_INFO", "该"+queryBean.getOnlineAcptNo()+"在线协议编号与在线协议类型不一致！");
    			ret.setRET_MSG("该"+queryBean.getOnlineAcptNo()+"在线协议编号与在线协议类型不一致");
    			ret.setRET_CODE(Constants.TX_FAIL_CODE);
    			response.setRet(ret);
    			return response;
			}
			
			//【2.1】票据池协议校验
			if(new BigDecimal(100).compareTo(protocol.getDepositRatio()) !=0){//非百分百保证金
				PedProtocolDto poolDto = queryBean.getPool();
				
				if(null == poolDto || !PoolComm.OPEN_01.equals(poolDto.getOpenFlag())){
		        	response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
					response.getBody().put("CHECK_INFO", "票据池未签约融资功能！");
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据池未签约融资功能！");
					response.setRet(ret);
					return response;
				}
				
				if(poolDto.getOpenFlag().equals(PoolComm.OPEN_01) && poolDto.getEffenddate().compareTo(new Date())<0 && poolDto.getIsGroup().equals(PoolComm.NO)){					
					response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
					response.getBody().put("CHECK_INFO", "无生效的单户融资票据池协议");
					ret.setRET_MSG("无生效的单户融资票据池协议");
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					response.setRet(ret);
					return response;
				}
				
			}
			
    		//【3】黑名单校验
			queryBean.setCustName(protocol.getCustName());
			queryBean.setCustNumber(protocol.getCustNumber());
//			queryBean.setCustOrgcode(protocol.getCustOrgcode());
			queryBean.setStatus(PublicStaticDefineTab.STATUS_1);//生效
    		if(onlineManageService.onlineBlackListCheck(queryBean)){
    			logger.info(protocol.getCustNumber()+"该客户已被加入线上禁入名单");
    			response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
				response.getBody().put("CHECK_INFO", "该"+queryBean.getOnlineAcptNo()+"在线协议已失效!");
				ret.setRET_MSG("该"+queryBean.getOnlineAcptNo()+"在线协议已失效");
    			ret.setRET_CODE(Constants.TX_FAIL_CODE);
    			response.setRet(ret);
    			return response;
    		}
    		
    		if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
    			
    			
    			if(new BigDecimal(100).compareTo(protocol.getDepositRatio()) !=0){//非百分百保证金
    				//【4】关联票据池校验
    				PedProtocolDto pool = queryBean.getPool();
    				Map comm = this.acptApplyCheck(queryBean,protocol,pool);
    				commError += (String) comm.get("error");//禁止
    				commMsg += (String) comm.get("msg");//提示
    				
    				//【5】池额度校验，（经办校验，复核的实占）
    				commMsg += this.txPoolCreditCheck(queryBean, pool);
    			}
    			//【6】协议额度校验
    			BigDecimal usedAmt = this.getOnlineAcptPtlAmt(queryBean);
    			if(protocol.getOnlineAcptTotal().subtract(usedAmt).compareTo(queryBean.getOnlineAcptTotal())<0){
    				commError = commError +"在线银承合同余额小于客户申请金额^";
    			}
    			
    			//【7】明细校验
    			List <PlOnlineAcptCacheDetail> arr =new ArrayList<PlOnlineAcptCacheDetail>();
    			for(int i=0;i<details.size();i++){
    				PlOnlineAcptDetail detail = (PlOnlineAcptDetail) details.get(i);
    				Map map = new HashMap();
    				String error = "";//禁止
    				String msg = "";//提示
    				//业务校验
    				Map payeeCheck = this.acptPayeeCheck(queryBean,protocol, detail, msg, error);
    				//返回信息
    				map.put("SERIAL_NO", detail.getBillSerialNo());//序号
    				msg += (String)payeeCheck.get("msg");
    				error += (String)payeeCheck.get("error");
    				if(msg.length()>0){
    					commMsg = commMsg + detail.getBillSerialNo() + "^" + msg + ";";
    					map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_2);
    				}
    				if(error.length()>0){
    					commError +=  detail.getBillSerialNo() + "^" + error + ";";
    					map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
    					response.getFileHead().put("FILE_FLAG", "2");
    				}else{
    					map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);
    				}
    				String str = error+msg;
    				if(str.length()>0){
    					if(str.endsWith("^")){
    						str = str.substring(0, (error+msg).length()-1);
    					}
    				}
    				map.put("CHECK_INFO", str);//校验结果说明
    				response.getDetails().add(map);
    				
    				//明细校验日志保存
    				if(error.length()>0 || msg.length()>0){
    					PlOnlineAcptCacheDetail hisDetail=new PlOnlineAcptCacheDetail();
            			BeanUtils.copyProperties(hisDetail, detail);
            			hisDetail.setAcptBatchId("1");
            			this.txStore(hisDetail);
            			arr.add(hisDetail);
    					onlineManageService.txSaveTrdeLog(protocol.getCustNumber(),protocol.getBpsNo(),hisDetail.getBillSerialNo(),hisDetail.getId(), "1", queryBean.getOperationType(), "网银", error, "PJC056", "在线银承申请", "receive");
    				}
    			}
    			
    			//批次校验日志保存
            	if(commError.length()>0 || commMsg.length()>0){
            		//保存历史 批次明细表
            		PlOnlineAcptCacheBatch hisBatch=new PlOnlineAcptCacheBatch();
            		PlOnlineAcptBatch batch = queryBean.getAcptBatch();
        			BeanUtils.copyProperties(hisBatch, batch);
        			hisBatch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
        			this.txStore(hisBatch);
        			
        			List hisList=new ArrayList();
        			for(PlOnlineAcptCacheDetail detail:arr){
        				PlOnlineAcptCacheDetail plDetail = new PlOnlineAcptCacheDetail();
        				BeanUtil.beanCopy(detail, plDetail);
        				plDetail.setAcptBatchId(hisBatch.getId());
            			hisList.add(plDetail);
        			}
        			financialAdviceService.txForcedSaveList(hisList);
//        			this.txStoreAll(hisList);
      			    financialAdviceService.txForcedSaveList(hisList);	
      			    String msg=commError+commMsg;
     			    if(msg.lastIndexOf("^") == msg.length()-1){//去除最后^
     				    msg= msg.substring(0, msg.length()-1); 
     			     }
     			   if(msg.lastIndexOf("|") == msg.length()-1){//去除最后|
    				    msg= msg.substring(0, msg.length()-1); 
    			     }
            		onlineManageService.txSaveTrdeLog(protocol.getCustNumber(),protocol.getBpsNo(),queryBean.getOnlineAcptNo(),hisBatch.getId(), "1", queryBean.getOperationType(), "网银", msg, "PJC056", "在线银承申请", "receive");
            		
            	}
    		}
        	
        	
        	if(commMsg.trim().length()>0){
        		response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_2);
        	}
        	if(commError.trim().length()>0){
        		response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
        		ret.setRET_MSG("票据池系统校验未通过！");
        	}else{
        		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
        			ret.setRET_MSG("票据池系统校验通过，请稍后查询出票结果！");
        		}else{
        			ret.setRET_MSG("票据池系统校验通过！");
        		}
        	}
        	 String msg=commError+commMsg;
        	 if(msg.trim().length()>0){
        		 if(msg.lastIndexOf("^") == msg.length()-1){//去除最后^
    				 msg= msg.substring(0, msg.length()-1); 
    			 }
        	 }else{
        		 msg="通过";
        	 }
        	response.getBody().put("CHECK_INFO", msg);
        }
        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        response.setRet(ret);
		return response;
	}
	
	/**
	 * 在线银承业务票据池池额度校验
	 * @param queryBean
	 * @param pool
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-16下午4:19:03
	 */
	private String txPoolCreditCheck(OnlineQueryBean queryBean,PedProtocolDto pool){
		
		String checkRet = "";
		try {
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pool);
			CreditProduct product = queryBean.getProduct();
			List<PedCreditDetail> crdtDetailList = queryBean.getCrdtDetailList();
			if(!PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
				List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
				String flowNo = Long.toString(System.currentTimeMillis());
				for(PedCreditDetail crdtDetail:crdtDetailList){
					CreditRegisterCache crdtReg = creditRegisterService.createCreditRegisterCache(crdtDetail, product, pool, ap.getApId());
					crdtReg.setFlowNo(flowNo);
					crdtRegList.add(crdtReg);
				}
				
				//保证金同步及额度计算及资产表重置
				String apId = ap.getApId();
				financialService.txUpdateBailAndCalculationCredit(apId, pool);
				
				//额度校验  若是总量模式则为票据池额度不足;若是期限配比则为期限配比额度不足
				Ret crdtCheckRet =  financialService.txCreditUsedCheck(crdtRegList, pool,flowNo);
				if(!crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
//					checkRet = checkRet +"当前票据池低风险可用额度不足^";
					if(pool.getPoolMode().equals(PoolComm.POOL_MODEL_01)){
						//总量模式
						logger.info("当前票据池低风险可用额度不足");
						checkRet += "当前票据池低风险可用额度不足^";
					}else{
						
						//这行逻辑不想解释，只为了满足业务要求的一条提示语，非要加个总量的校验，很讨厌，因为并没有意义
						
						//票据池保证金资产额度
		     	        AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);		     	        
		     	        //低风险票资产额度
		     	        AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
		     	        BigDecimal limitBalance = atBail.getCrdtFree().add(atBillLow.getCrdtFree());   //票据池低风险可用额度 
						
		     	        if(queryBean.getOnlineAcptTotal().compareTo(limitBalance)>0){
		     	        	checkRet += "票据池额度不足^";
		     	        }else{		     	        	
		     	        	checkRet += "票据池额度期限匹配不通过^";
		     	        	logger.info("票据池额度期限匹配不通过");
		     	        }
		     	       
		     	       
					}
				}
				
				
				if(crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					logger.info("票据池额度充足！");
					
				}else{
					AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);
					AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
					BigDecimal lowTotal = atBail.getCrdtFree().add(atBillLow.getCrdtFree());
					if(pool.getPoolMode().equals(PoolComm.POOL_MODEL_02)){
						//期限配比模式
						if(lowTotal.compareTo(queryBean.getOnlineLoanTotal())>=0){//低风险额度足，但是期限匹配不通过
							logger.info("当前票据池低风险可用额度不足");
							checkRet += "当前票据池低风险可用额度不足^";
						}else{						
							checkRet += "当前票据池低风险可用额度不足^";
							logger.info("当前票据池低风险可用额度不足");
						}
					}
					
				}
			}
			
		} catch (Exception e) {
			checkRet += "当前票据池低风险可用额度不足^";
		}
		return checkRet;
	}
	
	/**
	 * 在线银承业务票据池池额度复核校验
	 * @param queryBean
	 * @param pool
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-16下午4:19:03
	 */
	private String txPoolCreditApplyCheck(OnlineQueryBean queryBean,PedProtocolDto pool){
		
		String checkRet = "";
		try {
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pool);
			CreditProduct product = queryBean.getProduct();
			List<PedCreditDetail> crdtDetailList = queryBean.getCrdtDetailList();
			if(!PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
				List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
				String flowNo = Long.toString(System.currentTimeMillis());
				for(PedCreditDetail crdtDetail:crdtDetailList){
					CreditRegisterCache crdtReg = creditRegisterService.createCreditRegisterCache(crdtDetail, product, pool, ap.getApId());
					crdtReg.setFlowNo(flowNo);
					crdtRegList.add(crdtReg);
				}
				
				//保证金同步及额度计算及资产表重置
				String apId = ap.getApId();
				financialService.txUpdateBailAndCalculationCredit(apId, pool);
				
				//额度校验  若是总量模式则为票据池额度不足;若是期限配比则为期限配比额度不足
				Ret crdtCheckRet =  financialService.txCreditUsedCheck(crdtRegList, pool,flowNo);
				if(!crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
//					checkRet = checkRet +"当前票据池低风险可用额度不足^";
					if(pool.getPoolMode().equals(PoolComm.POOL_MODEL_01)){
						//总量模式
						logger.info("当前票据池低风险可用额度不足");
						checkRet += "当前票据池低风险可用额度不足^";
					}else{
						
						//这行逻辑不想解释，只为了满足业务要求的一条提示语，非要加个总量的校验，很讨厌，因为并没用
						
						//票据池保证金资产额度
		     	        AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);		     	        
		     	        //低风险票资产额度
		     	        AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
		     	        BigDecimal limitBalance = atBail.getCrdtFree().add(atBillLow.getCrdtFree());   //票据池低风险可用额度 
						
		     	        if(queryBean.getOnlineAcptTotal().compareTo(limitBalance)>0){
		     	        	checkRet += "票据池额度不足^";
		     	        }else{		     	        	
		     	        	checkRet += "票据池额度期限匹配不通过^";
		     	        	logger.info("票据池额度期限匹配不通过");
		     	        }
		     	       
		     	       
					}
				}
				
				
				if(crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
					logger.info("票据池额度充足！");
					
				}else{
					AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);
					AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
					BigDecimal lowTotal = atBail.getCrdtFree().add(atBillLow.getCrdtFree());
					if(pool.getPoolMode().equals(PoolComm.POOL_MODEL_02)){
						//期限配比模式
						if(lowTotal.compareTo(queryBean.getOnlineLoanTotal())>=0){//低风险额度足，但是期限匹配不通过
							logger.info("当前票据池低风险可用额度不足");
							checkRet += "当前票据池低风险可用额度不足^";
						}else{						
							checkRet += "当前票据池低风险可用额度不足^";
							logger.info("当前票据池低风险可用额度不足");
						}
					}
					
				}
			}
			
		} catch (Exception e) {
			checkRet += "当前票据池低风险可用额度不足^";
		}
		return checkRet;
	}
	
	
	/**
	 * @Title getOnlineAcptPtlAmt
	 * @author wss
	 * @date 2021-7-10
	 * @Description 协议已用额度
	 * @return BigDecimal
	 */
	private BigDecimal getOnlineAcptPtlAmt(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select sum(dto.billAmt) from PlOnlineAcptDetail dto,PlOnlineAcptBatch batch,PedOnlineAcptProtocol pro where dto.acptBatchId=batch.id and batch.onlineAcptNo=pro.onlineAcptNo ");
		
		hql.append(" and pro.onlineAcptNo =:onlineAcptNo");
		paramName.add("onlineAcptNo");
		paramValue.add(queryBean.getOnlineAcptNo());
		
		/**
		 * 添加过滤条件-解决生产出现的问题：复核时明细数据已保存，再次统计时本次业务的明细数据不计入
		 */
		if(queryBean.getAcptBatch() != null){
			hql.append(" and dto.acptBatchId !=:acptBatchId");
			paramName.add("acptBatchId");
			paramValue.add(queryBean.getAcptBatch().getId());
		}
		hql.append(" and dto.status not in(:status)");
		paramName.add("status");
		List list = new ArrayList();
		list.add(PublicStaticDefineTab.ACPT_DETAIL_012);
		paramValue.add(list);
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues );
		if(null != result && result.size()>0){
			return result.get(0)!=null?(BigDecimal) result.get(0):new BigDecimal(0);
		}else{
			return new BigDecimal(0);
		}
	}

	public String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtils.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}

	/**
	 * @author wss
	 * @date 2021-5-12
	 * @description 删除电票信息
	 * @param busiId
	 * @throws Exception 
	 */
	public void txApplyDeleteBill(PlOnlineAcptDetail detail){
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) this.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
		
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
		note.setSigner(batch.getElctrncSign());//电子签名
		note.setBillId(detail.getIssuerAcct());//申请人账号
		try {
			
			Map map = new HashMap();
			map.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", detail.getLoanNo());
			map.put("BILL_INFO_ARRAY.BILL_ID", detail.getBillId());
			map.put("BILL_INFO_ARRAY.HOLD_BILL_ID", detail.getHilrId());
			map.put("BILL_INFO_ARRAY.BILL_SOURCE", detail.getDraftSource());
			note.getDetails().add(map);
			
			ReturnMessageNew response = poolEcdsService.txDeleteApplyBill(note);
			if(!response.isTxSuccess()){
				//记录日志
				onlineManageService.txSaveTrdeLog(detail.getBillSerialNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "删除电票失败"+response.getRet().getRET_MSG(), "BBSP008", PublicStaticDefineTab.ACPT_BUSI_NAME_05, "send");
			}
		} catch (Exception e) {
			logger.error("驱动bbsp删除电票信息失败", e);
		}
	}

	/**
	 * @author wss
	 * @date 2021-5-13
	 * @description 提示承兑签收
	 * @param 
	 * @throws Exception 
	 */
	public boolean txApplyAcptSign(PlOnlineAcptDetail detail,String elctrncSign,String auditStatus) throws Exception {
		PlOnlineAcptBatch batch = (PlOnlineAcptBatch) this.dao.load(PlOnlineAcptBatch.class, detail.getAcptBatchId());
		PedOnlineAcptProtocol protocol = this.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
		String marginAccount ="";//票据池保证金账号
		if(new BigDecimal(100).compareTo(protocol.getDepositRatio())!=0){//不是100%银承
			PedProtocolDto pool = (PedProtocolDto) this.dao.load(PedProtocolDto.class, protocol.getBpsId());
			marginAccount = pool.getMarginAccount();
		}
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
		
		note.setAcptProtocolNo(batch.getContractNo());//承兑协议编号(因传协议编号信贷那边做合同占用变更时有问题,先用合同号)
		note.setInAcctBranch(detail.getIssuerBankCode());//业务发起机构    (出票人开户行行号)
		note.setBranchNo(protocol.getSignBranchNo());//账务机构
		note.setRemitterBatch(detail.getAcptBankCode());//签发机构            承兑签发机构(承兑人所在机构) 
		note.setCustNo(protocol.getAppNo());//客户经理号      
		note.setCustName(protocol.getAppName());//客户经理名称  
		note.setCllentNo(protocol.getCustNumber());//出票人客户号
		note.setRemitterAcctNo(detail.getIssuerAcct());//出票人账号
		note.setRemitter(detail.getIssuerName());//出票人名称
		note.setAcceptorBankNo(detail.getAcptBankCode());//付款行行号
		note.setAcceptorBankName(detail.getAcptBankName());//付款行行名
		note.setAcptDt(DateUtils.toString(detail.getIsseDate(), "yyyyMMdd"));//出票日期
		note.setDueDt(DateUtils.toString(detail.getDueDate(), "yyyyMMdd"));//汇票到期日
		note.setIouNo(detail.getLoanNo());//出账编号
		note.setAcctNo(detail.getIssuerAcct());//结算账号  
		
		if(null!=protocol.getDepositRateLevel()){
			if(protocol.getDepositRateLevel().equals("0")){
				note.setRateType("001");//活期
			}else if(protocol.getDepositRateLevel().equals("1")){
				note.setRateType("002");//定期
			}
		}
		note.setDepositRate(protocol.getDepositRatio());//保证金比例
		note.setPoolDepositAcctNo(marginAccount);//票据池保证金账号//票据池保证金账号
		note.setPoolDepositAcctNo(protocol.getDepositAcctNo());//保证金扣款账户
		
		/*
		note.setBillId(detail.getBillId());//票据id
		note.setSignature(elctrncSign);//电子签名
		note.setIouNo(detail.getLoanNo());//借据号
		note.setAcptProtocolNo(detail.getOnlineAcptNo());//承兑协议编号
		note.setContractNo(batch.getContractNo());
		note.setAcctNo(detail.getIssuerAcct());//结算账号
		note.setBillNo(detail.getBillNo());//票据号码
		note.setAcptDt(DateUtils.formatDateToString(detail.getIsseDate(),DateUtils.ORA_DATE_FORMAT));//承兑日期
		note.setDeduDepositAcctNo(protocol.getDepositAcctNo());//保证金扣款账户
		note.setInAcctBranch(protocol.getInAcctBranchNo());//出账机构
		note.setDepositRate(protocol.getDepositRatio());//保证金比例
		if(protocol.getDepositRatio().compareTo(new BigDecimal(0)) == 0){
			note.setRateType("001");//活期
		}
		note.setIntFluctuationMode(protocol.getDepositRateFloatFlag());//利率浮动方式
		note.setFloatIntRate(protocol.getDepositRateFloatValue());//浮动利率值
		if(null!=protocol.getDepositRateLevel()){
			if(protocol.getDepositRateLevel().equals("0")){
				note.setRateType("001");//活期
			}else if(protocol.getDepositRateLevel().equals("1")){
				note.setRateType("002");//定期
			}
		}
		
		note.setFeeFlag("1");//收费标志
		note.setFEE_RATE(protocol.getFeeRate());//手续费率
		note.setFeeAcctNO(detail.getIssuerAcct());//收费账号
		note.setFeeMode("2");//收费方式
		note.setAuditStatus("0");//审核状态
		note.setPoolDepositAcctNo(marginAccount);//票据池保证金账号//票据池保证金账号
		note.setBillMoney(detail.getBillAmt().toString());
		note.setRemitterAcctNo(detail.getIssuerAcct());
		note.setRemitter(detail.getIssuerName());
		note.setRemitterBankName(detail.getIssuerBankName());
		note.setAcceptorBankNo(detail.getAcptBankCode());
		note.setPayee(detail.getPayeeName());
		note.setPayeeAcctNo(detail.getPayeeAcct());
		note.setPayeeBankName(detail.getPayeeBankName());
		note.setAcptDt(DateUtils.formatDateToString(detail.getIsseDate(),DateUtils.ORA_DATE_FORMAT));
		note.setDueDt(DateUtils.formatDateToString(detail.getDueDate(),DateUtils.ORA_DATE_FORMAT));*/
		
		
		/**
		 * 票据信息数组需传送的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("BILL_INFO_ARRAY.IOU_NO",detail.getLoanNo());//借据编号            
		infoMap.put("BILL_INFO_ARRAY.EXPEND_DETAIL_SEQ_NO",detail.getLoanNo());//出账清单流水号      
		infoMap.put("BILL_INFO_ARRAY.TRAN_ID",detail.getTransId());//交易ID              
		infoMap.put("BILL_INFO_ARRAY.BILL_NO",detail.getBillNo());//票据(包)号码        
		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",detail.getDraftSource());//票据来源            
		infoMap.put("BILL_INFO_ARRAY.ACCE_DATE",DateUtils.formatDateToString(detail.getIsseDate(),DateUtils.ORA_DATE_FORMAT));//承兑日期            
		infoMap.put("BILL_INFO_ARRAY.INT_RATE_FLOAT_TYPE",protocol.getDepositRateFloatFlag() == null ? "0" : protocol.getDepositRateFloatFlag());//利率浮动方式        
		infoMap.put("BILL_INFO_ARRAY.FLOAT_INT_RATE_VALUE",protocol.getDepositRateFloatValue() == null ? "0" :BigDecimalUtils.setScale(6, protocol.getDepositRateFloatValue()).toPlainString());//浮动利率值          
		infoMap.put("BILL_INFO_ARRAY.FEE_FLAG","1");//收费标志            
		infoMap.put("BILL_INFO_ARRAY.FEE_MODE","2");//收费方式            
		infoMap.put("BILL_INFO_ARRAY.FEE_ACCT_NO",detail.getIssuerAcct());//收费账号            
		infoMap.put("BILL_INFO_ARRAY.AUDIT_STATUS",auditStatus);//审核状态            
		infoMap.put("BILL_INFO_ARRAY.FEE_RATE",protocol.getFeeRate().multiply(new BigDecimal("10")));//手续费率(票据池系统存的百分制，传给电票需要千分制)
		infoMap.put("BILL_INFO_ARRAY.BPS_DEPOSIT_ACCT_NO",marginAccount);//票据池保证金账号    
		infoMap.put("BILL_INFO_ARRAY.BILL_ID",detail.getBillId());//票据编号            
		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",detail.getHilrId());//持票编号            
		infoMap.put("BILL_INFO_ARRAY.GUARANTEE_NO",protocol.getDepositAcctNo());//保证金扣款账户   
		infoMap.put("BILL_INFO_ARRAY.GUARANTEE_SER_NO","");//担保序号            
		infoMap.put("BILL_INFO_ARRAY.GUARANTEE_AMT","");//担保金额            
		infoMap.put("BILL_INFO_ARRAY.DEPOSIT_RATE",protocol.getDepositRatio());//保证金比例          
		infoMap.put("BILL_INFO_ARRAY.SETTLE_ACCT_NO",detail.getIssuerAcct());//结算账号            
		infoMap.put("BILL_INFO_ARRAY.SETTLE_SUB_ACCT_NO",detail.getIssuerAcct());//结算账户子序号      
		infoMap.put("BILL_INFO_ARRAY.COMMISSION_AMT","");//手续费              
		infoMap.put("BILL_INFO_ARRAY.PROMISE_AMT","");//承诺费              
		infoMap.put("BILL_INFO_ARRAY.BILL_AMT",detail.getBillAmt().toString());//票据金额            
		infoMap.put("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NO",detail.getIssuerBankCode());//出票人开户行行号    
		infoMap.put("BILL_INFO_ARRAY.DRAWER_OPEN_BANK_NAME",detail.getIssuerBankName());//出票人开户行行名    
		infoMap.put("BILL_INFO_ARRAY.PAYEE_NAME",detail.getPayeeName());//收款人全称          
		infoMap.put("BILL_INFO_ARRAY.PAYEE_ACCT_NO",detail.getPayeeAcct());//收款人账号          
		infoMap.put("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NO",detail.getPayeeBankCode());//收款人开户行行号    
		infoMap.put("BILL_INFO_ARRAY.PAYEE_OPEN_BANK_NAME",detail.getPayeeBankName());//收款人开户行行名    
		infoMap.put("BILL_INFO_ARRAY.REMIT_EXPIRY_DATE","");//汇票到期日          
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_NAME","");//承兑人全称          
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_ACCT_NO","0");//承兑人账号          
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NO",detail.getAcptBankCode());//承兑人开户行行号    
		infoMap.put("BILL_INFO_ARRAY.ACCEPTOR_OPEN_BANK_NAME","");//承兑人开户行行名    
		infoMap.put("BILL_INFO_ARRAY.OBG_TEXT1","");//保留字段1           
		infoMap.put("BILL_INFO_ARRAY.OBG_TEXT2","");//保留字段2           
		infoMap.put("BILL_INFO_ARRAY.OBG_TEXT3","");//保留字段3           
		infoMap.put("BILL_INFO_ARRAY.POOL_LIMIT_SEQ_NO","");//票据池额度占用流水号
		infoMap.put("BILL_INFO_ARRAY.DEPOSIT_ACCT_INFO","");//扩展字段账号数据JSON
		infoMap.put("BILL_INFO_ARRAY.IN_EXT_INFO","");//扩展字段(对外)      
    

		note.getDetails().add(infoMap);
		
		try {
			return poolEcdsService.txApplyAcptSign(note);
		} catch (Exception e) {
			logger.error("提示承兑签收异常！", e);
			throw new Exception(e);
		}
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 提示收票
	 */
	public boolean txApplyReceiveBill(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch) throws Exception {
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
		note.setBillId(detail.getBillId());//票据id
		note.setAcctNo(detail.getIssuerAcct());//客户账号
		note.setSignature(batch.getElctrncSign());//电子签名
		note.setOperationType("1");//操作类型
		
		
		/**
		 * body内需要传送的值
		 */
		note.setApplicantAcctNo(detail.getIssuerAcct());//电票签约帐号  多账号|拼接
		note.setSignature("0");//电子签名
		
		/**
		 * 票据信息数组需传送的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",detail.getDraftSource());//票据来源 
		infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0032000);//交易编号  质押申请
		infoMap.put("BILL_INFO_ARRAY.BILL_NO",detail.getBillNo());//票据（包）号码
		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",detail.getHilrId());//持票id
		infoMap.put("BILL_INFO_ARRAY.BILL_ID",detail.getBillId());//票据id

		infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
		infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
		note.getDetails().add(infoMap);
		
		ReturnMessageNew response = poolEcdsService.txApplyImplawn(note);
		//区分通讯异常 抛出去   还是业务失败返回错误
		String responseCode = response.getRet().getRET_CODE();
        if(Constants.TX_SUCCESS_CODE.equals(responseCode)){
        	return true;
        }else{
        	return false;
        }
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 汇票撤销(未用退回)
	 */
	public boolean txApplyCancleBill(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch) throws Exception {
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
		
		/**
		 * body内需要传送的值
		 */
		note.setApplicantAcctNo(detail.getIssuerAcct());//电票签约帐号  多账号|拼接
		note.setSignature(batch.getElctrncSign());//电子签名
		
		/**
		 * 票据信息数组需传送的值
		 */
		Map infoMap = new HashMap();
		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",detail.getDraftSource());//票据来源 
		infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0142000);//交易编号  质押申请
		infoMap.put("BILL_INFO_ARRAY.BILL_NO",detail.getBillNo());//票据（包）号码
		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",detail.getHilrId());//持票id
		infoMap.put("BILL_INFO_ARRAY.BILL_ID",detail.getBillId());//票据id
		infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票

		infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
		note.getDetails().add(infoMap);
		
		
		
		ReturnMessageNew response = poolEcdsService.txApplyImplawn(note);
		return response.isTxSuccess();
	}

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-5-13
	 * @description 承兑出账撤销
	 */
	public boolean txApplyRevokeApply(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch) throws Exception{
		boolean flag = false;
		/**
		 * 判断是否需要做承兑撤销出账
		 */
		String acctStatus = this.txApplyQueryAcctStatus(detail,batch.getContractNo());
		if("0".equals(acctStatus) || "2".equals(acctStatus)){//未记账需撤销出账申请
			ECDSPoolTransNotes note =new ECDSPoolTransNotes();
			note.setConferNo(batch.getContractNo());//合同编号 
			note.setLoanAcctNo(detail.getLoanNo());//电子签名
			note.setXTransNo(detail.getLoanNo()+"|"+detail.getIssuerAcct());//批次流水号
			
			Map pledgeMap = new HashMap();
			pledgeMap.put("IOU_INFO_ARRAY.IOU_NO",detail.getLoanNo());//
			pledgeMap.put("IOU_INFO_ARRAY.EXPEND_DETAIL_SEQ_NO",detail.getLoanNo());//
			
			note.getDetails().add(pledgeMap);
			ReturnMessageNew resp =  poolEcdsService.txApplyRevokeSign(note);
			String responseCode = resp.getRet().getRET_CODE();
			// 如果响应成功保存
			if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
				List list = resp.getDetails();
				if(list != null && list.size() > 0){
					for (int i = 0; i < list.size(); i++) {
						Map map = (Map) list.get(i);
						String loanNo = (String) map.get("IOU_INFO_ARRAY.IOU_NO");
						String status = (String) map.get("IOU_INFO_ARRAY.FLOW_STATUS");
						
						if(loanNo.equals(detail.getLoanNo())){
							if(status.equals("PICE01020202-PICE01030201")){
								flag = true;
								break;
							}
						}else{
							continue;
						}
					}
				}else{
					flag = true;
				}
				
			}else{
				flag = false;
			}
		}else if("9".equals(acctStatus)){
			flag = true;
		}
		/**
		 * 2、继续撤销出票申请
		 */
		if(detail.getDraftSource().equals(PoolComm.CS01) || flag){
			ECDSPoolTransNotes note =new ECDSPoolTransNotes();
			note.setAcctNo(detail.getIssuerAcct());//账号
			note.setSignature(batch.getElctrncSign());//电子签名
			note.setTransNo(PoolComm.NES_0022000);//交易编号
			
			Map pledgeMap = new HashMap();
			pledgeMap.put("REQUEST_INFO_ARRAY.RECOURSE_ID","");//追索ID
			pledgeMap.put("REQUEST_INFO_ARRAY.BILL_SOURCE",detail.getDraftSource());//票据来源
			pledgeMap.put("REQUEST_INFO_ARRAY.BILL_ID",detail.getBillId());//票据id
			pledgeMap.put("REQUEST_INFO_ARRAY.FORCE_DISCOUT_FLAG","");//强制贴现标志 
			pledgeMap.put("REQUEST_INFO_ARRAY.INPOOL_FLAG","");//入池标志
			pledgeMap.put("REQUEST_INFO_ARRAY.LOCK_FLAG","");//锁定标志
			pledgeMap.put("REQUEST_INFO_ARRAY.CANCEL_SEQ_NO",detail.getLoanNo());//交易撤回流水号
			pledgeMap.put("REQUEST_INFO_ARRAY.TRAN_ID",detail.getSendTransId());//交易id
			note.getDetails().add(pledgeMap);
			return poolEcdsService.txApplyRevokeApply(note);
		}
		
		return false;
	
	}

	/**
	 * @throws Exception 
	 * @Title 信贷风险探测及额度占用
	 * @author wss
	 * @date 2021-5-15
	 * @param batch
	 * @param protocolType 银承 流贷
	 * @param operationType 占用 、校验、释放
	 * @param dueDate 最久到期日(经办岗)
	 */
	public ReturnMessageNew txPJE021Handler(PlOnlineAcptBatch batch, String protocolType,String operationType,Date dueDate) throws Exception {
		CreditTransNotes note = new CreditTransNotes();
		PedOnlineAcptProtocol protocol =  this.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
		note.setOnlineNo(batch.getOnlineAcptNo());
		note.setoNlineCreditNo(protocol.getBaseCreditNo());
		note.setCustomerId(protocol.getCustNumber());
		note.setOperationType(operationType);
		note.setOnlineType(protocolType);
		note.setContractNo(batch.getContractNo());
		if(StringUtils.isNotBlank(batch.getId())){
			List<PlOnlineAcptDetail> list = this.queryOnlineAcptDetailByBatchId(batch.getId());
			dueDate = (Date)list.get(0).getDueDate();
			for(PlOnlineAcptDetail detail:list){
				if(dueDate.compareTo(detail.getDueDate())<0){
					dueDate = detail.getDueDate();
				}
			}
			note.setEXPIRY_DATE(DateUtils.formatDateToString(dueDate, DateUtils.ORA_DATE_FORMAT));
		}else{
			note.setEXPIRY_DATE(DateUtils.formatDateToString(dueDate, DateUtils.ORA_DATE_FORMAT));
		}
		note.setEFFECTIVE_DATE(DateUtils.formatDateToString(batch.getApplyDate(), DateUtils.ORA_DATE_FORMAT));
		note.setBusinessSum(batch.getTotalAmt());
		ReturnMessageNew response = poolCreditClientService.txPJE021(note);
//		ReturnMessageNew response =new ReturnMessageNew();
//		response.setTxSuccess(true);
		return response;
	}

	public List<PlOnlineAcptDetail> queryOnlineAcptDetailByBatchId(String batchId) {
		String sql ="select dto from PlOnlineAcptDetail dto where dto.acptBatchId='"+batchId+"'";
		List result = this.find(sql);
		if(null != result && result.size()>0){
			return result;
		}else{
			return null;
		}
	}

	public List<PlOnlineAcptDetail> queryOnlineAcptHisDetailByBatchId(String batchId) {
		String sql ="select dto from PlOnlineAcptDetail dto where dto.acptBatchId='"+batchId+"'";
		List result = this.find(sql);
		if(null != result && result.size()>0){
			return result;
		}else{
			return null;
		}
	}
	/**
	 * 统一撤销
	 */
	public boolean txApplyQueryCancle(PlOnlineAcptDetail detail,String transType) throws Exception {
		ECDSPoolTransNotes note =new ECDSPoolTransNotes();
/*		note.setAcctNo(detail.getIssuerAcct());//账号
		note.setBillNo(detail.getBillNo());//票号
		note.setTransType(transType);//业务类型
		
		note.setApplicantAcctNo(detail.getIssuerAcct());//账号
		
		note.setBillNo(detail.getBillNo());//票号
		
		note.setPartnerType("QT01");//查询类型   QT00-可签收查询  QT01撤销申请查询
		note.setTransNo(PoolComm.NES_0142000);//交易类型
*/		
		Map infoMap = new HashMap();
//		infoMap.put("QUERY_INFO_ARRAY.INPOOL_FLAG", poolNotes.getIfInPool());//是否入池
//		infoMap.put("BRANCH_NO",poolNotes.getBranchNo());//查询机构号		
		infoMap.put("QUERY_INFO_ARRAY.APPLYER_ACCT_NO",detail.getIssuerAcct());//账号
		infoMap.put("QUERY_INFO_ARRAY.DRAFT_TYPE","");//汇票类型
		infoMap.put("QUERY_INFO_ARRAY.ACCEPTOR_ACCT_NAME","");//承兑人名称
		infoMap.put("QUERY_INFO_ARRAY.BILL_NO",detail.getBillNo());//票据号码
		infoMap.put("MIN_START_BILL_NO",detail.getBeginRangeNo());//票据开始子区间号
		infoMap.put("MAX_START_BILL_NO",detail.getBeginRangeNo());//票据开始子区间号
		infoMap.put("MIN_END_BILL_NO",detail.getEndRangeNo());//票据结束子区间号
		infoMap.put("MAX_END_BILL_NO",detail.getEndRangeNo());//票据结束子区间号
		infoMap.put("QUERY_INFO_ARRAY.MAX_ACCEPTOR_DATE",detail.getIsseDate());//出票日期上限
		infoMap.put("QUERY_INFO_ARRAY.MIN_ACCEPTOR_DATE",detail.getIsseDate());//出票日期下限
		infoMap.put("QUERY_INFO_ARRAY.MAX_DUE_DATE",detail.getDueDate());//汇票到期日期上限
		infoMap.put("QUERY_INFO_ARRAY.MIN_DUE_DATE",detail.getDueDate());//汇票到期日期下限
		infoMap.put("QUERY_INFO_ARRAY.MAX_AMT",detail.getTradeAmt());//票据最高金额
		infoMap.put("QUERY_INFO_ARRAY.MIN_AMT",detail.getTradeAmt());//票据最低金额
		infoMap.put("QUERY_INFO_ARRAY.QUERY_TYPE","QT01");//查询类型
		infoMap.put("QUERY_INFO_ARRAY.TRAN_NO_LIST",PoolComm.NES_0022000);//交易编号集合
		note.getDetails().add(infoMap);
		
		ReturnMessageNew response = poolEcdsService.txApplyImplawnForSign(note);
		String status="";
		List list = response.getDetails();
		if(null != list && list.size()>0){
			//有数据表示需要撤销
			return true;
		}
		return false;
	}

	/**
	 * 同步合同信息
	 * @throws Exception 
	 * 
	 */
	public void txSyncContract(String onlineNo, String contractNo) throws Exception {
		try {
			OnlineQueryBean queryBean = new OnlineQueryBean();
			queryBean.setContractNo(contractNo);
			queryBean.setOnlineAcptNo(onlineNo);
			List list = this.queryOnlineAcptBatchList(queryBean,null);
			
			if(null != list && list.size()>0){
				PlOnlineAcptBatch batch = (PlOnlineAcptBatch) list.get(0);
				List<PlOnlineAcptDetail> details = this.queryOnlineAcptDetailByBatchId(batch.getId());
				for(PlOnlineAcptDetail detail:details){
					boolean succ = redisQueueCache.getLock(detail.getId(), String.valueOf(new Date()), 10);
					if(succ){
						/**
						 * 新加逻辑、日间同步核心未用退回的借据
						 */
						//已出票成功的数据客户做了未用退回、在网银调起合同同步时，去同步核心该借据的状态
						if(PublicStaticDefineTab.ONLINE_DS_003.equals(detail.getDealStatus())){
							PedCreditDetail creditDetail = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(null,detail.getLoanNo());
							if(creditDetail != null){
								draftPoolQueryService.txUpdateLoanByCoreforQuery(creditDetail);
							}
						}
						
						//已撤销：“成功”状态借据，若借据对应的电票完成了未用退回交易，电票状态变更为“已撤销”
						else if(PublicStaticDefineTab.ONLINE_DS_004.equals(detail.getDealStatus()) && !PublicStaticDefineTab.ACPT_DETAIL_012.equals(detail.getStatus())){
							AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, AutoTaskNoDefine.POOL_ONLINE_RELEASE, detail.getId());
							if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus()) && PublicStaticDefineTab.DELETE_FLAG_NO.equals(autoTaskExe.getDelFlag())){
								try {
									//次日未用退回
									if(null != detail.getCancelDate() && detail.getCancelDate().compareTo(DateUtils.getCurrDate())!=0){
										//确定非当日未用退回就不通知信贷，因为要记录未用退回时间
										//次日在线银承的合同项下借据发起未用退回，日终时取回银承票据状态，合同金融不发生变化
										List<String> releseIds = new ArrayList<String>();
										releseIds.add(detail.getId());
										

										detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
										//释放成功后结束任务：处理完成	
										autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
									}else{
										
									//当日未用退回
										//1.查询合同项下的借据是否有撤销的   ||若为“已撤销”，释放在线银承额度、收票人额度、票据池低风险额度、票据池担保合同额度，并通知信贷系统释放
										//2.如果没有释放，在网银通知票据池系统时，就发布了释放额度的自动任务，判断是否有自动任务在执行
										//3.如果没有直接释放额度
										List<String> releseIds = new ArrayList<String>();
										releseIds.add(detail.getId());
										/*
										 * 池额度释放
										 */
										financialService.txOnlineBusiReleseCredit(releseIds, detail.getBpsNo());
										detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_002_2);
										
										/*
										 * 通知信贷释放额度
										 */
										//信贷额度
										ReturnMessageNew result = this.txPJE021Handler(batch, "1", PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE, null);
										if(result.isTxSuccess()){

											detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
											//释放成功后结束任务：处理完成	
											autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
										}else{
											detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
										}
									}
									this.dao.store(detail);
								} catch (Exception e) {
									this.dao.store(detail);
									logger.error("票号："+detail.getBillNo()+"网银同步合同信息》未用退回额度释放异常", e);
								}
							}
						}else 
						//处理中：1.银承记账成功，但是后续“提示承兑签收+解锁+提示收票人收票（若有）”未成功；2.ESB未反馈记账状态（通讯异常或失败）
						if(PublicStaticDefineTab.ONLINE_DS_001.equals(detail.getDealStatus())){
							
							if(PublicStaticDefineTab.ACPT_DETAIL_007_2.equals(detail.getStatus())){//签收失败
								AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_SIGN_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT, detail.getId());
								if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus()) && PublicStaticDefineTab.DELETE_FLAG_NO.equals(autoTaskExe.getDelFlag())){
									try {
										logger.info("PJC065同步合同 承兑签收失败处理后续流程 -查证 -查看签收  后续-----------------");
										this.txRepeatAcceptionSign(detail, batch);
										//释放成功后结束任务：处理完成	
										autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
									} catch (Exception e) {
										logger.error("票号："+detail.getBillNo()+"网银同步合同状态》签收失败",e);
									}
									
								}
							}else if(PublicStaticDefineTab.ACPT_DETAIL_008_2.equals(detail.getStatus())){//收票失败
								AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_SEND_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT_RECEIVE, detail.getId());
								if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus()) && PublicStaticDefineTab.DELETE_FLAG_NO.equals(autoTaskExe.getDelFlag())){
									try {
										boolean succ1 = this.txApplyReceiveBill(detail,batch);
										if(succ1){
											//成功变更状态
											detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_008);//提示收票申请
											this.dao.store(detail);
											autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
										}
									} catch (Exception e) {
										logger.error("票号："+detail.getBillNo()+"网银同步合同状态》提示收票失败",e);
									}
								}
							}else if(PublicStaticDefineTab.ACPT_DETAIL_007.equals(detail.getStatus())){//承兑签收申请 未收到BBSP通知
								try {
									logger.info("PJC065同步合同 承兑签收未收到bbsp处理后续流程 -查证 -查看签收  后续-----------------");
									this.txRepeatAcceptionSign(detail,batch);
								} catch (Exception e) {
									logger.info("承兑签收重复执行出错:"+e);
									continue;
								}
							}
									

							
						}else 
						//失败：未释放额度：释放额度失败或ESB未反馈
						if(PublicStaticDefineTab.ONLINE_DS_002.equals(detail.getDealStatus())){
							//状态可能来自  新增、撤票、未用退回（登记成功后的）、额度释放
							//新增
							PoolQueryBean bean = new PoolQueryBean();
							List queueNode = new ArrayList();
							queueNode.add(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO);
							queueNode.add(AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01);
							queueNode.add(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01);
							queueNode.add(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO);
							List queueName = new ArrayList();
							queueName.add(AutoTaskNoDefine.POOL_ONLINE_ACPT);
							queueName.add(AutoTaskNoDefine.POOL_ONLINE_ACPT_UNUSED);
							queueName.add(AutoTaskNoDefine.POOL_ONLINE_ACPT_CANCLE);
							queueName.add(AutoTaskNoDefine.POOL_ONLINE_RELEASE);
							
							AutoTaskExe autoTaskExe1 =autoTaskExeService.doAutoTaskExe(queueNode, queueName, detail.getId(),"4");
							if(null != autoTaskExe1 ){
								/*
								 * 通知信贷释放额度
								 */
								ReturnMessageNew result = this.txPJE021Handler(batch, "1", PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE, null);
								if(result.isTxSuccess()){

									detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
									//释放成功后结束任务：处理完成	
									autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe1.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
								}else{
									detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
								}
							}
						}else
						//失败：未到记账环节或者记账失败
						if(PublicStaticDefineTab.ONLINE_DS_005.equals(detail.getDealStatus())){
							AutoTaskExe autoTaskExe =autoTaskExeService.doAutoTaskExe(null, null, detail.getId(),"4");
							if(null != autoTaskExe){
								//新增失败
								//登记失败
								//额度占用失败
								//额度释放失败
								//提示承兑失败
								//未用退回失败
								//承兑签收失败
								//提示收票异常
								TaskDispatchConfig config = (TaskDispatchConfig) this.dao.load(TaskDispatchConfig.class, autoTaskExe.getTaskId());
								autoTaskPublishService.publishWaitTask(autoTaskExe.getMemberCode(), config.getQueueNode(),
					                        detail.getId(),config.getProductId(),null);
								 //更新流程状态
					            autoTaskExe.setProceStatus(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_INIT);
					            this.dao.store(autoTaskExe);
							}
						}
					}
				}
			}else{
				throw new Exception("未查询到对应合同信息！");
			}
		} catch (Exception e) {
			logger.error("网银同步合同信息", e);
		}
	}

	/**
	 * 同步借据信息
	 */
	public void txSyncPedCreditDetail(PlOnlineAcptDetail detail) {
		try {
				PlOnlineAcptBatch batch = (PlOnlineAcptBatch) this.dao.load(PlOnlineAcptBatch.class, detail.getAcptBatchId());
				boolean succ = redisQueueCache.getLock(detail.getId(), String.valueOf(new Date()), 10);
				if(succ){
					
					//已撤销：“成功”状态借据，若借据对应的电票完成了未用退回交易，电票状态变更为“已撤销”
					if(PublicStaticDefineTab.ONLINE_DS_004.equals(detail.getDealStatus()) && !PublicStaticDefineTab.ACPT_DETAIL_012.equals(detail.getStatus())){
						AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, AutoTaskNoDefine.POOL_ONLINE_RELEASE, detail.getId());
						if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus()) && PublicStaticDefineTab.DELETE_FLAG_NO.equals(autoTaskExe.getDelFlag())){
							try {
								//次日未用退回
								if(null != detail.getCancelDate() && detail.getCancelDate().compareTo(DateUtils.getCurrDate())!=0){
									//次日在线银承的合同项下借据发起未用退回，日终时取回银承票据状态，合同金融不发生变化
									List<String> releseIds = new ArrayList<String>();
									releseIds.add(detail.getId());

									detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
									//释放成功后结束任务：处理完成	
									autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
								}else{
									
								//当日未用退回
									//1.查询合同项下的借据是否有撤销的   ||若为“已撤销”，释放在线银承额度、收票人额度、票据池低风险额度、票据池担保合同额度，并通知信贷系统释放
									//2.如果没有释放，在网银通知票据池系统时，就发布了释放额度的自动任务，判断是否有自动任务在执行
									//3.如果没有直接释放额度
									List<String> releseIds = new ArrayList<String>();
									releseIds.add(detail.getId());
									/*
									 * 池额度释放
									 */
									financialService.txOnlineBusiReleseCredit(releseIds, detail.getBpsNo());
									detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_002_2);
									
									/*
									 * 通知信贷释放额度
									 */
									//信贷额度
									ReturnMessageNew result = this.misRepayAcptPJE028(batch);
									if(result.isTxSuccess()){

										detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
										//释放成功后结束任务：处理完成	
										autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
									}else{
										detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
									}
								}
								this.dao.store(detail);
							} catch (Exception e) {
								this.dao.store(detail);
								logger.error("票号："+detail.getBillNo()+"网银同步合同信息》未用退回额度释放异常", e);
							}
						}
					}else 
					//处理中：1.银承记账成功，但是后续“提示承兑签收+解锁+提示收票人收票（若有）”未成功；2.ESB未反馈记账状态（通讯异常或失败）
					if(PublicStaticDefineTab.ONLINE_DS_001.equals(detail.getDealStatus())){
						
						if(PublicStaticDefineTab.ACPT_DETAIL_007_2.equals(detail.getStatus())){//签收失败
							AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_SIGN_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT, detail.getId());
							if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus()) && PublicStaticDefineTab.DELETE_FLAG_NO.equals(autoTaskExe.getDelFlag())){
								try {
									logger.info("PJC066同步借据 承兑签收未收到处理后续流程 -查证 -查看签收  后续-----------------");
									this.txRepeatAcceptionSign(detail, batch);
									//释放成功后结束任务：处理完成	
									autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
								} catch (Exception e) {
									logger.error("票号："+detail.getBillNo()+"网银同步合同状态》签收失败",e);
								}
								
							}
						}else if(PublicStaticDefineTab.ACPT_DETAIL_008_2.equals(detail.getStatus())){//收票失败
							AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_SEND_NO, AutoTaskNoDefine.POOL_ONLINE_ACPT_RECEIVE, detail.getId());
							if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus()) && PublicStaticDefineTab.DELETE_FLAG_NO.equals(autoTaskExe.getDelFlag())){
								try {
									boolean succ1 = this.txApplyReceiveBill(detail,batch);
									if(succ1){
										//成功变更状态
										detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_008);//提示收票申请
										this.dao.store(detail);
										autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
									}
								} catch (Exception e) {
									logger.error("票号："+detail.getBillNo()+"网银同步合同状态》提示收票失败",e);
								}
							}
						}else if(PublicStaticDefineTab.ACPT_DETAIL_007.equals(detail.getStatus())){//承兑签收申请 未收到BBSP通知
							try {
								logger.info("PJC065同步合同 承兑签收未收到bbsp处理后续流程 -查证 -查看签收  后续-----------------");
								this.txRepeatAcceptionSign(detail,batch);
							} catch (Exception e) {
								logger.info("承兑签收重复执行出错:"+e);
							}
						}
						
					}else 
					//失败：未释放额度：释放额度失败或ESB未反馈
					if(PublicStaticDefineTab.ONLINE_DS_002.equals(detail.getDealStatus())){
						//状态可能来自  新增、撤票、未用退回（登记成功后的）、额度释放
						//新增
						PoolQueryBean bean = new PoolQueryBean();
						List queueNode = new ArrayList();
						queueNode.add(AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO);
						queueNode.add(AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01);
						queueNode.add(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01);
						queueNode.add(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO);
						List queueName = new ArrayList();
						queueName.add(AutoTaskNoDefine.POOL_ONLINE_ACPT);
						queueName.add(AutoTaskNoDefine.POOL_ONLINE_ACPT_UNUSED);
						queueName.add(AutoTaskNoDefine.POOL_ONLINE_ACPT_CANCLE);
						queueName.add(AutoTaskNoDefine.POOL_ONLINE_RELEASE);
						
						AutoTaskExe autoTaskExe1 =autoTaskExeService.doAutoTaskExe(queueNode, queueName, detail.getId(),"4");
						if(null != autoTaskExe1 ){
							/*
							 * 通知信贷释放额度
							 */
							ReturnMessageNew result = this.txPJE021Handler(batch, "1", PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE, null);
							if(result.isTxSuccess()){

								detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
								//释放成功后结束任务：处理完成	
								autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe1.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
							}else{
								detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
							}
						}
					}else
					//失败：未到记账环节或者记账失败
					if(PublicStaticDefineTab.ONLINE_DS_005.equals(detail.getDealStatus())){
						AutoTaskExe autoTaskExe =autoTaskExeService.doAutoTaskExe(null, null, detail.getId(),"4");
						if(null != autoTaskExe){
							//新增失败
							//登记失败
							//额度占用失败
							//额度释放失败
							//提示承兑失败
							//未用退回失败
							//承兑签收失败
							//提示收票异常
							TaskDispatchConfig config = (TaskDispatchConfig) this.dao.load(TaskDispatchConfig.class, autoTaskExe.getTaskId());
							autoTaskPublishService.publishWaitTask(autoTaskExe.getMemberCode(), config.getQueueNode(),
				                        detail.getId(),config.getProductId(),null);
							 //更新流程状态
				            autoTaskExe.setProceStatus(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_INIT);
				            this.dao.store(autoTaskExe);
						}
					}else if (PublicStaticDefineTab.ONLINE_DS_003.equals(detail.getDealStatus())){//成功
						/**
						 * 新加逻辑、日间同步核心未用退回的借据
						 */
						//已出票成功的数据客户做了未用退回、在网银调起合同同步时，去同步核心该借据的状态
						
						/**
						 * 查询银承明细对应的 PedCreditDetail 表
						 * 如果 PedCreditDetail 是 null 则不处理
						 * 如果不是null则调用
						 */
						CreditQueryBean queryBean = new CreditQueryBean();
						queryBean.setLoanNo(detail.getLoanNo());
						List result =poolCreditProductService.queryCreditDetailList(queryBean);
						if(result != null && result.size() > 0){
							PedCreditDetail acpt = (PedCreditDetail) result.get(0);
							draftPoolQueryService.txUpdateLoanByCoreforQuery(acpt);
						}
					}
				}
			
			
			//判断合同项下单张借据 根据状态，就是逐个判断  每一种额度释放了没有 
			//查询合同项下的借据是否有撤销的   ||若为“已撤销”，释放在线银承额度、收票人额度、票据池低风险额度、票据池担保合同额度，并通知信贷系统释放
			//TODO 额度释放
			//先校验额度是否释放
			//如果没有释放，在网银通知票据池系统时，就发布了释放额度的自动任务，判断是否有自动任务在执行
			//如果没有直接释放额度
			
		} catch (Exception e) {
			logger.error("借据号:"+detail.getLoanNo()+"网银同步借据异常", e);
		}
	}

	/**
	 * 提示承兑申请
	 */
	public boolean txApplyAcception(PlOnlineAcptDetail detail,String elctrncSign) throws Exception {
		ECDSPoolTransNotes note = new ECDSPoolTransNotes();
    	try {
    		
    		/**
    		 * body内需要传送的值
    		 */
    		note.setApplicantAcctNo(detail.getIssuerAcct());//电票签约帐号  多账号|拼接
    		note.setSignature(elctrncSign);//电子签名
    		
    		/**
    		 * 票据信息数组需传送的值
    		 */
    		Map infoMap = new HashMap();
    		infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",detail.getDraftSource());//票据来源 
    		infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0022000);//交易编号 提示承兑
    		infoMap.put("BILL_INFO_ARRAY.BILL_NO",detail.getBillNo());//票据（包）号码
    		infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",detail.getHilrId());//持票id
    		infoMap.put("BILL_INFO_ARRAY.BILL_ID",detail.getBillId());//票据id
    		infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO",detail.getLoanNo());//流水
    		infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","1");//经办锁类型 0-未经办锁票 1-已经办锁票

    		infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
    		note.getDetails().add(infoMap);
    		
    		
    		ReturnMessageNew response = poolEcdsService.txApplyImplawn(note);
    		String responseCode = response.getRet().getRET_CODE();
    		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
    			return true;
    		}else{
    			return false;
    		}
		} catch (Exception e) {
			logger.error("票号："+detail.getBillNo()+"提示承兑申请失败！", e);
			throw new Exception(e);
		}
	}

	/**
	 * 判断银承批次状态
	 */
	public void txSyncAcptBatchStatus(PlOnlineAcptBatch batch) {
		boolean succ = true;//成功
		boolean fail = true;//失败
		boolean ing = true;//处理中
		boolean cancel = true;//已撤销
		String dealStatu = "";
		String status = "";
		List<PlOnlineAcptDetail> details = this.queryOnlineAcptDetailByBatchId(batch.getId());
		for(PlOnlineAcptDetail detail:details){
			if(PublicStaticDefineTab.ONLINE_DS_005.equals(detail.getDealStatus())){
				fail = false;
			}
			if(PublicStaticDefineTab.ONLINE_DS_003.equals(detail.getDealStatus())){
				succ = false;
			}
			if(PublicStaticDefineTab.ONLINE_DS_001.equals(detail.getDealStatus())){
				ing = false;
			}
			if(PublicStaticDefineTab.ONLINE_DS_004.equals(detail.getDealStatus())){
				cancel = false;
			}
		}
		if(!succ && fail && ing){
			//成功
			dealStatu = PublicStaticDefineTab.ONLINE_DS_003;
			status = PublicStaticDefineTab.ACPT_BATCH_005;
		}else if(!fail && succ && ing ){
			//失败
			dealStatu = PublicStaticDefineTab.ONLINE_DS_005;
			status = PublicStaticDefineTab.ACPT_BATCH_006;
		}else if(!fail && !ing){
			//部分失败
			dealStatu = PublicStaticDefineTab.ONLINE_DS_007;
			status = PublicStaticDefineTab.ACPT_BATCH_006_1;
		}else if(!succ && !ing){
			//部分成功
			dealStatu = PublicStaticDefineTab.ONLINE_DS_006;
			status = PublicStaticDefineTab.ACPT_BATCH_005_1;
		}else if(!ing && succ && fail){
			//处理中
			dealStatu = PublicStaticDefineTab.ONLINE_DS_001;
		}else if(!cancel && ing && succ && fail){
			//已撤销
			dealStatu = PublicStaticDefineTab.ONLINE_DS_004;
		}else if(ing && !succ && !fail){
			//部分成功部分失败
			dealStatu = PublicStaticDefineTab.ONLINE_DS_007;
			status = PublicStaticDefineTab.ACPT_BATCH_006_1;
		}
		if(StringUtils.isNotBlank(dealStatu)){
			batch.setDealStatus(dealStatu);//处理状态
		}
		if(StringUtils.isNotBlank(status)){
			batch.setStatus(status);//业务状态
		}
		this.txStore(batch);
	}

	/**
	 * 在线银承合同查询
	 */
	public List queryOnlineAcptContractList(OnlineQueryBean queryBean, Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select batch,pro from PlOnlineAcptBatch batch,PedOnlineAcptProtocol pro where  batch.onlineAcptNo=pro.onlineAcptNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and pro.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and pro.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and pro.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and batch.createTime >=to_date(:startDate,'yyyy-MM-dd')");
			paramName.add("startDate");
			paramValue.add(DateUtils.toString(queryBean.getStartDate(),"yyyy-MM-dd"));
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and batch.createTime <=:endDate ");
			paramName.add("endDate");
			paramValue.add(DateUtils.getCurrentDayEndDate(queryBean.getEndDate()));
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and batch.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		hql.append(" order by batch.createTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues,page );
		if(result != null && result.size() > 0){
			List list = new ArrayList();
			for (int i = 0; i < result.size(); i++) {
				OnlineQueryBean bean = new OnlineQueryBean();
				Object[] obj = (Object[]) result.get(i);
				PlOnlineAcptBatch batch = (PlOnlineAcptBatch) obj[0];
				PedOnlineAcptProtocol pro=(PedOnlineAcptProtocol) obj[1];
				bean.setOnlineProtocolType(PublicStaticDefineTab.PRODUCT_YC);//在线银承
				bean.setOnlineAcptNo(batch.getOnlineAcptNo());     //在线业务协议编号  
				bean.setContractNo(batch.getContractNo());      //在线业务合同号   
				bean.setUnusedAmt(batch.getTotalAmt());//业务余额
				bean.setChangeDate(batch.getCreateTime());//操作时间
				bean.setStatus(batch.getDealStatus());//状态
				list.add(bean);
			}
			return list;
		}
		return null;
		

	
	}

	@Override
	public List queryOnlineAcptPtlHist(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct dto from PedOnlineAcptProtocolHist dto,PedOnlineAcptProtocol pro where dto.onlineAcptNo=pro.onlineAcptNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
			hql.append(" and pro.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineAcptNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and pro.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and pro.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and pro.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getModeMark())){
			hql.append(" and dto.modeMark =:modeMark");
			paramName.add("modeMark");
			paramValue.add(queryBean.getModeMark());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineAcptProtocolHist> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}

	@Override
	public List queryOnlinePayeeHistList(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineAcptInfoHist dto,PedOnlineAcptProtocol pro where dto.onlineAcptNo = pro.onlineAcptNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			hql.append(" and pro.onlineAcptNo =:onlineAcptNo");
			paramName.add("onlineAcptNo");
			paramValue.add(queryBean.getOnlineNo());
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			hql.append(" and pro.bpsNo =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(queryBean.getBpsNo());
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and pro.ebkCustNo like :ebkCustNo");
			paramName.add("ebkCustNo");
			paramValue.add("%"+queryBean.getEbkCustNo()+"%");
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and pro.crdtDueDt <=:endDate");
			paramName.add("endDate");
			paramValue.add(queryBean.getEndDate());
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and pro.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getModeMark())){
			hql.append(" and dto.modeMark =:modeMark");
			paramName.add("modeMark");
			paramValue.add(queryBean.getModeMark());
		}
		if(StringUtils.isNotBlank(queryBean.getLastSourceId())){
			hql.append(" and dto.lastSourceId =:lastSourceId");
			paramName.add("lastSourceId");
			paramValue.add(queryBean.getLastSourceId());
		}
		//收票人名称
		if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
			hql.append(" and dto.payeeAcctName =:payeeAcctName");
			paramName.add("payeeAcctName");
			paramValue.add(queryBean.getPayeeAcctName());
		}
		//收票人编号
		if(StringUtils.isNotBlank(queryBean.getPayeeId())){
			hql.append(" and dto.payeeId =:payeeId");
			paramName.add("payeeId");
			paramValue.add(queryBean.getPayeeId());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineAcptInfoHist> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}

	@Override
	public PedOnlineAcptProtocolHist compareDto(PedOnlineAcptProtocolHist hist,
			PedOnlineAcptProtocolHist last) {
		if(StringUtil.compareString(hist.getOnlineAcptNo(),last.getOnlineAcptNo())){
			last.setOnlineAcptNo(null);
		}
		if(StringUtil.compareString(hist.getCustName(),last.getCustName())){
			last.setCustName(null);
		}
		if(StringUtil.compareString(hist.getProtocolStatus(),last.getProtocolStatus())){
			last.setProtocolStatus(null);
		}
		if(StringUtil.compareString(hist.getBpsNo(),last.getBpsNo())){
			last.setBpsNo(null);
		}
		if(StringUtil.compareString(hist.getBaseCreditNo(),last.getBaseCreditNo())){
			last.setBaseCreditNo(null);
		}
		if(StringUtil.compareString(hist.getEbkCustNo(),last.getEbkCustNo())){
			last.setEbkCustNo(null);
		}
		if(hist.getOnlineAcptTotal().equals(last.getOnlineAcptTotal())){
			last.setOnlineAcptTotal(null);
		}
		if(StringUtil.compareString(hist.getAcceptorBankNo(),last.getAcceptorBankNo())){
			last.setAcceptorBankNo(null);
		}
		if(StringUtil.compareString(hist.getAcceptorBankName(),last.getAcceptorBankName())){
			last.setAcceptorBankName(null);
		}
		if(StringUtil.compareString(hist.getDepositAcctNo(),last.getDepositAcctNo())){
			last.setDepositAcctNo(null);
		}
		if(StringUtil.compareString(hist.getDepositAcctName(),last.getDepositAcctName())){
			last.setDepositAcctName(null);
		}
		if(StringUtil.compareString(hist.getDepositRateLevel(),last.getDepositRateLevel())){
			last.setDepositRateLevel(null);
		}
		if(StringUtil.compareString(hist.getDepositRateFloatFlag(),last.getDepositRateFloatFlag())){
			last.setDepositRateFloatFlag(null);
		}
		if(StringUtil.compareBigdecimal(hist.getDepositRateFloatValue(),(last.getDepositRateFloatValue()))){
			last.setDepositRateFloatValue(null);
		}
		if(StringUtil.compareBigdecimal(hist.getPoolCreditRatio(),(last.getPoolCreditRatio()))){
			last.setPoolCreditRatio(null);
		}
		if(StringUtil.compareBigdecimal(hist.getDepositRatio(),(last.getDepositRatio()))){
			last.setDepositRatio(null);
		}
		if(StringUtil.compareBigdecimal(hist.getFeeRate(),(last.getFeeRate()))){
			last.setFeeRate(null);
		}
		if(StringUtil.compareString(hist.getInAcctBranchNo(),last.getInAcctBranchNo())){
			last.setInAcctBranchNo(null);
		}
		if(StringUtil.compareString(hist.getInAcctBranchName(),last.getInAcctBranchName())){
			last.setInAcctBranchName(null);
		}
		if(StringUtil.compareString(hist.getContractNo(),last.getContractNo())){
			last.setContractNo(null);
		}	
		if(StringUtil.compareString(hist.getAppName(),last.getAppName())){
			last.setAppName(null);
		}
		if(StringUtil.compareString(hist.getAppNo(),last.getAppNo())){
			last.setAppNo(null);
		}
		if(StringUtil.compareString(hist.getSignBranchNo(),last.getSignBranchNo())){
			last.setSignBranchNo(null);
		}
		if(StringUtil.compareString(hist.getSignBranchName(),last.getSignBranchName())){
			last.setSignBranchName(null);
		}
		if(StringUtil.compareString(hist.getGuarantorNo(),last.getGuarantorNo())){
			last.setGuarantorNo(null);
		}
		if(DateUtils.compareDate(hist.getChangeDate(), last.getChangeDate())==0){
			last.setChangeDate(null);
		}
		if(DateUtils.compareDate(hist.getDueDate(), last.getDueDate())==0){
			last.setDueDate(null);
		}
		return last;
	}

	/**
	 * 银承明细导出
	 */
	public List loadAcptDetailToExpt(OnlineQueryBean bean, Page page) {
		List list = this.queryPlOnlineAcptDetailList(bean, page);
		List result = new ArrayList(); 
		if(null != list && list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				String s[] = new String[15];
				PlOnlineAcptDetail bill = (PlOnlineAcptDetail) list.get(i);
				s[0]=bill.getBillNo();
				s[1]=bill.getLoanNo();
				s[2]=String.valueOf(bill.getBillAmt());
				s[3]=DateUtils.toString(bill.getIsseDate(), "yyyy-MM-dd");
				s[4]=DateUtils.toString(bill.getDueDate(), "yyyy-MM-dd");
				s[5]=bill.getIssuerName();
				s[6]=bill.getIssuerBankName();
				s[7]=bill.getIssuerBankCode();
				s[8]=bill.getPayeeName();
				s[9]=bill.getPayeeAcct();
				s[10]=bill.getPayeeBankName();
				s[11]=bill.getPayeeBankCode();
				s[12]=bill.getAcptBankCode();
				s[13]=bill.getAcptBankName();
				s[14]=bill.getTransferFlag();
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * 银承缓存明细导出
	 */
	public List loadAcptCacheDetailToExpt(OnlineQueryBean bean, Page page) {
		List list = this.queryPlOnlineAcptCacheDetailList(bean, page);
		List result = new ArrayList(); 
		if(null != list && list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				String s[] = new String[15];
				PlOnlineAcptCacheDetail bill = (PlOnlineAcptCacheDetail) list.get(i);
				s[0]=bill.getBillNo();
				s[1]=bill.getLoanNo();
				s[2]=String.valueOf(bill.getBillAmt());
				s[3]=DateUtils.toString(bill.getIsseDate(), "yyyy-MM-dd");
				s[4]=DateUtils.toString(bill.getDueDate(), "yyyy-MM-dd");
				s[5]=bill.getIssuerName();
				s[6]=bill.getIssuerBankName();
				s[7]=bill.getIssuerBankCode();
				s[8]=bill.getPayeeName();
				s[9]=bill.getPayeeAcct();
				s[10]=bill.getPayeeBankName();
				s[11]=bill.getPayeeBankCode();
				s[12]=bill.getAcptBankCode();
				s[13]=bill.getAcptBankName();
				s[14]=bill.getTransferFlag();
				result.add(s);
			}
		}
		return result;
	}
	
	@Override
	public CreditProduct creatProductByAcptBatch(PlOnlineAcptBatch acptBatch,PedProtocolDto pro) {
		
		CreditProduct product = new CreditProduct();
		Date dueDate = DateUtils.getNextNYear(new Date(), 1);//在线银承的合同到期日
		logger.info("银承到期日："+dueDate);
		product.setId(acptBatch.getId());
		product.setCrdtNo(acptBatch.getContractNo());//信贷业务号
		product.setCrdtType(PoolComm.XD_01);//信贷产品类型  XD_01银承 
		product.setUseAmt(acptBatch.getTotalAmt());//信贷产品合同金额(不含占用比例)
		product.setCrdtIssDt(acptBatch.getApplyDate());//借款日期
		product.setCrdtDueDt(dueDate);//到期日--起始日加一年
		product.setCustNo(acptBatch.getCustNo());//核心客户号
		product.setCustName(pro.getCustname());//客户名称名称
		product.setSttlFlag(PoolComm.JQ_01);//结清标记  JQ_01：未结清
		product.setCrdtStatus(PoolComm.RZCP_YQS);//业务状态   RZ_03：额度占用成功   JQ_00 已结清   存储MIS系统发过来的状态：JQ_01 取消放贷  JQ_02 手工提前终止出账   JQ_03 合同到期    JQ_04 合同终止
		product.setReleaseAmt(new BigDecimal(0));//已还额度
		product.setCcupy("1");//占用比例--在线银承为100%
		product.setRestAmt(acptBatch.getTotalAmt());//业务余额:表示该融资业务剩余的待还金额
		product.setRisklevel(PoolComm.LOW_RISK);//风险等级	FX_01 高风险产品 
		product.setBpsNo(pro.getPoolAgreement());//票据池编号
		product.setIsOnline(PoolComm.YES);//是否线上 1 是 0 否
		product.setMinDueDate(dueDate);//借据最早到期日
		
		return product;
	}

	@Override
	public PedCreditDetail creatCrdtDetailByAcptDetail(PlOnlineAcptDetail acptDetail,PlOnlineAcptBatch acptBatch,PedProtocolDto pro) {
		PedCreditDetail crdtDetail = new PedCreditDetail();
		crdtDetail.setCreditDetailId(Long.toString(System.currentTimeMillis()));
		crdtDetail.setCrdtNo(acptBatch.getContractNo());//信贷产品号：信贷业务合同号
		crdtDetail.setCustNumber(acptBatch.getCustNo());//客户号
		crdtDetail.setCustName(pro.getCustname());//客户名称
		crdtDetail.setLoanNo(acptDetail.getLoanNo());//借据号
		crdtDetail.setTransTime(new Date());//交易时间
		crdtDetail.setLoanType(PoolComm.XD_01);//交易类型   （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
		crdtDetail.setLoanStatus(PoolComm.JJ_01);//交易状态（JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清  JJ_05:未用退回（已撤销） ）
//	     crdtDetail.settransAccount;//交易账号 （表外业务对应业务保证金账号，表内业务对应贷款账号）
		crdtDetail.setLoanAmount(acptDetail.getBillAmt());//借据金额
		crdtDetail.setLoanBalance(acptDetail.getBillAmt());//借据余额=借据应还本金
//	     crdtDetail.setrepaymentTime;//还款时间
		crdtDetail.setPenaltyAmount(BigDecimal.ZERO);//罚息金额
		crdtDetail.setActualAmount(acptDetail.getBillAmt().multiply(acptBatch.getPoolCreditRatio()).divide(new BigDecimal("100")));//实际占用金额=借据余额*票据池占用比例 %
		crdtDetail.setBailAccAmt(BigDecimal.ZERO);//保证金余额,用于银承
		crdtDetail.setDetailStatus(PoolComm.LOAN_1);//借据状态,0-不在处理,1-还需处理
		crdtDetail.setStartTime(acptDetail.getIsseDate());//借据起始日
		Date date = DateUtils.formatDate(acptDetail.getDueDate(), DateUtils.ORA_DATE_FORMAT);
		crdtDetail.setEndTime(date);//借据到期日
		crdtDetail.setIfAdvanceAmt(PoolComm.NO); //是否垫款0： 否 1: 是'
		crdtDetail.setLoanAdvanceNo(null);//垫款借据号
		crdtDetail.setInitialMarginAmt(acptDetail.getBillAmt().multiply(acptBatch.getDepositRatio()).divide(new BigDecimal("100")));//初始业务保证金金额
		
		return crdtDetail;
		
	}

	@Override
	public List<String> queryOnlineAcptDetailIdList(String batchId) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select detail.id from PlOnlineAcptDetail detail  where 1=1 ");
		hql.append(" and detail.acptBatchId =:batchId");
		paramName.add("batchId");
		paramValue.add(batchId);
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<String> result = this.find(hql.toString(), paramNames, paramValues );
		
		if(result != null && result.size()>0){			
			return result;
		}
		
		return null;
	}

	@Override
	public PedOnlineAcptInfo queryonlinePayeeByBean(OnlineQueryBean queryBean) {
		String sql ="select dto from PedOnlineAcptInfo dto,PedOnlineAcptProtocol info where dto.acptId=info.id ";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineAcptNo())){
				sql= sql+" and info.onlineAcptNo=?";
				param.add(queryBean.getOnlineAcptNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				sql= sql+" and dto.payeeStatus=?";
				param.add(queryBean.getPayeeStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getAcptId())){
				sql= sql+" and dto.acptId=?";
				param.add(queryBean.getAcptId());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and info.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
				sql= sql+" and dto.payeeAcctName  like ?";
				param.add("%"+queryBean.getPayeeAcctName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctNo())){
				sql= sql+" and dto.payeeAcctNo =?";
				param.add(queryBean.getPayeeAcctNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankNo())){
				sql= sql+" and dto.payeeOpenBankNo =?";
				param.add(queryBean.getPayeeOpenBankNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankName())){
				sql= sql+" and dto.payeeOpenBankName  like ?";
				param.add("%"+queryBean.getPayeeOpenBankName()+"%");
			}
		}
		List result = this.find(sql, param);
		if(null != result && result.size()>0){
			return (PedOnlineAcptInfo) result.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 银承协议和收票人额度占用
	 * @throws Exception 
	 * @param usedType 占用类型 02：释放 01：占用
	 */
	public Ret txLocalLimitUsed(PlOnlineAcptBatch batch,List<PlOnlineAcptDetail> details,String usedType) throws Exception {
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		if(StringUtils.isNotBlank(usedType)){
			OnlineQueryBean bean = new OnlineQueryBean();
			bean.setOnlineAcptNo(batch.getOnlineAcptNo());
			List list = this.queryOnlineAcptProtocolList(bean, null);
			if(null != list && list.size()>0){
				bean = (OnlineQueryBean) list.get(0);
			}
			PedOnlineAcptProtocol protocol = this.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
			if(PublicStaticDefineTab.LIMIT_USED.equals(usedType)){
				protocol.setUsedAmt(bean.getUsedAmt().add(batch.getTotalAmt()));
				for(PlOnlineAcptDetail detail:details){
					OnlineQueryBean queryBean = new OnlineQueryBean();
					queryBean.setOnlineAcptNo(batch.getOnlineAcptNo());
					queryBean.setPayeeAcctName(detail.getPayeeName());
					queryBean.setPayeeAcctNo(detail.getPayeeAcct());
					PedOnlineAcptInfo info = this.queryonlinePayeeByBean(queryBean);
					if(null != info){
						if(info.getPayeeTotalAmt().subtract(info.getPayeeUsedAmt()).compareTo(detail.getBillAmt())<0){
							throw new Exception("收票人"+detail.getPayeeName()+"额度不足|");
						}
						info.setPayeeUsedAmt(info.getPayeeUsedAmt().add(detail.getBillAmt()));
						this.dao.store(info);
					}else{
						throw new Exception("收票人"+detail.getPayeeName()+"不存在！|");
					}
				}
			}else{
				BigDecimal reduceAmt = new BigDecimal(0);
				for(PlOnlineAcptDetail detail:details){
					OnlineQueryBean queryBean = new OnlineQueryBean();
					queryBean.setOnlineAcptNo(batch.getOnlineAcptNo());
					queryBean.setPayeeAcctName(detail.getPayeeName());
					queryBean.setPayeeAcctNo(detail.getPayeeAcct());
					PedOnlineAcptInfo info = this.queryonlinePayeeByBean(queryBean);
					if(null != info){
						if(info.getPayeeUsedAmt().compareTo(detail.getBillAmt())<0){
							throw new Exception("收票人"+detail.getPayeeName()+"额度不足|");
						}
						info.setPayeeUsedAmt(info.getPayeeUsedAmt().subtract(detail.getBillAmt()));
						this.dao.store(info);
						reduceAmt = reduceAmt.add(detail.getBillAmt());
					}else{
						throw new Exception("收票人"+detail.getPayeeName()+"不存在！|");
					}
				}
				protocol.setUsedAmt(bean.getUsedAmt().subtract(reduceAmt));
				batch = this.calculateBatchAmt(batch);
			}
			this.dao.store(protocol);
			this.dao.store(batch);
		}else{
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("未告知额度操作类型！");
		}
		return ret;
	}
	
	/**
	 * @Title txRepeatAcceptionSign
	 * @author wss
	 * @date 2021-7-17
	 * @Description 承兑签收重复执行
	 * @return void
	 * @throws Exception 不在此方法里处理异常，放在调用层处理异常
	 */
	public void txRepeatAcceptionSign(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch) throws Exception {
		detail.setTaskDate(new Date());
		//驱动查询核心是否记账
		String acctStatus = this.txApplyQueryAcctStatus(detail,batch.getContractNo());
		if("1".equals(acctStatus)){//已记账
			String status ="";
			try{
				status = this.txApplyQueryBill(detail,"2",PoolComm.NES_0022000);
			}catch (Exception e) {
				logger.error(detail.getBillNo()+"查询bbsp票据信息异常...",e);
				throw new Exception(detail.getBillNo()+"查询bbsp票据信息异常"+e.getMessage());
			}
			//判断状态
			if(StringUtils.isBlank(status) || "3".equals(status)){//发送失败
				//记录日志
				onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "提示承兑签收失败", "BBSP009", PublicStaticDefineTab.ACPT_BUSI_NAME_10, "send");
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_2);//提示承兑签收申请失败
				//记账成功+签收失败：继续驱动签收，仍然发承兑签收接口
				boolean succ = this.txApplyAcptSign(detail,batch.getElctrncSign(),"0");
				if(succ){
					//成功变更状态
					detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007);//提示承兑签收申请
				}else{
					//记录日志
					onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "提示承兑签收申请失败", "BBSP009", PublicStaticDefineTab.ACPT_BUSI_NAME_10, "send");
					//4.变更状态
					throw new Exception("提示承兑签收失败");
//					detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_2);//提示承兑签收申请失败
//					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
//					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
//					return resultMap;
				}
				this.dao.store(detail);
			}else if(StringUtils.isNotBlank(status) && "2".equals(status)){//签收成功
				//变更状态
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_1);//提示承兑签收成功
				detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_003);
				//判断是否自动提示收票 
				if("1".equals(detail.getIsAutoCallPyee())){
					//发布提示收票
					autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_SEND_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_SEND, null, detail.getBillNo(), detail.getBpsNo(), null, null);
					logger.info("票号"+detail.getBillNo()+"发布提示收票任务！");
				}
				this.txStore(detail);
				if(new BigDecimal(100).compareTo(batch.getDepositRatio())!=0){
					onlineCommonService.txSavePedCreditDetail(PublicStaticDefineTab.PRODUCT_001,null,detail);
    				onlineCommonService.txSaveCreditProduct(PublicStaticDefineTab.PRODUCT_001,batch,detail);
				}
				
				/*//同步批次状态
				this.txSyncAcptBatchStatus(batch);
				
				 * 若该批次下全部票据都出成功，则置换额度信息
				 
				logger.info("======================在线银承为何没存主业务合同跟踪日志==========================批次状态："+batch.getStatus());
				if(PublicStaticDefineTab.ACPT_BATCH_005.equals(batch.getStatus())){
	    			if(new BigDecimal(100).compareTo(batch.getDepositRatio())!=0){//100%保证金不校验额度
	    				logger.info("======================在线银承为何没存主业务合同跟踪日志==========================非百分百保证金");
						//该合同下的全部借据
						CreditQueryBean queryBean = new CreditQueryBean();
						queryBean.setCrdtNo(batch.getContractNo());
						List<PedCreditDetail> crdtDetailList =  poolCreditProductService.queryCreditDetailList(queryBean) ;
	
						// 将原占用detail的额度信息，置换为占用PedCreditDetail
						financialService.txOnlineBusiCreditChange(batch.getContractNo(), crdtDetailList, batch.getBpsNo());
					}
	    			//短信通知
					List<PedOnlineMsgInfo> msgList = onlineManageService.queryOnlineMsgInfoList(batch.getOnlineAcptNo(),null);
					for(PedOnlineMsgInfo msgInfo:msgList){
						onlineManageService.txSendMsg(msgInfo.getAddresseeRole(), batch.getApplyName(), msgInfo.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, batch.getTotalAmt(), batch.getTotalAmt(), true,msgInfo.getAddresseeName(),msgInfo.getOnlineNo());
					}
				 }*/

				//签收记账成功唤醒银承业务明细状态及发生未用退回时金额统计
				logger.info("签收记账成功唤醒银承业务明细状态及发生未用退回时金额统计");
    			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

			    Map<String,String> reqParams = new HashMap<String,String>();
			    reqParams.put("acptBatchId", batch.getId());
			    reqParams.put("acptId", detail.getId());
			    reqParams.put("source", "1");  //签收成功
				autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);
				
				
			}else if("0".equals(status) || "1".equals(status)){
				logger.info("票号"+detail.getBillNo()+"提示承兑签收，处理等待中！");
				throw new Exception("提示承兑签收，处理等待中");
//				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
//				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
//				return resultMap;
			}
			this.dao.store(detail);
		}else{//记账失败
			//变更状态
			detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007_2);//提示承兑签收失败
			detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
			this.dao.store(detail);
//			batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
			this.dao.store(batch);
			//记录日志
			onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",detail.getBillNo(),detail.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, PublicStaticDefineTab.CHANNEL_NO_BBSP, "提示承兑签收记账失败", "BBSP009", PublicStaticDefineTab.ACPT_BUSI_NAME_10, "send");
			/**
			 * 发布统一撤销
			 */
			autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_01, null,  detail.getBillNo(), null, null, null);
			/**
			 * 结束任务
			 */
			autoTaskExeService.txUpdateAutoTaskExeDelFlag(AutoTaskNoDefine.POOL_ONLINE_SIGN_NO,AutoTaskNoDefine.POOL_ONLINE_ACPT,detail.getId());	        		
    		logger.info("票号"+detail.getBillNo()+"提示承兑签收失败，结束任务！");
		}
	}
	
	public void txFailExe(String queueNode,String queueName,String busiId)throws Exception{

    	PoolQueryBean query = new PoolQueryBean();
    	query.setQueueNode(queueNode);//队列节点
    	query.setQueueName(queueName);//队列名称
    	TaskDispatchConfig config =taskDispatchConfigService.queryTaskDispatchConfigByParm(query);
    	if(null==config){
    		return;
    	}
    	PoolQueryBean poolQuery = new PoolQueryBean();
    	poolQuery.setBusiId(busiId);//业务ID
    	poolQuery.setTaskId(config.getId());
    	AutoTaskExe autoTaskExe=autoTaskExeService.queryAutoTaskExeByParm(poolQuery);
    	logger.info("--------------------"+autoTaskExe.getStatus());
		logger.info("--------------------"+autoTaskExe.getErrorCount());
    	autoTaskExe.setStatus("4");//异常
		autoTaskExe.setErrorCount(autoTaskExe.getErrorCount()+1);
		autoTaskExe.setCreateDate(new Date());
		//autoTaskExeService.txStore(autoTaskExe);
		logger.info("--------------------"+autoTaskExe.getStatus());
		logger.info("--------------------"+autoTaskExe.getErrorCount());
		autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, "扣收保证金手续费或记账失败");
		logger.info("---------扣收保证金手续费或记账失败-----------");

	}
	
	/**
	 * @author 
	 * @throws Exception 
	 * @date 2021-7-29
	 * @description 在线银承复核校验
	 */
	public ReturnMessageNew txAcptCheckApply(OnlineQueryBean queryBean) throws Exception {
		ReturnMessageNew response =  new ReturnMessageNew();
		response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
        List details = queryBean.getDetalis();
        if(null != details && details.size()>0){
        	String commError ="";//禁止
			String commMsg = "";//提示
			PedOnlineAcptProtocol protocol = this.queryOnlinAcptPtlByNo(queryBean.getOnlineAcptNo());
    		
    		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
    			
    			boolean isAllPosit = false;//是否100%银承
    			if(new BigDecimal(100).compareTo(protocol.getDepositRatio())==0){
    				isAllPosit = true;
    			}
    			if(!isAllPosit){
    				
    				//【1】关联票据池校验
    				PedProtocolDto pool = queryBean.getPool();
    				Map comm = this.acptApplyCheck(queryBean,protocol,pool);
    				commError += (String) comm.get("error");//禁止
    				commMsg += (String) comm.get("msg");//提示
    				
    				//【2】池额度校验，（经办校验，复核的实占）
    				if(new BigDecimal(100).compareTo(protocol.getDepositRatio()) !=0){
    					commMsg += this.txPoolCreditApplyCheck(queryBean, pool);
    				}
    			}
    			//【3】协议额度校验
    			BigDecimal usedAmt = this.getOnlineAcptPtlAmt(queryBean);
    			logger.info("在线银承已用金额2:"+usedAmt+"..................");
    			logger.info("在线银承未用金额2:"+protocol.getOnlineAcptTotal().subtract(usedAmt)+"..................");
    			if(protocol.getOnlineAcptTotal().subtract(usedAmt).compareTo(queryBean.getOnlineAcptTotal())<0){
    				commError = commError +"该"+queryBean.getOnlineAcptNo()+"在线协议额度不足^";
    			}
    			/**
				 * 信贷额度风险探测
				 */
				ReturnMessageNew result1  = this.txPJE021Handler(queryBean.getAcptBatch(),PublicStaticDefineTab.PRODUCT_001,PublicStaticDefineTab.CREDIT_OPERATION_CHECK,null);
				if(!result1.isTxSuccess()){
					commError = commError + result1.getRet().getRET_MSG() +"^";
				}
    			
    			//【4】明细校验
    			for(int i=0;i<details.size();i++){
    				PlOnlineAcptDetail detail = (PlOnlineAcptDetail) details.get(i);
    				Map map = new HashMap();
    				String error = "";//禁止
    				String msg = "";//提示
    				//业务校验
    				Map payeeCheck = this.acptPayeeCheck(queryBean,protocol, detail, msg, error);
    				//返回信息
    				map.put("SERIAL_NO", detail.getBillSerialNo());//序号
    				msg += (String)payeeCheck.get("msg");
    				error += (String)payeeCheck.get("error");
    				if(msg.length()>0){
    					commMsg = commMsg + detail.getBillSerialNo() + "^" + msg + ";";
    					map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_2);
    				}
    				if(error.length()>0){
    					commError +=  detail.getBillSerialNo() + "^" + error + ";";
    					map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
    					response.getFileHead().put("FILE_FLAG", "2");
    				}else{
    					map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);
    				}
    				String str = error+msg;
    				if(str.length()>0){
    					if(str.endsWith("|")){
    						str = str.substring(0, (error+msg).length()-1);
    					}
    				}
    				map.put("CHECK_INFO", str);//校验结果说明
    				response.getDetails().add(map);
    				
    				//明细校验日志保存
    				if(error.length()>0 || msg.length()>0){
    					onlineManageService.txSaveTrdeLog(protocol.getCustNumber(),protocol.getBpsNo(),detail.getBillSerialNo(),detail.getId(), "1", queryBean.getOperationType(), "网银", error, "PJC056", "在线银承申请", "receive");
    				}
    			}
    		}
        	
        	//批次校验日志保存
        	if(commError.length()>0 || commMsg.length()>0){
        		onlineManageService.txSaveTrdeLog(protocol.getCustNumber(),protocol.getBpsNo(),queryBean.getOnlineAcptNo(),queryBean.getAcptBatch().getId(), "1", queryBean.getOperationType(), "网银", commError+commMsg, "PJC056", "在线银承申请", "receive");
        		 ret.setRET_CODE(Constants.TX_FAIL_CODE);
     			 ret.setRET_MSG(commError+commMsg);
        	     response.setRet(ret);
        	     return response;
        	}
        }
        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        response.setRet(ret);
		return response;
	}
	/**
	 * 银承信息 组装queryBean
	 * 
	 */
	public OnlineQueryBean createOnlineAcptApply(PlOnlineAcptBatch batch , List<PlOnlineAcptDetail>  details) throws Exception {
		
		OnlineQueryBean returnBean = new OnlineQueryBean();

		returnBean.setOnlineAcptNo(batch.getOnlineAcptNo());//在线银承编号
		returnBean.setOnlineAcptTotal(batch.getTotalAmt());//银承总金额
		returnBean.setElctrncSign(batch.getElctrncSign());//电子签名
		returnBean.setBbspAcctNo(batch.getApplyAcct());//电票签约账号
		returnBean.setApplyBankNo(batch.getApplyBankNo());//出票人开户行行号
		returnBean.setApplyBankName(batch.getApplyBankName());//出票人开户行行名
		returnBean.setOperationType(PublicStaticDefineTab.OPERATION_TYPE_02);//操作类型

		PedOnlineAcptProtocol protocol = this.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
		
		boolean isAllPosit = false;//是否100%银承
		if(new BigDecimal(100).compareTo(protocol.getDepositRatio())==0){
			isAllPosit = true;
		}
		
		PedProtocolDto pool = null;
		
		if(!isAllPosit){//非100%保证金
			pool = (PedProtocolDto) this.dao.load(PedProtocolDto.class, protocol.getBpsId());
			returnBean.setPool(pool);
		}
		
		//创建主业务合同表对象
		List<PlOnlineAcptDetail> acptDetailList = new ArrayList<PlOnlineAcptDetail>();//银承明细表
		List<PedCreditDetail> crdtDetailList = new ArrayList<PedCreditDetail>();//额度明细表
		for(int i=0;i<details.size();i++){
			PlOnlineAcptDetail detail = (PlOnlineAcptDetail) details.get(i);
			acptDetailList.add(detail);
			returnBean.getDetalis().add(detail);
			
			if(!isAllPosit){//非100%保证金				
				//创建借据对象
				PedCreditDetail crdtDetail = this.creatCrdtDetailByAcptDetail(detail, batch, pool);
				crdtDetailList.add(crdtDetail);
			}
		}
		returnBean.setAcptBatch(batch);//返回在线银承批次信息
		returnBean.setList(acptDetailList);//返回在线银承明细信息
		
		if(!isAllPosit){//非100%保证金				
			CreditProduct product = this.creatProductByAcptBatch(batch, pool);
			returnBean.setProduct(product);//返回主业务合同对象
			returnBean.setCrdtDetailList(crdtDetailList);//返回借据列表
		}
		
		//获取收票人信息
		OnlineQueryBean queryBean=new OnlineQueryBean();
		for(int i=0;i<acptDetailList.size();i++){
			PlOnlineAcptDetail detail = (PlOnlineAcptDetail) acptDetailList.get(i);
			if(!returnBean.getMap().containsKey(detail.getPayeeName())){
				queryBean.setPayeeAcctName(detail.getPayeeName());
				queryBean.setOnlineAcptNo(batch.getOnlineAcptNo());
				queryBean.setPayeeAcctNo(detail.getPayeeAcct());
				queryBean.setPayeeStatus(PublicStaticDefineTab.STATUS_1);
				queryBean.setAcptBatchId(batch.getId());
				List<OnlineQueryBean> payees = this.queryOnlineAcptPayeeListBean(queryBean, null);
				if(null != payees && payees.size()>0){
					returnBean.getMap().put(detail.getPayeeAcct(), payees.get(0).getPayeeTotalAmt().subtract(payees.get(0).getPayeeUsedAmt()!=null?payees.get(0).getPayeeUsedAmt():new BigDecimal(0)));
				}
			}
		}
		return returnBean;
	}
	
	
	
	/**
	 * 银承历史信息保存
	 * 
	 */
	public void txAcptApply(PlOnlineAcptBatch batch , List<PlOnlineAcptDetail>  details) throws Exception{
		PlOnlineAcptCacheBatch hisBatch=new PlOnlineAcptCacheBatch();
		BeanUtils.copyProperties(hisBatch, batch);
		this.txSaveEntity(hisBatch);
		
		List hisList=new ArrayList();
		for(PlOnlineAcptDetail detail:details){
			PlOnlineAcptCacheDetail hisDetail=new PlOnlineAcptCacheDetail();
			BeanUtils.copyProperties(hisDetail, detail);
			hisDetail.setAcptBatchId(hisBatch.getId());
			hisList.add(hisDetail);
		}
		financialAdviceService.txForcedSaveList(hisList);	
	}
	

	/**
	 * 在线银承失败后，将明细作废
	 * @param batch
	 * @author 
	 * @date 
	 */
	public void txFailChangeAcptDetail(PlOnlineAcptBatch batch,String status){
		List<PlOnlineAcptDetail> details = this.queryOnlineAcptDetailByBatchId(batch.getId());
		//在线协议已用额度恢复
		PedOnlineAcptProtocol protocol = this.queryOnlinAcptPtlByNo(batch.getOnlineAcptNo());
		protocol.setUsedAmt(protocol.getUsedAmt().subtract(batch.getTotalAmt()));//释放额度
		this.txStore(protocol);
		if(null != details){	
			List<PlOnlineAcptDetail> storPlanList = new ArrayList<PlOnlineAcptDetail>();
			for(PlOnlineAcptDetail detail : details){
				detail.setStatus(status);
				storPlanList.add(detail);
			}
			this.txStoreAll(storPlanList);
		}
		batch.setStatus(PublicStaticDefineTab.ACPT_BATCH_007);
		batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
		this.txStore(batch);	
	}
	/**
	 * @author wss
	 * @date 2021-4-28
	 * @description 不可修改字段校验
	 */
	private List unableModifyFieldCheck(OnlineQueryBean bean,PedOnlineAcptProtocol ptl,List errors) {
		// 在线协议类型
		if(!bean.getProtocolStatus().equals(ptl.getProtocolStatus())){
			if(null != bean.getDueDate() && bean.getDueDate().compareTo(DateUtils.getWorkDayDate())<0&&bean.getProtocolStatus().equals("1")){
				errors.add("协议已过到期日|");
			}
			if(null != bean.getDueDate() && bean.getDueDate().compareTo(ptl.getDueDate())<0&&bean.getProtocolStatus().equals("1")){
				errors.add("协议已过到期日|");
			}
		}
		//在线编号
		if(null != bean && null != ptl ){
			if(null !=bean.getOnlineAcptNo() && null !=ptl.getOnlineAcptNo()){
				if(!bean.getOnlineAcptNo().equals(ptl.getOnlineAcptNo())){
					errors.add("在线银承编号字段不可修改|");
				}				
			}
			//票据池编号
			if(null != bean.getDepositRatio() && new BigDecimal(100).compareTo(bean.getDepositRatio())!=0){
				if(null !=bean.getBpsNo() && null !=ptl.getBpsNo()){				
					if(!bean.getBpsNo().equals(ptl.getBpsNo())){
						errors.add("票据池编号字段不可修改|");
					}
				}
			}
			
			//核心客户号
			if(null !=bean.getCustNumber() && null !=ptl.getCustNumber()){					
				if(!bean.getCustNumber().equals(ptl.getCustNumber())){
					errors.add("核心客户号字段不可修改|");
				}
			}
			
			if(null !=bean.getCustNumber() && null !=ptl.getCustNumber()){					
				if(!bean.getCustNumber().equals(ptl.getCustNumber())){
					errors.add("担保人名称字段不可修改|");
				}
			}
			//担保人核心客户号
			if(!bean.getGuarantorNo().equals(ptl.getGuarantorNo())){
				errors.add("担保人核心客户号字段不可修改|");
			}
		}
		return errors;
	}

	@Override
	public Map<String, String> txApplyRevokeCheck(PlOnlineAcptDetail detail,PlOnlineAcptBatch batch){
		Map<String,String> resultMap = new HashMap<String,String>();
		detail.setTaskDate(new Date());
		try{
			//根据业务状态判断 是否需要重新执行
			boolean status = false;
			try{
				status = this.txApplyQueryCancle(detail,PublicStaticDefineTab.BBSP_BUSI_TYPE_04);
			}catch (Exception e) {
				logger.error(detail.getId()+"查询bbsp票据信息异常...",e);
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
				return resultMap;
			}
			//判断状态
			if(status){//撤销失败
				//承兑出账撤销
				boolean succ = this.txApplyRevokeApply(detail, batch);
				if(succ){
					detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_009_1);
					this.txStore(detail);
				}else{
					detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_009_2);
					this.txStore(detail);
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					return resultMap;
				}
			}else if(!status){//撤销成功
				//变更状态
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_009_1);
				this.txStore(detail);
				/**
				 * 唤醒未用退回
				 */
				autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_02, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_02, null);
        		logger.info("票号"+detail.getBillNo()+"唤醒未用退回！");
				
			}else{
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
				return resultMap;
			}
		}catch (Exception e) {
			this.txStore(detail);
			logger.error(detail.getId()+"撤销承兑申请异常...",e);
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			return resultMap;
		}
		return null;
		
	}

	@Override
	public Map<String, String> txApplyUnusedCheck(PlOnlineAcptDetail detail,
			PlOnlineAcptBatch batch) {
		Map<String,String> resultMap = new HashMap<String,String>();
		detail.setTaskDate(new Date());
		try{
			//根据业务状态判断 是否需要重新执行
			String status = null;
			try{
				status = this.txApplyQueryBill(detail,"1",PoolComm.NES_0142000);
			}catch (Exception e) {
				logger.error(detail.getId()+"查询bbsp票据信息异常...",e);
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
				return resultMap;
			}
			//判断状态
			if(StringUtils.isNotBlank(status) && "2".equals(status)){//未用退回成功

	    		//变更状态
	    		detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010_1);
	    		detail.setCancelDate(new Date());
	            //该笔借据置为“成功”，但是后续客户自行发起了未用退回，该借据任务状态为“已撤销”。“已撤销”属于“成功”类数据。后续合同状态判断按“成功”识别。
	    		if(PublicStaticDefineTab.ONLINE_DS_003.equals(detail.getDealStatus())){
	    			detail.setDealStatus(PublicStaticDefineTab.ONLINE_DS_004);
	    		}
	    		this.txStore(detail);
	    		Map<String, String> reqParam =new HashMap<String,String>();
	    		reqParam.put("busiId",detail.getId());
	    		reqParam.put("type", "2");//明细类型
	    		reqParam.put("busiType", PublicStaticDefineTab.PRODUCT_001);//业务类型
	    		
	    		//未用退回成功唤醒银承业务明细状态及发生未用退回时金额统计
				logger.info("未用退回成功唤醒银承业务明细状态及发生未用退回时金额统计");
    			String   id = 	batch.getBatchNo() +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成

			    Map<String,String> reqParams = new HashMap<String,String>();
			    reqParams.put("acptBatchId", batch.getId());
			    reqParams.put("acptId", detail.getId());
			    
	    		
	    		//考虑次日未用退回，不用通知信贷释放额度， 查询信贷额度占用流水，判断时间
	    		if(DateUtils.getCurrDate().compareTo(DateUtils.formatDate(detail.getCreateTime(),DateUtils.ORA_DATES_FORMAT))!=0){//隔日未用退回
					reqParam.put("isCredit", "0");//是否通知信贷
	        		autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam,detail.getBillNo(), detail.getBpsNo(), null, null);
	    		}else{
//	    			batch.setTotalAmt(batch.getTotalAmt().subtract(detail.getBillAmt()));
	    			this.txStore(batch);
	    			//本次未用退回成功  可能是撤票或者未用退回队列或者客户主动退回
	    			AutoTaskExe autoTaskExe1=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_CANCLE, detail.getId());
	    			reqParam.put("isCredit", "1");//是否通知信贷
	    			if(null != autoTaskExe1 && "N".equals(autoTaskExe1.getDelFlag())){//唤醒撤票队列
	    				autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_CANCLE_NO_03, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_03, null);
	    			}else {
	        			AutoTaskExe autoTaskExe2=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_01, AutoTaskNoDefine.POOL_ONLINE_ACPT_UNUSED, detail.getId());
	        			if(null != autoTaskExe2 && "N".equals(autoTaskExe2.getDelFlag())){//唤醒未用退回队列
	        				autoTaskPublishService.publishWaitTask(null, AutoTaskNoDefine.POOL_ONLINE_UNUSED_NO_02, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_02, null);
	        			}else{//客户主动退回
	                		autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, detail.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE, reqParam,detail.getBillNo(), detail.getBpsNo(), null, null);
	        			}
	    			}
	    			reqParams.put("source", "2");  //未用退回成功
	    		}
	    		
				autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, id, AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, batch.getBatchNo(), batch.getBpsNo(), null, null);
				
	    		
				
			}else if(StringUtils.isNotBlank(status) && "3".equals(status)){//未用退回成功失败
				detail.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010_2);
				this.txStore(detail);
				logger.info("票号"+detail.getBillNo()+"未用退回失败，重新执行未用退回开始！");
				boolean succ = this.txApplyCancleBill(detail, batch);
				if(!succ){
					logger.info("票号"+detail.getBillNo()+"未用退回申请失败！");
					resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
					resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
					return resultMap;
				}
			}else{//处理中
				logger.info("票号"+detail.getBillNo()+"未用退回申请处理中！");
				resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
				resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998);
				return resultMap;
			}
			this.txStore(detail);
		}catch (Exception e) {
			this.txStore(detail);
			logger.error(detail.getId()+"未用退回异常...",e);
			resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.Err_MSG_CODE);
			resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.ERR_MSG_998+e.getMessage());
			return resultMap;
		}
		
		return null;
	}

	@Override
	public PlOnlineAcptBatch queryPlOnlineAcptBatch(OnlineQueryBean queryBean)throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		
		
		hql.append("select dto from PlOnlineAcptBatch as dto where 1=1 ");
		if(StringUtils.isNotBlank(queryBean.getContractNo())){//合同号
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		
		
		//该方法根据需要添加条件

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PlOnlineAcptBatch> result = this.find(hql.toString(), paramNames, paramValues );
		if(null != result && result.size()>0){
			return result.get(0);
		}
		
		return null;
	}

	@Override
	public PlOnlineAcptDetail queryPlOnlineAcptDetailByBillNo(String loanNo)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		
		hql.append("select dto from PlOnlineAcptDetail as dto where dto.loanNo = '"+loanNo+"' ");

		List<PlOnlineAcptDetail> result = this.find(hql.toString() );
		if(null != result && result.size()>0){
			return result.get(0);
		}
		return null;
	}

	@Override
	public ReturnMessageNew misRepayAcptPJE028(PlOnlineAcptBatch batch) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		//额度系统额度释放
		/**
		 * 1、查询合同下借据信息
		 * 2、统计借据中当日未用退回金额
		 * 3、统计出账失败金额
		 */
		BigDecimal expendFailAmt = new BigDecimal(0);//出账失败金额
		BigDecimal unUsedReturnAmt = new BigDecimal(0);//未用退回金额
		BigDecimal loanAmt = new BigDecimal(0);//所有借据金额
		
		CreditQueryBean queryBean = new CreditQueryBean();
		queryBean.setCrdtNo(batch.getContractNo());
		List<PedCreditDetail> list = poolCreditProductService.queryCreditDetailList(queryBean);
		
		CreditTransNotes note = new CreditTransNotes();
		
		if(list != null && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				PedCreditDetail detail = list.get(i);
				logger.info("借据号："+detail.getLoanNo()+"借据状态为："+detail.getLoanStatus());
				if(DateUtils.formatDate(detail.getStartTime(),DateUtils.ORA_DATES_FORMAT).compareTo(DateUtils.formatDate(new Date(),DateUtils.ORA_DATES_FORMAT)) == 0 && detail.getLoanStatus().equals(PoolComm.JJ_05)){
					//2、统计借据中当日未用退回金额
					unUsedReturnAmt = unUsedReturnAmt.add(detail.getLoanAmount());
				}
				//统计所有借据金额
				loanAmt = loanAmt.add(detail.getLoanAmount());
			}
			expendFailAmt = batch.getOriContractAmt().subtract(loanAmt);
		}else{
			//没有借据信息则为全部出账失败数据
			expendFailAmt = batch.getOriContractAmt();
		}
		
		note.setContractNo(batch.getContractNo());//合同编号
		note.setExpendFailAmt(expendFailAmt);//出账失败金额
		note.setUnUsedReturnAmt(unUsedReturnAmt);//未用退回金额
		note.setOriContractAmt(batch.getOriContractAmt());//原合同金额
		
		try {
			response = poolCreditClientService.txPJE028(note);
		} catch (Exception e) {
			logger.info("信贷合同金额变更调用异常！"+e.getMessage());
			response.setTxSuccess(false);
		}
		return response;
	}

	@Override
	public String query(String id) throws Exception {
		//查询批次的成功笔数记录
		String sql = "select ID from PL_ONLINE_ACPT_BATCH_UPDATE acpt where BATCH_ID = '"+id+"' for update ";
		List list = dao.SQLQuery(sql);
		if(list!=null&&list.size()>0){
			Object obj = list.get(0);
			return (String) obj;
		}
		return null;
	}

	@Override
	public ReturnMessageNew txPJE028Handler(PlOnlineAcptBatch batch)
			throws Exception {
		/**
		 * 1、查询该合同下的借据
		 * 2、
		 */
		return null;
	}

	@Override
	public PlOnlineAcptBatch calculateBatchAmt(PlOnlineAcptBatch batch)
			throws Exception {
		BigDecimal failAmt = new BigDecimal(0);
		BigDecimal successAmt = new BigDecimal(0);
		/**
		 * 查询批次下的银承明细
		 */
		List<PlOnlineAcptDetail> details = this.queryOnlineAcptDetailByBatchId(batch.getId());
		for (int i = 0; i < details.size(); i++) {
			PlOnlineAcptDetail detail = details.get(i);
			if(detail.getStatus().equals(PublicStaticDefineTab.ACPT_DETAIL_012)){
				failAmt = failAmt.add(detail.getBillAmt());
			}else{
				successAmt = successAmt.add(detail.getBillAmt());
			}
			batch.setTotalAmt(successAmt);
		}
		
		return batch;
	}
	
	
}
