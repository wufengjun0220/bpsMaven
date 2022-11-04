package com.mingtech.application.pool.bank.translog.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.hkb.HKBConstants;
import com.mingtech.application.pool.bank.translog.action.TransLogAction;

public class TransLog implements Serializable{
	private static final Logger logger = Logger.getLogger(TransLog.class);
	private static final long serialVersionUID = 5727031620807971611L;
	private String id;                 //主键ID
	private String msgType;            //报文类型send/receive
	private String txCode;			   //交易代码
	private String txCodeStr ;         //交易名称
	private String billNo ;         //票据号码
	private String msgId;              //报文标识号--流水号，标识唯一一笔交易
	private Date txDate;//交易时间
	private Date creDtTm;              //报文创建时间--本地系统时间
	private String requisitionId;      //申请表单id
	private String status;             //状态--处理状态
	private byte[] msgContent;//报文内容
	private String remark;//备注内容
	private String dtoName;//交易实体名
	private String prodId;//产品类型
	private String txMark;//交易说明
	private byte[] rspMsgContent;//响应报文内容
	
	/*新增字段 2018-11-26*/
	private String transStatus;//交易流水
	private String custnumber;//核心客户号
	
	private String msgContentString;
	private String rspMsgContentStr;//响应报文内容字符串
	
	public String getMsgContentString() {
		try {
			 msgContentString = new String(this.getMsgContent(), HKBConstants.ENCODING);
			 
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}
		return msgContentString;
	}
	public void setMsgContentString(String msgContentString) {
		this.msgContentString = msgContentString;
	}
	
	public String getRspMsgContentStr() {
		try {
			rspMsgContentStr = new String(this.getRspMsgContent(), HKBConstants.ENCODING);
			 
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}
		return rspMsgContentStr;
	}
	public void setRspMsgContentStr(String rspMsgContentStr) {
		this.rspMsgContentStr = rspMsgContentStr;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getTxCode() {
		return txCode;
	}
	public void setTxCode(String txCode) {
		this.txCode = txCode;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public Date getCreDtTm() {
		return creDtTm;
	}
	public void setCreDtTm(Date creDtTm) {
		this.creDtTm = creDtTm;
	}
	public String getRequisitionId() {
		return requisitionId;
	}
	public void setRequisitionId(String requisitionId) {
		this.requisitionId = requisitionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public byte[] getMsgContent(){
		return msgContent;
	}
	
	public void setMsgContent(byte[] msgContent){
		this.msgContent = msgContent;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getTxDate() {
		return txDate;
	}
	public void setTxDate(Date txDate) {
		this.txDate = txDate;
	}
	public void setTxTypeName(String txTypeName){
	}
	
	public String getTxCodeStr(){
		return txCodeStr;
	}
	
	public void setTxCodeStr(String txCodeStr){
		this.txCodeStr = txCodeStr;
	}
	
	public String getDtoName(){
		return dtoName;
	}
	
	public void setDtoName(String dtoName){
		this.dtoName = dtoName;
	}
	
	public String getBillNo(){
		return billNo;
	}
	
	public void setBillNo(String billNo){
		this.billNo = billNo;
	}
	
	public String getProdId(){
		return prodId;
	}
	
	public void setProdId(String prodId){
		this.prodId = prodId;
	}
	
	public String getTxMark(){
		return txMark;
	}
	
	public void setTxMark(String txMark){
		this.txMark = txMark;
	}
	public String getTransStatus() {
		return transStatus;
	}
	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}
	public String getCustnumber() {
		return custnumber;
	}
	public void setCustnumber(String custnumber) {
		this.custnumber = custnumber;
	}
	public byte[] getRspMsgContent() {
		return rspMsgContent;
	}
	public void setRspMsgContent(byte[] rspMsgContent) {
		this.rspMsgContent = rspMsgContent;
	}
}
