package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetType;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedAssetTypeService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.framework.core.page.Page;

/**
 * @Title: 网银接口PJC039-集团成员融资信息查询
 * @author wu fengjun
 * @date 2019-6-24
 */
public class PJC039RequestHandler extends PJCHandlerAdapter{
	private static final Logger logger = Logger.getLogger(PJC039RequestHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PedAssetTypeService pedAssetTypeService;
	
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception {
		
		logger.info("pjc039-集团成员融资信息查询开始......");
		
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		List<Map> list = new ArrayList<Map>();
		Page page = getPage(request.getAppHead());
		try {
			String custNo = getStringVal(body.get("CORE_CLIENT_NO"));
			String bpsNo = getStringVal(body.get("BPS_NO"));
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(PoolComm.OPEN_01, null, bpsNo, null, null, null);
			
			if (dto==null) {
				ret.setRET_CODE(Constants.CREDIT_01);
				ret.setRET_MSG(custNo + ":该客户未开通融资票据池业务！");
			} else {
				
				ProListQueryBean queryBean = new ProListQueryBean();
				queryBean.setBpsNo(bpsNo);
				queryBean.setCustNo(custNo);
				queryBean.setStatus(PoolComm.PRO_LISE_STA_01);
				//单条信息
				PedProtocolList pedProtocolList = pedProtocolService.queryProtocolListByQueryBean(queryBean);
								
				if(pedProtocolList == null){
					ret.setRET_CODE(Constants.CREDIT_01);
					ret.setRET_MSG("【"+dto.getPoolName()+"】集团中无【"+custNo + "】客户信息！");
				}else{
					if(pedProtocolList.getRole().equals(PoolComm.JS_01)){//发生查询的客户类型为：主户
						
						//得到集团下所有成员信息
						ProListQueryBean bean = new ProListQueryBean();
						bean.setBpsNo(bpsNo);
						List<String> strs = new ArrayList<String>();
						strs.add(PoolComm.KHLX_01);
						strs.add(PoolComm.KHLX_02);
						strs.add(PoolComm.KHLX_03);
						bean.setCustIdentityList(strs);
						List<PedProtocolList> proLists  = pedProtocolService.queryProListByQueryBean(bean,page);

						if(proLists ==  null) {
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("该票据池未推担保合同，不支持融资信息查询!");
						}else {
							for (PedProtocolList proMem:proLists) {

								if(PoolComm.JS_01.equals(proMem.getRole())){//主户信息
									Map map = new HashMap();
									map = retuCoreRoleMap(dto,pedProtocolList);
									list.add(map);
								}else{
									Map map = new HashMap();

									if(PoolComm.KHLX_01.equals(proMem.getCustIdentity())){//出质人（出质人一定有出质信息，但是有可能有融资信息【双重身份客户融资解约导致】）
										map = this.retuKHLX_01(proMem);
										list.add(map);
									}else if(PoolComm.KHLX_02.equals(proMem.getCustIdentity())){//融资人（只有融资信息）
										map = this.retuKHLX_02(proMem);
										list.add(map);
									}else if(PoolComm.KHLX_03.equals(proMem.getCustIdentity())){//出质人+融资人（出质信息+融资信息）
										map = this.retuKHLX_03(proMem);
										list.add(map);
									}else if(PoolComm.KHLX_04.equals(proMem.getCustIdentity())){//签约客户（可能有融资信息【融资人解约导致】）
										map = this.retuKHLX_04(proMem);
										list.add(map);
									}
								}

							}
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("集团成员融资信息查询成功!");

						}
					}else {//发生查询的客户类型为：分户
						
						if(pedProtocolList.getCustIdentity().equals(PoolComm.KHLX_01)){//出质人（）
							Map map = new HashMap();
							map = this.retuKHLX_01(pedProtocolList);
							list.add(map);
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("集团成员融资信息查询成功!");
						}
						if(pedProtocolList.getCustIdentity().equals(PoolComm.KHLX_03)){//出质人加融资人
							Map map = new HashMap();
							map = this.retuKHLX_03(pedProtocolList);
							list.add(map);
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("集团成员融资信息查询成功!");
						}
						if(pedProtocolList.getCustIdentity().equals(PoolComm.KHLX_02)){//融资人
							Map map = new HashMap();
							map = this.retuKHLX_02(pedProtocolList);
							list.add(map);
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("集团成员融资信息查询成功!");
						}
						if(pedProtocolList.getCustIdentity().equals(PoolComm.KHLX_04)){//签约成员
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("客户["+custNo+"]为签约成员,非出质人也非融资人,无出质及融资信息!");
						}
					}
					
				}
				
			}
		} catch (Exception e) {
			logger.error("网银接口PJC039-集团成员融资信息查询异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("集团成员融资信息查询异常! 票据池内部执行错误");
		}
		setPage(response.getAppHead(), page);
		response.setRet(ret);
		response.setDetails(list);
		
		logger.info("pjc039-集团成员融资信息查询结束......");
		
		return response;
	}
	
	/**
	 * 当主户查询的时候返回的主户信息
	 * @Description TODO
	 * @author Ju Nana
	 * @param protocol
	 * @param protocolList
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午5:09:58
	 */
	public Map retuCoreRoleMap(PedProtocolDto protocol, PedProtocolList protocolList) throws Exception{
		Map map = new HashMap();
		//查询池总额度,高低风险额度
		AssetType at = pedAssetTypeService.queryPedAssetTypeByProtocol(protocol, PoolComm.ED_BZJ_HQ);
		EduResult eduResult =pedAssetPoolService.queryEduMember(protocolList.getBpsNo(), protocolList.getCustNo());
		BigDecimal total = eduResult.getTotalBillAmount().add(at.getCrdtTotal());
		BigDecimal lowRisk = eduResult.getLowRiskAmount().add(at.getCrdtTotal());
		map.put("FINANCING_INFO_ARRAY.CORE_CLIENT_NO", protocolList.getCustNo());
		map.put("FINANCING_INFO_ARRAY.CLIENT_NAME", protocolList.getCustName());
		map.put("FINANCING_INFO_ARRAY.POOL_AMT", total);//票据池总额度
		map.put("FINANCING_INFO_ARRAY.LOW_RISK_TOTAL_AMT", lowRisk);//低风险总额度
		map.put("FINANCING_INFO_ARRAY.HIGH_RISK_TOTAL_AMT", eduResult.getHighRiskAmount());//高风险票据总额度
		BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
		
		String custIdentity = protocolList.getCustIdentity();//客户类型
		
		if(PoolComm.KHLX_01.equals(custIdentity) && allUsed.compareTo(BigDecimal.ZERO)>0){
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_MAX_LIMIT_AMT", "0");//分配额度限额
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_LIMIT_AMT", "0");//分配额度
			map.put("FINANCING_INFO_ARRAY.USED_LIMIT_AMT", allUsed);//已使用额度
			map.put("FINANCING_INFO_ARRAY.REMAIN_AVAIL_LIMIT_AMT","0");//剩余可用额度			
		}
		if(PoolComm.KHLX_03.equals(custIdentity)){
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_MAX_LIMIT_AMT", protocolList.getMaxFinancLimit());//分配额度限额
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_LIMIT_AMT", protocolList.getFinancLimit());//分配额度
			map.put("FINANCING_INFO_ARRAY.USED_LIMIT_AMT", allUsed);//已使用额度
			map.put("FINANCING_INFO_ARRAY.REMAIN_AVAIL_LIMIT_AMT", protocolList.getFinancLimit().subtract(allUsed));//剩余可用额度			
		}
		map.put("FINANCING_INFO_ARRAY.FINANCING_CLIENT_TYPE", custIdentity);//客户类型
		
			
		
		return map;
	}
	
	
	/**
	 * 分户信息返回--出质人身份
	 * @Description 出质人一定有出质信息，但是有可能有融资信息【双重身份客户融资解约导致】
	 * @author Ju Nana
	 * @param protocolList
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午2:51:00
	 */
	public Map retuKHLX_01(PedProtocolList protocolList) throws Exception{
		Map map = new HashMap();
		EduResult eduResult =pedAssetPoolService.queryEduMember(protocolList.getBpsNo(), protocolList.getCustNo());
		
		map.put("FINANCING_INFO_ARRAY.CORE_CLIENT_NO", protocolList.getCustNo());
		map.put("FINANCING_INFO_ARRAY.CLIENT_NAME", protocolList.getCustName());
		map.put("FINANCING_INFO_ARRAY.POOL_AMT", eduResult.getTotalBillAmount());//票据池票据总额度
		map.put("FINANCING_INFO_ARRAY.LOW_RISK_TOTAL_AMT", eduResult.getLowRiskAmount());//低风险票据总额度
		map.put("FINANCING_INFO_ARRAY.HIGH_RISK_TOTAL_AMT", eduResult.getHighRiskAmount());//高风险票据总额度
		BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
		if(allUsed.compareTo(BigDecimal.ZERO)>0){
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_MAX_LIMIT_AMT", "0");//分配额度限额
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_LIMIT_AMT", "0");//分配额度
			map.put("FINANCING_INFO_ARRAY.USED_LIMIT_AMT", allUsed);//已使用额度
			map.put("FINANCING_INFO_ARRAY.REMAIN_AVAIL_LIMIT_AMT","0");//剩余可用额度			
		}
		map.put("FINANCING_INFO_ARRAY.FINANCING_CLIENT_TYPE", protocolList.getCustIdentity());//客户类型
		
		return map;
		
	}
	
	/**
	 * 分户信息返回--融资人身份
	 * @Description 只有融资信息
	 * @author Ju Nana
	 * @param protocolList
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午2:51:44
	 */
	public Map retuKHLX_02(PedProtocolList protocolList) throws Exception{
		Map map = new HashMap();
		EduResult eduResult =pedAssetPoolService.queryEduMember(protocolList.getBpsNo(), protocolList.getCustNo());
		
		map.put("FINANCING_INFO_ARRAY.CORE_CLIENT_NO", protocolList.getCustNo());
		map.put("FINANCING_INFO_ARRAY.CLIENT_NAME", protocolList.getCustName());
		map.put("FINANCING_INFO_ARRAY.ALLOCATION_MAX_LIMIT_AMT", protocolList.getMaxFinancLimit());//分配额度限额
		map.put("FINANCING_INFO_ARRAY.ALLOCATION_LIMIT_AMT", protocolList.getFinancLimit());//分配额度
		BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
		map.put("FINANCING_INFO_ARRAY.USED_LIMIT_AMT", allUsed);//已使用额度
		map.put("FINANCING_INFO_ARRAY.REMAIN_AVAIL_LIMIT_AMT", protocolList.getFinancLimit().subtract(allUsed));//剩余可用额度
		map.put("FINANCING_INFO_ARRAY.FINANCING_CLIENT_TYPE", protocolList.getCustIdentity());//客户类型
		
		return map;
		
	}
	
	/**
	 * 分户信息返回--出质人+融资人身份
	 * @Description 出质信息+融资信息
	 * @author Ju Nana
	 * @param protocolList
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午2:51:58
	 */
	public Map retuKHLX_03(PedProtocolList protocolList) throws Exception{
		Map map = new HashMap();
		EduResult eduResult =pedAssetPoolService.queryEduMember(protocolList.getBpsNo(), protocolList.getCustNo());
		
		map.put("FINANCING_INFO_ARRAY.CORE_CLIENT_NO", protocolList.getCustNo());
		map.put("FINANCING_INFO_ARRAY.CLIENT_NAME", protocolList.getCustName());
		map.put("FINANCING_INFO_ARRAY.POOL_AMT", eduResult.getTotalBillAmount());//票据池票据总额度
		map.put("FINANCING_INFO_ARRAY.LOW_RISK_TOTAL_AMT", eduResult.getLowRiskAmount());//低风险票据总额度
		map.put("FINANCING_INFO_ARRAY.HIGH_RISK_TOTAL_AMT", eduResult.getHighRiskAmount());//高风险票据总额度
		map.put("FINANCING_INFO_ARRAY.ALLOCATION_MAX_LIMIT_AMT", protocolList.getMaxFinancLimit());//分配额度限额
		map.put("FINANCING_INFO_ARRAY.ALLOCATION_LIMIT_AMT", protocolList.getFinancLimit());//分配额度
		BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
		map.put("FINANCING_INFO_ARRAY.USED_LIMIT_AMT", allUsed);//已使用额度
		map.put("FINANCING_INFO_ARRAY.REMAIN_AVAIL_LIMIT_AMT", protocolList.getFinancLimit().subtract(allUsed));//剩余可用额度
		map.put("FINANCING_INFO_ARRAY.FINANCING_CLIENT_TYPE", protocolList.getCustIdentity());//客户类型
		
		return map;
		
	}
	/**
	 * 分户信息返回--签约人信息
	 * @Description 可能有融资信息【融资人解约导致】
	 * @author Ju Nana
	 * @param protocolList
	 * @return
	 * @throws Exception
	 * @date 2019-6-29下午5:02:06
	 */
	public Map retuKHLX_04(PedProtocolList protocolList) throws Exception{
		Map map = new HashMap();
		EduResult eduResult =pedAssetPoolService.queryEduMember(protocolList.getBpsNo(), protocolList.getCustNo());
		
		map.put("FINANCING_INFO_ARRAY.CORE_CLIENT_NO", protocolList.getCustNo());
		map.put("FINANCING_INFO_ARRAY.CLIENT_NAME", protocolList.getCustName());
		BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
		if(allUsed.compareTo(BigDecimal.ZERO)>0){
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_MAX_LIMIT_AMT", "0");//分配额度限额
			map.put("FINANCING_INFO_ARRAY.ALLOCATION_LIMIT_AMT", "0");//分配额度
			map.put("FINANCING_INFO_ARRAY.USED_LIMIT_AMT", allUsed);//已使用额度
			map.put("FINANCING_INFO_ARRAY.REMAIN_AVAIL_LIMIT_AMT", "0");//剩余可用额度			
		}
		map.put("FINANCING_INFO_ARRAY.FINANCING_CLIENT_TYPE", protocolList.getCustIdentity());//客户类型
		
		return map;
		
	}

}
