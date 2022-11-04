
package com.mingtech.application.utils;

/**
 * <p> Description:自动任务编号常量定义</p>
 *
 * @author h2 20210418
 */
public class AutoTaskNoDefine {

	//工具类必须要定义一个私有的构造器-弱扫
	private AutoTaskNoDefine(){}
	/** 队列名称 **/
	public static final String  POOL_AUTO_POOLIN="POOL.AUTO.POOLIN"; //入池队列名称
	public static final String  POOL_AUTO_POOLOUT="POOL.AUTO.POOLOUT"; //出池队列名称
	public static final String  POOL_AUTO_POOLDIS="POOL.AUTO.POOLDIS"; //强贴队列名称
	public static final String  POOL_AUTO_MSS="POOL.AUTO.MSS"; //短信队列名称
	public static final String  POOL_ONLINE_ACPT_ADD="POOL.ONLINE.ACPT.ADD"; //银承批量新增队列名
	public static final String  POOL_ONLINE_ACPT="POOL.ONLINE.ACPT"; //银承出票队列名称
	public static final String  POOL_ONLINE_ACPT_RECEIVE="POOL.ONLINE.ACPT.RCV"; //银承提示收票队列名称
	public static final String  POOL_ONLINE_ACPT_CANCLE="POOL.ONLINE.ACPT.CANCLE"; //银承撤票队列名称
	public static final String  POOL_ONLINE_ACPT_UNUSED="POOL.ONLINE.ACPT.UNUSED"; //银承未用退回队列名称
	public static final String  POOL_ONLINE_RELEASE="POOL.ONLINE.RELEASE"; //额度释放队列名称
	public static final String  POOL_ONLINE_CRDT="POOL.ONLINE.CRDT"; //流贷队列名称
	public static final String  POOL_AUTO_CALCU="POOL.AUTO.CALCU"; //额度计算队列名称
	public static final String  POOL_AUTO_UPDATE="POOL.AUTO.UPDATE"; //银承业务明细状态及发生未用退回时金额统计队列名称
	/** 队列节点 **/
	public static final String TEST_TASK_NO="TEST_TASK_NO";//测试主任务节点
	public static final String POOLIN_TASK_NO="POOLIN_TASK_NO";//入池申请主任务节点
	public static final String POOLIN_SIGN_TASK_NO="POOLIN_SIGN_TASK_NO";//入池签收子任务节点            
	public static final String POOLIN_ACC_TASK_NO="POOLIN_ACC_TASK_NO"; //入池记账子任务节点      
	public static final String POOLIN_EDU_TASK_NO="POOLIN_EDU_TASK_NO"; //入池额度子任务节点      
	public static final String POOLOUT_EDU_TASK_NO="POOLOUT_EDU_TASK_NO";//出池额度释放主任务节点
	public static final String POOLOUT_ACC_TASK_NO="POOLOUT_ACC_TASK_NO";//出池记账子任务节点             
	public static final String POOLOUT_SEND_TASK_NO="POOLOUT_SEND_TASK_NO"; //出池申请子任务节点      
	public static final String POOLOUT_SIGN_TASK_NO="POOLOUT_SIGN_TASK_NO"; //出池签收子任务节点   
	public static final String POOLDIS_EDU_TASK_NO="POOLDIS_EDU_TASK_NO";//强贴额度校验主任务节点
	public static final String POOLDIS_SEND_TASK_NO="POOLDIS_SEND_TASK_NO";//强贴申请子任务节点
	public static final String POOLDIS_SIGN_TASK_NO="POOLDIS_SIGN_TASK_NO";//强贴签收记账子任务节点
	public static final String POOL_MSS_TASK_NO="POOL_MSS_TASK_NO";//短信任务节点
	public static final String  POOL_ONLINE_ADD="POOL_ONLINE_ADD"; //银承批量新增主任务节点
	public static final String  POOL_ONLINE_REGISTER_NO="POOL_ONLINE_REGISTER_NO"; //银承登记主任务节点
	public static final String  POOL_ONLINE_ACPT_NO="POOL_ONLINE_ACPT_NO"; //银承承兑子任务节点
	public static final String  POOL_ONLINE_SIGN_NO="POOL_ONLINE_SIGN_NO"; //银承签收子任务节点
	public static final String  POOL_ONLINE_SEND_NO="POOL_ONLINE_SEND_NO"; //银承提示收票主任务节点
	public static final String  POOL_ONLINE_CANCLE_NO_01="POOL_ONLINE_CANCLE_NO_01"; //银承撤票撤销主任务节点
	public static final String  POOL_ONLINE_CANCLE_NO_02="POOL_ONLINE_CANCLE_NO_02"; //银承撤票未用退回子任务节点
	public static final String  POOL_ONLINE_CANCLE_NO_03="POOL_ONLINE_CANCLE_NO_03"; //银承撤票额度释放子任务节点
	public static final String  POOL_ONLINE_UNUSED_NO_01="POOL_ONLINE_UNUSED_NO_01"; //银承未用退回主任务节点
	public static final String  POOL_ONLINE_UNUSED_NO_02="POOL_ONLINE_RELEASE_NO_02"; //银承未用退回额度释放子任务节点
	public static final String  POOL_ONLINE_RELEASE_NO="POOL_ONLINE_RELEASE_NO"; //额度释放主任务节点
	public static final String  POOL_ONLINE_CRDT_NO="POOL_ONLINE_CRDT_NO"; //流贷主任务节点
	public static final String  POOL_ONLINE_PAY_NO="POOL_ONLINE_PAY_NO"; //在线流贷受托支付支付节点
	public static final String  POOL_ONLINE_REPAY_NO="POOL_ONLINE_REPAY_NO"; //在线流贷受托支付支付贷款归还/提前还款任务
	public static final String  POOL_AUTO_CALCU_NO="POOL_AUTO_CALCU_NO"; //票据池池额度更新任务
	public static final String  POOL_AUTO_UPDATE_NO="POOL_AUTO_UPDATE_NO"; //银承业务明细状态及发生未用退回时金额统计任务
	
