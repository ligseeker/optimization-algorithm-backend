package com.example.optimization_algorithm_backend.module.optimize.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeResultEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeResultMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeTaskMapper;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.optimize.constant.OptimizeTaskStatus;
import com.example.optimization_algorithm_backend.module.optimize.converter.OptimizeTaskConverter;
import com.example.optimization_algorithm_backend.module.optimize.dto.CreateOptimizeTaskRequest;
import com.example.optimization_algorithm_backend.module.optimize.dto.OptimizeTaskQueryRequest;
import com.example.optimization_algorithm_backend.module.optimize.executor.AlgorithmExecutor;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskAppService;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeResultVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskSubmitVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OptimizeTaskAppServiceImpl implements OptimizeTaskAppService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，优化任务接口暂不可用";

    private final ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider;
    private final ObjectProvider<OptimizeResultMapper> optimizeResultMapperProvider;
    private final ResourceAccessService resourceAccessService;
    private final CurrentUserService currentUserService;
    private final AlgorithmExecutor algorithmExecutor;
    private final ObjectMapper objectMapper;

    public OptimizeTaskAppServiceImpl(ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider,
                                      ObjectProvider<OptimizeResultMapper> optimizeResultMapperProvider,
                                      ResourceAccessService resourceAccessService,
                                      CurrentUserService currentUserService,
                                      AlgorithmExecutor algorithmExecutor,
                                      ObjectMapper objectMapper) {
        this.optimizeTaskMapperProvider = optimizeTaskMapperProvider;
        this.optimizeResultMapperProvider = optimizeResultMapperProvider;
        this.resourceAccessService = resourceAccessService;
        this.currentUserService = currentUserService;
        this.algorithmExecutor = algorithmExecutor;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OptimizeTaskSubmitVO submitTask(CreateOptimizeTaskRequest request) {
        FlowGraphEntity graph = resourceAccessService.getAccessibleGraph(request.getGraphId());
        Long currentUserId = currentUserService.getCurrentUserId();

        OptimizeTaskEntity task = new OptimizeTaskEntity();
        task.setTaskNo(buildTaskNo());
        task.setWorkspaceId(graph.getWorkspaceId());
        task.setGraphId(graph.getId());
        task.setUserId(currentUserId);
        task.setAlgorithmType(request.getAlgorithmType());
        task.setAlgorithmMode(request.getAlgorithmMode());
        task.setTimeWeight(request.getTimeWeight());
        task.setPrecisionWeight(request.getPrecisionWeight());
        task.setCostWeight(request.getCostWeight());
        task.setTaskStatus(OptimizeTaskStatus.PENDING);
        task.setRetryCount(0);
        task.setMaxRetryCount(3);
        task.setQueueTime(LocalDateTime.now());
        getOptimizeTaskMapper().insert(task);

        submitAfterCommit(task.getId());
        return OptimizeTaskConverter.toSubmitVO(task);
    }

    @Override
    public PageResult<OptimizeTaskVO> listTasks(OptimizeTaskQueryRequest request) {
        if (request.getWorkspaceId() != null) {
            resourceAccessService.getAccessibleWorkspace(request.getWorkspaceId());
        }
        if (request.getGraphId() != null) {
            resourceAccessService.getAccessibleGraph(request.getGraphId());
        }

        LambdaQueryWrapper<OptimizeTaskEntity> queryWrapper = new LambdaQueryWrapper<OptimizeTaskEntity>()
                .orderByDesc(OptimizeTaskEntity::getCreatedAt);
        if (request.getWorkspaceId() != null) {
            queryWrapper.eq(OptimizeTaskEntity::getWorkspaceId, request.getWorkspaceId());
        }
        if (request.getGraphId() != null) {
            queryWrapper.eq(OptimizeTaskEntity::getGraphId, request.getGraphId());
        }
        if (StringUtils.hasText(request.getTaskStatus())) {
            String taskStatus = request.getTaskStatus().trim().toUpperCase(Locale.ROOT);
            if (!"ALL".equals(taskStatus)) {
                queryWrapper.eq(OptimizeTaskEntity::getTaskStatus, taskStatus);
            }
        }
        if (!currentUserService.isAdmin()) {
            queryWrapper.eq(OptimizeTaskEntity::getUserId, currentUserService.getCurrentUserId());
        }

        Page<OptimizeTaskEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<OptimizeTaskEntity> resultPage = getOptimizeTaskMapper().selectPage(page, queryWrapper);
        List<OptimizeTaskVO> records = resultPage.getRecords()
                .stream()
                .map(OptimizeTaskConverter::toTaskVO)
                .collect(Collectors.toList());
        return PageResult.of(records, resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public OptimizeTaskVO getTask(Long taskId) {
        return OptimizeTaskConverter.toTaskVO(getAccessibleTask(taskId));
    }

    @Override
    public OptimizeResultVO getTaskResult(Long taskId) {
        OptimizeTaskEntity task = getAccessibleTask(taskId);
        if (!OptimizeTaskStatus.SUCCESS.equals(task.getTaskStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "任务尚未成功完成，暂不可查询结果");
        }

        OptimizeResultEntity result = getOptimizeResultMapper().selectOne(new LambdaQueryWrapper<OptimizeResultEntity>()
                .eq(OptimizeResultEntity::getTaskId, task.getId()));
        if (result == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "优化结果不存在");
        }
        return OptimizeTaskConverter.toResultVO(result, objectMapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OptimizeTaskSubmitVO retryTask(Long taskId) {
        OptimizeTaskEntity task = getAccessibleTask(taskId);
        if (!OptimizeTaskStatus.FAILED.equals(task.getTaskStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "仅FAILED状态任务支持重试");
        }
        int retryCount = task.getRetryCount() == null ? 0 : task.getRetryCount();
        int maxRetryCount = task.getMaxRetryCount() == null ? 3 : task.getMaxRetryCount();
        if (retryCount >= maxRetryCount) {
            throw new BusinessException(ErrorCode.CONFLICT, "任务重试次数已达上限");
        }

        task.setRetryCount(retryCount + 1);
        getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, task.getId())
                .set(OptimizeTaskEntity::getRetryCount, task.getRetryCount())
                .set(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.PENDING)
                .set(OptimizeTaskEntity::getQueueTime, LocalDateTime.now())
                .set(OptimizeTaskEntity::getStartedAt, null)
                .set(OptimizeTaskEntity::getFinishedAt, null)
                .set(OptimizeTaskEntity::getErrorCode, null)
                .set(OptimizeTaskEntity::getErrorMessage, null)
                .set(OptimizeTaskEntity::getResultId, null));
        task.setTaskStatus(OptimizeTaskStatus.PENDING);

        submitAfterCommit(task.getId());
        return OptimizeTaskConverter.toSubmitVO(task);
    }

    private void submitAfterCommit(Long taskId) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    algorithmExecutor.submit(taskId);
                }
            });
            return;
        }
        algorithmExecutor.submit(taskId);
    }

    private OptimizeTaskEntity getAccessibleTask(Long taskId) {
        OptimizeTaskEntity task = getOptimizeTaskMapper().selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "优化任务不存在");
        }
        resourceAccessService.getAccessibleGraph(task.getGraphId());
        return task;
    }

    private String buildTaskNo() {
        String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
        return "TASK" + ts + random;
    }

    private OptimizeTaskMapper getOptimizeTaskMapper() {
        OptimizeTaskMapper mapper = optimizeTaskMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }

    private OptimizeResultMapper getOptimizeResultMapper() {
        OptimizeResultMapper mapper = optimizeResultMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }
}
