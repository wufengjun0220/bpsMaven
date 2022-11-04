package com.mingtech.application.pool.common.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.common.domain.Dictionary;
import com.mingtech.application.common.logic.IDictionaryService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.base.domain.BoCcmsPartyinf;
import com.mingtech.application.pool.base.domain.BoCcmsPartyinfBean;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.CpesBranch;
import com.mingtech.application.pool.common.domain.GuarantDiscMapping;
import com.mingtech.application.pool.common.domain.PedBlackList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

@Controller
public class PedBlackListController extends BaseController {

	@Autowired
	private BlackListManageService blackListManageService;// 黑名单管理服务
	@Autowired
	private IDictionaryService dictionaryService; 
	@Autowired
	private DictCommonService dictCommonService;
	@Autowired
	private DraftPoolInService draftPoolInService;

	
	private static final Logger logger = Logger.getLogger(PedBlackListController.class);

	/**
	 * 
	 * 跳转到银行黑灰名单管理页面
	 */
	@RequestMapping("/queryBlackGrayList")
	public String queryBlackGrayList() {
		return "/pool/riskList/queryBlackGrayList";
	}

	/**
	 * 
	 * 跳转到企业黑灰名单管理页面
	 */
	@RequestMapping("/custBlackGrayList")
	public String custBlackGrayList() {
		return "/pool/riskList/custBlackGrayList";
	}

	/**
	 * <p>
	 * 方法名称: loadBlackListBankJSON|描述: 银行查询黑名单列表JSON
	 * </p>
	 */
	@RequestMapping("/queryBlacklistBank")
	public void loadBlackListBankJSON(PedBlackList pb) {
		try {
			// pb.setType(PoolComm.BLACK); // 黑名单
			if(null == pb.getDataFrom()){
				pb.setDataFrom("01");//银行端
			}
			String json = blackListManageService.loadBlackListJSON(pb, this.getPage(),this.getCurrentUser());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}

	}
	/**
	 * <p>
	 * 方法名称: loadBlackListBankJSON|描述: 企业查询黑名单列表JSON
	 * </p>
	 */
	@RequestMapping("/queryBlacklistCust")
	public void loadBlackListBank(PedBlackList pb) {
		try {
			// pb.setType(PoolComm.BLACK); // 黑名单
		    pb.setDataFrom("00");//企业端
			String json = blackListManageService.loadBlackListJSON(pb, this.getPage(),this.getCurrentUser());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}

	}
	/**
	 * 黑名单 ,点击新增按钮，跳转到新增页面
	 * 
	 * @return
	 */
	@RequestMapping("/addBlacklist")
	public String addBlacklistBank(PedBlackList pedBlackList) {
		return "/pool/riskList/addBlackGrayList";
	}
	
