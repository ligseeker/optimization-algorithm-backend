package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.FlowGraphEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.FlowGraphMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.FlowGraphPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class FlowGraphPersistenceServiceImpl extends ServiceImpl<FlowGraphMapper, FlowGraphEntity> implements FlowGraphPersistenceService {
}
