package com.seyed.ali.task2securityauditing.config;

import com.seyed.ali.task2securityauditing.service.LogServiceDemo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfiguration {

    @Bean
    public LogServiceDemo logServiceDemo() {
        LogServiceDemo logServiceDemo = new LogServiceDemo();
//        logServiceDemo.logging();
        return logServiceDemo;
    }

}
