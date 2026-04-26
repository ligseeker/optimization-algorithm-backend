package com.example.optimization_algorithm_backend.module.graph.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.module.graph.vo.GraphVO;

public final class GraphConverter {

    private GraphConverter() {
    }

    public static GraphVO toGraphVO(FlowGraphEntity entity) {
        GraphVO vo = new GraphVO();
        vo.setId(entity.getId());
        vo.setWorkspaceId(entity.getWorkspaceId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setSourceType(entity.getSourceType());
        vo.setGraphStatus(entity.getGraphStatus());
        vo.setGraphVersion(entity.getGraphVersion());
        vo.setTotalTime(entity.getTotalTime());
        vo.setTotalPrecision(entity.getTotalPrecision());
        vo.setTotalCost(entity.getTotalCost());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
