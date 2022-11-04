package com.mingtech.application.pool.infomanage.domain;

public class CustomerDtoNode extends CustomerDto {
    private String id;
    private String text;

	private boolean leaf;
	
	public CustomerDtoNode(CustomerDto cust){
		this.setId(cust.getPkIxBoCustomerId());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
}
