package com.mingtech.application.audit.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.audit.domain.ApproveAuditBean;
import com.mingtech.application.audit.domain.ApproveAuditDto;
import com.mingtech.application.audit.domain.AuditResultDto;
import com.mingtech.application.ecds.common.domain.QueryBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
* <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
* @作者: ice
* @日期: 2019-07-15 上午10:28:06
* @描述: [AuditService]审批接口定义
*/
public interface AuditService extends GenericService{
	/**
	 * 【第一步 提交审批流】
	 * @param user 当前系统登录用户
	 * @param nodeNum 当前要提交的审批节点 默认为 1 第一岗，如有特殊需要 请传入岗位顺序号
	 * @param approveAudit 审批受理参数对象-所需参数说明如下：
	 * 一、必输参数说明：
	 * 1、auditType 审批类型 【如果为空 则为01 批次审批】参考 PublicStaticDefineTab AUDIT_TYPE_
	 * 2、busiType  业务类型【可以为空】 参考PublicStaticDefineTab AUDIT_BUSI_TYPE*****
	 * 3、busiId  业务清单id 
	 * 4、productId 产品号
	 * 5、applyNo 业务申请号(可以为批次号、票据明细、自定义编号)
	 * 二、当auditType=01批次、02票据明细时，以下参数为必输项
	 * 6、custName 客户(交易对手)名称
	 * 7、custCertNo 客户(交易对手)证件号码
	 * 8、custBankNm 客户(交易对手)开户行名称
	 * 9、auditAmt 审批金额
	 * 10、billType 票据类型
	 * 11、billMedia 票据介质
	 * 12、billTotalNum; 票据总笔数
	 * @param mvelDataMap 存放mvel表达式中使用的动态变量-常用参数存放Map的key值和数据类型说明如下:
	 * 1、金额：amount 类型：BigDecimal
	 * 2、业务笔数：totalNum 类型：int
	 * 
	 * 
	 * <p>20201016 审批优化 增加提交审批时可选择审批人</p>
	 * @param approverUserId 已选择的审批人id
	 * 
	 * @return AuditResultDto retCode 00成功；01 未找到审批路线配置；02审批金额过大 ，所有审批节点 都没有权限 03未找到审批人员
	 */
	public AuditResultDto txCommitApplyAudit(User user,String nodeNum,ApproveAuditBean approveAudit,Map mvelDataMap)throws Exception;
	
	/**
	 * 根据MVEL动态表达式查询 下一岗 审批 信息  【审批岗，点击审批按钮时查询 下一岗审批人 显示到审批页面】
	 * 1、如果  当前审批节点，配置了下一岗审批节点，但是找不到下一岗节点，会直接报错；
	 * 2、如果  有下一可用的审批节点，但是因为 金额不足，会直接提示 金额过大 ，所有人都无权审批；
	 * 3、其他如果是最后一个审批节点 则返回NULL
	 * 4、如果有下一节点，但是节点配置的角色下面 没有柜员，会直接报错；
	 * 5、其他正常情况下 会返回 带 有权人的 对象ApproveAuditDto---nextAuditUser 
	 * @param curUser  当前登录用户
	 * @param srcBusiId 原业务申请ID：批次/清单ID
	 * @param productId 产品号
	 * @return
	 * @throws Exception
	 */
	public ApproveAuditBean queryNextAuditInfo(User curUser,String srcBusiId,String productId)throws Exception;

	/**
	 * 【审批流程中 提交审批 】
	 * @param appAudit 审批受理对象
	 * @param auditMind 审批意见(0不同意、1同意)
	 * @param auditContent 审核意见
	 * @param user 当前用户
	 * @param nextUserId 选择的 下一岗审批人员ID列表，多个用户ID用户逗号,分隔
	 * @param curAuditNodeId 当前审批节点ID(从待审批查询列表中进行获取,用户检查当前审批节点可能已经被其他相同角色的用户审批)
	 * @return  
	 * 01:同意，流程正常结束
	 * 02:同意，流程 继续流转到 下一岗审批人；
	 * 03:拒绝，成功找到配置的拒绝岗  审批人，并将流程流转到 指定的拒绝岗；
	 * 04:拒绝，直接将审批流程结束，需要申请岗重新发起审批流程；
	 * @throws Exception
	 * 
	 * <p> 20201016 审批优化  增加驳回时可选驳回节点<p/>
	 * @param rejustNodeNum   驳回节点 
	 * @param rejustUserId    已选择的审批人ID
	 * 
	 */
	public String txCommitAudit(ApproveAuditDto appAudit,String auditMind, String auditContent,User user,String nextUserId,String curAuditNodeId,String rejustNodeNum,String rejustUserId) throws Exception;
	
