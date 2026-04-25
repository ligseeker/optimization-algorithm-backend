package com.example.optimization_algorithm_backend.module.equipment.service;

import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.module.equipment.dto.CreateEquipmentRequest;
import com.example.optimization_algorithm_backend.module.equipment.dto.EquipmentQueryRequest;
import com.example.optimization_algorithm_backend.module.equipment.dto.UpdateEquipmentRequest;
import com.example.optimization_algorithm_backend.module.equipment.vo.EquipmentVO;

public interface EquipmentAppService {

    EquipmentVO createEquipment(Long graphId, CreateEquipmentRequest request);

    PageResult<EquipmentVO> listEquipments(Long graphId, EquipmentQueryRequest request);

    EquipmentVO getEquipment(Long graphId, Long equipmentId);

    EquipmentVO updateEquipment(Long graphId, Long equipmentId, UpdateEquipmentRequest request);

    boolean deleteEquipment(Long graphId, Long equipmentId);
}
