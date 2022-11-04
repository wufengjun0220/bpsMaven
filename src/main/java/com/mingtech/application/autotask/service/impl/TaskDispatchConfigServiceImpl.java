package com.mingtech.application.autotask.service.impl;

import java.util.*;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.QueueDispatchNode;
import com.mingtech.application.autotask.domain.TaskDispatchConfigBean;
import com.mingtech.application.autotask.taskdispatch.AutoTaskDispatchAbstract;
import com.mingtech.application.sysmanage.domain.Resource;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.core.page.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.service.TaskDispatchConfigService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

@Service("taskDispatchConfigService")
public class TaskDispatchConfigServiceImpl extends GenericServiceImpl implements TaskDispatchConfigService {
	private static final Logger logger = Logger.getLogger(TaskDispatchConfigServiceImpl.class);
	@Autowired
    private AutoTaskExeService autoTaskExeService;
	
	
	/**
	 * 根据ID获取任务执行流水信息
	 * @param id 自动任务执行流水ID
	 */
	public TaskDispatchConfig getTaskDispatchConfigById(String id) {
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where id=? ");
		List values = new ArrayList();
		values.add(id);
		List list = this.find(hql.toString(),values);
		if(list.size() > 0) {
			return (TaskDispatchConfig)list.get(0);
		}else {
			return null;
		}
	}
	
	/**
	 * 根据会员编码和任务编号查询任务调度配置信息
	 * @param memberCode 会员编码
	 * @param taskNo 任务编号
	 */
	public TaskDispatchConfig getTaskDispatchConfigByMemberCodeAndTaskNo(String memberCode,String taskNo) {
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where memberCode=?  and taskNo=?");
		List values = new ArrayList();
		values.add(memberCode);
		values.add(taskNo);
		List list = this.find(hql.toString(),values);
		if(list.size() > 0) {
			return (TaskDispatchConfig)list.get(0);
		}else {
			return null;
		}
	}
	
	/**
	 * 根据会员编码和父任务ID、父任务编号查询下级所有子任务配置信息
	 * @param memberCode 会员编码
	 * @param id 父任务配置ID
	 * @param taskNo 父任务编号
	 */
	public List<TaskDispatchConfig> queryAllChildTaskDispatchConfigs(String memberCode,String id,String taskNo){
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where memberCode=?  and id !=?  and treeCode like ?");
		List values = new ArrayList();
		values.add(memberCode);
		values.add(id);
		values.add("%"+taskNo+"%");
		return (List<TaskDispatchConfig> )this.find(hql.toString(),values);
	}
	
