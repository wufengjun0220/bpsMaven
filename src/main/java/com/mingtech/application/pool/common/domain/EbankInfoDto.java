package com.mingtech.application.pool.common.domain;



/**
 * 网银人员名单
 * @author yy
 *
 */
public class EbankInfoDto implements java.io.Serializable{

	/**
	 * Dto属性
	 */
	// 1.主键id
	private String ebankId;
	// 2.网银人员姓名 
	private String ebankName;
	// 3.网银人员证件号
	private String ebankPeopleCard;
	// 4.人员名单归属客户名称 
	private String ebankCustName;
	// 5.人员名单归属客户组织结构代码
	private String ebankPlCommId;
	//6.人员名单归属客户核心号
	private String ebankCustNum;
	//7.客户信息表id
	private String ebankCustId;
	//8.网银人员证件类型
	private String ebankType;
	
	//证件类型中文名称
	private String ebankTypeName;
	
	
	
	/**
	 * set/get
	 */
	public String getEbankId() {
		return ebankId;
	}
	public void setEbankId(String ebankId) {
		this.ebankId = ebankId;
	}
	public String getEbankName() {
		return ebankName;
	}
	public void setEbankName(String ebankName) {
		this.ebankName = ebankName;
	}
	public String getEbankPeopleCard() {
		return ebankPeopleCard;
	}
	public void setEbankPeopleCard(String ebankPeopleCard) {
		this.ebankPeopleCard = ebankPeopleCard;
	}
	public String getEbankCustName() {
		return ebankCustName;
	}
	public void setEbankCustName(String ebankCustName) {
		this.ebankCustName = ebankCustName;
	}
	public String getEbankPlCommId() {
		return ebankPlCommId;
	}
	public void setEbankPlCommId(String ebankPlCommId) {
		this.ebankPlCommId = ebankPlCommId;
	}
	public String getEbankCustNum() {
		return ebankCustNum;
	}
	public void setEbankCustNum(String ebankCustNum) {
		this.ebankCustNum = ebankCustNum;
	}
	public String getEbankCustId() {
		return ebankCustId;
	}
	public void setEbankCustId(String ebankCustId) {
		this.ebankCustId = ebankCustId;
	}
	public String getEbankType() {
		return ebankType;
	}
	public void setEbankType(String ebankType) {
		this.ebankType = ebankType;
	}
	/**
	 * 证件类型jsp自定义显示
	 * @return
	 */
	public String getEbankTypeName() {
		String name = "";
		if ("01".equals(this.getEbankType())) {
			name = "居民身份证";
		} else if ("02".equals(this.getEbankType())) {
			name = "解放军军官证";
		} else if ("03".equals(this.getEbankType())) {
			name = "户口簿";
		}else if ("04".equals(this.getEbankType())) {
			name = "武警警官证";
		}else if ("05".equals(this.getEbankType())) {
			name = "解放军士兵证";
		}else if ("06".equals(this.getEbankType())) {
			name = "中国护照";
		}else if ("07".equals(this.getEbankType())) {
			name = "港澳居民来往内地通行证";
		}else if ("08".equals(this.getEbankType())) {
			name = "解放军文职干部证";
		}else if ("09".equals(this.getEbankType())) {
			name = "边民出入境通行证";
		}else if ("10".equals(this.getEbankType())) {
			name = "外国人永久居留证";
		}else if ("16".equals(this.getEbankType())) {
			name = "解放军离休干部荣誉证";
		}else if ("24".equals(this.getEbankType())) {
			name = "解放军军官退休证";
		}else if ("25".equals(this.getEbankType())) {
			name = "解放军文职干部退休证";
		}else if ("26".equals(this.getEbankType())) {
			name = "军事院校学员证";
		}else if ("31".equals(this.getEbankType())) {
			name = "武警士兵证";
		}else if ("32".equals(this.getEbankType())) {
			name = "武警离休干部荣誉证";
		}else if ("33".equals(this.getEbankType())) {
			name = "武警文职干部证";
		}else if ("34".equals(this.getEbankType())) {
			name = "武警军官退休证";
		}else if ("35".equals(this.getEbankType())) {
			name = "武警文职干部退休证";
		}else if ("39".equals(this.getEbankType())) {
			name = "台湾居民来往大陆通行证";
		}else if ("40".equals(this.getEbankType())) {
			name = "外国护照";
		}else if ("49".equals(this.getEbankType())) {
			name = "其他(个人)";
		} 
		return name;
	}
		
	public void setEbankTypeName(String ebankTypeName) {
		this.ebankTypeName = ebankTypeName;
	}
	
	
	
	
	

	
}



