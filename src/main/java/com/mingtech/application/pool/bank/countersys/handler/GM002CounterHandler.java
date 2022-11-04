package com.mingtech.application.pool.bank.countersys.handler;

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
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PlFeeScale;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 客户签约信息查询接口（柜面）
 * @author wu fengjun
 * @data 2019-06-10
 */
public class GM002CounterHandler  extends PJCHandlerAdapter{

	private static final Logger logger = Logger
	.getLogger(GM002CounterHandler.class);
	
	@Autowired
	private PedProtocolService pedProtocolService;
	
	/**
	 * 1、通过核心客户号，票据池编号查询协议（）
	 * 2、校验是否开通票据池
	 * 3、查询收费基础表,历史记录表
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			String bpsNO = getStringVal(request.getBody().get("BPS_NO"));
			Object custNumber = request.getBody().get("CORE_CLIENT_NO");
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null,bpsNO,  null, null, null);
			if(dto == null ) {
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("该票据池编号不存在!");
				response.setRet(ret);
				return response;
			}else {
				logger.info("客户号为:"+custNumber);
				if(custNumber == null || "".equals(custNumber)){
					logger.info("该协议为单户:");
					if (dto.getIsGroup().equals(PoolComm.NO)){
						logger.info("该协议为单户:");
						dto = pedProtocolService.queryProtocolDto(null, null,bpsNO,  null, null, null);

					}else {
						logger.info("该协议为集团户:");
						ProListQueryBean proBean = new ProListQueryBean();
						proBean.setBpsNo(bpsNO);
						PedProtocolList pro = pedProtocolService.queryProtocolListByQueryBean(proBean);
						if(pro == null ) {
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("票据池编号不存在!");
							response.setRet(ret);
							return response;
						}
					}

					ProListQueryBean queryBean = new ProListQueryBean();
					List<String> custIdentityList = new ArrayList<String>();
					custIdentityList.add(PoolComm.KHLX_01);
					custIdentityList.add(PoolComm.KHLX_03);
					queryBean.setCustIdentityList(custIdentityList);
					queryBean.setBpsNo(bpsNO);
					List<PedProtocolList> proMem = pedProtocolService.queryProListByQueryBean(queryBean);

					if(dto != null && dto.getOpenFlag().equals(PoolComm.OPEN_01)){//开通票据池
						response.getBody().put("BPS_NO", dto.getPoolAgreement());//票据池编号
						response.getBody().put("BPS_NAME", dto.getPoolName());//票据池名称
						response.getBody().put("DEDUCTION_ACCT_NO", dto.getPoolAccount());//结算账户
						response.getBody().put("DEDUCTION_ACCT_NAME", dto.getPoolAccountName());//结算账户名称
						List<Map> details = new ArrayList<Map>();
						if(dto.getIsGroup().equals("0")) {//单户
							Map map = new HashMap();
							map.put("CLIENT_ARRAY.CORE_CLIENT_NO", dto.getCustnumber());
							map.put("CLIENT_ARRAY.CLIENT_NAME", dto.getCustname());
							map.put("CLIENT_ARRAY.ORG_CODE", dto.getCustOrgcode());
							details.add(map);
						}else {
							if(proMem!=null && proMem.size()>0){
								for(PedProtocolList mem : proMem){
									Map map = new HashMap();
									map.put("CLIENT_ARRAY.CORE_CLIENT_NO", mem.getCustNo());
									map.put("CLIENT_ARRAY.CLIENT_NAME", mem.getCustName());
									map.put("CLIENT_ARRAY.ORG_CODE", mem.getOrgCoge());
									details.add(map);
								}
							}

						}
						response.setDetails(details);

						ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
						ret.setRET_MSG("查询成功！");
						response.setRet(ret);
						return response;

					}else{
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("该客户未开通票据池业务!");
						response.setRet(ret);
						return response;
					}
				}else {
					String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
					if (dto.getIsGroup().equals(PoolComm.NO)){
						//单户
						dto = pedProtocolService.queryProtocolDto(null, null,bpsNO,  custNo, null, null);
						if(dto == null ){
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("票据池编号与客户号不符!");
							response.setRet(ret);
							return response;
						}
					}else{
						ProListQueryBean proBean = new ProListQueryBean();
						proBean.setBpsNo(bpsNO);
						proBean.setCustNo(custNo);
						PedProtocolList pro = pedProtocolService.queryProtocolListByQueryBean(proBean);
						if(pro == null ) {
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("票据池编号与客户号不符!");
							response.setRet(ret);
							return response;
						}
						if(!pro.getCustIdentity().equals(PoolComm.KHLX_01) && !pro.getCustIdentity().equals(PoolComm.KHLX_03)){
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("该客户号不为出质人,不允许入池!");
							response.setRet(ret);
							return response;
						}
					}
					if (DateUtils.compareDate(DateUtils.getCurrDateTime(),dto.getContractDueDt()) > 0 ) {
						
						//合同到期日小于当前工作日   担保合同失效需推担保合同
						ret.setRET_CODE(Constants.TX_FAIL_CODE);
						ret.setRET_MSG("担保合同未生效,需推担保合同!");
						response.setRet(ret);
						return response;
					}else {

						ProListQueryBean queryBean = new ProListQueryBean();
						List<String> custIdentityList = new ArrayList<String>();
						custIdentityList.add(PoolComm.KHLX_01);
						custIdentityList.add(PoolComm.KHLX_03);
						queryBean.setCustIdentityList(custIdentityList);
						queryBean.setBpsNo(bpsNO);
						queryBean.setCustNo(custNo);
						List<PedProtocolList> proMem = pedProtocolService.queryProListByQueryBean(queryBean);

						if(dto != null && dto.getOpenFlag().equals(PoolComm.OPEN_01)){//开通票据池
							response.getBody().put("SIGN_BILL_ID", dto.getElecDraftAccount());//电票签约账号
							response.getBody().put("SIGN_BILL_NAME", dto.getElecDraftAccountName());//电票签约账号名称
							response.getBody().put("BPS_NO", dto.getPoolAgreement());//票据池编号
							response.getBody().put("BPS_NAME", dto.getPoolName());//票据池名称
							response.getBody().put("DEDUCTION_ACCT_NO", dto.getPoolAccount());//结算账户
							response.getBody().put("DEDUCTION_ACCT_NAME", dto.getPoolAccountName());//结算账户名称
							PlFeeScale scale = pedProtocolService.queryFeeScale();//票据池收费标准
							if (dto.getFeeType() != null && dto.getFeeType().equals(PoolComm.SFMS_01)) {//年费
								response.getBody().put("PRICE_STANDARD_DESC", scale.getEveryYear().setScale(2, BigDecimal.ROUND_HALF_UP));//
								response.getBody().put("FEE_MODE", "01");
								if(!pedProtocolService.isPaid(dto)){//年费未缴
									response.getBody().put("ANNUAL_FEE_PAYED_FLAG", "0");
								}else {
									response.getBody().put("ANNUAL_FEE_PAYED_FLAG", "1");
								}
							}else {
								//单笔
								response.getBody().put("PRICE_STANDARD_DESC", scale.getEveryPiece().setScale(2,BigDecimal.ROUND_HALF_UP));
								response.getBody().put("FEE_MODE", "02");
							}

							List<Map> details = new ArrayList<Map>();
							if(dto.getIsGroup().equals("0")) {//单户
								Map map = new HashMap();
								map.put("CLIENT_ARRAY.CORE_CLIENT_NO", dto.getCustnumber());
								map.put("CLIENT_ARRAY.CLIENT_NAME", dto.getCustname());
								map.put("CLIENT_ARRAY.ORG_CODE", dto.getCustOrgcode());
								details.add(map);
							}else {
								if(proMem!=null && proMem.size()>0){
									for(PedProtocolList mem : proMem){
										Map map = new HashMap();
										map.put("CLIENT_ARRAY.CORE_CLIENT_NO", mem.getCustNo());
										map.put("CLIENT_ARRAY.CLIENT_NAME", mem.getCustName());
										map.put("CLIENT_ARRAY.ORG_CODE", mem.getOrgCoge());
										details.add(map);
									}
								}

							}

							response.setDetails(details);
							ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
							ret.setRET_MSG("查询成功！");
							response.setRet(ret);
							return response;

						}else{
							ret.setRET_CODE(Constants.TX_FAIL_CODE);
							ret.setRET_MSG("该企业开通票据池业务，无法查询!");
							response.setRet(ret);
							return response;
						}

					}
				}
			}
		}catch(Exception e){
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询异常[" + e.getMessage() + "]");
			return response;
		}
		
		
	}
	
}
