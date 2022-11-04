package com.mingtech.application.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FTPUtils {
	private static final Logger logger = Logger.getLogger(FTPUtils.class);

	private static FTPUtils ftpUtils;
	private FTPClient ftpClient;

	private String port; // 服务器端口
	private String username; // 用户登录名
	private String password; // 用户登录密码

	private InputStream is; // 文件下载输入流

	/**
	 * 私有构造方法
	 */
	private FTPUtils() {
		initConfig();
		if (null == ftpClient) {
			ftpClient = new FTPClient();
		}
	}

	/**
	 * 获取FTPUtils对象实例
	 *
	 * @return FTPUtils对象实例
	 */
	public synchronized static FTPUtils getInstance() {
		if (null == ftpUtils) {
			ftpUtils = new FTPUtils();
		}
		return ftpUtils;
	}

	/**
	 * 初始化FTP服务器连接属性
	 */
	public void initConfig() {
		// 定义配置文件输入流
		port = "21";
		username = "test";
		password = "test";
	}

	/**
	 * 连接（配置通用连接属性）至服务器
	 *
	 * @param serverName 服务器名称
	 * @param remotePath 当前访问目录
	 * @return <b>true</b>：连接成功
	 * <br/>
	 * <b>false</b>：连接失败
	 */
	public boolean connectToTheServer(String serverName, String remotePath) {
		// 定义返回值
		boolean result = false;
		try {
			// 连接至服务器，端口默认为21时，可直接通过URL连接
			ftpClient.connect(serverName, Integer.parseInt(port));
			// 登录服务器
			ftpClient.login(username, password);
			// 判断返回码是否合法
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				// 不合法时断开连接
				ftpClient.disconnect();
				// 结束程序
				return result;
			}
			// 设置文件操作目录
			result = ftpClient.changeWorkingDirectory(remotePath);
			// 设置文件类型，二进制
			result = ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			// 设置缓冲区大小
			ftpClient.setBufferSize(3072);
			// 设置字符编码
			ftpClient.setControlEncoding("UTF-8");
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 上传文件至FTP服务器
	 *
	 * @param serverName 服务器名称
	 * @param storePath  上传文件存储路径
	 * @param fileName   上传文件存储名称
	 * @param is         上传文件输入流
	 * @return <b>true</b>：上传成功
	 * <br/>
	 * <b>false</b>：上传失败
	 */
	public boolean storeFile(String serverName, String storePath, String fileName, InputStream is) {
		boolean result = false;
		try {
			// 连接至服务器
			result = connectToTheServer(serverName, storePath);

			// 判断服务器是否连接成功
			if (result) {
				// 上传文件
				result = ftpClient.storeFile(fileName, is);
			}
			// 关闭输入流
			is.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			// 判断输入流是否存在
			if (null != is) {
				try {
					// 关闭输入流
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
			// 登出服务器并断开连接
			ftpUtils.logout();
		}
		return result;
	}

	/**
	 * 下载FTP服务器文件至本地<br/>
	 * 操作完成后需调用logout方法与服务器断开连接
	 *
	 * @param serverName 服务器名称
	 * @param remotePath 下载文件存储路径
	 * @param fileName   下载文件存储名称
	 * @return <b>InputStream</b>：文件输入流
	 */
	public InputStream retrieveFile(String serverName, String remotePath, String fileName) {
		try {
			boolean result = false;
			// 连接至服务器
			result = connectToTheServer(serverName, remotePath);
			// 判断服务器是否连接成功
			if (result) {
				// 获取文件输入流
				is = ftpClient.retrieveFileStream(fileName);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return is;
	}

	/**
	 * 删除FTP服务器文件
	 *
	 * @param serverName 服务器名称
	 * @param remotePath 当前访问目录
	 * @param fileName   文件存储名称
	 * @return <b>true</b>：删除成功
	 * <br/>
	 * <b>false</b>：删除失败
	 */
	public boolean deleteFile(String serverName, String remotePath, String fileName) {
		boolean result = false;
		// 连接至服务器
		result = connectToTheServer(serverName, remotePath);
		// 判断服务器是否连接成功
		if (result) {
			try {
				// 删除文件
				result = ftpClient.deleteFile(fileName);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			} finally {
				// 登出服务器并断开连接
				ftpUtils.logout();
			}
		}
		return result;
	}

	/**
	 * 检测FTP服务器文件是否存在
	 *
	 * @param serverName 服务器名称
	 * @param remotePath 检测文件存储路径
	 * @param fileName   检测文件存储名称
	 * @return <b>true</b>：文件存在
	 * <br/>
	 * <b>false</b>：文件不存在
	 */
	public boolean checkFile(String serverName, String remotePath, String fileName) {
		boolean result = false;
		try {
			// 连接至服务器
			result = connectToTheServer(serverName, remotePath);
			// 判断服务器是否连接成功
			if (result) {
				// 默认文件不存在
				result = false;
				// 获取文件操作目录下所有文件名称
				String[] remoteNames = ftpClient.listNames();
				// 循环比对文件名称，判断是否含有当前要下载的文件名
				for (String remoteName : remoteNames) {
					if (fileName.equals(remoteName)) {
						result = true;
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally {
			// 登出服务器并断开连接
			ftpUtils.logout();
		}
		return result;
	}

	/**
	 * 登出服务器并断开连接
	 *
	 * @return <b>true</b>：操作成功
	 * <br/>
	 * <b>false</b>：操作失败
	 */
	public boolean logout() {
		boolean result = false;
		if (null != is) {
			try {
				// 关闭输入流
				is.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		if (null != ftpClient) {
			try {
				// 登出服务器
				result = ftpClient.logout();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			} finally {
				// 判断连接是否存在
				if (ftpClient.isConnected()) {
					try {
						// 断开连接
						ftpClient.disconnect();
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
		}
		return result;
	}

}