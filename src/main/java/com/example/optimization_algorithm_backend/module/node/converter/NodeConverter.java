package com.example.optimization_algorithm_backend.module.node.converter;

import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.module.node.vo.NodeVO;

public final class NodeConverter {

    private NodeConverter() {
    }

    public static NodeVO toNodeVO(ProcessNodeEntity entity) {
        NodeVO vo = new NodeVO();
        vo.setId(entity.getId());
        vo.setGraphId(entity.getGraphId());
        vo.setNodeCode(entity.getNodeCode());
        vo.setNodeName(entity.getNodeName());
        vo.setNodeDescription(entity.getNodeDescription());
        vo.setEquipmentId(entity.getEquipmentId());
        vo.setTimeCost(entity.getTimeCost());
        vo.setPrecisionValue(entity.getPrecisionValue());
        vo.setCostValue(entity.getCostValue());
        vo.setSortNo(entity.getSortNo());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }
}
