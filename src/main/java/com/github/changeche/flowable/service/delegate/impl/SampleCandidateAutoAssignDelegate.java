package com.github.changeche.flowable.service.delegate.impl;

import com.github.changeche.flowable.biz.common.helper.DepartmentHelper;
import com.github.changeche.flowable.common.component.SpringUtil;
import com.github.changeche.flowable.model.consts.ExecutionKeyConst;
import com.github.changeche.flowable.service.delegate.AssignDelegate;
import com.github.changeche.flowable.service.delegate.CandidateAutoAssignDelegate;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在线课程 审批人自动分配
 * 只支持固定值
 * @author Chenjing
 */
@Slf4j
public class SampleCandidateAutoAssignDelegate extends CandidateAutoAssignDelegate {

    @Override
    public List<String> listUserMajor(String userId, String department) {
        return DepartmentHelper.listUserMajor(userId, department);
    }

    @Override
    public List<String> listFixedDepartmentMajor(String department) {
        return DepartmentHelper.listFixedDepartmentMajor(department);
    }

    @Override
    public String getFixedUpLevelDepartment(String userId, String department, int level) {
        return DepartmentHelper.getFixedUpLevelDepartment(userId, department, level);
    }

    @Override
    public List<String> getDefault(AssignDelegate.UserDepartment userDepartment) {
        // TODO
        return null;
    }

    /**
     * 根据实际业务规则
     * @param task
     * @param userDepartment
     */
    @Override
    protected void assignIfNoCandidate(Task task, AssignDelegate.UserDepartment userDepartment) {
        String taskId = task.getId();
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        Map<String, Object> processVariables = new HashMap<>(2);
        processVariables.put(ExecutionKeyConst.APPROVE, true);
        Map<String, Object> taskVariables = new HashMap<>(2);
        taskVariables.put("备注", "未找到合适的审批人，直接通过");
        taskService.setOwner(taskId, "-1");
        taskVariables.put(ExecutionKeyConst.APPROVE, true);
        taskService.setVariablesLocal(taskId, taskVariables);
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        runtimeService.setVariables(task.getExecutionId(), processVariables);
    }

    @Override
    protected String getDefaultDepartment(String userId) {
        return DepartmentHelper.getUserDepartment(userId);
    }
}
