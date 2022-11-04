package com.mingtech.application.autotask.service;

import java.util.List;
import java.util.Map;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.framework.core.service.GenericService;

/**
 * <p>说明:业务自动处理登记接口定义</p>
 * @author h2
 */
public interface AutoTaskExeService extends GenericService{
	
	/**
	 * 根据ID获取任务执行流水信息
	 * @param id 自动任务执行流水ID
	 */
	public AutoTaskExe getAutoTaskExeById(String id);
	
	
	/**
	 * 根据任务编号将指定业务执行所需的所有任务信息保存到自动任务流水表中，并返回主任务信息
	 * @param memberCode 会员编码
	 * @param taskNo 任务编号
	 * @param busiId 原业务ID
	 * @param productId业务类型(自定义业务类型，方便页面显示)
	 * @param reqParams 任务调度请求参数
	 * @return AutoTaskExe 主任务执行流水信息
	 */
	public AutoTaskExe txSaveAutoTaskExe(String memberCode,String taskNo,String busiId,String productId,Map<String,String> reqParams,
										 String batchNo,String bpsNo,String bpsName,String depId ) throws RuntimeException;

	
	/**
	 * 更新重新处理次数
	 * @param id 主键
	 * @return void
	 */
	public void txUpdateAutoTaskExeResetCount(String id);
	
	/**
	 * 更新自动任务执行业务状态和业务描述
	 * @param id 主键
	 * @param status 状态
	 * @param respDesc 交易描述
	 * @return void
	 */
	public void txUpdateAutoTaskExeStatus(String id,String status,String respDesc);
	
	
	
	/**
	 * 根据原业务ID和任务调度配置ID更新自动任务执行流水状态和请求参数
	 * @param id 主键
	 * @param taskIds任务调度配置信息ID
	 * @param reqParams 自动任务执行流水请求参数
	 * @return list 已更新的自动任务流水ID
	 */
	public List txUpdateNextAutoTaskExes(String id,List taskIds,String reqParams);
	
	/**
	 * 更新自动任务执行调度开始时间
	 * @param id 主键
	 * @return void
	 */
	public void  txUpdateAutoTaskExeStartTime(String id);
	/**
	 * 更新自动任务执行调度结束时间
	 * @param id 主键
	 * @return void
	 */
	public void  txUpdateAutoTaskExeEndTime(String id);
	
	/**
	 * 根据条件查询 AutoTaskExe主任务执行流水信息表
	 * @param PoolQueryBean 
	 * @return AutoTaskExe 主任务执行流水信息
	 */
	public AutoTaskExe queryAutoTaskExeByParm(PoolQueryBean poolQueryBean);
	 /**
     * <p>根据队列节点 队列名称 业务ID 查找自动任务业务表<p/>
     * @param queueNode 队列节点
     * @param queueName 队列名称
     * @param busiId 业务ID
     * @return AutoTaskExe
     */
    public AutoTaskExe doAutoTaskExe(String queueNode,String queueName,String busiId);
    /**
	 * 通过自动任务业务流水ID  删除所有任务  逻辑删除
	 * @param id AutoTaskExe中主键
	 * @return void
	 */
	public void txUpdateAutoTaskExeDelFlag(String id);
	  /**
       * 通过参数更新自动业务流水表
	   * @param memberCode 会员编码
	   * @param taskNo 任务编号
	   * @param busiId 原业务ID
	   * @param productId业务类型(自定义业务类型，方便页面显示)
	   * @param reqParams 任务调度请求参数
	   * @return AutoTaskExe
       */
	public AutoTaskExe txAutoTaskExeInfo(String memberCode,String treeCode,String busiId,String productId,Map<String,String> reqParams);

