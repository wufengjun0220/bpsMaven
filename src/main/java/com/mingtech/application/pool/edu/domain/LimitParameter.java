package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.pool.common.domain.Asset;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.sysmanage.domain.User;


public class LimitParameter {

	private PedProtocolDto protocol;
	
	//查询池额度用
	private AssetPool assetPool;
	private AssetType assetType;
	
	private CreditProduct creditProduct;
	/**
	额度-占用  ED_OPT_ZY
	额度-释放  ED_OPT_SF
	额度-冻结  ED_OPT_DJ
	额度-解冻  ED_OPT_JD
	资产_释放  ED_OPT_SF_ASSET
	 */
	private String useType;
	
	private List<Asset> assetlist = new ArrayList<Asset>();
	/**
	 * ED_10:票据衍生额度    ED_21:活期保证金
	 */
	private String assetDetailType;
	
	private String reason;

	private BigDecimal operAmt;
	
	private User user;
	

	public String getUseType() {
		return useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	public CreditProduct getCreditProduct() {
		return creditProduct;
	}

	public void setCreditProduct(CreditProduct creditProduct) {
		this.creditProduct = creditProduct;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}



	public BigDecimal getOperAmt() {
		return operAmt;
	}

	public void setOperAmt(BigDecimal operAmt) {
		this.operAmt = operAmt;
	}

	public AssetPool getAssetPool() {
		return assetPool;
	}

	public void setAssetPool(AssetPool assetPool) {
		this.assetPool = assetPool;
	}
	public Boolean isProtocolNull(){
		if(this.protocol==null || this.protocol.equals("")){
			return true;
		}else{
			return false;
		}
	}
	public Boolean isAssetPoolNull(){
		if(this.assetPool==null || this.assetPool.equals("")){
			return true;
		}else{
			return false;
		}
	}
	public Boolean isAssetTypeNull(){
		if(this.assetType==null || this.assetType.equals("")){
			return true;
		}else{
			return false;
		}
	}

	public PedProtocolDto getProtocol() {
		return protocol;
	}

	public void setProtocol(PedProtocolDto protocol) {
		this.protocol = protocol;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public List<Asset> getAssetlist() {
		return assetlist;
	}

	public void setAssetlist(List<Asset> assetlist) {
		this.assetlist = assetlist;
	}
	
	public void add(Asset a){
		this.assetlist.add(a);
	}
	
	public void addAll(List list){
		this.assetlist.addAll(list);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAssetDetailType() {
		return assetDetailType;
	}

	public void setAssetDetailType(String assetDetailType) {
		this.assetDetailType = assetDetailType;
	}
}
