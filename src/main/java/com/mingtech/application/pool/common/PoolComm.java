package com.mingtech.application.pool.common;

import java.math.BigDecimal;

import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.EdraftStatus;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Aug 19, 2010 4:37:31 PM
* @描述: [PoolComm]票据池共用常量定义
*/
public class PoolComm{
	
	//审批流程模板id
	
	/** 代保管存票*/
	public static final String POOL_STORAGE_IN = "pool_storage_in";
	/** 代保管取票*/
	public static final String POOL_STORAGE_OUT = "pool_storage_out";
	/** 托管已确认*/
	public static final String POOL_STORAGE_YES = "1";
	/** 托管未确认*/
	public static final String POOL_STORAGE_NO = "2";
	/** 票据池入池*/
	public static final String POOL_POOL_IN = "pool_pool_in";
	/** 票据池出池*/
	public static final String POOL_POOL_OUT = "pool_pool_out";
	/**入库*/
	public static final String POOL_STOCK_IN = "pool_stock_in";
	/**出库*/
	public static final String POOL_STOCK_OUT = "pool_stock_out";
	/**银承票据*/
	public static final String POOL_ACCEPTANCESTOCK = "pool_acceptanceStock";
	/**额度手工归还**/
	public static final String POOL_RELEASE_EDU = "pool_release_edu";
	
	//审批流程常量
	/** 批次id*/
	public static final String POOL_BATCH_ID = "batchID";
	/** 用户id*/
	public static final String POOL_USER_ID = "p_userId";
	/** 机构id */
	public static final String POOL_BRCH_ID = "brch_id";
	/** 产品id */
	public static final String POOL_BUPR_ID = "bupr_id";
	/** 审核机构级别 */
	public static final String NODE_LEVEL = "nodeLevel";
	
	/** 审批子流程机构id*/
	public static final String POOL_EXAMINE_BRCH_ID ="brchId";
	
	/** POOL_SERVICES_NAME*/
	public static final String POOL_SERVICES_NAME = "serviceName";
	
	/** 工作流-审批通过*/
	public static final String EXAMINE_YES ="1";
	/** 工作流-审批不通过*/
	public static final String EXAMINE_NO ="0";
	
	/* 审核 */
	/* 同意 */
	public static final String APPROVE_YES = "1";
	/* 不同意 */
	public static final String APPROVE_NO = "0";
	
	/** 票据池常量*/
	public static final String PJC = "PJC";
	//票据类型
	/** 银承*/
	public static final String BILL_TYPE_BANK = "AC01";
	/** 商承 */
	public static final String BILL_TYPE_BUSI = "AC02";
	
	//票据介质 
	/** 纸质 */
	public static final String BILL_MEDIA_PAPERY = "1";
	/** 电子 */
	public static final String BILL_MEDIA_ELECTRONICAL = "2";
	
	//币种
	/** 人民币 */
	public static final String CURRENCY_TYPE = "CNY";
	
	//票据池业务类型
	/** 票据池业务*/
	public static final String BILL_POOL = "YW_01";
	/** 代保管业务*/
	public static final String BILL_STORAGE = "YW_02";
	/** 存单池业务*/
	public static final String CDP_POOL = "YW_03";
	
	/**开通票据池/代保管标示**/
	public static final String DRAFT_POOL_OPEN = "SP_004";
	
	/**关闭票据池/代保管标示**/
	public static final String DRAFT_POOL_CLOSE = "SP_006";
	
	/** 勾选代保管/票据池业务 **/
	public static final String POOL_STORAGE_OPEN = "1";
	
	/** 未勾选代保管/票据池业务 **/
	public static final String POOL_STORAGE_CLOSE = "2";
	
	
	
	//额度种类
	/** 票据衍生额度:低风险额度*/
	public static final String ED_PJC = "ED_10";
	/** 票据衍生额度:高风险额度*/
	public static final String ED_PJC_01 = "ED_20";
	/** 存单衍生额度*/
	public static final String ED_CDP = "ED_11";
	
	/** 活期保证金*/
	public static final String ED_BZJ_HQ = "ED_21";
	/** 定期保证金*/
	public static final String ED_BZJ_DQ = "ED_22";
	
	//资产池种类
	/** 票据池*/
	public static final String ZCC_PJC = "ZC_01";
	
	//信贷产品类型
	/** 银承*/
	public static final String XDCP_YC = "XD_01";
	/** 流贷*/
	public static final String XDCP_LD = "XD_02";
	/** 保函*/
	public static final String XDCP_BH = "XD_03";
	/** 商票贴现*/
	public static final String XDCP_SPTX = "XD_04";
	
	//信贷产品结清状态
	/**取消放贷*/
	//public static final String JQZT_QXFD = "JQ_02";
	/** 已结清 */
	public static final String JQZT_YJQ = "JQ_00";
	/** 未结清*/
	public static final String JQZT_WJQ = "JQ_01";
	
	
	/***
	 * ****************************************保证金相关start*********************************
	 */
	
	//保证金类型
	/** 活期 */
	public static final String BZJ_HQ = "BZJ_00";
	/** 定期*/
	public static final String BZJ_DQ = "BZJ_01";
	
	/**保证金状态-无效*/
	public static final String BAIL_STATUS_INACTIVE = "BDS_00";
	
	/**保证金状态-有效*/
	public static final String BAIL_STATUS_ACTIVE = "BDS_01";
	
	/** 同城*/
	public static final String SAME_CITY="CT_01";
	/** 异地*/
	public static final String DIFFERENT_CITY="CT_02";
	
	/***
	 * ****************************************保证金相关end*********************************
	 */
	
	//数据来源
	/** 网银*/
	public static final String DATA_LY_WY = "LY_00";
	/** 柜面 */
	public static final String DATA_LY_GM = "LY_01";
	/** 代保管转入池*/
	public static final String DATA_LY_DBGZRC = "LY_02";
	/** 入池转代保管*/
	public static final String DATA_LY_RCZDBG = "LY_03";
	/** 信贷*/
	public static final String DATA_LY_XD = "LY_05";
	/** 票据池*/
	public static final String DATA_LY_PJC = "LY_06";
	/** 核心*/
	public static final String DATA_LY_HX = "LY_07";
	/** 代保管*/
	public static final String DATA_LY_DBG = "LY_08";
	
	/***
	 * ***********************************额度相关start***************************
	 */
	
	//额度使用状态
	/** 占用 */
	public static final String ED_ZY = "EDS_00";
	/** 部分释放*/
	public static final String ED_BFSF = "EDS_01";
	/** 全部释放*/
	public static final String ED_QBSF = "EDS_09";
	
	//信贷融资业务状态
	/** 未用释放*/
	public static final String RZCP_XZSQ="RZ_01";
	/** 申请已提交*/
	//public static final String RZCP_YTJ="RZ_02";
	/** 额度占用成功*/
	public static final String RZCP_YQS="RZ_03";
	/** 申请驳回*/
	//public static final String RZCP_YBH="RZ_04";
	/** 确认结清*/
	public static final String RZCP_QRJQ="RZ_05";
	/** 审批通过*/
	//public static final String RZCP_SPTG="RZ_06";
	
