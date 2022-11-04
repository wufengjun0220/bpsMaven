package com.mingtech.application.pool.bank.netbanksys.handler;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.codec.util.StringUtil;
import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.coresys.domain.CoreTransNotes;
import com.mingtech.application.pool.bank.coresys.service.PoolCoreService;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.PlFeeList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.framework.common.util.DateUtils;


/**
 * @Title: 网银接口PJC040-票据池服务费收取
 * @author xie cheng
 * @date 2019-05-30
 */
public class PJC040RequestHandler extends PJCHandlerAdapter{
	private static final Logger logger = Logger.getLogger(PJC040RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolCoreService poolCoreService;
	
	
	public ReturnMessageNew txHandleRequest(String code,ReturnMessageNew request) throws Exception {
		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();
		Map body = request.getBody();
		PlFeeList plFeeList = new PlFeeList();
		try {
			String bpsNo = getStringVal(body.get("BPS_NO"));
			String clientNo = getStringVal(body.get("CORE_CLIENT_NO"));
			PedProtocolDto dto = pedProtocolService.queryProtocolDto(null, null, bpsNo, null, null, null);
			
			//只有推送了担保合同，且合同在有效期才可以缴费
			String contract = dto.getContract();
			if(StringUtil.isBlank(contract)){
				ret.setRET_CODE(Constants.EBK_02);
                ret.setRET_MSG("该票据池未推送担保合同，无需缴纳票据池服务费!");
                response.setRet(ret);
                return response;
			}else{
				Date dueDate = dto.getContractDueDt();//担保合同到期日
				Date today = new Date();//当前日期		
				if(dueDate!=null){					
					if(today.getTime()>dueDate.getTime()){
						ret.setRET_CODE(Constants.EBK_02);
						ret.setRET_MSG("该票据池担保合同已到期，无法缴纳票据池服务费!");
						response.setRet(ret);
						return response;
					}
				}
			}
			
			
			if(null != dto){
                //单户校验
			    if(dto.getIsGroup().equals(PoolComm.NO)){
			        if (!clientNo.equals(dto.getCustnumber())){
                        ret.setRET_CODE(Constants.EBK_03);
                        ret.setRET_MSG("不能查询此身份的数据，无符合条件数据!");
                        response.setRet(ret);
                        return response;
                    }
                    if(!dto.getOpenFlag().equals(PoolComm.OPEN_01)){
                        ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("该客户未融资签约，不允许操作!");
                        response.setRet(ret);
                        return response;
                    }
                }

                //集团校验
                if(dto.getIsGroup().equals(PoolComm.YES)){
                    ProListQueryBean queryBean = new ProListQueryBean();
                    queryBean.setBpsNo(bpsNo);//票据池编号
                    queryBean.setCustNo(clientNo);//客户号
                    queryBean.setStatus(PoolComm.PRO_LISE_STA_01);
                    PedProtocolList pedList = pedProtocolService.queryProtocolListByQueryBean(queryBean);
                    if(null==pedList){
                        ret.setRET_CODE(Constants.EBK_03);
                        ret.setRET_MSG("该客户未查询到有效的集团签约协议,不允许操作!");
                        response.setRet(ret);
                        return response;
                    }
                    if(!pedList.getRole().equals(PoolComm.JS_01)){
                        ret.setRET_CODE(Constants.EBK_03);
                        ret.setRET_MSG("该交易只有集团主户方可进行操作!");
                        response.setRet(ret);
                        return response;
                    }
                    if(!dto.getOpenFlag().equals(PoolComm.OPEN_01)){
                        ret.setRET_CODE(Constants.TX_FAIL_CODE);
                        ret.setRET_MSG("该客户未融资签约，不允许操作!");
                        response.setRet(ret);
                        return response;
                    }
                }
                if(!dto.getFeeType().equals(PoolComm.SFMS_01)){
                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                    ret.setRET_MSG("该客户收费模式不为年费模式,不允许操作!");
                    response.setRet(ret);
                    return response;
                }
                if(DateUtils.checkOverLimited(dto.getFeeDueDt(),DateUtils.getCurrDate())){
                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                    ret.setRET_MSG("该客户年费到期日大于当前日期,不允许操作!");
                    response.setRet(ret);
                    return response;
                }
                plFeeList.setCustName(dto.getCustname());
                plFeeList.setBpsName(dto.getPoolName());
                plFeeList.setCustNo(clientNo);//核心客户号
                plFeeList.setBpsNo(bpsNo);//票据池编号
               // plFeeList.setFeeType(getStringVal(body.get("FEE_MODE")));//收费模式
                plFeeList.setRealAmt(getBigDecimalVal(body.get("REAL_CHAR_AMT")));//实收金额
                plFeeList.setChargeDate(new Date());//收费时间
                plFeeList.setSource(PoolComm.SFLY_00);
                plFeeList.setFeeType(PoolComm.SFMS_01);//收费模式

                Date sysDate =DateUtils.formatDate(new Date(),DateUtils.ORA_DATE_FORMAT);//机器时间
              
                
                
                
                //掉核心记账接口
                CoreTransNotes transNotes = new CoreTransNotes();
                transNotes.setDeductionAcctNo(getStringVal(body.get("PAYER_ACCT_NO")));
//			    transNotes.setFeeRateCode(getStringVal(body.get("")));
                transNotes.setAmt(getStringVal(body.get("REAL_CHAR_AMT")));

                transNotes.setUser((String)request.getSysHead().get("USER_ID"));
                transNotes.setBrcBld(getStringVal(request.getSysHead().get("BRANCH_ID")));
                transNotes.setBpsNo(bpsNo);//票据池编号
                transNotes.setBrcNo(dto.getCreditDeptNo()); //客户号对应的融资签约机构
                
                /*
                 * 核心收费记账接口调用
                 */
                ReturnMessageNew resp = poolCoreService.doFeeScaleCoreAccount(transNotes);
                
                
                
                if(resp.isTxSuccess()){
                    dto.setFeeIssueDt(sysDate);// 生效日期
                    dto.setFeeDueDt(DateUtils.formatDate(DateUtils.getNextNMonth(sysDate,12),DateUtils.ORA_DATE_FORMAT));// 到期日
                    poolEBankService.txStore(plFeeList);
                    poolEBankService.txStore(dto);
                    ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
                    ret.setRET_MSG(""+clientNo+"票据池服务费收取成功!");
                    logger.info(""+clientNo+"票据池服务费收取分配成功!");
                }else {
                    ret.setRET_CODE(Constants.TX_FAIL_CODE);
                    ret.setRET_MSG("票据池服务费收取记账失败!"+resp.getRet().getRET_MSG()+"");
                }
            }else {
                ret.setRET_CODE(Constants.TX_FAIL_CODE);
                ret.setRET_MSG("该客户查询不到有效的签约信息!");
            }
		} catch (Exception e) {
			logger.error("网银接口PJC040-票据池服务费收取异常!", e);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("票据池服务费收取异常! 票据池内部执行错误");
		}
		response.setRet(ret);
		return response;
	}

}
