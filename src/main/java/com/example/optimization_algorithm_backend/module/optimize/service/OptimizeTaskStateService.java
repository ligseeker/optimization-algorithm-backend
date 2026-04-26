package com.example.optimization_algorithm_backend.module.optimize.service;

import java.time.LocalDateTime;

public interface OptimizeTaskStateService {

    void markPending(Long taskId, Integer retryCount, LocalDateTime queueTime);

    void markRunning(Long taskId);

    void markSuccess(Long taskId, Long resultId);

    void markFailed(Long taskId, String errorCode, String errorMessage);
}