	/**额度-占用**/
	public static final String ED_OPT_ZY="ED_ZY";
	/**额度-释放**/
	public static final String ED_OPT_SF="ED_SF";
	/**额度-冻结**/
	public static final String ED_OPT_DJ="ED_DJ";
	/**额度-解冻**/
	public static final String ED_OPT_JD="ED_JD";
	/**额度-资产额度释放**/
	public static final String ED_OPT_SF_ASSET="ED_SF_AT";
	
	/**
	 * 额度使用原因-新增信贷业务
	 */
	public static final String ED_USE_REASEON_NEWBUSI = "新增信贷业务";
	
	/**
	 * 额度使用原因-信贷业务到期
	 */
	public static final String ED_USE_REASEON_BUSIDUE = "信贷业务到期";
	
	/**
	 * 额度使用原因-额度占用调整
	 */
	public static final String ED_USE_REASEON_EDAJUST = "额度占用调整";
	

	/***
	 * ***********************************额度相关end***************************
	 */
	
	/***
	 * *********************************************托管存票常量start**************************************
	 */
	
	//存票/入池明细状态
	/** 临时数据*/
	public static final String IN_LSSJ = "IN_001";
	/** 待分发*/
	public static final String IN_DFF = "IN_002";
	/** 确认分发*/
	public static final String IN_QRFF = "IN_003";
	/** 业务驳回*/
	public static final String IN_YWBH = "IN_004";
	/** 已加入批次*/
	public static final String IN_JRPC = "IN_005";
	/** 提交审批*/
	public static final String IN_TJSP = "IN_006";
	/** 审批中*/
	public static final String IN_SPZ = "IN_007";
	/** 审批通过*/
	public static final String IN_SPTG = "IN_008";
	/** 审批不通过*/
	public static final String IN_SPBTG = "IN_009";
//	/**
//	 * 手续费收取成功
//	 */
//	public static final String IN_SXFSQ = "IN_009_1";
	
	/** 已签收*/
	public static final String IN_YQS = "IN_010";
	/** 已退回*/
	public static final String IN_YTH = "IN_011";
	/** 已驳回*/
	public static final String IN_YBH = "IN_012";
	/** 发送记账 */
	public static final String IN_FSJZ = "IN_013";
	/** 记账成功*/
	public static final String IN_JZCG = "IN_014";
	/** 记账失败*/
	public static final String IN_JZSB = "IN_015";
	/** 记账撤回*/
	public static final String IN_JZCH = "IN_016";
	/** 入库申请*/
	public static final String IN_RKSQ = "IN_017";
	
	

	
	//取票/出池明细状态
	/** 临时数据*/
	public static final String OUT_LSSJ = "OUT_001";
	/** 已提交申请 */
	public static final String OUT_YTJSQ = "OUT_002";
	/** 业务驳回*/
	public static final String OUT_YWBH = "OUT_003";
	/** 加入批次*/
	public static final String OUT_JRPC = "OUT_005";
	/** 提交审批 */
	public static final String OUT_TJSP = "OUT_006";
	/** 审批中*/
	public static final String OUT_SPZ = "OUT_007";
	/** 审批通过*/
	public static final String OUT_SPTG = "OUT_008";
	/** 审批不通过*/
	public static final String OUT_SPBTG = "OUT_009";
	/** 已签收*/
	public static final String OUT_YQS = "OUT_010";
	/** 已退回*/
	public static final String OUT_YTH = "OUT_011";
	/** 已驳回*/
	public static final String OUT_YBH = "OUT_012";
	/** 发送记账*/
	public static final String OUT_FSJZ = "OUT_013";
	/** 记账成功*/
	public static final String OUT_JZCG = "OUT_014";
	/**  记账失败*/
	public static final String OUT_JZSB = "OUT_015";
	/** 记账撤回*/
	public static final String OUT_JZCH= "OUT_016";
	/** 出库申请*/
	public static final String OUT_CCSQ = "OUT_017";
	
	
	//批次状态
	/** 新增批次*/
	public static final String  PC_XZPC= "PBS_001";
	/** 提交审批*/
	public static final String  PC_TJSP= "PBS_002";
	/** 审批中*/
	public static final String  PC_SPZ= "PBS_003";
	/** 审批通过*/
	public static final String  PC_SPTG= "PBS_004";
	/** 审批不通过*/
	public static final String  PC_SPBTG= "PBS_005";
	/**手续费收取完毕 */
	public static final String  PC_SXFSQ= "PBS_005_1";
	
	/** 批次已签收*/
	public static final String  PC_YQS = "PBS_006";
	/** 批次已退回*/
	public static final String  PC_YTH = "PBS_007";
	/** 批次已驳回*/
	public static final String  PC_YBH = "PBS_008";
	/** 已发记账申请*/
	public static final String  PC_YFJZSQ= "PBS_009";
	/** 记账成功*/
	public static final String  PC_JZCG= "PBS_010";
	/** 记账失败*/
	public static final String  PC_JZSB= "PBS_011";
	/** 记账撤回*/
	public static final String  PC_JZCH= "PBS_012";
	/** 入库申请*/
	public static final String  PC_RKSQ= "PBS_013";
	/** 出库申请*/
	public static final String  PC_CKSQ= "PBS_014";	
	
	/***
	 * *********************************************托管存票常量end**************************************
	 */
	
	/***
	 * *********************************************电票报文常量start**************************************
	 */
	
	//电票报文状态
	/** 已发质押申请*/
	public static final String BW_ZY_YFZYSQ = "PBW_001";
	/** 质押申请失败*/
	public static final String BW_ZY_SQSB = "PBW_002";
	/** 质押申请成功*/
	public static final String BW_ZY_SQCG = "PBW_003";
	/** 已发质押驳回*/
	public static final String BW_ZY_YFZYBH = "PBW_004";
	/** 质押驳回失败*/
	public static final String BW_ZY_ZYBHSB = "PBW_005";
	/** 质押驳回成功*/
	public static final String BW_ZY_BHCG = "PBW_006";
	/** 已发质押签收*/
	public static final String  BW_ZY_YFZYQS= "PBW_007";
	/** 质押签收失败*/
	public static final String  BW_ZY_ZYQSSB= "PBW_008";
	/** 质押签收成功*/
	public static final String  BW_ZY_ZYQSCG= "PBW_009";
	
	/** 已发解质押申请*/
	public static final String  BW_JZY_YFJZYSQ= "PBW_011";
	/** 解质押申请成功*/
	public static final String  BW_JZY_JZYSQCG= "PBW_012";
	/** 解质押申请失败*/
	public static final String  BW_JZY_JZYSQSB= "PBW_013";
	/** 已发解质押申请撤回*/
	public static final String  BW_JZY_YYFJZYSQCH= "PBW_014";
	/** 解质押申请撤回成功*/
	public static final String  BW_JZY_JZYSQCHCG= "PBW_015";
	/** 解质押申请撤回失败*/
	public static final String  BW_JZY_JZYSQCHSB= "PBW_016";
	
