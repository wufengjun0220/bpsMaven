package com.mingtech.application.pool.financial.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.domain.AssetRegisterCache;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.creditmanage.domain.CreditRegister;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterCache;
import com.mingtech.application.pool.creditmanage.domain.CreditRegisterHis;
import com.mingtech.application.pool.creditmanage.service.CreditRegisterService;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.domain.CreditCalcuCache;
import com.mingtech.application.pool.financial.domain.CreditCalculation;
import com.mingtech.application.pool.financial.domain.CreditCalcutionBean;
import com.mingtech.application.pool.financial.service.FinancialAdviceService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.DraftRange;
import com.mingtech.application.utils.DraftRangeHandler;
import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 资产及额度处理实现类
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-5
 * @copyright 北明明润（北京）科技有限责任公司
 */
@Service("financialService")
public class FinancialServiceImpl extends GenericServiceImpl implements FinancialService {
	private static final Logger logger = Logger.getLogger(FinancialServiceImpl.class);

	@Autowired
	private FinancialAdviceService financialAdviceService;
	@Autowired
	private PoolBailEduService poolBailEduService;
	@Autowired
	private AssetRegisterService assetRegisterService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService ;
	@Autowired
	private CreditRegisterService creditRegisterService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	
	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public Class getEntityClass() {
		return null;
	}
	
	@Override
	public List<CreditCalculation>  txCreditCalculationTerm(PedProtocolDto proDto ) throws Exception {
		String bpsNo = proDto.getPoolAgreement();
		
		logger.info("期限配比模式，票据池【"+bpsNo+"】各区间段额度计算开始....");

		/*
		 * 先删除对应票据池协议下的期限匹配
		 */
		this.txDelCalculationByBpsNo(bpsNo);


		//时间段划分
        List<CreditCalculation> creditList = this.creditCalTaskDueDt(proDto);

		//高低风险现金流入数据组装
		creditList = this.creditCalTaskAmtIn(creditList, bpsNo);

		//高低风险现金流出数据组装
		creditList = this.creditCalTaskAmtOut(creditList, bpsNo);

		//最终额度信息组装
		creditList = this.creditCalTaskAll(creditList, bpsNo);

		System.out.println("期限匹配模式，各区间打印：");
		for(CreditCalculation dto :creditList){
			System.out.println(DateUtils.toString(dto.getStartDate(),DateUtils.ORA_DATES_FORMAT)+"      "+DateUtils.toString(dto.getEndDate(),DateUtils.ORA_DATES_FORMAT)+"      "+dto.getLowRiskIn()+"      "+dto.getLowRiskOut()+"      "+dto.getLowRiskCashFlow()+
					"      "+ dto.getHighRiskIn()+"      "+dto.getHighRiskOut()+"      "+dto.getHighRiskCashFlow()+"      "+
					"      "+dto.getLowRiskCredit()+"      "+dto.getHighRiskCredit()+"      "+dto.getAllCredit());

		}
		logger.info("期限配比模式，票据池【"+bpsNo+"】各区间段额度计算结束....");
		this.txStoreAll(creditList);
		return creditList;
	}
	/**
	 * 先删除对应票据池协议下的期限匹配
	 */
	public void txDelCalculationByBpsNo(String bpsNo){
		
		logger.info("票据池【"+bpsNo+"】额度计算表数据删除...");
		
		List values= new ArrayList();
		String hql = "from  CreditCalculation c where c.bpsNo = ?";
		values.add(bpsNo);
		List<CreditCalculation>  list  = this.find(hql,values);
		if(null!= list && list.size()>0){
			this.txDeleteAll(list);
		}
	}
	
	/**
	 * 时间段划分
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-7上午11:09:01
	 */
	public List<CreditCalculation> creditCalTaskDueDt(PedProtocolDto proDto){
		String bpsNo = proDto.getPoolAgreement();
		
		String today = DateUtils.toDateString(new Date());
		String todaySqlStr = "TO_DATE('"+today+"', 'yyyy-mm-dd')";
		
		logger.info("票据池【"+bpsNo+"】额度计算时间段划分开始....");
		/*
		 * 时间区间分段
		 * 		1）查询产生额度票据（含高低风险）到期日，按照到期日分组
		 *      2）查询用信合同项下未结清的借据到期日（无借据的按照合同的最早到期日）,按照到期日分组
		 *      3) 查询有效的主业务合同项下无借据的合同的最早到期日,按照到期日分组
		 *      3）将如上三个取并集
		 *      注意：冻结情况另行考虑
		 */
		List<Date> dueDateList = new ArrayList<Date>();
		String sql = "select a.duedt from("
				+" select due_date as duedt from ped_credit_register   where  bps_no = '"+bpsNo+"' and due_date >= "+todaySqlStr+"  group by due_date "
				+" union"
				+" select  a.asset_delay_duedt as due_date  from ped_asset_register a  "
				+"where a.bps_no = '"+bpsNo+"' and a.asset_delay_duedt >= "+todaySqlStr+"  group by a.asset_delay_duedt  "
				+" ) a  order by a.duedt";

		dueDateList=dao.SQLQuery(sql);

		//时间段划分，起始日、截止日组装，默认最后一条的截至时间为2099-12-31
		List<CreditCalculation> creditList = new ArrayList<CreditCalculation>();
		int size = dueDateList.size();
		int lastSize = size - 1;
        for(int i =0;i<lastSize;i++){
            CreditCalculation crdt = new CreditCalculation();
			crdt.setBpsNo(bpsNo);//票据池协议编号
            crdt.setCustPoolName(proDto.getPoolName());//客户资产池名称
            crdt.setCertType(PoolComm.CRT_01);//默认组织机构代码
            crdt.setCertCode(proDto.getCustOrgcode());//组织机构代码
            crdt.setCreateDate(DateUtils.getCurrDateTime());//创建时间
			crdt.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
			crdt.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
            Date dueDt = dueDateList.get(i);
            crdt.setStartDate(dueDt);
            Date dueDt2 = dueDateList.get(i+1);
            crdt.setEndDate(DateTimeUtil.getUtilDate(dueDt2, -1));//前一天
            creditList.add(crdt);
        }
        //最后一条数据
        if(size > 0){
            CreditCalculation crdt = new CreditCalculation();
			crdt.setBpsNo(bpsNo);//票据池协议编号
			crdt.setCustPoolName(proDto.getPoolName());//客户资产池名称
			crdt.setCertType(PoolComm.CRT_01);//默认组织机构代码
			crdt.setCertCode(proDto.getCustOrgcode());//组织机构代码
			crdt.setCreateDate(DateUtils.getCurrDateTime());//创建时间
			crdt.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
			crdt.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
            Date dueDt = dueDateList.get(size-1);
            crdt.setStartDate(dueDt);
            crdt.setEndDate(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));
            creditList.add(crdt);
        }

