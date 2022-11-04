package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.AssetPool;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedAssetPoolService;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.financial.service.FinancialService;

/**
 * 
 * @Title: 网银查询接口PJC022
 * @Description: 保证金管理-保证金余额查询接口
 * @author Ju Nana
 * @date 2018-10-24
 */
public class PJC022RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC022RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; //网银方法类
	@Autowired
	private FinancialService financialService ;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PedAssetPoolService pedAssetPoolService;

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request)
			throws Exception {
		
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String poolAgreement = getStringVal(request.getBody().get("BPS_NO"));//协议编号

			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setPoolAgreement(poolAgreement);
			queryBean.setCustnumber(custNo);
			queryBean.setOpenFlag(PoolComm.OPEN_01);//签约融资票据池功能
			PedProtocolDto protocolDto = pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			
			if (null!=protocolDto) {
				
				//锁AssetPool表
				AssetPool pool = pedAssetPoolService.queryPedAssetPoolByProtocol(protocolDto);
				String apId = pool.getApId();
				boolean isLockedSucss =  pedAssetPoolService.txGetAssetPoolTransAuthority(apId);
				if(!isLockedSucss){//加锁失败
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG("票据池其他额度相关任务正在处理中，请稍后再试！");
					response.setRet(ret);
					return response;
				}
				
				//核心同步保证金并重新计算池额度信息
				BailDetail bail = null;
				try {
					bail = financialService.txBailChangeAndCrdtCalculation(protocolDto);					
				} catch (Exception e) {
					ret.setRET_CODE(Constants.TX_FAIL_CODE);
					ret.setRET_MSG(e.getMessage());
					response.setRet(ret);
					return response;
				}
				
				
				//解锁AssetPool表，并重新计算该表数据
				pedAssetPoolService.txReleaseAssetPoolTransAuthoritySyncCredit(apId,true);

				// 然后查询同步之后的资产数据
				Map map = poolEBankService.queryMarginBalance(custNo,poolAgreement);
				map.put("FROZEN_AMT", bail.getAssetLimitFrzd());// 冻结金额
				map.put("DEPOSIT_BAL_AMT", bail.getAssetLimitTotal());// 保证金金额
				
				if(map!=null && map.size()>0){
					response.setBody(map);
					ret.setRET_MSG("查询成功");
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);	
				}else{
					ret.setRET_MSG("无符合条件数据");
					ret.setRET_CODE(Constants.EBK_03);
				}
				
			} else {
				ret.setRET_CODE(Constants.EBK_02);
            	ret.setRET_MSG("该客户票据池融资功能未开通,不允许操作!");
			}
		} catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询异常");
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
