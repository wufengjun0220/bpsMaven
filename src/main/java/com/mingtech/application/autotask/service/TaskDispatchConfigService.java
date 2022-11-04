package com.mingtech.application.autotask.service;

import java.util.List;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.QueueDispatchNode;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.domain.TaskDispatchConfigBean;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

/**
 * <p>说明:业务自动处理登记接口定义</p>
 * @author h2
 */
public interface TaskDispatchConfigService extends GenericService{
	
	/**
	 * 根据ID获取任务调度配置信息
	 * @param id 任务调度配置ID
	 */
	public TaskDispatchConfig getTaskDispatchConfigById(String id);
	
	/**
	 * 根据会员编码和任务编号查询任务调度配置信息
	 * @param memberCode 会员编码
	 * @param taskNo 任务编号
	 */
	public TaskDispatchConfig getTaskDispatchConfigByMemberCodeAndTaskNo(String memberCode,String taskNo);
	
	/**
	 * 根据会员编码和父任务ID、父任务编号查询下级所有子任务配置信息
	 * @param memberCode 会员编码
	 * @param id 父任务配置ID
	 * @param taskNo 父任务编号
	 */
	public List<TaskDispatchConfig> queryAllChildTaskDispatchConfigs(String memberCode,String id,String taskNo);
	
	
	/**
	 * 根据父ID和任务类型查询下属任务调度配置信息
	 * @param id 任务调度配置ID
	 * @param taskType 任务类型
	 */
	public List queryEffectTaskDispatchConfigByParentId(String parentId,String taskType);
	
	/**
	 * 根据父ID和任务类型查询下属任务调度配置信息ID
	 * @param id 任务调度配置ID
	 * @param taskType 任务类型
	 */
	public List queryEffectTaskDispatchConfigIdsByParentId(String parentId,String taskType);
	
	/**
	 * 根据条件查询 TaskDispatchConfig自动任务调度配置实体
	 * @param PoolQueryBean  gcj 20210517
	 * @return TaskDispatchConfig 自动任务调度配置实体
	 */
	public TaskDispatchConfig queryTaskDispatchConfigByParm(PoolQueryBean bean);
	/**
	 * 根据会员编码、父任务编号查询当前任务以及下级所有子任务配置信息
	 * @param memberCode 会员编码
	 * @param taskNo 父任务编号
	 */
	public List<TaskDispatchConfig> queryAllTaskDispatchConfigs(String memberCode,String taskNo);
	/**
	 * 查询任务调度配置信息
	 * @param user 当前用户
	 * @param page
	 * @param taskDispatchConfig 任务调度实体
	 */
	public String querySystemConfigList(TaskDispatchConfig taskDispatchConfig, User user, Page page) throws Exception;
	/**
	 * 根据主任务ID查询主任务及其下级子任务
	 * @param taskDispatchConfig 主任务ID
	 */
	public String queryTaskConfigListById(TaskDispatchConfig taskDispatchConfig) throws Exception;
	/**
	 * 根据任务ID查询任务
	 * @param id 任务ID
	 */
	public String queryTaskConfigByIdJSON(String id) throws Exception;
	/**
	 * <p>
	 *  方法名称: bulidTaskNo|描述: 自动生成任务调度编号
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	public  String bulidTaskNo(TaskDispatchConfig taskDispatchConfig);
	/**
	 * <p>
	 *  方法名称: bulidSortNum|描述: 自动生成任务排序号
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	public  Integer bulidSortNum(TaskDispatchConfig taskDispatchConfig);
	/**
	 * <p>
	 *  方法名称: txUpdateSubDisConfig|描述: 更具主任务删除信息更新子任务信息
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	public  void txUpdateSubDisConfig(TaskDispatchConfig taskDispatchConfig,User user);
	/**
	 * <p>
	 *  方法名称: checkTaskIsRuning|描述: 主任务修改时，判断其执行状态
	 * @param taskDispatchConfig  任务调度配置实体类
	 * </p>
	 */
	public Boolean checkTaskIsRuning(TaskDispatchConfig taskDispatchConfig);
	/**
	 * <p>
	 *  方法名称: checkTaskIsExist|描述: 主任务修改时，判断其执行状态
	 * @param taskId  任务调度id
	 * </p>
	 */
	public Boolean checkTaskIsExist(String taskId);
	/**
	 * 查询任务调度配置以及执行状态信息
	 * @param user 当前用户
	 * @param page
	 * @param taskDispatchConfigBean 任务调度实体封装类
	 */
	public String queryTaskExeConfigList(TaskDispatchConfigBean taskDispatchConfigBean, User user, Page page) throws Exception;
	/**
	 * 根据主任务ID查询主任务及其下级子任务执行信息
	 * @param taskDispatchConfigBean 任务调度实体封装类
	 */
	public String queryTaskExeConfigListById(TaskDispatchConfigBean taskDispatchConfigBean) throws Exception;
	/**
	 * <p>
	 *  方法名称: txUpdateTaskExeResetCout|描述: 异常任务重读执行时，更新执行表处理次数
	 * @param taskDispatchConfigBean  任务调度配置实体类封装
	 * </p>
	 */
	public  void txUpdateTaskExeResetCout(TaskDispatchConfigBean taskDispatchConfigBean,User user);
	/**
	 * <p>
	 *  方法名称: txUpdtTaskDispatchConfig|描述: 封装更新调度
	 * </p>
	 * @param type  1:新增 2：修改
	 */
	public TaskDispatchConfig txUpdtTaskDispatchConfig(TaskDispatchConfig taskDispatchConfig,String type,User user) throws Exception;
	/**
	 * 子任务修改--保存新增或修改的子任务
	 * 采用递归方式
	 */
	public void txSaveSubCofigForEdit(List<TaskDispatchConfig> list,TaskDispatchConfig lastConfig,User user) throws Exception;
	/**
	 * <p>
	 * 方法名称: txDelSubTaskDispatchConfig|描述: 删除主任务对应的子任务
	 * </p>
	 */
	public void txDelSubTaskDispatchConfig(String ids ,User user) throws  Exception;
	/**
	 * 根据队列名称查找分发节点
	 * @param queueName 任务队列名称
	 */
	public String queryQueueNodeJSON(String queueName) throws Exception;
	/**
	 * 根据队列名称查找分发节点
	 * @param queueName 任务队列名称
	 */
	public QueueDispatchNode queryQueueNode(String queueName) throws Exception;
	/**
	 * 根据树节点查找执行任务及其子任务
	 * @param treeCode 任务队列名称
	 */
	public List<AutoTaskExe>  queryTaskExeByTreeCode(String treeCode,String status,String busiId) throws Exception;
}

