package com.mingtech.application.pool.bank.translog.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.bbsp.service.impl.PoolEcdsServiceImpl;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.hkb.TransCodeMap;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.translog.domain.TransLog;
import com.mingtech.application.pool.bank.translog.service.TransLogService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司
 * </p>
 * 
 * @作者: 张永超
 * @日期: Dec 9, 2009 10:43:51 AM
 * @描述: [TransLogServiceImpl] 行内接口日志表，用于异常展示及处理
 */
public class TransLogServiceImpl extends GenericServiceImpl implements TransLogService {
	
	private static final Logger logger = Logger.getLogger(TransLogServiceImpl.class);
	
	public Class getEntityClass() {
		return TransLog.class;
	}

	public String getEntityName() {
		return StringUtil.getClass(TransLog.class);
	}

	public TransLog txStoreRecMsg(byte[] message, ReturnMessageNew request) throws Exception {
		TransLog tl = new TransLog();
		tl.setMsgType(Constants.MS_REC);
		tl.setTxCode(request.getTxCode());
		tl.setTxCodeStr(TransCodeMap.templateMapNew().getNameMap().get(request.getTxCode()));
		tl.setMsgId((String) request.getSysHead().get("CONSUMER_SEQ_NO"));// 前台流水号);
		// 交易日期
		tl.setTxDate(DateUtils.formatDate(new Date(), DateUtils.ORA_DATES_FORMAT));
		// 交易时间
		tl.setCreDtTm(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_TIMES3_FORMAT));
		tl.setStatus(Constants.MSG_REC_UNDO);
		tl.setRspMsgContent(message);
		// 暂时存请求系统编号
		tl.setProdId((String) request.getSysHead().get("CONSUMER_ID"));
		// 获取交易说明
		tl.setTxMark((String) request.getSysHead().get("SERVICE_CODE") + "|"
				+ (String) request.getSysHead().get("SERVICE_SCENE"));
		this.txStore(tl);
		return tl;
	}

	public boolean txUpdateTransLogStatus(String id, boolean b, String remark, String setSeqno, byte[] reqMsg) {
		TransLog tl = (TransLog) this.load(id);
		if (tl.getMsgType().equals(Constants.MS_SEND)) {// 发送交易
			if (b) {// 成功
				tl.setStatus(Constants.MSG_SEND_SENDSUCC);
			} else {// 失败
				tl.setStatus(Constants.MSG_SEND_SENDFAIL);
			}
		} else if (tl.getMsgType().equals(Constants.MS_REC)) {// 接收交易
			if (b) {// 成功
				tl.setStatus(Constants.MSG_REC_DONESUCC);
			} else {// 失败
				tl.setStatus(Constants.MSG_REC_DONEFAIL);
			}
		}
		if (null != reqMsg) {
			tl.setMsgContent(reqMsg);
		}
		tl.setRemark(remark);
		this.txStore(tl);
		return true;
	}

	public String getTransLogJSON(TransLog tlog, Date beginDate, Date endDate, Page page) throws Exception {
		List list = getList(tlog, beginDate, endDate, page);
		Map jsonMap = new HashMap();
		jsonMap.put("totalProperty", "results," + page.getTotalCount());
		jsonMap.put("root", "rows");
		return JsonUtil.fromCollections(list, jsonMap);
	}

	/**
	 * <p>
	 * 方法名称: getList|描述: 查询日志列表
	 * </p>
	 * 
	 * @param tlog
	 * @param beginDate
	 * @param endDate
	 * @param page
	 * @return
	 * @throws Exception
	 */
	private List getList(TransLog tlog, Date beginDate, Date endDate, Page page) throws Exception {
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		sb.append("select tlog from TransLog as tlog where 1=1 ");
		if (null != tlog) {
			if (null != tlog.getMsgType() && tlog.getMsgType().trim().length() != 0) {
				sb.append(" and tlog.msgType=?");
				paras.add(tlog.getMsgType());
			} else {
				sb.append(" and tlog.msgType !=?");
				paras.add(Constants.MS_DAY_END);
			}
			if (null != tlog.getTxCode() && tlog.getTxCode().trim().length() != 0) {
				sb.append(" and tlog.txCode like ?");
				paras.add("%"+tlog.getTxCode()+"%");
			}
			if (null != tlog.getMsgId() && tlog.getMsgId().trim().length() != 0) {
				sb.append(" and tlog.msgId=?");
				paras.add(tlog.getMsgId());
			}
			if (null != tlog.getStatus() && tlog.getStatus().trim().length() != 0) {
				sb.append(" and tlog.status=?");
				paras.add(tlog.getStatus());
			}
			if (StringUtils.isNotBlank(tlog.getBillNo())) {
				sb.append(" and tlog.billNo=?");
				paras.add(tlog.getBillNo());
			}
		}
		if (null != beginDate) {
			sb.append(" and tlog.creDtTm>=?");
			paras.add(beginDate);
		}
		if (null != endDate) {
			sb.append(" and tlog.creDtTm<=?");
			paras.add(DateUtils.modDay(endDate, 1));
		}
		sb.append(" order by tlog.creDtTm desc");
		return find(sb.toString(), paras, page);
	}

	public TransLog txStoreSendMsg(String txCode, byte[] msg, ReturnMessageNew request) throws Exception {
		
		logger.info("保存发送报文日志....");
		TransLog tl = new TransLog();
		try {			
			tl.setMsgType(Constants.MS_SEND);
			tl.setTxCode(txCode);
			tl.setTxCodeStr(TransCodeMap.templateMapNew().getNameMap().get(txCode));
			// 交易日期
			tl.setTxDate(DateUtils.formatDate(new Date(), DateUtils.ORA_DATES_FORMAT));
			// 交易时间
			tl.setCreDtTm(DateUtils.formatDate(new Date(), DateUtils.ORA_DATE_TIMES3_FORMAT));
			tl.setStatus(Constants.MSG_SEND_UNSEND);
			tl.setMsgContent(msg);
			this.txStore(tl);
			
		} catch (Exception e) {
			logger.error("报文日志保存异常："+ e);
		}
		
		return tl;
	}

	public boolean txUpdateTransLogRec(TransLog tlog, boolean isSuccess, ReturnMessageNew request, String remark)
			throws Exception {
		if (tlog.getMsgType().equals(Constants.MS_SEND)) {// 发送交易
			if (isSuccess) {// 成功
				tlog.setStatus(Constants.MSG_SEND_SENDSUCC);
			} else {// 失败
				tlog.setStatus(Constants.MSG_SEND_SENDFAIL);
			}
			if(request.getSysHead().get("SERV_SEQ_NO") != null) {
				tlog.setMsgId((String) request.getSysHead().get("SERV_SEQ_NO"));// 主机流水号
			} else if(request.getSysHead().get("BUSS_SEQ_NO") != null){
				tlog.setMsgId((String) request.getSysHead().get("BUSS_SEQ_NO"));// 业务流水号
			} else if(request.getSysHead().get("CONSUMER_SEQ_NO") != null) {
				tlog.setMsgId((String) request.getSysHead().get("CONSUMER_SEQ_NO"));// 请求流水号
			} else {
				
			}
			// 暂时存请求系统编号
			tlog.setProdId((String) request.getSysHead().get("CONSUMER_ID"));
			// 获取交易说明
			tlog.setTxMark((String) request.getSysHead().get("SERVICE_CODE") + "|"
					+ (String) request.getSysHead().get("SERVICE_SCENE"));
		} else if (tlog.getMsgType().equals(Constants.MS_REC)) {// 接收交易
			if (isSuccess) {// 成功
				tlog.setStatus(Constants.MSG_REC_DONESUCC);
			} else {// 失败
				tlog.setStatus(Constants.MSG_REC_DONEFAIL);
			}
		}
		if(remark.length() >= 1500){
			remark.substring(0, 1499);
		}
		tlog.setRemark(remark);
		this.txStore(tlog);
		return true;
	}

	public List queryTransLog(Date txDate) throws Exception {
		StringBuffer sb = new StringBuffer();
		List paras = new ArrayList();
		sb.append("select t from TransLog as t where t.txDate =? ");
		paras.add(DateUtils.formatDate(txDate, DateUtils.ORA_DATES_FORMAT));
		return find(sb.toString(), paras);
	}

	
	@Override
	public List querySysReportMsgLogList(TransLog tlog, Date beginDate,Date endDate, Page page) {
		StringBuffer sb = new StringBuffer();

		List paramName = new ArrayList();// 名称
		List paramValue = new ArrayList();// 值
		if(StringUtils.isNotEmpty(tlog.getId())){
			sb.append("select tlog from TransLog as tlog where id = '"+tlog.getId()+"' ");
		}else{
			sb.append("select tlog.id,tlog.msgType,tlog.txCode,tlog.txCodeStr,tlog.txDate,tlog.msgId,tlog.txMark,tlog.status,tlog.creDtTm from TransLog as tlog where 1=1 ");
			
			if(StringUtils.isNotEmpty(tlog.getTxCode())){
				sb.append(" and tlog.txCode like :txCode");
				paramName.add("txCode");
				paramValue.add("%"+tlog.getTxCode()+"%");
			}
			if(StringUtils.isNotEmpty(tlog.getMsgId())){
				sb.append(" and tlog.msgId=:msgId");
				paramName.add("msgId");
				paramValue.add(tlog.getMsgId());
			}
			if(StringUtils.isNotEmpty(tlog.getMsgType())){
				sb.append(" and tlog.msgType=:msgType");
				paramName.add("msgType");
				paramValue.add(tlog.getMsgType());
			}
			if(StringUtils.isNotEmpty(tlog.getStatus())){
				sb.append(" and tlog.status=:status");
				paramName.add("status");
				paramValue.add(tlog.getStatus());
			}
			
			if(null != beginDate){//开始日期
				sb.append(" and tlog.txDate>=TO_DATE(:sDate, 'yyyy-mm-dd hh24:mi:ss')");
				paramName.add("sDate");
				paramValue.add(DateUtils.toString(beginDate, "yyyy-MM-dd") + " 00:00:00");
			}
			if(null != endDate){//结束日期
				sb.append(" and tlog.txDate<=TO_DATE(:eDate, 'yyyy-mm-dd hh24:mi:ss')");
				paramName.add("eDate");
				paramValue.add(DateUtils.toString(endDate, "yyyy-MM-dd") + " 23:59:59");
			}	
			sb.append(" order by tlog.creDtTm desc ");
		}


		String paramNames[] = (String[]) paramName.toArray(new String[paramName.size()]);
		Object paramValues[] = paramValue.toArray();
		List list = this.find(sb.toString(), paramNames,paramValues, page);
		if(StringUtils.isNotEmpty(tlog.getId())){
			return list;
		}else{
			List result = new ArrayList();
			if(list != null && list.size() > 0){
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[]) list.get(i);
					TransLog log = new TransLog();
					log.setId((String) obj[0]);
					log.setMsgType((String) obj[1]);
					log.setTxCode((String) obj[2]);
					log.setTxCodeStr((String) obj[3]);
					log.setTxDate((Date) obj[4]);
					log.setMsgId((String) obj[5]);
					log.setTxMark((String) obj[6]);
					log.setStatus((String) obj[7]);
					log.setCreDtTm((Date) obj[8]);
					
					result.add(log);
				}
			}
			return result;
		}
	}

	@Override
	public TransLog txUpdateLog(TransLog tl, byte[] msg, boolean flag) throws Exception {
		if(flag){
			tl.setRspMsgContent(msg);
		}else{
			tl.setMsgContent(msg);
		}
		this.txStore(tl);
		return tl;
	}
}
