/**
 * 
 */
package com.mingtech.application.pool.draft.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.base.service.impl.PoolOutServiceImpl;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftPoolOutBatch;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.PlBatchInfo;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.trust.domain.DraftStorage;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @author wbyecheng
 * 
 * 票据资产出池服务实现（代保管取票、解质押出池）
 *
 */
@Service("draftPoolOutService")
public class DraftPoolOutServiceImpl extends PoolOutServiceImpl<DraftPoolOutBatch, DraftPoolOut> implements
		DraftPoolOutService {
	private static final Logger logger = Logger.getLogger(DraftPoolOutServiceImpl.class);

	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	@Autowired
	private ConsignService consignService;
	@Autowired
	private DraftPoolDiscountServer draftPoolDiscountServer;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private AutoTaskExeService autoTaskExeService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Override
	public Class<DraftPoolOutBatch> getEntityClass() {
		return DraftPoolOutBatch.class;
	}

	@Override
	public DraftPoolOutBatch load(String id) {
		return (DraftPoolOutBatch)super.load(id);
	}

	/**
	* <p>方法名称: toAddPoolInBatch|描述: 新增批次，批次数据初始化,并初始化明细费率和总费用</p>
	* @return
	*/
	public BigDecimal getPoolOutDataAmount(String ids) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select sum(p.plIsseAmt) from DraftPoolOut as p where 1=1 ");
		if (!StringUtil.isBlank(ids)) {
			sb.append(" and p.id in(:ids) ");
			keys.add("ids");
			String[] tmp = ids.split(",");
			List list = new ArrayList();
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i]);
			}
			values.add(list);
		}
		List value = find(sb.toString(), (String[]) keys
				.toArray(new String[keys.size()]), values.toArray());
		return (BigDecimal) value.get(0);
	}
	/**
	 * 票据池  待审批出池票据批次查询
	 * @param user
	 * @param page
	 * @param batch 批次
	 * @param prodList 产品列表
	 * @return
	 */
	public List queryPoolOutBatchForAuditNoFlow(User user, Page page,DraftPoolOutBatch batch,List prodList){
		if(page == null){
			page = new Page();
			page.setPageSize(Integer.MAX_VALUE);
		}
		StringBuffer hql = new StringBuffer("select dto from DraftPoolOutBatch dto,ApproveAuditDto audit ");
		hql.append(" where dto.id=audit.busiId ");
		hql.append(" and audit.productId=dto.productId and audit.openStatus='00' ");
		hql.append(" and audit.curAuditUser like '%"+user.getLoginName()+",%'");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		if(prodList!=null && prodList.size()>0){
			hql.append(" and dto.productId in(:productId)");
			parasName.add("productId");
			parasValue.add(prodList);
		}
		if(StringUtils.isNotBlank(batch.getPlTradeType())){
			hql.append(" and dto.plTradeType =:plTradeType");
			//大的业务类型  
			parasName.add("plTradeType");
			parasValue.add(batch.getPlTradeType());
		}
		if(StringUtils.isNotBlank(batch.getBranchId())){
			hql.append(" and dto.branchId =:branchId");
			//大的业务类型  
			parasName.add("branchId");
			parasValue.add(batch.getBranchId());
		}

		//清单状态为  提交审批+ 审批中的 
		List sattus = new ArrayList();
		sattus.add(PoolComm.PC_TJSP);//提交审批
		sattus.add(PoolComm.PC_SPZ);//审批中
		hql.append(" and dto.plStatus in(:plStatus)");
		parasName.add("plStatus");
		parasValue.add(sattus);
		hql.append(" order by dto.batchNo desc");//
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(" dto.id ",hql.toString(), nameForSetVar, parameters, page);
		return list;
	}
	
	public String txImplawnSendBackPoolOut(String[] batchIds, User user) throws Exception{
		DraftPoolOutBatch poolOutBatch =null;
		List listPoolOuts=new ArrayList();
		Set setPoolOuts = null;
		for (int i = 0; i < batchIds.length; i++) {
			poolOutBatch=this.load(batchIds[i]);
			setPoolOuts = poolOutBatch.getPoolOuts();
			for(Iterator it = setPoolOuts.iterator();it.hasNext();){
				DraftPoolOut poolOut = (DraftPoolOut) it.next();
				if(PoolComm.BW_JZY_YFJZYSQ.equals(poolOut.getMsgStatus()) 
						|| PoolComm.BW_JZY_JZYSQCG.equals(poolOut.getMsgStatus())
					    || PoolComm.BW_JZY_YYFJZYSQCH.equals(poolOut.getMsgStatus())
					    || PoolComm.BW_JZY_JZYSQCHSB.equals(poolOut.getMsgStatus())
					    || PoolComm.BW_JZY_JZYSQYQS.equals(poolOut.getMsgStatus())
					    ){
					throw new Exception("明细报文状态为【已发解质押申请、解质押申请成功、已发解质押申请撤回、解质押申请撤回失败、解质押申请已签收】的记录不可以退回");
				}
				poolOut.setPlStatus(PoolComm.OUT_YTH);
				listPoolOuts.add(poolOut);
		    }
			this.dao.storeAll(listPoolOuts);
			poolOutBatch.setPlStatus(PoolComm.PC_YTH);
			this.dao.store(poolOutBatch);
			
		}
		return null;
	}

	
	
	
	/**
	 * 	
	 * 取票申请
	 *
	 * @param list
	 * @param tradeType
	 * @param acctList
	 * @param remarks
	 * @throws Exception
	 */
	public void txFetchBillFromStorage(List list,String tradeType, List acctList, User user) throws Exception {
		boolean isNetBank = false; //isNetBank:区分出池申请来源  true来自网银 false来自柜面
		if(acctList != null){
			isNetBank = true;
		}
		for (int i = 0; i < list.size(); i++) {
			if(tradeType==null || tradeType.equals("0")){
				//代保管取票申请
				DraftStorage draft = (DraftStorage)this.load((String) list.get(i),DraftStorage.class);
				DraftPoolOut poolout = null;
	
				StringBuffer  hql = new StringBuffer();
				hql.append(" from DraftPoolOut po where po.plDraftNb= '"+draft.getPlDraftNb()+"'");
				List l = this.dao.find(hql.toString());
				if (l.size() > 0) {
					poolout =(DraftPoolOut)l.get(0);
				}else{
					poolout = new DraftPoolOut();
				}
	
				poolout.setBranchId(draft.getBranchId());
				poolout.setPlTradeType(PoolComm.BILL_STORAGE);//业务类型
				poolout.setPlReqTime(new Date());           //申请时间
				poolout.setProductId(PoolComm.PRODUCT_TYPE_DBGQP);//产品类型
				poolout.setPlStatus(PoolComm.OUT_YTJSQ);//票据状态  
				poolout.setPlDraftNb(draft.getPlDraftNb());//票号
				poolout.setPlDraftMedia(draft.getPlDraftMedia());//票据介质
				poolout.setPlDraftType(draft.getPlDraftType());//票据类型
				poolout.setPlIsseAmtValue(draft.getPlIsseAmtValue());//币种
				poolout.setPlIsseAmt(draft.getPlIsseAmt());//票面金额
				poolout.setPlIsseDt(draft.getPlIsseDt());//出票日
				poolout.setPlDueDt(draft.getPlDueDt());//到期日
				poolout.setPlDrwrNm(draft.getPlDrwrNm());//出票人
				poolout.setPlDrwrAcctId(draft.getPlDrwrAcctId());//出票人账号
				poolout.setPlDrwrAcctSvcr(draft.getPlDrwrAcctSvcr());//出票人开户行行号
				poolout.setPlDrwrAcctSvcrNm(draft.getPlDrwrAcctSvcrNm());//出票人开户行
				poolout.setPlPyeeNm(draft.getPlPyeeNm());//收款人
				poolout.setPlPyeeAcctId(draft.getPlPyeeAcctId());//收款人账号
				poolout.setPlPyeeAcctSvcr(draft.getPlPyeeAcctSvcr());//收款人开户行行号
				poolout.setPlPyeeAcctSvcrNm(draft.getPlPyeeAcctSvcrNm());//收款人开户行
				poolout.setPlAccptrNm(draft.getPlAccptrNm());//承兑人
				poolout.setPlAccptrId(draft.getPlAccptrId());//承兑人账号
				poolout.setPlAccptrSvcr(draft.getPlAccptrSvcr());//承兑行行号
				poolout.setPlAccptrSvcrNm(draft.getPlAccptrSvcrNm());//承兑行
				poolout.setPlApplyNm(draft.getPlApplyNm());//申请人
				poolout.setPlCommId(draft.getPlCommId());//申请人组织机构代码
				poolout.setPlApplyAcctSvcr(draft.getPlApplyAcctSvcr());//申请人开户行行号
				poolout.setPlApplyAcctSvcrNm(draft.getPlApplyAcctSvcrNm());//申请人开户行名称
				poolout.setPlApplyAcctId(draft.getPlApplyAcctId());//申请人账号
				poolout.setPlReqTime(DateUtils.getWorkDayDate());//请求时间
				poolout.setReqSource(PoolComm.DATA_LY_WY);//数据来源
				poolout.setDraftStroage(draft);//对应票据池表
				poolout.setPlRecSvcr(draft.getPlRecSvcr());//业务经办行行号
				poolout.setPlRecSvcrNm(draft.getPlRecSvcrNm());//业务经办行名称
				poolout.setDraftStroage(draft);//代保管对象
				poolout.setPlAccptrAddress(draft.getPlAccptrAddress());//承兑人地址
				poolout.setPlAccptrProto(draft.getPlAccptrProto());//承兑协议号
				poolout.setPlTradeProto(draft.getPlTradeProto());//交易合同号码
				poolout.setPlAccptrDt(draft.getPlAccptrDt());//承兑日
				poolout.setWorkerName(draft.getWorkerName());//经办人姓名
				if(!isNetBank){
					this.txStore(poolout);
				}else{
					//取票申请来自网银,需核对票据持有人账号
					if(((String)draft.getPlApplyAcctId()).equals((String)acctList.get(i))){
						this.txStore(poolout);
						//String operatDes = "来自网银的代保管取票申请";
					}else{
						throw new Exception("取票申请失败,保存DraftPoolOut实体时出错.原因:持票人账号不符!");
					}
				}
	   
				draft.setLastTransId(poolout.getId());
				draft.setLastTransType(poolout.getPlTradeType());
				draft.setPlStatus(PoolComm.DBG_QPSQ);//取票申请
				draft.setDraftFlag("2");    //托管转质押：2
				
				if(!isNetBank){//取票申请来自柜面,不需核对票据持有人账号
					   this.txStore(draft);
				}else{//取票申请来自网银,需核对票据持有人账号
					if(((String)draft.getPlApplyAcctId()).equals((String)acctList.get(i))){
							this.txStore(draft);
					}else{
						    throw new Exception("取票申请失败,保存DraftStorage实体时出错.持票人账号不符!");
					}
			    }
				
			
			}
		}
	}

	/**
	 * 同业额度
	 * @param batch	出池批次
	 * @param user	当前用户
	 * @param usedFlag	占用标志：ture占用 false释放
	 * @param busiType	业务类型：9：票据入池、10：票据出池)其他看BankLimitHistory类busiType
	 * @throws Exception
	 */
	public void txSaveOutLineOfTrade(DraftPoolOutBatch batch,User user,boolean usedFlag,String busiType) throws Exception{
	}

	/**
	 * 额度扣减失败回复已扣额度
	 * @param limitList
	 */
	public void txReleaseCreditQuota(List<DraftPoolOut> limitList,boolean usedFlag,User user){
	}


	@Override
	public DraftPoolOut getDraftPoolOutByDraftNb(String plDraftNb,String beginRangeNo, String endRangeNo)
			throws Exception {
		StringBuffer hql = new StringBuffer("select dto from DraftPoolOut dto where dto.plDraftNb=:plDraftNb and dto.beginRangeNo=:beginRangeNo and dto.endRangeNo=:endRangeNo and dto.plStatus !=:plStattus");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		parasName.add("plDraftNb");
		parasValue.add(plDraftNb);	

		/********************融合改造新增 start******************************/
		parasName.add("beginRangeNo");
		parasValue.add(beginRangeNo);
		parasName.add("endRangeNo");
		parasValue.add(endRangeNo);
		/********************融合改造新增 end******************************/

		parasName.add("plStattus");
		parasValue.add(PoolComm.CC_05);	
		
//		parasName.add("endFlag");
//		parasValue.add("0");	
//		hql.append(" order by p.plReqTime desc");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		if(list.size()>0){
			return (DraftPoolOut) list.get(0);
		}else{
			return null;
		}
	}

	

	public PoolBillInfo loadByBillNo(String billNo,String beginRangeNo, String endRangeNo) throws Exception {
		String sql = "select obj from PoolBillInfo obj where obj.SBillNo =? and obj.beginRangeNo =? and obj.endRangeNo =? ";
		List param = new ArrayList();
		param.add(billNo);
		param.add(beginRangeNo);
		param.add(endRangeNo);
		List list = this.find(sql, param);
		if(list!=null&&list.size()>0){
			return (PoolBillInfo) list.get(0);
		}
		return null;
	}
	/** 20210517 gcj
	* <p>方法名称: loadByBillDiscID|描述: 根据票据票据系统票据ID查询票据信息对象</p>
	* @param discBillId 票据系统票据ID
	* @return
	*/
	public PoolBillInfo loadByBillDiscID(String discBillId,String beginRangeNo, String endRangeNo) throws Exception {
		String sql = "select obj from PoolBillInfo obj where obj.discBillId =? and obj.beginRangeNo =? and obj.endRangeNo =?";
		List param = new ArrayList();
		param.add(discBillId);
		param.add(beginRangeNo);
		param.add(endRangeNo);
		List list = this.find(sql, param);
		if(list!=null&&list.size()>0){
			return (PoolBillInfo) list.get(0);
		}
		return null;
	}


	@Override
	public DraftPoolOut getDraftPoolOutBybean(PoolQueryBean bean) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select p from DraftPoolOut as p where 1=1 ");
		//票据号
		if (StringUtil.isNotBlank(bean.getBillNo())) {
			sb.append(" and p.plDraftNb =:plDraftNb");
			keys.add("plDraftNb");
			values.add(bean.getBillNo());
		}
		if(StringUtil.isNotBlank(bean.getSStatusFlag())){
			sb.append(" and p.plStatus =:plStatus");
			keys.add("plStatus");
			values.add(bean.getSStatusFlag());
		}
		//子票区间起始
		if(StringUtil.isNotBlank(bean.getBeginRangeNo())){
			sb.append(" and p.beginRangeNo =:beginRangeNo");
			keys.add("beginRangeNo");
			values.add(bean.getBeginRangeNo());
		}
		//子票区间截止
		if(StringUtil.isNotBlank(bean.getEndRangeNo())){
			sb.append(" and p.endRangeNo =:endRangeNo");
			keys.add("endRangeNo");
			values.add(bean.getEndRangeNo());
		}
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list = this.find(sb.toString(), paramNames, paramValues);
		if(list!= null && list.size() >0){
			return (DraftPoolOut) list.get(0);
		}
		return null;
	}
	
