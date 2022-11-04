package com.mingtech.application.autotask.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.service.TaskDispatchConfigService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.ecds.draftcollection.service.ConsignService;
import com.mingtech.application.pool.bank.bbsp.service.PoolEcdsService;
import com.mingtech.application.pool.bank.coresys.domain.ECDSPoolTransNotes;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.domain.TMessageRecord;
import com.mingtech.application.pool.common.PoolComm;
import com.mingtech.application.pool.common.domain.PoolBillInfo;
import com.mingtech.application.pool.draft.domain.DraftPool;
import com.mingtech.application.pool.draft.domain.DraftPoolIn;
import com.mingtech.application.pool.draft.domain.DraftPoolOut;
import com.mingtech.application.pool.draft.domain.DraftQueryBean;
import com.mingtech.application.pool.draft.domain.PlDiscount;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.pool.draft.service.DraftPoolDiscountServer;
import com.mingtech.application.pool.draft.service.DraftPoolInService;
import com.mingtech.application.pool.draft.service.DraftPoolOutService;
import com.mingtech.application.pool.draft.service.DraftPoolQueryService;
import com.mingtech.application.pool.edu.domain.PedCreditDetail;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptBatch;
import com.mingtech.application.pool.online.acception.domain.PlOnlineAcptDetail;
import com.mingtech.application.pool.online.acception.service.PedOnlineAcptService;
import com.mingtech.application.pool.online.common.domain.OnlineQueryBean;
import com.mingtech.application.pool.online.loan.domain.PlCrdtPayList;
import com.mingtech.application.pool.online.loan.domain.PlOnlineCrdt;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.utils.AutoTaskNoDefine;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("autoTaskExeService")
public class AutoTaskExeServiceImpl extends GenericServiceImpl implements
		AutoTaskExeService {
	private static final Logger logger = Logger
			.getLogger(AutoTaskExeServiceImpl.class);

	@Autowired
	private TaskDispatchConfigService taskDispatchConfigService;
	@Autowired
	private AutoTaskPublishService autoTaskPublishService;
	@Autowired
	DraftPoolInService draftPoolInService;
	@Autowired
	PoolEcdsService poolEcdsService;
	@Autowired
	DraftPoolQueryService draftPoolQueryService;
	@Autowired
	DraftPoolOutService draftPoolOutService;
	@Autowired
	PedOnlineAcptService acptService;
	@Autowired
	DraftPoolDiscountServer draftPoolDiscountServer;
	@Autowired
	ConsignService consignService;

	/**
	 * 根据ID获取任务执行流水信息
	 * @param id 自动任务执行流水ID
	 */
	public AutoTaskExe getAutoTaskExeById(String id) {
		StringBuffer hql = new StringBuffer(" from AutoTaskExe where id=? ");
		List values = new ArrayList();
		values.add(id);
		List list = this.find(hql.toString(),values);
		if(list.size() > 0) {
			return (AutoTaskExe)list.get(0);
		}else {
			return null;
		}
	}
	
	
	/**
	 * 根据任务编号将指定业务执行所需的所有任务信息保存到自动任务流水表中，并返回主任务信息
	 * @param memberCode 会员编码
	 * @param taskNo 任务编号
	 * @param busiId 原业务ID
	 * @param productId 业务类型ID
	 * @param reqParams 任务调度请求参数
	 * @return AutoTaskExe 主任务执行流水信息,返回null说明
	 */
	public AutoTaskExe txSaveAutoTaskExe(String memberCode,String taskNo,String busiId,String productId,Map<String,String> reqParams,
										 String batchNo,String bpsNo,String bpsName,String depId )throws RuntimeException {
		
		memberCode =  memberCode == null ? "0" : memberCode;//汉口没有设定会员编码，这里默认取0
		
		//根据会员编码和主任务编号查询主任务配置信息
		TaskDispatchConfig mainTaskDispathcConf = taskDispatchConfigService.getTaskDispatchConfigByMemberCodeAndTaskNo(memberCode,taskNo);
		if(mainTaskDispathcConf == null) {
			throw new RuntimeException("没有找到主任务配置信息");
		}
		AutoTaskExe mainAutoTaskExe = this.createAutoTaskExe(mainTaskDispathcConf, busiId, productId, reqParams,batchNo,bpsNo,bpsName,depId);
		
		//保存主任务执行调度信息
		this.txStore(mainAutoTaskExe);
		
		List<AutoTaskExe> autoTaskExeList = new ArrayList<AutoTaskExe>();//存放自动任务执行调度信息-用于批量保存使用
		//查询所有下级子任务调度配置信息
		List<TaskDispatchConfig> childTaskDispatchConfList = taskDispatchConfigService.queryAllChildTaskDispatchConfigs(memberCode,mainTaskDispathcConf.getId(),taskNo);
		for(TaskDispatchConfig  childTaskDispatchConf : childTaskDispatchConfList) {
			autoTaskExeList.add(this.createAutoTaskExe(childTaskDispatchConf, busiId, childTaskDispatchConf.getProductId(), reqParams,batchNo,bpsNo,bpsName,depId));
		}
		if(autoTaskExeList.size()>0){
			this.txStoreAll(autoTaskExeList);//保存子任务执行调度信息
		}
		return mainAutoTaskExe;
	}
	
	/**
	 *根据任务配置类型和原业务信息创建自动任务执行调度对象
	 *@param taskDispathcConf 任务调度配置
	 *@param busiId 原业务ID
	 *@param productId 业务类型ID
	 *@param reqParams 任务调度请求参数
	 *@return  AutoTaskExe 自动任务执行调度对象
	 */
	private AutoTaskExe createAutoTaskExe(TaskDispatchConfig taskDispathcConf,String busiId,String productId,Map<String,String> reqParams,
											String batchNo,String bpsNo,String bpsName,String depId) {
		AutoTaskExe autoTaskExe = new AutoTaskExe();
		autoTaskExe.setMemberCode(taskDispathcConf.getMemberCode());//会员编码
		autoTaskExe.setTaskId(taskDispathcConf.getId());//任务调度配置ID
		Map<String, String> reqParam =new HashMap<String,String>();
		if(reqParams==null){
			reqParam.put("busiId", busiId);
		}
		autoTaskExe.setReqParams( JSON.toJSON(reqParams==null?reqParam:reqParams).toString());//请求参数
		autoTaskExe.setProductId(productId);//业务类型ID
		autoTaskExe.setBusiId(busiId);//原业务ID
		autoTaskExe.setTreeCode(taskDispathcConf.getTreeCode());//树节点 2021528 新增
		autoTaskExe.setProceStatus(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_INIT);//流程状态默认未处理
		autoTaskExe.setBatchNo(batchNo);//批次号
		autoTaskExe.setBpsNo(bpsNo);//票据池协议号
		autoTaskExe.setBpsName(bpsName);//票据池协议名称
		if(StringUtils.isNotBlank(depId)){
			Department dept = (Department) this.load(depId,Department.class);
			autoTaskExe.setDeptId(depId);//操作机构id
			autoTaskExe.setDeptName(dept.getName());//操作机构名称
		}
	    return autoTaskExe;
	}
	
	
	
	/**
	 * 更新重新处理次数
	 * @param id 主键
	 * @return void
	 */
	public void txUpdateAutoTaskExeResetCount(String id){
		try{
			//这里进行逻辑删除
			StringBuffer sql = new StringBuffer("update BT_AUTOTASK_EXE set RESET_COUNT=RESET_COUNT+1  ");
			sql.append(	"where");
			sql.append(" id='").append(id).append("'");
			dao.updateSQL(sql.toString());
		}catch(Exception e){
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}
	
	/**
	 * 更新自动任务执行业务状态和业务描述
	 * @param id 主键
	 * @param status 状态
	 * @param respDesc 交易描述
	 * @return void
	 */
	public void txUpdateAutoTaskExeStatus(String id,String status,String respDesc){   
		try{
			//这里进行逻辑删除
			StringBuffer sql = new StringBuffer("update BT_AUTOTASK_EXE set STATUS=");
			sql.append("'").append(status).append("'");
			if(StringUtils.isNotBlank(respDesc)){
				if(respDesc.length() > 150) {
					logger.info("自动任务处理结果超长，进行结束-"+respDesc);
					respDesc = respDesc.substring(0,150);
				}
			   sql.append(",RESULT_DESC='"+respDesc+"'  ");
			}
			sql.append(" where");
			sql.append(" id='").append(id).append("'");
			dao.updateSQL(sql.toString());
		}catch(Exception e){
			logger.error(ErrorCode.ERR_MSG_996,e);
		}
	}
	
	
	/**
	 * 根据原业务ID和任务调度配置ID更新自动任务执行流水请求参数
	 * @param busiId 原业务ID
	 * @param taskIds任务调度配置信息ID
	 * @param reqParams 自动任务执行流水请求参数
	 * @return list 已更新的自动任务流水ID
	 */
	public List txUpdateNextAutoTaskExes(String busiId,List taskIds,String reqParams) {
		StringBuffer hql = new StringBuffer(" from AutoTaskExe where busiId=:busiId and taskId in (:taskIds)");
		List keyList = new ArrayList();
		List values = new ArrayList();
		keyList.add("busiId");
		values.add(busiId);
		keyList.add("taskIds");
		values.add(taskIds);
		String[] keyArray = (String[]) keyList.toArray(new String[keyList.size()]);
		List list = this.find(hql.toString(),keyArray,values.toArray());
		int count = list.size();
		for(int i=0; i<count; i++) {
			AutoTaskExe  autoTaskExe = (AutoTaskExe)list.get(i);
			autoTaskExe.setReqParams(reqParams);
		}
		dao.storeAll(list);
		return list;
	}
	
	
	/**
	 * 更新自动任务执行调度开始时间
	 * @param id 主键
	 * @return void
	 */
	public void  txUpdateAutoTaskExeStartTime(String id) {
		AutoTaskExe  autoTaskExe = this.getAutoTaskExeById(id);
		autoTaskExe.setStartDate(new Date());
		this.txStore(autoTaskExe);
	}
	/**
	 * 更新自动任务执行调度结束时间
	 * @param id 主键
	 * @return void
	 */
	public void  txUpdateAutoTaskExeEndTime(String id) {
		AutoTaskExe  autoTaskExe = this.getAutoTaskExeById(id);
		autoTaskExe.setEndDate(new Date());
		this.txStore(autoTaskExe);
	}
	

	public Class getEntity() {
		return  AutoTaskExe.class;
	}

	public Class getEntityClass() {
		return AutoTaskExe.class;
	}

	public String getEntityName() {
		
		return "AutoTaskExe";
	}
	/**
	 * 根据条件查询 AutoTaskExe主任务执行流水信息表
	 * @param PoolQueryBean  gcj 20210517
	 * @return AutoTaskExe 主任务执行流水信息
	 */
	public AutoTaskExe queryAutoTaskExeByParm(PoolQueryBean bean){
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select p from AutoTaskExe as p where 1=1 ");
		
		if (StringUtil.isNotBlank(bean.getBusinessId())) {//业务主键ID
			sb.append(" and p.id =:id");
			keys.add("id");
			values.add(bean.getBusinessId());
		}
		if(StringUtil.isNotBlank(bean.getTaskId())){//任务调度配置表ID
			sb.append(" and p.taskId =:taskId");
			keys.add("taskId");
			values.add(bean.getTaskId());
		}
		if(StringUtil.isNotBlank(bean.getProductId())){//产品ID
			sb.append(" and p.productId =:productId");
			keys.add("productId");
			values.add(bean.getProductId());
		}
		if(StringUtil.isNotBlank(bean.getMemberCode())){//会员号
			sb.append(" and p.memberCode =:memberCode");
			keys.add("memberCode");
			values.add(bean.getMemberCode());
		}
		if(StringUtil.isNotBlank(bean.getSStatusFlag())){//状态
			sb.append(" and p.status =:status");
			keys.add("status");
			values.add(bean.getSStatusFlag());
		}
		if(StringUtil.isNotBlank(bean.getDelFlag())){//逻辑删除标记
			sb.append(" and p.delFlag =:delFlag");
			keys.add("delFlag");
			values.add(bean.getDelFlag());
		}
		if(StringUtil.isNotBlank(bean.getBusiId())){//原业务id
			sb.append(" and p.busiId =:busiId");
			keys.add("busiId");
			values.add(bean.getBusiId());
		}
		sb.append(" order by  p.createDate desc");
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list = this.find(sb.toString(), paramNames, paramValues);
		if(list!= null && list.size() >0){
			return (AutoTaskExe) list.get(0);
		}
		return null;

	}
	
    /**
     * <p>根据队列节点 队列名称 业务ID 查找自动任务业务表<p/>
     * @param queueNode 队列节点
     * @param queueName 队列名称
     * @param busiId 业务ID
     * @return AutoTaskExe
     */
    public AutoTaskExe doAutoTaskExe(String queueNode,String queueName,String busiId){
    	PoolQueryBean query = new PoolQueryBean();
    	query.setQueueNode(queueNode);//队列节点
    	query.setQueueName(queueName);//队列名称
    	TaskDispatchConfig config =taskDispatchConfigService.queryTaskDispatchConfigByParm(query);
    	if(null==config){
    		return null;
    	}
    	PoolQueryBean poolQuery = new PoolQueryBean();
    	poolQuery.setBusiId(busiId);//业务ID
    	poolQuery.setTaskId(config.getId());
    	AutoTaskExe autoTaskExe=this.queryAutoTaskExeByParm(poolQuery);
		return autoTaskExe;
    	
    }
    
    /**
     * <p>根据队列节点 队列名称 业务ID 查找自动任务业务表<p/>
     * @param queueNode 队列节点
     * @param queueName 队列名称
     * @param busiId 业务ID
     * @param status 状态
     * @return AutoTaskExe
     */
    public AutoTaskExe doAutoTaskExe(List queueNode,List queueName,String busiId,String status){
    	StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select p from AutoTaskExe p , TaskDispatchConfig t where p.taskId = t.id and p.delFlag='N' ");
		if(null != queueNode && queueNode.size()>0){
			sb.append(" and t.queueNode in(:queueNode)");
			keys.add("queueNode");
			values.add(queueNode);
		}
		if(null != queueName && queueName.size()>0){
			sb.append(" and t.queueName in(:queueName)");
			keys.add("queueName");
			values.add(queueName);
		}
		if(StringUtils.isNotBlank(busiId)){
			sb.append(" and p.busiId =:busiId");
			keys.add("busiId");
			values.add(busiId);
		}
		if(StringUtils.isNotBlank(status)){
			sb.append(" and p.status =:status");
			keys.add("status");
			values.add(status);
		}
    	
		sb.append(" order by  p.createDate desc");
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list = this.find(sb.toString(), paramNames, paramValues);
		if(list!= null && list.size() >0){
			return (AutoTaskExe) list.get(0);
		}
		return null;
    }
    
    /**
     * 通过自动任务业务流水ID  删除所有任务  逻辑删除
     * @param id AutoTaskExe中主键
     * @return void
     * @throws Exception 
     */
    public void txUpdateAutoTaskExeDelFlag(String treeCode,String queueName,String busiId) throws Exception{
    	PoolQueryBean query = new PoolQueryBean();
    	query.setQueueNode(treeCode);//队列节点
    	query.setQueueName(queueName);//队列名称
    	TaskDispatchConfig config =taskDispatchConfigService.queryTaskDispatchConfigByParm(query);
    	if(null == config){
    		throw new Exception("当前任务配置类未查询到！");
    	}
    	PoolQueryBean poolQuery = new PoolQueryBean();
    	poolQuery.setBusiId(busiId);//业务ID
    	poolQuery.setTaskId(config.getId());
    	AutoTaskExe autoTaskExe=this.queryAutoTaskExeByParm(poolQuery);
    	if(null == autoTaskExe){
    		throw new Exception(treeCode+"当前任务类未查询到！");
    	}
    	
    	//该业务队列中的所有任务
    	List<TaskDispatchConfig> taskDispatchConfList = taskDispatchConfigService.queryAllTaskDispatchConfigs("0",config.getTreeCode());//截取前13位
    	for(TaskDispatchConfig  taskDispatchConf : taskDispatchConfList) {
    		PoolQueryBean queryBean = new PoolQueryBean();
    		queryBean.setBusiId(autoTaskExe.getBusiId());//配置表ID
    		queryBean.setTaskId(taskDispatchConf.getId());
    		queryBean.setProductId(taskDispatchConf.getProductId());
    		AutoTaskExe taskExe=this.queryAutoTaskExeByParm(queryBean);
    		taskExe.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_YES);////已删除
    		this.txStore(taskExe);
    	}
    }
	/**
	 * 通过自动任务业务流水ID  删除所有任务  逻辑删除
	 * @param id AutoTaskExe中主键
	 * @return void
	 */
	public void txUpdateAutoTaskExeDelFlag(String id){
		AutoTaskExe autoTaskExe=this.getAutoTaskExeById(id);
		PoolQueryBean query = new PoolQueryBean();
    	query.setIds(autoTaskExe.getTaskId());//配置表ID
    	TaskDispatchConfig taskDispatchConfig=taskDispatchConfigService.queryTaskDispatchConfigByParm(query);//当前任务配置
    	//该业务队列中的所有任务
		List<TaskDispatchConfig> taskDispatchConfList = taskDispatchConfigService.queryAllTaskDispatchConfigs("0",taskDispatchConfig.getTreeCode().substring(0, 13));//截取前13位
		for(TaskDispatchConfig  taskDispatchConf : taskDispatchConfList) {
			PoolQueryBean queryBean = new PoolQueryBean();
			queryBean.setBusinessId(autoTaskExe.getBusiId());//配置表ID
			queryBean.setTaskId(taskDispatchConf.getId());
			queryBean.setProductId(taskDispatchConf.getProductId());
			AutoTaskExe taskExe=this.queryAutoTaskExeByParm(queryBean);
			taskExe.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_YES);////已删除
			this.txStore(taskExe);
		}
	}
    
	  /**
     * 通过参数更新自动业务流水表
	   * @param memberCode 会员编码
	   * @param queueNode 任务节点
	   * @param busiId 原业务ID
	   * @param productId业务类型(自定义业务类型，方便页面显示)
	   * @param reqParams 任务调度请求参数
	   * @return AutoTaskExe
     */
	public AutoTaskExe txAutoTaskExeInfo(String memberCode,String queueNode,String busiId,String productId,Map<String,String> reqParams){
		
		PoolQueryBean query=new PoolQueryBean();
		query.setQueueNode(queueNode);//任务编号
		TaskDispatchConfig  taskDispatchConf = taskDispatchConfigService.queryTaskDispatchConfigByParm(query);
		PoolQueryBean bean=new PoolQueryBean();
		bean.setMemberCode(memberCode);//会员号
		bean.setBusiId(busiId);//原业务ID
	    bean.setProductId(productId);//业务类型
	    //bean.setSStatusFlag("0");//业务状态
		bean.setTaskId(taskDispatchConf.getId());//配置表ID
		bean.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
		AutoTaskExe autoTaskExe =this.queryAutoTaskExeByParm(bean);
		if(autoTaskExe != null && autoTaskExe.getResetCout()>0){//手工执行时需要置零
			autoTaskExe.setResetCout(0);//重新执行
			taskDispatchConfigService.txStore(autoTaskExe);
		}
		//autoTaskExe.setReqParams(JSON.toJSON(reqParams).toString());//请求参数
		//业务请求参数累加可以放开
//		JSONObject jsonOne = JSON.parseObject(autoTaskExe.getReqParams());
//		JSONObject jsonTwo = JSON.parseObject(JSON.toJSON(reqParams).toString());
//		JSONObject jsonThree = new JSONObject();
//		jsonThree.putAll(jsonOne);
//	    jsonThree.putAll(jsonTwo);
//		autoTaskExe.setReqParams(jsonThree.toString());//请求参数
		
		
		return autoTaskExe;
	}
	
	/**
	 * 更新自动任务出错次数
	 * @param id 主键
	 * @return void
	 */
	public void  txUpdateAutoTaskExeErrorCount(String id) {
		AutoTaskExe  autoTaskExe = this.getAutoTaskExeById(id);
		autoTaskExe.setErrorCount(autoTaskExe.getErrorCount()+1);
		this.txStore(autoTaskExe);
	}


	/**
	 * 变更任务执行次数
	 */
	public void txUpdateErrorCount(AutoTaskExe autoTaskExe) {
		autoTaskExe.setErrorCount(autoTaskExe.getErrorCount()+1);
		this.txStore(autoTaskExe);
	}

	/**
	 * @Title txUpdateProceStatus
	 * @author zjt
	 * @date 2021-6-2
	 * @Description 调度流程中变更流程状态
	 * @return void
	 */
	public void txUpdateProceStatus(AutoTaskExe autoTaskExe,String status,String respDesc){
		if(StringUtils.isNotBlank(respDesc)){
			if(respDesc.length() > 150) {
				logger.info("自动任务处理结果超长，进行结束-"+respDesc);
				respDesc = respDesc.substring(0,150);
			}
		}else{
			respDesc = "";
		}
		Boolean flag = true;
		//找到对应的执行主任务
		AutoTaskExe  exe = this.queryAutoTaskExe(autoTaskExe.getMemberCode(),autoTaskExe.getTreeCode().substring(0,10),autoTaskExe.getBusiId());

		List<String> list = this.queryAutoTaskExeByTree(autoTaskExe.getId(),exe.getMemberCode(),exe.getTreeCode(),autoTaskExe.getBusiId());
		if(list!= null && list.size()>0){
			if(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR.equals(status)){//若为失败，直接更新为失败
				exe.setProceStatus(status);
				exe.setResultDesc(respDesc);
				return;
			}
			if(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC.equals(status)){//若为成功，则查询其他任务是否也已成功
				for(int i=0;i<list.size();i++ ){
					String stu = list.get(i);
					if (!PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC.equals(stu)){
						flag = false;
						break;
					}
				}
				if (flag){
					exe.setProceStatus(status);
					exe.setResultDesc(respDesc);
				}
			}
			//若为处理中,则判断是否主任务的流程状态为失败状态，若无，则直接更新
			if(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_RUNNING.equals(status) &&
					!PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR.equals(exe.getProceStatus())){
				exe.setProceStatus(status);
			}
		}else {
			exe.setProceStatus(status);
			exe.setResultDesc(respDesc);
		}
		this.txStore(exe);

	}
	/**
	 * @Title queryAutoTaskExe
	 * @author zjt
	 * @date 2021-6-2
	 * @Description 根据会员编号和树编号找到对应的任务执行信息
	 * @return AutoTaskExe
	 */
	@Override
	public AutoTaskExe queryAutoTaskExe(String memberCode, String treeCode,String busiId) {
		StringBuffer hql = new StringBuffer(" from AutoTaskExe where memberCode=?  and treeCode = ? and busiId= ? ");
		List values = new ArrayList();
		values.add(memberCode);
		values.add(treeCode);
		values.add(busiId);
		List list  = this.find(hql.toString(),values);
		if (null!=list && list.size()>0){
			return (AutoTaskExe) list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * @Title queryAutoTaskExe
	 * @author zjt
	 * @date 2021-6-2
	 * @Description 根据会员编号和树编号找到主任务对应的子任务
	 * @return AutoTaskExe
	 */
	public List queryAutoTaskExeByTree(String id,String memberCode, String treeCode,String busiId) {
		StringBuffer hql = new StringBuffer("select status  from AutoTaskExe where memberCode=?  and treeCode like ? and id!= ? and busiId= ?");
		List values = new ArrayList();
		values.add(memberCode);
		values.add("%"+treeCode+"%");
		values.add(id);
		values.add(busiId);
		return this.find(hql.toString(),values);
	}

	/**
	 * @Title queryAutoTaskExeByCondition
	 * @author wss
	 * @date 2021-6-4
	 * @Description 根据条件查询任务
	 * @return AutoTaskExe
	 */
	public List queryAutoTaskExeByCondition(PoolQueryBean bean) {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select p from AutoTaskExe as p where 1=1 ");
		
		if (StringUtil.isNotBlank(bean.getBusinessId())) {//业务主键ID
			sb.append(" and p.id =:id");
			keys.add("id");
			values.add(bean.getBusinessId());
		}
		if(StringUtil.isNotBlank(bean.getTaskId())){//任务调度配置表ID
			sb.append(" and p.taskId =:taskId");
			keys.add("taskId");
			values.add(bean.getTaskId());
		}
		if(StringUtil.isNotBlank(bean.getProductId())){//产品ID
			sb.append(" and p.productId =:productId");
			keys.add("productId");
			values.add(bean.getProductId());
		}
		if(StringUtil.isNotBlank(bean.getMemberCode())){//会员号
			sb.append(" and p.memberCode =:memberCode");
			keys.add("memberCode");
			values.add(bean.getMemberCode());
		}
		if(StringUtil.isNotBlank(bean.getSStatusFlag())){//状态
			sb.append(" and p.proceStatus =:proceStatus");
			keys.add("proceStatus");
			values.add(bean.getSStatusFlag());
		}
		if(StringUtil.isNotBlank(bean.getDelFlag())){//逻辑删除标记
			sb.append(" and p.delFlag =:delFlag");
			keys.add("delFlag");
			values.add(bean.getDelFlag());
		}
		if(StringUtil.isNotBlank(bean.getBusiId())){//原业务id
			sb.append(" and p.busiId =:busiId");
			keys.add("busiId");
			values.add(bean.getBusiId());
		}
		if(null!=bean.getStatus()){//状态列表
			sb.append(" and p.status in(:status)");
			keys.add("status");
			values.add(bean.getStatus());
		}
		
		sb.append(" order by  p.createDate desc");
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List result = this.find(sb.toString(), paramNames, paramValues);
		return result;
	}

	/**
	 * @Title txAutoExeFailDispatch
	 * @author gcj
	 * @date 2021-7-16
	 * @Description 自动任务查询2小时内处理失败的任务重新执行
	 * @return void
	 */
	public void txAutoExeFailDispatch(String hour, String min) throws Exception {
		/**
		 * ①查询流程状态为失败的调度流水(查询条件为业务的调度处理时间大于五分钟小于两小时的) ②直接重新唤醒调度任务
		 */
		PoolQueryBean poolQueryBean = new PoolQueryBean();
//		poolQueryBean.setSStatusFlag(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR);
		ArrayList status = new ArrayList<String>();
		status.add(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR);// 失败
		poolQueryBean.setStatus(status);
		poolQueryBean.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);// 未删除
//		Date startDate = new Date(new Date().getTime() - 1000 * 60 * 60
//				* Integer.parseInt(hour));
//		poolQueryBean.setPstartDate(startDate);
		int count = 0;
		List<PoolQueryBean> list = this.queryAutoTaskExeError(poolQueryBean);
		for (PoolQueryBean task : list) {
			if(AutoPublishErrorWaitTask(task, hour, min)){
				count++;
			}
			
		}
		logger.info("总共执行条数：" + count + "条");
	}

	/**
	 * @Title txAutoExeFailDispatch
	 * @author gcj
	 * @date 2021-7-14
	 * @Description 查询未启用的自动任务 前置任务成功停留五分钟以上的 重新唤醒
	 * @return void
	 */
	public void txAutoExeWaitDispatch(String hour, String min) throws Exception {
		/**
		 * ①查询执行状态为未启动的调度流水(查询条件为业务的调度处理时间大于五分钟小于两小时的)根据业务id过滤多条子任务未启动避免重复
		 * ②无前置状态的任务未启动时 直接唤醒 ③有前置状态的查询前置调度任务;
		 * 场景：强贴、出入池、银承业务有未启动节点，根据业务状态唤醒相应的节点
		 * 特殊处理:强贴申请的前置任务为解质押签收完成:需查询解质押签收状态为成功的才可重新唤醒强贴申请的调度任务
		 */
		// 查询未启动调度流水
		PoolQueryBean poolQueryBean = new PoolQueryBean();
		ArrayList status = new ArrayList<String>();
		status.add(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_INIT);// 未启动
		poolQueryBean.setStatus(status);
		poolQueryBean.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);// 未删除

//		 Date startDate = new Date(new Date().getTime() - 1000*60*60*Integer.parseInt(hour));
//		 poolQueryBean.setPstartDate(startDate);
		List<PoolQueryBean> list = this.queryAutoTaskExeOrConfig(poolQueryBean);
		int count = 0;
		for (PoolQueryBean task : list) {
			/**
			 * 根据查询到未处理的调度 追踪前置调度是否为处理完成 场景分类：入池；出池；银承；流贷；强帖 无前置数据直接唤醒
			 * 有前置的调度任务，需要查询其前置任务的执行状态是否完成，若成功则直接唤醒当前调度
			 * 其中强帖申请的前置任务为解质押签收申请，需单独处理
			 * 
			 * 入池： ①质押申请 01 无前置调度 ②质押签收 02 前置为质押申请 01 ③质押额度占用 03 前置为质押签收 02
			 * ④质押记账 04 前置为质押额度占用 03 出池： ①解质押额度释放 05 无前置 ②解质押记账 06 前置为额度释放 06
			 * ③解质押申请 07 前置为解质押记账 07 ④解质押签收 08 前置位解质押申请 08 强帖： ①强帖额度校验 09 无前置
			 * ②强帖申请 10 前置为解质押签收 08 ③强帖签收 11 前置位强帖申请 10 银承： ①银承批量新增 13 无前置
			 * ②银承出票登记 14 无前置 ③银承承兑申请 15 前置为出票登记 14 ④银承承兑签收 16 前置为承兑申请 15
			 * ⑤银承提示收票（是否自动提示收票） 17 前置为承兑签收 16
			 * 
			 * ⑥银承撤票 18 无前置 ⑦银承未用退回 19 前置为银承撤票 18
			 * 
			 * 流贷： ①支付 25 无前置 ②额度释放 26 无前置 ③放款 24 无前置
			 * 
			 */
			
			Map map = this.AutoPublishWaitTask(task, hour, min);
			if((Boolean) map.get("flag")){
				count++;
			}

		}

		logger.info("总共执行条数：" + count + "条");

	}

	/**
	 * 查询调度任务的前置任务是否为完成状态
	 */
	public boolean checkTaskFront(PoolQueryBean task) throws Exception {
		PoolQueryBean query = new PoolQueryBean();
		ArrayList stus = new ArrayList<String>();
		stus.add(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC);// 处理完成
		query.setStatus(stus);
		query.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);// 未删除
		DecimalFormat df = new DecimalFormat("00");
		String product = df.format(Integer.parseInt(String.valueOf((Integer
				.parseInt(task.getProductId()) - 1))));
		query.setProductId(product);// 补零
		query.setBusiId(task.getBusiId());
		List<AutoTaskExe> li = this.queryAutoTaskExeByCondition(query);
		if (li != null && li.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @Title queryAutoTaskExeOrConfig
	 * @author wss
	 * @date 2021-7-14
	 * @Description 根据条件查询未启动任务
	 * @return AutoTaskExe
	 */
	public List queryAutoTaskExeOrConfig(PoolQueryBean bean) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select b from AutoTaskExe  b where treeCode in ( select min(treeCode) from AutoTaskExe p where b.busiId = p.busiId ");

		if (StringUtil.isNotBlank(bean.getId())) {// 业务主键ID
			sb.append(" and p.id =:id");
			keys.add("id");
			values.add(bean.getId());
		}
		if(StringUtil.isNotBlank(bean.getTaskId())){//任务调度配置表ID
			sb.append(" and p.taskId =:taskId");
			keys.add("taskId");
			values.add(bean.getTaskId());
		}
		if(StringUtil.isNotBlank(bean.getProductId())){//产品ID
			sb.append(" and p.productId =:productId");
			keys.add("productId");
			values.add(bean.getProductId());
		}
		if(StringUtil.isNotBlank(bean.getMemberCode())){//会员号
			sb.append(" and p.memberCode =:memberCode");
			keys.add("memberCode");
			values.add(bean.getMemberCode());
		}
		if(StringUtil.isNotBlank(bean.getSStatusFlag())){//状态
			sb.append(" and p.proceStatus =:proceStatus");
			keys.add("proceStatus");
			values.add(bean.getSStatusFlag());
		}
		if(StringUtil.isNotBlank(bean.getDelFlag())){//逻辑删除标记
			sb.append(" and p.delFlag =:delFlag");
			keys.add("delFlag");
			values.add(bean.getDelFlag());
		}
		if(StringUtil.isNotBlank(bean.getBusinessId())){//原业务id
			sb.append(" and p.busiId =:busiId");
			keys.add("busiId");
			values.add(bean.getBusinessId());
		}
		if(null!=bean.getStatus()){//状态列表
			sb.append(" and p.status in(:status)");
			keys.add("status");
			values.add(bean.getStatus());
		}
		if(StringUtil.isNotBlank(bean.getEndDate())){//结束时间
			sb.append(" and p.endDate is not null");
			sb.append(" and p.endDate >:endDate");
			keys.add("endDate");
			values.add(bean.getEndDate());
		}
		if(StringUtil.isNotBlank(bean.getStartDate())){//start时间
			sb.append(" and p.startDate >:startDate");
			keys.add("startDate");
			values.add(bean.getStartDate());
		}
//		if(null!=bean.getPstartDate()){//create时间
			sb.append(" and p.createDate =:createDate");
			keys.add("createDate");
			values.add(DateUtils.formatDate(new Date(), DateUtils.ORA_DATES_FORMAT));
//		}

		sb.append(" ) order by  b.createDate desc");
		String paramNames[] = (String[]) keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list = new ArrayList();
		List result = this.find(sb.toString(), paramNames, paramValues);
		for (int i = 0; i < result.size(); i++) {
			AutoTaskExe task = (AutoTaskExe) result.get(i);
			// AutoTaskExe autoTaskExe=(AutoTaskExe)object[0];
			// TaskDispatchConfig config=(TaskDispatchConfig)object[1];
			PoolQueryBean poolQueryBean = new PoolQueryBean();
			poolQueryBean.setId(task.getId());
			poolQueryBean.setBusiId(task.getBusiId());
			poolQueryBean.setProductId(task.getProductId());
			// poolQueryBean.setStartDate(autoTaskExe.getStartDate()==null?null:autoTaskExe.getStartDate().toString());
			// poolQueryBean.setEndDate(autoTaskExe.getEndDate()==null?null:autoTaskExe.getEndDate().toString());
			// poolQueryBean.setPstartDate(autoTaskExe.getStartDate()==null?null:autoTaskExe.getStartDate());
			// poolQueryBean.setPendDate(autoTaskExe.getEndDate()==null?null:autoTaskExe.getEndDate());

			// poolQueryBean.setQueueNode(config.getQueueNode());
			list.add(poolQueryBean);
		}
		return list;
	}
	
	/**
	 * 查询调度流水失败数据
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public List queryAutoTaskExeError(PoolQueryBean bean) throws Exception {
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select p,config from AutoTaskExe  p ,TaskDispatchConfig  config where p.treeCode=config.treeCode  ");
		
		if (StringUtil.isNotBlank(bean.getId())) {//业务主键ID
			sb.append(" and p.id =:id");
			keys.add("id");
			values.add(bean.getId());
		}
		if(StringUtil.isNotBlank(bean.getTaskId())){//任务调度配置表ID
			sb.append(" and p.taskId =:taskId");
			keys.add("taskId");
			values.add(bean.getTaskId());
		}
		if(StringUtil.isNotBlank(bean.getProductId())){//产品ID
			sb.append(" and p.productId =:productId");
			keys.add("productId");
			values.add(bean.getProductId());
		}
		if(StringUtil.isNotBlank(bean.getMemberCode())){//会员号
			sb.append(" and p.memberCode =:memberCode");
			keys.add("memberCode");
			values.add(bean.getMemberCode());
		}
		if(StringUtil.isNotBlank(bean.getSStatusFlag())){//状态
			sb.append(" and p.proceStatus =:proceStatus");
			keys.add("proceStatus");
			values.add(bean.getSStatusFlag());
		}
		if(StringUtil.isNotBlank(bean.getDelFlag())){//逻辑删除标记
			sb.append(" and p.delFlag =:delFlag");
			keys.add("delFlag");
			values.add(bean.getDelFlag());
		}
		if(StringUtil.isNotBlank(bean.getBusinessId())){//原业务id
			sb.append(" and p.busiId =:busiId");
			keys.add("busiId");
			values.add(bean.getBusinessId());
		}
		if(null!=bean.getStatus()){//状态列表
			sb.append(" and p.status in(:status)");
			keys.add("status");
			values.add(bean.getStatus());
		}
		if(StringUtil.isNotBlank(bean.getEndDate())){//结束时间
			sb.append(" and p.endDate is not null");
			sb.append(" and p.endDate >:endDate");
			keys.add("endDate");
			values.add(bean.getEndDate());
		}
		if(StringUtil.isNotBlank(bean.getStartDate())){//start时间
			sb.append(" and p.startDate >:startDate");
			keys.add("startDate");
			values.add(bean.getStartDate());
		}
//		if(null!=bean.getPstartDate()){//create时间
			sb.append(" and to_char(p.createDate,'yyyy-mm-dd') =:createDate");
			keys.add("createDate");
			values.add(DateUtils.toDateString(new Date()));
//		}
		
		
		sb.append(" order by  p.createDate desc");
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list=new ArrayList();
		List result = this.find(sb.toString(), paramNames, paramValues);
		 for(int i=0;i<result.size();i++)
		 {
			 Object[] object=(Object[])result.get(i);
			 AutoTaskExe autoTaskExe=(AutoTaskExe)object[0];
			 TaskDispatchConfig config=(TaskDispatchConfig)object[1];
		      PoolQueryBean poolQueryBean = new PoolQueryBean();
		      poolQueryBean.setId(autoTaskExe.getId());
		      poolQueryBean.setBusiId(autoTaskExe.getBusiId());
		      poolQueryBean.setProductId(autoTaskExe.getProductId());
//		      poolQueryBean.setStartDate(autoTaskExe.getStartDate()==null?null:autoTaskExe.getStartDate().toString());
//		      poolQueryBean.setEndDate(autoTaskExe.getEndDate()==null?null:autoTaskExe.getEndDate().toString());
		      poolQueryBean.setPstartDate(autoTaskExe.getStartDate()==null?null:autoTaskExe.getStartDate());
		      poolQueryBean.setPendDate(autoTaskExe.getEndDate()==null?null:autoTaskExe.getEndDate());

		      poolQueryBean.setQueueNode(config.getQueueNode());
		      list.add(poolQueryBean);
		 }
		return list;
		
	}

	@Override
	public void txAutoExeTaskStatus(String hour, String min,String id) throws Exception {
		/**
		 * 流程最后一步未等到bbsp返回信息
		 * 这里直接去bbsp查证状态，若查回的状态未终态直接更新业务表状态；若不是终态，则根据状态判断是否唤醒各自调度任务
		 */
		logger.info("查询解质押签收状态的未收到bbsp最终通知的数据开始.................id:"+id);
		// ①查询解质押签收状态的未收到bbsp最终通知的数据
		DraftQueryBean bean = new DraftQueryBean();
		bean.setAssetStatus(PoolComm.CC_04);
		if(StringUtil.isNotBlank(id)){
			bean.setBusiId(id);
		}
		bean.setTaskDate(new Date());
		List outs = draftPoolQueryService.toPoolAllOutQuery(bean, null, null);
		if (outs != null && outs.size() > 0) {
			for (Object obj : outs) {
				try {
					DraftPoolOut out = (DraftPoolOut) obj;
					/**
					 * bbsp 查证该笔已发解质押签收申请的数据是否已签收,若已签收修改相应状态
					 */
					out.setTaskDate(new Date());
					PoolBillInfo info = draftPoolInService.loadByBillNo(out.getPlDraftNb(),out.getBeginRangeNo(),out.getEndRangeNo());
					/**
					 * 查询bbsp 状态是否为解质押签收状态 不为此状态发送解质押签收 调取电票接口，获取解质押签收成功结果。
					 */
					ECDSPoolTransNotes poolTrans = new ECDSPoolTransNotes();
					
					poolTrans.setAcctNo(out.getAccNo());//电票签约账号
					
					poolTrans.setBeginRangeNo(out.getBeginRangeNo());//票据开始子区间号
					poolTrans.setEndRangeNo(out.getEndRangeNo());//票据结束子区间号
					poolTrans.setDataSource("3");//渠道来源  3-票据池
					poolTrans.setTransNo(PoolComm.CBS_0022012);//交易类型集合 提示付款
					poolTrans.setBillNo(out.getPlDraftNb());//票号
					poolTrans.setTransType("2");//业务类型  签收类
					
					
					logger.info("票据ID为[" + info.getDiscBillId() + "]的票据发送电子交易类查询开始");
					ReturnMessageNew resp = poolEcdsService
							.txApplyQueryBusinessBatch(poolTrans);
					List listDetails = resp.getDetails();
					Map map = (Map) listDetails.get(0);
					String statusCode = getStringVal(map.get("statusCode"));//交易结果
					if(statusCode.equals("2")){//签收成功
						logger.info("票号为[" + out.getPlDraftNb() + "]的票据签收成功,后续逻辑处理开始....");
						
						draftPoolOutService.txTransTypePoolOut(out, "2", info);
					}else if(statusCode.equals("3")){//签收失败
						logger.info("票号为[" + out.getPlDraftNb() + "]的票据签收失败,后续逻辑处理开始....");
						
						draftPoolOutService.txTransTypePoolOut(out, "3", info);
					}
					
					
					/*String status = getStringVal(map.get("status"));// 状态
					if (status.equals("TE201902_02")) {// 表示查询的结果为签收成功
						logger.info("票号为[" + out.getPlDraftNb() + "]的票据签收成功,后续逻辑处理开始....");
						
						draftPoolOutService.txTransTypePoolOut(out, "2", info);
						
					} else if (status.equals("TE201902_03")) {// 表示查询的结果为签收失败
						
						logger.info("票号为[" + out.getPlDraftNb() + "]的票据签收失败,后续逻辑处理开始....");
						
						draftPoolOutService.txTransTypePoolOut(out, "3", info);
					}*/
				} catch (Exception e) {
					logger.info("bbsp查询数据报错:"+e);
					continue;
				}
			}
		}
		
		logger.info("查询强帖的签收发送成功，bbsp未通知的数据.................id:"+id);
		//查询强帖的签收发送成功，bbsp未通知的数据
		DraftQueryBean dqueryBean = new DraftQueryBean();
		dqueryBean.setAssetStatus(PoolComm.TX_04);
		dqueryBean.setIds(id);
		List<PlDiscount> list=draftPoolDiscountServer.getDiscountsListByParamView(dqueryBean,null,null);
		if(list != null && list.size() > 0){
			for (PlDiscount discount : list) {
				discount.setTaskDate(new Date());
				/**
				 * queryType 去BBSP查询是否已发起过贴现签收
				 * 若记账返回失败;查询账务(时时返回)
				 */
				try{
					PoolBillInfo bill = draftPoolInService.loadByBillNo(discount.getSBillNo(),discount.getBeginRangeNo(),discount.getEndRangeNo());
					PoolQueryBean poolQueryBean = new PoolQueryBean();
					poolQueryBean.setBillNo(bill.getSBillNo());
					
					poolQueryBean.setBeginRangeNo(bill.getBeginRangeNo());
					poolQueryBean.setEndRangeNo(bill.getEndRangeNo());
					
					poolQueryBean.setSStatusFlag(PoolComm.DS_10);
					DraftPool pool=consignService.queryDraftByBean(poolQueryBean).get(0);

					ECDSPoolTransNotes poolTrans = new ECDSPoolTransNotes();
					poolTrans.setTransNo("2");//查询类型
					poolTrans.setConferNo(discount.getSSwapContNum());//合同号
					poolTrans.setBillId(bill.getDiscBillId());//票据id
					poolTrans.setBillSource(discount.getDraftSource());
					if(discount.getDraftSource().equals(PoolComm.CS02)){
						poolTrans.setBeginRangeNo(discount.getBeginRangeNo());//子票区间起始
						poolTrans.setEndRangeNo(discount.getEndRangeNo());//子票区间截至
						
					}
					
					ReturnMessageNew resp = poolEcdsService.txApplySynchronization(poolTrans);
					if(resp.isTxSuccess()){
						List details = resp.getDetails();
						String billId = "";//票据id
						Map map = new HashMap();
						String billNo = discount.getSBillNo();
						for (int j2 = 0; j2 < details.size(); j2++) {
							map = (Map) details.get(j2);
							billId = getStringVal(map.get("BILL_MSG_ARRAY.BILL_ID"));
							if(discount.getBillinfoId().getDiscBillId().equals(billId)){
								/*	
								 * 未处理: TRAN_STATUS  1：未处理
								 * 成功：ACCOUNT_STATUS 1：记账成功
								 * 失败：TRAN_STATUS  3签收失败；4：拒绝成功； ACCOUNT_STATUS 2：记账失败
								 * 
								 * ——BBSP系统对该接口码值作出修改                Ju Nana 2020.01.09
								*/
								String transResult = getStringVal(map.get("BILL_MSG_ARRAY.TRAN_STATUS"));
								String acctResult = getStringVal(map.get("BILL_MSG_ARRAY.ACCOUNT_STATUS"));

								draftPoolDiscountServer.txTaskDiscountSynchroniza(discount, transResult, acctResult);
								
							}
						}
					}
					
				}catch(Exception e){
					logger.info("贴现签收记账查询失败");
					continue;
				}
			}
		}
		
		logger.info("查询银承签收状态的未收到bbsp最终通知的数据.................id:"+id);
		//② 查询银承签收状态的未收到bbsp最终通知的数据
		OnlineQueryBean queryBean = new OnlineQueryBean();
		queryBean.setStatus(PublicStaticDefineTab.ACPT_DETAIL_007);
		if(StringUtil.isNotBlank(id)){
			queryBean.setId(id);
		}
		queryBean.setCreateDate(new Date());
		List<PlOnlineAcptDetail> acptDetails = acptService.queryPlOnlineAcptDetailList(queryBean, null);
		if(acptDetails != null && acptDetails.size() >0){
			for (PlOnlineAcptDetail detail : acptDetails) {
				PlOnlineAcptBatch batch = (PlOnlineAcptBatch) acptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
				try {
					acptService.txRepeatAcceptionSign(detail,batch);
				} catch (Exception e) {
					logger.info("承兑签收重复执行出错:"+e);
					continue;
				}
			}
		}
		
		logger.info("查询银承已发撤票状态的未收到bbsp最终通知的数据.................id:"+id);
		//③ 查询银承已发撤票状态的未收到bbsp最终通知的数据
		OnlineQueryBean queryBean1 = new OnlineQueryBean();
		queryBean1.setStatus(PublicStaticDefineTab.ACPT_DETAIL_009);
		if(StringUtil.isNotBlank(id)){
			queryBean1.setId(id);
		}
		queryBean1.setCreateDate(new Date());
		List<PlOnlineAcptDetail> acptDetails1 = acptService.queryPlOnlineAcptDetailList(queryBean1, null);
		if(acptDetails != null && acptDetails1.size() >0){
			for (PlOnlineAcptDetail detail : acptDetails1) {
				PlOnlineAcptBatch batch = (PlOnlineAcptBatch) acptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
				try {
					acptService.txApplyRevokeCheck(detail, batch);
				} catch (Exception e) {
					logger.info("银承撤票查验处理:"+e);
					continue;
				}
			}
		}

		logger.info("查询银承已发未用退回状态的未收到bbsp最终通知的数据.................id:"+id);
		//④ 查询银承已发未用退回状态的未收到bbsp最终通知的数据
		OnlineQueryBean queryBean2 = new OnlineQueryBean();
		queryBean2.setStatus(PublicStaticDefineTab.ACPT_DETAIL_010);
		if(StringUtil.isNotBlank(id)){
			queryBean2.setId(id);
		}
		queryBean2.setCreateDate(new Date());
		List<PlOnlineAcptDetail> acptDetails2 = acptService.queryPlOnlineAcptDetailList(queryBean2, null);
		if(acptDetails != null && acptDetails2.size() >0){
			for (PlOnlineAcptDetail detail : acptDetails2) {
				PlOnlineAcptBatch batch = (PlOnlineAcptBatch) acptService.load(detail.getAcptBatchId(),PlOnlineAcptBatch.class);
				try {
					acptService.txApplyUnusedCheck(detail, batch);
				} catch (Exception e) {
					logger.info("银承未用退回查验:"+e);
					continue;
				}
			}
		}
	
	}

	/**
	 * 任务唤醒
	 * 有前置的未启动调度的唤醒
	 * @return
	 */
	public Map AutoPublishWaitTask(PoolQueryBean task, String hour,
			String min) throws Exception {
		Map map = new HashMap();
		boolean flag = false;
		// 根据业务id获取业务对象
		String productId = task.getProductId();
		Date taskDate = null;
		String business = "";// 业务状态
		String btFlag = "";// 质押出入池是标记额度是否占用标识
		DraftPoolIn poolIn = null;
		DraftPoolOut out = null;
		PlDiscount discount = null;
		PlOnlineCrdt batch = null;
		PlCrdtPayList pay = null;
		PedCreditDetail loan = null;
		PlOnlineAcptDetail detail = null;
		PlOnlineCrdt batch2 = null;
		TMessageRecord msg = null;
		PlOnlineAcptBatch acptBatch = null;

		if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ZY)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_QS)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JZ)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_ED)) {
			// 质押流程
			poolIn = (DraftPoolIn) this.load(task.getBusiId(),
					DraftPoolIn.class);
			taskDate = poolIn.getTaskDate();
			business = poolIn.getPlStatus();
			btFlag = poolIn.getBtFlag();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_JZY)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JJZ)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JSQ)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JQS)) {
			// 解质押流程
			out = (DraftPoolOut) this
					.load(task.getBusiId(), DraftPoolOut.class);
			taskDate = out.getTaskDate();
			business = out.getPlStatus();
			btFlag = out.getBtFlag();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_TED)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_TX)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_TJZ)) {
			// 贴现流程
			discount = (PlDiscount) this.load(task.getBusiId(),
					PlDiscount.class);
			taskDate = discount.getTaskDate();
			if (discount.getSBillStatus().equals(PoolComm.TX_00)) {
				// 贴现状态为新建数据；查寻是否该票是否为已出池；已出池则直接唤醒强帖申请调度，反之不处理
				PoolBillInfo info = discount.getBillinfoId();
				business = info.getSDealStatus();
				if (info.getSDealStatus().equals(PoolComm.DS_04)) {// 解质押签收，成功已出池数据
					// 唤醒强帖申请任务
					RedisUtils redisrCache = (RedisUtils) SpringContextUtil
							.getBean("redisrCache");
					boolean result = redisrCache.getLock(task.getId(),
							String.valueOf(new Date()), 120);
					if (result) {
						logger.info("强帖申请未执行任务，业务ID:" + task.getBusiId());
						Map<String, String> reqParams = new HashMap<String, String>();// 重新唤醒任务
						autoTaskPublishService.publishWaitTask("0",
								AutoTaskNoDefine.POOLDIS_SEND_TASK_NO,
								discount.getId(),
								AutoTaskNoDefine.BUSI_TYPE_TX, reqParams);
						flag = true;
					}
					map.put("flag", flag);
					return map;
				}
			}

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_CRDT)) {
			// 流贷主流程
			batch = (PlOnlineCrdt) this.load(task.getBusiId(),
					PlOnlineCrdt.class);
			taskDate = batch.getTaskDate();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_PAY)) {
			// 支付流程
			pay = (PlCrdtPayList) this.load(task.getBusiId(),
					PlCrdtPayList.class);
			taskDate = pay.getTaskDate();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_PAY)) {
			// 贷款归还流程
			loan = (PedCreditDetail) this.load(task.getBusiId(),
					PedCreditDetail.class);// 获取借据
			taskDate = loan.getTaskDate();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE)) {
			// 额度释放流程 分三种场景：①银承明细释放 ②银承批次释放 ③ 流贷释放 根据业务id分别查询
			detail = (PlOnlineAcptDetail) this.load(task.getBusiId(),
					PlOnlineAcptDetail.class);// 银承明细对象

			acptBatch = (PlOnlineAcptBatch) this.load(task.getBusiId(),
					PlOnlineAcptBatch.class);// 银承批次对象

			batch = (PlOnlineCrdt) this.load(task.getBusiId(),
					PlOnlineCrdt.class);// 流贷对象

			if (detail != null) {
				taskDate = detail.getTaskDate();
			}
			if (batch != null) {
				taskDate = batch.getTaskDate();
			}
			if (acptBatch != null) {
				taskDate = acptBatch.getTaskDate();
			}

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_MSS)) {
			// 短信通知流程
			msg = (TMessageRecord) this.load(task.getBusiId(),
					TMessageRecord.class);
			taskDate = msg.getCreateTime();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_ADD)) {
			// 银承流程
			acptBatch = (PlOnlineAcptBatch) this.load(task.getBusiId(),
					PlOnlineAcptBatch.class);
			taskDate = batch.getTaskDate();

		} else {
			detail = (PlOnlineAcptDetail) this.load(task.getBusiId(),
					PlOnlineAcptDetail.class);
			taskDate = detail.getTaskDate();
			business = detail.getStatus();
		}
		if(taskDate != null){
			int second = DateUtils.getSecdBetweenDate(new Date(), taskDate);
			if (second > 60 * Integer.parseInt(hour) + 60 * Integer.parseInt(min)
					&& (second < (60 * 60 * Integer.parseInt(hour) + 60 * Integer
							.parseInt(min)))) {// 超过五分钟小于两小时五分钟 放redis 缓存中

				RedisUtils redisrCache = (RedisUtils) SpringContextUtil
						.getBean("redisrCache");
				boolean result = redisrCache.getLock(task.getId(),
						String.valueOf(new Date()), 120);
				if (result) {
					logger.info("异常监控超过五分钟未执行任务，业务ID:" + task.getBusiId());
					Map<String, String> reqParams = new HashMap<String, String>();// 重新唤醒任务
					if (StringUtil.isNotBlank(business)) {// 状态不为空是为出入池银承业务;需要根据状态判断应该唤醒哪个节点的调度
						if (business.equals(PoolComm.RC_01)
								|| business.equals(PoolComm.RC_03)) {
							// 超过五分钟并且为已发质押申请、发质押签收申请状态,bbsp未通知系统,需重新唤醒质押签收节点，会先进行查证
							/**
							 * 唤醒质押签收子任务。。。
							 */
							reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
							autoTaskPublishService.publishWaitTask("0",
									AutoTaskNoDefine.POOLIN_SIGN_TASK_NO,
									task.getBusiId(),
									AutoTaskNoDefine.BUSI_TYPE_QS, reqParams);
							flag = true;
						}else if (business.equals(PoolComm.RC_04)) {
							// 超过五分钟并且为质押申请已签收状态,bbsp通知系统签收成功,但未唤醒额度占用节点
							/**
							 * 查询是否占用过额度 已占用额度直接唤醒记账任务
							 */
							if (StringUtil.isNotEmpty(btFlag)
									&& PoolComm.SP_01.equals(btFlag)) {
								reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
								autoTaskPublishService.publishWaitTask("0",
										AutoTaskNoDefine.POOLIN_ACC_TASK_NO,
										task.getBusiId(),
										AutoTaskNoDefine.BUSI_TYPE_JZ, reqParams);
								flag = true;
							} else {
								/**
								 * 唤醒额度占用子任务。。。
								 */
								reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
								autoTaskPublishService.publishWaitTask("0",
										AutoTaskNoDefine.POOLIN_EDU_TASK_NO,
										task.getBusiId(),
										AutoTaskNoDefine.BUSI_TYPE_ED, reqParams);
								flag = true;
							}
						}else if (business.equals(PoolComm.CC_00)) {
							// 超过五分钟并且为解质押新建数据状态,根据是否占用额度成功唤醒额度占用节点或出池记账节点
							/**
							 * 查询是否占用过额度 已占用额度直接唤醒记账任务
							 */
							if (StringUtil.isNotEmpty(btFlag)
									&& PoolComm.SP_00.equals(btFlag)) {
								reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
								autoTaskPublishService.publishWaitTask("0",
										AutoTaskNoDefine.POOLOUT_ACC_TASK_NO,
										task.getBusiId(),
										AutoTaskNoDefine.BUSI_TYPE_JJZ, reqParams);
								flag = true;
							} else {
								/**
								 * 唤醒额度释放子任务。。。
								 */
								reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
								autoTaskPublishService.publishWaitTask("0",
										AutoTaskNoDefine.POOLOUT_EDU_TASK_NO,
										task.getBusiId(),
										AutoTaskNoDefine.BUSI_TYPE_JZY, reqParams);
								flag = true;
							}
						}else if (business.equals(PoolComm.CC_01) || business.equals(PoolComm.CC_02)) {
							// 超过五分钟并且为解质押出池记账状态或已发解质押出池申请状态,此时不确定是否解质押申请处理成功，需要去bbsp查验
							/**
							 * 查询bbsp 状态是否为解质押申请状态
							 */
							ECDSPoolTransNotes poolTrans = new ECDSPoolTransNotes();
							
							/*poolTrans.setBillNo(out.getPlDraftNb());//票号
							
							*//********************融合改造新增 start******************************//*
							poolTrans.setMaxBeginRangeNo(out.getBeginRangeNo());//票据开始子区间号
							poolTrans.setMinBeginRangeNo(out.getBeginRangeNo());//票据开始子区间号
							poolTrans.setMaxEndRangeNo(out.getEndRangeNo());//票据结束子区间号
							poolTrans.setMinEndRangeNo(out.getEndRangeNo());//票据结束子区间号
							
							poolTrans.setAcceptorAcctNo(out.getAccNo());//账号
							poolTrans.setTransNo(PoolComm.NES_0102000);//解质押
							poolTrans.setOperationType("QT01");//撤销申请查询  
							*/
							logger.info("票号为["+out.getPlDraftNb()+"],票据起始号：" + out.getBeginRangeNo() + " ，票据截至号： " + out.getEndRangeNo() + "的票,发送查询解质押待签收接口开始");
							/********************融合改造新增 end******************************/

							Map infoMap = new HashMap();
//							infoMap.put("INPOOL_FLAG", poolNotes.getIfInPool());//是否入池
//							infoMap.put("BRANCH_NO",poolNotes.getBranchNo());//查询机构号		
							infoMap.put("QUERY_INFO_ARRAY.APPLYER_ACCT_NO",out.getAccNo());//账号
							infoMap.put("QUERY_INFO_ARRAY.DRAFT_TYPE","");//汇票类型
							infoMap.put("QUERY_INFO_ARRAY.ACCEPTOR_ACCT_NAME","");//承兑人名称
							infoMap.put("QUERY_INFO_ARRAY.BILL_NO",out.getPlDraftNb());//票据号码
							infoMap.put("QUERY_INFO_ARRAY.MIN_START_BILL_NO",out.getBeginRangeNo());//票据开始子区间号
							infoMap.put("QUERY_INFO_ARRAY.MAX_START_BILL_NO",out.getBeginRangeNo());//票据开始子区间号
							infoMap.put("QUERY_INFO_ARRAY.MIN_END_BILL_NO",out.getEndRangeNo());//票据结束子区间号
							infoMap.put("QUERY_INFO_ARRAY.MAX_END_BILL_NO",out.getEndRangeNo());//票据结束子区间号
							infoMap.put("QUERY_INFO_ARRAY.MAX_ACCEPTOR_DATE",out.getPlIsseDt());//出票日期上限
							infoMap.put("QUERY_INFO_ARRAY.MIN_ACCEPTOR_DATE",out.getPlIsseDt());//出票日期下限
							infoMap.put("QUERY_INFO_ARRAY.MAX_DUE_DATE",out.getPlDueDt());//汇票到期日期上限
							infoMap.put("QUERY_INFO_ARRAY.MIN_DUE_DATE",out.getPlDueDt());//汇票到期日期下限
							infoMap.put("QUERY_INFO_ARRAY.MAX_AMT",out.getTradeAmt());//票据最高金额
							infoMap.put("QUERY_INFO_ARRAY.MIN_AMT",out.getTradeAmt());//票据最低金额
							infoMap.put("QUERY_INFO_ARRAY.QUERY_TYPE","QT01");//查询类型
							infoMap.put("TRAN_NO_LIST",PoolComm.NES_0092010);//交易编号集合
							poolTrans.getDetails().add(infoMap);
							
							ReturnMessageNew resp = poolEcdsService.txApplyImplawnForSign(poolTrans);
							logger.info("发送查询解质押待签收接口结束,返回的数据长度为["
									+ resp.getDetails().size() + "],返回的数据为["
									+ resp.getDetails() + "]");
							List rest = resp.getDetails();
							if (rest.size() > 0) {
								logger.info("状态为解质押申待签收；可唤醒解质押签收申请子任务");
								/**
								 * 唤醒出池签收子任务节点
								 */
								reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
								autoTaskPublishService.publishWaitTask("0",
										AutoTaskNoDefine.POOLOUT_SIGN_TASK_NO,
										task.getBusiId(),
										AutoTaskNoDefine.BUSI_TYPE_QS, reqParams);
								flag = true;
							} else {
								logger.info("可唤醒解质押申请子任务");

								/**
								 * 唤醒解出池申请子任务节点
								 */
								reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
								autoTaskPublishService.publishWaitTask("0",
										AutoTaskNoDefine.POOLOUT_SEND_TASK_NO,
										task.getBusiId(),
										AutoTaskNoDefine.BUSI_TYPE_JSQ, reqParams);
								flag = true;

							}
						}else if (business.equals(PoolComm.TX_01)) {
							// 超过五分钟并且为已发贴现申请状态，bbsp未通知系统，唤醒签收记账任务节点

							/**
							 * 唤醒贴现签收记账子任务节点
							 */
							reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
							autoTaskPublishService.publishWaitTask("0",
									AutoTaskNoDefine.POOLDIS_SIGN_TASK_NO,
									task.getBusiId(),
									AutoTaskNoDefine.BUSI_TYPE_TJZ, reqParams);
							flag = true;
						}else if (business.equals(PublicStaticDefineTab.ACPT_DETAIL_005)) {
							// 超过五分钟并且为已发出票登记申请状态，bbsp未通知系统，重新唤醒出票登记申请

							/**
							 * 唤醒出票登记申请子任务节点
							 */
							reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
							autoTaskPublishService.publishWaitTask("0",
									AutoTaskNoDefine.POOL_ONLINE_REGISTER_NO,
									task.getBusiId(),
									AutoTaskNoDefine.BUSI_TYPE_ONLINE_REGISTER,
									reqParams);
							flag = true;
						}else if (business.equals(PublicStaticDefineTab.ACPT_DETAIL_006)) {
							// 超过五分钟并且为已发承兑申请状态，bbsp未通知系统，重新唤醒承兑申请

							/**
							 * 唤醒出票登记申请子任务节点
							 */
							reqParams.put("source", "1");// 调度唤醒来源为自动任务;唤醒后需要先进行查询
							autoTaskPublishService.publishWaitTask("0",
									AutoTaskNoDefine.POOL_ONLINE_ACPT_NO,
									detail.getId(),
									AutoTaskNoDefine.BUSI_TYPE_ONLINE_ACPT,
									reqParams);
							flag = true;
						}

					}
				}
			}else{
				map.put("mag", "该任务的执行时间小于五分钟或大于两小时,暂不允许执行!");
			}
		}
		map.put("flag", flag);
		return map;

	}

	/**
	 * 失败任务的重新唤醒或界面点击的唤醒
	 * @param task
	 * @param hour
	 * @param min
	 * @return
	 * @throws Exception
	 */
	public boolean AutoPublishErrorWaitTask(PoolQueryBean task, String hour,String min) throws Exception {
		Date taskDate = task.getEndDDueDt();// 对象调度操作时间

		RedisUtils redisrCache = (RedisUtils) SpringContextUtil
				.getBean("redisrCache");
		
		// 根据业务id获取业务对象
		String productId = task.getProductId();

		/**
		 * （1）【在线流贷申请】 （2）【在线流贷受托支付申请】 （3）【在线流贷受托支付贷款归还/提前还款申请】
		 * （4）【在线银承申请任务1-银票批量新增】 【在线银承申请任务2-出票登记申请/提示承兑申请/承兑签收申请】
		 * 【在线银承申请任务3-提示收票】 【在线银承申请任务-撤票/撤销】 【在线银承申请任务-未用退回】
		 * 【在线银承申请任务-额度释放】 （5）【短信通知】 （6）【票据池贴现申请】 （7）【出池申请】 （8）【入池申请】
		 */

		if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ZY)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_QS)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JZ)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_ED)) {
			// 质押流程
			DraftPoolIn poolIn = (DraftPoolIn) this.load(
					task.getBusiId(), DraftPoolIn.class);
			taskDate = poolIn.getTaskDate();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_JZY)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JJZ)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JSQ)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_JQS)) {
			// 解质押流程
			DraftPoolOut out = (DraftPoolOut) this.load(
					task.getBusiId(), DraftPoolOut.class);
			taskDate = out.getTaskDate();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_TED)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_TX)
				|| productId.equals(AutoTaskNoDefine.BUSI_TYPE_TJZ)) {
			// 贴现流程
			PlDiscount discount = (PlDiscount) this.load(
					task.getBusiId(), PlDiscount.class);
			taskDate = discount.getTaskDate();

		} else if (productId
				.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_CRDT)) {
			// 流贷主流程
			PlOnlineCrdt batch = (PlOnlineCrdt) this.load(
					task.getBusiId(), PlOnlineCrdt.class);
			taskDate = batch.getTaskDate();

		} else if (productId
				.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_PAY)) {
			// 支付流程
			PlCrdtPayList pay = (PlCrdtPayList) this.load(
					task.getBusiId(), PlCrdtPayList.class);
			taskDate = pay.getTaskDate();

		} else if (productId
				.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_PAY)) {
			// 贷款归还流程
			PedCreditDetail loan = (PedCreditDetail) this.load(
					task.getBusiId(), PedCreditDetail.class);// 获取借据
			taskDate = loan.getTaskDate();

		} else if (productId
				.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_RELEASE)) {
			// 额度释放流程 分三种场景：①银承明细释放 ②银承批次释放 ③ 流贷释放 根据业务id分别查询
			PlOnlineAcptDetail detail = (PlOnlineAcptDetail) this.load(
					task.getBusiId(), PlOnlineAcptDetail.class);// 银承明细对象

			PlOnlineAcptBatch batch = (PlOnlineAcptBatch) this.load(
					task.getBusiId(), PlOnlineAcptBatch.class);// 银承批次对象

			PlOnlineCrdt batch2 = (PlOnlineCrdt) this.load(
					task.getBusiId(), PlOnlineCrdt.class);// 流贷对象

			if (detail != null) {
				taskDate = detail.getTaskDate();
			}
			if (batch != null) {
				taskDate = batch.getTaskDate();
			}
			if (batch2 != null) {
				taskDate = batch2.getTaskDate();
			}
		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_MSS)) {
			// 短信通知流程
			TMessageRecord msg = (TMessageRecord) this.load(
					task.getBusiId(), TMessageRecord.class);
			taskDate = msg.getCreateTime();

		} else if (productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_ADD)) {
			// 银承流程批量新增时
			PlOnlineAcptBatch batch = (PlOnlineAcptBatch) this.load(
					task.getBusiId(), PlOnlineAcptBatch.class);
			taskDate = batch.getTaskDate();

		} else if(productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_ADD)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_REGISTER)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_ACPT)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_SIGN)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_SEND)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_01)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_02)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_CANCLE_03)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_01)
				||productId.equals(AutoTaskNoDefine.BUSI_TYPE_ONLINE_UNUSED_02)){
			// 银承流程
			PlOnlineAcptDetail detail = (PlOnlineAcptDetail) this.load(
					task.getBusiId(), PlOnlineAcptDetail.class);
			taskDate = detail.getTaskDate();
		}

		if(taskDate != null){
			int second = DateUtils.getSecdBetweenDate(new Date(), taskDate);
			if (second > 60 * Integer.parseInt(hour) + 60
					* Integer.parseInt(min)
					&& (second < (60 * 60 * Integer.parseInt(hour) + 60 * Integer
							.parseInt(min)))) {// 超过五分钟小于两小时五分钟 放redis 缓存中
				boolean result = redisrCache.getLock(task.getId(),
						String.valueOf(new Date()), 12);
				if(result){
					logger.info("异常监控超过五分钟小于两小时五分钟执行失败的任务，业务ID:"
							+ task.getBusiId());
					Map<String, String> reqParams = new HashMap<String, String>();// 重新唤醒任务
					autoTaskPublishService.publishWaitTask("0",
							task.getQueueNode(), task.getBusiId(),
							task.getProductId(), reqParams);
					return true;
				}else {
					logger.info("调度表BT_AUTOTASK_EXE id" + task.getId()
							+ "已经加锁，失效后才能再次调用");
				}
			}	
		}else{
			boolean result = redisrCache.getLock(task.getId(),
					String.valueOf(new Date()), 12);
			logger.info("异常监控超过五分钟小于两小时五分钟执行失败的任务，业务ID:"
					+ task.getBusiId());
			if(result){
				Map<String, String> reqParams = new HashMap<String, String>();// 重新唤醒任务
				autoTaskPublishService.publishWaitTask("0",
						task.getQueueNode(), task.getBusiId(),
						task.getProductId(), reqParams);
				return true;
			}else {
				logger.info("调度表BT_AUTOTASK_EXE id" + task.getId()
						+ "已经加锁，失效后才能再次调用");
			}
		}
		return false;
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
}
