package com.example.optimization_algorithm_backend.module.optimize.service;

import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeResultVO;

public interface OptimizeTaskCacheService {

    void cacheTaskStatus(Long taskId, String status);

    String getTaskStatus(Long taskId);

    void evictTaskStatus(Long taskId);

    void cacheOptimizeResult(Long taskId, OptimizeResultVO resultVO);

    OptimizeResultVO getOptimizeResult(Long taskId);

    void evictOptimizeResult(Long taskId);
}
