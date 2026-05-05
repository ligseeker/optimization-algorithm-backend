package com.example.optimization_algorithm_backend.module.yaml.converter;

import com.example.optimization_algorithm_backend.algorithm.model.Constant;
import com.example.optimization_algorithm_backend.algorithm.model.ConstraintCondition;
import com.example.optimization_algorithm_backend.algorithm.model.Equipment;
import com.example.optimization_algorithm_backend.algorithm.model.MultiNode;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessPath;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.module.constraint.support.ConstraintTypeSupport;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ProcessMapConverter {

    private ProcessMapConverter() {
    }

    public static FlowGraphEntity toFlowGraphEntity(Long workspaceId, String graphName, ProcessMap processMap) {
        FlowGraphEntity entity = new FlowGraphEntity();
        entity.setWorkspaceId(workspaceId);
        entity.setName(graphName);
        entity.setDescription("YAML导入生成");
        entity.setSourceType("YAML_IMPORT");
        entity.setGraphStatus("READY");
        entity.setTotalTime(calculateTotalTime(processMap.getMultiNodes()));
        entity.setTotalPrecision(calculateTotalPrecision(processMap.getMultiNodes()));
        entity.setTotalCost(calculateTotalCost(processMap.getMultiNodes()));
        return entity;
    }

    public static EquipmentEntity toEquipmentEntity(Long graphId, Equipment equipment) {
        EquipmentEntity entity = new EquipmentEntity();
        entity.setGraphId(graphId);
        entity.setName(equipment.getName());
        entity.setDescription(equipment.getDescription());
        entity.setColor(equipment.getColor());
        entity.setImagePath(equipment.getImagePath());
        return entity;
    }

    public static ProcessNodeEntity toNodeEntity(Long graphId,
                                                 MultiNode node,
                                                 Long equipmentId,
                                                 Integer sortNo) {
        ProcessNodeEntity entity = new ProcessNodeEntity();
        entity.setGraphId(graphId);
        entity.setNodeCode(node.getNodeID());
        entity.setNodeName(node.getNodeID());
        entity.setNodeDescription(node.getNodeDescription());
        entity.setEquipmentId(equipmentId);
        entity.setTimeCost(node.getTime());
        entity.setPrecisionValue(BigDecimal.valueOf(node.getPrecision()));
        entity.setCostValue(node.getCost());
        entity.setSortNo(sortNo);
        return entity;
    }

    public static ProcessPathEntity toPathEntity(Long graphId,
                                                 ProcessPath path,
                                                 Long startNodeId,
                                                 Long endNodeId) {
        ProcessPathEntity entity = new ProcessPathEntity();
        entity.setGraphId(graphId);
        entity.setStartNodeId(startNodeId);
        entity.setEndNodeId(endNodeId);
        entity.setRelationType(Constant.NORMAL.name());
        entity.setRemark("YAML导入");
        return entity;
    }

    public static ConstraintConditionEntity toConstraintEntity(Long graphId,
                                                               ConstraintCondition condition,
                                                               Long nodeId1,
                                                               Long nodeId2,
                                                               String fallbackCode) {
        ConstraintConditionEntity entity = new ConstraintConditionEntity();
        entity.setGraphId(graphId);
        entity.setConditionCode(StringUtils.hasText(condition.getConditionID()) ? condition.getConditionID() : fallbackCode);
        entity.setConditionType(condition.getConditionType().name());
        entity.setConditionDescription(condition.getConditionDescription());
        entity.setNodeId1(nodeId1);
        entity.setNodeId2(nodeId2);
        entity.setEnabled(1);
        return entity;
    }

    public static ProcessMap toProcessMap(FlowGraphEntity graph,
                                          List<ProcessNodeEntity> nodeEntities,
                                          List<ProcessPathEntity> pathEntities,
                                          List<ConstraintConditionEntity> constraintEntities,
                                          List<EquipmentEntity> equipmentEntities) {
        List<ProcessNodeEntity> safeNodes = nodeEntities == null ? Collections.emptyList() : nodeEntities;
        List<ProcessPathEntity> safePaths = pathEntities == null ? Collections.emptyList() : pathEntities;
        List<ConstraintConditionEntity> safeConstraints = constraintEntities == null ? Collections.emptyList() : constraintEntities;
        List<EquipmentEntity> safeEquipments = equipmentEntities == null ? Collections.emptyList() : equipmentEntities;

        Map<Long, String> nodeCodeById = safeNodes.stream()
                .collect(java.util.stream.Collectors.toMap(ProcessNodeEntity::getId, ProcessNodeEntity::getNodeCode));
        Map<Long, String> equipmentNameById = safeEquipments.stream()
                .collect(java.util.stream.Collectors.toMap(EquipmentEntity::getId, EquipmentEntity::getName));

        ArrayList<MultiNode> multiNodes = new ArrayList<>();
        for (ProcessNodeEntity node : safeNodes) {
            String equipmentName = node.getEquipmentId() == null ? "" : equipmentNameById.getOrDefault(node.getEquipmentId(), "");
            String description = StringUtils.hasText(node.getNodeDescription()) ? node.getNodeDescription() : node.getNodeName();
            multiNodes.add(new MultiNode(
                    node.getNodeCode(),
                    description,
                    equipmentName,
                    node.getTimeCost() == null ? 0 : node.getTimeCost(),
                    node.getPrecisionValue() == null ? 0D : node.getPrecisionValue().doubleValue(),
                    node.getCostValue() == null ? 0 : node.getCostValue()
            ));
        }

        LinkedList<ProcessPath> processPaths = new LinkedList<>();
        for (ProcessPathEntity path : safePaths) {
            processPaths.add(new ProcessPath(
                    "P" + path.getId(),
                    nodeCodeById.getOrDefault(path.getStartNodeId(), ""),
                    nodeCodeById.getOrDefault(path.getEndNodeId(), "")
            ));
        }

        ArrayList<ConstraintCondition> constraints = new ArrayList<>();
        for (ConstraintConditionEntity condition : safeConstraints) {
            Constant constant = ConstraintTypeSupport.parseForExport(condition.getConditionType());
            constraints.add(new ConstraintCondition(
                    condition.getConditionCode(),
                    condition.getConditionDescription(),
                    constant,
                    nodeCodeById.getOrDefault(condition.getNodeId1(), ""),
                    nodeCodeById.getOrDefault(condition.getNodeId2(), "")
            ));
        }

        ArrayList<Equipment> equipments = new ArrayList<>();
        for (EquipmentEntity equipmentEntity : safeEquipments) {
            ArrayList<String> nodes = new ArrayList<>();
            for (ProcessNodeEntity node : safeNodes) {
                if (equipmentEntity.getId().equals(node.getEquipmentId())) {
                    nodes.add(node.getNodeCode());
                }
            }
            equipments.add(new Equipment(
                    equipmentEntity.getName(),
                    nodes,
                    equipmentEntity.getColor(),
                    equipmentEntity.getDescription(),
                    equipmentEntity.getImagePath()
            ));
        }
        ProcessMap processMap = new ProcessMap("graph_" + graph.getId(), multiNodes, processPaths, constraints, equipments);
        processMap.setTotalTime(graph.getTotalTime() == null ? 0 : graph.getTotalTime());
        processMap.setTotalPrecision(graph.getTotalPrecision() == null ? 0D : graph.getTotalPrecision().doubleValue());
        processMap.setTotalCost(graph.getTotalCost() == null ? 0 : graph.getTotalCost());
        return processMap;
    }

    private static Integer calculateTotalTime(List<MultiNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (MultiNode node : nodes) {
            total += node.getTime();
        }
        return total;
    }

    private static BigDecimal calculateTotalPrecision(List<MultiNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        double total = 0D;
        for (MultiNode node : nodes) {
            total += node.getPrecision();
        }
        return BigDecimal.valueOf(total);
    }

    private static Integer calculateTotalCost(List<MultiNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (MultiNode node : nodes) {
            total += node.getCost();
        }
        return total;
    }
}
