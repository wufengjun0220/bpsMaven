package com.mingtech.framework.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：票据状态定义接口，定义所有票据状态常量
 * @author huangshiqiang@
 * May 17, 2009
 */
public class EdraftStatus {

	/**
	 * 出票已登记状态码
	 */
	public static final String Register_Registered_Code="010004";
	/**
	 * 出票已登记状态文本
	 */
	public static final String Register_Registered_Text="出票已登记";
	/**
	 * 提示承兑待签收状态码
	 */
	public static final String Acceptance_Unsign_Code="020001";
	/**
	 * 提示承兑待签收状态文本
	 */
	public static final String Acceptance_Unsign_Text="提示承兑待签收";
	/**
	 * 提示承兑已签收状态码
	 */
	public static final String Acceptance_Signed_Code="020006";
	/**
	 * 提示承兑已签收状态文本
	 */
	public static final String Acceptance_Signed_Text="提示承兑已签收";
	/**
	 * 提示收票待签收状态码
	 */
	public static final String Issuance_Unsign_Code="030001";
	/**
	 * 提示收票待签收状态文本
	 */
	public static final String Issuance_Unsign_Text="提示收票待签收";
	/**
	 * 提示收票已签收状态码
	 */
	public static final String Issuance_Signed_Code="030006";
	/**
	 * 提示收票已签收状态文本
	 */
	public static final String Issuance_Signed_Text="提示收票已签收";
	/**
	 * 结束已作废状态码
	 */
	public static final String Finish_Abandon_Code="000002";
	/**
	 * 结束已作废状态文本
	 */
	public static final String Finish_Abandon_Text="结束已作废";
	/**
	 * 背书待签收状态码
	 */
	public static final String Endorsement_Unsign_Code="100001";
	/**
	 * 背书待签收状态文本
	 */
	public static final String Endorsement_Unsign_Text="背书待签收";
	
	/**
	 * 背书已签收状态码
	 */
	public static final String Endorsement_Signed_Code="100006";
	/**
	 * 背书已签收状态文本
	 */
	public static final String Endorsement_Signed_Text="背书已签收";
	/**
	 * 买断式贴现待签收状态码
	 */
	public static final String Discount_NoRepurchase_Unsign_Code="110101";
	/**
	 * 买断式贴现待签收状态文本
	 */
	public static final String Discount_NoRepurchased_Unsign_Text="买断式贴现待签收";
	/**
	 * 买断式贴现已签收待清算状态码
	 */
	public static final String Discount_NoRepurchase_SingnedUnsettlement_Code="110103";
	/**
	 * 买断式贴现已签收待清算状态文本
	 */
	public static final String Discount_NoRepurchase_SingnedUnsettlement_Text="买断式贴现已签收待清算";
	/**
	 * 买断式贴现已签收已排队状态码
	 */
	public static final String Discount_NoRepurchase_SingnedQueuing_Code="110103";
	/**
	 * 买断式贴现已签收已排队状态文本
	 */
	public static final String Discount_NoRepurchase_SingnedQueuing_Text="买断式贴现已签收已排队";
	/**
	 * 买断式贴现已签收状态码
	 */
	public static final String Discount_NoRepurchase_Singned_Code="110106";
	/**
	 * 买断式贴现已签收状态文本
	 */
	public static final String Discount_NoRepurchase_Singned_Text="买断式贴现已签收";
	/**
	 * 回购式贴现待签收收状态码
	 */
	public static final String Discount_Repurchase_Unsign_Code="110201";
	/**
	 * 回购式贴现待签收状态文本
	 */
	public static final String Discount_Repurchased_Unsign_Text="回购式贴现待签收";
	/**
	 * 回购式贴现已签收待清算状态码
	 */
	public static final String Discount_Repurchase_SingnedUnsettlement_Code="110203";
	/**
	 * 回购式贴现已签收待清算状态文本
	 */
	public static final String Discount_Repurchase_SingnedUnsettlement_Text="回购式贴现已签收待清算";
	/**
	 * 回购式贴现已签收已排队状态码
	 */
	public static final String Discount_Repurchase_SingnedQueuing_Code="110205";
	/**
	 * 回购式贴现已签收已排队状态文本
	 */
	public static final String Discount_Repurchase_SingnedQueuing_Text="回购式贴现已签收已排队";
	/**
	 * 回购式贴现已签收状态码
	 */
	public static final String Discount_Repurchase_Singned_Code="110206";
	/**
	 * 回购式贴现已签收状态文本
	 */
	public static final String Discount_Repurchase_Singned_Text="回购式贴现已签收";
	/**
	 * 回购式贴现已至赎回开放日状态码
	 */
	public static final String Discount_Repurchase_Open_Code="110216";
	/**
	 * 回购式贴现已至赎回开放日状态文本
	 */
	public static final String Discount_Repurchase_Open_Text="回购式贴现已至赎回开放日";
	/**
	 * 回购式贴现已逾赎回截止日状态码
	 */
	public static final String Discount_Repurchase_End_Code="110218";
	/**
	 * 回购式贴现已逾赎回截止日状态文本
	 */
	public static final String Discount_Repurchase_End_Text="回购式贴现已逾赎回截止日";
	/**
	 * 回购式贴现赎回待签收状态码
	 */
	public static final String Discount_Repurchase_RedeemUnsign_Code="120001";
	/**
	 * 回购式贴现赎回待签收状态文本
	 */
	public static final String Discount_Repurchase_RedeemUnsign_Text="回购式贴现赎回待签收";
	
