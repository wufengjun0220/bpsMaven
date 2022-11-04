package com.mingtech.application.pool.edu.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.domain.PedProtocolDto;
import com.mingtech.application.pool.common.service.PedProtocolService;
import com.mingtech.application.pool.edu.domain.PedBailTrans;
import com.mingtech.application.pool.edu.service.PoolBailEduService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * @author wbliujianfei
 * 
 */
@Controller
public class EduController extends BaseController {

	private Logger logger = Logger.getLogger(EduController.class);
	@Autowired
	private PedProtocolService pedProtocolService;
	@Autowired
	private PoolBailEduService poolBailEduService;


	/**
	 * 加载当日保证金查询界面
	 */
	@RequestMapping("toQueryBailToday")
	public String toQueryBailToday() {
		return "/pool/bail/queryBailToday";
	}

	/**
	 * 查询当日保证金流水信息list
	 * 
	 * @param flow
	 * @throws Exception
	 */
	@RequestMapping("bailFlowList")
	public void bailFlowList(QueryBean bean) throws Exception {
		Page page = this.getPage();
		try {
			List list = poolBailEduService.queryFlowByAcc(bean.getMarginAccount(),this.getCurrentUser(),page);
			page.setResult(list);
			Map map = new HashMap();
			map.put("totalProperty", "results," + page.getTotalCount());
			map.put("root", "rows");
			String json = JsonUtil.fromCollections(list, map);
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	/**
	 * 加载保证金历史查询界面
	 * 
	 * @return
	 */
	@RequestMapping("toQueryBailHistory")
	public String toQueryBailHistory() {
		return "/pool/bail/queryBailHistory";
	}

	/**
	 * 查询保证金划转历史记录
	 * 
	 * @param bean
	 */
	@RequestMapping("bailHistoryList")
	public void bailHistoryList(QueryBean bean) {
		Page page = this.getPage();
		try {
			String json;
			if(null==bean.getPoolAgreement() && null== bean.getMarginAccount() && null==bean.getStartplDueDt() && null == bean.getEndplDueDt()){
				json="查询失败";
			}else{
				List list = poolBailEduService.queryFlowByAHistorycc(bean,this.getCurrentUser(),page);
				page.setResult(list);
				Map map = new HashMap();
				map.put("totalProperty", "results," + page.getTotalCount());
				map.put("root", "rows");
				json = JsonUtil.fromCollections(list, map);
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}


	/**
	 * 查询本条票据池协议规则属性Dto 池票据查询
	 * 
	 * @author liu xiaodong
	 * @date
	 */
	@RequestMapping("querySaveViewtoEff")
	public String querySaveViewtoEff(PedProtocolDto pedProtocolDto, Model mode) {
		try {
			pedProtocolDto = (PedProtocolDto) pedProtocolService.load(pedProtocolDto.getPoolInfoId());
			mode.addAttribute("pedProtocolDto", pedProtocolDto);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return "/pool/query/poolDraftQuery";
	}

	
	/**
	 * 加载保证金支取信息
	 * @param id
	 * @author Wufengjun
	 * @date 20210701
	 */
	@RequestMapping("loadPedBailDetal")
	public void loadPedBailDetal(QueryBean bean){
		Page page = this.getPage();
		try {
			User user = this.getCurrentUser();
			List trans = poolBailEduService.queryBailTrans(bean,page,null);
			String json =JsonUtil.fromObject(trans.get(0));
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/************************************************************网银保证金划转***********************************************************************/
	
	/**
	 * 网银保证金划转界面首页查询
	 * @param bean
	 * @author Wufengjun
	 * @date 20210705
	 */
	@RequestMapping("queryPedBailTrans")
	public void queryPedBailTrans(QueryBean bean){
		String json = "";
		Page page = this.getPage();
		try {
			User user = this.getCurrentUser();
			//处理超过一天的数据值为作废
			QueryBean queryBean = new QueryBean();
			queryBean.setStartDate(DateUtils.modDay(new Date(),-1));
			queryBean.setFlag("3");//作废
			poolBailEduService.txCancelPedBailTran(queryBean);

			//保存后重新查询
			List trans = poolBailEduService.queryBailTrans(bean,page,user);

			json = JsonUtil.buildJson(trans, page.getTotalCount());
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 网银保证金划转界面提交审批
	 * @param bean
	 * @author Wufengjun
	 * @date 20210705
	 */
	@RequestMapping("submitPedBailTrans")
	public void submitPedBailTrans(String id){
		String json = "提交成功!";
		try {
			Page page = this.getPage();
			User user = this.getCurrentUser();
			
			poolBailEduService.txSubmitPedBailTrans(id,user);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.toString(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "提交审批失败:"+e.getMessage();
			this.sendJSON(json);
		}
	}
	
	/**
	 * 网银保证金划转界面撤销审批
	 * @param bean
	 * @author Wufengjun
	 * @date 20210705
	 */
	@RequestMapping("cancelAuditPedBailTrans")
	public void cancelAuditPedBailTrans(String id){
		String msg = "";
		try {
			User user = this.getCurrentUser();
			poolBailEduService.txCancelAudit(id,user);
			msg = "撤销审批成功";
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			 msg = "撤销审批失败:"+e.getMessage();
		}
		this.sendJSON(msg);
	}
	
	/**
	 * 网银保证金划转界面划转流水查询 ;查询核心是否划转
	 * @param id
	 * @author Wufengjun
	 * @date 20210705
	 */
	@RequestMapping("queryPedBailTransDetail")
	public void queryPedBailTransDetail(String id){
		String msg = "";
		try {
			User user = this.getCurrentUser();
			poolBailEduService.queryPedBailTransDetail(id,user);
			msg = "查询成功";
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			 msg = "查询失败:"+e.getMessage();
		}
		this.sendJSON(msg);
	}
	
	
	/**
	 * 网银保证金划转界面划转重新划转
	 * @param id
	 * @author Wufengjun
	 * @date 20210705
	 */
	@RequestMapping("sendPedBailTranAgain")
	public void sendPedBailTranAgain(String id){
		String json = "";
		try {
			User user = this.getCurrentUser();
			Ret ret = poolBailEduService.sendPedBailTranAgain(id,user);
			json =JsonUtil.fromObject(ret);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "查询失败:"+e.getMessage();
		}
		this.sendJSON(json);
	}
	
	
	/**
	 * 网银保证金划转查看详情
	 * @param id
	 * @author wfj
	 * @date 20210719
	 */
	@RequestMapping("queryPedBailDetailById")
	public void queryPedBailDetailById(String id){
		String json = "";
		try {
			User user = this.getCurrentUser();
			PedBailTrans trans = (PedBailTrans) poolBailEduService.load(id,PedBailTrans.class);
			json =JsonUtil.fromObject(trans);
			if(!(StringUtil.isNotBlank(json))){
				json = RESULT_EMPTY_DEFAULT;
			}
			this.sendJSON(json);
			
		} catch (Exception e) {
			logger.error("业务处理异常",e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			json = "查询失败:"+e.getMessage();
		}
		this.sendJSON(json);
	}
	
	/**
	 * 保证金账户变更信息查询
	 * 
	 * @param 
	 * @throws Exception
	 */
	@RequestMapping("queryBailAccountChange")
	public void queryBailAccountChange(QueryBean bean) throws Exception {
		Page page = this.getPage();
		try {
			List list = poolBailEduService.queryBailAccountChange(bean,page);
			page.setResult(list);
			Map map = new HashMap();
			map.put("totalProperty", "results," + page.getTotalCount());
			map.put("root", "rows");
			String json = JsonUtil.fromCollections(list, map);
			this.sendJSON(json);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/************************************************************网银保证金划转 end***********************************************************************/
	@RequestMapping("txRefreshJM")
	public void txRefreshJM() throws Exception{
		String json="已接收到同步请求，请稍后查询同步结果。";
		this.sendJSON(json);
		String result = poolBailEduService.txBailQueryFromCoreJM(null);
		
			

	}
	
}
