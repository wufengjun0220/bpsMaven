package com.mingtech.application.pool.enginee.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mingtech.application.pool.enginee.core.Context;
import com.mingtech.application.pool.enginee.utils.Constants;


public class DefaultContext implements Context {

	public DefaultContext() {
		initSysVars();
	}
	

	private void initSysVars() {
		vars.put(Constants.SYS_VAR_DATE, 
				new SimpleDateFormat("yyyyMMdd").format(new Date()));
		vars.put(Constants.SYS_VAR_TIME, 
				new SimpleDateFormat("HHmmss").format(new Date()));
	}

	@Override
	public void setVariable(String var, Object value) {
		vars.put(var, value);
	}

	@Override
	public Object getVariable(String var) {
		if(vars.containsKey(var)) {
			return vars.get(var);
		} else {
			return null;
		}
	}
	
	public void setVariables(Map<String, Object> values) {
		vars.putAll(values);
	}
	
	@Override
	public boolean has(String var) {
		return vars.containsKey(var);
	}
	
	public Map<String, Object> toMap() {
		return vars;
	}


	private Map<String, Object> vars = new HashMap<String, Object>();
}
