package com.example.optimization_algorithm_backend.module.optimize.service;

import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.module.optimize.dto.CreateOptimizeTaskRequest;
import com.example.optimization_algorithm_backend.module.optimize.dto.OptimizeTaskQueryRequest;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeResultVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskSubmitVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskVO;

public interface OptimizeTaskAppService {

    OptimizeTaskSubmitVO submitTask(CreateOptimizeTaskRequest request);

    PageResult<OptimizeTaskVO> listTasks(OptimizeTaskQueryRequest request);

    OptimizeTaskVO getTask(Long taskId);

    OptimizeResultVO getTaskResult(Long taskId);

    OptimizeTaskSubmitVO retryTask(Long taskId);
}
