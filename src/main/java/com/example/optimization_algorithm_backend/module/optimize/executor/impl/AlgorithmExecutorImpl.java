package com.example.optimization_algorithm_backend.module.optimize.executor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.optimization_algorithm_backend.algorithm.Main;
import com.example.optimization_algorithm_backend.algorithm.algorithm1.Algorithm1;
import com.example.optimization_algorithm_backend.algorithm.algorithm2.Algorithm2;
import com.example.optimization_algorithm_backend.algorithm.algorithm3.Algorithm3;
import com.example.optimization_algorithm_backend.algorithm.model.ConstraintCondition;
import com.example.optimization_algorithm_backend.algorithm.model.Equipment;
import com.example.optimization_algorithm_backend.algorithm.model.InputInfo;
import com.example.optimization_algorithm_backend.algorithm.model.MultiNode;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessMap;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessNode;
import com.example.optimization_algorithm_backend.algorithm.model.ProcessPath;
import com.example.optimization_algorithm_backend.algorithm.model.utils;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeResultEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeResultMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeTaskMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.module.optimize.constant.OptimizeTaskStatus;
import com.example.optimization_algorithm_backend.module.optimize.executor.AlgorithmExecutor;
import com.example.optimization_algorithm_backend.module.yaml.converter.ProcessMapConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class AlgorithmExecutorImpl implements AlgorithmExecutor {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，优化任务执行暂不可用";

    private final ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider;
    private final ObjectProvider<OptimizeResultMapper> optimizeResultMapperProvider;
    private final ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;
    private final ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    private final ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    private final ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    private final ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    private final ThreadPoolTaskExecutor optimizeTaskExecutor;
    private final ObjectMapper objectMapper;

    public AlgorithmExecutorImpl(ObjectProvider<OptimizeTaskMapper> optimizeTaskMapperProvider,
                                 ObjectProvider<OptimizeResultMapper> optimizeResultMapperProvider,
                                 ObjectProvider<FlowGraphMapper> flowGraphMapperProvider,
                                 ObjectProvider<ProcessNodeMapper> processNodeMapperProvider,
                                 ObjectProvider<ProcessPathMapper> processPathMapperProvider,
                                 ObjectProvider<EquipmentMapper> equipmentMapperProvider,
                                 ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider,
                                 @Qualifier("optimizeTaskExecutor") ThreadPoolTaskExecutor optimizeTaskExecutor,
                                 ObjectMapper objectMapper) {
        this.optimizeTaskMapperProvider = optimizeTaskMapperProvider;
        this.optimizeResultMapperProvider = optimizeResultMapperProvider;
        this.flowGraphMapperProvider = flowGraphMapperProvider;
        this.processNodeMapperProvider = processNodeMapperProvider;
        this.processPathMapperProvider = processPathMapperProvider;
        this.equipmentMapperProvider = equipmentMapperProvider;
        this.constraintConditionMapperProvider = constraintConditionMapperProvider;
        this.optimizeTaskExecutor = optimizeTaskExecutor;
        this.objectMapper = objectMapper;
    }

    @Override
    public void submit(Long taskId) {
        optimizeTaskExecutor.execute(() -> executeTask(taskId));
    }

    private void executeTask(Long taskId) {
        OptimizeTaskEntity task = getOptimizeTaskMapper().selectById(taskId);
        if (task == null) {
            return;
        }
        if (!OptimizeTaskStatus.PENDING.equals(task.getTaskStatus()) && !OptimizeTaskStatus.RUNNING.equals(task.getTaskStatus())) {
            return;
        }

        try {
            markTaskRunning(taskId);
            OptimizeContext context = buildContext(taskId);
            AlgorithmOutput output = runAlgorithm(context);
            persistSuccess(context, output);
        } catch (Exception ex) {
            persistFailure(taskId, ex);
        }
    }

    private OptimizeContext buildContext(Long taskId) {
        OptimizeTaskEntity task = getOptimizeTaskMapper().selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "优化任务不存在");
        }

        FlowGraphEntity graph = getFlowGraphMapper().selectById(task.getGraphId());
        if (graph == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流程图不存在");
        }

        List<ProcessNodeEntity> nodes = getProcessNodeMapper().selectList(new LambdaQueryWrapper<ProcessNodeEntity>()
                .eq(ProcessNodeEntity::getGraphId, graph.getId())
                .orderByAsc(ProcessNodeEntity::getSortNo)
                .orderByAsc(ProcessNodeEntity::getId));
        List<ProcessPathEntity> paths = getProcessPathMapper().selectList(new LambdaQueryWrapper<ProcessPathEntity>()
                .eq(ProcessPathEntity::getGraphId, graph.getId())
                .orderByAsc(ProcessPathEntity::getId));
        List<EquipmentEntity> equipments = getEquipmentMapper().selectList(new LambdaQueryWrapper<EquipmentEntity>()
                .eq(EquipmentEntity::getGraphId, graph.getId())
                .orderByAsc(EquipmentEntity::getId));
        List<ConstraintConditionEntity> constraints = getConstraintConditionMapper().selectList(new LambdaQueryWrapper<ConstraintConditionEntity>()
                .eq(ConstraintConditionEntity::getGraphId, graph.getId())
                .orderByAsc(ConstraintConditionEntity::getId));

        ProcessMap sourceMap = ProcessMapConverter.toProcessMap(graph, nodes, paths, constraints, equipments);
        ProcessMap sourceSnapshot = deepCopyProcessMap(sourceMap);
        int[] factors = new int[]{
                task.getTimeWeight() == null ? 1 : task.getTimeWeight(),
                task.getPrecisionWeight() == null ? 1 : task.getPrecisionWeight(),
                task.getCostWeight() == null ? 1 : task.getCostWeight()
        };
        return new OptimizeContext(task, graph, sourceMap, sourceSnapshot, factors);
    }

    private AlgorithmOutput runAlgorithm(OptimizeContext context) {
        InputInfo inputInfo = toInputInfo(context.sourceMap, context.factors);
        ProcessMap map1 = utils.initProcessMap(inputInfo);

        ProcessMap optimizedMap;
        Map<String, Object> values;
        int mode = context.task.getAlgorithmMode() == null ? 0 : context.task.getAlgorithmMode();
        if (context.task.getAlgorithmType() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "algorithmType不能为空");
        }
        switch (context.task.getAlgorithmType()) {
            case 1: {
                Algorithm1 algorithm1 = new Algorithm1();
                algorithm1.initAlgorithm(map1);
                ProcessMap map2 = algorithm1.getOptimizationMap(map1, mode);
                ProcessMap map3 = utils.restoreProcessMap(map2);
                optimizedMap = algorithm1.OptimizeMap(map3, context.factors, mode);
                values = algorithm1.getValue(optimizedMap);
                break;
            }
            case 2: {
                Algorithm2 algorithm2 = new Algorithm2();
                algorithm2.initAlgorithm(map1);
                ProcessMap map2 = algorithm2.getOptimizationMap(map1, context.factors, mode);
                ProcessMap map3 = utils.restoreProcessMap(map2);
                optimizedMap = algorithm2.OptimizeMap(map3, context.factors, mode);
                values = algorithm2.getValue(optimizedMap);
                break;
            }
            case 3: {
                Algorithm3 algorithm3 = new Algorithm3();
                algorithm3.initAlgorithm(map1);
                ProcessMap map2 = algorithm3.getOptimizationMap(map1, context.factors, mode);
                ProcessMap map3 = utils.restoreProcessMap(map2);
                optimizedMap = algorithm3.OptimizeMap(map3, context.factors, mode);
                values = algorithm3.getValue(optimizedMap);
                break;
            }
            default:
                throw new BusinessException(ErrorCode.PARAM_INVALID, "algorithmType仅支持1/2/3");
        }

        LinkedList<ProcessPath> oldPaths = copyPaths(context.sourceSnapshot.getProcessPaths());
        LinkedList<ProcessPath> newPaths = copyPaths(optimizedMap.getProcessPaths());
        Map<String, Object> pathDiff = Main.compareMapPaths(oldPaths, newPaths);
        String mapCode = Main.WriteMapCode(optimizedMap, pathDiff);

        int beforeTime = extractMetricInt(values, "oldValue", "time", context.sourceSnapshot.getTotalTime());
        double beforePrecision = extractMetricDouble(values, "oldValue", "precision", context.sourceSnapshot.getTotalPrecision());
        int beforeCost = extractMetricInt(values, "oldValue", "cost", context.sourceSnapshot.getTotalCost());
        int afterTime = extractMetricInt(values, "newValue", "time", optimizedMap.getTotalTime());
        double afterPrecision = extractMetricDouble(values, "newValue", "precision", optimizedMap.getTotalPrecision());
        int afterCost = extractMetricInt(values, "newValue", "cost", optimizedMap.getTotalCost());

        Map<String, Object> diff = buildSimplifiedDiff(pathDiff, beforeTime, beforePrecision, beforeCost, afterTime, afterPrecision, afterCost);
        Map<String, Object> resultGraph = buildResultGraph(optimizedMap);
        BigDecimal scoreRatio = calculateScoreRatio(beforeTime, beforePrecision, beforeCost, afterTime, afterPrecision, afterCost, context.factors);

        AlgorithmOutput output = new AlgorithmOutput();
        output.resultGraph = resultGraph;
        output.diff = diff;
        output.mapCode = mapCode;
        output.beforeTime = beforeTime;
        output.beforePrecision = beforePrecision;
        output.beforeCost = beforeCost;
        output.afterTime = afterTime;
        output.afterPrecision = afterPrecision;
        output.afterCost = afterCost;
        output.scoreRatio = scoreRatio;
        return output;
    }

    private void persistSuccess(OptimizeContext context, AlgorithmOutput output) {
        try {
            OptimizeResultEntity result = new OptimizeResultEntity();
            result.setTaskId(context.task.getId());
            result.setWorkspaceId(context.task.getWorkspaceId());
            result.setSourceGraphId(context.task.getGraphId());
            result.setResultName("优化结果-" + context.task.getTaskNo());
            result.setResultGraphJson(objectMapper.writeValueAsString(output.resultGraph));
            result.setDiffJson(objectMapper.writeValueAsString(output.diff));
            result.setMapCode(output.mapCode);
            result.setTotalTimeBefore(output.beforeTime);
            result.setTotalPrecisionBefore(BigDecimal.valueOf(output.beforePrecision).setScale(4, RoundingMode.HALF_UP));
            result.setTotalCostBefore(output.beforeCost);
            result.setTotalTimeAfter(output.afterTime);
            result.setTotalPrecisionAfter(BigDecimal.valueOf(output.afterPrecision).setScale(4, RoundingMode.HALF_UP));
            result.setTotalCostAfter(output.afterCost);
            result.setScoreRatio(output.scoreRatio);
            getOptimizeResultMapper().insert(result);

            getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                    .eq(OptimizeTaskEntity::getId, context.task.getId())
                    .set(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.SUCCESS)
                    .set(OptimizeTaskEntity::getFinishedAt, LocalDateTime.now())
                    .set(OptimizeTaskEntity::getResultId, result.getId())
                    .set(OptimizeTaskEntity::getErrorCode, null)
                    .set(OptimizeTaskEntity::getErrorMessage, null));
        } catch (Exception ex) {
            persistFailure(context.task.getId(), ex);
        }
    }

    private void persistFailure(Long taskId, Exception ex) {
        getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, taskId)
                .set(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.FAILED)
                .set(OptimizeTaskEntity::getFinishedAt, LocalDateTime.now())
                .set(OptimizeTaskEntity::getErrorCode, "TASK_EXECUTION_FAILED")
                .set(OptimizeTaskEntity::getErrorMessage, truncate(ex.getMessage(), 1000)));
    }

    private void markTaskRunning(Long taskId) {
        getOptimizeTaskMapper().update(null, new LambdaUpdateWrapper<OptimizeTaskEntity>()
                .eq(OptimizeTaskEntity::getId, taskId)
                .set(OptimizeTaskEntity::getTaskStatus, OptimizeTaskStatus.RUNNING)
                .set(OptimizeTaskEntity::getStartedAt, LocalDateTime.now())
                .set(OptimizeTaskEntity::getFinishedAt, null)
                .set(OptimizeTaskEntity::getErrorCode, null)
                .set(OptimizeTaskEntity::getErrorMessage, null));
    }

    private InputInfo toInputInfo(ProcessMap processMap, int[] factors) {
        ArrayList<ProcessNode> processNodes = new ArrayList<>();
        if (processMap.getMultiNodes() != null) {
            for (MultiNode node : processMap.getMultiNodes()) {
                processNodes.add(new ProcessNode(
                        node.getNodeID(),
                        node.getNodeDescription(),
                        node.getEquipmentName(),
                        node.getTime(),
                        node.getPrecision(),
                        node.getCost()
                ));
            }
        }
        return new InputInfo(
                processNodes,
                copyConstraints(processMap.getConstraintConditions()),
                copyPaths(processMap.getProcessPaths()),
                copyEquipments(processMap.getEquipments()),
                factors
        );
    }

    private Map<String, Object> buildSimplifiedDiff(Map<String, Object> pathDiff,
                                                    int beforeTime,
                                                    double beforePrecision,
                                                    int beforeCost,
                                                    int afterTime,
                                                    double afterPrecision,
                                                    int afterCost) {
        List<Map<String, String>> addedPaths = new ArrayList<>();
        List<Map<String, String>> removedPaths = new ArrayList<>();

        Object rawAdd = pathDiff.get("addPath");
        if (rawAdd instanceof List) {
            for (Object item : (List<?>) rawAdd) {
                if (item instanceof Map) {
                    Map<?, ?> raw = (Map<?, ?>) item;
                    Map<String, String> path = new HashMap<>();
                    path.put("fromNodeCode", String.valueOf(raw.get("start")));
                    path.put("toNodeCode", String.valueOf(raw.get("end")));
                    addedPaths.add(path);
                }
            }
        }

        Object rawRemove = pathDiff.get("removePath");
        if (rawRemove instanceof List) {
            for (Object item : (List<?>) rawRemove) {
                if (item instanceof Map) {
                    Map<?, ?> raw = (Map<?, ?>) item;
                    Map<String, String> path = new HashMap<>();
                    path.put("fromNodeCode", String.valueOf(raw.get("start")));
                    path.put("toNodeCode", String.valueOf(raw.get("end")));
                    removedPaths.add(path);
                }
            }
        }

        Map<String, Object> diff = new HashMap<>();
        diff.put("addedPaths", addedPaths);
        diff.put("removedPaths", removedPaths);
        Map<String, Object> metricDiff = new HashMap<>();
        metricDiff.put("time", metricDiffItem(beforeTime, afterTime));
        metricDiff.put("precision", metricDiffItem(beforePrecision, afterPrecision));
        metricDiff.put("cost", metricDiffItem(beforeCost, afterCost));
        diff.put("metricDiff", metricDiff);
        return diff;
    }

    private Map<String, Object> buildResultGraph(ProcessMap processMap) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        if (processMap.getMultiNodes() != null) {
            for (MultiNode node : processMap.getMultiNodes()) {
                Map<String, Object> item = new HashMap<>();
                item.put("nodeCode", node.getNodeID());
                item.put("nodeDescription", node.getNodeDescription());
                item.put("equipmentName", node.getEquipmentName());
                item.put("time", node.getTime());
                item.put("precision", node.getPrecision());
                item.put("cost", node.getCost());
                nodes.add(item);
            }
        }
        List<Map<String, Object>> paths = new ArrayList<>();
        if (processMap.getProcessPaths() != null) {
            for (ProcessPath path : processMap.getProcessPaths()) {
                Map<String, Object> item = new HashMap<>();
                item.put("from", path.getStartNodeID());
                item.put("to", path.getEndNodeID());
                paths.add(item);
            }
        }
        List<Map<String, Object>> constraints = new ArrayList<>();
        if (processMap.getConstraintConditions() != null) {
            for (ConstraintCondition condition : processMap.getConstraintConditions()) {
                Map<String, Object> item = new HashMap<>();
                item.put("conditionCode", condition.getConditionID());
                item.put("conditionType", condition.getConditionType() == null ? null : condition.getConditionType().name());
                item.put("conditionDescription", condition.getConditionDescription());
                item.put("nodeID1", condition.getNodeID1());
                item.put("nodeID2", condition.getNodeID2());
                constraints.add(item);
            }
        }
        List<Map<String, Object>> equipments = new ArrayList<>();
        if (processMap.getEquipments() != null) {
            for (Equipment equipment : processMap.getEquipments()) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", equipment.getName());
                item.put("nodes", equipment.getNodes());
                item.put("color", equipment.getColor());
                item.put("description", equipment.getDescription());
                item.put("imagePath", equipment.getImagePath());
                equipments.add(item);
            }
        }

        result.put("nodes", nodes);
        result.put("paths", paths);
        result.put("constraints", constraints);
        result.put("equipments", equipments);
        result.put("totalTime", processMap.getTotalTime());
        result.put("totalPrecision", processMap.getTotalPrecision());
        result.put("totalCost", processMap.getTotalCost());
        return result;
    }

    private Map<String, Object> metricDiffItem(Number before, Number after) {
        Map<String, Object> item = new HashMap<>();
        item.put("before", before);
        item.put("after", after);
        if (before instanceof Integer && after instanceof Integer) {
            item.put("change", after.intValue() - before.intValue());
        } else {
            BigDecimal change = BigDecimal.valueOf(after.doubleValue() - before.doubleValue()).setScale(6, RoundingMode.HALF_UP);
            item.put("change", change);
        }
        return item;
    }

    private int extractMetricInt(Map<String, Object> values, String parentKey, String key, int fallback) {
        Object parent = values.get(parentKey);
        if (!(parent instanceof Map)) {
            return fallback;
        }
        Object value = ((Map<?, ?>) parent).get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return fallback;
    }

    private double extractMetricDouble(Map<String, Object> values, String parentKey, String key, double fallback) {
        Object parent = values.get(parentKey);
        if (!(parent instanceof Map)) {
            return fallback;
        }
        Object value = ((Map<?, ?>) parent).get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return fallback;
    }

    private BigDecimal calculateScoreRatio(int beforeTime,
                                           double beforePrecision,
                                           int beforeCost,
                                           int afterTime,
                                           double afterPrecision,
                                           int afterCost,
                                           int[] factors) {
        int sum = factors[0] + factors[1] + factors[2];
        if (sum <= 0) {
            sum = 1;
        }
        double wTime = (double) factors[0] / sum;
        double wPrecision = (double) factors[1] / sum;
        double wCost = (double) factors[2] / sum;

        double timePart = beforeTime <= 0 ? 0D : (double) (beforeTime - afterTime) / beforeTime;
        double precisionPart = afterPrecision - beforePrecision;
        double costPart = beforeCost <= 0 ? 0D : (double) (beforeCost - afterCost) / beforeCost;
        double ratio = timePart * wTime + precisionPart * wPrecision + costPart * wCost;
        return BigDecimal.valueOf(ratio).setScale(6, RoundingMode.HALF_UP);
    }

    private ProcessMap deepCopyProcessMap(ProcessMap source) {
        ProcessMap copy = new ProcessMap(
                "copy_" + System.nanoTime(),
                copyNodes(source.getMultiNodes()),
                copyPaths(source.getProcessPaths()),
                copyConstraints(source.getConstraintConditions()),
                copyEquipments(source.getEquipments())
        );
        copy.setTotalTime(source.getTotalTime());
        copy.setTotalPrecision(source.getTotalPrecision());
        copy.setTotalCost(source.getTotalCost());
        return copy;
    }

    private ArrayList<MultiNode> copyNodes(List<MultiNode> nodes) {
        ArrayList<MultiNode> copied = new ArrayList<>();
        if (nodes == null) {
            return copied;
        }
        for (MultiNode node : nodes) {
            copied.add(new MultiNode(
                    node.getNodeID(),
                    node.getNodeDescription(),
                    node.getEquipmentName(),
                    node.getTime(),
                    node.getPrecision(),
                    node.getCost()
            ));
        }
        return copied;
    }

    private LinkedList<ProcessPath> copyPaths(List<ProcessPath> paths) {
        LinkedList<ProcessPath> copied = new LinkedList<>();
        if (paths == null) {
            return copied;
        }
        for (ProcessPath path : paths) {
            copied.add(new ProcessPath(
                    StringUtils.hasText(path.getPathID()) ? path.getPathID() : "P" + System.nanoTime(),
                    path.getStartNodeID(),
                    path.getEndNodeID()
            ));
        }
        return copied;
    }

    private ArrayList<ConstraintCondition> copyConstraints(List<ConstraintCondition> constraints) {
        ArrayList<ConstraintCondition> copied = new ArrayList<>();
        if (constraints == null) {
            return copied;
        }
        for (ConstraintCondition condition : constraints) {
            copied.add(new ConstraintCondition(
                    condition.getConditionID(),
                    condition.getConditionDescription(),
                    condition.getConditionType(),
                    condition.getNodeID1(),
                    condition.getNodeID2()
            ));
        }
        return copied;
    }

    private ArrayList<Equipment> copyEquipments(List<Equipment> equipments) {
        ArrayList<Equipment> copied = new ArrayList<>();
        if (equipments == null) {
            return copied;
        }
        for (Equipment equipment : equipments) {
            copied.add(new Equipment(
                    equipment.getName(),
                    equipment.getNodes() == null ? new ArrayList<>() : new ArrayList<>(equipment.getNodes()),
                    equipment.getColor(),
                    equipment.getDescription(),
                    equipment.getImagePath()
            ));
        }
        return copied;
    }

    private String truncate(String raw, int maxLen) {
        String value = raw == null ? "未知异常" : raw;
        if (value.length() <= maxLen) {
            return value;
        }
        return value.substring(0, maxLen);
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

    private FlowGraphMapper getFlowGraphMapper() {
        FlowGraphMapper mapper = flowGraphMapperProvider.getIfAvailable();
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

    private EquipmentMapper getEquipmentMapper() {
        EquipmentMapper mapper = equipmentMapperProvider.getIfAvailable();
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

    private static class OptimizeContext {
        private final OptimizeTaskEntity task;
        private final FlowGraphEntity graph;
        private final ProcessMap sourceMap;
        private final ProcessMap sourceSnapshot;
        private final int[] factors;

        private OptimizeContext(OptimizeTaskEntity task,
                                FlowGraphEntity graph,
                                ProcessMap sourceMap,
                                ProcessMap sourceSnapshot,
                                int[] factors) {
            this.task = task;
            this.graph = graph;
            this.sourceMap = sourceMap;
            this.sourceSnapshot = sourceSnapshot;
            this.factors = factors;
        }
    }

    private static class AlgorithmOutput {
        private Map<String, Object> resultGraph;
        private Map<String, Object> diff;
        private String mapCode;
        private int beforeTime;
        private double beforePrecision;
        private int beforeCost;
        private int afterTime;
        private double afterPrecision;
        private int afterCost;
        private BigDecimal scoreRatio;
    }
}
