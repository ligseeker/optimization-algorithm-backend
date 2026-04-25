package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeTaskEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeTaskMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.OptimizeTaskPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class OptimizeTaskPersistenceServiceImpl extends ServiceImpl<OptimizeTaskMapper, OptimizeTaskEntity> implements OptimizeTaskPersistenceService {
}
