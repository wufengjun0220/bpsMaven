package com.mingtech.application.test;

import java.util.Date;

import junit.framework.TestCase;

import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;

public class TestJson extends TestCase {

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void testJsonObject(){
		Department dept = new Department();
		//dept.setCode("aaaa");
		dept.setName("测试");
		System.out.println(JsonUtil.fromObject(dept));
	}
	
	private boolean reference(Object obj){
		obj = "测试改变字符串";
		return true;
	}
	
	public void testRefrence(){
		Object mes = "初始化内容";
		boolean result = this.reference(mes);
		System.out.println(mes);
	}
	
	
	public static void main(String[] args) {
		
		Date date = new Date();
		
		System.out.println(DateUtils.formatDateToString(date, "HHmmss"));
		
	}
}
