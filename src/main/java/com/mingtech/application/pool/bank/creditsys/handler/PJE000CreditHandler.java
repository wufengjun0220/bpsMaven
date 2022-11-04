package com.mingtech.application.pool.bank.creditsys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.mingtech.application.ecds.common.BigDecimalUtils;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.creditsys.service.PoolCreditService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.pool.financial.service.FinancialService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * @author zhaoding
 * 
 * @描述：担保合同信息推送
 */
public class PJE000CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE000CreditHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCreditService poolCreditService;
	@Autowired
	private FinancialService financialService;
	@Autowired
	private AssetRegisterService assetRegisterService;
	@Autowired
	private PoolBailEduService poolBailEduService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		String PJCBH ="";
		boolean isMoodChange = false;
		PedProtocolDto dto = null;
		try {
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));// 核心客户号
			PJCBH = getStringVal(request.getBody().get("BPS_SEQ_NO")); // 票据池编号
//			String ZWLX = getStringVal(request.getBody().get("RISK_LEVEL")); // 风险等级
			String HTBH = getStringVal(request.getBody().get("CONTRACT_NO")); // 担保合同编号
			Date QDRQ = getDateVal(request.getBody().get("SIGN_DATE")); // 合同签订日期
			Date QSR = getDateVal(request.getBody().get("START_DATE")); // 起始日
			Date DQR = getDateVal(request.getBody().get("EXPIRY_DATE")); // 到期日
			BigDecimal HTJE = getBigDecimalVal(request.getBody().get("CONTRACT_AMT")); // 合同金额
			String branchNo = getStringVal(request.getBody().get("FINANCING_BRANCH_NO"));// 机构号
			String branchName = getStringVal(request.getBody().get("FINANCING_BRANCH_NAME"));// 机构名称
			String pluscc = getStringVal(request.getBody().get("UNIFIED_CREDIT_CODE"));//社会信用代码
			String bussMode = getStringVal(request.getBody().get("BUSS_MODE"));//业务模式01:总量控制02：期限配比

			List details = request.getDetails();
			List<PedProtocolDto> dtoList =  pedProtocolService.queryProtocolInfo(null, null, PJCBH, null, null, null);

			
			
			PedProtocolList protocol = null;
			List<PedProtocolList> pedListsNew = new ArrayList<PedProtocolList>();//存放信贷最新担保合同推送的集团成员
			
			if (dtoList == null || dtoList.size() == 0) {
				ret.setRET_CODE(Constants.CREDIT_01);
				ret.setRET_MSG(custNo + ":该客户未开通融资票据池功能！");
				response.setRet(ret);
				return response;
			}
			
			dto = dtoList.get(0);
			String bpsNo = dto.getPoolAgreement();
			if (!PoolComm.OPEN_01.equals(dto.getOpenFlag())) {
				ret.setRET_CODE(Constants.CREDIT_01);
				ret.setRET_MSG(custNo + ":该客户未开通融资票据池功能！");
				response.setRet(ret);
				return response;
			}
			
			//判断合同生效日
			if(DateUtils.compareDate(DateUtils.getWorkDayDate(), DQR)>0){//协议已到期
				ret.setRET_CODE(Constants.CREDIT_08);
				ret.setRET_MSG(custNo + ":该客户开通融资票据池担保合同到期！");
				response.setRet(ret);
				return response;
			}
			

			//额度校验
			if(null!=dto.getContract()){//没推过担保合同的时候不校验
				
				//保证金同步及保证及资产登记处理
				BailDetail bail = poolBailEduService.queryBailDetailByBpsNo(bpsNo);
				assetRegisterService.txCurrentDepositAssetChange(bpsNo, bail.getAssetNb(), bail.getAssetLimitFree());

				if(null != dto.getPoolMode()&&StringUtil.isNotBlank(dto.getPoolMode())){				
					String oldMood = dto.getPoolMode();
					if(!oldMood.equals(bussMode)){//原模式与新模式不一致
						isMoodChange = true;
						String flowNo = Long.toString(System.currentTimeMillis());
						dto.setPoolMode(bussMode);
						Ret eduCheckRet = financialService.txCreditUsedCheck(null, dto, flowNo);
						if(Constants.TX_FAIL_CODE.equals(eduCheckRet.getRET_CODE())){
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("票据池变更为新模式之后池额度不足，原担保合同不允许失效！");
							response.setRet(ret);
							return response;
						}
					}
				}
				
			}

			/*	担保合同推送时  	1)初始“总量模式”默认为“是”；
			 *				2)初始“期限匹配模式”默认为“否”
			 */
			if(bussMode.equals("01")){//保证金支取是否人工审核   0-否  1-是 总量模式默认为是，期限配比模式默认为否
				dto.setIsAcctCheck(PoolComm.YES);
			}else if(bussMode.equals("02")){
				dto.setIsAcctCheck(PoolComm.NO);
			}
			logger.info("推送的担保合同编号为------------------------:"+HTBH);
			//单户票据池
			if(dto.getIsGroup().equals(PoolComm.NO)){
				logger.info("单户推送------------------------:"+HTBH);
				dto.setContract(HTBH);
				dto.setCreditamount(HTJE);
				dto.setCreditFreeAmount(dto.getCreditamount().subtract(dto.getCreditUsedAmount()));
				dto.setContractTransDt(QDRQ);
				dto.setContractEffectiveDt(QSR);
				dto.setContractDueDt(DQR);
				dto.setCreditDeptNo(branchNo);
				dto.setCreditDeptName(branchName);
				dto.setPoolMode(bussMode);
				if(pluscc != null && !pluscc.trim().equals("") &&!pluscc.trim().equals("00000000") ){
					dto.setPlUSCC("00000000"+pluscc+"0");
				}else {
					dto.setPlUSCC("00000000"+dto.getCustOrgcode()+"0");
				}
				pedProtocolService.txStore(dto);
				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("担保合同信息推送成功！");
				
			}
			
			//集团票据池
			if(dto.getIsGroup().equals(PoolComm.YES)){//集团票据池
				logger.info("集团推送------------------------:"+HTBH);
				dto.setContract(HTBH);
				dto.setCreditamount(HTJE);
				dto.setCreditFreeAmount(dto.getCreditamount().subtract(dto.getCreditUsedAmount()));
				dto.setContractTransDt(QDRQ);
				dto.setContractEffectiveDt(QSR);
				dto.setContractDueDt(DQR);
				dto.setCreditDeptNo(branchNo);
				dto.setCreditDeptName(branchName);
				dto.setPoolMode(bussMode);
				//1、查询网银推送过来的已签约的集团成员
				ProListQueryBean queryBean = new ProListQueryBean();
				queryBean.setBpsNo(PJCBH);
				queryBean.setCustNo(custNo);
				queryBean.setStatus(PoolComm.PRO_LISE_STA_01);  
				protocol = pedProtocolService.queryProtocolListByQueryBean(queryBean);//查询主户信息
				
				if(protocol == null){
					ret.setRET_CODE(Constants.CREDIT_06);
					ret.setRET_MSG("主户未在网银签约成员名单！");
					response.setRet(ret);
					return response;
				}
				
				//是否首次签约
				if(protocol.getCustIdentity().equals(PoolComm.KHLX_04)){//客户身份签约  首次推送担保协议
					logger.info("客户号["+custNo+"]首次签约!发送的成员有["+details.size()+"]条");
					//2、校验主户是否为出质人
					
					for (int i = 0; i < details.size(); i++) {
						Map map = (Map) details.get(i);
						String coreNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CORE_CLIENT_NO"));//核心客户号
						String coreNoName = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_NAME"));//户名
						String orgNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.ORG_CODE"));//组织机构代码
						BigDecimal maxEdu = getBigDecimalVal(map.get("GROUP_CLIENT_INFO_ARRAY.FINANCING_MAX_AMT"));//最高限额
						String type = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_TYPE"));//客户类型
						String socialCode = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.UNIFIED_CREDIT_CODE"));//社会信用代码
						
						if(coreNo.equals(custNo)){//客户是否为主户
							if(type.equals(PoolComm.KHLX_02)){//主户为融资人
								ret.setRET_CODE(Constants.CREDIT_06);
								ret.setRET_MSG("主户不为出质人！");
								response.setRet(ret);
								return response;
							}
						}
						
						if(type.equals(PoolComm.KHLX_01) || type.equals(PoolComm.KHLX_03)){//客户类型包含出质人	
							ProListQueryBean bean = new ProListQueryBean();
							bean.setBpsNo(PJCBH);
							bean.setCustNo(coreNo);
							bean.setStatus(PoolComm.PRO_LISE_STA_01);  
							protocol = pedProtocolService.queryProtocolListByQueryBean(bean);
							
							if(protocol == null){//不在网银推送的签约名单
								ret.setRET_CODE(Constants.CREDIT_06);
								ret.setRET_MSG(coreNo+":客户类型为出质人的成员未在网银签约成员名单！");
								response.setRet(ret);
								return response;
							}else {
								//校验出质人（户名、客户号、组代）是否一致
								System.out
								.println("查询的户名["+protocol.getCustName()+"],主代["+protocol.getOrgCoge()+"],客户号["+protocol.getCustNo()+"]");
								if(protocol.getCustName().equals(coreNoName) && protocol.getOrgCoge().equals(orgNo)){
									System.out
									.println("校验一致!");
									
									if(type.equals(PoolComm.KHLX_01)){//客户类型为出质人
										protocol.setCustIdentity(type);
										if(socialCode != null && !socialCode.trim().equals("")){
											protocol.setSocialCode(socialCode);
										}else {
											protocol.setSocialCode("00000000"+orgNo+"0");
										}
										
										pedListsNew.add(protocol);
									}else {
										protocol.setMaxFinancLimit(BigDecimalUtils.setScale(maxEdu));
										protocol.setFinancLimit(BigDecimal.ZERO);//融资限额
										protocol.setCustIdentity(PoolComm.KHLX_03);//出质人+融资人
										protocol.setFinancingStatus("SF_01");//融资人身份生效
										if(socialCode != null && !socialCode.trim().equals("")){
											protocol.setSocialCode(socialCode);
										}else {
											protocol.setSocialCode("00000000"+orgNo+"0");
										}
										pedListsNew.add(protocol);
									}
								}else {
									logger.info("客户号、客户名称或者组织机构代码核对不一致！");
									ret.setRET_CODE(Constants.CREDIT_06);
									ret.setRET_MSG("客户号、客户名称或者组织机构代码核对不一致！");
									response.setRet(ret);
									return response;
								}
							}
						}else{//融资人
							ProListQueryBean bean = new ProListQueryBean();
							bean.setBpsNo(PJCBH);
							bean.setCustNo(coreNo);
							protocol = pedProtocolService.queryProtocolListByQueryBean(bean);
							if(protocol == null ){
								protocol = new PedProtocolList();
								protocol.setBpsNo(PJCBH);
								protocol.setBpsName(dto.getPoolName());
								protocol.setCustNo(coreNo);
								protocol.setCustName(coreNoName);
								protocol.setOrgCoge(orgNo);
								protocol.setStatus(PoolComm.PRO_LISE_STA_00);
								protocol.setEditTime(DateUtils.getCurrDate());
								protocol.setMaxFinancLimit(BigDecimalUtils.setScale(maxEdu));
								protocol.setFinancLimit(BigDecimal.ZERO);//融资限额
								protocol.setCustIdentity(PoolComm.KHLX_02);//融资人
								protocol.setFinancingStatus("SF_01");//融资人身份生效
								protocol.setRole("02");
								protocol.setStatus("00");//未签约
								if(socialCode != null && !socialCode.trim().equals("")){
									protocol.setSocialCode(socialCode);
								}else {
									protocol.setSocialCode("00000000"+orgNo+"0");
								}
								
							}else {
								protocol.setMaxFinancLimit(BigDecimalUtils.setScale(maxEdu));
								protocol.setFinancLimit(BigDecimal.ZERO);//融资限额
								protocol.setCustIdentity(PoolComm.KHLX_02);//融资人
								protocol.setFinancingStatus("SF_01");//融资人身份生效
							}
							pedListsNew.add(protocol);
						}
					}
					pedProtocolService.txStoreAll(pedListsNew);//将最新担保信息保存
					pedProtocolService.txStore(dto);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("担保合同信息推送成功！");
					
				}else{//再次推送
					PedProtocolList pedProtocolList  = null;
					PedProtocolList pedPledge = null;
					List custAllList = pedProtocolService.queryPedListByParam(PJCBH, null, null, null, null);//查询该票据池编号下的所有客户
					List lastPledgeList = pedProtocolService.queryPedListByParam(PJCBH, null, null, "1", null);//上次担保推送的出质人员
					List lastRZproList = pedProtocolService.queryPedListByParam(PJCBH, null, null, "2", null);//上次担保推送的融资人员
					List pledgeCustNos = pedProtocolService.queryPedListCustNoByParam(PJCBH, "01", null);//网银推送的所有签约成员的客户号集合
					
					List lastCustNos = pedProtocolService.queryPedListCustNoByParam(PJCBH, null, "1");//上次信贷上次推送担保合同的出质人客户号集合
					
					List newCustNos = new ArrayList();//存储最新推送的担保合同出质人的客户号
					List lastList = new ArrayList();//存储最新推送的担保合同出质人的客户对象
					for (int i = 0; i < details.size(); i++) {
						Map map = (Map) details.get(i);
						String coreNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CORE_CLIENT_NO"));//核心客户号
						String coreNoName = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_NAME"));//户名
						String orgNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.ORG_CODE"));//组织机构代码
						String type = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_TYPE"));//客户类型
						
						if(type.equals(PoolComm.KHLX_01) || type.equals(PoolComm.KHLX_03)){//客户类型包含出质人
							ProListQueryBean bean = new ProListQueryBean();
							bean.setBpsNo(PJCBH);
							bean.setCustNo(coreNo);
							bean.setStatus(PoolComm.PRO_LISE_STA_01);  
							protocol = pedProtocolService.queryProtocolListByQueryBean(bean);
							
							if(protocol == null){//不在网银推送的签约名单
								ret.setRET_CODE(Constants.CREDIT_06);
								ret.setRET_MSG(coreNo+":客户类型为出质人的成员未在网银签约成员名单！");
								response.setRet(ret);
								return response;
							}else {
								//校验出质人（户名、客户号、组代）是否一致
								System.out
								.println("查询的户名["+protocol.getCustName()+"],主代["+protocol.getOrgCoge()+"],客户号["+protocol.getCustNo()+"]");
								if(protocol.getCustName().equals(coreNoName) && protocol.getOrgCoge().equals(orgNo)){
									lastList.add(protocol);
									newCustNos.add(coreNo);
								}else {
									ret.setRET_CODE(Constants.CREDIT_06);
									ret.setRET_MSG("客户号、客户名称或者组织机构代码核对不一致！！");
									throw new Exception("客户号、客户名称或者组织机构代码核对不一致！！");
								}
							}
						}
						
					}
					//1、出质人小于签约成员
					if(pledgeCustNos.containsAll(newCustNos)){//信贷新推送的出质人包含于网银推送的签约成员
						//2、出质人包含前一次推送所有出质人直接生效
						if(newCustNos.containsAll(lastCustNos)){//信贷上次推送的出质人包含于最新推送的出质人,增加了出质人
							
							for (int i = 0; i < details.size(); i++) {
								PedProtocolList pedpro = null;
								Map map = (Map) details.get(i);
								String coreNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CORE_CLIENT_NO"));//核心客户号
								BigDecimal maxEdu = getBigDecimalVal(map.get("GROUP_CLIENT_INFO_ARRAY.FINANCING_MAX_AMT"));//最高限额
								String type = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_TYPE"));//客户类型
								String coreNoName = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_NAME"));//户名
								String orgNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.ORG_CODE"));//组织机构代码
								String socialCode = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.UNIFIED_CREDIT_CODE"));//社会信用代码
								
								ProListQueryBean bean = new ProListQueryBean();
								bean.setBpsNo(PJCBH);
								bean.setCustNo(coreNo);
								pedpro = pedProtocolService.queryProtocolListByQueryBean(bean);
								
								if(type.equals(PoolComm.KHLX_01)){
									if(pedpro.getMaxFinancLimit() != null){
										pedpro.setMaxFinancLimit(BigDecimal.ZERO);
										pedpro.setFinancLimit(BigDecimal.ZERO);
									}
									pedpro.setCustIdentity(type);
									if(socialCode != null && !socialCode.trim().equals("")){
										pedpro.setSocialCode(socialCode);
									}else {
										pedpro.setSocialCode("00000000"+orgNo+"0");
									}
									pedListsNew.add(pedpro);
								}
								if(type.equals(PoolComm.KHLX_02) || type.equals(PoolComm.KHLX_03)){
									if(pedpro == null){
										pedpro = new PedProtocolList();
										pedpro.setBpsNo(PJCBH);
										pedpro.setBpsName(dto.getPoolAccountName());
										pedpro.setCustNo(coreNo);
										pedpro.setCustName(coreNoName);
										pedpro.setOrgCoge(orgNo);
										pedpro.setStatus(PoolComm.PRO_LISE_STA_00);
										pedpro.setEditTime(DateUtils.getCurrDate());
										pedpro.setMaxFinancLimit(BigDecimalUtils.setScale(maxEdu));
										pedpro.setFinancLimit(BigDecimal.ZERO);//融资限额
										pedpro.setCustIdentity(PoolComm.KHLX_02);//融资人
										pedpro.setRole("02");
										if(socialCode != null && !socialCode.trim().equals("")){
											pedpro.setSocialCode(socialCode);
										}else {
											pedpro.setSocialCode("00000000"+orgNo+"0");
										}
										pedpro.setFinancingStatus("SF_01");//融资人身份生效
									}else {
										pedpro.setMaxFinancLimit(BigDecimalUtils.setScale(maxEdu));
										if(pedpro.getFinancLimit() == null){
											pedpro.setFinancLimit(BigDecimal.ZERO);//融资限额
										}
										pedpro.setCustIdentity(type);
										pedpro.setFinancingStatus("SF_01");//融资身份生效
										if(socialCode != null && !socialCode.trim().equals("")){
											pedpro.setSocialCode(socialCode);
										}else {
											pedpro.setSocialCode("00000000"+orgNo+"0");
										}
									}
									pedListsNew.add(pedpro);
								}
								
							}
							
						}else{
							PoolQueryBean bean = new PoolQueryBean();
							ArrayList<String> status = new ArrayList<String>();//存票据状态
							//3、若比第一次的出质成员人减少，
							if(lastPledgeList!=null && lastPledgeList.size()>0){
								
								for (int i = 0; i < lastPledgeList.size(); i++) {
									pedProtocolList = (PedProtocolList) lastPledgeList.get(i);
									for (int j = 0; j < details.size(); j++) {//再次推送的成员
										Map map = (Map) details.get(j);
										String type = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_TYPE"));//客户类型
										String custNo2 = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CORE_CLIENT_NO"));//再次推送的成员的客户号
										if(type.equals(PoolComm.KHLX_01) || type.equals(PoolComm.KHLX_03)){//若为出质人
											
//														pedPledge = (PedProtocolList) lastList.get(j);
											if(pedProtocolList.getCustNo().equals(custNo2)){//此次推送的人员减少的成员
												lastPledgeList.remove(i);
												i--;
											}
										}
									}
								}
								//得到减少的成员集合   lastPledgeList   查询在池票据,若有不允许新担保合同生效
								logger.info("减少的出质人成员有["+lastPledgeList.size()+"]个");
								for (int i = 0; i < lastPledgeList.size(); i++) {
									pedProtocolList = (PedProtocolList) lastPledgeList.get(i);
									logger.info("减少的出质人成员的客户号为["+pedProtocolList.getCustNo()+"]");
									//减少的成员为出质人
									bean.setCustomernumber(pedProtocolList.getCustNo());//客户号
									bean.setProtocolNo(pedProtocolList.getBpsNo());//票据池编号
									status.add(PoolComm.DS_02);
									status.add(PoolComm.DS_06);
									bean.setStatus(status);
									List draftList = poolCreditService.queryDraftInfos(bean, null);
									if(draftList != null && draftList.size() >0){
										ret.setRET_CODE(Constants.CREDIT_06);
										ret.setRET_MSG("客户号["+pedProtocolList.getCustNo()+"]的客户,有入池票据，请出池后签担保合同！");
										throw new Exception("客户号["+pedProtocolList.getCustNo()+"]的客户,有入池票据，请出池后签担保合同！");
									}else {
										//减少的出质人,其客户身份置为签约成员
										pedProtocolList.setCustIdentity(PoolComm.KHLX_04);
										pedProtocolList.setMaxFinancLimit(BigDecimal.ZERO);
										pedProtocolList.setFinancLimit(BigDecimal.ZERO);
//													pedProtocolList.setFinancingStatus("SF_00");//融资身份失效
										pedProtocolService.txStore(pedProtocolList);
									}
									logger.info("保存后该客户的客户身份为["+pedProtocolList.getCustIdentity()+"]");
								}
							}
							
							//4、若比第一次的融资成员人减少，
							if(lastRZproList!=null && lastRZproList.size()>0){												
								for (int i = 0; i < lastRZproList.size(); i++) {
									pedProtocolList = (PedProtocolList) lastRZproList.get(i);
									for (int j = 0; j < details.size(); j++) {//再次推送的成员
										Map map = (Map) details.get(j);
										String type = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_TYPE"));//客户类型
										String custNo2 = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CORE_CLIENT_NO"));//再次推送的成员的客户号
										if(type.equals(PoolComm.KHLX_02)){//若为融资人
//														pedPledge = (PedProtocolList) lastList.get(j);
											if(pedProtocolList.getCustNo().equals(custNo2)){//此次推送的人员减少的成员
												lastRZproList.remove(i);
												i--;
											}
										}
									}
								}
								
								//得到减少的融资成员集合   lastPledgeList
								logger.info("减少的融资人成员有["+lastRZproList.size()+"]个");
								
								for (int i = 0; i < lastRZproList.size(); i++) {
									pedProtocolList = (PedProtocolList) lastRZproList.get(i);
									logger.info("减少的融资人成员的客户号为["+pedProtocolList.getCustNo()+"]");
									//减少的成员为融资人
									if(pedProtocolList.getStatus().equals(PoolComm.PRO_LISE_STA_01)) {//已签约表示签约成员
										pedProtocolList.setCustIdentity(PoolComm.KHLX_04);
										pedProtocolList.setMaxFinancLimit(BigDecimal.ZERO);
										pedProtocolList.setFinancLimit(BigDecimal.ZERO);
										pedProtocolService.txStore(pedProtocolList);
									}else {
										pedProtocolList.setMaxFinancLimit(BigDecimal.ZERO);
										pedProtocolList.setFinancLimit(BigDecimal.ZERO);
										pedProtocolList.setCustIdentity(PoolComm.KHLX_05);
										pedProtocolService.txStore(pedProtocolList);
									}
									logger.info("保存后该客户的客户身份为["+pedProtocolList.getCustIdentity()+"]");
								}
							}
							
							logger.info("处理推送List集合开始-----------------------------------------");
							for (int i = 0; i < details.size(); i++) {
								PedProtocolList pedpro = null;
								Map map = (Map) details.get(i);
								String coreNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CORE_CLIENT_NO"));//核心客户号
								BigDecimal maxEdu = getBigDecimalVal(map.get("GROUP_CLIENT_INFO_ARRAY.FINANCING_MAX_AMT"));//最高限额
								String type = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_TYPE"));//客户类型
								String coreNoName = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.CLIENT_NAME"));//户名
								String orgNo = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.ORG_CODE"));//组织机构代码
								String socialCode3 = getStringVal(map.get("GROUP_CLIENT_INFO_ARRAY.UNIFIED_CREDIT_CODE"));//社会信用代码
								
								ProListQueryBean queryB = new ProListQueryBean();
								queryB.setBpsNo(PJCBH);
								queryB.setCustNo(coreNo);
								pedpro = pedProtocolService.queryProtocolListByQueryBean(queryB);
								logger.info("推送的客户类型为["+type+"]");
								if(type.equals(PoolComm.KHLX_01)){
									if(pedpro.getMaxFinancLimit() != null){
										pedpro.setMaxFinancLimit(null);
										pedpro.setFinancLimit(null);
									}
									pedpro.setCustIdentity(type);
									if(socialCode3 != null && !socialCode3.trim().equals("")){
										pedpro.setSocialCode(socialCode3);
									}else {
										pedpro.setSocialCode("00000000"+orgNo+"0");
									}
									pedListsNew.add(pedpro);
									logger.info("客户["+pedpro.getCustNo()+"]的客户身份为["+pedpro.getCustIdentity()+"],pedListsNew集合元素有["+pedListsNew.size()+"]个");
								}
								if(type.equals(PoolComm.KHLX_02) || type.equals(PoolComm.KHLX_03)){
									if(pedpro == null){
										pedpro = new PedProtocolList();
										pedpro.setBpsNo(PJCBH);
										pedpro.setBpsName(dto.getPoolAccountName());
										pedpro.setCustNo(coreNo);
										pedpro.setCustName(coreNoName);
										pedpro.setOrgCoge(orgNo);
										pedpro.setStatus(PoolComm.PRO_LISE_STA_00);
										pedpro.setEditTime(DateUtils.getCurrDate());
										pedpro.setMaxFinancLimit(BigDecimalUtils.setScale(maxEdu));
										pedpro.setFinancLimit(BigDecimalUtils.setScale(maxEdu));//融资限额
										pedpro.setCustIdentity(PoolComm.KHLX_02);//融资人
										pedpro.setFinancingStatus("SF_01");//融资人身份生效
										pedpro.setRole("02");
										if(socialCode3 != null && !socialCode3.trim().equals("")){
											pedpro.setSocialCode(socialCode3);
										}else {
											pedpro.setSocialCode("00000000"+orgNo+"0");
										}
									}else {
										pedpro.setMaxFinancLimit(BigDecimalUtils.setScale(maxEdu));
										pedpro.setFinancLimit(BigDecimalUtils.setScale(maxEdu));//融资限额
										pedpro.setCustIdentity(type);
										pedpro.setFinancingStatus("SF_01");//融资身份生效
										if(socialCode3 != null && !socialCode3.trim().equals("")){
											pedpro.setSocialCode(socialCode3);
										}else {
											pedpro.setSocialCode("00000000"+orgNo+"0");
										}
									}
									pedListsNew.add(pedpro);
									logger.info("客户["+pedpro.getCustNo()+"]的客户身份为["+pedpro.getCustIdentity()+"],pedListsNew集合元素有["+pedListsNew.size()+"]个");
								}
								
							}
							logger.info("处理推送List集合结束-----------------------------------------");
						}
						pedProtocolService.txStoreAll(pedListsNew);//将最新担保信息保存
						//查询所有融资人身份失效的客户  
						List FinaList = pedProtocolService.queryPedListByParam(PJCBH, null, null, null, "SF_00");
						logger.info("处理失效融资人集合开始-----------------------------------------");
						if (FinaList != null ){
							for (int i = 0; i < FinaList.size(); i++) {
								protocol = (PedProtocolList) FinaList.get(i);
								protocol.setFinancLimit(new BigDecimal(0));
								pedProtocolService.txStore(protocol);
								logger.info("客户["+protocol.getCustNo()+"]的客户身份为["+protocol.getCustIdentity()+"]");
							}
						}
						logger.info("处理失效融资人集合结束-----------------------------------------");
						pedProtocolService.txStore(dto);
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("担保合同信息推送成功！");
					}
				}
			}


		} catch (Exception e) {
			if(ret.getRET_CODE() == null){
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("票据池系统内部执行错误" );
				logger.info(e);
			}
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
		response.setRet(ret);
		
		if(ret.getRET_CODE().equals(Constants.TX_SUCCESS_CODE)){
			
			if(isMoodChange){//发生额度变更
				try {
					financialService.txRefreshFinancial(dto,null);
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
			/*
			 * 协议信息及集团协议子户信息备份
			 */
			if(StringUtil.isNotBlank(PJCBH)){				
				pedProtocolService.txProtocolAndListHist(PJCBH);
			}
		}
		return response;
	}

	public PedProtocolService getPedProtocolService() {
		return pedProtocolService;
	}

	public void setPedProtocolService(PedProtocolService pedProtocolService) {
		this.pedProtocolService = pedProtocolService;
	}


	
	
	
}