	/**
	 * 根据父ID和任务类型查询下属任务调度配置信息
	 * @param id 任务调度配置ID
	 * @param taskType 任务类型
	 */
	public List queryEffectTaskDispatchConfigByParentId(String parentId,String taskType) {
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where parentId=?  and taskType=? and status=? and delFlag=?");
		List values = new ArrayList();
		values.add(parentId);
		values.add(taskType);
		values.add(PublicStaticDefineTab.TASK_DISPATCH_STATUS_START);//启用
		values.add(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
		
		List list = this.find(hql.toString(),values);
		return list;
	}
	
	
	/**
	 * 根据父ID和任务类型查询下属任务调度配置信息ID
	 * @param parentId 任务调度配置ID
	 * @param taskType 任务类型
	 */
	public List queryEffectTaskDispatchConfigIdsByParentId(String parentId,String taskType) {
		StringBuffer sql = new StringBuffer("select id  from BO_TASK_DISPATCH_CONFIG where PARENT_ID=?  and TASK_TYPE=? and STATUS=? and DEL_FLAG=?");
		List params = new ArrayList();
		params.add(parentId);
		params.add(taskType);
		params.add(PublicStaticDefineTab.TASK_DISPATCH_STATUS_START);//启用
		params.add(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除
		List list =  dao.SQLQuery(sql.toString(),params);
		return list;
	}



	/**
	 * 根据条件查询 TaskDispatchConfig自动任务调度配置实体
	 * @param
	 * @return TaskDispatchConfig 自动任务调度配置实体
	 */
	public TaskDispatchConfig queryTaskDispatchConfigByParm(PoolQueryBean bean){
		StringBuffer sb = new StringBuffer();
		List keys = new ArrayList();
		List values = new ArrayList();
		sb.append("select p from TaskDispatchConfig as p where 1=1 ");
		
		if (StringUtil.isNotBlank(bean.getTreeCode())) {//树叶子节点
			sb.append(" and p.treeCode =:treeCode");
			keys.add("treeCode");
			values.add(bean.getTreeCode());
		}
		if (StringUtil.isNotBlank(bean.getTaskNo())) {//任务编号
			sb.append(" and p.taskNo =:taskNo");
			keys.add("taskNo");
			values.add(bean.getTaskNo());
		}
		if(StringUtil.isNotBlank(bean.getQueueName())){//队列名称
			sb.append(" and p.queueName =:queueName");
			keys.add("queueName");
			values.add(bean.getQueueName());
		}
		if(StringUtil.isNotBlank(bean.getQueueNode())){//队列分发节点
			sb.append(" and p.queueNode =:queueNode");
			keys.add("queueNode");
			values.add(bean.getQueueNode());
		}
		if(StringUtil.isNotBlank(bean.getIds())){//id
			sb.append(" and p.id =:id");
			keys.add("id");
			values.add(bean.getIds());
		}
		if(StringUtil.isNotBlank(bean.getDelFlag())){//逻辑删除标记
			sb.append(" and p.delFlag =:delFlag");
			keys.add("delFlag");
			values.add(bean.getDelFlag());
		}
		String paramNames[] = (String[])keys.toArray(new String[keys.size()]);
		Object paramValues[] = values.toArray();
		List list = this.find(sb.toString(), paramNames, paramValues);
		if(list!= null && list.size() >0){
			return (TaskDispatchConfig) list.get(0);
		}
		return null;
		
		
	}
	
	/**
	 * 根据会员编码和父任务ID、父任务编号查询当前任务以及下级所有子任务配置信息
	 * @param memberCode 会员编码
	 * @param taskNo 父任务编号
	 */
	public List<TaskDispatchConfig> queryAllTaskDispatchConfigs(String memberCode,String taskNo){
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where memberCode=?  and treeCode like ?");
		List values = new ArrayList();
		values.add(memberCode);
		values.add("%"+taskNo+"%");
		return (List<TaskDispatchConfig> )this.find(hql.toString(),values);
	}
	/**
	 * 查询任务调度配置信息
	 * @param user 当前用户
	 * @param page
	 * @param taskDispatchConfig 任务调度实体
	 */
	@Override
	public String querySystemConfigList(TaskDispatchConfig taskDispatchConfig, User user, Page page) throws Exception{
		StringBuffer sb = new StringBuffer();
		List valueList = new ArrayList(); // 要查询的值列表
		sb.append("select task from TaskDispatchConfig task where 1 = 1 ");
		if(StringUtils.isNotBlank(taskDispatchConfig.getId())){//任务ID
			sb.append(" and task.id = ? ");
			valueList.add(taskDispatchConfig.getId());
		}
		if(StringUtils.isNotBlank(taskDispatchConfig.getTaskNo())){//任务编号
			sb.append(" and task.taskNo = ? ");
			valueList.add(taskDispatchConfig.getTaskNo());
		}
		if(StringUtils.isNotBlank(taskDispatchConfig.getTaskType())){//业务类型
			sb.append(" and task.taskType = ? ");
			valueList.add(taskDispatchConfig.getTaskType());
		}
		if(StringUtils.isNotBlank(taskDispatchConfig.getStatus())){//状态
			sb.append(" and task.status = ? ");
			valueList.add(taskDispatchConfig.getStatus());
		}
		if(StringUtils.isNotBlank(taskDispatchConfig.getTaskDesc())){//任务描述
			sb.append(" and task.taskDesc like ? ");
			valueList.add("%"+taskDispatchConfig.getTaskDesc()+"%");
		}
		if(StringUtils.isNotBlank(taskDispatchConfig.getParentId())){//父任务id
			sb.append(" and task.parentId  = ?  ");
			valueList.add(taskDispatchConfig.getParentId());
		}
		if(StringUtils.isNotBlank(taskDispatchConfig.getDelFlag())){//删除标记
			sb.append("  and task.delFlag = ?");
			valueList.add(taskDispatchConfig.getDelFlag());
		}
		sb.append(" order by task.taskNo asc ");
		List list=this.find(sb.toString(),valueList,page);
		Map map = new HashMap();
		map.put("totalProperty", "results," + page.getTotalCount());
		map.put("root", "rows");
		return JsonUtil.fromCollections(list,map);
	}
	/**
	 * 根据主任务ID查询主任务及其下级子任务
	 * @param taskDispatchConfig 主任务实体类
	 */
	@Override
	public String queryTaskConfigListById(TaskDispatchConfig taskDispatchConfig) throws Exception {
		taskDispatchConfig = (TaskDispatchConfig) this.load(taskDispatchConfig.getId(),TaskDispatchConfig.class);
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where  treeCode like ?  ");
		List values = new ArrayList();
		values.add("%"+taskDispatchConfig.getTaskNo()+"%");
		if(StringUtils.isNotBlank(taskDispatchConfig.getDelFlag())){//逻辑删除标记
			hql.append( " and delFlag = ? ");
			values.add(taskDispatchConfig.getDelFlag());
		}
		hql.append( " order by sortNum asc ");
		List list = this.find(hql.toString(),values);
		return JsonUtil.fromCollections(list);
	}
	/**
	 * 根据任务ID查询任务
	 * @param id 任务ID
	 */
	@Override
	public String queryTaskConfigByIdJSON(String id) throws Exception {
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where id=? ");
		List values = new ArrayList();
		values.add(id);
		List list = this.find(hql.toString(),values);
		return JsonUtil.fromCollections(list);
	}
	/**
	 * <p>
	 *  方法名称: bulidTaskNo|描述: 自动生成任务调度编号
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	@Override
	public String bulidTaskNo(TaskDispatchConfig taskDispatchConfig) {
		String taskNo = "";
		int num =100100;//初始默认编号
		taskNo=this.queryMaxTaskNo(taskDispatchConfig.getTaskType(),"1");
		if (StringUtil.isNotBlank(taskNo)){
			num = Integer.parseInt(taskNo.substring(4))+1;
		}
		taskNo=taskDispatchConfig.getTaskType()+num;
		return taskNo;
	}
	/**
	 * <p>
	 *  方法名称: bulidSortNum|描述: 自动生成任务排序号
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	@Override
	public Integer bulidSortNum(TaskDispatchConfig taskDispatchConfig) {
		String taskNo = "";
		Integer num =0;//初始默认编号
		taskNo=this.queryMaxTaskNo(taskDispatchConfig.getTaskType(),"2");
		if (StringUtil.isNotBlank(taskNo)){
			num = Integer.parseInt(taskNo)+1;
		}
		return num;
	}
	/**
	 * <p>
	 *  方法名称: txUpdateSubDisConfig|描述: 根据主任务逻辑删除信时，更新子任务信息
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	@Override
	public void txUpdateSubDisConfig(TaskDispatchConfig taskDispatchConfig,User user) {
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where  treeCode like ?  ");
		List values = new ArrayList();
		values.add("%"+taskDispatchConfig.getTaskNo()+"%");
		hql.append( " order by sortNum asc ");
		List list = this.find(hql.toString(),values);
		List uConfigs = new ArrayList();
		if(null!=list && list.size()>0){
			for(int i=0;i<list.size();i++){
				TaskDispatchConfig config = (TaskDispatchConfig) list.get(i);
				config.setUpdateDate(DateUtils.getWorkDayDate());
				config.setUpdateUserName(user.getName());
				config.setUpdateUserId(user.getId());
				config.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_YES);
				uConfigs.add(config);
			}
			this.txStoreAll(uConfigs);
		}
	}
	/**
	 * <p>
	 *  方法名称: checkTaskIsRuning|描述: 主任务修改时，判断其执行状态
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	@Override
	public Boolean checkTaskIsRuning(TaskDispatchConfig taskDispatchConfig) {
		Boolean flag = true;
		StringBuffer hql = new StringBuffer(" from AutoTaskExe where  taskId = ? and status = ? and status = ?  ");
		List values = new ArrayList();
		values.add(taskDispatchConfig.getId());
		values.add(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_QUEUE);
		values.add(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_RUNNING);
		List list = this.find(hql.toString(),values);
		if(null!=list && list.size()>0){
			flag = false;
		}
		return flag;
	}
	/**
	 * <p>
	 *  方法名称: checkTaskIsExist|描述: 主任务修改时，判断其执行状态
	 * @param taskId  任务调度id
	 * </p>
	 */
	@Override
	public Boolean checkTaskIsExist(String taskId) {
		Boolean flag = true;
		StringBuffer hql = new StringBuffer(" from AutoTaskExe where  taskId = ?  ");
		List values = new ArrayList();
		values.add(taskId);
		List list = this.find(hql.toString(),values);
		if(null!=list && list.size()>0){
			flag = false;
		}
		return flag;
	}
	/**
	 * 方法名称：queryMaxTaskNo|根据任务类型查询最大任务编号
	 * @param taskType
	 * @param queryType 1:查询最大任务编号 2：查询最大排序号
	 * @return
	 */
	public String queryMaxTaskNo(String taskType,String queryType){
		StringBuffer sb = new StringBuffer();
		List valueList = new ArrayList(); // 要查询的值列表
		if("1".equals(queryType)){
			sb.append("select max(task.taskNo) from TaskDispatchConfig task where 1 = 1 ");
		}else{
			sb.append("select max(task.sortNum) from TaskDispatchConfig task where 1 = 1 ");
		}
		if(StringUtils.isNotBlank(taskType)){//类型
			sb.append(" and task.taskType = ? ");
			valueList.add(taskType);
		}
		sb.append(" order by task.createDate desc ");
		List list=this.find(sb.toString(),valueList);
		if(null!=list && list.size()>0){
			if("1".equals(queryType)){
				return (String) list.get(0);
			}else {
				if(list.get(0)!=null){
					return Integer.toString((Integer) list.get(0));
				}else {
					return null;
				}
			}
		}else{
			return null;
		}
	}

	@Override
	public String queryTaskExeConfigList(TaskDispatchConfigBean taskDispatchConfigBean, User user, Page page) throws Exception {
		StringBuffer sb = new StringBuffer();
		List parasValue = new ArrayList();//查询条件值
		List parasName = new ArrayList();//查询条件名
		sb.append("select  task, exe ");
		sb.append(" from TaskDispatchConfig task,AutoTaskExe exe where task.id = exe.taskId  and task.parentId = :parentId");
		parasName.add("parentId");
		parasValue.add("-1");
		if(StringUtils.isNotBlank(taskDispatchConfigBean.getId())){
			sb.append(" and task.id = :id ") ;
			parasName.add("id");
			parasValue.add(taskDispatchConfigBean.getId());
		}
		/*if(StringUtils.isNotBlank(taskDispatchConfigBean.getTaskDesc())){//批次号,待数据库字段新增时放开
			sb.append("  and exe.batchNo =:batchNo ");
			parasName.add("batchNo");
			parasValue.add(taskDispatchConfigBean.getBatchNo());
		}*/
		if(StringUtils.isNotBlank(taskDispatchConfigBean.getTaskNo())){//任务编号
			sb.append(" and task.taskNo = :taskNo ");
			parasName.add("taskNo");
			parasValue.add(taskDispatchConfigBean.getTaskNo());
		}
		if(StringUtils.isNotBlank(taskDispatchConfigBean.getBusiId())){//业务ID
			sb.append(" and exe.busiId = :busiId ");
			parasName.add("busiId");
			parasValue.add(taskDispatchConfigBean.getBusiId());
		}
		if(StringUtils.isNotBlank(taskDispatchConfigBean.getTaskType())){//业务类型
			sb.append(" and task.taskType = :taskType ");
			parasName.add("taskType");
			parasValue.add(taskDispatchConfigBean.getTaskType());
		}
		if(StringUtils.isNotBlank(taskDispatchConfigBean.getStatus())){//执行状态
			sb.append(" and exe.status = :status ");
			parasName.add("status");
			parasValue.add(taskDispatchConfigBean.getStatus());
		}
		if(StringUtils.isNotBlank(taskDispatchConfigBean.getTaskName())){//任务名称
			sb.append(" and task.taskName like :taskName ");
			parasName.add("taskName");
			parasValue.add("%"+taskDispatchConfigBean.getTaskName()+"%");
		}

		if(StringUtils.isNotBlank(taskDispatchConfigBean.getDelFlag())){//删除标记
			sb.append("  and exe.delFlag =:delFlag ");
			parasName.add("delFlag");
			parasValue.add(taskDispatchConfigBean.getDelFlag());
		}
		if(StringUtils.isNotBlank(taskDispatchConfigBean.getBatchNo())){//批次ID
			sb.append("  and exe.batchNo =:batchNo ");
			parasName.add("batchNo");
			parasValue.add(taskDispatchConfigBean.getBatchNo());
		}
		sb.append(" order by exe.createDate desc ,task.sortNum asc ");
		//查询结果集
		String [] nameForSetVar = (String [])parasName.toArray(new String[parasName.size()]);//查询条件名
		Object[] parameters = parasValue.toArray(); //查询条件值
		List list = this.find("distinct exe.id",sb.toString(), nameForSetVar, parameters, page);
		List beanList = new ArrayList();
		if(null!=list && list.size()>0){
			for(int i=0;i<list.size();i++){
				TaskDispatchConfigBean bean = new TaskDispatchConfigBean();
				Object[] obj = (Object[]) list.get(i);
				TaskDispatchConfig config = (TaskDispatchConfig) obj[0];
				AutoTaskExe exe = (AutoTaskExe) obj[1];
				bean.setId(exe.getId());
				bean.setTaskId(config.getId());
				bean.setParentId(config.getProductId());
				bean.setTaskNo(config.getTaskNo());
				bean.setTreeCode(exe.getTreeCode());
				bean.setMemberCode(config.getMemberCode());
				bean.setTaskType(config.getTaskType());
				bean.setTaskDesc(config.getTaskDesc());
				bean.setTaskName(config.getTaskName());
				bean.setCreateDate(exe.getCreateDate());
				bean.setResetCout(exe.getResetCout());
				bean.setStatus(exe.getStatus());
				bean.setStartDate(exe.getStartDate());
				bean.setEndDate(exe.getEndDate());
				bean.setReqParams(exe.getReqParams());
				bean.setResultDesc(exe.getResultDesc());
				bean.setProceStatus(exe.getProceStatus());
				bean.setBusiId(exe.getBusiId());

				bean.setBatchNo(exe.getBatchNo());//批次号
				bean.setDeptName(exe.getDeptId());//机构id
				bean.setDeptNumber(exe.getDeptName());//机构行号
				bean.setBpsNo(exe.getBpsNo());//票据池编号
				bean.setBpsName(exe.getBpsName());//票据池名称

				bean.setTaskSource(config.getTaskSource());//跳转资源url
				bean.setQueueNode(config.getQueueNode());//任务节点
				beanList.add(bean);
			}
			Map map = new HashMap();
			map.put("totalProperty", "results," + page.getTotalCount());
			map.put("root", "rows");
			return JsonUtil.fromCollections(beanList,map);
		}else{
			return null;
		}
	}
    /**
     * 根据主任务ID查询主任务及其下级子任务执行信息
     * @param taskDispatchConfigBean 任务调度实体封装类
     */
    @Override
    public String queryTaskExeConfigListById(TaskDispatchConfigBean taskDispatchConfigBean) throws Exception {
    	AutoTaskExe exe=autoTaskExeService.getAutoTaskExeById(taskDispatchConfigBean.getId());
        TaskDispatchConfig taskDispatchConfig = (TaskDispatchConfig) this.load(taskDispatchConfigBean.getTaskId(),TaskDispatchConfig.class);
		List values = new ArrayList();
		StringBuffer hql = new StringBuffer("");
		hql.append("select distinct (exe.id),task.parentId,task.taskNo,task.memberCode,task.taskType,task.taskDesc,task.taskName,task.createDate, ");
		hql.append(" exe.resetCout,exe.status,exe.startDate,exe.endDate,exe.reqParams,exe.resultDesc ,task.className,exe.busiId ,exe.productId,task.id,exe.errorCount, ");
		hql.append(" exe.busiId,task.queueNode,task.sortNum ");
		hql.append(" from TaskDispatchConfig task,AutoTaskExe exe where task.id = exe.taskId ");
		hql.append(" and  exe.treeCode like ?  ");
        values.add("%"+taskDispatchConfig.getTreeCode()+"%");//AutoTaskExe中的treeCode
        hql.append(" and  exe.busiId =?  ");//????
        values.add(exe.getBusiId());//AutoTaskExe中的业务ID区分各业务
        hql.append( " order by task.sortNum asc ");
        List list = this.find(hql.toString(),values);
		List beanList = new ArrayList();
		if(null!=list && list.size()>0){
			for(int i=0;i<list.size();i++){
				TaskDispatchConfigBean bean = new TaskDispatchConfigBean();
				Object[] obj = (Object[]) list.get(i);
				bean.setId((String) obj[0]);
				bean.setParentId((String) obj[1]);
				bean.setTaskNo((String) obj[2]);
				bean.setMemberCode((String) obj[3]);
				bean.setTaskType((String) obj[4]);
				bean.setTaskDesc((String) obj[5]);
				bean.setTaskName((String) obj[6]);
				bean.setCreateDate((Date) obj[7]);
				bean.setResetCout((Integer) obj[8]);
				bean.setStatus((String) obj[9]);
				bean.setStartDate((Date) obj[10]);
				bean.setEndDate((Date) obj[11]);
				bean.setReqParams((String) obj[12]);
				bean.setResultDesc((String) obj[13]);
				bean.setClassName((String) obj[14]);
				bean.setBusiId((String) obj[15]);
				bean.setProductId((String) obj[16]);
				bean.setTaskId((String) obj[17]);
				bean.setErrorCount((Integer) obj[18]);
				bean.setQueueNode((String) obj[20]);
				beanList.add(bean);
			}
			return JsonUtil.fromCollections(beanList);
		}else{
			return null;
		}
    }
	/**
	 * <p>
	 *  方法名称: txUpdateTaskExeResetCout|描述: 异常任务重读执行时，更新执行表处理次数
	 * @param taskDispatchConfigBean  任务调度配置实体类封装
	 * </p>
	 */
	@Override
	public void txUpdateTaskExeResetCout(TaskDispatchConfigBean taskDispatchConfigBean,User user) {
		AutoTaskExe autoTaskExe = (AutoTaskExe) this.load(taskDispatchConfigBean.getId(),AutoTaskExe.class);
//		if(taskDispatchConfigBean.getExeResetCout()>=taskDispatchConfigBean.getResetCout()){
//			autoTaskExe.setResetCout(0);
//		}else{
//			autoTaskExe.setResetCout(autoTaskExe.getResetCout()+1);
//		}
		autoTaskExe.setDeptId(user.getDeptId());//操作机构id
		autoTaskExe.setDeptName(user.getDeptNm());//操作机构名称
		this.txStore(autoTaskExe);

	}

	/**
	 * <p>
	 *  方法名称: txUpdtTaskDispatchConfig|描述: 封装更新调度
	 * </p>
	 * @param type  1:新增 2：修改
	 */
	@Override
	public TaskDispatchConfig txUpdtTaskDispatchConfig(TaskDispatchConfig taskDispatchConfig, String type, User user) throws Exception {
		TaskDispatchConfig config = null;
		Map map= new HashMap();
		if ("1".equals(type)){//新增
			config = new TaskDispatchConfig();
			config.setTaskNo(this.bulidTaskNo(taskDispatchConfig));//调用自动生成任务编号方法
			config.setSortNum(this.bulidSortNum(taskDispatchConfig));//自动生成排序号
			config.setCreateUserId(user.getId());//创建用户ID
			config.setCreateUserName(user.getName());//创建用户名称
			config.setCreateDate(DateUtils.getCurrDateTime());//创建时间
			config.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//初始化未删除标记
			config.setMemberCode(user.getDepartment().getPjsMemberCode());//会员编号
			config.setDeptId(user.getDeptId());//机构id
			config.setDeptName(user.getDepartment().getName());//机构名称
			/** 20210623判断任务节点是否存在 */
			this.checkQueueNodeIsExist(taskDispatchConfig.getQueueNode());
		}else{//修改
			config = (TaskDispatchConfig) this.load(taskDispatchConfig.getId(),TaskDispatchConfig.class);
			//修改之前先判断任务是否在执行中，若在执行中，则不允许修改
			Boolean flag = this.checkTaskIsRuning(config);
			if(!flag){
				throw  new Exception("当前任务：【"+config.getTaskName()+"】处于可执行中状态，不允许修改！");
			}
			config.setUpdateUserId(user.getId());//更新用户ID
			config.setUpdateUserName(user.getName());//更新用户名称
			config.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
		}
		PoolQueryBean bean=new PoolQueryBean();
		bean.setQueueName(taskDispatchConfig.getQueueName());
		bean.setQueueNode(taskDispatchConfig.getQueueNode());
		bean.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);
		TaskDispatchConfig task=this.queryTaskDispatchConfigByParm(bean);
		if("1".equals(type)&&null!=task){
			throw  new Exception("当前任务：【"+config.getTaskName()+"】已有相同分发节点不允许保存！");
		}
		config.setTaskName(taskDispatchConfig.getTaskName());//任务名称
		config.setParentId(taskDispatchConfig.getParentId());//父任务ID
		config.setTaskType(taskDispatchConfig.getTaskType());//任务类型
		config.setStatus(taskDispatchConfig.getStatus());//任务状态
		config.setQueueName(taskDispatchConfig.getQueueName());//队列名称
		config.setQueueNode(taskDispatchConfig.getQueueNode());//队列分发节点
		config.setResetCout(taskDispatchConfig.getResetCout());//重读处理次数
		config.setSleepTime(taskDispatchConfig.getSleepTime());//延迟时间
		config.setWaiteTime(taskDispatchConfig.getWaiteTime());//等待时间
		QueueDispatchNode node=this.queryQueueNode(taskDispatchConfig.getQueueNode());
		config.setProductId(node.getProductId());//产品ID
		/** 20210603 判断调度类是否存在  */
		this.checkClassNameIsExist(taskDispatchConfig.getClassName().trim());
		config.setClassName(taskDispatchConfig.getClassName().trim());//调度类
		config.setTaskDesc(taskDispatchConfig.getTaskDesc());//任务描述
		/** 202106017 增加跳转资源url */
		if(StringUtils.isNotBlank(taskDispatchConfig.getTaskSourceId())){
			config.setTaskSourceId(taskDispatchConfig.getTaskSourceId());
			Resource resource = (Resource) this.load(taskDispatchConfig.getTaskSourceId(),Resource.class);
			String url="" ;
			url =this.createResUrl(resource,url);
			config.setTaskSource(url);
		}
		this.txStore(config);
		return config;
	}

	/**
	 * 递归方式获取资源url
	 * @param resource
	 * @param url
	 * @return
	 */
	public String createResUrl(Resource resource,String url){
		url="/"+resource.getCode()+url;
		if (null!=resource.getParent()){
			 url=this.createResUrl(resource.getParent(),url);
		}
		return  url;
	}
	/**
	 * 子任务修改--保存新增或修改的子任务
	 * 采用递归方式
	 */
	@Override
	public void txSaveSubCofigForEdit(List<TaskDispatchConfig> list, TaskDispatchConfig lastConfig,User user) throws Exception {
		TaskDispatchConfig config = null;
		for (int i=0;i<list.size();i++){
			config = list.get(i);
			if("-1".equals(config.getParentId())){//主任务
				if(null!=config.getChildren() && config.getChildren().size()>0){
					this.txSaveSubCofigForEdit(config.getChildren(),config,user);
				}
				return;
			}
			List<TaskDispatchConfig> childslist = config.getChildren();
			if (StringUtils.isNotBlank(config.getId())){//修改
				config = this.txUpdtTaskDispatchConfig(config,"2",user);
				this.txStore(config);
			}else{//新增
				config =  this.txUpdtTaskDispatchConfig(config,"1",user);
				config.setTreeCode(lastConfig.getTreeCode()+config.getTaskNo());//树编号
				config.setParentId(lastConfig.getId());
				this.txStore(config);
			}
			//递归方式去处理
			if(null!=childslist && childslist.size()>0){
				this.txSaveSubCofigForEdit(childslist,config,user);
			}
		}
	}
	/**
	 * <p>
	 * 方法名称: txDelSubTaskDispatchConfig|描述: 删除主任务对应的子任务
	 * </p>
	 */
	@Override
	public void txDelSubTaskDispatchConfig(String ids, User user) throws Exception {
		TaskDispatchConfig config = null;
		List listId = StringUtil.splitList(ids, ",");
		if(null!=listId && listId.size()>0){
			for (int i=0;i<listId.size();i++){
				String id = (String) listId.get(i);
				config = (TaskDispatchConfig) this.load(id,TaskDispatchConfig.class);
				Boolean flag = this.checkTaskIsExist(config.getId());
				if(flag){//物理删除--不在执行表中
					this.txDelete(config);
				}else{//逻辑删除
					this.txUpdateSubDisConfig(config,user);
				}
				logger.info("删除调度子任务配置【"+config.getTaskNo()+"】操作人【"+user.getLoginName()+"】！");
			}
		}
	}

	/**
	 * <p>
	 * 方法名称: checkClassNameIsExist|描述: 判断调度类名是否存在
	 * </p>
	 * @return
	 */
	public void checkClassNameIsExist(String className) throws  Exception{
		//通过反转生成调度类
		AutoTaskDispatchAbstract autoTaskDispatch = null;
		try {
			autoTaskDispatch= (AutoTaskDispatchAbstract)  Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			logger.error(ErrorCode.ERR_MSG_994 ,e);
		} catch (IllegalAccessException e) {
			logger.error(ErrorCode.ERR_MSG_994 ,e);
		} catch (ClassNotFoundException e) {
			logger.error(ErrorCode.ERR_MSG_994 ,e);
		}
		if(autoTaskDispatch == null) {
			throw new Exception("根据任务调度类名："+className+"找不到对应的任务调度！");
		}
	}
	/**
	 * <p>
	 * 方法名称: checkQueueNodeIsExist|描述: 判断任务节点是否存在
	 * </p>
	 * @return
	 */
	public void checkQueueNodeIsExist(String queueNode) throws  Exception{
		QueueDispatchNode node = null;
		try {
		 node=this.queryTaskDispatchConfigQueueNode(queueNode);
		}catch (Exception e) {
			logger.error(ErrorCode.ERR_MSG_994 ,e);
		}
		if(null!=node) {
			throw new Exception("任务节点："+queueNode+"已存在！");
		}
	}
	/**
	 * 根据队列名称查找分发节点json
	 * @param queueName 任务队列名称
	 */
	public String queryQueueNodeJSON(String queueName) throws Exception {
		StringBuffer hql = new StringBuffer(" from QueueDispatchNode where queueName=? ");
		List values = new ArrayList();
		values.add(queueName);
		List list = this.find(hql.toString(),values);
		return JsonUtil.fromCollections(list);
	}
	/**
	 * 根据分发节点查询
	 * @param queueNode 任务节点
	 */
	public QueueDispatchNode queryTaskDispatchConfigQueueNode(String queueNode) throws Exception {
		StringBuffer hql = new StringBuffer(" from TaskDispatchConfig where queueNode=? ");
		List values = new ArrayList();
		values.add(queueNode);
		List list = this.find(hql.toString(),values);
		if(list.size()>0){
			return (QueueDispatchNode)list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 根据分发节点查询
	 * @param queueNode 任务节点
	 */
	public QueueDispatchNode queryQueueNode(String queueNode) throws Exception {
		StringBuffer hql = new StringBuffer(" from QueueDispatchNode where queueNode=? ");
		List values = new ArrayList();
		values.add(queueNode);
		List list = this.find(hql.toString(),values);
		if(list.size()>0){
			return (QueueDispatchNode)list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 根据树节点查找执行任务及其子任务
	 * @param treeCode 任务树节点
	 * @param status 执行状态
	 * @param busiId 原业务ID
	 */
	@Override
	public List<AutoTaskExe> queryTaskExeByTreeCode(String treeCode, String status,String busiId) throws Exception {
		StringBuffer hql = new StringBuffer( " from AutoTaskExe a where 1=1 ");
		List values = new ArrayList();
		if (StringUtils.isNotBlank(treeCode)){
			hql.append(" and a.treeCode like ?");
			values.add("%"+treeCode+"%");
		}
		if (StringUtils.isNotBlank(status)){
			hql.append(" and a.status = ?");
			values.add(status);
		}
		if (StringUtils.isNotBlank(busiId)){
			hql.append(" and a.busiId = ?");
			values.add(busiId);
		}
		List<AutoTaskExe> list = this.find(hql.toString(),values);
		return  list;
	}

	public Class getEntity() {
		return  TaskDispatchConfig.class;
	}

	public Class getEntityClass() {
		return TaskDispatchConfig.class;
	}

	public String getEntityName() {

		return "TaskDispatchConfig";
	}

}
