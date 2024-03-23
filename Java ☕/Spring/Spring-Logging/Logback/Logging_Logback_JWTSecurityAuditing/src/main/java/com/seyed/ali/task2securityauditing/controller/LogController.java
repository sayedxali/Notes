package com.seyed.ali.task2securityauditing.controller;

import com.seyed.ali.task2securityauditing.service.LogServiceDemo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/log/public")
public class LogController {

    private final LogServiceDemo logServiceDemo;

    @GetMapping
    public void log() {
        this.logServiceDemo.logging();
    }

    @GetMapping("/act")
    public String log2() {
        log.trace("This is a TRACE level message");
        log.debug("This is a DEBUG level message");
        log.info("This is an INFO level message");
        log.warn("This is a WARN level message");
        log.error("This is an ERROR level message");
        return "See the log for details";
    }

    @GetMapping("/change-log-level-to/{logLevel}")
    public String changeLogLevel(@PathVariable String logLevel) {
        this.logServiceDemo.changeLogLevel(logLevel);
        return "changed log level to error";
    }

}
