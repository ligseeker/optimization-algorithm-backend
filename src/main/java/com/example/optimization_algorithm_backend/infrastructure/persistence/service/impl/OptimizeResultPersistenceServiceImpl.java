package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.OptimizeResultEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.OptimizeResultMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.OptimizeResultPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class OptimizeResultPersistenceServiceImpl extends ServiceImpl<OptimizeResultMapper, OptimizeResultEntity> implements OptimizeResultPersistenceService {
}
