package com.example.optimization_algorithm_backend.cache;

import com.example.optimization_algorithm_backend.common.cache.RedisSafeClient;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.graph.service.impl.GraphAppServiceImpl;
import com.example.optimization_algorithm_backend.module.graph.vo.GraphDetailVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphDetailCacheStrategyTest {

    @Mock
    private ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;
    @Mock
    private ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    @Mock
    private ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    @Mock
    private ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    @Mock
    private ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    @Mock
    private FlowGraphMapper flowGraphMapper;
    @Mock
    private ProcessNodeMapper processNodeMapper;
    @Mock
    private ProcessPathMapper processPathMapper;
    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private ConstraintConditionMapper constraintConditionMapper;
    @Mock
    private ResourceAccessService resourceAccessService;
    @Mock
    private RedisSafeClient redisSafeClient;

    private GraphAppServiceImpl graphAppService;

    @BeforeEach
    void setUp() {
        lenient().when(flowGraphMapperProvider.getIfAvailable()).thenReturn(flowGraphMapper);
        lenient().when(processNodeMapperProvider.getIfAvailable()).thenReturn(processNodeMapper);
        lenient().when(processPathMapperProvider.getIfAvailable()).thenReturn(processPathMapper);
        lenient().when(equipmentMapperProvider.getIfAvailable()).thenReturn(equipmentMapper);
        lenient().when(constraintConditionMapperProvider.getIfAvailable()).thenReturn(constraintConditionMapper);
        graphAppService = new GraphAppServiceImpl(flowGraphMapperProvider, processNodeMapperProvider, processPathMapperProvider,
                equipmentMapperProvider, constraintConditionMapperProvider, resourceAccessService, redisSafeClient);
        ReflectionTestUtils.setField(graphAppService, "graphDetailTtlMinutes", 30L);
        lenient().when(processNodeMapper.selectList(any())).thenReturn(Collections.emptyList());
        lenient().when(processPathMapper.selectList(any())).thenReturn(Collections.emptyList());
        lenient().when(equipmentMapper.selectList(any())).thenReturn(Collections.emptyList());
        lenient().when(constraintConditionMapper.selectList(any())).thenReturn(Collections.emptyList());
    }

    @Test
    void shouldWriteVersionedCacheOnMiss() {
        FlowGraphEntity graph = new FlowGraphEntity();
        graph.setId(11L);
        graph.setWorkspaceId(1L);
        graph.setGraphVersion(1L);
        when(resourceAccessService.getAccessibleGraph(11L)).thenReturn(graph);
        when(redisSafeClient.get("graph:detail:11:v1")).thenReturn(null);

        GraphDetailVO detail = graphAppService.getGraphDetail(11L);
        Assertions.assertNotNull(detail);
        verify(redisSafeClient).set(eq("graph:detail:11:v1"), any(GraphDetailVO.class), eq(30L), eq(java.util.concurrent.TimeUnit.MINUTES));
    }

    @Test
    void shouldReadFromCacheWhenHit() {
        FlowGraphEntity graph = new FlowGraphEntity();
        graph.setId(11L);
        graph.setWorkspaceId(1L);
        graph.setGraphVersion(1L);
        GraphDetailVO cached = new GraphDetailVO();
        when(resourceAccessService.getAccessibleGraph(11L)).thenReturn(graph);
        when(redisSafeClient.get("graph:detail:11:v1")).thenReturn(cached);

        GraphDetailVO result = graphAppService.getGraphDetail(11L);
        Assertions.assertSame(cached, result);
        verify(processNodeMapper, never()).selectList(any());
        verify(redisSafeClient, never()).set(eq("graph:detail:11:v1"), any(), eq(30L), eq(java.util.concurrent.TimeUnit.MINUTES));
    }

    @Test
    void shouldUseNewCacheKeyWhenGraphVersionChanges() {
        FlowGraphEntity graphV2 = new FlowGraphEntity();
        graphV2.setId(11L);
        graphV2.setWorkspaceId(1L);
        graphV2.setGraphVersion(2L);
        when(resourceAccessService.getAccessibleGraph(11L)).thenReturn(graphV2);
        when(redisSafeClient.get("graph:detail:11:v2")).thenReturn(null);

        graphAppService.getGraphDetail(11L);
        verify(redisSafeClient).get("graph:detail:11:v2");
        verify(redisSafeClient, never()).get("graph:detail:11:v1");
    }
}
