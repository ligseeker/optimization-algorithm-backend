package com.example.optimization_algorithm_backend.module.optimize.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeTaskMapper;
import com.example.optimization_algorithm_backend.module.optimize.constant.OptimizeTaskStatus;
import com.example.optimization_algorithm_backend.module.optimize.executor.AlgorithmExecutor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OptimizeTaskCompensationService {

    private final ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider;
    private final AlgorithmExecutor algorithmExecutor;

    @Value("${app.optimize.compensation.enabled:true}")
    private boolean compensationEnabled;

    @Value("${app.optimize.pending-timeout-minutes:30}")
    private long pendingTimeoutMinutes;

    @Value("${app.optimize.recovery.batch-size:200}")
    private long recoveryBatchSize;

    public OptimizeTaskCompensationService(ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider,
                                           AlgorithmExecutor algorithmExecutor) {
        this.optimizeTaskMapperProvider = optimizeTaskMapperProvider;
        this.algorithmExecutor = algorithmExecutor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void recoverPendingTasksOnStartup() {
        if (!compensationEnabled) {
            return;
        }
        OptimizeTaskMapper mapper = optimizeTaskMapperProvider.getIfAvailable();
        if (mapper == null) {
            return;
        }
        List<OptimizeTaskEntity> pendingTasks = mapper.selectPage(
                new Page<>(1, Math.max(recoveryBatchSize, 1)),
                new LambdaQueryWrapper<OptimizeTaskEntity>()
                        .eq(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.PENDING)
                        .orderByAsc(OptimizeTaskEntity::getQueueTime)
                        .orderByAsc(OptimizeTaskEntity::getId)
        ).getRecords();
        for (OptimizeTaskEntity task : pendingTasks) {
            if (isPendingTimeout(task)) {
                markPendingTimeoutFailed(mapper, task.getId(), "任务长时间处于PENDING，启动恢复时已判定超时");
            } else {
                algorithmExecutor.submit(task.getId());
            }
        }
    }

    @Scheduled(fixedDelayString = "${app.optimize.compensation.scan-interval-ms:60000}")
    public void markTimedOutPendingTasks() {
        if (!compensationEnabled) {
            return;
        }
        OptimizeTaskMapper mapper = optimizeTaskMapperProvider.getIfAvailable();
        if (mapper == null) {
            return;
        }
        List<OptimizeTaskEntity> pendingTasks = mapper.selectPage(
                new Page<>(1, Math.max(recoveryBatchSize, 1)),
                new LambdaQueryWrapper<OptimizeTaskEntity>()
                        .eq(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.PENDING)
                        .orderByAsc(OptimizeTaskEntity::getQueueTime)
                        .orderByAsc(OptimizeTaskEntity::getId)
        ).getRecords();
        for (OptimizeTaskEntity task : pendingTasks) {
            if (isPendingTimeout(task)) {
                markPendingTimeoutFailed(mapper, task.getId(), "任务排队超时，系统自动标记失败，请重试");
            }
        }
    }

    private boolean isPendingTimeout(OptimizeTaskEntity task) {
        LocalDateTime queueTime = task.getQueueTime();
        if (queueTime == null) {
            return false;
        }
        return queueTime.plusMinutes(Math.max(pendingTimeoutMinutes, 1)).isBefore(LocalDateTime.now());
    }

    private void markPendingTimeoutFailed(OptimizeTaskMapper mapper, Long taskId, String message) {
        mapper.update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, taskId)
                .eq(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.PENDING)
                .set(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.FAILED)
                .set(OptimizeTaskEntity::getFinishedAt, LocalDateTime.now())
                .set(OptimizeTaskEntity::getErrorCode, "TASK_PENDING_TIMEOUT")
                .set(OptimizeTaskEntity::getErrorMessage, message));
    }
}
