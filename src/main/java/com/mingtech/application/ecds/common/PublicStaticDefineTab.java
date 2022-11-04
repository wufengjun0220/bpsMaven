
package com.mingtech.application.ecds.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.EdraftStatus;

/**
 *
 * <p>
 * Description:公用静态定义文件
 * </p>
 *
 * @author Thinker Jun 1, 2009 5:55:53 PM
 */
public class PublicStaticDefineTab {
	//工具类必须要定义一个私有的构造器-弱扫
	private PublicStaticDefineTab(){}
	
	/**
	 * 票据池规则管理定义状态值
	 */
	public static final String SP001 = "SP_001"; // 已生效
	public static final String SP002 = "SP_002"; // 未生效
	public static final String SP003 = "SP_003"; // 复核
	public static final String SP004 = "SP_004"; // 已关闭
	public static final String SP005 = "SP_005"; // 已暂停
	
	
	/** ********************* 报文状态定义 ************************ */

	/* 交易已撤销 */
	public static final String TY001 = "TY_001";
	/* 票据已作废 */
	public static final String TY002 = "TY_002";
	/* 未承兑 */
	public static final String TY003 = "TY_003";
	/* 已承兑 */
	public static final String TY004 = "TY_004";
	/* 已结清 */
	public static final String TY005 = "TY_005";
	/* 已止付 */
	public static final String TY006 = "TY_006";
	
	/* 未发送 */
	public static final String BW000 = "BW_000";
	/* 待发送 */
	public static final String BW001 = "BW_001";
	/* 已发送 */
	public static final String BW002 = "BW_002";
	/* 人行已拒绝 */
	public static final String BW004 = "BW_003";
	/* 人行已确认 */
	public static final String BW005 = "BW_004";
	/* 发送失败 */
	public static final String BW006 = "BW_005";

	/** ******************** 票据状态定义 ************************* */

	/* 待清分失败状态 */
	public static final String QF001 = "QF_001";
	/* 清分失败状态 已发送待确认  */
	public static final String QF002 = "QF_002";
	/* 清分失败状态 */
	public static final String QF000 = "QF_000";
	/* 线上清算失败 */
	public static final String QS000 = "QS_000";
	
	/** 票据查验申请 查询票据信息状态列表 */
	public static final String BILL_EXAMINE_STATE_LIST = EdraftStatus.Register_Registered_Code + ","//出票已登记
	        + EdraftStatus.Acceptance_Signed_Code + ","//提示承兑已签收
	        + EdraftStatus.Issuance_Signed_Code + ","//提示收票已签收
	        + EdraftStatus.Endorsement_Signed_Code + ","//背书已签收
	        + EdraftStatus.Discount_NoRepurchase_Singned_Code + ","//买断式贴现已签收
	        + EdraftStatus.Discount_Repurchase_End_Code + ","//回购式贴现已逾赎回截止日
	        + EdraftStatus.Discount_Repurchase_RedeemSingned_Code + ","//回购式贴现赎回已签收
	        + EdraftStatus.NoRepurchase_Discount_Singned_Code + ","//买断式转贴现已签收
	        + EdraftStatus.Repurchase_Discount_End_Code + ","//回购式转贴现已逾赎回截止日
	        + EdraftStatus.Repurchase_Discount_RedeemSingned_Code + ","//回购式转贴现赎回已签收
	        + EdraftStatus.NoRepurchase_Rediscount_Singned_Code + ","//买断式再贴现已签收
	        + EdraftStatus.Repurchase_Rediscount_End_Code + ","//回购式再贴现已逾赎回截止日
	        + EdraftStatus.Repurchase_Rediscount_RedeemSingned_Code + ","//回购式再贴现赎回已签收
	        + EdraftStatus.RepurchasedCollateralization_Signed_Code + ","//质押解除已签收
	        + EdraftStatus.CenterBank_SellSigned_Code ;//央行卖票已签收状态

	/** 发出托收 查询票据信息状态列表 */
	public static final String FT_QRY_EDRAFT_STATE_LIST = EdraftStatus.Endorsement_Signed_Code//背书已签收
			+ ","
			+ EdraftStatus.Discount_NoRepurchase_Singned_Code //买断式贴现已签收
			+ ","
			+ EdraftStatus.Discount_Repurchase_End_Code //回购式贴现已逾赎回截止日
			+ ","
			+ EdraftStatus.NoRepurchase_Discount_Singned_Code //买断式转贴现已签收
			+ ","
			+ EdraftStatus.Repurchase_Discount_End_Code //回购式转贴现已逾赎回截止日
			+ ","
			+ EdraftStatus.Repurchase_Discount_RedeemSingned_Code //回购式转贴现赎回已签收
			+ ","
			+ EdraftStatus.Repurchase_Rediscount_RedeemSingned_Code //回购式再贴现赎回已签收
			+ ","
			+ EdraftStatus.Collateralization_End_Code//质押已至票据到期日
			+ ","
			+ EdraftStatus.Presentation_Refused_Code //提示付款已拒付状态文本（可拒付追索，只能追出票人，承兑人及其保证人）
			+ ","
			+ EdraftStatus.Presentation_RefusedAll_Code //提示付款已拒付状态文本（可拒付追索，可以追所有人）
			+ ","
			+ EdraftStatus.Presentation_RefusedNo_Code //提示付款已拒付状态文本 （不可进行拒付追索）
			+ ","
			+ EdraftStatus.OverduePresentation_Refused_Code //逾期提示付款已拒付状态文本（可拒付追索，只能追出票人，承兑人及其保证人）
			+ ","
			+ EdraftStatus.OverduePresentation_RefusedAll_Code //逾期提示付款已拒付状态文本（可拒付追索，可以追所有人）
			+ ","
			+ EdraftStatus.CenterBank_SellSigned_Code //央行卖票已签收状态
			+ ","
			+ EdraftStatus.RepurchasedCollateralization_Signed_Code//质押解除已签收
			+ ","
			+ EdraftStatus.Repurchase_Rediscount_End_Code//回购式再贴现已逾赎回截止日
			+ ","
			+ EdraftStatus.NoRepurchase_Rediscount_Singned_Code//买断式再贴现已签收
			+ ","
			+ EdraftStatus.Discount_Repurchase_RedeemSingned_Code//回购式贴现赎回已签收
			+ ","
			+ EdraftStatus.Issuance_Signed_Code;//提示收票已签收;
			

	/** 贴现申请 贴现业务查询大票表符合的状态* */
	/* 人行状态 */
	public static final String QUERY_BIG_TABLE_STATUS = EdraftStatus.Endorsement_Signed_Code
			+ "," // 背书转让已签收
			+ EdraftStatus.Issuance_Signed_Code + ","// 提示收票已签收
			+ EdraftStatus.Discount_Repurchase_Singned_Code + ","// 回购式贴现已签收
			+ EdraftStatus.Discount_NoRepurchase_Singned_Code + ","// 买断式贴现已签收
			+ EdraftStatus.RepurchasedCollateralization_Signed_Code + ","// 质压解除已签收
			+ EdraftStatus.Discount_Repurchase_RedeemSingned_Code ;// 回购式贴现赎回已签收
			
	public static final String QUERY_BIG_TABLE_STATUS_EX = EdraftStatus.Endorsement_Signed_Code
			+ "," // 背书转让已签收
			+ EdraftStatus.Issuance_Signed_Code + ","// 提示收票已签收
			+ EdraftStatus.Discount_Repurchase_Singned_Code + ","// 回购式贴现已签收
			+ EdraftStatus.Discount_NoRepurchase_Singned_Code + ","// 买断式贴现已签收
			+ EdraftStatus.RepurchasedCollateralization_Signed_Code;// 质压解除已签收
	/** 发起保证业务查询大票状态* */
	public static final String GUARANTEE_APPLY_ECDS_STATUS = EdraftStatus.Register_Registered_Code
			+ "," + // 1出票已登记
			EdraftStatus.Acceptance_Signed_Code + "," + // 2提示承兑已签收
			EdraftStatus.Issuance_Signed_Code + "," + // 3提示收票已签收
			EdraftStatus.Endorsement_Signed_Code + "," + // 4背书已签收
			EdraftStatus.Discount_NoRepurchase_Singned_Code + "," + // 5买断式贴现已签收
			EdraftStatus.Discount_Repurchase_RedeemSingned_Code + "," + // 6回购式贴现赎回已签收
			EdraftStatus.Discount_Repurchase_End_Code + "," + // 7回购式贴现赎回已逾截止日
			EdraftStatus.NoRepurchase_Discount_Singned_Code + "," + // 8买断式转贴现已签收
			EdraftStatus.Repurchase_Discount_RedeemSingned_Code + "," + // 9回购式转贴现赎回已签收
			EdraftStatus.Repurchase_Discount_End_Code + "," + // 10回购式转贴现已逾赎回截止日
			EdraftStatus.NoRepurchase_Rediscount_Singned_Code + "," + // 11买断式再贴现已签收
			EdraftStatus.Repurchase_Rediscount_RedeemSingned_Code + "," + // 12回购式再贴现赎回已签收
			EdraftStatus.Repurchase_Rediscount_End_Code + "," + // 13回购式再贴现已逾赎回截止日
			EdraftStatus.CenterBank_SellSigned_Code + "," + // 14央行卖票已签收
			EdraftStatus.RepurchasedCollateralization_Signed_Code; // 15质压解除已签收
	
	public static final String GUARANTEE_APPLY_ECDS_STATUS2 = EdraftStatus.Register_Registered_Code
		+ "," + // 1出票已登记
		EdraftStatus.Issuance_Signed_Code + "," + // 3提示收票已签收
		EdraftStatus.Endorsement_Signed_Code + "," + // 4背书已签收
		EdraftStatus.Discount_NoRepurchase_Singned_Code + "," + // 5买断式贴现已签收
		EdraftStatus.Discount_Repurchase_RedeemSingned_Code + "," + // 6回购式贴现赎回已签收
		EdraftStatus.Discount_Repurchase_End_Code + "," + // 7回购式贴现赎回已逾截止日
		EdraftStatus.NoRepurchase_Discount_Singned_Code + "," + // 8买断式转贴现已签收
		EdraftStatus.Repurchase_Discount_RedeemSingned_Code + "," + // 9回购式转贴现赎回已签收
		EdraftStatus.Repurchase_Discount_End_Code + "," + // 10回购式转贴现已逾赎回截止日
		EdraftStatus.NoRepurchase_Rediscount_Singned_Code + "," + // 11买断式再贴现已签收
		EdraftStatus.Repurchase_Rediscount_RedeemSingned_Code + "," + // 12回购式再贴现赎回已签收
		EdraftStatus.Repurchase_Rediscount_End_Code + "," + // 13回购式再贴现已逾赎回截止日
		EdraftStatus.CenterBank_SellSigned_Code + "," + // 14央行卖票已签收
		EdraftStatus.RepurchasedCollateralization_Signed_Code; // 15质压解除已签收
	
	
	
	/** 发起背书申请业务查询大票状态* */
	public static final String ENDROSE_APPLY_ECDS_STATUS =
			EdraftStatus.Issuance_Signed_Code + "," + // 3提示收票已签收
			EdraftStatus.Endorsement_Signed_Code + "," + // 4背书已签收
			EdraftStatus.Discount_Repurchase_RedeemSingned_Code + "," + // 6回购式贴现赎回已签收
			EdraftStatus.RepurchasedCollateralization_Signed_Code; // 15质压解除已签收
	
	/**
	 * 承兑业务
	 */
	/* 新增票据（未出票登记） */
	public static final String CD001 = "CD_001";
	/* 出票登记申请（对人行发出票登记报文） */
	public static final String CD002 = "CD_002";
	/**
	 *  提示承兑（人行返回033确认报文）
	 *  出票人已经发出提示承兑申请 
	 */
	public static final String CD003 = "CD_003";
	/**
	 * 承兑申请  已加入批次
	 */
	public static final String CD0031 = "CD_003_1";
	
	
	/* 出票已登记（人行返回005确认报文） */
	public static final String CD004 = "CD_004";
	/* 出票登记被拒绝（人行返回033拒绝报文） */
	public static final String CD005 = "CD_005";
	/* 提示承兑申请（对人行发提示承兑报文） */
	public static final String CD006 = "CD_006";
	/* 提示补录 */
	public static final String CD008 = "CD_008";
	

	/* 提交审批 */
	public static final String CD101 = "CD_101";
	/* 承兑审批中 */
	public static final String CD105 = "CD_105";
	/* 承兑审批同意  */
	public static final String CD110 = "CD_110";
	/* 承兑审批不同意  */
	public static final String CD111 = "CD_111";
	/**
	 * 费用收取完毕
	 */
	public static final String CD112 = "CD_112";
	/**
	 *承兑回复申请（对人行发承兑回复报文） 
	 * 银行已发承兑签收报文
	 */
	public static final String CD109 = "CD_109";
	/**
	 *  承兑确认签收
	 *  已经收到   人行回复的签收 033
	 */
	public static final String CD103 = "CD_103";
	/* 承兑记账通知核心 */
	public static final String CD104 = "CD_104";
	
	/** --------------记账状态-----------------  */
	/* 承兑未记账通知 */
	public static final String CD106 = "CD_106";
	/* 承兑已记账通知 */
	public static final String CD107 = "CD_107";
	/* 承兑记账完毕 */
	public static final String CD108 = "CD_108";

	/* 承兑记账完毕(废弃) */
	public static final String CD201 = "CD_201";
	/* 承兑驳回  */
	public static final String CD202 = "CD_202";
	/* 承兑记账不通过 */
	public static final String CD203 = "CD_203";

	/* 提示收票申请（对人行发提示收票报文） */
	public static final String CD007 = "CD_007";
	/* 提示收票  */
	public static final String CD301 = "CD_301";
	/* 收款人已签收  */
	public static final String CD302 = "CD_302";
	/* 未用退回申请 （对人行发撤票报文） */
	public static final String CD303 = "CD_303";
	/* 银行未用退回确认 */
	public static final String CD304 = "CD_304";
	/* 银行未用退回记账完毕 */
	public static final String CD305 = "CD_305";
	/* 收款人已驳回  */
	public static final String CD306 = "CD_306";
	/* 收票回复申请（对人行发收票回复报文） */
	public static final String CD307 = "CD_307";

	/* 已经提示付款申请 */
	public static final String CD401 = "CD_401";
	/* 提示付款签收 */
	public static final String CD402 = "CD_402";
	/* 提示付款拒付 */
	public static final String CD403 = "CD_403";
	/* 解付记账完成 */
	public static final String CD404 = "CD_404";
	/* 解付记账通知 */
	public static final String CD405 = "CD_405";
	/* 解付已签收 */
	public static final String CD406 = "CD_406";
	/* 已挂失止付 */
	public static final String CD407 = "CD_407";
	/* 止付已解除 */
	public static final String CD408 = "CD_408";
	/* 已打印未记账 */
	public static final String CD409 = "CD_409";
	/* 记账成功发送核心打印失败 */
	public static final String CD410 = "CD_410";
	

