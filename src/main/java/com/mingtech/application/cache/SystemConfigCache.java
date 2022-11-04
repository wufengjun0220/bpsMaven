package com.mingtech.application.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.runmanage.domain.SystemConfig;
import com.mingtech.application.runmanage.service.SystemConfigService;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Jun 15, 2009 10:03:35 AM
 * @描述: [SystemConfigCache]系统参数配置Cache
 */
public class SystemConfigCache extends GenericServiceImpl implements CacheService{
	private static final Logger logger = Logger.getLogger(SystemConfigCache.class);
	private static boolean startRedis=false;//启用redis
	
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private RedisUtils redisrCache;
	
	private static final Map systemConfigMap = Collections
			.synchronizedMap(new HashMap());//系统参数配置缓存
	/**票据平台文件存放路径**/
	public static final String DOCUMENT_ROOT_PATH = "DOCUMENT_ROOT_PATH";//文件存放根目录
	public static final String DOCUMENT_NOTES_PATH = "DOCUMENT_NOTES_PATH";//公告存放相对路径
	public static final String DOCUMENT_PJS_IMAGE_PATH = "DOCUMENT_PJS_IMAGE_PATH";//影像保存路径
	public static final String DOCUMENT_PJS_IMAGE_SERVER = "DOCUMENT_PJS_IMAGE_SERVER";//影像服务器地址
	public static final String DOCUMENT_PJS_DISCOUNTFILE_PATH = "DOCUMENT_PJS_DISCOUNTFILE_PATH";//贴现通附件保存路径
	public static final String DOCUMENT_Contract_PATH = "DOCUMENT_Contract_PATH";//电子合同保存路径
	public static final String DOCUMENT_PJS_DOWNLOAD_PATH = "DOCUMENT_PJS_DOWNLOAD_PATH";//票交所日终下载文件
    public static final String DOCUMENT_REPORT_TEMPLATE_PATH = "DOCUMENT_REPORT_TEMPLATE_PATH";//报表模板存放路径
    public static final String DOCUMENT_REPORT_FILE_PATH = "DOCUMENT_REPORT_FILE_PATH";//报表生成文件存放路径
    public static final String PJS_CCM008_FILE_PATH = "PJS_CCM008_FILE_PATH";//自由格式报文附件路径
    
	/**系统管理参数**/
	public static final String SYSTEM_MANAGE_INIT_USER_PWD = "SYSTEM_MANAGE_INIT_USER_PWD";//系统管理用户初始化密码
	public static final String SYSTEM_MANAGE_JOIN_PJS = "SYSTEM_MANAGE_JOIN_PJS";//系统已加入票交所直连
	public static final String SYSTEM_MANAGE_NOT_JOIN_PJS_BANKNUM = "SYSTEM_MANAGE_NOT_JOIN_PJS_BANKNUM";//未直连的大额行号
	/**系统运行管理参数**/
	public static final String ACCTRECORD_FILE_PATH = "ACCTRECORD_FILE_PATH";//会计分录快速初始化配置文件路径
	
	/**操作说明文件存放路径**/
	public static final String DOCUMENT_OPERINSTRUCTION_PATH = "DOCUMENT_OPERINSTRUCTION_PATH";//操作说明存放路径
	
	/**是否按照机构进行资源分配(1是、0否)**/
	public static final String DEPARTMENT_RESOURCE_ASSIGN = "DEPARTMENT_RESOURCE_ASSIGN";
	
	/**承兑手续费费率**/
	public static final String ACPT_CHARGE_RATE = "ACPT_CHARGE_RATE";
	
	/**数据库类型(oracle、db2、mysql、sqlserver)**/
	public static final String DATABASE_TYPE="DATABASE_TYPE";
	/**数据库类型(oracle、db2、mysql、sqlserver)**/
	public static final String OPEN_UPDATA_TABLE_NAME="OPEN_UPDATA_TABLE_NAME";
	
