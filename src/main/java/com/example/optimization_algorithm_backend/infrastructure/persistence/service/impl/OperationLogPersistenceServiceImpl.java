package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OperationLogEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OperationLogMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.OperationLogPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class OperationLogPersistenceServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogEntity> implements OperationLogPersistenceService {
}