	/**
	 * 回购式贴现赎回已签收待清算状态码
	 */
	public static final String Discount_Repurchase_RedeemSingnedUnSettlement_Code="120003";
	/**
	 * 回购式贴现赎回已签收待清算状态文本
	 */
	public static final String Discount_Repurchase_RedeemSingnedUnSettlement_Text="回购式贴现赎回已签收待清算";
	
	/**
	 * 回购式贴现赎回已签收已排队状态码
	 */
	public static final String Discount_Repurchase_RedeemSingnedQueuing_Code="120005";
	/**
	 * 回购式贴现赎回已签收已排队状态文本
	 */
	public static final String Discount_Repurchase_RedeemSingnedQueuing_Text="回购式贴现赎回已签收已排队";

	/**
	 * 回购式贴现赎回已签收状态码
	 */
	public static final String Discount_Repurchase_RedeemSingned_Code="120006";
	/**
	 * 回购式贴现赎回已签收状态文本
	 */
	public static final String Discount_Repurchase_RedeemSingned_Text="回购式贴现赎回已签收";

	/**
	 * 买断式转贴现待签收状态码
	 */
	public static final String NoRepurchase_Discount_Unsign_Code="130101";
	/**
	 * 买断式转贴现待签收状态文本
	 */
	public static final String NoRepurchase_Discount_Unsign_Text="买断式转贴现待签收";
	/**
	 * 买断式转贴现已签收待清算状态码
	 */
	public static final String NoRepurchase_Discount_SingnedUnsettlement_Code="130103";
	/**
	 * 买断式转贴现已签收待清算状态文本
	 */
	public static final String NoRepurchase_Discount_SingnedUnsettlement_Text="买断式转贴现已签收待清算";
	/**
	 * 买断式转贴现已签收已排队状态码
	 */
	public static final String NoRepurchase_Discount_SingnedQueuing_Code="130105";
	/**
	 * 买断式转贴现已签收已排队状态文本
	 */
	public static final String NoRepurchase_Discount_SingnedQueuing_Text="买断式转贴现已签收已排队";
	/**
	 * 买断式转贴现已签收状态码
	 */
	public static final String NoRepurchase_Discount_Singned_Code="130106";
	/**
	 * 买断式转贴现已签收状态文本
	 */
	public static final String NoRepurchase_Discount_Singned_Text="买断式转贴现已签收";
	/**
	 * 回购式转贴现待签收状态码
	 */
	public static final String Repurchase_Discount_Unsingn_Code="130201";
	/**
	 * 回购式转贴现待签收状态文本
	 */
	public static final String Repurchase_Discount_Unsign_Text="回购式转贴现待签收";
	/**
	 * 回购式转贴现已签收待清算状态码
	 */
	public static final String Repurchase_Discount_SingnedUnsettlement_Code="130203";
	/**
	 * 回购式转贴现已签收待清算状态文本
	 */
	public static final String Repurchase_Discount_SingnedUnsettlement_Text="回购式转贴现已签收待清算";
	/**
	 * 回购式转贴现已签收已排队状态码
	 */
	public static final String Repurchase_Discount_SingnedQueuing_Code="130205";
	/**
	 * 回购式转贴现已签收已排队状态文本
	 */
	public static final String Repurchase_Discount_SingnedQueuing_Text="回购式转贴现已签收已排队";
	/**
	 * 回购式转贴现已签收状态码
	 */
	public static final String Repurchase_Discount_Singned_Code="130206";
	/**
	 * 回购式转贴现已签收状态文本
	 */
	public static final String Repurchase_Discount_Singned_Text="回购式转贴现已签收";
	/**
	 * 回购式转贴现已至赎回开放日状态码
	 */
	public static final String Repurchase_Discount_Open_Code="130216";
	/**
	 * 回购式转贴现已至赎回开放日状态文本
	 */
	public static final String Repurchase_Discount_Open_Text="回购式转贴现已至赎回开放日";
	/**
	 * 回购式转贴现已逾赎回截止日状态码
	 */
	public static final String Repurchase_Discount_End_Code="130218";
	/**
	 * 回购式转贴现已逾赎回截止日状态文本
	 */
	public static final String Repurchase_Discount_End_Text="回购式转贴现已逾赎回截止日";
	/**
	 * 回购式转贴现赎回待签收状态码
	 */
	public static final String Repurchase_Discount_RedeemUnsingn_Code="140001";
	/**
	 * 回购式转贴现赎回待签收状态文本
	 */
	public static final String Repurchase_Discount_RedeemUnsingn_Text="回购式转贴现赎回待签收";
	/**
	 * 回购式转贴现赎回已签收待清算状态码
	 */
	public static final String Repurchase_Discount_RedeemSingnedUnsettlement_Code="140003";
	/**
	 * 回购式转贴现赎回已签收待清算状态文本
	 */
	public static final String Repurchase_Discount_RedeemSingnedUnsettlement_Text="回购式转贴现赎回已签收待清算";
	/**
	 * 回购式转贴现赎回已签收已排队状态码
	 */
	public static final String Repurchase_Discount_RedeemSingnedQueuing_Code="140005";
	/**
	 * 回购式转贴现赎回已签收已排队状态文本
	 */
	public static final String Repurchase_Discount_RedeemSingnedQueuing_Text="回购式转贴现赎回已签收已排队";
	/**
	 * 回购式转贴现赎回已签收状态码
	 */
	public static final String Repurchase_Discount_RedeemSingned_Code="140006";
	/**
	 * 回购式转贴现赎回已签收状态文本
	 */
	public static final String Repurchase_Discount_RedeemSingned_Text="回购式转贴现赎回已签收";
	/**
	 * 买断式再贴现待签收状态码
	 */
	public static final String NoRepurchase_Rediscount_Unsingn_Code="150101";
	/**
	 * 买断式再贴现待签收状态文本
	 */
	public static final String NoRepurchase_Rediscount_Unsingn_Text="买断式再贴现待签收";
	/**
	 * 买断式再贴现已签收待清算状态码
	 */
	public static final String NoRepurchase_Rediscount_SingnedUnsettlement_Code="150103";
	/**
	 * 买断式再贴现已签收待清算状态文本
	 */
	public static final String NoRepurchase_Rediscount_SingnedUnsettlement_Text="买断式再贴现已签收待清算";
	/**
	 * 买断式再贴现已签收已排队状态码
	 */
	public static final String NoRepurchase_Rediscount_SingnedQueuing_Code="150105";
	/**
	 * 买断式再贴现已签收已排队状态文本
	 */
	public static final String NoRepurchase_Rediscount_SingnedQueuing_Text="买断式再贴现已签收已排队";
	/**
	 * 买断式再贴现已签收状态码
	 */
	public static final String NoRepurchase_Rediscount_Singned_Code="150106";
	/**
	 * 买断式再贴现已签收状态文本
	 */
	public static final String NoRepurchase_Rediscount_Singned_Text="买断式再贴现已签收";
	/**
	 * 回购式再贴现待签收状态码
	 */
	public static final String Repurchase_Rediscount_Unsingn_Code="150201";
	/**
	 * 回购式再贴现待签收状态文本
	 */
	public static final String Repurchase_Rediscount_Unsingn_Text="回购式再贴现待签收";
	/**
	 * 回购式再贴现已签收待清算状态码
	 */
	public static final String Repurchase_Rediscount_SingnedUnsettlement_Code="150203";
	/**
	 * 回购式再贴现已签收待清算状态文本
	 */
	public static final String Repurchase_Rediscount_SingnedUnsettlement_Text="回购式再贴现已签收待清算";
	/**
	 * 回购式再贴现已签收已排队状态码
	 */
	public static final String Repurchase_Rediscount_SingnedQueuing_Code="150205";
	/**
	 * 回购式再贴现已签收已排队状态文本
	 */
	public static final String Repurchase_Rediscount_SingnedQueuing_Text="回购式再贴现已签收已排队";
	/**
	 * 回购式再贴现已签收状态码
	 */
	public static final String Repurchase_Rediscount_Singned_Code="150206";
	/**
	 * 回购式再贴现已签收状态文本
	 */
	public static final String Repurchase_Rediscount_Singned_Text="回购式再贴现已签收";
	/**
	 * 回购式再贴现已至赎回开放日状态码
	 */
	public static final String Repurchase_Rediscount_Open_Code="150216";
	/**
	 * 回购式再贴现已至赎回开放日状态文本
	 */
	public static final String Repurchase_Rediscount_Open_Text="回购式再贴现已至赎回开放日";
	/**
	 * 回购式再贴现已逾赎回截止日状态码
	 */
	public static final String Repurchase_Rediscount_End_Code="150218";
	/**
	 * 回购式再贴现已逾赎回截止日状态文本
	 */
	public static final String Repurchase_Rediscount_End_Text="回购式再贴现已逾赎回截止日";
	/**
	 * 回购式再贴现赎回待签收状态码
	 */
	public static final String Repurchase_Rediscount_RedeemUnsing_Code="160001";
	/**
	 * 回购式再贴现赎回待签收状态文本
	 */
	public static final String Repurchase_Rediscount_RedeemUnsing_Text="回购式再贴现赎回待签收";
	/**
	 * 回购式再贴现赎回已签收待清算状态码
	 */
	public static final String Repurchase_Rediscount_RedeemUnsettlement_Code="160003";
	/**
	 * 回购式再贴现赎回已签收待清算状态文本
	 */
	public static final String Repurchase_Rediscount_RedeemUnsettlement_Text="回购式再贴现赎回已签收待清算";
	
