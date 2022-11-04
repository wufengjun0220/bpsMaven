package com.mingtech.application.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

import com.mingtech.framework.common.util.DateTimeUtil;
import com.mingtech.framework.common.util.DateUtils;



public class test {

	/**
	 * @param args
	 * @author Ju Nana
	 * @throws Exception 
	 * @date 2021-6-23下午5:18:44 
	 */
	public static void main(String[] args) throws Exception {
		
		/**
         * 
         jdbc：代表以jdbc的方式连接； oracle:表示连接的是oracle数据库；
         * thin:表示连接时采用thin模式(oracle中有两种模式)；
         * 
         * @表示地址； 
         *        localhost:1521:orcl中localhost代表本地数据库，1521代表本地数据库端口号，orcl代表本地数据库的sid
         *        。 关于thin的解释： thin是一种瘦客户端的连接方式，即采用这种连接方式不需要安装oracle客户端,
         *        只要求classpath中包含jdbc驱动的jar包就行。thin就是纯粹用Java写的ORACLE数据库访问接口。
         *        oci是一种胖客户端的连接方式，即采用这种连接方式需要安装oracle客户端。oci是Oracle Call
         *        Interface的首字母缩写
         *        ，是ORACLE公司提供了访问接口，就是使用Java来调用本机的Oracle客户端，然后再访问数据库，优点是速度
         *        快，但是需要安装和配置数据库。
         */
        String driverName = "oracle.jdbc.driver.OracleDriver";// 加载驱动

        String dbURL = "jdbc:oracle:thin:@39.105.53.240:1521:ORCLCDB";// localhost代表本机，也可以是
                                                                    // 127.0.0.1，可以填写具体IP
        String userName = "HKBANK";// 用户名
        String Pwd = "HKBANK";// 密码
        try {
            Class.forName(driverName);
            System.out.println("加载驱动成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("加载驱动失败！");
        }
        try {
            Connection dbConn = DriverManager.getConnection(dbURL, userName,
                    Pwd);
            System.out.println("连接数据库成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("数据库连接失败！");
        }
	}
	
	public static void test01() throws IOException{
		
		int count = 10;
		for(int i=0 ;i<count;i++){
			
			String content = "第"+i+"个文件内容";
			
			File file =new File("file-"+i+".txt");
			
			if(!file.exists()){
				file.createNewFile();
			}
			//使用true，即进行append file
			
			FileWriter fileWritter = new FileWriter(file.getName(),true);
			
			fileWritter.write(content);
			
			fileWritter.close();
			System.out.println("执行完第【"+i+"】个文件");
		}
	}
	

}
