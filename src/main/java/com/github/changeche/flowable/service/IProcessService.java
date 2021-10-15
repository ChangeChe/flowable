package com.github.changeche.flowable.service;

import java.util.Map;

/**
 * @author Chenjing
 */
public interface IProcessService {

    /**
     * 开始一个流程
     * @param modelKey
     * @param initiator
     * @param department
     * @param variables
     * @return
     */
    void start(String modelKey, String initiator, String department, Map<String, Object> variables);

}
