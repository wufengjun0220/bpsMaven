package com.mingtech.application.pool.report.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 报表公共静态字段和方法
 * 
 * @author zhuchuanyong
 * 
 */
public class ReportUtils {


	// =========================== 余额维度 ==================================

	/**
	 * 票据类型维度
	 */
	public static final String DIM_CODE_BILL_TYPE = "D_001";
	public static final String DIM_NAME_BILL_TYPE = "票据类型";
	public static final String DIM_ITEM_CODE_BK_ACPT = "D_001001";
	public static final String DIM_ITEM_NAME_BK_ACPT = "银行承兑汇票";
	public static final String DIM_ITEM_CODE_CM_ACPT = "D_001002";
	public static final String DIM_ITEM_NAME_CM_ACPT = "商业承兑汇票";

	/**
	 * 票据期限维度
	 */
	public static final String DIM_CODE_BILL_TERM = "D_002";
	public static final String DIM_NAME_BILL_TERM = "票据期限";
	public static final String DIM_ITEM_CODE_LS_THN_3M = "D_002001";
	public static final String DIM_ITEM_NAME_LS_THN_3M = "3个月（含）以内";
	public static final String DIM_ITEM_CODE_3M_TO_6M = "D_002002";
	public static final String DIM_ITEM_NAME_3M_TO_6M = "3个月—6个月";
	public static final String DIM_ITEM_CODE_GT_THN_6M = "D_002003";
	public static final String DIM_ITEM_NAME_GT_THN_6M = "6个月以上";

	/**
	 * 行业分类维度
	 */
	public static final String DIM_CODE_INDUST_TYPE = "D_003";
	public static final String DIM_NAME_INDUST_TYPE = "行业分类";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_01 = "D_003001";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_01 = "农、林、牧、渔业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_02 = "D_003002";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_02 = "采矿业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_03 = "D_003003";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_03 = "制造业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_04 = "D_003004";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_04 = "电力、燃气及水的生产和供应业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_05 = "D_003005";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_05 = "建筑业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_06 = "D_003006";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_06 = "交通运输、仓储和邮政业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_07 = "D_003007";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_07 = "信息传输、计算机服务和软件业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_08 = "D_003008";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_08 = "批发和零售业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_09 = "D_003009";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_09 = "住宿和餐饮业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_10 = "D_003010";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_10 = "金融业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_11 = "D_003011";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_11 = "房地产业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_12 = "D_003012";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_12 = "租赁和商务服务业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_13 = "D_003013";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_13 = "科学研究、技术服务和地质勘查业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_14 = "D_003014";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_14 = "水利、环境和公共设施管理业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_15 = "D_003015";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_15 = "居民服务和其他服务业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_16 = "D_003016";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_16 = "教育";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_17 = "D_003017";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_17 = "卫生、社会保障和社会福利业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_18 = "D_003018";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_18 = "文化、体育和娱乐业";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_19 = "D_003019";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_19 = "公共管理和社会组织";
	public static final String DIM_ITEM_CODE_INDUST_TYPE_20 = "D_003020";
	public static final String DIM_ITEM_NAME_INDUST_TYPE_20 = "国际组织";
	
	

	/** 
	 * 业务类型：承兑
	 */
	public static final String BUSI_TYPE_ACPT = "A";

	/**
	 * 业务类型：买断式贴现
	 */
	public static final String BUSI_TYPE_DISC = "B";

	/**
	 * 业务类型：回购式贴现
	 */
	public static final String BUSI_TYPE_BUYBACK_DISC = "C";

	/**
	 * 业务类型：买断式转入
	 */
	public static final String BUSI_TYPE_REDISC_BUYIN = "D";

	/**
	 * 业务类型：买断式转出
	 */
	public static final String BUSI_TYPE_REDISC_SELLOUT = "E";

	/**
	 * 业务类型：买入返售票据
	 */
	public static final String BUSI_TYPE_REDISC_RESELL = "F";

	/**
	 * 业务类型：卖出回购票据
	 */
	public static final String BUSI_TYPE_REDISC_REBUY = "G";

	/**
	 * 业务类型：卖断式再贴现
	 */
	public static final String BUSI_TYPE_CBREDISC = "H";

	/**
	 * 业务类型：回购式再贴现
	 */
	public static final String BUSI_TYPE_BUYBACK_CBREDISC = "I";

	/**
	 * 业务类型：买入央行票据
	 */
	public static final String BUSI_TYPE_CBREDISC_BUYIN = "J";

	/**
	 * 业务类型：到期提示付款
	 */
	public static final String BUSI_TYPE_DUE_PAYOFF = "K";
	
