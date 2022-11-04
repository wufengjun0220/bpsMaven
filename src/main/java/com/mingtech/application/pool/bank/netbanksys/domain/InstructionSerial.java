package com.mingtech.application.pool.bank.netbanksys.domain;

import java.util.Date;

public class InstructionSerial  implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String instructionSerial;     //指令序号
	private String messageCode;           //明细ID
	private String tranCode;              //交易码
	private Date tranTime;                //交易执行时间

    /** default constructor */
    public InstructionSerial() {
    	
    }

    /** full constructor */
    public InstructionSerial(String instructionSerial, String messageCode, String tranCode,Date tranTime) {
        this.instructionSerial = instructionSerial;
        this.messageCode = messageCode;
        this.tranCode = tranCode;
        this.tranTime = tranTime;
    }

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getInstructionSerial() {
        return this.instructionSerial;
    }
    public void setInstructionSerial(String instructionSerial) {
        this.instructionSerial = instructionSerial;
    }

    public String getMessageCode() {
        return this.messageCode;
    }
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

	public String getTranCode() {
		return tranCode;
	}
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	public Date getTranTime() {
		return tranTime;
	}
	public void setTranTime(Date tranTime) {
		this.tranTime = tranTime;
	}

}