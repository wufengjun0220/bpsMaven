package com.mingtech.application.pool.base.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 二代行名行号表
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-12
 */

public class BoCcmsPartyinf implements java.io.Serializable {

	// Fields
	private String id;         //
	private Date etlDate;      //
	private String etlSys;     //
	private String etlMd5;     //
	private String prcptcd;    //参与机构行号               
	private String prcpttp;    //参与机构级别               
	private String bkctgrycd;  //行别代码                   
	private String subdrtbkcd; //所属清算行行号      票据池系统使用该行号为  上级行号！          
	private String sublglprsn; //所属法人                   
	private String hgrptcpt;   //【本字段实际不用】本行上级参与机构   上级行号        
	private String barbkcd;    //承接行行号                 
	private String chrgbkcd;   //管辖人行行号               
	private String subccpccd;  //所属CCPC                   
	private String citycd;     //所在城市代码               
	private String ptcptnm;    //参与机构全称               
	private String ptcptshrtnm;//参与机构简称               
	private String jntxsyssgn; //排雷顺序按从左到右为大额   
	private String adr;        //地址                       
	private String pstcd;      //邮编                       
	private String tel;        //电话/点挂                  
	private String email;      //电子邮件地址               
	private String expdate;    //注销日期                   
	private String rmkinf;     //备注                       
	private String upddt;      //更新日期                   
	private String updtm;      //更新时间                   
	private String mkinfo1;    //备用字段1                  
	private String mkinfo2;    //备用字段2                  
	private Timestamp createts;//
	private Timestamp updatets;//

	// Constructors

	/** default constructor */
	public BoCcmsPartyinf() {
	}

	/** minimal constructor */
	public BoCcmsPartyinf(Date etlDate, String etlSys) {
		this.etlDate = etlDate;
		this.etlSys = etlSys;
	}