	/**
	 * 回购式再贴现赎回已签收已排队状态码
	 */
	public static final String Repurchase_Rediscount_RedeemSingnedQueuing_Code="160005";
	/**
	 * 回购式再贴现赎回已签收已排队状态文本
	 */
	public static final String Repurchase_Rediscount_RedeemSingnedQueuing_Text="回购式再贴现赎回已签收已排队";
	/**
	 * 回购式再贴现赎回已签收状态码
	 */
	public static final String Repurchase_Rediscount_RedeemSingned_Code="160006";
	/**
	 * 回购式再贴现赎回已签收状态文本
	 */
	public static final String Repurchase_Rediscount_RedeemSingned_Text="回购式再贴现赎回已签收";
	/**
	 * 质押待签收状态码
	 */
	public static final String Collateralization_Unsingn_Code="180001";
	/**
	 * 质押待签收状态文本
	 */
	public static final String Collateralization_Unsingn_Text="质押待签收";
	/**
	 * 质押已签收状态码
	 */
	public static final String Collateralization_Singned_Code="180006";
	/**
	 * 质押已签收状态文本
	 */
	public static final String Collateralization_Singned_Text="质押已签收";
	/**
	 * 质押已至票据到期日状态码
	 */
	public static final String Collateralization_End_Code="180020";
	/**
	 * 质押已至票据到期日状态文本
	 */
	public static final String Collateralization_End_Text="质押已至票据到期日";
	/**
	 * 质押解除待签收状态码
	 */
	public static final String RepurchasedCollateralization_Unsign_Code="190001";
	/**
	 * 质押解除待签收状态文本
	 */
	public static final String RepurchasedCollateralization_Unsign_Text="质押解除待签收";
	/**
	 * 质押解除已签收状态码
	 */
	public static final String RepurchasedCollateralization_Signed_Code="190006";
	/**
	 * 质押解除已签收状态文本
	 */
	public static final String RepurchasedCollateralization_Signed_Text="质押解除已签收";
	/**
	 * 保证待签收状态码
	 */
	public static final String Guarantee_Unsign_Code="170001";
	/**
	 * 保证待签收状态文本
	 */
	public static final String Guarantee_Unsign_Text="保证待签收";
	/**
	 * 提示付款待签收状态码
	 */
	public static final String Presentation_Unsign_Code="200001";
	/**
	 * 提示付款待签收状态文本
	 */
	public static final String Presentation_Unsign_Text="提示付款待签收";
	/**
	 * 提示付款已签收待清算状态码
	 */
	public static final String Presentation_SignedUnsettlement_Code="200003";
	/**
	 * 提示付款已签收待清算状态文本
	 */
	public static final String Presentation_SignedUnsettlement_Text="提示付款已签收待清算";
	/**
	 * 提示付款已签收已排队状态码
	 */
	public static final String Presentation_SignedQueuing_Code="200005";
	/**
	 * 提示付款已签收已排队状态文本
	 */
	public static final String Presentation_SignedQueuing_Text="提示付款已签收已排队";
	
