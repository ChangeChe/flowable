package com.github.changeche.flowable.service.delegate;

import org.flowable.common.engine.impl.el.FixedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 除fixed-user会保存userId外，其他方式都是保存的部门-角色信息，便于数据扩展
 * @author Chenjing
 */
public interface AssignDelegate {

    /**
     * 角色存储前缀
     * 部门与角色都存在groupId属性中，通过前缀区分
     */
    static final String ROLE_PREFIX = "$$";

    /**
     * 指定用户
     * @param fixedUser
     * @return
     */
    default String getFixedUser(FixedValue fixedUser) {return fixedUser.getExpressionText();}

    /**
     * 指定部门
     * @param fixedDepartment
     * @return
     */
    default String getFixedDepartment(FixedValue fixedDepartment) {return fixedDepartment.getExpressionText();}

    /**
     * 指定角色
     * @param fixedRole
     * @return
     */
    default String getFixedRole(FixedValue fixedRole) {return ROLE_PREFIX + fixedRole.getExpressionText();}

    /**
     * 获取部门主管
     * - 有可能是上级部门的所有人，有可能是当前部门的某个指定角色，具体依据应用的实际情况来
     * - 不直接存人信息，存部门及角色信息
     * @param userId
     * @param department
     * @return
     */
    List<String> listUserMajor(String userId, String department);

    /**
     * 获取指定部门的主管
     * @param department
     * @return
     */
    List<String> listFixedDepartmentMajor(String department);

    /**
     * 获取指定等级的上级部门
     * @param userId
     * @param department
     * @param level
     * @return
     */
    String getFixedUpLevelDepartment(String userId, String department, int level);

    /**
     * 获取指定部门指定角色
     * @param fixedDepartmentRole
     * @return
     */
    default List<String> getFixedDepartmentRole(FixedValue fixedDepartmentRole) {
        String expressText = fixedDepartmentRole.getExpressionText();
        String[] split = expressText.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("fixed-department-role分配方式值的格式为'${department}:${role}'");
        }
        List<String> ret = new ArrayList<>();
        ret.add(CandidateType.DEPARTMENT_ROLE);
        ret.add(split[0]);
        ret.add(ROLE_PREFIX + split[1]);
        return ret;
    }

    class CandidateType {
        public static final String USER = "user";
        public static final String DEPARTMENT = "department";
        public static final String DEPARTMENT_ROLE = "department-role";
    }

    static class UserDepartment {
        public UserDepartment(String userId, String department) {
            this.userId = userId;
            this.department = department;
        }

        String userId;
        String department;

        public String getUserId() {
            return userId;
        }

        public String getDepartment() {
            return department;
        }
    }
}
