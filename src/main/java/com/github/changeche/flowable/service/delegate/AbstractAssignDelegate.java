package com.github.changeche.flowable.service.delegate;

import com.github.changeche.flowable.common.component.SpringUtil;
import com.github.changeche.flowable.model.consts.AssignTypeConst;
import com.github.changeche.flowable.model.consts.ExecutionKeyConst;
import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public abstract class AbstractAssignDelegate implements AssignDelegate, TaskListener {

    /**
     * 自动分配类型
     */
    FixedValue assignType;
    /**
     * 自动分配依据
     */
    FixedValue assignValue;

    @Override
    public void notify(DelegateTask delegateTask) {
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        List<Task> list = taskService.createTaskQuery().taskId(delegateTask.getId()).list();
        if (list.size() < 1) {
            return;
        }
        Task task = list.get(0);
        autoAssign(task.getProcessInstanceId(), task);
    }

    /**
     * 自动分配
     * @param processInstanceId
     * @param taskId
     */
    protected void autoAssign(String processInstanceId, Task task) {
        String taskId = task.getId();
        String typeValue = (String) assignType.getValue(null);
        RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceId(processInstanceId).list();
        ProcessInstance processInstance = list.get(0);
        UserDepartment userDepartment = getInitiatorDepartment(processInstance);
        String userId = null, department = null, role = null;
        List<String> userGroupList = null;
        switch (typeValue) {
            case AssignTypeConst.FIXED_USER:
                userId = getFixedUser(assignValue);
                break;
            case AssignTypeConst.USER_MAJOR:
                userGroupList = listUserMajor(userDepartment.userId, userDepartment.getDepartment());
                break;
            case AssignTypeConst.FIXED_DEPARTMENT:
                department = getFixedDepartment(assignValue);
                break;
            case AssignTypeConst.FIXED_DEPARTMENT_MAJOR:
                userGroupList = listFixedDepartmentMajor(assignValue.getExpressionText());
                break;
            case AssignTypeConst.FIXED_LEVEL_DEPARTMENT:
                int level = Integer.parseInt((String)assignValue.getValue(null));
                department = getFixedUpLevelDepartment(userDepartment.getUserId(), userDepartment.getDepartment(), level);
                break;
            case AssignTypeConst.FIXED_ROLE:
                role = getFixedRole(assignValue);
                break;
            case AssignTypeConst.FIXED_DEPARTMENT_ROLE:
                userGroupList = getFixedDepartmentRole(assignValue);
                break;
            default:
                userGroupList = getDefault(userDepartment);
                break;
        }
        doAutoAssign(task, userDepartment, userId, department, role, userGroupList);
    }

    /**
     * 自动分配
     * @param taskId
     * @param userDepartment
     * @param userId
     * @param department
     * @param role
     * @param userGroupList
     */
    abstract void doAutoAssign(Task task, UserDepartment userDepartment, String userId, String department, String role, List<String> userGroupList);

    /**
     * 获取默认
     * @param userDepartment
     * @return
     */
    public abstract List<String> getDefault(AbstractAssignDelegate.UserDepartment userDepartment);

    /**
     * 获取流程发起人
     * @param processInstance
     * @return
     */
    private UserDepartment getInitiatorDepartment(ProcessInstance processInstance) {
        Map<String, Object> processVariables = processInstance.getProcessVariables();
        String userId, department;
        if (processVariables.containsKey(ExecutionKeyConst.INITIATOR)) {
            userId = processVariables.get(ExecutionKeyConst.INITIATOR).toString();
            department = processVariables.containsKey(ExecutionKeyConst.DEPARTMENT) && null != processVariables.get(ExecutionKeyConst.DEPARTMENT) ? processVariables.get(ExecutionKeyConst.DEPARTMENT).toString() : getDefaultDepartment(userId);
            if (!processVariables.containsKey(ExecutionKeyConst.DEPARTMENT) && StringUtils.hasLength(department)) {
                RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
                runtimeService.setVariable(processInstance.getId(), ExecutionKeyConst.DEPARTMENT, department);
            }
        } else {
            String name = processInstance.getName();
            RepositoryService repositoryService = SpringUtil.getBean(RepositoryService.class);
            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).list();
            ProcessDefinition processDefinition = list.get(0);
            String processDefinitionName = processDefinition.getName();
            String userDepartment = name.substring(processDefinitionName.length() + 1);
            String[] userDepartments = userDepartment.split("-");
            userId = userDepartments[0];
            department = userDepartments[1];
        }
        return new UserDepartment(userId, department);
    }

    /**
     * 获取用户的部门
     * @param userId
     * @return
     */
    abstract protected String getDefaultDepartment(String userId);
}
