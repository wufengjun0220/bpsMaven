package com.mingtech.application.pool.bank.netbanksys.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.pool.bank.common.PJCHandlerAdapter;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.netbanksys.domain.QueryResult;
import com.mingtech.application.pool.bank.netbanksys.service.PoolEBankService;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.domain.PedProtocolList;
import com.mingtech.application.pool.common.domain.ProListQueryBean;
import com.mingtech.application.pool.common.domain.ProtocolQueryBean;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;

/**
 * 
 * @Title: 网银接口 PJC027
 * @Description: 帐务管家-全量票据明细查询接口
 * @author tangxiongyu
 * @date 2018-12-29
 */
public class PJC027RequestHandler extends PJCHandlerAdapter {

	private static final Logger logger = Logger.getLogger(PJC027RequestHandler.class);
	@Autowired
	private PoolEBankService poolEBankService;// 网银方法类
	@Autowired
	private PedProtocolService pedProtocolService;
	/**
	 * 网银接口 PJC027 全量票据明细查询接口
	 */
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request) throws Exception {

		ReturnMessageNew response = new ReturnMessageNew();
		Ret ret = new Ret();

		try {
			PoolQueryBean pool = QueryParamMap(request);
			
			ProtocolQueryBean queryBean = new ProtocolQueryBean();
			queryBean.setPoolAgreement(pool.getProtocolNo());
			PedProtocolDto pro =  pedProtocolService.queryProtocolDtoByQueryBean(queryBean);
			
			logger.info("网银传过来的客户号【"+pool.getSCustOrgCode()+"】");
			logger.info("票据池系统的客户号【"+pro.getCustnumber()+"】");
			boolean a = pool.getSCustOrgCode().equals(pro.getCustnumber());
			
			if(pro!=null && PoolComm.YES.equals(pro.getIsGroup()) && !pool.getSCustOrgCode().equals(pro.getCustnumber())){
				
				ret.setRET_CODE(Constants.EBK_03);
				ret.setRET_MSG("集团分户无账务管家查询权限！");
				
			}else{
				/*
				 * 账务管家重新更新数据
				 */
				String eleAccNos = null;
				ProtocolQueryBean qBean = new ProtocolQueryBean();
				qBean.setPoolAgreement(pool.getProtocolNo());
				PedProtocolDto dto = pedProtocolService.queryProtocolDtoByQueryBean(qBean);
				if(PoolComm.YES.equals(dto.getIsGroup())){//集团
					ProListQueryBean bean = new ProListQueryBean();
					bean.setBpsNo(dto.getPoolAgreement());
					List<String> custIdentityList = new ArrayList<String>();
					custIdentityList.add(PoolComm.KHLX_01);
					custIdentityList.add(PoolComm.KHLX_03);
					bean.setCustIdentityList(custIdentityList);
					List<PedProtocolList> mems = pedProtocolService.queryProListByQueryBean(bean);
					if(mems!=null && mems.size()>0){
						for(PedProtocolList mem : mems){
							if(StringUtils.isBlank(eleAccNos)){
								eleAccNos = mem.getElecDraftAccount();
							}else{
								eleAccNos = eleAccNos +"|"+mem.getElecDraftAccount();
							}
						}
					}
					
				}else{
					eleAccNos = dto.getElecDraftAccount();
				}
				poolEBankService.txAccountManagement(pool.getProtocolNo(),pool.getSCustOrgCode(),eleAccNos);
				
				
				/*
				 * 账务管家查询
				 */
				QueryResult result = poolEBankService.queryDraftAccountManagement(pool);
				if (null == result || result.getTotalCount() == 0) {
					ret.setRET_CODE(Constants.EBK_03);
					ret.setRET_MSG("无符合条件数据");
				} else {
					// 构建响应对象
					List details = result.getRecords();
					response.setDetails(details);
					response.getAppHead().put("TOTAL_ROWS", result.getTotalCount());// 总记录数
					ret.setRET_CODE(Constants.TX_SUCCESS_CODE);
					ret.setRET_MSG("查询成功!");
				}
			}
			

		} catch (Exception ex) {
			logger.error(ex, ex);
			ret.setRET_CODE(Constants.TX_FAIL_CODE);
			ret.setRET_MSG("查询异常");
		}
		response.setRet(ret);
		return response;
	}

	/**
	 * 
	 * @Description: 请求数据处理
	 * @param request
	 * @return PoolQueryBean
	 * @author tangxiongyu
	 * @date 2018-12-29
	 */
	private PoolQueryBean QueryParamMap(ReturnMessageNew request) throws Exception {

		PoolQueryBean pq = new PoolQueryBean();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map body = request.getBody();
		pq.setSCustOrgCode(getStringVal(body.get("CORE_CLIENT_NO"))); // 核心客户号
		pq.setProtocolNo(getStringVal(body.get("BPS_NO"))); // 协议编号
		pq.setPstartDate(getDateVal(body.get("QUERY_START_TIME"))); // 到期日开始
		pq.setPendDate(getDateVal(body.get("QUERY_ENT_TIME"))); // 到期日结束
		pq.setPoolEquities(getStringVal(body.get("BILL_RIGHTS")));// 票据权益
		pq.setSumType(getStringVal(body.get("SUM_TYPE")));// 汇总类型
		pq.setSumValue(getStringVal(body.get("SUM_VALUE")));// 汇总值
		pq.setBillType(getStringVal(body.get("BILL_CLASS"))); // 票据种类
		pq.setSBillMedia(getStringVal(body.get("BILL_TYPE"))); // 票据介质
		return pq;
	}
	public PoolEBankService getPoolEBankService() {
		return poolEBankService;
	}

	public void setPoolEBankService(PoolEBankService poolEBankService) {
		this.poolEBankService = poolEBankService;
	}

}
