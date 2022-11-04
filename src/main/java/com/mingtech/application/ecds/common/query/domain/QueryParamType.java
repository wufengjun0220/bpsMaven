package com.mingtech.application.ecds.common.query.domain;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: yixiaolong
 * @日期: Sep 9, 2011 8:23:33 PM
 * @描述: 流水表 查询条件
 */

public class QueryParamType implements java.io.Serializable{

	/** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	
	private String bill_no;//票号
	private String billmedia;//票据介质
	private String sbranchid;//机构
	private String htStart;  //贴现起始日
	private String htEnd;  //贴现截止日
	private String sbilltype;//票据类型
	private String iSystemType;// 系统类型
	private String returngoudt;//回购到期日
	private String returnGDtSt;// 回购开始日
	private String returnGDtEnd;//回购截止日
	private String acceptBankName; //承兑行名称
	private String guashiName;//挂失人名称
	private String guashiDt;//挂失时间
	private String zhiquanName;//质权人名称
	private String chuzhiName; //出质人名称
	
	private String sortoptions;//排序选项
	private String isSysOuter;//系统类型（内、外）
	
	private String productTypeId;   //产品类型Id
	private String productTypeName;  //产品类型名称
	
	private String parentTypeId;    //上级产品类型Id
	private String productLevel;    //产品类型级别
	
	private String sisseamt_begin;//票面金额begin
	private String sisseamt_end;//票面金额end
	
	private String sissedt_begin;//出票日begin
	private String sissedt_end;//出票日end
	
	private String sduedt_begin;//到期日begin
	private String sduedt_end;//到期日end
	
	private String dscntrpddt_begin;//贴现日begin
	private String dscntrpddt_end;//贴现日end
	
	private String tuoshouBeginDt;  //托收时间范围(开始)
	private String tuoshouEndDt;//托收时间范围(结束)
	
	private String frate_begin;//利率begin
	private String frate_end;//利率end
	
	private String sBankNos; //机构大额行号集合
	private String returnGouDt_begin;//回购日期begin（电）
	private String returnGouDt_end;//回购日期end（电）
	
	private String tuoshouDt_begin;//托收时间begin
	private String tuoshouDt_end;//托收时间end
	
	private String recourseName;//追索人名称Recourse
	private String recoursecode;//追索人组织机构代码
	
	private String customName;//客户名称
	private String custManagerName;//客户经理名称
	public String getCustManagerName() {
		return custManagerName;
	}
	public void setCustManagerName(String custManagerName) {
		this.custManagerName = custManagerName;
	}
	public String getBill_no() {
		return bill_no;
	}
	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	public String getBillmedia() {
		return billmedia;
	}
	public void setBillmedia(String billmedia) {
		this.billmedia = billmedia;
	}
	public String getSbranchid() {
		return sbranchid;
	}
	public void setSbranchid(String sbranchid) {
		this.sbranchid = sbranchid;
	}
	public String getHtStart() {
		return htStart;
	}
	public void setHtStart(String htStart) {
		this.htStart = htStart;
	}
	public String getHtEnd() {
		return htEnd;
	}
	public void setHtEnd(String htEnd) {
		this.htEnd = htEnd;
	}
	public String getSbilltype() {
		return sbilltype;
	}
	public void setSbilltype(String sbilltype) {
		this.sbilltype = sbilltype;
	}
	public String getISystemType() {
		return iSystemType;
	}
	public void setISystemType(String systemType) {
		iSystemType = systemType;
	}
	public String getReturngoudt() {
		return returngoudt;
	}
	public void setReturngoudt(String returngoudt) {
		this.returngoudt = returngoudt;
	}
	public String getReturnGDtSt() {
		return returnGDtSt;
	}
	public void setReturnGDtSt(String returnGDtSt) {
		this.returnGDtSt = returnGDtSt;
	}
	public String getReturnGDtEnd() {
		return returnGDtEnd;
	}
	public void setReturnGDtEnd(String returnGDtEnd) {
		this.returnGDtEnd = returnGDtEnd;
	}
	public String getAcceptBankName() {
		return acceptBankName;
	}
	public void setAcceptBankName(String acceptBankName) {
		this.acceptBankName = acceptBankName;
	}
	public String getGuashiName() {
		return guashiName;
	}
	public void setGuashiName(String guashiName) {
		this.guashiName = guashiName;
	}
	public String getGuashiDt() {
		return guashiDt;
	}
	public void setGuashiDt(String guashiDt) {
		this.guashiDt = guashiDt;
	}
	public String getZhiquanName() {
		return zhiquanName;
	}
	public void setZhiquanName(String zhiquanName) {
		this.zhiquanName = zhiquanName;
	}
	public String getChuzhiName() {
		return chuzhiName;
	}
	public void setChuzhiName(String chuzhiName) {
		this.chuzhiName = chuzhiName;
	}
	public String getSortoptions() {
		return sortoptions;
	}
	public void setSortoptions(String sortoptions) {
		this.sortoptions = sortoptions;
	}
	public String getIsSysOuter() {
		return isSysOuter;
	}
	public void setIsSysOuter(String isSysOuter) {
		this.isSysOuter = isSysOuter;
	}
	public String getProductTypeId() {
		return productTypeId;
	}
	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getParentTypeId() {
		return parentTypeId;
	}
	public void setParentTypeId(String parentTypeId) {
		this.parentTypeId = parentTypeId;
	}
	public String getProductLevel() {
		return productLevel;
	}
	public void setProductLevel(String productLevel) {
		this.productLevel = productLevel;
	}
	public String getSisseamt_begin() {
		return sisseamt_begin;
	}
	public void setSisseamt_begin(String sisseamt_begin) {
		this.sisseamt_begin = sisseamt_begin;
	}
	public String getSisseamt_end() {
		return sisseamt_end;
	}
	public void setSisseamt_end(String sisseamt_end) {
		this.sisseamt_end = sisseamt_end;
	}
	public String getSissedt_begin() {
		return sissedt_begin;
	}
	public void setSissedt_begin(String sissedt_begin) {
		this.sissedt_begin = sissedt_begin;
	}
	public String getSissedt_end() {
		return sissedt_end;
	}
	public void setSissedt_end(String sissedt_end) {
		this.sissedt_end = sissedt_end;
	}
	public String getSduedt_begin() {
		return sduedt_begin;
	}
	public void setSduedt_begin(String sduedt_begin) {
		this.sduedt_begin = sduedt_begin;
	}
	public String getSduedt_end() {
		return sduedt_end;
	}
	public void setSduedt_end(String sduedt_end) {
		this.sduedt_end = sduedt_end;
	}
	public String getDscntrpddt_begin() {
		return dscntrpddt_begin;
	}
	public void setDscntrpddt_begin(String dscntrpddt_begin) {
		this.dscntrpddt_begin = dscntrpddt_begin;
	}
	public String getDscntrpddt_end() {
		return dscntrpddt_end;
	}
	public void setDscntrpddt_end(String dscntrpddt_end) {
		this.dscntrpddt_end = dscntrpddt_end;
	}
	public String getFrate_begin() {
		return frate_begin;
	}
	public void setFrate_begin(String frate_begin) {
		this.frate_begin = frate_begin;
	}
	public String getFrate_end() {
		return frate_end;
	}
	public void setFrate_end(String frate_end) {
		this.frate_end = frate_end;
	}
	public String getSBankNos() {
		return sBankNos;
	}
	public void setSBankNos(String bankNos) {
		sBankNos = bankNos;
	}
	public String getReturnGouDt_begin() {
		return returnGouDt_begin;
	}
	public void setReturnGouDt_begin(String returnGouDt_begin) {
		this.returnGouDt_begin = returnGouDt_begin;
	}
	public String getReturnGouDt_end() {
		return returnGouDt_end;
	}
	public void setReturnGouDt_end(String returnGouDt_end) {
		this.returnGouDt_end = returnGouDt_end;
	}
	public String getTuoshouDt_begin() {
		return tuoshouDt_begin;
	}
	public void setTuoshouDt_begin(String tuoshouDt_begin) {
		this.tuoshouDt_begin = tuoshouDt_begin;
	}
	public String getTuoshouDt_end() {
		return tuoshouDt_end;
	}
	public void setTuoshouDt_end(String tuoshouDt_end) {
		this.tuoshouDt_end = tuoshouDt_end;
	}
	public String getRecourseName() {
		return recourseName;
	}
	public void setRecourseName(String recourseName) {
		this.recourseName = recourseName;
	}
	public String getRecoursecode() {
		return recoursecode;
	}
	public void setRecoursecode(String recoursecode) {
		this.recoursecode = recoursecode;
	}
	public String getTuoshouBeginDt() {
		return tuoshouBeginDt;
	}
	public void setTuoshouBeginDt(String tuoshouBeginDt) {
		this.tuoshouBeginDt = tuoshouBeginDt;
	}
	public String getTuoshouEndDt() {
		return tuoshouEndDt;
	}
	public void setTuoshouEndDt(String tuoshouEndDt) {
		this.tuoshouEndDt = tuoshouEndDt;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
}