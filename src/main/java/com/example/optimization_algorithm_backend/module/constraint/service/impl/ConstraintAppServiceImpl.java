package com.example.optimization_algorithm_backend.module.constraint.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.constraint.converter.ConstraintConverter;
import com.example.optimization_algorithm_backend.module.constraint.dto.ConstraintQueryRequest;
import com.example.optimization_algorithm_backend.module.constraint.dto.CreateConstraintRequest;
import com.example.optimization_algorithm_backend.module.constraint.dto.UpdateConstraintRequest;
import com.example.optimization_algorithm_backend.module.constraint.service.ConstraintAppService;
import com.example.optimization_algorithm_backend.module.constraint.vo.ConstraintVO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConstraintAppServiceImpl implements ConstraintAppService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，约束接口暂不可用";

    private final ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider;
    private final ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    private final ResourceAccessService resourceAccessService;

    public ConstraintAppServiceImpl(ObjectProvider<ConstraintConditionMapper> constraintConditionMapperProvider,
                                    ObjectProvider<ProcessNodeMapper> processNodeMapperProvider,
                                    ResourceAccessService resourceAccessService) {
        this.constraintConditionMapperProvider = constraintConditionMapperProvider;
        this.processNodeMapperProvider = processNodeMapperProvider;
        this.resourceAccessService = resourceAccessService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConstraintVO createConstraint(Long graphId, CreateConstraintRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        String conditionCode = request.getConditionCode().trim();
        ensureConditionCodeUnique(graphId, conditionCode, null);
        validateNodeExists(graphId, request.getNodeId1(), "约束关联节点1不存在");
        validateNodeExists(graphId, request.getNodeId2(), "约束关联节点2不存在");

        ConstraintConditionEntity entity = new ConstraintConditionEntity();
        entity.setGraphId(graphId);
        entity.setConditionCode(conditionCode);
        entity.setConditionType(request.getConditionType().trim());
        entity.setConditionDescription(request.getConditionDescription());
        entity.setNodeId1(request.getNodeId1());
        entity.setNodeId2(request.getNodeId2());
        entity.setEnabled(request.getEnabled() != null ? request.getEnabled() : 1);
        getConstraintConditionMapper().insert(entity);
        return ConstraintConverter.toConstraintVO(entity);
    }

    @Override
    public PageResult<ConstraintVO> listConstraints(Long graphId, ConstraintQueryRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        LambdaQueryWrapper<ConstraintConditionEntity> queryWrapper = new LambdaQueryWrapper<ConstraintConditionEntity>()
                .eq(ConstraintConditionEntity::getGraphId, graphId)
                .orderByDesc(ConstraintConditionEntity::getCreatedAt);
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper.like(ConstraintConditionEntity::getConditionCode, keyword)
                    .or().like(ConstraintConditionEntity::getConditionType, keyword));
        }
        Page<ConstraintConditionEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<ConstraintConditionEntity> resultPage = getConstraintConditionMapper().selectPage(page, queryWrapper);
        List<ConstraintVO> records = resultPage.getRecords().stream().map(ConstraintConverter::toConstraintVO).collect(Collectors.toList());
        return PageResult.of(records, resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public ConstraintVO getConstraint(Long graphId, Long constraintId) {
        return ConstraintConverter.toConstraintVO(getConstraintInGraph(graphId, constraintId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConstraintVO updateConstraint(Long graphId, Long constraintId, UpdateConstraintRequest request) {
        ConstraintConditionEntity constraint = getConstraintInGraph(graphId, constraintId);
        String conditionCode = request.getConditionCode().trim();
        if (!Objects.equals(constraint.getConditionCode(), conditionCode)) {
            ensureConditionCodeUnique(graphId, conditionCode, constraintId);
        }
        validateNodeExists(graphId, request.getNodeId1(), "约束关联节点1不存在");
        validateNodeExists(graphId, request.getNodeId2(), "约束关联节点2不存在");

        constraint.setConditionCode(conditionCode);
        constraint.setConditionType(request.getConditionType().trim());
        constraint.setConditionDescription(request.getConditionDescription());
        constraint.setNodeId1(request.getNodeId1());
        constraint.setNodeId2(request.getNodeId2());
        if (request.getEnabled() != null) {
            constraint.setEnabled(request.getEnabled());
        }
        getConstraintConditionMapper().updateById(constraint);
        return ConstraintConverter.toConstraintVO(constraint);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConstraint(Long graphId, Long constraintId) {
        ConstraintConditionEntity constraint = getConstraintInGraph(graphId, constraintId);
        return getConstraintConditionMapper().deleteById(constraint.getId()) > 0;
    }

    private ConstraintConditionEntity getConstraintInGraph(Long graphId, Long constraintId) {
        resourceAccessService.getAccessibleGraph(graphId);
        ConstraintConditionEntity constraint = getConstraintConditionMapper().selectById(constraintId);
        if (constraint == null || !Objects.equals(constraint.getGraphId(), graphId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "约束不存在");
        }
        return constraint;
    }

    private void ensureConditionCodeUnique(Long graphId, String conditionCode, Long excludeId) {
        LambdaQueryWrapper<ConstraintConditionEntity> queryWrapper = new LambdaQueryWrapper<ConstraintConditionEntity>()
                .eq(ConstraintConditionEntity::getGraphId, graphId)
                .eq(ConstraintConditionEntity::getConditionCode, conditionCode);
        if (excludeId != null) {
            queryWrapper.ne(ConstraintConditionEntity::getId, excludeId);
        }
        Long count = getConstraintConditionMapper().selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "同一流程图下conditionCode必须唯一");
        }
    }

    private void validateNodeExists(Long graphId, Long nodeId, String message) {
        ProcessNodeEntity node = getProcessNodeMapper().selectById(nodeId);
        if (node == null || !Objects.equals(node.getGraphId(), graphId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, message);
        }
    }

    private ConstraintConditionMapper getConstraintConditionMapper() {
        ConstraintConditionMapper mapper = constraintConditionMapperProvider.getIfAvailable();
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
}
