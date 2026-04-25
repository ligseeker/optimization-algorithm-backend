package com.example.optimization_algorithm_backend.module.node.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.node.converter.NodeConverter;
import com.example.optimization_algorithm_backend.module.node.dto.CreateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.dto.NodeQueryRequest;
import com.example.optimization_algorithm_backend.module.node.dto.UpdateNodeRequest;
import com.example.optimization_algorithm_backend.module.node.service.NodeAppService;
import com.example.optimization_algorithm_backend.module.node.vo.NodeVO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NodeAppServiceImpl implements NodeAppService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，节点接口暂不可用";

    private final ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    private final ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    private final ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    private final ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    private final ResourceAccessService resourceAccessService;

    public NodeAppServiceImpl(ObjectProvider<ProcessNodeMapper> processNodeMapperProvider,
                              ObjectProvider<ProcessPathMapper> processPathMapperProvider,
                              ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider,
                              ObjectProvider<EquipmentMapper> equipmentMapperProvider,
                              ResourceAccessService resourceAccessService) {
        this.processNodeMapperProvider = processNodeMapperProvider;
        this.processPathMapperProvider = processPathMapperProvider;
        this.constraintConditionMapperProvider = constraintConditionMapperProvider;
        this.equipmentMapperProvider = equipmentMapperProvider;
        this.resourceAccessService = resourceAccessService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NodeVO createNode(Long graphId, CreateNodeRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        String nodeCode = request.getNodeCode().trim();
        ensureNodeCodeUnique(graphId, nodeCode, null);
        validateEquipmentIfPresent(graphId, request.getEquipmentId());

        ProcessNodeEntity entity = new ProcessNodeEntity();
        entity.setGraphId(graphId);
        entity.setNodeCode(nodeCode);
        entity.setNodeName(request.getNodeName());
        entity.setNodeDescription(request.getNodeDescription());
        entity.setEquipmentId(request.getEquipmentId());
        entity.setTimeCost(request.getTimeCost() != null ? request.getTimeCost() : 0);
        entity.setPrecisionValue(request.getPrecisionValue() != null ? request.getPrecisionValue() : BigDecimal.ZERO);
        entity.setCostValue(request.getCostValue() != null ? request.getCostValue() : 0);
        entity.setSortNo(request.getSortNo() != null ? request.getSortNo() : 0);
        getProcessNodeMapper().insert(entity);
        return NodeConverter.toNodeVO(entity);
    }

    @Override
    public PageResult<NodeVO> listNodes(Long graphId, NodeQueryRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        LambdaQueryWrapper<ProcessNodeEntity> queryWrapper = new LambdaQueryWrapper<ProcessNodeEntity>()
                .eq(ProcessNodeEntity::getGraphId, graphId)
                .orderByAsc(ProcessNodeEntity::getSortNo)
                .orderByDesc(ProcessNodeEntity::getCreatedAt);
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper.like(ProcessNodeEntity::getNodeCode, keyword)
                    .or().like(ProcessNodeEntity::getNodeName, keyword));
        }
        Page<ProcessNodeEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<ProcessNodeEntity> resultPage = getProcessNodeMapper().selectPage(page, queryWrapper);
        List<NodeVO> records = resultPage.getRecords().stream().map(NodeConverter::toNodeVO).collect(Collectors.toList());
        return PageResult.of(records, resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public NodeVO getNode(Long graphId, Long nodeId) {
        return NodeConverter.toNodeVO(getNodeInGraph(graphId, nodeId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NodeVO updateNode(Long graphId, Long nodeId, UpdateNodeRequest request) {
        ProcessNodeEntity node = getNodeInGraph(graphId, nodeId);
        String nodeCode = request.getNodeCode().trim();
        if (!Objects.equals(node.getNodeCode(), nodeCode)) {
            ensureNodeCodeUnique(graphId, nodeCode, nodeId);
        }
        validateEquipmentIfPresent(graphId, request.getEquipmentId());

        node.setNodeCode(nodeCode);
        node.setNodeName(request.getNodeName());
        node.setNodeDescription(request.getNodeDescription());
        node.setEquipmentId(request.getEquipmentId());
        if (request.getTimeCost() != null) {
            node.setTimeCost(request.getTimeCost());
        }
        if (request.getPrecisionValue() != null) {
            node.setPrecisionValue(request.getPrecisionValue());
        }
        if (request.getCostValue() != null) {
            node.setCostValue(request.getCostValue());
        }
        if (request.getSortNo() != null) {
            node.setSortNo(request.getSortNo());
        }
        getProcessNodeMapper().updateById(node);
        return NodeConverter.toNodeVO(node);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNode(Long graphId, Long nodeId) {
        ProcessNodeEntity node = getNodeInGraph(graphId, nodeId);

        getProcessPathMapper().delete(new LambdaQueryWrapper<ProcessPathEntity>()
                .eq(ProcessPathEntity::getGraphId, graphId)
                .and(wrapper -> wrapper.eq(ProcessPathEntity::getStartNodeId, node.getId())
                        .or()
                        .eq(ProcessPathEntity::getEndNodeId, node.getId())));

        getConstraintConditionMapper().delete(new LambdaQueryWrapper<ConstraintConditionEntity>()
                .eq(ConstraintConditionEntity::getGraphId, graphId)
                .and(wrapper -> wrapper.eq(ConstraintConditionEntity::getNodeId1, node.getId())
                        .or()
                        .eq(ConstraintConditionEntity::getNodeId2, node.getId())));

        return getProcessNodeMapper().deleteById(node.getId()) > 0;
    }

    private ProcessNodeEntity getNodeInGraph(Long graphId, Long nodeId) {
        resourceAccessService.getAccessibleGraph(graphId);
        ProcessNodeEntity node = getProcessNodeMapper().selectById(nodeId);
        if (node == null || !Objects.equals(node.getGraphId(), graphId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "节点不存在");
        }
        return node;
    }

    private void ensureNodeCodeUnique(Long graphId, String nodeCode, Long excludeNodeId) {
        LambdaQueryWrapper<ProcessNodeEntity> queryWrapper = new LambdaQueryWrapper<ProcessNodeEntity>()
                .eq(ProcessNodeEntity::getGraphId, graphId)
                .eq(ProcessNodeEntity::getNodeCode, nodeCode);
        if (excludeNodeId != null) {
            queryWrapper.ne(ProcessNodeEntity::getId, excludeNodeId);
        }
        Long count = getProcessNodeMapper().selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "同一流程图下nodeCode必须唯一");
        }
    }

    private void validateEquipmentIfPresent(Long graphId, Long equipmentId) {
        if (equipmentId == null) {
            return;
        }
        EquipmentEntity equipment = getEquipmentMapper().selectById(equipmentId);
        if (equipment == null || !Objects.equals(equipment.getGraphId(), graphId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "节点关联的设备不存在");
        }
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

    private EquipmentMapper getEquipmentMapper() {
        EquipmentMapper mapper = equipmentMapperProvider.getIfAvailable();
        if (mapper == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, DB_UNAVAILABLE_MESSAGE);
        }
        return mapper;
    }
}
