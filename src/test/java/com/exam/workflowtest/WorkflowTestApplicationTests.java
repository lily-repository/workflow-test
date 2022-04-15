package com.exam.workflowtest;

import com.exam.workflowtest.service.ActService;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@SpringBootTest
class WorkflowTestApplicationTests {

    @Autowired
    ActService actService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    @Test
    void contextLoads() {
    }

    /**
     * ACT_RE_DEPLOYMENT 流程部署表，每部署一次就会增加一条记录
     * ACT_RE_PROCDEF 流程定义表  和ACT_RE_DEPLOYMENT 是一对多的关系
     * ACT_GE_BYTEARRAY 流程资源表
     * ACT_GE_PROPERTY UPDATE
     */
    @Test
    public void deployProcessA() {
        //获取默认的流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //使用repositoryService进行流程的部署，定义一个流程的名字，把bmpn和png部署到数据库中
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程")
                .addClasspathResource("bmp/evection.bpmn20.xml")
                .addClasspathResource("bmp/evection.bpmn20.png")
                .deploy();
        //4.输出部署信息
        System.out.println("流程部署id=" + deploy.getId());
        System.out.println("流程部署名字=" + deploy.getName());
    }

    @Test
    public void deployProcessByZip() {
        //获取默认的流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //读取资源包文件，构造成inputstream
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bmp/evection.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        //使用压缩包的流进行压缩
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        //4.输出部署信息
        System.out.println("流程部署id=" + deploy.getId());
        System.out.println("流程部署名字=" + deploy.getName());
    }

    /**
     * 启动流程
     * ACT_HI_TASKINST 任务的历史信息
     * ACT_HI_PROCINST 流程实例的历史信息
     * ACT_HI_ACTINST 流程实例执行历史
     * ACT_HI_IDENTITYLINK 流程参与者的历史信息
     * <p>
     * ACT_RU_EXECUTION 流程执行的信息
     * ACT_RU_TASK  任务信息
     * ACT_RU_JOB
     * ACT_RU_TIMER_JOB
     */
    @Test
    public void TestStartProcessA() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myevection");
        System.out.println("流程定义ID：" + instance.getProcessDefinitionId());
        System.out.println("流程实例ID：" + instance.getProcessInstanceId());
        System.out.println("当前活动ID：" + instance.getActivityId());
    }

    /**
     * 启动流程
     */
    @Test
    public void TestStartProcessB() {
        // 1. 查询流程定义列表 -- 这里相当于展示所有可以发起的流程。需要定义一个表，用来存储某类用户角色能够发起某类流程。
        List<ProcessDefinition> processDefinitionList = actService.getAllProcessDefinition();
        processDefinitionList.forEach(System.out::println);
        ProcessDefinition processDefinition = processDefinitionList.get(0);
        // 选择某一个流程类，启动一个流程。
        String businessKey = "b1";
        Map<String, Object> map = new HashMap<>();
        // 如果流程的执行过程中，没有选择多个人可以完成某个流程的情况。可以指定流程变量。
        map.put("applicant", "zhangsan");
        actService.startProcess(processDefinition.getId(), businessKey, map);
    }

    /**
     * 通过用户id查找流程。
     */
    @Test
    public void findTasksByUserId() {
        String userId = "tom1";
        //List<Task> tasks = taskService.createTaskQuery().taskName("创建出差申请").list();
        //System.out.println(JSONObject.wrap(tasks));
        List<Task> resultTask = taskService.createTaskQuery().taskAssignee(userId).list();
        System.out.println("任务列表：" + resultTask);
        // 任务列表里面。创建一个查询。通过流程id，获取指定类型的流程。
        String processInstanceId = resultTask.get(0).getProcessInstanceId();
        System.out.println("流程实例Id：" + processInstanceId);
    }

    /**
     * 完成个人任务
     */
    @Test
    public void completTaskA() {
        // 流程定义的key
        String key = "myevection";
        // 完成任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey(key)
                //.taskCandidateOrAssigned("zhangsan")
                .taskAssignee("tom4")
                .list();
        Task task = list.get(0);
        String processInstanceId = null;
        if (task != null) {
            // 根据任务id来 完成任务。
            //taskService.addCandidateUser(task.getId(),"lisi");
            //taskService.addCandidateUser(task.getId(),"wangwu");
            processInstanceId = task.getProcessInstanceId();
            System.out.println("流程实例id1:" + processInstanceId);
            taskService.complete(task.getId());
        }
    }

    /**
     * 完成个人任务
     * ACT_HI_TASKINST
     * ACT_HI_ACTINST
     * ACT_HI_IDENTITYLINK
     * ACT_RU_TASK
     * ACT_RU_IDENTITYLINK
     * <p>
     * ACT_HI_TASKINST update
     * ACT_RU_EXECUTION
     * ACT_HI_ACTINST
     * <p>
     * ACT_RU_TASK delete
     */
    @Test
    public void completTaskB() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        taskService.complete("f8788f91-bc87-11ec-823b-ae9800477187");
    }

    /**
     * 候选人拾取任务
     */
    @Test
    public void claimTaskA() {

        //3. 当前任务的id
        //String taskId = "5002";
        //4. 候选人
        String user = "tom3";
        //List<Task> list = taskService.createTaskQuery().taskCandidateUser(user).list();
        List<Task> list = taskService.createTaskQuery().taskAssignee(user).list();
        //Task task = list.get(0);

        System.out.println(list);
        //if(task != null){
        //    //5. 拾取任务
        //    taskService.claim(task.getId(),user);
        //    System.out.println("taskId:"+task.getId()+" 用户："+user+" 拾取任务完成");
        //
        //}
    }

    /**
     * 查询个人待执行的任务
     * select distinct RES.* from ACT_RU_TASK RES inner join ACT_RE_PROCDEF D on RES.PROC_DEF_ID_ = D.ID_ WHERE RES.ASSIGNEE_ = '张三' and D.KEY_ = 'myevection' order by RES.ID_ asc LIMIT 2147483647 OFFSET 0
     */
    @Test
    public void claimTaskB() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("myevection")
                .taskAssignee("张三").list();
        System.out.println(taskList);
        for (Task task : taskList) {
            System.out.println("流程实例id=" + task.getProcessInstanceId());
            System.out.println("任务id=" + task.getId());
            System.out.println("任务负责人=" + task.getAssignee());
            System.out.println("任务名称=" + task.getName());
        }
    }
}
