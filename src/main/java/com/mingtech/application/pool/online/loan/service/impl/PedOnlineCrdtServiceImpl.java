package com.mingtech.application.pool.online.loan.service.impl;

import java.lang.reflect.InvocationTargetException;
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

import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.audit.service.AuditService;
import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.BatchNoUtils;
import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.lprsys.service.PoolLprService;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.common.service.OnlineCommonService;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtInfo;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtInfoHist;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocolHist;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayCachePlan;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlanHist;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCacheCrdt;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineHandleLog;
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
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("pedOnlineCrdtService")
public class PedOnlineCrdtServiceImpl extends GenericServiceImpl implements PedOnlineCrdtService {
	private static final Logger logger = Logger.getLogger(PedOnlineCrdtServiceImpl.class);

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
	private OnlineManageService onlineManageService;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private OnlineCommonService onlineCommonService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private AutoTaskExeService autoTaskExeService;
	@Autowired
	private RedisUtils redisQueueCache;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AuditService auditService;
	@Autowired
	private BatchNoUtils batchNoUtils;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PoolLprService poolLprService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private GenericHibernateDao sessionDao;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	/**
	 * @author wss
	 * @date 2021-4-28
	 * @description 查询在线协议
	 * @param queryBean
	 */
	public List<PedOnlineCrdtProtocol> queryOnlineProtocolList(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineCrdtProtocol as dto where 1=1 ");
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
		if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
			hql.append(" and dto.onlineCrdtNo =:onlineCrdtNo");
			paramName.add("onlineCrdtNo");
			paramValue.add(queryBean.getOnlineCrdtNo());
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			hql.append(" and dto.protocolStatus =:protocolStatus");
			paramName.add("protocolStatus");
			paramValue.add(queryBean.getProtocolStatus());
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
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and dto.appName like(:appName)");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and dto.signBranchName like(:signBranchName)");
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
		
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineCrdtProtocol> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}
	/**
	 * @author wss
	 * @date 2021-4-28
	 * @description 查询在线协议
	 * @param queryBean
	 */
	@Override
	public List queryOnlineProtocolList(OnlineQueryBean queryBean,Page page) {
		StringBuffer hql = new StringBuffer();
//		hql.append("select dto.ID,dto.BPS_ID,dto.BPS_NO,dto.BPS_NAME,dto.CUST_NUMBER ,dto.CUST_ORGCODE ,dto.CUST_NAME ,dto.PROTOCOL_STATUS ,dto.ONLINE_CRDT_NO ,dto.BASE_CREDIT_NO ,dto.EBK_CUST_NO,DTO.POOL_CREDIT_RATIO ,dto.ONLINE_LOAN_TOTAL,dto.BASE_RATE_TYPE,dto.RATE_FLOAT_TYPE,dto.RATE_FLOAT_VALUE,dto.OVER_RATE_FLOAT_TYPE,dto.OVER_RATE_FLOAT_VALUE,dto.MAKE_LOAN_TYPE,dto.REPAYMENT_TYPE,dto.IS_AUTO_DEDUCT,dto.IS_DISC_INTEREST,dto.LOAN_ACCT_NO,dto.LOAN_ACCT_NAME,dto.DEDU_ACCT_NO,dto.DEDU_ACCT_NAME,dto.IN_ACCT_BRANCH_NO ,dto.IN_ACCT_BRANCH_NAME,dto.CONTRACT_NO,dto.GUARANTOR,dto.APP_NAME,dto.APP_NO,dto.SIGN_BRANCH_NO,dto.SIGN_BRANCH_NAME,dto.OPEN_DATE,dto.CHANGE_DATE,dto.DUE_DATE,dto.CREATE_TIME,dto.UPDATE_TIME,info.usedAmt " +
//				"from PED_ONLINE_CRDT_PROTOCOL dto left join (select sum(detail.TOTAL_AMT) usedAmt,ONLINE_CRDT_NO from PL_CRDT_PAY_PLAN detail where detail.STATUS not in(:status) group by ONLINE_CRDT_NO) info on dto.ONLINE_CRDT_NO = info.ONLINE_CRDT_NO where 1=1 ");
//		
		hql.append("select dto.ID,dto.BPS_ID,dto.BPS_NO,dto.BPS_NAME,dto.CUST_NUMBER ,dto.CUST_ORGCODE ,dto.CUST_NAME ,dto.PROTOCOL_STATUS ,dto.ONLINE_CRDT_NO ,dto.BASE_CREDIT_NO ,dto.EBK_CUST_NO,DTO.POOL_CREDIT_RATIO ,dto.ONLINE_LOAN_TOTAL,dto.BASE_RATE_TYPE,dto.RATE_FLOAT_TYPE,dto.RATE_FLOAT_VALUE,dto.OVER_RATE_FLOAT_TYPE,dto.OVER_RATE_FLOAT_VALUE,dto.MAKE_LOAN_TYPE,dto.REPAYMENT_TYPE,dto.IS_AUTO_DEDUCT,dto.IS_DISC_INTEREST,dto.LOAN_ACCT_NO,dto.LOAN_ACCT_NAME,dto.DEDU_ACCT_NO,dto.DEDU_ACCT_NAME,dto.IN_ACCT_BRANCH_NO ,dto.IN_ACCT_BRANCH_NAME,dto.CONTRACT_NO,dto.GUARANTOR,dto.APP_NAME,dto.APP_NO,dto.SIGN_BRANCH_NO,dto.SIGN_BRANCH_NAME,dto.OPEN_DATE,dto.CHANGE_DATE,dto.DUE_DATE,dto.CREATE_TIME,dto.UPDATE_TIME,dto.USED_AMT " +
				"from PED_ONLINE_CRDT_PROTOCOL dto where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
//		queryBean.getStatuList().add(PublicStaticDefineTab.PAY_PLAN_04);
//		paramName.add("status");
//		paramValue.add(queryBean.getStatuList());
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
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				hql.append(" and dto.ONLINE_CRDT_NO =:ONLINE_CRDT_NO");
				paramName.add("ONLINE_CRDT_NO");
				paramValue.add(queryBean.getOnlineCrdtNo());
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
  				bean.setOnlineCrdtNo((String)obj[8]);
  				bean.setBaseCreditNo((String)obj[9]);
  				bean.setEbkCustNo((String)obj[10]);
  				bean.setPoolCreditRatio((BigDecimal)obj[11]);
  				bean.setOnlineLoanTotal((BigDecimal)obj[12]);
  				bean.setBaseRateType((String)obj[13]);
  				bean.setRateFloatType((String)obj[14]);
  				bean.setRateFloatValue((BigDecimal)obj[15]);
  				bean.setOverRateFloatType((String)obj[16]);
  				bean.setOverRateFloatValue((BigDecimal)obj[17]);
  				bean.setMakeLoanType((String)obj[18]);
  				bean.setRepaymentType((String)obj[19]);
  				bean.setIsAutoDeduct((String)obj[20]);
  				bean.setIsDiscInterest((String)obj[21]);
  				bean.setLoanAcctNo((String)obj[22]);
  				bean.setLoanAcctName((String)obj[23]);
  				bean.setDeduAcctNo((String)obj[24]);
  				bean.setDeduAcctName((String)obj[25]);
  				bean.setInAcctBranchNo((String)obj[26]);
  				bean.setInAcctBranchName((String)obj[27]);
  				bean.setContractNo((String)obj[28]);
  				bean.setGuarantor((String)obj[29]);
  				bean.setAppName((String)obj[30]);
  				bean.setAppNo((String)obj[31]);
  				bean.setSignBranchNo((String)obj[32]);
  				bean.setSignBranchName((String)obj[33]);
  				bean.setOpenDate((Date)obj[34]);
  				bean.setChangeDate((Date)obj[35]);
  				bean.setDueDate((Date)obj[36]);
  				bean.setCreateTime((Date)obj[37]);
  				bean.setUpdateTime((Date)obj[38]);
  				bean.setUsedAmt((BigDecimal)obj[39]!=null?(BigDecimal)obj[39]:new BigDecimal(0));
  				result.add(bean);
  			}
  			return result;
  		}
		return list;
	}
	
	/**
	 * 查询生效银承协议
	 */
	public PedOnlineCrdtProtocol queryOnlineCrdtProtocol(OnlineQueryBean queryBean) {
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineCrdtProtocol as dto where 1=1 ");
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
		if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
			hql.append(" and dto.onlineCrdtNo =:onlineCrdtNo");
			paramName.add("onlineCrdtNo");
			paramValue.add(queryBean.getOnlineCrdtNo());
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			hql.append(" and dto.protocolStatus =:protocolStatus");
			paramName.add("protocolStatus");
			paramValue.add(queryBean.getProtocolStatus());
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
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and dto.appName like(:appName)");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and dto.signBranchName like(:signBranchName)");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		hql.append(" order by  dto.createTime desc");

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineCrdtProtocol> result = this.find(hql.toString(), paramNames, paramValues );
		if(null != result && result.size()>0){
			return (PedOnlineCrdtProtocol) result.get(0);
		}else{
			return null;
		}
	}

	/**
	 * @description 校验在线流贷协议
	 * @author wss
	 * @date 2021-4-28
	 * @param queryBean
	 * @return Ret
	 */
	public Ret onlineCrdtCheck(OnlineQueryBean queryBean) {
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		List errors = new ArrayList();
		//关联票据池
		PedProtocolDto pool=null;
		try {
			ProtocolQueryBean pBean = new ProtocolQueryBean();
			pBean.setPoolAgreement(queryBean.getBpsNo());
			pBean.setIsGroup(PoolComm.NO);//单户
			pBean.setOpenFlag(PoolComm.OPEN_01);//已签约
			pool = pedProtocolService.queryProtocolDtoByQueryBean(pBean);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		if(null == pool){
			errors.add("该客户无对应生效票据池信息|");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setSomeList(errors);
			return ret;
		}
		//在线业务禁入名单校验 
		queryBean.setStatus(PublicStaticDefineTab.STATUS_1);
		boolean flag = onlineManageService.onlineBlackListCheck(queryBean);
		if(flag){
			errors.add("该客户属于在线业务禁入名单客户|");
		}
		boolean check = true;//是否校验 票据池协议的保证金账号所属机构与在线协议的入账机构所号跨分行  生效的修改或新增生效校验,失效时不校验
		
		if(PublicStaticDefineTab.MOD01.equals(queryBean.getModeType())){
			//在线银承协议编号
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				String crdtNo = queryBean.getOnlineCrdtNo();
				OnlineQueryBean bean =  new OnlineQueryBean();
				bean.setOnlineCrdtNo(crdtNo);
				List list = this.queryOnlineProtocolList(bean);
				if(null != list && list.size()>0){
					errors.add("在线流贷协议编号重复|");
				}
				if(!crdtNo.startsWith("LDOL") || !crdtNo.substring(4).matches("[0-9]+")){
					errors.add("在线流贷协议编号格式有误|"+crdtNo.substring(2));
				}
			}
			//票据池编号
			if(StringUtils.isNotBlank(queryBean.getBpsNo())){
				errors = this.checkPoolInfo(queryBean, pool, errors);
			}
			//如果占用票据池额度
			if(null != queryBean.getPoolCreditRatio() && (queryBean.getPoolCreditRatio().compareTo(new BigDecimal(0))>0)){
				//客户名称
				if(StringUtils.isNotBlank(queryBean.getCustName()) && !queryBean.getCustName().equals(pool.getCustname())){
					errors.add("客户名称与票据池客户名称不一致|");
				}
				//核心客户号
				if(StringUtils.isNotBlank(queryBean.getCustNumber()) && !queryBean.getCustNumber().equals(pool.getCustnumber())){
					errors.add("核心客户号与票据池不一致|");
				}
				//票据池额度比例（%）
				if(null != queryBean.getPoolCreditRatio() && queryBean.getPoolCreditRatio().compareTo(new BigDecimal(105))<0){
					errors.add("票据池额度比例（%）应不小于105%|");
				}
				
			}
		}else{
			PedOnlineCrdtProtocol oldProtocol = this.queryOnlineProtocolByCrdtNo(queryBean.getOnlineCrdtNo());
			//信用风险管理系统传输过来的字段含不可修改字段
			errors = this.unableModifyFieldCheck(queryBean,oldProtocol,errors);
			//关联的票据池是否生效
			if(!PoolComm.OPEN_01.equals(pool.getOpenFlag())){
				errors.add("关联的担保合同非该客户“生效”状态的最高额票据池担保合同|");
			}
			//在线协议状态 0失效；1生效
			if(PublicStaticDefineTab.STATUS_0.equals(queryBean.getProtocolStatus())){
				//修改做失效  不需校验跨分行
				check = false;
			}
			
//			if(PublicStaticDefineTab.STATUS_1.equals(queryBean.getProtocolStatus()) && PublicStaticDefineTab.STATUS_0.equals(oldProtocol.getProtocolStatus())){
//				if(oldProtocol.getDueDate().compareTo(DateUtils.getWorkDayDate())>=0){
//					errors.add("协议已过到期日|");
//				}
				if(null != queryBean.getDueDate() && queryBean.getDueDate().compareTo(DateUtils.getWorkDayDate())<=0){
					errors.add("协议已过到期日|");
				}
//			}
			//在线流贷总额
			if(null != queryBean.getOnlineLoanTotal()){
				BigDecimal oldAmt = this.getOnlineCrdtPayeeAmt(queryBean.getOnlineCrdtNo(),null);
				if(null != oldAmt && (queryBean.getOnlineLoanTotal().compareTo(oldAmt)<0)){
					errors.add("在线流贷总额小于客户已用额度|");
				}
			}
		}
		//====================================通用校验===================================
		
		//收款人
		errors = this.onlinePayeesCheck(queryBean, errors);
		//利率浮动方式
		if(StringUtils.isNotBlank(queryBean.getDepositRateFloatFlag())){
			if(!"0".equals(queryBean.getDepositRateFloatFlag()) && !"1".equals(queryBean.getDepositRateFloatFlag())){
				errors.add("利率浮动方式非标准格式|");
			}
		}
		//逾期利率浮动方式
		if(StringUtils.isNotBlank(queryBean.getOverRateFloatType())){
			if(!"0".equals(queryBean.getOverRateFloatType()) && !"1".equals(queryBean.getOverRateFloatType()) && !"2".equals(queryBean.getOverRateFloatType()) ){
				errors.add("逾期利率浮动方式非标准格式|");
			}
		}
		//利率浮动值（%）
		if(null != queryBean.getDepositRateFloatValue()){
			if(StringUtils.isNotBlank(queryBean.getDepositRateFloatFlag()) && "0".equals(queryBean.getDepositRateFloatFlag())){
				if(queryBean.getDepositRateFloatValue().intValue()!=0){
					errors.add("利率浮动方式与基准保持一致模式，利率浮动值应为0|");
				}
			}
		}
		//逾期利率浮动值（%）
		if(null != queryBean.getOverRateFloatValue()){
			if(StringUtils.isNotBlank(queryBean.getOverRateFloatType()) && "0".equals(queryBean.getOverRateFloatType())){
				if(queryBean.getOverRateFloatValue().intValue()!=0){
					errors.add("逾期利率浮动方式为基准保持一致，保证金利率浮动值应为0|");
				}
			}
		}
		//入账机构所号
		if(StringUtils.isNotBlank(queryBean.getInAcctBranchNo())){
			Department dep = departmentService.queryByInnerBankCode(queryBean.getInAcctBranchNo());
			if(null == dep){
				errors.add("入账机构未在票据池系统登记|");
			}else{
				queryBean.setInAcctBranchName(dep.getName());
			}
			
			if(check){
				/**
				 * （在线协议的入账机构与保证金账号的归属机构需要相同)
				 */
				CoreTransNotes transNotes = new CoreTransNotes();
				transNotes.setAccNo(pool.getMarginAccount());
				transNotes.setCurrentFlag("1");
				ReturnMessageNew response1;
				try {
					response1 = poolCoreService.PJH716040Handler(transNotes, "0");
					if(response1.isTxSuccess()){
						String branch = (String) response1.getBody().get("OPEN_BRANCH");//机构号
	    				String accBranch = (String) response1.getBody().get("ACCOUNT_BRANCH_ID");//核算机构

						if(!dep.getAuditBankCode().equals(accBranch)){
				        	errors.add("票据池协议的保证金账号所属机构与在线协议的入账机构所号跨分行，不允许修改！|");
				        }
					}
				} catch (Exception e) {
					logger.info("校验入账机构与保证金账号归属机构时，查询保证金归属机构时报错！"+e);
					errors.add("入账机构未在票据池系统登记|");
				}
			}
			
			
		}
		//担保合同编号
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			if(!queryBean.getBpsNo().equals(pool.getPoolAgreement())){
				errors.add("与关联的最高额票据池的票据池号不一致|");
			}
			if(!queryBean.getContractNo().equals(pool.getContract())){
				errors.add("与关联的最高额票据池担保合同的担保合同编号不一致|");
			}
//			if(!queryBean.getGuarantor().equals(pool.getCustname())){
//				errors.add("与关联的最高额票据池担保合同的担保人不一致|");
//			}
			if(!queryBean.getGuarantorNo().equals(pool.getCustnumber())){
				errors.add("与关联的最高额票据池担保合同的担保人核心客户号不一致|");
			}
		}
		//经办人
		User user=new User();
		if(StringUtils.isNotBlank(queryBean.getAppNo())){
			try {
				user = userService.getUserByLoginName(queryBean.getAppNo());
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
						if(StringUtils.isNotBlank(queryBean.getAppName()) && !queryBean.getAppName().equals(user.getName())){
							errors.add("经办人名称与票据池系统签约信息不符|");
						}
						boolean isBranchNo = false;//入账机构号是否与客户经理归属机构的内部机构号或者其下属机构的“内部机构号”相同
						//入账机构号是否与客户经理归属机构的内部机构号或者其下属机构的“内部机构号”
						//查询客户经理归属机构的内部机构号或者其下属机构的“内部机构号”
						List list = departmentService.getAllChildrenInnerCodeList(dept.getInnerBankCode(), 1);
						for (int i = 0; i < list.size(); i++) {
							String branchNo = (String) list.get(i);
							if(branchNo.equals(queryBean.getInAcctBranchNo())){
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
								queryBean.setInAcctBranchName(name);
							}
						}
	    			}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		logger.info("在线流贷协议推送签约机构号："+queryBean.getSignBranchNo());
		//签约机构
		if(StringUtils.isNotBlank(queryBean.getSignBranchNo())){
//			if(null != pool && !pool.getSignDeptNo().equals(queryBean.getSignBranchNo())){
//				errors.add("签约机构与票据池系统签约信息不符|");
//			}
			if(null != user && null!=user.getDeptId()){
				Department dept=(Department) userService.load(user.getDeptId(),Department.class);
				logger.info("在线流贷协议推送通过客户经理查询的客户经理归属机构号："+dept.getInnerBankCode());
				if(null ==dept){
					errors.add("经办人签约机构与票据池系统签约信息不符|");
				}else{
					if(null != pool && !dept.getInnerBankCode().equals(queryBean.getSignBranchNo())){
						errors.add("经办人签约机构与票据池系统签约信息不符|");
					}
				}
			}
//			Department dep = departmentService.queryByInnerBankCode(queryBean.getSignBranchNo());
//			if(null == dep){
//				errors.add("签约机构所号非票据池系统已有所号|");
//			}
		}
		//到期日
		if(null != queryBean.getDueDate()){
			if(queryBean.getDueDate().compareTo(queryBean.getOpenDate())>1){
				errors.add("不得早于开通期日期|");
			}
			if(queryBean.getDueDate().compareTo(queryBean.getChangeDate())>1){
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
	 * @description 校验收款人
	 * @author wss
	 * @date 2021-4-28
	 * @param protocol 收款人
	 */
	public List onlinePayeesCheck(OnlineQueryBean protocol,List errors) {
		List payees =protocol.getPayees();
		if(null != payees && payees.size()>0){
			for(int i=0;i<payees.size();i++){
				PedOnlineCrdtInfo info = (PedOnlineCrdtInfo) payees.get(i);
				boolean isByOneself = false;
				if(StringUtil.isBlank(info.getPayeeAcctName())){					
					errors.add("收款人名称未输入");
					continue;
				}
								
				if(StringUtil.isBlank(info.getPayeeId())||StringUtil.isBlank(info.getModeType())||null == info.getPayeeTotalAmt()){
					errors.add("收款人信息填写不完整");
					continue;
				}
				
				
				if(PublicStaticDefineTab.MOD03.equals(info.getModeType()) || PublicStaticDefineTab.MOD02.equals(info.getModeType())){
					PedOnlineCrdtInfo old = this.queryOnlinePayeeByPayeeId(info.getPayeeId());
					if(null != old){
						if(PublicStaticDefineTab.MOD03.equals(info.getModeType()) && old.getPayeeUsedAmt().compareTo(new BigDecimal("0"))>0){
							errors.add("不得删除收款人已收款金额不为0的数据:"+info.getPayeeAcctName()+"|");
						}
						if(info.getPayeeTotalAmt().compareTo(old.getPayeeUsedAmt())<0){
							errors.add("收款人收款总额小于收款人已收款额度:"+info.getPayeeAcctName()+"|");
						}
					}else{
						errors.add("收款人在票据池系统不存在:"+info.getPayeeAcctName()+"|");
					}
				}
				if(info.getPayeeTotalAmt().compareTo(new BigDecimal("0"))<0){
					errors.add("收款人收款总额金额不得为负:"+info.getPayeeAcctName()+"|");
				}
				if("自主支付".equals(info.getPayeeAcctName()) && info.getPayeeTotalAmt().compareTo(new BigDecimal("10000000"))>0){
					errors.add("自主支付的金额不得超过1000万:"+info.getPayeeAcctName()+"|");
				}
				if(!"自主支付".equals(info.getPayeeAcctName()) && !info.getPayeeAcctNo().matches("[0-9]+")){
					errors.add("收款人账号非数字:"+info.getPayeeAcctName()+"|");
				}
				if(!"1".equals(info.getIsLocal()) && (!info.getPayeeOpenBankNo().matches("[0-9]+") || 12!=info.getPayeeOpenBankNo().length())){
					errors.add("收款人开户行行号非12位数字:"+info.getPayeeAcctName()+"|");
				}
			}
		}
		return errors;
	}
	
	private PedOnlineCrdtInfo queryOnlinePayeeByPayeeId(String payeeId) {
		String sql ="select dto from PedOnlineCrdtInfo dto where dto.payeeId=? ";
		List param = new ArrayList();
		param.add(payeeId);
		List result = this.find(sql, param);
		if(null != result && result.size()>0){
			return (PedOnlineCrdtInfo) result.get(0);
		}else{
			return null;
		}
	}

	/**
	 * @author wss
	 * @date 2021-4-28
	 * @description 不可修改字段校验
	 */
	private List unableModifyFieldCheck(OnlineQueryBean bean,PedOnlineCrdtProtocol ptl,List errors) {
		// 在线协议类型
		if(!bean.getProtocolStatus().equals(ptl.getProtocolStatus())){
			if(null != bean.getDueDate() && bean.getDueDate().compareTo(DateUtils.getWorkDayDate())<0&&bean.getProtocolStatus().equals("1")){
				errors.add("协议已过到期日|");
			}
			if(null != bean.getDueDate() && bean.getDueDate().compareTo(ptl.getDueDate())<0&&bean.getProtocolStatus().equals("1")){
				errors.add("协议已过到期日|");
			}
		}
		//在线流贷编号
		if(!bean.getOnlineCrdtNo().equals(ptl.getOnlineCrdtNo())){
			errors.add("在线流贷编号字段不可修改|");
		}
		//票据池编号
		if(!bean.getBpsNo().equals(ptl.getBpsNo())){
			errors.add("票据池编号字段不可修改|");
		}
		//核心客户号
		if(!bean.getCustNumber().equals(ptl.getCustNumber())){
			errors.add("核心客户号字段不可修改|");
		}
		//逾期利率执行
		//放款方式
		if(!bean.getMakeLoanType().equals(ptl.getMakeLoanType())){
			errors.add("放款方式字段不可修改|");
		}
		//还款方式
		if(!bean.getRepaymentType().equals(ptl.getRepaymentType())){
			errors.add("还款方式字段不可修改|");
		}
		//是否自动扣划本息
		if(!bean.getIsAutoDeduct().equals(ptl.getIsAutoDeduct())){
			errors.add("是否自动扣划本息字段不可修改|");
		}
		//是否贴息
		if(!bean.getIsDiscInterest().equals(ptl.getIsDiscInterest())){
			errors.add("是否贴息字段不可修改|");
		}
		//基准利率类型
		if(!bean.getBaseRateType().equals(ptl.getBaseRateType())){
			errors.add("字段不可修改|");
		}
		//执行利率
		//担保人名称
		if(!bean.getGuarantor().equals(ptl.getGuarantor())){
			errors.add("担保人名称字段不可修改|");
		}
		//担保人核心客户号
		if(!bean.getGuarantorNo().equals(ptl.getGuarantorNo())){
			errors.add("担保人核心客户号字段不可修改|");
		}
		return errors;
	}

	/**
	 * @author wss
	 * @date 2021-4-28
	 * @description
	 */
	public PedOnlineCrdtProtocol queryOnlineProtocolByCrdtNo(String onlineCrdtNo) {
		String sql ="select dto from PedOnlineCrdtProtocol dto where dto.onlineCrdtNo=? ";
		List param = new ArrayList();
		param.add(onlineCrdtNo);
		List result = this.find(sql, param);
		if(null != result && result.size()>0){
			return (PedOnlineCrdtProtocol) result.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * @description 查询客户总额度
	 * @author wss
	 * @date 2021-4-28
	 * @param onlineCrdtNo
	 * @param payeeId
	 */
	public BigDecimal getOnlineCrdtPayeeAmt(String onlineCrdtNo,String payeeId) {
		String sql ="select sum(dto.payeeUsedAmt) from PedOnlineCrdtInfo dto where dto.onlineCrdtNo=? ";
		List param = new ArrayList();
		param.add(onlineCrdtNo);
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
	 * @description 校验关联票据池信息
	 * @author wss
	 * @date 2021-4-26
	 * @param protocol 信贷传参
	 * @param pool 票据池信息
	 * @param errors 错误日志
	 */
	private List checkPoolInfo(OnlineQueryBean protocol,PedProtocolDto pool, List errors) {
		if(null == pool){
			errors.add("该客户无对应生效票据池信息|");
		}else{
			if(StringUtils.isNotBlank(pool.getContract()) && !PoolComm.OPEN_01.equals(pool.getOpenFlag())){
				errors.add("该客户票据池未签约融资功能|");
			}else{
				if(protocol.getChangeDate().compareTo(pool.getContractEffectiveDt())<0 ){
					errors.add("该客户票据池未关联最高额担保合同|");
				}else{
					protocol.setBpsId(pool.getPoolInfoId());
					protocol.setBpsName(pool.getPoolName());
				}
			}
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
	 * @description 保存在线流贷协议
	 * @author wss
	 * @date 2021-4-28
	 * @param
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void txSaveOnlineProtocol(OnlineQueryBean queryBean) throws Exception {
		//在线银承/流贷协议的机构归属以信用风险管理系统发过来的【签约机构】归属确认。
		//在线银承/流贷协议的客户经理归属以在线银承/流贷协议融资功能开通时【经办人名称】归属确认
		PedOnlineCrdtProtocol ptl = new PedOnlineCrdtProtocol();
		PedOnlineCrdtProtocolHist hist = new PedOnlineCrdtProtocolHist();
		if(PublicStaticDefineTab.MOD02.equals(queryBean.getModeType())){
			/*
			 * 1、判断主协议的内容是否变动；根据新推的主协议信息与库存中的协议信息作比较
			 */
			String modeContent = "";
			//判断主协议是否是否变动
			//主协议
			ptl = this.queryOnlineProtocolByCrdtNo(queryBean.getOnlineCrdtNo());
			modeContent = this.judgeProtocoIsModify(queryBean,ptl);
			/*
			 * 2、保存本次协议修改信息
			 */
			
			//ptl.setOpenDate(DateUtils.formatDate(ptl.getOpenDate(), DateUtils.ORA_DATES_FORMAT));
			ptl.setDueDate(DateUtils.formatDate(ptl.getDueDate(), DateUtils.ORA_DATES_FORMAT));
		
			this.copyProperties(ptl, queryBean);
			this.txStore(ptl);
			/**
			 * 3、先查询本协议的历史最新的一条数据 
			 */
			List<PedOnlineCrdtProtocolHist> oldList = this.queryOnlineCrdtPtlHistList(queryBean,null);

			/**
			 * 4、保存本次修改的协议历史
			 */
			//保存历史
			BeanUtils.copyProperties(hist, ptl);
			hist.setId(null);
			hist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());//修改标志
			hist.setCreateTime(new Date());
			hist.setUpdateTime(new Date());
			if(null != oldList && oldList.size()>0){
				PedOnlineCrdtProtocolHist old = oldList.get(0);
				hist.setLastSourceId(old.getId());
			}
			this.txStore(hist);
			
			
			/**
			 * 5、收款人信息保存；是否变更：查询库存中的收款人信息，与推送过来的收款人信息做对比
			 * 		保若有修改查询上次历史收款人，并保存本次历史收款人的LastSourceId
			 */
			//收款人
			List payees = queryBean.getPayees();
			if(null != payees && payees.size()>0){
				List crdtHists = new ArrayList();
				List payeeList = new ArrayList();
				boolean isPayee = true;//收票人是否改动
				PedOnlineCrdtInfoHist crdtHist = null;
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setOnlineCrdtNo(queryBean.getOnlineCrdtNo());
				List list = this.queryOnlineCrdtPayeeList(queryBean, null);
				for(int i=0;i<payees.size();i++){
					PedOnlineCrdtInfo payee = (PedOnlineCrdtInfo) payees.get(i);
					crdtHist = new PedOnlineCrdtInfoHist();
				    if(PublicStaticDefineTab.MOD00.equals(payee.getModeType())){
						payee.setCrdtId(ptl.getId());
						payee.setModeType(PublicStaticDefineTab.MOD00);
						isPayee = false;
					}else if(PublicStaticDefineTab.MOD01.equals(payee.getModeType())||PublicStaticDefineTab.MOD02.equals(payee.getModeType())){
						payee.setCrdtId(ptl.getId());
						if(PublicStaticDefineTab.MOD01.equals(payee.getModeType())){
							payee.setModeType(PublicStaticDefineTab.MOD01);
						}else{
							payee.setModeType(PublicStaticDefineTab.MOD02);
							if(null!=list){
								for(int j=0;j<list.size();j++){
									PedOnlineCrdtInfo info=(PedOnlineCrdtInfo)list.get(j);
									if(info.getPayeeId().equals(payee.getPayeeId())){
										//查询上次收款人历史数据
										bean.setPayeeAcctName(payee.getPayeeAcctName());
										bean.setPayeeId(payee.getPayeeId());
										List oldLists = this.queryOnlinePayeeHistList(bean);//本条修改数据的上一次历史
										if(oldLists != null && oldLists.size() >0){
											PedOnlineCrdtInfoHist old=(PedOnlineCrdtInfoHist)oldLists.get(0);
											crdtHist.setLastSourceId(old.getId());
										}
									}
								}
							}
						}
						BeanUtils.copyProperties(crdtHist, payee);
					}else if(PublicStaticDefineTab.MOD03.equals(payee.getModeType())){
						payee.setModeType(PublicStaticDefineTab.MOD03);
						payee.setCrdtId(ptl.getId());
						BeanUtils.copyProperties(crdtHist, payee);
					}
					crdtHist.setId(null);
					crdtHist.setCreateTime(new Date());
					crdtHist.setUpdateTime(new Date());
					crdtHist.setModeMark(hist.getModeMark());
					crdtHist.setOnlineCrdtNo(ptl.getOnlineCrdtNo());

					if(!PublicStaticDefineTab.MOD00.equals(payee.getModeType())){
					    crdtHists.add(crdtHist);
					}
					payee.setId(null);
					payee.setCreateTime(new Date());
					payee.setUpdateTime(new Date());
					payee.setOnlineCrdtNo(ptl.getOnlineCrdtNo());
					if(!PublicStaticDefineTab.MOD03.equals(payee.getModeType())){
						payeeList.add(payee);
					}
				}
				this.txDeleteAll(list);
				this.txStoreAll(crdtHists);
				this.txStoreAll(payeeList);

				if(isPayee){
					modeContent = modeContent+"收款人|";
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
				bean.setOnlineNo(queryBean.getOnlineCrdtNo());
				bean.setAddresseeRole(PublicStaticDefineTab.ROLE_0);
				List list = onlineManageService.queryOnlineMsgInfoList(bean,null);
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
							String Template = "您已签约"+ptl.getCustName()+"在汉口银行办理在线流贷的短信通知功能。";
							onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, msg.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,msg.getAddresseeName(),msg.getOnlineNo());
							msg.setModeType(PublicStaticDefineTab.MOD01);
						}else{
							//修改情况下通知短信至联系人
							msg.setModeType(PublicStaticDefineTab.MOD02);
							for(int j=0;j<list.size();j++){
								PedOnlineMsgInfo info=(PedOnlineMsgInfo)list.get(j);
								if(info.getAddresseeNo().equals(msg.getAddresseeNo())){

									//比较电话是否变更  若变更则发短信通知联系人
									if(!info.getAddresseePhoneNo().equals(msg.getAddresseePhoneNo())){
										String Template = "您已签约"+ptl.getCustName()+"在汉口银行办理在线流贷的短信通知功能。";
										onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, msg.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,msg.getAddresseeName(),msg.getOnlineNo());
										String tmp = "您已取消"+ptl.getCustName()+"在汉口银行办理在线流贷的短信通知功能。";
										onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, info.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, tmp,msg.getAddresseeName(),msg.getOnlineNo());
									}

									bean.setAddresseeNo(msg.getAddresseeNo());
									List lastList = onlineManageService.queryOnlineMsgHist(bean,null);
									if(lastList != null && lastList.size() > 0){
										PedOnlineMsgInfoHist old = (PedOnlineMsgInfoHist) lastList.get(0);
										//查询上次短信通知人历史数据
										msgHist.setLastSourceId(old.getId());
									}
								}
							}
						}
						BeanUtils.copyProperties(msgHist, msg);
					}else if(PublicStaticDefineTab.MOD03.equals(msg.getModeType())) {
						String Template = "您已取消"+ptl.getCustName()+"在汉口银行办理在线流贷的短信通知功能。";
						onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, msg.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,msg.getAddresseeName(),msg.getOnlineNo());
					
						msg.setOnlineProtocolId(ptl.getId());
						msg.setModeType(PublicStaticDefineTab.MOD03);
						BeanUtils.copyProperties(msgHist, msg);
					}
					msgHist.setId(null);
					msgHist.setCreateTime(new Date());
					msgHist.setModeMark(hist.getModeMark());
					msgHist.setOnlineNo(ptl.getOnlineCrdtNo());
					if(!PublicStaticDefineTab.MOD00.equals(msg.getModeType())){
						hisList.add(msgHist);
					}
					msg.setId(null);
					msg.setCreateTime(new Date());
					msg.setUpdateTime(new Date());
					msg.setOnlineNo(ptl.getOnlineCrdtNo());
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
			hist.setModeContent(modeContent);//修改内容  主协议|收票人|短信|担保合同|签约信息
			this.txStore(hist);
			
		}else{
			BeanUtils.copyProperties(ptl, queryBean);
			ptl.setCreateTime(new Date());
			ptl.setUpdateTime(new Date());
			this.txStore(ptl);

			BeanUtils.copyProperties(hist, ptl);
			hist.setId(null);
			hist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());//修改标志
			this.txStore(hist);
			//票据池客户信息数据落库
			CustomerRegister customer=new CustomerRegister();
			customer.setCustNo(queryBean.getCustNumber());
			customer.setCustName(queryBean.getCustName());
			customer.setFirstDateSource("PJE016");
			customerService.txSaveCustomerRegister(customer);
			if(null != queryBean.getPayees() && queryBean.getPayees().size()>0){
				List payList = new ArrayList();
				PedOnlineCrdtInfoHist crdtHist = null;
				for(int i=0;i<queryBean.getPayees().size();i++){
					PedOnlineCrdtInfo info = (PedOnlineCrdtInfo) queryBean.getPayees().get(i);
					info.setCrdtId(ptl.getId());
					info.setOnlineCrdtNo(ptl.getOnlineCrdtNo());
					crdtHist =  new PedOnlineCrdtInfoHist();
					BeanUtils.copyProperties(crdtHist, info);
					crdtHist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());
					crdtHist.setCreateTime(new Date());
					crdtHist.setUpdateTime(new Date());
					payList.add(crdtHist);
				}
				this.txStoreAll(queryBean.getPayees());
				this.txStoreAll(payList);
			}
			//查询前六个月内是否有同类型的在线协议;若有则将原协议下的短信联系人迁移至当前协议下
			OnlineQueryBean query = new OnlineQueryBean();
			query.setCustNumber(queryBean.getCustNumber());
			query.setCustName(queryBean.getCustName());
			query.setProtocolStatus("0");
			PedOnlineCrdtProtocol crdt = this.queryOnlineCrdtProtocol(query);
			List<PedOnlineMsgInfo> newList = new ArrayList<PedOnlineMsgInfo>();
			List<PedOnlineMsgInfoHist> histList = new ArrayList<PedOnlineMsgInfoHist>();
            if(null!=crdt){
            	if(DateUtils.calRangePcds(crdt.getChangeDate(), DateUtils.getCurrDate())){
    				//原始失效的协议日期与当前日期在六个月之内需要迁移短信联系人
    				List msgList = onlineManageService.queryOnlineMsgInfoList(crdt.getOnlineCrdtNo(), PublicStaticDefineTab.ROLE_1);
    				for (int i = 0; i < msgList.size(); i++) {
    					PedOnlineMsgInfo newInfo = new PedOnlineMsgInfo();
    					PedOnlineMsgInfoHist msgHist = new PedOnlineMsgInfoHist();				
    					PedOnlineMsgInfo old = (PedOnlineMsgInfo) msgList.get(i);
    					newInfo.setAddresseeNo(old.getAddresseeNo());
    					newInfo.setAddresseeName(old.getAddresseeName());
    					newInfo.setAddresseeRole(old.getAddresseeRole());
    					newInfo.setAddresseePhoneNo(old.getAddresseePhoneNo());
    					newInfo.setOnlineProtocolType(old.getOnlineProtocolType());
    					newInfo.setOnlineNo(ptl.getOnlineCrdtNo());//在线协议编号
    					newInfo.setOnlineProtocolId(ptl.getId());
    					newInfo.setUpdateTime(new Date());
    					//生成短信历史
    					BeanUtils.copyProperties(msgHist, newInfo);
    					msgHist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());
    					msgHist.setUpdateTime(new Date());
    					newList.add(newInfo);
    					histList.add(msgHist);
    				}
    			}
    		    if(null!=newList && null!=histList){
    		    	financialAdviceService.txCreateList(newList);
    		    	financialAdviceService.txCreateList(histList);
    		    }
            }
			
			
			
			if(null != queryBean.getDetalis() && queryBean.getDetalis().size()>0){
				List msgList = new ArrayList();
				PedOnlineMsgInfoHist msgHist = null;
				for(int i=0;i<queryBean.getDetalis().size();i++){
					PedOnlineMsgInfo info = (PedOnlineMsgInfo) queryBean.getDetalis().get(i);
					//新增员工信息通知联系人
					String Template = "您已签约"+ptl.getCustName()+"在汉口银行办理在线流贷的短信通知功能。";
					onlineManageService.toSendMsgForNotifier(PublicStaticDefineTab.ROLE_0, info.getAddresseePhoneNo(), PublicStaticDefineTab.PRODUCT_001, Template,info.getAddresseeName(),info.getOnlineNo());
				
					info.setOnlineProtocolId(ptl.getId());
					info.setOnlineNo(ptl.getOnlineCrdtNo());
					msgHist = new PedOnlineMsgInfoHist();
					BeanUtils.copyProperties(msgHist, info);
					msgHist.setModeMark(DateUtils.getCurrDateTime()+ptl.getId());
					msgHist.setCreateTime(new Date());
					msgHist.setUpdateTime(new Date());
					msgList.add(msgHist);
				}
				this.txStoreAll(queryBean.getDetalis());
				this.txStoreAll(msgList);
			}
		}
	}

	private void copyProperties(PedOnlineCrdtProtocol ptl,
			OnlineQueryBean bean) {
		ptl.setProtocolStatus(bean.getProtocolStatus());//在线协议状态
		ptl.setBaseCreditNo(bean.getBaseCreditNo());//基本授信额度编号
		ptl.setCustName(bean.getCustName());//客户名称
		ptl.setEbkCustNo(bean.getEbkCustNo());//网银客户号
		ptl.setPoolCreditRatio(bean.getPoolCreditRatio());//票据池比例（%）	
		ptl.setOnlineLoanTotal(bean.getOnlineLoanTotal());//在线流贷总额
		ptl.setRateFloatType(bean.getRateFloatType());//利率浮动方式
		ptl.setRateFloatValue(bean.getRateFloatValue());//利率浮动值（%）
		ptl.setOverRateFloatType(bean.getOverRateFloatType());//逾期利率浮动方式	
		ptl.setOverRateFloatValue(bean.getOverRateFloatValue());//逾期利率浮动值（%）
		ptl.setLoanAcctNo(bean.getLoanAcctNo());//放款账户账号
		ptl.setLoanAcctName(bean.getLoanAcctName());//放款账户名称
		ptl.setDeduAcctNo(bean.getDeduAcctNo());//扣款账户账号
		ptl.setDeduAcctName(bean.getDeduAcctName());//扣款账户名称
		ptl.setSignBranchNo(bean.getSignBranchNo());//签约机构所号
		ptl.setSignBranchName(bean.getSignBranchName());//签约机构名称
		ptl.setContractNo(bean.getContractNo());//担保合同编号
		ptl.setAppNo(bean.getAppNo());//经办人名称
		ptl.setAppName(bean.getAppName());//经办人编号
		ptl.setInAcctBranchNo(bean.getInAcctBranchNo());//入账机构所号
		ptl.setInAcctBranchName(bean.getInAcctBranchName());//入账机构名称
		ptl.setUpdateTime(bean.getUpdateTime());//变更日期
		ptl.setDueDate(bean.getDueDate());//到期日期
		ptl.setChangeDate(DateUtils.formatDate(bean.getChangeDate(), DateUtils.ORA_DATES_FORMAT));

	}

	private String judgeProtocoIsModify(OnlineQueryBean queryBean,
			PedOnlineCrdtProtocol ptl) {
		String str ="";
		//主协议
		if(!queryBean.getProtocolStatus().equals(ptl.getProtocolStatus())){
			str = "主协议|";
		}else if(queryBean.getBaseCreditNo().equals(ptl.getBaseCreditNo())){
			str = "主协议|";
		}else if(queryBean.getCustName().equals(ptl.getCustName())){
			str = "主协议|";
		}else if(queryBean.getEbkCustNo().equals(ptl.getEbkCustNo())){
			str = "主协议|";
		}else if(queryBean.getPoolCreditRatio().equals(ptl.getPoolCreditRatio())){
			str = "主协议|";
		}else if(queryBean.getOnlineLoanTotal().equals(ptl.getOnlineLoanTotal())){
			str = "主协议|";
		}else if(queryBean.getRateFloatType().equals(ptl.getRateFloatType())){
			str = "主协议|";
		}else if(queryBean.getRateFloatValue().equals(ptl.getRateFloatValue())){
			str = "主协议|";
		}else if(queryBean.getOverRateFloatType().equals(ptl.getOverRateFloatType())){
			str = "主协议|";
		}else if(queryBean.getOverRateFloatValue().equals(ptl.getOverRateFloatValue())){
			str = "主协议|";
		}else if(queryBean.getLoanAcctNo().equals(ptl.getLoanAcctNo())){
			str = "主协议|";
		}else if(queryBean.getLoanAcctName().equals(ptl.getLoanAcctName())){
			str = "主协议|";
		}else if(queryBean.getDeduAcctNo().equals(ptl.getDeduAcctNo())){
			str = "主协议|";
		}else if(queryBean.getDeduAcctName().equals(ptl.getDeduAcctName())){
			str = "主协议|";
		}else if(queryBean.getInAcctBranchNo().equals(ptl.getInAcctBranchNo())){
			str = "主协议|";
		}else if(queryBean.getInAcctBranchName().equals(ptl.getInAcctBranchName())){
			str = "主协议|";
		}
		//担保签约
		if(!queryBean.getGuarantorNo().equals(ptl.getGuarantorNo())){
			str = "担保合同|";
		}
		//签约
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

	public List<PedOnlineCrdtProtocolHist> queryOnlineCrdtPtlHistList(
			OnlineQueryBean queryBean, Page page) {
		String sql ="select dto from PedOnlineCrdtProtocolHist dto where 1=1 ";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				sql= sql+" and dto.onlineCrdtNo=?";
				param.add(queryBean.getOnlineCrdtNo());
			}
		}
		sql = sql+" order by dto.createTime desc";
		List result = this.find(sql, param, page);
		return result;
	}

	/**
	 * @author wss
	 * @date 2021-4-29
	 * @description 查询在线流贷收票人
	 * @param onlineCrdtNo
	 * @param status
	 */
	public List queryOnlinePayeeList(String onlineCrdtNo, String status) {
		String sql ="select dto from PedOnlineCrdtInfo dto where dto.onlineCrdtNo=? ";
		List param = new ArrayList();
		param.add(onlineCrdtNo);
		if(StringUtils.isNotBlank(status)){
			sql= sql+" and dto.payeeStatus=?";
			param.add(status);
		}
		List result = this.find(sql, param);
		return result;
	}

	@Override
	public List queryOnlineCrdtPayeeList(OnlineQueryBean queryBean, Page page) {
		String sql ="select dto from PedOnlineCrdtInfo dto,PedOnlineCrdtProtocol pro where dto.crdtId=pro.id ";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				sql= sql+" and pro.onlineCrdtNo=?";
				param.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeId())){
				sql= sql+" and pro.payeeId=?";
				param.add(queryBean.getPayeeId());
			}
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				sql= sql+" and pro.custNumber=?";
				param.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctNo())){
				sql= sql+" and dto.payeeAcctNo=?";
				param.add(queryBean.getPayeeAcctNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				sql= sql+" and dto.payeeStatus=?";
				param.add(queryBean.getPayeeStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getCrdtId())){
				sql= sql+" and dto.crdtId=?";
				param.add(queryBean.getCrdtId());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and  pro.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeAcctName())){
				sql= sql+" and dto.payeeAcctName like(?)";
				param.add("%"+queryBean.getPayeeAcctName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getId())){
				sql= sql+" and info.id=?";
				param.add(queryBean.getId());
			}
			//收款人行号
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankNo())){
				sql= sql+" and  dto.payeeOpenBankNo = ?";
				param.add(queryBean.getPayeeOpenBankNo());
			}
			//收款人开户行名
			if(StringUtils.isNotBlank(queryBean.getPayeeOpenBankName())){
				sql= sql+" and dto.payeeOpenBankName like ?";
				param.add("%"+queryBean.getPayeeOpenBankName()+"%");
			}
		}
		List result = new ArrayList();
		if(null != page){
			result = this.find(sql, param, page);
		}else{
			result = this.find(sql, param);
		}
		return result;
	}
	
	@Override
	public List queryOnlineCrdtPayeeListByBean(OnlineQueryBean queryBean, Page page) {
		/**
		 * 1、查询协议下的总已用的批次额度
		 */
		String sql = "SELECT sum(LOAN_AMT) from PL_ONLINE_CRDT WHERE ONLINE_CRDT_NO = '"+queryBean.getOnlineNo()+"' and Deal_Status not in('DS005') ";
		List crdt = sessionDao.SQLQuery(sql);
		BigDecimal bigcrdt = new BigDecimal(0);
		if(crdt != null && crdt.size() > 0){
			if(crdt.get(0) != null){
				bigcrdt = (BigDecimal) crdt.get(0);
			}
		}
		/**
		 * 2、查询协议下的总已用的支付计划额度
		 */
		String sql2 = "select sum(TOTAL_AMT) usedAmt from PL_CRDT_PAY_PLAN detail where detail.STATUS not in('P04') and ONLINE_CRDT_NO = '"+queryBean.getOnlineNo()+"'";
		List pay = sessionDao.SQLQuery(sql2);
		BigDecimal bigpay = new BigDecimal(0);
		if(pay != null && pay.size() > 0){
			if(pay.get(0) != null){
				bigpay = (BigDecimal) pay.get(0);
			}
		}
		BigDecimal bigres = bigcrdt.subtract(bigpay);
		
		/**
		 * 3、统计各收款人的已用额度
		 */
		StringBuffer hql = new StringBuffer();
		hql.append("select info.ID,info.PAYEE_ID,info.PAYEE_ACCT_NAME,info.PAYEE_ACCT_NO,info.PAYEE_OPEN_BANK_NO,info.PAYEE_OPEN_BANK_NAME,info.PAYEE_TOTAL_AMT,info.PAYEE_STATUS,info.ONLINE_CRDT_NO,info.CRDT_ID,info.CREATE_TIME,info.UPDATE_TIME,d.usedAmt,info.IS_LOCAL " +
				"from PED_ONLINE_CRDT_INFO info left join PED_ONLINE_CRDT_PROTOCOL dto on info.CRDT_ID = dto.id left join (select sum(detail.TOTAL_AMT-detail.REPAY_AMT) usedAmt,DEDU_ACCT_NO,ONLINE_CRDT_NO from PL_CRDT_PAY_PLAN detail where detail.STATUS not in(:status) group by DEDU_ACCT_NO,ONLINE_CRDT_NO) d on info.PAYEE_ACCT_NO = d.DEDU_ACCT_NO and d.ONLINE_CRDT_NO = info.ONLINE_CRDT_NO where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("status");
		queryBean.getStatuList().add(PublicStaticDefineTab.PAY_PLAN_04);
		paramValue.add(queryBean.getStatuList());
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				hql.append(" and info.ONLINE_CRDT_NO =:onlineCrdtNo");
				paramName.add("onlineCrdtNo");
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
			if(StringUtils.isNotBlank(queryBean.getCrdtId())){
				hql.append(" and info.CRDT_ID=:acptId");
				paramName.add("acptId");
				paramValue.add(queryBean.getCrdtId());
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
				hql.append(" and info.ONLINE_CRDT_NO =:onlineCrdtNo");
				paramName.add("onlineCrdtNo");
				paramValue.add(queryBean.getOnlineNo());
			}
		}
		
		hql.append(" order by info.PAY_TYPE ASC ,info.PAYEE_ID ASC ");
		
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
  				if(obj[3] == null){
  					//收款人账号为空，是自主支付
  					bean.setPayeeUsedAmt(bigres);
  				}else{
  					bean.setPayeeUsedAmt((BigDecimal)obj[12]!= null?(BigDecimal)obj[12]:new BigDecimal(0));
  				}
  				bean.setIsLocal((String)obj[13]);
  				bean.setPayeeFreeAmt(bean.getPayeeTotalAmt().subtract(bean.getPayeeUsedAmt()));
  				result.add(bean);    
  			}
  			return result;
  		}
		return list;
	}

	
	public List queryOnlineCrdtPayeeHistListByBean(OnlineQueryBean queryBean, Page page) {
		StringBuffer hql = new StringBuffer();
		hql.append("select info.ID,info.PAYEE_ID,info.PAYEE_ACCT_NAME,info.PAYEE_ACCT_NO,info.PAYEE_OPEN_BANK_NO,info.PAYEE_OPEN_BANK_NAME,info.PAYEE_TOTAL_AMT,info.PAYEE_STATUS,info.ONLINE_CRDT_NO,info.CRDT_ID,info.CREATE_TIME,info.UPDATE_TIME,d.usedAmt,info.IS_LOCAL ,info.mode_type " +
				"from PED_ONLINE_CRDT_INFO_HIST info left join PED_ONLINE_CRDT_PROTOCOL dto on info.CRDT_ID = dto.id left join (select sum(detail.TOTAL_AMT) usedAmt,DEDU_ACCT_NO from PL_CRDT_PAY_PLAN detail where detail.STATUS not in(:status) group by DEDU_ACCT_NO) d on info.PAYEE_ACCT_NO = d.DEDU_ACCT_NO where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("status");
		queryBean.getStatuList().add(PublicStaticDefineTab.PAY_PLAN_04);
		paramValue.add(queryBean.getStatuList());
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
				hql.append(" and info.ONLINE_CRDT_NO =:onlineCrdtNo");
				paramName.add("onlineCrdtNo");
				paramValue.add(queryBean.getOnlineNo());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeStatus())){
				hql.append(" and info.PAYEE_STATUS=:payeeStatus");
				paramName.add("payeeStatus");
				paramValue.add(queryBean.getPayeeStatus());
			}
