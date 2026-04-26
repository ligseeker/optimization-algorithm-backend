package com.example.optimization_algorithm_backend.yaml;

import com.example.optimization_algorithm_backend.common.exception.ImportValidationException;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.yaml.service.impl.GraphYamlServiceImpl;
import com.example.optimization_algorithm_backend.module.yaml.vo.GraphImportResponse;
import com.example.optimization_algorithm_backend.module.yaml.vo.GraphYamlExportResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphYamlServiceImplTest {

    @Mock
    private ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;
    @Mock
    private ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    @Mock
    private ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    @Mock
    private ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    @Mock
    private ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    @Mock
    private FlowGraphMapper flowGraphMapper;
    @Mock
    private EquipmentMapper equipmentMapper;
    @Mock
    private ProcessNodeMapper processNodeMapper;
    @Mock
    private ProcessPathMapper processPathMapper;
    @Mock
    private ConstraintConditionMapper constraintConditionMapper;
    @Mock
    private ResourceAccessService resourceAccessService;

    private GraphYamlServiceImpl graphYamlService;

    @BeforeEach
    void setUp() {
        lenient().when(flowGraphMapperProvider.getIfAvailable()).thenReturn(flowGraphMapper);
        lenient().when(equipmentMapperProvider.getIfAvailable()).thenReturn(equipmentMapper);
        lenient().when(processNodeMapperProvider.getIfAvailable()).thenReturn(processNodeMapper);
        lenient().when(processPathMapperProvider.getIfAvailable()).thenReturn(processPathMapper);
        lenient().when(constraintConditionMapperProvider.getIfAvailable()).thenReturn(constraintConditionMapper);
        graphYamlService = new GraphYamlServiceImpl(flowGraphMapperProvider, equipmentMapperProvider,
                processNodeMapperProvider, processPathMapperProvider, constraintConditionMapperProvider, resourceAccessService);
    }

    @Test
    void shouldImportValidYamlSuccessfully() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(1L);
        when(resourceAccessService.getAccessibleWorkspace(1L)).thenReturn(workspace);
        when(flowGraphMapper.selectCount(any())).thenReturn(0L);
        when(flowGraphMapper.insert(any())).thenAnswer(invocation -> {
            FlowGraphEntity entity = invocation.getArgument(0);
            entity.setId(1001L);
            return 1;
        });
        AtomicLong equipmentId = new AtomicLong(2000L);
        AtomicLong nodeId = new AtomicLong(3000L);
        when(equipmentMapper.insert(any())).thenAnswer(invocation -> {
            EquipmentEntity entity = invocation.getArgument(0);
            entity.setId(equipmentId.incrementAndGet());
            return 1;
        });
        when(processNodeMapper.insert(any())).thenAnswer(invocation -> {
            ProcessNodeEntity entity = invocation.getArgument(0);
            entity.setId(nodeId.incrementAndGet());
            return 1;
        });

        String yaml = "ProcessNodes:\n" +
                "  - nodeID: A1\n" +
                "    nodeDescription: 节点A\n" +
                "    equipmentName: EQ1\n" +
                "    time: 10\n" +
                "    precision: 0.9\n" +
                "    cost: 20\n" +
                "  - nodeID: B1\n" +
                "    nodeDescription: 节点B\n" +
                "    equipmentName: EQ1\n" +
                "    time: 12\n" +
                "    precision: 0.85\n" +
                "    cost: 22\n" +
                "Paths:\n" +
                "  - from: A1\n" +
                "    to: B1\n" +
                "ConstraintConditions:\n" +
                "  - conditionID: C1\n" +
                "    conditionType: FOLLOW\n" +
                "    conditionDescription: A在B前\n" +
                "    nodeID1: A1\n" +
                "    nodeID2: B1\n" +
                "Equipments:\n" +
                "  - name: EQ1\n" +
                "    nodes: [A1, B1]\n" +
                "    color: '#1677ff'\n" +
                "    description: 设备1\n" +
                "    imagePath: /img/eq1.png\n";

        MockMultipartFile file = new MockMultipartFile("file", "ok.yaml", "application/x-yaml", yaml.getBytes());
        GraphImportResponse response = graphYamlService.importGraph(1L, "g1", file);
        Assertions.assertNotNull(response.getGraphId());
        Assertions.assertEquals(2, response.getNodeCount());
        Assertions.assertEquals(1, response.getPathCount());
    }

    @Test
    void shouldFailImportWhenNodeIdDuplicated() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(1L);
        when(resourceAccessService.getAccessibleWorkspace(1L)).thenReturn(workspace);

        String yaml = "ProcessNodes:\n" +
                "  - nodeID: A1\n" +
                "    nodeDescription: 节点A\n" +
                "    equipmentName: EQ1\n" +
                "    time: 10\n" +
                "    precision: 0.9\n" +
                "    cost: 20\n" +
                "  - nodeID: A1\n" +
                "    nodeDescription: 节点A2\n" +
                "    equipmentName: EQ1\n" +
                "    time: 11\n" +
                "    precision: 0.8\n" +
                "    cost: 21\n" +
                "Paths: []\n" +
                "ConstraintConditions: []\n" +
                "Equipments: []\n";

        MockMultipartFile file = new MockMultipartFile("file", "dup.yaml", "application/x-yaml", yaml.getBytes());
        ImportValidationException ex = Assertions.assertThrows(ImportValidationException.class,
                () -> graphYamlService.importGraph(1L, "g1", file));
        Assertions.assertTrue(ex.getErrorReport().getErrors().stream()
                .anyMatch(item -> "NODE_ID_DUPLICATE".equals(item.getCode())));
    }

    @Test
    void shouldFailImportWhenPathOrConstraintNodeMissing() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(1L);
        when(resourceAccessService.getAccessibleWorkspace(1L)).thenReturn(workspace);

        String yaml = "ProcessNodes:\n" +
                "  - nodeID: A1\n" +
                "    nodeDescription: 节点A\n" +
                "    equipmentName: EQ1\n" +
                "    time: 10\n" +
                "    precision: 0.9\n" +
                "    cost: 20\n" +
                "Paths:\n" +
                "  - from: A1\n" +
                "    to: X1\n" +
                "ConstraintConditions:\n" +
                "  - conditionID: C1\n" +
                "    conditionType: FOLLOW\n" +
                "    conditionDescription: A在B前\n" +
                "    nodeID1: A1\n" +
                "    nodeID2: X2\n" +
                "Equipments: []\n";

        MockMultipartFile file = new MockMultipartFile("file", "badref.yaml", "application/x-yaml", yaml.getBytes());
        ImportValidationException ex = Assertions.assertThrows(ImportValidationException.class,
                () -> graphYamlService.importGraph(1L, "g1", file));
        Assertions.assertTrue(ex.getErrorReport().getErrors().stream()
                .anyMatch(item -> "PATH_END_NOT_FOUND".equals(item.getCode())));
        Assertions.assertTrue(ex.getErrorReport().getErrors().stream()
                .anyMatch(item -> "CONSTRAINT_NODE2_NOT_FOUND".equals(item.getCode())));
    }

    @Test
    void shouldExportYamlFromMysqlData() {
        FlowGraphEntity graph = new FlowGraphEntity();
        graph.setId(88L);
        graph.setName("graph88");
        graph.setGraphVersion(2L);
        graph.setTotalTime(20);
        graph.setTotalPrecision(new BigDecimal("1.8000"));
        graph.setTotalCost(30);
        when(resourceAccessService.getAccessibleGraph(88L)).thenReturn(graph);

        EquipmentEntity equipment = new EquipmentEntity();
        equipment.setId(701L);
        equipment.setGraphId(88L);
        equipment.setName("EQ1");
        equipment.setColor("#1677ff");
        equipment.setDescription("设备");
        equipment.setImagePath("/img.png");

        ProcessNodeEntity n1 = new ProcessNodeEntity();
        n1.setId(801L);
        n1.setGraphId(88L);
        n1.setNodeCode("A1");
        n1.setNodeName("A1");
        n1.setNodeDescription("节点A");
        n1.setEquipmentId(701L);
        n1.setTimeCost(10);
        n1.setPrecisionValue(new BigDecimal("0.9"));
        n1.setCostValue(10);
        n1.setSortNo(1);

        ProcessNodeEntity n2 = new ProcessNodeEntity();
        n2.setId(802L);
        n2.setGraphId(88L);
        n2.setNodeCode("B1");
        n2.setNodeName("B1");
        n2.setNodeDescription("节点B");
        n2.setEquipmentId(701L);
        n2.setTimeCost(10);
        n2.setPrecisionValue(new BigDecimal("0.9"));
        n2.setCostValue(20);
        n2.setSortNo(2);

        ProcessPathEntity path = new ProcessPathEntity();
        path.setId(901L);
        path.setGraphId(88L);
        path.setStartNodeId(801L);
        path.setEndNodeId(802L);

        when(equipmentMapper.selectList(any())).thenReturn(Collections.singletonList(equipment));
        when(processNodeMapper.selectList(any())).thenReturn(new ArrayList<ProcessNodeEntity>() {{
            add(n1);
            add(n2);
        }});
        when(processPathMapper.selectList(any())).thenReturn(Collections.singletonList(path));
        when(constraintConditionMapper.selectList(any())).thenReturn(Collections.emptyList());

        GraphYamlExportResponse response = graphYamlService.exportGraphYaml(88L);
        Assertions.assertNotNull(response.getYamlContent());
        Assertions.assertTrue(response.getYamlContent().contains("A1"));
        Assertions.assertTrue(response.getYamlContent().contains("B1"));
    }
}
