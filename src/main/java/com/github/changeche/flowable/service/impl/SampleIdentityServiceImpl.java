package com.github.changeche.flowable.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.idm.api.*;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.IdmIdentityServiceImpl;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author Chenjing
 */
//@Service
@Slf4j
public class SampleIdentityServiceImpl extends IdmIdentityServiceImpl {

    ApplicationContext applicationContext;

    public SampleIdentityServiceImpl(ApplicationContext applicationContext, IdmEngineConfiguration idmEngineConfiguration) {
        super(idmEngineConfiguration);
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean checkPassword(String userId, String password) {
        return executeCheckPassword(userId, password);
    }

    protected boolean executeCheckPassword(final String userId, final String password) {
        // Extra password check, see http://forums.activiti.org/comment/22312
        if (password == null || password.length() == 0) {
            throw new FlowableException("Null or empty passwords are not allowed!");
        }

        try {
            // 根据实际的业务逻辑进行登录操作 TODO
            return true;
        } catch (FlowableException e) {
            log.error("Could not authenticate user : {}", userId, e);
            return false;
        }
    }

    @Override
    public User newUser(String userId) {
        throw new FlowableException("XMW identity service doesn't support creating a new user");
    }

    @Override
    public void saveUser(User user) {
        throw new FlowableException("XMW identity service doesn't support saving an user");
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        throw new FlowableException("XMW identity service doesn't support native querying");
    }

    @Override
    public void deleteUser(String userId) {
        throw new FlowableException("XMW identity service doesn't support deleting an user");
    }

    @Override
    public Group newGroup(String groupId) {
        throw new FlowableException("XMW identity service doesn't support creating a new group");
    }

    @Override
    public NativeGroupQuery createNativeGroupQuery() {
        throw new FlowableException("XMW identity service doesn't support native querying");
    }

    @Override
    public void saveGroup(Group group) {
        throw new FlowableException("XMW identity service doesn't support saving a group");
    }

    @Override
    public void deleteGroup(String groupId) {
        throw new FlowableException("XMW identity service doesn't support deleting a group");
    }

    @Override
    public UserQuery createUserQuery() {
        return new SampleUserQueryImpl();
    }

    @Override
    public GroupQuery createGroupQuery() {
        return new SampleGroupQueryImpl();
    }

    @Override
    public List<Group> getGroupsWithPrivilege(String name) {
        throw new FlowableException("XMW identity service doesn't support getting groups with privilege");
    }

    @Override
    public List<User> getUsersWithPrivilege(String name) {
        throw new FlowableException("XMW identity service doesn't support getting users with privilege");
    }
}