	/* 保证待申请 */
	public static final String BZ000 = "BZ_000";
	/* 保证申请（对人行发保证申请报文） */
	public static final String BZ001 = "BZ_001";
	/* 保证申请签收  */
	public static final String BZ010 = "BZ_010";
	/* 保证申请驳回 */
	public static final String BZ011 = "BZ_011";
	/* 保证签收记账 */
	public static final String BZ012 = "BZ_012";
	/* 保证签收记账通知 */
	public static final String BZ013 = "BZ_013";
	/* 保证申请待回复  */
	public static final String BZ003 = "BZ_003";
	/* 保证回复申请  */
	public static final String BZ004 = "BZ_004";
	/* 保证申请被拒绝  */
	public static final String BZ005 = "BZ_005";

	/**
	 * 托收业务
	 */

	/* 撤回提示付款 */
	public static final String TS000 = "TS_000";
	/* 待提示付款 */
	public static final String TS001 = "TS_001";
	/* 提示付款待签收 */
	public static final String TS002 = "TS_002";
	/* 提示付款签收完毕 */
	public static final String TS003 = "TS_003";
	/* 提示付款驳回 */
	public static final String TS004 = "TS_004";
	/* 记账通知 */
	public static final String TS005 = "TS_005";
	/* 记账完毕 */
	public static final String TS006 = "TS_006";
	/* 记账撤回 */
	public static final String TS007 = "TS_007";
	/* 提示付款签收（对人行发提示付款确认签收报文） */
	public static final String TS101 = "TS_101";
	/* 提示付款拒付（对人行发提示付款拒付报文） */
	public static final String TS102 = "TS_102";
	/* 回款记账完毕 */
	public static final String TS103 = "TS_103";
	/* 回款记账通知 */
	public static final String TS104 = "TS_104";

	/**
	 * 追索
	 */
	/* 撤回追索 */
	public static final String ZS000 = "ZS_000";
	/* 追索申请待清偿 */
	public static final String ZS001 = "ZS_001";
	/* 同意清偿 */
	public static final String ZS002 = "ZS_002";
	/* 同意清偿撤回 */
	public static final String ZS011 = "ZS_011";
	/* 同意清偿待签收 */
	public static final String ZS010 = "ZS_010";
	/* 同意清偿签收(对人行发同意清偿签收报文） */
	public static final String ZS003 = "ZS_003";
	/* 同意清偿驳回（对人行发同意清偿驳回报文） */
	public static final String ZS004 = "ZS_004";
	/* 同意清偿记账 */
	public static final String ZS005 = "ZS_005";
	/* 追索申请驳回 */
	public static final String ZS006 = "ZS_006";
	/* 同意清偿记账通知 */
	public static final String ZS007 = "ZS_007";
	/* 他行同意清偿签收完成 */
	public static final String ZS008 = "ZS_008";
	/* 追索通知待申请 */
	public static final String ZS009 = "ZS_009";
	/* 同意清偿申请失败 */
	public static final String ZS012 = "ZS_012";
	
	//已发同意清偿押申请
	public static final String HAVE_DISCHARGE_APPLYED = "1";	
	//未发同意清偿申请
	public static final String HAVE_DISCHARGE_APPLYING = "0";

	/**
	 * 贴现业务
	 */
	/* 批次状态 */
	/* 贴现申请 */
	public static final String TX501 = "TX_501";
	/* 未提交审批 */
	public static final String TX502 = "TX_502";
	/* 已提交审批 */
	public static final String TX503 = "TX_503";
	/* 审核中 */
	public static final String TX504 = "TX_504";
	/* 审批通过 */
	public static final String TX505 = "TX_505";
	/* 审批不通过 */
	public static final String TX506 = "TX_506";
	/* 贴现签收完毕 */
	public static final String TX507 = "TX_507";
	/* 贴现驳回 */
	public static final String TX50701 = "TX_507_01";
	/* 贴现记账完毕 */
	public static final String TX508 = "TX_508";
	/* 贴现记账通知 */
	public static final String TX509 = "TX_509";
	/* 贴现记账撤回 */
	public static final String TX50901 = "TX_509_1";
	/* 查询查复通过 */
	public static final String TX531 = "TX_531";
	/* 查询查复不通过 */
	public static final String TX532 = "TX_532";
	/* 已发查询查复 */
	public static final String TX533 = "TX_533";

	/* 批次状态 */
	/* 贴现赎回申请批次生成 */
	public static final String TX511 = "TX_511";
	/* 撤销审批 */
	public static final String TX512 = "TX_512";
	/* 已提交审批 */
	public static final String TX513 = "TX_513";
	/* 审核中 */
	public static final String TX514 = "TX_514";
	/* 审批通过 */
	public static final String TX515 = "TX_515";
	/* 审批不通过 */
	public static final String TX516 = "TX_516";
	/* 贴现赎回申请待签收 */
	public static final String TX517 = "TX_517";
	/* 贴现赎回申请撤回 ） */
	public static final String TX518 = "TX_518";
	/* 贴现赎回签收完毕 */
	public static final String TX519 = "TX_519";
	/* 贴现赎回驳回完毕 */
	public static final String TX51901 = "TX_519_01";
	/* 贴现赎回记账通知 */
	public static final String TX520 = "TX_520";
	/* 贴现赎回记账完毕 */
	public static final String TX521 = "TX_521";
	/*查询查复通过*/
	public static final String TX529 = "TX_529";
	/*查询查复不通过*/
	public static final String TX522 = "TX_522";
	/* 已发查询查复 */
	public static final String TX523 = "TX_523";
	
	/* 处理标识 */
	public static final String QUERY_BIG_TABLE_SIGN = "DS_001" + "," + "DS_002";

	/* 明细状态 */
	/* 贴现待申请 */
	public static final String TX100 = "TX_100";
	/* 撤回贴现申请 */
	public static final String TX000 = "TX_000";
	/* 贴现申请待签收 */
	public static final String TX001 = "TX_001";
	/* 已加入贴现批次 */
	public static final String TX002 = "TX_002";
	/* 提交审批 */
	public static final String TX003 = "TX_003";
	/* 审批中 */
	public static final String TX004 = "TX_004";
	/* 审批通过 */
	public static final String TX005 = "TX_005";
	/* 审批不通过 */
	public static final String TX006 = "TX_006";
	
	/* 贴现签收驳回（对人行发贴现驳回报文） */
	public static final String TX007 = "TX_007";
	/* 贴现签收完毕（对人行发贴现签收报文） */
	public static final String TX008 = "TX_008";
	/* 贴现记账完毕 */
	public static final String TX009 = "TX_009";
	/* 贴现记账通知 */
	public static final String TX010 = "TX_010";
	/* 贴现记账撤回 */
	public static final String TX01001 = "TX_010_1";
	/* 已发贴现申请未加入批次人行回复（人行回复贴现申请确认报文） */
	public static final String TX033011 = "TX_033_011";

	/* 赎回贴现加入批次 */
	public static final String TX011 = "TX_011";
	/* 赎回贴现未加入批次 */
	public static final String TX012 = "TX_012";
	/* 撤销审批 */
	public static final String TX013 = "TX_013";
	/* 提交审批 */
	public static final String TX014 = "TX_014";
	/* 审批中 */
	public static final String TX015 = "TX_015";
	/* 审批通过 */
	public static final String TX016 = "TX_016";
	/* 审批不通过 */
	public static final String TX017 = "TX_017";
	/* 贴现赎回申请待签收 */
	public static final String TX018 = "TX_018";
	/* 贴现赎回申请撤回 */
	public static final String TX019 = "TX_019";
	/* 贴现赎回记账通知 */
	public static final String TX020 = "TX_020";
	/* 贴现赎回签收 */
	public static final String TX021 = "TX_021";
	/* 贴现赎回驳回 */
	public static final String TX022 = "TX_022";
	/* 贴现赎回记账完毕 */
	public static final String TX023 = "TX_023";
	
	/* 待发贴现赎回申请 */
	public static final String TX040 = "TX_040";
	/* 未发贴现赎回申请 */
	public static final String TX041 = "TX_041";
	/* 已发贴现赎回申请 */
	public static final String TX042 = "TX_042";

	/**
	 * 转贴现业务
	 */
	/* 批次状态 */
	/* 未提交审批 */
	public static final String ZT501 = "ZT_501";
	/* 已提交审批 */
	public static final String ZT502 = "ZT_502";
	/* 审核中 */
	public static final String ZT503 = "ZT_503";
	/* 审批通过 */
	public static final String ZT504 = "ZT_504";
	/* 审批不通过 */
	public static final String ZT505 = "ZT_505";
	/* 转贴现卖出申请（待确认） */
	public static final String ZT5061 = "ZT_5061";
	/* 转贴现卖出申请（被人行拒绝） */
	public static final String ZT5062 = "ZT_5062";
	/* 转贴现卖出申请（卖出申请已确认） */
	public static final String ZT506 = "ZT_506";
	/* 转贴现签收（待确认） */
	public static final String ZT5071 = "ZT_5071";
	/* 转贴现签收（被拒绝） */
	public static final String ZT5072 = "ZT_5072";
	/* 转贴现签收完毕（签收完毕） */
	public static final String ZT507 = "ZT_507";
	/* 转贴现记账完毕 */
	public static final String ZT508 = "ZT_508";
	/* 转贴现买入申请（对人行发转贴现申请报文） */
	public static final String ZT509 = "ZT_509";
	/* 转贴现买入驳回（对人行发转贴现驳回报文） */
	public static final String ZT510 = "ZT_510";
	/** ZT5101 转贴现买入驳回待确认*/
	public static final String ZT5101 = "ZT_5101";
	/** ZT5102 转贴现买入驳回被拒绝*/
	public static final String ZT5102 = "ZT_5102";
	/* 转贴现记账通知 */
	public static final String ZT511 = "ZT_511";
	/*****记账失败*******/
	public static final String ZT512 = "ZT_512";
	/*****额度恢复失败---适用于自动记账的情况*******/
	public static final String ZT513 = "ZT_513";
	/* 转贴现记账完毕-回购类业务补流水 */
	public static final String ZT5081 = "ZT_5081";
	
	/*票据未期标识*/
	public static final String DQ00 = "00";

	/* 明细状态 */

	/* 撤销转贴现申请成功 */
	public static final String ZT000 = "ZT_000";
	
	/** ZT0001 撤销待确认*/
	public static final String ZT0001 ="ZT_0001";
	/** ZT0002 撤销被拒绝 */
	public static final String ZT0002 ="ZT_0002";
	/* 已发转贴现申请未加入批次（对人行发转贴现申请报文） */
	public static final String ZT001 = "ZT_001";
	/* 已加入转贴现批次 */
	public static final String ZT002 = "ZT_002";
	/* 提交审批 */
	public static final String ZT003 = "ZT_003";
	/* 审批中 */
	public static final String ZT004 = "ZT_004";
	/* 审批通过 */
	public static final String ZT005 = "ZT_005";
	/* 审批不通过 */
	public static final String ZT006 = "ZT_006";
	/* 转贴现卖出申请（待确认） */
	public static final String ZT0071 = "ZT_0071";
	/* 转贴现卖出申请（被拒绝） */
	public static final String ZT0072 = "ZT_0072";
	/* 转贴现卖出申请（对人行发转贴现申请报文） */
	public static final String ZT007 = "ZT_007";
	/* 转贴现申请驳回（对人行发转贴现驳回报文、被对方驳回） */
	public static final String ZT008 = "ZT_008";
	/** ZT0081转贴现申请驳回待确认*/
	public static final String ZT0081 = "ZT_0081";
	/** ZT0082 转贴现驳回被拒绝*/
	public static final String ZT0082 = "ZT_0082";
	/* 转贴现签收（待确认） */
	public static final String ZT0091 = "ZT_0091";
	/* 转贴现签收（被拒绝） */
	public static final String ZT0092 = "ZT_0092";
	/* 转贴现签收完毕（对人行发转贴现签收报文） */
	public static final String ZT009 = "ZT_009";
	/** REFUSE_SIGN 拒绝签收*/
	public static final String REFUSE_SIGN="1111";
	/* 转贴现记账完毕 */
	public static final String ZT010 = "ZT_010";
	/* 转贴现记账冲正 */
	public static final String ZT011 = "ZT_011";
	/* 转贴现买入申请（对人行发转贴现申请报文） */
	public static final String ZT012 = "ZT_012";
	/* 转贴现记账通知 */
	public static final String ZT013 = "ZT_013";
	/* 转贴现记账失败 */
	public static final String ZT014 = "ZT_014";
	/* 转贴现记账完毕-回购类业务补流水 */
	public static final String ZT0101 = "ZT_0101";
	/*转贴现提前提醒天数 */
	public static final String ZTTX = "3";
	/*前台轮询持续的时间 单位:小时*/
    public static final int    ZTTXD = 5;
    /*前提提示的消息名称*/
    public static final String ZTTXM =  "转贴现赎回提醒";
    public static final String ZTTX_TN = "快到转贴现赎回截止日票据";
    
	/**
	 * 质押
	 */
	/* 质押待申请 */
	public static final String ZY000 = "ZY_000";
	/* 质押申请（对人行发质押申请报文） */
	public static final String ZY001 = "ZY_001";
	/* 质押签收确认（对人行发质押签收确认报文） */
	public static final String ZY002 = "ZY_002";
	/* 质押驳回（对人行发质押驳回报文） */
	public static final String ZY003 = "ZY_003";
	/* 质押记账完毕 */
	public static final String ZY004 = "ZY_004";
	/* 质押保证金帐号关联完毕 */
	public static final String ZY005 = "ZY_005";
	/* 质押申请撤销 */
	public static final String ZY006 = "ZY_006";
	/* 质押记账通知 */
	public static final String ZY007 = "ZY_007";
	
	//河北银行---------
	/* 质押提交审批 */
	public static final String ZY008 = "ZY_008";
	/* 质押审批通过 */
	public static final String ZY009 = "ZY_009";
	/* 质押审批不通过 */
	public static final String ZY010 = "ZY_010";
	
	
	/* 质押入库 */
	public static final String ZY011 = "ZY_011";
	
	
	//河北银行---------
	/* 质押-业务类型能 */
	/* 质押 */
	public static final String OPERATION_TYPE_COLLATERAL = "0";
	/* 解质押 */
	public static final String OPERATION_TYPE_UNCOLLATERAL = "1";
	//已发解质押申请
	public static final String UN_COLLZTN_APPLYED = "1";	
	//未发解质押申请
	public static final String UN_COLLZTN_APPLYING = "0";
	//已至票据到期日
	public static final String UN_COLLZTN_END = "2";
	


	/**
	 * 解质押
	 */
	/* 解质押待申请 */
	public static final String JY000 = "JY_000";
	/* 解质押申请（对人行发解质押申请报文） */
	public static final String JY001 = "JY_001";
	/* 解质押签收确认（对人行发解质押签收确认报文） */
	public static final String JY002 = "JY_002";
	/* 解质押驳回（对人行发解质押驳回报文） */
	public static final String JY003 = "JY_003";
	/* 解质押记账完毕 */
	public static final String JY004 = "JY_004";
	/* 解质押申请撤销 */
	public static final String JY005 = "JY_005";
	/* 解质押记账通知 */
	public static final String JY006 = "JY_006";
	