	/** 解质押申请已签收 */
	public static final String  BW_JZY_JZYSQYQS= "PBW_017";
	/** 解质押申请已驳回*/
	public static final String  BW_JZY_JZYSQYBH= "PBW_018";
	
	/** 已发质押申请撤回*/
	public static final String  BW_ZY_YFZYSQCH= "PBW_019";
	/** 质押申请撤回成功*/
	public static final String  BW_ZY_ZYSQCHCG= "PBW_020";
	/** 质押申请撤回失败*/
	public static final String  BW_ZY_ZYSQCHSB= "PBW_021";
	
	/** 已发纸票查询*/
	public static final String BW_ZP_YFCXSQ = "PBW_101";
	/** 纸票查询被拒绝*/
	public static final String BW_ZP_SQBJJ = "PBW_102";
	/** 纸票已查复*/
	public static final String BW_ZP_YCF = "PBW_103";
	/** 纸票人行未登记*/
	public static final String BW_ZP_WDJ = "PBW_104";
	//电票报文状态
	
	/***
	 * *********************************************电票报文常量start**************************************
	 */
	
	
	/***
	 * ***********************************托管表常量start**********************************
	 */
	
	//代保管表状态
	/** 已存票*/
	public static final String  DBG_YCP= "PDS_001";
	/** 票据已到期*/
	public static final String  DBG_PJYDQ= "PDS_002";
	/** 票据已发托*/
	public static final String  DBG_PJYFT= "PDS_003";
	/** 托收已回款*/
	public static final String  DBG_TSYHK= "PDS_004";
	/** 托收拒付*/
	public static final String  DBG_TSJF= "PDS_005";
	/** 取票申请*/
	public static final String  DBG_QPSQ= "PDS_006";
	/** 已取票*/
	public static final String  DBG_YQP= "PDS_007";
	/** 代保管转入池申请*/
	public static final String  DBG_ZRCSQ= "PDS_008";
	
	
	/***
	 * ***********************************托管表常量end**********************************
	 */
	
	
	/***
	 * ***********************************票据池表常量start**********************************
	 */
	//票据池表状态
	/** 已入池*/
	public static final String  PJC_YRC= "PCS_001";
	/** 票据已到期*/
	public static final String  PJC_PJYDQ= "PCS_002";
	/** 票据已发托*/
	public static final String  PJC_PJYFT= "PCS_003";
	/** 托收已回款*/
	public static final String  PJC_TSYHK= "PCS_004";
	/** 托收拒付*/
	public static final String  PJC_TSJF= "PCS_005";
	/** 出池申请*/
	public static final String  PJC_CCSQ= "PCS_006";
	/** 已出池*/
	public static final String  PJC_YCC= "PCS_007";
	
	/***
	 * ***********************************票据池表常量end**********************************
	 */
	
	

	
	
	
	
	
	/** 代保管存票*/
	public static final String PRODUCT_TYPE_DBGCP="2000001";
	/** 代保管取票*/
	public static final String PRODUCT_TYPE_DBGQP="2000002";
	/** 入池*/
	public static final String PRODUCT_TYPE_RC="2000101";
	/** 出池*/
	public static final String PRODUCT_TYPE_CC="2000102";
	/** 代保管转入池*/
	public static final String PRODUCT_TYPE_DBGZRC="2000103";
	/** 入库*/
	public static final String PRODUCT_TYPE_RK="2000201";
	/** 出库*/
	public static final String PRODUCT_TYPE_CK="2000202";
	/** 转账*/
	public static final String PRODUCT_TYPE_ZZ="99999";
	/** 手工释放额度**/
	public static final String PRODUCT_TYPE_SGSFED="2000104";
	
	/** 手工占用额度**/
	public static final String PRODUCT_TYPE_SGZYED="2000105";
	/** 本币手工释放额度**/
	public static final String PRODUCT_TYPE_BBSGSFED="2000106"; 
	/** 本币手工自由释放额度**/
	public static final String PRODUCT_TYPE_BBZYSFED="2000107";
	
	

	//---------保证金状态------------
	/**新增交易**/
	public static final String BAIL_TRANS_ADD = "PBZ_00";
	/**已提交交易**/
	public static final String BAIL_TRANS_CONFIRM = "PBZ_01";
	/**已确认交易**/
	public static final String BAIL_TRANS_SUBMIT = "PBZ_02";
	/**驳回交易**/
	public static final String BAIL_TRANS_BACK = "PBZ_03";
	

	//----------保证金交易类型-------------
	/**活转定**/
	public static final String BAIL_CUR_TIME = "BZT_00";
	/**活期转结算**/
	public static final String BAIL_CUR_ACCT = "BZT_01";
	/**定期到期处理**/
	public static final String BAIL_TIME_DEAIL = "BZT_02";
	
	/** 同业额度占用交易类型--入池*/
	public static final String CREDITQUTOA_USED_RC = "10";
	/** 同业额度释放交易类型--出池*/
	public static final String CREDITQUTOA_RELEASE_CC = "20";
	
	/** 质押类别-票据池入池*/
	public static final String COLL_TYPE_POOL = "01";
	/** 解质押类别-票据池出池*/
	public static final String UNCOLL_TYPE_POOL = "02";
	
	/** 大票表保存是否是票据池对象 **/
	public static final String S_IF_POOLFLAG="01";//01代表是票据池对象，发托使用
	public static final String S_IF_DRAFTFLAG="02";//02代表是代保管对象，发托使用
	
	public static final String PAPER_QUERY_NORESULT_CODE = "PE1I0004";//纸票查询--查询异常 不存在该票据的纸质票据登记信息错!
	
	/**代保管最低价 **/
	public static final BigDecimal STORAGE_LOW_AMT = new BigDecimal(50);
	/**代保管费率 **/
	public static final BigDecimal STORAGE_LOW_RATE = new BigDecimal(0.05);
	/**票据池最低价 **/
	public static final BigDecimal POOL_LOW_AMT = new BigDecimal(500);
	/**票据池费率 **/
	public static final BigDecimal POOL_LOW_RATE = new BigDecimal(0.5);
	
	public static final String RISK_BILL = "1" ; //风险票据标识
	
	/** 是否产生额度 **/
	public static final String EDU_EXIST="1";    //1产生额度
	public static final String EDU_NOT_EXIST="0";  //0不产生额度
	
	/**判断是否为root用户登录
	 * @param user
	 * @return
	 */
	public static boolean isRootUser(User user){
		if("root".equals(user.getLoginName())){//如果为root登录
			return true;
		}
		return false;
	}
	
	
	/***
	 * *********************************库存start*********************************
	 */
	
	//入库明细
//	/** 入库申请*/
//	public static final String RK_RKSQ = "RK_000";
	/** 加入批次*/
	public static final String RK_JRPC = "RK_001";
	/** 提交审批*/
	public static final String RK_TJSP = "RK_002";
	/** 审批中*/
	public static final String RK_SPZ = "RK_003";
	/** 审批通过*/
	public static final String RK_SPTG = "RK_004";
	/** 审批不通过*/
	public static final String RK_SPBTG = "RK_005";
//	/** 入库退回*/
//	public static final String RK_RKTH = "RK_006";
	/** 确认入库*/
	public static final String RK_QRRK = "RK_007";
//	/** 入库申请*/
//	public static final String RK_RKCH = "RK_008";
	