	public static final String  POOL_AUTO_BANKE_NO="POOL_AUTO_BANK_NO"; //解质押后续流程驱动任务
	
	/** 队列产品ID **/
	public static final String BUSI_TYPE_ZY="01";//质押申请
	public static final String BUSI_TYPE_QS="02";//质押签收
	public static final String BUSI_TYPE_ED="03";//质押额度占用
	public static final String BUSI_TYPE_JZ="04";//质押记账
	public static final String BUSI_TYPE_JZY="05";//解质押额度释放
	public static final String BUSI_TYPE_JJZ="06";//解质押记账
	public static final String BUSI_TYPE_JSQ="07";//解质押申请
	public static final String BUSI_TYPE_JQS="08";//解质押签收申请
	public static final String BUSI_TYPE_TED="09";//强贴额度校验
	public static final String BUSI_TYPE_TX="10";//强贴申请
	public static final String BUSI_TYPE_TJZ="11";//强贴签收记账申请
	public static final String BUSI_TYPE_MSS="12";//短信申请
	public static final String BUSI_TYPE_ONLINE_ADD="13";//银承批量新增
	public static final String BUSI_TYPE_ONLINE_REGISTER="14";//银承登记
	public static final String BUSI_TYPE_ONLINE_ACPT="15";//银承承兑
	public static final String BUSI_TYPE_ONLINE_SIGN="16";//银承签收
	public static final String BUSI_TYPE_ONLINE_SEND="17";//银承提示收票
	public static final String BUSI_TYPE_ONLINE_CANCLE_01="18";//银承撤票撤销
	public static final String BUSI_TYPE_ONLINE_CANCLE_02="19";//银承撤票未用退回
	public static final String BUSI_TYPE_ONLINE_CANCLE_03="20";//银承撤票额度释放
	public static final String BUSI_TYPE_ONLINE_UNUSED_01="21";//银承未用退回
	public static final String BUSI_TYPE_ONLINE_UNUSED_02="22";//银承未用退回额度释放
	public static final String BUSI_TYPE_ONLINE_RELEASE="23";//额度释放
	public static final String BUSI_TYPE_ONLINE_CRDT="24";//流贷
	public static final String BUSI_TYPE_ONLINE_PAY="25";//支付
	public static final String BUSI_TYPE_ONLINE_REPAY="26";//在线流贷受托支付支付贷款归还/提前还款任务
	public static final String BUSI_TYPE_CAL="27";//票据池池额度更新任务
	public static final String BUSI_TYPE_UPDATE="28";//银承业务明细状态及发生未用退回时金额统计任务
	
	public static final String BUSI_TYPE_AUTOBANK="33";//解质押后续流程驱动任务
}

