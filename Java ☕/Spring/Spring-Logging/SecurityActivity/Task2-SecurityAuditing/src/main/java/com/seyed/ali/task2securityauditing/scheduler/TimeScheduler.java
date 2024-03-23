package com.seyed.ali.task2securityauditing.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimeScheduler {

    @Scheduled(fixedRate = 60_000)
    public void logInMinute() {
        log.info("");
    }

}
