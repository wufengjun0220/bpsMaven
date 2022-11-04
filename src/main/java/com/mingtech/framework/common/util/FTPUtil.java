package com.mingtech.framework.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * FTP常用操作类 使用方法:
 * <p>
 * 
 * 1)连接到服务器:构造FTPUtil对象即可
 * <p>
 * 
 * 2)上传文件:uploadFile
 * <p>
 * 
 * 3)下载文件downloadFile
 * <p>
 * 
 * 4)文件列表查询:listFile
 * <p>
 * 
 * 5)删除文件deleteFile
 * <p>
 * 
 * 6)使用完毕后,一定要调用disConnect方法,释放连接
 * <p>
 * 
 * 7)调用某个方法后,如果返回结果错误,可以通过getErrMsg()获取详细原因
 * <p>
 * 
 * 
 * 
 */
public class FTPUtil {

	private static Logger log = Logger.getLogger(FTPUtil.class);

	/*
	 * 错误消息
	 */
	private String errMsg;

	private FTPClient ftp = null;

	private String hostName;

	private int hostPort;

	/*
	 * 是否连接到服务器
	 */
	private boolean isConnect = false;

	private String passWord;

	/*
	 * 用户主目录
	 */
	private String userHomeDir = ".";

	private String userName;

	/**
	 * 构造函数
	 * 
	 *@param hostName
	 *            服务器地址
	 *@param userName
	 *            FTP用户名
	 *@param passWord
	 *            FTP密码
	 *@param hostPort
	 *            服务器端口
	 */
	public FTPUtil(String hostName, String userName, String passWord,
			int hostPort) {
		this.hostName = hostName;
		this.userName = userName;
		this.passWord = passWord;
		this.hostPort = hostPort;
		userHomeDir = ".";
		ftp = new FTPClient();
		isConnect = connectServer();

	}