	//票据系统服务总线访问地址
	public static final String DSSB_REQ_HTTP_URL = "DSSB_REQ_HTTP_URL";
	//票据系统服务总线-发送第三方系统请求报文接收服务名称
	public static final String DSSB_Forward_MicSrv_Name ="DSSB_Forward_MicSrv_Name";
	//接口对接挡板开关，取值范围：关1开、0关
	public static final String INTERFACE_BAFFLE_ON_OFF = "INTERFACE_BAFFLE_ON_OFF";
	
	/**接口对接交易码定义**/
	//电票承兑记账交易码
	public static final String ELEC_ACCEPT_ACCOUNT_TXCODE = "ELEC_ACCEPT_ACCOUNT_TXCODE";
	
	
	/**
	 * 票e通影像文件上传路径
	 */
	public static final String DOCUMENT_ACPTONLINE_IMG = "DOCUMENT_ACPTONLINE_IMG";
	
	/**
	 * 票据应用Session超时时间-单位：分钟
	 */
	public static final String SYS_SESSION_TIMEOUT = "SYS_SESSION_TIMEOUT";
	
	/**
	 * 票据应用是否已启用Redis：true是、false否
	 */
	public static final String SYS_IS_START_REDIS = "SYS_IS_START_REDIS";
	
	/**
	 * 单个账号是否同时只允许登录一个：true是、false否
	 */
	public static final String SYS_SINGLE_USE_LOGIN = "SYS_SINGLE_USE_LOGIN";
	/**
	 * 系统公共序列最大值
	 **/
	public static final String SYS_COMM_SEQNO = "SYS_COMM_SEQNO";
	/**
	 * 价税分离开关 true开、false关
	 **/
	public static final String PRICE_TAX_SWITCH ="PRICE_TAX_SWITCH";
	/**
	 * 价税分离税率(%)
	 **/
	public static final String PRICE_TAX_RATE="PRICE_TAX_RATE";
	/**
	 * 商票提示付款标识符号 0-T+N,1-T+N+1
	 */
	public static final String AC02_COLL_OVERTYPE ="AC02_COLL_OVERTYPE";
	/**
	 * 是否开启代客托收功能 true开、false关
	 */
	public static final String ELEC_VALET_COLL_RC01 ="ELEC_VALET_COLL_RC01";
	/**
	 * 密码校验规则
	 */
	public static final String PASSWORD_RESULT ="PASSWORD_RESULT";
	/**
	 * 密码校验规则描述
	 */
	public static final String  PASSWORD_RESULT_DESC  = "PASSWORD_RESULT_DESC"; 
	/**
	 * 密码剩余期限
	 */
	public static final String PASSWORD_VOLID_DAYS ="PASSWORD_VOLID_DAYS";
	/**
	 * 服务地址
	 */
	public static final String SERVER_URL ="SERVER_URL";
	/**
	 * 纸票影像信息（收到cim009）
	 */
	public static final String PAPER_BILL_IMG_PATH = "PAPER_BILL_IMG_PATH";
	
	/**
	 * 头寸账户预报开关   true开、false关
	 */
	public static final String CASH_ACCOUNT_PREDICT_SWITCH ="CASH_ACCOUNT_PREDICT_SWITCH";
	
