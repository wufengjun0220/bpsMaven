package com.mingtech.application.pool.enginee.core;

import java.util.Map;


public interface Context {

	
	public void setVariable(String var, Object value);
	
	
	public Object getVariable(String var);
	
	public void setVariables(Map<String, Object> values);
	
	public boolean has(String var);
	
	public Map<String, Object> toMap();
}
