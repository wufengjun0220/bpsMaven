package com.mingtech.application.pool.common.util;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.framework.common.util.ProjectConfig;


/**
* <p>版权所有:(C)2003-2011 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Oct 9, 2011 5:38:50 PM
* @描述: [TFtpUtil] 河北银行文件传输
*/
public class TFtpUtil{
	private Logger logger = Logger.getLogger(TFtpUtil.class);
	public boolean uploadFile(String localDir,String remoteDir) throws Exception{
		logger.info("准备上传文件...");
		String clientPath = ProjectConfig.getInstance().getTftClientPath();
		clientPath += Constants.FILE_SEPARTOR+Constants.SH_UPLOAD_NAME ;
		logger.info("本地配置文件存放目录："+clientPath);
		logger.info("发送目录："+remoteDir);
		logger.info("文件名："+localDir);
	    String[] arg1 = new String[] { "sh", " ",clientPath,localDir, remoteDir};
	    logger.info("CMD："+Arrays.toString(arg1));
	    exec(arg1);
	    logger.info("文件名："+localDir);
		return true;
	}
	public boolean downLoadFile(String remoteDir,String localdir)throws Exception{
		logger.info("准备下载文件...");
		String clientPath = ProjectConfig.getInstance().getTftClientPath();
		clientPath += Constants.FILE_SEPARTOR+Constants.SH_DOWNLOAD_NAME ;
		logger.info("本地配置文件存放目录："+clientPath);
		logger.info("发送目录："+remoteDir);
		logger.info("文件名："+localdir);
	    String[] arg1 = new String[] { "sh", " ",clientPath,localdir, remoteDir};
	    logger.info("CMD："+Arrays.toString(arg1));
	    exec(arg1);
	    logger.info("文件名："+localdir);
		return true;
	}
	
	private void exec(String[] arg1)throws Exception{
		Process proc = null;
		Runtime runtime = null;
		runtime = Runtime.getRuntime();
		try{
			proc = runtime.exec(arg1);
		}catch (IOException e){
			logger.error(e.getMessage(),e);
			throw new Exception("上传文件失败！"+e);
		}
		try{
			if(null !=proc){
				proc.waitFor();
			}
		}catch (InterruptedException e){
			logger.error(e.getMessage(),e);
			throw new Exception("上传文件失败！"+e);
		}
	}
}
