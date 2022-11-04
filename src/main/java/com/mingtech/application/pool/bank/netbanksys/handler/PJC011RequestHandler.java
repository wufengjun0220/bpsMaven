package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PlFeeScale;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.edu.domain.PedCheck;
import com.mingtech.framework.core.page.Page;

/**
 * 
 * @Title: 网银查询接口-OJC011协议管理
 * @Description: 协议管理-签约查询接口
 * @author liu xiaodong 
 * @date 2019-7-08
 */
public class PJC011RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC011RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Page page = getPage(request.getAppHead());
		try {
			String custnumber =getStringVal(request.getBody().get("CORE_CLIENT_NO"));//客户号
			String poolAgreement =getStringVal(request.getBody().get("BPS_NO"));//票据池编号
			String custOrgcode =getStringVal(request.getBody().get("ORG_CODE"));//组织机构代码
			String custname =getStringVal(request.getBody().get("CLIENT_NAME"));//客户名称


			ProtocolQueryBean queryBeanped = new ProtocolQueryBean();
			ProListQueryBean queryBean = new ProListQueryBean();

			queryBeanped.setPoolAgreement(poolAgreement);
			PedProtocolDto  pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBeanped);

			if (null!=pedProtocolDto && PoolComm.VS_01.equals(pedProtocolDto.getvStatus())) {
					//单户查询也需要增加 查询明细
					if(PoolComm.NO.equals(pedProtocolDto.getIsGroup())){
						if(!custnumber.equals(pedProtocolDto.getCustnumber())){
							ret.setRET_CODE(Constants.EBK_03);
							ret.setRET_MSG("不能查询此身份的数据，无符合条件数据！");
							setPage(response.getAppHead(), page);
							response.setRet(ret);
							return response;
						}
						if(StringUtils.isNotBlank(custOrgcode)&&!custOrgcode.equals(pedProtocolDto.getCustOrgcode())){
							ret.setRET_CODE(Constants.EBK_03);
							ret.setRET_MSG("查询的组织机构代码与签约组织机构代码不一致！");
							setPage(response.getAppHead(), page);
							response.setRet(ret);
							return response;
						}
						if(StringUtils.isNotBlank(custname)&&!custname.equals(pedProtocolDto.getCustname())){
							ret.setRET_CODE(Constants.EBK_03);
							ret.setRET_MSG("查询的客户名称与签约客户名称不一致！");
							setPage(response.getAppHead(), page);
							response.setRet(ret);
							return response;
						}
						// 报文体主数据组装
						response.setBody(dataProcessBody(pedProtocolDto));
						// 报文明细组装
						List<Map> listDetail = dataProcessOnly(pedProtocolDto);
						response.setDetails(listDetail);
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("查询成功！");
					}else {//集团
						queryBean.setBpsNo(poolAgreement);//票据池编号
						queryBean.setCustNo(custnumber);//客户号
						queryBean.setStatus(PoolComm.PRO_LISE_STA_01);
						PedProtocolList pedList = pedProtocolService.queryProtocolListByQueryBean(queryBean);//客户号查询
						if (null != pedList) {
							if (PoolComm.JS_01.equals(pedList.getRole())) {//主户
								if(StringUtils.isNotBlank(custOrgcode)&&!custOrgcode.equals(pedProtocolDto.getCustOrgcode())){
									ret.setRET_CODE(Constants.EBK_03);
									ret.setRET_MSG("查询的组织机构代码与签约组织机构代码不一致！");
									setPage(response.getAppHead(), page);
									response.setRet(ret);
									return response;
								}
								if(StringUtils.isNotBlank(custname)&&!custname.equals(pedProtocolDto.getCustname())){
									ret.setRET_CODE(Constants.EBK_03);
									ret.setRET_MSG("查询的客户名称与签约客户名称不一致！");
									setPage(response.getAppHead(), page);
									response.setRet(ret);
									return response;
								}
								// 报文体主数据组装
								response.setBody(dataProcessBody(pedProtocolDto));
								// 报文体明细数据组装
								queryBean.setCustNo(null);//客户号
								List<PedProtocolList> pedListNew = pedProtocolService.queryProListByQueryBean(queryBean, page);//票据池编号查询
								List<Map> listDetail = dataProcess(pedListNew);
								response.setDetails(listDetail);
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG("查询成功！");
							} else {//分户
								if(StringUtils.isNotBlank(custOrgcode)&&!custOrgcode.equals(pedList.getOrgCoge())){
									ret.setRET_CODE(Constants.EBK_03);
									ret.setRET_MSG("该组织机构代码与签约组织机构代码不一致！");
									setPage(response.getAppHead(), page);
									response.setRet(ret);
									return response;
								}
								if(StringUtils.isNotBlank(custname)&&!custname.equals(pedList.getCustName())){
									ret.setRET_CODE(Constants.EBK_03);
									ret.setRET_MSG("客户名称与签约客户名称不一致！");
									setPage(response.getAppHead(), page);
									response.setRet(ret);
									return response;
								}
								// 报文体主数据组装
								response.setBody(dataProcessBody(pedProtocolDto));
								// 报文体明细数据组装
								List<Map> listDetail = dataProcess(pedList);
								response.setDetails(listDetail);
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG("查询成功！");
								setPage(response.getAppHead(), page);
								response.setRet(ret);
								return response;
								}
							}else {
								ret.setRET_CODE(Constants.EBK_03);
								ret.setRET_MSG("该客户未签约！");
							}
						}
			} else {
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG(request.getBody().get("CORE_CLIENT_NO") + ":该企业未开通票据池功能");
			}
		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池签约查询异常");
		}
		setPage(response.getAppHead(), page);
		response.setRet(ret);
		return response;
	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}
	
	public PedProtocolService getPedProtocolService() {
		return pedProtocolService;
	}

	public void setPedProtocolService(PedProtocolService pedProtocolService) {
		this.pedProtocolService = pedProtocolService;
	}
	
	/**
	 * 报文主题返回信息处理
	 * @author Ju Nana
	 * @param pro
	 * @return
	 * @throws Exception
	 * @date 2019-8-30下午2:54:43
	 */
	private Map dataProcessBody(PedProtocolDto pro) throws Exception {
		Map body = new HashMap();
		PlFeeScale plFeeScale = pedProtocolService.queryFeeScale();
		if(null != plFeeScale){
			//01：年费 02：单笔
			if(PoolComm.SFMS_01.equals(pro.getFeeType())){
				body.put("PRICE_STANDARD_DESC", plFeeScale.getEveryYear().setScale(2,
						BigDecimal.ROUND_HALF_UP));// 收费标准
				body.put("SERVICE_FEE_CHAR_DATE", pro.getFeeIssueDt());// 服务费收取日
				body.put("SERVICE_FEE_EXPIRY_DATE", pro.getFeeDueDt());// 服务费到期日
			}else if(PoolComm.SFMS_02.equals(pro.getFeeType())){
				body.put("PRICE_STANDARD_DESC", plFeeScale.getEveryPiece().setScale(2,
						BigDecimal.ROUND_HALF_UP));// 收费标准
				body.put("SERVICE_FEE_CHAR_DATE", "");// 服务费收取日
				body.put("SERVICE_FEE_EXPIRY_DATE","");// 服务费到期日
			}
		}
		body.put("BUSS_CATEGORY", pro.getBusiType());// 业务类型
		if(StringUtils.isNotBlank(pro.getOpenFlag())&&pro.getOpenFlag().equals(PoolComm.OPEN_00)){//网银不要融资业务签约标识为00
			body.put("FINANCING_POOL_FLAG",PoolComm.OPEN_02);// 融资池开通状态
		}else{
			body.put("FINANCING_POOL_FLAG", pro.getOpenFlag());// 融资池开通状态
		}
		if(StringUtils.isNotBlank(pro.getOpenFlag())&&!pro.getOpenFlag().equals(PoolComm.OPEN_01)){//网银融资功能未开通不需要返回受理网点
			body.put("RCV_BRANCH_ID", "");// 受理网点
			body.put("RCVER_NAME", "");// 受理人
			body.put("RCV_BRANCH_NAME", "");// 受理网点名称
		}else if(StringUtils.isNotBlank(pro.getOpenFlag())&&pro.getOpenFlag().equals(PoolComm.OPEN_01)) {
			body.put("RCV_BRANCH_ID", pro.getOfficeNet());// 受理网点
			body.put("RCVER_NAME", pro.getOperatorName1());// 受理人
			body.put("RCV_BRANCH_NAME", pro.getOfficeNetName());// 受理网点名称
		}
		body.put("VT_AMT_POOL_FLAG", pro.getvStatus());// 虚拟池开通状态
		body.put("BPS_NO", pro.getPoolAgreement());// 票据池编号
		body.put("BPS_NAME", pro.getPoolName());// 票据池名称
		body.put("IS_GROUP", pro.getIsGroup());// 是否集团
		body.put("FEE_MODE", pro.getFeeType());// 收费模式
		body.put("AGREE_EFFECTIVE_DATE", pro.getEffstartdate());// 协议生效日期
		body.put("AGREE_EXPIRY_DATE", pro.getEffenddate());// 协议到期日期
		body.put("GUARANTEE_CONTRACT_NO", pro.getContract());// 担保合同号
		body.put("GUARANTEE_CONTRACT_AMT", pro.getCreditamount());// 担保合同金额
		body.put("CONTRACT_SIGN_DATE", pro.getContractTransDt());// 合同签约日期
		body.put("GUARANTEE_CONTRACT_START_DATE", pro.getContractEffectiveDt());// 担保合同起始日期
		body.put("GUARANTEE_CONTRACT_EXPIRY_DATE", pro.getContractDueDt());// 担保合同到期日期
		body.put("AUTO_RENEW_FLAG", pro.getXyflag());// 自动续约标志
		body.put("BUSS_TYPE", pro.getBusiType());// 业务类型
		body.put("FROZEN_STATUS", pro.getFrozenstate());// 冻结状态
		body.put("SIGN_TYPE", pro.getSigningFunction());// 签约类型
		body.put("SIGN_MODE", pro.getPoolMode());//签约方式
		body.put("APP_NAME", pro.getLicename());// 经办人名称
		body.put("APP_GLOBAL_ID", pro.getAuthperson());// 经办人证件号码
		body.put("APP_MOBIL", pro.getPhonenumber());// 经办人手机
		body.put("DEPOSIT_ACCT_NO", pro.getMarginAccount());// 保证金账户
		body.put("DEPOSIT_ACCT_NAME", pro.getMarginAccountName());// 保证金账户名称
		
		
		/*
		 * 查询担保合同已用金额与可用金额
		 */
		PedProtocolDto dto = pedAssetTypeService.queryPedAssetTypeReturnCredit(pro);
		body.put("GUARANTEE_CONTRACT_USED_AMT", dto.getCreditUsedAmount());// 担保合同已用金额
		body.put("GUARANTEE_CONTRACT_LEFT_AMT", dto.getCreditFreeAmount());// 担保合同未用金额
		
		body.put("SIGN_BRANCH_ID", pro.getSignDeptNo());// 签约机构号
		body.put("SIGN_BRANCH_NAME", pro.getSignDeptName());// 签约机构名称
		
		
		body.put("GUARANTEE_CONTRACT_NO", pro.getContract());// 担保合同号
		body.put("SETTLE_ACCT_NO", pro.getPoolAccount());// 结算账户
		body.put("SETTLE_ACCT_NAME", pro.getPoolAccountName());// 结算账户名称

		body.put("FINANCING_BRANCH_NO", pro.getCreditDeptNo());// 融资机构号
		body.put("FINANCING_BRANCH_NAME", pro.getCreditDeptName());// 融资机构名称
		body.put("GUARANTEE_CONTRACT_SIGN_DATE", pro.getContractTransDt());// 担保合同签订日期
		body.put("GUARANTEE_CONTRACT_START_DATE", pro.getContractEffectiveDt());// 担保合同生效日期
		body.put("GUARANTEE_CONTRACT_EXPIRY_DATE", pro.getContractDueDt());// 担保合同到期日期
		body.put("CUST_MANAGER_ID", pro.getAccountManagerId());//客户经理ID
		body.put("CUST_MANAGER_NAME", pro.getAccountManager());// 客户经理名称
		
		/*
		 *	若未融资签约，如下字段不展示          20200116 Ju Nana 
		 */
		if(StringUtils.isNotBlank(pro.getOpenFlag())&& !pro.getOpenFlag().equals(PoolComm.OPEN_01)){
			body.put("SETTLE_ACCT_NO", "");// 结算账户
			body.put("SETTLE_ACCT_NAME", "");// 结算账户名称
			body.put("DEPOSIT_ACCT_NO", "");// 保证金账户
			body.put("DEPOSIT_ACCT_NAME", "");// 保证金账户名称
			body.put("CUST_MANAGER_ID", "");//客户经理ID
			body.put("CUST_MANAGER_NAME", "");// 客户经理名称
			body.put("RCV_BRANCH_ID", "");// 受理网点
			body.put("RCVER_NAME", "");// 受理人
			body.put("RCV_BRANCH_NAME", "");// 受理网点名称
			
			body.put("FEE_MODE", "");// 收费模式
			body.put("PRICE_STANDARD_DESC", "");// 收费标准
			body.put("SERVICE_FEE_CHAR_DATE", "");// 服务费收取日
			body.put("SERVICE_FEE_EXPIRY_DATE","");// 服务费到期日
		}
		//body.put("SFMS", pro.getFeeType());// 收费模式
		return body;
	}

	private List<Map> dataProcess(List<PedProtocolList> pros) throws Exception {
		List<Map> detail = new ArrayList();
		for (PedProtocolList pro : pros) {
			Map proMap = new HashMap<String, String>();
			//proMap.put("CONTRACT_INF_ARRAY.BUSS_CATEGORY", pro.getBusiType());// 业务类型
			//proMap.put("CONTRACT_INF_ARRAY.i", pro.getOpenFlag());// 融资池开通状态
			//proMap.put("CONTRACT_INF_ARRAY.VT_AMT_POOL_FLAG", pro.getvStatus());// 虚拟池开通状态
			//proMap.put("CONTRACT_INF_ARRAY.BPS_NO", pro.getPoolAgreement());// 票据池编号
			proMap.put("CONTRACT_INF_ARRAY.CLIENT_NAME", pro.getCustName());// 客户名称
			proMap.put("CONTRACT_INF_ARRAY.CORE_CLIENT_NO", pro.getCustNo());// 核心客户号
			proMap.put("CONTRACT_INF_ARRAY.ORG_CODE", pro.getOrgCoge());// 组织机构代码
			proMap.put("CONTRACT_INF_ARRAY.POOL_ACCOUNT", pro.getBpsNo());// 票据池账号
			proMap.put("CONTRACT_INF_ARRAY.AUTO_PLEDGE_INPOOL_FLAG", pro.getZyFlag());// 自动质押入池标志
			proMap.put("CONTRACT_INF_ARRAY.GROUP_ROLE_TYPE", pro.getRole());// 集团角色
			proMap.put("CONTRACT_INF_ARRAY.CLIENT_TYPE", pro.getCustIdentity());// 客户类型
			proMap.put("CONTRACT_INF_ARRAY.FINANCING_AMT", pro.getFinancLimit());// 融资限额
			proMap.put("CONTRACT_INF_ARRAY.FINANCING_MAX_AMT", pro.getMaxFinancLimit());// 最高融资限额
			proMap.put("CONTRACT_INF_ARRAY.SIGN_BILL_ID", pro.getElecDraftAccount());// 电票签约账户
			proMap.put("CONTRACT_INF_ARRAY.SIGN_BILL_NAME", pro.getElecDraftAccountName());// 电票签约账户名称

			PedCheck pedCheck = pedProtocolService.queryPedCheck(pro.getBpsNo(), pro.getCustNo());
			if(null!=pedCheck){
				proMap.put("CONTRACT_INF_ARRAY.RECON_RESULT", pedCheck.getCheckResult());// 对账结果
			}else{
				proMap.put("CONTRACT_INF_ARRAY.RECON_RESULT", "");// 对账结果
			}
			//是否已签约过（其他）自动入池业务 0：否 1：是
			boolean pedIsAuto = pedProtocolService.isAutoCheck(pro.getBpsNo(), pro.getCustNo());
			if (pedIsAuto) {
				proMap.put("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG",PoolComm.YES );
			}else{
				proMap.put("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG", PoolComm.NO);
			}
			detail.add(proMap);
		}
		return detail;
	}
	

	private List<Map> dataProcessOnly(PedProtocolDto pedProtocolDto) throws Exception {
		List<Map> detail = new ArrayList();
		Map proMap = new HashMap<String, String>();
		//proMap.put("CONTRACT_INF_ARRAY.BUSS_CATEGORY", pro.getBusiType());// 业务类型
		//proMap.put("CONTRACT_INF_ARRAY.FINANCING_POOL_FLAG", pro.getOpenFlag());// 融资池开通状态
		//proMap.put("CONTRACT_INF_ARRAY.VT_AMT_POOL_FLAG", pro.getvStatus());// 虚拟池开通状态
		//proMap.put("CONTRACT_INF_ARRAY.BPS_NO", pro.getPoolAgreement());// 票据池编号
		proMap.put("CONTRACT_INF_ARRAY.CLIENT_NAME", pedProtocolDto.getCustname());// 客户名称
		proMap.put("CONTRACT_INF_ARRAY.CORE_CLIENT_NO", pedProtocolDto.getCustnumber());// 核心客户号
		proMap.put("CONTRACT_INF_ARRAY.ORG_CODE", pedProtocolDto.getCustOrgcode());// 组织机构代码
		proMap.put("CONTRACT_INF_ARRAY.BPS_ACCT_NO", pedProtocolDto.getPoolAgreement());// 票据池账号
		proMap.put("CONTRACT_INF_ARRAY.AUTO_PLEDGE_INPOOL_FLAG", pedProtocolDto.getZyflag());// 自动质押入池标志
		//proMap.put("CONTRACT_INF_ARRAY.GROUP_ROLE_TYPE", "");// 集团角色
		//proMap.put("CONTRACT_INF_ARRAY.CLIENT_TYPE", "");// 客户类型
		//proMap.put("CONTRACT_INF_ARRAY.FINANCING_AMT", "");// 融资限额
		//proMap.put("CONTRACT_INF_ARRAY.FINANCING_MAX_AMT", "");// 最高融资限额
		proMap.put("CONTRACT_INF_ARRAY.SIGN_BILL_ID", pedProtocolDto.getElecDraftAccount());// 电票签约账户
		proMap.put("CONTRACT_INF_ARRAY.SIGN_BILL_NAME", pedProtocolDto.getElecDraftAccountName());// 电票签约账户名称
		PedCheck pedCheck = pedProtocolService.queryPedCheck(pedProtocolDto.getPoolAgreement(), pedProtocolDto.getCustnumber());
		if(null!=pedCheck){
			proMap.put("CONTRACT_INF_ARRAY.RECON_RESULT", pedCheck.getCheckResult());// 对账结果
		}else{
			proMap.put("CONTRACT_INF_ARRAY.RECON_RESULT", "");// 对账结果
		}
		//是否已签约过（其他）自动入池业务 0：否 1：是
		boolean pedIsAuto = pedProtocolService.isAutoCheck(pedProtocolDto.getPoolAgreement(), pedProtocolDto.getCustnumber());
		if (pedIsAuto) {
			proMap.put("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG",PoolComm.YES  );
		}else{
			proMap.put("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG", PoolComm.NO);
		}
		if(StringUtils.isNotBlank(pedProtocolDto.getOpenFlag())&& !pedProtocolDto.getOpenFlag().equals(PoolComm.OPEN_01)){
			proMap.put("CONTRACT_INF_ARRAY.AUTO_PLEDGE_INPOOL_FLAG", "");// 自动质押入池标志
		}
		detail.add(proMap);	
		return detail;
	}
	/**
	 * 集团子户返回信息处理
	 * @author Ju Nana
	 * @param pedProtocolList
	 * @return
	 * @throws Exception
	 * @date 2019-8-5下午6:56:07
	 */
	private List<Map> dataProcess(PedProtocolList pedProtocolList) throws Exception {
		List<Map> detail = new ArrayList();
			Map proMap = new HashMap<String, String>();
			proMap.put("CONTRACT_INF_ARRAY.CLIENT_NAME", pedProtocolList.getCustName());// 客户名称
			proMap.put("CONTRACT_INF_ARRAY.CORE_CLIENT_NO", pedProtocolList.getCustNo());// 核心客户号
			proMap.put("CONTRACT_INF_ARRAY.ORG_CODE", pedProtocolList.getOrgCoge());// 组织机构代码
			proMap.put("CONTRACT_INF_ARRAY.POOL_ACCOUNT", pedProtocolList.getBpsNo());// 票据池账号
			proMap.put("CONTRACT_INF_ARRAY.AUTO_PLEDGE_INPOOL_FLAG", pedProtocolList.getZyFlag());// 自动质押入池标志
			proMap.put("CONTRACT_INF_ARRAY.GROUP_ROLE_TYPE", pedProtocolList.getRole());// 集团角色
			proMap.put("CONTRACT_INF_ARRAY.CLIENT_TYPE", pedProtocolList.getCustIdentity());// 客户类型
			proMap.put("CONTRACT_INF_ARRAY.FINANCING_AMT", pedProtocolList.getFinancLimit());// 融资限额
			proMap.put("CONTRACT_INF_ARRAY.FINANCING_MAX_AMT", pedProtocolList.getMaxFinancLimit());// 最高融资限额
			proMap.put("CONTRACT_INF_ARRAY.SIGN_BILL_ID", pedProtocolList.getElecDraftAccount());// 电票签约账户
			proMap.put("CONTRACT_INF_ARRAY.SIGN_BILL_NAME", pedProtocolList.getElecDraftAccountName());// 电票签约账户名称

			PedCheck pedCheck = pedProtocolService.queryPedCheck(pedProtocolList.getBpsNo(), pedProtocolList.getCustNo());
			if(null!=pedCheck){
				proMap.put("CONTRACT_INF_ARRAY.RECON_RESULT", pedCheck.getCheckResult());// 对账结果
			}else{
				proMap.put("CONTRACT_INF_ARRAY.RECON_RESULT", "");// 对账结果
			}
			//是否已签约过（其他）自动入池业务 0：否 1：是
			boolean pedIsAuto = pedProtocolService.isAutoCheck(pedProtocolList.getBpsNo(), pedProtocolList.getCustNo());
			if (pedIsAuto) {
				proMap.put("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG",PoolComm.YES );
			}else{
				proMap.put("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG", PoolComm.NO);
			}
			detail.add(proMap);

		return detail;
	}
}
