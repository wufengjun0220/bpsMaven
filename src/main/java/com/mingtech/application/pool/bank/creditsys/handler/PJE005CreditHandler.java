package com.mingtech.application.pool.bank.creditsys.handler;

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
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;

/**
 * @author zhaoding
 * 
 * @描述：票据池签约信息查询
 */
public class PJE005CreditHandler extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJE005CreditHandler.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingtech.application.interface_scls.banksys.creditsys.server.
	 * MinaCreditServer#txHandleRequest(java.lang.String,
	 * com.mingtech.application.interface_scls.message.ReturnMessage)
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List<Map> details = new ArrayList<Map>();
		Map detailMap = null;
		PedProtocolList Pedprotocol = null;
		try {
			Map map = request.getBody();
			String PJCBH = getStringVal(map.get("BPS_SEQ_NO"));
			// 通过票据池编号查询得到协议实体
			List<PedProtocolDto> protocol =  pedProtocolService.queryProtocolInfo(PoolComm.OPEN_01, null, PJCBH, null, null, null);
			if (protocol != null && protocol.size() > 0) {
				PedProtocolDto dto = (PedProtocolDto) protocol.get(0);
				if (dto != null && dto.getOpenFlag().equals(PoolComm.OPEN_01)) {
					response.getBody().put("BPS_SEQ_NO", dto.getPoolAgreement());// 票据池编号
					response.getBody().put("BPS_NAME", dto.getPoolName());// 票据池名称
					response.getBody().put("BPS_TYPE", dto.getContractType());// 票据池类型
					if (dto.getIsGroup().equals("0")) {// 非集团
						detailMap = new HashMap();
						response.getBody().put("BPS_TYPE", "01");// 票据池类型--单户
						detailMap.put("CLIENT_ARRAY.CLIENT_NO", dto.getCustnumber());
						detailMap.put("CLIENT_ARRAY.ORG_CODE", dto.getCustOrgcode());
						detailMap.put("CLIENT_ARRAY.CLIENT_NAME", dto.getCustname());
						detailMap.put("CLIENT_ARRAY.CLIENT_TYPE", PoolComm.KHLX_03);//出质人加融资人
						details.add(detailMap);
					} else {
						response.getBody().put("BPS_TYPE", "03");// 票据池类型--集团
						ProListQueryBean bean = new ProListQueryBean();
						bean.setBpsNo(PJCBH);
						bean.setStatus(PoolComm.PRO_LISE_STA_01);  //返回全部网银已签约的全部成员        20190924林丛林要求
						List<PedProtocolList> list = pedProtocolService.queryProListByQueryBean(bean);
						
						for (int i = 0; i < list.size(); i++) {
							detailMap = new HashMap();
							Pedprotocol = (PedProtocolList) list.get(i);
							detailMap.put("CLIENT_ARRAY.CLIENT_NO", Pedprotocol.getCustNo());
							detailMap.put("CLIENT_ARRAY.ORG_CODE", Pedprotocol.getOrgCoge());
							detailMap.put("CLIENT_ARRAY.CLIENT_NAME", Pedprotocol.getCustName());
							detailMap.put("CLIENT_ARRAY.CLIENT_TYPE", PoolComm.KHLX_04);//只返回签约成员    20190924林丛林要求
							detailMap.put("CLIENT_ARRAY.FINANCING_MAX_AMT", Pedprotocol.getMaxFinancLimit());//
							detailMap.put("CLIENT_ARRAY.ROLE_TYPE", Pedprotocol.getRole());//
							details.add(detailMap);
						}
					}

					response.getBody().put("BPS_STATUS", dto.getOpenFlag());// 票据池状态
					response.getBody().put("FROZEN_STATUS", dto.getFrozenstate());// 冻结状态
					
//					detailMap.put("CLIENT_ARRAY.CLIENT_NO", dto.getCustnumber());
//					detailMap.put("CLIENT_ARRAY.ORG_CODE", dto.getCustOrgcode());
//					detailMap.put("CLIENT_ARRAY.CLIENT_NAME", dto.getCustname());
					
//					for (int i = 0; i < protocol.size(); i++) {
//						detailMap = new HashMap();
//						PedProtocolDto pro = (PedProtocolDto) protocol.get(i);
//						detailMap.put("CLIENT_ARRAY.CLIENT_NO", pro.getCustnumber());
//						detailMap.put("CLIENT_ARRAY.ORG_CODE", pro.getCustOrgcode());
//						detailMap.put("CLIENT_ARRAY.CLIENT_NAME", pro.getCustname());
//						detailMap.put("CLIENT_ARRAY.CLIENT_TYPE", "");
//						details.add(detailMap);
//					}
					response.setDetails(details);

					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("查询成功!");
				} else {
					ret.setRET_CODE(Constants.CREDIT_05);
					ret.setRET_MSG(PJCBH + "该票据池信息不存在！");
				}
			} else {
				ret.setRET_CODE(Constants.CREDIT_05);
				ret.setRET_MSG(PJCBH + "该票据池信息不存在！");
			}

		} catch (Exception e) {
			logger.error("操作失败!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("操作失败");
		}
		response.setRet(ret);

		return response;
	}

	public PedProtocolService getPedProtocolService() {
		return pedProtocolService;
	}

	public void setPedProtocolService(PedProtocolService pedProtocolService) {
		this.pedProtocolService = pedProtocolService;
	}

}