	/**
	 * 获取字典表里所有的省份及城市树
	 */
	@RequestMapping("/queryArea")
	public void queryArea() {
		String json = RESULT_EMPTY_DEFAULT;
		try {
			List list = dictionaryService.getDictionaryByParentCode("PJC_01", 0);
			if (list != null && list.size() > 0) {
				//List<TreeNodeEx> resultList = new ArrayList<TreeNodeEx>();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < list.size(); i++) {
					Dictionary dict = (Dictionary) list.get(i);
					
					List children = dictionaryService.getDictionaryByParentCode(dict.getCode(), 0);
					dict.setChildren(children);
				}
				//sb.append(JsonUtil.fromCollections(resultList));
				Map map = new HashMap();
				map.put("rows", list);
				sb.append(JSON.toJSONString(map));
				json = sb.toString();
			}
		} catch (Exception e) {
			json = "0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 获取字典表里所有的省份
	 */
	@RequestMapping("/queryProvince")
	public void queryProvince() {
		String json = "";
		try {
			List list = dictionaryService.getDictionaryByParentCode("PJC_01", 0);
			if (list != null && list.size() > 0) {
				json = JsonUtil.fromCollections(list);
			}
		} catch (Exception e) {
			json = "0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	
	
	/**
	 * 根据省份代码查询城市
	 * @param province
	 */
	@RequestMapping("/queryCityByProvince")
	public void queryCityByProvince(String province) {
		String json = "";
		try {
			if(StringUtil.isNotBlank(province)) {
				List list = dictionaryService.getDictionaryByParentCode(province, 0);
				if (list != null && list.size() > 0) {
					json = JsonUtil.fromCollections(list);
				}
			} else {
				json = "1";
			}
		} catch (Exception e) {
			json = "0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 黑名单新增信息或修改后保存 返回黑名单展示页面
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping("/saveBlackList")
	public void saveBlackList(PedBlackList pedBlackList) throws Exception {
		
		logger.info("黑名单维护开始....");
		User user = this.getCurrentUser();
		
		PedBlackList oldBlackList = null;
		boolean isAddAcptBankToBalack = false;//是否增加承兑行黑名单
		boolean isDelAcptBankToBalack = false;//是否删除承兑行黑名单
		
		try {
			
			List<String> blackBankNos = new ArrayList<String>();//添加至黑名单的承兑行
			List<String> backBankNos = new ArrayList<String>();//从黑名单移除的承兑行
			
			
			String id = pedBlackList.getId() ;
			
			if(StringUtil.isEmpty(id)) {//新增
				
				
//				pedBlackList.setId(null);
				oldBlackList = new PedBlackList();
				BeanUtil.copyValue(pedBlackList, oldBlackList);
				pedBlackList.setContent(oldBlackList.getContentTmp());
				
				logger.info("新增黑名单："+ oldBlackList.getContentTmp());
				
			}else{//修改，原黑名单改为灰名单，或者原承兑行号发生了变化，都需要删除承兑行黑名单票据
				
				oldBlackList = (PedBlackList)blackListManageService.load(id, PedBlackList.class);
				if(PoolComm.KEY_WAYS_03.equals(pedBlackList.getKeywords()) && PoolComm.BLACK.equals(pedBlackList.getType())&& !oldBlackList.getContent().equals(pedBlackList.getContentTmp())){
					//黑名单，承兑行,但是行号发生变化的情况
					
					logger.info("黑名单【承兑行】修改行号发生变化的情况，修改内容："+ pedBlackList.getContentTmp());
					
					isDelAcptBankToBalack = true;//删除承兑行到黑名单
					
					String bankNumber = blackListManageService.queryTotalBankNo(pedBlackList.getContentTmp());//查询总行行号
					if(bankNumber != null && bankNumber.equals(pedBlackList.getContentTmp())){//相同则是总行
						//查询总行下的所有分行行号在黑名单中的数据
						List blackList = blackListManageService.queryAllBankNo(bankNumber);
						if(blackList != null && blackList.size() > 0){
							//直接删除分行黑名单数据
							blackListManageService.txDeleteAll(blackList);
						}
						
						//黑名单承兑行列表
						blackBankNos = blackListManageService.queryAllBranchBank(bankNumber);
						blackBankNos.add(bankNumber);
						
					}else{
						//不同则添加的为分行   新增时,查询总行是否在黑名单,若在则返回已加总行为黑名单,不在则允许添加
						PedBlackList bean = new PedBlackList();
						bean.setContent(bankNumber);
						bean.setKeywords(PoolComm.KEY_WAYS_03);
						bean.setType(PoolComm.BLACK);
						List blackList = blackListManageService.queryBlackListByBean(bean, null);
						if(blackList != null){
							//总行已维护黑名单，不需要再次维护分行黑名单
							this.sendJSON("黑名单新增修改失败：总行已维护黑名单，不需要再次维护分行黑名单");
							return;
						}
						blackBankNos.add(bankNumber);
					}
					
				}
				if(PoolComm.KEY_WAYS_03.equals(pedBlackList.getKeywords()) && PoolComm.BLACK.equals(oldBlackList.getType())&& PoolComm.GRAY.equals(pedBlackList.getType()) ){
					
					logger.info("黑名单【承兑行】修改行号发生变化之前是黑名单，现在改成灰名单的情况，修改内容："+ pedBlackList.getContentTmp());
					
					//之前是黑名单，现在改成灰名单
					isDelAcptBankToBalack = true;//删除承兑行到黑名单
					
					String bankNumber = blackListManageService.queryTotalBankNo(pedBlackList.getContentTmp());//查询总行行号
					if(bankNumber != null && bankNumber.equals(pedBlackList.getContentTmp())){//相同则是总行
						//黑名单需移除的承兑行列表
						backBankNos = blackListManageService.queryAllBranchBank(bankNumber);
						backBankNos.add(bankNumber);
					}else{
						//黑名单需移除的承兑行列表
						backBankNos.add(bankNumber);
					}
					
					
				}
				BeanUtil.copyValue(pedBlackList, oldBlackList);
			}
			
			if(PoolComm.KEY_WAYS_03.equals(pedBlackList.getKeywords()) && PoolComm.BLACK.equals(pedBlackList.getType())){//黑名单，承兑行
				isAddAcptBankToBalack = true;//新增承兑行到黑名单
				
				logger.info("黑名单【承兑行】新增，修改内容："+ pedBlackList.getContentTmp());
				
				String bankNumber = blackListManageService.queryTotalBankNo(pedBlackList.getContent());//查询总行行号
				if(bankNumber != null){
					if(bankNumber.equals(pedBlackList.getContent())){//相同则是总行
						//查询总行下的所有分行行号在黑名单中的数据
						List blackList = blackListManageService.queryAllBankNo(bankNumber);
						
						if(blackList != null && blackList.size() > 0){
							//直接删除分行黑名单数据
							blackListManageService.txDeleteAll(blackList);
						}
						
						//黑名单承兑行列表
						blackBankNos = blackListManageService.queryAllBranchBank(bankNumber);
						blackBankNos.add(bankNumber);
						
						
					}else{
						//不同则添加的为分行   新增时,查询总行是否在黑名单,若在则返回已加总行为黑名单,不在则允许添加
						PedBlackList bean = new PedBlackList();
						bean.setContent(bankNumber);
						bean.setKeywords(PoolComm.KEY_WAYS_03);
						bean.setType(PoolComm.BLACK);
						List blackList = blackListManageService.queryBlackListByBean(bean, null);
						if(blackList != null){
							//总行已维护黑名单，不需要再次维护分行黑名单
							this.sendJSON("黑名单新增修改失败：总行已维护黑名单，不需要再次维护分行黑名单");
							return;
						}
						blackBankNos.add(bankNumber);
					}
					
				}
			}
			
			if(isAddAcptBankToBalack||isDelAcptBankToBalack){
				/**
				 * ①、根据行号查询会员表，是否为总行；
				 * ②、新增时若为总行则将下属行也加入黑名单，不为总行，只加本行
				 * ③、
				 * ④、
				 * ⑤、
				 * ⑥、
				 */
				
				this.sendJSON("黑名单新增/修改了承兑行名单信息，正在同步变更池额度信息，请稍后查询变更结果！");// 修改成功
				
				logger.info("黑名单新增/修改了承兑行名单信息，正在同步变更池额度信息，请稍后查询变更结果，修改内容："+ pedBlackList.getContentTmp());
				if(blackBankNos.size() > 0){
					List<String> bankList = new ArrayList<String>();
					int count = 0;
					if(isAddAcptBankToBalack){//增加到黑名单
						if(blackBankNos.size() > 1000){
							for (int i = 0; i < blackBankNos.size(); i++) {
								String str = blackBankNos.get(i);
								bankList.add(str);
								count++;
								if(count == 950){
									
									blackListManageService.txAddAcptBankToBlackList(bankList);
									bankList = new ArrayList<String>();
									count = 0;
								}
							}
						}else{
							bankList.addAll(blackBankNos);
							blackListManageService.txAddAcptBankToBlackList(blackBankNos);
						}
						
						
					}
					if(isDelAcptBankToBalack){//移出黑名单
						bankList = new ArrayList<String>();
						if(backBankNos.size() > 1000){
							for (int i = 0; i < backBankNos.size(); i++) {
								String str = backBankNos.get(i);
								bankList.add(str);
								count++;
								if(count == 950){
									
									blackListManageService.txDelAcptBankFromBlackList(bankList);
									bankList = new ArrayList<String>();
									count = 0;
								}
							}
						}else{
							bankList.addAll(blackBankNos);
							blackListManageService.txDelAcptBankFromBlackList(backBankNos);
						}
						
					}
				}
					
				}
			
			
			
			if(!pedBlackList.getKeywords().equals("04")){
				oldBlackList.setContent(pedBlackList.getContentTmp());
			}
			oldBlackList.setDataFrom(PoolComm.SOUR_BBSP);// 数据来源
			oldBlackList.setTellerNo(user.getLoginName());// 柜员号
			oldBlackList.setNetNo(user.getDepartment().getName());// 网点号
			oldBlackList.setCreateTime(DateUtils.getCurrDateTime()); // 创建时间
			blackListManageService.txStore(oldBlackList);
						
			
		} catch (Exception e) {
			logger.error("黑名单新增修改失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("黑名单新增修改失败："+e.getMessage());

		}
		
		/*if(isAddAcptBankToBalack||isDelAcptBankToBalack){
			*//**
			 * ①、根据行号查询会员表，是否为总行；
			 * ②、新增时若为总行则将下属行也加入黑名单，不为总行，只加本行
			 * ③、
			 * ④、
			 * ⑤、
			 * ⑥、
			 *//*
			
			this.sendJSON("黑名单新增/修改了承兑行名单信息，正在同步变更池额度信息，请稍后查询变更结果！");// 修改成功
			
			if(isAddAcptBankToBalack){//增加到黑名单
				
				blackListManageService.txAddAcptBankToBlackList(pedBlackList.getContent());
			
			}else{//移出黑名单
				
				List<String> acptBankNos = new ArrayList<String>();
				acptBankNos.add(oldBlackList.getContent());
				blackListManageService.txDelAcptBankFromBlackList(acptBankNos);
			}
		}else{
			this.sendJSON("黑名单新增修改成功");// 修改成功
			
		}*/
	}

	/**
	 * <p>
	 * 方法名称: editBlacklist|描述: 查询要编辑的记录
	 * </p>
	 * 
	 * @return
	 */
	@RequestMapping("/editBlacklist")
	public String editBlacklist(String id, Model mode) {
		try {
			List provinceList = dictionaryService.getDictionaryByParentCode("PJC_01", 0);
			mode.addAttribute("provinceList", provinceList);
			if (StringUtil.isNotBlank(id)) {
				PedBlackList pb = (PedBlackList) blackListManageService.load(id);
				if(StringUtil.isNotBlank(pb.getProvince())) {
					List cityList = dictionaryService.getDictionaryByParentCode(pb.getProvince(), 0);
					mode.addAttribute("cityList", cityList);
				}
				//pb.setDueDt(DateUtils.formatDate(pb.getDueDt(), DateUtils.ORA_DATES_FORMAT));
				mode.addAttribute("pedBlackList", pb);
			} else {
				return "/pool/riskList/addBlackGrayList";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/riskList/editBlackGrayList";
	}

	/**
	 * <p>
	 * 方法名称: deleteBlacklist|描述: 删除黑名单
	 * </p>
	 */
	@RequestMapping("/deleteBlackList")
	public void deleteBlacklist(String blackListIds) throws Exception{
		
		List<String> acptBankNos = new ArrayList<String>();
		
		try {
			if (!StringUtil.isEmpty(blackListIds)) {
				String[] ids = blackListIds.split(",");
				if (ids != null && ids.length > 0) {
					List<PedBlackList> list = new ArrayList<PedBlackList>();
					for (int i = 0; i < ids.length; i++) {
						PedBlackList pbl = (PedBlackList) blackListManageService.load(ids[i]);
						list.add(pbl);
						
						if(PoolComm.KEY_WAYS_03.equals(pbl.getKeywords()) && PoolComm.BLACK.equals(pbl.getType())){//黑名单，承兑行
							//
							String bankNumber = blackListManageService.queryTotalBankNo(pbl.getContent());//查询总行行号
							if(bankNumber != null){
								if(bankNumber.equals(pbl.getContent())){//相同则是总行
									//黑名单需移除的承兑行列表
									List<String> backBankNos = blackListManageService.queryAllBranchBank(bankNumber);
									acptBankNos.addAll(backBankNos);
									acptBankNos.add(bankNumber);
								}else{
									//黑名单需移除的承兑行列表
									acptBankNos.add(bankNumber);
								}
							}
							
//							acptBankNos.add(pbl.getContent());
						}
					}
					blackListManageService.txDeleteAll(list);
				}
			}
		} catch (Exception e) {
			logger.error("黑名单删除失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("黑名单删除失败："+e.getMessage());

		}
		
		if(acptBankNos.size()>0){
			this.sendJSON("删除黑名单中含有承兑行黑名单的数据，正在处理池额度信息，请稍后查询！");
			
			List<String> bankList = new ArrayList<String>();
			int count = 0;
			if(acptBankNos.size() > 1000){
				for (int i = 0; i < acptBankNos.size(); i++) {
					String str = acptBankNos.get(i);
					bankList.add(str);
					count++;
					if(count == 950){
						
						blackListManageService.txDelAcptBankFromBlackList(bankList);
						bankList = new ArrayList<String>();
						count = 0;
					}
				}
			}else{
				bankList.addAll(acptBankNos);
				blackListManageService.txDelAcptBankFromBlackList(bankList);
			}
		}else{			
			this.sendJSON("黑名单删除成功");
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/****************************************** 以下方法废弃掉*************************************************************/
	/**
	 * 
	 * 跳转到黑名单管理页面
	 */
	@RequestMapping("/blackListManage")
	public String blackListManage() {
		return "/pool/riskList/queryBlackListBank";
	}

	/**
	 * 
	 * 跳转到灰名单管理页面
	 */
	@RequestMapping("/grayListManage")
	public String GrayListManage() {
		return "/pool/riskList/queryGrayListBank";
	}
	// -------------------灰名单crud方法----------------------

	/**
	 * <p>
	 * 方法名称: loadGrayListBankJSON|描述: 查询灰名单列表JSON
	 * </p>
	 */
	@RequestMapping("/queryGraylistBank")
	public void loadGrayListBankJSON(PedBlackList pb) {

		try {
			pb.setType(PoolComm.GRAY); // 灰名单
			String json = blackListManageService.loadGrayListJSON(pb, this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 灰名单,点击新增按钮，跳转到新增页面
	 * 
	 * @return
	 */
	@RequestMapping("/addGraylist")
	public String addGraylistBank() {
		return "/pool/riskList/grayListBankAdd";
	}

	/**
	 * 灰名单新增或修改后保存 返回黑名单展示页面
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping("/saveGrayList")
	public String saveGrayList(PedBlackList pb) throws Exception {
		if (pb.getId() == "") {
			pb.setId(null);
		}
		User user = this.getCurrentUser();
		pb.setTellerNo(user.getLoginName());// 柜员号
		pb.setNetNo(user.getDepartment().getInnerBankCode());// 网点号
		pb.setDataFrom(PoolComm.SOUR_BBSP);// 数据来源
		pb.setCreateTime(DateUtils.getCurrDateTime()); // 创建时间
		pb.setType(PoolComm.GRAY); // 灰名单
		try {
			blackListManageService.txStore(pb);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/riskList/queryGrayListBank";

	}

	/**
	 * 描述: 查询要编辑的记录
	 * </p>
	 * 
	 * @return
	 */
	@RequestMapping("/editGraylist")
	public String editGraylist(String id, Model mode) {
		try {
			PedBlackList pb = null;
			String hql = "from PedBlackList pb where pb.id ='" + id + "'";
			List find = blackListManageService.find(hql);
			pb = (PedBlackList) find.get(0);
			pb.setId(id);
			mode.addAttribute("pedBlackList", pb);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/riskList/grayListBankEdit";
	}

	/**
	 * <p>
	 * 方法名称: deleteBlacklist|描述: 删除灰名单
	 * </p>
	 */
	@RequestMapping("/deleteGrayList")
	public String deleteGrayList(String blackListIds, PedBlackList pb) {

		if (!StringUtil.isEmpty(blackListIds)) {
			String[] ids = blackListIds.split(",");
			PedBlackList p = null;
			List<PedBlackList> list = new ArrayList<PedBlackList>();
			for (int i = 0; i < ids.length; i++) {
				p = new PedBlackList();
				p.setId(ids[i]);
				list.add(p);
				// pedRuleService.txDelete(tRule);
			}
			blackListManageService.txDeleteAll(list);
		}
		return "/pool/riskList/queryGrayListBank";
	}
	
	/**
	 * <p>
	 * 方法名称: queryInterbankSubordinate|描述: 查询灰名单列表JSON
	 * </p>
	 */
	@RequestMapping("/queryInterbankSubordinate")
	public void queryInterbankSubordinate(BoCcmsPartyinf pb) {
		Page page = this.getPage();
		String json = "";
		try {
			List list = new ArrayList();
			List listNew = blackListManageService.loadbankSubordinate(pb,page);
			if(listNew!=null && listNew.size()>0){				
				for(int i=0;i<listNew.size();i++){
					BoCcmsPartyinfBean bean =new BoCcmsPartyinfBean();
					BoCcmsPartyinf bof=(BoCcmsPartyinf) listNew.get(i);
					if(null != bof.getSubdrtbkcd()){
						BoCcmsPartyinf bofNew=blackListManageService.queryByPrcptcdNo(bof.getSubdrtbkcd());
						if(null != bofNew){
							bean.setPrcptcdHigh(bofNew.getPrcptcd());
							bean.setPtcptnmHigh(bofNew.getPtcptnm());
						}
					}
					bean.setPrcptcd(bof.getPrcptcd());
					bean.setPtcptnm(bof.getPtcptnm());
					list.add(bean);
				}
			}
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 票交所表
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryInterbankSubordinatePJS")
	public void queryInterbankSubordinatePJS(ProtocolQueryBean queryBean) {
		Page page = this.getPage();
		User user = this.getCurrentUser();
		String json = "";
		try {
			List<CpesBranch> list = new ArrayList<CpesBranch>();
			list = blackListManageService.queryCpesBranch(queryBean, user, page);
			
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}
	}
	
	@RequestMapping("queryInterbankSubordinateExpt")
	public void queryInterbankSubordinateExpt(BoCcmsPartyinf pedProtocolDto) {
		int[] num = { };
		String[] typeName = {  };
		try {
			List list = new ArrayList();
			List listNew = blackListManageService.loadbankSubordinate(pedProtocolDto,this.getPage());
			if(listNew!=null && listNew.size()>0){				
				for(int i=0;i<listNew.size();i++){
					BoCcmsPartyinfBean bean =new BoCcmsPartyinfBean();
					BoCcmsPartyinf bof=(BoCcmsPartyinf) listNew.get(i);
					if(null != bof.getSubdrtbkcd()){
						BoCcmsPartyinf bofNew=blackListManageService.queryByPrcptcdNo(bof.getSubdrtbkcd());
						if(null != bofNew){
							bean.setPrcptcdHigh(bofNew.getPrcptcd());
							bean.setPtcptnmHigh(bofNew.getPtcptnm());
						}
					}
					bean.setPrcptcd(bof.getPrcptcd());
					bean.setPtcptnm(bof.getPtcptnm());
					list.add(bean);
				}
			}
			
			List list1 = blackListManageService.exportbankSubordinate(list, null);

			String ColumnNames = "prcptcd,ptcptnm,prcptcdHigh,ptcptnmHigh";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("prcptcd", "行号");
			mapinfo.put("ptcptnm", "行名");
			mapinfo.put("prcptcdHigh", "上级行号");
			mapinfo.put("ptcptnmHigh", "上级行名");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list1, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("同业行下属机构.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	@RequestMapping("queryacceptorBank")
	public void queryacceptorBank(String prcptcd ,Model mode) throws Exception{
		String json="";
		try {
			if(StringUtils.isNotEmpty(prcptcd)){
				List list = blackListManageService.queryByPrcptcd(prcptcd);
				if(list!=null&&list.size()>0){
					Map map = (Map) list.get(0);
					json=(String)map.get("msg");
					//json = "{success:true,msg:'"+(String)map.get("msg")+"'}";
				}else{
					json = "1";
				}
			}else{
				json = "2";
			}
		} catch (Exception e) {
			json ="0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 生成JSON
	 * @param page
	 * @param list
	 * @return
	 * @throws Exception
	 */
	protected String toJson(Page page,List list) throws Exception{
		
		Map jsonMap = new HashMap();
		jsonMap.put("totalProperty", "results," + page.getTotalCount());
		jsonMap.put("root", "rows");
		String json= JsonUtil.fromCollections(list, jsonMap);
		if(!(StringUtil.isNotBlank(json))){
			json = RESULT_EMPTY_DEFAULT;
		}
		
		return json;
	}
	
	/**
	 * 承兑人映射信息管理界面主页面查询
	 * @param disc
	 */
	@RequestMapping("acceptorMapping")
	public void acceptorMapping(GuarantDiscMapping disc){
		String json = "";
		try {
			Page page = getPage();
			List result = blackListManageService.queryAcceptorMappingList(disc, page);
			json = JsonUtil.buildJson(result, page.getTotalCount());
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("查询失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "查询批失败:"+e.getMessage();
		}
		this.sendJSON(json);
	}
	
	
	
	/**
	 * loadMemberBankJSON 承兑行会员信息同步查询
	 * @author gcj
	 * @date 20210814
	 */
	@RequestMapping("/loadMemberBankJSON")
	public void loadMemberBankJSON(ProtocolQueryBean pedProtocolDto) {
		
		String json = "";
		try {
			json = blackListManageService.loadMemberBankList(pedProtocolDto, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("承兑行会员信息同步查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("承兑行会员信息同步查询失败："+e.getMessage());
		}
		
	}
	
	/**
	 * loadMemberBankJSON 承兑行会员信息同步
	 * @author gcj
	 * @date 20210814
	 */
	@RequestMapping("/updateMemberBank")
	public void updateMemberBank(String memberId) {
		
		String json = "更新失败";
		ReturnMessageNew response = null;
		try {
			response = blackListManageService.txSynchBankMember(memberId);
			json=response.getRet().getRET_MSG();
			this.sendJSON("向MIS系统同步承兑行会员信息成功，请稍后查询同步后的处理结果。");
			
		} catch (Exception e) {
			logger.error("承兑行会员信息同步查询失败:"+e.getMessage(),e);
			this.sendJSON("承兑行会员信息同步异常！");
		}
		
		
		try {
			/*
			 * 处理同步完成后的操作
			 */
			blackListManageService.txBankMemberChangedHandle(response);
			
		} catch (Exception e) {
			
			logger.error("承兑行会员信息同步后续处理异常："+e.getMessage(),e);
			
		}
		
	}
	
	
	/**
	 * loadFinanceMappingJSON 财票校验映射信息查询
	 * @author gcj
	 * @date 20210814
	 */
	@RequestMapping("/loadFinanceMappingJSON")
	public void loadFinanceMappingJSON(ProtocolQueryBean pedProtocolDto) {
		
		String json = "";
		try {
			pedProtocolDto.setCheckType("1");//财票
			json = blackListManageService.loadGuarantDiscMappingList(pedProtocolDto, null, this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("财票校验映射信息查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("财票校验映射信息查询失败："+e.getMessage());
		}
		
	}
	
	/**
	 * loadFinanceMappingJSON 商票校验映射信息查询
	 * @author gcj
	 * @date 20210814
	 */
	@RequestMapping("/loadBusinessMappingJSON")
	public void loadBusinessMappingJSON(ProtocolQueryBean pedProtocolDto) {
		
		String json = "";
		try {
			pedProtocolDto.setCheckType("2");//商票
			json = blackListManageService.loadGuarantDiscMappingList(pedProtocolDto,null, this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("商票校验映射信息查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("商票校验映射信息查询失败："+e.getMessage());
		}
		
	}
	
	/**
	 * 查询票交所承兑行行名
	 * @param acptBankCode
	 * @param type 2：商票	3：财票
	 * @throws Exception
	 */
	@RequestMapping("queryPjsAcptName")
	public void queryPjsAcptName(String acptBankCode,String type) throws Exception{
		String json="1";
		try {
			json = blackListManageService.queryPjsAcptName(acptBankCode,type);
			
		} catch (Exception e) {
			json ="2";//查询失败，请稍候再试...
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 查询保贴人编号
	 * @param mode
	 * @throws Exception
	 */
	@RequestMapping("queryGuarantDiscNo")
	public void queryGuarantDiscNo(String guarantDiscName) throws Exception{
		String json="1";
		try {
			json = blackListManageService.queryGuarantDiscNo(guarantDiscName);
			
		} catch (Exception e) {
			json ="2";//查询失败，请稍候再试...
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	
	/**
	 * 保贴映射关系保存
	 * @author 
	 * @throws Exception
	 */
	@RequestMapping("/saveGuarantDiscMapping")
	public void saveGuarantDiscMapping(GuarantDiscMapping grarant){
		String json = "";
		try{
			json=blackListManageService.txsaveGuarantDiscMapping(grarant, this.getCurrentUser());
			if(json.equals("1")){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("存在生效的财票映射总行不允许新增！");
			}else if(json.equals("2")){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("存在生效的财票映射总行不允许修改！");
			}else if(json.equals("3")){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("存在生效的商票映射不允许修改！");
			}else if(json.equals("4")){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("存在生效的商票映射不允许新增！");
			}else if(json.equals("5")){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("存在生效的财票映射不允许修改！");
			}else if(json.equals("6")){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("存在生效的财票映射不允许新增！");
			}else{
				this.sendJSON(json);
			}
		}catch(Exception e){
			logger.error("数据库操作失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("数据库操作失败："+e.getMessage());
		}
	}
	
	/**
	 * 方法说明: 删除保贴映射关系(逻辑删除)
	 * @param deleteGuarantDiscMapping
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteGuarantDiscMapping",method = RequestMethod.POST)
	public void deleteGuarantDiscMapping(String disIds){
		try{
			String[] ids = disIds.split(",");
			List list = Arrays.asList(ids);
			int size = list.size();
			for(int i=0;i<size;i++){
				GuarantDiscMapping grarant = (GuarantDiscMapping)blackListManageService.load(list.get(i).toString(),GuarantDiscMapping.class);
				grarant.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_YES);
				blackListManageService.txStore(grarant);
			}
		    this.sendJSON("删除成功");							
		}catch (Exception e){
			logger.error("删除保贴映射关系失败"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除保贴映射关系失败："+e.getMessage());
		}
	}
	
	/**
	 * 财票申请提交    20210701
	 * @param id 
	 */
	@RequestMapping("submitAuditFinance")
	public void submitAuditFinance(String id){
		String json = "";
		try {
			User user = this.getCurrentUser();
			//财票申请提交
			blackListManageService.txSubmitAuditFinance(id, user);
			
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("财票申请失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "财票申请失败:"+e.getMessage();
		}
		this.sendJSON(json);
	}
	
	@RequestMapping("/cancelAuditFinance")
	public void cancelAuditFinance(String id) {
		String msg = "";
		try {
			User user = this.getCurrentUser();
			blackListManageService.txCancelAuditFinance(id, user);
			msg = "撤销审批成功";
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			 msg = "撤销审批失败:"+e.getMessage();
		}
		this.sendJSON(msg);
	}
	
	
	/**
	 * loadFinanceMappingJSON 财票校验映射信息查询
	 * @author wfj
	 * @date 20210926
	 */
	@RequestMapping("/loadFinanceMappingDetail")
	public void loadFinanceMappingDetail(ProtocolQueryBean pedProtocolDto) {
		
		String json = "";
		try {
			json = blackListManageService.loadGuarantDiscMappingList(pedProtocolDto, null, this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error("财票校验映射信息查询失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("财票校验映射信息查询失败："+e.getMessage());
		}
		
	}
	
	/***------------------------------------ 财票改造开发专用方法   start------------------------------------*/
	
	@RequestMapping("/financeMappingDevelop")
	public void financeMappingDevelop(PoolBillInfo bean){
		String json ="";;
		try {
			List list = new ArrayList();
			list.add(bean.getAccNo());
			List result = queryCheckBills(bean.getCustNo(), list);
			
			json = JsonUtil.buildJson(result, result.size());
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("查询失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "查询批失败:"+e.getMessage();
		}
		this.sendJSON(json);
		
		
	}

	public List<PoolBillInfo> queryCheckBills(String custNo,List acctNoLsit)throws Exception{		
		StringBuffer hql = new StringBuffer("select bill from PoolBillInfo bill ");
		List keys = new ArrayList();
		List values = new ArrayList();
		
		hql.append(" where 1=1 ");
		hql.append(" and bill.SDealStatus ='DS_00'");//初始化
		hql.append(" and bill.blackFlag != '02' ");//不在黑名单
		hql.append(" and bill.SBanEndrsmtFlag = '0' ");//可转让		
		hql.append(" and bill.ebkLock != '0' ");//BBSP锁票
		//hql.append(" and (bill.rickLevel != 'FX_03' or bill.rickLevel is null) ");//风险标识不为不在风险名单
		
		if(StringUtil.isNotBlank(custNo)){
			hql.append(" and custNo =:custNo ");//客户号
			keys.add("custNo");
			values.add(custNo);
		}
		
//		hql.append(" and accNo in(:acctNoLsit) ");//电票签约账号
//		keys.add("acctNoLsit");
//		values.add(acctNoLsit);
		
		List billList = blackListManageService.find(hql.toString(), (String[]) keys.toArray(new String[keys.size()]), values.toArray());
		return billList;
	}
	
	@RequestMapping("/queryFinance")
	public void queryFinance(String id){
		try {
			PoolBillInfo info = (PoolBillInfo) blackListManageService.load(id,PoolBillInfo.class);
			List<PoolBillInfo> billList = new ArrayList<PoolBillInfo>();
			billList.add(info);
			blackListManageService.txMisCreditQuery(billList);
					
					
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@RequestMapping("/sendFinance")
	public void sendFinance(String id){
		try {
			PoolBillInfo info = (PoolBillInfo) blackListManageService.load(id,PoolBillInfo.class);
			
			draftPoolInService.txMisCreditOccupy(info);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	
	
	/***------------------------------------ 财票改造开发专用方法    end------------------------------------*/

}
