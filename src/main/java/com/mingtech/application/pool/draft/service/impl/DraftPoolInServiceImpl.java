/**
 * 
 */
package com.mingtech.application.pool.draft.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.CreditTransNotes;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditClientService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetFactory;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolInBatch;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedGuaranteeCredit;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;



/**
 * @author wbyecheng
 * 
 *         票据资产入池服务实现（代保管存票、质押入池）
 * 
 */
@Service("draftPoolInService")
public class DraftPoolInServiceImpl extends GenericServiceImpl implements DraftPoolInService {
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private PoolEcdsService poolEcdsService;
	@Autowired
	private 	DraftPoolQueryService drarftPoolQueryService;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private AssetRegisterService assetRegisterService ;
	@Autowired
	private PoolBailEduService poolBailEduService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private AssetTypeManageService assetTypeManageService;
	@Autowired
	private PoolCreditClientService poolCreditClientService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	@Autowired
	private FinancialAdviceService financialAdviceService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	
	private static final Logger logger = Logger.getLogger(DraftPoolInServiceImpl.class);
	
	@Override
	public Class<DraftPoolInBatch> getEntityClass() {
		return DraftPoolInBatch.class;
	}

	@Override
	public DraftPoolInBatch load(String id) {
		return (DraftPoolInBatch) super.load(id);
	}


