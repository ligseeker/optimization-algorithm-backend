package com.example.optimization_algorithm_backend.module.optimize.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.common.response.Result;
import com.example.optimization_algorithm_backend.module.log.annotation.OperationLog;
import com.example.optimization_algorithm_backend.module.optimize.dto.CreateOptimizeTaskRequest;
import com.example.optimization_algorithm_backend.module.optimize.dto.OptimizeTaskQueryRequest;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskAppService;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeResultVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskSubmitVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@SaCheckLogin
@RestController
@RequestMapping("/api/optimize/tasks")
@Tag(name = "OptimizeTask", description = "优化任务中心接口")
public class OptimizeTaskController {

    private final OptimizeTaskAppService optimizeTaskAppService;

    public OptimizeTaskController(OptimizeTaskAppService optimizeTaskAppService) {
        this.optimizeTaskAppService = optimizeTaskAppService;
    }

    @PostMapping
    @Operation(summary = "提交优化任务")
    @OperationLog(operationType = "SUBMIT_OPTIMIZE_TASK", objectType = "OPTIMIZE_TASK")
    public Result<OptimizeTaskSubmitVO> submitTask(@Valid @RequestBody CreateOptimizeTaskRequest request) {
        return Result.success("任务已提交", optimizeTaskAppService.submitTask(request));
    }

    @GetMapping
    @Operation(summary = "分页查询优化任务")
    public Result<PageResult<OptimizeTaskVO>> listTasks(@Valid @ModelAttribute OptimizeTaskQueryRequest request) {
        return Result.success(optimizeTaskAppService.listTasks(request));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "查询任务状态")
    public Result<OptimizeTaskVO> getTask(@PathVariable @Min(value = 1, message = "taskId必须大于0") Long taskId) {
        return Result.success(optimizeTaskAppService.getTask(taskId));
    }

    @GetMapping("/{taskId}/result")
    @Operation(summary = "查询优化结果")
    @OperationLog(operationType = "QUERY_OPTIMIZE_RESULT", objectType = "OPTIMIZE_RESULT", objectIdParam = "taskId")
    public Result<OptimizeResultVO> getTaskResult(@PathVariable @Min(value = 1, message = "taskId必须大于0") Long taskId) {
        return Result.success(optimizeTaskAppService.getTaskResult(taskId));
    }

    @PostMapping("/{taskId}/retry")
    @Operation(summary = "重试失败任务")
    public Result<OptimizeTaskSubmitVO> retryTask(@PathVariable @Min(value = 1, message = "taskId必须大于0") Long taskId) {
        return Result.success("重试任务已提交", optimizeTaskAppService.retryTask(taskId));
    }
}
