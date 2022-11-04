package com.mingtech.application.utils;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.framework.common.util.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
/**
 * Created with IntelliJ IDEA
 *
 * @description: DraftRangeHandler
 * @author: silen
 * @time: 2021/8/9 15:57
 */
public class DraftRangeHandler {

	
    /**
     * 等分化零票子票区间
     */
    private static final String DRAFT_RANGE_ZERO = "0";

    /**
     * 页面展示格式化子票区间间隔符
     */
    private static final String DRAFT_RANGE_LIMIT = "-";


    private static final String BEGIN_DRAFT_RANGE = "BEGIN_DRAFT_RANGE";

    private static final String END_DRAFT_RANGE = "END_DRAFT_RANGE";
    public static final String DRAFT_SOURCE_01 = "CS01";
    public static final String DRAFT_SOURCE_02 = "CS02";
    public static final String DRAFT_SOURCE_03 = "CS03";

    /**
     * CS01 ECDS传统票据
     */
    private static final String DRAFT_CS01 = "1,2";

    /**
     * CS02 金融机构票据（等分化票据）
     */
    private static final String DRAFT_CS02 = "5,6";

    /**
     * CS03 供应链票据
     */
    private static final String DRAFT_CS03 = "7,8";


    private DraftRangeHandler(){
    }

    /**
     * 格式化子票区间
     * @param beginRangeNo 子票区间起始
     * @param endRangeNo 子票区间截止
     * @return 子票区间结合
     */
  /*  public static String execute(String beginRangeNo,String endRangeNo){
        if(DRAFT_RANGE.equals(beginRangeNo) || beginRangeNo.substring(beginRangeNo.length()-1).equals(beginRangeNo)){
            return String.valueOf(0);
        }
        return beginRangeNo + DRAFT_RANGE_LIMIT + endRangeNo;
    }
*/

    /**
     * 根据票据(包)号判断票据来源
     * @param draftNo 票据（包）号
     * @return 票据来源code
     */
    public static String draftSourceJudge(String draftNo){
        String draftNo1 = draftNo.substring(0,1);
        if(DRAFT_CS01.contains(draftNo1)){
            return DRAFT_SOURCE_01;
        }else if(DRAFT_CS02.contains(draftNo1)){
            return DRAFT_SOURCE_02;
        }else if(DRAFT_CS03.contains(draftNo1)){
            return DRAFT_SOURCE_03;
        }
        return null;
    }

    /**
     * 截取子区间的起始
     * @param draftRange 子票区间
     * @return 子票区间起始
     */
    public static String getBeginDraftRangeNo(String draftRange){
        if(StringUtils.isNotBlank(draftRange) && draftRange.length() > 1){
            return draftRange.substring(0,12);
        }else if(StringUtils.isNotBlank(draftRange) && draftRange.length() == 1){
            return DRAFT_RANGE_ZERO;
        }else{
            return null;
        }

    }


    /**
     * 截取子区间的截止
     * @param draftRange 子票区间
     * @return 子票区间截止
     */
    public static String getEndDraftRangeNo(String draftRange){
        if(StringUtils.isNotBlank(draftRange) && draftRange.length() > 1){
            return draftRange.substring(13,25);
        }else if(StringUtils.isNotBlank(draftRange) && draftRange.length() == 1){
            return DRAFT_RANGE_ZERO;
        }else{
            return null;
        }
    }


    /**
     * 根据子票区间起始判断报文子票区间是否赋值
     * @param begin 子票区间起始
     * @return
     */
   /* public static boolean draftRangeIsEmpty(String begin){
        if(StringUtils.isBlank(begin)){
            return true;
        }
        return DRAFT_RANGE.equals(begin);
    }
*/





