package com.mingtech.application.sysmanage.domain;

import java.util.Comparator;
import java.util.List;

/**
 * 资源实体
 * @author huboA
 * @hibernate.class table="t_resource"
 * @hibernate.cache usage="read-write"
 *
 */
public class Resource implements Comparator,java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final int menu = 5;
	public static final int operation = 10;
	private String id;					//资源ID
	private String name;				//资源名称
	private String desc = "";           //资源描述
	private String code = "";           //资源代码--只允许英文字母
	private int type = 10;              //资源类型 5 menu|菜单 10 operation|操作(其它如file有待扩展)
	private int order = 0;              //资源序号
	private String actionName = "";     //资源调用的Action名称
	private String url = "";            //menu菜单对应的链接
	private Resource parent;			//父资源
	private String pid;
	private List roleList;//角色列表
	
	
	private String iconCss ;//图标
	private int isShow = 0;//是否显示主页 1是 0不是
	private String showName;//显示主页名称
	private String rootId;//最上级 菜单id
	private int sort = 0;// 类别  0初始 1 电票 2 纸票 3票据池
	
	/**
	 * 资源ID
	 *
	 * @hibernate.id generator-class="uuid" type="string" length="50"
	 *               column="id"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 资源名称
	 *
	 * @hibernate.property type="string" length="100" column="r_name"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 资源描述
	 *
	 * @hibernate.property type="string" length="500" column="r_desc"
	 */
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * 资源代码
	 *
	 * @hibernate.property type="string" length="100" column="r_code"
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 资源类型
	 *
	 * @hibernate.property type="int" column="r_type" length="8"
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 资源序号
	 *
	 * @hibernate.property type="int" column="r_order" length="8"
	 */
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	/**
	 * 资源调用的Action名称
	 *
	 * @hibernate.property type="string" length="500" column="r_action"
	 */
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	/**
	 * menu菜单对应的链接
	 *
	 * @hibernate.property type="string" length="500" column="r_url"
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 父资源
	 * @hibernate.many-to-one column="r_pid" lazy="proxy"
	 *                        class="com.mingtech.application.sysmanage.domain.Resource"
	 *                        not-null="false"
	 */
	public Resource getParent() {
		return parent;
	}

	public void setParent(Resource parent) {
		this.parent = parent;
	}
	
	

	public String getPid() {
		if(parent != null) {
			return parent.getId();
		} else {
			return pid;
		}
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public int getLevel(){
		return (Integer.toString(this.order).length())/2;
	}

	public int compare(Object o1, Object o2) {
		Resource res1 = (Resource)o1;
		Resource res2 = (Resource)o2;
		//int result = Integer.toString(res1.getOrder()).compareTo(Integer.toString(res2.getOrder()));
		boolean flag= res1.getOrder()>=res2.getOrder();
		if(flag)
			return 1;
		return 0;
	}

	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}else{
			return this.id.equals(((Resource)obj).getId());
		}
	}


	public List getRoleList(){
		return roleList;
	}


	public void setRoleList(List roleList){
		this.roleList = roleList;
	}

	public String getIconCss() {
		return iconCss;
	}

	public void setIconCss(String iconCss) {
		this.iconCss = iconCss;
	}

	public int getIsShow() {
		return isShow;
	}

	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	
	
	
}
