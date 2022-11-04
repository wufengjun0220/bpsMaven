package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.EduResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;

/**
 * @Title: 网银接口PJC038-集团成员额度分配
 * @author xie cheng
 * @date 2019-05-30
 */
public class PJC038RequestHandler extends PJCHandlerAdapter{
	private static final Logger logger = Logger.getLogger(PJC038RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;
	@Autowired
	private PedProtocolService pedProtocolService;
	
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		PoolQueryBean queryBean= new PoolQueryBean();
		ProtocolQueryBean protocolQueryBean= new ProtocolQueryBean();
		List list = request.getDetails();
		List<PedProtocolList> Pedls = new LinkedList<PedProtocolList>();
		PedProtocolList pedProtocolList =null;
		try {
			queryBean.setProtocolNo(getStringVal(body.get("BPS_NO")));// 票据池编号
			protocolQueryBean.setPoolAgreement(getStringVal(body.get("BPS_NO")));// 票据池编号
			protocolQueryBean.setCustnumber(getStringVal(body.get("CORE_CLIENT_NO")));// 客户号
			PedProtocolDto protocolDto= pedProtocolService.queryProtocolDtoByQueryBean(protocolQueryBean);
			if(null!=protocolDto) {
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						Map map = null;
						map = (Map) list.get(i);
						queryBean.setCustomernumber(getStringVal(map.get("LIMIT_ARRAY.SUB_CORE_CLIENT_NO")));//核心客户号(成员)
						Pedls = poolEBankService.queryPedProtocolListParams(queryBean, null);

						BigDecimal FinancLimit = getBigDecimalVal(map.get("LIMIT_ARRAY.ALLOCATION_LIMIT_AMT"));//分配额度
						if (null != Pedls && Pedls.size() > 0) {
							pedProtocolList = Pedls.get(0);
							BigDecimal maxLimit = pedProtocolList.getMaxFinancLimit();
							if (!pedProtocolList.getCustIdentity().equals(PoolComm.KHLX_02)&&!pedProtocolList.getCustIdentity().equals(PoolComm.KHLX_03)) {
								//若分配额度的客户号不为融资人则报错
								throw new Exception("客户[" + pedProtocolList.getCustName() + "]不是融资人,不允许分配融资限额!");
							}
							if(maxLimit.compareTo(FinancLimit)<0){
								throw new Exception("分配金额不能大于最高融资限额，客户【"+pedProtocolList.getCustName()+"】的最高融资限额为【"+pedProtocolList.getMaxFinancLimit()+"】");
							}
							EduResult eduResult = pedAssetPoolService.queryEduMember(pedProtocolList.getBpsNo(), pedProtocolList.getCustNo());
							BigDecimal allUsed = eduResult.getUsedLowRiskAmount().add(eduResult.getUsedHighRiskAmount());
							if (FinancLimit.compareTo(allUsed) > 0 ) {
								pedProtocolList.setFinancLimit(FinancLimit);//分配额度
								pedProtocolList.setEditTime(new Date());//修改时间
								poolEBankService.txStore(pedProtocolList);
							} else {
								throw new Exception("客户[" + pedProtocolList.getCustName() + "]已使用额度大于分配额度,请重新修改");
							}
							
							if (FinancLimit.compareTo(pedProtocolList.getMaxFinancLimit()) <= 0) {
								pedProtocolList.setFinancLimit(FinancLimit);//分配额度
								pedProtocolList.setEditTime(new Date());//修改时间
								poolEBankService.txStore(pedProtocolList);
							} else {
								throw new Exception("客户[" + pedProtocolList.getCustName() + "]分配额度大于最高融资限额,请重新修改");
							}
							
							
						}
					}

					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("额度分配成功!");
					logger.info("网银接口PJC038-[" + getStringVal(body.get("BPS_NO")) + "]集团成员额度分配成功!");
				} else {
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("传入成员列表为空，集团成员额度分配失败!");
					logger.info("网银接口PJC038-[" + getStringVal(body.get("BPS_NO")) + "]集团成员额度分配失败!");
				}
			}else{
				ret.setRET_CODE(Constants.TX_FAIL_CODE);
				ret.setRET_MSG("额度分配请联系集团管理户处理!");
			}
		} catch (Exception e) {
			logger.error("网银接口PJC038-集团成员额度分配异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("集团成员额度分配异常! ["+ e.getMessage()+ "]");
		}
		response.setRet(ret);
		return response;
	}
}
