package com.example.optimization_algorithm_backend.module.common.service;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;

public interface ResourceAccessService {

    WorkspaceEntity getAccessibleWorkspace(Long workspaceId);

    FlowGraphEntity getAccessibleGraph(Long graphId);

    void checkGraphBelongsWorkspace(Long workspaceId, Long graphId);
}