//			if(StringUtils.isNotBlank(queryBean.getCrdtId())){
//				hql.append(" and info.CRDT_ID=:acptId");
//				paramName.add("acptId");
//				paramValue.add(queryBean.getCrdtId());
//			}
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
				hql.append(" and info.mode_mark =:mode_mark");
				paramName.add("mode_mark");
				paramValue.add(queryBean.getModeMark());
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
  				bean.setIsLocal((String)obj[13]);
  				bean.setPayeeFreeAmt(bean.getPayeeTotalAmt().subtract(bean.getPayeeUsedAmt()));
  				bean.setModeType((String)obj[14]);
  				result.add(bean);    
  			}
  			return result;
  		}
		return list;
	}
	@Override
	public List queryPlCrdtPayPlanListByBean(OnlineQueryBean queryBean,Page page) {
		String sql ="select dto from PlCrdtPayPlan dto,PedOnlineCrdtProtocol pro where dto.onlineCrdtNo=pro.onlineCrdtNo";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				sql= sql+" and dto.onlineCrdtNo=?";
				param.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				sql= sql+" and pro.custNumber=?";
				param.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getStatus())){
				sql= sql+" and dto.status=?";
				param.add(queryBean.getStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getStatus())){
				sql= sql+" and dto.status =?";
				param.add(queryBean.getStatus());
			}
			if(queryBean.getStatuList() != null && queryBean.getStatuList().size() > 0){
				sql= sql+" and dto.status in('P02','P03')";
//				param.add(queryBean.getStatuList());
			}
			if(StringUtils.isNotBlank(queryBean.getContractNo())){
				sql= sql+" and dto.contractNo=?";
				param.add(queryBean.getContractNo());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and  pro.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getDeduAcctName())){
				sql= sql+" and dto.deduAcctName like(?)";
				param.add("%"+queryBean.getDeduAcctName()+"%");
			}
			if(null != queryBean.getStartAmt()){
				sql= sql+" and dto.totalAmt <=?";
				param.add(queryBean.getStartAmt());
			}
			if(null != queryBean.getEndAmt()){
				sql= sql+" and dto.totalAmt >=?";
				param.add(queryBean.getEndAmt());
			}
			if(StringUtils.isNotBlank(queryBean.getLoanNo())){
				sql= sql+" and dto.loanNo=?";
				param.add(queryBean.getLoanNo());
			}
			if(StringUtils.isNotBlank(queryBean.getSerialNo())){
				sql= sql+" and dto.serialNo=?";
				param.add(queryBean.getSerialNo());
			}
			if(StringUtils.isNotBlank(queryBean.getCrdtId())){
				sql= sql+" and dto.crdtId=?";
				param.add(queryBean.getCrdtId());
			}
			if(StringUtils.isNotBlank(queryBean.getIds())){
				sql= sql+" and dto.id= in(?)";
				/* 将数组转换成List */
				param.add(Arrays.asList(queryBean.getIds().split(",")));
			}
			if(StringUtils.isNotBlank(queryBean.getBatchId())){
				sql= sql+" and dto.crdtId=?";
				param.add(queryBean.getBatchId());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeSerialNo())){
				sql= sql+" and dto.serialNo=?";
				param.add(queryBean.getPayeeSerialNo());
			}
		}
		List result = this.find(sql, param, page);
		if(null != result && result.size()>0){
			
			return result;
		}
		return null;
	}

	@Override
	public List queryPlCrdtPayCachePlanListByBean(OnlineQueryBean queryBean,Page page) {
		String sql ="select dto from PlCrdtPayCachePlan dto,PedOnlineCrdtProtocol pro where dto.onlineCrdtNo=pro.onlineCrdtNo";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				sql= sql+" and dto.onlineCrdtNo=?";
				param.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				sql= sql+" and pro.custNumber=?";
				param.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getStatus())){
				sql= sql+" and dto.status=?";
				param.add(queryBean.getStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getContractNo())){
				sql= sql+" and dto.contractNo=?";
				param.add(queryBean.getContractNo());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and  pro.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getDeduAcctName())){
				sql= sql+" and dto.deduAcctName like(?)";
				param.add("%"+queryBean.getDeduAcctName()+"%");
			}
			if(null != queryBean.getStartAmt()){
				sql= sql+" and dto.totalAmt <=?";
				param.add(queryBean.getStartAmt());
			}
			if(null != queryBean.getEndAmt()){
				sql= sql+" and dto.totalAmt >=?";
				param.add(queryBean.getEndAmt());
			}
			if(StringUtils.isNotBlank(queryBean.getLoanNo())){
				sql= sql+" and dto.loanNo=?";
				param.add(queryBean.getLoanNo());
			}
			if(StringUtils.isNotBlank(queryBean.getSerialNo())){
				sql= sql+" and dto.serialNo=?";
				param.add(queryBean.getSerialNo());
			}
			if(StringUtils.isNotBlank(queryBean.getCrdtId())){
				sql= sql+" and dto.crdtId=?";
				param.add(queryBean.getCrdtId());
			}
			if(StringUtils.isNotBlank(queryBean.getIds())){
				sql= sql+" and dto.id= in(?)";
				/* 将数组转换成List */
				param.add(Arrays.asList(queryBean.getIds().split(",")));
			}
			if(StringUtils.isNotBlank(queryBean.getBatchId())){
				sql= sql+" and dto.crdtId=?";
				param.add(queryBean.getBatchId());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeSerialNo())){
				sql= sql+" and dto.serialNo=?";
				param.add(queryBean.getPayeeSerialNo());
			}
		}
		List result = this.find(sql, param, page);
		if(null != result && result.size()>0){
			
			return result;
		}
		return null;
	}
	
	/**
	 * 流贷全量校验
	 * @throws Exception 
	 */
	public ReturnMessageNew txCrdtFullCheck(OnlineQueryBean queryBean) throws Exception {
		
		logger.info("在线流贷业务校验开始...");
		
		ReturnMessageNew response =  new ReturnMessageNew();
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		
		//在线流贷协议校验
        PedOnlineCrdtProtocol protocol = this.queryOnlineProtocolByCrdtNo(queryBean.getOnlineCrdtNo());
        if(null == protocol){
        	response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
			response.getBody().put("CHECK_INFO", "票据池系统不存在["+queryBean.getOnlineCrdtNo()+"]在线流贷协议信息！");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池系统不存在有效的["+queryBean.getOnlineCrdtNo()+"]在线流贷协议信息！");
			response.setRet(ret);
			return response;
        }
        //在线流贷协议客户开关校验
        if(protocol.getLdFlag().equals(PoolComm.NO)){
        	//流贷协议开关为关闭
        	response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
			response.getBody().put("CHECK_INFO", "在线流贷协议客户开关为关闭！");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("流贷协议客户开关为关闭！");
			response.setRet(ret);
			return response;
        }
        
        //必须有票据池
        PedProtocolDto pool = (PedProtocolDto) this.dao.load(PedProtocolDto.class, protocol.getBpsId());
        if(null == pool || !PoolComm.OPEN_01.equals(pool.getOpenFlag())){
        	response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
			response.getBody().put("CHECK_INFO", "票据池系统无生效的融资票据池协议！");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池系统无生效的融资票据池协议！");
			response.setRet(ret);
			return response;
		}
        
      //【2.1】票据池协议校验
		if(pool.getOpenFlag().equals(PoolComm.OPEN_01) && pool.getEffenddate().compareTo(new Date())<0 && pool.getIsGroup().equals(PoolComm.NO) ){
			response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
			response.getBody().put("CHECK_INFO", "票据池协议已失效");
			ret.setRET_MSG("票据池协议已失效");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			response.setRet(ret);
			return response;
		}
        
        
        //在线业务禁入名单校验
		queryBean.setCustName(protocol.getCustName());
		queryBean.setCustNumber(protocol.getCustNumber());
//		queryBean.setCustOrgcode(protocol.getCustOrgcode());
		queryBean.setStatus(PublicStaticDefineTab.STATUS_1);//生效
		boolean flag = onlineManageService.onlineBlackListCheck(queryBean);
		if(flag){
			logger.info(protocol.getCustNumber()+"该客户已被加入线上禁入名单");
			response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
			response.getBody().put("CHECK_INFO", "在线协议已失效");
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("该"+queryBean.getOnlineCrdtNo()+"在线协议已失效");
			response.setRet(ret);
			return response;
		}
        
        //1.在线流贷业务开关(总开关、)、运营时间 如果失败直接跳出
		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
			ret = onlineManageService.checkOnlineSwitch(protocol.getSignBranchNo(),PublicStaticDefineTab.PRODUCT_002);
			if(!ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				String msg=ret.getRET_MSG()+ret.getError_MSG();
				if(msg.length() > 0){
					ret.setRET_MSG(msg.substring(0, msg.length()-1));
				}
				response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
				response.getBody().put("CHECK_INFO", ret.getRET_MSG());
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				response.setRet(ret);
				return response;
			}
		}
        
		
		
		if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
			PlOnlineCrdt onlineCrdt=(PlOnlineCrdt)queryBean.getCrdtBatchList().get(0);
			String payType=PublicStaticDefineTab.PAY_1;
			List<PlOnlineCrdt> list=queryBean.getCrdtBatchList();
			  for(PlOnlineCrdt crdt : list){
				  if(PublicStaticDefineTab.PAY_2.equals(crdt.getPayType())){
					  payType=crdt.getPayType();
				  }
			  }
			List details = queryBean.getDetalis();

				String loanNo = "";
				
				Map map = new HashMap();
				String commError ="";//禁止
				String commMsg = "";//提示
				
				
				//担保合同限额校验
				BigDecimal creditamount = pool.getCreditamount();//最高额担保合同金额
				BigDecimal use = pool.getCreditUsedAmount().add(queryBean.getOnlineLoanTotal().multiply(onlineCrdt.getPoolCreditRatio().divide(new BigDecimal("100")))) ;//票据池担保额度已用金额  + 本次在线业务合同占用总金额*系数
				BigDecimal result = creditamount.subtract(use);//可用额度
				if(result.doubleValue()<0){//如果该客户    已用额度 + 本次需要占用额度 > 担保金额    则不允许占用
					commError+= "票据池担保合同余额不足|";
				}
				
				//票据池池额度校验
				commMsg += this.txPoolCreditCheck(queryBean, pool);
				
				
				//关联票据池校验
				Map comm = this.crdtApplyCheck(queryBean,protocol,pool);
				commError += (String) comm.get("error");//禁止
				commMsg += (String) comm.get("msg");//提示
				
				
				//协议额度校验
				BigDecimal usedAmt = this.getOnlineCrdtPtlAmt(queryBean);
				//BigDecimal addsUedAmt = usedAmt.multiply(queryBean.getCrdtBatchList().get(0).getPoolCreditRatio().divide(new BigDecimal("100")));//本次协议金额 * 票据池占用比例 %
				if(protocol.getOnlineLoanTotal().subtract(usedAmt).compareTo(queryBean.getOnlineLoanTotal())<0){
					
					commError +="在线流贷合同余额小于客户申请金额|";
					
				}
	        	 List<PlCrdtPayCachePlan> arr =new ArrayList<PlCrdtPayCachePlan>();
	        	 Map payyMap = new HashMap();