	/* 解质押出库 */
	public static final String JY007 = "JY_007";
	
	/**
	 * 背书
	 */
	/* 背书待申请 */
	public static final String BS000 = "BS_000";
	/* 背书申请 */
	public static final String BS001 = "BS_001";
	/* 背书申请被拒绝 */
	public static final String BS002 = "BS_002";
	/* 背书申请待回复 */
	public static final String BS003 = "BS_003";
	/* 背书回复申请 */
	public static final String BS004 = "BS_004";
	/* 背书签收 */
	public static final String BS005 = "BS_005";
	/* 背书驳回 */
	public static final String BS006 = "BS_006";
	
	
	
	/* 新增合同  */
	public static final String HT001 = "HT_001";
	/* 合同申请（对人行发电子合同要约报文） */
	public static final String HT002 = "HT_002";
	/* 合同待承诺   */
	public static final String HT003 = "HT_003";
	/* 合同申请失败 (人行033处理失败)  */
	public static final String HT004 = "HT_004";
	/* 合同已成立   */
	public static final String HT005 = "HT_005";
	/* 合同被拒绝   */
	public static final String HT006 = "HT_006";
	/* 合同回复申请   */
	public static final String HT007 = "HT_007";
	/* 已过承诺截止日   */
	public static final String HT008 = "HT_008";

	
	
	/*新增合同解除申请*/
	public static final String HTJC001 = "HTJC_001";
	/*新增合同解除撤回*/
	public static final String HTJC002 = "HTJC_002";
	/*合同协商解除待签收*/
	public static final String HTJC003 = "HTJC_003";
	/*合同协商解除已签收*/
	public static final String HTJC004 = "HTJC_004";
	/*合同协商解除已驳回*/
	public static final String HTJC005 = "HTJC_005";
	/*合同已通知解除*/
	public static final String HTJC006 = "HTJC_006";
	
	/*合同解除方式*/
	public static final String HTJC_RSCISN_MRKR_XSJC = "RM01";//协商解除
	public static final String HTJC_RSCISN_MRKR_TZJC = "RM02";//通知解除
	
	
	/*金融机构类型*/
	/*国有银行*/
	public static final String ORG_TYPE_ST_OWNED_BK = "TYPE_01";
	/*商业银行*/
	public static final String ORG_TYPE_COMM_BK = "TYPE_02";
	/*外资银行*/
	public static final String ORG_TYPE_FOREIGN_BK = "TYPE_03";
	/*其他金融银行*/
	public static final String ORG_TYPE_OTHER_ORG = "TYPE_04";

	
	/* 持有状态 */
	public static final String CY001 = "CY_001";

	/* 不持有状态 */
	public static final String NCY001 = "NCY_001";


	/* 额度占用对象类型（承兑人、申请人、第三方） */
	/* 承兑人 */
	public static final String LIMIT_OBJECT_CLASS_ACCEPTOR = "01";
	/* 申请人 */
	public static final String LIMIT_OBJECT_CLASS_OWNER = "02";
	/* 第三方 */
	public static final String LIMIT_OBJECT_CLASS_OTHER = "03";

	/** CREDIT_SET_NOSET 额度设置 未设置*/
	public static final String CREDIT_SET_NOSET="01";
	/** CREDIT_SET_PARTSET 额度设置 部分设置*/
	public static final String CREDIT_SET_PARTSET="02";
	/** CREDIT_SET_ALLSET 额度设置 全部设置*/
	public static final String CREDIT_SET_ALLSET="03";
	
	/** CREDIT_PROCESS_SUCCESS 额度扣减 成功*/
	public static final String CREDIT_PROCESS_SUCCESS="01";	
	/** CREDIT_PROCESS_FAILER 额度扣减 失败*/
	public static final String CREDIT_PROCESS_FAILER="02";
	
	/** CREDIT_WAY_TOTAL 扣减方式 全额扣减*/
	public static final String CREDIT_WAY_TOTAL="CW01";
	/** CREDIT_WAY_RATE 扣减方式 比例扣减*/
	public static final String CREDIT_WAY_RATE="CW02";
	/** CREDIT_WAY_AMOUNT 扣减方式 金额扣减*/
	public static final String CREDIT_WAY_AMOUNT="CW03";
	
	/** CREDIT_OBJ_ACCEPTANCE 额度扣减对象 承兑行*/
	public static final String CREDIT_OBJ_ACCEPTANCE="CO01";	
	/** CREDIT_OBJ_APPLYER 额度扣减对象 交易对手*/
	public static final String CREDIT_OBJ_APPLYER="CO02";
	/** CREDIT_OBJ_PAYER 额度扣减对象 付款人*/
	public static final String CREDIT_OBJ_PAYER="CO03";
	/** CREDIT_OBJ_THIRD 额度扣减对象 第三方*/
	public static final String CREDIT_OBJ_THIRD="CO04";
	
	/** CREDIT_OBJTYPE_COMPANY 额度扣减对象类型 对公*/
	public static final String CREDIT_OBJTYPE_COMPANY="CT01";
	/** CREDIT_OBJTYPE_COMPANY 额度扣减对象类型 同业*/
	public static final String CREDIT_OBJTYPE_BANK="CT02";
	
	/**
	 * 额度占用类型  00 预占用 
	 */
	public static final String CREDIT_OCCP_TYPE_00="00";
	/**
	 * 额度占用类型  01 占用 
	 */
	public static final String CREDIT_OCCP_TYPE_01="01";
	/**
	 * 额度占用类型  02 释放
	 */
	public static final String CREDIT_OCCP_TYPE_02="02";
	/**额度占用交易类型 贴现 转贴现**/
	/** CREDITQUTOA_USED_DISCOUNT  额度占用交易类型 贴现*/
	public static final String CREDITQUTOA_USED_DISCOUNT = "01";

	/** CREDITQUTOA_USED_REDISCOUNT 额度占用交易类型 转贴现*/
	public static final String CREDITQUTOA_USED_REDISCOUNT = "02";

	/** CREDITQUTOA_USED_ZY 额度占用交易类型 质押*/
	public static final String CREDITQUTOA_USED_ZY = "03";
	
	/** CREDITQUTOA_USED_CD 额度占用交易类型 承兑*/
	public static final String CREDITQUTOA_USED_CD = "04";

	/**交易额度释放类型 转贴现卖出、结清、追索同意清偿、未用撤回**/
	/** CREDITQUTOA_RELEASE_REDISCOUNT 交易额度释放类型 转帖卖出*/
	public static final String CREDITQUTOA_RELEASE_REDISCOUNT = "01";

	/** CREDITQUTOA_RELEASE_SETTLED 交易额度释放类型 结清*/
	public static final String CREDITQUTOA_RELEASE_SETTLED = "02";

	/** CREDITQUTOA_RELEASE_AGREEMENT 交易额度释放类型 追索同意清偿*/
	public static final String CREDITQUTOA_RELEASE_AGREEMENT = "03";

	/** CREDITQUTOA_RELEASE_NOUSE 交易额度释放类型 未用撤回*/
	public static final String CREDITQUTOA_RELEASE_NOUSE = "04";

	/** CREDITQUTOA_RELEASE_JZY 交易额度释放类型 解质押*/
	public static final String CREDITQUTOA_RELEASE_JZY = "05";
	
	/** CREDITQUTOA_RELEASE_HGTXSH 交易额度释放类型 回购式贴现赎回*/
	public static final String CREDITQUTOA_RELEASE_HGTXSH = "06";
	/** CREDITQUTOA_RELEASE_TSFK 交易额度释放类型 提示付款*/
	public static final String CREDITQUTOA_RELEASE_TSFK = "07";
	/** CREDITQUTOA_RELEASE_YQ_TSFK 交易额度释放类型 逾期提示付款*/
	public static final String CREDITQUTOA_RELEASE_YQ_TSFK = "08";
	/**
	 * 纸票托收回款
	 */
	public static final String CREDITQUTOA_RELEASE_TS = "09";

	/** ******************** 其他静态参数定义 ************************* */

	/** VALUE_YES 是*/
	public static final String VALUE_YES="Y";
	/** VALUE_NO 否*/
	public static final String VALUE_NO="N";
	
	public static final String OVERDUE_DAY="ECD03010";
	public static final String DeferBusi_DAY ="ECD03008";//商业承兑汇票提示付款应答日期(单位:天)

	/* 票据类型 */
	/* 银承 */
	public static final String BILL_TYPE_BANK = "AC01";
	/* 商承 */
	public static final String BILL_TYPE_BUSI = "AC02";

	/* 票据介质 */
	/* 纸质 */
	public static final String BILL_MEDIA_PAPERY = "1";
	/* 电子 */
	public static final String BILL_MEDIA_ELECTRONICAL = "2";

	//public static final String SAME_CITY_00 = "0"; //同城
	//public static final String SAME_CITY_01 = "1"; //异地
	/* 审核 */
	/* 同意 */
	public static final String APPROVE_YES = "1";
	/* 不同意 */
	public static final String APPROVE_NO = "0";

//	/* 质权人名称 */
//	public static final String COLLZTNBK_NAME = "北京银行";


	/** 同业额度管理状态 */
	public static final String VALID = "1";// 生效
	public static final String INVALID = "2";// 失效
	public static final String DELETED = "3";// 无效
	public static final String OUTDATE = "4";// 过期

	/* 票据类型 */
	/* 银票 */
	public static final String BILL_TYPE_BANK_NAME = "银票";
	/* 商票 */
	public static final String BILL_TYPE_BUSI_NAME = "商票";

	/* 票据介质 */
	/* 纸质 */
	public static final String BILL_MEDIA_PAPERY_NAME = "纸质";
	public static final String BILL_MEDIA_PAPERY_CODE = "1";
	/* 电子 */
	public static final String BILL_MEDIA_ELECTRONICAL_NAME = "电子";
	public static final String BILL_MEDIA_ELECTRONICAL_CODE = "2";
	
	/* 扣费标识 */
	public static final String CHARGE_Y = "1";//已扣费

	/* 银票贴现利率下限 */
	public static final String DICOUNT_BUSIBILL_RATE = "1";
	/* 商票贴现利率下限 */
	public static final String DICOUNT_BANKBILL_RATE = "2";
	/* 转贴现买断利率下限 */
	public static final String REDICOUNT_BUY_RATE = "3";
	/* 转贴现回购利率下限 */
	public static final String REDICOUNT_SELL_RATE = "4";

	/* 利率方式 分为：年 月 日 */
	/* 年利率 */
	public static final String DISCOUNT_RATE_YEAR = "RATE_Y";
	/* 月利率 */
	public static final String DISCOUNT_RATE_MONTH = "RATE_M";
	/* 日利率 */
	public static final String DISCOUNT_RATE_DATE = "RATE_D";

	/* 节假日标识 0非节假日 */
	public static final String HOLIDAY_IF_HOLIDAY_NO = "0";
	/* 节假日标识 1是节假日 */
	public static final String HOLIDAY_IF_HOLIDAY_YES = "1";

	/* 追索类型 0追索 */
	public static final String RECOURSE_TYPE_TROVER = "0";
	/* 追索类型 1被追索 */
	public static final String RECOURSE_TYPE_PSVTTROVER = "1";

	/* 提示付款产品类型 */
	public static final String PRODUCT_TYPE_COLLECTION = "10001";
	/* 买断式贴现产品类型 */
	public static final String PRODUCT_TYPE_MDTX = "1001";
	/* 回购式贴现产品类型 */
	public static final String PRODUCT_TYPE_MCHGTX = "1002";
	/* 贴现卖出产品类型 */
	public static final String PRODUCT_TYPE_MCTX = "6001";
	/* 转贴现买断产品类型 */
	public static final String PRODUCT_TYPE_ZTXBUY = "2001";
	/* 转贴现卖断产品类型 */
	public static final String PRODUCT_TYPE_ZTXSELL = "3001";
	/* 卖出回购式转贴现产品类型 */
	public static final String PRODUCT_TYPE_MCHGZTX = "3002";
	/* 追索产品类型 */
	public static final String PRODUCT_TYPE_RESOURSE = "11001";


	// 角色业务流程种类，用于审批时判断审批金额，分为贴现转贴现业务
	/* 贴现 */
	public static final String PRODUCT_TYPE_DISCOUNT = "DISCOUNT";
	/* 转贴现 */
	public static final String PRODUCT_TYPE_REDISCOUNT = "REDISCOUNT";

	// 类别 RoleCode
	/* 接入行 */
	public static final String RC_00 = "RC00";
	/* 企业 */
	public static final String RC_01 = "RC01";
	/* 人民银行 */
	public static final String RC_02 = "RC02";
	/* 被代理行 */
	public static final String RC_03 = "RC03";
	/* 被代理财务公司 */
	public static final String RC_04 = "RC04";
	/* 接入财务公司 */
	public static final String RC_05 = "RC05";

	// 追索理由机构代码
	/* 承兑人被依法宣告破产 */
	public static final String Recourse_Reason_Code_RC00 = "RC00";
	/* 承兑人因违法被责令终止活动 */
	public static final String Recourse_Reason_Code_RC01 = "RC01";

	// SignUpMarkCode 2位字母+2位数字编码
	/* 同意签收 */
	public static final String SIGN_MARK_YES = "SU00";
	/* 拒绝签收 */
	public static final String SIGN_MARK_NO = "SU01";

	// RecourseTypeCode 2位字母+2位数字编码
	/* 拒付追索 */
	public static final String RECOURSE_TYPE_YES = "RT00";
	/* 非拒付追索 */
	public static final String RECOURSE_TYPE_NO = "RT01";

	// ConsignmentCode 2位字母+2位数字编码

	/* 含委托/承诺兑付 */
	public static final String CONSIGNMENT_CODE_YES = "CC00";
	/* 不含委托/不承诺兑付 */
	public static final String CONSIGNMENT_CODE_NO = "CC01";

	// TypeMarkCode 2位字母+2位数字编码
	/* 登陆 */
	public static final String TYPE_MARK_CODE_IN = "TM00";
	/* 退出 */
	public static final String TYPE_MARK_CODE_OUT = "TM01";

	// ExceptionCode 2位字母+2位数字编码
	/* 无此帐号 */
	public static final String EXCEPTION_CODE_00 = "EC00";
	/* 无此行号 */
	public static final String EXCEPTION_CODE_01 = "EC01";
	/* 名称不符 */
	public static final String EXCEPTION_CODE_02 = "EC02";
	/* 贴现入账信息账号错误 */
	public static final String EXCEPTION_CODE_03 = "EC03";

	// ProxySignatureCode 2位字母+2位数字编码
	/* 开户机构代理回复签章 */
	public static final String PROXY_SINGNATURE_CODE_00 = "PS00";
	/* 客户自己签章 */
	public static final String PROXY_SINGNATURE_CODE_01 = "PS01";

