package com.mingtech.application.pool.draft.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.InPoolBillBean;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("draftPoolDiscountServer")
public class DraftPoolDiscountServerImpl extends GenericServiceImpl implements DraftPoolDiscountServer{
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private DraftPoolInService draftPoolInService;
	@Autowired
	private ConsignService consignService;
	@Autowired
	private AutoTaskExeService autoTaskExeService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	private static final Logger logger = Logger.getLogger(DraftPoolDiscountServerImpl.class);

	@Override
	public List<PlDiscount> getDiscountsListByParam(String SBillStatus,
			String billNo, String beginNo,String endNo,Page page) throws Exception {
		StringBuffer hql = new StringBuffer("select pd from PlDiscount pd where 1=1 ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		if(SBillStatus != null && !SBillStatus.equals("")){
			hql.append(" and pd.SBillStatus =:SBillStatus");
			parasName.add("SBillStatus");
			parasValue.add(SBillStatus);
		}
		if(StringUtil.isNotBlank(billNo)){
			hql.append(" and pd.SBillNo =:SBillNo");
			parasName.add("SBillNo");
			parasValue.add(billNo);
		}

		/********************融合改造新增 start******************************/
		if(StringUtil.isNotBlank(beginNo)){
			hql.append(" and pd.beginRangeNo =:beginNo");
			parasName.add("beginNo");
			parasValue.add(beginNo);
		}
		if(StringUtil.isNotBlank(endNo)){
			hql.append(" and pd.endRangeNo =:endNo");
			parasName.add("endNo");
			parasValue.add(endNo);
		}
		/********************融合改造新增 end******************************/
		
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters,page);
		return list;
	}
	
	@Override
	public List<PlDiscount> getDiscountsListByParamView(DraftQueryBean bean,User user,Page page) throws Exception {
		StringBuffer hql = new StringBuffer("select pd from PlDiscount pd,PedProtocolDto ped where 1=1 and pd.bpsNo=ped.poolAgreement ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		/*if(ids != null && !ids.equals("")){
			hql.append(" and pd.id in(:ids)");
			parasName.add("ids");
			List idList = Arrays.asList(ids.split(",")); //id集合
			parasValue.add(idList);
		}*/
		
		if(bean.getIds() != null && !bean.getIds().equals("")){
			hql.append(" and pd.id =:id");
			parasName.add("id");
			parasValue.add(bean.getIds());
		}
		if(bean.getAssetStatus() != null && !bean.getAssetStatus().equals("")){
			hql.append(" and pd.SBillStatus =:SBillStatus");
			parasName.add("SBillStatus");
			parasValue.add(bean.getAssetStatus());
		}
		if(StringUtil.isNotBlank(bean.getAssetNb())){
			hql.append(" and pd.SBillNo like :SBillNo");
			parasName.add("SBillNo");
			parasValue.add("%"+bean.getAssetNb().trim()+"%");
		}
		
		/********************融合改造新增 start******************************/
		if(StringUtil.isNotBlank(bean.getBeginRangeNo())){//子票区间起
			hql.append(" and pd.beginRangeNo = :beginRangeNo");
			parasName.add("beginRangeNo");
			parasValue.add(bean.getBeginRangeNo().trim());
		}
		if(StringUtil.isNotBlank(bean.getEndRangeNo())){//子票区间止
			hql.append(" and pd.endRangeNo = :endRangeNo");
			parasName.add("endRangeNo");
			parasValue.add(bean.getEndRangeNo().trim());
		}
		
		/**
		 * 票据来源
		 */
		if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS01)){
			hql.append(" and (pd.draftSource is null or pd.draftSource =:draftSource) ");
			parasName.add("draftSource");
			parasValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS02)){
			hql.append(" and pd.draftSource =:draftSource ");
			parasName.add("draftSource");
			parasValue.add(bean.getDraftSource());
		}else if(StringUtil.isNotBlank(bean.getDraftSource()) && bean.getDraftSource().equals(PoolComm.CS03)){
			hql.append(" and pd.draftSource =:draftSource ");
			parasName.add("draftSource");
			parasValue.add(bean.getDraftSource());
		}
		
		/**
		 * 是否可拆分
		 */
		if(bean.getSplitFlag()!=null&&!bean.getSplitFlag().equals("")){
			hql.append(" and pd.splitFlag = :splitFlag");
			parasName.add("splitFlag");
			parasValue.add(bean.getSplitFlag());
		}
		/********************融合改造新增 end******************************/
		
		if(StringUtil.isNotBlank(bean.getPoolAgreement())){//票据池编号
			hql.append(" and pd.bpsNo like :bpsNo");
			parasName.add("bpsNo");
			parasValue.add("%"+bean.getPoolAgreement().trim()+"%");
		}
		if(StringUtil.isNotBlank(bean.getPoolName())){//客户名称
			hql.append(" and pd.bpsName like :bpsName");
			parasName.add("bpsName");
			parasValue.add("%"+bean.getPoolName().trim()+"%");
		}
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and ped.officeNet in (:officeNetList) ");
				parasName.add("officeNetList");
				parasValue.add(resultList);
			}
		}		
		hql.append(" order by pd.discBatchDt desc ");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters,page);
		return list;
	}
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PlBatchInfo> getBatchInfoByParam(String doFlag, String str)
			throws Exception {
		StringBuffer hql = new StringBuffer("select info from PlBatchInfo info where 1=1 ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		if(doFlag != null && !doFlag.equals("")){
			hql.append(" and info.doFlag =:doFlag");
			parasName.add("doFlag");
			parasValue.add(doFlag);
		}
		
		
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		
		return list;
	}
	
	@Override
	public List findInPoolBillByBeanExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[14];
				InPoolBillBean bill = (InPoolBillBean) res.get(i);
				s[0] = bill.getAssetNb();//票号
				s[1] = String.valueOf(bill.getFBillAmount());//票面金额
				if(StringUtil.isNotBlank(bill.getSDealStatus())){
					String status = "";
					if(PoolComm.DS_02.equals(bill.getSDealStatus())){ //DS_02已入池   DS_03出池处理中  DS_04已出池
						status = "已入池";
					}else if(PoolComm.DS_03.equals(bill.getSDealStatus())){
						status = "出池处理中";
					}else if(PoolComm.DS_04.equals(bill.getSDealStatus())){
						status = "已出池";
					}
					s[2] = status;//状态
				}
				s[3] = bill.getPlDrwrNm();
				s[4] = bill.getPlDrwrAcctId();
				s[5] = bill.getPlDrwrAcctSvcrNm();
				s[6] = bill.getPlPyeeNm();
				s[7] = bill.getPlPyeeAcctId();
				s[8] = bill.getPlPyeeAcctSvcrNm();
				s[9] = bill.getPlAccptrNm();//承兑人/付款人  
				if (bill.getPlDueDt() != null) {
					s[10] = String.valueOf(bill.getPlDueDt()).substring(0, 10);
				}else{
					s[10] = "";
				}	
				s[11] = bill.getMarginAccount();
				s[12] = bill.getMarginAccountName();
 				s[13] = bill.getPoperBeatch();//机构				

				list.add(s);
			}
		}
		return list;
	}
	@Override
	public List findForcedDiscountExpt(List res, Page page) {
		List list = new ArrayList();
		if (res != null && res.size() > 0) {
			for (int i = 0; i < res.size(); i++) {
				String s[] = new String[9];
				PlDiscount pool = (PlDiscount) res.get(i);
				s[0] = pool.getSBillNo();
//				s[1] = this.reBillType(pool.getBillType());
				
				s[1] = this.reBillMedia(pool.getBillMedia());
				s[2] = pool.getBpsName();
				if (pool.getDiscBatchDt() != null) {
					s[3] = String.valueOf(pool.getDiscBatchDt()).substring(0, 10);
				}else{
					s[3] = "";
				}

				if (pool.getDIssueDt() != null) {
					s[4] = String.valueOf(pool.getDIssueDt()).substring(0, 10);
				}else{
					s[4] = "";
				}
				if (pool.getDDueDt() != null) {
					s[5] = String.valueOf(pool.getDDueDt()).substring(0, 10);
				}else {
					s[5] = "";
				}
				s[6] = pool.getSAgcysvcr();
				s[7] = String.valueOf(pool.getFBillAmount());
				s[8] = this.reSBillStatus(pool.getSBillStatus());

				list.add(s);
			}
		}
		return list;
	}
	public String reBillType(String value) {
		if(value != null){
			if (value.equals(PoolComm.BILL_TYPE_BANK)) {
				return "银承";
			}else if(value.equals(PoolComm.BILL_TYPE_BUSI)){
				return "商承";
			}
		}
		return "";
	}
	public String reBillMedia(String value) {
		if(value != null){
			if (value.equals(PoolComm.BILL_MEDIA_PAPERY)) {
				return "纸质";
			}else if(value.equals(PoolComm.BILL_MEDIA_ELECTRONICAL)){
				return "电子";
			}
		}
		return "";
	}
	public String reSBillStatus(String value) {
		if (value != null) {
			if (value.equals(PoolComm.TX_00)) {
				return "新建数据";
			} else if (value.equals(PoolComm.TX_01)) {
				return "已发贴现申请";
			} else if (value.equals(PoolComm.TX_02)) {
				return "贴现申请待签收";
			} else if (value.equals(PoolComm.TX_03)) {
				return "可发签收记账";
			} else if (value.equals(PoolComm.TX_04)) {
				return "已发贴现签收记账";
			} else if (value.equals(PoolComm.TX_05)) {
				return "贴现记账";
			} else if (value.equals(PoolComm.TX_06)) {
				return "贴现失败";
			}
		}
		return "";
	}
	@Override
	public List<InPoolBillBean> queryInPoolBillByBean(InPoolBillBean bean,User user,Page page) throws Exception {
		List listPool = new ArrayList();
		StringBuffer hql = new StringBuffer("select ped.marginAccount,ped.marginAccountName,pd from DraftPool pd,PedProtocolDto ped where 1=1 and ped.poolAgreement=pd.poolAgreement ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		String SBillMedia = PoolComm.BILL_MEDIA_PAPERY;
		hql.append(" and pd.poolBillInfo.SBillMedia =:SBillMedia");
		parasName.add("SBillMedia");
		parasValue.add(SBillMedia);
		// 增加机构筛选条件
		if (user != null && user.getDepartment() != null) {
			// 总行可以看所有信息，分行或一级支行看本辖内，网点查看本网点
			if (!PublicStaticDefineTab.isRootDepartment(user)) {
				List resultList = departmentService.getAllChildrenInnerCodeList(user.getDepartment().getInnerBankCode(), -1);
				hql.append(" and ped.officeNet in (:officeNetList) ");
				parasName.add("officeNetList");
				parasValue.add(resultList);
			}
		}	
		
		if(null != bean){
			//状态
			if(StringUtil.isNotBlank(bean.getSDealStatus())){
				hql.append(" and pd.poolBillInfo.SDealStatus = :SDealStatus");
				parasName.add("SDealStatus");
				parasValue.add(bean.getSDealStatus());
			}else{
				List  SDealStatus = new ArrayList();
				SDealStatus.add(PoolComm.DS_02);//已入池
				SDealStatus.add(PoolComm.DS_03);//出池处理中
				SDealStatus.add(PoolComm.DS_04);//已出池
				hql.append(" and pd.poolBillInfo.SDealStatus in(:SDealStatus)");
				parasName.add("SDealStatus");
				parasValue.add(SDealStatus);
			}
			//保管机构
			if(StringUtil.isNotBlank(bean.getPoperBeatch())){
				hql.append(" and pd.poperBeatch =:poperBeatch");
				parasName.add("poperBeatch");
				parasValue.add(bean.getPoperBeatch());
			}
			//票号
			if(StringUtil.isNotBlank(bean.getAssetNb())){
				hql.append(" and pd.assetNb =:assetNb");
				parasName.add("assetNb");
				parasValue.add(bean.getAssetNb());
			}
			//金额start
			if(!"".equals(bean.getAssetAmtStart()) && null != bean.getAssetAmtStart()){
				hql.append(" and pd.assetAmt >=:assetAmtStart");
				parasName.add("assetAmtStart");
				parasValue.add(bean.getAssetAmtStart());
			}
			//金额end
			if(!"".equals(bean.getAssetAmtEnd()) && null != bean.getAssetAmtEnd()){
				hql.append(" and pd.assetAmt <=:assetAmtEnd");
				parasName.add("assetAmtEnd");
				parasValue.add(bean.getAssetAmtEnd());
			}
			// 出票日期开始
			if (bean.getPlIsseDtStart() != null && !"".equals(bean.getPlIsseDtStart())) {
				hql.append(" and pd.plIsseDt>=TO_DATE(:plstartDt, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("plstartDt");
				parasValue.add(DateUtils.toString(bean.getPlIsseDtStart(), "yyyy-MM-dd") + " 00:00:00");
			}
			// 出票日期结束
			if (bean.getPlIsseDtEnd() != null && !"".equals(bean.getPlIsseDtEnd())) {
				hql.append(" and pd.plIsseDt<=TO_DATE(:plIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("plIsseDt");
				parasValue.add(DateUtils.toString(bean.getPlIsseDtEnd(), "yyyy-MM-dd") + " 23:59:59");
			}

			// 到期日期开始
			if (bean.getPlDueDtStart() != null && !"".equals(bean.getPlDueDtStart())) {
				hql.append(" and pd.plDueDt>=TO_DATE(:plstartDueDt, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("plstartDueDt");
				parasValue.add(DateUtils.toString(bean.getPlDueDtStart(), "yyyy-MM-dd") + " 00:00:00");
			}
			// 到期日期结束
			if (bean.getPlDueDtEnd() != null && !"".equals(bean.getPlDueDtEnd())) {
				hql.append(" and pd.plDueDt<=TO_DATE(:plDueDt, 'yyyy-mm-dd hh24:mi:ss')");
				parasName.add("plDueDt");
				parasValue.add(DateUtils.toString(bean.getPlDueDtEnd(), "yyyy-MM-dd") + " 23:59:59");
			}
		}else{
			List  SDealStatus = new ArrayList();
			SDealStatus.add(PoolComm.DS_02);//已入池
			SDealStatus.add(PoolComm.DS_03);//出池处理中
			SDealStatus.add(PoolComm.DS_04);//已出池
			hql.append(" and pd.poolBillInfo.SDealStatus in(:SDealStatus)");
			parasName.add("SDealStatus");
			parasValue.add(SDealStatus);
		}
		hql.append(" order by pd.plDueDt ");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters,page);
		InPoolBillBean  poolBean = null;
		DraftPool pool=null;
		for(int i=0;i<list.size();i++){			
			Object[] obj = (Object[]) list.get(i);
			poolBean = new InPoolBillBean();
			if (obj[0] != null) {
				poolBean.setMarginAccount(obj[0].toString());// 保证金账号
			}
			if (obj[1] != null) {
				poolBean.setMarginAccountName(obj[1].toString());// 保证金账号名
			}
			if (obj[2] != null) {
				pool=(DraftPool) obj[2];
				poolBean.setPoperBeatch(pool.getPoperBeatch());
				poolBean.setAssetNb(pool.getPoolBillInfo().getSBillNo());
				poolBean.setFBillAmount(pool.getPoolBillInfo().getFBillAmount());
				poolBean.setPlIsseDt(pool.getPlIsseDt());
				poolBean.setPlDueDt(pool.getPlDueDt());
				poolBean.setPlDrwrNm(pool.getPlDrwrNm());
				poolBean.setPlDrwrAcctId(pool.getPlDrwrAcctId());
				poolBean.setPlDrwrAcctSvcrNm(pool.getPlDrwrAcctSvcrNm());
				poolBean.setPlPyeeNm(pool.getPlPyeeNm());
				poolBean.setPlPyeeAcctId(pool.getPlPyeeAcctId());
				poolBean.setPlPyeeAcctSvcrNm(pool.getPlPyeeAcctSvcrNm());
				poolBean.setPlAccptrNm(pool.getPlAccptrNm());
				poolBean.setSDealStatus(pool.getPoolBillInfo().getSDealStatus());
				poolBean.setSerial(pool.getId());
			}
			listPool.add(poolBean);
		}
		return listPool;
	}
	
	/**
	 *  根据ID查询DraftPoolIn
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PlDiscount loadByPlDiscountId(String id) throws Exception {
		String sql = "select obj from PlDiscount obj where obj.id =?";
		List param = new ArrayList();
		param.add(id);
		List list = this.find(sql, param);
		if(list!=null&&list.size()>0){
			return (PlDiscount) list.get(0);
		}
		return null;
	}

	/**
	 * 强帖签收记账后处理
	 * @param discount
	 * @param transResult	交易结果 1：未处理；（申请、签收）2：交易成功；（申请、签收）3：交易失败；（申请、签收）4：签收拒绝成功
	 * @param acctResult	记账结果 0:未记账	1：记账成功	2：记账失败
	 * @return
	 * @throws Exception
	 */
	@Override
	public void txTaskDiscountSynchroniza(PlDiscount discount,String transResult,String acctResult)
			throws Exception {
		
		
		discount.setTaskDate(new Date());
		PoolBillInfo bill = draftPoolInService.loadByBillNo(discount.getSBillNo(),discount.getBeginRangeNo(),discount.getEndRangeNo());
		logger.info("通过票号查询到的大票表对象为["+bill+"]");

		PoolQueryBean poolQueryBean = new PoolQueryBean();
		poolQueryBean.setBillNo(bill.getSBillNo());
		
		/********************融合改造新增 start******************************/
		poolQueryBean.setBeginRangeNo(bill.getBeginRangeNo());
		poolQueryBean.setEndRangeNo(bill.getEndRangeNo());
		/********************融合改造新增 end******************************/
		
		
		poolQueryBean.setSStatusFlag(PoolComm.DS_10);
		DraftPool pool=consignService.queryDraftByBean(poolQueryBean).get(0);
		if("2".equals(transResult)){
			/*
			 * 签收成功  校验记账状态是否为成功
			 * ①成功	直接更新状态
			 * ②失败	去核心做记账查证  
			 */
			
			if(!"1".equals(acctResult)){//记账失败  去核心做记账查证  
				logger.info("强贴自动任务:记账查询");
				
				/**
				 * 上层查询接口已返回记账状态    不用再去查核心的记账状态
				 */
				logger.info("票号"+discount.getSBillNo()+"承兑签收成功，核心记账失败！");
				discount.setSBillStatus(PoolComm.TX_03);
				this.txStore(discount);
				//驱动贴现签收 继续签收
				AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLDIS_SIGN_TASK_NO, AutoTaskNoDefine.POOL_AUTO_POOLDIS, discount.getId());
				autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), "4", "票号"+discount.getSBillNo()+"承兑签收成功，核心记账失败！");
				return ;
				
				
				/*ReturnMessageNew response = new ReturnMessageNew();
				ECDSPoolTransNotes note =new ECDSPoolTransNotes();
				
				
				note.setBillNo(discount.getSBillNo());//票号
				*//********************融合改造新增 start******************************//*
				note.setBeginRangeNo(discount.getBeginRangeNo());//
				note.setEndRangeNo(discount.getEndRangeNo());//
				*//********************融合改造新增 end******************************//*
				
				
				note.setTradeDate(new Date());//交易日期
				response = poolEcdsService.txApplyQueryAcctStatus(note);
				String acctFlag = (String) response.getBody().get("ACCOUNT_STATUS");//记账状态
				if(!acctFlag.equals("1")){//核心返回不是记账成功查询调度值为失败
					logger.info("票号"+discount.getSBillNo()+"承兑签收成功，核心记账失败！");
					discount.setSBillStatus(PoolComm.TX_03);
					this.txStore(discount);
					//驱动贴现签收 继续签收
					AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLDIS_SIGN_TASK_NO, AutoTaskNoDefine.POOL_AUTO_POOLDIS, discount.getId());
					autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), "4", "票号"+discount.getSBillNo()+"承兑签收成功，核心记账失败！");
//					autoTaskExe.setStatus("4");//异常
//					this.txStore(autoTaskExe);
					return ;
				}*/
			}
			discount.setSBillStatus(PoolComm.TX_05);//成功	记账成功
			discount.setAccountStatus(acctResult);//记账成功
			discount.setReTranstatus(transResult);
			discount.setLastOperTm(new Date());
			discount.setLastOperName("强贴自动任务,记账成功");
			pool.setAssetStatus(PoolComm.DS_11);//贴现已完成
			bill.setSDealStatus(PoolComm.DS_11);//贴现已完成
			pool.setLastOperTm(new Date());
			pool.setLastOperName("强贴自动任务,贴现签收及记账申请结果查询");
			bill.setLastOperTm(new Date());
			bill.setLastOperName("强贴自动任务,贴现签收及记账申请结果查询");
			
		}else{
			/**
			 * 签收失败	直接重新调起签收调度执行签收任务
			 */
			logger.info("票号"+discount.getSBillNo()+"承兑签收记账成功，签收失败！");
			discount.setSBillStatus(PoolComm.TX_03);
			this.txStore(discount);
			//驱动贴现签收 继续签收
			AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLDIS_SIGN_TASK_NO, AutoTaskNoDefine.BUSI_TYPE_TJZ, discount.getId());
			autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), "4", "票号"+discount.getSBillNo()+"承兑签收记账成功，签收失败！");
			
		}
		this.txStore(discount);
		this.txStore(pool);
		this.txStore(bill);
		
		
		
	}
}
