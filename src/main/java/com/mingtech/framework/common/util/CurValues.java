package com.mingtech.framework.common.util;

public interface CurValues {
	/**
	 * 部门状态：被删除（逻辑删除,实际为停用）
	 */
	public static final int DEPART_DEL = 0;

	// session中对象的Key值
	public static final String USER = "cur_user"; // 当前用户对象
	
	public static final String CUSTOMER = "curCustomer"; // 当前客户对象
	
	public static final String CURACCOUTN = "curAccount"; // 当前客户账户对象

	public static final String RESOURCEMAP = "resmap"; // 当前用户可用的资源MAP(以Action名称为key)

	public static final String ANONYMOUS = "anonymous"; // 匿名角色对象

	public static final String DEFAULT = "default"; // 默认角色对象

	public static final String ROOT = "root"; // 默认超级角色对象

	public static final String REASONTYPE = "reasontype"; // 信息提示原因

	// 登录及注销相关提示信息
	public static final String LOGONMSG1 = "用户名或密码错误！";

	public static final String LOGONMSG2 = "当前用户未开通帐号！";

	public static final String LOGONMSG3 = "登录验证参数无效！";

	public static final String LOGONMSG4 = "IP地址验证失败！";

	public static final String LOGONMSG5 = "登录超时或权限不足！";

	public static final String LOGONMSG6 = "身份不合法！";

	public static final String LOGOUTMSG1 = "注销成功！";
	
	public static final String LOCALCUS = "柜面代理管理员";

	public Long MAPPING_ROLE = new Long(0);

	public Long MAPPING_USER = new Long(1);

	// 有效标记---有效
	public Long VALID_FLAG_TRUE = new Long(1);

	// 有效标记---无效
	public Long VALID_FLAG_FALSE = new Long(0);

	// 报文交易处理状态
	// 发送申请交易 处理状态
	public static final String S_Approval = "审批中"; // 所有审批单的初始状态

	public static final String S_ApprovalOver = "审批结束"; // 针对出票流程中纸票出票 

	public static final String S_CreatingMsg = "待创建"; //  审批通过后，等待创建发送报文。

	public static final String S_Sending = "待发送"; // 等待发送初始状态

	public static final String S_Sended = "已发送"; // （无确认报文类交易，如：纸票登记） ，结束状态

	public static final String S_Unconfirmed = "已发送待确认"; // 等待确认

	public static final String S_Refused = "已拒绝"; // 所有被033 拒绝的交易。

	public static final String S_Confirmed = "已确认"; // 已确认（不需要回执的交易） ， 结束状态

	public static final String S_Signing = "已确认待签收"; // 已确认待签收

	public static final String S_Signed = "已签收"; // 交易处理状态。

	public static final String S_QueryResponse = "已查复"; // 已查复，对于需要查复的报文

	public static final String S_SendFailure = "发送失败"; // 发送失败

	/**
	 * 拒接签收
	 */
	public static final String S_RefuesdSign = "拒绝签收";

	/**
	 * 清分失败
	 */
	public static final String S_FailError = "清分失败";

	/**
	 * 清算失败
	 */
	public static final String SR_Settlement_Fail = "清算失败";

	// 接收申请 处理 状态
	public static final String R_ReceiveUnsign = "已接收"; // 接收交易 需要应答报文的第一个 状态

	public static final String R_Signed = "已签收"; // 完成状态。

	// 报文发送处理状态
	public static final String MS_Sending = "待发送"; // 等待发送的报文；

	public static final String MS_Sended = "已发送"; // 无需确认的报文的 最终状态

	public static final String MS_Unconfirmed = "已发送待确认"; // 等待确认

	public static final String MS_Confirmed = "已确认"; // 已确认 ， 结束状态

	public static final String MS_QueryResponse = "已查复"; // 已查复，对于需要查复的报文

	public static final String MS_SendFailure = "发送失败"; // 发送失败
	
	public static final String MS_Cancell = "已撤销";//报文被撤销
	
