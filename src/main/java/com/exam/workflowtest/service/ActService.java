package com.exam.workflowtest.service;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * *
 *
 * @author: RenLiLi
 * @date: 2022/4/14 10:29
 */
@Service
public class ActService {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    /**
     * 1. 部署流程服务
     *
     * @return
     */
    public Deployment deployProcessByZip(InputStream inputStream) {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        return repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    /**
     * 2. 查询流程定义（查询所有的已部署流程）-- 需要查询最新的部署流程
     * TODO  待测试
     *
     * @return
     */
    public List<ProcessDefinition> getAllProcessDefinition() {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        return query.orderByProcessDefinitionVersion().desc().list();
    }

    /**
     * 3. 查询所有流程实例
     *
     * @return
     */
    public List<ProcessInstance> getAllProcessInstance() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        return list;
    }

    /**
     * 4. 查询所有流程任务
     */
    public List<Task> getAllProcessTask(String username) {
        List<Task> list = taskService.createTaskQuery().list();

        List<Task> list2 = list.stream().filter(item -> {
            return item.getAssignee().equals(username);
        }).collect(Collectors.toList());
        return list2;
    }

    /**
     * 完成指定任务--知道任务id
     * --完成任务的时候，需要根据用户角色，
     */
    public void completeTask(Task task, Map<String, Object> map) {
        taskService.complete(task.getId(), map);
    }

    /**
     * 启动流程实例
     */
    public ProcessInstance startProcess(String processDefinitionKey, String businessKey, Map<String, Object> map) {
        return runtimeService.startProcessInstanceById(processDefinitionKey, businessKey, map);
    }
}