	/**
	 * PropertyObject对象中propertyValueType的取值
	 */
	public static final String BIGDECIAML = "bigdeciaml"; // 金额型
	public static final String INT = "int"; // 数值型
	public static final String STRING = "string"; // 字符串型
	public static final String DATE = "date"; //日期类型
	
	/**
	 * PropertyObject对象中propertyName和propertyId的取值
	 */
	public static final String BANKNAMEKEY = "bankNameKey";
	public static final String BANKNAMEVALUE = "行名";
	public static final String CASHDEPOSITISSEAMTKEY = "cashDepositIsseAmtKey";
	public static final String CASHDEPOSITISSEAMTVALUE = "保证金余额";
	public static final String DEPOSITRECEIPTISSEAMTKEY = "depositReceiptIsseAmtKey";
	public static final String DEPOSITRECEIPTISSEAMTVALUE = "存单质押余额";
	public static final String OTHERISSEAMTKEY = "otherIsseAmtKey";
	public static final String OTHERISSEAMTVALUE = "其他权利余额";
	public static final String LINEOFCREDITISSEAMTKEY = "lineOfCreditIsseAmtKey";
	public static final String LINEOFCREDITISSEAMTVALUE = "敞口余额";
	public static final String ISSEAMTKEY = "isseAmtKey";
	public static final String ISSEAMTVALUE = "金额";
	public static final String LASTMONTH = "lastMonth";
	public static final String THISMONTH = "thisMonth";

	public static final String BUSINESSSIZE = "业务比数";	//业务比数
	public static final String LASTMONTHBALANCE = "上月余额";// 上月余额
	public static final String LASTMONTHBUSINESSSIZE = "上月比数";//上月业务比数
	public static final String DISCREPANCYAMT  = "增减额度";//相差金额
	public static final String TOTALAMTBYYEAR = "年累计发生额";
	public static final String BALANCEDIFFBYLASTYEAR = "余额比年初";
	
	public static final String ACCPTBALANCEKEY = "accptBalance";
	public static final String ACCPTBALANCEVALUE = "银承签发余额";
	public static final String CURRENTLYACCPTAMTKEY = "currentlyAccptAmt";
	public static final String CURRENTLYACCPTAMTVALUE = "银承当期签发额";
	
	public static final String DISCOUNTFINALBALANCEKEY = "discountFinalBalance";
	public static final String DISCOUNTFINALBALANCEVALUE = "票据贴现期末余额";
	public static final String DISCOUNTCURRENTLYAMTKEY = "discountCurrentlyAmt";
	public static final String DISCOUNTCURRENTLYAMTVALUE = "票据贴现当期发生额";
	

	public static final String ISSUERACCOUNTKEY = "IssuerAccountKey";
	public static final String ISSUERACCOUNTVALUE = "出票人账号";
	public static final String ISSUERBANKCODEKEY = "IssuerBankCodeKey";
	public static final String ISSUERBANKCODEVALUE = "出票人开户行行号";
	public static final String ISSUERBANKNAMEKEY = "IssuerBankNameKey";
	public static final String ISSUERBANKNAMEVALUE = "出票人开户行名称";
	
	public static final String DISCOUNTOUTACCOUNTKEY = "DiscountOutAccountKey";
	public static final String DISCOUNTOUTACCOUNTVALUE = "贴出人账号";
	public static final String DISCOUNTOUTBANKCODEKEY = "DiscountOutBankCodeKey";
	public static final String DISCOUNTOUTBANKCODEVALUE = "贴出人开户行行号";
	
	public static final String DISCOUNTINBANKCODEKEY = "DiscountInBankCodeKey";
	public static final String DISCOUNTINBANKCODEVALUE = "贴入行行号";
	public static final String DISCOUNTINBANKNAMEKEY = "DiscountInBankNameKey";
	public static final String DISCOUNTINBANKNAMEVALUE = "贴入行行名";
	
	public static final String REDISCOUNTOUTACCOUNTKEY = "RediscountOutAccountKey";
	public static final String REDISCOUNTOUTACCOUNTVALUE = "转贴现贴出人账号";
	public static final String REDISCOUNTOUTBANKCODEKEY = "RediscountOutBankCodeKey";
	public static final String REDISCOUNTOUTBANKCODEVALUE = "转贴现贴出人开户行行号";
	
	public static final String REDISCOUNTWITHCENTRALOUTACCOUNTKEY = "RediscountWithCentralOutAccountKey";
	public static final String REDISCOUNTWITHCENTRALOUTACCOUNTVALUE = "再贴现贴出人账号";
	public static final String REDISCOUNTWITHCENTRALOUTBANKCODEKEY = "RediscountWithCentralOutBankCodeKey";
	public static final String REDISCOUNTWITHCENTRALOUTBANKCODEVALUE = "再贴现贴出人开户行行号";