	public static final String MS_FailError = "清分失败";
	// 报文接收处理状态
	public static final String MR_Undo = "待处理"; // 接收报文 待处理

	public static final String MR_Doing = "正在处理"; // 接收报文正在处理状态。

	public static final String MR_Done = "已处理"; // 接收报文已处理 完成状态。
	
	

	// 审批已经状态
	public static final String FINISH = "完成";

	public static final String NOTFINISH = "未完成";

	// 
	/**
	 * 签收标记 :同意签收
	 */
	public static final String AgreeSigned = "SU00"; // 同意签收
	/**
	 * 签收标记:拒绝签收
	 */
	public static final String RefuseSigned = "SU01"; // 拒绝签收

	//贴现类别
	public static final String Discount_Sell = "SELL";//贴现卖出
	public static final String Discount_Buy = "BUY";//贴现买入
	/**
	 * 买断式
	 */
	public static final String Discount_Repurchased_No = "RM00";//买断式
	/**
	 * 回购式
	 */
	public static final String Discount_Repurchased_Yes = "RM01";//回购式
	/**
	 * 线上清算
	 */
	public static final String Discount_SettleOnline = "SM00";
	/**
	 * 线下清算
	 */

	public static final String Discount_SettleUnderline="SM01";
	
	/**
	 * 拒付追索
	 */
	public static final String Discount_Recourse_Yes = "RT00"; //拒付追索
	
	/**
	 * 非拒付追索
	 */
	public static final String Discount_Recourse_No = "RT01";  //非拒付追索
	
	
	// sequence 类别
	
	public static final String Discount_BanEndorsement_Yes = "EM00"; // 可再转让
	
	public static final String Discount_BanEndorsement_No = "EM01"; // 不得转让
	
	// sequence 类别

	public static final String DraftRequisition = "DraftRequisition"; // 银承出票

	public static final String MS_SEND = "send"; // 发送

	public static final String MS_RECEIVE = "receive"; // 发送

	

	public static final String NPCCODE = "9968";

	//数字证书行号绑定关系变更申请报文
	public static final String AB_add = "AB00"; //新增

	public static final String AB_add_name = "新增";

	public static final String AB_delete = "AB01"; //删除

	public static final String AB_delete_name = "删除";

	public static final String CDCB_on = "启用"; //证书启用

	public static final String CDCB_off = "注销"; //证书注销

	

	// 059 行名行号变更状态
	public static final String BANK_OTHER = "状态未知";
	public static final String BANK_NEW = "addedinf";   // xml 标签    新增
	public static final String BANK_NEW_CODE = "02";
	public static final String BANK_DEL = "deltdinf";   // xml 标签   注销
	public static final String BANK_DEL_CODE = "03";
	public static final String BANK_MODIFY = "altrdinf";// xml 标签   变更
	public static final String BANK_MODIFY_CODE = "01";
	
	public static final String BD_STATUS_ON_CODE = "01";
	public static final String BD_STATUS_ON = "有效";
	public static final String BD_STATUS_BEFORE_ON_CODE = "00";
	public static final String BD_STATUS_BEFORE_ON = "生效前";
	public static final String BD_STATUS_OFF_CODE = "02";
	public static final String BD_STATUS_OFF = "注销";
	
	public static final String BANK_STATE_DONE = "行状态已更新";
	
	// 062 接入点状态变更
	public static final String AP_OTHER = "-1";   // 变更
	public static final String AP_MODIFY_CODE = "01";   // 变更
	public static final String AP_NEW_CODE = "02";      // 新增
	public static final String AP_DEL_CODE = "03";      // 注销
	
	public static final String AP_MSG_NEWMODIFY_CODE = "01";      // 01-新增/变更
	public static final String AP_MSG_DEL_CODE = "02";      // 02-删除
	
	public static final String AP_ON = "01";       // 1：有效
	public static final String AP_OFF = "02";      // 2：注销
	