	/**
	 * 结束已结清状态码
	 */
	public static final String Finish_Closed_Code="000000";
	/**
	 * 结束已结清状态文本
	 */
	public static final String Finish_Closed_Text="结束已结清";
	/**
	 * 提示付款已拒付状态码（可拒付追索，只能追出票人，承兑人及其保证人）
	 */
	public static final String Presentation_Refused_Code="200312";
	/**
	 * 提示付款已拒付状态文本（可拒付追索，只能追出票人，承兑人及其保证人）
	 */
	public static final String Presentation_Refused_Text="提示付款已拒付";
	/**
	 * 提示付款已拒付状态码（可拒付追索，可以追所有人）
	 */
	public static final String Presentation_RefusedAll_Code="200412";
	/**
	 * 提示付款已拒付状态文本（可拒付追索，可以追所有人）
	 */
	public static final String Presentation_RefusedAll_Text="提示付款已拒付";
	/**
	 * 提示付款已拒付状态码（不可进行拒付追索）
	 */
	public static final String Presentation_RefusedNo_Code="200512";
	/**
	 * 提示付款已拒付状态文本 （不可进行拒付追索）
	 */
	public static final String Presentation_RefusedNo_Text="提示付款已拒付";
	/**
	 * 逾期提示付款待签收状态码
	 */
	public static final String OverduePresentation_Unsign_Code="210001";
	/**
	 * 逾期提示付款待签收状态文本
	 */
	public static final String OverduePresentation_Unsign_Text="逾期提示付款待签收";
	/**
	 * 逾期提示付款已签收待清算状态码
	 */
	public static final String OverduePresentation_SignedUnsettlement_Code="210003";
	/**
	 * 逾期提示付款已签收待清算状态文本
	 */
	public static final String OverduePresentation_SignedUnsettlement_Text="逾期提示付款已签收待清算";
	/**
	 * 逾期提示付款已签收已排队状态码
	 */
	public static final String OverduePresentation_SignedQueuing_Code="210005";
	/**
	 * 逾期提示付款已签收已排队状态文本
	 */
	public static final String OverduePresentation_SignedQueuing_Text="逾期提示付款已签收已排队";
	/**
	 * 逾期提示付款已拒付状态码 （可拒付追索，只能追出票人，承兑人及其保证人）
	 */
	public static final String OverduePresentation_Refused_Code="210312";
	/**
	 * 逾期提示付款已拒付状态文本（可拒付追索，只能追出票人，承兑人及其保证人）
	 */
	public static final String OverduePresentation_Refused_Text="逾期提示付款已拒付";
	/**
	 * 逾期提示付款已拒付状态码 （可拒付追索，可以追所有人）
	 */
	public static final String OverduePresentation_RefusedAll_Code="210412";
	/**
	 * 逾期提示付款已拒付状态文本（可拒付追索，可以追所有人）
	 */
	public static final String OverduePresentation_RefusedAll_Text="逾期提示付款已拒付";
	/**
	 * 拒付追索待清偿状态码 
	 */
	public static final String Recourse_RefusedPay_Code="220607";
	/**
	 * 拒付追索待清偿状态文本
	 */
	public static final String Recourse_RefusedPay_Text="拒付追索待清偿";
	/**
	 * 拒付追索同意清偿待签收状态码 
	 */
	public static final String Recourse_RefusedPayUnsign_Code="230601";
	/**
	 * 拒付追索同意清偿待签收状态文本
	 */
	public static final String Recourse_RefusedPayUnsign_Text="拒付追索同意清偿待签收";
	/**
	 * 拒付追索同意清偿已签收状态码 
	 */
	public static final String Recourse_RefusedPaySigned_Code="230606";
	/**
	 * 拒付追索同意清偿已签收状态文本
	 */
	public static final String Recourse_RefusedPaySigned_Text="拒付追索同意清偿已签收";
	/**
	 * 非拒付追索待清偿状态码 
	 */
	public static final String Recourse_Unpay_Code="220707";
	/**
	 * 非拒付追索待清偿状态文本
	 */
	public static final String Recourse_Unpay_Text="非拒付追索待清偿";
	/**
	 * 非拒付追索已撤销状态码 
	 */
	public static final String Recourse_Cancelled_Code="220710";
	/**
	 * 非拒付追索已撤销状态文本
	 */
	public static final String Recourse_Cancelled_Text="非拒付追索已撤销";
	
