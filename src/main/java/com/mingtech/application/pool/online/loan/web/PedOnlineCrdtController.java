package com.mingtech.application.pool.online.loan.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.edu.domain.CreditProduct;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.edu.service.PoolCreditProductService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocol;
import com.mingtech.application.pool.online.loan.domain.PedOnlineCrdtProtocolHist;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayPlan;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCacheCrdt;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.pool.online.loan.service.PedOnlineCrdtService;
import com.mingtech.application.pool.online.manage.service.OnlineManageService;
import com.mingtech.application.pool.query.domain.CommonQueryBean;
import com.mingtech.application.pool.query.service.CommonQueryService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;



@Controller
public class PedOnlineCrdtController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(PedOnlineCrdtController.class);
	
	@Autowired
	private PedOnlineCrdtService pedOnlineCrdtService;
	@Autowired
	private OnlineManageService onlineManageService;
	@Autowired
	private CommonQueryService commonQueryService;
	@Autowired
	private PoolCreditProductService poolCreditProductService;
	
	/**
	 * ????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineCrdtPtlById",method=RequestMethod.POST)
	public void queryOnlineCrdtPtlById(String id){
		try {
			PedOnlineCrdtProtocol crdtProtocol = (PedOnlineCrdtProtocol) pedOnlineCrdtService.load(id,PedOnlineCrdtProtocol.class);
			this.sendJSON(JsonUtil.fromObject(crdtProtocol));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		} 
	}
	
	/**
	 * ?????????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineCrdtPayeeByCrdtId",method=RequestMethod.POST)
	public void queryOnlineCrdtPayeeByCrdtId(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineCrdtService.queryOnlineCrdtPayeeListByBean(bean, page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		} 
	}
	
	/**
	 * ??????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlinePlanListByCrdtId",method=RequestMethod.POST)
	public void queryOnlinePlanListByCrdtId(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineCrdtService.queryPlCrdtPayPlanListByBean(bean, page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("??????????????????????????????"+ e.getMessage());
		} 
	}
	/**
	 * ????????????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlinePlanHisByCrdtId",method=RequestMethod.POST)
	public void queryOnlinePlanHisByCrdtId(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineCrdtService.queryPlCrdtPayCachePlanListByBean(bean, page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????????????????"+ e.getMessage());
		} 
	}
	/**
	 * ????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineCrdtById",method=RequestMethod.POST)
	public void queryOnlineCrdtById(String id){
		try {
			PlOnlineCrdt batch = (PlOnlineCrdt) pedOnlineCrdtService.load(id, PlOnlineCrdt.class);
			this.sendJSON(JsonUtil.fromObject(batch));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("??????????????????????????????"+ e.getMessage());
		} 
	}

	/**
	 * ??????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineCrdtHisById",method=RequestMethod.POST)
	public void queryOnlineCrdtHisById(String id){
		try {
			PlOnlineCacheCrdt batch = (PlOnlineCacheCrdt) pedOnlineCrdtService.load(id, PlOnlineCacheCrdt.class);
			this.sendJSON(JsonUtil.fromObject(batch));
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????????????????"+ e.getMessage());
		} 
	}
	/**
	 * ??????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineCrdtPtlHist",method=RequestMethod.POST)
	public void queryOnlineCrdtPtlHist(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			PedOnlineCrdtProtocolHist hist = (PedOnlineCrdtProtocolHist) pedOnlineCrdtService.load(bean.getId(), PedOnlineCrdtProtocolHist.class);
			Map map = new HashMap();
			if(StringUtils.isNotBlank(hist.getLastSourceId())){
				PedOnlineCrdtProtocolHist lastdhHist = (PedOnlineCrdtProtocolHist) pedOnlineCrdtService.load(hist.getLastSourceId(), PedOnlineCrdtProtocolHist.class);
				lastdhHist = pedOnlineCrdtService.compareDto(hist,lastdhHist);
				map.put("updateDate", lastdhHist);
			}
			map.put("data", hist);
			this.sendJSON(JsonUtil.fromObject(map));
		
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("??????????????????????????????"+ e.getMessage());
		} 
	}
	
	/**
	 * ???????????????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlineCrdtPayeeHist",method=RequestMethod.POST)
	public void queryOnlineCrdtPayeeHist(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineCrdtService.queryOnlineCrdtPayeeHistListByBean(bean,null);
			if(null != list && list.size()>0){
				Map map = new HashMap();
				List lastList = new ArrayList();
				OnlineQueryBean hist = (OnlineQueryBean) list.get(0);
				if(StringUtils.isNotBlank(hist.getLastSourceId())){
					bean.setLastSourceId(hist.getLastSourceId());
					bean.setModeMark(null);
					lastList = pedOnlineCrdtService.queryOnlineCrdtPayeeHistListByBean(bean,null);
				}
				map.put("updateData", lastList);
				map.put("data", list);
				this.sendJSON(JsonUtil.fromObject(map));
			}else{
				this.sendJSON(this.RESULT_EMPTY_DEFAULT);
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("?????????????????????????????????"+ e.getMessage());
		} 
	}
	
	/**
	 * ????????????????????????????????????-??????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="queryOnlinePlanHistListByPlanId",method=RequestMethod.POST)
	public void queryOnlinePlanHistListByPlanId(OnlineQueryBean bean){
		try {
			Page page = this.getPage();
			List list = pedOnlineCrdtService.queryCrdtPlanHistList(bean,page);
			String json = JsonUtil.buildJson(list, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		} 
	}
	
	
	
	/**
	 * ??????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="txOnlinePayPlanCancel",method=RequestMethod.POST)
	public void txOnlinePayPlanCancel(OnlineQueryBean bean,String opType){
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			
			List list = pedOnlineCrdtService.queryPlCrdtPayPlanListByBean(bean, page);
			
			
			
//			pedOnlineCrdtService.txOnlinePayPlanCancel(list,user);
//			this.sendJSON("???????????????");
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		} 
	}
	
	/**
	 * ?????????????????????????????????????????????????????????
	 * @author wss
	 * @date 2021-6-7
	 */
	@RequestMapping(value="txModifyOnlinePayPlan",method=RequestMethod.POST)
	public void txModifyOnlinePayPlan(OnlineQueryBean bean){
		try {
			User user = this.getCurrentUser();
			PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(bean.getId(), PlCrdtPayPlan.class);
			
			if(bean.getType().equals("0")){
    			
				//??????????????????
				Ret ret = pedOnlineCrdtService.txModifyOnlinePayPlan(plan, bean.getRepayAmt());
				if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
					this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				}
				this.sendJSON(ret.getRET_MSG());
				
			}else{
				
				//????????????????????????--????????????
				Ret ret = pedOnlineCrdtService.txRepayOnlinePayPlanAudit(plan,bean.getRepayAmt(),user);
				if(ret.getRET_CODE().equals(Constants.TX_FAIL_CODE)){
					this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				}
				this.sendJSON(ret.getRET_MSG());
			}
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		} 
	}
	
	
	/**
	 * ?????????????????????????????????????????????????????????
	 * @author wfj
	 * @date 2021-7-6
	 */
	@RequestMapping(value="queryOnlinePayPlan",method=RequestMethod.POST)
	public void queryOnlinePayPlan(String id){
		String json = "";
		try {
			User user = this.getCurrentUser();
			ApproveAuditDto auditDto = (ApproveAuditDto) pedOnlineCrdtService.load(id,ApproveAuditDto.class);

			PlCrdtPayList pay = (PlCrdtPayList) pedOnlineCrdtService.load(auditDto.getBusiId(), PlCrdtPayList.class);
			PlCrdtPayPlan pp = (PlCrdtPayPlan) pedOnlineCrdtService.load(pay.getPayPlanId(), PlCrdtPayPlan.class);
			PlOnlineCrdt pc= (PlOnlineCrdt) pedOnlineCrdtService.load(pp.getCrdtId(), PlOnlineCrdt.class);
			CommonQueryBean query=new CommonQueryBean();
			
			query.setId(pp.getId());
			query.setBpsNo(pc.getBpsNo());//???????????????
			query.setCustName(pc.getCustName());//?????????
			query.setLoanAcctName(pp.getLoanAcctName());
			query.setCustNo(pc.getCustNo());
			query.setOnlineNo(pc.getOnlineCrdtNo());
			query.setContractNo(pc.getContractNo());
			query.setLoanNo(pc.getLoanNo());
			query.setLoanAcctNo(pp.getLoanAcctNo());
			query.setAmt(pp.getTotalAmt());
			query.setSurpluslAmt(pp.getTotalAmt().subtract(pp.getUsedAmt()));
			query.setDeduAcctName(pp.getDeduAcctName());
			query.setDeduAcctNo(pp.getDeduAcctNo());
			query.setDeduBankCode(pp.getDeduBankCode());
			query.setDeduBankName(pp.getDeduBankName());
			query.setLoanAmt(pp.getUsedAmt());
			query.setLoanBalance(auditDto.getAuditAmt());//????????????
			
//			PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(id, PlCrdtPayPlan.class);
//			PlOnlineCrdt crdt = (PlOnlineCrdt) pedOnlineCrdtService.load(plan.getCrdtId(), PlOnlineCrdt.class);
			json =JsonUtil.fromObject(query);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			
			
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		} 
		this.sendJSON(json);
	}
	
	/**
	 * ??????????????????????????????????????????????????????????????????
	 * @author wfj
	 * @date 2021-7-6
	 */
	@RequestMapping(value="queryOnlinePayPlanDetails",method=RequestMethod.POST)
	public void queryOnlinePayPlanDetails(OnlineQueryBean queryBean){
		String json = "";
		try {
			User user = this.getCurrentUser();
			Page page = this.getPage();
//			
			PlCrdtPayPlan plan = (PlCrdtPayPlan) pedOnlineCrdtService.load(queryBean.getId(),PlCrdtPayPlan.class);
			PlOnlineCrdt crdt = (PlOnlineCrdt) pedOnlineCrdtService.load(plan.getCrdtId(), PlOnlineCrdt.class);
			CommonQueryBean bean = new CommonQueryBean();
			bean.setOnlineCrdtNo(crdt.getOnlineCrdtNo());
			bean.setCustName(crdt.getCustName());
			bean.setContractNo(crdt.getContractNo());
			bean.setLoanNo(crdt.getLoanNo());
			bean.setId(plan.getId());
			json = commonQueryService.loadOnlinePayList(bean, this.getCurrentUser(), this.getPage());
//			List plans = pedOnlineCrdtService.queryOnlinePayPlanDetails(queryBean,page);
			
//			json = JsonUtil.buildJson(plans, page.getTotalCount());
		} catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_998,e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("????????????????????????"+ e.getMessage());
		} 
		this.sendJSON(json);
	}
	
	/**
	 * ??????????????????????????????
	 * @param id
	 * @author wfj
	 * @date 20210720
	 */
	@RequestMapping("cancelAuditPayPlan")
	public void cancelAuditPayPlan(String id){
		String msg = "";
		try {
			User user = this.getCurrentUser();
			pedOnlineCrdtService.txCancelAuditPayPlan(id, user);
			msg = "??????????????????";
		} catch (Exception e) {
			logger.error("??????????????????",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			 msg = "??????????????????:"+e.getMessage();
		}
		this.sendJSON(msg);
	}
	
	
	
		
}
