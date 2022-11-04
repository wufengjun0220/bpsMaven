package com.mingtech.application.pool.bank.codec;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.com.bobj.frontend.EncryptionKeyer;

import com.mingtech.application.pool.bank.message.Constants;


/**
 * 加密工具类，使用前置系统提供的加密算法
 */
public class EncryptUtil{
	private static final Logger logger = Logger.getLogger(EncryptUtil.class);
	/**
	 * 加密报文，首先需要根据报文体的字节长度构建报头，然后将报头和报文体分别进行加密
	* <p>方法名称: encrypt|描述: </p>
	* @param message 报文体
	* @return
	* @throws Exception
	 */
	public static byte [] encryptMessage(String message) throws Exception
	{
		//构建报头
		String header = Integer.toString(message.toString().getBytes().length);
		header = StringUtils.leftPad(header,Constants.MSG_HEAD_LEN,'0');
		
		//加密
		EncryptionKeyer encoder = new EncryptionKeyer();
		byte [] bHeader = encoder.addKeyToByte(header.getBytes("GBK"));
		byte [] bBody = encoder.addKeyToByte(message.getBytes("GBK"));
		
		//合并报文
		byte [] content = new byte[bHeader.length + bBody.length];
		System.arraycopy(bHeader, 0, content, 0, bHeader.length);
		System.arraycopy(bBody, 0, content, bHeader.length, bBody.length);
		return content;
	}
	
	/**
	 * 解密报文
	* <p>方法名称: encrypt|描述: </p>
	* @param bytes 报文体字节数组,不包括报文头
	* @return
	* @throws Exception
	 */
	public static String decryptMessage(byte [] bytes) throws Exception
	{

		EncryptionKeyer encoder = new EncryptionKeyer();
		encoder.subkey(bytes, bytes.length);
		return new String(bytes,"GBK");

	}
	/**
	 * 加密字符串
	* <p>方法名称: encrypt|描述: </p>
	* @param source 需要被加密的字符串
	* @return
	* @throws Exception
	 */
	public static byte [] encrypt(String source) throws Exception
	{
		try{
			byte [] data = source.getBytes("GBK");
			EncryptionKeyer encoder = new EncryptionKeyer();
			data = encoder.addKeyToByte(data);
			return data;
			
		}catch (UnsupportedEncodingException e){
			logger.error("加密失败!",e);
			throw e;
		}		
	}	
	
	/**
	 * 解密字符数组
	* <p>方法名称: decrypt|描述: </p>
	* @param source 需要解密的字节数组
	* @return
	* @throws Exception
	 */
	public static String decrypt(byte [] bytes) throws Exception
	{
		try{
			EncryptionKeyer encoder = new EncryptionKeyer();
			encoder.subkey(bytes, bytes.length);
			return new String(bytes,"GBK");
			
		}catch (UnsupportedEncodingException e){
			logger.error("解密失败!",e);
			throw e;
		}		
	}	
}
