package com.mingtech.application.pool.edu.domain;
import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.common.domain.Asset;
import com.mingtech.application.pool.common.util.BigDecimalUtils;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Aug 19, 2010 3:36:06 PM
* @描述: [BatlDetail]保证金资产明细
*/
public class BailDetail extends Asset implements java.io.Serializable{

	/** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private String bailType;//保证金类型：BZJ_00:活期；BZJ_01:定期
	private Date dueDt;//到期日，定期保证金用
	private String bdPeriod;//定期保证金的期限，单位：月
	private String bankName;     //账户开户行
	private BigDecimal rate = BigDecimalUtils.ZERO;;          //  利率
	//-----------待划出(存入)金额-end--------
	private Date staDate;//转入日期（起息日）
	
    public BailDetail(String assetType, String floatType, String rickLevel) {
		super(assetType, floatType, rickLevel);
	}

    
    
	/**
	 * 无参构造方法私有，构建改使用AssetFactory类
	 */
    private BailDetail() {
	}
    
    
	public String getBailType() {
		return bailType;
	}


	public void setBailType(String bailType) {
		this.bailType = bailType;
	}


	public Date getDueDt() {
		return dueDt;
	}


	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
	}


	public String getBdPeriod() {
		return bdPeriod;
	}


	public void setBdPeriod(String bdPeriod) {
		this.bdPeriod = bdPeriod;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public BigDecimal getRate() {
		return rate;
	}


	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}


	public Date getStaDate() {
		return staDate;
	}


	public void setStaDate(Date staDate) {
		this.staDate = staDate;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	




}