	// ProxyPropositionCode 2位字母+2位数字编码
	/* 开户机构代理申请签章 */
	public static final String PROXY_PROPOSITION_CODE_00 = "PP00";
	/* 客户自己签章 */
	public static final String PROXY_PROPOSITION_CODE_01 = "PP01";

	/* 转贴现流程审核结果名 */
	public static final String REDISCOUNT_AUDIT_RESULT = "auditResult";
	/* 转贴现流程机构级别 */
	public static final String REDISCOUNT__BRCH_LEVEL = "brchLevel";
	/* 审核路线 */
	public static final String REDISCOUNT_AUDITROUTE = "auditRoute";
	/* 机构id */
	public static final String brch_id = "brch_id";
	/* 产品id */
	public static final String bupr_id = "bupr_id";
	/* 服务名 */
	public static final String serviceName = "rediscountBuyService";

	/* 转贴现买入流程名 */
	public static final String REDISCOUNTBUY_PROCESS_NAME = "rediscountBuy";
	/* 转贴现买入流程批次ID */
	public static final String REDISCOUNTBUY_BATCH_ID = "buybatchId";

	public static final String SALESERVICENAME = "rediscountSaleService";
	/* 转贴现卖出流程名 */
	public static final String REDISCOUNTSALE_PROCESS_NAME = "rediscountSell";
	/* 转贴现卖出流程批次ID */
	public static final String REDISCOUNTSALE_BATCH_ID = "saleBatchId";

	/* 分买支流程名 */
	public static final String BRANCHBUY_PROCESS_NAME = "branchBuy";
	/* 批次ID */
	public static final String BRANCHBUY_BATCH_ID = "saleBatchId";
	/* 审核路线 */
	public static final String BRANCHBUY_AUDITROUTE = "auditRoute";
	/* 机构id */
	public static final String BRANCHBUY_BRCH_LEVEL = "brch_id";
	/* 产品id */
	public static final String BRANCHBUY_PRODID = "prodId";
	/* 服务名 */
	public static final String BRANCHBUY_SERVICENAME = "serviceName";
	/* 审核结果 */
	public static final String BRANCHBUY_AUDIT_RESULT = "auditResult";
	/* 分买支-分替支转贴现申请 */
	public static final String BRANCHBUY_SALE_APPLY = "分替支转贴现申请";
	/* 分买支-分替支记账通知 */
	public static final String BRANCHBUY_SALE_ACCOUNT = "分替支记账通知";
	/* 分买支-分替支转贴现申请 */
	public static final String BRANCHBUY_SALE_BRANCH_SIGNIN = "分行签收";
	/* 分买支-分替支记账通知 */
	public static final String BRANCHBUY_SALE_BRANCH_ACCOUNT = "分行记账通知";

	/* 贴现卖出流程名 */
	public static final String DISCOUNTSALE_PROCESS_NAME = "discountSale";
	/* 贴现卖出消息流程名 */
	public static final String DISCOUNTSALE_MESSAGE_NAME = "discountSaleMsg";
	/* 贴现卖出流程批次ID */
	public static final String DISCOUNTSALE_BATCH_ID = "batchId";
	/* 贴现卖出流程审核结果名 */
	public static final String DISCOUNT_AUDIT_RESULT = "auditResult";
	/* 贴现卖出流程机构级别 */
	public static final String DISCOUNT_BRCH_LEVEL = "bankLevel";
	/* 贴现卖出审核路线 */
	public static final String DISCOUNT_AUDITROUTE = "auditRoute";
	/* 贴现卖出返回状态 */
	public static final String DISCOUNT_RESSTATUS = "resStatus";
	/* 产品ID */
	public static final String DISCOUNTINNER_PROD_ID = "procId";
	/* 机构ID */
	public static final String DISCOUNTINNER_BRCH_ID = "brchId";

	/* 系统内转贴现流程名 */
	public static final String REDISCOUNTINNER_PROCESS_NAME = "rediscountInner";
	/* 系统内转贴现卖出流程批次ID */
	public static final String REDISCOUNTINNER_BATCH_ID = "innerSalebatchId";
//	/* 系统内转贴现买入流程批次ID */
//	public static final String REDISCOUNTINNER_BUY_BATCH_ID = "innerBuybatchId";
	/* 获取总金额服务名 */
	public static final String REDISCOUNTINNER_SERVICE_NAME = "serviceName";
	/*存放卖出方机构ID */
	public static final String REDISCOUNTINNER_BRCH_ID = "brchId";
	/*存放买入方机构ID*/
	public static final String REDISCOUNTINNER_Buy_BRCH_ID = "buyBrchId";
	/* 产品ID */
	public static final String REDISCOUNTINNER_PROD_ID = "prodId";
	/* 验证结果 */
	public static final String REDISCOUNTINNER_AUTH_RESULT = "authResult";

	/** 质押工作流程名 * */
	/* 质押-质押申请处理流程模板id */
	public static String IMPLAWN_APLY_PROCESS_NAME = "implawn_aply_process";
	/* 质押-质押签收处理流程模板id */
	public static String IMPLAWN_PROCESS_NAME = "process_implawn";
	/* 质押明细ID变量名 */
	public static String IMPLAWN_DETAIL_KEY = "implawn_id";
	/* 质押签收标识-变量名 */
	public static String IMPLAWN_SIGN_FLAG = "status";
	/* 票据标识-变量名 */
	public static String BILL_INFO_ID = "bill_info_id";
	/* 票据号码--变量 */
	public static String BILL_NUM = "billNum";
	/* 解质押处理流程模板id */
	public static String UN_IMPLAWN_PROCESS_NAME = "un_implawn_process";
	/* 解质押签收处理流程模板id */
	public static String UN_IMPLAWN_SIGN_PROCESS_NAME = "un_implawn_sign_process";
	/* 解质押消息ID */
	public static String UN_IMPLAWN_MESSAGE = "un_implawn_message";
	/* 消息状态明细-状态名 */
	public static String MESSAGE_STATUS = "stat";
	/* 消息状态明细-明细表id */
	public static String MESSAGE_ID = "mes_id";
	/* 消息中票号名 */
	public static String MESSAGE_SBILLNUM = "sBillNum";
	/* 票据持有人-组织机构代码 */
	public static String IMPLAWN_ORG_CODE = "orgCode";
	/* 默认工作流启动用户id */
	public static String START_USER_ID = "admin";
	public static String CASE_ID = "caseid";
	/* 质押签收/驳回人工节点名 */
	public static String P_NODE_IMPLAWN_SIGN_NAME = "质押签收/驳回";
	/* 质押记账人工节点名 */
	public static String P_NODE_IMPLAWN_ACCNT_NAME = "质押记账";
	/* 关联保证金人工节点 */
	public static String P_NODE_IMPLAWN_BAIL_NAME = "关联保证金";
	/* 解质押记账人工节点 */
	public static String P_NODE_UN_IMPLAWN_ACCT_NAME = "解质押记账";

	/* 审批子流程批次ID变量名 */
	public static String SUB_AUDIT_PROCESS_BATCH_KEY = "batchID";
	/* 审批子流程审批结果变量名 */
	public static String SUB_AUDIT_PROCESS_AUDIT_RESULT_KEY = "approvedResult";
	/* 审批权限 */
	public static String SUB_AUDIT_PROCESS_AUDIT_LIMIT_RESULT = "limitResult";
	/* 审批流程名 */
	public static String SUB_AUDIT_PROCESS_NAME = "sub_audition_process"; // 审核子流程
	/* 买入方审批流程名 */
	public static String SUB_AUDIT_PROCESS_NAME_BUY = "sub_audition_process_buy"; // 买入方审核子流程

	/* 承兑保证类型 */
	/* 出票保证 */
	public static String BILL_ACCURE_TYPE = "01";
	/* 承兑保证 */
	public static String ACPT_ACCURE_TYPE = "02";
	/* 背书保证 */
	public static String ENDO_ACCURE_TYPE = "03";

	/* 不得转让标记 */

	// 可再转让
	public static String NOT_ATTRON_FLAG_NO = "EM00";
	// 不得转让
	public static String NOT_ATTRON_FLAG_YES = "EM01";

	/** 报文类型对应业务类型 */
	/* 背书业务 */
	public static String ECDS_MSG_TYPE_HAND_OVER = "010";
	/* 贴现业务 */
	public static String ECDS_MSG_TYPE_DISCOUNT_APLY = "011";
	/* 转贴现业务 */
	public static String ECDS_MSG_TYPE_REDISCOUNT_APLY = "013";
	/* 再贴现业务 */
	public static String ECDS_MSG_TYPE_REDISCOUNTCB_APLY = "015";
	/* 央行卖票业务 */
	public static String ECDS_MSG_TYPE_REDISCOUNTCBS_APLY = "025";
	/* 保证申请 */
	public static String ECDS_MSG_TYPE_ASUR_APLY = "017";
	/* 质押申请 */
	public static String ECDS_MSG_TYPE_IMPW_APLY = "018";
	/* 解质押申请 */
	public static String ECDS_MSG_TYPE_UNIMPW_APLY = "019";
	/* 提示付款 */
	public static String ECDS_MSG_TYPE_CLEW_PMNT = "020";
	/* 追索 */
	public static String ECDS_MSG_TYPE_CHSE_DMAD = "022";
	/* 追索同意清偿申请 */
	public static String ECDS_MSG_TYPE_AGRE_APLY = "023";

	/** 处理状态 * */
	/* 处理中 */
	public static String DS_001 = "DS_001";
	/* 未处理 */
	public static String DS_002 = "DS_002";
	/** 票据持有类型 */
	/* 本行持有 */
	public static String CD_EDFAFT_OWNER_STS_BHCY = "本行持有";
	/** 承兑业务流程名称 */
	/* 柜面代理 */
	public static String PROCESS_COUNTER_PROXY = "pro_cter_prxy";
	/* 收款人收票 */
	public static String PROCESS_RECEIVE_BILL = "pro_rcve_bill";
	/* 信贷审批 */
	public static String PROCESS_CREDIT_AUDITING = "pro_crdt_adit";
	/* 提示付款 */
	public static String PROCESS_CLEW_PAYMENT = "pro_clew_pymt";
	/* 到期解付签收 */
	public static String PROCESS_MATU_RECEIVE = "pro_matu_rcve";
	/* 撤票 */
	public static String PROCESS_UNTREAD_BILL = "pro_utrd_bill";
	/* 承兑审批流程 */
	public static String PROCESS_ACCEPTION_AUDITING = "pro_acception_audit";
	
	
	/** 业务明细表报文状态 * */
	/* 未发送 */
	public static String MSG_STATUS_NO = "0";
	/* 已发送 */
	public static String MSG_STATUS_YES = "1";
	/* 发送失败 */
	public static String MSG_STATUS_LOST = "2";

	/** 贴现种类 * */
	/* 买断式 */
	public static String DISC_TYPE_BUYSTOP = "RM00";
	/* 回购式 */
	public static String DISC_TYPE_RETURN = "RM01";

	/** 工作流节点定义(与工作流Name必须一致) */
	/* 银行承兑－信贷审批 */
	public static String WF_CRDT_ADIT_SBMT_ADIT = "提交审批";
	public static String WF_CRDT_ADIT_QURY_ADIT_RSUT = "查询审批结果";
	public static String WF_CRDT_ADIT_RCVE_OVRL = "承兑签收/驳回";
	public static String WF_CRDT_ADIT_ACPT_ACCT = "承兑记账通知";
	public static String WF_CRDT_ADIT_QURY_CORE_RSUT = "查询核心记账结果";

	/* 银行承兑－到期付款签收 */
	public static String WF_UNCH_PYMT_ACCOUNT = "到期解付记账";
	public static String WF_UNCH_PYMT_RECEIVE = "到期解付签收";
	public static String WF_UNCH_PYMT_RECEIVE_SHENQING = "到期解付签收申请";

	/* 子流程审批任务名 */
	public static final String AUDIT_NAME = "审批";
	/*买入方审批*/
	public static final String AUDIT_NAME_BUY = "买入方审批";

	/* 卖方发出申请 */
	public static final String INNER_SALE_APLLY = "系统内卖方申请";
	/* 卖方撤消申请 */
	public static final String INNER_SALE_CANCLE_APPLY = "系统内撤回申请";
	/* 买方签收 */
	public static final String INNER_BUY_ASSIGN = "系统内买方签收";
	/* 系统内卖方记账 */
	public static final String INNER_SALE_ACCOUNT = "系统内卖方记账";
	/* 系统内买方记账 */
	public static final String INNER_BUY_ACCOUNT = "系统内买方记账";

	/* 系统外转贴现申请 */
	public static final String REDISC_SALE_APPLY = "系统外转贴现申请";

	/* 系统外记账签收 */
	public static final String REDISC_SALE_ACCOUNT_SIGN = "系统外记账签收";

	/* 转贴现买入触发签收 */
	public static final String REDISC_BUY_SIGN = "触发签收";

	/* 转贴现买入触发记账 */
	public static final String REDISC_BUY_ACCOUNT = "触发记账";

	/** 清算方式 */
	/* 线上 SM00 */
	public static final String BLCE_MODE_ONLN = "SM00";
	/* 线下 SM01 */
	public static final String BLCE_MODE_FUTV = "SM01";

	/** 查询类型 */

	/** OVER_FLAG_WDQ 票据逾期标识--未到期*/
	public static final String CP_QRY_TYP_WDQ = "00";//票据逾期标识--未到期
	/**
	 *票据逾期标识--到期未逾期 
	 * 提示付款 01
	 */
	public static final String CP_QRY_TYP_CLEW_PYMT = "01";
	/* 逾期提示付款 02 */
	public static final String CP_QRY_TYP_OVDU_CLEW_PYMT = "02";//票据逾期标识--已逾期
	/* 提示付款应答 03 */
	public static final String CP_QRY_TYP_CLEW_PYMT_RCVE = "03";
	/* (逾期)提示付款发起撤回 59 */
	public static final String CP_QRY_TYP_CLEW_PYMT_CTMT = "01";

	/* 签收状态 */
	/* 同意签收 */
	public static final String SIGN_UP_MARK_CODE_YES = "SU00";
	/* 拒绝签收 */
	public static final String SIGN_UP_MARK_CODE_NO = "SU01";
	/* 报文 */
	/* 质押申请 */
	public static final String MESSAGE_018 = "018";
	/* 质押撤回 */
	public static final String MESSAGE_032_018 = "032018";
	/* 质押申请应答 */
	public static final String MESSAGE_031_018 = "031018";

	/* 解质押申请 */
	public static final String MESSAGE_019 = "019";
	/* 解质押撤回 */
	public static final String MESSAGE_032_019 = "032019";
	/* 解质押申请应答 */
	public static final String MESSAGE_031_019 = "031019";

