package com.mingtech.application.autotask.web;

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

import com.alibaba.fastjson.JSON;
import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.domain.TaskDispatchConfigBean;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.service.TaskDispatchConfigService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.StringUtil;

@Controller
public class TaskDispatchConfigController extends BaseController {
    private static final Logger logger = Logger.getLogger(TaskDispatchConfigController.class);
    @Autowired
    private TaskDispatchConfigService taskDispatchConfigService;
    @Autowired
    private AutoTaskPublishService autoTaskPublishService;
    @Autowired
    private AutoTaskExeService autoTaskExeService;

    /**  ----------------------------   统一任务调度管理 start     ---------------------------------    */
    /**
     * <p>
     * 方法名称: loadSystemConfigJSON|描述: 查询任务调度配置列表JSON
     * </p>
     */
    @RequestMapping("/queryTaskConfigList")
    public void queryTaskConfigList(TaskDispatchConfig taskDispatchConfig) {
        try {
            taskDispatchConfig.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除标记
            taskDispatchConfig.setParentId("-1");//代表主任务查询
            String json = taskDispatchConfigService.querySystemConfigList(taskDispatchConfig, this.getCurrentUser(),this.getPage() );
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString(),e);
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * <p>
     * 方法名称: loadTaskConfigListByIdJSON|描述: 根据任务调度id查询主任务及其子任务
     * 进入子任务维护界面
     * </p>
     */
    @RequestMapping("/queryTaskConfigListByIdJSON")
    public void queryTaskConfigListByIdJSON(TaskDispatchConfig taskDispatchConfig) {
        try {
            taskDispatchConfig.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除标记
            String json = taskDispatchConfigService.queryTaskConfigListById(taskDispatchConfig);
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString(),e);
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * <p>
     * 方法名称: loadTaskConfigByIdJSON|描述: 查询任务id查询对应任务
     * 点击对应的任务显示对应的任务详情
     * </p>
     */
    @RequestMapping("/queryTaskConfigByIdJSON")
    public void queryTaskConfigByIdJSON(String id) {
        try {
            String json = taskDispatchConfigService.queryTaskConfigByIdJSON(id);
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString());
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * <p>
     * 方法名称: txSaveTaskDispatchConfig|描述: 保存主调度配置修改
     * </p>
     */
    @RequestMapping("/saveTaskDispatchConfig")
    public void saveTaskDispatchConfig(TaskDispatchConfig taskDispatchConfig) {
        User user = this.getCurrentUser();
        TaskDispatchConfig config = null;
        try {
           if (StringUtils.isNotBlank(taskDispatchConfig.getId())){//更新
               taskDispatchConfigService.txUpdtTaskDispatchConfig(taskDispatchConfig,"2",user);
               this.sendJSON("更新调度配置成功");
               logger.info("更新调度配置【"+taskDispatchConfig.getTaskTypeDesc()+"】操作人【"+user.getLoginName()+"】！");
           }else{//新增
               config=taskDispatchConfigService.txUpdtTaskDispatchConfig(taskDispatchConfig,"1",user);
               config.setTreeCode(config.getTaskNo());
               config.setParentId("-1");
               taskDispatchConfigService.txStore(config);
               this.sendJSON("新增调度配置成功");
               logger.info("新增调度配置【"+config.getTaskNo()+"】操作人【"+user.getLoginName()+"】！");
           }
        } catch (Exception e) {
            logger.error("数据库操作失败"+e.getMessage(),e);
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("数据库操作失败："+e.getMessage());
        }
    }
    /**
     * <p>
     * 方法名称: txSaveTaskDispatchConfig|描述: 删除主调度配置
     * </p>
     */
    @RequestMapping("/delTaskDispatchConfig")
    public void delTaskDispatchConfig(String id) {
        User user = this.getCurrentUser();
        TaskDispatchConfig config = null;
        try {
            config = (TaskDispatchConfig) taskDispatchConfigService.load(id,TaskDispatchConfig.class);
            if (null!=config){
                logger.info("删除调度主任务配置【"+config.getTaskNo()+"】操作人【"+user.getLoginName()+"】！");
                //判断任务是否处于待执行或者执行中状态
                Boolean flag = taskDispatchConfigService.checkTaskIsExist(config.getId());
                if(flag){
                    //物理删除主任务
                    taskDispatchConfigService.txDelete(config);
                }else {
                    //逻辑删除主任务以及对应的子任务
                    taskDispatchConfigService.txUpdateSubDisConfig(config,user);
                }
                this.sendJSON("主调度任务删除成功");
            }else {
                logger.info("删除调度主任务配置失败，根据"+id+"未找到对应的任务调度！");
                this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
                this.sendJSON("数据库操作失败：根据"+id+"未找到对应的任务调度！");
            }
        } catch (Exception e) {
            logger.error("数据库操作失败"+e.getMessage());
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("数据库操作失败："+e.getMessage());
        }
    }
    /**
     * <p>
     * 方法名称: txSaveTaskDispatchConfig|描述: 保存子任务调度配置修改
     * </p>
     */
    @RequestMapping("/saveSubTaskDispatchConfig")
    public void saveSubTaskDispatchConfig( String rows,String delIds) {
        List<TaskDispatchConfig> list = JSON.parseArray(rows, TaskDispatchConfig.class);
        User user = this.getCurrentUser();
        List configs = new ArrayList();
        try {
            if(null!= list && list.size()>0){
                //1、保存主任务调度和新增或修改的子任务
                taskDispatchConfigService.txSaveSubCofigForEdit(list,null,user);
            }
            if(StringUtils.isNotBlank(delIds)){
                //2、删除对应的任务调度
                taskDispatchConfigService.txDelSubTaskDispatchConfig(delIds,user);
            }
            this.sendJSON("子任务维护成功");
            logger.info("子任务维护成功，操作人【"+user.getLoginName()+"】！");
        } catch (Exception e) {
            logger.error("数据库操作失败"+e.getMessage());
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("数据库操作失败："+e.getMessage());
        }
    }

    /**
     * <p>
     * 方法名称: txDelSubTaskDispatchConfig|描述: 删除主任务对应的子任务
     * </p>
     */
    @RequestMapping("/delSubTaskDispatchConfig")
    public void delSubTaskDispatchConfig(String configIds) {
        User user = this.getCurrentUser();
        TaskDispatchConfig config = null;
        List delConfig = new ArrayList();
        try {
            taskDispatchConfigService.txDelSubTaskDispatchConfig(configIds,user);
            logger.info("删除调度子任务配置【"+config.getTaskNo()+"】操作人【"+user.getLoginName()+"】！");
        } catch (Exception e) {
            logger.error("数据库操作失败"+e.getMessage());
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("数据库操作失败："+e.getMessage());
        }
    }
    /**
     * <p>
     * 方法名称: loadTaskExeConfigJSON|描述: 自动任务跟踪查询列表JSON
     * </p>
     */
    @RequestMapping("/queryTaskExeConfigList")
    public void queryTaskExeConfigList(TaskDispatchConfigBean taskDispatchConfigBean) {
        try {
            taskDispatchConfigBean.setParentId("-1");//代表主任务查询
            String json = taskDispatchConfigService.queryTaskExeConfigList(taskDispatchConfigBean, this.getCurrentUser(),this.getPage() );
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString());
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * <p>
     * 方法名称: loadTaskExeConfigListByIdJSON|描述: 根据任务调度taskId和执行id查询主任务及其子任务
     * 进入子任务维护界面
     * </p>
     */
    @RequestMapping("/queryTaskExeConfigListByIdJSON")
    public void queryTaskExeConfigListByIdJSON(TaskDispatchConfigBean taskDispatchConfigBean) {
        try {
            taskDispatchConfigBean.setDelFlag(PublicStaticDefineTab.DELETE_FLAG_NO);//未删除标记
            String json = taskDispatchConfigService.queryTaskExeConfigListById(taskDispatchConfigBean);
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString());
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * <p>
     * 方法名称: reRunTaskExe|描述: 重新唤醒任务调度
     * </p>
     */
    @RequestMapping("/reRunTaskExe")
    public void reRunTaskExe(TaskDispatchConfigBean taskDispatchConfigBean) {
        try {
            //1.更新执行表处理次数
            taskDispatchConfigService.txUpdateTaskExeResetCout(taskDispatchConfigBean,this.getCurrentUser());
            //2、调用唤醒执行任务
            Map<String,String> map = new HashMap();
            map.put("reqParams",taskDispatchConfigBean.getReqParams());
            autoTaskPublishService.publishWaitTask( taskDispatchConfigBean.getMemberCode(), taskDispatchConfigBean.getQueueNode(),
                    taskDispatchConfigBean.getBusiId(),taskDispatchConfigBean.getProductId(),map);
        } catch (Exception e) {
            logger.error("数据库操作失败"+e.getMessage());
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("数据库操作失败："+e.getMessage());
        }
    }
    /**
     * <p>
     * 方法名称: reRunTaskExeForBase|描述: 点击主任务，唤醒主任务及下失败的子任务
     * </p>
     */
    @RequestMapping("/reRunTaskExeForBase")
    public void reRunTaskExeForBase(TaskDispatchConfigBean taskDispatchConfigBean) {
        try {
            //0.查询主任务及其失败的任务调度
            List<AutoTaskExe> list = taskDispatchConfigService.queryTaskExeByTreeCode(taskDispatchConfigBean.getTreeCode(),PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR,taskDispatchConfigBean.getBusiId());
            if(list != null && list.size() > 0){
            	for(AutoTaskExe exe:list){
            		TaskDispatchConfig config = (TaskDispatchConfig) taskDispatchConfigService.load(exe.getTaskId(),TaskDispatchConfig.class);
            		TaskDispatchConfigBean bean = new TaskDispatchConfigBean();
            		BeanUtil.beanCopy(exe,bean);
            		bean.setResetCout(taskDispatchConfigBean.getResetCout());
            		//1.更新执行表处理次数3
            		taskDispatchConfigService.txUpdateTaskExeResetCout(bean,this.getCurrentUser());
            		//2、调用唤醒执行任务
            		PoolQueryBean task = new PoolQueryBean();
            		task.setId(exe.getId());
            		task.setBusiId(exe.getBusiId());
            		task.setProductId(exe.getProductId());
            		task.setQueueNode(config.getQueueNode());
            		boolean flag = autoTaskExeService.AutoPublishErrorWaitTask(task, "2", "5");
            		if(!flag){
            			logger.error("异常监控超过五分钟小于两小时无法唤醒");
            			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            			this.sendJSON("异常监控超过五分钟小于两小时无法唤醒!");
            			return;
            		}
            	}
            	//4、更新流程状态
            	AutoTaskExe baseExe = (AutoTaskExe) taskDispatchConfigService.load(taskDispatchConfigBean.getId(),AutoTaskExe.class);
            	baseExe.setProceStatus(PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_INIT);
            	taskDispatchConfigService.txStore(baseExe);
            	this.sendJSON("任务执行成功！");
            }else{
            	logger.error("流程处理中未失败,请等待执行!");
    			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
    			this.sendJSON("流程处理中未失败,请等待执行!");
            }
        } catch (Exception e) {
            logger.error("数据库操作失败"+e.getMessage());
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("数据库操作失败："+e.getMessage());
        }
    }

    /**------------------------------   统一任务调度管理 end     ---------------------------------  */

    
    /**
     * <p>
     * 方法名称: reRunQueueNode|描述: 根据队列找到分发节点
     * </p>
     */
    @RequestMapping("/reQueueNode")
    public void reQueueNode(String queueName) {
        try {
        	
        	String json = taskDispatchConfigService.queryQueueNodeJSON(queueName);
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString());
            logger.error(e.getMessage(),e);
        }
    }
    
    /**
     * 界面流程未收到bbsp通知的业务触发接口查询方法
     * @param id
     */
    @RequestMapping("reRunTaskExeToBusiness")
    public void reRunTaskExeToBusiness(String id){
    	try {
			autoTaskExeService.txAutoExeTaskStatus("2","5",null);
			this.sendJSON("任务执行成功！");
		} catch (Exception e) {
			logger.error("任务执行失败"+e.getMessage());
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("任务执行失败："+e.getMessage());
		}
    }
    
}
