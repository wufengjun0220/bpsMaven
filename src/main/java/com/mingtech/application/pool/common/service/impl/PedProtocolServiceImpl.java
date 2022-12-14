package com.mingtech.application.pool.common.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.domain.CollectionSendDto;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetFactory;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.MarginAcctChangeHist;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolHist;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PedProtocolListHist;
import com.mingtech.application.pool.common.domain.PedProtocolModDto;
import com.mingtech.application.pool.common.domain.PlFeeScale;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.edu.domain.PedCheckList;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.runmanage.domain.RunState;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.application.sysmanage.service.UserService;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("pedProtocolService")
public class PedProtocolServiceImpl extends GenericServiceImpl implements
		PedProtocolService {
	private static final Logger logger = Logger.getLogger(PedProtocolServiceImpl.class);
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private PoolCoreService poolCoreService;
	@Autowired
	private PoolBailEduService poolBailEduService ;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	
	
	public Class getEntityClass() {
		return PedProtocolDto.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}

	/**
	 * ??????????????????serviceImpl
	 */
	public void txCommitPedpro(PedProtocolDto pedProtocolDto) throws Exception {
		pedProtocolDto = (PedProtocolDto) this.load(pedProtocolDto
				.getPoolInfoId());
		// ?????????????????????????????????
		pedProtocolDto.setApproveFlag(PoolComm.APPROVE_01);//???????????????
		pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
		pedProtocolDto.setContractType(PoolComm.BT_08);
		this.txStore(pedProtocolDto);
	}

	/**
	 * ??????????????????serviceImpl
	 */
	public void txCommitPedproFor(PedProtocolDto pedProtocolDto) throws Exception {
		pedProtocolDto = (PedProtocolDto) this.load(pedProtocolDto
				.getPoolInfoId());
		if(null != pedProtocolDto){
			if(PoolComm.FROZEN_STATUS_01.equals(pedProtocolDto.getFrozenstate()) || PoolComm.FROZEN_STATUS_02.equals(pedProtocolDto.getFrozenstate()) || PoolComm.FROZEN_STATUS_03.equals(pedProtocolDto.getFrozenstate()) ){
				pedProtocolDto.setContractType(PoolComm.BT_07);//????????????
			}else if(PoolComm.FROZEN_STATUS_00.equals(pedProtocolDto.getFrozenstate())){//?????????????????????????????????
				pedProtocolDto.setContractType(PoolComm.BT_06);//????????????
			}
		}
		this.txStore(pedProtocolDto);
	}

	// ?????????????????????????????? ??????
	public void txSaveApproveOpclose(PedProtocolDto pedProtocolDto,PedProtocolModDto newPed)
			throws Exception {
		
		String approveFlag = pedProtocolDto.getApproveFlag();
		if(PoolComm.APPROVE_01.equals(approveFlag)){//???????????????
			//???????????????????????????  start
			pedProtocolDto.setMarginAccount(newPed.getMarginAccount());
			pedProtocolDto.setCustnumber(newPed.getCustnumber());
			pedProtocolDto.setCustname(newPed.getCustname());
			pedProtocolDto.setZyflag(newPed.getZyflag());
			pedProtocolDto.setIsMarginGroup(newPed.getIsMarginGroup());
			pedProtocolDto.setPoolAccount(newPed.getPoolAccount());
			pedProtocolDto.setPoolAccountName(newPed.getPoolAccountName());
			pedProtocolDto.setOperatorName1(newPed.getOperatorName1());
			pedProtocolDto.setMarginAccountName(newPed.getMarginAccountName());
			pedProtocolDto.setCustOrgcode(newPed.getCustOrgcode());
//			String officeNet = newPed.getOfficeNet();
			pedProtocolDto.setOfficeNet(newPed.getOfficeNet());
			pedProtocolDto.setOfficeNetName(newPed.getOfficeNetName());
			pedProtocolDto.setAccountManager(newPed.getAccountManager());
			pedProtocolDto.setAccountManagerId(newPed.getAccountManagerId());
			
			User user = userService.getUserByLoginName(newPed.getAccountManagerId());//????????????ID
			if(null != user){    				
				Department dept =  (Department)userService.load(user.getDeptId(),Department.class);
				String deptNo = dept.getInnerBankCode();
				String deptName = dept.getName();
				pedProtocolDto.setOfficeNet(deptNo);
				pedProtocolDto.setOfficeNetName(deptName);
				
			}
			pedProtocolDto.setOperateTime(new Date());//????????????
//			ped.setApproveFlag(PoolComm.APPROVE_01);//???????????????
			pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
			//??????????????????   end
			pedProtocolDto.setOpenFlag(PoolComm.OPEN_01);//?????????
			pedProtocolDto.setApproveFlag(PoolComm.APPROVE_03);//??????????????????
			pedProtocolDto.setContractType(PoolComm.BT_08);//??????????????????
			//??????
			newPed.setOpenFlag(PoolComm.OPEN_01);//?????????
			newPed.setApproveFlag(PoolComm.APPROVE_03);//??????????????????
			newPed.setContractType(PoolComm.BT_08);//??????????????????
		}
		if(PoolComm.APPROVE_04.equals(approveFlag)){//???????????????
			pedProtocolDto.setOpenFlag(PoolComm.OPEN_02);//?????????
			pedProtocolDto.setApproveFlag(PoolComm.APPROVE_06);//??????????????????
			pedProtocolDto.setContractType(PoolComm.BT_08);//??????????????????
			////??????
			newPed.setOpenFlag(PoolComm.OPEN_02);//?????????
			newPed.setApproveFlag(PoolComm.APPROVE_06);//??????????????????
			newPed.setContractType(PoolComm.BT_08);//??????????????????
		}
		pedProtocolDto.setOperateTime(new Date());
		newPed.setOperateTime(new Date());
		this.txStore(pedProtocolDto);
		this.txStore(newPed);
	}

	
	/**
	 * json???
	 */
	public String loadProolJSON(PedProtocolDto pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception {
		
		List list = loadProolList(pedProtocolDto, queryBean, user, page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	public String loadProolJSONQuery(PedProtocolDto pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception {

		List list = loadProolListQuery(pedProtocolDto, queryBean, user, page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	/**
	 * ??????,??????????????? ???????????????????????????????????????????????????
	 * 
	 * @param pedProtocolDto
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadProolListQuery(PedProtocolDto pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select ppd from PedProtocolDto ppd where 1=1 ");
		List valueList = new ArrayList(); // ?????????????????????
		List keyList = new ArrayList(); // ????????????????????????
		
		// ????????????????????????
			if (user != null && user.getDepartment() != null) {
				// ?????????????????????????????????????????????????????????????????????????????????????????????
				if (!PublicStaticDefineTab.isRootDepartment(user)) {
					List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:officeNetList) ");
					keyList.add("officeNetList");
					valueList.add(resultList);
				}else 
					if(StringUtils.isNotBlank(pedProtocolDto.getOfficeNet())){//????????????????????????????????????
						Department dept=departmentService.getDeptById(pedProtocolDto.getOfficeNet());
						List resultList = departmentService.getAllChildrenInnerCodeList(dept.getInnerBankCode(), -1);
						sb.append(" and ppd.officeNet in (:officeNetList) ");
						keyList.add("officeNetList");
						valueList.add(resultList);
					}
			}
		
		if (pedProtocolDto != null) {
			if (StringUtils.isNotBlank(pedProtocolDto.getPoolAgreement())) {
				sb.append(" and ppd.poolAgreement like :poolAgreement ");
				keyList.add("poolAgreement");
				valueList.add("%" + pedProtocolDto.getPoolAgreement() + "%");
			}
			
			if (StringUtils.isNotBlank(pedProtocolDto.getCustnumber())) {
				sb.append(" and ppd.custnumber like :custnumber ");
				keyList.add("custnumber");
				valueList.add("%" + pedProtocolDto.getCustnumber() + "%");
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getCustname())) {
				sb.append(" and ppd.custname like :custname ");
				keyList.add("custname");
				valueList.add("%" + pedProtocolDto.getCustname() + "%");
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getOpenFlag())) {
				sb.append(" and ppd.openFlag = :openFlag ");
				keyList.add("openFlag");
				valueList.add(pedProtocolDto.getOpenFlag());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getApproveFlag())) {
				sb.append(" and ppd.approveFlag = :approveFlag ");
				keyList.add("approveFlag");
				valueList.add(pedProtocolDto.getApproveFlag());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getCustOrgcode())) {
				sb.append(" and ppd.custOrgcode = :custOrgcode ");
				keyList.add("custOrgcode");
				valueList.add(pedProtocolDto.getCustOrgcode());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getIsGroup())) {
				sb.append(" and ppd.isGroup = :isGroup ");
				keyList.add("isGroup");
				valueList.add(pedProtocolDto.getIsGroup());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getvStatus())) {
				sb.append(" and ppd.vStatus = :vStatus ");
				keyList.add("vStatus");
				valueList.add(pedProtocolDto.getvStatus());
			}
			if(StringUtils.isNotBlank(pedProtocolDto.getPoolName())){
				sb.append(" and ppd.poolName like :poolName ");
				keyList.add("poolName");
				valueList.add("%"+pedProtocolDto.getPoolName()+"%");
			}
			// ?????????????????????????????????
			if ( null!=pedProtocolDto.getEffstartdate()) {
				sb.append(" and ppd.effstartdate>=:effstartdate ");
				keyList.add("effstartdate");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getEffstartdate()));
			}
			// ?????????????????????????????????
			if ( null!=pedProtocolDto.getEffenddate()) {
				sb.append(" and ppd.effstartdate<=:effstartdate1 ");
				keyList.add("effstartdate1");
				valueList.add(DateUtils.getCurrentDayEndDate(pedProtocolDto.getEffenddate()));
			}
			
		}
		sb.append(" order by ppd.poolAgreement desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // ???????????????
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
	}
	
	public RunState getSysRunState(){
		List list = this.find("select rs from RunState as rs", 1, 1);
		if (list.size()>0) {
			return (RunState)list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * ??????,???????????????
	 * 
	 * @param pedProtocolDto
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadProolList(PedProtocolDto pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select ppd from PedProtocolDto ppd where 1=1 ");
		List valueList = new ArrayList(); // ?????????????????????
		List keyList = new ArrayList(); // ????????????????????????
		if (pedProtocolDto != null) {
			if (StringUtils.isNotBlank(pedProtocolDto.getPoolAgreement())) {
				sb.append(" and ppd.poolAgreement like :poolAgreement ");
				keyList.add("poolAgreement");
				valueList.add("%" + pedProtocolDto.getPoolAgreement() + "%");
			}
			
			if (StringUtils.isNotBlank(pedProtocolDto.getCustnumber())) {
				sb.append(" and ppd.custnumber like :custnumber ");
				keyList.add("custnumber");
				valueList.add("%" + pedProtocolDto.getCustnumber() + "%");
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getCustname())) {
				sb.append(" and ppd.custname like :custname ");
				keyList.add("custname");
				valueList.add("%" + pedProtocolDto.getCustname() + "%");
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getOpenFlag())) {
				sb.append(" and ppd.openFlag = :openFlag ");
				keyList.add("openFlag");
				valueList.add(pedProtocolDto.getOpenFlag());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getApproveFlag())) {
				sb.append(" and ppd.approveFlag = :approveFlag ");
				keyList.add("approveFlag");
				valueList.add(pedProtocolDto.getApproveFlag());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getCustOrgcode())) {
				sb.append(" and ppd.custOrgcode like :custOrgcode ");
				keyList.add("custOrgcode");
				valueList.add("%" +pedProtocolDto.getCustOrgcode() + "%");
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getIsGroup())) {
				sb.append(" and ppd.isGroup = :isGroup ");
				keyList.add("isGroup");
				valueList.add(pedProtocolDto.getIsGroup());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getvStatus())) {
				sb.append(" and ppd.vStatus = :vStatus ");
				keyList.add("vStatus");
				valueList.add(pedProtocolDto.getvStatus());
			}
			// ????????????????????????
			if(user != null && user.getDepartment() != null) {
				// ?????????????????????????????????????????????????????????????????????????????????????????????
				if(!PublicStaticDefineTab.isRootDepartment(user)) {
					List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:officeNetList) ");
					keyList.add("officeNetList");
					valueList.add(resultList);
				}else if(StringUtils.isNotBlank(pedProtocolDto.getOfficeNet())){//????????????????????????????????????
					Department dept=departmentService.getDeptById(pedProtocolDto.getOfficeNet());
					List resultList = departmentService.getAllChildrenInnerCodeList(dept.getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:officeNetList) ");
					keyList.add("officeNetList");
					valueList.add(resultList);
				}
			}
			// ?????????????????????????????????
			if ( null!=pedProtocolDto.getEffstartdate()) {
				sb.append(" and ppd.effstartdate>=:effstartdate ");
				keyList.add("effstartdate");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getEffstartdate()));
			}
			// ?????????????????????????????????
			if ( null!=pedProtocolDto.getEffenddate()) {
				sb.append(" and ppd.effstartdate<=:effstartdate1 ");
				keyList.add("effstartdate1");
				valueList.add(DateUtils.getCurrentDayEndDate(pedProtocolDto.getEffenddate()));
			}
		}
		sb.append(" order by ppd.poolAgreement desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // ???????????????
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
	}
	
	/**
	 * ??????????????? ???????????? ???????????????
	 * 
	 * @param
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List queryBatchForAudit(List prdList, PedProtocolDto ppd, User user,
			Page page) throws Exception {

		StringBuffer sql = new StringBuffer();
		List paramName = new ArrayList();
		List paramValue = new ArrayList();
		List retList = new ArrayList();

		if (page == null) {
			page = new Page();
			page.setPageSize(1000);
		}

		sql.append(" select  ppd from PedProtocolDto ppd  where contractType ='"
				+ PoolComm.BT_07 + "' or contractType = '"+PoolComm.BT_06 +"'");
		List returnList = this.find(" distinct ppd.poolInfoId ",
				sql.toString(),
				(String[]) paramName.toArray(new String[paramName.size()]),
				paramValue.toArray(), page);
	
		StringBuffer sql2 = new StringBuffer();
		sql2.append("select  ppd from PedProtocolModDto ppd  where approveFlag ='"
				+ PoolComm.APPROVE_01 + "' or approveFlag = '"+PoolComm.APPROVE_04 +"'");
		List lsit2 = this.find(" distinct ppd.poolInfoId ",
				sql2.toString(),
				(String[]) paramName.toArray(new String[paramName.size()]),
				paramValue.toArray(), page);
		retList.addAll(returnList);
		retList.addAll(lsit2);
		return retList;
	}

	@Override
	public PedProtocolModDto queryModProtocolByCode(String orgcode) throws Exception {
			String hql = "from PedProtocolModDto ppd where ppd.custnumber ='"
				+ orgcode+"'";
			List find = this.find(hql);
			PedProtocolModDto newPed = null;
			if (find != null && find.size() > 0) {
				newPed = (PedProtocolModDto) find.get(0);
			}
		return newPed;
		}
	@Override
	public PedProtocolModDto queryModProtocolById(String poolInfoId) throws Exception {
			String hql = "from PedProtocolModDto ppd where ppd.poolInfoId ='"
				+ poolInfoId+"'";
			List find = this.find(hql);
			PedProtocolModDto newPed = null;
			if (find != null && find.size() > 0) {
				newPed = (PedProtocolModDto) find.get(0);
			}
		return newPed;
		}
	@Override
	public Department queryDertByNumer(String officeNet){
			String hql = "from Department ppd where ppd.bankNumber ='"
				+ officeNet+"'";
			List find = this.find(hql);
			Department depart = null;
			if (find != null && find.size() > 0) {
				depart = (Department) find.get(0);
			}
		return depart;
	}

	@Override
	public Department queryDertById(String id){
			String hql = "from Department ppd where ppd.id ='"
				+ id+"'";
			List find = this.find(hql);
			Department depart = null;
			if (find != null && find.size() > 0) {
				depart = (Department) find.get(0);
			}
		return depart;
	}

	@Override
	public void createAssetPoolInfo(PedProtocolDto pedProtocolDto) throws Exception{
		
		String custNo = pedProtocolDto.getCustnumber();
		
		/*???????????????????????????????????????????????????????????????*/
		AssetPool poolOld = pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(pedProtocolDto.getPoolAgreement(),null, custNo);
		if(poolOld!=null){
			AssetType typeOld = null;
			logger.info("??????AssetPool,AssetPool?????????:"+poolOld.getApId()+"??????????????????:"+pedProtocolDto.getPoolAgreement());
			pedAssetPoolService.txDelete(poolOld);
			List<AssetType> typeList= pedAssetTypeService.queryPedAssetTypeByAssetPool(poolOld);	
			if(typeList!=null&&typeList.size()>0){
				for(int i=0;i<typeList.size();i++){
					typeOld = typeList.get(i)  ;
					pedAssetTypeService.txDelete(typeOld);
				}
			}
		}
		
		//??????assetPool??????

		AssetPool ap = new AssetPool();

		ap.setApName("?????????");
		ap.setPoolType(PoolComm.ZCC_PJC);
		ap.setCustId(pedProtocolDto.getPoolInfoId());// ????????????id
		ap.setCustName(pedProtocolDto.getCustname());// 4.????????????
		ap.setCustNo(pedProtocolDto.getCustnumber());// 3.?????????
		ap.setCustOrgcode(pedProtocolDto.getCustOrgcode());//????????????????????????
//		ap.setCrtOptid(user.getId());
		ap.setBpsNo(pedProtocolDto.getPoolAgreement());//???????????????
        ap.setDealStatus("DS_002");//?????????
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(new Date());
		Date date = sdf.parse(dateStr);
		ap.setCrtTm(date);
		pedAssetPoolService.txStore(ap);
		logger.info("??????AssetPool,AssetPool?????????:"+ap.getApId()+"??????????????????:"+pedProtocolDto.getPoolInfoId()+"??????????????????:"+pedProtocolDto.getPoolAgreement());
		
	}

	@Override
	public void txCreateAssetTypeInfo(PedProtocolDto pedProtocolDto,String assetType)
			throws Exception {
		//??????AssetType??????
		AssetType at = new AssetType();
		BigDecimal zero = new BigDecimal("0");
			if(PoolComm.ED_PJC.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("?????????????????????");
			}else if(PoolComm.ED_PJC_01.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("?????????????????????");
			}else if(PoolComm.ED_BZJ_DQ.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("?????????????????????");
			}else if(PoolComm.ED_BZJ_HQ.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("?????????????????????");
			
			}
			
			at.setCrdtTotal(zero);
			at.setCrdtFree(zero);
			at.setCrdtUsed(zero);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sdf.format(new Date());
			Date date = sdf.parse(dateStr);
			at.setCrtTm(date);
			at.setCrdtFrzd(zero);
			String apId = pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(pedProtocolDto.getPoolAgreement(),null, pedProtocolDto.getCustnumber()).getApId();
			at.setApId(apId);
			pedAssetTypeService.txStore(at);
			
			if(PoolComm.ED_BZJ_HQ.equals(assetType)){
				//deleteBailDetailByAtId(pedProtocolDto.getMarginAccount());
				BailDetail bailDetail = poolBailEduService.queryBailDetailByBpsNo(pedProtocolDto.getPoolAgreement());
				if(null==bailDetail) {
					BailDetail curBail = AssetFactory.newCurBailDetail();
					curBail.setAssetNb(pedProtocolDto.getMarginAccount());
					curBail.setBailType(PoolComm.BZJ_HQ);
					curBail.setAssetAmt(BigDecimal.ZERO);
					curBail.setAssetLimitTotal(BigDecimal.ZERO);
					curBail.setAssetLimitUsed(BigDecimal.ZERO);
					curBail.setAssetLimitFree(BigDecimal.ZERO);
					curBail.setAssetLimitFrzd(BigDecimal.ZERO);
					curBail.setAssetStatus(PoolComm.BAIL_STATUS_ACTIVE);
					curBail.setAt(at.getId());
					curBail.setAssetNb(pedProtocolDto.getMarginAccount());
					curBail.setPoolAgreement(pedProtocolDto.getPoolAgreement());
					this.txStore(curBail);
				}
			}
			
	}

	/**
	 * ??????marginAccount??????BailDetail????????????
	 * @param marginAccount   
	 * @author Ju Nana
	 * @date 2019-2-20 ??????6:58:44
	 */
	private void deleteBailDetailByAtId(String marginAccount){
		String sql = "select  bail from BailDetail as bail where bail.assetNb = '"+marginAccount+"'";
		List result = this.find(sql);
		if(result!=null&&result.size()>0){
			for(int i=0;i<result.size();i++){
				BailDetail bai = (BailDetail)result.get(i);
				this.txDelete(bai);
			}
		}
		
	}

	@Override
	public CollectionSendDto queryDtoById(String id) throws Exception {
		String sql = "select Dto from CollectionSendDto as Dto where Dto.collectionSendId = '"+id+"'";
		List result = this.find(sql);
		if(result!=null&&result.size()>0){
			return (CollectionSendDto) result.get(0);
		}
		return null;
	}

	@Override
	public List<PedProtocolDto> queryProtocolByParam(String poolAgreement,String custnumber, String custOrgcode, String marginAccount)
			throws Exception {
		String sql = "select pedProtocolDto from PedProtocolDto as pedProtocolDto where 1=1 ";
		List<String> param = new ArrayList<String>();
		
		if(StringUtils.isNotEmpty(poolAgreement)){
			sql = sql + " and pedProtocolDto.poolAgreement = ? ";
			param.add(poolAgreement);
		}
		if(StringUtils.isNotEmpty(custnumber)){
			sql = sql + " and pedProtocolDto.custnumber = ? ";
			param.add(custnumber);
		}
		if(StringUtils.isNotEmpty(custOrgcode)){
			sql = sql + " and pedProtocolDto.custOrgcode = ? ";
			param.add(custOrgcode);
		}
		if(StringUtils.isNotEmpty(marginAccount)){
			sql = sql + " and pedProtocolDto.marginAccount = ? ";
			param.add(marginAccount);
		}
		List<PedProtocolDto> result = this.find(sql, param);
		if(result!=null&&result.size()>0){
			return result;
		}
		return null;
	}

	@Override
	public void contractExtension() throws Exception {
			String sql = "select pedProtocolDto from PedProtocolDto as pedProtocolDto where xyflag='1'  and pedProtocolDto.openFlag= '"+PoolComm.OPEN_01+"'";
			List result = this.find(sql);
			if(result!=null&&result.size()>0){
				for(int i=0;i<result.size();i++){
					PedProtocolDto pro = (PedProtocolDto)result.get(i);
					Date dueDate = pro.getEffenddate();//???????????????
					Date today = new Date();//????????????
					Calendar cal = Calendar.getInstance();
					cal.setTime(today);
					cal.add(Calendar.DATE,1);//?????????????????????
					Date todayNext =  cal.getTime();
					
					if(dueDate!=null  ){
						if(todayNext.getTime()>dueDate.getTime()){//????????????>????????????????????????
							cal.add(Calendar.YEAR, 1);//????????????
							dueDate = cal.getTime();
							pro.setEffenddate(dueDate);
							this.txStore(pro);
						}
						
					}
				}
			}
	}

	@Override
	public List<PedProtocolDto> queryProtocolInfo(String openFlag,String status,String poolAgreement,String custnumber,String custOrgcode,String marginAccount) throws Exception {
		String sql = "select pedProtocolDto from PedProtocolDto as pedProtocolDto where 1=1 ";
		if(StringUtil.isNotBlank(openFlag)){
			sql = sql + " and pedProtocolDto.openFlag='"+openFlag+"'";
		}
		if(StringUtil.isNotBlank(status)){
			sql = sql + " and pedProtocolDto.vStatus='"+status+"'";
		}
		if(StringUtil.isNotBlank(poolAgreement)){
			sql = sql + " and pedProtocolDto.poolAgreement='"+poolAgreement+"'";
		}
		if(StringUtil.isNotBlank(custnumber)){
			sql = sql + " and pedProtocolDto.custnumber='"+custnumber+"'";
		}
		if(StringUtil.isNotBlank(custOrgcode)){
			sql = sql + " and pedProtocolDto.custOrgcode='"+custOrgcode+"'";
		}
		if(StringUtil.isNotBlank(marginAccount)){
			sql = sql + " and pedProtocolDto.marginAccount='"+marginAccount+"'";
		}
		List result = this.find(sql);
		if(result!=null&&result.size()>0){
			return result;	
		}
		return null;
	}

	@Override
	public List<PedProtocolList> queryPedListByParam(String bpsNo,
			String custNo, String status ,String type ,String financingStatus) throws Exception {
		String sql = "select pdl from PedProtocolList as pdl where 1=1 ";
		List<String> param = new ArrayList<String>();
		
		if(StringUtils.isNotEmpty(bpsNo)){
			sql = sql + " and pdl.bpsNo = ? ";
			param.add(bpsNo);
		}
		if(StringUtils.isNotEmpty(custNo)){
			sql = sql + " and pdl.custNo = ? ";
			param.add(custNo);
		}
		if(StringUtils.isNotEmpty(status)){
			sql = sql + " and pdl.status = ? ";
			param.add(status);
		}
		if(type != null && type.equals("1")){
			sql = sql + " and pdl.custIdentity in ('KHLX_01','KHLX_03') ";
		}
		if(type != null && type.equals("2")){
			sql = sql + " and pdl.custIdentity = ? ";
			param.add("KHLX_02");
		}
		if(type != null && type.equals("3")){
			sql = sql + " and (pdl.custIdentity in ('KHLX_01','KHLX_03') or pdl.financingStatus = 'SF_01') ";
		}
		if(StringUtil.isNotEmpty(financingStatus)){
			sql = sql + " and pdl.financingStatus = ? ";
			param.add(financingStatus);
		}
		List<PedProtocolList> result = this.find(sql, param);
		if(result != null && result.size()>0){
			return result;
		}
		return null;
	}

	@Override
	public List queryPedListCustNoByParam(String bpsNo, String status,
			String type) throws Exception {
		String sql = "select pdl.custNo from PedProtocolList as pdl where 1=1 ";
		List<String> param = new ArrayList<String>();
		
		if(StringUtils.isNotEmpty(bpsNo)){
			sql = sql + " and pdl.bpsNo = ? ";
			param.add(bpsNo);
		}
		if(StringUtils.isNotEmpty(status)){
			sql = sql + " and pdl.status = ? ";
			param.add(status);
		}
		if(type != null && type.equals("1")){
			sql = sql + " and pdl.custIdentity in ('KHLX_01','KHLX_03') ";
		}
		if(type != null && type.equals("3")){
			sql = sql + " and (pdl.custIdentity in ('KHLX_01','KHLX_03') or pdl.financingStatus = 'FS_01') ";
		}
		List result = this.find(sql, param);
		if(result != null && result.size()>0){
			return result;
		}
		return null;
	}

@Override
	public List<PedCheck> queryPedCheck(String bpsNo, String custNo,
			Date accountDate, String batchNo,Page page) throws Exception {
		String sql = "select ch from PedCheck ch  where 1=1 ";
		List param = new ArrayList();
		
		if(StringUtils.isNotBlank(bpsNo)){
			sql = sql + " and ch.poolAgreement = ? ";
			param.add(bpsNo);
		}
		if(StringUtils.isNotBlank(custNo)){
			sql = sql + " and ch.custNo = ? ";
			param.add(custNo);
		}
		if(accountDate!=null){
			sql = sql + " and ch.accountDate = ? ";
			param.add(accountDate);
		}
		if(StringUtils.isNotBlank(batchNo)){
			sql = sql + " and ch.batchNo = ? ";
			param.add(batchNo);
		}
		
		sql = sql + "order by ch.accountDate desc ,ch.curTime desc ";
		List result = this.find(sql, param,page);
		if(result!=null && result.size()>0){
			return result;
		}
		return null;
	}

	@Override
	public List<PedCheckList> queryPedCheckListBybatch(String batchNo,Page page)
			throws Exception {
		String sql = "select ch from PedCheckList ch  where 1=1 ";
		List param = new ArrayList();
		
		if(StringUtils.isNotBlank(batchNo)){
			sql = sql + " and ch.batchNo = ? ";
			param.add(batchNo);
		}
		sql = sql + " order by ch.billNo ";
		List result = this.find(sql, param,page);
		if(result!=null && result.size()>0){
			return result;
		}
		return null;
	}

	@Override
	public List<PedCheckBatch> queryPedCheckBatch(String bpsNo, String custNo,
			Date accountDate, String batchNo,Page page) throws Exception {
		String sql = "select ch from PedCheckBatch ch  where 1=1 ";
		List param = new ArrayList();
		
		if(StringUtils.isNotBlank(bpsNo)){
			sql = sql + " and ch.poolAgreement = ? ";
			param.add(bpsNo);
		}
		if(StringUtils.isNotBlank(custNo)){
			sql = sql + " and ch.custNo = ? ";
			param.add(custNo);
		}
		if(accountDate!=null){
			sql = sql + " and ch.accountDate = ? ";
			param.add(accountDate);
		}
		if(StringUtils.isNotBlank(batchNo)){
			sql = sql + " and ch.batchNo = ? ";
			param.add(batchNo);
		}
		
		List result = this.find(sql, param,page);
		if(result!=null && result.size()>0){
			return result;
		}
		return null;
	}
	@Override
	public PlFeeScale queryFeeScale() throws Exception {
		String sql = "select fee from PlFeeScale as fee where fee.isseDt <= ? order by fee.isseDt desc ,fee.createDate desc ";
		List param = new ArrayList();
		param.add(new Date());
		List result = this.find(sql,param);
		if(result!=null&&result.size()>0){
			PlFeeScale feeScale = (PlFeeScale)result.get(0);
			return feeScale;	
		}
		return null;
	}

	public List<PedProtocolList> queryPoolCustNo(String custNo,String role,String xyIntoPool) throws Exception {
		String hql = "select pedProtocolList from PedProtocolList ppd where 1=1 '";
		List<String> param = new ArrayList<String>();
		if(StringUtils.isNotEmpty(custNo)){
			hql = hql + " and ppd.custNo = ? ";
			param.add(custNo);
		}
		if(StringUtils.isNotEmpty(role)){
			hql = hql + " and ppd.role = ? ";
			param.add(role);
		}
		if(StringUtils.isNotEmpty(xyIntoPool)){
			hql = hql + " and ppd.xyIntoPool = ? ";
			param.add(xyIntoPool);
		}
		List<PedProtocolList> result = this.find(hql, param);
		if(result!=null&&result.size()>0){
			return result;
		}
		return null;
	}

	@Override
	public List<PedProtocolList> queryProListByQueryBean(
			ProListQueryBean queryBean) throws Exception {
		List paramName = new ArrayList();// ??????
		List paramValue = new ArrayList();// ???
		StringBuffer hql = new StringBuffer();
		hql.append("select proList from PedProtocolList as proList where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getBpsNo())){//???????????????
				hql.append(" and proList.bpsNo =:bpsNo");
				paramName.add("bpsNo");
				paramValue.add(queryBean.getBpsNo());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustNo())){//???????????????
				hql.append(" and proList.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNo());
			}
			if(queryBean.getCustNos()!=null && queryBean.getCustNos().size()>0){//???????????????list
				hql.append(" and proList.custNo in (:custNos)");
				paramName.add("custNos");
				paramValue.add(queryBean.getCustNos());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOrgCoge())){//??????????????????
				hql.append(" and proList.orgCoge =:orgCoge");
				paramName.add("orgCoge");
				paramValue.add(queryBean.getOrgCoge());
			}
			
			if(StringUtil.isNotBlank(queryBean.getStatus())){//????????????    00????????????  01????????????    02????????????
				hql.append(" and proList.status =:status");
				paramName.add("status");
				paramValue.add(queryBean.getStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getFinancingStatus())){//?????????????????????	FS_01 ??????   FS_00 ??????
				hql.append(" and proList.financingStatus =:financingStatus");
				paramName.add("financingStatus");
				paramValue.add(queryBean.getFinancingStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getRole())){// ??????   JS_01?????????     JS_02?????????
				hql.append(" and proList.role =:role");
				paramName.add("role");
				paramValue.add(queryBean.getRole());
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustIdentity())){//????????????  KHLX_01:?????????  KHLX_02:?????????   KHLX_03:?????????+?????????  KHLX_04:????????????
				hql.append(" and proList.custIdentity =:custIdentity");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentity());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////??????????????????  01   ????????? 00
				hql.append(" and proList.zyFlag =:zyFlag");
				paramName.add("zyFlag");
				paramValue.add(queryBean.getZyflag());
			}
			
			if(queryBean.getStautsList()!=null&&queryBean.getStautsList().size()>0){////????????????
				hql.append(" and proList.status in (:statusList)");
				paramName.add("statusList");
				paramValue.add(queryBean.getStautsList());
			}
			
			if(queryBean.getCustIdentityList()!=null&&queryBean.getCustIdentityList().size()>0){//????????????
				hql.append(" and proList.custIdentity in (:custIdentity)");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentityList());
			}
		}
		
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolList> result = this.find(hql.toString(), paramNames, paramValues );
		
		if(result!=null&&result.size()>0){
			return result;	
		}
		
		return null;
	}

	@Override
	public PedProtocolList queryProtocolListByQueryBean(
			ProListQueryBean queryBean) throws Exception {
		List paramName = new ArrayList();// ??????
		List paramValue = new ArrayList();// ???
		StringBuffer hql = new StringBuffer();
		hql.append("select proList from PedProtocolList as proList where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getBpsNo())){//???????????????
				hql.append(" and proList.bpsNo =:bpsNo");
				paramName.add("bpsNo");
				paramValue.add(queryBean.getBpsNo());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustNo())){//???????????????
				hql.append(" and proList.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNo());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOrgCoge())){//??????????????????
				hql.append(" and proList.orgCoge =:orgCoge");
				paramName.add("orgCoge");
				paramValue.add(queryBean.getOrgCoge());
			}
			
			if(StringUtil.isNotBlank(queryBean.getStatus())){//????????????    00????????????  01????????????    02????????????
				hql.append(" and proList.status =:status");
				paramName.add("status");
				paramValue.add(queryBean.getStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getFinancingStatus())){//?????????????????????	FS_01 ??????   FS_00 ??????
				hql.append(" and proList.financingStatus =:financingStatus");
				paramName.add("financingStatus");
				paramValue.add(queryBean.getFinancingStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getRole())){// ??????   JS_01?????????     JS_02?????????
				hql.append(" and proList.role =:role");
				paramName.add("role");
				paramValue.add(queryBean.getRole());
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustIdentity())){//????????????  KHLX_01:?????????  KHLX_02:?????????   KHLX_03:?????????+?????????  KHLX_04:????????????
				hql.append(" and proList.custIdentity =:custIdentity");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentity());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////??????????????????  01   ????????? 00
				hql.append(" and proList.zyflag =:zyflag");
				paramName.add("zyflag");
				paramValue.add(queryBean.getZyflag());
			}
			
			if(queryBean.getStautsList()!=null&&queryBean.getStautsList().size()>0){////??????????????????  01   ????????? 00
				hql.append(" and proList.status in (:statusList)");
				paramName.add("statusList");
				paramValue.add(queryBean.getStautsList());
			}
			
			if(StringUtil.isNotBlank(queryBean.getEleAccount())){//??????????????????
				hql.append(" and proList.elecDraftAccount like :elecDraftAccount");
				paramName.add("elecDraftAccount");
				paramValue.add("%"+queryBean.getEleAccount()+"%");
			}
		}
		
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolList> result = this.find(hql.toString(), paramNames, paramValues );

		if(result!=null&&result.size()>0){
			PedProtocolList proList = result.get(0);
			return proList;	
		}
		return null;
	}

	@Override
	public PedProtocolDto queryProtocolDto(String openFlag, String status,
			String poolAgreement, String custnumber, String custOrgcode,
			String marginAccount) throws Exception {

		String sql = "from PedProtocolDto as pedProtocolDto where 1=1 ";
		if(StringUtil.isNotBlank(openFlag)){
			sql = sql + " and pedProtocolDto.openFlag='"+openFlag+"'";
		}
		if(StringUtil.isNotBlank(status)){
			sql = sql + " and pedProtocolDto.vStatus='"+status+"'";
		}
		if(StringUtil.isNotBlank(poolAgreement)){
			sql = sql + " and pedProtocolDto.poolAgreement='"+poolAgreement+"'";
		}
		if(StringUtil.isNotBlank(custnumber)){
			sql = sql + " and pedProtocolDto.custnumber='"+custnumber+"'";
		}
		if(StringUtil.isNotBlank(custOrgcode)){
			sql = sql + " and pedProtocolDto.custOrgcode='"+custOrgcode+"'";
		}
		if(StringUtil.isNotBlank(marginAccount)){
			sql = sql + " and pedProtocolDto.marginAccount='"+marginAccount+"'";
		}
		List<PedProtocolDto> result = this.find(sql);
		if(result!=null&&result.size()>0){
			PedProtocolDto dto = result.get(0);
			return dto;	
		}
		return null;
	
	}

	@Override
	public Ret isCanCreateCreditByCheckResult(PedProtocolDto protocol)throws Exception {
		
		logger.info("??????????????????.......................");
		
		Ret ret =  new Ret();
		String str = "";
		boolean canOrNot = true;//??????????????????????????????    true?????????   false????????????
		
		if(PoolComm.NO.equals(protocol.getIsGroup())){//?????????
			canOrNot = this.isEveryCheckPass(protocol.getPoolAgreement(),protocol.getCustnumber());
			if (!canOrNot) {
				ret.setRET_CODE(Constants.CREDIT_10);
				ret.setRET_MSG(protocol.getCustnumber() + ":??????????????????????????????????????????");
				return ret;
			}
		}else{//?????? ???????????????????????????????????????????????????????????????????????????
			ProListQueryBean queryBean = new ProListQueryBean();
			queryBean.setBpsNo(protocol.getPoolAgreement());
			List<String> custIdentityList = new ArrayList<String>();
			custIdentityList.add(PoolComm.KHLX_01);
			custIdentityList.add(PoolComm.KHLX_03);
			queryBean.setCustIdentityList(custIdentityList);
			List<PedProtocolList> proMems = this.queryProListByQueryBean(queryBean); 
			
			for(PedProtocolList mem : proMems ){
				boolean result = this.isEveryCheckPass(mem.getBpsNo(), mem.getCustNo());
				if(!result){
					canOrNot = false;
					str = str + mem.getCustNo() +"???";
					continue;
				}
			}
			if (!canOrNot) {
				str = str.substring(0, str.length()-1);
				ret.setRET_CODE(Constants.CREDIT_10);
				ret.setRET_MSG(str + ":??????????????????????????????????????????");
				return ret;
			}
			
		}
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG("???????????????");
		return ret;
	}
	
	/**
	 * ?????????????????????????????????
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-1??????10:42:19
	 */
	@Override
	public boolean isEveryCheckPass(String bpsNo,String custNo)
			throws Exception {
		
		logger.info("??????????????????"+bpsNo+"???????????????"+custNo+"???????????????????????????.......................");
		
		boolean result = false;//??????????????????   true?????????     false????????????
		Date twoMonAgo = DateTimeUtil.getDateWithMonths(new Date(), -2);//?????????????????????
		
		
		logger.info("?????????????????????twoMonAgo???"+DateUtils.formatDateToString(twoMonAgo, DateUtils.ORA_DATE_FORMAT));
		
		List paramName = new ArrayList();// ??????
		List paramValue = new ArrayList();// ???
		StringBuffer hql = new StringBuffer();
		hql.append("select ch from PedCheck ch  where 1=1 ");
		
		hql.append(" and ch.accountDate >:twoMonAgo");
		paramName.add("twoMonAgo");
		paramValue.add(twoMonAgo);

		
		if(StringUtils.isNotBlank(custNo)){
			hql.append(" and ch.custNo =:custNo");
			paramName.add("custNo");
			paramValue.add(custNo);
		}
		
		if(StringUtils.isNotBlank(bpsNo)){
			hql.append(" and ch.poolAgreement =:bpsNo");
			paramName.add("bpsNo");
			paramValue.add(bpsNo);
		}

		
		
		hql.append(" order by ch.accountDate desc ,ch.curTime desc ");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedCheck> resultList = this.find(hql.toString(), paramNames, paramValues );
		
		if(resultList!=null && resultList.size()>0){	

			if(PoolComm.DZJG_02.equals(resultList.get(0).getCheckResult())){//???2?????????????????????????????????????????????
				result =  false;
			}else{//???1??????????????????????????????2?????????????????????????????????
				int autoNum = 0 ;//??????????????????????????????
				boolean haveCheckOne =  false ;//???????????????????????????
				
				for(PedCheck check : resultList){
					if(PoolComm.YES.equals(check.getIsAuto())){
						autoNum ++;
					}
					if(!PoolComm.DZJG_00.equals(check.getCheckResult())){
						haveCheckOne = true; 
					}
				}
				if(autoNum<2 || haveCheckOne){
					result = true;
				}
			}

		}else{
			result = true;
		}
		return result;
	}

	@Override
	public PedProtocolDto queryProtocolDtoByQueryBean(
			ProtocolQueryBean queryBean) throws Exception {
		List paramName = new ArrayList();// ??????
		List paramValue = new ArrayList();// ???
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedProtocolDto as dto where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getPoolAgreement())){//???????????????
				hql.append(" and dto.poolAgreement =:poolAgreement");
				paramName.add("poolAgreement");
				paramValue.add(queryBean.getPoolAgreement());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustnumber())){//???????????????
				hql.append(" and dto.custnumber =:custnumber");
				paramName.add("custnumber");
				paramValue.add(queryBean.getCustnumber());
			}
			
			if(StringUtil.isNotBlank(queryBean.getIsGroup())){//????????????	1??????  0??????
				hql.append(" and dto.isGroup =:isGroup");
				paramName.add("isGroup");
				paramValue.add(queryBean.getIsGroup());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOpenFlag())){//????????????    00????????????  01????????????    02????????????
				hql.append(" and dto.openFlag =:openFlag");
				paramName.add("openFlag");
				paramValue.add(queryBean.getOpenFlag());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////??????????????????  01   ????????? 00
				hql.append(" and dto.zyflag =:zyflag");
				paramName.add("zyflag");
				paramValue.add(queryBean.getZyflag());
			}

			if(StringUtil.isNotBlank(queryBean.getvStatus())){//???????????????????????????   VS_01?????????    VS_02:??????
				hql.append(" and dto.vStatus =:vStatus");
				paramName.add("vStatus");
				paramValue.add(queryBean.getvStatus());
			}
			
		}
		
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolDto> result = this.find(hql.toString(), paramNames, paramValues );

		if(result!=null&&result.size()>0){
			PedProtocolDto dto = result.get(0);
			return dto;	
		}
		return null;
	}

	@Override
	public List<PedProtocolDto> queryProtocolDtoListByQueryBean(
			ProtocolQueryBean queryBean) throws Exception {
		List paramName = new ArrayList();// ??????
		List paramValue = new ArrayList();// ???
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedProtocolDto as dto where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getPoolAgreement())){//???????????????
				hql.append(" and dto.poolAgreement =:poolAgreement");
				paramName.add("poolAgreement");
				paramValue.add(queryBean.getPoolAgreement());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustnumber())){//???????????????
				hql.append(" and dto.custnumber =:custnumber");
				paramName.add("custnumber");
				paramValue.add(queryBean.getCustnumber());
			}
			
			if(StringUtil.isNotBlank(queryBean.getIsGroup())){//????????????	1??????  0??????
				hql.append(" and dto.isGroup =:isGroup");
				paramName.add("isGroup");
				paramValue.add(queryBean.getIsGroup());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOpenFlag())){//????????????    00????????????  01????????????    02????????????
				hql.append(" and dto.openFlag =:openFlag");
				paramName.add("openFlag");
				paramValue.add(queryBean.getOpenFlag());
			}
			
			if(StringUtil.isNotBlank(queryBean.getvStatus())){//????????????    VS_01?????????    VS_02:??????
				hql.append(" and dto.vStatus =:vStatus");
				paramName.add("vStatus");
				paramValue.add(queryBean.getvStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////??????????????????  01   ????????? 00
				hql.append(" and dto.zyflag =:zyflag");
				paramName.add("zyflag");
				paramValue.add(queryBean.getZyflag());
			}
			
			if(StringUtil.isNotBlank(queryBean.getContract())){//?????????????????????
				hql.append(" and dto.contract != 'null'");
			}

			if(queryBean.getContractDueDt() != null && !"".equals(queryBean.getContractDueDt()) ){//??????????????????????????????????????????
				hql.append(" and dto.contractDueDt>=TO_DATE(:contractDueDt, 'yyyy-mm-dd hh24:mi:ss')");
				paramName.add("contractDueDt");
				paramValue.add(DateUtils.toString(queryBean.getContractDueDt(), "yyyy-MM-dd") + " 00:00:00");
			}
			
		}
		
		
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolDto> result = this.find(hql.toString(), paramNames, paramValues );
		if(result!=null&&result.size()>0){
			return result;	
		}
		return null;
		
	}

	@Override
	public PedCheckBatch queryPedCheckBatchId(String batchNo) throws Exception {
		String hql = "select ppd from PedCheckBatch ppd where 1=1 ";
		List<String> param = new ArrayList<String>();
		if(StringUtils.isNotEmpty(batchNo)){
			hql = hql + " and ppd.id = ? ";
			param.add(batchNo);
		}
		List<PedCheckBatch> result = this.find(hql, param);
		if(result!=null&&result.size()>0){
			PedCheckBatch pedBatch = (PedCheckBatch)result.get(0);
			return pedBatch;
		}
		return null;
	}

	@Override
	public PedCheck queryPedCheckNo(String batchNo) throws Exception {
		String hql = "select ppd from PedCheck ppd where 1=1 ";
		List<String> param = new ArrayList<String>();
		if(StringUtils.isNotEmpty(batchNo)){
			hql = hql + " and ppd.id = ? ";
			param.add(batchNo);
		}
		List<PedCheck> result = this.find(hql, param);
		if(result!=null&&result.size()>0){
			PedCheck pedCheck = (PedCheck)result.get(0);
			return pedCheck;
		}
		return null;
	}

	@Override
	public List queryPedListByParamDetail(String bpsNo, String custIdentity,
			String status,Page page) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from PedProtocolList as pdl where 1=1 ");
		List paramName = new ArrayList();// ??????
		List paramValue = new ArrayList();// ???
		
		if(StringUtils.isNotEmpty(bpsNo)){
			hql.append( " and pdl.bpsNo = :bpsNo ");
			paramName.add("bpsNo");
			paramValue.add(bpsNo);
		}
		if(StringUtils.isNotEmpty(custIdentity)){
			if(PoolComm.KHLX_01.equals(custIdentity)){
				List custIdentityList = new ArrayList();
				custIdentityList.add(PoolComm.KHLX_01);
				custIdentityList.add(PoolComm.KHLX_03);
				hql.append(" and pdl.custIdentity in( :custIdentity) ");
				paramName.add("custIdentity");
				paramValue.add(custIdentityList);
			}else if( PoolComm.KHLX_02.equals(custIdentity)){
				List custIdentityList = new ArrayList();
				custIdentityList.add(PoolComm.KHLX_02);
				custIdentityList.add(PoolComm.KHLX_03);
				hql.append(" and pdl.custIdentity in( :custIdentity) ");
				paramName.add("custIdentity");
				paramValue.add(custIdentityList);
			}else{
				hql.append(" and pdl.custIdentity = :custIdentity ");
				paramName.add("custIdentity");
				paramValue.add(custIdentity);
			}
		}
		if(StringUtils.isNotEmpty(status)){
			hql.append(" and pdl.status = :status ");
			paramName.add("status");
			paramValue.add(status);
		}
		hql.append(" order by pdl.role");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolDto> result = this.find(hql.toString(), paramNames, paramValues,page);
		if(result != null && result.size()>0){
			return result;
		}
		return null;
	}

	@Override
	public boolean isAutoCheck(String bpsNo,String custNo) throws Exception {
		
		/*
		 * ?????????????????????????????????
		 */
		ProtocolQueryBean queryBean1 =  new ProtocolQueryBean();
		queryBean1.setCustnumber(custNo);
		queryBean1.setIsGroup(PoolComm.NO);//?????????
		queryBean1.setZyflag(PoolComm.ZY_FLAG_01);//????????????
		queryBean1.setOpenFlag(PoolComm.OPEN_01);//?????????
		List<PedProtocolDto> pro = this.queryProtocolDtoListByQueryBean(queryBean1);
		
		/*
		 * ????????????????????????????????????
		 */
		ProListQueryBean queryBean2 =  new ProListQueryBean();
		queryBean2.setCustNo(custNo);
		queryBean2.setStatus(PoolComm.PRO_LISE_STA_01);//???????????????
		queryBean2.setZyflag(PoolComm.ZY_FLAG_01);
		List<PedProtocolList> proMem = this.queryProListByQueryBean(queryBean2); 
		
		/*
		 *	????????????????????????????????????????????????????????? 
		 */
		
		if(StringUtil.isNotBlank(bpsNo)){
			
			//????????????????????????????????????????????????????????????????????????
			
			if(pro!=null && pro.size()>0){
				PedProtocolDto dto = pro.get(0);
				if(!dto.getPoolAgreement().equals(bpsNo)){//????????????????????????????????????
					return true;				
				}
			}
			
			if(proMem!=null && proMem.size()>0){
				PedProtocolList mem = proMem.get(0);
				if(!mem.getBpsNo().equals(bpsNo)){//????????????????????????????????????
					return true;				
				}
			}
			
		}else{
			
			//???????????????????????????????????????????????????????????????????????? 
			
			if(pro!=null && pro.size()>0){
				return true;				
			}
			
			if(proMem!=null && proMem.size()>0){
				return true;				
			}
			
		}
		
		
		return false;
	}	
	
	@Override
	public List<PedProtocolList> queryProListByQueryBean(ProListQueryBean queryBean, Page page) throws Exception {
		List paramName = new ArrayList();// ??????
		List paramValue = new ArrayList();// ???
		StringBuffer hql = new StringBuffer();
		hql.append("select proList from PedProtocolList as proList where 1=1 ");

		if(queryBean!=null){

			if(StringUtil.isNotBlank(queryBean.getBpsNo())){//???????????????
				hql.append(" and proList.bpsNo =:bpsNo");
				paramName.add("bpsNo");
				paramValue.add(queryBean.getBpsNo());

			}

			if(StringUtil.isNotBlank(queryBean.getCustNo())){//???????????????
				hql.append(" and proList.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNo());
			}

			if(StringUtil.isNotBlank(queryBean.getOrgCoge())){//??????????????????
				hql.append(" and proList.orgCoge =:orgCoge");
				paramName.add("orgCoge");
				paramValue.add(queryBean.getOrgCoge());
			}

			if(StringUtil.isNotBlank(queryBean.getStatus())){//????????????    00????????????  01????????????    02????????????
				hql.append(" and proList.status =:status");
				paramName.add("status");
				paramValue.add(queryBean.getStatus());
			}

			if(StringUtil.isNotBlank(queryBean.getFinancingStatus())){//?????????????????????	FS_01 ??????   FS_00 ??????
				hql.append(" and proList.financingStatus =:financingStatus");
				paramName.add("financingStatus");
				paramValue.add(queryBean.getFinancingStatus());
			}

			if(StringUtil.isNotBlank(queryBean.getRole())){// ??????   JS_01?????????     JS_02?????????
				hql.append(" and proList.role =:role");
				paramName.add("role");
				paramValue.add(queryBean.getRole());
			}

			if(StringUtil.isNotBlank(queryBean.getCustIdentity())){//????????????  KHLX_01:?????????  KHLX_02:?????????   KHLX_03:?????????+?????????  KHLX_04:????????????
				hql.append(" and proList.custIdentity =:custIdentity");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentity());
			}

			if(StringUtil.isNotBlank(queryBean.getZyflag())){////??????????????????  01   ????????? 00
				hql.append(" and proList.zyFlag =:zyFlag");
				paramName.add("zyFlag");
				paramValue.add(queryBean.getZyflag());
			}

			if(queryBean.getStautsList()!=null&&queryBean.getStautsList().size()>0){////????????????
				hql.append(" and proList.status in (:statusList)");
				paramName.add("statusList");
				paramValue.add(queryBean.getStautsList());
			}

			if(queryBean.getCustIdentityList()!=null&&queryBean.getCustIdentityList().size()>0){//????????????
				hql.append(" and proList.custIdentity in (:custIdentity)");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentityList());
			}
		}

		hql.append(" order by proList.role ASC ");
		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List<PedProtocolList> result = this.find(hql.toString(), paramNames, paramValues,page );

		if(result!=null&&result.size()>0){
			return result;
		}

		return null;
	}

	@Override
	public QueryResult queryPedCheckList(String batchNo, Page page) throws Exception {
		QueryResult queryResult = new QueryResult();

		List records = new ArrayList();
		List poolLists = new ArrayList();

		String amountFieldName = "billAmount";
		records = this.queryPedCheckBatchNoList(batchNo, page);
		if (records != null && records.size() > 0) {
			queryResult = QueryResult.buildQueryResult(records, amountFieldName);
		} else {
			return null;
		}
		return queryResult;
	}


	public List<PedCheckList> queryPedCheckBatchNoList(String batchNo, Page page) throws Exception {
		String sql = "select ch from PedCheckList ch  where 1=1 ";
		List param = new ArrayList();

		if(StringUtils.isNotBlank(batchNo)){
			sql = sql + " and ch.batchNo = ? ";
			param.add(batchNo);
		}

		sql = sql +" order by ch.dueDt desc , ch.id desc  ";
		List result = this.find(sql, param,page);
		if(result!=null && result.size()>0){
			return result;
		}
		return null;

	}

	@Override
	public boolean isChecked(String bpsNo, String custNo) throws Exception {
		String sql = "select ch from PedCheck ch  where 1=1 ";
		List param = new ArrayList();

		if(StringUtils.isNotBlank(bpsNo)){
			sql = sql + " and ch.poolAgreement = ? ";
			param.add(bpsNo);
		}
		
		if(StringUtils.isNotBlank(custNo)){
			sql = sql + " and ch.custNo = ? ";
			param.add(custNo);
		}

		sql = sql +" order by ch.accountDate desc  ";
		List<PedCheck> result = this.find(sql, param);
		if(result!=null && result.size()>0){
			PedCheck check = result.get(0);
			if(PoolComm.DZJG_00.equals(check.getCheckResult())){
				return false;
			}else{
				return true;
			}
		}
		return true;
	}
	
	public User queryUserById(String accountManagerId) throws Exception {
		List resultList = new ArrayList();
		List<String> paramName = new ArrayList<String>();// ??????
		List<String> paramValue = new ArrayList<String>();// ???
		String hql= "from User b where 1=1 ";
		if(StringUtils.isNotEmpty(accountManagerId)){
			hql = hql+" and b.loginName =:loginName  ";
			paramName.add("loginName");
			paramValue.add(accountManagerId);	
		}
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		resultList = this.find(hql,paramNames,paramValues);
		if(resultList!=null && resultList.size()>0){
			User user = (User) resultList.get(0);
			return user;
		}
		return null;
	}

	@Override
	public PedCheck queryPedCheck(String bpsNo, String custNo) throws Exception {
		String sql = "select ch from PedCheck ch  where 1=1 ";
		List param = new ArrayList();

		if(StringUtils.isNotBlank(bpsNo)){
			sql = sql + " and ch.poolAgreement = ? ";
			param.add(bpsNo);
		}

		if(StringUtils.isNotBlank(custNo)){
			sql = sql + " and ch.custNo = ? ";
			param.add(custNo);
		}

		sql = sql +" order by ch.accountDate desc ,ch.curTime desc  ";
		List<PedCheck> result = this.find(sql, param);
		if(result!=null && result.size()>0){
			PedCheck check = result.get(0);
				return check;
			}else {
			return null;
		}
	}
	
	@Override
	public List<PedCheck> queryPedCheckNew(PedCheck ped,User user,Page page) throws Exception {
		StringBuffer hql = new StringBuffer("select ch from PedCheck ch,PedProtocolDto ped  where 1=1 and ch.poolAgreement = ped.poolAgreement ");
		List parasValue = new ArrayList();//???????????????
		List parasName = new ArrayList();//???????????????
		// ????????????????????????
		if (user != null && user.getDepartment() != null) {
			// ???????????????????????????????????????????????????????????????????????????????????????
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and ped.signDeptNo in (:signDeptNo) ");
				parasName.add("signDeptNo");
				parasValue.add(resultList);
			}
		}		
		if(null != ped){
			if(StringUtils.isNotBlank(ped.getPoolAgreement())){
				hql.append(" and ch.poolAgreement =:poolAgreement  ");
				parasName.add("poolAgreement");
				parasValue.add(ped.getPoolAgreement());
			}

			if(StringUtils.isNotBlank(ped.getCustNo())){
				hql.append(" and ch.custNo =:custNo ") ;
				parasName.add("custNo");
				parasValue.add(ped.getCustNo());
			}
			if(StringUtils.isNotBlank(ped.getCustName())){
				hql.append(" and ch.custName =:custName") ;
				parasName.add("custName");
				parasValue.add(ped.getCustName());
			}
			if(StringUtils.isNotBlank(ped.getCheckResult())){
				hql.append(" and ch.checkResult =:checkResult") ;
				parasName.add("checkResult");
				parasValue.add(ped.getCheckResult());
			}
			// ????????????
			if (ped.getCurTimeStart() != null && !"".equals(ped.getCurTimeStart())) {
				hql.append(" and ch.curTime>=TO_DATE(:curTimeStart, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("curTimeStart");
				parasValue.add(DateUtils.toString(ped.getCurTimeStart(), "yyyy-MM-dd") + " 00:00:00");
			}
			//????????????
			if (ped.getCurTimeEnd() != null && !"".equals(ped.getCurTimeEnd())) {
				hql.append(" and ch.curTime<=TO_DATE(:curTimeEnd, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("curTimeEnd");
				parasValue.add(DateUtils.toString(ped.getCurTimeEnd(), "yyyy-MM-dd") + " 23:59:59");
			}			
						
		}
		hql.append(" order by ch.curTime desc  ");
		//???????????????
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//???????????????
		Object[] parameters = parasValue.toArray(); //???????????????
		List<PedCheck> result = this.find(hql.toString(), nameForSetVar, parameters,page);		
		if(result!=null && result.size()>0){
			return result;
		}
		return null;	
	}
	@Override
	public List<PedCheckBatch> queryPedCheckBatchNew(PedCheckBatch ped,User user,Page page) throws Exception {
		StringBuffer hql = new StringBuffer("select ch from PedCheckBatch ch,PedProtocolDto ped  where 1=1 and ch.poolAgreement = ped.poolAgreement ");
		List parasValue = new ArrayList();//???????????????
		List parasName = new ArrayList();//???????????????
		// ????????????????????????
		if (user != null && user.getDepartment() != null) {
			// ???????????????????????????????????????????????????????????????????????????????????????
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and ped.signDeptNo in (:signDeptNo) ");
				parasName.add("signDeptNo");
				parasValue.add(resultList);
			}
		}		
		if(null != ped){
			if(StringUtils.isNotBlank(ped.getPoolAgreement())){
				hql.append(" and ch.poolAgreement =:poolAgreement  ");
				parasName.add("poolAgreement");
				parasValue.add(ped.getPoolAgreement());
			}

			if(StringUtils.isNotBlank(ped.getCustNo())){
				hql.append(" and ch.custNo =:custNo ") ;
				parasName.add("custNo");
				parasValue.add(ped.getCustNo());
			}
			if(StringUtils.isNotBlank(ped.getBatchNo())){
				hql.append(" and ch.batchNo =:batchNo ") ;
				parasName.add("batchNo");
				parasValue.add(ped.getBatchNo());
			}
			if(StringUtils.isNotBlank(ped.getCustName())){
				hql.append(" and ch.custName =:custName") ;
				parasName.add("custName");
				parasValue.add(ped.getCustName());
			}
			/*// ????????????
			if (ped.getCurTimeStart() != null && !"".equals(ped.getCurTimeStart())) {
				hql.append(" and ch.curTime>=TO_DATE(:curTimeStart, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("curTimeStart");
				parasValue.add(DateUtils.toString(ped.getCurTimeStart(), "yyyy-MM-dd") + " 00:00:00");
			}
			//????????????
			if (ped.getCurTimeEnd() != null && !"".equals(ped.getCurTimeEnd())) {
				hql.append(" and ch.curTime<=TO_DATE(:curTimeEnd, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("curTimeEnd");
				parasValue.add(DateUtils.toString(ped.getCurTimeEnd(), "yyyy-MM-dd") + " 23:59:59");
			}*/			
			if(StringUtils.isNotBlank(ped.getBeginDate())){
				hql.append(" and ch.accountDate =:accountDate") ;
				parasName.add("accountDate");
				parasValue.add(DateUtils.parse(ped.getBeginDate(), DateUtils.ORA_DATES_FORMAT));
			}			
		}
		hql.append(" order by ch.curTime desc  ");
		//???????????????
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//???????????????
		Object[] parameters = parasValue.toArray(); //???????????????
		List<PedCheckBatch> result = this.find(hql.toString(), nameForSetVar, parameters,page);			
		if(result!=null && result.size()>0){
			return result;
		}
		return null;
	}

	@Override
	public void merge(Object o) throws Exception {
		this.dao.merge(o);
	}

	@Override
	public void flush() throws Exception {
		this.dao.flush();
	}

	@Override
	public void txProtocolAndListHist(String bpsNo) throws Exception {
		
		/*
		 * ????????????
		 */
		ProtocolQueryBean queryBean = new  ProtocolQueryBean();
		queryBean.setPoolAgreement(bpsNo);
		PedProtocolDto protocol =  this.queryProtocolDtoByQueryBean(queryBean);
		
		/*
		 * ????????????
		 */
		PedProtocolHist proHist = new PedProtocolHist();
		BeanUtil.beanCopy(protocol, proHist);
		proHist.setOperateTime(new Date());
		this.txSaveStore(proHist);
		
		if(PoolComm.YES.equals(protocol.getIsGroup())){
			/*
			 * ??????????????????
			 */
			ProListQueryBean bean = new ProListQueryBean();
			bean.setBpsNo(bpsNo);
			List<PedProtocolList> mems =  this.queryProListByQueryBean(bean);
			if(mems!=null && mems.size()>0){
				for(PedProtocolList proList : mems){					
					/*
					 * ??????????????????
					 */
					PedProtocolListHist listHist = new PedProtocolListHist();
					BeanUtil.beanCopy(proList, listHist);
					listHist.setEditTime(new Date());
					this.txSaveStore(listHist);
				}
			}
			
		}
		
		
		
	}


	@Override
	public String userManageChangeCheck(String accountManagerId,
			String accountManagerIdOld,User userLogon) throws Exception {

		if(StringUtils.isBlank(accountManagerId)){
			return "2";//???????????????Id??????
		}
		
		String deptIdLogon = userLogon.getDeptId();//??????????????????????????????
		
		User userOld =null;//?????????????????????
		String deptIdOld = null;//?????????????????????ID
		
		User userNew =null;//?????????????????????
		String deptIdNew = null;//?????????????????????ID
		
		/*
		 * ???????????????????????????
		 */
		
		userOld = this.queryUserById(accountManagerIdOld);
		if(userOld == null){
			return "4";//????????????????????????
		}
		deptIdOld =  userOld.getDeptId();//?????????????????????ID
		
		/*
		 * ???????????????????????????
		 */
		userNew = this.queryUserById(accountManagerId);
		if(userNew == null){
			return "1";//????????????????????????????????????
		}
		deptIdNew = userNew.getDeptId();//?????????????????????ID
		
		/*
		 * ???????????????????????????????????????
		 */
		boolean isManager = false;//?????????????????????
		if(userNew.getRoleList()!=null && userNew.getRoleList().size()>0){
			for(Role role : (List<Role>)userNew.getRoleList()){
				if( StringUtil.isNotBlank(role.getCode())){						
					String roleCode  = role.getCode();
					if(PoolComm.roleCode6.equals(roleCode)){
						isManager = true;
						break;
					}
				}
			}
		}
		if(!isManager){
             return "5";//???????????????????????????????????????
		}
		
		if(!this.deptCheck(deptIdLogon, deptIdOld)){
			return "6";//????????????????????????????????????????????????????????????????????????????????????
		}
		
		if(!this.deptCheck(deptIdLogon, deptIdNew)){
			return "7";//??????????????????????????????????????????????????????????????????????????????
		}
		
		return userNew.getName();
	}
	
	/**
	 * ???????????????????????????????????????????????????????????????
	 * @author Ju Nana
	 * @return
	 * @date 2019-10-22??????10:34:12
	 */
	private boolean deptCheck(String deptIdLogon,String deptId){
		
		/*
		 * ????????????????????????????????????
		 */
		if(deptIdLogon.equals(deptId)){
			return true;	
		}
		
		/*
		 * ????????????????????????????????????
		 */
		
		//????????????????????????
		List<String> underAllList = new ArrayList<String>();//??????????????????Id
		
		
		List<String> deptIdUnderList1 = new ArrayList<String>();//?????????????????????
		List<String> deptIdUnderList2 = new ArrayList<String>();//?????????????????????
		List<String> deptIdUnderList3 = new ArrayList<String>();//?????????????????????
		List<String> deptIdUnderList4 = new ArrayList<String>();//?????????????????????
		
		/*
		 * ?????????????????????
		 */
		deptIdUnderList1 = departmentService.getAllChildrenIdList(deptIdLogon, 1);
		
		/*
		 * ?????????????????????
		 */
		if(deptIdUnderList1!=null && deptIdUnderList1.size()>0){
			
			underAllList.addAll(deptIdUnderList1);
			
			for(String id : deptIdUnderList1){					
				List<String> childDeptIdList = departmentService.getAllChildrenIdList(id, 1);
				if(childDeptIdList!=null && childDeptIdList.size()>0){						
					deptIdUnderList2.addAll(childDeptIdList);
				}
			}
		}
		
		/*
		 * ?????????????????????
		 */
		if(deptIdUnderList2!=null && deptIdUnderList2.size()>0){
			
			underAllList.addAll(deptIdUnderList2);
			
			for(String id : deptIdUnderList2){					
				List<String> childDeptIdList = departmentService.getAllChildrenIdList(id, 1);
				if(childDeptIdList!=null && childDeptIdList.size()>0){						
					deptIdUnderList3.addAll(childDeptIdList);
				}
			}
		}
		
		/*
		 * ?????????????????????
		 */
		if(deptIdUnderList3!=null && deptIdUnderList3.size()>0){
			
			underAllList.addAll(deptIdUnderList3);
			
			for(String id : deptIdUnderList3){					
				List<String> childDeptIdList = departmentService.getAllChildrenIdList(id, 1);
				if(childDeptIdList!=null && childDeptIdList.size()>0){						
					deptIdUnderList4.addAll(childDeptIdList);
				}
			}
		}
		
		if(deptIdUnderList4!=null && deptIdUnderList4.size()>0){
			
			underAllList.addAll(deptIdUnderList4);
			
		}
		

		
		//????????????ID?????????????????????????????????????????????????????????
		if(underAllList!=null && underAllList.size()>0){
			for(String id : underAllList){
				if(deptId.equals(id)){
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	/**
	 * ??????,??????????????? ???????????????????????????????????????????????????
	 * 
	 * @param pedProtocolDto
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List exportProolListQuery(PedProtocolDto pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select ppd from PedProtocolDto ppd where 1=1 ");
		List valueList = new ArrayList(); // ?????????????????????
		List keyList = new ArrayList(); // ????????????????????????
		
		// ????????????????????????
			if (user != null && user.getDepartment() != null) {
				// ?????????????????????????????????????????????????????????????????????????????????????????????
				if (!PublicStaticDefineTab.isRootDepartment(user)) {
					List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:officeNetList) ");
					keyList.add("officeNetList");
					valueList.add(resultList);
				}
			}
		
		if (pedProtocolDto != null) {
			if (StringUtils.isNotBlank(pedProtocolDto.getPoolAgreement())) {
				sb.append(" and ppd.poolAgreement like :poolAgreement ");
				keyList.add("poolAgreement");
				valueList.add("%" + pedProtocolDto.getPoolAgreement() + "%");
			}
			
			if (StringUtils.isNotBlank(pedProtocolDto.getCustnumber())) {
				sb.append(" and ppd.custnumber like :custnumber ");
				keyList.add("custnumber");
				valueList.add("%" + pedProtocolDto.getCustnumber() + "%");
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getCustname())) {
				sb.append(" and ppd.custname like :custname ");
				keyList.add("custname");
				valueList.add("%" + pedProtocolDto.getCustname() + "%");
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getOpenFlag())) {
				sb.append(" and ppd.openFlag = :openFlag ");
				keyList.add("openFlag");
				valueList.add(pedProtocolDto.getOpenFlag());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getApproveFlag())) {
				sb.append(" and ppd.approveFlag = :approveFlag ");
				keyList.add("approveFlag");
				valueList.add(pedProtocolDto.getApproveFlag());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getCustOrgcode())) {
				sb.append(" and ppd.custOrgcode = :custOrgcode ");
				keyList.add("custOrgcode");
				valueList.add(pedProtocolDto.getCustOrgcode());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getIsGroup())) {
				sb.append(" and ppd.isGroup = :isGroup ");
				keyList.add("isGroup");
				valueList.add(pedProtocolDto.getIsGroup());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getvStatus())) {
				sb.append(" and ppd.vStatus = :vStatus ");
				keyList.add("vStatus");
				valueList.add(pedProtocolDto.getvStatus());
			}
			if(StringUtils.isNotBlank(pedProtocolDto.getPoolName())){
				sb.append(" and ppd.custname = :custname ");
				keyList.add("custname");
				valueList.add(pedProtocolDto.getPoolName());
			}
		}
		sb.append("order by ppd.operateTime asc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // ???????????????
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
	}
	@Override
	public List exportProolListQuery(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			PedProtocolDto dto = null;
			for (int i = 0; i < res.size(); i++) {
				String[] s = new String[16];
				dto = (PedProtocolDto) res.get(i);
				s[0] = dto.getPoolAgreement();
				s[1] = dto.getCustname();
				s[2] = dto.getCustnumber();
				s[3] = String.valueOf(dto.getCreditamount());
				s[4] = dto.getCustOrgcode();	
				if (dto.getEffstartdate() != null) {
					s[5] = DateUtils.formatDateToString(dto.getEffstartdate(), DateUtils.ORA_DATES_FORMAT);
				}
				if (dto.getEffenddate() != null) {
					s[6] = DateUtils.formatDateToString(dto.getEffenddate(), DateUtils.ORA_DATES_FORMAT);
				}
				s[7] = dto.getIsGroupName();	
				s[8] = dto.getVtStatusName();
				s[9] = dto.getOpenFlagName();
				s[10] = dto.getFrozenstateName();
				s[11] = dto.getAccountManager();
				s[12] = dto.getAccountManagerId();
				s[13] = dto.getSignDeptName();
				s[14] = dto.getCreditDeptName();
				s[15] = dto.getOfficeNetName();
				list.add(s);
			}
		}
		return list;
	}
	
	/** ??????:??????????????????????????????
     * @param protocol  ???????????????
     * @return Map
     * @throws Exception
     */
    public ReturnMessageNew txMarginAccount(PedProtocolDto protocol) throws Exception {
    	ReturnMessageNew returnMessageNew =new ReturnMessageNew();
    	ReturnMessageNew returnRate = new ReturnMessageNew();
    	
    	//??????????????????
    	String rate = "";
    	try {			
    		returnRate = poolCoreService.core011Handler( new CoreTransNotes());
    		if (returnRate.isTxSuccess()) {
    			if(null == returnRate.getBody().get("INT_RATE")){
    				returnRate.setTxSuccess(false);
        			returnRate.getRet().setRET_MSG("?????????????????????????????????");
        			return returnRate;
    			}else{    				
    				rate = ((String)returnRate.getBody().get("INT_RATE"));//??????
    			}
    		}else{
    			returnRate.setTxSuccess(false);
    			returnRate.getRet().setRET_MSG("????????????????????????:"+returnRate.getRet().getRET_MSG());
    			return returnRate;
    		}
    		
		} catch (Exception e) {
			returnRate.setTxSuccess(false);
			returnRate.getRet().setRET_MSG("????????????????????????:"+e.getMessage());
			return returnRate;
		}
    	
    	
    	//?????????????????????
    	
    	try {
	        CoreTransNotes transNotes = new CoreTransNotes();
	        transNotes.setCustIdA(protocol.getCustnumber());//?????????
	        transNotes.setCustNam(protocol.getCustname());//????????????  
	        
	        //???????????????????????????????????????
	        logger.info("?????????????????????"+protocol.getAccountManagerId()+"............................");
	        User user = userService.getUserByLoginName(protocol.getAccountManagerId());//??????????????????
	        logger.info("?????????????????????????????????"+user.getDeptId()+"............................");
	        Department dept = (Department) this.load(user.getDeptId(),Department.class);
	        logger.info("???????????????????????????"+dept.getInnerBankCode()+"............................");
	        transNotes.setBrcNo(dept.getInnerBankCode());//????????????
	        
	        //???????????????????????????????????????????????????????????????????????????????????????????????????????????=(???????*)))???
	        transNotes.setRate(rate);	        
	        
	        returnMessageNew = poolCoreService.core006Handler(transNotes);
	        if (returnMessageNew.isTxSuccess()) {
	        	protocol.setMarginAccount((String)returnMessageNew.getBody().get("ACCT_NO"));//??????
	        	protocol.setMarginAccountName("????????????????????????"+protocol.getCustname()+"???");
	            return returnMessageNew;
	        }else{
	            return returnMessageNew;
	        }
    	} catch (Exception e) {
			returnMessageNew.setTxSuccess(false);
			returnMessageNew.getRet().setRET_MSG("????????????????????????????????????:"+e.getMessage());
			logger.info("????????????????????????????????????"+e.getMessage());
			return returnMessageNew;
		}
        
    }
	/** ??????:??????????????????????????????
     * @param oldAcc  ?????????
     * @param newAcc ?????????
     * @return Map
     * @throws Exception
     */
    public ReturnMessageNew txMarginAccChange(String oldAcc,String newAcc,String poolAgreement) throws Exception {
    	ReturnMessageNew returnMessageNew =new ReturnMessageNew();
    	MarginAcctChangeHist  margin=new MarginAcctChangeHist();
    	List list =new ArrayList();
		 margin.setBpsNo(poolAgreement);
		 margin.setNewAcctNo(newAcc);
		 margin.setOldAcctNo(oldAcc);
		 margin.setCreateDate(new Date());
		 list.add(margin);//?????????????????????????????????
		 financialAdviceService.txCreateList(list);
    	try {
    		CoreTransNotes transNotes = new CoreTransNotes();
    		transNotes.setDepositAcctNo(oldAcc);//???????????????????????????
    		transNotes.setDrAcctNo(newAcc);//???????????????????????????
    		 returnMessageNew = poolCoreService.core010Handler(transNotes);

    		return returnMessageNew;

		} catch (Exception e) {
			returnMessageNew.setTxSuccess(false);
			returnMessageNew.getRet().setRET_MSG("????????????????????????????????????:"+e.getMessage());
			logger.info("????????????????????????????????????"+e.getMessage());
			return returnMessageNew;
		}
    }
	/** ??????:???????????????????????????
     * @return String
     * @throws Exception
     */
	public String loadContractJSON(ProtocolQueryBean pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception {

		List list = loadContractList(pedProtocolDto, queryBean, user, page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	/**
	 * ???????????????????????????
	 * 
	 * @param pedProtocolDto
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadContractList(ProtocolQueryBean pedProtocolDto,
			PoolQueryBean queryBean, User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select ppd from PedProtocolDto ppd where 1=1 ");
		List valueList = new ArrayList(); // ?????????????????????
		List keyList = new ArrayList(); // ????????????????????????
		if (pedProtocolDto != null) {
			
			String str = roleService.queryRoleDeptByUser(user);
			if(str != null){
				if(str.equals("0") || str.equals("2")){//???????????????;??????????????????????????????????????????????????????  ?????????????????????
					
				}else if(str.equals("1") || str.equals("3")){//???????????????;?????????????????????????????????????????????????????? ?????????????????????????????????
						//?????????????????????????????????????????????????????????
					List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:branchId) ");
					keyList.add("branchId");
					valueList.add(resultList);
				}else if(str.equals("4")){//????????????:?????????????????????????????????????????????????????????
					sb.append(" and ppd.accountManager = :accountManager ");
					keyList.add("accountManager");
					valueList.add(user.getName());
				}else if(str.equals("5")){//??????????????????:?????????
					return null;
				}
			}
			
			if (StringUtils.isNotBlank(pedProtocolDto.getPoolAgreement())) {//???????????????
				sb.append(" and ppd.poolAgreement like :poolAgreement ");
				keyList.add("poolAgreement");
				valueList.add("%" + pedProtocolDto.getPoolAgreement() + "%");
			}
			
			if (StringUtils.isNotBlank(pedProtocolDto.getPoolMode())) {//????????????
				sb.append(" and ppd.poolMode =:poolMode ");
				keyList.add("poolMode");
				valueList.add(pedProtocolDto.getPoolMode());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getContract())) {//????????????
				sb.append(" and ppd.contract = :contract ");
				keyList.add("contract");
				valueList.add(pedProtocolDto.getContract());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getvStatus())) {
				sb.append(" and ppd.vStatus = :vStatus ");
				keyList.add("vStatus");
				valueList.add(pedProtocolDto.getvStatus());
			}
			// ????????????????????????
			if ( null!=pedProtocolDto.getEffstartdate()) {
				sb.append(" and ppd.contractEffectiveDt>=:contractEffectiveDt");
				keyList.add("contractEffectiveDt");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getEffstartdate()));
			}
			// ??????????????????????????????
			if ( null!=pedProtocolDto.getEffenddate()) {
				sb.append(" and ppd.contractEffectiveDt<=:contractEffectiveDt1");
				keyList.add("contractEffectiveDt1");
				valueList.add(DateUtils.getCurrentDayEndDate(pedProtocolDto.getEffenddate()));
			}
			// ????????????????????????
			if ( null!=pedProtocolDto.getContractEffectiveDt()) {
				sb.append(" and ppd.contractDueDt>=:contractDueDt");
				keyList.add("contractDueDt");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getContractEffectiveDt()));
			}
			// ??????????????????????????????
			if ( null!=pedProtocolDto.getContractDueDt()) {
				sb.append(" and ppd.contractDueDt<=:contractDueDt2");
				keyList.add("contractDueDt2");
				valueList.add(DateUtils.getCurrentDayEndDate(pedProtocolDto.getContractDueDt()));
			}
						
			
			
			
		}
		sb.append(" order by ppd.poolAgreement asc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // ???????????????
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
	}

	@Override
	public ReturnMessageNew txChangeEdu(PedProtocolDto pedProtocolDto)  throws Exception {
		
		PedProtocolDto  ppd  = (PedProtocolDto) this.load(pedProtocolDto.getPoolInfoId());	
		String poolMode = pedProtocolDto.getPoolMode();

		ReturnMessageNew returnMessge=this.txPJE018Handler(ppd, poolMode);
        if(!returnMessge.isTxSuccess()){
     		return returnMessge;
        }
		ppd.setPoolMode(poolMode);
		if(StringUtils.isNotBlank(ppd.getContract())){
			//????????????????????????????????????
			if(poolMode.equals("01")){//?????????????????????????????????   0-???  1-??? ?????????????????????????????????????????????????????????
				ppd.setIsAcctCheck("1");
			}else if(poolMode.equals("02")){
				ppd.setIsAcctCheck("0");
			}
		}
		this.txStore(ppd);
		return returnMessge;
	}
	

	/**
	 * @throws Exception 
	 * @Title ???????????????????????????
	 * @author gcj
	 * @date 2021-7-19
	 * @param pedProtocolDto
	 */
	public ReturnMessageNew txPJE018Handler(PedProtocolDto pedProtocolDto,String poolMode) throws Exception {
		CreditTransNotes note = new CreditTransNotes();
		note.setBpsNo(pedProtocolDto.getPoolAgreement());
		note.setPoolMode(poolMode);
		ReturnMessageNew response = poolCreditClientService.txPJE018(note);
		return response;
	}

	@Override
	public boolean isPaid(PedProtocolDto pro) throws Exception {
	
		PlFeeScale  psScale = this.queryFeeScale();//??????????????????????????????
		if(null == psScale){
			logger.error("??????????????????????????????????????????????????????????????????");
			return false;
			
		}
		
		BigDecimal everyYear = psScale.getEveryYear();//????????????
		BigDecimal everyPiece = psScale.getEveryPiece();//??????????????????
		
		if((PoolComm.SFMS_01).equals(pro.getFeeType()) ){//????????????
			
			if( new BigDecimal("0").compareTo(everyYear)==0){	
				
				return true;//?????????????????????0?????????????????????????????????
			}
			
			if(DateUtils.compareDate(DateUtils.getCurrDateTime(),pro.getFeeDueDt())>0){//?????????
				return false;
			}else{
				return true;
			}
		}
		
		if((PoolComm.SFMS_02).equals(pro.getFeeType())){//??????????????????
			
			if(new BigDecimal("0").compareTo(everyYear)==0){
			
				return true;//?????????????????????0?????????????????????????????????
				
			}else{
				
				return false;
			}
		}
		
		return false;
		
		
	}
	
	public  String txCreateOnlineProductNo(String custId , int length) throws Exception{
		synchronized (this) {
			
			CustomerRegister dto = null;
			int olPrdtSerialNo = 0;
			String prdtNo = null ;//???????????????
			try {
				dto = (CustomerRegister) this.load(custId,CustomerRegister.class);
				olPrdtSerialNo = dto.getOlPrdtSerialNo();//???????????????????????????????????????
				
				prdtNo = olPrdtSerialNo + "";
				
				int noLength = prdtNo.length();
				if(noLength <= length){
					String prefix = "";
					for(int i=0;i<length - noLength;i++){
						prefix += "0";
					}
					prdtNo = prefix + prdtNo;
				}else{
					throw new Exception("?????????????????????????????????????????????");
				}
				
				olPrdtSerialNo = olPrdtSerialNo + 1 ;
				dto.setOlPrdtSerialNo(olPrdtSerialNo);
				this.txStore(dto);
				
			} catch (Exception e) {				
				logger.error("?????????????????????????????????????????????:", e);
				throw new Exception("?????????????????????????????????????????????");
				
			}
			
			logger.info("????????????????????????????????????"+prdtNo+"???");
			
			return prdtNo ;

		}
	}
	
	
	@Override
	public CustomerRegister queryCustomerRegisterByCustNo(String custNo) throws Exception {
		String hql = "select cust from CustomerRegister cust where 1=1 ";
		List<String> param = new ArrayList<String>();
		if(StringUtils.isNotEmpty(custNo)){
			hql = hql + " and cust.custNo = ? ";
			param.add(custNo);
		}
		List<CustomerRegister> result = this.find(hql, param);
		if(result!=null&&result.size()>0){
			CustomerRegister cust = (CustomerRegister)result.get(0);
			return cust;
		}
		return null;
	}

	
}
