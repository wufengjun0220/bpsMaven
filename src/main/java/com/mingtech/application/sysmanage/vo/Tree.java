/**
 * 
 */
package com.mingtech.application.sysmanage.vo;


/**
 * @author huboA
 * 
 */
public class Tree extends TreeNode {

	/**
	 * [{ id: 1, text: 'A leaf Node', leaf: true },{ id: 2, text: 'A folder
	 * Node', children: [{ id: 3, text: 'A child Node', leaf: true }] }]
	 */

	private String children;

	private boolean expanded;

	public String getChildren() {
		return children;
	}

	public void setChildren(String children) {
		this.children = children;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

}