	//入库批次状态
	/** 新增批次*/
	public static final String  RK_PC_XZPC= "PRK_001";
	/** 提交审批*/
	public static final String  RK_PC_TJSP= "PRK_002";
	/** 审批中*/
	public static final String  RK_PC_SPZ= "PRK_003";
	/** 审批通过*/
	public static final String  RK_PC_SPTG= "PRK_004";
	/** 审批不通过*/
	public static final String  RK_PC_SPBTG= "PRK_005";
//	/** 批次已退回*/
//	public static final String  RK_PC_YTH = "PRK_006";
	/** 确认入库*/
	public static final String  RK_PC_QRRK = "PRK_007";
//	/** 提交入库*/
//	public static final String  RK_PC_TJRK = "PRK_008";
//	/** 确认受理*/
//	public static final String  RK_PC_QRSL = "PRK_009";
//	/** 入库确认退回*/
//	public static final String  RK_PC_QRTH = "PRK_010";
//	/**逆回购业务出库申请*/
//	public static final String  RK_PC_DQCL = "PRK_011";
//	/**逆回购业务出库申请退回*/
//	public static final String  RK_PC_DQTH = "PRK_012";
	
	//出库明细
//	/** 出库申请*/
//	public static final String CK_CKSQ = "CK_000";
	/** 加入批次*/
	public static final String CK_JRPC = "CK_001";
	/** 提交审批*/
	public static final String CK_TJSP = "CK_002";
	/** 审批中*/
	public static final String CK_SPZ = "CK_003";
	/** 审批通过*/
	public static final String CK_SPTG = "CK_004";
	/** 审批不通过*/
	public static final String CK_SPBTG = "CK_005";
//	/** 出库退回*/
//	public static final String CK_CKTH = "CK_006";
	/** 确认出库*/
	public static final String CK_QRCK = "CK_007";
//	/** 确认出库*/
//	public static final String CK_CKCH = "CK_008";
	
	//出库批次状态
	/** 新增批次*/
	public static final String  CK_PC_XZPC= "PCK_001";
	/** 提交审批*/
	public static final String  CK_PC_TJSP= "PCK_002";
	/** 审批中*/
	public static final String  CK_PC_SPZ= "PCK_003";
	/** 审批通过*/
	public static final String  CK_PC_SPTG= "PCK_004";
	/** 审批不通过*/
	public static final String  CK_PC_SPBTG= "PCK_005";
//	/** 批次已退回*/
//	public static final String  CK_PC_YTH = "PCK_006";
	/** 确认出库*/
	public static final String  CK_PC_QRCK = "PCK_007";
//	/** 确认受理*/
//	public static final String CK_PC__QRSL = "PCK_008";
//	/** 确认出库-退回*/
//	public static final String CK_PC_QRTH = "PCK_009";
	//库表
	/** 已入库*/
	public static final String  KC_YRK= "KC_001";
	/** 出库申请*/
	public static final String  KC_CKSQ= "KC_005";
	/** 已出库*/
	public static final String  KC_YCK= "KC_008";
	
	//入库业务类型
	/** 直贴买入*/
	public static final String  RK_YW_ZTMR= "01";
	/** 买断式转贴现买入*/
	public static final String  RK_YW_MDZTMR= "02";
	/** 回购式转贴现买入*/
	public static final String  RK_YW_HGZTMR= "03";
	/** 正回购买入*/
	public static final String  RK_YW_ZHGMR= "04";
	/** 代保管存票*/
	public static final String  RK_YW_DBGCP= "05";
	/** 票据池入池*/
	public static final String  RK_YW_PJCRC= "06";
	/** 本行承兑*/
	public static final String  RK_YW_BHCD_RK= "07";
	/** 其他*/
	public static final String  RK_YW_QT_RK= "10";
	/** 票据内集中业务*/
	public static final String  RK_YW_PJJZ= "11";
	/** 代保管转票据池*/
	public static final String  RK_YW_DBGZPJC= "12";
	
	//出库业务类型
	/** 卖断式转贴现卖出*/
	public static final String  RK_YW_MDZTMC= "01";
	/** 回购式转贴现卖出*/
	public static final String  RK_YW_HGZTMC= "02";
	/** 逆回购到期处理*/
	public static final String  RK_YW_YHGMC= "03";
	/** 再贴现卖出*/
	public static final String  RK_YW_ZTMC= "04";
	/** 代保管取票*/
	public static final String  RK_YW_DBGQP= "05";
	/** 票据池出池*/
	public static final String  RK_YW_PJCCC= "06";
	/** 本行承兑*/
	public static final String  RK_YW_BHCD_CK= "07";
	/** 托收*/
	public static final String  RK_YW_BHCD_TS= "08";
	/** 再贴现回购转出*/
	public static final String  RK_YW_ZTHGMC= "09";
	/** 其他*/
	public static final String  RK_YW_QT_CK= "10";
	/** 票据内集中业务*/
	public static final String  RK_YW_QT_JZ= "11";
	
//	//出入库类别
//	/** 临时出库*/
//	public static final String  CK_LS= "CK_01";
//	/** 正式出库*/
//	public static final String  CK_ZS= "CK_02";
//	/** 临时入库*/
//	public static final String  RK_LS= "RK_01";
//	/** 正式入库*/
//	public static final String  RK_ZS= "RK_02";

	//是否为封包票据
	/** 封包票据*/
	public static final String FB_TRUE = "1";
	/** 非封包票据*/
	public static final String FB_FALSE = "0";
	
	
	/***
	 * *********************************库存end*********************************
	 */
	
	/**是否瑕疵票据*/
	public static final String YC_XC_ZCPJ ="XC_01";//正常票据
	public static final String YC_XC_XCZZC ="XC_02";//瑕疵转正常
	public static final String YC_XC_XCPJ ="XC_03";//瑕疵票据
	
	//保证金变更
	/**新增审批***/
	public static final String BZJ_BG_00 = "BZJ_BG_00";
	/**审批通过***/
	public static final String BZJ_BG_01 = "BZJ_BG_01";
	/**审批不通过***/
	public static final String BZJ_BG_02 = "BZJ_BG_02";
	/**审批确认***/
	public static final String BZJ_BG_03 = "BZJ_BG_03";
	/**审批失败***/
	public static final String BZJ_BG_04 = "BZJ_BG_04";
	
	
	/**
	 * 票据池账户
	 */
	public static final String ACCT_BZJC="1";    //保证金账户
	public static final String ACCT_JS="2";    //结算账户
	public static final String ACCT_PT="3";     //无
	public static final String ACCT_BZJ_DQ = "4"; // 票据池保证金定期账户



	/**
	 * 票据池规则管理定义状态值
	 */
	public static final String SP001 = "SP_001"; // 审核拒绝
	public static final String SP002 = "SP_002"; // 未生效
	public static final String SP003 = "SP_003"; // 复核
	public static final String SP005 = "SP_005"; // 已过期
	public static final String SP004 = "SP_004"; // 已开通
	public static final String SP006 = "SP_006"; // 已解约
	
	
	public static final String PJC_ZC="0";  //票据池
	public static final String PJC_JT="1";  //集团票据池
	public static final String PJC_CD="2";  //大额存单
	
