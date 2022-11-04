
1、日终任务 通过auto_task表进行配置；
2、AutoTaskInitializerServlet  通过web.xml 将所有任务在启动时 注册到spring任务调度器中；
3、AbstractAutoTask 是所有日终任务的父类；日终任务需要继承此类 并重写run()方法；
  run方法可以根据以前的日终类 修改方法名称；
4、AutoTaskCurrentMonitor 日终任务监控器，会实时的刷新 任务状态、根据日志状态刷新任务状态
  调度任务在执行任务时，会通过监控器 中任务的状态进行各种判断逻辑处理；
5、AutoTaskListener 是spring调度任务时需要的一个 监听器，会有任务执行前、中、完成 等的回调方法，
   我们用来记录日志；
6、ScheduleHelper是调度任务辅助类，定义了 spring job 需要的各种参数；
7、taskService 包
      将所有的日终任务 都放到这个包下面 ，方便后期维护 
======
如果要手工执行任务， autoTaskervice  里面的 runTask  执行任务

====
主要  任务  通过 脚本 插入，不提供页面维护；
加入任务后需要重启系统；
--insert into AUTO_TASK (ID, NAME, CODE, CLASS_NAME, STATUS, CRON_EXPR, DEPEND_TASKS, WAITING_TIME, WORKDAY, EXEC_STATUS, EXEC_MODULE, ORDER_NUM) 
--values ('1', '手工任务', '001', 'com.mingtech.application.autotask.test.TestTaskManual', '1', '0 30 12 * * ?', '', null, to_date('2016-10-16','yyyy-MM-dd'), '4', '1', 1);

	
	       
