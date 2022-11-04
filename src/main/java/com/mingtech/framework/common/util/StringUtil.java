package com.mingtech.framework.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 字符串工具类
 * 
 * @author hexin@
 * @since Jun 13, 2008
 */
public class StringUtil extends StringUtils {

	private static Logger logger = Logger.getLogger(StringUtil.class);
	public static final String COMMA = ",";
	public static final String DIV = "、";
	public static final String BLANK = " ";

	private StringUtil() {
	}

	/**
	 * 字符编码函数，把ISO8859-1编码的字符串转成GBK编码
	 * 
	 * @param str
	 *            字符串
	 * @author hexin@
	 * @return String 如果是null类型或者值为null(不分大小写)的字符串,一切返回空字符串,返回GBK编码的字符串
	 * @since Jun 13, 2008
	 */
	public static String getISOGBK(String chi) {
		String result = null;
		try {
			if (chi == null || chi.equals("null")) {
				result = null;
			} else {
				result = new String(chi.getBytes(CharacterEncoding.ISO_8859_1),
						CharacterEncoding.GBK);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("getGBKStr is error", e);
		}
		return result;
	}

	/**
	 * 字符编码函数，把ISO8859-1编码的字符串转成UTF-8编码
	 * 
	 * @param str
	 *            字符串
	 * @author hexin@
	 * @return String 如果是null类型或者值为null(不分大小写)的字符串,一切返回空字符串,返回GBK编码的字符串
	 * @since Jun 13, 2008
	 */
	public static String getISOUTF(String chi) {
		String result = null;
		try {
			if (chi == null || chi.equals("null")) {
				result = null;
			} else {
				result = new String(chi.getBytes(CharacterEncoding.ISO_8859_1),
						CharacterEncoding.UTF8);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("getGBKStr is error", e);
		}
		return result;
	}

	/**
	 * 字符编码函数，把GBK编码的字符串转成ISO8859-1编码
	 * 
	 * @param text
	 *            字符串
	 * @author hexin@
	 * @return String 如果是null类型或者值为null(不分大小写)的字符串,一切返回空字符串
	 * @since Jun 13, 2008
	 */
	public static String getGBKISO(String text) {
		String result = "";
		try {
			result = new String(text.getBytes(CharacterEncoding.GBK),
					CharacterEncoding.ISO_8859_1);
		} catch (UnsupportedEncodingException e) {
			logger.error("getGBKStr is error", e);
		}
		return result;
	}

	/**
	 * MD5编码初始化方法,取得MessageDigest实例
	 * 
	 * @author hexin@
	 * @return MessageDigest 实例
	 * @since Jun 17, 2008
	 */
	private static MessageDigest getMD5DigestAlgorithm()
			throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("MD5");
	}

	/**
	 * 取得二进制信息摘要
	 * 
	 * @param source
	 *            取得摘要的二进制参数
	 * @author hexin@
	 * @return String byte[]数组
	 * @since Jun 17, 2008
	 */
	private static byte[] getMD5Digest(byte[] source)
			throws NoSuchAlgorithmException {
		return getMD5DigestAlgorithm().digest(source);
	}

	/**
	 * 取得二进制信息摘要
	 * 
	 * @param source
	 *            取得摘要的字符串参数
	 * @author hexin@
	 * @return String byte[]数组
	 * @since Jun 17, 2008
	 */
	private static byte[] getMD5Digest(String source)
			throws NoSuchAlgorithmException {
		return getMD5Digest(source.getBytes());
	}

	/**
	 * 把字符进行MD5加密算法编码
	 * 
	 * @param source
	 *            准备编码的字符串
	 * @author hexin@
	 * @return String 返回MD5编码以后的字符串
	 * @since Jun 17, 2008
	 */
	public static String getMD5DigestHex(String source) {
		String tmp = "";
		try {
			tmp = new String(Hex.encodeHex(getMD5Digest(source)));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(),e);
		}
		return tmp;
	}