	public static final String FINTDAYSKEY = "fIntDaysKey";
	public static final String FINTDAYSKEYVALUE = "计息天数";
	
	public static final String DISCOUNTOUTCOMPANYSIZEKEY = "discountOutCompanySizeKey";
	public static final String DISCOUNTOUTCOMPANYSIZEVALUEKEY = "贴现贴出人企业规模";
	
	public static final String DISCOUNTOUTCOMPANYNAMEKEY = "discountOutCompanyNameKey";
	public static final String DISCOUNTOUTCOMPANYNAMEVALUEKEY = "贴现贴出人名称";
	
	public static final String DISCOUNTOUTCOMPANYORGCODEKEY = "discountOutCompanyOrgCodeKey";
	public static final String DISCOUNTOUTCOMPANYORGCODEVALUEKEY = "贴现贴出人组织机构代码";
	
	public static final String DISCOUNTOUTCOMPANYCUSTCHARACTERKEY = "discountOutCompanyCustCharacterKey";
	public static final String DISCOUNTOUTCOMPANYCUSTCHARACTERVALUEKEY = "贴现贴出人企业性质";
	
	public static final String DISCOUNTOUTCOMPANYCLASSKEY = "discountOutCompanyClassKey";
	public static final String DISCOUNTOUTCOMPANYCLASSVALUEKEY = "贴现贴出人企业类别";

	public static final String DRWRCOMPANYSIZEKEY = "drwrOutCompanySizeKey";
	public static final String DRWRCOMPANYSIZEVALUEKEY = "出票人企业规模";
	
	public static final String DRWRCOMPANYCLASSKEY = "drwrCompanyClassKey";
	public static final String DRWRCOMPANYCLASSVALUEKEY = "出票人企业类别";
	
	
	public static final String CASHDEPOSITKEYS = ISSEAMTKEY + "," 
									+ CASHDEPOSITISSEAMTKEY + "," 
									+ DEPOSITRECEIPTISSEAMTKEY + "," 
									+ OTHERISSEAMTKEY + "," 
									+ LINEOFCREDITISSEAMTKEY;
	
	/**
	 * 银行承兑汇票相关数据统计
	 */
	public static final String BANKACCEPTBILLDATASTATIS = "银承数据统计";
	
	/**
	 * 商业承兑汇票相关数据统计
	 */
	public static final String BUSIACCEPTBILLDATASTATIS = "商承数据统计";
	
	/**
	 * 商业承兑汇票承兑企业业务数据统计
	 */
	public static final String ACCEPTIONCUSTOMERDATASTATIS = "承兑企业业务统计";
	
	/**
	 * 企业规模分布
	 */
	public static final String ENTERPRISEDISTRIBUTE = "企业规模分布";
	/**
	 * 出票企业行业分布
	 */
	public static final String VOCATIONDISTRBUTE = "企业行业分布";
	/**
	 * 保证方式
	 */
	public static final String CASHDEPOSIT = "保证方式";
	
	/**
	 * 合计
	 */
	public static final String TOTAL = "total";
	
	/**
	 * 合计
	 */
	public static final String TOTALNAME = "合计";
	
	/**
	 * 余额
	 */
	public static final String BALANCE = "余额";
	/**
	 * 发生额
	 */
	public static final String AMOUNT = "发生额";
	
	public static final String CASHDEPOSITAMOUNT = "保证金发生额";
	public static final String DEPOSITRECEIPTAMOUNT = "存单质押发生额";
	
	public static final String CASHDEPOSITAMOUNTBYYEAR = "保证金年累计发生额";
	public static final String DEPOSITRECEIPTAMOUNTBYYEAR = "存单质押年累计发生额";
	
	/**
	 * 统计方式
	 */
	public static final String MONTH = "月度";
	public static final String DAY = "天";
	public static final String YEAR = "年度";
	public static final String QUARTER = "季度";
	public static final String SPECIFYTIME = "指定时间";
	
	/**
	 * 年月常量
	 */
	public static final String YEARANDMONTH = "年月";
	
	
	/**
	 * 统计金额单位
	 */
	public static final String YUAN = "1"; // 元
	public static final String TENTHOUSAND = "10000"; // 万元
	public static final String YIYUAN = "100000000";//亿元
	
	/**
	 * 票据类型
	 */
	public static final String BILL_TYPE_CODE = "billType";//票据类型代码
	public static final String BILL_TYPE_NAME = "票据类型";//票据类型
	
