package com.mingtech.application.pool.creditmanage.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterHis;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("creditRegisterService")
public class CreditRegisterServiceImpl extends GenericServiceImpl implements CreditRegisterService {
    private static final Logger logger = Logger.getLogger(CreditRegisterServiceImpl.class);
    @Autowired
    private PoolCreditProductService poolCreditProductService;
    @Autowired
    private PedProtocolService pedProtocolService;
    @Autowired
    private PedAssetPoolService pedAssetPoolService;
    @Autowired
    private FinancialService financialService; 
    @Autowired
    private PedOnlineCrdtService pedOnlineCrdtService;
    @Autowired
    private PedOnlineAcptService pedOnlineAcptService;
    @Autowired
    private PedAssetTypeService pedAssetTypeService;
    /**
     * <p>描述：查询所有的合同融资登记信息</p>
     */
    @Override
    public List queryAllCreditCont() {
        List value = new ArrayList();
        String hql = " from CreditRegister where voucherType = ?";
        value.add("0");
        return this.find(hql,value);
    }
    /**
     * <p>描述：根据ids查询所有的合同融资登记信息</p>
     */
    @Override
    public List<CreditRegister> queryAllCreditContByBusiIds(List ids) {
    	
		StringBuffer hql = new StringBuffer("select cr from CreditRegister cr where 1=1 ");
		List parasName = new ArrayList();
		List parasValue = new ArrayList();

		hql.append(" and cr.busiId in(:ids)");
		parasName.add("ids");
		parasValue.add(ids);

		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		
        if(null!=list&&list.size()>0){
        	return list;
        }
        return null;
    }

    /**
     * <p>描述：查询该合同下所有的借据信息</p>
     */
    @Override
    public List<CreditRegister> queryCreditContByNo(String contNo,String busiNo,String voucherType ) {
        List values = new ArrayList();
        String hql = " from CreditRegister cr where 1=1 ";
        if(StringUtil.isNotBlank(contNo)){
        	hql += " and cr.contractNo = ? ";
        	values.add(contNo);
        }
        if(StringUtil.isNotBlank(busiNo)){
        	hql += " and cr.busiNo = ? ";
        	values.add(busiNo);
        }
        if(StringUtil.isNotBlank(voucherType)){
        	hql += " and cr.voucherType = ?";
        	values.add(voucherType);
        }
        List<CreditRegister> list = (List<CreditRegister>)this.find(hql,values); 
        if(null != list && list.size()>0){
        	return list;
        }
        return null;
    }

    /**
     * <p>描述：查询该合同编号下所有有效借据的总有效金额</p>
     * @param  contNo 合同编号
     * @return
     */
    public BigDecimal queryCountAmout(String contNo){
        BigDecimal coutAmount = new BigDecimal(0);
        List values = new ArrayList();
        String hql = " select sum（cr.occupyAmount） from CreditRegister cr where cr.contractNo = ? and cr.voucherType = ? ";
        values.add(contNo);
        values.add(PoolComm.VT_1);//借据
        List list = this.find(hql,values);
        if(null!= list && list.size()>0){
            coutAmount = (BigDecimal) list.get(0);
        }
        return coutAmount;
    }
  /** --------------------------     融资合同封装方法开始  start        ---------------------------------     */
    /**
     * <p>描述：保存融资业务用信登记信息</p>
     * @param creditReg 融资业务用信保存
     * (用信对象必输项- bpsNo、busiId、busiNo、voucherType、contractNo、isOnline、busiType、riskType、busiAmount、occupyRatio、dueDt;//资产到期日)
     * @return
     * @throws Exception
     */
    @Override
    public void txSaveCreditRegister(CreditRegister creditReg) throws Exception {
        BigDecimal realCredit = creditReg.getOccupyAmount().multiply(creditReg.getOccupyRatio()).setScale(2,BigDecimal.ROUND_UP);
        creditReg.setOccupyCredit(realCredit);; //实际占用额度  四舍五入，保留2位小数
        creditReg.setCreateDate(DateUtils.getWorkDayDate());//创建时间
        creditReg.setUpdateDate(new Date());//更新时间
        creditReg.setTransDate(DateUtils.getCurrDate());//交易时间
        this.txStore(creditReg);
        this.txCreateRegisterHis(creditReg);//保存历史
    }
    /**
     * <p>描述:更新融资业务登记信息 </p>
     * @param creditReg 融资业务用信
     * 统一更新接口
     * @return void
     * @throws Exception
     */
    @Override
    public void txUpdateCreditRegister(CreditRegister creditReg) throws Exception {
        //每次更新都要将数据保留历史表中
        CreditRegister credit = (CreditRegister) this.load(creditReg.getId(),CreditRegister.class);
        BeanUtil.beanCopy(creditReg,credit);
        BigDecimal realCredit = credit.getOccupyAmount().multiply(credit.getOccupyRatio()).setScale(2,BigDecimal.ROUND_UP);
        credit.setOccupyCredit(realCredit);; //实际占用额度  四舍五入，保留2位小数
        credit.setUpdateDate(DateUtils.getWorkDayDate());//更新日期
        this.txStore(credit);
        this.txCreateRegisterHis(credit);//保存历史
    }


