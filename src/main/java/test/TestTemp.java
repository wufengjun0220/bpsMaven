package test;

import junit.framework.TestCase;

public class TestTemp extends TestCase {

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}
	
	public void testSubstring(){
		String submitIds=",asdf,";
		submitIds=submitIds.substring(0, submitIds.length()-1);
		System.out.println(submitIds);
	}
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
