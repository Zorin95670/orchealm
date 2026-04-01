package com.orchealm.orchealmapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean("teamTaskExecutor")
    public TaskExecutor teamTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);        // nombre max de threads
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("TeamCreator-");
        executor.initialize();
        return executor;
    }
}