	/**
	 * 票据介质
	 */
	public static final String BILL_MEDIA_CODE = "billMedia";//票据介质代码
	public static final String BILL_MEDIA_NAME = "票据介质";//票据介质
	
	/**
	 * 贴入利率
	 */
	public static final String BILL_RATE_CODE = "rate";//贴入利率代码
	public static final String BILL_RATE_NAME = "贴入利率";//贴入利率
	
	/**
	 * 贴入利息
	 */
	public static final String BILL_INT_CODE = "fInt";//贴入利息代码
	public static final String BILL_INT_NAME = "贴入利息";//贴入利息
	
	/**
	 * 贴现利差
	 */
	public static final String BILL_MARGIN_KEY = "MARGIN";//贴现利差代码
	public static final String BILL_MARGIN_NAME = "贴现利差";//贴现利差
	
	
	
	//统计业务类型 
	public static final String ACCEPTIONBUSINESS = "签发"; 	//签发业务
	public static final String DISCOUNTBUSINESS = "贴现";	//贴现业务
	public static final String DISCOUNT_NoRepurchase = "买断式贴现";
	public static final String DISCOUNT_Repurchase = "回购式贴现";
	public static final String REDISCOUNTINNER_BUY_Repurchase = "买断式转贴现系统内转入";
	public static final String REDISCOUNTINNER_BUY_NoRepurchase = "回购式转贴现系统内转入";
	public static final String REDISCOUNTINNER_BUY_NoRepurchase_Redeem = "系统内买入返售到期转贴现";
	public static final String REDISCOUNTINNER_SELL_Repurchase = "买断式转贴现系统内转出";
	public static final String REDISCOUNTINNER_SELL_NoRepurchase = "回购式转贴现系统内转出";
	public static final String REDISCOUNTINNER_SELL_NoRepurchase_Redeem = "系统内卖出返售到期转贴现";
	public static final String REDISCOUNTOUTER_BUY_Repurchase = "买断式转贴现系统外转入";
	public static final String REDISCOUNTOUTER_BUY_NoRepurchase = "回购式转贴现系统外转入";
	public static final String REDISCOUNTOUTER_BUY_NoRepurchase_Redeem = "系统外买入返售到期转贴现";
	public static final String REDISCOUNTOUTER_SELL_Repurchase = "买断式转贴现系统外转出";
	public static final String REDISCOUNTOUTER_SELL_NoRepurchase = "回购式转贴现系统外转出";
	public static final String REDISCOUNTOUTER_SELL_NoRepurchase_Redeem = "系统外卖出返售到期转贴现";
	
	public static final String REDISCOUNTTOPEOPLEBANK = "再贴现";//再贴现业务
	public static final String REDISCOUNTTOPEOPLEBANK_SELL_NoRepurchase = "卖出回购式再贴现"; 
	
	
	public static final String DISCOUNTBUSINESSAMOUNT = "贴现业务数据统计";	//
	/**
	 * 利息
	 */
	public static final String FINT = "利息";
	
	/**
	 * 笔数
	 */
	public static final String BILLNUM = "笔数";
	
	/**
	 * 利差
	 */
	public static final String MARGIN = "利差";
	
	/**
	 * 票面金额*付息天数
	 */
	public static final String FAMOUNTDAYS = "票面金额*付息天数";
	
	/**
	 * 利率
	 */
	public static final String RATE = "利率";
	
	public static final String THREEINNER = "3个月内";
	
	public static final String THREETOSIX = "3到6个月";
	
	public static final String SIXTOTWELVE = "6到12个月";
	
	
	public static final String MAXRATE = "最高利率";
	
	public static final String MAXRATEAMOUNT = "最高利率对应金额";
	
    public static final String MINRATE = "最低利率";
	
	public static final String MINRATEAMOUNT = "最低利率对应金额";
	
	public static final String YUQI = "逾期";
	
	/**
	 * 纸票
	 */
	public static final String PAPERYBILL = "纸票";
	
	/**
	 * 电票
	 */
	public static final String ELECTRONICAL = "电票";
	
	/**
	 * 电票纸票
	 */
	public static final String ALLDRAFTSTUFF = "电票纸票";
	
	/**
	 * 客户
	 */
	public static final String  CUST = "客户";
	
	
	/**
	 * 把数组组合成"##"分隔的字符串
	 * 
	 * @param arr
	 * @return
	 */
	public static String arr2Str(Object[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			sb.append("##" + arr[i]);
		}
		return sb.delete(0, 2).toString();
	}

	/**
	 * 把字符串拆分成数组
	 * 
	 * @param str
	 * @return
	 */
	public static String[] str2Arr(String str) {
		if (str == null || str.equals(""))
			return new String[0];
		else
			return str.split("##");
	}