	/**
	 * CBESlink 地址
	 */
	public static final String PJS_BASELINK_URL = "PJS_BASELINK_URL";
	/**
	 * 报文乱序轮询 延迟处理时间
	 */
	public static final String BW_DELAY_TIME = "BW_DELAY_TIME";
	/**
	 * 报文乱序轮训  处理次数
	 */
	public static final String BW_DEAL_TIMES = "BW_DEAL_TIMES";
	/**
	 * 总行集中兑付开关（取值范围：true 开、false 关）
	 */
	public static final String CENTRALIZED_CASHING_SWITCH = "CENTRALIZED_CASHING_SWITCH";
	/**
	 * 是否系统指定承兑行（取值范围：true 是、false 否）
	 */
	public static final String IF_SYS_DISTRIBUTE_ACEPTOR = "IF_SYS_DISTRIBUTE_ACEPTOR";
	/**
	 * 承兑受理行开关（OPENING开户行、RECEIVING接收行）
	 */
	public static final String ACCEPTANCE_BANK_SWITCH = "ACCEPTANCE_BANK_SWITCH";
	/**
	 * 承兑记账完成自动签收开关（取值范围：true 开、false 关）
	 */
	public static final String ACPT_AUTO_SIGN_SWITCH = "ACPT_AUTO_SIGN_SWITCH";
	/**
	 * 承兑审批通过自动记账开关（取值范围：true 开、false 关）
	 */
	public static final String ACPT_AUTO_ACCT_SWITCH = "ACPT_AUTO_ACCT_SWITCH";
	/**
	 * 普通贴现审批通过自动签收开关（取值范围：true 开、false 关）
	 */
	public static final String DISC_AUTO_SIGN_SWITCH = "DISC_AUTO_SIGN_SWITCH";
	/**
	 * 贴现签收完成自动记账开关（取值范围：true 开、false 关）
	 */
	public static final String DISC_AUTO_ACCT_SWITCH = "DISC_AUTO_ACCT_SWITCH";
	/**
	 * 转贴现业务自动记账开关（取值范围：true 开、false 关）
	 */
	public static final String REDISC_AUTO_ACCT_SWITCH = "REDISC_AUTO_ACCT_SWITCH";
	/**
	 * 转贴现审批通过自动发送成交开关（取值范围：true 开、false 关）
	 */
	public static final String REDISC_AUTO_SIGN_SWITCH = "REDISC_AUTO_SIGN_SWITCH";
	/**
	 * 是否自动占额度
	 */
	public static final String SYS_IS_AUTO_OCC ="SYS_IS_AUTO_OCC";
	/**
	 * 系统请求网关地址
	 */
	public static final String GATEWAY_REQ_HTTP_URL = "GATEWAY_REQ_HTTP_URL";
	/**
	 *审批中业务是否允许申请人直接撤回开关（取值范围：true 开、false 关）
	 */
	public static final String AUDIT_WITHDRAWAL_APPLY="AUDIT_WITHDRAWAL_APPLY";
	/**
	 * 审批节点默认最大节点数
	 */
	public static final String AUDIT_NODE_NUM = "AUDIT_NODE_NUM";
	/**
	 * 预审批金额校验开关
	 */
	public static final String ADVANCE_APPROVE_QUOTE_SWITCH ="ADVANCE_APPROVE_QUOTE_SWITCH";
	/**
	 * 买入预审批报价单是否可以为空
	 */
	public static final String ADVANCE_APPROVE_QUOTE_IS_NULL ="ADVANCE_APPROVE_QUOTE_IS_NULL";
	/**
	 * 机构产品准入模板开关 （取值范围：true 开、false 关）
	 */
	public static final String DEPT_PRODUCT_SWITCH ="DEPT_PRODUCT_SWITCH";
	
	
	public static final String  OL_OPEN_PJC    = "OL_OPEN_PJC";   //在线业务总开关 
	public static final String  OL_OPEN_YC     = "OL_OPEN_YC";    //在线银承开关  
	public static final String  OL_OPEN_LD     = "OL_OPEN_LD";    //在线流贷开关  
	public static final String  OL_OPENTIME_YC = "OL_OPENTIME_YC";//在线银承开始时间
	public static final String  OL_ENDTIME_YC  = "OL_ENDTIME_YC"; //在线银承结束时间
	public static final String  OL_OPENTIME_LD = "OL_OPENTIME_LD";//在线流贷开始时间
	public static final String  OL_ENDTIME_LD  = "OL_ENDTIME_LD"; //在线流贷结束时间
	
	public static final String  OL_LIMIT_TIME  = "OL_LIMIT_TIME"; //在线业务异常轮训处理时间 2小时
	public static final String  OL_LIMIT_ALLTIME  = "OL_LIMIT_ALLTIME"; //在线业务异常轮训处理时间 12小时
	public static final String  OL_LIMIT_MIN_TIME  = "OL_LIMIT_MIN_TIME"; //轮训处理五分钟之前数据
	