    @Override
    public void txDelCreditRegisterByBusiIds(List ids) throws Exception {
        CreditRegister credit = null;
        List credits = null;
         
    	if(null!=ids && ids.size()>0){
    		credits = this.queryAllCreditContByBusiIds(ids);
    		if(null != credits && credits.size()>0){
    			for(int i=0;i<credits.size();i++){
    				
    				credit = (CreditRegister) credits.get(i);
    				
    				credit.setTransDate(DateTimeUtil.parse("2099-12-31"));
    				this.txCreateRegisterHis(credit);//保存历史
    			}
    			this.txDeleteAll(credits);
    		}
        }
    }
    /**
     * <p>描述:判断该合同项下所有的有效借据 额之和是否等于合同金额</p>
     *  若相等则删除该合同
     * @param register 用信登记对象
     */
    public void checkAmtIsSame(CreditRegister register) throws Exception{
        //查询该合同下，∑借据金额（有效借据）
        BigDecimal amout = this.queryCountAmout(register.getContractNo());
        //查找主合同信息
        List list= this.queryCreditContByNo(register.getContractNo(),null,PoolComm.VT_0);
        CreditRegister crt = (CreditRegister) list.get(0);
        if(amout.compareTo(crt.getBusiAmount())==0){
            //删除该合同信息
            this.txDelete(crt);
          //更新历史信息
            crt.setDueDt(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));
            this.txCreateRegisterHis(crt);
        }else if (amout.compareTo(crt.getBusiAmount())<0){
            //实际占用金额】调整为合同金额-∑借据金额（有效借据）
            BigDecimal occupyAmount = crt.getBusiAmount().subtract(amout);
            crt.setOccupyAmount(occupyAmount);
            //更新合同信息
            this.txUpdateCreditRegister(crt);
            //更新历史信息
            this.txCreateRegisterHis(crt);
        }
    }

    /**
     * <p>描述:自动保存历史信息</p>
     * @param creditRegister
     */
    public void txCreateRegisterHis(CreditRegister creditRegister) throws  Exception{
        CreditRegisterHis his = new CreditRegisterHis();
        BeanUtil.beanCopy(creditRegister,his);
        his.setCreateDate(new Date());
        his.setUpdateDate(new Date());
        this.txStore(his);
    }

    /** --------------------------     融资合同封装方法结束  end        ---------------------------------     */

    @Override
    public String getEntityName() {
        return null;
    }

    @Override
    public Class getEntityClass() {
        return null;
    }
    
    
    
	@Override
	public CreditRegister createCreditRegister(CreditProduct product,PedProtocolDto pro,String apId)throws Exception {
		
		CreditRegister crdtReg = this.queryCreditRegisterByBusiId(product.getId());
		
		if(null!=crdtReg){//非首次登记
			
			crdtReg.setOccupyAmount(product.getRestAmt());
			crdtReg.setUpdateDate(new Date());//更新日期(含时分秒)
			
			crdtReg.setRiskType(product.getRisklevel());// 风险等级  用于处理风险等级发生变化的情况
			crdtReg.setOccupyRatio(new BigDecimal(product.getCcupy()));//占用比例  用于处理占用比例发生变化的情况
			crdtReg.setDueDt(product.getMinDueDate());//到期日 用于处理到期日发生变化的情况
			
		}else{//首次登记			
			crdtReg = new CreditRegister();
			crdtReg.setApId(apId);//资产池表主键
			crdtReg.setBpsNo(product.getBpsNo());//票据池协议编号
			crdtReg.setBusiId(product.getId());// 外键ID，存主业务合同表ID或者借据表ID
			crdtReg.setBusiNo(product.getCrdtNo());//  主业务合同号OR借据号
			crdtReg.setVoucherType(PoolComm.VT_0);// 0：主业务合同 
			crdtReg.setContractNo(product.getCrdtNo());//主业务合同号
			crdtReg.setIsOnline(product.getIsOnline());//是否线上业务
			crdtReg.setBusiType(product.getCrdtType());//业务类型
			crdtReg.setRiskType(product.getRisklevel());// 风险等级
			crdtReg.setBusiAmount(product.getUseAmt());//业务金额(合同金额、借据金额)
			crdtReg.setOccupyRatio(new BigDecimal(product.getCcupy()));//占用比例
			crdtReg.setOccupyAmount(product.getUseAmt());//业务实际占用金额-上限=业务金额
			crdtReg.setDueDt(product.getMinDueDate());//到期日
			crdtReg.setTransDate(new Date());//交易日期(不含时分秒)
			crdtReg.setCreateDate(new Date());//创建日期(含时分秒)
			crdtReg.setUpdateDate(new Date());//更新日期(含时分秒)
		}
		
		return crdtReg;
	}
	
	@Override
	public CreditRegister createCreditRegister(PedCreditDetail loan,PedProtocolDto pro,String apId)throws Exception {
		
		CreditRegister crdtReg = this.queryCreditRegisterByBusiId(loan.getCreditDetailId());
		CreditProduct product = poolCreditProductService.queryProductByCreditNo(loan.getCrdtNo(), PoolComm.JQZT_WJQ);
		
		if(null!=crdtReg){//非首次登记
			crdtReg.setOccupyAmount(loan.getActualAmount());//实际占用额度
			crdtReg.setUpdateDate(new Date());//更新日期(含时分秒)	
			crdtReg.setDueDt(loan.getEndTime());//到期日   --这里赋值是为了处理到期日变更的情况
			
			if(null != product){				
				crdtReg.setOccupyRatio(new BigDecimal(product.getCcupy()));//占用比例  --这里赋值是为了处理系数变更的情况
				crdtReg.setRiskType(product.getRisklevel());// 风险等级  --风险等级发生变化的情况
				
			}
		}else{//首次登记			
			crdtReg = new CreditRegister();
			crdtReg.setApId(apId);//资产池表主键
			crdtReg.setBpsNo(pro.getPoolAgreement());//票据池协议编号
			crdtReg.setBusiId(loan.getCreditDetailId());// 外键ID，存主业务合同表ID或者借据表ID
			crdtReg.setBusiNo(loan.getLoanNo());//  主业务合同号OR借据号
			crdtReg.setVoucherType(PoolComm.VT_1);// 0：借据号 
			crdtReg.setContractNo(loan.getCrdtNo());//主业务合同号
			crdtReg.setBusiAmount(loan.getLoanAmount());//业务金额(合同金额、借据金额)
			crdtReg.setOccupyAmount(loan.getActualAmount());//业务实际占用金额
			crdtReg.setBusiType(loan.getLoanType());//业务类型
			crdtReg.setDueDt(loan.getEndTime());//到期日
			crdtReg.setTransDate(new Date());//交易日期(不含时分秒)
			crdtReg.setCreateDate(new Date());//创建日期(含时分秒)
			crdtReg.setUpdateDate(new Date());//更新日期(含时分秒)
			
			
			/*
			 * 针对线上业务的特殊处理
			 */
			BigDecimal onlineCreditOccupyRatio = new BigDecimal("1");//线上业务比例-线上银承为1，线上流贷根据协议中的比例获取
			if((null == product&&PoolComm.XD_02.equals(loan.getLoanType()))||PoolComm.XD_02.equals(loan.getLoanType()) && (PoolComm.YES.equals(product.getIsOnline()))){//线上流贷
				PlOnlineCrdt crdt = pedOnlineCrdtService.queryonlineCrdtByContractNo(loan.getCrdtNo());
				onlineCreditOccupyRatio = crdt.getPoolCreditRatio().divide(new BigDecimal("100"));
			}
			
			if(null != product){
				crdtReg.setIsOnline(product.getIsOnline());//是否线上业务
				crdtReg.setRiskType(product.getRisklevel());// 风险等级
				crdtReg.setOccupyRatio(new BigDecimal(product.getCcupy()));//占用比例
				if(PoolComm.YES.equals(product.getIsOnline())){//线上业务
					crdtReg.setIsOnline(PoolComm.YES);//是否线上业务			
					crdtReg.setRiskType(PoolComm.LOW_RISK);// 风险等级
				}
			}
			if(null == product){
				crdtReg.setIsOnline(PoolComm.YES);//是否线上业务			
				crdtReg.setRiskType(PoolComm.LOW_RISK);// 风险等级
				crdtReg.setOccupyRatio(onlineCreditOccupyRatio);//占用比例				
			}
			
		}
		
		return crdtReg;
	}
	
	@Override
	public CreditRegister queryCreditRegisterByBusiId(String busiId)throws Exception {
		List<String> paramName = new ArrayList<String>();// 名称
		List<String> paramValue = new ArrayList<String>();// 值
		
		String hql = "select cr from CreditRegister cr where cr.busiId =:busiId " ;
		
		paramName.add("busiId");
		paramValue.add(busiId);

		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		
		List<CreditRegister>  result = this.find(hql,paramNames,paramValues);
		
		if(result!=null && result.size()>0){
			return result.get(0);
		}
		return null;		
	}
	@Override
	public CreditRegisterCache createCreditRegisterCache(CreditProduct product,PedProtocolDto pro, String apId) throws Exception {
		
		CreditRegisterCache crdtReg = new CreditRegisterCache();
		crdtReg.setApId(apId);//资产池表主键
		crdtReg.setBpsNo(pro.getPoolAgreement());//票据池协议编号
		crdtReg.setBusiId(product.getId());// 外键ID，存主业务合同表ID或者借据表ID
		crdtReg.setBusiNo(product.getCrdtNo());//  主业务合同号OR借据号
		crdtReg.setVoucherType(PoolComm.VT_0);// 0：主业务合同 
		crdtReg.setContractNo(product.getCrdtNo());//主业务合同号
		crdtReg.setIsOnline(product.getIsOnline());//是否线上业务
		crdtReg.setBusiType(product.getCrdtType());//业务类型
		crdtReg.setRiskType(product.getRisklevel());// 风险等级
		crdtReg.setBusiAmount(product.getUseAmt());//业务金额(合同金额、借据金额)
		crdtReg.setOccupyRatio(new BigDecimal(product.getCcupy()));//占用比例-0.##-1
		crdtReg.setOccupyAmount(product.getUseAmt());//业务实际占用金额-上限=业务金额
		crdtReg.setOccupyCredit(product.getUseAmt().multiply(crdtReg.getOccupyRatio()).setScale(2,BigDecimal.ROUND_HALF_UP));//实际占用额度		
		crdtReg.setDueDt(product.getCrdtDueDt());//到期日
		crdtReg.setTransDate(new Date());//交易日期(不含时分秒)
		crdtReg.setCreateDate(new Date());//创建日期(含时分秒)
		crdtReg.setUpdateDate(new Date());//更新日期(含时分秒)
		
		return crdtReg;
	}
	
	@Override
	public CreditRegisterCache createCreditRegisterCache(PedCreditDetail crdtDetail,CreditProduct product,PedProtocolDto pro, String apId) throws Exception {
		
		
		/*
		 * 注意：该方法只适用于在线业务
		 */
		
		CreditRegisterCache crdtReg = new CreditRegisterCache();
		crdtReg.setApId(apId);//资产池表主键
		crdtReg.setBpsNo(pro.getPoolAgreement());//票据池协议编号
		crdtReg.setBusiId(crdtDetail.getCreditDetailId());// 外键ID，存主业务合同表ID或者借据表ID
		crdtReg.setBusiNo(crdtDetail.getLoanNo());//  主业务合同号OR借据号
		crdtReg.setVoucherType(PoolComm.VT_1);// 1：借据号 
		crdtReg.setContractNo(crdtDetail.getCrdtNo());//主业务合同号
		crdtReg.setIsOnline(PoolComm.YES);//是否线上业务--是
		crdtReg.setBusiType(crdtDetail.getLoanType());//业务类型
		crdtReg.setRiskType(PoolComm.LOW_RISK);// 风险等级--低风险
		crdtReg.setBusiAmount(crdtDetail.getLoanAmount());//业务金额(合同金额、借据金额)
		crdtReg.setOccupyRatio(new BigDecimal(product.getCcupy()));//占用比例-0.##-1
		crdtReg.setOccupyAmount(crdtDetail.getActualAmount());//业务实际占用金额-上限=业务金额
		crdtReg.setOccupyCredit(crdtDetail.getActualAmount().multiply(new BigDecimal(product.getCcupy())));//实际占用额度
		crdtReg.setDueDt(crdtDetail.getEndTime());//到期日
		crdtReg.setTransDate(new Date());//交易日期(不含时分秒)
		crdtReg.setCreateDate(new Date());//创建日期(含时分秒)
		crdtReg.setUpdateDate(new Date());//更新日期(含时分秒)
		
		return crdtReg;
	}
	@Override
	public Ret txReleaseCreditByProduct(CreditProduct product, String releaseType ,BigDecimal releaseAmt)throws Exception {
		
		logger.info("根据主业务合同释放额度，处理主业务合同号【"+product.getCrdtNo()+"】释放类型【"+releaseType+"】");
		if(null == releaseAmt){
			releaseAmt = BigDecimal.ZERO;
		}
		
		Ret ret = new Ret();
		if(PoolComm.JQ_02.equals(product.getCrdtStatus())&&releaseAmt.compareTo(BigDecimal.ZERO)==0){//【日终】提前终止出账，直接将合同移出
			/*
			 * 移出融资业务登记表
			 */
			List<String> busiIds = new ArrayList<String>();
			busiIds.add(product.getId());
			this.txDelCreditRegisterByBusiIds(busiIds);
			ret.setRET_CODE(Constants.TX_SUCCESS);
			return ret;
		}
		
		if(PoolComm.EDSF_01.equals(releaseType)){
			/*
			 * 结清释放：将主业务合同与主业务合同所对应的全部借据移出融资业务登记表
			 */
			
			
			List<String> idList = new ArrayList<String>();//主业务合同ID极其向下的所有尚需处理的借据ID
			
			StringBuffer hql = new StringBuffer();
			hql.append(" select pd.creditDetailId  from PedCreditDetail pd where detailStatus = '1' ");
			List<String> value = new ArrayList<String>();
			List<String> key = new ArrayList<String>();
			
			hql.append("  and pd.crdtNo=:crdtNo ");
			value.add("crdtNo");
			key.add(product.getCrdtNo());
			
			String paramNames[] = (String[]) value.toArray(new String[value.size()]);
			Object paramValues[] = key.toArray();
			idList = this.find(hql.toString(), paramNames, paramValues);
			
			idList.add(product.getId());//合同也移出
			
			//借据/合同移出融资业务登记表
			this.txDelCreditRegisterByBusiIds(idList);
			
			
		}else{
			
			/*
			 * 部分释放：将该主业务合同的【实际占用金额】调整为合同金额 - 传入的释放金额 - ∑借据金额（有效借据）（若结果为0，将数据直接移出该表）
			 */
			
			String hql = "select sum(cd.loanAmount) from PedCreditDetail cd  where cd.loanStatus !='JJ_05' and cd.loanType !='XD_05' and cd.crdtNo=?"; //去除未用退回跟表外业务垫款
			List<String> param = new ArrayList<String>();
			param.add(product.getCrdtNo());
			List temp = this.find(hql, param);
			BigDecimal allLoanAmt = new BigDecimal("0");
			if (temp != null && temp.size() > 0) {
				allLoanAmt = (BigDecimal) temp.get(0);
				if(null == allLoanAmt){
					allLoanAmt = new BigDecimal("0");
				}
			}
			BigDecimal occuAmt = product.getUseAmt().subtract(releaseAmt);//合同金额 - 传入的释放金额
			occuAmt = occuAmt.subtract(allLoanAmt);//再减去 ∑借据金额（有效借据）
			
			if(occuAmt.compareTo(new BigDecimal("0"))==0){

				/*
				 * 移出融资业务登记表
				 */

				List<String> busiIds = new ArrayList<String>();
				busiIds.add(product.getId());
				this.txDelCreditRegisterByBusiIds(busiIds);
			}else{
				
				List<String> idList = new ArrayList<String>();
				idList.add(product.getId());
				
				List<CreditRegister> crList = this.queryAllCreditContByBusiIds(idList);
				if(null != crList && crList.size()>0){					
					CreditRegister creditReg = crList.get(0);
					creditReg.setOccupyAmount(occuAmt);//实际占用金额
					/*
					 * 融资业务登记表变更
					 */
					this.txSaveCreditRegister(creditReg);
				}
			}
			
			
		}
		ret.setRET_CODE(Constants.TX_SUCCESS);
		return ret;
	}
	
	@Override
	public Ret txReleaseCreditByLoan(PedCreditDetail loan, String releaseType)throws Exception {
		
		logger.info("根据借据释放额度，处理借据号【"+loan.getLoanNo()+"】释放类型【"+releaseType+"】");
		
		if(PoolComm.EDSF_01.equals(releaseType)){
			/*
			 * 结清释放：将该借据在融资业务登记表中的对应数据移出
			 */
			
			List<String> ids = new ArrayList<String>();
			ids.add(loan.getCreditDetailId());
			/*
			 * 移出融资业务登记表
			 */
			this.txDelCreditRegisterByBusiIds(ids);
			
		}else{
			/*
			 * 部分释放：更新该笔借据对应的融资业务登记数据
			 */
			List<String> idList = new ArrayList<String>();
			idList.add(loan.getCreditDetailId());
			CreditRegister creditReg = this.queryAllCreditContByBusiIds(idList).get(0);
			creditReg.setOccupyAmount(loan.getActualAmount());//实际占用金额
			/*
			 * 融资业务登记表变更
			 */
			this.txSaveCreditRegister(creditReg);
			
		}
		
		return null;
	}
	@Override
	public void txChangeProductCcupy(CreditProduct cp, String newCcupy) throws Exception {
		
		logger.info("【"+cp.getCrdtNo()+"】信贷业务系数调整,原系数为【"+cp.getCcupy()+"】调整后系数为【"+newCcupy+"】");
		
		String bpsNo = cp.getBpsNo();//票据池编号
		PedProtocolDto pro = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
		
		//锁AssetPool表
		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pro);
		String apId = ap.getApId();
		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
		if(!isLockedSucss){//加锁失败
			logger.info("票据池【"+bpsNo+"】加锁失败！");
			throw new Exception("该票据池【"+bpsNo+"】尚有业务处理中，请稍后再试！");
		}
		
		//系数变更，重新登记用信业务信息
		cp.setCcupy(newCcupy);
		List<CreditRegister> crList = this.queryCreditContByNo(cp.getCrdtNo(), null, null);
		if(null != crList){
			List<CreditRegister> storeCrList = new ArrayList<CreditRegister>();	
			for(CreditRegister cr : crList ){
				
				cr.setOccupyRatio(new BigDecimal(newCcupy));
				
				if(PoolComm.VT_0.equals(cr.getVoucherType())){//主业务合同					
					cr = this.createCreditRegister(cp, pro, apId);
					this.txSaveCreditRegister(cr);
				}else{//借据
					PedCreditDetail loan = (PedCreditDetail)this.load(cr.getBusiId(), PedCreditDetail.class);
					cr = this.createCreditRegister(loan, pro, apId);
					this.txSaveCreditRegister(cr);
				}
				
				storeCrList.add(cr);
			}
			this.txStoreAll(storeCrList);
		}
		this.txStore(cp);
		
		//同步核心保证金，并重新计算额度
		financialService.txBailChangeAndCrdtCalculation(pro);
		
		//解锁AssetPool表，并重新计算该表数据
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
		
	}
	@Override
	public BigDecimal queryCreditBalance(String bpsNo, String riskLevel) {
		
		BigDecimal creditBalance = BigDecimal.ZERO;
		
		String hql = "select sum(cr.occupyCredit)  from CreditRegister cr where cr.bpsNo=? ";
		List param = new ArrayList();
		param.add(bpsNo);
		
		if(StringUtil.isNotBlank(riskLevel)){
			hql += " and cr.riskType =?";
			param.add(riskLevel);
		}
		List temp = this.find(hql, param);
		if (temp != null && temp.size() > 0) {
			Object obj = temp.get(0);
			if (obj != null && !"".equals(obj)) {
				creditBalance = new BigDecimal(obj.toString());// 占用余额
			}
		}
		return creditBalance;

	}
	@Override
	public BigDecimal queryCreditBalance(List crdtNos) {
		BigDecimal creditBalance = BigDecimal.ZERO;
		
		StringBuffer hql = new StringBuffer();
		List value = new ArrayList();
		List key = new ArrayList();
		
		hql.append(" select sum(cr.occupyCredit)  from CreditRegister cr where cr.contractNo in (:crdtNos) ");
		value.add("crdtNos");
		key.add(crdtNos);
		
		String paramNames[] = (String[]) value.toArray(new String[value.size()]);
		Object paramValues[] = key.toArray();
		List temp = this.find(hql.toString(), paramNames, paramValues);

		if (temp != null && temp.size() > 0) {
			Object obj = temp.get(0);
			if (obj != null && !"".equals(obj)) {
				creditBalance = new BigDecimal(obj.toString());// 占用余额
			}
		}
		return creditBalance;
	}
	@Override
	public ReturnMessageNew checkScale(String onlineNo, String onlineType,
			String billAmt, String dueDate) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        String apId = "";//AssetPool表主键
        
		BigDecimal billAmount = BigDecimal.ZERO;//开票金额
        Date dueDt = null;//银承业务到期日
        boolean isPoolCreditEnough = false;//池额度是否充足
        boolean isProCreditEnough = false;//担保额度是否充足
        boolean isOnlineCreditEnough = false;//在线合同是否充足

        String msg = "";//测算结果说明
        boolean isAllBail = false;//是否100%保证金的银承
        BigDecimal poolCreditRatio = new BigDecimal(0);//票据池额度占用比例
        
        BigDecimal  prptocolBalance = new BigDecimal(0);//在线银承合同可用余额 
        BigDecimal  contractBalance = new BigDecimal(0);//票据池担保合同可用余额
        BigDecimal  limitBalance = new BigDecimal(0);    //当前区间票据池低风险可用额度 
        BigDecimal  availableAmt = new BigDecimal(0);   //当前可开票额度=MIN[在线银承合同可用余额、Rounddown（票据池担保合同可用余额/票据池额度比例，2）、Rounddown（票据池低风险可用额度/票据池额度比例，2）]（若传来银承业务到期日不为空）
        
        //输入校验
        if(StringUtil.isBlank(onlineNo)||StringUtil.isBlank(onlineType)){
        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
	     	ret.setRET_MSG("在线协议编号与业务类型均为必送字段！");
	     	response.setRet(ret);
	        return response;
        }
        
        //输入校验
        if(StringUtil.isNotBlank(billAmt)&&StringUtil.isBlank(dueDate)){
        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
	     	ret.setRET_MSG("开票金额不为空时，业务到期日为必输！");
	     	response.setRet(ret);
	        return response;
        }
                
        
        //在线协议查询
        OnlineQueryBean bean = new OnlineQueryBean();
        List list = null;
        bean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);//生效
        if("1".equals(onlineType)){//在线流贷
        	bean.setOnlineCrdtNo(onlineNo);
        	list = pedOnlineCrdtService.queryOnlineProtocolList(bean);
        }else{//在线银承
        	bean.setOnlineAcptNo(onlineNo);
        	list = pedOnlineAcptService.queryOnlineAcptProtocolList(bean,null);
        	
        }
        //校验：在线银承签约校验
        if(null == list || list.size()==0){
        	ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("无生效的协议信息！");
			response.setRet(ret);
	        return response;
        }

    	PedProtocolDto pool = null;
    	if("1".equals(onlineType)){//在线流贷
    		//在线银承合同可用余额 
	        PedOnlineCrdtProtocol crdtPro = (PedOnlineCrdtProtocol) list.get(0);
        	prptocolBalance =crdtPro.getOnlineLoanTotal().subtract(crdtPro.getUsedAmt());
        	//票据池担保合同可用余额
        	pool  =  pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, crdtPro.getBpsNo(), null, null, null);
        	
        	poolCreditRatio = crdtPro.getPoolCreditRatio().divide(new BigDecimal("100"));
    	}else{
    		//在线银承合同可用余额 
	        OnlineQueryBean onlinePro = (OnlineQueryBean) list.get(0);
        	prptocolBalance =onlinePro.getOnlineAcptTotal().subtract(onlinePro.getUsedAmt());
        	poolCreditRatio = onlinePro.getPoolCreditRatio().divide(new BigDecimal("100"));
        	if(new BigDecimal("100").compareTo(onlinePro.getDepositRatio())==0){//100%业务保证金模式
        		isAllBail = true;
        	}
        	//票据池担保合同可用余额
        	if(!isAllBail){//100%保证金不查
        		pool  =  pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, onlinePro.getBpsNo(), null, null, null);
        		
        		//锁AssetPool表
    			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pool);
    			apId = ap.getApId();
    			boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
    			if(!isLockedSucss){//加锁失败
    				ret.setRET_CODE(Constants.EBK_11);
    				ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
    				response.setRet(ret);
    				logger.info("票据池【"+pool.getPoolAgreement()+"】上锁！");
    				return response;
    			}
        	}
    	}
    	
    	if(null != pool && !isAllBail){
    		contractBalance = pool.getCreditFreeAmount();
    		pool.getPoolMode();
    	}else{
    		ret.setRET_CODE(Constants.TX_FAIL_CODE);
	     	ret.setRET_MSG("无生效的融资票据池协议信息！");
	     	response.setRet(ret);
	        return response;
    	}
    	
    	response.getBody().put("ONLINE_BUSS_NO", onlineNo);//在线业务编号       
        response.getBody().put("ONLINE_ACPT_AVAL_BALANCE", prptocolBalance);//在线银承合同可用余额     
        response.getBody().put("GUARANTEE_CONTRACT_AVAL_BALANCE",contractBalance);//票据池担保合同可用余额
    		
        if(StringUtil.isBlank(dueDate)){ //业务到期日为空只返回在线银承合同可用余额、票据池担保合同可用余额
        	String msg1="";
        	if(billAmount.compareTo(contractBalance)<=0){
        		isProCreditEnough = true;//担保额度充足
        	}
        	
        	if(billAmount.compareTo(prptocolBalance)<=0){
        		isOnlineCreditEnough = true;//在线银承合同可用余额充足 
        	}
        	if(!isOnlineCreditEnough){
        		msg1+= "在线银承合同可用余额、";
			}
			if(!isProCreditEnough){
				msg1+= "票据池担保合同可用余额、";
				
			}
			if(isOnlineCreditEnough && isProCreditEnough){
    			msg1="当前可开票额度满足您的融资金额需求";//测算结果说明
        		response.getBody().put("CHECK_RESULT_REMARK", msg1);//测算结果说明
        		response.getBody().put("CHECK_RESULT", "0");//测算结果  0：通过 1：不通过
			}else{
				msg1+="不足，请联系客户经理。";
    			msg1 = msg1.replace("、不足", "不足");//去除最后一个顿号
        		response.getBody().put("CHECK_RESULT_REMARK", msg1);//测算结果说明
        		response.getBody().put("CHECK_RESULT", "1");//测算结果  0：通过 1：不通过
			}
    		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
			response.setRet(ret);
	        return response;
        }
        
        
        //若到期日不为空，开票金额默认为0
        if(StringUtil.isBlank(billAmt)){
        	billAmt = "0";
        }
        
        billAmount = new BigDecimal(billAmt);
        dueDt = DateTimeUtil.parse(dueDate);
        
        if(!isAllBail){//非100%业务保证金
        	
        	//保证金同步及额度计算及资产表重置
        	AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pool);
        	apId = ap.getApId();
        	financialService.txUpdateBailAndCalculationCredit(apId, pool);
        	
        	
        	//额度重算
        	List<CreditCalculation> ccList = new ArrayList<CreditCalculation>();
        	if(PoolComm.POOL_MODEL_01.equals(pool.getPoolMode())){//总量模式
        		ccList.add(financialService.txCreditCalculationTotal(pool));
        		
        	}else if(PoolComm.POOL_MODEL_02.equals(pool.getPoolMode())){//期限配比
        		ccList = financialService.txCreditCalculationTerm(pool);
        	}
        	
        	//解锁AssetPool表，并重新计算该表数据
    		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
    		
        	
        	 if(StringUtil.isBlank(dueDate)){

        		 //票据池保证金资产额度
     	        AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_BZJ_HQ);
     	        
     	        //低风险票资产额度
     	        AssetType atBail = pedAssetTypeService.queryPedAssetTypeByProtocol(pool, PoolComm.ED_PJC);
     	        
     	        limitBalance = atBail.getCrdtFree().add(atBillLow.getCrdtFree());   //票据池低风险可用额度 
     	        
        	 }else{	        		 
        		 //循环找到给定到期日所在的区间的低风险可用额度
        		 for(CreditCalculation cc : ccList){
        			 if(DateUtils.isBetweenTowDay(cc.getStartDate(), dueDt, cc.getEndDate())){
        				 limitBalance = cc.getLowRiskCredit(); //当前区间票据池低风险可用额度 
        				 break;
        			 }
        			 
        		 }
        	 }
        }else{
        	limitBalance = new BigDecimal("99900000000");//100%保证金设置池额度默认值为999亿（这里没有实际意义，为了下一步的比较规则与非100%银承一致而设置）
        }

        
        //可开票额度 = 票据池低风险可用额度 、票据池担保合同可用余额 、在线银承合同可用余额     中最小的一个  ，然后除以协议中的比例
        availableAmt = prptocolBalance.compareTo(contractBalance)<0?prptocolBalance:contractBalance;
        availableAmt = availableAmt.compareTo(limitBalance)<0?availableAmt:limitBalance;
        availableAmt = availableAmt.divide(poolCreditRatio,2,BigDecimal.ROUND_DOWN);
        
        if(isAllBail){	        	
        	response.getBody().put("LOW_RISK_LIMIT_BALANCE", "0");//票据池低风险可用额度--100%保证金给网银返回0        
        }else{
        	response.getBody().put("LOW_RISK_LIMIT_BALANCE", limitBalance);//票据池低风险可用额度       
        }
        response.getBody().put("AVAILABLE_LIMIT_AMT", availableAmt);//当前可开票额度
        
        
        if(StringUtil.isNotBlank(billAmt)&&StringUtil.isNotBlank(dueDate)){
        	
        	if(isAllBail){//100%业务保证金银承
        		isPoolCreditEnough=true;//池额度充足	
	        }else{
	        	String flowNo  = Long.toString(System.currentTimeMillis());
	        	CreditProduct product = new CreditProduct();
	        	product.setId(Long.toString(System.currentTimeMillis()));
	        	product.setCrdtNo(flowNo);
	        	product.setCustNo(pool.getCustnumber());
	        	product.setCustName(pool.getCustname());
	        	if("0".equals(onlineType)){ 				
	        		product.setCrdtType(PoolComm.XDCP_YC);//融资类型--银承
	        	}
	        	if("1".equals(onlineType)){ 				
	        		product.setCrdtType(PoolComm.XDCP_YC);//融资类型--流贷
	        	}
	        	product.setCrdtIssDt(new Date());//生效日
	        	product.setCrdtDueDt(dueDt);//到期日
	        	product.setUseAmt(billAmount);//合同金额
	        	product.setRestUseAmt(billAmount);//需要占用的额度
	        	product.setCrdtStatus(PoolComm.RZCP_YQS);//业务状态   RZ_03：额度占用成功   JQ_00 已结清   存储MIS系统发过来的状态：JQ_01 取消放贷  JQ_02 手工提前终止出账   JQ_03 合同到期    JQ_04 合同终止
	        	product.setSttlFlag(PoolComm.JQZT_WJQ);//结清标记   JQ_00:已结清   JQ_01：未结清
	        	product.setCrdtBankCode(pool.getOfficeNet());//网点
	        	product.setRisklevel(PoolComm.LOW_RISK);//风险等级--低风险
	        	product.setCcupy("1");//占用比例
	        	product.setBpsNo(pool.getPoolAgreement());
	        	product.setIsOnline(PoolComm.YES);//线上
	        	product.setMinDueDate(dueDt);//借据最早到期日
	        	
	        	/*
	        	 * 票据池额度校验
	        	 */
	        	CreditRegisterCache crdtReg = this.createCreditRegisterCache(product, pool,apId);
	        	List<CreditRegisterCache> crdtRegList = new ArrayList<CreditRegisterCache>();
	        	crdtReg.setFlowNo(flowNo);
	        	crdtRegList.add(crdtReg);
	        	Ret crdtCheckRet =  financialService.txCreditUsedCheck(crdtRegList, pool,flowNo);
	        	
	        	if(crdtCheckRet.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
	        		
	        		isPoolCreditEnough=true;//池额度充足	
	        	}
	        	
	        }
        	
	        
        	if(billAmount.compareTo(contractBalance)<=0){
        		isProCreditEnough = true;//担保额度充足
        	}
        	
        	if(billAmount.compareTo(prptocolBalance)<=0){
        		isOnlineCreditEnough = true;//在线银承合同可用余额充足 
        	}
        	
        	logger.info(" 担保金额:"+contractBalance+" 在线银承合同可用余额 "+ prptocolBalance);
        	logger.info("池额度充足:"+isPoolCreditEnough+" 担保额度充足:"+isProCreditEnough+" 在线银承合同可用余额充足 "+ isOnlineCreditEnough);
        	
    		if(billAmount.compareTo(availableAmt)<=0){//若【开票金额】小于等于【当前可开票额度（元）】
    			
    			response.getBody().put("CHECK_RESULT", "0");//测算结果  0：通过 1：不通过
    			msg="当前可开票额度满足您的融资金额需求";//测算结果说明
    		
    		}else{//不足
    			
    			
    			//若【在线银承合同可用余额】、Rounddown（票据池担保合同可用余额/票据池额度比例，2）、Rounddown（票据池低风险可用额度/票据池额度比例，2）中至少两个小于【开票金额】，反馈文本：
    			// “在线银承合同可用余额、票据池担保合同可用余额、票据池低风险可用额度（三个字段按顺序，哪个值小于就显示对应的文字）不足，请联系客户经理。”
    			
    			response.getBody().put("CHECK_RESULT", "1");//测算结果  0：通过 1：不通过
    			
    			if(!isOnlineCreditEnough){
    				msg+= "在线银承合同可用余额、";
    			}
    			if(!isProCreditEnough){
    				msg+= "票据池担保合同可用余额、";
    				
    			}
    			if(!isPoolCreditEnough){
    				msg+= "票据池低风险可用额度、";
    			}
    			msg+="不足，请联系客户经理。";
    			msg = msg.replace("、不足", "不足");//去除最后一个顿号
    			
    			
    			if(!isOnlineCreditEnough && isPoolCreditEnough && isProCreditEnough){//若仅【在线银承合同可用余额】小于【开票金额】
    				msg= "在线银承合同可用余额不足，请联系客户经理。";//测算结果说明
    			}
    			
    			if(isOnlineCreditEnough && isPoolCreditEnough && !isProCreditEnough){//若仅Rounddown（票据池担保合同余额/票据池额度比例，2）小于【开票金额】
    				msg= "票据池担保合同可用余额不足，请联系客户经理。";//测算结果说明
    			}
    			
    			if(isOnlineCreditEnough && !isPoolCreditEnough && isProCreditEnough){//若仅当前可开票额度小于Rounddown（票据池低风险可用/票据池额度比例，2）
    				msg= "当前票据池低风险额度不足，建议补充票据池资产或者调低融资金额。";//测算结果说明
    			}
    			        			
    		}
    		response.getBody().put("CHECK_RESULT_REMARK", msg);//测算结果说明
        	
        }
        
        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
		response.setRet(ret);
		return response;

	}
	
	
	
	 @Override
    public List<CreditRegister> queryCreditRegisterBycontractNos(List<String> contractNos,String voucherType) {
    	
		StringBuffer hql = new StringBuffer("select cr from CreditRegister cr where 1=1 ");
		List parasName = new ArrayList();
		List parasValue = new ArrayList();

		hql.append(" and cr.contractNo in(:contractNos)");
		parasName.add("contractNos");
		parasValue.add(contractNos);
		
		hql.append(" and cr.voucherType =:voucherType ");
		parasName.add("voucherType");
		parasValue.add(voucherType);

		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find(hql.toString(), nameForSetVar, parameters);
		
        if(null!=list&&list.size()>0){
        	return list;
        }
        return null;
    }
	
}
