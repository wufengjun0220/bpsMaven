package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.assetmanage.domain.AssetRegister;
import com.mingtech.application.pool.assetmanage.service.AssetRegisterService;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.financial.service.FinancialService;

/**
 * 票据出池校验
 * @author Ju Nana
 * @version v1.0
 * @date 2021-8-12
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class PJC072RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC072RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;// 网银服务
	@Autowired
	private PedProtocolService pedProtocolService;// 协议服务
	@Autowired
	private FinancialService financialService;
	@Autowired
	private AssetRegisterService assetRegisterService ;

	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {
				
		ReturnMessageNew response = new ReturnMessageNew();
		
		//出池申请校验
		response = poolOutCheck(request);
		
		return response;
	}


	/**
	 * 票据出池校验
	 * @param request
	 * @return
	 * @author Ju Nana
	 * @date 2021-8-12下午5:40:19
	 */
	public ReturnMessageNew poolOutCheck(ReturnMessageNew request) {
		
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		List<String> billNos = new ArrayList<String>();
		
		try {
			String bpsNo = (String) request.getBody().get("BPS_NO");
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));//核心客户号
			
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			
			
			
			//数据列表校验
			List list = request.getDetails();		
			if (list == null || list.size() == 0){
				ret.setRET_CODE(Constants.EBK_04);
				ret.setRET_MSG("未获取到出池票据信息申请");
				response.setRet(ret);
				logger.info("未获取到出池票据信息！");
				return response;
			}
			
			//出池票号组装
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				String billNo = (String) map.get("BILL_INFO_ARRAY.BILL_NO");// 票号
				billNos.add(billNo);
			}
			
			
			//冻结校验
			if (dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_02)|| dto.getFrozenstate().equals(PoolComm.FROZEN_STATUS_03)) {
				ret.setRET_CODE(Constants.EBK_04);
				ret.setRET_MSG("该客户票据池的票据额度已冻结，无法发起出池申请！");
				response.setRet(ret);
				logger.info("票据池【"+dto.getPoolAgreement()+"】额度冻结，不允许出池！");
				return response;
				
			} 
			
			
			//集团成员身份校验

//			if(dto.getIsGroup().equals(PoolComm.YES)){
//				ProListQueryBean listBean = new ProListQueryBean();
//				listBean.setBpsNo(bpsNo);
//				listBean.setCustNo(custNo);
//				PedProtocolList mem = pedProtocolService.queryProtocolListByQueryBean(listBean);
//				if(!PoolComm.KHLX_01.equals(mem.getCustIdentity())&&!PoolComm.KHLX_03.equals(mem.getCustIdentity())){
//					ret.setRET_CODE(Constants.TX_FAIL_CODE);
//					ret.setRET_MSG("该客户:"+custNo+"身份不为出质人或融资人不允许操作");
//					response.setRet(ret);
//					return response;
//				}
//			}
			
	
			/*
			 * 获取【除去出池资产之后】的资产列表
			 */
			List<AssetRegister> arList = assetRegisterService.queryAssetRegisterExceptAssetNos(bpsNo,billNos);
			
			//额度校验
			Ret checkRet =  financialService.txAssetOutCheck(arList, dto);
			
			
			if (Constants.TX_SUCCESS_CODE.equals(checkRet.getRET_CODE())) {

				ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
				ret.setRET_MSG("资产出池校验通过！");
				
				
			} else {
				ret.setRET_CODE(Constants.EBK_05);
				ret.setRET_MSG(checkRet.getRET_MSG());
			}
			
			
		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("资产出池校验异常，请联系票据池系统！");
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

}
