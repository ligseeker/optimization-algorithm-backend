package com.example.optimization_algorithm_backend.module.path.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.module.path.vo.PathVO;

public final class PathConverter {

    private PathConverter() {
    }

    public static PathVO toPathVO(ProcessPathEntity entity) {
        PathVO vo = new PathVO();
        vo.setId(entity.getId());
        vo.setGraphId(entity.getGraphId());
        vo.setStartNodeId(entity.getStartNodeId());
        vo.setEndNodeId(entity.getEndNodeId());
        vo.setRelationType(entity.getRelationType());
        vo.setRemark(entity.getRemark());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
