package com.mingtech.application.pool.edu.domain;
import java.math.BigDecimal;
import java.util.Date;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Sep 9, 2010 11:05:22 AM
* @描述: [AccTrans] 票据池-账户间转账实体
*/
public class AccTrans  implements java.io.Serializable {
     /** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private String id;               //主键id
	private String cusCommid;        //客户组织机构代码
	private String cusCommname;      //客户名称
	private String transType;        //划转类型
	private BigDecimal transAmt;         //划转金额
	private String fromAccnumber;    //划出账号
	private Integer fromAcclimit;    //划出账户期限
	private Date fromAccduedt;       //划出账户到期日
	private String toAccnumber;      //划入账号
	private Integer toAcclimit;      //划入账号期限(单位:月)
	private Date toAccduedt;         //划入账号到期日
	private String transStatus;      //交易状态
	private Date operTime;           //交易时间
	private String operLog;          //交易记录，大字段，保存前先get然后追加
	private String operBankNumber;   //交易机构大额号
	private String operBankName;     //交易机构名称
	private Date staDate ; //起息日
	private BigDecimal timeRate;//利率
	private String remark;//摘要
	private String gylsh;//柜员流水号
	private String usage;//用途   1.保证金支出； 2.保证金存入；3.其他      实际只有支出功能
	public String getGylsh() {
		return gylsh;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public void setGylsh(String gylsh) {
		this.gylsh = gylsh;
	}

	public BigDecimal getTimeRate() {
		return timeRate;
	}

	public void setTimeRate(BigDecimal timeRate) {
		this.timeRate = timeRate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getCusCommid() {
        return this.cusCommid;
    }
    
    public void setCusCommid(String cusCommid) {
        this.cusCommid = cusCommid;
    }

    public String getCusCommname() {
        return this.cusCommname;
    }
    
    public void setCusCommname(String cusCommname) {
        this.cusCommname = cusCommname;
    }

    public String getTransType() {
        return this.transType;
    }
    
    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getFromAccnumber() {
        return this.fromAccnumber;
    }
    
    public void setFromAccnumber(String fromAccnumber) {
        this.fromAccnumber = fromAccnumber;
    }

    public Date getFromAccduedt() {
        return this.fromAccduedt;
    }
    
    public void setFromAccduedt(Date fromAccduedt) {
        this.fromAccduedt = fromAccduedt;
    }

    public String getToAccnumber() {
        return this.toAccnumber;
    }
    
    public void setToAccnumber(String toAccnumber) {
        this.toAccnumber = toAccnumber;
    }

    public Date getToAccduedt() {
        return this.toAccduedt;
    }
    
    public void setToAccduedt(Date toAccduedt) {
        this.toAccduedt = toAccduedt;
    }

    public String getTransStatus() {
        return this.transStatus;
    }
    
    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public Date getOperTime() {
        return this.operTime;
    }
    
    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getOperLog() {
        return this.operLog;
    }
    
    public void setOperLog(String operLog) {
        this.operLog = operLog;
    }

	
	public Integer getFromAcclimit(){
		return fromAcclimit;
	}

	
	public void setFromAcclimit(Integer fromAcclimit){
		this.fromAcclimit = fromAcclimit;
	}

	
	public Integer getToAcclimit(){
		return toAcclimit;
	}

	
	public void setToAcclimit(Integer toAcclimit){
		this.toAcclimit = toAcclimit;
	}

	
	public String getOperBankNumber(){
		return operBankNumber;
	}

	
	public void setOperBankNumber(String operBankNumber){
		this.operBankNumber = operBankNumber;
	}

	
	public String getOperBankName(){
		return operBankName;
	}

	
	public void setOperBankName(String operBankName){
		this.operBankName = operBankName;
	}

	public BigDecimal getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(BigDecimal transAmt) {
		this.transAmt = transAmt;
	}

	public Date getStaDate() {
		return staDate;
	}

	public void setStaDate(Date staDate) {
		this.staDate = staDate;
	}

}