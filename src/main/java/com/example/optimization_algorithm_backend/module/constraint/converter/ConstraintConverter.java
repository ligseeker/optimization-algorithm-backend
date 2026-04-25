package com.example.optimization_algorithm_backend.module.constraint.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.module.constraint.vo.ConstraintVO;

public final class ConstraintConverter {

    private ConstraintConverter() {
    }

    public static ConstraintVO toConstraintVO(ConstraintConditionEntity entity) {
        ConstraintVO vo = new ConstraintVO();
        vo.setId(entity.getId());
        vo.setGraphId(entity.getGraphId());
        vo.setConditionCode(entity.getConditionCode());
        vo.setConditionType(entity.getConditionType());
        vo.setConditionDescription(entity.getConditionDescription());
        vo.setNodeId1(entity.getNodeId1());
        vo.setNodeId2(entity.getNodeId2());
        vo.setEnabled(entity.getEnabled());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
