package com.mingtech.framework.common.util;

import java.math.BigDecimal;


/**
 * 数学精度工具
 *
 */
public class MathScaleUtil {
	/*
	 * 默认除法运算精度
	 */
	private static final int DEFAULT_DIV_SCALE = 10;

	public static final Double DOUBLE_ZERO = Double.valueOf("0.00");
	
	public static final String STRING_ZERO = "0.00";
	
	/**
	 * 提供精确的加法运算
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的加法运算
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的和，以字符串格式返回
	 */
	public static String add(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.add(b2).toString();
	}

	/**
	 * 提供精确的减法运算
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的差
	 */
	public static double subtract(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		double b = b1.subtract(b2).doubleValue();
		if(b == 0.00){
			b = 0.00;
		}
		return b;
	}

	/**
	 * 提供精确的减法运算
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的差，以字符串格式返回
	 */
	public static String subtract(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.subtract(b2).toString();
	}

	/**
	 * 提供精确的乘法运算
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的积
	 */
	public static double multiply(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的积，以字符串格式返回
	 */
	public static String multiply(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).toString();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到小数点以后10位，以后的数字四舍五入，舍入模式采用ROUND_HALF_UP
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的商
	 */
	public static double divide(double v1, double v2) {
		return divide(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入，舍入模式采用ROUND_HALF_UP
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @param scale 小数点后保留几位
	 * @return 两个参数的商
	 */
	public static double divide(double v1, double v2, int scale) {
		return divide(v1, v2, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入，舍入模式采用用户指定舍入模式
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @param scale 小数点后保留几位
	 * @param round_mode 指定的舍入模式
	 * @return 两个参数的商
	 */
	public static double divide(double v1, double v2, int scale, int round_mode) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, round_mode).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到小数点以后10位，以后的数字四舍五入，舍入模式采用ROUND_HALF_UP
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @return 两个参数的商，以字符串格式返回
	 */
	public static String divide(String v1, String v2) {
		return divide(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入，舍入模式采用ROUND_HALF_UP
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @param scale 小数点后保留几位
	 * @return 两个参数的商，以字符串格式返回
	 */
	public static String divide(String v1, String v2, int scale) {
		return divide(v1, v2, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入，舍入模式采用用户指定舍入模式
	 * 
	 * @param v1 参数1
	 * @param v2 参数2
	 * @param scale 小数点后保留几位
	 * @param round_mode 指定的舍入模式
	 * @return 两个参数的商，以字符串格式返回
	 */
	public static String divide(String v1, String v2, int scale, int round_mode) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, scale, round_mode).toString();
	}

	/**
	 * 提供精确的小数位四舍五入处理，舍入模式采用ROUND_HALF_UP
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		return round(v, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位四舍五入处理，舍入模式采用用户指定舍入模式
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @param round_mode 指定的舍入模式
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale, int round_mode) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		
		String s = Double.toString(v);
		try {
			BigDecimal b = new BigDecimal(s);
			return b.setScale(scale, round_mode).doubleValue();
		} catch (RuntimeException re) {
			System.err.println("BigDecimal exception value: " + s + ", scale: " + scale + ", round_mode: " + round_mode);
			throw re;
		}
	}

	/**
	 * 提供精确的小数位四舍五入处理，舍入模式采用ROUND_HALF_UP
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果，以字符串格式返回
	 */
	public static String round(String v, int scale) {
		return round(v, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位四舍五入处理，舍入模式采用用户指定舍入模式
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @param round_mode 指定的舍入模式
	 * @return 四舍五入后的结果，以字符串格式返回
	 */
	public static String round(String v, int scale, int round_mode) {
		if (v == null) {
			return null;
		}

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		try {
			BigDecimal b = new BigDecimal(v);
			return b.setScale(scale, round_mode).toString();
		} catch (RuntimeException re) {
			System.err.println("BigDecimal exception value: " + v + ", scale: " + scale + ", round_mode: " + round_mode);
			throw re;
		}
	}
	
	/**
	 * 提供精确的小数位四舍五入处理，舍入模式采用ROUND_HALF_UP
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果，以Double格式返回
	 */
	public static Double round(Double v, int scale) {
		return round(v, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位四舍五入处理，舍入模式采用用户指定舍入模式
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @param round_mode 指定的舍入模式
	 * @return 四舍五入后的结果，以Double格式返回
	 */
	public static Double round(Double v, int scale, int round_mode) {
		if (v == null) {
			return null;
		}

		return new Double(round(v.doubleValue(), scale, round_mode));
	}
	
	/**
	 * value是否大于等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isMoreThan(double value, double baseValue) {
		if (compareTo(value, baseValue) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * value是否大于等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isMoreThan(String value, String baseValue) {
		if (compareTo(value, baseValue) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否大于等于1分钱（2位精度处理）
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isMoreThanOne(double value) {
		return isMoreThan(value, 0.01);
	}

	/**
	 * 是否大于等于1分钱（2位精度处理）
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isMoreThanOne(String value) {
		return isMoreThan(value, "0.01");
	}

	/**
	 * value是否小于等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isLessThan(double value, double baseValue) {
		if (compareTo(value, baseValue) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * value是否小于等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isLessThan(String value, String baseValue) {
		if (compareTo(value, baseValue) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * value是否小于等于0.00（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isLessThanZero(double value) {
		return isLessThan(value, 0.00);
	}

	/**
	 * value是否小于等于0.00（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isLessThanZero(String value) {
		return isLessThan(value, "0.00");
	}

	/**
	 * value是否等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isEqual(double value, double baseValue) {
		if (compareTo(value, baseValue) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * value是否等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isEqual(String value, String baseValue) {
		if (compareTo(value, baseValue) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * value是否不等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isNotEqual(double value, double baseValue) {
		if (compareTo(value, baseValue) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * value是否不等于baseValue（2位精度处理）
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static boolean isNotEqual(String value, String baseValue) {
		if (compareTo(value, baseValue) != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 与0比较，相等返回true（2位精度处理）
	 * 
	 * @param value
	 * @return
	 */
	public static boolean compareWithZero(double value) {
		return isEqual(value, 0.00);
	}

	/**
	 * 与0比较，相等返回true（2位精度处理）
	 * 
	 * @param value
	 * @return
	 */
	public static boolean compareWithZero(String value) {
		return isEqual(value, "0.00");
	}

	/**
	 * 与1000000000比较，大于1000000000则返回true（2位精度处理）
	 * 
	 * @param value
	 * @return
	 */
	public static boolean compareWithBillion(String value) {
		if (compareTo(value, "1000000000.00") > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 当取完精度后，value在数字上小于、等于或大于baseValue时，返回-1、0或1
	 * 
	 * @param value
	 * @param baseValue
	 * @param scale
	 * @return
	 */
	public static int compareTo(double value, double baseValue, int scale) {
		BigDecimal v1 = new BigDecimal(value).setScale(scale,
				BigDecimal.ROUND_HALF_UP);
		BigDecimal v2 = new BigDecimal(baseValue).setScale(scale,
				BigDecimal.ROUND_HALF_UP);

		return v1.compareTo(v2);
	}

	/**
	 * 当取完2位精度后，value在数字上小于、等于或大于baseValue时，返回-1、0或1
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static int compareTo(double value, double baseValue) {
		return compareTo(value, baseValue, 2);
	}

	/**
	 * 当取完精度后，value在数字上小于、等于或大于baseValue时，返回-1、0或1
	 * 
	 * @param value
	 * @param baseValue
	 * @param scale
	 * @return
	 */
	public static int compareTo(String value, String baseValue, int scale) {
		BigDecimal v1 = new BigDecimal(value).setScale(scale,
				BigDecimal.ROUND_HALF_UP);
		BigDecimal v2 = new BigDecimal(baseValue).setScale(scale,
				BigDecimal.ROUND_HALF_UP);

		return v1.compareTo(v2);
	}

	/**
	 * 当取完2位精度后，value在数字上小于、等于或大于baseValue时，返回-1、0或1
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static int compareTo(String value, String baseValue) {
		return compareTo(value, baseValue, 2);
	}
	
	/**
	 * 原始值比较（未进行精度处理），value在数字上小于、等于或大于baseValue时，返回-1、0或1
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static int simpleCompareTo(double value, double baseValue) {
		BigDecimal v1 = new BigDecimal(value);
		BigDecimal v2 = new BigDecimal(baseValue);

		return v1.compareTo(v2);
	}

	/**
	 * 原始值比较（未进行精度处理），value在数字上小于、等于或大于baseValue时，返回-1、0或1
	 * 
	 * @param value
	 * @param baseValue
	 * @return
	 */
	public static int simpleCompareTo(String value, String baseValue) {
		BigDecimal v1 = new BigDecimal(value);
		BigDecimal v2 = new BigDecimal(baseValue);

		return v1.compareTo(v2);
	}
	
	/**
	 * 银承专用，计算未用退回释放金额
	 * 
	 * @param billAmt 票面金额
	 * @param freezeTotalGrantAmt 汇总实际圈存金额
	 * @param totalAmtForDeduct 到期应扣款总金额
	 * @return 应释放金额
	 */
	public static double getFreeGrantAmt(Double billAmt, Double freezeTotalGrantAmt, Double totalAmtForDeduct) {
		if (compareWithZero(freezeTotalGrantAmt)) {
			return 0.0;
		}

		BigDecimal vBillAmt = new BigDecimal(billAmt);
		BigDecimal vFreezeTotalGrantAmt = new BigDecimal(freezeTotalGrantAmt);
		BigDecimal vTotalAmtForDeduct = new BigDecimal(totalAmtForDeduct);
		// 释放金额＝票面金额×保证金比例＝票面金额×(汇总实际圈存金额/到期应扣款总金额)
		return vFreezeTotalGrantAmt.divide(vTotalAmtForDeduct, DEFAULT_DIV_SCALE,
				BigDecimal.ROUND_HALF_UP).multiply(vBillAmt).doubleValue();
	}
}