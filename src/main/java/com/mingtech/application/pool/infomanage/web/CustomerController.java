/**
 * 
 */
package com.mingtech.application.pool.infomanage.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.pool.online.manage.domain.PedOnlineBlackInfo;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @author wbyecheng
 *
 */
@Controller
public class CustomerController extends BaseController {
	private static final Logger logger = Logger.getLogger(CustomerController.class);
	@Autowired
	private CustomerService customerService;
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private CommonQueryService commonQueryService;
	/**
	 * 客户查询-wss 20210616
	 */
	@RequestMapping("queryCustomerInfoData")
	public void queryCustomerInfoData(CustomerDto customer) {
		Page page = this.getPage();
		String json = "";
		try {
			List list = customerService.query(customer, page);
			json = JsonUtil.buildJson(list, page.getTotalCount());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 同步客户信息-wss 20210616
	 */
	@RequestMapping("syncCustomerInfo")
	public void syncCustomerInfo(CustomerDto customer) {
		Page page = this.getPage();
		String json = "成功";
		try {
			//TODO 同步客户信息，更新业务表
			CommonQueryBean commonQueryBean =new CommonQueryBean();
			commonQueryBean.setCustNo(customer.getCoreCustomerCode());
			List list = commonQueryService.loadDebarList(commonQueryBean, this.getCurrentUser(), this.getPage());
               if(null!=list&&list.size()>0){
       			List lists = poolEBankService.queryCustomert(customer.getCoreCustomerCode());
       			if(null!=lists){
       				PedOnlineBlackInfo info=(PedOnlineBlackInfo)list.get(0);
           			PedProtocolDto dto= (PedProtocolDto)lists.get(0);
           			info.setCustName(dto.getCustname());
           			poolEBankService.txStore(info);
           			json = "成功";
       			}else{
       				json = "核心同步失败";
       			}
               }			

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("系统异常", e);
		}
		this.sendJSON(json);
	}
	
	
	
}