	public static final String PJC_LEVEL="0";  //客户级别，一等级
	public static final String PJC_TLEVEL="1";  //客户级别,二等级
	public static final String BLACK_BILL="BLACK_BILL";  //池内异常票据定义状态
	
	public static final String CT_01 = "CT_01";//实体票据池
	public static final String CT_02 = "CT_02";//虚拟票据池
	
	public static final String LOW_RISK = "FX_01";//低风险
	public static final String HIGH_RISK = "FX_02";//高风险
	public static final String NOTIN_RISK = "FX_03";//不在风险名单中
	
	public static final String SOUR_EBK = "00";//数据来源：客户网银系统
	public static final String SOUR_BBSP = "01";//数据来源：银行（票据系统）
	
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
	
	
	/**
	 * 业务 已结清 01
	 */
	public static final String BUSI_END_YES="01";
	/** 
	 * 业务未 结清 00
	 */
	public static final String BUSI_END_NO="00";
	

	
	/* 签收状态 */
	/* 同意签收 */
	public static final String SIGN_UP_MARK_CODE_YES = "SU00";
	/* 拒绝签收 */
	public static final String SIGN_UP_MARK_CODE_NO = "SU01";
	
	

	
	/**
	 * ***************************************20180716 zhaoding 资产池新增常量******************************************
	 */
	
	
	/**
	 * 默认的信贷产品ID，用于额度使用记录中，没有信贷产品的情况，如：票据出池时，冻结额度
	 */
	public static final String DEFAULT_CREDIT_ID = "RSVD_CRT_ID_FOR_NULL";
	/**
	 * 默认的信贷产品ID，用于额度使用记录中，没有信贷产品的情况，如：票据出池时，冻结额度
	 */
	public static final String DEFAULT_CREDIT_NO = "RSVD_CRT_NO_FOR_NULL";
	
	//额度浮动
	
	/** 浮动 **/
	public static final String ED_FD_YES = "FD_YES";
	/** 不浮动 **/
	public static final String ED_FD_NO = "FD_NO";
	
	/***数据来源**/
	
	public static final String REQ_XD = "01";
	public static final String REQ_ECDS = "02";
	
	/***池模式**/
	public static final String POOL_MODEL_01 = "01";	//总量控制
	public static final String POOL_MODEL_02 = "02"; //最短期限错配
	
	/**
	 * ***************************************20180716 zhaoding END******************************************
	 */
	

	/*托收*/
	public static final String PTS001 = "PTS_001";//未发出托收

	public static final String PTS002 = "PTS_002";//已发出托收


	public static final String PTS003 = "PTS_003";//发托记账完毕
	public static final String PTS004 = "PTS_004";//托收收回记账失败(未记账)	
	public static final String PTS005 = "PTS_005";//发托记账申请
	/**************汉口银行票据池201810 start***************/
	
	/***pool_in表状态字段 plStatus（入池状态）-start***/
	
	public static final String RC_00 = "RC_00";//:新建数据
	public static final String RC_01 = "RC_01";//:已发质押申请
	public static final String RC_02 = "RC_02";//质押申请待签收
	public static final String RC_03 = "RC_03";//已发质押签收申请
	public static final String RC_04 = "RC_04";//质押申请已签收
	public static final String RC_05 = "RC_05";//入池已记账
	public static final String RC_06 = "RC_06";//入池校验不通过不可入池
	public static final String RC_07 = "RC_07";//入池发生异常
	
	/***pool_in表状态字段 plStatus（入池状态）-end******/
	
	/***pool_out表状态字段 plStatus（出池状态）-start***/
	public static final String CC_00 = "CC_00";//新建数据
	public static final String CC_01 = "CC_01";//出池已记账
	public static final String CC_02 = "CC_02";//已发解质押申请
	public static final String CC_03 = "CC_03";//解质押申请待签收
	public static final String CC_04 = "CC_04";//已发解质押签收申请
	public static final String CC_05 = "CC_05";//解质押申请已签收
	public static final String CC_05_1= "CC_05_1";//解质押申请签收失败
	public static final String CC_05_2= "CC_05_2";//解质押到期异常
	public static final String CC_06 = "CC_06";//出池记账撤回
	
	/***pool_out表状态字段 plStatus（出池状态）-****end*****/
	
	/***CD_EDRAFT表状态字段 SDealStatus（票据状态）-start***/
	/* 未处理 */
	public static String DS_00 = "DS_00";//未处理
	/* 入池处理中*/
	public static String DS_01 = "DS_01";//入池处理中
	public static String DS_02 = "DS_02";//已入池
	public static String DS_03 = "DS_03";//出池处理中
	public static String DS_04= "DS_04";//已出池
	public static String DS_05="DS_05";//签收处理中    ：质押解质押签收
	public static String DS_06="DS_06";//到期处理中
	public static String DS_07="DS_07";//托收已签收
	public static String DS_08="DS_08";//已拒绝
	public static String DS_09="DS_09";//已驳回
	public static String DS_99="DS_99";//不可入池票据：原属于票据池客户的持有票，后续查回来之后不再属于该客户，且不属于票据池其他客户（比如：）
	public static String DS_10="DS_10";//贴现处理中
	public static String DS_11="DS_11";//贴现已完成
	public static String DS_12="DS_12";//做过拆分操作的失效数据
	/***CD_EDRAFT表状态字段 SDealStatus（票据状态）-end***/

	/***pl_discount表状态字段 SBillStatus（贴现状态）-start***/
	
	public static final String TX_00 = "TX_00";//:新建数据
	public static final String TX_01 = "TX_01";//:已发贴现申请
	public static final String TX_02 = "TX_02";//贴现申请待签收
	public static final String TX_03 = "TX_03";//可发签收记账
	public static final String TX_04 = "TX_04";//已发贴现签收记账
	public static final String TX_05 = "TX_05";//贴现记账
	public static final String TX_06 = "TX_06";//贴现失败
	
	
	/***pl_discount表状态字段 SBillStatus（贴现状态）-end******/
	
