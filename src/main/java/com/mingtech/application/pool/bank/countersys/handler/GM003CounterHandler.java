package com.mingtech.application.pool.bank.countersys.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.FileName;
import com.mingtech.application.pool.common.PoolBatchNoUtils;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.common.domain.PoolBillInfoDto;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.vtrust.domain.PoolVtrust;
import com.mingtech.application.pool.vtrust.domain.PoolVtrustBeanQuery;
import com.mingtech.application.pool.vtrust.service.PoolVtrustService;
import com.mingtech.framework.common.util.BeanUtil;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 风险名单及额度校验接口（柜面）
 * @author wu fengjun
 * @data 2019-06-10
 */
public class GM003CounterHandler  extends PJCHandlerAdapter{

	private static final Logger logger = Logger.getLogger(GM003CounterHandler.class);
	
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private BlackListManageService blackListManageService;
	@Autowired
	private PoolVtrustService poolVtrustService;
	@Autowired
	private PoolBatchNoUtils poolBatchNoUtils;
	@Autowired
	private DraftPoolQueryService draftPoolQueryService;

	
	/**
	 * 1、通过核心客户号，票据池编号查询协议（）
	 * 2、校验是否开通票据池
	 * 3、循环柜面发送的票号,校验黑名单（若有黑名单 直接报错）
	 * 4、商票是否可占保贴额度产生高风险额度
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		
		response = this.toCheck(code, request);
		
		return response;

	}
	
	/**
	 * 校验逻辑
	 * @param code
	 * @param request
	 * @return
	 * @author Ju Nana
	 * @date 2021-10-27下午7:54:45
	 */
	private ReturnMessageNew toCheck(String code, ReturnMessageNew request){
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List resuList = new ArrayList();//存放返回给柜面的数据集合       
		Map resuMap =  null;
		String inBatchNo = "";
		
		Map<String,PoolBillInfo> billMap = new HashMap<String,PoolBillInfo>();
		
		
		try {
			String clientNO = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String bpsNO = getStringVal(request.getBody().get("BPS_NO"));
			inBatchNo = poolBatchNoUtils.txGetBatchNo("IN",6);
			List details = request.getDetails();
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNO, null, null, null);
			if(dto != null && dto.getOpenFlag().equals(PoolComm.OPEN_01) && dto.getContractDueDt() != null && DateUtils.compareDate(DateUtils.getCurrDate(),dto.getContractDueDt())<0 ){//开通票据池
				//担保合同生效才可入池
				
				ProListQueryBean queryBean = new ProListQueryBean();
				queryBean.setBpsNo(bpsNO);
				queryBean.setCustNo(clientNO);
				PedProtocolList proMem =  pedProtocolService.queryProtocolListByQueryBean(queryBean);
				if( (dto.getIsGroup().equals("0")) ||
						(dto.getIsGroup().equals("1") && proMem!=null && (PoolComm.KHLX_01.equals(proMem.getCustIdentity()) || PoolComm.KHLX_03.equals(proMem.getCustIdentity())))){
					if(details!=null &&  details.size()>0){	
						
						List<PoolBillInfo>  billInfoList = new ArrayList<PoolBillInfo>();
						List<PoolBillInfo>  zeroEduList = new ArrayList<PoolBillInfo>();
						
						Map<String,PoolBillInfoDto> infoMap = new HashMap<String, PoolBillInfoDto>();
						for (int i = 0; i < details.size(); i++) {
							resuMap = new HashMap();
							Map map = (Map) details.get(i);
							String billNo = getStringVal(map.get("BILL_NO"));
							
							PoolBillInfo billInfo = draftPoolQueryService.queryObj(billNo,"0","0");
							
							if(billInfo == null){
								billInfo = new PoolBillInfo();
								billInfo.setBillIfExist(PoolComm.YES);//这里赋值没有任何意义，只为了标记后续删除
							}else{
								PoolBillInfoDto poolDto = new PoolBillInfoDto();
								BeanUtil.copyValue(billInfo, poolDto);
								infoMap.put(billInfo.getSBillNo(), poolDto);
							}
							
							billInfo.setSBillNo(billNo);//票号
							billInfo.setSBillType(getStringVal(map.get("BILL_CLASS")));//票据类型
							billInfo.setSAcceptor(getStringVal(map.get("ACCEPTOR_NAME")));//承兑人名称
							billInfo.setSAcceptorBankCode(getStringVal(map.get("ACCEPTOR_OPENBANK_NAME")));//承兑人开户行行号
							//承兑行总行
							String acptBankNo = billInfo.getSAcceptorBankCode();
							Map cpes = blackListManageService.queryCpesMember(acptBankNo);
							if(cpes != null){
								billInfo.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
								String transBrchClass = (String) cpes.get("transBrchClass");
								String memberName = (String) cpes.get("memberName");//总行行名
								billInfo.setAcptHeadBankName(memberName);//总行行名
								if(transBrchClass.equals("301")){//财务公司行号
									billInfo.setCpFlag("1");
								}
							}
							
							billInfo.setSAcceptorAccount("1");//承兑人帐号  柜面没有,默认赋值让
							billInfo.setSIssuerName(getStringVal(map.get("BILL_NAME")));//出票人名称
							billInfo.setDDueDt(getDateVal(map.get("EXPIRY_DATE")));//到期日
							billInfo.setSBanEndrsmtFlag(PoolComm.NOT_ATTRON_FLAG_NO);//不得转让标识
							billInfo.setFBillAmount(getBigDecimalVal(map.get("BILL_AMT")));//票面金额
							billInfo.setSBillMedia("1");//纸票
							
							
							//财票改造开关
							String olOpenCp = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.NEW_RISK_CHECK);//在线业务总开关 
							
							if(olOpenCp != null && PoolComm.YES.equalsIgnoreCase(olOpenCp)){			
								billInfo = blackListManageService.txBlacklistCheck(billInfo, clientNO);
							}else{
								billInfo = blackListManageService.txBlacklistAndRiskCheck(billInfo, clientNO);
							}
							
							if(!PoolComm.BLACK.equals(billInfo.getBlackFlag())&&!PoolComm.NOTIN_RISK.equals(billInfo.getRickLevel())){//黑名单票据与非名单票据不再到MIS系统查询								
								billInfoList.add(billInfo);
							}else{
								zeroEduList.add(billInfo);
							}
							
							//存放票据映射关系
							billMap.put(billInfo.getSBillNo(), billInfo);
							
						}
						
						/*
						 * 向信贷系统进行额度校验
						 */
						billInfoList = blackListManageService.txMisCreditCheck(billInfoList);
						
						List<PoolBillInfo>  allList = new ArrayList<PoolBillInfo>();
						if(null !=billInfoList && billInfoList.size()>0){			
							allList.addAll(billInfoList);
						}
						if(null !=zeroEduList && zeroEduList.size()>0){							
							allList.addAll(zeroEduList);
						}
						
						
						for(PoolBillInfo bill : allList){
							
							resuMap = new HashMap();
							
							/*
							 * 将校验结果存入虚拟票据表
							 */
							String billNo = bill.getSBillNo();
							
							PoolVtrustBeanQuery bean = new PoolVtrustBeanQuery();
							bean.setVtNb(billNo);
							PoolVtrust vt  = poolVtrustService.queryPoolVtrust(bean);
							
							logger.info("接收到的返回数据：票号："+bill.getSBillNo()+"该票名单类型为："+bill.getBlackFlag()+"-----------");
							PoolBillInfo bil = billMap.get(billNo);

							if(vt==null){
								
								logger.info("虚拟表为null!");
								PoolVtrust pv = new PoolVtrust();
								pv.setVtNb(billNo);
								pv.setBlackFlag(bil.getBlackFlag());
								pv.setRickLevel(bil.getRickLevel());
								pv.setInBatchNo(inBatchNo);
								logger.info("虚拟表的风险等级:"+pv.getRickLevel()+"大票表风险等级为:"+bil.getRickLevel());
								poolVtrustService.txStore(pv);
							}else{
								logger.info("虚拟表不为null!");
								vt.setBlackFlag(bil.getBlackFlag());
								vt.setRickLevel(bil.getRickLevel());
								vt.setInBatchNo(inBatchNo);
								logger.info("虚拟表的风险等级:"+vt.getRickLevel()+"大票表风险等级为:"+bil.getRickLevel());
								poolVtrustService.txStore(vt);
							}
							
							if(bill.getBlackFlag().equals(PoolComm.BLACK)){//黑名单
								resuMap.put("IS_PRODUCE_MONEY", null);
								resuMap.put("INPOOL_FLAG", 0);
								resuMap.put("RISK_LEVEL", null);
							}else {
								resuMap.put("IS_PRODUCE_MONEY", null);
								resuMap.put("INPOOL_FLAG", 1);
								resuMap.put("RISK_LEVEL", null);
							}
							
							resuMap.put("BILL_NO", billNo);
							resuList.add(resuMap);
							
							
							//删掉不应该落到大票表的数据
							if(PoolComm.YES.equals(bil.getBillIfExist())){								
								PoolBillInfo delBill = draftPoolQueryService.queryObj(billNo,"0","0");
								if(null != delBill){									
									poolVtrustService.txDelete(delBill);
								}
							}
							
							
							/*
							 * 大票表数据回滚：解决hibernate缓存问题导致的问题
							 */
							PoolBillInfoDto poolDto = infoMap.get(billNo);							
							if(null != poolDto){
								PoolBillInfo billInfo = draftPoolQueryService.queryObj(billNo,"0","0");
								BeanUtil.copyValue(poolDto, billInfo);
								draftPoolQueryService.txStore(billInfo);
							}
							
						}
						
						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("交易成功！");
						
						if (resuList != null && resuList.size() > 0 ){
							String path = FileName.getFileNameClient(request.getTxCode())+".txt";
							response.getFileHead().put("FILE_FLAG", "2");
							response.getFileHead().put("FILE_PATH", path);
							response.getBody().put("INPOOL_BATCH_NO", inBatchNo);

						}else {
							response.getFileHead().put("FILE_FLAG", "0");
						}
						response.setDetails(resuList);
						response.setRet(ret);		
						return response;
						
					}else{
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("无传入票据信息!");
						response.setRet(ret);
						return response;
					}


				}else{
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("该客户非该票据池出质人！");
					response.setRet(ret);
					return response;
				}
				
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("未查到到该票据池信息,或担保合同未生效！");
				response.setRet(ret);
				return response;
			}
			
		}catch(Exception e){
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池系统校验异常[" + e.getMessage() + "]");
			response.setRet(ret);
			return response;
		}
		
	}
	
	/**
	 * 创建校验对象
	 * @param billNo
	 * @param map
	 * @return
	 * @throws Exception
	 * @author Ju Nana
	 * @date 2021-10-27下午7:29:42
	 */
	private PoolBillInfo toCreatCheckBill(String billNo , Map map) throws Exception{
		
		PoolBillInfo billInfo = draftPoolQueryService.queryObj(billNo,"0","0");
		
		if(billInfo == null){
			billInfo = new PoolBillInfo();
			billInfo.setBillIfExist(PoolComm.YES);//这里赋值没有任何意义，只为了标记后续删除
		}
		
		billInfo.setSBillNo(billNo);//票号
		billInfo.setSBillType(getStringVal(map.get("BILL_CLASS")));//票据类型
		billInfo.setSAcceptor(getStringVal(map.get("ACCEPTOR_NAME")));//承兑人名称
		billInfo.setSAcceptorBankCode(getStringVal(map.get("ACCEPTOR_OPENBANK_NAME")));//承兑人开户行行号
		//承兑行总行
		String acptBankNo = billInfo.getSAcceptorBankCode();
		Map cpes = blackListManageService.queryCpesMember(acptBankNo);
		if(cpes != null){
			billInfo.setAcptHeadBankNo((String)cpes.get("totalBankNo"));//总行行号
			String transBrchClass = (String) cpes.get("transBrchClass");
			String memberName = (String) cpes.get("memberName");//总行行名
			billInfo.setAcptHeadBankName(memberName);//总行行名
			if(transBrchClass.equals("301")){//财务公司行号
				billInfo.setCpFlag("1");
			}
		}
		
		billInfo.setSAcceptorAccount("1");//承兑人帐号  柜面没有,默认赋值让
		billInfo.setSIssuerName(getStringVal(map.get("BILL_NAME")));//出票人名称
		billInfo.setDDueDt(getDateVal(map.get("EXPIRY_DATE")));//到期日
		billInfo.setSBanEndrsmtFlag(PoolComm.NOT_ATTRON_FLAG_NO);//不得转让标识
		billInfo.setFBillAmount(getBigDecimalVal(map.get("BILL_AMT")));//票面金额
		billInfo.setSBillMedia("1");//纸票
		
		return billInfo;
	}
	
	
}
