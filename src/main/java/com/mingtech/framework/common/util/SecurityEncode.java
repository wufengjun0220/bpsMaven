package com.mingtech.framework.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

/**
 * 安全加密类
 * 
 * @date 2007-10-31
 * 
 */
public class SecurityEncode {

	/**
	 * MD5方式加密字符串
	 * 
	 * @param str
	 *            要加密的字符串
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @date 2007-10-31
	 * @comment 程序的价值体现在两个方面:它现在的价值,它未来的价值
	 */
	public static String EncoderByMd5(String str) {
		try {
			// 确定计算方法
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			BASE64Encoder base64en = new BASE64Encoder();
			// 加密后的字符串
			String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
			return newstr;
		} catch (Exception ex) {
			return "加密失败！";
		}
	}
	
	public static void main(String args[]){
	}
}