package com.example.optimization_algorithm_backend.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.optimization_algorithm_backend.common.exception.BusinessException;
import com.example.optimization_algorithm_backend.common.response.ErrorCode;
import com.example.optimization_algorithm_backend.common.response.PageResult;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.module.common.service.ResourceAccessService;
import com.example.optimization_algorithm_backend.module.equipment.converter.EquipmentConverter;
import com.example.optimization_algorithm_backend.module.equipment.dto.CreateEquipmentRequest;
import com.example.optimization_algorithm_backend.module.equipment.dto.EquipmentQueryRequest;
import com.example.optimization_algorithm_backend.module.equipment.dto.UpdateEquipmentRequest;
import com.example.optimization_algorithm_backend.module.equipment.service.EquipmentAppService;
import com.example.optimization_algorithm_backend.module.equipment.vo.EquipmentVO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EquipmentAppServiceImpl implements EquipmentAppService {

    private static final String DB_UNAVAILABLE_MESSAGE = "数据库未配置或不可用，设备接口暂不可用";

    private final ObjectProvider<EquipmentMapper> equipmentMapperProvider;
    private final ObjectProvider<ProcessNodeMapper> processNodeMapperProvider;
    private final ResourceAccessService resourceAccessService;

    public EquipmentAppServiceImpl(ObjectProvider<EquipmentMapper> equipmentMapperProvider,
                                   ObjectProvider<ProcessNodeMapper> processNodeMapperProvider,
                                   ResourceAccessService resourceAccessService) {
        this.equipmentMapperProvider = equipmentMapperProvider;
        this.processNodeMapperProvider = processNodeMapperProvider;
        this.resourceAccessService = resourceAccessService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EquipmentVO createEquipment(Long graphId, CreateEquipmentRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        String name = request.getName().trim();
        ensureEquipmentNameUnique(graphId, name, null);

        EquipmentEntity entity = new EquipmentEntity();
        entity.setGraphId(graphId);
        entity.setName(name);
        entity.setDescription(request.getDescription());
        entity.setColor(request.getColor());
        entity.setImagePath(request.getImagePath());
        getEquipmentMapper().insert(entity);
        return EquipmentConverter.toEquipmentVO(entity);
    }

    @Override
    public PageResult<EquipmentVO> listEquipments(Long graphId, EquipmentQueryRequest request) {
        resourceAccessService.getAccessibleGraph(graphId);
        LambdaQueryWrapper<EquipmentEntity> queryWrapper = new LambdaQueryWrapper<EquipmentEntity>()
                .eq(EquipmentEntity::getGraphId, graphId)
                .orderByDesc(EquipmentEntity::getCreatedAt);
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.like(EquipmentEntity::getName, request.getKeyword().trim());
        }
        Page<EquipmentEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        Page<EquipmentEntity> resultPage = getEquipmentMapper().selectPage(page, queryWrapper);
        List<EquipmentVO> records = resultPage.getRecords()
                .stream()
                .map(EquipmentConverter::toEquipmentVO)
                .collect(Collectors.toList());
        return PageResult.of(records, resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
    }

    @Override
    public EquipmentVO getEquipment(Long graphId, Long equipmentId) {
        return EquipmentConverter.toEquipmentVO(getEquipmentInGraph(graphId, equipmentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EquipmentVO updateEquipment(Long graphId, Long equipmentId, UpdateEquipmentRequest request) {
        EquipmentEntity equipment = getEquipmentInGraph(graphId, equipmentId);
        String name = request.getName().trim();
        if (!Objects.equals(equipment.getName(), name)) {
            ensureEquipmentNameUnique(graphId, name, equipmentId);
        }

        equipment.setName(name);
        equipment.setDescription(request.getDescription());
        equipment.setColor(request.getColor());
        equipment.setImagePath(request.getImagePath());
        getEquipmentMapper().updateById(equipment);
        return EquipmentConverter.toEquipmentVO(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEquipment(Long graphId, Long equipmentId) {
        EquipmentEntity equipment = getEquipmentInGraph(graphId, equipmentId);

        // 清理节点上的设备引用，避免出现无效 equipmentId。
        getProcessNodeMapper().update(null, new LambdaUpdateWrapper<ProcessNodeEntity>()
                .eq(ProcessNodeEntity::getGraphId, graphId)
                .eq(ProcessNodeEntity::getEquipmentId, equipment.getId())
                .set(ProcessNodeEntity::getEquipmentId, null));

        return getEquipmentMapper().deleteById(equipment.getId()) > 0;
    }

    private EquipmentEntity getEquipmentInGraph(Long graphId, Long equipmentId) {
        resourceAccessService.getAccessibleGraph(graphId);
        EquipmentEntity equipment = getEquipmentMapper().selectById(equipmentId);
        if (equipment == null || !Objects.equals(equipment.getGraphId(), graphId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "设备不存在");
        }
        return equipment;
    }

    private void ensureEquipmentNameUnique(Long graphId, String name, Long excludeId) {
        LambdaQueryWrapper<EquipmentEntity> queryWrapper = new LambdaQueryWrapper<EquipmentEntity>()
                .eq(EquipmentEntity::getGraphId, graphId)
                .eq(EquipmentEntity::getName, name);
        if (excludeId != null) {
            queryWrapper.ne(EquipmentEntity::getId, excludeId);
        }
        Long count = getEquipmentMapper().selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "同一流程图下设备名称不能重复");
        }
    }

    private EquipmentMapper getEquipmentMapper() {
        EquipmentMapper mapper = equipmentMapperProvider.getIfAvailable();
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