		return creditList;
	}
	/**
	 * 高低风险现金流入
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-8下午8:00:56
	 */
	public List<CreditCalculation> creditCalTaskAmtIn(List<CreditCalculation> creditList,String bpsNo) throws Exception{

		logger.info("票据池【"+bpsNo+"】额度计算高低风险现金流入数据组装开始....");

		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtIn = BigDecimal.ZERO;//低风险额度总额
		BigDecimal highTotalAmtIn = BigDecimal.ZERO;//高风险额度总额

		
		String sql = "select a  from AssetRegister a   where  a.bpsNo = '"+bpsNo+"'  and a.delFlag !='D' order by a.assetDelayDueDt asc";
		List<AssetRegister> billList = new ArrayList<AssetRegister>();
		billList = this.find(sql);
        for(AssetRegister billInf : billList){
            CreditCalculation creditCalculation=creditList.get(count);
        	int size = creditList.size();
            for(int i = count ;i < size;i++  ){
				creditCalculation=creditList.get(count);
				
				if(count == 0 && DateUtils.checkOverLimited(creditCalculation.getStartDate(), billInf.getAssetDelayDueDt())){
					// 到期日比第一个额度区间的开始日小，则计算在第一个时间区间内，这种情况发生在资产到期后没有承兑
                    break;
				}
				
				if(DateUtils.isBetweenTowDay(creditCalculation.getStartDate(), billInf.getAssetDelayDueDt(), creditCalculation.getEndDate())){
                    // 到期日在[开始时间，截至时间]之内
                    break;
                }else{
					if(PoolComm.LOW_RISK.equals(billInf.getRiskType())){//低风险
						creditCalculation.setLowRiskIn(lowTotalAmtIn);//低风险现金流入

					}else if(PoolComm.HIGH_RISK.equals(billInf.getRiskType())){//高风险
						creditCalculation.setHighRiskIn(highTotalAmtIn);//高风险现金流入
					}
                    count ++ ;
                }
            }
            if(PoolComm.LOW_RISK.equals(billInf.getRiskType())){//低风险
                lowTotalAmtIn =  lowTotalAmtIn.add(billInf.getAssetAmount());
                creditCalculation.setLowRiskIn(lowTotalAmtIn);//低风险现金流入

            }else if(PoolComm.HIGH_RISK.equals(billInf.getRiskType())){//高风险
                highTotalAmtIn =  highTotalAmtIn.add(billInf.getAssetAmount());
                creditCalculation.setHighRiskIn(highTotalAmtIn);//高风险现金流入
            }
        }

		return creditList;
	}
	/**
	 * 高低风险现金流出
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2021-5-8下午10:12:12
	 */
	public List<CreditCalculation> creditCalTaskAmtOut(List<CreditCalculation> creditList,String bpsNo) throws Exception{

		logger.info("票据池【"+bpsNo+"】额度计算高低风险现金流出数据组装开始....");

		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtOut = new BigDecimal(0);//低风险用信额度总额
		BigDecimal highTotalAmtOut = new BigDecimal(0);//高风险用信额度总额

		String sql = "select r.due_date,r.occupy_credit,r.risk_type from ped_credit_register r where r.bps_no = '"+bpsNo+"'" +
				"  order by r.due_date asc ";

		List crdtList = new ArrayList();
		crdtList = dao.SQLQuery(sql);

		int size = crdtList.size();
		if(size>0){
			Date dueDt = null;
			BigDecimal crdtAmt = BigDecimal.ZERO;
			String riskLevel = null;

			for(int k=0;k<size;k++){
                int creditSize = creditList.size();
                CreditCalculation creditCalculation=creditList.get(count);
				Object[] obj = (Object[])crdtList.get(k);
				dueDt = (Date) obj[0];
				crdtAmt = new BigDecimal(String.valueOf(obj[1]));
				riskLevel = String.valueOf(obj[2]);

				for(int i = count ;i < creditSize;i++  ){
					creditCalculation=creditList.get(count);
					
					if(count == 0 && DateUtils.checkOverLimited(creditCalculation.getStartDate(), dueDt)){
						// 用信业务到期日比第一个额度区间的开始日小，则计算在第一个时间区间内，这种情况发生在用信业务到期后没有结清的情况
	                    break;
					}
					
					if(DateUtils.isBetweenTowDay(creditCalculation.getStartDate(), dueDt, creditCalculation.getEndDate())){
						// 到期日在[开始时间，截至时间]之内;
						break;
					}else{
						if(PoolComm.LOW_RISK.equals(riskLevel)){//低风险
							creditCalculation.setLowRiskOut(lowTotalAmtOut);//低风险现金流出

						}else if(PoolComm.HIGH_RISK.equals(riskLevel)){//高风险
							creditCalculation.setHighRiskOut(highTotalAmtOut);//高风险现金流出
						}
						count++ ;
					}

				}
				if(PoolComm.LOW_RISK.equals(riskLevel)){//低风险
					lowTotalAmtOut =  lowTotalAmtOut.add(crdtAmt);
                    creditCalculation.setLowRiskOut(lowTotalAmtOut);//低风险现金流出

				}else if(PoolComm.HIGH_RISK.equals(riskLevel)){//高风险
					highTotalAmtOut =  highTotalAmtOut.add(crdtAmt);
                    creditCalculation.setHighRiskOut(highTotalAmtOut);//高风险现金流出
				}
			}
		}
		return creditList;
	}
	/**
	 * 额度信息组装
	 * @param creditList
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-8下午10:26:06
	 */
	public List<CreditCalculation> creditCalTaskAll(List<CreditCalculation> creditList,String bpsNo){

		logger.info("票据池【"+bpsNo+"】额度计算最终额度信息数据组装开始....");
		int size = creditList.size();
		for(int i = 0 ;i<size;i++){
			CreditCalculation crdt = creditList.get(i);

			//(1)
//			logger.info("（1）高低风险现金流入流出为0的数据补齐");
			if(i>0){
				//当在某个区间段内没有低/高风险的票的情况下，当前低/高风险的现金流入与上一时间区间一致
				//当在某个区间段内没有低/高风险的融资业务的情况下，当前低/高风险的现金流出与上一时间区间一致

				//低风险流入，0流入补齐
				if(crdt.getLowRiskIn().compareTo(BigDecimal.ZERO)<=0){
					crdt.setLowRiskIn(creditList.get(i-1).getLowRiskIn());
				}
				//高风险流入，0流入补齐
				if(crdt.getHighRiskIn().compareTo(BigDecimal.ZERO)<=0){
					crdt.setHighRiskIn(creditList.get(i-1).getHighRiskIn());
				}
				//低风险流出，0流出补齐
				if(crdt.getLowRiskOut().compareTo(BigDecimal.ZERO)<=0){
					crdt.setLowRiskOut(creditList.get(i-1).getLowRiskOut());
				}
				//高风险流出，0流出补齐
				if(crdt.getHighRiskOut().compareTo(BigDecimal.ZERO)<=0){
					crdt.setHighRiskOut(creditList.get(i-1).getHighRiskOut());
				}
			}
			//(2)高低风险现金流时点组装
//			logger.info("(2)高低风险现金流时点组装");
			BigDecimal lowCrdt = new BigDecimal(0);
			if(crdt.getHighRiskIn().compareTo(crdt.getHighRiskOut())>=0){//高风险额度足够高风险业务占用
				crdt.setHighRiskCashFlow(crdt.getHighRiskIn().subtract(crdt.getHighRiskOut()));//高风险现金时点=高风险现金流入-高风险现金流出
			}else{
//                crdt.setHighRiskCashFlow(crdt.getHighRiskIn());//占完全部高风险额度
				crdt.setHighRiskCashFlow(new BigDecimal(0));//占完全部高风险额度
				lowCrdt=crdt.getHighRiskOut().subtract(crdt.getHighRiskIn());
				//缺少部分留待占用低风险额度
			}
			//(3)低风险现金流时点组装
//			logger.info("(3)低风险现金流时点组装");
			crdt.setLowRiskCashFlow(crdt.getLowRiskIn().subtract(crdt.getLowRiskOut()).subtract(lowCrdt));//低风险现金时点=低风险现金流入-低风险现金流出-高风险业务需要占用低风险的额度
		}
		//(4)双重for循环组装可用额度
//		logger.info("(4)双重for循环组装可用额度");
		CreditCalculation crdt1 = null;
		CreditCalculation crdt2 = null;
		for(int j = 0 ;j<size;j++){
			crdt1 = creditList.get(j);
			crdt1.setHighRiskCredit(crdt1.getHighRiskCashFlow());//默认高风险可用额度为当前时点的现金流
			crdt1.setLowRiskCredit(crdt1.getLowRiskCashFlow());//默认低风险可用额度为当前时点的现金流
			for(int k = j+1 ;k<size;k++){
				crdt2 = creditList.get(k);
				//高风险可用额度
				if(crdt1.getHighRiskCashFlow().compareTo(crdt2.getHighRiskCashFlow())>0
						&&crdt1.getHighRiskCredit().compareTo(crdt2.getHighRiskCashFlow())>0 ){//只要后续日期中存在时点上比当前可用额度少，则该时点可用额度为后续最小点的值
					crdt1.setHighRiskCredit(crdt2.getHighRiskCashFlow());
				}
				//低风险可用额度
				if(crdt1.getLowRiskCashFlow().compareTo(crdt2.getLowRiskCashFlow())>0
						&& crdt1.getLowRiskCredit().compareTo(crdt2.getLowRiskCashFlow())>0){//只要后续日期中存在时点上比当前可用额度少，则该时点可用额度为后续最小点的值
					crdt1.setLowRiskCredit(crdt2.getLowRiskCashFlow());
				}
			}
		}
		return creditList;
	}

	/**
	 * 额度计算生成表-总量模式
	 * @param proDto
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-7上午10:24:42
	 */
	@Override
	public CreditCalculation txCreditCalculationTotal(PedProtocolDto proDto) throws Exception {
		
		logger.info("总量模式，票据池【"+proDto.getPoolAgreement()+"】额度计算开始....");
		String bpsNo = proDto.getPoolAgreement();
		
		//删除额度计算表数据
		this.txDelCalculationByBpsNo(bpsNo);

		CreditCalculation crdtCal = new CreditCalculation();
        crdtCal.setCustPoolName(proDto.getPoolName());//客户资产池名称
        crdtCal.setCertType(PoolComm.CRT_01);//默认组织机构代码
        crdtCal.setCertCode(proDto.getCustOrgcode());//组织机构代码
        crdtCal.setBpsNo(bpsNo);            //客户票据池编号
		crdtCal.setStartDate(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT));      //开始日期                    --今天
		crdtCal.setEndDate(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));            //结束日期             --2099-12-31
		crdtCal.setLowRiskIn(this.querySumAssetAmt(bpsNo,PoolComm.LOW_RISK));        //低风险流入资金      --SUM AssetRegister表 中【该客户】【低风险】的【票据&保证金】总资产金额
		crdtCal.setLowRiskOut(this.querySumCreditAmt(bpsNo,PoolComm.LOW_RISK));       //低风险流出资金      --SUM CreditRegister表 中【该客户】【低风险】的总融资金额
		crdtCal.setHighRiskIn(this.querySumAssetAmt(bpsNo,PoolComm.HIGH_RISK));       //高风险流入资金     --SUM AssetRegister表 中【该客户】【高风险】的【票据】总资产金额
		crdtCal.setHighRiskOut(this.querySumCreditAmt(bpsNo,PoolComm.HIGH_RISK));      //高风险流出资金     --SUM CreditRegister表 中【该客户】【高风险】的总融资金额

		crdtCal.setTransDate(DateUtils.getCurrDate());        //交易日期(不含时分秒)
		crdtCal.setCreateDate(DateUtils.getCurrDateTime()); //创建日期(含时分秒)
		crdtCal.setUpdateDate(new Date()); //更新日期(含时分秒)
		crdtCal.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);          //逻辑删除标记-N未删除、D已删除

		List<CreditCalculation> list  = new ArrayList<CreditCalculation>();
		list.add(crdtCal);
		list=this.creditCalTaskAll(list,bpsNo);
		crdtCal = list.get(0);

		this.txStore(crdtCal);//落库
		return crdtCal;
	}
	/**
	 * 额度占用方法
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-6-7上午10:28:21
	 */
	@Override
	public Ret txCreditUsed(List<CreditRegister> crdtRegList,PedProtocolDto proDto) throws Exception {
		
		logger.info("票据池【"+proDto.getPoolAgreement()+"】额度占用...");
		
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("额度不足！");
		String apId = "";
		/*
		 * 【1】CreditRegister落库
		 */
		List<CreditRegister> list  =new ArrayList<CreditRegister>();
		List<CreditRegisterHis> hislist  =new ArrayList<CreditRegisterHis>();
		for(CreditRegister crdtReg: crdtRegList){
			apId = crdtReg.getApId();
			BigDecimal realCredit = crdtReg.getOccupyAmount().multiply(crdtReg.getOccupyRatio()).setScale(2,BigDecimal.ROUND_UP);
			crdtReg.setOccupyCredit(realCredit); //实际占用额度  四舍五入，保留2位小数
			crdtReg.setCreateDate(DateUtils.getWorkDayDate());//创建时间
			crdtReg.setUpdateDate(new Date());//更新时间
			crdtReg.setTransDate(DateUtils.getCurrDate());//交易时间
			list.add(crdtReg);
			CreditRegisterHis his = new CreditRegisterHis();
			BeanUtil.beanCopy(crdtReg,his);
			hislist.add(his);
		}
		financialAdviceService.txCreateList(list);
		
		try {			
			/*
			 * 【2】重新计算额度计算CreditCalculation表
			 */
			if(PoolComm.POOL_MODEL_01.equals(proDto.getPoolMode())){//总量模式
				this.txCreditCalculationTotal(proDto);
				
			}else if(PoolComm.POOL_MODEL_02.equals(proDto.getPoolMode())){//期限配比		
				this.txCreditCalculationTerm(proDto);
			}
			
			/*
			 * 【3】只要额度计算CreditCalculation表中存在[高or低风险可用额度]小于0，则额度不足
			 */
			List<Object> listCalcultion = new ArrayList<Object>();
			listCalcultion = this.queryCreditCalculationMinByBpsNo(proDto.getPoolAgreement());
			
			//获取已用额度
			List<Object> maxList = new ArrayList<Object>();
			maxList = this.queryCreditCalculationMaxByBpsNo(proDto.getPoolAgreement());
			
			if(null!=listCalcultion && listCalcultion.size()>0) {
				Object[] obj = (Object[]) listCalcultion.get(0);
				BigDecimal lowRiskCredit =(BigDecimal)obj[0];
				BigDecimal highRiskCredit =(BigDecimal)obj[1];
				
				//获取全部流出金额
				BigDecimal lowRiskOut = BigDecimal.ZERO;
				BigDecimal highRiskOut = BigDecimal.ZERO;
				BigDecimal allOut =  BigDecimal.ZERO;
				
				if(null!=maxList && maxList.size()>0){					
					Object[] obj2 = (Object[]) maxList.get(0);
					lowRiskOut =(BigDecimal)obj2[0];
					highRiskOut =(BigDecimal)obj2[1];
					allOut = lowRiskOut.add(highRiskOut);//全部已用
				}
	            
	            
				if (lowRiskCredit.compareTo(BigDecimal.ZERO) >= 0
						&& highRiskCredit.compareTo(BigDecimal.ZERO) >= 0) {
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("票据池额度占用成功！");
				}
				
				//=====================冻结校验开始=============（汉口银行需要，其他行一定不需要该部分代码）=============================//
				
				//票据池保证金资产额度
				AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(proDto, PoolComm.ED_BZJ_HQ);
				BigDecimal bailAmt = atBillLow.getCrdtFree();
				
				
				if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_02)){//（1）票据冻结
					if(allOut.compareTo(bailAmt)>0){//只要已用大于保证金金额就是额度不足
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("额度不足！");
						logger.info("该客户的票据额度有冻结，额度不足，票据池【"+proDto.getPoolAgreement()+"】");
					}
				}
				
				if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_01)){//（2）保证金冻结
					if ((lowRiskCredit.subtract(bailAmt)).compareTo(BigDecimal.ZERO) < 0){//最小的低风险可用减去保证金金额之后小于0即为额度不足
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("额度不足！");
						logger.info("该客户的保证金有冻结，额度不足，票据池【"+proDto.getPoolAgreement()+"】");
					}
				}
				
				//（3）全冻结无需在此校验
				
				//=====================冻结校验结束==========================================//
				
			}
			//额度占用失败回滚
			if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
				
				this.txFailRollback(list, proDto, apId);
			}
			
		} catch (Exception e) {//额度占用处理失败回滚
			
			logger.error("额度占用失败回滚，票据池编号【"+proDto.getPoolAgreement()+"】",e);
			this.txFailRollback(list, proDto, apId);
			
		}		
		
		financialAdviceService.txCreateList(hislist);//保存历史
		
		return ret;
	}
	/**
	 * 额度占用失败回滚
	 * @param list
	 * @param proDto
	 * @param apId
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-7-18上午2:12:12
	 */
	private void txFailRollback(List<CreditRegister> list,PedProtocolDto proDto,String apId)throws Exception{
		
		logger.error("额度占用失败回滚，票据池编号【"+proDto.getPoolAgreement()+"】");
		
		//1、删除保存的融资用信对象
		financialAdviceService.txDelList(list);
		
		/*
		 * 重新计算池额度信息
		 */
		if(PoolComm.POOL_MODEL_01.equals(proDto.getPoolMode())){//总量模式
			this.txCreditCalculationTotal(proDto);
		}else{//期限配比					
			this.txCreditCalculationTerm(proDto);
		}
		
		/*
		 * 不解锁AssetPool表，重新计算该表数据
		 */
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,false);
		
	}
    /**
     * 资产出池
     * @param assetOutList
     * @param proDto
     * @return
     * @throws Exception
     * @author Ju Nana
     * @date 2021-6-7下午2:30:24
     */
	@Override
	public Ret txAssetOut(List<AssetRegister> assetOutList,PedProtocolDto proDto,Map<String,PoolBillInfo> mapList) throws Exception {
		
		logger.info("资产出池方法txAssetOut处理开始...");
		/**
		 * 可拆分的数据登记表数据处理逻辑
		 * 1、将传送进来的要处理的数据做备份，发送异常时回滚使用
		 * 2、如果出池的数据有拆分的数据，生成拆分后的登记数据
		 * 3、移除传进来的要处理的登记数据，并落库
		 * 4、保存第二部生成的拆分后的登记数据
		 * 5、保证金同步，并重新计算额度计算CreditCalculation表
		 * 6、只要额度计算CreditCalculation表中存在[高or低风险可用额度]小于0，则额度不足不允许出池
		 * 7、资产出池后额度不足，数据回滚：1）保存备份数据，删除生成的拆分后数据
		 * 8、回滚--重新计算额度计算CreditCalculation表
		 * 2022-03-22  wfj
		 */
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("额度不足！");
		
		if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_02)){//票据冻结，不允许出池
			logger.info("票据池【"+proDto.getPoolAgreement()+"】票据额度冻结，不允许出池！");
			return ret;
		}

		/**
		 * 1、回滚数据准备
		 */
		List<AssetRegister> assetBackList = new ArrayList<AssetRegister>();//回滚使用的资产列表
		List<AssetRegister> assetSplitBackList = new ArrayList<AssetRegister>();//拆分后的的资产列表

		if(null!=assetOutList && assetOutList.size()>0){//注意：需要循环并copy赋值是因为assetBackList被先删除，后保存的时候，因为提前存储的assetBackList是含有AssetRegister地址引用的，所以保存会报ID改变的问题。
			for(AssetRegister ar : assetOutList){
				AssetRegister arNew = new AssetRegister();
				BeanUtil.beanCopy(ar, arNew);
				assetBackList.add(arNew);
				
				/**
				 * 2、如果出池的数据有拆分的数据，生成拆分后的登记数据
				 */
				PoolBillInfo bill = mapList.get(ar.getAssetNo());
				 /**
				  *  判断是否进行拆包
				  */
	            if(bill.getTradeAmt().compareTo(ar.getAssetAmount()) < 0){
					/**
					 * 生成拆票的子票区间
					 */
					DraftRange range = DraftRangeHandler.buildLimitNewBeginAndEndDraftRange(bill.getFBillAmount(), bill.getTradeAmt(), bill.getStandardAmt(), bill.getBeginRangeNo(), bill.getEndRangeNo());
					
					//拆分的数据
					AssetRegister arSPlitNew = new AssetRegister();
					
					BeanUtil.beanCopy(ar, arSPlitNew);
					arSPlitNew.setAssetNo(bill.getSBillNo() + "-" + range.getBeginDraftRange() + "-" + range.getEndDraftRange());
					arSPlitNew.setAssetAmount(ar.getAssetAmount().subtract(bill.getTradeAmt()));
					assetSplitBackList.add(arSPlitNew);
				}
				
			}
		}
		
		if(null==assetOutList){
			logger.info("出池资产均不在额度登记表中，可出池");			
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("不在额度登记信息中，可出池");	
			return ret;
		}
		
		/*
		 * 【3】将assetOutList从AssetRegister表中移出，并落库
		 */
		logger.info("【1】将assetOutList从AssetRegister表中移出，并落库...");
		financialAdviceService.txDelList(assetOutList);
		
		/**
		 * 4、生成拆分后的登记数据落库
		 */
		if(null!=assetSplitBackList && assetSplitBackList.size()>0){
			financialAdviceService.txForcedSaveList(assetSplitBackList);
		}
		
		
		/*
		 * 【5】保证金同步，并重新计算额度计算CreditCalculation表
		 */
		logger.info("【2】保证金同步，并重新计算额度计算CreditCalculation表");
		this.txBailChangeAndCrdtCalculation(proDto);
		
		/*
		 * 【6】只要额度计算CreditCalculation表中存在[高or低风险可用额度]小于0，则额度不足不允许出池
		 */
		logger.info("【3】只要额度计算CreditCalculation表中存在[高or低风险可用额度]小于0，则额度不足不允许出池");
		List<Object> listCalcultion = new ArrayList<Object>();

		listCalcultion = this.queryCreditCalculationMinByBpsNo(proDto.getPoolAgreement());
		if(null!=listCalcultion && listCalcultion.size()>0) {
            Object[] obj = (Object[]) listCalcultion.get(0);
            BigDecimal lowRiskCredit =(BigDecimal)obj[0];
            BigDecimal highRiskCredit =(BigDecimal)obj[1];
            
			if (lowRiskCredit.compareTo(BigDecimal.ZERO) >= 0 && highRiskCredit.compareTo(BigDecimal.ZERO) >= 0) {
				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("额度充足，可以出池！");
			}
			
			
			//===================================保证金冻结校验======================================//
			
			if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_01)){//（2）保证金冻结
				
				//票据池保证金资产额度
				AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(proDto, PoolComm.ED_BZJ_HQ);
				BigDecimal bailAmt = atBillLow.getCrdtFree();
				
				if ((lowRiskCredit.subtract(bailAmt)).compareTo(BigDecimal.ZERO) < 0){//最小的低风险可用减去保证金金额之后小于0即为额度不足
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("额度不足！");
					logger.info("该客户的保证金有冻结，额度不足，票据池【"+proDto.getPoolAgreement()+"】");
				}
				
			}
		}
		
		if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
			
			
			logger.info("资产出池后额度不足，数据回滚开始...");
			
			
			//7、回滚--资产
			logger.info("资产回滚...");
			financialAdviceService.txForcedSaveList(assetBackList);
			financialAdviceService.txDelList(assetSplitBackList);
			
			
			//8、回滚--重新计算额度计算CreditCalculation表
			logger.info("回滚--重新计算额度计算CreditCalculation表..");
			if(PoolComm.POOL_MODEL_01.equals(proDto.getPoolMode())){//总量模式
				this.txCreditCalculationTotal(proDto);

			}else if(PoolComm.POOL_MODEL_02.equals(proDto.getPoolMode())){//期限配比
				this.txCreditCalculationTerm(proDto);
			}
			ret.setRET_CODE(Constants.EBK_05);
			logger.info("资产出池后额度不足，数据回滚完成");
		}
		return ret;
		
	}

	@Override
	public List<CreditCalcuCache> txCreditCalculationCacheTerm(PedProtocolDto proDto,String flowNo) throws Exception {

		String bpsNo = proDto.getPoolAgreement();
		logger.info("试算：期限配比模式，票据池【"+bpsNo+"】各区间段额度计算开始....");

		List<CreditCalcuCache> creditCacheList = new ArrayList<CreditCalcuCache>();

		//时间段划分
		creditCacheList = this.creditCalCacheDueDt(proDto,flowNo);

		//高低风险现金流入数据组装
		creditCacheList = this.creditCalCacheTaskAmtIn(creditCacheList, bpsNo);

		//高低风险现金流出数据组装
		creditCacheList = this.creditCalCacheTaskAmtOut(creditCacheList, bpsNo,flowNo);

		//最终额度信息组装
		creditCacheList = this.creditCalCacheTaskAll(creditCacheList, bpsNo);

		System.out.println("期限匹配模式【临时计算】的结果日志打印：");
		for(CreditCalcuCache dto :creditCacheList){
			System.out.println(DateUtils.toString(dto.getStartDate(),DateUtils.ORA_DATES_FORMAT)+"      "+DateUtils.toString(dto.getEndDate(),DateUtils.ORA_DATES_FORMAT)+"      "+dto.getLowRiskIn()+"      "+dto.getLowRiskOut()+"      "+dto.getLowRiskCashFlow()+
					"      "+ dto.getHighRiskIn()+"      "+dto.getHighRiskOut()+"      "+dto.getHighRiskCashFlow()+"      "+
					"      "+dto.getLowRiskCredit()+"      "+dto.getHighRiskCredit()+"      "+dto.getAllCredit());

		}
		logger.info("期限配比模式，临时计算，票据池【"+bpsNo+"】各区间段额度计算结束....");
		this.txStoreAll(creditCacheList);
		return creditCacheList;

	}
	
	@Override
	public Ret txCreditUsedCheck(List<CreditRegisterCache> crdtRegCacheList,PedProtocolDto proDto, String flowNo) throws Exception {
		logger.info("额度校验开始，票据池【"+proDto.getPoolAgreement()+"】");
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("额度不足！");
		
		
		/*
		 * 【1】crdtRegCacheList落库
		 */
		if(null != crdtRegCacheList){			
			financialAdviceService.txForcedSaveList(crdtRegCacheList);
		}
		
		/*
		 * 【2】重新计算额度计算CreditCalculation表
		 */
		if(PoolComm.POOL_MODEL_01.equals(proDto.getPoolMode())){//总量模式
			this.txCreditCalculationCacheTotal(proDto,flowNo);
			
		}else if(PoolComm.POOL_MODEL_02.equals(proDto.getPoolMode())){//期限配比		
			this.txCreditCalculationCacheTerm(proDto,flowNo);
		}
		/*
		 * 【3】只要额度计算 CreditCalcuCache表中存在[高or低风险可用额度]小于0，则额度不足
		 */
		List<Object> listCalcultion = new ArrayList<Object>();
		
		//获取可用额度的最小值
		listCalcultion = this.queryCreditCalculCacheMinByBpsNo(proDto.getPoolAgreement(),flowNo);
		
		//获取已用额度
		List<Object> maxList = new ArrayList<Object>();
		maxList = this.queryCreditCalculCacheMaxByBpsNo(proDto.getPoolAgreement(), flowNo);
		
		if(null!=listCalcultion && listCalcultion.size()>0) {
            
			//获取高低风险可用金额
			Object[] obj = (Object[]) listCalcultion.get(0);
            BigDecimal lowRiskCredit =(BigDecimal)obj[0];//低风险可用
            BigDecimal highRiskCredit =(BigDecimal)obj[1];//高风险可用
            
            //获取全部流出金额
            BigDecimal lowRiskOut = BigDecimal.ZERO;
			BigDecimal highRiskOut = BigDecimal.ZERO;
			BigDecimal allOut =  BigDecimal.ZERO;
			
			if(null!=maxList && maxList.size()>0){					
				Object[] obj2 = (Object[]) maxList.get(0);
				lowRiskOut =(BigDecimal)obj2[0];
				highRiskOut =(BigDecimal)obj2[1];
				allOut = lowRiskOut.add(highRiskOut);//全部已用
			}
            
			if (lowRiskCredit.compareTo(BigDecimal.ZERO) >= 0 && highRiskCredit.compareTo(BigDecimal.ZERO) >= 0) {
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("额度充足！");
				logger.info("冻结校验之前额度充足，票据池【"+proDto.getPoolAgreement()+"】,流水号："+flowNo);
				
				
				//=====================冻结校验开始=============（汉口银行需要，其他行一定不需要该部分代码）=============================//
				
				//票据池保证金资产额度
				AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(proDto, PoolComm.ED_BZJ_HQ);
				BigDecimal bailAmt = atBillLow.getCrdtFree();
				
				
				if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_02)){//（1）票据冻结
					if(allOut.compareTo(bailAmt)>0){//只要已用大于保证金金额就是额度不足
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("额度不足！");
						logger.info("该客户的票据额度有冻结，额度不足，票据池【"+proDto.getPoolAgreement()+"】,流水号："+flowNo);
					}
				}
				
				if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_01)){//（2）保证金冻结
					if ((lowRiskCredit.subtract(bailAmt)).compareTo(BigDecimal.ZERO) < 0){//最小的低风险可用减去保证金金额之后小于0即为额度不足
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("额度不足！");
						logger.info("该客户的保证金有冻结，额度不足，票据池【"+proDto.getPoolAgreement()+"】,流水号："+flowNo);
					}
				}
				
				//（3）全冻结无需在此校验
				
				//=====================冻结校验结束==========================================//
				
			}
			
			
		}

		if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
			logger.info("额度不足！，票据池【"+proDto.getPoolAgreement()+"】,流水号："+flowNo);
			ret.setRET_MSG("额度不足!");
		}

        /*
         * 根据票据池编号删除额度计算临时表中的数据
         */
		if(null != crdtRegCacheList){			
			financialAdviceService.txDelList(crdtRegCacheList);
		}
        this.txDelCalculationCacheByBpsNo(proDto.getPoolAgreement(),flowNo);
		return ret;
	}

	@Override
	public Ret txCreditCalculationByProtocol(PedProtocolDto proDto) throws Exception {
		
		logger.info("更新票据池【"+proDto.getPoolAgreement()+"】额度开始...");
		
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		String apId = null;
		try {
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(proDto);
			apId = ap.getApId();
			
			//assetPool锁
			boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
			if(!isLockedSucss){//加锁失败
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该票据池有其他额度相关任务正在处理中，请稍后再试！");
				return ret;
			}
			
			
			//同步核心保证金，并重新计算额度
			this.txBailChangeAndCrdtCalculation(proDto);
			
			//解锁AssetPool表，并重新计算该表数据
			pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
			
		} catch (Exception e) {
			if(null != apId){				
				pedAssetPoolService.txReleaseAssetPoolLock(apId);
			}
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			logger.info("队列的额度更新方法执行异常，更新票据池【"+proDto.getPoolAgreement()+"】：",e);
		}
		
		return ret;
	}



	
	/**
	 *<p>描述:期限匹配现金流表查询</p>
	 * @param user
	 */
	@Override
	public String queryCreditCalculation(String bpsNo, User user) throws Exception {
		List values = new ArrayList();
		String hql = " from CreditCalculation cr where 1=1 ";
		if(StringUtils.isNotBlank(bpsNo)){
			hql=hql+" and cr.bpsNo = ?";
			values.add(bpsNo);
		}
		List list = this.find(hql,values);
		if(null!=list && list.size()>0){
			Map map = new HashMap();
			map.put("totalProperty", "results," + list.size());
			map.put("root", "rows");
			return JsonUtil.fromCollections(list,map);
		}else{
			return "";
		}

	}

	/**
	 * 获取该票据池中最小低风险可用额度与最小高风险可用额度
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-10-11下午9:24:49
	 */
	public List queryCreditCalculationMinByBpsNo(String bpsNo) throws Exception {
		List valueList = new ArrayList();
		String hql = "select min(cr.lowRiskCredit),min(cr.highRiskCredit) from CreditCalculation cr where cr.bpsNo = ? ";
		valueList.add(bpsNo);
		List list = this.find(hql,valueList);
		return  list ;
	}
	
	/**
	 * 获取该票据池中最大低风险流出与最大高风险流出
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-10-11下午9:25:24
	 */
	public List queryCreditCalculationMaxByBpsNo(String bpsNo) throws Exception {
		List valueList = new ArrayList();
		String hql = "select max(cr.lowRiskOut),max(cr.highRiskOut) from CreditCalculation cr where cr.bpsNo = ? ";
		valueList.add(bpsNo);
		List list = this.find(hql,valueList);
		return  list ;
	}
	
	/**
	 * 获取该票据池本次试算中最小低风险可用额度与最小高风险可用额度
	 * @param bpsNo
	 * @param flowNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-10-11下午8:53:01
	 */
	public List queryCreditCalculCacheMinByBpsNo(String bpsNo,String flowNo) throws Exception {
		List valueList = new ArrayList();
		String hql = "select min(cr.lowRiskCredit),min(cr.highRiskCredit) from CreditCalcuCache cr where cr.bpsNo = ? and cr.flowNo =? ";
		valueList.add(bpsNo);
		valueList.add(flowNo);
		List list = this.find(hql,valueList);
		return  list ;
	}
	
	/**
	 * 获取该票据池本次试算中最大高风险流出与最大低风险流出
	 * @param bpsNo
	 * @param flowNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-10-11下午8:53:43
	 */
	public List queryCreditCalculCacheMaxByBpsNo(String bpsNo,String flowNo) throws Exception {
		List valueList = new ArrayList();
		String hql = "select max(cr.lowRiskOut),max(cr.highRiskOut) from CreditCalcuCache cr where cr.bpsNo = ? and cr.flowNo =? ";
		valueList.add(bpsNo);
		valueList.add(flowNo);
		List list = this.find(hql,valueList);
		return  list ;
	}

	/**
	 *根据承兑协议编号查询资产登记总金额
	 * @param bpsNo 票据池协议号
	 * 	 * @param riskType 风险等级
	 * @return BigDecimal
	 * @throws Exception
	 */
	public BigDecimal querySumAssetAmt(String bpsNo,String riskType) throws Exception{
		
		logger.info("根据票据池编号【"+bpsNo+"】风险类型【"+riskType+"】查询资产登记总金额...");
		
		BigDecimal amt = new BigDecimal("0");
		String hql ="";
		List valueList = new ArrayList();
		valueList.add(bpsNo);
		valueList.add(riskType);
		hql = "select sum(a.assetAmount) from AssetRegister a where a.delFlag !='D' and a.bpsNo = ? and a.riskType = ?";
		List list = this.find(hql,valueList);
		if(null!=list && list.size()>0  && null!= list.get(0)){
			amt = (BigDecimal) list.get(0);
		}
		
		return amt;
	}

	/**
	 * 根据承兑协议编号查询融资用信登记总金额
	 * @param bpsNo 票据池协议号
	 * @param riskType 风险等级
	 * @return BigDecimal
	 * @throws Exception
	 */
	public BigDecimal querySumCreditAmt(String bpsNo,String riskType) throws Exception{
		
		logger.info("根据票据池编号【"+bpsNo+"】风险类型【"+riskType+"】查询融资用信登记总金额...");
		
		BigDecimal amt = new BigDecimal("0");
		String hql ="";
		List valueList = new ArrayList();
		valueList.add(bpsNo);
		valueList.add(riskType);
		hql = "select sum(a.occupyCredit)  from CreditRegister a where a.bpsNo = ? and a.riskType = ?";
		List list = this.find(hql,valueList);
		if(null!=list && list.size()>0  && null!= list.get(0)){
			amt = (BigDecimal) list.get(0);
		}
		return amt;
	}
	/**
	 * 根据票据池编号、风险类型、试算资产流水号查询融资用信登记总金额
	 * @param bpsNo 票据池协议号
	 * @param riskType 风险等级
	 * @return BigDecimal
	 * @throws Exception
	 */
	public BigDecimal querySumCreditCacheAmt(String bpsNo,String riskType,String flowNo) throws Exception{
		
		logger.info("根据票据池编号【"+bpsNo+"】风险类型【"+riskType+"】试算资产流水号【"+flowNo+"】查询融资用信登记总金额...");
		
		BigDecimal amt = new BigDecimal("0");

		String sql = "select sum(occupy_amount) from " +
				" (select  sum (c.occupy_amount) as occupy_amount from PED_CREDIT_REGISTER c where c.BPS_NO = '"+bpsNo+"' and c.RISK_TYPE ='"+riskType+"' " +
				" union" +
				" select  sum (c1.occupy_amount) as occupy_amount from PED_CREDIT_REGISTER_CACHE c1 where c1.BPS_NO = '"+bpsNo+"' and c1.RISK_TYPE ='"+riskType+"' and  c1.FLOW_NO='"+flowNo+"' ) a";
		List list = dao.SQLQuery(sql);
		if(null!=list && list.size()>0  && null!= list.get(0)){
			amt = (BigDecimal) list.get(0);
		}
		return amt;
	}
	/**
	 * 根据票据池编号删除额度计算临时表中的数据
	 * @param bpsNo
	 * @author Ju Nana
	 * @date 2021-6-7下午4:19:12
	 */
	private void txDelCalculationCacheByBpsNo(String bpsNo,String flowNo){
		
		logger.info("根据票据池编号【"+bpsNo+"】及流水号【"+flowNo+"】删除额度计算临时表中的数据...");
		
		List values= new ArrayList();
        String hql = " from  CreditCalcuCache c where 1=1";
        if(StringUtils.isNotBlank(bpsNo)){
            hql=hql+" and c.bpsNo = ? ";
            values.add(bpsNo);
        }
        if(StringUtils.isNotBlank(flowNo)){
            hql=hql+" and c.flowNo = ? ";
            values.add(flowNo);
        }
		List<CreditCalcuCache>  list  = this.find(hql,values);
		if(null!= list && list.size()>0){
			this.txDeleteAll(list);
		}
	}
	
	
	
	/**
	 *临时计算： 时间段划分
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-7上午11:09:01
	 */
	public List<CreditCalcuCache> creditCalCacheDueDt(PedProtocolDto proDto,String flowNo){
		String bpsNo = proDto.getPoolAgreement();
		
		String today = DateUtils.toDateString(new Date());
		String todaySqlStr = "TO_DATE('"+today+"', 'yyyy-mm-dd')";

		logger.info("临时计算：票据池【"+bpsNo+"】额度计算时间段划分开始....");
		/*
		 * 时间区间分段
		 * 		1）查询产生额度票据（含高低风险）到期日，按照到期日分组
		 *      2）查询用信合同项下未结清的借据到期日（无借据的按照合同的最早到期日）,按照到期日分组
		 *      3) 查询有效的主业务合同项下无借据的合同的最早到期日,按照到期日分组
		 *      3）将如上三个取并集
		 *      注意：冻结情况另行考虑
		 */
		
		
		List<Date> dueDateList = new ArrayList<Date>();
		String sql = "select   a.duedt  from("
				+" select due_date as duedt from ped_credit_register   where  bps_no = '"+bpsNo+"' and due_date >= "+todaySqlStr+"  group by due_date "
				+" union "
				+" select due_date as duedt from PED_CREDIT_REGISTER_CACHE   where  bps_no = '"+bpsNo+"' and FLOW_NO='"+flowNo+"' and due_date >= "+todaySqlStr+" group by due_date "
				+" union "
				+" select  a.asset_delay_duedt as due_date  from ped_asset_register a  "
				+" where a.bps_no = '"+bpsNo+"' and asset_delay_duedt >= "+todaySqlStr+" group by a.asset_delay_duedt  "
				+" ) a  order by a.duedt";

		dueDateList=dao.SQLQuery(sql);

		//时间段划分，起始日、截止日组装，默认最后一条的截至时间为2099-12-31
		List<CreditCalcuCache> creditList = new ArrayList<CreditCalcuCache>();
		int size = dueDateList.size();
		int lastSize = size - 1;
		for(int i =0;i<lastSize;i++){
			CreditCalcuCache crdt = new CreditCalcuCache();
			crdt.setFlowNo(flowNo);
			crdt.setBpsNo(bpsNo);//票据池协议编号
			crdt.setCustPoolName(proDto.getPoolName());//客户资产池名称
			crdt.setCertType(PoolComm.CRT_01);//默认组织机构代码
			crdt.setCertCode(proDto.getCustOrgcode());//组织机构代码
			crdt.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
			crdt.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
			Date dueDt = dueDateList.get(i);
			crdt.setCreateDate(DateUtils.getCurrDateTime());//创建时间
			crdt.setStartDate(dueDt);
			Date dueDt2 = dueDateList.get(i+1);
			crdt.setEndDate(DateTimeUtil.getUtilDate(dueDt2, -1));//前一天
			creditList.add(crdt);
		}
		//最后一条数据
		if(size > 0){
			CreditCalcuCache crdt = new CreditCalcuCache();
			crdt.setFlowNo(flowNo);
			crdt.setBpsNo(bpsNo);//票据池协议编号
			crdt.setCustPoolName(proDto.getPoolName());//客户资产池名称
			crdt.setCertType(PoolComm.CRT_01);//默认组织机构代码
			crdt.setCertCode(proDto.getCustOrgcode());//组织机构代码
			crdt.setCreateDate(DateUtils.getCurrDateTime());//创建时间
			crdt.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
			crdt.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
			Date dueDt = dueDateList.get(size-1);
			crdt.setStartDate(dueDt);
			crdt.setEndDate(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));
			creditList.add(crdt);
		}
		logger.info("到期日的列表："+dueDateList);
		String due = "";
		for(CreditCalcuCache cache : creditList){
			due = due + "    "+ DateTimeUtil.toDateString (cache.getEndDate());
		}
		logger.info("到期日的列表："+due);
		return creditList;
	}
	/**
	 * 临时计算：高低风险现金流入
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2021-5-8下午8:00:56
	 */
	public List<CreditCalcuCache> creditCalCacheTaskAmtIn(List<CreditCalcuCache> creditList,String bpsNo) throws Exception{

		logger.info("临时计算：票据池【"+bpsNo+"】额度计算高低风险现金流入数据组装开始....");

		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtIn = BigDecimal.ZERO;//低风险额度总额
		BigDecimal highTotalAmtIn = BigDecimal.ZERO;//高风险额度总额


		String sql = "select a  from AssetRegister a  " +
				" where  a.bpsNo = '"+bpsNo+"' and a.delFlag !='D' order by a.assetDelayDueDt asc";
		List<AssetRegister> billList = new ArrayList<AssetRegister>();

		billList = this.find(sql);

		for(AssetRegister billInf : billList){
            CreditCalcuCache crdt = creditList.get(count);
			int size = creditList.size();
			for(int i = count ;i < size;i++  ){
				crdt = creditList.get(count);
				
				if(count == 0 && DateUtils.checkOverLimited(crdt.getStartDate(), billInf.getAssetDelayDueDt())){
					// 资产到期日比第一个额度区间的开始日小，则计算在第一个时间区间内，这种情况发生在资产业务到期后没有解付
                    break;
				}
				
				if(DateUtils.isBetweenTowDay(crdt.getStartDate(), billInf.getAssetDelayDueDt(), crdt.getEndDate())){
					// 到期日在[开始时间，截至时间]之内; 
					break;
				}else{
					if(PoolComm.LOW_RISK.equals(billInf.getRiskType())){//低风险
						crdt.setLowRiskIn(lowTotalAmtIn);//低风险现金流入

					}else if(PoolComm.HIGH_RISK.equals(billInf.getRiskType())){//高风险
						crdt.setHighRiskIn(highTotalAmtIn);//高风险现金流入
					}
					count ++ ;
				}

			}
			if(PoolComm.LOW_RISK.equals(billInf.getRiskType())){//低风险
				lowTotalAmtIn =  lowTotalAmtIn.add(billInf.getAssetAmount());
                crdt.setLowRiskIn(lowTotalAmtIn);//低风险现金流入

			}else if(PoolComm.HIGH_RISK.equals(billInf.getRiskType())){//高风险
				highTotalAmtIn =  highTotalAmtIn.add(billInf.getAssetAmount());
                crdt.setHighRiskIn(highTotalAmtIn);//高风险现金流入
			}
		}
		return creditList;
	}
	/**
	 * 临时计算：高低风险现金流出
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2021-5-8下午10:12:12
	 */
	public List<CreditCalcuCache> creditCalCacheTaskAmtOut(List<CreditCalcuCache> creditList,String bpsNo,String flowNo) throws Exception{

		logger.info("临时计算：票据池【"+bpsNo+"】额度计算高低风险现金流出数据组装开始....");

		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtOut = new BigDecimal(0);//低风险用信额度总额
		BigDecimal highTotalAmtOut = new BigDecimal(0);//高风险用信额度总额

		
		/***********************===========================查询取ped_credit_register  与  ped_credit_register_cache 并集==================================****************************************/
		
		String sql = "select  ra.due_date,ra.occupy_credit,ra.risk_type,ra.id from" +
				"(select  r.id, r.due_date,r.occupy_credit,r.risk_type from ped_credit_register r where r.bps_no = '"+bpsNo+"'" +
				" group by r.due_date,r.occupy_credit,r.risk_type,r.id   " +
				" union" +
				" select rc.id, rc.due_date,rc.occupy_credit,rc.risk_type from ped_credit_register_cache rc where rc.bps_no = '"+bpsNo+"' and rc.FLOW_NO='"+flowNo+"'" +
				"  group by rc.due_date,rc.occupy_credit,rc.risk_type,rc.id  ) " +
				"  ra  order by ra.due_date ";
		System.out.println("----:"+sql);

		List crdtList = new ArrayList();
		crdtList =  dao.SQLQuery(sql);
		//billList = //查询结果
		int creditSize = creditList.size();
		int size = crdtList.size();
		//billList = //查询结果
		if(size>0){
			Date dueDt = null;
			BigDecimal crdtAmt = BigDecimal.ZERO;
			String riskLevel = null;

			for(int k=0;k<size;k++){
                CreditCalcuCache crdt = creditList.get(count);
				Object[] obj = (Object[])crdtList.get(k);

				dueDt = (Date) obj[0];
				crdtAmt = new BigDecimal(String.valueOf(obj[1]));
				riskLevel = String.valueOf(obj[2]);

				for(int i = count ;i < creditSize;i++  ){
					crdt = creditList.get(count);
					
					if(count == 0 && DateUtils.checkOverLimited(crdt.getStartDate(), dueDt)){
						// 用信业务到期日比第一个额度区间的开始日小，则计算在第一个时间区间内，这种情况发生在用信业务到期后没有结清的情况
	                    break;
					}
					
					if(DateUtils.isBetweenTowDay(crdt.getStartDate(), dueDt, crdt.getEndDate())){
						// 到期日在[开始时间，截至时间]之内
						break;
					}else{
						if(PoolComm.LOW_RISK.equals(riskLevel)){//低风险
							crdt.setLowRiskOut(lowTotalAmtOut);//低风险现金流出

						}else if(PoolComm.HIGH_RISK.equals(riskLevel)){//高风险
							crdt.setHighRiskOut(highTotalAmtOut);//高风险现金流出
						}
						count ++ ;
					}

				}
				if(PoolComm.LOW_RISK.equals(riskLevel)){//低风险
					lowTotalAmtOut =  lowTotalAmtOut.add(crdtAmt);
                    crdt.setLowRiskOut(lowTotalAmtOut);//低风险现金流出

				}else if(PoolComm.HIGH_RISK.equals(riskLevel)){//高风险
					highTotalAmtOut =  highTotalAmtOut.add(crdtAmt);
                    crdt.setHighRiskOut(highTotalAmtOut);//高风险现金流出
				}
			}
		}
		return creditList;
	}
	/**
	 * 临时计算：额度信息组装
	 * @param creditList
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-8下午10:26:06
	 */
	public List<CreditCalcuCache> creditCalCacheTaskAll(List<CreditCalcuCache> creditList,String bpsNo){

		logger.info("临时计算：票据池【"+bpsNo+"】额度计算最终额度信息数据组装开始....");
		int size = creditList.size();
		for(int i = 0 ;i<size;i++){
			CreditCalcuCache crdt = creditList.get(i);

			//(1)
//			logger.info("（1）高低风险现金流入流出为0的数据补齐");
			if(i>0){
				//当在某个区间段内没有低/高风险的票的情况下，当前低/高风险的现金流入与上一时间区间一致
				//当在某个区间段内没有低/高风险的融资业务的情况下，当前低/高风险的现金流出与上一时间区间一致

				//低风险流入，0流入补齐
				if(crdt.getLowRiskIn().compareTo(BigDecimal.ZERO)<=0){
					crdt.setLowRiskIn(creditList.get(i-1).getLowRiskIn());
				}
				//高风险流入，0流入补齐
				if(crdt.getHighRiskIn().compareTo(BigDecimal.ZERO)<=0){
					crdt.setHighRiskIn(creditList.get(i-1).getHighRiskIn());
				}
				//低风险流出，0流出补齐
				if(crdt.getLowRiskOut().compareTo(BigDecimal.ZERO)<=0){
					crdt.setLowRiskOut(creditList.get(i-1).getLowRiskOut());
				}
				//高风险流出，0流出补齐
				if(crdt.getHighRiskOut().compareTo(BigDecimal.ZERO)<=0){
					crdt.setHighRiskOut(creditList.get(i-1).getHighRiskOut());
				}
			}
			//(2)高低风险现金流时点组装
//			logger.info("(2)高低风险现金流时点组装");
			BigDecimal lowCrdt = new BigDecimal(0);
			if(crdt.getHighRiskIn().compareTo(crdt.getHighRiskOut())>=0){//高风险额度足够高风险业务占用
				crdt.setHighRiskCashFlow(crdt.getHighRiskIn().subtract(crdt.getHighRiskOut()));//高风险现金时点=高风险现金流入-高风险现金流出
			}else{
//                crdt.setHighRiskCashFlow(crdt.getHighRiskIn());//占完全部高风险额度
				crdt.setHighRiskCashFlow(new BigDecimal(0));//占完全部高风险额度
				lowCrdt=crdt.getHighRiskOut().subtract(crdt.getHighRiskIn());
				//缺少部分留待占用低风险额度
			}
			//(3)低风险现金流时点组装
//			logger.info("(3)低风险现金流时点组装");
			crdt.setLowRiskCashFlow(crdt.getLowRiskIn().subtract(crdt.getLowRiskOut()).subtract(lowCrdt));//低风险现金时点=低风险现金流入-低风险现金流出-高风险业务需要占用低风险的额度
		}
		//(4)双重for循环组装可用额度
//		logger.info("(4)双重for循环组装可用额度");
		CreditCalcuCache crdt1 = null;
		CreditCalcuCache crdt2 = null;
		for(int j = 0 ;j<size;j++){
			crdt1 = creditList.get(j);
			creditList.get(j).setHighRiskCredit(creditList.get(j).getHighRiskCashFlow());//默认高风险可用额度为当前时点的现金流
			creditList.get(j).setLowRiskCredit(creditList.get(j).getLowRiskCashFlow());//默认低风险可用额度为当前时点的现金流
			for(int k = j+1 ;k<creditList.size();k++){
				crdt2 = creditList.get(k);
				//高风险可用额度
				if(crdt1.getHighRiskCashFlow().compareTo(crdt2.getHighRiskCashFlow())>0
						&&creditList.get(j).getHighRiskCredit().compareTo(crdt2.getHighRiskCashFlow())>0 ){//只要后续日期中存在时点上比当前可用额度少，则该时点可用额度为后续最小点的值
					creditList.get(j).setHighRiskCredit(crdt2.getHighRiskCashFlow());
				}
				//低风险可用额度
				if(crdt1.getLowRiskCashFlow().compareTo(crdt2.getLowRiskCashFlow())>0
						&& creditList.get(j).getLowRiskCredit().compareTo(crdt2.getLowRiskCashFlow())>0){//只要后续日期中存在时点上比当前可用额度少，则该时点可用额度为后续最小点的值
					creditList.get(j).setLowRiskCredit(crdt2.getLowRiskCashFlow());
				}
			}
		}
		return creditList;
	}
	
	@Override
	public CreditCalcuCache txCreditCalculationCacheTotal(PedProtocolDto proDto,String flowNo) throws Exception {
		String bpsNo = proDto.getPoolAgreement();
		//先删除临时表中的期限配比总量模式数据
		//20210610 此处先不删除，试算时流水号是唯一的，故每次试算的结果也是唯一的，校验完成以后再删除
//		this.txDelCalculationCacheByBpsNo(bpsNo,flowNo);
		CreditCalcuCache crdtCal = new CreditCalcuCache();
        crdtCal.setCustPoolName(proDto.getPoolName());//客户资产池名称
        crdtCal.setCertType(PoolComm.CRT_01);//默认组织机构代码
        crdtCal.setCertCode(proDto.getCustOrgcode());//组织机构代码
        crdtCal.setBpsNo(bpsNo);            //客户票据池编号
		crdtCal.setStartDate(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT));      //开始日期                    --今天
		crdtCal.setEndDate(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));            //结束日期             --2099-12-31
		crdtCal.setLowRiskIn(this.querySumAssetAmt(bpsNo,PoolComm.LOW_RISK));        //低风险流入资金      --SUM AssetRegister表 中【该客户】【低风险】的【票据&保证金】总资产金额

		/*************************************************取Credit_cache并集*******************************************************************************/
		
		crdtCal.setLowRiskOut(this.querySumCreditCacheAmt(bpsNo,PoolComm.LOW_RISK,flowNo));       //低风险流出资金      --SUM CreditRegister表 中【该客户】【低风险】的总融资金额
		crdtCal.setHighRiskIn(this.querySumAssetAmt(bpsNo,PoolComm.HIGH_RISK));       //高风险流入资金     --SUM AssetRegister表 中【该客户】【高风险】的【票据】总资产金额
		
		/*************************************************取Credit_cache并集*******************************************************************************/
		crdtCal.setHighRiskOut(this.querySumCreditCacheAmt(bpsNo,PoolComm.HIGH_RISK,flowNo));      //高风险流出资金     --SUM CreditRegister表 中【该客户】【高风险】的总融资金额

		crdtCal.setTransDate(DateUtils.getCurrDate());        //交易日期(不含时分秒)
		crdtCal.setCreateDate(DateUtils.getCurrDateTime()); //创建日期(含时分秒)
		crdtCal.setUpdateDate(new Date()); //更新日期(含时分秒)
		crdtCal.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);    	      //逻辑删除标记-N未删除、D已删除
        crdtCal.setFlowNo(flowNo);//流水号

		List<CreditCalcuCache> list  = new ArrayList<CreditCalcuCache>();
		list.add(crdtCal);
		list=this.creditCalCacheTaskAll(list,bpsNo);
		crdtCal = list.get(0);

		this.txStore(crdtCal);//落库
		return crdtCal;
	}
	/**
	 *<p>描述:期限匹配新增高低风险流出时试算</p>
	 * @param creditCalcuCacheList
	 * @param pedProtocolDto
	 */
	@Override
	public String txCreditCalCahceForAddOut(List<CreditCalcuCache> creditCalcuCacheList, PedProtocolDto pedProtocolDto,String flowNo) throws Exception {
		logger.info("新增高低风险现金流入流出时，额度计算开始。。。。。。。。");
		creditCalcuCacheList = this.creditCalCacheTaskAll(creditCalcuCacheList,pedProtocolDto.getPoolAgreement());
		List<CreditCalcutionBean> calcuCachelist = new ArrayList<CreditCalcutionBean>();
		/*
		 * 【3】只要额度计算 CreditCalcuCache表中存在[高or低风险可用额度]小于0，则额度不足
		 */
		for(CreditCalcuCache calcuCache:creditCalcuCacheList){
			CreditCalcutionBean bean = new CreditCalcutionBean();
			BigDecimal lowRiskCashFlowNew = calcuCache.getLowRiskCashFlowNew();
			BigDecimal highRiskCashFlowNew = calcuCache.getHighRiskCashFlowNew();

			calcuCache.setLowRiskCashFlowNew(calcuCache.getLowRiskCashFlow());
			calcuCache.setLowRiskCashFlow(lowRiskCashFlowNew);

			calcuCache.setHighRiskCashFlowNew(calcuCache.getHighRiskCashFlow());
			calcuCache.setHighRiskCashFlow(highRiskCashFlowNew);

			if (calcuCache.getLowRiskCredit().compareTo(BigDecimal.ZERO) >= 0
					&& calcuCache.getHighRiskCredit().compareTo(BigDecimal.ZERO) >= 0) {
				calcuCache.setIsAdeQuate(PoolComm.IS_ADEQUATE_1);//充足
			}else{
				calcuCache.setIsAdeQuate(PoolComm.IS_ADEQUATE_0);//不足
			}
			BeanUtil.beanCopy(calcuCache,bean);
			SimpleDateFormat df1 = new SimpleDateFormat(DateUtils.ORA_DATES_FORMAT);
			SimpleDateFormat df2 = new SimpleDateFormat(DateUtils.ORA_DATES_FORMAT);

			String start  = df1.format(calcuCache.getStartDate());
			String end  = df2.format(calcuCache.getEndDate());
			bean.setStartDate(start);
			bean.setEndDate(end);
			calcuCachelist.add(bean);
		}
		Map map = new HashMap();
		map.put("totalProperty", "results," + calcuCachelist.size());
		map.put("root", "rows");
		return  JsonUtil.fromCollections(calcuCachelist,map);
	}

	
	@Override
	public void txReCreditCalculationTask(List<PedProtocolDto> proList) throws Exception {
		
		logger.info("批量重新计算票据池额度开始...");
		for(PedProtocolDto pro : proList){

			String bpsNo = pro.getPoolAgreement();
			String proId = pro.getPoolInfoId();
			try {
				String   id = 	bpsNo +"-"+ Long.toString(System.currentTimeMillis());//id如果直接取业务id的话展示会重复，这里用时间戳生成
				Map<String,String> reqParams = new HashMap<String,String>();
				reqParams.put("proId", proId);
				autoTaskPublishService.publishTask(null, AutoTaskNoDefine.POOL_AUTO_CALCU_NO, id, AutoTaskNoDefine.BUSI_TYPE_CAL, reqParams, bpsNo, bpsNo, null, null);
				
			} catch (Exception e) {
				
				logger.error("MIS日终出账任务，重新计算票据池额度,计算【"+pro.getPoolAgreement()+"】票据池处理失败！",e);
				
			}
			
			logger.info("批量重新计算票据池额度结束！");
			
			
		}
		
	}

	@Override
	public void txOnlineBusiReleseCredit(List<String> releseIds,String bpsNo) throws Exception {
		
		PedProtocolDto pro = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
		/*
		 * 锁AssetPool表
		 */
		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pro);
		String apId = ap.getApId();
		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
		if(!isLockedSucss){//加锁失败
			logger.info("票据池【"+bpsNo+"】加锁失败！");
			throw new Exception("票据池【"+bpsNo+"】加锁失败！");
		}
		
		/*
		 * 用信业务登记表删除数据
		 */
		
		creditRegisterService.txDelCreditRegisterByBusiIds(releseIds);
		
		
		/*
		 * 同步核心保证金，并重新计算额度
		 */
		this.txBailChangeAndCrdtCalculation(pro);
		
		
		/*
		 * 解锁AssetPool表，并重新计算该表数据
		 */
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
		
	}

	@Override
	public void txOnlineBusiCreditChange(String contractNo,List<PedCreditDetail> crdtDetailList, String bpsNo)throws Exception {
		PedProtocolDto pro = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
		/*
		 * 锁AssetPool表
		 */
		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pro);
		String apId = ap.getApId();
		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
		if(!isLockedSucss){//加锁失败
			logger.info("票据池【"+bpsNo+"】加锁失败！");
			throw new Exception("票据池【"+bpsNo+"】加锁失败！");
		}
		
		/*
		 * 根据主业务合同号删除用信业务登记表数据
		 */
		List<CreditRegister> crList = creditRegisterService.queryCreditContByNo(contractNo,null, PoolComm.VT_1);
		if(null != crList){// 为null只会在测试环境出现			
			creditRegisterService.txDeleteAll(crList);
		}

		
		/*
		 * 转换为借据明细占用额度
		 */
		for(PedCreditDetail loan : crdtDetailList){			
			CreditRegister crdtReg = creditRegisterService.createCreditRegister(loan, pro, apId);
			creditRegisterService.txSaveCreditRegister(crdtReg);
		}
		
		
		/*
		 * 同步核心保证金，并重新计算额度
		 */
		this.txBailChangeAndCrdtCalculation(pro);
		
		/*
		 * 解锁AssetPool表，并重新计算该表数据
		 */
		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
		
	}
	
	
	@Override
	public void txUpdateBailAndCalculationCredit(String apId,PedProtocolDto dto) throws Exception{
		
		String bpsNo = dto.getPoolAgreement();//票据池协议编号
		
		//（1）保证金同步
		BailDetail bail = poolBailEduService.txUpdateBailDetail(bpsNo);
		
//		//如下到（2）之前为测试代码
//		BailDetail bail = poolBailEduService.queryBailDetailByBpsNo(bpsNo);
		
		//（2）判断保证金是否发生变化
		boolean isChange =  assetRegisterService.txCurrentDepositAssetChange(bpsNo, bail.getAssetNb(), bail.getAssetLimitFree());
		
		//（3）若保证金发生变化，则更新资产计算表
		if(isChange){
			
			/*
			 * 重新计算池额度信息
			 */
			if(PoolComm.POOL_MODEL_01.equals(dto.getPoolMode())){//总量模式
				this.txCreditCalculationTotal(dto);
			}else{//期限配比					
				this.txCreditCalculationTerm(dto);
			}
			
			/*
			 * 不解锁AssetPool表，重新计算该表数据
			 */
			pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,false);
		}
	}

	public List<CreditCalculation> queryCreditCalculationListByBpsNo(String bpsNo) throws Exception {
		List valueList = new ArrayList();
		String hql = "select cc from CreditCalculation cc where cc.bpsNo = ? ";
		valueList.add(bpsNo);
		
		
		hql+= " order by cc.startDate ";
		List<CreditCalculation> list = (List<CreditCalculation>)this.find(hql,valueList);
		if(null != list && list.size()>0){
			return  list ;
		}
		return null;
	}

	@Override
	public List<CreditCalculation> queryCreditCalculationList(String bpsNo)throws Exception {
		List values = new ArrayList();
		String hql = " from CreditCalculation cr where 1=1 ";
		if(StringUtils.isNotBlank(bpsNo)){
			hql=hql+" and cr.bpsNo = ?";
			values.add(bpsNo);
		}
		hql+=" order by cr.startDate ";
		List<CreditCalculation> list = (List<CreditCalculation>)this.find(hql,values);
		if(null!=list && list.size()>0){
			return list;
		}
		return null;

	}

	@Override
	public BailDetail txBailChangeAndCrdtCalculation(PedProtocolDto pro)throws Exception {

		String bpsNo =pro.getPoolAgreement();//票据池编号
		
		logger.info("票据池"+bpsNo+"核心保证金同步及额度重新计算开始....");

		//核心保证金同步
		BailDetail bail = null;
		try {			
			bail = poolBailEduService.txUpdateBailDetail(pro.getPoolAgreement());
			//测试代码：
//		     bail = poolBailEduService.queryBailDetailByBpsNo(pro.getPoolAgreement());
		} catch (Exception e) {
			throw new Exception("核心系统保证金同步异常，请联系票据池系统排查与核心报文交互问题！核心错误内容："+ e.getMessage());
		}
		
		try {
			
			/*
			 * 判断并更新保证金资产登记表信息
			 */
			assetRegisterService.txCurrentDepositAssetChange(bpsNo, bail.getAssetNb(), bail.getAssetLimitFree());
			
			/*
			 * 重新计算池额度信息
			 */
			if(PoolComm.POOL_MODEL_01.equals(pro.getPoolMode())){//总量模式
				this.txCreditCalculationTotal(pro);
			}else if(PoolComm.POOL_MODEL_02.equals(pro.getPoolMode())){//期限配比					
				this.txCreditCalculationTerm(pro);
			}
			
		} catch (Exception e) {
			logger.info("票据池额度更新失败：",e);
		}
		
		
		return bail;
		
	}

	@Override
	public Ret txOnlineCreditUsed(PlOnlineCrdt batch) {
		Ret crdtCheckRet = new Ret();//额度校验返回对象
		
		try {
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, batch.getBpsNo(), null, null, null);
			AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
			String apId = ap.getApId();
			String bpsNo = dto.getPoolAgreement();
			List<CreditRegister> crdtRegList = new ArrayList<CreditRegister>();
			
			//保证金同步
			BailDetail bail = poolBailEduService.queryBailDetailByBpsNo(bpsNo);
			
			//保证金资产登记处理
			assetRegisterService.txCurrentDepositAssetChange(bpsNo, bail.getAssetNb(), bail.getAssetLimitFree());
	        
			//额度占用
			PedCreditDetail crdtDetail = pedOnlineCrdtService.creatCrdtDetailByPlOnlineCrdt(batch, dto);
			CreditRegister crdtReg = creditRegisterService.createCreditRegister(crdtDetail, dto, apId);
			crdtRegList.add(crdtReg);
			crdtCheckRet =  this.txCreditUsed(crdtRegList, dto);

		} catch (Exception e) {
			crdtCheckRet.setRET_CODE(Constants.TX_FAIL_CODE);
			crdtCheckRet.setRET_MSG("票据池额度处理异常!");
			logger.error("在线流贷OnlineCrdtAutoTaskDispatch票据池额度占用异常!",e);
		}
		
		return crdtCheckRet;
	}

	@Override
	public Ret txAssetOutCheck(List<AssetRegister> assetOutList,PedProtocolDto proDto) throws Exception {
		
		logger.info("额度校验开始，票据池【"+proDto.getPoolAgreement()+"】");
		Ret ret = new Ret();
		ret.setRET_CODE(Constants.TX_FAIL_CODE);
		ret.setRET_MSG("票据池额度不足！");
		
		if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_02)){//票据冻结，不允许出池
			logger.info("票据池【"+proDto.getPoolAgreement()+"】票据额度冻结，不允许出池！");
			return ret;
		}
		
		String flowNo = Long.toString(System.currentTimeMillis());
		
		List<AssetRegisterCache> arcList = new ArrayList<AssetRegisterCache>();
		if(null != assetOutList){
			for(AssetRegister ar : assetOutList){
				AssetRegisterCache arc = new AssetRegisterCache();
			    BeanUtil.beanCopy(ar,arc);
			    arc.setFlowNo(flowNo);
			    arcList.add(arc);
			}
			/*
			 * 【1】arcList资产缓存落库--事务方法
			 */
			logger.info("【1】资产出池额度校验，票据池【"+proDto.getPoolAgreement()+"】arcList资产缓存落库...");
			financialAdviceService.txForcedSaveList(arcList);
		}
		
		logger.info("【2】资产出池额度校验，票据池【"+proDto.getPoolAgreement()+"】核心保证金同步..");
		//核心保证金同步
		BailDetail bail = null;
		try {			
			bail = poolBailEduService.txUpdateBailDetail(proDto.getPoolAgreement());
			
			//测试代码：
//		    bail = poolBailEduService.queryBailDetailByBpsNo(proDto.getPoolAgreement());
		} catch (Exception e) {
			ret.setRET_MSG("核心系统保证金同步异常，请联系票据池系统排查与核心报文交互问题！核心错误内容："+ e.getMessage());
			return ret;
		}
		//判断并更新保证金资产登记表信息
		assetRegisterService.txCurrentDepositAssetChange(proDto.getPoolAgreement(), bail.getAssetNb(), bail.getAssetLimitFree());
		
		
		/*
		 * 【3】重新计算额度计算CreditCalculation表
		 */
		logger.info("【3】资产出池额度校验，票据池【"+proDto.getPoolAgreement()+"】重新计算额度计算CreditCalculation表..");
		if(PoolComm.POOL_MODEL_01.equals(proDto.getPoolMode())){//总量模式
			this.txOutCreditCalculationCacheTotal(proDto,flowNo);
			
		}else if(PoolComm.POOL_MODEL_02.equals(proDto.getPoolMode())){//期限配比		
			this.txOutCreditCalculationCacheTerm(proDto,flowNo);
		}
		
		/*
		 * 【4】只要额度计算 CreditCalcuCache表中存在[高or低风险可用额度]小于0，则额度不足
		 */
		logger.info("【4】资产出池额度校验，票据池【"+proDto.getPoolAgreement()+"】只要额度计算 CreditCalcuCache表中存在[高or低风险可用额度]小于0，则额度不足...");
		List<Object> listCalcultion = new ArrayList<Object>();

		listCalcultion = this.queryCreditCalculCacheMinByBpsNo(proDto.getPoolAgreement(),flowNo);
		if(null!=listCalcultion && listCalcultion.size()>0) {
            Object[] obj = (Object[]) listCalcultion.get(0);
            BigDecimal lowRiskCredit =(BigDecimal)obj[0];
            BigDecimal highRiskCredit =(BigDecimal)obj[1];
			if (lowRiskCredit.compareTo(BigDecimal.ZERO) >= 0
					&& highRiskCredit.compareTo(BigDecimal.ZERO) >= 0) {
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("额度充足，资产出池校验通过！");
				logger.info("额度充足！，票据池【"+proDto.getPoolAgreement()+"】,流水号："+flowNo);
			}
			
			
			//===================================保证金冻结校验======================================//
			
			if(StringUtils.isNotBlank(proDto.getFrozenstate())&&proDto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_01)){//（2）保证金冻结
				
				//票据池保证金资产额度
				AssetType atBillLow = pedAssetTypeService.queryPedAssetTypeByProtocol(proDto, PoolComm.ED_BZJ_HQ);
				BigDecimal bailAmt = atBillLow.getCrdtFree();
				
				if ((lowRiskCredit.subtract(bailAmt)).compareTo(BigDecimal.ZERO) < 0){//最小的低风险可用减去保证金金额之后小于0即为额度不足
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("额度不足！");
					logger.info("该客户的保证金有冻结，额度不足，票据池【"+proDto.getPoolAgreement()+"】,流水号："+flowNo);
				}
			}

		}

		if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
			logger.info("额度不足，资产出池校验不通过，票据池【"+proDto.getPoolAgreement()+"】,流水号："+flowNo);
			
			if(PoolComm.POOL_MODEL_02.equals(proDto.getPoolMode())){				
				ret.setRET_MSG("票据池额度期限匹配不通过。");
			}else{
				ret.setRET_MSG("票据池额度不足。");
			}
		}

        /*
         * 【5】根据票据池编号删除额度计算临时表中的数据
         */
		logger.info("【4】资产出池额度校验，票据池【"+proDto.getPoolAgreement()+"】根据票据池编号删除额度计算临时表中的数据...");
        financialAdviceService.txDelList(arcList);
        this.txDelCalculationCacheByBpsNo(proDto.getPoolAgreement(),flowNo);
		return ret;

	}

	@Override
	public List<CreditCalcuCache> txOutCreditCalculationCacheTerm(PedProtocolDto proDto,String flowNo) throws Exception {
		String bpsNo = proDto.getPoolAgreement();
		logger.info("资产出池试算：期限配比模式，票据池【"+bpsNo+"】各区间段额度计算开始....");

		List<CreditCalcuCache> creditCacheList = new ArrayList<CreditCalcuCache>();

		//时间段划分
		creditCacheList = this.outCreditCalCacheDueDt(proDto,flowNo);

		//高低风险现金流入数据组装
		creditCacheList = this.outCreditCalCacheTaskAmtIn(creditCacheList, bpsNo,flowNo);

		//高低风险现金流出数据组装
		creditCacheList = this.outCreditCalCacheTaskAmtOut(creditCacheList, bpsNo);

		//最终额度信息组装
		creditCacheList = this.creditCalCacheTaskAll(creditCacheList, bpsNo);

		System.out.println("期限匹配模式【出池临时计算】的结果日志打印：");
		for(CreditCalcuCache dto :creditCacheList){
			System.out.println(DateUtils.toString(dto.getStartDate(),DateUtils.ORA_DATES_FORMAT)+"      "+DateUtils.toString(dto.getEndDate(),DateUtils.ORA_DATES_FORMAT)+"      "+dto.getLowRiskIn()+"      "+dto.getLowRiskOut()+"      "+dto.getLowRiskCashFlow()+
					"      "+ dto.getHighRiskIn()+"      "+dto.getHighRiskOut()+"      "+dto.getHighRiskCashFlow()+"      "+
					"      "+dto.getLowRiskCredit()+"      "+dto.getHighRiskCredit()+"      "+dto.getAllCredit());

		}
		logger.info("期限配比模式，资产出池临时计算，票据池【"+bpsNo+"】各区间段额度计算结束....");
		this.txStoreAll(creditCacheList);
		return creditCacheList;
		
	}

	@Override
	public CreditCalcuCache txOutCreditCalculationCacheTotal(PedProtocolDto proDto,String flowNo) throws Exception {
		String bpsNo = proDto.getPoolAgreement();
		CreditCalcuCache crdtCal = new CreditCalcuCache();
        crdtCal.setCustPoolName(proDto.getPoolName());//客户资产池名称
        crdtCal.setCertType(PoolComm.CRT_01);//默认组织机构代码
        crdtCal.setCertCode(proDto.getCustOrgcode());//组织机构代码
        crdtCal.setBpsNo(bpsNo);            //客户票据池编号
		crdtCal.setStartDate(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_FORMAT));      //开始日期                    --今天
		crdtCal.setEndDate(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));            //结束日期             --2099-12-31
		crdtCal.setLowRiskIn(this.querySumAssetCacheAmt(bpsNo,PoolComm.LOW_RISK,flowNo));        //低风险流入资金      --SUM AssetRegisterCache表 中【该客户】【低风险】的【票据&保证金】总资产金额
		crdtCal.setLowRiskOut(this.querySumCreditAmt(bpsNo,PoolComm.LOW_RISK));       //低风险流出资金      --SUM CreditRegister表 中【该客户】【低风险】的总融资金额
		crdtCal.setHighRiskIn(this.querySumAssetCacheAmt(bpsNo,PoolComm.HIGH_RISK,flowNo));       //高风险流入资金     --SUM AssetRegister表 中【该客户】【高风险】的【票据】总资产金额
		crdtCal.setHighRiskOut(this.querySumCreditAmt(bpsNo,PoolComm.HIGH_RISK));      //高风险流出资金     --SUM AssetRegisterCache表 中【该客户】【高风险】的总融资金额
		crdtCal.setTransDate(DateUtils.getCurrDate());        //交易日期(不含时分秒)
		crdtCal.setCreateDate(DateUtils.getCurrDateTime()); //创建日期(含时分秒)
		crdtCal.setUpdateDate(new Date()); //更新日期(含时分秒)
		crdtCal.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);    	      //逻辑删除标记-N未删除、D已删除
        crdtCal.setFlowNo(flowNo);//流水号

		List<CreditCalcuCache> list  = new ArrayList<CreditCalcuCache>();
		list.add(crdtCal);
		list=this.creditCalCacheTaskAll(list,bpsNo);
		crdtCal = list.get(0);

		this.txStore(crdtCal);//落库
		return crdtCal;
		
	}
	
	
	
	
	/**
	 * 资产出池临时计算： 时间段划分
	 * @param bpsNo
	 * @return
	 * @author Ju Nana
	 * @date 2021-5-7上午11:09:01
	 */
	public List<CreditCalcuCache> outCreditCalCacheDueDt(PedProtocolDto proDto,String flowNo){
		String bpsNo = proDto.getPoolAgreement();
		
		String today = DateUtils.toDateString(new Date());
		String todaySqlStr = "TO_DATE('"+today+"', 'yyyy-mm-dd')";

		logger.info("出池临时计算：票据池【"+bpsNo+"】额度计算时间段划分开始....");
		/*
		 * 时间区间分段
		 * 		1）查询产生额度票据（含高低风险）到期日，按照到期日分组
		 *      2）查询用信合同项下未结清的借据到期日（无借据的按照合同的最早到期日）,按照到期日分组
		 *      3) 查询有效的主业务合同项下无借据的合同的最早到期日,按照到期日分组
		 *      3）将如上三个取并集
		 *      注意：冻结情况另行考虑
		 */
		
		
		List<Date> dueDateList = new ArrayList<Date>();
		
		String sql = "select a.duedt from("
				+" select due_date as duedt from ped_credit_register   where  bps_no = '"+bpsNo+"' and due_date >= "+todaySqlStr+"  group by due_date "
				+" union"
				+" select  a.asset_delay_duedt as due_date  from ped_asset_register_cache a  "
				+"where a.bps_no = '"+bpsNo+"' and a.FLOW_NO = '"+flowNo+"' and a.asset_delay_duedt >= "+todaySqlStr+"  group by a.asset_delay_duedt  "
				+" ) a  order by a.duedt";

		dueDateList=dao.SQLQuery(sql);

		dueDateList=dao.SQLQuery(sql);

		//时间段划分，起始日、截止日组装，默认最后一条的截至时间为2099-12-31
		List<CreditCalcuCache> creditList = new ArrayList<CreditCalcuCache>();
		int size = dueDateList.size();
		int lastSize = size - 1;
		for(int i =0;i<lastSize;i++){
			CreditCalcuCache crdt = new CreditCalcuCache();
			crdt.setFlowNo(flowNo);
			crdt.setBpsNo(bpsNo);//票据池协议编号
			crdt.setCustPoolName(proDto.getPoolName());//客户资产池名称
			crdt.setCertType(PoolComm.CRT_01);//默认组织机构代码
			crdt.setCertCode(proDto.getCustOrgcode());//组织机构代码
			crdt.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
			crdt.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
			Date dueDt = dueDateList.get(i);
			crdt.setCreateDate(DateUtils.getCurrDateTime());//创建时间
			crdt.setStartDate(dueDt);
			Date dueDt2 = dueDateList.get(i+1);
			crdt.setEndDate(DateTimeUtil.getUtilDate(dueDt2, -1));//前一天
			creditList.add(crdt);
		}
		if(size > 0){
			CreditCalcuCache crdt = new CreditCalcuCache();
			crdt.setFlowNo(flowNo);
			crdt.setBpsNo(bpsNo);//票据池协议编号
			crdt.setCustPoolName(proDto.getPoolName());//客户资产池名称
			crdt.setCertType(PoolComm.CRT_01);//默认组织机构代码
			crdt.setCertCode(proDto.getCustOrgcode());//组织机构代码
			crdt.setCreateDate(DateUtils.getCurrDateTime());//创建时间
			crdt.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
			crdt.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
			Date dueDt = dueDateList.get(size-1);
			crdt.setStartDate(dueDt);
			crdt.setEndDate(DateTimeUtil.parse("2099-12-31",DateUtils.ORA_DATES_FORMAT));
			creditList.add(crdt);
		}

		return creditList;
	}
	
	/**
	 * 资产出池临时计算：高低风险现金流入
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2021-5-8下午8:00:56
	 */
	public List<CreditCalcuCache> outCreditCalCacheTaskAmtIn(List<CreditCalcuCache> creditList,String bpsNo,String flowNo) throws Exception{

		logger.info("资产出池临时计算：票据池【"+bpsNo+"】额度计算高低风险现金流入数据组装开始....");

		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtIn = BigDecimal.ZERO;//低风险额度总额
		BigDecimal highTotalAmtIn = BigDecimal.ZERO;//高风险额度总额

		
		String sql = "select a  from AssetRegisterCache a  " +
				     " where  a.bpsNo = '"+bpsNo+"' and a.flowNo = '"+flowNo+"' order by a.assetDelayDueDt asc";
		List<AssetRegisterCache> billList = new ArrayList<AssetRegisterCache>();

		billList = this.find(sql);

		for(AssetRegisterCache billInf : billList){
            CreditCalcuCache crdt = creditList.get(count);
			int size = creditList.size();
			for(int i = count ;i < size;i++  ){
				crdt = creditList.get(count);
				
				if(count == 0 && DateUtils.checkOverLimited(crdt.getStartDate(), billInf.getAssetDelayDueDt())){
					// 资产到期日比第一个额度区间的开始日小，则计算在第一个时间区间内，这种情况发生在资产业务到期后没有解付
                    break;
				}
				
				if(DateUtils.isBetweenTowDay(crdt.getStartDate(), billInf.getAssetDelayDueDt(), crdt.getEndDate())){
					// 到期日在[开始时间，截至时间]之内; 
					break;
				}else{
					if(PoolComm.LOW_RISK.equals(billInf.getRiskType())){//低风险
						crdt.setLowRiskIn(lowTotalAmtIn);//低风险现金流入

					}else if(PoolComm.HIGH_RISK.equals(billInf.getRiskType())){//高风险
						crdt.setHighRiskIn(highTotalAmtIn);//高风险现金流入
					}
					count ++ ;
				}

			}
			if(PoolComm.LOW_RISK.equals(billInf.getRiskType())){//低风险
				lowTotalAmtIn =  lowTotalAmtIn.add(billInf.getAssetAmount());
                crdt.setLowRiskIn(lowTotalAmtIn);//低风险现金流入

			}else if(PoolComm.HIGH_RISK.equals(billInf.getRiskType())){//高风险
				highTotalAmtIn =  highTotalAmtIn.add(billInf.getAssetAmount());
                crdt.setHighRiskIn(highTotalAmtIn);//高风险现金流入
			}
		}
		return creditList;
	}
	/**
	 * 资产出池临时计算：高低风险现金流出
	 * @param creditList
	 * @return
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2021-5-8下午10:12:12
	 */
	public List<CreditCalcuCache> outCreditCalCacheTaskAmtOut(List<CreditCalcuCache> creditList,String bpsNo) throws Exception{

		logger.info("资产出池临时计算：票据池【"+bpsNo+"】额度计算高低风险现金流出数据组装开始....");

		int count = 0;//creditList计数器
		BigDecimal lowTotalAmtOut = new BigDecimal(0);//低风险用信额度总额
		BigDecimal highTotalAmtOut = new BigDecimal(0);//高风险用信额度总额

				
		String sql = "select ra.due_date,ra.occupy_credit,ra.risk_type from" +
				" (select r.due_date,r.occupy_credit,r.risk_type from ped_credit_register r where r.bps_no = '"+bpsNo+"'" +
				"  group by r.due_date,r.occupy_credit,r.risk_type  )  ra  order by ra.due_date ";

		List crdtList = new ArrayList();
		crdtList =  dao.SQLQuery(sql);
		int creditSize = creditList.size();
		int size = crdtList.size();
		
		if(size>0){
			Date dueDt = null;
			BigDecimal crdtAmt = BigDecimal.ZERO;
			String riskLevel = null;

			for(int k=0;k<size;k++){
                CreditCalcuCache crdt = creditList.get(count);
				Object[] obj = (Object[])crdtList.get(k);

				dueDt = (Date) obj[0];
				crdtAmt = new BigDecimal(String.valueOf(obj[1]));
				riskLevel = String.valueOf(obj[2]);

				for(int i = count ;i < creditSize;i++  ){
					crdt = creditList.get(count);
					
					if(count == 0 && DateUtils.checkOverLimited(crdt.getStartDate(), dueDt)){
						// 用信业务到期日比第一个额度区间的开始日小，则计算在第一个时间区间内，这种情况发生在用信业务到期后没有结清的情况
	                    break;
					}
					
					if(DateUtils.isBetweenTowDay(crdt.getStartDate(), dueDt, crdt.getEndDate())){
						// 到期日在[开始时间，截至时间]之内
						break;
					}else{
						if(PoolComm.LOW_RISK.equals(riskLevel)){//低风险
							crdt.setLowRiskOut(lowTotalAmtOut);//低风险现金流出

						}else if(PoolComm.HIGH_RISK.equals(riskLevel)){//高风险
							crdt.setHighRiskOut(highTotalAmtOut);//高风险现金流出
						}
						count ++ ;
					}

				}
				if(PoolComm.LOW_RISK.equals(riskLevel)){//低风险
					lowTotalAmtOut =  lowTotalAmtOut.add(crdtAmt);
                    crdt.setLowRiskOut(lowTotalAmtOut);//低风险现金流出

				}else if(PoolComm.HIGH_RISK.equals(riskLevel)){//高风险
					highTotalAmtOut =  highTotalAmtOut.add(crdtAmt);
                    crdt.setHighRiskOut(highTotalAmtOut);//高风险现金流出
				}
			}
		}
		return creditList;
	}
	
	/**
	 * 根据票据池编号查询资产登记缓存表总金额
	 * @param bpsNo
	 * @param riskType
	 * @param flowNo
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-8-12下午9:17:09
	 */
	
	public BigDecimal querySumAssetCacheAmt(String bpsNo,String riskType,String flowNo) throws Exception{
		
		logger.info("根据票据池编号【"+bpsNo+"】风险类型【"+riskType+"】流水号【"+flowNo+"】查询资产登记缓存表总金额...");
		
		BigDecimal amt = new BigDecimal("0");
		String hql ="";
		List valueList = new ArrayList();
		valueList.add(bpsNo);
		valueList.add(riskType);
		valueList.add(flowNo);
		
		hql = "select sum(a.assetAmount) from AssetRegisterCache a where a.bpsNo = ? and a.riskType = ? and a.flowNo = ? ";
		List list = this.find(hql,valueList);
		if(null!=list && list.size()>0  && null!= list.get(0)){
			amt = (BigDecimal) list.get(0);
		}
		
		return amt;
	}

	@Override
	public void txRefreshFinancial(PedProtocolDto pro, String apId) throws Exception{
		
		logger.info("票据池【"+pro.getPoolAgreement()+"】额度更新...");
		
		try {
			
			if(null == apId){
				AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(pro);
				apId = ap.getApId();
			}
			
			//核心同步保证金并重新计算池额度信息
			this.txBailChangeAndCrdtCalculation(pro);
			
			
			//解锁AssetPool表，并重新计算该表数据
			pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true); 	
			
		} catch (Exception e) {
			pedAssetPoolService.txReleaseAssetPoolLock(apId);//无条件解锁
			logger.error(e.getMessage(),e);
			throw new Exception("额度更新异常："+e.getMessage());
		}
		
		
	}
	
}
