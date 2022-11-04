
package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.common.util.BigDecimalUtils;
import com.mingtech.application.pool.infomanage.domain.CustomerRegister;
import com.mingtech.application.pool.infomanage.service.CustomerService;

/**
 * 
 * @Title: 网银接口PJC010
 * @Description: 签约管理接口
 * @author Ju Nana
 * @date 2018-10-22
 */
public class PJC010RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC010RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
    private PoolCoreService poolCoreService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	@Autowired
	private CustomerService customerService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
		// 构建信息接收对象
		PedProtocolDto protocol = QueryProtocolMap(request);
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		String poolAgreement = null;
		
		try {
			if(protocol.getEbankFlag().equals("02")){//解约时候判断有无已用额度，若有则不允许解约
				List<PedProtocolDto> dtoList = pedProtocolService.queryProtocolByParam(protocol.getPoolAgreement(), null, null, null);
				PedProtocolDto dto = null;
				/*解约条件：
				 * （1）判断虚拟票据池是解约状态，则告知已解约
				 * （2）若融资池为非开通状态，则直接解约
				 * （3）若融资池为开通状态，则判断总额度为 0（即：已用可用票据总额度均为0，核心保证金总额也为0）则允许解约，否则不允许
				 */
				if (dtoList != null && dtoList.size() > 0) {
					dto = dtoList.get(0);
					if (dto.getvStatus().equals(PoolComm.VS_01)) {
						if(PoolComm.OPEN_01.equals(dto.getOpenFlag())){//已签约状态
							List <PoolBillInfo>  poolListAll = new LinkedList<PoolBillInfo>();
							if(dto!=null && dto.getElecDraftAccount()!=null){
								String[] accArr = dto.getElecDraftAccount().split("\\|");
								for(String acc : accArr){
									List<PoolBillInfo> poolList  = poolEBankService.queryBillByElecAccAndType(acc, "1");//全部
									if(poolList!=null && poolList.size()>0){
										poolListAll.addAll(poolList);
									}
								}
							}
							
							BigDecimal bailTotalAmt = new BigDecimal("0.0");//票据池保证金总额度
							BigDecimal draftTotalAmt = new BigDecimal("0.0");//票据总额度
							//调用核心保证金查询接口，同步保证金信息
							CoreTransNotes transNotes = new CoreTransNotes();
							transNotes.setAccNo(dto.getMarginAccount());
							transNotes.setCurrentFlag("1");
							ReturnMessageNew resp = poolCoreService.PJH716040Handler(transNotes, "0");
							if (resp.isTxSuccess()) {
								Map map = resp.getBody();
								if (map.get("BALANCE") != null) { //核心账户余额
									bailTotalAmt = BigDecimalUtils.valueOf((String) map.get("BALANCE"));
								}
							}
							//查询票据总额度
							AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(protocol, PoolComm.ED_PJC);
							if(at!=null){
								draftTotalAmt = at.getCrdtTotal();
							}
							if(bailTotalAmt.compareTo(BigDecimal.ZERO)>0 || draftTotalAmt.compareTo(BigDecimal.ZERO)>0||poolListAll.size()>0){//若票据总额度或者保证金总额度有大于0，则不允许解约
								if(bailTotalAmt.compareTo(BigDecimal.ZERO)>0 ){
									ret.setRET_CODE(Constants.EBK_01);
									ret.setRET_MSG("该客户保证金账户余额不为0，不允许解约操作！");
									response.getBody().put("BPS_NO", dto.getPoolAgreement()); // 返回协议编号
								}
								if(draftTotalAmt.compareTo(BigDecimal.ZERO)>0){
									ret.setRET_CODE(Constants.EBK_01);
									ret.setRET_MSG("该客户有在途业务，不允许解约操作！");
									response.getBody().put("BPS_NO", dto.getPoolAgreement()); // 返回协议编号
								}
								if(poolListAll.size()>0){
									ret.setRET_CODE(Constants.EBK_01);
									ret.setRET_MSG("该客户有在途业务，不允许解约操作！");
									response.getBody().put("BPS_NO", dto.getPoolAgreement()); // 返回协议编号	
								}
								
								
							}else{//解约
								poolAgreement = poolEBankService.txPedProtocolDtoPJC010(protocol);
								response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
								ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
								ret.setRET_MSG("解约成功！");
							}
							
						}else{//直接解约
							poolAgreement = poolEBankService.txPedProtocolDtoPJC010(protocol);
							response.getBody().put("BPS_NO", poolAgreement); // 返回协议编号
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("解约成功！");
						}
					} else {
						ret.setRET_CODE(Constants.EBK_06);
						ret.setRET_MSG("该票据池已解约！");
					}
				}else{
					ret.setRET_CODE(Constants.EBK_03);
					ret.setRET_MSG("未找到该客户信息！");
				}
			}else if(protocol.getEbankFlag().equals("01")){
				ProtocolQueryBean queryBeanped = new ProtocolQueryBean();
				queryBeanped.setCustnumber(protocol.getCustnumber());
				queryBeanped.setIsGroup(PoolComm.NO);
				PedProtocolDto  pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBeanped);//根据客户号与是否集团查询票据池协议
				
				if(StringUtil.isBlank(protocol.getCustOrgcode())){
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("签约组织机构代码不能为空！");
					response.setRet(ret);
					return response;
				}
				if(null!=pedProtocolDto){
					if(pedProtocolDto.getvStatus().equals(PoolComm.VS_01)){
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("该客户已签约，不允许重复签约！");
					}else{
						poolAgreement = poolEBankService.txPedProtocolDtoPJC010(protocol);
						response.getBody().put("BPS_NO", poolAgreement); //返回协议编号
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("执行成功！");
					}
				}else{
					poolAgreement = poolEBankService.txPedProtocolDtoPJC010(protocol);
					response.getBody().put("BPS_NO", poolAgreement); //返回协议编号
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("执行成功！");
				}
		    	
			}else{//签约修改
				/*修改规则：
				 * （1）只允许修改修改电票签约账号及名称
				 * （2）若有需要去掉的电票签约账号，则检查该账号下有无不是初始化的票据，若有则不允许删除，若无，则删掉初始化的票据
				 */
				if(StringUtil.isBlank(protocol.getCustOrgcode())){
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("签约组织机构代码不能为空！");
					response.setRet(ret);
					return response;
				}
				
				ProtocolQueryBean  queryBeanped = new ProtocolQueryBean();
				queryBeanped.setCustnumber(protocol.getCustnumber());
				queryBeanped.setPoolAgreement(protocol.getPoolAgreement());
				queryBeanped.setvStatus(PoolComm.VS_01);
				queryBeanped.setIsGroup(PoolComm.NO);
				PedProtocolDto  pedProtocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBeanped);
				if(null!=pedProtocolDto) {
					//判断电票签约账号是否被修改
					String[] oldElecAccounts = null;// 原电票签约账号
					String[] newElecAccounts = null; // 新电票签约账号
					List<String> inOldNotinNew = new LinkedList<String>(); // 在原签约账号中有在新签约账号中没有的账号信息
					List<PoolBillInfo> poolList1 = new LinkedList<PoolBillInfo>();// 大票表中初始化的票据集合
					List<PoolBillInfo> poolList2 = new LinkedList<PoolBillInfo>();// 大票表中非初始化的票据集合
					if (pedProtocolDto.getElecDraftAccount() != null) {
						oldElecAccounts = pedProtocolDto.getElecDraftAccount().split("\\|");
					}
					if (protocol.getElecDraftAccount() != null) {
						newElecAccounts = protocol.getElecDraftAccount().split("\\|");
					}
					if (oldElecAccounts != null && oldElecAccounts.length > 0) {
						for (String old : oldElecAccounts) {
							if (!ArrayUtils.contains(newElecAccounts, old)) {// 如果新的电票签约账号中不包含原电票签约账号
								inOldNotinNew.add(old);
							}
						}
					}
					if (inOldNotinNew != null && inOldNotinNew.size() > 0) {
						for (String acc : inOldNotinNew) {
							List<PoolBillInfo> poolListA = poolEBankService.queryBillByElecAccAndTypeNew(acc, "0", protocol.getPoolAgreement());// 初始化的票
							if (poolListA != null && poolListA.size() > 0) {
								poolList1.addAll(poolListA);
							}
							List<PoolBillInfo> poolListB = poolEBankService.queryBillByElecAccAndTypeNew(acc, "1", protocol.getPoolAgreement());// 全部
							if (poolListB != null && poolListB.size() > 0) {
								poolList2.addAll(poolListB);
							}
						}
						if (poolList2 != null && poolList2.size() > 0) {
							response.getBody().put("BPS_NO", protocol.getPoolAgreement()); // 返回协议编号
							ret.setRET_CODE(Constants.EBK_09);
							ret.setRET_MSG("该客户已签约电票签约账户下有未处理完毕的业务，不允许更换电票账号！");
							response.setRet(ret);
							return response;
						}else{
							poolAgreement = poolEBankService.txPedProtocolDtoPJC010(protocol);
							response.getBody().put("BPS_NO", poolAgreement); //返回协议编号
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("签约修改执行成功！");
							if(poolList1!=null&&poolList1.size()>0){//如果该电票签约账号下只有初始化的票据，则删除所有初始化的数据，然后可以修改签约
								poolEBankService.txDeleteAll(poolList1);
							}
						}

					}else {
						poolAgreement = poolEBankService.txPedProtocolDtoPJC010(protocol);
						response.getBody().put("BPS_NO", poolAgreement); //返回协议编号
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("签约修改执行成功！");
					     }
				}else{
					ret.setRET_CODE(Constants.EBK_03);
					ret.setRET_MSG("未找到该客户有效的签约信息！");
				}
			}

		} catch (Exception e) {
			logger.error("虚拟票据池签约处理异常", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("虚拟票据池签约处理异常[" + e.getMessage() + "]");
		}
		response.setRet(ret);
		if(ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			/*
			 * 协议信息及集团协议子户信息备份
			 */
			if(StringUtil.isNotBlank(protocol.getPoolAgreement())){				
				pedProtocolService.txProtocolAndListHist(protocol.getPoolAgreement());
			}
			//生成协议客户信息对象
			CustomerRegister customer = new CustomerRegister();
			customer.setCustNo(protocol.getCustnumber());
			customer.setCustName(protocol.getCustname());
			customer.setFirstDateSource("PJC010");

			customerService.txSaveCustomerRegister(customer);
		}
		return response;
	}

	public static PedProtocolDto QueryProtocolMap(ReturnMessageNew request) throws Exception {

		PedProtocolDto protocol = new PedProtocolDto();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		String ebankFlag = (String) request.getBody().get("SIGN_FLAG");// 签约标识
		String custnumber = (String) request.getBody().get("CORE_CLIENT_NO");// 核心客户号
		String custOrgcode  = (String) request.getBody().get("ORG_CODE");// 组织机构代码
        String poolAgreement = (String) request.getBody().get("BPS_NO");// 协议编号
        String elecDraftAccount = (String) request.getBody().get("SIGN_BILL_ID"); //签约电票账号
        String elecDraftAccountName = (String) request.getBody().get("SIGN_BILL_NAME"); //签约电票账号名称
        String custname = (String) request.getBody().get("CLIENT_NAME");// 客户名称
		String busiType = (String) request.getBody().get("BUSS_TYPE");// 业务类型
        String signingFunction = (String)  request.getBody().get("SIGN_TYPE"); //签约功能
        String poolMode = (String) request.getBody().get("SIGN_MODE"); //签约模式
        String isGroup = (String) request.getBody().get("IS_GROUP");  //是否集团
        
        String stDate = (String) request.getBody().get("AGREE_EFFECTIVE_DATE");
        String edDate = (String) request.getBody().get("AGREE_EXPIRY_DATE");
        Date effstartdate =null;
        if(StringUtils.isNotBlank(stDate)){
        	effstartdate = sdf.parse(stDate);//协议生效日期
        }
        Date effenddate =null;
        if(StringUtils.isNotBlank(edDate)){
        	effenddate = sdf.parse(edDate);// 协议到期日
        }
        String licename = (String) request.getBody().get("OLD_APP_NAME");  //客户经办人1名称
        String authperson = (String) request.getBody().get("APP_GLOBAL_ID");  //客户经办人1身份证号
        String phonenumber = (String) request.getBody().get("APP_MOBIL");  //客户经办人1手机号码
        String xyflag = (String) request.getBody().get("AUTO_RENEW_FLAG");//是否自动续约  : 默认为全自动
        String signDeptNo = (String) request.getBody().get("SIGN_BRANCH_ID");//签约机构号
        String signDeptName = (String) request.getBody().get("SIGN_BRANCH_NAME");//签约机构名称
		protocol.setEbankFlag(ebankFlag);
		protocol.setCustnumber(custnumber);
		if(StringUtil.isNotEmpty(poolAgreement)){
			protocol.setPoolAgreement(poolAgreement);	
		}
		if(StringUtil.isNotBlank(elecDraftAccount)){
			protocol.setElecDraftAccount(elecDraftAccount);	
		}
		if(StringUtil.isNotBlank(elecDraftAccountName)){
			protocol.setElecDraftAccountName(elecDraftAccountName);
		}
		if(StringUtil.isNotBlank(custname)){
			protocol.setCustname(custname);
			protocol.setPoolName("票据池（"+custname+"）");//票据池名称
		}
		if(StringUtil.isNotBlank(busiType)){
			protocol.setBusiType(busiType);
		}
		if(StringUtil.isNotBlank(signingFunction)){
			protocol.setSigningFunction(signingFunction);
		}
//		if(StringUtil.isNotBlank(poolMode)){
//			protocol.setPoolMode(poolMode);
//		}
		if(StringUtil.isNotBlank(isGroup)){
			protocol.setIsGroup(isGroup);
			protocol.setContractType(PoolComm.QY_01);//单户票据池
		}
		if(effstartdate!=null){
			protocol.setEffstartdate(effstartdate);
		}
		if(effenddate!=null){
			protocol.setEffenddate(effenddate);
		}		
		if(StringUtil.isNotEmpty(licename)){
			protocol.setLicename(licename);	
		}
		if(StringUtil.isNotEmpty(authperson)){
			protocol.setAuthperson(authperson);
		}
		if(StringUtil.isNotEmpty(phonenumber)){
			protocol.setPhonenumber(phonenumber);
		}
		if(StringUtil.isNotEmpty(signDeptNo)){
			protocol.setSignDeptNo(signDeptNo);
		}
		if(StringUtil.isNotEmpty(signDeptName)){
			protocol.setSignDeptName(signDeptName);
		}
		protocol.setCreditamount(new BigDecimal("0.00"));
		
		protocol.setXyflag(PoolComm.YES);

		if(StringUtil.isNotBlank(custOrgcode)){
			protocol.setCustOrgcode(custOrgcode);
		}
		return protocol;

	}

	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
