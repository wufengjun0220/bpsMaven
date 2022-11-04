package com.mingtech.application.pool.common.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.domain.EbankInfoDto;
import com.mingtech.application.pool.common.service.EbankService;
import com.mingtech.application.pool.infomanage.domain.CustomerDto;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

@Controller
public class EbankController extends BaseController{
	private static final Logger logger = Logger.getLogger(EbankController.class);

	@Autowired
	private EbankService ebankService;

	/**
	 * 跳转到网银人员名单展示页面；ListEbank.jap
	 */
	@RequestMapping("/ebankInfoShow")
	public String ebankInfoShow() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/listEbankInfo";
	}
	
	/**
	 * 跳转到网银人员名单增加；addEbank.jap
	 */
	@RequestMapping("/addEbank")
	public String addEbank() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/addEbank";
	}

	/**
	 * 向前台输出json串
	 * @param info
	 */
	@RequestMapping("/listEbankJson")
	public void list(EbankInfoDto info) {
		String json = "";
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			json = ebankService.loadEbankJson(info, user,page);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		sendJSON(json);
	}
	
	/**
	 * 新增方法
	 * @param info
	 * @param mode
	 * @return
	 */
	@RequestMapping("/saveEbank")
	public String saveEbank(EbankInfoDto info, Model mode) {
		CustomerDto customer =null;
		EbankInfoDto  ebankInfoDto = new EbankInfoDto();
		ebankInfoDto.setEbankName(info.getEbankName());
		ebankInfoDto.setEbankPeopleCard(info.getEbankPeopleCard());
		ebankInfoDto.setEbankCustName(info.getEbankCustName());
		ebankInfoDto.setEbankPlCommId(info.getEbankPlCommId());
		ebankInfoDto.setEbankCustNum(info.getEbankCustNum());
		ebankInfoDto.setEbankType(info.getEbankType());
        /**
         * 新增网银人员名单时,根据客户组织结构代码,核心号,准确查询客户表,保存客户表id
         */
		customer = ebankService.queryCustomerDtoByEbankParm(info.getEbankPlCommId(), info.getEbankCustNum());
		if(customer !=null){
			ebankInfoDto.setEbankCustId(customer.getPkIxBoCustomerId());
		}
		ebankService.txStore(ebankInfoDto);
		
		return "/pool/common/listEbankInfo";	
}

	
	
	/**
	 * 跳转到编辑页面
	 * @return
	 */
	@RequestMapping("/editEbank")
	public String editEbank(EbankInfoDto ebankInfoDto, Model mode){
		try {
			String id =ebankInfoDto.getEbankId();
			String hql="from EbankInfoDto info where info.ebankId ='"+id+"'";
			 List find = ebankService.find(hql);
			 ebankInfoDto = (EbankInfoDto)find.get(0);
			 mode.addAttribute("ebankInfoDto", ebankInfoDto);		 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/editEbank";
	}
	
	/**
	 * 编辑方法
	 * @param info
	 * @param mode
	 * @return
	 */
	@RequestMapping("/editEbankOper")
	public String editEbankOper(EbankInfoDto info, Model mode) {
		CustomerDto customer =null;
		customer = ebankService.queryCustomerDtoByEbankParm(info.getEbankPlCommId(), info.getEbankCustNum());
		if(customer !=null){
			info.setEbankCustId(customer.getPkIxBoCustomerId());
		}
		ebankService.txStore(info);

		return "/pool/common/listEbankInfo";	
}
	
	/**
	 * 删除实体对象
	 * @param info
	 * @param mode
	 * @return
	 */
	@RequestMapping("/deleteEbank")
	public String deleteEbank(EbankInfoDto info, Model mode) {
		EbankInfoDto ebankInfoDto=null;
		ebankInfoDto = (EbankInfoDto) ebankService.load(info.getEbankId());		
		ebankService.txDelete(ebankInfoDto);

		return "/pool/common/listEbankInfo";	
}
	
	
	/**
	 * 输入客户号后加载信息
	 * param1:组织结构代码
	 */
	@RequestMapping("/EbankQueryCustomer")
	public void EbankQueryCustomer(String ebankPlCommId){
		String json = "[]";
		CustomerDto customerDto;
		try {
			customerDto = this.findCustomerByCustNumber(ebankPlCommId);
			if (StringUtil.isNotEmpty(customerDto.getSCustName()) && StringUtil.isNotEmpty(customerDto.getCustNum())) {
				json = "[" + JsonUtil.fromObject(customerDto) + "]";
			}
		} catch (Exception e) {			
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	
	/**
	 * 输入客户号后加载信息
	 * @param 客户号
	 * @throws Exception
	 */
	public CustomerDto findCustomerByCustNumber(String SOrgCode) throws Exception {
		CustomerDto customerDto = null;
		String hql = "select cd from CustomerDto cd where cd.SOrgCode=:SOrgCode"
						+ " and cd.SCustFlag=:SCustFlag";
		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		
		paramName.add("SOrgCode");
		paramValue.add(SOrgCode);
		
		paramName.add("SCustFlag");
		paramValue.add(1);
			
		String paramNames[] = (String[])paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List res = ebankService.find(hql,paramNames,paramValues);
		if(res != null && res.size()>0){
			customerDto = (CustomerDto)res.get(0);
		}
		return customerDto;
	}
  
}
