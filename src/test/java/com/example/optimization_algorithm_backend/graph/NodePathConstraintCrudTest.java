package com.example.optimization_algorithm_backend.graph;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.module.constraint.dto.CreateConstraintRequest;
import com.example.optimization_algorithm_backend.module.constraint.service.impl.ConstraintAppServiceImpl;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.graph.service.GraphVersionService;
import com.example.optimization_algorithm_backend.module.node.dto.CreateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.dto.UpdateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.service.impl.NodeAppServiceImpl;
import com.example.optimization_algorithm_backend.module.path.dto.CreatePathRequest;
import com.example.optimization_algorithm_backend.module.path.service.impl.PathAppServiceImpl;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodePathConstraintCrudTest {

    @Mock
    private ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    @Mock
    private ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    @Mock
    private ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    @Mock
    private ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    @Mock
    private ProcessNodeMapper processNodeMapper;
    @Mock
    private ProcessPathMapper processPathMapper;
    @Mock
    private ConstraintConditionMapper constraintConditionMapper;
    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private ResourceAccessService resourceAccessService;
    @Mock
    private GraphVersionService graphVersionService;

    private NodeAppServiceImpl nodeAppService;

    @BeforeEach
    void setUp() {
        lenient().when(processNodeMapperProvider.getIfAvailable()).thenReturn(processNodeMapper);
        lenient().when(processPathMapperProvider.getIfAvailable()).thenReturn(processPathMapper);
        lenient().when(constraintConditionMapperProvider.getIfAvailable()).thenReturn(constraintConditionMapper);
        lenient().when(equipmentMapperProvider.getIfAvailable()).thenReturn(equipmentMapper);
        nodeAppService = new NodeAppServiceImpl(processNodeMapperProvider, processPathMapperProvider,
                constraintConditionMapperProvider, equipmentMapperProvider, resourceAccessService, graphVersionService);
    }

    @Test
    void shouldCreateUpdateDeleteNodeAndCascadeDeletePathAndConstraint() {
        CreateNodeRequest create = new CreateNodeRequest();
        create.setNodeCode("A1");
        create.setNodeName("A");
        create.setNodeDescription("desc");
        create.setTimeCost(10);
        create.setPrecisionValue(new BigDecimal("0.9"));
        create.setCostValue(20);
        create.setSortNo(1);
        when(processNodeMapper.selectCount(any())).thenReturn(0L);
        when(processNodeMapper.insert(any())).thenAnswer(invocation -> {
            ProcessNodeEntity entity = invocation.getArgument(0);
            entity.setId(100L);
            return 1;
        });

        nodeAppService.createNode(9L, create);
        verify(graphVersionService, times(1)).increaseVersion(9L);

        ProcessNodeEntity existing = new ProcessNodeEntity();
        existing.setId(100L);
        existing.setGraphId(9L);
        existing.setNodeCode("A1");
        when(processNodeMapper.selectById(100L)).thenReturn(existing);
        when(processNodeMapper.selectCount(any())).thenReturn(0L);

        UpdateNodeRequest update = new UpdateNodeRequest();
        update.setNodeCode("A2");
        update.setNodeName("A2");
        update.setNodeDescription("d2");
        update.setTimeCost(11);
        update.setPrecisionValue(new BigDecimal("0.95"));
        update.setCostValue(21);
        update.setSortNo(2);
        nodeAppService.updateNode(9L, 100L, update);
        verify(graphVersionService, times(2)).increaseVersion(9L);

        when(processPathMapper.delete(any())).thenReturn(2);
        when(constraintConditionMapper.delete(any())).thenReturn(1);
        when(processNodeMapper.deleteById(100L)).thenReturn(1);
        boolean deleted = nodeAppService.deleteNode(9L, 100L);
        Assertions.assertTrue(deleted);
        verify(processPathMapper).delete(any());
        verify(constraintConditionMapper).delete(any());
        verify(graphVersionService, times(3)).increaseVersion(9L);
    }

    @Test
    void shouldCreateAndDeletePathWithNodeValidation() {
        PathAppServiceImpl pathAppService = new PathAppServiceImpl(
                processPathMapperProvider,
                processNodeMapperProvider,
                resourceAccessService,
                graphVersionService
        );
        ProcessNodeEntity n1 = new ProcessNodeEntity();
        n1.setId(1L);
        n1.setGraphId(9L);
        ProcessNodeEntity n2 = new ProcessNodeEntity();
        n2.setId(2L);
        n2.setGraphId(9L);
        when(processNodeMapper.selectById(1L)).thenReturn(n1);
        when(processNodeMapper.selectById(2L)).thenReturn(n2);

        CreatePathRequest req = new CreatePathRequest();
        req.setStartNodeId(1L);
        req.setEndNodeId(2L);
        pathAppService.createPath(9L, req);
        verify(processPathMapper).insert(any());
        verify(graphVersionService).increaseVersion(9L);
    }

    @Test
    void shouldRejectNodePrecisionGreaterThanOne() {
        CreateNodeRequest create = new CreateNodeRequest();
        create.setNodeCode("A1");
        create.setPrecisionValue(new BigDecimal("1.1"));

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> nodeAppService.createNode(9L, create));
        Assertions.assertTrue(ex.getMessage().contains("precisionValue必须在0到1之间"));
    }

    @Test
    void shouldNormalizeAndRejectConstraintType() {
        ConstraintAppServiceImpl constraintAppService = new ConstraintAppServiceImpl(
                constraintConditionMapperProvider,
                processNodeMapperProvider,
                resourceAccessService,
                graphVersionService
        );

        ProcessNodeEntity n1 = new ProcessNodeEntity();
        n1.setId(1L);
        n1.setGraphId(9L);
        ProcessNodeEntity n2 = new ProcessNodeEntity();
        n2.setId(2L);
        n2.setGraphId(9L);
        when(processNodeMapper.selectById(1L)).thenReturn(n1);
        when(processNodeMapper.selectById(2L)).thenReturn(n2);
        when(constraintConditionMapper.selectCount(any())).thenReturn(0L);

        CreateConstraintRequest valid = new CreateConstraintRequest();
        valid.setConditionCode("C1");
        valid.setConditionType(" follow ");
        valid.setNodeId1(1L);
        valid.setNodeId2(2L);
        constraintAppService.createConstraint(9L, valid);
        verify(constraintConditionMapper).insert(any());

        CreateConstraintRequest invalid = new CreateConstraintRequest();
        invalid.setConditionCode("C2");
        invalid.setConditionType("NORMAL");
        invalid.setNodeId1(1L);
        invalid.setNodeId2(2L);
        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> constraintAppService.createConstraint(9L, invalid));
        Assertions.assertTrue(ex.getMessage().contains("conditionType仅支持"));
    }
}
