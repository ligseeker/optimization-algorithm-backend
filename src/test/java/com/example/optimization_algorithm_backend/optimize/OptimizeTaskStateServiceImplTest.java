package com.example.optimization_algorithm_backend.optimize;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeTaskMapper;
import com.example.optimization_algorithm_backend.module.optimize.service.OptimizeTaskCacheService;
import com.example.optimization_algorithm_backend.module.optimize.service.impl.OptimizeTaskStateServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OptimizeTaskStateServiceImplTest {

    @Mock
    private ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider;
    @Mock
    private OptimizeTaskMapper optimizeTaskMapper;
    @Mock
    private OptimizeTaskCacheService optimizeTaskCacheService;

    private OptimizeTaskStateServiceImpl optimizeTaskStateService;

    @BeforeEach
    void setUp() {
        lenient().when(optimizeTaskMapperProvider.getIfAvailable()).thenReturn(optimizeTaskMapper);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), OptimizeTaskEntity.class);
        optimizeTaskStateService = new OptimizeTaskStateServiceImpl(optimizeTaskMapperProvider, optimizeTaskCacheService);
    }

    @Test
    void shouldUpdatePendingRunningSuccessFailedAndCacheStatus() {
        optimizeTaskStateService.markPending(1L, 2, LocalDateTime.now());
        verify(optimizeTaskMapper).update(any(), any());
        verify(optimizeTaskCacheService).cacheTaskStatus(1L, "PENDING");

        optimizeTaskStateService.markRunning(1L);
        verify(optimizeTaskCacheService).cacheTaskStatus(1L, "RUNNING");

        optimizeTaskStateService.markSuccess(1L, 99L);
        verify(optimizeTaskCacheService).cacheTaskStatus(1L, "SUCCESS");

        optimizeTaskStateService.markFailed(1L, "TASK_EXECUTION_FAILED", "err");
        verify(optimizeTaskCacheService).cacheTaskStatus(1L, "FAILED");
    }
}
