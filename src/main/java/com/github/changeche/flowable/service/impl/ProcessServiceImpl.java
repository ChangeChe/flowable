package com.github.changeche.flowable.service.impl;

import com.github.changeche.flowable.service.IProcessService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chenjing
 */
@Service
public class ProcessServiceImpl implements IProcessService {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Override
    public void start(String modelKey, String initiator, String department, Map<String, Object> variables) {
        // Sample TODO
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(modelKey).latestVersion().list();
        if (list.size() < 1) {
            throw new RuntimeException("未找到流程定义");
        }
        ProcessDefinition processDefinition = list.get(0);
        // 将发起人以及部门存放到流程名称里
        String processInstanceName = String.format("%s-%s-%s", processDefinition.getName(), initiator, department);
        String outcome = String.format("%s-%s-%s", processDefinition.getId(), initiator, department);
        ProcessInstance processInstance = runtimeService.startProcessInstanceWithForm(processDefinition.getId(), outcome, variables, processInstanceName);

    }
}