	/** full constructor */
	public BoCcmsPartyinf(Date etlDate, String etlSys, String etlMd5,
			String prcptcd, String prcpttp, String bkctgrycd,
			String subdrtbkcd, String sublglprsn, String hgrptcpt,
			String barbkcd, String chrgbkcd, String subccpccd, String citycd,
			String ptcptnm, String ptcptshrtnm, String jntxsyssgn, String adr,
			String pstcd, String tel, String email, String expdate,
			String rmkinf, String upddt, String updtm, String mkinfo1,
			String mkinfo2, Timestamp createts, Timestamp updatets) {
		this.etlDate = etlDate;
		this.etlSys = etlSys;
		this.etlMd5 = etlMd5;
		this.prcptcd = prcptcd;
		this.prcpttp = prcpttp;
		this.bkctgrycd = bkctgrycd;
		this.subdrtbkcd = subdrtbkcd;
		this.sublglprsn = sublglprsn;
		this.hgrptcpt = hgrptcpt;
		this.barbkcd = barbkcd;
		this.chrgbkcd = chrgbkcd;
		this.subccpccd = subccpccd;
		this.citycd = citycd;
		this.ptcptnm = ptcptnm;
		this.ptcptshrtnm = ptcptshrtnm;
		this.jntxsyssgn = jntxsyssgn;
		this.adr = adr;
		this.pstcd = pstcd;
		this.tel = tel;
		this.email = email;
		this.expdate = expdate;
		this.rmkinf = rmkinf;
		this.upddt = upddt;
		this.updtm = updtm;
		this.mkinfo1 = mkinfo1;
		this.mkinfo2 = mkinfo2;
		this.createts = createts;
		this.updatets = updatets;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getEtlDate() {
		return this.etlDate;
	}

	public void setEtlDate(Date etlDate) {
		this.etlDate = etlDate;
	}

	public String getEtlSys() {
		return this.etlSys;
	}

	public void setEtlSys(String etlSys) {
		this.etlSys = etlSys;
	}

	public String getEtlMd5() {
		return this.etlMd5;
	}

	public void setEtlMd5(String etlMd5) {
		this.etlMd5 = etlMd5;
	}

	public String getPrcptcd() {
		return this.prcptcd;
	}

	public void setPrcptcd(String prcptcd) {
		this.prcptcd = prcptcd;
	}

	public String getPrcpttp() {
		return this.prcpttp;
	}

	public void setPrcpttp(String prcpttp) {
		this.prcpttp = prcpttp;
	}

	public String getBkctgrycd() {
		return this.bkctgrycd;
	}

	public void setBkctgrycd(String bkctgrycd) {
		this.bkctgrycd = bkctgrycd;
	}

	public String getSubdrtbkcd() {
		return this.subdrtbkcd;
	}

	public void setSubdrtbkcd(String subdrtbkcd) {
		this.subdrtbkcd = subdrtbkcd;
	}

	public String getSublglprsn() {
		return this.sublglprsn;
	}

	public void setSublglprsn(String sublglprsn) {
		this.sublglprsn = sublglprsn;
	}

	public String getHgrptcpt() {
		return this.hgrptcpt;
	}

	public void setHgrptcpt(String hgrptcpt) {
		this.hgrptcpt = hgrptcpt;
	}

	public String getBarbkcd() {
		return this.barbkcd;
	}

	public void setBarbkcd(String barbkcd) {
		this.barbkcd = barbkcd;
	}

	public String getChrgbkcd() {
		return this.chrgbkcd;
	}

	public void setChrgbkcd(String chrgbkcd) {
		this.chrgbkcd = chrgbkcd;
	}

	public String getSubccpccd() {
		return this.subccpccd;
	}

	public void setSubccpccd(String subccpccd) {
		this.subccpccd = subccpccd;
	}

	public String getCitycd() {
		return this.citycd;
	}

	public void setCitycd(String citycd) {
		this.citycd = citycd;
	}

	public String getPtcptnm() {
		return this.ptcptnm;
	}

	public void setPtcptnm(String ptcptnm) {
		this.ptcptnm = ptcptnm;
	}

	public String getPtcptshrtnm() {
		return this.ptcptshrtnm;
	}

	public void setPtcptshrtnm(String ptcptshrtnm) {
		this.ptcptshrtnm = ptcptshrtnm;
	}

	public String getJntxsyssgn() {
		return this.jntxsyssgn;
	}

	public void setJntxsyssgn(String jntxsyssgn) {
		this.jntxsyssgn = jntxsyssgn;
	}

	public String getAdr() {
		return this.adr;
	}

	public void setAdr(String adr) {
		this.adr = adr;
	}

	public String getPstcd() {
		return this.pstcd;
	}

	public void setPstcd(String pstcd) {
		this.pstcd = pstcd;
	}

	public String getTel() {
		return this.tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getExpdate() {
		return this.expdate;
	}

	public void setExpdate(String expdate) {
		this.expdate = expdate;
	}

	public String getRmkinf() {
		return this.rmkinf;
	}

	public void setRmkinf(String rmkinf) {
		this.rmkinf = rmkinf;
	}

	public String getUpddt() {
		return this.upddt;
	}

	public void setUpddt(String upddt) {
		this.upddt = upddt;
	}

	public String getUpdtm() {
		return this.updtm;
	}

	public void setUpdtm(String updtm) {
		this.updtm = updtm;
	}

	public String getMkinfo1() {
		return this.mkinfo1;
	}

	public void setMkinfo1(String mkinfo1) {
		this.mkinfo1 = mkinfo1;
	}

	public String getMkinfo2() {
		return this.mkinfo2;
	}

	public void setMkinfo2(String mkinfo2) {
		this.mkinfo2 = mkinfo2;
	}

	public Timestamp getCreatets() {
		return this.createts;
	}

	public void setCreatets(Timestamp createts) {
		this.createts = createts;
	}

	public Timestamp getUpdatets() {
		return this.updatets;
	}

	public void setUpdatets(Timestamp updatets) {
		this.updatets = updatets;
	}

}