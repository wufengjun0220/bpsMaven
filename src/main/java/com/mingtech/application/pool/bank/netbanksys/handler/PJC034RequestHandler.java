
package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.handler.handlermapping.PJC034Mapping;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.domain.CreditQueryBean;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.application.pool.online.acception.domain.PedOnlineAcptProtocol;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.Role;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.sysmanage.service.DepartmentService;
import com.mingtech.application.sysmanage.service.UserService;

/**
 * 
 * @Title: 集团签约信息维护（银行端）
 * @Description: 集团签约接口
 * @author liu xiaodong
 * @date 2019-06-15
 */
public class PJC034RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC034RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private UserService userService;
	@Autowired
	private PoolBailEduService poolBailEduService;
	@Autowired
    private PedAssetPoolService pedAssetPoolService;
    @Autowired
    private FinancialService financialService;
    @Autowired
    private PoolCoreService poolCoreService;
    @Autowired
    private PoolCreditProductService poolCreditProductService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private DepartmentService departmentService;
	
	public String FLAG_03 = "03";//主户修改模式
	public String FLAG_07 = "07";//网银银行端修改模式

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		
		PedProtocolDto protocol = PJC034Mapping.QueryProtocolMap(request);
		
		/*User user = userService.getUserByLoginName(protocol.getAccountManagerId());//客户经理ID
		if(null != user){    				
			Department dept =  (Department)userService.load(user.getDeptId(),Department.class);
			String deptNo = dept.getInnerBankCode();
			String deptName = dept.getName();
			protocol.setOfficeNet(deptNo);
			protocol.setOfficeNetName(deptName);
			
		}*/
		
		ReturnMessageNew response = new ReturnMessageNew();
		String ebankFlag = protocol.getEbankFlag();
		Ret ret = new Ret();
		
		try {
			
			if(ebankFlag.equals("01")){//主户：签约
				
				logger.debug("PJC034【签约任务】"+protocol.getpSignType()+"-开始......");
				ret = this.signContract(protocol, request);
		
			}else if(ebankFlag.equals("02")){//主户 or分户 ：解约
				
				logger.debug("PJC034【解约任务】开始......");
				ret = this.endContract(protocol);
				
			}else if(ebankFlag.equals("03")){//主户：签约修改
				
				logger.debug("PJC034【主户：签约修改】开始......");
				ret = this.EditContract03(protocol, request);
				
			}else if(ebankFlag.equals("04")){//主户：增加成员
				
				logger.debug("PJC034【主户：增加成员】开始......");
				ret = this.EditContract04(protocol, request);
				
			}else if(ebankFlag.equals("05")){//主户：减少成员
				
				logger.debug("PJC034【主户：减少成员】开始......");
				ret = this.EditContract05(protocol, request);
				
			}else if(ebankFlag.equals("06")){//分户：信息修改
				
				logger.debug("PJC034【分户：信息修改】开始......");
				ret = this.EditContract06(protocol, request);
				
			}else if(ebankFlag.equals("07")){//网银银行端：全量信息修改 
				
				logger.debug("PJC034【网银银行端全量修改】开始......");
				ret = this.EditContract07(protocol, request);
				
			}
		} catch (Exception e) {
			logger.error("PJC034票据池签约处理异常", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池签约处理异常[" + e.getMessage() + "]");
		}
		if(ebankFlag.equals("01")){
			if(Constants.TX_SUCCESS_CODE.equals(ret.getRET_CODE())){
				response.getBody().put("BPS_NO", ret.getRET_MSG()); // 返回协议编号
				ret.setRET_MSG("签约成功！");				
			}
		}else{			
			response.getBody().put("BPS_NO", protocol.getPoolAgreement()); // 返回协议编号
		}
		if(ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			/*
			 * 协议信息及集团协议子户信息备份
			 */
			if(StringUtil.isNotBlank(protocol.getPoolAgreement())){				
				pedProtocolService.txProtocolAndListHist(protocol.getPoolAgreement());
			}
			
		}
		response.setRet(ret);
		return response;
	}
	
	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}
	
	/**
	 * 1.签约：【签约标识】为【01签约】时候，可同时送过主协议信息及多个子户的信息，子户信息包含自动入池及电票签约账号信息，只有主户可用
	 * @author Ju Nana
	 * @param protocol
	 * @return
	 * @throws Exception 
	 * @date 2019-7-29下午2:38:33
	 */
	private Ret signContract(PedProtocolDto protocol,ReturnMessageNew request) throws Exception {
		
		Ret ret = new Ret();
		String poolAgreement = null;
		

		ProtocolQueryBean queryBean = new ProtocolQueryBean();
		queryBean.setCustnumber(protocol.getCustnumber());
		queryBean.setIsGroup(PoolComm.YES);
		queryBean.setvStatus(PoolComm.VS_01);
		List<PedProtocolDto> dtoList1 = pedProtocolService.queryProtocolDtoListByQueryBean(queryBean);
		PedProtocolDto dto1 = new PedProtocolDto();

		if (dtoList1 != null && dtoList1.size() > 0) {// 已经签约基础协议，再签约融资时候
			logger.debug("【已签约基础协议，再签约融资业务】....开始......");
			dto1 = dtoList1.get(0);
			poolAgreement = dto1.getPoolAccountName();
			if (PoolComm.OPEN_01.equals(dto1.getOpenFlag())&& PoolComm.QYLX_02.equals(protocol.getpSignType())) {
				
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户已融资签约，不允许重复签约！");
				return ret;
				
			} else if (PoolComm.QYLX_01.equals(protocol.getpSignType())) {
				
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户已基础签约，不允许重复签约！");
				return ret;
				
			} else {//在已基础签约的基础进行融资签约
				
				ret = this.creditSignContract(protocol, request);
				if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
					return ret;
				}
				poolAgreement = ret.getRET_MSG();
			}
			
		} else {
			
			if (protocol.getpSignType().equals(PoolComm.QYLX_01)) {// 基础签约
				
				logger.debug("【集团基础业务签约】....开始......");
				
				List list = request.getDetails();
				Map map = null;
				poolAgreement = poolEBankService.createProtocolStorePJC034(protocol);
				for (int i = 0; i < list.size(); i++) {
					map = (Map) list.get(i);
					PedProtocolList pedList = PJC034Mapping.QueryDetailMap(map);
					String poolName = "集团票据池(" + protocol.getCustname() + ")";
					
					pedList.setStatus(PoolComm.PRO_LISE_STA_01);
					pedList.setEditTime(new Date());
					pedList.setBpsNo(poolAgreement);
					pedList.setBpsName(poolName);
					pedList.setCustIdentity(PoolComm.KHLX_04);
					poolEBankService.txStore(pedList);
				}
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG(poolAgreement);
				
			} else {//全签约
				
				logger.debug("【集团全签约】....开始......");
				
				ret = this.creditSignContract(protocol, request);
				if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
					return ret;
				}
				poolAgreement = ret.getRET_MSG();
			}

		}
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG(poolAgreement);
		return ret;
	}
	
	/**
	 * 融资信息签约，包含基础签约基础上的融资签约与融资全签约
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午7:55:29
	 */
	private Ret creditSignContract(PedProtocolDto protocol,ReturnMessageNew request) throws Exception{
		
		logger.debug("【融资业务签约】....开始......");
		
		
		Ret ret = new Ret();
		String poolAgreement =null;
		
		/*
		 * 客户经理校验：若网银传入客户经理在票据池系统不存在，则报错，返回错误信息
		 */
		ret = this.checkAccountManager(protocol, request);
		if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
			return ret;
		}
		
		/*
		 * 自动入池校验：若本次签约中含有自动入池的客户，则判断该客户是否有其他自动入池，若有，则返回错误信息
		 */
		List listNew = request.getDetails();
		Map mapNew = null;
		if(listNew!=null && listNew.size()>0){
			for (int j = 0; j < listNew.size(); j++) {
				mapNew = (Map) listNew.get(j);
				PedProtocolList pedList = PJC034Mapping.QueryDetailMap(mapNew);
				
				if (PoolComm.ZY_FLAG_01.equals(pedList.getZyFlag())) {
					boolean pedIsAuto = pedProtocolService.isAutoCheck(pedList.getBpsNo(), pedList.getCustNo());
					if (pedIsAuto) {
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("【"+pedList.getCustNo()+"】：该客户已签约自动入池，不允许再次签约自动入池!");
						return ret;
					}
				}
			}
		}
		if(StringUtil.isNotBlank(protocol.getMarginAccount())){//接口传送的保证金账号不为空  需校验账号是否可用
			/*
			 * 保证金账户开户校验：调用核心保证金账户查询接口，若查询成功则表示该保证金账户已在核心开户，校验通过，否则校验不通过返回错误信息
			 */
			CoreTransNotes transNotes = new CoreTransNotes();
	        transNotes.setAccNo(protocol.getMarginAccount());
	        transNotes.setCurrentFlag("1");
			ReturnMessageNew returnMessageNew = poolCoreService.PJH716040Handler(transNotes, null);
			
			if(!returnMessageNew.isTxSuccess()){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("签约保证金账号未在核心开户！");
				return ret;
			}
			
		}else{
			ReturnMessageNew response=pedProtocolService.txMarginAccount(protocol);
			if(!response.isTxSuccess()){//保证金账号在核心开户
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG(response.getRet().getRET_MSG());
				return ret;
			}
		}

		
		/*
		 * 保证金账户是否已签约其他票据池校验：若已签约其他有效票据池，则报错返回报错信息
		 */
		List<PedProtocolDto> listMargin = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null, null,protocol.getMarginAccount());
		if (null != listMargin && listMargin.size() >= 1) {
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("该保证金账号有对应的【其他】有效的签约协议，不允许签约！");
			return ret;
		}
		
		/*
		 * 将校验通过的签约信息保存到协议表与集团客户表
		 * 保存到协议表的同时需要创建assetPool、assetType、bailDetail三条数据
		 */
		poolAgreement = poolEBankService.createProtocolStorePJC034(protocol);
		
		
		/*
		 * 删除原数据
		 */
		if(StringUtil.isNotBlank(poolAgreement)){			
			ProListQueryBean  queryBeanList = new ProListQueryBean();
			queryBeanList.setBpsNo(poolAgreement);
			List<PedProtocolList> memList = pedProtocolService.queryProListByQueryBean(queryBeanList);
			if(memList!=null && memList.size()>0){
				pedProtocolService.txDeleteAll(memList);			
			}
		}

		
		for (int i = 0; i < listNew.size(); i++) {
			Map map = (Map) listNew.get(i);
			PedProtocolList pedList = PJC034Mapping.QueryDetailMap(map);
			String poolName = "集团票据池(" + protocol.getCustname() + ")";
			
			/*
			 * 保存最新的数据
			 */
			//融资签约改变集团成员客户的状态
			pedList.setStatus(PoolComm.PRO_LISE_STA_01);
			pedList.setEditTime(new Date());
			pedList.setBpsNo(poolAgreement);
			pedList.setBpsName(poolName);
			pedList.setCustIdentity(PoolComm.KHLX_04);
			pedList.setMaxFinancLimit(BigDecimal.ZERO);
			pedList.setFinancLimit(BigDecimal.ZERO);
			poolEBankService.txStore(pedList);
			
		}
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG(poolAgreement);
		return ret;
	
	}
	
	/**
	 * 2.解约：【签约标识】为【02解约】时候，主户发过来表示整个票据池解约，分户发过来表示将自身从集团票据池中解约出去，主户分户均可用
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午2:47:57
	 */
	private Ret endContract(PedProtocolDto protocol) throws Exception{
		
		Ret ret = new Ret();
		String poolAgreement = protocol.getPoolAgreement();	//票据池编号
		String custNo = protocol.getCustnumber(); //传过来的核心客户号
		
		
		//判断是主户解约还是分户
		ProtocolQueryBean  queryBean= new ProtocolQueryBean();
		queryBean.setPoolAgreement(poolAgreement);
		queryBean.setvStatus(PoolComm.VS_01);
		PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
		
		if(dto==null){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池系统未查询到有效的解约协议！");
			return ret;
		}else{
			if(custNo.equals(dto.getCustnumber())){//主户解约
				ret = this.endContractRole01(custNo, dto);
			}else{//分户解约
				ret = this.endContractRole02(custNo, dto);
			}
		}
		
		return ret;
	}
	
	/**
	 * 签约修改
	 * 3.签约修改：
	 * （1）【签约标识】为【03签约修改】时候，能修改协议信息，修改主户自身的是否自动入池、签约电票账户等信息。只能主户自己用。
	 * （2）【签约标识】为【03签约修改】且【解约标识】送【JYLX_01:融资功能解约】时，将整个票据池融资解约。
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午7:38:29
	 */
	private Ret EditContract03(PedProtocolDto protocol,ReturnMessageNew request) throws Exception{
		
		Ret ret = new Ret();
		
		//查询协议信息
		ProtocolQueryBean dtoBean = new ProtocolQueryBean();
		dtoBean.setPoolAgreement(protocol.getPoolAgreement());
		PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(dtoBean);
		
		/*
		 * 判断有无传入修改协议信息，若无则返回报错信息
		 */
		if(dto==null || PoolComm.VS_02.equals(dto.getvStatus())){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("无该票据池签约信息！");
            return ret;
		}
		
		
		if(PoolComm.OPEN_01.equals(dto.getOpenFlag())){//已签约融资协议的修改
			
			ret = this.creditSignEdit(protocol, request, FLAG_03, dto);
			if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
				return ret;
			}
			
		}else{//未签约融资协议的修改
			
			ret = this.baseSignEdit(protocol, request, FLAG_03,dto);
			if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
				return ret;
			}
			
		}
		
		return ret;
	}
	/**
	 * 4.【签约标识】为04增加成员时候，只能增加成员信息，只需要传票据池编号主户的核心客户号，数组中可多条传增加成员的全部信息，只有主户可用
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午7:42:36
	 */
	private Ret EditContract04(PedProtocolDto protocol,ReturnMessageNew request) throws Exception{
		
		Ret ret = new Ret();
		String poolAgreement = protocol.getPoolAgreement();
		List list = request.getDetails();
		
		ProtocolQueryBean dtoBean = new ProtocolQueryBean();
		dtoBean.setPoolAgreement(poolAgreement);
		PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(dtoBean);
		
		
		
		//根据票据池编号查询【已签约】的集团成员客户号
		List<Object>  custNos = poolEBankService.queryProListCustNo(poolAgreement);
		
		String[]  custNosArr = null;
		if(custNos!=null && custNos.size()>0){
			for(int i =0;i<custNos.size();i++){
				custNosArr = new String[custNos.size()];
				custNosArr[i] = (String) custNos.get(i);
			}
		}
		
		if(null != list && list.size() >0){
			for(int i=0;i<list.size();i++){
				Map map = (Map) list.get(i);
				PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
				
				/*
				 * 是否存在已签约重复传入的情况校验
				 */
				if(custNosArr!=null){
					if(ArrayUtils.contains(custNosArr, pedListNew.getCustNo())){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("协议中已经存在【"+pedListNew.getCustNo()+"】签约成员，不允许增加!");
						return ret;
					}
					
				}

				/*
				 * 自动入池判断
				 */
				if(PoolComm.ZY_FLAG_01.equals(pedListNew.getZyFlag())){
					boolean pedIsAuto=pedProtocolService.isAutoCheck(poolAgreement,pedListNew.getCustNo());
					if(pedIsAuto){//自动入池的判断
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("已经存在签约自动入池，不允许增加!");
                        return ret;
					}
				}
				
				/*
				 * 是否存在已签约过的数据
				 */
				if(StringUtil.isNotBlank(pedListNew.getCustNo())&& StringUtil.isNotBlank(poolAgreement)){					
					ProListQueryBean  queryBeanList=new ProListQueryBean();
					queryBeanList.setCustNo(pedListNew.getCustNo());
					queryBeanList.setBpsNo(poolAgreement);
					PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(queryBeanList);
					if(mem!=null){//若存在已签约过的数据
						pedListNew.setId(mem.getId());
						if(PoolComm.KHLX_02.equals(mem.getCustIdentity())){
							pedListNew.setCustIdentity(PoolComm.KHLX_02);
						}else{
							pedListNew.setCustIdentity(PoolComm.KHLX_04);
						}
					}else{
						pedListNew.setBpsNo(dto.getPoolAgreement());
						pedListNew.setBpsName(dto.getPoolName());
						pedListNew.setCustIdentity(PoolComm.KHLX_04);
					}
					/*
					 * 新增成员落库
					 */
					pedListNew.setRole(PoolComm.JS_02);//分户
					pedListNew.setStatus(PoolComm.PRO_LISE_STA_01);//已签约
					pedListNew.setEditTime(new Date());
					pedListNew.setBpsNo(poolAgreement);
					pedListNew.setBpsName(dto.getPoolName());
					pedListNew.setMaxFinancLimit(BigDecimal.ZERO);
					pedListNew.setFinancLimit(BigDecimal.ZERO);

					pedProtocolService.merge(pedListNew);

				}else{
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据池编号或者新增客户的客户号为空!");
                    return ret;
				}
				

			}
		}else{
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("增加成员为空！");
            return ret;
		}
		ret.setRET_MSG("成员增加成功！");
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		return ret;
	}
	
	/**
	 * 5.【签约标识】为05减少成员时候，只能减少成员信息，只需要传票据池编号主户的核心客户号，数组中可多条传减少成员的客户号，只有主户可用
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午7:43:27
	 */
	private Ret EditContract05(PedProtocolDto protocol,ReturnMessageNew request) throws Exception{
		Ret ret = new Ret();
		String poolAgreement = protocol.getPoolAgreement();
		List list = request.getDetails();
		
		if(null != list && list.size() >0){
			List<String> custNos = new ArrayList<String>();
			for(int i=0;i<list.size();i++){
				Map map = (Map) list.get(i);
				String custNo = (String) map.get("CONTRACT_INF_ARRAY.CORE_CLIENT_NO");// 核心客户号					
				custNos.add(custNo);
			}
			
			/*
			 * 待减少的客户名下有无在池业务校验
			 */
			boolean canEnd = poolEBankService.memIsCanEndContractCheck(poolAgreement, custNos);
			if(!canEnd){				
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("要删除的客户名下有在池的票据业务，不允许解约！");
				return ret;
			}
			
			/*
			 * 将成员表签约状态置为解约
			 */
			List<PedProtocolList> canEndCustomers = new ArrayList<PedProtocolList>();
			for(String custNo : custNos){
				ProListQueryBean queryBean = new ProListQueryBean();
				queryBean.setBpsNo(poolAgreement);
				queryBean.setCustNo(custNo);
				
				PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(queryBean);
				String custIdentity = mem.getCustIdentity();//客户身份
				
				if(PoolComm.KHLX_01.equals(custIdentity)){//出质人变为签约成员解约
					custIdentity = PoolComm.KHLX_06;
				}else if(PoolComm.KHLX_02.equals(custIdentity)){//融资人不变
					custIdentity = PoolComm.KHLX_02;
				}else if(PoolComm.KHLX_03.equals(custIdentity)){//出质人+融资人 变为 融资人
					custIdentity = PoolComm.KHLX_02;
				}else if(PoolComm.KHLX_04.equals(custIdentity)){//签约成员变签约成员解约
					custIdentity = PoolComm.KHLX_06;
				}
				
				mem.setCustIdentity(custIdentity);
				mem.setStatus(PoolComm.PRO_LISE_STA_02);
				canEndCustomers.add(mem);
			}
			pedProtocolService.txStoreAll(canEndCustomers);
			
			
		}else{
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("减少成员列表为空！");
			return ret;
		}
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG("减少成员成功！");
		return ret;
	}
	
	/**
	 * 6.【签约标识】为06修改成员时候，只需要传票据池编号，自己的核心客户号，数组中传自己的核心客户号及修改的电票签约账号相关信息及自动入池信息，该码值主户分户均可使用，每次数组中只有一条信息
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午7:44:38
	 */
	private Ret EditContract06(PedProtocolDto protocol,ReturnMessageNew request) throws Exception{
		Ret ret = new Ret();
		String poolAgreement = protocol.getPoolAgreement();
		List list = request.getDetails();
		if(list!=null && list.size()>0){
			
			Map map = (Map) list.get(0);
			PedProtocolList newMem = PJC034Mapping.QueryDetailMap(map);
			
			String custNo = newMem.getCustNo();	
			String newAccNo =newMem.getElecDraftAccount();  //签约电票账号
			
			ProListQueryBean queryBean = new ProListQueryBean();
			queryBean.setBpsNo(poolAgreement);
			queryBean.setCustNo(custNo);
			PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(queryBean);
			
			/*
			 * 电票签约账号校验
			 */
			ret = poolEBankService.eleAccNoCheck(newAccNo, custNo, poolAgreement);
			if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
				return ret;
			}
			/*
			 * 自动入池校验
			 */
			String zyFlag = newMem.getZyFlag();
			if(PoolComm.ZY_FLAG_01.equals(zyFlag)){
				boolean pedIsAuto=pedProtocolService.isAutoCheck(poolAgreement,custNo);
				if(pedIsAuto){//该客户有其他自动入池的票据池信息
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("该客户存在其他自动入池的票据池，不允许修改为自动入池!");
					return ret;
				}
			}
			
			/*
			 * 落库处理
			 */
			mem.setEditTime(new Date());
			mem.setZyFlag(newMem.getZyFlag());     
			mem.setElecDraftAccount(newAccNo);//电票签约账号        
			mem.setElecDraftAccountName(newMem.getElecDraftAccountName());//电票签约账号名称   
			mem.setCustName(newMem.getCustName());//客户名     
			mem.setOrgCoge(newMem.getOrgCoge());//组织机构代码
			pedProtocolService.txStore(mem);
			
		}else{
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("传入待成员为空！");
			return ret;
		}
		
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG("修改成功！");
		return ret;
	}
	
	/**
	 * 7.签约全修改
	 * （1）【签约标识】为【07签约修改】时候，能修改协议信息，加减成员，修改成员信息。（只能主户或者银行操作员）
	 * （2）【签约标识】为【07签约修改】且【解约标识】送【JYLX_01:融资功能解约】时，将整个票据池融资解约。
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午7:45:28
	 */
	private Ret EditContract07(PedProtocolDto protocol,ReturnMessageNew request) throws Exception{
		
		Ret ret = new Ret();
		String poolAgreement = protocol.getPoolAgreement();
		
		//查询协议信息
		ProtocolQueryBean dtoBean = new ProtocolQueryBean();
		dtoBean.setPoolAgreement(protocol.getPoolAgreement());
		PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(dtoBean);
		
		/*
		 * 判断有无传入修改协议信息，若无则返回报错信息
		 */
		if(dto==null || PoolComm.VS_02.equals(dto.getvStatus())){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("无该票据池签约信息！");
            return ret;
		}
		
		
		if(PoolComm.OPEN_01.equals(dto.getOpenFlag())){//已签约融资协议的修改
			
			ret = this.creditSignEdit(protocol, request, FLAG_07, dto);
			if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
				return ret;
			}
			
		}else{//未签约融资协议的修改
			
			ret = this.baseSignEdit(protocol, request, FLAG_07,dto);
			if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
				return ret;
			}
			
		}
		
		return ret;
	}
	

	/**
	 * 基础签约信息修改
	 * @author Ju Nana
	 * @param protocol:网银传过来的协议内容
	 * @param request
	 * @param flag  flag=03  主户修改    flag=07 银行端修改
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午9:02:54
	 */
	private Ret baseSignEdit(PedProtocolDto protocol,ReturnMessageNew request,String flag,PedProtocolDto oldPro) throws Exception{
		
		logger.debug("【基础签约信息修改】....开始......");
		
		Ret ret = new Ret();
		String poolAgreement = protocol.getPoolAgreement();
		
		/*
		 * 保存协议表
		 */
            oldPro.setOperateTime(new Date());
            oldPro.setSignDeptNo(protocol.getSignDeptNo());//签约机构号
            oldPro.setSignDeptName(protocol.getSignDeptName());//签约机构名称
            oldPro.setOfficeNet(protocol.getOfficeNet());//受理网点
            oldPro.setOfficeNetName(protocol.getOfficeNetName());//受理网点名
            oldPro.setAccountManagerId(protocol.getAccountManagerId());//客户经理id
            oldPro.setAccountManager(protocol.getAccountManager());//客户经理
            
            /*
    		 * 客户经理校验：若网银传入客户经理在票据池系统不存在，则报错，返回错误信息
    		 */
    		ret = this.checkAccountManager(protocol, request);
    		if(ret.getRET_CODE().equals("Constants.TX_FAIL_CODE")){
    			return ret;
    		}
            
            User user = userService.getUserByLoginName(protocol.getAccountManagerId());//校验客户经理
            if(null != user){            	
            	Department dept =  (Department)userService.load(user.getDeptId(),Department.class);
            	String deptNo = dept.getInnerBankCode();
            	String deptName = dept.getName();
            	oldPro.setOfficeNet(deptNo);
            	oldPro.setOfficeNetName(deptName);
            }

            oldPro.setCustOrgcode(protocol.getCustOrgcode());//组织机构代码
            pedProtocolService.txStore(oldPro);
		/*
		 * 处理集团子户表
		 * （1）若为集团主户修改，则只允许修改自身的子户信息
		 * （2）若为网银银行端修改，则允许修改全部子户信息
		 */
		List list = request.getDetails();
		if(list!=null && list.size()>0){
			
			if("03".equals(flag)){//集团主户修改:直接查询出原有数据，覆盖
				
				Map map = (Map) list.get(0);
				PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
				
				
				ProListQueryBean  queryBeanList = new ProListQueryBean();
				queryBeanList.setCustNo(pedListNew.getCustNo());
				queryBeanList.setBpsNo(poolAgreement);
				PedProtocolList pedAuto=pedProtocolService.queryProtocolListByQueryBean(queryBeanList);
				
				pedAuto.setCustName(pedListNew.getCustName());
				pedAuto.setOrgCoge(pedListNew.getOrgCoge());
				pedAuto.setSocialCode(pedListNew.getSocialCode());
				pedAuto.setElecDraftAccount(pedListNew.getElecDraftAccount());
				pedAuto.setElecDraftAccountName(pedListNew.getElecDraftAccountName());
				pedAuto.setEditTime(new Date());
				pedAuto.setZyFlag(pedListNew.getZyFlag());
				
				pedProtocolService.txStore(pedAuto);
				
			}else if("07".equals(flag)){//网银银行端修改:
				/*
				 * 直接删除全部该票据池下子户，重新保存最新的子户信息
				 */
				if(StringUtil.isNotBlank(poolAgreement)){					
					ProListQueryBean  queryBeanList = new ProListQueryBean();
					queryBeanList.setBpsNo(poolAgreement);
					List<PedProtocolList> memList = pedProtocolService.queryProListByQueryBean(queryBeanList);
					pedProtocolService.txDeleteAll(memList);
				}
				
				List<PedProtocolList> newMems = new ArrayList<PedProtocolList>(); 
				for(int i=0;i<list.size();i++){
					Map map = (Map) list.get(i);
					PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
					pedListNew.setBpsNo(oldPro.getPoolAgreement());
					pedListNew.setBpsName(oldPro.getPoolName());
					
					pedListNew.setEditTime(new Date());
					pedListNew.setStatus(PoolComm.PRO_LISE_STA_01);
					pedListNew.setBpsNo(poolAgreement);
					pedListNew.setBpsName(oldPro.getPoolName());
					pedListNew.setCustIdentity(PoolComm.KHLX_04);
					newMems.add(pedListNew);
				}
				pedProtocolService.txStoreAll(newMems);
			}
			
		}
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		return ret;
	
	}
	/**
	 * 已签约融资协议的修改
	 * @author Ju Nana
	 * @param protocol
	 * @param request
	 * @param flag
	 * @return
	 * @throws Exception
	 * @date 2019-7-29下午9:29:30
	 */
	private Ret creditSignEdit(PedProtocolDto protocol,ReturnMessageNew request,String flag,PedProtocolDto dto) throws Exception{
		
		logger.debug("【已签约融资协议的修改】....开始......");
		
		/*
		 * 校验：
		 * 		1.融资业务解约校验
		 * 		2.协议相关校验：（1）客户经理	（2）保证金账户是否已签约其他票据池  （3）保证金账户在核心账户中的余额是否足够   （4）年费模式变更是否年费已到期
		 * 		3.子户相关校验：（1）自动入池校验	（2）电票签约账号校验
		 * 
		 * 如上校验只要一个不能通过，则给网银返回错误信息
		 * 
		 */
		
		Ret ret = new Ret();
		String poolAgreement = protocol.getPoolAgreement();
		List proList = request.getDetails();
		
		List<String> inOldNotinNewCustomer = null; //用于记录07的时候在原协议中有在新协议中没有的客户
		
		/*
		 * 2.（1）客户经理校验：若网银传入客户经理在票据池系统不存在，则报错，返回错误信息
		 */

		ret = this.checkAccountManager(protocol, request);
		if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
			return ret;
		}

		
		/*
		 * 1.融资业务解约校验
		 */
		if (null != protocol.getpBreakType()&& protocol.getpBreakType().equals(PoolComm.JYLX_01)) {// 融资功能解约

			//融资解约校验方法
			ret = poolEBankService.endCreditCheck(dto);
			
			if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){//校验失败直接返回
				return ret;
			}else{//校验通过：直接融资解约
				
				/*
				 * 处理融资解约后的成员信息
				 */
				ProListQueryBean  queryBeanList = new ProListQueryBean();
				queryBeanList.setBpsNo(poolAgreement);
				List<PedProtocolList> memList = pedProtocolService.queryProListByQueryBean(queryBeanList);
				List<PedProtocolList> memListNew = new ArrayList<PedProtocolList>();
				List list = request.getDetails();
				if(memList!=null && memList.size()>0){
					for(PedProtocolList mem : memList){
						String status =  mem.getStatus();
						if(PoolComm.PRO_LISE_STA_00.equals(status)){//未签约的融资人
							mem.setCustIdentity(PoolComm.KHLX_05);
							mem.setFinancingStatus(PoolComm.SXBZ_00);//融资人失效
						}else if(PoolComm.PRO_LISE_STA_01.equals(status)){//
							mem.setCustIdentity(PoolComm.KHLX_06);
							if(mem.getFinancingStatus()!=null && PoolComm.SXBZ_01.equals(mem.getFinancingStatus())){								
								mem.setFinancingStatus(PoolComm.SXBZ_00);//融资人失效
							}	
						}
						for(int i=0;i<list.size();i++){
							Map map = (Map) list.get(i);
							PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
							if(null!=mem.getCustNo()){
								if(mem.getCustNo().equalsIgnoreCase(pedListNew.getCustNo())){
									mem.setZyFlag(pedListNew.getZyFlag());
								}

							}
						}
						memListNew.add(mem);
					}
				}
				if(memListNew!=null && memListNew.size()>0){//保存修改后的子户信息
					pedProtocolService.txStoreAll(memListNew);
				}
				
				
				dto.setOpenFlag(PoolComm.OPEN_02);//已解约
				dto.setApproveFlag(PoolComm.APPROVE_06);//解约审核通过
				
				this.editProtocolStore(protocol, dto, "01");
				
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("协议修改成功！");
				return ret;
			}

		} else {//不包含融资解约功能

			List<PedProtocolDto> listMargin = pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, null, null,null, protocol.getMarginAccount());

			/*
			 * （1）保证金账户是否已签约其他票据池校验：若已签约其他有效票据池，则报错返回报错信息
			 */
			
			if (listMargin == null || listMargin.size() < 1) {// 校验通过
				
			} else if (listMargin.size() == 1) {

				PedProtocolDto forMargin = listMargin.get(0);
				if (!forMargin.getPoolAgreement().equals(protocol.getPoolAgreement())) {
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("该保证金账号有对应的【其他】有效的签约协议，不允许签约！");
					return ret;
				}
				
			} else {// 正常情况下不存在该种情况
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该保证金账号有对应的【其他】有效的签约协议，不允许签约！");
				return ret;
			}

			/*
			 * （2）保证金账户在核心账户中的余额是否足够 校验
			 */
            String marginAccount= dto.getMarginAccount();
			if(!dto.getMarginAccount().equals(protocol.getMarginAccount())){//保证金账号改变
				
				/**
            	 * 新加逻辑  保证金账号变更时:存量该票据池担保的业务结清且在线协议失效才允许跨分行（在线协议的入账机构与新保证金账号的归属机构）；
            	 * 1、查询是否有生效的在线银承或流贷协议
            	 * 2、根据协议判断是否有未结清的存量业务
            	 * 3、
            	 */
				
				boolean bool = departmentService.checkBranch(protocol.getMarginAccount(),protocol.getAccountManagerId());
				if(!bool){
					
					boolean Flag;
            		
                	CreditQueryBean bean = new CreditQueryBean();
                	bean.setBpsNo(dto.getPoolAgreement());
                	List status = new ArrayList();
                	status.add(PoolComm.JJ_04);//结清
                	status.add(PoolComm.JJ_05);//未用退回
                	bean.setLoanStatusNotInLsit(status);
                	//查询该票据池的流贷业务是否有未结清数据
                	List<PedCreditDetail> cdList = poolCreditProductService.queryCreditDetailList(bean);
                	if(cdList != null && cdList.size() > 0){
                		//存量业务未结清
                		Flag = false;
                	}else{
                		//存量业务已结清
                		Flag = true;
                	}
            		if(!Flag){
            			ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("该票据池存在未结清的融资业务，不允许做跨分行保证金账号变更！");
                        return ret;
            		}
            		
            		//不同分行的保证金账号变更
					OnlineQueryBean queryBean = new OnlineQueryBean();
	            	queryBean.setBpsNo(dto.getPoolAgreement());
	            	queryBean.setProtocolStatus(PublicStaticDefineTab.STATUS_1);//生效
	            	
	            	List<PedOnlineAcptProtocol> acptList = pedOnlineAcptService.queryOnlineAcptProtocolList(queryBean);
	            	List<PedOnlineCrdtProtocol> crdtList = pedOnlineCrdtService.queryOnlineProtocolList(queryBean);
                	if((acptList != null && acptList.size() > 0) || (crdtList != null && crdtList.size() > 0) ){
                		ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("该票据池的在线协议未失效，不允许做跨分行保证金账号变更！");
                        return ret;
	            	}
				}
				
				//A：重组融资业务

				 /*
                 * assetPool锁
                 */
        		AssetPool ap = pedAssetPoolService.queryPedAssetPoolByProtocol(dto);
        		String apId = ap.getApId();
        		boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
        		if(!isLockedSucss){//加锁失败
        			ret.setRET_CODE(Constants.TX_FAIL_CODE);
        			ret.setRET_MSG("该票据池有其他额度相关任务正在处理中，请稍后再试！");
                    return ret;
        		}
        		
                /*
        		 * 同步核心保证金，并重新计算额度
        		 */
        		financialService.txBailChangeAndCrdtCalculation(dto);
        		
        		/*
        		 * 解锁AssetPool表，并重新计算该表数据
        		 */
        		pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);
				
				//B:获取保证金已用额度
				AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(dto, PoolComm.ED_BZJ_HQ);
				BigDecimal usedAmt = at.getCrdtUsed();//已用保证金额度
				
				//C：核心获取新保证金的余额
				BigDecimal freeAmt = poolBailEduService.queryCoreBailFree(protocol.getMarginAccount());
				
				//D:如果新保证金账户中的可用额度小于已占用的额度，则不允许更换。核心查询无结果不允许更换。
				if(freeAmt==null){
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("该保证金账号无核心开户信息，不允许更换！");
					return ret;
				}
				if(freeAmt.compareTo(usedAmt)<0){
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("新保证金账户余额小于票据池保证金已用额度，不允许更换！");
					return ret;
				}
			}
			
			/*
			 * （3）修改如果原协议表是年费，需要判断原协议年费到期日是否小于当前到期日，小于则报错，大于则通过.(只有年费模式改为逐笔时候存在该问题)
			 */
			if(PoolComm.SFMS_02.equals(protocol.getFeeType()) && PoolComm.SFMS_01.equals(dto.getFeeType())){
				if(dto.getFeeDueDt().getTime() > (new Date()).getTime()){
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("年费未到期，不允许修改收费模式!");
                    return ret;
				}
			}
			
			
			
			/*
			 * （4）自动入池校验	（2）电票签约账号校验
			 */
			if(proList!=null && proList.size()>0){
				ret = this.autoInPoolAndEleAccCheck(protocol, proList, flag, dto);
				if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
					return ret;
				}
			}
			
			if(FLAG_07.equals(flag)){
				/*
				 * (4)-2 :原协议子户中包含，但是新协议中不包含的子户的电票签约账号校验
				 */
				ret = this.oldMemEleAccEndCheck(proList, poolAgreement);
				inOldNotinNewCustomer = ret.getSomeList();
				if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){//校验失败直接返回
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG(ret.getRET_MSG());
					return ret;
				}
			}
			/*
			 * （5）更新保证金表
			 */
			if(null!=marginAccount && !marginAccount.equals(protocol.getMarginAccount())){//保证金账号改变
				AssetType assetTypeDto = pedAssetTypeService.queryPedAssetTypeByProtocol(dto,PoolComm.ED_BZJ_HQ);
				if(assetTypeDto!=null){
					ReturnMessageNew response=pedProtocolService.txMarginAccChange(marginAccount,protocol.getMarginAccount(),protocol.getPoolAgreement()); 
					  if(!response.isTxSuccess()){//票据池保证金账户变更
                          ret.setRET_CODE(Constants.TX_FAIL_CODE);
                          ret.setRET_MSG(response.getRet().getRET_MSG());
                          return ret;
                        }else{
        					BailDetail detail = poolBailEduService.queryBailDetail(assetTypeDto.getId());
        					detail.setAssetNb(protocol.getMarginAccount());
        					poolBailEduService.txStore(detail);
        					
        					//保证金更换时资产登记表中数据同步更新  先查询登记表中是否存在,存在则更新,不存在不处理
                        	poolEBankService.txChangeBailAccNo(dto.getPoolAgreement(), protocol.getMarginAccount(), marginAccount);
        					
                        }
				}else{
					logger.error("签约信息维护，获取资产池为空！");	
				}
				
			}
			
			/*
			 * （6）全部校验完毕更新主协议内容
			 */
			this.editProtocolStore(protocol, dto, "02");
			
			/*
			 * (7)更新子户信息
			 */
			if(proList!=null && proList.size()>0){
				
				this.editProtocolListStore(proList, poolAgreement, flag,inOldNotinNewCustomer);
				
			}
			
			

			
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG("协议修改成功！");
			return ret;
		}
	
	}
	
	
	/**
	 * 自动入池及电票签约账号校验
	 * @author Ju Nana
	 * @param protocol
	 * @param proList
	 * @param flag
	 * @param dto
	 * @return
	 * @throws Exception
	 * @date 2019-7-30下午3:32:32
	 */
	private Ret autoInPoolAndEleAccCheck(PedProtocolDto protocol,List proList,String flag,PedProtocolDto dto) throws Exception{
		
		logger.debug("【自动入池及电票签约账号校验】....开始......");
		
		Ret ret = new Ret();
		
		if(FLAG_03.equals(flag)){//只校验主户
			
			
			Map map = (Map) proList.get(0);
			PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
			
			/*
			 * (1)自动入池的校验
			 */
			if(PoolComm.ZY_FLAG_01.equals(pedListNew.getZyFlag())){
				boolean pedIsAuto=pedProtocolService.isAutoCheck(protocol.getPoolAgreement(),pedListNew.getCustNo());
				if(pedIsAuto){//该客户有其他自动入池的票据池信息
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("该客户存在其他自动入池的票据池，不允许修改为自动入池!");
                    return ret;
				}
			}
			
			
			/*
			 * （2）电票签约账户校验
			 */
			ret = poolEBankService.eleAccNoCheck(pedListNew.getElecDraftAccount(), pedListNew.getCustNo(), protocol.getPoolAgreement());
			if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
				return ret;
			}
			
		}else if(FLAG_07.equals(flag)){//校验全部成员
			
			for(int i=0;i<proList.size();i++){
				Map map = (Map) proList.get(i);
				PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
				
				/*
				 * 自动入池校验
				 */
				if(PoolComm.ZY_FLAG_01.equals(pedListNew.getZyFlag())){
					boolean pedIsAuto=pedProtocolService.isAutoCheck(protocol.getPoolAgreement(),pedListNew.getCustNo());
					if(pedIsAuto){//该客户有其他自动入池的票据池信息
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("【"+pedListNew.getCustNo()+"】该客户存在其他自动入池的票据池，不允许自动入池!");
	                    return ret;
					}
				}
				
				/*
				 * （2）电票签约账户校验
				 */
				
				ret = poolEBankService.eleAccNoCheck(pedListNew.getElecDraftAccount(), pedListNew.getCustNo(), protocol.getPoolAgreement());
				if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){
					return ret;
				}
				
			}
			
		}
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		return ret;
	}
	
	/**
	 * 签约修改协议字段整合落库
	 * @author Ju Nana
	 * @param newPro
	 * @param oldPro
	 * @param flag  01:基础信息修改    02：融资信息修改
	 * @date 2019-7-31上午12:16:31
	 */
	private void editProtocolStore(PedProtocolDto newPro,PedProtocolDto oldPro,String flag) throws Exception{
		
		oldPro.setOperateTime(new Date());

            oldPro.setSignDeptNo(newPro.getSignDeptNo());//签约机构号
            oldPro.setSignDeptName(newPro.getSignDeptName());//签约机构名称
            oldPro.setOfficeNet(newPro.getOfficeNet());//受理网点
            oldPro.setOfficeNetName(newPro.getOfficeNetName());//受理网点名
            oldPro.setAccountManagerId(newPro.getAccountManagerId());//客户经理id
            oldPro.setAccountManager(newPro.getAccountManager());//客户经理
            
            User user = userService.getUserByLoginName(newPro.getAccountManagerId());//校验客户经理
            if(null != user){            	
            	Department dept =  (Department)userService.load(user.getDeptId(),Department.class);
            	String deptNo = dept.getInnerBankCode();
            	String deptName = dept.getName();
            	oldPro.setOfficeNet(deptNo);
            	oldPro.setOfficeNetName(deptName);
            }

		oldPro.setCustOrgcode(newPro.getCustOrgcode());//组织机构代码
		if("02".equals(flag)){//融资签约
			
			oldPro.setMarginAccount(newPro.getMarginAccount());//保证金账号
			oldPro.setMarginAccountName(newPro.getMarginAccountName());//保证金账号名
			oldPro.setPoolAccount(newPro.getPoolAccount());//结算账户
			oldPro.setPoolAccountName(newPro.getPoolAccountName());//结算账号名
			oldPro.setFeeType(newPro.getFeeType());//收费模式
			
		}
		
		pedProtocolService.txStore(oldPro);
	}
	
	/**
	 * 签约修改子户信息落库处理
	 * @author Ju Nana
	 * @param list：网银传入的新的子户list
	 * @param bpsNo
	 * @param flag
	 * @throws Exception
	 * @date 2019-7-31上午1:06:33
	 */
	private void editProtocolListStore(List proList,String poolAgreement,String flag,List<String> inOldNotinNewCustomer) throws Exception{

		List<PedProtocolList> mems = new ArrayList<PedProtocolList>();
		String bpsName = null;
		
		if(FLAG_03.equals(flag)){//只有主户一条协议
			
			Map map = (Map) proList.get(0);
			PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
			
			ProListQueryBean bean1 = new ProListQueryBean();
			bean1.setBpsNo(poolAgreement);
			bean1.setCustNo(pedListNew.getCustNo());
			PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(bean1);
			
			
			mem.setZyFlag(pedListNew.getZyFlag());
			mem.setElecDraftAccount(pedListNew.getElecDraftAccount());
			mem.setElecDraftAccountName(pedListNew.getElecDraftAccountName());
			
			mems.add(mem);
			
		}else if(FLAG_07.equals(flag)){
			
			/*
			 * 查询出所有的原子户
			 * 1.原子户中有，新协议中没有的
			 * 		（1）身份为【融资人的】不变
			 * 		（2）身份为【出质人+融资人】的改为【已解约】身份改为【融资人】
			 * 		（3）身份为【出质人】的改为【已解约】
			 * 		（4）身份为【签约成员】的改为【已解约】
			 * 3.原子户中有，新协议中也有的，修改
			 * * 	（1）身份为【融资人的】 ——不存在
			 * 		（2）身份为【出质人+融资人】的改基本信息
			 * 		（3）身份为【出质人】的改基本信息
			 * 		（4）身份为【签约成员】的改基本信息
			 * 2.原子户中没有的全部新增
			 */
			
			//将该票据池下所有【已签约】的子户置为已解约
			ProListQueryBean bean = new ProListQueryBean();
			bean.setBpsNo(poolAgreement);
			bean.setStatus(PoolComm.PRO_LISE_STA_01);
			List<PedProtocolList> memList = pedProtocolService.queryProListByQueryBean(bean);
			
			
			if(memList!=null){
				for(PedProtocolList member : memList){
					member.setStatus(PoolComm.PRO_LISE_STA_02);
					if(member.getBpsName()!=null){
						bpsName = member.getBpsName();
					}
				}
			}
			pedProtocolService.txStoreAll(memList);
			
			if(proList!=null && proList.size()>0){
				for(int i=0;i<proList.size();i++){
					
					Map map = (Map) proList.get(i);
					PedProtocolList pedListNew = PJC034Mapping.QueryDetailMap(map);
					
					ProListQueryBean bean2 = new ProListQueryBean();
					bean2.setBpsNo(poolAgreement);
					bean2.setCustNo(pedListNew.getCustNo());
					PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(bean2);
					
					if(mem!=null){
						
						mem.setZyFlag(pedListNew.getZyFlag());
						mem.setElecDraftAccount(pedListNew.getElecDraftAccount());
						mem.setElecDraftAccountName(pedListNew.getElecDraftAccountName());	
						mem.setEditTime(new Date());
						mem.setStatus(PoolComm.PRO_LISE_STA_01);//已签约
						
					}else{//增加子户的情况
						mem = pedListNew;
						mem.setCustIdentity(PoolComm.KHLX_04);//签约成员
						mem.setRole(PoolComm.JS_02);//分户
						mem.setEditTime(new Date());
						mem.setStatus(PoolComm.PRO_LISE_STA_01);//已签约
						mem.setBpsNo(poolAgreement);
						mem.setBpsName(bpsName);
						
					}
					mems.add(mem);
				}
			}
			
			/*
			 * 原协议中有，新协议中没有的客户
			 * 状态置为：已解约	客户类型字段：原KHLX_01变为KHLX_06   原KHLX_02不变  原KHLX_03变KHLX_02 原KHLX_04变KHLX_06）
			 */
			if(inOldNotinNewCustomer!=null && inOldNotinNewCustomer.size()>0){
				
				for(String custNo : inOldNotinNewCustomer){
					ProListQueryBean bean3 = new ProListQueryBean();
					bean3.setBpsNo(poolAgreement);
					bean3.setCustNo(custNo);
					PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(bean3);
					mem.setStatus(PoolComm.PRO_LISE_STA_02);//已解约
					mem.setEditTime(new Date());
					String custIdentity = mem.getCustIdentity();
					if(PoolComm.KHLX_01.equals(custIdentity)){
						custIdentity = PoolComm.KHLX_06;
					}else if(PoolComm.KHLX_02.equals(custIdentity)){
						custIdentity = PoolComm.KHLX_02;
					}else if(PoolComm.KHLX_03.equals(custIdentity)){
						custIdentity = PoolComm.KHLX_02;
					}else if(PoolComm.KHLX_04.equals(custIdentity)){
						custIdentity = PoolComm.KHLX_06;
					}
					mem.setCustIdentity(custIdentity);
					mems.add(mem);
					
				}
				
			}
			
			
		}
		
		pedProtocolService.txStoreAll(mems);
		
	}

	
	/**
	 * 原协议子户中包含，但是新协议中不包含的子户的电票签约账号未校验
	 * @author Ju Nana
	 * @param newlist
	 * @param bpsNo
	 * @return
	 * @throws Exception
	 * @date 2019-7-31下午8:21:32
	 */
	private Ret oldMemEleAccEndCheck(List newlist,String bpsNo) throws Exception{
		Ret ret = new Ret();
		
		List<Object> oldCustNoObj = poolEBankService.queryProListCustNo(bpsNo);
		List<String> oldCustNo = new ArrayList<String>();
		if(oldCustNoObj!=null && oldCustNoObj.size()>0){			
			for(int i = 0;i<oldCustNoObj.size();i++){
				oldCustNo.add(String.valueOf(oldCustNoObj.get(i)));
			}
		}
		
		List<String> newCustNo = new ArrayList<String>();//新协议中的客户号
		String[] newCustNoArr = null;//新协议中的客户号数组
		List<String> inOldNotinNewCustomer = new LinkedList<String>(); // 在原协议中有在新协议中没有的客户号
		if(newlist!=null && newlist.size()>0){
			newCustNoArr = new String[newlist.size()];
			for(int i=0;i<newlist.size();i++){
				Map map = (Map) newlist.get(i);
				String custNo = (String) map.get("CONTRACT_INF_ARRAY.CORE_CLIENT_NO");// 核心客户号
				newCustNo.add(custNo);
				newCustNoArr[i] =  custNo;
			}
		}
		
		if (oldCustNo != null && oldCustNo.size()>0) {
			for (String old : oldCustNo) {
				if (!ArrayUtils.contains(newCustNoArr,old)) {// 如果新的客户号中不包含原客户号（即删除客户的行为）,记录下来
					inOldNotinNewCustomer.add(old);
				}
			}
		}
		
		if(inOldNotinNewCustomer!=null &&inOldNotinNewCustomer.size()>0){
			
			boolean checkResult = poolEBankService.memIsCanEndContractCheck(bpsNo, inOldNotinNewCustomer);
			
			if(!checkResult){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("要删除的客户名下有在池的票据业务，不允许解约！");
				return ret;
			}
		}
		ret.setSomeList(inOldNotinNewCustomer);
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		return ret;
	}
	
	/**
	 * 主户解约
	 * @author Ju Nana
	 * @param custNo
	 * @param dto
	 * @return
	 * @throws Exception
	 * @date 2019-7-31上午9:31:27
	 */
	private Ret endContractRole01(String custNo,PedProtocolDto dto) throws Exception{
		
		logger.debug("【主户解约业务】....开始......");
		
		Ret ret = new Ret();
		
		if(PoolComm.OPEN_01.equals(dto.getOpenFlag())){//融资协议已开通
			
			ret = poolEBankService.endCreditCheck(dto);//融资解约校验方法
			if(Constants.TX_FAIL_CODE.equals(ret.getRET_CODE())){//校验失败直接返回
				return ret;
			}
			
		}
		
		//成员表：将该票据池下所有【已签约】的子户置为已解约
		ProListQueryBean bean = new ProListQueryBean();
		bean.setBpsNo(dto.getPoolAgreement());
		bean.setStatus(PoolComm.PRO_LISE_STA_01);
		List<PedProtocolList> memList = pedProtocolService.queryProListByQueryBean(bean);
		if(memList!=null){
			for(PedProtocolList member : memList){
				if(PoolComm.PRO_LISE_STA_00.equals(member.getStatus())){
					member.setCustIdentity(PoolComm.KHLX_05);	
				}else if(PoolComm.PRO_LISE_STA_01.equals(member.getStatus())){
					member.setCustIdentity(PoolComm.KHLX_06);
				}
				member.setStatus(PoolComm.PRO_LISE_STA_02);
				
			}
		}
		//协议表：置为已解约
		dto.setvStatus(PoolComm.VS_02);
		dto.setOpenFlag(PoolComm.OPEN_02);
		dto.setOperateTime(new Date());
		pedProtocolService.txStore(dto);
		if(null!=memList){
			pedProtocolService.txStoreAll(memList);
		}
		
		
		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG("解约成功！");
		return ret;
		
		
		
	}

	/**
	 * 分户解约
	 * @author Ju Nana
	 * @param custNo
	 * @param dto
	 * @return
	 * @throws Exception
	 * @date 2019-7-31上午9:31:35
	 */
	private Ret endContractRole02(String custNo,PedProtocolDto dto) throws Exception{
		
		logger.debug("【分户解约业务】....开始......");
		
		Ret ret = new Ret();
		String poolAgreement = dto.getPoolAgreement();
		
		ProListQueryBean queryBean = new ProListQueryBean();
		queryBean.setBpsNo(poolAgreement);
		queryBean.setCustNo(custNo);
		queryBean.setStatus(PoolComm.PRO_LISE_STA_01);
		
		PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(queryBean);
		if(null==mem){
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("该票据池中无当前客户:"+custNo+"的有效签约信息!");
			return ret;
		}
		
		
		if(PoolComm.OPEN_01.equals(dto.getOpenFlag())){//融资协议已开通的解约
			/*
			 * 分户解约名下无在池票据
			 */
			List<String> custNos = new ArrayList<String>();
			custNos.add(custNo);
			boolean canEnd = poolEBankService.memIsCanEndContractCheck(poolAgreement, custNos);
			if(!canEnd){				
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该客户名下有票据池有效票据业务，不允许解约！");
				return ret;
			}else{
				mem.setCustIdentity(PoolComm.KHLX_06);
				mem.setStatus(PoolComm.PRO_LISE_STA_02);
				pedProtocolService.txStore(mem);
			}
			
			
		}else{//融资协议未开通的解约
			mem.setStatus(PoolComm.PRO_LISE_STA_02);
			mem.setCustIdentity(PoolComm.KHLX_06);
			pedProtocolService.txStore(mem);
		}

		ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
		ret.setRET_MSG("解约成功！");
		return ret;
		
	}
	
	/**
	 * 校验客户经理信息
	 * @param protocol
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Ret checkAccountManager(PedProtocolDto protocol,ReturnMessageNew request) throws Exception{
		Ret ret = new Ret();
        User user = userService.getUserByLoginName(protocol.getAccountManagerId());//校验客户经理
        if (null == user) {
            ret.setRET_CODE(Constants.TX_FAIL_CODE);
            ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
            return ret;
        }else{
        	boolean isManager = false;//是否为客户经理
			if(user.getRoleList()!=null && user.getRoleList().size()>0){
				for(Role role : (List<Role>)user.getRoleList()){
					if( StringUtil.isNotBlank(role.getCode())){						
						String roleCode  = role.getCode();
						if(PoolComm.roleCode6.equals(roleCode)){
							isManager = true;
							break;
						}
					}
				}
			}
			if(!isManager){
				 ret.setRET_CODE(Constants.TX_FAIL_CODE);
                 ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                 return ret;
			}
			if(!StringUtils.isBlank(protocol.getAccountManager())&&!protocol.getAccountManager().equals(user.getName())){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
                ret.setRET_MSG("客户经理信息不存在，请确认后再录入。");
                return ret;
			}
        }
        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
        return ret;
	}
}
