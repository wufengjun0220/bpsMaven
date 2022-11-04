package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.Date;


/**
 * MisCredit entity. @author MyEclipse Persistence Tools
 */

public class MisCredit  implements java.io.Serializable {


    // Fields    

    private String creditDetailId;
    private String crdtNo;        //信贷产品号
    private String custNumber;    //核心客户号
    private String custName;      //客户名称   
    private String loanNo;        //借据号
    private String loanNoReal;        //借据号——废弃
    private Date transTime;       //业务时间
    private String loanType;      //借据类型 
    private String loanStatus;    //借据状态
    private String transAccount;  //业务账号（表内业务为：业务保证金账号，表外业务为：贷款账号）
    private BigDecimal loanAmount;    //借据金额
    private BigDecimal loanBalance;   //借据余额=剩余应还本金
    private Date repaymentTime;   //还款时间    ——该字段改为  借据到期日
    private BigDecimal penaltyAmount; //罚息金额
    private String endFlag;  //处理标识   0：未处理  1：已处理
    private String oldLoanNo;        //原借据号
    // Constructors

    /** default constructor */
    public MisCredit() {
    }

    
    /** full constructor */
    public MisCredit(String crdtNo, String custNumber, String custName, String loanNo,String loanNoReal, Date transTime, String loanType, String loanStatus, String transAccount, BigDecimal loanAmount, BigDecimal loanBalance, Date repaymentTime, BigDecimal penaltyAmount,String endFlag) {
        this.crdtNo = crdtNo;//主业务合同号
        this.custNumber = custNumber;//客户号
        this.custName = custName;//客户名称
        this.loanNo = loanNo;//贷款账号——票据池系统与核心及柜面均用贷款账号为唯一标识，与借据号一一对应
        this.transTime = transTime;//交易时间
        this.loanType = loanType;//借据类型  （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
        this.loanStatus = loanStatus;//借据状态 （JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清）
        this.transAccount = transAccount;//交易金额
        this.loanAmount = loanAmount;//借据金额
        this.loanBalance = loanBalance;//借据余额 = 结清该借据的应还本金
        this.repaymentTime = repaymentTime;//到期日：借据的到期日
        this.penaltyAmount = penaltyAmount;//欠息
        this.loanNoReal = loanNoReal;//借据号 ：该借据号只提供票据池系统的界面显示以及提供给网银
        this.endFlag = endFlag;
        this.oldLoanNo = oldLoanNo;
        
    }

   
    // Property accessors

    
    public String getCreditDetailId() {
        return this.creditDetailId;
    }
    
    public String getEndFlag() {
		return endFlag;
	}


	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}


	public String getLoanNoReal() {
		return loanNoReal;
	}


	public void setLoanNoReal(String loanNoReal) {
		this.loanNoReal = loanNoReal;
	}


	public void setCreditDetailId(String creditDetailId) {
        this.creditDetailId = creditDetailId;
    }

    public String getCrdtNo() {
        return this.crdtNo;
    }
    
    public void setCrdtNo(String crdtNo) {
        this.crdtNo = crdtNo;
    }

    public String getCustNumber() {
        return this.custNumber;
    }
    
    public void setCustNumber(String custNumber) {
        this.custNumber = custNumber;
    }

    public String getCustName() {
        return this.custName;
    }
    
    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getLoanNo() {
        return this.loanNo;
    }
    
    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public Date getTransTime() {
        return this.transTime;
    }
    
    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public String getLoanType() {
        return this.loanType;
    }
    
    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLoanStatus() {
        return this.loanStatus;
    }
    
    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public String getTransAccount() {
        return this.transAccount;
    }
    
    public void setTransAccount(String transAccount) {
        this.transAccount = transAccount;
    }

    public BigDecimal getLoanAmount() {
        return this.loanAmount;
    }
    
    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getLoanBalance() {
        return this.loanBalance;
    }
    
    public void setLoanBalance(BigDecimal loanBalance) {
        this.loanBalance = loanBalance;
    }

    public Date getRepaymentTime() {
        return this.repaymentTime;
    }
    
    public void setRepaymentTime(Date repaymentTime) {
        this.repaymentTime = repaymentTime;
    }

    public BigDecimal getPenaltyAmount() {
        return this.penaltyAmount;
    }
    
    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }


	public String getOldLoanNo() {
		return oldLoanNo;
	}


	public void setOldLoanNo(String oldLoanNo) {
		this.oldLoanNo = oldLoanNo;
	}
   








}