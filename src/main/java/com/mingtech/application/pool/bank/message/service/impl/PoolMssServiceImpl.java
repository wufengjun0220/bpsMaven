package com.mingtech.application.pool.bank.message.service.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.bbsp.client.EcdsClient;
import com.mingtech.application.pool.bank.coresys.CoreClient;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.service.PoolMssService;


@Service("poolMssService")
public class PoolMssServiceImpl implements PoolMssService{
	private static final Logger logger = Logger
			.getLogger(PoolMssServiceImpl.class);
	
	@Autowired 
	EcdsClient ecdsClient;
	/**
	 * @Title txMss001Handler
	 * @author gcj
	 * @date 20210527
	 * @Description 短信发送 
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew txMess001Handler(CoreTransNotes transNotes) throws Exception {
		ReturnMessageNew request = new ReturnMessageNew();
		
		request.getBody().put("PUSH_TYPE", "SMS");//推送类型
		request.getBody().put("PUSH_ID", transNotes.getPushID());//推送编号
		request.getBody().put("GLOBAL_TYPE","");//证件类型
		request.getBody().put("GLOBAL_ID", "");//证件号
		request.getBody().put("PUSH_CHANNEL", "Account");//推送子通道
		request.getBody().put("MESSAGE_LEVEL", "0");//消息级别
		request.getBody().put("TEMPLATE_CODE", "");//模板代码
		request.getBody().put("TEMPLATE_FIELD", transNotes.getTemplate());//模板项
		request.getBody().put("MSG_TITLE", "");//信息标题
		request.getBody().put("MSG_SUBJECT", "");//信息主题
		request.getBody().put("BOOK_TIME", "");//预约时间
		request.getBody().put("SENDER_NO", "");//发送号码


		ReturnMessageNew response = ecdsClient.processMSS("MESS001", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			response.setTxSuccess(true);
		} else {
			response.setTxSuccess(false);
		}
		return response;
		//throw new Exception(responseCode);
	}
}
