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
	 * 提交开通审批serviceImpl
	 */
	public void txCommitPedpro(PedProtocolDto pedProtocolDto) throws Exception {
		pedProtocolDto = (PedProtocolDto) this.load(pedProtocolDto
				.getPoolInfoId());
		// 修改明细状态和报文状态
		pedProtocolDto.setApproveFlag(PoolComm.APPROVE_01);//签约审批中
		pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
		pedProtocolDto.setContractType(PoolComm.BT_08);
		this.txStore(pedProtocolDto);
	}

	/**
	 * 提交开通审批serviceImpl
	 */
	public void txCommitPedproFor(PedProtocolDto pedProtocolDto) throws Exception {
		pedProtocolDto = (PedProtocolDto) this.load(pedProtocolDto
				.getPoolInfoId());
		if(null != pedProtocolDto){
			if(PoolComm.FROZEN_STATUS_01.equals(pedProtocolDto.getFrozenstate()) || PoolComm.FROZEN_STATUS_02.equals(pedProtocolDto.getFrozenstate()) || PoolComm.FROZEN_STATUS_03.equals(pedProtocolDto.getFrozenstate()) ){
				pedProtocolDto.setContractType(PoolComm.BT_07);//改为解冻
			}else if(PoolComm.FROZEN_STATUS_00.equals(pedProtocolDto.getFrozenstate())){//判断状态是解冻还是冻结
				pedProtocolDto.setContractType(PoolComm.BT_06);//改为冻结
			}
		}
		this.txStore(pedProtocolDto);
	}

	// 审批开通时调用的方法 测试
	public void txSaveApproveOpclose(PedProtocolDto pedProtocolDto,PedProtocolModDto newPed)
			throws Exception {
		
		String approveFlag = pedProtocolDto.getApproveFlag();
		if(PoolComm.APPROVE_01.equals(approveFlag)){//签约审核中
			//签约时新增原表字段  start
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
			
			User user = userService.getUserByLoginName(newPed.getAccountManagerId());//客户经理ID
			if(null != user){    				
				Department dept =  (Department)userService.load(user.getDeptId(),Department.class);
				String deptNo = dept.getInnerBankCode();
				String deptName = dept.getName();
				pedProtocolDto.setOfficeNet(deptNo);
				pedProtocolDto.setOfficeNetName(deptName);
				
			}
			pedProtocolDto.setOperateTime(new Date());//操作时间
//			ped.setApproveFlag(PoolComm.APPROVE_01);//签约审批中
			pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);
			//新增原表字段   end
			pedProtocolDto.setOpenFlag(PoolComm.OPEN_01);//已签约
			pedProtocolDto.setApproveFlag(PoolComm.APPROVE_03);//签约审核通过
			pedProtocolDto.setContractType(PoolComm.BT_08);//置空业务标识
			//备表
			newPed.setOpenFlag(PoolComm.OPEN_01);//已签约
			newPed.setApproveFlag(PoolComm.APPROVE_03);//签约审核通过
			newPed.setContractType(PoolComm.BT_08);//置空业务标识
		}
		if(PoolComm.APPROVE_04.equals(approveFlag)){//解约审核中
			pedProtocolDto.setOpenFlag(PoolComm.OPEN_02);//已解约
			pedProtocolDto.setApproveFlag(PoolComm.APPROVE_06);//解约审核通过
			pedProtocolDto.setContractType(PoolComm.BT_08);//置空业务标识
			////备表
			newPed.setOpenFlag(PoolComm.OPEN_02);//已解约
			newPed.setApproveFlag(PoolComm.APPROVE_06);//解约审核通过
			newPed.setContractType(PoolComm.BT_08);//置空业务标识
		}
		pedProtocolDto.setOperateTime(new Date());
		newPed.setOperateTime(new Date());
		this.txStore(pedProtocolDto);
		this.txStore(newPed);
	}

	
	/**
	 * json串
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
	 * 查询,带模糊查询 添加生效日判断和票据池签约生效判断
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
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		
		// 增加机构筛选条件
			if (user != null && user.getDepartment() != null) {
				// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
				if (!PublicStaticDefineTab.isRootDepartment(user)) {
					List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:officeNetList) ");
					keyList.add("officeNetList");
					valueList.add(resultList);
				}else 
					if(StringUtils.isNotBlank(pedProtocolDto.getOfficeNet())){//总行用户查询签约机构可选
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
			// 票据池协议生效日期开始
			if ( null!=pedProtocolDto.getEffstartdate()) {
				sb.append(" and ppd.effstartdate>=:effstartdate ");
				keyList.add("effstartdate");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getEffstartdate()));
			}
			// 票据池协议生效日期结束
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
		if (page != null) { // 有分页对象
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
	 * 查询,带模糊查询
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
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
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
			// 增加机构筛选条件
			if(user != null && user.getDepartment() != null) {
				// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
				if(!PublicStaticDefineTab.isRootDepartment(user)) {
					List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:officeNetList) ");
					keyList.add("officeNetList");
					valueList.add(resultList);
				}else if(StringUtils.isNotBlank(pedProtocolDto.getOfficeNet())){//总行用户查询签约机构可选
					Department dept=departmentService.getDeptById(pedProtocolDto.getOfficeNet());
					List resultList = departmentService.getAllChildrenInnerCodeList(dept.getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:officeNetList) ");
					keyList.add("officeNetList");
					valueList.add(resultList);
				}
			}
			// 票据池协议生效日期开始
			if ( null!=pedProtocolDto.getEffstartdate()) {
				sb.append(" and ppd.effstartdate>=:effstartdate ");
				keyList.add("effstartdate");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getEffstartdate()));
			}
			// 票据池协议生效日期结束
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
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		return list;
	}
	
	/**
	 * 转贴现卖出 审批查询 卖出方查询
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
		
		/*检查数据库中该客户名下有无数据，若有则删除*/
		AssetPool poolOld = pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(pedProtocolDto.getPoolAgreement(),null, custNo);
		if(poolOld!=null){
			AssetType typeOld = null;
			logger.info("删除AssetPool,AssetPool主键为:"+poolOld.getApId()+"票据池编号为:"+pedProtocolDto.getPoolAgreement());
			pedAssetPoolService.txDelete(poolOld);
			List<AssetType> typeList= pedAssetTypeService.queryPedAssetTypeByAssetPool(poolOld);	
			if(typeList!=null&&typeList.size()>0){
				for(int i=0;i<typeList.size();i++){
					typeOld = typeList.get(i)  ;
					pedAssetTypeService.txDelete(typeOld);
				}
			}
		}
		
		//创建assetPool信息

		AssetPool ap = new AssetPool();

		ap.setApName("票据池");
		ap.setPoolType(PoolComm.ZCC_PJC);
		ap.setCustId(pedProtocolDto.getPoolInfoId());// 协议主键id
		ap.setCustName(pedProtocolDto.getCustname());// 4.客户名称
		ap.setCustNo(pedProtocolDto.getCustnumber());// 3.客户号
		ap.setCustOrgcode(pedProtocolDto.getCustOrgcode());//客户组织机构代码
