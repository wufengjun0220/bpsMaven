package com.mingtech;

import org.apache.log4j.Logger;


public class JettyServer {
	private static final Logger log = Logger.getLogger(JettyServer.class);

	/**
	 * @param args
	public static void main(String[] args) {
		ResourceBundle resb = ResourceBundle.getBundle("jetty");
		String port = resb.getString("jetty.server.port");
		String contextPath = resb.getString("jetty.server.contextPath");
		File file = new File(JettyServer.class.getResource("/").getPath());
		String appPath = file.getParentFile().getParentFile().toString();
		 Server server = createDevServer(Integer.valueOf(port).intValue(), contextPath,appPath);
	     try {
	            server.start();
	            server.join();
	        } catch (Exception e) {
	        	log.error("通过jetty启动服务异常",e);
	        } finally {
	            try {
	                server.stop();
	            } catch (Exception e) {
	                log.error("jetty服务停止异常",e);
	            }
	        }
	}
	 */

	
	/**
	 * 创建用于开发运行调试的JettyServer
	 * @param port        访问服务器的端口
	 * @param contextPath 访问服务器的上下文路径
	public static Server createJettyServer(int port, String contextPath) {
        Server server = new Server(port);
        //设置在JVM退出时关闭Jetty
        server.setStopAtShutdown(true);
        ProtectionDomain protectionDomain = JettyServer.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        String warFile = location.toExternalForm();
        WebAppContext context = new WebAppContext(warFile, contextPath);
        context.setServer(server);

        // 设置work dir,war包将解压到该目录，jsp编译后的文件也将放入其中。
        String currentDir = new File(location.getPath()).getParent();
        File workDir = new File(currentDir, "work");
        context.setTempDirectory(workDir);
        server.setHandler(context);
        return server;
    }
	 */

	
	/**
	 * 创建用于开发运行调试的JettyServer
	 * @param port        访问服务器的端口
	 * @param contextPath 访问服务器的上下文路径
	 * @param appPath web资源存放更路径
	 
    public static Server createDevServer(int port, String contextPath,String appPath) {

        Server server = new Server();
        server.setStopAtShutdown(true);

        ServerConnector connector = new ServerConnector(server);
        // 设置服务端口
        connector.setPort(port);
       // 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
        connector.setReuseAddress(false);
        server.setConnectors(new Connector[] {connector});
   
        // 设置web资源根路径以及访问web的根路径
        WebAppContext webAppCtx = new WebAppContext(appPath, contextPath);
        // 设置webapp的位置
        webAppCtx.setResourceBase(appPath);
        //设置web.xml配置文件路径，默认是$(baseResource)/WEB-INF/web.xml
        //webAppCtx.setDescriptor(DEFAULT_APP_CONTEXT_PATH + "/WEB-INF/web.xml");
       
        webAppCtx.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(webAppCtx);

        return server;
    }
    */
}
