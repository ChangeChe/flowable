package com.github.changeche.flowable.controller;

import com.github.changeche.flowable.service.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chenjing
 */
@RestController
public class ProcessController {
    @Autowired
    IProcessService processService;

    public Object start() {
        //processService.start();
        return null;
    }
}