	/**
	 * 通过票号获取池内票据
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	@Override
	public DraftPool getDraftPoolByDraftNb(String DraftNb,String startNo, String endNo) throws Exception{
		StringBuffer hql = new StringBuffer("select dto from DraftPool dto where dto.assetNb=:assetNb and dto.isEduExist=:isEduExist and dto.assetStatus in (:assetStatus) and dto.beginRangeNo = :startNo and dto.endRangeNo = :endNo ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		parasName.add("assetNb");
		parasValue.add(DraftNb);	
		parasName.add("isEduExist");
		parasValue.add(PoolComm.EDU_EXIST);	
		List assetStatus = new ArrayList();
		assetStatus.add(PoolComm.DS_02);
		assetStatus.add(PoolComm.DS_03);
		parasName.add("assetStatus");
		parasValue.add(assetStatus);
		
		parasName.add("startNo");
		parasValue.add(startNo);
		parasName.add("endNo");
		parasValue.add(endNo);
		
		hql.append(" order by dto.plTm desc");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		if(list.size()>0){
			return (DraftPool) list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 通过票号获取池内票据
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<DraftPool> getDraftPoolByDraftNbList(List draftNbList) throws Exception{
		StringBuffer hql = new StringBuffer("select dto from DraftPool dto where dto.assetNb in (:assetNb)  ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		
		parasName.add("assetNb");
		parasValue.add(draftNbList);	
		
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		return list;
	}


	/**
	 * 通过票号获取入池对象
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	@Override
	public DraftPoolIn getDraftPoolInByDraftNb(String plDraftNb,String beginRangeNo, String endRangeNo) throws Exception{
		StringBuffer hql = new StringBuffer("select dto from DraftPoolIn dto where dto.plDraftNb=:plDraftNb and dto.endFlag=:endFlag and dto.beginRangeNo=:beginRangeNo and dto.endRangeNo=:endRangeNo ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		parasName.add("plDraftNb");
		parasValue.add(plDraftNb);	
		parasName.add("endFlag");
		parasValue.add("0");	
		
		parasName.add("beginRangeNo");
		parasValue.add(beginRangeNo);	
		parasName.add("endRangeNo");
		parasValue.add(endRangeNo);	
		
		hql.append(" order by dto.plReqTime desc");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		if(list.size()>0){
			return (DraftPoolIn) list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public String txSendPoolNotify(DraftPoolIn in, User u)
			throws Exception {
		String res = "succsess";

		
		//入池记账完成,票据入池
		DraftPool poolOld = this.getDraftPoolByDraftNb(in.getPlDraftNb(),in.getBeginRangeNo(),in.getEndRangeNo());

		if(null != poolOld){
			if(!poolOld.getAssetStatus().equals(PoolComm.DS_04)){
				throw new Exception("票据未出池完成，请勿重复入池！");
			}else{
				throw new Exception("票据已入池，请勿重复入池！");
			}
		}
		DraftPool pool = AssetFactory.newDraftPool();
		BeanUtil.DraftPoolCopy(in, pool);

		pool.setPlTm(DateUtils.getCurrDate());
		pool.setAssetLimitTotal(in.getPlIsseAmt());//衍生额度
		pool.setAssetLimitFree(in.getPlIsseAmt());//可用额度
		pool.setAssetLimitUsed(new BigDecimal(0));//已用额度
		pool.setAssetLimitFrzd(new BigDecimal(0));//已冻结额度
		pool.initLimit();
		pool.setAssetStatus(PoolComm.PJC_YRC);
		pool.setLastTransId(in.getId());
		pool.setLastTransType("入池");
		pool.setChargeFlag("1"); //扣费标示
		pool.setAssetStatus(PoolComm.DS_02);//已入池
		pool.setAssetCommId(in.getCustNo());
		String type = null;//额度类型：低风险额度-ED_10  高风险额度-ED_20

		if(PoolComm.LOW_RISK.equals(pool.getRickLevel())){//低风险票据
			type = PoolComm.ED_PJC;
			/**
			 * 查询并保存客户assetType消息
			 */
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(type, in.getPoolAgreement());
			pool.setAt(at.getId());//关联资产对象
		}else if(PoolComm.HIGH_RISK.equals(pool.getRickLevel())){//高风险票据
			type = PoolComm.ED_PJC_01;
			/**
			 * 查询并保存客户assetType消息
			 */
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(type, in.getPoolAgreement());
			pool.setAt(at.getId());//关联资产对象
		}else{
			type = PoolComm.ED_PJC;
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(type, in.getPoolAgreement());
			pool.setAt(at.getId());//关联资产对象
			pool.setAssetLimitTotal(new BigDecimal(0));//衍生额度
			pool.setAssetLimitFree(new BigDecimal(0));//可用额度
			pool.setAssetLimitUsed(new BigDecimal(0));//已用额度
			pool.setAssetLimitFrzd(new BigDecimal(0));//已冻结额度
		}
		
		/**
		 * 查询并保存客户assetType消息
		 */	

        /*入池改变大票表状态 wfj 1228*/
        PoolBillInfo poolBillInfo = this.loadByBillNo(pool.getAssetNb(),pool.getBeginRangeNo(),pool.getEndRangeNo());
        pool.setPoolBillInfo(poolBillInfo);//关联大票表。注意：关联大票表后，大票表同步持有数据时，不能删除（汉口银行）。
        poolBillInfo.setSDealStatus(PoolComm.DS_02);//已入池
		poolBillInfo.setRickLevel(in.getRickLevel());
        pool.setBlackFlag(poolBillInfo.getBlackFlag());//黑名单标志
        pool.setPoolAgreement(poolBillInfo.getPoolAgreement());//票据池编号

		PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, poolBillInfo.getPoolAgreement(), null, null, null);
		if(dto.getIsGroup().equals(PoolComm.NO)) {//单户
			pool.setCustName(dto.getCustname());
			pool.setSocialCode(dto.getPlUSCC());
		}else {
			ProListQueryBean queryB = new ProListQueryBean();
			queryB.setBpsNo(poolBillInfo.getPoolAgreement());
			queryB.setCustNo(poolBillInfo.getCustNo());
			PedProtocolList pedpro = pedProtocolService.queryProtocolListByQueryBean(queryB);
			pool.setCustName(pedpro.getCustName());
			pool.setSocialCode(pedpro.getSocialCode());
		}

			
		//顺延天数
		Long deferDays = assetTypeManageService.queryDelayDays(pool.getRickLevel(), pool.getPlDueDt());
		pool.setDelayDays(deferDays.intValue());
		pool.setCpFlag(poolBillInfo.getCpFlag());
		
		
		this.txStore(pool);
		this.txStore(poolBillInfo);
		

		in.setDraftPool(pool);//入池明细表关联draftpool
		in.setPlStatus(PoolComm.RC_05);//入池已记账
		this.txStore(in);
		
		//资产登记表登记
		String riskFlag = pool.getRickLevel();
		if(PoolComm.LOW_RISK.equals(riskFlag)||PoolComm.HIGH_RISK.equals(riskFlag)){			
			assetRegisterService.txBillAssetRegister(pool, dto);
		}
		
		try {
			
			//核心保证金同步
			BailDetail bail = poolBailEduService.txUpdateBailDetail(dto.getPoolAgreement());
			
			
			//判断并更新保证金资产登记表信息
			assetRegisterService.txCurrentDepositAssetChange(dto.getPoolAgreement(), bail.getAssetNb(), bail.getAssetLimitFree());
			
			//重新计算池额度信息
			if(PoolComm.POOL_MODEL_01.equals(dto.getPoolMode())){//总量模式
				financialService.txCreditCalculationTotal(dto);
			}else{//期限配比					
				financialService.txCreditCalculationTerm(dto);
			}
			
		} catch (Exception e) {
			logger.error("额度计算出现问题！");
		}
		
		return res;

	}
	
	public PoolBillInfo loadByBillNo(String billNo,String startNo, String endNo) throws Exception {
		String sql = "select obj from PoolBillInfo obj where 1=1 ";
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		if(billNo != null && StringUtils.isNotEmpty(billNo)){
			sql = sql + " and obj.SBillNo = :billNo ";
			parasName.add("billNo");
			parasValue.add(billNo);
		}
		if(startNo != null && StringUtils.isNotEmpty(startNo)){
			sql = sql + " and obj.beginRangeNo = :startNo ";
			parasName.add("startNo");
			parasValue.add(startNo);
		}
		if(endNo != null && StringUtils.isNotEmpty(endNo)){
			sql = sql + " and obj.endRangeNo = :endNo ";
			parasName.add("endNo");
			parasValue.add(endNo);
		}
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(sql, nameForSetVar, parameters);
		if(list!=null&&list.size()>0){
			return (PoolBillInfo) list.get(0);
		}
		return null;
	}
	
	/**
	 * 通过票号状态获取入池对象
	 * @param DraftNb
	 * @return
	 * @throws Exception
	 */
	public DraftPoolIn getDraftPoolInByDraftNb(String DraftNb,String plstatus,String beginRangeNo, String endRangeNo) throws Exception{
		StringBuffer hql = new StringBuffer("select dto from DraftPoolIn dto where dto.plDraftNb=:plDraftNb and dto.plStatus=:plStatus and dto.beginRangeNo=:beginRangeNo and dto.endRangeNo=:endRangeNo");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		parasName.add("plDraftNb");
		parasValue.add(DraftNb);	
		parasName.add("plStatus");
		parasValue.add(plstatus);
		parasName.add("beginRangeNo");
		parasValue.add(beginRangeNo);
		parasName.add("endRangeNo");
		parasValue.add(endRangeNo);
		hql.append(" order by dto.plReqTime desc");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		if(list.size()>0){
			return (DraftPoolIn) list.get(0);
		}else{
			return null;
		}
	}

	/**
	 *  根据ID查询DraftPoolIn
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DraftPoolIn loadByPoolInId(String id) throws Exception {
		String sql = "select obj from DraftPoolIn obj where obj.id =?";
		List param = new ArrayList();
		param.add(id);
		List list = this.find(sql, param);
		if(list!=null&&list.size()>0){
			return (DraftPoolIn) list.get(0);
		}
		return null;
	}

	@Override
	public void txAutoInPool() throws Exception {
		
		logger.info("自动入池方法处理...");
		

		List list = this.queryAutoBill();
		
		if(null != list){		
			for(int j=0;j<list.size();j++){
				
				PoolBillInfo pool =(PoolBillInfo)list.get(j);
				ProtocolQueryBean bean=new ProtocolQueryBean();
				bean.setPoolAgreement(pool.getPoolAgreement());
				PedProtocolDto pedDto = pedProtocolService.queryProtocolDtoByQueryBean(bean); 
				
				/*
				 * 加电票经办锁
				 */
				try {					
					ECDSPoolTransNotes ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
					/********************融合改造新增 start******************************/
					ecdsNotes.setAcctNo(pool.getAccNo());
					ecdsNotes.setDataSource(pool.getDraftSource());//票据来源	必输
					ecdsNotes.setTransType("1");//1-持票 2-申请交易 3-应答交易
					ecdsNotes.setIsLock("1");//加锁  1加锁   2解锁
					ecdsNotes.setTransNo(PoolComm.NES_0092010);//交易编号	必输
					ecdsNotes.setHldrId(pool.getHilrId());//持票ID集合
					
					HashMap map = new HashMap();
					map.put("BILL_INFO_ARRAY.BILL_ID", pool.getDiscBillId());// 票据ID
					
					ecdsNotes.getDetails().add(map);
					
					/********************融合改造新增 end******************************/
					//调用BBSP系统锁票接口，返回成功标记
					if (poolEcdsService.txApplyLockEbk(ecdsNotes)){//bbsp操作成功
						logger.info("票据【"+pool.getSBillNo()+"】,子票区间为【"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo()+"】自动入池电票系统经办锁加锁成功!");
					}else{
						logger.info("票据【"+pool.getSBillNo()+"】,子票区间为【"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo()+"】自动入池电票系统经办锁加锁失败，不执行自动入池操作!");
						continue;
					}
				} catch (Exception e) {
					logger.info("票据【"+pool.getSBillNo()+"】,子票区间为【"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo()+"】自动入池加锁异常，不执行自动入池操作：",e);
					
					/*
					 * 异常中的解锁操作
					 */
					try {
						ECDSPoolTransNotes ecdsNotes = new ECDSPoolTransNotes();//调用bbsp同步加锁/解锁传参
						/********************融合改造新增 start******************************/
						ecdsNotes.setAcctNo(pool.getAccNo());
						ecdsNotes.setDataSource(pool.getDraftSource());//票据来源	必输
						ecdsNotes.setTransType("2");//1-持票 2-申请交易 3-应答交易
						ecdsNotes.setIsLock("2");//加锁  1加锁   2解锁
						ecdsNotes.setTransNo(PoolComm.NES_0092010);//交易编号	必输
						ecdsNotes.setHldrId(pool.getHilrId());//持票ID集合
						
						HashMap map = new HashMap();
						map.put("BILL_INFO_ARRAY.BILL_ID", pool.getDiscBillId());// 票据ID
						ecdsNotes.getDetails().add(map);
						/********************融合改造新增 start******************************/
						if (poolEcdsService.txApplyLockEbk(ecdsNotes)){//bbsp操作成功
							logger.info("票据【"+pool.getSBillNo()+"】,子票区间为【"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo()+"】加锁异常后解锁成功!");
						}else{
							logger.info("票据【"+pool.getSBillNo()+"】,子票区间为【"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo()+"】加锁异常后解锁失败!");
						}
					} catch (Exception e2) {
						logger.info("票据【"+pool.getSBillNo()+"】,子票区间为【"+pool.getBeginRangeNo()+"-"+pool.getEndRangeNo()+"】加锁异常后解锁异常：",e);
					}
					
					logger.info("票据【"+pool.getSBillNo()+"】自动入池加锁异常后防止锁票的解锁操作完成");
					
					continue;
				}
				
				/*
				 * poolIn表落库
				 */
				DraftPoolIn poolIn=this.copyBillToPoolIn(pool);
				this.txStore(poolIn);
				
				/*
				 * cd_edraft表落库
				 */
				pool.setSDealStatus(PoolComm.DS_01);// 入池处理中
				this.txStore(pool);
				
				/*
				 * 质押申请自动任务生成：生成自动任务流水记录 异步执行质押申请 gcj   
				 */
				Map<String, String> reqParams =new HashMap<String,String>();
				reqParams.put("busiId", poolIn.getId());
				autoTaskPublishService.publishTask("0", AutoTaskNoDefine.POOLIN_TASK_NO, poolIn.getId(), AutoTaskNoDefine.BUSI_TYPE_ZY, reqParams,  poolIn.getPlDraftNb(), pool.getPoolAgreement(), null, null);
				
			}
		}
		
	}
	
	/**
	 * pl_pool_in数据处理
	 * @author Ju Nana
	 * @param bill
	 * @param branchId
	 * @return
	 * @throws Exception
	 * @date 2019-10-17下午3:09:46
	 */
	public DraftPoolIn copyBillToPoolIn(PoolBillInfo bill)throws Exception {
		
		DraftPoolIn poolIn  = new DraftPoolIn();
		poolIn.setPlReqTime(new Date());// 申请时间

		poolIn.setPlDraftNb(bill.getSBillNo());// 票据号码
		poolIn.setPlDraftMedia(bill.getSBillMedia());// 票据介质
		poolIn.setPlDraftType(bill.getSBillType());// 票据类型
		poolIn.setPlIsseDt(bill.getDIssueDt());// 出票日
		poolIn.setPlDueDt(bill.getDDueDt());// 到期日
		/** 出票人信息 **/
		poolIn.setPlDrwrNm(bill.getSIssuerName());// 出票人名称
		poolIn.setPlDrwrAcctId(bill.getSIssuerAccount());// 出票人账号
		poolIn.setPlDrwrAcctSvcr(bill.getSIssuerBankCode());// 出票人开户行行号
		poolIn.setPlDrwrAcctSvcrNm(bill.getSIssuerBankName());// 出票人开户行名称
		poolIn.setPlDrwrAcctName(bill.getSIssuerAcctName());//出票人账户名称
		/** 收款人信息 **/
		poolIn.setPlPyeeNm(bill.getSPayeeName());// 收款人名称
		poolIn.setPlPyeeAcctId(bill.getSPayeeAccount());// 收款人账号
		poolIn.setPlPyeeAcctSvcr(bill.getSPayeeBankCode());// 收款人开户行行号
		poolIn.setPlPyeeAcctSvcrNm(bill.getSPayeeBankName());// 收款人开户行名称
		poolIn.setPlPyeeAcctName(bill.getSPayeeAcctName());//收款人账号
		/** 承兑人信息 **/
		poolIn.setPlAccptrNm(bill.getSAcceptor());// 承兑人名称
		poolIn.setPlAccptrId(bill.getSAcceptorAccount());// 承兑人账号
		poolIn.setPlAccptrSvcr(bill.getSAcceptorBankCode());// 承兑人开户行行号
		poolIn.setAcptHeadBankNo(bill.getAcptHeadBankNo());//承兑人开户行总行
		poolIn.setAcptHeadBankName(bill.getAcptHeadBankName());//承兑人开户行名总行
		poolIn.setPlAccptrSvcrNm(bill.getSAcceptorBankName());// 承兑人开户行名称
		poolIn.setPlAccptrAcctName(bill.getSAcceptorAcctName());
		/** 申请人信息 **/

		poolIn.setPlIsseAmt(bill.getFBillAmount());// 票面金额

		poolIn.setPlReqTime(new Date());// 申请时间
		poolIn.setPlApplyNm(bill.getSOwnerBillName());// 申请人名称
		poolIn.setPlCommId(bill.getSOwnerOrgCode());// 申请人组织机构代码

		poolIn.setPlApplyAcctId(bill.getSOwnerAcctId());// 申请人账号
		poolIn.setPlApplyAcctSvcr(bill.getSMbfeBankCode());// 申请人开户行行号
		poolIn.setPlApplyAcctSvcrNm(bill.getDraftOwnerSvcrName());// 申请人开户行名称
		poolIn.setPlStatus(PoolComm.RC_00);// 新建数据
		poolIn.setPlTradeType("YW_01");// 代保管YW_02/票据池 YW_01 存单池YW_03
		poolIn.setForbidFlag(bill.getSBanEndrsmtFlag());// 禁止背书标识
		poolIn.setPlRemark("");// 备注
		poolIn.setRickLevel(bill.getRickLevel());// 风险等级
		poolIn.setRiskComment(bill.getRiskComment());// 风险说明
		poolIn.setBlackFlag(bill.getBlackFlag());//黑灰名单标志
		poolIn.setAccNo(bill.getAccNo());// 电票签约账号
		poolIn.setCustNo(bill.getCustNo());// 核心客户号
		poolIn.setCustName(bill.getCustName());

		poolIn.setEndFlag("0");// 表示新入池
		poolIn.setPoolAgreement(bill.getPoolAgreement());//票据池编号
		poolIn.setAccptrOrg(bill.getAccptrOrg());//承兑人组织机构代码
		poolIn.setBranchId(bill.getSBranchId());//存储网点信息  用于权限分配
		poolIn.setTaskDate(new Date());
		
		
		/*** 融合改造新增字段  start*/
		poolIn.setBeginRangeNo(bill.getBeginRangeNo());//票据开始子区间号
		poolIn.setEndRangeNo(bill.getEndRangeNo());//票据结束子区间号
		poolIn.setStandardAmt(bill.getStandardAmt());//标准金额
		poolIn.setTradeAmt(bill.getTradeAmt());//交易金额(等分化票据实际交易金额)
		poolIn.setDraftSource(bill.getDraftSource());//票据来源
		poolIn.setSplitFlag(bill.getSplitFlag());//是否允许拆分标记 1是 0否
		poolIn.setHilrId(bill.getHilrId());

		/*** 融合改造新增字段  end*/
		
		return poolIn;
	}
	
	private List<PoolBillInfo> queryAutoBill() {
		
		StringBuffer hql = new StringBuffer("select bill from PoolBillInfo bill ");
		hql.append(" where 1=1 ");
		hql.append(" and bill.zyFlag='01' ");//自动入池
		hql.append(" and bill.SDealStatus ='DS_00'");//初始化
		hql.append(" and bill.blackFlag != '02' ");//不在黑名单
		hql.append(" and bill.SBanEndrsmtFlag = '0' ");//可转让		
		hql.append(" and bill.ebkLock != '0' ");//BBSP锁票
		hql.append(" and ((bill.SBillType = 'AC01' and bill.SAcceptorAccount = '0') or bill.SBillType = 'AC02') ");//银承时承兑行账号为0的

		List billList = this.find(hql.toString());
		if (billList != null && billList.size() > 0) {
			return billList;
		}
		return null;


	}

	@Override
	public void txQueryAllBillFromBbsp(boolean isAutoInPool) throws Exception {
		
		if(isAutoInPool){			
			logger.info("【自动】质押入池查询开始---------------------------------------");
		}else{
			logger.info("非自动入池查询BBSP系统客户号签约信息组装开始---------------------------------------");
		}
		
		String accNos = "" ;//用于拼接电票签约账号	
		Map accMap = new HashMap();//用于存放电票签约账号和协议的对应关系
		Map<String,String> keyMap = new HashMap();//存放电票签约账号

		/*
		 * 【单户】自动入池的协议信息查询
		 */
		ProtocolQueryBean queryBean = new ProtocolQueryBean();
		queryBean.setIsGroup(PoolComm.NO);//非集团
		queryBean.setOpenFlag(PoolComm.OPEN_01);//融资业务已开通
		if(isAutoInPool){				
			queryBean.setZyflag(PoolComm.ZY_FLAG_01);//自动入池
		}
		queryBean.setContract("1");//担保合同不可为空（后续再改吧....）
		List<PedProtocolDto> dtoList1 = pedProtocolService.queryProtocolDtoListByQueryBean(queryBean);
		
		
		
		/*
		 * 【单户】自动入池的电票签约账号拼接
		 */
		if(dtoList1 != null && dtoList1.size() > 0){
			logger.info("查询到单户自动质押的协议有["+dtoList1.size()+"]个");
			PedProtocolDto dto = null;
			for (int i = 0; i < dtoList1.size(); i++) {
				//拿到电票签约账号
				dto = (PedProtocolDto) dtoList1.get(i);
				String accNo = dto.getElecDraftAccount();//电票签约账号
				
				logger.info("单户自动质押第"+i+1+"次!协议票据池编号:"+dto.getPoolAgreement()+"电票签约账号:"+accNo);
				
				if(accNo != null && !"".equals(accNo)){
					String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
					for (int j = 0; j < arr.length; j++) {
						accMap.put(arr[j], dto);
						keyMap.put(arr[j],"1");
                        accNos = accNos + "|" + arr[j];
                    }
				}
			}
			logger.info("自动质押的单户电票签约账号为:"+accNos);
		}
		
		
		/*
		 * 【集团】自动入池的协议信息查询
		 */
		queryBean = new ProtocolQueryBean();
		queryBean.setIsGroup("1");
		queryBean.setOpenFlag(PoolComm.OPEN_01);
		queryBean.setContract("1");//担保合同不可为空（后续再改吧....）
		queryBean.setContractDueDt(DateUtils.getCurrDate());
		List<PedProtocolDto> dtoList2 = pedProtocolService.queryProtocolDtoListByQueryBean(queryBean);
		
		
		/*
		 * 【集团】自动入池的电票签约账号拼接
		 */
		if(dtoList2 != null && dtoList2.size() > 0){
			logger.info("查询到集团自动质押的协议有["+dtoList2.size()+"]");
			PedProtocolDto dto = null;
			for (int i = 0; i < dtoList2.size(); i++) {
				
				dto = (PedProtocolDto) dtoList2.get(i);
				logger.info("集团户自动质押第"+i+1+"次!协议票据池编号:"+dto.getPoolAgreement());
				
				/*
				 * 成员信息查询
				 */
				ProListQueryBean bean = new ProListQueryBean();
				bean.setBpsNo(dto.getPoolAgreement());//票据池编号
				List<String> identityList = new ArrayList<String>();
				identityList.add(PoolComm.KHLX_01);
				identityList.add(PoolComm.KHLX_03);
				identityList.add(PoolComm.KHLX_04);
				bean.setCustIdentityList(identityList);
				if(isAutoInPool){						
					bean.setZyflag(PoolComm.ZY_FLAG_01);//自动入池
				}
				List<PedProtocolList> pedList = pedProtocolService.queryProListByQueryBean(bean);
				
				if(pedList != null && pedList.size() > 0){
					for (int j = 0; j < pedList.size(); j++) {
						PedProtocolList pedProtocolList = pedList.get(j);
						String accNo = pedProtocolList.getElecDraftAccount();//电票签约账号
						if(accNo != null && !"".equals(accNo)){
							String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
							for (int k = 0; k < arr.length; k++) {
								accMap.put(arr[k], dto);
								keyMap.put(arr[k],"1");
								accNos = accNos + "|" + arr[k];
							}
						}
						logger.info("自动质押的集团户客户号为:"+pedProtocolList.getCustNo()+"电票签约账号为:"+accNo);
					}
				}

			}
		}

		logger.info("全部签约自动入池的单户及集团的账号:"+accNos);
		
		
		/*
		 * 电票账号处理：全部签约自动入池的电票签约账户分批次查询，每5个电票签约账号查询一次（避免查询BBSP系统字段超长）
		 */
		String accs= "";
		int m = 0 ;
		int n = 0;
		List accNoList = new ArrayList();
		for (String key : keyMap.keySet()){
			accs = accs + "|" + key;
			m++;
			n++;
			if(m >= 5 || keyMap.size() == n){
				m = 1;
				accs = accs.substring(1, accs.length());
				logger.info("Map拼接的账号:"+accs);
				accNoList.add(accs);
				accs = "";
			}
		}
		

		/*
		 * 调用BBSP系统进行持有票据查询
		 */
		if(accNoList!=null && accNoList.size()>0){		
			for (int o = 0 ;o < accNoList.size(); o++ ){
				
				//所有DS_00的新建数据map集合
				Map<String, PoolBillInfo> billMap = new HashMap<String, PoolBillInfo>();
				
				//查询入池的初始化的票据信息
				PoolQueryBean billQueryBean = new PoolQueryBean();
				billQueryBean.setSStatusFlag(PoolComm.DS_00);
				if(isAutoInPool){					
					billQueryBean.setZyFlag(PoolComm.ZY_FLAG_01);//自动入池
				}
				billQueryBean.setIsnotFlag(PoolComm.NOT_ATTRON_FLAG_NO);
				List<PoolBillInfo> poolList = drarftPoolQueryService.queryPoolBillInfoByPram(billQueryBean);
				
				if(poolList != null ){
					logger.info("票据状态为DS_00的票有["+poolList.size()+"]张");
					for (int i = 0; i < poolList.size(); i++) {
						
						PoolBillInfo info = poolList.get(i);
						if(info.getDraftSource().equals(PoolComm.CS01)){
							billMap.put(info.getDiscBillId(), info);
							logger.info("老电票票据id为["+info.getDiscBillId()+"]，票号为："+info.getSBillNo());
						}else{
							billMap.put(info.getHilrId(), info);
							logger.info("新电票持票id为["+info.getHilrId()+"]，票号为："+info.getSBillNo());
						}
						
					}
				}
				
				/*
				 * 持有票据处理操作
				 */
				this.txQueryBillsFromBbsp((String) accNoList.get(o), billMap, accMap,isAutoInPool);
				
				/*
				 * 2.向信贷系统进行额度校验，校验额度不足的，不产生额度
				 */
				blackListManageService.txMisCreditCheck(this.queryCheckBills(true));
				
				/*
				 * 3.自动入池的票据自动入池
				 */
				this.txAutoInPool();

			}
		}
		
		logger.info("质押入池查询结束---------------------------------------");
	}
	
	
	/**
	 * 调用BBSP系统接口查询持有票
	 * @author Ju Nana
	 * @param accNos 电票签约账号集合
	 * @param billMap 大票表中已存在的DS_00初始化的票，key为票号，value为大票表对象
	 * @param accMap 电票签约账号与协议的对应map，key为电票签约协议，value为签约对象
	 * @param isAutoInPool  是否自动入池
	 * @throws Exception
	 * @date 2019-10-17下午2:27:05
	 */
	public void txQueryBillsFromBbsp(String accNos, Map billMap ,Map accMap,boolean isAutoInPool) throws Exception{

		logger.info("根据电票签约账号查询持有票据开始");
		
//		for (int i = 1; i < 100; i++) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());//设置起时间
		    cal.add(Calendar.DATE, 1);//加一天
		    Date dueDt = cal.getTime();//到期日当天的票据不查

		    /*
		     * 调用BBSP系统持有票查询接口
		     */
			ECDSPoolTransNotes transNotes = new ECDSPoolTransNotes();
			transNotes.setApplicantAcctNo(accNos);
			transNotes.setMinDueDt(DateUtils.toString(dueDt, "yyyyMMdd"));
//			transNotes.setCurrentPage(i);
//			transNotes.setPageSize(10000);
			ReturnMessageNew response = poolEcdsService.txApplyPossessBill(transNotes);
//			int b = (Integer) response.getAppHead().get("TOTAL_PAGES");
//			if(b == i ){
//				break;
//			}
			List list = response.getDetails();//从BBSP系统查回的持有票据
			PedProtocolDto pedDto = null ;
			
			/**
			 * 返回信息缺少的字段:是否允许拆分标记、bsp上锁标识、是否我行承兑标志、承兑人组织机构代码
			 */
			
			if(list !=null && list.size() >0 ){
				logger.info("根据电票签约账号查询持有票据结束,持有票据笔数:"+list.size());
				List<PoolBillInfo> billStoreList = new ArrayList<PoolBillInfo>();
				
				for (int j = 0; j < list.size(); j++) {
					Map map = (Map) list.get(j);
					String billNo = getStringVal(map.get("BILL_NO"));
					String beginRangeNo = getStringVal(map.get("START_BILL_NO"));
					String endRangeNo = getStringVal(map.get("END_BILL_NO"));
					
					String billId = getStringVal(map.get("BILL_ID"));
					String holdBillId = getStringVal(map.get("HOLD_BILL_ID"));
					String billSource = getStringVal(map.get("BILL_SOURCE"));
					logger.info("持有票查询回来的数据票号为："+billNo+" 持票id为："+holdBillId+"  票据id为："+billId);
					if(billSource.equals(PoolComm.CS01)){
						if(billMap.get(billId) != null){
							billMap.remove(billId);
						}
					}else{
						if(billMap.get(holdBillId) != null){
							billMap.remove(holdBillId);
						}
					}
					if(getStringVal(map.get("BILL_SOURCE")).equals(PoolComm.CS01) ){
						beginRangeNo = "0";
						endRangeNo = "0";
					}
					PoolBillInfo bill = drarftPoolQueryService.queryObj(billNo,beginRangeNo,endRangeNo);			
					pedDto = (PedProtocolDto) accMap.get(getStringVal(map.get("HOLDER_ACCT_NO")));
					
					logger.info("票号为["+billNo+"],子票起始号["+beginRangeNo+"],子票截止号["+endRangeNo+"]的票,电票账号为["+getStringVal(map.get("HOLDER_ACCT_NO"))+"],对应的核心客户号为["+pedDto+"],锁票标记0锁票1未锁票:["+getStringVal(map.get("CMS_LOCK_FLAG"))+"]");
					
					// 若为空或者已出池则可以落库
					if (bill == null) {//大票表中不存在的票据落库处理
						
						logger.info("票号为[" + billNo + "],子票起始号["+beginRangeNo+"],子票截止号["+endRangeNo+"]信息在大票表中不存在");
						// 先创建新对象然后保存入库
						bill = new PoolBillInfo();
						bill.setDiscBillId(getStringVal(map.get("BILL_ID")));
						bill.setSBillNo(getStringVal(map.get("BILL_NO")));// 票号
						
						/***********************************融合改造新增字段start******************/
						if(getStringVal(map.get("BILL_SOURCE")).equals(PoolComm.CS01) ){
							bill.setBeginRangeNo("0");//子票起始号
							bill.setEndRangeNo("0");//子票截止
						}else{
							bill.setBeginRangeNo(getStringVal(map.get("START_BILL_NO")));//子票起始号
							bill.setEndRangeNo(getStringVal(map.get("END_BILL_NO")));//子票截止
						}
						bill.setStandardAmt(getBigDecimalVal(map.get("STANDARO_AMTSTANDARO_AMT")));//标准金额
						bill.setTradeAmt(getBigDecimalVal(map.get("BILL_AMT")));//交易金额(等分化票据实际交易金额)
						bill.setDraftSource(getStringVal(map.get("BILL_SOURCE")));//票据来源
						bill.setSplitFlag(getStringVal(map.get("IS_SPLIT")));//是否允许拆分标记 1是 0否
						System.out.println("==================================");
						System.out.println(map.get("ACCE_DATE"));
						bill.setSAcceptorDt(getDateVal(map.get("ACCE_DATE")));//承兑日
						bill.setHilrId(getStringVal(map.get("HOLD_BILL_ID")));//持有id
						
						/***********************************融合改造新增字段end******************/
						
						bill.setSBillType(getStringVal(map.get("BILL_TYPE")));
						
						/*if ("1".equals(getStringVal(map.get("BILL_TYPE")))) {// 票据类型-银票
							bill.setSBillType(PoolComm.BILL_TYPE_BANK);
						} else if ("2".equals(getStringVal(map.get("BILL_TYPE")))) {// 票据类型-商票
							bill.setSBillType(PoolComm.BILL_TYPE_BUSI);
						}
*/
						bill.setDIssueDt(getDateVal(map.get("DRAW_BILL_DATE")));// 出票日
						bill.setDDueDt(getDateVal(map.get("REMIT_EXPIPY_DATE")));// 到期日
						bill.setSIssuerName(getStringVal(map.get("DRAWER_NAME")));//出票人全程
						bill.setSIssuerAccount(getStringVal(map.get("DRAWER_ACCT_NO")));//出票人账号
						bill.setSIssuerBankCode(getStringVal(map.get("DRAWER_OPEN_BANK_NO")));//出票人开户行行号
						bill.setSIssuerBankName(getStringVal(map.get("DRAWER_OPEN_BANK_NAME")));//出票人开户行行名
						bill.setSPayeeName(getStringVal(map.get("PAYEE_NAME")));//收款人全程
						bill.setSPayeeAccount(getStringVal(map.get("PAYEE_ACCT_NO")));//收款人账号
						bill.setSPayeeBankName(getStringVal(map.get("PAYEE_OPEN_BANK_NAME")));//收款人开户行行名
						bill.setSPayeeBankCode(getStringVal(map.get("PAYEE_OPEN_BANK_NO")));//收款人开户行行号
						bill.setSAcceptorAccount(getStringVal(map.get("ACCEPTOP_ACCT_NO")));//承兑人账号
						bill.setSAcceptorBankCode(getStringVal(map.get("ACCEPTOP_OPEN_BANK_NO")));//承兑人开户行行号
						
						//承兑行总行
						String acptBankNo = bill.getSAcceptorBankCode();
						Map cpes = blackListManageService.queryCpesMember(acptBankNo);
						if(cpes != null){
							bill.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
							String transBrchClass = (String) cpes.get("transBrchClass");
							String memberName = (String) cpes.get("memberName");//总行行名
							bill.setAcptHeadBankName(memberName);//总行行名
							if(transBrchClass.equals("301")){//财务公司行号
								bill.setCpFlag("1");
							}
						}
						
						bill.setSAcceptorBankName(getStringVal(map.get("ACCEPTOP_OPEN_BANK_NAME")));//承兑人开户行名称
						bill.setSAcceptor(getStringVal(map.get("ACCEPTOP_NAME")));//承兑人全程
						bill.setFBillAmount(getBigDecimalVal(map.get("BILL_AMT")));//票据（包）金额
						if(getStringVal(map.get("UNENDORSE_FLAG")).equals("EM00")){
							bill.setSBanEndrsmtFlag("0");// 可转让 
						}else{
							bill.setSBanEndrsmtFlag("1");// 不可转让 
						}
						if(getStringVal(map.get("BILL_SOURCE")).equals(PoolComm.CS01)){
							bill.setPjsCirculationStatus(getStringVal(map.get("ECDS_BILL_STATUS_LIST")));// bbsp过来的登记状态
						}else{
							bill.setPjsCirculationStatus(getStringVal(map.get("MIS_BILL_STATUS_LIST")));// bbsp过来的登记状态
							
						}
						bill.setSDealStatus(PoolComm.DS_00);// 初始状态未处理
						bill.setAccNo(getStringVal(map.get("HOLDER_ACCT_NO")));// 电票签约账号				
						bill.setSBillMedia(PoolComm.BILL_MEDIA_ELECTRONICAL);// 介质类型-电子
						bill.setSIfDirectAccep(getStringVal(map.get("ACCEPTOR_FLAG")));//是否我行承兑标志   1:我行     0:他行
						bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
						bill.setAccptrOrg(getStringVal(map.get("ACCEPTOP_ORG_CODE")));//承兑人组织机构代码
						bill.setSplitFlag(StringUtils.isBlank(getStringVal(map.get("IS_SPLIT"))) ? "0" : getStringVal(map.get("IS_SPLIT")));//拆分标记
						bill.setClearingFlag(getStringVal(map.get("SETTLE_TYPE")));//结算方式
						
						/**
						 * 新增字段
						 */
						bill.setSIssuerAcctName(getStringVal(map.get("DRAWER_ACCT_NAME")));//出票人账户名称
						bill.setSPayeeAcctName(getStringVal(map.get("PAYEE_ACCT_NAME")));//收款人账户名称
						bill.setSAcceptorAcctName(getStringVal(map.get("ACCEPTOR_ACCT_NAME")));//承兑人账户名称
						bill.setTranRpiorName(getStringVal(map.get("TRAN_RPIOR_NAME")));//直接前手名称
						
						/*
						 *  校验协议是单户还是集团,集团需通过票据池编号及电票签约账号查询成员对象,再赋客户信息
						 */
						if(pedDto.getIsGroup().equals(PoolComm.YES)){//集团
							//集团
							ProListQueryBean queryBean = new ProListQueryBean();
							queryBean.setBpsNo(pedDto.getPoolAgreement());
							queryBean.setEleAccount(getStringVal(map.get("HOLDER_ACCT_NO")));
							PedProtocolList pedList = pedProtocolService.queryProtocolListByQueryBean(queryBean);
							
							bill.setCustNo(pedList.getCustNo());// 核心客户号
							bill.setCustName(pedList.getCustName());// 客户名称
							bill.setSBranchId(pedDto.getOfficeNet());//存储网点信息 用于配置权限
							
						}else if(pedDto.getIsGroup().equals(PoolComm.NO)){//单户
							
							bill.setCustNo(pedDto.getCustnumber());// 核心客户号
							bill.setCustName(pedDto.getCustname());// 客户名称
							bill.setSBranchId(pedDto.getOfficeNet());//存储网点信息 用于配置权限
							
							

						}
						
					} else {//大票表原有数据处理
						
						/*
						 * 如果该票已在入池处理中不再更新数据
						 */
						if(PoolComm.DS_01.equals(bill.getSDealStatus())){
							//------？？？？？？？？？？？？？？？？？？？？---------------------状态？---------------------------------------------------------------------------//
							continue;
						}
						
						logger.info("票号为[" + billNo+ "],子票起始号["+beginRangeNo+"],子票截止号["+endRangeNo+"]信息在大票表中存在,并且状态为["+ bill.getSDealStatus() + "]，获取的网银经办锁值为："+getStringVal(map.get("CMS_LOCK_FLAG")));

						//承兑行总行
						String acptBankNo = bill.getSAcceptorBankCode();
						Map<String,String> cpes = blackListManageService.queryCpesMember(acptBankNo);
						if(cpes != null){
							bill.setAcptHeadBankNo(cpes.get("totalBankNo"));//总行行号
							String transBrchClass = cpes.get("transBrchClass");
							String memberName = cpes.get("memberName");//总行行名
							bill.setAcptHeadBankName(memberName);//总行行号
							if(transBrchClass.equals("301")){//财务公司行号
								bill.setCpFlag("1");
							}
						}
						
						bill.setHilrId(getStringVal(map.get("HOLD_BILL_ID")));//持有id
						bill.setSplitFlag(StringUtils.isBlank(getStringVal(map.get("IS_SPLIT"))) ? "0" : getStringVal(map.get("IS_SPLIT")));//拆分标记
						if(getStringVal(map.get("BILL_SOURCE")).equals(PoolComm.CS01) ){
							bill.setBeginRangeNo("0");//子票起始号
							bill.setEndRangeNo("0");//子票截止
						}else{
							bill.setBeginRangeNo(getStringVal(map.get("START_BILL_NO")));//子票起始号
							bill.setEndRangeNo(getStringVal(map.get("END_BILL_NO")));//子票截止
						}
						bill.setTradeAmt(getBigDecimalVal(map.get("BILL_AMT")));//交易金额(等分化票据实际交易金额)
						
						
						bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
						if(getStringVal(map.get("UNENDORSE_FLAG")).equals("EM00")){
							bill.setSBanEndrsmtFlag("0");// 可转让 
						}else{
							bill.setSBanEndrsmtFlag("1");// 不可转让 
						}
						/**
						 * 新增字段
						 */
						bill.setSIssuerAcctName(getStringVal(map.get("DRAWER_ACCT_NAME")));//出票人账户名称
						bill.setSPayeeAcctName(getStringVal(map.get("PAYEE_ACCT_NAME")));//收款人账户名称
						bill.setSAcceptorAcctName(getStringVal(map.get("ACCEPTOR_ACCT_NAME")));//承兑人账户名称
						bill.setTranRpiorName(getStringVal(map.get("TRAN_RPIOR_NAME")));//直接前手名称
						
						//先看协议是否为集团
						if(pedDto.getIsGroup().equals(PoolComm.NO)){//非集团

							if(!pedDto.getCustnumber().equals(bill.getCustNo())){//新查回的票与库里票不是同一个持有人
								logger.info("票号【"+bill.getSBillNo()+"】数据库中的客户号与新持有的客户号不相同!");
								//1.不同  则重置产品ID,客户信息,票据状态
								if(getStringVal(map.get("BILL_SOURCE")).equals(PoolComm.CS01)){
									bill.setPjsCirculationStatus(getStringVal(map.get("ECDS_BILL_STATUS_LIST")));// bbsp过来的登记状态
								}else{
									bill.setPjsCirculationStatus(getStringVal(map.get("MIS_BILL_STATUS_LIST")));// bbsp过来的登记状态
									
								}
								bill.setSDealStatus(PoolComm.DS_00);// 初始状态未处理
								bill.setAccNo(getStringVal(map.get("HOLDER_ACCT_NO")));// 电票签约账号
								bill.setProduct_id(null);//是否入池标志
								bill.setSIfDirectAccep(getStringVal(map.get("ACCEPTOR_FLAG")));//是否我行承兑标志   1:我行     0:他行
								bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
								bill.setCustNo(pedDto.getCustnumber());// 核心客户号
								bill.setCustName(pedDto.getCustname());// 客户名称
								bill.setSBranchId(pedDto.getOfficeNet());//存储网点信息 用于配置权限

							}else {//新查回的票与库里票是同一个持有人
								logger.info("票号【"+bill.getSBillNo()+"】数据库中的客户号与新持有的客户号相同!");
								if(StringUtil.isEmpty(bill.getProduct_id())){//若产品ID为空 未做过出池操作
									logger.info("票号【"+bill.getSBillNo()+"】数据库中的产品ID为空 未做过出池操作!");
									bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
									if(bill.getSDealStatus().equals(PoolComm.DS_99)){
										bill.setSDealStatus(PoolComm.DS_00);
									}
									
								}else {//产品代码不为空 	看是否已出池
									logger.info("票号【"+bill.getSBillNo()+"】数据库中的产品ID为空 做过出池操作!产品代码值为："+bill.getProduct_id());
									if(bill.getProduct_id().equals(PoolComm.PRODUCT_TYPE_CC)){//出过池  只能手动 入池
										bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
										bill.setSDealStatus(PoolComm.DS_00);// 初始状态未处理
										bill.setAccNo(getStringVal(map.get("HOLDER_ACCT_NO")));// 电票签约账号
										bill.setPoolAgreement(null);//票据池编号
									}
								}
							}
							logger.info("票号【"+bill.getSBillNo()+"】修改后的状态为:"+bill.getSDealStatus());
						}else {
							//集团户   先查子户信息
							ProListQueryBean queryBean = new ProListQueryBean();
							queryBean.setBpsNo(pedDto.getPoolAgreement());
							queryBean.setEleAccount(getStringVal(map.get("HOLDER_ACCT_NO")));
							PedProtocolList pedList = pedProtocolService.queryProtocolListByQueryBean(queryBean);

							if(!pedList.getCustNo().equals(bill.getCustNo())){
								logger.info("客户号不同");
								//1.不同  则重置产品ID,客户信息,票据状态
								if(getStringVal(map.get("BILL_SOURCE")).equals(PoolComm.CS01)){
									bill.setPjsCirculationStatus(getStringVal(map.get("ECDS_BILL_STATUS_LIST")));// bbsp过来的登记状态
								}else{
									bill.setPjsCirculationStatus(getStringVal(map.get("MIS_BILL_STATUS_LIST")));// bbsp过来的登记状态
									
								}
								bill.setSDealStatus(PoolComm.DS_00);// 初始状态未处理
								bill.setAccNo(getStringVal(map.get("HOLDER_ACCT_NO")));// 电票签约账号
								bill.setProduct_id(null);//是否入池标志
								bill.setSIfDirectAccep(getStringVal(map.get("ACCEPTOR_FLAG")));//是否我行承兑标志   1:我行     0:他行
								bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
								bill.setCustNo(pedList.getCustNo());// 核心客户号
								bill.setCustName(pedList.getCustName());// 客户名称
								bill.setSBranchId(pedDto.getOfficeNet());//存储网点信息 用于配置权限

							}else {
								//客户号相同
								logger.info("客户号相同,产品ID为["+bill.getProduct_id()+"],票据状态为["+bill.getSDealStatus()+"]");
								
								if(StringUtil.isEmpty(bill.getProduct_id())){//若产品ID为空 未做过出池操作
									bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
									if(bill.getSDealStatus().equals(PoolComm.DS_99)){
										bill.setSDealStatus(PoolComm.DS_00);// 初始状态未处理
									}
									
								}else {//不为空 	看是否已出池
									if(bill.getProduct_id().equals(PoolComm.PRODUCT_TYPE_CC)){//出过池  只能手动 入池
										bill.setEbkLock(getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
										bill.setSDealStatus(PoolComm.DS_00);// 初始状态未处理
										bill.setAccNo(getStringVal(map.get("HOLDER_ACCT_NO")));// 电票签约账号
										bill.setPoolAgreement(null);//票据池编号

									}
								}
								

							}
							
						}

					}
					
					bill.setEbkLock(StringUtils.isBlank(getStringVal(map.get("CMS_LOCK_FLAG"))) ? "2" : getStringVal(map.get("CMS_LOCK_FLAG")));//bbsp上锁标识	2 未锁票  1 锁票
					
					if(isAutoInPool){//自动质押入池
						
						bill.setZyFlag(PoolComm.ZY_FLAG_01);//自动质押入池
						bill.setPoolAgreement(pedDto.getPoolAgreement());//票据池编号
						
					}
					
					/*
					 * 风险校验，落库操作
					 */
					try {
						//财票改造开关
						String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//在线业务总开关 
						
						if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){	
							
							bill = blackListManageService.txBlacklistCheck(bill, pedDto.getCustnumber());
						}else{
							bill = blackListManageService.txBlacklistAndRiskCheck(bill, pedDto.getCustnumber());
						}
						
						billStoreList.add(bill);
						
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						continue;
					}
					
				}
				financialAdviceService.txStoreAll(billStoreList);
			}
			
			
//		}
		
		
		
		/*
		 * 未调持有票之前自动入池协议下的DS_00数据在去除查完持有票后剩余数据修改客户信息和状态  新增
		 */
		
		
		Set  keySet= billMap.keySet();
		Iterator iterator = keySet.iterator();
		List<PoolBillInfo> billList = new ArrayList<PoolBillInfo>();
		
		while(iterator.hasNext()) {
			Object key = iterator.next();
			PoolBillInfo pool = (PoolBillInfo) billMap.get(key);
			logger.info("票号为["+pool.getSBillNo()+"],子票起始号["+pool.getBeginRangeNo()+"],子票截止号["+pool.getEndRangeNo()+"]的票据状态需要修改为DS_99");
			pool.setSDealStatus(PoolComm.DS_99);
			billList.add(pool);
		}
		financialAdviceService.txStoreAll(billList);

		logger.info("根据客户电票账号查询持有票据结束");
	
	}
	
	@Override
	public List<PoolBillInfo> queryCheckBills(boolean isAutoPoolIn)throws Exception{		
		StringBuffer hql = new StringBuffer("select bill from PoolBillInfo bill ");
		hql.append(" where 1=1 ");
		if(isAutoPoolIn){			
			hql.append(" and bill.zyFlag='01' ");//自动入池
		}
		hql.append(" and bill.SDealStatus ='DS_00'");//初始化
		hql.append(" and bill.blackFlag != '02' ");//不在黑名单
		hql.append(" and bill.SBanEndrsmtFlag = '0' ");//可转让		
		hql.append(" and bill.ebkLock != '0' ");//BBSP锁票
		hql.append(" and bill.rickLevel != 'FX_03' ");//风险标识不为不在风险名单
		List billList = this.find(hql.toString());
		return billList;
	}

	private String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}
	private Date getDateVal(Object obj) throws Exception {
		Date value = null;
		if (obj != null && !obj.equals("null")) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
			}
		}
		return value;
	}

	private BigDecimal getBigDecimalVal(Object obj) throws Exception {
		BigDecimal value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = new BigDecimal(temp);
			}
		}
		return value;
	}

	/**
	 * 通过票号、状态获取池内票据
	 * @param DraftNb assetStatus
	 * @return
	 * @throws Exception
	 */
	@Override
	public DraftPool getDraftPoolByDraftNbOrStatus(String billNo,String startNo, String endNo,String assetStatus) throws Exception{
		StringBuffer hql = new StringBuffer("select dto from DraftPool dto where dto.assetNb =:assetNb and dto.beginRangeNo =:startNo and dto.endRangeNo =:endNo ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		
		parasName.add("assetNb");
		parasValue.add(billNo);	
		
		parasName.add("startNo");
		parasValue.add(startNo);
		
		parasName.add("endNo");
		parasValue.add(endNo);	
		
		if(assetStatus!=null){
			parasName.add("assetStatus");
			parasValue.add(assetStatus);	
			hql.append(" and assetStatus=:assetStatus");
		}
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		if(list != null && list.size() > 0){
			return (DraftPool) list.get(0);
		}
		return null;
	}
	
	public ReturnMessageNew txMisCreditOccupy(PoolBillInfo bill) throws Exception{
		
		ReturnMessageNew resp =new ReturnMessageNew();
		List<Map> reqList =new ArrayList<Map>();
			
		logger.info("电票【"+bill.getSBillNo()+"】占用额度系统额度处理开始...");
		
		String billNo = bill.getSBillNo();//票号
		BigDecimal billAmt = bill.getFBillAmount();//票面金额
		String billType = bill.getSBillType();//票据类型
		String bankNo = bill.getSAcceptorBankCode();//承兑行行号
		String totalBankNo = bill.getAcptHeadBankNo();//承兑行行号--总行
		String totalBankName = bill.getAcptHeadBankName();//承兑行行名--总行
		String acceptor = bill.getSAcceptor();//承兑人全称
		String acptAcctNo = bill.getSAcceptorAccount();//承兑人账号
//		String guarantDiscNo = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
		Map rsuMap = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
		
		/*
		 * 额度系统额度占用接口处理
		 */
		CreditTransNotes note = new CreditTransNotes();
		
		note.setBillType(billType);
		note.setBillMedia(PoolComm.BILL_MEDIA_ELECTRONICAL);//电票
		note.setBillNo(billNo);
		note.setBillsum(billAmt);
		
		if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
			totalBankNo ="";
			totalBankName = acceptor;
		}
		
		note.setBankId(totalBankNo);
		if(rsuMap != null){
			note.setCustomerName((String)rsuMap.get("guarantDiscName"));
			note.setCORE_CLIENT_NO((String)rsuMap.get("guarantDiscNo"));
			
		}else{
			note.setCustomerName(totalBankName);
		}
		
		resp = poolCreditClientService.txPJE023(note);
		
		
		return resp;
		
	}
	
	public ReturnMessageNew txMisCreditOccupy(PoolBillInfo bill,DraftPoolIn poolIn,PedProtocolDto dto) throws Exception{
		
		logger.info("电票【"+poolIn.getPlDraftNb()+"】占用额度系统额度处理开始...");
		
		ReturnMessageNew resp =new ReturnMessageNew();


		String billNo = bill.getSBillNo();//票号
		
		/********************融合改造新增 start******************************/
		String beginRangeNo  = bill.getBeginRangeNo();//票据开始子区间号
		String endRangeNo  = bill.getEndRangeNo();//票据结束子区间号
		/********************融合改造新增 end******************************/
		
		BigDecimal billAmt = bill.getFBillAmount();//票面金额
		String billType = bill.getSBillType();//票据类型
		String bankNo = bill.getSAcceptorBankCode();//承兑行行号
		String totalBankNo = bill.getAcptHeadBankNo();//承兑行行号--总行
		String totalBankName = bill.getAcptHeadBankName();//承兑行行名--总行
		String acceptor = bill.getSAcceptor();//承兑人全称
		String acptAcctNo = bill.getSAcceptorAccount();//承兑人账号
//		String guarantDiscNo = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
		Map rsuMap = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
		
		
//		List<Map> reqList =new ArrayList<Map>();
//		Map<String,Object> map = new HashMap<String, Object>();
//		map.put("BILL_NO", billNo);//票号
//		map.put("BILL_AMT",billAmt );//票面金额
//		map.put("BILL_TYPE", billType);//票据类型
//		map.put("BANK_NO", totalBankNo);//承兑行行号
//		map.put("ACCEPTOR",acceptor);//承兑人名称
//		map.put("GUARANT_DISC_NO",guarantDiscNo);//保贴人编号
//
//		reqList.add(map);
		
		/*
		 * 额度系统额度占用接口处理
		 */
		
		CreditTransNotes note = new CreditTransNotes();
		note.setBillType(billType);
		note.setBillMedia(PoolComm.BILL_MEDIA_ELECTRONICAL);//电票

		if(bill.getSplitFlag().equals("1")){//可拆分的等分化票据
			note.setBillNo(billNo+"-"+beginRangeNo+"-"+endRangeNo);
		}else{
			note.setBillNo(billNo);
		}
		note.setBillsum(billAmt);
		
		
		/********************融合改造新增 start******************************/
		note.setBeginRangeNo(beginRangeNo);
		note.setEndRangeNo(endRangeNo);
		/********************融合改造新增 end******************************/
		
		if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
			totalBankNo ="";
			totalBankName = acceptor;
		}
		
		note.setBankId(totalBankNo);
		if(rsuMap != null){
			note.setCustomerName((String)rsuMap.get("guarantDiscName"));
			note.setCORE_CLIENT_NO((String)rsuMap.get("guarantDiscNo"));
		}else{
			note.setCustomerName(totalBankName);
		}
		