	/***pl_discount表状态字段 reTranstatus -start***/
	public static final String DISCOUNT_03 = "3";//强贴申请失败
	public static final String DISCOUNT_00 = "0";//强贴签收未记账
	public static final String DISCOUNT_01 = "1";//强贴签收记账成功
	public static final String DISCOUNT_02 = "2";//强贴签收记账失败
	/***pl_discount表状态字段 reTranstatus -end***/
	
	
	/*票据池协议业务类型*/
	public static String BT_01="BT_01";//开通
	public static String BT_02="BT_02";//解约
	public static String BT_03="BT_03";//续约
	public static String BT_04="BT_04";//暂停
	public static String BT_05="BT_05";//重启
	public static String BT_06="BT_06";//冻结
	public static String BT_07="BT_07";//解冻
	public static String BT_08="BT_08";//无业务状态
	public static String BT_09="BT_09";//解冻审批通过
	public static String BT_10="BT_10";//冻结审批通过
	public static String BT_11="BT_11";//冻结审批不通过
	public static String BT_12="BT_12";//解冻审批不通过
	/**
	 * 托收业务
	 */
	/******CollectionSendDto对象 SDealStatus(明细状态)***************/

	
	public static final String TS00 = "TS_00";//* 已发提示付款申请 */
	/* 待提示付款 */
	public static final String TS01 = "TS_01";//提示付款申请失败（发送BBS失败）
	/* 提示付款待签收 */
	public static final String TS02 = "TS_02";//提示付款申请已拒绝
	public static final String TS03 = "TS_03";//* 提示付款签收完毕 */
	public static final String TS04 = "TS_04";//* 提示付款撤回 */
	public static final String TS05 = "TS_05";//* 记账完毕 */
	public static final String TS06 = "TS_06";//* 记账失败 */
	
	 /*--------------------------黑名单灰名单----------------start-----------*/
//	public static final String BLACKLIST = "01"; // 黑名单
//	public static final String GRAYLIST = "02"; // 灰名单
	 /*--------------------------黑名单灰名单----------------end-----------*/
	
	 /*--------------------------黑名单灰名单关键字----------------start-----------*/
	 public static final String KEY_WAYS_01 = "01";  //开票人
	 public static final String KEY_WAYS_02 = "02";  //承兑人
	 public static final String KEY_WAYS_03 = "03";  //承兑行
	 public static final String KEY_WAYS_04 = "04";  //承兑行所在地区	
	 public static final String KEY_WAYS_05 = "05";  //票号
	 public static final String KEY_WAYS_06 = "06";  //背书人
	 public static final String KEY_WAYS_07 = "07";  //票据流转次数
	 public static final String KEY_WAYS_08 = "08";  //剩余期限
	 
	 /*--------------------------黑名单灰名单关键字----------------end-----------*/
	 
	 /*--------------------------黑名单灰名单方式----------------start-----------*/
	 public static final String way_01 = "1";  //精准
	 public static final String way_02 = "2";  //模糊
	 /*--------------------------黑名单灰名单方式----------------end-----------*/ 
	 
	 /*-------------------------------YECHENG以下从PublicStaticDefineTab转移过来的变量-----------------------------------------------*/
	 /** 处理状态 * */
	/* 处理中 */
	public static String DS_001 = "DS_001";
	/* 未处理 */
	public static String DS_002 = "DS_002";

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
	
	/** OVER_FLAG_WDQ 票据逾期标识--未到期*/
	public static final String CP_QRY_TYP_WDQ = "00";//票据逾期标识--未到期
	/**
	 *票据逾期标识--到期未逾期 
	 * 提示付款 01
	 */
	public static final String CP_QRY_TYP_CLEW_PYMT = "01";
	/* 逾期提示付款 02 */
	public static final String CP_QRY_TYP_OVDU_CLEW_PYMT = "02";//票据逾期标识--已逾期
	
	/**---------------票据出入库标示---------------*/
	public static final int BILL_INITIAL = -1;//初始值
	public static final int BILL_IN=0;//入库
	public static final int BILL_OUT=1;//出库
	
	/* 不得转让标记 */

	// 可再转让
	public static String NOT_ATTRON_FLAG_NO = "0";
	// 不得转让
	public static String NOT_ATTRON_FLAG_YES = "1";
	
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
	
	/**黑名单标识*/
	 public static final String WHITE = "00";//不在黑名单中
	 public static final String GRAY = "02";//在灰名单中
	 public static final String BLACK = "01";//在黑名单中
	 
	 /**业务撤回类型*/
	 public static final String BACK_01="01";//质押申请撤回
	 public static final String BACK_02="02";// 票据信息登记申请撤回
	 public static final String BACK_03="03";//黑白名单登记申请撤回
	 public static final String BACK_04="04";//保证金划转申请撤回
	 
	 /**保证金划转用途*/
	 public static final String USAGE_1="1";//保证金支出
	 public static final String USAGE_2="2";//保证金存入
	 public static final String USAGE_3="3";//其他
	 
	 /**保证金划转交易状态*/
	 public static final String MARGIN_TRANSFER_STATUS_1="01";//保证金预划转
	 public static final String MARGIN_TRANSFER_STATUS_2="02";//保证金划转审核通过
	 public static final String MARGIN_TRANSFER_STATUS_3="03";//保证金划转审核不通过
	 public static final String MARGIN_TRANSFER_STATUS_4="04";//保证金划转已划转
	 
	 /**额度冻结状态*/
	 public static final String FROZEN_STATUS_00="DJ_00";//额度正常状态
	 public static final String FROZEN_STATUS_01="DJ_01";//保证金额度冻结
	 public static final String FROZEN_STATUS_02="DJ_02";//票据额度冻结
	 public static final String FROZEN_STATUS_03="DJ_03";//全冻结
//	 public static final String FROZEN_STATUS_04="DJ_04";//票据冻结--保证金未冻结的部分冻结状态
//	 public static final String FROZEN_STATUS_05="DJ_05";//保证金冻结--票据未冻结的部分冻结状态
	 /**额度解冻状态*/
	 public static final String FROZEN_STATUS_OPEN_00="JD_00";//额度正常状态
	 public static final String FROZEN_STATUS_OPEN_01="JD_01";//保证金额度解冻
	 public static final String FROZEN_STATUS_OPEN_02="JD_02";//票据额度解冻
	 public static final String FROZEN_STATUS_OPEN_03="JD_03";//全解冻
	 
	 /**虚拟票据池状态*/
	 public static final String  VS_01 = "VS_01";//虚拟票据池签约  
	 public static final String  VS_02 = "VS_02";//虚拟票据池解约  
	 
	 /**签约类型  liuxiaodong add*/
	 public static final String  QYLX_01 = "QYLX_01";//基础签约  
	 public static final String  QYLX_02 = "QYLX_02";//融资签约
	 public static final String  QYLX_03 = "QYLX_03";//基础签约+融资签约 
	 
	 /**解约类型  liuxiaodong add*/
	 public static final String  JYLX_01 = "JYLX_01";//融资功能解约  
	 public static final String  JYLX_02 = "JYLX_02";//全解约
	
	 /**账务管家资产类型*/
	 public static final String  SF_01 = "SF_01";//应收票据
	 public static final String SF_02 = "SF_02";//应付票据
	 
	 /**是、否通用码值*/
	 public static final String  NO = "0";//否
	 public static final String  YES = "1";//是  
	 
	 /**融资业务签约标识*/
	 public static final String OPEN_00="00";//未开通
	 public static final String OPEN_01="01";//已签约
	 public static final String OPEN_02="02";//已解约
	 
	 /**融资业务开通审批标识*/
	 public static final String APPROVE_00="00";//初始化
	 public static final String APPROVE_01="01";//签约审批中
	 public static final String APPROVE_02="02";//签约审批拒绝
	 public static final String APPROVE_03="03";//签约审批通过
	 public static final String APPROVE_04="04";//解约审批中
	 public static final String APPROVE_05="05";//解约审批拒绝
	 public static final String APPROVE_06="06";//解约审批通过
	 
