package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ConstraintConditionEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ConstraintConditionMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.ConstraintConditionPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class ConstraintConditionPersistenceServiceImpl extends ServiceImpl<ConstraintConditionMapper, ConstraintConditionEntity> implements ConstraintConditionPersistenceService {
}