//	        	 if(payType.equals(PublicStaticDefineTab.PAY_2)){
	        		 if(null != details && details.size()>0){
							//明细项校验
							for(int i=0;i<details.size();i++){
								String error = "";//禁止
								String msg = "";//提示
								String pass = "";//通过
								PlCrdtPayPlan detail = (PlCrdtPayPlan) details.get(i);
								loanNo = detail.getLoanNo();
								//收款信息校验
								map = this.crdtPayeeCheck(queryBean,detail,msg,error,map,payyMap);
								
								//5.返回信息
								map.put("SERIAL_NO", detail.getSerialNo());//序号
								msg = msg + (String)map.get("msg");
								error = error + (String)map.get("error");
								if(error.length()>0){
									commError = commError + detail.getSerialNo() + "|" + error + ";";
								}
								if(msg.length()>0){
									commMsg = commMsg + detail.getSerialNo() + "|" + msg + ";";
								}
								if(error.length()>0){//校验结果 0：不通过 1：通过
									ret.setRET_CODE(Constants.TX_FAIL_CODE);
									ret.setRET_MSG("票据池系统校验未通过！");
									map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
								}else{
									map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);
									pass = "通过";//校验结果说明
								}
								map.put("CHECK_RESULT_REMARK", error+msg+pass);//校验结果说明
								map.remove("error");
								map.remove("msg");
								
								//返回文件明细内容
								response.getDetails().add(map);
								
								//6.明细校验日志保存
								if(error.length()>0 || msg.length()>0){
									PlCrdtPayPlan tail = (PlCrdtPayPlan) details.get(i);
									PlCrdtPayCachePlan hisDetail=new PlCrdtPayCachePlan();
				        			BeanUtils.copyProperties(hisDetail, tail);
				        			hisDetail.setCrdtId("1");
				        			onlineManageService.txStore(hisDetail);
				        			arr.add(hisDetail);
									PedOnlineHandleLog log = new PedOnlineHandleLog();
									log.setBusiName("在线流贷申请-明细日志");     //业务名称   
									log.setTradeName(PublicStaticDefineTab.CHANNEL_NO_EBK);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
									log.setTradeCode("PJC071");    //报文码  
									log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
									if(error.length() > 3000){
										log.setTradeResult(error.substring(0,2999));  //处理结果
									}else{
										log.setTradeResult(error);  //处理结果
									}
									log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
									log.setOperationType(queryBean.getOperationType());//岗位     经办、复核
									log.setBillNo(loanNo+"-"+hisDetail.getSerialNo());       //票号、批次号--只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录
									log.setBusiId(hisDetail.getId());       //业务id --只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录 
									log.setOnlineNo(protocol.getOnlineCrdtNo());     //在线协议编号
									log.setCustNumber(protocol.getCustNumber());   //客户号
									log.setBpsNo(protocol.getBpsNo());
									onlineManageService.txSaveTrdeLog(log);
								}
							}
			         } else{
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("无支付计划明细信息！");
							response.setRet(ret);					     
						    return response;
			         }
