package com.mingtech.application.ecds.common.domain;
/**
 * 审批 结果返回对象
 * @author Administrator
 *
 */
public class AuditResultDto {
	/**
	 * 成功
	 */
	public static String CODE_AUDIT_SUCCESS_00="00";
	/**
	 * 未找到审批路线
	 */
	public static String CODE_NO_AUDIT_01="01";
	/**
	 * 审批金额过大 ，所有审批节点 都没有权限
	 */
	public static String CODE_NO_AUDIT_02="02";
	/**
	 * 没有找到审批人员
	 */
	public static String CODE_NO_AUDIT_NO_USERS_03="02";
	
	private String retCode;//返回结果处理码
	private String retMsg;//处理结果信息
	private boolean ifSuccess;//是否成功
	
	
	public boolean isIfSuccess() {
		return ifSuccess;
	}
	public void setIfSuccess(boolean ifSuccess) {
		this.ifSuccess = ifSuccess;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
}
