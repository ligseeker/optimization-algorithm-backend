package com.example.optimization_algorithm_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Value("${app.thread-pool.optimize.core-pool-size:4}")
    private int corePoolSize;

    @Value("${app.thread-pool.optimize.max-pool-size:8}")
    private int maxPoolSize;

    @Value("${app.thread-pool.optimize.queue-capacity:100}")
    private int queueCapacity;

    @Value("${app.thread-pool.optimize.keep-alive-seconds:60}")
    private int keepAliveSeconds;

    @Bean(name = "optimizeTaskExecutor")
    public ThreadPoolTaskExecutor optimizeTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("optimize-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
