package com.mingtech.application.sysmanage.domain;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: yufei
 * @日期: Jun 2, 2009 9:53:12 AM
 * @描述: [ProductTypeDto]产品类型对象记录系统中所有的产品记录(卖断;卖出回购;再贴现;买断;买入返售;直贴等)
 */
public class ProductTypeDto implements java.io.Serializable{

	/** serialVersionUID*/
	private static final long serialVersionUID = -5286999000731251568L;
	// Fields
	private String product_id;// 产品ID
	private String SProdtName;// 产品名称
	private String SSupeprodtId;// 上级产品ID
	private String SProdtDesc;// 产品描述
    private String SProdtNumber;//产品编号

	// Constructors
	/** default constructor */
	public ProductTypeDto(){
	}

	/** full constructor */
	public ProductTypeDto(String SProdtName, String SSupeprodtId,
			String SProdtDesc){
		this.SProdtName = SProdtName;
		this.SSupeprodtId = SSupeprodtId;
		this.SProdtDesc = SProdtDesc;
	}


	/**
	 * <p>方法名称: getSProdtName|描述:获取产品名称 </p>
	 * @return
	 */
	public String getSProdtName(){
		return this.SProdtName;
	}

	/**
	 * <p>方法名称: setSProdtName|描述:set产品名称 </p>
	 * @param SProdtName
	 */
	public void setSProdtName(String SProdtName){
		this.SProdtName = SProdtName;
	}

	/**
	 * <p>方法名称: getSSupeprodtId|描述:获取上级产品ID </p>
	 * @return
	 */
	public String getSSupeprodtId(){
		return this.SSupeprodtId;
	}

	/**
	 * <p>方法名称: setSSupeprodtId|描述:set上级产品ID </p>
	 * @param SSupeprodtId
	 */
	public void setSSupeprodtId(String SSupeprodtId){
		this.SSupeprodtId = SSupeprodtId;
	}

	/**
	 * <p>方法名称: getSProdtDesc|描述: 获取对产品描述内容</p>
	 * @return
	 */
	public String getSProdtDesc(){
		return this.SProdtDesc;
	}

	/**
	 * <p>方法名称: setSProdtDesc|描述:set产品描述内容 </p>
	 * @param SProdtDesc
	 */
	public void setSProdtDesc(String SProdtDesc){
		this.SProdtDesc = SProdtDesc;
	}

	/**
	 *
	* <p>方法名称: getSProdtNumber|描述: 获取产品编号</p>
	* @return
	 */
	public String getSProdtNumber(){
		return SProdtNumber;
	}


	public void setSProdtNumber(String prodtNumber){
		SProdtNumber = prodtNumber;
	}


	public String getProduct_id(){
		return product_id;
	}


	public void setProduct_id(String product_id){
		this.product_id = product_id;
	}



}