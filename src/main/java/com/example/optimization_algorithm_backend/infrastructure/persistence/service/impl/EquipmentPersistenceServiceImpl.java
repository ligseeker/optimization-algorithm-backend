package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.EquipmentEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.EquipmentMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.EquipmentPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class EquipmentPersistenceServiceImpl extends ServiceImpl<EquipmentMapper, EquipmentEntity> implements EquipmentPersistenceService {
}