/**gcj 20210513
 * 通过id查询DraftPoolOut对象
 * @param id
 * @return
 * @throws Exception
 */
	public DraftPoolOut loadByOutId(String id) throws Exception {
		String sql = "select obj from DraftPoolOut obj where obj.id =?";
		List param = new ArrayList();
		param.add(id);
		List list = this.find(sql, param);
		if(list!=null&&list.size()>0){
			return (DraftPoolOut) list.get(0);
		}
		return null;
	}
	
	@Override
	public PlBatchInfo getPlBatchInfoBybean(PoolQueryBean bean) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select p from PlBatchInfo as p where 1=1 ");
		//票据号
		if (StringUtil.isNotBlank(bean.getSBatchNo())) {//操作批次号
			sb.append(" and p.doBatchNo =:doBatchNo");
			keys.add("doBatchNo");
			values.add(bean.getSBatchNo());
		}
		if(StringUtil.isNotBlank(bean.getSStatusFlag())){//处理状态
			sb.append(" and p.doFlag =:doFlag");
			keys.add("doFlag");
			values.add(bean.getSStatusFlag());
		}
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list = this.find(sb.toString(), paramNames, paramValues);
		if(list!= null && list.size() >0){
			return (PlBatchInfo) list.get(0);
		}
		return null;
	}
	
	
	/**gcj 20210517
	 * 质押出池
	 * @param DraftPool 基础信息表
	 * @param PlBatchInfo 续接批次表
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew sendPoolOutMsg(DraftPool pool ,PlBatchInfo info,String hildId) throws Exception{
	        ReturnMessageNew response = new ReturnMessageNew();
	        Ret ret = new Ret();

			logger.info("解质押后续操作自动任务:质押出池");
			PedProtocolDto dto = null;
			dto = pedProtocolService.queryProtocolDto( null, null,pool.getPoolAgreement(), null, null, null);
			ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
			
			
			/**
			 * body内需要传送的值
			 */
			poolTransNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
			//若电子签名为空设置为0
			poolTransNotes.setSignature(info.getESign());//电子签名
			
			/**
			 * 票据信息数组需传送的值
			 */
			Map infoMap = new HashMap();
			infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",pool.getDraftSource());//票据来源 
			infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0092000);//交易编号  质押申请
			infoMap.put("BILL_INFO_ARRAY.BILL_NO",pool.getAssetNb());//票据（包）号码
			infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",pool.getHilrId());//持票id
			infoMap.put("BILL_INFO_ARRAY.BILL_ID",pool.getPoolBillInfo().getDiscBillId());//票据id
			infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",pool.getBeginRangeNo());//子票区间起始
			infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",pool.getEndRangeNo());//子票区间截至
			infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
			infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
			if(pool.getDraftSource().equals(PoolComm.CS01)){
				infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型1：申请、0：撤销
			}
			
			String seq = poolBatchNoUtils.txGetFlowNo();
			infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
			
			poolTransNotes.getDetails().add(infoMap);

			/**
			 * 质押信息数组需传送的值
			 */
			Map pledgeMap = new HashMap();
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_DATE",DateUtils.toString(new Date(),"yyyyMMdd"));//质权日期 必输
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_NAME",info.getPledgeeName());//质权人名称 必输
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_NO",info.getPledgeeAcctNo());//质权人账号 必输
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_NAME",info.getPledgeeName());//质权人账户名称
			//质权人用融资机构信息	若果有融资机构号拿融资机构号,若没有拿受理网点
			String orgNo = "10000";
			if(StringUtil.isNotBlank(dto.getCreditDeptNo())){
				orgNo = dto.getCreditDeptNo();
			}else {
				orgNo = dto.getOfficeNet();
			}
			logger.info("根据机构号["+orgNo+"]查询机构信息开始");
			Department ment = departmentService.queryByInnerBankCode(orgNo);
			if(ment!=null){
				logger.info("查询部门信息结束,质权人开户行号为["+ment.getBankNumber()+"],质权人开户行名称为["+ment.getName()+"]");
				pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_OPEN_BANK_NO",ment.getPjsBrchNo());//质权人开户行行号
				poolTransNotes.setReceiverBankNo(ment.getPjsBrchNo());//质权人开户行行号
			}else {
				logger.info("未查询到部门信息");
			}
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_OPEN_BANK_NAME","");//质权人开户行行名
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_TRANSACT_CHANNEL_NO","");//质权人业务办理渠道代码 3-票据池
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.PLEDGEE_ACCT_TYPE","");//质权人识别类型
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.INPOOL_FLAG","0");//入池标志 必输
			pledgeMap.put("BILL_INFO_ARRAY.PLEDGE_INFO_ARRAY.LOCK_FLAG","");//锁定标志
			poolTransNotes.getDetails().add(pledgeMap);
			
			//2.调用质押申请接口
			logger.info("票号为["+pool.getAssetNb()+"]的票,发送质押申请开始");
			ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
			logger.info("返回得code值："+resp.getRet().getRET_CODE());
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				info.setDoFlag(PoolComm.DO_01);
				pedProtocolService.txStore(info);
				logger.info("发送质押申请结束");
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
		     	ret.setRET_MSG(ErrorCode.ERR_MSG_998+"发送质押申请失败");
				response.setRet(ret);
		     	return  response;
			}

			response.setRet(ret);
	     	return  response;
		}
	
	/**gcj 20210517
	 * 背书出池
	 * @param DraftPool 基础信息表
	 * @param PlBatchInfo 续接批次表
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew sendPoolendorseeMsg(DraftPool pool ,PlBatchInfo info,String hildId) throws Exception{
	        ReturnMessageNew response = new ReturnMessageNew();
	        Ret ret = new Ret();
			logger.info("解质押后续操作自动任务:背书出池");
			ECDSPoolTransNotes poolNotes =new ECDSPoolTransNotes();
			
			/**
			 * body内需要传送的值
			 */
			poolNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
			//若电子签名为空设置为0
			poolNotes.setSignature(pool.getElsignature());//电子签名
			
			/**
			 * 票据信息数组需传送的值
			 */
			Map infoMap = new HashMap();
			infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",pool.getDraftSource());//票据来源 
			infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0062000);//交易编号 背书申请
			infoMap.put("BILL_INFO_ARRAY.BILL_NO",pool.getAssetNb());//票据（包）号码
			infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",pool.getHilrId());//持票id
			infoMap.put("BILL_INFO_ARRAY.BILL_ID",pool.getPoolBillInfo().getDiscBillId());//票据id
			infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",pool.getBeginRangeNo());//子票区间起始
			infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",pool.getEndRangeNo());//子票区间截至
			infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
			infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
			if(pool.getDraftSource().equals(PoolComm.CS01)){
				infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型1：申请、0：撤销
			}
			
			String seq = poolBatchNoUtils.txGetFlowNo();
			infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
			
			poolNotes.getDetails().add(infoMap);

			/**
			 * 背书信息数组需传送的值
			 * 结算方式 必输  没值
			 */
			Map endorseMap = new HashMap();
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSE_DATE", DateUtils.toString(new Date(),"yyyyMMdd"));//背书日期 必输
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_NAME", info.getEndorseeName());//被背书人名称 必输
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_ACCT_NO",info.getEndorseeAcctNo());//被背书人账号 必输
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_ACCT_NAME", info.getEndorseeName());//被背书人账户名称
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_OPEN_BANK_NO", info.getEndorseeOpenBank());//被背书人开户行号
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_OPEN_BANK_NAME", "");//被背书人开户行名
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_TRANSACT_CHANNEL_NO", "");//被背书人业务办理渠道代码
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.ENDORSEE_ACCT_TYPE","");//被背书人识别类型
			endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.REMARK", "");//备注
			if(pool.getPoolBillInfo().getSBanEndrsmtFlag().equals("0")){
				//可转让
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.UNENDORSE_FLAG","EM00");//禁止背书标记 必输EM00可再转让   EM01不得转让
			}else{
				endorseMap.put("BILL_INFO_ARRAY.ENDORSE_INFO_ARRAY.UNENDORSE_FLAG","EM01");//禁止背书标记 必输EM00可再转让   EM01不得转让
			}
			
			poolNotes.getDetails().add(endorseMap);
			
			//2.调用背书申请接口
			logger.info("票号为["+pool.getAssetNb()+"]的票,发送背书申请开始");
			ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolNotes);
			logger.info("返回得code值："+resp.getRet().getRET_CODE());
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				info.setDoFlag(PoolComm.DO_01);
				pedProtocolService.txStore(info);
				logger.info("发送背书申请结束");
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
		     	ret.setRET_MSG(ErrorCode.ERR_MSG_998+"发送背书申请失败");
				response.setRet(ret);
		     	return  response;
			}

		   response.setRet(ret);
		   return  response;
		}

	/**gcj 20210519
	 * 贴现出池
	 * @param DraftPool 基础信息表
	 * @param PlBatchInfo 续接批次表
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew sendPoolDiscountMsg(DraftPool pool ,PlBatchInfo info,String hildId) throws Exception{
	        ReturnMessageNew response = new ReturnMessageNew();
	        Ret ret = new Ret();
	        PoolBillInfo billInfo = this.loadByBillNo(pool.getAssetNb(),pool.getBeginRangeNo(),pool.getEndRangeNo());
			ECDSPoolTransNotes poolTransNotes =new ECDSPoolTransNotes();
			
			/**
			 * body内需要传送的值
			 */
			poolTransNotes.setApplicantAcctNo(pool.getAccNo());//电票签约帐号  多账号|拼接
			//若电子签名为空设置为0
			poolTransNotes.setSignature(pool.getElsignature());//电子签名
			
			/**
			 * 票据信息数组需传送的值
			 */
			Map infoMap = new HashMap();
			infoMap.put("BILL_INFO_ARRAY.BILL_SOURCE",pool.getDraftSource());//票据来源 
			infoMap.put("BILL_INFO_ARRAY.TRAN_NO",PoolComm.NES_0072000);//交易编号  贴现申请
			infoMap.put("BILL_INFO_ARRAY.BILL_NO",pool.getAssetNb());//票据（包）号码
			infoMap.put("BILL_INFO_ARRAY.HOLD_BILL_ID",billInfo.getHilrId());//持票id
			infoMap.put("BILL_INFO_ARRAY.BILL_ID",billInfo.getDiscBillId());//票据id
			infoMap.put("BILL_INFO_ARRAY.START_BILL_NO",pool.getBeginRangeNo());//子票区间起始
			infoMap.put("BILL_INFO_ARRAY.END_BILL_NO",pool.getEndRangeNo());//子票区间截至
			infoMap.put("BILL_INFO_ARRAY.SOURCE_CHANNEL_NO","3");//渠道来源  3-票据池
			infoMap.put("BILL_INFO_ARRAY.APP_LOCK_TYPE","0");//经办锁类型 0-未经办锁票 1-已经办锁票
			if(pool.getDraftSource().equals(PoolComm.CS01)){
				infoMap.put("BILL_INFO_ARRAY.OPERATION_TYPE","1");//操作类型1：申请、0：撤销
			}
			
			String seq = poolBatchNoUtils.txGetFlowNo();
			infoMap.put("BILL_INFO_ARRAY.TRAN_SEQ_NO", seq);//申请流水号
			
			poolTransNotes.getDetails().add(infoMap);

			/**
			 * 贴现信息数组需传送的值
			 * 结算方式 必输  没值
			 */
			Map discountMap = new HashMap();
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_TYPE","RM00");//贴现类型 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_INT_RATE",info.getDiscountIntRate().setScale(2, BigDecimal.ROUND_UP).toString());//贴现利率 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_INT_RATE_TYPE","");//贴现利率类型
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.REAL_PAY_AMT",pool.getAssetAmt());//贴现实付金额 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_DATE",DateUtils.toString(info.getDiscountDate(),"yyyyMMdd"));//贴现日 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_BANK_NAME",info.getDiscountInBankName());//贴入行名 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_PERSON_NAME",info.getDiscountInBankName());//贴入人名 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_BANK_NO",info.getDiscountInBankCode());//贴入人行号 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.DISCOUNT_IN_ACCT_NO","0");//贴入行账号 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_ACCT_NO",info.getEnterAcctNo());//入账账号 必输
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.ENTER_BANK_NO",info.getEnterBankCode());//入账行号 必输
			if(pool.getPoolBillInfo().getSBanEndrsmtFlag().equals("0")){
				//可转让
				discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.UNENDORSE_FLAG","EM00");//禁止背书标记 必输EM00可再转让   EM01不得转让
			}else{
				discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.UNENDORSE_FLAG","EM01");//禁止背书标记 必输EM00可再转让   EM01不得转让
			}
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.LOCK_FLAG","0");//锁票标志
			discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.FORCE_DICOUNT_FLAG","");//强制贴现标志
			if(pool.getDraftSource().equals(PoolComm.CS02)){
				discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","ST02");//结算方式 必输
			}else{
				if(info.getOnlineSettleFlag().equals("0")){
					//线下清算  SM01线下清算
					discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","SM01");//结算方式 必输
				}else{
					//SM00线上清算 
					discountMap.put("BILL_INFO_ARRAY.DISCOUNT_INFO_ARRAY.SETTLE_TYPE","SM00");//结算方式 必输
				}
			}
			
			poolTransNotes.getDetails().add(discountMap);
			
			

			
			/*poolTransNotes.setPayIntMode(info.getDiscountMode());//贴现方式
			poolTransNotes.setRedeemOpemDate(DateUtils.toString(info.getRedeemOpenDate(),"yyyyMMdd"));//赎回开放日期
			poolTransNotes.setRedeenEndDate(DateUtils.toString(info.getRedeemEndDate(),"yyyyMMdd"));//赎回截止日期
			poolTransNotes.setRedeemIntRate(info.getRedeemIntRate().setScale(2, BigDecimal.ROUND_HALF_UP).toString());//赎回利率
			*/
			
			//2.调用贴现申请接口
			logger.info("票号为["+pool.getAssetNb()+"]的票,发送贴现申请开始");
			ReturnMessageNew resp = poolEcdsService.txApplyImplawn(poolTransNotes);
			logger.info("返回得code值："+resp.getRet().getRET_CODE());
			if(resp.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
				info.setDoFlag(PoolComm.DO_01);
				pedProtocolService.txStore(info);
				logger.info("发送贴现申请结束");
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		     	ret.setRET_MSG(ErrorCode.SUCC_MSG_QU);
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
		     	ret.setRET_MSG(ErrorCode.ERR_MSG_998+"发送贴现申请失败");
				response.setRet(ret);
		     	return  response;
			}
		   response.setRet(ret);
		   return  response;
		}


	/**
	 * @param 票据出池查询
	 * @param gcj 20210701
	 * @return
	 * @throws Exception
	 */
	public QueryResult toPoolOutByQueryBean(DraftQueryBean bean, Page page) throws Exception {
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值

		StringBuffer hql = new StringBuffer();
		hql.append("select po,info from DraftPoolOut  po,PoolBillInfo info  where po.plDraftNb = info.SBillNo  ");
		// 票据号码
		if (bean.getPlDraftNb() != null && !"".equals((bean.getPlDraftNb()))) {
			hql.append(" and po.plDraftNb like :plDraftNb");
			paramName.add("plDraftNb");
			paramValue.add("%" + bean.getPlDraftNb() + "%");
		}
		/********************融合改造新增 start******************************/
		// 票据号码起
		if (bean.getBeginRangeNo() != null && !"".equals((bean.getBeginRangeNo()))) {
			hql.append(" and po.beginRangeNo = :beginRangeNo");
			paramName.add("beginRangeNo");
			paramValue.add(bean.getBeginRangeNo());
		}
		// 票据号码止
		if (bean.getEndRangeNo() != null && !"".equals((bean.getEndRangeNo()))) {
			hql.append(" and po.endRangeNo = :endRangeNo");
			paramName.add("endRangeNo");
			paramValue.add(bean.getEndRangeNo());
		}
		// 票据来源
		if (bean.getDraftSource() != null && !"".equals((bean.getDraftSource()))) {
			hql.append(" and po.draftSource = :draftSource");
			paramName.add("draftSource");
			paramValue.add(bean.getDraftSource());
		}
		/********************融合改造新增 end******************************/
		if (bean.getPlStatus() != null && !bean.getPlStatus().isEmpty()) {
			hql.append(" and po.plStatus in(:plStatus)");
			paramName.add("plStatus");
			paramValue.add(bean.getPlStatus());
		}
		// id
		if (bean.getAssetNb() != null && !"".equals((bean.getAssetNb()))) {
			hql.append(" and po.id like :id");
			paramName.add("id");
			paramValue.add("%" + bean.getAssetNb() + "%");
		}
		// 批次号
		if (bean.getBatchNo() != null && !"".equals((bean.getBatchNo()))) {
			hql.append(" and po.batchNo like :batchNo");
			paramName.add("batchNo");
			paramValue.add("%" + bean.getBatchNo() + "%");
		}

		// 票据介质plDraftMedia
		if (bean.getPlDraftMedia() != null && !"".equals(bean.getPlDraftMedia()) && !"0".equals(bean.getPlDraftMedia() )) {
			hql.append(" and po.plDraftMedia=:plDraftMedia");
			paramName.add("plDraftMedia");
			paramValue.add(bean.getPlDraftMedia());
		}

		// 票据种类assetType
		if (bean.getAssetType() != null && !"".equals(bean.getAssetType()) && !"0000".equals(bean.getAssetType() )) {
			hql.append(" and po.plDraftType=:plDraftType");
			paramName.add("plDraftType");
			paramValue.add(bean.getAssetType());
		}

		// 入池日期开始startplDueDt
		if (bean.getStartplReqTime() != null && !"".equals(bean.getStartplReqTime())) {
			hql.append(" and po.plReqTime>=TO_DATE(:splReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splReqTime");
			paramValue.add(DateUtils.toString(bean.getStartplReqTime(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 入池申请日期结束endplDueDt
		if (bean.getEndplReqTime() != null && !"".equals(bean.getEndplReqTime())) {
			hql.append(" and po.plReqTime<=TO_DATE(:eplReqTime, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplReqTime");
			paramValue.add(DateUtils.toString(bean.getEndplReqTime(), "yyyy-MM-dd") + " 23:59:59");
		}
		// 批次状态 assetStatus
		if (bean.getAssetStatus() != null && !"".equals(bean.getAssetStatus())) {
			hql.append(" and po.plStatus=:plStatus");
			paramName.add("plStatus");
			paramValue.add(bean.getAssetStatus());
		}
		// 票据池编号 poolAgreement
		if (bean.getPoolAgreement() != null && !"".equals(bean.getPoolAgreement())) {
			hql.append(" and po.poolAgreement=:poolAgreement");
			paramName.add("poolAgreement");
			paramValue.add(bean.getPoolAgreement());
		}
		// 票据池客户名称CustName
		if (bean.getCustName() != null && !"".equals(bean.getCustName())) {
			hql.append(" and po.custName=:custName");
			paramName.add("custName");
			paramValue.add(bean.getCustName());
		}
		// 票据池核心客户号CustNo
		if (bean.getCustNo() != null && !"".equals(bean.getCustNo())) {
			hql.append(" and po.custNo=:custNo");
			paramName.add("custNo");
			paramValue.add(bean.getCustNo());
		}
		// 票据金额开始startassetAmt
		if (bean.getStartassetAmt() != null) {
			hql.append(" and po.plIsseAmt>=:startplIsseAmt");
			paramName.add("startplIsseAmt");
			paramValue.add(bean.getStartassetAmt());
		}
		// 票据金额结束endassetAmt
		if (bean.getEndassetAmt() != null) {
			hql.append(" and po.plIsseAmt<=:endplIsseAmt");
			paramName.add("endplIsseAmt");
			paramValue.add(bean.getEndassetAmt());
		}

		// 出票日期开始StartplIsseDt
		if (bean.getStartplIsseDt() != null && !"".equals(bean.getStartplIsseDt())) {
			hql.append(" and po.plIsseDt>=TO_DATE(:splIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splIsseDt");
			paramValue.add(DateUtils.toString(bean.getStartplIsseDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 出票日期结束endplDueDt
		if (bean.getEndplIsseDt() != null && !"".equals(bean.getEndplIsseDt())) {
			hql.append(" and po.plIsseDt<=TO_DATE(:eplIsseDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplIsseDt");
			paramValue.add(DateUtils.toString(bean.getEndplIsseDt(), "yyyy-MM-dd") + " 23:59:59");
		}
		// 到期日开始StartplDueDt
		if (bean.getStartplDueDt() != null && !"".equals(bean.getStartplDueDt())) {
			hql.append(" and po.plDueDt>=TO_DATE(:splDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("splDueDt");
			paramValue.add(DateUtils.toString(bean.getStartplDueDt(), "yyyy-MM-dd") + " 00:00:00");
		}
		// 到期日结束endplDueDt
		if (bean.getEndplDueDt() != null && !"".equals(bean.getEndplDueDt())) {
			hql.append(" and po.plDueDt<=TO_DATE(:eplDueDt, 'yyyy-mm-dd hh24:mi:ss')");
			paramName.add("eplDueDt");
			paramValue.add(DateUtils.toString(bean.getEndplDueDt(), "yyyy-MM-dd") + " 23:59:59");
		}
		hql.append(" order by po.plReqTime desc ");

		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		QueryResult qr = new QueryResult();
		List res = this.find(hql.toString(), paramNames, paramValues, page);
		if(res != null && res.size() > 0){
			List result = new ArrayList();
			for (int i = 0; i < res.size(); i++) {
				Object[] obj = (Object[]) res.get(i);
				DraftPoolOut out = (DraftPoolOut) obj[0];
				PoolBillInfo info = (PoolBillInfo) obj[1];
				out.setPlDrwrAcctSvcr(info.getSIssuerBankCode());
				out.setPlPyeeAcctSvcr(info.getSPayeeBankCode());
				out.setForbidFlag(info.getSBanEndrsmtFlag());
				out.setSOperatorId(info.getDiscBillId());//字段暂存票据id
				result.add(out);
			}
			String amountFieldName = "plIsseAmt";
			if (result != null && result.size() > 0) {
				qr = QueryResult.buildQueryResult(result, amountFieldName);
			}
		}
		return qr;
	}

	@Override
	public ReturnMessageNew txTransTypePoolOut(DraftPoolOut draftPoolOut, String transResult,PoolBillInfo bill)
			throws Exception {
		
		PoolBillInfo info = (PoolBillInfo) this.load(bill.getBillinfoId(),PoolBillInfo.class);
		
		ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
		//解质押签收
    	if(transResult.equals("2")){//签收成功
    		PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setBillNo(info.getSBillNo());
			poolQueryBean.setSStatusFlag(PoolComm.DS_03);
			
			/********************融合改造新增 start******************************/
			poolQueryBean.setBeginRangeNo(draftPoolOut.getBeginRangeNo());
			poolQueryBean.setEndRangeNo(draftPoolOut.getEndRangeNo());
			/********************融合改造新增 end******************************/
			
			DraftPool dpool=consignService.queryDraftByBean(poolQueryBean).get(0);
			dpool.setAssetStatus(PoolComm.DS_04);//已出池
			info.setSDealStatus(PoolComm.DS_04);//已出池
			dpool.setLastOperTm(new Date());
			dpool.setLastOperName("自动出池过程,获取解质押签收结果,已出池");
			info.setLastOperTm(new Date());
			info.setLastOperName("自动出池过程,获取解质押签收结果,已出池");
			info.setProduct_id(PoolComm.PRODUCT_TYPE_CC);//表示已出过池
			this.txStore(dpool);
			this.txStore(info);
			draftPoolOut.setPlStatus(PoolComm.CC_05);//变更状态为：质押申请已签收
			this.txStore(draftPoolOut);
			
			
			
			PoolQueryBean pq = new PoolQueryBean();
			pq.setSBatchNo(dpool.getDoBatchNo());//批次号
			pq.setSStatusFlag(PoolComm.DO_00);//状态 未处理
			PlBatchInfo  plBatchInf=null;
			if(null!=dpool.getDoBatchNo()){
				  plBatchInf = this.getPlBatchInfoBybean(pq);
			}
			try {
				if(null!=plBatchInf){
					
//					Map<String, String> reqParams =new HashMap<String,String>();
//					reqParams.put("busiId", plBatchInf.getId());
//					autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOL_AUTO_BANKE_NO, plBatchInf.getId(), AutoTaskNoDefine.BUSI_TYPE_AUTOBANK, reqParams, plBatchInf.getDoBatchNo(), plBatchInf.getBpsNo(), plBatchInf.getBpsName(), null);
				
					
//					Map<String,String> reqParams = new HashMap<String,String>();
//					reqParams.put("busiId", plBatchInf.getId());
//					autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_UPDATE_NO, plBatchInf.getId(), AutoTaskNoDefine.BUSI_TYPE_UPDATE, reqParams, plBatchInf.getDoBatchNo(), plBatchInf.getBpsNo(), plBatchInf.getBpsName(), null);
					
					
					/*logger.info("票据出池后续操作任务【"+plBatchInf.getOutMode()+"】处理开始...");
					if(plBatchInf.getOutMode().equals("CCMS_01")){//出池模式	质押出池
						if(dpool.getAssetStatus().equals(PoolComm.DS_04)){//已出池
							ReturnMessageNew res = new ReturnMessageNew();
							res=this.sendPoolOutMsg(dpool, plBatchInf,bill.getHilrId());
							if(res.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){//出池成功
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG(res.getRet().getRET_MSG());
								response.setRet(ret);
								return response;
							}else{//出池失败
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG("票据池处理成功，但是后续【"+plBatchInf.getOutMode()+"】报文发送失败，这里不需要处理，客户手动在网银发起即可。");
								response.setRet(ret);
								return response;
							}
							
							
							
						}
					}else if(plBatchInf.getOutMode().equals("CCMS_02")){	//出池模式		贴现出池
						if(dpool.getAssetStatus().equals(PoolComm.DS_04)){//已出池
							ReturnMessageNew res = new ReturnMessageNew();
							res=this.sendPoolDiscountMsg(dpool, plBatchInf,bill.getHilrId());
							if(res.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG(res.getRet().getRET_MSG());
								response.setRet(ret);
								return response;
							}else{//出池失败
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG("票据池处理成功，但是后续【"+plBatchInf.getOutMode()+"】报文发送失败，这里不需要处理，客户手动在网银发起即可。");
								response.setRet(ret);
								return response;
							}
							
						}
					}else if(plBatchInf.getOutMode().equals("CCMS_03")){//出池模式		背书出池
						if(dpool.getAssetStatus().equals(PoolComm.DS_04)){//已出池
							ReturnMessageNew res = new ReturnMessageNew();
							res=this.sendPoolendorseeMsg(dpool, plBatchInf,bill.getHilrId());
							if(res.getRet().getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG(res.getRet().getRET_MSG());
								response.setRet(ret);
								return response;
							}else{
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG("票据池处理成功，但是后续【"+plBatchInf.getOutMode()+"】报文发送失败，这里不需要处理，客户手动在网银发起即可。");
								response.setRet(ret);
								return response;
							}
						}
					}*/
				}
				
			} catch (Exception e) {
				logger.error("");
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("票据池处理成功，但是后续【"+plBatchInf.getOutMode()+"】报文发送失败，这里不需要处理，客户手动在网银发起即可。");
				response.setRet(ret);
				return response;
			}

			
			//强贴部分
			List dis=draftPoolDiscountServer.getDiscountsListByParam(PoolComm.TX_00, info.getSBillNo(), info.getBeginRangeNo(), info.getEndRangeNo(), null);
			if(dis.size()>0){
				if(dpool.getTXFlag().equals("1")){//强贴标记
					/**
					 * 唤醒贴现申请子任务
					 */
					PlDiscount discount=(PlDiscount)dis.get(0);	
					Map<String, String> reqParams =new HashMap<String,String>();
					reqParams.put("busiId", discount.getId());
					autoTaskPublishService.publishWaitTask("0", AutoTaskNoDefine.POOLDIS_SEND_TASK_NO, discount.getId(), AutoTaskNoDefine.BUSI_TYPE_TX, reqParams);
					
				}
			}

    	}else if(transResult.equals("3")){//签收失败
			draftPoolOut.setPlStatus(PoolComm.CC_03);//状态变更为质押待签收重新发送签收申请
			this.txStore(draftPoolOut);
			//签收异常重新手工执行
			
        	AutoTaskExe autoTaskExe=autoTaskExeService.doAutoTaskExe(AutoTaskNoDefine.POOLOUT_SIGN_TASK_NO, AutoTaskNoDefine.POOL_AUTO_POOLOUT, draftPoolOut.getId());
        	autoTaskExe.setStatus("4");
        	draftPoolDiscountServer.txStore(autoTaskExe);
    	}
    	
    	ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
     	ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
     	response.setRet(ret);
		return response;
    	
    	
	}
		
}