	/**
	 * 判断传入字符是否包含中文字符
	 * 
	 * @param str
	 *            待判断的字符
	 * @return boolean 不包含中文返回false,包含中文返回true
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	public static boolean isChiness(String str) {
		String pattern = "[\u4e00-\u9fa5]+";
		Pattern p = Pattern.compile(pattern);
		Matcher result = p.matcher(str);
		return result.find();
	}

	/**
	 * 将传入字符串转换UTF-8编码
	 * 
	 * @param str
	 *            待转换的字符
	 * @return String 转换成UTF-8编码的字符串
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	public static String utf8Code(String str) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes(CharacterEncoding.UTF8);
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
						k += 256;
					}
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	/**
	 * UTF-8解码，把UTF-8编码字符解码
	 * 
	 * @param str
	 *            待转换的字符
	 * @return String UTF-8编码字符解码
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	public static String utf8deCode(String str) {
		String result = "";
		int p = 0;

		if (str != null && str.length() > 0) {
			str = str.toLowerCase();
			p = str.indexOf("%e");
			if (p == -1) {
				return str;
			}
			while (p != -1) {
				result += str.substring(0, p);
				str = str.substring(p, str.length());

				if (str == "" || str.length() < 9) {
					return result;
				}

				result += CodeToWord(str.substring(0, 9));
				str = str.substring(9, str.length());
				p = str.indexOf("%e");
			}
		}
		return result + str;
	}

	/**
	 * 判断给定字符是否UTF-8编码
	 * 
	 * @param str
	 *            待转换的字符
	 * @return boolean 判断是否utf-8编码,是返回true,否返回false
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	public static boolean isUtf8(String str) {
		str = str.toLowerCase();
		int p = str.indexOf("%");
		if (p != -1 && str.length() - p > 9) {
			str = str.substring(p, p + 9);
		}
		return Utf8codeCheck(str);
	}

	/**
	 * utf-8编码转成字符
	 * 
	 * @param text
	 *            待转换的字符
	 * @return String 转换完成以后的字符
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	private static String CodeToWord(String text) {
		String result;

		if (Utf8codeCheck(text)) {
			byte[] code = new byte[3];
			code[0] = (byte) (Integer.parseInt(text.substring(1, 3), 16) - 256);
			code[1] = (byte) (Integer.parseInt(text.substring(4, 6), 16) - 256);
			code[2] = (byte) (Integer.parseInt(text.substring(7, 9), 16) - 256);
			try {
				result = new String(code, CharacterEncoding.UTF8);
			} catch (UnsupportedEncodingException ex) {
				result = null;
			}
		} else {
			result = text;
		}
		return result;
	}

	/**
	 * 判断给定字符编码是否有效
	 * 
	 * @param text
	 *            待判断的字符
	 * @return boolean 有效返回true,无效返回false
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	private static boolean Utf8codeCheck(String text) {
		String sign = "";
		if (text.startsWith("%e")) {
			for (int i = 0, p = 0; p != -1; i++) {
				p = text.indexOf("%", p);
				if (p != -1)
					p++;
				sign += p;
			}
		}
		return sign.equals("147-1");
	}

	/**
	 * 判断给定字符是否为数字
	 * 
	 * @param str
	 *            待判断的字符
	 * @return boolean 是数字返回true,否则返回false
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	public static boolean isNumLegal(String str) {
		if (str == null) {
			return false;
		}

		for (int i = 0; i < str.getBytes().length; i++) {
			char ch = str.charAt(i);
			if (ch < '0' || ch > '9')
				return false;
		}
		return true;
	}

	/**
	 * 判断给定字符是否为日期格式（必须符合yyyy-MM-dd格式)
	 * 
	 * @param str
	 *            待判断的字符
	 * @return boolean 是日期格式返回true,否则返回false
	 * @author hexin@
	 * @since Jun 17, 2008
	 */
	public static boolean isDateLegal(String dateStr) {
		if (dateStr == null) {
			return false;
		}
		if (dateStr.length() != 10) {
			return false;
		}

		String strArr[] = dateStr.split("-");
		if (strArr.length != 3) {
			return false;
		}
		for (int i = 0; i < strArr.length; i++) {
			if (!isNumLegal(strArr[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 格式化定长字符串,前面不足补'0'
	 * 
	 * @param str
	 *            要格式化的字符串
	 * @param size
	 *            格式化后的位数
	 * @return
	 */
	public static String formateString(String str, int size) {
		return formateString(str, size, "0");
	}

	/**
	 * 格式化定长字符串,前面不足补自定义的字符
	 * 
	 * @param str
	 *            要格式化的字符串
	 * @param size
	 *            格式化后的位数
	 * @param pattern
	 *            要填补的字符
	 * @return
	 */
	public static String formateString(String str, int size, String pattern) {
		if (StringUtils.isBlank(str))
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = size; i > str.length(); i--) {
			sb.append(pattern);
		}
		sb.append(str);
		return sb.toString();
	}

	/**
	 * 随机生成一个UUID字符串
	 * 
	 * @return
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 将一个字符串数据拼成一个逗号间隔的字符串，例如：
	 * <p>
	 * <code>
	 * String [] arg = {"1", "2", "3"};
	 * <p>
	 * String test = StringUtilTools.getStringFromArray(arg);
	 * <p>
	 * test = "1,2,3";
	 * </code>
	 * 
	 * @param str
	 * @return
	 */
	public static String getStringFromArray(String[] str) {
		StringBuffer temp = new StringBuffer();
		if (str != null) {
			for (int i = 0; i < str.length; i++) {
				if (i == 0) {
					temp.append(str[i]);
				} else {
					temp.append(COMMA + str[i]);
				}
			}
		}
		return temp.toString();
	}

	/**
	 * 将一个字符串数据拼成一个逗号间隔的字符串数组，例如：
	 * <p>
	 * <code>
	 * String　test = "1,2,3";
	 * <p>
	 * String []arg = StringUtilTools.getArrayFromString(test, ",");
	 * <p>
	 * 
	 * arg = {"1", "2", "3"};
	 * </code>
	 * 
	 * @param str
	 *            要分割的字符串
	 * @param order
	 *            分割规则采用正则表达式
	 * 
	 * @return
	 */
	public static String[] getArrayFromString(String str, String order) {
		String[] temp = null;
		if (str != null) {
			temp = str.split(order);
		}
		return temp;
	}
	/**
	 * 根据字符串 及传入的分隔符  
	 * 返回分割后的 List列表
	 * @param str 字符串
	 * @param order 分隔符
	 * @return
	 */
	public static List getListFromString(String str, String order) {
		String[] temp = null;
		if (str != null) {
			temp = str.split(order);
		}else{
			return null;
		}
		List retList = new ArrayList();
		for(int i=0;i<temp.length;i++){
			retList.add(temp[i]);
		}
		return retList;
	}
	/**
	 * 将一个字符串数据拼成一个逗号间隔的字符串数组，例如：
	 * 
	 * @param str
	 *            要分割的字符串
	 * @return
	 */
	public static String[] getArrayFromString(String str) {
		return getArrayFromString(str, DIV);
	}

	/**
	 * 得到一个类的全名：
	 * 
	 * @param cls
	 *            类名
	 * @return
	 */
	public static String getClass(Class cls) {
		return getArrayFromString(cls.toString(), BLANK)[1];
	}

	/**
	 * 将整数转换为中文小写字符串，各个数字依次转换， 比如整数102将被转换为"一○二"
	 * 
	 * @param number
	 *            整数
	 * @return 转换后的汉字小写字符串
	 */
	public static String getStringNumber(int number) {
		// 中文数字字符数组
		String[] chineseNumber = new String[] { "○", "一", "二", "三", "四", "五",
				"六", "七", "八", "九" };
		if (number < 0) {
			return "负" + getStringNumber(-number);
		} else if (number < 10) {
			return chineseNumber[number];
		} else {
			return getStringNumber(number / 10) + getStringNumber(number % 10);
		}
	}

	/**
	 * 将整数转换为中文的整数字符串，按汉语习惯的称呼各个数字依次转换， 比如整数20将被转换为"二十"
	 * 
	 * @param number
	 *            整数(暂不支持绝对值大于99的转换)
	 * @return 转换后的中文的整数字符串
	 */
	public static String getChineseNumber(int number) {
		// 中文数字字符数组
		String[] chineseNumber = new String[] { "零", "一", "二", "三", "四", "五",
				"六", "七", "八", "九" };
		// 中文单位数组
		String[] chineseUnit = new String[] { "", "十", "百", "千", "万", "十", "百",
				"千", "亿", "十", "百", "千" };

		// String sNumber = "";

		if (number < 0) {
			// 负几
			return "负" + getChineseNumber(-number);
		} else if (number < 10) {
			// 几
			return chineseNumber[number];
		} else if (number < 20) {
			if (number % 10 == 0) {
				// "十"
				return chineseUnit[1];
			} else {
				// 十几
				return chineseUnit[1] + chineseNumber[number % 10];
			}
		} else if (number < 100) {
			if (number % 10 == 0) {
				// 几十
				return chineseNumber[number / 10] + chineseUnit[1];
			} else {
				// 几十几
				return chineseNumber[number / 10] + chineseUnit[1]
						+ chineseNumber[number % 10];
			}
		} else {
			throw new java.lang.IllegalArgumentException("暂不支持绝对值大于99的转换");
		}
	}

	/**
	 * 随机生成指定位数且不重复的字符串.去除了部分容易混淆的字符，如1和l，o和0等，
	 * 
	 * 随机范围1-9 a-z A-Z
	 * 
	 * @param length
	 *            指定字符串长度
	 * @return 返回指定位数且不重复的字符串
	 */
	public static String getRandomString(int length) {
		StringBuffer bu = new StringBuffer();
		String[] arr = { "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c",
				"d", "e", "f", "g", "h", "i", "j", "k", "m", "n", "p", "q",
				"r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C",
				"D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "P",
				"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		Random random = new Random();
		while (bu.length() < length) {
			String temp = arr[random.nextInt(57)];
			if (bu.indexOf(temp) == -1)
				bu.append(temp);
		}
		return bu.toString();
	}

	/**
	 * 获取某个范围内的随机整数
	 * 
	 * @param sek
	 *            随机种子
	 * @param start
	 *            最小范围
	 * @param max
	 *            最大范围
	 * @return 整数
	 */
	public static int getRandomInt(int sek, int min, int max) {

		Random random = new Random();

		int temp = 0;

		do {
			temp = random.nextInt(sek);
		} while (temp < min || temp > max);

		return temp;
	}
	
	/**
	 * 判断字符串是否为空（NULL 或者 空字符串s）
	 * 
	 * @param obj
	 * @return
	 * @author huangshiqiang
	 * @date Dec 4, 2007
	 * @comment
	 */
	public static boolean isStringEmpty(String obj) {
		if (obj == null) {
			return true;
		} else {
			if (obj.trim().length() == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	* 方法说明: 截取报文标识号中的报文类型
	* @param msgId 报文标识号
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-5-15 上午11:38:57
	*/
	public static String subMsgType(String msgId){
		String msgType = "";
		if(!isStringEmpty(msgId.trim())){
			msgType = msgId.substring(20, 23);
		}
		return msgType;
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
	 *  * 将字符串 按照 给定的分割符 分隔成 String 类型的数组返回
	 * @param sourceStr
	 * @param spliterStr
	 * @return
	 */
	public static String[] splitArray(String sourceStr,String spliterStr){
		String[] tmp=null;
		if(sourceStr!=null && !sourceStr.trim().equalsIgnoreCase("")){
			 tmp= sourceStr.split(spliterStr); 
		}
		return tmp;
	}
	
	/**
	 * 将字符串数组按照给定的分割符组成字符串返回
	 * @param array
	 * @param spliter
	 * @return
	 */
	public static String arrayToString(String[] array,String spliter){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if(array[i].trim().length()>0){
				sb.append(array[i]);
				if(i<array.length-1)
					sb.append(spliter);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将字符串数组按照给定的分割符组成字符串返回
	 * @param array
	 * @param spliter
	 * @return
	 */
	public static String arrayObjectToString(Object[] array,String spliter){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if(((String)array[i]).trim().length()>0){
				sb.append(array[i]);
				if(i<array.length-1)
					sb.append(spliter);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 不支持中文字符串
	 * 将字符串oldStr扩充到长度toLength,如果超过长度则去除左边超过的部分,否则则在左边补上fillChar字符
	 * @param oldStr 	需要补充的原字符串
	 * @param toLength 	指定扩充到的长度
	 * @param fillChar	需要补上的字符
	 * @return  返回的处理后字串
	 * 
	 */
	public static String fillCharLeft(String oldStr,int toLength, char fillChar) {
		int length = oldStr.getBytes().length;
		if (length == toLength) {
			return oldStr;
		} else if (length < toLength) {
			for (int i = length; i < toLength; i++) {
				oldStr = fillChar + oldStr;
			}
			return oldStr;
		} else {
			return oldStr.substring(length - toLength);
		}
	}
	
	/**
	 * 不支持中文字符串
	 * 将字符串oldStr扩充到长度toLength，如果超过长度则去除左边超过的部分，否则右补空格
	 * @param oldStr 	需要补充的原字符串
	 * @param toLength  指定扩充到的长度
	 * @return 返回的处理后字串
	 * 
	 */
	public static String fillBlankRight(String oldStr,int toLength) {
		return fillCharRight(oldStr,toLength, ' ');
	}
	
	/**
	 * 不支持中文字符串
	 * 将字符串oldStr扩充到长度toLength,如果超过长度则去除左边超过的部分,否则则在后面补上fillChar字符
	 * @param oldStr 	需要补充的原字符串
	 * @param toLength  指定扩充到的长度
	 * @param fillChar  补上的字符
	 * @return 返回的处理后字串
	 * 
	 */
	public static String fillCharRight(String oldStr,int toLength, char fillChar) {
		int length = oldStr.getBytes().length;
		if (length == toLength) {
			return oldStr;
		} else if (length < toLength) {
			for (int i = length; i < toLength; i++) {
				oldStr = oldStr + fillChar;
			}
			return oldStr;
		} else {
			return oldStr.substring(length - toLength);
		}
	}
	
	/**
	 * 不支持中文字符串
	 * 将字符串oldStr扩充到长度toLength，如果超过长度则去除左边超过的部分，否则左补0
	 * @param oldStr 	需要补充的原字符串
	 * @param toLength  指定扩充到的长度
	 * @return 返回的处理后字串
	 * 
	 */
	public static String fillZeroLeft(String oldStr,int toLength) {
		return fillCharLeft(oldStr,toLength, '0');
	}
	/**
	 * 如果字符串不为null，那么就在两面加上''， 如果字符串为""，那么就把它设为null，该方法一般用于书写Sql语句
	 * 
	 * @param originalStr  传入的字串
	 *            
	 * @return 返回的字串
	 */
	public static String settingSingleQuotationMark(String originalStr) {
		if (originalStr != null) {
			if (!originalStr.equals("")) {
				originalStr = "'" + originalStr + "'";
			} else {
				originalStr = "NULL";
			}
		}
		return originalStr;
	}
	
	public static BigDecimal getBigDecimalVal(Object obj) throws Exception {
		BigDecimal value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = new BigDecimal(temp);
			}
		}
		return value;
	}
	
	public static String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null && !"null".equals(obj)) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}

	public static Date getDateVal(Object obj) throws Exception {
		Date value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
			}
		}
		return value;
	}
	
	/**
	 * @Title compareString
	 * @author wss
	 * @date 2021-6-18
	 * @Description 判断两个字符串是否相等
	 * @return boolean
	 */
	public static boolean compareString(String str1,String str2) {
		boolean flag = false;
		if(null == str1){
			if(null == str2){
				flag = true;
			}
		}else{
			if(null != str2){
				if(str1.equals(str2)){
					flag = true;
				}
			}
		}
		return flag;
	}
	/**
	 * @Title compareBigdecimal
	 * @author wss
	 * @date 2021-6-18
	 * @Description 判读两个参数是否相等
	 * @return boolean
	 */
	public static boolean compareBigdecimal(BigDecimal b1,
			BigDecimal b2) {
		boolean flag = false;
		if(null == b1){
			if(null == b2){
				flag = true;
			}
		}else{
			if(null != b2){
				if(b1.equals(b2)){
					flag = true;
				}
			}
		}
		return flag;
	}
}
