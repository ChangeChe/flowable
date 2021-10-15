package com.github.changeche.flowable.service.delegate;

import com.github.changeche.flowable.common.component.SpringUtil;
import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;

import java.util.List;

/**
 * 抄送人分配
 * @author Chenjing
 */
public abstract class ParticipantAutoAssignDelegate extends AbstractAssignDelegate{

    @Override
    protected void doAutoAssign(Task task, UserDepartment userDepartment, String userId, String department, String role, List<String> userGroupList) {
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        String taskId = task.getId();
        if (null != userId) {
            taskService.addUserIdentityLink(taskId, userId, IdentityLinkType.PARTICIPANT);
        } else if (null != department) {
            taskService.addGroupIdentityLink(taskId, department, IdentityLinkType.PARTICIPANT);
        } else if (null != role) {
            taskService.addGroupIdentityLink(taskId, role, IdentityLinkType.PARTICIPANT);
        } else if (null != userGroupList && userGroupList.size() > 0) {
            String candidateType = userGroupList.get(0);
            switch (candidateType) {
                case CandidateType.USER:
                    for (int i = 1; i < userGroupList.size(); i++) {
                        taskService.addUserIdentityLink(taskId, userGroupList.get(i), IdentityLinkType.PARTICIPANT);
                    }
                    break;
                case CandidateType.DEPARTMENT:
                    for (int i = 1; i < userGroupList.size(); i++) {
                        taskService.addGroupIdentityLink(taskId, userGroupList.get(i), IdentityLinkType.PARTICIPANT);
                    }
                    break;
                case CandidateType.DEPARTMENT_ROLE:
                    String _department = userGroupList.get(1);
                    for (int i = 2; i < userGroupList.size(); i++) {
                        taskService.addGroupIdentityLink(taskId, _department + ":" + userGroupList.get(i), IdentityLinkType.PARTICIPANT);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("候选类型不合法");
            }
        }
        // 如果没找到合适的就放弃
    }
}