//	        	 }
				
				//6.全部校验日志保存
				
				if(commError.length()>0 || commMsg.length()>0){
					//保存历史 批次明细表
					PlOnlineCacheCrdt hisBatch=new PlOnlineCacheCrdt();
	    			BeanUtils.copyProperties(hisBatch, onlineCrdt);
        			hisBatch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);//失败
	    			this.txStore(hisBatch);
					List<PlCrdtPayCachePlan> arry =new ArrayList<PlCrdtPayCachePlan>();
	    			for(PlCrdtPayCachePlan plan :arr){
	    				plan.setCrdtId(hisBatch.getId());
	    				arry.add(plan);
	    			}
	    			this.txStoreAll(arry);
	    			String msg=commError+commMsg;
	   			    if(msg.lastIndexOf("^") == msg.length()-1){//去除最后^
	   				 msg= msg.substring(0, msg.length()-1); 
	   			     }
	   			    if(msg.lastIndexOf("|") == msg.length()-1){//去除最后|
	   				   msg= msg.substring(0, msg.length()-1); 
	   			     }
					PedOnlineHandleLog log = new PedOnlineHandleLog();
					log.setBusiName("在线流贷申请-汇总日志");     //业务名称   
					log.setTradeName(PublicStaticDefineTab.CHANNEL_NO_EBK);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
					log.setTradeCode("PJC071");    //报文码  
					log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
					if(msg.length() > 3000){
						log.setTradeResult(msg.substring(0,2999));  //处理结果
					}else{
						log.setTradeResult(msg);  //处理结果
					}
					log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
					log.setOperationType(queryBean.getOperationType());//岗位     经办、复核
					log.setBillNo(loanNo);       //票号、批次号--只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录
					log.setBusiId(hisBatch.getId());       //业务id  --只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录
					log.setOnlineNo(protocol.getOnlineCrdtNo());     //在线协议编号
					log.setCustNumber(protocol.getCustNumber());   //客户号
					log.setBpsNo(protocol.getBpsNo());
					onlineManageService.txSaveTrdeLog(log);
					
				}
				
				response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);//通过
				
				if(commMsg.trim().length()>0){
					response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_2);//提示性错误
				}
				
				if(commError.trim().length()>0){
					response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);//禁止性错误
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
				}
				
				if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){//复核--复核时候无提示性错误，均为禁止性错误
					
					if(commMsg.trim().length()>0||commError.trim().length()>0){
						response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);//禁止性错误
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
					}
				}
				
				
				response.getBody().put("ONLINE_CRDT_NO", queryBean.getOnlineCrdtNo());//在线流贷编号
				if(commError.trim().length()>0 || commMsg.trim().length()>0){
					String msg=commError+commMsg;
	   			    if(msg.lastIndexOf("^") == msg.length()-1){//去除最后^
	   				   msg= msg.substring(0, msg.length()-1); 
	   			     }
	   			    if(msg.lastIndexOf("|") == msg.length()-1){//去除最后|
	   				   msg= msg.substring(0, msg.length()-1); 
	   			     }
					response.getBody().put("CHECK_INFO", msg);
					ret.setRET_MSG(msg);
				}else{
					response.getBody().put("CHECK_INFO", "通过");
					ret.setRET_MSG("通过");
				}
		}
		else{
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("通过");
			response.getBody().put("ONLINE_CRDT_NO", queryBean.getOnlineCrdtNo());//在线流贷编号
		}
        response.setRet(ret);
        
        logger.info("在线流贷业务校验结束！");
        
		return response;
	}
	
	/**
	 * 在线流贷业务票据池池额度校验
	 * @param queryBean
	 * @param pool
	 * @return
	 * @author Ju Nana
	 * @date 2021-7-16下午2:41:06
	 */
	private String txPoolCreditCheck(OnlineQueryBean queryBean,PedProtocolDto pool){
		
		logger.info("在线流贷业务票据池池额度校验开始...");
		
		String checkMsg = "";
				
		try {
			
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pool);
			String apId = ap.getApId();
			CreditProduct product = queryBean.getProduct();
			List<PedCreditDetail> crdtDetailList = queryBean.getCrdtDetailList();
			List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
			String flowNo = Long.toString(System.currentTimeMillis());//额度试算流水号
			for(PedCreditDetail crdtDetail:crdtDetailList){
				CreditRegisterCache crdtReg = creditRegisterService.createCreditRegisterCache(crdtDetail, product, pool, apId);
				crdtReg.setFlowNo(flowNo);
				crdtRegList.add(crdtReg);
			}
			
			
			//保证金同步及额度计算及资产表重置
			apId = ap.getApId();
			financialService.txUpdateBailAndCalculationCredit(apId, pool);
			
			//额度校验
			Ret crdtCheckRet =  financialService.txCreditUsedCheck(crdtRegList, pool,flowNo);
			
			if(crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				logger.info("票据池额度充足！");
				
			}else{

				if(pool.getPoolMode().equals(PoolComm.POOL_MODEL_01)){
					//总量模式
					logger.info("当前票据池低风险可用额度不足");
					checkMsg += "当前票据池低风险可用额度不足^";
				}else{
					
					//这行逻辑不想解释，只为了满足业务要求的一条提示语，非要加个总量的校验，很讨厌，因为并没用
					
					//票据池保证金资产额度
	     	        AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);		     	        
	     	        //低风险票资产额度
	     	        AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
	     	        BigDecimal limitBalance = atBail.getCrdtFree().add(atBillLow.getCrdtFree());   //票据池低风险可用额度 
					
	     	        if(queryBean.getOnlineLoanTotal().compareTo(limitBalance)>0){
	     	        	checkMsg += "票据池额度不足^";
	     	        }else{		     	        	
	     	        	checkMsg += "票据池额度期限匹配不通过^";
	     	        	logger.info("票据池额度期限匹配不通过");
	     	        }
					
				}
			}
		} catch (Exception e) {
			checkMsg += "当前票据池低风险可用额度不足|";
		}
		
		logger.info("在线流贷业务票据池池额度校验结束,校验结果："+checkMsg);
		
		return checkMsg;
	}
	
	/**
	 * @Title getOnlineCrdtPtlAmt
	 * @author wss
	 * @date 2021-7-5
	 * @Description 获取协议已用额度
	 * @return BigDecimal
	 */
	private BigDecimal getOnlineCrdtPtlAmt(OnlineQueryBean queryBean) {
		BigDecimal loanAmt = new BigDecimal(0);
		BigDecimal repayAmt = new BigDecimal(0);
		
		
		/**
		 * 1、统计业务批次的借据金额
		 */
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select sum(dto.loanAmt) from PlOnlineCrdt dto,PedOnlineCrdtProtocol pro where dto.onlineCrdtNo=pro.onlineCrdtNo ");
		
		hql.append(" and pro.onlineCrdtNo =:onlineCrdtNo");
		paramName.add("onlineCrdtNo");
		paramValue.add(queryBean.getOnlineCrdtNo());
			
		hql.append(" and dto.dealStatus not in(:dealStatus)");
		paramName.add("dealStatus");
		List list = new ArrayList();
		list.add(PublicStaticDefineTab.ONLINE_DS_005);
		paramValue.add(list);
		
		/**
		 * 因流贷协议发送与后续处理存在先落库情景,导致统计时将本次业务金额一起统计
		 * 所以需去掉本次业务的统计
		 */
		if(queryBean.getCrdtBatchList() != null && queryBean.getCrdtBatchList().size() > 0){
			hql.append(" and dto.id !=:id");
			paramName.add("id");
			paramValue.add(queryBean.getCrdtBatchList().get(0).getId());
		}
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues );
		if(null != result && result.size()>0){
			loanAmt = result.get(0)!=null?(BigDecimal) result.get(0):new BigDecimal(0);
		}
		
		/**
		 * 2、统计已发生业务的支付计划的释放金额
		 */
		List name = new ArrayList();// 名称
		List value = new ArrayList();// 值
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(dto.repayAmt) from PlCrdtPayPlan dto,PedOnlineCrdtProtocol pro where dto.onlineCrdtNo=pro.onlineCrdtNo ");
		
		sql.append(" and pro.onlineCrdtNo =:onlineCrdtNo");
		name.add("onlineCrdtNo");
		value.add(queryBean.getOnlineCrdtNo());
			
		sql.append(" and dto.status not in(:status)");
		name.add("status");
		List list2 = new ArrayList();
		list2.add(PublicStaticDefineTab.PAY_PLAN_04);
		value.add(list2);
		
		/**
		 * 因流贷协议发送与后续处理存在先落库情景,导致统计时将本次业务金额一起统计
		 * 所以需去掉本次业务的统计
		 */
		if(queryBean.getPayList() != null && queryBean.getPayList().size() > 0){
			sql.append(" and dto.id !=:id");
			name.add("id");
			value.add(queryBean.getPayList().get(0).getId());
		}
		String paramNames2[] = (String[]) name.toArray(new String[name.size()]);
		Object paramValues2[] = value.toArray();
		List result2 = this.find(sql.toString(), paramNames2, paramValues2 );
		if(null != result2 && result2.size()>0){
			repayAmt = result2.get(0)!=null?(BigDecimal) result2.get(0):new BigDecimal(0);
		}
		
		return loanAmt.subtract(repayAmt);
	}

	private Map crdtApplyCheck(OnlineQueryBean queryBean,PedOnlineCrdtProtocol protocol, PedProtocolDto pool) throws Exception {
		
		
		logger.info("在线流贷业务票据池系统内基础校验开始...");
		
		Map resultMap = new HashMap();
		String error = "";//禁止
		String msg = "";//提示
		//在线流贷编号判断是否流贷类型
		if(null == pool){
			error = error+"该"+queryBean.getOnlineCrdtNo()+"在线流贷协议关联票据池不存在|";
		}else{
			//若为连续两个月对账任务未处理或最新对账结果为“核对不一致”，拒绝。
			Ret canCreateCredit = pedProtocolService.isCanCreateCreditByCheckResult(pool);
			if(canCreateCredit.getRET_CODE().equals(Constants.CREDIT_10)) { 
				error = error+"核对不一致或连续两个月票据池业务未对账|";
			}
			//票据池收费 若有欠费，拒绝。
			if(!pedProtocolService.isPaid(pool)){
				error = error+"票据池有欠费|";
			}
			
			//票据池状态若为“已冻结”，拒绝。
			if(PoolComm.FROZEN_STATUS_03.equals(pool.getFrozenstate())){
				error = error+"票据池已冻结|";
			}
			//票据池融资签约状态若未签约融资功能，拒绝。
			if(!PoolComm.OPEN_01.equals(pool.getOpenFlag())){
				error = error+"票据池未签约融资功能|";
			}
			//票据池担保合同失效(或超过到期日)
			if(pool.getContractDueDt().compareTo(new Date())<=0){
				error = error+"票据池担保合同已失效|";
			}
			//票据池担保合同号与在线协议担保合同号一致
			if(!pool.getContract().equals(protocol.getContractNo())){
				error = error+"票据池担保合同号与在线协议担保合同号不一致^";
			}
			//票据池担保合同可用额度小于Roundup（贷款金额*对应在线流贷协议—票据池额度比例，2）
			BigDecimal big = new BigDecimal(0);
			big = protocol.getPoolCreditRatio().divide(new BigDecimal(100));
			big = big.multiply(queryBean.getLoanAmt());//需占用额度
			logger.info("需占用额度..................."+big);
			if(pool.getCreditFreeAmount().compareTo(big.setScale(2, RoundingMode.UP))<0){
				error = error+"票据池担保合同余额不足|";
			}
			
		}
		//贷款期限
		if(DateTimeUtil.isMoreThanNYear(DateUtils.getCurrDate(), queryBean.getDueDate(),1)){
			error = error+"贷款期限超过一年|";
		}
		//在线流贷协议状态   若为“失效”或超过到期日
		if(!PublicStaticDefineTab.STATUS_1.equals(protocol.getProtocolStatus()) || (protocol.getDueDate().compareTo(DateUtils.getWorkDayDate())<=0)){
			error = error+"在线流贷协议已失效|";
		}
		/**
		 * 外层方法已存在合同金额的校验(此校验暂时去掉)
		 */
		//在线流贷合同可用余额（即在线流贷协议可用余额）
//		BigDecimal usedAmt = this.getOnlineCrdtPtlAmt(queryBean);
//		if(queryBean.getLoanAmt().compareTo(protocol.getOnlineLoanTotal().subtract(usedAmt))>0){
//			error = error+"在线流贷合同余额小于客户申请金额|";
//		}
		//控制开关
		if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
			Ret ret = onlineManageService.checkOnlineSwitch(protocol.getSignBranchNo(),PublicStaticDefineTab.PRODUCT_002);
			if(!ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				msg = msg+ret.getRET_MSG();
				error = error+ret.getError_MSG();
			}
		}
		logger.info("经办类型:"+queryBean.getOperationType()+"...................................");
		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
			try {			
				CoreTransNotes note = new CoreTransNotes();
				note.setAccNo(protocol.getDeduAcctNo());//扣款账号
				logger.info("扣款账号:"+protocol.getDeduAcctNo()+"...................................");
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
					
					String DR_CONTROL_BALANCE = (String) result.getBody().get("DR_CONTROL_BALANCE");//借方控制余额
					String BALANCE = (String) result.getBody().get("BALANCE");//余额
					
					//超金额冻结 ：   借方控制余额  >  余额  即 DR_CONTROL_BALANCE  >  BALANCE
					BigDecimal moreAmt =  new BigDecimal(DR_CONTROL_BALANCE).subtract(new BigDecimal(BALANCE));
					
					if("9".equals(status)){
						error = error+"扣款账户状态异常|";
					}
					else
					if(moreAmt.compareTo(BigDecimal.ZERO)>0){
						error = error+"扣款账户状态异常|";
					}else if("1".equals(ANNUAL_CHECK_FLAG)){
						error = error+"扣款账户状态异常|";
					}
					else
					if("0".equals(APPROVE_FLAG)){
						error = error+"扣款账户状态异常|";
					}else
					if("1".equals(IS_STOP_FLAG)){
						error = error+"扣款账户状态异常|";
					}
				}
				
				CoreTransNotes note1 = new CoreTransNotes();
				note1.setAccNo(protocol.getLoanAcctNo());//放款账号
				logger.info("放款账号:"+protocol.getLoanAcctNo()+"...................................");
				note1.setCurrentFlag("1");
				ReturnMessageNew result1 = poolCoreService.PJH716040Handler(note1, "0");
				if(result1.isTxSuccess()){
					//扣收账户状态
					String status = (String) result1.getBody().get("ACCT_STATUS");
					//是否未年检
					String ANNUAL_CHECK_FLAG = (String) result1.getBody().get("ANNUAL_CHECK_FLAG");
					//核准状态
					String APPROVE_FLAG = (String) result1.getBody().get("APPROVE_FLAG");
					//是否冻结--不用了
					String IS_FRZ_FLAG = (String) result1.getBody().get("IS_FRZ_FLAG");
					//是否止付
					String IS_STOP_FLAG = (String) result1.getBody().get("IS_STOP_FLAG");
					
					String DR_CONTROL_BALANCE = (String) result.getBody().get("DR_CONTROL_BALANCE");//借方控制余额
					String BALANCE = (String) result.getBody().get("BALANCE");//余额
					
					//超金额冻结 ：   借方控制余额  >  余额  即 DR_CONTROL_BALANCE  >  BALANCE
					BigDecimal moreAmt =  new BigDecimal(DR_CONTROL_BALANCE).subtract(new BigDecimal(BALANCE));
					
					if("9".equals(status)){
						error = error+"放款账户状态异常|";
					}
					else
					if(moreAmt.compareTo(BigDecimal.ZERO)>0){
						error = error+"放款账户状态异常|";
					}
					else
					if("1".equals(ANNUAL_CHECK_FLAG)){
						error = error+"放款账户状态异常|";
					}
					else
					if("0".equals(APPROVE_FLAG)){
						error = error+"放款账户状态异常|";
					}else
					if("1".equals(IS_STOP_FLAG)){
						error = error+"放款账户状态异常|";
					}
				}
				
				
			} catch (Exception e) {
				error = error+"票据池从核心系统查询账户状态异常|";
			}
			
			
			
		}

		resultMap.put("error", error);
		resultMap.put("msg", msg);
		
		logger.info("在线流贷业务票据池系统内基础校验完成！");
		
		return resultMap;
	}

	/**
	 * @Title 在线流贷收款人校验
	 * @author wss
	 * @date 2021-5-16
	 * @return Map
	 * @throws Exception 
	 */
	private Map crdtPayeeCheck(OnlineQueryBean queryBean,PlCrdtPayPlan detail, String msg, String error,Map map,Map payyMap) throws Exception {
		Map resultMap = new HashMap();
		//收款人信息准确
		queryBean.setPayeeAcctName(detail.getDeduAcctName());
		queryBean.setPayeeAcctNo(detail.getDeduAcctNo());
		queryBean.setPayeeOpenBankName(detail.getDeduBankName());
		queryBean.setPayeeOpenBankNo(detail.getDeduBankCode());
		queryBean.setOnlineCrdtNo(queryBean.getOnlineCrdtNo());
		
		List payees = null;
		
		//如果输入中有一项是空值，则认为收款人信息不存在
		if(PublicStaticDefineTab.ZI_ZHU_ZHI_FU.equals(detail.getDeduAcctName())){//自主支付
			if(StringUtil.isNotBlank(queryBean.getOnlineCrdtNo())){			
				payees = this.queryOnlineCrdtPayeeList(queryBean, null);
			}
		}else{			
			if(StringUtil.isNotBlank(detail.getDeduAcctNo())
					&&StringUtil.isNotBlank(detail.getDeduBankName())&&StringUtil.isNotBlank(detail.getDeduBankCode())&&StringUtil.isNotBlank(queryBean.getOnlineCrdtNo())){			
				payees = this.queryOnlineCrdtPayeeList(queryBean, null);
			}
		}
		
		if(null == payees || payees.size()==0){
			//收款人信息准确
			error = error+"第"+detail.getSerialNo()+"行收款人信息不存在^";
		}else{
			PedOnlineCrdtInfo payee = (PedOnlineCrdtInfo) payees.get(0);
			if(!PublicStaticDefineTab.STATUS_1.equals(payee.getPayeeStatus())){
				//收款人状态
				error = error+"第"+detail.getSerialNo()+"行收款人信息已失效^";
			}

			Iterator<Map.Entry<String, BigDecimal>> it=queryBean.getMap().entrySet().iterator();
			while(it.hasNext()){
				Entry<String, BigDecimal> entry=it.next();
				logger.info("校验收款人可用额度。。。。。。。。。。。收款人名称："+payee.getPayeeAcctName());
				if(payee.getPayeeAcctName().equals("自主支付")){
					if(entry.getKey().equals(payee.getPayeeAcctName())){
						logger.info("校验收款人金额："+entry.getValue());
						if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
							if(detail.getTotalAmt().compareTo(entry.getValue())>0){
								//收款人可用余额
				           		 error = error+"第"+detail.getSerialNo()+"行收款人可用余额不足^";
							}else{
								queryBean.getMap().put(entry.getKey(), entry.getValue().subtract(detail.getTotalAmt()));
							}
						}else{
							if(detail.getTotalAmt().compareTo(entry.getValue().add(detail.getTotalAmt()))>0){
								//收款人可用余额
				           		 error = error+"第"+detail.getSerialNo()+"行收款人可用余额不足^";
							}else{
								queryBean.getMap().put(entry.getKey(), entry.getValue().subtract(detail.getTotalAmt()));
							}
						}
					}
					
				}else{
					if(entry.getKey().equals(payee.getPayeeAcctNo())){
						if(PublicStaticDefineTab.OPERATION_TYPE_01.equals(queryBean.getOperationType())){
							if(detail.getTotalAmt().compareTo(entry.getValue())>0){
								//收款人可用余额
				           		 error = error+"第"+detail.getSerialNo()+"行收款人可用余额不足^";
							}else{
								queryBean.getMap().put(entry.getKey(), entry.getValue().subtract(detail.getTotalAmt()));
							}
						}else{
							if(detail.getTotalAmt().compareTo(entry.getValue().add(detail.getTotalAmt()))>0){
								//收款人可用余额
				           		 error = error+"第"+detail.getSerialNo()+"行收款人可用余额不足^";
							}else{
								queryBean.getMap().put(entry.getKey(), entry.getValue().subtract(detail.getTotalAmt()));
							}
						}

						
					}
				}
				
			}
			
	        //收款人重复
	        PlCrdtPayPlan old = (PlCrdtPayPlan) map.get(detail.getDeduAcctName()+detail.getDeduAcctNo()+detail.getDeduBankName()+detail.getDeduBankCode());
	        if(null != old){
	        	if(old.getDeduAcctName().equals(detail.getDeduAcctName()) && old.getDeduAcctNo().equals(detail.getDeduAcctNo()) && old.getDeduBankCode().equals(detail.getDeduBankCode()) && old.getDeduBankName().equals(detail.getDeduBankName())){
	        		error = error+"第"+detail.getSerialNo()+"行收款人信息已失效^";
	        	}
	        }
	        //收款人重复
	        PlCrdtPayPlan temp = (PlCrdtPayPlan) payyMap.get(detail.getDeduAcctName()+detail.getDeduAcctNo()+detail.getDeduBankName()+detail.getDeduBankCode());
	        if(null != temp){
        		error = error+"第"+detail.getSerialNo()+"行收款人信息重复^";
	        }else{
	        	payyMap.put(detail.getDeduAcctName()+detail.getDeduAcctNo()+detail.getDeduBankName()+detail.getDeduBankCode(), detail);
	        }
	        
		}
		resultMap.put("error", error);
		resultMap.put("msg", msg);
		return resultMap;
	}
	/**
	 * 受托支付保存
	 * @throws Exception 
	 */
	public OnlineQueryBean createOnlineCrdt(OnlineQueryBean queryBean) throws Exception{
		
		logger.info("在线流贷主业务合同及支付计划数据组装生成开始...");
		
		OnlineQueryBean returnBean = queryBean;
		
		PedOnlineCrdtProtocol protocol = this.queryOnlineProtocolByCrdtNo(queryBean.getOnlineCrdtNo());
		PedProtocolDto pool = (PedProtocolDto) this.dao.load(PedProtocolDto.class, protocol.getBpsId());
		List<PlOnlineCrdt> list = new ArrayList<PlOnlineCrdt>();
		List<PlCrdtPayPlan> payList = new ArrayList<PlCrdtPayPlan>();//支付计划列表
		List<PedCreditDetail> crdtDetailList = new ArrayList<PedCreditDetail>();
		PlOnlineCrdt self = null;
		PlOnlineCrdt entrust = null;
		//序列号生成
		CustomerRegister cust = pedProtocolService.queryCustomerRegisterByCustNo(protocol.getCustNumber());
		
		if(queryBean.isSelfPay()){
			String serialNo = pedProtocolService.txCreateOnlineProductNo(cust.getId(), 5);			
			String contractNo =protocol.getOnlineCrdtNo()+ serialNo;	
			self = new PlOnlineCrdt();
			BeanUtils.copyProperties(self, queryBean);
			self.setStatus(PublicStaticDefineTab.CRDT_BATCH_001);
			self.setDealStatus(PublicStaticDefineTab.ONLINE_DS_001);
			self.setContractNo(contractNo);
			String batchNo = Long.toString(System.currentTimeMillis());
			self.setBatchNo("LD"+batchNo);
			self.setBpsNo(protocol.getBpsNo());
			self.setBaseRateType(protocol.getBaseRateType());
			self.setApplyDate(new Date());
			self.setCreateTime(new Date());
			self.setUpdateTime(new Date());
			self.setBranchNo(protocol.getInAcctBranchNo());
			self.setCreditAcct(protocol.getLoanAcctNo());
			self.setPoolCreditRatio(protocol.getPoolCreditRatio());
			self.setCustName(protocol.getCustName());
			self.setCustNo(protocol.getCustNumber());
			self.setDeduAcctNo(protocol.getDeduAcctNo());
			self.setOverRateFloatType(protocol.getOverRateFloatType());
			self.setOverRateFloatValue(protocol.getOverRateFloatValue());
			self.setRateFloatType(protocol.getRateFloatType());
			self.setRateFloatValue(protocol.getRateFloatValue());
			self.setPayType(PublicStaticDefineTab.PAY_1);
			self.setLoanAmt(queryBean.getLoanAmt());
			self.setDepositAcctNo(pool.getMarginAccount());
			String loanNo = poolBatchNoUtils.txGetIOUNo("LDOL", 8);
			self.setLoanNo(loanNo);
			self.setCurrency(PublicStaticDefineTab.CURRENCY_TYPE_01);
		}
		if(queryBean.isEntrustedPay()){
			String serialNo = pedProtocolService.txCreateOnlineProductNo(cust.getId(), 5);			
			String contractNo =protocol.getOnlineCrdtNo()+ serialNo;	
			entrust = new PlOnlineCrdt();
			BeanUtils.copyProperties(entrust, queryBean);
			entrust.setStatus(PublicStaticDefineTab.CRDT_BATCH_001);
			entrust.setDealStatus(PublicStaticDefineTab.ONLINE_DS_001);
			entrust.setContractNo(contractNo);
			String batchNo = Long.toString(System.currentTimeMillis());
			entrust.setBatchNo("LD"+batchNo);
			entrust.setBpsNo(protocol.getBpsNo());
			entrust.setBaseRateType(protocol.getBaseRateType());
			entrust.setApplyDate(new Date());
			entrust.setCreateTime(new Date());
			entrust.setUpdateTime(new Date());
			entrust.setBranchNo(protocol.getInAcctBranchNo());
			entrust.setCreditAcct(protocol.getLoanAcctNo());
			entrust.setPoolCreditRatio(protocol.getPoolCreditRatio());
			entrust.setCustName(protocol.getCustName());
			entrust.setCustNo(protocol.getCustNumber());
			entrust.setDeduAcctNo(protocol.getDeduAcctNo());
			entrust.setOverRateFloatValue(protocol.getOverRateFloatValue());
			entrust.setOverRateFloatType(protocol.getOverRateFloatType());
			entrust.setRateFloatType(protocol.getRateFloatType());
			entrust.setRateFloatValue(protocol.getRateFloatValue());
			entrust.setPayType(PublicStaticDefineTab.PAY_2);
			entrust.setLoanAmt(queryBean.getLoanAmt());
			entrust.setDepositAcctNo(pool.getMarginAccount());
			String loanNo = poolBatchNoUtils.txGetIOUNo("LDOL", 8);
			entrust.setLoanNo(loanNo);
			entrust.setCurrency(PublicStaticDefineTab.CURRENCY_TYPE_01);
		}
		
		//创建主业务合同表对象
		CreditProduct product = this.creatProductByPlOnlineCrdt(protocol, pool);
		
		BigDecimal total1 = new BigDecimal(0);
		BigDecimal total2 = new BigDecimal(0);
		List details = queryBean.getDetalis();
		if(null != details && details.size()>0){
			for(int i=0;i<details.size();i++){
				PlCrdtPayPlan plan = (PlCrdtPayPlan) details.get(i);
				queryBean.setPayeeAcctName(plan.getDeduAcctName());
				queryBean.setPayeeAcctNo(plan.getDeduAcctNo());
				List payees = this.queryOnlineCrdtPayeeList(queryBean, null);
				if(PublicStaticDefineTab.ZI_ZHU_ZHI_FU.equals(plan.getDeduAcctName())){
					total1 = total1.add(plan.getTotalAmt());
					plan.setCrdtId(self.getId());
					plan.setContractNo(self.getContractNo());
					plan.setLoanNo(self.getLoanNo());
					plan.setOnlineCrdtNo(self.getOnlineCrdtNo());
				}else{
					total2 = total2.add(plan.getTotalAmt());
					plan.setCrdtId(entrust.getId());
					plan.setContractNo(entrust.getContractNo());
					plan.setLoanNo(entrust.getLoanNo());
					plan.setOnlineCrdtNo(entrust.getOnlineCrdtNo());
				}
				plan.setStatus(PublicStaticDefineTab.PAY_PLAN_01);
				plan.setBpsNo(protocol.getBpsNo());
				plan.setLoanAcctName(protocol.getCustName());
				plan.setLoanAcctNo(protocol.getLoanAcctNo());
				plan.setUpdateTime(new Date());
				plan.setOperaStatus(PoolComm.DS_00);
				plan.setIsLocal("0");
				plan.setOperaBatch("123456789");
				plan.setOperationType("0");
				plan.setRepayAmt(BigDecimal.ZERO);//取消支付金额，初始为0
				plan.setUsedAmt(BigDecimal.ZERO);//已支付金额，初始为0
				if(null != payees && payees.size()>0){
					PedOnlineCrdtInfo info = (PedOnlineCrdtInfo) payees.get(0);
					plan.setIsLocal(info.getIsLocal());
				}
				if(!PublicStaticDefineTab.ZI_ZHU_ZHI_FU.equals(plan.getDeduAcctName())){					
					payList.add(plan);
				}

			}
		}
		if(null != self){
			self.setLoanAmt(total1);
			list.add(self);

			PedCreditDetail crdtDetail = this.creatCrdtDetailByPlOnlineCrdt(self, pool);
			crdtDetailList.add(crdtDetail);
		}
		if(null != entrust){
			entrust.setLoanAmt(total2);
			list.add(entrust);
			
			PedCreditDetail crdtDetail = this.creatCrdtDetailByPlOnlineCrdt(entrust, pool);
			crdtDetailList.add(crdtDetail);
		}
		
		
		
		
		returnBean.setCrdtBatchList(list);//在线流贷批次对象列表
		returnBean.setPayList(payList);//在线流贷支付计划列表
		returnBean.setCrdtDetailList(crdtDetailList);//借据列表
		returnBean.setProduct(product);//返回主业务合同对象
	    returnBean.setCrdtPro(protocol);
	    returnBean.setPool(pool);
		logger.info("在线流贷主业务合同及支付计划数据组装生成完成！");
		return returnBean;
	}

	/**
	 * 信贷系统风险探测、信贷系统（额度系统）额度占用
	 */
	public ReturnMessageNew txPJE021(PlOnlineCrdt batch, String productType,String operationType) throws Exception {
		CreditTransNotes note = new CreditTransNotes();
		PedOnlineCrdtProtocol protocol =  this.queryOnlineProtocolByCrdtNo(batch.getOnlineCrdtNo());
//		note.setOnlineNo("YCOL20210706001");
		note.setOnlineNo(batch.getOnlineCrdtNo());
		note.setoNlineCreditNo(protocol.getBaseCreditNo());
		note.setCustomerId(protocol.getCustNumber());
		note.setOperationType(operationType);
		note.setOnlineType(productType);
		note.setContractNo(batch.getContractNo());
		note.setEFFECTIVE_DATE(DateUtils.toDateString(DateUtils.getCurrDate()));
		note.setEXPIRY_DATE(DateUtils.toDateString(batch.getDueDate()));
		note.setBusinessSum(batch.getLoanAmt());
		ReturnMessageNew response = poolCreditClientService.txPJE021(note);
		return response;
	}

	public List<PlCrdtPayPlan> queryPlCrdtPayPlanByBatchId(String id) {
		String sql ="select dto from PlCrdtPayPlan dto where dto.crdtId='"+id+"'";
		List result = this.find(sql);
		if(null != result && result.size()>0){
			
			return result;
		}
		
		return null;
	}

	@Override
	public List queryOnlineContractList(OnlineQueryBean queryBean, Page page) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("select '0' as type,batch.ONLINE_ACPT_NO as onlineNo,batch.CONTRACT_NO as contractNo,pro.ONLINE_ACPT_TOTAL as TOTAL,pro.USED_AMT as USED_AMT,batch.CREATE_TIME as crdtDt,batch.DEAL_STATUS as dealStatus,batch.TOTAL_AMT as amt " +
				" from PL_ONLINE_ACPT_BATCH batch,PED_ONLINE_ACPT_PROTOCOL pro where batch.ONLINE_ACPT_NO=pro.ONLINE_ACPT_NO ");

		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.CUST_NUMBER = '"+queryBean.getCustNumber()+"' ");
		}

		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and pro.EBK_CUST_NO like '%"+queryBean.getEbkCustNo()+"%' ");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and batch.CREATE_TIME >= to_date('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd")+ "','yyyy-MM-dd') ");
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and batch.CREATE_TIME <= "+DateUtils.getCurrentDayEndDate(queryBean.getEndDate())+ " ");
		}

		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and batch.CONTRACT_NO = '"+queryBean.getContractNo()+"' ");
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			hql.append(" and pro.ONLINE_ACPT_NO = '"+queryBean.getOnlineNo()+"' ");
		}

		hql.append(" union ");
		hql.append("select '1' as type,batch.ONLINE_CRDT_NO as onlineNo,batch.CONTRACT_NO as contractNo,pro.ONLINE_LOAN_TOTAL as TOTAL,pro.USED_AMT as USED_AMT ,batch.CREATE_TIME as crdtDt,batch.DEAL_STATUS as dealStatus,batch.LOAN_AMT as amt  " +
				"from PL_ONLINE_CRDT batch,PED_ONLINE_CRDT_PROTOCOL pro where  batch.ONLINE_CRDT_NO=pro.ONLINE_CRDT_NO ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.CUST_NUMBER = '"+queryBean.getCustNumber()+"' ");
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			hql.append(" and pro.ONLINE_CRDT_NO = '"+queryBean.getOnlineNo()+"' ");
		}
		if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
			hql.append(" and pro.EBK_CUST_NO like '%"+queryBean.getEbkCustNo()+"%' ");
		}
		if(null !=queryBean.getStartDate()){
			hql.append(" and batch.CREATE_TIME >= to_date('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd")+ "','yyyy-MM-dd') ");
		}
		if(null !=queryBean.getEndDate()){
			hql.append(" and batch.CREATE_TIME <= "+DateUtils.getCurrentDayEndDate(queryBean.getEndDate())+ " ");
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and batch.CONTRACT_NO = '"+queryBean.getContractNo()+"' ");
		}
		List list = dao.SQLQuery(hql.toString(),page);

		if(list != null && list.size() > 0){
			List result = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				OnlineQueryBean bean = new OnlineQueryBean();
				Object[] obj = (Object[]) list.get(i);
				bean.setOnlineProtocolType(String.valueOf(obj[0]));//在线银承
				bean.setOnlineAcptNo((String) obj[1]);     //在线业务协议编号
				bean.setContractNo((String) obj[2]);      //在线业务合同号
				bean.setUnusedAmt(((BigDecimal) obj[7]));//业务余额
				bean.setChangeDate((Date) obj[5]);//操作时间
				bean.setStatus((String) obj[6]);//状态
				result.add(bean);
			}
			return result;
		}
		return null;
	}

	/**
	 * 核心放款申请
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyMakeLoans(PlOnlineCrdt batch) throws Exception {
		PedOnlineCrdtProtocol pro = this.queryOnlineProtocolByCrdtNo(batch.getOnlineCrdtNo());
		CoreTransNotes note = new CoreTransNotes();
		note.setDeduAcctNo(batch.getDeduAcctNo());//扣款
		note.setCreditAcct(batch.getCreditAcct());//放款账户
		note.setDueDate(batch.getDueDate());//结束日期
		note.setRateFloatType(pro.getRateFloatType());//正常浮动方式
		note.setRateFloatValue(BigDecimalUtils.setScale(6, pro.getRateFloatValue()));//正常浮动值
		note.setOverRateFloatType(pro.getOverRateFloatType());//逾期利率浮动方式
		note.setOverRateFloatValue(BigDecimalUtils.setScale(6, pro.getOverRateFloatValue()));//逾期利率浮动值
		note.setLoanNo(batch.getLoanNo());//借据号
		note.setBranchNo(batch.getBranchNo());//管理机构
		note.setCustNo(batch.getCustNo());//客户号
		note.setLoanAmt(batch.getLoanAmt());
		note.setPayMode(batch.getPayType());//支付类型
		note.setContractNo(batch.getContractNo());//主业务合同号
		String str = poolBatchNoUtils.txGetFlowNo();
		note.setDevSeqNo(str);//设备流水号
		batch.setDevSeqNo(str);
		batch.setAcctDate(new Date());
		note.setTranDate(DateUtils.toString(batch.getAcctDate(), "yyyyMMdd"));
		note.setDepositAcctNo(batch.getDepositAcctNo());
		this.txStore(batch);
		ReturnMessageNew response = null;
		response = poolCoreService.CORE001Handler(note);
		if(response.isTxSuccess()){
			if(null!=response.getBody().get("ROPE_NO")){				
				batch.setFundNo((String)response.getBody().get("ROPE_NO"));//圈存编号
			}
			batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004);//变更状态
			batch.setAcctDate(new Date());//核心记账日期
			batch.setAcctFlow((String)response.getBody().get("SUCCESS_SEQ_NO"));//记账流水
			if(null!=response.getBody().get("CREDIT_ACCT_NO")){				
				batch.setTransAccount((String)response.getBody().get("CREDIT_ACCT_NO"));
			}
			this.txStore(batch);	
		}else{
			response.setTxSuccess(false);
			logger.error("核心出账失败");
			
		}
		
		return response;
	}

	/**
	 * 查询LPR利率
	 */
	public BigDecimal queryRatefromLPR() throws Exception {
		ReturnMessageNew response = poolLprService.txhandlerLPS001("LPS001");
        String responseCode = response.getRet().getRET_CODE();
		logger.info("LPS001利率查询结果："+responseCode);
		BigDecimal rate= null;
		if(Constants.TX_SUCCESS_CODE.equals(responseCode)){
			List details = response.getDetails();
			if(null != details && details.size()>0){
				for(int i=0;i<details.size();i++){
					Map map = (Map) details.get(i);
					String rateTye = (String) map.get("INT_RATE_ARRAY.INT_RATE_TYPE");
					String term = (String) map.get("INT_RATE_ARRAY.INT_RATE_TERM");
					if("Y".equals(rateTye) && "1Y".equals(term)){
						if(StringUtils.isNotBlank((String) map.get("INT_RATE_ARRAY.INT_RATE"))){
							String rateStr = (String) map.get("INT_RATE_ARRAY.INT_RATE");
							if(rateStr.contains(".")){
								String rateArr[] = rateStr.split("\\.");
								if(rateArr[1].length()>6){
									rateStr = rateArr[0] +"."+ rateArr[1].substring(0, 6);
								}
							}
							rate = new BigDecimal(rateStr);
						}
						logger.info("LPS001利率查询结果："+rate);
					}
				}
			}
		}else{
			throw new Exception("利率查询有误!");
		}
		return rate;
	}

	/**
	 * 核心贷款记账查询
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyQueryAcct(PlOnlineCrdt batch) throws Exception {
		CoreTransNotes note = new CoreTransNotes();
		note.setSerSeqNo(batch.getDevSeqNo());//流水号
		ReturnMessageNew response = poolCoreService.CORE002Handler(note);
		return response;
	}

	/**
	 * 解圈
	 * @throws Exception 
	 */
	public void txApplyFreeTransfer(PlCrdtPayPlan plan,BigDecimal tranAmt) throws Exception {
		CoreTransNotes note = new CoreTransNotes();
		PlOnlineCrdt dto = (PlOnlineCrdt) this.load(plan.getCrdtId());
		note.setTranAmt(tranAmt.toString());//解圈金额
		note.setFundNo(dto.getFundNo());//圈存编号
		note.setBrcNo(dto.getBranchNo());
		ReturnMessageNew response = poolCoreService.CORE008Handler(note);
		if(!response.isTxSuccess()){
			throw new Exception("批次号"+dto.getBatchNo()+"序列号"+plan.getSerialNo()+"解圈失败"+response.getRet().getRET_MSG());
		}
	}
	
	/**
	 * @author wss
	 * @date 2021-5-21
	 * @Description 圈存
	 */
	public void txApplyMakeTransfer(PlCrdtPayPlan plan,BigDecimal tranAmt) throws Exception {
		CoreTransNotes note = new CoreTransNotes();
		PlOnlineCrdt dto = (PlOnlineCrdt) this.load(plan.getCrdtId());
		note.setTranAmt(tranAmt.toString());//解圈金额
		note.setFundNo(dto.getFundNo());//圈存编号
		note.setBrcBld(dto.getBranchNo());//机构上送 入账机构
		ReturnMessageNew response = poolCoreService.CORE007Handler(note);
		if(!response.isTxSuccess()){
			response.getBody().get("ROPE_NO");//圈存编号
			throw new Exception("批次号"+dto.getBatchNo()+"序列号"+plan.getSerialNo()+"解圈失败"+response.getRet().getRET_MSG());
		}
	}

	@Override
	public PlOnlineCrdt queryonlineCrdtByContractNo(String contractNo) {
		String sql ="select dto from PlOnlineCrdt dto where dto.contractNo= '"+contractNo+"' ";
		List result = this.find(sql);
		if(null != result && result.size()>0){
			return (PlOnlineCrdt) result.get(0);
		}else{
			return null;
		}
	}

	@Override
	public void txSavePlCrdtPayPlanHist(PlCrdtPayPlan info,String operatorType) throws Exception {
		PlCrdtPayPlanHist hist = new PlCrdtPayPlanHist();
		BeanUtils.copyProperties(hist, info);
		hist.setPlanId(info.getId());
		hist.setCreateDate(new Date());
		this.txSaveEntity(hist);
	}

	public List<PlCrdtPayPlanHist> queryonlinePlCrdtPayPlanHist(OnlineQueryBean queryBean, Page page) {
		String sql ="select dto from PlCrdtPayPlanHist dto where 1=1 ";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getContractNo())){
				sql= sql+" and dto.contractNo=?";
				param.add(queryBean.getContractNo());
			}
			if(StringUtils.isNotBlank(queryBean.getSerialNo())){
				sql= sql+" and dto.serialNo=?";
				param.add(queryBean.getSerialNo());
			}
			if(StringUtils.isNotBlank(queryBean.getLoanNo())){
				sql= sql+" and dto.loanNo=?";
				param.add(queryBean.getLoanNo());
			}
		}
		sql = sql+" order by dto.createDate desc";
		List result = new ArrayList();
		if(null != page){
			result = this.find(sql, param, page);
		}else{
			result = this.find(sql, param);
		}
		return result;
	}

	/**
	 * 核心还款
	 * @throws Exception 
	 */
	public ReturnMessageNew txApplyRepayment(String fundNo,String loanNo, BigDecimal payAmt,String payType, String accNo,String DeduNo,String branchNo) throws Exception {
		CoreTransNotes note = new CoreTransNotes();
		ReturnMessageNew response = new ReturnMessageNew();
		
		if("1".equals(payType)){//提前还款
			
			logger.info("借据【"+loanNo+"】提前还款处理...");
			
			note.setTranAmt(payAmt.toString());//解圈金额
			note.setFundNo(fundNo);//圈存编号
			note.setCreditAcct(accNo);
			note.setDeduAcctNo(DeduNo);
			note.setBranchNo(branchNo);
			note.setLoanNo(loanNo);
			response = poolCoreService.CORE003Handler(note);

		}else{//贷款归还
			
			logger.info("借据【"+loanNo+"】贷款归还处理...");
			
			note.setTranAmt(payAmt.toString());//解圈金额
			note.setFundNo(fundNo);//圈存编号
			note.setCreditAcct(accNo);
			note.setDeduAcctNo(DeduNo);
			note.setBranchNo(branchNo);
			note.setLoanNo(loanNo);
			response = poolCoreService.Core009Handler(note);

		}
		return response;
	}
	
	public List queryPlOnlineCrdtList(OnlineQueryBean queryBean,Page page) {
		String sql ="select distinct dto from PlOnlineCrdt dto,PedOnlineCrdtProtocol pro where dto.onlineCrdtNo = pro.onlineCrdtNo ";
		List param = new ArrayList();
		if(null!= queryBean){
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				sql= sql+" and dto.custNo=?";
				param.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				sql= sql+" and dto.onlineCrdtNo=?";
				param.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getContractNo())){
				sql= sql+" and dto.contractNo=?";
				param.add(queryBean.getContractNo());
			}
			if(null != queryBean.getStartDate()){
				sql= sql+" and dto.createTime <=?";
				param.add(queryBean.getStartDate());
			}
			if(null != queryBean.getEndDate()){
				sql= sql+" and dto.createTime >=?";
				param.add(queryBean.getEndDate());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and  dto.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(null != queryBean.getStatuList() && queryBean.getStatuList().size()>0){
				sql= sql+" and dto.status in(?)";
				param.add(queryBean.getStatuList());
			}else{
				if(StringUtils.isNotBlank(queryBean.getStatus())){
					sql= sql+" and dto.status =?";
					param.add(queryBean.getStatus());
				}
			}
		}
		sql = sql+" order by dto.createTime desc ";
		List result = null;
		if(null != page){
			result = this.find(sql, param,page);
		}else{
			result = this.find(sql, param);
		}
		return result;
	}
	
	public List queryCreditProductList(OnlineQueryBean queryBean) {
		String sql ="select dto from CreditProduct dto,PedOnlineCrdtProtocol pro,PlOnlineCrdt batch where dto.crdtNo = pro.contractNo and pro.onlineCrdtNo=batch.onlineCrdtNo ";
		List param = new ArrayList();
		if(null!= queryBean){
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				sql= sql+" and batch.custNo=?";
				param.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				sql= sql+" and batch.onlineCrdtNo=?";
				param.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getContractNo())){
				sql= sql+" and batch.contractNo=?";
				param.add(queryBean.getContractNo());
			}
			if(null != queryBean.getStartDate()){
				sql= sql+" and batch.createTime <=?";
				param.add(queryBean.getStartDate());
			}
			if(null != queryBean.getEndDate()){
				sql= sql+" and batch.createTime >=?";
				param.add(queryBean.getEndDate());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and dto.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
		}
		sql = sql+" order by dto.createTime desc ";
		List result = this.find(sql, param);
		return result;
	}

	/**
	 * 同步合同状态
	 * @throws Exception 
	 */
	public void txSyncContract(String onlineNo, String contractNo) throws Exception {
		OnlineQueryBean queryBean = new OnlineQueryBean();
		queryBean.setOnlineCrdtNo(onlineNo);
		queryBean.setContractNo(contractNo);
		List list = this.queryPlOnlineCrdtList(queryBean, null);
		if(null != list && list.size()>0){
			PlOnlineCrdt batch = (PlOnlineCrdt) list.get(0);
			boolean succ = redisQueueCache.getLock(batch.getId(), String.valueOf(new Date()), 10);
			if(succ){
				//状态是“处理中”
				if(PublicStaticDefineTab.ONLINE_DS_001.equals(batch.getDealStatus())){
					//通过企业网银或票据池系统手工点“同步合同状态”/“同步借据状态”按钮同步任务状态，票据池系统调查证交易驱动后续流程
					ReturnMessageNew response = this.txApplyQueryAcct(batch);
					if(response.isTxSuccess()){
						String status = (String) response.getBody().get("SUCC_FLAG");//成功标志
						if(!"1".equals(status)){
							//票据池将该明细、批次置为失败，并记录失败原因。先释放在线流贷协议流贷额度、票据池担保合同额度、票据池低风险额度、收款人额度（若有），然后实时通知MIS系统释放
							batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
							batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
							
							List<PlCrdtPayPlan> details = this.queryPlCrdtPayPlanByBatchId(batch.getId());
							for(PlCrdtPayPlan plan:details){
								plan.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
								this.txStore(plan);
							}
							//保存日志
							onlineManageService.txSaveTrdeLog(batch.getCustNo(),"",batch.getBatchNo(),batch.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, "核心", "核心放款失败", "待定", "在线流贷放款申请", "receive");
							//判断是否已存在，如果已存在并且执行异常
							AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, AutoTaskNoDefine.POOL_ONLINE_RELEASE, batch.getId());
							if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus())){
								//判断各种额度是否已释放，直接释放额度,变更处理状态
								List<String> releseIds = new ArrayList<String>();
								releseIds.add(batch.getId());
								/*
								 * 池额度释放
								 */
								financialService.txOnlineBusiReleseCredit(releseIds, batch.getBpsNo());
								/*
								 * 信贷额度释放
								 */
								ReturnMessageNew result = this.txPJE021(batch, "2",PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
								if(result.isTxSuccess()){

									//释放成功后结束任务：处理完成	
									autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
								}else{
									batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
								}
								this.dao.store(batch);
							}
						}else if("1".equals(status)){
							batch.setFundNo((String)response.getBody().get("REPO_NO"));//圈存编号
							batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004);//变更状态
							batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_003);
							batch.setAcctDate(DateUtils.StringToDate((String)response.getBody().get("SUCCESS_DATE"),DateUtils.ORA_DATES_FORMAT));//核心记账日期
							batch.setAcctFlow((String)response.getBody().get("SUCCESS_SEQ_NO"));//记账流水
							this.dao.store(batch);
							//区分受托支付和自主支付
							List<PlCrdtPayPlan> details = this.queryPlCrdtPayPlanByBatchId(batch.getId());
							for(PlCrdtPayPlan plan:details){
								plan.setStatus(PublicStaticDefineTab.PAY_PLAN_02);
								this.dao.store(plan);
							}
							//生成借据
							onlineCommonService.txSavePedCreditDetail(PublicStaticDefineTab.PRODUCT_002,(String)response.getBody().get("REPO_NO"),batch);
						}
					}
				}else if(PublicStaticDefineTab.ONLINE_DS_002.equals(batch.getDealStatus())){//如果对应的业务为失败：未释放额度，为失败
						//通知MIS系统释放对应金额
						//判断是否已存在，如果已存在并且执行异常
						AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, AutoTaskNoDefine.POOL_ONLINE_RELEASE, batch.getId());
						if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus())){
							//直接释放信贷额度,变更处理状态
							ReturnMessageNew result = this.txPJE021(batch, "2",PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
							if(result.isTxSuccess()){
								batch.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
								batch.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
								this.dao.store(batch);
								List<PlCrdtPayPlan> details = this.queryPlCrdtPayPlanByBatchId(batch.getId());
								for(PlCrdtPayPlan plan:details){
									plan.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
									this.txStore(plan);
								}
								//释放成功后结束任务：处理完成	
								autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
							}
						}
					}
			    }
			}
	}

	/**
	 * 同步借据信息
	 * @throws Exception 
	 */
	public void txSyncPedCreditDetail(String crdtNo,  String loanNo) throws Exception {
		OnlineQueryBean queryBean = new OnlineQueryBean();
		queryBean.setOnlineCrdtNo(crdtNo);
		queryBean.setLoanNo(loanNo);
		List<PlOnlineCrdt> list = this.queryPlOnlineCrdtList(queryBean, null);
		
		for(PlOnlineCrdt crdt:list){
			
			boolean succ = redisQueueCache.getLock(crdt.getId(), String.valueOf(new Date()), 10);
			if(succ){
				//状态是“处理中”,票据池系统调核心系统查证交易驱动后续流程 
				if(PublicStaticDefineTab.ONLINE_DS_001.equals(crdt.getDealStatus())){
					//TODO 通过企业网银或票据池系统手工点“同步合同状态”/“同步借据状态”按钮同步任务状态，票据池系统调查证交易驱动后续流程
					ReturnMessageNew response = this.txApplyQueryAcct(crdt);
					String status = (String) response.getBody().get("SUCC_FLAG");//成功标志
					if(!"1".equals(status)){ //记账成功
						//票据池将该明细、批次置为失败，并记录失败原因。先释放在线流贷协议流贷额度、票据池担保合同额度、票据池低风险额度、收款人额度（若有），然后实时通知MIS系统释放
						crdt.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
						this.txStore(crdt);
						List<PlCrdtPayPlan> details = this.queryPlCrdtPayPlanByBatchId(crdt.getId());
						for(PlCrdtPayPlan plan:details){
							plan.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
							this.txStore(plan);
						}
						//保存日志
						onlineManageService.txSaveTrdeLog(crdt.getCustNo(),"",crdt.getBatchNo(),crdt.getId(), "1", PublicStaticDefineTab.OPERATION_TYPE_02, "核心", "核心放款失败", "待定", "在线流贷放款申请", "receive");
						//判断是否已存在，如果已存在并且执行异常
						AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, AutoTaskNoDefine.POOL_ONLINE_RELEASE, crdt.getId());
						if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus())){
							//判断各种额度是否已释放，直接释放额度,变更处理状态
							List<String> releseIds = new ArrayList<String>();
							releseIds.add(crdt.getId());
							/*
							 * 池额度释放
							 */
							financialService.txOnlineBusiReleseCredit(releseIds, crdt.getBpsNo());
							/*
							 * 信贷额度释放
							 */
							ReturnMessageNew result = this.txPJE021(crdt, "2",PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
							if(result.isTxSuccess()){

								//释放成功后结束任务：处理完成	
								autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
							}else{
								crdt.setDealStatus(PublicStaticDefineTab.ONLINE_DS_002);
							}
							this.dao.store(crdt);
						}
					}
				}else
				//TODO 如果对应的业务为失败：未释放额度，为失败
				if(PublicStaticDefineTab.ONLINE_DS_002.equals(crdt.getDealStatus())){
					//调查证交易(查信贷)
					//通知MIS系统释放对应金额
					//判断是否已存在，如果已存在并且执行异常
					AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOL_ONLINE_RELEASE_NO, AutoTaskNoDefine.POOL_ONLINE_RELEASE, crdt.getId());
					if(null != autoTaskExe && "4".equals(autoTaskExe.getStatus())){
						//直接释放信贷额度,变更处理状态
						ReturnMessageNew result = this.txPJE021(crdt, "2",PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
						if(result.isTxSuccess()){
							crdt.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
							crdt.setDealStatus(PublicStaticDefineTab.ONLINE_DS_005);
							this.dao.store(crdt);
							List<PlCrdtPayPlan> details = this.queryPlCrdtPayPlanByBatchId(crdt.getId());
							for(PlCrdtPayPlan plan:details){
								plan.setStatus(PublicStaticDefineTab.CRDT_BATCH_004_1);
								this.txStore(plan);
							}
							//释放成功后结束任务：处理完成	
							autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
						}
					}
				}
			}
		}
	}

	/**
	 * 同步流贷批次业务状态
	 */
	public void txSyncPlOnlineCrdtStatus(PlOnlineCrdt crdt) {
		//支付计划明细
		List<PlCrdtPayPlan> list = this.queryPlCrdtPayPlanByBatchId(crdt.getId());
		boolean flag = true;
		for(PlCrdtPayPlan plan:list){
			//不是支付完成以及借据总额度与已使用额度不相等
			if(!PublicStaticDefineTab.PAY_PLAN_02.equals(plan.getStatus()) || (plan.getTotalAmt().compareTo(plan.getUsedAmt().add(plan.getRepayAmt()))!=0)){
				flag = false;
			}
		}
		if(flag){
			crdt.setStatus(PublicStaticDefineTab.CRDT_BATCH_005);
			this.txStore(crdt);
		}
	}

	/**
	 * 流贷合同查询
	 */
	public List queryOnlineCrdtContractList(OnlineQueryBean queryBean, Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select batch,pro from PlOnlineCrdt batch,PedOnlineCrdtProtocol pro where   batch.onlineCrdtNo=pro.onlineCrdtNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
			hql.append(" and pro.onlineCrdtNo =:onlineCrdtNo");
			paramName.add("onlineCrdtNo");
			paramValue.add(queryBean.getOnlineCrdtNo());
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
			hql.append(" and batch.createTime <=:endDate");
			paramName.add("endDate");
			paramValue.add(DateUtils.getCurrentDayEndDate(queryBean.getEndDate()));
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and batch.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues,page );
		if(result != null && result.size() > 0){
			List list = new ArrayList();
			for (int i = 0; i < result.size(); i++) {
				OnlineQueryBean bean = new OnlineQueryBean();
				Object[] obj = (Object[]) result.get(i);
				PlOnlineCrdt batch = (PlOnlineCrdt) obj[0];
				PedOnlineCrdtProtocol pro =(PedOnlineCrdtProtocol)obj[1];
				bean.setOnlineProtocolType(PublicStaticDefineTab.PRODUCT_LD);//在线银承
				bean.setOnlineAcptNo(batch.getOnlineCrdtNo());     //在线业务协议编号  
				bean.setContractNo(batch.getContractNo());      //在线业务合同号   
				bean.setUnusedAmt(batch.getLoanAmt());//业务余额
				bean.setChangeDate(batch.getCreateTime());//操作时间
				bean.setStatus(batch.getDealStatus());//状态
				list.add(bean);
			}
			return list;
		}
		return null;
		
	}

	@Override
	public List queryOnlineCrdtPtlHist(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct dto from PedOnlineCrdtProtocolHist dto,PedOnlineCrdtProtocol pro where dto.onlineCrdtNo = pro.onlineCrdtNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
			hql.append(" and pro.onlineCrdtNo =:onlineCrdtNo");
			paramName.add("onlineCrdtNo");
			paramValue.add(queryBean.getOnlineCrdtNo());
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
		if(StringUtils.isNotBlank(queryBean.getModeMark())){
			hql.append(" and dto.modeMark =:modeMark");
			paramName.add("modeMark");
			paramValue.add(queryBean.getModeMark());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineCrdtProtocolHist> result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}

	@Override
	public List queryOnlinePayeeHistList(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineCrdtInfoHist dto,PedOnlineCrdtProtocol pro where dto.onlineCrdtNo = pro.onlineCrdtNo ");
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			hql.append(" and pro.custNumber =:custNo");
			paramName.add("custNo");
			paramValue.add(queryBean.getCustNumber());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			hql.append(" and pro.onlineCrdtNo =:onlineCrdtNo");
			paramName.add("onlineCrdtNo");
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
		if(StringUtils.isNotBlank(queryBean.getModeMark())){
			hql.append(" and dto.modeMark =:modeMark");
			paramName.add("modeMark");
			paramValue.add(queryBean.getModeMark());
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}

	@Override
	public List queryOnlinePedCreditDetailList(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedCreditDetail dto,PlOnlineCrdt batch,PedOnlineCrdtProtocol pro where dto.loanNo = pro.loanNo and batch.onlineCrdtNo=pro.onlineCrdtNo ");
		if(null != queryBean){
			if(null != queryBean.getStatuList() && queryBean.getStatuList().size()>0){
				hql.append(" and dto.loanStatus in(:loanStatus)");
				paramName.add("loanStatus");
				paramValue.add(queryBean.getStatuList());
			}else if(StringUtils.isNotBlank(queryBean.getStatus())){
				hql.append(" and dto.loanStatus =:loanStatus");
				paramName.add("loanStatus");
				paramValue.add(queryBean.getStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				hql.append(" and pro.custNumber =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				hql.append(" and batch.onlineCrdtNo =:onlineCrdtNo");
				paramName.add("onlineCrdtNo");
				paramValue.add(queryBean.getOnlineCrdtNo());
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
			if(StringUtils.isNotBlank(queryBean.getLoanNo())){
				hql.append(" and dto.loanNo=:loanNo");
				paramName.add("loanNo");
				paramValue.add(queryBean.getLoanNo());
			}
			if(null !=queryBean.getEndDate()){
				hql.append(" and dto.endTime=:endTime");
				paramName.add("endTime");
				paramValue.add(queryBean.getEndDate());
			}
		}
		hql.append(" order by dto.updateTime desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = this.find(hql.toString(), paramNames, paramValues );
		return result;
	}

	@Override
	public PlOnlineCrdt queryonlineCrdtByLoanNo(String loanNo) {
		String sql ="select dto from PlOnlineCrdt dto where dto.loanNo = "+loanNo;
		List result = this.find(sql);
		if(null != result && result.size()>0){
			return (PlOnlineCrdt) result.get(0);
		}else{
			return null;
		}
	}

	@Override
	public List queryCrdtPlanHistList(OnlineQueryBean queryBean, Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select list from PlCrdtPayList list,PlCrdtPayPlan dto where list.payPlanId = dto.id ");
		if(null != queryBean){
			if(null != queryBean.getStatuList() && queryBean.getStatuList().size()>0){
				hql.append(" and dto.status in(:status)");
				paramName.add("status");
				paramValue.add(queryBean.getStatuList());
			}else if(StringUtils.isNotBlank(queryBean.getStatus())){
				hql.append(" and dto.status =:status");
				paramName.add("status");
				paramValue.add(queryBean.getStatus());
			}
			if(StringUtils.isNotBlank(queryBean.getPayPlanId())){
				hql.append(" and list.payPlanId=:payPlanId");
				paramName.add("payPlanId");
				paramValue.add(queryBean.getPayPlanId());
			}
			if(null !=queryBean.getEndDate()){
				hql.append(" and dto.endTime=:endTime");
				paramName.add("endTime");
				paramValue.add(queryBean.getEndDate());
			}
		}
		hql.append(" order by list.createDate desc");
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List result = null;
		if(null != page){
			result = this.find(hql.toString(), paramNames, paramValues,page);
		}else{
			result = this.find(hql.toString(), paramNames, paramValues );
		}
		return result;
	}

	
	/**
	 * 支付计划还款 -- 提交审批
	 * 校验是否为同一借据下的支付计划
	 * 生成审批对象
	 * @throws Exception 
	 */
	public Ret txRepayOnlinePayPlanAudit(PlCrdtPayPlan plan, BigDecimal repayAmt,User user) throws Exception {
		
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("提交审批失败");
		
		//审批中的数据不允许提交
		if(plan.getOperaStatus().equals(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT) || plan.getOperaStatus().equals(PublicStaticDefineTab.AUDIT_STATUS_RUNNING)){
			ret.setRET_MSG("审批中数据不允许提交!");
			return ret;
		}
		
		// 主业务合同
		CreditProduct product = poolCreditProductService.queryProductByCreditNo(plan.getContractNo(),null);// 主业务合同 
		
		//查询借据
		PlOnlineCrdt crdt = (PlOnlineCrdt) this.load(plan.getCrdtId(),PlOnlineCrdt.class);
		
		//查询借据--在线流贷合同下只有一笔借据
		PedCreditDetail loan = null;
		CreditQueryBean queryLoanBean = new CreditQueryBean();
		queryLoanBean.setCrdtNo(product.getCrdtNo());
		List<PedCreditDetail> loanList =  poolCreditProductService.queryCreditDetailList(queryLoanBean); 
		if(null != loanList){
			loan = loanList.get(0);
		}
		

		//同步借据信息，并更新最新的额度信息
		loan = poolCreditProductService.txSynchroLoan(loan, product, PoolComm.XDCP_LD);
    	
		if(repayAmt.compareTo(plan.getWaitPayAmt())>0){
			ret.setRET_MSG("还借据的金额："+repayAmt+"不可大于该笔业务的剩余待支付金额："+plan.getWaitPayAmt());
			return ret;
		}
		if(repayAmt.compareTo(loan.getActualAmount())>0){//若释放金额大于当前借据实际金额，则不允许（这种情况出现在客户在柜面等渠道进行了还款后，没有修改支付计划，直接贷款归还的情况）
			ret.setRET_MSG("还借据的金额："+repayAmt+"不可大于该笔业务的实际占用金额："+loan.getActualAmount());
			return ret;
		}else{
			//生成支付计划 
			logger.info("在线流贷支付计划【贷款未用归还】审批支付计划生成...");
			PlCrdtPayList pay = new PlCrdtPayList();
			pay = this.toCreatPlCrdtPayListByPlan(plan, pay);
			pay.setTransChanel(PublicStaticDefineTab.CHANNEL_NO_BPS);//渠道-票据池
			pay.setOperatorType(PoolComm.PAY_TYPE_1);//支付类型-还款
			pay.setStatus(PoolComm.PAY_STATUS_00);//初始化
			pay.setSerialNo(plan.getSerialNo());//支付计划编号
			pay.setPayAmt(repayAmt);//释放金额
			String flowNo = "PAY-"+Long.toString(System.currentTimeMillis());//流水号，用来标记该批申请
			pay.setRepayFlowNo(flowNo);//流水号
			pay.setUsage("票据池-贷款未用归还");//用途
			pay.setPostscript("票据池-贷款未用归还");//附言
			pay = this.toCreatPlCrdtPayListByPlan(plan, pay);
			
			this.txStore(pay);
			
			plan.setDetailId(pay.getId());
			
			//生成审批对象
			
			ApproveAuditBean  approveAudit = new ApproveAuditBean();
			approveAudit.setAuditType(PublicStaticDefineTab.AUDIT_TYPE_COMMON);
			approveAudit.setBusiId(pay.getId()); //
			approveAudit.setProductId("3001001"); //产品码
			approveAudit.setCustName(crdt.getCustName()); //客户名称
			approveAudit.setAuditAmt(repayAmt); //总金额
			approveAudit.setBusiType("30010");
			
			approveAudit.setApplyNo(batchNoUtils.txGetBatchNo());
			Map<String,String> mvelDataMap = new HashMap<String,String>();
			mvelDataMap.put("amount", repayAmt.toString());
			AuditResultDto retAudit = auditService.txCommitApplyAudit(user, null, approveAudit, mvelDataMap);
			if(!retAudit.isIfSuccess()){
				//没有配置审批路线
				if("01".equals(retAudit.getRetCode())){
					ret.setRET_MSG("没有配置审批路线");
					return ret;
				}else if("02".equals(retAudit.getRetCode())){
					ret.setRET_MSG("审批金额过大 ，所有审批节点 都没有权限");
					return ret;
				}else{
					ret.setRET_MSG(retAudit.getRetMsg());
					return ret;
				}
			}
			
			//状态修改
			plan.setOperaStatus(PublicStaticDefineTab.AUDIT_STATUS_SUBMIT);// 提交审批
			plan.setOperationType("1");
			txStore(plan);
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("提交审批成功！");
			return ret;
			
		}
	
	}


	private PedOnlineCrdtInfo queryOnlinePayeeByAcctNo(String loanAcctNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PedOnlineCrdtProtocolHist compareDto(PedOnlineCrdtProtocolHist hist,
			PedOnlineCrdtProtocolHist last) {
		if(StringUtil.compareString(hist.getProtocolStatus(), last.getProtocolStatus())){
			last.setProtocolStatus(null);
		}
		if(StringUtil.compareString(hist.getOnlineCrdtNo(), last.getOnlineCrdtNo())){
			last.setOnlineCrdtNo(null);
		}
		if(StringUtil.compareString(hist.getBaseRateTypeDesc(), last.getBaseRateTypeDesc())){
			last.setBaseRateTypeDesc(null);
			last.setBaseRateType(null);
		}
		if(StringUtil.compareString(hist.getRateFloatTypeDesc(), last.getRateFloatTypeDesc())){
			last.setRateFloatTypeDesc(null);
		}
		if(StringUtil.compareString(hist.getRateFloatTypeDesc(), last.getRateFloatTypeDesc())){
			last.setRateFloatTypeDesc(null);
		}
		if(StringUtil.compareString(hist.getMakeLoanTypeDesc(), last.getMakeLoanTypeDesc())){
			last.setMakeLoanTypeDesc(null);
			last.setMakeLoanType(null);
		}
		if(StringUtil.compareString(hist.getRepaymentTypeDesc(), last.getRepaymentTypeDesc())){
			last.setRepaymentTypeDesc(null);
			last.setRepaymentType(null);
		}
		if(StringUtil.compareString(hist.getIsAutoDeductDesc(), last.getIsAutoDeductDesc())){
			last.setIsAutoDeductDesc(null);
			last.setIsAutoDeduct(null);
		}
		if(StringUtil.compareString(hist.getIsDiscInterestDesc(), last.getIsDiscInterestDesc())){
			last.setIsDiscInterestDesc(null);
			last.setIsDiscInterest(null);
		}
		logger.info("******************"+last.getIsDiscInterest()+last.getIsDiscInterestDesc()+"******************");
		logger.info("******************"+last.getIsAutoDeduct()+last.getIsAutoDeductDesc()+"******************");

		if(StringUtil.compareString(hist.getBaseCreditNo(), last.getBaseCreditNo())){
			last.setBaseCreditNo(null);
		}
		if(StringUtil.compareString(hist.getCustName(), last.getCustName())){
			last.setCustName(null);
		}
		if(StringUtil.compareString(hist.getEbkCustNo(), last.getEbkCustNo())){
			last.setEbkCustNo(null);
		}
		if(StringUtil.compareBigdecimal(hist.getPoolCreditRatio(), last.getPoolCreditRatio())){
			last.setPoolCreditRatio(null);
		}
		if(StringUtil.compareBigdecimal(hist.getOnlineLoanTotal(), last.getOnlineLoanTotal())){
			last.setOnlineLoanTotal(null);
		}
		if(StringUtil.compareString(hist.getRateFloatType(), last.getRateFloatType())){
			last.setRateFloatType(null);
		}
		if(StringUtil.compareBigdecimal(hist.getRateFloatValue(), last.getRateFloatValue())){
			last.setRateFloatValue(null);
		}
		if(StringUtil.compareString(hist.getOverRateFloatType(), last.getOverRateFloatType())){
			last.setOverRateFloatType(null);
		}
		if(StringUtil.compareBigdecimal(hist.getOverRateFloatValue(), last.getOverRateFloatValue())){
			last.setOverRateFloatValue(null);
		}
		if(StringUtil.compareString(hist.getLoanAcctNo(), last.getLoanAcctNo())){
			last.setLoanAcctNo(null);
		}
		if(StringUtil.compareString(hist.getLoanAcctName(), last.getLoanAcctName())){
			last.setLoanAcctName(null);
		}
		if(StringUtil.compareString(hist.getDeduAcctNo(), last.getDeduAcctNo())){
			last.setDeduAcctNo(null);
		}
		if(StringUtil.compareString(hist.getDeduAcctName(), last.getDeduAcctName())){
			last.setDeduAcctName(null);
		}
		if(StringUtil.compareString(hist.getInAcctBranchNo(), last.getInAcctBranchNo())){
			last.setInAcctBranchNo(null);
		}
		if(StringUtil.compareString(hist.getInAcctBranchName(), last.getInAcctBranchName())){
			last.setInAcctBranchName(null);
		}
		if(StringUtil.compareString(hist.getAppNo(), last.getAppNo())){
			last.setAppNo(null);
		}
		if(StringUtil.compareString(hist.getAppName(), last.getAppName())){
			last.setAppName(null);
		}
		if(StringUtil.compareString(hist.getSignBranchNo(), last.getSignBranchNo())){
			last.setSignBranchNo(null);
		}
		if(StringUtil.compareString(hist.getSignBranchName(), last.getSignBranchName())){
			last.setSignBranchName(null);
		}
		if(DateUtils.compareDate(hist.getChangeDate(), last.getChangeDate())==0){
			last.setChangeDate(null);
		}
		if(DateUtils.compareDate(hist.getDueDate(), last.getDueDate())==0){
			last.setDueDate(null);
		}
		if(StringUtil.compareString(hist.getContractNo(), last.getContractNo())){
			last.setContractNo(null);
		}
		if(StringUtil.compareString(hist.getGuarantorNo(), last.getGuarantorNo())){
			last.setGuarantorNo(null);
		}
		if(StringUtil.compareString(hist.getProtocolStatusDesc(), last.getProtocolStatusDesc())){
			last.setProtocolStatusDesc(null);
		}
		
		return last;
	}

	@Override
	public PedOnlineCrdtProtocol queryOnlineProtocol(OnlineQueryBean queryBean) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedOnlineCrdtProtocol as dto where 1=1 ");
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
		if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
			hql.append(" and dto.onlineCrdtNo =:onlineCrdtNo");
			paramName.add("onlineCrdtNo");
			paramValue.add(queryBean.getOnlineCrdtNo());
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			hql.append(" and dto.protocolStatus =:protocolStatus");
			paramName.add("protocolStatus");
			paramValue.add(queryBean.getProtocolStatus());
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
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			hql.append(" and dto.contractNo =:contractNo");
			paramName.add("contractNo");
			paramValue.add(queryBean.getContractNo());
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			hql.append(" and dto.appName like(:appName)");
			paramName.add("appName");
			paramValue.add("%"+queryBean.getAppName()+"%");
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			hql.append(" and dto.signBranchName like(:signBranchName)");
			paramName.add("signBranchName");
			paramValue.add("%"+queryBean.getSignBranchName()+"%");
		}
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedOnlineCrdtProtocol> result = this.find(hql.toString(), paramNames, paramValues );
		if(null != result && result.size()>0){
			return result.get(0);
		}else{
			return null;
		}
	}

	@Override
	public List queryOnlinePayPlanDetails(OnlineQueryBean queryBean,Page page) {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PlCrdtPayPlan as dto where 1=1 ");
		if(StringUtils.isNotBlank(queryBean.getId())){//支付批次id
			hql.append(" and dto.crdtId = :crdtId");
			paramName.add("crdtId");
			paramValue.add(queryBean.getId());
		}
		if(StringUtils.isNotBlank(queryBean.getAppNo())){//操作批次号
			hql.append(" and dto.operaBatch = :operaBatch");
			paramName.add("operaBatch");
			paramValue.add(queryBean.getAppNo());
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
			//协议编号
			hql.append(" and dto.onlineCrdtNo = :onlineCrdtNo");
			paramName.add("onlineCrdtNo");
			paramValue.add(queryBean.getOnlineCrdtNo());
		}
		if(StringUtils.isNotBlank(queryBean.getPayeeName())){
			//收款人信息
			hql.append(" and dto.deduAcctName = :deduAcctName");
			paramName.add("deduAcctName");
			paramValue.add(queryBean.getPayeeName());
		}
		if(queryBean.getStatuList() != null && queryBean.getStatuList().size() > 0){
			//状态类型
			hql.append(" and dto.status in (:status)");
			paramName.add("status");
			paramValue.add(queryBean.getStatuList());
		}
		
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PlCrdtPayPlan> result = this.find(hql.toString(), paramNames, paramValues,page);
		if(null != result && result.size()>0){
			return result;
		}else{
			return null;
		}
	}

	@Override
	public CreditProduct creatProductByPlOnlineCrdt(PedOnlineCrdtProtocol crdtBatch,PedProtocolDto pro) {
		
		CreditProduct product = new CreditProduct();
		product.setId(crdtBatch.getId());
		product.setCrdtNo(crdtBatch.getContractNo());//信贷业务号
		product.setCrdtType(PoolComm.XD_02);//信贷产品类型  XD_02流贷 
		product.setUseAmt(crdtBatch.getOnlineLoanTotal());//信贷产品合同金额(不含占用比例)
		product.setCrdtIssDt(DateUtils.getWorkDayDate());//借款日期
		product.setCrdtDueDt(crdtBatch.getDueDate());//到期日
		product.setCustNo(crdtBatch.getCustNumber());//核心客户号
		product.setCustName(pro.getCustname());//客户名称名称
		product.setSttlFlag(PoolComm.JQ_01);//结清标记  JQ_01：未结清
		product.setCrdtStatus(PoolComm.RZCP_YQS);//业务状态   RZ_03：额度占用成功   JQ_00 已结清   存储MIS系统发过来的状态：JQ_01 取消放贷  JQ_02 手工提前终止出账   JQ_03 合同到期    JQ_04 合同终止
		product.setReleaseAmt(new BigDecimal(0));//已还额度
		product.setCcupy(crdtBatch.getPoolCreditRatio().divide(new BigDecimal("100")).toString());//占用比例
		product.setRestAmt(crdtBatch.getOnlineLoanTotal());//业务余额:表示该融资业务剩余的待还金额
		product.setRisklevel(PoolComm.LOW_RISK);//风险等级	FX_01 高风险产品 
		product.setBpsNo(pro.getPoolAgreement());//票据池编号
		product.setIsOnline(PoolComm.YES);//是否线上 1 是 0 否
		product.setMinDueDate(crdtBatch.getDueDate());//借据最早到期日
		
		return product;
	}

	@Override
	public PedCreditDetail creatCrdtDetailByPlOnlineCrdt(PlOnlineCrdt crdtBatch, PedProtocolDto pro) {
		
		PedCreditDetail crdtDetail = new PedCreditDetail();
		crdtDetail.setCreditDetailId(Long.toString(System.currentTimeMillis()));
		crdtDetail.setBpsNo(pro.getPoolAgreement());
		crdtDetail.setCrdtNo(crdtBatch.getContractNo());//信贷产品号：信贷业务合同号
		crdtDetail.setCustNumber(crdtBatch.getCustNo());//客户号
		crdtDetail.setCustName(pro.getCustname());//客户名称
		crdtDetail.setLoanNo(crdtBatch.getLoanNo());//借据号--这里放合同号
		crdtDetail.setTransTime(new Date());//交易时间
		crdtDetail.setLoanType(PoolComm.XD_02);//交易类型   （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
		crdtDetail.setLoanStatus(PoolComm.JJ_01);//交易状态（JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清  JJ_05:未用退回（已撤销） ）
//	     crdtDetail.settransAccount;//交易账号 （表外业务对应业务保证金账号，表内业务对应贷款账号）
		crdtDetail.setLoanAmount(crdtBatch.getLoanAmt());//借据金额
		crdtDetail.setLoanBalance(crdtBatch.getLoanAmt());//借据余额=借据应还本金
//	     crdtDetail.setrepaymentTime;//还款时间
		crdtDetail.setPenaltyAmount(BigDecimal.ZERO);//罚息金额
		crdtDetail.setActualAmount(crdtBatch.getLoanAmt());//实际占用金额=借据余额
		crdtDetail.setBailAccAmt(BigDecimal.ZERO);//保证金余额,用于银承
		crdtDetail.setDetailStatus(PoolComm.LOAN_1);//借据状态,0-不在处理,1-还需处理
		crdtDetail.setStartTime(crdtBatch.getApplyDate());//借据起始日
		Date date = DateUtils.formatDate(crdtBatch.getDueDate(), DateUtils.ORA_DATE_FORMAT);
		logger.info(date);
		crdtDetail.setEndTime(crdtBatch.getDueDate());//借据到期日
		crdtDetail.setIfAdvanceAmt(PoolComm.NO); //是否垫款0： 否 1: 是'
		crdtDetail.setLoanAdvanceNo(null);//垫款借据号
		
		return crdtDetail;
	}

	@Override
	public List<PlCrdtPayPlan> queryPayPlanListByBatchContractNo(String contractNo) {
		String sql ="select pay from PlCrdtPayPlan pay where pay.contractNo='"+contractNo+"'";
		List result = this.find(sql);
		if(null != result && result.size()>0){
			
			return result;
		}
		
		return null;
	}

	@Override
	public void txPayPlanPayTask(PlCrdtPayList pay) throws Exception {
		
		logger.info("支付记录划转，在线协议编号【"+pay.getOnlineCrdtNo()+"】主业务合同号【"+pay.getContractNo()+"】支付计划编号【"+pay.getSerialNo()+"】");
		this.txStore(pay);
	    autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_ONLINE_PAY_NO, pay.getId(), AutoTaskNoDefine.BUSI_TYPE_ONLINE_PAY, null, pay.getContractNo(), pay.getBpsNo(), null, null);
		
	}
	
	@Override
	public void txCancelAuditPayPlan(String id, User user) throws Exception {
		//查询支付计划
		PlCrdtPayPlan plan = (PlCrdtPayPlan) this.load(id,PlCrdtPayPlan.class);
		PlCrdtPayList list = (PlCrdtPayList) this.load(plan.getDetailId(),PlCrdtPayList.class);
		if(!PublicStaticDefineTab.AUDIT_STATUS_SUBMIT.equals(plan.getOperaStatus())){
			throw new Exception("当前授信额度状态不允许撤销审批。");
		}
		auditService.txCommitCancelAudit("3001001", list.getId());
		
		//状态回滚
		plan.setOperaStatus(PublicStaticDefineTab.AUDIT_STATUS_UNPROCESSED);
		plan.setDetailId(null);
		this.txStore(plan);
		
	}

	@Override
	public PlCrdtPayList toCreatPlCrdtPayListByPlan(PlCrdtPayPlan plan,PlCrdtPayList pay) {
		
		pay.setPayPlanId(plan.getId());        //对应支付计划表ID                            
		pay.setLoanNo(plan.getLoanNo());           //借据编号                                 
		pay.setContractNo(plan.getContractNo());       //合同编号                                 
		pay.setOnlineCrdtNo(plan.getOnlineCrdtNo());      //在线流贷协议编号                             
		pay.setBpsNo(plan.getBpsNo());            //票据池编号                                
		pay.setLoanAcctNo(plan.getLoanAcctNo());       //付款账号                                 
		pay.setLoanAcctName(plan.getLoanAcctName());      //付款户名                                 
		pay.setDeduAcctNo(plan.getDeduAcctNo());       //收款人名称                                
		pay.setDeduAcctName(plan.getDeduAcctName());      //收款人账号                                
		pay.setDeduBankCode(plan.getDeduBankCode());      //收款人开户行行号         ·                    
		pay.setDeduBankName(plan.getDeduBankName());      //收款人开户行名称                             
		pay.setIsLocal(plan.getIsLocal());          //是否跨行 0-否 1-是                         
		pay.setCreateDate(new Date());
		pay.setUpdateTime(new Date());		
		
		return pay;
	}

	@Override
	public List<PlCrdtPayList> queryPlCrdtPayListByflowNo(String flowNo) {
		
		List result = this.find("select pl from PlCrdtPayList pl where pl.repayFlowNo ='"+flowNo+"'");
		
		if(null != result && result.size()>0){
			return result;
		}
		return null;
	}
	
	
	/**
	 * 在线流贷主业务合同及支付计划数据组装
	 */
	public OnlineQueryBean createOnlineCrdtApply(PlOnlineCrdt plOnlineCrdt) throws Exception{
		
		logger.info("在线流贷主业务合同及支付计划数据组装生成开始...");
		
		OnlineQueryBean returnBean = new OnlineQueryBean();
		
		returnBean.setOnlineCrdtNo(plOnlineCrdt.getOnlineCrdtNo());//在线流贷编号
		returnBean.setLoanAmt(plOnlineCrdt.getLoanAmt());//流贷总金额
		returnBean.setDueDate(plOnlineCrdt.getDueDate());//流贷到期日
		returnBean.setOperationType(PublicStaticDefineTab.OPERATION_TYPE_02);//操作类型
		returnBean.setOnlineLoanTotal(plOnlineCrdt.getLoanAmt());//流贷总金额

		PedOnlineCrdtProtocol protocol = this.queryOnlineProtocolByCrdtNo(plOnlineCrdt.getOnlineCrdtNo());
		PedProtocolDto pool = (PedProtocolDto) this.dao.load(PedProtocolDto.class, protocol.getBpsId());
		List<PlOnlineCrdt> list = new ArrayList<PlOnlineCrdt>();
		List<PlCrdtPayPlan> payList = new ArrayList<PlCrdtPayPlan>();//支付计划列表
		List<PedCreditDetail> crdtDetailList = new ArrayList<PedCreditDetail>();

		list.add(plOnlineCrdt);
		payList=this.queryPlCrdtPayPlanByBatchId(plOnlineCrdt.getId());
		
		//创建主业务合同表对象
		CreditProduct product = this.creatProductByPlOnlineCrdt(protocol, pool);
		
		PedCreditDetail crdtDetail = this.creatCrdtDetailByPlOnlineCrdt(plOnlineCrdt, pool);
		crdtDetailList.add(crdtDetail);

		returnBean.setCrdtBatchList(list);//在线流贷批次对象列表
		returnBean.setPayList(payList);//在线流贷支付计划列表
		returnBean.setCrdtDetailList(crdtDetailList);//借据列表
		returnBean.setProduct(product);//返回主业务合同对象

		//获取收款人剩余可用额
		OnlineQueryBean queryBean=new OnlineQueryBean();
		if(null!=payList && !payList.isEmpty()){
			for(int i=0;i<payList.size();i++){
				PlCrdtPayPlan	plan = (PlCrdtPayPlan)payList.get(i);
			    if(!queryBean.getMap().containsKey(plan.getDeduAcctName())){
				queryBean.setPayeeAcctName(plan.getDeduAcctName());
				queryBean.setPayeeAcctNo(plan.getDeduAcctNo());
				queryBean.setPayeeStatus(PublicStaticDefineTab.STATUS_1);
				queryBean.setOnlineNo(plOnlineCrdt.getOnlineCrdtNo());
				queryBean.setOnlineCrdtNo(plOnlineCrdt.getOnlineCrdtNo());//在线协议编号
			    List<OnlineQueryBean> payees = this.queryOnlineCrdtPayeeListByBean(queryBean, null);
				if(null != payees && payees.size()>0){
					returnBean.getMap().put(plan.getDeduAcctNo(), payees.get(0).getPayeeTotalAmt().subtract(payees.get(0).getPayeeUsedAmt()!=null?payees.get(0).getPayeeUsedAmt():new BigDecimal(0)));
				}
				}
			}
		}
		
		logger.info("在线流贷主业务合同及支付计划数据组装生成完成！");
		
		return returnBean;
	}
	
	/**
	 * 流贷复核校验
	 * @throws Exception 
	 */
	public ReturnMessageNew txCrdtApplyCheck(OnlineQueryBean queryBean) throws Exception {
		
		logger.info("在线流贷业务校验开始...");
		
		ReturnMessageNew response =  new ReturnMessageNew();
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        PedOnlineCrdtProtocol protocol = this.queryOnlineProtocolByCrdtNo(queryBean.getOnlineCrdtNo());
        PedProtocolDto pool = (PedProtocolDto) this.dao.load(PedProtocolDto.class, protocol.getBpsId());

		if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){
			PlOnlineCrdt onlineCrdt=(PlOnlineCrdt)queryBean.getCrdtBatchList().get(0);
			List details = queryBean.getPayList();
				String loanNo = "";
				Map map = new HashMap();
				String commError ="";//禁止
				String commMsg = "";//提示

				//担保合同限额校验
				BigDecimal creditamount = pool.getCreditamount();//最高额担保合同金额
				BigDecimal use = pool.getCreditUsedAmount().add(queryBean.getOnlineLoanTotal()) ;//票据池担保额度已用金额  + 本次在线业务合同占用总金额
				BigDecimal result = creditamount.subtract(use);//可用额度
				if(result.doubleValue()<0){//如果该客户    已用额度 + 本次需要占用额度 > 担保金额    则不允许占用
					commError+= "票据池担保合同余额不足|";
				}
				
				//票据池池额度校验
				commMsg += this.txPoolCreditCheck(queryBean, pool);
				
				
				//关联票据池校验
				Map comm = this.crdtApplyCheck(queryBean,protocol,pool);
				commError += (String) comm.get("error");//禁止
				commMsg += (String) comm.get("msg");//提示
				
				
				//LPR利率校验
				try {					
					BigDecimal rate = this.queryRatefromLPR();//基准利率
					if(null != rate){
						if(null !=onlineCrdt.getLprRate()){
							if(onlineCrdt.getLprRate().compareTo(rate)!=0){
								logger.info("基准利率变更，协议中的利率【"+onlineCrdt.getLprRate()+"】，最新查询回来的利率【"+rate+"】");
								commError+= "基准利率已变更|";
							}
						}else{
							logger.info("在线流贷协议中的LPR利率为null，需要排查！");
							commError+= "基准利率已变更|";
						}
					}else{
						logger.info("LPR利率查询未查询到正确的值，需要排查！");
						commError+= "基准利率已变更|";
					}
				} catch (Exception e) {
					logger.error("LPR利率查询异常：",e);
					commError+= "基准利率已变更|";
				}
     			
				
				//协议额度校验
				BigDecimal usedAmt = this.getOnlineCrdtPtlAmt(queryBean);
				if(protocol.getOnlineLoanTotal().subtract(usedAmt).compareTo(queryBean.getOnlineLoanTotal())<0){
					commError +="在线流贷合同余额小于客户申请金额|";
				}
				
				/**
				 * 需求漏加额度系统风险探测
				 */
				ReturnMessageNew misResult  = this.txPJE021(queryBean.getCrdtBatchList().get(0),PublicStaticDefineTab.PRODUCT_002,PublicStaticDefineTab.CREDIT_OPERATION_CHECK);
				if(!misResult.isTxSuccess()){
					commError += misResult.getRet().getRET_MSG()+"|";
				}
				
				logger.info("-----------------------"+queryBean.getMap().values());

//				if(onlineCrdt.getPayType().equals(PublicStaticDefineTab.PAY_2)){
					logger.info("-----------------------明细条数"+details.size()+"类型："+queryBean.getOperationType());
					if(null != details && details.size()>0){
						 Map payyMap = new HashMap();
						//明细项校验
						for(int i=0;i<details.size();i++){
							String error = "";//禁止
							String msg = "";//提示
							String pass = "";//通过
							PlCrdtPayPlan detail = (PlCrdtPayPlan) details.get(i);
							loanNo = detail.getLoanNo();
							//收款信息校验
							map = this.crdtPayeeCheck(queryBean,detail,msg,error,map,payyMap);
							
							//5.返回信息
							map.put("CRDT_SERIAL_NO", detail.getSerialNo());//序号
							msg = msg + (String)map.get("msg");
							error = error + (String)map.get("error");
							if(error.length()>0){
								commError = commError + detail.getSerialNo() + "|" + error + ";";
							}
							if(msg.length()>0){
								commMsg = commMsg + detail.getSerialNo() + "|" + msg + ";";
							}
							if(error.length()>0){//校验结果 0：不通过 1：通过
								ret.setRET_CODE(Constants.TX_FAIL_CODE);
								ret.setRET_MSG("票据池系统校验未通过！");
								map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);
							}else{
								map.put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);
								pass = "通过";//校验结果说明
							}
							map.put("CHECK_INFO", error+msg+pass);//校验结果说明
							map.remove("error");
							map.remove("msg");
							
							//返回文件明细内容
							response.getDetails().add(map);
							
							//6.明细校验日志保存
							if(error.length()>0 || msg.length()>0){
								PedOnlineHandleLog log = new PedOnlineHandleLog();
								log.setBusiName("在线流贷申请-明细日志");     //业务名称   
								log.setTradeName(PublicStaticDefineTab.CHANNEL_NO_EBK);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
								log.setTradeCode("PJC071");    //报文码  
								log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
								if(error.length() > 3000){
									log.setTradeResult(error.substring(0,2999));  //处理结果
								}else{
									log.setTradeResult(error);  //处理结果
								}
								log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
								log.setOperationType(queryBean.getOperationType());//岗位     经办、复核
								log.setBillNo(loanNo+"-"+detail.getSerialNo());       //票号、批次号--只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录
								log.setBusiId(detail.getId());       //业务id --只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录 
								log.setOnlineNo(protocol.getOnlineCrdtNo());     //在线协议编号
								log.setCustNumber(protocol.getCustNumber());   //客户号
								log.setBpsNo(protocol.getBpsNo());
								
								onlineManageService.txSaveTrdeLog(log);
							 }
						   }
						}else{
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("无支付计划明细信息！");
							response.setRet(ret);
							return response;
						}
//			     }
				
				//6.全部校验日志保存
				
				if(commError.length()>0 || commMsg.length()>0){
					PedOnlineHandleLog log = new PedOnlineHandleLog();
					log.setBusiName("在线流贷申请-汇总日志");     //业务名称   
					log.setTradeName(PublicStaticDefineTab.CHANNEL_NO_EBK);    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
					log.setTradeCode("PJC071");    //报文码  
					log.setSendType(PublicStaticDefineTab.SEND_RECEIVE_TYPE_01);//收发类型 receive、send
					String str = commError+commMsg;
					if(str.length() > 3000){
						log.setTradeResult(str.substring(0,2999));  //处理结果
					}else{
						log.setTradeResult(str);  //处理结果
					}
					
					log.setErrorType(PoolComm.LOG_TYPE_1);    //错误类型-禁止
					log.setOperationType(queryBean.getOperationType());//岗位     经办、复核
					log.setBillNo(loanNo);       //票号、批次号--只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录
					log.setBusiId(onlineCrdt.getId());       //业务id  --只能存临时生成的借据号，并且该借据号如果只是经办或者复核不通过，数据库中并无此记录
					log.setOnlineNo(protocol.getOnlineCrdtNo());     //在线协议编号
					log.setCustNumber(protocol.getCustNumber());   //客户号
					log.setBpsNo(protocol.getBpsNo());
					
					onlineManageService.txSaveTrdeLog(log);
				}
				
				response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_1);//通过
				
				if(commMsg.trim().length()>0){
					response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_2);//提示性错误
				}
				
				if(commError.trim().length()>0){
					response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);//禁止性错误
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
				}
				
				if(PublicStaticDefineTab.OPERATION_TYPE_02.equals(queryBean.getOperationType())){//复核--复核时候无提示性错误，均为禁止性错误
					
					if(commMsg.trim().length()>0||commError.trim().length()>0){
						response.getBody().put("CHECK_RESULT", PublicStaticDefineTab.CHECK_0);//禁止性错误
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
					}
				}
				
				
				response.getBody().put("ONLINE_CRDT_NO", queryBean.getOnlineCrdtNo());//在线流贷编号
				if(commError.trim().length()>0 || commMsg.trim().length()>0){
					response.getBody().put("CHECK_INFO", commError+commMsg);
					ret.setRET_MSG(commError+commMsg);
				}else{
					response.getBody().put("CHECK_INFO", "通过");
					ret.setRET_MSG("通过");
				}
		}
        response.setRet(ret);

        logger.info("在线流贷业务校验结束！");
        
		return response;
	}
	
	/**
	 * 提前还款/贷款归还成功处理
	 * @param pay
	 * @param plan
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-20下午9:19:08
	 */
	private void txRePaySucc(String flowNo,PedCreditDetail loan,CreditProduct product) throws Exception {
		
		logger.info("贷款归还 "+flowNo+" 处理成功后续落库及额度处理 ");
		
		List<PlCrdtPayList> payList = this.queryPlCrdtPayListByflowNo(flowNo);
		 
		 if(null != payList){
			 
			 for(PlCrdtPayList pay : payList){
				 
				 //支付明细
				 pay.setUpdateTime(new Date());
				 pay.setStatus(PoolComm.PAY_STATUS_01);//支付成功
				 pay.setOperatorType(PoolComm.PAY_TYPE_1);
				 pay.setPayDesc("贷款归还/提前还款处理成功");//支付结果说明
				 this.txStore(pay);
				 
				 
				//支付计划
				 OnlineQueryBean queryBean = new  OnlineQueryBean();
				 queryBean.setOnlineCrdtNo(pay.getOnlineCrdtNo());//在线流贷协议编号
				 queryBean.setContractNo(pay.getContractNo());//在线流贷主业务合同号
				 queryBean.setSerialNo(pay.getSerialNo());//支付计划编号
				 PlCrdtPayPlan plan = (PlCrdtPayPlan) this.queryPlCrdtPayPlanListByBean(queryBean, null).get(0);//有且只有一条
				 
				 BigDecimal newRepayAmt = plan.getRepayAmt().add(pay.getPayAmt()); 
				 plan.setRepayAmt(newRepayAmt);//变更支付计划中已支付金额
				 
				 BigDecimal allUsed = plan.getUsedAmt().add(plan.getRepayAmt());
			     if(allUsed.compareTo(plan.getTotalAmt())==0){
					plan.setStatus(PublicStaticDefineTab.PAY_PLAN_03);//支付完成
  				 }
				 this.txStore(plan);
				 
				 //支付计划历史
				 this.txSavePlCrdtPayPlanHist(plan,PoolComm.PAY_TYPE_1);//保存历史
			 }
		 }
		
		 if(DateUtils.formatDate(loan.getStartTime(),DateUtils.DATE_FORMAT).compareTo(DateUtils.formatDate(new Date(), DateUtils.DATE_FORMAT)) == 0){
			 //如果贷款当天做了贷款归还 需要去信贷变更占用金额
			 BigDecimal unUsedReturnAmt = this.queryUnUsedReturnAmtOfCrdt(loan.getCrdtNo(),loan.getStartTime());//贷款未用归还金额
			 	/**
				 * 发生还款   需去信贷变更额度占用
				 */
				//统计未用退回金额
				CreditTransNotes note = new CreditTransNotes();
				note.setContractNo(loan.getCrdtNo());//合同编号
				note.setExpendFailAmt(new BigDecimal(0));//出账失败金额
				note.setUnUsedReturnAmt(unUsedReturnAmt);//未用退回金额
				note.setOriContractAmt(loan.getLoanAmount());//原合同金额
				
				poolCreditClientService.txPJE028(note);
			 
		 }
		
		//同步借据信息，并更新最新的额度信息
		loan = poolCreditProductService.txSynchroLoan(loan, product, PoolComm.XDCP_LD);
	}
	
	
	/**
	 * 提前还款/贷款归还失败
	 * @param pay
	 * @param failMsg
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-20下午9:29:36
	 */
	private void txRepayFail(String flowNo,String failMsg) throws Exception{
		
		logger.info("贷款归还处理失败处理，失败流水号："+ flowNo + "失败原因："+failMsg);
		
		 List<PlCrdtPayList> payList = this.queryPlCrdtPayListByflowNo(flowNo);
		 
		 if(null != payList){
			 List<PlCrdtPayList> storePayList = new ArrayList<PlCrdtPayList>();
			 for(PlCrdtPayList pay : payList){				 
				 pay.setUpdateTime(new Date());
				 pay.setStatus(PoolComm.PAY_STATUS_02);//支付失败
				 failMsg = failMsg.length()>1000 ? failMsg.substring(0,1000) : failMsg;//避免超长
				 pay.setPayDesc(failMsg);//支付结果说明
				 storePayList.add(pay);
			 }
		 }
		
		 this.txStoreAll(payList);
		
	}
	
	/**
	 * 支付计划修改校验成功后的处理
	 * @param plan
	 * @param relsAmt
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-21下午8:32:23
	 */
	public void txPayPlanModSuccess(PlCrdtPayPlan plan,BigDecimal relsAmt)throws Exception{
		
		logger.info("主业务合同【"+plan.getContractNo()+"】借据【"+plan.getLoanNo()+"】支付计划【"+plan.getSerialNo()+"】修改成功...");
		plan.setRepayAmt(plan.getRepayAmt().add(relsAmt));
		
		BigDecimal allUsed = plan.getUsedAmt().add(plan.getRepayAmt());
		if(allUsed.compareTo(plan.getTotalAmt())==0){
			plan.setStatus(PublicStaticDefineTab.PAY_PLAN_03);//支付完成
		}
		
		//生成一条支付记录
		PlCrdtPayList cp = new PlCrdtPayList();
		cp.setSerialNo(plan.getSerialNo());//支付计划编号
		cp.setPayAmt(relsAmt);//本次支付金额
		cp.setUsage("网银/票据池-支付计划修改");//用途
		cp.setPostscript("网银/票据池-支付计划修改");//附言
		cp.setPayPlanId(plan.getId());        //对应支付计划表ID                            
		cp.setLoanNo(plan.getLoanNo());           //借据编号                                 
		cp.setContractNo(plan.getContractNo());       //合同编号                                 
		cp.setOnlineCrdtNo(plan.getOnlineCrdtNo());      //在线流贷协议编号                             
		cp.setBpsNo(plan.getBpsNo());            //票据池编号                                
		cp.setLoanAcctNo(plan.getLoanAcctNo());       //付款账号                                 
		cp.setLoanAcctName(plan.getLoanAcctName());      //付款户名                                 
		cp.setDeduAcctNo(plan.getDeduAcctNo());       //收款人名称                                
		cp.setDeduAcctName(plan.getDeduAcctName());      //收款人账号                                
		cp.setDeduBankCode(plan.getDeduBankCode());      //收款人开户行行号         ·                    
		cp.setDeduBankName(plan.getDeduBankName());      //收款人开户行名称                             
		cp.setIsLocal(plan.getIsLocal());          //是否跨行 0-否 1-是                         
		cp.setTransChanel(PublicStaticDefineTab.CHANNEL_NO_EBK);//渠道-网银
		cp.setOperatorType(PoolComm.PAY_TYPE_2);//支付类型-修改
		cp.setCreateDate(new Date());
		cp.setUpdateTime(new Date());
		cp.setStatus(PoolComm.PAY_STATUS_01);//支付成功--修改成功
		cp.setPayDesc("网银/票据池-支付计划修改成功");//支付结果说明
		
		this.txStore(cp);
		this.txStore(plan);
		this.txSavePlCrdtPayPlanHist(plan,PoolComm.PAY_TYPE_2);//保存历史
		
	}
	/**
	 * 支付计划修改异常，记录异常原因，扣减修改掉的金额
	 * @param plan
	 * @param relsAmt
	 * @param errMsg
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-30上午1:33:24
	 */
	public void txPayPlanModError(PlCrdtPayPlan plan,BigDecimal relsAmt,String errMsg)throws Exception{
		
		logger.info("主业务合同【"+plan.getContractNo()+"】借据【"+plan.getLoanNo()+"】支付计划【"+plan.getSerialNo()+"】修改失败...");
		plan.setRepayAmt(plan.getRepayAmt().add(relsAmt));
		
		BigDecimal allUsed = plan.getUsedAmt().add(plan.getRepayAmt());
		if(allUsed.compareTo(plan.getTotalAmt())==0){
			plan.setStatus(PublicStaticDefineTab.PAY_PLAN_03);//支付完成
		}
		
		//生成一条支付记录
		PlCrdtPayList cp = new PlCrdtPayList();
		cp.setSerialNo(plan.getSerialNo());//支付计划编号
		cp.setPayAmt(relsAmt);//本次支付金额
		cp.setUsage("网银/票据池-支付计划修改");//用途
		cp.setPostscript("网银/票据池-支付计划修改");//附言
		cp.setPayPlanId(plan.getId());        //对应支付计划表ID                            
		cp.setLoanNo(plan.getLoanNo());           //借据编号                                 
		cp.setContractNo(plan.getContractNo());       //合同编号                                 
		cp.setOnlineCrdtNo(plan.getOnlineCrdtNo());      //在线流贷协议编号                             
		cp.setBpsNo(plan.getBpsNo());            //票据池编号                                
		cp.setLoanAcctNo(plan.getLoanAcctNo());       //付款账号                                 
		cp.setLoanAcctName(plan.getLoanAcctName());      //付款户名                                 
		cp.setDeduAcctNo(plan.getDeduAcctNo());       //收款人名称                                
		cp.setDeduAcctName(plan.getDeduAcctName());      //收款人账号                                
		cp.setDeduBankCode(plan.getDeduBankCode());      //收款人开户行行号         ·                    
		cp.setDeduBankName(plan.getDeduBankName());      //收款人开户行名称                             
		cp.setIsLocal(plan.getIsLocal());          //是否跨行 0-否 1-是                         
		cp.setTransChanel(PublicStaticDefineTab.CHANNEL_NO_EBK);//渠道-网银
		cp.setOperatorType(PoolComm.PAY_TYPE_2);//支付类型-修改
		cp.setCreateDate(new Date());
		cp.setUpdateTime(new Date());
		cp.setStatus(PoolComm.PAY_STATUS_02);//支付成功--修改成功
		cp.setPayDesc(errMsg);//支付结果说明
		
		this.txStore(cp);
		this.txStore(plan);
		this.txSavePlCrdtPayPlanHist(plan,PoolComm.PAY_TYPE_2);//保存历史
		
	}

	@Override
	public Ret txModifyOnlinePayPlan(PlCrdtPayPlan plan,BigDecimal rlsAmt) throws Exception {
		
		logger.info("支付计划修改，处理主业务合同号【"+plan.getContractNo()+"】借据号【"+plan.getLoanNo()+"】支付计划编号【"+plan.getSerialNo()+"】");
		
		Ret modRet = new Ret();
		modRet.setRET_CODE(Constants.TX_FAIL_CODE);
		modRet.setRET_MSG("支付计划修改失败！");
		
		String onlineCrdtNo = plan.getOnlineCrdtNo();//在线业务协议编号
		String contractNo = plan.getContractNo();//在线业务主业务合同号


		
		//借据信息
		PedCreditDetail pcd = poolCreditProductService.queryCreditDetailByTransAccountOrLoanNo(null, plan.getLoanNo());
		
		// 主业务合同
		CreditProduct product = poolCreditProductService.queryProductByCreditNo(contractNo,null);// 主业务合同
		
		//在线流贷合同
		PlOnlineCrdt crdt = this.queryonlineCrdtByContractNo(contractNo);
		
		//核心同步借据状态
		pcd = poolCreditProductService.txSynchroLoan(pcd, product, product.getCrdtType());

		//该合同下所有支付计划总和
		PlCrdtPayPlan allPlanAmt =  this.queryAllPlanAmt(contractNo);

		//支付计划理论可修改金额 = 支付总额-取消支付金额
		BigDecimal planCanRelsAmt = allPlanAmt.getTotalAmt().subtract(allPlanAmt.getRepayAmt());
		//本条要修改的支付计划的客户支付总金额
		BigDecimal planListCanRelsAmt = plan.getTotalAmt().subtract(plan.getRepayAmt());

		if(planCanRelsAmt.compareTo(pcd.getActualAmount())>0){//整个借据的圈存金额  ＞ 实际借据金额
			
			//可释放金额=圈存金额-借据实际占用金额
			BigDecimal canRels = planCanRelsAmt.subtract(pcd.getActualAmount());//可释放金额总额 = 支付计划总额 - 取消支付的金额 - 借据余额
			if(canRels.compareTo(rlsAmt)>=0 && rlsAmt.compareTo(planListCanRelsAmt)>0){
				//可释放金额 >= 本次释放金额   并且  本次释放金额 > 本条支付计划的最大可释放金额  会出现支付总额为负情况
				modRet.setRET_MSG("修改金额总额不得超过"+planListCanRelsAmt+"元");
		        return modRet;
			}
			if(rlsAmt.compareTo(new BigDecimal(0))==0){
				//可释放金额 >= 本次释放金额   并且  本次释放金额 > 本条支付计划的最大可释放金额  会出现支付总额为负情况
				modRet.setRET_MSG("修改金额不得等于0元");
		        return modRet;
			}
			if(canRels.compareTo(rlsAmt)>=0){//可释放金额 >= 本次释放金额
				
				/**
				 *  因信贷原因，暂时不做当日贷款未用归还的信贷额度的释放
				 */
//				BigDecimal relsAmt = new BigDecimal(0);
//				BigDecimal addAmt = new BigDecimal(0);//累计每笔(支付计划总金额  - 取消支付计划金额)
//				//通过借据编号，查询借据下的支付计划
//    			OnlineQueryBean queryBean = new  OnlineQueryBean();
//				queryBean.setContractNo(pcd.getCrdtNo());//在线流贷主业务合同号
//				queryBean.setLoanNo(pcd.getLoanNo());//借据号
//    			List<PlCrdtPayPlan> plans = this.queryPlCrdtPayPlanListByBean(queryBean, null);
//    			
//    			if(null != plans && plans.size() > 0){
//    				for (int i = 0; i < plans.size(); i++) {
//    					PlCrdtPayPlan payPlan = plans.get(i);
//    					addAmt = addAmt.add(payPlan.getTotalAmt().subtract(payPlan.getRepayAmt()));
//					}
//    			}
    			//上送额度系统重新占用的金额  =  addAmt -  本次释放金额
//    			relsAmt = addAmt.subtract(rlsAmt);
//				ReturnMessageNew response = this.misRepayCrdtPJE021(crdt, relsAmt);
//				if(response.isTxSuccess()){
					//解圈存
					ReturnMessageNew unFrozResponse = null;
					try {
						CoreTransNotes unFroznote = new CoreTransNotes();
						unFroznote.setTranAmt(rlsAmt.toString());//解圈金额
						unFroznote.setFundNo(crdt.getFundNo());//圈存编号
						unFroznote.setBrcNo(crdt.getBranchNo());//机构上送入账机构
						unFrozResponse = poolCoreService.CORE008Handler(unFroznote);				
					} catch (Exception e) {
						
						//解圈存异常
						String errMsg = "支付计划修改失败，调核心系统异常:"+e;
						logger.info(errMsg);
						
						//解圈存异常依然增加取消支付的金额
						this.txPayPlanModError(allPlanAmt, rlsAmt, errMsg);
						
						modRet.setRET_MSG(errMsg);
				        return modRet;
					}

					
					if(unFrozResponse.isTxSuccess()){					
					
						//支付成功后的处理
						this.txPayPlanModSuccess(plan, rlsAmt);
						//协议已用额度修改
//		        		PedOnlineCrdtProtocol pro = this.queryOnlineProtocolByCrdtNo(crdt.getOnlineCrdtNo());
//						pro.setUsedAmt(pro.getUsedAmt().subtract(rlsAmt));
//						this.txStore(pro);
						modRet.setRET_CODE(Constants.TX_SUCCESS_CODE);
						modRet.setRET_MSG("支付计划修改成功！");
				        return modRet;
					
					}else{
						modRet.setRET_MSG("核心解圈存失败："+unFrozResponse.getRet().getRET_MSG());
				        return modRet;
					}
//				}else{
//					modRet.setRET_MSG("信贷额度释放失败！");
//			        return modRet;
//				}
		        
			}else{
				modRet.setRET_MSG("修改金额总额不得超过"+canRels+"元");
		        return modRet;

			}
			
		}else{
			modRet.setRET_MSG("借据余额等于圈存金额，本支付计划暂不支持修改！");
	        return modRet;
		}

	}
	
	@Override
	public PlCrdtPayPlan queryAllPlanAmt(String contractNo) {
		String sql ="select  sum(pay.totalAmt),sum(pay.usedAmt),sum(pay.repayAmt) from PlCrdtPayPlan pay where pay.contractNo='"+contractNo+"'";
		List result = this.find(sql);
		if(null != result && result.size()>0){
			Object[] amtObj =  (Object[]) result.get(0);
			PlCrdtPayPlan allPlanAmt = new PlCrdtPayPlan();
			allPlanAmt.setTotalAmt((BigDecimal)amtObj[0]);
			allPlanAmt.setUsedAmt((BigDecimal)amtObj[1]);
			allPlanAmt.setRepayAmt((BigDecimal)amtObj[2]);
			return allPlanAmt;
		}
		
		return null;
	}

	@Override
	public Ret txRepayOnlinePayPlan(PedCreditDetail loan, BigDecimal totalRelsAmt,String flowNo)throws Exception {
		
		logger.info("通过支付计划进行贷款未用归还处理，处理主业务合同号【"+loan.getCrdtNo()+"】借据号【"+loan.getLoanNo()+"】");
		
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("支付计划贷款未用归还操作失败！");
		
		PlOnlineCrdt crdt = this.queryonlineCrdtByContractNo(loan.getCrdtNo());//在线流贷批次表
		CreditProduct product = poolCreditProductService.queryProductByCreditNo(loan.getCrdtNo(),null);// 主业务合同 
		
		String fundNo = crdt.getFundNo();//圈存编号
		String loanNo = loan.getLoanNo();//借据号
		String accNo = crdt.getTransAccount();//贷款账号
		String DeduNo = crdt.getDeduAcctNo();//扣款账号
		
		
        CoreTransNotes note = new CoreTransNotes();
        note.setAccNo(loan.getTransAccount());//贷款账号
        ReturnMessageNew result = poolCoreService.PJH126012Handler(note);
        if(result.isTxSuccess()){
        	ReturnMessageNew response = null;
        	Map map = result.getBody();
        	
        	BigDecimal amt1 = StringUtil.getBigDecimalVal(map.get("DEFAULT_COMPOUND_INTEREST"));//拖欠复利
        	BigDecimal amt2 = StringUtil.getBigDecimalVal(map.get("DEFAULT_INTEREST_AMT"));//拖欠利息
        	BigDecimal amt3 = StringUtil.getBigDecimalVal(map.get("DEFAULT_PENALTY_INTEREST"));//拖欠罚息
        	if(null == amt1){
        		amt1 = new BigDecimal("0.00");
        	}
        	if(null == amt2){
        		amt2 = new BigDecimal("0.00");
        	}
        	if(null == amt3){
        		amt3 = new BigDecimal("0.00");
        	}
        	BigDecimal interest =amt1.add(amt2).add(amt3);   //利息
        	
        	BigDecimal totalAmt = new BigDecimal((String)map.get("LOAN_LEFT_AMT"));//至当日本息合计
        	
        	String type = "";//还款类型  3：提前还款  1：贷款归还
        	
        	if(DateUtils.getCurrDate().compareTo(loan.getEndTime())<=0){//未到期(含到期日当日)
        		if(totalRelsAmt.compareTo(interest)>0){
        			if(totalRelsAmt.compareTo(totalAmt)>=0){
        				
        				//贷款归还
        				response = this.txApplyRepayment(fundNo,loanNo, totalRelsAmt,"2",accNo,DeduNo,crdt.getBranchNo());
        				
        			}else{
        				
        				//提前还款
        				response = this.txApplyRepayment(fundNo,loanNo, totalRelsAmt,"1",accNo,DeduNo,crdt.getBranchNo());
        				
        			}
        		}else{ 
        			
        			//失败
        			this.txRepayFail(flowNo, "贷款归还失败：还款金额【"+totalRelsAmt+"】小于当前应还利息【"+interest+"】");
        			ret.setRET_MSG("贷款归还失败：还款金额【"+totalRelsAmt+"】小于当前应还利息【"+interest+"】");
        			return ret;
        		}
        	}else{
        		
        		//贷款归还
        		response = this.txApplyRepayment(fundNo,loanNo, totalRelsAmt,"2",accNo,DeduNo,crdt.getBranchNo());
        	}
        	
        	if(null != response && response.isTxSuccess()){
        		
        		/**
        		 * 业务当日做提前还款或贷款归还需要释放信贷授信额度
        		 * 因信贷原因，暂时不做当日贷款未用归还的信贷额度的释放
        		 */
        		/*if(DateUtils.formatDate(loan.getStartTime(), DateUtils.DATE_FORMAT).compareTo(DateUtils.formatDate(new Date(), DateUtils.DATE_FORMAT)) == 0){
        			
        			BigDecimal relsAmt = new BigDecimal(0);//上送额度系统重新占用的金额  =  addAmt  -  本次释放金额
        			BigDecimal addAmt = new BigDecimal(0);//累计每笔(支付计划总金额  - 取消支付计划金额)
        			
        			//通过借据编号，查询借据下的支付计划
        			OnlineQueryBean queryBean = new  OnlineQueryBean();
					queryBean.setContractNo(loan.getCrdtNo());//在线流贷主业务合同号
					queryBean.setLoanNo(loan.getLoanNo());//借据号
        			List<PlCrdtPayPlan> plans = this.queryPlCrdtPayPlanListByBean(queryBean, null);
        			
        			if(null != plans && plans.size() > 0){
        				for (int i = 0; i < plans.size(); i++) {
        					PlCrdtPayPlan plan = plans.get(i);
        					addAmt = addAmt.add(plan.getTotalAmt().subtract(plan.getRepayAmt()));
						}
        			}
        			relsAmt = addAmt.subtract(totalRelsAmt);
        			
   				 	//额度系统额度释放
					ReturnMessageNew response2 = this.misRepayCrdtPJE021(crdt, relsAmt);
					if(response2.isTxSuccess()){
						//额度系统释放额度成功，核心还款成功
						//成功
						this.txRePaySucc(flowNo, loan, product);
					}else{
						//额度系统释放额度失败，还款金额重新圈存
						//重新圈存
						CoreTransNotes frozNote = new CoreTransNotes();
						frozNote.setTranAmt(totalRelsAmt.toString());//圈存金额
						frozNote.setFundNo(crdt.getFundNo());//圈存编号

						frozNote.setUser(crdt.getBranchNo());//柜员
						frozNote.setBrcBld(crdt.getBranchNo());//机构
						ReturnMessageNew frozResponse = null;
						
						try {						
							frozResponse = poolCoreService.CORE007Handler(frozNote);
							if(frozResponse.isTxSuccess()){
								//圈存异常需手工处理后台数据
								//失败
			        			this.txRepayFail(flowNo, "贷款归还成功，在释放额度系统额度时失败，重新圈存成功，还款金额恢复！");
			        			ret.setRET_MSG("贷款归还成功，在释放额度系统额度时失败，重新圈存成功，还款金额恢复！");
			        			return ret;
							}else{
								//圈存异常需手工处理后台数据
								//失败
			        			this.txRepayFail(flowNo, "贷款归还成功，在释放额度系统额度时失败，重新圈存失败，还款金额恢复！");
			        			ret.setRET_MSG("贷款归还成功，在释放额度系统额度时失败，重新圈存失败，还款金额恢复！");
			        			return ret;
							}
						} catch (Exception e2) {
							//圈存异常需手工处理后台数据
							//失败
		        			this.txRepayFail(flowNo, "贷款归还成功，在释放额度系统额度时失败，重新圈存异常，还款金额恢复！");
		        			ret.setRET_MSG("贷款归还成功，在释放额度系统额度时失败，重新圈存异常，还款金额恢复！");
		        			return ret;
						}
					}
        			
        		}else{*/
        			
        			//成功  隔日还款不需释放信贷授信额度
        			this.txRePaySucc(flowNo, loan, product);
//        		}
        		
        		
//        		//协议已用额度修改
//        		PedOnlineCrdtProtocol pro = this.queryOnlineProtocolByCrdtNo(crdt.getOnlineCrdtNo());
//				pro.setUsedAmt(pro.getUsedAmt().subtract(totalRelsAmt));
//				this.txStore(pro);
        		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        		ret.setRET_MSG("支付计划贷款未用归还操作成功！");
        		
        	}else{
        		
        		//失败
        		this.txRepayFail(flowNo, "贷款归还失败："+response.getRet().getRET_MSG());        		
        		ret.setRET_MSG("贷款归还失败："+response.getRet().getRET_MSG());
        	}
        	

        }
        
        String bpsNo = null;
        
        try {
        	logger.info("客户做了贷款归还后更新该票据池的额度...");
			bpsNo = loan.getBpsNo();
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
        	List<PedProtocolDto> proList = new ArrayList<PedProtocolDto>();
        	proList.add(dto);
        	financialService.txReCreditCalculationTask(proList);
		} catch (Exception e) {
			logger.error("提前还款后更新票据池【"+bpsNo+"】失败："+e.getMessage(), e);
		}

		return ret;
	}
	
	public ReturnMessageNew misRepayCrdtPJE021(PlOnlineCrdt crdt,BigDecimal totalRelsAmt){
		ReturnMessageNew response = new ReturnMessageNew();
		//额度系统额度释放
		CreditTransNotes note1 = new CreditTransNotes();
		PedOnlineCrdtProtocol protocol =  this.queryOnlineProtocolByCrdtNo(crdt.getOnlineCrdtNo());
		note1.setOnlineNo(crdt.getOnlineCrdtNo());
		note1.setoNlineCreditNo(protocol.getBaseCreditNo());
		note1.setCustomerId(protocol.getCustNumber());
		note1.setOperationType(PublicStaticDefineTab.CREDIT_OPERATION_EFFECTIVE);
		note1.setOnlineType(PublicStaticDefineTab.PRODUCT_002);
		note1.setContractNo(crdt.getContractNo());
		note1.setEFFECTIVE_DATE(DateUtils.toDateString(DateUtils.getCurrDate()));
		note1.setEXPIRY_DATE(DateUtils.toDateString(crdt.getDueDate()));
		note1.setBusinessSum(totalRelsAmt);
		try {
			response = poolCreditClientService.txPJE021(note1);
		} catch (Exception e) {
			logger.info("信贷额度释放接口调用异常！"+e.getMessage());
			response.setTxSuccess(false);
		}
		return response;
	}

	@Override
	public List queryPlCrdtPayPlanUncleared(OnlineQueryBean queryBean, Page page)
			throws Exception {
		String sql ="select dto from PlCrdtPayPlan dto,PedOnlineCrdtProtocol pro,PedCreditDetail detail where dto.onlineCrdtNo=pro.onlineCrdtNo and dto.loanNo = detail.loanNo ";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())){
				sql= sql+" and dto.onlineCrdtNo=?";
				param.add(queryBean.getOnlineCrdtNo());
			}
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				sql= sql+" and pro.custNumber=?";
				param.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getStatus())){
				sql= sql+" and dto.status=?";
				param.add(queryBean.getStatus());
			}
			if(queryBean.getStatuList() != null && queryBean.getStatuList().size() > 0){
				sql= sql+" and dto.status in('P02','P03')";
//				param.add(queryBean.getStatuList());
			}
			if(StringUtils.isNotBlank(queryBean.getContractNo())){
				sql= sql+" and dto.contractNo=?";
				param.add(queryBean.getContractNo());
			}
			if(StringUtils.isNotBlank(queryBean.getEbkCustNo())){
				sql= sql+" and  pro.ebkCustNo like ?";
				param.add("%"+queryBean.getEbkCustNo()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getDeduAcctName())){
				sql= sql+" and dto.deduAcctName like(?)";
				param.add("%"+queryBean.getDeduAcctName()+"%");
			}
			if(null != queryBean.getStartAmt()){
				sql= sql+" and dto.totalAmt-dto.repayAmt >=?";
				param.add(queryBean.getStartAmt());
			}
			if(null != queryBean.getEndAmt()){
				sql= sql+" and dto.totalAmt-dto.repayAmt <=?";
				param.add(queryBean.getEndAmt());
			}
			if(StringUtils.isNotBlank(queryBean.getLoanNo())){
				sql= sql+" and dto.loanNo=?";
				param.add(queryBean.getLoanNo());
			}
			if(StringUtils.isNotBlank(queryBean.getSerialNo())){
				sql= sql+" and dto.serialNo=?";
				param.add(queryBean.getSerialNo());
			}
			if(StringUtils.isNotBlank(queryBean.getCrdtId())){
				sql= sql+" and dto.crdtId=?";
				param.add(queryBean.getCrdtId());
			}
			if(StringUtils.isNotBlank(queryBean.getIds())){
				sql= sql+" and dto.id= in(?)";
				/* 将数组转换成List */
				param.add(Arrays.asList(queryBean.getIds().split(",")));
			}
			if(StringUtils.isNotBlank(queryBean.getBatchId())){
				sql= sql+" and dto.crdtId=?";
				param.add(queryBean.getBatchId());
			}
			if(StringUtils.isNotBlank(queryBean.getPayeeSerialNo())){
				sql= sql+" and dto.serialNo=?";
				param.add(queryBean.getPayeeSerialNo());
			}
			//借据是否已结清
			if(StringUtils.isNotBlank(queryBean.getStatusDesc())){
				sql= sql+" and detail.loanStatus !=?";
				param.add(queryBean.getStatusDesc());
			}
			if(StringUtils.isNotBlank(queryBean.getStatus())){
				sql= sql+" and dto.status=?";
				param.add(queryBean.getStatus());
			}
			  //根据网银调整，传 1 的时候查询出可支付的支付计划
			
