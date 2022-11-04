package com.mingtech.application.pool.common.domain;

import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;

/**
 * 保贴映射关系表
 * @author Ju Nana
 * @version v1.0
 * @date 2021-7-31
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class GuarantDiscMapping implements java.io.Serializable {


	private static final long serialVersionUID = 1L;
	private String id;             //主键ID            
	private String acceptor;       //承兑人名称           
	private String acptBankCode;   //承兑行行号（承兑人开户行行号） 
	private String acptBankname;   //承兑人名称（承兑人开户行行名） 
	private String acptAcctNo;     //承兑人账号           
	private String guarantDiscName;//保贴人名称           
	private String guarantDiscNo;  //保贴编号            
	private String userNo;         //柜员编号            
	private String userName;       //柜员名称            
	private String branchNo;       //机构号             
	private String branchName;     //机构名称            
	private String checkType;      //校验类型  1-财票 2-商票 
	private String status;         //状态 0-初始数据 1-生效  2-失效
	private String auditStatus;    //审批状态  SP_05新增  SP_01：提交审批   SP_02：处理中   SP_03：驳回   SP_04：通过
	private String auditStatusDesc;
	private Date createtime;       //创建时间            
	private Date updatetime;       //更新时间            
	private String delFlag;        //逻辑删除标记-N未删除、D已删除  


	public GuarantDiscMapping() {
	}


	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAcceptor() {
		return this.acceptor;
	}

	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}

	public String getAcptBankCode() {
		return this.acptBankCode;
	}

	public void setAcptBankCode(String acptBankCode) {
		this.acptBankCode = acptBankCode;
	}



	public String getAcptBankname() {
		return acptBankname;
	}


	public void setAcptBankname(String acptBankname) {
		this.acptBankname = acptBankname;
	}


	public String getAcptAcctNo() {
		return this.acptAcctNo;
	}

	public void setAcptAcctNo(String acptAcctNo) {
		this.acptAcctNo = acptAcctNo;
	}

	public String getGuarantDiscName() {
		return this.guarantDiscName;
	}

	public void setGuarantDiscName(String guarantDiscName) {
		this.guarantDiscName = guarantDiscName;
	}

	public String getGuarantDiscNo() {
		return this.guarantDiscNo;
	}

	public void setGuarantDiscNo(String guarantDiscNo) {
		this.guarantDiscNo = guarantDiscNo;
	}

	public String getUserNo() {
		return this.userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBranchNo() {
		return this.branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCheckType() {
		return this.checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getAuditStatusDesc() {
		return auditStatusDesc = DictionaryCache.getFromPoolDictMap(String.valueOf(auditStatus));
	}

	public void setAuditStatusDesc(String auditStatusDesc) {
		this.auditStatusDesc = auditStatusDesc;
	}
}