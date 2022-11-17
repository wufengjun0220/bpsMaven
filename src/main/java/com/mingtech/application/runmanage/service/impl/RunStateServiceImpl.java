package com.mingtech.application.runmanage.service.impl;

import java.util.Date;
import java.util.List;

import com.mingtech.application.cache.RunStateCache;
import com.mingtech.application.runmanage.domain.RunState;
import com.mingtech.application.runmanage.service.RunStateService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RunStateServiceImpl extends GenericServiceImpl	implements RunStateService {

	
	public Class getEntityClass() {
		return RunState.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(RunState.class);
	}
	
	public RunState getSysRunState(){
		
		RunState runState = RunStateCache.getRunstateCache();
		if(runState != null){
			return runState;
		}
		List list = this.find("select rs from RunState as rs", 1, 1);
		if (list.size()>0) {
	 		return (RunState)list.get(0);
		}else{
			 return null;
		}
		
	}
	
	public RunState getSysRunStateFromDb(){
		List list = this.find("select rs from RunState as rs");
		if (list.size()>0) {
	 		return (RunState)list.get(0);
		}else{
			return null;
		}
	}
	
	public Date getWorkDateTime(){
		RunState rs = this.getSysRunState();
		Date curDate = rs.getCurDate();
		if(curDate != null){
			String d = DateUtils.toString(curDate, DateUtils.ORA_DATES_FORMAT);
			String t = DateUtils.toString(new Date(),DateUtils.ORA_TIME2_FORMAT);
			String dt = d + " " + t;
			return DateUtils.StringToDate(dt, DateUtils.ORA_DATE_TIMES3_FORMAT);
		}
		return new Date();
	}

}
