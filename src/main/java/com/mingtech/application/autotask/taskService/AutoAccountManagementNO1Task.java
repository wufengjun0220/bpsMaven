package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftAccountManagement;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.draft.service.MisDraftService;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 
 * @Title: 账务管家落库处理
 * @Description: 日终从bbsp,mis系统统计数据并整理本系统数据存入账务管家表
 * @author wu fengjun
 * @date 2018-12-29
 */
public class AutoAccountManagementNO1Task  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoAccountManagementNO1Task.class);
	
	PoolEcdsService poolEcdsService = PoolCommonServiceFactory.getPoolEcdsService();
	PedProtocolService pedProtocolService =  PoolCommonServiceFactory.getPedProtocolService();
	PoolVtrustService poolVtrustService = PoolCommonServiceFactory.getPoolVtrustService();
	DraftPoolQueryService draftPoolQueryService = PoolCommonServiceFactory.getDraftPoolQueryService();
    MisDraftService misDraftService = PoolCommonServiceFactory.getMisDraftService();
    DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
    BlackListManageService blackListManageService = PoolCommonServiceFactory.getBlackListManageService();
	/**
	 * （1）查询持有票据,质押票据,虚拟票据池录入的应收票据信息(应收)
	 * （2）查询从bbsp系统提示承兑已签收票据,mis系统签发出账票据,虚拟票据池录入应付票据(应付)
	 * （3）落入账务管家表
	 */
	public BooleanAutoTaskResult run() throws Exception {
		
		logger.info("账务管家功能自动任务开始...");
		
		/*
		 * 同步全库所有签约融资票据池的
		 */
		logger.info("账务管家功能,同步全库所有签约融资池的票据");
		draftPoolInService.txQueryAllBillFromBbsp(false);
		
		/*
		 * 向信贷系统进行额度校验，校验额度不足的，不产生额度
		 */
		logger.info("账务管家功能，向信贷系统进行额度校验");
		blackListManageService.txMisCreditCheck(draftPoolInService.queryCheckBills(false));
		
		//清空表数据
        draftPoolQueryService.txDeleteDraftAccountManagement(null, null);
        
    	List pedList = pedProtocolService.queryProtocolInfo(null,PoolComm.VS_01, null, null, null, null);
    	if(pedList!=null && pedList.size()>0){
    		/*
    		 * 获取全部签约基础协议的客户的电票签约账户，组成一个"|"分隔的长字符串
    		 * 记录客户信息与电票签约账户的关系
    		 */
			String eAccs = null; 
			Map accMap = new HashMap();//用于存放电票签约账号和客户的对应关系
			
			if(pedList!=null && pedList.size()>0){
				for(int i=0;i<pedList.size();i++){
					PedProtocolDto pedDto= (PedProtocolDto)pedList.get(i);
					if(PoolComm.NO.equals(pedDto.getIsGroup())){//非集团
						String accNo = pedDto.getElecDraftAccount();
						if(StringUtil.isNotBlank(accNo)){
							if(eAccs==null){								
								eAccs = accNo;
							}else{
								eAccs = eAccs + "|" + accNo;
							}
							String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
							for (int j = 0; j < arr.length; j++) {
								ProtocolQueryBean queryBean = new ProtocolQueryBean();
								queryBean.setCustnumber(pedDto.getCustnumber());
								queryBean.setCustname(pedDto.getCustname());
								
								accMap.put(arr[j], queryBean);
							}
						}
						
					}else{
						ProListQueryBean bean = new ProListQueryBean();
						bean.setBpsNo(pedDto.getPoolAgreement());
						List<String> custIdentityList = new ArrayList<String>();
						custIdentityList.add(PoolComm.KHLX_01);
						custIdentityList.add(PoolComm.KHLX_03);
						bean.setCustIdentityList(custIdentityList);
						List<PedProtocolList> mems = pedProtocolService.queryProListByQueryBean(bean);
						if(mems!=null && mems.size()>0){
							for(int j=0;j<mems.size();j++){
								PedProtocolList mem = mems.get(j);
								String accNo = mem.getElecDraftAccount();
								if(StringUtil.isNotBlank(accNo)){
									if(eAccs==null){								
										eAccs = accNo;
									}else{
										eAccs = eAccs + "|" + accNo;
									}
									String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
									for (int k = 0; k < arr.length; k++) {
										ProtocolQueryBean queryBean = new ProtocolQueryBean();
										queryBean.setCustnumber(mem.getCustNo());
										queryBean.setCustname(mem.getCustName());
										accMap.put(arr[k], queryBean);
									}
								}
								
							}
						}
					}
				}
			}
			
			/*
			 * 电票签约账号去重复数据
			 */
	    	String[] eleAccArr =null;
			List<String> eleAccList  = new ArrayList<String>();
			if(eAccs!=null){
				eleAccArr = eAccs.split("\\|");
			}
			
			if(eleAccArr!=null){
				for(String no : eleAccArr){
					if(!eleAccList.contains(no)){
						eleAccList.add(no);
					}
				}
			}
			/*
			 *去重复之后根据电票签约账户每20个一组调BBSP系统全量票据查询
			 */
			List<String> accNoList = null;
			if(eleAccList!=null && eleAccList.size()>0){
				accNoList = new ArrayList<String>();
				
				String accNos = null;
				int count =0;
				int count1 =0;//总数计数器
				for(String accNo :eleAccList){
					count++;
					count1++;
					if(count<=20){
						if(accNos ==null){
							accNos = accNo;
						}else{							
							accNos = accNos+"|"+accNo;
						}
					}
					
					if(count>=20||(eleAccList.size()==count1)){
						accNoList.add(accNos);
						count=0;
						accNos = null;
					}
				}
				
			}
			
    		logger.info("************JD01客户持有票据落库开始************");
    		this.doProcessJD01();	//JD_01：客户BBSP系统持有票据落库
    		
    		if(accNoList!=null && accNoList.size()>0){
    			for(String accNos : accNoList){
    				logger.info("************JD02客户已质押票据落库开始************");    				
    				this.doProcessJD02(accNos,accMap);	//JD_02：客户已质押票据落库
    				
    				logger.info("************JD04客户BBSP系统提示承兑已签收票据落库开始************"); 
    				this.doProcessJD04(accNos,accMap);	//JD_04：客户BBSP系统提示承兑已签收票据落库   
    				// 备注：客户A作为该票据的承兑人，票据是由其他非银行的客户向A发送提示付款申请，A客户做了提示承兑签收的票据，票据到期后持票人向客户A申请付款，该票据属于客户的应付票据，一定为商票（或者财务公司的银票）
    			}
    		}
    		
    		logger.info("************JD03虚拟票据池录入应收票据落库开始************");
    		this.doProcessJD03();	//JD_03：客户虚拟票据池录入应收票据落库
    		
    		logger.info("************JD05客户MIS系统签发出账票据落库开始************");
    		this.doProcessJD05(pedList);	//JD_05：客户MIS系统签发出账票据落库
    		//备注：客户作为出票人，银行作为承兑人的票据，票据到期后，承兑行向持票人付款，出票客户向承兑行付款，属于客户的应付票据，一定为银票
    		
    		logger.info("************JD06虚拟票据池录入应付票据开始************");
    		this.doProcessJD06();	//JD_06：客户虚拟票据池录入应付票据落库
    		logger.info("************JD06结束************");
    	}
		
    	draftPoolQueryService.txDelectRepeatDraftAccountManagement();
		
		
		return new BooleanAutoTaskResult(true);
	}
	
	/**
	 * 账务管家：JD_01—客户BBSP系统持有票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	public void doProcessJD01(){
		try{
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setSStatusFlag(PoolComm.DS_00);//持有票据
			List<PoolBillInfo> infos =  draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
			//1、客户持有票据
			if(infos!=null && infos.size()>0){
				logger.info("持有票据查询结束,合计票据有["+infos.size()+"]条");
				for (PoolBillInfo info :infos) {
					DraftAccountManagement management ;
					management = new DraftAccountManagement();
					management.setCustNo(info.getCustNo());//客户号(用的是承兑人账号)
					management.setCustName(info.getCustName());//客户名称
					management.setAssetType(PoolComm.QY_01);//资产类型
					management.setTransferPhase("JD_01");//交易阶段
					management.setDraftNb(info.getSBillNo());//票号
					management.setDraftMedia("2");//票据介质
					management.setDraftType(info.getSBillType());//票据类型
					management.setIsseAmt(info.getFBillAmount());//票面金额
					management.setIsseDt(info.getDIssueDt());//出票日
					management.setDueDt(info.getDDueDt());//到期日
					management.setEdBanEndrsmtMk(info.getSBanEndrsmtFlag());//大票表 0-转让  1-不转让
					management.setDrwrNm(info.getSIssuerName());//出票人全称
					management.setDrwrAcctId(info.getSIssuerAccount());//出票人账号
					management.setDrwrAcctSvcr(info.getSIssuerBankCode());//出票人开户行行号
					management.setDrwrAcctSvcrNm(info.getSIssuerBankName());//出票人开户行名称
					management.setPyeeNm(info.getSPayeeName());//收款人全称
					management.setPyeeAcctId(info.getSPayeeAccount());//收款人账号
					management.setPyeeAcctSvcr(info.getSPayeeBankCode());//收款人开户行行号
					management.setPyeeAcctSvcrNm(info.getSPayeeBankName());//收款人开户行名称
					management.setAccptrNm(info.getSAcceptor());//承兑人全称
					management.setAccptrId(info.getSAcceptorAccount());//承兑人账号
					management.setAccptrSvcr(info.getSAcceptorBankCode());//承兑人开户行全称
					management.setAccptrSvcrNm(info.getSAcceptorBankName());//承兑人开户行行名
					management.setTrusteeshipFalg("0");//是否托管
					management.setDraftOwnerSts(PoolComm.QY_01);//票据持有类型

					if(PoolComm.LOW_RISK.equals(info.getRickLevel())||PoolComm.HIGH_RISK.equals(info.getRickLevel())){
						management.setIsEdu("1");//是否产生额度:是
					}else{
						management.setIsEdu("0");//是否产生额度: 否
					}
					management.setRiskLevel(info.getRickLevel());//风险等级
//					management.setRecePayType(PoolComm.QY_01);//票据权益（QY_01:持有票据    QY_02:应付票据）
					management.setDataSource("SRC_01");//数据来源:BBSP
					management.setBillSaveAddr("02");//票据保管地(01:本行   02：自持  03：他行)
					management.setOtherBankSaveAddr("");//他行保管地
					management.setStatusFlag(PoolComm.NO);
					management.setElecDraftAccount(info.getAccNo());//电票签约账号
					
					/********************融合改造新增 start******************************/
					management.setBeginRangeNo(info.getBeginRangeNo());//子票区间起
					management.setEndRangeNo(info.getEndRangeNo());//子票区间止
					management.setDraftSource(info.getDraftSource());//
					management.setSplitFlag(info.getSplitFlag());//
					management.setPlDrwrAcctName(info.getSIssuerAcctName());//出票人账号名称
					management.setPlPyeeAcctName(info.getSPayeeAcctName());// 收款人账号名称
					management.setPlAccptrAcctName(info.getSAcceptorAcctName());//承兑人账号名称
					
					
					/********************融合改造新增 end******************************/
					
					pedProtocolService.txStore(management);
					}
			
				}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 账务管家：JD_02—客户已质押票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	public void doProcessJD02(String  accNos,Map accMap){
		try{
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setSStatusFlag(PoolComm.DS_02);//已入池
			List<PoolBillInfo> infos =  draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
			if(null!=infos&&infos.size()>0){
			logger.info("持有票据查询结束,合计票据有["+infos.size()+"]条");
			for (PoolBillInfo info :infos) {
				DraftAccountManagement management ;
				management = new DraftAccountManagement();
				management.setCustNo(info.getCustNo());//客户号(用的是承兑人账号)
				management.setCustName(info.getCustName());//客户名称
				management.setAssetType(PoolComm.QY_01);//资产类型
				management.setTransferPhase("JD_02");//交易阶段
				management.setDraftNb(info.getSBillNo());//票号
				management.setDraftMedia(info.getSBillMedia());//票据介质
				management.setDraftType(info.getSBillType());//票据类型
				management.setIsseAmt(info.getFBillAmount());//票面金额
				management.setIsseDt(info.getDIssueDt());//出票日
				management.setDueDt(info.getDDueDt());//到期日
				management.setEdBanEndrsmtMk(info.getSBanEndrsmtFlag());
				management.setDrwrNm(info.getSIssuerName());//出票人全称
				management.setDrwrAcctId(info.getSIssuerAccount());//出票人账号
				management.setDrwrAcctSvcr(info.getSIssuerBankCode());//出票人开户行行号
				management.setDrwrAcctSvcrNm(info.getSIssuerBankName());//出票人开户行名称
				management.setPyeeNm(info.getSPayeeName());//收款人全称
				management.setPyeeAcctId(info.getSPayeeAccount());//收款人账号
				management.setPyeeAcctSvcr(info.getSPayeeBankCode());//收款人开户行行号
				management.setPyeeAcctSvcrNm(info.getSPayeeBankName());//收款人开户行名称
				management.setAccptrNm(info.getSAcceptor());//承兑人全称
				management.setAccptrId(info.getSAcceptorAccount());//承兑人账号
				management.setAccptrSvcr(info.getSAcceptorBankCode());//承兑人开户行号
				management.setAccptrSvcrNm(info.getSAcceptorBankName());//承兑人开户行行名
				management.setTrusteeshipFalg("0");//是否托管
				management.setDraftOwnerSts(PoolComm.QY_01);//票据持有类型
				management.setDataSource("SRC_03");//数据来源:SRC_03:票据池系统（融资票据池）
				management.setBillSaveAddr("01");//票据保管地(01:本行   02：自持  03：他行)    
				management.setOtherBankSaveAddr("");//他行保管地
				management.setBpsNo(info.getPoolAgreement());//票据池编号
				PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDto(null,null,info.getPoolAgreement(),null,null,null);
				if(null!=pedProtocolDto){
					management.setBpsName(pedProtocolDto.getPoolName());//票据池名称
				}
				management.setStatusFlag(PoolComm.YES);
				if(PoolComm.LOW_RISK.equals(info.getRickLevel())||PoolComm.HIGH_RISK.equals(info.getRickLevel())){
					management.setIsEdu("1");//是否产生额度:是
				}else{
					management.setIsEdu("0");//是否产生额度: 否
				}
				management.setRiskLevel(info.getRickLevel());//风险等级
				
				/********************融合改造新增 start******************************/
				management.setBeginRangeNo(info.getBeginRangeNo());//子票区间起
				management.setEndRangeNo(info.getEndRangeNo());//子票区间止
				
				management.setDraftSource(info.getDraftSource());//
				management.setSplitFlag(info.getSplitFlag());//
				management.setPlDrwrAcctName(info.getSIssuerAcctName());//出票人账号名称
				management.setPlPyeeAcctName(info.getSPayeeAcctName());// 收款人账号名称
				management.setPlAccptrAcctName(info.getSAcceptorAcctName());//承兑人账号名称
				
				/********************融合改造新增 end******************************/
				
				pedProtocolService.txStore(management);
				}
			}else {
				logger.info("持有票据查询结束,合计票据有["+0+"]条");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * JD02落库处理
	 * @author Ju Nana
	 * @param poolinList
	 * @param queryBean
	 * @throws Exception
	 * @date 2019-6-18下午5:12:02
	 */
	private void doJD02(List poolinList,Map accMap) throws Exception{
		
		
		for (int j = 0; j < poolinList.size(); j++) {
			Map map = (Map) poolinList.get(j);
			String accNo = getStringVal(map.get("accNo"));//电票签约账号
			ProtocolQueryBean queryBean = (ProtocolQueryBean)accMap.get(accNo);
			DraftAccountManagement management = new DraftAccountManagement();
			management.setCustNo(queryBean.getCustnumber());//客户号
			management.setCustName(queryBean.getCustname());//客户名称
			management.setElecDraftAccount(accNo);//电票签约账户
			management.setAssetType(PoolComm.QY_01);//资产类型
			management.setTransferPhase("JD_02");//交易阶段
			management.setDraftNb((String)map.get("billNo"));//票号
			
			PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setSStatusFlag(PoolComm.DS_02);//已入池
			poolQueryBean.setBillNo((String)map.get("billNo"));//票号
			List<PoolBillInfo> poolBillInfos =  draftPoolQueryService.queryPoolBillInfoByPram(poolQueryBean);
			
			if(null!=poolBillInfos&&poolBillInfos.size()>0){
				PoolBillInfo bill = poolBillInfos.get(0);
				management.setBpsNo(bill.getPoolAgreement());//票据池编号
				PedProtocolDto pedProtocolDto = pedProtocolService.queryProtocolDto(null,null,bill.getPoolAgreement(),null,null,null);
				if(null!=pedProtocolDto){
					management.setBpsName(pedProtocolDto.getPoolName());//票据池名称
				}
				management.setStatusFlag(PoolComm.YES);
				String risk = bill.getRickLevel();
				String isEdu = null;
				management.setRiskLevel(risk);
				if(PoolComm.NOTIN_RISK.equals(risk)){
					isEdu =PoolComm.NO;
				}else{
					isEdu =PoolComm.YES;
				}
				management.setIsEdu(isEdu);
				
			}else{
				management.setBpsNo("");//票据池编号
				management.setBpsName("");//票据池名称
				management.setStatusFlag(PoolComm.NO);
				management.setIsEdu(PoolComm.NO);
				management.setRiskLevel("");
			}
			
			management.setDraftMedia("2");//票据介质
			if("1".equals(map.get("billType"))){//银承
				management.setDraftType(PoolComm.BILL_TYPE_BANK);//票据类型-银承
			}else{
				management.setDraftType(PoolComm.BILL_TYPE_BUSI);//票据类型-商承
			}
			management.setIsseAmt(new BigDecimal((String)map.get("billMoney")));//票面金额
			String issDate = (String)map.get("acptDt");//出票日
			String dueDate = (String)map.get("dueDt");//到期日
			management.setIsseDt(DateUtils.parse(issDate, DateUtils.ORA_DATE_FORMAT));//出票日
			management.setDueDt(DateUtils.parse(dueDate, DateUtils.ORA_DATE_FORMAT));//到期日

			management.setEdBanEndrsmtMk((String)map.get("forbidFlag"));//BSP数据 0-转让  1-不转让

			if(map.get("remitter")!=null){
				management.setDrwrNm((String)map.get("remitter"));//出票人全称
			}
			if(map.get("remitterAcctNo")!=null){
				management.setDrwrAcctId((String)map.get("remitterAcctNo"));//出票人账号
			}
			if(map.get("remitterBankNo")!=null){
				management.setDrwrAcctSvcr((String)map.get("remitterBankNo"));//出票人开户行行号
			}
			if(map.get("remitterBankName")!=null){
				management.setDrwrAcctSvcrNm((String)map.get("remitterBankName"));//出票人开户行名称
			}
			if(map.get("payee")!=null){
				management.setPyeeNm((String)map.get("payee"));//收款人全称
			}
			if(map.get("payeeAcctNo")!=null){
				management.setPyeeAcctId((String)map.get("payeeAcctNo"));//收款人账号
			}
			if(map.get("payeeBankName")!=null){
				management.setPyeeAcctSvcrNm((String)map.get("payeeBankName"));//收款人开户行名称
			}
			if(map.get("payeeBankNo")!=null){
				management.setPyeeAcctSvcr((String)map.get("payeeBankNo"));//收款人开户行行号
			}
			if(map.get("acceptor")!=null){
				management.setAccptrNm((String)map.get("acceptor"));//承兑人全称
			}
			if(map.get("acceptorAcctNo")!=null){
				management.setAccptrId((String)map.get("acceptorAcctNo"));//承兑人账号
			}
			if(map.get("acceptorBankNo")!=null){
				management.setAccptrSvcr((String)map.get("acceptorBankNo"));//承兑人开户行行号
			}
			if(map.get("acceptorBankName")!=null){
				management.setAccptrSvcrNm((String)map.get("acceptorBankName"));//承兑人开户行全称
			}
			management.setTrusteeshipFalg((String)map.get("0"));//是都托管
			
			management.setDraftOwnerSts((String)map.get(PoolComm.QY_01));//票据持有类型					
			management.setDataSource("SRC_01");//数据来源:BBSP
			management.setBillSaveAddr("01");//票据保管地(01:本行   02：自持  03：他行)
			management.setOtherBankSaveAddr("");//他行保管地
			pedProtocolService.txStore(management);
		}
		
	}
	/**
	 * 账务管家：JD_03—客户虚拟票据池录入应收票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	public void doProcessJD03(){
		try{
			//3、虚拟票据池录入应收票据
			logger.info("虚拟票据池录入应收票据开始");
			//List list = poolVtrustService.queryPoolVtrust(null,null, null, null, PoolComm.QY_01);
			PoolVtrustBeanQuery queryBean = new PoolVtrustBeanQuery();
			queryBean.setPayType(PoolComm.QY_01);
			queryBean.setVtStatus(PoolComm.DS_00);
			List<PoolVtrust> list = poolVtrustService.queryPoolVtrustList(queryBean);
			
			if(list != null && list.size() >0 ){
				logger.info("虚拟票据池录入应收票据结束,可落库的数据有["+list.size()+"]条");
				for (int i = 0; i < list.size(); i++) {
					PoolVtrust pool = (PoolVtrust) list.get(i);
					DraftAccountManagement management = new DraftAccountManagement();
					management.setCustNo(pool.getVtEntpNo());//客户号
					management.setCustName(pool.getVtEntpName());//客户名称
					management.setAssetType(pool.getPayType());//资产类型
					management.setTransferPhase("JD_03");//交易阶段
					management.setDraftNb(pool.getVtNb());//票号
					management.setDraftMedia(pool.getVtDraftMedia());//票据介质
					management.setDraftType(pool.getVtType());//票据类型
					management.setIsseAmt(pool.getVtisseAmt());//票面金额
					management.setIsseDt(pool.getVtisseDt());//出票日
					management.setDueDt(pool.getVtdueDt());//到期日
//					management.setEdBanEndrsmtMk(pool.getVtTranSfer());//能否转让标记
					if(pool.getVtTranSfer().equals("1")){
						management.setEdBanEndrsmtMk("0");//可转让
					}else {
						management.setEdBanEndrsmtMk("1");//不可转让
					}

					management.setDrwrNm(pool.getVtdrwrName());//出票人全称
					management.setDrwrAcctId(pool.getVtdrwrAccount());//出票人账号
					management.setDrwrAcctSvcr(pool.getVtdrwrBankNumber());//出票人开户行行号
					management.setDrwrAcctSvcrNm(pool.getVtdrwrBankName());//出票人开户行名称
					management.setAccptrNm(pool.getVtaccptrName());//收款人全称
					management.setPyeeAcctId(pool.getVtpyeeAccount());//收款人账号
					management.setPyeeAcctSvcr(pool.getVtpyeeBankAccount());//收款人开户行行号
					management.setPyeeAcctSvcrNm(pool.getVtpyeeBankName());//收款人开户行名称
					management.setAccptrId(pool.getVtaccptrAccount());//承兑人账号
					management.setAccptrSvcr(pool.getVtaccptrBankAccount());//承兑人开户行行号
					management.setAccptrSvcrNm(pool.getVtaccptrBankName());//承兑人开户行全称
					management.setTrusteeshipFalg(pool.getVtLogo());//是否托管(1:已托管  2：未托管)
					management.setDraftOwnerSts(pool.getPayType());//票据持有类型
					management.setIsEdu("");//是否产生额度:虚拟票据池均不产生额度
					management.setRiskLevel("");//风险等级：虚拟票据池无风险等级
//					management.setRecePayType(pool.getPayType());//票据权益（QY_01:持有票据    QY_02:应付票据）
					management.setDataSource("SRC_04");//数据来源:虚拟票据池录入
					management.setBillSaveAddr(pool.getBillPosition());//票据保管地(01:本行   02：自持  03：他行)
					management.setOtherBankSaveAddr(pool.getBillPositionAddr());//他行保管地
					management.setPyeeNm(pool.getVtpyeeName());
					management.setContractNo(pool.getContractNo());//交易合同号
					management.setAcceptanceAgreeNo(pool.getAcceptanceAgreeNo());//承兑协议编号
					management.setBpsNo(pool.getBpsNo());//票据池编号
					management.setBpsName(pool.getBpsName());//票据池名称 
					management.setStatusFlag(PoolComm.YES);
					
					/********************融合改造新增 start******************************/
					management.setBeginRangeNo(pool.getBeginRangeNo());//子票区间起
					management.setEndRangeNo(pool.getEndRangeNo());//子票区间止
					
					management.setDraftSource(pool.getDraftSource());//
					management.setSplitFlag(pool.getSplitFlag());//
					management.setPlDrwrAcctName(pool.getPlDrwrAcctName());//出票人账号名称
					management.setPlPyeeAcctName(pool.getPlPyeeAcctName());// 收款人账号名称
					management.setPlAccptrAcctName(pool.getPlAccptrAcctName());//承兑人账号名称
					/********************融合改造新增 end******************************/
										
					pedProtocolService.txStore(management);
				}
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 账务管家：JD_04—客户BBSP系统提示承兑已签收票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	public void doProcessJD04(String  accNos,Map accMap){
		try{
			//4、:BBSP系统提示承兑已签收票据
			List draftList = new ArrayList();//承兑已签收票据信息列表
			if(accNos!=null){
				ECDSPoolTransNotes poolTrans =new ECDSPoolTransNotes();
				poolTrans.setStatus("TE200202_02");//客户承兑已签收
				poolTrans.setAcctNo(accNos);
				
				ReturnMessageNew response = poolEcdsService.txApplyFullBill(poolTrans);
				draftList = response.getDetails();
				if (response.isTxSuccess()) {
					if(draftList!=null && draftList.size()>0){
						this.doJD04(draftList, accMap);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

		
	}
	
	/**
	 * PD04落库处理
	 * @Description TODO
	 * @author Ju Nana
	 * @param draftList
	 * @param queryBean
	 * @throws Exception
	 * @date 2019-6-18下午7:50:54
	 */
	private void doJD04(List draftList,Map accMap) throws Exception{

		for (int j = 0; j < draftList.size(); j++) {
			Map map = (Map) draftList.get(j);
			String accNo = getStringVal(map.get("accNo"));//电票签约账号
			ProtocolQueryBean queryBean = (ProtocolQueryBean)accMap.get(accNo);
			DraftAccountManagement management = new DraftAccountManagement();
			management.setCustNo(queryBean.getCustnumber());//客户号
			management.setCustName(queryBean.getCustname());//客户名称
			
			/********************融合改造新增 start******************************/
			management.setBeginRangeNo((String)map.get("startBillNo"));//子票区间起
			management.setEndRangeNo((String)map.get("endBillNo"));//子票区间止
			
			management.setDraftSource((String)map.get("billSource"));//
//			management.setSplitFlag(pool.getSplitFlag());//
//			management.setPlDrwrAcctName(pool.getPlDrwrAcctName());//出票人账号名称
//			management.setPlPyeeAcctName(pool.getPlPyeeAcctName());// 收款人账号名称
//			management.setPlAccptrAcctName(pool.getPlAccptrAcctName());//承兑人账号名称
			
			/********************融合改造新增 end******************************/
			
			
			management.setElecDraftAccount(accNo);//电票签约账号
			management.setAssetType(PoolComm.QY_02);//资产类型
			management.setTransferPhase("JD_04");//交易阶段
			management.setDraftNb((String)map.get("billNo"));//票号
			management.setDraftMedia("2");//票据介质
			if("1".equals(map.get("billType"))){//银承
				management.setDraftType(PoolComm.BILL_TYPE_BANK);//票据类型-银承
			}else{
				management.setDraftType(PoolComm.BILL_TYPE_BUSI);//票据类型-商承
			}
			management.setIsseAmt(new BigDecimal((String)map.get("billMoney")));//票面金额
			String issDate = (String)map.get("acptDt");//出票日
			String dueDate = (String)map.get("dueDt");//到期日
			management.setIsseDt(DateUtils.parse(issDate, DateUtils.ORA_DATE_FORMAT));//出票日
			management.setDueDt(DateUtils.parse(dueDate, DateUtils.ORA_DATE_FORMAT));//到期日
			management.setEdBanEndrsmtMk((String)map.get("forbidFlag"));//能否转让标记   BBSP数据 0-转让  1-不转让
			if(map.get("remitter")!=null){
				management.setDrwrNm((String)map.get("remitter"));//出票人全称
			}
			if(map.get("remitterAcctNo")!=null){
				management.setDrwrAcctId((String)map.get("remitterAcctNo"));//出票人账号
			}
			if(map.get("remitterBankNo")!=null){
				management.setDrwrAcctSvcr((String)map.get("remitterBankNo"));//出票人开户行行号
			}
			if(map.get("remitterBankName")!=null){
				management.setDrwrAcctSvcrNm((String)map.get("remitterBankName"));//出票人开户行名称
			}
			if(map.get("payee")!=null){
				management.setPyeeNm((String)map.get("payee"));//收款人全称
			}
			if(map.get("payeeAcctNo")!=null){
				management.setPyeeAcctId((String)map.get("payeeAcctNo"));//收款人账号
			}
			if(map.get("payeeBankNo")!=null){
				management.setPyeeAcctSvcr((String)map.get("payeeBankNo"));//收款人开户行行号
			}
			if(map.get("payeeBankName")!=null){
				management.setPyeeAcctSvcrNm((String)map.get("payeeBankName"));//收款人开户行名称
			}
			if(map.get("acceptor")!=null){
				management.setAccptrNm((String)map.get("acceptor"));//承兑人全称
			}
			if(map.get("acceptorAcctNo")!=null){
				management.setAccptrId((String)map.get("acceptorAcctNo"));//承兑人账号
			}
			if(map.get("acceptorBankNo")!=null){
				management.setAccptrSvcr((String)map.get("acceptorBankNo"));//承兑人开户行号
			}
			if(map.get("acceptorBankName")!=null){
				management.setAccptrSvcrNm((String)map.get("acceptorBankName"));//承兑人开户行行名
			}
			management.setTrusteeshipFalg((String)map.get("0"));//是都托管
			management.setDraftOwnerSts(PoolComm.QY_02);//票据持有类型
			
			management.setIsEdu("");//是否产生额度:提示承兑已签收状态不校验额度
			management.setRiskLevel("");//风险等级：提示承兑票据不校验风险
			management.setDataSource("SRC_01");//数据来源:BBSP系统
			management.setBillSaveAddr("02");//票据保管地(01:本行   02：自持  03：他行)
			management.setOtherBankSaveAddr("");//他行保管地
			management.setStatusFlag(PoolComm.NO);
			pedProtocolService.txStore(management);
			
		}
	
	}
	/**
	 * 账务管家：JD_05—客户MIS系统签发出账票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	public void doProcessJD05(List  pedList){
		
		try {
			List<String> custNos = new ArrayList<String>();//已签约的客户号
			Map<String,String> custToBps = new HashMap();
			if(pedList!=null && pedList.size()>0){
				for(int j = 0;j<pedList.size();j++){
					PedProtocolDto pro = (PedProtocolDto)pedList.get(j);
					if(PoolComm.YES.equals(pro.getIsGroup())){//集团户
						ProListQueryBean bean = new ProListQueryBean();
						bean.setBpsNo(pro.getPoolAgreement());
						List<String> custIdentityList = new ArrayList<String>();
						custIdentityList.add(PoolComm.KHLX_01);
						custIdentityList.add(PoolComm.KHLX_03);
						bean.setCustIdentityList(custIdentityList);
						List<PedProtocolList> mems = pedProtocolService.queryProListByQueryBean(bean);
						if(mems!=null && mems.size()>0){
							for(int k=0;k<mems.size();k++){
								PedProtocolList mem = mems.get(k);
								custNos.add(mem.getCustNo());
							}
						}
						
					}else{
						custNos.add(pro.getCustnumber());
					}
				}
			}
			
			
			if(custNos!=null&&custNos.size()>0){
				/*
				 * custNo去重复
				 */
				for(int i=0;i<custNos.size()-1;i++){
					for(int j=custNos.size()-1;j>i;j--){
						if(custNos.get(i).equals(custNos.get(j))){
							custNos.remove(custNos.get(j));
						}
					}
				}
				
				
				try {
					//MIS签发已记账的电票落账务管家表
					List eDraftList = misDraftService.queryMisEdraftByParam(custNos);
					this.txJD05DoProsess(eDraftList, "e");//e:电票
					
					//MIS签发已记账的纸票入账务管家表
					List pDraftList = misDraftService.queryMisPdraftByParam(custNos);
					this.txJD05DoProsess(pDraftList, "p");//p:纸票
					
				} catch (Exception e) {
					logger.info("账务管家mis签发已记账数据落库异常");
				}
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * mis纸票电票数据整理
	 * @param list
	 * @param type
	 * @throws Exception 
	 */
	private void txJD05DoProsess(List list,String type) throws Exception{
		List<DraftAccountManagement> bills = new ArrayList();
		List<String> elecBills = new ArrayList<String>();
		List<String> papeBills = new ArrayList<String>();
		if(list!=null&&list.size()>0){
			for (int j = 0; j < list.size(); j++) {
				DraftAccountManagement draft  = new DraftAccountManagement();
				Object[] obj = (Object[]) list.get(j);
				if (obj[0] != null) {
					if(obj[0]!=null){
						draft.setCustNo(obj[0].toString());
					}
					if(obj[1]!=null){
						draft.setCustName(obj[1].toString());
					}
					
					draft.setAssetType(PoolComm.QY_02);//资产类型 :mis提示承兑已签收的均为应付类型
					draft.setTransferPhase(PoolComm.JD_05);
					
					if(obj[4]!=null){
						draft.setDraftNb(obj[4].toString());
					}
					
					if(obj[5]!=null){
						draft.setDraftMedia(obj[5].toString());
					}
					if(obj[6]!=null){
						draft.setDraftType("AC01");//MIS均为银票
					}
					if(obj[7]!=null){
						draft.setIsseAmt(new BigDecimal(obj[7].toString()));//BigDecimal
					}
					if(obj[8]!=null){
						draft.setIsseDt((Date)obj[8]);
					}
					
					if(obj[9]!=null){
						draft.setDueDt((Date)obj[9]);
					}
					
					if(obj[10]!=null){
						draft.setEdBanEndrsmtMk(obj[10].toString());
					}
					if(obj[11]!=null){
						draft.setDrwrNm(obj[11].toString());
					}
					if(obj[12]!=null){
						draft.setDrwrAcctId(obj[12].toString());
					}
					if(obj[13]!=null){
						draft.setDrwrAcctSvcr(obj[13].toString());
					}
					if(obj[14]!=null){
						draft.setDrwrAcctSvcrNm(obj[14].toString());
					}
					if(obj[15]!=null){
						draft.setPyeeNm(obj[15].toString());
					}
					if(obj[16]!=null){
						draft.setPyeeAcctId(obj[16].toString());
					}
					if(obj[17]!=null){
						draft.setPyeeAcctSvcr(obj[17].toString());
					}
					if(obj[18]!=null){
						draft.setPyeeAcctSvcrNm(obj[18].toString());
					}
					if(obj[19]!=null){
						draft.setAccptrNm(obj[19].toString());
					}
					if(obj[20]!=null){
						draft.setAccptrId(obj[20].toString());
					}
					if(obj[21]!=null){
						draft.setAccptrSvcr(obj[21].toString());
					}
					if(obj[22]!=null){
						draft.setAccptrSvcrNm(obj[22].toString());
					}
					if(obj[23]!=null){
						draft.setTrusteeshipFalg(obj[23].toString());
					}
					if(obj[24]!=null){
						draft.setDraftOwnerSts(obj[24].toString());
					}
					draft.setIsEdu("");//是否产生额度
					draft.setRiskLevel("");//风险等级：虚拟票据池无风险等级
//					draft.setRecePayType(PoolComm.QY_02);//票据权益（QY_01:持有票据    QY_02:应付票据）
					draft.setDataSource("SRC_02");//数据来源:MIS系统
					draft.setBillSaveAddr("02");//票据保管地(01:本行   02：自持  03：他行)
					draft.setOtherBankSaveAddr("");//他行保管地
					draft.setStatusFlag(PoolComm.NO);
					bills.add(draft);
					if("e".equals(type)){
						elecBills.add(draft.getDraftNb());
					}else{
						papeBills.add(draft.getDraftNb());
					}
			}
		}
		
		}
		if(elecBills.size()>0){//需要更新的电票
			List<String> eUptateBillNos = new ArrayList<String>();//最终要更新的票号in 字符串
			String nos = "(";
			int count =0;
			for(String no :elecBills){
				count++;
				if(count<=30){
					nos = nos+"'"+no+"',";
				}
				
				if(count>=30||(elecBills.size()==count)){
					nos = nos.subSequence(0, nos.length()-1) +")";
					eUptateBillNos.add(nos);
					 nos = "(";
					count=0;
				}
			}
			misDraftService.txUpdateMisEdraft(eUptateBillNos);
		}
		if(papeBills.size()>0){//需要更新的纸票
			List<String> pUptateBillNos = new ArrayList<String>();//最终要更新的票号in 字符串
			String nos = "(";
			int count =0;
			for(String no :papeBills){
				count++;
				if(count<=30){
					nos = nos+"'"+no+"',";
				}
				
				if(count>=30||(papeBills.size()==count)){
					nos = nos.subSequence(0, nos.length()-1) +")";
					pUptateBillNos.add(nos);
					 nos = "(";
					count=0;
				}
			}
			misDraftService.txUpdateMisPdraft(pUptateBillNos);
		}
		misDraftService.txStoreAll(bills);
	}
	/**
	 * 账务管家：JD_06—客户虚拟票据池录入应付票据落库
	 * @param pedList   已签约票据池的客户信息
	 * @author Ju Nana
	 * @date 2019-1-16 下午5:07:59
	 */
	public void doProcessJD06()throws Exception{
		//6、虚拟票据池录入应付票据
		try {
			logger.info("虚拟票据池录入应付票据开始");
			PoolVtrustBeanQuery queryBean = new PoolVtrustBeanQuery();
			queryBean.setPayType(PoolComm.QY_02);
			queryBean.setVtStatus(PoolComm.DS_00);
			List<PoolVtrust> list = poolVtrustService.queryPoolVtrustList(queryBean);
			if(list != null && list.size() > 0 ){
				logger.info("虚拟票据池录入应付票据结束,可落库数据有["+list.size()+"]条");
				for (int i = 0; i < list.size(); i++) {
					PoolVtrust pool = (PoolVtrust) list.get(i);
					DraftAccountManagement management = new DraftAccountManagement();
					management.setCustNo(pool.getVtEntpNo());//客户号
					management.setCustName(pool.getVtEntpName());//客户名称
					management.setAssetType(pool.getPayType());//资产类型
					management.setTransferPhase("JD_06");//交易阶段
					management.setDraftNb(pool.getVtNb());//票号
					management.setDraftMedia(pool.getVtDraftMedia());//票据介质
					management.setDraftType(pool.getVtType());//票据类型
					management.setIsseAmt(pool.getVtisseAmt());//票面金额
					management.setIsseDt(pool.getVtisseDt());//出票日
					management.setDueDt(pool.getVtdueDt());//到期日
					if(pool.getVtTranSfer().equals("1")){
						management.setEdBanEndrsmtMk("0");//可转让
					}else {
						management.setEdBanEndrsmtMk("1");//不可转让
					}
//					management.setEdBanEndrsmtMk(pool.getVtTranSfer());//能否转让标记
					management.setDrwrNm(pool.getVtdrwrName());//出票人全称
					management.setDrwrAcctId(pool.getVtdrwrAccount());//出票人账号
					management.setDrwrAcctSvcr(pool.getVtdrwrBankNumber());//出票人开户行行号
					management.setDrwrAcctSvcrNm(pool.getVtdrwrBankName());//出票人开户行名称
					management.setPyeeNm(pool.getVtaccptrName());//收款人全称
					management.setPyeeAcctId(pool.getVtaccptrAccount());//收款人账号
					management.setPyeeAcctSvcr(pool.getVtpyeeBankAccount());//收款人开户行行号
					management.setPyeeAcctSvcrNm(pool.getVtpyeeBankName());//收款人开户行名称
					management.setAccptrNm(pool.getVtpyeeName());//承兑人全称
					management.setAccptrId(pool.getVtpyeeAccount());//承兑人账号
					management.setAccptrSvcr(pool.getVtaccptrBankAccount());//承兑人开户行全称
					management.setAccptrSvcrNm(pool.getVtaccptrBankName());//承兑人开户行行名
					management.setTrusteeshipFalg(pool.getVtLogo());//是都托管
					management.setDraftOwnerSts("");//票据持有类型
					management.setIsEdu("");//是否产生额度:虚拟票据池均不产生额度
					management.setRiskLevel("");//风险等级：虚拟票据池无风险等级
//					management.setRecePayType(pool.getPayType());//票据权益（QY_01:持有票据    QY_02:应付票据）
					management.setDataSource("SRC_04");//数据来源:虚拟票据池录入
					management.setBillSaveAddr(pool.getBillPosition());//票据保管地(01:本行   02：自持  03：他行)
					management.setOtherBankSaveAddr(pool.getBillPositionAddr());//他行保管地
					management.setContractNo(pool.getContractNo());//交易合同号
					management.setAcceptanceAgreeNo(pool.getAcceptanceAgreeNo());//承兑协议编号
					management.setBpsNo(pool.getBpsNo());//票据池编号
					management.setBpsName(pool.getBpsName());//票据池名称
					management.setStatusFlag(PoolComm.YES);
					pedProtocolService.txStore(management);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}


	
}
