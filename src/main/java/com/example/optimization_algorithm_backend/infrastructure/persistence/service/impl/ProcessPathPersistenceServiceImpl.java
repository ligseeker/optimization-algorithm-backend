package com.example.optimization_algorithm_backend.infrastructure.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.optimization_algorithm_backend.infrastructure.persistence.entity.ProcessPathEntity;
import com.example.optimization_algorithm_backend.infrastructure.persistence.mapper.ProcessPathMapper;
import com.example.optimization_algorithm_backend.infrastructure.persistence.service.ProcessPathPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@ConditionalOnBean(DataSource.class)
public class ProcessPathPersistenceServiceImpl extends ServiceImpl<ProcessPathMapper, ProcessPathEntity> implements ProcessPathPersistenceService {
}