	/**
	 * 非拒付追索同意清偿待签收状态码 
	 */
	public static final String Recourse_PayUnsign_Code="230701";
	/**
	 * 非拒付追索同意清偿待签收状态文本
	 */
	public static final String Recourse_PayUnsign_Text="非拒付追索同意清偿待签收";
	/**
	 * 非拒付追索同意清偿已签收状态码 
	 */
	public static final String Recourse_PaySigned_Code="230706";
	/**
	 * 非拒付追索同意清偿已签收状态文本
	 */
	public static final String Recourse_PaySigned_Text="非拒付追索同意清偿已签收";
	/**
	 * 央行卖票待签收状态码 
	 */
	public static final String CenterBank_SellUnsign_Code="250001";
	/**
	 * 央行卖票待签收状态文本
	 */
	public static final String CenterBank_SellUnsign_Text="央行卖票待签收";
	/**
	 * 央行卖票已签收待清算状态码 
	 */
	public static final String CenterBank_SellSignedUnsettlement_Code="250003";
	/**
	 * 央行卖票已签收待清算状态文本
	 */
	public static final String CenterBank_SellSignedUnsettlement_Text="央行卖票已签收待清算";
	
	/**
	 * 央行卖票已签收已排队状态码 
	 */
	public static final String CenterBank_SellSignedQueuing_Code="250005";
	/**
	 * 央行卖票已签收已排队状态文本
	 */
	public static final String CenterBank_SellSignedQueuing_Text="央行卖票已签收已排队";
	