	/**
	 * 撤销审批 使用  
	 * 查询  当前开启的审批流，并删除
	 * @param productId 产品号
	 * @param busiId 原业务id
	 * @return
	 */
	public void txCommitCancelAudit(String productId,String srcBusiId)throws Exception;
	
	/**
	 * 查询审批待受理业务信息
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception
	 */
	public List queryAuditAcceptsBusiness(User user, QueryBean queryBean, Page page) throws Exception;
	
	/**
	 * 根据id查询审批受理申请信息
	 * @param id 审批受理申请信息ID
	 * @return ApproveAuditDto 审批受理申请
	 * @throws Exception
	 */
	public ApproveAuditDto getApproveAuditDtoById(String id)throws Exception;
	
	/**
	 * 根据审批受理申请id或原业务id查询该笔业务审核路线
	 * @param id 审批受理申请信息ID
	 * @param srcBusiId 原业务ID
	 * @return Map key=auditInf value=审批受理信息；key=nodes value=审核批节点信息
	 * @throws Exception
	 */
	public Map<String,Object>  queryAuditBusiAuditRoute(String id,String srcBusiId,User user)throws Exception;
	
	/**
	 * 根据原业务id、审批受理申请id查询该业务审批结果
	 * @param srcBusiId 原业务ID
	 * @param auditId 审批受理申请id
	 * @return List 审核路线
	 * @throws Exception
	 */
	public List queryAuditResultByBusiIdAndAuditId(String srcBusiId,String auditId,Page page)throws Exception;
	

	/**
	 * 通用审批方法
	 * @param user 当前登录用户
	 * @param approveId 审批受理申请id
	 * @param auditComment 审批意见
	 * @param curAuditNodeId 当前审批节点id
	 * @param nextUserId 下一岗审批人ID，多个id使用,逗号分隔
	 * @throws Exception
	 */
	public String txCommonCommitAudit(User user,String approveId,String approveFlag,String approveComment,String curAuditNodeId,String nextUserId,String rejustNodeNum,String rejustUserId)throws Exception;
	
	/**
	 * 终止审批
	 * @param user 当前登录用户
	 * @param id 审批受理id
	 * @param auditComment 审批意见
	 * @throws Exception
	 */
	public void txStopAudit(User user,String id,String auditComment)throws Exception;
	
	/**
	 * 首页待办任务查询
	 * @param user 当前登录用户
	 * @param list 待审批业务统一信息
	 * @throws Exception
	 */
	public List queryAuditTask(User user)throws Exception;
	

	/**
	 * 查询历史审批业务
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception	 
	 * */
	public List queryHistoryAuditBusiness(User user,QueryBean queryBean, Page page) throws Exception;
	
	
	/**
	 * 查询审批流程
	 * @param user 当前登录用户
	 * @param queryBean 查询条件
	 * @param page 分页对象
	 * @return list 审批待受理业务信息
	 * @throws Exception	 
	 * */
	public List queryAuditProcess(User user,QueryBean queryBean, Page page) throws Exception;
	
	/**
	 * 根据id查询审批信息
	 * @param id 审批id
	 * @throws Exception	 
	 * */
	public ApproveAuditDto queryAuditInfoById(String id) throws Exception;
	

	/**
	 * <p>方法说明:根据审核路线id查询配置的请求参数</p>
	 * @param routeId
	 * @return list 审批路线请求参数
	 */
	public List queryApproveParamtersByApproveAuditId(String approveAuditId,Page pg) throws Exception;
	
	/**
	 * 查询在审批流中的信息
	 * @param productId
	 * @param busiId
	 * @return
	 * @throws Exception
	 */
	public List getOpenApproveAuditDto(String productId,String busiId) throws Exception;
	
	/**
	 * 根据当前用户机构信及产品信息查询提交审批时可选择的审批人
	 * @param productId
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List<User> queryChooseSubmit(String productId,User user) throws Exception;
	/**
	 * 查询 打开的 审批流程 对象
	 * @param productId 产品号
	 * @param busiId  业务ID
	 * @return
	 */
	public ApproveAuditDto getOpenApproveAudit(String productId,String busiId);
}
