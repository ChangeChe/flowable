package com.github.changeche.flowable.biz.common.helper;

import java.util.List;

/**
 * 部门模块帮助类
 * @author Chenjing
 */
public class DepartmentHelper {
    /**
     * 获取部门主管 - 上级部门的所有人
     * @param userId
     * @param department
     * @return
     */
    public static List<String> listUserMajor(String userId, String department) {
        // TODO
        return null;
    }

    /**
     * 获取指定部门的主管
     * @param department
     * @return
     */
    public static List<String> listFixedDepartmentMajor(String department) {
        // TODO
        return null;
    }

    /**
     * 获取指定等级的上级部门
     * @param userId
     * @param department
     * @param level
     * @return
     */
    public static String getFixedUpLevelDepartment(String userId, String department, int level) {
        // TODO
        return null;
    }

    // TODO
    public static List<String> getDepartmentCandidate() {
        // TODO
        return null;
    }

    public static String getUserDepartment(String userId) {
        // TODO
        return null;
    }
}