	/* 追索通知申请 */
	public static final String MESSAGE_022 = "022";
	/* 追索同意清偿 */
	public static final String MESSAGE_023 = "023";
	/* 追索通知撤回 */
	public static final String MESSAGE_032_022 = "032022";
	/* 追索同意清偿撤回 */
	public static final String MESSAGE_032_023 = "032023";
	/* 追索同意清偿签收 */
	public static final String MESSAGE_033_023 = "033023";
	/* 追索同意清偿应答 */
	public static final String MESSAGE_031_023 = "031023";

	/* 保证申请 */
	public static final String MESSAGE_017 = "017";
	/* 保证撤回 */
	public static final String MESSAGE_032_017 = "032017";
	/* 保证申请应答 */
	public static final String MESSAGE_031_017 = "031017";
	
	

	/** 拒付理由代码 */
	public static final String REFUSE_CODE_00 = "DC00";
	public static final String REFUSE_CODE_00_INFO = "与自己有直接债权债务关系的持票人未履行约定义务";

	public static final String REFUSE_CODE_01 = "DC01";
	public static final String REFUSE_CODE_01_INFO = "持票人以欺诈、偷盗或者胁迫等手段取得票据";

	public static final String REFUSE_CODE_02 = "DC02";
	public static final String REFUSE_CODE_02_INFO = "持票人明知有欺诈、偷盗或者胁迫等情形，出于恶意取得票据";

	public static final String REFUSE_CODE_03 = "DC03";
	public static final String REFUSE_CODE_03_INFO = "持票人明知债务人与出票人或者持票人的前手之间存在抗辩事由而取得票据";

	public static final String REFUSE_CODE_04 = "DC04";
	public static final String REFUSE_CODE_04_INFO = "持票人因重大过失取得不符合《票据法》规定的票据";

	public static final String REFUSE_CODE_05 = "DC05";
	public static final String REFUSE_CODE_05_INFO = "超过提示付款期";

	public static final String REFUSE_CODE_06 = "DC06";
	public static final String REFUSE_CODE_06_INFO = "被法院冻结或收到法院止付通知书";

	public static final String REFUSE_CODE_07 = "DC07";
	public static final String REFUSE_CODE_07_INFO = "票据未到期";

	public static final String REFUSE_CODE_08 = "DC08";
	public static final String REFUSE_CODE_08_INFO = "商业承兑汇票承兑人账户余额不足";

	public static final String REFUSE_CODE_09 = "DC09";
	public static final String REFUSE_CODE_09_INFO = "其他（必须注明）";

	/* 人民币 */
	public static final String CURRENCY_TYPE = "CNY";
	/* 汉口银行核心系统人民币码值 */
	public static final String CURRENCY_TYPE_01 = "01";

	/** Msg031报文回复类型 */
	public static final String MSG031_RVET_TYPE_002 = "002";

	/** 是否系统内 */
	/* 系统内 */
	public static final String SYS_INNER = "01";
	/* 系统外 */
	public static final String SYS_OUTER = "02";
	/* 分买支 */
	public static final String SYS_FEN = "03";
	/**--------记录流水处理标示-------------*/
	public static final String FLOW_DEAL_FLAG_OK="SUCCES";//成功
	public static final String FLOW_DEAL_FLAG_FAIL="ERROR";//失败
	/**---------------票据出入库标示---------------*/
	public static final int BILL_INITIAL = -1;//初始值
	public static final int BILL_IN=0;//入库
	public static final int BILL_OUT=1;//出库

	/* 到期解付登记标志 */
	public static final String COLL_STATUS_DJ = "DQ01";
	/* 到期解付记账标志 */
	public static final String COLL_STATUS_JZ = "DQ02";
	
	/** 额度释放类型 **/
	public static final String RELEASETYPE_02 = "02";//正常释放
	public static final String RELEASETYPE_03 = "03";//未用释放
	
	/** 柜面代理/网银标识 **/
	public static final String PROXY = "proxy";//柜面代理
	public static final String NETBANK = "netbank";//网银
	
	/** 承兑产品类型ID **/
	public static final String ACCEPTION_PRODUCTID = "12001";// 承兑产品类型ID
	
	/** 纸质承兑产品类型ID **/
	public static final String PCDS_ACCEPTION_PRODUCTID = "12002";// 纸质承兑产品类型ID
	
	/** 保证金类别 **/
	public static final String CASHDEPOSITTYPE_01 = "01";// 保证金
	
	/** 绑定关系变更类型 **/
	public static final String ALTERATIONBINDINGCODE_AB00 = "AB00"; // 新增
	public static final String ALTERATIONBINDINGCODE_AB01 = "AB01"; // 删除
	
	/** 电子合同通用回复标记 **/
	public static final String SIGN_UP_CONTRACT_MARK_CODE_YES = "RM01"; // 同意
	public static final String SIGN_UP_CONTRACT_MARK_CODE_NO = "RM02"; //  拒绝 
	
	/** 票据有效期范围 **/
	public static final String ONETOTHREEMONTH = "01"; //  1-3个月
	public static final String THREETOSIXMONTH = "02"; //  3-6个月
	public static final String MORETHANSIXMONTH = "03"; // 6个月以上
	
	/** 挂失止付 **/
	public static final String SUSPENSIONPAY = "SP00"; // 挂失止付
	/** 公示催告止付 **/
	public static final String PUBLICITYSUSPENSIONPAY = "SP01"; // 公示催告止付
	
/**-----------------------------------------------------纸票----------------------------------------------------------**/
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////以下为纸票公用静态定义文件///////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**------------------------------------------------------纸票-----------------------------------------------------------**/

	/* 明细状态 */
	/* 录入完毕 */
	public static final String PTX001 = "PTX_001";
	/* 已加入转贴现批次 */
	public static final String PTX002 = "PTX_002";
	/* 提交至转贴现批次复核 */
	public static final String PTX002_1 = "PTX_002_1";
	/*提交审批 */
	public static final String PTX003 = "PTX_003";
	/*审批中 */
	public static final String PTX004 = "PTX_004";
	/*审批通过 */
	public static final String PTX005 = "PTX_005";
	/*审批不通过*/
	public static final String PTX006 = "PTX_006";
	/*入库状态*/
	public static final String PTX007 = "PTX_007";
	/*出库状态*/
	public static final String PTX008 = "PTX_008";
	/* 记账通知 */
	public static final String PTX009 = "PTX_009";
	/* 查询查复通过 */
	public static final String PTX010 = "PTX_010";
	/* 查询查复不通过 */
	public static final String PTX011 = "PTX_011";
	/* 记账完毕 */
	public static final String PTX012 = "PTX_012";
	/* 撤销成功 */
	public static final String PTX013 = "PTX_013";
	/* 已发查询查复 */
	public static final String PTX014 = "PTX_014";
	
	
	/* 批次状态 */
	/* 保存批次 */
	public static final String PZT501 = "PZT_501";
	/*提交审批 */
	public static final String PZT502 = "PZT_502";
	/*审批中 */
	public static final String PZT503 = "PZT_503";
	/*审批通过 */
	public static final String PZT504 = "PZT_504";
	/*审批不通过 */
	public static final String PZT505 = "PZT_505";
	/*入库状态 */
	public static final String PZT506 = "PZT_506";
	/*出库状态 */
	public static final String PZT507 = "PZT_507";
	/*记账通知*/
	public static final String PZT508 = "PZT_508";
	/*查询查复通过*/
	public static final String PZT509 = "PZT_509";
	/*查询查复不通过*/
	public static final String PZT510 = "PZT_510";
	/* 记账完毕 */
	public static final String PZT511 = "PZT_511";
	/* 撤销成功 */
	public static final String PZT512 = "PZT_512";
	/* 已发查询查复 */
	public static final String PZT513 = "PZT_513";
	/* 已发解冻通知 */
	public static final String PZT514 = "PZT_514";
	
	
	public static final int YIDISHUNYAN_TIANSHU = 3;


	/** 加入状态 * */
	/* 未加入 */
	public static String ADD_001 = "ADD_001";
	/* 已加入 */
	public static String ADD_002 = "ADD_002";

	/** 是否已存在 * */
	/* 不存在 */
	public static String BILLIFEXIST_0 = "0";
	/* 存在 */
	public static String BILLIFEXIST_1 = "1";

	/*托收*/
	public static final String PTS000 = "PTS_000";//已挑选完毕
	public static final String PTS0000 = "PTS_0000";//代客托收添加完票  20170317 zx
	public static final String PTS0001 = "PTS_0001";//出库or确认完代客托收  20170118 zx
	public static final String PTS001 = "PTS_001";//未发出托收

	public static final String PTS002 = "PTS_002";//已发出托收
	public static final String PTS007 = "PTS_007";//托收退票

	public static final String PTS003 = "PTS_003";//发托记账完毕
	public static final String PTS004 = "PTS_004";//托收收回记账失败(未记账)
	
	public static final String PTS005 = "PTS_005";//大额来帐未确认 
	public static final String PTS006 = "PTS_006";//大额来帐已确认 
	public static final String PTS008 = "PTS_008";//归还入库 yywadd
	/**---------------以下是在Msg0631实体中应用的常量  Begin--------------------**/

	/* 未更新至BankData表*/
	public static final String NOUPDATE = "未更新";
	/* 已更新至BankData表*/
	public static final String ALREADYUPDATE = "已更新";

	/**---------------以下是在Msg0631实体中应用的常量  End--------------------**/


	/**---------------以下是在BankData实体中应用的常量  Begin--------------------**/

	/* 是否参与人行Ecds */
	public static final String JOIN = "是";

	/**---------------以下是在BankData实体中应用的常量  End--------------------**/

	/** 同城*/
	public static final String SAME_CITY="CT_01";
	/** 异地*/
	public static final String DIFFERENT_CITY="CT_02";

	/** PCDS_REDISCOUNTSALE_PROCESS_NAME 纸票转帖卖出流程名称*/
	public static final String PCDS_REDISCOUNTSALE_PROCESS_NAME = "pcdsRediscountSell";
	/** PCDS_DISCOUNT_PROCESS_NAME 纸票贴现买入流程名称*/
	public static final String PCDS_DISCOUNT_PROCESS_NAME = "pcdsDiscountProcess";
	/** PCDS_INNERREDISCOUNT_PROCESS_NAME 纸票分买支流程名称*/
	public static final String PCDS_INNERREDISCOUNT_PROCESS_NAME = "pcdsInnerRediscount";
	/** PCDS_INNERREDISCOUNT_PROCESS_NAME 纸票系统内转帖流程名称*/
	public static final String PCDS_INSIDEREDISCOUNT_PROCESS_NAME = "pcdsInsideRediscount";
	

	/**------------------  以下是在WorkInfo实体中应用的常量  Begin  --------------------**/

	/** 流程状态：活动中 */
	public static final String ACTIVE = "active";

	/** 流程状态：已结束 */
	public static final String OVER = "over";

	/** 流程查阅人：是当前查阅人 */
	public static final String YES = "Y";

	/** 查阅类型：全程查看  */
	public static final String ALL = "all";

	/** 查阅类型：当前处理 */
	public static final String CURRENT = "current";

	/**------------------  以上是在WorkInfo实体中应用的常量  End  --------------------**/

	/**--------------------以下是发票类型常量begin-----------------**/
	/** 增值税发票*/
	public static final String FP_001="FP_001";
	/** 普通发票*/
	public static final String FP_002="FP_002";
	/** 异地发票*/
	public static final String FP_003="FP_003";
	/** 特种发票*/
	public static final String FP_004="FP_004";
	/** 专用发票*/
	public static final String FP_005="FP_005";
	/** 销售发票*/
	public static final String FP_006="FP_006";
	/**--------------------以上是发票类型常量end-------------------**/

	/**--------------------以下是否代理贴现常量begin-------------------**/
	/** 是*/
	public static final String PY_01="PY_01";
	/** 否*/
	public static final String PN_02="PN_02";
	/**--------------------以上是否代理贴现常量end-------------------**/


	/**--------------------以下是付息方式常量begin-------------------**/
	/** 卖方付息*/
	public static final String PW_001="pw_001";
	/** 买方付息*/
	public static final String PW_002="pw_002";
	/** 协议付息*/
	public static final String PW_003="pw_003";
	/**--------------------以上是付息方式常量end-------------------**/

	/**-------------------根据贴出人（申请人的组织机构代码查询核心接口获取客户信息与申请人名称是否一致）判断申请人名称是否一致------*/
	public static final String ORGCODE_YES="01";	//
	public static final String ORGCODE_NO="02";
	public static final String ORGCODE_EX="exception";

	/**-----------------------暂存账号名称以-------**/
	public static final String TEMP_ACCOUNT="261021032";
	public static final String TEMP_ACCOUNT_NAME="待划转商业汇票贴现款项*标准";

	public static final String bjBankCode="313290020018";
	/**------------是否贸易背景检查------------**/
	public static final String CHECK_TRADE_YES="CTY_01";//是
	public static final String CHECK_TRADE_NO="CTN_02";//否
	
	/**-----------基准利率种类---------------**/
	public static final String BENCHMARK_Rate_TYPE = "01";//基准利率
	public static final String DISCOUNT_COST_Rate_TYPE = "02";//贴现成本利率
	
    /**------------基准利率类型----------------**/
	public static final String ONE_W_Rate_TYPE = "1W";//1周
	public static final String TOW_W_Rate_TYPE = "2W";//2周
	public static final String ONE_M_Rate_TYPE = "1M";//1月
	public static final String TOW_M_Rate_TYPE = "2M";//2月
	public static final String THREE_M_Rate_TYPE = "3M";//3月
	public static final String SIX_M_Rate_TYPE = "6M";//6月
	public static final String NINE_M_Rate_TYPE = "9M";//6月
	public static final String ONE_Y_Rate_TYPE = "1年";//6月
	
	/**-----------票据所有权-------------------**/
	public static final String BILL_Proprietorship_OWNER = "01";//本行所有
	public static final String BILL_Proprietorship_OTHER = "02";//他行所有
	
	/**-----------票据买入方式-------------------0_83 **/
	/**
	 * //直贴
	 */
	public static final String BILL_billBuyMode_ZT = "01";
	/**
	 * //买断转贴
	 */
	public static final String BILL_billBuyMode_MDZT = "02";
	/**
	 * //买入返售转贴
	 */
	public static final String BILL_billBuyMode_MRFSZT = "03";
	/**
	 * //双买
	 */
	public static final String BILL_billBuyMode_SMZT = "04";
	/**
	 * //系统内买断
	 */
	public static final String BILL_billBuyMode_INNERBUY = "05";
	/**
	 * 同业卖断
	 */
	public static final String BILL_billBuyMode_SALE = "06";
	/**
	 * 同业卖出回购
	 */
	public static final String BILL_billBuyMode_SALE_BACK = "07";
	
	/**
	 * 质押
	 */
	public static final String BILL_billBuyMode_ZY = "08";
	
	/**---------------- 承兑协议   ---------------------------**/
	public static final String XY001 = "XY_001"; // 新增承兑协议
	public static final String XY002 = "XY_002"; // 额度已扣减
	public static final String XY003 = "XY_003"; // 已打印
	public static final String XY004 = "XY_004"; // 审批同意
	public static final String XY005 = "XY_005"; // 审批不同意
	public static final String XY006 = "XY_006"; // 同意放款
	
	
	
	
	
