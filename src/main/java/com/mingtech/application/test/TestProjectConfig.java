package com.mingtech.application.test;

import java.io.File;
import java.util.Date;

import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.ProjectConfig;

import junit.framework.TestCase;

public class TestProjectConfig extends TestCase {

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void atestAttachPath(){
		String rootpath = ProjectConfig.getInstance().getAttachPath();
		String filepath = rootpath + File.separator + "aaaa" + File.separator+DateUtils.toString(new Date(), DateUtils.ORA_DATES_FORMAT)
				+ "1223435335355" + File.separator;
		System.out.println(filepath);
	}
	
	public void fun(String a){
		if(a=="1"){
			System.out.println("正常 不出错");
		}
	}
	public void testNull(){
		String a=null;
		fun(a);
	}
}
