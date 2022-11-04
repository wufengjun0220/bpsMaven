package com.mingtech.application.pool.bank.netbanksys.handler.handlermapping;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.framework.common.util.StringUtil;

public class PJC034Mapping  {
	
	/**
	 * 传入报文体请求数据整理
	 * @author Ju Nana
	 * @param request
	 * @return
	 * @throws Exception
	 * @date 2019-7-30下午5:17:45
	 */
	public static PedProtocolDto QueryProtocolMap(ReturnMessageNew request) throws Exception {
		
		PedProtocolDto protocol = new PedProtocolDto();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		String pSignType = (String) request.getBody().get("SIGN_TYPE"); //签约类型:  QYLX_01:基础签约     QYLX_02:融资签约
		protocol.setpSignType(pSignType);
		
		if(request.getBody().get("SIGN_FLAG")!=null){
			String ebankFlag = (String)  request.getBody().get("SIGN_FLAG"); //签约标识	
			if(ebankFlag.trim()!=""){
				
				protocol.setEbankFlag(ebankFlag);
			}
		}
		
		if(request.getBody().get("SIGN_OFF_TYPE")!=null){
			String pBreakType = (String) request.getBody().get("SIGN_OFF_TYPE");  //解约类型
			if(pBreakType.trim()!=""){
				
				protocol.setpBreakType(pBreakType);
			}
			
		}
		
		if(request.getBody().get("CORE_CLIENT_NO")!=null){			
			String custnumber = (String) request.getBody().get("CORE_CLIENT_NO");// 核心客户号
			if(custnumber.trim()!=""){
				
				protocol.setCustnumber(custnumber);
			}
		}
		
		if(request.getBody().get("CLIENT_NAME")!=null){
			String custname = (String) request.getBody().get("CLIENT_NAME");// 客户名称	
			if(custname.trim()!=""){
				
				protocol.setCustname(custname);
			}
		}
		
		if(request.getBody().get("ORG_CODE")!=null){
			String custOrgcode = (String) request.getBody().get("ORG_CODE"); //组织机构代码
			if(custOrgcode.trim()!=""){
				
				protocol.setCustOrgcode(custOrgcode);
			}
		}
		
		if(request.getBody().get("UNIFIED_CREDIT_CODE")!=null){
			String plUSCC = (String) request.getBody().get("UNIFIED_CREDIT_CODE"); //社会信用代码	
			if(plUSCC.trim()!=""){
				
				protocol.setPlUSCC(plUSCC);
			}
		}
		
		if(request.getBody().get("BPS_NO")!=null){
			String poolAgreement = (String) request.getBody().get("BPS_NO");// 票据池编号		
			if(StringUtil.isNotBlank(poolAgreement)){		
				protocol.setPoolAgreement(poolAgreement);
			}
		}
		
		if(request.getBody().get("SIGN_BILL_ID")!=null){
			String elecDraftAccount = (String) request.getBody().get("SIGN_BILL_ID");//签约电票账号	
			if(elecDraftAccount.trim()!=""){
				
				protocol.setElecDraftAccount(elecDraftAccount);
			}
		}
		
		if(request.getBody().get("SIGN_BILL_NAME")!=null){
			String elecDraftAccountName = (String) request.getBody().get("SIGN_BILL_NAME");//签约电票账号名称
			if(elecDraftAccountName.trim()!=""){
				
				protocol.setElecDraftAccountName(elecDraftAccountName);
			}
		}
		if(request.getBody().get("BUSS_TYPE")!=null){
			String busiType = (String) request.getBody().get("BUSS_TYPE");//业务类型
			if(busiType.trim()!=""){
				
				protocol.setBusiType(busiType);
			}
		}
		
		if(request.getBody().get("SUB_SIGN_TYPE")!=null){
			String signingFunction = (String) request.getBody().get("SUB_SIGN_TYPE");//签约类型  签约功能
			if(signingFunction.trim()!=""){
				
				protocol.setSigningFunction(signingFunction);
			}
			
		}
		if(request.getBody().get("SIGN_MODE")!=null){
			String poolMode = (String) request.getBody().get("SIGN_MODE");//签约方式总量控制
			if(poolMode.trim()!=""){
				
				protocol.setPoolMode(poolMode);
				//保证金划转是否审批
				if(poolMode.equals("01")){//保证金支取是否人工审核   0-否  1-是 总量模式默认为是，期限配比模式默认为否
					protocol.setIsAcctCheck("1");
				}else if(poolMode.equals("02")){
					protocol.setIsAcctCheck("0");
				}
				
			}
		}
		
		if(request.getBody().get("IS_GROUP")!=null){
			String isGroup = (String) request.getBody().get("IS_GROUP");//是否集团	
			if(isGroup.trim()!=""){
				
				protocol.setIsGroup(isGroup);
			}
		}
/*
		Object obj1 = request.getBody().get("AGREE_EFFECTIVE_DATE");
		Date value = null;
		if (obj1 != null) {
			String temp = (String) obj1;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
				protocol.setEffstartdate(value);//协议生效日
			}
		}
		
		Object obj2 = request.getBody().get("AGREE_EXPIRY_DATE");
		Date value2 = null;
		if (obj2 != null) {
			String temp = (String) obj2;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
				protocol.setEffstartdate(value2);//协议到期日
			}
		}
		
*/
		if(request.getBody().get("AUTO_RENEW_FLAG")!=null){
			String xyflag = (String) request.getBody().get("AUTO_RENEW_FLAG");//自动续约标志
			if(xyflag.trim()!=""){
				protocol.setXyflag(xyflag);
				
			}
			
		}
		if(request.getBody().get("SIGN_BRANCH_ID")!=null){
			String signDeptNo = (String) request.getBody().get("SIGN_BRANCH_ID");//签约机构号
			if(signDeptNo.trim()!=""){
				protocol.setSignDeptNo(signDeptNo);
				
			}
		}
		if(request.getBody().get("SIGN_BRANCH_NAME")!=null){
			String signDeptName = (String) request.getBody().get("SIGN_BRANCH_NAME");//签约机构名称
			if(signDeptName.trim()!=""){
				
				protocol.setSignDeptName(signDeptName);
			}
		}
		
		//集团是否自动入池的控制在子户表中
//		if(request.getBody().get("SFZDRC")!=null){
//			String zyflag = (String) request.getBody().get("SFZDRC");//是否自动入池
//			protocol.setZyflag(zyflag);
//		}
		if(request.getBody().get("RCV_BRANCH_ID")!=null){
			String officeNet = (String) request.getBody().get("RCV_BRANCH_ID");//受理网点
			if(officeNet.trim()!=""){
				
				protocol.setOfficeNet(officeNet);
			}
			
		}
		if(request.getBody().get("RCV_BRANCH_NAME")!=null){
			String officeNetName = (String) request.getBody().get("RCV_BRANCH_NAME");//受理网点名称
			if(officeNetName.trim()!=""){
				
				protocol.setOfficeNetName(officeNetName);
			}
			
		}
		if(request.getBody().get("CUST_MANAGER_ID")!=null){
			String accountManagerId = (String) request.getBody().get("CUST_MANAGER_ID");//客户经理id
			if(accountManagerId.trim()!=""){
				
				protocol.setAccountManagerId(accountManagerId);
			}
			
		}
		if(request.getBody().get("CUST_MANAGER_NAME")!=null){
			String accountManager = (String) request.getBody().get("CUST_MANAGER_NAME");//客户经理姓名
			if(accountManager.trim()!=""){
				
				protocol.setAccountManager(accountManager);
			}
		}
		
		protocol.setCreditamount(new BigDecimal("0.00"));
		
		if(request.getBody().get("DEPOSIT_ACCT_NO")!=null){
			String marginAccount = (String) request.getBody().get("DEPOSIT_ACCT_NO");//保证金账户
			if(marginAccount.trim()!=""){
				
				protocol.setMarginAccount(marginAccount);
			}
			
		}
		if(request.getBody().get("DEPOSIT_ACCT_NAME")!=null){
			String marginAccountName = (String) request.getBody().get("DEPOSIT_ACCT_NAME");//池保证金账户名称
			if(marginAccountName.trim()!=""){
				
				protocol.setMarginAccountName(marginAccountName);
			}
			
		}
		if(request.getBody().get("SETTLE_ACCT_NO")!=null){
			String poolAccount = (String) request.getBody().get("SETTLE_ACCT_NO");//结算账户
			if(poolAccount.trim()!=""){
				
				protocol.setPoolAccount(poolAccount);
			}
			
		}
		if(request.getBody().get("SETTLE_ACCT_NAME")!=null){
			String poolAccountName = (String) request.getBody().get("SETTLE_ACCT_NAME");//结算账户名
			if(poolAccountName.trim()!=""){
				
				protocol.setPoolAccountName(poolAccountName);
			}
			
		}
		
		if(request.getBody().get("FEE_MODE")!=null){
			String feeType = (String) request.getBody().get("FEE_MODE");//收费模式	
			if(feeType.trim()!=""){
				protocol.setFeeType(feeType);
				
			}
		}
		 //签约默认保证金划转审批为否
        protocol.setIsAcctCheck(PoolComm.NO);
		
		return protocol;

	}
	
	
	public static PedProtocolList QueryDetailMap (Map map) {	
		
		PedProtocolList pedList = new PedProtocolList();
		
		if(map.get("CONTRACT_INF_ARRAY.CLIENT_NAME")!=null){
			String custname =(String) map.get("CONTRACT_INF_ARRAY.CLIENT_NAME"); //客户名称 			
			pedList.setCustName(custname);
			
		}
		if(map.get("CONTRACT_INF_ARRAY.CORE_CLIENT_NO")!=null){
			String custNo = (String) map.get("CONTRACT_INF_ARRAY.CORE_CLIENT_NO");// 核心客户号
			pedList.setCustNo(custNo);
			
		}
		if(map.get("CONTRACT_INF_ARRAY.ORG_CODE")!=null){
			String orgCoge = (String) map.get("CONTRACT_INF_ARRAY.ORG_CODE"); //组织机构代码
			pedList.setOrgCoge(orgCoge);
			
		}
		if(map.get("CONTRACT_INF_ARRAY.UNIFIED_CREDIT_CODE")!=null){
			String socialCode = (String) map.get("CONTRACT_INF_ARRAY.UNIFIED_CREDIT_CODE"); //社会信用代码
			pedList.setSocialCode(socialCode);
			
		}
		if(map.get("CONTRACT_INF_ARRAY.GROUP_ROLE_TYPE")!=null){
			String role = (String) map.get("CONTRACT_INF_ARRAY.GROUP_ROLE_TYPE");//集团角色
			pedList.setRole(role);
			
		}
		System.out.println(map.get("CONTRACT_INF_ARRAY.FINANCING_AMT"));
		if(map.get("CONTRACT_INF_ARRAY.FINANCING_AMT")!=null && StringUtil.isNotEmpty((String)map.get("CONTRACT_INF_ARRAY.FINANCING_AMT"))){
			System.out.println("-------------------------");
			String financLimit = (String) map.get("CONTRACT_INF_ARRAY.FINANCING_AMT"); //融资限额
			pedList.setFinancLimit(new BigDecimal(financLimit));
			
		}
		if(map.get("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG")!=null){
			String zyFlag = (String) map.get("CONTRACT_INF_ARRAY.AUTO_INPOOL_FLAG"); //是否自动入池
			pedList.setZyFlag(zyFlag);
			
		}
		if(map.get("CONTRACT_INF_ARRAY.SIGN_BILL_ID")!=null){
			String elecDraftAccount = (String) map.get("CONTRACT_INF_ARRAY.SIGN_BILL_ID");  //签约电票账号
			pedList.setElecDraftAccount(elecDraftAccount);
			
		}
		if(map.get("CONTRACT_INF_ARRAY.SIGN_BILL_NAME")!=null){
			
			String elecDraftAccountName = (String) map.get("CONTRACT_INF_ARRAY.SIGN_BILL_NAME");//签约电票账号名称
			pedList.setElecDraftAccountName(elecDraftAccountName);
		}

		return pedList;

	}
}