//		ap.setCrtOptid(user.getId());
		ap.setBpsNo(pedProtocolDto.getPoolAgreement());//票据池编号
        ap.setDealStatus("DS_002");//未处理
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(new Date());
		Date date = sdf.parse(dateStr);
		ap.setCrtTm(date);
		pedAssetPoolService.txStore(ap);
		logger.info("创建AssetPool,AssetPool主键为:"+ap.getApId()+"票据池主键为:"+pedProtocolDto.getPoolInfoId()+"票据池编号为:"+pedProtocolDto.getPoolAgreement());
		
	}

	@Override
	public void txCreateAssetTypeInfo(PedProtocolDto pedProtocolDto,String assetType)
			throws Exception {
		//创建AssetType信息
		AssetType at = new AssetType();
		BigDecimal zero = new BigDecimal("0");
			if(PoolComm.ED_PJC.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("低风险票据额度");
			}else if(PoolComm.ED_PJC_01.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("高风险票据额度");
			}else if(PoolComm.ED_BZJ_DQ.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("定期保证金额度");
			}else if(PoolComm.ED_BZJ_HQ.equals(assetType)){
				at.setAstType(assetType);
				at.setAsstName("活期保证金额度");
			
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
	 * 根据marginAccount删除BailDetail中的数据
	 * @param marginAccount   
	 * @author Ju Nana
	 * @date 2019-2-20 下午6:58:44
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
					Date dueDate = pro.getEffenddate();//协议到期日
					Date today = new Date();//当前日期
					Calendar cal = Calendar.getInstance();
					cal.setTime(today);
					cal.add(Calendar.DATE,1);//当前日期后一天
					Date todayNext =  cal.getTime();
					
					if(dueDate!=null  ){
						if(todayNext.getTime()>dueDate.getTime()){//如果明天>到期日则续约一年
							cal.add(Calendar.YEAR, 1);//增加一年
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
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select proList from PedProtocolList as proList where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getBpsNo())){//票据池编号
				hql.append(" and proList.bpsNo =:bpsNo");
				paramName.add("bpsNo");
				paramValue.add(queryBean.getBpsNo());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustNo())){//核心客户号
				hql.append(" and proList.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNo());
			}
			if(queryBean.getCustNos()!=null && queryBean.getCustNos().size()>0){//核心客户号list
				hql.append(" and proList.custNo in (:custNos)");
				paramName.add("custNos");
				paramValue.add(queryBean.getCustNos());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOrgCoge())){//组织机构代码
				hql.append(" and proList.orgCoge =:orgCoge");
				paramName.add("orgCoge");
				paramValue.add(queryBean.getOrgCoge());
			}
			
			if(StringUtil.isNotBlank(queryBean.getStatus())){//签约状态    00：未签约  01：已签约    02：已解约
				hql.append(" and proList.status =:status");
				paramName.add("status");
				paramValue.add(queryBean.getStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getFinancingStatus())){//融资人生效标志	FS_01 生效   FS_00 失效
				hql.append(" and proList.financingStatus =:financingStatus");
				paramName.add("financingStatus");
				paramValue.add(queryBean.getFinancingStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getRole())){// 角色   JS_01：主户     JS_02：分户
				hql.append(" and proList.role =:role");
				paramName.add("role");
				paramValue.add(queryBean.getRole());
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustIdentity())){//客户身份  KHLX_01:出质人  KHLX_02:融资人   KHLX_03:出质人+融资人  KHLX_04:签约成员
				hql.append(" and proList.custIdentity =:custIdentity");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentity());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////是否自动入池  01   不自动 00
				hql.append(" and proList.zyFlag =:zyFlag");
				paramName.add("zyFlag");
				paramValue.add(queryBean.getZyflag());
			}
			
			if(queryBean.getStautsList()!=null&&queryBean.getStautsList().size()>0){////签约状态
				hql.append(" and proList.status in (:statusList)");
				paramName.add("statusList");
				paramValue.add(queryBean.getStautsList());
			}
			
			if(queryBean.getCustIdentityList()!=null&&queryBean.getCustIdentityList().size()>0){//客户身份
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
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select proList from PedProtocolList as proList where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getBpsNo())){//票据池编号
				hql.append(" and proList.bpsNo =:bpsNo");
				paramName.add("bpsNo");
				paramValue.add(queryBean.getBpsNo());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustNo())){//核心客户号
				hql.append(" and proList.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNo());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOrgCoge())){//组织机构代码
				hql.append(" and proList.orgCoge =:orgCoge");
				paramName.add("orgCoge");
				paramValue.add(queryBean.getOrgCoge());
			}
			
			if(StringUtil.isNotBlank(queryBean.getStatus())){//签约状态    00：未签约  01：已签约    02：已解约
				hql.append(" and proList.status =:status");
				paramName.add("status");
				paramValue.add(queryBean.getStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getFinancingStatus())){//融资人生效标志	FS_01 生效   FS_00 失效
				hql.append(" and proList.financingStatus =:financingStatus");
				paramName.add("financingStatus");
				paramValue.add(queryBean.getFinancingStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getRole())){// 角色   JS_01：主户     JS_02：分户
				hql.append(" and proList.role =:role");
				paramName.add("role");
				paramValue.add(queryBean.getRole());
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustIdentity())){//客户身份  KHLX_01:出质人  KHLX_02:融资人   KHLX_03:出质人+融资人  KHLX_04:签约成员
				hql.append(" and proList.custIdentity =:custIdentity");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentity());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////是否自动入池  01   不自动 00
				hql.append(" and proList.zyflag =:zyflag");
				paramName.add("zyflag");
				paramValue.add(queryBean.getZyflag());
			}
			
			if(queryBean.getStautsList()!=null&&queryBean.getStautsList().size()>0){////是否自动入池  01   不自动 00
				hql.append(" and proList.status in (:statusList)");
				paramName.add("statusList");
				paramValue.add(queryBean.getStautsList());
			}
			
			if(StringUtil.isNotBlank(queryBean.getEleAccount())){//电票签约账号
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
		
		logger.info("对账校验开始.......................");
		
		Ret ret =  new Ret();
		String str = "";
		boolean canOrNot = true;//是否可以发生融资业务    true：可以   false：不可以
		
		if(PoolComm.NO.equals(protocol.getIsGroup())){//非集团
			canOrNot = this.isEveryCheckPass(protocol.getPoolAgreement(),protocol.getCustnumber());
			if (!canOrNot) {
				ret.setRET_CODE(Constants.CREDIT_10);
				ret.setRET_MSG(protocol.getCustnumber() + ":客户未对账或对账核对不一致！");
				return ret;
			}
		}else{//集团 ：集团用户必须每个集团子户对账无误才可以做用信业务
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
					str = str + mem.getCustNo() +"、";
					continue;
				}
			}
			if (!canOrNot) {
				str = str.substring(0, str.length()-1);
				ret.setRET_CODE(Constants.CREDIT_10);
				ret.setRET_MSG(str + ":客户未对账或对账核对不一致！");
				return ret;
			}
			
		}
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG("对账通过！");
		return ret;
	}
	
	/**
	 * 检查每个客户的对账信息
	 * @Description TODO
	 * @author Ju Nana
	 * @param bpsNo
	 * @param custNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-1上午10:42:19
	 */
	@Override
	public boolean isEveryCheckPass(String bpsNo,String custNo)
			throws Exception {
		
		logger.info("票据池编号【"+bpsNo+"】客户号【"+custNo+"】对账信息校验开始.......................");
		
		boolean result = false;//是否可以融资   true：可以     false：不可以
		Date twoMonAgo = DateTimeUtil.getDateWithMonths(new Date(), -2);//两个月前的日期
		
		
		logger.info("两个月前的时间twoMonAgo："+DateUtils.formatDateToString(twoMonAgo, DateUtils.ORA_DATE_FORMAT));
		
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
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

			if(PoolComm.DZJG_02.equals(resultList.get(0).getCheckResult())){//（2）最新对账结果为“核对不一致”
				result =  false;
			}else{//（1）校验最后一次对账后2个月内无最新未对账记录
				int autoNum = 0 ;//自动生成对账单的笔数
				boolean haveCheckOne =  false ;//是否发生过一笔对账
				
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
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedProtocolDto as dto where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getPoolAgreement())){//票据池编号
				hql.append(" and dto.poolAgreement =:poolAgreement");
				paramName.add("poolAgreement");
				paramValue.add(queryBean.getPoolAgreement());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustnumber())){//核心客户号
				hql.append(" and dto.custnumber =:custnumber");
				paramName.add("custnumber");
				paramValue.add(queryBean.getCustnumber());
			}
			
			if(StringUtil.isNotBlank(queryBean.getIsGroup())){//是否集团	1集团  0单户
				hql.append(" and dto.isGroup =:isGroup");
				paramName.add("isGroup");
				paramValue.add(queryBean.getIsGroup());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOpenFlag())){//签约状态    00：未签约  01：已签约    02：已解约
				hql.append(" and dto.openFlag =:openFlag");
				paramName.add("openFlag");
				paramValue.add(queryBean.getOpenFlag());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////是否自动入池  01   不自动 00
				hql.append(" and dto.zyflag =:zyflag");
				paramName.add("zyflag");
				paramValue.add(queryBean.getZyflag());
			}

			if(StringUtil.isNotBlank(queryBean.getvStatus())){//虚拟票据池签约状态   VS_01：签约    VS_02:解约
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
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select dto from PedProtocolDto as dto where 1=1 ");
		
		if(queryBean!=null){
			
			if(StringUtil.isNotBlank(queryBean.getPoolAgreement())){//票据池编号
				hql.append(" and dto.poolAgreement =:poolAgreement");
				paramName.add("poolAgreement");
				paramValue.add(queryBean.getPoolAgreement());
				
			}
			
			if(StringUtil.isNotBlank(queryBean.getCustnumber())){//核心客户号
				hql.append(" and dto.custnumber =:custnumber");
				paramName.add("custnumber");
				paramValue.add(queryBean.getCustnumber());
			}
			
			if(StringUtil.isNotBlank(queryBean.getIsGroup())){//是否集团	1集团  0单户
				hql.append(" and dto.isGroup =:isGroup");
				paramName.add("isGroup");
				paramValue.add(queryBean.getIsGroup());
			}
			
			if(StringUtil.isNotBlank(queryBean.getOpenFlag())){//签约状态    00：未签约  01：已签约    02：已解约
				hql.append(" and dto.openFlag =:openFlag");
				paramName.add("openFlag");
				paramValue.add(queryBean.getOpenFlag());
			}
			
			if(StringUtil.isNotBlank(queryBean.getvStatus())){//签约状态    VS_01：签约    VS_02:解约
				hql.append(" and dto.vStatus =:vStatus");
				paramName.add("vStatus");
				paramValue.add(queryBean.getvStatus());
			}
			
			if(StringUtil.isNotBlank(queryBean.getZyflag())){////是否自动入池  01   不自动 00
				hql.append(" and dto.zyflag =:zyflag");
				paramName.add("zyflag");
				paramValue.add(queryBean.getZyflag());
			}
			
			if(StringUtil.isNotBlank(queryBean.getContract())){//担保合同不为空
				hql.append(" and dto.contract != 'null'");
			}

			if(queryBean.getContractDueDt() != null && !"".equals(queryBean.getContractDueDt()) ){//担保到期日大于当前日才可入池
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
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
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
		 * 查询自动入池的单户信息
		 */
		ProtocolQueryBean queryBean1 =  new ProtocolQueryBean();
		queryBean1.setCustnumber(custNo);
		queryBean1.setIsGroup(PoolComm.NO);//非集团
		queryBean1.setZyflag(PoolComm.ZY_FLAG_01);//自动入池
		queryBean1.setOpenFlag(PoolComm.OPEN_01);//已签约
		List<PedProtocolDto> pro = this.queryProtocolDtoListByQueryBean(queryBean1);
		
		/*
		 * 查询自动入池的集团户信息
		 */
		ProListQueryBean queryBean2 =  new ProListQueryBean();
		queryBean2.setCustNo(custNo);
		queryBean2.setStatus(PoolComm.PRO_LISE_STA_01);//已签约状态
		queryBean2.setZyflag(PoolComm.ZY_FLAG_01);
		List<PedProtocolList> proMem = this.queryProListByQueryBean(queryBean2); 
		
		/*
		 *	核心客户号传入的时候需要排除掉本票据池 
		 */
		
		if(StringUtil.isNotBlank(bpsNo)){
			
			//无论是单户还是集团只要有签约自动入池，则均返回是
			
			if(pro!=null && pro.size()>0){
				PedProtocolDto dto = pro.get(0);
				if(!dto.getPoolAgreement().equals(bpsNo)){//非本票据池的自动入池状态
					return true;				
				}
			}
			
			if(proMem!=null && proMem.size()>0){
				PedProtocolList mem = proMem.get(0);
				if(!mem.getBpsNo().equals(bpsNo)){//非本票据池的自动入池状态
					return true;				
				}
			}
			
		}else{
			
			//无论是单户还是集团只要有签约自动入池，则均返回是 
			
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
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		StringBuffer hql = new StringBuffer();
		hql.append("select proList from PedProtocolList as proList where 1=1 ");

		if(queryBean!=null){

			if(StringUtil.isNotBlank(queryBean.getBpsNo())){//票据池编号
				hql.append(" and proList.bpsNo =:bpsNo");
				paramName.add("bpsNo");
				paramValue.add(queryBean.getBpsNo());

			}

			if(StringUtil.isNotBlank(queryBean.getCustNo())){//核心客户号
				hql.append(" and proList.custNo =:custNo");
				paramName.add("custNo");
				paramValue.add(queryBean.getCustNo());
			}

			if(StringUtil.isNotBlank(queryBean.getOrgCoge())){//组织机构代码
				hql.append(" and proList.orgCoge =:orgCoge");
				paramName.add("orgCoge");
				paramValue.add(queryBean.getOrgCoge());
			}

			if(StringUtil.isNotBlank(queryBean.getStatus())){//签约状态    00：未签约  01：已签约    02：已解约
				hql.append(" and proList.status =:status");
				paramName.add("status");
				paramValue.add(queryBean.getStatus());
			}

			if(StringUtil.isNotBlank(queryBean.getFinancingStatus())){//融资人生效标志	FS_01 生效   FS_00 失效
				hql.append(" and proList.financingStatus =:financingStatus");
				paramName.add("financingStatus");
				paramValue.add(queryBean.getFinancingStatus());
			}

			if(StringUtil.isNotBlank(queryBean.getRole())){// 角色   JS_01：主户     JS_02：分户
				hql.append(" and proList.role =:role");
				paramName.add("role");
				paramValue.add(queryBean.getRole());
			}

			if(StringUtil.isNotBlank(queryBean.getCustIdentity())){//客户身份  KHLX_01:出质人  KHLX_02:融资人   KHLX_03:出质人+融资人  KHLX_04:签约成员
				hql.append(" and proList.custIdentity =:custIdentity");
				paramName.add("custIdentity");
				paramValue.add(queryBean.getCustIdentity());
			}

			if(StringUtil.isNotBlank(queryBean.getZyflag())){////是否自动入池  01   不自动 00
				hql.append(" and proList.zyFlag =:zyFlag");
				paramName.add("zyFlag");
				paramValue.add(queryBean.getZyflag());
			}

			if(queryBean.getStautsList()!=null&&queryBean.getStautsList().size()>0){////签约状态
				hql.append(" and proList.status in (:statusList)");
				paramName.add("statusList");
				paramValue.add(queryBean.getStautsList());
			}

			if(queryBean.getCustIdentityList()!=null&&queryBean.getCustIdentityList().size()>0){//客户身份
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
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
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
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有信息，分行或一级支行看本辖内，网点查看本网点
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
			// 日期开始
			if (ped.getCurTimeStart() != null && !"".equals(ped.getCurTimeStart())) {
				hql.append(" and ch.curTime>=TO_DATE(:curTimeStart, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("curTimeStart");
				parasValue.add(DateUtils.toString(ped.getCurTimeStart(), "yyyy-MM-dd") + " 00:00:00");
			}
			//日期结束
			if (ped.getCurTimeEnd() != null && !"".equals(ped.getCurTimeEnd())) {
				hql.append(" and ch.curTime<=TO_DATE(:curTimeEnd, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("curTimeEnd");
				parasValue.add(DateUtils.toString(ped.getCurTimeEnd(), "yyyy-MM-dd") + " 23:59:59");
			}			
						
		}
		hql.append(" order by ch.curTime desc  ");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List<PedCheck> result = this.find(hql.toString(), nameForSetVar, parameters,page);		
		if(result!=null && result.size()>0){
			return result;
		}
		return null;	
	}
	@Override
	public List<PedCheckBatch> queryPedCheckBatchNew(PedCheckBatch ped,User user,Page page) throws Exception {
		StringBuffer hql = new StringBuffer("select ch from PedCheckBatch ch,PedProtocolDto ped  where 1=1 and ch.poolAgreement = ped.poolAgreement ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有信息，分行或一级支行看本辖内，网点查看本网点
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
			/*// 日期开始
			if (ped.getCurTimeStart() != null && !"".equals(ped.getCurTimeStart())) {
				hql.append(" and ch.curTime>=TO_DATE(:curTimeStart, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("curTimeStart");
				parasValue.add(DateUtils.toString(ped.getCurTimeStart(), "yyyy-MM-dd") + " 00:00:00");
			}
			//日期结束
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
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
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
		 * 协议查询
		 */
		ProtocolQueryBean queryBean = new  ProtocolQueryBean();
		queryBean.setPoolAgreement(bpsNo);
		PedProtocolDto protocol =  this.queryProtocolDtoByQueryBean(queryBean);
		
		/*
		 * 协议备份
		 */
		PedProtocolHist proHist = new PedProtocolHist();
		BeanUtil.beanCopy(protocol, proHist);
		proHist.setOperateTime(new Date());
		this.txSaveStore(proHist);
		
		if(PoolComm.YES.equals(protocol.getIsGroup())){
			/*
			 * 集团子户查询
			 */
			ProListQueryBean bean = new ProListQueryBean();
			bean.setBpsNo(bpsNo);
			List<PedProtocolList> mems =  this.queryProListByQueryBean(bean);
			if(mems!=null && mems.size()>0){
				for(PedProtocolList proList : mems){					
					/*
					 * 集团子户备份
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
			return "2";//新客户经理Id为空
		}
		
		String deptIdLogon = userLogon.getDeptId();//当前登录柜员的机构号
		
		User userOld =null;//原客户经理信息
		String deptIdOld = null;//原客户经理机构ID
		
		User userNew =null;//新客户经理信息
		String deptIdNew = null;//新客户经理机构ID
		
		/*
		 * 原客户经理信息查询
		 */
		
		userOld = this.queryUserById(accountManagerIdOld);
		if(userOld == null){
			return "4";//原客户经理不存在
		}
		deptIdOld =  userOld.getDeptId();//原客户经理机构ID
		
		/*
		 * 新客户经理信息查询
		 */
		userNew = this.queryUserById(accountManagerId);
		if(userNew == null){
			return "1";//查询客户经理信息不存在！
		}
		deptIdNew = userNew.getDeptId();//新客户经理机构ID
		
		/*
		 * 移交到的必须为客户经理角色
		 */
		boolean isManager = false;//是否为客户经理
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
             return "5";//录入用户信息角色非客户经理
		}
		
		if(!this.deptCheck(deptIdLogon, deptIdOld)){
			return "6";//被移交客户经理不属于登录用户的平级或下辖机构，不允许移交
		}
		
		if(!this.deptCheck(deptIdLogon, deptIdNew)){
			return "7";//新客户经理不属于登录用户的平级或下辖机构，不允许移交
		}
		
		return userNew.getName();
	}
	
	/**
	 * 客户经理移交的机构检查，校验机构是否可移交
	 * @author Ju Nana
	 * @return
	 * @date 2019-10-22上午10:34:12
	 */
	private boolean deptCheck(String deptIdLogon,String deptId){
		
		/*
		 * 如果是同一机构，可以移交
		 */
		if(deptIdLogon.equals(deptId)){
			return true;	
		}
		
		/*
		 * 如果是下辖机构，可以移交
		 */
		
		//查询所有下辖机构
		List<String> underAllList = new ArrayList<String>();//所有下辖机构Id
		
		
		List<String> deptIdUnderList1 = new ArrayList<String>();//第一级下辖机构
		List<String> deptIdUnderList2 = new ArrayList<String>();//第二级下辖机构
		List<String> deptIdUnderList3 = new ArrayList<String>();//第三级下辖机构
		List<String> deptIdUnderList4 = new ArrayList<String>();//第四级下辖机构
		
		/*
		 * 第一级机构查询
		 */
		deptIdUnderList1 = departmentService.getAllChildrenIdList(deptIdLogon, 1);
		
		/*
		 * 第二级机构查询
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
		 * 第三级机构查询
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
		 * 第四级机构查询
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
		

		
		//检查机构ID是否属于下辖机构，如果是，则返回可移交
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
	 * 查询,带模糊查询 添加生效日判断和票据池签约生效判断
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
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		
		// 增加机构筛选条件
			if (user != null && user.getDepartment() != null) {
				// 总行可以看所有签约信息，分行或一级支行看本辖内，网点查看本网点
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
		if (page != null) { // 有分页对象
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
	
	/** 描述:票据池保证金账户开立
     * @param protocol  新签约信息
     * @return Map
     * @throws Exception
     */
    public ReturnMessageNew txMarginAccount(PedProtocolDto protocol) throws Exception {
    	ReturnMessageNew returnMessageNew =new ReturnMessageNew();
    	ReturnMessageNew returnRate = new ReturnMessageNew();
    	
    	//核心利率查询
    	String rate = "";
    	try {			
    		returnRate = poolCoreService.core011Handler( new CoreTransNotes());
    		if (returnRate.isTxSuccess()) {
    			if(null == returnRate.getBody().get("INT_RATE")){
    				returnRate.setTxSuccess(false);
        			returnRate.getRet().setRET_MSG("核心系统利率返回为空！");
        			return returnRate;
    			}else{    				
    				rate = ((String)returnRate.getBody().get("INT_RATE"));//利率
    			}
    		}else{
    			returnRate.setTxSuccess(false);
    			returnRate.getRet().setRET_MSG("核心查询利率失败:"+returnRate.getRet().getRET_MSG());
    			return returnRate;
    		}
    		
		} catch (Exception e) {
			returnRate.setTxSuccess(false);
			returnRate.getRet().setRET_MSG("核心查询利率失败:"+e.getMessage());
			return returnRate;
		}
    	
    	
    	//核心保证金开户
    	
    	try {
	        CoreTransNotes transNotes = new CoreTransNotes();
	        transNotes.setCustIdA(protocol.getCustnumber());//客户号
	        transNotes.setCustNam(protocol.getCustname());//客户名称  
	        
	        //机构使用客户经理的归属机构
	        logger.info("客户经理编号："+protocol.getAccountManagerId()+"............................");
	        User user = userService.getUserByLoginName(protocol.getAccountManagerId());//校验客户经理
	        logger.info("查询的用户的机构信息："+user.getDeptId()+"............................");
	        Department dept = (Department) this.load(user.getDeptId(),Department.class);
	        logger.info("客户经理归属所号："+dept.getInnerBankCode()+"............................");
	        transNotes.setBrcNo(dept.getInnerBankCode());//签约机构
	        
	        //该字段从核心查回来，再送给核心，因为核心不给改动，没办法，卑微的系统，ε=(´ο｀*)))唉
	        transNotes.setRate(rate);	        
	        
	        returnMessageNew = poolCoreService.core006Handler(transNotes);
	        if (returnMessageNew.isTxSuccess()) {
	        	protocol.setMarginAccount((String)returnMessageNew.getBody().get("ACCT_NO"));//账号
	        	protocol.setMarginAccountName("汉口银行保证金（"+protocol.getCustname()+"）");
	            return returnMessageNew;
	        }else{
	            return returnMessageNew;
	        }
    	} catch (Exception e) {
			returnMessageNew.setTxSuccess(false);
			returnMessageNew.getRet().setRET_MSG("票据池保证金账户开立失败:"+e.getMessage());
			logger.info("票据池保证金账户开立失败"+e.getMessage());
			return returnMessageNew;
		}
        
    }
	/** 描述:票据池保证金账户变更
     * @param oldAcc  旧账号
     * @param newAcc 新账号
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
		 list.add(margin);//新增保证金账号变更记录
		 financialAdviceService.txCreateList(list);
    	try {
    		CoreTransNotes transNotes = new CoreTransNotes();
    		transNotes.setDepositAcctNo(oldAcc);//旧票据池保证金账户
    		transNotes.setDrAcctNo(newAcc);//新票据池保证金账户
    		 returnMessageNew = poolCoreService.core010Handler(transNotes);

    		return returnMessageNew;

		} catch (Exception e) {
			returnMessageNew.setTxSuccess(false);
			returnMessageNew.getRet().setRET_MSG("票据池保证金账户变更失败:"+e.getMessage());
			logger.info("票据池保证金账户变更失败"+e.getMessage());
			return returnMessageNew;
		}
    }
	/** 描述:票据池担保合同查询
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
	 * 票据池担保合同查询
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
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (pedProtocolDto != null) {
			
			String str = roleService.queryRoleDeptByUser(user);
			if(str != null){
				if(str.equals("0") || str.equals("2")){//总行管理员;部门总行角色为查询员、管理员、审批员  可查询所有数据
					
				}else if(str.equals("1") || str.equals("3")){//支行管理员;部门分行角色为查询员、管理员、审批员 可查询分支行下所有数据
						//分行或一级支行看本辖内，网点查看本网点
					List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
					sb.append(" and ppd.officeNet in (:branchId) ");
					keyList.add("branchId");
					valueList.add(resultList);
				}else if(str.equals("4")){//客户经理:只可查询票据池协议为业务员为自己的数据
					sb.append(" and ppd.accountManager = :accountManager ");
					keyList.add("accountManager");
					valueList.add(user.getName());
				}else if(str.equals("5")){//授权结算柜员:无数据
					return null;
				}
			}
			
			if (StringUtils.isNotBlank(pedProtocolDto.getPoolAgreement())) {//票据池编号
				sb.append(" and ppd.poolAgreement like :poolAgreement ");
				keyList.add("poolAgreement");
				valueList.add("%" + pedProtocolDto.getPoolAgreement() + "%");
			}
			
			if (StringUtils.isNotBlank(pedProtocolDto.getPoolMode())) {//额度模式
				sb.append(" and ppd.poolMode =:poolMode ");
				keyList.add("poolMode");
				valueList.add(pedProtocolDto.getPoolMode());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getContract())) {//担保合同
				sb.append(" and ppd.contract = :contract ");
				keyList.add("contract");
				valueList.add(pedProtocolDto.getContract());
			}
			if (StringUtils.isNotBlank(pedProtocolDto.getvStatus())) {
				sb.append(" and ppd.vStatus = :vStatus ");
				keyList.add("vStatus");
				valueList.add(pedProtocolDto.getvStatus());
			}
			// 合同起始日期开始
			if ( null!=pedProtocolDto.getEffstartdate()) {
				sb.append(" and ppd.contractEffectiveDt>=:contractEffectiveDt");
				keyList.add("contractEffectiveDt");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getEffstartdate()));
			}
			// 合同起始日期开始结束
			if ( null!=pedProtocolDto.getEffenddate()) {
				sb.append(" and ppd.contractEffectiveDt<=:contractEffectiveDt1");
				keyList.add("contractEffectiveDt1");
				valueList.add(DateUtils.getCurrentDayEndDate(pedProtocolDto.getEffenddate()));
			}
			// 合同到期日期开始
			if ( null!=pedProtocolDto.getContractEffectiveDt()) {
				sb.append(" and ppd.contractDueDt>=:contractDueDt");
				keyList.add("contractDueDt");
				valueList.add(DateUtils.getCurrentDayStartDate(pedProtocolDto.getContractEffectiveDt()));
			}
			// 合同到期日期开始结束
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
		if (page != null) { // 有分页对象
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
			//推送过担保合同的才会变更
			if(poolMode.equals("01")){//保证金支取是否人工审核   0-否  1-是 总量模式默认为是，期限配比模式默认为否
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
	 * @Title 信贷池模式变更通知
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
	
		PlFeeScale  psScale = this.queryFeeScale();//查询票据池服务费标准
		if(null == psScale){
			logger.error("票据池系统没有生效的服务费标准信息，请维护！");
			return false;
			
		}
		
		BigDecimal everyYear = psScale.getEveryYear();//年费标准
		BigDecimal everyPiece = psScale.getEveryPiece();//逐笔收费标准
		
		if((PoolComm.SFMS_01).equals(pro.getFeeType()) ){//年费模式
			
			if( new BigDecimal("0").compareTo(everyYear)==0){	
				
				return true;//年费收费标准为0时候，认为是已收服务费
			}
			
			if(DateUtils.compareDate(DateUtils.getCurrDateTime(),pro.getFeeDueDt())>0){//已到期
				return false;
			}else{
				return true;
			}
		}
		
		if((PoolComm.SFMS_02).equals(pro.getFeeType())){//逐笔收费模式
			
			if(new BigDecimal("0").compareTo(everyYear)==0){
			
				return true;//逐笔收费标准为0时候，认为是已收服务费
				
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
			String prdtNo = null ;//生成的编号
			try {
				dto = (CustomerRegister) this.load(custId,CustomerRegister.class);
				olPrdtSerialNo = dto.getOlPrdtSerialNo();//在线协议主业务合同号序列号
				
				prdtNo = olPrdtSerialNo + "";
				
				int noLength = prdtNo.length();
				if(noLength <= length){
					String prefix = "";
					for(int i=0;i<length - noLength;i++){
						prefix += "0";
					}
					prdtNo = prefix + prdtNo;
				}else{
					throw new Exception("错误——序号长度超过规定上限！");
				}
				
				olPrdtSerialNo = olPrdtSerialNo + 1 ;
				dto.setOlPrdtSerialNo(olPrdtSerialNo);
				this.txStore(dto);
				
			} catch (Exception e) {				
				logger.error("错误——在线业务合同号生成异常:", e);
				throw new Exception("错误：在线业务合同号生成异常！");
				
			}
			
			logger.info("生成在线业务合同序列号【"+prdtNo+"】");
			
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
