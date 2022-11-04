package com.mingtech.application.pool.bank.message.service;

import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;

public interface PoolMssService {

	
	/**
	 * @Title txMss001Handler
	 * @author gcj
	 * @date 20210527
	 * @Description 短信发送 
	 * @return ReturnMessageNew
	 * @throws Exception 
	 */
	public ReturnMessageNew txMess001Handler(CoreTransNotes transNotes) throws Exception;
}
