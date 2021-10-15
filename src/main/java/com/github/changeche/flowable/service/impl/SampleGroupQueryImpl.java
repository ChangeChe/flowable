package com.github.changeche.flowable.service.impl;

import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.idm.api.Group;
import org.flowable.idm.engine.impl.GroupQueryImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chenjing
 */
public class SampleGroupQueryImpl extends GroupQueryImpl {
    @Override
    public List<Group> executeList(CommandContext commandContext) {
        return new ArrayList<>();
    }
}
