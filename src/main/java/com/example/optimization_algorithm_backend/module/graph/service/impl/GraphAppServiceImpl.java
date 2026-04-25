package com.example.optimization_algorithm_backend.module.graph.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
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
import com.example.optimization_algorithm_backend.module.graph.converter.GraphConverter;
import com.example.optimization_algorithm_backend.module.graph.dto.CreateGraphRequest;
import com.example.optimization_algorithm_backend.module.graph.dto.GraphQueryRequest;
import com.example.optimization_algorithm_backend.module.graph.dto.UpdateGraphRequest;
import com.example.optimization_algorithm_backend.module.graph.service.GraphAppService;
import com.example.optimization_algorithm_backend.module.graph.vo.GraphVO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GraphAppServiceImpl implements GraphAppService {

    private static final String SOURCE_TYPE_MANUAL = "MANUAL";
    private static final String GRAPH_STATUS_DRAFT = "DRAFT";
    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，流程图接口暂不可用";

    private final ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;
    private final ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    private final ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    private final ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    private final ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    private final ResourceAccessService resourceAccessService;

    public GraphAppServiceImpl(ObjectProvider<FlowGraphMapper> flowGraphMapperProvider,
                               ObjectProvider<ProcessNodeMapper> processNodeMapperProvider,
                               ObjectProvider<ProcessPathMapper> processPathMapperProvider,
                               ObjectProvider<EquipmentMapper> equipmentMapperProvider,
                               ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider,
                               ResourceAccessService resourceAccessService) {
        this.flowGraphMapperProvider = flowGraphMapperProvider;
        this.processNodeMapperProvider = processNodeMapperProvider;
        this.processPathMapperProvider = processPathMapperProvider;
        this.equipmentMapperProvider = equipmentMapperProvider;
        this.constraintConditionMapperProvider = constraintConditionMapperProvider;
        this.resourceAccessService = resourceAccessService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GraphVO createGraph(Long workspaceId, CreateGraphRequest request) {
        resourceAccessService.getAccessibleWorkspace(workspaceId);
        String graphName = request.getName().trim();
        ensureGraphNameUnique(workspaceId, graphName, null);

        FlowGraphEntity entity = new FlowGraphEntity();
        entity.setWorkspaceId(workspaceId);
        entity.setName(graphName);
        entity.setDescription(request.getDescription());
        entity.setSourceType(StringUtils.hasText(request.getSourceType()) ? request.getSourceType().trim() : SOURCE_TYPE_MANUAL);
        entity.setGraphStatus(StringUtils.hasText(request.getGraphStatus()) ? request.getGraphStatus().trim() : GRAPH_STATUS_DRAFT);
        entity.setTotalTime(0);
        entity.setTotalPrecision(BigDecimal.ZERO);
        entity.setTotalCost(0);
        getFlowGraphMapper().insert(entity);
        return GraphConverter.toGraphVO(entity);
    }

    @Override
    public PageResult<GraphVO> listGraphs(Long workspaceId, GraphQueryRequest request) {
        resourceAccessService.getAccessibleWorkspace(workspaceId);
        LambdaQueryWrapper<FlowGraphEntity> queryWrapper = new LambdaQueryWrapper<FlowGraphEntity>()
                .eq(FlowGraphEntity::getWorkspaceId, workspaceId)
                .orderByDesc(FlowGraphEntity::getCreatedAt);
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.like(FlowGraphEntity::getName, request.getKeyword().trim());
        }

        Page<FlowGraphEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<FlowGraphEntity> resultPage = getFlowGraphMapper().selectPage(page, queryWrapper);
        List<GraphVO> records = resultPage.getRecords().stream().map(GraphConverter::toGraphVO).collect(Collectors.toList());
        return PageResult.of(records, resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public GraphVO getGraph(Long graphId) {
        return GraphConverter.toGraphVO(resourceAccessService.getAccessibleGraph(graphId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GraphVO updateGraph(Long graphId, UpdateGraphRequest request) {
        FlowGraphEntity graph = resourceAccessService.getAccessibleGraph(graphId);
        String graphName = request.getName().trim();
        if (!Objects.equals(graph.getName(), graphName)) {
            ensureGraphNameUnique(graph.getWorkspaceId(), graphName, graphId);
        }
        graph.setName(graphName);
        graph.setDescription(request.getDescription());
        if (StringUtils.hasText(request.getGraphStatus())) {
            graph.setGraphStatus(request.getGraphStatus().trim());
        }
        if (request.getTotalTime() != null) {
            graph.setTotalTime(request.getTotalTime());
        }
        if (request.getTotalPrecision() != null) {
            graph.setTotalPrecision(request.getTotalPrecision());
        }
        if (request.getTotalCost() != null) {
            graph.setTotalCost(request.getTotalCost());
        }
        getFlowGraphMapper().updateById(graph);
        return GraphConverter.toGraphVO(graph);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGraph(Long graphId) {
        FlowGraphEntity graph = resourceAccessService.getAccessibleGraph(graphId);
        Long targetGraphId = graph.getId();

        getConstraintConditionMapper().delete(new LambdaQueryWrapper<ConstraintConditionEntity>()
                .eq(ConstraintConditionEntity::getGraphId, targetGraphId));
        getProcessPathMapper().delete(new LambdaQueryWrapper<ProcessPathEntity>()
                .eq(ProcessPathEntity::getGraphId, targetGraphId));
        getProcessNodeMapper().delete(new LambdaQueryWrapper<ProcessNodeEntity>()
                .eq(ProcessNodeEntity::getGraphId, targetGraphId));
        getEquipmentMapper().delete(new LambdaQueryWrapper<EquipmentEntity>()
                .eq(EquipmentEntity::getGraphId, targetGraphId));

        return getFlowGraphMapper().deleteById(targetGraphId) > 0;
    }

    private void ensureGraphNameUnique(Long workspaceId, String graphName, Long excludeGraphId) {
        LambdaQueryWrapper<FlowGraphEntity> queryWrapper = new LambdaQueryWrapper<FlowGraphEntity>()
                .eq(FlowGraphEntity::getWorkspaceId, workspaceId)
                .eq(FlowGraphEntity::getName, graphName);
        if (excludeGraphId != null) {
            queryWrapper.ne(FlowGraphEntity::getId, excludeGraphId);
        }
        Long count = getFlowGraphMapper().selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "流程图名称已存在");
        }
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
}
