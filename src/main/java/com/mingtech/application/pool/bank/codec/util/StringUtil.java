package com.mingtech.application.pool.bank.codec.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
* @作者: mjw
* @日期: 2009-9-9 上午09:21:58
* @描述: [StringUtil]处理和字符串有关的通用方法
 */
public class StringUtil extends StringUtils{
	
	/**
	 * 按照字节对指定字符串进行右补位 <p>方法名称: rightPad|描述: </p>
	 * @param oldStr 需要补位的字符串
	 * @param toLength 总字节长度
	 * @param fillChar 需要补位的字符
	 * @return
	 */
	public static String rightPad(String oldStr, int toLength, char fillChar){
		int length = oldStr.getBytes().length;
		if(length == toLength){
			return oldStr;
		}else if(length < toLength){
			for(int i = length; i < toLength; i++){
				oldStr = oldStr + fillChar;
			}
			return oldStr;
		}else
			throw new RuntimeException("字符串" + oldStr + "长度超出指定长度" + toLength);
	}

	/**
	 * 按照字节对指定字符串进行左补位 <p>方法名称: leftPad|描述: </p>
	 * @param oldStr 需要补位的字符串
	 * @param toLength 总字节长度
	 * @param fillChar 需要补位的字符
	 * @return
	 */
	public static String leftPad(String oldStr, int toLength, char fillChar){
		int length = oldStr.getBytes().length;
		if(length == toLength){
			return oldStr;
		}else if(length < toLength){
			for(int i = length; i < toLength; i++){
				oldStr = fillChar + oldStr;
			}
			return oldStr;
		}else
			throw new RuntimeException("字符串" + oldStr + "长度超出指定长度" + toLength);
	}
	
	/**
	* <p>方法名称: isEmpty|描述: 判断字符串是否为空</p>
	* @param str
	* @return
	 */
	public static boolean isEmpty(String str){
		
		if(str == null || "".equals(str)){
			return true;
		}else{
			return false;
		}
		
	}
	
	
	/**
	 * 将字符串 按照 给定的分割符 分隔成list 对象返回
	 * @param sourceStr
	 * @param spliterStr
	 * @return
	 */
	
	public static List splitList(String sourceStr,String spliterStr){
		List list=null; 
		if(sourceStr!=null && !sourceStr.trim().equalsIgnoreCase("")){
			String[] tmp= sourceStr.split(spliterStr);
			if(tmp.length>0){
				list=new ArrayList();
				for(int i=0;i<tmp.length;i++){
					list.add(tmp[i]);
				}
			}
		}
		return list;
	}
	/**
	 * @Title compareString
	 * @author wss
	 * @date 2021-6-18
	 * @Description 判断是否相等
	 * @return boolean
	 */
	public static boolean compareString(String str1,String str2) {
		boolean flag = false;
		if(null == str1){
			if(null == str2){
				flag = true;
			}else{
				flag = false;
			}
		}else{
			if(null == str2){
				flag = false;
			}else{
				if(str1.equals(str2)){
					flag = true;
				}else{
					flag = false;
				}
			}
		}
		return flag;
	}

}
