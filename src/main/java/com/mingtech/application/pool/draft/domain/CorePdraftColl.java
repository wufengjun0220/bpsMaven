package com.mingtech.application.pool.draft.domain;

import java.util.Date;

/**
 * 支票托收回款信息实体，该实体对应的库表数据来源为数据平台同步核心系统插数
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-7-5
 */

public class CorePdraftColl implements java.io.Serializable {

	private static final long serialVersionUID = 9222287936212085654L;
	private String id;
	private String afxno;      //附件号码 ：票据号码                    
	private String collStatus;//托收状态

	// Constructors

	/** default constructor */
	public CorePdraftColl() {
	}



	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAfxno() {
		return afxno;
	}

	public void setAfxno(String afxno) {
		this.afxno = afxno;
	}

	public String getCollStatus() {
		return collStatus;
	}

	public void setCollStatus(String collStatus) {
		this.collStatus = collStatus;
	}
}