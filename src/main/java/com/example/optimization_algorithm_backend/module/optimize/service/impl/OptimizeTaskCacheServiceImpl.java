package com.example.optimization_algorithm_backend.module.optimize.service.impl;

import com.example.optimization_algorithm_backend.common.cache.RedisSafeClient;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskCacheService;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeResultVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OptimizeTaskCacheServiceImpl implements OptimizeTaskCacheService {

    private static final String TASK_STATUS_KEY_PREFIX = "optimize:task:status:";
    private static final String RESULT_KEY_PREFIX = "optimize:result:";

    private final RedisSafeClient redisSafeClient;

    @Value("${app.cache.optimize.task-status-ttl-minutes:1440}")
    private Long taskStatusTtlMinutes;

    @Value("${app.cache.optimize.result-ttl-minutes:1440}")
    private Long optimizeResultTtlMinutes;

    public OptimizeTaskCacheServiceImpl(RedisSafeClient redisSafeClient) {
        this.redisSafeClient = redisSafeClient;
    }

    @Override
    public void cacheTaskStatus(Long taskId, String status) {
        redisSafeClient.set(taskStatusKey(taskId), status, taskStatusTtlMinutes, TimeUnit.MINUTES);
    }

    @Override
    public String getTaskStatus(Long taskId) {
        Object value = redisSafeClient.get(taskStatusKey(taskId));
        return value == null ? null : String.valueOf(value);
    }

    @Override
    public void evictTaskStatus(Long taskId) {
        redisSafeClient.delete(taskStatusKey(taskId));
    }

    @Override
    public void cacheOptimizeResult(Long taskId, OptimizeResultVO resultVO) {
        redisSafeClient.set(resultKey(taskId), resultVO, optimizeResultTtlMinutes, TimeUnit.MINUTES);
    }

    @Override
    public OptimizeResultVO getOptimizeResult(Long taskId) {
        Object value = redisSafeClient.get(resultKey(taskId));
        if (value instanceof OptimizeResultVO) {
            return (OptimizeResultVO) value;
        }
        return null;
    }

    @Override
    public void evictOptimizeResult(Long taskId) {
        redisSafeClient.delete(resultKey(taskId));
    }

    private String taskStatusKey(Long taskId) {
        return TASK_STATUS_KEY_PREFIX + taskId;
    }

    private String resultKey(Long taskId) {
        return RESULT_KEY_PREFIX + taskId;
    }
}
