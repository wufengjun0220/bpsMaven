package com.mingtech.application.pool.online.manage.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.message.domain.TMessageRecord;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.manage.domain.PedOnlineBlackInfo;
import com.mingtech.application.pool.online.manage.domain.PedOnlineHandleLog;
import com.mingtech.application.pool.online.manage.domain.PedOnlineMsgInfo;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface OnlineManageService extends GenericService {

	/**
	 * 
	 * @author wss
	 * @date 2021-4-27
	 * @description 查询短信通知人
	 * @param onlineAcptNo 在线协议编号
	 */
	public List<PedOnlineMsgInfo> queryOnlineMsgInfoList(String onlineNo,String role);

	/**
	 * @author wss
	 * @throws Exception 
	 * @date 2021-4-30
	 * @description 处理短信通知人信息
	 */
	public Ret txSaveMsgInfo(OnlineQueryBean queryBean) throws Exception;
	
	/**
	 * @author wss
	 * @date 2021-4-30
	 * @description
	 */
	public List queryOnlineMsgInfoList(OnlineQueryBean queryBean,Page page);

	/**
	 * @author wss
	 * @date 2021-5-6
	 * @param queryBean
	 * @description 查询黑名单
	 */
	public List queryBlackList(OnlineQueryBean queryBean);
	
	/**
	 * @author wss
	 * @date 2021-5-6
	 * @param queryBean
	 * @description 校验黑名单
	 */
	public boolean onlineBlackListCheck(OnlineQueryBean queryBean);
	/**
	 * @Title 保存交易日志
	 * @author wss
	 * @date 2021-5-13
	 * @param billNo 票号、批次号、序列号
	 * @param busiId 业务id
	 * @param errorType 0提示、1禁止
	 * @param operationType 经办CZLX_01、复核CZLX_02
	 * @param tradeName 渠道 信贷、电票、lpr、网银、核心、智慧宝、消息中心 
	 * @param tradeResult 结果
	 * @param tradeCode 交易编码
	 * @param busiName 业务名称
	 * @param sendType 收发类型
	 * @param custNo 客户号
	 * @param bspNo 票据池编号
	 */
	public void txSaveTrdeLog(String custNo,String bpsNo,String billNo, String busiId,String errorType, String operationType,
			String tradeName, String tradeResult, String tradeCode,
			String busiName, String sendType);
	
	/**
	 * 在线业务交易日志保存
	 * @param log
	 * @author Ju Nana
	 * @date 2021-7-16下午5:39:55
	 */
	public void txSaveTrdeLog(PedOnlineHandleLog log);
	
	/**
	 *  根据ID查询PedOnlineMsgInfo 短信联系人表
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public PedOnlineMsgInfo loadByPedOnlineMsgInfoId(String id) throws Exception;
	
	/**
	 * @Title txSendMsg
	 * @author wss
	 * @date 2021-5-28
	 * @Description 短信通知
	 * @param role 角色
	 * @param custName 客户名称
 	 * @param phoneNo 通知人电话
	 * @param busiType 业务类型
	 * @param totalAmt 总金额
	 * @param succAmt 成功金额
	 * @param succFlag 成功\失败
	 * @param AddresseeName 联系人名称
	 * @param onlineNo 在线协议编号
	 * @throws Exception 
	 */
	public void txSendMsg(String role, String custName,
			String phoneNo, String busiType, BigDecimal totalAmt,
			BigDecimal succAmt,boolean succFlag,String addresseeName,String onlineNo) throws Exception;
	/**
	 * @Title queryOnlineAgreementList
	 * @author wss
	 * @date 2021-6-7
	 * @Description 查询在线业务协议表
	 * @return List
	 * @throws Exception 
	 */
	public List txqueryOnlineAgreementList(OnlineQueryBean bean, User user, Page page) throws Exception;
	/**
	 * @Title txSaveOrUpdateBlackList
	 * @author wss
	 * @date 2021-6-8
	 * @Description 保存或修改在线业务禁入名单
	 * @return void
	 * @throws Exception 
	 */
	public void txSaveOrUpdateBlackList(PedOnlineBlackInfo info) throws Exception;
	/**
	 * @Title queryOnlineAgreementHistList
	 * @author wss
	 * @date 2021-6-9
	 * @Description 在线协议历史查询
	 * @return List
	 * @throws Exception 
	 */
	public List queryOnlineAgreementHistList(OnlineQueryBean bean, User user, Page page) throws Exception;
	/**
	 * @Title queryOnlineBusiList
	 * @author wss
	 * @date 2021-6-9
	 * @Description 在线业务综合查询
	 * @return List
	 * @throws Exception 
	 */
	public List queryOnlineBusiList(OnlineQueryBean bean, Page page) throws Exception;
	/**
	 * 在线银承业务综合查询
	 * @throws Exception 
	 */
	public List queryOnlineAcptList(OnlineQueryBean queryBean, User user, Page page) throws Exception ;
	/**
	 * 在线银流贷务综合查询
	 * @throws Exception 
	 */
	public List queryOnlineCrdtList(OnlineQueryBean queryBean, User user, Page page) throws Exception;
	/**
	 * @Title queryErrorLogList
	 * @author wss
	 * @date 2021-6-10
	 * @Description 查询业务日志信息
	 * @return List
	 */
	public List queryErrorLogList(OnlineQueryBean bean, Page page);
	/**
	 * @Title queryHandleLog
	 * @author wss
	 * @date 2021-6-10
	 * @Description 获取错误日志内容
	 * @return String
	 */
	public String queryHandleLog(OnlineQueryBean bean);
	/**
	 * @Title queryOnlineMsgHist
	 * @author wss
	 * @date 2021-6-11
	 * @Description 查询短信通知人历史
	 * @return List
	 */
	public List queryOnlineMsgHist(OnlineQueryBean bean, Page page);
	/**
	 * @Title calculateDueDate
	 * @author wss
	 * @date 2021-6-21
	 * @Description 根据类型和数值计算 到期日
	 * @return Date 
	 * @param validDate 数值
	 * @param validDateType 时间类型
	 * @param startDate 起始日
	 */
	public String calculateDueDate(String validDate, String validDateType,Date startDate);
	/**
	 * @Title checkOnlineSwitch
	 * @author wss
	 * @date 2021-7-1
	 * @Description 银承业务开关
	 * @param branchNo 业务机构
	 * @return ret
	 */
	public Ret checkOnlineSwitch(String branchNo,String busiType) throws Exception;
	/**
	 * @param role 角色
	 * @param phoneNo 通知人电话
	 * @param busiType 业务类型
	 * @param Template 通知内容
	 * @param AddresseeName 联系人名称
	 * @param onlineNo 在线协议编号
	 * @throws Exception
	 * @author wfj
	 * @date 2021-07-31 11:40:39
	 */
	public TMessageRecord toSendMsgForNotifier(String role,String phoneNo,String busiType,String Template,String addresseeName,String onlineNo) throws Exception;
	
	/**
	 * @param custNo 客户号
	 * @throws Exception
	 * @author wfj
	 * @date 2021-11-5 11:40:39
	 */
	public String toQueryCustorForCore(String custNo) throws Exception;
	
	/**
	 * 在线协议查询数据导出
	 * @param bean 
	 * @throws Exception
	 * @author wfj
	 * @date 2021-12-13 
	 */
	public List findOnlineListExpt(List res, Page page) throws Exception;

	/**
	 * 在线协议历史查询数据导出
	 * @param bean 
	 * @throws Exception
	 * @author wfj
	 * @date 2021-12-13 
	 */
	public List findOnlineListHistExpt(List res, Page page) throws Exception;
	
	/**
	 * 在线流贷业务数据导出
	 * @param bean 
	 * @throws Exception
	 * @author wfj
	 * @date 2021-12-13 
	 */
	public List findOnlineCrdtListExpt(List res, Page page) throws Exception;
	
	/**
	 * 在线银承业务数据导出
	 * @param bean 
	 * @throws Exception
	 * @author wfj
	 * @date 2021-12-13 
	 */
	public List findOnlineAcptListExpt(OnlineQueryBean queryBean, User user, Page page) throws Exception;
}
