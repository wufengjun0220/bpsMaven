package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.utils.ErrorCode;

/**
 * @Description 在线业务借据信息同步
 * @author 
 * @version v1.0
 * @date 2021-04-28
 */
public class PJC066RequestHandler  extends PJCHandlerAdapter {
	private static final Logger logger = Logger.getLogger(PJC066RequestHandler.class);
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private PedOnlineAcptService pedOnlineAcptService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Ret ret = new Ret();
        /*通过线下核心还款：票据池系统日间或日终同步借据余额后（若余额减少，非到期还款交易），按如下流程处理：
         ①自动释放对应票据池低风险额度、票据池担保合同额度。
         ②收款人额度需由人工通过企业网银——在线流贷资金支付模块，“修改支付计划”按钮操作后释放。
        */
        try {
	        Map map = request.getBody();
	        String acptNo = getStringVal(map.get("ONLINE_ACPT_NO"));//在线业务编号
	        String loanNo = getStringVal(map.get("IOU_NO"));//在线业务借据
	        String onlineType = getStringVal(map.get("ONLINE_BUSS_TYPE"));//在线业务类型0：在线银承1：在线流贷
	        if(PublicStaticDefineTab.PRODUCT_YC.equals(onlineType)){
	        	OnlineQueryBean queryBean = new OnlineQueryBean();
	    		queryBean.setOnlineAcptNo(acptNo);
	    		queryBean.setMsgStatus(PublicStaticDefineTab.ACPT_DETAIL_012);
	    		Date startDate = new Date(new Date().getTime() - 1000 * 60 * 60
	    				* 2);
	    		queryBean.setTaskDateStart(startDate);
	    		queryBean.setTaskDateEnd(new Date());
	    		List list = pedOnlineAcptService.queryOnlinAcptDetails(queryBean);
	    		if(null != list && list.size()>0){
	    			logger.info(list.size()+"PJC066在线业务借据信息同步------------------------");
	    			for(int i=0;i<list.size();i++){
	    				PlOnlineAcptDetail detail = (PlOnlineAcptDetail) list.get(i);
	    	        	pedOnlineAcptService.txSyncPedCreditDetail(detail);
	    			}
	    		}
	        }else{
	        	pedOnlineCrdtService.txSyncPedCreditDetail(acptNo,loanNo);
	        }
			// 构建响应对象
			ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
			ret.setRET_MSG(ErrorCode.SUCC_MSG_CH);
        } catch (Exception e) {
			logger.error(e, e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("在线银承借据信息同步异常");
		
		}
		response.setRet(ret);
        return response;
    }

}