	public static final String  NEW_RISK_CHECK  = "NEW_RISK_CHECK"; //财票改造开关 0-关 1-开
	
	
	
	
	
	/**
	 * 初始化缓存
	 * @return void
	 */
	public void initCache(){
		if(startRedis == true){
			logger.info("-从Redis中获取系统配置缓存.............");
			Map tmpProductTypeMap = (Map) redisrCache.hmget("systemConfigMap");
			if(tmpProductTypeMap != null && !tmpProductTypeMap.isEmpty()){
				return;
			}
			logger.info("从Redis中未获取到系统配置缓存,从DB中重新加载.............");
		}else{
			logger.info("-开始从db加载系统配置缓存.............");
		}
		this.initDataFromDb();
		//启用redis管理缓存
		if(startRedis){
			this.setMapToReids();
			logger.info("-将系统配置缓存更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}
	}
	
	 public void initDataFromDb(){
		 List list = systemConfigService.queryAllConfigs();
		 for(int i = 0; i < list.size(); i++){
			SystemConfig conf = (SystemConfig) list.get(i);
			systemConfigMap.put(conf.getCode(), conf.getItem());
		 }
	 }
	 
	 private void setMapToReids(){
		 redisrCache.hmset("systemConfigMap",systemConfigMap);
	 }
	
	/**
	 * 重新加载缓存
	 * @return void
	 */
	public void reloadCache(){
		long threadNm = Thread.currentThread().getId();
		if(startRedis == true){
			this.initDataFromDb();
			this.setMapToReids();
			logger.info(threadNm+"-将系统参数配置更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}else{
			this.clearAllCache();
			this.initCache();
		}
		
	}
	
	public void clearAllCache(){
		systemConfigMap.clear();
	}

	  
	/**
	  * 重新加载单笔缓存
	  * @param dataMap 数据集合 key=code value=系统参数配置CODE
	  * @return void 
	  */
	public void reloadSignleCache(Map dataMap){
		logger.debug("---");
	}
	
	/**
	 *根据系统参数编码获取系统参数配置信息
	 * @param code 系统参数编码
	 * @return 系统配置参数值
	 */
	public static String getSystemConfigItemByCode(String code){
		if(startRedis){
			return getDicNameFromRedis("systemConfigMap",code);
		}
		return systemConfigMap.containsKey(code) ? (String)systemConfigMap.get(code) : "";
	}

	public static String getDicNameFromRedis(String mapName,String code){
		if(code == null){
			return code;
		}
		RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
		String name = (String)redisrCache.hget(mapName,code);
		return StringUtils.isNotBlank(name)?name:"";
	}
	
	/**
	 *根据机构核算网点号，获取该网点的虚拟柜员
	 * @param code 系统参数编码
	 * @return 系统配置参数值
	 */
	public static String getDepartmentVirtualUserNoByCode(String code){
		if(startRedis){
			return getDicNameFromRedis("systemConfigMap","VIRTUAL_"+code+"_USERNO");
		}
		return systemConfigMap.containsKey("VIRTUAL_"+code+"_USERNO") ? (String)systemConfigMap.get("VIRTUAL_"+code+"_USERNO") : null;
		
	}
	
	/**
	 *根据机构核算网点号，后去该网点的虚拟柜员
	 * @param code 系统参数编码
	 * @return 系统配置参数值
	 */
	public static String getAcceptionBkInnerAccountByCode(String code){
		if(startRedis){
			return getDicNameFromRedis("systemConfigMap","BK_"+code+"_ACCOUNT");
		}
		return systemConfigMap.containsKey("BK_"+code+"_ACCOUNT") ? (String)systemConfigMap.get("BK_"+code+"_ACCOUNT") : null;
	}
	
	public Class getEntityClass(){
		return null;
	}

	public String getEntityName(){
		return null;
	}

	public boolean isStartRedis() {
		return startRedis;
	}

	public void setStartRedis(boolean startRedis) {
		this.startRedis = startRedis;
	}
}