    /**
     * 根据子票区间起始、截止、交易金额、标准金额计算当前交易子票区间
     * @param tradeAmt 交易金额
     * @param standAmt 标准金额
     * @param end 子票区间截止
     * @return 当前交易票据的子票区间 {000000000000,000000000001}
     */
    public static String generateLimitDraftRange(BigDecimal draftAmt, BigDecimal tradeAmt, BigDecimal standAmt, String end){

        BigDecimal subtract1 = draftAmt.subtract(tradeAmt);
        //一般交易金额与子票区间为整数倍关系
        BigDecimal divide = subtract1.divide(standAmt,6, RoundingMode.HALF_UP);
        BigDecimal end1 = new BigDecimal(end);
        BigDecimal subtract = end1.subtract(divide);
        DecimalFormat df = new DecimalFormat(DRAFT_RANGE_ZERO);
        //使用0进行补位,位数12
        return df.format(subtract);

    }


    /**
     * 拆分后的出池区间
     * @param draftAmt 票据基金额
     * @param tradeAmt 交易金额
     * @param standAmt 标准金额
     * @param begin 子票区间起始
     * @param end 子票区间截止
     * @return {@link DraftRange} 申请信息中的子票区间起始和子票区间截止
     */
    public static DraftRange buildLimitBeginAndEndDraftRange(BigDecimal draftAmt,BigDecimal tradeAmt,BigDecimal standAmt,String begin,String end){
        String newEndDraftRange = generateLimitDraftRange(draftAmt, tradeAmt, standAmt, end);
        return new DraftRange( begin,newEndDraftRange);
    }


    /**
     * 拆分后的在池区间
     * @param draftAmt 票据金额
     * @param tradeAmt 交易金额
     * @param standAmt 标准金额
     * @param begin 子票区间起始
     * @param end 子票区间截止
     * @return {@link DraftRange} 分包后的子票区间
     */
    public static DraftRange buildLimitNewBeginAndEndDraftRange(BigDecimal draftAmt,BigDecimal tradeAmt,BigDecimal standAmt,String begin,String end){
        String newEndDraftRange = generateLimitDraftRange(draftAmt, tradeAmt, standAmt, end);
        //int newEndDraftRange1 = Integer.parseInt(newEndDraftRange) + 1 ;
        BigDecimal newEndDraftRange1 =  new BigDecimal(newEndDraftRange).add(BigDecimal.ONE);
        DecimalFormat df = new DecimalFormat(DRAFT_RANGE_ZERO);
        return new DraftRange( df.format(newEndDraftRange1) , end);
    }
    
    /**
     * 根据票号格式成12位子票区间
     * @param billNo
     * @return
     */
    /*public static String buildFromatBillNo(String billNo){
    	DecimalFormat df = new DecimalFormat(DRAFT_RANGE);
    	if(billNo == null || StringUtil.isEmpty(billNo) || billNo.equals("000000000000")){
    		return "000000000000";
    	}
    	//使用0进行补位,位数12
        return df.format(new BigDecimal(billNo).setScale(2, BigDecimal.ROUND_HALF_UP));
    	
    }*/
    
    /**
     * 根据子票区间计算金额
     * @param startBillNo
     * @param endBillNo
     * @return
     */
    public static BigDecimal formatBillNos(String startBillNo, String endBillNo){
    	BigDecimal money1 = new BigDecimal(startBillNo);
		BigDecimal money2 = new BigDecimal(endBillNo);
    	
		BigDecimal billAmt = money2.subtract(money1).add(new BigDecimal(1));
		
		return billAmt.divide(new BigDecimal(100));
    	
    	
    }
    
    public static void main(String[] args){
		String str1 = "1";
		String str2 = "10300";
		
		
		BigDecimal money1 = formatBillNos(str1, str2);
		DraftRange raftInt = buildLimitNewBeginAndEndDraftRange(new BigDecimal(1000), new BigDecimal(121), new BigDecimal(0.01), "101", "100100");
		DraftRange raftOut = buildLimitBeginAndEndDraftRange(new BigDecimal(1000), new BigDecimal(121), new BigDecimal(0.01), "101", "100100");
		
	}

}

