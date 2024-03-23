package com.seyed.ali.task2securityauditing.service;

import com.seyed.ali.task2securityauditing.controller.LogController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogServiceDemo {

    public void logging() {
        for (int i = 0; i < 10_000; i++) {
            log.info("demo log: {}", i);
        }
    }

    public void changeLogLevel(String logLevel) {
        LoggingSystem system = LoggingSystem.get(LogController.class.getClassLoader());
        system.setLogLevel(LogController.class.getName(), LogLevel.valueOf(logLevel.toUpperCase()));
    }

}