	/** Response_Code_Yes 同意签署*/
	public static final String Response_Code_Yes = "RM01";
	/** Response_Code_No 拒绝签收*/
	public static final String Response_Code_No="RM02";
	
	public static final String DraftRegister = "出票登记";
	public static final String AcceptanceRequest = "承兑申请";
	public static final String IssuanceRequest = "提示收票";
	public static final String Edraft_TradeType_Discount_sell = "贴现卖出";
	public static final String RediscountWithCBR = "再贴现卖出";
	public static final String RepurchasedRediscountWithCBR = "回购式再贴现赎回申请回复";
	public static final String GuaranteeRequest = "保证申请";
	public static final String RediscountWithCentralBankRequest = "回购式再贴现赎回申请";
	public static final String CentralBankSellingDrafts = "央行卖票回复";
	 
	//052处理结果码值
	public static final String PrcCdValue = "PE1I0000";    // 正确的处理结果码值
	public static final String RepeatLogon = "PE1O3014";   //重复登录的处理结果码值
	public static final String RepeatLogout = "PE1O3016";  //重复退出的处理结果码值
	
	//纸票登记"待提交"状态
	public static final String WaitSubmit = "待提交"; //创建完纸票登记未点发送按钮状态
	
	
	/** 开户机构代理回复签章 */
	public static final String ProxySignature = "PS00"; // 开户机构代理回复签章
	
	
	/** 客户自己签章*/
	public static final String CustomerSignature = "PS01"; // 客户自己签章
	
	
	/** 全额 */
	public static final String FullDeposit = "全额"; // 全额
	
	/** 差额 */
	public static final String BalanceDeposit = "差额"; // 差额
	
	
	/** 接入行 */
	public static final String DirectBank = "RC00"; // 接入行
	
	
	/** 企业 */
	public static final String Enterprise = "RC01"; // 企业
	
	
	/** 含委托/承诺兑付 */
	public static final String UcondlConsgnmtMrk_Yes = "CC00";
	
	/** 不含委托/不承诺兑付 */
	public static final String UcondlConsgnmtMrk_No = "CC01";
	
	/** Bussiness_Discount_Sell  贴现卖出*/
	public static final String Bussiness_Discount_Sell="贴现卖出";
	
	/** Bussiness_Discount_Buy  贴现买入*/
	public static final String Bussiness_Discount_Buy="贴现买入";
	
	/** Bussiness_RepurchasedDiscount_Sell 回购式贴现赎回卖出*/
	public static final String Bussiness_RepurchasedDiscount_Sell="回购式贴现赎回卖出";
	
	/** Bussiness_RepurchasedDiscount_Buy 回购式贴现赎回买入*/
	public static final String Bussiness_RepurchasedDiscount_Buy="回购式贴现赎回买入";
	
	/** Bussiness_Rediscount_Sell 转贴现卖出*/
	public static final String Bussiness_Rediscount_Sell="转贴现卖出";
	
	/** Bussiness_Rediscount_Buy 转贴现买入*/
	public static final String Bussiness_Rediscount_Buy="转贴现买入";
	
	/** Bussiness_InnerRediscount 系统内转贴现*/
	public static final String Bussiness_InnerRediscount="系统内转贴现";
	
	/** Bussiness_RepurchasedRediscount_Sell 回购式转贴现赎回卖出*/
	public static final String Bussiness_RepurchasedRediscount_Sell="回购式转贴现赎回卖出";
	
	/** Bussiness_RepurchasedRediscount_Buy 回购式转贴现赎回买入*/
	public static final String Bussiness_RepurchasedRediscount_Buy="回购式转贴现赎回买入";
	
	/**广播类型*/
	public static final String Non_Broadcast = "BC00";
	
	public static final String Non_Broadcast_Name = "非广播";
	
	public static final String One_Broadcast = "BC01";
	
	public static final String One_Broadcast_Name = "一级广播";
	
	public static final String Two_Broadcast = "BC02";
	
	public static final String Two_Broadcast_Name = "二级广播";
	
}