	/**
	 * 央行卖票已签收状态码 
	 */
	public static final String CenterBank_SellSigned_Code="250006";
	/**
	 * 央行卖票已签收状态文本
	 */
	public static final String CenterBank_SellSigned_Text="央行卖票已签收";
	/**
	 * 已逾票据权利失效日状态码 
	 */
	public static final String CenterBank_OverDue_Code="000026";
	/**
	 * 已逾票据权利失效日文本
	 */
	public static final String CenterBank_OverDue_Text="已逾票据权利失效日";
	
	/** Contract_Wait_Response_Code 要约待承诺码*/
	public static final String Contract_Wait_Response_Code= "010151";
	
	/** Contract_Wait_Response_Text 要约待承诺文本*/
	public static final String Contract_Wait_Response_Text="要约待承诺";
	
	/** Contract_Abuse_Code 要约已失效码*/
	public static final String Contract_Abuse_Code="010152";
	
	/** Contract_Abuse_Text 要约已失效文本*/
	public static final String Contract_Abuse_Text="要约已失效";
	
	/** Contract_Cancel_Code 要约已撤销码*/
	public static final String Contract_Cancel_Code="010153";
	
	/** Contract_Cancel_Text 要约已撤销文本*/
	public static final String Contract_Cancel_Text="要约已撤销";
	
