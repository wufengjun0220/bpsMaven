package com.mingtech.application.pool.bank.netbanksys.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;

/**
 * @Description 票据池服务费收费查询
 * @author Ju Nana
 * @version v1.0
 * @date 2019-08-01
 */
public class PJC049RequestHandler  extends PJCHandlerAdapter {
    @Autowired
    private PoolEBankService poolEBankService;
    @Autowired
    private PedProtocolService pedProtocolService;

    public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception{
        ReturnMessageNew response = new ReturnMessageNew();
        Map map = request.getBody();
        Ret ret = new Ret();
        List<Map> list = new ArrayList<Map>();
        BigDecimal ZERO  = BigDecimal.ZERO;
        /*
         * 备注：按照网银同事要求，当非系统错误的报错信息时候，不返回错误码，将收费标识返回2，表示查询非系统异常     20190917    Ju Nana
         */
        try {
            String custNo = getStringVal(map.get("CORE_CLIENT_NO"));//核心客户号
            String bpsNo = getStringVal(map.get("BPS_NO"));//票据池编号
            PedProtocolDto pro = pedProtocolService.queryProtocolDto(null, null, bpsNo, custNo, null, null);//注意：集团户只有主户可以驱动收费，所以加入客户号字段
            if(pro != null) {
                if(!pro.getOpenFlag().equals(PoolComm.OPEN_01)){//-------------实际应为返回查询错误码值，根据网银要求修改
                	response.getBody().put("ANNUAL_FEE_STANDARD", ZERO);
                    response.getBody().put("FEE_FLAG", "2");
                    response.getBody().put("BPS_NO", pro.getPoolAgreement());
                    response.getBody().put("BPS_NAME", pro.getPoolName());
                    response.getBody().put("SETTLE_ACCT_NO", pro.getPoolAccount());//结算账户
                    response.getBody().put("SETTLE_ACCT_NAME", pro.getPoolAccountName());//结算账户名
                    ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                    ret.setRET_MSG("该客户未融资签约,不允许操作!");
                    response.setRet(ret);
                    return response;
                }
                if(pro.getFeeType().equals(PoolComm.SFMS_01)) {
                    list =poolEBankService.queryPoolFee(custNo,bpsNo);
                    if(list != null ) {
                        for( Map<String, Object> mapList : list ) {
                            response.getBody().put("ANNUAL_FEE_STANDARD", mapList.get("ANNUAL_FEE_STANDARD"));
                            response.getBody().put("FEE_FLAG", mapList.get("IS_CHARGE"));
                            response.getBody().put("BPS_NO", pro.getPoolAgreement());
                            response.getBody().put("BPS_NAME", pro.getPoolName());
                            response.getBody().put("SETTLE_ACCT_NO", pro.getPoolAccount());//结算账户
                            response.getBody().put("SETTLE_ACCT_NAME", pro.getPoolAccountName());//结算账户名
                        }
                        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                        ret.setRET_MSG("查询成功!");
                    }else {//-------------实际应为返回查询错误码值，根据网银要求修改
                    	response.getBody().put("ANNUAL_FEE_STANDARD", ZERO);
                        response.getBody().put("FEE_FLAG", "2");
                        response.getBody().put("BPS_NO", pro.getPoolAgreement());
                        response.getBody().put("BPS_NAME", pro.getPoolName());
                        response.getBody().put("SETTLE_ACCT_NO", pro.getPoolAccount());//结算账户
                        response.getBody().put("SETTLE_ACCT_NAME", pro.getPoolAccountName());//结算账户名
                        ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                        ret.setRET_MSG("票据池服务费收费查询失败!");
                    }
                }else {//-------------实际应为返回查询错误码值，根据网银要求修改
                	response.getBody().put("ANNUAL_FEE_STANDARD", ZERO);
                    response.getBody().put("FEE_FLAG", "2");
                    response.getBody().put("BPS_NO", pro.getPoolAgreement());
                    response.getBody().put("BPS_NAME", pro.getPoolName());
                    response.getBody().put("SETTLE_ACCT_NO", pro.getPoolAccount());//结算账户
                    response.getBody().put("SETTLE_ACCT_NAME", pro.getPoolAccountName());//结算账户名
                    ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                    ret.setRET_MSG("该客户收费模式为单笔收费!");
                }
            }else {//-------------实际应为返回查询错误码值，根据网银要求修改
            	response.getBody().put("ANNUAL_FEE_STANDARD", ZERO);
                response.getBody().put("FEE_FLAG", "2");
                response.getBody().put("BPS_NO", bpsNo);
                response.getBody().put("BPS_NAME", "");
                response.getBody().put("SETTLE_ACCT_NO", "");//结算账户
                response.getBody().put("SETTLE_ACCT_NAME", "");//结算账户名
                ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                ret.setRET_MSG("该客户查询不到有效的签约信息!");
            }
        } catch (Exception e) {
            ret.setRET_CODE(Constants.TX_FAIL_CODE);//
            ret.setRET_MSG("票据池服务费收费查询! 票据池内部执行错误");
        }
        response.setRet(ret);
        return response;
    }

}
