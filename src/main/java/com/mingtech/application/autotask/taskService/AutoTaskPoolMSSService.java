package com.mingtech.application.autotask.taskService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.domain.TMessageRecord;
import com.mingtech.application.pool.bank.message.service.PoolMssService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 *短信发送处理类
 *
 *gcj 20210528
 */


public class AutoTaskPoolMSSService {
	private static final Logger logger = Logger.getLogger(AutoTaskPoolMSSService.class);
	OnlineManageService onlineManageService=PoolCommonServiceFactory.getOnlineManageService();
	PoolMssService poolMssService=  PoolCommonServiceFactory.getPoolMssService();
	/**
	 * gcj 20210528
	 * @param busiId 业务ID
	 * @param queryType  查询类型 页面触发需先查一遍票据状态其他传null
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew txHandleRequest(String busiId,String queryType,Map reqParamsMap) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		TMessageRecord info = (TMessageRecord) onlineManageService.load(busiId, TMessageRecord.class);
		try {
		if(null==info){
			this.response(Constants.TX_FAIL_CODE, "根据ID"+busiId+"未能找到短信实体", response, ret);
			return response;
		}
		ReturnMessageNew res = new ReturnMessageNew();
		String msg=(String)reqParamsMap.get("msg");//短信内容
		CoreTransNotes transNotes =new CoreTransNotes();
		transNotes.setPushID(info.getPhoneNo());
		transNotes.setTemplate(msg);
		logger.info(info.getPhoneNo()+"发送短信开始...");
		res=poolMssService.txMess001Handler(transNotes);
		if(res.isTxSuccess()){
			this.response(Constants.TX_SUCCESS_CODE, "发送短信成功", response, ret);
			info.setSendResult("1");//成功
			onlineManageService.txStore(info);
		}else{
			this.response(Constants.TX_FAIL_CODE, "发送短信失败", response, ret);
			info.setSendResult("2");//失败
			onlineManageService.txStore(info);
		}
		} catch (Exception e) {
			this.response(Constants.TX_FAIL_CODE, "发送短信失败"+e.getMessage(), response, ret);
			info.setSendResult("2");//失败
			onlineManageService.txStore(info);
		}
		return response;
	}
	
	public void response(String code,String msg,ReturnMessageNew response,Ret ret){
		logger.info(msg);
		ret.setRET_CODE(code);
		ret.setRET_MSG(msg);
		response.setRet(ret);
	}
	protected String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}
	protected BigDecimal getBigDecimalVal(Object obj) throws Exception {
		BigDecimal value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = new BigDecimal(temp);
			}
		}
		return value;
	}
	protected Date getDateVal(Object obj) throws Exception {
		Date value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
			}
		}
		return value;
	}
}