//			if(StringUtils.isNotBlank(queryBean.getOperatorType()) && queryBean.getOperatorType().equals("0")){//查询已支付完成的支付计划   剩余支付金额为0
//				sql= sql+" and (dto.totalAmt - dto.usedAmt - dto.repayAmt) = 0 ";
//			}else if(StringUtils.isNotBlank(queryBean.getOperatorType()) && queryBean.getOperatorType().equals("1")){//查询未支付的支付计划  已支付金额为0
//				sql= sql+" and dto.usedAmt = 0 ";
//			}else if(StringUtils.isNotBlank(queryBean.getOperatorType()) && queryBean.getOperatorType().equals("2")){//查询 部分支付的支付计划    剩余支付金额不为0  已支付金额不为0
//				sql= sql+" and dto.usedAmt != 0 and (dto.totalAmt - dto.usedAmt - dto.repayAmt) != 0 ";
//			}
		}
		List result = this.find(sql, param, page);
		if(null != result && result.size()>0){
			
			return result;
		}
		return null;
	}
	
	
	@Override
	public BigDecimal queryOnlineCrdtFrozenTotalAmt(String loanNo) throws Exception{
		
		BigDecimal totalFrozenAmt = BigDecimal.ZERO;//圈存总额
		
		//圈存总金额 = 该借据下所有支付计划中的总金额  - 总已支付 - 总取消支付 （ 即：totalAmt - usedAmt - repayAmt ）
		
		String sql ="select sum(dto.totalAmt)-sum(dto.usedAmt)-sum(dto.repayAmt) from PlCrdtPayPlan dto where dto.status='P02' AND dto.loanNo = ?";
		List param = new ArrayList();
		param.add(loanNo);
		List result = this.find(sql, param);
		
		if(null != result && result.size()>0){
			totalFrozenAmt = (BigDecimal)result.get(0);
			if(null == totalFrozenAmt){
				totalFrozenAmt =  BigDecimal.ZERO;
			}
		}
		
		return totalFrozenAmt;
	}

	@Override
	public List queryOnlineCrdtPayeeListForPjc053(OnlineQueryBean queryBean,
			Page page) {
		
		/**
		 * 3、统计各收款人的已用额度
		 */
		StringBuffer hql = new StringBuffer();
		hql.append("select info.ID,info.PAYEE_ID,info.PAYEE_ACCT_NAME,info.PAYEE_ACCT_NO,info.PAYEE_OPEN_BANK_NO,info.PAYEE_OPEN_BANK_NAME,info.PAYEE_TOTAL_AMT,info.PAYEE_STATUS,info.ONLINE_CRDT_NO,info.CRDT_ID,info.CREATE_TIME,info.UPDATE_TIME,d.usedAmt,info.IS_LOCAL " +
				"from PED_ONLINE_CRDT_INFO info left join PED_ONLINE_CRDT_PROTOCOL dto on info.CRDT_ID = dto.id left join (select sum(detail.TOTAL_AMT-detail.REPAY_AMT) usedAmt,DEDU_ACCT_NO,ONLINE_CRDT_NO from PL_CRDT_PAY_PLAN detail where detail.STATUS not in(:status) group by DEDU_ACCT_NO,ONLINE_CRDT_NO) d on info.PAYEE_ACCT_NO = d.DEDU_ACCT_NO and d.ONLINE_CRDT_NO = info.ONLINE_CRDT_NO where 1=1 ");
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("status");
		queryBean.getStatuList().add(PublicStaticDefineTab.PAY_PLAN_04);
		paramValue.add(queryBean.getStatuList());
		if(null != queryBean){
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
			if(StringUtils.isNotBlank(queryBean.getCrdtId())){
				hql.append(" and info.CRDT_ID=:acptId");
				paramName.add("acptId");
				paramValue.add(queryBean.getCrdtId());
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
		}
		
		hql.append(" order by info.PAY_TYPE ASC ,info.PAYEE_ID ASC ");
		
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
  				if(StringUtils.isNotBlank((String)obj[2]) && obj[2].equals("自主支付")){
  					//自主支付
  					/**
  					 * 1、查询协议下的总已用的批次额度
  					 */
  					String sql = "SELECT sum(LOAN_AMT) from PL_ONLINE_CRDT WHERE ONLINE_CRDT_NO = '"+bean.getOnlineCrdtNo()+"' and Deal_Status not in('DS005') ";
  					List crdt = sessionDao.SQLQuery(sql);
  					BigDecimal bigcrdt = new BigDecimal(0);
  					if(crdt != null && crdt.size() > 0){
  						if(crdt.get(0) != null){
  							bigcrdt = (BigDecimal) crdt.get(0);
  						}
  					}
  					/**
  					 * 2、查询协议下的总已用的支付计划额度
  					 */
  					String sql2 = "select sum(TOTAL_AMT) usedAmt from PL_CRDT_PAY_PLAN detail where detail.STATUS not in('P04') and ONLINE_CRDT_NO = '"+bean.getOnlineCrdtNo()+"'";
  					List pay = sessionDao.SQLQuery(sql2);
  					BigDecimal bigpay = new BigDecimal(0);
  					if(pay != null && pay.size() > 0){
  						if(pay.get(0) != null){
  							bigpay = (BigDecimal) pay.get(0);
  						}
  					}
  					BigDecimal bigres = bigcrdt.subtract(bigpay);
  					bean.setPayeeUsedAmt(bigres);
  				}else{
  					bean.setPayeeUsedAmt((BigDecimal)obj[12]!= null?(BigDecimal)obj[12]:new BigDecimal(0));
  				}
  				bean.setIsLocal((String)obj[13]);
  				bean.setPayeeFreeAmt(bean.getPayeeTotalAmt().subtract(bean.getPayeeUsedAmt()));
  				result.add(bean);    
  			}
  			return result;
  		}
		return list;
	}

	@Override
	public BigDecimal queryUnUsedReturnAmtOfCrdt(String crdtNo,Date date)
			throws Exception {
		String hql = "select sum(payAmt) from PlCrdtPayList where contractNo = '"+crdtNo+"' and to_char(createDate,'yyyy-MM-dd') = '"+DateUtils.toString(date,"yyyy-MM-dd")+"' ";
		List result = this.find(hql);
		BigDecimal unUsedReturnAmt = BigDecimal.ZERO;
		
		if(null != result && result.size()>0){
			unUsedReturnAmt = (BigDecimal)result.get(0);
			if(null == unUsedReturnAmt){
				unUsedReturnAmt =  BigDecimal.ZERO;
			}
		}
		return unUsedReturnAmt;
	}


}
