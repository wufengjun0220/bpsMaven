package com.mingtech.application.pool.bank.msgrecord.service.impl;

import com.mingtech.application.pool.bank.msgrecord.domain.MsgRecord;
import com.mingtech.application.pool.bank.msgrecord.service.MsgRecordService;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


public class MsgRecordServiceImpl extends GenericServiceImpl implements MsgRecordService{

	

	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public Class getEntityClass() {
		return null;
	}

	public void txSaveMsg(MsgRecord msgRecord){
		this.txStore(msgRecord);
	}


}
