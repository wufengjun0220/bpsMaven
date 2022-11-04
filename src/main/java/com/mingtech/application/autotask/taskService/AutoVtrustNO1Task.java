package com.mingtech.application.autotask.taskService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.domain.DraftPoolBase;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 
 * @Title: 虚拟票据池自动入池
 * @Description: 查询票据系统进行虚拟票据池自动入池任务
 * @author Ju Nana
 * @date 2018-11-8
 */
public class AutoVtrustNO1Task  extends AbstractAutoTask {
	BlackListManageService blackListManageService  = PoolCommonServiceFactory.getBlackListManageService();
	
	private static final Logger logger = Logger
	.getLogger(AutoVtrustNO1Task.class);
	
	/**
	 * （1）删除票据池中来源为BBSP系统的已签约虚拟票据池的签约信息
	 * （2）查询出已签约虚拟票据池的客户号
	 * （3）用本客户名下已签约的账号调用票据系统客户持有票据查询接口
	 * （4）将返回数据存入pl_vtrust表
	 */
	public BooleanAutoTaskResult run() throws Exception {
		
		PoolEcdsService poolEcdsService = PoolCommonServiceFactory.getPoolEcdsService();
		PedProtocolService pedProtocolService =  PoolCommonServiceFactory.getPedProtocolService();
		PoolVtrustService poolVtrustService = PoolCommonServiceFactory.getPoolVtrustService();
		
		/*
		 * 删除BBSP来源的票据信息
		 */
		poolVtrustService.deleteVtrustInfoBySource(PoolComm.SOUR_BBSP);
		/*
		 * 基础签约已签约，融资未签约的
		 */
		ProtocolQueryBean queryBean = new ProtocolQueryBean();
		queryBean.setOpenFlag(PoolComm.OPEN_00);
		queryBean.setvStatus(PoolComm.VS_01);
		queryBean.setIsGroup(PoolComm.NO);
		/*
		 * 单户票据池
		 */
		List<PedProtocolDto> custList =  pedProtocolService.queryProtocolDtoListByQueryBean(queryBean);
		
		queryBean.setIsGroup(PoolComm.YES);
		/*
		 * 集团票据池
		 */
		List<PedProtocolDto> custListGroup =pedProtocolService.queryProtocolDtoListByQueryBean(queryBean);
						
		String accStr = null;//拼接电票签约账号
		Map accMap = new HashMap();//用于存放电票签约账号和协议的对应关系
		
		/*
		 * 单户电票签约账户集成
		 */
		if(custList != null && custList.size() > 0 ){
			PedProtocolDto dto = null;
			for(int i =0 ;i<custList.size();i++){
				dto = (PedProtocolDto)custList.get(i);
				String accNo = dto.getElecDraftAccount();//电票签约账号
				if(accNo != null && !"".equals(accNo)){
					String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
					for (int j = 0; j < arr.length; j++) {
						ProtocolQueryBean bean = new ProtocolQueryBean();
						bean.setCustnumber(dto.getCustnumber());
						bean.setCustname(dto.getCustname());
						accMap.put(arr[j], bean);
					}
					if(accStr == null){
						accStr = accNo;
					}else{						
						accStr = accStr + "|" + accNo;
					}
				}
			}
		}
		
		/*
		 * 集团的电票签约账户集成
		 */
		if(custListGroup != null && custListGroup.size() > 0 ){
			for(int i =0 ;i<custListGroup.size();i++){
				PedProtocolDto dto = (PedProtocolDto)custListGroup.get(i);
				ProListQueryBean bean = new ProListQueryBean();
				bean.setBpsNo(dto.getPoolAgreement());
				List<String> custIdentityList = new ArrayList<String>();
				custIdentityList.add(PoolComm.KHLX_01);
				custIdentityList.add(PoolComm.KHLX_03);
				bean.setCustIdentityList(custIdentityList);
				List<PedProtocolList> mems = pedProtocolService.queryProListByQueryBean(bean);
				if(mems!=null && mems.size()>0){
					for(int j=0;j<mems.size();j++){
						PedProtocolList mem = mems.get(j);
						String accNo = mem.getElecDraftAccount();//电票签约账号
						if(accNo != null && !"".equals(accNo)){
							String[] arr = accNo.split("\\|");//按|分割多个签约账号的情况,为了每个账号对应协议
							for (int k = 0; k < arr.length; k++) {
								ProtocolQueryBean bean1 = new ProtocolQueryBean();
								bean1.setCustnumber(mem.getCustNo());
								bean1.setCustname(mem.getCustName());
								accMap.put(arr[k], bean1);
							}
							if(accStr == null){
								accStr = accNo;
							}else{						
								accStr = accStr + "|" + accNo;
							}
							accStr = accStr + "|" + accNo;
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
		if(accStr!=null){
			eleAccArr = accStr.split("\\|");
		}
		
		if(eleAccArr!=null){
			for(String no : eleAccArr){
				if(!eleAccList.contains(no)){
					eleAccList.add(no);
				}
			}
		}
		logger.info("所有电票签约账号："+eleAccList.toString());
		/*
		 *根据电票签约账户每20个一组调BBSP系统全量票据查询
		 */
		List<String> accNoList = null;
		if(eleAccList!=null && eleAccList.size()>0){
			accNoList = new ArrayList<String>();
			
			String accNos = null;
			int count =0;//单数计算器
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
		
		/*
		 * 调用BBSP系统查回持有票据
		 */
		if(accNoList!=null && accNoList.size()>0){
			for(String accNos : accNoList){
				accNos = accNos.substring(0, accNos.length());
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());//设置起时间		    
				cal.add(Calendar.DATE, 1);//加一天
				Date dueDt = cal.getTime();//到期日当天的票据不查
				
				//调用持有票据查询接口
				ECDSPoolTransNotes transNotes = new ECDSPoolTransNotes();
				transNotes.setApplicantAcctNo(accNos);
				transNotes.setMinDueDt(DateUtils.toString(dueDt, "yyyyMMdd"));
				/*
				 * 持有票查询
				 */
				ReturnMessageNew response = poolEcdsService.txApplyPossessBill(transNotes);
				
				List list = response.getDetails();
				if(list!=null && list.size()>0){
					
					logger.info("根据电票签约账号查询持有票据结束,持有票据笔数:"+list.size());
					ProtocolQueryBean bean2 = null ;
					for (int i = 0; i < list.size(); i++) {
						//根据电票签约账号先与协议先对应
						Map map =(Map) list.get(i);
						logger.info("虚拟票据池赋值开始");
						PoolVtrust vtrust =dataProcess(map);
						logger.info("虚拟票据池赋值结束");
						bean2 = (ProtocolQueryBean) accMap.get(getStringVal(map.get("accNo")));//根据电票签约账号获取协议信息，将客户信息存入票中
						
						vtrust = this.vtrustToPool(vtrust);
						vtrust.setVtEntpNo(bean2.getCustnumber());//核心客户号
						vtrust.setVtEntpName(bean2.getCustname());//客户名称
						poolVtrustService.txStore(vtrust);
					}
				}
				}
		}
		
		return new BooleanAutoTaskResult(true);
	}
	private  PoolVtrust dataProcess(Map map) throws ParseException{
		PoolVtrust vtrust = new PoolVtrust();
		DateFormat format =new SimpleDateFormat("yyyyMMdd");
		try {
			vtrust.setVtNb(getStringVal(map.get("BILL_NO")));
			if("1".equals(getStringVal(map.get("BILL_TYPE")))){//票据类型-银票
				vtrust.setVtType(PoolComm.BILL_TYPE_BANK);
			}else if("2".equals(getStringVal(map.get("BILL_TYPE")))){//票据类型-商票
				vtrust.setVtType(PoolComm.BILL_TYPE_BUSI);	
			}
			String acptDt = getStringVal(map.get("DRAW_BILL_DATE"));
			vtrust.setVtisseDt(format.parse(acptDt));
			String dueDt = getStringVal(map.get("REMIT_EXPIPY_DATE"));
			vtrust.setVtdueDt(format.parse(dueDt));
			vtrust.setVtdrwrName(getStringVal(map.get("DRAWER_NAME")));
			vtrust.setVtdrwrAccount(getStringVal(map.get("DRAWER_ACCT_NO")));
			vtrust.setVtdrwrBankNumber(getStringVal(map.get("DRAWER_OPEN_BANK_NO")));
			vtrust.setVtdrwrBankName(getStringVal(map.get("DRAWER_OPEN_BANK_NAME")));
			vtrust.setVtpyeeName(getStringVal(map.get("PAYEE_NAME")));
			vtrust.setVtpyeeAccount(getStringVal(map.get("PAYEE_ACCT_NO")));
			vtrust.setVtpyeeBankAccount(getStringVal(map.get("PAYEE_OPEN_BANK_NO")));
			vtrust.setVtpyeeBankName(getStringVal(map.get("PAYEE_OPEN_BANK_NAME")));
			vtrust.setVtaccptrAccount(getStringVal(map.get("ACCEPTOP_ACCT_NO")));
			vtrust.setVtaccptrBankAccount(getStringVal(map.get("ACCEPTOP_OPEN_BANK_NO")));
			
			 //承兑行总行
			String acptBankNo = vtrust.getVtaccptrBankAccount();
			Map cpes = blackListManageService.queryCpesMember(acptBankNo);
			if(cpes != null){
				vtrust.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
				String memberName = (String) cpes.get("memberName");//总行行名
				vtrust.setAcptHeadBankName(memberName);//总行行名
			}
			 
			vtrust.setVtaccptrBankName(getStringVal(map.get("ACCEPTOP_OPEN_BANK_NAME")));
			vtrust.setVtaccptrName(getStringVal(map.get("ACCEPTOP_NAME")));
			vtrust.setVtisseAmt(getBigDecimalVal(map.get("BILL_AMT")));
			vtrust.setVtTranSfer(getStringVal(map.get("UNENDORSE_FLAG")));//不得转让标记
			vtrust.setVtDraftMedia(PoolComm.BILL_MEDIA_ELECTRONICAL);
			vtrust.setPayType(PoolComm.QY_01);
			vtrust.setVtStatus(PoolComm.DS_00);
			vtrust.setPlTm(new Date());
			vtrust.setLastOperTm(new Date());
			vtrust.setLastOperName("虚拟票据池自动入池落库");
			vtrust.setVtSource(PoolComm.SOUR_BBSP);
			
			if(getStringVal(map.get("BILL_SOURCE")).equals(PoolComm.CS01) ){
				vtrust.setBeginRangeNo("0");//子票起始号
				vtrust.setEndRangeNo("0");//子票截止
			}else{
				vtrust.setBeginRangeNo(getStringVal(map.get("START_BILL_NO")));//子票起始号
				vtrust.setEndRangeNo(getStringVal(map.get("END_BILL_NO")));//子票截止
			}
			vtrust.setStandardAmt(getBigDecimalVal(map.get("STANDARO_AMTSTANDARO_AMT")));//标准金额
			vtrust.setTradeAmt(getBigDecimalVal(map.get("BILL_AMT")));//交易金额(等分化票据实际交易金额)
			vtrust.setDraftSource(getStringVal(map.get("BILL_SOURCE")));//票据来源
			vtrust.setSplitFlag(getStringVal(map.get("IS_SPLIT")));//是否允许拆分标记 1是 0否
			vtrust.setPlDrwrAcctName(getStringVal(map.get("DRAWER_ACCT_NAME")));//出票人账户名称
			vtrust.setPlPyeeAcctName(getStringVal(map.get("PAYEE_ACCT_NAME")));//收款人账户名称
			vtrust.setPlAccptrAcctName(getStringVal(map.get("ACCEPTOR_ACCT_NAME")));//承兑人账户名称
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return vtrust;
		
	}
	
	private PoolVtrust vtrustToPool(PoolVtrust vtrust) throws Exception{
		DraftPoolBase pool = new DraftPoolBase();
		pool.setPlDrwrNm(vtrust.getVtdrwrName());//出票人
		pool.setPlAccptrNm(vtrust.getVtaccptrName());//承兑人
		pool.setPlAccptrSvcr(vtrust.getVtaccptrBankAccount());//承兑行
		
		//承兑行总行
		String acptBankNo = pool.getPlAccptrSvcr();
		Map cpes = blackListManageService.queryCpesMember(acptBankNo);
		if(cpes != null){
			pool.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
			String memberName = (String) cpes.get("memberName");//总行行名
			pool.setAcptHeadBankName(memberName);//总行行名
		}
		
		//承兑行所在地区
		pool.setPlDraftNb(vtrust.getVtNb());//票号
		pool.setPlDraftMedia(vtrust.getVtDraftMedia());//票据介质
		pool.setForbidFlag(vtrust.getVtTranSfer());//禁止背书标识
		return vtrust;
	}

	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}





	
}
