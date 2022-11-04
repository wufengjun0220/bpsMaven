package com.mingtech.application.autotask.taskService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;


/**
 *  在线协议日终自动任务
 *  在线协议到期日状态变更为失效
 * @author wss
 * 
 */
public class AutoOnlineProtocolTask  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoOnlineProtocolTask.class);
	
	PedOnlineAcptService pedOnlineAcptService = PoolCommonServiceFactory.getPedOnlineAcptService();
	PedOnlineCrdtService pedOnlineCrdtService = PoolCommonServiceFactory.getPedOnlineCrdtService();
	
	public AutoOnlineProtocolTask() {
	}
	public BooleanAutoTaskResult run() throws Exception {
		
		try {
			/*
			 * 查询到期日小于当天的生效的【在线银承协议】信息，置为失效状态
			 */
			OnlineQueryBean queryBean = new OnlineQueryBean();
			queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
			queryBean.setEndDate(new Date());
			List<PedOnlineAcptProtocol>  acptList = pedOnlineAcptService.queryOnlineAcptProtocolList(queryBean);
			if(acptList != null && acptList.size() >0){
				List<PedOnlineAcptProtocol> storeList = new ArrayList<PedOnlineAcptProtocol>();
				for (PedOnlineAcptProtocol acpt : acptList) {
					
					acpt.setProtocolStatus(PublicStaticDefineTab.STATUS_0);//失效
					storeList.add(acpt);
				}
				pedOnlineAcptService.txStoreAll(storeList);
			}
			
			
			/*
			 * 查询到期日小于当天的生效的【在线流贷协议】信息，置为失效状态
			 */
			
			OnlineQueryBean queryBean1 = new OnlineQueryBean();
			queryBean1.setProtocolStatus(PublicStaticDefineTab.STATUS_1);
			queryBean.setEndDate(new Date());
			List<PedOnlineCrdtProtocol> crdtList = pedOnlineCrdtService.queryOnlineProtocolList(queryBean);
			if(crdtList != null && crdtList.size() >0){
				List<PedOnlineCrdtProtocol> storeList = new ArrayList<PedOnlineCrdtProtocol>();
				for (Object obj : crdtList) {
					PedOnlineCrdtProtocol crdt  = (PedOnlineCrdtProtocol) obj;					
					crdt.setProtocolStatus(PublicStaticDefineTab.STATUS_0);//失效
					storeList.add(crdt);
				}
				pedOnlineCrdtService.txStoreAll(storeList);
			}
			
			
		} catch (Exception e) {
			logger.error("日终在线协议失效变更异常：", e);
		}
		
		return new BooleanAutoTaskResult(true);
	}

	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

}
