package com.mingtech.application.sysmanage.web;

import java.util.ArrayList;
import java.util.List;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.utils.ErrorCode;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.sysmanage.domain.ProductTypeDto;
import com.mingtech.application.sysmanage.service.ProductTypeService;
import com.mingtech.application.sysmanage.vo.TreeDepartment;
import com.mingtech.framework.common.util.JsonUtil;

@Controller
public class ProductTypeController extends BaseController {
	private static final Logger logger = Logger.getLogger(ProductTypeController.class);
	@Autowired
	private ProductTypeService productTypeService;
	@Autowired
	private CacheUpdateService cacheUpdateService;
	

	
	/**
	* 方法说明: 查询并展示产品树
	* @param pid 父产品id
	* @param sync 1同步、0异步
	* @author  h2
	* @return void
	* @date 2019-03-15 上午10:30:47
	*/
	@RequestMapping(value="/listTreeProduct")
	public void listTreeProduct(String pid,String sync){
		try {
			
			List products = productTypeService.getAllChildrens(pid,sync);
			List result=new ArrayList();
			for(int i=0;i<products.size();i++){
				ProductTypeDto product = (ProductTypeDto) products.get(i);
				TreeDepartment tree=new TreeDepartment();
				tree.setId(String.valueOf(product.getProduct_id()));
				tree.setPid(product.getSSupeprodtId());
				tree.setName(product.getSProdtName());
				result.add(tree);
			}
			this.sendJSON(JsonUtil.buildJson(result, 0));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON(e.getMessage());
		}
	}
}
