package com.example.optimization_algorithm_backend.module.optimize.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeTaskMapper;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskCacheService;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskStateService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OptimizeTaskStateServiceImpl implements OptimizeTaskStateService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，优化任务状态更新暂不可用";

    private final ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider;
    private final OptimizeTaskCacheService optimizeTaskCacheService;

    public OptimizeTaskStateServiceImpl(ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider,
                                        OptimizeTaskCacheService optimizeTaskCacheService) {
        this.optimizeTaskMapperProvider = optimizeTaskMapperProvider;
        this.optimizeTaskCacheService = optimizeTaskCacheService;
    }

    @Override
    public void markPending(Long taskId, Integer retryCount, LocalDateTime queueTime) {
        getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, taskId)
                .set(OptimizeTaskEntity::getRetryCount, retryCount)
                .set(OptimizeTaskEntity::getTaskStatus, "PENDING")
                .set(OptimizeTaskEntity::getQueueTime, queueTime)
                .set(OptimizeTaskEntity::getStartedAt, null)
                .set(OptimizeTaskEntity::getFinishedAt, null)
                .set(OptimizeTaskEntity::getErrorCode, null)
                .set(OptimizeTaskEntity::getErrorMessage, null)
                .set(OptimizeTaskEntity::getResultId, null));
        optimizeTaskCacheService.cacheTaskStatus(taskId, "PENDING");
    }

    @Override
    public void markRunning(Long taskId) {
        getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, taskId)
                .set(OptimizeTaskEntity::getTaskStatus, "RUNNING")
                .set(OptimizeTaskEntity::getStartedAt, LocalDateTime.now())
                .set(OptimizeTaskEntity::getFinishedAt, null)
                .set(OptimizeTaskEntity::getErrorCode, null)
                .set(OptimizeTaskEntity::getErrorMessage, null));
        optimizeTaskCacheService.cacheTaskStatus(taskId, "RUNNING");
    }

    @Override
    public void markSuccess(Long taskId, Long resultId) {
        getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, taskId)
                .set(OptimizeTaskEntity::getTaskStatus, "SUCCESS")
                .set(OptimizeTaskEntity::getFinishedAt, LocalDateTime.now())
                .set(OptimizeTaskEntity::getResultId, resultId)
                .set(OptimizeTaskEntity::getErrorCode, null)
                .set(OptimizeTaskEntity::getErrorMessage, null));
        optimizeTaskCacheService.cacheTaskStatus(taskId, "SUCCESS");
    }

    @Override
    public void markFailed(Long taskId, String errorCode, String errorMessage) {
        getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, taskId)
                .set(OptimizeTaskEntity::getTaskStatus, "FAILED")
                .set(OptimizeTaskEntity::getFinishedAt, LocalDateTime.now())
                .set(OptimizeTaskEntity::getErrorCode, errorCode)
                .set(OptimizeTaskEntity::getErrorMessage, errorMessage));
        optimizeTaskCacheService.cacheTaskStatus(taskId, "FAILED");
    }

    private OptimizeTaskMapper getOptimizeTaskMapper() {
        OptimizeTaskMapper mapper = optimizeTaskMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }
}
