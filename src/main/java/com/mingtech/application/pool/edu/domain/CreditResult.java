package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.List;

import com.mingtech.application.pool.common.util.BigDecimalUtils;


/**
 * Credit查询返回
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-28
 */
public class CreditResult {
	
	private BigDecimal useAmtTotal = BigDecimalUtils.ZERO;//累计占用额度
	private BigDecimal usedBalance = BigDecimalUtils.ZERO;//额度占用余额=占用额度 - 累计释放额度
	private BigDecimal rlsAmtTotal = BigDecimalUtils.ZERO;//额度累计释放金额
	
	
	public BigDecimal getUseAmtTotal() {
		return useAmtTotal;
	}
	public void setUseAmtTotal(BigDecimal useAmtTotal) {
		this.useAmtTotal = useAmtTotal;
	}
	public BigDecimal getUsedBalance() {
		return usedBalance;
	}
	public void setUsedBalance(BigDecimal usedBalance) {
		this.usedBalance = usedBalance;
	}
	public BigDecimal getRlsAmtTotal() {
		return rlsAmtTotal;
	}
	public void setRlsAmtTotal(BigDecimal rlsAmtTotal) {
		this.rlsAmtTotal = rlsAmtTotal;
	}
	
	
	
    

}
