package com.example.optimization_algorithm_backend.module.equipment.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.module.equipment.vo.EquipmentVO;

public final class EquipmentConverter {

    private EquipmentConverter() {
    }

    public static EquipmentVO toEquipmentVO(EquipmentEntity entity) {
        EquipmentVO vo = new EquipmentVO();
        vo.setId(entity.getId());
        vo.setGraphId(entity.getGraphId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setColor(entity.getColor());
        vo.setImagePath(entity.getImagePath());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