	 /**票据账务管家-流转阶段*/
	 public static final String JD_01= "JD_01";//客户持有票据
	 public static final String JD_02= "JD_02";//已质押票据
	 public static final String JD_03= "JD_03";//虚拟票据池录入应收票据
	 public static final String JD_04= "JD_04";//BBSP系统提示承兑已签收票据
	 public static final String JD_05= "JD_05";//MIS系统签发出账票据
	 public static final String JD_06= "JD_06";//虚拟票据池录入应付票据
	 
	 /**账户类型*/
	 public static final String ZH_01 ="ZH_01";//票据池保证金账户
	 public static final String ZH_02 ="ZH_02";//票据池结算账户
	 public static final String ZH_03 ="ZH_03";//电票签约账户
	 
	 /**柜面保证金划转校验结果码值*/
	 public static final String CHECK_FLAG_00 ="00";//保证金足额
	 public static final String CHECK_FLAG_01 ="01";//保证金不足
	 public static final String CHECK_FLAG_02 ="02";//该借据非票据池担保
	 public static final String CHECK_FLAG_03 ="03";//保证金账户与核心客户号不匹配
	 public static final String CHECK_FLAG_04 ="04";//保证金足额,但是用保证金还借据后后续有业务到期时候会额度不足
	 
	 /**票据权益*/
	 public static final String QY_01 ="QY_01";//持有票据:
	 public static final String QY_02 ="QY_02";//应付票据:
	 
	 /**借据处理状态*/
	 public static final String LOAN_1="1";//未结束借据
	 public static final String LOAN_0="0";//已结束借据
	 
	 /**信贷业务交易类型*/
	 public static final String XD_01="XD_01";//银承
	 public static final String XD_02="XD_02";//流贷
	 public static final String XD_03="XD_03";//保函
	 public static final String XD_04="XD_04";//信用证
	 public static final String XD_05="XD_05";//表外业务垫款
	 public static final String XD_06="XD_06";//全部信贷业务交易类型
	 public static final String XD_07="XD_07";//贴现
	 
	 /**借据交易装填*/
	 public static final String JJ_01="JJ_01";//已放款
	 public static final String JJ_02="JJ_02";//部分还款
	 public static final String JJ_03="JJ_03";//逾期/垫款
	 public static final String JJ_04="JJ_04";//结清
	 public static final String JJ_05="JJ_05";//未用退回

	 
	 /**保贴额度校验返回码值**/
	 public static final String BT_1="1";//无对应买方客户或无对应有效买方商票保贴授信额度
	 public static final String BT_2="2";//存在有效买方商票保贴授信额度
	 public static final String BT_3="3";//额度不足
	 public static final String BT_4="4";//额度充足占用成功
	 public static final String BT_5="5";//释放成功
	 
	 
	 /**票据池对账码值**/
	 public static final String DZJG_00 = "DZJG_00"; //未对账
	 public static final String DZJG_01 = "DZJG_01"; //对账一致
	 public static final String DZJG_02 = "DZJG_02"; //对账不一致
	 
	 /**集团子户签约状态  PedProtocolList的status字段码值  00：未签约  01：已签约    02：已解约*/
	 public static final String PRO_LISE_STA_00 = "00"; //未签约
	 public static final String PRO_LISE_STA_01 = "01"; //已签约
	 
	 public static final String PRO_LISE_STA_02 = "02"; //已解约
	 
	 /**客户类型*/
	 public static final String JS_00 = "00"; //单户票据池签约户
	 public static final String JS_01 = "01"; //主户
	 public static final String JS_02 = "02"; //分户
	 
	 public static final String KHLX_01 = "KHLX_01"; //出质人
	 public static final String KHLX_02 = "KHLX_02"; //融资人
	 public static final String KHLX_03 = "KHLX_03"; //出质人加融资人
	 public static final String KHLX_04 = "KHLX_04"; //签约成员
	 public static final String KHLX_05 = "KHLX_05"; //融资人解约
	 public static final String KHLX_06 = "KHLX_06"; //签约成员解约

	 /**网银加锁解锁标识*/
	 public static final String EBKLOCK_02 = "2";//未锁票
	 public static final String EBKLOCK_01 = "1";//锁票
	 
	 /**bbsp加锁解锁标志*/
	 public static final String BBSPLOCK_01 = "01";//加锁
	 public static final String BBSPLOCK_02 = "02";//解锁
	 
	 /**信贷系统主业务合同业务额度释放码值*/
	 public static final String EDSF_01 ="01";//结清释放
	 public static final String EDSF_02 ="02";//未用释放
	 
	 
	 /**自动入池标识*/
	 public static final String ZY_FLAG_00 ="00";//非自动
	 public static final String ZY_FLAG_01 ="01";//自动
	 
	 public static final String OUT_01 ="1";//批次未出池
	 public static final String OUT_00 ="0";//批次已出池

	/**持有人*/
	public static final String KHCY_00 ="00";//客户持有
	public static final String WHCY_00 ="01";//我行持有
	public static final String THCY_00 ="02";//他行持有

	/**收费来源 */
	public static final String SFLY_00 ="00";//网银收费
	public static final String SFLY_01="01";//柜面收费
	/** 
	  * 缴费模式01:年费 02:逐笔
	  */
	public static final String SFMS_01 ="01";//年费
	public static final String SFMS_02 ="02";//逐笔
	
	/**
	 * 集团成员表融资人生效标识
	 */
	public static final String SXBZ_00 ="SF_00";//失效
	public static final String SXBZ_01 ="SF_01";//生效
	
	/**
	 * 额度系统额度占用状态
	 */
	public static final String SP_00 ="0";//释放
	public static final String SP_01 ="1";//占用

	public static final String DO_00 ="0";//未处理
	public static final String DO_01 ="1";//已处理
	
	
	/**
	 * 账务管家相关码值
	 * dataSource 数据来源: SRC_01:BBSP   SRC_04:票据池虚拟录入   SRC_02:MIS系统 
	 */
	public static final String SRC_01 ="SRC_01";//BBSP
	public static final String SRC_02 ="SRC_02";//MIS
	public static final String SRC_04 ="SRC_04";//虚拟票据池
	
	/**
	 * 收费标准表的收费产品类型码值
	 */
	public static final String FEE_01 ="FEE_01";//票据池服务费
	
	
	/**
	 * 角色代码
	 */
	public static final String roleCode1 = "A00000";//超级管理员
	public static final String roleCode2 = "A00001";//管理员    
	public static final String roleCode3 = "A00002";//查询员    
	public static final String roleCode4 = "A00003";//审批员    
	public static final String roleCode5 = "B00001";//行长      
	public static final String roleCode6 = "C00001";//客户经理  
	public static final String roleCode7 = "D00001";//综合柜员  
	public static final String roleCode8 = "D00002";//结算授权员
	
	/**
	 * 贴现标识
	 */
	public static final String TX_FLAG_1 ="1";//强贴
	public static final String TX_FLAG_0 ="0";//贴现完成
	