//		note.setReqList(reqList);
		resp = poolCreditClientService.txPJE023(note);
		
		if(resp.isTxSuccess()){
			/*
			 * 获取额度系统返回的列表
			 */
			List respList = resp.getDetails();
			if(respList!=null && respList.size()>0){
//				for(int i=0;i<respList.size();i++){
					Map misRetBillMap =  (Map) respList.get(0);
					
					String misBillNo = (String)misRetBillMap.get("LIMIT_ARRAY.BILL_NO");//票号
					
					/********************融合改造新增 start******************************/
					String misBeginRangeNo = (String)misRetBillMap.get("LIMIT_ARRAY.BeginRangeNo");//票据开始子区间号
					String misEndRangeNo = (String)misRetBillMap.get("LIMIT_ARRAY.EndRangeNo");//票据结束子区间号
					/********************融合改造新增 end******************************/
					
					
					String misCreditType = (String)misRetBillMap.get("LIMIT_ARRAY.USE_LIMIT_TYPE");//额度类型 0-无额度/额度不足  1-低风险 2-高风险
					String GuarantDiscName = (String)misRetBillMap.get("LIMIT_ARRAY.ACCEPTOR_NAME");//占用名称
					String GuarantDiscNo = (String)misRetBillMap.get("LIMIT_ARRAY.ACCEPTOR_CLIENT_NO");//占用核心客户号
					String creditObjType = "";//额度主体类型  1-同业额度  2-对公额度
					String rickLevel = "";//风险等级 低风险、高风险、不在风险名单
					String riskComment = "";//风险说明
					if(null != misRetBillMap.get("LIMIT_ARRAY.USE_POOL_LIMIT_TYPE")){
						creditObjType = (String)misRetBillMap.get("LIMIT_ARRAY.USE_POOL_LIMIT_TYPE");
					}
					
					if("1".equals(misCreditType)){//低风险
						rickLevel = PoolComm.LOW_RISK;
						poolIn.setBtFlag(PoolComm.SP_01);//占用成功
						poolIn.setCreditObjType(creditObjType);
						poolIn.setGuarantDiscName(GuarantDiscName);
						poolIn.setGuarantDiscNo(GuarantDiscNo);
						
						//记录保贴额度信息
						this.txSavePedGuaranteeCredit(poolIn, dto);
						
					}else if("2".equals(misCreditType)){//高风险
						rickLevel = PoolComm.HIGH_RISK;
						poolIn.setBtFlag(PoolComm.SP_01);//占用成功
						poolIn.setCreditObjType(creditObjType);
						poolIn.setGuarantDiscName(GuarantDiscName);
						poolIn.setGuarantDiscNo(GuarantDiscNo);
						
						//记录保贴额度信息
						this.txSavePedGuaranteeCredit(poolIn, dto);
						
					}else{//不在名单
						rickLevel = PoolComm.NOTIN_RISK;
						riskComment = "|非本行额度名单票据";
					}
					
					/*
					 * 大票表落库处理
					 */
					bill.setRickLevel(rickLevel);//风险等级
					bill.setCreditObjType(creditObjType);//额度主体类型
					if(null!=bill.getRiskComment()){						
						String[] comment = bill.getRiskComment().split("//|");
						bill.setRiskComment(comment[0] + riskComment);
					}else{
						bill.setRiskComment(riskComment);
					}
					
					blackListManageService.txStore(bill);
					
					/*
					 * poolIn表落库处理
					 */
					poolIn.setRickLevel(rickLevel);
					poolIn.setRiskComment(bill.getRiskComment());

					poolIn.setTaskDate(new Date());
					
					blackListManageService.txStore(poolIn);
					
				}
//			}
			
		}else{
			
			bill.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
			poolIn.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
			poolIn.setTaskDate(new Date());
			this.txStore(poolIn);
			this.txStore(bill);
			
			//注意：额度占用失败也返回成功，只是不产生额度
			resp.getRet().setRET_CODE(Constants.TX_SUCCESS_CODE);
			resp.setTxSuccess(true);
			
		}
		
		return resp;
				
	}
	
	
	public void txSavePedGuaranteeCredit(DraftPoolIn poolIn,PedProtocolDto dto) throws Exception{
		
		logger.info("【"+poolIn.getPlDraftNb()+"】保贴额度占用实体生成开始...");
		
		PoolQueryBean pBean = new PoolQueryBean();
		 pBean.setProtocolNo(poolIn.getPoolAgreement());
		 pBean.setBillNo(poolIn.getPlDraftNb());
		 
			/********************融合改造新增 start******************************/
		 pBean.setBeginRangeNo(poolIn.getBeginRangeNo());
		pBean.setEndRangeNo(poolIn.getEndRangeNo());
		/********************融合改造新增 end******************************/
		 
		 PedGuaranteeCredit pedCredit = poolCreditProductService.queryByBean(pBean);
		 
		 if(pedCredit == null ){
			 pedCredit = new PedGuaranteeCredit();
		 }
		 pedCredit.setBpsNo(poolIn.getPoolAgreement());
		 pedCredit.setCustNo(poolIn.getCustNo());
		 pedCredit.setBpsName(dto.getPoolName());
		 if(dto.getIsGroup().equals(PoolComm.YES)){
			 //集团户
			 
			 ProListQueryBean quer = new ProListQueryBean();
			 quer.setBpsNo(dto.getPoolAgreement());
			 quer.setCustNo(poolIn.getCustNo());
			 PedProtocolList ped = pedProtocolService.queryProtocolListByQueryBean(quer);
			 pedCredit.setCustName(ped.getCustName());
		 }else {
			 pedCredit.setCustName(dto.getCustname());
		 }
		 pedCredit.setBillNo(poolIn.getPlDraftNb());
		 pedCredit.setBillType(poolIn.getPlDraftType());
		 pedCredit.setBillAmt(poolIn.getPlIsseAmt());
		 pedCredit.setAcceptorOrg(poolIn.getAccptrOrg());
		 pedCredit.setStatus(PoolComm.SP_01);
		 pedCredit.setAcceptor(poolIn.getPlAccptrNm());
		 pedCredit.setCreateTime(new Date());
		 pedCredit.setIsGroup(dto.getIsGroup());
		 pedCredit.setCreditObjType(poolIn.getCreditObjType());
		 pedCredit.setGuarantDiscName(poolIn.getGuarantDiscName());
		 pedCredit.setGuarantDiscNo(poolIn.getGuarantDiscNo());
		 
			/********************融合改造新增 start******************************/
		 pedCredit.setBeginRangeNo(poolIn.getBeginRangeNo());//票据开始子区间号
		 pedCredit.setEndRangeNo(poolIn.getEndRangeNo());//票据结束子区间号
			/********************融合改造新增 end******************************/
		 
		 this.txStore(pedCredit);
	}
	
	public void txSavePedGuaranteeCredit(PoolVtrust vtrust,PedProtocolDto dto,String clientNo) throws Exception{
		
		logger.info("【"+vtrust.getVtNb()+"】保贴额度占用实体生成开始...");
		
		logger.info("柜面入池PedGuaranteeCredit额度系统额度占用记录表数据处理......");
		
		PoolQueryBean pBean = new PoolQueryBean();
		pBean.setProtocolNo(dto.getPoolAgreement());
		pBean.setBillNo(vtrust.getVtNb());
		
		/********************融合改造新增 start******************************/
		pBean.setBeginRangeNo(vtrust.getBeginRangeNo());
		pBean.setEndRangeNo(vtrust.getEndRangeNo());
		/********************融合改造新增 end******************************/
		
		PedGuaranteeCredit pedCredit = poolCreditProductService.queryByBean(pBean);
		
		if(pedCredit == null ){
			pedCredit = new PedGuaranteeCredit();
		}
		vtrust.setBtFlag(PoolComm.SP_01);//占用成功
		pedCredit.setBpsNo(dto.getPoolAgreement());
		pedCredit.setBpsName(dto.getPoolName());
		if(dto.getIsGroup().equals(PoolComm.YES)){
			//集团户
			
			ProListQueryBean quer = new ProListQueryBean();
			quer.setBpsNo(dto.getPoolAgreement());
			quer.setCustNo(clientNo);
			PedProtocolList ped = pedProtocolService.queryProtocolListByQueryBean(quer);
			pedCredit.setCustName(ped.getCustName());
			vtrust.setVtEntpName(ped.getCustName());
		}else {
			pedCredit.setCustName(dto.getCustname());
			vtrust.setVtEntpName(dto.getCustname());
		}
		pedCredit.setCustNo(clientNo);
		pedCredit.setBillNo(vtrust.getVtNb());
		pedCredit.setBillType(vtrust.getVtType());
		pedCredit.setBillAmt(vtrust.getVtisseAmt());
		pedCredit.setStatus(PoolComm.SP_01);
		pedCredit.setAcceptor(vtrust.getVtaccptrName());
		pedCredit.setCreateTime(new Date());
		pedCredit.setIsGroup(dto.getIsGroup());
		this.txStore(pedCredit);
	}
	
	
	@Override
	public List<String> txMisCreditOccupy(List<PoolVtrust> bills,String clientNo, PedProtocolDto dto) throws Exception {
		
		logger.info("纸票占用额度系统额度处理开始...");
		
		Map<String , PoolVtrust> vtBills =new HashMap<String , PoolVtrust>();
		ReturnMessageNew resp =new ReturnMessageNew();
		List<Map> reqList =new ArrayList<Map>();
		List<String> sucessList = new ArrayList<String>();//用来记录占用成功的票号
		
		for(PoolVtrust pv : bills ){
			
			String billNo = pv.getVtNb();//票号
			BigDecimal billAmt = pv.getVtisseAmt();//票面金额
			String billType = pv.getVtType();//票据类型
			String bankNo = pv.getVtaccptrBankAccount();//承兑行行号
    		String totalBankNo = pv.getAcptHeadBankNo();//承兑行行号--总行
    		String totalBankName = pv.getAcptHeadBankName();//承兑行行名--总行
			String acceptor = pv.getVtaccptrName();//承兑人全称
			String acptAcctNo = pv.getVtaccptrAccount();//承兑人账号
//			String guarantDiscNo = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
			Map rsuMap = blackListManageService.queryGuarantNo(acceptor, acptAcctNo, bankNo, billType,totalBankNo);//保贴人编号
			
			if(rsuMap != null){
				pv.setGuarantDiscName((String)rsuMap.get("guarantDiscName"));
				pv.setGuarantDiscNo((String)rsuMap.get("guarantDiscNo"));
			}else{
				pv.setGuarantDiscName(acceptor);
			}
			vtBills.put(billNo, pv);
			logger.info("赋值之前：承兑行行号："+bankNo+"，承兑行行号--总行："+totalBankNo+"，承兑人全称："+acceptor);
			
			/*
			 * 额度系统额度占用接口处理
			 */
			CreditTransNotes note = new CreditTransNotes();
			
			note.setBillType(billType);
			note.setBillMedia(PoolComm.BILL_MEDIA_PAPERY);//电票
			note.setBillNo(billNo);
			note.setBillsum(billAmt);
			
			if(PoolComm.BILL_TYPE_BUSI.equals(billType)){//商票
				totalBankNo ="";
				totalBankName = acceptor;
			}
			
			note.setBankId(totalBankNo);
			
			
			
			if(rsuMap != null){
				note.setCustomerName((String)rsuMap.get("guarantDiscName"));
				note.setCORE_CLIENT_NO((String)rsuMap.get("guarantDiscNo"));
			}else{
				note.setCustomerName(totalBankName);
			}
			logger.info("赋值之后：承兑行行号："+note.getBankId()+"，核心客户号："+note.getCORE_CLIENT_NO());
			resp = poolCreditClientService.txPJE023(note);
			
			if(resp.isTxSuccess()){
				/*
				 * 获取额度系统返回的列表
				 */
				List respList = resp.getDetails();
				if(respList!=null && respList.size()>0){
//					for(int i=0;i<respList.size();i++){
						Map misRetBillMap =  (Map) respList.get(0);
						
						String misBillNo = (String)misRetBillMap.get("LIMIT_ARRAY.BILL_NO");//票号
						String misCreditType = (String)misRetBillMap.get("LIMIT_ARRAY.USE_LIMIT_TYPE");//额度类型 0-无额度/额度不足  1-低风险 2-高风险
						String creditObjType = "";//额度主体类型  1-同业额度  2-对公额度
						String rickLevel = "";//风险等级 低风险、高风险、不在风险名单
						String riskComment = "";//风险说明
						if(null != misRetBillMap.get("LIMIT_ARRAY.USE_POOL_LIMIT_TYPE")){
							creditObjType = (String)misRetBillMap.get("LIMIT_ARRAY.USE_POOL_LIMIT_TYPE");
						}
						
						pv.setGuarantDiscName((String)misRetBillMap.get("LIMIT_ARRAY.ACCEPTOR_NAME"));
						pv.setGuarantDiscNo((String)misRetBillMap.get("LIMIT_ARRAY.ACCEPTOR_CLIENT_NO"));
						pv.setCreditObjType(creditObjType);
						
						if("1".equals(misCreditType)){//低风险
							rickLevel = PoolComm.LOW_RISK;
							pv.setBtFlag(PoolComm.SP_01);//占用成功
							sucessList.add(misBillNo);
							//记录保贴额度信息
							this.txSavePedGuaranteeCredit(pv, dto,clientNo);
							
						}else if("2".equals(misCreditType)){//高风险
							rickLevel = PoolComm.HIGH_RISK;
							pv.setBtFlag(PoolComm.SP_01);//占用成功
							sucessList.add(misBillNo);
							//记录保贴额度信息
							this.txSavePedGuaranteeCredit(pv, dto,clientNo);
							
						}else{//不在名单
							rickLevel = PoolComm.NOTIN_RISK;
							riskComment = "|非本行额度名单票据";
						}
						
						/*
						 * vtrust表落库处理
						 */
						pv.setRickLevel(rickLevel);
						pv.setRiskComment(pv.getRiskComment());
						pv.setCreditObjType(creditObjType);
						
						blackListManageService.txStore(pv);
						
						
					}
//				}
				
			}else{
				
				pv.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
				pv.setRickLevel(PoolComm.NOTIN_RISK);//不在风险名单
				this.txStore(pv);
			}
		}
		
		return sucessList;
	}
	
	

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PoolBillInfo> queryPoolBillInfoListByTotalBank(List<String> acptHeadBankNos) throws Exception {
		StringBuffer hql = new StringBuffer("select bill from PoolBillInfo bill where bill.acptHeadBankNo in (:acptHeadBankNos) and bill.creditObjType='1' and bill.SDealStatus in(:SDealStatus) ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		
		parasName.add("acptHeadBankNos");
		parasValue.add(acptHeadBankNos);	
		
		List<String> SDealStatus = new ArrayList<String>(); 
		SDealStatus.add(PoolComm.DS_02);
		SDealStatus.add(PoolComm.DS_06);
		parasName.add("SDealStatus");
		parasValue.add(SDealStatus);
		
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		
		if(null != list && list.size()>0){			
			return list;
		}
		
		return null;
		
	}

	@Override
	public List<DraftPool> queryDraftPoolListByTotalBank(List<String> acptHeadBankNos) throws Exception {
		
		StringBuffer hql = new StringBuffer("select bill from DraftPool bill where bill.acptHeadBankNo in (:acptHeadBankNos) and bill.creditObjType='1' and bill.assetStatus in (:status) ");
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		
		parasName.add("acptHeadBankNos");
		parasValue.add(acptHeadBankNos);	
		
		List<String> status = new ArrayList<String>(); 
		status.add(PoolComm.DS_02);
		status.add(PoolComm.DS_06);
		parasName.add("status");
		parasValue.add(status);
			
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		
		if(null != list && list.size()>0){			
			return list;
		}
		
		return null;
	}

	@Override
	public PoolBillInfo loadBySplit(String splitId, String status)
			throws Exception {
		String sql = "select obj from PoolBillInfo obj where 1=1 ";
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名

		if(splitId != null && StringUtils.isNotEmpty(splitId)){
			sql = sql + " and obj.splitId = :splitId ";
			parasName.add("splitId");
			parasValue.add(splitId);
		}
		if(status != null && StringUtils.isNotEmpty(status)){
			sql = sql + " and obj.SDealStatus = :SDealStatus ";
			parasName.add("SDealStatus");
			parasValue.add(status);
		}
		
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(sql, nameForSetVar, parameters);
		if(list!=null&&list.size()>0){
			return (PoolBillInfo) list.get(0);
		}
		return null;
	}


}
