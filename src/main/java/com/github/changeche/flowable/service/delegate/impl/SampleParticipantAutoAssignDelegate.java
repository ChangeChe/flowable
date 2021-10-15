package com.github.changeche.flowable.service.delegate.impl;


import com.github.changeche.flowable.biz.common.helper.DepartmentHelper;
import com.github.changeche.flowable.service.delegate.AssignDelegate;
import com.github.changeche.flowable.service.delegate.ParticipantAutoAssignDelegate;

import java.util.List;

/**
 * 抄送人自动分配
 * @author Chenjing
 */
public class SampleParticipantAutoAssignDelegate extends ParticipantAutoAssignDelegate {
    @Override
    public List<String> getDefault(AssignDelegate.UserDepartment userDepartment) {
        return null;
    }

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
    protected String getDefaultDepartment(String userId) {
        return DepartmentHelper.getUserDepartment(userId);
    }
}
