package com.mingtech.application.pool.query.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.discount.domain.CenterPlatformBean;
import com.mingtech.application.pool.discount.service.CenterPlatformSysService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayCachePlan;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCacheCrdt;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.query.domain.AcptCheckMainlist;
import com.mingtech.application.pool.query.domain.AcptCheckSublist;
import com.mingtech.application.pool.query.domain.BusiolControlConfig;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.RoleService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.dao.impl.GenericHibernateDao;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 通用查询实现类
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 * @copyright 北明明润（北京）科技有限责任公司
 */
@Service("commonQueryService")
public class CommonQueryServiceImpl extends GenericServiceImpl implements CommonQueryService {
	private static final Logger logger = Logger.getLogger(CommonQueryServiceImpl.class);

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private GenericHibernateDao sessionDao;
	@Autowired
	private RoleService roleService;
	@Autowired
	private CenterPlatformSysService centerPlatformSysService;
	/**
	 * 查询在线业务禁入名单
	 * @Description TODO
	 * @author gcj
	 * @version v1.0
	 * @date 20210607
	 */
	public String loadDebarJSON( CommonQueryBean commonQueryBean, User user, Page page) throws Exception {

		List list = loadDebarList( commonQueryBean, user, page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	/**
	 * 查询在线业务禁入名单
	 * 
	 * @param CommonQueryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadDebarList(CommonQueryBean queryBean, User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pb from PedOnlineBlackInfo pb where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getCustNo())) {//客户号
				sb.append(" and pb.custNo = :custNo ");
				keyList.add("custNo");
				valueList.add(queryBean.getCustNo());
			}
			
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//客户名称
				sb.append(" and pb.custName like(:custName) ");
				keyList.add("custName");
				valueList.add("%"+queryBean.getCustName()+"%");
			}
			if (StringUtils.isNotBlank(queryBean.getCustOrgcode())) {//组织结构代码
				sb.append(" and pb.custOrgcode =:custOrgcode ");
				keyList.add("custOrgcode");
				valueList.add(queryBean.getCustOrgcode());
			}
			if (StringUtils.isNotBlank(queryBean.getDeprtName())) {//报送机构
				sb.append(" and pb.deprtId =:deprtId ");
				keyList.add("deprtId");
				valueList.add(queryBean.getDeprtName());
			}
			if (StringUtils.isNotBlank(queryBean.getStatus())) {//状态 0：失效 1：生效
				sb.append(" and pb.status =:status ");
				keyList.add("status");
				valueList.add(queryBean.getStatus());
			}
			if (StringUtils.isNotBlank(queryBean.getDeprtId())) {//机构
				Department dept=departmentService.getDeptById(queryBean.getDeprtId());
				List resultList = departmentService.getAllChildrenInnerCodeList(dept.getInnerBankCode(), -1);
				sb.append(" and pb.deprtId in (:deprtId) ");
				keyList.add("deprtId");
				valueList.add(resultList);
			}
			
			
			
			
		}
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(!str.equals("0") && !str.equals("2")){//总行管理员可查
				return null;
			}
		}
		sb.append("order by pb.createTime desc ");
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
	 * 商票承兑行名单查询
	 * 
	 * @param pedProtocolDto
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadCommercialList(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select bm from AcptCheckSublist bm where 1=1 ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getAcceptName())) {//承兑人名称
				sb.append(" and bm.acceptName = :acceptName ");
				keyList.add("acceptName");
				valueList.add(queryBean.getAcceptName());
			}
			
			if (StringUtils.isNotBlank(queryBean.getBankNo())) { //开户行行号
				sb.append(" and bm.bankNo =:bankNo ");
				keyList.add("bankNo");
				valueList.add(queryBean.getBankNo());
			}
			if (StringUtils.isNotBlank(queryBean.getBankName())) {//开户行行名
				sb.append(" and bm.bankName =:bankName ");
				keyList.add("bankName");
				valueList.add(queryBean.getBankName());
			}
		}
		sb.append(" order by bm.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	
	/**
	 * 保存或更新商票承兑行名单
	 * @author gcj
	 * @param AcptCheckMainlist
	 * @return
	 * @throws Exception
	 * @date 20210607
	 */
	public String txSavaCommercial(AcptCheckSublist acptList) throws Exception{
		String value="";
		if(acptList.getId()!=null&&!acptList.getId().equals("")){// 更新
			AcptCheckSublist tmpBusi = (AcptCheckSublist) this.load(acptList.getId(),AcptCheckSublist.class);
			tmpBusi.setAcceptName(acptList.getAcceptName());
			tmpBusi.setBankName(acptList.getBankName());
			tmpBusi.setBankNo(acptList.getBankNo());
			tmpBusi.setAccount(acptList.getAccount());
			tmpBusi.setUpdateTime( new Timestamp(System.currentTimeMillis()));
			this.txStore(tmpBusi);
			value="更新商票承兑行名单成功";
		}else{
			acptList.setCreateTime(new Timestamp(System.currentTimeMillis()));
			acptList.setId(null);
			this.txStore(acptList);
			value="保存商票承兑行名单成功";
		}
		return value;
	}
	
	/**
	 * 删除商票承兑行名单
	 * @author gcj
	 * @param busIds  AcptCheckMainlist列表ID
	 * @return
	 * @throws Exception
	 * @date 20210607
	 */
	public String txDeleteCommercial(String busIds) throws Exception {
		String value="";
		String[] ids = busIds.split(",");
		List list = Arrays.asList(ids);
		int size = list.size();
		for(int i=0;i<size;i++){
			AcptCheckMainlist temp = (AcptCheckMainlist)this.load(list.get(i).toString(),AcptCheckMainlist.class);
		    this.txDelete(temp);
		}
		value="删除商票承兑行名单成功";		
		return value;

	}
	
	/**
	 * 票据池资产实点查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadPointList(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pc from PedAssetCrdtDaily pc,PedProtocolDto dto where pc.bpsNo = dto.poolAgreement ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) {//票据池编号
				sb.append(" and pc.bpsNo = :bpsNo ");
				keyList.add("bpsNo");
				valueList.add(queryBean.getBpsNo());
			}
			
			if(null!=queryBean.getCreateDate() && !"".equals(queryBean.getCreateDate())){
				sb.append("and pc.createDate>=TO_DATE(:createDate, 'yyyy-mm-dd hh24:mi:ss')");
				keyList.add("createDate");
				valueList.add(DateUtils.toString(queryBean.getCreateDate(), "yyyy-MM-dd") + " 00:00:00");
				
				sb.append(" and  pc.createDate<=TO_DATE(:createDate1, 'yyyy-mm-dd hh24:mi:ss')");
				keyList.add("createDate1");
				valueList.add(DateUtils.toString(queryBean.getCreateDate(), "yyyy-MM-dd") + " 23:59:59");
			}
		}
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//总行管理员;部门总行角色为查询员、管理员、审批员  可查询所有数据
				
			}else if(str.equals("1") || str.equals("3")){//支行管理员;部门分行角色为查询员、管理员、审批员 可查询分支行下所有数据
					//分行或一级支行看本辖内，网点查看本网点
				List list = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				if(list != null && list.size() > 0){
					sb.append(" and dto.signDeptNo in (:signDeptNo) ");
					keyList.add("signDeptNo");
					valueList.add(list);
				}
			}else if(str.equals("4")){//客户经理:只可查询票据池协议为业务员为自己的数据
				sb.append(" and dto.accountManager = '"+user.getName()+"' ");
			}else if(str.equals("5")){//授权结算柜员:无数据
				return null;
			}
		}
		
		
		sb.append(" order by pc.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	/**
	 * 票据池每日资产查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadPedAssetDaily(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pd from PedAssetCrdtDaily pc ,PedAssetDaily pd where pc.bpsNo=pd.bpsNo and pc.id=pd.batchId  ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) {//票据池编号
				sb.append(" and pc.bpsNo = :bpsNo ");
				keyList.add("bpsNo");
				valueList.add(queryBean.getBpsNo());
			}
			
			if (StringUtils.isNotBlank(queryBean.getId())) {//PedAssetCrdtDaily表 id
				sb.append(" and pd.batchId = :batchId ");
				keyList.add("batchId");
				valueList.add(queryBean.getId());
			}
			if (StringUtils.isNotBlank(queryBean.getBillNo())) {//票号
				sb.append(" and pd.billNo = :billNo ");
				keyList.add("billNo");
				valueList.add(queryBean.getBillNo());
			}
		}
		sb.append(" order by pd.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	/**
	 * 票据池每日融资业务查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadPedCrdtDaily(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pd from PedAssetCrdtDaily pc ,PedCrdtDaily pd where pc.bpsNo=pd.bpsNo and pc.id=pd.batchId ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) {//票据池编号
				sb.append(" and pc.bpsNo = :bpsNo ");
				keyList.add("bpsNo");
				valueList.add(queryBean.getBpsNo());
			}
			
			if (StringUtils.isNotBlank(queryBean.getId())) {//PedAssetCrdtDaily表 id
				sb.append(" and pd.batchId = :batchId ");
				keyList.add("batchId");
				valueList.add(queryBean.getId());
			}
			if (StringUtils.isNotBlank(queryBean.getBillNo())) {//票号
				sb.append(" and pd.billNo = :billNo ");
				keyList.add("billNo");
				valueList.add(queryBean.getBillNo());
			}
			
			if (StringUtils.isNotBlank(queryBean.getLoanNo())) {//借据号
				sb.append(" and pd.loanNo = :loanNo ");
				keyList.add("loanNo");
				valueList.add(queryBean.getLoanNo());
			}
		}
		sb.append(" order by pd.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	/**
	 * 短信信息查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadInformatioNoteList(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select tr from TMessageRecord tr where 1=1 ");
		//sb.append("select distinct(pm),tr,at from PedOnlineMsgInfo pm,TMessageRecord tr,AutoTaskExe at where pm.addresseePhoneNo=tr.phoneNo and at.busiId=tr.id and tr.busiType=pm.onlineProtocolType");

		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getAddresseeName())) {//联系人名称
				sb.append(" and tr.addresseeName = :addresseeName ");
				keyList.add("addresseeName");
				valueList.add(queryBean.getAddresseeName());
			}
			
			if (StringUtils.isNotBlank(queryBean.getAddresseePhoneNo())) { //联系人电话
				sb.append(" and tr.phoneNo =:phoneNo ");
				keyList.add("phoneNo");
				valueList.add(queryBean.getAddresseePhoneNo());
			}
			if (StringUtils.isNotBlank(queryBean.getOnlineNo())) {//在线协议编号
				sb.append(" and tr.onlineNo =:onlineNo ");
				keyList.add("onlineNo");
				valueList.add(queryBean.getOnlineNo());
			}
			if (StringUtils.isNotBlank(queryBean.getBusiId())) {//业务ID
				sb.append(" and tr.id =:id ");
				keyList.add("id");
				valueList.add(queryBean.getBusiId());
			}
		}
		sb.append(" order by tr.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
//		List temp=new ArrayList();
//		for(int i=0;i<list.size();i++){
//			CommonQueryBean query=new CommonQueryBean();
//			Object[] obj= (Object[])list.get(i);
//			TMessageRecord mess= (TMessageRecord)obj[0];
//			query.setId(mess.getId());
//			query.setAddresseeName(mess.getAddresseeName());
//			query.setAddresseePhoneNo(mess.getPhoneNo());
//			query.setOnlineNo(mess.getOnlineNo());
//			query.setStatusName(mess.getSendResultDesc());
//			query.setMsgContent(mess.getMsgContent());
//			temp.add(query);
//		}
		
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	
	/**
	 * 在线流贷支付查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadOnlinePayList(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pc,pp from PlOnlineCrdt pc,PlCrdtPayPlan pp,PedOnlineCrdtProtocol ped where pc.id=pp.crdtId and ped.onlineCrdtNo = pc.onlineCrdtNo ");

		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getOnlineCrdtNo())) {//在线协议编号
				sb.append(" and pc.onlineCrdtNo = :onlineCrdtNo ");
				keyList.add("onlineCrdtNo");
				valueList.add(queryBean.getOnlineCrdtNo());
			}
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//融资申请人名称
				sb.append(" and pc.custName = :custName ");
				keyList.add("custName");
				valueList.add(queryBean.getCustName());
			}
			if (StringUtils.isNotBlank(queryBean.getContractNo())) {//合同号
				sb.append(" and pc.contractNo = :contractNo ");
				keyList.add("contractNo");
				valueList.add(queryBean.getContractNo());
			}
			if (StringUtils.isNotBlank(queryBean.getLoanNo())) {//借据号
				sb.append(" and pc.loanNo = :loanNo ");
				keyList.add("loanNo");
				valueList.add(queryBean.getLoanNo());
			}
			if (StringUtils.isNotBlank(queryBean.getLoanAcctName())) {//付款户名
				sb.append(" and pp.loanAcctName = :loanAcctName ");
				keyList.add("loanAcctName");
				valueList.add(queryBean.getLoanAcctName());
			}
			if (StringUtils.isNotBlank(queryBean.getDeduAcctName())) {//收款人名称
				sb.append(" and pp.deduAcctName = :deduAcctName ");
				keyList.add("deduAcctName");
				valueList.add(queryBean.getDeduAcctName());
			}
			if(StringUtils.isNotBlank(queryBean.getId())){
				sb.append(" and pp.id = :id ");
				keyList.add("id");
				valueList.add(queryBean.getId());
			}
		}
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//总行管理员;部门总行角色为查询员、管理员、审批员  可查询所有数据
				
			}else if(str.equals("1") || str.equals("3")){//支行管理员;部门分行角色为查询员、管理员、审批员 可查询分支行下所有数据
					//分行或一级支行看本辖内，网点查看本网点
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				sb.append(" and ped.signBranchNo in (:signBranchNo) ");
				keyList.add("signBranchNo");
				valueList.add(resultList);
			}else if(str.equals("4")){//客户经理:只可查询票据池协议为业务员为自己的数据
				sb.append(" and ped.appName = :appName ");
				keyList.add("appName");
				valueList.add(user.getName());
			}else if(str.equals("5")){//授权结算柜员:无数据
				return null;
			}
		}
		
		sb.append(" and pp.status in ('P02','P03') order by pp.createDate desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		List temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			PlOnlineCrdt pc= (PlOnlineCrdt)obj[0];
			PlCrdtPayPlan pp= (PlCrdtPayPlan)obj[1];
			query.setId(pp.getId());
			query.setCustName(pc.getCustName());
			query.setLoanAcctName(pp.getLoanAcctName());
			query.setCustNo(pc.getCustNo());
			query.setOnlineNo(pc.getOnlineCrdtNo());
			query.setContractNo(pc.getContractNo());
			query.setLoanNo(pc.getLoanNo());
			query.setLoanAcctNo(pp.getLoanAcctNo());
			query.setAmt(pp.getUsedAmt());
			query.setWaitPayAmt(pp.getWaitPayAmt());
			query.setDeduAcctName(pp.getDeduAcctName());
			query.setDeduAcctNo(pp.getDeduAcctNo());
			query.setDeduBankCode(pp.getDeduBankCode());
			query.setDeduBankName(pp.getDeduBankName());
			query.setLoanAmt(pp.getTotalAmt());
			query.setOperaStatus(pp.getOperaStatus());
//			query.setLoanAmt(pc.getLoanAmt());
			temp.add(query);
		}
		
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(temp, map);

	}
	/**
	 * 在线流贷信息查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadPlOnlineCrdtList(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pc from PlOnlineCrdt pc,PedOnlineCrdtProtocol ped where ped.onlineCrdtNo = pc.onlineCrdtNo ");

		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getOnlineNo())) {//在线协议编号
				sb.append(" and pc.onlineCrdtNo = :onlineCrdtNo ");
				keyList.add("onlineCrdtNo");
				valueList.add(queryBean.getOnlineNo());
			}
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//融资申请人名称
				sb.append(" and pc.custName = :custName ");
				keyList.add("custName");
				valueList.add(queryBean.getCustName());
			}
			if (StringUtils.isNotBlank(queryBean.getContractNo())) {//合同号
				sb.append(" and pc.contractNo = :contractNo ");
				keyList.add("contractNo");
				valueList.add(queryBean.getContractNo());
			}
			if(queryBean.getCrdtType().equals("2")){
				//管理查询
				sb.append(" and ped.appName = :appName ");
				keyList.add("appName");
				valueList.add(user.getName());
			}else if(queryBean.getCrdtType().equals("1")){
				//普通查询
				String str = roleService.queryRoleDeptByUser(user);
				if(str != null){
					if(str.equals("0") || str.equals("2")){//总行管理员;部门总行角色为查询员、管理员、审批员  可查询所有数据
						
					}else if(str.equals("1") || str.equals("3")){//支行管理员;部门分行角色为查询员、管理员、审批员 可查询分支行下所有数据
							//分行或一级支行看本辖内，网点查看本网点
						List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
						sb.append(" and ped.signBranchNo in (:signBranchNo) ");
						keyList.add("signBranchNo");
						valueList.add(resultList);
					}else if(str.equals("4")){//客户经理:只可查询票据池协议为业务员为自己的数据
						sb.append(" and ped.appName = :appName ");
						keyList.add("appName");
						valueList.add(user.getName());
					}else if(str.equals("5")){//授权结算柜员:无数据
						return null;
					}
				}
			}
		}
		
		
		sb.append(" order by pc.createTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		
		
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list, map);

	}
	/**
	 * 在线流贷支付查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadOnlinePayByBeanExp(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pc,pp from PlOnlineCrdt pc,PlCrdtPayPlan pp where pc.id=pp.crdtId ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		List lists = new ArrayList(); 

		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getOnlineNo())) {//在线协议编号
				sb.append(" and pc.onlineCrdtNo = :onlineCrdtNo ");
				keyList.add("onlineCrdtNo");
				valueList.add(queryBean.getOnlineNo());
			}
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//融资申请人名称
				sb.append(" and pp.loanAcctName = :loanAcctName ");
				keyList.add("loanAcctName");
				valueList.add(queryBean.getCustName());
			}
			if (StringUtils.isNotBlank(queryBean.getContractNo())) {//合同号
				sb.append(" and pc.contractNo = :contractNo ");
				keyList.add("contractNo");
				valueList.add(queryBean.getContractNo());
			}
			if (StringUtils.isNotBlank(queryBean.getLoanNo())) {//借据号
				sb.append(" and pc.loanNo = :loanNo ");
				keyList.add("loanNo");
				valueList.add(queryBean.getLoanNo());
			}
			if (StringUtils.isNotBlank(queryBean.getLoanAcctName())) {//付款户名
				sb.append(" and pp.loanAcctName = :loanAcctName ");
				keyList.add("loanAcctName");
				valueList.add(queryBean.getLoanAcctName());
			}
			if (StringUtils.isNotBlank(queryBean.getDeduAcctName())) {//收款人名称
				sb.append(" and pp.deduAcctName = :deduAcctName ");
				keyList.add("deduAcctName");
				valueList.add(queryBean.getDeduAcctName());
			}
			if(StringUtils.isNotBlank(queryBean.getId())){
				sb.append(" and pp.id in(:id) ");
				keyList.add("id");
				valueList.add(Arrays.asList(queryBean.getId().split(",")));
			}
		}
		sb.append(" order by pp.createDate desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		List temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			PlOnlineCrdt pc= (PlOnlineCrdt)obj[0];
			PlCrdtPayPlan pp= (PlCrdtPayPlan)obj[1];
			query.setId(pp.getId());
			query.setLoanAcctName(pp.getLoanAcctName());
			query.setCustNo(pc.getCustNo());
			query.setOnlineNo(pc.getOnlineCrdtNo());
			query.setContractNo(pc.getContractNo());
			query.setLoanNo(pc.getLoanNo());
			query.setLoanAcctNo(pp.getLoanAcctNo());
			query.setSurpluslAmt(pp.getWaitPayAmt());
			query.setDeduAcctName(pp.getDeduAcctName());
			query.setDeduAcctNo(pp.getDeduAcctNo());
			query.setDeduBankCode(pp.getDeduBankCode());
			query.setDeduBankName(pp.getDeduBankName());
			query.setLoanAmt(pp.getTotalAmt());
			query.setAmt(pp.getUsedAmt());
			temp.add(query);
		}
		if (temp != null && temp.size() > 0) {
			for (int i = 0; i < temp.size(); i++) {
				String s[] = new String[14];
				CommonQueryBean bill = (CommonQueryBean) temp.get(i);
				s[0]=bill.getLoanAcctName();
				s[1]=bill.getCustNo();
				s[2]=bill.getOnlineNo();
				s[3]=bill.getContractNo();
				s[4]=bill.getLoanNo();
				s[5]=bill.getLoanAcctName();
				s[6]=bill.getLoanAcctNo();
				s[7]=String.valueOf(bill.getLoanAmt());
				s[8]=String.valueOf(bill.getSurpluslAmt());
				s[9]=String.valueOf(bill.getAmt());
				s[10]=bill.getDeduAcctName();
				s[11]=bill.getDeduAcctNo();
				s[12]=bill.getDeduBankName();
				s[13]=bill.getDeduBankCode();
				lists.add(s);
			}
			}
		
		return lists;

	}
	/**
	 * 在线流贷支付查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadOnlinePayHisByBeanExp(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pc,pp from PlOnlineCacheCrdt pc,PlCrdtPayCachePlan pp where pc.id=pp.crdtId ");
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		List lists = new ArrayList(); 

		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getOnlineNo())) {//在线协议编号
				sb.append(" and pc.onlineCrdtNo = :onlineCrdtNo ");
				keyList.add("onlineCrdtNo");
				valueList.add(queryBean.getOnlineNo());
			}
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//融资申请人名称
				sb.append(" and pp.loanAcctName = :loanAcctName ");
				keyList.add("loanAcctName");
				valueList.add(queryBean.getCustName());
			}
			if (StringUtils.isNotBlank(queryBean.getContractNo())) {//合同号
				sb.append(" and pc.contractNo = :contractNo ");
				keyList.add("contractNo");
				valueList.add(queryBean.getContractNo());
			}
			if (StringUtils.isNotBlank(queryBean.getLoanNo())) {//借据号
				sb.append(" and pc.loanNo = :loanNo ");
				keyList.add("loanNo");
				valueList.add(queryBean.getLoanNo());
			}
			if (StringUtils.isNotBlank(queryBean.getLoanAcctName())) {//付款户名
				sb.append(" and pp.loanAcctName = :loanAcctName ");
				keyList.add("loanAcctName");
				valueList.add(queryBean.getLoanAcctName());
			}
			if (StringUtils.isNotBlank(queryBean.getDeduAcctName())) {//收款人名称
				sb.append(" and pp.deduAcctName = :deduAcctName ");
				keyList.add("deduAcctName");
				valueList.add(queryBean.getDeduAcctName());
			}
			if(StringUtils.isNotBlank(queryBean.getId())){
				sb.append(" and pp.id in(:id) ");
				keyList.add("id");
				valueList.add(Arrays.asList(queryBean.getId().split(",")));
			}
		}
		sb.append(" order by pp.createDate desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		List temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			PlOnlineCacheCrdt pc= (PlOnlineCacheCrdt)obj[0];
			PlCrdtPayCachePlan pp= (PlCrdtPayCachePlan)obj[1];
			query.setId(pp.getId());
			query.setLoanAcctName(pp.getLoanAcctName());
			query.setCustNo(pc.getCustNo());
			query.setOnlineNo(pc.getOnlineCrdtNo());
			query.setContractNo(pc.getContractNo());
			query.setLoanNo(pc.getLoanNo());
			query.setLoanAcctNo(pp.getLoanAcctNo());
			query.setSurpluslAmt(pp.getTotalAmt().subtract(pp.getUsedAmt()));
			query.setDeduAcctName(pp.getDeduAcctName());
			query.setDeduAcctNo(pp.getDeduAcctNo());
			query.setDeduBankCode(pp.getDeduBankCode());
			query.setDeduBankName(pp.getDeduBankName());
			temp.add(query);
		}
		if (temp != null && temp.size() > 0) {
			for (int i = 0; i < temp.size(); i++) {
				String s[] = new String[12];
				CommonQueryBean bill = (CommonQueryBean) temp.get(i);
				s[0]=bill.getLoanAcctName();
				s[1]=bill.getCustNo();
				s[2]=bill.getOnlineNo();
				s[3]=bill.getContractNo();
				s[4]=bill.getLoanNo();
				s[5]=bill.getLoanAcctName();
				s[6]=bill.getLoanAcctNo();
				s[7]=String.valueOf(bill.getSurpluslAmt());
				s[8]=bill.getDeduAcctName();
				s[9]=bill.getDeduAcctNo();
				s[10]=bill.getDeduBankName();
				s[11]=bill.getDeduBankCode();
				lists.add(s);
			}
			}
		
		return lists;

	}
	
public List queryDisolControlConfigList(BusiolControlConfig config, Page page) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		sb.append("select dto from BusiolControlConfig as dto where 1=1 ");
		
		
		if (null != config) {
			if(StringUtil.isNotBlank(config.getBusiType())){
				sb.append(" and dto.busiType =?"); //业务类型
				param.add(config.getBusiType());	
			}
			if (StringUtil.isNotBlank(config.getConCode())) {//编码
				sb.append(" and dto.conCode=?");
				param.add(config.getConCode());
			}
			if (StringUtil.isNotBlank(config.getState())) {//状态
				sb.append(" and dto.state=?");
				param.add(config.getState());
			}
			if (StringUtil.isNotBlank(config.getConName())) {//名称
				sb.append(" and dto.conName like ?");
				param.add("%" + config.getConName() + "%");
			}
		}
		sb.append(" order by dto.order ");
		return this.find(sb.toString(), param, page);
	}
	
	
	/**
	 * 保存控制管理
	 * @param keys
	 * @param values
	 * @throws Exception
	 */
	public void txSaveControlConfig(Map<String,String> map,User user) throws Exception{
		Iterator entries = map.entrySet().iterator();

		while (entries.hasNext()) {
			Entry<String, String> entry = (Entry<String, String>) entries.next();
			
			String sql = "update t_config set c_item = '" + entry.getValue() +"'  where c_code = '" + entry.getKey() + "'";
	    	
			dao.updateSQL(sql);
		}

	    //	调用中台在线业务开关信息变更通知接口
	    CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
	    centerPlatformBean.setBusi_id(map.get("ONLINE_BUSS_ID"));
	    centerPlatformBean.setTx_id(map.get("ONLINE_DISCOUNT_BUSS_ID"));
	    centerPlatformBean.setBusiFlag(map.get("OL_OPEN_PJC"));
	    centerPlatformBean.setTxFlag(map.get("OL_OPEN_TX"));
	    centerPlatformBean.setTxFlagBeginTime(map.get("OL_OPENTIME_TX").replaceAll(":", ""));
	    centerPlatformBean.setTxFlagEndTime(map.get("OL_ENDTIME_TX").replaceAll(":", ""));
	    //	修改总配置信息和在线贴现配置信息
	    boolean flag = centerPlatformSysService.txChangeOnlineConfig(centerPlatformBean,user);
	    
	    if(flag){
	    	queryAndUpdateConfig(user);
	    }
	}
	
	private void queryAndUpdateConfig(User user) throws Exception{
		CenterPlatformBean centerPlatformBean = new CenterPlatformBean();
		//		获取修改后信息   并保存到本地 
    	ReturnMessageNew returnMessageNew = centerPlatformSysService.txQueryOnlineConfig(centerPlatformBean,user);
    	
    	Map bodyMap = returnMessageNew.getBody();
    	//	修改总开关
    	String sql1 = "update t_config set id = '" +  bodyMap.get("ONLINE_BUSS_ID") + "', c_item = '" + bodyMap.get("ONLINE_BUSS_STATUS") +"'  where c_code = 'OL_OPEN_PJC'  ";
    	//	修改在线贴现业务总开关
    	String sql2 = "update t_config set id = '" +  bodyMap.get("ONLINE_DISCOUNT_BUSS_ID") + "', c_item = '" + bodyMap.get("ONLINE_DISCOUNT_BUSS_STATUS") +"'  where c_code = 'OL_OPEN_TX'  ";
    	//	修改在线贴现开始时间
    	String sql3 = "update t_config set c_item = '" + bodyMap.get("ONLINE_DISCOUNT_START_TIME") +"'  where c_code = 'OL_OPENTIME_TX'  ";
    	//	    修改在线贴现结束时间
    	String sql4 = "update t_config set c_item = '" + bodyMap.get("ONLINE_DISCOUNT_END_TIME") +"'  where c_code = 'OL_ENDTIME_TX'  ";
    
    	dao.updateSQL(sql1);
    	dao.updateSQL(sql2);
    	dao.updateSQL(sql3);
    	dao.updateSQL(sql4);
	}
	
	/**
	 *票据池合同协议表查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadPoolQuotaList(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pp,cp from PedProtocolDto pp,CreditProduct cp where pp.poolAgreement=cp.bpsNo ");

		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) {//票据池编号
				sb.append(" and pp.poolAgreement like :poolAgreement ");
				keyList.add("poolAgreement");
				valueList.add("%"+queryBean.getBpsNo()+"%");
			}
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//融资申请人名称
				sb.append(" and cp.custName like :custName ");
				keyList.add("custName");
				valueList.add("%"+queryBean.getCustName()+"%");
			}
			if (StringUtils.isNotBlank(queryBean.getContractNo())) {//合同号
				sb.append(" and cp.crdtNo like :contract ");
				keyList.add("contract");
				valueList.add("%"+queryBean.getContractNo()+"%");
			}
		}
		
		sb.append(" and cp.sttlFlag = :sttlFlag ");
		keyList.add("sttlFlag");
		valueList.add(PoolComm.JQ_01);//未结清的
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			//调整池额度系数
			if(!str.equals("0") && !str.equals("2")){//总行管理员可查询所有数据
				return null;
			}
		}else{
			return null;
		}
		
		
		sb.append(" order by pp.operateTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		List temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			PedProtocolDto pp= (PedProtocolDto)obj[0];
			CreditProduct cp= (CreditProduct)obj[1];
			query.setId(cp.getId());
			query.setContractNo(cp.getCrdtNo());
			query.setBpsNo(pp.getPoolAgreement());
			query.setCustName(cp.getCustName());
			query.setCrdtTypeName(cp.getCrdtTypeName());
			query.setCrdtType(cp.getCrdtType());
			query.setCcupy(cp.getCcupy());
			query.setCreditamount(cp.getUseAmt());
			query.setStartDate(cp.getCrdtIssDt());
			query.setEndDate(cp.getCrdtDueDt());
			temp.add(query);
		}
		
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(temp, map);

	}
	

	/**
	 *票据池合同协议表查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List loadPoolQuotaToExpt(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select pp,cp from PedProtocolDto pp,CreditProduct cp where pp.poolAgreement=cp.bpsNo ");
		List lists = new ArrayList(); 
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getBpsNo())) {//票据池编号
				sb.append(" and pp.poolAgreement = :poolAgreement ");
				keyList.add("poolAgreement");
				valueList.add(queryBean.getBpsNo());
			}
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//融资申请人名称
				sb.append(" and cp.custName = :custName ");
				keyList.add("custName");
				valueList.add(queryBean.getCustName());
			}
			if (StringUtils.isNotBlank(queryBean.getContractNo())) {//合同号
				sb.append(" and cp.crdtNo = :contract ");
				keyList.add("contract");
				valueList.add(queryBean.getContractNo());
			}
		}
		sb.append(" order by pp.operateTime desc ");
		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象
			list = this
					.find(sb.toString(), keyArray, valueList.toArray(), page);
		} else {
			list = this.find(sb.toString(), keyArray, valueList.toArray());
		}
		
		List temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			PedProtocolDto pp= (PedProtocolDto)obj[0];
			CreditProduct cp= (CreditProduct)obj[1];
			query.setId(cp.getId());
			query.setContractNo(pp.getContract());
			query.setBpsNo(pp.getPoolAgreement());
			query.setCustName(cp.getCustName());
			query.setCrdtTypeName(cp.getCrdtTypeName());
			query.setCrdtType(cp.getCrdtType());
			query.setCcupy(cp.getCcupy());
			query.setCreditamount(pp.getCreditamount());
			query.setContractEffectiveDt(pp.getContractEffectiveDt());
			query.setContractDueDt(pp.getContractDueDt());
			temp.add(query);
		}
		
		if (temp != null && temp.size() > 0) {
			for (int i = 0; i < temp.size(); i++) {
				String s[] = new String[8];
				CommonQueryBean bill = (CommonQueryBean) temp.get(i);
				s[0]=bill.getContractNo();
				s[1]=bill.getBpsNo();
				s[2]=bill.getCustName();
				s[3]=bill.getCrdtTypeName();
				s[4]=bill.getCcupy();
				s[5]=String.valueOf(bill.getCreditamount());
				s[6]=DateUtils.toString(bill.getContractEffectiveDt(), "yyyy-MM-dd");
				s[7]=DateUtils.toString(bill.getContractDueDt(), "yyyy-MM-dd");
				lists.add(s);
			 }
		}

		return lists;

	}
	
	/**
	 *  获取开户行名称
	 * 
	 * @param bankNo 行号
	 * @return String  行名
	 * @throws Exception
	 */
	public String queryAcceptNameJson(String bankNo) throws Exception {
        String s;
		StringBuffer sb = new StringBuffer();
		sb.append("select bc.ptcptnm from BoCcmsPartyinf bc where 1=1 ");

		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		sb.append(" and bc.prcptcd = :prcptcd ");
		keyList.add("prcptcd");
		valueList.add(bankNo);
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = new ArrayList();
		list = this.find(sb.toString(), keyArray, valueList.toArray());
		if(list.size()==1){
			s=(String)list.get(0);
		}else{
			s=null;
			throw new Exception("根据行号获取行名失败");
		}
		return JsonUtil.fromString(s);

	}
	
	/**
	 *票据池签约客户查询
	 * 
	 * @param queryBean
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public String loadCustomerRegisterList(CommonQueryBean queryBean,User user, Page page) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("select cust.id,cust.CUST_NAME,cust.CUST_NO,pro.V_STATUS,pro.OPEN_FLAG,pro.FROZENSTATE,crdt.PROTOCOL_STATUS as crPro,acpt.PROTOCOL_STATUS as acPro,cust.FIRST_SIGN_DATE,pro.SIGN_DEPT_NAME,crdt.SIGN_BRANCH_NAME as crName,acpt.SIGN_BRANCH_NAME as acName,pro.P_POOLAGREEMENT,cust.AUTH_BRANCH_NO  from BO_CUSTOMER_REGISTER cust left join PED_PROTOCOL pro on cust.CUST_NO=pro.P_CUSTNUMBER and pro.P_ISGROUP='0' " +
				"left join PED_ONLINE_ACPT_PROTOCOL acpt on acpt.CUST_NUMBER=pro.P_CUSTNUMBER and acpt.PROTOCOL_STATUS ='1' " +
				"left join PED_ONLINE_CRDT_PROTOCOL crdt on crdt.CUST_NUMBER=pro.P_CUSTNUMBER and crdt.PROTOCOL_STATUS ='1'  where 1=1 ");


		
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		if(user != null){
//			List roles = roleService.getRoleByBean(user);
//			
//			
//			for (Object object : roles) {
//				String code = (String) object;
//				if(code.equals("A00000")){//超级管理员
//					flag = true;
//				}
//			}
			
			String str = roleService.queryRoleDeptByUser(user);
			if(str != null){
				if(str.equals("0") || str.equals("2")){//管理员;部门总行角色为查询员、管理员、审批员  可查询所有数据
					
				}else if(str.equals("1") || str.equals("3")){//支行管理员;部门分行角色为查询员、管理员、审批员 可查询分支行下所有数据
						//分行或一级支行看本辖内，网点查看本网点
					String sql = departmentService.queryDeptInnerbankcode(user.getDepartment().getInnerBankCode(), -1);
					if(StringUtils.isNotEmpty(sql)){
						sb.append(" and pro.SIGN_DEPT_NO in ("+sql+") ");
					}
				}else if(str.equals("4")){//客户经理:只可查询票据池协议为业务员为自己的数据
					sb.append(" and pro.ACCOUNT_MANAGER = '"+user.getName()+"' ");
				}else if(str.equals("5")){//授权结算柜员
					
				}
			}
			
			
		}
		if (queryBean != null) {
			if (StringUtils.isNotBlank(queryBean.getCustNo())) {//客户号
				sb.append(" and cust.CUST_NO = '" +queryBean.getCustNo()+"'");
			}
			if (StringUtils.isNotBlank(queryBean.getCustName())) {//客户名称
				sb.append(" and cust.CUST_NAME = '" +queryBean.getCustName()+"'");
			}
			
		}

		String[] keyArray = (String[]) keyList.toArray(new String[keyList
				.size()]);
		List list = new ArrayList();
		if (page != null) { // 有分页对象 sql
			list = sessionDao.SQLQuery(sb.toString(),page);
		} else {
			list = sessionDao.SQLQuery(sb.toString());
		}
		
		List temp=new ArrayList();
		for(int i=0;i<list.size();i++){
			CommonQueryBean query=new CommonQueryBean();
			Object[] obj= (Object[])list.get(i);
			query.setId((String)obj[0]);
			query.setCustName((String)obj[1]);
			query.setCustNo((String)obj[2]);
			query.setStatus((String)obj[3]);
			query.setOpenFlag((String)obj[4]);
			query.setFrozenstate((String)obj[5]);
			if(null!=(String)obj[6]){
				query.setProtocolStatus((String)obj[6]);
				query.setIfCrdt("0");
			}else{
				query.setIfCrdt("1");
				query.setProtocolStatus("0");
			}
			if(null!=(String)obj[7]){
				query.setProtocolStatus1((String)obj[7]);
				query.setIfAcpt("0");
			}else{
				query.setIfAcpt("1");
				query.setProtocolStatus1("0");
			}
            Date date=(Date)obj[8];
			query.setFirstSignDate(date);
			query.setBpsNo((String)obj[12]);
			if(((String)obj[13]).equals("PJC010")||((String)obj[13]).equals("PJC033")){
				query.setSignDeptName((String)obj[9]);
			}else if(((String)obj[13]).equals("PJE014")){
				if(StringUtils.isBlank((String)obj[11])){
					query.setSignDeptName((String)obj[9]);
				}else{
					query.setSignDeptName((String)obj[11]);
				}
			}else if(((String)obj[13]).equals("PJE015")){
				if(StringUtils.isBlank((String)obj[10])){
					query.setSignDeptName((String)obj[9]);
				}else{
					query.setSignDeptName((String)obj[10]);
				}			}
			temp.add(query);
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(temp, map);

	}
	
	
public List<SystemConfig> queryControlConfigList(SystemConfig config, Page page, User user) throws Exception {
		queryAndUpdateConfig(user);	
	
		StringBuffer sb = new StringBuffer();
		List param = new ArrayList();
		List valueList = new ArrayList(); // 要查询的值列表
		List keyList = new ArrayList(); // 要查询的字段列表
		sb.append("select dto from SystemConfig as dto where 1=1 ");
		List codeList=new ArrayList();
		codeList.add("OL_OPEN_PJC");
		codeList.add("OL_OPEN_YC");
		codeList.add("OL_OPEN_LD");
		codeList.add("OL_OPENTIME_YC");
		codeList.add("OL_ENDTIME_YC");
		codeList.add("OL_OPENTIME_LD");
		codeList.add("OL_ENDTIME_LD");
		codeList.add("OL_OPEN_JG");
		codeList.add("OL_OPENTIME_TX");
		codeList.add("OL_ENDTIME_TX");
		codeList.add("OL_OPEN_TX");
		sb.append(" and dto.code in (:codeList)"); //控制参数
		keyList.add("codeList");
		valueList.add(codeList);
		sb.append(" order by dto.id asc ");
		
		String str = roleService.queryRoleDeptByUser(user);
		if(str != null){
			if(str.equals("0") || str.equals("2")){//总行管理员可查
				String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
				List<SystemConfig> confList = this.find(sb.toString(), keyArray, valueList.toArray(), page);
				return confList;
			}
		}
		return null;
	}
   
		/**
		 * list 类型转换 QueryResult
		 * 
		 * @param list 
		 * @return
		 * @throws Exception
		 */
		public QueryResult  loadDataByResult(List list ,String amtName) throws Exception {
			QueryResult qr = new QueryResult();
			String amountFieldName = amtName;
			if (list != null && list.size() > 0) {
				qr = QueryResult.buildQueryResult(list, amountFieldName);
			}
		    return qr;
		}

	
	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public Class getEntityClass() {
		return null;
	}
	
	
	public static void main(String[] args) {
		String str = "01:00:00";
		System.out.println(str.replaceAll(":", ""));
	}
}
