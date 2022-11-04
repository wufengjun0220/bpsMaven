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
import com.mingtech.framework.common.util.StringUtil;

/**
 * 
 * @Title: 网银查询接口PJC020
 * @Description: 保证金管理-保证金账号查询接口
 * @author Ju Nana
 * @date 2018-10-24
 */
public class PJC020RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC020RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService; // 网银方法类

	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		try {
			String custNo = getStringVal(request.getBody().get("CORE_CLIENT_NO"));
			String poolAgreement = getStringVal(request.getBody().get("BPS_NO"));// 协议编号
			String singFlag = poolEBankService.queryPoolCommOpen(poolAgreement,null, custNo, null, null);
			if (StringUtil.isNotEmpty(singFlag) && singFlag.equals(PoolComm.DRAFT_POOL_OPEN)) {
				Map map = poolEBankService.queryMarginAccountPJC020(custNo, poolAgreement);
				if (map != null && map.size() > 0) {
					response.setBody(map);
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("查询成功");
				} else {
					ret.setRET_MSG("无符合条件数据");
					ret.setRET_CODE(Constants.EBK_03);
				}

			} else {
				ret.setRET_CODE(Constants.EBK_02);
				ret.setRET_MSG("该客户未开通票据池业务");
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

	/**
	 * 1.构建信息接收,从map中得到三证合一信息 2通过三证合一信息查询 CustomerDto 3.获取CustomerDto 中的客户组织结构代码
	 * 4.判断客户是否开通了票据池 5.根据客户组织机构代码查询额度信息,返回List result=.... AssetType,AssetPool
	 */

}
