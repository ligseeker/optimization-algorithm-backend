package com.example.optimization_algorithm_backend.module.path.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.path.converter.PathConverter;
import com.example.optimization_algorithm_backend.module.path.dto.CreatePathRequest;
import com.example.optimization_algorithm_backend.module.path.dto.PathQueryRequest;
import com.example.optimization_algorithm_backend.module.path.dto.UpdatePathRequest;
import com.example.optimization_algorithm_backend.module.path.service.PathAppService;
import com.example.optimization_algorithm_backend.module.path.vo.PathVO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PathAppServiceImpl implements PathAppService {

    private static final String DEFAULT_RELATION_TYPE = "NORMAL";
    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，路径接口暂不可用";

    private final ObjectProvider<ProcessPathMapper> processPathMapperProvider;
    private final ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    private final ResourceAccessService resourceAccessService;

    public PathAppServiceImpl(ObjectProvider<ProcessPathMapper> processPathMapperProvider,
                              ObjectProvider<ProcessNodeMapper> processNodeMapperProvider,
                              ResourceAccessService resourceAccessService) {
        this.processPathMapperProvider = processPathMapperProvider;
        this.processNodeMapperProvider = processNodeMapperProvider;
        this.resourceAccessService = resourceAccessService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathVO createPath(Long graphId, CreatePathRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        validateNodeExists(graphId, request.getStartNodeId(), "路径起点节点不存在");
        validateNodeExists(graphId, request.getEndNodeId(), "路径终点节点不存在");

        ProcessPathEntity entity = new ProcessPathEntity();
        entity.setGraphId(graphId);
        entity.setStartNodeId(request.getStartNodeId());
        entity.setEndNodeId(request.getEndNodeId());
        entity.setRelationType(StringUtils.hasText(request.getRelationType()) ? request.getRelationType().trim() : DEFAULT_RELATION_TYPE);
        entity.setRemark(request.getRemark());
        getProcessPathMapper().insert(entity);
        return PathConverter.toPathVO(entity);
    }

    @Override
    public PageResult<PathVO> listPaths(Long graphId, PathQueryRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        LambdaQueryWrapper<ProcessPathEntity> queryWrapper = new LambdaQueryWrapper<ProcessPathEntity>()
                .eq(ProcessPathEntity::getGraphId, graphId)
                .orderByDesc(ProcessPathEntity::getCreatedAt);
        Page<ProcessPathEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<ProcessPathEntity> resultPage = getProcessPathMapper().selectPage(page, queryWrapper);
        List<PathVO> records = resultPage.getRecords().stream().map(PathConverter::toPathVO).collect(Collectors.toList());
        return PageResult.of(records, resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public PathVO getPath(Long graphId, Long pathId) {
        return PathConverter.toPathVO(getPathInGraph(graphId, pathId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathVO updatePath(Long graphId, Long pathId, UpdatePathRequest request) {
        ProcessPathEntity path = getPathInGraph(graphId, pathId);
        validateNodeExists(graphId, request.getStartNodeId(), "路径起点节点不存在");
        validateNodeExists(graphId, request.getEndNodeId(), "路径终点节点不存在");

        path.setStartNodeId(request.getStartNodeId());
        path.setEndNodeId(request.getEndNodeId());
        path.setRelationType(StringUtils.hasText(request.getRelationType()) ? request.getRelationType().trim() : DEFAULT_RELATION_TYPE);
        path.setRemark(request.getRemark());
        getProcessPathMapper().updateById(path);
        return PathConverter.toPathVO(path);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePath(Long graphId, Long pathId) {
        ProcessPathEntity path = getPathInGraph(graphId, pathId);
        return getProcessPathMapper().deleteById(path.getId()) > 0;
    }

    private ProcessPathEntity getPathInGraph(Long graphId, Long pathId) {
        resourceAccessService.getAccessibleGraph(graphId);
        ProcessPathEntity path = getProcessPathMapper().selectById(pathId);
        if (path == null || !Objects.equals(path.getGraphId(), graphId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "路径不存在");
        }
        return path;
    }

    private void validateNodeExists(Long graphId, Long nodeId, String message) {
        ProcessNodeEntity node = getProcessNodeMapper().selectById(nodeId);
        if (node == null || !Objects.equals(node.getGraphId(), graphId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, message);
        }
    }

    private ProcessPathMapper getProcessPathMapper() {
        ProcessPathMapper mapper = processPathMapperProvider.getIfAvailable();
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
