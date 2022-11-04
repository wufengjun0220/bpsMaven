/**
 * 
 */
package com.mingtech.application.sysmanage.vo;


/**
 * @author huboA
 * 
 */
public class TreeNode {

	/**
	 * [{ id: 1, text: 'A leaf Node', leaf: true }] }]
	 */

	private String id;

	private String text;

	private boolean leaf;
	
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
