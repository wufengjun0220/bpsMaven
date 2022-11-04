package com.mingtech.application.audit.domain;
/**
 * 审批 结果返回对象
 * @author Administrator
 *
 */
public class AuditResultDto {
	
	
	private String retCode;//返回结果处理码
	private String retMsg;//处理结果信息
	private String reqNo;//请求流水号
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
	public String getReqNo() {
		return reqNo;
	}
	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}
}