	/**---------------- 票据流通性   ---------------------------**/
	public static final String NOBLEMISH = "可流通"; // 可流通
	
	
	/**----------------首页汇总信息  ---------------------------**/
	public static final String GATHER_FLAG0 = "0"; //发生额/余额标志(0:统计标志  1:不需要统计)
	public static final String GATHER_FLAG1 = "1"; //发生额/余额标志(0:统计标志  1:不需要统计)
	
	public static final String FS_FLAG = "fs"; //发生额
	public static final String YE_FLAG = "ye"; //余额
	
	public static final String DAY_FLAG = "d"; //日 
	public static final String WEEK_FLAG = "w"; //周
	public static final String MONTH_FLAG = "m"; //月
	public static final String QUARTER_FLAG = "q"; //季度
	public static final String YEAR_FLAG = "y"; //年
	
	public static final String ACCEPTOR_FLAG = "acceptor"; //承兑
	public static final String DISCOUNT_FLAG = "discount"; //贴现
	public static final String DISCOUNT_ALERT_FLAG = "discount_alert"; //先贴后查逾期提醒
	public static final String REDISCOUNTBUY_FLAG = "rediscountbuy"; //转贴现买入
	public static final String REDISCOUNTSELL_FLAG = "rediscountsell"; //转贴现卖出
	public static final String ZAITIEXIANBUY_FLAG = "zaitiexianbuy"; //再转贴现卖入
	public static final String ZAITIEXIANSELL_FLAG = "zaitiexiansell"; //再转贴现卖出
	public static final String SHUANGSELL_FLAF = "resell"; //双卖
	public static final String SHUANGBUY_FLAG = "rebuy";   //双买
	
	public static final String TUOSHOU_FLAG = "tuoshou"; //托收
	public static final String JIEFU_FLAG = "jiefu"; //解付
	
	public static final String SELL_TYPE = "sell"; //卖断
	public static final String BUYIN_TYPE = "buy"; //买断
	public static final String FANSHOU_TYPE = "fanshou"; //买入贩售
	public static final String HUIGOU_TYPE = "huigou"; //卖出回购
	
	public static final String INNER_TYPE = "inner"; //系统内
	public static final String OUTER_TYPE = "outer"; //系统外
	
	
	public static final int ONE_DAY = 1; //1天
	public static final int SENVEN_DAY = 7; //7天
	public static final int THIRTY_DAY = 30; //30天
	
	/*---------------    同业存入        ----------------------*/
	
	/* 同业存入审批流程 */
	public static String PROCESS_BANKBORROW_AUDITING = "bankBorrow_process";
	
	public static final String CR001 = "CR_001"; // 新建存入
	public static final String CR002 = "CR_002"; // 提交审批
	public static final String CR003 = "CR_003"; // 审批同意
	public static final String CR004 = "CR_004"; // 审批不同意
	public static final String CR005 = "CR_005"; // 已记账
	public static final String CR006 = "CR_006"; // 取款并记账
	
	public static final String FUNFTYPE_01 = "01"; // 存入类型  01 活期
	public static final String FUNFTYPE_02 = "02"; // 存入类型  02 定期
	
	public static final String FUNDTERM_01 = "01"; // 存款期限  01 一年
	public static final String FUNDTERM_03 = "03"; // 存款期限  03 三个月
	public static final String FUNDTERM_06 = "06"; // 存款期限  06 六个月
	
	/*---------------    同业存入        ----------------------*/
	
	
	/*---------------    同业存出        ----------------------*/
	
	/* 同业存出审批流程 */
	public static String PROCESS_BANKLENT_AUDITING = "bankLent_process";
	
	public static final String CC001 = "CC_001"; // 新建存出
	public static final String CC002 = "CC_002"; // 提交审批
	public static final String CC003 = "CC_003"; // 审批同意
	public static final String CC004 = "CC_004"; // 审批不同意
	public static final String CC005 = "CC_005"; // 已记账
	public static final String CC006 = "CC_006"; // 取款并记账
	
	/*---------------    同业存出        ----------------------*/
	
	
	public static final String SELF = "self"; // 本行
	public static final String OTHER = "other"; // 他行
	
    /*---------------    票据状态  wxb  ecds1.nc_zhiyadj   ----------------------*/
	
	public static final String dzy = "1"; // 待质押
	public static final String yzy = "2"; // 已质押
	public static final String dzycx = "3"; // 待质押已查询
	public static final String dzycf = "4"; // 待质押已查复
	public static final String dzycxeorr = "5"; // 待质押查询异常
	public static final String jzy = "6"; // 解质押
	public static final String enterbig = "7"; // 进入大额查询
	
	/*---------------    票据状态  wxb  ecds1.nc_zhiyadj   ----------------------*/
	
	/*---------------     多岗复核路线       ----------------------*/
	
	public static final String ACCOUNT = "account"; // 记账
	
	public static final String NOTFINISH = "未完结"; // 未完结
	
	public static final String FINISH = "已完结"; // 已完结
	
	
	
	/*---------------     多岗复核路线       ----------------------*/
	
    /*---------------    出质人类别  wxb  ecds1.nc_zhiyadj   ----------------------*/
	
	public static final String RC00 = "RC00"; // 接入行
	public static final String RC01 = "RC01"; // 企业
	public static final String RC02 = "RC02"; // 人民银行
	public static final String RC03 = "RC03"; // 被代理行
	public static final String RC04 = "RC04"; // 被代理财务公司
	public static final String RC05 = "RC05"; // 接入财务公司
	
	/*---------------    出质人类别  wxb  ecds1.nc_zhiyadj   ----------------------*/
	
	/*---------------    计提摊销 参数设置  wxb  ecds1.t_ParaConfig   ----------------------*/
	
	public static final String tanXiaoType_DAY = "d"; //摊销类型 （天：d;旬：x;月：m；季:q）
	public static final String tanXiaoType_XUN = "x"; 
	public static final String tanXiaoType_MONTH = "m"; 
	public static final String tanXiaoType_QUARTER = "q"; 
	
	public static final String headOrTail_HEAD = "h";//算头还是算尾 (头：h;尾:t)
	public static final String headOrTail_TAIL = "t";
	
	public static final String calInterestDate_Repurchase = "0";//电票回购式利息日(票据到期日还是回购截止日)(回购截止日：0；票据到期日：1)
	public static final String calInterestDate_Maturity = "1";
	
	public static final String saleTanxiaoPrin_Achieve = "0";//是否摊销到最后一天(是：0；否：1);
	public static final String saleTanxiaoPrin_happen = "1";
	
	public static final String RECEIVE_INTEREST = "s"; //标志（应付标志：f；应收标志：s）
	public static final String PAY_INTEREST = "f"; //标志（应付标志：f；应收标志：s）
	public static final String CONTINUE_TANXIAO = "C"; //是否继续摊销(是：C，否：S)
	public static final String STOP_TANXIAO = "S"; //是否继续摊销(是：C，否：S)
	
	/*-------    新增加交易状态  (所有交易通用)-------*/
	public static final String CHONGZHENG_SUCC = "CZ_001";  //冲正成功
	
	public static final String IS_LIMIT_TRUE = "1";    //权限不足
	public static final String IS_LIMIT_FALSE = "2";   //权限充足
	public static final String IS_NOT_QUERY = "3";     //未知权限
	public static final String IS_NOT_AUDITNODE = "4"; //没有审批节点
	/* ------------------------------- 客户经理绩效考核相关  ------------------------------- */
	public static final String CUSTVOLUME_TYPE_CUST = "Cust";//客户经理考核
	public static final String CUSTVOLUME_TYPE_CUDTGROUP = "CustGroup";//客户经理组考核
	public static final String CUSTVOLUME_TYPE_BANK = "Bank";//分支行考核
	/* ------------------------------- 客户经理绩效考核相关  ------------------------------- */
	/**
	 * 从userlist 列表删除 curuser 
	 * @param userlist
	 * @param curuser
	 * @return 删除后的列表信息
	 */
	public static List deleteExistUser(List userlist,User curuser)
	{
	  List list = userlist;
	  List removeList = new ArrayList();
	  try{
		  if(null != list && list.size()>0 && null != curuser)
		  {
			 Iterator users = list.iterator();
			 while(users.hasNext())
			 {
				 User user = (User)users.next();
				 if(user.getId().equals(curuser.getId()))
				 {
					 removeList.add(user);
				 }
			 }
			 list.removeAll(removeList);
		  }
	  }catch(Exception e)
	  {
	  }
     return list;
	}
	/**
	 * 判断是否是总行用户
	 * 
	 * @param user
	 * @return
	 */
	public static boolean isRootDepartment(User user) {
		boolean is = false;
		/*if (null != user && existParentDepartment(user) == false && user.getDepartment().getLevel() == 1) {
			is = true;
		}*/
		// 1.当前用户不空，当前机构不空；当前机构生效（启用）
		Department curDept = user.getDepartment();
		if (null != user && null != curDept && 1 == curDept.getStatus()) {
			// 2.当前机构为总行（上级机构为空）
			if(null == curDept.getParent()) {
				is = true;
			} else {
				//3.当前机构级别为总行且上级机构为总行 
				//if (curDept.getLevel() == 1 && curDept.getParent().getParent() == null) {
				if (curDept.getLevel() == 1 && curDept.getParent().getId().equals("0098")) {
					is = true;
				}
			}
		}
		return is;
	}
	/**
	 * 是否存在上级部门
	 * 
	 * @param user
	 * @return
	 */
	public static boolean existParentDepartment(User user) {
		boolean is = false;
		if (null != user && null != user.getDepartment().getParent() && 1 == user.getDepartment().getStatus()) {
			is = true;
		}
		return is;
	}
	
	/** 账户类型 
	 * 票据池保证金账户
	 * */
	public static final String ACCT_BZJC="1";  //票据池保证金账户
	public static final String ACCT_JS="2";    //结算账户
	public static final String ACCT_PT="3";     //无
	public static final String ACCT_BZJ_DQ = "4"; // 票据池保证金定期账户
	
	/* 额度占用对象种类（同业、对公） */
	/* 同业 */
	public static final String LIMIT_OBJECT_TYPE_BANK = "01";
	/* 对公 */
	public static final String LIMIT_OBJECT_TYPE_COMP = "02";
	
	/* 扣减信贷系统同业额度开关*/
	public static final boolean CREDIT_EDU_FLAG = false ;
	/* ------------------------------- 客户经理绩效考核相关  ------------------------------- */
	/**南充支持先贴后查**/
	public static final String FIR_DISCOUNT_LQUERY = "Y";   // 先贴后查
	public static final String FIR_QUERY_LDISCOUNT = "N";   // 先查后贴
	
	/**贴现 查询查复报文状态**/
	public static final String CXBW000 = "CXBW_000"; //未发查询查复报文
	public static final String CXBW001 = "CXBW_001"; //已发查询查复报文
	public static final String CXBW002 = "CXBW_002"; //查询查复通过
	public static final String CXBW003 = "CXBW_003"; //查询查复未通过
	
	/*---------------------------大额查询查复-----------------Start---------------------------------*/
	/**本行角色**/
	public static final String BANKROLE_QUERY = "query";   //查询行
	public static final String BANKROLE_REPLY = "reply";   //查复行
	
	/**查询发送方式**/
	public static final String SENDQUERYTYPE_SHOUGONG = "sg";      //手工查询
	public static final String SENDQUERYTYPE_DISCOUNTCHECK = "dc";    //贴现核对查询
	public static final String SENDQUERYTYPE_DISCOUNTNOTICE = "dn";   //贴入通知查询

	/**发送查询状态**/
	public static final String SENDQUERYSTATUS_001 = "SQS_001";   //待发送
	public static final String SENDQUERYSTATUS_002 = "SQS_002";   //已发送
	public static final String SENDQUERYSTATUS_003 = "SQS_003";   //查复待处理
	public static final String SENDQUERYSTATUS_004 = "SQS_004";   //已完成
		/**接收查询状态**/
	public static final String RECEIVEMSGSTATUS_001 = "RMS_001";   //待处理
	public static final String RECEIVEMSGSTATUS_002 = "RMS_002";   //待查复
	public static final String RECEIVEMSGSTATUS_003 = "RMS_003";   //已查复
	public static final String RECEIVEMSGSTATUS_004 = "BOHUI";   //已驳回
	
	/**查询对应查复方式**/
	public static final String REPLAYTYPEFORQUERY_001 = "RTFQ_001";   //确认查复
	public static final String REPLAYTYPEFORQUERY_002 = "RTFQ_002";   //不确认查复
	public static final String REPLAYTYPEFORQUERY_003 = "RTFQ_003";   //无查复
	
	/**接收报文类型**/
	public static final String ACCEPTMSGTYPE_001 = "AMT_001";   //票据相关
	public static final String ACCEPTMSGTYPE_002 = "AMT_002";   //非票据相关
	
	/*---------------------------大额查询查复-----------------Start---------------------------------*/
	/**
	 * 20151115 去掉工作流 增加 审批路线  的业务类型 配置
	 * 业务类型  1	贴现买入	2转贴现买入3转贴现卖出4再贴现卖出5再贴现买入			
	6回购式贴现到期卖出        7纸票转贴现买入8纸票转贴现卖出9纸票贴现
	10提示付款11追索12提示承兑
	20000代保管20001票据池20002库存票据
	以产品编码的  大类作为分类标准  
	 * 【参考ApproveAuditDto 类】
	 */
	/**
	 * 1 贴现买入
	 */
	public static final String AUDIT_BUSI_TYPE_1 = "1";
	/**
	 * 2转贴现买入
	 */
	public static final String AUDIT_BUSI_TYPE_2 = "2";
	/**
	 * 3转贴现卖出
	 */
	public static final String AUDIT_BUSI_TYPE_3 = "3";
	/**
	 * 4再贴现卖出
	 */
	public static final String AUDIT_BUSI_TYPE_4 = "4";
	/**
	 * 5再贴现买入	
	 */
	public static final String AUDIT_BUSI_TYPE_5 = "5";
	/**
	 * 6回购式贴现到期卖出 
	 */
	public static final String AUDIT_BUSI_TYPE_6 = "6";
	/**
	 *   7纸票转贴现买入
	 */
	public static final String AUDIT_BUSI_TYPE_7 = "7";
	/**
	 * 8纸票转贴现卖出
	 */
	public static final String AUDIT_BUSI_TYPE_8 = "8";
	/**
	 * 9纸票贴现
	 */
	public static final String AUDIT_BUSI_TYPE_9 = "9";
	/**
	 * 10提示付款
	 */
	public static final String AUDIT_BUSI_TYPE_10 = "10";
	/**
	 * 11追索
	 */
	public static final String AUDIT_BUSI_TYPE_11 = "11";
	/**
	 * 12提示承兑
	 */
	public static final String AUDIT_BUSI_TYPE_12 = "12";
	