	/**
	 * 信贷系统传入结清标志字段码值
	 */
	public static final String JQ_00 = "JQ_00";//码值未启用
	public static final String JQ_01 = "JQ_01";//码值未启用
	public static final String JQ_02 = "JQ_02";//手工提前终止出账
	public static final String JQ_03 = "JQ_03";//合同到期
	public static final String JQ_04 = "JQ_04";//合同终止（不可再生效的合同失效）
	public static final String JQ_05 = "JQ_05";//合同失效（可再生效的合同失效）

	/**
	 * 证件类型
	 */
	public static final String CRT_01 = "01";//01组织机构代码
	public static final String CRT_02 = "02";//02统一授信编码
	public static final String CRT_03 = "03";//03客户号

	/**
	 * 业务类型
	 */
	public static final String BUSI_TYPE_01 ="01";//票据池签约客户数             
	public static final String BUSI_TYPE_02 ="02";//票据池总签约客户数           
	public static final String BUSI_TYPE_03 ="03";//票据池线下银承发生金额       
	public static final String BUSI_TYPE_04 ="04";//票据池线下流贷发生金额       
	public static final String BUSI_TYPE_05 ="05";//票据池线下保函发生金额       
	public static final String BUSI_TYPE_06 ="06";//票据池线下信用证发生金额     
	public static final String BUSI_TYPE_07 ="07";//票据池线上银承发生金额       
	public static final String BUSI_TYPE_08 ="08";//票据池线上流贷发生金额       
	public static final String BUSI_TYPE_09 ="09";//票据池线下融资金额           
	public static final String BUSI_TYPE_10 ="10";//票据池线上融资金额           

    /**
     * 融资用信业务类型
     */
    public static final String VT_0 = "0";// 0：主业务合同
    public static final String VT_1 = "1";//1：借据号

	/**
	 * 额度是否充足
	 */
	public static final String IS_ADEQUATE_0 = "0";// 0：不足
	public static final String IS_ADEQUATE_1 = "1";//1：充足

	/**
	 * 时间选择模式
	 */
 	public static final String TIME_MODEL_01 = "01";// 模式1
	public static final String TIME_MODEL_02 = "02";// 模式2
	public static final String TIME_MODEL_03 = "03";// 模式3
	public static final String TIME_MODEL_04 = "04";// 模式4
	public static final String TIME_MODEL_05 = "05";// 模式5
	public static final String TIME_MODEL_06 = "06";// 模式6
	
	public static final String LOG_TYPE_0 = "0";//提示性错误-对于网银只提示，不禁止的日志 
	public static final String LOG_TYPE_1 = "1";//禁止性错误-对于网银禁止性日志 
	
	public static final String PAY_TYPE_0 ="0";//支付计划支付类型-付款交易
	public static final String PAY_TYPE_1 ="1";//支付计划支付类型-还款交易
	public static final String PAY_TYPE_2 ="2";//支付计划支付类型-支付计划修改
	public static final String PAY_TYPE_3 ="3";//支付计划支付类型-贷款未用归还
	
	       
	 public static final String PAY_STATUS_00="00";//00-初始化
	 public static final String PAY_STATUS_01="01";//01-支付成功
	 public static final String PAY_STATUS_02="02";//02-支付失败
	 
	 
	 /**
	  * PedBailTrans表planStatus;//支付状态  0-未处理/1-成功/2-失败/3-作废（超过一个自然日（24小时）未处理的）  
	  */
	 public static final String BAIL_TRANS_0 ="0";//未处理
	 public static final String BAIL_TRANS_1 ="1";//支付成功
	 public static final String BAIL_TRANS_2 ="2";//支付失败
	 public static final String BAIL_TRANS_3 ="3";//3-作废（超过一个自然日（24小时）未处理的）

	 /*
	  * 额度主体类型  1-同业额度  2-对公额度
	  */
	 public static final String CREDIT_OBJ_TYPE_1 = "1";//同业额度
	 public static final String CREDIT_OBJ_TYPE_2 = "2";//对公额度
	 
	 
	 /**
	  * 交易编号
	  */
	public static final String CBS_0022001 = "CBS.002.20.01P";//--可发起申请汇总查询
	public static final String CBS_0022010 = "CBS.002.20.10P";//--追索通知汇总查询
	public static final String CBS_0022011 = "CBS.002.20.11P";//--同意清偿申请汇总查询
	public static final String CBS_0022012 = "CBS.002.20.12P";//--提示付款申请汇总查询
	public static final String CBS_0022019 = "CBS.002.20.19P";//--追索申请撤销汇总
	public static final String CBS_0022020 = "CBS.002.20.20P";//--同意清偿申请撤销汇总
	public static final String CBS_0022007 = "CBS.002.20.07P";//--持有票据汇总查询
	public static final String NES_0012000 = "NES.001.20.00P";//--出票登记
	public static final String NES_0022000 = "NES.002.20.00P";//--提示承兑
	public static final String NES_0032000 = "NES.003.20.00P";//--提示收票
	public static final String NES_0142000 = "NES.014.20.00P";//--撤票
	public static final String NES_0062000 = "NES.006.20.00P";//--企业背书
	public static final String NES_0072000 = "NES.007.20.00P";//--买断式贴现
	public static final String NES_0072010 = "NES.007.20.10P";//--回购式贴现
	public static final String NES_0072002 = "NES.007.20.02P";//-极速（自助）贴现
	public static final String NES_0042000 = "NES.004.20.00P";//--出票保证
	public static final String NES_0042010 = "NES.004.20.10P";//--承兑保证
	public static final String NES_0042020 = "NES.004.20.20P";//--背书保证
	public static final String NES_0092000 = "NES.009.20.00P";//--质押
	public static final String NES_0102000 = "NES.010.20.00P";//--质押解除
	public static final String NES_0092010 = "NES.009.20.10P";//--入池质押
	public static final String NES_0102010 = "NES.010.20.10P";//--出池解质押
	public static final String NES_0102011 = "NES.010.20.11P";//--电票出池预申请
	public static final String NES_0152000 = "NES.015.20.00P";//--不得转让撤销
	public static final String NES_0112000 = "NES.011.20.00P";//--提前提示付款
	public static final String NES_0112001 = "NES.011.20.01P";//--到期提示付款
	public static final String NES_0112003 = "NES.011.20.03P";//--逾期提示付款
	public static final String NES_0122010 = "NES.012.20.10P";//--拒付追索同意清偿申请(CPES)
	public static final String NES_0122011 = "NES.012.20.11P";//--非拒付追索同意清偿(CPES)
	public static final String NES_0122030 = "NES.012.20.30P";//--拒付追索同意清偿申请(ECDS)
	public static final String NES_0122031 = "NES.012.20.31";//--非拒付追索同意清偿(ECDS)
	public static final String NES_0122000 = "NES.012.20.00P";//--拒付追索通知
	public static final String NES_0122001 = "NES.012.20.01P";//--非拒付追索通知
	
	
	/**
	 * 票据来源
	 */
	public static final String CS01 = "CS01";//--ECDS
	public static final String CS02 = "CS02";//--金融机构
	public static final String CS03 = "CS03";//--供应链平台
	

}