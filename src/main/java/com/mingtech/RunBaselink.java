package com.mingtech;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RunBaselink {
	private static final Log a = LogFactory.getLog(RunBaselink.class);

	public static void main(String[] args) {
		//BasLinkServer.main(new String[0]);
	}

	/*public void init() {
		a.info("begin to run!");
		a.info("java.library.path:"+System.getProperty("java.library.path"));
		a.info("java.class.path:"+System.getProperty("java.class.path"));
		d d1 = new d();
		try {
			d1.a(ConfigurationFactory.getInstance()
					.getConfiguration("services"));
		} catch (Exception exception) {
			a.error("BasLinkServer start failed.reason:read services configuration file error",
					exception);
			return;
		}
		(new BasLinkServer(d1)).startAll();
	}*/
}
