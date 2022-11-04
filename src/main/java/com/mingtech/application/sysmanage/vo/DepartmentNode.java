package com.mingtech.application.sysmanage.vo;

import com.mingtech.application.sysmanage.domain.Department;

/**
 * 类说明：机构树节点
 * @author huangshiqiang@
 * Jun 12, 2009
 */
public class DepartmentNode extends Department {

	private String text;

	private boolean leaf;


	public DepartmentNode(Department dept){
		this.setBankNumber(dept.getBankNumber());
		this.setOrgCode(dept.getOrgCode());
		this.setInnerBankCode(dept.getInnerBankCode());
		this.setDescription(dept.getDescription());
		this.setId(dept.getId());
		this.setIsOrg(dept.getIsOrg());
		this.setLevel(dept.getLevel());
		this.setName(dept.getName());
		this.setOrder(dept.getOrder());
		this.setParent(dept.getParent());
		this.setStatus(dept.getStatus());
		this.setTreeCode(dept.getTreeCode());
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
