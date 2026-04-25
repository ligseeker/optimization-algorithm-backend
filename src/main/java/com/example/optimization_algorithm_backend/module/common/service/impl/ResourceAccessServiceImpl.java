package com.example.optimization_algorithm_backend.module.common.service.impl;

import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.WorkspaceMapper;
import com.example.optimization_algorithm_backend.module.auth.service.CurrentUserService;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ResourceAccessServiceImpl implements ResourceAccessService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，接口暂不可用";

    private final ObjectProvider<WorkspaceMapper> workspaceMapperProvider;
    private final ObjectProvider<FlowGraphMapper> flowGraphMapperProvider;
    private final CurrentUserService currentUserService;

    public ResourceAccessServiceImpl(ObjectProvider<WorkspaceMapper> workspaceMapperProvider,
                                     ObjectProvider<FlowGraphMapper> flowGraphMapperProvider,
                                     CurrentUserService currentUserService) {
        this.workspaceMapperProvider = workspaceMapperProvider;
        this.flowGraphMapperProvider = flowGraphMapperProvider;
        this.currentUserService = currentUserService;
    }

    @Override
    public WorkspaceEntity getAccessibleWorkspace(Long workspaceId) {
        WorkspaceEntity workspace = getWorkspaceMapper().selectById(workspaceId);
        if (workspace == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "工作空间不存在");
        }
        if (currentUserService.isAdmin()) {
            return workspace;
        }
        if (!Objects.equals(workspace.getOwnerUserId(), currentUserService.getCurrentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该工作空间");
        }
        return workspace;
    }

    @Override
    public FlowGraphEntity getAccessibleGraph(Long graphId) {
        FlowGraphEntity graph = getFlowGraphMapper().selectById(graphId);
        if (graph == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "流程图不存在");
        }
        getAccessibleWorkspace(graph.getWorkspaceId());
        return graph;
    }

    @Override
    public void checkGraphBelongsWorkspace(Long workspaceId, Long graphId) {
        FlowGraphEntity graph = getAccessibleGraph(graphId);
        if (!Objects.equals(graph.getWorkspaceId(), workspaceId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "流程图不属于当前工作空间");
        }
    }

    private WorkspaceMapper getWorkspaceMapper() {
        WorkspaceMapper mapper = workspaceMapperProvider.getIfAvailable();
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
}
