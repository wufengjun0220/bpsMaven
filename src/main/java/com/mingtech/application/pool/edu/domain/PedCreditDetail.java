package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 出账信息实体类
 * @Description: 单笔借据详细信息
 * @author Ju Nana
 * @date 2018-12-17
 */
public class PedCreditDetail  implements java.io.Serializable {
	
	private static final long serialVersionUID = -9059497050213295770L;
	private String creditDetailId;//主键ID
     private String crdtNo;//信贷产品号：信贷业务合同号
     private String custNumber;//客户号
     private String custName;//客户名称
     private String loanNo;//借据号
     private Date transTime;//交易时间
     private String loanType;//交易类型 : XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款
     private String loanStatus;//交易状态 : JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清  JJ_05:未用退回（已撤销）
     private String transAccount;//交易账号 （表外业务对应业务保证金账号，表内业务对应贷款账号）
     private BigDecimal loanAmount;//借据金额
     private BigDecimal loanBalance;//借据余额=借据应还本金  ---暂时未用
     private Date repaymentTime;//还款时间
     private BigDecimal penaltyAmount;//罚息金额
     private BigDecimal actualAmount;//实际占用金额=借据余额+罚息金额
     private BigDecimal bailAccAmt;//保证金余额,用于银承
     private String detailStatus;//借据状态,0-不在处理,1-还需处理
     private Date startTime;//借据起始日
     private Date endTime;//借据到期日
	 private String ifAdvanceAmt; //是否垫款0： 否 1: 是'
     private String loanAdvanceNo;//垫款借据号
     private BigDecimal initialMarginAmt;//表外业务初始业务保证金金额
     private String isOnline;//是否线上 1 是 0 否
     private Date taskDate;//调度操作时间
     private String bpsNo;//票据池编号
     private String riskLevel;//风险等级
 	
    
     
	public Date getTaskDate() {
		return taskDate;
	}


	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}


	public String getIsOnline() {
		return isOnline;
	}


	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}


	public BigDecimal getInitialMarginAmt() {
		return initialMarginAmt;
	}


	public void setInitialMarginAmt(BigDecimal initialMarginAmt) {
		this.initialMarginAmt = initialMarginAmt;
	}


	public String getIfAdvanceAmt() {
		return ifAdvanceAmt;
	}


	public void setIfAdvanceAmt(String ifAdvanceAmt) {
		this.ifAdvanceAmt = ifAdvanceAmt;
	}


	public String getLoanAdvanceNo() {
		return loanAdvanceNo;
	}


	public void setLoanAdvanceNo(String loanAdvanceNo) {
		this.loanAdvanceNo = loanAdvanceNo;
	}

	
	
    
    public PedCreditDetail() {
    }

    
    public PedCreditDetail(String crdtNo, String custNumber, String custName, String loanNo, Date transTime, String loanType, String loanStatus, 
    		String transAccount, BigDecimal loanAmount, BigDecimal loanBalance, Date repaymentTime, BigDecimal penaltyAmount, 
    		BigDecimal actualAmount,String bpsNo,String riskLevel) {
        this.crdtNo = crdtNo;
        this.custNumber = custNumber;
        this.custName = custName;
        this.loanNo = loanNo;
        this.transTime = transTime;
        this.loanType = loanType;
        this.loanStatus = loanStatus;
        this.transAccount = transAccount;
        this.loanAmount = loanAmount;
        this.loanBalance = loanBalance;
        this.repaymentTime = repaymentTime;
        this.penaltyAmount = penaltyAmount;
        this.actualAmount = actualAmount;
        this.bpsNo = bpsNo;
        this.riskLevel = riskLevel;
    }

   
    public String getRiskLevel() {
		return riskLevel;
	}


	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}


	public String getBpsNo() {
    	return bpsNo;
    }
    
    public void setBpsNo(String bpsNo) {
    	this.bpsNo = bpsNo;
    }

    public String getCreditDetailId() {
        return this.creditDetailId;
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

    public BigDecimal getActualAmount() {
        return this.actualAmount;
    }
    
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getBailAccAmt() {
        return bailAccAmt;
    }

    public void setBailAccAmt(BigDecimal bailAccAmt) {
        this.bailAccAmt = bailAccAmt;
    }

    public String getDetailStatus() {
        return detailStatus;
    }

    public void setDetailStatus(String detailStatus) {
        this.detailStatus = detailStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}