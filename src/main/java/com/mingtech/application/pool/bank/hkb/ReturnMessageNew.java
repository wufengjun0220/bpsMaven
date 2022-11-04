package com.mingtech.application.pool.bank.hkb;

import java.util.HashMap;
import java.util.Map;

import com.mingtech.application.pool.bank.message.ReturnMessage;


/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 
 *
 */
public class ReturnMessageNew extends ReturnMessage{

	
	private static final long serialVersionUID = -3585050331652749122L;
	private Map sysHead = new HashMap();          //主报文SysHeader内容Map
    private Map appHead = new HashMap();          //主报文AppHeader内容Map
	
	private Map localHead = new HashMap();          //主报文localHeader内容Map
    private Map fileHead = new HashMap();          //主报文fileHeader内容Map
	
	private Map body = new HashMap();          //主报文body内容Map
	
	private Ret ret;

	private String codeSign;//核心标识  上传文件时,根据此标识判断是核心交易,确定文件分隔符

	public String getCodeSign() {
		return codeSign;
	}

	public void setCodeSign(String codeSign) {
		this.codeSign = codeSign;
	}

	// 返回状态码及对象
	private String txCode;

	public Map getSysHead() {
		return sysHead;
	}

	public void setSysHead(Map sysHead) {
		this.sysHead = sysHead;
	}

	public Map getAppHead() {
		return appHead;
	}

	public void setAppHead(Map appHead) {
		this.appHead = appHead;
	}

	public Map getLocalHead() {
		return localHead;
	}

	public void setLocalHead(Map localHead) {
		this.localHead = localHead;
	}

	public Map getFileHead() {
		return fileHead;
	}

	public void setFileHead(Map fileHead) {
		this.fileHead = fileHead;
	}

	public Map getBody() {
		return body;
	}

	public void setBody(Map body) {
		this.body = body;
	}

	public Ret getRet() {
		return ret;
	}

	public void setRet(Ret ret) {
		this.ret = ret;
	}

	public String getTxCode() {
		return txCode;
	}

	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}



	
	
}
