package com.mingtech.application.sysmanage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mingtech.application.sysmanage.domain.ProductTypeDto;
import com.mingtech.application.sysmanage.service.ProductTypeService;
import com.mingtech.application.sysmanage.vo.Tree;
import com.mingtech.application.sysmanage.vo.TreeNode;
import com.mingtech.framework.common.util.CollectionUtil;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductTypeServiceImpl extends GenericServiceImpl implements ProductTypeService{

	public String getProductTypesJSON(ProductTypeDto productType, Page page) throws Exception {
		List resources = this.getProductTypes(productType,page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(resources, map);
	}
	
	/**
	 * 分页返回所有角色
	 * @param roleManager
	 * @return
	 * @throws Exception
	 */
	public List getProductTypes(ProductTypeDto productType, Page page) throws Exception {
		List paras = new ArrayList();
		String expression = " select prod from ProductTypeDto as prod where 1=1 ";
		if (StringUtil.isNotBlank(productType.getSProdtName())) {
			expression += " and prod.SProdtName like ?";
			paras.add("%" + productType.getSProdtName() + "%");
		}
		return find(expression, paras, page);
	}
	
	public List getAllChildren(String pid) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (null == pid) {
			sb.append("from ProductTypeDto prod WHERE prod.SSupeprodtId is null");
		} else {
			sb.append("from ProductTypeDto prod WHERE prod.SSupeprodtId ='" + pid + "'");
		}
		sb.append(" order by prod.SProdtName ASC");
		return this.find(sb.toString());
	}
	/**
	 * 获取所有产品列表
	 * @return
	 * @throws Exception
	 */
	public List getAllProductList() {
		StringBuffer sb = new StringBuffer();
		sb.append("from ProductTypeDto prod ");
		sb.append(" order by prod.product_id ASC");
		return this.find(sb.toString());
	}

	/**
	 * 2017  ddp 修改为 查询 电票、纸票
	 * @return
	 */
	public List getPcdsChildren(){
		String sql = " from  ProductTypeDto prod WHERE  prod.SSupeprodtId is null and prod.product_id in ('1','2','3','4','5','7','8','9')";
		return this.find(sql);
	}
	public String getProducts(Tree tree) throws Exception {
		List children = null;
		if (StringUtil.equals(tree.getId(), "-1") ||StringUtil.equals(tree.getId(), "-2")) {// 取根节点
			if(StringUtil.equals(tree.getId(), "-1")){
			children = this.getAllChildren(null);
			}else{
				//纸票利率管理 产品
				children = getPcdsChildren();
			}
		} else {
			ProductTypeDto product = (ProductTypeDto) this.dao.load(ProductTypeDto.class,tree.getId());
			children = this.getAllChildren(product.getProduct_id());
		}
		StringBuffer sb = new StringBuffer();
		if (CollectionUtil.isEmpty(children)) {
			tree.setLeaf(true);
		} else {
			List list = new ArrayList();
			for (int i = 0; i < children.size(); i++) {
				ProductTypeDto product = (ProductTypeDto) children.get(i);
				TreeNode tmp = new TreeNode();
				tmp.setText(product.getSProdtName());
				tmp.setId(product.getProduct_id());
				if (CollectionUtil.isEmpty(this.getAllChildren(product.getProduct_id()))) {
					tmp.setLeaf(true);
				}
				list.add(tmp);
			}
			sb.append(JsonUtil.fromCollections(list));
		}
		return sb.toString();
	}

	@Override
	public List getAllChildrens(String pid, String sync) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (StringUtil.isBlank(pid)) {
			if("1".equals(sync)){
				sb.append("from ProductTypeDto prod");
			}else{
				sb.append("from ProductTypeDto prod WHERE prod.SSupeprodtId ='-1' ");
			}
		} else {
			sb.append("from ProductTypeDto prod WHERE prod.SSupeprodtId ='" + pid + "'");
		}
		sb.append(" order by prod.product_id ");
		return this.find(sb.toString());
	}


	public Class getEntityClass() {
		return ProductTypeDto.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(getEntityClass());
	}

}
