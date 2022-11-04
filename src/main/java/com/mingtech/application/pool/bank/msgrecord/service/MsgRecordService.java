package com.mingtech.application.pool.bank.msgrecord.service;

import com.mingtech.application.pool.bank.msgrecord.domain.MsgRecord;
import com.mingtech.framework.core.service.GenericService;

public interface MsgRecordService extends GenericService{
	public void txSaveMsg(MsgRecord msgRecord);
}
