package com.mingtech.application.autotask.taskService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.edu.domain.PedBailHis;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 
 * @Title: 保证金历史查询
 * @Description: 
 * @author Ju Nana
 * @date 2018-11-8
 */
public class AutomarginAccountNO1Task  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutomarginAccountNO1Task.class);
	BlackListManageService blackListManageService  = PoolCommonServiceFactory.getBlackListManageService();
	PoolBailEduService poolBailEduService = PoolCommonServiceFactory.getPoolBailEduService();
	/**
	 * （1）查询接口所需要的字段值
	 * 
	 * （2）将返回数据存入PED_BAIL_HIS表
	 */
	public BooleanAutoTaskResult run() throws Exception {
		
		PedProtocolService pedProtocolService =  PoolCommonServiceFactory.getPedProtocolService();
		PoolCoreService poolCoreService = PoolCommonServiceFactory.getPoolCoreService();
		List  pedList = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01,PoolComm.VS_01, null, null, null, null);//获取所有未解约票据池的客户信息
		PedProtocolDto dto = null;
		if(pedList!=null && pedList.size()>0){
				for (int i = 0; i < pedList.size(); i++) {
					try {
					
					dto = (PedProtocolDto) pedList.get(i);
					logger.info("保证金历史查询，查询保证金账号:"+dto.getMarginAccount()+"第"+(i+1)+"次循环");
					CoreTransNotes trans  = new CoreTransNotes();
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(DateUtils.getCurrDate());//设置起时间
				    cal.add(Calendar.DATE, -1);//查询前一日的时间
				    Date transDate = cal.getTime();
				    Date endDate = cal.getTime();//起始日与终止日为一天
				    
					trans.setTranDate(DateUtils.toString(DateUtils.getCurrDate(), "yyyy-MM-dd"));//交易日期
					trans.setDateEnd(DateUtils.toString(endDate, "yyyy-MM-dd"));//终止日期
					trans.setDateStr(DateUtils.toString(transDate, "yyyy-MM-dd"));//开始日期
					trans.setAccNo(dto.getMarginAccount());//保证金账号
					trans.setAmt("0.00");//金额
					trans.setIsSucced("");//查询类型
		//			trans.setSubSeq("1");//款项序号
					ReturnMessageNew response = poolCoreService.PJH584141Handler(trans);
					if(response.isTxSuccess()){
						List Deatils = response.getDetails();
						if(Deatils!=null && Deatils.size()>0){
							logger.info("保证金历史查询，得到的数据有："+Deatils.size()+"条");
							for (int j = 0; j < Deatils.size(); j++) {
								Map map = (Map) Deatils.get(j);
								PedBailHis bail = new PedBailHis();
								bail.setAccNo((String)map.get("AccNo"));
								bail.setCcy((String)map.get("Ccy"));      
								bail.setVouTyp((String)map.get("VouTyp"));   
								bail.setVouNo((String)map.get("VouNo"));    
								bail.setDateTran((String)map.get("DateTran")); 
								bail.setTimeMch((String)map.get("TimeMch"));  
								bail.setMemoNo((String)map.get("MemoNo"));   
								bail.setMemo((String)map.get("Memo"));     
								bail.setAmtTran((String)map.get("AmtTran")); 
								bail.setBal((String)map.get("Bal"));      
								bail.setSerSeqNo((String)map.get("SerSeqNo")); 
								bail.setCSeqNo((String)map.get("CSeqNo"));   
								bail.setFlgCd((String)map.get("FlgCD"));    
								bail.setFlagCt((String)map.get("FlagCT"));   
								bail.setAccNoA((String)map.get("AccNoA"));   
								bail.setAcctSeqNo((String)map.get("AcctSeqNo"));
								bail.setSubSeq((String)map.get("SubSeq"));   
								bail.setTelTran((String)map.get("TelTran"));  
								bail.setBrcTran((String)map.get("BrcTran"));  
								bail.setBrcName((String)map.get("BrcName"));  
								bail.setNum1((String)map.get("Num1"));     
								bail.setCustNam((String)map.get("CustNam"));  
								bail.setField((String)map.get("Field"));    
								bail.setFlgCanl((String)map.get("FlgCanl"));  
								bail.setSeqNoR((String)map.get("SeqNoR"));   
								bail.setAccNoA2((String)map.get("AccNoA2"));  
								bail.setAccNamA2((String)map.get("AccNamA2"));
								bail.setBnkNamA2((String)map.get("BnkNamA2"));
								
								pedProtocolService.txStore(bail);
							}
						}
						
					}else{
						logger.info("核心【保证金账户查询接口】查询异常，无保证金当日交易记录，核心错误码：【"+response.getRet().getRET_CODE()+"】核心错误信息：【"+response.getRet().getRET_MSG()+"】");
					}
					
					
					} catch (Exception e) {
						logger.info("保证金历史交易查询异常");
						logger.error(e.getMessage(),e);
					}
					
				}
	}
		//删除保证金当日交易表中的数据
		poolBailEduService.txDeleteBailFlow(null);
		return new BooleanAutoTaskResult(true);
	}
	

	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}





	
}
