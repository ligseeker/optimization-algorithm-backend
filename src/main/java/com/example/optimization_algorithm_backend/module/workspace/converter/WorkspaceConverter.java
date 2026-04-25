package com.example.optimization_algorithm_backend.module.workspace.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.WorkspaceEntity;
import com.example.optimization_algorithm_backend.module.workspace.vo.WorkspaceVO;

public final class WorkspaceConverter {

    private WorkspaceConverter() {
    }

    public static WorkspaceVO toWorkspaceVO(WorkspaceEntity entity) {
        WorkspaceVO vo = new WorkspaceVO();
        vo.setId(entity.getId());
        vo.setOwnerUserId(entity.getOwnerUserId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
