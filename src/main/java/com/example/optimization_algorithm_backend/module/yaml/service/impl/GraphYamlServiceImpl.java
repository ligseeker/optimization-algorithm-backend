package com.example.optimization_algorithm_backend.module.yaml.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.optimization_algorithm_backend.algorithm.Main;
import com.example.optimization_algorithm_backend.algorithm.model.Constant;
import com.example.optimization_algorithm_backend.algorithm.model.ConstraintCondition;
import com.example.optimization_algorithm_backend.algorithm.model.Equipment;
import com.example.optimization_algorithm_backend.algorithm.model.InputInfo;
import com.example.optimization_algorithm_backend.algorithm.model.MultiNode;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessPath;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.exception.ImportValidationException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.constraint.support.ConstraintTypeSupport;
import com.example.optimization_algorithm_backend.module.yaml.converter.ProcessMapConverter;
import com.example.optimization_algorithm_backend.module.yaml.service.GraphYamlService;
import com.example.optimization_algorithm_backend.module.yaml.vo.GraphImportResponse;
import com.example.optimization_algorithm_backend.module.yaml.vo.GraphYamlExportResponse;
import com.example.optimization_algorithm_backend.module.yaml.vo.ImportErrorItem;
import com.example.optimization_algorithm_backend.module.yaml.vo.ImportErrorReport;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GraphYamlServiceImpl implements GraphYamlService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，YAML导入导出接口暂不可用";
    private static final Set<String> ALLOWED_FILE_SUFFIX = new HashSet<>(java.util.Arrays.asList(".yaml", ".yml"));

    private final ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;
    private final ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    private final ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    private final ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    private final ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    private final ResourceAccessService resourceAccessService;

    public GraphYamlServiceImpl(ObjectProvider<FlowGraphMapper> flowGraphMapperProvider,
                                ObjectProvider<EquipmentMapper> equipmentMapperProvider,
                                ObjectProvider<ProcessNodeMapper> processNodeMapperProvider,
                                ObjectProvider<ProcessPathMapper> processPathMapperProvider,
                                ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider,
                                ResourceAccessService resourceAccessService) {
        this.flowGraphMapperProvider = flowGraphMapperProvider;
        this.equipmentMapperProvider = equipmentMapperProvider;
        this.processNodeMapperProvider = processNodeMapperProvider;
        this.processPathMapperProvider = processPathMapperProvider;
        this.constraintConditionMapperProvider = constraintConditionMapperProvider;
        this.resourceAccessService = resourceAccessService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GraphImportResponse importGraph(Long workspaceId, String graphName, MultipartFile file) {
        resourceAccessService.getAccessibleWorkspace(workspaceId);
        validateFile(file);

        ProcessMap processMap = parseYamlToProcessMap(file);
        validateProcessMap(processMap);

        String finalGraphName = normalizeGraphName(graphName, file.getOriginalFilename());
        ensureGraphNameUnique(workspaceId, finalGraphName);

        FlowGraphEntity graphEntity = ProcessMapConverter.toFlowGraphEntity(workspaceId, finalGraphName, processMap);
        graphEntity.setGraphVersion(1L);
        graphEntity.setLastImportAt(LocalDateTime.now());
        getFlowGraphMapper().insert(graphEntity);
        Long graphId = graphEntity.getId();

        Map<String, Long> equipmentIdByName = saveEquipments(graphId, processMap.getEquipments());
        Map<String, Long> equipmentIdByNodeCode = buildEquipmentIdByNodeCode(processMap.getEquipments(), equipmentIdByName);
        Map<String, Long> nodeIdByCode = saveNodes(graphId, processMap.getMultiNodes(), equipmentIdByName, equipmentIdByNodeCode);
        savePaths(graphId, processMap.getProcessPaths(), nodeIdByCode);
        saveConstraints(graphId, processMap.getConstraintConditions(), nodeIdByCode);

        GraphImportResponse response = new GraphImportResponse();
        response.setGraphId(graphId);
        response.setWorkspaceId(workspaceId);
        response.setGraphName(finalGraphName);
        response.setSourceType(graphEntity.getSourceType());
        response.setNodeCount(processMap.getMultiNodes() == null ? 0 : processMap.getMultiNodes().size());
        response.setPathCount(processMap.getProcessPaths() == null ? 0 : processMap.getProcessPaths().size());
        response.setEquipmentCount(processMap.getEquipments() == null ? 0 : processMap.getEquipments().size());
        response.setConstraintCount(processMap.getConstraintConditions() == null ? 0 : processMap.getConstraintConditions().size());
        return response;
    }

    @Override
    public GraphYamlExportResponse exportGraphYaml(Long graphId) {
        FlowGraphEntity graph = resourceAccessService.getAccessibleGraph(graphId);
        List<EquipmentEntity> equipments = getEquipmentMapper().selectList(new LambdaQueryWrapper<EquipmentEntity>()
                .eq(EquipmentEntity::getGraphId, graphId));
        List<ProcessNodeEntity> nodes = getProcessNodeMapper().selectList(new LambdaQueryWrapper<ProcessNodeEntity>()
                .eq(ProcessNodeEntity::getGraphId, graphId)
                .orderByAsc(ProcessNodeEntity::getSortNo)
                .orderByAsc(ProcessNodeEntity::getId));
        List<ProcessPathEntity> paths = getProcessPathMapper().selectList(new LambdaQueryWrapper<ProcessPathEntity>()
                .eq(ProcessPathEntity::getGraphId, graphId)
                .orderByAsc(ProcessPathEntity::getId));
        List<ConstraintConditionEntity> constraints = getConstraintConditionMapper().selectList(new LambdaQueryWrapper<ConstraintConditionEntity>()
                .eq(ConstraintConditionEntity::getGraphId, graphId)
                .orderByAsc(ConstraintConditionEntity::getId));

        ProcessMap processMap = ProcessMapConverter.toProcessMap(graph, nodes, paths, constraints, equipments);
        String yamlContent = serializeProcessMapToYaml(processMap);

        graph.setLastExportAt(LocalDateTime.now());
        getFlowGraphMapper().updateById(graph);

        GraphYamlExportResponse response = new GraphYamlExportResponse();
        response.setGraphId(graph.getId());
        response.setGraphName(graph.getName());
        response.setFileName("graph-" + graph.getId() + ".yaml");
        response.setYamlContent(yamlContent);
        return response;
    }

    private void validateFile(MultipartFile file) {
        ImportErrorReport report = new ImportErrorReport();
        List<ImportErrorItem> errors = new ArrayList<>();
        if (file == null || file.isEmpty()) {
            errors.add(new ImportErrorItem("FILE_EMPTY", "file", "导入文件不能为空"));
        } else {
            String fileName = file.getOriginalFilename();
            String suffix = getFileSuffix(fileName);
            if (!ALLOWED_FILE_SUFFIX.contains(suffix)) {
                errors.add(new ImportErrorItem("FILE_TYPE_INVALID", "file", "仅支持.yaml或.yml文件"));
            }
        }
        if (!errors.isEmpty()) {
            report.setErrors(errors);
            report.setTotalErrors(errors.size());
            throw new ImportValidationException("YAML导入校验失败", report);
        }
    }

    private ProcessMap parseYamlToProcessMap(MultipartFile file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("graph-import-", ".yaml");
            file.transferTo(tempFile);
            InputInfo inputInfo = Main.readData(tempFile.getAbsolutePath());
            return Main.initMapTest(inputInfo);
        } catch (Exception ex) {
            ImportErrorReport report = new ImportErrorReport();
            List<ImportErrorItem> errors = new ArrayList<>();
            errors.add(new ImportErrorItem("YAML_PARSE_ERROR", "file", "YAML解析失败: " + ex.getMessage()));
            report.setErrors(errors);
            report.setTotalErrors(errors.size());
            throw new ImportValidationException("YAML导入校验失败", report);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                // 临时文件仅用于承接旧解析逻辑，解析后立即清理。
                tempFile.delete();
            }
        }
    }

    private void validateProcessMap(ProcessMap processMap) {
        List<ImportErrorItem> errors = new ArrayList<>();

        List<MultiNode> nodes = processMap.getMultiNodes() == null ? new ArrayList<>() : processMap.getMultiNodes();
        LinkedList<ProcessPath> paths = processMap.getProcessPaths() == null ? new LinkedList<>() : processMap.getProcessPaths();
        List<ConstraintCondition> constraints = processMap.getConstraintConditions() == null
                ? new ArrayList<>()
                : processMap.getConstraintConditions();

        Set<String> nodeCodes = new LinkedHashSet<>();
        for (int i = 0; i < nodes.size(); i++) {
            MultiNode node = nodes.get(i);
            String nodeCode = node.getNodeID();
            String location = "ProcessNodes[" + i + "].nodeID";
            if (!StringUtils.hasText(nodeCode)) {
                errors.add(new ImportErrorItem("NODE_ID_EMPTY", location, "节点ID不能为空"));
                continue;
            }
            if (!nodeCodes.add(nodeCode)) {
                errors.add(new ImportErrorItem("NODE_ID_DUPLICATE", location, "节点ID重复: " + nodeCode));
            }

            if (node.getTime() < 0) {
                errors.add(new ImportErrorItem("TIME_INVALID", "ProcessNodes[" + i + "].time", "time必须大于等于0"));
            }
            if (node.getCost() < 0) {
                errors.add(new ImportErrorItem("COST_INVALID", "ProcessNodes[" + i + "].cost", "cost必须大于等于0"));
            }
            if (Double.isNaN(node.getPrecision()) || Double.isInfinite(node.getPrecision()) || node.getPrecision() < 0 || node.getPrecision() > 1) {
                errors.add(new ImportErrorItem("PRECISION_INVALID", "ProcessNodes[" + i + "].precision", "precision必须在0到1之间"));
            }
        }

        Set<String> pathPairSet = new HashSet<>();
        for (int i = 0; i < paths.size(); i++) {
            ProcessPath path = paths.get(i);
            if (!nodeCodes.contains(path.getStartNodeID())) {
                errors.add(new ImportErrorItem(
                        "PATH_START_NOT_FOUND",
                        "Paths[" + i + "].from",
                        "路径起点节点不存在: " + path.getStartNodeID()
                ));
            }
            if (!nodeCodes.contains(path.getEndNodeID())) {
                errors.add(new ImportErrorItem(
                        "PATH_END_NOT_FOUND",
                        "Paths[" + i + "].to",
                        "路径终点节点不存在: " + path.getEndNodeID()
                ));
            }
            String pairKey = path.getStartNodeID() + "->" + path.getEndNodeID();
            if (!pathPairSet.add(pairKey)) {
                errors.add(new ImportErrorItem("PATH_DUPLICATE", "Paths[" + i + "]", "路径重复: " + pairKey));
            }
        }

        Set<String> conditionCodeSet = new HashSet<>();
        for (int i = 0; i < constraints.size(); i++) {
            ConstraintCondition condition = constraints.get(i);
            if (StringUtils.hasText(condition.getConditionID()) && !conditionCodeSet.add(condition.getConditionID())) {
                errors.add(new ImportErrorItem(
                        "CONDITION_CODE_DUPLICATE",
                        "ConstraintConditions[" + i + "].conditionID",
                        "约束ID重复: " + condition.getConditionID()
                ));
            }

            if (!nodeCodes.contains(condition.getNodeID1())) {
                errors.add(new ImportErrorItem(
                        "CONSTRAINT_NODE1_NOT_FOUND",
                        "ConstraintConditions[" + i + "].nodeID1",
                        "约束关联节点1不存在: " + condition.getNodeID1()
                ));
            }
            if (!nodeCodes.contains(condition.getNodeID2())) {
                errors.add(new ImportErrorItem(
                        "CONSTRAINT_NODE2_NOT_FOUND",
                        "ConstraintConditions[" + i + "].nodeID2",
                        "约束关联节点2不存在: " + condition.getNodeID2()
                ));
            }
            if (!ConstraintTypeSupport.isAllowed(condition.getConditionType())) {
                errors.add(new ImportErrorItem(
                        "CONSTRAINT_TYPE_INVALID",
                        "ConstraintConditions[" + i + "].conditionType",
                        "约束类型非法: " + (condition.getConditionType() == null ? "null" : condition.getConditionType().name())
                ));
            }
        }

        if (!errors.isEmpty()) {
            ImportErrorReport report = new ImportErrorReport(errors.size(), errors);
            throw new ImportValidationException("YAML导入校验失败", report);
        }
    }

    private Map<String, Long> saveEquipments(Long graphId, List<Equipment> equipments) {
        Map<String, Long> result = new HashMap<>();
        if (equipments == null) {
            return result;
        }
        for (Equipment equipment : equipments) {
            if (!StringUtils.hasText(equipment.getName())) {
                continue;
            }
            EquipmentEntity entity = ProcessMapConverter.toEquipmentEntity(graphId, equipment);
            getEquipmentMapper().insert(entity);
            result.put(equipment.getName(), entity.getId());
        }
        return result;
    }

    private Map<String, Long> buildEquipmentIdByNodeCode(List<Equipment> equipments, Map<String, Long> equipmentIdByName) {
        Map<String, Long> mapping = new HashMap<>();
        if (equipments == null) {
            return mapping;
        }
        for (Equipment equipment : equipments) {
            if (equipment.getNodes() == null || equipment.getNodes().isEmpty()) {
                continue;
            }
            Long equipmentId = equipmentIdByName.get(equipment.getName());
            if (equipmentId == null) {
                continue;
            }
            for (String nodeCode : equipment.getNodes()) {
                mapping.put(nodeCode, equipmentId);
            }
        }
        return mapping;
    }

    private Map<String, Long> saveNodes(Long graphId,
                                        List<MultiNode> nodes,
                                        Map<String, Long> equipmentIdByName,
                                        Map<String, Long> equipmentIdByNodeCode) {
        Map<String, Long> nodeIdByCode = new HashMap<>();
        if (nodes == null) {
            return nodeIdByCode;
        }
        int sort = 1;
        for (MultiNode node : nodes) {
            Long equipmentId = null;
            if (StringUtils.hasText(node.getEquipmentName())) {
                equipmentId = equipmentIdByName.get(node.getEquipmentName());
            }
            if (equipmentId == null) {
                equipmentId = equipmentIdByNodeCode.get(node.getNodeID());
            }
            ProcessNodeEntity entity = ProcessMapConverter.toNodeEntity(graphId, node, equipmentId, sort++);
            getProcessNodeMapper().insert(entity);
            nodeIdByCode.put(node.getNodeID(), entity.getId());
        }
        return nodeIdByCode;
    }

    private void savePaths(Long graphId, List<ProcessPath> paths, Map<String, Long> nodeIdByCode) {
        if (paths == null) {
            return;
        }
        for (ProcessPath path : paths) {
            Long startNodeId = nodeIdByCode.get(path.getStartNodeID());
            Long endNodeId = nodeIdByCode.get(path.getEndNodeID());
            ProcessPathEntity entity = ProcessMapConverter.toPathEntity(graphId, path, startNodeId, endNodeId);
            getProcessPathMapper().insert(entity);
        }
    }

    private void saveConstraints(Long graphId, List<ConstraintCondition> constraints, Map<String, Long> nodeIdByCode) {
        if (constraints == null) {
            return;
        }
        int index = 1;
        for (ConstraintCondition constraint : constraints) {
            Long nodeId1 = nodeIdByCode.get(constraint.getNodeID1());
            Long nodeId2 = nodeIdByCode.get(constraint.getNodeID2());
            String fallbackCode = "CND_" + index++;
            ConstraintConditionEntity entity = ProcessMapConverter.toConstraintEntity(graphId, constraint, nodeId1, nodeId2, fallbackCode);
            getConstraintConditionMapper().insert(entity);
        }
    }

    private String serializeProcessMapToYaml(ProcessMap processMap) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("graph-export-", ".yaml");
            Main.writeData(processMap, tempFile.getAbsolutePath());
            // 旧算法写文件使用平台默认编码，这里用同编码读取避免 Windows 下导出乱码/解码失败。
            return Files.readString(tempFile.toPath(), Charset.defaultCharset());
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.FILE_PARSE_FAILED, "YAML导出失败: " + ex.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private String normalizeGraphName(String graphName, String originalFileName) {
        if (StringUtils.hasText(graphName)) {
            return graphName.trim();
        }
        if (StringUtils.hasText(originalFileName)) {
            String fileName = originalFileName.trim();
            int idx = fileName.lastIndexOf('.');
            if (idx > 0) {
                fileName = fileName.substring(0, idx);
            }
            if (StringUtils.hasText(fileName)) {
                return fileName;
            }
        }
        return "yaml_import_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
    }

    private void ensureGraphNameUnique(Long workspaceId, String graphName) {
        Long count = getFlowGraphMapper().selectCount(new LambdaQueryWrapper<FlowGraphEntity>()
                .eq(FlowGraphEntity::getWorkspaceId, workspaceId)
                .eq(FlowGraphEntity::getName, graphName));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "流程图名称已存在");
        }
    }

    private String getFileSuffix(String fileName) {
        if (!StringUtils.hasText(fileName) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    }

    private FlowGraphMapper getFlowGraphMapper() {
        FlowGraphMapper mapper = flowGraphMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }

    private EquipmentMapper getEquipmentMapper() {
        EquipmentMapper mapper = equipmentMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }

    private ProcessNodeMapper getProcessNodeMapper() {
        ProcessNodeMapper mapper = processNodeMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }

    private ProcessPathMapper getProcessPathMapper() {
        ProcessPathMapper mapper = processPathMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }

    private ConstraintConditionMapper getConstraintConditionMapper() {
        ConstraintConditionMapper mapper = constraintConditionMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }
}
