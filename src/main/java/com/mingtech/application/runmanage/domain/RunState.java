package com.mingtech.application.runmanage.domain;

import java.util.Date;

import com.mingtech.framework.common.util.DateUtils;

/**
 * <p>说明:接入点运行状态信息</p>
 *
 * @author   chenwei
 * @Date	 Apr 10, 2009 2:03:28 PM 
 * @hibernate.class table="cd_runstate"
 * @hibernate.cache usage="read-write"
 */
public class RunState {
	
	public static final String LOGONING = "正在登录";
	public static final String LOGONED = "已登录";
	public static final String LOGOUTING = "正在退出";
	public static final String LOGOUTED = "已退出";
	
	public static final String SYSSTS_BUSINESS_END = "业务截止";
	public static final String SYSSTS_BUSINESS_END_WORK = "日终处理";
	public static final String SYSSTS_BUSINESS_READY = "营业准备";
	public static final String SYSSTS_BUSINESS_HOUR = "日间处理";
	public static final String SYSSTS_CODE_BUSINESS_END = "20";
	public static final String SYSSTS_CODE_BUSINESS_END_WORK = "30";
	public static final String SYSSTS_CODE_BUSINESS_READY = "00";
	/**
	 * 日间处理
	 */
	public static final String SYSSTS_CODE_BUSINESS_HOUR = "10";	
	
	private	String	id;                  //主键ID
	private	String	bankName;            //接入商行名称
	private	String	bankCode;            //接入商行行号
	private	String	apId;                //接入点ID
	private	String	apName;              //接入点名称
	private String  npcCode;             //接入NPC号码
	private	String	logonState;          //登录状态（正在登录/已登录/正在退出/已退出，正常按上述顺序循环切换状态，除强制退出）
	private	String	oldPwd;              //原识别信息
	private	String	newPwd;              //新识别信息
	private	Date	orgnlSysDt;          //原系统日期
	private	String	orgnlSysSts;         //原系统状态
	private	Date	curDate;             //当前系统日期
	private	String	sysState;            //当前系统状态
	private	Date	nextDate;            //下一系统工作日期
	private	String	bizRefTm;            //营业参考时间
	private	String	rmrk;                //附言
	//private	String	crlFile;             //证书文件路径
	private	Date	crlLastUpdateTime;   //证书更新时间
	private	String	prcCd;               //登录处理结果码
	private	String	prcMsg;              //登录处理内容结果
	private String  pfxPath;             //私钥路径
	private String  pfxFileName;         //私钥文件名称 
	private String  unifyKey;            //是否使用统一证书
	private String  pfxPassword;         //私钥密码
	private String  crlPath;             //CRL吊销列表路径
	private String  sttlmOnlineMrk;      //线上清算标识
	private String  sttlmOnlineRmrk;     //线上清算附言
	private Date  hvpsNxtSysDt;        //大额下一系统工作日    
	private String npcStatus;          // NPC 状态
	private String ccpcCode;           //   ccpc码
	private String ccpcStatus;          //ccpc状态
	private String sysPartner;          // 系统参与者类别
	private String operSwitch;          //营业前准备批处理开关SO01：开 SO02 关
	private String swithType;           //开关种类ST01：营业前准备批处理开关
	private String region;				//省别区域代码
	private String bankcodetype;		//银行机构代码
	
	//201612 增加系统工作日概念
	private Date workDate;
	//系统状态概念    1正常营业  0日终状态 ，不可以做业务；
	private String systemSwitch;
	
	
	/**
     * @hibernate.id generator-class="uuid.hex" type="string" length="40"
	 * column="id"
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @hibernate.property type="string" column="rs_bankName"
	 */
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * @hibernate.property type="string" column="rs_apId"
	 */
	public String getApId() {
		return apId;
	}
	public void setApId(String apId) {
		this.apId = apId;
	}
	/**
	 * @hibernate.property type="string" column="rs_apName"
	 */
	public String getApName() {
		return apName;
	}
	public void setApName(String apName) {
		this.apName = apName;
	}

	/**
	 * @hibernate.property type="string" column="rs_npcCode" length="4"
	 */
	public String getNpcCode() {
		return npcCode;
	}
	public void setNpcCode(String npcCode) {
		this.npcCode = npcCode;
	}
	
