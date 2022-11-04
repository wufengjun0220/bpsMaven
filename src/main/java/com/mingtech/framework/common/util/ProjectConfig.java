package com.mingtech.framework.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * 类说明：获取系统配置资源
 *
 * @author huangshiqiang@ May 11, 2009
 */
public class ProjectConfig {
	private static ProjectConfig config;
	private Properties properties;
	private Logger logger = Logger.getLogger(ProjectConfig.class);

	/**
	 * 构造函数 加载配置文件
	 */
	private ProjectConfig() {
		try {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("config/project.properties");
			properties = new Properties();
			properties.load(is);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	/**
	 * 获取项目配置对象实例
	 *
	 * @return
	 * @author:huangshiqiang@
	 * @time:May 11, 2009
	 */
	public static ProjectConfig getInstance() {
		if (null != config) {
			return config;
		} else {
			return new ProjectConfig();
		}
	}

	/**
	 * 获取附件配置路径
	 *
	 * @return 附件存放路径
	 * @author:huangshiqiang@
	 * @time:May 11, 2009
	 */
	public String getAttachPath() {
		if (null != properties) {
			return properties.get("attach.path").toString();
		} else {
			return "";
		}
	}
	
	

	/**
	* 方法说明: 获取通用确认报文处理成功的报文处理码(报文处理码要以逗号[英文半角]分隔)
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-5-17 上午11:40:37
	*/
	public String getMsg033SuccessCode(){
		if (null != properties) {
			return properties.get("ecds.msg033SuccessCode").toString();
		} else {
			return "";
		}
	}

	/**
	* 方法说明: 获取线上清算结果通知报文处理成功的报文处理码(报文处理码要以逗号[英文半角]分隔)
	* @param
	* @author  E-mail: pengdaochang@
	* @return
	* @date 2009-6-3 下午10:25:24
	*/
	public String getMsg036SuccessCode(){
		if (null != properties) {
			return properties.get("ecds.msg036SuccessCode").toString();
		} else {
			return "";
		}
	}

	/**
	 * 获取excel校验的xsd配置路径
	 *
	 * @return excel校验的xsd存放路径
	 * @author:xiangwanli@
	 * @time:May 11, 2009
	 */
	public String getXsdPath() {
		if (null != properties) {
			return properties.get("excelmodel.path").toString();
		} else {
			return "";
		}
	}
	
	/**
	* <p>方法名称: getNotificationAutoSend|描述: 判断是否自动发送清分失败报文</p>
	* @return
	*/
	public boolean getNotificationAutoSend(){
		if (null != properties) {
			String result = properties.get("ecds.notification.autoSend").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	/**
	* <p>方法名称: getLocateCustomerLogin|描述: 判断柜面代理操作是否需要重复登录</p>
	* @return
	*/
	public boolean getLocateCustomerLogin(){
		if (null != properties) {
			String result = properties.get("ecds.locateCustomer.login").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	/**
	* <p>方法名称: getConsignAutoSend|描述: 判断是否在票据到期日后自动发起提示付款</p>
	* @return
	*/
	public boolean getConsignAutoSend(){
		if (null != properties) {
			String result = properties.get("ecds.consign.autoSend").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	/**
	* <p>方法名称: getDiscountRate|描述: 判断是否对贴现利率控制进行检查</p>
	* @return
	*/
	public boolean getDiscountRate(){
		if (null != properties) {
			String result = properties.get("ecds.discount.rate").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	/**
	* <p>方法名称: getDiscountContract|描述: 判断是否对贴现贸易背景控制进行检查</p>
	* @return
	*/
	public boolean getDiscountContract(){
		if (null != properties) {
			String result = properties.get("ecds.discount.contract").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	/**
	* <p>方法名称: getDiscountCredit|描述:判断是否对贴现额度控制进行检查</p>
	* @return
	*/
	public boolean getDiscountCredit(){
		if (null != properties) {
			String result = properties.get("ecds.discount.credit").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	/**
	* <p>方法名称: getDiscountCredit|描述:判断是否对贴现额度控制进行检查</p>
	* @return
	*/
	public boolean getCollateralQuota(){
		if (null != properties) {
			String result = properties.get("ecds.collateral.quota").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	
	/**
	* <p>方法名称: getAmountByYear|描述: 判断统计年累计发生额的方式</p>
	* @return
	*/
	public String getAmountByYear(){
		if (null != properties) {
			String result = properties.get("report.amountByYear").toString();
			return result;
		} else {
			return "";
		}
	}

	
	/**
	* <p>方法名称: getDiscountAcceptorBankCode|描述: 贴现交易中验证银票票据承兑行行号开关   
	*  true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getDiscountAcceptorBankCode(){
		if (null != properties) {
			String result = properties.get("ecds.discount.acceptorBankCode").toString();
			return result;
		} else {
			return "";
		}
		
	}
	
	/**
	* <p>方法名称: getDiscountAcceptor|描述: 贴现交易中验证商票票据承兑人开关    
	* true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getDiscountAcceptor(){
		if (null != properties) {
			String result = properties.get("ecds.discount.acceptor").toString();
			return result;
		} else {
			return "";
		}
		
	}
	
	/**
	* <p>方法名称: getDiscountCounterparty|描述: 贴现交易中验证交易对手开关    
	* true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getDiscountCounterparty(){
		if (null != properties) {
			String result = properties.get("ecds.discount.counterparty").toString();
			return result;
		} else {
			return "";
		}
		
	}
	
	/**
	* <p>方法名称: getDiscountIdNb|描述: 贴现交易中验证票据号码开关    
	* true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getDiscountIdNb(){
		if (null != properties) {
			String result = properties.get("ecds.discount.idNb").toString();
			return result;
		} else {
			return "";
		}
		
	}
	

	/**
	* <p>方法名称: getRediscountAcceptorBankCode|描述: 转贴现交易中验证银票票据承兑行行号开关   
	*  true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getRediscountAcceptorBankCode(){
		if (null != properties) {
			String result = properties.get("ecds.rediscount.acceptorBankCode").toString();
			return result;
		} else {
			return "";
		}
		
	}
	
	/**
	* <p>方法名称: getRediscountAcceptor|描述: 转贴现交易中验证商票票据承兑人开关    
	* true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getRediscountAcceptor(){
		if (null != properties) {
			String result = properties.get("ecds.rediscount.acceptor").toString();
			return result;
		} else {
			return "";
		}
		
	}
	
	/**
	* <p>方法名称: getRediscountCounterparty|描述: 转贴现交易中验证交易对手开关    
	* true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getRediscountCounterparty(){
		if (null != properties) {
			String result = properties.get("ecds.rediscount.counterparty").toString();
			return result;
		} else {
			return "";
		}
		
	}
	
	/**
	* <p>方法名称: getRediscountIdNb|描述: 转贴现交易中验证票据号码开关    
	* true 提示且限制业务操作   false 提示但不限制业务操作</p>
	* @return
	*/
	public String getRediscountIdNb(){
		if (null != properties) {
			String result = properties.get("ecds.rediscount.idNb").toString();
			return result;
		} else {
			return "";
		}
		
	}
	

	
	/**
	 * <p>方法名称: getBankLevel|描述: 报表机构级别</p>
	 * @return
	 */
	public String getBankLevel(){
		if(null != properties){
			return properties.get("report.bankLevel").toString();
		}else{
			return "0";
		}
	}
	/**
	* <p>方法名称: getUseDefaultEdu|描述: 是否使用维护的默认同业额度生成授信额度</p>
	* @return
	*/
	public boolean getUseDefaultEdu(){
		if (null != properties) {
			String result = properties.get("ecds.useDefaultEdu").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	
	/**
	* <p>方法名称: getacceptanceProtocolDrwrOrgCode|描述: 承兑协议中验证出票人组织机构代码开关</p>
	* @return
	*/
	public String getacceptanceProtocolDrwrOrgCode(){
		if (null != properties) {
			String result = properties.get("ecds.acceptanceProtocol.drwrOrgCode").toString();
			return result;
		}else{
			return "";
		} 
		
	}
	//河北银行
	/**
	* <p>方法名称: isValidateFromCoreSys|描述: 是否去核心校验密码</p>
	* @return
	*/
	public boolean isValidateFromCoreSys(){
		if (null != properties) {
			String result = properties.get("ecds.validateFromCoreSys").toString();
			return ("true".equals(result));
		} else {
			return false;
		}
	}
	/**
	* <p>方法名称: getLocalPath|描述: 本地文件路径</p>
	* @return
	*/
	public String getLocalPath(){
		if (null != properties) {
			String result = properties.get("ecds.tft.localPath").toString();
			return result;
		} else {
			return "";
		}
	}
	/**
	* <p>方法名称: getTftClientPath|描述: TFT工具路径</p>
	* @return
	*/
	public String getTftClientPath(){
		if (null != properties) {
			String result = properties.get("ecds.tft.clientPath").toString();
			return result;
		} else {
			return "";
		}
	}
	/**
	* <p>方法名称: getTftDownRemoteCheckAccountPath|描述: 对账相对路径，远程下载对账文件路径</p>
	* @return
	*/
	public String getTftDownRemoteCheckAccountPath(){
		if (null != properties) {
			String result = properties.get("ecds.tft.down.remote.checkaccount.Path").toString();
			return result;
		} else {
			return "";
		}
	}
	/**
	* <p>方法名称: getTftUpRemoteBeiKuanPath|描述: 备款相对路径，远程上传备款文件路径</p>
	* @return
	*/
	public String getTftUpRemoteBeiKuanPath(){
		if (null != properties) {
			String result = properties.get("ecds.tft.up.remote.beikuan.path").toString();
			return result;
		} else {
			return "";
		}
	}
	/**
	* <p>方法名称: getTftUpRemoteCreditSynPath|描述: 信贷状态同步文件上传路径</p>
	* @return
	*/
	public String getTftUpRemoteCreditSynPath(){
		if (null != properties) {
			String result = properties.get("ecds.tft.up.remote.creditSyn.Path").toString();
			return result;
		} else {
			return "";
		}
	}
	public String getkeyValue(String key){
		if (null != properties) {
			String result = properties.get(key).toString();
			return result;
		} else {
			return "";
		}
	}
	
	/**
	 * 获得信贷连接票据池的FTP目录
	 * @return
	 */
	public String getFtpPoolCreditLocalpath(){
		if (null != properties) {
			String result = properties.get("ecds.ftp.pool.credit.localpath").toString();
			return result;
		} else {
			return "";
		}
	}
	
	public String getTftClientC112Path(){
		if (null != properties) {
			String result = properties.get("ecds.ftp.credit.c112SendPath").toString();
			return result;
		} else {
			return "";
		}
	}
	public String getTftClientC113SendPath(){
		if (null != properties) {
			String result = properties.get("ecds.ftp.credit.c113SendPath").toString();
			return result;
		} else {
			return "";
		}
	}
	public String getTftClientC113ReceivePath(){
		if (null != properties) {
			String result = properties.get("ecds.ftp.credit.c113ReceivePath").toString();
			return result;
		} else {
			return "";
		}
	}
	
	/**
	* <p>方法名称: getCustVolumeBusinessType|描述: 判断客户经理绩效考核类型</p>
	* @return
	*/
	public String getCustVolumeBusinessType(){
		String result = "";
		if (null != properties) {
			result = properties.get("ecds.CustVolume.BusinessType").toString();
			return result;
		} else {
			return result;
		}
	}
	
	/**
	 * 获取信贷系统webservice服务的IP
	 *
	 * @return 获取信贷系统webservice服务的IP
	 * @author:qichao@
	 * @time:Apr 04, 2012
	 */
	public String getCreditServerIp() {
		if (null != properties) {
			return properties.get("webservice.credit.ServerIp").toString();
		} else {
			return "";
		}
	}
	
	/**
	 * 获取信贷系统webservice服务的port
	 *
	 * @return 获取信贷系统webservice服务的port
	 * @author:qichao@
	 * @time:Apr 04, 2012
	 */
	public String getCreditServerPort() {
		if (null != properties) {
			return properties.get("webservice.credit.ServerPort").toString();
		} else {
			return "";
		}
	}
	/**
	 * 总行机构号
	 * @return
	 */
	public String getRootBrachNo() {
		if (null != properties) {
			return properties.get("ROOT_BRANCH_NO").toString();
		} else {
			return "";
		}
	}

	/**
	 * 获取 数据库连接方式
	 * 1:直连数据库  2：数据源方式连接数据库
	 */
	public String getConnType() {
		if (null != properties) {
			return properties.get("datasource.conn.type").toString();
		} else {
			return "1";
		}
	}
	
	/**
	 * 获取附件配置路径
	 *
	 * @return 附件存放路径
	 * @author:huangshiqiang@
	 * @time:May 11, 2009
	 */
	public String getImagePath() {
		if (null != properties) {
			return properties.get("image.path").toString();
		} else {
			return "";
		}
	}

	public Properties getProperties() {
		return properties;
	}
	
}