	public FTPUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the ErrMsg attribute of the FTPUtil object
	 * 
	 *@param errMsg
	 *            The new ErrMsg value
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
		if (errMsg != null && errMsg.length() > 0) {
			log.error(errMsg);
		}
	}

	/**
	 * Sets the HostName attribute of the FTPUtil object
	 * 
	 *@param hostName
	 *            The new HostName value
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Sets the HostPort attribute of the FTPUtil object
	 * 
	 *@param hostPort
	 *            The new HostPort value
	 */
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}

	/**
	 * Sets the PassWord attribute of the FTPUtil object
	 * 
	 *@param passWord
	 *            The new PassWord value
	 */
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	/**
	 * Sets the UserName attribute of the FTPUtil object
	 * 
	 *@param userName
	 *            The new UserName value
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the ErrMsg attribute of the FTPUtil object
	 * 
	 *@return The ErrMsg value
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * Gets the HostName attribute of the FTPUtil object
	 * 
	 *@return The HostName value
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Gets the HostPort attribute of the FTPUtil object
	 * 
	 *@return The HostPort value
	 */
	public int getHostPort() {
		return hostPort;
	}

	/**
	 * Gets the PassWord attribute of the FTPUtil object
	 * 
	 *@return The PassWord value
	 */
	public String getPassWord() {
		return passWord;
	}

	/**
	 * Gets the UserName attribute of the FTPUtil object
	 * 
	 *@return The UserName value
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 切换工作目录
	 * 
	 *@param remotePath
	 *            目标文件路径
	 *@param isCreate
	 *            如果不存在,是否要创建
	 *@return Description of the Returned Value
	 */
	public boolean changeWorkDir(String remotePath, boolean isCreate) {
		setErrMsg("");
		try {

			if (!isConnect) {
				isConnect = connectServer();
			}
			if (!isConnect) {
				return false;
			}
			remotePath = remotePath.replace('\\', '/').trim();
			/*
			 * 从用户主目录开始
			 */
			if (remotePath.startsWith("/")) {
				ftp.changeWorkingDirectory(userHomeDir);
				remotePath = remotePath.substring(0);
				if (remotePath.length() == 0) {
					return true;
				}
			}

			boolean ret = false;
			ret = ftp.changeWorkingDirectory(remotePath);
			if (!ret) {
				if (!isCreate) {
					return false;
				} else {
					/*
					 * 如果不存在且isCreate==true,创建目录
					 */
					mkDir(remotePath);

				}

			}

			ftp.changeWorkingDirectory(userHomeDir);
			ret = ftp.changeWorkingDirectory(remotePath);
			if (!ret) {
				setErrMsg("服务器路径" + remotePath + "不存在");
				return false;
			}
			return true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			return false;
		}

	}

	/**
	 * 连接服务器
	 * 
	 *@return Description of the Returned Value
	 */
	public boolean connectServer() {
		setErrMsg("");
		try {
			ftp.connect(hostName, hostPort);
			int reply;

			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				setErrMsg("连接到服务器的请求被拒绝");

				return false;
			}

			if (!ftp.login(userName, passWord)) {
				setErrMsg("用户名,密码校验失败");
				return false;
			}
			ftp.setFileType(FTP.ASCII_FILE_TYPE);

			ftp.enterLocalPassiveMode();
			userHomeDir = ftp.printWorkingDirectory();
			return true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			setErrMsg(ex.getMessage());
			return false;
		}

	}

	/**
	 * 删除文件
	 * 
	 *@param remotePath
	 *            服务器路径
	 *@param remoteFileName
	 *            将要删除的文件名
	 *@return Description of the Returned Value
	 */
	public boolean deleteFile(String remotePath, String remoteFileName) {
		setErrMsg("");
		try {

			if (!isConnect) {
				isConnect = connectServer();
			}
			if (!isConnect) {
				return false;
			}
			boolean ret = false;
			/*
			 * 如果目标路径不存在,返回失败
			 */
			ret = changeWorkDir(remotePath, false);

			if (!ret) {
				return false;
			}
			return ftp.deleteFile(remoteFileName);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return false;
		}

	}

	/**
	 * 断开与服务器的连接
	 * 
	 *@return Description of the Returned Value
	 */
	public boolean disConnectServer() {
		isConnect = false;
		setErrMsg("");
		try {
			ftp.logout();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("ftp logout 失败");

		}

		try {

			ftp.disconnect();
			log.info("断开ftp连接成功....");
			return true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("ftp disconnect 失败");
			log.info("断开ftp连接失败....");
			return false;
		}

	}

	/**
	 * 上传数据
	 * 
	 *@param remotePath
	 *            服务器远程目录
	 *@param remoteFileName
	 *            服务器将要读取的文件名
	 *@param os
	 *            Description of Parameter
	 *@return 成功:true,否则为False
	 */
	private boolean downloadData(String remotePath, String remoteFileName,
			OutputStream os) {
		setErrMsg("");
		try {

			if (!isConnect) {
				isConnect = connectServer();
			}
			if (!isConnect) {
				return false;
			}
			boolean ret = false;
			/*
			 * 如果目标路径不存在,返回失败
			 */
			ret = changeWorkDir(remotePath, false);

			if (!ret) {
				setErrMsg("服务器路径" + remotePath + "不存在");
				return false;
			}

			return ftp.retrieveFile(remoteFileName, os);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			return false;
		}

	}

	/**
	 * 下载文件
	 * 
	 *@param remotePath
	 *            服务器远程目录
	 *@param remoteFileName
	 *            服务器目标文件名
	 *@param localFileName
	 *            本地文件名
	 *@return 成功:true,否则为False
	 */
	public boolean downloadFile(String remotePath, String remoteFileName,
			String localFileName) {
		setErrMsg("");
		BufferedOutputStream bos = null;
		try {

			bos = new BufferedOutputStream(new FileOutputStream(localFileName));

			if (!downloadData(remotePath, remoteFileName, bos)) {
				return false;
			}
			return true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("下载文件失败:" + ex.getMessage());
			return false;
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				//ex.printStackTrace();
			}

		}

	}
	
	/**
	 * 下载文件
	 * 
	 *@param remotePath
	 *            服务器远程目录
	 *@param remoteFileName
	 *            服务器目标的文件名
	 *@return 二进制对象
	 */
	public byte[] downloadFile(String remotePath, String remoteFileName) {
		setErrMsg("");
		BufferedOutputStream bos = null;
		try {
			ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(bos2);

			if (!downloadData(remotePath, remoteFileName, bos)) {
				return null;
			}
			return bos2.toByteArray();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("下载文件失败:" + ex.getMessage());
			return null;
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (Exception ex) {
				 log.error(ex.getMessage());
				//ex.printStackTrace();
			}

		}

	}

	/**
	 * 文件列表
	 * 
	 *@param remotePath
	 *            目标文件路径
	 *@return Description of the Returned Value
	 */
	public String[] listFile(String remotePath) {
		setErrMsg("");
		try {

			if (!isConnect) {
				isConnect = connectServer();
			}
			if (!isConnect) {
				setErrMsg("连接服务器失败");
				return null;
			}
			boolean ret = false;
			if (remotePath == null || remotePath.trim().length() == 0) {
				remotePath = "/";
			}
			ret = changeWorkDir(remotePath, false);
			if (!ret) {
				setErrMsg("服务器路径" + remotePath + "不存在");
				return null;
			}
			FTPFile[] ff = ftp.listFiles(".");
			if (ff == null) {
				ff = ftp.listFiles();
			}
			if (ff == null) {
				return null;
			}
			Vector v = new Vector();
			for (int i = 0; i < ff.length; i++) {
				if (ff[i].isFile()) {
					v.add(ff[i].getName());
				}
			}
			String[] sret = new String[v.size()];
			v.toArray(sret);
			return sret;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg(ex.getMessage());
			return null;
		}

	}

	/**
	 * The main program for the FTPUtil class
	 * 
	 *@param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {

		// FTPUtil FTPUtil1 = new FTPUtil("localhost", "outServ", "web", 21);
		// System.err.println(FTPUtil1.listFile("/").length);
		// System.err.println(FTPUtil1.changeWorkDir("\\", true));
		// System.err.println(FTPUtil1.uploadFile("\\request2\\20031210",
		// "a.doc", "f:/a.doc"));
		// System.err.println(FTPUtil1.downloadFile("\\request2\\20031210",
		// "a.doc", "f:/a2.doc"));
		// System.err.println(FTPUtil1.deleteFile("\\request2\\20031210",
		// "a.doc"));
		// System.err.println(FTPUtil1.listFile("\\request2\\20031210").length);
		// System.err.println(FTPUtil1.uploadFile("\\request2\\20031210",
		// "TestLog.xls", "f:/TestLog.xls"));
		// FTPUtil1.disConnectServer();
		
		FTPUtil FTPUtil1 = new FTPUtil("10.168.168.10", "cm", "cm-000000", 21);
		String upload = "123456";
		ByteArrayInputStream b = new ByteArrayInputStream(upload.getBytes());
		FTPUtil1.uploadData("\\History\\test", "test", b);
		FTPUtil1.disConnectServer();
	}
	
	/**
	 * 根据文件名获取文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public byte[] getFile(String fileName) {
		setErrMsg("");
		FileInputStream fi = null;
		try {
			fi = new FileInputStream(fileName);
			byte[] t = new byte[fi.available()];
			fi.read(t);

			return t;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("获取文件内容件失败:" + ex.getMessage());
			return null;
		} finally {
			try {
				if (fi != null) {
					fi.close();
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				//ex.printStackTrace();
			}

		}

	}
	/**
	 * 创建文件夹
	 * 
	 *@param remotePath
	 *            目标文件路径
	 *@return Description of the Returned Value
	 */
	public boolean mkDir(String remotePath) {
		setErrMsg("");
		try {

			if (!isConnect) {
				isConnect = connectServer();
			}
			if (!isConnect) {
				return false;
			}
			remotePath = remotePath.replace('\\', '/').trim();
			/*
			 * 从用户主目录开始
			 */
			if (remotePath.startsWith("/")) {
				ftp.changeWorkingDirectory(userHomeDir);
				remotePath = remotePath.substring(1);
			}
			boolean ret = false;
			ret = ftp.changeWorkingDirectory(remotePath);
			if (!ret) {
				String[] paths = remotePath.split("/");
				for (int i = 0; i < paths.length; i++) {
					String path = paths[i].trim();
					if (paths[i].length() > 0) {
						ftp.makeDirectory(path);
						ftp.changeWorkingDirectory(path);
					}
				}

			}
			ret = ftp.changeWorkingDirectory(remotePath);
			if (!ret) {
				setErrMsg("服务器路径" + remotePath + "不存在");
				return false;
			}
			return true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			return false;
		}

	}

	/**
	 * 上传数据
	 * 
	 *@param remotePath
	 *            服务器远程目录
	 *@param remoteFileName
	 *            服务器将要保存的文件名
	 *@param is
	 *            输入流
	 *@return 成功:true,否则为False
	 */
	private boolean uploadData(String remotePath, String remoteFileName, InputStream is) {
		setErrMsg("");
		try {

			if (!isConnect) {
				isConnect = connectServer();
			}
			if (!isConnect) {
				return false;
			}
			boolean ret = false;
			/*
			 * 如果目标路径不存在,首先创建
			 */
			ret = ftp.changeWorkingDirectory(remotePath);
			if (!ret) {
				ret = changeWorkDir(remotePath, true);
			}

			if (!ret) {
				setErrMsg("服务器路径" + remotePath + "不存在");
				return false;
			}

			return ftp.storeFile(remoteFileName, is);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			return false;
		}

	}

	/**
	 * 上传文件
	 * 
	 *@param remotePath
	 *            服务器远程目录
	 *@param remoteFileName
	 *            服务器将要保存的文件名
	 *@param localFileName
	 *            本地文件名
	 *@return 成功:true,否则为False
	 */
	public boolean uploadFile(String remotePath, String remoteFileName,
			String localFileName) {
		setErrMsg("");
		BufferedInputStream bis = null;
		try {

			bis = new BufferedInputStream(new FileInputStream(localFileName));
			return uploadData(remotePath, remoteFileName, bis);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("上传文件失败:" + ex.getMessage());
			return false;
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				//ex.printStackTrace();
			}

		}

	}

	/**
	 * 上传文件
	 * 
	 *@param remotePath
	 *            服务器远程目录
	 *@param remoteFileName
	 *            服务器将要保存的文件名
	 *@param data
	 *            二进制文件
	 *@return 成功:true,否则为False
	 */
	public boolean uploadFile(String remotePath, String remoteFileName,byte[] data) {
		setErrMsg("");
		BufferedInputStream bis = null;
		try {

			bis = new BufferedInputStream(new ByteArrayInputStream(data));
			return uploadData(remotePath, remoteFileName, bis);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("上传文件失败:" + ex.getMessage());
			return false;
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				//ex.printStackTrace();
			}

		}

	}
	
	/**
	 * 写入本地文件
	 * 
	 *@param localPath
	 *            本地目录
	 *@param fileName
	 *            服务器将要保存的文件名
	 *@param data
	 *            
	 *@return 成功:true,否则为False
	 */
	public boolean writeLocalFile(String localPath, String fileName,String data) {
		
		BufferedInputStream bis = null;
		try {
			String date = fileName.substring(13, 21);
			localPath=localPath+File.separator+date;
			localPath.replace(File.separator.concat(File.separator), File.separator);
		//	bis = new BufferedInputStream(new ByteArrayInputStream(data));
			return writeData(localPath, fileName, data);
		} catch (Exception ex) {
			log.info(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("写入文件失败:" + ex.getMessage());
			return false;
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (Exception ex) {
				log.info(ex.getMessage());
			}

		}

	}
	
	private boolean writeData(String localPath, String fileName, String data) {
		setErrMsg("");
		BufferedWriter bufw =null;
		try {

			File file = new File(localPath,fileName);
			if(file.exists()){
				setErrMsg("文件路径'"+localPath+"'下,已经存在该文件："+file.getName());
				return false;
			}else{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream fOutputStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOutputStream,"UTF-8");
			bufw = new BufferedWriter(outputStreamWriter);
			bufw.write(data);
			bufw.flush();
			return true;
			} catch (Exception ex) {
				 log.error(ex.getMessage());
				//ex.printStackTrace();
				return false;
			}finally {
				try {
					if (bufw != null) {
						bufw.close();
					}
					} catch (Exception ex) {
						log.error(ex.getMessage());
						//ex.printStackTrace();
					}
			}

	}
	
	
	/**
	 * 读取本地文件
	 * 
	 *@param localPath
	 *            本地目录
	 *@param fileName
	 *            服务器将要保存的文件名
	 *            
	 *@return 文件内容
	 */
	public String readLocalFile(String localPath, String fileName) {
		setErrMsg("");
		FileInputStream fis = null;
		BufferedReader reader = null;
		String line=null;
		StringBuffer sb = new StringBuffer();
		String date = fileName.substring(13, 21);
		String pathFileName=localPath+File.separator+date+File.separator+fileName;
	//	String pathFileName = localPath +"/"+fileName;
		pathFileName = pathFileName.replace(File.separator.concat(File.separator), File.separator);
		try {
			File file = new File(pathFileName);
			if(!file.exists()) throw new FileNotFoundException("文件路径不存在:"+localPath);
			fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis));
			
			int i =0;
			while ((line = reader.readLine()) != null) {
				if(i==0) {
					i++;
					continue;//首行跳出
				}
				sb.append(line);
				sb.append("\n");
			}
			
		} catch (Exception ex) {
			log.info(ex.getMessage());
			//ex.printStackTrace();
			setErrMsg("读取文件失败:" + ex.getMessage());
			return null;
		} finally {
			try {
				if(fis != null) fis.close();
				if(reader != null) reader.close();
			} catch (Exception ex) {
				log.info(ex.getMessage());
			}
		}
		return sb.toString().trim();
	}


}
