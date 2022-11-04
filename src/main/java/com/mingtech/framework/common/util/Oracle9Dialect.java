package com.mingtech.framework.common.util;

import org.hibernate.dialect.OracleDialect;

public class Oracle9Dialect extends OracleDialect {
	public Oracle9Dialect() {
		 super();
	}     
	  public boolean supportsLimit() {
		return false;
	}   
	   

}