	/**
	 * 审批流程类型   01 批次审批
	 */
	public static final String AUDIT_TYPE_APPLY="01";
	/**
	 * 审批流程类型 02 明细清单审批
	 */
	public static final String AUDIT_TYPE_BILLS="02";
	
	/**
	 * 审批流程 状态  00开启
	 */
	public static final String AUDIT_STATUS_OPEN="00";
	/**
	 * 审批流程状态 01关闭
	 */
	public static final String AUDIT_STATUS_CLOSE="01";
	
	/**
	 * 业务结清标记 01 同业卖断
	 */
	public static final String BUSI_END_SALE="01";
	/**
	 * 业务结清标记 02 系统内卖断
	 */
	public static final String BUSI_END_SALE_INNER="02";
	/**
	 * 业务结清标记 03 同业卖出回购
	 */
	public static final String BUSI_END_SALE_HG="03";
	/**
	 * 业务结清标记 04 同业卖出回购到期
	 */
	public static final String BUSI_END_SALE_HG_DQ="04";
	
	/**
	 * 业务结清标记 05 托收在途
	 */
	public static final String BUSI_END_SUB="05";
	/**
	 * 业务结清标记 06 托收回款结清
	 */
	public static final String BUSI_END_SUB_END="06";
	/**
	 * 业务结清标记   07 买入返售到期 
	 */
	public static final String BUSI_END_BUY_HG="07";
	
	/**
	 * 业务 已结清 01
	 */
	public static final String BUSI_END_YES="01";
	/** 
	 * 业务未 结清 00
	 */
	public static final String BUSI_END_NO="00";
	
	/**
	 * 余额标记：1贴现余额 2同业间回购式卖出余额
	 * 3卖出回购式再贴现余额 4卖断销账
	 * 5返售到期销账 6托收在途余额 7托收销账 
	 */
	public static final String BALANCE_FLAG_1="1";
	/**
	 * 2同业间回购式卖出余额
	 */
	public static final String BALANCE_FLAG_2="2";
	/**
	 * 3卖出回购式再贴现余额
	 */
	public static final String BALANCE_FLAG_3="3";
	/**
	 * 4卖断销账
	 */
	public static final String BALANCE_FLAG_4="4";
	/**
	 * 5返售到期销账
	 */
	public static final String BALANCE_FLAG_5="5";
	/**
	 *  6托收在途余额
	 */
	public static final String BALANCE_FLAG_6="6";
	/**
	 * 7托收销账
	 */
	public static final String BALANCE_FLAG_7="7";
	/**
	 * 承兑手续费
	 */
	public static final String CHARGE_SXF = "01";//承兑手续费
	/**
	 * 纸票 工本费
	 */
	public static final String CHARGE_GBF = "02";//纸票工本费
	
	/**
	 * 代理接入行业务标记   1代理行业务  0本行业务
	 */
	public static final String AGENT_BANK_FLAG_0="0";
	/**
	 * 代理接入行业务标记   1代理行业务  0本行业务
	 */
	public static final String AGENT_BANK_FLAG_1="1";
	
	/**
	 * 凭证管理    票号状态   初始状态
	 */
	public static final String VOCH_00 ="00";
	/**
	 * 凭证管理    票号状态   已使用
	 */
	public static final String VOCH_01 ="01";
	/**
	 * 凭证管理    票号状态   已作废
	 */
	public static final String VOCH_02 ="02";
	
	/**
	 * 挂失止付标记   挂失止付登记
	 */
	public static final String STOP_PAYMENT_01 ="01";
	/**
	 * 挂失止付标记   解除挂失止付
	 */
	public static final String STOP_PAYMENT_02 ="02";
	
	/**
	 * 网银处理常量
	 */
	public static final String WY_SUCCESS_CODE = "1";
	public static final String WY_ERRMSG = "处理此业务出现异常!";
	
	public static final String MOD00 ="MOD_00";//维持
	public static final String MOD01 ="MOD_01";//新增
	public static final String MOD02 ="MOD_02";//修改
	public static final String MOD03 ="MOD_03";//删除
	
	/**
	 * 记账流水类型   记账状态
	 */
	public static final String ACCT_TYPE_00 = "00";
	
	/**
	 * 记账流水类型   撤销记账
	 */
	public static final String ACCT_TYPE_01 = "01";
	
	/**
	 * 记账流水状态   初始状态
	 */
	public static final String ACCT_STATUS_00 = "00";
	/**
	 * 记账流水状态   成功状态
	 */
	public static final String ACCT_STATUS_01 = "01";
	/**
	 * 记账流水状态   系统记账异常状态
	 */
	public static final String ACCT_STATUS_02 = "02";
	/**
	 * 记账流水状态   核心记账异常状态
	 */
	public static final String ACCT_STATUS_03 = "03";
	
	/**
	 * 托收票据来源   初始状态
	 */
	public static final String BILL_HIST_STATION_00 = "00";
	/**
	 * 托收票据来源   大票表   
	 * 01票据系统自己；02手工输入(代客托收) 03 票据池
	 */
	public static final String BILL_HIST_STATION_01 = "01";
	/**
	 * 托收票据来源   手动输入
	 * 01票据系统自己；02手工输入(代客托收) 03 票据池
	 */
	public static final String BILL_HIST_STATION_02 = "02";
	/**
	 * 托收票据来源   票据池
	 * 01票据系统自己；02手工输入(代客托收) 03 票据池
	 */
	public static final String BILL_HIST_STATION_03 = "03";
	
	
	 /*---------------------------三菱电票二期--------start------------------------*/
	 
	 //收到清分失败的业务类型
	 public static final String REC_002 = "rec002";//承兑
	 public static final String REC_003 = "rec003";//收票
	 public static final String REC_010 = "rec010";//背书
	 public static final String REC_011 = "rec011";//贴现
	 public static final String REC_017 = "rec017";//保证
	 public static final String REC_018 = "rec018";//质押
	 
	//发送清分失败的业务类型
	 public static final String SEND_002 = "send002";//承兑
	 public static final String SEND_003 = "send003";//收票
	 public static final String SEND_010 = "send010";//背书
	 public static final String SEND_017 = "send017";//保证
	 
	//发送业务后被拒绝签收的业务类型
	 public static final String REFUSE_002 = "ref002";//承兑
	 public static final String REFUSE_003 = "ref003";//收票
	 public static final String REFUSE_010 = "ref010";//背书
	 public static final String REFUSE_011 = "ref011";//贴现
	 public static final String REFUSE_017 = "ref017";//保证
	 public static final String REFUSE_018 = "ref018";//质押
	 public static final String REFUSE_020 = "ref020";//提示付款
	 public static final String REFUSE_022 = "ref022";//追索
	 public static final String REFUSE_023 = "ref023";//同意清偿
	 
	 
	 //企业客户类型
	 public static final String Customer_Account_01 = "Account_01";//18位  同城交换标准账号
	 public static final String Customer_Account_02 = "Account_02";//17位    同城交换账号少 - 
	 public static final String Customer_Account_03 = "Account_03";//6位      6位标准账号
	 public static final String Customer_Account_04 = "Account_04";//11位    00000 + 6位标准账号
	 public static final String Customer_Account_05 = "Account_05";//15位      G/L code 标准账号 
	 public static final String Customer_Account_06 = "Account_06";//13位      标准  G/L code 少两个 - 
	 
	 
	 /*---------------------------三菱电票二期----------end-----------------------*/
	 
	 /*---------------------------票交所----------start-----------------------*/
	 public static final String PJSCANCLE_01 = "PJSCAN_01";//未用退回
	 public static final String PJSCANCLE_02 = "PJSCAN_02";//票据作废
	 public static final String PJSCANCLE_03 = "PJSCAN_03";//信息作废
	 
	 
	 
	 /*---------------------------票交所----------end-----------------------*/
	 
	 
	 /*---------------------------票交所承兑----------start-----------------------*/
	 public static final String PJSHONOUR_00 = "HONOUR_00";//承兑影像待录入
	 public static final String PJSHONOUR_01 = "HONOUR_01";//承兑信息填写完整
	 public static final String PJSHONOUR_02 = "HONOUR_02";//承兑信息待复核
	 public static final String PJSHONOUR_03 = "HONOUR_03";//承兑已登记
	 public static final String PJSHONOUR_04 = "HONOUR_04";//承兑复核驳回待修改
	 
	 public static final String PJSHONOURCANCLE_00 = "HONCAN_00";//承兑登记撤回
	 
	 
	 
	 
	 
	 /*---------------------------票交所承兑----------end-----------------------*/
	 /*---------------------------票交所  承兑保证登记----------start-----------------------*/
	 public static final String PJSACKNOWLEDGE_00 = "ACK_00";//补充影像待录入
	 public static final String PJSACKNOWLEDGE_01 = "ACK_01";//信息完整
	 public static final String PJSACKNOWLEDGE_02 = "ACK_02";//驳回待修改
	 public static final String PJSACKNOWLEDGE_03 = "ACK_03";//信息待复核
	 public static final String PJSACKNOWLEDGE_04 = "ACK_04";//已提交
	 
	 public static final String PJSACKNOWLEDGECANClE_00 = "ACK_CAN_00";//正在撤回
	 
	 /*---------------------------票交所  承兑保证登记----------end-----------------------*/
	 /*---------------------------票交所  结清信息登记 ---------start-----------------------*/
	 public static final String PJSBALANCE_00 = "BAL_00";// 结清信息登记
	 public static final String PJSBALANCECANCLE_00 = "CAN_00";//正在撤回
	 
	 /*---------------------------票交所  结清信息登记 ---------end-----------------------*/
	 /*---------------------------票交所  贴现信息登记 ---------start-----------------------*/
	 public static final String PJSDISCOUNT_00 = "DIS_00";//补充影像待录入
	 public static final String PJSDISCOUNT_01 = "DIS_01";//信息完整
	 public static final String PJSDISCOUNT_02 = "DIS_02";//驳回待修改
	 public static final String PJSDISCOUNT_03 = "DIS_03";//信息待复核
	 public static final String PJSDISCOUNT_04 = "DIS_04";//已提交
	 
	 public static final String PJSDISCOUNTCANClE_00 = "DIS_CAN_00";//正在撤回
	 /*---------------------------票交所  贴现信息登记 ---------end-----------------------*/
	 /*---------------------------票交所  止付信息登记 ---------start-----------------------*/
	 public static final String PJSPAYMENT_00 = "PAY_00";// 信息录入完成
	 public static final String PJSPAYMENT_01 = "PAY_01";// 已提交
	 public static final String PJSPAYMENTCANCLE_00 = "CAN_00";//正在撤回
	 //止付状态
	 public static final String PJSPAYCATE_01 = "PJSCATE_01";//挂失止付
	 public static final String PJSPAYCATE_02 = "PJSCATE_02";//公示催告
	 public static final String PJSPAYCATE_03 = "PJSCATE_03";//司法冻结
	 public static final String PJSPAYCATE_04 = "PJSCATE_04";//真伪存疑
	 
	 //解除止付状态
	 public static final String PJSPAYCATECAN_01 = "PJSCATECAN_01";//挂失止付到期
	 public static final String PJSPAYCATECAN_02 = "PJSCATECAN_02";//除权判决
	 public static final String PJSPAYCATECAN_03 = "PJSCATECAN_03";//解除公示催告
	 public static final String PJSPAYCATECAN_04 = "PJSCATECAN_04";//解除司法冻结
	 public static final String PJSPAYCATECAN_05 = "PJSCATECAN_04";//提交仲裁解冻
	 
	 /*---------------------------票交所  止付信息登记 ---------end-----------------------*/
	 
	 //缓存数据类型
	 public static final String CACHE_DATA_TYPE_SYSCONFIG = "01";//系统参数配置
	 public static final String CACHE_DATA_TYPE_RUNSTATE = "02";//系统运行状态
	 public static final String CACHE_DATA_TYPE_DIC = "03";//数据字典
	 public static final String CACHE_DATA_TYPE_PRODUCT = "04";//产品类型
	 public static final String CACHE_DATA_TYPE_HOLIDAY = "05";//节假日
	 public static final String CACHE_DATA_TYPE_PJSCOMMDATE = "06";//票交所基础数据
	 public static final String CACHE_DATA_TYPE_DISCOUNTDIMBASICCOF= "07";//在线贴现基础参数
	 public static final String CACHE_DATA_TYPE_BUSICONTROL= "08";//业务控制参数
	 public static final String CACHE_DATA_TYPE_MSG_EXECSEQUENCE= "09";//乱序报文配置
	 public static final String CACHE_DATA_TYPE_MICSERVICE_ROUTE= "10";//微服务路由配置
	 public static final String CACHE_DATA_TYPE_MICSERVICE_CONFIG= "11";//微服务配置
	 public static final String CACHE_DATA_TYPE_ASSET_TYPE_MANAGE= "12";//资产类型缓存

	 //自动任务执行流程状态0未启动、1处理完成 2排队中、3处理中、4处理失败
	 public static final String AUTO_TASK_EXE_STATUS_INIT = "0";//未启动
	 public static final String AUTO_TASK_EXE_STATUS_SUCC = "1";//处理完成
	 public static final String AUTO_TASK_EXE_STATUS_QUEUE = "2";//排队中
	 public static final String AUTO_TASK_EXE_STATUS_RUNNING = "3";//处理中
	 public static final String AUTO_TASK_EXE_STATUS_ERR = "4";//处理失败
	 
	 //任务调度配置状态
	 public static final String TASK_DISPATCH_STATUS_START = "1";//启用
	 
	 //任务调度类型
	 public static final String TASK_DISPATCH_TYPE_AUTO = "AUTO";//自动任务
	 
	 //逻辑删除状态
	 public static final String DELETE_FLAG_NO = "N";//未删除
	 public static final String DELETE_FLAG_YES = "D";//已删除
	 
	 //岗位
	 public static final String OPERATION_TYPE_01 = "CZLX_01";//经办
	 public static final String OPERATION_TYPE_02 = "CZLX_02";//复核
	 //到期日类型
	 public static final String LIMIT_TYPE_0 = "QXFS_0";//到期日
	 public static final String LIMIT_TYPE_1 = "QXFS_1";//月
	 //风险探测 
	 public static final String CREDIT_OPERATION_CHECK = "Check";//检查
	 public static final String CREDIT_OPERATION_EFFECTIVE = "Effective";//生效
	 public static final String CREDIT_OPERATION_CANCEL = "Cancel";//撤销（回滚）
	 
	 //协议类型
	 public static final String PRODUCT_001 = "001";//在线银承
	 public static final String PRODUCT_002 = "002";//在线流贷
	 
	 public static final String PRODUCT_YC = "0";//在线银承	
	 public static final String PRODUCT_LD = "1";//在线流贷	
	 
	 //额度使用类型
	 public static final String LIMIT_USED = "01";//占用
	 public static final String LIMIT_RELEASE = "02";//释放
	 
