package com.mingtech.application.pool.online.manage.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.domain.TMessageRecord;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocolHist;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocolHist;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineBlackInfo;
import com.mingtech.application.pool.online.manage.domain.PedOnlineHandleLog;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfoHist;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
@Service("onlineManageService")
public class OnlineManageServiceImpl extends GenericServiceImpl implements OnlineManageService {
	private static final Logger logger = Logger.getLogger(OnlineManageServiceImpl.class);

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
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private GenericHibernateDao sessionDao;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PoolCoreService poolCoreService;
	/**
	 * @author wss
	 * @date 2021-4-27
	 * @description ?????????????????????
	 * @param onlineAcptNo ??????????????????
	 */
	public List<PedOnlineMsgInfo> queryOnlineMsgInfoList(String onlineNo,String role) {
		String sql ="select dto from PedOnlineMsgInfo dto where dto.onlineNo=? ";
		List param = new ArrayList();
		param.add(onlineNo);
		if(StringUtils.isNotBlank(role)){
			sql =sql + " and dto.addresseeRole = ?";
			param.add(role);
		}
		List<PedOnlineMsgInfo> result = (List<PedOnlineMsgInfo>)this.find(sql, param);
		return result;
	}
	
	/**
	 * @author wss
	 * @date 2021-4-27
	 * @description ?????????????????????
	 * @param onlineAcptNo ??????????????????
	 */
	public List queryOnlineMsgInfoList(OnlineQueryBean queryBean,Page page) {
		String sql ="select dto from PedOnlineMsgInfo dto where 1=1";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getAddresseeNo())){
				sql = sql+" and dto.addresseeNo =?";
				param.add(queryBean.getAddresseeNo());
			}
			if(StringUtils.isNotBlank(queryBean.getAddresseeName())){
				sql = sql+" and dto.addresseeName like(?)";
				param.add("%"+queryBean.getAddresseeName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineProtocolType())){
				sql = sql+" and dto.onlineProtocolType =?";
				param.add(queryBean.getOnlineProtocolType());
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
				sql = sql+" and dto.onlineNo =?";
				param.add(queryBean.getOnlineNo());
			}
			if(StringUtils.isNotBlank(queryBean.getModeType())){
				sql = sql+" and dto.modeType =?";
				param.add(queryBean.getModeType());
			}
			if(StringUtils.isNotBlank(queryBean.getAddresseeRole())){
				sql = sql+" and dto.addresseeRole =?";
				param.add(queryBean.getAddresseeRole());
			}
		}
		List result = this.find(sql, param,page);
		return result;
	}

	/**
	 * @author wss
	 * @throws  
	 * @throws Exception 
	 * @date 2021-4-30
	 * @description ???????????????????????????
	 */
	public Ret txSaveMsgInfo(OnlineQueryBean queryBean) throws Exception {
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		if(null != queryBean.getDetalis() && queryBean.getDetalis().size()>0){
			//????????????
			List msgList = null;
			String onlineNo ="";
			String ptlId = "";
			String bpsName = "";//????????????
			String busiTypeName = "";//????????????
			queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);//??????
			if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
				if(PublicStaticDefineTab.PRODUCT_001.equals(queryBean.getOnlineProtocolType())){
					queryBean.setOnlineAcptNo(queryBean.getOnlineNo());
				}else if(PublicStaticDefineTab.PRODUCT_002.equals(queryBean.getOnlineProtocolType())){
					queryBean.setOnlineCrdtNo(queryBean.getOnlineNo());
				}
			}
			if(PublicStaticDefineTab.PRODUCT_001.equals(queryBean.getOnlineProtocolType())){//??????
				PedOnlineAcptProtocol ptl = pedOnlineAcptService.queryOnlineAcptProtocol(queryBean);
				busiTypeName = "??????";
				if(null != ptl){
					onlineNo = ptl.getOnlineAcptNo();
					ptlId = ptl.getId();
					bpsName = ptl.getBpsName();
					msgList = this.queryOnlineMsgInfoList(ptl.getOnlineAcptNo(), PublicStaticDefineTab.ROLE_1);
				}else{
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("??????????????????????????????");
					return ret;
				}
			}else if(PublicStaticDefineTab.PRODUCT_002.equals(queryBean.getOnlineProtocolType())){
				PedOnlineCrdtProtocol ptl = pedOnlineCrdtService.queryOnlineProtocol(queryBean);
				busiTypeName = "??????";
				if(null != ptl){
					onlineNo = ptl.getOnlineCrdtNo();
					ptlId = ptl.getId();
					bpsName = ptl.getBpsName();
					msgList = this.queryOnlineMsgInfoList(ptl.getOnlineCrdtNo(), PublicStaticDefineTab.ROLE_1);
				}else{
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("??????????????????????????????");
					return ret;
				}
			}
			List errors = new ArrayList();
			//?????????????????????
			for(int i=0;i<queryBean.getDetalis().size();i++){
				boolean flag = false;//?????????????????????????????????????????????????????????
				PedOnlineMsgInfo info = (PedOnlineMsgInfo) queryBean.getDetalis().get(i);
				info.setOnlineNo(onlineNo);//??????????????????
				info.setOnlineProtocolId(ptlId);
				if(StringUtils.isBlank(info.getAddresseePhoneNo()) ){
					errors.add(info.getAddresseeName()+"??????????????????11?????????|");
				}else if(11 !=info.getAddresseePhoneNo().length() || !info.getAddresseePhoneNo().matches("[0-9]+")){
					errors.add(info.getAddresseeName()+"??????????????????11?????????|");
				}
				if(null != msgList && msgList.size()>0){
					for(int f=0;f<msgList.size();f++){
						PedOnlineMsgInfo old = (PedOnlineMsgInfo) msgList.get(f);
						//???????????????????????? ????????????????????????????????????
						if(old.getAddresseePhoneNo().equals(info.getAddresseePhoneNo())){
							flag = true;//???????????????????????????
							//??????????????????
							boolean isChange = this.adjustIsChange(old,info);
							if(!isChange){
								old.setModeType(PublicStaticDefineTab.MOD02);
								info.setModeType(PublicStaticDefineTab.MOD02);
							}else{
								old.setModeType(PublicStaticDefineTab.MOD00);
								info.setModeType(PublicStaticDefineTab.MOD00);
							}
						}
					}
					
				}
				if(!flag){
					//??????????????????????????????  ????????????????????????
					String Template = "??????????????????????????????"+bpsName+"???????????????????????????"+busiTypeName+"????????????????????????";
					toSendMsgForNotifier(PublicStaticDefineTab.ROLE_1, info.getAddresseePhoneNo(), queryBean.getOnlineProtocolType(), Template,info.getAddresseeName(),info.getOnlineNo());
					
				}
			}
			//??????????????????
			List<PedOnlineMsgInfo> list =new ArrayList<PedOnlineMsgInfo>();
			if(null != msgList && msgList.size()>0){
				for(int f=0;f<msgList.size();f++){
					boolean flag = false;
					PedOnlineMsgInfo old = (PedOnlineMsgInfo) msgList.get(f);
					for(int e = 0;e<queryBean.getDetalis().size();e++){
						PedOnlineMsgInfo info = (PedOnlineMsgInfo) queryBean.getDetalis().get(e);
						if(old.getAddresseePhoneNo().equals(info.getAddresseePhoneNo())){
							flag = true;
							break;
						}
					}
					if(!flag){
						old.setModeType(PublicStaticDefineTab.MOD03);
						//?????????????????????????????????????????????
						String Template = "??????????????????????????????"+bpsName+"???????????????????????????"+busiTypeName+"????????????????????????";
						toSendMsgForNotifier(PublicStaticDefineTab.ROLE_1, old.getAddresseePhoneNo(), queryBean.getOnlineProtocolType(), Template,old.getAddresseeName(),old.getOnlineNo());
						list.add(old);
					}
				}
			}
			this.dao.storeAll(queryBean.getDetalis());
			//????????????
			if(null != msgList && msgList.size()>0){
				this.txSaveMsgHist(onlineNo, msgList);
				this.txDeleteAll(msgList);
			}
			if(null !=queryBean.getDetalis() && queryBean.getDetalis().size()>0){
				this.txSaveMsgHist(onlineNo, queryBean.getDetalis());
			}
			ret.setSomeList(errors);
		}else{
			//?????????????????????????????????;?????????????????????????????????
			
			
		}
		return ret;
	}
	private boolean adjustIsChange(PedOnlineMsgInfo old, PedOnlineMsgInfo info) {
		if(!old.getAddresseeNo().equals(info.getAddresseeNo())){
			return false;
		}
		if(!old.getAddresseeName().equals(info.getAddresseeName())){
			return false;
		}
		if(!old.getAddresseePhoneNo().equals(info.getAddresseePhoneNo())){
			return false;
		}
		return true;
	}

	/**
	 * @Title addMsgToMap
	 * @author wss
	 * @date 2021-6-2
	 * @Description ??????????????????????????????
	 */
	private Map addMsgToMap(Map map, PedOnlineMsgInfo msg) {
		if(map.containsKey(msg.getOnlineNo())){
			List msgList = (List) map.get(msg.getOnlineNo());
			msgList.add(msg);
			map.put(msg.getOnlineNo(), msgList);
		}else{
			List msgList = new ArrayList();
			msgList.add(msg);
			map.put(msg.getOnlineNo(), msgList);
		}
		return map;
	}

	/**
	 * @Title txSaveMsgHist
	 * @author wss
	 * @date 2021-6-2
	 * @Description ????????????
	 */
	private void txSaveMsgHist(String onlineNo,List<PedOnlineMsgInfo> list) throws Exception {
		//????????????
		OnlineQueryBean bean = new OnlineQueryBean();
		List msgList = new ArrayList();
		List payList = new ArrayList();
		if(PublicStaticDefineTab.PRODUCT_001.equals(list.get(0).getOnlineProtocolType())){
			bean.setOnlineAcptNo(onlineNo);
			List ptlHists = pedOnlineAcptService.queryOnlineAcptPtlHist(bean);
			if(null != ptlHists && ptlHists.size()>0){
				PedOnlineAcptProtocolHist oldHist = (PedOnlineAcptProtocolHist) ptlHists.get(0);
				PedOnlineAcptProtocolHist ptlHist = new PedOnlineAcptProtocolHist();
				BeanUtils.copyProperties(ptlHist, oldHist);
				ptlHist.setId(null);
				PedOnlineAcptProtocol protocol = this.pedOnlineAcptService.queryOnlinAcptPtlByNo(onlineNo);
				ptlHist.setModeMark(DateUtils.getCurrDateTime()+protocol.getId());
				ptlHist.setLastSourceId(oldHist.getId());
				pedOnlineAcptService.txStore(ptlHist);
				//???????????????
				for(int i=0;i<list.size();i++){
					PedOnlineMsgInfo msg = (PedOnlineMsgInfo) list.get(i);
					PedOnlineMsgInfoHist hist = new PedOnlineMsgInfoHist();
					BeanUtils.copyProperties(hist, msg);
					hist.setCreateTime(new Date());
					PedOnlineMsgInfoHist old = this.queryonlineMsgInfoHist(msg.getAddresseeNo());
					if(null != old){
						hist.setLastSourceId(old.getId());
					}
					hist.setModeMark(ptlHist.getModeMark());
					msgList.add(hist);
				}
			}
		}else{
			bean.setOnlineCrdtNo(onlineNo);
			List ptlHists = pedOnlineCrdtService.queryOnlineCrdtPtlHist(bean);
			if(null != ptlHists && ptlHists.size()>0){
				PedOnlineCrdtProtocolHist oldHist = (PedOnlineCrdtProtocolHist) ptlHists.get(0);
				PedOnlineCrdtProtocolHist ptlHist = new PedOnlineCrdtProtocolHist();
				BeanUtils.copyProperties(ptlHist, oldHist);
				ptlHist.setId(null);
				PedOnlineCrdtProtocol protocol = this.pedOnlineCrdtService.queryOnlineProtocolByCrdtNo(onlineNo);
				ptlHist.setModeMark(DateUtils.getCurrDateTime()+protocol.getId());
				ptlHist.setLastSourceId(oldHist.getId());
				pedOnlineCrdtService.txStore(ptlHist);
				//???????????????
				for(int i=0;i<list.size();i++){
					PedOnlineMsgInfo msg = (PedOnlineMsgInfo) list.get(i);
					PedOnlineMsgInfoHist hist = new PedOnlineMsgInfoHist();
					BeanUtils.copyProperties(hist, msg);
					hist.setCreateTime(new Date());
					PedOnlineMsgInfoHist old = this.queryonlineMsgInfoHist(msg.getAddresseeNo());
					if(null != old){
						hist.setLastSourceId(old.getId());
					}
					hist.setModeMark(ptlHist.getModeMark());
					msgList.add(hist);
				}
			}
		}
		this.txStoreAll(msgList);
	}
	
	/**
	 * @Title queryonlineMsgInfoHist
	 * @author wss
	 * @date 2021-6-2
	 * @Description ???????????????????????????
	 * @return PedOnlineMsgInfoHist
	 */
	public PedOnlineMsgInfoHist queryonlineMsgInfoHist(String addresseeNo) {
		String sql ="select dto from PedOnlineMsgInfoHist dto where dto.addresseeNo=?";
		List param = new ArrayList();
		param.add(addresseeNo);
		sql = sql +" order by dto.createTime desc";
		List result = this.find(sql, param);
		if(null != result && result.size()>0){
			return (PedOnlineMsgInfoHist) result.get(0);
		}else{
			return null;
		}
	}

	/**
	 * @author wss
	 * @date 2021-5-6
	 * @param queryBean
	 * @description ???????????????
	 */
	public List queryBlackList(OnlineQueryBean queryBean) {
		String sql ="select dto from PedOnlineBlackInfo dto where 1=1";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getCustNumber())){
				sql = sql+" and dto.custNo =?";
				param.add(queryBean.getCustNumber());
			}
			if(StringUtils.isNotBlank(queryBean.getCustName())){
				sql = sql+" and dto.custName like(?)";
				param.add("%"+queryBean.getCustName()+"%");
			}
			if(StringUtils.isNotBlank(queryBean.getDeptId())){
				sql = sql+" and dto.deprtId =?";
				param.add(queryBean.getDeptId());
			}
			if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
				sql = sql+" and dto.custOrgcode =?";
				param.add(queryBean.getCustOrgcode());
			}
			if(StringUtils.isNotBlank(queryBean.getStatus())){
				sql = sql+" and dto.status =?";
				param.add(queryBean.getStatus());
			}
		}
		List result = this.find(sql, param);
		return result;
	}

	/**
	 * @author wss
	 * @date 2021-5-6
	 * @param queryBean
	 * @description ???????????????
	 */
	public boolean onlineBlackListCheck(OnlineQueryBean queryBean) {
		List list = this.queryBlackList(queryBean);
		if(null != list && list.size()>0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * ??????????????????
	 */
	public void txSaveTrdeLog(String custNo,String bpsNo,String billNo,String busiId,String errorType,String operationType,String tradeName,String tradeResult,String tradeCode,String busiName, String sendType) {
		
		try {			
			PedOnlineHandleLog log = new PedOnlineHandleLog();
			log.setCustNumber(custNo);
			log.setBpsNo(bpsNo);
			log.setBillNo(billNo);//??????/?????????
			log.setErrorType(errorType);//????????????
			log.setOnlineNo("");//??????????????????
			log.setOperationType(operationType);//??????
			log.setTradeCode(tradeCode);//?????????
			log.setTradeName(tradeName);//??????
			log.setBusiName(busiName);//????????????
			/**
			 * ????????????????????????????????????
			 */
			if(tradeResult.length() > 3000){
				log.setTradeResult(tradeResult.substring(0,2999));//????????????
			}else{
				log.setTradeResult(tradeResult);//????????????
			}
			log.setSendType(sendType);//????????????
			log.setBusiId(busiId);
			log.setCreateTime(new Date());
			this.txStore(log);
		} catch (Exception e) {
			logger.error("??????????????????PedOnlineHandleLog?????????????????????????????????"+bpsNo+"?????????????????????????????????"+tradeResult+"???",e);
		}
	}
	
	@Override
	public void txSaveTrdeLog(PedOnlineHandleLog log){
		try {
			log.setCreateTime(new Date());
			this.txStore(log);
		} catch (Exception e) {
			logger.error("??????????????????PedOnlineHandleLog?????????????????????????????????"+log.getBpsNo()+"?????????????????????????????????"+log.getTradeResult()+"???",e);
		}
	}

	/**
	 *  ??????ID??????PedOnlineMsgInfo ??????????????????
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PedOnlineMsgInfo loadByPedOnlineMsgInfoId(String id) throws Exception {
		String sql = "select obj from PedOnlineMsgInfo obj where obj.id =?";
		List param = new ArrayList();
		param.add(id);
		List list = this.find(sql, param);
		if(list!=null&&list.size()>0){
			return (PedOnlineMsgInfo) list.get(0);
		}
		return null;
	}

	/**
	 * ????????????
	 * @throws Exception 
	 */
	public void txSendMsg(String role, String custName, String phoneNo,
			String busiType, BigDecimal totalAmt, BigDecimal succAmt,boolean succFlag,String addresseeName,String onlineNo) throws Exception {
		String typeNname = "";
		String succ = "";
		if(PublicStaticDefineTab.PRODUCT_001.equals(busiType)){
			typeNname="??????";
		}else{
			typeNname="??????";
		}
		if(null == succAmt){
			succAmt = new BigDecimal(0);
		}
		if(succFlag){
			succ="??????";
		}else{
			succ="??????";
		}
		
		CoreTransNotes note = new CoreTransNotes();
		note.setPushID(phoneNo);
		if("0".equals(role)){//??????
			note.setTemplate(custName+"????????????????????????"+typeNname+"??????"+totalAmt+"??????"+succ+"???????????????"+succ+"???");
		}else{
			note.setTemplate(custName+"?????????"+typeNname+"?????????"+totalAmt+"??????"+succ+succAmt+"?????????????????????????????????????????????");
		}
		TMessageRecord msg = new TMessageRecord();
		msg.setBusiType(busiType);
		msg.setMsgContent(note.getTemplate());
		msg.setPhoneNo(phoneNo);
		msg.setRole(role);
		msg.setCreateTime(new Date());
		msg.setSendResult("0");
		msg.setOnlineNo(onlineNo);
		msg.setAddresseeName(addresseeName);
		List<TMessageRecord> list = new ArrayList<TMessageRecord>();
		list.add(msg);
		financialAdviceService.txCreateList(list);
		//??????????????????
	    Map<String, String> reqParams =new HashMap<String,String>();
		reqParams.put("busiId", msg.getId());
		reqParams.put("msg", note.getTemplate());
		autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOL_MSS_TASK_NO, msg.getId(), AutoTaskNoDefine.BUSI_TYPE_MSS, reqParams,phoneNo,phoneNo,null,null);

	}

	/**
	 * ????????????????????????
	 */
	public List txqueryOnlineAgreementList(OnlineQueryBean queryBean, User user, Page page) throws Exception {
		String sql ="select a.id,a.type,a.onlineNo,a.PROTOCOL_STATUS,a.CUST_NAME,a.amt,a.USED_AMT,a.BPS_NO,a.BPS_NAME,a.APP_NAME,a.SIGN_BRANCH_NAME,a.OPEN_DATE,a.CHANGE_DATE,a.DUE_DATE,a.CREATE_TIME,a.UPDATE_TIME,a.POOL_CREDIT_RATIO,a.checked,a.SIGN_BRANCH_NO,a.usedAmt,a.REPAY_AMT,a.appNo  from (";
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineProtocolType())){
				if(PublicStaticDefineTab.PRODUCT_001.equals(queryBean.getOnlineProtocolType())){
					sql += this.createAcptPtlSql(queryBean);
				}else{
					sql += this.createCrdtSql(queryBean);
				}
			}else{
				sql += this.createAcptPtlSql(queryBean);
				sql += " union all ";
				sql += this.createCrdtSql(queryBean);
			}
		}else{
			sql += this.createAcptPtlSql(queryBean);
			sql += " union all ";
			sql += this.createCrdtSql(queryBean);
		}
		sql += " )a where 1=1 ";
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(queryBean.getType().equals("0")){
				//??????????????????
				if(str.equals("0") || str.equals("2")){//???????????????;??????????????????????????????????????????????????????  ?????????????????????
					
				}else if(str.equals("1") || str.equals("3")){//???????????????;?????????????????????????????????????????????????????? ?????????????????????????????????
						//?????????????????????????????????????????????????????????
					String hql = departmentService.queryDeptInnerbankcode(user.getDepartment().getInnerBankCode(), -1);
					if(StringUtils.isNotEmpty(hql)){
						sql = sql + (" and a.SIGN_BRANCH_NO in ("+hql+") ");
						
					}
				}else if(str.equals("4")){//????????????:?????????????????????????????????????????????????????????
					sql = sql + (" and a.APP_NAME = '"+user.getName()+"' ");
				}else if(str.equals("5")){//??????????????????:?????????
					return null;
				}
				
			}else{
				//????????????????????????-??????????????????
				if(!str.equals("0") && !str.equals("2")){//????????????????????????????????????
					return null;
				}
				
			}
			
		}else{
			return null;
		}
		sql += " order by a.onlineNo desc";
		List list = sessionDao.SQLQuery(sql,page);
		if(null != list && list.size()>0){
			List result = new ArrayList();
			for(int i=0;i<list.size();i++){
				Object[] obj= (Object[])list.get(i);
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setId((String)obj[0]);
				bean.setOnlineProtocolType(obj[1].toString());
				bean.setOnlineNo((String)obj[2]);
				
				if(PublicStaticDefineTab.STATUS_0.equals((String)obj[3])){
					bean.setStatusDesc( "??????");
				}else if(PublicStaticDefineTab.STATUS_1.equals((String)obj[3])){
					bean.setStatusDesc("??????");
				}else{
					bean.setStatusDesc((String)obj[3]);
				}
				bean.setProtocolStatus((String)obj[3]);
				bean.setCustName((String)obj[4]);
				bean.setTotalAmt((BigDecimal)obj[5]);
				
				
				BigDecimal useAmt1 =  new BigDecimal(0);//?????????????????????
				BigDecimal useAmt2 =  new BigDecimal(0);//?????????????????????
				BigDecimal useAmt3 =  new BigDecimal(0);//????????????????????????
				if((BigDecimal)obj[6]!=null){
					useAmt1 = (BigDecimal)obj[6];
				}
				if((BigDecimal)obj[19]!=null){
					useAmt2 = (BigDecimal)obj[19];
				}
				if((BigDecimal)obj[20]!=null){
					useAmt3 = (BigDecimal)obj[20];
				}
				bean.setUsedAmt(useAmt2.subtract(useAmt3));
				if(useAmt1.compareTo(useAmt2.subtract(useAmt3)) != 0){
					if(obj[1].toString().equals("0")){
						//????????????
						PedOnlineAcptProtocol pro = (PedOnlineAcptProtocol) this.load(bean.getId(),PedOnlineAcptProtocol.class);
						pro.setUsedAmt(useAmt2.subtract(useAmt3));
						this.txStore(pro);
					}else{
						//????????????
						PedOnlineCrdtProtocol pro = (PedOnlineCrdtProtocol) this.load(bean.getId(),PedOnlineCrdtProtocol.class);
						pro.setUsedAmt(useAmt2.subtract(useAmt3));
						this.txStore(pro);
					}
				}
				
				bean.setAvailableAmt(bean.getTotalAmt().subtract(bean.getUsedAmt()));
				bean.setBpsNo((String)obj[7]);
				bean.setBpsName((String)obj[8]);
				bean.setAppName((String)obj[9]);
				bean.setSignBranchName((String)obj[10]);
				bean.setOpenDate((Date)obj[11]);
				bean.setChangeDate((Date)obj[12]);
				bean.setDueDate((Date)obj[13]);
				bean.setCreateDate((Date)obj[14]);
				bean.setUpdateTime((Date)obj[15]);
				bean.setPoolCreditRatio((BigDecimal)obj[16]!=null?(BigDecimal)obj[16]:new BigDecimal(0));
				bean.setChecked((String)obj[17]);
				bean.setSignBranchNo((String)obj[18]);
				bean.setAppNo((String) obj[21]);
				result.add(bean);
			}
			return result;
		}
		return list;
	}
	
	private String createCrdtSql(OnlineQueryBean queryBean) {
//		String sql =  "select dto.ID,'1' type,dto.ONLINE_CRDT_NO as onlineNo,dto.PROTOCOL_STATUS,dto.CUST_NAME,dto.ONLINE_LOAN_TOTAL as amt,info.usedAmt,dto.BPS_NO,dto.BPS_NAME,dto.APP_NAME,dto.SIGN_BRANCH_NAME,dto.OPEN_DATE,dto.CHANGE_DATE,dto.DUE_DATE,dto.CREATE_TIME,dto.UPDATE_TIME,dto.POOL_CREDIT_RATIO,dto.LD_FLAG as checked " +
//			" from PED_ONLINE_CRDT_PROTOCOL dto left join (select sum(batch.LOAN_AMT) usedAmt,ONLINE_CRDT_NO from PL_ONLINE_CRDT batch where batch.STATUS in('"+PublicStaticDefineTab.CRDT_BATCH_004+"') group by ONLINE_CRDT_NO) info on dto.ONLINE_CRDT_NO = info.ONLINE_CRDT_NO where 1=1 ";
		String sql =  "select dto.ID,'1' type,dto.ONLINE_CRDT_NO as onlineNo,dto.PROTOCOL_STATUS,dto.CUST_NAME,dto.ONLINE_LOAN_TOTAL as amt,dto.USED_AMT,dto.BPS_NO,dto.BPS_NAME,dto.APP_NAME,dto.SIGN_BRANCH_NAME,dto.OPEN_DATE,dto.CHANGE_DATE,dto.DUE_DATE,dto.CREATE_TIME,dto.UPDATE_TIME,dto.POOL_CREDIT_RATIO,dto.LD_FLAG as checked,dto.SIGN_BRANCH_NO,info.usedAmt,info.REPAY_AMT,dto.APP_NO as appNo " +
				" from PED_ONLINE_CRDT_PROTOCOL dto left join ( SELECT SUM(batch.LOAN_AMT) usedAmt,plan.REPAY_AMT  ,batch.ONLINE_CRDT_NO FROM PL_ONLINE_CRDT batch LEFT JOIN (SELECT sum( REPAY_AMT ) REPAY_AMT, ONLINE_CRDT_NO  FROM PL_CRDT_PAY_PLAN WHERE status NOT IN ( '012' ) GROUP BY ONLINE_CRDT_NO ) plan " +
				"ON plan.ONLINE_CRDT_NO = batch.ONLINE_CRDT_NO WHERE batch.Deal_Status NOT IN ( 'DS005' ) GROUP BY batch.ONLINE_CRDT_NO ,plan.REPAY_AMT ) info ON dto.ONLINE_CRDT_NO = info.ONLINE_CRDT_NO where 1=1 ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.CUST_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			sql += " and dto.APP_NAME like '%"+queryBean.getAppName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			sql += " and dto.SIGN_BRANCH_NAME like '%"+queryBean.getSignBranchName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_CRDT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.OPEN_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.OPEN_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getDueStartDate()) {
			sql +=" and dto.DUE_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getDueStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getDueEndDate()) {
			sql +=" and dto.DUE_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getDueEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getOpenDate()) {
			sql +=" and dto.OPEN_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getOpenDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
			sql +=" and dto.OPEN_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getOpenDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";

		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			sql += " and dto.PROTOCOL_STATUS ='"+queryBean.getProtocolStatus()+"'";
		}
		return sql;
	}

	private String createAcptPtlSql(OnlineQueryBean queryBean) {
		String sql = "select dto.ID,'0' as type,dto.ONLINE_ACPT_NO as onlineNo,dto.PROTOCOL_STATUS,dto.CUST_NAME,dto.ONLINE_ACPT_TOTAL as amt,dto.USED_AMT,dto.BPS_NO,dto.BPS_NAME,dto.APP_NAME,dto.SIGN_BRANCH_NAME,dto.OPEN_DATE,dto.CHANGE_DATE,dto.DUE_DATE,dto.CREATE_TIME,dto.UPDATE_TIME,dto.POOL_CREDIT_RATIO,dto.YC_FLAG as checked,dto.SIGN_BRANCH_NO,info.usedAmt,0 as REPAY_AMT,dto.APP_NO as appNo " +
				"from PED_ONLINE_ACPT_PROTOCOL dto left join (select sum(detail.BILL_AMT) usedAmt,ONLINE_ACPT_NO from PL_ONLINE_ACPT_DETAIL detail where detail.STATUS not in('012') group by ONLINE_ACPT_NO) info on dto.ONLINE_ACPT_NO = info.ONLINE_ACPT_NO where 1=1  ";
		
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.CUST_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			sql += " and dto.APP_NAME like '%"+queryBean.getAppName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			sql += " and dto.SIGN_BRANCH_NAME like '%"+queryBean.getSignBranchName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_ACPT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.OPEN_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.OPEN_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		
		if ( null!=queryBean.getDueStartDate()) {
			sql +=" and dto.DUE_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getDueStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getDueEndDate()) {
			sql +=" and dto.DUE_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getDueEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		
		if ( null!=queryBean.getOpenDate()) {
			sql +=" and dto.OPEN_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getOpenDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
			sql +=" and dto.OPEN_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getOpenDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";

		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			sql += " and dto.PROTOCOL_STATUS ='"+queryBean.getProtocolStatus()+"'";
		}
		return sql;
	}

	/**
	 * ??????????????????????????????
	 */
	public List queryOnlineAgreementHistList(OnlineQueryBean queryBean, User user, Page page) throws Exception {
		String sql ="select a.id,a.type,a.onlineNo,a.name,a.MODE_CONTENT,a.APP_NAME,a.APP_NO,a.SIGN_BRANCH_NAME,a.UPDATE_TIME,MODE_MARK,SIGN_BRANCH_NO  from (";
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineProtocolType())){
				if(PublicStaticDefineTab.PRODUCT_001.equals(queryBean.getOnlineProtocolType())){
					sql += this.createAcptHistSql(queryBean);
				}else{
					sql += this.createCrdtHistSql(queryBean);
				}
			}else{
				sql += this.createAcptHistSql(queryBean);
				sql += " union all ";
				sql += this.createCrdtHistSql(queryBean);
			}
		}else{
			sql += this.createAcptHistSql(queryBean);
			sql += " union all ";
			sql += this.createCrdtHistSql(queryBean);
		}
		sql += " )a where 1=1 ";
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//???????????????;??????????????????????????????????????????????????????  ?????????????????????
				
			}else if(str.equals("1") || str.equals("3")){//???????????????;?????????????????????????????????????????????????????? ?????????????????????????????????
					//?????????????????????????????????????????????????????????
				String hql = departmentService.queryDeptInnerbankcode(user.getDepartment().getInnerBankCode(), -1);
				if(StringUtils.isNotEmpty(hql)){
					sql = sql + (" and a.SIGN_BRANCH_NO in ("+hql+") ");
				}
			}else if(str.equals("4")){//????????????:?????????????????????????????????????????????????????????
				sql = sql + (" and a.APP_NAME = '"+user.getName()+"' ");
			}else if(str.equals("5")){//??????????????????:?????????
				return null;
			}
		}
		sql += "  order by a.UPDATE_TIME desc";
		List list = sessionDao.SQLQuery(sql,page);
		if(null != list && list.size()>0){
			List result = new ArrayList();
			for(int i=0;i<list.size();i++){
				Object[] obj= (Object[])list.get(i);
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setId((String)obj[0]);
				bean.setOnlineProtocolType(obj[1].toString());
				bean.setOnlineNo((String)obj[2]);
				bean.setCustName((String)obj[3]);
				bean.setModeContent((String)obj[4]);
				bean.setAppName((String)obj[5]);
				bean.setAppNo((String)obj[6]);
				bean.setSignBranchName((String)obj[7]);
				bean.setUpdateTime((Date)obj[8]);
				bean.setModeMark((String)obj[9]);
				result.add(bean);
			}
			return result;
		}
		return list;
	}

	private String createCrdtHistSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct id,'1' as type,ONLINE_CRDT_NO as onlineNo,CUST_NAME as name,MODE_CONTENT,APP_NAME,APP_NO,SIGN_BRANCH_NAME,UPDATE_TIME,MODE_MARK,SIGN_BRANCH_NO from PED_ONLINE_CRDT_PROTOCOL_HIST dto where 1=1 ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.CUST_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			sql += " and dto.APP_NAME like '%"+queryBean.getAppName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			sql += " and dto.SIGN_BRANCH_NAME like '%"+queryBean.getSignBranchName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_CRDT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.UPDATE_TIME>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.UPDATE_TIME<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getDueStartDate()) {
			sql +=" and dto.DUE_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getDueStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getDueEndDate()) {
			sql +=" and dto.DUE_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getDueEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			sql += " and dto.PROTOCOL_STATUS ='"+queryBean.getProtocolStatus()+"'";
		}
		if(null != queryBean.getStartAmt()){
			sql += " and dto.TOTAL_AMT >='"+queryBean.getStartAmt()+"'";
		}
		if(null != queryBean.getEndAmt()){
			sql += " and dto.TOTAL_AMT <='"+queryBean.getEndAmt()+"'";
		}
		return sql;
	}

	private String createAcptHistSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct id,'0' as type,ONLINE_ACPT_NO as onlineNo,CUST_NAME as name,MODE_CONTENT,APP_NAME,APP_NO,SIGN_BRANCH_NAME,UPDATE_TIME,MODE_MARK,SIGN_BRANCH_NO from PED_ONLINE_ACPT_PROTOCOL_HIST dto where 1=1 ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.CUST_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getAppName())){
			sql += " and dto.APP_NAME like '%"+queryBean.getAppName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getSignBranchName())){
			sql += " and dto.SIGN_BRANCH_NAME like '%"+queryBean.getSignBranchName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_ACPT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.UPDATE_TIME>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.UPDATE_TIME<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getDueStartDate()) {
			sql +=" and dto.DUE_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getDueStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getDueEndDate()) {
			sql +=" and dto.DUE_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getDueEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getProtocolStatus())){
			sql += " and dto.PROTOCOL_STATUS ='"+queryBean.getProtocolStatus()+"'";
		}
		if(null != queryBean.getStartAmt()){
			sql += " and dto.TOTAL_AMT >='"+queryBean.getStartAmt()+"'";
		}
		if(null != queryBean.getEndAmt()){
			sql += " and dto.TOTAL_AMT <='"+queryBean.getEndAmt()+"'";
		}
		return sql;
	}

	/**
	 * ????????????????????????
	 */
	public void txSaveOrUpdateBlackList(PedOnlineBlackInfo info) throws Exception {
		if(StringUtils.isNotBlank(info.getId())){//??????
			PedOnlineBlackInfo old = (PedOnlineBlackInfo) dao.load(PedOnlineBlackInfo.class,info.getId());
			old.setStatus(info.getStatus());
			if(null == info.getStartDate()){
				old.setStartDate(DateUtils.getCurrDate());
			}else{
				old.setStartDate(info.getStartDate());
			}
			String dueDate = this.calculateDueDate(info.getValidDate(), info.getDateType(),info.getStartDate());
			old.setEndtDate(DateUtils.parseDatStr2Date(dueDate, null));
			old.setDeprtId(info.getDeprtId());
			old.setDeprtName(info.getDeprtName());
			old.setValidDate(info.getValidDate());
			old.setDateType(info.getDateType());
			old.setUpdateTime(new Date());
			this.txStore(old);
		}else{//??????
			OnlineQueryBean bean = new OnlineQueryBean();
			bean.setCustNumber(info.getCustNo());
//			bean.setDeptId(info.getDeprtId());
			List list = this.queryBlackList(bean);
			if(null != list & list.size()>0){
				PedOnlineBlackInfo old = (PedOnlineBlackInfo) list.get(0);
				throw new Exception("??????????????????????????????,????????????"+old.getStatusDesc());
			}
			if(null == info.getStartDate()){
				info.setStartDate(DateUtils.getCurrDate());
			}
			String dueDate = this.calculateDueDate(info.getValidDate(), info.getDateType(),info.getStartDate());
			info.setEndtDate(DateUtils.parseDatStr2Date(dueDate, null));
			info.setCreateTime(new Date());
			info.setUpdateTime(new Date());
			this.txStore(info);
		}
	}

	/**
	 * ????????????????????????
	 * @throws Exception 
	 */
	public List queryOnlineBusiList(OnlineQueryBean queryBean, Page page) throws Exception {
		String sql ="select a.id,a.type,a.name,a.amt,a.CREATE_TIME,a.CONTRACT_NO,a.LOAN_NO,a.DEAL_STATUS  from (";
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getOnlineProtocolType())){
				if(PublicStaticDefineTab.PRODUCT_001.equals(queryBean.getOnlineProtocolType())){
					sql += this.createAcptBusiSql(queryBean);
				}else{
					sql += this.createCrdtBusiSql(queryBean);
				}
			}else{
				sql += this.createAcptBusiSql(queryBean);
				sql += " union all ";
				sql += this.createCrdtBusiSql(queryBean);
			}
		}else{
			sql += this.createAcptBusiSql(queryBean);
			sql += " union all ";
			sql += this.createCrdtBusiSql(queryBean);
		}
		sql += " )a order by a.create_time desc";
		List list = sessionDao.SQLQuery(sql,page);
		if(null != list && list.size()>0){
			List result = new ArrayList();
			for(int i=0;i<list.size();i++){
				Object[] obj= (Object[])list.get(i);
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setId((String)obj[0]);
				bean.setOnlineProtocolType(obj[1].toString());
				bean.setCustName((String)obj[2]);
				bean.setTotalAmt((BigDecimal)obj[3]);
				bean.setCreateTime((Date)obj[4]);
				bean.setContractNo((String)obj[5]);
				bean.setLoanNo((String)obj[6]);
				bean.setDealStatusDesc(DictionaryCache.getDealstatusmap((String)obj[7]));
				result.add(bean);
			}
			return result;
		}
		return list;
	}
	
	
	/**
	 * ??????????????????????????????
	 * @throws Exception 
	 */
	public List queryOnlineAcptList(OnlineQueryBean queryBean, User user, Page page) throws Exception {
		
		//??????
		if(StringUtils.isNotBlank(queryBean.getBusiId())){
			PlOnlineAcptDetail detail = (PlOnlineAcptDetail) pedOnlineAcptService.load(queryBean.getBusiId(),PlOnlineAcptDetail.class);
			if(null!=detail){
				queryBean.setAcptBatchId(detail.getAcptBatchId());
				queryBean.setBusiId(null);
			}
		}
		
		
		String sql ="select a.id,a.type,a.name,a.amt,a.CREATE_TIME,a.CONTRACT_NO,a.LOAN_NO,a.DEAL_STATUS ,'0',a.flag,a.ONLINE_ACPT_NO,a.APPLY_BANK_NO,a.APPLY_BANK_NAME,a.APPLY_ACCT,a.status,a.cacheType,a.BPS_NO,a.SIGN_BRANCH_NO,a.APP_NAME,a.DEPOSIT_RATIO,a.POOL_CREDIT_RATIO from (";
			if(null==queryBean.getType()){
				sql += this.createAcptSql(queryBean);
				sql += " union all ";
				sql += this.createAcptHisSql(queryBean);
			}else if(PublicStaticDefineTab.OPERATION_TYPE_01.equalsIgnoreCase(queryBean.getType())){
				sql += this.createAcptHisSql(queryBean);
			}else if(PublicStaticDefineTab.OPERATION_TYPE_02.equalsIgnoreCase(queryBean.getType())){
				sql += this.createAcptSql(queryBean);
		}
		sql += " ) a where 1=1";
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//???????????????;??????????????????????????????????????????????????????  ?????????????????????
				
			}else if(str.equals("1") || str.equals("3")){//???????????????;?????????????????????????????????????????????????????? ?????????????????????????????????
					//?????????????????????????????????????????????????????????
				String hql = departmentService.queryDeptInnerbankcode(user.getDepartment().getInnerBankCode(), -1);
				if(StringUtils.isNotEmpty(hql)){
					sql = sql + (" and a.SIGN_BRANCH_NO in ("+hql+") ");
				}
			}else if(str.equals("4")){//????????????:?????????????????????????????????????????????????????????
				sql = sql + (" and a.APP_NAME = '"+user.getName()+"' ");
			}else if(str.equals("5")){//??????????????????:?????????
				return null;
			}
		}
		sql += "  order by a.create_time desc";
		List list = sessionDao.SQLQuery(sql,page);
		if(null != list && list.size()>0){
			List result = new ArrayList();
			for(int i=0;i<list.size();i++){
				Object[] obj= (Object[])list.get(i);
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setId((String)obj[0]);
				bean.setOnlineProtocolType(obj[1].toString());
				bean.setCustName((String)obj[2]);
				bean.setTotalAmt((BigDecimal)obj[3]);
				bean.setCreateTime((Date)obj[4]);
				bean.setContractNo((String)obj[5]);
				bean.setLoanNo((String)obj[6]);
				String dealStatusDesc=DictionaryCache.getDealstatusmap((String)obj[7]);
				if(StringUtils.isBlank(dealStatusDesc)){
					dealStatusDesc=(String)obj[7];
				}
				bean.setDealStatusDesc(dealStatusDesc);
//				bean.setAcptBatchId((String)obj[8]);
				bean.setIsLocal(obj[9].toString());
				bean.setOnlineAcptNo(obj[10].toString());
				if(obj[11] != null){
					bean.setApplyBankNo(obj[11].toString());
				}
				if(obj[12] != null){
					bean.setApplyBankName(obj[12].toString());		
				}
				if(obj[13] != null){
					bean.setPayeeAcct(obj[13].toString());
				}
				String statusDesc=DictionaryCache.getAcptStatusMap((String)obj[14]);
				bean.setStatusDesc(statusDesc);
				bean.setType(obj[15].toString());
				if (obj[15].toString().equals("1")) {
					bean.setTypeName("??????");
				}else if (obj[15].toString().equals("0")) {
					bean.setTypeName("??????");
				}
				if(obj[16] != null){
					bean.setBpsNo(obj[16].toString());
				}
				if(obj[19] != null){
					bean.setDepositRatio((BigDecimal)obj[19]);
				}
				if(obj[20] != null){
					bean.setPoolCreditRatio((BigDecimal)obj[20]);
				}
				result.add(bean);
			}
			return result;
		}
		return list;
	}
	
	/**
	 * ??????????????????????????????
	 * @throws Exception 
	 */
	public List queryOnlineCrdtList(OnlineQueryBean queryBean, User user, Page page) throws Exception {
		String sql ="select a.id,a.type,a.name,a.amt,a.CREATE_TIME,a.CONTRACT_NO,a.LOAN_NO,a.DEAL_STATUS ,'0',a.flag,a.BPS_NO,a.CUST_NO,a.APPLY_DATE,a.DUE_DATE,a.ONLINE_CRDT_NO, a.cacheType,a.STATUS,a.TRANS_ACCOUNT,a.SIGN_BRANCH_NO,a.APP_NAME from (";
		if(null==queryBean.getType()){
			sql += this.createCrdtListSql(queryBean);
			sql += " union all ";
			sql += this.createCrdtHisSql(queryBean);
		}else if(PublicStaticDefineTab.OPERATION_TYPE_01.equalsIgnoreCase(queryBean.getType())){
			sql += this.createCrdtHisSql(queryBean);
		}else if(PublicStaticDefineTab.OPERATION_TYPE_02.equalsIgnoreCase(queryBean.getType())){
			sql += this.createCrdtListSql(queryBean);
		}
		sql += " )a where 1=1 ";
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//???????????????;??????????????????????????????????????????????????????  ?????????????????????
				
			}else if(str.equals("1") || str.equals("3")){//???????????????;?????????????????????????????????????????????????????? ?????????????????????????????????
					//?????????????????????????????????????????????????????????
				String hql = departmentService.queryDeptInnerbankcode(user.getDepartment().getInnerBankCode(), -1);
				if(StringUtils.isNotEmpty(hql)){
					sql = sql + (" and a.SIGN_BRANCH_NO in ("+hql+") ");
				}
			}else if(str.equals("4")){//????????????:?????????????????????????????????????????????????????????
				sql = sql + (" and a.APP_NAME = '"+user.getName()+"' ");
			}else if(str.equals("5")){//??????????????????:?????????
				return null;
			}
		}
		sql += " order by a.create_time desc";
		
		List list = sessionDao.SQLQuery(sql,page);
		if(null != list && list.size()>0){
			List<OnlineQueryBean> result = new ArrayList<OnlineQueryBean>();
			for(int i=0;i<list.size();i++){
				Object[] obj= (Object[])list.get(i);
				OnlineQueryBean bean = new OnlineQueryBean();
				bean.setId((String)obj[0]);
				bean.setOnlineProtocolType(obj[1].toString());
				bean.setCustName((String)obj[2]);
				bean.setTotalAmt((BigDecimal)obj[3]);
				bean.setCreateTime((Date)obj[4]);
				bean.setContractNo((String)obj[5]);
				bean.setLoanNo((String)obj[6]);
				String dealStatusDesc=DictionaryCache.getDealstatusmap((String)obj[7]);
				if(StringUtils.isBlank(dealStatusDesc)){
					dealStatusDesc=(String)obj[7];
				}
				bean.setDealStatusDesc(dealStatusDesc);
//				bean.setAcptBatchId((String)obj[8]);
				bean.setIsLocal(obj[9].toString());
				bean.setBpsNo(obj[10].toString());
				bean.setCustNo(obj[11].toString());
				bean.setStartDate((Date)obj[12]);
				bean.setEndDate((Date)obj[13]);
				bean.setOnlineCrdtNo(obj[14].toString());
				bean.setType(obj[15].toString());
				if(obj[15].toString().equals("1")){
					bean.setTypeName("??????");
				}else if (obj[15].toString().equals("0")){
					bean.setTypeName("??????");
				}
				String statusDesc=DictionaryCache.getCrdtStatuspMap((String)obj[16]);
				if(StringUtils.isBlank(statusDesc)){
					statusDesc=(String)obj[16];
				}
				bean.setStatusDesc(statusDesc);
				bean.setTransAccount("");
				if(null != obj[17]){
					bean.setTransAccount((String)obj[17]);
				}
				result.add(bean);
			}
			return result;
		}
		return list;
	}
	

	private String createCrdtBusiSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct id,'1' as type,CUST_NAME as name,LOAN_AMT as amt,CREATE_TIME,CONTRACT_NO,LOAN_NO,DEAL_STATUS from PL_ONLINE_CRDT dto where 1=1 ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.CUST_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_CRDT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if(null !=queryBean.getStartDate()){
			sql += " and dto.APPLY_DATE >='"+queryBean.getStartDate()+"'";
		}
		if(null !=queryBean.getEndDate()){
			sql += " and dto.APPLY_DATE <='"+queryBean.getEndDate()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getDealStatus())){
			sql += " and dto.DEAL_STATUS ='"+queryBean.getDealStatus()+"'";
		}
		if(null != queryBean.getTotalAmt()){
			sql += " and dto.LOAN_AMT ='"+queryBean.getTotalAmt()+"'";
		}
		return sql;
	}

	private String createAcptBusiSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct id,'0' as type,APPLY_NAME as name,TOTAL_AMT as amt,CREATE_TIME,CONTRACT_NO,'' as LOAN_NO,DEAL_STATUS from PL_ONLINE_ACPT_BATCH dto where 1=1 ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.APPLY_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			sql += " and dto.APPLY_ORGCODE ='"+queryBean.getCustOrgcode()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_ACPT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if(null !=queryBean.getStartDate()){
			sql += " and dto.APPLY_DATE >='"+queryBean.getStartDate()+"'";
		}
		if(null !=queryBean.getEndDate()){
			sql += " and dto.APPLY_DATE <='"+queryBean.getEndDate()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getDealStatus())){
			sql += " and dto.DEAL_STATUS ='"+queryBean.getDealStatus()+"'";
		}
		if(null != queryBean.getTotalAmt()){
			sql += " and dto.TOTAL_AMT ='"+queryBean.getTotalAmt()+"'";
		}
		return sql;
	}

	private String createAcptSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct dto.id,'0' as type,dto.APPLY_NAME as name,dto.TOTAL_AMT as amt,dto.CREATE_TIME,dto.CONTRACT_NO,'' as LOAN_NO,dto.DEAL_STATUS,'0','1' as flag ,dto.ONLINE_ACPT_NO,dto.APPLY_BANK_NO," +
				"dto.APPLY_BANK_NAME,dto.APPLY_ACCT,dto.STATUS, '0' as cacheType,dto.BPS_NO,pro.SIGN_BRANCH_NO,pro.APP_NAME,dto.DEPOSIT_RATIO,dto.POOL_CREDIT_RATIO from PL_ONLINE_ACPT_BATCH dto,PED_ONLINE_ACPT_PROTOCOL pro where dto.ONLINE_ACPT_NO=pro.ONLINE_ACPT_NO ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.APPLY_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			sql += " and dto.APPLY_ORGCODE ='"+queryBean.getCustOrgcode()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_ACPT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.APPLY_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.APPLY_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBusiId())){
			sql += " and dto.id ='"+queryBean.getBusiId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getAcptBatchId())){
			sql += " and dto.id ='"+queryBean.getAcptBatchId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getDealStatus())){
			sql += " and dto.DEAL_STATUS ='"+queryBean.getDealStatus()+"'";
		}
		if(null != queryBean.getTotalAmt()){
			sql += " and dto.TOTAL_AMT ='"+queryBean.getTotalAmt()+"'";
		}
		return sql;
	}
	private String createAcptHisSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct dto.id,'0' as type,dto.APPLY_NAME as name,dto.TOTAL_AMT as amt,dto.CREATE_TIME,dto.CONTRACT_NO,'' as LOAN_NO,dto.DEAL_STATUS,'0','2' as flag,dto.ONLINE_ACPT_NO,dto.APPLY_BANK_NO," +
				"dto.APPLY_BANK_NAME,dto.APPLY_ACCT,dto.STATUS,'1' as cacheType,dto.BPS_NO,pro.SIGN_BRANCH_NO,pro.APP_NAME,dto.DEPOSIT_RATIO,dto.POOL_CREDIT_RATIO from PL_ONLINE_ACPT_CACHE_BATCH dto,PED_ONLINE_ACPT_PROTOCOL pro where dto.ONLINE_ACPT_NO=pro.ONLINE_ACPT_NO";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.APPLY_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustOrgcode())){
			sql += " and dto.APPLY_ORGCODE ='"+queryBean.getCustOrgcode()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_ACPT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.APPLY_DATE>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.APPLY_DATE<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBusiId())){
			sql += " and dto.id ='"+queryBean.getBusiId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getAcptBatchId())){
			sql += " and dto.id ='"+queryBean.getAcptBatchId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getDealStatus())){
			sql += " and dto.DEAL_STATUS ='"+queryBean.getDealStatus()+"'";
		}
		if(null != queryBean.getTotalAmt()){
			sql += " and dto.TOTAL_AMT ='"+queryBean.getTotalAmt()+"'";
		}
		return sql;
	}
	
	private String createCrdtListSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct dto.id,'1' as type,dto.CUST_NAME as name,dto.LOAN_AMT as amt,dto.CREATE_TIME,dto.CONTRACT_NO,dto.LOAN_NO,dto.DEAL_STATUS,'0','1' as flag,dto.BPS_NO,dto.CUST_NO,dto.APPLY_DATE,dto.DUE_DATE,dto.ONLINE_CRDT_NO,'0' as cacheType,dto.STATUS,dto.TRANS_ACCOUNT,pro.SIGN_BRANCH_NO,pro.APP_NAME from PL_ONLINE_CRDT dto,PED_ONLINE_CRDT_PROTOCOL pro where pro.ONLINE_CRDT_NO= dto.ONLINE_CRDT_NO ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.CUST_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_CRDT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.UPDATE_TIME>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.UPDATE_TIME<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBusiId())){
			sql += " and dto.id ='"+queryBean.getBusiId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getDealStatus())){
			sql += " and dto.DEAL_STATUS ='"+queryBean.getDealStatus()+"'";
		}
		if(null != queryBean.getTotalAmt()){
			sql += " and dto.LOAN_AMT ='"+queryBean.getTotalAmt()+"'";
		}
		if(null != queryBean.getLoanNo()){
			sql += " and dto.LOAN_NO ='"+queryBean.getLoanNo()+"'";
		}
		return sql;
	}
	private String createCrdtHisSql(OnlineQueryBean queryBean) {
		String sql =  "select distinct dto.id,'1' as type,dto.CUST_NAME as name,dto.LOAN_AMT as amt,dto.CREATE_TIME,dto.CONTRACT_NO,dto.LOAN_NO,dto.DEAL_STATUS,'0','2' as flag,dto.BPS_NO,dto.CUST_NO,dto.APPLY_DATE,dto.DUE_DATE,dto.ONLINE_CRDT_NO ,'1' as cacheType,dto.STATUS,dto.TRANS_ACCOUNT,pro.SIGN_BRANCH_NO,pro.APP_NAME from PL_ONLINE_CACHE_CRDT dto,PED_ONLINE_CRDT_PROTOCOL pro where pro.ONLINE_CRDT_NO= dto.ONLINE_CRDT_NO ";
		if(StringUtils.isNotBlank(queryBean.getCustNumber())){
			sql += " and dto.CUST_NO ='"+queryBean.getCustNumber()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getCustName())){
			sql += " and dto.CUST_NAME like '%"+queryBean.getCustName()+"%'";
		}
		if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
			sql += " and dto.ONLINE_CRDT_NO ='"+queryBean.getOnlineNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBpsNo())){
			sql += " and dto.BPS_NO ='"+queryBean.getBpsNo()+"'";
		}
		if ( null!=queryBean.getStartDate()) {
			sql +=" and dto.UPDATE_TIME>=TO_DATE('"+DateUtils.toString(queryBean.getStartDate(), "yyyy-MM-dd") + " 00:00:00"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if ( null!=queryBean.getEndDate()) {
			sql +=" and dto.UPDATE_TIME<=TO_DATE('"+DateUtils.toString(queryBean.getEndDate(), "yyyy-MM-dd") + " 23:59:59"+"', 'yyyy-mm-dd hh24:mi:ss')";
		}
		if(StringUtils.isNotBlank(queryBean.getContractNo())){
			sql += " and dto.CONTRACT_NO ='"+queryBean.getContractNo()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getId())){
			sql += " and dto.id ='"+queryBean.getId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getBusiId())){
			sql += " and dto.id ='"+queryBean.getBusiId()+"'";
		}
		if(StringUtils.isNotBlank(queryBean.getDealStatus())){
			sql += " and dto.DEAL_STATUS ='"+queryBean.getDealStatus()+"'";
		}
		if(null != queryBean.getTotalAmt()){
			sql += " and dto.LOAN_AMT ='"+queryBean.getTotalAmt()+"'";
		}
		if(null != queryBean.getLoanNo()){
			sql += " and dto.LOAN_NO ='"+queryBean.getLoanNo()+"'";
		}
		return sql;
	}
	
	@Override
	public List queryErrorLogList(OnlineQueryBean queryBean, Page page) {
		String sql ="select dto from PedOnlineHandleLog dto where 1=1";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getBusiId())){
				sql = sql +" dto.busiId = ?";
				param.add(queryBean.getBusiId());
			}
			if(StringUtils.isNotBlank(queryBean.getBillNo())){
				sql = sql +" dto.billNo = ?";
				param.add(queryBean.getBillNo());
			}
			if(StringUtils.isNotBlank(queryBean.getBpsNo())){
				sql = sql +" dto.bpsNo = ?";
				param.add(queryBean.getBpsNo());
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
				sql = sql +" dto.bpsNo = ?";
				param.add(queryBean.getOnlineNo());
			}
		}
		sql = sql +" order by dto.createTime desc";
		List result = null;
		if(null != page){
			result = this.find(sql, param,page);
		}else{
			result = this.find(sql, param);
		}
		return result;
	}
	
	@Override
	public String queryHandleLog(OnlineQueryBean queryBean) {
		String sql ="select dto.tradeResult from PedOnlineHandleLog dto where 1=1";
		List param = new ArrayList();
		if(null != queryBean){
			if(StringUtils.isNotBlank(queryBean.getBusiId())){
				sql = sql +" and dto.busiId = ?";
				param.add(queryBean.getBusiId());
			}
			else{
				return null;
			}
			if(StringUtils.isNotBlank(queryBean.getBillNo())){
				sql = sql +" and dto.billNo = ?";
				param.add(queryBean.getBillNo());
			}
			if(StringUtils.isNotBlank(queryBean.getBpsNo())){
				sql = sql +" and dto.bpsNo = ?";
				param.add(queryBean.getBpsNo());
			}
			if(StringUtils.isNotBlank(queryBean.getOnlineNo())){
				sql = sql +" and dto.bpsNo = ?";
				param.add(queryBean.getOnlineNo());
			}
		}
		//sql = sql +" order by dto.createTime desc";
		List result = this.find(sql, param);
		if(null != result && result.size()>0){
			String json = "";
			for(int i=0;i<result.size();i++){
				json = json+(String) result.get(i);
			}
			return json;
		}else{
			return "????????????";
		}
	}

	@Override
	public List queryOnlineMsgHist(OnlineQueryBean bean, Page page) {
		String sql ="select dto from PedOnlineMsgInfoHist dto where 1=1";
		List param = new ArrayList();
		if(null != bean){
			if(StringUtils.isNotBlank(bean.getLastSourceId())){
				sql = sql +" and dto.lastSourceId=?";
				param.add(bean.getLastSourceId());
			}
			if(StringUtils.isNotBlank(bean.getOnlineProtocolId())){
				sql = sql +" and dto.onlineProtocolId=?";
				param.add(bean.getOnlineProtocolId());
			}
			if(StringUtils.isNotBlank(bean.getOnlineNo())){
				sql = sql +" and dto.onlineNo=?";
				param.add(bean.getOnlineNo());
			}
			if(StringUtils.isNotBlank(bean.getAddresseeNo())){
				sql = sql +" and dto.addresseeNo=?";
				param.add(bean.getAddresseeNo());
			}
			if(StringUtils.isNotBlank(bean.getAddresseeRole())){
				sql = sql +" and dto.addresseeRole=?";
				param.add(bean.getAddresseeRole());
			}
			if(StringUtils.isNotBlank(bean.getModeMark())){
				sql = sql +" and dto.modeMark=?";
				param.add(bean.getModeMark());
			}
			if(StringUtils.isNotBlank(bean.getAddresseeName())){
				sql = sql +" and dto.addresseeName like(?)";
				param.add("%"+bean.getAddresseeName()+"%");
			}
		}
		sql =sql+ " order by dto.updateTime desc";
		List result = this.find(sql, param);
		return result;
	}
	
	/**
	 * ???????????????
	 */
	public String calculateDueDate(String validDate, String validDateType,Date startDate) {
		Date curDate = DateUtils.getCurrDate();
		if(null != startDate){
			curDate = startDate;
		}
		Date dueDate = null;
		String dueStr="";
		if(PublicStaticDefineTab.YEAR_FLAG.equals(validDateType)){
			dueDate = DateUtils.getNextNYear(curDate, Integer.parseInt(validDate));
		}else if(PublicStaticDefineTab.MONTH_FLAG.equals(validDateType)){
			dueDate = DateUtils.getNextNMonth(curDate, Integer.parseInt(validDate));
		}else if(PublicStaticDefineTab.DAY_FLAG.equals(validDateType)){
			dueDate = DateUtils.adjustDateByDay(curDate, Integer.parseInt(validDate));
		}
		if(null != dueDate){
			dueStr = DateUtils.toString(dueDate, DateUtils.ORA_DATES_FORMAT);
		}
		return dueStr;
	}

	/**
	 * ????????????????????????
	 */
	public Ret checkOnlineSwitch(String branchNo,String busiType) throws Exception {
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		String msg="";
		String error="";
		Department dept = departmentService.queryByInnerBankCode(branchNo);
		if(null != dept){
			if(!"1".equals(dept.getYcFlag()) && PublicStaticDefineTab.PRODUCT_001.equals(busiType)){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				msg = msg +"????????????????????????????????????!|";
			}else if(!"1".equals(dept.getLdFlag()) && PublicStaticDefineTab.PRODUCT_002.equals(busiType)){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				msg = msg +"????????????????????????????????????!|";
			}
		}else{
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("??????????????????????????????????????????????????????");
			return ret;
		}
		
		String olOpenPjc    = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_OPEN_PJC    );//????????????????????? 
		
		String olOpenYc     = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_OPEN_YC     );//??????????????????   
		String olOpentimeYc = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_OPENTIME_YC );//????????????????????????
		String olEndtimeYc  = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_ENDTIME_YC  );//????????????????????????

		String olOpenId     = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_OPEN_LD     );//??????????????????   
		String olOpentimeId = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_OPENTIME_LD );//????????????????????????
		String olEndtimeId  = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_ENDTIME_LD  );//????????????????????????
		
		
		if("0".equals(olOpenPjc)){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			error = error +"???????????????????????????!|";
		}

		
		if(PublicStaticDefineTab.PRODUCT_001.equals(busiType)){
			if("0".equals(olOpenYc)){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				error = error +"???????????????????????????!|";
			}

			if((DateUtils.formatDate(DateUtils.getCurrDateTime(),DateUtils.ORA_TIME2_FORMAT)).before(DateUtils.parseDatStr2Date(olOpentimeYc,DateUtils.ORA_TIME2_FORMAT))){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				msg = msg +"????????????????????????!|";
			}

			if(DateUtils.formatDate(DateUtils.getCurrDateTime(),DateUtils.ORA_TIME2_FORMAT).after(DateUtils.parseDatStr2Date(olEndtimeYc, DateUtils.ORA_TIME2_FORMAT))){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				msg = msg +"????????????????????????!|";
			}

		}else{
			
			if("0".equals(olOpenId)){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				error = error +"???????????????????????????!|";
			}

			if((DateUtils.formatDate(DateUtils.getCurrDateTime(),DateUtils.ORA_TIME2_FORMAT)).before(DateUtils.parseDatStr2Date(olOpentimeId,DateUtils.ORA_TIME2_FORMAT))){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				msg = msg +"????????????????????????!|";
			}

			if(DateUtils.formatDate(DateUtils.getCurrDateTime(),DateUtils.ORA_TIME2_FORMAT).after(DateUtils.parseDatStr2Date(olEndtimeId, DateUtils.ORA_TIME2_FORMAT))){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				msg = msg +"????????????????????????!|";
			}
		}
		ret.setRET_MSG(msg);
		ret.setError_MSG(error);
		return ret;
	}

	@Override
	public TMessageRecord toSendMsgForNotifier(String role, String phoneNo,
			String busiType, String Template,String addresseeName,String onlineNo) throws Exception {
	
		TMessageRecord msg = new TMessageRecord();
		msg.setBusiType(busiType);
		msg.setMsgContent(Template);
		msg.setPhoneNo(phoneNo);
		msg.setRole(role);
		msg.setCreateTime(new Date());
		msg.setSendResult("0");
		msg.setOnlineNo(onlineNo);
		msg.setAddresseeName(addresseeName);
		List<TMessageRecord> list = new ArrayList<TMessageRecord>();
		list.add(msg);
		financialAdviceService.txCreateList(list);
		this.txStore(msg);
		//??????????????????
	    Map<String, String> reqParams =new HashMap<String,String>();
		reqParams.put("busiId", msg.getId());
		reqParams.put("msg", Template);
		autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOL_MSS_TASK_NO, msg.getId(), AutoTaskNoDefine.BUSI_TYPE_MSS, reqParams,phoneNo,phoneNo,null,null);
		return msg;
	}

	@Override
	public String toQueryCustorForCore(String custNo) throws Exception {
		CoreTransNotes transNotes = new CoreTransNotes();
		transNotes.setCustIdA(custNo);
		List list = new ArrayList();
		ReturnMessageNew response = poolCoreService.PJH854111Handler(transNotes);
		if (response.isTxSuccess()) {
			Map map = response.getBody();
			PedProtocolDto pedProtocolDto = new PedProtocolDto();
			if(map.get("CLIENT_NAME")!=null){				
				String clientName = (String) map.get("CLIENT_NAME");
//				String orgcode = (String) map.get("GLOBAL_ID");//??????????????????18???  91420100300248067P?????????9???17???
//				if(StringUtils.isNotEmpty(orgcode)){
//					pedProtocolDto.setCustOrgcode(orgcode.substring(8, 16));
//				}
				pedProtocolDto.setCustname(clientName.trim());// ????????????
				return JsonUtil.fromObject(pedProtocolDto);
			}
		}
		return "2";
	}

	@Override
	public List findOnlineListExpt(List res, Page page)
			throws Exception {
		
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[15];
				OnlineQueryBean bean = (OnlineQueryBean) res.get(i);
				s[0] = bean.getOnlineNo();
				s[1] = bean.getOnlineProtocolTypeDesc();
				s[2] = bean.getStatusDesc();
				s[3] = bean.getCustName();
				s[4] = String.valueOf(bean.getTotalAmt());
				s[5] = String.valueOf(bean.getUsedAmt());
				s[6] = String.valueOf(bean.getAvailableAmt());
				s[7] = bean.getBpsNo();
				s[8] = bean.getBpsName();
				s[9] = bean.getAppName();
				s[10] = String.valueOf(bean.getPoolCreditRatio());
				s[11] = bean.getSignBranchName();
				
				if (bean.getDueDate() != null) {
					s[12] = String.valueOf(bean.getDueDate()).substring(0, 10);
				}else{
					s[12] = "";
				}
				if (bean.getOpenDate() != null) {
					s[13] = String.valueOf(bean.getOpenDate()).substring(0, 10);
				}else{
					s[13] = "";
				}
				if (bean.getChangeDate() != null) {
					s[14] = String.valueOf(bean.getChangeDate()).substring(0, 10);
				}else{
					s[14] = "";
				}
				
				list.add(s);
			}
		}
		return list;
	}

	@Override
	public List findOnlineListHistExpt(List res, Page page) throws Exception {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[7];
				OnlineQueryBean bean = (OnlineQueryBean) res.get(i);
				s[0] = bean.getOnlineNo();
				s[1] = bean.getCustName();
				s[2] = bean.getOnlineProtocolTypeDesc();
				s[3] = bean.getModeContent();
				s[4] = bean.getAppName();
				s[5] = bean.getAppNo();
				s[6] = bean.getSignBranchName();
				
				list.add(s);
			}
		}
		return list;
	}

	@Override
	public List findOnlineCrdtListExpt(List res, Page page) throws Exception {

		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[15];
				OnlineQueryBean bean = (OnlineQueryBean) res.get(i);
				s[0] = bean.getCustName();
				s[1] = bean.getOnlineCrdtNo();
				s[2] = bean.getOnlineProtocolTypeDesc();
				s[3] = String.valueOf(bean.getTotalAmt());
				s[4] = bean.getBpsNo();
				s[5] = bean.getCustNo();
				if (bean.getStartDate() != null) {
					s[6] = String.valueOf(bean.getStartDate()).substring(0, 10);
				}else{
					s[6] = "";
				}
				if (bean.getEndDate() != null) {
					s[7] = String.valueOf(bean.getEndDate()).substring(0, 10);
				}else{
					s[7] = "";
				}
				s[8] = bean.getContractNo();
				s[9] = bean.getLoanNo();
				s[10] = bean.getTransAccount();
				if(bean.getType().equals("0")){
					s[11] = "??????";
				}else{
					s[11] = "??????";
				}
				s[12] = bean.getDealStatusDesc();
				
				s[13] = bean.getStatusDesc();
				if (bean.getCreateTime() != null) {
					s[14] = String.valueOf(bean.getCreateTime()).substring(0, 10);
				}else{
					s[14] = "";
				}
				
				list.add(s);
			}
		}
		return list;
	}

	@Override
	public List findOnlineAcptListExpt(OnlineQueryBean queryBean, User user, Page page) throws Exception {
		List list = this.queryOnlineAcptList(queryBean,user,page);
		List result = new ArrayList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String[] s = new String[15];
				OnlineQueryBean bean = (OnlineQueryBean) list.get(i);
				s[0] = bean.getCustName();
				s[1] = bean.getOnlineProtocolTypeDesc();
				s[2] = bean.getApplyBankNo();
				s[3] = bean.getApplyBankName();
				s[4] = bean.getPayeeAcct();
				s[5] = String.valueOf(bean.getTotalAmt());
				s[6] = bean.getBpsNo();
				s[7] = bean.getOnlineAcptNo();
				s[8] = bean.getContractNo();
				s[9] = String.valueOf(bean.getDepositRatio());
				s[10] = String.valueOf(bean.getPoolCreditRatio());
				s[11] = bean.getType();
				s[12] = bean.getDealStatusDesc();
				s[13] = bean.getStatusDesc();
				if (bean.getCreateTime() != null) {
					s[14] = String.valueOf(bean.getCreateTime()).substring(0, 10);
				}else{
					s[14] = "";
				}
				result.add(s);
			}
		}
		return result;
	}

}
