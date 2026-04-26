package com.example.optimization_algorithm_backend.optimize;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeResultEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeResultMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeTaskMapper;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.optimize.constant.OptimizeTaskStatus;
import com.example.optimization_algorithm_backend.module.optimize.dto.CreateOptimizeTaskRequest;
import com.example.optimization_algorithm_backend.module.optimize.executor.AlgorithmExecutor;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskCacheService;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskStateService;
import com.example.optimization_algorithm_backend.module.optimize.service.impl.OptimizeTaskAppServiceImpl;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeResultVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskSubmitVO;
import com.example.optimization_algorithm_backend.module.optimize.vo.OptimizeTaskVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OptimizeTaskAppServiceCacheTest {

    @Mock
    private ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider;
    @Mock
    private ObjectProvider<OptimizeResultMapper> optimizeResultMapperProvider;
    @Mock
    private OptimizeTaskMapper optimizeTaskMapper;
    @Mock
    private OptimizeResultMapper optimizeResultMapper;
    @Mock
    private ResourceAccessService resourceAccessService;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private AlgorithmExecutor algorithmExecutor;
    @Mock
    private OptimizeTaskCacheService optimizeTaskCacheService;
    @Mock
    private OptimizeTaskStateService optimizeTaskStateService;

    private OptimizeTaskAppServiceImpl optimizeTaskAppService;

    @BeforeEach
    void setUp() {
        lenient().when(optimizeTaskMapperProvider.getIfAvailable()).thenReturn(optimizeTaskMapper);
        lenient().when(optimizeResultMapperProvider.getIfAvailable()).thenReturn(optimizeResultMapper);
        optimizeTaskAppService = new OptimizeTaskAppServiceImpl(
                optimizeTaskMapperProvider,
                optimizeResultMapperProvider,
                resourceAccessService,
                currentUserService,
                algorithmExecutor,
                new ObjectMapper(),
                optimizeTaskCacheService,
                optimizeTaskStateService
        );
    }

    @Test
    void shouldCachePendingWhenSubmitTask() {
        FlowGraphEntity graph = new FlowGraphEntity();
        graph.setId(8L);
        graph.setWorkspaceId(1L);
        when(resourceAccessService.getAccessibleGraph(8L)).thenReturn(graph);
        when(currentUserService.getCurrentUserId()).thenReturn(7L);
        when(optimizeTaskMapper.insert(any())).thenAnswer(invocation -> {
            OptimizeTaskEntity task = invocation.getArgument(0);
            task.setId(101L);
            return 1;
        });

        CreateOptimizeTaskRequest request = new CreateOptimizeTaskRequest();
        request.setGraphId(8L);
        request.setAlgorithmType(1);
        request.setAlgorithmMode(1);
        request.setTimeWeight(1);
        request.setPrecisionWeight(1);
        request.setCostWeight(1);
        OptimizeTaskSubmitVO submitVO = optimizeTaskAppService.submitTask(request);

        Assertions.assertEquals(101L, submitVO.getTaskId());
        verify(optimizeTaskCacheService).cacheTaskStatus(101L, OptimizeTaskStatus.PENDING);
    }

    @Test
    void shouldFallbackToMysqlAndBackfillResultCache() {
        OptimizeTaskEntity task = new OptimizeTaskEntity();
        task.setId(11L);
        task.setGraphId(8L);
        task.setTaskStatus(OptimizeTaskStatus.SUCCESS);
        when(optimizeTaskMapper.selectById(11L)).thenReturn(task);
        when(resourceAccessService.getAccessibleGraph(8L)).thenReturn(new FlowGraphEntity());
        when(optimizeTaskCacheService.getTaskStatus(11L)).thenReturn(null);
        when(optimizeTaskCacheService.getOptimizeResult(11L)).thenReturn(null);

        OptimizeResultEntity result = new OptimizeResultEntity();
        result.setId(20L);
        result.setTaskId(11L);
        result.setWorkspaceId(1L);
        result.setSourceGraphId(8L);
        result.setResultName("r1");
        result.setResultGraphJson("{\"k\":1}");
        result.setDiffJson("{\"d\":1}");
        result.setScoreRatio(new BigDecimal("0.123456"));
        when(optimizeResultMapper.selectOne(any())).thenReturn(result);

        OptimizeResultVO vo = optimizeTaskAppService.getTaskResult(11L);
        Assertions.assertEquals(20L, vo.getId());
        verify(optimizeTaskCacheService).cacheTaskStatus(11L, OptimizeTaskStatus.SUCCESS);
        verify(optimizeTaskCacheService).cacheOptimizeResult(11L, vo);
    }

    @Test
    void shouldReadTaskStatusFromRedisWhenHit() {
        OptimizeTaskEntity task = new OptimizeTaskEntity();
        task.setId(12L);
        task.setGraphId(8L);
        task.setTaskStatus(OptimizeTaskStatus.PENDING);
        when(optimizeTaskMapper.selectById(12L)).thenReturn(task);
        when(resourceAccessService.getAccessibleGraph(8L)).thenReturn(new FlowGraphEntity());
        when(optimizeTaskCacheService.getTaskStatus(12L)).thenReturn(OptimizeTaskStatus.RUNNING);

        OptimizeTaskVO vo = optimizeTaskAppService.getTask(12L);
        Assertions.assertEquals(OptimizeTaskStatus.RUNNING, vo.getTaskStatus());
    }

    @Test
    void shouldEvictOldCacheWhenRetrySameTaskId() {
        OptimizeTaskEntity task = new OptimizeTaskEntity();
        task.setId(13L);
        task.setGraphId(8L);
        task.setTaskStatus(OptimizeTaskStatus.FAILED);
        task.setRetryCount(0);
        task.setMaxRetryCount(3);
        when(optimizeTaskMapper.selectById(13L)).thenReturn(task);
        when(resourceAccessService.getAccessibleGraph(8L)).thenReturn(new FlowGraphEntity());

        OptimizeTaskSubmitVO vo = optimizeTaskAppService.retryTask(13L);
        Assertions.assertEquals(OptimizeTaskStatus.PENDING, vo.getTaskStatus());
        verify(optimizeTaskCacheService).evictOptimizeResult(13L);
        verify(optimizeTaskCacheService).evictTaskStatus(13L);
        verify(optimizeTaskStateService).markPending(eq(13L), eq(1), any(LocalDateTime.class));
    }
}
