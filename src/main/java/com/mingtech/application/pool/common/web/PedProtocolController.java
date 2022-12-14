package com.mingtech.application.pool.common.web;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.mingtech.application.ecds.common.service.DictCommonService;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetFactory;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PedProtocolModDto;
import com.mingtech.application.pool.common.domain.PlFeeScale;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.application.pool.edu.domain.PedCheckBatch;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.CustomerService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ExcelUtil;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;


@Controller
public class PedProtocolController extends BaseController{

	private static final Logger logger = Logger.getLogger(PedProtocolController.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
    private PoolCoreService poolCoreService;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;
	@Autowired
	private PoolBailEduService poolBailEduService;
	@Autowired
	BlackListManageService blackListManageService ;
	@Autowired
	private DictCommonService dictCommonService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private FinancialService financialService;

	private PoolQueryBean queryBean = new PoolQueryBean();//??????????????????


    /**
     * 
     * @Description: ??????????????????????????????
     * @author Ju Nana
     * @date 2018-11-5 ??????4:22:21
     */
	@RequestMapping("/openpoollist")
	public String openpoollist() {
		return "/pool/common/listProtocol";
	}

	/**
	 * <p>
	 * loadPoolJSON ?????????????????????????????????????????????
	 * </p>
	 */
	@RequestMapping("/loadMypoolJSON")
	public void loadMypoolJSON(PedProtocolDto pedProtocolDto) {
		
		String json = "";
		try {
			json = pedProtocolService.loadProolJSON(pedProtocolDto, queryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			logger.debug("load pedProtocolDto JSON:" + json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}
		sendJSON(json);
	}
	
	/**
	 * <p>
	 * loadPoolJSON ????????????????????????????????????????????? ??????????????????
	 * </p>
	 */
	@RequestMapping("/loadQuerypoolJSON")
	public void loadQuerypoolJSON(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
//			pedProtocolDto.setvStatus(PoolComm.VS_01);
			json = pedProtocolService.loadProolJSONQuery(pedProtocolDto, queryBean,user, page);
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			logger.debug("load pedProtocolDto JSON:" + json);
		} catch (Exception e) {
			logger.error(e,e);
			logger.error(e.getMessage(),e);
		}
		sendJSON(json);
	}
	@RequestMapping("querypoolExpt")
	public void custEduListExpt(PedProtocolDto pedProtocolDto) {
		int[] num = { 3};
		String[] typeName = { "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount", "amount" };
		try {
			List list1 = pedProtocolService.loadProolListQuery(pedProtocolDto, queryBean, null,null);
			List list = pedProtocolService.exportProolListQuery(list1, null);

			String ColumnNames = "poolAgreement,custName,custnumber,creditamount,custOrgcode,effstartdate," +
								 "effenddate,isGroupName,vtStatusName,openFlagName" +
					",frozenstateName,accountManager,accountManagerId,signDeptName,creditDeptName,officeNetName";
			Map mapinfo = new LinkedHashMap();
			mapinfo.put("poolAgreement", "???????????????");
			mapinfo.put("custName", "????????????");
			mapinfo.put("custnumber", "?????????");
			mapinfo.put("creditamount", "??????????????????");
			mapinfo.put("custOrgcode", "??????????????????");
			mapinfo.put("effstartdate", "???????????????");
			mapinfo.put("effenddate", "???????????????");
			mapinfo.put("isGroupName", "????????????");
			mapinfo.put("vtStatusName", "??????????????????");
			mapinfo.put("openFlagName", "??????????????????");
			mapinfo.put("frozenstateName", "????????????");
			mapinfo.put("accountManager", "??????????????????");
			mapinfo.put("accountManagerId", "????????????ID");
			mapinfo.put("signDeptName", "????????????");
			mapinfo.put("creditDeptName", "????????????");
			mapinfo.put("officeNetName", "????????????");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("???????????????.xls", "utf-8"));
			os.write(buffer);
			os.flush();
			os.close();
			os = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * 
	 * @Description: ??????????????????????????????????????????????????????????????????????????????
	 * @author Ju Nana
	 * @date 2018-11-7 ??????2:44:33
	 */
	@RequestMapping("/addAcceptancePool")
	public String addAcceptancePool( Model mode) {
		PedProtocolDto pedProtocolDto = new PedProtocolDto();
		User user = new User();
		try {
			user = this.getCurrentUser();
			pedProtocolDto.setOfficeNet(user.getDepartment().getName());
			pedProtocolDto.setOperatorName1(user.getName());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			mode.addAttribute("pedProtocolDto", pedProtocolDto);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return "/pool/common/addProtocol";
	}
	/**
	 * @Description:??????????????? ??????????????????????????????
	 * @param SAccountNo
	 * @author Wu Fengjun
	 */
	@RequestMapping("queryCustomertFromCoreSys")
	public void queryCustomertFromCoreSys(String SAccountNo ,Model mode) throws Exception{
		
		String json="";
		
		try {
			List list = poolEBankService.queryCustomert(SAccountNo);
			if(list!=null&&list.size()>0){
				json = this.toJson(this.getPage(), list);
			}else{
				json = "1";
			}
		} catch (Exception e) {
			json = "0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	/**
	 * @Description:??????????????? ??????????????????????????????   ?????????
	 * @param SAccountNo
	 * @author Wu Fengjun
	 */
	@RequestMapping("queryCustomertFromCoreSysSave")
	public void queryCustomertFromCoreSysSave(String SAccountNo ,Model mode) throws Exception{
		
		String json="";
		
		try {
			List list = poolEBankService.queryCustomert(SAccountNo);
			if(list!=null&&list.size()>0){
				PedProtocolDto  pedOld =(PedProtocolDto) list.get(0);
				//???????????????
				ProtocolQueryBean bean = new ProtocolQueryBean();
				bean.setCustnumber(SAccountNo);
				List<PedProtocolDto>   listPed = pedProtocolService.queryProtocolDtoListByQueryBean(bean);
				if(null !=listPed && listPed.size()>0){
					for(int i=0;i<listPed.size();i++){
						PedProtocolDto  ped = listPed.get(i);
						ped.setCustname(pedOld.getCustname());
						pedProtocolService.txStore(ped);
					}
				}
				//?????????????????????
				ProListQueryBean queryBean = new ProListQueryBean();
				queryBean.setCustNo(SAccountNo);
				List<PedProtocolList> listPro = pedProtocolService.queryProListByQueryBean(queryBean);
				if(null !=listPro && listPro.size()>0){
					for(int i=0;i<listPro.size();i++){
						PedProtocolList  ped = listPro.get(i);
						ped.setCustName(pedOld.getCustname());
						pedProtocolService.txStore(ped);
					}
				}
				json = "2";
			}else{
				json = "1";
			}
		} catch (Exception e) {
			json = "0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * @Description:??????????????? ??????????????????????????????   ?????????
	 * @param SAccountNo
	 * @author Wu Fengjun
	 */
	@RequestMapping("updateCustomertFromCore")
	public void updateCustomertFromCore(String SAccountNo ,Model mode) throws Exception{
		
		String json="";
		
		try {
			List list = poolEBankService.queryCustomert(SAccountNo);
			if(list!=null&&list.size()>0){
				PedProtocolDto  pedOld =(PedProtocolDto) list.get(0);
				//???????????????
				ProtocolQueryBean bean = new ProtocolQueryBean();
				bean.setCustnumber(SAccountNo);
				List<PedProtocolDto>   listPed = pedProtocolService.queryProtocolDtoListByQueryBean(bean);
				if(null !=listPed && listPed.size()>0){
					for(int i=0;i<listPed.size();i++){
						PedProtocolDto  ped = listPed.get(i);
						ped.setCustname(pedOld.getCustname());
						pedProtocolService.txStore(ped);
					}
				}
				//?????????????????????
				ProListQueryBean queryBean = new ProListQueryBean();
				queryBean.setCustNo(SAccountNo);
				List<PedProtocolList> listPro = pedProtocolService.queryProListByQueryBean(queryBean);
				if(null !=listPro && listPro.size()>0){
					for(int i=0;i<listPro.size();i++){
						PedProtocolList  ped = listPro.get(i);
						ped.setCustName(pedOld.getCustname());
						pedProtocolService.txStore(ped);
					}
				}
				//???????????????????????????
				CustomerRegister customer=new CustomerRegister();
				customer.setCustNo(SAccountNo);
				CustomerRegister cost=customerService.loadCustomerRegister(customer);
				if(null!=cost){
					cost.setCustName(pedOld.getCustname());
					customerService.txStore(cost);
				}
				//??????????????????
				OnlineQueryBean query =new OnlineQueryBean();
				query.setCustNumber(SAccountNo);
				PedOnlineAcptProtocol acpt=pedOnlineAcptService.queryOnlineAcptProtocol(query);
				if(null!=acpt){
					acpt.setCustName(pedOld.getCustname());
					pedOnlineAcptService.txStore(acpt);
				}
				//??????????????????
				OnlineQueryBean online =new OnlineQueryBean();
				online.setCustNumber(SAccountNo);
				PedOnlineCrdtProtocol  crdt=pedOnlineCrdtService.queryOnlineCrdtProtocol(online);
				if(null!=crdt){
					crdt.setCustName(pedOld.getCustname());
					pedOnlineCrdtService.txStore(crdt);
				}
				json = "2";
			}else{
				json = "1";
			}
		} catch (Exception e) {
			json = "0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * @Description:????????????????????? ??????????????????????????????
	 * @param SAccountNo
	 * @author Wu Fengjun
	 */
	@RequestMapping("queryAccountFromCoreSys")
	public void queryAccountFromCoreSys(String SAccountNo,String custnumber,Model mode) throws Exception{
		String json="";
		try {
			List list = poolEBankService.queryAccount(SAccountNo);
			PedProtocolDto pedProtocolDto = null;
			List<PedProtocolDto> proList =  pedProtocolService.queryProtocolInfo(null, null, null, custnumber, null, null);
			if(proList!=null && proList.size()>0){
				pedProtocolDto = proList.get(0);
			}
			
			
			logger.debug(list);
			if(list!=null&&list.size()>0){
				Map map = (Map) list.get(0);
				if(map.get("code").equals("1")){
					String clientNo=(String) map.get("clientNo");//?????????
					String productNo=(String) map.get("productNo");//????????????
					String balnceStr=(String) map.get("balnce");//??????*/
					BigDecimal balnce=new BigDecimal(balnceStr);
					if(null != pedProtocolDto){
						if(!clientNo.equals(custnumber)){
							json = "2";
						}else if(!"2209022".equals(productNo)){
							json ="3";
						}else if(PoolComm.OPEN_01.equals(pedProtocolDto.getOpenFlag())){
							BailDetail bail=poolBailEduService.queryBailDetailByBpsNo(pedProtocolDto.getPoolAgreement());
							if(null != bail){
								BigDecimal limit=bail.getAssetLimitUsed();//?????????????????????
								if(limit.compareTo(balnce) == 1){
									json ="4";
								}else{
//									json = "{success:true,msg:'"+(String)map.get("msg")+"'}";
									json = "{success:true,Org:'"+(String)map.get("Org")+"',msg:'"+(String)map.get("msg")+"'}";
									logger.debug(json);
								}
							}else{
								json ="5";
							}
						}else{
//							PedProtocolDto dto = (PedProtocolDto) map.get("dto");
//							json = "{success:true,msg:'"+(String)map.get("msg")+"'}";
							json = "{success:true,Org:'"+(String)map.get("Org")+"',msg:'"+(String)map.get("msg")+"'}";
							logger.debug(json);
						}
					}
				}else{
					json = "{success:false,msg:'"+(String)map.get("msg")+"'}";
				}
			}else{
				json = "1";
			}
		} catch (Exception e) {
			json ="0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	/**
	 * @Description:?????????????????? ??????????????????????????????
	 * @param SAccountNo
	 * @author Wu Fengjun
	 */
	@RequestMapping("queryaccNoFromCoreSys")
	public void queryaccNoFromCoreSys(String PAccountNo ,Model mode) throws Exception{
		String json="";
		try {
			List list = poolEBankService.queryAccNo(PAccountNo);
			if(list!=null&&list.size()>0){
				Map map = (Map) list.get(0);
				if(map.get("code").equals("1")){
					json = "{success:true,msg:'"+(String)map.get("msg")+"'}";
				}else{
					json = "{success:false,msg:'"+(String)map.get("msg")+"'}";
				}
			}else{
				json = "1";
			}
		} catch (Exception e) {
			json ="0";
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * ??????JSON
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
	 * 
	 * @Description: ?????????????????????
	 * @param  pedProtocolDto ??????????????????
	 * @author Ju Nana
	 * @date 2018-11-5 ??????4:13:21
	 */
	@RequestMapping("/savePool")
	public String txsavePool(PedProtocolDto pedProtocolDto) {
		
		try {
			PedProtocolDto protocol = null;
			List<PedProtocolDto> proList =  pedProtocolService.queryProtocolInfo(null, null, null, pedProtocolDto.getCustnumber(), null, null);
			if(proList!=null && proList.size()>0){
				protocol = proList.get(0);
			}
			
			if(protocol!=null){
				throw new Exception("????????????????????????????????????,??????????????????????????????");
			}
		} catch (Exception e) {
			this.sendJSON(e.getMessage() );
			logger.error(e,e);
			return "/pool/common/listProtocol";
		}
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			PedProtocolDto  ppd = new PedProtocolDto();
			BigDecimal BAIFEN=new BigDecimal(0.01);      //?????????90%--->0.90
			BigDecimal QIANFEN=new BigDecimal(0.001);    //?????????5???----???0.005

			//???????????????????????????????????????
			ppd.setGuarantNumber(pedProtocolDto.getGuarantNumber());
			ppd.setDisRate(BigDecimalUtils.multiply3(pedProtocolDto.getDisRate(),QIANFEN ));   //??????
			ppd.setIsGroup(pedProtocolDto.getIsGroup());//??????????????????
			ppd.setCustnumber(pedProtocolDto.getCustnumber());//?????????
			ppd.setCustOrgcode(pedProtocolDto.getCustOrgcode()); // ????????????????????????
			ppd.setCustname(pedProtocolDto.getCustname());//????????????
			ppd.setPoolAccount(pedProtocolDto.getPoolAccount());//??????????????????
			ppd.setCustlevel(pedProtocolDto.getCustlevel());//????????????
			ppd.setPoolAccountName(pedProtocolDto.getPoolAccountName());//??????????????????
			ppd.setMarginAccount(pedProtocolDto.getMarginAccount());//???????????????
			ppd.setDiscountRatio(BigDecimalUtils.multiply(pedProtocolDto.getDiscountRatio(), BAIFEN)); //??????????????????100%
			ppd.setIsMarginGroup(pedProtocolDto.getIsMarginGroup());//?????????????????????
			ppd.setAssetType(pedProtocolDto.getAssetType());//????????????????????????
			ppd.setPoolMode(pedProtocolDto.getPoolMode());//??????????????????????????????
			ppd.setLicename(pedProtocolDto.getLicename());//?????????????????????
			ppd.setAuthperson(pedProtocolDto.getAuthperson());//???????????????????????????
			ppd.setPhonenumber(pedProtocolDto.getPhonenumber());//????????????????????????
			ppd.setOfficeNet(pedProtocolDto.getOfficeNet());//????????????
			ppd.setOperatorName1(pedProtocolDto.getOperatorName1());//?????????
			ppd.setZyflag(pedProtocolDto.getZyflag());     
			ppd.setXyflag(pedProtocolDto.getXyflag());
			pedProtocolDto.setOfficeNet(this.getCurrentUser().getDepartment().getName());//TODO  Asteria ???????????????????????????????????????
			//ppd.setEffestate(PoolComm.SP002);//????????????????????????
			//ppd.setBusiType(PoolComm.BT_01);//?????????????????????
			ppd.setContractType(PoolComm.CT_01);//???????????????
			ppd.setPoolMode(PoolComm.POOL_MODEL_01);//?????????????????????
			ppd.setOperateTime(new Date());//????????????
			pedProtocolService.txStore(ppd);
			return "/pool/common/listProtocol";
		
	}
	
	/**
	 * 
	 * @Description:???????????? ????????????????????????????????????
	 * @author Ju Nana
	 * @date 2018-11-5 ??????4:24:43
	 */
	@RequestMapping("/showPedPro")
	public String showPedPro(PedProtocolDto pedProtocolDto, Model mode){
		String isShow="";

		try {
			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId()); 
            mode.addAttribute("pedProtocolDto", pedProtocolDto);
			 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/showProtocol";
	}
	@RequestMapping("/showPedProV")
	public String showPedProV(PedProtocolModDto pedProtocolDto, Model mode){
		String isShow="";

		try {
			pedProtocolDto =  pedProtocolService.queryModProtocolById(pedProtocolDto.getPoolInfoId()); 
			/*BigDecimal BAIFEN =new BigDecimal(100);      //?????????0.90-->90%--->
			BigDecimal QIANFEN=new BigDecimal(1000);    //?????????0.005--->5???----???

			pedProtocolDto.setDiscountRatio(BigDecimalUtils.multiply2(pedProtocolDto.getDiscountRatio(),BAIFEN));
			pedProtocolDto.setDisRate(BigDecimalUtils.multiply2(pedProtocolDto.getDisRate(),QIANFEN));   //??????
			if(pedProtocolDto.getShortLoanRate()!=null && !"".equals(pedProtocolDto.getShortLoanRate())){
					pedProtocolDto.setShortLoanRate(BigDecimalUtils.multiply2(pedProtocolDto.getShortLoanRate(),BAIFEN));
			}*/
            mode.addAttribute("pedProtocolDto", pedProtocolDto);
			 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/showProtocol";
	}
	/**
	 * 
	 * @Description: ??????????????????
	 * @param  protocolId  ped_protorol?????????id
	 * @param  pedProtocolDto
	 * @author Ju Nana
	 * @date 2018-10-31 ??????2:44:47
	 */
	@RequestMapping("/deleteProtocol")
	public String deleteProtocol(String poolInfoIds,PedProtocolDto pedProtocolDto){

		if (!StringUtil.isEmpty(poolInfoIds)) {
			String[] ids = poolInfoIds.split(",");
			PedProtocolDto p = null;
			List<PedProtocolDto> list = new ArrayList<PedProtocolDto>();
			for(int i=0;i<ids.length;i++) {
				p = new PedProtocolDto();
				p.setPoolInfoId(ids[i]);
				list.add(p);
			}
			pedProtocolService.txDeleteAll(list);
		}
		return "/pool/common/listProtocol";
	}
	
	/**
	 * 
	 * @Description: ??????????????????????????????????????????
	 * @param  pedProtocolDto
	 * @author Ju Nana
	 * @date 2018-11-5 ??????4:51:09
	 */
	@RequestMapping("/loadAcceptancePpooll")
	public String loadAcceptancePpooll(PedProtocolDto pedProtocolDto, Model mode){
		try {
 			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			pedProtocolDto.setOfficeNet(this.getCurrentUser().getDepartment().getInnerBankCode());
		  mode.addAttribute("pedProtocolDto", pedProtocolDto);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/editProtocol";
	}
	

	/**
	 * 
	 * @Description: ????????????
	 * @param pedProtocolDto
	 * @author Ju Nana
	 * @date 2018-11-5 ??????4:59:43
	 */
	@RequestMapping("/editPool")
	public String txeditPool(PedProtocolDto pedProtocolDto){
		PedProtocolDto ppd=null;
		PedProtocolModDto  newPpd = new PedProtocolModDto();//  custnumber  ?????????  custname???????????? zyflag?????????????????? marginAccount???????????????  marginAccountName???????????????
		//isMarginGroup????????????   poolAccount???????????? poolAccountName ????????????  officeNet????????????  operatorName1???????????????
		ppd = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
		try {
		BeanUtils.copyProperties(newPpd, ppd);
		newPpd.setPoolInfoId(null);
		newPpd.setMarginAccount(pedProtocolDto.getMarginAccount());
		newPpd.setCustnumber(pedProtocolDto.getCustnumber());
		newPpd.setCustname(pedProtocolDto.getCustname());
		newPpd.setZyflag(pedProtocolDto.getZyflag());
//		newPpd.setIsMarginGroup(pedProtocolDto.getIsMarginGroup());
		newPpd.setPoolAccount(pedProtocolDto.getPoolAccount());
		newPpd.setPoolAccountName(pedProtocolDto.getPoolAccountName());
//		newPpd.setOperatorName1(pedProtocolDto.getOperatorName1());
		newPpd.setMarginAccountName(pedProtocolDto.getMarginAccountName());
		newPpd.setCustOrgcode(pedProtocolDto.getCustOrgcode());
		String officeNet = pedProtocolDto.getOfficeNet();
		if("".equals(pedProtocolDto.getOfficeNet())){
			newPpd.setOfficeNet(this.getCurrentUser().getDepartment().getInnerBankCode());
			newPpd.setOfficeNetName(this.getCurrentUser().getDepartment().getName());
		}else{
			Department depart= pedProtocolService.queryDertByNumer(officeNet);
			if(null !=depart){
				String officeNetNew=depart.getInnerBankCode();
				newPpd.setOfficeNet(officeNetNew);
				newPpd.setOfficeNetName(depart.getName());
			}
		}
		newPpd.setAccountManager(pedProtocolDto.getAccountManager());
		newPpd.setAccountManagerId(pedProtocolDto.getAccountManagerId());
		newPpd.setOperateTime(new Date());//????????????
		ppd.setApproveFlag(PoolComm.APPROVE_01);//???????????????
		newPpd.setApproveFlag(PoolComm.APPROVE_01);//???????????????
		newPpd.setFrozenstate(PoolComm.FROZEN_STATUS_00);
		newPpd.setContractType(PoolComm.BT_08);
		//???????????????detail???
		
			pedProtocolService.txStore(ppd);
			pedProtocolService.txStore(newPpd);
		} catch (Exception e) {
			logger.error("??????????????????????????????????????????????????????????????????", e);
		}
		
	    BailDetail curBail =AssetFactory.newCurBailDetail();
		
		return "/pool/common/listProtocol";
		}
	/**
	 * 
	 * @Description: ??????????????????
	 * @author Ju Nana
	 * @date 2018-11-5 ??????8:18:29
	 */
	@RequestMapping("/submitPedpro")
	public void submitPedpro(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			pedProtocolService.txCommitPedpro(pedProtocolDto);
			json = "{'result':true}";
		} catch (Exception e) {
			logger.error(e, e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}
	/**
	 * 
	 * @Description: ????????????/????????????
	 * @author liu xiaodong
	 * @date 2018-11-5 ??????8:18:29
	 */
	@RequestMapping("/submitPedproFor")
	public void submitPedproFor(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			pedProtocolService.txCommitPedproFor(pedProtocolDto);
			json = "{'result':true}";
		} catch (Exception e) {
			logger.error(e, e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}

	/**
	 * 
	 * @Description: ??????????????????????????????????????????
	 * @author Ju Nana
	 * @date 2018-11-7 ??????2:39:57
	 */
	@RequestMapping("/fuhelist")
	public String fuhelist() {
		return "/pool/common/auditProtocol";
	}
	

	/**
	 * ??????????????????
	 * @Description:????????????????????????????????????????????? 
	 * @author Ju Nana
	 * @date 2018-11-7 ??????2:38:26
	 */
	@RequestMapping("/closePool")
	public String closePool(PedProtocolDto pedProtocolDto) {
		try {
			PedProtocolDto ppd = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			BigDecimal bailTotalAmt = new BigDecimal("0.0");//?????????????????????
			BigDecimal draftUsedAmt = new BigDecimal("0.0");//?????????????????????
			//?????????????????????????????????????????????????????????
			CoreTransNotes transNotes = new CoreTransNotes();
			transNotes.setAccNo(ppd.getMarginAccount());
			transNotes.setCurrentFlag("1");
			ReturnMessageNew resp = poolCoreService.PJH716040Handler(transNotes, "0");
			if (resp.isTxSuccess()) {
				Map map = resp.getBody();
				if (map.get("BALANCE") != null) { //??????????????????
					bailTotalAmt = BigDecimalUtils.valueOf((String) map.get("BALANCE"));
				}
			}
			//?????????????????????
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(ppd, PoolComm.ED_PJC);
			if(at!=null){
				draftUsedAmt = at.getCrdtUsed();
			}
			//?????????????????????????????????????????????
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setCustomernumber(ppd.getCustnumber());//???????????????
			ArrayList<String> statusList = new ArrayList<String>();
			statusList.add(PoolComm.DS_00);//?????????????????????
			statusList.add(PoolComm.DS_04);//?????????????????????
			statusList.add(PoolComm.TS05);//???????????????????????????
			queryBean.setStatus(statusList);
			List<PoolBillInfo> infos = draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
			
			if(bailTotalAmt.compareTo(BigDecimal.ZERO)!=0){
				throw new Exception("????????????????????????????????????0??????????????????");
			}else if(draftUsedAmt.compareTo(BigDecimal.ZERO)>0){
				throw new Exception("????????????????????????????????????????????????");
			}else if(infos!=null && infos.size()>0){
				logger.debug("?????????????????????"+infos.size()+"???");
				throw new Exception("??????????????????????????????????????????");
			}else{//??????
				//??????
				ppd.setOperateTime(new Date());
				ppd.setApproveFlag(PoolComm.APPROVE_04);
				//??????
				PedProtocolModDto newPed = (PedProtocolModDto) pedProtocolService.queryModProtocolByCode(ppd.getCustnumber());
				if(null !=newPed){
					newPed.setOperateTime(new Date());
					newPed.setApproveFlag(PoolComm.APPROVE_04);
					pedProtocolService.txStore(newPed);
				}
				pedProtocolService.txStore(ppd);
				this.sendJSON("{isOk:\'true\'}");
			}
		} catch (Exception ex) {
			this.sendJSON("{isOk:\'" + ex.getMessage() + "\'}");
			logger.error(ex,ex);
			return "/pool/common/listProtocol";
		}
		return "/pool/common/listProtocol";
	}
		/**
		 * 
		 * @Description: ??????????????????
		 * @return String  
		 * @author Ju Nana
		 * @date 2018-11-7 ??????2:36:50
		 */
		@RequestMapping("/refusePool")
		public String refusePool(PedProtocolDto pedProtocolDto) {
			try {				
				PedProtocolDto ppd =new PedProtocolDto();
				//??????
				PedProtocolModDto newPpd = (PedProtocolModDto) pedProtocolService.queryModProtocolById(pedProtocolDto.getPoolInfoId());
				//??????
				List<PedProtocolDto> proList =  pedProtocolService.queryProtocolInfo(null, null, null, newPpd.getCustnumber(), null, null);
				if(proList!=null && proList.size()>0){
					ppd = proList.get(0);
				}
				
				
				String approveFlag = ppd.getApproveFlag();
				if(PoolComm.APPROVE_01.equals(approveFlag)){//???????????????
					ppd.setApproveFlag(PoolComm.APPROVE_02);//??????????????????
					newPpd.setApproveFlag(PoolComm.APPROVE_02);//??????????????????
				}
				if(PoolComm.APPROVE_04.equals(approveFlag)){//???????????????
					ppd.setApproveFlag(PoolComm.APPROVE_05);//??????????????????
					newPpd.setApproveFlag(PoolComm.APPROVE_05);//??????????????????
				}
				//??????
				User user = this.getCurrentUser();
				ppd.setOfficeNet2(user.getDepartment().getName());
				ppd.setOperatorName2(user.getName());
				ppd.setOperateTime(new Date());
				//??????
				newPpd.setOfficeNet2(user.getDepartment().getName());
				newPpd.setOperatorName2(user.getName());
				newPpd.setOperateTime(new Date());
			    pedProtocolService.txStore(ppd);
			    pedProtocolService.txStore(newPpd);
				this.sendJSON("{isOk:\'true\'}");
			} catch (Exception ex) {
				this.sendJSON("{isOk:\'" + ex.getMessage() + "\'}");
				logger.error(ex,ex);
				return "/pool/common/auditProtocol";
			}
			return "/pool/common/auditProtocol";
		}	

		/**
		 * 
		 * @Description: ????????????
		 * @return String  
		 * @author Ju Nana
		 * @date 2018-11-7 ??????2:36:50
		 */
		@RequestMapping("/refusePoolCrt")
		public String refusePoolCrt(PedProtocolDto pedProtocolDto) {
			try {				
				PedProtocolDto ppd =new PedProtocolDto();
				ppd=(PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			if(null != ppd){
				ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//?????????????????????
				if(PoolComm.BT_06.endsWith(ppd.getContractType())){
					ppd.setContractType(PoolComm.BT_12);
				}else if(PoolComm.BT_07.endsWith(ppd.getContractType())){
					ppd.setContractType(PoolComm.BT_11);
				}
					pedProtocolService.txStore(ppd);
					this.sendJSON("{isOk:\'true\'}");
				}
			} catch (Exception ex) {
				this.sendJSON("{isOk:\'" + ex.getMessage() + "\'}");
				logger.error(ex,ex);
				return "/pool/common/auditProtocol";
			}
			return "/pool/common/auditProtocol";
		}	
		/**
		 * 
		 * @Description: ????????????
		 * @return String  
		 * @author Ju Nana
		 * @date 2018-11-7 ??????2:36:50
		 */
		@RequestMapping("/refusePoolOpen")
		public String refusePoolOpen(PedProtocolDto pedProtocolDto) {
			try {				
				PedProtocolDto ppd =new PedProtocolDto();
				ppd=(PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			if(null != ppd){
				ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????????????????
				if(PoolComm.BT_06.endsWith(ppd.getContractType())){
					ppd.setContractType(PoolComm.BT_12);
				}else if(PoolComm.BT_07.endsWith(ppd.getContractType())){
					ppd.setContractType(PoolComm.BT_11);
				}
					pedProtocolService.txStore(ppd);
					this.sendJSON("{isOk:\'true\'}");
				}
			} catch (Exception ex) {
				this.sendJSON("{isOk:\'" + ex.getMessage() + "\'}");
				logger.error(ex,ex);
				return "/pool/common/auditProtocol";
			}
			return "/pool/common/auditProtocol";
		}	

	@InitBinder
	public void initBinder(WebDataBinder binder) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));

	}
		

	/**
	 * <p>
	 * ????????????: submitBatch|??????: ???????????????????????????
	 * </p>
	 */
	@RequestMapping("/openPoolAuditnew")
	public void openPoolAuditnew(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			User user = this.getCurrentUser();
//			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			//??????
			PedProtocolModDto newPed=(PedProtocolModDto) pedProtocolService.queryModProtocolById(pedProtocolDto.getPoolInfoId());
			//??????,???????????????
			PedProtocolDto ped = null;
			List<PedProtocolDto> proList =  pedProtocolService.queryProtocolInfo(null, null, newPed.getPoolAgreement(), null, null, null);
			if(proList!=null && proList.size()>0){
				ped = proList.get(0);
			}
			
			ped.setOfficeNet2(user.getDepartment().getName());
			ped.setOperatorName2(user.getName());
			//??????????????????
			AssetType assetTypeDto = pedAssetTypeService.queryPedAssetTypeByProtocol(ped,PoolComm.ED_BZJ_HQ);
			if(assetTypeDto!=null){
				BailDetail detail = poolBailEduService.queryBailDetail(assetTypeDto.getId());
				detail.setAssetNb(newPed.getMarginAccount());
				poolBailEduService.txStore(detail);
			}else{
				logger.error("?????????????????????????????????????????????");	
			}
			pedProtocolService.txSaveApproveOpclose(ped,newPed);
			
			AssetPool ap =pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(ped.getPoolAgreement(),null, ped.getCustnumber());
			if(ap==null){
				//??????asstPool??????
				pedProtocolService.createAssetPoolInfo(ped);
				//??????assetType??????
				for(int i=0;i<4;i++){
					String assetType = null;//??????????????????
					if(i==0){
						assetType = PoolComm.ED_PJC;//?????????????????????
					}else if(i==1){
						assetType = PoolComm.ED_PJC_01;//?????????????????????
					}else if(i==2){
						assetType = PoolComm.ED_BZJ_HQ;//???????????????
					}else if(i==3){
						assetType = PoolComm.ED_BZJ_DQ;//???????????????
					}
					//???????????? 
//					pedProtocolService.txCreateAssetTypeInfo(ped,assetType,newPed);
				}
			}
			
			this.sendJSON("{isOk:\'true\'}");
		} catch (Exception e) {
			logger.error(e, e);
			this.sendJSON("{isOk:\'" + e.getMessage() + "\'}");
		}
		this.sendJSON(json);
	}
	/**
	 * <p>
	 * ????????????: submitBatch|??????: ??????
	 * </p>
	 */
	@RequestMapping("/openPoolAuditnewCrt")
	public void openPoolAuditnewCrt(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			User user = this.getCurrentUser();
			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			if(null != pedProtocolDto){//???????????????????????????????????????
				if(PoolComm.BT_06.endsWith(pedProtocolDto.getContractType())){//
					pedProtocolDto.setContractType(PoolComm.BT_10);
				}else if(PoolComm.BT_07.endsWith(pedProtocolDto.getContractType())){
					pedProtocolDto.setContractType(PoolComm.BT_09);
				}
				pedProtocolService.txStore(pedProtocolDto);
				this.sendJSON("{isOk:\'true\'}");
			}
		} catch (Exception e) {
			logger.error(e, e);
			this.sendJSON("{isOk:\'" + e.getMessage() + "\'}");
		}
		this.sendJSON(json);
	}
	/**
	 * <p>
	 * ????????????: submitBatch|??????: ??????
	 * </p>
	 */
	@RequestMapping("/openPoolAuditnewOpen")
	public void openPoolAuditnewOpen(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			User user = this.getCurrentUser();
			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			String frozenstate = pedProtocolDto.getFrozenstate();//??????
			String frozenFlag = pedProtocolDto.getFrozenFlag();//??????
			if(null != pedProtocolDto){//????????????
				if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_01.equals(frozenstate)){//???????????????--???????????????
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);//?????????
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
				}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_02.equals(frozenstate)){//????????????--????????????
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);//?????????
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
				}else if(PoolComm.FROZEN_STATUS_OPEN_03.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//?????????--?????????
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);//?????????
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
				}else if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//?????????--???????????????
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_02);//????????????
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
				}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//?????????--????????????
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_01);//???????????????
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
				}
				if(PoolComm.BT_06.endsWith(pedProtocolDto.getContractType())){//
					pedProtocolDto.setContractType(PoolComm.BT_10);
				}else if(PoolComm.BT_07.endsWith(pedProtocolDto.getContractType())){
					pedProtocolDto.setContractType(PoolComm.BT_09);
				}
				pedProtocolService.txStore(pedProtocolDto);
				this.sendJSON("{isOk:\'true\'}");
			}
		} catch (Exception e) {
			logger.error(e, e);
			this.sendJSON("{isOk:\'" + e.getMessage() + "\'}");
		}
		this.sendJSON(json);
	}
	/**
	 * <p>
	 * ????????????: auditBatch|??????:?????????????????????
	 * </p>
	 * @return String
	 */
	@RequestMapping("/auditPoolJsonnew")
	public void auditPoolJsonnew(PedProtocolDto pedProtocolDto) {
		String bankNumber = this.getCurrentUser().getDepartment().getId();
		try {
			String json = "";
			Page page = this.getPage();
			List productIds = new ArrayList();
			productIds.add("13001");
			List result = pedProtocolService.queryBatchForAudit(productIds,pedProtocolDto, this.getCurrentUser(), page);
			json = JsonUtil.buildJson(result, page.getTotalCount());
			if (StringUtil.isBlank(json)) {
				json = this.RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e, e);
		}
	}
  
	
	/**
	 * ??????/??????????????????
	 * @author  liu xiaodong
	 * @date 2018-12-24 ??????1:47:18
	 */
	@RequestMapping("/toFreezeOpen")
	public String toFreezeOpen(PedProtocolDto pedProtocolDto, Model mode){
		try {
		pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId()); 			
		 mode.addAttribute("pedProtocolDto", pedProtocolDto);
			 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/freezeProtocolUpdate";
	}
	/**
	 * ??????/??????????????????
	 * @author liu xiaodong
	 * @date 2018-12-24 ??????1:47:18
	 */
	@RequestMapping("/toFreezeClose")
	public String toFreezeClose(PedProtocolDto pedProtocolDto, Model mode){
		try {
		pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId()); 			
		 mode.addAttribute("pedProtocolDto", pedProtocolDto);
			 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/common/freezeProtocolClose";
	}
	
	/**
	 * ??????????????????
	 * @author Ju Nana
	 * @date 2018-12-24 ??????1:48:16
	 */
	@RequestMapping("/frozenSaveOpen")
	public void frozenSaveOpen(PedProtocolDto pedProtocolDto){
		String json = "";
		try{
		PedProtocolDto  ppd  = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());	
		String frozenstate = pedProtocolDto.getFrozenstate();
		String frozenFlag = pedProtocolDto.getFrozenFlag();
		
		if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_01.equals(frozenstate)){//???????????????--???????????????
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//?????????
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
		}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_02.equals(frozenstate)){//????????????--????????????
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//?????????
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
		}else if(PoolComm.FROZEN_STATUS_OPEN_03.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//?????????--?????????
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//?????????
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
		}else if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//?????????--???????????????
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_02);//????????????
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
		}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//?????????--????????????
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_01);//???????????????
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//?????????
		}	
//		ppd.setFrozenFlag(frozenFlag);
//		ppd.setFrozenstate(frozenstate);
		ppd.setContractType(PoolComm.BT_09);//????????????????????????
		pedProtocolService.txStore(ppd);//ppd.setFrozenTime(new Date())
		json="{result:true}";
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex,ex);
			json="{result:false,message:'"+ex.getMessage()+"'}";
		}
		this.sendJSON(json);
	}
	/**
	 * ??????????????????
	 * @author Ju Nana
	 * @date 2018-12-24 ??????1:48:16
	 */
	@RequestMapping("/frozenSaveClose")
	public void frozenSaveClose(PedProtocolDto pedProtocolDto){
		String json = "";
		try{
		PedProtocolDto  ppd  = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
		String frozenstate = pedProtocolDto.getFrozenstate();
		//String frozenFlag = pedProtocolDto.getFrozenFlag();
		//String  frozenstateOld=frozenstate.substring(0,frozenstate.indexOf(","));
		//String  frozenstateNew=frozenstate.substring(frozenstate.indexOf(",")+1);//???????????????????????????
		ppd.setFrozenstate(frozenstate);
		ppd.setContractType(PoolComm.BT_10);//????????????????????????	
		pedProtocolService.txStore(ppd);//ppd.setFrozenTime(new Date())
		json="{result:true}";
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex,ex);
			json="{result:false,message:'"+ex.getMessage()+"'}";
		}
		this.sendJSON(json);
	}
	@RequestMapping("/bankNameTreeView")
	public String bankNameTreeView(){
		return "/common/bankNameTree";
	}
	/**
	 * ???????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/serviceCharge")
	public void serviceCharge(String poolAgreement,String poolAgreementName,String feeType) throws Exception {
		Page page=this.getPage();
		User user = this.getCurrentUser();
		String json = "";
		try {
			List list =pedAssetTypeService.queryserviceCharge(poolAgreement,poolAgreementName,feeType,user,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("????????????", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * ???????????????????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryServiceChargeDetail")
	public void queryServiceChargeDetail(String plCommId) throws Exception {
		Page page=this.getPage();
		String json = "";
		try {
			List list =pedAssetTypeService.queryserviceChargeDetail(plCommId,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("????????????", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * ???????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/standardServiceManage")
	public void standardServiceManage() throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =pedAssetTypeService.queryserviceChargeManage(page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("????????????", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	@RequestMapping("/saveServiceManage")
	public void saveServiceManage(PlFeeScale plFeeScale, Model mode) {
		if (plFeeScale != null) {
			if (StringUtil.isEmpty(plFeeScale.getId())) {
				plFeeScale.setCreateDate(Calendar.getInstance().getTime());
				plFeeScale.setId(null);
				plFeeScale.setProductType(PoolComm.FEE_01);
				pedAssetTypeService.txStore(plFeeScale);
			}
		}
		sendJSON("??????");
//		return "redirect:/toRuleList.mvc";
	}
	@RequestMapping("/delServiceManage")
	public void delServiceManage(String ids, Model mode) {
		if (!StringUtil.isEmpty(ids)) {
			String[] id = ids.split(",");
			PlFeeScale plFeeScale = null;
			List<PlFeeScale> list = new ArrayList<PlFeeScale>();
			for(int i=0;i<id.length;i++) {
				plFeeScale = new PlFeeScale();
				plFeeScale.setId(id[i]);
				list.add(plFeeScale);
				//pedRuleService.txDelete(tRule);
			}
			pedAssetTypeService.txDeleteAll(list);
		}
		sendJSON("????????????");
//		return "redirect:/toRuleList.mvc";
//		return "/pool/rule/listRule";
	}
	/**
	 * ??????????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryReconciliationInformation")
	public void queryReconciliationInformation(PedCheck ped) throws Exception {
		Page page = this.getPage();
		User user = this.getCurrentUser();
		String json = "";
		try {
			List list =pedProtocolService.queryPedCheckNew(ped,user,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("????????????", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * ??????????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/pushReconciliationInformation")
	public void pushReconciliationInformation(PedCheckBatch ped) throws Exception {
		Page page=this.getPage();
		User user = this.getCurrentUser();
		String json = "";
		try {
			List list =pedProtocolService.queryPedCheckBatchNew(ped,user,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("????????????", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * ????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/queryPedCheckDetail")
	public void queryPedCheckDetail(String plCommId) throws Exception {
		Page page=this.getPage();
		String json = "";
		try {
			List list =pedProtocolService.queryPedCheckListBybatch(plCommId,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("????????????", e);
			//this.addActionMessage(e.getMessage());
		}
	}

	/**
	 * ????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("comePushReconciliaton")
	public void comePushReconciliaton(String ids){
		String json = "";
		boolean flag = true;
		String msg = "";
		try {
			if (!StringUtil.isEmpty(ids)) {
				String[] id = ids.split(",");
				for(int i=0;i<id.length;i++) {
					PedCheckBatch pedBatch=pedProtocolService.queryPedCheckBatchId(id[i]);
					PedCheck pedCheckOld = pedProtocolService.queryPedCheck(pedBatch.getPoolAgreement(),pedBatch.getCustNo());
					if(null != pedCheckOld  && pedCheckOld.getAccountDate().compareTo(pedBatch.getAccountDate()) >= 0){
						flag = false;
						msg = msg + pedCheckOld.getCustName()+"????????????"+ DateUtils.toDateString(pedCheckOld.getAccountDate()) +"??????????????????,??????????????????????????????????????????!!!";
					}else{
						PedCheck pedCheck =new PedCheck();
						BeanUtils.copyProperties(pedCheck, pedBatch);
//						PedCheck pedCheckOld = pedProtocolService.queryPedCheckNo(id[i]);
//						if(null == pedCheckOld){
							pedCheck.setId(null);
//						}
						pedCheck.setIsAuto(PoolComm.NO);
						pedCheck.setCheckResult(PoolComm.DZJG_00);
						pedCheck.setCurTime(new Date());
						
						
						String checkResult = "";
						BigDecimal marginbalance = null;
						if(pedBatch.getMarginBalance()!=null){
							marginbalance = pedBatch.getMarginBalance();
						}else{
							marginbalance = BigDecimal.ZERO;
						}
						if("0".equals(pedBatch.getBillTotalNum())&&( pedBatch.getMarginBalance()==null || pedBatch.getMarginBalance().compareTo(BigDecimal.ZERO)<=0)){//???????????????????????????0???????????????????????????0??????????????????????????????????????????
							checkResult = PoolComm.DZJG_01;//????????????--???????????? 
						}else{
							checkResult = PoolComm.DZJG_00;//????????????--????????? 
						}
						
						pedCheck.setCheckResult(checkResult);
						pedProtocolService.txStore(pedCheck);
					}					
				}
			}
			if(flag){
				json = "{'result':true}";
			}else{
				json = "{'result':false,'message':'" + msg + "'}";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			json = "{'result':false,'message':'" + e.getMessage() + "'}";
		}
		this.sendJSON(json);
	}
	/**
	 * ????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/delReconciliation")
	public String delReconciliation(String ids, Model mode) throws Exception {
		if (!StringUtil.isEmpty(ids)) {
			String[] id = ids.split(",");
			for(int i=0;i<id.length;i++) {
				PedCheck pedCheck=pedProtocolService.queryPedCheckNo(id[i]);
				pedProtocolService.txDelete(pedCheck);
			}
		}
		return "/pool/riskList/queryGrayListBank";
	}
	/**
	 * ????????????
	 * liuxiaodong
	 * @throws Exception
	 */
	@RequestMapping("/pedProDetailList")
	public void pedProDetailList(String plCommId,String custIdentity,String status) throws Exception {
		Page page = this.getPage();
		String json = "";
		try {
			List list =pedProtocolService.queryPedListByParamDetail(plCommId,custIdentity,status,page);
			if (list != null && list.size() > 0) {
				json = this.toJson(page, list);
			}
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("????????????", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * ????????????????????????
	 * @author liu xiaodong
	 * @date 2018-12-24 ??????1:48:16
	 */
	@RequestMapping("/CustomerManagerMove")
	public void CustomerManagerMove(PedProtocolDto pedProtocolDto){
		String json = "";
		try{
		PedProtocolDto  ppd  = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());	
		ppd.setAccountManager(pedProtocolDto.getAccountManager());
		ppd.setAccountManagerId(pedProtocolDto.getAccountManagerId());
		pedProtocolService.txStore(ppd);//ppd.setFrozenTime(new Date())
		json="{result:true}";
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex,ex);
			json="{result:false,message:'"+ex.getMessage()+"'}";
		}
		this.sendJSON(json);
	}
	
	/**
	 * ????????????????????????
	 * @author Ju Nana
	 * @param accountManagerId  ???????????????
	 * @param accountManagerIdOld	???????????????
	 * @param mode
	 * @throws Exception
	 * @date 2019-10-22??????9:37:24
	 */
	@RequestMapping("queryUserManage")
	public void queryUserManage(String accountManagerId ,String accountManagerIdOld,Model mode) throws Exception{
		String json="0";
		User userLogon = this.getCurrentUser();//?????????????????????
		
		try {
			/*
			 * ????????????????????????
			 * 0?????????????????????????????????...
			 * 1???????????????????????????????????????
			 * 2??????????????????Id??????
			 * 4???????????????????????????
			 * 5??????????????????ID?????????????????????
			 * 6???????????????????????????????????????????????????????????????????????????????????????
			 * 7?????????????????????????????????????????????????????????????????????????????????
			 */
			json = pedProtocolService.userManageChangeCheck(accountManagerId, accountManagerIdOld, userLogon);
			
		} catch (Exception e) {
			json ="0";//??????????????????????????????...
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * ?????? ????????????
	 * @author liu xiaodong
	 * @date 2018-12-24 ??????1:48:16
	 */
	@RequestMapping("/OfficeNetMove")
	public void OfficeNetMove(PedProtocolDto pedProtocolDto){
		String json = "";
		try{
		PedProtocolDto  ppd  = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
		Department depart= (Department) pedProtocolService.load(pedProtocolDto.getOfficeNet(),Department.class);
		ppd.setOfficeNet(depart.getInnerBankCode());
		ppd.setOfficeNetName(depart.getName());
		pedProtocolService.txStore(ppd);//ppd.setFrozenTime(new Date())
		json="{result:true}";
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex,ex);
			json="{result:false,message:'"+ex.getMessage()+"'}";
		}
		this.sendJSON(json);
	}
	/**
	 * ????????????
	 * @author gcj
	 * @date 20210604
	 */
	@RequestMapping("/changeEdu")
	public void changeEdu(PedProtocolDto pedProtocolDto){
		String json = "";
		PedProtocolDto dto = null;
		try{
			//????????????
			String flowNo = Long.toString(System.currentTimeMillis());
			dto = (PedProtocolDto)pedProtocolService.load(pedProtocolDto.getPoolInfoId(),PedProtocolDto.class);
			String oldMood = dto.getPoolMode();
			dto.setPoolMode(pedProtocolDto.getPoolMode());
			Ret eduCheckRet = financialService.txCreditUsedCheck(null, dto, flowNo);
			
			if(Constants.TX_SUCCESS_CODE.equals(eduCheckRet.getRET_CODE())){
				ReturnMessageNew returnMessge =pedProtocolService.txChangeEdu(pedProtocolDto);
				if(returnMessge.isTxSuccess()){
					json="????????????????????????";
				}else{
					json="?????????????????????"+returnMessge.getRet().getRET_MSG();
				}						
			}else{
				dto.setPoolMode(oldMood);
				pedProtocolService.txStore(dto);//???????????????hibernate???set?????????????????????
				json="??????????????????????????????????????????????????????";
				this.sendJSON(json);
				return;
				
			}
			
		}catch(Exception e){
			logger.error("????????????????????????"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+e.getMessage());
			return;
		}
		this.sendJSON(json);
		
		//????????????????????????????????????
		try {			
			PedProtocolDto pro  = pedProtocolService.queryProtocolDto( null, null,  dto.getPoolAgreement(),null, null, null);
			financialService.txRefreshFinancial(pro,null);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}
	/**
	 * ???????????????
	 * @author gcj
	 * @date 20210604
	 */
	@RequestMapping("/depositDraw")
	public void depositDraw(PedProtocolDto pedProtocolDto){
		String json = "";
		try{
		PedProtocolDto  ppd  = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());	
		String isAcctCheck = pedProtocolDto.getIsAcctCheck();
		ppd.setIsAcctCheck(isAcctCheck);
		pedProtocolService.txStore(ppd);
		json="{result:true}";
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex,ex);
			json="{result:false,message:'"+ex.getMessage()+"'}";
		}
		this.sendJSON(json);
	}

	/**
	 * loadContractJSON ???????????????????????????
	 * @author gcj
	 * @date 20210604
	 */
	@RequestMapping("/loadContractJSON")
	public void loadContractJSON(ProtocolQueryBean pedProtocolDto) {
		
		String json = "";
		try {
			json = pedProtocolService.loadContractJSON(pedProtocolDto, queryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			logger.debug("load pedProtocolDto JSON:" + json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}
		sendJSON(json);
	}
	
	/**
	 * loadContractJSON ?????????????????????????????????
	 * @author wfj
	 * @date 20220215
	 */
	@RequestMapping("/ContractJSONExport")
	public void ContractJSONExport(HttpServletResponse response,ProtocolQueryBean pedProtocolDto) {
		
		/*String json = "";
		try {
			json = pedProtocolService.loadContractJSON(pedProtocolDto, queryBean, this.getCurrentUser(), this.getPage());
			if (!(StringUtil.isNotBlank(json))) {
				json = RESULT_EMPTY_DEFAULT;
			}
			logger.debug("load pedProtocolDto JSON:" + json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			logger.error(e.getMessage(),e);
		}
		sendJSON(json);*/
		
		ExcelWriter excelWriter = null;
		try {
			User user = this.getCurrentUser();
			List result = new ArrayList();
			String dateTime = DateUtils.toString(new Date(), DateUtils.ORA_DATE_TIME_FORMAT);
			response.setContentType("application/vnd.ms-excel");
		    response.setCharacterEncoding("utf-8");
		    String fileName = URLEncoder.encode("???????????????????????????", "UTF-8");
		    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
			Map<String, String> mapinfo = new LinkedHashMap();
			
			mapinfo.put("poolAgreement", "???????????????");
			mapinfo.put("contract", "???????????????");
			mapinfo.put("custname", "???????????????");
			mapinfo.put("creditamount", "????????????");
			mapinfo.put("poolModeName", "????????????");
			mapinfo.put("contractEffectiveDt", "???????????????");
			mapinfo.put("contractDueDt", "???????????????");
			
			excelWriter = EasyExcel.write(response.getOutputStream()).head(ExcelUtil.head(mapinfo)).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("???????????????????????????").head(ExcelUtil.head(mapinfo)).build();
			Page page = new Page();
			do{
				page.setPageSize(ExcelUtil.PageSize);
				page.setPageIndex(ExcelUtil.pageIndex);
				result = pedProtocolService.loadContractList(pedProtocolDto, queryBean, user, page);
				List values1 = ExcelUtil.convertBeanToArray(result, mapinfo);
				logger.info(values1.size()+"--------------------------------");
				excelWriter.write(ExcelUtil.dataList(values1), writeSheet);
				ExcelUtil.pageIndex++;	
			}while(result.size()!=0);
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
		} catch (Exception e) {
			excelWriter.finish();
		    ExcelUtil.pageIndex = 1;
			logger.error("??????????????????????????????",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
	
	
	
}
