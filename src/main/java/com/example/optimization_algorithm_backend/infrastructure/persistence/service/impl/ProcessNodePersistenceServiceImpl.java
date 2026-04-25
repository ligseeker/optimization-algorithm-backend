package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessNodeEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessNodeMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.ProcessNodePersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class ProcessNodePersistenceServiceImpl extends ServiceImpl<ProcessNodeMapper, ProcessNodeEntity> implements ProcessNodePersistenceService {
}