	/**
	 * 更新自动任务出错次数
	 * @param id 主键
	 * @return void
	 */
	public void  txUpdateAutoTaskExeErrorCount(String id);
	/**
	 * @Title txUpdateAutoTaskExeDelFlag
	 * @author wss
	 * @date 2021-5-20
	 * @Description 通过自动任务业务树叶子节点、原业务ID 、队列名称  删除所有任务  逻辑删除
	 * @param treeCode 树叶子节点
	 * @param busiId 原业务ID
	 * @param queueName 队列名称
	 * @throws Exception 
	 */
	public void txUpdateAutoTaskExeDelFlag(String poolOnlineRegisterNo,String poolOnlineAcpt, String id) throws Exception;

	/**
	 * @Title txUpdateErrorCount
	 * @author wss
	 * @date 2021-5-20
	 * @Description 变更该条任务执行次数
	 * @return void
	 */
	public void txUpdateErrorCount(AutoTaskExe autoTaskExe);

	/**
	 * @Title txUpdateProceStatus
	 * @author zjt
	 * @date 2021-6-2
	 * @Description 调度失败后变更流程状态
	 * @return void
	 */
	public void txUpdateProceStatus(AutoTaskExe autoTaskExe,String status,String respDesc);
	/**
	 * @Title queryAutoTaskExe
	 * @author zjt
	 * @date 2021-6-2
	 * @Description 根据会员编号和树编号找到对应的任务执行信息
	 * @return AutoTaskExe
	 */
	public AutoTaskExe queryAutoTaskExe(String memberCode,String treeCode,String busiId);

	/**
	 * @Title queryAutoTaskExeByCondition
	 * @author wss
	 * @date 2021-6-4
	 * @Description 根据条件查询任务
	 * @return List
	 */
	public List queryAutoTaskExeByCondition(PoolQueryBean poolQueryBean);
	
	/**
	 * @Title txAutoExeFailDispatch
	 * @author gcj
	 * @date 2021-7-14
	 * @Description 查询失败的自动任务 重新唤醒
	 * @return List
	 */
	public void txAutoExeFailDispatch(String hour,String min) throws Exception;
	/**
	 * @Title txAutoExeFailDispatch
	 * @author gcj
	 * @date 2021-7-14
	 * @Description 查询未启用的自动任务 前置任务成功停留五分钟以上的 重新唤醒
	 * @return List
	 */
	public void txAutoExeWaitDispatch(String hour,String min) throws Exception;
	/**
	 * @Title queryAutoTaskExeOrConfig
	 * @author wss
	 * @date 2021-7-14
	 * @Description 根据条件查询任务
	 * @return AutoTaskExe
	 */
	public List queryAutoTaskExeOrConfig(PoolQueryBean bean) throws Exception;

	 /**
     * <p>根据队列节点 队列名称 业务ID 查找自动任务业务表<p/>
     * @param queueNode 队列节点
     * @param queueName 队列名称
     * @param busiId 业务ID
     * @param status 执行状态
     * @return AutoTaskExe
     */
	public AutoTaskExe doAutoTaskExe(List queueNode,
			List queueName, String id, String status);
	/**
	 * @Title txAutoExeFailDispatch
	 * @author wfj
	 * @date 2021-8-12
	 * @param id 业务流程的id(界面触发是传值)
	 * @Description 查询出入池、银承签收、未用退回、撤票交易，未收到bbsp 通知的数据
	 * @return List
	 */
	public void txAutoExeTaskStatus(String hour,String min,String id) throws Exception;
	
	/**
	 * 自动调度唤醒操作
	 * @param task
	 * @param hour 最大执行时间(小时)
	 * @param min  最小执行时间(分钟)
	 * @return
	 * @throws Exception
	 */
	public Map AutoPublishWaitTask(PoolQueryBean task, String hour,
			String min) throws Exception;
	
	/**
	 * 失败任务的重新唤醒或界面点击的唤醒
	 * @param task
	 * @param hour
	 * @param min
	 * @return
	 * @throws Exception
	 */
	public boolean AutoPublishErrorWaitTask(PoolQueryBean task, String hour,String min) throws Exception;
}

