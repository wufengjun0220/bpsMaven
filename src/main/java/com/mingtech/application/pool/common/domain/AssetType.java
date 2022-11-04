package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.pool.edu.domain.LimitVo;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Aug 19, 2010 3:30:40 PM
* @描述: [AssetType]资产类别实体
*/
public class AssetType  implements java.io.Serializable  {
    /** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private String id;
	private String astType;//池资产类型：ED_10:低风险票据池；ED_20:高风险票据池；ED_21:保证金池-活期保证金；ED_22:保证金池-定期保证金;
	private String asstName;//资产名称
	private BigDecimal crdtTotal;//衍生总额度
	private BigDecimal crdtFree;//可用额度
	private BigDecimal crdtUsed;//已用额度
	private Date crtTm;//创建时间
	private String apId;//资产池ID
	private String asstTabname;//资产明细表名
	private BigDecimal crdtFrzd;//已冻结额度
	
	private boolean firstTimeCaculate = true;//判断是否已经开始重新计算衍生额度 - 不存数据库
	
	/**
	 * zhaoding  新增票据数量
	 */
	private Long count;
	
	public void setUpVo(LimitVo vo){
		this.setCrdtTotal(vo.getTotal());
		this.setCrdtUsed(vo.getUsed());
		this.setCrdtFree(vo.getFree());
		this.setCrdtFrzd(vo.getFrzd());
		this.setCount(vo.getCount());
	}
	
	public void addUsedLimit(BigDecimal amt){
		crdtUsed = crdtUsed.add(amt);
    	crdtFree = crdtTotal.subtract(crdtUsed).subtract(crdtFrzd);
	}
	
	public void subUsedLimit(BigDecimal amt){
		crdtUsed = crdtUsed.add(amt);
    	crdtFree = crdtTotal.subtract(crdtUsed).subtract(crdtFrzd);
	}
	
	public void frzdLimit(BigDecimal amt){
		crdtFrzd = amt;
    	crdtFree = crdtTotal.subtract(crdtUsed).subtract(crdtFrzd);
	}
	
	public void unfrLimit(BigDecimal amt){
		crdtFrzd = crdtFrzd.subtract(amt);
    	crdtFree = crdtTotal.subtract(crdtUsed).subtract(crdtFrzd);
	}
	
	

    public String getAstType() {
        return this.astType;
    }
    
    public void setAstType(String astType) {
        this.astType = astType;
    }

    public String getAsstName() {
        return this.asstName;
    }
    
    public void setAsstName(String asstName) {
        this.asstName = asstName;
    }

    
	public BigDecimal getCrdtTotal(){
		return crdtTotal==null?new BigDecimal("0.00"):crdtTotal;
	}

	


	
	public BigDecimal getCrdtFree(){
		return crdtFree==null?new BigDecimal("0.00"):crdtFree;
	}

	


	
	public BigDecimal getCrdtUsed(){
		return crdtUsed==null?new BigDecimal("0.00"):crdtUsed;
	}

	


	public Date getCrtTm() {
        return this.crtTm;
    }
    
    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public String getApId() {
        return this.apId;
    }
    
    public void setApId(String apId) {
        this.apId = apId;
    }

    public String getAsstTabname() {
        return this.asstTabname;
    }
    
    public void setAsstTabname(String asstTabname) {
        this.asstTabname = asstTabname;
    }
   

	public boolean isFirstTimeCaculate() {
		return firstTimeCaculate;
	}

	public void setFirstTimeCaculate(boolean firstTimeCaculate) {
		this.firstTimeCaculate = firstTimeCaculate;
	}
	/**
	 * 已冻结额度
	 * @return
	 */
	public BigDecimal getCrdtFrzd() {
		return crdtFrzd==null?new BigDecimal("0.00"):crdtFrzd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCrdtTotal(BigDecimal crdtTotal) {
		this.crdtTotal = crdtTotal;
	}

	public void setCrdtFree(BigDecimal crdtFree) {
		this.crdtFree = crdtFree;
	}

	public void setCrdtUsed(BigDecimal crdtUsed) {
		this.crdtUsed = crdtUsed;
	}

	public void setCrdtFrzd(BigDecimal crdtFrzd) {
		this.crdtFrzd = crdtFrzd;
	}

	public Long getCount() {
		return count==null ? 0 :count;
	}

	public void setCount(Long count) {
		this.count = count;
	}




}