	/** Contract_Refused_Code 要约已拒绝码*/
	public static final String Contract_Refused_Code="010154";
	
	/** Contract_Refused_Text 要约已拒绝文本*/
	public static final String Contract_Refused_Text="要约已拒绝";
	
	/** Contract_Success_Code 合同已成立码*/
	public static final String Contract_Success_Code="020255";
	
	/** Contract_Success_Text 合同已成立文本*/
	public static final String Contract_Success_Text="合同已成立";
	
	/** Contract_Wait_Rescission_Code 合同已成立待解除码*/
	public static final String Contract_Wait_Rescission_Code="020256";
	
	/** Contract_Wait_Rescission_Text 合同已成立待解除文本*/
	public static final String Contract_Wait_Rescission_Text="合同已成立待解除";
	
	/** Contract_Rescission_Code 合同已解除码*/
	public static final String Contract_Rescission_Code="010257";
	
	/** Contract_Rescission_Text 合同已解除文本*/
	public static final String Contract_Rescission_Text="合同已解除";
	private static final Map map = new HashMap();
	
	public static final String getTextByCode(final String code){
		return (String)map.get(code);
	}
	
	static {
		map.put("000000","结束已结清");
		map.put("000002","结束已作废");
		map.put("010004","出票已登记");
		map.put("020001","提示承兑待签收");
		map.put("020006","提示承兑已签收");
		map.put("030001","提示收票待签收");
		map.put("030006","提示收票已签收");
		map.put("100001","背书待签收");
		map.put("100006","背书已签收");
		map.put("110101","买断式贴现待签收");
		map.put("110103","买断式贴现已签收待清算");
		map.put("110105","买断式贴现已签收已排队");
		map.put("110106","买断式贴现已签收");
		map.put("110201","回购式贴现待签收");
		map.put("110203","回购式贴现已签收待清算");
		map.put("110205","回购式贴现已签收已排队");
		map.put("110206","回购式贴现已签收");
		map.put("110216","回购式贴现已至赎回开放日");
		map.put("110218","回购式贴现已逾赎回截止日");
		map.put("120001","回购式贴现赎回待签收");
		map.put("120003","回购式贴现赎回已签收待清算");
		map.put("120005","回购式贴现赎回已签收已排队");
		map.put("120006","回购式贴现赎回已签收");
		map.put("130101","买断式转贴现待签收");
		map.put("130103","买断式转贴现已签收待清算");
		map.put("130105","买断式转贴现已签收已排队");
		map.put("130106","买断式转贴现已签收");
		map.put("130201","回购式转贴现待签收");
		map.put("130203","回购式转贴现已签收待清算");
		map.put("130205","回购式转贴现已签收已排队");
		map.put("130206","回购式转贴现已签收");
		map.put("130216","回购式转贴现已至赎回开放日");
		map.put("130218","回购式转贴现已逾赎回截止日");
		map.put("140001","回购式转贴现赎回待签收");
		map.put("140003","回购式转贴现赎回已签收待清算");
		map.put("140005","回购式转贴现赎回已签收已排队");
		map.put("140006","回购式转贴现赎回已签收");
		map.put("150101","买断式再贴现待签收");
		map.put("150103","买断式再贴现已签收待清算");
		map.put("150105","买断式再贴现已签收已排队");
		map.put("150106","买断式再贴现已签收");
		map.put("150201","回购式再贴现待签收");
		map.put("150203","回购式再贴现已签收待清算");
		map.put("150205","回购式再贴现已签收已排队");
		map.put("150206","回购式再贴现已签收");
		map.put("150216","回购式再贴现已至赎回开放日");
		map.put("150218","回购式再贴现已逾赎回截止日");
		map.put("160001","回购式再贴现赎回待签收");
		map.put("160003","回购式再贴现赎回已签收待清算");
		map.put("160005","回购式再贴现赎回已签收已排队");
		map.put("160006","回购式再贴现赎回已签收");
		map.put("180001","质押待签收");
		map.put("180006","质押已签收");
		map.put("180020","质押已至票据到期日");
		map.put("190001","质押解除待签收");
		map.put("190006","质押解除已签收");
		map.put("170001","保证待签收");
		map.put("200001","提示付款待签收");
		map.put("200003","提示付款已签收待清算");
		map.put("200005","提示付款已签收已排队");
		map.put("200312","提示付款已拒付（可拒付追索，只能追出票人，承兑人及其保证人）");
		map.put("200412","提示付款已拒付（可拒付追索，可以追所有人）");
		map.put("200512","提示付款已拒付（不可进行拒付追索）");
		map.put("210001","逾期提示付款待签收");
		map.put("210003","逾期提示付款已签收待清算");
		map.put("210005","逾期提示付款已签收已排队");
		map.put("210312","逾期提示付款已拒付（可拒付追索，只能追出票人，承兑人及其保证人）");
		map.put("210412","逾期提示付款已拒付（可拒付追索，可以追所有人）");
		map.put("220607","拒付追索待清偿");
		map.put("220707","非拒付追索待清偿");
		map.put("220710","非拒付追索已撤销");		
		map.put("230601","拒付追索同意清偿待签收");
		map.put("230606","拒付追索同意清偿已签收");
		map.put("230701","非拒付追索同意清偿待签收");
		map.put("230706","非拒付追索同意清偿已签收");
		map.put("250001","央行卖票待签收");
		map.put("250003","央行卖票已签收待清算");
		map.put("250005","央行卖票已签收已排队");
		map.put("250006","央行卖票已签收");
		map.put("010151","要约待承诺");
		map.put("010152","要约已失效");
		map.put("010153","要约已撤销");
		map.put("010154","要约已拒绝");
		map.put("020255","合同已成立");
		map.put("020256","合同已成立待解除");
		map.put("010257","合同已解除");
	}
}