	 //身份角色
	 public static final String ROLE_0 = "0";//员工
	 public static final String ROLE_1 = "1";//客户	
	 //状态
	 public static final String STATUS_0 = "0";//失效
	 public static final String STATUS_1 = "1";//生效
	 //支付方式
	 public static final String PAY_1 = "1";//自主支付
	 public static final String PAY_2 = "2";//受托支付
	 //校验结果
	 public static final String CHECK_0 = "0";//禁止
	 public static final String CHECK_1 = "1";//通过
	 public static final String CHECK_2 = "2";//提示
	 
	 //bbsp业务类型
	 public static final String BBSP_BUSI_TYPE_01 = "2001";//出票登记
	 public static final String BBSP_BUSI_TYPE_02 = "2002";//提示承兑
	 public static final String BBSP_BUSI_TYPE_03 = "2003";//提示收票
	 public static final String BBSP_BUSI_TYPE_04 = "200202";//提示承兑撤销
	 public static final String BBSP_BUSI_TYPE_05 = "2004";//撤票（未用退回）
	 
	 //渠道码
	 public static final String CHANNEL_NO_BBSP = "bbsp";//电票
	 public static final String CHANNEL_NO_MIS = "mis";//信贷
	 public static final String CHANNEL_NO_EBK = "ebk";//网银
	 public static final String CHANNEL_NO_CORE = "CORE";//核心
	 public static final String CHANNEL_NO_LPR = "LPR";//核心
	 public static final String CHANNEL_NO_ZHB = "zhb";//智汇宝
	 public static final String CHANNEL_NO_BPS = "bps";//票据池
	 
	 //业务码
	 public static final String ACPT_BUSI_NAME_01 = "01";//在线银承申请
	 public static final String ACPT_BUSI_NAME_02 = "02";//风险探测及额度占用
	 public static final String ACPT_BUSI_NAME_03 = "03";//风险探测及额度校验
	 public static final String ACPT_BUSI_NAME_04 = "04";//电票批量新增
	 public static final String ACPT_BUSI_NAME_05 = "05";//电票删除
	 public static final String ACPT_BUSI_NAME_06 = "06";//出票登记
	 public static final String ACPT_BUSI_NAME_07 = "07";//提示承兑申请
	 public static final String ACPT_BUSI_NAME_08 = "08";//提示承兑撤销
	 public static final String ACPT_BUSI_NAME_09 = "09";//未用退回
	 public static final String ACPT_BUSI_NAME_10 = "10";//提示承兑签收
	 
	 //在线银承批次
	 public static final String ACPT_BATCH_001 = "001";//新增
	 public static final String ACPT_BATCH_002 = "002";//票据池额度占用成功
	 public static final String ACPT_BATCH_002_1 = "002_1";//票据池额度占用失败
	 public static final String ACPT_BATCH_002_2 = "002_2";//票据池额度释放
	 public static final String ACPT_BATCH_002_3 = "002_3";//在线协议额度释放
	 public static final String ACPT_BATCH_003 = "003";//信贷额度占用成功
	 public static final String ACPT_BATCH_003_1 = "003_1";//信贷额度占用失败
	 public static final String ACPT_BATCH_003_2 = "003_2";//信贷额度释放
	 public static final String ACPT_BATCH_004 = "004";//批量新增成功
	 public static final String ACPT_BATCH_004_1 = "004_1";//批量新增失败
	 public static final String ACPT_BATCH_005 = "005";//成功
	 public static final String ACPT_BATCH_005_1 = "005_1";//部分成功
	 public static final String ACPT_BATCH_006 = "006";//失败
	 public static final String ACPT_BATCH_006_1 = "006_1";//部分失败
	 public static final String ACPT_BATCH_007 = "007";//作废（所有额度释放）
	 
	 
	 //在线银承明细
	 public static final String ACPT_DETAIL_001 = "001";//新增
	 public static final String ACPT_DETAIL_002 = "002";//票据池额度占用
	 public static final String ACPT_DETAIL_002_1 = "002_1";//票据池额度占用失败
	 public static final String ACPT_DETAIL_002_2 = "002_2";//票据池额度释放
	 public static final String ACPT_DETAIL_002_3 = "002_3";//在线协议额度释放
	 public static final String ACPT_DETAIL_003 = "003";//信贷额度占用
	 public static final String ACPT_DETAIL_003_1 = "003_1";//信贷额度占用失败
	 public static final String ACPT_DETAIL_004 = "004";//推送(推送bbsp)
	 public static final String ACPT_DETAIL_004_1 = "004_1";//推送失败(推送bbsp)
	 public static final String ACPT_DETAIL_005 = "005";//出票登记申请
	 public static final String ACPT_DETAIL_005_1 = "005_1";//出票登记成功
	 public static final String ACPT_DETAIL_005_2 = "005_2";//出票登记失败
	 public static final String ACPT_DETAIL_006 = "006";//承兑申请
	 public static final String ACPT_DETAIL_006_1 = "006_1";//承兑申请成功
	 public static final String ACPT_DETAIL_006_2 = "006_2";//承兑申请失败
	 public static final String ACPT_DETAIL_007 = "007";//承兑签收申请
	 public static final String ACPT_DETAIL_007_1 = "007_1";//承兑签收成功
	 public static final String ACPT_DETAIL_007_2 = "007_2";//承兑签收失败
	 public static final String ACPT_DETAIL_008 = "008";//提示收票申请
	 public static final String ACPT_DETAIL_008_1 = "008_1";//提示收票成功
	 public static final String ACPT_DETAIL_008_2 = "008_2";//提示收票失败
	 public static final String ACPT_DETAIL_009 = "009";//撤票申请
	 public static final String ACPT_DETAIL_009_1 = "009_1";//撤票成功
	 public static final String ACPT_DETAIL_009_2 = "009_2";//撤票失败
	 public static final String ACPT_DETAIL_010 = "010";//未用退回申请
	 public static final String ACPT_DETAIL_010_1 = "010_1";//未用退回成功
	 public static final String ACPT_DETAIL_010_2 = "010_2";//未用退回失败
	 public static final String ACPT_DETAIL_011 = "011";//登记失败已删除
	 public static final String ACPT_DETAIL_012 = "012";//作废（所有额度释放）
	 
	 //在线业务处理状态
	 public static final String ONLINE_DS_001 = "DS001";//处理中
	 public static final String ONLINE_DS_002 = "DS002";//失败：未释放额度
	 public static final String ONLINE_DS_003 = "DS003";//成功
	 public static final String ONLINE_DS_004 = "DS004";//已撤销
	 public static final String ONLINE_DS_005 = "DS005";//失败
	 public static final String ONLINE_DS_006 = "DS006";//部分成功
	 public static final String ONLINE_DS_007 = "DS007";//部分失败
	 
	 //资产入库、出库类型
	 public static final String STOCK_OUT_TYPE_DRAW = "01";//支取
	 public static final String STOCK_OUT_TYPE_DUE = "02";//到期
	 public static final String STOCK_OUT_TYPE_DEPOSIT = "03";//存入
	 public static final String STOCK_OUT_TYPE_OUTPOOL = "04";//出库
	 public static final String STOCK_OUT_TYPE_RISK = "05";//风险变更--MIS高风险名单变更
	 
	 //在线流贷批次状态
	 public static final String CRDT_BATCH_001 = "001";//新增
	 public static final String CRDT_BATCH_002 = "002";//票据池额度占用成功
	 public static final String CRDT_BATCH_002_1 = "002_1";//票据池额度占用失败
	 public static final String CRDT_BATCH_003 = "003";//信贷额度占用成功
	 public static final String CRDT_BATCH_003_1 = "003_1";//信贷额度占用失败
	 public static final String CRDT_BATCH_004 = "004";//放款成功
	 public static final String CRDT_BATCH_004_1 = "004_1";//放款失败
	 public static final String CRDT_BATCH_005 = "005";//支付完成
	 
	 //支付计划状态
	 public static final String PAY_PLAN_01 = "P01";//新增-初始状态
	 public static final String PAY_PLAN_02 = "P02";//未完成（有余额）-流贷放款成功后状态变为P02,发生部分还款或者支付计划修改等均不改变该状态
	 public static final String PAY_PLAN_03 = "P03";//已完成（无余额）-支付完成
	 public static final String PAY_PLAN_04 = "P04";//作废 - 放款不成功的状态 
	 
	 
	 
	 
	 //电票bbsp001接口返回值
	 public static final String BBSP001_200001 = "200001";//200001-新增票据  
	 public static final String BBSP001_200101 = "200101";//200101-出票登记  
	 public static final String BBSP001_200201 = "200201";//200201-承兑申请  
	 public static final String BBSP001_200202 = "200202";//200202-承兑签收  
	 public static final String BBSP001_201801 = "201801";//201801-质押申请  
	 public static final String BBSP001_201802 = "201802";//201802-质押签收  
	 public static final String BBSP001_201901 = "201901";//201901-解质押申请 
	 public static final String BBSP001_201902 = "201902";//201902-解质押签收 
	 public static final String BBSP001_201101 = "201101";//201101-贴现申请  
	 public static final String BBSP001_201102 = "201102";//201102-贴现签收  
	 public static final String BBSP001_100140 = "100140";//100140-撤销出票
	 public static final String BBSP001_200401 = "200401";//200401-未用退回  
	 public static final String BBSP001_202001 = "202001";//202001-提示付款清算通知  
	 public static final String BBSP001_202101 = "202101";//202101-追索同意清偿  
	 
	 //电票通用查询返回值
	 public static final String TE200101_00="TE200101_00";//状态初始化
	 public static final String TE200101_01="TE200101_01";//待处理
	 public static final String TE200101_02="TE200101_02";//出票登记成功
	 public static final String TE200101_03="TE200101_03";//出票登记失败
	 public static final String TE200201_01="TE200201_01";//承兑申请处理中
	 public static final String TE200201_02="TE200201_02";//承兑申请成功
	 public static final String TE200201_03="TE200201_03";//承兑申请失败
	 public static final String TE200202_00="TE200202_00";//承兑签收初始化
	 public static final String TE200202_01="TE200202_01";//承兑签收处理中
	 public static final String TE200202_02="TE200202_02";//承兑签收成功
	 public static final String TE200202_03="TE200202_03";//承兑签收失败
	 public static final String TE200302_02="TE200302_02";//提示收票申请成功
	 public static final String TE200302_03="TE200302_03";//提示收票申请失败

	 public static final String CS01="CS01";//已出票
	 public static final String CS02="CS02";//已承兑
	 public static final String CS03="CS03";//已收票
	 public static final String CS04="CS04";//已到期
	 public static final String CS05="CS05";//已终止
	 public static final String CS06="CS06";//已结清

	/** 20210619 审批系统添加 start   */
	/**
	 * 审批类型类常量定义
	 */
	public static final String AUDIT_TYPE_BATCH="01";//01 批次审批
//	public static final String AUDIT_TYPE_BILLS="02";//02 明细清单审批
	public static final String AUDIT_TYPE_COMMON="03";//03 通用业务审批

	/**
	 * 审批结果常量定义
	 */
	public static String AUDIT_SUCCESS_CODE_00="00";//审批通过
	public static String AUDIT_FAIL_CODE_01="01";//未找到审批路线
	public static String AUDIT_FAIL_CODE_02="02";//审批金额过大 ，所有审批节点 都没有权限
	public static String AUDIT_FAIL_CODE_NO_USERS_03="03";//没有找到审批人员
	public static String AUDIT_FAIL_CODE_04="04";//必输项检查失败
	public static String AUDIT_FAIL_CODE_05="05";//提交第三方系统审批失败

	/**
	 *审批状态常量定义
	 */
	public static String AUDIT_STATUS_STOP = "SP_00";//终止
	public static String AUDIT_STATUS_SUBMIT = "SP_01";//提交审批
	public static String AUDIT_STATUS_RUNNING = "SP_02";//处理中
	public static String AUDIT_STATUS_GOBACK = "SP_03";//驳回
	public static String AUDIT_STATUS_PASS = "SP_04";//通过
	public static String AUDIT_STATUS_UNPROCESSED = "SP_05";//未处理

	//审批结束节点编号
	public static String AUDIT_NODE_END_NUM = "-1";

	public static final String TX_CODE = "TxCode";

	public static final String DSSB_RSPCODE = "RespCode";
	public static final String DSSB_RSPDESC = "RespDesc";
	public static final String DSSB_BODY= "Body";
	/**
	 * DSSB处理成功
	 */
	public static final String DSSB_RSPCODE_SUCCESS="0000";
	public static final String RESPONSE_CODE_E002="E002";


	/** 20210619 审批系统添加 end   */

	//报表状态
	public static final String Report_STATUS_0 = "0";//未生成
	public static final String Report_STATUS_1 = "1";//已生成
	public static final String Report_STATUS_2 = "2";//生成失败
	public static final String Report_STATUS_3 = "3";//正在生成
	
	
	/**
	 * AssetRegister证件类型字段码值
	 */
	public static final String CERT_TYPE_01="01";//01组织机构代码
	public static final String CERT_TYPE_02="02";//02统一授信编码
	public static final String CERT_TYPE_03="03";//03客户号
	
	//参数表码值
	public static final String OL_OPEN_PJC="OL_OPEN_PJC";//在线业务总开关
	public static final String OL_OPEN_YC="OL_OPEN_YC";//在线银承开关
	public static final String OL_OPEN_LD="OL_OPEN_LD";//在线流贷开关
	public static final String OL_OPENTIME_YC="OL_OPENTIME_YC";//在线银承开始时间
	public static final String OL_ENDTIME_YC="OL_ENDTIME_YC";//在线银承结束时间
	public static final String OL_OPENTIME_LD="OL_OPENTIME_LD";//在线流贷开始时间
	public static final String OL_ENDTIME_LD="OL_ENDTIME_LD";//在线流贷结束时间
	
	
	public static final String ZI_ZHU_ZHI_FU ="自主支付"; 
	
	public static final String SEND_RECEIVE_TYPE_01 ="receive";//收发类型-接收
	public static final String SEND_RECEIVE_TYPE_02 ="send";//收发类型-发送
	
	public static final String APPROVAL_ROUTE_01 ="5001001";//分支行审批
	public static final String APPROVAL_ROUTE_0201 ="500100201";//公司条线审批
	public static final String APPROVAL_ROUTE_0202 ="500100202";//小微条线审批
	public static final String APPROVAL_ROUTE_0203 ="500100203";//科金条线审批
	public static final String APPROVAL_ROUTE_03 ="5001003";//资产负债部审批
	public static final String APPROVAL_ROUTE_08 ="5001008";//金融市场部部审批
	
	public static final String BUSITY_DISCOUNT ="01";//票据审价直贴
	public static final String BUSITY_SHIFT_DISCOUNT ="02";//票据审价直转通
	public static final String BUSITY_RE_DISCOUNT ="03";//票据审再贴现
	public static final String BUSITY_MODEL1 ="04";//额度审价模式一
	public static final String BUSITY_MODEL2 ="05";//额度审价模式二
	
	public static final String AUDIT_TYPE_01 = "01";//公司
	public static final String AUDIT_TYPE_02 = "02";//小微
	public static final String AUDIT_TYPE_03 = "03";//科金

	
}

