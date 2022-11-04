package com.mingtech.application.runmanage.domain;


/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: huangshiqiang
* @日期: Dec 2, 2009 4:53:06 PM
* @描述: [SystemConfig]系统配置项实体类
*/
public class SystemConfig{
	private String id;//主键
	private String code;//码
	private String item;//项
	private String type;//类型 01 文档管理类 02 系统管理类 03 运行管理类、04接口对接
	private String descrip;//说明
	
	private String typeName; //类型中文名称

	/**
	* <p>方法名称: getId|描述:主键 </p>
	* @return
	*/
	public String getId(){
		return id;
	}
	
	/**
	* <p>方法名称: setId|描述:主键 </p>
	* @param id
	*/
	public void setId(String id){
		this.id = id;
	}
	
	/**
	* <p>方法名称: getCode|描述:码 </p>
	* @return
	*/
	public String getCode(){
		return code;
	}
	
	/**
	* <p>方法名称: setCode|描述:码 </p>
	* @param code
	*/
	public void setCode(String code){
		this.code = code;
	}
	
	/**
	* <p>方法名称: getDescrip|描述:说明 </p>
	* @return
	*/
	public String getDescrip(){
		return descrip;
	}

	
	/**
	* <p>方法名称: setDescrip|描述:说明 </p>
	* @param descrip
	*/
	public void setDescrip(String descrip){
		this.descrip = descrip;
	}

	/**
	* <p>方法名称: getItem|描述:项 </p>
	* @return
	*/
	public String getItem(){
		return item;
	}

	
	/**
	* <p>方法名称: setItem|描述:项 </p>
	* @param item
	*/
	public void setItem(String item){
		this.item = item;
	}

	
	/**
	* <p>方法名称: getType|描述: 类型</p>
	* @return
	*/
	public String getType(){
		return type;
	}

	
	/**
	* <p>方法名称: setType|描述:类型 </p>
	* @param type
	*/
	public void setType(String type){
		this.type = type;
	}

	public String getTypeName() {
		String str = "";
		if("01".equals(this.getType())){
			str = "文档管理类-01";
		}else if("02".equals(this.getType())){
			str = "系统管理类-02";
		}else if("03".equals(this.getType())){
			str = "运行管理类-03";
		}else if("04".equals(this.getType())){
			str = "接口对接类-04";
		}
		return str;
		
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	
	
	
}
