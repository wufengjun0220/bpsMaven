package com.mingtech.application.pool.common.domain;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.mingtech.application.pool.edu.domain.LimitVo;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: 张永超
* @日期: Aug 19, 2010 3:27:38 PM
* @描述: [AssetPool]资产池实体
*/
public class AssetPool  implements java.io.Serializable {
     /** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	private String apId;//主键
	private String apName;//池名称
	private String poolType;//池种类：ZC_01:票据池
	private String custId;//池所属客户ID
	private String custName;//池所属客户名称
	private String custNo;//池所属客户号，固定填写组织机构代码，最为外围系统访问的客户唯一标识
	private String custOrgcode;//客户组织机构代码
	private Date crtTm;//创建时间
	private String crtOptid;//创建人ID
	private String bpsNo;//票据池编号
	private String dealStatus;//处理状态
	
	
	/**
	 * drftOpnStt 是否使用待定
	 */
	private String drftOpnStt;//票据池开通标记，已开通：DOP_01，已关闭：DOP_00；客户签约后认为票据池即可开通，解约后票据池关闭
	private Set assetTypes  = new HashSet(0);//票据池下的资产类别

	private BigDecimal crdtTotal;//衍生总额度
	private BigDecimal crdtFree;//可用额度
	private BigDecimal crdtUsed;//已用额度
	private BigDecimal crdtFrzd;//已冻结额度
	private Long count;
	public void setUpVo(LimitVo vo){
		this.setCrdtTotal(vo.getTotal());
		this.setCrdtUsed(vo.getUsed());
		this.setCrdtFree(vo.getFree());
		this.setCrdtFrzd(vo.getFrzd());
		this.setCount(vo.getCount());
	}
	
	
    public String getBpsNo() {
		return bpsNo;
	}


	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}


	public String getApId() {
        return this.apId;
    }
    
    public void setApId(String apId) {
        this.apId = apId;
    }

    public String getApName() {
        return this.apName;
    }
    
    public void setApName(String apName) {
        this.apName = apName;
    }

    public String getPoolType() {
        return this.poolType;
    }
    
    public void setPoolType(String poolType) {
        this.poolType = poolType;
    }

    public String getCustId() {
        return this.custId;
    }
    
    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustName() {
        return this.custName;
    }
    
    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustNo() {
        return this.custNo;
    }
    
    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustOrgcode() {
        return this.custOrgcode;
    }
    
    public void setCustOrgcode(String custOrgcode) {
        this.custOrgcode = custOrgcode;
    }

    public Date getCrtTm() {
        return this.crtTm;
    }
    
    public void setCrtTm(Date crtTm) {
        this.crtTm = crtTm;
    }

    public String getCrtOptid() {
        return this.crtOptid;
    }
    
    public void setCrtOptid(String crtOptid) {
        this.crtOptid = crtOptid;
    }

	public String getDrftOpnStt() {
		return drftOpnStt;
	}

	public void setDrftOpnStt(String drftOpnStt) {
		this.drftOpnStt = drftOpnStt;
	}

	public Set getAssetTypes() {
		return assetTypes;
	}

	public void setAssetTypes(Set assetTypes) {
		this.assetTypes = assetTypes;
	}
   

	public BigDecimal getCrdtTotal() {
		return crdtTotal==null?new BigDecimal("0.00"):crdtTotal;
	}

	public BigDecimal getCrdtFree(){
		return crdtFree==null?new BigDecimal("0.00"):crdtFree;
	}
	
	public BigDecimal getCrdtUsed(){
		return crdtUsed==null?new BigDecimal("0.00"):crdtUsed;
	}

	public BigDecimal getCrdtFrzd() {
		return crdtFrzd==null?new BigDecimal("0.00"):crdtFrzd;
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
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}


	public String getDealStatus() {
		return dealStatus;
	}


	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}









}