package com.mingtech.application.pool.common.util;

import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;

/**
 *
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
* @作者: yufei
* @日期: Jul 3, 2009 2:30:17 PM
* @描述: [BigDecimalUtils]大数字的数值计算类
 */

public class BigDecimalUtils{

	public static final BigDecimal ZERO = new BigDecimal("0");
	public static final BigDecimal ONE = new BigDecimal("1");

	/* 手续费计算 */
	public static BigDecimal euHdlChgCompute(BigDecimal billMoney)
			throws Exception{
		BigDecimal hdlChage = null;
		if(billMoney == null){// 如果参数为空
			throw new Exception("【错误：票面金额不能为空！】");
		}else if(billMoney.compareTo(new BigDecimal(0)) == 0){// 如果票据金额为0
			throw new Exception("【错误：票面金额不能为0!】");
		}else if(billMoney.compareTo(new BigDecimal(0)) < 0){
			throw new Exception("【错误：票面金额不能小于0!】");
		}
		hdlChage = multiply(billMoney,new BigDecimal(0.0005));
//			billMoney.multiply(new BigDecimal(0.0005)).compareTo(
//				new BigDecimal(50)) > 0 ? billMoney.multiply(new BigDecimal(
//				0.0005)) : new BigDecimal(50);
		return hdlChage;
	}

	/**
	 * 两数相乘，四舍五入，保留2位小数
	 * @param one
	 * @param two
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal one, BigDecimal two){
		return multiply(one, two, 2);
	}
	
	
	/**
	 * 两数相乘，四舍五入，保留2位小数
	 * @param one
	 * @param two
	 * @return
	 */
	public static BigDecimal multiply2(BigDecimal one, BigDecimal two){
		return multiply(one, two, 0);
	}

	
	
	/**
	 * 两数相乘，四舍五入，保留3位小数
	 * @param one
	 * @param two
	 * @return
	 */
	public static BigDecimal multiply3(BigDecimal one, BigDecimal two){
		return multiply(one, two, 3);
	}
	

	/**
	 * 两数相乘，四舍五入
	 * @param one
	 * @param two
	 * @param scale 保留位数
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal one, BigDecimal two, int scale){
		return one.multiply(two).setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 三数相乘，四舍五入，保留2位小数
	 * @param one
	 * @param two
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal one, BigDecimal two,
			BigDecimal three){
		return one.multiply(two).multiply(three).setScale(2,
				BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal divide(BigDecimal numerator, BigDecimal denominator){
		return divide(numerator, denominator, 2);
	}

	public static BigDecimal divide(BigDecimal numerator,
			BigDecimal denominator, int scale){
		return numerator.divide(denominator, scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal setScale(BigDecimal decimal){
		return decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal setScale(int scale, BigDecimal decimal){
		return decimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 解析String为BigDecimal，如果str为空返回0，如果抛出NumberFormatException，返回0
	 * @param str 需要解析的数字
	 * @return BigDecimal
	 */
	public static BigDecimal valueOf(String str){
		return valueOf(str, ZERO);
	}

	/**
	 * 解析String为BigDecimal，如果str为空返回0，如果抛出NumberFormatException，返回default
	 * @param str 需要解析的数字
	 * @param defaultValue
	 * @return BigDecimal
	 */
	public static BigDecimal valueOf(String str, BigDecimal defaultValue){
		if(StringUtils.isEmpty(str)){
			return defaultValue;
		}
		try{
			return new BigDecimal(str);
		}catch (NumberFormatException ex){
			return defaultValue;
		}
	}
	public static BigDecimal min(BigDecimal b1,BigDecimal b2){
		if (b1.compareTo(b2)>0){
			return b2;
		}else{
			return b1;
		}
	}
	public static void main(String[] args){
		String str1 = "000000000020";
		String str2 = "000000000070";
		
		BigDecimal money1 = new BigDecimal(str1);
		BigDecimal money2 = new BigDecimal(str2);
		
		
	 System.out.println(money1.compareTo(money2));
	}
}
