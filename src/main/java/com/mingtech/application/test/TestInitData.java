package com.mingtech.application.test;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import junit.framework.TestCase;

public class TestInitData extends TestCase {
	public void testInitData(){
		Configuration config=new Configuration().configure();
		SchemaExport se=new SchemaExport(config);
		se.create(true, true);
	}
}
