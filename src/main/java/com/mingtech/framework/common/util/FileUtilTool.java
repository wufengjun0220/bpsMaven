package com.mingtech.framework.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 文件处理工具类
 * 
 * @author hexin@
 * @since Jun 13, 2008
 */
public class FileUtilTool {
	private static final Logger logger = Logger.getLogger(FileUtilTool.class);


	/**
	 * 取得文件相对路径
	 * 
	 * @param basePath
	 *            基路径
	 * @param basePath
	 *            文件绝对路径
	 * @return String 文件相对路径
	 * @author hexin@
	 * @since Jun 16, 2008
	 */
	public static String getRealPath(String basePath, String rePath) {
		return rePath.substring(basePath.length());
	}

	/**
	 * 取得文件后缀
	 * 
	 * @param fileName
	 *            文件名
	 * @return String 后缀名
	 * @author hexin@
	 * @since Jun 16, 2008
	 */
	public static String getExtension(String fileName) {
		if (fileName != null) {
			int i = fileName.lastIndexOf('.');
			if (i > 0 && i < fileName.length() - 1) {
				return fileName.substring(i + 1).toLowerCase();
			}
		}
		return "";
	}

	/**
	 * 取得文件前缀
	 * 
	 * @param fileName
	 *            文件名
	 * @return String 文件前缀
	 * @author hexin@
	 * @since Jun 16, 2008
	 */
	public static String getPrefix(String fileName) {
		if (fileName != null) {
			int i = fileName.lastIndexOf('.');
			if (i > 0 && i < fileName.length() - 1) {
				return fileName.substring(0, i);
			}
		}
		return "";
	}

	/**
	 * 删除上下文环境中的一个文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param application
	 *            系统上下文环境
	 * @author hexin@
	 * @since Jun 16, 2008
	 */
	public static void deleteFile(ServletContext application, String filePath) {
		if (StringUtil.isNotBlank(filePath)) {
			String physicalFilePath = application.getRealPath(filePath);
			if (StringUtil.isNotBlank(physicalFilePath)) {
				File file = new File(physicalFilePath);
				file.delete();
			}
		}
	}
	/**
	* <p>方法名称: base64Decoder|描述: 将字符串进行base64解码</p>
	* @param str 需要解码的字符串
	* @param filePath  解码后要存储的文件路径（包括要存储的文件名和后缀）
	*/
	public static void base64Decoder(String str,String filePath){
		if(StringUtils.isNotBlank(str)){
			try{
				byte[] sDecoder = Base64.decodeBase64(str.getBytes());
				File fileTemp = new File(filePath);
				FileOutputStream fileOutputStream = new FileOutputStream(fileTemp);
				fileOutputStream.write(sDecoder);
				fileOutputStream.close();
			}catch (IOException e){
				logger.error(e.getMessage(),e);
			}
		}
		
	}
	
	/**
	* <p>方法名称: base64Encoder|描述: 将文件进行base64编码</p>
	* @param file 需要编码的文件
	* @return
	*/
	public static String base64Encoder(File file){
		String strEncoder = "";
		if(file != null){
			try{
				FileInputStream fileInputStream = new FileInputStream(file);
				byte[] t = new byte[fileInputStream.available()];
				fileInputStream.read(t);
				byte[] encodeBase64 = Base64.encodeBase64(t);
				strEncoder = new String(encodeBase64);
			}catch (IOException e){
				logger.error(e.getMessage(),e);
			}
		}
		return strEncoder;
		
	}
	
}
