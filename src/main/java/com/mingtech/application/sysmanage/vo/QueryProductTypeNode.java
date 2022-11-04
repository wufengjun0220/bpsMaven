package com.mingtech.application.sysmanage.vo;

import com.mingtech.application.ecds.common.query.domain.QueryProductType;


/**
 * 类说明：机构树节点
 * @author huangshiqiang@
 * Jun 12, 2009
 */
public class QueryProductTypeNode extends QueryProductType {

	private static final long serialVersionUID = 1L;

	private String text;

	private boolean leaf;

	private String proNumber;
	
	public QueryProductTypeNode(QueryProductType dept){
		this.setId(dept.getId());
		this.setProductTypeName(dept.getProductTypeName());
		this.setParentTypeId(dept.getParentTypeId());
		this.setProductLevel(dept.getProductLevel());
	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public String getProNumber() {
		return proNumber;
	}
	public void setProNumber(String proNumber) {
		this.proNumber = proNumber;
	}
}