	/**
	 * @hibernate.property type="string" column="rs_logonState"
	 */
	public String getLogonState() {
		return logonState;
	}
	public void setLogonState(String logonState) {
		this.logonState = logonState;
	}
	/**
	 * @hibernate.property type="string" column="rs_oldPwd"
	 */
	public String getOldPwd() {
		return oldPwd;
	}
	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}
	/**
	 * @hibernate.property type="string" column="rs_newPwd"
	 */
	public String getNewPwd() {
		return newPwd;
	}
	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	/**
	 * @hibernate.property type="date" column="rs_orgnlSysDt"
	 */
	public Date getOrgnlSysDt() {
		return orgnlSysDt;
	}
	public void setOrgnlSysDt(Date orgnlSysDt) {
		this.orgnlSysDt = orgnlSysDt;
	}
	/**
	 * @hibernate.property type="string" column="rs_orgnlSysSts"
	 */
	public String getOrgnlSysSts() {
		return orgnlSysSts;
	}
	public void setOrgnlSysSts(String orgnlSysSts) {
		this.orgnlSysSts = orgnlSysSts;
	}
	/**
	 * @hibernate.property type="date" column="rs_curDate"
	 */
	public Date getCurDate() {
		return curDate;
	}
	public void setCurDate(Date curDate) {
		this.curDate = curDate;
	}
	public Date getCurDateTime() {
		String dt = DateUtils.toString(this.getCurDate(), DateUtils.ORA_DATES_FORMAT)+" "+DateUtils.toString(new Date(), DateUtils.ORA_TIME2_FORMAT);
		return DateUtils.StringToDate(dt, DateUtils.ORA_DATE_TIMES3_FORMAT);
	}
	/**
	 * @hibernate.property type="string" column="rs_sysState"
	 */
	public String getSysState() {
		return sysState;
	}
	public void setSysState(String sysState) {
		this.sysState = sysState;
	}
	/**
	 * @hibernate.property type="date" column="rs_nextDate"
	 */
	public Date getNextDate() {
		return nextDate;
	}
	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}
	/**
	 * @hibernate.property type="string" column="rs_bizRefTm"
	 */
	public String getBizRefTm() {
		return bizRefTm;
	}
	public void setBizRefTm(String bizRefTm) {
		this.bizRefTm = bizRefTm;
	}
	/**
	 * @hibernate.property type="string" column="rs_rmrk"
	 */
	public String getRmrk() {
		return rmrk;
	}
	public void setRmrk(String rmrk) {
		this.rmrk = rmrk;
	}

	/**
	 * @hibernate.property type="timestamp" column="rs_crlLastUpdateTime"
	 */
	public Date getCrlLastUpdateTime() {
		return crlLastUpdateTime;
	}
	public void setCrlLastUpdateTime(Date crlLastUpdateTime) {
		this.crlLastUpdateTime = crlLastUpdateTime;
	}
	/**
	 * @hibernate.property type="string" column="rs_prcCd"
	 */
	public String getPrcCd() {
		return prcCd;
	}
	public void setPrcCd(String prcCd) {
		this.prcCd = prcCd;
	}
	/**
	 * @hibernate.property type="string" column="rs_prcMsg"
	 */
	public String getPrcMsg() {
		return prcMsg;
	}
	public void setPrcMsg(String prcMsg) {
		this.prcMsg = prcMsg;
	}
	/**
	 * @hibernate.property type="string" column="rs_bankCode"
	 */
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	/**
	 * @hibernate.property type="string" column="rs_pfxPath" length="50"
	 * @return
	 */
	public String getPfxPath() {
		return pfxPath;
	}
	public void setPfxPath(String pfxPath) {
		this.pfxPath = pfxPath;
	}
	/**
	 * @hibernate.property type="string" column="rs_pfxPassword" length="20"
	 * @return
	 */
	public String getPfxPassword() {
		return pfxPassword;
	}
	public void setPfxPassword(String pfxPassword) {
		this.pfxPassword = pfxPassword;
	}
    /**
     * @hibernate.property type="string" column="rs_crlPath" length="50"
     * @return
     */
	public String getCrlPath() {
		return crlPath;
	}
	public void setCrlPath(String crlPath) {
		this.crlPath = crlPath;
	}
	
	/**
	 * @hibernate.property type="string" column="rs_sttlmOnlineMrk" length="4"
	 * @return
	 */
	public String getSttlmOnlineMrk() {
		return sttlmOnlineMrk;
	}
	public void setSttlmOnlineMrk(String sttlmOnlineMrk) {
		this.sttlmOnlineMrk = sttlmOnlineMrk;
	}
	
	/**
	 * @hibernate.property type="string" column="rs_sttlmOnlineRmrk" length="60"
	 * @return
	 */
	public String getSttlmOnlineRmrk() {
		return sttlmOnlineRmrk;
	}
	public void setSttlmOnlineRmrk(String sttlmOnlineRmrk) {
		this.sttlmOnlineRmrk = sttlmOnlineRmrk;
	}
	
	/**
	 * @hibernate.property type="date" column="rs_hvpsNxtSysDt"
	 * @return
	 */
	public Date getHvpsNxtSysDt() {
		return hvpsNxtSysDt;
	}
	public void setHvpsNxtSysDt(Date hvpsNxtSysDt) {
		this.hvpsNxtSysDt = hvpsNxtSysDt;
	}
	
	/**
	 * @hibernate.property type="string" column="rs_pfxFileName" length="100"
	 * @return
	 */
	public String getPfxFileName(){
		return pfxFileName;
	}
	
	public void setPfxFileName(String pfxFileName){
		this.pfxFileName = pfxFileName;
	}
	/**
	 * @hibernate.property type="string" column="rs_unifyKey" length="4"
	 * @return
	 */
	public String getUnifyKey(){
		return unifyKey;
	}
	
	public void setUnifyKey(String unifyKey){
		this.unifyKey = unifyKey;
	}
	
	/**
	* <p>方法名称: getNpcStatus|描述:NPC状态 </p>
	* @return
	* @hibernate.property type="string" column="rs_npcStatus" length="10"
	*/
	public String getNpcStatus(){
		return npcStatus;
	}
	
	/**
	* <p>方法名称: setNpcStatus|描述:NPC状态  </p>
	* @param npcStatus
	*/
	public void setNpcStatus(String npcStatus){
		this.npcStatus = npcStatus;
	}
	
	/**
	* <p>方法名称: getCcpcCode|描述: ccpc号</p>
	* @return
	* @hibernate.property type="string" column="rs_ccpcCode" length="30"
	*/
	public String getCcpcCode(){
		return ccpcCode;
	}
	
	/**
	* <p>方法名称: setCcpcCode|描述:ccpc号 </p>
	* @param ccpcCode
	*/
	public void setCcpcCode(String ccpcCode){
		this.ccpcCode = ccpcCode;
	}
	
	/**
	* <p>方法名称: getCcpcStatus|描述:ccpc状态 </p>
	* @return
	* @hibernate.property type="string" column="rs_ccpcStatus" length="10"
	*/
	public String getCcpcStatus(){
		return ccpcStatus;
	}
	
	/**
	* <p>方法名称: setCcpcStatus|描述:ccpc状态 </p>
	* @param ccpcStatus
	*/
	public void setCcpcStatus(String ccpcStatus){
		this.ccpcStatus = ccpcStatus;
	}
	

	/**
	* <p>方法名称: getSysPartner|描述: 系统参与者类别</p>
	* @return
	* @hibernate.property type="string" column="rs_sysPartner" length="10"
	*/
	public String getSysPartner(){
		return sysPartner;
	}

	/**
	* <p>方法名称: setSysPartner|描述: 系统参与者类别</p>
	* @param sysPartner
	*/
	public void setSysPartner(String sysPartner){
		this.sysPartner = sysPartner;
	}
	
	/**
	* <p>方法名称: getOperSwitch|描述:营业前准备批处理开关SO01：开 SO02 关 </p>
	* @return
	* @hibernate.property type="string" column="rs_operSwitch" length="10"
	*/
	public String getOperSwitch(){
		return operSwitch;
	}
	
	/**
	* <p>方法名称: setOperSwitch|描述:营业前准备批处理开关SO01：开 SO02 关 </p>
	* @param operSwitch
	*/
	public void setOperSwitch(String operSwitch){
		this.operSwitch = operSwitch;
	}
	
	/**
	* <p>方法名称: getSwithType|描述: 开关种类ST01：营业前准备批处理开关</p>
	* @return
	* @hibernate.property type="string" column="RS_SWITCHTYPE" length="10"
	*/
	public String getSwithType(){
		return swithType;
	}
	
	/**
	* <p>方法名称: setSwithType|描述: 开关种类ST01：营业前准备批处理开关</p>
	* @param swithType
	*/
	public void setSwithType(String swithType){
		this.swithType = swithType;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getBankcodetype() {
		return bankcodetype;
	}
	public void setBankcodetype(String bankcodetype) {
		this.bankcodetype = bankcodetype;
	}
	public String getSystemSwitch() {
		return systemSwitch;
	}
	public void setSystemSwitch(String systemSwitch) {
		this.systemSwitch = systemSwitch;
	}
	public Date getWorkDate() {
		return workDate;
	}
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
}

