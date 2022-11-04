package com.mingtech.application.ecds.common.query.domain;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: yixiaolong
 * @日期: Sep 9, 2011 8:23:33 PM
 * @描述: [CalTanxiaoDto]计提摊销对象
 */

public class QueryProductType implements java.io.Serializable{

	/** serialVersionUID*/
	private static final long serialVersionUID = 1L;
	
	private String id;   //产品类型Id
	private String productTypeName;  //产品类型名称
	private String parentTypeId;    //上级产品类型Id
	private String productLevel;    //产品类型级别
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
}