	/**
	 * 把字符串拆分成列表
	 * 
	 * @param str
	 * @return
	 */
	public static List str2List(String str) {
		return arr2List(str2Arr(str));
	}

	/**
	 * 把数组转化成列表
	 * 
	 * @param str
	 * @return
	 */
	public static List arr2List(String[] strs) {
		List lst = new ArrayList();
		for (int i = 0, len = strs.length; i < len; i++) {
			lst.add(strs[i]);
		}
		return lst;
	}

	/**
	 * 数组字符串加上指定元素
	 * 
	 * @param str0
	 * @param str1
	 * @return
	 */
	public static String addArrElem(String str0, String str1) {
		if (str0 == null || str0.equals(""))
			return str1;
		else
			return str0 + "##" + str1;
	}

	/**
	 * 数组字符串删除指定元素
	 * 
	 * @param str0
	 * @param str1
	 * @return
	 */
	public static String delArrElem(String str0, String str1) {
		if (str0 == null || str0.equals(""))
			return str0;
		else if (str0.indexOf("##") < 0)
			return str0.replaceFirst(str1, "");
		else if (str0.startsWith(str1))
			return str0.replaceFirst(str1 + "##", "");
		else
			return str0.replaceFirst("##" + str1, "");
	}

	/**
	 * 获取元素索引
	 * 
	 * @param objs
	 * @param obj
	 * @return
	 */
	public static int indexOf(Object[] objs, Object obj) {
		for (int i = 0; i < objs.length; i++) {
			if (objs[i].equals(obj)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 取列表中的最大值
	 * 
	 * @param list
	 * @return
	 */
	public static BigDecimal getMaxValue(List list) {
		BigDecimal maxVal = null;
		if (list != null && !list.isEmpty()) {
			maxVal = (BigDecimal) list.get(0);
			for (int i = 0, len = list.size(); i < len; i++) {
				BigDecimal tmpVal = (BigDecimal) list.get(i);
				if (maxVal.compareTo(tmpVal) < 0)
					maxVal = tmpVal;
			}
		}
		return maxVal;
	}

	/**
	 * 取列表中的最小值
	 * 
	 * @param list
	 * @return
	 */
	public static BigDecimal getMinValue(List list) {
		BigDecimal minVal = null;
		if (list != null && !list.isEmpty()) {
			minVal = (BigDecimal) list.get(0);
			for (int i = 0, len = list.size(); i < len; i++) {
				BigDecimal tmpVal = (BigDecimal) list.get(i);
				if (minVal.compareTo(tmpVal) > 0)
					minVal = tmpVal;
			}
		}
		return minVal;
	}

	/**
	 * 判断两个日期是否是在同一天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isTheSameDay(Date date1, Date date2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr1 = df.format(date1);
		String dateStr2 = df.format(date2);
		return dateStr1.equalsIgnoreCase(dateStr2);
	}

	/**
	 * 获取给定日期的前一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getPreDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	/**
	* <p>方法名称: getYearAndMonth|描述: 获取要按月统计分析报表的年和月</p>
	* @param date 报表统计的开始日 比如:2010-09-08
	* @return
	 */
	public static String getYearAndMonth(Date date){
		String partten = "yyyy年MM月";
		return DateUtils.toString(date, partten);
	}
	
	/**
	* <p>方法名称: getYearAndMonth|描述: 取得给定日期的上一年</p>
	* @param date 报表统计的开始日 比如:2010-09-08
	* @return
	 */
	public static String getYearAndMonthOfLast(String thisMonth){
		String[] partten = thisMonth.split("年");
		int year = Integer.valueOf(partten[0]).intValue();
		return (year-1)+"年"+partten[1];
	}
	
	/**
	* <p>方法名称: getLastMonth|描述: 根据本月月份得到上个月的月份信息</p>
	* @param thisMonth
	* @return
	 */
	public static String getLastMonth(String thisMonth){
		String y = thisMonth.substring(0,4);	 //得到年份的数字
		String m = thisMonth.substring(5,7);//得到月份的数字
		if(m.equals("01")){
			int year = Integer.parseInt(y);
			int ly = year -1 ;//获取上一年的数字
			return ly  +"年12月";
		} else {
			//否则月份就是 二月到12月
			int month = Integer.parseInt(m);
			int lm = month - 1 ;
			if(month == 11 || month == 12){
				return y + "年" + lm+ "月" ;
			} else {
				return  y + "年0" +lm + "月";//如果是lm是1-9的话,前面要加0
			}
		}
	}


}
