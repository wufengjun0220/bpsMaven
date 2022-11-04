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

	private PoolQueryBean queryBean = new PoolQueryBean();//组合查询对象


    /**
     * 
     * @Description: 跳转到协议管理首界面
     * @author Ju Nana
     * @date 2018-11-5 下午4:22:21
     */
	@RequestMapping("/openpoollist")
	public String openpoollist() {
		return "/pool/common/listProtocol";
	}

	/**
	 * <p>
	 * loadPoolJSON 查询票据池协议信息，带模糊查询
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
	 * loadPoolJSON 查询票据池协议信息，带模糊查询 用于综合查询
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
			mapinfo.put("poolAgreement", "票据池编号");
			mapinfo.put("custName", "客户名称");
			mapinfo.put("custnumber", "客户号");
			mapinfo.put("creditamount", "担保合同金额");
			mapinfo.put("custOrgcode", "组织机构代码");
			mapinfo.put("effstartdate", "协议生效日");
			mapinfo.put("effenddate", "协议到期日");
			mapinfo.put("isGroupName", "是否集团");
			mapinfo.put("vtStatusName", "签约功能状态");
			mapinfo.put("openFlagName", "融资功能状态");
			mapinfo.put("frozenstateName", "冻结标识");
			mapinfo.put("accountManager", "客户经理名称");
			mapinfo.put("accountManagerId", "客户经理ID");
			mapinfo.put("signDeptName", "签约机构");
			mapinfo.put("creditDeptName", "融资机构");
			mapinfo.put("officeNetName", "受理网点");

			Map mapfileds = new LinkedHashMap();
			byte[] buffer = dictCommonService.creatSheetModel(list, ColumnNames, mapinfo, mapfileds, num, typeName);
			OutputStream os = getResponse().getOutputStream();
			getResponse().setContentType("application/octet-stream");
			getResponse().addHeader("Content-Disposition",
					"attachment; filename=" + URLEncoder.encode("票据池签约.xls", "utf-8"));
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
	 * @Description: 票据池协议信息展示页面，点击新增按钮，跳转到新增页面
	 * @author Ju Nana
	 * @date 2018-11-7 下午2:44:33
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
	 * @Description:根据客户号 调用核心接口返回信息
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
	 * @Description:根据客户号 调用核心接口返回信息   并保存
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
				//协议表修改
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
				//集团成员表修改
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
	 * @Description:根据客户号 调用核心接口返回信息   并保存
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
				//协议表修改
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
				//集团成员表修改
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
				//客户信息登记表修改
				CustomerRegister customer=new CustomerRegister();
				customer.setCustNo(SAccountNo);
				CustomerRegister cost=customerService.loadCustomerRegister(customer);
				if(null!=cost){
					cost.setCustName(pedOld.getCustname());
					customerService.txStore(cost);
				}
				//在线银承修改
				OnlineQueryBean query =new OnlineQueryBean();
				query.setCustNumber(SAccountNo);
				PedOnlineAcptProtocol acpt=pedOnlineAcptService.queryOnlineAcptProtocol(query);
				if(null!=acpt){
					acpt.setCustName(pedOld.getCustname());
					pedOnlineAcptService.txStore(acpt);
				}
				//在线流贷修改
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
	 * @Description:根据保证金账号 调用核心接口返回信息
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
					String clientNo=(String) map.get("clientNo");//客户号
					String productNo=(String) map.get("productNo");//产品编号
					String balnceStr=(String) map.get("balnce");//余额*/
					BigDecimal balnce=new BigDecimal(balnceStr);
					if(null != pedProtocolDto){
						if(!clientNo.equals(custnumber)){
							json = "2";
						}else if(!"2209022".equals(productNo)){
							json ="3";
						}else if(PoolComm.OPEN_01.equals(pedProtocolDto.getOpenFlag())){
							BailDetail bail=poolBailEduService.queryBailDetailByBpsNo(pedProtocolDto.getPoolAgreement());
							if(null != bail){
								BigDecimal limit=bail.getAssetLimitUsed();//已使用的保证金
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
	 * @Description:根据结算账号 调用核心接口返回信息
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
	 * 
	 * @Description: 票据池协议新增
	 * @param  pedProtocolDto 界面传入信息
	 * @author Ju Nana
	 * @date 2018-11-5 下午4:13:21
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
				throw new Exception("该客户已开通过票据池业务,如需开通请选择重启！");
			}
		} catch (Exception e) {
			this.sendJSON(e.getMessage() );
			logger.error(e,e);
			return "/pool/common/listProtocol";
		}
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			PedProtocolDto  ppd = new PedProtocolDto();
			BigDecimal BAIFEN=new BigDecimal(0.01);      //例如：90%--->0.90
			BigDecimal QIANFEN=new BigDecimal(0.001);    //例如：5‰----》0.005

			//新增质押担保合同编号和费率
			ppd.setGuarantNumber(pedProtocolDto.getGuarantNumber());
			ppd.setDisRate(BigDecimalUtils.multiply3(pedProtocolDto.getDisRate(),QIANFEN ));   //费率
			ppd.setIsGroup(pedProtocolDto.getIsGroup());//是否集团：否
			ppd.setCustnumber(pedProtocolDto.getCustnumber());//客户号
			ppd.setCustOrgcode(pedProtocolDto.getCustOrgcode()); // 客户组织结构代码
			ppd.setCustname(pedProtocolDto.getCustname());//客户名称
			ppd.setPoolAccount(pedProtocolDto.getPoolAccount());//客户结算账户
			ppd.setCustlevel(pedProtocolDto.getCustlevel());//客户等级
			ppd.setPoolAccountName(pedProtocolDto.getPoolAccountName());//结算账户名称
			ppd.setMarginAccount(pedProtocolDto.getMarginAccount());//保证金账户
			ppd.setDiscountRatio(BigDecimalUtils.multiply(pedProtocolDto.getDiscountRatio(), BAIFEN)); //质押率：默认100%
			ppd.setIsMarginGroup(pedProtocolDto.getIsMarginGroup());//保证金是否归集
			ppd.setAssetType(pedProtocolDto.getAssetType());//资产类型：票据池
			ppd.setPoolMode(pedProtocolDto.getPoolMode());//池模式：默认总量控制
			ppd.setLicename(pedProtocolDto.getLicename());//客户经办人名称
			ppd.setAuthperson(pedProtocolDto.getAuthperson());//客户经办人身份证号
			ppd.setPhonenumber(pedProtocolDto.getPhonenumber());//客户经办人手机号
			ppd.setOfficeNet(pedProtocolDto.getOfficeNet());//受理网点
			ppd.setOperatorName1(pedProtocolDto.getOperatorName1());//受理人
			ppd.setZyflag(pedProtocolDto.getZyflag());     
			ppd.setXyflag(pedProtocolDto.getXyflag());
			pedProtocolDto.setOfficeNet(this.getCurrentUser().getDepartment().getName());//TODO  Asteria 经办单位后续用本机构号获取
			//ppd.setEffestate(PoolComm.SP002);//开通标记：未生效
			//ppd.setBusiType(PoolComm.BT_01);//业务类型：开通
			ppd.setContractType(PoolComm.CT_01);//实体票据池
			ppd.setPoolMode(PoolComm.POOL_MODEL_01);//默认为总量控制
			ppd.setOperateTime(new Date());//操作时间
			pedProtocolService.txStore(ppd);
			return "/pool/common/listProtocol";
		
	}
	
	/**
	 * 
	 * @Description:签约信息 查看功能，跳转至查看界面
	 * @author Ju Nana
	 * @date 2018-11-5 下午4:24:43
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
			/*BigDecimal BAIFEN =new BigDecimal(100);      //例如：0.90-->90%--->
			BigDecimal QIANFEN=new BigDecimal(1000);    //例如：0.005--->5‰----》

			pedProtocolDto.setDiscountRatio(BigDecimalUtils.multiply2(pedProtocolDto.getDiscountRatio(),BAIFEN));
			pedProtocolDto.setDisRate(BigDecimalUtils.multiply2(pedProtocolDto.getDisRate(),QIANFEN));   //费率
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
	 * @Description: 协议信息删除
	 * @param  protocolId  ped_protorol表主键id
	 * @param  pedProtocolDto
	 * @author Ju Nana
	 * @date 2018-10-31 下午2:44:47
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
	 * @Description: 跳转到编辑票据池协议信息页面
	 * @param  pedProtocolDto
	 * @author Ju Nana
	 * @date 2018-11-5 下午4:51:09
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
	 * @Description: 编辑功能
	 * @param pedProtocolDto
	 * @author Ju Nana
	 * @date 2018-11-5 下午4:59:43
	 */
	@RequestMapping("/editPool")
	public String txeditPool(PedProtocolDto pedProtocolDto){
		PedProtocolDto ppd=null;
		PedProtocolModDto  newPpd = new PedProtocolModDto();//  custnumber  客户号  custname客户名称 zyflag是否质押入池 marginAccount保证金账号  marginAccountName保证金户名
		//isMarginGroup是否归集   poolAccount结算账户 poolAccountName 结算户名  officeNet受理网点  operatorName1受理人姓名
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
		newPpd.setOperateTime(new Date());//操作时间
		ppd.setApproveFlag(PoolComm.APPROVE_01);//签约审批中
		newPpd.setApproveFlag(PoolComm.APPROVE_01);//签约审批中
		newPpd.setFrozenstate(PoolComm.FROZEN_STATUS_00);
		newPpd.setContractType(PoolComm.BT_08);
		//更新保证金detail表
		
			pedProtocolService.txStore(ppd);
			pedProtocolService.txStore(newPpd);
		} catch (Exception e) {
			logger.error("签约信息维护，获取资产池或者保证金明细错误！", e);
		}
		
	    BailDetail curBail =AssetFactory.newCurBailDetail();
		
		return "/pool/common/listProtocol";
		}
	/**
	 * 
	 * @Description: 协议开通功能
	 * @author Ju Nana
	 * @date 2018-11-5 下午8:18:29
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
	 * @Description: 额度冻结/解约功能
	 * @author liu xiaodong
	 * @date 2018-11-5 下午8:18:29
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
	 * @Description: 跳转到票据池协议复核管理页面
	 * @author Ju Nana
	 * @date 2018-11-7 下午2:39:57
	 */
	@RequestMapping("/fuhelist")
	public String fuhelist() {
		return "/pool/common/auditProtocol";
	}
	

	/**
	 * 解约协议信息
	 * @Description:判断客户无质押票据无用信业务等 
	 * @author Ju Nana
	 * @date 2018-11-7 下午2:38:26
	 */
	@RequestMapping("/closePool")
	public String closePool(PedProtocolDto pedProtocolDto) {
		try {
			PedProtocolDto ppd = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			BigDecimal bailTotalAmt = new BigDecimal("0.0");//保证金账户金额
			BigDecimal draftUsedAmt = new BigDecimal("0.0");//票据池已用额度
			//调用核心保证金查询接口，同步保证金信息
			CoreTransNotes transNotes = new CoreTransNotes();
			transNotes.setAccNo(ppd.getMarginAccount());
			transNotes.setCurrentFlag("1");
			ReturnMessageNew resp = poolCoreService.PJH716040Handler(transNotes, "0");
			if (resp.isTxSuccess()) {
				Map map = resp.getBody();
				if (map.get("BALANCE") != null) { //核心账户余额
					bailTotalAmt = BigDecimalUtils.valueOf((String) map.get("BALANCE"));
				}
			}
			//查询票据总额度
			AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(ppd, PoolComm.ED_PJC);
			if(at!=null){
				draftUsedAmt = at.getCrdtUsed();
			}
			//根据核心客户号查询有无在途票据
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setCustomernumber(ppd.getCustnumber());//核心客户号
			ArrayList<String> statusList = new ArrayList<String>();
			statusList.add(PoolComm.DS_00);//非初始化的票据
			statusList.add(PoolComm.DS_04);//非初出池完毕的
			statusList.add(PoolComm.TS05);//非初托收记账完毕的
			queryBean.setStatus(statusList);
			List<PoolBillInfo> infos = draftPoolQueryService.queryPoolBillInfoByPram(queryBean);
			
			if(bailTotalAmt.compareTo(BigDecimal.ZERO)!=0){
				throw new Exception("该客户保证金账户金额不为0，不允许解约");
			}else if(draftUsedAmt.compareTo(BigDecimal.ZERO)>0){
				throw new Exception("该客户有发生融资业务，不允许解约");
			}else if(infos!=null && infos.size()>0){
				logger.debug("在途票据张数【"+infos.size()+"】");
				throw new Exception("该客户有在途票据，不允许解约");
			}else{//解约
				//原表
				ppd.setOperateTime(new Date());
				ppd.setApproveFlag(PoolComm.APPROVE_04);
				//备表
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
		 * @Description: 签约复核拒绝
		 * @return String  
		 * @author Ju Nana
		 * @date 2018-11-7 下午2:36:50
		 */
		@RequestMapping("/refusePool")
		public String refusePool(PedProtocolDto pedProtocolDto) {
			try {				
				PedProtocolDto ppd =new PedProtocolDto();
				//备表
				PedProtocolModDto newPpd = (PedProtocolModDto) pedProtocolService.queryModProtocolById(pedProtocolDto.getPoolInfoId());
				//原表
				List<PedProtocolDto> proList =  pedProtocolService.queryProtocolInfo(null, null, null, newPpd.getCustnumber(), null, null);
				if(proList!=null && proList.size()>0){
					ppd = proList.get(0);
				}
				
				
				String approveFlag = ppd.getApproveFlag();
				if(PoolComm.APPROVE_01.equals(approveFlag)){//签约审核中
					ppd.setApproveFlag(PoolComm.APPROVE_02);//签约审核拒绝
					newPpd.setApproveFlag(PoolComm.APPROVE_02);//签约审核拒绝
				}
				if(PoolComm.APPROVE_04.equals(approveFlag)){//解约审核中
					ppd.setApproveFlag(PoolComm.APPROVE_05);//解约审核拒绝
					newPpd.setApproveFlag(PoolComm.APPROVE_05);//解约审核拒绝
				}
				//原表
				User user = this.getCurrentUser();
				ppd.setOfficeNet2(user.getDepartment().getName());
				ppd.setOperatorName2(user.getName());
				ppd.setOperateTime(new Date());
				//备表
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
		 * @Description: 冻结拒绝
		 * @return String  
		 * @author Ju Nana
		 * @date 2018-11-7 下午2:36:50
		 */
		@RequestMapping("/refusePoolCrt")
		public String refusePoolCrt(PedProtocolDto pedProtocolDto) {
			try {				
				PedProtocolDto ppd =new PedProtocolDto();
				ppd=(PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			if(null != ppd){
				ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//置为未冻结状态
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
		 * @Description: 解冻拒绝
		 * @return String  
		 * @author Ju Nana
		 * @date 2018-11-7 下午2:36:50
		 */
		@RequestMapping("/refusePoolOpen")
		public String refusePoolOpen(PedProtocolDto pedProtocolDto) {
			try {				
				PedProtocolDto ppd =new PedProtocolDto();
				ppd=(PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			if(null != ppd){
				ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为未解冻状态
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
	 * 方法名称: submitBatch|描述: 票据池协议开通审批
	 * </p>
	 */
	@RequestMapping("/openPoolAuditnew")
	public void openPoolAuditnew(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			User user = this.getCurrentUser();
//			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			//备表
			PedProtocolModDto newPed=(PedProtocolModDto) pedProtocolService.queryModProtocolById(pedProtocolDto.getPoolInfoId());
			//原表,通过客户号
			PedProtocolDto ped = null;
			List<PedProtocolDto> proList =  pedProtocolService.queryProtocolInfo(null, null, newPed.getPoolAgreement(), null, null, null);
			if(proList!=null && proList.size()>0){
				ped = proList.get(0);
			}
			
			ped.setOfficeNet2(user.getDepartment().getName());
			ped.setOperatorName2(user.getName());
			//保存协议信息
			AssetType assetTypeDto = pedAssetTypeService.queryPedAssetTypeByProtocol(ped,PoolComm.ED_BZJ_HQ);
			if(assetTypeDto!=null){
				BailDetail detail = poolBailEduService.queryBailDetail(assetTypeDto.getId());
				detail.setAssetNb(newPed.getMarginAccount());
				poolBailEduService.txStore(detail);
			}else{
				logger.error("签约信息维护，获取资产池为空！");	
			}
			pedProtocolService.txSaveApproveOpclose(ped,newPed);
			
			AssetPool ap =pedAssetPoolService.queryPedAssetPoolByOrgCodeOrCustNo(ped.getPoolAgreement(),null, ped.getCustnumber());
			if(ap==null){
				//创建asstPool信息
				pedProtocolService.createAssetPoolInfo(ped);
				//创建assetType信息
				for(int i=0;i<4;i++){
					String assetType = null;//产生额度类型
					if(i==0){
						assetType = PoolComm.ED_PJC;//低风险票据额度
					}else if(i==1){
						assetType = PoolComm.ED_PJC_01;//高风险票据额度
					}else if(i==2){
						assetType = PoolComm.ED_BZJ_HQ;//活期保证金
					}else if(i==3){
						assetType = PoolComm.ED_BZJ_DQ;//定期保证金
					}
					//修改方法 
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
	 * 方法名称: submitBatch|描述: 冻结
	 * </p>
	 */
	@RequestMapping("/openPoolAuditnewCrt")
	public void openPoolAuditnewCrt(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			User user = this.getCurrentUser();
			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			if(null != pedProtocolDto){//冻结同意审批只改变审批状态
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
	 * 方法名称: submitBatch|描述: 解冻
	 * </p>
	 */
	@RequestMapping("/openPoolAuditnewOpen")
	public void openPoolAuditnewOpen(PedProtocolDto pedProtocolDto) {
		String json = "";
		try {
			User user = this.getCurrentUser();
			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			String frozenstate = pedProtocolDto.getFrozenstate();//冻结
			String frozenFlag = pedProtocolDto.getFrozenFlag();//解冻
			if(null != pedProtocolDto){//解冻同意
				if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_01.equals(frozenstate)){//保证金冻结--保证金解冻
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);//未冻结
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
				}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_02.equals(frozenstate)){//票据冻结--票据解冻
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);//未冻结
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
				}else if(PoolComm.FROZEN_STATUS_OPEN_03.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//全冻结--全解冻
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_00);//未冻结
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
				}else if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//全冻结--保证金解冻
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_02);//票据冻结
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
				}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//全冻结--票据解冻
					pedProtocolDto.setFrozenstate(PoolComm.FROZEN_STATUS_01);//保证金冻结
					pedProtocolDto.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
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
	 * 方法名称: auditBatch|描述:待审批信息列表
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
	 * 冻结/解冻跳转操作
	 * @author  liu xiaodong
	 * @date 2018-12-24 下午1:47:18
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
	 * 冻结/解冻跳转操作
	 * @author liu xiaodong
	 * @date 2018-12-24 下午1:47:18
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
	 * 解冻保存操作
	 * @author Ju Nana
	 * @date 2018-12-24 下午1:48:16
	 */
	@RequestMapping("/frozenSaveOpen")
	public void frozenSaveOpen(PedProtocolDto pedProtocolDto){
		String json = "";
		try{
		PedProtocolDto  ppd  = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());	
		String frozenstate = pedProtocolDto.getFrozenstate();
		String frozenFlag = pedProtocolDto.getFrozenFlag();
		
		if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_01.equals(frozenstate)){//保证金冻结--保证金解冻
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//未冻结
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
		}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_02.equals(frozenstate)){//票据冻结--票据解冻
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//未冻结
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
		}else if(PoolComm.FROZEN_STATUS_OPEN_03.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//全冻结--全解冻
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_00);//未冻结
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
		}else if(PoolComm.FROZEN_STATUS_OPEN_01.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//全冻结--保证金解冻
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_02);//票据冻结
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
		}else if(PoolComm.FROZEN_STATUS_OPEN_02.equals(frozenFlag) && PoolComm.FROZEN_STATUS_03.equals(frozenstate)){//全冻结--票据解冻
			ppd.setFrozenstate(PoolComm.FROZEN_STATUS_01);//保证金冻结
			ppd.setFrozenFlag(PoolComm.FROZEN_STATUS_OPEN_00);//置为空
		}	
//		ppd.setFrozenFlag(frozenFlag);
//		ppd.setFrozenstate(frozenstate);
		ppd.setContractType(PoolComm.BT_09);//改为解冻审批通过
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
	 * 冻结保存操作
	 * @author Ju Nana
	 * @date 2018-12-24 下午1:48:16
	 */
	@RequestMapping("/frozenSaveClose")
	public void frozenSaveClose(PedProtocolDto pedProtocolDto){
		String json = "";
		try{
		PedProtocolDto  ppd  = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
		String frozenstate = pedProtocolDto.getFrozenstate();
		//String frozenFlag = pedProtocolDto.getFrozenFlag();
		//String  frozenstateOld=frozenstate.substring(0,frozenstate.indexOf(","));
		//String  frozenstateNew=frozenstate.substring(frozenstate.indexOf(",")+1);//获取第二个选中状态
		ppd.setFrozenstate(frozenstate);
		ppd.setContractType(PoolComm.BT_10);//改为冻结审批通过	
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
	 * 服务费查询
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
			logger.error("系统异常", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * 服务费查询逐笔明细
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
			logger.error("系统异常", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * 服务费查询
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
			logger.error("系统异常", e);
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
		sendJSON("成功");
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
		sendJSON("删除成功");
//		return "redirect:/toRuleList.mvc";
//		return "/pool/rule/listRule";
	}
	/**
	 * 对账任务查询
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
			logger.error("系统异常", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * 对账查询推送
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
			logger.error("系统异常", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * 对账明细
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
			logger.error("系统异常", e);
			//this.addActionMessage(e.getMessage());
		}
	}

	/**
	 * 对账推送
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
						msg = msg + pedCheckOld.getCustName()+"客户已于"+ DateUtils.toDateString(pedCheckOld.getAccountDate()) +"生成对账文件,不得推送早于该日期的对账文件!!!";
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
						if("0".equals(pedBatch.getBillTotalNum())&&( pedBatch.getMarginBalance()==null || pedBatch.getMarginBalance().compareTo(BigDecimal.ZERO)<=0)){//保证金金额小于等于0且出质票据总金额为0，则无需对账，默认为核对无误
							checkResult = PoolComm.DZJG_01;//对账结果--核对一致 
						}else{
							checkResult = PoolComm.DZJG_00;//对账结果--未对账 
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
	 * 对账撤回
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
	 * 集团成员
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
			logger.error("系统异常", e);
			//this.addActionMessage(e.getMessage());
		}
	}
	/**
	 * 客户经理移交保存
	 * @author liu xiaodong
	 * @date 2018-12-24 下午1:48:16
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
	 * 客户经理移交校验
	 * @author Ju Nana
	 * @param accountManagerId  新客户经理
	 * @param accountManagerIdOld	原客户经理
	 * @param mode
	 * @throws Exception
	 * @date 2019-10-22上午9:37:24
	 */
	@RequestMapping("queryUserManage")
	public void queryUserManage(String accountManagerId ,String accountManagerIdOld,Model mode) throws Exception{
		String json="0";
		User userLogon = this.getCurrentUser();//当前登录的柜员
		
		try {
			/*
			 * 客户经理移交校验
			 * 0：查询失败，请稍候再试...
			 * 1：查询客户经理信息不存在！
			 * 2：新客户经理Id为空
			 * 4：原客户经理不存在
			 * 5：录入新用户ID非客户经理角色
			 * 6：被移交客户经理不属于登录用户的平级或下辖机构，不允许移交
			 * 7：新客户经理不属于登录用户的平级或下辖机构，不允许移交
			 */
			json = pedProtocolService.userManageChangeCheck(accountManagerId, accountManagerIdOld, userLogon);
			
		} catch (Exception e) {
			json ="0";//查询失败，请稍候再试...
			logger.error(e.getMessage(),e);
		}
		this.sendJSON(json);
	}
	
	/**
	 * 机构 移交保存
	 * @author liu xiaodong
	 * @date 2018-12-24 下午1:48:16
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
	 * 额度变更
	 * @author gcj
	 * @date 20210604
	 */
	@RequestMapping("/changeEdu")
	public void changeEdu(PedProtocolDto pedProtocolDto){
		String json = "";
		PedProtocolDto dto = null;
		try{
			//变更校验
			String flowNo = Long.toString(System.currentTimeMillis());
			dto = (PedProtocolDto)pedProtocolService.load(pedProtocolDto.getPoolInfoId(),PedProtocolDto.class);
			String oldMood = dto.getPoolMode();
			dto.setPoolMode(pedProtocolDto.getPoolMode());
			Ret eduCheckRet = financialService.txCreditUsedCheck(null, dto, flowNo);
			
			if(Constants.TX_SUCCESS_CODE.equals(eduCheckRet.getRET_CODE())){
				ReturnMessageNew returnMessge =pedProtocolService.txChangeEdu(pedProtocolDto);
				if(returnMessge.isTxSuccess()){
					json="额度模式变更成功";
				}else{
					json="额度变更失败："+returnMessge.getRet().getRET_MSG();
				}						
			}else{
				dto.setPoolMode(oldMood);
				pedProtocolService.txStore(dto);//这里是解决hibernate的set自动落库的问题
				json="当前存量融资业务不支持额度模式变更。";
				this.sendJSON(json);
				return;
				
			}
			
		}catch(Exception e){
			logger.error("额度模式变更异常"+e.getMessage());
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("额度模式变更异常"+e.getMessage());
			return;
		}
		this.sendJSON(json);
		
		//变更完成之后对额度的处理
		try {			
			PedProtocolDto pro  = pedProtocolService.queryProtocolDto( null, null,  dto.getPoolAgreement(),null, null, null);
			financialService.txRefreshFinancial(pro,null);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}
	/**
	 * 保证金支取
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
	 * loadContractJSON 票据池担保合同查询
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
	 * loadContractJSON 票据池担保合同查询导出
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
		    String fileName = URLEncoder.encode("票据池担保合同查询", "UTF-8");
		    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
			Map<String, String> mapinfo = new LinkedHashMap();
			
			mapinfo.put("poolAgreement", "票据池编号");
			mapinfo.put("contract", "担保合同号");
			mapinfo.put("custname", "担保人名称");
			mapinfo.put("creditamount", "担保金额");
			mapinfo.put("poolModeName", "额度模式");
			mapinfo.put("contractEffectiveDt", "合同起始日");
			mapinfo.put("contractDueDt", "合同到期日");
			
			excelWriter = EasyExcel.write(response.getOutputStream()).head(ExcelUtil.head(mapinfo)).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("票据池担保合同查询").head(ExcelUtil.head(mapinfo)).build();
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
			logger.error("在线协议信息导出失败",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
	
	
	
}
