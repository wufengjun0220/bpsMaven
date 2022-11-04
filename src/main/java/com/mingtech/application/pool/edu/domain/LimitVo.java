package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;

import com.mingtech.application.pool.common.domain.AssetType;

public class LimitVo {
	private BigDecimal total = new BigDecimal("0");//衍生额度 
	private BigDecimal used = new BigDecimal("0");//已用额度
	private BigDecimal free = new BigDecimal("0");//可用额度
	private BigDecimal frzd = new BigDecimal("0");//已冻结额度
	private Long count = new Long(0);
	
	public LimitVo(){
		
	};
	public LimitVo(AssetType at){
		total = at.getCrdtTotal();
		used = at.getCrdtUsed();
		free = at.getCrdtFree();
		frzd = at.getCrdtFrzd();
		count = at.getCount();
	}
	public LimitVo(BigDecimal total,BigDecimal used,BigDecimal free,BigDecimal frzd,Long count){
		this.total = total;
		this.used = used;
		this.free = free;
		this.frzd = frzd;
		this.count = count;
	}
	
	public void add(LimitVo vo){
		this.total = this.total.add(vo.getTotal());
		this.used = this.used.add(vo.getUsed());
		this.free = this.free.add(vo.getFree());
		this.frzd = this.frzd.add(vo.getFrzd());
		this.count +=vo.getCount();
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getUsed() {
		return used;
	}
	public void setUsed(BigDecimal used) {
		this.used = used;
	}
	public BigDecimal getFree() {
		return free;
	}
	public void setFree(BigDecimal free) {
		this.free = free;
	}
	public BigDecimal getFrzd() {
		return frzd;
	}
	public void setFrzd(BigDecimal frzd) {
		this.frzd = frzd;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}


	
}
